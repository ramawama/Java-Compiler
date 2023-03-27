package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.TypeCheckException;

import java.util.HashMap;
import java.util.Stack;


public class ASTVisit implements ASTVisitor{

    public static class SymbolTable {
        static Type progType;
        Stack<Object> sStack = new Stack<>(); // for authentication in declaring and initializing in same expression @test8
        HashMap<String, NameDef> entries = new HashMap<>(); //changed to namedef

        public boolean insert(String name, NameDef namedef){
            return (entries.putIfAbsent(name, namedef)==null);
        }

        public NameDef lookup(String name) {
            return entries.get(name);
        }
    }

    SymbolTable symbolTable = new SymbolTable();

    private void check(boolean cond, String message) throws TypeCheckException {
        if(!cond) {
            System.out.println(message);
            throw new TypeCheckException(message);} //if false means error/exception
    }

    private boolean compatible(Type target, Type rhs){
        return (target == rhs ||
                (target == Type.IMAGE && rhs == Type.PIXEL) ||
                (target == Type.IMAGE && rhs == Type.STRING) ||
                (target == Type.PIXEL && rhs == Type.INT) ||
                (target == Type.INT && rhs == Type.PIXEL) ||
                (target == Type.STRING && rhs == Type.INT) ||
                (target == Type.STRING && rhs == Type.PIXEL) ||
                (target == Type.STRING && rhs == Type.IMAGE));
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException {

        return null;
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException { //fix check!! - R
        NameDef nameDef = declaration.getNameDef();
        nameDef.visit(this,arg); //ensures nameDef is properly typed
        Expr init = declaration.getInitializer();
        if(nameDef.getType() == Type.IMAGE)
            check(init != null || nameDef.dimension != null, "Type image must have initializer or dimension");
        if(init != null){
            Type initType = (Type) init.visit(this,arg);
            System.out.println(nameDef.getType());
            System.out.println(initType);
            check(compatible(declaration.getNameDef().getType(), initType), "expression and declared types do not match");

        }
        return null;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        Expr expr1 = dimension.getWidth();
        Expr expr2 = dimension.getHeight();
        expr1.setType((Type) expr1.visit(this,arg));
        expr2.setType((Type) expr1.visit(this,arg));
        System.out.println(expr1.firstToken.getTokenString());
        System.out.println(expr2.firstToken.getTokenString());
        System.out.println(expr1.getType());
        check(expr1.getType().equals(Type.INT),"Type should be int");
        check(expr2.getType().equals(Type.INT),"Type should be int");

        return null;
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        Object right = binaryExpr.right.visit(this, arg);
        Object left = binaryExpr.left.visit(this, arg);
        //System.out.println(right.toString());
        //System.out.println(left.toString());
        Type result = null;

        switch(binaryExpr.op){
            case PLUS -> {
                if(left == Type.INT && right == Type.INT) {result = Type.INT;}
                else if (left == Type.STRING && right == Type.STRING) {result = Type.STRING;}
                else if(left == Type.PIXEL && right == Type.PIXEL) {result = Type.PIXEL;}
                else if(left == Type.IMAGE && right == Type.IMAGE) {result = Type.IMAGE;}
                else {check(false, "incompatible types");}
            }
            case MINUS -> {
                if(left == Type.INT && right == Type.INT) {result = Type.INT;}
                else if(left == Type.PIXEL && right == Type.PIXEL) {result = Type.PIXEL;}
                else if(left == Type.IMAGE && right == Type.IMAGE) {result = Type.IMAGE;}
                else{check(false, "incompatible types");}
            }
            case TIMES,DIV,MOD -> {
                if(left == Type.INT && right == Type.INT) {result = Type.INT;}
                else if(left == Type.PIXEL && right == Type.PIXEL) {result = Type.PIXEL;}
                else if(left == Type.IMAGE && right == Type.IMAGE) {result = Type.IMAGE;}
                else if(left == Type.PIXEL && right == Type.INT) {result = Type.PIXEL;}
                else if(left == Type.IMAGE && right == Type.INT) {result = Type.IMAGE;}
                else {check(false, "incompatiblel types");}
            }
            case EXP -> {
                if(left == Type.INT && right == Type.INT) {result = Type.INT;}
                else if(left == Type.PIXEL && right == Type.INT) {result = Type.PIXEL;}
                else {check(false, "incompatiblel types");}
            }
            case LT, GT, LE, GE, OR, AND -> {
                if(left == Type.INT && right == Type.INT) {result = Type.INT;}
                else {check(false, "incompatible types");}
            }
            case BITOR, BITAND -> {
                if(left == Type.PIXEL && right == Type.PIXEL) {result = Type.PIXEL;}
                else {check(false, "incompatible types");}
            }
            default ->{
                throw new PLCException("compiler error");
            }
        }
        binaryExpr.setType(result);
        return result;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException {
        Expr prim = unaryExprPostfix.getPrimary();
        PixelSelector pixel = unaryExprPostfix.getPixel();
        ColorChannel color = unaryExprPostfix.getColor();
        prim.setType( (Type) prim.visit(this,arg));
        check(pixel != null || color != null, "At least one pixelSelector / ChannelSelector needed");
        if(prim.getType() == Type.PIXEL){
            check(pixel == null && color != null,"Pixel selector must be null and Channel must be present");
            unaryExprPostfix.setType(Type.INT);
        } //got these from first table
        if(prim.getType() == Type.IMAGE){
            if(pixel == null && color != null) unaryExprPostfix.setType(Type.IMAGE);
            else if(pixel != null){
                pixel.visit(this,arg);
                if(color == null)unaryExprPostfix.setType(Type.PIXEL);
                if(color != null)unaryExprPostfix.setType(Type.INT);
            }
            else check(false,"Incorrect UnaryExprPostFix");
        }
        System.out.println(unaryExprPostfix.getType());
        return unaryExprPostfix.getType();
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException {
        return Type.STRING;
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {

        return Type.INT;
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException {
        Expr expr0 = pixelSelector.getX();
        expr0.setType( (Type) expr0.visit(this,arg));
        Expr expr1 = pixelSelector.getY();
        expr1.setType( (Type) expr1.visit(this,arg));
        check(expr0.getType() == Type.INT && expr1.getType() == Type.INT,"Pixel Expr0 and Expr1 must be int");
        return null;
    }

    @Override
    public Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCException {
        symbolTable.progType = program.type;
        for (int i = 0; i < program.getParamList().size(); i++){ //visits namedef
            visitNameDef(program.getParamList().get(i),arg);
        }
        Block block = program.getBlock();
        for (Declaration dList : block.decList){
            dList.visit(this,arg);
        }
        for (Statement sList : block.statementList){
            sList.visit(this,arg);
        }
        return null;
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
//        String name = identExpr.getName();
//        Declaration dec = symbolTable.lookup(name);
//        check(dec != null, "unidentified ident" + name);
//        check(dec.initialized, "using uninitialized variable");
//        //save declaration?
//        Type type = dec.getNameDef().getType();
//        identExpr.setType(type);
//        return type;
        NameDef checker = symbolTable.lookup(identExpr.getName());
        check(!checker.equals(null),"Undefined variable");
        check(!symbolTable.sStack.peek().equals(checker),"Initializer cannot refer to name being defined" );

        return checker.getType();
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
//        Ident ident = lValue.getIdent();
//        PixelSelector pixel = lValue.getPixelSelector();
//        ColorChannel color = lValue.getColor();
//        check(symbolTable.entries.containsKey(ident.getName()),"Ident has not been declared / not visible");
//        System.out.println("ident.getName()");
//        return ident.def.getType();
        return null;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        //System.out.println(nameDef.getIdent().getName());

        check(!nameDef.getType().equals(Type.VOID),"Type cannot be void");
        if (nameDef.getDimension() != null){
            System.out.println(nameDef.getType());
            check(nameDef.getType().equals(Type.IMAGE),"Type must equal image if dimension is declared");
            visitDimension(nameDef.getDimension(),arg);
        }
        check(symbolTable.insert(nameDef.getIdent().getName(),nameDef), "Name already declared");
            //if false already present
        symbolTable.sStack.push(nameDef);
        return null;
    }

    @Override
    public Object visitZExpr(ZExpr constExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException {
        System.out.println("MADE IT");
        System.out.println(returnStatement.getE().toString());
        Expr expr = returnStatement.getE();
        expr.setType( (Type) expr.visit(this,arg));
        check(compatible(expr.getType(), symbolTable.progType), "Expr.type is not assignment compatible with program.type");
        return expr;
    }
}
