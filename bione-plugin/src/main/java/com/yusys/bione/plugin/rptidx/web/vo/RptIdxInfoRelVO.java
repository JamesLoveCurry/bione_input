package com.yusys.bione.plugin.rptidx.web.vo;

import java.util.List;

import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;

@SuppressWarnings("serial")
public class RptIdxInfoRelVO extends RptIdxInfo{


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((upNo == null) ? 0 : upNo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RptIdxInfoRelVO other = (RptIdxInfoRelVO) obj;
		if(this.getId().getIndexNo()!=null){
			if (other.getId().getIndexNo() != null)
				return false;
			else if(!this.getId().getIndexNo().equals(other.getId().getIndexNo()))
				return false;
		}
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (upNo == null) {
			if (other.upNo != null)
				return false;
		} else if (!upNo.equals(other.upNo))
			return false;
		return true;
	}

	private String upNo;
	
	private Object value;
	
	private List<RptIdxInfoRelVO> children;

	public List<RptIdxInfoRelVO> getChildren() {
		return children;
	}

	public void setChildren(List<RptIdxInfoRelVO> children) {
		this.children = children;
	}

	public String getUpNo() {
		return upNo;
	}

	public void setUpNo(String upNo) {
		this.upNo = upNo;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
