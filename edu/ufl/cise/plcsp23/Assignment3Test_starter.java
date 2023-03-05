package edu.ufl.cise.plcsp23;

import static edu.ufl.cise.plcsp23.IToken.Kind.RES_a;
import static edu.ufl.cise.plcsp23.IToken.Kind.RES_a_polar;
import static edu.ufl.cise.plcsp23.IToken.Kind.RES_r;
import static edu.ufl.cise.plcsp23.IToken.Kind.RES_r_polar;
import static edu.ufl.cise.plcsp23.IToken.Kind.RES_x;
import static edu.ufl.cise.plcsp23.IToken.Kind.RES_x_cart;
import static edu.ufl.cise.plcsp23.IToken.Kind.RES_y;
import static edu.ufl.cise.plcsp23.IToken.Kind.RES_y_cart;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.ast.AST;
import edu.ufl.cise.plcsp23.ast.AssignmentStatement;
import edu.ufl.cise.plcsp23.ast.BinaryExpr;
import edu.ufl.cise.plcsp23.ast.Block;
import edu.ufl.cise.plcsp23.ast.ColorChannel;
import edu.ufl.cise.plcsp23.ast.ConditionalExpr;
import edu.ufl.cise.plcsp23.ast.Declaration;
import edu.ufl.cise.plcsp23.ast.Dimension;
import edu.ufl.cise.plcsp23.ast.ExpandedPixelExpr;
import edu.ufl.cise.plcsp23.ast.Expr;
import edu.ufl.cise.plcsp23.ast.Ident;
import edu.ufl.cise.plcsp23.ast.IdentExpr;
import edu.ufl.cise.plcsp23.ast.LValue;
import edu.ufl.cise.plcsp23.ast.NameDef;
import edu.ufl.cise.plcsp23.ast.NumLitExpr;
import edu.ufl.cise.plcsp23.ast.PixelFuncExpr;
import edu.ufl.cise.plcsp23.ast.PixelSelector;
import edu.ufl.cise.plcsp23.ast.PredeclaredVarExpr;
import edu.ufl.cise.plcsp23.ast.Program;
import edu.ufl.cise.plcsp23.ast.RandomExpr;
import edu.ufl.cise.plcsp23.ast.Statement;
import edu.ufl.cise.plcsp23.ast.StringLitExpr;
import edu.ufl.cise.plcsp23.ast.Type;
import edu.ufl.cise.plcsp23.ast.UnaryExpr;
import edu.ufl.cise.plcsp23.ast.UnaryExprPostfix;
import edu.ufl.cise.plcsp23.ast.WhileStatement;
import edu.ufl.cise.plcsp23.ast.WriteStatement;
import edu.ufl.cise.plcsp23.ast.ZExpr;

class Assignment3Test_starter {
	static final int TIMEOUT_MILLIS = 1000;

	/**
	 * Constructs a scanner and parser for the given input string, scans and parses
	 * the input and returns and AST.
	 *
	 * @param input String representing program to be tested
	 * @return AST representing the program
	 * @throws PLCException
	 */
	AST getAST(String input) throws PLCException {
		return CompilerComponentFactory.makeParser(input).parse();
	}

	/**
	 * Checks that the given AST e has type NumLitExpr with the indicated value.
	 * Returns the given AST cast to NumLitExpr.
	 *
	 * @param e
	 * @param value
	 * @return
	 */
	NumLitExpr checkNumLit(AST e, int value) {
		assertThat("", e, instanceOf(NumLitExpr.class));
		NumLitExpr ne = (NumLitExpr) e;
		assertEquals(value, ne.getValue());
		return ne;
	}

	/**
	 * Checks that the given AST e has type StringLitExpr with the given String
	 * value. Returns the given AST cast to StringLitExpr.
	 * 
	 * @param e
	 * @param name
	 * @return
	 */
	StringLitExpr checkStringLit(AST e, String value) {
		assertThat("", e, instanceOf(StringLitExpr.class));
		StringLitExpr se = (StringLitExpr) e;
		assertEquals(value, se.getValue());
		return se;
	}

	/**
	 * Checks that the given AST e has type UnaryExpr with the given operator.
	 * Returns the given AST cast to UnaryExpr.
	 * 
	 * @param e
	 * @param op Kind of expected operator
	 * @return
	 */
	private UnaryExpr checkUnary(AST e, Kind op) {
		assertThat("", e, instanceOf(UnaryExpr.class));
		assertEquals(op, ((UnaryExpr) e).getOp());
		return (UnaryExpr) e;
	}

