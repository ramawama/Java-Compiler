package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.*;

import java.util.ArrayList;
import java.util.List;

import static edu.ufl.cise.plcsp23.IToken.Kind;

public class Parser implements IParser{
    private IScanner scanner;
    private IToken current;
    private IToken prev;//previous token
    private Block block;

    public Parser(Scanner scanner) throws PLCException{ //consume()
        this.scanner = scanner;
        current = scanner.next();
    }

    //changes token state for prev and current, returns boolean for token existence
    protected boolean match(Kind... kinds) throws PLCException{
        for(Kind k: kinds){
            if(k == current.getKind()) {
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
        if(match(Kind.IDENT)){
            ident = new Ident(prev); // create ident object if its a match

        }
        else throw new SyntaxException("expected IDENT"); // follow program grammar

        if(match(Kind.LPAREN)) paramList = param(); //checking for paramlist
        else throw new SyntaxException("Expected (");

        System.out.println(current.getTokenString());
        if (match(Kind.LCURLY)) block = block();
        else throw new SyntaxException("Expected {");

        return new Program(first, progType, ident, paramList, block);
    }

    private List<NameDef> param() throws PLCException{
        List<NameDef> retList = new ArrayList<NameDef>();
        while (!match(Kind.RPAREN)){ //while in parameters list
            match(Kind.COMMA);
            if (match(Kind.RES_image,Kind.RES_pixel, Kind.RES_int, Kind.RES_string, Kind.RES_void)){
                //NameDef must start with type
                NameDef parameter = nameDef();
                retList.add(parameter);
            }
            else if (!match(Kind.COMMA)) return retList;
            else throw new PLCException("Expected Type");
        } //maybe add error to check if param list doesnt end
        return retList;

    }

    private NameDef nameDef() throws PLCException{ //maybe change access modifier idk should be good for now
        IToken firstNameDef = prev;
        if (match(Kind.IDENT)){ //check for first type of parameters 'type IDENT'
//            System.out.println(firstNameDef.getTokenString());
//            System.out.println(current.getTokenString());
            if (match(Kind.LSQUARE)){ //means dimension
                Dimension dim = dimension();
                //System.out.println("Bad");
                return new NameDef(firstNameDef, Type.getType(firstNameDef), dim, new Ident(prev));
            }
            else {
                //System.out.println(current.getTokenString());
                return new NameDef(firstNameDef,Type.getType(firstNameDef),null,new Ident(prev));
            }

            //return new NameDef(prev,Type.getType(prev),)
        }
        return null;
    }

    private Dimension dimension() throws PLCException{
        Expr width = expression();
        Expr height = null;
        if(match(Kind.COMMA)) {
            height = expression();
        }
        else throw new PLCException("Expected ,");
        if(match(Kind.RSQUARE)){
            return new Dimension(prev,width,height);
        }
        return null;
    }

    private Block block() throws PLCException{
        IToken firstBlock = prev; //should be left curly??

        List<Declaration> decList = declarationList();
        List<Statement> statList = statementList();
        return new Block(firstBlock, decList, statList);
    }

    private List<Declaration> declarationList() throws PLCException{
        List<Declaration> ret = new ArrayList<Declaration>();
//        NameDef firstNameDef = nameDef();
//        while (nameDef() != null){
//
//        }
        return ret;
    }

    private List<Statement> statementList() throws PLCException{
        List<Statement> ret = new ArrayList<Statement>();
        return ret;
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
        return primary();
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
