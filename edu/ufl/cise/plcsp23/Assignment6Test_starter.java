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
import edu.ufl.cise.plcsp23.runtime.ImageOps.OP;
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

	String peter = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/a1bdc1e4-f501-488b-8005-47d4543d6328/dfbfxu7-b5d74ad3-d3af-41e3-baff-ccc97d84ba99.jpg/v1/fill/w_709,h_1127,q_70,strp/peter_griffin_by_edmodevz_dfbfxu7-pre.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9MjAzNSIsInBhdGgiOiJcL2ZcL2ExYmRjMWU0LWY1MDEtNDg4Yi04MDA1LTQ3ZDQ1NDNkNjMyOFwvZGZiZnh1Ny1iNWQ3NGFkMy1kM2FmLTQxZTMtYmFmZi1jY2M5N2Q4NGJhOTkuanBnIiwid2lkdGgiOiI8PTEyODAifV1dLCJhdWQiOlsidXJuOnNlcnZpY2U6aW1hZ2Uub3BlcmF0aW9ucyJdfQ.p8iaNB7ttQ6t3_GTRq_Juk8TUlYmVqidE672yFHner0";

	String lebron = "https://cdn.nba.com/headshots/nba/latest/1040x760/2544.png";
	String eren = "https://w0.peakpx.com/wallpaper/451/391/HD-wallpaper-eren-yeager-attack-on-titan-eren-yeager-fanart.jpg";

	@Test
	void imageImageTimesDivMod() throws Exception {
		String inputTimes = """
            image p(string s1, string s2, int w, int h) {
                image[w,h] i1 = s1.
                image[w,h] i2 = s2.
                : i1 * i2.
            }
            """;

		String inputDiv = """
            image p(string s1, pixel p, int w, int h) {
                image[w,h] i1 = s1.
                image[w,h] i2 = p.
                : i1 / i2.
            }
            """;

		String inputMod = """
            image p(string s1, pixel p, int w, int h) {
                image[w,h] i1 = s1.
                image[w,h] i2 = p.
                : i1 % i2.
            }
            """;

		int w = 500;
		int h = 800;
		int p = 0xff112233;
		Object[] paramsTimes = {peter, lebron, w, h};
		Object[] paramsDiv = {peter, p, w, h};
		Object[] paramsMod = paramsDiv;

		BufferedImage i1 = ImageOps.copyAndResize(FileURLIO.readImage(peter), w, h);
		BufferedImage i2 = ImageOps.copyAndResize(FileURLIO.readImage(lebron), w, h);
		BufferedImage i3 = ImageOps.setAllPixels(ImageOps.makeImage(w, h), p);

		BufferedImage expectedTimes = ImageOps.binaryImageImageOp(ImageOps.OP.TIMES, i1, i2);
		BufferedImage outputTimes = (BufferedImage) genCodeAndRun(inputTimes, "", paramsTimes);
		show(outputTimes);
		BufferedImage expectedDiv = ImageOps.binaryImageImageOp(ImageOps.OP.DIV, i1, i3);
		BufferedImage outputDiv = (BufferedImage) genCodeAndRun(inputDiv, "", paramsDiv);

		BufferedImage expectedMod = ImageOps.binaryImageImageOp(ImageOps.OP.MOD, i1, i3);
		BufferedImage outputMod = (BufferedImage) genCodeAndRun(inputMod, "", paramsMod);

		imageEquals(expectedTimes, outputTimes);
		imageEquals(expectedDiv, outputDiv);
		imageEquals(expectedMod, outputMod);
	}

	@Test
	void imageScalarTimesDivMode() throws Exception {
		String input = """
            image p(string s1, int l, int w, int h) {
                image[w,h] i1 = s1.
                : i1 %s l.
            }
            """;

		int w = 800;
		int h = 600;
		int l = 3;
		Object[] params = {peter, l, w, h};

		BufferedImage i1 = ImageOps.copyAndResize(FileURLIO.readImage(peter), w, h);

		BufferedImage expectedTimes = ImageOps.binaryImageScalarOp(OP.TIMES, i1, l);
		BufferedImage outputTimes = (BufferedImage) genCodeAndRun(String.format(input, "*"), "", params);

		BufferedImage expectedDiv = ImageOps.binaryImageScalarOp(OP.DIV, i1, l);
		BufferedImage outputDiv = (BufferedImage) genCodeAndRun(String.format(input, "/"), "", params);

		BufferedImage expectedMod = ImageOps.binaryImageScalarOp(OP.MOD, i1, l);
		BufferedImage outputMod = (BufferedImage) genCodeAndRun(String.format(input, "%"), "", params);

		imageEquals(expectedTimes, outputTimes);
		imageEquals(expectedDiv, outputDiv);
		imageEquals(expectedMod, outputMod);

	}


	@Test
	void pixelPixelTimesDivMod() throws Exception {
		String input = """
            pixel p(pixel p1, pixel p2) {
                : p1 %s p2.
            }
            """;

		int p1 = 0xff001122;
		int p2 = 0xff112233;
		Object[] params = {p1, p2};

		int expectedTimes = ImageOps.binaryPackedPixelPixelOp(OP.TIMES, p1, p2);
		int outputTimes = (int) genCodeAndRun(String.format(input, "*"), "", params);

		int expectedDiv = ImageOps.binaryPackedPixelPixelOp(OP.DIV, p1, p2);
		int outputDiv = (int) genCodeAndRun(String.format(input, "/"), "", params);

		int expectedMod = ImageOps.binaryPackedPixelPixelOp(OP.MOD, p1, p2);
		int outputMod = (int) genCodeAndRun(String.format(input, "%"), "", params);

		assertEquals(expectedTimes, outputTimes);
		assertEquals(expectedDiv, outputDiv);
		assertEquals(expectedMod, outputMod);
	}

	@Test
	void pixelScalarTimesDivMod() throws Exception {
		String input = """
            pixel p(pixel p, int s) {
            : p %s s.
            }
            """;
		int p = 0xff112233;
		int s = 4;
		Object[] params = {p, s};

		int expectedTimes = ImageOps.binaryPackedPixelScalarOp(OP.TIMES, p, s);
		int outputTimes = (int) genCodeAndRun(String.format(input, "*"), "", params);

		int expectedDiv = ImageOps.binaryPackedPixelScalarOp(OP.DIV, p, s);
		int outputDiv = (int) genCodeAndRun(String.format(input, "/"), "", params);

		int expectedMod = ImageOps.binaryPackedPixelScalarOp(OP.MOD, p, s);
		int outputMod = (int) genCodeAndRun(String.format(input, "%"), "", params);

		assertEquals(expectedTimes, outputTimes);
		assertEquals(expectedDiv, outputDiv);
		assertEquals(expectedMod, outputMod);
	}

	@Test
	void intIntTimesDivMod() throws Exception {
		String input = """
            int i(int i1) {
            : -(-(i1+i1)*(i1/i1) %s -6).
            }
            """;
		int i1 = 5;
		Object[] params = {i1};

		assertEquals(-(-(i1+i1) * -6), genCodeAndRun(String.format(input, "*"), "", params));
		assertEquals(-(-(i1+i1) / -6), genCodeAndRun(String.format(input, "/"), "", params));
		assertEquals(-(-(i1+i1) % -6), genCodeAndRun(String.format(input, "%"), "", params));
	}

	@Test
	void intIntMinus() throws Exception {
		String input = """
            int m(int i) {
                : -5 - -(i > 0).
            }
            """;
		int i = 10;
		Object[] params = {i};

		assertEquals(i > 0 ? -4 : -5, genCodeAndRun(input, "", params));

	}

	@Test
	void pixelPixelMinus() throws Exception {
		String input = """
            image p(pixel p1, pixel p2, int w, int h) {
                image[w, h] i.
                i = p1 - (p2**2).
                : i.
            }
            """;
		int p1 = 0xff001122;
		int p2 = 0xff048202;
		int w = 500;
		int h = 1000;
		Object[] params = {p1, p2, w, h};
		BufferedImage expected = ImageOps.setAllPixels(ImageOps.makeImage(w, h),
				ImageOps.binaryPackedPixelPixelOp(OP.MINUS, p1, (int) Math.pow(p2, 2)));
		BufferedImage output = (BufferedImage) genCodeAndRun(input, "", params);
		show(output);
		imageEquals(expected, output);
	}

	@Test
	void imageImageMinus() throws Exception {
		String input = """
            image i(string s1, string s2, pixel p, int w, int h) {
                image[w, h] i1.
                image[w, h] i2 = s2.
                p = p * (if w > 250 ? 3 ? 2).
                i1 = s1.
                i1[x,y]:grn = p.
                : i1-i2.
            }
            """;
		String s1 = lebron;
		String s2 = peter;
		int p = 0xff759204;
		int w = 1500;
		int h = 800;
		Object[] params = {s1, s2, p, w, h};

		BufferedImage i1 = ImageOps.makeImage(w, h);
		BufferedImage i2 = FileURLIO.readImage(s2, w, h);
		p = ImageOps.binaryPackedPixelScalarOp(OP.TIMES, p, (w > 250 ? 3 : 2));
		ImageOps.copyInto(FileURLIO.readImage(s1), i1);
		for(int y = 0; y != h; y++)
			for(int x = 0; x != w; x++)
				ImageOps.setRGB(i1, x, y,
						PixelOps.setGrn(ImageOps.getRGB(i1, x, y), p));
		BufferedImage expected = ImageOps.binaryImageImageOp(OP.MINUS, i1, i2);
		BufferedImage output = (BufferedImage) genCodeAndRun(input, "", params);
		show(output);
		imageEquals(expected, output);
	}

	@Test
	void intIntPlus() throws Exception {
		String input = """
            int m(int i, int j) {
            : -(i == j) + ((((i*j>0) > 0) < i) + i - --j).
            }
            """;
		int i = 10;
		int j = 92;
		Object[] params = {i, j};

		assertEquals(-(i == j ? 1 : 0) + ((i*j>0 ? 1 : 0) + i - -(-(j))), (int) genCodeAndRun(input, "", params));
	}

	@Test
	void pixelPixelPlus() throws Exception {
		String input = """
            pixel p(pixel p1, pixel p2) {
            : ((((p1 | p2) & p2 ) | p1) - (p2 + p2)) + ([11,11,11] + p2 - [204,84,58]).
            }
            """;
		int p1 = 0xff850202;
		int p2 = 0xff028593;
		Object[] params = {p1, p2};
		assertEquals(ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.PLUS, ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.MINUS, ((p1|p2)&p2)|p1, ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.PLUS, p2, p2)),
						ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.MINUS, ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.PLUS, PixelOps.pack(11,11,11), p2), PixelOps.pack(204, 84, 58))),
				(int) genCodeAndRun(input, "", params));
	}

	@Test
	void imageImagePlus() throws Exception {
		String input = """
            image i(string s1, string s2, int w, int h) {
                image[w,h] i1 = s1.
                image[w,h] i2 = s2.
                
                i1[x,y] = i2[if w > 500 ? y/2 ? y/3, if x > 400 ? x / 3 ? x / 4].
                : i1 + i2.
            }
            """;
		String s1 = lebron;
		String s2 = eren;
		int w = 1000;
		int h = 800;
		Object[] params = {s1, s2, w, h};

		BufferedImage i1 = FileURLIO.readImage(s1, w, h);
		BufferedImage i2 = FileURLIO.readImage(s2, w, h);

		for(int y = 0; y != h; y++)
			for(int x = 0; x != h; x++)
				ImageOps.setRGB(i1, x, y,
						ImageOps.getRGB(i2, y % 31 == 0 ? y/(x+1) : y/(x+2), x % 31 == 0 ? x / 3 : x / 4));
		BufferedImage expected = ImageOps.binaryImageImageOp(OP.PLUS, i1, i2);
		show(expected);
	}

	@Test
	void stringStringPlus() throws Exception {
		String input = """
            string s(string s1, int w, int h) {
                image[w,h] i2.
                string sI1.
                string sI2.
                image[w,h] i1 = s1.
                i2[x,y] = i1[(x/h)/2,(y/w)/2].
                sI1 = i1[w/2,h/3]:grn.
                sI2 = i2[w-h,h].
                : s1 + ", " + sI1 + ", " + sI2.
            }
            """;

		String s1 = eren;
		int w = 1000;
		int h = 500;
		Object[] params = {s1, w, h};

		BufferedImage i2 = ImageOps.makeImage(w, h);
		String sI1;
		String sI2;
		BufferedImage i1 = FileURLIO.readImage(s1, w, h);
		for(int y = 0; y != h; y++)
			for(int x = 0; x != w; x++)
				ImageOps.setRGB(i2, x, y, ImageOps.getRGB(i1, (x/h)/2, (y/w)/2));
		sI1 = Integer.toString(PixelOps.grn(ImageOps.getRGB(i1, w/2, h/3)));
		sI2 = PixelOps.packedToString(ImageOps.getRGB(i2, w-h, h));
		String expected = s1 + ", " + sI1 + ", " + sI2 ;
		String output = (String) genCodeAndRun(input, "", params);
		show(output);
		assertEquals(expected, output);
	}

	@Test
	void intIntPow() throws Exception {
		String input = """
            int i(int d) {
            : (d+(d>0)*(if d>4 ? 1 ? 3))**(d>(d>(d<d))).
            }
            """;
		int d = 4;
		Object[] params = {d};
		assertEquals((int) Math.pow(d+(d > 0 ? 1 : 0)*(d > 4 ? 1 : 3), d>(d>(d<d ? 1 : 0) ? 1 : 0) ? 1 : 0),
				(int) genCodeAndRun(input, "", params));
	}

	@Test
	void pixelIntPow() throws Exception {
		String input = """
            pixel p(pixel p1, int aa) {
                : p1**((((p1==[6,13,3])+3)>5) + aa % 5).
            }
            """;
		int p1 = 0xff180593;
		int aa = 43;
		Object[] params = {p1, aa};
		assertEquals((int) Math.pow(p1, (((p1 == PixelOps.pack(6, 13, 3) ? 1 : 0) + 3) > 5 ? 1 : 0) + aa % 5),
				genCodeAndRun(input, "", params));
	}

	@Test
	void pixelPixelOrAnd() throws Exception {
		String input = """
            pixel p(pixel p1, int i) {
                pixel p2 = [(i > 65) * 3 + 5, ((i < p1:grn) + 3 * p1:blu), i > p1:red].
                pixel p3 = (p1 | p2) & [i*2, i**3, i].
                : p3.
            }
            """;
		int p1 = 0xff950285;
		int i = 138;
		Object[] params = {p1, i};

		int p2 = PixelOps.pack((i > 65 ? 1 : 0) * 3 + 5, (i < PixelOps.grn(p1) ? 1 : 0) + 3 * PixelOps.blu(p1), i > PixelOps.red(p1) ? 1 : 0);
		int p3 = (p1 | p2) & PixelOps.pack(i*2, (int) Math.pow(i, 3), i);
		int output = (int) genCodeAndRun(input, "", params);
		show(PixelOps.packedToString(output));
		assertEquals(p3, output);
	}

	@Test
	void erenBeach() throws Exception {
		String input = """
            image i(string s1, string s2, string s3, int w, int h) {
                image[w, h] i1 = s1.
                image[w, h] i2 = s2.
                image[w, h] i3 = s3.
                image[w*3, h*3] i4.
                
                i1 = i1:grn.
                i2 = i2:blu.
                i3 = i3:red.
                
                i4 = i1 + i2 + i3.
                
                :i4 - i4 / 3.
            }
            """;

		String s1 = eren;
		String s2 = dino;
		String s3 = beach;
		int w = 700;
		int h = 700;
		Object[] params = {s1, s2, s3, w, h};

		BufferedImage i1 = FileURLIO.readImage(s1, w, h);
		BufferedImage i2 = FileURLIO.readImage(s2, w, h);
		BufferedImage i3 = FileURLIO.readImage(s3, w, h);
		BufferedImage i4 = ImageOps.makeImage(w*3, h*3);

		ImageOps.copyInto(ImageOps.extractGrn(i1), i1);
		ImageOps.copyInto(ImageOps.extractBlu(i2), i2);
		ImageOps.copyInto(ImageOps.extractRed(i3), i3);

		ImageOps.copyInto(ImageOps.binaryImageImageOp(OP.PLUS, i1, ImageOps.binaryImageImageOp(OP.PLUS, i2, i3)), i4);
		ImageOps.copyInto(ImageOps.binaryImageImageOp(OP.MINUS, i4, ImageOps.binaryImageScalarOp(OP.DIV, i4, 3)), i4);

		BufferedImage output = (BufferedImage) genCodeAndRun(input, "", params);
		show(output);
		imageEquals(i4, output);
	}

	@Test
	void erenErenEren() throws Exception {
		String input = """
            image i(string s1, int d) {
                image[d, d] eren1 = s1.
                image[d, d] eren2.
                image[d, d] eren3.
                
                eren2[x,y] = eren1[y,x].
                eren3[x,y] = eren2[d-x, y].
                
                eren1:red = [0,0,0].
                eren2:grn = [0,0,0].
                eren3:blu = [0,0,0].
                
                : eren1 + eren2 + eren3.
            }
            """;

		String s1 = eren;
		int d = 800;
		Object[] params = {s1, d};

		BufferedImage eren1 = FileURLIO.readImage(s1, d, d);
		BufferedImage eren2 = ImageOps.makeImage(d, d);
		BufferedImage eren3 = ImageOps.makeImage(d, d);

		for(int y = 0; y != d; y++)
			for(int x = 0; x != d; x++)
				ImageOps.setRGB(eren2, x, y, ImageOps.getRGB(eren1, y, x));

		for(int y = 0; y != d; y++)
			for(int x = 0; x != d; x++)
				ImageOps.setRGB(eren3, x, y, ImageOps.getRGB(eren2, d-x, y));

		for(int y = 0; y != d; y++)
			for(int x = 0; x != d; x++)
				ImageOps.setRGB(eren1, x, y, PixelOps.red(PixelOps.pack(0,0,0)));

		for(int y = 0; y != d; y++)
			for(int x = 0; x != d; x++)
				ImageOps.setRGB(eren2, x, y, PixelOps.grn(PixelOps.pack(0,0,0)));

		for(int y = 0; y != d; y++)
			for(int x = 0; x != d; x++)
				ImageOps.setRGB(eren3, x, y, PixelOps.blu(PixelOps.pack(0,0,0)));

		BufferedImage expected = ImageOps.binaryImageImageOp(OP.PLUS, eren1, ImageOps.binaryImageImageOp(OP.PLUS, eren2, eren3));
		BufferedImage output = (BufferedImage) genCodeAndRun(input, "", params);
		show(output);
		imageEquals(expected, output);
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

//	@Test
//	void cg24a() throws Exception {
//		String input = """
//				int imageEqual(string s0, string s1){
//				image i0 = s0.
//				image i1 = s1.
//				int eq = i0 == i1.
//				:eq.
//				}
//				""";
//		String s0 = beach;
//		String s1 = beach;
//		Object[] params = { s0, s1 };
//		int result = (int) genCodeAndRun(input, "", params);
//		BufferedImage i0 = FileURLIO.readImage(s0);
//		BufferedImage i1 = FileURLIO.readImage(s1);
//		int expected = (ImageOps.equalsForCodeGen(i0, i1));
//		assertEquals(expected, result);
//	}
//
//	@Test
//	void cg24b() throws Exception {
//		String input = """
//				int imageEqual(string s0, string s1, int w, int h){
//				image[w,h] i0 = s0.
//				image[w,h] i1 = s1.
//				int eq = i0 == i1.
//				:eq.
//				}
//				""";
//		String s0 = beach;
//		String s1 = beach;
//		int w = 100;
//		int h = 200;
//		Object[] params = { s0, s1, w, h };
//		int result = (int) genCodeAndRun(input, "", params);
//		BufferedImage i0 = FileURLIO.readImage(s0, w, h);
//		BufferedImage i1 = FileURLIO.readImage(s1, w, h);
//		int expected = (ImageOps.equalsForCodeGen(i0, i1));
//		assertEquals(expected, result);
//	}

//	@Test
//	void cg24c() throws Exception {
//		String input = """
//				int imageEqual(string s0, string s1, int w, int h){
//				image[w,h] i0 = s0.
//				image[w,h] i1 = s1.
//				int eq = i0 == i1.
//				:eq.
//				}
//				""";
//		String s0 = beach;
//		String s1 = owl;
//		int w = 100;
//		int h = 200;
//		Object[] params = { s0, s1, w, h };
//		int result = (int) genCodeAndRun(input, "", params);
//		BufferedImage i0 = FileURLIO.readImage(s0, w, h);
//		BufferedImage i1 = FileURLIO.readImage(s1, w, h);
//		int expected = (ImageOps.equalsForCodeGen(i0, i1));
//		assertEquals(expected, result);
//	}

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
