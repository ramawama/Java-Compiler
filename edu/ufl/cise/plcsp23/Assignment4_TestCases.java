package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.ast.*;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static edu.ufl.cise.plcsp23.IToken.Kind.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
class Assignment4Tests{
	static final int TIMEOUT_MILLIS = 1000;
	/** Constructs a scanner and parser for the given input string, scans and parses the input and returns and AST.
	 *
	 * @param input   String representing program to be tested
	 * @return  AST representing the program
	 * @throws PLCException
	 */

	AST getAST(String input) throws PLCException {
		AST ast = CompilerComponentFactory.makeParser(input).parse();
		ASTVisitor typeChecker = CompilerComponentFactory.makeTypeChecker();
		ast.visit(typeChecker, null);
		return ast;
	}

	/**
	 * Checks that the given AST e has type NumLitExpr with the indicated value.  Returns the given AST cast to NumLitExpr.
	 *
	 * @param e
	 * @param value
	 * @return
	 */
	NumLitExpr checkNumLit(AST e, int value) {
		assertThat("",e, instanceOf( NumLitExpr.class));
		NumLitExpr ne = (NumLitExpr)e;
		assertEquals(value, ne.getValue());
		return ne;
	}

	/**
	 *  Checks that the given AST e has type StringLitExpr with the given String value.  Returns the given AST cast to StringLitExpr.
	 * @param e
	 * @param name
	 * @return
	 */
	StringLitExpr checkStringLit(AST e, String value) {
		assertThat("",e, instanceOf( StringLitExpr.class));
		StringLitExpr se = (StringLitExpr)e;
		assertEquals(value,se.getValue());
		return se;
	}

	/**
	 *  Checks that the given AST e has type UnaryExpr with the given operator.  Returns the given AST cast to UnaryExpr.
	 * @param e
	 * @param op  Kind of expected operator
	 * @return
	 */
	private UnaryExpr checkUnary(AST e, Kind op) {
		assertThat("",e, instanceOf( UnaryExpr.class));
		assertEquals(op, ((UnaryExpr)e).getOp());
		return (UnaryExpr)e;
	}


	/**
	 *  Checks that the given AST e has type ConditionalExpr.  Returns the given AST cast to ConditionalExpr.
	 * @param e
	 * @return
	 */
	private ConditionalExpr checkConditional(AST e) {
		assertThat("",e, instanceOf( ConditionalExpr.class));
		return (ConditionalExpr)e;
	}

	/**
	 *  Checks that the given AST e has type BinaryExpr with the given operator.  Returns the given AST cast to BinaryExpr.
	 *
	 * @param e
	 * @param op  Kind of expected operator
	 * @return
	 */
	BinaryExpr checkBinary(AST e, Kind expectedOp) {
		assertThat("",e, instanceOf(BinaryExpr.class));
		BinaryExpr be = (BinaryExpr)e;
		assertEquals(expectedOp, be.getOp());
		return be;
	}

	/**
	 * Checks that the given AST e has type IdentExpr with the given name.  Returns the given AST cast to IdentExpr.
	 * @param e
	 * @param name
	 * @return
	 */
	IdentExpr checkIdentExpr(AST e, String name) {
		assertThat("",e, instanceOf( IdentExpr.class));
		IdentExpr ident = (IdentExpr)e;
		assertEquals(name,ident.getName());
		return ident;
	}
	/**
	 * Checks that the given AST e has type Ident with the given name.  Returns the given AST cast to IdentExpr.
	 * @param e
	 * @param name
	 * @return
	 */
	Ident checkIdent(AST e, String name) {
		assertThat("",e, instanceOf( Ident.class));
		Ident ident = (Ident)e;
		assertEquals(name,ident.getName());
		return ident;
	}

	NameDef checkNameDef(AST d , String name, Type type) {
		assertThat("", d, instanceOf(NameDef.class));
		NameDef def = (NameDef)d;
		assertEquals(name, def.getIdent().getName());
		assertEquals(type, def.getType());
		return def;
	}
	@Test
	void test0() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f(){}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(0, v6);
			List<Statement> v7 = v4.getStatementList();
			int v8= v7.size();
			assertEquals(0, v8);
		});
	}

	@Test
	void test1() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				int f(int xx, string ss, image ii, pixel p){}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(4, v3);
			NameDef v4 = v2.get(0);
			assertThat("",v4,instanceOf(NameDef.class));
			checkNameDef(v4,"xx",Type.INT);
			assertNull(v4.getDimension());
			NameDef v5 = v2.get(1);
			assertThat("",v5,instanceOf(NameDef.class));
			checkNameDef(v5,"ss",Type.STRING);
			assertNull(v5.getDimension());
			NameDef v6 = v2.get(2);
			assertThat("",v6,instanceOf(NameDef.class));
			checkNameDef(v6,"ii",Type.IMAGE);
			assertNull(v6.getDimension());
			NameDef v7 = v2.get(3);
			assertThat("",v7,instanceOf(NameDef.class));
			checkNameDef(v7,"p",Type.PIXEL);
			assertNull(v7.getDimension());
			Block v8 = v0.getBlock();
			assertThat("",v8,instanceOf(Block.class));
			List<Declaration> v9 = v8.getDecList();
			int v10= v9.size();
			assertEquals(0, v10);
			List<Statement> v11 = v8.getStatementList();
			int v12= v11.size();
			assertEquals(0, v12);
		});
	}

	@Test
	void test2() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				string f(int f){}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(1, v3);
			NameDef v4 = v2.get(0);
			assertThat("",v4,instanceOf(NameDef.class));
			checkNameDef(v4,"f",Type.INT);
			assertNull(v4.getDimension());
			Block v5 = v0.getBlock();
			assertThat("",v5,instanceOf(Block.class));
			List<Declaration> v6 = v5.getDecList();
			int v7= v6.size();
			assertEquals(0, v7);
			List<Statement> v8 = v5.getStatementList();
			int v9= v8.size();
			assertEquals(0, v9);
		});
	}

	@Test
	void test3() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				string f(int f, string s){
				  int g.
				  string ss.
				   }
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(2, v3);
			NameDef v4 = v2.get(0);
			assertThat("",v4,instanceOf(NameDef.class));
			checkNameDef(v4,"f",Type.INT);
			assertNull(v4.getDimension());
			NameDef v5 = v2.get(1);
			assertThat("",v5,instanceOf(NameDef.class));
			checkNameDef(v5,"s",Type.STRING);
			assertNull(v5.getDimension());
			Block v6 = v0.getBlock();
			assertThat("",v6,instanceOf(Block.class));
			List<Declaration> v7 = v6.getDecList();
			int v8= v7.size();
			assertEquals(2, v8);
			Declaration v9 = v7.get(0);
			assertThat("",v9,instanceOf(Declaration.class));
			NameDef v10 = v9.getNameDef();
			assertThat("",v10,instanceOf(NameDef.class));
			checkNameDef(v10,"g",Type.INT);
			assertNull(v10.getDimension());
			assertNull(v9.getInitializer());
			Declaration v11 = v7.get(1);
			assertThat("",v11,instanceOf(Declaration.class));
			NameDef v12 = v11.getNameDef();
			assertThat("",v12,instanceOf(NameDef.class));
			checkNameDef(v12,"ss",Type.STRING);
			assertNull(v12.getDimension());
			assertNull(v11.getInitializer());
			List<Statement> v13 = v6.getStatementList();
			int v14= v13.size();
			assertEquals(0, v14);
		});
	}

	@Test
	void test4() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				int f(){
				:3.
				}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(0, v6);
			List<Statement> v7 = v4.getStatementList();
			int v8= v7.size();
			assertEquals(1, v8);
			Statement v9 = v7.get(0);
			assertThat("",v9,instanceOf(ReturnStatement.class));
			Expr v10 = ((ReturnStatement)v9).getE();
			checkNumLit(v10,3);
			assertEquals(Type.INT, v10.getType());
		});
	}

	@Test
	void test5() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				image f(){
				image i = "url".
				image j = i.
				int rr = j[3,4]:red.
				}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(3, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"i",Type.IMAGE);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkStringLit(v9,"url");assertEquals(Type.STRING, v9.getType());
			Declaration v10 = v5.get(1);
			assertThat("",v10,instanceOf(Declaration.class));
			NameDef v11 = v10.getNameDef();
			assertThat("",v11,instanceOf(NameDef.class));
			checkNameDef(v11,"j",Type.IMAGE);
			assertNull(v11.getDimension());
			Expr v12 = v10.getInitializer();
			checkIdentExpr(v12,"i");
			assertEquals(Type.IMAGE, v12.getType());
			Declaration v13 = v5.get(2);
			assertThat("",v13,instanceOf(Declaration.class));
			NameDef v14 = v13.getNameDef();
			assertThat("",v14,instanceOf(NameDef.class));
			checkNameDef(v14,"rr",Type.INT);
			assertNull(v14.getDimension());
			Expr v15 = v13.getInitializer();
			assertThat("",v15,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.INT, v15.getType());
			Expr v16 = ((UnaryExprPostfix)v15).getPrimary();
			checkIdentExpr(v16,"j");
			assertEquals(Type.IMAGE, v16.getType());
			PixelSelector v17 = ((UnaryExprPostfix)v15).getPixel();
			assertThat("",v17,instanceOf(PixelSelector.class));
			Expr v18 = ((PixelSelector)v17).getX();
			checkNumLit(v18,3);
			assertEquals(Type.INT, v18.getType());
			Expr v19 = ((PixelSelector)v17).getY();
			checkNumLit(v19,4);
			assertEquals(Type.INT, v19.getType());
			assertEquals( ColorChannel.red,((UnaryExprPostfix)v15).getColor());
			List<Statement> v20 = v4.getStatementList();
			int v21= v20.size();
			assertEquals(0, v21);
		});
	}

	@Test
	void test6() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				image f(){
				image i = "url".
				i[x,y] = [x,y,0].
				}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(1, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"i",Type.IMAGE);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkStringLit(v9,"url");assertEquals(Type.STRING, v9.getType());
			List<Statement> v10 = v4.getStatementList();
			int v11= v10.size();
			assertEquals(1, v11);
			Statement v12 = v10.get(0);
			assertThat("",v12,instanceOf(AssignmentStatement.class));
			LValue v13 = ((AssignmentStatement)v12).getLv();
			assertThat("",v13,instanceOf(LValue.class));
			Ident v14 = v13.getIdent();
			checkIdent(v14,"i");
			PixelSelector v15 = v13.getPixelSelector();
			assertThat("",v15,instanceOf(PixelSelector.class));
			Expr v16 = ((PixelSelector)v15).getX();
			assertThat("",v16,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v16.getType());
			assertEquals(RES_x,((PredeclaredVarExpr)v16).getKind());
			Expr v17 = ((PixelSelector)v15).getY();
			assertThat("",v17,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v17.getType());
			assertEquals(RES_y,((PredeclaredVarExpr)v17).getKind());
			assertNull(((LValue)v13).getColor());
			Expr v18 = ((AssignmentStatement)v12).getE();
			assertThat("",v18,instanceOf(ExpandedPixelExpr.class));
			assertEquals(Type.PIXEL, v18.getType());
			Expr v19 = ((ExpandedPixelExpr)v18).getRedExpr();
			assertThat("",v19,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v19.getType());
			assertEquals(RES_x,((PredeclaredVarExpr)v19).getKind());
			Expr v20 = ((ExpandedPixelExpr)v18).getGrnExpr();
			assertThat("",v20,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v20.getType());
			assertEquals(RES_y,((PredeclaredVarExpr)v20).getKind());
			Expr v21 = ((ExpandedPixelExpr)v18).getBluExpr();
			checkNumLit(v21,0);
			assertEquals(Type.INT, v21.getType());
		});
	}

	@Test
	void test7() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f(string url){
				image[300,400] i = url.
				image[300,400] j. 
				j[a,r] = i[a_polar[y,x], r_polar[x,y]].
				write j.
				}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(1, v3);
			NameDef v4 = v2.get(0);
			assertThat("",v4,instanceOf(NameDef.class));
			checkNameDef(v4,"url",Type.STRING);
			assertNull(v4.getDimension());
			Block v5 = v0.getBlock();
			assertThat("",v5,instanceOf(Block.class));
			List<Declaration> v6 = v5.getDecList();
			int v7= v6.size();
			assertEquals(2, v7);
			Declaration v8 = v6.get(0);
			assertThat("",v8,instanceOf(Declaration.class));
			NameDef v9 = v8.getNameDef();
			assertThat("",v9,instanceOf(NameDef.class));
			checkNameDef(v9,"i",Type.IMAGE);
			Dimension v10 = ((NameDef)v9).getDimension();
			assertThat("",v10,instanceOf(Dimension.class));
			Expr v11 = ((Dimension)v10).getWidth();
			checkNumLit(v11,300);
			assertEquals(Type.INT, v11.getType());
			Expr v12 = ((Dimension)v10).getHeight();
			checkNumLit(v12,400);
			assertEquals(Type.INT, v12.getType());
			Expr v13 = v8.getInitializer();
			checkIdentExpr(v13,"url");
			assertEquals(Type.STRING, v13.getType());
			Declaration v14 = v6.get(1);
			assertThat("",v14,instanceOf(Declaration.class));
			NameDef v15 = v14.getNameDef();
			assertThat("",v15,instanceOf(NameDef.class));
			checkNameDef(v15,"j",Type.IMAGE);
			Dimension v16 = ((NameDef)v15).getDimension();
			assertThat("",v16,instanceOf(Dimension.class));
			Expr v17 = ((Dimension)v16).getWidth();
			checkNumLit(v17,300);
			assertEquals(Type.INT, v17.getType());
			Expr v18 = ((Dimension)v16).getHeight();
			checkNumLit(v18,400);
			assertEquals(Type.INT, v18.getType());
			assertNull(v14.getInitializer());
			List<Statement> v19 = v5.getStatementList();
			int v20= v19.size();
			assertEquals(2, v20);
			Statement v21 = v19.get(0);
			assertThat("",v21,instanceOf(AssignmentStatement.class));
			LValue v22 = ((AssignmentStatement)v21).getLv();
			assertThat("",v22,instanceOf(LValue.class));
			Ident v23 = v22.getIdent();
			checkIdent(v23,"j");
			PixelSelector v24 = v22.getPixelSelector();
			assertThat("",v24,instanceOf(PixelSelector.class));
			Expr v25 = ((PixelSelector)v24).getX();
			assertThat("",v25,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v25.getType());
			assertEquals(RES_a,((PredeclaredVarExpr)v25).getKind());
			Expr v26 = ((PixelSelector)v24).getY();
			assertThat("",v26,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v26.getType());
			assertEquals(RES_r,((PredeclaredVarExpr)v26).getKind());
			assertNull(((LValue)v22).getColor());
			Expr v27 = ((AssignmentStatement)v21).getE();
			assertThat("",v27,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v27.getType());
			Expr v28 = ((UnaryExprPostfix)v27).getPrimary();
			checkIdentExpr(v28,"i");
			assertEquals(Type.IMAGE, v28.getType());
			PixelSelector v29 = ((UnaryExprPostfix)v27).getPixel();
			assertThat("",v29,instanceOf(PixelSelector.class));
			Expr v30 = ((PixelSelector)v29).getX();
			assertThat("",v30,instanceOf(PixelFuncExpr.class));
			assertEquals(Type.INT, v30.getType());
			assertEquals(RES_a_polar,((PixelFuncExpr)v30).getFunction());
			PixelSelector v31 = ((PixelFuncExpr)v30).getSelector();
			assertThat("",v31,instanceOf(PixelSelector.class));
			Expr v32 = ((PixelSelector)v31).getX();
			assertThat("",v32,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v32.getType());
			assertEquals(RES_y,((PredeclaredVarExpr)v32).getKind());
			Expr v33 = ((PixelSelector)v31).getY();
			assertThat("",v33,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v33.getType());
			assertEquals(RES_x,((PredeclaredVarExpr)v33).getKind());
			Expr v34 = ((PixelSelector)v29).getY();
			assertThat("",v34,instanceOf(PixelFuncExpr.class));
			assertEquals(Type.INT, v34.getType());
			assertEquals(RES_r_polar,((PixelFuncExpr)v34).getFunction());
			PixelSelector v35 = ((PixelFuncExpr)v34).getSelector();
			assertThat("",v35,instanceOf(PixelSelector.class));
			Expr v36 = ((PixelSelector)v35).getX();
			assertThat("",v36,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v36.getType());
			assertEquals(RES_x,((PredeclaredVarExpr)v36).getKind());
			Expr v37 = ((PixelSelector)v35).getY();
			assertThat("",v37,instanceOf(PredeclaredVarExpr.class));
			assertEquals(Type.INT, v37.getType());
			assertEquals(RES_y,((PredeclaredVarExpr)v37).getKind());
			assertNull(((UnaryExprPostfix)v27).getColor());
			Statement v38 = v19.get(1);
			assertThat("",v38,instanceOf(WriteStatement.class));
			Expr v39 = ((WriteStatement)v38).getE();
			checkIdentExpr(v39,"j");
			assertEquals(Type.IMAGE, v39.getType());
		});
	}

	@Test
	void test8() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
