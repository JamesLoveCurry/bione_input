/**
 * 
 */
package com.yusys.bione.frame.mainpage.web.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <pre>
 * Title:首页构造用具体信息VO
 * Description: 首页构造用具体信息VO 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class MpDetailInfoVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private BigDecimal posNo;
	
	private String isDisplayLabel;
	
	private String moduleName;
	
	private String labelPath;
	
	private String modulePath;
	
	private String moduleType;
	
	private String moduleId;

	public BigDecimal getPosNo() {
		return posNo;
	}

	public void setPosNo(BigDecimal posNo) {
		this.posNo = posNo;
	}

	public String getIsDisplayLabel() {
		return isDisplayLabel;
	}

	public void setIsDisplayLabel(String isDisplayLabel) {
		this.isDisplayLabel = isDisplayLabel;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getLabelPath() {
		return labelPath;
	}

	public void setLabelPath(String labelPath) {
		this.labelPath = labelPath;
	}

	public String getModulePath() {
		return modulePath;
	}

	public void setModulePath(String modulePath) {
		this.modulePath = modulePath;
	}

	public String getModuleType() {
		return moduleType;
	}

	public void setModuleType(String moduleType) {
		this.moduleType = moduleType;
	}

	public String getModuleId() {
		return moduleId;
	}

	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}

	public MpDetailInfoVO() {
		super();
	}
	
	public MpDetailInfoVO(BigDecimal posNo, String isDisplayLabel,
			String moduleId, String moduleName, String labelPath, String modulePath,
			String moduleType) {
		super();
		this.posNo = posNo;
		this.isDisplayLabel = isDisplayLabel;
		this.moduleName = moduleName;
		this.labelPath = labelPath;
		this.modulePath = modulePath;
		this.moduleType = moduleType;
		this.moduleId = moduleId;
	}
	
}
