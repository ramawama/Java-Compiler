/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */

package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.ast.*;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class Assignment2Test_starter {
	
	
	/** Indicates whether show should generate output*/
	static final boolean VERBOSE = true;

	/**
	 * Prints obj to console if VERBOSE.  This is easier to type than System.out.println and makes it easy to disable output. 
	 * 
	 * @param obj
	 */
	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}
	
	/** Constructs a scanner and parser for the given input string, scans and parses the input and returns and AST. 
	 * 
	 * @param input   String representing program to be tested
	 * @return  AST representing the program
	 * @throws PLCException
	 */
	AST getAST(String input) throws  PLCException {
		return  CompilerComponentFactory.makeAssignment2Parser(input).parse();
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
	 * @param_name
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
	 * @param_op  Kind of expected operator
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
	IdentExpr checkIdent(AST e, String name) {
		assertThat("",e, instanceOf( IdentExpr.class));
		IdentExpr ident = (IdentExpr)e;
		assertEquals(name,ident.getName());
		return ident;		
	}
		
	@Test
	void emptyProgram() throws PLCException {
		String input = "";  //no empty expressions, this program should throw a SyntaxException
		assertThrows(SyntaxException.class, () -> {
			getAST(input);
		});
	}
	
	@Test
	void numLit() throws PLCException {
		String input= "3";
		checkNumLit(getAST(input),3);
	}
	
	@Test
	void stringLit() throws PLCException {
		String input= "\"Go Gators\" ";
		checkStringLit(getAST(input), "Go Gators");
	}
	
	@Test 
	void Z() throws PLCException {
		String input = " Z  ";
		AST e = getAST(input);
		assertThat("",e, instanceOf( ZExpr.class));
	}
	
	@Test
	void rand() throws PLCException {
		String input = "  rand";
		Expr e = (Expr) getAST(input);
		assertEquals(1,e.getLine());
		assertEquals(3, e.getColumn());
		assertThat("",e, instanceOf( RandomExpr.class));
	}
	
	@Test
	void primary() throws PLCException {
		String input = " (3) ";
		Expr e = (Expr) getAST(input);
		checkNumLit(e,3);
	}
	


@Test
void unary1() 
	throws PLCException {
		String input = " -3 ";
		UnaryExpr ue = checkUnary(getAST(input), Kind.MINUS);
		checkNumLit(ue.getE(),3);
	}



@Test
void unary2() 
	throws PLCException {
		String input = " cos atan ! - \"hello\" ";	
		UnaryExpr ue0 = checkUnary(getAST(input), Kind.RES_cos);
		UnaryExpr ue1 = checkUnary(ue0.getE(), Kind.RES_atan);
		UnaryExpr ue2 = checkUnary(ue1.getE(),Kind.BANG);
		UnaryExpr ue3 = checkUnary(ue2.getE(), Kind.MINUS);	
		checkStringLit(ue3.getE(), "hello");
	}

@Test void ident() throws PLCException {
	String input = "b";
	checkIdent(getAST(input),"b");
}

@Test void binary0() throws PLCException {
	String input = "b+2";
	BinaryExpr binary = checkBinary(getAST(input),Kind.PLUS);
	checkIdent(binary.getLeft(),"b");
	checkNumLit(binary.getRight(),2);
}
@Test void binary1() throws PLCException {
	String input = "1-2+3*4/5%6";  //   (1-2) +  (((3  * 4)  /  5) % 6)

	BinaryExpr be0 = checkBinary(getAST(input), Kind.PLUS); // (1-2) + (3*4/5%6)

	BinaryExpr be0l = checkBinary(be0.getLeft(),Kind.MINUS); // 1-2
	checkNumLit(be0l.getLeft(),1);
	checkNumLit(be0l.getRight(),2);

	BinaryExpr be0r = checkBinary(be0.getRight(),Kind.MOD);  //(3*4/5)%6
	checkNumLit(be0r.getRight(),6);

	BinaryExpr be0rl = checkBinary(be0r.getLeft(),Kind.DIV );  //(3*4)/5
	checkNumLit(be0rl.getRight(),5);  // 5
		
    BinaryExpr be0rll = checkBinary(be0rl.getLeft(), Kind.TIMES); // 3*4
	checkNumLit(be0rll.getLeft(),3);
	checkNumLit(be0rll.getRight(),4);
}

@Test void conditional0() throws PLCException {
	String input = " if d ? e ? f";
	ConditionalExpr ce = checkConditional(getAST(input));
	checkIdent(ce.getGuard(),"d");
	checkIdent(ce.getTrueCase(),"e");
	checkIdent(ce.getFalseCase(),"f");
}



@Test void conditional1() throws PLCException {
	String input = """
			if if 3 ? 4 ? 5 ? if 6 ? 7 ? 8 ? if 9 ? 10 ? 11
			""";
	ConditionalExpr ce = checkConditional(getAST(input));
	ConditionalExpr guard = checkConditional(ce.getGuard());
	ConditionalExpr trueCase = checkConditional(ce.getTrueCase());
	ConditionalExpr falseCase = checkConditional(ce.getFalseCase());
	
	checkNumLit(guard.getGuard(),3);
	checkNumLit(guard.getTrueCase(),4);
	checkNumLit(guard.getFalseCase(),5);
	
	checkNumLit(trueCase.getGuard(),6);
	checkNumLit(trueCase.getTrueCase(),7);
	checkNumLit(trueCase.getFalseCase(),8);
	
	checkNumLit(falseCase.getGuard(),9);
	checkNumLit(falseCase.getTrueCase(),10);
	checkNumLit(falseCase.getFalseCase(),11);
}


@Test void error0() throws PLCException {
	String input = "b + + 2";
	assertThrows(SyntaxException.class, () -> {
		getAST(input);
	});
}

@Test void error1() throws PLCException {
	String input = "3 @ 4"; //this should throw a LexicalException
	assertThrows(LexicalException.class, () -> {
		getAST(input);
	});
}

	@Test
	void andPowerExpressions() throws PLCException {
		String input = " 2 ** 3 ** 5 "; // 2 ** (3 ** 5)
		BinaryExpr be0 = checkBinary(getAST(input), Kind.EXP);

		checkNumLit(be0.getLeft(), 2);
		BinaryExpr be1 = checkBinary(be0.getRight(), Kind.EXP);
		checkNumLit(be1.getLeft(), 3);
		checkNumLit(be1.getRight(), 5);
	}

	@Test
	void andParentheses() throws PLCException {
		String input = " ( 7 ** 11 ) ** 2 ** 3 ** 5 "; // ( 7 ** 11 ) ** (2 ** (3 ** 5))
		BinaryExpr be0 = checkBinary(getAST(input), Kind.EXP);
		BinaryExpr bel1 = checkBinary(be0.getLeft(), Kind.EXP);
		checkNumLit(bel1.getLeft(), 7);
		checkNumLit(bel1.getRight(), 11);
		BinaryExpr ber1 = checkBinary(be0.getRight(), Kind.EXP);
		checkNumLit(ber1.getLeft(), 2);
		BinaryExpr berr2 = checkBinary(ber1.getRight(), Kind.EXP);
		checkNumLit(berr2.getLeft(), 3);
		checkNumLit(berr2.getRight(), 5);
	}

	@Test
	void andMismatchedParentheses() throws PLCException {
		String input = " (((oh)) ";
		assertThrows(SyntaxException.class, () -> {
			getAST(input);
		});
	}

	@Test
	void andDeepParentheses() throws PLCException {
		String input = " ((((((((1)))))))) ";
		AST e = getAST(input);
		checkNumLit(e, 1);
	}

	@Test
	void andUnaryChain() throws PLCException {
		String input = " !-atan!--!!cos sin love";
		UnaryExpr u1 = checkUnary(getAST(input), Kind.BANG);
		UnaryExpr u2 = checkUnary(u1.getE(), Kind.MINUS);
		UnaryExpr u3 = checkUnary(u2.getE(), Kind.RES_atan);
		UnaryExpr u4 = checkUnary(u3.getE(), Kind.BANG);
		UnaryExpr u5 = checkUnary(u4.getE(), Kind.MINUS);
		UnaryExpr u6 = checkUnary(u5.getE(), Kind.MINUS);
		UnaryExpr u7 = checkUnary(u6.getE(), Kind.BANG);
		UnaryExpr u8 = checkUnary(u7.getE(), Kind.BANG);
		UnaryExpr u9 = checkUnary(u8.getE(), Kind.RES_cos);
		UnaryExpr u10 = checkUnary(u9.getE(), Kind.RES_sin);
		checkIdent(u10.getE(), "love");
	}

	@Test
	void andAMixOfOperators() throws PLCException {
		String input = " !1 + -2 - -3 * atan 4 ** 5";
		BinaryExpr e0 = checkBinary(getAST(input), Kind.EXP);
		checkNumLit(e0.getRight(), 5);
		BinaryExpr el1 = checkBinary(e0.getLeft(), Kind.MINUS);
		BinaryExpr ell2 = checkBinary(el1.getLeft(), Kind.PLUS);
		UnaryExpr elll3 = checkUnary(ell2.getLeft(), Kind.BANG);
		checkNumLit(elll3.getE(), 1);
		UnaryExpr ellr3 = checkUnary(ell2.getRight(), Kind.MINUS);
		checkNumLit(ellr3.getE(), 2);
		BinaryExpr elr2 = checkBinary(el1.getRight(), Kind.TIMES);
		UnaryExpr elrl3 = checkUnary(elr2.getLeft(), Kind.MINUS);
		checkNumLit(elrl3.getE(), 3);
		UnaryExpr elrr3 = checkUnary(elr2.getRight(), Kind.RES_atan);
		checkNumLit(elrr3.getE(), 4);
	}

	@Test
	void andLogicalOperators() throws PLCException {
		String input = "1 || (if 2 && 3 ? 4 || 5 ? 6 || 7 && 8 && 9) && 10";
		BinaryExpr e0 = checkBinary(getAST(input), Kind.OR);
		checkNumLit(e0.getLeft(), 1);
		BinaryExpr er1 = checkBinary(e0.getRight(), Kind.AND);
		checkNumLit(er1.getRight(), 10);
		ConditionalExpr erl2 = checkConditional(er1.getLeft());
		BinaryExpr erlg3 = checkBinary(erl2.getGuard(), Kind.AND);
		checkNumLit(erlg3.getLeft(), 2);
		checkNumLit(erlg3.getRight(), 3);
		BinaryExpr erlt3 = checkBinary(erl2.getTrueCase(), Kind.OR);
		checkNumLit(erlt3.getLeft(), 4);
		checkNumLit(erlt3.getRight(), 5);
		BinaryExpr erlf3 = checkBinary(erl2.getFalseCase(), Kind.OR);
		checkNumLit(erlf3.getLeft(), 6);
		BinaryExpr erlfr4 = checkBinary(erlf3.getRight(), Kind.AND);
		checkNumLit(erlfr4.getRight(), 9);
		BinaryExpr erlfrl5 = checkBinary(erlfr4.getLeft(), Kind.AND);
		checkNumLit(erlfrl5.getLeft(), 7);
		checkNumLit(erlfrl5.getRight(), 8);
	}

	@Test
	void andSomeSentence() throws PLCException {
		String input = """
			if youre- atan person | see?
			you & me? ~us?
			we- sin together ~<3
			""";
		ConditionalExpr c = checkConditional(getAST(input));
		BinaryExpr cg = checkBinary(c.getGuard(), Kind.BITOR);
		checkIdent(cg.getRight(), "see");
		BinaryExpr cgl = checkBinary(cg.getLeft(), Kind.MINUS);
		checkIdent(cgl.getLeft(), "youre");
		UnaryExpr cglr = checkUnary(cgl.getRight(), Kind.RES_atan);
		checkIdent(cglr.getE(), "person");
		BinaryExpr ct = checkBinary(c.getTrueCase(), Kind.BITAND);
		checkIdent(ct.getLeft(), "you");
		checkIdent(ct.getRight(), "me");
		BinaryExpr cf = checkBinary(c.getFalseCase(), Kind.MINUS);
		checkIdent(cf.getLeft(), "we");
		UnaryExpr cfr = checkUnary(cf.getRight(), Kind.RES_sin);
		checkIdent(cfr.getE(), "together");
	}
	@Test
	void primary1() throws PLCException {
		String input = " (b) "; // Ident
		Expr e = (Expr) getAST(input);
		checkIdent(e, "b");
	}
}

