package com.yusys.bione.plugin.yuformat.web;

import java.io.Serializable;

/**
 * 拼装Html与JavaScript的VO对象
 * @author xch
 *
 */
public class HtmlJavaScriptVO implements Serializable {

	private String html;

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getJavaScript() {
		return javaScript;
	}

	public void setJavaScript(String javaScript) {
		this.javaScript = javaScript;
	}

	private String javaScript; //
}
