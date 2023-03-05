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
import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.PLCException;

public class PredeclaredVarExpr extends Expr {

	public PredeclaredVarExpr(IToken firstToken) {
		super(firstToken);
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitPredeclaredVarExpr(this,arg);
	}
	
	public Kind getKind() {
		return getFirstToken().getKind();
	}

	@Override
	public String toString() {
		return "PredeclaredVarExpr [getKind()=" + getKind() + ", getType()=" + getType() + "]";
	}

}
