package com.yusys.bione.plugin.rptidx.web.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.rptidx.entity.RptIdxBusiExt;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfoPK;

/**
 * @author rdpc0628
 * 
 */
public class RptIdxInfoVO {
	private RptIdxInfoPK id;
	private String indexNo;

	private String indexVerId;

	private String calcCycle;

	private BigDecimal dataLen;

	private BigDecimal dataPrecision;

	private String dataType;

	private String dataUnit;

	private String valRange;

	private String endDate;

	private String indexCatalogNo;

	private String indexNm;

	private String indexSts;

	private String indexType;

	private Timestamp lastUptTime;

	private String lastUptUser;

	private String remark;

	private String rptId;

	private String srcIndexNo;
	
	private String srcIndexMeasure;

	private String srcSourceId;

	private String startDate;

	private String isSum;

	private String isRptIndex;

	private String sumAccMeaNo;// 组合指标的来源指标为总账指标时的相关维度

	private String indexUsualNm;

	private String indexUsualNmTemp1;

	private String indexUsualNmTemp2;

	private String busiDef;

	private String busiRule;

	private String useDept;

	private String defDept;

	private String formulaDesc;

	private String formulaContent;

	private String dimTypes;

	private String storeCol;

	private String setId;

	private String saveType;

	private String dataJsonStr;

	private String dataJsonFilterModeStr;
	
	private String dataJsonDimStr;

	private String infoRights;

	private String isNewVer = "N";

	private String isPublish;

	private String isSave;
	
	private String busiModel;

	private String dimnos;// 补录指标所选择的维度集合

	private String defSrc;
	private String defOrg;
	private String defUser;

	private String statType;
	
	private String userId;
	
	private String userNm;
	
	private String tel;
	
	private String mail;
	
	private String deptId;
	
	private String deptNm;
	
	private String isCabin;
	
	private String lineId;
	
	private String busiLibId;
	
	public RptIdxInfoVO() {
		
	}
	
	public RptIdxInfoVO(RptIdxInfo idx,RptIdxBusiExt ext) {
		BeanUtils.copy(ext, this);
		BeanUtils.copy(idx, this);
	}
	
	public RptIdxInfo getIdxInfo() {
		RptIdxInfo info = new RptIdxInfo();
		BeanUtils.copy(this, info);
		return info;
	}

	public String getDimnos() {
		return dimnos;
	}

	public void setDimnos(String dimnos) {
		this.dimnos = dimnos;
	}

	public String getBusiModel() {
		return busiModel;
	}

	public void setBusiModel(String busiModel) {
		this.busiModel = busiModel;
	}

	public RptIdxInfoPK getId() {
		return id;
	}

	public void setId(RptIdxInfoPK id) {
		this.id = id;
	}

	public String getDataJsonStr() {
		return dataJsonStr;
	}

	public void setDataJsonStr(String dataJsonStr) {
		this.dataJsonStr = dataJsonStr;
	}

	public String getSaveType() {
		return saveType;
	}

