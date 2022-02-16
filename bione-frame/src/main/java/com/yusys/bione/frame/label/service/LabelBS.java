package com.yusys.bione.frame.label.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.frame.label.entity.BioneLabelInfo;
import com.yusys.bione.frame.label.entity.BioneLabelObjInfo;
import com.yusys.bione.frame.label.entity.BioneLabelObjRel;
import com.yusys.bione.frame.label.entity.BioneLabelObjRelPK;
import com.yusys.bione.frame.label.web.vo.LabelInfoVO;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * <pre>
 * Title:标签配置
 * Description:
 * </pre>
 * 
 * @author weijx weijx@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：		  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class LabelBS extends BaseBS<Object> {

	/**
	 * 得出树节点
	 * 
	 * @param realId
	 * @param type
	 * @return
	 */
	public List<CommonTreeNode> getTreeNode(String basePath,String realId, String type, String labelObjNo) {
		String logicSysNo = getLogicSysNo();
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		
		if (StringUtils.isEmpty(realId) && StringUtils.isEmpty(type)) {
			if(StringUtils.isNotBlank(labelObjNo)){
				BioneLabelObjInfo info = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
				String jql1 = "select info from BioneLabelInfo info where 1=1 and info.labelObjId=?0 and info.upId = ?1 order by info.labelName";
				String jql2 = "select distinct info.upId from BioneLabelInfo info";
				List<BioneLabelInfo> list1 = baseDAO.findWithIndexParam(
						jql1, info.getLabelObjId(),"0");
				List<BioneLabelInfo> plist = baseDAO.findWithIndexParam(
						jql2);
				if (list1 != null) {
					for (int i = 0; i < list1.size(); i++) {
						BioneLabelInfo entity = list1.get(i);
						CommonTreeNode node = new CommonTreeNode();
						node.setId("type_" + entity.getLabelId());
						node.setText(entity.getLabelName());
						Map<String, String> params = Maps.newHashMap();
						params.put("type", "label");
						params.put("labelObjId", entity.getLabelObjId());
						params.put("realId", entity.getLabelId());
						node.setParams(params);
						node.setIsParent(false);
						if(plist.contains(entity)){
							node.setIsParent(true);
						}
						node.setIcon(basePath
								+ "/images/classics/icons/tag_blue.png");
						nodeList.add(node);
					}
				}
			}
			else{
				List<BioneLabelObjInfo> list = new ArrayList<BioneLabelObjInfo>();
				String jql1 = "select t from BioneLabelObjInfo t where t.logicSysNo=?0 ";
				String jql2 = "select distinct info.labelObjId from BioneLabelInfo info";
				list = baseDAO.findWithIndexParam(jql1,
						logicSysNo);
				List<Object> pList = baseDAO.findWithIndexParam(jql2);
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						BioneLabelObjInfo entity = list.get(i);
						CommonTreeNode node = new CommonTreeNode();
						node.setId("obj_" + entity.getLabelObjId());
						node.setText(entity.getLabelObjName());
						Map<String, String> params = Maps.newHashMap();
						params.put("type", "obj");
						params.put("realId", entity.getLabelObjId());
						node.setParams(params);
						node.setIsParent(false);
						if (pList != null && pList.contains(entity.getLabelObjId())) {
							node.setIsParent(true);
						}
						node.setIcon(basePath
								+ "/images/classics/icons/tag_red.png");
						nodeList.add(node);
					}
				}
			}
		}
		if (!StringUtils.isEmpty(realId) && !StringUtils.isEmpty(type)) {
			if ("obj".equals(type)) {
				String jql1 = "select info from BioneLabelInfo info where 1=1 and info.labelObjId=?0 and info.upId = ?1 order by info.labelName";
				String jql2 = "select distinct info.upId from BioneLabelInfo info";
				List<BioneLabelInfo> list1 = baseDAO.findWithIndexParam(
						jql1, realId,"0");
				List<BioneLabelInfo> plist = baseDAO.findWithIndexParam(
						jql2);
				if (list1 != null) {
					for (int i = 0; i < list1.size(); i++) {
						BioneLabelInfo entity = list1.get(i);
						CommonTreeNode node = new CommonTreeNode();
						node.setId("type_" + entity.getLabelId());
						node.setText(entity.getLabelName());
						Map<String, String> params = Maps.newHashMap();
						params.put("type", "label");
						params.put("labelObjId", entity.getLabelObjId());
						params.put("realId", entity.getLabelId());
						node.setParams(params);
						node.setIsParent(false);
						if(plist.contains(entity)){
							node.setIsParent(true);
						}
						node.setIcon(basePath
								+ "/images/classics/icons/tag_blue.png");
						nodeList.add(node);
					}
				}
			}
			if ("label".equals(type)) {
				String jql = "select info from BioneLabelInfo info where info.upId=?0 order by info.labelName ";
				String jql2 = "select distinct info.upId from BioneLabelInfo info";
				List<BioneLabelInfo> list = baseDAO.findWithIndexParam(jql,
						realId);
				List<BioneLabelInfo> plist = baseDAO.findWithIndexParam(
						jql2);
				if (list != null) {
					for (int i = 0; i < list.size(); i++) {
						BioneLabelInfo entity = list.get(i);
						CommonTreeNode node = new CommonTreeNode();
						node.setId("label_" + entity.getLabelId());
						node.setText(entity.getLabelName());
						Map<String, String> params = Maps.newHashMap();
						params.put("type", "label");
						params.put("realId", entity.getLabelId());
						params.put("labelObjId", entity.getLabelObjId());
						node.setParams(params);
						node.setIsParent(false);
						if(plist.contains(entity)){
							node.setIsParent(true);
						}
						node.setIcon(basePath
								+ "/images/classics/icons/tag_blue.png");
						nodeList.add(node);
					}
				}
			}
		}
		return nodeList;
	}

	/**
	 * 得到标签对象内容列表
	 * 
	 * @param firstResult
	 * @param pageSize
	 * @param condition
	 * @return
	 */
	public SearchResult<BioneLabelObjInfo> findObjPage(int firstResult,
			int pageSize, Map<String, Object> condition) {
		StringBuilder jql = new StringBuilder();
		jql.append("select t from BioneLabelObjInfo t where t.logicSysNo=:logicSysNo");
		String where = (String) condition.get("jql");
		if (!StringUtils.isEmpty(where)) {
			jql.append(" and ").append(where);
		}
		jql.append(" order by t.labelObjNo asc");
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) condition
				.get("params");
		values.put("logicSysNo", getLogicSysNo());
		SearchResult<BioneLabelObjInfo> sr = baseDAO.findPageWithNameParam(
				firstResult, pageSize, jql.toString(), values);
		return sr;
	}


	/**
	 * 得到标签内容列表
	 * 
	 * @param firstResult
	 * @param pageSize
	 * @param condition
	 * @param realId
	 * @return
	 */
	public SearchResult<BioneLabelInfo> findLabelPage(int firstResult,
			int pageSize, Map<String, Object> condition, String realId,String labelObjId) {
		StringBuilder jql = new StringBuilder();
		jql.append("select t from BioneLabelInfo t where t.upId=:realId and t.labelObjId=:labelObjId");
		String where = (String) condition.get("jql");
		if (!StringUtils.isEmpty(where)) {
			jql.append(" and ").append(where);
		}
		jql.append(" order by t.labelId asc");
		@SuppressWarnings("unchecked")
		Map<String, Object> values = (Map<String, Object>) condition
				.get("params");
		values.put("realId", realId);
		values.put("labelObjId", labelObjId);
		SearchResult<BioneLabelInfo> sr = baseDAO.findPageWithNameParam(
				firstResult, pageSize, jql.toString(), values);
		return sr;
	}

	/**
	 * 获取系统逻辑号
	 * 
	 * @return
	 */
	public String getLogicSysNo() {
		String logicSysNo = BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo();
		return logicSysNo;
	}

	/**
	 * 删除标签对象
	 * 
	 * @param id
	 */
	public void deleteObj(String id) {
		JSONArray ja = JSON.parseArray(id);
		List<String> idList = Lists.newArrayList();
		for (int i = 0; i < ja.size(); i ++) {
			idList.add(ja.getString(i));
		}
		String jql1 = "delete from BioneLabelObjInfo t where t.labelObjId in ?0";
		String jql2 = "delete from BioneLabelTypeInfo t where t.labelObjId in ?0";
		String jql3 = "delete from BioneLabelInfo t where t.typeId in (select t2.typeId from BioneLabelTypeInfo t2 where t2.labelObjId in (?0))";
		String jql4 = "delete from BioneLabelObjRel t where t.id.labelObjId in ?0";
		baseDAO.batchExecuteWithIndexParam(jql4, idList);
		baseDAO.batchExecuteWithIndexParam(jql3, idList);
		baseDAO.batchExecuteWithIndexParam(jql2, idList);
		baseDAO.batchExecuteWithIndexParam(jql1, idList);
	}

	/**
	 * 删除标签类型
	 * 
	 * @param id
	 */
	public void deleteLabel(String id) {
		String jql1 = "delete from BioneLabelInfo t";
		if(StringUtils.isNotBlank(id)) {
			List<String> idList = Lists.newArrayList();
			JSONArray ja = JSON.parseArray(id);
			for (int i = 0; i < ja.size(); i ++) {
				idList.add(ja.getString(i));
			}
			jql1 += " where t.labelId in ?0"; 
			baseDAO.batchExecuteWithIndexParam(jql1, idList);
		}
		baseDAO.batchExecuteWithIndexParam(jql1);
	}

	/**
	 * 是否存在标签对象标识
	 * 
	 * @param id
	 * @param labelObjId
	 * @return
	 */
	public boolean isContainObj(String id, String labelObjId) {
		String jql = "";
		Long obj = 0L;
		if (StringUtils.isEmpty(labelObjId)) {
			jql = "select count(t) from BioneLabelObjInfo t where t.labelObjNo=?0";
			obj = baseDAO.findUniqueWithIndexParam(jql, id);
		} else {
			jql = "select count(t) from BioneLabelObjInfo t where t.labelObjNo=?0 and t.labelObjId<>?1";
			obj = baseDAO.findUniqueWithIndexParam(jql, id, labelObjId);
		}
		if (obj != null && obj > 0L) {
			return true;
		}
		return false;
	}

	/**
	 * 是否存在标签对象名
	 * 
	 * @param name
	 * @param labelObjId
	 * @return
	 */
	public boolean isContainObjName(String name, String labelObjId) {
		String jql = "";
		Long obj = 0L;
		if (StringUtils.isEmpty(labelObjId)) {
			jql = "select count(t) from BioneLabelObjInfo t where t.labelObjName=?0";
			obj = baseDAO.findUniqueWithIndexParam(jql, name);
		} else {
			jql = "select count(t) from BioneLabelObjInfo t where t.labelObjName=?0 and t.labelObjId<>?1";
			obj = baseDAO.findUniqueWithIndexParam(jql, name, labelObjId);
		}
		if (obj != null && obj > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 是否存在标签类型名
	 * 
	 * @param id
	 * @param typeName
	 * @param labelObjId
	 * @return
	 */
	public boolean isContainTypeName(String id, String typeName,
			String labelObjId) {
		String jql = "";
		Long obj = 0L;
		if (StringUtils.isEmpty(id)) {
			jql = "select count(t) from BioneLabelTypeInfo t where t.typeName=?0 and t.labelObjId=?1";
			obj = baseDAO.findUniqueWithIndexParam(jql, typeName, labelObjId);
		} else {
			jql = "select count(t) from BioneLabelTypeInfo t where t.typeName=?0 and t.labelObjId=?1 and t.typeId<>?2";
			obj = baseDAO.findUniqueWithIndexParam(jql, typeName, labelObjId,
					id);
		}
		if (obj > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 是否存在标签名
	 * 
	 * @param id
	 * @param labelName
	 * @param typeId
	 * @return
	 */
	public boolean isContainLabelName(String id, String labelName, String labelObjId) {
		String jql = "";
		Long obj = 0L;
		if (StringUtils.isEmpty(id)) {
			jql = "select count(t) from BioneLabelInfo t where t.labelName=?0 and t.labelObjId=?1";
			obj = baseDAO.findUniqueWithIndexParam(jql, labelName, labelObjId);
		} else {
			jql = "select count(t) from BioneLabelInfo t where t.labelName=?0 and t.labelObjId=?1 and t.labelId<>?2";
			obj = baseDAO.findUniqueWithIndexParam(jql, labelName, labelObjId, id);
		}
		if (obj > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 接口: 根据标签类型获取标签
	 * 
	 * @param typeId
	 *            标签类型
	 * @return 标签列表
	 */
	public List<BioneLabelInfo> getLabelsByType(String typeId) {
		String jql = "select t from BioneLabelInfo t where t.typeId=?0";
		List<BioneLabelInfo> list = baseDAO.findWithIndexParam(jql, typeId);
		return list;
	}


	public List<CommonComboBoxNode> getLabelInfo(String labelObjNo){
		List<CommonComboBoxNode>  nodes = new ArrayList<CommonComboBoxNode>();
		String jql ="select new com.yusys.bione.frame.label.web.vo.LabelInfoVO(label,type.typeName) from BioneLabelInfo label,BioneLabelTypeInfo type,BioneLabelObjInfo obj where label.typeId = type.typeId and type.labelObjId = obj.labelObjId and obj.labelObjNo = ?0";
		List<LabelInfoVO> vos = this.baseDAO.findWithIndexParam(jql, labelObjNo);
		if(vos != null && vos.size()>0){
			for(LabelInfoVO vo : vos){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(vo.getLabelId());
				node.setText(vo.getTypeName()+": "+vo.getLabelName());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public List<CommonComboBoxNode> getObjLabelInfo(String id,String labelObjNo){
		List<CommonComboBoxNode>  nodes = new ArrayList<CommonComboBoxNode>();
		String jql ="select new com.yusys.bione.frame.label.web.vo.LabelInfoVO(label,type.typeName) from BioneLabelInfo label,BioneLabelTypeInfo type,BioneLabelObjInfo obj,BioneLabelObjRel rel where rel.id.objId = ?0 and obj.labelObjNo = ?1 and rel.id.labelId = label.labelId and label.typeId = type.typeId and type.labelObjId = obj.labelObjId  ";
		List<LabelInfoVO> vos = this.baseDAO.findWithIndexParam(jql, id, labelObjNo);
		if(vos != null && vos.size()>0){
			for(LabelInfoVO vo : vos){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(vo.getLabelId());
				node.setText(vo.getTypeName()+": "+vo.getLabelName());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	@Transactional(readOnly = false)
	public void saveLabelRel(String id, String labelObjNo, String labelIds) {
		BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
		String jql = "delete from BioneLabelObjRel rel where rel.id.objId=?0";
		this.baseDAO.batchExecuteWithIndexParam(jql, id);
		String labels[] = StringUtils.split(labelIds,",");
		for(int i=0; i < labels.length ; i++){
			BioneLabelObjRel rel = new BioneLabelObjRel();
			BioneLabelObjRelPK rid = new  BioneLabelObjRelPK();
			rid.setLabelId(labels[i]);
			rid.setLabelObjId("");
			if(obj!=null){
				rid.setLabelObjId(obj.getLabelObjId());
			}
			rid.setObjId(id);
			rel.setId(rid);
			this.saveOrUpdateEntity(rel);
		}
	}
	
	public Map<String,Object> getLabelRel(String id, String labelObjNo){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		Map<String,Object> result = new HashMap<String, Object>();
		Map<String,Map<String,String>> infoMaps = getLabelMap(labelObjNo);
		Map<String,String> infoNamesMaps = infoMaps.get("name");
		BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
		String jql ="select info from BioneLabelInfo info ,BioneLabelObjRel rel where 1=1 and rel.id.labelObjId=?0 and rel.id.objId = ?1 and info.labelId = rel.id.labelId and info.labelObjId = rel.id.labelObjId";
		List<BioneLabelInfo> infos = this.baseDAO.findWithIndexParam(jql, obj.getLabelObjId(),id);
		if(infos != null && infos.size() > 0){
			for(BioneLabelInfo info : infos){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(info.getLabelId());
				node.setText(infoNamesMaps.get(info.getLabelId()));
				nodes.add(node);
			}
		}
		result.put("infoMaps", infoMaps);
		result.put("nodes", nodes);
		return result;
	}
	
	public Map<String,Map<String,String>> getLabelMap(String labelObjNo){
		Map<String,Map<String,String>> result = new HashMap<String, Map<String,String>>();
		List<BioneLabelInfo> infos = new ArrayList<BioneLabelInfo>();
		if(StringUtils.isNotBlank(labelObjNo)){
			BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
			infos = this.getEntityListByProperty(BioneLabelInfo.class, "labelObjId", obj.getLabelObjId());
		}
		else{
			infos = this.getAllEntityList(BioneLabelInfo.class, "labelObjId", false);
		}
		Map<String,BioneLabelInfo> infoMaps = new HashMap<String, BioneLabelInfo>();
		Map<String,String> infoNamesMaps = new HashMap<String, String>();
		Map<String,String> infoIdsMaps = new HashMap<String, String>();
		Map<String,String> infoPIdsMaps = new HashMap<String, String>();
		
		if(infos != null && infos.size()>0){
			for(BioneLabelInfo info : infos){
				infoMaps.put(info.getLabelId(), info);
			}
			for(BioneLabelInfo info : infos){
				String labelNm = info.getLabelName();
				String labelId = info.getLabelId();
				BioneLabelInfo ninfo = info;
				while(true){
					if(ninfo != null)
					ninfo = infoMaps.get(ninfo.getUpId());
					if(ninfo!=null){
						labelNm = ninfo.getLabelName()+">"+labelNm;
						labelId = ninfo.getLabelId()+","+labelId;
					}
					if(ninfo==null || !ninfo.getUpId().equals("0")){
						break;
					}
				}
				infoNamesMaps.put(info.getLabelId(), labelNm);
				infoIdsMaps.put(info.getLabelId(), labelId);
				if(!info.getUpId().equals("0")){
					if(infoPIdsMaps.get(info.getUpId())==null){
						infoPIdsMaps.put(info.getUpId(),info.getLabelId());
					}
					else{
						String labelIds = infoPIdsMaps.get(info.getUpId());
						labelIds += "," + info.getLabelId();
						infoPIdsMaps.put(info.getUpId(),labelIds);
					}
				}
				
			}
		}
		result.put("name", infoNamesMaps);
		result.put("id", infoIdsMaps);
		result.put("pid", infoPIdsMaps);
		return result;
	}
	
	public List<CommonTreeNode> getLabelObjTree(String basePath,List<CommonTreeNode> objNodes,String labelObjNo){
		List<String> labelIds = new ArrayList<String>();
		List<CommonTreeNode> returnNodes = new ArrayList<CommonTreeNode>();
		Map<String,BioneLabelInfo> infoMaps = new HashMap<String, BioneLabelInfo>();
		BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
		String jql ="select info from BioneLabelInfo info where 1=1 and info.labelObjId = ?0";
		List<BioneLabelInfo> infos = this.baseDAO.findWithIndexParam(jql, obj.getLabelObjId());
		if(infos != null && infos.size()>0){
			for(BioneLabelInfo info : infos){
				infoMaps.put(info.getLabelId(), info);
			}
		}
		List<String> objIds = new ArrayList<String>();
		List<String> iobjIds = new ArrayList<String>();
		Map<String,CommonTreeNode> objMaps = new HashMap<String, CommonTreeNode>();
		if(objNodes!=null && objNodes.size()>0){
			for(CommonTreeNode node : objNodes){
				objIds.add(node.getParams().get("realId"));
				objMaps.put(node.getParams().get("realId"), node);
			}
		}
		jql = "select rel from BioneLabelObjRel rel where 1=1 and rel.id.labelObjId = ?0";
		List<BioneLabelObjRel> rels = this.baseDAO.findWithIndexParam(jql, obj.getLabelObjId());
		if(rels != null && rels.size()>0){
			for(BioneLabelObjRel rel : rels){
				if(objIds.contains(rel.getId().getObjId())){
					iobjIds.add(rel.getId().getObjId());
					CommonTreeNode newNode = new CommonTreeNode();
					BeanUtils.copy(objMaps.get(rel.getId().getObjId()), newNode);
					newNode.setUpId(rel.getId().getLabelId());
					newNode.setData(objMaps.get(rel.getId().getObjId()).getData());
					newNode.setChildren(objMaps.get(rel.getId().getObjId()).getChildren());
					if(!labelIds.contains(rel.getId().getLabelId())){
						BioneLabelInfo rinfo = infoMaps.get(rel.getId().getLabelId());
						if(rinfo != null){
							returnNodes.add(generateLabelNode(basePath,rinfo));
							labelIds.add(rinfo.getLabelId());
						}
						else{
							newNode.setUpId("nullLabel");
							
						}
						returnNodes.add(newNode);
						while(rinfo != null && !rinfo.getUpId().equals("0")){
							rinfo = infoMaps.get(rinfo.getUpId());
							if(!labelIds.contains(rinfo.getLabelId())){
								returnNodes.add(generateLabelNode(basePath,rinfo));
								labelIds.add(rinfo.getLabelId());
							}
							else{
								break;
							}
						}
					}
				}
			}
		}
		boolean nullLabel = false;
		if(objNodes!=null && objNodes.size()>0){
			for(CommonTreeNode node : objNodes){
				if(!iobjIds.contains(node.getParams().get("realId"))){
					nullLabel =true;
					node.setUpId("nullLabel");
					returnNodes.add(node);
				}
			}
		}
		if(nullLabel){
			CommonTreeNode node = new CommonTreeNode();
			node.setId("nullLabel");
			node.setText("（无标签）");
			node.setUpId("0");
			node.setIcon(basePath
					+ "/images/classics/icons/tag_blue.png");
			Map<String,String> paramMap = new HashMap<String, String>();
			paramMap.put("nodeType", "label");
			node.setParams(paramMap);
			returnNodes.add(0,node);
		}
		return returnNodes;
	}
	
	private CommonTreeNode generateLabelNode(String basePath,BioneLabelInfo info){
		CommonTreeNode node = new CommonTreeNode();
		node.setId(info.getLabelId());
		node.setText(info.getLabelName());
		node.setUpId(info.getUpId());
		node.setIcon(basePath
				+ "/images/classics/icons/tag_blue.png");
		Map<String,String> paramMap = new HashMap<String, String>();
		paramMap.put("nodeType", "label");
		node.setParams(paramMap);
		return node;
	}
	
	public List<BioneLabelInfo> getLabelByObjNo(String labelObjNo,String upId){
		BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
		String jql ="select info from BioneLabelInfo info where 1=1 and info.labelObjId = ?0 and info.upId =?1";
		return this.baseDAO.findWithIndexParam(jql, obj.getLabelObjId(),upId);
	}
	
	public List<String> getObjIdByObjNo(String labelObjNo){
		BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
		String jql ="select rel.id.objId from BioneLabelObjRel rel where 1=1 and rel.id.labelObjId = ?0 ";
		return this.baseDAO.findWithIndexParam(jql, obj.getLabelObjId());
	}
	
	public List<String> getObjIdByObjNo(String labelObjNo,List<String> labelIds){
		BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
		String jql ="select rel.id.objId from BioneLabelObjRel rel where 1=1 and rel.id.labelObjId = ?0 and rel.id.labelId in ?1";
		return this.baseDAO.findWithIndexParam(jql, obj.getLabelObjId(),labelIds);
	}
	
	@SuppressWarnings("static-access")
	public Map<String,List<String>> getIdxLabelMap(String labelObjNo,List<String> indexNos){
		Map<String,List<String>> idxLabelMap = new HashMap<String, List<String>>();
		Map<String,Object> params = new HashMap<String, Object>();
		BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", labelObjNo);
		params.put("labelObjId", obj.getLabelObjId());
		String jql ="select rel from BioneLabelObjRel rel where 1=1 and rel.id.labelObjId = :labelObjId ";
		if(indexNos != null && indexNos.size()>0 ){
			jql +=  " and ( ";
			List<List<?>> indexLists = this.splitLists(indexNos);
			int i = 0;
			for(List<?> indexList : indexLists){
				jql +=  " rel.id.objId in (:indexList"+i+") ";
				params.put("indexList"+i, indexList);
				if(i < indexLists.size()-1){
					jql += " or ";
				}
				else{
					jql += " ) ";
				}
				i++;
			}
		}
		List<BioneLabelObjRel> rels = this.baseDAO.findWithNameParm(jql, params);
		for(BioneLabelObjRel rel : rels){
			if(idxLabelMap.get(rel.getId().getObjId()) != null){
				idxLabelMap.get(rel.getId().getObjId()).add(rel.getId().getLabelId());
			}
			else{
				List<String> labelIds = new ArrayList<String>();
				labelIds.add(rel.getId().getLabelId());
				idxLabelMap.put(rel.getId().getObjId(), labelIds);
			}
		}
		return idxLabelMap;
	}
	
	public static List<List<?>> splitLists(List<?> lists){
		List<List<?>> newlists=new ArrayList<List<?>>();
		int index=lists.size()/1000;
		for(int i=0;i<=index;i++){
			newlists.add(lists.subList(i*1000, (i*1000+1000)>=lists.size()?lists.size():(i*1000+1000)));
		}
		return newlists;
	}
	
	/**
	 * 同步参数为标签
	 * @param paramId
	 */
	@Transactional(readOnly = false)
	public void configLabel(String paramValue, String paramName) {
		if(StringUtils.isNotBlank(paramValue)) {
			BioneLabelObjInfo obj = this.getEntityByProperty(BioneLabelObjInfo.class, "labelObjNo", "idx");
			if(null != obj) {
				BioneLabelInfo LabelInfo = new BioneLabelInfo();
				LabelInfo.setLabelId(paramValue);
				LabelInfo.setLabelName(paramName);
				LabelInfo.setUpId("0");
				LabelInfo.setLabelObjId(obj.getLabelObjId());
				this.saveOrUpdateEntity(LabelInfo);
			}
		}
	}

}
