package com.yusys.bione.plugin.datashow.web.vo;



public class IdxBaseInfoVO{
	private String indexNo;// 指标号
	private long indexVerId;// 版本
	private String indexNm;// 名称
	private String indexType;// 种类类型
	private String startDate;// 启用日期
	private String endDate;// 终止日期
	private String calcCycle;//取值范围
	private String dataType;// 数据格式
	private String dataLen;// 数据长度
	private String indexSts;// 状态
	private String serviceRule;// 业务规则
	private String serviceDef;// 业务定义
	private String defDept;// 定义部门
	private String useDept;// 使用部门
	private String isSum;
	private String remark; // 备注
	private String dataPrecision;
	
	public String getDataPrecision() {
		return dataPrecision;
	}

	public void setDataPrecision(String dataPrecision) {
		this.dataPrecision = dataPrecision;
	}

	public String getIndexNo() {
		return indexNo;
	}
	
	public String getIsSum() {
		return isSum;
	}

	public void setIsSum(String isSum) {
		this.isSum = isSum;
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
	public String getIndexNm() {
		return indexNm;
	}
	public void setIndexNm(String indexNm) {
		this.indexNm = indexNm;
	}
	public String getIndexType() {
		return indexType;
	}
	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getCalcCycle() {
		return calcCycle;
	}
	public void setCalcCycle(String calcCycle) {
		this.calcCycle = calcCycle;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getDataLen() {
		return dataLen;
	}
	public void setDataLen(String dataLen) {
		this.dataLen = dataLen;
	}
	public String getIndexSts() {
		return indexSts;
	}
	public void setIndexSts(String indexSts) {
		this.indexSts = indexSts;
	}
	public String getServiceRule() {
		return serviceRule;
	}
	public void setServiceRule(String serviceRule) {
		this.serviceRule = serviceRule;
	}
	public String getServiceDef() {
		return serviceDef;
	}
	public void setServiceDef(String serviceDef) {
		this.serviceDef = serviceDef;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
