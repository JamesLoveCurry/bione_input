package com.yusys.bione.frame.base.web.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class DataTransferOutputStream extends OutputStream {

	/** The header for accepted encodings */
	private static final String ACCEPT_ENCODING = "Accept-Encoding";

	/** The header for content encodings */
	private static final String CONTENT_ENCODING = "Content-Encoding";

	/**
	 * 缓存区中的最大数据量
	 */
	public static final int MAX_DATA_LENGTH = 512;

	/**
	 * 缓存区
	 */
	private byte[] buffer;

	/**
	 * 缓存区数据长度，为-1时表示缓存区已溢出，需要根据gzOutput是否为空判断是否需要压缩
	 */
	private int length;

	/**
	 * 单字节缓存区
	 */
	private byte[] singleByte;

	private HttpServletResponse response;

	private GZIPOutputStream gzOutput;

	public DataTransferOutputStream(HttpServletResponse response) {
		this.response = response;
		buffer = new byte[MAX_DATA_LENGTH];
		singleByte = new byte[1];
	}

	/**
	 * 是否需要压缩
	 */
	private boolean needCompress() {
		String contentType = response.getContentType();
		return ! response.containsHeader(CONTENT_ENCODING) && StringUtils.isNotEmpty(contentType)
				&& ! contentType.startsWith("application/zip") && ! contentType.startsWith("image/")
				&& ! contentType.startsWith("audio/") && ! contentType.startsWith("video/")
				&& ! contentType.startsWith("application/x-jpg") && ! contentType.startsWith("application/x-png");
	}

	@Override
	public void write(int b) throws IOException {
		singleByte[0] = (byte)b;
		write(singleByte, 0, 1);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		if (gzOutput != null) {
			// 缓存已溢出，需要压缩输出的
			gzOutput.write(b, off, len);
		} else if (length == -1) {
			// 缓存已溢出，不需要压缩输出的
			response.getOutputStream().write(b, off, len);
		} else if (length + len <= MAX_DATA_LENGTH) {
			// 加上新数据，缓存仍不溢出的
			System.arraycopy(b, off, buffer, length, len);
			length += len;
		} else if (needCompress()){
			// 加上新数据，缓存将溢出，且需要压缩输出的
			
			// Make sure that proxies know that content will be returned
			// differently based on the Accept-Encoding request header; this
			// prevents them from caching the gzip'd response and returning it
			// to non-gzip-aware clients
			response.addHeader("Vary", ACCEPT_ENCODING);

			response.setHeader(CONTENT_ENCODING, "gzip");
			gzOutput = new GZIPOutputStream(response.getOutputStream());
			gzOutput.write(buffer, 0, length);
			gzOutput.write(b, off, len);
			length = -1;
		} else {
			// 加上新数据，缓存将溢出，且不需要压缩输出的
			response.getOutputStream().write(buffer, 0, length);
			response.getOutputStream().write(b, off, len);
			length = -1;
		}
	}

	@Override
	public void flush() throws IOException {
		if (gzOutput == null) {
			response.getOutputStream().flush();
		} else {
			gzOutput.flush();
		}
	}

	@Override
	public void close() throws IOException {
		if (length > 0) {
			response.getOutputStream().write(buffer, 0, length);
		} else if (gzOutput != null) {
			gzOutput.finish();
			gzOutput.close();
			gzOutput = null;
		}
		length = 0;
		response.getOutputStream().flush();
	}
}
