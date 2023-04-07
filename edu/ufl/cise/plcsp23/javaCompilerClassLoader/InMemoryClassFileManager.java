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

import java.io.IOException;
import java.security.SecureClassLoader;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardJavaFileManager;

public class InMemoryClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
	
	InMemoryBytecodeObject byteCodeObject;
	
	public InMemoryClassFileManager(StandardJavaFileManager standardManager) {
		super(standardManager);
	}
	
	@Override
	public ClassLoader getClassLoader(Location location) {
		return new SecureClassLoader() {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException {
				byte[] b = byteCodeObject.getBytes();
				return super.defineClass(name, b, 0, b.length);
			}
		};
	}
	
	public JavaFileObject getJavaFileForOutput(Location location, String name, Kind kind, FileObject sibling) throws IOException{
		byteCodeObject = new InMemoryBytecodeObject(name, kind);
		return byteCodeObject;
	}

}
