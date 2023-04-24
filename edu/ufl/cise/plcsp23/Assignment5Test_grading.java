package edu.ufl.cise.plcsp23;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Duration;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plcsp23.ast.AST;
import edu.ufl.cise.plcsp23.ast.Program;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicClassLoader;
import edu.ufl.cise.plcsp23.javaCompilerClassLoader.DynamicCompiler;
import edu.ufl.cise.plcsp23.runtime.ConsoleIO;

import static org.junit.jupiter.api.Assertions.*;

class Assignment5Test_grading {
    static final int TIMEOUT_MILLIS = 1000;

    Object genCodeAndRun(String input, String mypackage, Object[] params) throws Exception {
		show("**** Input ****");
		show(input);
        AST ast = CompilerComponentFactory.makeParser(input).parse();
        ast.visit(CompilerComponentFactory.makeTypeChecker(), null);
        String name = ((Program) ast).getIdent().getName();
        String packageName = "";
        String code = (String) ast.visit(CompilerComponentFactory.makeCodeGenerator(packageName), null);
		show("**** Generated Code ****");
		show(code);
		show("**** Output ****");
        byte[] byteCode = DynamicCompiler.compile(name, code);
        Object result = DynamicClassLoader.loadClassAndRunMethod(byteCode, name, "apply", params);
		show("**** Returned Result ****");
		show(result);
        return result;
    }

    // makes it easy to turn output on and off (and less typing
    // thanSystem.out.println)
    static final boolean VERBOSE = true;

    void show(Object obj) {
        if (VERBOSE) {
            System.out.println(obj);
        }
    }

