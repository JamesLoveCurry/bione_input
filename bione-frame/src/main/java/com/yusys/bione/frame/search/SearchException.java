/**
 * 
 */
package com.yusys.bione.frame.search;

/**
 * <pre>
 * Title:检索的异常封装，隔离用户调用接口对底层的依赖
 * Description:
 * </pre>
 * 
 * @author tanxu@yuchengtech.com
 * @version
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:	修改人：		修改日期:	修改内容:
 * </pre>
 */
public class SearchException extends Exception {

	private static final long serialVersionUID = -2283296908560185541L;

	private Throwable throwable;

	public SearchException() {
		super();
	}

	public SearchException(String msg) {
		super(msg);
	}

	public SearchException(Throwable cause) {
		super(cause);
	}

	public SearchException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

	public Throwable getException() {
		throwable = super.getCause();
		return throwable;
	}

}
