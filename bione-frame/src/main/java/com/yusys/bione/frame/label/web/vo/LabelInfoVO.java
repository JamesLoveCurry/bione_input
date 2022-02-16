package com.yusys.bione.frame.label.web.vo;

import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.frame.label.entity.BioneLabelInfo;

@SuppressWarnings("serial")
public class LabelInfoVO extends BioneLabelInfo{
	public LabelInfoVO(BioneLabelInfo info,String typeName){
		BeanUtils.copy(info, this);
		this.setTypeName(typeName);
	}
	private String typeName;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
