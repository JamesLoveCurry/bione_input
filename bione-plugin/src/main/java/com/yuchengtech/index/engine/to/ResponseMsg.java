package com.yuchengtech.index.engine.to;

/**
 * 总任务返回的结果
 * @author xch
 *
 */
public class ResponseMsg extends BaseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String responseResult;

	public ResponseMsg() {
		setType(MsgConstants.RESPONSE);

	}

	public ResponseMsg(String _result) {
		setType(MsgConstants.RESPONSE);
		this.responseResult = _result;
	}

	public String getResponseResult() {
		return responseResult;
	}

	public void setResponseResult(String _responseResult) {
		this.responseResult = _responseResult;
	}

}
