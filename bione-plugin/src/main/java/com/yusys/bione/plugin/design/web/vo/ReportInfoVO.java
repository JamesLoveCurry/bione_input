/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * <pre>
 * Title:监管报表VO
 * Description: 监管报表VO
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@ExcelSheet(index="0",name="报表信息",firstCol=2,titleFlag = true,insertType ="01",extType = "02")
public class ReportInfoVO implements Serializable {

	private static final long serialVersionUID = -3366922933210021472L;

	// 基本信息
	private String rptType = GlobalConstants4plugin.RPT_TYPE_DESIGN; // 报表类型-设计器报表
	private String rptId; // 报表ID
	@ExcelColumn(index = "2", name = "报表名称",colTitle = "基础信息")
	private String rptNm; // 报表名称
	private String catalogId; // 目录ID
	@ExcelColumn(index = "3", name = "报表目录",colTitle = "基础信息")
	private String catalogNm; // 目录名称
	@ExcelColumn(index = "7", name = "报表状态",colTitle = "业务信息",value={"Y","N","1","0"},text={"启用","停用","启用","停用",})
	private String rptSts; // 报表状态
	@ExcelColumn(index = "15", name = "报表描述",colTitle = "业务信息")
	private String rptDesc; // 描述
	private String showPriority; // 展示优先级
	private Timestamp createTime; // 创建时间
	private String createTimeStr; // 创建时间字符串
	private String beginTime; // 启用日期
	private String endTime; // 停用日期
	@ExcelColumn(index = "0", name = "报表编号",colTitle = "基础信息")
	private String rptNum; // 报表编号
	@ExcelColumn(index = "6", name = "报表周期",colTitle = "基础信息",value={"01","02","03","12","04"},text={"日","月","季","半年","年"})
	private String rptCycle; // 报表周期
	private String defSrc; // 定义来源
	private String defOrg; // 定义机构
	private String defUser; // 定义用户

	// 复制相关属性
	private String copyRptId; // 待拷贝报表ID

	// 监管业务信息
	private String extType = GlobalConstants4plugin.RPT_EXT_TYPE_BANK; // 业务扩展类型-统一报表业务
	@ExcelColumn(index = "8", name = "业务类型",colTitle = "业务信息",relDs={"com.yusys.bione.frame.variable.entity.BioneParamInfo","paramValue","paramName"," PARAM_TYPE_NO ='idxTaskType'"})
	private String busiType; // 业务类型（人行/1104）
	private String defDept; // 定义部门
	private String deptName; // 定义部门
	@ExcelColumn(index = "12", name = "是否下发提交",colTitle = "业务信息",value={"","Y","N","1","0"},text={"","是","否","是","否"})
	private String isReleaseSubmit; // 是否下发提交
	@ExcelColumn(index = "9", name = "上报编码",colTitle = "业务信息")
	private String reportCode; // 上报编码
	@ExcelColumn(index = "10", name = "报送主体",colTitle = "业务信息")
	private String submitMain; // 报送主体
	@ExcelColumn(index = "11", name = "报送范围",colTitle = "业务信息")
	private String submitRange; // 报送范围
	
	// 设计器信息
	private String templateId; // 模板ID
	@ExcelColumn(index = "1", name = "版本号",colTitle = "基础信息")
	private BigDecimal verId; // 版本号
	private BigDecimal maxVerId; // 最新版本号
	//@ExcelColumn(index = "6", name = "启用日期",colTitle = "基础信息")报表导入要根据版本号来控制开始结束日期，现在不需要展示这个字段
	private String verStartDate; // 版本开始时间
	private String verEndDate; // 版本结束时间
	@ExcelColumn(index = "4", name = "模板类型",colTitle = "基础信息",value={"01","02","03","04","05","06"},text={"明细报表","单元格报表","综合报表","纵向报表列表","横向报表列表","交叉列表"})
	private String templateType; // 模板类型
	@ExcelColumn(index = "13", name = "是否数据修改",colTitle = "业务信息",value={"","Y","N","1","0"},text={"","是","否","是","否"})
	private String isUpt; // 是否修改
	@ExcelColumn(index = "5", name = "报表单位",colTitle = "基础信息",value={"01","02","03","04","05"},text={"个","百","千","万","亿"})
	private String templateUnit; // 模板单位
	@ExcelColumn(index = "16", name = "升级概况",colTitle = "业务信息",value={"01","02","03","04"},text={"新增","修改","停用","无变化"})
	private String templateNm; // 模板名称
	
