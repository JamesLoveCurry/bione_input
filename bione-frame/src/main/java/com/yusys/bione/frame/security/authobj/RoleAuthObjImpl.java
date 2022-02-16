/**
 * 
 */
package com.yusys.bione.frame.security.authobj;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfo;
import com.yusys.bione.frame.authobj.entity.BioneRoleInfoExt;
import com.yusys.bione.frame.authobj.service.RoleBS;
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

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE;


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
public class RoleAuthObjImpl implements IAuthObject {

	protected String icon = "images/classics/icons/role.gif";

	@Autowired
	private AuthBS authBS;
	
	@Autowired
	private RoleBS roleBS;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#getAuthObjDefNo()
	 */
	@Override
	public String getAuthObjDefNo() {
		return AUTH_OBJ_DEF_ID_ROLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ytec.bione.security.IAuthObject#doGetAuthObjectIdListOfUser()
	 */
	@Override
    public List<String> doGetAuthObjectIdListOfUser() {
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<String> authObjIdList = this.authBS.findValidAuthRoleIdOfUser(
				userObj.getCurrentLogicSysNo(), this.getAuthObjDefNo(),
				userObj.getUserId());

		return authObjIdList;
	}

	/* (non-Javadoc)
	 * @see com.yusys.bione.frame.security.IAuthObject#doGetAuthObjectInfoAsync(com.yusys.bione.comp.common.CommonTreeNode)
	 */
	@Override
	public List<CommonTreeNode> doGetAuthObjectInfoAsync(
			CommonTreeNode parentNode, String... searchText) {

		return this.doGetAuthObjectInfo(searchText);
	}

	
	@Override
	public String getAuthObjNameById(String id){
		List<BioneRoleInfo> list = this.authBS.getEntityListByProperty(BioneRoleInfo.class, "roleId", id);
		if(list != null && list.size() > 0){
			return list.get(0).getRoleName();
		}
		return "";
	}
	
	@Override
	public List<CommonTreeNode> doGetAuthObjectInfo(String... searchText) {
		CommonTreeNode commonTreeNode;
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		List<BioneRoleInfoExt> bean = this.roleBS.getBioneRoleInfoExt();
		for (BioneRoleInfoExt boine : bean) {
			commonTreeNode = new CommonTreeNode();
			commonTreeNode.setId(boine.getCode());
			commonTreeNode.setText(boine.getCodeDesc());
			nodes.add(commonTreeNode);
		}
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<BioneRoleInfo> roleList = null;
		
		if (searchText.length > 0 && StringUtils.isNotEmpty(searchText[0])) {
			roleList = this.authBS
					.findValidAuthRoleOfUser(userObj.getCurrentLogicSysNo(), searchText[0]);
		} else {
			roleList = this.authBS
					.findValidAuthRoleOfUser(userObj.getCurrentLogicSysNo());
		}
		if (roleList == null) {
			return nodes;
		}
		for (int i = 0; i < roleList.size(); i++) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_ROLE);
			CommonTreeNode node = new CommonTreeNode();
			BioneRoleInfo role = roleList.get(i);
			node.setId(role.getRoleId());
			node.setText(role.getRoleName());
			node.setData(role);
			paramMap.put("realNo", role.getRoleNo());
			paramMap.put("objDefNo", AUTH_OBJ_DEF_ID_ROLE);
			paramMap.put("id", role.getRoleId());
			node.setParams(paramMap);
			node.setIcon(icon);
			node.setUpId(role.getRoleTypeJg());
			nodes.add(node);
		}
		return nodes;
	}
}
