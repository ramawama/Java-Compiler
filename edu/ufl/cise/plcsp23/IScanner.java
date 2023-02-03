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

public interface IScanner {
	/**
	 * Return an IToken and advance the internal position so that subsequent calls
	 * will return subsequent ITokens.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	IToken next() throws LexicalException;

}
