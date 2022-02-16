package com.yusys.bione.frame.util.http;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;

public class IdleConnectionMonitorThread extends Thread {

	private final HttpClientConnectionManager connMgr;

	private volatile boolean shutdown;

	public IdleConnectionMonitorThread(HttpClientConnectionManager connMgr) {
		this.connMgr = connMgr;
	}

	@Override
	public void run() {
		try {
			while (! shutdown) {
				synchronized (this) {
					wait(30000);
				}
				if (shutdown) {
					break;
				}
				// Close expired connections
				connMgr.closeExpiredConnections();
				// Optionally, close connections
				// that have been idle longer than 60 sec
				connMgr.closeIdleConnections(60, TimeUnit.SECONDS);
			}
		} catch (InterruptedException ex) {
		}
	}

	public void shutdown() {
		shutdown = true;
		synchronized (this) {
			notifyAll();
		}
	}
}
