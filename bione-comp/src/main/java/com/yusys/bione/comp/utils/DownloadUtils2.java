package com.yusys.bione.comp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
public class DownloadUtils2 {

	/**
	 * 文件下载时，将文件以附件的形式写入Content-disposition中时，某些字符出现问题
	 * 后面的字符读不出来，导致文件名出现问题
	 * 该方法实现了将几个有问题的字符，用_代替
	 * @param fileName
	 * @return
	 */
	private static String safeFileName(String fileName)
	{
		//httpContent-disposition附加附件时，附件名称需要特殊处理的字符
		//将这些字符修改为_
		char AllowedChars[] = ": /;".toCharArray();
	    char[] newFileName = fileName.toCharArray();
	    int j = 0;
	    for (int i = 0; i < newFileName.length; i++)
	    {
	        for(j=0; j < AllowedChars.length; j++){
	        	
	            if(AllowedChars[j] == newFileName[i])
	            	newFileName[i] = '_';
	        }
	    }
	    return new String(newFileName);
	}
	/**
	 * 导出文件
	 * 
	 * @param response
	 * @param file
	 *            导出文件
	 * @param contentType
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, File file) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		long fileLength = file.length();

		response.setContentType("application/octet-stream");
		// old codes
//		response.setHeader("Content-disposition", "attachment; filename="
//				+ new String(file.getName().getBytes("utf-8"), "ISO8859-1"));
		// 修改为：
		String fileName = safeFileName(file.getName());
		response.setHeader("Content-disposition", "attachment; filename="
				+ new String(fileName.getBytes("gbk"), "ISO8859-1") );
		
		//
		response.setHeader("Content-Length", String.valueOf(fileLength));
		bis = new BufferedInputStream(new FileInputStream(file));
		bos = new BufferedOutputStream(response.getOutputStream());
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		bis.close();
		bos.close();
	}
	
	/**
	 * 导出文件
	 * 
	 * @param response
	 * @param file
	 *            导出文件
	 * @param contentType
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, File file,String fileName) throws Exception {
		response.setContentType("text/html;charset=UTF-8");
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		long fileLength = file.length();

		response.setContentType("application/octet-stream");
		// old codes
//		response.setHeader("Content-disposition", "attachment; filename="
//				+ new String(file.getName().getBytes("utf-8"), "ISO8859-1"));
		// 修改为：
		response.setHeader("Content-disposition", "attachment; filename="
				+ new String(fileName.getBytes("gbk"), "ISO8859-1") );
		
		//
		response.setHeader("Content-Length", String.valueOf(fileLength));
		bis = new BufferedInputStream(new FileInputStream(file));
		bos = new BufferedOutputStream(response.getOutputStream());
		byte[] buff = new byte[2048];
		int bytesRead;
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		bis.close();
		bos.close();
	}
}