string s(string s0, string s1, int ok){
				:if ok ? s0 ? s0 + s1 .
				}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"s");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(3, v3);
			NameDef v4 = v2.get(0);
			assertThat("",v4,instanceOf(NameDef.class));
			checkNameDef(v4,"s0",Type.STRING);
			assertNull(v4.getDimension());
			NameDef v5 = v2.get(1);
			assertThat("",v5,instanceOf(NameDef.class));
			checkNameDef(v5,"s1",Type.STRING);
			assertNull(v5.getDimension());
			NameDef v6 = v2.get(2);
			assertThat("",v6,instanceOf(NameDef.class));
			checkNameDef(v6,"ok",Type.INT);
			assertNull(v6.getDimension());
			Block v7 = v0.getBlock();
			assertThat("",v7,instanceOf(Block.class));
			List<Declaration> v8 = v7.getDecList();
			int v9= v8.size();
			assertEquals(0, v9);
			List<Statement> v10 = v7.getStatementList();
			int v11= v10.size();
			assertEquals(1, v11);
			Statement v12 = v10.get(0);
			assertThat("",v12,instanceOf(ReturnStatement.class));
			Expr v13 = ((ReturnStatement)v12).getE();
			checkConditional(v13);
			assertEquals(Type.STRING, v13.getType());
			Expr v14 = ((ConditionalExpr)v13).getGuard();
			checkIdentExpr(v14,"ok");
			assertEquals(Type.INT, v14.getType());
			Expr v15 = ((ConditionalExpr)v13).getTrueCase();
			checkIdentExpr(v15,"s0");
			assertEquals(Type.STRING, v15.getType());
			Expr v16 = ((ConditionalExpr)v13).getFalseCase();

			checkBinary(v16,Kind.PLUS);
			assertEquals(Type.STRING, v16.getType());
			Expr v17 = ((BinaryExpr)v16).getLeft();
			checkIdentExpr(v17,"s0");
			assertEquals(Type.STRING, v17.getType());
			Expr v18 = ((BinaryExpr)v16).getRight();
			checkIdentExpr(v18,"s1");
			assertEquals(Type.STRING, v18.getType());
		});
	}

	@Test
	void test9() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				int f(int xx){
				: Z/2 + xx.
				}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(1, v3);
			NameDef v4 = v2.get(0);
			assertThat("",v4,instanceOf(NameDef.class));
			checkNameDef(v4,"xx",Type.INT);
			assertNull(v4.getDimension());
			Block v5 = v0.getBlock();
			assertThat("",v5,instanceOf(Block.class));
			List<Declaration> v6 = v5.getDecList();
			int v7= v6.size();
			assertEquals(0, v7);
			List<Statement> v8 = v5.getStatementList();
			int v9= v8.size();
			assertEquals(1, v9);
			Statement v10 = v8.get(0);
			assertThat("",v10,instanceOf(ReturnStatement.class));
			Expr v11 = ((ReturnStatement)v10).getE();

			checkBinary(v11,Kind.PLUS);
			assertEquals(Type.INT, v11.getType());
			Expr v12 = ((BinaryExpr)v11).getLeft();

			checkBinary(v12,Kind.DIV);
			assertEquals(Type.INT, v12.getType());
			Expr v13 = ((BinaryExpr)v12).getLeft();
			assertThat("",v13,instanceOf(ZExpr.class));assertEquals(Type.INT, v13.getType());
			Expr v14 = ((BinaryExpr)v12).getRight();
			checkNumLit(v14,2);
			assertEquals(Type.INT, v14.getType());
			Expr v15 = ((BinaryExpr)v11).getRight();
			checkIdentExpr(v15,"xx");
			assertEquals(Type.INT, v15.getType());
		});
	}

	@Test
	void test10() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				int f(int xx){
				int i = 3.
				while i > 0 {
				   write xx.
				   i = i -1.
				}.
				: i.
				}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(1, v3);
			NameDef v4 = v2.get(0);
			assertThat("",v4,instanceOf(NameDef.class));
			checkNameDef(v4,"xx",Type.INT);
			assertNull(v4.getDimension());
			Block v5 = v0.getBlock();
			assertThat("",v5,instanceOf(Block.class));
			List<Declaration> v6 = v5.getDecList();
			int v7= v6.size();
			assertEquals(1, v7);
			Declaration v8 = v6.get(0);
			assertThat("",v8,instanceOf(Declaration.class));
			NameDef v9 = v8.getNameDef();
			assertThat("",v9,instanceOf(NameDef.class));
			checkNameDef(v9,"i",Type.INT);
			assertNull(v9.getDimension());
			Expr v10 = v8.getInitializer();
			checkNumLit(v10,3);
			assertEquals(Type.INT, v10.getType());
			List<Statement> v11 = v5.getStatementList();
			int v12= v11.size();
			assertEquals(2, v12);
			Statement v13 = v11.get(0);
			assertThat("",v13,instanceOf(WhileStatement.class));
			Expr v14 = ((WhileStatement)v13).getGuard();

			checkBinary(v14,Kind.GT);
			assertEquals(Type.INT, v14.getType());
			Expr v15 = ((BinaryExpr)v14).getLeft();
			checkIdentExpr(v15,"i");
			assertEquals(Type.INT, v15.getType());
			Expr v16 = ((BinaryExpr)v14).getRight();
			checkNumLit(v16,0);
			assertEquals(Type.INT, v16.getType());
			Block v17 = ((WhileStatement)v13).getBlock();
			assertThat("",v17,instanceOf(Block.class));
			List<Declaration> v18 = v17.getDecList();
			int v19= v18.size();
			assertEquals(0, v19);
			List<Statement> v20 = v17.getStatementList();
			int v21= v20.size();
			assertEquals(2, v21);
			Statement v22 = v20.get(0);
			assertThat("",v22,instanceOf(WriteStatement.class));
			Expr v23 = ((WriteStatement)v22).getE();
			checkIdentExpr(v23,"xx");
			assertEquals(Type.INT, v23.getType());
			Statement v24 = v20.get(1);
			assertThat("",v24,instanceOf(AssignmentStatement.class));
			LValue v25 = ((AssignmentStatement)v24).getLv();
			assertThat("",v25,instanceOf(LValue.class));
			Ident v26 = v25.getIdent();
			checkIdent(v26,"i");
			assertNull(v25.getPixelSelector());
			assertNull(((LValue)v25).getColor());
			Expr v27 = ((AssignmentStatement)v24).getE();

			checkBinary(v27,Kind.MINUS);
			assertEquals(Type.INT, v27.getType());
			Expr v28 = ((BinaryExpr)v27).getLeft();
			checkIdentExpr(v28,"i");
			assertEquals(Type.INT, v28.getType());
			Expr v29 = ((BinaryExpr)v27).getRight();
			checkNumLit(v29,1);
			assertEquals(Type.INT, v29.getType());
			Statement v30 = v11.get(1);
			assertThat("",v30,instanceOf(ReturnStatement.class));
			Expr v31 = ((ReturnStatement)v30).getE();
			checkIdentExpr(v31,"i");
			assertEquals(Type.INT, v31.getType());
		});
	}

	@Test
	void test11() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				int f(int xx){
				int i = 3.
				while i > 0 {
				   string xx = "hello".
				   write xx.
				   i = i -1.
				}.
				i = 3.
				while i > 0 {
				   image xx = "url".
				   write xx.
				   i = i -1.
				}.
				: i.
				}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(1, v3);
			NameDef v4 = v2.get(0);
			assertThat("",v4,instanceOf(NameDef.class));
			checkNameDef(v4,"xx",Type.INT);
			assertNull(v4.getDimension());
			Block v5 = v0.getBlock();
			assertThat("",v5,instanceOf(Block.class));
			List<Declaration> v6 = v5.getDecList();
			int v7= v6.size();
			assertEquals(1, v7);
			Declaration v8 = v6.get(0);
			assertThat("",v8,instanceOf(Declaration.class));
			NameDef v9 = v8.getNameDef();
			assertThat("",v9,instanceOf(NameDef.class));
			checkNameDef(v9,"i",Type.INT);
			assertNull(v9.getDimension());
			Expr v10 = v8.getInitializer();
			checkNumLit(v10,3);
			assertEquals(Type.INT, v10.getType());
			List<Statement> v11 = v5.getStatementList();
			int v12= v11.size();
			assertEquals(4, v12);
			Statement v13 = v11.get(0);
			assertThat("",v13,instanceOf(WhileStatement.class));
			Expr v14 = ((WhileStatement)v13).getGuard();

			checkBinary(v14,Kind.GT);
			assertEquals(Type.INT, v14.getType());
			Expr v15 = ((BinaryExpr)v14).getLeft();
			checkIdentExpr(v15,"i");
			assertEquals(Type.INT, v15.getType());
			Expr v16 = ((BinaryExpr)v14).getRight();
			checkNumLit(v16,0);
			assertEquals(Type.INT, v16.getType());
			Block v17 = ((WhileStatement)v13).getBlock();
			assertThat("",v17,instanceOf(Block.class));
			List<Declaration> v18 = v17.getDecList();
			int v19= v18.size();
			assertEquals(1, v19);
			Declaration v20 = v18.get(0);
			assertThat("",v20,instanceOf(Declaration.class));
			NameDef v21 = v20.getNameDef();
			assertThat("",v21,instanceOf(NameDef.class));
			checkNameDef(v21,"xx",Type.STRING);
			assertNull(v21.getDimension());
			Expr v22 = v20.getInitializer();
			checkStringLit(v22,"hello");assertEquals(Type.STRING, v22.getType());
			List<Statement> v23 = v17.getStatementList();
			int v24= v23.size();
			assertEquals(2, v24);
			Statement v25 = v23.get(0);
			assertThat("",v25,instanceOf(WriteStatement.class));
			Expr v26 = ((WriteStatement)v25).getE();
			checkIdentExpr(v26,"xx");
			assertEquals(Type.STRING, v26.getType());
			Statement v27 = v23.get(1);
			assertThat("",v27,instanceOf(AssignmentStatement.class));
			LValue v28 = ((AssignmentStatement)v27).getLv();
			assertThat("",v28,instanceOf(LValue.class));
			Ident v29 = v28.getIdent();
			checkIdent(v29,"i");
			assertNull(v28.getPixelSelector());
			assertNull(((LValue)v28).getColor());
			Expr v30 = ((AssignmentStatement)v27).getE();

			checkBinary(v30,Kind.MINUS);
			assertEquals(Type.INT, v30.getType());
			Expr v31 = ((BinaryExpr)v30).getLeft();
			checkIdentExpr(v31,"i");
			assertEquals(Type.INT, v31.getType());
			Expr v32 = ((BinaryExpr)v30).getRight();
			checkNumLit(v32,1);
			assertEquals(Type.INT, v32.getType());
			Statement v33 = v11.get(1);
			assertThat("",v33,instanceOf(AssignmentStatement.class));
			LValue v34 = ((AssignmentStatement)v33).getLv();
			assertThat("",v34,instanceOf(LValue.class));
			Ident v35 = v34.getIdent();
			checkIdent(v35,"i");
			assertNull(v34.getPixelSelector());
			assertNull(((LValue)v34).getColor());
			Expr v36 = ((AssignmentStatement)v33).getE();
			checkNumLit(v36,3);
			assertEquals(Type.INT, v36.getType());
			Statement v37 = v11.get(2);
			assertThat("",v37,instanceOf(WhileStatement.class));
			Expr v38 = ((WhileStatement)v37).getGuard();

			checkBinary(v38,Kind.GT);
			assertEquals(Type.INT, v38.getType());
			Expr v39 = ((BinaryExpr)v38).getLeft();
			checkIdentExpr(v39,"i");
			assertEquals(Type.INT, v39.getType());
			Expr v40 = ((BinaryExpr)v38).getRight();
			checkNumLit(v40,0);
			assertEquals(Type.INT, v40.getType());
			Block v41 = ((WhileStatement)v37).getBlock();
			assertThat("",v41,instanceOf(Block.class));
			List<Declaration> v42 = v41.getDecList();
			int v43= v42.size();
			assertEquals(1, v43);
			Declaration v44 = v42.get(0);
			assertThat("",v44,instanceOf(Declaration.class));
			NameDef v45 = v44.getNameDef();
			assertThat("",v45,instanceOf(NameDef.class));
			checkNameDef(v45,"xx",Type.IMAGE);
			assertNull(v45.getDimension());
			Expr v46 = v44.getInitializer();
			checkStringLit(v46,"url");assertEquals(Type.STRING, v46.getType());
			List<Statement> v47 = v41.getStatementList();
			int v48= v47.size();
			assertEquals(2, v48);
			Statement v49 = v47.get(0);
			assertThat("",v49,instanceOf(WriteStatement.class));
			Expr v50 = ((WriteStatement)v49).getE();
			checkIdentExpr(v50,"xx");
			assertEquals(Type.IMAGE, v50.getType());
			Statement v51 = v47.get(1);
			assertThat("",v51,instanceOf(AssignmentStatement.class));
			LValue v52 = ((AssignmentStatement)v51).getLv();
			assertThat("",v52,instanceOf(LValue.class));
			Ident v53 = v52.getIdent();
			checkIdent(v53,"i");
			assertNull(v52.getPixelSelector());
			assertNull(((LValue)v52).getColor());
			Expr v54 = ((AssignmentStatement)v51).getE();

			checkBinary(v54,Kind.MINUS);
			assertEquals(Type.INT, v54.getType());
			Expr v55 = ((BinaryExpr)v54).getLeft();
			checkIdentExpr(v55,"i");
			assertEquals(Type.INT, v55.getType());
			Expr v56 = ((BinaryExpr)v54).getRight();
			checkNumLit(v56,1);
			assertEquals(Type.INT, v56.getType());
			Statement v57 = v11.get(3);
			assertThat("",v57,instanceOf(ReturnStatement.class));
			Expr v58 = ((ReturnStatement)v57).getE();
			checkIdentExpr(v58,"i");
			assertEquals(Type.INT, v58.getType());
		});
	}

	@Test
	void test12() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    int xx = 2.
    : -xx.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(1, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"xx",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkNumLit(v9,2);
			assertEquals(Type.INT, v9.getType());
			List<Statement> v10 = v4.getStatementList();
			int v11= v10.size();
			assertEquals(1, v11);
			Statement v12 = v10.get(0);
			assertThat("",v12,instanceOf(ReturnStatement.class));
			Expr v13 = ((ReturnStatement)v12).getE();
			checkUnary(v13,Kind.MINUS);
			assertEquals(Type.INT, v13.getType());
			Expr v14 = ((UnaryExpr)v13).getE();
			checkIdentExpr(v14,"xx");
			assertEquals(Type.INT, v14.getType());
		});
	}

	@Test
	void test13() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    int xx = 2.
    : cos xx.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(1, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"xx",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkNumLit(v9,2);
			assertEquals(Type.INT, v9.getType());
			List<Statement> v10 = v4.getStatementList();
			int v11= v10.size();
			assertEquals(1, v11);
			Statement v12 = v10.get(0);
			assertThat("",v12,instanceOf(ReturnStatement.class));
			Expr v13 = ((ReturnStatement)v12).getE();
			checkUnary(v13,Kind.RES_cos);
			assertEquals(Type.INT, v13.getType());
			Expr v14 = ((UnaryExpr)v13).getE();
			checkIdentExpr(v14,"xx");
			assertEquals(Type.INT, v14.getType());
		});
	}

	@Test
	void test14() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    int xx = 2.
    : sin xx.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(1, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"xx",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkNumLit(v9,2);
			assertEquals(Type.INT, v9.getType());
			List<Statement> v10 = v4.getStatementList();
			int v11= v10.size();
			assertEquals(1, v11);
			Statement v12 = v10.get(0);
			assertThat("",v12,instanceOf(ReturnStatement.class));
			Expr v13 = ((ReturnStatement)v12).getE();
			checkUnary(v13,Kind.RES_sin);
			assertEquals(Type.INT, v13.getType());
			Expr v14 = ((UnaryExpr)v13).getE();
			checkIdentExpr(v14,"xx");
			assertEquals(Type.INT, v14.getType());
		});
	}

	@Test
	void test15() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    int xx = 2.
    : atan xx.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(1, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"xx",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkNumLit(v9,2);
			assertEquals(Type.INT, v9.getType());
			List<Statement> v10 = v4.getStatementList();
			int v11= v10.size();
			assertEquals(1, v11);
			Statement v12 = v10.get(0);
			assertThat("",v12,instanceOf(ReturnStatement.class));
			Expr v13 = ((ReturnStatement)v12).getE();
			checkUnary(v13,Kind.RES_atan);
			assertEquals(Type.INT, v13.getType());
			Expr v14 = ((UnaryExpr)v13).getE();
			checkIdentExpr(v14,"xx");
			assertEquals(Type.INT, v14.getType());
		});
	}

	@Test
	void test16() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    int xx = 2.
    image[200,200] ab.
    ab[100,100] = !ab[100,100].
    : ! xx.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(2, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"xx",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkNumLit(v9,2);
			assertEquals(Type.INT, v9.getType());
			Declaration v10 = v5.get(1);
			assertThat("",v10,instanceOf(Declaration.class));
			NameDef v11 = v10.getNameDef();
			assertThat("",v11,instanceOf(NameDef.class));
			checkNameDef(v11,"ab",Type.IMAGE);
			Dimension v12 = ((NameDef)v11).getDimension();
			assertThat("",v12,instanceOf(Dimension.class));
			Expr v13 = ((Dimension)v12).getWidth();
			checkNumLit(v13,200);
			assertEquals(Type.INT, v13.getType());
			Expr v14 = ((Dimension)v12).getHeight();
			checkNumLit(v14,200);
			assertEquals(Type.INT, v14.getType());
			assertNull(v10.getInitializer());
			List<Statement> v15 = v4.getStatementList();
			int v16= v15.size();
			assertEquals(2, v16);
			Statement v17 = v15.get(0);
			assertThat("",v17,instanceOf(AssignmentStatement.class));
			LValue v18 = ((AssignmentStatement)v17).getLv();
			assertThat("",v18,instanceOf(LValue.class));
			Ident v19 = v18.getIdent();
			checkIdent(v19,"ab");
			PixelSelector v20 = v18.getPixelSelector();
			assertThat("",v20,instanceOf(PixelSelector.class));
			Expr v21 = ((PixelSelector)v20).getX();
			checkNumLit(v21,100);
			assertEquals(Type.INT, v21.getType());
			Expr v22 = ((PixelSelector)v20).getY();
			checkNumLit(v22,100);
			assertEquals(Type.INT, v22.getType());
			assertNull(((LValue)v18).getColor());
			Expr v23 = ((AssignmentStatement)v17).getE();
			checkUnary(v23,Kind.BANG);
			assertEquals(Type.PIXEL, v23.getType());
			Expr v24 = ((UnaryExpr)v23).getE();
			assertThat("",v24,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v24.getType());
			Expr v25 = ((UnaryExprPostfix)v24).getPrimary();
			checkIdentExpr(v25,"ab");
			assertEquals(Type.IMAGE, v25.getType());
			PixelSelector v26 = ((UnaryExprPostfix)v24).getPixel();
			assertThat("",v26,instanceOf(PixelSelector.class));
			Expr v27 = ((PixelSelector)v26).getX();
			checkNumLit(v27,100);
			assertEquals(Type.INT, v27.getType());
			Expr v28 = ((PixelSelector)v26).getY();
			checkNumLit(v28,100);
			assertEquals(Type.INT, v28.getType());
			assertNull(((UnaryExprPostfix)v24).getColor());
			Statement v29 = v15.get(1);
			assertThat("",v29,instanceOf(ReturnStatement.class));
			Expr v30 = ((ReturnStatement)v29).getE();
			checkUnary(v30,Kind.BANG);
			assertEquals(Type.INT, v30.getType());
			Expr v31 = ((UnaryExpr)v30).getE();
			checkIdentExpr(v31,"xx");
			assertEquals(Type.INT, v31.getType());
		});
	}

	@Test
	void test17() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    int xx = rand.
    : xx.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(1, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"xx",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			assertThat("",v9,instanceOf(RandomExpr.class));assertEquals(Type.INT, v9.getType());
			List<Statement> v10 = v4.getStatementList();
			int v11= v10.size();
			assertEquals(1, v11);
			Statement v12 = v10.get(0);
			assertThat("",v12,instanceOf(ReturnStatement.class));
			Expr v13 = ((ReturnStatement)v12).getE();
			checkIdentExpr(v13,"xx");
			assertEquals(Type.INT, v13.getType());
		});
	}

	@Test
	void test18() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
