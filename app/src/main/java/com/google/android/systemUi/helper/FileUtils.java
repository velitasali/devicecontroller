package com.google.android.systemUi.helper;

import java.io.*;

public class FileUtils
{
	public static File appendText(File file, String what) throws IOException
	{
		if (!file.exists())
			file.createNewFile();

		// order is important if you change it file cannot be read because of lock

		String readBefore = readFileString(file);
		String nowWrite = (readBefore.equals("")) ? what : what + "\n" + readBefore;

		writeFile(file, nowWrite);

		return file;
	}

	public static boolean isFile(String folderName)
	{
		return new File(folderName).isFile();
	}

	public static boolean isDirectory(String folderName)
	{
		return new File(folderName).isDirectory();
	}

	public static ByteArrayOutputStream readFile(File file) throws FileNotFoundException, IOException
	{
		FileInputStream os = new FileInputStream(file);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		while (os.available() > 0)
		{
			baos.write(os.read());
		}

		return baos;
	}

	public static String readFileString(File file) throws FileNotFoundException, IOException
	{
		return readFile(file).toString();
	}

	public static void writeFile(File file, String write) throws IOException
	{
		FileWriter writer = new FileWriter(file);

		writer.write(write);

		writer.flush();
		writer.close();
	}

	public static void writeFile(File file, byte[] write) throws IOException
	{
		FileOutputStream writer = new FileOutputStream(file);

		writer.write(write);

		writer.flush();
		writer.close();
	}
}
