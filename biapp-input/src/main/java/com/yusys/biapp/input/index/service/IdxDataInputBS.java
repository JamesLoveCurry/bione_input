package com.yusys.biapp.input.index.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.greenpineyu.fel.common.StringUtils;
import com.yusys.biapp.input.index.entity.RptIdxPlanResult;
import com.yusys.biapp.input.index.entity.RptIdxPlanResultPK;
import com.yusys.biapp.input.index.entity.RptIdxResult;
import com.yusys.biapp.input.index.entity.RptIdxResultPK;
import com.yusys.biapp.input.index.entity.RptInputCatalog;
import com.yusys.biapp.input.index.entity.RptInputIdxCfgDetail;
import com.yusys.biapp.input.index.entity.RptInputIdxCfgOrg;
import com.yusys.biapp.input.index.entity.RptInputIdxCfgOrgPK;
import com.yusys.biapp.input.index.entity.RptInputIdxCfgValidate;
import com.yusys.biapp.input.index.entity.RptInputIdxCfgValidatePK;
import com.yusys.biapp.input.index.entity.RptInputIdxDimFilter;
import com.yusys.biapp.input.index.entity.RptInputIdxDimFilterPK;
import com.yusys.biapp.input.index.entity.RptInputIdxTemplate;
import com.yusys.biapp.input.index.entity.RptInputIdxUpdlog;
import com.yusys.biapp.input.index.entity.RptInputIdxUpdlogPK;
import com.yusys.biapp.input.index.entity.RptInputIdxValidate;
import com.yusys.biapp.input.index.repository.IdxDataInputDao;
import com.yusys.biapp.input.index.util.JdbcUtils;
import com.yusys.biapp.input.index.util.TaskXlsExcelExportUtil;
import com.yusys.biapp.input.index.util.vo.DsInfoVO;
import com.yusys.biapp.input.index.util.vo.TableColumnVO;
import com.yusys.biapp.input.index.util.vo.TableGridVO;
import com.yusys.biapp.input.index.web.vo.DataInputDimVO;
import com.yusys.biapp.input.index.web.vo.DataInputExportTableVO;
import com.yusys.biapp.input.index.web.vo.DataInputTaskVO;
import com.yusys.biapp.input.index.web.vo.DataInputTemplateVO;
import com.yusys.biapp.input.index.web.vo.DataInputVO;
import com.yusys.biapp.input.index.web.vo.DimFilterVO;
import com.yusys.biapp.input.index.web.vo.DimRowVO;
import com.yusys.biapp.input.index.web.vo.IdxCfgDetailVO;
import com.yusys.biapp.input.index.web.vo.TemplateVO;
import com.yusys.biapp.input.task.repository.TaskDao;
import com.yusys.biapp.input.utils.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jdbc.JDBCBaseDAO;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.message.messager.BioneMessageException;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.plugin.access.repository.RptAccessOrgDao;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.repository.RptTmpDataDao;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.service.RptDimBS;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.repository.IdxInfoMybatisDao;

@Service
@Transactional(readOnly = true)
public class IdxDataInputBS {

	@Autowired
	private RptTmpDataDao rptTmpDataDAO;
	@Autowired
	public IdxDataInputDao idxDataInputDao;
	@Autowired
	private RptAccessOrgDao orgDao;
	@Autowired
	private TaskDao taskDao;
	@Autowired
	private RptDimBS rptDimBS;
	@Autowired
	private IdxInfoMybatisDao idxDao;
	@Autowired
	protected JDBCBaseDAO jdbcBaseDAO;
	// @Autowired
	// private RptIdxPlanResultBS rptIdxPlanResultBS;
	// @Autowired
	// private RptIdxResultBS rptIdxResultBS;

	public static final String DEFAULT_ROOT = "ROOT";
	public static final String CATALOG_TYPE = "catalog";
	public static final String TEMPLATE_TYPE = "template";

	/**
	 * 创建目录
	 * 
	 * @param catalogNm
	 * @param remark
	 * @param upNo
	 */
	public void createTskCatalog(String catalogId, String catalogNm,
			String remark, String upNo) {

		RptInputCatalog catalog = new RptInputCatalog();
		boolean isUpd = true;
		if (catalogId == null || catalogId.equals("")) {
			catalogId = RandomUtils.uuid2();
			isUpd = false;
		}
		catalog.setCatalogId(catalogId);
		catalog.setCatalogNm(catalogNm);
		catalog.setRemark(remark);
		catalog.setCatalogType("01");
		catalog.setUpNo(upNo);
		if (!isUpd)
			saveDataInputCatalog(catalog);
		else
			updateRptTskCatalog(catalog);

	}

	/**
	 * 创建目录
	 * 
	 * @param catalog
	 */
	public void saveDataInputCatalog(RptInputCatalog catalog) {
		idxDataInputDao.saveDataInputCatalog(catalog);
	}

	/**
	 * 更新目录
	 * 
	 * @param catalog
	 */
	public void updateRptTskCatalog(RptInputCatalog catalog) {
		idxDataInputDao.updateDataInputCatalog(catalog);
	}

	/**
	 * 得到所有的目录信息
	 * 
	 * @return
	 */
	public List<RptInputCatalog> getDataInputCatalog(String parentCatalogId,
			String catalogId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("parentCatalogId", parentCatalogId);
		map.put("catalogId", catalogId);

		return idxDataInputDao.getDataInputCatalog(map);
	}

	public void deleteDataInputCatalog(List<String> catalogList) {
		idxDataInputDao.deleteDataInputCatalog(catalogList);
	}

	/**
	 * 根据条件查询目录下的任务信息
	 * 
	 * @param catalogId
	 * @return
	 */
	public List<RptInputIdxTemplate> getTemplateInfos(String catalogId,
			String templateInfoId, String templateInfoNm) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("catalogId", catalogId);
		map.put("templateId", templateInfoId);
		map.put("templateNm", templateInfoNm);

