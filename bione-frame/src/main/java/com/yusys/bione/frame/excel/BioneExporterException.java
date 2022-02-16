/**
 * 
 */
package com.yusys.bione.frame.excel;

/**
 * @author tanxu
 *
 */
public class BioneExporterException extends Exception {

	private static final long serialVersionUID = 2518046408001115452L;
	
	private Throwable throwable;
	
	public BioneExporterException() {
		super();
	}
	
	public BioneExporterException(String detail) {
		super(detail);
	}
	
	public BioneExporterException(Throwable cause) {
		super(cause);
	}
	
	public BioneExporterException(String detail, Throwable cause) {
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
