/**
 * 
 */
package com.yusys.bione.frame.security.authobj;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authobj.entity.BioneAuthObjgrpInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IAuthObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.AUTH_OBJ_DEF_ID_GROUP;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
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
@Component
public class ObjGrpAuthObjImpl implements IAuthObject {

	private String icon = "images/classics/icons/couple.png";

	@Autowired
	private AuthBS authBS;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#getAuthObjDefNo()
	 */
	@Override
	public String getAuthObjDefNo() {

		return AUTH_OBJ_DEF_ID_GROUP;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#doGetAuthObjectInfo()
	 */
	@Override
	public List<CommonTreeNode> doGetAuthObjectInfo(String... searchText) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<BioneAuthObjgrpInfo> objList = null;
		if (searchText.length > 0 && StringUtils.isNotEmpty(searchText[0])) {
			objList = this.authBS.findValidAuthObjGrpOfUser(userObj
					.getCurrentLogicSysNo(), searchText[0]);
		} else {
			objList = this.authBS.findValidAuthObjGrpOfUser(userObj
					.getCurrentLogicSysNo());
		}
		if (objList == null) {
			return nodes;
		}
		for (int i = 0; i < objList.size(); i++) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_GROUP);
			CommonTreeNode node = new CommonTreeNode();
			BioneAuthObjgrpInfo obj = objList.get(i);
			node.setId(obj.getObjgrpId());
			node.setText(obj.getObjgrpName());
			node.setData(obj);
			paramMap.put("id", obj.getObjgrpId());
			paramMap.put("realNo", obj.getObjgrpNo());
			node.setParams(paramMap);
			node.setIcon(icon);

			nodes.add(node);
		}
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#doGetAuthObjectIdListOfUser()
	 */
	@Override
    public List<String> doGetAuthObjectIdListOfUser() {
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<String> authObjIdList = this.authBS.findValidAuthObjGrpIdOfUser(
				userObj.getCurrentLogicSysNo(), this.getAuthObjDefNo(),
				userObj.getUserId());

		return authObjIdList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yusys.bione.frame.security.IAuthObject#doGetAuthObjectInfoAsync
	 * (com.yusys.bione.comp.common.CommonTreeNode)
	 */
	@Override
	public List<CommonTreeNode> doGetAuthObjectInfoAsync(
			CommonTreeNode parentNode, String... searchText) {
		return this.doGetAuthObjectInfo(searchText);
	}
	
	@Override
	public String getAuthObjNameById(String id){
		List<BioneAuthObjgrpInfo> list = this.authBS.getEntityListByProperty(BioneAuthObjgrpInfo.class, "objgrpId", id);
		if(list != null && list.size() > 0){
			return list.get(0).getObjgrpName();
		}
		return "";
	}
}
