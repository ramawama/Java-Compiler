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

public class ConditionalExpr extends Expr {

	final Expr guard;
	final Expr trueCase;
	final Expr falseCase;

	public ConditionalExpr(IToken firstToken, Expr guard, Expr trueCase, Expr falseCase) {
		super(firstToken);
		this.guard = guard;
		this.trueCase = trueCase;
		this.falseCase = falseCase;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitConditionalExpr(this, arg);
	}

	public Expr getGuard() {
		return guard;
	}

	public Expr getTrueCase() {
		return trueCase;
	}

	public Expr getFalseCase() {
		return falseCase;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(falseCase, guard, trueCase);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConditionalExpr other = (ConditionalExpr) obj;
		return Objects.equals(falseCase, other.falseCase) && Objects.equals(guard, other.guard)
				&& Objects.equals(trueCase, other.trueCase);
	}

	@Override
	public String toString() {
		return "ConditionalExpr [guard=" + guard + ", trueCase=" + trueCase + ", falseCase=" + falseCase + "]";
	}

}
