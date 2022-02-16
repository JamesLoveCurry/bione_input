package com.yusys.bione.frame.mainpage.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the BIONE_MP_DESIGN_FUNC database table.
 * 
 */
@Entity
@Table(name="BIONE_MP_DESIGN_FUNC")
@NamedQuery(name="BioneMpDesignFunc.findAll", query="SELECT b FROM BioneMpDesignFunc b")
public class BioneMpDesignFunc  implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="DESIGN_ID")
	private String designId;

	@Column(name="DESIGN_NM")
	private String designNm;

	@Column(name="LAYOUT_ID")
	private String layoutId;

	@Column(name="LOGIC_SYS_NO")
	private String logicSysNo;

	private String remark;
	
	public BioneMpDesignFunc() {
	}

	public String getDesignId() {
		return this.designId;
	}

	public void setDesignId(String designId) {
		this.designId = designId;
	}

	public String getDesignNm() {
		return this.designNm;
	}

	public void setDesignNm(String designNm) {
		this.designNm = designNm;
	}

	public String getLayoutId() {
		return this.layoutId;
	}

	public void setLayoutId(String layoutId) {
		this.layoutId = layoutId;
	}

	public String getLogicSysNo() {
		return this.logicSysNo;
	}

	public void setLogicSysNo(String logicSysNo) {
		this.logicSysNo = logicSysNo;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}