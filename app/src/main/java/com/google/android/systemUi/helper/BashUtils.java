package com.google.android.systemUi.helper;

import java.io.*;

public class BashUtils
{
	public static void write(OutputStream os, String command) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(os);
		
		dos.writeBytes(command);
		dos.flush();
	}
	
	public static String readAll(InputStream is) throws IOException
	{
		DataInputStream dis = new DataInputStream(is);
		
		String out = "";
		
		while (dis.available() > 0)
			out += dis.readLine();
			
		return out;
	}
}