	public void setSaveType(String saveType) {
		this.saveType = saveType;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public String getIndexVerId() {
		return indexVerId;
	}

	public void setIndexVerId(String indexVerId) {
		this.indexVerId = indexVerId;
	}

	public String getCalcCycle() {
		return calcCycle;
	}

	public void setCalcCycle(String calcCycle) {
		this.calcCycle = calcCycle;
	}

	public BigDecimal getDataLen() {
		return dataLen;
	}

	public void setDataLen(BigDecimal dataLen) {
		this.dataLen = dataLen;
	}

	public BigDecimal getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(BigDecimal dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataUnit() {
		return dataUnit;
	}

	public void setDataUnit(String dataUnit) {
		this.dataUnit = dataUnit;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getIndexCatalogNo() {
		return indexCatalogNo;
	}

	public void setIndexCatalogNo(String indexCatalogNo) {
		this.indexCatalogNo = indexCatalogNo;
	}

	public String getIndexNm() {
		return indexNm;
	}

	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}

	public String getIndexSts() {
		return indexSts;
	}

	public void setIndexSts(String indexSts) {
		this.indexSts = indexSts;
	}

	public String getIndexType() {
		return indexType;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public Timestamp getLastUptTime() {
		return lastUptTime;
	}

	public void setLastUptTime(Timestamp lastUptTime) {
		this.lastUptTime = lastUptTime;
	}

	public String getLastUptUser() {
		return lastUptUser;
	}

	public void setLastUptUser(String lastUptUser) {
		this.lastUptUser = lastUptUser;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRptId() {
		return rptId;
	}

	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	public String getSrcIndexNo() {
		return srcIndexNo;
	}

	public void setSrcIndexNo(String srcIndexNo) {
		this.srcIndexNo = srcIndexNo;
	}

	public String getSrcSourceId() {
		return srcSourceId;
	}

	public void setSrcSourceId(String srcSourceId) {
		this.srcSourceId = srcSourceId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getDefDept() {
		return defDept;
	}

	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}

	public String getIndexUsualNm() {
		return indexUsualNm;
	}

	public void setIndexUsualNm(String indexUsualNm) {
		this.indexUsualNm = indexUsualNm;
	}

	public String getUseDept() {
		return useDept;
	}

	public void setUseDept(String useDept) {
		this.useDept = useDept;
	}

	public String getIndexUsualNmTemp1() {
		return indexUsualNmTemp1;
	}

	public void setIndexUsualNmTemp1(String indexUsualNmTemp1) {
		this.indexUsualNmTemp1 = indexUsualNmTemp1;
	}

	public String getIndexUsualNmTemp2() {
		return indexUsualNmTemp2;
	}

	public void setIndexUsualNmTemp2(String indexUsualNmTemp2) {
		this.indexUsualNmTemp2 = indexUsualNmTemp2;
	}

	public String getIsSum() {
		return isSum;
	}

	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}

	public String getFormulaDesc() {
		return formulaDesc;
	}

	public void setFormulaDesc(String formulaDesc) {
		this.formulaDesc = formulaDesc;
	}

	public String getFormulaContent() {
		return formulaContent;
	}

	public void setFormulaContent(String formulaContent) {
		this.formulaContent = formulaContent;
	}

	public String getDimTypes() {
		return dimTypes;
	}

	public void setDimTypes(String dimTypes) {
		this.dimTypes = dimTypes;
	}

	public String getStoreCol() {
		return storeCol;
	}

	public void setStoreCol(String storeCol) {
		this.storeCol = storeCol;
	}

	public String getSetId() {
		return setId;
	}

	public void setSetId(String setId) {
		this.setId = setId;
	}

	public String getIsRptIndex() {
		return isRptIndex;
	}

	public void setIsRptIndex(String isRptIndex) {
		this.isRptIndex = isRptIndex;
	}

	public String getSumAccMeaNo() {
		return sumAccMeaNo;
	}

	public void setSumAccMeaNo(String sumAccMeaNo) {
		this.sumAccMeaNo = sumAccMeaNo;
	}

	public String getBusiDef() {
		return busiDef;
	}

	public void setBusiDef(String busiDef) {
		this.busiDef = busiDef;
	}

	public String getBusiRule() {
		return busiRule;
	}

	public void setBusiRule(String busiRule) {
		this.busiRule = busiRule;
	}

	public String getIsNewVer() {
		return isNewVer;
	}

	public void setIsNewVer(String isNewVer) {
		this.isNewVer = isNewVer;
	}

	public String getInfoRights() {
		return infoRights;
	}

	public void setInfoRights(String infoRights) {
		this.infoRights = infoRights;
	}

	public String getValRange() {
		return valRange;
	}

	public void setValRange(String valRange) {
		this.valRange = valRange;
	}

	public String getDataJsonFilterModeStr() {
		return dataJsonFilterModeStr;
	}
	
	public void setDataJsonFilterModeStr(String dataJsonFilterModeStr) {
		this.dataJsonFilterModeStr = dataJsonFilterModeStr;
	}
	
	public String getDataJsonDimStr() {
		return dataJsonDimStr;
	}

	public void setDataJsonDimStr(String dataJsonDimStr) {
		this.dataJsonDimStr = dataJsonDimStr;
	}

	public String getIsPublish() {
		return isPublish;
	}

	public void setIsPublish(String isPublish) {
		this.isPublish = isPublish;
	}

	public String getDefSrc() {
		return defSrc;
	}

	public void setDefSrc(String defSrc) {
		this.defSrc = defSrc;
	}

	public String getDefOrg() {
		return defOrg;
	}

	public void setDefOrg(String defOrg) {
		this.defOrg = defOrg;
	}

	public String getDefUser() {
		return defUser;
	}

	public void setDefUser(String defUser) {
		this.defUser = defUser;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}


	public String getIsCabin() {
		return isCabin;
	}

	public void setIsCabin(String isCabin) {
		this.isCabin = isCabin;
	}


	public String getSrcIndexMeasure() {
		return srcIndexMeasure;
	}

	public void setSrcIndexMeasure(String srcIndexMeasure) {
		this.srcIndexMeasure = srcIndexMeasure;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserNm() {
		return userNm;
	}

	public void setUserNm(String userNm) {
		this.userNm = userNm;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptNm() {
		return deptNm;
	}

	public void setDeptNm(String deptNm) {
		this.deptNm = deptNm;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getBusiLibId() {
		return busiLibId;
	}

	public void setBusiLibId(String busiLibId) {
		this.busiLibId = busiLibId;
	}
}