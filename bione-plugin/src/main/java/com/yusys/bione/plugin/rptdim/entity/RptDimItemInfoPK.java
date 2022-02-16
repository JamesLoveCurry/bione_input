/**
 * 
 */
package com.yusys.bione.plugin.rptdim.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
@Embeddable
public class RptDimItemInfoPK implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name="DIM_ITEM_NO")
	private String dimItemNo;
	
	@Column(name="DIM_TYPE_NO")
	private String dimTypeNo;

	/**
	 * @return 返回 dimItemNo。
	 */
	public String getDimItemNo() {
		return dimItemNo;
	}

	/**
	 * @param dimItemNo 设置 dimItemNo。
	 */
	public void setDimItemNo(String dimItemNo) {
		this.dimItemNo = dimItemNo;
	}

	/**
	 * @return 返回 dimTypeNo。
	 */
	public String getDimTypeNo() {
		return dimTypeNo;
	}

	/**
	 * @param dimTypeNo 设置 dimTypeNo。
	 */
	public void setDimTypeNo(String dimTypeNo) {
		this.dimTypeNo = dimTypeNo;
	}

	public RptDimItemInfoPK() {
		super();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dimItemNo == null) ? 0 : dimItemNo.hashCode());
		result = prime * result
				+ ((dimTypeNo == null) ? 0 : dimTypeNo.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptDimItemInfoPK other = (RptDimItemInfoPK) obj;
		if (dimItemNo == null) {
			if (other.dimItemNo != null)
				return false;
		} else if (!dimItemNo.equals(other.dimItemNo))
			return false;
		if (dimTypeNo == null) {
			if (other.dimTypeNo != null)
				return false;
		} else if (!dimTypeNo.equals(other.dimTypeNo))
			return false;
		return true;
	}
	
}
