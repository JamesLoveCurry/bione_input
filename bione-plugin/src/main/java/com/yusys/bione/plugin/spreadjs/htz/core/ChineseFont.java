package com.yusys.bione.plugin.spreadjs.htz.core;

import java.util.regex.Pattern;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.itextpdf.text.pdf.BaseFont;


/**
 * 中文字体设置
 */
public final class ChineseFont {
	/** 中文字体 */
	public static BaseFont BASE_CHINESE_FONT = null;
	
	// 初始化中文字体
	static {
		try {
			String path = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
					.getSession().getServletContext().getRealPath("/");
			// 装载中文字体，可能需要1-3秒的时间
			BASE_CHINESE_FONT = BaseFont.createFont(path+"/font/simsun.ttf", BaseFont.IDENTITY_H,BaseFont.NOT_EMBEDDED); 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 判断字符串是否有中文
	 * @param s - 要判断的字符串
	 * @return
	 */
	public static boolean containsChinese(String s) {
		if (s == null || s.length() == 0)
			return false;
		return Pattern.compile("[\u0391-\uFFE5]+").matcher(s).find();
	}
	
	private ChineseFont() {}
}
