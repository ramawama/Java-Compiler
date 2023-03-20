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

import edu.ufl.cise.plcsp23.ast.AST;
import edu.ufl.cise.plcsp23.ast.ASTVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TypeCheckTest_starter {
	

	/* Scans, parses, and type checks input.  Returns normally if no errors. */
	AST typeCheck(String input) throws LexicalException, PLCException {
		AST ast = CompilerComponentFactory.makeParser(input).parse();
		ASTVisitor typeChecker = CompilerComponentFactory.makeTypeChecker();
		ast.visit(typeChecker, null);
		return ast;
	}
	
	/* Scans, parses, and type checks input.  Expects input to scan and parse
	 * without errors.  An error is expected during type checking.
	 */
	AST typeCheckError(String input) throws LexicalException, PLCException {
		AST ast = CompilerComponentFactory.makeParser(input).parse();
		ASTVisitor typeChecker = CompilerComponentFactory.makeTypeChecker();
		assertThrows(TypeCheckException.class, () -> {
			ast.visit(typeChecker, null);
		});
		return ast;
	}

	// makes it easy to turn output on and off (and less typing than
	// System.out.println)
	static final boolean VERBOSE = true;

	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}


	@Test
	void t0() throws PLCException{
		String input = """
				void f(){}
				""";
		typeCheck(input);				
	}
	
	@Test
	void t1() throws PLCException{
		String input = """
				int f(int xx, string ss, image ii, pixel p){}
				""";
		typeCheck(input);				
	}
	
	@Test
	void t2() throws PLCException{
		String input = """
				string f(int f){}
				""";
		typeCheck(input);				
	}
	
	@Test
	void t3() throws PLCException{
		String input = """
				string f(int f, string f){}
				""";
		typeCheckError(input);				
	}
	
	@Test
	void t4() throws PLCException{
		String input = """
				string f(int f, string s){
				  int g.
				  string ss.
				   }
				""";
		typeCheck(input);				
	}
	
	@Test
	void t5() throws PLCException{
		String input = """
				string f(int f, string s){
				  int s.
				  string ss.
				   }
				""";
		typeCheckError(input);				
	}
	
	@Test
	void t6() throws PLCException{
		String input = """
				void f(void xx){}
				""";
		typeCheckError(input);
	}
	
	@Test
	void t7() throws PLCException{
		String input = """
				void f(){
				  int xx = 2.
				  string ss = "hello".
				  image[100,100] ii = "url".
				  image[200,200] ii1 = ii.
				  :xx.
				  }
				""";
		typeCheckError(input);
	}
	
	@Test
	void t8() throws PLCException{
		String input = """
				void f(){
				  int xx = 2+xx.
				  }
				""";
		typeCheckError(input);
	}
	
	@Test void t9() throws PLCException{
		String input = """
				int f(){
				:3.
				}
				""";
		typeCheck(input);		
	}
	
	@Test void t10() throws PLCException{
		String input = """
				image f(){
				image i = "url".
				image j = i.
				int rr = j[3,4]:red.
				}
				""";
		typeCheck(input);		
	}
	
	@Test void t11() throws PLCException{
		String input = """
				image f(){
				image i = "url".
				i[x,y] = [x,y,0].
				}
				""";
		typeCheck(input);		
	}
	
	@Test void t12() throws PLCException {
		String input = """
				void f(string url){
				image[300,400] i = url.
				image[300,400] j. 
				j[a,r] = i[a_polar[y,x], r_polar[x,y]].
				write j.
				}
				""";
		typeCheck(input);
	}
	
	@Test void t13() throws PLCException {
		String input = """
				string s(string s0, string s1, int ok){
				:if ok ? s0 ? s0 + s1 .
				}
				""";
		typeCheck(input);
	}
	
	@Test void t14() throws PLCException {
		String input = """
				string s(string s0, string s1, int ok){
				:if ok ? ok+1 ? s0 + s1 .
				}
				""";
		typeCheckError(input);
	}
	
	@Test void t15() throws PLCException {
		String input = """
				int f(int xx){
				: Z/2 + xx.
				}				
				""";
		typeCheck(input);
	}
	
	@Test void t16() throws PLCException {
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
		typeCheck(input);
	}
	
	@Test void t17() throws PLCException {
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
		typeCheck(input);
	}
	
	@Test void t18() throws PLCException {
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
		typeCheckError(input);
	}
}
