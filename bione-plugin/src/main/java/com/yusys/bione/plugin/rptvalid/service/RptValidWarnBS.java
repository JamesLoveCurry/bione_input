package com.yusys.bione.plugin.rptvalid.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxDimTypeRelVO;
import com.yusys.bione.plugin.rptvalid.entity.RptValidDimRel;
import com.yusys.bione.plugin.rptvalid.entity.RptValidDimRelPK;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.entitiy.RptValidWarnLevel;
import com.yusys.bione.plugin.valid.entitiy.RptValidWarnLevelPK;
import com.yusys.bione.plugin.valid.repository.ValidWarnLevelMybatisDao;
import com.yusys.bione.plugin.valid.repository.ValidWarnMybatisDao;
import com.yusys.bione.plugin.valid.service.RptValidGroupBS;
import com.yusys.bione.plugin.valid.web.vo.ValidCfgextWarnVO;
import com.yusys.bione.plugin.wizard.web.vo.IdxWarnImportVO;

@Service
@Transactional(readOnly = true)
public class RptValidWarnBS extends BaseBS<RptValidCfgextWarn>{

	@Autowired
	private ValidWarnMybatisDao warnDao;
	
	@Autowired
	private ValidWarnLevelMybatisDao levelDao;
	
	@Autowired
	private RptValidGroupBS groupBS;
	
	
	public Map<String, Object> list(Pager pager, String indexCatalogNo, 
			String indexNo,String defSrc) {
	    Map<String, String> map = new HashMap<String, String>();
	    String indexNo1 = null;
	    if(indexNo!=null){
	    	if(!indexNo.contains(".")){//contains返回true或false
	    		indexNo1 = indexNo+".%";
		    }
	    }
	    map.put("indexNo1", indexNo1);
	    map.put("indexCatalogNo", indexCatalogNo);
	    map.put("indexNo", indexNo);
	    map.put("defSrc", defSrc);
	    PageHelper.startPage(pager);
	    PageMyBatis<ValidCfgextWarnVO> page = (PageMyBatis<ValidCfgextWarnVO>) this.warnDao.listWarn(map);
        
	    Map<String, Object> result = new HashMap<String, Object>();
	    result.put("Rows", page.getResult());
	    result.put("Total", page.getTotalCount());
	    return result;
	}


