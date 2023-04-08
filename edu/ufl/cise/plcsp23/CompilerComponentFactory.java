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


import edu.ufl.cise.plcsp23.ast.ASTVisit;
import edu.ufl.cise.plcsp23.ast.ASTVisitor;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicClassLoader;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicCompiler;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.StringJavaFileObject;
import edu.ufl.cise.plcsp23.ast.CodeGenVisitor;

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
		ASTVisitor visitor = new ASTVisit();
		return visitor; //not sure if this is correct
	}
	public static CodeGenVisitor makeCodeGenerator(String packagename) {
		CodeGenVisitor visitor = new CodeGenVisitor();
		return visitor;
	}
}