pixel f() {
    image[200,200] ab.
    int xx = ab[100,100] | ab[150,150].
    xx = ab[100,100] & ab[150,150].
    : xx.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(2, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"ab",Type.IMAGE);
			Dimension v9 = ((NameDef)v8).getDimension();
			assertThat("",v9,instanceOf(Dimension.class));
			Expr v10 = ((Dimension)v9).getWidth();
			checkNumLit(v10,200);
			assertEquals(Type.INT, v10.getType());
			Expr v11 = ((Dimension)v9).getHeight();
			checkNumLit(v11,200);
			assertEquals(Type.INT, v11.getType());
			assertNull(v7.getInitializer());
			Declaration v12 = v5.get(1);
			assertThat("",v12,instanceOf(Declaration.class));
			NameDef v13 = v12.getNameDef();
			assertThat("",v13,instanceOf(NameDef.class));
			checkNameDef(v13,"xx",Type.INT);
			assertNull(v13.getDimension());
			Expr v14 = v12.getInitializer();

			checkBinary(v14,Kind.BITOR);
			assertEquals(Type.PIXEL, v14.getType());
			Expr v15 = ((BinaryExpr)v14).getLeft();
			assertThat("",v15,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v15.getType());
			Expr v16 = ((UnaryExprPostfix)v15).getPrimary();
			checkIdentExpr(v16,"ab");
			assertEquals(Type.IMAGE, v16.getType());
			PixelSelector v17 = ((UnaryExprPostfix)v15).getPixel();
			assertThat("",v17,instanceOf(PixelSelector.class));
			Expr v18 = ((PixelSelector)v17).getX();
			checkNumLit(v18,100);
			assertEquals(Type.INT, v18.getType());
			Expr v19 = ((PixelSelector)v17).getY();
			checkNumLit(v19,100);
			assertEquals(Type.INT, v19.getType());
			assertNull(((UnaryExprPostfix)v15).getColor());
			Expr v20 = ((BinaryExpr)v14).getRight();
			assertThat("",v20,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v20.getType());
			Expr v21 = ((UnaryExprPostfix)v20).getPrimary();
			checkIdentExpr(v21,"ab");
			assertEquals(Type.IMAGE, v21.getType());
			PixelSelector v22 = ((UnaryExprPostfix)v20).getPixel();
			assertThat("",v22,instanceOf(PixelSelector.class));
			Expr v23 = ((PixelSelector)v22).getX();
			checkNumLit(v23,150);
			assertEquals(Type.INT, v23.getType());
			Expr v24 = ((PixelSelector)v22).getY();
			checkNumLit(v24,150);
			assertEquals(Type.INT, v24.getType());
			assertNull(((UnaryExprPostfix)v20).getColor());
			List<Statement> v25 = v4.getStatementList();
			int v26= v25.size();
			assertEquals(2, v26);
			Statement v27 = v25.get(0);
			assertThat("",v27,instanceOf(AssignmentStatement.class));
			LValue v28 = ((AssignmentStatement)v27).getLv();
			assertThat("",v28,instanceOf(LValue.class));
			Ident v29 = v28.getIdent();
			checkIdent(v29,"xx");
			assertNull(v28.getPixelSelector());
			assertNull(((LValue)v28).getColor());
			Expr v30 = ((AssignmentStatement)v27).getE();

			checkBinary(v30,Kind.BITAND);
			assertEquals(Type.PIXEL, v30.getType());
			Expr v31 = ((BinaryExpr)v30).getLeft();
			assertThat("",v31,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v31.getType());
			Expr v32 = ((UnaryExprPostfix)v31).getPrimary();
			checkIdentExpr(v32,"ab");
			assertEquals(Type.IMAGE, v32.getType());
			PixelSelector v33 = ((UnaryExprPostfix)v31).getPixel();
			assertThat("",v33,instanceOf(PixelSelector.class));
			Expr v34 = ((PixelSelector)v33).getX();
			checkNumLit(v34,100);
			assertEquals(Type.INT, v34.getType());
			Expr v35 = ((PixelSelector)v33).getY();
			checkNumLit(v35,100);
			assertEquals(Type.INT, v35.getType());
			assertNull(((UnaryExprPostfix)v31).getColor());
			Expr v36 = ((BinaryExpr)v30).getRight();
			assertThat("",v36,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v36.getType());
			Expr v37 = ((UnaryExprPostfix)v36).getPrimary();
			checkIdentExpr(v37,"ab");
			assertEquals(Type.IMAGE, v37.getType());
			PixelSelector v38 = ((UnaryExprPostfix)v36).getPixel();
			assertThat("",v38,instanceOf(PixelSelector.class));
			Expr v39 = ((PixelSelector)v38).getX();
			checkNumLit(v39,150);
			assertEquals(Type.INT, v39.getType());
			Expr v40 = ((PixelSelector)v38).getY();
			checkNumLit(v40,150);
			assertEquals(Type.INT, v40.getType());
			assertNull(((UnaryExprPostfix)v36).getColor());
			Statement v41 = v25.get(1);
			assertThat("",v41,instanceOf(ReturnStatement.class));
			Expr v42 = ((ReturnStatement)v41).getE();
			checkIdentExpr(v42,"xx");
			assertEquals(Type.INT, v42.getType());
		});
	}

	@Test
	void test19() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
