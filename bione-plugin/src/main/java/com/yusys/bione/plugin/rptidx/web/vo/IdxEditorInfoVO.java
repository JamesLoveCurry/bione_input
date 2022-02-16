package com.yusys.bione.plugin.rptidx.web.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.plugin.rptidx.entity.RptIdxBusiExt;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
/**
 * Created by kanglg on 2015-9-2.
 * <pre>
 * Title:
 * Description: 
 * </pre>
 * 
 * @author kanglg@yuchengtech.com
 * @version 1.00.00
 * 
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class IdxEditorInfoVO {

	private String indexCatalogNm;

	private String indexNo;

	private long indexVerId;

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

	private String srcIndexNo;

	private String srcSourceId;

	private String startDate;

	private String isSum;
	

	private String isRptIndex;

	private String srcIndexMeasure;

	private String busiType;

	private String templateId;

	private String infoRights;

	private String isPublish;

	private String defSrc;

	private String defOrg;

	private String defUser;

	private String busiNo;

	private String isSave;

	private String statType;

	private String indexUsualNm;

	private String busiDef;

	private String busiRule;

	private String defDept;

	private String useDept;

	private String busiModel;
	
	private String userId;
	
	private String userNm;
	
	private String mail;
	
	private String deptId;
	
	private String deptNm;
	
	private String lineId;
	
	//--lcy 业务分库 分表
	private String busiLibId;
	
	public IdxEditorInfoVO() {
		
	}

	public IdxEditorInfoVO(RptIdxInfo idx, RptIdxCatalog cata, RptIdxBusiExt ext) {
		BeanUtils.copy(ext, this);
		BeanUtils.copy(idx, this);
		this.indexNo = idx.getId().getIndexNo();
		this.indexVerId = idx.getId().getIndexVerId();
		this.indexCatalogNm = cata.getIndexCatalogNm();
	}

	public String getIndexCatalogNm() {
		return indexCatalogNm;
	}

	public void setIndexCatalogNm(String indexCatalogNm) {
		this.indexCatalogNm = indexCatalogNm;
	}

	public String getIndexNo() {
		return indexNo;
	}

	public void setIndexNo(String indexNo) {
		this.indexNo = indexNo;
	}

	public long getIndexVerId() {
		return indexVerId;
	}

	public void setIndexVerId(long indexVerId) {
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

	public String getValRange() {
		return valRange;
	}

	public void setValRange(String valRange) {
		this.valRange = valRange;
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

	public String getIsSum() {
		return isSum;
	}

	public void setIsSum(String isSum) {
		this.isSum = isSum;
	}


	public String getIsRptIndex() {
		return isRptIndex;
	}

	public void setIsRptIndex(String isRptIndex) {
		this.isRptIndex = isRptIndex;
	}

	public String getSrcIndexMeasure() {
		return srcIndexMeasure;
	}

	public void setSrcIndexMeasure(String srcIndexMeasure) {
		this.srcIndexMeasure = srcIndexMeasure;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getTemplateId() {
		return templateId;
	}

	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	public String getInfoRights() {
		return infoRights;
	}

	public void setInfoRights(String infoRights) {
		this.infoRights = infoRights;
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

	public String getBusiNo() {
		return busiNo;
	}

	public void setBusiNo(String busiNo) {
		this.busiNo = busiNo;
	}

	public String getIsSave() {
		return isSave;
	}

	public void setIsSave(String isSave) {
		this.isSave = isSave;
	}

	public String getStatType() {
		return statType;
	}

	public void setStatType(String statType) {
		this.statType = statType;
	}

	public String getIndexUsualNm() {
		return indexUsualNm;
	}

	public void setIndexUsualNm(String indexUsualNm) {
		this.indexUsualNm = indexUsualNm;
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

	public String getDefDept() {
		return defDept;
	}

	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}

	public String getUseDept() {
		return useDept;
	}

	public void setUseDept(String useDept) {
		this.useDept = useDept;
	}

	public String getBusiModel() {
		return busiModel;
	}

	public void setBusiModel(String busiModel) {
		this.busiModel = busiModel;
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
