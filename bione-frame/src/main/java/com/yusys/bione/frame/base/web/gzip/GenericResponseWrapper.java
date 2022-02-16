/* Copyright 2005 Tacit Knowledge LLC
 * 
 * Licensed under the Tacit Knowledge Open License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.tacitknowledge.com/licenses-1.0.
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yusys.bione.frame.base.web.gzip;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;

/**
 * Wraps the HttpServletResponseWrapper
 *
 * @author Scott Askew (scott@tacitknowledge.com)
 * @version $Id: GenericResponseWrapper.java,v 1.4 2005/02/24 16:01:37 mike Exp
 *          $
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper {

	private DataTransferOutputStream dataTransferOutputStream;

	private ServletOutputStream servletOutputStream;

	private PrintWriter printWriter;

	/** The type of the content to write */
	private String contentType;

	/**
	 * Wrap a normal servlet response so that we can transform the data before
	 * it gets sent to the underlying stream
	 *
	 * @param response
	 *            the normal servlet response to wrap
	 */
	public GenericResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	private DataTransferOutputStream getDataTransferOutputStream() {
		if (dataTransferOutputStream == null) {
			dataTransferOutputStream = new DataTransferOutputStream((HttpServletResponse)this.getResponse());
		}
		return dataTransferOutputStream;
	}

	/**
	 * Get the output stream for this response
	 *
	 * @return a FilterServletOutputStream wrapping the temporary data
	 */
	public ServletOutputStream getOutputStream() {
		if (servletOutputStream == null) {
			servletOutputStream = new FilterServletOutputStream(getDataTransferOutputStream());
		}
		return servletOutputStream;
	}

	/**
	 * Get a Writer version of the OutputStream
	 *
	 * @return PrintWriter wrapping a FilterServletOutputStream
	 */
	public PrintWriter getWriter() throws IOException {
		if (printWriter == null) {
			String encoding = super.getCharacterEncoding();
			if (encoding == null) {
				encoding = "UTF-8";
			}
			printWriter = new PrintWriter(new OutputStreamWriter(getDataTransferOutputStream(), encoding));
		}
		return printWriter;
	}

	/**
	 * 释放资源
	 */
	public void close() {
		IOUtils.closeQuietly(printWriter);
		IOUtils.closeQuietly(servletOutputStream);
		IOUtils.closeQuietly(dataTransferOutputStream);
	}

	@Override
	public void setContentLength(int val) {
	}

	@Override
	public void setContentType(String type) {
		contentType = type;
		super.setContentType(type);
	}

	@Override
	public String getContentType() {
		return contentType;
	}
}
