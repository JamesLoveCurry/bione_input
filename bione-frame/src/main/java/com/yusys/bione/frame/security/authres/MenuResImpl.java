package com.yusys.bione.frame.security.authres;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.auth.service.AuthBS;
import com.yusys.bione.frame.authres.entity.BioneDataRuleInfo;
import com.yusys.bione.frame.authres.entity.BioneFuncInfo;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;
/**
 * 
 * <pre>
 * Title:菜单授权资源实现类
 * Description: 给权限框架提供菜单资源相关的数据
 * </pre>
 * 
 * @author mengzx
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component
public class MenuResImpl implements IResObject {

	private String icon_home = "images/classics/icons/house.png";
	private String icon_root = "images/classics/icons/application_view_tile.png";
	private String icon = "images/classics/icons/menuItem.gif";
	
	private AuthBS authBS;
	
	public static final String RES_OBJ_DEF_NO = "AUTH_RES_MENU";

	@Autowired
	public void setAuthBS(AuthBS authBS) {
		this.authBS = authBS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#doGetResInfo()
	 */
	
	public List<CommonTreeNode> doGetResInfo() {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		List<Object[]> menuObjs = this.authBS.findValidMenu(userObj.getCurrentLogicSysNo());
		if(menuObjs == null){
			return nodes;
		}
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId(CommonTreeNode.ROOT_ID);
		rootNode.setText("全部菜单");
		rootNode.setIcon(icon_home);
		nodes.add(rootNode);
		if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			for(Object[] o : menuObjs){
				if(o.length < 5){
					// 格式不合法
					continue;
				}
				CommonTreeNode node = new CommonTreeNode();
				node.setId((String)o[0]);
				node.setUpId((String)o[3]);
				node.setText((String)o[2]);
				Map<String,String> paramMap = new HashMap<String,String>();
				paramMap.put("resDefNo", AUTH_RES_DEF_ID_MENU);
				paramMap.put("funcId", (String)o[1]);
				paramMap.put("menuId", (String)o[0]);
				paramMap.put("realId", (String)o[0]);
				paramMap.put("id", (String)o[0]);
				node.setParams(paramMap);
				String iconTmp = (String)o[4];
				if(iconTmp == null || "".equals(iconTmp)){
					if(CommonTreeNode.ROOT_ID.equals(node.getUpId())){
						//若是一级菜单
						node.setIcon(icon_root);
					}else{
						node.setIcon(icon);
					}				
				}else{
					node.setIcon(iconTmp);
				}
				nodes.add(node);
			}
		}else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
			// 获取授权菜单
			List<String> resIds = BioneSecurityUtils.getResIdListOfUser(RES_OBJ_DEF_NO);
			if(resIds == null){
				return nodes;
			}
			for(Object[] o : menuObjs){
				if(o.length < 5){
					// 格式不合法
					continue;
				}
				if(resIds.contains((String)o[0])){
					CommonTreeNode node = new CommonTreeNode();
					node.setId((String)o[0]);
					node.setUpId((String)o[3]);
					node.setText((String)o[2]);
					Map<String,String> paramMap = new HashMap<String,String>();
					paramMap.put("resDefNo", AUTH_RES_DEF_ID_MENU);
					paramMap.put("funcId", (String)o[1]);
					paramMap.put("menuId", (String)o[0]);
					paramMap.put("realId", (String)o[0]);
					paramMap.put("id", (String)o[0]);
					node.setParams(paramMap);
					String iconTmp = (String)o[4];
					if(iconTmp == null || "".equals(iconTmp)){
						if(CommonTreeNode.ROOT_ID.equals(node.getUpId())){
							//若是一级菜单
							node.setIcon(icon_root);
						}else{
							node.setIcon(icon);
						}				
					}else{
						node.setIcon(iconTmp);
					}
					nodes.add(node);
				}
			}
		}else{
			return nodes;
		}
		return nodes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ytec.bione.security.IResObject#doGetResPermissions(java.util
	 * .List)
	 */
	
	public List<String> doGetResPermissions(
			List<BioneAuthObjResRel> authObjResRelList) {

		List<String> permissions = new ArrayList<String>();

		Map<String, BioneResOperInfo> resOperInfoMap = this.authBS
				.findAllResOperInfoMap();
		Map<String, BioneDataRuleInfo> dataRuleInfoMap = this.authBS
				.findAllDataRuleInfoMap();
		Map<String, BioneFuncInfo> menuInfoMap = this.authBS.findAllMenuInfoMap();

		String permissionSepartor = GlobalConstants4frame.PERMISSION_SEPARATOR;

		for (BioneAuthObjResRel authObjResRel : authObjResRelList) {

			BioneFuncInfo menuInfo = menuInfoMap.get(authObjResRel.getId()
					.getResId());

			if (menuInfo == null || StringUtils.isEmpty(menuInfo.getNavPath()))
				continue;

			String path = menuInfo.getNavPath();

			// 去掉URL后面的参数
			path = StringUtils.substringBefore(path, "?");

			// 1、保证path以/开头，
			if (!path.startsWith("/"))
				path = "/" + path;

			String permissionId = authObjResRel.getId().getPermissionId();
			String permissionType = authObjResRel.getId().getPermissionType();

			if (permissionId.equals(GlobalConstants4frame.PERMISSION_ALL)) {

				// 菜单访问URL
				permissions.add(GlobalConstants4frame.PERMISSION_PREFIX_URL
						+ permissionSepartor + path);
			} else {

				// 操作权限
				if (GlobalConstants4frame.RES_PERMISSION_TYPE_OPER
						.equals(permissionType)) {

					BioneResOperInfo resOperInfo = resOperInfoMap
							.get(permissionId);

					if (resOperInfo != null) {
						permissions.add(GlobalConstants4frame.PERMISSION_PREFIX_OPER
								+ permissionSepartor + resOperInfo.getOperNo());
					}
				} else {// 数据权限

					BioneDataRuleInfo dataRuleInfo = dataRuleInfoMap
							.get(permissionId);

					if (dataRuleInfo != null) {
						permissions.add(GlobalConstants4frame.PERMISSION_PREFIX_DATA
								+ permissionSepartor
								+ dataRuleInfo.getDataRuleNo());
					}
				}
			}
		}

		return permissions;
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
	 * @see
	 * com.ytec.bione.security.IResObject#doGetResDataRuleInfo(java.lang
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

		return AUTH_RES_DEF_ID_MENU;
	}

	/*
	 * (non-Javadoc)
	 * @see com.yusys.bione.frame.security.IResObject#findResOperList(java.lang.String, java.util.List)
	 */
	public List<BioneResOperInfo> findResOperList(String resDefNo,
			List<String> resIdList) {
		
		List<BioneResOperInfo> operInfos = new ArrayList<BioneResOperInfo>();
		
		for (String resNo : resIdList) {
			BioneFuncInfo funcInfo = this.authBS.findFuncByMenuId(resNo);
			List<BioneResOperInfo> resOperInfos = this.authBS.getEntityListByProperty(BioneResOperInfo.class, "resNo", funcInfo.getFuncId());
			for (BioneResOperInfo bioneResOperInfo : resOperInfos) {
				bioneResOperInfo.setResNo(resNo);
				operInfos.add(bioneResOperInfo);
			}
		}
		return operInfos;
		
//		List<BioneFuncInfo> bioneFuncInfos = this.authBS.findFuncByMenuId(resIdList);
//		
//		List<BioneResOperInfo> resOperInfos = new ArrayList<BioneResOperInfo>();
////		List<String> funcInfoIds = new ArrayList<String>();
//		for (BioneFuncInfo bioneFuncInfo : bioneFuncInfos) {
////			funcInfoIds.add(bioneFuncInfo.getFuncId());
//			this.authBS.getEntityListByProperty(BioneResOperInfo.class, "operNo", value)
//			resOperInfos
//		}
//		
//		return this.authBS.findResOperList(resDefNo, funcInfoIds);
	}

}
