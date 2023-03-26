package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.Token;
import edu.ufl.cise.plcsp23.TypeCheckException;

import java.util.HashMap;
import java.util.List;

import static edu.ufl.cise.plcsp23.IToken.Kind.*;


public class ASTVisit implements ASTVisitor{

    public static class SymbolTable {
        HashMap<String, Declaration> entries = new HashMap<>();

        public boolean insert(String name, Declaration declaration){
            return (entries.putIfAbsent(name, declaration)==null);
        }

        public Declaration lookup(String name) {
            return entries.get(name);
        }
    }

    SymbolTable symbolTable = new SymbolTable();

    private void check(boolean cond, String message) throws TypeCheckException {
        if(!cond) {throw new TypeCheckException(message);}
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
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException {
        String name = declaration.getNameDef().toString();
        boolean inserted = symbolTable.insert(name, declaration);
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
        stringLitExpr.setType(Type.STRING);
        return Type.STRING;  //or getValue?
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {
        numLitExpr.setType(Type.INT);
        return Type.INT;
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
        return predeclaredVarExpr.getType();
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCException {
        //Object ident = program.ident.visit(this,null);
        List<NameDef> paramList = program.getParamList();
        //Object block = program.block.visit(this, null);
        for(NameDef node : paramList) {
            node.visit(this,arg);
        }
        return program;
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
        String name = identExpr.getName();
        Declaration dec = symbolTable.lookup(name);
        check(dec != null, "unidentified ident" + name);
        check(dec.initialized, "using uninitialized variable");
        //save declaration?
        Type type = dec.getNameDef().getType();
        identExpr.setType(type);
        return type;
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitZExpr(ZExpr constExpr, Object arg) throws PLCException {
        return constExpr.getType();
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        return randomExpr.getType();
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException {
        return null;
    }
}
