package com.yusys.bione.plugin.datashow.web.vo;

import com.yusys.bione.comp.common.CommonTreeNode;

/**
 * <pre>
 * Title: RptTreeNode
 * Description:
 * </pre>
 * 
 * @author kanglg kanglg@yuchengtech.com
 * @version 1.00.00
 */
public class RptTreeNode extends CommonTreeNode {
//	private boolean open = false;
	private static final long serialVersionUID = 1L;
	private boolean nocheck = false;
	private String openStr = "false";
	private String nocheckStr = "false";

	public void setRealId(String id) {
		getParams().put("realId", id);
	}

	public void setIndexVerId(String indexVerId) {
		getParams().put("indexVerId", indexVerId);
	}

	public void setType(String type) {
		getParams().put("type", type);
	}

	public void setRptType(String type) {
		getParams().put("rptType", type);
	}

	public void setCfgId(String cfgId) {
		getParams().put("cfgId", cfgId);
	}

//	public boolean getOpen() {
//		return this.open;
//	}
//
//	public void setOpen(boolean open) {
//		this.open = open;
//	}

	public boolean isNocheck() {
		return nocheck;
	}

	public void setNocheck(boolean nocheck) {
		this.nocheck = nocheck;
	}

	/**
	 * @return 返回 openStr。
	 */
	public String getOpenStr() {
		return openStr;
	}

	/**
	 * @param openStr
	 *            设置 openStr。
	 */
	public void setOpenStr(String openStr) {
		this.openStr = openStr;
//		if ("true".equals(openStr)) {
//			this.open = true;
//		} else {
//			this.open = false;
//		}
	}

	/**
	 * @return 返回 nocheckStr。
	 */
	public String getNocheckStr() {
		return nocheckStr;
	}

	/**
	 * @param nocheckStr
	 *            设置 nocheckStr。
	 */
	public void setNocheckStr(String nocheckStr) {
		this.nocheckStr = nocheckStr;
		if ("true".equals(nocheckStr)) {
			this.nocheck = true;
		} else {
			this.nocheck = false;
		}
	}

}
