package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.*;

import java.util.ArrayList;
import java.util.List;

import static edu.ufl.cise.plcsp23.IToken.Kind;

public class Parser implements IParser{
    private IScanner scanner;
    private IToken current;
    private IToken prev;//previous token

    public Parser(Scanner scanner) throws PLCException{ //consume()
        this.scanner = scanner;
        current = scanner.next();
    }

    //changes token state for prev and current, returns boolean for token existence
    protected boolean match(Kind... kinds) throws PLCException{
        for(Kind k: kinds){
            if(k == current.getKind()) {
                //System.out.println(current.getTokenString());
                prev = current;
                current = scanner.next();          // "consume()"
                return true;
            }
        }
        return false;
    }

    private Program program() throws PLCException{ //maybe change access modifier

        IToken first = prev; //  set to first since match consumed
        Type progType = Type.getType(first); // will return an error if it isnt approved type
        Ident ident;
        List<NameDef> paramList;
        Block block;
        if(match(Kind.IDENT)){
            ident = new Ident(prev); // create ident object if its a match

        }
        else throw new SyntaxException("expected IDENT"); // follow program grammar

        if(match(Kind.LPAREN)) paramList = param(); //checking for paramlist
        else throw new SyntaxException("Expected (");


        //System.out.println(current.getTokenString());
        block = block();

        return new Program(first, progType, ident, paramList, block);
    }

    private List<NameDef> param() throws PLCException{
        List<NameDef> retList = new ArrayList<NameDef>();
        while (!match(Kind.RPAREN)){ //while in parameters list
            match(Kind.COMMA);

            NameDef parameter = nameDef();
            retList.add(parameter);
            if (match(Kind.RPAREN)) break;
            if (!match(Kind.COMMA)) return retList;

        } //maybe add error to check if param list doesnt end
        return retList;

    }

    private NameDef nameDef() throws PLCException{ //maybe change access modifier idk should be good for now
        IToken firstNameDef = null;
        if (match(Kind.RES_image,Kind.RES_pixel, Kind.RES_int, Kind.RES_string, Kind.RES_void))
            firstNameDef = prev; //NameDef must start with type
        else throw new SyntaxException("Expected Type");

        if (match(Kind.IDENT)) { //check for first type of parameters 'type IDENT'
            return new NameDef(firstNameDef, Type.getType(firstNameDef), null, new Ident(prev));
        }
        else if (match(Kind.LSQUARE)){ //means dimension
            Dimension dim = dimension();

            if (!match(Kind.IDENT)) throw new SyntaxException("Expected IDENT");
            return new NameDef(firstNameDef, Type.getType(firstNameDef), dim, new Ident(prev));
            }
        else return null;


    }

    private Dimension dimension() throws PLCException{
        IToken first = prev; //should it start at bracket??
        Expr width = expression();
        Expr height = null;
        if(match(Kind.COMMA)) {
            height = expression();
        }
        else throw new SyntaxException("Expected ,");
        if(match(Kind.RSQUARE)){
            return new Dimension(first,width,height);
        }
        return null;
    }

    private Block block() throws PLCException{
        if (!match(Kind.LCURLY)) throw new SyntaxException("Expected {");
        IToken firstBlock = prev; //should be left curly??
        List<Declaration> decList = declarationList();
        List<Statement> statList = statementList();
        return new Block(firstBlock, decList, statList);
    }

    private List<Declaration> declarationList() throws PLCException{
        List<Declaration> ret = new ArrayList<Declaration>();
        NameDef retName = null;
        boolean inDec = true;
        IToken first;
        while (inDec){
            if (current.getKind() == Kind.RES_while || current.getKind() == Kind.RES_write || current.getKind() == Kind.IDENT || current.getKind() == Kind.RCURLY)
                break; //must be end or statement
            first = current;

            retName = nameDef();
            if(match(Kind.ASSIGN)) {
                Expr decExpr = expression();
                ret.add(new Declaration(first, retName, decExpr));
            }
            else ret.add(new Declaration(first, retName, null));
            match(Kind.DOT);
        }
        return ret;
    }

