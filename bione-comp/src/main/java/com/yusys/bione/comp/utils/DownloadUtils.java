package com.yusys.bione.comp.utils;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class DownloadUtils {
	
	/**
	 * 导出文件
	 * 
	 * @param response
	 * @param file 导出文件
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, File file)
			throws IOException {
		download(response, file, file.getName(), null);
	}

	/**
	 * 导出文件
	 * 
	 * @param response
	 * @param file 导出文件
	 * @param fileName
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, File file,
			String fileName) throws IOException {
		download(response, file, fileName, null);
	}

	/**
	 * 导出文件
	 * 
	 * @param response
	 * @param file 导出文件
	 * @param fileName
	 * @param contentType
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, File file,
			String fileName, String contentType) throws IOException {
		printHeader(response, fileName, contentType);
		response.setHeader("Content-Length", String.valueOf(file.length()));
		FileUtils.copyFile(file, response.getOutputStream());
	}

	/**
	 * 在response中设置ContentType和Content-disposition
	 * 
	 * @param response
	 * @param fileName
	 * @param contentType
	 * @return response输出流
	 */
	public static void printHeader(HttpServletResponse response, String fileName, String contentType)
			throws IOException {
		response.setContentType(StringUtils.isEmpty(contentType) ?
				"application/octet-stream" : contentType);
		fileName = URLEncoder.encode(fileName, "UTF-8");
		StringBuilder sb = new StringBuilder();
		sb.append("attachment; filename=\"").append(fileName).append('"');
		sb.append("; filename*=utf-8''").append(fileName);
		response.setHeader("Content-disposition", sb.toString());
	}
}
