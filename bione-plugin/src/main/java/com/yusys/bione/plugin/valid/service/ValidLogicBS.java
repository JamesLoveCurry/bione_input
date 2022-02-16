package com.yusys.bione.plugin.valid.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.excel.AbstractExcelImport;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.ExcelImporter;
import com.yusys.bione.frame.excel.XlsExcelTemplateExporter;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaFunc;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaSymbol;
import com.yusys.bione.plugin.rptidx.repository.IdxDimRelMybatisDao;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptmgr.repository.RptMgrInfoMybatisDao;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import com.yusys.bione.plugin.valid.check.ValidateException;
import com.yusys.bione.plugin.valid.entitiy.*;
import com.yusys.bione.plugin.valid.repository.FunAndSymbolMybatisDao;
import com.yusys.bione.plugin.valid.repository.ValidLogicMybatisDao;
import com.yusys.bione.plugin.valid.repository.ValidLogicRelMybatisDao;
import com.yusys.bione.plugin.valid.utils.ValidateExpressionUtils;
import com.yusys.bione.plugin.valid.web.vo.CfgextLogicVO;
import com.yusys.bione.plugin.valid.web.vo.NameAndIdMap;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ValidLogicBS extends BaseBS<RptValidCfgextLogic>{

	@Autowired
	private ValidLogicMybatisDao logicDao;
	
	@Autowired
	private ValidLogicRelMybatisDao relDao;
	
	@Autowired
	private FunAndSymbolMybatisDao funAndSymbolMybatisDao;
	
	@Autowired
	private IdxDimRelMybatisDao dimRelDao;
	
	@Autowired
	private RptMgrInfoMybatisDao reportInfoDAO;
	
	@Autowired
	private RptValidLogicIdxRelBS rptValidLogicIdxRelBS;
	
	@Autowired
	private RptOrgInfoBS rptOrgInfoBS;
	
	public static String IS_YES = "是";
	
	public List<RptValidCfgextLogic> getAllExpression(){
		String jql = "select logic from RptValidCfgextLogic logic";
		List<RptValidCfgextLogic> list = this.baseDAO.findWithIndexParam(jql);
		return list;
	}
	
	public Map<String, Object> list(Pager pager, String templateId, String srcIdxNo, String dataDate, String busiType) {

		if(StringUtils.isBlank(templateId) && StringUtils.isBlank(srcIdxNo)) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(templateId)){
			map.put("rptTemplateId", templateId);
		}
		if(StringUtils.isNotBlank(busiType)){
			map.put("busiType", busiType);
		}
		if(StringUtils.isNotBlank(srcIdxNo)) {
			map.put("indexNo", srcIdxNo);
		}
		if(StringUtils.isNotBlank(dataDate)) {
			map.put("dataDate", dataDate);
		}
		String condition = pager.getCondition();
		if(StringUtils.isBlank(condition) || condition.indexOf("logic.endDate") < 0) {
			map.put("endDate", "29991231");
		}
		if(StringUtils.isNotBlank(condition)) {
			JSONObject obj = JSONObject.parseObject(condition);
			JSONArray array = JSONArray.parseArray(obj.getString("rules"));
			for(int i = 0; i < array.size(); i++){
				JSONObject job = array.getJSONObject(i);
				if(job.getString("field").equalsIgnoreCase("logic.endDate")){
					map.put("startDate", job.getString("value"));
				} else if (job.getString("field").equalsIgnoreCase("expressionDesc")){
					map.put("expressionDesc", "%"+job.getString("value")+"%");
				} else if (job.getString("field").equalsIgnoreCase("logic.checkId")){
					map.put("checkId", job.getString("value"));
				} else if (job.getString("field").equalsIgnoreCase("logic.isOrgFilter")){
					map.put("isOrgFilter", job.getString("value"));
				} else if (job.getString("field").equalsIgnoreCase("logic.checkType")){
					map.put("checkType", job.getString("value"));
				} else if (job.getString("field").equalsIgnoreCase("logic.checkSrc")){
					map.put("checkSrc", job.getString("value"));
				}
			}
		} else {
			pager.setCondition(StringUtils.replace(pager.getCondition(), "'", "''"));
		}
		//查询当前用户的所有下级机构
		List<String> orgList = new ArrayList<String>();
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			orgList.addAll(authOrgNos);
			for(String authOrgNo : authOrgNos){
				// 机构权限 修改为用户能看到自己及下级机构的任务
				orgList.addAll(rptOrgInfoBS.getAllChildOrgNo(authOrgNo, busiType, true));
			}
			map.put("orgNos", SplitStringBy1000.change(orgList));
		}
