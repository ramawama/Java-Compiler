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

public class NameDef extends AST {
	
	final Type type;
	final Dimension dimension;
	final Ident ident;
	
	public NameDef(IToken firstToken, Type type, Dimension dimension, Ident ident) {
		super(firstToken);
		this.type = type;
		this.dimension = dimension;
		this.ident = ident;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitNameDef(this, arg);
	}

	public Type getType() {
		return type;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public Ident getIdent() {
		return ident;
	}

	@Override
	public String toString() {
		return "NameDef [type=" + type + ", dimension=" + dimension + ", ident=" + ident + "]";
	}

	
	
}
