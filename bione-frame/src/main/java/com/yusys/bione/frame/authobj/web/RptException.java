package com.yusys.bione.frame.authobj.web;

import java.io.Serializable;

/**
 * <pre>
 *  Title:
 *  Description: 
 * </pre>
 * 
 * @author 贠磊	yunlei@yuchengtech.com
 * @version 1.00.00
 * 
 * <pre>
 *  修改记录
 *     修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class RptException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1527439486844389137L;

	protected String msg;

	/**
	 * @param msg
	 */
	public RptException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
    public RptException(String msg, Throwable cause) {
        super(msg, cause);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
	
}
