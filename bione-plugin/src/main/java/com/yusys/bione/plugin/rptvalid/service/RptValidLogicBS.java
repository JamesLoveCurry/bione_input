package com.yusys.bione.plugin.rptvalid.service;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.design.repository.RptTmpDAO;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.*;
import com.yusys.bione.plugin.rptidx.repository.IdxDimRelMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.IdxInfoMybatisDao;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxDimTypeRelVO;
import com.yusys.bione.plugin.rptvalid.entity.RptValidDimRel;
import com.yusys.bione.plugin.rptvalid.entity.RptValidDimRelPK;
import com.yusys.bione.plugin.valid.check.ValidateException;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicIdxRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicIdxRelPK;
import com.yusys.bione.plugin.valid.repository.FunAndSymbolMybatisDao;
import com.yusys.bione.plugin.valid.repository.ValidLogicMybatisDao;
import com.yusys.bione.plugin.valid.service.RptValidGroupBS;
import com.yusys.bione.plugin.valid.utils.ValidateExpressionUtils;
import com.yusys.bione.plugin.valid.web.vo.CfgextLogicVO;
import com.yusys.bione.plugin.valid.web.vo.IdxInfoVO;
import com.yusys.bione.plugin.valid.web.vo.NameAndIdMap;
import com.yusys.bione.plugin.valid.web.vo.RptValidLogicIdxRelVO;
import com.yusys.bione.plugin.wizard.web.vo.IdxLogicImportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class RptValidLogicBS extends BaseBS<RptValidCfgextLogic>{

	@Autowired
	private ValidLogicMybatisDao logicDao;
	
	@Autowired
	private FunAndSymbolMybatisDao funAndSymbolMybatisDao;
	
	@Autowired
	private IdxDimRelMybatisDao dimRelDao;
	
	@Autowired
	public RptTmpDAO rptTmpDAO;
	
	@Autowired
	private IdxInfoMybatisDao idxDao;

	@Autowired
	public IdxInfoBS idxInfoBS;
	
	@Autowired
	private RptValidGroupBS groupBS;
	
	public List<RptValidCfgextLogic> getAllExpression(){
		String jql = "select logic from RptValidCfgextLogic logic";
		List<RptValidCfgextLogic> list = this.baseDAO.findWithIndexParam(jql);
		return list;
	}
	
	public Map<String, Object> list(Pager pager, String indexCatalogNo, 
			String indexNo,String defSrc) {
		Map<String, String> map = new HashMap<String, String>();
	    map.put("indexNo", indexNo);
	    if(pager.getCondition() != null ){
	    	pager.setCondition(StringUtils.replace(pager.getCondition(), "'", "''"));
	    }
	    PageHelper.startPage(pager);
	    PageMyBatis<RptValidCfgextLogic> page = (PageMyBatis<RptValidCfgextLogic>) this.logicDao
	    		.indexList(map);
        
	    Map<String, Object> result = new HashMap<String, Object>();
	    result.put("Rows", page.getResult());
	    result.put("Total", page.getTotalCount());
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
	public void saveLogic(RptValidCfgextLogic logic, String leftFormulaIndex, String rightFormulaIndex) {
		if(logic.getCheckId() != null && !logic.getCheckId().equals("")){//修改
			Map<String, Object> map = new HashMap<String, Object>();
			List<String> idList = new ArrayList<String>();
			idList.add(logic.getCheckId());
			map.put("ids", idList);
			this.logicDao.deleteLogic(map);
			this.logicDao.insertLogic(logic);
			
		}else{
			String checkId = RandomUtils.uuid2();
			logic.setCheckId(checkId);
			this.logicDao.insertLogic(logic);
		}
		
		//保存逻辑校验和指标的关系表
		String leftIdxNos[] = StringUtils.split(leftFormulaIndex, ',');
		String rightIdxNos[] = StringUtils.split(rightFormulaIndex, ',');
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("checkId", logic.getCheckId());
		this.logicDao.deleteLogicRel(map);
		
		if(!StringUtils.isEmpty(leftFormulaIndex)){
			for(String tmp : leftIdxNos){
				RptValidLogicIdxRelPK pk = new RptValidLogicIdxRelPK();
				RptValidLogicIdxRel rel = new RptValidLogicIdxRel();
				pk.setCheckId(logic.getCheckId());
				pk.setIndexNo(tmp);
				pk.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_LEFT);
				pk.setTemplateId("-");
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
				pk.setTemplateId("-");
				rel.setId(pk);
				this.logicDao.insertLogicRel(rel);
			}
		}
		
	}

	public CfgextLogicVO getInfo(String checkId) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("checkId", checkId);
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

	@Transactional(readOnly = false)
	public void delete(String checkIds) {
		if (checkIds != null && !checkIds.equals("")) {
			if (checkIds.endsWith(",")) {
				checkIds = checkIds.substring(0, checkIds.length() - 1);
			}
			String id[] = StringUtils.split(checkIds, ',');
			List<String> idList = new ArrayList<String>();
			for (String tmp : id) {
				idList.add(tmp);
			}

			Map<String, Object> condition = new HashMap<String, Object>();
			condition.put("ids", idList);
			this.logicDao.deleteLogic(condition);
			
			String jql = " delete from RptValidDimRel rel where rel.id.checkId in (:ids) ";
			this.baseDAO.batchExecuteWithNameParam(jql,condition); 
			
			//删除逻辑校验和指标的关系表
			this.logicDao.deleteLogicRel(condition);
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
			tmp.setTitle(symbol.getSymbolDisplay());
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
			tmp.setTitle(func.getFormulaDisplay());
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

	/**
	 * 查询指标信息表所有存在的指标
	 * @return 指标号实体
	 */
	public List<Object[]> getIndexNoList() {
		String sql = "select info.INDEX_NO,info.INDEX_NM from RPT_IDX_INFO info where info.END_DATE = ?0 and info.IS_RPT_INDEX = ?1 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql, "29991231","N");
	}
	
	/**
	 * 查询指标信息表所有存在的指标及度量信息
	 * @return 指标号实体
	 */
	public List<Object[]> getIndexAndMeasureList() {
		String sql = "select (case when instr((info.INDEX_NO||'.'||mr.measure_no),'INDEX_VAL')>0 "
				+ " then info.INDEX_NO else (info.INDEX_NO||'.'||mr.measure_no) end)  as compNo,"
				+ " (case when instr((info.INDEX_NM||'.'||m.measure_nm),'值')>0 "
				+ " then info.INDEX_NM else (info.INDEX_NM||'.'||m.measure_nm) end)  as compNm"
				+ " from RPT_IDX_INFO info "
				+ " left join RPT_IDX_MEASURE_REL mr on mr.index_no = info.index_no"
				+ " left join RPT_IDX_MEASURE_INFO m on m.measure_no = mr.measure_no"
				+ " where info.END_DATE = ?0 and info.IS_RPT_INDEX = ?1 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql, "29991231","N");
	}
	
	/**
	 * 查询所有维度类型信息
	 * @return 所有维度类型
	 */
	public List<Object[]> getCheckDimList() {
		String sql = "select info.DIM_TYPE_NO,info.DIM_TYPE_NM,info.DIM_TYPE from RPT_DIM_TYPE_INFO info where info.DIM_STS = ?0 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql,"Y");
	}
	
	/**
	 * 查询指标逻辑校验中所有校验公式名称
	 * @return 校验公式实体
	 */
	public List<Object[]> getCheckIdList() {
		String sql = "select info.CHECK_ID,info.CHECK_NM from RPT_VALID_CFGEXT_LOGIC info where 1=1 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql);
	}
	
	public Map<String, Object> replaceExpression(String leftExpression, String rightExpression, String expression) {
		Map<String, String> replaceMap = new HashMap<String, String>();
		List<Object[]> idxs = this.getIndexAndMeasureList();
		
		if(null != idxs && idxs.size() > 0){//将查询到的主指标编号放入到map变量中
			for(Object[] idx : idxs){
				replaceMap.put("I('" + idx[1].toString() + "')", "I('" + idx[0].toString() + "')");
			}
		}
		
		Pattern pattern = Pattern.compile("I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|[\\[]|[\\]]|[－]|\\.)+'\\)");
		//"I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|[\\[]|[\\]]|[－]|\\.)+'\\)"
		//"I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[-])+'\\)"
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Set<String> leftSourceIdxSet = new HashSet<String>();
		Set<String> rightSourceIdxSet = new HashSet<String>();
		
		if (!StringUtils.isEmpty(leftExpression)) {
			Matcher matcher = pattern.matcher(leftExpression);
			StringBuffer sb = new StringBuffer();
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
			
			//替换数据模型
			Map<String, String> replaceModelMap = replaceModel(sb.toString());
			if(replaceModelMap.get("message") != null){
				resultMap.put("message", replaceModelMap.get("message"));
				return resultMap;
			}
			String exp = replaceModelMap.get("expression");
			//替换数据模型 结束
			//校验左表达式
//			try{
//			ValidateExpressionUtils.testFomula(leftExpression);
			try {
				ValidateExpressionUtils.validateFomula(leftExpression);
			} catch (ValidateException e) {
				resultMap.put("message", "左表达式中" + e.getMessage());
				return resultMap;
			}
//			}catch(Exception e){
//				resultMap.put("message", "左表达式中" + e.getMessage());
//				return resultMap;
//			}
			
			
			resultMap.put("leftExpression", changeRptFunc(exp));
		}
		if (!StringUtils.isEmpty(rightExpression)) {
			Matcher matcher = pattern.matcher(rightExpression);
			StringBuffer sb = new StringBuffer();
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
			
			//替换数据模型
			Map<String, String> replaceModelMap = replaceModel(sb.toString());
			if(replaceModelMap.get("message") != null){
				resultMap.put("message", replaceModelMap.get("message"));
				return resultMap;
			}
			String exp = replaceModelMap.get("expression");
			//替换数据模型 结束
			
			//校验右表达式
			try{
				ValidateExpressionUtils.validateFomula(rightExpression);
			}catch(Exception e){
				resultMap.put("message", "右表达式中" + e.getLocalizedMessage());
				return resultMap;
			}
			resultMap.put("rightExpression", changeRptFunc(exp));
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
			
			//校验表达式
			try{
				ValidateExpressionUtils.validateFomula(sb.toString());
			}catch(Exception e){
				resultMap.put("message", "表达式中" + e.getLocalizedMessage());
				return resultMap;
			}
			resultMap.put("expression", changeRptFunc(sb.toString()));
		}
		
		
		String leftSourceIdx = "";
		List<String> idxNoList = new ArrayList<String>();
		for(String str : leftSourceIdxSet){
			leftSourceIdx += str + ",";
			if(!idxNoList.contains(str)){
				idxNoList.add(str);
			}
		}
		if(leftSourceIdx.endsWith(",")){
			leftSourceIdx = leftSourceIdx.substring(0, leftSourceIdx.length() - 1);
		}
		resultMap.put("leftFormulaIndex", leftSourceIdx);
		
		String rightSourceIdx = "";
		for(String str : rightSourceIdxSet){
			rightSourceIdx += str + ",";
			if(!idxNoList.contains(str)){
				idxNoList.add(str);
			}
		}
		if(rightSourceIdx.endsWith(",")){
			rightSourceIdx = rightSourceIdx.substring(0, rightSourceIdx.length() - 1);
		}
		resultMap.put("rightFormulaIndex", rightSourceIdx);
		
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
		}
		
		return resultMap;
	}

	private String changeRptFunc(String expr){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("funcType", GlobalConstants4plugin.VALID_FUNC_TYPE_RPT);
		List<RptIdxFormulaFunc> funcs = this.funAndSymbolMybatisDao.listFunc(map);
		if(funcs == null || funcs.size() == 0)
			return expr;
		
		StringBuffer sb = new StringBuffer();
		String patternString = "";
		for(RptIdxFormulaFunc func : funcs){
			patternString = patternString  + func.getFormulaId() + "\\(I\\('([A-Z]|[a-z]|[0-9]|[_]|\\.)+'\\)\\)" + "|";
		}
		if(patternString.endsWith("|")){
			patternString = patternString.substring(0, patternString.length() - 1);
		}
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(expr);
		while (matcher.find()) {
			matcher.appendReplacement(sb,matcher.group(0).substring(0, matcher.group(0).indexOf("("))+ "(" + 
					matcher.group(0).substring(matcher.group(0).indexOf("'"), matcher.group(0).indexOf("'", matcher.group(0).indexOf("'") + 1)) + "')");
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

	/**
	 * 修改FJ 代码 
	 * @param rptId  待修改的报表ID，若传入空格则修改所有的报表
	 */
	public void syncExpressionDesc(String rptId) {
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<String> templateTypeList = new ArrayList<String>();
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_CELL);
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_COM);
		map.put("templateTypeList", templateTypeList);
		map.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_FRS);
		map.put("rptType", GlobalConstants4plugin.RPT_TYPE_DESIGN);
		map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		
		
		
		
		List<NameAndIdMap> list = this.logicDao.selectMapping(map);
		
		Map<String, String> replaceMap = new HashMap<String, String>();
		for(NameAndIdMap tmp : list){
			if(tmp.getLineId() == null || tmp.getLineId().equals("")){
				replaceMap.put("('" + tmp.getIndexNo() + "')", "('" + tmp.getRptNm() + ".主模板." + tmp.getIndexNm() + "')");
			}else{
				replaceMap.put("('" + tmp.getIndexNo() + "')", "('" + tmp.getRptNm() + "." + tmp.getLineNm() + "." + tmp.getIndexNm() + "')");
			}
		}
		Map<String, Object> codition = new HashMap<String, Object>();
		if(!"".equals(rptId)){
			codition.put("rptTemplateId", rptId);
		}
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
		expressionList = (List<CfgextLogicVO>) resultMap.values();
		
		if(expressionList != null && expressionList.size() > 0){
			Pattern pattern = Pattern.compile("\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[-])+'\\)");
			
			for(CfgextLogicVO logic : expressionList){
				
				String expression = "(" + logic.getLeftExpression() + ")" + logic.getLogicOperType() + "(" + logic.getRightExpression() + ")";
				Matcher matcher = pattern.matcher(expression);
				StringBuffer sb = new StringBuffer();
				boolean flag = false;
				while (matcher.find()) {
					if (replaceMap.get(matcher.group(0)) == null) {
						flag = true;
						/*"message", "表达式中第" + (matcher.start() + 1)
								+ "位的指标不存在，请修改！");
						return resultMap;*/
						
					}else{
						
						if(expression.substring(matcher.start() - 1, matcher.start()).equals("I")){//一般指标公式
							matcher.appendReplacement(sb,
									replaceMap.get(matcher.group(0)));
						}else{//涉及到比上期等函数的使用
							matcher.appendReplacement(sb,
									"(I" + replaceMap.get(matcher.group(0)) + ")");
						}
					}
					//leftSourceIdxSet.add(replaceMap.get(matcher.group(0)).substring(3, replaceMap.get(matcher.group(0)).length() - 2));
					
				}
				matcher.appendTail(sb);
				
				if(!flag){
					logic.setExpressionDesc(sb.toString());
					this.logicDao.updateLogic(logic);
				}
			}
			
			
		}
		
	}

	/**
	 * 
	 * @param formulaIndex 来源指标
	 * @return
	 */
	public Map<String, Object> checkCycleRef(String formulaIndex) {
		
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
	}
	
	/**
	 * 查询所选择指标对应的逻辑校验数据
	 *  @return 指标号实体 
	 */
	public List<IdxLogicImportVO> getAllLogicList(String idx) {
		String index =idx+".%";
		String sql =" select t1.check_nm,t1.left_Expression,t1.logic_Oper_Type,t1.right_Expression, "
				+ " t1.float_Val,t1.start_Date,t1.busi_Explain,t1.check_id from"
				+ " RPT_VALID_CFGEXT_LOGIC t1"
				+ " where t1.check_id in (select t.check_id from RPT_VALID_LOGIC_IDX_REL t "
				+ " where t.index_no = ?0 or t.index_no like ?1 and t.FORMULA_TYPE =?2)  ";
		List<Object[]> logicInfo = this.baseDAO.findByNativeSQLWithIndexParam(sql, idx ,index,"L");
		List<IdxLogicImportVO> vos = new ArrayList<IdxLogicImportVO>();
		for(Object[] logic: logicInfo ){
			String relateDim = "";
			if(logic[7]!= null){
				String sql1 = " select t1.DIM_TYPE_NM from RPT_VALID_DIM_REL t"
						+ " left join RPT_DIM_TYPE_INFO t1 on t1.DIM_TYPE_NO = t.DIM_NO "
						+ " where t.check_id = ?0 and t.valid_type = ?1"; 
				List<Object[]> dimList = this.baseDAO.findByNativeSQLWithIndexParam(sql1, logic[7],"01");
				if(dimList.size()>0){
					for(Object dim : dimList){
						relateDim += dim+","; 
					}
					relateDim = relateDim.substring(0, relateDim.length()-1);
				}
			}
			IdxLogicImportVO vo = new IdxLogicImportVO();
			Map<String,String> logicLeft = this.replaceNameByIdx(logic[1].toString());
			Map<String,String> logicRight = this.replaceNameByIdx(logic[3].toString());
			vo.setCheckNm(logic[0].toString());
			vo.setLeftExpression(logicLeft.get("expressionDesc")); 
			vo.setLogicOperType(logic[2].toString());
			vo.setRightExpression(logicRight.get("expressionDesc"));
			vo.setFloatVal(logic[4]!= null ? new BigDecimal(logic[4].toString()):new BigDecimal(""));
			vo.setStartDate(logic[5].toString());
			vo.setBusiExplain(logic[6]!= null ? logic[6].toString():"");
			vo.setRelateDim(relateDim);
			vos.add(vo);
		}
		return  vos;
	}
	
	private Map<String,String> replaceNameByIdx(String expression){
		Map<String, Object> con = Maps.newHashMap();
		Map<String, String> NoAndNameMap = new HashMap<String, String>();
		Map<String, String> resultMap = new HashMap<String, String>();
		List<RptIdxMeasureInfo> minfos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
		String testExpression = "";
		resultMap.put("expression", expression);
		if (!StringUtils.isEmpty(expression)) {
			testExpression = expression;
			Set<String> indexSet = new HashSet<String>();
			Pattern pattern = Pattern
					.compile("I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.)+'\\)");
			StringBuffer sb = new StringBuffer();
			Matcher matcher = pattern.matcher(testExpression);
			while (matcher.find()) {
				String indexNo = matcher.group(0).substring(
						matcher.group(0).indexOf("'") + 1,
						matcher.group(0).indexOf("'",
								matcher.group(0).indexOf("'") + 1));
				if (indexNo.contains(".")) {
					indexNo = indexNo.substring(0, indexNo.indexOf("."));
				}
				indexSet.add(indexNo);
			}
			if(indexSet != null && indexSet.size()>0){
				con.put("list", ReBuildParam.splitLists(new ArrayList<String>(indexSet)));
				List<RptIdxInfo> list = this.idxDao.listIdxInfo(con);
				if(list != null && list.size()>0){
					for (RptIdxInfo idx : list) {
						NoAndNameMap.put(idx.getId().getIndexNo(), idx.getIndexNm());
						if(idx.getIndexType().equals(GlobalConstants4plugin.SUM_ACCOUNT_INDEX)){
							for(RptIdxMeasureInfo info : minfos){
								NoAndNameMap.put(
										idx.getId().getIndexNo() + "." + info.getMeasureNo(),
										idx.getIndexNm() + "." + info.getMeasureNm());
							}
						}
					}
				}
				matcher.reset();
				while (matcher.find()) {
					String indexNo = matcher.group(0).substring(
							matcher.group(0).indexOf("'") + 1,
							matcher.group(0).indexOf("'",
									matcher.group(0).indexOf("'") + 1));
					if (NoAndNameMap.get(indexNo) == null) {
						resultMap.put("message", "表达式中第" + (matcher.start() + 1)
								+ "位的指标不存在，请修改！");
						return resultMap;
					}
					matcher.appendReplacement(sb, "I('" + NoAndNameMap.get(indexNo)
							+ "')");
				}
				matcher.appendTail(sb);
				resultMap.put("expressionDesc", sb.toString());
			}
			else{
				resultMap.put("expressionDesc", expression);
			}
			
		}
		else{
			resultMap.put("expressionDesc", "");
		}
		return resultMap;
	}
	
	/**
	 * @param expression
	 * @param expressionDesc
	 * @return
	 */
	public Map<String, String> splitNoByIdx(String expressionDesc) {
		Map<String, Object> con = Maps.newHashMap();
		List<RptIdxMeasureInfo> minfos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
		
		Map<String, String> NoAndNameMap = new HashMap<String, String>();
		Map<String, String> resultMap = new HashMap<String, String>();
		String testExpression = expressionDesc;

		Set<String> indexSet = new HashSet<String>();
		Set<String> indexNms = new HashSet<String>();
		Set<String> indexExi = new HashSet<String>();
		
		String indexNos = "";
		String idxNms = "";
		Pattern pattern = Pattern
				.compile("I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|[\\[]|[\\]]|\\.)+'\\)");
		Matcher matcher = pattern.matcher(testExpression);
		while (matcher.find()) {
			String indexNm = matcher.group(0).substring(
					matcher.group(0).indexOf("'") + 1,
					matcher.group(0).indexOf("'",matcher.group(0).indexOf("'") + 1));
			if (indexNm.contains(".")) {
				indexNm = indexNm.substring(0, indexNm.indexOf("."));
			}
			indexNms.add(indexNm);
		}
		
		if(indexNms != null && indexNms.size() > 0){
			con.put("indexNms", ReBuildParam.splitLists(new ArrayList<String>(indexNms)));
			List<RptIdxInfo> list = this.idxDao.listIdxInfo(con);
			if (!StringUtils.isEmpty(expressionDesc)) {// 将表达式描述替换为表达式
				for (RptIdxInfo idx : list) {
					NoAndNameMap.put(idx.getIndexNm(), idx.getId().getIndexNo());
					if(idx.getIndexType().equals(GlobalConstants4plugin.SUM_ACCOUNT_INDEX)){
						for(RptIdxMeasureInfo info : minfos){
							NoAndNameMap.put(
									idx.getIndexNm() + "." + info.getMeasureNm(),
									idx.getId().getIndexNo() + "." + info.getMeasureNo());
						}
					}
				}
			}
			matcher.reset();
			while (matcher.find()) {
				String indexNm = matcher.group(0).substring(
						matcher.group(0).indexOf("'") + 1,
						matcher.group(0).indexOf("'",
								matcher.group(0).indexOf("'") + 1));
				String indexNo = NoAndNameMap.get(indexNm);
				if (NoAndNameMap.get(indexNm) == null) {
					indexExi.add(indexNm);
				}
				
				indexSet.add(indexNo);
			}
			if(indexExi.size()>0){
				for (String tmp : indexExi) {
			    	idxNms += tmp + ",";
			    }
			    if (idxNms.endsWith(",")) {
			    	idxNms = idxNms.substring(0, idxNms.length() - 1);
			    }
			    resultMap.put("indexNms", idxNms);
			    return resultMap;
			}
			for (String tmp : indexSet) {
				indexNos += tmp + ",";
			}
			if (indexNos.endsWith(",")) {
				indexNos = indexNos.substring(0, indexNos.length() - 1);
			}
			resultMap.put("indexNos", indexNos);
		}
		return resultMap;
	}
	
	/**
	 * 查询导入的逻辑校验数据是否存在
	 *  @return checkId
	 */
	public String getLogicExistList(String checkNm) {
		String checkId = "";
		String sql =" select t.check_id from RPT_VALID_CFGEXT_LOGIC  t where t.check_nm = ?0  ";
		List<Object[]> checkIdList = this.baseDAO.findByNativeSQLWithIndexParam(sql, checkNm);
		if(checkIdList.size()>0){
			for(Object check : checkIdList){
				checkId = check.toString();
			}
		}
		return  checkId;
	}
	/*public String getLogicExistList(String expression) {
		String[] composite = expression.split(",");
		String checkId = "";
		String sql =" select t.check_id from RPT_VALID_CFGEXT_LOGIC  t where t.left_Expression = ?0 and "
				+ " t.logic_Oper_Type = ?1 and t.right_Expression = ?2";
		List<Object[]> checkIdList = this.baseDAO.findByNativeSQLWithIndexParam(sql, composite[0], composite[1], composite[2]);
		if(checkIdList.size()>0){
			for(Object check : checkIdList){
				checkId = check.toString();
			}
		}
		return  checkId;
	}*/
	/**
	 * 判断校验公式名称是否重复
	 * @param checkNm
	 * @return
	 */
	public Boolean testSameCheckNm(String checkNm,String checkId){
		Boolean flag = true;
		String jql = " select t.checkId from RptValidCfgextLogic t where t.checkNm = ?0";
		List<String> checkList = this.baseDAO.findWithIndexParam(jql, checkNm);
		if(checkId!=null && !"".equals(checkId)){
			if(checkList.size()>1){
				flag = false;
			}
		}else{
			if(checkList.size()>0){
				flag = false;
			}
		}
		return flag;
	}
	
	/**
	 * 获取异步树的目录节点
	 * @param upNode 上级节点
	 * @param basePath 工程路径
	 * @param validType 校验类型
	 * @return
	 */
	public List<CommonTreeNode> getValidIdxCtls(CommonTreeNode upNode, String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<String> allCtlNos = new ArrayList<String>();
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getLogicValidIdx());//规避oracle in 超过1000
		Map<String,Object> value = new HashMap<String, Object>();
		String jql = "select idx.indexCatalogNo from RptIdxInfo idx where idx.endDate =:endDate and (";
		for(int i=0;i<multiList.size();i++){
			String indexNos = "index" + i;
			if(i == 0){
				jql += " idx.id.indexNo in (:"+indexNos+") ";
			}else{
				jql += " or idx.id.indexNo in (:"+indexNos+") ";
			}
			value.put(indexNos, multiList.get(i));
		}
		jql += ")";
		value.put("endDate", "29991231");
		List<String> oneLvlctlNos = this.baseDAO.findWithNameParm(jql, value);
		if(null != oneLvlctlNos && oneLvlctlNos.size() > 0){
			allCtlNos.addAll(oneLvlctlNos);
			this.getAllUpCtlNos(oneLvlctlNos,allCtlNos);
			jql = "select ctl from RptIdxCatalog ctl where ctl.upNo = ?0 and ctl.indexCatalogNo in (?1)";
			List<RptIdxCatalog> ctls = this.baseDAO.findWithIndexParam(jql, upNode.getId(), allCtlNos);
			if(null != ctls && ctls.size() > 0){
				for(RptIdxCatalog ctl : ctls){
					CommonTreeNode node = new CommonTreeNode();
					Map<String, String> params = new HashMap<String, String>();
					params.put("nodeType", "idxCatalog");
					params.put("validType", validType);
					node.setId(ctl.getIndexCatalogNo());
					node.setUpId(upNode.getId());
					node.setText(ctl.getIndexCatalogNm());
					node.setTitle(ctl.getIndexCatalogNm());
					node.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
					node.setIsParent(true);
					node.setParams(params);
					rstNode.add(node);
				}
			}
		}
		return rstNode;
	}
	
	/**
	 * 获取异步树的指标节点
	 * @param upNode 上级节点
	 * @param basePath 工程路径
	 * @param validType 校验类型
	 * @return
	 */
	public List<CommonTreeNode> getValidIdxs(CommonTreeNode upNode, String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getLogicValidIdx());//规避oracle in 超过1000
		Map<String,Object> value = new HashMap<String, Object>();
		String jql = "select idx from RptIdxInfo idx where idx.indexCatalogNo = :indexCatalogNo and idx.endDate = :endDate and (";
		for(int i=0;i<multiList.size();i++){
			String indexNos = "index" + i;
			if(i == 0){
				jql += " idx.id.indexNo in (:"+indexNos+") ";
			}else{
				jql += " or idx.id.indexNo in (:"+indexNos+") ";
			}
			value.put(indexNos, multiList.get(i));
		}
		jql += ")";
		value.put("indexCatalogNo", upNode.getId());
		value.put("endDate", "29991231");
		List<RptIdxInfo> idxs = this.baseDAO.findWithNameParm(jql, value);
		if(null != idxs && idxs.size() > 0){
			for(RptIdxInfo idx : idxs){
				CommonTreeNode node = new CommonTreeNode();
				Map<String, String> params = new HashMap<String, String>();
				params.put("nodeType", "idxInfo");
				params.put("validType", validType);
				params.put("idxType", idx.getIndexType());
				node.setId(idx.getId().getIndexNo());
				node.setUpId(upNode.getId());
				node.setText(idx.getIndexNm());
				node.setTitle(idx.getIndexNm());
				node.setIcon(basePath + "/images/classics/menuicons/grid.png");
				node.setIsParent(true);
				node.setParams(params);
				rstNode.add(node);
			}
		}
		return rstNode;
	}
	
	/**
	 * 获取异步树的校验名称节点
	 * @param upNode 上级节点
	 * @param basePath 工程路径
	 * @param validType 校验类型
	 * @return
	 */
	public List<CommonTreeNode> getIdxLogicValid(CommonTreeNode upNode,String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<String> vIdxs = this.getValidIdxByUpNo(upNode.getId());
		if(null != vIdxs && vIdxs.size() > 0){
			String sql = " select rel.INDEX_NO,rel.CHECK_ID,rel.FORMULA_TYPE,loc.CHECK_NM "
					   + " from RPT_VALID_LOGIC_IDX_REL rel,RPT_VALID_CFGEXT_LOGIC loc "
					   + " where rel.INDEX_NO in (?0) and rel.FORMULA_TYPE = ?1 and rel.CHECK_ID = loc.CHECK_ID";
			List<Object[]> checks = this.baseDAO.findByNativeSQLWithIndexParam(sql, vIdxs, "L");
			if(null != checks && checks.size() > 0){
				for(Object[] obj : checks){
					CommonTreeNode node = new CommonTreeNode();
					Map<String, String> params = new HashMap<String, String>();
					params.put("nodeType", "check");
					params.put("validType", validType);
					node.setId(obj[1].toString());
					node.setUpId(upNode.getId());
					node.setText(obj[3] != null && !"".equals(obj[3]) ? obj[3].toString() : "");
					node.setTitle(obj[3] != null && !"".equals(obj[3]) ? obj[3].toString() : "");
					node.setIcon(basePath + "/images/classics/icons/chart_organisation.png");
					node.setParams(params);
					rstNode.add(node);
				}
			}
		}
		return rstNode;
	}
	
	/**
	 * 获取异步树的度量节点
	 * @param upNode 上级节点
	 * @param basePath 工程路径
	 * @param validType 校验类型
	 * @return
	 */
	public List<CommonTreeNode> getIdxMeasure(CommonTreeNode upNode,String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<String> indexNos = new ArrayList<String>();
		indexNos.add(upNode.getId());
		Map<String, String> glMap = this.groupBS.getMeasureRelMapping(indexNos);
		String jql = "select rel from RptValidLogicIdxRel rel where rel.id.indexNo in (?0) and rel.id.formulaType = ?1";
		List<RptValidLogicIdxRel> logics = this.baseDAO.findWithIndexParam(jql, glMap.keySet(), "L");
		if(null != logics && logics.size() > 0){
			for(RptValidLogicIdxRel loc : logics){
				CommonTreeNode node = new CommonTreeNode();
				Map<String, String> params = new HashMap<String, String>();
				params.put("nodeType", "measureInfo");
				params.put("validType", validType);
				node.setId(loc.getId().getIndexNo());
				node.setUpId(upNode.getId());
				node.setText(glMap.get(loc.getId().getIndexNo()));
				node.setTitle(glMap.get(loc.getId().getIndexNo()));
				node.setIcon(basePath + "/images/classics/icons/icon_link.gif");
				node.setIsParent(true);
				node.setParams(params);
				rstNode.add(node);
			}
		}
		return rstNode;
	}
	
	@SuppressWarnings("serial")
	private List<String> getLogicValidIdx() {
		List<String> idxs = new ArrayList<String>();
		String jql = "select distinct loc.id.indexNo from RptValidLogicIdxRel loc,RptValidCfgextLogic ext where loc.id.formulaType = ?0 and loc.id.checkId = ext.checkId";
		List<String> vIdxs = this.baseDAO.findWithIndexParam(jql, "L");//只取左表达式
		if(null != vIdxs && vIdxs.size() > 0){
			for(String indexNo : vIdxs){
				if(StringUtils.isNotBlank(indexNo)){
					idxs.add(StringUtils.split(indexNo, ".")[0]);
				}
			}
		}
		return idxs.size() > 0 ? idxs : new ArrayList<String>(){{add("null");}};
	}
	
	public Map<String,Set<String>> getLogicValidIdxAndGlMeasure() {
		Map<String, Set<String>> rstMap = new HashMap<String, Set<String>>();
		Set<String> allLocIdx = new HashSet<String>();
		Set<String> glMeasure = new HashSet<String>();
		Set<String> idxChecks = new HashSet<String>();
		String jql = "select loc from RptValidLogicIdxRel loc,RptValidCfgextLogic ext where loc.id.formulaType = ?0 and loc.id.checkId = ext.checkId";
		List<RptValidLogicIdxRel> logic = this.baseDAO.findWithIndexParam(jql, "L");//只取左表达式
		if(null != logic && logic.size() > 0){
			for(RptValidLogicIdxRel rel : logic){
				if(StringUtils.isNotBlank(rel.getId().getIndexNo())){
					if(StringUtils.contains(rel.getId().getIndexNo(), ".")){
						glMeasure.add(rel.getId().getIndexNo());
						allLocIdx.add(StringUtils.split(rel.getId().getIndexNo(), ".")[0]);
					}else{
						allLocIdx.add(rel.getId().getIndexNo());
					}
					idxChecks.add(rel.getId().getCheckId() + ";" + rel.getId().getIndexNo());
				}
			}
			rstMap.put("allLocIdx", allLocIdx);
			rstMap.put("glMeasure", glMeasure);
			rstMap.put("idxChecks", idxChecks);
		}
		return rstMap;
	}
	
	private void getAllUpCtlNos(List<String> ctlNos,List<String> allCtlNos) {
		String jql = "select ctl.upNo from RptIdxCatalog ctl where ctl.indexCatalogNo in (?0)";
		ctlNos = this.baseDAO.findWithIndexParam(jql, ctlNos);
		if(null != ctlNos && ctlNos.size() > 0){
			allCtlNos.addAll(ctlNos);
			getAllUpCtlNos(ctlNos,allCtlNos);
		}
	}
	
	private List<String> getValidIdxByUpNo(String upNodeId) {//非总账
		String jql = "select rel.id.indexNo from RptValidLogicIdxRel rel where rel.id.indexNo = ?0 and rel.id.formulaType = ?1";
		return this.baseDAO.findWithIndexParam(jql, upNodeId, "L");
	}
	
	/**
	 * 查询异步树
	 */
	public List<CommonTreeNode> findSyncTreeByKeyWord(String basePath, String searchNm, String validType) {
		List<CommonTreeNode> rstNodes = new ArrayList<CommonTreeNode>();
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getLogicValidIdx());//规避oracle in 超过1000
		Map<String,Object> values = new HashMap<String, Object>();
		String jql = "select idx from RptIdxInfo idx where (idx.id.indexNo like :indexNo or idx.indexNm like :indexNm) and idx.endDate = :endDate and (";
		for(int i=0;i<multiList.size();i++){
			String indexNos = "index" + i;
			if(i == 0){
				jql += " idx.id.indexNo in (:"+indexNos+") ";
			}else{
				jql += " or idx.id.indexNo in (:"+indexNos+") ";
			}
			values.put(indexNos, multiList.get(i));
		}
		jql += ")";
		values.put("indexNo", "%"+searchNm+"%");
		values.put("indexNm", "%"+searchNm+"%");
		values.put("endDate", "29991231");
		List<RptIdxInfo> idxs = this.baseDAO.findWithNameParm(jql, values);
		if(null != idxs && idxs.size() > 0) {
			List<String> glIndexNo = new ArrayList<String>();//总账
			List<String> otIndexNo = new ArrayList<String>();//非总账
			List<String> oneLvlCtlNos = new ArrayList<String>();
			List<RptIdxCatalog> allCtlobj = new ArrayList<RptIdxCatalog>();
			for(RptIdxInfo idx : idxs) {
				CommonTreeNode node = new CommonTreeNode();
				Map<String, String> params = new HashMap<String, String>();
				params.put("nodeType", "idxInfo");
				params.put("validType", validType);
				params.put("idxType", idx.getIndexType());
				node.setId(idx.getId().getIndexNo());
				node.setUpId(idx.getIndexCatalogNo());
				node.setText(idx.getIndexNm());
				node.setTitle(idx.getIndexNm());
				node.setIcon(basePath + "/images/classics/menuicons/grid.png");
				node.setParams(params);
				rstNodes.add(node);
				oneLvlCtlNos.add(idx.getIndexCatalogNo());
				if("05".equals(idx.getIndexType())){
					glIndexNo.add(idx.getId().getIndexNo());
				}else{
					otIndexNo.add(idx.getId().getIndexNo());
				}
			}
			this.groupBS.getAllUpCtlNos(oneLvlCtlNos, allCtlobj);
			if(null != allCtlobj && allCtlobj.size() > 0) {
				for(RptIdxCatalog ctl : allCtlobj) {
					CommonTreeNode node = new CommonTreeNode();
					Map<String, String> params = new HashMap<String, String>();
					params.put("nodeType", "idxCatalog");
					params.put("validType", validType);
					node.setId(ctl.getIndexCatalogNo());
					node.setUpId(ctl.getUpNo());
					node.setText(ctl.getIndexCatalogNm());
					node.setTitle(ctl.getIndexCatalogNm());
					node.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
					node.setParams(params);
					rstNodes.add(node);
				}
			}
			if(null != glIndexNo && glIndexNo.size() > 0) {//总账
				Map<String, String> glMap = this.groupBS.getMeasureRelMapping(glIndexNo);
				jql = "select new com.yusys.bione.plugin.valid.web.vo.RptValidLogicIdxRelVO(rel, loc.checkNm) from RptValidLogicIdxRel rel, RptValidCfgextLogic loc where rel.id.indexNo in (?0) and rel.id.formulaType = ?1 and rel.id.checkId = loc.checkId";
				List<RptValidLogicIdxRelVO> vos = this.baseDAO.findWithIndexParam(jql, glMap.keySet(), "L");
				if(null != vos && vos.size() > 0){
					Map<String, String> multiMap = new HashMap<String, String>();
					for(RptValidLogicIdxRelVO vo : vos){
						if(null == multiMap.get(vo.getId().getIndexNo())){
							CommonTreeNode node = new CommonTreeNode();
							Map<String, String> params = new HashMap<String, String>();
							params.put("nodeType", "measureInfo");
							params.put("validType", validType);
							node.setId(vo.getId().getIndexNo());
							node.setUpId(StringUtils.split(vo.getId().getIndexNo(), ".")[0]);
							node.setText(glMap.get(vo.getId().getIndexNo()));
							node.setTitle(glMap.get(vo.getId().getIndexNo()));
							node.setIcon(basePath + "/images/classics/icons/icon_link.gif");
							node.setParams(params);
							rstNodes.add(node);
							multiMap.put(vo.getId().getIndexNo(), vo.getId().getIndexNo());
						}
						
						CommonTreeNode node = new CommonTreeNode();
						Map<String, String> params = new HashMap<String, String>();
						params.put("nodeType", "check");
						params.put("validType", validType);
						node.setId(vo.getId().getCheckId());
						node.setUpId(vo.getId().getIndexNo());
						node.setText(vo.getCheckNm());
						node.setTitle(vo.getCheckNm());
						node.setIcon(basePath + "/images/classics/icons/chart_organisation.png");
						node.setParams(params);
						rstNodes.add(node);
					}
				}
			}
			if(null != otIndexNo && otIndexNo.size() > 0){//非总账
				jql = "select new com.yusys.bione.plugin.valid.web.vo.RptValidLogicIdxRelVO(rel, loc.checkNm) from RptValidLogicIdxRel rel, RptValidCfgextLogic loc where rel.id.indexNo in (?0) and rel.id.formulaType = ?1 and rel.id.checkId = loc.checkId";
				List<RptValidLogicIdxRelVO> vos = this.baseDAO.findWithIndexParam(jql, otIndexNo, "L");
				if(null != vos && vos.size() > 0){
					for(RptValidLogicIdxRelVO vo : vos) {
						CommonTreeNode node = new CommonTreeNode();
						Map<String, String> params = new HashMap<String, String>();
						params.put("nodeType", "check");
						params.put("validType", validType);
						node.setId(vo.getId().getCheckId());
						node.setUpId(vo.getId().getIndexNo());
						node.setText(vo.getCheckNm());
						node.setTitle(vo.getCheckNm());
						node.setIcon(basePath + "/images/classics/icons/chart_organisation.png");
						node.setParams(params);
						rstNodes.add(node);
					}
				}
			}
		}
		return rstNodes;
	}
	/**
	 * 获取维度类型信息
	 * 
	 * @param idxNos
	 * @return
	 */
	public List<RptDimTypeInfo> getDimTypesByNos(List<String> idxNos) {
		if (idxNos == null || idxNos.size() <= 0) {
			return new ArrayList<RptDimTypeInfo>();
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("idxNos", idxNos);
		params.put("idxSize", idxNos.size());
		params.put("dimSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		List<RptDimTypeInfo> dims = this.rptTmpDAO.getDimTypes(params);
		return dims;
	}
	
	/**
	 * 构造维度树
	 * 
	 * @param params
	 * @return
	 */
	public List<CommonTreeNode> listDimTree(List<RptDimTypeInfo> types,String indexNos,String checkId) {
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		
		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId("0");
		baseNode.setText("全部");
		baseNode.setUpId("0");
		RptIdxDimTypeRelVO dimBase = new RptIdxDimTypeRelVO();
		baseNode.setData(dimBase);
		baseNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
		treeNodes.add(baseNode);
		
		
		if(null != types && types.size() > 0){//把机构放入到map里，便于判断
			for(RptDimTypeInfo dim : types){
				RptIdxDimTypeRelVO dimTypeRel= new RptIdxDimTypeRelVO();
				dimTypeRel.setDimNo(dim.getDimTypeNo() != null ? dim.getDimTypeNo() : ""); 
				dimTypeRel.setDimTypeNm(dim.getDimTypeNm() != null ? dim.getDimTypeNm() : "");
				if("DATE".equals(dim.getDimTypeNo())){
					dimTypeRel.setDimType("01");
					dimTypeRel.setJudge("1");
				}else if("ORG".equals(dim.getDimTypeNo())){
					dimTypeRel.setDimType("02");
					dimTypeRel.setJudge("1");
				}else if("CURRENCY".equals(dim.getDimTypeNo())){
					dimTypeRel.setDimType("03");
					dimTypeRel.setJudge(isChecked(checkId,dim.getDimTypeNo()));
				}else if("INDEXNO".equals(dim.getDimTypeNo())){
					continue;
				}else{
					dimTypeRel.setDimType("00");
					dimTypeRel.setJudge(isChecked(checkId,dim.getDimTypeNo()));
				}
				
				CommonTreeNode treeChildNode = new CommonTreeNode();
				treeChildNode.setId(dim.getDimTypeNo() != null ? dim.getDimTypeNo() : "");
				treeChildNode.setText(dim.getDimTypeNm() != null ? dim.getDimTypeNm() : "");
				treeChildNode.setUpId("0");
				treeChildNode.setData(dimTypeRel);
				treeChildNode.getParams().put("type", "dimInfo");
				treeChildNode.setIcon(basePath + GlobalConstants4frame.LOGIC_ORG_ICON);
				treeNodes.add(treeChildNode);
			}
		}
		return treeNodes;
	}

	public String  isChecked(String checkId,String dimNo){
		String isCheck = "0";
		String sql = " select t.CHECK_ID from RPT_VALID_DIM_REL t where t.CHECK_ID =?0 and t.DIM_NO = ?1";
		List<Object[]> checkList = this.baseDAO.findByNativeSQLWithIndexParam(sql, checkId,dimNo);
		if(checkList.size()>0){
			isCheck = "1";
		}
		return isCheck;
	}  
	
	/**
	 * 关联维度保存
	 * 
	 * @param ids
	 * 
	 */
	@Transactional(readOnly = false)
	public void saveDimInfo(String checkId,String ids,String dimTypes) {
		if(StringUtils.isNotEmpty(checkId) && StringUtils.isNotEmpty(ids) && StringUtils.isNotEmpty(dimTypes)){
			String jql = " delete from RptValidDimRel rel where rel.id.checkId = ?0 ";
			this.baseDAO.batchExecuteWithIndexParam(jql,checkId);
			String[] id = StringUtils.split(ids, ',');
		    String[] dimType = StringUtils.split(dimTypes, ',');
		    for(int i = 0,j = 1;i<id.length;i++){
		    	RptValidDimRel dimRel = new RptValidDimRel();
		    	RptValidDimRelPK pk = new RptValidDimRelPK();
		    	dimRel.setId(pk);
		    	pk.setCheckId(checkId);
		    	pk.setValidType("01");
		    	pk.setDimNo(id[i]);
		    	dimRel.setDimType(dimType[i]);
		    	if("01".equals(dimType[i])){
		    		dimRel.setStoreCol("DATA_DATE");
		    	}else if("02".equals(dimType[i])){
		    		dimRel.setStoreCol("ORG_NO");
		    	}else if("03".equals(dimType[i])){
		    		dimRel.setStoreCol("CURRENCY");
		    	}else{
		    		dimRel.setStoreCol("DIM"+j);
		    		j++;
		    	}
		        this.groupBS.saveEntity(dimRel);
		       } 
		}
	}
}