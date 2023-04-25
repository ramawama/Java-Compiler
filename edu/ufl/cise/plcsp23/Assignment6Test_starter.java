package edu.ufl.cise.plcsp23;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plcsp23.ast.AST;
import edu.ufl.cise.plcsp23.ast.Program;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicClassLoader;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicCompiler;
import edu.ufl.cise.plcsp23.runtime.ConsoleIO;
import edu.ufl.cise.plcsp23.runtime.FileURLIO;
import edu.ufl.cise.plcsp23.runtime.ImageOps;
import edu.ufl.cise.plcsp23.runtime.PixelOps;

class Assignment6Test_starter {

	// Some images to use in tests. Feel free to replace with your own.
	String beach = "https://images.freeimages.com/images/large-previews/5a5/the-path-to-the-sunrise-1629704.jpg";
	String owl = "https://pocket-syndicated-images.s3.amazonaws.com/622ad94833741.png";
	String dino = "https://cdn.theatlantic.com/thumbor/-WDVFQL2O-tLHvsDK1DzflsSWAo=/1500x1000/media/img/photo/2023/03/photos-week-5/a01_1249659784/original.jpg";

	Object genCodeAndRun(String input, String mypackage, Object[] params) throws Exception {
		show(input);
		show("****");
		AST ast = CompilerComponentFactory.makeParser(input).parse();
		ast.visit(CompilerComponentFactory.makeTypeChecker(), null);
		show(ast); // display decorated AST
		String name = ((Program) ast).getIdent().getName();
		String code = (String) ast.visit(CompilerComponentFactory.makeCodeGenerator(""), null);
		show(code);
		byte[] byteCode = DynamicCompiler.compile(name, code);
		Object result = DynamicClassLoader.loadClassAndRunMethod(byteCode, name, "apply", params);
		return result;
	}

	static final boolean VERBOSE = true;
	static final boolean WAIT_FOR_INPUT = false;

	/**
	 * This waits for input to prevent Junit and your IDE from closing the window
	 * displaying your image before you have a chance to see it. If you do not need
	 * or want this, set WAIT_FOR_INPUT to false to disable
	 * 
	 * @throws IOException
	 */

	void wait_for_input() throws IOException {
		if (WAIT_FOR_INPUT) {
			System.out.print("enter any char to close: ");
			System.in.read();
		}
	}

	/**
	 * Displays an image on the screen.
	 * 
	 * @param obj
	 * @throws IOException
	 */
	void show(BufferedImage obj) throws IOException {
		if (VERBOSE) {
			ConsoleIO.displayImageOnScreen(obj);
		}
		wait_for_input();
	}

	/**
	 * Normal show that uses obj.toString to display.
	 * 
	 * @param obj
	 */
	void show(Object obj) {
		if (VERBOSE) {
			System.out.println(obj);
		}
	}

	void imageEquals(BufferedImage expectedImage, BufferedImage image) {
		int expectedWidth = expectedImage.getWidth();
		int expectedHeight = expectedImage.getHeight();
		int width = image.getWidth();
		int height = image.getHeight();
		assertEquals(expectedImage.getWidth(), image.getWidth());
		assertEquals(expectedImage.getHeight(), image.getHeight());
		int[] expectedPixelArray = expectedImage.getRGB(0, 0, expectedWidth, expectedHeight, null, 0, expectedWidth);
		for (int i = 0; i < expectedWidth * expectedHeight; i++) {
			expectedPixelArray[i] = expectedPixelArray[i] & 0xFF000000;
		}
		int[] pixelArray = image.getRGB(0, 0, width, height, null, 0, width);
		for (int i = 0; i < expectedWidth * expectedHeight; i++) {
			pixelArray[i] = pixelArray[i] & 0xFF000000;
		}
		assertArrayEquals(expectedPixelArray, pixelArray);
	}

	@Test
	void cg6_0() throws Exception {
		String input = """
				pixel P(string s, int xx, int yy){
				image im = s.
				: im[xx,yy].
				}
				""";
		String s = owl;
		int xx = 0;
		int yy = 0;
		Object[] params = { s, xx, yy };
		int result = (int) genCodeAndRun(input, "", params);
		show(Integer.toHexString(result));
		BufferedImage sourceImage = FileURLIO.readImage(s);
		int expected = ImageOps.getRGB(sourceImage, xx, yy);
		assertEquals(expected, result);
	}

