package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.runtime.ConsoleIO;

import java.util.List;

import static edu.ufl.cise.plcsp23.IToken.Kind;
import static edu.ufl.cise.plcsp23.IToken.Kind.EXP;

public class CodeGenVisitor implements ASTVisitor {
    StringBuilder prog = new StringBuilder();
    String imports;
    boolean logic = false;
    boolean inReturn = false;
    boolean ifString = false;
    boolean isNameString = false;
    @Override
    public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException {
        StringBuilder state = new StringBuilder();
        state.append(statementAssign.getLv().visit(this, arg)).append(" = ");
        if(statementAssign.getE().firstToken.getKind() == Kind.NUM_LIT && isNameString){
            state.append("\"").append(statementAssign.getE().visit(this, arg)).append("\"");
        }else{
            state.append(statementAssign.getE().visit(this, arg));
        }
        state.append(";\n\t\t");
        return state;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        StringBuilder bin = new StringBuilder();
        boolean isEXP = false;
        Object left = binaryExpr.getLeft().visit(this, arg);
        Object right = binaryExpr.getRight().visit(this, arg);
        bin.append("(");
        if(binaryExpr.op == EXP){
            isEXP = true;
            imports = ("import java.lang.Math;");
            bin.append("(int) Math.pow(");
        }
        if ((binaryExpr.op == Kind.AND || binaryExpr.op == Kind.OR) && !inReturn){
            bin.append("(");
        }
        bin.append(left);
        if(isEXP) bin.append(",");
        switch(binaryExpr.op){
            case PLUS -> bin.append("+");
            case MINUS -> bin.append("-");
            case TIMES -> bin.append("*");
            case DIV -> bin.append("/");
            case MOD -> bin.append("%");
            case EQ -> bin.append("==");
            case LE -> bin.append("<=");
            case GE -> bin.append(">=");
            case LT -> bin.append("<");
            case GT -> bin.append(">");
            case BITOR -> bin.append("|");
            case BITAND -> bin.append("&");
            case OR -> bin.append("!=0) || (");
            case AND -> bin.append("!=0) && (");

        }
        bin.append(right);
        //handles boolean vs int
        if(binaryExpr.op == Kind.GE || binaryExpr.op == Kind.LE || binaryExpr.op == Kind.LT || binaryExpr.op == Kind.GT || binaryExpr.op == Kind.EQ ){
            bin.append("? 1 : 0)");
        }else if (binaryExpr.op == Kind.AND || binaryExpr.op == Kind.OR){
            logic = true;
            bin.append("!=0)");
            if(!inReturn){
                bin.append("? 1 : 0)");
            }
        }else{
            bin.append(")");
        }
        if(binaryExpr.op == EXP){
            bin.append(")");
        }
        return bin;
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLCException {
        StringBuilder b = new StringBuilder();
        List<Declaration> decList = block.getDecList();
        for(Declaration dList: decList){
            b.append(dList.visit(this, arg));
        }
        List<Statement> stateList = block.getStatementList();
        for(Statement sList: stateList){
            b.append(sList.visit(this, arg));
        }
        return b;
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException {
        StringBuilder cond = new StringBuilder();
        Object expr0 = conditionalExpr.getGuard().visit(this, arg);
        cond.append("(");
        if(logic){
            cond.append(expr0);
        }else{
            cond.append("(").append(expr0).append("!=0)");  // !=0 handles boolean vs int, same in while
        }
        cond.append(" ? ");
        cond.append(conditionalExpr.getTrueCase().visit(this, arg));
        cond.append(" : ");
        cond.append(conditionalExpr.getFalseCase().visit(this, arg));
        cond.append(")");
        return cond;
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException {
        StringBuilder dec = new StringBuilder();
        dec.append(declaration.getNameDef().visit(this, arg));
        if(declaration.getNameDef().getType() == Type.STRING){
            isNameString = true;
        }
        if(declaration.getInitializer() != null){
            dec.append(" = ");
            if(declaration.getNameDef().getType() == Type.STRING && declaration.getInitializer().firstToken.getKind() == Kind.NUM_LIT) {
                dec.append("\"").append(declaration.getInitializer().visit(this, arg)).append("\";\n\t\t");
            }else{
                dec.append(declaration.getInitializer().visit(this, arg)).append(";\n\t\t");
            }
        }else{
            if(declaration.getNameDef().getType() == Type.STRING ) {
                isNameString = true;
            }
            dec.append(";\n\t\t");
        }
        return dec;
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
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
        return identExpr.getName();
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        PixelSelector px = lValue.getPixelSelector();
        ColorChannel color =  lValue.getColor();
        return lValue.getIdent().getName();
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        StringBuilder name  = new StringBuilder();
        String type = nameDef.getType().name().toLowerCase();
        if (type.equals("string")){
            isNameString = true;
            type = "String";
        }
        name.append(type).append(" ").append(nameDef.getIdent().getName());
        return name;
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {
        //ConsoleIO.write(numLitExpr.getValue());
        return numLitExpr.getValue();
    }

    @Override
    public Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException {
        return null;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCException {
        StringBuilder clas = new StringBuilder();
        //need to add import statements like consoleIO into the beginning
        prog.append("public static ");
        String type = program.getType().name().toLowerCase();
        if (type.equals("string")){
            ifString = true;
            type = "String";
        }
        prog.append(type).append(" apply(");
        //visit parameters
        int i = 1;
        for(NameDef params: program.getParamList()){
            prog.append(params.visit(this, arg));
            if(i++ != program.getParamList().size()){ prog.append(", "); }
        }
        //visit block
        prog.append("){\n\t\t").append(program.getBlock().visit(this, arg)).append("\n\t}");
        if (imports != null) clas.append(imports).append('\n');
        //if package name != “”, append package name?  logic might have to do with the string passed in compilercomponentfactory, but idk how u would pass a string into a visitor implementation
        clas.append("public class ").append(program.getIdent().getName()).append("{ \n\t").append(prog).append("\n}");
        return clas.toString();
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        imports = ("import java.lang.Math;");
        String rand = "(int)Math.floor(Math.random() * 256)";
        return rand;
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException {
        inReturn = true;
        StringBuilder ret = new StringBuilder();
        ret.append("return ");
        if(ifString && returnStatement.getE().firstToken.getKind() == Kind.NUM_LIT ){
            ret.append("\"");
        }
        ret.append(returnStatement.getE().visit(this, arg));
        if(ifString && returnStatement.getE().firstToken.getKind() == Kind.NUM_LIT ){
            ret.append("\"");
        }
        ret.append(";");
        return ret.toString();
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException {
        return "\"" + stringLitExpr.getValue() + "\"";  //Test doesnt pass if escape sequences r ignored so whtv
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
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException { //generated code format doesnt match exactly with scopes but just match parentheses when looking
        StringBuilder wile = new StringBuilder();
        Object guard = whileStatement.getGuard().visit(this,arg);
        wile.append("while((").append(guard).append("!=0)) {\n\t\t\t");
        wile.append(whileStatement.getBlock().visit(this,arg)).append("\t\t}\n\t\t");
        return wile;
    }

    @Override
    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException {
        StringBuilder write = new StringBuilder();
        write.append("ConsoleIO.write(").append(statementWrite.getE().visit(this,arg)).append(");\n\t\t");
        imports = ("import edu.ufl.cise.plcsp23.runtime.ConsoleIO;");
        //ConsoleIO.write();  //should return value associated with ident not ident
        return write;
    }

    @Override
    public Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException {
        return 255;
    }
}