	/**
	 * Checks that the given AST e has type ConditionalExpr. Returns the given AST
	 * cast to ConditionalExpr.
	 * 
	 * @param e
	 * @return
	 */
	private ConditionalExpr checkConditional(AST e) {
		assertThat("", e, instanceOf(ConditionalExpr.class));
		return (ConditionalExpr) e;
	}

	/**
	 * Checks that the given AST e has type BinaryExpr with the given operator.
	 * Returns the given AST cast to BinaryExpr.
	 *
	 * @param e
	 * @param op Kind of expected operator
	 * @return
	 */
	BinaryExpr checkBinary(AST e, Kind expectedOp) {
		assertThat("", e, instanceOf(BinaryExpr.class));
		BinaryExpr be = (BinaryExpr) e;
		assertEquals(expectedOp, be.getOp());
		return be;
	}

	/**
	 * Checks that the given AST e has type IdentExpr with the given name. Returns
	 * the given AST cast to IdentExpr.
	 * 
	 * @param e
	 * @param name
	 * @return
	 */
	IdentExpr checkIdentExpr(AST e, String name) {
		assertThat("", e, instanceOf(IdentExpr.class));
		IdentExpr ident = (IdentExpr) e;
		assertEquals(name, ident.getName());
		return ident;
	}

	/**
	 * Checks that the given AST e has type Ident with the given name. Returns the
	 * given AST cast to IdentExpr.
	 * 
	 * @param e
	 * @param name
	 * @return
	 */
	Ident checkIdent(AST e, String name) {
		assertThat("", e, instanceOf(Ident.class));
		Ident ident = (Ident) e;
		assertEquals(name, ident.getName());
		return ident;
	}

	NameDef checkNameDef(AST d, String name, Type type) {
		assertThat("", d, instanceOf(NameDef.class));
		NameDef def = (NameDef) d;
		assertEquals(name, def.getIdent().getName());
		assertEquals(type, def.getType());
		return def;
	}

