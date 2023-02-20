package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.*;

import static edu.ufl.cise.plcsp23.IToken.Kind;

public class Parser implements IParser{
    private IScanner scanner;
    private IToken current;
    private IToken prev;      //previous token

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

    //add a try catch block to each method or just the primary() method?
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
            Expr right = expression();
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

    private Expr primary() throws PLCException{               //add try block prob. change error to parseException? (in header & instead of syntax)
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
            if(!match(Kind.RPAREN)) throw new SyntaxException("expected right paran, )");
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
            if(match(Kind.RES_if)){
                ret = conditional();
            }else{
                ret = expression();
            }
        }
        return ret;

    }
}
