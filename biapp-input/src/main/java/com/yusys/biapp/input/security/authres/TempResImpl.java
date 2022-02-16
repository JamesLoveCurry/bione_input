package com.yusys.biapp.input.security.authres;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yusys.biapp.input.catalog.service.DataInputCatalogBS;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
/**
 * 
 * @author tobe
 *
 */
@Component
public class TempResImpl implements IResObject {

	public static String name = "tempResImpl"; 
	@Autowired
	private DataInputCatalogBS dirBS;
	
	@Autowired
	private TempleBS templeBS;
	
	public static final String RES_OBJ_DEF_NO = "AUTH_RES_TEMP";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#doGetResInfo()
	 */
	
	public List<CommonTreeNode> doGetResInfo() {
		
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		
//		BioneUser BioneUser = BioneSecurityUtils.getCurrentUserInfo();
		
//		List<CommonTreeNode> dirList = dirBS.getByType(UdipConstants.DIR_TYPE_TEMP, BioneUser.getCurrentLogicSysNo());
//		for(CommonTreeNode dir: dirList){//目录，能看到所有的模板目录
//			CommonTreeNode node = new CommonTreeNode();
//			node.setId(dir.getId());
//			node.setUpId(dir.getUpId());
//			node.setText(dir.getText());
//			node.setIcon(UdipConstants.ICON_FOLDER);
//			nodes.add(node);
// 		}
		
		nodes.addAll(getTemp());
 		
		return nodes;
	}

	public List<CommonTreeNode> getTemp(){
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser BioneUser = BioneSecurityUtils.getCurrentUserInfo();
		List<String> roleList = BioneUser.getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ROLE);
		String logicSysNo = BioneUser.getCurrentLogicSysNo() ;
		List<String> list = Lists.newArrayList();
		List<RptInputLstTempleInfo> tempList = this.templeBS.findEntityListByProperty("logicSysNo", logicSysNo);
		//List<BioneAuthObjResRel> resList = dirBS.findByPropertys(BioneAuthObjResRel.class, new String[]{"id.logicSysNo","id.objDefNo","id.resDefNo"}, new Object[]{logicSysNo,GlobalConstants.UA_AUTH_OBJ_DEF_ID_ROLE,RES_OBJ_DEF_NO});
//		List<BioneAuthObjResRel> resList = dirBS.findByPropertys(BioneAuthObjResRel.class, new String[]{"id.logicSysNo","id.objDefNo","id.resDefNo"}, new Object[]{logicSysNo,GlobalConstants.LOCAL_AUTH_OBJ_DEF_ID_ROLE,RES_OBJ_DEF_NO});
		List<BioneAuthObjResRel> resList = dirBS.findByPropertys(logicSysNo, GlobalConstants4plugin.LOCAL_AUTH_OBJ_DEF_ID_ROLE,RES_OBJ_DEF_NO);
		if(resList != null && roleList != null){
			for(BioneAuthObjResRel res: resList){//有权限查看的
				if(roleList.contains(res.getId().getObjId()))
					list.add(res.getId().getResId());
			}
			
		}
		
 		for(RptInputLstTempleInfo temp: tempList){
 			if(BioneUser.isSuperUser()||temp.getDutyUser().equals(BioneUser.getLoginName())||list.contains(temp.getTempleId())){//只能看到自己创建的，或者拥有角色的
 				CommonTreeNode node = new CommonTreeNode();
 				node.setId(temp.getTempleId());
 				node.setUpId(temp.getCatalogId());
 				node.setText(temp.getTempleName());
 				node.setIcon(UdipConstants.ICON_TEMPLE);
 				nodes.add(node);
 			}
 		}
 		return nodes;
	}
	
	public List<String> getTempId(){
		List<CommonTreeNode> nodes = getTemp();
		List<String> idList = Lists.newArrayList();
		
		for(CommonTreeNode node : nodes){
			idList.add(node.getId());
		}
 		return idList;
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

		return GlobalConstants4frame.AUTH_RES_DEF_ID_MENU;
	}

	/*
	 * (non-Javadoc)
	 * @see com.yuchengtech.emp.biappframe.security.IResObject#findResOperList(java.lang.String, java.util.List)
	 */
	public List<BioneResOperInfo> findResOperList(String resDefNo,
			List<String> resIdList) {
		
		List<BioneResOperInfo> operInfos = new ArrayList<BioneResOperInfo>();
		
		return operInfos;
		
	}

//	@Override
//	public List<CommonTreeNode> doGetResInfo(List<String> ids) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public List<String> initResIds(List<BioneAuthObjResRel> rels) {
//		// TODO Auto-generated method stub
//		List<String> resIds = new ArrayList<String>();
//		if(rels != null && rels.size()>0){
//			for(BioneAuthObjResRel rel : rels){
//				resIds.add(rel.getId().getResId());
//			}
//		}
//		return resIds;
//	}
//
//	@Override
//	public String getAuthResNameById(String resId) {
//		// TODO Auto-generated method stub
//		return null;
//	}
}