    @Test
    void cg0() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = "void f(){}";
            Object[] params = {};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(null, result);
        });
    }

    @Test
    void cg1() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = "int f(){:3.}";
            Object[] params = {};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(3, ((Integer) result).intValue());
        });
    }

    @Test
    void cg2() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int aa, int bb){
                       :aa+bb.
                    }""";
            int aa = 5;
            int bb = 7;
            Object[] params = {aa, bb};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(aa + bb, ((Integer) result).intValue());

        });
    }

    @Test
    void cg3() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(string aa, string bb){
                       :aa+bb.
                    }""";
            String aa = "aa";
            String bb = "bb";
            Object[] params = {aa, bb};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(aa + bb, result);
        });
    }

    @Test
    void cg5a() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string c(int val){
                       string ss = if val > 0 ? "greater" ? "not greater".
                       :ss.
                       }
                    """;
            int v = -2;
            Object[] params = {v};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(v > 0 ? "greater" : "not greater", result);
        });
    }

    @Test
    void cg5b() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string c(int val){
                       string ss = if val > 0 ? "greater" ? "not greater".
                       :ss.
                       }
                    """;
            int v = 2;
            Object[] params = {v};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(v > 0 ? "greater" : "not greater", result);
        });
    }

    @Test
    void cg5c() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string c(int val){
                       string ss = if val > 0 ? "greater" ? "not greater".
                       :ss.
                       }
                    """;
            int v = 0;
            Object[] params = {v};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(v > 0 ? "greater" : "not greater", result);
        });
    }

    @Test
    void cg6() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string c(int val){
                       string ss = if val > 0 ? if 1 ? "greater" ? "1" ? "not greater".
                       :ss.
                       }
                    """;
            int v = 2;
            Object[] params = {v};
            Object result = genCodeAndRun(input, "", params);
            show(result);
            assertEquals("greater", result);
        });
    }

    @Test
    void cg7() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    void d(int val){
                    int vv = val/2.
                    write vv.
                    }
                    """;
            int v = 4;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream test = new PrintStream(baos);
            ConsoleIO.setConsole(test);
            Object[] params = {v};
            Object result = genCodeAndRun(input, "", params);
            String output = baos.toString();
            // should write 2 to OUTPUT
            assertEquals(null, result);
            assertTrue(output.equals("2\n") || output.equals("2\r\n"));

        });
    }

    @Test
    void cg8() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int d(int val){
                    int aaa = val.
                    int bbb = aaa.
                    while (bbb>0) {
                       write bbb.
                       bbb = bbb - 1.
                       }.
                       :bbb.
                       }
                    	""";
            int v = 4;
            Object[] params = {v};
            int result = (Integer) genCodeAndRun(input, "", params);
            // should write
            // 4
            // 3
            // 2
            // 1
            // to OUTPUT
            assertEquals(0, result);
        });
    }

    @Test
    void cg9() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int testWhile(int val){
                    int aa = val.
                    int g = aa.
                    write val.
                    while ((g > 0)) {
                      int aa = val/2.
                      write "outer loop:  aa=".
                      write aa.
                      g = (aa%2==0).
                      val = val-1.
                      while (val > 0){
                         int aa = val/5.
                         write "inner loop:  aa=".
                         write aa.
                         val = 0.
                      }.
                      write "outer loop after inner loop:  aa=".
                      write aa.
                    }.
                    : aa.
                    }
                    """;
            int v = 100;
            Object[] params = {v};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(100, result);
        });
    }

    @Test
    void cg10() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int xx, string ss){
                    	:xx.
                    }
                    """;
            int v = 2;
            Object[] params = {v, "ss"};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(v, result);
        });
    }

    @Test
    void cg11() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(string s0, string s1){
                    	:s0 + " " + s1.
                    }
                    """;
            int v = 2;
            Object[] params = {"aa", "ss"};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("aa ss", result);
        });
    }

    @Test
    void cg12() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(string s0, string s1){
                    	int i = 3.
                    	while i {
                    		string s0 = "bb".
                    		:s0 + " " + s1.
                    	}.
                    	: "cc".
                    }
                    """;
            int v = 2;
            Object[] params = {"aa", "ss"};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("bb ss", result);
        });
    }

    @Test
    void cg13() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int i(int i0, int i1){
                    	int i = 12/(4*3).
                    	while i {
                    		int i0 = 10.
                    		:i0 + i1/3.
                    	}.
                    	: 12**4.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 24};
            Object result = genCodeAndRun(input, "", params);
            int expected = (int)Math.round(Math.pow(12,4));
            assertEquals(18, result);
        });
    }

    @Test
    void cg14() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 && i1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 5};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1, result);
        });
    }

    @Test
    void cg15() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 && i1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(0, result);
        });
    }

    @Test
    void cg16() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 || i1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 5};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1, result);
        });
    }

    @Test
    void cg17() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 || i1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1, result);
        });
    }

    @Test
    void cg18() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2 = (((i0 > 0 || i1 > 0)*5)<=5)-1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(0, result);
        });
    }

    @Test
    void cg20() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                    	int i2 = if (i0 > 0 || i1 > 0)?10?100.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("10", result);
        });
    }

    @Test
    void cg22() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2 = i0 ** i1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {5, 4};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(625, result);
        });
    }

    @Test
    void cg23() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2 = (i0 % i1).
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {5, 1};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(0, result);
        });
    }

    @Test
    void cg24() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = i0 && i1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 5};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1, result);
        });
    }

    @Test
    void cg25() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = i0 && i1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(0, result);
        });
    }

    @Test
    void cg26() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = i0 > 0 || i1 > 0.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 5};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1, result);
        });
    }

    @Test
    void cg27() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = i0 || i1.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1, result);
        });
    }

    @Test
    void cg29() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                    	string i2.
                    	i2 = if (i0 > 0 || i1 > 0)?"aa"?"bb".
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("aa", result);
        });
    }

    @Test
    void cg30() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	int i2.
                    	i2 = if (i0 > 0 || i1 > 0)?10?100.
                    	: i2.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(10, result);
        });
    }

    @Test
    void cg31() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                    	while (i0 > 0 && i1 > 0){
                    		: "aa".
                    	}.
                    	: "bb".
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("bb", result);
        });
    }

    @Test
    void cg32() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                        : if Z ? "aa" ? "bb".
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("aa", result);
        });
    }

    @Test
    void cg33() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                    	while (i0 ** i1){
                    		: "aa".
                    	}.
                    	: "bb".
                    }
                    """;
            int v = 2;
            Object[] params = {1, 10};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("aa", result);
        });
    }

    @Test
    void cg34() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                    	while (i0 ** i1){
                    		: "aa".
                    	}.
                    	: "bb".
                    }
                    """;
            int v = 2;
            Object[] params = {0, 10};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("bb", result);
        });
    }

    @Test
    void cg35() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                    	: if (i0 && i1) ? "aa" ? "bb".
                    }
                    """;
            int v = 2;
            Object[] params = {1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("bb", result);
        });
    }

    @Test
    void cg36() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                    	: if (i0 ** i1) ? "aa" ? "bb".
                    }
                    """;
            int v = 2;
            Object[] params = {1, 10};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("aa", result);
        });
    }

    @Test
    void cg37() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    string f(int i0, int i1){
                    	: if (i0 ** i1) ? "aa" ? "bb".
                    }
                    """;
            int v = 2;
            Object[] params = {0, 10};
            Object result = genCodeAndRun(input, "", params);
            assertEquals("bb", result);
        });
    }

    @Test
    void cg38() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	: i0 + i0 * i1 / i0 ** 3 % 6 .
                    }
                    """;
            int v = 2;
            Object[] params = {3, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1331, result);
        });
    }

    @Test
    void cg39() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)>3.
                    }
                    """;
            int v = 2;
            Object[] params = {3, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1, result);
        });
    }

    @Test
    void cg40() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)>=3.
                    }
                    """;
            int v = 2;
            Object[] params = {3, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(1, result);
        });
    }

    @Test
    void cg41() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)==3.
                    }
                    """;
            int v = 2;
            Object[] params = {1, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(0, result);
        });
    }

    @Test
    void cg42() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)<5.
                    }
                    """;
            int v = 2;
            Object[] params = {3, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(0, result);
        });
    }

    @Test
    void cg43() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	: (i0 + i0 * i1 / i0 ** 3 % 6)<=3.
                    }
                    """;
            int v = 2;
            Object[] params = {3, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(0, result);
        });
    }

    @Test
    void cg44() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	while i0{
                    		int i0 = i1.
                    		while i0 {
                    		    int i0 = 2.
                    		    :i0.
                    		}.
                    		:i0.
                    	}.
                    	:5.
                    }
                    """;
            int v = 2;
            Object[] params = {3, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(2, result);
        });
    }

    @Test
    void cg45() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	while i0{
                    		int i2 = i1.
                    		:i2.
                    	}.
                    	:5.
                    }
                    """;
            int v = 2;
            Object[] params = {3, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(8, result);
        });
    }

    @Test
    void cg46() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1){
                    	while i0 > 0{
                    		int i1 = 0.
                    		i0 = i0/2.
                    	}.
                    	while (i0 > 0|| 3 > 0){
                    		:i1.
                    	}.
                    	:5.
                    }
                    """;
            int v = 2;
            Object[] params = {3, 8};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(8, result);
        });
    }

    @Test
    void cg47() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    int f(int i0, int i1, int i2, int i3, int i4, int i5, int i6, int q_, int w_, int e_, int r_, int s_, int t_){
                    	int zz = 32.
                    	while i0{
                    		int q_ = 0.
                    		i0 = i0 - 1.
                    		while i1{
                    			int w_ = 0.
                    			i1 = i1 - 1.
                    			while i2{
                    				int e_ = 0.
                    				i2 = i2 - 1.
                    			}.
                    			while i3{
                    				int r_ = 0.
                    				i3 = i3 - 1.
                    			}.
                    		}.
                    		while i4{
                    			int s_ = 0.
                    			i4 = i4 - 1.
                    		}.
                    		while i5{
                    			int t_ = 0.
                    			i5 = i5 - 1.
                    		}.
                    	}.
                    	while i6{
                    		t_ = 1.
                    		i6 = i6 - 1.
                    	}.
                    	: zz*q_*w_*e_*r_*s_*t_.

                    }
                    """;
            int v = 2;
            Object[] params = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0};
            Object result = genCodeAndRun(input, "", params);
            assertEquals(32, result);
        });
    }

    @Test
    void cg48() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    void d(string val){
                    write val.
                    }
                    """;
            int v = 4;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream test = new PrintStream(baos);
            ConsoleIO.setConsole(test);
            Object[] params = {"abc"};
            Object result = genCodeAndRun(input, "", params);
            String output = baos.toString();
            // should write 2 to OUTPUT
            assertEquals(null, result);
            assertTrue(output.equals("abc\n") || output.equals("abc\r\n"));

        });
    }

    @Test
    void cg49() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    void d(string val){
                    write val + "cc\\t".
                    }
                    """;
            int v = 4;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream test = new PrintStream(baos);
            ConsoleIO.setConsole(test);
            Object[] params = {"abc"};
            Object result = genCodeAndRun(input, "", params);
            String output = baos.toString();
            // should write 2 to OUTPUT
            assertEquals(null, result);
            assertTrue(output.equals("abccc\t\n") || output.equals("abccc\t\r\n"));

        });
    }

    @Test
    void cg50() throws Exception {
        assertTimeoutPreemptively(Duration.ofMillis(TIMEOUT_MILLIS), () -> {
            String input = """
                    void d(int val){
                    string aa = val.
                    write aa + "cc\\t".
                    }
                    """;
            int v = 4;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream test = new PrintStream(baos);
            ConsoleIO.setConsole(test);
            Object[] params = {12};
            Object result = genCodeAndRun(input, "", params);
            String output = baos.toString();
            // should write 2 to OUTPUT
            assertEquals(null, result);
            assertTrue(output.equals("12cc\t\n") || output.equals("12cc\t\r\n"));

        });
    }

}