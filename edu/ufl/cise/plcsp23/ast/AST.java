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

import java.util.Objects;

import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public abstract class AST {

	public final IToken firstToken;

	public AST(IToken firstToken) {
		this.firstToken = firstToken;
	}

	public abstract Object visit(ASTVisitor v, Object arg) throws PLCException;

	public IToken getFirstToken() {
		return firstToken;
	}

	public int getLine() {
		return firstToken.getSourceLocation().line();
	}

	public int getColumn() {
		return firstToken.getSourceLocation().column();
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstToken);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AST other = (AST) obj;
		return Objects.equals(firstToken, other.firstToken);
	}

	@Override
	public String toString() {
		return "AST [" + (firstToken != null ? "firstToken=" + firstToken : "") + "]";
	}

}
