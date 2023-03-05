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

public class WhileStatement extends Statement {
	
	final Expr guard;
	final Block block;
	
	

	public WhileStatement(IToken firstToken, Expr guard, Block block) {
		super(firstToken);
		this.guard = guard;
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitWhileStatement(this, arg);
	}

	public Expr getGuard() {
		return guard;
	}
	public Block getBlock() {
		return block;
	}

	@Override
	public String toString() {
		return "WhileStatement [guard=" + guard + ", block=" + block + "]";
	}

}