	// 查询维度信息
	private String paramId; // 参数模板ID
	private String publicDim; // 报表维度
	private String queryDim; // 查询维度
	private String paramJson; // 参数模板对应JSON
	private String isAutoAdj;//自动调整
	private String userId;//用户ID
	private String userName;//联系人名称
	private String tel;//联系人电话
	private String email;//联系人邮件
	private String isCabin; // 是否管架使用
	private BigDecimal rankOrder;//排列顺序
	@ExcelColumn(index = "14", name = "业务条线",colTitle = "业务信息",relDs={"com.yusys.bione.plugin.businessline.entity.RptMgrBusiLine","lineId","lineNm"})
	private String lineId;//条线ID
	private String rptlineId;//条线ID
	//lcy 业务分库
	@ExcelColumn(index = "17", name = "业务分库",colTitle = "业务信息",relDs={"com.yusys.bione.plugin.businesslib.entity.RptMgrBusiLibInfo","busiLibId","busiLibNm"})
	private String busiLibId;
	private String busiLibNm;
	
	@ExcelColumn(index = "18", name = "制度表样版本",colTitle = "业务信息")
	private String tmpVersionId;
	
	@ExcelColumn(index = "20", name = "制度表样编号",colTitle = "业务信息")
	private String repId;

	@ExcelColumn(index = "19", name = "报表业务名称",colTitle = "业务信息")
	private String rptBusiNm;
	
	//@ExcelColumn(index = "20", name = "是否定长",colTitle = "业务信息", value={"Y","N"},text={"是","否"})
	private String fixedLength;
	//@ExcelColumn(index = "21", name = "是否分页",colTitle = "业务信息", value={"Y","N"},text={"是","否"})
	private String isPaging;
	
	private String sortSql;

	private String importConfig;
	
	private String fillDesc;
	
	private String systemName; //制度版本名称
	
	private String excelRun; //是否跑excel

	private String reportObj; //报表报送类型
	
	public String getExcelRun() {
		return excelRun;
	}

	public void setExcelRun(String excelRun) {
		this.excelRun = excelRun;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getBusiLibNm() {
		return busiLibNm;
	}

	public void setBusiLibNm(String busiLibNm) {
		this.busiLibNm = busiLibNm;
	}

	public String getFillDesc() {
		return fillDesc;
	}

	public void setFillDesc(String fillDesc) {
		this.fillDesc = fillDesc;
	}

	public ReportInfoVO() {
		super();
	}

	public String getRepId() {
		return repId;
	}

	public void setRepId(String repId) {
		this.repId = repId;
	}

	public String getFixedLength() {
		return fixedLength;
	}

	public void setFixedLength(String fixedLength) {
		this.fixedLength = fixedLength;
	}

	public String getIsPaging() {
		return isPaging;
	}

	public void setIsPaging(String isPaging) {
		this.isPaging = isPaging;
	}
	
	public String getTmpVersionId() {
		return tmpVersionId;
	}

	public void setTmpVersionId(String tmpVersionId) {
		this.tmpVersionId = tmpVersionId;
	}

	/**
	 * @return 返回 rptType。
	 */
	public String getRptType() {
		return rptType;
	}

	/**
	 * @param rptType
	 *            设置 rptType。
	 */
	public void setRptType(String rptType) {
		this.rptType = rptType;
	}

	/**
	 * @return 返回 rptId。
	 */
	public String getRptId() {
		return rptId;
	}

	/**
	 * @param rptId
	 *            设置 rptId。
	 */
	public void setRptId(String rptId) {
		this.rptId = rptId;
	}

	/**
	 * @return 返回 rptNm。
	 */
	public String getRptNm() {
		return rptNm;
	}

	/**
	 * @param rptNm
	 *            设置 rptNm。
	 */
	public void setRptNm(String rptNm) {
		this.rptNm = rptNm;
	}

	/**
	 * @return 返回 catalogId。
	 */
	public String getCatalogId() {
		return catalogId;
	}

	/**
	 * @param catalogId
	 *            设置 catalogId。
	 */
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}

	/**
	 * @return 返回 catalogNm。
	 */
	public String getCatalogNm() {
		return catalogNm;
	}

	/**
	 * @param catalogNm
	 *            设置 catalogNm。
	 */
	public void setCatalogNm(String catalogNm) {
		this.catalogNm = catalogNm;
	}

	/**
	 * @return 返回 rptSts。
	 */
	public String getRptSts() {
		return rptSts;
	}

	/**
	 * @param rptSts
	 *            设置 rptSts。
	 */
	public void setRptSts(String rptSts) {
		this.rptSts = rptSts;
	}



	public BigDecimal getRankOrder() {
		return rankOrder;
	}

	public void setRankOrder(BigDecimal rankOrder) {
		this.rankOrder = rankOrder;
	}

