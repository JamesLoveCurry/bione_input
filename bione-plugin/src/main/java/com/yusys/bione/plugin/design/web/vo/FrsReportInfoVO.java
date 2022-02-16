/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.plugin.design.web.vo.ReportInfoVO;


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
public class FrsReportInfoVO extends ReportInfoVO {

	private static final long serialVersionUID = -3366922933210021472L;

	//增加附件说明
	private String addAttachs;
	private String delAttachs;

	private String fillDesc;   //填报说明
	private String isMainRpt;  //是否主表
	private String mainRptId;  //主表id
	private String mainRptNm;  //主表名称
	private String sysTemName; //版本名称
	
	public FrsReportInfoVO() {
		super();
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
	
	public String getMainRptNm() {
		return mainRptNm;
	}

	public void setMainRptNm(String mainRptNm) {
		this.mainRptNm = mainRptNm;
	}

	public String getAddAttachs() {
		return addAttachs;
	}

	public void setAddAttachs(String addAttachs) {
		this.addAttachs = addAttachs;
	}

	public String getDelAttachs() {
		return delAttachs;
	}

	public void setDelAttachs(String delAttachs) {
		this.delAttachs = delAttachs;
	}

	public String getSysTemName() {
		return sysTemName;
	}

	public void setSysTemName(String sysTemName) {
		this.sysTemName = sysTemName;
	}
}