//		PageHelper.startPage(pager);
//		PageMyBatis<CfgextLogicVO> page = (PageMyBatis<CfgextLogicVO>) this.logicDao.list(map);
		List<CfgextLogicVO> pageResult = this.logicDao.list(map);
		//拼接过滤机构
		Map<String, CfgextLogicVO> resultMap = new HashMap<>();
		for (CfgextLogicVO cfgextLogicVO : pageResult) {
			if(resultMap.containsKey(cfgextLogicVO.getCheckId())){
				CfgextLogicVO cfgextLogicVO1 = resultMap.get(cfgextLogicVO.getCheckId());
				cfgextLogicVO1.setCheckOrg(cfgextLogicVO1.getCheckOrg()+","+cfgextLogicVO.getCheckOrg());
			} else {
				resultMap.put(cfgextLogicVO.getCheckId(), cfgextLogicVO);
			}
		}
		List<CfgextLogicVO> rows = resultMap.values().stream().collect(Collectors.toList());
		int startIndex = pager.getPageFirstIndex();
		int endIndex = startIndex + pager.getPagesize();
		if(endIndex > rows.size()){
			endIndex = rows.size();
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("Rows", rows.subList(startIndex, endIndex));
		result.put("Total", rows.size());
		return result;
	}

	public List<RptIdxFormulaFunc> getFuncAll() {
		Map<String, Object> map = new HashMap<String, Object>();
		return this.funAndSymbolMybatisDao.listFunc(map);
	}
	public List<RptIdxFormulaSymbol> getSymbolAll() {
		Map<String, Object> map = new HashMap<String, Object>();
		return this.funAndSymbolMybatisDao.listSymbol(map);
	}

	@Transactional(readOnly = false)
	public void saveLogic(RptValidCfgextLogic logic, String templateId, String leftFormulaIndex, 
			String rightFormulaIndex,String leftFormulaDs,String rightFormulaDs, String busiType, String orgNo) {
		if(StringUtils.isBlank(templateId)) {
			return;
		}
//		// 根据用户机构获取报送机构
//		String jql = "select org_no, org_nm from RPT_ORG_INFO where ORG_TYPE = ?0 and mgr_org_no = ?1";
//		List<Object[]> orgNos = this.baseDAO.findByNativeSQLWithIndexParam(jql, busiType, BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
//		Object[] orgs = orgNos.get(0);
		if(logic.getCheckId() != null && !logic.getCheckId().equals("")){//修改
			Map<String, Object> map = new HashMap<String, Object>();
			List<String> idList = new ArrayList<String>();
			idList.add(logic.getCheckId());
			map.put("ids", idList);
			this.logicDao.deleteLogic(map);
			this.logicDao.insertLogic(logic);
			// 如果是机构过滤，过滤机构存储当前登录人机构号
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("checkId", logic.getCheckId());
			this.logicDao.deleteRptValidLogicOrg(params);
		}else{
			String checkId = RandomUtils.uuid2();
			logic.setCheckId(checkId);
			this.logicDao.insertLogic(logic);
		}
		//批量插入
		if(GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(logic.getIsOrgFilter())){
			String[] checkOrgs = orgNo.split(",");
			List<Map<String, Object>> rptValidLogicOrgList = new ArrayList<>();
			for (String checkOrg : checkOrgs) {
				Map<String, Object> rptValidLogicOrg = new HashMap<>();
				rptValidLogicOrg.put("checkId", logic.getCheckId());
				rptValidLogicOrg.put("templateId", templateId);
				rptValidLogicOrg.put("startDate", logic.getStartDate());
				rptValidLogicOrg.put("endDate", logic.getEndDate());
				rptValidLogicOrg.put("checkOrg", checkOrg);
				rptValidLogicOrgList.add(rptValidLogicOrg);
			}
			this.logicDao.batchInsertRptValidLogicOrg(rptValidLogicOrgList);
		}
		
		//先删除所有检验公式和报表的关系
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("checkId", logic.getCheckId());
		this.relDao.deleteRel(condition);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("checkId", logic.getCheckId());
		//先删除所有检验公式和指标的关系
		this.logicDao.deleteLogicRel(map);
		//先删除所有检验公式和数据源的关系
		this.logicDao.deleteLogicDsRel(map);
		
		//保存逻辑校验和指标的关系表
		String leftIdxNos[] = StringUtils.split(leftFormulaIndex, ',');
		String rightIdxNos[] = StringUtils.split(rightFormulaIndex, ',');
		String leftDsNos[] = StringUtils.split(leftFormulaDs, ',');
		String rightDsNos[] = StringUtils.split(rightFormulaDs, ',');
		
		
		//保存逻辑校验和报表的关系
		RptValidLogicRptRel rptRel = new RptValidLogicRptRel();
		RptValidLogicRptRelPK rptRelPk = new RptValidLogicRptRelPK();
		rptRelPk.setCheckId(logic.getCheckId());
		rptRelPk.setRptTemplateId(templateId);
		rptRel.setId(rptRelPk);
		this.relDao.saveRel(rptRel);
		
		if(!StringUtils.isEmpty(leftFormulaIndex)){
			for(String tmp : leftIdxNos){
				RptValidLogicIdxRelPK pk = new RptValidLogicIdxRelPK();
				RptValidLogicIdxRel rel = new RptValidLogicIdxRel();
				pk.setCheckId(logic.getCheckId());
				pk.setIndexNo(tmp);
				pk.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_LEFT);
				pk.setTemplateId(templateId);
				rel.setId(pk);
				this.logicDao.insertLogicRel(rel);
			}
		}
		
		if(!StringUtils.isEmpty(rightFormulaIndex)){
			for(String tmp : rightIdxNos){
				RptValidLogicIdxRelPK pk = new RptValidLogicIdxRelPK();
				RptValidLogicIdxRel rel = new RptValidLogicIdxRel();
				pk.setCheckId(logic.getCheckId());
				pk.setIndexNo(tmp);
				pk.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_RIGHT);
				pk.setTemplateId(templateId);
				rel.setId(pk);
				this.logicDao.insertLogicRel(rel);
			}
		}
		
		if(!StringUtils.isEmpty(leftFormulaDs)){
			for(String tmp : leftDsNos){
				String dsNos[] = StringUtils.split(tmp,"-");
				RptValidLogicDsRelPK pk = new RptValidLogicDsRelPK();
				RptValidLogicDsRel rel = new RptValidLogicDsRel();
				pk.setCheckId(logic.getCheckId());
				pk.setColumnId(dsNos[0]);
				pk.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_LEFT);
				pk.setTemplateId(templateId);
				pk.setColumnType(dsNos[1]);
				if(dsNos.length>2)
					pk.setDimFilter(dsNos[2]);
				else
					pk.setDimFilter("");
				rel.setId(pk);
				this.logicDao.insertLogicDsRel(rel);
			}
		}
		
		if(!StringUtils.isEmpty(rightFormulaDs)){
			for(String tmp : rightDsNos){
				String dsNos[] = StringUtils.split(tmp,"-");
				
				RptValidLogicDsRelPK pk = new RptValidLogicDsRelPK();
				RptValidLogicDsRel rel = new RptValidLogicDsRel();
				pk.setCheckId(logic.getCheckId());
				pk.setColumnId(dsNos[0]);
				pk.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_LEFT);
				pk.setTemplateId(templateId);
				pk.setColumnType(dsNos[1]);
				if(dsNos.length>2)
					pk.setDimFilter(dsNos[2]);
				else
					pk.setDimFilter("");
				rel.setId(pk);
				
				this.logicDao.insertLogicDsRel(rel);
			}
		}
		
	}

	public CfgextLogicVO getInfo(String checkId, String orgType) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("checkId", checkId);
		condition.put("orgType", orgType);
		List<CfgextLogicVO> list = this.logicDao.listLogic(condition);
		if(list == null || list.size() == 0){
			return null;
		}
		CfgextLogicVO cfgextLogicVO = list.get(0);
		List<String> checkOrgs = list.stream().map(item -> item.getCheckOrg()).collect(Collectors.toList());
		List<String> orgNos = list.stream().map(item -> item.getOrgNo()).collect(Collectors.toList());
		List<String> orgNms = list.stream().map(item -> item.getOrgNm()).collect(Collectors.toList());

		cfgextLogicVO.setCheckOrg(StringUtils.join(checkOrgs,","));
		cfgextLogicVO.setOrgNo(StringUtils.join(orgNos,","));
		cfgextLogicVO.setOrgNm(StringUtils.join(orgNms,","));
		return cfgextLogicVO;
		
	}

	/**
	 * 根据模板id和校验id删除检验公式
	 * @param checkIds
	 * @param templateId
	 */
	@Transactional(readOnly = false)
	public void delete(String checkIds, String templateId) {
		if (StringUtils.isNotBlank(templateId)) {
			List<String> idList = new ArrayList<String>();
			Map<String, Object> condition = new HashMap<String, Object>();
			if(StringUtils.isNotBlank(checkIds)) {
				if (checkIds.endsWith(",")) {
					checkIds = checkIds.substring(0, checkIds.length() - 1);
				}
				String id[] = StringUtils.split(checkIds, ',');
				idList = Arrays.asList(id);
			}else {
				condition.put("templateId", templateId);
				idList = logicDao.findCheckIds(condition);
			}
			if(null != idList && idList.size() > 0) {
				//进行1000为一个单元的分割
				List<List<String>> idLists = SplitStringBy1000.change(idList);
				for(List<String> idListBy1000 : idLists) {
					condition.clear();
					condition.put("ids", idListBy1000);
					//删除检验信息表
					this.logicDao.deleteLogic(condition);
					//删除逻辑校验和指标的关系表
					this.logicDao.deleteLogicRel(condition);
					//删除校验的报表的关系表
					this.logicDao.deleteLogicRptRel(condition);
				}
			}
		}
	}

	/**
	 * 根据模板id和结束日期，删除对应的校验公式
	 * @param templateId
	 * @param endDate
	 */
	@Transactional(readOnly = false)
	public void deleteByVer(String templateId, String endDate) {
		if (StringUtils.isNotBlank(templateId) && StringUtils.isNotBlank(endDate)) {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("templateId", templateId);
			condition.put("endDate", endDate);
			List<String> checkIds = logicDao.findCheckIds(condition);
			if(null != checkIds && checkIds.size() > 0) {
				//进行1000为一个单元的分割
				List<List<String>> idLists = SplitStringBy1000.change(checkIds);
				for(List<String> idListBy1000 : idLists) {
					condition.clear();
					condition.put("ids", idListBy1000);
					//删除检验信息表
					this.logicDao.deleteLogic(condition);
					//删除逻辑校验和指标的关系表
					this.logicDao.deleteLogicRel(condition);
					//删除校验的报表的关系表
					this.logicDao.deleteLogicRptRel(condition);
				}
			}
		}
	}
	
	public List<CommonTreeNode> getSymbolTree() {
		List<RptIdxFormulaSymbol> list = this.getSymbolAll();
		List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId("0");
		rootNode.setText("运算符");
		rootNode.setIsParent(true);
		rootNode.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_MODULE_ICON);
		
		for(RptIdxFormulaSymbol symbol:list){
			CommonTreeNode tmp = new CommonTreeNode();
			tmp.setId(symbol.getSymbolId());
			tmp.setText(symbol.getSymbolNm());
			tmp.setUpId("0");
			tmp.setIsParent(false);
			Map<String, String> map = new HashMap<String, String>();
			map.put("display", symbol.getSymbolDisplay());
			map.put("type", "symbol");
			tmp.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + "/images/classics/menuicons/grid.png");
			tmp.setParams(map);
			childList.add(tmp);
		}
		rootNode.setChildren(childList);
		rootNode.getParams().put("type", "symbol");
		List<CommonTreeNode> result = new ArrayList<CommonTreeNode>();
		result.add(rootNode);
		return result;
	}

	public List<CommonTreeNode> getFuncTree() {
		List<RptIdxFormulaFunc> list = this.getFuncAll();
		List<CommonTreeNode> childList = new ArrayList<CommonTreeNode>();
		CommonTreeNode rootNode = new CommonTreeNode();
		rootNode.setId("0");
		rootNode.setText("函数");
		rootNode.setIsParent(true);
		rootNode.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + GlobalConstants4frame.LOGIC_MODULE_ICON);
		
		for(RptIdxFormulaFunc func:list){
			CommonTreeNode tmp = new CommonTreeNode();
			tmp.setId(func.getFormulaId());
			tmp.setText(func.getFormulaNm());
			tmp.setUpId("0");
			tmp.setIsParent(false);
			tmp.setIcon(GlobalConstants4frame.APP_CONTEXT_PATH + "/images/classics/menuicons/grid.png");
			Map<String, String> map = new HashMap<String, String>();
			map.put("display", func.getFormulaDisplay());
			map.put("type", "func");
			tmp.setParams(map);
			childList.add(tmp);
		}
		rootNode.setChildren(childList);
		rootNode.getParams().put("type", "func");
		List<CommonTreeNode> result = new ArrayList<CommonTreeNode>();
		result.add(rootNode);
		return result;
	}
	
	public Map<String, String> replaceModel(String expression){
		Map<String, String> map = new HashMap<String, String>();
		
		Pattern modelPattern = Pattern.compile("Sql\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[{]|[}]|[ ]|\\[|\\]|\\.|[(]|[)]|[-]|[;]|'|\\)|,|\\()+'\\)");
		
		Set<String> set = new HashSet<String>();
		Matcher modelMatcher = modelPattern.matcher(expression);
		boolean isMatcher = false;
		while (modelMatcher.find()) {
			isMatcher = true;
			String s = modelMatcher.group(0);
			set.add(s.substring(s.indexOf("'") + 1, s.indexOf("'", s.indexOf("'") + 1)));
		}
		if(!isMatcher){
			map.put("expression", expression);
			return map;
		}
		
		String jql = "select col.dimTypeNo, info.setNm, info.sourceId,info.tableEnNm, col.enNm from RptSysModuleCol col, RptSysModuleInfo info "
				+ "where col.setId = info.setId and info.setNm in (?0) and col.colType=?1";
		List<Object[]> list = this.baseDAO.findWithIndexParam(jql, set, GlobalConstants4plugin.COL_TYPE_DIM);
		
		StringBuffer sb = new StringBuffer();
		modelMatcher = modelPattern.matcher(expression);
		while (modelMatcher.find()) {
			List<Object[]> setList = new ArrayList<Object[]>();
			String s = modelMatcher.group(0);
			String setNm = s.substring(s.indexOf("'") + 1, s.indexOf("'", s.indexOf("'") + 1));
			
			String dsId = "";
			String tableNm = "";
			String dateCol = "";
			String orgCol = "";
			boolean flag = false;
			for(int i=0;i<list.size();i++){
				Object[] ob = list.get(i);
				if(ob[1].toString().equals(setNm)){
					flag = true;
					setList.add(ob);
					dsId = ob[2].toString();
					tableNm = ob[3].toString();
					if(ob[0].toString().equals(GlobalConstants4plugin.DIM_TYPE_DATE_NAME)){
						dateCol = ob[4].toString();
					}
					if(ob[0].toString().equals(GlobalConstants4plugin.DIM_TYPE_ORG_NAME)){
						orgCol = ob[4].toString();
					}
				}
			}
			if(!flag){
				map.put("message", "数据模型[" + setNm  + "]不存在");
				return map;
			}
			
			StringBuilder sql = new StringBuilder( "select ");
			StringBuilder groupBy = new StringBuilder(" group by ");
			StringBuilder where = new StringBuilder(" ");
			
			Pattern p = Pattern.compile("\\[([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[\\[]|[{]|[}]|[\\]]|[ ]|\\.|[(]|[)]|[-]|[;]|'|\\)|,|\\()+\\]");
			Matcher m = p.matcher(s);
			List<Map<String, String>> colAndDimArray = new ArrayList<Map<String,String>>();
			int count = 0;
			while(m.find()){
				String col = m.group(0);
				String[] cols = StringUtils.split(col, "],[");
				for(String tmp : cols){
					tmp = StringUtils.remove(tmp, '[');
					tmp = StringUtils.remove(tmp, ']');
					
					if(tmp.contains("{") || !tmp.contains("(")){//维度
						String colName = tmp.substring(0, tmp.indexOf("{"));
						sql.append( colName + ",");
						groupBy.append(colName + ",");
						if(tmp.contains("{")){
							String filter[] = StringUtils.split(tmp.substring(tmp.indexOf("{") + 1, tmp.indexOf("}")), ',');
							StringBuilder filterString = new StringBuilder("");
							for(String a: filter){
								filterString.append("'" + a + "',");
							}
							where.append(" and " + colName + " in (" + filterString.substring(0, filterString.length() -1) + ")");
						}
						for(Object[] ob : setList){
							if(ob[4].toString().equals(colName)){
								Map<String, String> vo = new HashMap<String, String>();
								vo.put("Key",ob[4].toString());
								vo.put("Val", ob[0].toString());
								colAndDimArray.add(vo);
							}
						}
					}else{
						Map<String, String> vo = new HashMap<String, String>();
						vo.put("Key", "col" + (count));
						vo.put("Val", "");
						colAndDimArray.add(vo);
						sql.append(tmp + " as col"+(count++)+",");
						
					}
				}
				

				
			}
			
			if(sql.charAt(sql.length() - 1) == ','){
				sql.replace(0, sql.length(), sql.substring(0, sql.length() - 1));
			}
			sql.append(" from " + tableNm + " where 1=1 ");
			
			if(!StringUtils.isEmpty(dateCol)){
				sql.append(" and " + dateCol + "=:DATE");
			}
			if(!StringUtils.isEmpty(orgCol)){
				sql.append(" and " + orgCol + "=:ORG");
			}
			
			sql.append(where);
			
			
			if(groupBy.charAt(groupBy.length() - 1) == ','){
				groupBy.replace(0, groupBy.length(), groupBy.substring(0, groupBy.length() - 1));
			}
			
			Map<String, List<Map<String, String>>> jsonMap = new HashMap<String, List<Map<String,String>>>();
			jsonMap.put("Cols", colAndDimArray);
			
			if(groupBy.toString().equals(" group by ")){//没有group by 字句
				modelMatcher.appendReplacement(sb,"Sql('" + dsId + "','" + sql + "',"+ JSON.toJSONString(jsonMap)  + ")");
			}else{
				
				modelMatcher.appendReplacement(sb,"Sql('" + dsId + "','" + sql + groupBy + "','" + JSON.toJSONString(jsonMap) + "')");
			}
			
		}
		modelMatcher.appendTail(sb);
		map.put("expression", sb.toString());
		
		return map;
	}

	public void getRptNums(List<String> rptNums, String expression){
		if(StringUtils.isNotEmpty(expression)){
			String[] strs = StringUtils.splitByWholeSeparator(expression, "I('");
			for(int j=0; j<strs.length; j++){          //从第一位开始
				String[] s = StringUtils.split(strs[j], '.');
				if(!rptNums.contains(s[0])){
					rptNums.add(s[0]);                //不重复的才加入其中
				}
			}
		}
	}
	
		/**
		 * 改变单元格名称
		 * @Title: changeNm
		 * @Description: 
		 * @param oldNm
		 * @param rptNum
		 * @return String  
		 * @throws
		 */
		private String changeNm(String oldNm, String rptNum){
			String newNm = oldNm;
			if(oldNm != "" && oldNm != null
					&& rptNum != "" && rptNum != null){
				if(oldNm.indexOf(rptNum) == 0){
					if(!oldNm.equals(rptNum)){
						newNm = oldNm.substring(rptNum.length() + 1);  //截取指标名称之前的报表编号
					}
				}
			}
			return newNm;
		}
	
		public Map<String, String> replaceModel(String expression,Set<String> SourceColumnSet){
			Map<String, String> map = new HashMap<String, String>();
			
			Pattern modelPattern = Pattern.compile("Sql\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[{]|[}]|[ ]|\\[|\\]|\\.|[(]|[)]|[;]|'|\\)|,|\\()+'\\)");
			
			Set<String> set = new HashSet<String>();
			Matcher modelMatcher = modelPattern.matcher(expression);
			boolean isMatcher = false;
			while (modelMatcher.find()) {
				isMatcher = true;
				String s = modelMatcher.group(0);
				set.add(s.substring(s.indexOf("'") + 1, s.indexOf("'", s.indexOf("'") + 1)));
			}
			if(!isMatcher){
				map.put("expression", expression);
				return map;
			}
			
			String jql = "select col.dimTypeNo, info.setNm, info.sourceId,info.tableEnNm, col.enNm,col.colId from RptSysModuleCol col, RptSysModuleInfo info "
					+ "where col.setId = info.setId and info.setNm in (?0) ";
			List<Object[]> list = this.baseDAO.findWithIndexParam(jql, set);
			
			StringBuffer sb = new StringBuffer();
			modelMatcher = modelPattern.matcher(expression);
			while (modelMatcher.find()) {
				List<Object[]> setList = new ArrayList<Object[]>();
				String s = modelMatcher.group(0);
				String setNm = s.substring(s.indexOf("'") + 1, s.indexOf("'", s.indexOf("'") + 1));
				
				String dsId = "";
				String tableNm = "";
				String dateCol = "";
				String orgCol = "";
				boolean flag = false;
				Map<String,String> colMaps = new HashMap<String, String>();
				for(int i=0;i<list.size();i++){
					Object[] ob = list.get(i);
					if(ob[1].toString().equals(setNm)){
						flag = true;
						setList.add(ob);
						dsId = ob[2].toString();
						tableNm = ob[3].toString();
						if(ob[0].toString().equals(GlobalConstants4plugin.DIM_TYPE_DATE_NAME)){
							dateCol = ob[4].toString();
						}
						if(ob[0].toString().equals(GlobalConstants4plugin.DIM_TYPE_ORG_NAME)){
							orgCol = ob[4].toString();
						}
						colMaps.put(ob[4].toString(),  ob[5].toString());
					}
				}
				if(!flag){
					map.put("message", "数据模型[" + setNm  + "]不存在");
					return map;
				}
				
				StringBuffer sql = new StringBuffer( "select ");
				StringBuffer groupBy = new StringBuffer(" group by ");
				StringBuffer where = new StringBuffer(" ");
				
				Pattern p = Pattern.compile("\\[([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[\\[]|[{]|[}]|[\\]]|[ ]|\\.|[(]|[)]|[;]|'|\\)|,|\\()+\\]");
				Matcher m = p.matcher(s);
				List<Map<String, String>> colAndDimArray = new ArrayList<Map<String,String>>();
				sql.append( orgCol + ",");
				sql.append( dateCol + ",");
				Map<String, String> vo = new HashMap<String, String>();
				vo.put("Key", orgCol);
				vo.put("Val", "ORG");
				colAndDimArray.add(vo);
				vo = new HashMap<String, String>();
				vo.put("Key", dateCol);
				vo.put("Val", "DATE");
				colAndDimArray.add(vo);
				groupBy.append(orgCol + ",");
				groupBy.append(dateCol + ",");
				int count = 0;
				String dimFilter = "";
				String formula ="";
				String colId ="";
				while(m.find()){
					String col = m.group(0);
					String[] cols = StringUtils.split(col, "],[");
					for(String tmp : cols){
						tmp = StringUtils.remove(tmp, '[');
						tmp = StringUtils.remove(tmp, ']');
						
						if(tmp.contains("{") && !tmp.contains("(")){//维度
							String colName = tmp.substring(0, tmp.indexOf("{"));
							
							//sql.append( colName + ",");
							//groupBy.append(colName + ",");
							if(tmp.contains("{")){
								String filter[] = StringUtils.split(tmp.substring(tmp.indexOf("{") + 1, tmp.indexOf("}")), ',');
								StringBuffer filterString = new StringBuffer("");
								for(String a: filter){
									dimFilter += a +",";
									filterString.append("\\\\'" + a + "\\\\'");
								}
								dimFilter=dimFilter.substring(0,dimFilter.length()-1);
								where.append(" and " + colName + " = " + filterString.substring(0, filterString.length()) );
							}
							/*for(Object[] ob : setList){
								if(ob[4].toString().equals(colName)){
									vo = new HashMap<String, String>();
									vo.put("Key",ob[4].toString());
									vo.put("Val", ob[0].toString());
									colAndDimArray.add(vo);
								}
							}*/
						}else{
							String colNm = tmp.substring(tmp.indexOf("(")+1, tmp.indexOf(")"));
							formula = tmp.substring(0,tmp.indexOf("("));
							colId = colMaps.get(colNm);
							
							vo = new HashMap<String, String>();
							vo.put("Key", "col" + (count));
							vo.put("Val", "");
							colAndDimArray.add(vo);
							sql.append(tmp + " as col"+(count++)+",");
						}
					}
					SourceColumnSet.add(colId + "-" + formula+ "-" + dimFilter);
				}
				
				if(sql.charAt(sql.length() - 1) == ','){
					sql.replace(0, sql.length(), sql.substring(0, sql.length() - 1));
				}
				sql.append(" from " + tableNm + " where 1=1 ");
				
				if(!StringUtils.isEmpty(dateCol)){
					sql.append(" and " + dateCol + " =:DATE ");
				}
				if(!StringUtils.isEmpty(orgCol)){
					sql.append(" and " + orgCol + " in(:ORG) ");
				}
				
				sql.append(where);
				
				
				if(groupBy.charAt(groupBy.length() - 1) == ','){
					groupBy.replace(0, groupBy.length(), groupBy.substring(0, groupBy.length() - 1));
				}
				
				Map<String, List<Map<String, String>>> jsonMap = new HashMap<String, List<Map<String,String>>>();
				jsonMap.put("Cols", colAndDimArray);
				
				if(groupBy.toString().equals(" group by ")){//没有group by 字句
					modelMatcher.appendReplacement(sb,"Sql('" + dsId + "','" + sql + "','"+ JSON.toJSONString(jsonMap)  + "')");
				}else{
					modelMatcher.appendReplacement(sb,"Sql('" + dsId + "','" + sql + groupBy + "','" + JSON.toJSONString(jsonMap) + "')");
				}
				
			}
			modelMatcher.appendTail(sb);
			map.put("expression", sb.toString());
			
			return map;
		}
		
	public Map<String, Object> replaceExpression(Boolean isInnerCheck,String leftExpression, String rightExpression, String expression) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		//获取相关报表的rptNum edit by xy
		List<String> rptNums = new ArrayList<String>();
		this.getRptNums(rptNums, leftExpression);
		this.getRptNums(rptNums, rightExpression);
		this.getRptNums(rptNums, expression);
		
		List<String> templateTypeList = new ArrayList<String>();
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_CELL);
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_COM);
		map.put("templateTypeList", templateTypeList);
		map.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_FRS);
		map.put("rptType", GlobalConstants4plugin.RPT_TYPE_DESIGN);
		map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		
		if(rptNums != null && rptNums.size()>0){
			map.put("rptNums", rptNums);
		}
		
		List<NameAndIdMap> list = this.logicDao.selectMapping(map);
		
		Map<String, String> replaceMap = new HashMap<String, String>();
		Map<String, String> leftDescMap = new HashMap<String, String>();
		Map<String, String> rightDescMap = new HashMap<String, String>();
		
		//增加判断是否有表达式的报表和左表达式的报表一致 add by chenl 2016年10月29日
		Map<String,String>rptNmMap=Maps.newHashMap();
		@SuppressWarnings("unused")
		String mainRptNm = null;
		
		String tmpIndexNm = "";
		for(NameAndIdMap tmp : list){
			//edit by xy
			tmpIndexNm = this.changeNm(tmp.getIndexNm(), tmp.getRptNm());  //改变指标名称
			
			if(tmp.getLineId() == null || tmp.getLineId().equals("")){
				//replaceMap.put("I('" + tmp.getRptNm() + ".主模板." + tmp.getIndexNm() + "')", "I('" + tmp.getIndexNo() + "')");
				replaceMap.put("I('" + tmp.getRptNm() + "." + tmpIndexNm + "')", "I('" + tmp.getIndexNo() + "')");
				//左表达式
				leftDescMap.put("I('" + tmp.getRptNm() + "." + tmpIndexNm + "')", "I["+tmpIndexNm+"]");
				rptNmMap.put("I('" + tmp.getRptNm() + "." + tmpIndexNm + "')", tmp.getRptNm());//add by chenl
				//右表达式
				String rightDescStr = "["+ tmp.getRptNm() + "@" +tmpIndexNm+"]"; 
				rightDescMap.put("I('" + tmp.getRptNm() + "." + tmpIndexNm + "')", rightDescStr);
			}else{
				replaceMap.put("I('" + tmp.getRptNm() + "." + tmp.getLineNm() + "." + tmpIndexNm + "')", "I('" + tmp.getIndexNo() + "')");
			}
		}
		
		Pattern pattern = Pattern.compile("I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[:]|[-])+'\\)");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Set<String> leftSourceIdxSet = new HashSet<String>();
		Set<String> rightSourceIdxSet = new HashSet<String>();
		
		Set<String> leftSourceDsSet = new HashSet<String>();
		Set<String> rightSourceDsSet = new HashSet<String>();
		
		if (!StringUtils.isEmpty(leftExpression)) {
			Matcher matcher = pattern.matcher(leftExpression);
			Matcher leftMatcher = pattern.matcher(leftExpression);
			StringBuffer sb = new StringBuffer();
			StringBuffer leftSb = new StringBuffer();
			while (matcher.find()) {
				if (replaceMap.get(matcher.group(0)) == null) {
					resultMap.put("message", "左表达式中第" + (matcher.start() + 1)
							+ "位的指标" + matcher.group(0) + "不存在，请修改！");
					return resultMap;
				}
				leftSourceIdxSet.add(replaceMap.get(matcher.group(0)).substring(3, replaceMap.get(matcher.group(0)).length() - 2));
				matcher.appendReplacement(sb, replaceMap.get(matcher.group(0)));
			}
			matcher.appendTail(sb);
			//构造左表达简式
			while (leftMatcher.find()) {
				String targetNm = leftMatcher.group(0);
				leftMatcher.appendReplacement(leftSb, leftDescMap.get(targetNm));
				mainRptNm = rptNmMap.get(targetNm);
			}
			leftMatcher.appendTail(leftSb);
			//替换数据模型
			Map<String, String> replaceModelMap = replaceModel(sb.toString(),leftSourceDsSet);
			if(replaceModelMap.get("message") != null){
				resultMap.put("message", replaceModelMap.get("message"));
				return resultMap;
			}
			sb = new StringBuffer(replaceModelMap.get("expression"));
			//替换数据模型 结束
			//校验左表达式
			try {
				ValidateExpressionUtils.validateFomula(leftExpression);
			} catch (ValidateException e) {
				resultMap.put("message", "左表达式中" + e.getMessage());
				return resultMap;
			}
			
			resultMap.put("leftExpressionDesc", leftSb.toString());
			resultMap.put("leftExpression", changeRptFunc(sb.toString()));
		}
		if (!StringUtils.isEmpty(rightExpression)) {
			Matcher matcher = pattern.matcher(rightExpression);
			Matcher rightMatcher = pattern.matcher(rightExpression);
			StringBuffer sb = new StringBuffer();
			StringBuffer rightSb = new StringBuffer();
			while (matcher.find()) {
				if (replaceMap.get(matcher.group(0)) == null) {
					resultMap.put("message", "右表达式中第" + (matcher.start() + 1)
							+ "位的指标不存在，请修改！");
					return resultMap;
				}
				rightSourceIdxSet.add(replaceMap.get(matcher.group(0)).substring(3, replaceMap.get(matcher.group(0)).length() - 2));
				matcher.appendReplacement(sb,
						replaceMap.get(matcher.group(0)));
			}
			matcher.appendTail(sb);
			//构造表达右简式
			while (rightMatcher.find()) {
				
				String rightDesc = rightDescMap.get(rightMatcher.group(0));
				/*if(rightDesc.contains(mainRptNm))
					rightDesc = "I["+rightDesc.substring(rightDesc.indexOf("@")+1);
				else rightDesc = rightDesc.replace('@', '.');*/
				//表内公式/表间公式的简式都只显示单元格名称
				rightDesc = "I["+rightDesc.substring(rightDesc.indexOf("@")+1);
				rightMatcher.appendReplacement(rightSb,rightDesc
						);
			}
			rightMatcher.appendTail(rightSb);
			//替换数据模型
			Map<String, String> replaceModelMap = replaceModel(sb.toString(),rightSourceDsSet);
			if(replaceModelMap.get("message") != null){
				resultMap.put("message", replaceModelMap.get("message"));
				return resultMap;
			}
			sb = new StringBuffer(replaceModelMap.get("expression"));
			//替换数据模型 结束
			//校验右表达式
			try{
				ValidateExpressionUtils.validateFomula(rightExpression);
			}catch(Exception e){
				resultMap.put("message", "右表达式中" + e.getLocalizedMessage());
				return resultMap;
			}
			resultMap.put("rightExpressionDesc", rightSb.toString());
			resultMap.put("rightExpression", changeRptFunc(sb.toString()));
		}
		if (!StringUtils.isEmpty(expression)) {
			Matcher matcher = pattern.matcher(expression);
			StringBuffer sb = new StringBuffer();
			while (matcher.find()) {
				if (replaceMap.get(matcher.group(0)) == null) {
					resultMap.put("message", "表达式中第" + (matcher.start() + 1)
							+ "位的指标不存在，请修改！");
					return resultMap;
				}
				leftSourceIdxSet.add(replaceMap.get(matcher.group(0)).substring(3, replaceMap.get(matcher.group(0)).length() - 2));
				matcher.appendReplacement(sb,
						replaceMap.get(matcher.group(0)));
			}
			matcher.appendTail(sb);
			
			//校验右表达式
			try{
				ValidateExpressionUtils.validateFomula(sb.toString());
			}catch(Exception e){
				resultMap.put("message", "表达式中" + e.getLocalizedMessage());
				return resultMap;
			}
			resultMap.put("expression", changeRptFunc(sb.toString()));
		}
		
		
		String leftSourceIdx = "";
		String leftSourceDs = "";
		List<String> idxNoList = new ArrayList<String>();
		List<String> dsNoList = new ArrayList<String>();
		
		for(String str : leftSourceIdxSet){
			leftSourceIdx += str + ",";
			if(!idxNoList.contains(str)){
				idxNoList.add(str);
			}
		}
		if(leftSourceIdx.endsWith(",")){
			leftSourceIdx = leftSourceIdx.substring(0, leftSourceIdx.length() - 1);
		}
		
		for(String str : leftSourceDsSet){
			if(StringUtils.isNotBlank(str)){
				leftSourceDs += str + ",";
				if(!dsNoList.contains(str)){
					dsNoList.add(str);
				}
			}
		}
		if(leftSourceDs.endsWith(",")){
			leftSourceDs = leftSourceDs.substring(0, leftSourceDs.length() - 1);
		}
		
		resultMap.put("leftFormulaIndex", leftSourceIdx);
		resultMap.put("leftFormulaDs", leftSourceDs);
		
		String rightSourceIdx = "";
		String rightSourceDs = "";
		
		for(String str : rightSourceIdxSet){
			if(StringUtils.isNotBlank(str)){
				rightSourceIdx += str + ",";
				if(!idxNoList.contains(str)){
					idxNoList.add(str);
				}
			}
			
		}
		if(rightSourceIdx.endsWith(",")){
			rightSourceIdx = rightSourceIdx.substring(0, rightSourceIdx.length() - 1);
		}
		
		for(String str : rightSourceDsSet){
			rightSourceDs += str + ",";
			if(!dsNoList.contains(str)){
				dsNoList.add(str);
			}
		}
		if(rightSourceDs.endsWith(",")){
			rightSourceDs = rightSourceDs.substring(0, rightSourceDs.length() - 1);
		}
		
		resultMap.put("rightFormulaIndex", rightSourceIdx);
		resultMap.put("rightFormulaDs", rightSourceDs);
		
		if(!StringUtils.isEmpty(expression)){
			Map<String, Object> dimMap = new HashMap<String, Object>();
			if(idxNoList.size() > 0){
				List<List<String>> indexNosParam = new ArrayList<List<String>>();
				if(idxNoList.size() > 1000){
					int index = 0;
					int remain = idxNoList.size();
					while(remain > 1000){
						indexNosParam.add(idxNoList.subList(index, index+1000));
						index += 1000;
						remain -= 1000;
					}
					if(index < idxNoList.size()){
						indexNosParam.add(idxNoList.subList(index, idxNoList.size()));
					}
				}else{
					indexNosParam.add(idxNoList);
				}
				dimMap.put("ids", indexNosParam);
				List<RptDimTypeInfo> dimTypeList = dimRelDao.getDimByIdxInfo(dimMap);
				resultMap.put("formulaDims", dimTypeList);
			}else{
				resultMap.put("formulaDims", new ArrayList<RptDimTypeInfo>());
			}
			resultMap.put("formulaIndex", leftSourceIdx);
			resultMap.put("formulaDs", rightSourceDs);
		}
		return resultMap;
	}

	/**
	 * 將"03"类型的公式转换成引擎能识别的公式：ThisMonthFirst(I('bfa6ead060e711e98651cfd84b3ca728'))=》ThisMonthFirst('bfa6ead060e711e98651cfd84b3ca728')
	 * @param expr
	 * @return
	 */
	private String changeRptFunc(String expr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("funcType", GlobalConstants4plugin.VALID_FUNC_TYPE_RPT);
		List<RptIdxFormulaFunc> funcs = this.funAndSymbolMybatisDao.listFunc(map);
		if(funcs == null || funcs.size() == 0)
			return expr;
		
		StringBuffer sb = new StringBuffer();
		String patternString = "";
		//构造正则表达式
		for(RptIdxFormulaFunc func : funcs){
			if("A".equals(func.getFormulaId())){//A()公式有两个或三个参数，特殊处理一下 mapjin2 20200325
				patternString = patternString  + func.getFormulaId() + "\\(I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|\\.)+'\\)+,'[A-Z]','Y'+\\)" + "|";
				patternString = patternString  + func.getFormulaId() + "\\(I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|\\.)+'\\)+,'[A-Z]'+\\)" + "|";
			}else{
				patternString = patternString  + func.getFormulaId() + "\\(I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|\\.)+'\\)\\)" + "|";
			}
		}
		if(patternString.endsWith("|")){
			patternString = patternString.substring(0, patternString.length() - 1);
		}
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(expr);
		//匹配公式，匹配到了转换公式
		while (matcher.find()) {
			matcher.appendReplacement(sb,matcher.group(0).substring(0, matcher.group(0).indexOf("("))+ "(" + //公式A+( =>A(
					matcher.group(0).substring(matcher.group(0).indexOf("'"), matcher.group(0).indexOf("'", matcher.group(0).indexOf("'") + 1)+1) + //从第一个单引号截取到第二个单引号后一位 =>'bfa6ead060e711e98651cfd84b3ca728'
					matcher.group(0).substring(matcher.group(0).indexOf("'", matcher.group(0).indexOf("'") + 1)+2, matcher.group(0).lastIndexOf(")"))+")");//从第二个单引号截取到最后一个括号前一位+“)” =>,'M') 最终=A('bfa6ead060e711e98651cfd84b3ca728','M')
		}
		matcher.appendTail(sb);
		return sb.toString().equals("")?expr:sb.toString();
	}
	public Map<String, String> validExpression(String expression, String expressionDesc) {
		Map<String, String> map = new HashMap<String, String>();
		try{
			ValidateExpressionUtils.validateFomula(expressionDesc);
			map.put("expression", changeRptFunc(expression));
			map.put("expressionDesc", expressionDesc);
		}catch(Exception e){
			map.put("message", e.getLocalizedMessage());
		}
		
		return map;
	}

	public Map<String, String> getRptAndLine(String templateId, String rptId) {
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("templateId", templateId);
		String lineNm = this.logicDao.getLineNm(condition);
		condition.clear();
		
		condition.put("rptId", rptId);
		String rptNm = this.logicDao.getRptNm(condition);

		Map<String, String> map = new HashMap<String, String>();
		map.put("rptNm", rptNm);
		map.put("lineNm", lineNm);
		return map;
		
	}

	/**
	 * 表达式调整功能
	 * @param rptTemplateId
	 */
	public Map<String, Object> syncExpressionDesc(String rptTemplateId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if(StringUtils.isBlank(rptTemplateId)){//报表模板编号必须要带，不然就不执行调整逻辑
			returnMap.put("isSuccess", "No");
			returnMap.put("tip", "调整失败");
			return returnMap;
		}
		
		//找到这个模板下的校验公式关联的所有的指标
		List<RptValidLogicIdxRel> idxList = this.baseDAO.findWithIndexParam("select idx from RptValidLogicIdxRel idx where idx.id.templateId = (?0)", rptTemplateId);
		if(idxList == null || idxList.size() == 0) {
			returnMap.put("isSuccess", "No");
			returnMap.put("tip", "调整失败");
			return returnMap;
		}
		List<String> idxNos = new ArrayList<String>();
		for(RptValidLogicIdxRel idxRel : idxList) {
			idxNos.add(idxRel.getId().getIndexNo());
		}
		
		 List<List<String>>  idxNoList = SplitStringBy1000.change(idxNos);
		//构造相关的报表指标信息
		 List<NameAndIdMap> list = new ArrayList<NameAndIdMap>();
		 for(List<String> idxs :  idxNoList) {
			 Map<String, Object> map = new HashMap<String, Object>();
			 map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			 map.put("idxNos", idxs);
			 map.put("endDate", "29991231");//只调整最新版本的公式
			 list.addAll(this.logicDao.getNameAndNo(map));
		 }
		
		Map<String, String> replaceMap = new HashMap<String, String>();//构造表达式描述{报表指标编号：报表名称.单元格名称}
		Map<String, String> replaceDescMap = new HashMap<String, String>();//构造表达式简式{报表指标编号：单元格名称}
		for(NameAndIdMap indexMap : list){
			if(StringUtils.isBlank(indexMap.getLineId())){
				replaceMap.put("('" + indexMap.getIndexNo() + "')", "('" + indexMap.getRptNm() + "." + indexMap.getIndexNm() + "')");
			}else{
				replaceMap.put("('" + indexMap.getIndexNo() + "')", "('" + indexMap.getRptNm() + "." + indexMap.getLineNm() + "." + indexMap.getIndexNm() + "')");
			}
			replaceDescMap.put("('" + indexMap.getIndexNo() + "')", "[" + indexMap.getIndexNm() + "]");
		}
		Map<String, Object> codition = new HashMap<String, Object>();
		codition.put("rptTemplateId", rptTemplateId);
		codition.put("endDate", "29991231");//只调整最新版本的公式
		List<CfgextLogicVO> expressionList = this.logicDao.list(codition);
		//拼接过滤机构
		Map<String, CfgextLogicVO> resultMap = new HashMap<>();
		for (CfgextLogicVO cfgextLogicVO : expressionList) {
			if(resultMap.containsKey(cfgextLogicVO.getCheckId())){
				CfgextLogicVO cfgextLogicVO1 = resultMap.get(cfgextLogicVO.getCheckId());
				cfgextLogicVO1.setCheckOrg(cfgextLogicVO1.getCheckOrg()+","+cfgextLogicVO.getCheckOrg());
			} else {
				resultMap.put(cfgextLogicVO.getCheckId(), cfgextLogicVO);
			}
		}
		expressionList = resultMap.values().stream().collect(Collectors.toList());
//		expressionList = (List<CfgextLogicVO>) resultMap.values();
		
		if(expressionList != null && expressionList.size() > 0){
			boolean isSuccess = true;
			Pattern pattern = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[_]|[\\.]|[-])+'\\)");
			for(CfgextLogicVO logic : expressionList){
				String expression = "(" + logic.getLeftExpression() + ")" + logic.getLogicOperType() + "(" + logic.getRightExpression() + ")";
				String expressionShortDesc =  logic.getLeftExpression() + logic.getLogicOperType() + logic.getRightExpression();
				Matcher matcher = pattern.matcher(expression);
				StringBuffer sb = new StringBuffer();
				while (matcher.find()) {
					//只替换本报表的单元格，其他报表的单元格不进行替换
					if (replaceMap.get(matcher.group(0)) == null) {
						isSuccess = false;
						matcher.appendReplacement(sb, "('" + "来源指标未定义" + "')");
						expressionShortDesc = expressionShortDesc.replace(matcher.group(0), "['" + "来源指标未定义" + "']");
						returnMap.put("isSuccess", "No");
						returnMap.put("tip", "该报表有校验公式配置错误，请检查");
					}else{
						if(expression.substring(matcher.start() - 1, matcher.start()).equals("I")){//一般指标公式
							matcher.appendReplacement(sb, replaceMap.get(matcher.group(0)));
							expressionShortDesc = expressionShortDesc.replace(matcher.group(0), replaceDescMap.get(matcher.group(0)));
						}else{//涉及到比上期等函数的使用
							matcher.appendReplacement(sb, "(I" + replaceMap.get(matcher.group(0)) + ")");
							expressionShortDesc = expressionShortDesc.replace(matcher.group(0), "[I" + replaceDescMap.get(matcher.group(0)) + "]");
						}
						
					}
				}
				matcher.appendTail(sb);
				logic.setExpressionDesc(sb.toString());
				logic.setExpressionShortDesc(expressionShortDesc);
				this.logicDao.updateLogic(logic);
				if(isSuccess) {
					returnMap.put("isSuccess", "Yes");
				}
			}
		}else {
			returnMap.put("isSuccess", "No");
			returnMap.put("tip", "没有校验公式，无需调整");
		}
		return returnMap;
	}

	/**
	 * 这个检测没有意义，来源指标是自己也是可以的，比如取上期的值，所以先注掉
	 * @param formulaIndex 来源指标
	 * @return
	 */
