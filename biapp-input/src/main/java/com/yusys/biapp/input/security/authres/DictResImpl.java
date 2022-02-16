package com.yusys.biapp.input.security.authres;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.security.IResObject;

@Component
public class DictResImpl implements IResObject {

	public static String name = "dictResImpl"; 
//	@Autowired
//	private DirBS dirBS;
//	
//	@Autowired
//	private TempleBS templeBS;
	
	public static final String RES_OBJ_DEF_NO = "AUTH_RES_LIB";
private static final String AUTH_RES_DEF_ID_MENU = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytec.bione.security.IResObject#doGetResInfo()
	 */
	
	public List<CommonTreeNode> doGetResInfo() {
		
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		
//		BioneUser biOneUser = BioneSecurityUtils.getCurrentUserInfo();
		
//		List<CommonTreeNode> dirList = dirBS.getByType(UdipConstants.DIR_TYPE_LIB, biOneUser.getCurrentLogicSysNo());
//		for(CommonTreeNode dir: dirList){//目录，能看到所有的模板目录
//			CommonTreeNode node = new CommonTreeNode();
//			node.setId(dir.getId());
//			node.setUpId(dir.getUpId());
//			node.setText(dir.getText());
//			node.setIcon(UdipConstants.ICON_FOLDER);
//			nodes.add(node);
// 		}
		
		nodes.addAll(getLib());
 		
		return nodes;
	}

	public List<CommonTreeNode> getLib(){
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
//		BioneUser bioneUser = BioneSecurityUtils.getCurrentUserInfo();
//		List<String> roleList = bioneUser.getRoleList();
//		String logicSysNo = bioneUser.getCurrentLogicSysNo() ;
//		List<String> list = Lists.newArrayList();
//		List<InputDataDictInfo> libList = templeBS.getEntityListByProperty(UdipDataLibInfo.class, "logicSysNo", logicSysNo);
//		//List<BioneAuthObjResRel> resList = dirBS.findByPropertys(BioneAuthObjResRel.class, new String[]{"id.logicSysNo","id.objDefNo","id.resDefNo"}, new Object[]{logicSysNo,GlobalConstants.UA_AUTH_OBJ_DEF_ID_ROLE,RES_OBJ_DEF_NO});
//		List<BioneAuthObjResRel> resList = dirBS.findByPropertys(BioneAuthObjResRel.class, new String[]{"id.logicSysNo","id.objDefNo","id.resDefNo"}, new Object[]{logicSysNo,GlobalConstants.LOCAL_AUTH_OBJ_DEF_ID_ROLE,RES_OBJ_DEF_NO});
//		for(BioneAuthObjResRel res: resList){//有权限查看的
//			if(roleList.contains(res.getId().getObjId()))
//				list.add(res.getId().getResId());
//		}
 		
// 		for(InputDataDictInfo lib: libList){
// 			if(bioneUser.isSuperUser()||lib.getCreator().equals(bioneUser.getLoginName())||list.contains(lib.getLibId())){//只能看到自己创建的，或者拥有角色的
// 				CommonTreeNode node = new CommonTreeNode();
// 				node.setId(lib.getLibId());
// 				node.setUpId(lib.getLibDir());
// 				node.setText(lib.getLibName());
// 				node.setIcon(UdipConstants.ICON_LIB);
// 				nodes.add(node);
// 			}
//			}
 		return nodes;
	}
	
	public List<String> getLibId(){
		List<CommonTreeNode> nodes = getLib();
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

		return AUTH_RES_DEF_ID_MENU;
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
