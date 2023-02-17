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
}

