/**
 * 
 */
package com.yusys.bione.frame.security.authobj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.citicbank.authservice.common.ConnectUaBean;
import com.citicbank.authservice.common.RoleBean;
import com.citicbank.uaauthservice.UaAuthService;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfoUa;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.IAuthObject;

/**
 * <pre>
 * Title:Ua认证的机构授权对象实现类
 * Description:获取全部角色列表以及用户的授权角色列表
 * </pre>
 * 
 * @author songxf songxf@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component
public class UaRoleAuthObjImpl implements IAuthObject {

	private String icon = "images/classics/icons/role.gif";

	@Autowired
	public AuthConfigBS authConfigBS;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#getAuthObjDefNo()
	 */

	@Override
	public String getAuthObjDefNo() {

		return "UA_AUTH_OBJ_ROLE";

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IAuthObject#doGetAuthObjectInfo()
	 */

	@Override
	public List<CommonTreeNode> doGetAuthObjectInfo(String... searchText) {

		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		List<BioneAuthInfoUa> lists=this.authConfigBS.getEntityList(BioneAuthInfoUa.class);
		BioneAuthInfoUa authUa=new BioneAuthInfoUa();
		if(lists!=null&&lists.size()>0){
			authUa = lists.get(0);
		}
		else{
			return null;
		}
		// 生成连接服务参数类，构造方法ConnectUaBean(ip地址，访问端口，认证系统代码)
		ConnectUaBean cub = new ConnectUaBean(authUa.getIpAddress(), authUa.getServerPort(), authUa.getAuthSystemNo());
		// 生成提供服务类
		UaAuthService uas = new UaAuthService(cub);

		RoleBean[] rb = null;
		try {
			rb = uas.getAllRoleInfoByAuthSysCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < rb.length; i++) {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("objDefNo", "UA_AUTH_OBJ_ROLE");
			RoleBean role = rb[i];
			CommonTreeNode node = new CommonTreeNode();
			node.setId(role.getRoleCode());
			node.setText(role.getRoleName());
			node.setData(role);
			paramMap.put("realNo", role.getRoleCode());
			paramMap.put("id", role.getRoleCode());
			node.setParams(paramMap);
			node.setIcon(icon);
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

		BioneAuthInfoUa authUa = (BioneAuthInfoUa) this.authConfigBS.getEntityList(BioneAuthInfoUa.class).get(0);
		// 生成连接服务参数类，构造方法ConnectUaBean(ip地址，访问端口，认证系统代码)
		ConnectUaBean cub = new ConnectUaBean(authUa.getIpAddress(), authUa.getServerPort(), authUa.getAuthSystemNo());
		// 生成提供服务类
		UaAuthService uas = new UaAuthService(cub);

		RoleBean[] rb = null;
		try {
			// 客户端代码中要求密码必须等于40位，密码传递了40个0
			rb = uas.getRoleInfoByUserName(BioneSecurityUtils.getCurrentUserInfo().getLoginName(),
					"0000000000000000000000000000000000000000");
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < rb.length; i++) {
			result.add(rb[i].getRoleCode());
		}

		return result;
	}

	/* (non-Javadoc)
	 * @see com.yusys.bione.frame.security.IAuthObject#doGetAuthObjectInfoAsync(com.yusys.bione.comp.common.CommonTreeNode)
	 */
	@Override
	public List<CommonTreeNode> doGetAuthObjectInfoAsync(
			CommonTreeNode parentNode, String... searchText) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String getAuthObjNameById(String id){
		return "";
	}
}
