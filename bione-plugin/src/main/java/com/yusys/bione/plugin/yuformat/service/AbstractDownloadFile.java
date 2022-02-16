package com.yusys.bione.plugin.yuformat.service;

import java.io.InputStream;

import javax.servlet.ServletOutputStream;

import com.alibaba.fastjson.JSONObject;

//下载文件的抽象类,凡是使用平台下载工具下载文件的,统一用这个函数
public abstract class AbstractDownloadFile {

	public static int DownLoadAsInputStream = 1; //输入流

	public static int DownLoadAsString = 2; //返回字符串
	public static int DownLoadAsBytes = 3; //返回字节

	public static int WriteServletOutputStream = 4; //直接输出流

	//设置下载类型
	public abstract int getDownloadType();

	//下载内容是一个输入流对象,之所以是个对象,是因为必须要知道输入流的大小,浏览器的下载进度才准确!
	public DownLoadInStreamVO getDownLoadContentAsInputStream(JSONObject _par) throws Exception {
		return null; //
	}

	//下载内容是字符串
	public String getDownLoadContentAsString(JSONObject _par) throws Exception {
		return null; //
	}

	//下载文件内容!
	public byte[] getDownLoadContentAsBytes(JSONObject _par) throws Exception {
		return null; //
	}

	//直接输出
	public void downLoaByWriteServletOut(JSONObject _par, ServletOutputStream _servertOut) throws Exception {

	}

	//专门用于下载的输入流的对象,之所以是在InputStream外面再包装一个对象,是因为必须要知道输入流的大小,浏览器的下载进度才准确!
	public class DownLoadInStreamVO {
		private InputStream inputStream; //输入流
		private long fileLength = 5242880; //文件大小默认是5M

		public InputStream getInputStream() {
			return inputStream;
		}

		public void setInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
		}

		public long getFileLength() {
			return fileLength;
		}

		public void setFileLength(long fileLength) {
			this.fileLength = fileLength;
		}

	}

}
