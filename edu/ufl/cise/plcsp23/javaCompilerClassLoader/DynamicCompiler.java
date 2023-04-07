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

import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public class DynamicCompiler {
	
	
	/** Compiles java source code provided in the form a  String and returns the class file in the form of a byte array. */
	public static byte[] compile(String fullyQualifiedName, String sourceCode) throws Exception {
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		InMemoryClassFileManager fileManager = new InMemoryClassFileManager(compiler.getStandardFileManager(null, null, null));
		
		List<JavaFileObject> sourceFiles = new ArrayList<>();
		sourceFiles.add(new StringJavaFileObject(fullyQualifiedName, sourceCode));
		
		List<String> options = new ArrayList<>();
		options.add("-classpath");
		options.add("bin");
		
		boolean success = compiler.getTask(null, fileManager, null, options, null, sourceFiles).call();
		if (success) {
			return fileManager.byteCodeObject.getBytes();
		}
		else throw new Exception("error compiling generated code");
		}
	}
	

