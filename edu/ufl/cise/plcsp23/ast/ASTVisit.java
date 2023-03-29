package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.TypeCheckException;

import java.util.*;


public class ASTVisit implements ASTVisitor{

    public static class SymbolTable {
        private boolean startScope;
        static Type progType;
        private int currScope;
        private String name;
        private NameDef nameDef;
        Vector<NameDef> parameters = new Vector<>();
        Stack<Object> sStack = new Stack<>(); // for authentication in declaring and initializing in same expression @test8
        HashMap<String, NameDef> entries = new HashMap<>(); //changed to namedef
        Stack<HashMap<String, NameDef>> scopeMap = new Stack<>();

        public boolean insert(String name, NameDef namedef){
            this.name = name;
            //scopeMap.putIfAbsent(name, currScope);
            this.nameDef = namedef;
            if (currScope > 0){
//                for(NameDef names: parameters){
//                    if(names == namedef) return true;
//                }
                System.out.println(currScope);
                return (scopeMap.peek().putIfAbsent(name,namedef) == null);
            }


            return (entries.putIfAbsent(name, namedef)==null);
        }

        public NameDef lookup(String name) {
            if (currScope > 0){
                for(NameDef names: parameters){
                    System.out.println(names.getIdent().getName());
                    System.out.println(name);

                    if(names.getIdent().getName().equals(name)){
                        System.out.println("BALFUHS");
                        return names;
                    }
                }
                return scopeMap.peek().get(name);
            }
            return entries.get(name);
        }

