package edu.ufl.cise.plcsp23.ast;

import java.util.List;
import java.util.Objects;

import edu.ufl.cise.plcsp23.IToken;
import edu.ufl.cise.plcsp23.PLCException;

public class Block extends AST {

	final List<Declaration> decList;
	final List<Statement> statementList;
	
	public Block(IToken firstToken, List<Declaration> decList, List<Statement> statementList) {
		super(firstToken);
		this.decList = decList;
		this.statementList = statementList;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws PLCException {
		return v.visitBlock(this,arg);
	}

	public List<Declaration> getDecList() {
		return decList;
	}

	public List<Statement> getStatementList() {
		return statementList;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(decList, statementList);
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
		Block other = (Block) obj;
		return Objects.equals(decList, other.decList) && Objects.equals(statementList, other.statementList);
	}

	@Override
	public String toString() {
		return "Block [decList=" + decList + ", statementList=" + statementList + "]";
	}

	
}
