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

public class Declaration extends AST {

	final NameDef nameDef;
	final Expr initializer;
	final Boolean initialized;  //added this for visitor implementation

	public Declaration(IToken firstToken, NameDef nameDef, Expr initializer, boolean initialized) {
		super(firstToken);
		this.nameDef = nameDef;
		this.initializer = initializer;
		this.initialized = initialized;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitDeclaration(this, arg);
	}

	public NameDef getNameDef() {
		return nameDef;
	}

	public Expr getInitializer() {
		return initializer;
	}

	@Override
	public String toString() {
		return "Declaration [nameDef=" + nameDef + ", initializer=" + initializer + "]";
	}

}
