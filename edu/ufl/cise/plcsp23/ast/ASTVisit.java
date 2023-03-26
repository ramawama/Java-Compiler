package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.TypeCheckException;

import java.util.HashMap;


public class ASTVisit implements ASTVisitor{

    public static class SymbolTable {
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
        if(cond) {throw new TypeCheckException(message);} //if true means error/exception
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
        String name = declaration.getNameDef().toString();
        boolean inserted = symbolTable.insert(name, declaration.getNameDef());
        check(inserted, "variable " + name + " already declared");
        Expr init = declaration.getInitializer();;
        if(init != null){
            Type initType = (Type)init.visit(this,arg);
            check(compatible(declaration.getNameDef().type, initType), "expression and declared types do not match");
            declaration.initialized = true;
        }
        return null;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        Expr expr1 = dimension.getWidth();
        Expr expr2 = dimension.getHeight();
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
        return null;
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
        return null;
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
        return null;
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException {
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
        for (int i = 0; i < program.getParamList().size(); i++){ //visits namedef
            visitNameDef(program.getParamList().get(i),arg);
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
        return null;
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        System.out.println(nameDef.getIdent().getName());
        check(nameDef.getType().equals(Type.VOID),"Type cannot be void");
        if (nameDef.getDimension() != null){
            check(nameDef.getType().equals(Type.IMAGE),"Type must equal image if dimension is declared");
            visitDimension(nameDef.getDimension(),arg);
        }
        check(!symbolTable.insert(nameDef.getIdent().getName(),nameDef), "Name already initialized");
            //if false already present
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
        return null;
    }
}