	@Test
	void cg6_1() throws Exception {
		String input = """
				pixel P(string s, int xx, int yy){
				image im = s.
				: im[xx,yy]:red.
				}
				""";
		String s = owl;
		int xx = 0;
		int yy = 0;
		Object[] params = { s, xx, yy };
		int result = (int) genCodeAndRun(input, "", params);
		show(Integer.toHexString(result));
		BufferedImage sourceImage = FileURLIO.readImage(s);
		int expected = PixelOps.red(ImageOps.getRGB(sourceImage, xx, yy));
		assertEquals(expected, result);
	}

	@Test
	void cg6_2() throws Exception {
		String input = """
				   image P(string s){
				image im = s.
				: im:red.
				}
				""";
		String s = owl;
		Object[] params = { s };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		show(result);
		BufferedImage sourceImage = FileURLIO.readImage(s);
		BufferedImage expected = ImageOps.extractRed(sourceImage);
		imageEquals(expected, result);
	}
	
	@Test
	void cg6_2a() throws Exception {
		String input = """
				   image P(string s){
				image im = s.
				image imr = im:red.
				: imr:blu. ~this is a black image
				}
				""";
		String s = owl;
		Object[] params = { s };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		show(result);
		BufferedImage sourceImage = FileURLIO.readImage(s);
		BufferedImage imr = ImageOps.extractRed(sourceImage);
		BufferedImage expected = ImageOps.extractBlu(imr);
		imageEquals(expected, result);
	}

	@Test
	void cg6_3() throws Exception {
		String input = """
				pixel f(){
				pixel p = [4,5,6].
				: p:grn.
				}
				""";
		Object[] params = {};
		int result = (int) genCodeAndRun(input, "", params);
		show(result);
		int p = PixelOps.pack(4, 5, 6);
		int expected = PixelOps.grn(p);
		assertEquals(expected, result);
	}

	@Test
	void cg6_4() throws Exception {
		String input = """
				image addImage(){
				int w = 100.
				int h = 100.
				image[w,h] grnImage = [0,Z,0].
				image[w,h] bluImage = [0,0,Z].
				image[w,h] tealImage.
				tealImage = grnImage+bluImage.
				:tealImage.
				}
				""";
		Object[] params = {};
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		int w = 100;
		int h = 100;
		BufferedImage grnImage = (ImageOps.makeImage(w, h));
		grnImage = ImageOps.setAllPixels(grnImage, PixelOps.pack(0, 255, 0));
		BufferedImage bluImage = (ImageOps.makeImage(w, h));
		bluImage = ImageOps.setAllPixels(bluImage, PixelOps.pack(0, 0, 255));
		BufferedImage expected = ImageOps.makeImage(w, h);
		ImageOps.copyInto((ImageOps.binaryImageImageOp(ImageOps.OP.PLUS, grnImage, bluImage)), expected);
		show(result);
		imageEquals(expected, result);
	}

	@Test
	void cg6_5() throws Exception {
		String input = """
				image darker(string s){
				image im = s.
				:im/3.
				}
				""";
		Object[] params = { owl };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		show(result);
		BufferedImage sourceImage = FileURLIO.readImage(owl);
		BufferedImage expected = ImageOps.binaryImageScalarOp(ImageOps.OP.DIV, sourceImage, 3);
		imageEquals(expected, result);
	}

