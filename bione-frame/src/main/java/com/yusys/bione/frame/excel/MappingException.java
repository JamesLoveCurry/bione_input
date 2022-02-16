/**
 * 
 */
package com.yusys.bione.frame.excel;

/**
 * @author tanxu
 * 
 */
public class MappingException extends RuntimeException {

	private static final long serialVersionUID = -6257594990742941211L;

	public MappingException(String message) {
		super(message);
	}

	public MappingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MappingException(Throwable message) {
		super(message);
	}
}