	@Test
	void test0() throws PLCException {
		String input = """
				void d(){}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "d");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(0, v6);
		List<Statement> v7 = v4.getStatementList();
		int v8 = v7.size();
		assertEquals(0, v8);
	}

	@Test
	void test1() throws PLCException {
		String input = """
				int d(int j) {}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "d");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(1, v3);
		NameDef v4 = v2.get(0);
		assertThat("", v4, instanceOf(NameDef.class));
		checkNameDef(v4, "j", Type.INT);
		assertNull(v4.getDimension());
		Block v5 = v0.getBlock();
		assertThat("", v5, instanceOf(Block.class));
		List<Declaration> v6 = v5.getDecList();
		int v7 = v6.size();
		assertEquals(0, v7);
		List<Statement> v8 = v5.getStatementList();
		int v9 = v8.size();
		assertEquals(0, v9);
	}

	@Test
	void test2() throws PLCException {
		String input = """
				void prog(
				string j,
				image k){
				write x.
						}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "prog");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(2, v3);
		NameDef v4 = v2.get(0);
		assertThat("", v4, instanceOf(NameDef.class));
		checkNameDef(v4, "j", Type.STRING);
		assertNull(v4.getDimension());
		NameDef v5 = v2.get(1);
		assertThat("", v5, instanceOf(NameDef.class));
		checkNameDef(v5, "k", Type.IMAGE);
		assertNull(v5.getDimension());
		Block v6 = v0.getBlock();
		assertThat("", v6, instanceOf(Block.class));
		List<Declaration> v7 = v6.getDecList();
		int v8 = v7.size();
		assertEquals(0, v8);
		List<Statement> v9 = v6.getStatementList();
		int v10 = v9.size();
		assertEquals(1, v10);
		Statement v11 = v9.get(0);
		assertThat("", v11, instanceOf(WriteStatement.class));
		Expr v12 = ((WriteStatement) v11).getE();
		assertThat("", v12, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_x, ((PredeclaredVarExpr) v12).getKind());
	}

	@Test
	void test3() throws PLCException {
		String input = """
				void prog(int j, string z, image i) {}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "prog");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(3, v3);
		NameDef v4 = v2.get(0);
		assertThat("", v4, instanceOf(NameDef.class));
		checkNameDef(v4, "j", Type.INT);
		assertNull(v4.getDimension());
		NameDef v5 = v2.get(1);
		assertThat("", v5, instanceOf(NameDef.class));
		checkNameDef(v5, "z", Type.STRING);
		assertNull(v5.getDimension());
		NameDef v6 = v2.get(2);
		assertThat("", v6, instanceOf(NameDef.class));
		checkNameDef(v6, "i", Type.IMAGE);
		assertNull(v6.getDimension());
		Block v7 = v0.getBlock();
		assertThat("", v7, instanceOf(Block.class));
		List<Declaration> v8 = v7.getDecList();
		int v9 = v8.size();
		assertEquals(0, v9);
		List<Statement> v10 = v7.getStatementList();
		int v11 = v10.size();
		assertEquals(0, v11);
	}

	@Test
	void test4() throws PLCException {
		String input = """
				int prog() {
				int s0.
				string s1.
				image s2.
				pixel s3.
				}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "prog");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(4, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "s0", Type.INT);
		assertNull(v8.getDimension());
		assertNull(v7.getInitializer());
		Declaration v9 = v5.get(1);
		assertThat("", v9, instanceOf(Declaration.class));
		NameDef v10 = v9.getNameDef();
		assertThat("", v10, instanceOf(NameDef.class));
		checkNameDef(v10, "s1", Type.STRING);
		assertNull(v10.getDimension());
		assertNull(v9.getInitializer());
		Declaration v11 = v5.get(2);
		assertThat("", v11, instanceOf(Declaration.class));
		NameDef v12 = v11.getNameDef();
		assertThat("", v12, instanceOf(NameDef.class));
		checkNameDef(v12, "s2", Type.IMAGE);
		assertNull(v12.getDimension());
		assertNull(v11.getInitializer());
		Declaration v13 = v5.get(3);
		assertThat("", v13, instanceOf(Declaration.class));
		NameDef v14 = v13.getNameDef();
		assertThat("", v14, instanceOf(NameDef.class));
		checkNameDef(v14, "s3", Type.PIXEL);
		assertNull(v14.getDimension());
		assertNull(v13.getInitializer());
		List<Statement> v15 = v4.getStatementList();
		int v16 = v15.size();
		assertEquals(0, v16);
	}

	@Test
	void test5() throws PLCException {
		String input = """
				int prog() {
				int s0.
				string s1=1.
				image s2=b.
				pixel s3= sin 90.
				}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "prog");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(4, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "s0", Type.INT);
		assertNull(v8.getDimension());
		assertNull(v7.getInitializer());
		Declaration v9 = v5.get(1);
		assertThat("", v9, instanceOf(Declaration.class));
		NameDef v10 = v9.getNameDef();
		assertThat("", v10, instanceOf(NameDef.class));
		checkNameDef(v10, "s1", Type.STRING);
		assertNull(v10.getDimension());
		Expr v11 = v9.getInitializer();
		checkNumLit(v11, 1);
		Declaration v12 = v5.get(2);
		assertThat("", v12, instanceOf(Declaration.class));
		NameDef v13 = v12.getNameDef();
		assertThat("", v13, instanceOf(NameDef.class));
		checkNameDef(v13, "s2", Type.IMAGE);
		assertNull(v13.getDimension());
		Expr v14 = v12.getInitializer();
		checkIdentExpr(v14, "b");
		Declaration v15 = v5.get(3);
		assertThat("", v15, instanceOf(Declaration.class));
		NameDef v16 = v15.getNameDef();
		assertThat("", v16, instanceOf(NameDef.class));
		checkNameDef(v16, "s3", Type.PIXEL);
		assertNull(v16.getDimension());
		Expr v17 = v15.getInitializer();
		checkUnary(v17, Kind.RES_sin);
		Expr v18 = ((UnaryExpr) v17).getE();
		checkNumLit(v18, 90);
		List<Statement> v19 = v4.getStatementList();
		int v20 = v19.size();
		assertEquals(0, v20);
	}

	@Test
	void test6() throws PLCException {
		String input = """
				void prog(){
				image[30,40] aa = "url".
				int xx = aa[0,0]:red.
				}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "prog");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(2, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "aa", Type.IMAGE);
		Dimension v9 = ((NameDef) v8).getDimension();
		assertThat("", v9, instanceOf(Dimension.class));
		Expr v10 = ((Dimension) v9).getWidth();
		checkNumLit(v10, 30);
		Expr v11 = ((Dimension) v9).getHeight();
		checkNumLit(v11, 40);
		Expr v12 = v7.getInitializer();
		checkStringLit(v12, "url");
		Declaration v13 = v5.get(1);
		assertThat("", v13, instanceOf(Declaration.class));
		NameDef v14 = v13.getNameDef();
		assertThat("", v14, instanceOf(NameDef.class));
		checkNameDef(v14, "xx", Type.INT);
		assertNull(v14.getDimension());
		Expr v15 = v13.getInitializer();
		assertThat("", v15, instanceOf(UnaryExprPostfix.class));
		Expr v16 = ((UnaryExprPostfix) v15).getPrimary();
		checkIdentExpr(v16, "aa");
		PixelSelector v17 = ((UnaryExprPostfix) v15).getPixel();
		assertThat("", v17, instanceOf(PixelSelector.class));
		Expr v18 = ((PixelSelector) v17).getX();
		checkNumLit(v18, 0);
		Expr v19 = ((PixelSelector) v17).getY();
		checkNumLit(v19, 0);
		assertEquals(ColorChannel.red, ((UnaryExprPostfix) v15).getColor());
		List<Statement> v20 = v4.getStatementList();
		int v21 = v20.size();
		assertEquals(0, v21);
	}

	@Test
	void test7() throws PLCException {
		String input = """
							string p() {
							int b.
					        image [3,40] aa.
					        string c = "hello" + 3.
					        aa[x,y] = bb[y,x].
					        aa[a,r] = bb[a+90,r].

				               }

				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "p");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(3, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "b", Type.INT);
		assertNull(v8.getDimension());
		assertNull(v7.getInitializer());
		Declaration v9 = v5.get(1);
		assertThat("", v9, instanceOf(Declaration.class));
		NameDef v10 = v9.getNameDef();
		assertThat("", v10, instanceOf(NameDef.class));
		checkNameDef(v10, "aa", Type.IMAGE);
		Dimension v11 = ((NameDef) v10).getDimension();
		assertThat("", v11, instanceOf(Dimension.class));
		Expr v12 = ((Dimension) v11).getWidth();
		checkNumLit(v12, 3);
		Expr v13 = ((Dimension) v11).getHeight();
		checkNumLit(v13, 40);
		assertNull(v9.getInitializer());
		Declaration v14 = v5.get(2);
		assertThat("", v14, instanceOf(Declaration.class));
		NameDef v15 = v14.getNameDef();
		assertThat("", v15, instanceOf(NameDef.class));
		checkNameDef(v15, "c", Type.STRING);
		assertNull(v15.getDimension());
		Expr v16 = v14.getInitializer();

		checkBinary(v16, Kind.PLUS);
		Expr v17 = ((BinaryExpr) v16).getLeft();
		checkStringLit(v17, "hello");
		Expr v18 = ((BinaryExpr) v16).getRight();
		checkNumLit(v18, 3);
		List<Statement> v19 = v4.getStatementList();
		int v20 = v19.size();
		assertEquals(2, v20);
		Statement v21 = v19.get(0);
		assertThat("", v21, instanceOf(AssignmentStatement.class));
		LValue v22 = ((AssignmentStatement) v21).getLv();
		assertThat("", v22, instanceOf(LValue.class));
		Ident v23 = v22.getIdent();
		checkIdent(v23, "aa");
		PixelSelector v24 = v22.getPixelSelector();
		assertThat("", v24, instanceOf(PixelSelector.class));
		Expr v25 = ((PixelSelector) v24).getX();
		assertThat("", v25, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_x, ((PredeclaredVarExpr) v25).getKind());
		Expr v26 = ((PixelSelector) v24).getY();
		assertThat("", v26, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_y, ((PredeclaredVarExpr) v26).getKind());
		assertNull(((LValue) v22).getColor());
		Expr v27 = ((AssignmentStatement) v21).getE();
		assertThat("", v27, instanceOf(UnaryExprPostfix.class));
		Expr v28 = ((UnaryExprPostfix) v27).getPrimary();
		checkIdentExpr(v28, "bb");
		PixelSelector v29 = ((UnaryExprPostfix) v27).getPixel();
		assertThat("", v29, instanceOf(PixelSelector.class));
		Expr v30 = ((PixelSelector) v29).getX();
		assertThat("", v30, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_y, ((PredeclaredVarExpr) v30).getKind());
		Expr v31 = ((PixelSelector) v29).getY();
		assertThat("", v31, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_x, ((PredeclaredVarExpr) v31).getKind());
		assertNull(((UnaryExprPostfix) v27).getColor());
		Statement v32 = v19.get(1);
		assertThat("", v32, instanceOf(AssignmentStatement.class));
		LValue v33 = ((AssignmentStatement) v32).getLv();
		assertThat("", v33, instanceOf(LValue.class));
		Ident v34 = v33.getIdent();
		checkIdent(v34, "aa");
		PixelSelector v35 = v33.getPixelSelector();
		assertThat("", v35, instanceOf(PixelSelector.class));
		Expr v36 = ((PixelSelector) v35).getX();
		assertThat("", v36, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_a, ((PredeclaredVarExpr) v36).getKind());
		Expr v37 = ((PixelSelector) v35).getY();
		assertThat("", v37, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_r, ((PredeclaredVarExpr) v37).getKind());
		assertNull(((LValue) v33).getColor());
		Expr v38 = ((AssignmentStatement) v32).getE();
		assertThat("", v38, instanceOf(UnaryExprPostfix.class));
		Expr v39 = ((UnaryExprPostfix) v38).getPrimary();
		checkIdentExpr(v39, "bb");
		PixelSelector v40 = ((UnaryExprPostfix) v38).getPixel();
		assertThat("", v40, instanceOf(PixelSelector.class));
		Expr v41 = ((PixelSelector) v40).getX();

		checkBinary(v41, Kind.PLUS);
		Expr v42 = ((BinaryExpr) v41).getLeft();
		assertThat("", v42, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_a, ((PredeclaredVarExpr) v42).getKind());
		Expr v43 = ((BinaryExpr) v41).getRight();
		checkNumLit(v43, 90);
		Expr v44 = ((PixelSelector) v40).getY();
		assertThat("", v44, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_r, ((PredeclaredVarExpr) v44).getKind());
		assertNull(((UnaryExprPostfix) v38).getColor());
	}

	@Test
	void test8() throws PLCException {
		String input = """
					image jj()
					{
						string[34,80] bb = "jello".
					}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "jj");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(1, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "bb", Type.STRING);
		Dimension v9 = ((NameDef) v8).getDimension();
		assertThat("", v9, instanceOf(Dimension.class));
		Expr v10 = ((Dimension) v9).getWidth();
		checkNumLit(v10, 34);
		Expr v11 = ((Dimension) v9).getHeight();
		checkNumLit(v11, 80);
		Expr v12 = v7.getInitializer();
		checkStringLit(v12, "jello");
		List<Statement> v13 = v4.getStatementList();
		int v14 = v13.size();
		assertEquals(0, v14);
	}

	@Test
	void test9() throws PLCException {
		String input = """
				void p(){
				   int z.
				   while z {
				       while m {
				          string z.
				       }.
				    }.
				    }
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "p");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(1, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "z", Type.INT);
		assertNull(v8.getDimension());
		assertNull(v7.getInitializer());
		List<Statement> v9 = v4.getStatementList();
		int v10 = v9.size();
		assertEquals(1, v10);
		Statement v11 = v9.get(0);
		assertThat("", v11, instanceOf(WhileStatement.class));
		Expr v12 = ((WhileStatement) v11).getGuard();
		checkIdentExpr(v12, "z");
		Block v13 = ((WhileStatement) v11).getBlock();
		assertThat("", v13, instanceOf(Block.class));
		List<Declaration> v14 = v13.getDecList();
		int v15 = v14.size();
		assertEquals(0, v15);
		List<Statement> v16 = v13.getStatementList();
		int v17 = v16.size();
		assertEquals(1, v17);
		Statement v18 = v16.get(0);
		assertThat("", v18, instanceOf(WhileStatement.class));
		Expr v19 = ((WhileStatement) v18).getGuard();
		checkIdentExpr(v19, "m");
		Block v20 = ((WhileStatement) v18).getBlock();
		assertThat("", v20, instanceOf(Block.class));
		List<Declaration> v21 = v20.getDecList();
		int v22 = v21.size();
		assertEquals(1, v22);
		Declaration v23 = v21.get(0);
		assertThat("", v23, instanceOf(Declaration.class));
		NameDef v24 = v23.getNameDef();
		assertThat("", v24, instanceOf(NameDef.class));
		checkNameDef(v24, "z", Type.STRING);
		assertNull(v24.getDimension());
		assertNull(v23.getInitializer());
		List<Statement> v25 = v20.getStatementList();
		int v26 = v25.size();
		assertEquals(0, v26);
	}

	@Test
	void test10() throws PLCException {
		String input = """
				image gen(){
				pixel p = [33,44,56].
				}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "gen");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(1, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "p", Type.PIXEL);
		assertNull(v8.getDimension());
		Expr v9 = v7.getInitializer();
		assertThat("", v9, instanceOf(ExpandedPixelExpr.class));
		Expr v10 = ((ExpandedPixelExpr) v9).getRedExpr();
		checkNumLit(v10, 33);
		Expr v11 = ((ExpandedPixelExpr) v9).getGrnExpr();
		checkNumLit(v11, 44);
		Expr v12 = ((ExpandedPixelExpr) v9).getBluExpr();
		checkNumLit(v12, 56);
		List<Statement> v13 = v4.getStatementList();
		int v14 = v13.size();
		assertEquals(0, v14);
	}

	@Test
	void test11() throws PLCException {
		String input = """
				string s(){
				image j.
				j[x,y] = k[x_cart [a,r], y_cart [a,r]].
				j[a,r] = k[a_polar [x,y], r_polar [y,x]].
				}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "s");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(1, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "j", Type.IMAGE);
		assertNull(v8.getDimension());
		assertNull(v7.getInitializer());
		List<Statement> v9 = v4.getStatementList();
		int v10 = v9.size();
		assertEquals(2, v10);
		Statement v11 = v9.get(0);
		assertThat("", v11, instanceOf(AssignmentStatement.class));
		LValue v12 = ((AssignmentStatement) v11).getLv();
		assertThat("", v12, instanceOf(LValue.class));
		Ident v13 = v12.getIdent();
		checkIdent(v13, "j");
		PixelSelector v14 = v12.getPixelSelector();
		assertThat("", v14, instanceOf(PixelSelector.class));
		Expr v15 = ((PixelSelector) v14).getX();
		assertThat("", v15, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_x, ((PredeclaredVarExpr) v15).getKind());
		Expr v16 = ((PixelSelector) v14).getY();
		assertThat("", v16, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_y, ((PredeclaredVarExpr) v16).getKind());
		assertNull(((LValue) v12).getColor());
		Expr v17 = ((AssignmentStatement) v11).getE();
		assertThat("", v17, instanceOf(UnaryExprPostfix.class));
		Expr v18 = ((UnaryExprPostfix) v17).getPrimary();
		checkIdentExpr(v18, "k");
		PixelSelector v19 = ((UnaryExprPostfix) v17).getPixel();
		assertThat("", v19, instanceOf(PixelSelector.class));
		Expr v20 = ((PixelSelector) v19).getX();
		assertThat("", v20, instanceOf(PixelFuncExpr.class));
		assertEquals(RES_x_cart, ((PixelFuncExpr) v20).getFunction());
		PixelSelector v21 = ((PixelFuncExpr) v20).getSelector();
		assertThat("", v21, instanceOf(PixelSelector.class));
		Expr v22 = ((PixelSelector) v21).getX();
		assertThat("", v22, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_a, ((PredeclaredVarExpr) v22).getKind());
		Expr v23 = ((PixelSelector) v21).getY();
		assertThat("", v23, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_r, ((PredeclaredVarExpr) v23).getKind());
		Expr v24 = ((PixelSelector) v19).getY();
		assertThat("", v24, instanceOf(PixelFuncExpr.class));
		assertEquals(RES_y_cart, ((PixelFuncExpr) v24).getFunction());
		PixelSelector v25 = ((PixelFuncExpr) v24).getSelector();
		assertThat("", v25, instanceOf(PixelSelector.class));
		Expr v26 = ((PixelSelector) v25).getX();
		assertThat("", v26, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_a, ((PredeclaredVarExpr) v26).getKind());
		Expr v27 = ((PixelSelector) v25).getY();
		assertThat("", v27, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_r, ((PredeclaredVarExpr) v27).getKind());
		assertNull(((UnaryExprPostfix) v17).getColor());
		Statement v28 = v9.get(1);
		assertThat("", v28, instanceOf(AssignmentStatement.class));
		LValue v29 = ((AssignmentStatement) v28).getLv();
		assertThat("", v29, instanceOf(LValue.class));
		Ident v30 = v29.getIdent();
		checkIdent(v30, "j");
		PixelSelector v31 = v29.getPixelSelector();
		assertThat("", v31, instanceOf(PixelSelector.class));
		Expr v32 = ((PixelSelector) v31).getX();
		assertThat("", v32, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_a, ((PredeclaredVarExpr) v32).getKind());
		Expr v33 = ((PixelSelector) v31).getY();
		assertThat("", v33, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_r, ((PredeclaredVarExpr) v33).getKind());
		assertNull(((LValue) v29).getColor());
		Expr v34 = ((AssignmentStatement) v28).getE();
		assertThat("", v34, instanceOf(UnaryExprPostfix.class));
		Expr v35 = ((UnaryExprPostfix) v34).getPrimary();
		checkIdentExpr(v35, "k");
		PixelSelector v36 = ((UnaryExprPostfix) v34).getPixel();
		assertThat("", v36, instanceOf(PixelSelector.class));
		Expr v37 = ((PixelSelector) v36).getX();
		assertThat("", v37, instanceOf(PixelFuncExpr.class));
		assertEquals(RES_a_polar, ((PixelFuncExpr) v37).getFunction());
		PixelSelector v38 = ((PixelFuncExpr) v37).getSelector();
		assertThat("", v38, instanceOf(PixelSelector.class));
		Expr v39 = ((PixelSelector) v38).getX();
		assertThat("", v39, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_x, ((PredeclaredVarExpr) v39).getKind());
		Expr v40 = ((PixelSelector) v38).getY();
		assertThat("", v40, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_y, ((PredeclaredVarExpr) v40).getKind());
		Expr v41 = ((PixelSelector) v36).getY();
		assertThat("", v41, instanceOf(PixelFuncExpr.class));
		assertEquals(RES_r_polar, ((PixelFuncExpr) v41).getFunction());
		PixelSelector v42 = ((PixelFuncExpr) v41).getSelector();
		assertThat("", v42, instanceOf(PixelSelector.class));
		Expr v43 = ((PixelSelector) v42).getX();
		assertThat("", v43, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_y, ((PredeclaredVarExpr) v43).getKind());
		Expr v44 = ((PixelSelector) v42).getY();
		assertThat("", v44, instanceOf(PredeclaredVarExpr.class));
		assertEquals(RES_x, ((PredeclaredVarExpr) v44).getKind());
		assertNull(((UnaryExprPostfix) v34).getColor());
	}

	@Test
	void test12() throws PLCException {
		String input = """
				string s(){
				int xx = rand.
				int yy = Z.
				}
				""";
		AST ast = getAST(input);
		assertThat("", ast, instanceOf(Program.class));
		Program v0 = (Program) ast;
		Ident v1 = v0.getIdent();
		checkIdent(v1, "s");
		List<NameDef> v2 = v0.getParamList();
		int v3 = v2.size();
		assertEquals(0, v3);
		Block v4 = v0.getBlock();
		assertThat("", v4, instanceOf(Block.class));
		List<Declaration> v5 = v4.getDecList();
		int v6 = v5.size();
		assertEquals(2, v6);
		Declaration v7 = v5.get(0);
		assertThat("", v7, instanceOf(Declaration.class));
		NameDef v8 = v7.getNameDef();
		assertThat("", v8, instanceOf(NameDef.class));
		checkNameDef(v8, "xx", Type.INT);
		assertNull(v8.getDimension());
		Expr v9 = v7.getInitializer();
		assertThat("", v9, instanceOf(RandomExpr.class));
		Declaration v10 = v5.get(1);
		assertThat("", v10, instanceOf(Declaration.class));
		NameDef v11 = v10.getNameDef();
		assertThat("", v11, instanceOf(NameDef.class));
		checkNameDef(v11, "yy", Type.INT);
		assertNull(v11.getDimension());
		Expr v12 = v10.getInitializer();
		assertThat("", v12, instanceOf(ZExpr.class));
		List<Statement> v13 = v4.getStatementList();
		int v14 = v13.size();
		assertEquals(0, v14);
	}

	@Test
	void test13() throws PLCException {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
					prog s(){
					xx = 22
					}
					""";
			assertThrows(SyntaxException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

	@Test
	void test14() throws PLCException {
		assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
			String input = """
					prog s(){
					xx = 22;
					}
					""";
			assertThrows(SyntaxException.class, () -> {
				@SuppressWarnings("unused")
				AST ast = getAST(input);
			});
		});
	}

}
