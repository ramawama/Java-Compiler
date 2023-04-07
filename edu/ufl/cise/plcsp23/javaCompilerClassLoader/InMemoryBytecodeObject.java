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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class InMemoryBytecodeObject extends SimpleJavaFileObject {
	
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	
	public InMemoryBytecodeObject(String name, Kind kind) {
        super(URI.create("string:///" + name.replace('.','/') + kind.extension), kind);
	}
	
	public byte[] getBytes() {return bos.toByteArray();}
	
	@Override
	public OutputStream openOutputStream() throws IOException{
		return bos;
	}

}
