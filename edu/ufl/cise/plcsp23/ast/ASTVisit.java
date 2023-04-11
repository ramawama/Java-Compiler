package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.PLCException;
import edu.ufl.cise.plcsp23.TypeCheckException;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;


public class ASTVisit implements ASTVisitor{

    public static class SymbolTable { //leBlanc
        NameDef currDec;
        int current,next;
        Type progType;
        Stack<Integer> scope;
        HashMap<String,HashMap<Integer,NameDef>> table;
        public SymbolTable() {
            //constructor
            currDec = null;
            progType = null;
            current = 0;
            next = 1;
            scope = new Stack<>();
            scope.push(current);
            table = new HashMap<>();
        }
        public boolean insert(String name, NameDef nameDef){
            if(table.containsKey(name)){ //checks for scope as well
                if(!table.get(name).containsKey(scope.peek())){ // if the table doesnt contain name for current stack add
                    table.get(name).put(scope.peek(),nameDef);
                    return true;
                }
                else return false;
            }
            //else
            HashMap<Integer,NameDef> value = new HashMap<>();
            value.put(scope.peek(),nameDef);
            table.put(name,value);
            return true;
        }
        public NameDef lookup(String name){
            if(!table.containsKey(name)) return null;
            HashMap<Integer,NameDef> currScope = table.get(name);
            //scan chain—entries whose serial number is in the scope stack are visible.
            //Return entry with with serial number closest to the top of the scopestack.
            //If none, this is an error—the name is not bound in the current scope.
            int max = -1;
            NameDef ret = null;
            for (Integer scope: currScope.keySet()){
                //key iterator
                int temp = this.scope.indexOf(scope);
                if(this.scope.contains(scope) && temp > max){
                    max = temp;
                    ret = currScope.get(scope);
                }
            }
            return ret;
        }
        void enter(){
            current = next++;
            scope.push(current);
        }
        void leave(){
            current = scope.pop();
        }
    }

    SymbolTable symbolTable = new SymbolTable();

    private void check(boolean cond, String message) throws TypeCheckException {
        if(!cond) {
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
        symbolTable.currDec = nameDef;
        Expr init = declaration.getInitializer();
        if(nameDef.getType() == Type.IMAGE)
            check(init != null || nameDef.dimension != null , "Type image must have initializer or dimension");
        if(init != null){ //means = expr
            Type initType = (Type) init.visit(this,arg);

            check(compatible(nameDef.getType(), initType), "expression and declared types do not match");
            ///check(!symbolTable.sStack.peek().equals(init) || !nameDef.initialized,"Initializer cannot refer to name being defined" );

        }
        symbolTable.currDec = null;
        return null;
    }

    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        Expr e1 = dimension.getWidth();
        Expr e2 = dimension.getHeight();
        Type expr1 = (Type) dimension.getWidth().visit(this, arg);
        Type expr2 = (Type) dimension.getHeight().visit(this, arg);

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
        return ident.getDef().getType();
        //return null;
    }

    @Override
    public Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException {
        Type lVal = (Type) statementAssign.getLv().visit(this, arg);
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
        return unaryExprPostfix.getType();
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException {
        Type e = (Type) whileStatement.getGuard().visit(this, arg);
        check(e == Type.INT, "Type is not an int");
        symbolTable.enter();
        whileStatement.getBlock().visit(this, arg);
        symbolTable.leave();
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
        NameDef checker = symbolTable.lookup(identExpr.getName());
        check(!(checker == null),"Undefined variable");
        check(checker != symbolTable.currDec,"Cannot refer to object being initialized");
//        if(symbolTable.sStack.peek() != null){
//            check(!symbolTable.sStack.peek().equals(checker) || !checker.initialized,"Initializer cannot refer to name being defined" );
//        }
        return checker.getType();
    }

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        Ident ident = lValue.getIdent();
        PixelSelector pixel = lValue.getPixelSelector();
        ColorChannel color = lValue.getColor();
        Type result = null;
        NameDef def = symbolTable.lookup(ident.getName());

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
//        if(symbolTable.sStack.peek() != null)
//            check(!symbolTable.sStack.peek().equals(ident.getDef()), "Not visible in scope");
        check(symbolTable.table.containsKey(ident.getName()),"Ident has not been declared / not visible"); //change to lookuo
        return result;
        //return ident.def.getType();
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        check(!nameDef.getType().equals(Type.VOID),"Type cannot be void");
        if (nameDef.getDimension() != null){
            check(nameDef.getType().equals(Type.IMAGE),"Type must equal image if dimension is declared");
            visitDimension(nameDef.getDimension(),arg);
        }
        //System.out.println(nameDef.getType().name()+ " aw");
        check(symbolTable.insert(nameDef.getIdent().getName(),nameDef) , "Name already declared");
        //if false already present

        //symbolTable.sStack.push(nameDef);
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
        Type expr = (Type) returnStatement.getE().visit(this,arg);
        check(compatible(symbolTable.progType,expr), "Expr.type is not assignment compatible with program.type");
        return expr;
    }
}