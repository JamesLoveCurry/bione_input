/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

/**
 * @author tanxu
 *
 */
public class SpreadImportException extends RuntimeException {
	
	private static final long serialVersionUID = -4845511236510163839L;
	
	private Throwable throwable;
	
	public SpreadImportException() {
		super();
	}
	
	public SpreadImportException(String detail) {
		super(detail);
	}
	
	public SpreadImportException(Throwable cause) {
		super(cause);
	}
	
	public SpreadImportException(String detail, Throwable cause) {
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
