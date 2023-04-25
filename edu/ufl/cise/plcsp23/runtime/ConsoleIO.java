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

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

import java.awt.image.BufferedImage;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class ConsoleIO {

	/**
	 * Destination of "console" output. Can be changed to redirect output. Generated
	 * code should use ConsoleIO.write(...) etc. instead of
	 * System.out.println
	 */
	public static PrintStream console = System.out;

	/**
	 * change destination of console output for non-image types
	 */
	public static void setConsole(PrintStream out) {
		console = out;
	}

	public static void write(String s) {
		console.println(s);
	}

	public static void write(int val) {
		console.println(val);
	}

	public 	static void writePixel(int val) {
		console.println(Integer.toHexString(val));
	}

	public 	static void write(BufferedImage image) {
		displayImageOnScreen(image);
	}


	public static boolean DISPLAY_IMAGES = true;

	public static void displayImageOnScreen(BufferedImage image) {
		if (DISPLAY_IMAGES) {
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
			frame.setSize(image.getWidth(), image.getHeight());
			JLabel label = new JLabel(new ImageIcon(image));
			frame.add(label);
			frame.pack();
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					public void run() {
						frame.setVisible(true);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
}
