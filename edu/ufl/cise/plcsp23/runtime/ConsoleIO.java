/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */

package edu.ufl.cise.plcsp23.runtime;

import java.io.PrintStream;

public class ConsoleIO {

	/**
	 * Destination of "console" output. Can be changed to redirect output. Generated
	 * code should use ConsoleIO.write(...) etc. instead of
	 * System.out.println
	 */
	public static PrintStream console = System.out;

	/**
	 * change destination of console output for non-image types
	 */
	public static void setConsole(PrintStream out) {
		console = out;
	}

	public static void write(String s) {
		console.println(s);
	}
	
	public static void write(int val) {
		console.println(val);
	}
	
}
