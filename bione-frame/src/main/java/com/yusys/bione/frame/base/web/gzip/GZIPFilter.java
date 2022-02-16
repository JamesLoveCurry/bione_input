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
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A filter to wrap all output from the server and Gzip encode it if the client
 * can accept gzip encoded content
 */
public class GZIPFilter implements Filter {

	private static Logger logger = LoggerFactory.getLogger(GZIPFilter.class);
	
	/** The header for accepted encodings */
	private static final String ACCEPT_ENCODING = "Accept-Encoding";

	/** The filter configuration provided by the container */
	private FilterConfig filterConfig;

	/** {@inheritDoc} */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		doFilterInternal((HttpServletRequest) request, (HttpServletResponse) response, chain);
	}

	/**
	 * Filters the response by wrapping the output stream, by checking to see if
	 * the browser will accept gzip encodings, and if they can, temporarily
	 * storing all of the response data and gzipping it before finally sending
	 * it to the client
	 *
	 * @param request
	 *            the request to filter
	 * @param response
	 *            the response to filter
	 * @param chain
	 *            the FilterChain to participate in
	 * @exception IOException
	 *                if there is a problem writing the data to the client
	 * @exception ServletException
	 *                for servlet problems
	 */
	private void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 不压缩JPG、GIF、PNG等静态图片文件
		String url = request.getRequestURI().toLowerCase();
		if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".gif") || url.endsWith(".png") ||
			isIncluded(request) || ! acceptsGzip(request)) {
			chain.doFilter(request, response);
			return;
		}

		// Create a response wrapper that captures servlet output
		// instead of sending it to the client
		GenericResponseWrapper wrapper = new GenericResponseWrapper(response);

		// Let the server perform any work that it must perform,
		// then get the output back
		chain.doFilter(request, wrapper);

		wrapper.close();
	}

	/**
	 * Print a configuration banner including your Version, as well as any
	 * configuration information you have
	 */
	private void printBanner() {
		logger.debug("$Id: GZIPFilter.java,v 1.15 2005/03/12 01:52:29 mike Exp $");
	}

	/** {@inheritDoc} */
	public void init(FilterConfig filterConfig) throws ServletException {
		setFilterConfig(filterConfig);
		printBanner();
	}

	/** {@inheritDoc} */
	public void destroy() {
		setFilterConfig(null);
	}

	/**
	 * Sets the <code>FilterConfig</code> for this filter's web.xml
	 * registration.
	 * 
	 * @param filterConfig
	 *            the <code>FilterConfig</code> for this filter's web.xml
	 *            registration. This is required by WebLogic 6.1's filter
	 *            implementation.
	 */
	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	/**
	 * Returns the <code>FilterConfig</code> for this filter's web.xml
	 * registration.
	 * 
	 * @return the <code>FilterConfig</code> for this filter's web.xml
	 *         registration
	 */
	public FilterConfig getFilterConfig() {
		return filterConfig;
	}

	/**
	 * Make sure that the request isn't actually an internal redirect, gzipping
	 * content as it bashed around inside the server isn't a good idea
	 * 
	 * @param request
	 *            the request to check for internal referencing
	 * @return true if the request is included
	 */
	private boolean isIncluded(ServletRequest request) {
		String uri = (String)request.getAttribute("javax.servlet.include.request_uri");
		return uri != null;
	}

	/**
	 * Examine the request to make sure that the browser can accept gzip
	 * encodings
	 * 
	 * @param request
	 *            the request to examine for gzip acceptance
	 * @return boolean true if the request can accept gzip
	 */
	private boolean acceptsGzip(HttpServletRequest request) {
		return headerContains(request, ACCEPT_ENCODING, "gzip");
	}

	/**
	 * Utility method to examine the given request, find the given header and
	 * see if the header contains the provided value
	 * 
	 * @param request
	 *            the request to examine
	 * @param header
	 *            the specific header to check the value on
	 * @param value
	 *            the value to look for in the header
	 * @return boolean true if the header contains the given value
	 */
	@SuppressWarnings("unchecked")
	private boolean headerContains(HttpServletRequest request, String header, String value) {
		Enumeration<String> accepted = (Enumeration<String>)request.getHeaders(header);
		while (accepted.hasMoreElements()) {
			String headerValue = accepted.nextElement();
			if (headerValue.indexOf(value) != -1) {
				return true;
			}
		}
		return false;
	}
}
