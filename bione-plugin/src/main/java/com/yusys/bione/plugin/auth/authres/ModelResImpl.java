package com.yusys.bione.plugin.auth.authres;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.security.IResObject;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.service.RptTmpBS;

@Component
public class ModelResImpl implements IResObject{
	@Autowired
	private RptTmpBS rptTmpBS;

	public static final String RES_OBJ_DEF_NO = GlobalConstants4plugin.MODEL_RES_NO;

	@Autowired
	public void setAuthBS(AuthBS authBS) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#doGetResInfo()
	 */

	public List<CommonTreeNode> doGetResInfo() {
		return this.rptTmpBS.getModuleTree("","", "",false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#doGetResPermissions(java.util
	 * .List)
	 */

	public List<String> doGetResPermissions(
			List<BioneAuthObjResRel> authObjResRelList) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#doGetResOperateInfo()
	 */

	public List<CommonTreeNode> doGetResOperateInfo(Long resId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#doGetResDataRuleInfo(java.lang
	 * .Long)
	 */

	public List<CommonTreeNode> doGetResDataRuleInfo(Long resId) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#getResObjNo()
	 */

	public String getResObjDefNo() {
		return "AUTH_RES_MODEL";
	}

	@Override
	public List<BioneResOperInfo> findResOperList(String resDefNo,
			List<String> resIdList) {
		// TODO Auto-generated method stub
		return null;
	}
}
