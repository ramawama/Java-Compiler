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

public class Ident extends AST {
	
	NameDef def;

	public Ident(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitIdent(this, arg);
	}
	
	public String getName() {
		return firstToken.getTokenString();
	}

	public NameDef getDef() {
		return def;
	}

	public void setDef(NameDef def) {
		this.def = def;
	}

	@Override
	public String toString() {
		return "Ident [getName()=" + getName() + ", getDef()=" + getDef() + "]";
	}

}
