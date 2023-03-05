/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */


package edu.ufl.cise.plcsp23;

public interface IToken {
	/** 
	 * Represents the location in the source code
	 */
	public record SourceLocation(int line, int column) {}
	
	public static enum Kind {
		IDENT,
		NUM_LIT,
		STRING_LIT,
		RES_image,
		RES_pixel,
		RES_int,
		RES_string,
		RES_void,
		RES_nil,
		RES_load,
		RES_display,
		RES_write,
		RES_x,
		RES_y,
		RES_a,
		RES_r,
		RES_X,
		RES_Y,
		RES_Z,
		RES_x_cart,
		RES_y_cart,
		RES_a_polar,
		RES_r_polar,
		RES_rand,
		RES_sin,
		RES_cos,
		RES_atan,
		RES_if,
		RES_while,
		DOT, //  .
		COMMA, // ,
		QUESTION, // ?
		COLON, // :
		LPAREN, // (
		RPAREN, // )
		LT, // <
		GT, // >
		LSQUARE, // [
		RSQUARE, // ]
		LCURLY, // {
		RCURLY, // }
		ASSIGN, // =
		EQ, // ==
		EXCHANGE, // <->
		LE, // <=
		GE, // >=
		BANG, // !
		BITAND, // &
		AND, // &&
		BITOR, // |
		OR, // ||
		PLUS, // +
		MINUS, // -
		TIMES, // *
		EXP, // **
		DIV, // /
		MOD, // %
		EOF,
		ERROR,
		RES_red,
		RES_grn,
		RES_blu
	}


	/**
	 * Returns a SourceLocation record containing the line and column number of this token.
	 * Both counts start numbering at 1.
	 * 
	 * @return Line number and column of this token.  
	 */
	public SourceLocation getSourceLocation();
	
	/** Returns the kind of this Token
	 * 
	 * @return kind
	 */
	public Kind getKind();
	
	/**
	 * Returns a char array containing the characters of this token.
	 * 
	 * @return
	 */
	public String getTokenString();




}
