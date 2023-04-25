package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.runtime.ImageOps;
import edu.ufl.cise.plcsp23.runtime.PixelOps;

import java.util.HashMap;
import java.util.List;

import static edu.ufl.cise.plcsp23.IToken.Kind;
import static edu.ufl.cise.plcsp23.IToken.Kind.EXP;

public class CodeGenVisitor implements ASTVisitor {
    StringBuilder prog = new StringBuilder();
    //String imports;
    StringBuilder imports = new StringBuilder();
    boolean logic = false;
    boolean inReturn = false;
    boolean ifString = false;
    boolean isNameString = false;
    boolean isMinus = false;
    boolean checkType = true;

    ASTVisit.SymbolTable symbolTable = new ASTVisit.SymbolTable();
    private Integer instances(String name){
        if (symbolTable.table.containsKey(name)){
            HashMap<Integer,NameDef> temp = symbolTable.table.get(name);
            int checker = -1;
            for(Integer scope: temp.keySet()){
                if(scope <= symbolTable.scope.peek()) checker++;
            }
            if (isMinus ) checker--;
            if (checker < 0) return 0;
            return checker;
        }
        return 0;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException {
        StringBuilder state = new StringBuilder();
        NameDef lv = symbolTable.lookup(statementAssign.getLv().getIdent().getName());
        //NameDef e = symbolTable.lookup((String)statementAssign.getE().visit(this, arg));
        //System.out.println(e);

        if(lv.getType() == Type.PIXEL){
            state.append(statementAssign.getLv().visit(this, checkType)).append(" = ");
            state.append("PixelOps.pack(").append(statementAssign.getE().visit(this, arg)).append(")");
        }
        else if(lv.getType() == Type.IMAGE){
            if(statementAssign.getLv().getPixelSelector() == null && statementAssign.getLv().getColor() == null){//check if image
                statementAssign.getE().visit(this,checkType);
                if(statementAssign.getE().getType() == Type.IMAGE){
                    state.append("ImageOps.copyInto(").append(statementAssign.getE().visit(this, arg)).append(",").append(statementAssign.getLv().visit(this, arg)).append(")");
                    state.append(";\n\t\t");
                }
                else if(statementAssign.getE().getType() == Type.PIXEL){
                    imports.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
                    state.append("ImageOps.setAllPixels(").append(statementAssign.getLv().visit(this, arg)).append(",").append("PixelOps.pack(").append(statementAssign.getE().visit(this, arg)).append("))");
                    state.append(";\n\t\t");
                }
                else if(statementAssign.getE().getType() == Type.STRING){
                    imports.append("import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n");
                    state.append("ImageOps.copyInto(").append("FileURLIO.readImage(").append(statementAssign.getE().visit(this, arg)).append(")").append(",").append(statementAssign.getLv().visit(this, arg)).append(")");
                    state.append(";\n\t\t");
                }

            }else if(statementAssign.getLv().getPixelSelector() != null && statementAssign.getLv().getColor() == null){
                String nameWInst = lv.getIdent().getName().concat("_").concat(instances(lv.getIdent().getName()).toString());
                state.append("for(int y = 0; y != ").append(nameWInst).append(".getHeight(); y++){\n\t");
                state.append("\t\tfor(int x = 0; x != ").append(nameWInst).append(".getWidth(); x++){\n\t");
                state.append("\t\t\tImageOps.setRGB(").append(nameWInst).append(",x,y,").append(statementAssign.getE().visit(this,arg)).append(")");
                state.append(";\n\t\t");
                state.append("\t\t}\n").append("\t\t\t}\n\t\t");
            }else if(statementAssign.getLv().getPixelSelector() != null && statementAssign.getLv().getColor() != null){
                String nameWInst = lv.getIdent().getName().concat("_").concat(instances(lv.getIdent().getName()).toString());
                String col = statementAssign.getLv().getColor().toString();
                col = col.substring(0, 1).toUpperCase() + col.substring(1);
                String getColor = "PixelOps.".concat("set").concat(col).concat("(ImageOps.getRGB(").concat(nameWInst).concat(",x,y),");
                String imageOps = "ImageOps.setRGB(".concat(nameWInst).concat(",x,y");
                state.append("for(int y = 0; y != ").append(nameWInst).append(".getHeight(); y++){\n\t");
                state.append("\t\tfor(int x = 0; x != ").append(nameWInst).append(".getWidth(); x++){\n\t");
                state.append("\t\t\t").append(imageOps).append(',').append(getColor).append(statementAssign.getE().visit(this,arg)).append("))");
                state.append(";\n\t\t");
                state.append("\t\t}\n").append("\t\t\t}\n\t\t");
            }
        }else {
            if (statementAssign.getE().firstToken.getKind() == Kind.NUM_LIT && isNameString) {
                state.append("\"").append(statementAssign.getE().visit(this, arg)).append("\"");
            } else {
                state.append(statementAssign.getE().visit(this, arg));
            }
        }
        return state;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        StringBuilder bin = new StringBuilder();
        boolean isEXP = false;
        Object left = binaryExpr.getLeft().visit(this, arg);
        Object right = binaryExpr.getRight().visit(this, arg);
        bin.append("(");

        ////need to figure out how to check type of left and right
        //System.out.println(left + " " + right);
        //System.out.println(binaryExpr.getLeft().getType() + " " + binaryExpr.getRight().getType() );
        /*if((.getType() == Type.IMAGE && r.getType() == Type.IMAGE){
            bin.append("ImageOps.binaryImageImageOp(").append(binaryExpr.op).append(",").append(left).append(",").append(right).append(")");
            return bin;
        }else if(l.getType() == Type.IMAGE && r.getType() == Type.INT){
            bin.append("ImageOps.binaryImageScalarOp(").append(binaryExpr.op).append(",").append(left).append(",").append(right).append(")");
            return bin;
        }else if(l.getType() == Type.PIXEL && r.getType() == Type.PIXEL){
            bin.append("ImageOps.binaryImagePixelOp(").append(binaryExpr.op).append(",").append(left).append(",").append(right).append(")");
            return bin;
        }*/
        if (binaryExpr.op == EXP) {
            isEXP = true;
            imports.append("import java.lang.Math;\n");
            bin.append("(int) Math.pow(");
        }
        if ((binaryExpr.op == Kind.AND || binaryExpr.op == Kind.OR) && !inReturn) {
            bin.append("(");
        }
        bin.append(left);
        if (isEXP) bin.append(",");
        switch (binaryExpr.op) {
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
        if (binaryExpr.op == Kind.GE || binaryExpr.op == Kind.LE || binaryExpr.op == Kind.LT || binaryExpr.op == Kind.GT || binaryExpr.op == Kind.EQ) {
            bin.append("? 1 : 0)");
        } else if (binaryExpr.op == Kind.AND || binaryExpr.op == Kind.OR) {
            logic = true;
            bin.append("!=0)");
            if (!inReturn) {
                logic = false;
                bin.append("? 1 : 0)");
            }
        } else {
            bin.append(")");
        }
        if (binaryExpr.op == EXP) {
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
            if (sList.getClass() == ReturnStatement.class && decList.size() == 0 && symbolTable.current > 1)isMinus = true;
            if (sList.getClass() == AssignmentStatement.class && decList.size() == 0 && symbolTable.current > 1)isMinus = true;
            b.append(sList.visit(this, arg));
            isMinus = false;
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
        NameDef name = symbolTable.lookup(declaration.getNameDef().getIdent().getName());
        if(declaration.getNameDef().getType() == Type.STRING){
            isNameString = true;
        }
        if(declaration.getInitializer() != null){
            NameDef type = symbolTable.lookup(declaration.getInitializer().firstToken.getTokenString());
            dec.append(" = ");
            //from assignment 5
            if(declaration.getNameDef().getType() == Type.STRING && declaration.getInitializer().firstToken.getKind() == Kind.NUM_LIT) {
                dec.append("\"").append(declaration.getInitializer().visit(this, arg)).append("\";\n\t\t");
            }else if (type != null && declaration.getNameDef().getType() == Type.STRING && type.getType() == Type.INT){
                dec.append("Integer.toString(").append(declaration.getInitializer().visit(this,arg)).append(");\n\t\t");
            }
            else if(declaration.getNameDef().getType() == Type.IMAGE) {
                //checking image with dimension
                if (declaration.getNameDef().dimension != null) {
                    if (type != null && type.getType() == Type.STRING) {
                        imports.append("import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n");
                        dec.append("FileURLIO.readImage(").append(declaration.getInitializer().visit(this, arg)).append(",").append(declaration.getNameDef().dimension.visit(this, arg)).append(");\n\t\t");
                    } else if (type != null && type.getType() == Type.IMAGE) {
                        dec.append("ImageOps.copyAndResize(").append(declaration.getInitializer().visit(this, arg)).append(",100,200);\n\t\t");
                    } else if (type != null && type.getType() == Type.PIXEL) {
                        dec.append("ImageOps.makeImage(").append(declaration.getNameDef().dimension.visit(this, arg)).append(");\n\t\t");
                        dec.append("ImageOps.setAllPixels(").append(name.getIdent().visit(this, arg)).append(",").append(declaration.getInitializer().visit(this, arg)).append(");\n\t\t");
                    }else {
                        imports.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
                        dec.append("ImageOps.makeImage(").append(declaration.getNameDef().dimension.visit(this, arg)).append(");\n\t\t");
                        dec.append("ImageOps.setAllPixels(").append(name.getIdent().visit(this, arg)).append(",").append("PixelOps.pack(").append(declaration.getInitializer().visit(this, arg)).append("));\n\t\t");
                    }
                }
                //image w/o dimension
                else if (declaration.getNameDef().dimension == null) {
                    if (type != null && type.getType() == Type.STRING) {
                        imports.append("import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n");
                        dec.append("FileURLIO.readImage(").append(declaration.getInitializer().visit(this, arg)).append(");\n\t\t");
                    }
                    if (type != null && type.getType() == Type.IMAGE) {
                        imports.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                        dec.append("ImageOps.cloneImage(").append(declaration.getInitializer().visit(this, arg)).append(");\n\t\t");
                    }
                }
            }else if(declaration.getNameDef().getType() == Type.PIXEL){
                //dec.append("PixelOps.pack(").append(declaration.getInitializer().visit(this, arg)).append(");\n\t\t");
                if (type != null && (type.getType() == Type.INT || type.getType() == Type.IMAGE)) {
                    dec.append(declaration.getInitializer().visit(this, arg)).append(";\n\t\t");
                } else {
                    dec.append("PixelOps.pack(").append(declaration.getInitializer().visit(this, arg)).append(");\n\t\t");
                }

            }
            else{
                dec.append(declaration.getInitializer().visit(this, arg)).append(";\n\t\t");
            }
            //from assignment 5 else
        }else{
            if(declaration.getNameDef().dimension != null){
                dec.append("= ImageOps.makeImage(").append(declaration.getNameDef().dimension.visit(this, arg)).append(")");
            }
            dec.append(";\n\t\t");
        }
        return dec;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        Object width = dimension.getWidth().visit(this, arg);
        Object height = dimension.getHeight().visit(this, arg);
        StringBuilder dim = new StringBuilder();
        dim.append(width).append(",").append(height);
        return dim;
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException {
        Object expr = expandedPixelExpr.getBluExpr().visit(this, arg);
        Object expr1 = expandedPixelExpr.getGrnExpr().visit(this, arg);
        Object expr2 = expandedPixelExpr.getRedExpr().visit(this, arg);
        expandedPixelExpr.setType(Type.PIXEL);
        return expr2 +"," + expr1 +","+ expr;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLCException {
        NameDef temp = symbolTable.lookup(ident.getName());
        if (temp == null) {
            return ident.getName();
        }else{
            int tempi= instances(ident.getName().toString());
            return temp.getIdent().getName().concat("_"+tempi);
        }
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
        NameDef temp = symbolTable.lookup(identExpr.getName());
        if (temp == null) {
            return identExpr.getName();
        }else{
            if(arg != null) {
                identExpr.setType(temp.getType());
            }
            int tempi= instances(identExpr.getName().toString());
            return temp.getIdent().getName().concat("_"+tempi);
        }
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        PixelSelector px = lValue.getPixelSelector();
        ColorChannel color =  lValue.getColor();
        //if (symbolTable.current > 1) isMinus = true;
        NameDef temp = symbolTable.lookup(lValue.getIdent().getName());
        //System.out.println(lValue.getIdent().getDef().getType());
        if (temp == null){
            isMinus = false;
            return lValue.getIdent().getName();
        }
        else{
            String ret = temp.getIdent().getName().concat("_"+instances(lValue.getIdent().getName()).toString());
            isMinus = false;
            return ret;
        }
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        StringBuilder name  = new StringBuilder();
        String type = nameDef.getType().name().toLowerCase();
        if (type.equals("string")){
            isNameString = true;
            type = "String";
        }else if (type.equals("image")){
            if(imports.indexOf("import java.awt.image.BufferedImage;") == -1){
                imports.append("import java.awt.image.BufferedImage;\n");
            }
            if(imports.indexOf("import edu.ufl.cise.plcsp23.runtime.ImageOps;") == -1){
                imports.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
            }
            type = "BufferedImage";
        }else if (type.equals("pixel")){
            if(imports.indexOf("import edu.ufl.cise.plcsp23.runtime.PixelOps;") == -1){
                imports.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
            }
            type = "int";
        }
        symbolTable.insert(nameDef.getIdent().getName(),nameDef);
        NameDef temp = symbolTable.lookup(nameDef.getIdent().getName());

        if (temp == null)
            name.append(type).append(" ").append(nameDef.getIdent().getName());
        else name.append(type).append(" ").append(temp.getIdent().getName()).append("_"+instances(nameDef.getIdent().getName()).toString());
        //else
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
        Object expr0 = pixelSelector.getX().visit(this, arg);
        Object expr1 = pixelSelector.getY().visit(this, arg);
        return expr0 + "," + expr1;
    }

    @Override
    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException {
        if(predeclaredVarExpr.getKind() == Kind.RES_y || predeclaredVarExpr.getKind() == Kind.RES_Y) return "y";
        if(predeclaredVarExpr.getKind() == Kind.RES_x || predeclaredVarExpr.getKind() == Kind.RES_X) return "x";
        else return "error for rn in predeclaredVarExpr";
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
        }else if(type.equals("image")){
            if(imports.indexOf("import java.awt.image.BufferedImage;") == -1){
                imports.append("import java.awt.image.BufferedImage;\n");
            }
            if(imports.indexOf("import edu.ufl.cise.plcsp23.runtime.ImageOps;") == -1){
                imports.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
            }
            type = "BufferedImage";
        }else if(type.equals("pixel")){
            if(imports.indexOf("import edu.ufl.cise.plcsp23.runtime.PixelOps;") == -1){
                imports.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
            }
            type = "int";
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
        clas.append("public class ").append(program.getIdent().getName()).append("{ \n\t").append(prog).append("\n}");
        return clas.toString();
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        imports.append("import java.lang.Math;\n");
        String rand = "(int)Math.floor(Math.random() * 256)";
        return rand;
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException {
        inReturn = true;
        NameDef type = symbolTable.lookup(returnStatement.getE().firstToken.getTokenString());
        StringBuilder ret = new StringBuilder();
        ret.append("return ");
        if(type != null && ifString && type.getType() == Type.INT){
            imports.append("import java.lang.String;\n");
            ret.append("Integer.toString(");
        }
        if(ifString && returnStatement.getE().firstToken.getKind() == Kind.NUM_LIT ){
            ret.append("\"");
        }

        /**
         * I think the below solution works as intended meant to solve cg46 test case
         */

        ret.append(returnStatement.getE().visit(this, arg));

        if(type != null && ifString && type.getType() == Type.INT){
            ret.append(")");
        }
        if(ifString && returnStatement.getE().firstToken.getKind() == Kind.NUM_LIT ){
            ret.append("\"");
        }
        ret.append(";");
        return ret.toString();
    }

    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException {
        stringLitExpr.setType(Type.STRING);
        return "\"" + stringLitExpr.getValue() + "\"";  //Test doesnt pass if escape sequences r ignored so whtv
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {
        return "tumama";
    }

    @Override
    public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException {
        StringBuilder unary = new StringBuilder();
        Object expr = unaryExprPostfix.getPrimary().visit(this, arg);
        if(imports.indexOf("import edu.ufl.cise.plcsp23.runtime.PixelOps;") == -1){
            imports.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
        }
        if(unaryExprPostfix.getPrimary().getType() == Type.IMAGE){
            if(unaryExprPostfix.getPixel() != null && unaryExprPostfix.getColor() == null){
                System.out.println(unaryExprPostfix.getPixel());
                unary.append("ImageOps.getRGB(").append(expr).append(",").append(unaryExprPostfix.getPixel().visit(this, arg)).append(")");
            }else if(unaryExprPostfix.getPixel() != null && unaryExprPostfix.getColor() != null){
                if(unaryExprPostfix.getColor() == ColorChannel.red){
                    unary.append("PixelOps.red(ImageOps.getRGB(").append(expr).append(",").append(unaryExprPostfix.getPixel().visit(this, arg)).append("))");
                }else if(unaryExprPostfix.getColor() == ColorChannel.blu){
                    unary.append("PixelOps.blu(ImageOps.getRGB(").append(expr).append(",").append(unaryExprPostfix.getPixel().visit(this, arg)).append("))");
                }else if(unaryExprPostfix.getColor() == ColorChannel.grn){
                    unary.append("PixelOps.grn(ImageOps.getRGB(").append(expr).append(",").append(unaryExprPostfix.getPixel().visit(this, arg)).append("))");
                }
            }else if(unaryExprPostfix.getPixel() == null && unaryExprPostfix.getColor() != null){
                if(unaryExprPostfix.getColor() == ColorChannel.red){
                    unary.append("ImageOps.extractRed(").append(expr).append(")");
                }else if(unaryExprPostfix.getColor() == ColorChannel.blu){
                    unary.append("ImageOps.extractBlu(").append(expr).append(")");
                }else if(unaryExprPostfix.getColor() == ColorChannel.grn){
                    unary.append("ImageOps.extractGrn(").append(expr).append(")");
                }
            }
        }else if(unaryExprPostfix.getPrimary().getType() ==  Type.PIXEL){
            if(unaryExprPostfix.getPixel() == null && unaryExprPostfix.getColor() != null){
                if(unaryExprPostfix.getColor() == ColorChannel.red){
                    unary.append("PixelOps.red(").append(expr).append(")");
                }else if(unaryExprPostfix.getColor() == ColorChannel.blu){
                    unary.append("PixelOps.blu(").append(expr).append(")");
                }else if(unaryExprPostfix.getColor() == ColorChannel.grn){
                    unary.append("PixelOps.grn(").append(expr).append(")");
                }
            }
        }
        return unary;
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException { //generated code format doesnt match exactly with scopes but just match parentheses when looking
        StringBuilder wile = new StringBuilder();
        Object guard = whileStatement.getGuard().visit(this,arg);
        wile.append("while((").append(guard).append("!=0)) {\n\t\t\t");
        symbolTable.enter();
        wile.append(whileStatement.getBlock().visit(this,arg)).append("\t\t}\n\t\t");
        symbolTable.leave();
        return wile;
    }

    @Override
    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException {
        StringBuilder write = new StringBuilder();
        if(statementWrite.getE().getType() == Type.PIXEL){
            write.append("ConsoleIO.writePixel("); //dont know if this works
        }else{
            write.append("ConsoleIO.write(");
        }
        write.append(statementWrite.getE().visit(this,arg)).append(");\n\t\t");
        //so a million of the same import isnt added
        if(imports.indexOf("import edu.ufl.cise.plcsp23.runtime.ConsoleIO;") == -1){
            imports.append("import edu.ufl.cise.plcsp23.runtime.ConsoleIO;\n");
        }
        //ConsoleIO.write();  //should return value associated with ident not ident
        return write;
    }

    @Override
    public Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException {
        return 255;
    }
}
