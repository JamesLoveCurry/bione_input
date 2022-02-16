package com.yusys.bione.plugin.frsorg.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="rpt_fim_addr_info")
public class RptFimAddrInfo {
	@Id
	@Column(name="ORG_NO")
	private String orgNo;
	//地区编号
	@Column(name="ADDR_NO")
	private String addrNo;
	//地区名称
	@Column(name="ADDR_NM")
	private String addrNm;
	//地区层级
	@Column(name="ADDR_LVL")
	private BigDecimal addrLvl;
	
	@Column(name="UP_ADDR_NO")
	private String upAddrNo;
	
	@Column(name="NAMESPACE")
	private String namespace;
	//机构名称
	@Column(name="ORG_NM")
	private String orgNm;
	//人行机构编码
	@Column(name="DTRCT_NO")
	private String dtrctNo;

	public String getAddrNo() {
		return addrNo;
	}

	public void setAddrNo(String addrNo) {
		this.addrNo = addrNo;
	}

	public String getAddrNm() {
		return addrNm;
	}

	public void setAddrNm(String addrNm) {
		this.addrNm = addrNm;
	}

	public BigDecimal getAddrLvl() {
		return addrLvl;
	}

	public void setAddrLvl(BigDecimal addrLvl) {
		this.addrLvl = addrLvl;
	}

	public String getUpAddrNo() {
		return upAddrNo;
	}

	public void setUpAddrNo(String upAddrNo) {
		this.upAddrNo = upAddrNo;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getOrgNo() {
		return orgNo;
	}

	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}

	public String getOrgNm() {
		return orgNm;
	}

	public void setOrgNm(String orgNm) {
		this.orgNm = orgNm;
	}

	public String getDtrctNo() {
		return dtrctNo;
	}

	public void setDtrctNo(String dtrctNo) {
		this.dtrctNo = dtrctNo;
	}

	
}
