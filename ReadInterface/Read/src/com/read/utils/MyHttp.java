package com.read.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyHttp {

	/**
	 * 发送网络请求
	 * @param path             请求的路径
	 * @param type             请求的方式
	 * @param onStreamListener 回调的接口
	 */
	public static void send(String path, OnStreamListener onStreamListener) {
		try {
			URL url = new URL(path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36");
			connection.setReadTimeout(5000);
			connection.setReadTimeout(5000);
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = connection.getInputStream();
				onStreamListener.onStreamListener(inputStream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public interface OnStreamListener {
		public void onStreamListener(InputStream inputStream);
	}
}
