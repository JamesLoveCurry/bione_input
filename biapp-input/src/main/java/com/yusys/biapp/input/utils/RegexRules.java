package com.yusys.biapp.input.utils;

/**
 * 常用正则表达式<br>
 * @author tobe
 */
public class RegexRules {

	public final static String email = "w+([-+.]w+)*@w+([-.]w+)*.w+([-.]w+)*";
	public final static String integer = "^[+|-]?[1-9]\\d*$";
	public final static String number = "^[+|-]?([1-9]\\d*.\\d*|0.\\d*[1-9]\\d*|0?.0+|0)$";
	public final static String url = "[a-zA-z]+://[^s]*";
}