    private List<Statement> statementList() throws PLCException{
        List<Statement> ret = new ArrayList<Statement>();
        IToken first = current; // should be while/write/ident
        Expr retExpr;
        Block retBlock;
        while (match(Kind.RES_while,Kind.RES_write,Kind.IDENT)){
            switch (first.getKind()){
                case IDENT -> {
                    LValue retL = lValue();
                    if (match(Kind.ASSIGN)){
                        retExpr = expression();
                        ret.add(new AssignmentStatement(first,retL,retExpr));
                    }
                    else throw new SyntaxException("Expected =");
                }
                case RES_write -> {
                    //System.out.println(current.getTokenString());
                    retExpr = expression();
                    ret.add(new WriteStatement(first,retExpr));

                }
                case RES_while -> {
                    retExpr = expression();
                    retBlock = block();
                    ret.add(new WhileStatement(first,retExpr,retBlock));
                }
                default -> throw new SyntaxException("Expected statement");
            }
            if(match(Kind.RCURLY)) break;
            if(!match(Kind.DOT)) throw new SyntaxException("Expected .");

        }

        return ret;
    }

    private LValue lValue() throws PLCException{
        IToken first = prev;

        PixelSelector retPixel = null;
        if (match(Kind.LSQUARE)){ //pixel selector
            //System.out.println(current.getTokenString());
            retPixel = pixelSelector();
        }
        if (match(Kind.COLON)){ // Channel selector
            return new LValue(first, new Ident(first), retPixel, ColorChannel.getColor(current));
        }
        return new LValue(first, new Ident(first), retPixel, null);
    }

    private Expr expression() throws PLCException{

        //try{
        if(match(Kind.RES_if)) return conditional();

        else return orExpr();
        /*}catch(ParseException e) {
            throw new ParseException(e.getMessage(), e.getErrorOffset());
        }*/
    }

    private Expr conditional() throws PLCException {
        Expr guard, trueCase = null, falseCase = null;
        IToken first = prev;
        if (match(Kind.RES_if)) { //means nested conditional so recursion
            guard = conditional();
        } else {
            guard = expression();

        }
        if (match(Kind.QUESTION)) {
            if (match(Kind.RES_if)) trueCase = conditional();
            else trueCase = expression();
        }
        else throw new SyntaxException("expected ?");
        if (match(Kind.QUESTION)){
            if (match(Kind.RES_if)) falseCase = conditional();
            else falseCase = expression();
        }
        else throw new SyntaxException("expected ?");
        return new ConditionalExpr(first,guard,trueCase,falseCase);


    }

    private Expr orExpr() throws PLCException{
        Expr expr = andExpr();
        while(match(Kind.BITOR, Kind.OR)){
            IToken op = prev;
            Expr right = andExpr();
            expr = new BinaryExpr(op, expr, op.getKind(), right);
        }
        return expr;
    }

    private Expr andExpr() throws PLCException{
        Expr expr = comparison();
        while(match(Kind.BITAND, Kind.AND)){
            IToken op = prev;
            Expr right = comparison();
            expr = new BinaryExpr(op, expr, op.getKind(), right);
        }
        return expr;
    }

    private Expr comparison() throws PLCException{
        Expr expr = power();
        while(match(Kind.LE, Kind.GE, Kind.EQ, Kind.LT, Kind.GT)){
            IToken op = prev;
            Expr right = power();
            expr = new BinaryExpr(op, expr, op.getKind(), right);
        }
        return expr;
    }

    private Expr power() throws PLCException{
        Expr expr = additive();
        while(match(Kind.EXP)){
            IToken op = prev;
            Expr right = power();
            expr = new BinaryExpr(op, expr, op.getKind(), right);
        }
        return expr;
    }

    private Expr additive() throws PLCException{
        Expr expr = multiplicative();
        while(match(Kind.PLUS, Kind.MINUS)){
            IToken op = prev;
            Expr right = multiplicative();
            expr = new BinaryExpr(op, expr, op.getKind(), right);
        }
        return expr;
    }

    private Expr multiplicative() throws PLCException{
        Expr expr = unary();
        while(match(Kind.TIMES, Kind.DIV, Kind.MOD)){
            IToken op = prev;
            Expr right = unary();
            expr = new BinaryExpr(op, expr, op.getKind(), right);
        }
        return expr;
    }