/*	public Map<String, Object> checkCycleRef(String formulaIndex) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<IdxInfoVO> list = this.logicDao.getAllIdx(new HashMap<String, Object>());
		Map<String, String> idxAndSrcidxMap = new HashMap<String, String>();
		for(IdxInfoVO vo : list){
			idxAndSrcidxMap.put(vo.getIndexNo(), vo.getSrcIndexNo());
		}
			
		List<IdxInfoVO> currentIdxs = new ArrayList<IdxInfoVO>();
		List<IdxInfoVO> nextIdxs = new ArrayList<IdxInfoVO>();
		String formulaIndxArray[] = StringUtils.split(formulaIndex, ',');
		
		for(String tmp : formulaIndxArray){
			IdxInfoVO vo = new IdxInfoVO();
			vo.setIndexNo(tmp);
			vo.setSrcIdxNoPath("/" + tmp + "/");
			
			currentIdxs.add(vo);
		}
		
		do{
			nextIdxs.clear();
			for(IdxInfoVO tmp : currentIdxs){
				if(!StringUtils.isEmpty(idxAndSrcidxMap.get(tmp.getIndexNo()))){
					String tmpSrcIdx[] = StringUtils.split(idxAndSrcidxMap.get(tmp.getIndexNo()), ',');
					for(String tmp1 : tmpSrcIdx){
						if(!tmp.getSrcIdxNoPath().contains("/" + tmp1 + "/")){
							IdxInfoVO vo = new IdxInfoVO();
							vo.setIndexNo(tmp1);
							vo.setSrcIdxNoPath(tmp.getSrcIdxNoPath() + tmp1 + "/");
							nextIdxs.add(vo);
						}else{
							resultMap.put("message", "指标存在循环引用");
							//System.out.println("指标"+tmp.getIndexNo()+"存在循环引用");
							return resultMap;
						}
					}
				}
			}
			currentIdxs.clear();
			for(IdxInfoVO tmp : nextIdxs){
				currentIdxs.add(tmp);
			}
			
			
		}while(nextIdxs != null && nextIdxs.size() > 0);
		return null;
	}*/

	public List<RptSysModuleCol> getModelInfo(String setId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("notColType", GlobalConstants4plugin.COL_TYPE_NORMAL);
		map.put("setId", setId);
		List<RptSysModuleCol> result = this.logicDao.selectDatasetInfo(map);
		return result;
		
	}

	public List<CommonTreeNode> getDimItems(String dimTypeNo) {
		String jql = "select item from RptDimItemInfo item where item.id.dimTypeNo =?0";
		List<RptDimItemInfo> list = this.baseDAO.findWithIndexParam(jql, dimTypeNo);
		List<CommonTreeNode> result = new ArrayList<CommonTreeNode>();
		for(RptDimItemInfo info: list){
			CommonTreeNode node = new CommonTreeNode();
			node.setId(info.getId().getDimItemNo());
			node.setText(info.getDimItemNm());
			node.setUpId(info.getUpNo());
			result.add(node);
		}
		return result;
	}

	/**
	 * 校验关系导出
	 * @param endDate 
	 * @return
	 */
	public File expValidLogicRel(String ids, String templateId, String rptNm, String realPath, String endDate, String busiType, String isCellNo){
		File resFile = null;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("cfgId", templateId);
		RptMgrReportInfo info = reportInfoDAO.getRptInfoByParams(params);

		//查询当前用户的所有下级机构
		List<String> orgList = new ArrayList<String>();
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			orgList.addAll(authOrgNos);
			for(String authOrgNo : authOrgNos){
				// 机构权限 修改为用户能看到自己及下级机构的任务
				orgList.addAll(rptOrgInfoBS.getAllChildOrgNo(authOrgNo, busiType, true));
			}
		}
		Map<String, Object> condition = new HashMap<String, Object>();
		if(orgList != null && orgList.size() > 0){
			condition.put("orgNos", SplitStringBy1000.change(orgList));
		}
		String checkIds[] = StringUtils.split(ids, ",");
		if(StringUtils.isNotBlank(endDate)){
			condition.put("endDate", endDate);
		}else{
			condition.put("endDate", "29991231");//默认导出最新版本的校验公式
		}
		condition.put("orgType", busiType);

		List<CfgextLogicVO> vos = new ArrayList<CfgextLogicVO>();
		if("all".equals(ids)){
			condition.put("tempId", templateId);
			List<CfgextLogicVO> logicList = logicDao.listLogic(condition);
			//根据checkId进行分组
			Map<String, List<CfgextLogicVO>> collect = logicList.stream().collect(Collectors.groupingBy(CfgextLogicVO::getCheckId));
			if(collect != null && collect.size() > 0){
				List<List<CfgextLogicVO>> listList = collect.values().stream().collect(Collectors.toList());
				for (List<CfgextLogicVO> cfgextLogicVOS : listList) {
					if(null != cfgextLogicVOS && cfgextLogicVOS.size() > 0) {
						//拼接过滤机构
						CfgextLogicVO vo = cfgextLogicVOS.get(0);
						List<String> orgNos = cfgextLogicVOS.stream().map(item -> item.getOrgNo()).collect(Collectors.toList());

						vo.setCheckOrg(StringUtils.join(orgNos,"|"));
						vo.setCfgId(info.getCfgId());
						vo.setCheckId(vo.getCheckId());
						vo.setRptId(info.getRptId());
						vo.setRptNm(info.getRptNm());
						vos.add(vo);
					}
				}
			}
		} else {
			for (String checkId : checkIds) {
				condition.put("checkId", checkId);
				List<CfgextLogicVO> logicList = logicDao.listLogic(condition);
				if(null != logicList && logicList.size() > 0) {
					CfgextLogicVO vo = logicList.get(0);
					List<String> orgNos = logicList.stream().map(item -> item.getOrgNo()).collect(Collectors.toList());

					vo.setCheckOrg(StringUtils.join(orgNos,"|"));
					vo.setCfgId(info.getCfgId());
					vo.setCheckId(checkId);
					vo.setRptId(info.getRptId());
					vo.setRptNm(info.getRptNm());
					vos.add(vo);
				}
			}
		}
		//下载报表编号.单元格编号的校验公式
		Map<String, String> replaceMap = new HashMap<String, String>();//构造表达式描述{报表指标编号：报表编号.单元格编号}
		Map<String, String> replaceDescMap = new HashMap<String, String>();//构造表达式简式{报表指标编号：单元格编号}
		if(StringUtils.isNotBlank(isCellNo) && "Y".equals(isCellNo)) {
			//查询当前报表所有单元格信息
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
			map.put("endDate", info.getEndDate());
			map.put("rptNm", info.getRptNm());
			this.strNmAndCellNo(map, replaceMap, replaceDescMap);

			for(CfgextLogicVO vo : vos) {
				structureIndexNoToCellNo(vo, replaceMap, replaceDescMap);
			}
		}

		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);
		XlsExcelTemplateExporter fe = null;
		if(StringUtils.isBlank(rptNm)) {
			rptNm = RandomUtils.uuid2();
		}
		try {
			if (FilepathValidateUtils.validateFilepath(realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + rptNm + ".xls")) {
				resFile = new File(realPath + GlobalConstants4plugin.DESIGN_EXPORT_PATH
						+ File.separator + rptNm + ".xls");
				fe = new XlsExcelTemplateExporter(resFile, realPath
						+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
						+ GlobalConstants4plugin.EXPORT_VALIDLOGIC_TEMPLATE_PATH, list);
				fe.run();
			}
		} catch (Exception e) {
			resFile = null;
			e.printStackTrace();
		} finally {
			try {
				if (fe != null) {
					fe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		return resFile;

	}

	/**
	 * @方法描述:
	 * @创建人: huzq1 将报表名称.单元格名称转为报表编号.单元格编号
	 * @创建时间: 2021/8/3 10:29
	  * @param vo
	 * @param replaceMap
	 * @param replaceDescMap
	 * @return
	 **/
	private ValidErrorInfoObj structureIndexNmoCellNo(CfgextLogicVO vo, Map<String, String> replaceMap, Map<String, String> replaceDescMap) {
		try {
			String leftExpression = vo.getLeftExpression();//左表达式
			String rightExpression = vo.getRightExpression();//右表达式
			String logicOperType = vo.getLogicOperType();//逻辑运算符

			//进行校验公式转换（将跨期公式中带I的先将I去掉）
			leftExpression = changeRptFunc(leftExpression);
			rightExpression = changeRptFunc(rightExpression);

			String expressionShortDesc =  leftExpression;
			if(StringUtils.isNotBlank(leftExpression) && StringUtils.isNotBlank(rightExpression)){
				Pattern pattern = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[-]|[，]|[,]|[%]|[+]|[\\[]|[\\]])+'\\)");
				Pattern cpattern = Pattern.compile("C\\('([YMD])+'\\)");
				Matcher leftMatcher = pattern.matcher(leftExpression);
				Matcher cleftMatcher = cpattern.matcher(leftExpression);
				List<String> cleftList = new ArrayList<>();
				while(cleftMatcher.find()){
					String s = cleftMatcher.group(0);
					cleftList.add(s.substring(3, s.length()-1));
				}
				StringBuffer leftSb = new StringBuffer();
				while (leftMatcher.find()) {
					//如果为空，可能是没有加载这种报表的数据，先去库里查一次
					if (StringUtils.isBlank(replaceMap.get(leftMatcher.group(0)))) {
						String logicText = leftMatcher.group(0);
						logicText = logicText.substring(2, logicText.length()-1);
						if(!cleftList.contains(logicText)){
							String[] logicTexts = logicText.split("\\.");
							if(logicTexts.length > 1) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
								map.put("rptNm", logicTexts[0]);
								map.put("endDate", vo.getEndDate());
								this.strNmAndCellNo(map, replaceMap, replaceDescMap);
							}
						}
					}
					if(leftExpression.substring(leftMatcher.start() - 1, leftMatcher.start()).equals("I")){//一般指标公式
						leftMatcher.appendReplacement(leftSb, replaceMap.get(leftMatcher.group(0)));
						expressionShortDesc = expressionShortDesc.replace(leftMatcher.group(0), replaceDescMap.get(leftMatcher.group(0)));
					}else{//涉及到比上期等函数的使用
						leftMatcher.appendReplacement(leftSb, "(I" + replaceMap.get(leftMatcher.group(0)) + ")");
						expressionShortDesc = expressionShortDesc.replace(leftMatcher.group(0), "[I" + replaceDescMap.get(leftMatcher.group(0)) + "]");
					}
				}
				leftMatcher.appendTail(leftSb);
				expressionShortDesc = expressionShortDesc + logicOperType + rightExpression;
				Matcher rightMatcher = pattern.matcher(rightExpression);
				Matcher crightMatcher = cpattern.matcher(rightExpression);
				List<String> crightList = new ArrayList<>();
				while (crightMatcher.find()){
					String s = crightMatcher.group(0);
					crightList.add(s.substring(3, s.length()-1));
				}
				StringBuffer rightSb = new StringBuffer();
				while (rightMatcher.find()) {
					//如果为空，可能是没有加载这种报表的数据，先去库里查一次
					if (StringUtils.isBlank(replaceMap.get(rightMatcher.group(0)))) {
						String logicText = rightMatcher.group(0);
						logicText = logicText.substring(2, logicText.length()-1);
						if(!crightList.contains(logicText)){
							String[] logicTexts = logicText.split("\\.");
							if(logicTexts.length > 1) {
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
								map.put("rptNm", logicTexts[0]);
								map.put("endDate", vo.getEndDate());
								this.strNmAndCellNo(map, replaceMap, replaceDescMap);
							}
						}
					}
					if(rightExpression.substring(rightMatcher.start() - 1, rightMatcher.start()).equals("I")){//一般指标公式
						rightMatcher.appendReplacement(rightSb, replaceMap.get(rightMatcher.group(0)));
						expressionShortDesc = expressionShortDesc.replace(rightMatcher.group(0), replaceDescMap.get(rightMatcher.group(0)));
					}else{//涉及到比上期等函数的使用
						rightMatcher.appendReplacement(rightSb, "(I" + replaceMap.get(rightMatcher.group(0)) + ")");
						expressionShortDesc = expressionShortDesc.replace(rightMatcher.group(0), "[I" + replaceDescMap.get(rightMatcher.group(0)) + "]");
					}
				}
				rightMatcher.appendTail(rightSb);
				String expressionDesc = "(" + leftSb + ")" + logicOperType + "(" + rightSb + ")";
				vo.setLeftExpression(leftSb.toString());
				vo.setRightExpression(rightSb.toString());
				vo.setExpressionDesc(expressionDesc.toString());
				vo.setExpressionShortDesc(expressionShortDesc.toString());
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 根据校验公式表述，构造报表编号.单元格编号类型数据
	 * @param vo
	 * @param replaceMap
	 * @return
	 */
	private void structureIndexNoToCellNo(CfgextLogicVO vo, Map<String, String> replaceMap, Map<String, String> replaceDescMap) {
		try {
			String leftExpression = vo.getLeftExpression();//左表达式
			String rightExpression = vo.getRightExpression();//右表达式
			//进行校验公式转换（将跨期公式中带I的先将I去掉）
			leftExpression = changeRptFunc(leftExpression);
			rightExpression = changeRptFunc(rightExpression);

			String logicOperType = vo.getLogicOperType();//逻辑运算符
			String expressionShortDesc =  leftExpression;
			if(StringUtils.isNotBlank(leftExpression) && StringUtils.isNotBlank(rightExpression)){
				Pattern pattern = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[_]|[-])+'\\)");
				Pattern cpattern = Pattern.compile("C\\('([YMD])+'\\)");
				Matcher leftMatcher = pattern.matcher(leftExpression);
				Matcher cleftMatcher = cpattern.matcher(leftExpression);
				List<String> clist = new ArrayList<>();
				while (cleftMatcher.find()){
					String s = cleftMatcher.group(0).substring(3, cleftMatcher.group(0).length()-2);
					clist.add(s);
				}
				StringBuffer leftSb = new StringBuffer();
				while (leftMatcher.find()) {
					//如果为空，可能是没有加载这种报表的数据，先去库里查一次
					if (StringUtils.isBlank(replaceMap.get(leftMatcher.group(0)))) {
						String rptIndexNo = leftMatcher.group(0).substring(2, leftMatcher.group(0).length()-2);
						if(!clist.contains(rptIndexNo)){
							Map<String, Object> param = new HashMap<String, Object>();
							param.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
							param.put("rptIndexNo", rptIndexNo);
							param.put("endDate", vo.getEndDate());
							List<NameAndIdMap> rptIndexList = this.logicDao.getNameAndCellNo(param);
							if(rptIndexList != null && rptIndexList.size() > 0){
								String rptNm = rptIndexList.get(0).getRptNm();
								param.remove("rptIndexNo");
								param.put("rptNm", rptNm);
								this.strNmAndCellNo(param, replaceMap, replaceDescMap);
							}
						}
					}
					//替换指标编号为报表编号.单元编号
					if(leftExpression.substring(leftMatcher.start() - 1, leftMatcher.start()).equals("I")){//一般指标公式
						leftMatcher.appendReplacement(leftSb, replaceMap.get(leftMatcher.group(0)));
						expressionShortDesc = expressionShortDesc.replace(leftMatcher.group(0), replaceDescMap.get(leftMatcher.group(0)));
					}else{//涉及到比上期等函数的使用
						leftMatcher.appendReplacement(leftSb, "(I" + replaceMap.get(leftMatcher.group(0)) + ")");
						expressionShortDesc = expressionShortDesc.replace(leftMatcher.group(0), "[I" + replaceDescMap.get(leftMatcher.group(0)) + "]");
					}
				}
				leftMatcher.appendTail(leftSb);
				expressionShortDesc = expressionShortDesc + logicOperType + rightExpression;
				Matcher rightMatcher = pattern.matcher(rightExpression);
				Matcher crightMatcher = cpattern.matcher(rightExpression);
				List<String> crightList = new ArrayList<>();
				while (crightMatcher.find()){
					String s = crightMatcher.group(0).substring(3, crightMatcher.group(0).length()-2);
					crightList.add(s);
				}
				StringBuffer rightSb = new StringBuffer();
				while (rightMatcher.find()) {
					//如果为空，可能是没有加载这种报表的数据，先去库里查一次
					if (StringUtils.isBlank(replaceMap.get(rightMatcher.group(0)))) {
						String rptIndexNo = rightMatcher.group(0).substring(2, rightMatcher.group(0).length()-2);
						if(!crightList.contains(rptIndexNo)){
							Map<String, Object> param = new HashMap<String, Object>();
							param.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
							param.put("rptIndexNo", rptIndexNo);
							param.put("endDate", vo.getEndDate());
							List<NameAndIdMap> rptIndexList = this.logicDao.getNameAndCellNo(param);
							if(rptIndexList != null && rptIndexList.size() > 0){
								String rptNm = rptIndexList.get(0).getRptNm();
								param.remove("rptIndexNo");
								param.put("rptNm", rptNm);
								this.strNmAndCellNo(param, replaceMap, replaceDescMap);
							}
						}
					}
					//替换指标编号为报表编号.单元编号
					if(rightExpression.substring(rightMatcher.start() - 1, rightMatcher.start()).equals("I")){//一般指标公式
						rightMatcher.appendReplacement(rightSb, replaceMap.get(rightMatcher.group(0)));
						expressionShortDesc = expressionShortDesc.replace(rightMatcher.group(0), replaceDescMap.get(rightMatcher.group(0)));
					}else{//涉及到比上期等函数的使用
						rightMatcher.appendReplacement(rightSb, "(I" + replaceMap.get(rightMatcher.group(0)) + ")");
						expressionShortDesc = expressionShortDesc.replace(rightMatcher.group(0),  "[I" + replaceDescMap.get(rightMatcher.group(0)) + "]");
					}
				}
				rightMatcher.appendTail(rightSb);

				String expressionDesc = "(" + leftSb + ")" + logicOperType + "(" + rightSb + ")";
				vo.setLeftExpression(leftSb.toString());
				vo.setRightExpression(rightSb.toString());
				vo.setExpressionDesc(expressionDesc.toString());
				vo.setExpressionShortDesc(expressionShortDesc.toString());
			}
		} catch (Exception e) {
//			structureIndexNmoCellNo(vo, replaceMap, replaceDescMap);
		}
	}

	/**
	 * 根据传入报表名称构造报表指标信息
	 * @param map
	 * @param replaceMap
	 * @param replaceDescMap
	 */
	private void strNmAndCellNo(Map<String, Object> map, Map<String, String> replaceMap, Map<String, String> replaceDescMap) {
		//构造相关的报表指标信息
		List<NameAndIdMap> list = this.logicDao.getNameAndCellNo(map);
		for(NameAndIdMap tmp : list){
			if(StringUtils.isBlank(tmp.getLineId())){
				//构造(Key:指标编号,val:报表编号.单元格编号)
				replaceMap.put("('" + tmp.getIndexNo() + "')", "('" + tmp.getRptNum() + "." + tmp.getCellNo() + "')");
				replaceDescMap.put("('" + tmp.getIndexNo() + "')", "[" + tmp.getCellNo() + "]");
				//构造(Key:报表名称.单元格名称,val:单元格编号)
				replaceMap.put("('" + tmp.getRptNm() + "." + tmp.getIndexNm() + "')", "('" + tmp.getCellNo() + "')");
				replaceDescMap.put("('" + tmp.getRptNm() + "." + tmp.getIndexNm() + "')", "[" + tmp.getCellNo() + "]");
			}else{
				//构造(Key:指标编号,val:报表名称.单元格编号)
				replaceMap.put("('" + tmp.getIndexNo() + "')", "('" + tmp.getRptNum() + "." + tmp.getLineNm() + "." + tmp.getCellNo() + "')");
				replaceDescMap.put("('" + tmp.getIndexNo() + "')", "[" + tmp.getCellNo() + "]");
				//构造(Key:报表名称.单元格名称,val:单元格编号)
				replaceMap.put("('" + tmp.getRptNm() + "." + tmp.getLineNm() + "." + tmp.getIndexNm() + "')", "('" + tmp.getCellNo() + "')");
				replaceDescMap.put("('" + tmp.getRptNm() + "." + tmp.getLineNm() + "." + tmp.getIndexNm() + "')", "[" + tmp.getCellNo() + "]");
			}
		}
	}

	/**
	 * 上传文件
	 * @param file
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public UploadResult impValidLogicRel(File file, String ehcacheId){
		
		AbstractExcelImport xlsImport = null;
		xlsImport = new ExcelImporter(CfgextLogicVO.class, file);
		UploadResult result = new UploadResult();
		try {
			if(xlsImport!=null){
				List<CfgextLogicVO> vos = (List<CfgextLogicVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
				if (null == vos) {
					vos = new ArrayList<CfgextLogicVO>();
				}
				List<CfgextLogicVO> newVos = (List<CfgextLogicVO>) xlsImport.ReadExcel();
				vos.addAll(newVos);
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, vos);
				result.setEhcacheId(ehcacheId);
				result.setFileName(file.getName());
				if (newVos != null && newVos.size() > 0) {
					RptMgrReportInfo info = getRptMgrReportInfo(newVos.get(0));
					if (null != info)
						result.setInfo(info.getRptId(), info.getRptNm());
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public  RptValidLogicRptRel getRptValidLogicRptRel(CfgextLogicVO vo) {
		RptValidLogicRptRelPK pk = new RptValidLogicRptRelPK();
		pk.setCheckId(vo.getCheckId());
		pk.setRptTemplateId(vo.getCfgId());
		RptValidLogicRptRel rel = new RptValidLogicRptRel();
		rel.setId(pk);
		return rel;
	}
	
	public  RptMgrReportInfo getRptMgrReportInfo(CfgextLogicVO vo) {
		Map<String, Object> params = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(vo.getRptId())) {
			params.put("rptId", vo.getRptId());
		}
		if(StringUtils.isNotBlank(vo.getRptNm())) {
			params.put("rptNm", vo.getRptNm());
		}
		return reportInfoDAO.getRptInfoByParams(params);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public  void saveValidLogicRel(String ehcacheId, String dsId) {
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptValidCfgextLogic> logics = (List<RptValidCfgextLogic>) lists.get(0);//校验公式list
		List<RptValidLogicRptRel> rels = (List<RptValidLogicRptRel>) lists.get(1);//校验公式与报表关系list
		List<String> checkIdList = (List<String>) lists.get(2);//校验公式编号list
		List<CfgextLogicVO> cfgextLogic = (List<CfgextLogicVO>) lists.get(3);//校验公式导入VO list
		List<RptValidLogicOrg> rptValidLogicOrgList = (List<RptValidLogicOrg>) lists.get(4);// 校验公式机构过滤 list
		if(null != checkIdList && checkIdList.size() > 0) {
			//先删除已有的检验公式根据检验ID
			this.deletelogicByCheckId(checkIdList);
		}
		for (RptValidCfgextLogic logic : logics) {
			logicDao.insertLogic(logic);
		}
		for (RptValidLogicRptRel rel : rels) {
			relDao.saveRel(rel);
		}
		for (RptValidLogicOrg rptValidLogicOrg : rptValidLogicOrgList) {
			String checkOrgStr = rptValidLogicOrg.getCheckOrg();
			if(StringUtils.isNotBlank(checkOrgStr)){
				String[] checkOrgs = checkOrgStr.split("\\|");
				List<Map<String, Object>> validLogicOrgList = new ArrayList<>();
				for (String orgNo : checkOrgs) {
					Map<String, Object> map = new HashMap<>();
					map.put("checkId", rptValidLogicOrg.getCheckId());
					map.put("templateId", rptValidLogicOrg.getTemplateId());
					map.put("startDate", rptValidLogicOrg.getStartDate());
					map.put("endDate", rptValidLogicOrg.getEndDate());
					map.put("checkOrg", orgNo);
					validLogicOrgList.add(map);
				}
				this.logicDao.batchInsertRptValidLogicOrg(validLogicOrgList);
			}
		}
		
		/**
		 * 校验导入生成检验公式和指标关系
		 */
		for(CfgextLogicVO temp : cfgextLogic){
			Set<String> lRptNums = new HashSet<String>();
			this.getRptIdxid(lRptNums, temp.getLeftExpression());
			Set<String> rRptNums = new HashSet<String>();
			this.getRptIdxid(rRptNums, temp.getRightExpression());
			
			for(String lIdx : lRptNums){
				RptValidLogicIdxRel lIdxRel = new RptValidLogicIdxRel();
				RptValidLogicIdxRelPK lIdxRelPK = new RptValidLogicIdxRelPK();
				
				lIdxRelPK.setCheckId(temp.getCheckId());
				lIdxRelPK.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_LEFT);
				lIdxRelPK.setIndexNo(lIdx);
				lIdxRelPK.setTemplateId(temp.getCfgId());
				lIdxRel.setId(lIdxRelPK);
				rptValidLogicIdxRelBS.saveEntity(lIdxRel);
			}
			for(String rIdx : rRptNums){
				RptValidLogicIdxRel rIdxRel = new RptValidLogicIdxRel();
				RptValidLogicIdxRelPK rIdxRelPK = new RptValidLogicIdxRelPK();
				
				rIdxRelPK.setCheckId(temp.getCheckId());
				rIdxRelPK.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_RIGHT);
				rIdxRelPK.setIndexNo(rIdx);
				rIdxRelPK.setTemplateId(temp.getCfgId());
				rIdxRel.setId(rIdxRelPK);
				rptValidLogicIdxRelBS.saveEntity(rIdxRel);
			}
		}
		//保存完成，删除缓存
		EhcacheUtils.remove(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		//EhcacheUtils.remove(BioneSecurityUtils.getCurrentUserId(), "easyImpValidLogicIdxMap");
		//EhcacheUtils.remove(BioneSecurityUtils.getCurrentUserId(), "easyImpValidLogicIdxDescMap");
	}
	
	private void getRptIdxid(Set<String> rptNums, String expression){
		if(StringUtils.isNotEmpty(expression)){
			Pattern pattern = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[-])+'\\)");
			Matcher matcher = pattern.matcher(expression);
			while (matcher.find()) {
				String idxNo = matcher.group(0);
				idxNo = idxNo.replaceAll("'", "");
				idxNo = idxNo.replaceAll("\\(", "");
				idxNo = idxNo.replaceAll("\\)", "");
				rptNums.add(idxNo); 
			}
			//之前这个处理无法处理跨期公式不带I的情况
/*			String[] strs = StringUtils.splitByWholeSeparator(expression, "I('");
			for(int j=0; j<strs.length; j++){          //从第一位开始
				if(strs[j].contains("')")){
					String s = StringUtils.substring(strs[j], 0, strs[j].indexOf('\''));
					if(StringUtils.isNotBlank(s)){
						rptNums.add(s.replace('.', '_'));                //不重复的才加入其中
					}
				}
			}*/
		}
	}
	
	public void deletelogicByTmpId(String templateId){
		if(StringUtils.isNotEmpty(templateId)){
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("templateId", templateId);
			logicDao.deleteLogicByTmpId(param);
			logicDao.deleteLogicIdxByTmpId(param);
			logicDao.deleteLogicRptByTmpId(param);
		}
	}
	/**
	 * 删除检验相关表信息根据检验公式ID
	 * @param checkIdList
	 */
	public void deletelogicByCheckId(List<String> checkIdList){
		if(null != checkIdList && checkIdList.size() > 0) {
			//进行1000为一个单元的分割
			List<List<String>> idLists = SplitStringBy1000.change(checkIdList);
			for(List<String> idListBy1000 : idLists) {
				Map<String, Object> condition = new HashMap<String, Object>();
				condition.put("ids", idListBy1000);
				//删除检验信息表
				this.logicDao.deleteLogic(condition);
				//删除逻辑校验和指标的关系表
				this.logicDao.deleteLogicRel(condition);
				//删除校验的报表的关系表
				this.logicDao.deleteLogicRptRel(condition);
				//删除校验公式机构过滤表
				this.logicDao.deleteRptValidLogicOrg(condition);
			}
		}
	}
	
	/**
	 * 根据模板编号开始结束日期更新检验公式
	 * @param templateId
	 * @param startDate 要删除版本的开始日期
	 * @param endDate 要删除版本的结束日期
	 */
	@Transactional(readOnly = false)
	public void updateValidVer(String templateId, String startDate, String endDate) {
		if (StringUtils.isNotBlank(templateId) && StringUtils.isNotBlank(endDate) && StringUtils.isNotBlank(startDate)) {
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("templateId", templateId);
			condition.put("endDate", startDate);
			List<String> checkIds = logicDao.findCheckIds(condition);//找到上一个版本的校验公式
			List<List<String>> checkIdsBy1000 = SplitStringBy1000.change(checkIds);//1000分割
			for(List<String> checkIdList : checkIdsBy1000) {
				String jql = "update RptValidCfgextLogic info set info.endDate = :endDate where info.checkId in :checkId and info.endDate = :startDate";//更新上一个版本的结束日期
				Map<String,Object> params = new HashMap<String, Object>();
				params.put("endDate", endDate);
				params.put("startDate", startDate);
				params.put("checkId", checkIdList);
				if(checkIdList.size() > 0) {
					this.baseDAO.batchExecuteWithNameParam(jql, params);
				}
			}
		}
	}
	
	/**
	 * 根据机构类型获取机构层级数据
	 * @param orgType
	 * @return
	 */
	public Map<String, Object> initOrgLvl(String orgType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(orgType)) {
			String jql = "select ORG_LEVEL, max(ORG_NM) from RPT_ORG_INFO where ORG_TYPE = ?0 and ORG_LEVEL is not null group by ORG_LEVEL";
			List<Object[]> orgInfos = this.baseDAO.findByNativeSQLWithIndexParam(jql, orgType);
			if(orgInfos.size() > 0) {
				List<CommonComboBoxNode> boxNodes = new ArrayList<CommonComboBoxNode>();
				for(Object[] orgInfo : orgInfos) {
					CommonComboBoxNode boxNode = new CommonComboBoxNode();
					boxNode.setId((String) orgInfo[0]);
					boxNode.setText((String) orgInfo[1]);
					boxNodes.add(boxNode);
				}
				resultMap.put("orgLvl", boxNodes);
			}
		}
		return resultMap;
	}
	
	/**
	 * 检验公式导入功能
	 * @param dsId
	 * @param ehcacheId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  List<ValidErrorInfoObj> easyImpValidLogic(String dsId, String ehcacheId, String isNmImp) {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<CfgextLogicVO> vos = (List<CfgextLogicVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<String> chackIds = new ArrayList<String>();
		List<RptValidCfgextLogic> logics = new ArrayList<RptValidCfgextLogic>();
		List<RptValidLogicRptRel> rels = new ArrayList<RptValidLogicRptRel>();
		List<RptValidLogicOrg> rptValidLogicOrgList = new ArrayList<RptValidLogicOrg>();
		// 查询所有报表所属的业务类型
		List<String> rptNms = new ArrayList<String>();
		List<String> orgTypeList = new ArrayList<String>();
		for(CfgextLogicVO logicVo : vos){
			if(!rptNms.contains(logicVo.getRptNm())){
				rptNms.add(logicVo.getRptNm());
				RptMgrReportInfo info = getRptMgrReportInfo(logicVo);
				if (info != null) {
					if(!orgTypeList.contains(info.getBusiType())){
						orgTypeList.add(info.getBusiType());
					}
				}
			}
		}
		
		//查询当前用户的所有下级机构
		List<String> orgList = new ArrayList<String>();
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> authOrgNos = BioneSecurityUtils.getCurrentUserInfo().getAuthObjMap().get(GlobalConstants4frame.AUTH_OBJ_DEF_ID_ORG);
			orgList.addAll(authOrgNos);
			for(String authOrgNo : authOrgNos){
				// 机构权限 修改为用户能看到自己及下级机构的任务
				orgList.addAll(rptOrgInfoBS.getAllChildOrgNo(authOrgNo, StringUtils.join(orgTypeList, ","), true));
			}
		}

		Map<String, String> replaceMap = new HashMap<String, String>();//构造表达式描述{报表指标编号：报表名称.单元格名称}
		Map<String, String> replaceDescMap = new HashMap<String, String>();//构造表达式简式{报表指标编号：单元格名称}
		Map<String, String> replaceAllMap = new HashMap<String, String>();//构造表达式简式{报表编号.单元格编号：报表名称.指标名称}
		if (vos != null && vos.size() > 0) {
			for (CfgextLogicVO vo : vos) {
				boolean flag = true;
				if (StringUtils.isBlank(vo.getRptNm())) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("非空校验");
					obj.setErrorMsg("报表名称不允许为空");
					errors.add(obj);
					flag = false;
				}
				// 是否过滤机构校验
				if(GlobalConstants4plugin.COMMON_BOOLEAN_NO.equals(vo.getIsOrgFilter())){// 不进行机构过滤
					if (StringUtils.isNotBlank(vo.getCheckOrg())) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(15);
						obj.setValidTypeNm("关联性校验");
						obj.setErrorMsg("【是否过滤机构】字段配置为【否】时，【过滤机构编号】字段应配置为空！");
						errors.add(obj);
						flag = false;
					}
				} else if(GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(vo.getIsOrgFilter())){ //机构过滤
					if (StringUtils.isBlank(vo.getCheckOrg())) {
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(15);
						obj.setValidTypeNm("关联性校验");
						obj.setErrorMsg("【是否过滤机构】字段配置为【是】时，【过滤机构编号】字段不能为空！");
						errors.add(obj);
						flag = false;
					} else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
						String[] checkOrgArr = vo.getCheckOrg().split("\\|");
						for (String checkOrg : checkOrgArr) {
							if(!orgList.contains(checkOrg)){
								ValidErrorInfoObj obj = new ValidErrorInfoObj();
								obj.setFieldName(vo.getRptNm());
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(15);
								obj.setValidTypeNm("关联性校验");
								obj.setErrorMsg("【是否过滤机构】字段配置为【是】时，【过滤机构编号】字段应为当前用户所属机构的辖内机构！");
								errors.add(obj);
								flag = false;
								break;
							}
						}
					}
				}
				RptMgrReportInfo info = getRptMgrReportInfo(vo);
				if (info == null) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("报表校验");
					obj.setErrorMsg("报表“" + vo.getRptNm() + "”不存在，请先导入对应报表");
					errors.add(obj);
					flag = false;
				}else {
					vo.setCfgId(info.getCfgId());
				}				
				String sql = "select * from rpt_design_tmp_info t where t.template_id = ?0 and t.ver_start_date = ?1 and t.ver_end_date = ?2";
				List<Object[]> tmp = this.baseDAO.findByNativeSQLWithIndexParam(sql, vo.getCfgId(), vo.getStartDate(), vo.getEndDate());
				if(tmp.size() == 0){
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(6);
					obj.setValidTypeNm("日期校验");
					obj.setErrorMsg("校验公式的开始结束日期与报表["+vo.getRptNm()+"]的不一致，请在通用报表定制功能查看报表版本，填写正确的日期");
					errors.add(obj);
					flag = false;
				}
				//新增逻辑运算类型校验
				sql = "select symbol_nm from rpt_idx_formula_symbol";
				List<Object[]> symbolNms = this.baseDAO.findByNativeSQLWithIndexParam(sql);
				if (StringUtils.isBlank(vo.getLogicOperType()) || !symbolNms.contains(vo.getLogicOperType())) {
					ValidErrorInfoObj obj = new ValidErrorInfoObj();
					obj.setFieldName(vo.getRptNm());
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("逻辑运算类型校验");
					obj.setErrorMsg("逻辑运算类型不合法");
					errors.add(obj);
					flag = false;
				}
				if (!flag)
					continue;
				if(!rptNms.contains(vo.getRptNm())){
					rptNms.add(vo.getRptNm());
					//构造相关的报表指标信息
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
					map.put("rptNm", vo.getRptNm());
					map.put("endDate", vo.getEndDate());
					List<NameAndIdMap> list = this.logicDao.getNameAndNo(map);
					for(NameAndIdMap indexMap : list){
						if(StringUtils.isBlank(indexMap.getLineId())){
							//构造(Key:指标编号,val:报表名称.单元格名称)
							replaceMap.put("('" + indexMap.getIndexNo() + "')", "('" + indexMap.getRptNm() + "." + indexMap.getIndexNm() + "')");
							replaceDescMap.put("('" + indexMap.getIndexNo() + "')", "[" + indexMap.getIndexNm() + "]");
							//构造(Key:报表名称.单元格名称,val:指标编号)
							replaceMap.put("('" + indexMap.getRptNm() + "." + indexMap.getIndexNm() + "')", "('" + indexMap.getIndexNo() + "')");
							replaceDescMap.put("('" + indexMap.getRptNm() + "." + indexMap.getIndexNm() + "')", "[" + indexMap.getIndexNm() + "]");
							//构造(Key:报表编号.单元格编号, val:指标编号)
							replaceMap.put("('" + indexMap.getRptNum() + "." + indexMap.getCellNo() + "')", "('" + indexMap.getIndexNo() + "')");
							replaceDescMap.put("('" + indexMap.getRptNum() + "." + indexMap.getCellNo() + "')", "[" + indexMap.getIndexNm() + "]");
							replaceAllMap.put("('" + indexMap.getRptNum() + "." + indexMap.getCellNo() + "')", "('" + indexMap.getRptNm() + "." + indexMap.getIndexNm() + "')");
						}else{
							//构造(Key:指标编号,val:报表名称.单元格名称)
							replaceMap.put("('" + indexMap.getIndexNo() + "')", "('" + indexMap.getRptNm() + "." + indexMap.getLineNm() + "." + indexMap.getIndexNm() + "')");
							replaceDescMap.put("('" + indexMap.getIndexNo() + "')", "[" + indexMap.getIndexNm() + "]");
							//构造(Key:报表名称.单元格名称,val:指标编号)
							replaceMap.put("('" + indexMap.getRptNm() + "." + indexMap.getLineNm() + "." + indexMap.getIndexNm() + "')", "('" + indexMap.getIndexNo() + "')");
							replaceDescMap.put("('" + indexMap.getRptNm() + "." + indexMap.getLineNm() + "." + indexMap.getIndexNm() + "')", "[" + indexMap.getIndexNm() + "]");
							//构造(Key:报表编号.单元格编号, val:指标编号)
							replaceMap.put("('" + indexMap.getRptNum() + "." + indexMap.getLineNm() + "." + indexMap.getCellNo() + "')", "('" + indexMap.getIndexNo() + "')");
							replaceDescMap.put("('" + indexMap.getRptNum() + "." + indexMap.getLineNm() + "." + indexMap.getCellNo() + "')", "[" + indexMap.getIndexNm() + "]");
							replaceAllMap.put("('" + indexMap.getRptNum() + "." + indexMap.getLineNm() + "." + indexMap.getCellNo() + "')", "('" + indexMap.getRptNm() + "." + indexMap.getIndexNm() + "')");
						}
					}
				}

				RptValidCfgextLogic logic = new RptValidCfgextLogic();
				ValidErrorInfoObj errorsObj = new ValidErrorInfoObj();
				RptValidLogicOrg rptValidLogicOrg = new RptValidLogicOrg();
				if(GlobalConstants4plugin.RPT_VALID_IMPORT_TYPE_1.equals(isNmImp)) { //按照指标编号进行导入
					errorsObj = structureRptValidVO(vo, replaceMap, replaceDescMap);
				} else if(GlobalConstants4plugin.RPT_VALID_IMPORT_TYPE_2.equals(isNmImp)) { //按照报表名称.单元格名称进行导入
					errorsObj = structureRptValidVOByNm(vo, replaceMap, replaceDescMap);
				} else if(GlobalConstants4plugin.RPT_VALID_IMPORT_TYPE_3.equals(isNmImp)) { //按照报表编号.单元格编号进行导入
					errorsObj = structureRptValidVOByCell(vo, replaceMap, replaceDescMap, replaceAllMap);
				}
				if(null != errorsObj) {
					errors.add(errorsObj);
				}
				RptValidLogicRptRel rel = getRptValidLogicRptRel(vo);
				BeanUtils.copy(vo, logic);
				logics.add(logic);
				rels.add(rel);
				if(GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(vo.getIsOrgFilter())){
					BeanUtils.copy(vo, rptValidLogicOrg);
					rptValidLogicOrg.setTemplateId(vo.getCfgId());
					rptValidLogicOrgList.add(rptValidLogicOrg);
				}
				chackIds.add(logic.getCheckId());
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(logics);
		lists.add(rels);
		lists.add(chackIds);
		lists.add(vos);
		lists.add(rptValidLogicOrgList);
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId, lists);
		return errors;
	}

	/**
	 * 根据校验公式表述，构造相关数据(根据报表编号.单元格编号进行构造)
	 * @param vo
	 * @param replaceMap
	 * @return
	 */
	private ValidErrorInfoObj structureRptValidVOByCell(CfgextLogicVO vo, Map<String, String> replaceMap, Map<String, String> replaceDescMap, Map<String, String> replaceAllMap) {
		String leftExpression = vo.getLeftExpression();//左表达式
		String rightExpression = vo.getRightExpression();//右表达式
		//进行校验公式转换（将跨期公式中带I的先将I去掉）
		leftExpression = changeRptFunc(leftExpression);
		rightExpression = changeRptFunc(rightExpression);

		String logicOperType = vo.getLogicOperType();//逻辑运算符
		String expressionShortDesc =  leftExpression;
		String expressionDesc = "(" + leftExpression + ")";
		if(StringUtils.isNotBlank(leftExpression) && StringUtils.isNotBlank(rightExpression)){
			Pattern pattern = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[-]|[，]|[,]|[%]|[+]|[\\[]|[\\]])+'\\)");
			Pattern cpattern = Pattern.compile("C\\('([YMD])+'\\)");
			Matcher leftMatcher = pattern.matcher(leftExpression);
			Matcher cleftMatcher = cpattern.matcher(leftExpression);
			List<String> clist = new ArrayList<>();
			while (cleftMatcher.find()){
				String s = cleftMatcher.group(0).substring(3, cleftMatcher.group(0).length()-2);
				clist.add(s);
			}
			StringBuffer leftSb = new StringBuffer();
			while (leftMatcher.find()) {
				String rptCellNo = leftMatcher.group(0);
				String rptCellShortNo = rptCellNo.substring(2, rptCellNo.length()-2);
				//第一次没有，可能是没有加载这种报表的数据，先去库里查一次
				if(StringUtils.isBlank(replaceMap.get(rptCellNo))){
					if(!clist.contains(rptCellNo)){
						String[] rptCellNos = rptCellShortNo.split("\\.");
						if(rptCellNos.length > 1){
							Map<String, Object> map = new HashMap<>();
							map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
							map.put("rptNum", rptCellNos[0]);
							map.put("endDate", vo.getEndDate());
							this.strNmAndCellNoByImp(map,replaceMap,replaceDescMap,replaceAllMap);
						}
					}
				}
				//第二次没有，那就是真的没有
				if (StringUtils.isBlank(replaceMap.get(rptCellNo))) {
					if(!clist.contains(rptCellNo)){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(4);
						obj.setValidTypeNm("指标校验");
						obj.setErrorMsg(leftMatcher.group(0) + "不存在,请核对左表达式配置！");
						return obj;
					}
				}else{
					if(leftExpression.substring(leftMatcher.start() - 1, leftMatcher.start()).equals("I")){//一般指标公式
						leftMatcher.appendReplacement(leftSb, replaceMap.get(rptCellNo));
						expressionShortDesc = expressionShortDesc.replace(rptCellNo, replaceDescMap.get(rptCellNo));
						expressionDesc = expressionDesc.replace(rptCellNo, replaceAllMap.get(rptCellNo));
					}else{//涉及到比上期等函数的使用
						leftMatcher.appendReplacement(leftSb, "(I" + replaceMap.get(rptCellNo) + ")");
						expressionShortDesc = expressionShortDesc.replace(rptCellNo, "[I" + replaceDescMap.get(rptCellNo) + "]");
						expressionDesc = expressionDesc.replace(rptCellNo, "(I" + replaceAllMap.get(rptCellNo) + ")");
					}
				}
			}
			leftMatcher.appendTail(leftSb);
			expressionShortDesc = expressionShortDesc + logicOperType + rightExpression;
			expressionDesc = expressionDesc + logicOperType + "(" + rightExpression + ")";
			Matcher rightMatcher = pattern.matcher(rightExpression);
			Matcher crightMatcher = cpattern.matcher(rightExpression);
			List<String> crightList = new ArrayList<>();
			while (crightMatcher.find()){
				String s = crightMatcher.group(0).substring(3, crightMatcher.group(0).length()-2);
				crightList.add(s);
			}
			StringBuffer rightSb = new StringBuffer();
			String checkType = "01";//表内校验
			while (rightMatcher.find()) {
				String rptCellNo = rightMatcher.group(0);
				String rptCellShortNo = rptCellNo.substring(2, rptCellNo.length()-2);
				//第一次没有，可能是没有加载这种报表的数据，先去库里查一次
				if(StringUtils.isBlank(replaceMap.get(rptCellNo))){
					if(!clist.contains(rptCellNo)){
						String[] rptCellNos = rptCellShortNo.split("\\.");
						if(rptCellNos.length > 1){
							Map<String, Object> map = new HashMap<>();
							map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
							map.put("rptNum", rptCellNos[0]);
							map.put("endDate", vo.getEndDate());
							this.strNmAndCellNoByImp(map,replaceMap,replaceDescMap,replaceAllMap);
						}
					}
				}
				//第二次没有，那就是真的没有
				if (StringUtils.isBlank(replaceMap.get(rptCellNo))) {
					if(!crightList.contains(rptCellNo)){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(6);
						obj.setValidTypeNm("指标校验");
						obj.setErrorMsg(rightMatcher.group(0) + "不存在,请核对右表达式配置！");
						return obj;
					}
				}else{
					if(replaceAllMap.get(rptCellNo).indexOf(vo.getRptNm()) < 0) {
						checkType = "02";//表间校验
					}
					if(rightExpression.substring(rightMatcher.start() - 1, rightMatcher.start()).equals("I")){//一般指标公式
						rightMatcher.appendReplacement(rightSb, replaceMap.get(rptCellNo));
						expressionShortDesc = expressionShortDesc.replace(rptCellNo, replaceDescMap.get(rptCellNo));
						expressionDesc = expressionDesc.replace(rptCellNo, replaceAllMap.get(rptCellNo));
					}else{//涉及到比上期等函数的使用
						rightMatcher.appendReplacement(rightSb, "(I" + replaceMap.get(rptCellNo) + ")");
						expressionShortDesc = expressionShortDesc.replace(rptCellNo,  "[I" + replaceDescMap.get(rptCellNo) + "]");
						expressionDesc = expressionDesc.replace(rptCellNo,  "(I" + replaceAllMap.get(rptCellNo) + ")");
					}
				}
			}
			rightMatcher.appendTail(rightSb);
//			String expressionDesc = "(" + leftSb + ")" + logicOperType + "(" + rightSb + ")";
			//新增校验公式
			if(StringUtils.isBlank(vo.getCheckId())) {
				vo.setCheckId(RandomUtils.uuid2());
			}
			//是否预校验，默认否
			if(StringUtils.isBlank(vo.getIsPre())) {
				vo.setIsPre("0");
			}
			//是否机构过滤，默认否
			if(StringUtils.isBlank(vo.getIsOrgFilter())) {
				vo.setIsOrgFilter("N");
			}
			vo.setCheckType(checkType);
			vo.setLeftExpression(leftSb.toString());
			vo.setRightExpression(rightSb.toString());
			vo.setExpressionDesc(expressionDesc);
			vo.setExpressionShortDesc(expressionShortDesc);
		}else{
			ValidErrorInfoObj obj = new ValidErrorInfoObj();
			obj.setFieldName(vo.getRptNm());
			obj.setSheetName(vo.getSheetName());
			obj.setExcelRowNo(vo.getExcelRowNo());
			obj.setExcelColNo(4);
			obj.setValidTypeNm("公式配置校验");
			obj.setErrorMsg("左表达式或右表达式为空，请检查公式配置！");
			return obj;
		}
		return null;
	}

	/**
	 * 根据传入报表名称构造报表指标信息
	 * @param map
	 * @param replaceMap
	 * @param replaceDescMap
	 */
	private void strNmAndCellNoByImp(Map<String, Object> map, Map<String, String> replaceMap, Map<String, String> replaceDescMap, Map<String, String> replaceAllMap) {
		//构造相关的报表指标信息
		List<NameAndIdMap> list = this.logicDao.getNameAndCellNo(map);
		for(NameAndIdMap tmp : list){
			if(StringUtils.isBlank(tmp.getLineId())){
				//构造(Key:报表编号.单元格编号, val:指标编号)
				replaceMap.put("('" + tmp.getRptNum() + "." + tmp.getCellNo() + "')", "('" + tmp.getIndexNo() + "')");
				replaceDescMap.put("('" + tmp.getRptNum() + "." + tmp.getCellNo() + "')", "[" + tmp.getIndexNm() + "]");
				replaceAllMap.put("('" + tmp.getRptNum() + "." + tmp.getCellNo() + "')", "('" + tmp.getRptNm() + "." + tmp.getIndexNm() + "')");
//				//构造(Key:单元格名称,val:报表名.指标名)
//				replaceMap.put("('" + tmp.getCellNo() + "')", "('" + tmp.getRptNm() + "." + tmp.getIndexNm() + "')");
//				replaceDescMap.put("('" + tmp.getCellNo() + "')", "[" + tmp.getIndexNm() + "]");
			}else{
				//构造(Key:报表编号.单元格编号, val:指标编号)
				replaceMap.put("('" + tmp.getRptNum() + "." + tmp.getLineNm() + "." + tmp.getCellNo() + "')", "('" + tmp.getIndexNo() + "')");
				replaceDescMap.put("('" + tmp.getRptNum() + "." + tmp.getLineNm() + "." + tmp.getCellNo() + "')", "[" + tmp.getIndexNm() + "]");
				replaceAllMap.put("('" + tmp.getRptNum() + "." + tmp.getLineNm() + "." + tmp.getCellNo() + "')", "('" + tmp.getRptNm() + "." + tmp.getIndexNm() + "')");
//				//构造(Key:单元格名称,val:报表名.指标名)
//				replaceMap.put("('" + tmp.getCellNo() + "')", "('" + tmp.getRptNm() + "." + tmp.getLineNm() + "." + tmp.getIndexNm() + "')");
//				replaceDescMap.put("('" + tmp.getCellNo() + "')", "[" + tmp.getIndexNm() + "]");
			}
		}
	}

	/**
	 * 根据校验公式表述，构造相关数据(根据报表指标编号进行构造)
	 * @param vo
	 * @param replaceMap
	 * @return
	 */
	private ValidErrorInfoObj structureRptValidVO(CfgextLogicVO vo, Map<String, String> replaceMap, Map<String, String> replaceDescMap) {
		String leftExpression = vo.getLeftExpression();//左表达式
		String rightExpression = vo.getRightExpression();//右表达式
		//进行校验公式转换（将跨期公式中带I的先将I去掉）
		leftExpression = changeRptFunc(leftExpression);
		rightExpression = changeRptFunc(rightExpression);
		vo.setLeftExpression(leftExpression);
		vo.setRightExpression(rightExpression);
		
		String logicOperType = vo.getLogicOperType();//逻辑运算符
		String expressionShortDesc =  leftExpression;
		if(StringUtils.isNotBlank(leftExpression) && StringUtils.isNotBlank(rightExpression)){
			Pattern pattern = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[_]|[-])+'\\)");
			Pattern cpattern = Pattern.compile("C\\('([YMD])+'\\)");
			Matcher leftMatcher = pattern.matcher(leftExpression);
			Matcher cleftMatcher = cpattern.matcher(leftExpression);
			List<String> clist = new ArrayList<>();
			while (cleftMatcher.find()){
				String s = cleftMatcher.group(0).substring(3, cleftMatcher.group(0).length()-2);
				clist.add(s);
			}
			StringBuffer leftSb = new StringBuffer();
			while (leftMatcher.find()) {
				//第一次没有，可能是没有加载这种报表的数据，先去库里查一次
				if (replaceMap.get(leftMatcher.group(0)) == null) {
					String rptIndexNo = leftMatcher.group(0).substring(2, leftMatcher.group(0).length()-2);
					if(!clist.contains(rptIndexNo)){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
						map.put("rptIndexNo", rptIndexNo);
						map.put("endDate", vo.getEndDate());
						List<NameAndIdMap> rptIndexList = this.logicDao.getNameAndNo(map);
						if(rptIndexList != null && rptIndexList.size() > 0){
							String rptNm = rptIndexList.get(0).getRptNm();
							map.remove("rptIndexNo");
							map.put("rptNm", rptNm);
							this.strNmAndNo(map, replaceMap, replaceDescMap);
						}
					}
				}
				
				//第二次没有，那就是真的没有
				if (replaceMap.get(leftMatcher.group(0)) == null) {
					String rptIndexNo = leftMatcher.group(0).substring(2, leftMatcher.group(0).length()-2);
					if(!clist.contains(rptIndexNo)){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(4);
						obj.setValidTypeNm("指标校验");
						obj.setErrorMsg(leftMatcher.group(0) + "不存在,请核对左表达式配置！");
						return obj;
					}
				}else{
					if(leftExpression.substring(leftMatcher.start() - 1, leftMatcher.start()).equals("I")){//一般指标公式
						leftMatcher.appendReplacement(leftSb, replaceMap.get(leftMatcher.group(0)));
						expressionShortDesc = expressionShortDesc.replace(leftMatcher.group(0), replaceDescMap.get(leftMatcher.group(0)));
					}else{//涉及到比上期等函数的使用
						leftMatcher.appendReplacement(leftSb, "(I" + replaceMap.get(leftMatcher.group(0)) + ")");
						expressionShortDesc = expressionShortDesc.replace(leftMatcher.group(0), "[I" + replaceDescMap.get(leftMatcher.group(0)) + "]");
					}
				}
			}
			leftMatcher.appendTail(leftSb);
			expressionShortDesc = expressionShortDesc + logicOperType + rightExpression;
			Matcher rightMatcher = pattern.matcher(rightExpression);
			Matcher crightMatcher = cpattern.matcher(rightExpression);
			List<String> crightList = new ArrayList<>();
			while (crightMatcher.find()){
				String s = crightMatcher.group(0).substring(3, crightMatcher.group(0).length()-2);
				crightList.add(s);
			}
			StringBuffer rightSb = new StringBuffer();
			String checkType = "01";//表内校验
			while (rightMatcher.find()) {
				//第一次没有，可能是没有加载这种报表的数据，先去库里查一次
				if (replaceMap.get(rightMatcher.group(0)) == null) {
					String rptIndexNo = rightMatcher.group(0).substring(2, rightMatcher.group(0).length()-2);
					if(!crightList.contains(rptIndexNo)){
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
						map.put("rptIndexNo", rptIndexNo);
						map.put("endDate", vo.getEndDate());
						List<NameAndIdMap> rptIndexList = this.logicDao.getNameAndNo(map);
						if(rptIndexList != null && rptIndexList.size() > 0){
							String rptNm = rptIndexList.get(0).getRptNm();
							map.remove("rptIndexNo");
							map.put("rptNm", rptNm);
							this.strNmAndNo(map, replaceMap, replaceDescMap);
						}
					}
				}
				//第二次没有，那就是真的没有
				if (replaceMap.get(rightMatcher.group(0)) == null) {
					String rptIndexNo = rightMatcher.group(0).substring(2, rightMatcher.group(0).length()-2);
					if(!crightList.contains(rptIndexNo)){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(6);
						obj.setValidTypeNm("指标校验");
						obj.setErrorMsg(rightMatcher.group(0) + "不存在,请核对右表达式配置！");
						return obj;
					}
				}else{
					if(replaceMap.get(rightMatcher.group(0)).indexOf(vo.getRptNm()) < 0) {
						checkType = "02";//表间校验
					}
					if(rightExpression.substring(rightMatcher.start() - 1, rightMatcher.start()).equals("I")){//一般指标公式
						rightMatcher.appendReplacement(rightSb, replaceMap.get(rightMatcher.group(0)));
						expressionShortDesc = expressionShortDesc.replace(rightMatcher.group(0), replaceDescMap.get(rightMatcher.group(0)));
					}else{//涉及到比上期等函数的使用
						rightMatcher.appendReplacement(rightSb, "(I" + replaceMap.get(rightMatcher.group(0)) + ")");
						expressionShortDesc = expressionShortDesc.replace(rightMatcher.group(0),  "[I" + replaceDescMap.get(rightMatcher.group(0)) + "]");
					}
				}
			}
			rightMatcher.appendTail(rightSb);
			
			String expressionDesc = "(" + leftSb + ")" + logicOperType + "(" + rightSb + ")";
			
			//新增校验公式
			if(StringUtils.isBlank(vo.getCheckId())) {
				vo.setCheckId(RandomUtils.uuid2());
			}
			//是否预校验，默认否
			if(StringUtils.isBlank(vo.getIsPre())) {
				vo.setIsPre("0");
			}
			//是否机构过滤，默认否
			if(StringUtils.isBlank(vo.getIsOrgFilter())) {
				vo.setIsOrgFilter("N");
			}
			vo.setCheckType(checkType);
			vo.setExpressionDesc(expressionDesc.toString());
			vo.setExpressionShortDesc(expressionShortDesc.toString());
			
		}else{
			ValidErrorInfoObj obj = new ValidErrorInfoObj();
			obj.setFieldName(vo.getRptNm());
			obj.setSheetName(vo.getSheetName());
			obj.setExcelRowNo(vo.getExcelRowNo());
			obj.setExcelColNo(4);
			obj.setValidTypeNm("公式配置校验");
			obj.setErrorMsg("左表达式或右表达式为空，请检查公式配置！");
			return obj;
		}
		return null;
	}
	
	/**
	 * 根据校验公式表述，构造相关数据(根据报表名称和单元格名称进行构造)
	 * @param vo
	 * @param replaceMap
	 * @param replaceDescMap
	 * @param isNmImp
	 * @return
	 */
	private ValidErrorInfoObj structureRptValidVOByNm(CfgextLogicVO vo, Map<String, String> replaceMap, Map<String, String> replaceDescMap) {
		String leftExpression = vo.getLeftExpression();//左表达式
		String rightExpression = vo.getRightExpression();//右表达式
		String logicOperType = vo.getLogicOperType();//逻辑运算符
		String expressionDesc = "(" + leftExpression + ")" + logicOperType + "(" + rightExpression + ")";
		
		//进行校验公式转换（将跨期公式中带I的先将I去掉）
		leftExpression = changeRptFunc(leftExpression);
		rightExpression = changeRptFunc(rightExpression);	
		
		String expressionShortDesc =  leftExpression;
		if(StringUtils.isNotBlank(leftExpression) && StringUtils.isNotBlank(rightExpression)){
			Pattern pattern = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[-]|[，]|[,]|[%]|[+]|[\\[]|[\\]])+'\\)");
			Pattern cpattern = Pattern.compile("C\\('([YMD])+'\\)");
			Matcher leftMatcher = pattern.matcher(leftExpression);
			Matcher cleftMatcher = cpattern.matcher(leftExpression);
			List<String> cleftList = new ArrayList<>();
			while(cleftMatcher.find()){
				String s = cleftMatcher.group(0);
				cleftList.add(s.substring(3, s.length()-1));
			}
			StringBuffer leftSb = new StringBuffer();
			while (leftMatcher.find()) {
				//第一次没有，可能是没有加载这种报表的数据，先去库里查一次
				if (replaceMap.get(leftMatcher.group(0)) == null) {
					String logicText = leftMatcher.group(0);
					logicText = logicText.substring(2, logicText.length()-1);
					if(!cleftList.contains(logicText)){
						String[] logicTexts = logicText.split("\\.");
						if(logicTexts.length > 1) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
							map.put("rptNm", logicTexts[0]);
							map.put("endDate", vo.getEndDate());
							this.strNmAndNo(map, replaceMap, replaceDescMap);
						}
					}
				}
				//第二次没有，那就是真的没有
				if (replaceMap.get(leftMatcher.group(0)) == null) {
					String logicText = leftMatcher.group(0);
					logicText = logicText.substring(2, logicText.length()-1);
					if(!cleftList.contains(logicText)){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(3);
						obj.setValidTypeNm("指标校验");
						obj.setErrorMsg(leftMatcher.group(0) + "不存在,请核对校验公式配置！");
						return obj;
					}
				}else{
					if(leftExpression.substring(leftMatcher.start() - 1, leftMatcher.start()).equals("I")){//一般指标公式
						expressionShortDesc = expressionShortDesc.replace(leftMatcher.group(0), replaceDescMap.get(leftMatcher.group(0)));
					}else{//涉及到比上期等函数的使用
						expressionShortDesc = expressionShortDesc.replace(leftMatcher.group(0), "[I" + replaceDescMap.get(leftMatcher.group(0)) + "]");
					}
					leftMatcher.appendReplacement(leftSb, replaceMap.get(leftMatcher.group(0)));
				}
			}
			leftMatcher.appendTail(leftSb);
			expressionShortDesc = expressionShortDesc + logicOperType + rightExpression;
			Matcher rightMatcher = pattern.matcher(rightExpression);
			Matcher crightMatcher = cpattern.matcher(rightExpression);
			List<String> crightList = new ArrayList<>();
			while (crightMatcher.find()){
				String s = crightMatcher.group(0);
				crightList.add(s.substring(3, s.length()-1));
			}
			StringBuffer rightSb = new StringBuffer();
			String checkType = "01";//表内校验
			while (rightMatcher.find()) {
				//第一次没有，可能是没有加载这种报表的数据，先去库里查一次
				if (replaceMap.get(rightMatcher.group(0)) == null) {
					String logicText = rightMatcher.group(0);
					logicText = logicText.substring(2, logicText.length()-1);
					if(!crightList.contains(logicText)){
						String[] logicTexts = logicText.split("\\.");
						if(logicTexts.length > 1) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
							map.put("rptNm", logicTexts[0]);
							map.put("endDate", vo.getEndDate());
							this.strNmAndNo(map, replaceMap, replaceDescMap);
						}
					}
				}
				//第二次没有，那就是真的没有
				if (replaceMap.get(rightMatcher.group(0)) == null) {
					String logicText = rightMatcher.group(0);
					logicText = logicText.substring(2, logicText.length()-1);
					if(!crightList.contains(logicText)){
						ValidErrorInfoObj obj = new ValidErrorInfoObj();
						obj.setFieldName(vo.getRptNm());
						obj.setSheetName(vo.getSheetName());
						obj.setExcelRowNo(vo.getExcelRowNo());
						obj.setExcelColNo(3);
						obj.setValidTypeNm("指标校验");
						obj.setErrorMsg(rightMatcher.group(0) + "不存在,请核对校验公式配置！");
						return obj;
					}
				}else{
					if(rightMatcher.group(0).indexOf(vo.getRptNm()) < 0) {
						checkType = "02";//表间校验
					}
					if(rightExpression.substring(rightMatcher.start() - 1, rightMatcher.start()).equals("I")){//一般指标公式
						expressionShortDesc = expressionShortDesc.replace(rightMatcher.group(0), replaceDescMap.get(rightMatcher.group(0)));
					}else{//涉及到比上期等函数的使用
						expressionShortDesc = expressionShortDesc.replace(rightMatcher.group(0), "[I" + replaceDescMap.get(rightMatcher.group(0)) + "]");
					}
					rightMatcher.appendReplacement(rightSb, replaceMap.get(rightMatcher.group(0)));
				}
			}
			rightMatcher.appendTail(rightSb);
			//转换完一条公式，检查一下是否全部转换为指标编号
