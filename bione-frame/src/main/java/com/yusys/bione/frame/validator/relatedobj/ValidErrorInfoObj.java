/**
 * 
 */
package com.yusys.bione.frame.validator.relatedobj;

import java.io.Serializable;

/**
 * <pre>
 * Title:校验未通过时，返回的信息对象
 * Description: 包含，校验类型、校验项、对象、字段、信息等
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
public class ValidErrorInfoObj implements Serializable {
	private static final long serialVersionUID = 9003642461763148115L;

	private String sheetName; // 数据对应的sheet名称
	private Integer excelRowNo; // 数据对应的excel行
	private Integer excelColNo; // 数据对应的excel列
	private String validType; // 校验类型
	
	private String validTypeNm; // 校验类型名称
	
	private String validInfo; // 校验内容
	
	private String errorMsg; // 错误信息
	
	private Object annoObj;// 校验未通过的对象信息
	private String fieldName; // 校验未通过的字段名，target为field类型校验失败时此处非空
	
	private String errorLocation; // 错误定位

	/**
	 * 
	 */
	public ValidErrorInfoObj() {
		super();
	}

	/**
	 * @param validType
	 * @param validInfo
	 * @param errorMsg
	 * @param annoObj
	 * @param fieldName
	 */
	public ValidErrorInfoObj(String validType, String validTypeNm,
			String validInfo, String errorMsg, Object annoObj, String fieldName) {
		super();
		this.validType = validType;
		this.validTypeNm = validTypeNm;
		this.validInfo = validInfo;
		this.errorMsg = errorMsg;
		this.fieldName = fieldName;
		this.annoObj = annoObj;
	}

	/**
	 * 
	 * @param excelRowNo
	 * @param excelColNo
	 * @param validType
	 * @param validTypeNm
	 * @param validInfo
	 * @param errorMsg
	 * @param annoObj
	 * @param fieldName
	 */
	public ValidErrorInfoObj(String sheetName, Integer excelRowNo,
			Integer excelColNo, String validType, String validTypeNm,
			String validInfo, String errorMsg, Object annoObj, String fieldName) {
		super();
		this.validType = validType;
		this.validTypeNm = validTypeNm;
		this.validInfo = validInfo;
		this.errorMsg = errorMsg;
		this.fieldName = fieldName;
		this.annoObj = annoObj;
		this.sheetName = sheetName;
		this.excelColNo = excelColNo;
		this.excelRowNo = excelRowNo;
	}

	/**
	 * 
	 * @param errorLocation
	 * @param validType
	 * @param validTypeNm
	 * @param validInfo
	 * @param errorMsg
	 * @param annoObj
	 * @param fieldName
	 */
	
	public ValidErrorInfoObj(String errorLocation, String validType,
			String validTypeNm, String validInfo, String errorMsg,
			Object annoObj, String fieldName) {
		super();
		this.validType = validType;
		this.validTypeNm = validTypeNm;
		this.validInfo = validInfo;
		this.errorMsg = errorMsg;
		this.fieldName = fieldName;
		this.annoObj = annoObj;
		this.errorLocation = errorLocation;
	}

	public String getValidType() {
		return validType;
	}

	public void setValidType(String validType) {
		this.validType = validType;
	}

	public String getValidInfo() {
		return validInfo;
	}

	public void setValidInfo(String validInfo) {
		this.validInfo = validInfo;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValidTypeNm() {
		return validTypeNm;
	}

	public void setValidTypeNm(String validTypeNm) {
		this.validTypeNm = validTypeNm;
	}

	public Object getAnnoObj() {
		return annoObj;
	}

	public void setAnnoObj(Object annoObj) {
		this.annoObj = annoObj;
	}

	public Integer getExcelRowNo() {
		return excelRowNo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annoObj == null) ? 0 : annoObj.hashCode());
		result = prime * result
				+ ((errorLocation == null) ? 0 : errorLocation.hashCode());
		result = prime * result
				+ ((errorMsg == null) ? 0 : errorMsg.hashCode());
		result = prime * result
				+ ((excelColNo == null) ? 0 : excelColNo.hashCode());
		result = prime * result
				+ ((excelRowNo == null) ? 0 : excelRowNo.hashCode());
		result = prime * result
				+ ((fieldName == null) ? 0 : fieldName.hashCode());
		result = prime * result
				+ ((sheetName == null) ? 0 : sheetName.hashCode());
		result = prime * result
				+ ((validInfo == null) ? 0 : validInfo.hashCode());
		result = prime * result
				+ ((validType == null) ? 0 : validType.hashCode());
		result = prime * result
				+ ((validTypeNm == null) ? 0 : validTypeNm.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ValidErrorInfoObj other = (ValidErrorInfoObj) obj;
		if (annoObj == null) {
			if (other.annoObj != null)
				return false;
		} else if (!annoObj.equals(other.annoObj))
			return false;
		if (errorLocation == null) {
			if (other.errorLocation != null)
				return false;
		} else if (!errorLocation.equals(other.errorLocation))
			return false;
		if (errorMsg == null) {
			if (other.errorMsg != null)
				return false;
		} else if (!errorMsg.equals(other.errorMsg))
			return false;
		if (excelColNo == null) {
			if (other.excelColNo != null)
				return false;
		} else if (!excelColNo.equals(other.excelColNo))
			return false;
		if (excelRowNo == null) {
			if (other.excelRowNo != null)
				return false;
		} else if (!excelRowNo.equals(other.excelRowNo))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		if (sheetName == null) {
			if (other.sheetName != null)
				return false;
		} else if (!sheetName.equals(other.sheetName))
			return false;
		if (validInfo == null) {
			if (other.validInfo != null)
				return false;
		} else if (!validInfo.equals(other.validInfo))
			return false;
		if (validType == null) {
			if (other.validType != null)
				return false;
		} else if (!validType.equals(other.validType))
			return false;
		if (validTypeNm == null) {
			if (other.validTypeNm != null)
				return false;
		} else if (!validTypeNm.equals(other.validTypeNm))
			return false;
		return true;
	}
	

	public void setExcelRowNo(Integer excelRowNo) {
		this.excelRowNo = excelRowNo;
	}

	public Integer getExcelColNo() {
		return excelColNo;
	}

	public void setExcelColNo(Integer excelColNo) {
		this.excelColNo = excelColNo;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	public String getErrorLocation() {
		return errorLocation;
	}

	public void setErrorLocation(String errorLocation) {
		this.errorLocation = errorLocation;
	}

}
