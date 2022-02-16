package com.yusys.bione.plugin.rptidx.web.vo;

import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;

@SuppressWarnings("serial")
public class RptIdxDimRelVO extends RptDimTypeInfo{
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dsId == null) ? 0 : dsId.hashCode());
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
		RptIdxDimRelVO other = (RptIdxDimRelVO) obj;
		if (this.getDimTypeNo() == null) {
			if (other.getDimTypeNo() != null)
				return false;
		} else if (!this.getDimTypeNo().equals(other.getDimTypeNo()))
			return false;
		return true;
	}

	private String dsId;

	public String getDsId() {
		return dsId;
	}

	public void setDsId(String dsId) {
		this.dsId = dsId;
	}
}