	@Test
	void cg6_6() throws Exception {
		String input = """
				int pixelPixel(){
				pixel p = [1,2,3].
				pixel q = [3,2,1].
				pixel rr = p + q.
				:rr.
				}
				""";
		Object[] params = {};
		int result = (int) genCodeAndRun(input, "", params);
		int p = PixelOps.pack(1, 2, 3);
		int q = PixelOps.pack(3, 2, 1);
		int expected = ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.PLUS, p, q);
		assertEquals(expected, result);
		show(Integer.toHexString(result));
	}

	@Test
	void cg6_7() throws Exception {
		String input = """
				int pixelPixel(){
				pixel p = [3,6,7].
				int q = 3.
				pixel rr = p % q.
				:rr.
				}
				""";
		Object[] params = {};
		int result = (int) genCodeAndRun(input, "", params);
		int p = PixelOps.pack(3, 6, 7);
		int q = 3;
		int expected = ImageOps.binaryPackedPixelIntOp(ImageOps.OP.MOD, p, q);
		assertEquals(expected, result);
		show(Integer.toHexString(result));
	}

	@Test
	void cg10() throws Exception {
		String input = """
				image f(string s){
				image k = s.
				:k.
				}
				""";
		String s = owl;
		Object[] params = { s };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = FileURLIO.readImage(s);
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg12a() throws Exception {
		String input = """
				image f(string s, int w, int h){
				image[w,h] k = s.
				:k.
				}
				""";
		String s = owl;
		Object[] params = { s, 100, 200 };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = FileURLIO.readImage(s, 100, 200);
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg10a() throws Exception {
		String input = """
				pixel ff(){
				image[100,200] k.
				pixel p = k[50,50].
				:p.
				}
				""";
		Object[] params = {};
		int result = (int) genCodeAndRun(input, "", params);
		show(Integer.toHexString(result));
	}

	@Test
	void cg11() throws Exception {
		String input = """
				image f(string s){
				image k = s.
				image kk = k.
				:kk.
				}
				""";
		String s = owl;
		Object[] params = { s };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage k = FileURLIO.readImage(s);
		BufferedImage kk = ImageOps.cloneImage(k);
		imageEquals(kk, result);
		show(result);
	}

	@Test
	void cg11a() throws Exception {
		String input = """
				image f(string s){
				image k = s.
				image[100,200] kk = k.
				:kk.
				}
				""";
		String s = owl;
		Object[] params = { s };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage k = FileURLIO.readImage(s);
		BufferedImage kk = ImageOps.copyAndResize(k, 100, 200);
		imageEquals(result, kk);
		show(result);
	}

	@Test
	void cg11b() throws Exception {
		String input = """
				image f(string s){
				image[200,50] k = s.
				:k.
				}
				""";
		String s = owl;
		Object[] params = { s };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage k = FileURLIO.readImage(s, 200, 50);
		imageEquals(result, k);
		show(result);
	}

	@Test
	void cg11c() throws Exception {
		String input = """
				pixel f(int rr, int gg, int bb){
				pixel p = [rr,gg,bb].
				:p.
				}
				""";
		int rr = 100;
		int gg = 200;
		int bb = 300; // this will be truncated to 255 (ff)
		Object[] params = { rr, gg, bb };
		int result = (int) genCodeAndRun(input, "", params);
		int expected = PixelOps.pack(rr, gg, bb);
		assertEquals(expected, result);
		show(Integer.toHexString(result));
	}

	@Test
	void cg12() throws Exception {
		String input = """
				image f(string s, int w, int h){
				image k = s.
				image[w,h] kk = k.
				:kk.
				}
				""";
		String s = owl;
		Object[] params = { s, 100, 200 };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = FileURLIO.readImage(s, 100, 200);
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg13() throws Exception {
		String input = """
				image f(int w, int h){
				image[w,h] im0 = [Z,0,0].
				:im0.
				}
				""";
		int w = 1000;
		int h = 500;
		Object[] params = { w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(w, h);
		int color = 0xFF0000; // red
		ImageOps.setAllPixels(expected, color);
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg14() throws Exception {
		String input = """
				image f(int w, int h){
				image[w,h] im0 = [0,Z,0].
				:im0.
				}
				""";
		int w = 1000;
		int h = 500;
		Object[] params = { w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(w, h);
		int color = 0x00FF00; // green
		ImageOps.setAllPixels(expected, color);
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg15() throws Exception {
		String input = """
				image f(int w, int h){
				image[w,h] im0 = [0,0,Z].
				:im0.
				}
				""";
		int w = 500;
		int h = 400;
		Object[] params = { w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(w, h);
		int color = 0x0000FF; // blue
		ImageOps.setAllPixels(expected, color);
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg16() throws Exception {
		String input = """
				image f(int w, int h, int val) {
					pixel p = val.
					image[w,h] im0 = p.
					:im0.
				}
				""";
		int w = 1000;
		int h = 500;
		Object[] params = { 1000, 500, 0xff0000ff };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(w, h);
		int color = 0x0000FF; // blue
		ImageOps.setAllPixels(expected, color);
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg17() throws Exception {
		String input = """
				image f(int w, int h, int val) {
					pixel p = [val,val,val].
					image[w,h] im0 = p.
					:im0.
				}
				""";
		int w = 1000;
		int h = 200;
		int val = 200;
		Object[] params = { w, h, val };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(w, h);
		int color = PixelOps.pack(val, val, val);
		ImageOps.setAllPixels(expected, color);
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg18() throws Exception {
		String input = """
				image f(int w, int h, int val) {
					pixel p = [0,0,val].
					image[w,h] im0 = p.
					write p.
					write val.
					:im0.
				}
				""";
		int w = 400;
		int h = 400;
		int val = 0x88;
		Object[] params = { w, h, val };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(w, h);
		int color = PixelOps.pack(0, 0, val);
		ImageOps.setAllPixels(expected, color);
		imageEquals(expected, result);
		show(result);
	}

	/*
	 * This test doesn't check assertions--look at the output It should display a
	 * black image and a white image that is half the size.
	 * 
	 * It should also print ff000000 ffffffff
	 */
	@Test
	void cg19() throws Exception {
		String input = """
				void f() {
				int w = 500.
				int h = 500.
					pixel p0 = [0,0,0].
					pixel p1 = [Z,Z,Z].
					image[w,h] im0 = p0.
					image[w/2,h/2] im1 = p1.
					write p0.
					write p1.
					write im0.
					write im1.
				}
				""";
		Object[] params = {};
		genCodeAndRun(input, "", params);
		wait_for_input();
	}

	@Test
	void cg20() throws Exception {
		String input = """
				image f(string s, int w, int h){
				image k = s.
				image[w,h] kk.
				kk = k.
				write k.
				write kk.
				:kk.
				}
				""";
		String s = owl;
		BufferedImage sourceImage = FileURLIO.readImage(s);
		int wSource = sourceImage.getWidth();
		int hSource = sourceImage.getHeight();
		int wDest = wSource / 4;
		int hDest = hSource / 4;
		Object[] params = { s, wDest, hDest };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(wDest, hDest);
		ImageOps.copyInto(sourceImage, expected);
		imageEquals(expected, result);
	}

	@Test
	void cg20a() throws Exception {
		String input = """
				image f(int w, int h){
				image[w,h] kk.
				kk = [Z,0,Z].
				write kk.
				:kk.
				}
				""";
		Object[] params = { 400, 500 };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage kk = ImageOps.makeImage(400, 500);
		ImageOps.setAllPixels(kk, PixelOps.pack(255, 0, 255));
		imageEquals(kk, result);
		show(result);
	}

	@Test
	void cg21() throws Exception {
		String input = """
				 		image rotate(string s, int w) {
					  image[w,w] k = s.
					  image[w,w] rot.
					  rot[x,y]=k[y,x].
					  :rot.
				}
				 		""";
		String s = owl;
		BufferedImage b = FileURLIO.readImage(s);
		int w = b.getWidth() / 2;
		Object[] params = { s, w };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(w, w);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < w; y++) {
				ImageOps.setRGB(expected, x, y, result.getRGB(y, x));
			}
		}
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg22() throws Exception {
		String input = """
				image beachAndOwl(string beach, string owl, int w, int h){
				image[w,h] b = beach.
				image[w,h] o = owl.
				image[w,h] sum.
				sum = (b + o)/2.  ~these are operations on images, use functions in ImageOps
				write b.
				write o.
				write sum.
				:sum.
				}
				""";
		BufferedImage b = FileURLIO.readImage(beach);
		BufferedImage o = FileURLIO.readImage(owl);
		int w = b.getWidth();
		int h = o.getHeight();
		b = FileURLIO.readImage(beach, w, h);
		o = FileURLIO.readImage(owl, w, h);
		Object[] params = { beach, owl, w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.binaryImageScalarOp(ImageOps.OP.DIV,
				ImageOps.binaryImageImageOp(ImageOps.OP.PLUS, b, o), 2);
		imageEquals(expected, result);
		show(result);
		show(expected);

	}

	@Test
	void cg23r() throws Exception {
		String input = """
				image makeRedImage(int w, int h){
				   image[w,h] im = [0,0,0].
				   im[x,y]:red = Z.
				   :im.
				   }
				   """;
		int w = 100;
		int h = 200;
		Object[] params = { w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(w, h);
		ImageOps.setAllPixels(expected, PixelOps.pack(0, 0, 0));
		int x;
		int y;
		for (x = 0; x < expected.getWidth(); x++) {
			for (y = 0; y < expected.getHeight(); y++) {
				ImageOps.setRGB(expected, x, y, PixelOps.setRed(expected.getRGB(x, y), 255));
			}
		}
		imageEquals(expected, result);
		show(result);
		show(expected);
	}

	@Test
	void cg23teal() throws Exception {
		String input = """
				image makeRedImage(int w, int h){
				   image[w,h] im = [0,0,0].
				   im[x,y]:grn = Z.
				   im[x,y]:blu = Z.
				   :im.
				   }
				   """;
		int w = 400;
		int h = 400;
		Object[] params = { w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage im = ImageOps.makeImage(w, h);
		ImageOps.setAllPixels(im, PixelOps.pack(0, 0, 0));
		for (int y = 0; y != im.getHeight(); y++) {
			for (int x = 0; x != im.getWidth(); x++) {
				ImageOps.setRGB(im, x, y, PixelOps.setGrn(ImageOps.getRGB(im, x, y), 255));
			}
		}
		for (int y = 0; y != im.getHeight(); y++) {
			for (int x = 0; x != im.getWidth(); x++) {
				ImageOps.setRGB(im, x, y, PixelOps.setBlu(ImageOps.getRGB(im, x, y), 255));
			}
		}
		imageEquals(im, result);
		show(result);
	}

	@Test
	void cg24a() throws Exception {
		String input = """
				int imageEqual(string s0, string s1){
				image i0 = s0.
				image i1 = s1.
				int eq = i0 == i1.
				:eq.
				}
				""";
		String s0 = beach;
		String s1 = beach;
		Object[] params = { s0, s1 };
		int result = (int) genCodeAndRun(input, "", params);
		BufferedImage i0 = FileURLIO.readImage(s0);
		BufferedImage i1 = FileURLIO.readImage(s1);
		int expected = (ImageOps.equalsForCodeGen(i0, i1));
		assertEquals(expected, result);
	}

	@Test
	void cg24b() throws Exception {
		String input = """
				int imageEqual(string s0, string s1, int w, int h){
				image[w,h] i0 = s0.
				image[w,h] i1 = s1.
				int eq = i0 == i1.
				:eq.
				}
				""";
		String s0 = beach;
		String s1 = beach;
		int w = 100;
		int h = 200;
		Object[] params = { s0, s1, w, h };
		int result = (int) genCodeAndRun(input, "", params);
		BufferedImage i0 = FileURLIO.readImage(s0, w, h);
		BufferedImage i1 = FileURLIO.readImage(s1, w, h);
		int expected = (ImageOps.equalsForCodeGen(i0, i1));
		assertEquals(expected, result);
	}

	@Test
	void cg24c() throws Exception {
		String input = """
				int imageEqual(string s0, string s1, int w, int h){
				image[w,h] i0 = s0.
				image[w,h] i1 = s1.
				int eq = i0 == i1.
				:eq.
				}
				""";
		String s0 = beach;
		String s1 = owl;
		int w = 100;
		int h = 200;
		Object[] params = { s0, s1, w, h };
		int result = (int) genCodeAndRun(input, "", params);
		BufferedImage i0 = FileURLIO.readImage(s0, w, h);
		BufferedImage i1 = FileURLIO.readImage(s1, w, h);
		int expected = (ImageOps.equalsForCodeGen(i0, i1));
		assertEquals(expected, result);
	}

	@Test
	void cg25() throws Exception {
		String input = """
				image gradient(int size){
				image[size,size] im.
				im[x,y] = [x-y, 0, y-x].
				:im.
				}
				""";
		int size = 400;
		Object[] params = { size };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		show(result);
		BufferedImage expected = ImageOps.makeImage(size, size);
		for (int x = 0; x != expected.getWidth(); x++) {
			for (int y = 0; y != expected.getHeight(); y++) {
				ImageOps.setRGB(expected, x, y, PixelOps.pack((x - y), 0, (y - x)));
			}
		}
		imageEquals(expected, result);
	}

	@Test
	void cg26() throws Exception {
		String input = """
				image flag(int size){
				image[size,size] c.
				int stripeSize = size/2.
				pixel yellow.
				pixel blue.
				yellow = [Z,Z,0].
				blue = [0,0,Z].
				c[x,y] = if (y > stripeSize) ? yellow ? blue .
				:c.
				}
				""";
		int size = 400;
		Object[] params = { size };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage(size, size);
		int stripeSize = (size / 2);
		int yellow;
		int blue;
		yellow = PixelOps.pack(255, 255, 0);
		blue = PixelOps.pack(0, 0, 255);
		for (int x = 0; x != expected.getWidth(); x++) {
			for (int y = 0; y != expected.getHeight(); y++) {
				ImageOps.setRGB(expected, x, y, ((((y > stripeSize) ? 1 : 0) != 0) ? yellow : blue));
			}
		}
		;
		imageEquals(expected, result);
		show(result);
	}

	@Test
	void cg26a() throws Exception {
		String input = """
				image readInAssignment(string s){
				image[100,500] tallOwl.
				tallOwl = s.
				:tallOwl.
				}
				""";
		String s = owl;
		Object[] params = { s };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		show(result);
		BufferedImage tallOwl = ImageOps.makeImage(100, 500);
		ImageOps.copyInto(FileURLIO.readImage(s), tallOwl);
		imageEquals(tallOwl, result);
	}

	@Test
	void cg27() throws Exception {
		String input = """
				image darker(string s){
				image owl = s.
				image darkowl = owl.
				darkowl = owl/3.
				:darkowl.
				}
				""";
		String s = owl;
		Object[] params = { s };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		show(result);
		BufferedImage owlImage = FileURLIO.readImage(s);
		BufferedImage expected = ImageOps.cloneImage(owlImage);
		ImageOps.copyInto((ImageOps.binaryImageScalarOp(ImageOps.OP.DIV, owlImage, 3)), expected);
		imageEquals(expected, result);
	}

	// This test illustrates that it isn't necessary to have only x and y in the
	// selector on the left side. Any expressions work.
	@Test
	void cg28() throws Exception {
		String input = """
				image bently(string s, int w, int h){
				image[w,h] newImage.
				image jlb = s.
				newImage[x, y-(jlb[x,y]:red /4)] = jlb[x,y].
				~newImage[x, y-3] = jlb[x,y].
				:newImage.
				}
				""";
		String s = owl;
		BufferedImage sourceImage = FileURLIO.readImage(s);
		int w = sourceImage.getWidth();
		int h = sourceImage.getHeight();
		Object[] params = { s, w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage newImage = ImageOps.makeImage(w, h);
		BufferedImage jlb = FileURLIO.readImage(s);
		for (int y = 0; y != newImage.getHeight(); y++) {
			for (int x = 0; x != newImage.getWidth(); x++) {
				ImageOps.setRGB(newImage, x, (y - (PixelOps.red(ImageOps.getRGB(jlb, x, y)) / 4)),
						ImageOps.getRGB(jlb, x, y));
			}
		}
		imageEquals(newImage,result);
		show(result);
	}

	@Test
	void cg30() throws Exception {
		String input = """
				image dino (string s, int w, int h){
				image womanAndDino = s.
				image [w/2, h/2] cropped.
				int hshift = 0.
				int vshift = h/2.
				cropped[x,y]= womanAndDino[x+hshift, y+vshift].
				:cropped.
				}
				""";
		String s = dino;
		BufferedImage womanAndDino = FileURLIO.readImage(s);
		int w = womanAndDino.getWidth();
		int h = womanAndDino.getHeight();
		Object[] params = { s, w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		BufferedImage expected = ImageOps.makeImage((w / 2), (h / 2));
		int hshift = 0;
		int vshift = (h / 2);
		for (int x = 0; x != expected.getWidth(); x++) {
			for (int y = 0; y != expected.getHeight(); y++) {
				ImageOps.setRGB(expected, x, y, ImageOps.getRGB(womanAndDino, (x + hshift), (y + vshift)));
			}
		}
		imageEquals(result, expected);
		show(result);
	}

	@Test
	void cg31() throws Exception {
		String input = """
				image f(string url, int w, int h){
				image aa = url.
				int strip = w/4.
				image[w,h] b.
				b[x,y] = if  x%strip < strip/2 ? [aa[x,y]:red,0,0] ? [0,0,aa[x,y]:blu].
				:b.
				}
				""";
		String s = beach;
		BufferedImage sourceImage = FileURLIO.readImage(s);
		int w = sourceImage.getWidth();
		int h = sourceImage.getHeight();
		Object[] params = { s, w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		int strip = (w / 4);
		BufferedImage expected = ImageOps.makeImage(w, h);
		for (int x = 0; x != expected.getWidth(); x++) {
			for (int y = 0; y != expected.getHeight(); y++) {
				ImageOps.setRGB(expected, x, y,
						(((((x % strip) < (strip / 2)) ? 1 : 0) != 0)
								? PixelOps.pack(PixelOps.red(ImageOps.getRGB(sourceImage, x, y)), 0, 0)
								: PixelOps.pack(0, 0, PixelOps.blu(ImageOps.getRGB(sourceImage, x, y)))));
			}

		}
		imageEquals(result, expected);
		show(result);
	}

	@Test
	void cg32() throws Exception {
		String input = """
				image f(string url, int w, int h){
				image aa = url.
				int strip = w/4.
				image[w,h] b.
				b[x,y] = if  x%strip < strip/2 ? aa[x,y]*2 ? aa[x,y]/2.
				:b.
				}
				""";
		String s = beach;
		BufferedImage sourceImage = FileURLIO.readImage(s);
		int w = sourceImage.getWidth();
		int h = sourceImage.getHeight();
		Object[] params = { s, w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		int strip = (w / 4);
		BufferedImage expected = ImageOps.makeImage(w, h);
		for (int x = 0; x != expected.getWidth(); x++) {
			for (int y = 0; y != expected.getHeight(); y++) {
				ImageOps.setRGB(expected, x, y, (((((x % strip) < (strip / 2)) ? 1 : 0) != 0)
						? (ImageOps.binaryPackedPixelScalarOp(ImageOps.OP.TIMES, ImageOps.getRGB(sourceImage, x, y), 2))
						: (ImageOps.binaryPackedPixelScalarOp(ImageOps.OP.DIV, ImageOps.getRGB(sourceImage, x, y),
								2))));
			}
		}
		imageEquals(result, expected);
		show(result);
	}

	@Test
	void cg33() throws Exception {
		String input = """
				image ChessBoard(string url0, string url1, int w, int h){
				image[w,h] im0 = url0.
				image[w,h] im1 = url1.
				int stripH = w/4.
				int stripV = h/4.
				image[w,h] woven.
				woven[x,y] = if  ((x%stripH < stripH/2)&&(y%stripV < stripV/2) || (x%stripH >= stripH/2)&&(y%stripV >= stripV/2)) ? im0[x,y] ? im1[x,y].
				:woven.
				}
				""";
		String s0 = beach;
		String s1 = owl;
		BufferedImage sourceImage0 = FileURLIO.readImage(s0);
		BufferedImage sourceImage1 = FileURLIO.readImage(s1);
		int w = sourceImage0.getWidth();
		int h = sourceImage1.getHeight();
		Object[] params = { s0, s1, w, h };
		BufferedImage result = (BufferedImage) genCodeAndRun(input, "", params);
		int stripH = (w / 4);
		int stripV = (h / 4);
		BufferedImage expected = ImageOps.makeImage(w, h);
		for (int x = 0; x != expected.getWidth(); x++) {
			for (int y = 0; y != expected.getHeight(); y++) {
				ImageOps.setRGB(expected, x, y,
						(((((((((((x % stripH) < (stripH / 2)) ? 1 : 0)) == 0 ? false : true)
								&& (((((y % stripV) < (stripV / 2)) ? 1 : 0)) == 0 ? false : true) ? 1 : 0)) == 0
										? false
										: true)
								|| ((((((((x % stripH) >= (stripH / 2)) ? 1 : 0)) == 0 ? false : true)
										&& (((((y % stripV) >= (stripV / 2)) ? 1 : 0)) == 0 ? false : true) ? 1
												: 0)) == 0 ? false : true) ? 1 : 0) != 0)
														? ImageOps.getRGB(sourceImage0, x, y)
														: ImageOps.getRGB(sourceImage1, x, y)));
			}

		}
		imageEquals(result, expected);
		show(result);
	}

}
