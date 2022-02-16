package com.yusys.biapp.input.suptask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yusys.biapp.input.task.entity.DataSupSearchInfo;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.security.IResObject;


/**
 * @ClassName: SupTaskImpl
 * @Description: TODO(给权限框架提供补录任务表相关的数据)
 * @author Yao
 * @date 2017年11月17日
 *
 */
@Component
public class SupTaskImpl implements IResObject {
	
	@Autowired
	private DataSupDAO dataSupDAO;
	
	public static final String AUTH_RES_SUP_TASK = "AUTH_RES_SUP_TASK";
	
	//返回授权资源的标识
	public String getResObjDefNo() {
		return AUTH_RES_SUP_TASK;
	}

	public List<CommonTreeNode> doGetResInfo() {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		BioneUser userObj = BioneSecurityUtils.getCurrentUserInfo();
		// 构造根节点
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		rootNode.setText("全部");
		Map<String, String> rootParam = new HashMap<String, String>();
		rootParam.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
		rootParam.put("type", "root");
		rootNode.setParams(rootParam);
		rootNode.setIsParent(true);
		nodes.add(rootNode);
		
		boolean isHierarchicalAuth = BioneSecurityUtils.checkIsHierarchicalAuth(userObj.getCurrentLogicSysNo());
		// 是否启用分级授权
		List<String> resIds = new ArrayList<String>();
		if(isHierarchicalAuth){
			resIds = BioneSecurityUtils.getResIdListOfUser(AUTH_RES_SUP_TASK);
			if(resIds == null){
				resIds = new ArrayList<String>();
			}
		}
		
		// 获取数据模型信息
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		params.put("objDefNo", "AUTH_OBJ_ROLE");
		params.put("resDefNo", "AUTH_RES_SUP_TASK");
        List<String> objRole = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get("AUTH_OBJ_ROLE");
        
        if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
        	params.put("objIds", objRole);
        }
        
		List<DataSupSearchInfo> modules = this.dataSupDAO.getTableInfoNodes(params);
		for(DataSupSearchInfo nodeObj : modules){
			CommonTreeNode childNode = new CommonTreeNode();
			Map<String, String> tableParam = new HashMap<String, String>();
			tableParam.put("dsId", nodeObj.getDsId());
			tableParam.put("tabEnNm", nodeObj.getTableEnName());
			tableParam.put("templeId", nodeObj.getLogicSysNo());
			
			tableParam.put("type", nodeObj.getType());
			
			if("table".equals(nodeObj.getType())){
				childNode.setId(nodeObj.getTableId() + "," + nodeObj.getLogicSysNo());
				childNode.setData(nodeObj.getTableId());
				childNode.setIsParent(false);
			}else{
				childNode.setIsParent(true);
				childNode.setData(nodeObj.getType());
				childNode.setId(nodeObj.getTableId());
			}
			//换成tableId
			childNode.setText(nodeObj.getTableCnName());
			childNode.setUpId(nodeObj.getUpId());
			childNode.setParams(tableParam);
			tableParam.put("resId", nodeObj.getTableId());
			tableParam.put("realId", nodeObj.getTableId());
			tableParam.put("resType", GlobalConstants4frame.RES_TYPE_NODE);
            tableParam.put("resDefNo", AUTH_RES_SUP_TASK);
			
			nodes.add(childNode);
		}
		return nodes;
	}

//	@Override
	public List<CommonTreeNode> doGetResInfo(List<String> ids) {
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		// 构造根节点
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		rootNode.setText("全部");
		Map<String, String> rootParam = new HashMap<String, String>();
		rootParam.put("realId", GlobalConstants4frame.TREE_ROOT_NO);
		rootParam.put("type", "root");
		rootNode.setParams(rootParam);
		rootNode.setIsParent(true);
		nodes.add(rootNode);
		
		// 获取数据模型信息
		Map<String,Object> params = new HashMap<String, Object>();
		List<DataSupSearchInfo> modules = this.dataSupDAO.getTableInfoNodes(params);
		
        for(DataSupSearchInfo nodeObj : modules){
            CommonTreeNode childNode = new CommonTreeNode();
            Map<String, String> tableParam = new HashMap<String, String>();
            tableParam.put("dsId", nodeObj.getDsId());
            tableParam.put("tabEnNm", nodeObj.getTableEnName());
            tableParam.put("templeId", nodeObj.getLogicSysNo());
            
            tableParam.put("type", nodeObj.getType());
            
            if("table".equals(nodeObj.getType())){
                childNode.setId(nodeObj.getTableId() + "," + nodeObj.getLogicSysNo());
                childNode.setData(nodeObj.getTableId());
                childNode.setIsParent(false);
            }else{
                childNode.setIsParent(true);
                childNode.setData(nodeObj.getType());
                childNode.setId(nodeObj.getTableId());
            }
            
            if(ids.contains(nodeObj.getTableId())){
                tableParam.put("checked", "Y");
            }
            else{
                tableParam.put("checked", "N");
            }
            //换成tableId
            childNode.setText(nodeObj.getTableCnName());
            childNode.setUpId(nodeObj.getUpId());
            childNode.setParams(tableParam);
            tableParam.put("resId", nodeObj.getTableId());
            tableParam.put("realId", nodeObj.getTableId());
            tableParam.put("resType", GlobalConstants4frame.RES_TYPE_NODE);
            tableParam.put("resDefNo", AUTH_RES_SUP_TASK);
            
            nodes.add(childNode);
        }
		
		return nodes;
	}

	public List<String> doGetResPermissions(
			List<BioneAuthObjResRel> authObjResRelList) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CommonTreeNode> doGetResOperateInfo(Long resId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CommonTreeNode> doGetResDataRuleInfo(Long resId) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<BioneResOperInfo> findResOperList(String resDefNo,
			List<String> resIdList) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
	public List<String> initResIds(List<BioneAuthObjResRel> rels) {
		List<String> resIds = new ArrayList<String>();
		if(rels != null && rels.size()>0){
			for(BioneAuthObjResRel rel : rels){
				if(!GlobalConstants4frame.RES_TYPE_OPER.equals(rel.getResType())){
					resIds.add(rel.getId().getResId());
				}
			}
		}
		return resIds;
	}

//	@Override
	public String getAuthResNameById(String resId) {
		// TODO Auto-generated method stub
		return null;
	}

}