		return idxDataInputDao.getTemplateInfos(map);
	}

	public TemplateVO getTemplateVOs(String templateId) {
		TemplateVO vo = new TemplateVO();
		List<RptInputIdxTemplate> templateList = getTemplateInfos(null,
				templateId, null);
		if (templateList != null && templateList.size() == 1) {
			vo.setTemplate(templateList.get(0));
			vo.setDetailList(getDetailListByTemplateId(templateId));
		}

		return vo;
	}

	private List<IdxCfgDetailVO> getDetailListByTemplateId(String templateId) {
		List<IdxCfgDetailVO> detailList = idxDataInputDao
				.getRptInputIdxCfgDetailByTemplateId(templateId);
		if (detailList != null && !detailList.isEmpty()) {
			for (IdxCfgDetailVO detail : detailList) {
				appendCfgOrg(detail, detail.getCfgId());

				List<RptInputIdxDimFilter> filterList = idxDataInputDao
						.getRptInputIdxDimFilterByCfgId(detail.getCfgId());
				if (filterList != null && !filterList.isEmpty()) {
					List<DimFilterVO> filterVOList = Lists.newArrayList();
					for (RptInputIdxDimFilter filter : filterList) {
						DimFilterVO filterVO = new DimFilterVO();
						BeanUtils.copy(filter, filterVO);
						filterVO.setDimNo(filter.getId().getDimNo());
						filterVO.setIdxNo(filter.getId().getIndexNo());
						filterVO.setFilterText(filter.getFilterExplain());
						filterVO.setDimNm(filter.getDimNm());
						filterVOList.add(filterVO);
					}
					detail.setDimFilterInfos(filterVOList);

				}
			}
			return detailList;
		}

		return null;

	}

	private void appendCfgOrg(IdxCfgDetailVO vo, String cfgId) {
		List<RptInputIdxCfgOrg> cfgOrgList = idxDataInputDao
				.getRptInputIdxCfgOrgByCfgId(cfgId);
		StringBuilder orgNoBuff = new StringBuilder();
		StringBuilder orgNmBuff = new StringBuilder();
		boolean isFirst = true;
		for (RptInputIdxCfgOrg cfgOrg : cfgOrgList) {
			if (!isFirst) {
				orgNoBuff.append(",");
				orgNmBuff.append(",");
			}
			orgNoBuff.append(cfgOrg.getId().getOrgNo());
			orgNmBuff.append(cfgOrg.getOrgNm());
			isFirst = false;
		}

		vo.setOrgNo(orgNoBuff.toString());
		vo.setOrgNm(orgNmBuff.toString());
	}

	public List<CommonTreeNode> getRootRptTskInfoNode(String basePath) {

		return getTaskNode(basePath, null, null);

	}

	public List<CommonTreeNode> getRptTskInfoNode(String basePath,
			String searchNm, String nodeId) {
		return getTaskNode(basePath, nodeId, searchNm);
	}

	/**
	 * 查询目录下的任务节点信息
	 * 
	 * @param basePath
	 * @param catalogId
	 * @return
	 */
	private List<CommonTreeNode> getTaskNode(String basePath, String catalogId,
			String taskName) {
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		if (catalogId == null)
			appendRootTreeNode(basePath, nodeList);
		else {
			// 添加子目录节点
			nodeList.addAll(getCatalogTreeNode(basePath, catalogId, null));
			// 添加子任务节点
			Map<String, Object> map = Maps.newHashMap();
			map.put("taskName", taskName);
			map.put("catalogId", catalogId);
			List<DataInputTemplateVO> dataInputTemplateList = idxDataInputDao
					.getShowTemplateInfos(map);
			for (DataInputTemplateVO dataInputTemplateVO : dataInputTemplateList) {
				CommonTreeNode treeNode = new CommonTreeNode();
				treeNode.setId(dataInputTemplateVO.getTemplateId());
				treeNode.setText(dataInputTemplateVO.getTemplateNm());
				treeNode.setIsParent(false);
				treeNode.getParams().put("ralateToTask", dataInputTemplateVO.getRalateToTask());
				treeNode.getParams().put("nodeType", TEMPLATE_TYPE);
				treeNode.setIcon(basePath
						+ "/images/classics/menuicons/grid.png");
				nodeList.add(treeNode);
			}
		}
		return nodeList;
	}

	private void appendRootTreeNode(String basePath,
			List<CommonTreeNode> nodeList) {

		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId("ROOT");
		treeNode.setText("根目录");
		treeNode.setIsParent(true);
		treeNode.setUpId(null);
		treeNode.setIsexpand(true);
		treeNode.getParams().put("nodeType", "catalog");
		treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
		nodeList.add(treeNode);

	}

	/**
	 * 查询目录节点信息
	 * 
	 * @param basePath
	 * @return
	 */
	public List<CommonTreeNode> getCatalogTreeNode(String basePath,
			String parentCatalogId, String catalogName) {
		List<CommonTreeNode> nodeList = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		map.put("catalogName", catalogName);
		map.put("parentCatalogId", parentCatalogId);
		List<RptInputCatalog> catalogList = idxDataInputDao
				.getDataInputCatalog(map);
		for (RptInputCatalog catalog : catalogList) {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(catalog.getCatalogId());
			treeNode.setText(catalog.getCatalogNm());
			treeNode.setIsParent(true);
			treeNode.setUpId(catalog.getUpNo());
			treeNode.setIsexpand(false);
			treeNode.getParams().put("nodeType", "catalog");
			treeNode.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
			nodeList.add(treeNode);
		}

		return nodeList;
	}

	public RptInputCatalog getRptTskCatalogByCatalogId(String catalogId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("catalogId", catalogId);
		List<RptInputCatalog> catalogList = idxDataInputDao
				.getDataInputCatalog(params);
		if (catalogList != null && !catalogList.isEmpty())
			return catalogList.get(0);
		return null;
	}

	public boolean testSameIndexCatalogNm(String upNo, String catalogName,
			String catalogId) {

		Map<String, Object> params = Maps.newHashMap();
		params.put("upNo", upNo);
		params.put("catalogName", catalogName);
		params.put("catalogId", catalogId);

		return idxDataInputDao.testSameIndexCatalogNm(params) == 0;

	}

	public boolean testTemplateNm(String catalogId, String templateId,
			String templateNm) {

		Map<String, Object> params = Maps.newHashMap();
		if (catalogId == null)
			catalogId = "ROOT";
		params.put("catalogId", catalogId);
		params.put("templateId", templateId);
		params.put("templateNm", templateNm);

		return idxDataInputDao.testTemplateNm(params) == 0;

	}
	
	
	public Map<String,Object> getCheckVal(String cfgId) {
		//RPT_INPUT_IDX_VALIDATE
		try{
			Map<String,Object>map = Maps.newHashMap();
			RptInputIdxValidate rptInputIdxValidate = this.idxDataInputDao.getRuleByCfg(cfgId);
			double value ;
			if(rptInputIdxValidate.getRuleType().equals("1")){
				//sql
				value = getValueBySql(rptInputIdxValidate.getSourceId(),rptInputIdxValidate.getSqlExpression());
			}else{
				//数值
				value = rptInputIdxValidate.getRuleVal().doubleValue();
			}
			map.put("value", value);
			map.put("ruleType", rptInputIdxValidate.getRuleType());
			/*
			if(rptInputIdxValidate.equals("1"))
				return value>targetValue;
			if(rptInputIdxValidate.equals("2"))
				return value>=targetValue;
			if(rptInputIdxValidate.equals("3"))
				return value<targetValue;
			if(rptInputIdxValidate.equals("4"))
				return value<=targetValue;
			return value==targetValue;
		*/
			return map;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	
	private double getValueBySql(String sourceId,String sqlExpression) throws BioneMessageException{
		DsInfoVO   info  = idxDataInputDao.getDataSourceById(sourceId);
		JdbcUtils util = new JdbcUtils(sqlExpression,info);
		try{
			 List<Map<String, Object>> rsList = util.executeQuerySql();
			 if(rsList==null||rsList.isEmpty()||rsList.size()!=1)
				 throw new BioneMessageException("规则数据错误");

			 Object obj =  (rsList.get(0).values().toArray())[0];
			 if(obj instanceof Integer){
				 return ((Integer)obj).intValue();
			 }else if(obj instanceof Double){
				 return ((Double)obj).doubleValue();
			 }else if(obj instanceof BigDecimal){
				 return ((BigDecimal)obj).doubleValue();
			 }else if(obj instanceof String){
				 return Double.parseDouble((String)obj);
			 }
		}catch(Exception ex){
			 throw new BioneMessageException("读取数据库信息出错:"+ex.getMessage());
		}
		 return 0;
	}

	/**
	 * 遍历目录及目录下的子节点,将所有节点下的任务数量加和,判断是否可删除 0 可删除 1 不可删除
	 * 
	 * @param catalogId
	 * @return
	 */
	public String canDeleteCatalog(String catalogId) {
		boolean canDel = true;
		canDel = countCatalogTaskContainChild(catalogId, canDel);
		return canDel ? "0" : "1";
	}

	/**
	 * 将所有子节点下面的任务数量加和
	 * 
	 * @param catalogId
	 */
	private boolean countCatalogTaskContainChild(String catalogId,
			boolean canDel) {

		if (!canDel)
			return canDel;
		Map<String, Object> params = Maps.newHashMap();
		params.put("catalogId", catalogId);
		Integer cnt = this.idxDataInputDao.getTemplateCount(params);
		int catalogCnt = cnt == null ? 0 : cnt.intValue();
		if (catalogCnt != 0) {
			canDel = false;
			return canDel;
		}
		Map<String, Object> map = Maps.newHashMap();
		map.put("parentCatalogId", catalogId);
		List<RptInputCatalog> logList = idxDataInputDao
				.getDataInputCatalog(map);
		for (RptInputCatalog log : logList) {
			canDel = countCatalogTaskContainChild(log.getCatalogId(), canDel);
			if (!canDel)
				return canDel;
		}
		return canDel;
	}

	/**
	 * 遍历目录,将目录删除
	 * 
	 * @param id
	 */
	public void batchDelCatalog(String catalogId) {
		List<String> catalogIdList = Lists.newArrayList();
		gatherCatalogDelList(catalogIdList, catalogId);
		idxDataInputDao.deleteDataInputCatalog(catalogIdList);

	}

	/**
	 * 根据ID删除模板
	 * 
	 * @param templateId
	 */
	public boolean deleteTemplateById(String templateIds) {
		try {
			String[] templateIdArray = templateIds.split(",");
			for (String templateId : templateIdArray) {
				if (StringUtils.isEmpty(templateId))
					continue;
				clearTemplateInfo(templateId);
				idxDataInputDao
						.deleteRptInputIdxTemplateByTemplateId(templateId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 取得子目录信息
	 * 
	 * @param catalogIdList
	 * @param catalogId
	 */
	private void gatherCatalogDelList(List<String> catalogIdList,
			String catalogId) {

		catalogIdList.add(catalogId);
		Map<String, Object> map = Maps.newHashMap();
		map.put("parentCatalogId", catalogId);
		List<RptInputCatalog> catalogList = idxDataInputDao
				.getDataInputCatalog(map);
		for (RptInputCatalog catalog : catalogList) {
			gatherCatalogDelList(catalogIdList, catalog.getCatalogId());
		}

	}
	public Map<String, Object> getShowTemplateInfoList(Pager pager, String catalogId) {

		Map<String, Object> params = Maps.newHashMap();

		if (catalogId == null) {
			catalogId = "ROOT";
		}

		if (!catalogId.equals("showAll"))
			params.put("catalogId", catalogId);
		String condition = pager.getCondition();

		if (condition == null) {
			if (catalogId != null)
				condition = "{\"op\":\"and\",\"rules\":[{\"op\":\"=\",\"field\":\"catalogId\",\"value\":\""
						+ catalogId + "\",\"type\":\"string\"}]}";
			pager.setCondition(condition);
		}
		if (StringUtils.isNotEmpty(condition)) {
			JSONObject json = JSON.parseObject(pager.getCondition());
			JSONArray array = json.getJSONArray("rules");
			for (int i = 0; i < array.size(); i++) {
				JSONObject temp = array.getJSONObject(i);
				String filedName = (String) temp.get("field");
				if (filedName.equals("templateNm")) {
					params.put("templateNm", "%" + temp.get("value") + "%");
					continue;
				}
				if (filedName.equals("templateType")) {
					params.put("templateType", temp.get("value"));
					continue;
				}
			}
			json.put("rules", "[]");
			pager.setCondition(json.toString());
		}
		pager.setSortname("");
		PageHelper.startPage(pager);
		 List<DataInputTemplateVO> voList = idxDataInputDao.getShowTemplateInfos(params);
		PageMyBatis<DataInputTemplateVO> page = (PageMyBatis<DataInputTemplateVO>) voList;

		Map<String, Object> rsMap = Maps.newHashMap();
		rsMap.put("Rows", page.getResult());
		rsMap.put("Total", page.getTotalCount());

		return rsMap;
	
	}
	
	
	/*
	public Map<String, Object> getTemplateInfoList(Pager pager, String catalogId) {

		Map<String, Object> params = Maps.newHashMap();

		if (catalogId == null) {
			catalogId = "ROOT";
		}

		if (!catalogId.equals("showAll"))
			params.put("catalogId", catalogId);
		String condition = pager.getCondition();

		if (condition == null) {
			if (catalogId != null)
				condition = "{\"op\":\"and\",\"rules\":[{\"op\":\"=\",\"field\":\"catalogId\",\"value\":\""
						+ catalogId + "\",\"type\":\"string\"}]}";
			pager.setCondition(condition);
		}
		if (StringUtils.isNotEmpty(condition)) {
			JSONObject json = JSONObject.fromObject(pager.getCondition());
			JSONArray array = json.getJSONArray("rules");
			for (int i = 0; i < array.size(); i++) {
				String arr = array.getString(i);
				JSONObject temp = JSONObject.fromObject(arr);
				String filedName = (String) temp.get("field");
				if (filedName.equals("templateNm")) {
					params.put("templateNm", "%" + temp.get("value") + "%");
					continue;
				}
				if (filedName.equals("templateType")) {
					params.put("templateType", temp.get("value"));
					continue;
				}
			}
			json.put("rules", "[]");
			pager.setCondition(json.toString());
		}
		PageHelper.startPage(pager);

		PageMyBatis<RptInputIdxTemplate> page = (PageMyBatis<RptInputIdxTemplate>) idxDataInputDao
				.getTemplateInfos(params);

		Map<String, Object> rsMap = Maps.newHashMap();
		rsMap.put("Rows", page.getResult());
		rsMap.put("Total", page.getTotalCount());

		return rsMap;
	}
	*/
	// DeployTaskBS.setProperties

	public String updateTemplate(String templateVO) {

		if (templateVO == null)
			return null;
		TemplateVO vo = getTemplateVOByJson(templateVO);
		String templateId = createTemplate(vo);
		return templateId;
	}

	public void saveTemplate(String templateJson) {
		createTemplate(getTemplateVOByJson(templateJson));
	}

	private String createTemplate(TemplateVO templateVO) {

		RptInputIdxTemplate template = templateVO.getTemplate();
		String templateId = template.getTemplateId();
		if (StringUtils.isEmpty(templateId)) {
			templateId = RandomUtils.uuid2();
			template.setTemplateId(templateId);
			idxDataInputDao.saveRptInputIdxTemplate(template);
		} else {
			idxDataInputDao.updateRptInputIdxTemplate(template);
			clearTemplateInfo(templateId);
		}

		if (templateVO.getDetailList() != null
				&& !templateVO.getDetailList().isEmpty()) {
			for (IdxCfgDetailVO vo : templateVO.getDetailList()) {

				RptInputIdxCfgDetail detail = new RptInputIdxCfgDetail();
				String cfgId = RandomUtils.uuid2();
				detail.setTemplateId(templateId);
				detail.setCfgId(cfgId);
				detail.setCfgNm(vo.getCfgNm());
				detail.setIndexNo(vo.getIdxNo());
				detail.setIndexNm(vo.getIndexNm());
				detail.setOrderNum(vo.getOrderNum());
				detail.setOrgMode(vo.getOrgMode());
				idxDataInputDao.saveRptInputIdxCfgDetail(detail);
				
				if(StringUtils.isNotEmpty(vo.getRuleId())){
					//RPT_INPUT_IDX_CFG_VALIDATE
					RptInputIdxCfgValidate val = new RptInputIdxCfgValidate();
					RptInputIdxCfgValidatePK pk = new RptInputIdxCfgValidatePK();
					pk.setCfgId(cfgId);
					pk.setRuleId(vo.getRuleId());
					val.setId(pk);
					idxDataInputDao.saveRptInputIdxCfgValidate(val);
				}
				

				if (!StringUtils.isEmpty(vo.getOrgNo())) {
					String[] orgNoArrays = vo.getOrgNo().split(",");
					String[] orgNmArrays = vo.getOrgNm().split(",");
					for (int x = 0; x < orgNoArrays.length; x++) {
						RptInputIdxCfgOrg cfgOrg = new RptInputIdxCfgOrg();
						RptInputIdxCfgOrgPK pk = new RptInputIdxCfgOrgPK();
						pk.setCfgId(cfgId);
						pk.setOrgNo(orgNoArrays[x]);
						cfgOrg.setOrgNm(orgNmArrays[x]);
						cfgOrg.setId(pk);
						idxDataInputDao.saveRptInputIdxCfgOrg(cfgOrg);
					}

				}

				List<DimFilterVO> dimFilterVOList = vo.getDimFilterInfos();
				if (dimFilterVOList != null && !dimFilterVOList.isEmpty()) {
					for (DimFilterVO dimFilterVO : dimFilterVOList) {
						RptInputIdxDimFilter filter = new RptInputIdxDimFilter();
						RptInputIdxDimFilterPK pk = new RptInputIdxDimFilterPK();
						pk.setCfgId(cfgId);
						pk.setDimNo(dimFilterVO.getDimNo());
						pk.setIndexNo(vo.getIdxNo());

						filter.setId(pk);
						filter.setFilterMode(dimFilterVO.getFilterMode());
						filter.setFilterVal(dimFilterVO.getFilterVal());
						filter.setFilterExplain(dimFilterVO.getFilterText());
						filter.setDimNm(dimFilterVO.getDimNm());
						dimFilterVO.getDimNm();
						filter.setTemplateId(templateId);

						idxDataInputDao.saveRptInputIdxDimFilter(filter);
					}
				}
				
			}
		}
		return templateId;
	}

	private void clearTemplateInfo(String templateId) {
		idxDataInputDao.deleteRptInputIdxDimFilterByTemplateId(templateId);
		idxDataInputDao.deleteRptInputIdxCfgDetailByTemplateId(templateId);
		idxDataInputDao.deleteRptInputIdxCfgOrgByTemplateId(templateId);
	}

	private TemplateVO getTemplateVOByJson(String json) {

		JSONObject taskValueObj = JSON.parseObject(json);

		JSONObject rptTskInfoJsonObj = taskValueObj.getJSONObject("template");
		JSONArray taskNodeObjListJsonObj = taskValueObj
				.getJSONArray("detailList");

		TemplateVO templateVO = new TemplateVO();
		templateVO.setTemplate(geTemplateInfoByJsonObj(rptTskInfoJsonObj));
		templateVO
				.setDetailList(getDetailListByJsonObj(taskNodeObjListJsonObj));

		return templateVO;

	}

	private RptInputIdxTemplate geTemplateInfoByJsonObj(
			JSONObject rptTskInfoJsonObj) {
		return rptTskInfoJsonObj.toJavaObject(RptInputIdxTemplate.class);
	}

	private List<IdxCfgDetailVO> getDetailListByJsonObj(JSONArray jns) {

		if (jns == null || jns.size() == 0)
			return null;
		List<IdxCfgDetailVO> taskNodeObjVOList = Lists.newArrayList();
		for (int i = 0; i < jns.size(); i++) {
			JSONObject detail = jns.getJSONObject(i);
			IdxCfgDetailVO vors = detail.toJavaObject(IdxCfgDetailVO.class);
			JSONArray filterInfos = detail.getJSONArray("filterinfos");
			List<DimFilterVO> filterVOList = Lists.newArrayList();
			for (int j = 0; j < filterInfos.size(); j++) {
				DimFilterVO dimFilterVO = filterInfos.getObject(j, DimFilterVO.class);
				filterVOList.add(dimFilterVO);
			}
			vors.setDimFilterInfos(filterVOList);
			taskNodeObjVOList.add(vors);
		}

		return taskNodeObjVOList;

	}

	/**
	 * 获取处理任务的模板信息
	 * 
	 * @param templateId
	 * @return
	 */
	public List<TableGridVO> getTemplateToJson(String templateId,
			String taskInstanceId) {

		List<IdxCfgDetailVO> detailList = getDetailListByTemplateId(templateId);

		Map<String, List<Map<String, String>>> result = getIdxResult(
				templateId, taskInstanceId);

		return templateInfoToTableGrid(detailList, result, taskInstanceId);
	}

	/**
	 * 01：指标补录 02：指标计划值补录
	 */
	private Map<String, List<Map<String, String>>> getIdxResult(
			String templateId, String taskInstanceId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("templateId", templateId);

		RptInputIdxTemplate template = idxDataInputDao.getTemplateInfos(params)
				.get(0);
		List<Map<String, Object>> result = null;
		params.clear();
		params.put("taskInstanceId", taskInstanceId);
		if (template.getTemplateType().equals("01")) {
			// 指标补录
			// RptInputIdxTempResult result
			result = idxDataInputDao.getRptInputIdxTempResult(params);
		} else {
			// 计划值补录
			// RptInputIdxPlanTempResult result
			result = idxDataInputDao.getRptInputIdxPlanTempResult(params);
		}
		if (result == null)
			return null;
		else
			return getCorrespIdxResult(result);
	}

	private Map<String, List<Map<String, String>>> getCorrespIdxResult(
			List<Map<String, Object>> result) {
		Map<String, List<Map<String, String>>> rs = Maps.newHashMap();
		for (Map<String, Object> resultMap : result) {
			String cfgId = (String) resultMap.get("CFG_ID");
			List<Map<String, String>> rsList = rs.get(cfgId);
			if (rsList == null)
				rsList = Lists.newArrayList();
			Map<String, String> tmpMap = Maps.newHashMap();
			tmpMap.put("cfgId", cfgId);
			String indexNo = (String) resultMap.get("INDEX_NO");
			List<Map<String, String>> fieldMap = idxDataInputDao
					.getRptIdxDimRelDefaultMaxVersion(indexNo);

			// Map<String,String>returnMap = Maps.newHashMap();

			for (Map<String, String> mapKey : fieldMap) {
				String dimNo = mapKey.get("DIM_NO");
				String storeCol = mapKey.get("STORE_COL");
				String value = (String) resultMap.get(storeCol);
				tmpMap.put(dimNo, value);
			}
			
			String dataSts = (String) resultMap.get("DATA_STS");
			tmpMap.put("DATA_STS", dataSts);
			
			String orgNo = (String) resultMap.get("ORG_NO");
			orgNo = orgNo.equals("-") ? "" : orgNo;
			tmpMap.put("ORG", orgNo);

			String currency = (String) resultMap.get("CURRENCY");
			currency = currency.equals("-") ? "" : currency;
			tmpMap.put("CURRENCY", currency);

			String operOpinion = (String) resultMap.get("OPER_OPINION");
			operOpinion = operOpinion.equals("-") ? "" : operOpinion;
			tmpMap.put("OPER_OPINION", operOpinion);

			String value = resultMap.get("INDEX_VAL") == null ? ""
					: ((BigDecimal) resultMap.get("INDEX_VAL")).intValue() + "";
			tmpMap.put("value", value);
			tmpMap.put("INDEX_NO", indexNo);
			rsList.add(tmpMap);
			rs.put(cfgId, rsList);
		}
		return rs;
	}

	private List<TableGridVO> templateInfoToTableGrid(
			List<IdxCfgDetailVO> detailList,
			Map<String, List<Map<String, String>>> result, String taskInstanceId) {
		List<TableGridVO> gridList = Lists.newArrayList();
		for (IdxCfgDetailVO vo : detailList) {

			TableGridVO grid = TableGridVO.getDefaultTableColumn();
			grid.setColumnVO(getColumns(vo));
			grid.setData(getTableData(vo, result.get(vo.getCfgId()),
					taskInstanceId));
			grid.setCfgId(vo.getCfgId());
			gridList.add(grid);
		}

		return gridList;
	}

	private TableColumnVO getColumns(IdxCfgDetailVO vo) {

		TableColumnVO colVO = TableColumnVO.getDefaultTableColumn();
		StringBuilder displayBuff = new StringBuilder();
		displayBuff.append("配置名称:").append(vo.getCfgNm());
		// .append("(指标:").append(vo.getIndexNm()).append("")
		colVO.setDisplay(displayBuff.toString());
		colVO.setColumns(getSubColumns(vo.getDimFilterInfos(), vo.getIndexNm(),
				vo.getOrgMode()));

		return colVO;
	}

	private List<TableColumnVO> getSubColumns(List<DimFilterVO> filterInfos,
			String indexNm, String orgMode) {
		if (filterInfos == null || filterInfos.isEmpty())
			return null;

		List<TableColumnVO> voList = Lists.newArrayList();
		int defaultCnt = 5;
		// if(orgMode.equals("01"))
		// defaultCnt = 4;
		int cnt = filterInfos.size() + defaultCnt;
		int width = 99 / cnt;
		// 指标列
		addIndexCol(width, indexNm, voList);
		// 币种列和机构列
		addDefaultCol(width, orgMode, voList);
		for (DimFilterVO filter : filterInfos) {
			TableColumnVO colVO = TableColumnVO.getDefaultTableColumn();
			colVO.setDisplay(filter.getDimNm());
			colVO.setName(filter.getIdxNo() + "_" + filter.getDimNo());
			colVO.setWidth(width + "%");
			voList.add(colVO);
		}
		addValueCol(width, voList);

		return voList;
	}

	private void addValueCol(int width, List<TableColumnVO> voList) {
		TableColumnVO colVO = TableColumnVO.getDefaultTableColumn();
		colVO.setDisplay("值");
		colVO.setName("value");
		colVO.setWidth(width + "%");
		voList.add(colVO);
	}

	private void addDefaultCol(int width, String orgMode,
			List<TableColumnVO> voList) {
		// 配置机构 01：取实例机构 02：独立配置机构
		TableColumnVO colVO = TableColumnVO.getDefaultTableColumn();
		colVO.setDisplay("机构");
		colVO.setName("org" + orgMode);
		colVO.setWidth(width + "%");
		voList.add(colVO);
		// 添加币种
		TableColumnVO currVO = TableColumnVO.getDefaultTableColumn();
		currVO.setDisplay("币种");
		currVO.setName("currency");
		currVO.setWidth(width + "%");
		voList.add(currVO);

	}

	private void addIndexCol(int width, String indexNm,
			List<TableColumnVO> voList) {
		TableColumnVO colVO = TableColumnVO.getDefaultTableColumn();
		colVO.setDisplay("指标名称");
		colVO.setName("indexNm");
		colVO.setWidth(width + "%");
		voList.add(colVO);
	}

	private List<List<Map<String, String>>> getTableData(IdxCfgDetailVO vo,
			List<Map<String, String>> resultList, String taskInstanceId) {
		if (vo == null || vo.getDimFilterInfos() == null
				|| vo.getDimFilterInfos().isEmpty())
			return null;
		List<DimFilterVO> filterInfos = vo.getDimFilterInfos();
		List<List<Map<String, String>>> rsColList = Lists.newArrayList();
		String idxNm = null;
		String idxId = null;
		if (resultList != null && !resultList.isEmpty()) {
			for (Map<String, String> result : resultList) {
				List<Map<String, String>> list = Lists.newArrayList();
				for (DimFilterVO filter : filterInfos) {
					Map<String, String> colMap = Maps.newHashMap();
					colMap.put("type", "commColumn");
					colMap.put("key",
							filter.getIdxNo() + "_" + filter.getDimNo());
					String value = "";
					String text = "";
					if (result != null) {
						value = result.get(filter.getDimNo());
						if (!StringUtils.isEmpty(value) && !value.equals("-")) {

							Map<String, String> params = Maps.newHashMap();
							params.put("dimItemNo", value);
							params.put("dimTypeNo", filter.getDimNo());
							RptDimItemInfo item = this.rptDimBS
									.findDimItemInfoByPkId(params);
							text = item.getDimItemNm();
						}
					}
					colMap.put("dimNo", value);
					colMap.put("text", text);
					colMap.put("filterVal", filter.getFilterVal());
					if (idxNm == null) {
						idxNm = vo.getIndexNm();
						idxId = vo.getIdxNo();
					}
					list.add(colMap);
				}
				String filterVal = vo.getOrgNo();
				if (vo.getOrgMode().equals("01")) {
					Map<String, Object> insMap = taskDao
							.getRptTskInsOrgInfo(taskInstanceId);
					filterVal = (String) insMap.get("ORG_NO") + "_"
							+ (String) insMap.get("ORG_NM");
					// result.put("ORG",()insMap.get("ORG_NO"));
					appendOrgCol((String) insMap.get("ORG_NO"), list);
				} else {
					if (!StringUtils.isEmpty(vo.getOrgNm())) {
						StringBuilder filterValBuffer = new StringBuilder();
						String[] orgNos = vo.getOrgNo().split(",");
						String[] orgNms = vo.getOrgNm().split(",");
						for (int i = 0; i < orgNos.length; i++) {
							if (i != 0)
								filterValBuffer.append(",");
							filterValBuffer.append(orgNos[i]).append("_")
									.append(orgNms[i]);
						}
						filterVal = filterValBuffer.toString();
					}
					appendOrgCol(result == null ? null : result.get("ORG"),
							list, filterVal);
				}
				appendDataSts((String) result.get("DATA_STS"),list);
				// 添加指标列
				appendIdxCol(idxId, idxNm, list);
				appendCurrencyCol(
						result == null ? null : result.get("CURRENCY"), list);
				appendValueCol(result == null ? null : result.get("value"),
						list);
				appendCfgCol(vo.getCfgId(), list);
				appendOperOpinionCol(
						result == null ? null : result.get("OPER_OPINION"),
						list);
				rsColList.add(list);
			}
		} else {

			List<Map<String, String>> list = Lists.newArrayList();
			for (DimFilterVO filter : filterInfos) {
				Map<String, String> colMap = Maps.newHashMap();
				colMap.put("type", "commColumn");
				colMap.put("key", filter.getIdxNo() + "_" + filter.getDimNo());
				String value = "";
				colMap.put("value", value);
				colMap.put("filterVal", filter.getFilterVal());
				if (idxNm == null) {
					idxNm = vo.getIndexNm();
					idxId = vo.getIdxNo();
				}
				list.add(colMap);
			}
			String filterVal = vo.getOrgNo();
			if (vo.getOrgMode().equals("01")) {
				Map<String, Object> insMap = taskDao
						.getRptTskInsOrgInfo(taskInstanceId);
				filterVal = (String) insMap.get("ORG_NO") + "_"
						+ (String) insMap.get("ORG_NM");
				// result.put("ORG",()insMap.get("ORG_NO"));
				appendOrgCol((String) insMap.get("ORG_NO"), list);
			} else {
				if (!StringUtils.isEmpty(vo.getOrgNm())) {
					StringBuilder filterValBuffer = new StringBuilder();
					String[] orgNos = vo.getOrgNo().split(",");
					String[] orgNms = vo.getOrgNm().split(",");
					for (int i = 0; i < orgNos.length; i++) {
						if (i != 0)
							filterValBuffer.append(",");
						filterValBuffer.append(orgNos[i]).append("_")
								.append(orgNms[i]);
					}
					filterVal = filterValBuffer.toString();
				}
				appendOrgCol(null, list, filterVal);
			}
			// 添加指标列
			appendIdxCol(idxId, idxNm, list);
			appendCurrencyCol(null, list);
			appendValueCol(null, list);
			appendCfgCol(vo.getCfgId(), list);
			rsColList.add(list);

		}

		return rsColList;
	}
	
	private void appendDataSts(String dataSts,List<Map<String, String>> colList){

		Map<String, String> colMap = Maps.newHashMap();
		colMap.put("type", "dataSts");
		colMap.put("value", dataSts);
		colList.add(colMap);
	}

	private void appendOperOpinionCol(String operOpinion,
			List<Map<String, String>> colList) {
		Map<String, String> colMap = Maps.newHashMap();
		colMap.put("type", "operOpinion");
		colMap.put("value", operOpinion);

		colList.add(colMap);
	}

	private void appendCfgCol(String cfgId, List<Map<String, String>> colList) {
		Map<String, String> colMap = Maps.newHashMap();
		colMap.put("type", "cfgId");
		colMap.put("value", cfgId);

		colList.add(colMap);
	}

	private void appendValueCol(String value, List<Map<String, String>> colList) {
		Map<String, String> colMap = Maps.newHashMap();
		colMap.put("type", "value");
		colMap.put("value", value);

		colList.add(colMap);
	}

	private void appendIdxCol(String idxId, String idxNm,
			List<Map<String, String>> colList) {
		Map<String, String> colMap = Maps.newHashMap();
		colMap.put("type", "indexColumn");
		colMap.put("idxId", idxId);
		colMap.put("idxNm", idxNm);

		colList.add(colMap);
	}

	private void appendOrgCol(String orgId, List<Map<String, String>> colList) {
		String orgNm = "";
		if (StringUtils.isNotEmpty(orgId)) {
			Map<String, Object> queryParam = Maps.newHashMap();

			queryParam.put("orgNo", orgId);
			BioneOrgInfo orgInfo = orgDao.list(queryParam).get(0);
			orgNm = orgInfo.getOrgName();
		}
		Map<String, String> colMap = Maps.newHashMap();
		colMap.put("orgId", orgId);
		colMap.put("orgNm", orgNm);
		colMap.put("type", "orgColumn");

		colList.add(colMap);
	}

	private void appendOrgCol(String orgId, List<Map<String, String>> colList,
			String filterVal) {
		String orgNm = "";
		if (StringUtils.isNotEmpty(orgId)) {
			Map<String, Object> queryParam = Maps.newHashMap();

			queryParam.put("orgNo", orgId);
			BioneOrgInfo orgInfo = orgDao.list(queryParam).get(0);
			orgNm = orgInfo.getOrgName();
		}
		Map<String, String> colMap = Maps.newHashMap();
		colMap.put("orgId", orgId);
		colMap.put("type", "orgColumn");
		colMap.put("orgNm", orgNm);
		colMap.put("filterVal", filterVal);

		colList.add(colMap);
	}

	private void appendCurrencyCol(String currencyId,
			List<Map<String, String>> colList) {

		Map<String, String> colMap = Maps.newHashMap();

		if (currencyId != null) {
			colMap.put("currencyId", currencyId);
			colMap.put("currencyNm", idxDataInputDao.queryCurNm(currencyId));
		}
		colMap.put("type", "currencyColumn");

		colList.add(colMap);
	}

	public List<CommonTreeNode> getDataInputDimTree(String dimInfo,
			String filterInfo, String basePath) {
		Map<String, Object> conditions = Maps.newHashMap();

		String dimTypeNo = dimInfo.split("_")[1];
		conditions.put("dimTypeNo", dimTypeNo);
		List<String> itemList = Lists.newArrayList();
		if (!StringUtils.isEmpty(basePath)) {
			if (filterInfo != null && !filterInfo.equals("null")) {
				String[] items = filterInfo.split(",");
				for (String item : items)
					itemList.add(item);
				conditions.put("list", itemList);
			}
		}
		List<RptDimItemInfo> infos = this.idxDataInputDao
				.getDataInputDim(conditions);
		List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
		if (infos != null && infos.size() > 0) {
			for (RptDimItemInfo info : infos) {
				CommonTreeNode node = new CommonTreeNode();
				node.setId(info.getId().getDimItemNo());
				node.setText(info.getDimItemNm());
				node.setUpId(info.getUpNo());
				node.setIcon(basePath
						+ GlobalConstants4plugin.REPORT_LABELOBJ_ICON);
				nodes.add(node);
			}
		}
		return nodes;
	}

	public String saveDataInputData(String data) {

		DataInputTaskVO dataInputTaskVO = getDataInputTaskVOFromJsonString(data);
		return batchSaveDataInputInfo(dataInputTaskVO);
	}

	private String batchSaveDataInputInfo(DataInputTaskVO dataInputTaskVO) {
		try {
			// 清空补录数据
			clearDataInputRs(dataInputTaskVO.getInputType(),
					dataInputTaskVO.getTaskInstanceId());
			for (DataInputVO dataInputVO : dataInputTaskVO.getDataInputList()) {
				saveDataInputInfo(dataInputVO, dataInputTaskVO.getInputType(),
						dataInputTaskVO.getTaskId(),
						dataInputTaskVO.getTaskInstanceId(),
						dataInputTaskVO.getDataDate());
			}
			return "1";
		} catch (Exception ex) {
			ex.printStackTrace();
			return "0";
		}
	}

	private void saveDataInputInfo(DataInputVO dataInputVO, String inputType,
			String taskId, String taskInstanceId, String dataDate) {
		// 重新插入补录数据
		saveDataInputRs(dataInputVO, inputType, taskId, taskInstanceId,
				dataDate);

	}

	private void saveDataInputRs(DataInputVO dataInputVO, String inputType,
			String taskId, String taskInstanceId, String dataDate) {

		List<Map<String, String>> fieldMapList = idxDataInputDao
				.getRptIdxDimRelDefaultMaxVersion(dataInputVO.getIdxNo());
		Map<String, String> fieldMap = Maps.newHashMap();
		for (Map<String, String> map : fieldMapList) {
			fieldMap.put((String) map.get("DIM_NO"),
					(String) map.get("STORE_COL"));
		}
		// 得到指标对应维度下的字段对应关系
		Map<String, Object> dimMap = Maps.newHashMap();
		List<DimRowVO> rowList = dataInputVO.getDimRowList();
		for (DimRowVO rowVO : rowList) {
			for (DataInputDimVO dimVO : rowVO.getDimList()) {
				// 得到对应的维度字段对应位置
				/*
				 * if(dimVO.getDimTypeNo().equals("INDEX_NO")||
				 * dimVO.getDimTypeNo().equals("CURRENCY")||
				 * dimVO.getDimTypeNo().equals("ORG_NO")||
				 * dimVO.getDimTypeNo().equals("INDEX_VAL")) {
				 * dimMap.put(dimVO.getDimTypeNo(), dimVO.getDimItemValue()); }
				 */
				if (dimVO.getDimTypeNo().equals("INDEX_NO")) {
					dimMap.put("indexNo", dimVO.getDimItemValue());
				} else if (dimVO.getDimTypeNo().equals("CURRENCY")) {
					dimMap.put("currency", dimVO.getDimItemValue());
				} else if (dimVO.getDimTypeNo().equals("ORG_NO")) {
					dimMap.put("orgNo", dimVO.getDimItemValue());
				} else if (dimVO.getDimTypeNo().equals("INDEX_VAL")) {
					dimMap.put("indexVal", StringUtils.isEmpty(dimVO
							.getDimItemValue()) ? new BigDecimal(0) : dimVO
							.getDimItemValue().equals("-") ? new BigDecimal(0)
							: new BigDecimal(dimVO.getDimItemValue()));
				} else if (dimVO.getDimTypeNo().equals("CFG_ID")) {
					dimMap.put("cfgId", dimVO.getDimItemValue());
				} else if (dimVO.getDimTypeNo().equals("operOpinion")) {
					dimMap.put("operOpinion", dimVO.getDimItemValue());
				} else if(dimVO.getDimTypeNo().equals("DATA_STS")){
					dimMap.put("dataSts", dimVO.getDimItemValue());
				}else {
					dimMap.put(
							fieldMap.get(dimVO.getDimTypeNo()).toLowerCase(),
							dimVO.getDimItemValue());
				}
				// dimMap.put(dimFieldName, dimVO.getDimItemNo());
			}
			dimMap.put("taskId", taskId);
			dimMap.put("taskInstanceId", taskInstanceId);
			dimMap.put("dataDate", dataDate);
			dimMap.put("inputDataId", RandomUtils.uuid2());
			checkDimMap(dimMap);
			createDataInputRs(inputType, dimMap);
		}
		// List<DataInputDimVO> dimList = dataInputVO.getDimList();

	}

	private void checkDimMap(Map<String, Object> dimMap) {
		for (int i = 1; i <= 10; i++) {
			if (dimMap.get("dim" + i) == null) {
				dimMap.put("dim" + i, "-");
			}
		}
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		dimMap.put("operUser", user.getUserId());
		dimMap.put("operTime", new Timestamp(System.currentTimeMillis()));
	}

	private void createDataInputRs(String type, Map<String, Object> params) {
		if (type.equals("01"))
			idxDataInputDao.saveRptInputIdxTempResult(params);
		if (type.equals("02"))
			idxDataInputDao.saveRptInputIdxPlanTempResult(params);
	}

	private void clearDataInputRs(String type, String taskInstanceId) {
		if (type.equals("01"))
			idxDataInputDao
					.deleteRptInputIdxTempResultByTaskInstanceId(taskInstanceId);
		if (type.equals("02"))
			idxDataInputDao
					.deleteRptInputIdxPlanTempResultByTaskInstanceId(taskInstanceId);

	}

	private DataInputTaskVO getDataInputTaskVOFromJsonString(String data) {
		DataInputTaskVO dataInputTaskVO = new DataInputTaskVO();
		JSONObject jsObj = JSON.parseObject(data);
		String templateId = jsObj.getString("templateId");
		Map<String, Object> map = Maps.newHashMap();
		map.put("templateId", templateId);
		RptInputIdxTemplate template = idxDataInputDao.getTemplateInfos(map)
				.get(0);

		dataInputTaskVO.setInputType(template.getTemplateType());
		dataInputTaskVO.setTaskId(jsObj.getString("taskId"));
		dataInputTaskVO.setTaskInstanceId(jsObj.getString("taskInstanceId"));
		dataInputTaskVO.setDataDate(jsObj.getString("dataDate"));

		dataInputTaskVO.setDataInputList(getDataInputInfoFromJsonString(jsObj
				.getJSONArray("dataInputList")));

		return dataInputTaskVO;
	}

	private List<DataInputVO> getDataInputInfoFromJsonString(JSONArray contents) {
		List<DataInputVO> voList = Lists.newArrayList();
		String idxNo = null;
		for (int j = 0; j < contents.size(); j++) {
			DataInputVO inputVO = new DataInputVO();
			JSONArray itemList = contents.getJSONArray(j);
			List<DimRowVO> dimRowList = Lists.newArrayList();
			for (int i = 0; i < itemList.size(); i++) {
				DimRowVO vo = new DimRowVO();
				List<DataInputDimVO> dimVOList = Lists.newArrayList();
				JSONArray objList = (JSONArray) itemList.get(i);
				for (int x = 0; x < objList.size(); x++) {
					DataInputDimVO inputDimVO = new DataInputDimVO();
					JSONObject obj = objList.getJSONObject(x);
					String dimTypeNo = obj.getString("dimTypeNo");
					String dimItemValue = "-";
					if (obj.containsKey("dimItemValue")
							&& !obj.getString("dimItemValue").equals("null"))
						dimItemValue = obj.getString("dimItemValue");
					if (dimTypeNo.equals("INDEX_NO"))
						idxNo = dimItemValue;
					inputDimVO.setDimItemValue(dimItemValue);
					inputDimVO.setDimTypeNo(dimTypeNo);
					dimVOList.add(inputDimVO);
				}
				vo.setDimList(dimVOList);
				dimRowList.add(vo);
			}
			inputVO.setDimRowList(dimRowList);
			inputVO.setIdxNo(idxNo);
			voList.add(inputVO);
		}
		return voList;
	}

	/**
	 * 从结果表中取出信息 01：指标补录 02：指标计划值补录
	 * 
	 * @param taskInstanceId
	 * @param templateId
	 * @return
	 */
	public List<Map<String, Object>> getResultInfo(String taskInstanceId,
			String templateId) {
		RptInputIdxTemplate template = getTemplateInfos(null, templateId, null)
				.get(0);
		List<Map<String, Object>> resultList = Lists.newArrayList();
		if (template.getTemplateType().equals("01")) {
			resultList = idxDataInputDao
					.getRptInputIdxTempResultByTaskInstanceId(taskInstanceId);
		} else if (template.getTemplateType().equals("02")) {
			resultList = idxDataInputDao
					.getRptInputIdxPlanTempResultByTaskInstanceId(taskInstanceId);
		}
		return resultList;
	}
	
	private String getDsOfResult(){
		Map<String,Object>params = Maps.newHashMap();
		params.put("setId", "RPT_IDX_RESULT");
		return idxDataInputDao.getSysNo(params);
	}

	@Transactional(readOnly = false)
	public void tempDataToResult(String templateId, String taskInstanceId) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("templateId", templateId);
		RptInputIdxTemplate template = idxDataInputDao.getTemplateInfos(map)
				.get(0);
		
		String ds = getDsOfResult();
		List<Map<String, Object>> rsList = null;
		Map<String, Object> queryParam = null;
		Map<String,RptInputIdxUpdlog>logMap = Maps.newHashMap();
		if (template.getTemplateType().equals("01")) {
			rsList = idxDataInputDao
					.getRptInputIdxTempResultByTaskInstanceId(taskInstanceId);
			for (Map<String, Object> rs : rsList) {

				RptIdxResultPK pk = (RptIdxResultPK) getResultObject(
						RptIdxResultPK.class, rs);
				RptIdxResult result = (RptIdxResult) getResultObject(
						RptIdxResult.class, rs);
				result.setId(pk);
				queryParam = getQueryMap(rs);
				if (getRptIdxResultByParam(queryParam,ds) ==0) {
					// 更新
					saveRptIdxResult(result,ds);
				} else {
					// 新增
					updateRptIdxResult(result,ds);
				}
				//添加日志
				String key = (String)rs.get("INDEX_NO")+"_"+(String)rs.get("DATA_DATE");
				RptInputIdxUpdlog log = logMap.get(key);
				if(log==null){
					log = new RptInputIdxUpdlog();
					RptInputIdxUpdlogPK logpk = new RptInputIdxUpdlogPK();
					logpk.setDataDate((String)rs.get("DATA_DATE"));
					logpk.setIndexId((String)rs.get("INDEX_NO"));
					log.setId(logpk);
					log.setType("01");
					log.setUpdateDate(new Timestamp(System.currentTimeMillis()));
					log.setUpdateUser(BioneSecurityUtils.getCurrentUserInfo().getUserId());
					log.setOrgNo("-");
					logMap.put(key, log);
				}
			}
		} else if (template.getTemplateType().equals("02")) {
			rsList = idxDataInputDao
					.getRptInputIdxPlanTempResultByTaskInstanceId(taskInstanceId);
			for (Map<String, Object> rs : rsList) {
				RptIdxPlanResultPK pk = (RptIdxPlanResultPK) getResultObject(
						RptIdxPlanResultPK.class, rs);
				RptIdxPlanResult result = (RptIdxPlanResult) getResultObject(
						RptIdxPlanResult.class, rs);
				pk.setPlanType("-");
				result.setId(pk);
				// rptIdxPlanResultBS.saveOrUpdateEntity(result);
				queryParam = getQueryMap(rs);
				if (getRptIdxPlanResultByParam(queryParam,ds)  ==0) {
					// 更新
					saveRptIdxPlanResult(result,ds);
				} else {
					// 新增
					updateRptIdxPlanResult(result,ds);
				}
				//添加日志
				String key = (String)rs.get("INDEX_NO")+"_"+(String)rs.get("DATA_DATE");
				RptInputIdxUpdlog log = logMap.get(key);
				if(log==null){
					log = new RptInputIdxUpdlog();
					RptInputIdxUpdlogPK logpk = new RptInputIdxUpdlogPK();
					logpk.setDataDate((String)rs.get("DATA_DATE"));
					logpk.setIndexId((String)rs.get("INDEX_NO"));
					log.setId(logpk);
					log.setType("02");
					log.setUpdateDate(new Timestamp(System.currentTimeMillis()));
					log.setUpdateUser(BioneSecurityUtils.getCurrentUserInfo().getUserId());
					log.setOrgNo("-");
					logMap.put(key, log);
				}
			}
		}
		Map<String,Object>params = Maps.newHashMap();
		for(String logKey:logMap.keySet())
		{
			String[] keyArray = logKey.split("_");
			params.put("indexNo", keyArray[0]);
			params.put("dataDate", keyArray[1]);
			int logCnt = idxDataInputDao.getRptInputIdxUpdlogCnt (params);
			if(logCnt ==0)
				idxDataInputDao.saveRptInputIdxUpdlog(logMap.get(logKey));
			else
				idxDataInputDao.updateRptInputIdxUpdlog(logMap.get(logKey));
		}
	}
	
	private void saveRptIdxResult(RptIdxResult result,String ds){
		StringBuilder buff = new StringBuilder();
		buff.append("INSERT INTO ").append(StringUtils.isEmpty(ds)?"":(ds+".")).append("RPT_IDX_RESULT( INDEX_NO,    DATA_DATE,    ORG_NO,    CURRENCY,    DIM1,    DIM2,DIM3,    DIM4,    DIM5,    DIM6,    DIM7,    DIM8,    DIM9,    DIM10,    INDEX_VAL)")
		.append(" VALUES (?,?,?,?,?,  ?,?,?,?,?, ?,?,?,?,?)");
		List<Object[]> params = new ArrayList<Object[]>();
		Object[] obj = new Object[15];
		obj[0] = result.getId().getIndexNo();
		obj[1] = result.getId().getDataDate();
		obj[2] = result.getId().getOrgNo();
		obj[3] = result.getId().getCurrency();
		obj[4] = result.getId().getDim1();
		obj[5] = result.getId().getDim2();
		obj[6] = result.getId().getDim3();
		obj[7] = result.getId().getDim4();
		obj[8] = result.getId().getDim5();
		obj[9] = result.getId().getDim6();
		obj[10] = result.getId().getDim7();
		obj[11] = result.getId().getDim8();
		obj[12] = result.getId().getDim9();
		obj[13] = result.getId().getDim10();
		obj[14] = result.getIndexVal();
		
		params.add(obj);
		this.jdbcBaseDAO.batchUpdate(buff.toString(), params, 1000);
	}
	
	private void updateRptIdxResult(RptIdxResult result,String ds){

		StringBuilder buff = new StringBuilder();
		buff.append("UPDATE ").append(StringUtils.isEmpty(ds)?"":(ds+".")) .append("RPT_IDX_RESULT SET INDEX_VAL =? ");
		buff.append("WHERE	 INDEX_NO= ? AND 		DATA_DATE= ? AND		ORG_NO=?  AND		")
		.append(" CURRENCY=? AND		DIM1=? AND 		DIM2=?  AND		DIM3=?  AND	")
		.append(" DIM4=?  AND		DIM5=?  AND		DIM6=?  AND		DIM7=? AND	")
		.append(" DIM8=?  AND		DIM9=?  AND		DIM10=?	");
		
		List<Object[]> params = new ArrayList<Object[]>();
		Object[] obj = new Object[15];
		obj[0] = result.getIndexVal();
		obj[1] = result.getId().getIndexNo();
		obj[2] = result.getId().getDataDate();
		obj[3] = result.getId().getOrgNo();
		obj[4] = result.getId().getCurrency();
		obj[5] = result.getId().getDim1();
		obj[6] = result.getId().getDim2();
		obj[7] = result.getId().getDim3();
		obj[8] = result.getId().getDim4();
		obj[9] = result.getId().getDim5();
		obj[10] = result.getId().getDim6();
		obj[11] = result.getId().getDim7();
		obj[12] = result.getId().getDim8();
		obj[13] = result.getId().getDim9();
		obj[14] = result.getId().getDim10();
		
		params.add(obj);
		this.jdbcBaseDAO.batchUpdate(buff.toString(), params, 1000);
		
	}
	
	private void saveRptIdxPlanResult(RptIdxPlanResult result,String ds){
		StringBuilder buff = new StringBuilder();
		buff.append("INSERT INTO ").append(StringUtils.isEmpty(ds)?"":(ds+".")).append("RPT_IDX_PLAN_RESULT( INDEX_NO,    DATA_DATE,    ORG_NO,    CURRENCY,    DIM1,    DIM2,DIM3,    DIM4,    DIM5,    DIM6,    DIM7,    DIM8,    DIM9,    DIM10,    INDEX_VAL)")
		.append(" VALUES (?,?,?,?,?,  ?,?,?,?,?, ?,?,?,?,?)");
		List<Object[]> params = new ArrayList<Object[]>();
		Object[] obj = new Object[15];
		obj[0] = result.getId().getIndexNo();
		obj[1] = result.getId().getDataDate();
		obj[2] = result.getId().getOrgNo();
		obj[3] = result.getId().getCurrency();
		obj[4] = result.getId().getDim1();
		obj[5] = result.getId().getDim2();
		obj[6] = result.getId().getDim3();
		obj[7] = result.getId().getDim4();
		obj[8] = result.getId().getDim5();
		obj[9] = result.getId().getDim6();
		obj[10] = result.getId().getDim7();
		obj[11] = result.getId().getDim8();
		obj[12] = result.getId().getDim9();
		obj[13] = result.getId().getDim10();
		obj[14] = result.getIndexVal();
		
		params.add(obj);
		this.jdbcBaseDAO.batchUpdate(buff.toString(), params, 1000);
	}

	private void updateRptIdxPlanResult(RptIdxPlanResult result,String ds){

		StringBuilder buff = new StringBuilder();
		buff.append("UPDATE ").append(StringUtils.isEmpty(ds)?"":(ds+".")) .append("RPT_IDX_PLAN_RESULT SET INDEX_VAL =? ");
		buff.append("WHERE	 INDEX_NO= ? AND 		DATA_DATE= ? AND		ORG_NO=?  AND		")
		.append(" CURRENCY=? AND		DIM1=? AND 		DIM2=?  AND		DIM3=?  AND	")
		.append(" DIM4=?  AND		DIM5=?  AND		DIM6=?  AND		DIM7=? AND	")
		.append(" DIM8=?  AND		DIM9=?  AND		DIM10=?	");
		
		List<Object[]> params = new ArrayList<Object[]>();
		Object[] obj = new Object[15];
		obj[0] = result.getIndexVal();
		obj[1] = result.getId().getIndexNo();
		obj[2] = result.getId().getDataDate();
		obj[3] = result.getId().getOrgNo();
		obj[4] = result.getId().getCurrency();
		obj[5] = result.getId().getDim1();
		obj[6] = result.getId().getDim2();
		obj[7] = result.getId().getDim3();
		obj[8] = result.getId().getDim4();
		obj[9] = result.getId().getDim5();
		obj[10] = result.getId().getDim6();
		obj[11] = result.getId().getDim7();
		obj[12] = result.getId().getDim8();
		obj[13] = result.getId().getDim9();
		obj[14] = result.getId().getDim10();
		
		params.add(obj);
		this.jdbcBaseDAO.batchUpdate(buff.toString(), params, 1000);
		
	}

	private Integer getRptIdxPlanResultByParam(Map<String,Object> param,String ds){
		StringBuilder buff = new StringBuilder();
		buff.append("SELECT 1 FROM ").append(StringUtils.isEmpty(ds)?"":(ds+".")).append("RPT_IDX_PLAN_RESULT  WHERE	INDEX_NO=?  AND ")
		.append("  DATA_DATE=?  AND		ORG_NO=? AND		CURRENCY=? AND		DIM1=? AND ")
		.append("  DIM2=?  AND		DIM3=? AND		DIM4=?  AND		DIM5=? AND		DIM6=?  AND ")
		.append("  DIM7=?  AND		DIM8=? AND		DIM9=?  AND		DIM10=?	 ");

		Object[] objs = new Object[14];
		objs[0] = param.get("indexNo");
		objs[1] = param.get("dataDate");
		objs[2] = param.get("orgNo");
		objs[3] = param.get("currency");
		objs[4] = param.get("dim1");
		objs[5] = param.get("dim2");
		objs[6] = param.get("dim3");
		objs[7] = param.get("dim4");
		objs[8] = param.get("dim5");
		objs[9] = param.get("dim6");
		objs[10] = param.get("dim7");
		objs[11] = param.get("dim8");
		objs[12] = param.get("dim9");
		objs[13] = param.get("dim10");
		List<Map<String, Object>> idxs = this.jdbcBaseDAO.find(buff.toString(), objs);
		return idxs.size();
	}
	private Integer getRptIdxResultByParam(Map<String,Object> param,String ds){
		StringBuilder buff = new StringBuilder();
		/*
		buff.append("SELECT 1 FROM ").append(ds).append(".").append("RPT_IDX_RESULT  WHERE	INDEX_NO=#{indexNo} AND ")
		.append("  DATA_DATE=#{dataDate} AND		ORG_NO=#{orgNo} AND		CURRENCY=#{currency} AND		DIM1=#{dim1} AND ")
		.append("  DIM2=#{dim2} AND		DIM3=#{dim3} AND		DIM4=#{dim4} AND		DIM5=#{dim5} AND		DIM6=#{dim6} AND ")
		.append("  DIM7=#{dim7} AND		DIM8=#{dim8} AND		DIM9=#{dim9} AND		DIM10=#{dim10}	 ");
*/
		buff.append("SELECT 1 FROM ").append(StringUtils.isEmpty(ds)?"":(ds+".")).append("RPT_IDX_RESULT  WHERE	INDEX_NO=?  AND ")
		.append("  DATA_DATE=?  AND		ORG_NO=? AND		CURRENCY=? AND		DIM1=? AND ")
		.append("  DIM2=? AND		DIM3=? AND		DIM4=? AND		DIM5=? AND		DIM6=? AND ")
		.append("  DIM7=? AND		DIM8=? AND		DIM9=? AND		DIM10=?	 ");
		Object[] objs = new Object[14];
		objs[0] = param.get("indexNo");
		objs[1] = param.get("dataDate");
		objs[2] = param.get("orgNo");
		objs[3] = param.get("currency");
		objs[4] = param.get("dim1");
		objs[5] = param.get("dim2");
		objs[6] = param.get("dim3");
		objs[7] = param.get("dim4");
		objs[8] = param.get("dim5");
		objs[9] = param.get("dim6");
		objs[10] = param.get("dim7");
		objs[11] = param.get("dim8");
		objs[12] = param.get("dim9");
		objs[13] = param.get("dim10");

		List<Map<String, Object>> idxs = this.jdbcBaseDAO.find(buff.toString(), objs);
		//Integer rs = jdbcBaseDAO.findObject(buff.toString(), objs, Integer.class);
		
		return idxs.size();
	}

	private Map<String, Object> getQueryMap(Map<String, Object> rs) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("currency", rs.get("CURRENCY"));
		params.put("dataDate", rs.get("DATA_DATE"));
		params.put("indexVal", rs.get("INDEX_VAL"));
		params.put("indexNo", rs.get("INDEX_NO"));
		params.put("orgNo", rs.get("ORG_NO"));
		params.put("dim1", rs.get("DIM1"));
		params.put("dim2", rs.get("DIM2"));
		params.put("dim3", rs.get("DIM3"));
		params.put("dim4", rs.get("DIM4"));
		params.put("dim5", rs.get("DIM5"));
		params.put("dim6", rs.get("DIM6"));
		params.put("dim7", rs.get("DIM7"));
		params.put("dim8", rs.get("DIM8"));
		params.put("dim9", rs.get("DIM9"));
		params.put("dim10", rs.get("DIM10"));
		return params;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object getResultObject(Class c, Map<String, Object> map) {

		try {
			Object obj = c.newInstance();
			Field[] fields = c.getDeclaredFields();
			for (Field f : fields) {
				if (f.isAnnotationPresent(Column.class)) {
					String fName = f.getName();
					Method m = c.getDeclaredMethod(
							"set" + fName.substring(0, 1).toUpperCase()
									+ fName.substring(1), f.getType());
					String dbField = (f.getAnnotation(Column.class)).name();
					Object value = map.get(dbField);
					if (f.getType().getName().equals("java.math.BigDecimal")) {
						m.invoke(obj, value);
					} else
						m.invoke(obj, value);
				}
			}
			return obj;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void exportData(HttpServletResponse response, String jsonObj,
			String realPath) {
		List<DataInputExportTableVO> voList = Lists.newArrayList();
		JSONArray jnArray = JSON.parseArray(jsonObj);
		for (int i = 0; i < jnArray.size(); i++) {
			DataInputExportTableVO vo = new DataInputExportTableVO();
			String sheetName = "";
			JSONObject jnObj = (JSONObject) jnArray.get(i);

			JSONArray jnColumn = (JSONArray) jnObj.get("columnInfo");
			LinkedHashMap<String, String> columnInfo = Maps.newLinkedHashMap();
			for (int j = 0; j < jnColumn.size(); j++) {
				columnInfo.put(j + "", jnColumn.getString(j));
			}
			vo.setColumnInfo(columnInfo);

			List<Map<String, Object>> infoList = Lists.newArrayList();
			JSONArray jaInfo = (JSONArray) jnObj.get("infoList");
			for (int j = 0; j < jaInfo.size(); j++) {
				Map<String, Object> info = Maps.newHashMap();
				JSONArray tmpArray = jaInfo.getJSONArray(j);
				for (int k = 0; k < tmpArray.size(); k++) {
					if (k == 0)
						sheetName = tmpArray.getString(k);
					info.put(k + "", tmpArray.getString(k));
				}
				infoList.add(info);
				// String[] jnCells = jaInfo.getString(j).split(",");
				// for(int k = 0 ; k <jnCells.length;k++)
				// info.put(k+"", jnCells[k]);
				// infoList.add(info);
			}
			vo.setSheetName(sheetName);
			vo.setInfoList(infoList);
			voList.add(vo);
		}
		File file = new File(realPath + "/export/frame/datainput/");
		if (!file.exists()) {
			file.mkdirs();
		}
		String filePath = realPath + "/export/frame/datainput/"
				+ RandomUtils.uuid2() + ".xls";
		System.out.println("=============================       exportData"
				+ filePath);
		TaskXlsExcelExportUtil util = new TaskXlsExcelExportUtil(response,
				voList, filePath);
		util.exportFile();
	}

	// 获取excel工作薄
	private Workbook getWorkBook(String fileName) {
		String ex = fileName.substring(fileName.lastIndexOf(".") + 1);
		Workbook workbook = null;
		try {
			if ("xls".equals(ex)) {
				workbook = new HSSFWorkbook(new FileInputStream(fileName));
			} else if ("xlsx".equals(ex)) {
				workbook = new XSSFWorkbook(new FileInputStream(fileName));
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return workbook;
	}
	
	/**
	 * 导入EXCEL的数据
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> dealExcel(String fileName) throws Exception {

		Workbook dataWB = getWorkBook(fileName);
		int sheetIndex = 0;
		List<Map<String, Object>> valueList = Lists.newArrayList();
		int activeSheetIndex  =  dataWB.getActiveSheetIndex();
		while (sheetIndex<=activeSheetIndex) {
			Sheet sheet = dataWB.getSheetAt(sheetIndex);

			Row firstRow = sheet.getRow(0);
			int lastCellNum = firstRow.getLastCellNum()-1;
			int firstRowNum = sheet.getFirstRowNum();
			int lastRowNum = sheet.getLastRowNum();
			// 读取配置Sheet放入map
			for (int i = firstRowNum + 1; i <= lastRowNum; i++) {
				Map<String, Object> valueMap = Maps.newHashMap();
				Row row = sheet.getRow(i);
				if (row != null && row.getCell(lastCellNum) != null) {
					double cellValue = row.getCell(lastCellNum)
							.getNumericCellValue();
					StringBuilder buff = new StringBuilder();
					for (int cellNum = 0; cellNum < lastCellNum; cellNum++) {
							if (cellNum != 0)
								buff.append("_");
							if(row.getCell(cellNum)!=null){
							buff.append(row.getCell(cellNum).getStringCellValue());
						}
					}
					//valueMap.put(buff.toString(), cellValue);
					valueMap.put("key", buff.toString());
					valueMap.put("value",cellValue);

					valueList.add(valueMap);
				}
			}
			sheetIndex++;
		}
		return valueList;
	}
	public List<CommonTreeNode> getAsyncTreeWithIdxType(String contextPath, String upId, String indexType,String childCatalogs){
		if(upId == null )
			return getAllDataInputRootCatalog( contextPath, indexType);
		else{
			List<CommonTreeNode>  nodeList= Lists.newArrayList();
			nodeList.addAll(getChildCatalog(contextPath,upId,childCatalogs));
			nodeList.addAll(getChildIdx( contextPath,   upId,  indexType));
			return nodeList;
		}
	}
	private List<CommonTreeNode>getChildCatalog(String contextPath,String upId,String childCatalogs){

		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		Map<String, Object> params = Maps.newHashMap();
		params.put("catalogNos", StringUtils.isEmpty(childCatalogs)?null: Arrays.asList( childCatalogs.split(",")));
		params.put("upNo", upId);
		List<RptIdxCatalog> idxCatalogList = this.rptTmpDataDAO.getCatalogIdx(params);
		for (RptIdxCatalog tmp : idxCatalogList) {
			CommonTreeNode catalog = new CommonTreeNode();
			catalog.setId(tmp.getIndexCatalogNo());
			catalog.setText(tmp.getIndexCatalogNm());
			catalog.setData(tmp);
			catalog.setIsParent(true);
			catalog.setUpId(tmp.getIndexCatalogNo());
			catalog.setIcon(contextPath+ GlobalConstants4frame.LOGIC_MODULE_ICON);
			catalog.getParams().put("nodeType", "idxCatalog");
			catalog.getParams().put("childCatalogs",childCatalogs);
			resultList.add(catalog);
		}
		return resultList;
	}
	
	private List<CommonTreeNode>getChildIdx(String contextPath,String upId, String indexType){

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("indexCatalogNo", upId);
		condition.put("needOrderByNm", true);
		condition.put("indexType", indexType);
		List<RptIdxInfo> idxList = this.idxDao.listIdxInfo(condition);

		List<CommonTreeNode>resultList = Lists.newArrayList();
		for (RptIdxInfo tmp : idxList) {
			CommonTreeNode idxNode = new CommonTreeNode();
			idxNode.setId(tmp.getId().getIndexNo());
			idxNode.setText(tmp.getIndexNm());
			idxNode.setData(tmp);
			idxNode.setUpId(tmp.getId().getIndexNo());
			idxNode.setIcon(contextPath
					+ "/images/classics/menuicons/grid.png");
			idxNode.getParams().put("nodeType", "idxInfo");
			idxNode.getParams().put("indexNo",
					tmp.getId().getIndexNo());
			idxNode.getParams().put("indexVerId",
					String.valueOf((tmp.getId().getIndexVerId())));
			idxNode.getParams().put("isSum", tmp.getIsSum());
			idxNode.setIsParent(false);
			resultList.add(idxNode);
		}
		return resultList;
	}
	
	private List<CommonTreeNode>getAllDataInputRootCatalog(String contextPath,String indexType){

		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("indexType", indexType);
		List<RptIdxInfo> idxList = this.idxDao.listIdxInfo(condition);
		Map<String,List<String>>childCatalogMap = Maps.newHashMap();
		for(RptIdxInfo idx:idxList){
			if(!childCatalogMap.keySet().contains(idx.getIndexCatalogNo()))
			{
				List<String>logList = Lists.newArrayList();
				logList.add(idx.getIndexCatalogNo());
				childCatalogMap.put(idx.getIndexCatalogNo(), logList);
			}
		}
		return  getUpCatalog(contextPath,  childCatalogMap);
	}
	
	private List<CommonTreeNode>getUpCatalog(String contextPath,Map<String,List<String>> catalogIdMap){
		
		Map<String,List<String>> idxMap = Maps.newHashMap();
		for(String catalogId:catalogIdMap.keySet()){
			List<String> list = Lists.newArrayList();
			List<String> allCatalogIds = getUpCatalogByCatalogId(catalogId,list);
			idxMap.put(allCatalogIds.get(0), allCatalogIds);
		}
		
		Map<String,List<String>> tempMap = Maps.newHashMap();
		for(List<String>catatmpList:idxMap.values()){
			String firstRoot = catatmpList.get(catatmpList.size()-1);
			if(tempMap.containsKey(firstRoot)){
				List<String>list = tempMap.get(firstRoot);
				list.addAll(catatmpList);
			}else{
				List<String>list = Lists.newArrayList();
				list.addAll(catatmpList);
				tempMap.put(firstRoot, list);
			}
		}
//		catalogIdList.addAll(idxMap.keySet());
		Map<String, Object> params = Maps.newHashMap();
		params.put("catalogNos", tempMap.keySet());
		List<RptIdxCatalog> idxCatalog = this.rptTmpDataDAO.getCatalogIdx(params);
		List<CommonTreeNode>resultList = Lists.newArrayList();
		for(RptIdxCatalog tmp:idxCatalog){
			CommonTreeNode catalog = new CommonTreeNode();
			catalog.setId(tmp.getIndexCatalogNo());
			catalog.setText(tmp.getIndexCatalogNm());
			catalog.setData(tmp);
			catalog.setIsParent(true);
			catalog.setUpId(tmp.getIndexCatalogNo());
			catalog.setIcon(contextPath
					+ GlobalConstants4frame.LOGIC_MODULE_ICON);
			catalog.getParams().put("nodeType", "idxCatalog");
			StringBuilder buff = new StringBuilder();
			List<String> list = tempMap.get(tmp.getIndexCatalogNo());
			if(list!=null && list.size()>0) {
				for(int i=0;i<list.size();i++){
					if(i!=0)
						buff.append(",");
					buff.append(list.get(i));
				}
			}
			catalog.getParams().put("childCatalogs",  buff.toString() );
			resultList.add(catalog);
		}
		return resultList;
	}
	
	public List<String> getUpCatalogByCatalogId(String childCatalogId,List<String>resultList){
		List<String> childList = Lists.newArrayList();
		childList.add(childCatalogId);
		Map<String, Object> params = Maps.newHashMap();
		params.put("catalogNos", childList);
		RptIdxCatalog idxCatalog = this.rptTmpDataDAO
				.getCatalogIdx(params).get(0);
		
		resultList.add(idxCatalog.getIndexCatalogNo());
		
		if(idxCatalog.getUpNo()!=null&&!idxCatalog.getUpNo().equals("0"))
			return getUpCatalogByCatalogId(idxCatalog.getUpNo(),resultList);
		else return resultList;
		
	}

	public List<CommonTreeNode> getRuleTree(String contextPath,String indexNo){
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		
		Map<String,Object>params=Maps.newHashMap();
		params.put("indexNo", indexNo);
		
		List<RptInputIdxValidate> rptInputIdxValidateList = this.idxDataInputDao
				.getRuleByIndexNo(params);

		for (RptInputIdxValidate rptInputIdxValidate : rptInputIdxValidateList) {
			CommonTreeNode catalog = new CommonTreeNode();
			catalog.setId(rptInputIdxValidate.getRuleId());
			catalog.setText(rptInputIdxValidate.getRuleNm());
			catalog.setData(rptInputIdxValidate);
			catalog.setIsParent(false);
			catalog.setUpId("0");
			catalog.setIcon(contextPath
					+ GlobalConstants4frame.DATA_TREE_NODE_ICON_REPORT);
			resultList.add(catalog);
		}
		return resultList;
	}
	
	private void checkSql(RptInputIdxValidate rptInputIdxValidate) throws BioneMessageException{
		
			getValueBySql(rptInputIdxValidate.getSourceId(),rptInputIdxValidate.getSqlExpression());
	}
	
	public String saveRptInputIdxValidate(RptInputIdxValidate rptInputIdxValidate){
		
		try{
			if(rptInputIdxValidate.getRuleType().equals("1"))
			{
				checkSql(rptInputIdxValidate);
			}
			if(StringUtils.isEmpty(rptInputIdxValidate.getRuleId())){
				rptInputIdxValidate.setRuleId( RandomUtils.uuid2());
				idxDataInputDao.saveRptInputIdxValidate(rptInputIdxValidate);
			}else
				idxDataInputDao.updateRptInputIdxValidate(rptInputIdxValidate);
				
			
			
			return "";
		}catch(BioneMessageException e ){
			return "sql语句错误,请检查";
		}
		catch(Exception ex){
			ex.printStackTrace();
			return "保存出错 ,错误信息:"+ex.getMessage();
		}
	}

	
	public void deleteRptInputIdxValidate(String ruleId){
			idxDataInputDao.deleteRptInputIdxValidate(ruleId);
	}
	
}
