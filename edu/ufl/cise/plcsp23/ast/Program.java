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

import java.util.List;
import java.util.Objects;

import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class Program extends AST {

	final Type type;
	final Ident ident;
	final List<NameDef> paramList;
	final Block block;

	public Program(IToken firstToken, Type type, Ident ident, List<NameDef> paramList, Block block) {
		super(firstToken);
		this.type = type;
		this.ident = ident;
		this.paramList = paramList;
		this.block = block;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitProgram(this, arg);
	}

	public Type getType() {
		return type;
	}

	public Ident getIdent() {
		return ident;
	}

	public List<NameDef> getParamList() {
		return paramList;
	}

	public Block getBlock() {
		return block;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(block, ident, paramList, type);
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
		Program other = (Program) obj;
		return Objects.equals(block, other.block) && Objects.equals(ident, other.ident)
				&& Objects.equals(paramList, other.paramList) && type == other.type;
	}

	
}
