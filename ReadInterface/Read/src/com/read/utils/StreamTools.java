package com.read.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamTools {

	public static String readStream(InputStream in) throws IOException {
		InputStreamReader outputStream = new InputStreamReader(in, "UTF-8");
		StringBuilder builder = new StringBuilder();
		char[] chars = new char[1024];
		int len = -1;
		while ((len = outputStream.read(chars)) != -1) {
			builder.append(new String(chars, 0, len));
		}
		return builder.toString();
	}

	public static String readStream(InputStream in, String charsetName) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int len = -1;
		byte[] bytes = new byte[1024];
		while ((len = in.read(bytes)) != -1) {
			outputStream.write(bytes, 0, len);
		}
		in.close();
		String content = outputStream.toString();
		outputStream.close();
		return new String(content.getBytes(), charsetName);
	}
}