pixel f() {
    int xx = 2.
    int yy = 3.
    : xx && yy.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(2, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"xx",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkNumLit(v9,2);
			assertEquals(Type.INT, v9.getType());
			Declaration v10 = v5.get(1);
			assertThat("",v10,instanceOf(Declaration.class));
			NameDef v11 = v10.getNameDef();
			assertThat("",v11,instanceOf(NameDef.class));
			checkNameDef(v11,"yy",Type.INT);
			assertNull(v11.getDimension());
			Expr v12 = v10.getInitializer();
			checkNumLit(v12,3);
			assertEquals(Type.INT, v12.getType());
			List<Statement> v13 = v4.getStatementList();
			int v14= v13.size();
			assertEquals(1, v14);
			Statement v15 = v13.get(0);
			assertThat("",v15,instanceOf(ReturnStatement.class));
			Expr v16 = ((ReturnStatement)v15).getE();

			checkBinary(v16,Kind.AND);
			assertEquals(Type.INT, v16.getType());
			Expr v17 = ((BinaryExpr)v16).getLeft();
			checkIdentExpr(v17,"xx");
			assertEquals(Type.INT, v17.getType());
			Expr v18 = ((BinaryExpr)v16).getRight();
			checkIdentExpr(v18,"yy");
			assertEquals(Type.INT, v18.getType());
		});
	}

	@Test
	void test20() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int aa = "a" == "b".
    image[200,200] image1.
    image[200,200] image2.
    int bb = image1 == image2.
    int cc = image1[100,100] == image2[100,100].
    int dd = 2 == 3.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(6, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"aa",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();

			checkBinary(v9,Kind.EQ);
			assertEquals(Type.INT, v9.getType());
			Expr v10 = ((BinaryExpr)v9).getLeft();
			checkStringLit(v10,"a");assertEquals(Type.STRING, v10.getType());
			Expr v11 = ((BinaryExpr)v9).getRight();
			checkStringLit(v11,"b");assertEquals(Type.STRING, v11.getType());
			Declaration v12 = v5.get(1);
			assertThat("",v12,instanceOf(Declaration.class));
			NameDef v13 = v12.getNameDef();
			assertThat("",v13,instanceOf(NameDef.class));
			checkNameDef(v13,"image1",Type.IMAGE);
			Dimension v14 = ((NameDef)v13).getDimension();
			assertThat("",v14,instanceOf(Dimension.class));
			Expr v15 = ((Dimension)v14).getWidth();
			checkNumLit(v15,200);
			assertEquals(Type.INT, v15.getType());
			Expr v16 = ((Dimension)v14).getHeight();
			checkNumLit(v16,200);
			assertEquals(Type.INT, v16.getType());
			assertNull(v12.getInitializer());
			Declaration v17 = v5.get(2);
			assertThat("",v17,instanceOf(Declaration.class));
			NameDef v18 = v17.getNameDef();
			assertThat("",v18,instanceOf(NameDef.class));
			checkNameDef(v18,"image2",Type.IMAGE);
			Dimension v19 = ((NameDef)v18).getDimension();
			assertThat("",v19,instanceOf(Dimension.class));
			Expr v20 = ((Dimension)v19).getWidth();
			checkNumLit(v20,200);
			assertEquals(Type.INT, v20.getType());
			Expr v21 = ((Dimension)v19).getHeight();
			checkNumLit(v21,200);
			assertEquals(Type.INT, v21.getType());
			assertNull(v17.getInitializer());
			Declaration v22 = v5.get(3);
			assertThat("",v22,instanceOf(Declaration.class));
			NameDef v23 = v22.getNameDef();
			assertThat("",v23,instanceOf(NameDef.class));
			checkNameDef(v23,"bb",Type.INT);
			assertNull(v23.getDimension());
			Expr v24 = v22.getInitializer();

			checkBinary(v24,Kind.EQ);
			assertEquals(Type.INT, v24.getType());
			Expr v25 = ((BinaryExpr)v24).getLeft();
			checkIdentExpr(v25,"image1");
			assertEquals(Type.IMAGE, v25.getType());
			Expr v26 = ((BinaryExpr)v24).getRight();
			checkIdentExpr(v26,"image2");
			assertEquals(Type.IMAGE, v26.getType());
			Declaration v27 = v5.get(4);
			assertThat("",v27,instanceOf(Declaration.class));
			NameDef v28 = v27.getNameDef();
			assertThat("",v28,instanceOf(NameDef.class));
			checkNameDef(v28,"cc",Type.INT);
			assertNull(v28.getDimension());
			Expr v29 = v27.getInitializer();

			checkBinary(v29,Kind.EQ);
			assertEquals(Type.INT, v29.getType());
			Expr v30 = ((BinaryExpr)v29).getLeft();
			assertThat("",v30,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v30.getType());
			Expr v31 = ((UnaryExprPostfix)v30).getPrimary();
			checkIdentExpr(v31,"image1");
			assertEquals(Type.IMAGE, v31.getType());
			PixelSelector v32 = ((UnaryExprPostfix)v30).getPixel();
			assertThat("",v32,instanceOf(PixelSelector.class));
			Expr v33 = ((PixelSelector)v32).getX();
			checkNumLit(v33,100);
			assertEquals(Type.INT, v33.getType());
			Expr v34 = ((PixelSelector)v32).getY();
			checkNumLit(v34,100);
			assertEquals(Type.INT, v34.getType());
			assertNull(((UnaryExprPostfix)v30).getColor());
			Expr v35 = ((BinaryExpr)v29).getRight();
			assertThat("",v35,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v35.getType());
			Expr v36 = ((UnaryExprPostfix)v35).getPrimary();
			checkIdentExpr(v36,"image2");
			assertEquals(Type.IMAGE, v36.getType());
			PixelSelector v37 = ((UnaryExprPostfix)v35).getPixel();
			assertThat("",v37,instanceOf(PixelSelector.class));
			Expr v38 = ((PixelSelector)v37).getX();
			checkNumLit(v38,100);
			assertEquals(Type.INT, v38.getType());
			Expr v39 = ((PixelSelector)v37).getY();
			checkNumLit(v39,100);
			assertEquals(Type.INT, v39.getType());
			assertNull(((UnaryExprPostfix)v35).getColor());
			Declaration v40 = v5.get(5);
			assertThat("",v40,instanceOf(Declaration.class));
			NameDef v41 = v40.getNameDef();
			assertThat("",v41,instanceOf(NameDef.class));
			checkNameDef(v41,"dd",Type.INT);
			assertNull(v41.getDimension());
			Expr v42 = v40.getInitializer();

			checkBinary(v42,Kind.EQ);
			assertEquals(Type.INT, v42.getType());
			Expr v43 = ((BinaryExpr)v42).getLeft();
			checkNumLit(v43,2);
			assertEquals(Type.INT, v43.getType());
			Expr v44 = ((BinaryExpr)v42).getRight();
			checkNumLit(v44,3);
			assertEquals(Type.INT, v44.getType());
			List<Statement> v45 = v4.getStatementList();
			int v46= v45.size();
			assertEquals(0, v46);
		});
	}

	@Test
	void test21() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int aa = 2 ** 3.
    image[200,200] bb.
    int cc = bb[200,200] ** 3.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(3, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"aa",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();

			checkBinary(v9,Kind.EXP);
			assertEquals(Type.INT, v9.getType());
			Expr v10 = ((BinaryExpr)v9).getLeft();
			checkNumLit(v10,2);
			assertEquals(Type.INT, v10.getType());
			Expr v11 = ((BinaryExpr)v9).getRight();
			checkNumLit(v11,3);
			assertEquals(Type.INT, v11.getType());
			Declaration v12 = v5.get(1);
			assertThat("",v12,instanceOf(Declaration.class));
			NameDef v13 = v12.getNameDef();
			assertThat("",v13,instanceOf(NameDef.class));
			checkNameDef(v13,"bb",Type.IMAGE);
			Dimension v14 = ((NameDef)v13).getDimension();
			assertThat("",v14,instanceOf(Dimension.class));
			Expr v15 = ((Dimension)v14).getWidth();
			checkNumLit(v15,200);
			assertEquals(Type.INT, v15.getType());
			Expr v16 = ((Dimension)v14).getHeight();
			checkNumLit(v16,200);
			assertEquals(Type.INT, v16.getType());
			assertNull(v12.getInitializer());
			Declaration v17 = v5.get(2);
			assertThat("",v17,instanceOf(Declaration.class));
			NameDef v18 = v17.getNameDef();
			assertThat("",v18,instanceOf(NameDef.class));
			checkNameDef(v18,"cc",Type.INT);
			assertNull(v18.getDimension());
			Expr v19 = v17.getInitializer();

			checkBinary(v19,Kind.EXP);
			assertEquals(Type.PIXEL, v19.getType());
			Expr v20 = ((BinaryExpr)v19).getLeft();
			assertThat("",v20,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v20.getType());
			Expr v21 = ((UnaryExprPostfix)v20).getPrimary();
			checkIdentExpr(v21,"bb");
			assertEquals(Type.IMAGE, v21.getType());
			PixelSelector v22 = ((UnaryExprPostfix)v20).getPixel();
			assertThat("",v22,instanceOf(PixelSelector.class));
			Expr v23 = ((PixelSelector)v22).getX();
			checkNumLit(v23,200);
			assertEquals(Type.INT, v23.getType());
			Expr v24 = ((PixelSelector)v22).getY();
			checkNumLit(v24,200);
			assertEquals(Type.INT, v24.getType());
			assertNull(((UnaryExprPostfix)v20).getColor());
			Expr v25 = ((BinaryExpr)v19).getRight();
			checkNumLit(v25,3);
			assertEquals(Type.INT, v25.getType());
			List<Statement> v26 = v4.getStatementList();
			int v27= v26.size();
			assertEquals(0, v27);
		});
	}

	@Test
	void test22() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int aa = 2 + 2.
    image[200,200] bb.
    string cc = "2" + "2".
    bb[200,200] = bb[1,1] + bb[2,2].
    bb = bb + bb.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(3, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"aa",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();

			checkBinary(v9,Kind.PLUS);
			assertEquals(Type.INT, v9.getType());
			Expr v10 = ((BinaryExpr)v9).getLeft();
			checkNumLit(v10,2);
			assertEquals(Type.INT, v10.getType());
			Expr v11 = ((BinaryExpr)v9).getRight();
			checkNumLit(v11,2);
			assertEquals(Type.INT, v11.getType());
			Declaration v12 = v5.get(1);
			assertThat("",v12,instanceOf(Declaration.class));
			NameDef v13 = v12.getNameDef();
			assertThat("",v13,instanceOf(NameDef.class));
			checkNameDef(v13,"bb",Type.IMAGE);
			Dimension v14 = ((NameDef)v13).getDimension();
			assertThat("",v14,instanceOf(Dimension.class));
			Expr v15 = ((Dimension)v14).getWidth();
			checkNumLit(v15,200);
			assertEquals(Type.INT, v15.getType());
			Expr v16 = ((Dimension)v14).getHeight();
			checkNumLit(v16,200);
			assertEquals(Type.INT, v16.getType());
			assertNull(v12.getInitializer());
			Declaration v17 = v5.get(2);
			assertThat("",v17,instanceOf(Declaration.class));
			NameDef v18 = v17.getNameDef();
			assertThat("",v18,instanceOf(NameDef.class));
			checkNameDef(v18,"cc",Type.STRING);
			assertNull(v18.getDimension());
			Expr v19 = v17.getInitializer();

			checkBinary(v19,Kind.PLUS);
			assertEquals(Type.STRING, v19.getType());
			Expr v20 = ((BinaryExpr)v19).getLeft();
			checkStringLit(v20,"2");assertEquals(Type.STRING, v20.getType());
			Expr v21 = ((BinaryExpr)v19).getRight();
			checkStringLit(v21,"2");assertEquals(Type.STRING, v21.getType());
			List<Statement> v22 = v4.getStatementList();
			int v23= v22.size();
			assertEquals(2, v23);
			Statement v24 = v22.get(0);
			assertThat("",v24,instanceOf(AssignmentStatement.class));
			LValue v25 = ((AssignmentStatement)v24).getLv();
			assertThat("",v25,instanceOf(LValue.class));
			Ident v26 = v25.getIdent();
			checkIdent(v26,"bb");
			PixelSelector v27 = v25.getPixelSelector();
			assertThat("",v27,instanceOf(PixelSelector.class));
			Expr v28 = ((PixelSelector)v27).getX();
			checkNumLit(v28,200);
			assertEquals(Type.INT, v28.getType());
			Expr v29 = ((PixelSelector)v27).getY();
			checkNumLit(v29,200);
			assertEquals(Type.INT, v29.getType());
			assertNull(((LValue)v25).getColor());
			Expr v30 = ((AssignmentStatement)v24).getE();

			checkBinary(v30,Kind.PLUS);
			assertEquals(Type.PIXEL, v30.getType());
			Expr v31 = ((BinaryExpr)v30).getLeft();
			assertThat("",v31,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v31.getType());
			Expr v32 = ((UnaryExprPostfix)v31).getPrimary();
			checkIdentExpr(v32,"bb");
			assertEquals(Type.IMAGE, v32.getType());
			PixelSelector v33 = ((UnaryExprPostfix)v31).getPixel();
			assertThat("",v33,instanceOf(PixelSelector.class));
			Expr v34 = ((PixelSelector)v33).getX();
			checkNumLit(v34,1);
			assertEquals(Type.INT, v34.getType());
			Expr v35 = ((PixelSelector)v33).getY();
			checkNumLit(v35,1);
			assertEquals(Type.INT, v35.getType());
			assertNull(((UnaryExprPostfix)v31).getColor());
			Expr v36 = ((BinaryExpr)v30).getRight();
			assertThat("",v36,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v36.getType());
			Expr v37 = ((UnaryExprPostfix)v36).getPrimary();
			checkIdentExpr(v37,"bb");
			assertEquals(Type.IMAGE, v37.getType());
			PixelSelector v38 = ((UnaryExprPostfix)v36).getPixel();
			assertThat("",v38,instanceOf(PixelSelector.class));
			Expr v39 = ((PixelSelector)v38).getX();
			checkNumLit(v39,2);
			assertEquals(Type.INT, v39.getType());
			Expr v40 = ((PixelSelector)v38).getY();
			checkNumLit(v40,2);
			assertEquals(Type.INT, v40.getType());
			assertNull(((UnaryExprPostfix)v36).getColor());
			Statement v41 = v22.get(1);
			assertThat("",v41,instanceOf(AssignmentStatement.class));
			LValue v42 = ((AssignmentStatement)v41).getLv();
			assertThat("",v42,instanceOf(LValue.class));
			Ident v43 = v42.getIdent();
			checkIdent(v43,"bb");
			assertNull(v42.getPixelSelector());
			assertNull(((LValue)v42).getColor());
			Expr v44 = ((AssignmentStatement)v41).getE();

			checkBinary(v44,Kind.PLUS);
			assertEquals(Type.IMAGE, v44.getType());
			Expr v45 = ((BinaryExpr)v44).getLeft();
			checkIdentExpr(v45,"bb");
			assertEquals(Type.IMAGE, v45.getType());
			Expr v46 = ((BinaryExpr)v44).getRight();
			checkIdentExpr(v46,"bb");
			assertEquals(Type.IMAGE, v46.getType());
		});
	}

	@Test
	void test23() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int aa.
    image[200,200] bb.
    aa = aa - aa.
    bb[1,1] = bb[2,2] - bb[3,3].
    bb = bb - bb.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(2, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"aa",Type.INT);
			assertNull(v8.getDimension());
			assertNull(v7.getInitializer());
			Declaration v9 = v5.get(1);
			assertThat("",v9,instanceOf(Declaration.class));
			NameDef v10 = v9.getNameDef();
			assertThat("",v10,instanceOf(NameDef.class));
			checkNameDef(v10,"bb",Type.IMAGE);
			Dimension v11 = ((NameDef)v10).getDimension();
			assertThat("",v11,instanceOf(Dimension.class));
			Expr v12 = ((Dimension)v11).getWidth();
			checkNumLit(v12,200);
			assertEquals(Type.INT, v12.getType());
			Expr v13 = ((Dimension)v11).getHeight();
			checkNumLit(v13,200);
			assertEquals(Type.INT, v13.getType());
			assertNull(v9.getInitializer());
			List<Statement> v14 = v4.getStatementList();
			int v15= v14.size();
			assertEquals(3, v15);
			Statement v16 = v14.get(0);
			assertThat("",v16,instanceOf(AssignmentStatement.class));
			LValue v17 = ((AssignmentStatement)v16).getLv();
			assertThat("",v17,instanceOf(LValue.class));
			Ident v18 = v17.getIdent();
			checkIdent(v18,"aa");
			assertNull(v17.getPixelSelector());
			assertNull(((LValue)v17).getColor());
			Expr v19 = ((AssignmentStatement)v16).getE();

			checkBinary(v19,Kind.MINUS);
			assertEquals(Type.INT, v19.getType());
			Expr v20 = ((BinaryExpr)v19).getLeft();
			checkIdentExpr(v20,"aa");
			assertEquals(Type.INT, v20.getType());
			Expr v21 = ((BinaryExpr)v19).getRight();
			checkIdentExpr(v21,"aa");
			assertEquals(Type.INT, v21.getType());
			Statement v22 = v14.get(1);
			assertThat("",v22,instanceOf(AssignmentStatement.class));
			LValue v23 = ((AssignmentStatement)v22).getLv();
			assertThat("",v23,instanceOf(LValue.class));
			Ident v24 = v23.getIdent();
			checkIdent(v24,"bb");
			PixelSelector v25 = v23.getPixelSelector();
			assertThat("",v25,instanceOf(PixelSelector.class));
			Expr v26 = ((PixelSelector)v25).getX();
			checkNumLit(v26,1);
			assertEquals(Type.INT, v26.getType());
			Expr v27 = ((PixelSelector)v25).getY();
			checkNumLit(v27,1);
			assertEquals(Type.INT, v27.getType());
			assertNull(((LValue)v23).getColor());
			Expr v28 = ((AssignmentStatement)v22).getE();

			checkBinary(v28,Kind.MINUS);
			assertEquals(Type.PIXEL, v28.getType());
			Expr v29 = ((BinaryExpr)v28).getLeft();
			assertThat("",v29,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v29.getType());
			Expr v30 = ((UnaryExprPostfix)v29).getPrimary();
			checkIdentExpr(v30,"bb");
			assertEquals(Type.IMAGE, v30.getType());
			PixelSelector v31 = ((UnaryExprPostfix)v29).getPixel();
			assertThat("",v31,instanceOf(PixelSelector.class));
			Expr v32 = ((PixelSelector)v31).getX();
			checkNumLit(v32,2);
			assertEquals(Type.INT, v32.getType());
			Expr v33 = ((PixelSelector)v31).getY();
			checkNumLit(v33,2);
			assertEquals(Type.INT, v33.getType());
			assertNull(((UnaryExprPostfix)v29).getColor());
			Expr v34 = ((BinaryExpr)v28).getRight();
			assertThat("",v34,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v34.getType());
			Expr v35 = ((UnaryExprPostfix)v34).getPrimary();
			checkIdentExpr(v35,"bb");
			assertEquals(Type.IMAGE, v35.getType());
			PixelSelector v36 = ((UnaryExprPostfix)v34).getPixel();
			assertThat("",v36,instanceOf(PixelSelector.class));
			Expr v37 = ((PixelSelector)v36).getX();
			checkNumLit(v37,3);
			assertEquals(Type.INT, v37.getType());
			Expr v38 = ((PixelSelector)v36).getY();
			checkNumLit(v38,3);
			assertEquals(Type.INT, v38.getType());
			assertNull(((UnaryExprPostfix)v34).getColor());
			Statement v39 = v14.get(2);
			assertThat("",v39,instanceOf(AssignmentStatement.class));
			LValue v40 = ((AssignmentStatement)v39).getLv();
			assertThat("",v40,instanceOf(LValue.class));
			Ident v41 = v40.getIdent();
			checkIdent(v41,"bb");
			assertNull(v40.getPixelSelector());
			assertNull(((LValue)v40).getColor());
			Expr v42 = ((AssignmentStatement)v39).getE();

			checkBinary(v42,Kind.MINUS);
			assertEquals(Type.IMAGE, v42.getType());
			Expr v43 = ((BinaryExpr)v42).getLeft();
			checkIdentExpr(v43,"bb");
			assertEquals(Type.IMAGE, v43.getType());
			Expr v44 = ((BinaryExpr)v42).getRight();
			checkIdentExpr(v44,"bb");
			assertEquals(Type.IMAGE, v44.getType());
		});
	}

	@Test
	void test24() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    image[200,200] aa.
    aa[1,1] = aa[1,1] * aa[1,1].
    aa[1,1] = aa[1,1] / aa[1,1].
    aa[1,1] = aa[1,1] % aa[1,1].
    aa = aa * aa.
    aa = aa / aa.
    aa = aa % aa.
    aa[1,1] = aa[1,1] * 2.
    aa[1,1] = aa[1,1] / 2.
    aa[1,1] = aa[1,1] % 2.
    aa = aa * 2.
    aa = aa / 2.
    aa = aa % 2.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(1, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"aa",Type.IMAGE);
			Dimension v9 = ((NameDef)v8).getDimension();
			assertThat("",v9,instanceOf(Dimension.class));
			Expr v10 = ((Dimension)v9).getWidth();
			checkNumLit(v10,200);
			assertEquals(Type.INT, v10.getType());
			Expr v11 = ((Dimension)v9).getHeight();
			checkNumLit(v11,200);
			assertEquals(Type.INT, v11.getType());
			assertNull(v7.getInitializer());
			List<Statement> v12 = v4.getStatementList();
			int v13= v12.size();
			assertEquals(12, v13);
			Statement v14 = v12.get(0);
			assertThat("",v14,instanceOf(AssignmentStatement.class));
			LValue v15 = ((AssignmentStatement)v14).getLv();
			assertThat("",v15,instanceOf(LValue.class));
			Ident v16 = v15.getIdent();
			checkIdent(v16,"aa");
			PixelSelector v17 = v15.getPixelSelector();
			assertThat("",v17,instanceOf(PixelSelector.class));
			Expr v18 = ((PixelSelector)v17).getX();
			checkNumLit(v18,1);
			assertEquals(Type.INT, v18.getType());
			Expr v19 = ((PixelSelector)v17).getY();
			checkNumLit(v19,1);
			assertEquals(Type.INT, v19.getType());
			assertNull(((LValue)v15).getColor());
			Expr v20 = ((AssignmentStatement)v14).getE();

			checkBinary(v20,Kind.TIMES);
			assertEquals(Type.PIXEL, v20.getType());
			Expr v21 = ((BinaryExpr)v20).getLeft();
			assertThat("",v21,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v21.getType());
			Expr v22 = ((UnaryExprPostfix)v21).getPrimary();
			checkIdentExpr(v22,"aa");
			assertEquals(Type.IMAGE, v22.getType());
			PixelSelector v23 = ((UnaryExprPostfix)v21).getPixel();
			assertThat("",v23,instanceOf(PixelSelector.class));
			Expr v24 = ((PixelSelector)v23).getX();
			checkNumLit(v24,1);
			assertEquals(Type.INT, v24.getType());
			Expr v25 = ((PixelSelector)v23).getY();
			checkNumLit(v25,1);
			assertEquals(Type.INT, v25.getType());
			assertNull(((UnaryExprPostfix)v21).getColor());
			Expr v26 = ((BinaryExpr)v20).getRight();
			assertThat("",v26,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v26.getType());
			Expr v27 = ((UnaryExprPostfix)v26).getPrimary();
			checkIdentExpr(v27,"aa");
			assertEquals(Type.IMAGE, v27.getType());
			PixelSelector v28 = ((UnaryExprPostfix)v26).getPixel();
			assertThat("",v28,instanceOf(PixelSelector.class));
			Expr v29 = ((PixelSelector)v28).getX();
			checkNumLit(v29,1);
			assertEquals(Type.INT, v29.getType());
			Expr v30 = ((PixelSelector)v28).getY();
			checkNumLit(v30,1);
			assertEquals(Type.INT, v30.getType());
			assertNull(((UnaryExprPostfix)v26).getColor());
			Statement v31 = v12.get(1);
			assertThat("",v31,instanceOf(AssignmentStatement.class));
			LValue v32 = ((AssignmentStatement)v31).getLv();
			assertThat("",v32,instanceOf(LValue.class));
			Ident v33 = v32.getIdent();
			checkIdent(v33,"aa");
			PixelSelector v34 = v32.getPixelSelector();
			assertThat("",v34,instanceOf(PixelSelector.class));
			Expr v35 = ((PixelSelector)v34).getX();
			checkNumLit(v35,1);
			assertEquals(Type.INT, v35.getType());
			Expr v36 = ((PixelSelector)v34).getY();
			checkNumLit(v36,1);
			assertEquals(Type.INT, v36.getType());
			assertNull(((LValue)v32).getColor());
			Expr v37 = ((AssignmentStatement)v31).getE();

			checkBinary(v37,Kind.DIV);
			assertEquals(Type.PIXEL, v37.getType());
			Expr v38 = ((BinaryExpr)v37).getLeft();
			assertThat("",v38,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v38.getType());
			Expr v39 = ((UnaryExprPostfix)v38).getPrimary();
			checkIdentExpr(v39,"aa");
			assertEquals(Type.IMAGE, v39.getType());
			PixelSelector v40 = ((UnaryExprPostfix)v38).getPixel();
			assertThat("",v40,instanceOf(PixelSelector.class));
			Expr v41 = ((PixelSelector)v40).getX();
			checkNumLit(v41,1);
			assertEquals(Type.INT, v41.getType());
			Expr v42 = ((PixelSelector)v40).getY();
			checkNumLit(v42,1);
			assertEquals(Type.INT, v42.getType());
			assertNull(((UnaryExprPostfix)v38).getColor());
			Expr v43 = ((BinaryExpr)v37).getRight();
			assertThat("",v43,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v43.getType());
			Expr v44 = ((UnaryExprPostfix)v43).getPrimary();
			checkIdentExpr(v44,"aa");
			assertEquals(Type.IMAGE, v44.getType());
			PixelSelector v45 = ((UnaryExprPostfix)v43).getPixel();
			assertThat("",v45,instanceOf(PixelSelector.class));
			Expr v46 = ((PixelSelector)v45).getX();
			checkNumLit(v46,1);
			assertEquals(Type.INT, v46.getType());
			Expr v47 = ((PixelSelector)v45).getY();
			checkNumLit(v47,1);
			assertEquals(Type.INT, v47.getType());
			assertNull(((UnaryExprPostfix)v43).getColor());
			Statement v48 = v12.get(2);
			assertThat("",v48,instanceOf(AssignmentStatement.class));
			LValue v49 = ((AssignmentStatement)v48).getLv();
			assertThat("",v49,instanceOf(LValue.class));
			Ident v50 = v49.getIdent();
			checkIdent(v50,"aa");
			PixelSelector v51 = v49.getPixelSelector();
			assertThat("",v51,instanceOf(PixelSelector.class));
			Expr v52 = ((PixelSelector)v51).getX();
			checkNumLit(v52,1);
			assertEquals(Type.INT, v52.getType());
			Expr v53 = ((PixelSelector)v51).getY();
			checkNumLit(v53,1);
			assertEquals(Type.INT, v53.getType());
			assertNull(((LValue)v49).getColor());
			Expr v54 = ((AssignmentStatement)v48).getE();

			checkBinary(v54,Kind.MOD);
			assertEquals(Type.PIXEL, v54.getType());
			Expr v55 = ((BinaryExpr)v54).getLeft();
			assertThat("",v55,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v55.getType());
			Expr v56 = ((UnaryExprPostfix)v55).getPrimary();
			checkIdentExpr(v56,"aa");
			assertEquals(Type.IMAGE, v56.getType());
			PixelSelector v57 = ((UnaryExprPostfix)v55).getPixel();
			assertThat("",v57,instanceOf(PixelSelector.class));
			Expr v58 = ((PixelSelector)v57).getX();
			checkNumLit(v58,1);
			assertEquals(Type.INT, v58.getType());
			Expr v59 = ((PixelSelector)v57).getY();
			checkNumLit(v59,1);
			assertEquals(Type.INT, v59.getType());
			assertNull(((UnaryExprPostfix)v55).getColor());
			Expr v60 = ((BinaryExpr)v54).getRight();
			assertThat("",v60,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v60.getType());
			Expr v61 = ((UnaryExprPostfix)v60).getPrimary();
			checkIdentExpr(v61,"aa");
			assertEquals(Type.IMAGE, v61.getType());
			PixelSelector v62 = ((UnaryExprPostfix)v60).getPixel();
			assertThat("",v62,instanceOf(PixelSelector.class));
			Expr v63 = ((PixelSelector)v62).getX();
			checkNumLit(v63,1);
			assertEquals(Type.INT, v63.getType());
			Expr v64 = ((PixelSelector)v62).getY();
			checkNumLit(v64,1);
			assertEquals(Type.INT, v64.getType());
			assertNull(((UnaryExprPostfix)v60).getColor());
			Statement v65 = v12.get(3);
			assertThat("",v65,instanceOf(AssignmentStatement.class));
			LValue v66 = ((AssignmentStatement)v65).getLv();
			assertThat("",v66,instanceOf(LValue.class));
			Ident v67 = v66.getIdent();
			checkIdent(v67,"aa");
			assertNull(v66.getPixelSelector());
			assertNull(((LValue)v66).getColor());
			Expr v68 = ((AssignmentStatement)v65).getE();

			checkBinary(v68,Kind.TIMES);
			assertEquals(Type.IMAGE, v68.getType());
			Expr v69 = ((BinaryExpr)v68).getLeft();
			checkIdentExpr(v69,"aa");
			assertEquals(Type.IMAGE, v69.getType());
			Expr v70 = ((BinaryExpr)v68).getRight();
			checkIdentExpr(v70,"aa");
			assertEquals(Type.IMAGE, v70.getType());
			Statement v71 = v12.get(4);
			assertThat("",v71,instanceOf(AssignmentStatement.class));
			LValue v72 = ((AssignmentStatement)v71).getLv();
			assertThat("",v72,instanceOf(LValue.class));
			Ident v73 = v72.getIdent();
			checkIdent(v73,"aa");
			assertNull(v72.getPixelSelector());
			assertNull(((LValue)v72).getColor());
			Expr v74 = ((AssignmentStatement)v71).getE();

			checkBinary(v74,Kind.DIV);
			assertEquals(Type.IMAGE, v74.getType());
			Expr v75 = ((BinaryExpr)v74).getLeft();
			checkIdentExpr(v75,"aa");
			assertEquals(Type.IMAGE, v75.getType());
			Expr v76 = ((BinaryExpr)v74).getRight();
			checkIdentExpr(v76,"aa");
			assertEquals(Type.IMAGE, v76.getType());
			Statement v77 = v12.get(5);
			assertThat("",v77,instanceOf(AssignmentStatement.class));
			LValue v78 = ((AssignmentStatement)v77).getLv();
			assertThat("",v78,instanceOf(LValue.class));
			Ident v79 = v78.getIdent();
			checkIdent(v79,"aa");
			assertNull(v78.getPixelSelector());
			assertNull(((LValue)v78).getColor());
			Expr v80 = ((AssignmentStatement)v77).getE();

			checkBinary(v80,Kind.MOD);
			assertEquals(Type.IMAGE, v80.getType());
			Expr v81 = ((BinaryExpr)v80).getLeft();
			checkIdentExpr(v81,"aa");
			assertEquals(Type.IMAGE, v81.getType());
			Expr v82 = ((BinaryExpr)v80).getRight();
			checkIdentExpr(v82,"aa");
			assertEquals(Type.IMAGE, v82.getType());
			Statement v83 = v12.get(6);
			assertThat("",v83,instanceOf(AssignmentStatement.class));
			LValue v84 = ((AssignmentStatement)v83).getLv();
			assertThat("",v84,instanceOf(LValue.class));
			Ident v85 = v84.getIdent();
			checkIdent(v85,"aa");
			PixelSelector v86 = v84.getPixelSelector();
			assertThat("",v86,instanceOf(PixelSelector.class));
			Expr v87 = ((PixelSelector)v86).getX();
			checkNumLit(v87,1);
			assertEquals(Type.INT, v87.getType());
			Expr v88 = ((PixelSelector)v86).getY();
			checkNumLit(v88,1);
			assertEquals(Type.INT, v88.getType());
			assertNull(((LValue)v84).getColor());
			Expr v89 = ((AssignmentStatement)v83).getE();

			checkBinary(v89,Kind.TIMES);
			assertEquals(Type.PIXEL, v89.getType());
			Expr v90 = ((BinaryExpr)v89).getLeft();
			assertThat("",v90,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v90.getType());
			Expr v91 = ((UnaryExprPostfix)v90).getPrimary();
			checkIdentExpr(v91,"aa");
			assertEquals(Type.IMAGE, v91.getType());
			PixelSelector v92 = ((UnaryExprPostfix)v90).getPixel();
			assertThat("",v92,instanceOf(PixelSelector.class));
			Expr v93 = ((PixelSelector)v92).getX();
			checkNumLit(v93,1);
			assertEquals(Type.INT, v93.getType());
			Expr v94 = ((PixelSelector)v92).getY();
			checkNumLit(v94,1);
			assertEquals(Type.INT, v94.getType());
			assertNull(((UnaryExprPostfix)v90).getColor());
			Expr v95 = ((BinaryExpr)v89).getRight();
			checkNumLit(v95,2);
			assertEquals(Type.INT, v95.getType());
			Statement v96 = v12.get(7);
			assertThat("",v96,instanceOf(AssignmentStatement.class));
			LValue v97 = ((AssignmentStatement)v96).getLv();
			assertThat("",v97,instanceOf(LValue.class));
			Ident v98 = v97.getIdent();
			checkIdent(v98,"aa");
			PixelSelector v99 = v97.getPixelSelector();
			assertThat("",v99,instanceOf(PixelSelector.class));
			Expr v100 = ((PixelSelector)v99).getX();
			checkNumLit(v100,1);
			assertEquals(Type.INT, v100.getType());
			Expr v101 = ((PixelSelector)v99).getY();
			checkNumLit(v101,1);
			assertEquals(Type.INT, v101.getType());
			assertNull(((LValue)v97).getColor());
			Expr v102 = ((AssignmentStatement)v96).getE();

			checkBinary(v102,Kind.DIV);
			assertEquals(Type.PIXEL, v102.getType());
			Expr v103 = ((BinaryExpr)v102).getLeft();
			assertThat("",v103,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v103.getType());
			Expr v104 = ((UnaryExprPostfix)v103).getPrimary();
			checkIdentExpr(v104,"aa");
			assertEquals(Type.IMAGE, v104.getType());
			PixelSelector v105 = ((UnaryExprPostfix)v103).getPixel();
			assertThat("",v105,instanceOf(PixelSelector.class));
			Expr v106 = ((PixelSelector)v105).getX();
			checkNumLit(v106,1);
			assertEquals(Type.INT, v106.getType());
			Expr v107 = ((PixelSelector)v105).getY();
			checkNumLit(v107,1);
			assertEquals(Type.INT, v107.getType());
			assertNull(((UnaryExprPostfix)v103).getColor());
			Expr v108 = ((BinaryExpr)v102).getRight();
			checkNumLit(v108,2);
			assertEquals(Type.INT, v108.getType());
			Statement v109 = v12.get(8);
			assertThat("",v109,instanceOf(AssignmentStatement.class));
			LValue v110 = ((AssignmentStatement)v109).getLv();
			assertThat("",v110,instanceOf(LValue.class));
			Ident v111 = v110.getIdent();
			checkIdent(v111,"aa");
			PixelSelector v112 = v110.getPixelSelector();
			assertThat("",v112,instanceOf(PixelSelector.class));
			Expr v113 = ((PixelSelector)v112).getX();
			checkNumLit(v113,1);
			assertEquals(Type.INT, v113.getType());
			Expr v114 = ((PixelSelector)v112).getY();
			checkNumLit(v114,1);
			assertEquals(Type.INT, v114.getType());
			assertNull(((LValue)v110).getColor());
			Expr v115 = ((AssignmentStatement)v109).getE();

			checkBinary(v115,Kind.MOD);
			assertEquals(Type.PIXEL, v115.getType());
			Expr v116 = ((BinaryExpr)v115).getLeft();
			assertThat("",v116,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v116.getType());
			Expr v117 = ((UnaryExprPostfix)v116).getPrimary();
			checkIdentExpr(v117,"aa");
			assertEquals(Type.IMAGE, v117.getType());
			PixelSelector v118 = ((UnaryExprPostfix)v116).getPixel();
			assertThat("",v118,instanceOf(PixelSelector.class));
			Expr v119 = ((PixelSelector)v118).getX();
			checkNumLit(v119,1);
			assertEquals(Type.INT, v119.getType());
			Expr v120 = ((PixelSelector)v118).getY();
			checkNumLit(v120,1);
			assertEquals(Type.INT, v120.getType());
			assertNull(((UnaryExprPostfix)v116).getColor());
			Expr v121 = ((BinaryExpr)v115).getRight();
			checkNumLit(v121,2);
			assertEquals(Type.INT, v121.getType());
			Statement v122 = v12.get(9);
			assertThat("",v122,instanceOf(AssignmentStatement.class));
			LValue v123 = ((AssignmentStatement)v122).getLv();
			assertThat("",v123,instanceOf(LValue.class));
			Ident v124 = v123.getIdent();
			checkIdent(v124,"aa");
			assertNull(v123.getPixelSelector());
			assertNull(((LValue)v123).getColor());
			Expr v125 = ((AssignmentStatement)v122).getE();

			checkBinary(v125,Kind.TIMES);
			assertEquals(Type.IMAGE, v125.getType());
			Expr v126 = ((BinaryExpr)v125).getLeft();
			checkIdentExpr(v126,"aa");
			assertEquals(Type.IMAGE, v126.getType());
			Expr v127 = ((BinaryExpr)v125).getRight();
			checkNumLit(v127,2);
			assertEquals(Type.INT, v127.getType());
			Statement v128 = v12.get(10);
			assertThat("",v128,instanceOf(AssignmentStatement.class));
			LValue v129 = ((AssignmentStatement)v128).getLv();
			assertThat("",v129,instanceOf(LValue.class));
			Ident v130 = v129.getIdent();
			checkIdent(v130,"aa");
			assertNull(v129.getPixelSelector());
			assertNull(((LValue)v129).getColor());
			Expr v131 = ((AssignmentStatement)v128).getE();

			checkBinary(v131,Kind.DIV);
			assertEquals(Type.IMAGE, v131.getType());
			Expr v132 = ((BinaryExpr)v131).getLeft();
			checkIdentExpr(v132,"aa");
			assertEquals(Type.IMAGE, v132.getType());
			Expr v133 = ((BinaryExpr)v131).getRight();
			checkNumLit(v133,2);
			assertEquals(Type.INT, v133.getType());
			Statement v134 = v12.get(11);
			assertThat("",v134,instanceOf(AssignmentStatement.class));
			LValue v135 = ((AssignmentStatement)v134).getLv();
			assertThat("",v135,instanceOf(LValue.class));
			Ident v136 = v135.getIdent();
			checkIdent(v136,"aa");
			assertNull(v135.getPixelSelector());
			assertNull(((LValue)v135).getColor());
			Expr v137 = ((AssignmentStatement)v134).getE();

			checkBinary(v137,Kind.MOD);
			assertEquals(Type.IMAGE, v137.getType());
			Expr v138 = ((BinaryExpr)v137).getLeft();
			checkIdentExpr(v138,"aa");
			assertEquals(Type.IMAGE, v138.getType());
			Expr v139 = ((BinaryExpr)v137).getRight();
			checkNumLit(v139,2);
			assertEquals(Type.INT, v139.getType());
		});
	}

	@Test
	void test25() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    image[200,200] aa.
    pixel bb = aa[200,200].
    int cc.
    aa:red = aa.
    bb:grn = cc.
    aa[1,1]:grn = cc.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(3, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"aa",Type.IMAGE);
			Dimension v9 = ((NameDef)v8).getDimension();
			assertThat("",v9,instanceOf(Dimension.class));
			Expr v10 = ((Dimension)v9).getWidth();
			checkNumLit(v10,200);
			assertEquals(Type.INT, v10.getType());
			Expr v11 = ((Dimension)v9).getHeight();
			checkNumLit(v11,200);
			assertEquals(Type.INT, v11.getType());
			assertNull(v7.getInitializer());
			Declaration v12 = v5.get(1);
			assertThat("",v12,instanceOf(Declaration.class));
			NameDef v13 = v12.getNameDef();
			assertThat("",v13,instanceOf(NameDef.class));
			checkNameDef(v13,"bb",Type.PIXEL);
			assertNull(v13.getDimension());
			Expr v14 = v12.getInitializer();
			assertThat("",v14,instanceOf(UnaryExprPostfix.class));
			assertEquals(Type.PIXEL, v14.getType());
			Expr v15 = ((UnaryExprPostfix)v14).getPrimary();
			checkIdentExpr(v15,"aa");
			assertEquals(Type.IMAGE, v15.getType());
			PixelSelector v16 = ((UnaryExprPostfix)v14).getPixel();
			assertThat("",v16,instanceOf(PixelSelector.class));
			Expr v17 = ((PixelSelector)v16).getX();
			checkNumLit(v17,200);
			assertEquals(Type.INT, v17.getType());
			Expr v18 = ((PixelSelector)v16).getY();
			checkNumLit(v18,200);
			assertEquals(Type.INT, v18.getType());
			assertNull(((UnaryExprPostfix)v14).getColor());
			Declaration v19 = v5.get(2);
			assertThat("",v19,instanceOf(Declaration.class));
			NameDef v20 = v19.getNameDef();
			assertThat("",v20,instanceOf(NameDef.class));
			checkNameDef(v20,"cc",Type.INT);
			assertNull(v20.getDimension());
			assertNull(v19.getInitializer());
			List<Statement> v21 = v4.getStatementList();
			int v22= v21.size();
			assertEquals(3, v22);
			Statement v23 = v21.get(0);
			assertThat("",v23,instanceOf(AssignmentStatement.class));
			LValue v24 = ((AssignmentStatement)v23).getLv();
			assertThat("",v24,instanceOf(LValue.class));
			Ident v25 = v24.getIdent();
			checkIdent(v25,"aa");
			assertNull(v24.getPixelSelector());
			assertEquals( ColorChannel.red,v24.getColor());
			Expr v26 = ((AssignmentStatement)v23).getE();
			checkIdentExpr(v26,"aa");
			assertEquals(Type.IMAGE, v26.getType());
			Statement v27 = v21.get(1);
			assertThat("",v27,instanceOf(AssignmentStatement.class));
			LValue v28 = ((AssignmentStatement)v27).getLv();
			assertThat("",v28,instanceOf(LValue.class));
			Ident v29 = v28.getIdent();
			checkIdent(v29,"bb");
			assertNull(v28.getPixelSelector());
			assertEquals( ColorChannel.grn,v28.getColor());
			Expr v30 = ((AssignmentStatement)v27).getE();
			checkIdentExpr(v30,"cc");
			assertEquals(Type.INT, v30.getType());
			Statement v31 = v21.get(2);
			assertThat("",v31,instanceOf(AssignmentStatement.class));
			LValue v32 = ((AssignmentStatement)v31).getLv();
			assertThat("",v32,instanceOf(LValue.class));
			Ident v33 = v32.getIdent();
			checkIdent(v33,"aa");
			PixelSelector v34 = v32.getPixelSelector();
			assertThat("",v34,instanceOf(PixelSelector.class));
			Expr v35 = ((PixelSelector)v34).getX();
			checkNumLit(v35,1);
			assertEquals(Type.INT, v35.getType());
			Expr v36 = ((PixelSelector)v34).getY();
			checkNumLit(v36,1);
			assertEquals(Type.INT, v36.getType());
			assertEquals( ColorChannel.grn,v32.getColor());
			Expr v37 = ((AssignmentStatement)v31).getE();
			checkIdentExpr(v37,"cc");
			assertEquals(Type.INT, v37.getType());
		});
	}

	@Test
	void test26() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int i = 2.
    while i > 0 {
        int i = 3.
        i = 4.
        while i < 5 {
            int i = 2.
            i = 7.
        }.
    }.
}
""";
			AST ast = getAST(input);
			assertThat("",ast,instanceOf(Program.class));
			Program v0= (Program)ast;
			Ident v1 = v0.getIdent();
			checkIdent(v1,"f");
			List<NameDef> v2 = v0.getParamList();
			int v3= v2.size();
			assertEquals(0, v3);
			Block v4 = v0.getBlock();
			assertThat("",v4,instanceOf(Block.class));
			List<Declaration> v5 = v4.getDecList();
			int v6= v5.size();
			assertEquals(1, v6);
			Declaration v7 = v5.get(0);
			assertThat("",v7,instanceOf(Declaration.class));
			NameDef v8 = v7.getNameDef();
			assertThat("",v8,instanceOf(NameDef.class));
			checkNameDef(v8,"i",Type.INT);
			assertNull(v8.getDimension());
			Expr v9 = v7.getInitializer();
			checkNumLit(v9,2);
			assertEquals(Type.INT, v9.getType());
			List<Statement> v10 = v4.getStatementList();
			int v11= v10.size();
			assertEquals(1, v11);
			Statement v12 = v10.get(0);
			assertThat("",v12,instanceOf(WhileStatement.class));
			Expr v13 = ((WhileStatement)v12).getGuard();

			checkBinary(v13,Kind.GT);
			assertEquals(Type.INT, v13.getType());
			Expr v14 = ((BinaryExpr)v13).getLeft();
			checkIdentExpr(v14,"i");
			assertEquals(Type.INT, v14.getType());
			Expr v15 = ((BinaryExpr)v13).getRight();
			checkNumLit(v15,0);
			assertEquals(Type.INT, v15.getType());
			Block v16 = ((WhileStatement)v12).getBlock();
			assertThat("",v16,instanceOf(Block.class));
			List<Declaration> v17 = v16.getDecList();
			int v18= v17.size();
			assertEquals(1, v18);
			Declaration v19 = v17.get(0);
			assertThat("",v19,instanceOf(Declaration.class));
			NameDef v20 = v19.getNameDef();
			assertThat("",v20,instanceOf(NameDef.class));
			checkNameDef(v20,"i",Type.INT);
			assertNull(v20.getDimension());
			Expr v21 = v19.getInitializer();
			checkNumLit(v21,3);
			assertEquals(Type.INT, v21.getType());
			List<Statement> v22 = v16.getStatementList();
			int v23= v22.size();
			assertEquals(2, v23);
			Statement v24 = v22.get(0);
			assertThat("",v24,instanceOf(AssignmentStatement.class));
			LValue v25 = ((AssignmentStatement)v24).getLv();
			assertThat("",v25,instanceOf(LValue.class));
			Ident v26 = v25.getIdent();
			checkIdent(v26,"i");
			assertNull(v25.getPixelSelector());
			assertNull(((LValue)v25).getColor());
			Expr v27 = ((AssignmentStatement)v24).getE();
			checkNumLit(v27,4);
			assertEquals(Type.INT, v27.getType());
			Statement v28 = v22.get(1);
			assertThat("",v28,instanceOf(WhileStatement.class));
			Expr v29 = ((WhileStatement)v28).getGuard();

			checkBinary(v29,Kind.LT);
			assertEquals(Type.INT, v29.getType());
			Expr v30 = ((BinaryExpr)v29).getLeft();
			checkIdentExpr(v30,"i");
			assertEquals(Type.INT, v30.getType());
			Expr v31 = ((BinaryExpr)v29).getRight();
			checkNumLit(v31,5);
			assertEquals(Type.INT, v31.getType());
			Block v32 = ((WhileStatement)v28).getBlock();
			assertThat("",v32,instanceOf(Block.class));
			List<Declaration> v33 = v32.getDecList();
			int v34= v33.size();
			assertEquals(1, v34);
			Declaration v35 = v33.get(0);
			assertThat("",v35,instanceOf(Declaration.class));
			NameDef v36 = v35.getNameDef();
			assertThat("",v36,instanceOf(NameDef.class));
			checkNameDef(v36,"i",Type.INT);
			assertNull(v36.getDimension());
			Expr v37 = v35.getInitializer();
			checkNumLit(v37,2);
			assertEquals(Type.INT, v37.getType());
			List<Statement> v38 = v32.getStatementList();
			int v39= v38.size();
			assertEquals(1, v39);
			Statement v40 = v38.get(0);
			assertThat("",v40,instanceOf(AssignmentStatement.class));
			LValue v41 = ((AssignmentStatement)v40).getLv();
			assertThat("",v41,instanceOf(LValue.class));
			Ident v42 = v41.getIdent();
			checkIdent(v42,"i");
			assertNull(v41.getPixelSelector());
			assertNull(((LValue)v41).getColor());
			Expr v43 = ((AssignmentStatement)v40).getE();
			checkNumLit(v43,7);
			assertEquals(Type.INT, v43.getType());
		});
	}

	@Test
	void test27() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				string f(int f, string f){}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test28() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				string f(int f, string s){
				  int s.
				  string ss.
				   }
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test29() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				void f(void xx){}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test30() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f(){
				  int xx = 2.
				  string ss = "hello".
				  image[100,100] ii = "url".
				  image[200,200] ii1 = ii.
				  :xx.
				  }
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test31() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				void f(){
				  int xx = 2+xx.
				  }
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test32() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				string s(string s0, string s1, int ok){
				:if ok ? ok+1 ? s0 + s1 .
				}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test33() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
				int f(){
				int i = 3.
				while i > 0 {
				   string xx = "hello".
				   write xx.
				   i = i -1.
				}.
				i = 3.
				while i > 0 {
				   write xx.
				   i = i -1.
				}.
				: i.
				}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test34() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    string xx = "x".
    : -xx.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test35() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    string xx = "x".
    : !xx.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test36() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    image[100,100] xx = rand.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test37() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    : 2 | 2.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test38() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    string xx = "a".
    string yy = "b".
    int bb = xx && yy.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test39() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    string xx = "a".
    string yy = "b".
    int bb = xx > yy.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test40() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int cc = 2 == "a".
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test41() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int cc = "a" ** 2.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test42() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int cc.
    cc = "2" + 2.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test43() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int cc.
    cc = "2" - 2.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test44() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    string aa = "2" * "2".
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test45() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    image[200,200] aa.
    pixel bb = aa[200,200].
    bb[200,200]:grn = 2.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test46() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    : 2.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test47() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
image f() {
    : 2.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test48() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
pixel f() {
    : "b".
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test49() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
int f() {
    : "A".
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test50() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int i = 2.
    while i < 2{
        int j.
    }.
    while i > 3 {
        int t = 2.
        while t > 4 {
            j = 5.
        }.
    }.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test51() throws PLCException{
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
void f() {
    int i = 2.
    while i < 2{
        int j.
    }.
    j = 2.
}
""";
			assertThrows(TypeCheckException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

}