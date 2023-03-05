package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.IToken;

public enum Type {
	IMAGE,
	PIXEL,
	INT,
	STRING,
	VOID;

	public static Type getType(IToken token) {
		return switch(token.getKind()) {
		case RES_image -> IMAGE;
		case RES_pixel -> PIXEL;
		case RES_int -> INT;
		case RES_string -> STRING;
		case RES_void -> VOID;
		default -> throw new RuntimeException("error in Type.getType, unexpected token kind " + token.getKind());
		};
	}
}
