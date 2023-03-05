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

public class LValue extends AST {

	final Ident ident;
	final PixelSelector pixelSelector;
	final ColorChannel color;

	public LValue(IToken firstToken, Ident ident, PixelSelector pixelSelector, ColorChannel color) {
		super(firstToken);
		this.ident = ident;
		this.pixelSelector = pixelSelector;
		this.color = color;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitLValue(this, arg);
	}

	public Ident getIdent() {
		return ident;
	}

	public PixelSelector getPixelSelector() {
		return pixelSelector;
	}

	public ColorChannel getColor() {
		return color;
	}

	@Override
	public String toString() {
		return "LValue [ident=" + ident + ", pixelSelector=" + pixelSelector + ", color=" + color + "]";
	}

}
