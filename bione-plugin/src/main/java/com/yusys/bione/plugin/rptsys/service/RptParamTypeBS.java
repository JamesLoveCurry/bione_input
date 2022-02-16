package com.yusys.bione.plugin.rptsys.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.variable.entity.BioneParamTypeInfo;
import com.yusys.bione.plugin.rptsys.repository.RptParamMybatisDao;

/**
 * <pre>
 * Title:参数类型的业务逻辑类
 * Description: 提供参数类型管理相关业务逻辑处理功能，提供事务控制
 * </pre>
 * 
 * @author yangyuhui yangyh4@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class RptParamTypeBS  {

	protected static Logger log = LoggerFactory.getLogger(RptParamTypeBS.class);
	List<BioneParamTypeInfo> resultList = null;
	@Autowired
	private RptParamMybatisDao paramDao;
	/**
	 * 根据参数类型标识查询参数类型信息
	 * 
	 * @param paramTypeNo
	 * @return
	 */
	@Transactional(readOnly = false)
	public void saveParamType(BioneParamTypeInfo info) {
		this.paramDao.paramTypesave(info);
	}
	
	@Transactional(readOnly = false)
	public void updateParamType(BioneParamTypeInfo info) {
		this.paramDao.paramTypeupdate(info);
	}
	public BioneParamTypeInfo getParamTypeById(String paramTypeId){
		Map<String, Object> values = Maps.newHashMap();
		values.put("paramTypeId", paramTypeId);
		List<BioneParamTypeInfo> lists=this.paramDao.paramTypelist(values);
		if(lists!=null&&lists.size()>0)
			return lists.get(0);
		return null;
	}
	public BioneParamTypeInfo findParamTypeInfoByNo(String paramTypeNo) {
		Map<String, Object> values = Maps.newHashMap();
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		values.put("paramTypeNo", paramTypeNo);
		List<BioneParamTypeInfo> paramTypes = this.paramDao.paramTypelist(values);
		if(paramTypes!=null&&paramTypes.size()>0)
			return paramTypes.get(0);
		return null;
	}

	/**
	 * 验证参数类型标识是否存在
	 * 
	 * @param paramTypeNo
	 * @return
	 */
	public boolean checkIsParamTypeExist(String paramTypeNo) {
		boolean flag = false;
		Map<String, Object> values = Maps.newHashMap();
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		values.put("paramTypeNo", paramTypeNo);
		List<BioneParamTypeInfo> paramTypeList = this.paramDao.paramTypelist(values);
		if (paramTypeList != null && paramTypeList.size() > 0) {
			flag = true;
		}
		return flag;
	}

	/**
	 * 删除当前参数类型包含所有子类型及相关参数值
	 * 
	 * @param paramTypeNo
	 * @return
	 */
	public void delParamTypeListByNo(String paramTypeNo) {

		List<BioneParamTypeInfo> list = findParamTypeListByNo(paramTypeNo);
		if (list != null) {
			for (BioneParamTypeInfo paramType : list) {
				String curparamTypeNo = paramType.getParamTypeNo();
				// 删除参数类型和关联参数值
				Map<String, Object> values = Maps.newHashMap();
				values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
						.getCurrentLogicSysNo());
				values.put("paramTypeNo", curparamTypeNo);
				this.paramDao.paramTypedelete(values);
				this.paramDao.paramInfodelete(values);
			}
		}
		// 删除参数类型和关联参数值
		Map<String, Object> values = Maps.newHashMap();
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		values.put("paramTypeNo", paramTypeNo);
		this.paramDao.paramTypedelete(values);
		this.paramDao.paramInfodelete(values);
	}

	/**
	 * 递归查询参数类型下所有子类型
	 * 
	 * @param paramTypeNo
	 */
	private List<BioneParamTypeInfo> findParamTypeListByNo(String paramTypeNo) {
		Map<String, Object> values = Maps.newHashMap();
		values.put("upNo", paramTypeNo);
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		List<BioneParamTypeInfo> list = this.paramDao.paramTypelist(values);
		for (BioneParamTypeInfo paramType : list) {
			if (resultList == null) {
				resultList = new ArrayList<BioneParamTypeInfo>();
			}
			resultList.add(paramType);
			findParamTypeListByNo(paramType.getParamTypeNo());
		}
		return resultList;
	}

	/**
	 * 生成类型树
	 * 
	 * @param nodeId
	 * @return
	 */
	public List<CommonTreeNode> buildParamTypeTree(String nodeId) {
		List<CommonTreeNode> treeList = new ArrayList<CommonTreeNode>();
		if (nodeId == null || "".equals(nodeId)) {
			nodeId = "0";
		}
		Map<String, Object> values = Maps.newHashMap();
		values.put("upNo", nodeId);
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		List<BioneParamTypeInfo> list = this.paramDao.paramTypelist(values);
		for (BioneParamTypeInfo paramType : list) {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(paramType.getParamTypeNo());
			treeNode.setUpId(paramType.getUpNo());
			treeNode.setText(paramType.getParamTypeName());
			treeNode.setData(paramType);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("isParent", isHadChild(paramType.getParamTypeNo()));
			treeNode.setParams(paramMap);
			treeList.add(treeNode);
		}

		return treeList;
	}

	/**
	 * 是否有子节点
	 * @param nodeId
	 * @return
	 */
	public String isHadChild(String nodeId){
		Map<String, Object> values = Maps.newHashMap();
		values.put("upNo", nodeId);
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		List<BioneParamTypeInfo> list = this.paramDao.paramTypelist(values);
		if(list.size()>0){
			return "true";
		}else{
			return "false";
		}
		
	}
	/**
	 * 通过逻辑系统标识获得参数列
	 * 
	 * @param logicSysNo
	 *            逻辑系统编号
	 * @return paramTypeList 参数类型列表
	 */
	public List<BioneParamTypeInfo> findParamTypeList(String logicSysNo) {
		List<BioneParamTypeInfo> paramTypeList = null;
		Map<String, Object> values = Maps.newHashMap();
		values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		paramTypeList= this.paramDao.paramTypelist(values);
		return paramTypeList;
	}
}
