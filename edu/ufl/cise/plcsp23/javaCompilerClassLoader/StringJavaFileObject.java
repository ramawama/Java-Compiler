/*Copyright 2023 by Beverly A Sanders
 * 
 * This code is provided for solely for use of students in COP4020 Programming Language Concepts at the 
 * University of Florida during the spring semester 2023 as part of the course project.  
 * 
 * No other use is authorized. 
 * 
 * This code may not be posted on a public web site either during or after the course.  
 */

package edu.ufl.cise.plcsp23.javaCompilerClassLoader;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;
/**
 * 
 * A FileObject used to represent Java source provided in a String.  Required for implementation of DynamicCompiler
 * 
 * @Adopted from https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/tools/JavaCompiler.html
 *
 */

public class StringJavaFileObject extends SimpleJavaFileObject {
	
	final String code;  //The string containing the source code
	
	/**
	 * @param name     name of class
	 * @param code     source code for class
	 */
	public StringJavaFileObject(String name, String code) {
        super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),
                Kind.SOURCE);
          this.code = code;		
	}

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
        return code;
    }
}
	