//			StringBuffer logicSb = new StringBuffer();
//			logicSb.append(leftSb);
//			logicSb.append(rightSb);
//		    Pattern pchinese = Pattern.compile("[\u4e00-\u9fa5]");
//		    Matcher mchinese = pchinese.matcher(logicSb);
//		    if (mchinese.find()) {
//				ValidErrorInfoObj obj = new ValidErrorInfoObj();
//				obj.setFieldName(vo.getRptNm());
//				obj.setSheetName(vo.getSheetName());
//				obj.setExcelRowNo(vo.getExcelRowNo());
//				obj.setExcelColNo(3);
//				obj.setValidTypeNm("指标校验");
//				obj.setErrorMsg(leftExpression + logicOperType + rightExpression + " ：公式有错误,请核对校验公式配置！");
//				return obj;
//		    }
			
			//新增校验公式
			if(StringUtils.isBlank(vo.getCheckId())) {
				vo.setCheckId(RandomUtils.uuid2());
			}
			//是否预校验，默认否
			if(StringUtils.isBlank(vo.getIsPre())) {
				vo.setIsPre("0");
			}
			//是否机构过滤，默认否
			if(StringUtils.isBlank(vo.getIsOrgFilter())) {
				vo.setIsOrgFilter("N");
			}
			vo.setLeftExpression(leftSb.toString());
			vo.setRightExpression(rightSb.toString());
			vo.setCheckType(checkType);
			vo.setExpressionDesc(expressionDesc.toString());
			vo.setExpressionShortDesc(expressionShortDesc.toString());
			
		}else{
			ValidErrorInfoObj obj = new ValidErrorInfoObj();
			obj.setFieldName(vo.getRptNm());
			obj.setSheetName(vo.getSheetName());
			obj.setExcelRowNo(vo.getExcelRowNo());
			obj.setExcelColNo(4);
			obj.setValidTypeNm("公式配置校验");
			obj.setErrorMsg("左表达式或右表达式为空，请检查公式配置！");
			return obj;
		}
		return null;
	}
	
	/**
	 * 根据传入报表名称构造报表指标信息
	 * @param map
	 * @param replaceMap
	 * @param replaceDescMap
	 */
	private void strNmAndNo(Map<String, Object> map, Map<String, String> replaceMap, Map<String, String> replaceDescMap) {
		//构造相关的报表指标信息
		List<NameAndIdMap> list = this.logicDao.getNameAndNo(map);
		for(NameAndIdMap tmp : list){
			if(StringUtils.isBlank(tmp.getLineId())){
				//构造(Key:指标编号,val:报表名称.单元格名称)
				replaceMap.put("('" + tmp.getIndexNo() + "')", "('" + tmp.getRptNm() + "." + tmp.getIndexNm() + "')");
				replaceDescMap.put("('" + tmp.getIndexNo() + "')", "[" + tmp.getIndexNm() + "]");
				//构造(Key:报表名称.单元格名称,val:指标编号)
				replaceMap.put("('" + tmp.getRptNm() + "." + tmp.getIndexNm() + "')", "('" + tmp.getIndexNo() + "')");
				replaceDescMap.put("('" + tmp.getRptNm() + "." + tmp.getIndexNm() + "')", "[" + tmp.getIndexNm() + "]");
			}else{
				//构造(Key:指标编号,val:报表名称.单元格名称)
				replaceMap.put("('" + tmp.getIndexNo() + "')", "('" + tmp.getRptNm() + "." + tmp.getLineNm() + "." + tmp.getIndexNm() + "')");
				replaceDescMap.put("('" + tmp.getIndexNo() + "')", "[" + tmp.getIndexNm() + "]");
				//构造(Key:报表名称.单元格名称,val:指标编号)
				replaceMap.put("('" + tmp.getRptNm() + "." + tmp.getLineNm() + "." + tmp.getIndexNm() + "')", "('" + tmp.getIndexNo() + "')");
				replaceDescMap.put("('" + tmp.getRptNm() + "." + tmp.getLineNm() + "." + tmp.getIndexNm() + "')", "[" + tmp.getIndexNm() + "]");
			}
		}
	}
	
	/**
	 * 根据指标编号删除检验公式
	 * @param idxNo
	 */
	@Transactional(readOnly = false)
	public void deleteByIdxNo(String idxNo) {
		if (StringUtils.isNotBlank(idxNo)) {
			List<String> idList = new ArrayList<String>();
			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("idxNo", idxNo);
			condition.put("endDate", "29991231");
			idList = logicDao.findCheckIds(condition);
			if(null != idList && idList.size() > 0) {
				//进行1000为一个单元的分割
				List<List<String>> idLists = SplitStringBy1000.change(idList);
				for(List<String> idListBy1000 : idLists) {
					condition.clear();
					condition.put("ids", idListBy1000);
					//删除检验信息表
					this.logicDao.deleteLogic(condition);
					//删除逻辑校验和指标的关系表
					this.logicDao.deleteLogicRel(condition);
					//删除校验的报表的关系表
					this.logicDao.deleteLogicRptRel(condition);
				}
			}
		}
	}
}