    private Expr unary() throws PLCException{
        if(match(Kind.BANG, Kind.MINUS, Kind.RES_sin, Kind.RES_cos, Kind.RES_atan)){
            IToken op = prev;
            Expr right = unary();
            return new UnaryExpr(op, op.getKind(), right);
        }
        return unaryPostExpr();
    }

    private Expr unaryPostExpr() throws PLCException{
        IToken start = current;
        Expr currentPrim = primary();
        PixelSelector retPixel = null;
        if(match(Kind.LSQUARE)) retPixel = pixelSelector(); //pixel select0r
        if(match(Kind.COLON)){ // Channel selector
            if(match(Kind.RES_red,Kind.RES_blu,Kind.RES_grn)){
                return new UnaryExprPostfix(start, currentPrim,retPixel, ColorChannel.getColor(prev));
            }
            else throw new SyntaxException("Expected color");
        }
        if (retPixel == null) return currentPrim;
        else return new UnaryExprPostfix(start, currentPrim, retPixel, null);
    }

    private PixelSelector pixelSelector() throws PLCException{
        IToken start = prev;
        Expr x = expression();
        if (!match(Kind.COMMA)) throw new SyntaxException("Expected ,");
        Expr y = expression();
        PixelSelector retPixel = new PixelSelector(start,x,y);
        match(Kind.RSQUARE);
        return retPixel;
    }

    private ExpandedPixelExpr expandedPixel() throws PLCException{
        IToken start = prev;
        Expr x = expression();
        if (!match(Kind.COMMA)) throw new SyntaxException("Expected ,");
        Expr y = expression();
        if (!match(Kind.COMMA)) throw new SyntaxException("Expected ,");
        Expr z = expression();
        ExpandedPixelExpr retPixel = new ExpandedPixelExpr(start,x,y,z);
        if (match(Kind.RSQUARE)) return retPixel;
        else throw new SyntaxException("Expected ]");
    }

    private PixelFuncExpr pixelFunc() throws PLCException{
        IToken start = prev;
        PixelSelector pixel = null;
        if (match(Kind.LSQUARE)) pixel = pixelSelector();
        else throw new SyntaxException("Expected [");
        return new PixelFuncExpr(start,start.getKind(),pixel);
    }


    private Expr primary() throws PLCException{               //add try block prob
        Expr expr;
        if(match(Kind.STRING_LIT))
            return new StringLitExpr(prev);
        else if(match(Kind.NUM_LIT))
            return new NumLitExpr(prev);
        else if(match(Kind.IDENT))
            return new IdentExpr(prev);
        else if(match(Kind.RES_Z))
            return new ZExpr(prev);
        else if(match(Kind.RES_rand))
            return new RandomExpr(prev);
        else if(match(Kind.RES_x))
            return new PredeclaredVarExpr(prev);
        else if(match(Kind.RES_y))
            return new PredeclaredVarExpr(prev);
        else if(match(Kind.RES_a))
            return new PredeclaredVarExpr(prev);
        else if(match(Kind.RES_r))
            return new PredeclaredVarExpr(prev);
        else if(match(Kind.LSQUARE)){
            expr = expandedPixel();
        }
        else if(match(Kind.RES_x_cart,Kind.RES_y_cart,Kind.RES_a_polar,Kind.RES_r_polar)){
            expr = pixelFunc();
        }
        else if(match(Kind.LPAREN)){
            expr = expression();
            if(!match(Kind.RPAREN)) throw new SyntaxException("expected right paren, )");
        }
        else{
            throw new SyntaxException("expected literal, ident, or paren");
        }
        return expr;
    }

    @Override
    public AST parse() throws PLCException {
        AST ret = null;
        if(current.getKind() == Kind.EOF){
            throw new SyntaxException("Empty program");
        }
        while (current.getKind() != Kind.EOF){
            if (match(Kind.RES_image,Kind.RES_pixel, Kind.RES_int, Kind.RES_string, Kind.RES_void)){

                ret = program();
                return ret;
            }
            else if(match(Kind.RES_if)){
                ret = conditional();
            }else{
                ret = expression();
            }
        }
        return ret;

    }
}
