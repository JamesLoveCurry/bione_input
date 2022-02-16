package com.yusys.bione.frame.util.http;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.utils.SpringContextHolder;

@Component
public class HttpClient implements InitializingBean, DisposableBean {

	/**
	 * 考虑到HttpClient功能用的地方不多，因此其实现部分采用了类似lazy-init的方案，即用即取
	 */
	private AtomicReference<HttpClientImpl> httpClientImplRef = new AtomicReference<HttpClientImpl>();

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Override
	public void destroy() throws Exception {
		HttpClientImpl httpClientImpl = httpClientImplRef.get();
		if (httpClientImpl != null) {
			httpClientImpl.destroy();
		}
	}

	private HttpClientImpl getHttpClientImpl() {
		HttpClientImpl httpClientImpl = httpClientImplRef.get();
		if (httpClientImpl == null) {
			httpClientImpl = new HttpClientImpl();
			if (! httpClientImplRef.compareAndSet(null, httpClientImpl)) {
				httpClientImpl = httpClientImplRef.get();
			}
		}
		return httpClientImpl;
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
	public static Object get(String url, Map<String, String> headers, File saveFile) throws IOException {
		return getInstance().getHttpClientImpl().get(url, headers, saveFile);
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
	public static Object post(String url, Map<String, String> headers, String postData, String charset, File saveFile)
			throws IOException {
		return getInstance().getHttpClientImpl().post(url, headers, postData, charset, saveFile);
	}

	/**
	 * 向服务器发送HTTP DELETE请求
	 * 
	 * @param url HTTP请求URL
	 * @param headers 需要增加的HTTP头，可以为null；默认已增加"Accept-Encoding: gzip"
	 * @return HTTP请求返回结果(byte[]：响应内容，或者String：新URL)
	 * @throws IOException
	 */
	public static Object delete(String url, Map<String, String> headers) throws IOException {
		return getInstance().getHttpClientImpl().delete(url, headers);
	}
	
	private static HttpClient getInstance() {
		return SpringContextHolder.getBean("httpClient");
	}
}
