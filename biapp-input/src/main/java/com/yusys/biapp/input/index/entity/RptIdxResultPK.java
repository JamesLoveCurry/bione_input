package com.yusys.biapp.input.index.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The persistent class for the rpt_fltsk_flow_node database table.
 * 
 */
@Embeddable
public class RptIdxResultPK implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "INDEX_NO")
	private String indexNo;
	@Column(name = "DATA_DATE")
	private String dataDate;
	@Column(name = "ORG_NO")
	private String orgNo;
	@Column(name = "CURRENCY")
	private String currency;
	@Column(name = "DIM1")
	private String dim1;
	@Column(name = "DIM2")
	private String dim2;
	@Column(name = "DIM3")
	private String dim3;
	@Column(name = "DIM4")
	private String dim4;
	@Column(name = "DIM5")
	private String dim5;
	@Column(name = "DIM6")
	private String dim6;
	@Column(name = "DIM7")
	private String dim7;
	@Column(name = "DIM8")
	private String dim8;
	@Column(name = "DIM9")
	private String dim9;
	@Column(name = "DIM10")
	private String dim10;

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getDataDate() {
		return dataDate;
	}

	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDim1() {
		return dim1;
	}

	public void setDim1(String dim1) {
		this.dim1 = dim1;
	}

	public String getDim2() {
		return dim2;
	}

	public void setDim2(String dim2) {
		this.dim2 = dim2;
	}

	public String getDim3() {
		return dim3;
	}

	public void setDim3(String dim3) {
		this.dim3 = dim3;
	}

	public String getDim4() {
		return dim4;
	}

	public void setDim4(String dim4) {
		this.dim4 = dim4;
	}

	public String getDim5() {
		return dim5;
	}

	public void setDim5(String dim5) {
		this.dim5 = dim5;
	}

	public String getDim6() {
		return dim6;
	}

	public void setDim6(String dim6) {
		this.dim6 = dim6;
	}

	public String getDim7() {
		return dim7;
	}

	public void setDim7(String dim7) {
		this.dim7 = dim7;
	}

	public String getDim8() {
		return dim8;
	}

	public void setDim8(String dim8) {
		this.dim8 = dim8;
	}

	public String getDim9() {
		return dim9;
	}

	public void setDim9(String dim9) {
		this.dim9 = dim9;
	}

	public String getDim10() {
		return dim10;
	}

	public void setDim10(String dim10) {
		this.dim10 = dim10;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currency == null) ? 0 : currency.hashCode());
		result = prime * result
				+ ((dataDate == null) ? 0 : dataDate.hashCode());
		result = prime * result + ((dim1 == null) ? 0 : dim1.hashCode());
		result = prime * result + ((dim10 == null) ? 0 : dim10.hashCode());
		result = prime * result + ((dim2 == null) ? 0 : dim2.hashCode());
		result = prime * result + ((dim3 == null) ? 0 : dim3.hashCode());
		result = prime * result + ((dim4 == null) ? 0 : dim4.hashCode());
		result = prime * result + ((dim5 == null) ? 0 : dim5.hashCode());
		result = prime * result + ((dim6 == null) ? 0 : dim6.hashCode());
		result = prime * result + ((dim7 == null) ? 0 : dim7.hashCode());
		result = prime * result + ((dim8 == null) ? 0 : dim8.hashCode());
		result = prime * result + ((dim9 == null) ? 0 : dim9.hashCode());
		result = prime * result + ((indexNo == null) ? 0 : indexNo.hashCode());
		result = prime * result + ((orgNo == null) ? 0 : orgNo.hashCode());
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
		RptIdxResultPK other = (RptIdxResultPK) obj;
		if (currency == null) {
			if (other.currency != null)
				return false;
		} else if (!currency.equals(other.currency))
			return false;
		if (dataDate == null) {
			if (other.dataDate != null)
				return false;
		} else if (!dataDate.equals(other.dataDate))
			return false;
		if (dim1 == null) {
			if (other.dim1 != null)
				return false;
		} else if (!dim1.equals(other.dim1))
			return false;
		if (dim10 == null) {
			if (other.dim10 != null)
				return false;
		} else if (!dim10.equals(other.dim10))
			return false;
		if (dim2 == null) {
			if (other.dim2 != null)
				return false;
		} else if (!dim2.equals(other.dim2))
			return false;
		if (dim3 == null) {
			if (other.dim3 != null)
				return false;
		} else if (!dim3.equals(other.dim3))
			return false;
		if (dim4 == null) {
			if (other.dim4 != null)
				return false;
		} else if (!dim4.equals(other.dim4))
			return false;
		if (dim5 == null) {
			if (other.dim5 != null)
				return false;
		} else if (!dim5.equals(other.dim5))
			return false;
		if (dim6 == null) {
			if (other.dim6 != null)
				return false;
		} else if (!dim6.equals(other.dim6))
			return false;
		if (dim7 == null) {
			if (other.dim7 != null)
				return false;
		} else if (!dim7.equals(other.dim7))
			return false;
		if (dim8 == null) {
			if (other.dim8 != null)
				return false;
		} else if (!dim8.equals(other.dim8))
			return false;
		if (dim9 == null) {
			if (other.dim9 != null)
				return false;
		} else if (!dim9.equals(other.dim9))
			return false;
		if (indexNo == null) {
			if (other.indexNo != null)
				return false;
		} else if (!indexNo.equals(other.indexNo))
			return false;
		if (orgNo == null) {
			if (other.orgNo != null)
				return false;
		} else if (!orgNo.equals(other.orgNo))
			return false;
		return true;
	}
	
	
}