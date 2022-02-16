package com.yuchengtech.index.engine.to;

/**
 * 总的客户端请求的数据包!
 * @author xch
 *
 */
public class RequestMsg extends BaseMsg {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String requestPar;

	public RequestMsg() {
		setType(MsgConstants.REQUEST);
	}

	public RequestMsg(String _par) {
		setType(MsgConstants.REQUEST);
		this.requestPar = _par;
	}

	public String getRequestPar() {
		return requestPar;
	}

	public void setRequestPar(String _requestParStr) {
		this.requestPar = _requestParStr;
	}

}
