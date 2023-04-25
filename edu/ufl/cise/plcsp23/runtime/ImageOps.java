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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import edu.ufl.cise.plcsp23.runtime.PLCRuntimeException;


/**
 * An image is represented by a 2D array of pixels. An image is implemented as
 * an instance of java.awt.image.BufferedImage using the default color model.
 *
 * The BufferedImage class offers a way to get and set the pixel value,
 * getRGB(int x, int y), which returns an int, and setRGB(int x, int y, int
 * rgb); In these methods, pixels are represented as an int that encodes the 3
 * colors plus the alpha (transparency) value.
 * 
 * In PLCLang, we do not deal with the alpha value--whenever required, it is set
 * to 0xff.
 * 
 * In an image, the color of a pixel is packed into an int, where 8 bits are
 * available for the alpha, red, green, and blue components. See the PixelOps 
 * class for methods for working with pixels.
 * 
 */

public class ImageOps {

//	/**
//	 * returns the pixel at the x,y location in the given image in packed int form.
//	 * 
//	 * @param image
//	 * @param x
//	 * @param y
//	 * @return
//	 */
//	public static int getPackedColor(BufferedImage image, int x, int y) {
//		return image.getRGB(x, y);
//	}


	/**
	 * Returns a new image containing only the red component of the given image.
	 * This method can be used to implement the red channel selector applied to an image.
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage extractRed(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = getRGB(image, x, y);
				int red = PixelOps.red(pixel);
				int redPixel = PixelOps.pack(red, 0, 0);
				newImage.setRGB(x, y, redPixel);
			}
		}
		return newImage;
	}

	/**
	 * Returns a new image containing only the green component of the given image.
	 * This can be used to implement the grn  channel selector applied to an image.
	 * 
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage extractGrn(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = getRGB(image, x, y);
				int grn = PixelOps.grn(pixel);
				int grnPixel = PixelOps.pack(0, grn, 0);
				newImage.setRGB(x, y, grnPixel);
			}
		}
		return newImage;
	}

	/**
	 * Returns a new image containing only the blue component of the given image.
	 * This can be used to implement the blu channel selector applied to an image.
	 * 
	 * 
	 * @param image
	 * @return
	 */
	public static BufferedImage extractBlu(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = getRGB(image, x, y);
				int blu = PixelOps.blu(pixel);
				int bluPixel = PixelOps.pack(0, 0, blu);
				newImage.setRGB(x, y, bluPixel);
			}
		}
		return newImage;
	}

	public enum OP {
		PLUS, MINUS, TIMES, DIV, MOD;
	}

	public enum BoolOP {
		EQUALS, NOT_EQUALS;
	}

	/**

	 */
	public static int binaryPackedPixelPixelOp(OP op, int leftPacked, int rightPacked) {
		int lred = PixelOps.red(leftPacked);
		int lgrn = PixelOps.grn(leftPacked);
		int lblu = PixelOps.blu(leftPacked);
		int rred = PixelOps.red(rightPacked);
		int rgrn = PixelOps.grn(rightPacked);
		int rblu = PixelOps.blu(rightPacked);
		return 
		switch(op) {
		case PLUS -> PixelOps.pack(lred + rred, lgrn + rgrn, lblu + rblu);
		case MINUS -> PixelOps.pack(lred - rred, lgrn - rgrn, lblu - rblu);
		case TIMES -> PixelOps.pack(lred * rred, lgrn * rgrn, lblu * rblu);
		case DIV -> PixelOps.pack(lred / rred, lgrn / lgrn, lblu / rblu);
		case MOD -> PixelOps.pack(lred % rred, lgrn % lgrn, lblu % rblu);
		default -> throw new IllegalArgumentException("Compiler/runtime error Unexpected value: " + op);
		};		
	}
	
	public static int binaryPackedPixelScalarOp(OP op, int leftPacked, int right) {
		int lred = PixelOps.red(leftPacked);
		int lgrn = PixelOps.grn(leftPacked);
		int lblu = PixelOps.blu(leftPacked);
		return 
		switch(op) {
		case PLUS -> PixelOps.pack(lred + right, lgrn + right, lblu + right);
		case MINUS -> PixelOps.pack(lred - right, lgrn - right, lblu - right);
		case TIMES -> PixelOps.pack(lred * right, lgrn * right, lblu * right);
		case DIV -> PixelOps.pack(lred / right, lgrn / right, lblu / right);
		case MOD -> PixelOps.pack(lred % right, lgrn % right, lblu % right);
		default -> throw new IllegalArgumentException("Compiler/runtime error Unexpected value: " + op);
		};				
	}
	
	
	public static boolean binaryPackedPixelBooleanOp(BoolOP op, int left, int right) {
		int leftNoAlpha = left & ~PixelOps.SELECT_ALPHA;  //zero out alpha component before comparison.
		int rightNoAlpha = right & ~PixelOps.SELECT_ALPHA;
		return (op == BoolOP.EQUALS) ? leftNoAlpha == rightNoAlpha : leftNoAlpha != rightNoAlpha;
	}
		
	/**
	 * Returns a new BufferedImage obtained by applying the given binary operator
	 * to each color component in each pixel in the given images.
	 * 
	 * If the images do not have the same shape, a PLCRuntimeException is thrown.
	 * 
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	
	public static BufferedImage binaryImageImageOp(OP op, BufferedImage left, BufferedImage right) {
		int lwidth = left.getWidth();
		int rwidth = right.getWidth();
		int lheight = left.getHeight();
		int rheight = right.getHeight();
		if (lwidth != rwidth || lheight != rheight) {
			throw new PLCRuntimeException("Attempting binary operation on images with unequal sizes");
		}
		BufferedImage result = new BufferedImage(lwidth, lheight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < lwidth; x++) {
			for (int y = 0; y < lheight; y++) {
				int leftPixel = left.getRGB(x, y);
				int rightPixel = right.getRGB(x, y);
				int newPixel = binaryPackedPixelPixelOp(op, leftPixel, rightPixel);
				result.setRGB(x, y, newPixel);
			}
		}
		return result;
	}
	
	
	public static BufferedImage binaryImagePixelOp(OP op, BufferedImage left, int right) {
		int lwidth = left.getWidth();
		int lheight = left.getHeight();

		BufferedImage result = new BufferedImage(lwidth, lheight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < lwidth; x++) {
			for (int y = 0; y < lheight; y++) {
				int leftPixel = left.getRGB(x, y);
				int newPixel = binaryPackedPixelPixelOp(op, leftPixel, right);
				result.setRGB(x, y, newPixel);
			}
		}
		return result;
	}
	
	/**
	 * Returns a new buffered image obtained by applying the given binary operation 
	 * to each color component in each pixel in the given image (left) and the int value (right).
	 * 
	 * @param op
	 * @param left
	 * @param right
	 * @return
	 */
	public static BufferedImage binaryImageScalarOp(OP op, BufferedImage left, int right) {
		int lwidth = left.getWidth();
		int lheight = left.getHeight();
		BufferedImage result = new BufferedImage(lwidth, lheight, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < lwidth; x++) {
			for (int y = 0; y < lheight; y++) {
				int leftPixel = left.getRGB(x, y);
				int newPixel = binaryPackedPixelScalarOp(op,leftPixel, right);
				result.setRGB(x, y, newPixel);
			}
		}
		return result;
	}
	

	
	public static BufferedImage setAllPixels(BufferedImage image, int packed) {
		for (int x = 0; x < image.getWidth(); x++)
			for (int y = 0; y < image.getHeight(); y++) {
				image.setRGB(x, y,  packed);
			}
		return image;
	}

	
	/**
	 * Creates an image of given size. 
	 * 
	 * @param width
	 * @param height

	 * @return
	 */
	public static BufferedImage makeImage(int width, int height) {
		return new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
	}
	
	/**
	 * Returns a new image that is copy of the given BufferedImage 
	 * @param image
	 * @return new image that is copy of the given image
	 */
	public static final BufferedImage cloneImage(BufferedImage image) {
	    BufferedImage clone = new BufferedImage(image.getWidth(),
	            image.getHeight(), image.getType());
	    Graphics2D g2d = clone.createGraphics();
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();
	    return clone;
	}
	

	/**
	 * Copies one image into the other, resizing to fit the size of the destination image
	 * 
	 * @param sourceImage
	 * @param destImage
	 */
	public static final void copyInto(BufferedImage sourceImage, BufferedImage destImage) {		
        int w = sourceImage.getWidth();
		int h = sourceImage.getHeight();
		int maxX = destImage.getWidth();
		int maxY = destImage.getHeight();
		AffineTransform at = new AffineTransform();
		at.scale(((float) maxX) / w, ((float) maxY) / h);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		scaleOp.filter(sourceImage, destImage);
	}
	
	/**
	 * Returns a new image that is a resized version of the 'before' image.
	 * 
	 * @param image
	 * @param maxX
	 * @param maxY
	 * @return new image that is a resized version of the 'before' image
	 */
	public static BufferedImage copyAndResize(BufferedImage image, int maxX,
			int maxY) {
		int w = image.getWidth();
		int h = image.getHeight();
		AffineTransform at = new AffineTransform();
		at.scale(((float) maxX) / w, ((float) maxY) / h);
		AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		BufferedImage newResizedImage = null;
		newResizedImage = scaleOp.filter(image, newResizedImage);
		return newResizedImage;
	}
	

	/**
	 * Returns an array of ints representing the packed pixels of the given image.
	 * 
	 * This can be used in Junit test to compare two images by using 
	 * assertArrayEquals on the array return from this method.
	 * 
	 * @param result
	 * @return array of ints representing the packed pixels of the given image
	 */
	public static int[] getRGBPixels(BufferedImage result) {
		return result.getRGB(0,0,result.getWidth(), result.getHeight(), null,0,result.getWidth());
	}
	
	public static boolean equals(BufferedImage image0, BufferedImage image1) {
		int[] pixels0 = getRGBPixels(image0);
		int[] pixels1 = getRGBPixels(image1);
		return Arrays.equals(pixels0, pixels1);
	}
	
	public static int equalsForCodeGen(BufferedImage image0, BufferedImage image1) {
		return equals(image0,image1)?1:0;
	}


	/**
	 * If coordinates are out of bounds does nothing. 
	 */
	public static void setRGB(BufferedImage image, int x, int y, int pixel) {
		int w = image.getWidth();
		int h = image.getHeight();
		if (0 <= x && x < w && 0 <= y && y < h) {
			image.setRGB(x, y, pixel);
		}
	}
		
	/**
	 * If coordinates are out of bounds it returns a black pixel. 
	 */
	public static int getRGB(BufferedImage image, int x, int y) {
		int w = image.getWidth();
		int h = image.getHeight();
		if (0 <= x && x < w && 0 <= y && y < h) {
			return image.getRGB(x, y);
		}
		return PixelOps.pack(0, 0, 0);
	}

	public static int binaryPackedPixelIntOp(OP op, int leftPacked, int q) {
		int lred = PixelOps.red(leftPacked);
		int lgrn = PixelOps.grn(leftPacked);
		int lblu = PixelOps.blu(leftPacked);
		int rred = q;
		int rgrn = q;
		int rblu = q;
		return 
		switch(op) {
		case PLUS -> PixelOps.pack(lred + rred, lgrn + rgrn, lblu + rblu);
		case MINUS -> PixelOps.pack(lred - rred, lgrn - rgrn, lblu - rblu);
		case TIMES -> PixelOps.pack(lred * rred, lgrn * rgrn, lblu * rblu);
		case DIV -> PixelOps.pack(lred / rred, lgrn / rgrn, lblu / rblu);
		case MOD -> PixelOps.pack(lred % rred, lgrn % rgrn, lblu % rblu);
		default -> throw new IllegalArgumentException("Compiler/runtime error Unexpected value: " + op);
		};		
	}
		
	}