        public void enterScope(){
            currScope++;
            System.out.println("currScope");
            System.out.println(currScope);

            startScope = true;
            scopeMap.push(new HashMap<>());

        }
        public void leaveScope(){
            //entries.remove(name);
            currScope--;
            System.out.println("-currScope");
            System.out.println(currScope);

            startScope = false;
            scopeMap.pop();
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
                (target == Type.STRING && rhs == Type.IMAGE) );
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException {
        Expr guard = conditionalExpr.getGuard();
        guard.setType((Type)guard.visit(this,arg));
        Expr trueCase = conditionalExpr.getTrueCase();
        trueCase.setType((Type) trueCase.visit(this,arg));
        Expr falseCase = conditionalExpr.getFalseCase();
        falseCase.setType((Type)falseCase.visit(this,arg));

        check(guard.getType() == Type.INT && trueCase.getType() == falseCase.getType(), "expr0 must be type int and expr1 & 2 types must match");
        return trueCase.type;
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException { //fix check!! - R
        NameDef nameDef = declaration.getNameDef();
        nameDef.visit(this,arg); //ensures nameDef is properly typed

        Expr init = declaration.getInitializer();
        if(nameDef.getType() == Type.IMAGE)
            check(init != null || nameDef.dimension != null , "Type image must have initializer or dimension");
        if(init != null){ //means = expr
            Type initType = (Type) init.visit(this,arg);

            /*System.out.println(nameDef.getType());
            System.out.println("init.firstToken.getTokenString()");
            System.out.println(init.toString());
            System.out.println(nameDef.ident.toString());*/
            check(compatible(nameDef.getType(), initType), "expression and declared types do not match");
            ///check(!symbolTable.sStack.peek().equals(init) || !nameDef.initialized,"Initializer cannot refer to name being defined" );

        }
        return null;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        Expr e1 = dimension.getWidth();
        Expr e2 = dimension.getHeight();
        Type expr1 = (Type) dimension.getWidth().visit(this, arg);
        Type expr2 = (Type) dimension.getHeight().visit(this, arg);
        //System.out.println(e1.firstToken.getTokenString());
        //System.out.println(e2.firstToken.getTokenString());
        check(expr1.equals(Type.INT),"Type should be int");
        check(expr2.equals(Type.INT),"Type should be int");
        return null;
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException {
        Type expr0 = (Type) expandedPixelExpr.getRedExpr().visit(this, arg);
        Type expr1 = (Type) expandedPixelExpr.getGrnExpr().visit(this, arg);
        Type expr2 = (Type) expandedPixelExpr.getBluExpr().visit(this, arg);
        check(expr0 == Type.INT && expr1 == Type.INT && expr2 == Type.INT,"Pixel Expr0 and Expr1 must be int");
        return Type.PIXEL;
    }

    @Override
    public Object visitIdent(Ident ident, Object arg) throws PLCException {
        //return ident.getDef().getType();
        System.out.println("AFHSUGHOVLBSV");
        return null;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException {
        Type lVal = (Type) statementAssign.getLv().visit(this, arg);
        //System.out.println("statementAssign.getE().toString()");
        //System.out.println(statementAssign.getE().toString());
        Type eType = ((Type) statementAssign.getE().visit(this, arg));
        check(compatible(lVal, eType), "incompatible types");
        return null;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        Type right = (Type) binaryExpr.getRight().visit(this, arg);
        Type left = (Type) binaryExpr.getLeft().visit(this, arg);


        switch(binaryExpr.op){
            case PLUS -> {
                if(left == right) {binaryExpr.setType(left);}
                else {check(false, "incompatible types");}
            }
            case MINUS -> {
                if(left == Type.INT && right == Type.INT) {binaryExpr.setType(Type.INT);}
                else if(left == Type.PIXEL && right == Type.PIXEL) {binaryExpr.setType(Type.PIXEL);}
                else if(left == Type.IMAGE && right == Type.IMAGE) {binaryExpr.setType(Type.IMAGE);}
                else{check(false, "incompatible types");}
            }
            case TIMES,DIV,MOD -> {
                if(left == Type.INT && right == Type.INT) {binaryExpr.setType(Type.INT);}
                else if(left == Type.PIXEL && right == Type.PIXEL) {binaryExpr.setType(Type.PIXEL);}
                else if(left == Type.IMAGE && right == Type.IMAGE) {binaryExpr.setType(Type.IMAGE);}
                else if(left == Type.PIXEL && right == Type.INT) {binaryExpr.setType(Type.PIXEL);}
                else if(left == Type.IMAGE && right == Type.INT) {binaryExpr.setType(Type.IMAGE);}
                else {check(false, "incompatible types");}
            }
            case EXP -> {
                if(left == Type.INT && right == Type.INT) {binaryExpr.setType(Type.INT);}
                else if(left == Type.PIXEL && right == Type.INT) {binaryExpr.setType(Type.PIXEL);}
                else {check(false, "incompatiblel types");}
            }
            case LT, GT, LE, GE, OR, AND -> {
                if(left == Type.INT && right == Type.INT) {binaryExpr.setType(Type.INT);}
                else {check(false, "incompatible types");}
            }
            case BITOR, BITAND -> {
                if(left == Type.PIXEL && right == Type.PIXEL) {binaryExpr.setType(Type.PIXEL);}
                else {check(false, "incompatible types");}
            }
            case EQ -> {
                if(left == right){binaryExpr.setType(Type.INT);}
                else {check(false, "incompatible types");}
            }
            default ->{
                throw new TypeCheckException("compiler error");
            }
        }
        return binaryExpr.getType();
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLCException {
        List<Declaration> decList = block.getDecList();
        for(Declaration dList: decList){
            dList.visit(this, arg);
        }
        System.out.println("me");
        symbolTable.sStack.push(null);
        List<Statement> stateList = block.getStatementList();
        for(Statement sList: stateList){
            sList.visit(this, arg);
        }
        return null;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {
        Expr e = unaryExpr.getE();
        e.setType((Type)e.visit(this, arg));
        Type result = null;
        switch(unaryExpr.op){
            case BANG ->{
                if(e.getType() == Type.INT) { result = Type.INT;}
                else if(e.getType() == Type.PIXEL) {result = Type.PIXEL;}
                else{check(false, "incompatible types");}
            }
            case MINUS, RES_cos, RES_sin, RES_atan -> {
                if(e.getType() == Type.INT) {result = Type.INT;}
                else{check(false, "incompatible types");}
            }
        }
        return result;
    }

    @Override
    public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException {
        Expr prim = unaryExprPostfix.getPrimary();
        PixelSelector pixel = unaryExprPostfix.getPixel();
        ColorChannel color = unaryExprPostfix.getColor();
        System.out.println("Image time x3?");
        prim.setType( (Type) prim.visit(this,arg));
        System.out.println("Image time x3?");
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
        //System.out.println("Image time x3?");
        return unaryExprPostfix.getType();
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException {
        symbolTable.enterScope();
        Type e = (Type) whileStatement.getGuard().visit(this, arg);
        check(e == Type.INT, "Type is not an int");
//        System.out.println(e.name());
//        System.out.println(whileStatement.guard.toString());
//        System.out.println(symbolTable.currScope);

        //System.out.println(symbolTable.currScope);
        whileStatement.getBlock().visit(this, arg);
        //System.out.println(symbolTable.currScope);
        symbolTable.leaveScope();
        //System.out.println(symbolTable.currScope);

        return null;
    }

    @Override
    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException {
        statementWrite.getE().visit(this, arg);
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
        PixelSelector pf = (PixelSelector) pixelFuncExpr.getSelector().visit(this, arg);
        return Type.INT;
    }

    @Override
    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException {
        return Type.INT;
    }

    @Override
    public Object visitProgram(Program program, Object arg) throws PLCException {
        symbolTable.progType = program.type;

        for (int i = 0; i < program.getParamList().size(); i++){ //visits namedef
            visitNameDef(program.getParamList().get(i),arg);
            symbolTable.parameters.add((NameDef)symbolTable.sStack.peek());
        }
//        Iterator hmIter = symbolTable.entries.entrySet().iterator();
//        while(hmIter.hasNext()){
//            Map.Entry mapElement = (Map.Entry)hmIter.next();
//            symbolTable.parameters.add((NameDef) mapElement.getValue());
//        }
        Block block = program.getBlock();
        block.visit(this,arg);

        return null;
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
        if(symbolTable.startScope == true){
            symbolTable.scopeMap.peek().put(identExpr.getName(),symbolTable.entries.get(identExpr.getName()));
            symbolTable.startScope = false;
        }
        NameDef checker = symbolTable.lookup(identExpr.getName());
        check(!(checker == null),"Undefined variable");
        /*System.out.println("symbolTable.sStack.peek(");
        System.out.println(symbolTable.sStack.peek());*/
        System.out.println(checker.getIdent().getName());

        if(symbolTable.sStack.peek() != null){
            check(!symbolTable.sStack.peek().equals(checker) || !checker.initialized,"Initializer cannot refer to name being defined" );
        }
            return checker.getType();
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        Ident ident = lValue.getIdent();
        PixelSelector pixel = lValue.getPixelSelector();
        ColorChannel color = lValue.getColor();
        Type result = null;
        NameDef def = symbolTable.lookup(ident.getName());
        System.out.println("def");
        //System.out.println("def");
        System.out.println(ident.getName());
        if(def.getType() == Type.IMAGE){
            if((pixel == null && color == null) || (pixel == null && color != null)){result = Type.IMAGE;}
            else if (pixel != null && color == null){result = Type.PIXEL;}
            else if (pixel != null && color != null){result = Type.INT;}
        }else if (def.getType() == Type.PIXEL){
            if(pixel == null && color == null){result = Type.PIXEL;}
            else if(pixel == null && color != null){result = Type.INT;}
        }else if(def.getType() == Type.STRING){
            if(pixel == null && color == null){result = Type.STRING;}
        }else if(def.getType() == Type.INT){
            if(pixel == null && color == null){result = Type.INT;}
        }
        if(symbolTable.sStack.peek() != null)
            check(!symbolTable.sStack.peek().equals(ident.getDef()), "Not visible in scope");
        check(symbolTable.entries.containsKey(ident.getName()),"Ident has not been declared / not visible");
        return result;
        //return ident.def.getType();
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        //System.out.println(nameDef.getIdent().getName());
        check(!nameDef.getType().equals(Type.VOID),"Type cannot be void");
        if (nameDef.getDimension() != null){
            //System.out.println(nameDef.getType());
            check(nameDef.getType().equals(Type.IMAGE),"Type must equal image if dimension is declared");
            visitDimension(nameDef.getDimension(),arg);
        }
        check(symbolTable.insert(nameDef.getIdent().getName(),nameDef) , "Name already declared");
        //if false already present

        //symbolTable.insert(nameDef.getIdent().getName(),nameDef);
        symbolTable.sStack.push(nameDef);
        return null;
    }

    @Override
    public Object visitZExpr(ZExpr constExpr, Object arg) throws PLCException {
        return Type.INT;
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        return Type.INT;
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException {
        System.out.println("MADE IT");
        //System.out.println(returnStatement.getE().toString());
        Type expr = (Type) returnStatement.getE().visit(this,arg);
        check(compatible(symbolTable.progType,expr), "Expr.type is not assignment compatible with program.type");
        return expr;
    }
}