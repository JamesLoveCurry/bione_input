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
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;

/**
 * 
 * <pre>
 * Title:菜单授权资源实现类
 * Description: 给权限框架提供菜单资源相关的数据
 * </pre>
 * 
 * @author zhongqh
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component
public class IdxResImpl implements IResObject {

	@Autowired
	private IdxInfoBS idxInfoBs;

	public static final String RES_OBJ_DEF_NO = GlobalConstants4plugin.IDX_RES_NO;

	@Autowired
	public void setAuthBS(AuthBS authBS) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#doGetResInfo()
	 */

	public List<CommonTreeNode> doGetResInfo() {
		return this.idxInfoBs.getAuthTree();
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
		return "AUTH_RES_IDX";
	}

	@Override
	public List<BioneResOperInfo> findResOperList(String resDefNo,
			List<String> resIdList) {
		// TODO Auto-generated method stub
		return null;
	}

}