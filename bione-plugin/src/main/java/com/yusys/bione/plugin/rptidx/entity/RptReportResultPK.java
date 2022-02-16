package com.yusys.bione.plugin.rptidx.entity;

import java.io.Serializable;
import javax.persistence.*;

import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;

/**
 * The primary key class for the RPT_REPORT_RESULT database table.
 * 
 */
@Embeddable
public class RptReportResultPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="INDEX_NO")
	private String indexNo;

	@Column(name="DATA_DATE")
	private String dataDate;

	@Column(name="ORG_NO")
	private String orgNo;

	private String dim1;

	private String dim2;

	private String dim3;

	private String dim4;

	private String dim5;

	private String dim6;

	private String dim7;

	private String dim8;

	private String dim9;

	private String dim10;

	private String currency;

	public RptReportResultPK() {
	}
	
	public RptReportResultPK(String indexNo, String dataDate, String orgNo) {
		this.indexNo = indexNo;
		this.dataDate = dataDate;
		this.orgNo = orgNo;
		this.dim1 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim2 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim3 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim4 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim5 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim6 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim7 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim8 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim9 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.dim10 = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
		this.currency = GlobalConstants4plugin.COMMON_STR_MINUS_SIGN;
	}
	
	public String getIndexNo() {
		return this.indexNo;
	}
	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}
	public String getDataDate() {
		return this.dataDate;
	}
	public void setDataDate(String dataDate) {
		this.dataDate = dataDate;
	}
	public String getOrgNo() {
		return this.orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public String getDim1() {
		return this.dim1;
	}
	public void setDim1(String dim1) {
		this.dim1 = dim1;
	}
	public String getDim2() {
		return this.dim2;
	}
	public void setDim2(String dim2) {
		this.dim2 = dim2;
	}
	public String getDim3() {
		return this.dim3;
	}
	public void setDim3(String dim3) {
		this.dim3 = dim3;
	}
	public String getDim4() {
		return this.dim4;
	}
	public void setDim4(String dim4) {
		this.dim4 = dim4;
	}
	public String getDim5() {
		return this.dim5;
	}
	public void setDim5(String dim5) {
		this.dim5 = dim5;
	}
	public String getDim6() {
		return this.dim6;
	}
	public void setDim6(String dim6) {
		this.dim6 = dim6;
	}
	public String getDim7() {
		return this.dim7;
	}
	public void setDim7(String dim7) {
		this.dim7 = dim7;
	}
	public String getDim8() {
		return this.dim8;
	}
	public void setDim8(String dim8) {
		this.dim8 = dim8;
	}
	public String getDim9() {
		return this.dim9;
	}
	public void setDim9(String dim9) {
		this.dim9 = dim9;
	}
	public String getDim10() {
		return this.dim10;
	}
	public void setDim10(String dim10) {
		this.dim10 = dim10;
	}
	public String getCurrency() {
		return this.currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RptReportResultPK)) {
			return false;
		}
		RptReportResultPK castOther = (RptReportResultPK)other;
		return 
			this.indexNo.equals(castOther.indexNo)
			&& this.dataDate.equals(castOther.dataDate)
			&& this.orgNo.equals(castOther.orgNo)
			&& this.dim1.equals(castOther.dim1)
			&& this.dim2.equals(castOther.dim2)
			&& this.dim3.equals(castOther.dim3)
			&& this.dim4.equals(castOther.dim4)
			&& this.dim5.equals(castOther.dim5)
			&& this.dim6.equals(castOther.dim6)
			&& this.dim7.equals(castOther.dim7)
			&& this.dim8.equals(castOther.dim8)
			&& this.dim9.equals(castOther.dim9)
			&& this.dim10.equals(castOther.dim10)
			&& this.currency.equals(castOther.currency);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.indexNo.hashCode();
		hash = hash * prime + this.dataDate.hashCode();
		hash = hash * prime + this.orgNo.hashCode();
		hash = hash * prime + this.dim1.hashCode();
		hash = hash * prime + this.dim2.hashCode();
		hash = hash * prime + this.dim3.hashCode();
		hash = hash * prime + this.dim4.hashCode();
		hash = hash * prime + this.dim5.hashCode();
		hash = hash * prime + this.dim6.hashCode();
		hash = hash * prime + this.dim7.hashCode();
		hash = hash * prime + this.dim8.hashCode();
		hash = hash * prime + this.dim9.hashCode();
		hash = hash * prime + this.dim10.hashCode();
		hash = hash * prime + this.currency.hashCode();
		
		return hash;
	}
}