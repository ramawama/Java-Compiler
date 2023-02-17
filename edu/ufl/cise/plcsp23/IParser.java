package edu.ufl.cise.plcsp23;

import edu.ufl.cise.plcsp23.ast.AST;

public interface IParser {
	
	AST parse() throws PLCException;

}
