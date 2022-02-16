/**
 * 
 */
package com.yusys.bione.frame.message.messager;

/**
 * @author	tanxu
 * @email	tanxu@yuchengtech.com
 * @date	2014-1-21
 */
public class BioneMessageException extends Exception {

	private static final long serialVersionUID = -5164589250921060243L;
	
	private Throwable throwable;
	
	public BioneMessageException() {
		super();
	}
	
	public BioneMessageException(String detail) {
		super(detail);
	}
	
	public BioneMessageException(String detail, Throwable cause) {
		super(detail);
		initCause(null);
		throwable = cause;
	}
	
	public Throwable getCause() {
        return throwable;
    }
	
	@Override
	public String getMessage() {
		if (throwable == null) {
			return super.getMessage();
		} else {
			return super.getMessage() + "; nested exception is: \n\t" + throwable.toString();
		}
	}
}
