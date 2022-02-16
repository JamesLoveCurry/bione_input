package com.yusys.bione.plugin.paramtmp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.mtool.entity.BioneDatasetInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.paramtmp.ParamModule;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpAttr;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpCatalog;
import com.yusys.bione.plugin.paramtmp.entity.RptParamtmpInfo;
import com.yusys.bione.plugin.paramtmp.repository.ParamtmpMybatisDao;
import com.yusys.bione.plugin.paramtmp.web.vo.RptParamtmpVO;

/**
 * 
 * <pre>
 * Title:报表参数模板业务类
 * Description: 提供对动态参数模板自定义配置
 * </pre>
 * 
 * @author fanll fanll@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
@Transactional(readOnly = true)
public class ParamTempBS extends BaseBS<Object>{

	
	@Autowired
	private ParamtmpMybatisDao paramtmpMybatisDao;
	
	@Autowired
	private CommonComboBoxBS boxBs;
	/**
	 * 参数模板目录树
	 * 
	 * @param upId
	 *            上级Id
	 * @param basePath
	 *            工程路径
	 * @return
	 */
	public List<CommonTreeNode> getDatasetCatalogTree(String upId,
			String basePath) {
		List<CommonTreeNode> nodes = Lists.newArrayList();
		if (!notEmpty(upId)) {
			// 创建根节点
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId("ROOT");
			treeNode.setText("参数模板");
			treeNode.setIcon(basePath + GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
			treeNode.setIsParent(true);
			Map<String, String> params = new HashMap<String, String>();
			params.put("realId", "0");
			params.put("isParent", "true");
			
			treeNode.setParams(params);
			nodes.add(treeNode);
		} else {
//			String jql = "select c from RptParamtmpCatalog c where c.upId=?0 ";
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("upId", upId);
			List<RptParamtmpCatalog> catalogs = this.paramtmpMybatisDao.findParamtmpCatalog(condition);
			if (notEmpty(catalogs)) {
				for (int i = 0; i < catalogs.size(); i++) {
					RptParamtmpCatalog cl = catalogs.get(i);
					CommonTreeNode treeNode = new CommonTreeNode();
					treeNode.setId(cl.getCatalogId());
					treeNode.setText(cl.getCatalogNm());
					treeNode.setIsParent(true);
					treeNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
					Map<String, String> params = new HashMap<String, String>();
					params.put("realId", cl.getCatalogId());
					params.put("isParent", "true");
					treeNode.setParams(params);
					nodes.add(treeNode);
				}
			}
		}
		return nodes;
	}

	/**
	 * 获取参数模板目录-参数模板树
	 * @param basePath
	 * @return
	 */
	public List<CommonTreeNode> getAyncTree(String basePath) {
		List<CommonTreeNode> nodes = Lists.newArrayList();
		
		/*CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId("ROOT");
		treeNode.setText("固定参数模板");
		treeNode.setIcon(basePath + DATA_TREE_NODE_ICON_ROOT);
		Map<String, String> params = new HashMap<String, String>();
		params.put("realId", "0");
		params.put("isParent", "true");
		treeNode.setParams(params);*/
		
//		nodes.add(treeNode);
		
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("orderName", "catalog_Nm");
//		String catalogsJql =  "select c from RptParamtmpCatalog c where 1=1 order by c.catalogNm";
		List<RptParamtmpCatalog> catalogs = this.paramtmpMybatisDao.findParamtmpCatalog(condition);
		
//		String tmpJql = "select info from RptParamtmpInfo info where 1=1 and info.templateType='custom' order by info.paramtmpNm";
		condition.clear();
		condition.put("templateType", "custom");
		condition.put("orderName", "paramtmpNm");
		List<RptParamtmpInfo> tmpList = this.paramtmpMybatisDao.findParamtmpInfo(condition);
		
		
		for(int i=0;i<catalogs.size();i++){
			CommonTreeNode catalogNode = new CommonTreeNode();
			catalogNode.setId(catalogs.get(i).getCatalogId());
			catalogNode.setText(catalogs.get(i).getCatalogNm());
			catalogNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_CATALOG);
			catalogNode.setUpId(catalogs.get(i).getUpId());
			Map<String, String> catalogMap = new HashMap<String, String>();
			catalogMap.put("type", "catalog");
			catalogNode.setParams(catalogMap);
			
			List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();
			for(int j=0;j<tmpList.size();j++){
				
				if(tmpList.get(j).getCatalogId().equals(catalogs.get(i).getCatalogId())){
					CommonTreeNode tmpNode = new CommonTreeNode();
					tmpNode.setId(tmpList.get(j).getParamtmpId());
					tmpNode.setUpId(tmpList.get(j).getCatalogId());
					tmpNode.setText(tmpList.get(j).getParamtmpNm());
					tmpNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_REPORT);
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put("type", "paramtmp");
					tmpNode.setParams(paramMap);
					childList.add(tmpNode);
				}
			}
			if(childList != null && childList.size()>0){
				catalogNode.setChildren(childList);
			}
			nodes.add(catalogNode);
			
		}
		
		
		List<CommonTreeNode> outList = boxBs.createTreeNode(nodes);
		return outList;
	}
	

	
	/**
	 * 判断同路径下是否已存在同名目录
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param upId
	 *            上级目录Id
	 * @param catalogName
	 *            目录名称
	 * @return
	 */
	public boolean catalogNameCanUse(String catalogId, String upId,
			String catalogName) {
//		String jql = "select cl from RptParamtmpCatalog cl where cl.upId=?0 and cl.catalogNm=?1 ";
//		if (notEmpty(catalogId)) {
//			jql += " and cl.catalogId<>'" + catalogId + "'";
//		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("upId", upId);
		map.put("catalogNm", catalogName);
		if (notEmpty(catalogId)) {
			map.put("catalogId", catalogId);
		}
		List<RptParamtmpCatalog> list = this.paramtmpMybatisDao.findCatalogNotId(map);
		return (notEmpty(list)) ? false : true;
	}

	/**
	 * 删除目录
	 * 
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean deleteCatalog(String catalogId) {
		if (!notEmpty(catalogId))
			return false;

		List<String> sonIds = new ArrayList<String>();
		sonIds.add(catalogId);
		// 获取所有子目录
		this.getSonsByType(catalogId, sonIds);

		// 删除前检查该目录下是否有配置参数模板
//		String jql = "select tp from RptParamtmpInfo tp where tp.catalogId in (?0) ";
//		List<RptParamtmpInfo> list = this.baseDAO.findWithIndexParam(jql,
//				sonIds);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("catalogIds", sonIds);
		List<RptParamtmpInfo> list = this.paramtmpMybatisDao.findParamtmpInfoInCatalog(map);
		if (notEmpty(list))
			return false;
//		jql = "delete from RptParamtmpCatalog cl where cl.catalogId in(?0) ";
//		this.baseDAO.batchExecuteWithIndexParam(jql, sonIds);
		map.clear();
		map.put("catalogId", sonIds);
		this.paramtmpMybatisDao.deleteCatalogInID(sonIds);
		return true;
	}

	/**
	 * 删除固定参数模板
	 * 
	 * @param paramtmpId
	 *            参数模板编号
	 * @param logicSysNo
	 * @return
	 */
	@Transactional(readOnly = false)
	public void deleteTemp(String paramtmpId, String logicSysNo) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("paramtmpId", paramtmpId);
		this.paramtmpMybatisDao.deleteParamtmpInfo(map);
		
		map.clear();
		map.put("paramtmpId", paramtmpId);
		this.paramtmpMybatisDao.deleteParamtmpAttr(map);
	}

	/**
	 * 递归查找目录下的所有子目录
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param sonIds
	 *            子目录Id集合
	 */
	private void getSonsByType(String catalogId, List<String> sonIds) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("upId", catalogId);
		List<RptParamtmpCatalog> children = this.paramtmpMybatisDao.findParamtmpCatalog(map);
		if (notEmpty(children)) {
			for (RptParamtmpCatalog cl : children) {
				sonIds.add(cl.getCatalogId());
				this.getSonsByType(cl.getCatalogId(), sonIds);
			}
		}
	}

	/**
	 * 获取目录下的参数模板列表
	 * 
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @param catalogId
	 *            目录Id
	 * @return 目录下的参数模板集合
	 */
	public SearchResult<RptParamtmpInfo> getParamTemps(Pager pager, String catalogId) {
		if ("".equals(catalogId)) {
			return null;
		}
		
//		Map<String, SearchFilter> searchFilter = pager.getSearchFilters();
//		SearchFilter filter = new SearchFilter("catalogId", Operator.EQ, catalogId);
//		searchFilter.put("catalogId", filter);
//		Page<RptParamtmpInfo> list =
//		this.ParamtmpDao.findAll(this.buildSpecification(searchFilter),
//		pager.getPageRequest());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("catalogId", catalogId);
		PageHelper.startPage(pager);
		PageMyBatis<RptParamtmpInfo> page = (PageMyBatis<RptParamtmpInfo>) this.paramtmpMybatisDao.findParamtmpInfo(map);
		SearchResult<RptParamtmpInfo> results = new SearchResult<RptParamtmpInfo>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		return results;
		 // 可以直接返回Page,以下代码是为了适应老的controller
//		SearchResult<RptParamtmpInfo> results = new
//		SearchResult<RptParamtmpInfo>();
//		results.setTotalCount(list.getTotalElements());
//		results.setResult(list.getContent());
//
//		return results;
	}

	/**
	 * 判断同路径下是否已存在同名模板
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param paramtmpName
	 *            模板名称
	 * @return
	 */
	public boolean tmpNameCanUse(String catalogId, String paramtmpName,
			String paramtmpId, String logicSysNo) {
//		String jql = "select t from RptParamtmpInfo t where t.catalogId=?0 and t.paramtmpNm=?1 ";
//		if (notEmpty(paramtmpId)) {
//			jql += " and t.paramtmpId<>'" + paramtmpId + "'";
//		}
		Map<String, String> map = new HashMap<String, String>();
		map.put("catalogId", catalogId);
		map.put("paramtmpNm", paramtmpName);
		if (notEmpty(paramtmpId)) {
			map.put("paramtmpId", paramtmpId);
		}
		List<RptParamtmpInfo> list = this.paramtmpMybatisDao.findParamtmpInfoNotId(map);
		return (notEmpty(list)) ? false : true;
	}

	/**
	 * 保存配置完成的参数模板
	 * 
	 * @param temp
	 *            参数模板
	 * @param paramsJsonStr
	 *            参数项集合json
	 */
	@Transactional(readOnly = false)
	public void save(RptParamtmpInfo temp, String paramsJsonStr) {
		if (temp.getParamtmpId() == null || temp.getParamtmpId().equals("")) {// 新增
			temp.setParamtmpId(RandomUtils.uuid2());
			this.paramtmpMybatisDao.saveParamtmpInfo(temp);
		} else {// 修改
			Map<String,String> map = new HashMap<String, String>();
			map.put("paramtmpId", temp.getParamtmpId());
			this.paramtmpMybatisDao.deleteParamtmpAttr(map);
//			this.paramtmpMybatisDao.updateParamtmpInfo(temp);
			this.baseDAO.merge(temp);
		}
		String paramtmpId = temp.getParamtmpId();
//		if (!notEmpty(paramtmpId)) {
//			paramtmpId = RandomUtils.uuid2();
//			temp.setParamtmpId(paramtmpId);
//		}
//		this.saveOrUpdateEntity(temp);
//		this.paramtmpMybatisDao.saveParamtmpInfo(temp);

		JSONArray queryArray = JSON.parseArray(paramsJsonStr);
		for (int i = 0; i < queryArray.size(); i++) {

			String paramId = RandomUtils.uuid2();

			//修改数据源
			JSONObject object = queryArray.getJSONObject(i);
			if (object.get("datasource") != null) {

				//String sTmp = object.get("datasource").toString();
				//String a = "\"" + sTmp + "\"";
				//object.remove("datasource");
				//object.put("datasource", a);
				// 保存数据源的配置信息
				// RptParamtmpSql entity = new RptParamtmpSql();
				// entity.setParamId(paramId);
				// entity.setParamSql(sTmp);
				// this.baseDAO.save(entity);
			}
			if (object.get("validate") != null) {
				String validate = object.getString("validate");
				validate = "\"" + validate + "\"";
				object.put("validate", validate);
			}
/*			if(object.get("disabled") != null){
				String disabled = object.getString("disabled");
				if(disabled.equals("\"null\"")){
					object.put("disabled", "null");
				}
			}*/
			String value = object.toString();
			//替换null
			value = StringUtils.replace(value, "\\\"null\\\"", "null");
			
			RptParamtmpAttr attrs = new RptParamtmpAttr();

			attrs.setParamId(paramId);
			attrs.setParamtmpId(paramtmpId);
			attrs.setParamType("01");
			attrs.setParamVal(value);
			attrs.setOrderNum(new BigDecimal(i + 1));
			
			this.paramtmpMybatisDao.saveParamtmpAttr(attrs);
			// 保存
		}
	}

	
	/**
	 * 替换空间参数中SQL语句有形如#{}的字符串
	 * @param paramJson
	 * @return
	 */
	public String replace(String paramJson) {

		Pattern pattern = Pattern.compile("\\#\\{(\\w+)\\}");
		Matcher matcher = pattern.matcher(paramJson);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			if (ParamModule.formString(matcher.group(1)) != null) {
				matcher.appendReplacement(sb, StringUtils.defaultString(ParamModule.formString(matcher.group(1)).apply(), ""));
			}
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	/**
	 * 得到ParamModule里面数据的名称
	 * @return
	 */
	public String getText() {
		List<CommonComboBoxNode> list = Lists.newArrayList();
		for (ParamModule p : ParamModule.values()) {
			CommonComboBoxNode node = new CommonComboBoxNode();
			node.setId(p.name());
			node.setText(p.toChinese());
			list.add(node);
		}
		return JSON.toJSONString(list);
	}


	/**
	 * 获取参数模板信息
	 * 
	 * @param paramtmpId
	 *            模板Id
	 * @return
	 */
	public String getParamtmpJson(String paramtmpId, String catalogId) {
		RptParamtmpInfo temp = new RptParamtmpInfo();
		temp.setCatalogId(catalogId);
		if (notEmpty(paramtmpId)) {
			RptParamtmpInfo oldTemp = this.getUniqueParamtmpById(
					paramtmpId);
			if (oldTemp != null) {
				temp = oldTemp;
			}
		}
		return JSON.toJSONString(temp);
	}

	/**
	 * 获取参数信息,返回JSON格式的字符串
	 * 
	 * @param paramtmpId
	 *            模板Id
	 * @return
	 */
	public List<RptParamtmpAttr> getParamsJson(String paramtmpId) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("paramtmpId", paramtmpId);
//		String jql = "select attr from RptParamtmpAttr attr where attr.paramtmpId=?0  order by orderNum";
		List<RptParamtmpAttr> attrs = this.paramtmpMybatisDao.findParamtmpAttr(map);
		return attrs;
	}

	/**
	 * 分页查询系统参数类型
	 * 
	 * @param firstResult
	 *            第一条数据索引
	 * @param pageSize
	 *            每页显示条数
	 * @param orderBy
	 *            排序列
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            参数列表
	 * @return 查询结果集
	 */
	public Map<String, Object> getSysParams(Pager pager) {
//		String currentLogicNo = BioneSecurityUtils.getCurrentUserInfo()
//				.getCurrentLogicSysNo();
//		PageHelper.startPage(pager);
//		PageMyBatis<BioneParamTypeInfo> page = (PageMyBatis<BioneParamTypeInfo>) this.paramtmpMybatisDao.findParamTypeInfoPage();
//		SearchResult<BioneParamTypeInfo> results = new SearchResult<BioneParamTypeInfo>();
//		results.setTotalCount(page.getTotalCount());
//		results.setResult(page.getResult());
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("Rows", results.getResult());
//		map.put("Total", results.getTotalCount());
//		return map;
		return null;
	}

	/**
	 * 分页查询系统数据集
	 * 
	 * @param firstResult
	 *            第一条数据索引
	 * @param pageSize
	 *            每页显示条数
	 * @param orderBy
	 *            排序列
	 * @param orderType
	 *            排序方式
	 * @param conditionMap
	 *            参数列表
	 * @return 查询结果集
	 */
	public Map<String, Object> getDatasets(Pager pager) {
//		String currentLogicNo = BioneSecurityUtils.getCurrentUserInfo()
//				.getCurrentLogicSysNo();
//		StringBuilder jql = new StringBuilder(
//				"select p from BioneDatasetInfo p where p.logicSysNo='"
//						+ currentLogicNo + "'");
//		if (!conditionMap.get("jql").equals("")) {
//			jql.append(" and " + conditionMap.get("jql"));
//		}
//		if (!StringUtils.isEmpty(orderBy)) {
//			jql.append(" order by p." + orderBy + " " + orderType + " ");
//		}
//		Map<String, ?> values = (Map<String, ?>) conditionMap.get("params");
//		SearchResult<BioneDatasetInfo> searchResult = this.baseDAO
//				.findPageWithNameParam(firstResult, pageSize, jql.toString(),
//						values);
		PageHelper.startPage(pager);
		PageMyBatis<BioneDatasetInfo> page = (PageMyBatis<BioneDatasetInfo>) this.paramtmpMybatisDao.findDatasetInfo(new HashMap<String, String>());
		SearchResult<BioneDatasetInfo> results = new SearchResult<BioneDatasetInfo>();
		results.setTotalCount(page.getTotalCount());
		results.setResult(page.getResult());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Rows", results.getResult());
		map.put("Total", results.getTotalCount());
		return map;
	}

	// 非空判断
	private boolean notEmpty(String temp) {
		if (temp == null || temp.trim().length() == 0)
			return false;
		else
			return true;
	}

	private boolean notEmpty(List<?> temp) {
		if (temp == null || temp.size() == 0)
			return false;
		else
			return true;
	}
	/**
	 * 根据模板ID和类型来显示参数模板
	 * @param id 模板ID
	 * @param type edit 修改； view 预览
	 * @return
	 */
	public RptParamtmpVO show(String id, String type) {
		List<RptParamtmpAttr> listAttr = this.getParamsJson(id);
		List<String> string = new ArrayList<String>();
		if (type != null && type.equals("edit")) {
			for (int i = 0; i < listAttr.size(); i++) {
				string.add(listAttr.get(i).getParamVal());
			}
		} else {
			string = this.changeOption(listAttr);
		}
		String paramJson = string.toString();
		paramJson = StringUtils.replace(paramJson, "\\\"null\\\"", "null");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("paramtmpId", id);
		RptParamtmpInfo tmp = this.paramtmpMybatisDao.findParamtmpInfo(map).get(0);
		
		RptParamtmpVO vo = new RptParamtmpVO();
		if(tmp == null)
		{
			return null;
		}
		//预览时，将"#{user_id}"、"#{dept_no}"、"#{org_no}"替换成其实际值
		if(type.equals("view")){
			if(paramJson != null){
				paramJson = replace(paramJson);
			}
		}
		vo.setCatalogId(tmp.getCatalogId());
		vo.setParamJson(paramJson);
		vo.setParamtmpId(tmp.getParamtmpId());
		vo.setParamtmpName(tmp.getParamtmpNm());
		vo.setRemark(tmp.getRemark());
		vo.setTemplateType(tmp.getTemplateType());
		vo.setTemplateUrl(tmp.getTemplateUrl());
		return vo;
	}
	/**
	 * 预览时将下拉框或者树里的SQL语句转换成URL
	 * @param listParam
	 * @return
	 */
	private List<String> changeOption(List<RptParamtmpAttr> listParam) {
		List<String> newList = new ArrayList<String>();
		for (int i = 0; i < listParam.size(); i++) {
			String param = listParam.get(i).getParamVal();
			JSONObject queryObject = JSON.parseObject(param);
			String paramType = queryObject.getString("type");
			String paramId = listParam.get(i).getParamId();
			if (queryObject.get("datasource") != null) {
				Object datasource = queryObject.get("datasource");
				JSONObject option = JSON.parseObject(datasource.toString());
				JSONObject db = (JSONObject)JSON.toJSON(option.get("options"));
				if (db.get("sql") != null) {
					db.remove("sql");
					db.remove("db");
					if (paramType.equals("combobox")) {// 构造树url
						db.put("url", "/report/frame/param/commonComboBox/executeTreeSQL.json/"
										+ paramId);
					} else if (paramType.equals("popup")){
						db.put("url", "/report/frame/param/commonComboBox/executePopupSQL.json/"
										+ paramId);
					}else{
						db.put("url",
								"/report/frame/param/commonComboBox/executeSelectSQL.json/"
										+ paramId);
					}
					
					db.put("ajaxType", "POST");
					option.put("options", db.toString());
					queryObject.put("datasource", option.toString());
				}
			}
			newList.add(i, queryObject.toString());

		}
		return newList;
	}
	
	/**
	 * 查找指定模版ID的已关联的报表数量
	 * @param paramtmpIds
	 * @param logicSysNo
	 * @return
	 */
	public Object getRelReport(List<String> paramtmpIds, String logicSysNo){
//		String jql = "select count(report) from RptMgrReportInfo report where report.paramtmpId in ( ?0 ) ";
		Object num = "0";
//		num = this.baseDAO.findUniqueWithIndexParam(jql, paramtmpIds);
		return num;
	}
	
	/**
	 * 重置指定模版ID所关联报表的的参数模版项为空
	 * @param paramtmpIds
	 * @param logicSysNo
	 */
	@Transactional(readOnly = false)
	public void updateRptParamtmpId(List<String> paramtmpIds, String logicSysNo){
		if(notEmpty(paramtmpIds)){
			for(int i=0;i<paramtmpIds.size();i++){
				
//				String jql = "update RptMgrReportInfo report set report.paramtmpId = ?0 where report.paramtmpId in ( ?1 ) and report.logicSysNo = ?2";
//				this.baseDAO.batchExecuteWithIndexParam(jql, "", paramtmpIds, logicSysNo);
			}
		}
	}
	
	/**
	 * 获取参数模版目录结构    包括  参数模版节点
	 * @param basePath
	 * @return
	 */
	public List<CommonTreeNode> getParamtmpTree(String upId, String basePath) {
		List<CommonTreeNode> nodes = Lists.newArrayList();
		if (!notEmpty(upId)) {
			// 创建根节点
			CommonTreeNode newNode = new CommonTreeNode();
			newNode.setText("空参数模板");
			newNode.setIcon(basePath + GlobalConstants4plugin.TREE_ICON_ROOT);
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("isParent", "false");
			newNode.setParams(paramMap);
			nodes.add(newNode);
			
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId("ROOT");
			treeNode.setText("参数模版目录");
			treeNode.setIcon(basePath + GlobalConstants4plugin.TREE_ICON_ROOT);
			Map<String, String> params = new HashMap<String, String>();
			params.put("realId", "0");
			params.put("isParent", "true");
			treeNode.setParams(params);
			nodes.add(treeNode);
		}else{
			this.getParamtmpNode(upId, basePath, nodes);
		}
		return nodes;
	}


	/**
	 * 获取参数模版目录结构    包括  参数模版节点
	 * @param basePath
	 * @return
	 */
	/**
	 * 获取参数模版 子节点  包括 目录和参数
	 * @param upId
	 * @param basePath
	 * @param nodes
	 */
	private void getParamtmpNode(String catalogId, String basePath,
			List<CommonTreeNode> nodes) {
//		String jql = "SELECT c FROM RptParamtmpCatalog c WHERE c.upId=?0";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("upId", catalogId);
		List<RptParamtmpCatalog> catalogs = this.paramtmpMybatisDao.findParamtmpCatalog(map);
		if (notEmpty(catalogs)) {
			for (int i = 0; i < catalogs.size(); i++) {
				RptParamtmpCatalog cl = catalogs.get(i);
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(cl.getCatalogId());
				treeNode.setText(cl.getCatalogNm());
				treeNode.setIcon(basePath + GlobalConstants4plugin.PARAMTMP_CATALOG_ICON);
				Map<String, String> params = new HashMap<String, String>();
				params.put("realId", cl.getCatalogId());
				params.put("isParent", "true");
				treeNode.setParams(params);
				nodes.add(treeNode);
			}
		}
		
//		jql = "SELECT ri FROM RptParamtmpInfo ri WHERE ri.catalogId=?0";
		map.clear();
		map.put("catalogId", catalogId);
		List<RptParamtmpInfo> reportInfo = this.paramtmpMybatisDao.findParamtmpInfo(map);
		if (notEmpty(reportInfo)) {
			CommonTreeNode treeNode = null;
			for (int i = 0; i < reportInfo.size(); i++) {
				RptParamtmpInfo ri = reportInfo.get(i);
				treeNode = new CommonTreeNode();
				treeNode.setId(ri.getParamtmpId());
				treeNode.setText(ri.getParamtmpNm());
				treeNode.setIcon(basePath + com.yusys.bione.plugin.base.common.GlobalConstants4plugin.PARAMTMP_ICON);
				Map<String, String> params = new HashMap<String, String>();
				params.put("realId", ri.getParamtmpId());
				params.put("isParent", "false");
				treeNode.setParams(params);
				nodes.add(treeNode);
			}
		}
	}

	public Map<String,Object> getParamGird(String paramTmpId){
		Map<String,Object> result = new HashMap<String, Object>();
		String jql = "select attr from RptParamtmpAttr attr where attr.paramtmpId = ?0";
		List<RptParamtmpAttr> attrs = this.baseDAO.findWithIndexParam(jql, paramTmpId);
		List<Map<String,Object>> rows = new ArrayList<Map<String,Object>>();
		if(attrs != null && attrs.size() > 0){
			for(RptParamtmpAttr attr : attrs){
				Map<String,Object> row = new HashMap<String, Object>();
				String json = attr.getParamVal();
				JSONObject valMap = JSON.parseObject(json);
				row.put("id", valMap.get("name"));
				row.put("text", valMap.get("display"));
				rows.add(row);
			}
		}
		result.put("Rows", rows);
		return result;
	}
	public RptParamtmpInfo getUniqueParamtmpById(String paramtmpId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("paramtmpId", paramtmpId);
		List<RptParamtmpInfo> list = this.paramtmpMybatisDao.findParamtmpInfo(map);
		if(list != null && list.size() == 1)
			return list.get(0);
		return null;
	}
	public RptParamtmpCatalog getUniqueParamtmpCatalogById(String catalogId){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("catalogId", catalogId);
		List<RptParamtmpCatalog> list = this.paramtmpMybatisDao.findParamtmpCatalog(map);
		if(list != null && list.size() == 1)
			return list.get(0);
		return null;
	}
	@Transactional(readOnly = false)
	public void saveParamtmpInfo(RptParamtmpInfo info) {
		paramtmpMybatisDao.saveParamtmpInfo(info);
	}
	@Transactional(readOnly = false)
	public void updateParamtmpInfo(RptParamtmpInfo info) {
		paramtmpMybatisDao.updateParamtmpInfo(info);
	}

	@Transactional(readOnly = false)
	public void saveParamtmpAttr(RptParamtmpAttr info) {
		paramtmpMybatisDao.saveParamtmpAttr(info);
	}
	@Transactional(readOnly = false)
	public void updateParamtmpAttr(RptParamtmpAttr info) {
		paramtmpMybatisDao.updateParamtmpAttr(info);
	}
	@Transactional(readOnly = false)
	public void saveParamtmpCatalog(RptParamtmpCatalog info) {
		paramtmpMybatisDao.saveParamtmpCatalog(info);
	}
	@Transactional(readOnly = false)
	public void updateParamtmpCatalog(RptParamtmpCatalog info) {
		paramtmpMybatisDao.updateParamtmpCatalog(info);
	}
}
