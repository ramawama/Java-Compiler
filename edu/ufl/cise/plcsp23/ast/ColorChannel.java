/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */

package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.IToken;

public enum ColorChannel {
	red,
	grn,
	blu;

	public static ColorChannel getColor(IToken token) {
		return switch(token.getKind()) {
		case RES_red -> red;
		case RES_grn -> grn;
		case RES_blu -> blu;
		default -> throw new RuntimeException("error in ColorChannel.getColor, unexpected token kind " + token.getKind());
		};
	}
}
