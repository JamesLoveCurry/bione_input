/**
 * 
 */
package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述 
 * </pre>
 * @author caiqy  caiqy@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容: 
 * </pre>
 */
public class RptSysModuleColVO extends RptSysModuleCol{

	/**
	 * 
	 */
	private static final long serialVersionUID = -135987950756666045L;
	
	private String dimTypeStruct;
	
	private String busiType;

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	/**
	 * @return 返回 dimTypeStruct。
	 */
	public String getDimTypeStruct() {
		return dimTypeStruct;
	}

	/**
	 * @param dimTypeStruct 设置 dimTypeStruct。
	 */
	public void setDimTypeStruct(String dimTypeStruct) {
		this.dimTypeStruct = dimTypeStruct;
	}
	
	
	
}
