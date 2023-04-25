/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */


package edu.ufl.cise.plcsp23.runtime;

public class PixelOps {

	/*
	 * create a packed color with the given color component values. Values less than
	 * 0 or greater than 255 are truncated.
	 */
	public static int pack(int redVal, int grnVal, int bluVal) {
		int pixel = ((0xFF << SHIFT_ALPHA) | (truncate(redVal) << SHIFT_RED) | (truncate(grnVal) << SHIFT_GRN)
				| (truncate(bluVal) << SHIFT_BLU));
		return pixel;
	}
	
	public static int red(int pixel) {
		return (pixel & SELECT_RED) >> SHIFT_RED;
	}

	public static int grn(int pixel) {
		return (pixel & SELECT_GRN) >> SHIFT_GRN;
	}

	public static int blu(int pixel) {
		return (pixel & SELECT_BLU) >> SHIFT_BLU;
	}

	public static int setRed(int pixel, int val) {
		return pack(val, grn(pixel), blu(pixel));
	}

	public static int setGrn(int pixel, int val) {
		return pack(red(pixel), val, blu(pixel));
	}
	
	public static int setBlu(int pixel, int val) {
		return pack(red(pixel), grn(pixel), val);
	}
	
	
	/**
	 * truncates an int to value in range of [0,256)
	 * 
	 * @param z
	 * @return value in [0,256)
	 */
	private static int truncate(int z) {
		return z < 0 ? 0 : (z > 255 ? 255 : z);
	}
	
	
	/** Constants used in building and select color components from a packed int */
	public static final int SELECT_RED = 0x00ff0000;
	public static final int SELECT_GRN = 0x0000ff00;
	public static final int SELECT_BLU = 0x000000ff;
	public static final int SELECT_ALPHA = 0xff000000;
	public static final int SHIFT_ALPHA = 24;
	public static final int SHIFT_RED = 16;
	public static final int SHIFT_GRN = 8;
	public static final int SHIFT_BLU = 0;

	/**
	 * Returns String showing packed pixel in hex format. Alpha, red, green, and
	 * blue component are each two digits. This is not required for the project, but
	 * may be useful when debugging.
	 * 
	 * @param packedPixel
	 * @return
	 */
	public static String packedToString(int packedPixel) {
		return Integer.toHexString(packedPixel);
	}

	
}
