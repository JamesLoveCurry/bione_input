/**
 * 
 */
package com.yusys.bione.plugin.spreadjs.service;

/**
 * @author tanxu
 *
 */
public class SpreadExportException extends Exception {
	
	private static final long serialVersionUID = -4845511236510163839L;
	
	private Throwable throwable;
	
	public SpreadExportException() {
		super();
	}
	
	public SpreadExportException(String detail) {
		super(detail);
	}
	
	public SpreadExportException(Throwable cause) {
		super(cause);
	}
	
	public SpreadExportException(String detail, Throwable cause) {
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