	/**
	 * @return 返回 rptDesc。
	 */
	public String getRptDesc() {
		return rptDesc;
	}

	/**
	 * @param rptDesc
	 *            设置 rptDesc。
	 */
	public void setRptDesc(String rptDesc) {
		this.rptDesc = rptDesc;
	}

	/**
	 * @return 返回 showPriority。
	 */
	public String getShowPriority() {
		return showPriority;
	}

	/**
	 * @param showPriority
	 *            设置 showPriority。
	 */
	public void setShowPriority(String showPriority) {
		this.showPriority = showPriority;
	}

	/**
	 * @return 返回 createTime。
	 */
	public Timestamp getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            设置 createTime。
	 */
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return 返回 beginTime。
	 */
	public String getBeginTime() {
		return beginTime;
	}

	/**
	 * @param beginTime
	 *            设置 beginTime。
	 */
	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	/**
	 * @return 返回 endTime。
	 */
	public String getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime
	 *            设置 endTime。
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return 返回 extType。
	 */
	public String getExtType() {
		return extType;
	}

	/**
	 * @param extType
	 *            设置 extType。
	 */
	public void setExtType(String extType) {
		this.extType = extType;
	}

	/**
	 * @return 返回 rptNum。
	 */
	public String getRptNum() {
		return rptNum;
	}

	/**
	 * @param rptNum
	 *            设置 rptNum。
	 */
	public void setRptNum(String rptNum) {
		this.rptNum = rptNum;
	}

	/**
	 * @return 返回 busiType。
	 */
	public String getBusiType() {
		return busiType;
	}

	/**
	 * @param busiType
	 *            设置 busiType。
	 */
	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	/**
	 * @return 返回 defDept。
	 */
	public String getDefDept() {
		return defDept;
	}

	/**
	 * @param defDept
	 *            设置 defDept。
	 */
	public void setDefDept(String defDept) {
		this.defDept = defDept;
	}

	/**
	 * @return 返回 isReleaseSubmit。
	 */
	public String getIsReleaseSubmit() {
		return isReleaseSubmit;
	}

	/**
	 * @param isReleaseSubmit
	 *            设置 isReleaseSubmit。
	 */
	public void setIsReleaseSubmit(String isReleaseSubmit) {
		this.isReleaseSubmit = isReleaseSubmit;
	}

	/**
	 * @return 返回 reportCode。
	 */
	public String getReportCode() {
		return reportCode;
	}

	/**
	 * @param reportCode
	 *            设置 reportCode。
	 */
	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	/**
	 * @return 返回 submitMain。
	 */
	public String getSubmitMain() {
		return submitMain;
	}

	/**
	 * @param submitMain
	 *            设置 submitMain。
	 */
	public void setSubmitMain(String submitMain) {
		this.submitMain = submitMain;
	}

	/**
	 * @return 返回 submitRange。
	 */
	public String getSubmitRange() {
		return submitRange;
	}

	/**
	 * @param submitRange
	 *            设置 submitRange。
	 */
	public void setSubmitRange(String submitRange) {
		this.submitRange = submitRange;
	}

	/**
	 * @return 返回 templateId。
	 */
	public String getTemplateId() {
		return templateId;
	}