	public Map<String, Object> getLevelInfo(String checkId) {
		List<RptValidWarnLevel> list = new ArrayList<RptValidWarnLevel>();
		if (checkId != null && !checkId.equals("")) {
			Map<String, String> condition = new HashMap<String, String>();
			condition.put("checkId", checkId);

			list = this.levelDao
					.listWarnLevel(condition);
		}else{
			RptValidWarnLevel level1 = new RptValidWarnLevel();
			level1.setIsPassCond("1");
			level1.setLevelNm("预警");
			level1.setLevelType("01");
			level1.setMinusRangeVal(new BigDecimal(0));
			level1.setPostiveRangeVal(new BigDecimal(0));
			level1.setRemindColor("660000");
			list.add(level1);
			
			RptValidWarnLevel level = new RptValidWarnLevel();
			level.setIsPassCond("0");
			level.setLevelNm("报警");
			level.setLevelType("01");
			level.setMinusRangeVal(new BigDecimal(0));
			level.setPostiveRangeVal(new BigDecimal(0));
			level.setRemindColor("ff0000");
			list.add(level);
			
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("Rows", list);
		result.put("Total", list.size());
		return result;
	}


	@Transactional(readOnly = false)
	public void saveWarn(RptValidCfgextWarn warn, String levelInfo) {
		String checkId = "";
		Map<String, Object> condition = new HashMap<String, Object>();
		
		if(warn.getCheckId() == null || warn.getCheckId().equals("")){//新增
			checkId = RandomUtils.uuid2();
			warn.setCheckId(checkId);
			
			this.warnDao.saveWarn(warn);
			
		}else{//修改
			//删除报警级别
			condition.clear();
			RptValidWarnLevelPK tmp = new RptValidWarnLevelPK();
			tmp.setCheckId(warn.getCheckId());
			condition.put("id", tmp);
			this.levelDao.deleteWarnLevel(condition);
			
			this.warnDao.updateWarn(warn);
		}
		
		//保存警报级别
		checkId = warn.getCheckId();
		if(levelInfo != null && !levelInfo.equals("")){
			JSONArray queryArray = JSON.parseArray(levelInfo);
			for (int i = 0; i < queryArray.size(); i++) {

				JSONObject object = queryArray.getJSONObject(i);
				RptValidWarnLevel level = new RptValidWarnLevel();
				RptValidWarnLevelPK pk = new RptValidWarnLevelPK();
				
				pk.setCheckId(checkId);
				pk.setLevelNum(RandomUtils.uuid2());
				level.setId(pk);
				level.setLevelNm(object.get("levelNm") == null ? "" : object.getString("levelNm"));
				level.setLevelType(object.get("levelType") == null ? "02" : object.getString("levelType"));
				level.setMinusRangeVal(new BigDecimal(object.get("minusRangeVal") == null ? "0" : object.getString("minusRangeVal")));
				level.setPostiveRangeVal(new BigDecimal(object.get("postiveRangeVal") == null ? "0" : object.getString("postiveRangeVal")));
				level.setRemindColor(object.get("remindColor") == null ? "" : object.getString("remindColor"));
				level.setIsPassCond(object.get("isPassCond") == null ? "": object.getString("isPassCond"));
				level.setIndexNo(warn.getIndexNo());
				
				this.levelDao.saveWarnLevel(level);
			}
		}
	}


	public ValidCfgextWarnVO getInfo(String checkId, String indexNo) {
		Map<String, String> condition = new HashMap<String, String>();
		condition.put("checkId", checkId);
		List<ValidCfgextWarnVO> list = this.warnDao.listWarn(condition);
		if(list != null && list.size() == 1){
			return list.get(0);
		}
		return null;
	}


	@Transactional(readOnly = false)
	public void delete(String checkIds) {
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
			this.warnDao.deleteWarn(condition);
			
			String jql = " delete from RptValidDimRel rel where rel.id.checkId in (:ids) ";
			this.baseDAO.batchExecuteWithNameParam(jql,condition); 
			
			condition.clear();
			condition.put("checkIds", idList);
			this.levelDao.deleteWarnLevel(condition);
	}

	public String validStartDate(String cfgId) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("templateId", cfgId);
		
		List<String> list = this.warnDao.getTmpInfo(map);
		if(list != null && list.size() == 1){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 查询所选择指标对应的逻辑校验数据
	 *  @return 指标号实体 
	 */
	public List<IdxWarnImportVO> getAllWarnList(String idx) {
		List<Object[]> warnInfo = null ;
		List<IdxWarnImportVO> vos = new ArrayList<IdxWarnImportVO>();
		String index =idx+".%";
		
		String sql =" select count(check_id) from RPT_VALID_WARN_LEVEL t1 "
				+ " where t1.check_id in (select t.check_id from RPT_VALID_CFGEXT_WARN t where t.index_no = ?0 or t.index_no like ?1 )";
		List<Object[]> count = this.baseDAO.findByNativeSQLWithIndexParam(sql, idx,index);
		for(Object num : count){
			if("0".equals(num.toString())){
				String sql1 =" select t.check_nm,t.index_No,t.compare_Val_Type,t.range_Type,t.start_Date,"
						+ " t.remark,t.check_id from RPT_VALID_CFGEXT_WARN t  "
						+ " where t.index_no = ?0 or t.index_no like ?1  ";
				warnInfo = this.baseDAO.findByNativeSQLWithIndexParam(sql1, idx,index);
				for(Object[] warn: warnInfo ){
					String relateDim = "";
					if(warn[6]!= null){
						String sql2 = " select t1.DIM_TYPE_NM from RPT_VALID_DIM_REL t"
								+ " left join RPT_DIM_TYPE_INFO t1 on t1.DIM_TYPE_NO = t.DIM_NO "
								+ " where t.check_id = ?0 and t.valid_type = ?1"; 
						List<Object[]> dimList = this.baseDAO.findByNativeSQLWithIndexParam(sql2, warn[6],"02");
						if(dimList.size()>0){
							for(Object dim : dimList){
								relateDim += dim+","; 
							}
							relateDim = relateDim.substring(0, relateDim.length()-1);
						}
					}
					IdxWarnImportVO vo = new IdxWarnImportVO();
					vo.setCheckNm(warn[0].toString()); 
					vo.setIndexNo(warn[1].toString()); 
					vo.setCompareValType(warn[2].toString());
					vo.setRangeType(warn[3].toString());
					vo.setStartDate(warn[4].toString());
					vo.setRelateDim(relateDim);
					vo.setRemark(warn[5]!= null ? warn[5].toString():"");
					vo.setLevelNm("");
					vo.setLevelType("");
					vo.setRemindColor("");
					vo.setPostiveRangeVal(new BigDecimal(""));
					vo.setMinusRangeVal(new BigDecimal(""));
					vo.setIsPassCond("");
					vos.add(vo);
				}
			}else{
				String sql2 =" select t.check_nm,t.index_No,t.compare_Val_Type,t.range_Type,t.start_Date,"
						+ " t.remark,t1.level_Nm,t1.level_Type,t1.remind_Color,"
						+ " t1.postive_Range_Val, t1.minus_Range_Val,t1.is_Pass_Cond,t.check_id "
						+ " from RPT_VALID_CFGEXT_WARN t "
						+ " left join RPT_VALID_WARN_LEVEL t1 on t.check_id = t1.check_id "
						+ " where t.index_no = ?0 or t.index_no like ?1  ";
				warnInfo = this.baseDAO.findByNativeSQLWithIndexParam(sql2, idx,index);
				for(Object[] warn: warnInfo ){
					String relateDim = "";
					if(warn[12]!= null){
						String sql3 = " select t1.DIM_TYPE_NM from RPT_VALID_DIM_REL t"
								+ " left join RPT_DIM_TYPE_INFO t1 on t1.DIM_TYPE_NO = t.DIM_NO "
								+ " where t.check_id = ?0 and t.valid_type = ?1"; 
						List<Object[]> dimList = this.baseDAO.findByNativeSQLWithIndexParam(sql3, warn[12],"02");
						if(dimList.size()>0){
							for(Object dim : dimList){
								relateDim += dim+","; 
							}
							relateDim = relateDim.substring(0, relateDim.length()-1);
						}
					}
					IdxWarnImportVO vo = new IdxWarnImportVO();
					vo.setCheckNm(warn[0].toString()); 
					vo.setIndexNo(warn[1].toString()); 
					vo.setCompareValType(warn[2].toString());
					vo.setRangeType(warn[3].toString());
					vo.setStartDate(warn[4].toString());
					vo.setRelateDim(relateDim);
					vo.setRemark(warn[5]!= null ? warn[5].toString():"");
					vo.setLevelNm(warn[6].toString());
					vo.setLevelType(warn[7].toString());
					vo.setRemindColor(warn[8].toString());
					vo.setPostiveRangeVal(new BigDecimal(warn[9].toString()));
					vo.setMinusRangeVal(new BigDecimal(warn[10].toString()));
					vo.setIsPassCond(warn[11].toString());
					vos.add(vo);
				}
			}
		}
		return  vos;
	}
	
	/**
	 * 查询导入的警示校验数据是否存在
	 *  @return checkId
	 */
	public String getWarnExistList(String expression) {
		String[] composite = StringUtils.split(expression, ',');
		String checkId = "";
		String sql =" select t.check_id from  RPT_VALID_CFGEXT_WARN t where t.index_no = ?0 and "
				+ " t.compare_val_type = ?1 and t.range_type = ?2";
		List<Object[]> checkIdList = this.baseDAO.findByNativeSQLWithIndexParam(sql, composite[0], composite[1], composite[2]);
		if(checkIdList.size()>0){
			for(Object check : checkIdList){
				checkId = check.toString();
			}
		}
		return  checkId;
	}
	
	/**
	 * 查询指标警示校验中所有校验名称
	 * @return 校验实体
	 */
	public List<Object[]> getCheckIdList() {
		String sql = "select info.CHECK_ID,info.CHECK_NM from RPT_VALID_CFGEXT_WARN info where 1=1 ";
		return this.baseDAO.findByNativeSQLWithIndexParam(sql);
	}
	
	/**
	 * 判断校验名称是否重复
	 * @param checkNm
	 * @return
	 */
	public Boolean testSameCheckNm(String checkNm,String checkId){
		Boolean flag = true;
		String jql = " select t.checkId from RptValidCfgextWarn t where t.checkNm = ?0";
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
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getWarnValidIdx());//规避oracle in 超过1000
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
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getWarnValidIdx());//规避oracle in 超过1000
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
	 * 获取异步树的度量节点
	 * @param upNode 上级节点
	 * @param basePath 工程路径
	 * @param validType 校验类型
	 * @return
	 */
	public List<CommonTreeNode> getIdxMeasure(CommonTreeNode upNode, String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<String> indexNos = new ArrayList<String>();
		indexNos.add(upNode.getId());
		Map<String, String> glMap = this.groupBS.getMeasureRelMapping(indexNos);
		String jql = "select warn from RptValidCfgextWarn warn where warn.indexNo in (?0)";
		List<RptValidCfgextWarn> warns = this.baseDAO.findWithIndexParam(jql, glMap.keySet());
		if(null != warns && warns.size() > 0){
			for(RptValidCfgextWarn warn : warns){
				CommonTreeNode node = new CommonTreeNode();
				Map<String, String> params = new HashMap<String, String>();
				params.put("nodeType", "measureInfo");
				params.put("validType", validType);
				node.setId(warn.getIndexNo());
				node.setUpId(upNode.getId());
				node.setText(glMap.get(warn.getIndexNo()));
				node.setTitle(glMap.get(warn.getIndexNo()));
				node.setIcon(basePath + "/images/classics/icons/icon_link.gif");
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
	public List<CommonTreeNode> getIdxWarnValid(CommonTreeNode upNode, String basePath, String validType) {
		List<CommonTreeNode> rstNode = new ArrayList<CommonTreeNode>();
		List<RptValidCfgextWarn> checks = this.getEntityListByProperty(RptValidCfgextWarn.class, "indexNo", upNode.getId());
		if(null != checks && checks.size() > 0){
			for(RptValidCfgextWarn check : checks){
				CommonTreeNode node = new CommonTreeNode();
				Map<String, String> params = new HashMap<String, String>();
				params.put("nodeType", "check");
				params.put("validType", validType);
				node.setId(check.getCheckId());
				node.setUpId(upNode.getId());
				node.setText(check.getCheckNm());
				node.setTitle(check.getCheckNm());
				node.setIcon(basePath + "/images/classics/icons/chart_organisation.png");
				node.setParams(params);
				rstNode.add(node);
			}
		}
		return rstNode;
	}
	
	@SuppressWarnings("serial")
	private List<String> getWarnValidIdx() {
		List<String> idxs = new ArrayList<String>();
		String jql = "select distinct warn.indexNo from RptValidCfgextWarn warn";
		List<String> vIdxs = this.baseDAO.findWithIndexParam(jql);
		if(null != vIdxs && vIdxs.size() > 0){
			for(String indexNo : vIdxs){
				if(StringUtils.isNotBlank(indexNo)){
					idxs.add(StringUtils.split(indexNo, ".")[0]);
				}
			}
		}
		return idxs.size() > 0 ? idxs : new ArrayList<String>(){{add("null");}};
	}
	
	private void getAllUpCtlNos(List<String> ctlNos,List<String> allCtlNos) {
		String jql = "select ctl.upNo from RptIdxCatalog ctl where ctl.indexCatalogNo in (?0)";
		ctlNos = this.baseDAO.findWithIndexParam(jql, ctlNos);
		if(null != ctlNos && ctlNos.size() > 0){
			allCtlNos.addAll(ctlNos);
			getAllUpCtlNos(ctlNos,allCtlNos);
		}
	}
	
	public Map<String,Set<String>> getWarnValidIdxAndGlMeasure() {
		Map<String, Set<String>> rstMap = new HashMap<String, Set<String>>();
		Set<String> allWarIdx = new HashSet<String>();
		Set<String> glMeasure = new HashSet<String>();
		Set<String> idxChecks = new HashSet<String>();
		List<RptValidCfgextWarn> warns = this.getEntityList(RptValidCfgextWarn.class);
		if(null != warns && warns.size() > 0){
			for(RptValidCfgextWarn warn : warns){
				if(StringUtils.isNotBlank(warn.getIndexNo())){
					if(StringUtils.contains(warn.getIndexNo(), ".")){
						glMeasure.add(warn.getIndexNo());
						allWarIdx.add(StringUtils.split(warn.getIndexNo(), ".")[0]);
					}else{
						allWarIdx.add(warn.getIndexNo());
					}
					idxChecks.add(warn.getCheckId() + ";" + warn.getIndexNo());
				}
			}
			rstMap.put("allWarIdx", allWarIdx);
			rstMap.put("glMeasure", glMeasure);
			rstMap.put("idxChecks", idxChecks);
		}
		return rstMap;
	}
	
	/**
	 * 查询同步树
	 */
	public List<CommonTreeNode> findSyncTreeByKeyWord(String basePath, String searchNm, String validType) {
		List<CommonTreeNode> rstNodes = new ArrayList<CommonTreeNode>();
		List<List<String>> multiList = this.groupBS.splitToMultiList(this.getWarnValidIdx());//规避oracle in 超过1000
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
				jql = "select warn from RptValidCfgextWarn warn where warn.indexNo in (?0)";
				List<RptValidCfgextWarn> vos = this.baseDAO.findWithIndexParam(jql, glMap.keySet());
				if(null != vos && vos.size() > 0){
					Map<String, String> multiMap = new HashMap<String, String>();
					for(RptValidCfgextWarn vo : vos){
						if(null == multiMap.get(vo.getIndexNo())){
							CommonTreeNode node = new CommonTreeNode();
							Map<String, String> params = new HashMap<String, String>();
							params.put("nodeType", "measureInfo");
							params.put("validType", validType);
							node.setId(vo.getIndexNo());
							node.setUpId(StringUtils.split(vo.getIndexNo(), ".")[0]);
							node.setText(glMap.get(vo.getIndexNo()));
							node.setTitle(glMap.get(vo.getIndexNo()));
							node.setIcon(basePath + "/images/classics/icons/icon_link.gif");
							node.setParams(params);
							rstNodes.add(node);
							multiMap.put(vo.getIndexNo(), vo.getIndexNo());
						}
						
						CommonTreeNode node = new CommonTreeNode();
						Map<String, String> params = new HashMap<String, String>();
						params.put("nodeType", "check");
						params.put("validType", validType);
						node.setId(vo.getCheckId());
						node.setUpId(vo.getIndexNo());
						node.setText(vo.getCheckNm());
						node.setTitle(vo.getCheckNm());
						node.setIcon(basePath + "/images/classics/icons/chart_organisation.png");
						node.setParams(params);
						rstNodes.add(node);
					}
				}
			}
			if(null != otIndexNo && otIndexNo.size() > 0){//非总账
				jql = "select warn from RptValidCfgextWarn warn where warn.indexNo in (?0)";
				List<RptValidCfgextWarn> vos = this.baseDAO.findWithIndexParam(jql, otIndexNo);
				if(null != vos && vos.size() > 0){
					for(RptValidCfgextWarn vo : vos) {
						CommonTreeNode node = new CommonTreeNode();
						Map<String, String> params = new HashMap<String, String>();
						params.put("nodeType", "check");
						params.put("validType", validType);
						node.setId(vo.getCheckId());
						node.setUpId(vo.getIndexNo());
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
	 * 构造维度树
	 * 
	 * @param params
	 * @return
	 */
	public List<CommonTreeNode> listDimTree(String indexNo,String checkId) {
		String basePath = GlobalConstants4frame.APP_CONTEXT_PATH;
		List<CommonTreeNode> treeNodes = Lists.newArrayList();
		
		String idxNo = indexNo;
		
		if(idxNo.contains(".")){
			idxNo = idxNo + ".";
			String compNo[] = StringUtils.split(idxNo, ".");
			idxNo = compNo[0];
		}

		// 构造根节点
		CommonTreeNode baseNode = new CommonTreeNode();
		baseNode.setId("0");
		baseNode.setText("全部");
		baseNode.setUpId("0");
		RptIdxDimTypeRelVO dimBase = new RptIdxDimTypeRelVO();
		baseNode.setData(dimBase);
		baseNode.setIcon(basePath + GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
		treeNodes.add(baseNode);
		
		Map<String, Object> dimMap = new HashMap<String, Object>();
		dimMap.put("indexNo", idxNo);
		dimMap.put("dimType", "04");
		
		//获取指标的所有维度
		String sql = " select distinct rel.dim_No,dim.dim_Type_Nm,rel.dim_Type from Rpt_Idx_Dim_Rel rel"
				+ " left join Rpt_Dim_Type_Info dim on dim.dim_Type_No = rel.dim_No"
				+ " where rel.index_No = :indexNo and rel.dim_Type <> :dimType order by rel.dim_type desc ";
		List<Object[]> dimList = this.baseDAO.findByNativeSQLWithNameParam(sql, dimMap);
		
		if(null != dimList && dimList.size() > 0){//把机构放入到map里，便于判断
			for(Object[] dim : dimList){
				RptIdxDimTypeRelVO dimTypeRel= new RptIdxDimTypeRelVO();
				dimTypeRel.setDimNo(dim[0] != null?dim[0].toString():""); 
				dimTypeRel.setDimTypeNm(dim[1] != null?dim[1].toString():""); 
				dimTypeRel.setDimType(dim[2] != null?dim[2].toString():"");
				if("01".equals(dim[2].toString())||"02".equals(dim[2].toString())){
					dimTypeRel.setJudge("1");
				}else{
					dimTypeRel.setJudge(isChecked(checkId,dim[0].toString()));
				}
	
				CommonTreeNode treeChildNode = new CommonTreeNode();
				treeChildNode.setId(dim[0].toString());
				treeChildNode.setText(dim[1].toString());
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
	public void saveDimInfo(String checkId,String indexNo,String ids,String dimTypes) {
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
		    	pk.setValidType("02");
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