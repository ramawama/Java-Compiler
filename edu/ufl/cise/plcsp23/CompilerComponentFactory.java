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


import edu.ufl.cise.plcsp23.ast.ASTVisitor;
import edu.ufl.cise.plcsp23.ast.AVisit;

public class CompilerComponentFactory {
	public static IScanner makeScanner(String input) {
		return new Scanner(input);  //instance of scanner
	}

	public static IParser makeAssignment2Parser(String input) throws PLCException {
		Scanner scanner = new Scanner(input);
		return new Parser(scanner);
	}

	public static IParser makeParser(String input) throws PLCException {
		Scanner scanner = new Scanner(input);
		return new Parser(scanner);
	}
	public static ASTVisitor makeTypeChecker() {
		AVisit visitor = new AVisit();
		return visitor; //not sure if this is correct
	}
}