	/**
	 * @param templateId
	 *            设置 templateId。
	 */
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}

	/**
	 * @return 返回 templateType。
	 */
	public String getTemplateType() {
		return templateType;
	}

	/**
	 * @param templateType
	 *            设置 templateType。
	 */
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}

	/**
	 * @return 返回 isUpt。
	 */
	public String getIsUpt() {
		return isUpt;
	}

	/**
	 * @param isUpt
	 *            设置 isUpt。
	 */
	public void setIsUpt(String isUpt) {
		this.isUpt = isUpt;
	}

	/**
	 * @return 返回 verId。
	 */
	public BigDecimal getVerId() {
		return verId;
	}

	/**
	 * @param verId
	 *            设置 verId。
	 */
	public void setVerId(BigDecimal verId) {
		this.verId = verId;
	}

	/**
	 * @return 返回 rptCycle。
	 */
	public String getRptCycle() {
		return rptCycle;
	}

	/**
	 * @param rptCycle
	 *            设置 rptCycle。
	 */
	public void setRptCycle(String rptCycle) {
		this.rptCycle = rptCycle;
	}

	/**
	 * @return 返回 verStartDate。
	 */
	public String getVerStartDate() {
		return verStartDate;
	}

	/**
	 * @param verStartDate
	 *            设置 verStartDate。
	 */
	public void setVerStartDate(String verStartDate) {
		this.verStartDate = verStartDate;
	}

	/**
	 * @return 返回 verEndDate。
	 */
	public String getVerEndDate() {
		return verEndDate;
	}

	/**
	 * @param verEndDate
	 *            设置 verEndDate。
	 */
	public void setVerEndDate(String verEndDate) {
		this.verEndDate = verEndDate;
	}

	/**
	 * @return 返回 templateUnit。
	 */
	public String getTemplateUnit() {
		return templateUnit;
	}

	/**
	 * @param templateUnit
	 *            设置 templateUnit。
	 */
	public void setTemplateUnit(String templateUnit) {
		this.templateUnit = templateUnit;
	}

	/**
	 * @return 返回 createTimeStr。
	 */
	public String getCreateTimeStr() {
		return createTimeStr;
	}

	/**
	 * @param createTimeStr
	 *            设置 createTimeStr。
	 */
	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public BigDecimal getMaxVerId() {
		return maxVerId;
	}

	public void setMaxVerId(BigDecimal maxVerId) {
		this.maxVerId = maxVerId;
	}

	/**
	 * @return 返回 defSrc。
	 */
	public String getDefSrc() {
		return defSrc;
	}

	/**
	 * @param defSrc
	 *            设置 defSrc。
	 */
	public void setDefSrc(String defSrc) {
		this.defSrc = defSrc;
	}

	/**
	 * @return 返回 defOrg。
	 */
	public String getDefOrg() {
		return defOrg;
	}

	/**
	 * @param defOrg
	 *            设置 defOrg。
	 */
	public void setDefOrg(String defOrg) {
		this.defOrg = defOrg;
	}

	/**
	 * @return 返回 defUser。
	 */
	public String getDefUser() {
		return defUser;
	}

	/**
	 * @param defUser
	 *            设置 defUser。
	 */
	public void setDefUser(String defUser) {
		this.defUser = defUser;
	}

	/**
	 * @return 返回 publicDim。
	 */
	public String getPublicDim() {
		return publicDim;
	}

	/**
	 * @param publicDim 设置 publicDim。
	 */
	public void setPublicDim(String publicDim) {
		this.publicDim = publicDim;
	}

	/**
	 * @return 返回 queryDim。
	 */
	public String getQueryDim() {
		return queryDim;
	}

	/**
	 * @param queryDim 设置 queryDim。
	 */
	public void setQueryDim(String queryDim) {
		this.queryDim = queryDim;
	}

	/**
	 * @return 返回 paramJson。
	 */
	public String getParamJson() {
		return paramJson;
	}

	/**
	 * @param paramJson 设置 paramJson。
	 */
	public void setParamJson(String paramJson) {
		this.paramJson = paramJson;
	}

	/**
	 * @return 返回 paramId。
	 */
	public String getParamId() {
		return paramId;
	}

	/**
	 * @param paramId 设置 paramId。
	 */
	public void setParamId(String paramId) {
		this.paramId = paramId;
	}

	/**
	 * @return 返回 copyRptId。
	 */
	public String getCopyRptId() {
		return copyRptId;
	}

	/**
	 * @param copyRptId 设置 copyRptId。
	 */
	public void setCopyRptId(String copyRptId) {
		this.copyRptId = copyRptId;
	}

	public String getIsAutoAdj() {
		return isAutoAdj;
	}

	public void setIsAutoAdj(String isAutoAdj) {
		this.isAutoAdj = isAutoAdj;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getIsCabin() {
		return isCabin;
	}

	public void setIsCabin(String isCabin) {
		this.isCabin = isCabin;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getLineId() {
		return lineId;
	}

	public void setLineId(String lineId) {
		this.lineId = lineId;
	}

	public String getRptlineId() {
		return rptlineId;
	}

	public void setRptlineId(String rptlineId) {
		this.rptlineId = rptlineId;
	}

	public String getTemplateNm() {
		return templateNm;
	}

	public void setTemplateNm(String templateNm) {
		this.templateNm = templateNm;
	}

	public String getBusiLibId() {
		return busiLibId;
	}

	public void setBusiLibId(String busiLibId) {
		this.busiLibId = busiLibId;
	}
	
	public String getRptBusiNm() {
		return rptBusiNm;
	}

	public void setRptBusiNm(String rptBusiNm) {
		this.rptBusiNm = rptBusiNm;
	}

	public String getSortSql() {
		return sortSql;
	}

	public void setSortSql(String sortSql) {
		this.sortSql = sortSql;
	}

	public String getImportConfig() {
		return importConfig;
	}

	public void setImportConfig(String importConfig) {
		this.importConfig = importConfig;
	}

	public String getReportObj() {
		return reportObj;
	}

	public void setReportObj(String reportObj) {
		this.reportObj = reportObj;
	}
}
