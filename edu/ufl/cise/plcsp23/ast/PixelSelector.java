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
import edu.ufl.cise.plcsp23.PLCException;

public class PixelSelector extends AST {

	final Expr x;
	final Expr y;

	public PixelSelector(IToken firstToken, Expr x, Expr y) {
		super(firstToken);
		this.x = x;
		this.y = y;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitPixelSelector(this, arg);
	}

	public Expr getX() {
		return x;
	}

	public Expr getY() {
		return y;
	}

	@Override
	public String toString() {
		return "PixelSelector [x=" + x + ", y=" + y + "]";
	}




	
}
