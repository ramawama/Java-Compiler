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
import edu.ufl.cise.plcsp23.IToken.SourceLocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestScanner_starter {

	// makes it easy to turn output on and off (and less typing than
	// System.out.println)
	static final boolean VERBOSE = true;

	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}

	// check that this token has the expected kind
	void checkToken(Kind expectedKind, IToken t) {
		assertEquals(expectedKind, t.getKind());
	}
	
	void checkToken(Kind expectedKind, String expectedChars, SourceLocation expectedLocation, IToken t) {
		assertEquals(expectedKind, t.getKind());
		assertEquals(expectedChars, t.getTokenString());
		assertEquals(expectedLocation, t.getSourceLocation());
		;
	}

	void checkIdent(String expectedChars, IToken t) {
		checkToken(Kind.IDENT, t);
		assertEquals(expectedChars.intern(), t.getTokenString().intern());
		;
	}

	void checkString(String expectedValue, IToken t) {
		assertTrue(t instanceof IStringLitToken);
		assertEquals(expectedValue, ((IStringLitToken) t).getValue());
	}

	void checkString(String expectedChars, String expectedValue, SourceLocation expectedLocation, IToken t) {
		assertTrue(t instanceof IStringLitToken);
		assertEquals(expectedValue, ((IStringLitToken) t).getValue());
		assertEquals(expectedChars, t.getTokenString());
		assertEquals(expectedLocation, t.getSourceLocation());
	}

	void checkNUM_LIT(int expectedValue, IToken t) {
		checkToken(Kind.NUM_LIT, t);
		int value = ((INumLitToken)t).getValue();
		assertEquals(expectedValue, value); //t.getTokenString()
	}
	
	void checkNUM_LIT(int expectedValue, SourceLocation expectedLocation, IToken t) {
		checkToken(Kind.NUM_LIT, t);
		int value = ((INumLitToken) t).getValue();
		assertEquals(expectedValue, value);
		assertEquals(expectedLocation, t.getSourceLocation());
	}

	void checkTokens(IScanner s, IToken.Kind... kinds) throws LexicalException {
		for (IToken.Kind kind : kinds) {
			checkToken(kind, s.next());
		}
	}

	void checkTokens(String input, IToken.Kind... kinds) throws LexicalException {
		IScanner s = CompilerComponentFactory.makeScanner(input);
		for (IToken.Kind kind : kinds) {
			checkToken(kind, s.next());
		}
	}

	// check that this token is the EOF token
	void checkEOF(IToken t) {
		checkToken(Kind.EOF, t);
	}


	@Test
	void emptyProg() throws LexicalException {
		String input = "";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkEOF(scanner.next());
	}

	@Test
	void onlyWhiteSpace() throws LexicalException {
		String input = " \t \r\n \f \n";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkEOF(scanner.next());
		checkEOF(scanner.next());  //repeated invocations of next after end reached should return EOF token
	}

	@Test
	void numLits1() throws LexicalException {
		String input = """
				123
				05 240
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkNUM_LIT(123, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(5, scanner.next());
		checkNUM_LIT(240, scanner.next());
		checkEOF(scanner.next());
	}
	
	@Test
	//Too large should still throw LexicalException
	void numLitTooBig() throws LexicalException {
		String input = "999999999999999999999";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}


	@Test
	void identsAndReserved() throws LexicalException {
		String input = """
				i0
				  i1  x ~~~2 spaces at beginning and after il
				y Y
				""";

		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT,"i0", new SourceLocation(1,1), scanner.next());
		checkToken(Kind.IDENT, "i1",new SourceLocation(2,3), scanner.next());
		checkToken(Kind.RES_x, "x", new SourceLocation(2,7), scanner.next());		
		checkToken(Kind.RES_y, "y", new SourceLocation(3,1), scanner.next());
		checkToken(Kind.RES_Y, "Y", new SourceLocation(3,3), scanner.next());
		checkEOF(scanner.next());
	}
	

	@Test
	void operators0() throws LexicalException {
		String input = """
				==
				+
				/
				====
				=
				===
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.PLUS, scanner.next());
		checkToken(Kind.DIV, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.ASSIGN, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.ASSIGN, scanner.next());
		checkEOF(scanner.next());
	}

	@Test
	void equals() throws LexicalException{
		String input = """
				==
				== ==
				==*==
				*==+
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.TIMES, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.TIMES, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.PLUS, scanner.next());
	}


	@Test
	void stringLiterals1() throws LexicalException {
		String input = """
				"hello"
				"\t"
				"\\""
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString(input.substring(0, 7),"hello", new SourceLocation(1,1), scanner.next());
		checkString(input.substring(8, 11), "\t", new SourceLocation(2,1), scanner.next());
		checkString(input.substring(12, 16), "\"",  new SourceLocation(3,1), scanner.next());
		checkEOF(scanner.next());
	}

	@Test
	void stringLiterals2() throws LexicalException {
		String input = """
				"hello" "hi"
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString(input.substring(0, 7),"hello", new SourceLocation(1,1), scanner.next());
		checkString(input.substring(8, 12),"hi", new SourceLocation(1,10), scanner.next());
		checkEOF(scanner.next());
	}

	//new tests
	@Test
	void stringEscape() throws LexicalException {
		String input = """
			"\\b \\t \\n \\r \\" \\\\"
			""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString(input.substring(0, 19),"\b \t \n \r \" \\\"", new SourceLocation(1,1), scanner.next());
		checkEOF(scanner.next());
	}

	@Test
	void identsWithUnderscore() throws LexicalException{
		String input = """
				i0
				i1
				_
				__
				a_b_c
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.IDENT,"i0", new SourceLocation(1,1), scanner.next());
		checkToken(Kind.IDENT,"i1", new SourceLocation(2,1), scanner.next());
		checkToken(Kind.IDENT,"_", new SourceLocation(3,1), scanner.next());
		checkToken(Kind.IDENT,"__", new SourceLocation(4,1), scanner.next());
		checkToken(Kind.IDENT,"a_b_c", new SourceLocation(5,1), scanner.next());
	}

	@Test
	void nonTerminatedString() throws LexicalException{
		String input = """
				"abc
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		//checkString(input.substring(0, 3),"abc", new SourceLocation(1,1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	@Test
	void illegalEscape() throws LexicalException {
		String input = """
				"\\t"
				"\\k"
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\t\"","\t", new SourceLocation(1,1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	@Test
	void escapeSeq() throws LexicalException {
		String input = """
				"\\t"
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\t\"","\t", new SourceLocation(1,1), scanner.next());

	}
	
	@Test
	void illegalLineTermInStringLiteral() throws LexicalException {
		String input = """
				"\\n"  ~ this one passes the escape sequence--it is OK
				"\n"   ~ this on passes the LF, it is illegal.
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\n\"","\n", new SourceLocation(1,1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	@Test
	void lessThanGreaterThanExchange() throws LexicalException {
		String input = """
				<->>>>=
				<<=<
				""";
		checkTokens(input, Kind.EXCHANGE, Kind.GT, Kind.GT, Kind.GE, Kind.LT, Kind.LE, Kind.LT, Kind.EOF);
	}

	@Test
	void lessThanExchange() throws LexicalException {
		String input = """
				<<=<->
				""";
		checkTokens(input, Kind.LT, Kind.LE, Kind.EXCHANGE, Kind.EOF);
	}
	
	/** The Scanner should not backtrack so this input should throw an exception */
	@Test
	void incompleteExchangeThrowsException() throws LexicalException {
		String input = " <- ";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});	
	}

	@Test
	void illegalChar() throws LexicalException {
		String input = """
				abc
				@
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkIdent("abc", scanner.next());
		assertThrows(LexicalException.class, () -> {
			@SuppressWarnings("unused")
			IToken t = scanner.next();
		});
	}
	@Test
	void specialPunctuation() throws LexicalException{
		String input = "! & && | || 123";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.BANG, scanner.next());
		checkToken(Kind.BITAND, scanner.next());
		checkToken(Kind.AND, scanner.next());
		checkToken(Kind.BITOR, scanner.next());
		checkToken(Kind.OR, scanner.next());

	}
	@Test
	void bitOR() throws LexicalException{
		String input = "| ||";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.BITOR, scanner.next());
		checkToken(Kind.OR, scanner.next());
	}

	@Test
	void andNothingButComments() throws LexicalException {
		String input = """
            ~jerry
            ~can
            ~move
            ~if
            ~he's
            ~not
            ~@#$%&#^%&@
            ~tired
            """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkEOF(scanner.next());
	}

	@Test
	void andNumLitsZeroes() throws LexicalException {
		String input = """
            000
            00
            001
            10 0
            """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(1, scanner.next());
		checkNUM_LIT(10, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkEOF(scanner.next());
	}

	@Test
	void andIdentsWithNumLits() throws LexicalException {
		String input = """
            0f0f0
            12if21
            12if 21
            00 if 12
            """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkNUM_LIT(0, scanner.next());
		checkToken(Kind.IDENT, "f0f0", new SourceLocation(1, 2), scanner.next());
		checkNUM_LIT(12, scanner.next());
		checkToken(Kind.IDENT, "if21", new SourceLocation(2, 3), scanner.next());
		checkNUM_LIT(12, scanner.next());
		checkToken(Kind.RES_if, "if", new SourceLocation(3, 3), scanner.next());
		checkNUM_LIT(21, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkNUM_LIT(0, scanner.next());
		checkToken(Kind.RES_if, "if", new SourceLocation(4, 4), scanner.next());
		checkNUM_LIT(12, scanner.next());
		checkEOF(scanner.next());
	}

	@Test
	void andOperators() throws LexicalException {
		String input = """
            =&&
            *****
            ~====
            ||?:,|
            """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.ASSIGN, scanner.next());
		checkToken(Kind.AND, scanner.next());
		checkToken(Kind.EXP, scanner.next());
		checkToken(Kind.EXP, scanner.next());
		checkToken(Kind.TIMES, scanner.next());
		checkToken(Kind.OR, scanner.next());
		checkToken(Kind.QUESTION, scanner.next());
		checkToken(Kind.COLON, scanner.next());
		checkToken(Kind.COMMA, scanner.next());
		checkToken(Kind.BITOR, scanner.next());
		checkEOF(scanner.next());
	}

	@Test
	void andEmptyStrings() throws LexicalException {
		String input = """
            \"\"\"\"\"\"\"
            """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("", scanner.next());
		checkString("", scanner.next());
		checkString("", scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

	@Test
	void allOperatorsAndSeparators() throws LexicalException {
		/* Operators and Separators . | , | ? | : | ( | ) | < | > | [ | ] | { | } | = | == | <-> | <= | >= | ! | & | && | | | || |
+ | - | * | ** | / | % */
		String input = """
				. , ? : ( ) < > [ ] { } = == <-> <= >= ! & && | || + - * ** / %
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkToken(Kind.DOT, scanner.next());
		checkToken(Kind.COMMA, scanner.next());
		checkToken(Kind.QUESTION, scanner.next());
		checkToken(Kind.COLON, scanner.next());
		checkToken(Kind.LPAREN, scanner.next());
		checkToken(Kind.RPAREN, scanner.next());
		checkToken(Kind.LT, scanner.next());
		checkToken(Kind.GT, scanner.next());
		checkToken(Kind.LSQUARE, scanner.next());
		checkToken(Kind.RSQUARE, scanner.next());
		checkToken(Kind.LCURLY, scanner.next());
		checkToken(Kind.RCURLY, scanner.next());
		checkToken(Kind.ASSIGN, scanner.next());
		checkToken(Kind.EQ, scanner.next());
		checkToken(Kind.EXCHANGE, scanner.next());
		checkToken(Kind.LE, scanner.next());
		checkToken(Kind.GE, scanner.next());
		checkToken(Kind.BANG, scanner.next());
		checkToken(Kind.BITAND, scanner.next());
		checkToken(Kind.AND, scanner.next());
		checkToken(Kind.BITOR, scanner.next());
		checkToken(Kind.OR, scanner.next());
		checkToken(Kind.PLUS, scanner.next());
		checkToken(Kind.MINUS, scanner.next());
		checkToken(Kind.TIMES, scanner.next());
		checkToken(Kind.EXP, scanner.next());
		checkToken(Kind.DIV, scanner.next());
		checkToken(Kind.MOD, scanner.next());
	}

	@Test
	void stringLiteralTest() throws LexicalException {
		String input = """
				 \"\\"Hello World\\"\"
				 """;
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"Hello World\"", scanner.next());
	}

	@Test
	void andIllegalCarriageReturn() throws LexicalException {
		String input = """
				"\\r" ~ legal
				"\r" ~ illegal
				""";
		IScanner scanner = CompilerComponentFactory.makeScanner(input);
		checkString("\"\\r\"", "\r", new SourceLocation(1, 1), scanner.next());
		assertThrows(LexicalException.class, () -> {
			scanner.next();
		});
	}

}
