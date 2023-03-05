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

import edu.ufl.cise.plcsp23.PLCException;

public interface ASTVisitor {

	Object visitAssignmentStatement(AssignmentStatement statementAssign, Object arg) throws PLCException;

	Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException;

	Object visitBlock(Block block, Object arg) throws PLCException;

	Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException;

	Object visitDeclaration(Declaration declaration, Object arg) throws PLCException;

	Object visitDimension(Dimension dimension, Object arg) throws PLCException;

	Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException;

	Object visitIdent(Ident ident, Object arg) throws PLCException;

	Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException;

	Object visitLValue(LValue lValue, Object arg) throws PLCException;

	Object visitNameDef(NameDef nameDef, Object arg) throws PLCException;

	Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException;

	Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException;

	Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException;

	Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException;

	Object visitProgram(Program program, Object arg) throws PLCException;

	Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException;

	Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException;

	Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException;

	Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException;

	Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException;

	Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException;

	Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException;



}
