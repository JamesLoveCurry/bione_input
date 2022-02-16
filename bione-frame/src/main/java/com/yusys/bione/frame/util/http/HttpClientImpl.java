package com.yusys.bione.frame.util.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class HttpClientImpl {

	/**
	 * Defines the timeout in milliseconds used when requesting a connection from the connection manager.
	 */
	private final static int CONNECTION_REQUEST_TIMEOUT = 30000;

	/**
	 * Defines the timeout in milliseconds until a connection is established.
	 */
	private final static int CONNECT_TIMEOUT = 10000;

	/**
	 * Defines the socket timeout (SO_TIMEOUT) in milliseconds, which is the timeout for waiting for data or,
	 * put differently, a maximum period inactivity between two consecutive data packets).
	 */
	private final static int SOCKET_TIMEOUT = 300000;

	/**
	 * Defines period of inactivity in milliseconds after which persistent connections must be re-validated
	 * prior to being leased to the consumer.
	 */
	private final static int VALIDATE_AFTER_INACTIVITY = 300000;

	private ConcurrentMap<Thread, HttpClientContext> contextMap = new ConcurrentHashMap<Thread, HttpClientContext>();

	private ConcurrentMap<String, CloseableHttpClient> httpClientMap = new ConcurrentHashMap<String, CloseableHttpClient>();

	private ConcurrentMap<String, IdleConnectionMonitorThread> monitorThreadMap =
			new ConcurrentHashMap<String, IdleConnectionMonitorThread>();

	private final RequestConfig defaultRequestConfig = RequestConfig.custom()
			.setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
			.setConnectTimeout(CONNECT_TIMEOUT)
			.setSocketTimeout(SOCKET_TIMEOUT)
			.build();

	private final RedirectStrategy redirectStrategy = new RedirectStrategy() {
		@Override
		public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response,
				HttpContext context) throws ProtocolException {
			return null;
		}

		@Override
		public boolean isRedirected(HttpRequest request, HttpResponse response,
				HttpContext context) throws ProtocolException {
			return false;
		}
	};

	private final HttpRequestRetryHandler retryHandler = new StandardHttpRequestRetryHandler();

	protected void destroy() {
		for (Iterator<Entry<String, IdleConnectionMonitorThread>> it = monitorThreadMap.entrySet().iterator();
				it.hasNext();) {
			it.next().getValue().shutdown();
		}
		for (Iterator<Entry<String, CloseableHttpClient>> it = httpClientMap.entrySet().iterator();
				it.hasNext();) {
			IOUtils.closeQuietly(it.next().getValue());
		}
	}

	private HttpClientContext getContext() {
		Thread t = Thread.currentThread();
		HttpClientContext context = contextMap.get(t);
		if (context == null) {
			context = HttpClientContext.create();
			HttpClientContext oldContext = contextMap.putIfAbsent(t, context);
			if (oldContext != null) {
				context = oldContext;
			}
		}
		return context;
	}

	private CloseableHttpClient getHttpClient(String schema) {
		CloseableHttpClient httpClient = httpClientMap.get(schema);
		if (httpClient != null) {
			return httpClient;
		}
		HttpClientBuilder httpClientBuilder = HttpClients.custom()
				.disableCookieManagement()
				.setDefaultRequestConfig(defaultRequestConfig)
				.setRetryHandler(retryHandler)
				.setRedirectStrategy(redirectStrategy);
		PoolingHttpClientConnectionManager connectionManager;
		if ("http".equals(schema)) {
			connectionManager = new PoolingHttpClientConnectionManager();
		} else if ("https".equals(schema)){
			Registry<ConnectionSocketFactory> r = RegistryBuilder.<ConnectionSocketFactory>create()
			        .register("https", new TrustAllSecureProtocolSocketFactory())
			        .build();
			connectionManager = new PoolingHttpClientConnectionManager(r);
		} else {
			return null;
		}
		connectionManager.setValidateAfterInactivity(VALIDATE_AFTER_INACTIVITY);
		httpClientBuilder.setConnectionManager(connectionManager);
		httpClient = httpClientBuilder.build();
		CloseableHttpClient oldHttpClient = httpClientMap.putIfAbsent(schema, httpClient);
		if (oldHttpClient == null) {
			IdleConnectionMonitorThread t = new IdleConnectionMonitorThread(connectionManager);
			monitorThreadMap.put(schema, t);
			t.start();
			return httpClient;
		}
		IOUtils.closeQuietly(httpClient);
		return oldHttpClient;
	}

	private Object sendRequest(HttpRequestBase httpRequest, Map<String, String> headers, File saveFile) throws IOException {
		httpRequest.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip");
		if (headers != null) {
			for (Iterator<Entry<String, String>> it = headers.entrySet().iterator(); it.hasNext(); ) {
				Entry<String, String> entry = it.next();
				httpRequest.addHeader(entry.getKey(), entry.getValue());
			}
		}

		// 构造上下文
		HttpClientContext context = getContext();
		// 获取HttpClient
		CloseableHttpClient httpClient = getHttpClient(httpRequest.getURI().getScheme());
		CloseableHttpResponse httpResponse = null;
		try {
			// 发送HTTP请求
			httpResponse = httpClient.execute(httpRequest, context);
			// 接收返回码
			int httpCode = httpResponse.getStatusLine().getStatusCode();
			if (httpCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| httpCode == HttpStatus.SC_MOVED_TEMPORARILY
					|| httpCode == HttpStatus.SC_SEE_OTHER
					|| httpCode == HttpStatus.SC_TEMPORARY_REDIRECT) {
				return httpResponse.getFirstHeader(HttpHeaders.LOCATION).getValue();
			}
			if (httpCode >= 300) {
				// 只处理2xx的返回码
				throw new IOException("Unprocessed http code: " + httpCode);
			}
			// 获取数据
			HttpEntity contentEntity = httpResponse.getEntity();
			if (saveFile == null) {
				return EntityUtils.toByteArray(contentEntity);
			}
			File tmpFile = new File(saveFile.getAbsolutePath() + ".tmp");
			OutputStream out = null;
			try {
				out = new FileOutputStream(tmpFile);
				contentEntity.writeTo(out);
			} finally {
				IOUtils.closeQuietly(out);
			}
			if (httpResponse.containsHeader(HttpHeaders.LAST_MODIFIED)) {
				Date d = DateUtils.parseDate(httpResponse.getFirstHeader(HttpHeaders.LAST_MODIFIED).getValue());
				tmpFile.setLastModified(d.getTime());
			}
			if (saveFile.exists() && ! saveFile.delete()) {
				throw new IOException("Delete old file fail: " + saveFile.getAbsolutePath());
			}
			if (! tmpFile.renameTo(saveFile)) {
				throw new IOException("Rename temp file to target file fail: " + tmpFile.getAbsolutePath() + " " + saveFile.getAbsolutePath());
			}
			return saveFile;
		} finally {
			// 释放资源
			IOUtils.closeQuietly(httpResponse);
		}
	}

	/**
	 * 向服务器发送HTTP GET请求
	 * 
	 * @param url HTTP请求URL
	 * @param headers 需要增加的HTTP头，可以为null；默认已增加"Accept-Encoding: gzip"
	 * @param saveFile 返回结果存储文件，可以为null
	 * @return HTTP请求返回结果(byte[]：响应内容，或者String：新URL，或者当saveFile非null时，返回saveFile)
	 * @throws IOException
	 */
	protected Object get(String url, Map<String, String> headers, File saveFile) throws IOException {
		// 构造HTTP报文
		HttpGet httpRequest = new HttpGet(url);
		return sendRequest(httpRequest, headers, saveFile);
	}

	/**
	 * 向服务器发送HTTP POST请求
	 * 
	 * @param url HTTP请求URL
	 * @param headers 需要增加的HTTP头，可以为null；默认已增加"Accept-Encoding: gzip"
	 * @param postData 需要通过POST方式传递的数据
	 * @param charset postData的字符编码
	 * @param saveFile 返回结果存储文件，可以为null
	 * @return HTTP请求返回结果(byte[]：响应内容，或者String：新URL，或者当saveFile非null时，返回saveFile)
	 * @throws IOException
	 */
	protected Object post(String url, Map<String, String> headers, String postData, String charset, File saveFile)
			throws IOException {
		// 构造参数体
		StringEntity entity = new StringEntity(postData, charset);
		// 构造HTTP报文
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setEntity(entity);
		return sendRequest(httpRequest, headers, saveFile);
	}

	/**
	 * 向服务器发送HTTP DELETE请求
	 * 
	 * @param url HTTP请求URL
	 * @param headers 需要增加的HTTP头，可以为null；默认已增加"Accept-Encoding: gzip"
	 * @return HTTP请求返回结果(byte[]：响应内容，或者String：新URL)
	 * @throws IOException
	 */
	protected Object delete(String url, Map<String, String> headers) throws IOException {
		// 构造HTTP报文
		HttpDelete httpRequest = new HttpDelete(url);
		return sendRequest(httpRequest, headers, null);
	}
}
