package com.yusys.bione.plugin.rptmgr.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the RPT_MGR_FRS_EXT database table.
 * 
 */
@Entity
@Table(name="RPT_MGR_FRS_EXT")
public class RptMgrFrsExt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="RPT_ID")
	private String rptId;

	@Column(name="DEF_DEPT")
	private String defDept;
	
	@Column(name="USER_ID")
	private String userId;

	@Column(name="IS_RELEASE_SUBMIT")
	private String isReleaseSubmit;

	private String remark;

	@Column(name="REPORT_CODE")
	private String reportCode;

	@Column(name="BUSI_TYPE")
	private String busiType;
	
	@Column(name="SUBMIT_MAIN")
	private String submitMain;
	
	@Column(name="SUBMIT_RANGE")
	private String submitRange;

	@Column(name="FILL_DESC")
	private String fillDesc;
	
	@Column(name="IS_MAIN_RPT")
	private String isMainRpt;

	@Column(name="MAIN_RPT_ID")
	private String mainRptId;
	
	@Column(name="KEY_WORD")
	private String keyword;
	
	@Column(name="DATA_PRO")
	private String datapro;
	
	@Column(name="PRELIMINARY_DES")
	private String preliminaryDes;
	
	@Column(name="AUTHOR_SYS")
	private String authorSys;
	
	@Column(name="SERVICE_CYC")
	private String serviceCyc;
	
	@Column(name="VER_NUM")
	private String verNum;
	
	@Column(name="DEPT_USER_ID")
	private String  deptUserID;
	
	@Column(name="DEPT_USER")
	private String deptUser;
	
	@Column(name="DESC_INFO_PUB")
	private String descInfoPub;
	
	@Column(name="ROLE_INFO")
	private String roleInfo;
	
	@Column(name="FROM_SYS")
	private String fromSys;
	
	@Column(name="DATA_RESERVE")
	private String dataReserve;
	
	@Column(name="BUS_CAL")
	private String busCal;

	@Column(name="SRC_TABLE")
	private String srcTable;	// 源系统表名称
	
	@Column(name="IS_ADD_SRCFEILD")
	private String isAddSrcFeild;	// 是否增加条线
	
	@Column(name="SRC_FEILD")
	private String srcFeild;	// 源系统字段
	
	@Column(name="IS_ADD_ORG")
	private String isAddOrgFeild;  //是否按机构查询
	
	@Column(name="SEARCH_FEILD")
	private String searchFeild;  //数据查询日期
	
	@Column(name="IS_DATETYPE")
	private String isDateType;  //是否日期类型
	
	@Column(name="TMP_VERSION_ID")
	private String tmpVersionId;  //制度表样版本
	
	@Column(name="REP_ID")
	private String repId;  //制度表样编号
	
	public String getRepId() {
		return repId;
	}

	public void setRepId(String repId) {
		this.repId = repId;
	}

	public String getTmpVersionId() {
		return tmpVersionId;
	}

	public void setTmpVersionId(String tmpVersionId) {
		this.tmpVersionId = tmpVersionId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getDatapro() {
		return datapro;
	}

	public void setDatapro(String datapro) {
		this.datapro = datapro;
	}

	public String getPreliminaryDes() {
		return preliminaryDes;
	}

	public void setPreliminaryDes(String preliminaryDes) {
		this.preliminaryDes = preliminaryDes;
	}

	public String getRoleInfo() {
		return roleInfo;
	}

	public void setRoleInfo(String roleInfo) {
		this.roleInfo = roleInfo;
	}

	public String getAuthorSys() {
		return authorSys;
	}

	public void setAuthorSys(String authorSys) {
		this.authorSys = authorSys;
	}

	public String getServiceCyc() {
		return serviceCyc;
	}

	public void setServiceCyc(String serviceCyc) {
		this.serviceCyc = serviceCyc;
	}

	public String getVerNum() {
		return verNum;
	}

	public void setVerNum(String verNum) {
		this.verNum = verNum;
	}

	public String getDeptUserID() {
		return deptUserID;
	}

	public void setDeptUserID(String deptUserID) {
		this.deptUserID = deptUserID;
	}

	public String getDeptUser() {
		return deptUser;
	}

	public void setDeptUser(String deptUser) {
		this.deptUser = deptUser;
	}

	public String getDescInfoPub() {
		return descInfoPub;
	}

	public void setDescInfoPub(String descInfoPub) {
		this.descInfoPub = descInfoPub;
	}

	public String getFromSys() {
		return fromSys;
	}

	public void setFromSys(String fromSys) {
		this.fromSys = fromSys;
	}

	public String getDataReserve() {
		return dataReserve;
	}

	public void setDataReserve(String dataReserve) {
		this.dataReserve = dataReserve;
	}

	public String getBusCal() {
		return busCal;
	}

	public void setBusCal(String busCal) {
		this.busCal = busCal;
	}

	public RptMgrFrsExt() {
    }

	public String getRptId() {
		return this.rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getDefDept() {
		return this.defDept;
	}

	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}

	public String getIsReleaseSubmit() {
		return this.isReleaseSubmit;
	}

	public void setIsReleaseSubmit(String isReleaseSubmit) {
		this.isReleaseSubmit = isReleaseSubmit;
	}

	public String getRemark() {
		return this.remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getReportCode() {
		return this.reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public String getBusiType() {
		return this.busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getSubmitMain() {
		return submitMain;
	}

	public void setSubmitMain(String submitMain) {
		this.submitMain = submitMain;
	}

	public String getSubmitRange() {
		return submitRange;
	}

	public void setSubmitRange(String submitRange) {
		this.submitRange = submitRange;
	}

	public String getFillDesc() {
		return fillDesc;
	}

	public void setFillDesc(String fillDesc) {
		this.fillDesc = fillDesc;
	}

	public String getIsMainRpt() {
		return isMainRpt;
	}

	public void setIsMainRpt(String isMainRpt) {
		this.isMainRpt = isMainRpt;
	}
	

	public String getMainRptId() {
		return mainRptId;
	}

	public void setMainRptId(String mainRptId) {
		this.mainRptId = mainRptId;
	}

	public String getSrcTable() {
		return srcTable;
	}

	public void setSrcTable(String srcTable) {
		this.srcTable = srcTable;
	}

	public String getIsAddSrcFeild() {
		return isAddSrcFeild;
	}

	public void setIsAddSrcFeild(String isAddSrcFeild) {
		this.isAddSrcFeild = isAddSrcFeild;
	}

	public String getSrcFeild() {
		return srcFeild;
	}

	public void setSrcFeild(String srcFeild) {
		this.srcFeild = srcFeild;
	}

	public String getIsAddOrgFeild() {
		return isAddOrgFeild;
	}

	public void setIsAddOrgFeild(String isAddOrgFeild) {
		this.isAddOrgFeild = isAddOrgFeild;
	}

	public String getSearchFeild() {
		return searchFeild;
	}

	public void setSearchFeild(String searchFeild) {
		this.searchFeild = searchFeild;
	}

	public String getIsDateType() {
		return isDateType;
	}

	public void setIsDateType(String isDateType) {
		this.isDateType = isDateType;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}