package com.fun.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;


public class FileUtil {
	public static String fileToString(File f) throws IOException {
		if (f != null) {
			Reader read = new InputStreamReader(new FileInputStream(f), "utf-8");
			char[] c = new char[(int) f.length()];
			//int len = 0;
			StringBuffer PageTemplet =new StringBuffer();
			read.read(c);
			PageTemplet.append(c);
			read.close();
			return PageTemplet.toString();
		}
		return null;
	}
	synchronized public static void stringToFile(String s,File f) throws IOException {
		if (f != null) {
			if(!f.exists()) {
				f.createNewFile();
			}
		BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f),"utf-8"));
		bf.write(s);
		bf.flush();
		bf.close();
		}
	}
}
