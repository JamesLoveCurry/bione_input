package com.yusys.bione.frame.util.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.protocol.HttpContext;

public class TrustAllSecureProtocolSocketFactory implements ConnectionSocketFactory {

	private SSLContext sslcontext = null;

	private SSLContext createSSLContext() {
		SSLContext sslcontext = null;
		try {
			sslcontext = SSLContext.getInstance("SSL");
			sslcontext.init(null,
					new TrustManager[] { new TrustAllTrustManager() },
					new java.security.SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return sslcontext;
	}

	private SSLContext getSSLContext() {
		if (this.sslcontext == null) {
			this.sslcontext = createSSLContext();
		}
		return this.sslcontext;
	}

	// 自定义私有类
	private static class TrustAllTrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
		}

		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	@Override
	public Socket connectSocket(int connectTimeout, Socket sock, HttpHost host,
			InetSocketAddress remoteAddress, InetSocketAddress localAddress,
			HttpContext context) throws IOException {
		SocketAddress remoteaddr = new InetSocketAddress(
				remoteAddress.getAddress().getHostAddress(), remoteAddress.getPort());
		sock.connect(remoteaddr, connectTimeout);
		return sock;
	}

	@Override
	public Socket createSocket(HttpContext context) throws IOException {
		return getSSLContext().getSocketFactory().createSocket();
	}
}
