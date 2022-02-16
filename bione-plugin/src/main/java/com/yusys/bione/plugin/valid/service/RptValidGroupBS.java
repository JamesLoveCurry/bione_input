package com.yusys.bione.plugin.valid.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validtype.entity.BioneValidTypeDef;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxDimRel;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureRelPK;
import com.yusys.bione.plugin.rptidx.repository.IdxDimRelMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.IdxMeasureMybatisDao;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxMeasureInfoVO;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.entitiy.RptValidDataRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidDataRelPK;
import com.yusys.bione.plugin.valid.entitiy.RptValidGroupInfo;
import com.yusys.bione.plugin.valid.entitiy.RptValidGroupMain;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicIdxRel;
import com.yusys.bione.plugin.valid.web.vo.RptIdxMeasureRelVO;
import com.yusys.bione.plugin.valid.web.vo.RptValidDataRelVO;

@Service
@Transactional(readOnly = true)
public class RptValidGroupBS extends BaseBS<Object>{
	
	@Autowired
	private ExcelBS excelBS;
	
	@Autowired
	private IdxDimRelMybatisDao relDao;
	
	@Autowired
	private IdxMeasureMybatisDao measureDao;
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	@SuppressWarnings("unchecked")
	public Map<String,Object> list(Pager pager){
		Map<String,Object> rows = new HashMap<String, Object>();
		Map<String,String> values = new HashMap<String, String>();
		String jql = "select grp from RptValidGroupMain grp where 1=1";
		if(null != pager.getCondition() && !"".equals(pager.getCondition())){
			jql += " and "+ pager.getSearchCondition().get("jql") +"";
			values = (Map<String,String>)pager.getSearchCondition().get("params");
		}
		SearchResult<RptValidGroupInfo> result = this.baseDAO.findPageWithNameParam(pager.getPageFirstIndex(), pager.getPagesize(), jql, values);
		rows.put("Rows", result.getResult());
		rows.put("Total", result.getTotalCount());
		return rows;
	}
	
	public Map<String,RptValidGroupMain> getValidGroupById(String groupId) {
		Map<String,RptValidGroupMain> rMap = new HashMap<String, RptValidGroupMain>();
		rMap.put("grp", (RptValidGroupMain)this.getEntityById(RptValidGroupMain.class, groupId));
		return rMap;
	}
	
	@Transactional(readOnly = false)
	public void delValidGrp(String ids) {
		String jql = "delete from RptValidGroupMain grp where grp.groupId in (?0)";
		List<String> delIds = Arrays.asList(StringUtils.split(ids, ","));
		this.baseDAO.batchExecuteWithIndexParam(jql, delIds);
	}
	
	public List<BioneValidTypeDef> getValidTypeDef() {
		Map<String,String> param = Maps.newHashMap();
		String jql = "select def from BioneValidTypeDef def,BioneValidTypeSysRel rel where rel.id.logicSysNo = :logicSysNo and def.objDefNo = rel.id.objDefNo order by def.objDefNo";
		param.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		return this.baseDAO.findWithNameParm(jql, param);
	}
	
	public Map<String, String> getMeasureRelMapping(List<String> indexNos){
		Map<String, String> rstMap = new HashMap<String, String>();
		String jql = "select new com.yusys.bione.plugin.valid.web.vo.RptIdxMeasureRelVO(rel,info.measureNm) from RptIdxMeasureRel rel,RptIdxInfo idx,RptIdxMeasureInfo info where rel.id.indexNo in (?0) and idx.endDate = ?1 and rel.id.indexNo = idx.id.indexNo and rel.id.measureNo = info.measureNo";
		List<RptIdxMeasureRelVO> rels = this.baseDAO.findWithIndexParam(jql, indexNos, "29991231");
		if(null != rels && rels.size() > 0){
			for(RptIdxMeasureRelVO rel : rels){
				rstMap.put(rel.getId().getIndexNo() + "." + rel.getId().getMeasureNo(), rel.getMeasureNm());
			}
		}
		return rstMap;
	}
	
	public List<List<String>> splitToMultiList(List<String> list){
		List<List<String>> multiList = new ArrayList<List<String>>();
		if(list.size() > 1000){
			int splitCount = 0;
			if(list.size() % 1000 == 0){
				splitCount = list.size() / 1000;
			}else{
				splitCount = list.size() / 1000 + 1;
			}
			for(int i=0;i<splitCount;i++){
				List<String> subList = new ArrayList<String>();
				if(i == (splitCount - 1)){
					subList = list.subList(i*1000, list.size());
				}else{
					subList = list.subList(i*1000, ((i+1)*1000));
				}
				multiList.add(subList);
			}
		}else{
			multiList.add(list);
		}
		return multiList;
	}
	
	@Transactional(readOnly = false)
	public Map<String, String> delValidGroupById(String groupId, String grpType) {
		Map<String, String> rMap = new HashMap<String, String>();
		String jql = "select distinct info.validGid from RptValidGroupInfo info where info.groupId = ?0 and info.groupType = ?1";
		List<String> ids = this.baseDAO.findWithIndexParam(jql, groupId, grpType);
		if(null != ids && ids.size() > 0){
			jql = "select rel from RptValidDataRel rel where rel.id.validGid in (?0)";
			List<RptValidDataRel> rels = this.baseDAO.findWithIndexParam(jql, ids);
			if(null != rels && rels.size() > 0){
				for(RptValidDataRel rel : rels){
					rMap.put(rel.getId().getValidType() + "@" + rel.getId().getCheckId(), rel.getId().getValidGid());
				}
			}
			jql = "delete from RptValidDataRel rel where rel.id.validGid in (?0)";
			this.baseDAO.batchExecuteWithIndexParam(jql, ids);
			jql = "delete from RptValidGroupInfo info where info.groupId = ?0 and info.groupType = ?1";
			this.baseDAO.batchExecuteWithIndexParam(jql, groupId, grpType);
		}
		return rMap;
	}
	
	@Transactional(readOnly = false)
	public void saveValidGroupInfo(String groupId, String grpType, String saveData) {
		Map<String, String> gidMap = this.delValidGroupById(groupId, grpType);
		String[] nodes = StringUtils.split(saveData, ",");
		if(null != nodes && nodes.length > 0){
			Map<String, String> checkNodeMap = new HashMap<String, String>();//判断节点是否重复
			Map<String, String> checkIndexMap = new HashMap<String, String>();//判断指标是否重复
			List<RptValidGroupInfo> grpList = new ArrayList<RptValidGroupInfo>();
			List<RptValidDataRel> relList = new ArrayList<RptValidDataRel>();
			List<String> logicNodeIds = new ArrayList<String>();
			List<String> warnNodeIds = new ArrayList<String>();
			List<String> zeroNodeIds = new ArrayList<String>();
			List<String> sumNodeIds = new ArrayList<String>();
			List<String> planNodeIds = new ArrayList<String>();
			for(String node : nodes){
				String[] nodeInfo = StringUtils.split(node, "@");
				if ("01".equals(nodeInfo[0])){//逻辑校验
					logicNodeIds.add(nodeInfo[1]);
				}else if ("02".equals(nodeInfo[0])){//警示校验
					warnNodeIds.add(nodeInfo[1]);
				}else if ("03".equals(nodeInfo[0])){//零值校验
					zeroNodeIds.add(nodeInfo[1]);
				}else if ("04".equals(nodeInfo[0])){//总分校验
					sumNodeIds.add(nodeInfo[1]);
				}else if ("05".equals(nodeInfo[0])){//计划值校验
					planNodeIds.add(nodeInfo[1]);
				}
			}
			//逻辑校验保存
			if(null != logicNodeIds && logicNodeIds.size() > 0){
				String jql = "select loc from RptValidLogicIdxRel loc,RptValidCfgextLogic ext where loc.id.formulaType = ?0 and ext.checkId in (?1) and loc.id.checkId = ext.checkId";
				List<RptValidLogicIdxRel> logicRels =this.baseDAO.findWithIndexParam(jql, "L", logicNodeIds);
				if(null != logicRels && logicRels.size() > 0){
					for(RptValidLogicIdxRel rel : logicRels){
						String validGid = (null == gidMap.get("01@" + rel.getId().getCheckId()) ? RandomUtils.uuid2() : gidMap.get("01@" + rel.getId().getCheckId()));
						if(null == checkNodeMap.get(rel.getId().getIndexNo() + rel.getId().getCheckId())){
							if(null == checkIndexMap.get(rel.getId().getIndexNo())){
								RptValidGroupInfo grp = new RptValidGroupInfo();
								grp.setGroupId(groupId);
								grp.setGroupType(grpType);
								grp.setIndexNo(rel.getId().getIndexNo());
								grp.setValidGid(validGid);
								grpList.add(grp);
								checkIndexMap.put(rel.getId().getIndexNo(), validGid);
							}
							RptValidDataRel validRel = new RptValidDataRel();
							RptValidDataRelPK relPk = new RptValidDataRelPK();
							relPk.setCheckId(rel.getId().getCheckId());
							relPk.setValidGid(checkIndexMap.get(rel.getId().getIndexNo()));
							relPk.setValidType("01");//逻辑
							validRel.setId(relPk);
							relList.add(validRel);
							checkNodeMap.put(rel.getId().getIndexNo() + rel.getId().getCheckId(),
									rel.getId().getIndexNo() + rel.getId().getCheckId());
						}
					}
				}
			}
			//警示校验保存
			if(null != warnNodeIds && warnNodeIds.size() > 0){
				String jql = "select warn from RptValidCfgextWarn warn where warn.checkId in (?0)";
				List<RptValidCfgextWarn> warns = this.baseDAO.findWithIndexParam(jql, warnNodeIds);
				for(RptValidCfgextWarn warn :warns){
					String validGid = (null == gidMap.get("02@" + warn.getCheckId()) ? RandomUtils.uuid2() : gidMap.get("02@" + warn.getCheckId()));
					if(null == checkIndexMap.get(warn.getIndexNo())){
						RptValidGroupInfo grp = new RptValidGroupInfo();
						grp.setGroupId(groupId);
						grp.setGroupType(grpType);
						grp.setIndexNo(warn.getIndexNo());
						grp.setValidGid(validGid);
						grpList.add(grp);
						checkIndexMap.put(warn.getIndexNo(), validGid);
					}
					RptValidDataRel validRel = new RptValidDataRel();
					RptValidDataRelPK relPk = new RptValidDataRelPK();
					relPk.setCheckId(warn.getCheckId());
					relPk.setValidGid(checkIndexMap.get(warn.getIndexNo()));
					relPk.setValidType("02");//警示
					validRel.setId(relPk);
					relList.add(validRel);
				}
			}
			//零值校验保存
			if(null != zeroNodeIds && zeroNodeIds.size() > 0){
				this.getOtherValidObj(groupId, grpType, "03",checkIndexMap, zeroNodeIds, grpList, relList, gidMap);
			}
			//总分校验保存
			if(null != sumNodeIds && sumNodeIds.size() > 0){
				this.getOtherValidObj(groupId, grpType, "04", checkIndexMap, sumNodeIds, grpList, relList, gidMap);
			}
			//计划值校验保存
			if(null != planNodeIds && planNodeIds.size() > 0){
				this.getOtherValidObj(groupId, grpType, "05", checkIndexMap, planNodeIds, grpList, relList, gidMap);
			}
			this.excelBS.saveEntityJdbcBatch(grpList);
			this.excelBS.saveEntityJdbcBatch(relList);
		}
	}
	
	private void getOtherValidObj(String groupId, String grpType, String validType, Map<String, String> checkIndexMap, 
			List<String> nodeIds, List<RptValidGroupInfo> grpList, List<RptValidDataRel> relList, Map<String, String> gidMap){
		List<List<String>> multiList = this.splitToMultiList(nodeIds);//规避oracle in 超过1000
		Map<String,Object> value = new HashMap<String, Object>();
		String jql = "select idx from RptIdxInfo idx where idx.indexType != :indexType and idx.endDate = :endDate and (";
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
		value.put("indexType", "05");
		value.put("endDate", "29991231");
		
		List<RptIdxInfo> idxs = this.baseDAO.findWithNameParm(jql, value);
		if(null != idxs && idxs.size() > 0){
			for(RptIdxInfo idx : idxs){
				String validGid = (null == gidMap.get(validType + "@" + idx.getId().getIndexNo()) ? RandomUtils.uuid2() : gidMap.get(validType + "@" + idx.getId().getIndexNo()));
				if(null == checkIndexMap.get(idx.getId().getIndexNo())){
					RptValidGroupInfo grp = new RptValidGroupInfo();
					grp.setGroupId(groupId);
					grp.setGroupType(grpType);
					grp.setIndexNo(idx.getId().getIndexNo());
					grp.setValidGid(validGid);
					grpList.add(grp);
					checkIndexMap.put(idx.getId().getIndexNo(), validGid);
				}
				RptValidDataRel validRel = new RptValidDataRel();
				RptValidDataRelPK relPk = new RptValidDataRelPK();
				relPk.setCheckId(idx.getId().getIndexNo());
				relPk.setValidGid(checkIndexMap.get(idx.getId().getIndexNo()));
				relPk.setValidType(validType);//零值
				validRel.setId(relPk);
				relList.add(validRel);
			}
		}
		//查找总账度量
		for(String indexNo : nodeIds){
			if(StringUtils.contains(indexNo, ".")){
				String validGid = (null == gidMap.get(validType + "@" + indexNo) ? RandomUtils.uuid2() : gidMap.get(validType + "@" + indexNo));
				if(null == checkIndexMap.get(indexNo)){
					RptValidGroupInfo grp = new RptValidGroupInfo();
					grp.setGroupId(groupId);
					grp.setGroupType(grpType);
					grp.setIndexNo(indexNo);
					grp.setValidGid(validGid);
					grpList.add(grp);
					checkIndexMap.put(indexNo, validGid);
				}
				RptValidDataRel validRel = new RptValidDataRel();
				RptValidDataRelPK relPk = new RptValidDataRelPK();
				relPk.setCheckId(indexNo);
				relPk.setValidGid(checkIndexMap.get(indexNo));
				relPk.setValidType(validType);//零值
				validRel.setId(relPk);
				relList.add(validRel);
			}
		}
	}
	/**
	 * 用于反显已被勾选的节点
	 * @param groupId
	 * @param grpType
	 * @return
	 */
	public Map<String, Object> findSelectNodeObj(String groupId, String grpType) {
		Map<String, Object> rstMap = new HashMap<String, Object>();
		List<String> checkNodeIds = new ArrayList<String>();
		List<String> saveNodes = new ArrayList<String>();
//		Map<String, String> saveNodes = new HashMap<String, String>();
		String jql = "select def.objDefNo from BioneValidTypeDef def,BioneValidTypeSysRel rel where rel.id.logicSysNo = ?0 and def.objDefNo = rel.id.objDefNo order by def.objDefNo";
		List<String> validTypes =  this.baseDAO.findWithIndexParam(jql, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		if(null != validTypes && validTypes.size() > 0){
			for(String validType : validTypes){
				List<String> vtIdxNos = new ArrayList<String>();
				jql = "select new com.yusys.bione.plugin.valid.web.vo.RptValidDataRelVO(info.groupId, info.groupType, info.indexNo, rel) from RptValidGroupInfo info,RptValidDataRel rel where info.groupId = ?0 and info.groupType = ?1 and rel.id.validType in (?2) and info.validGid = rel.id.validGid";
				List<RptValidDataRelVO> vos = this.baseDAO.findWithIndexParam(jql, groupId, grpType, validType);
				if(null != vos && vos.size() > 0) {
					for(RptValidDataRelVO vo : vos) {
						if(StringUtils.isNotBlank(vo.getIndexNo())){
							//指标节点及度量节点
							checkNodeIds.add(validType + "@" + vo.getIndexNo());
							if(StringUtils.contains(vo.getIndexNo(), ".")){
								vtIdxNos.add(StringUtils.split(vo.getIndexNo(), ".")[0]);
								checkNodeIds.add(validType + "@" + StringUtils.split(vo.getIndexNo(), ".")[0]);
								if(!"01".equals(validType) && !"02".equals(validType)){
									saveNodes.add(validType + "@" + vo.getIndexNo());
								}
							}else{
								vtIdxNos.add(vo.getIndexNo());
							}
							//校验节点
							if(!vo.getIndexNo().equals(vo.getId().getCheckId())){
								checkNodeIds.add(validType + "@" + vo.getId().getCheckId());
								saveNodes.add(validType + "@" + vo.getId().getCheckId());
							}
						}
					}
					if(null != vtIdxNos && vtIdxNos.size() > 0){
						jql = "select idx from RptIdxInfo idx where idx.id.indexNo in (?0) and idx.endDate = ?1";
						List<RptIdxInfo> idxs = this.baseDAO.findWithIndexParam(jql, vtIdxNos, "29991231");
						List<String> oneLvlCtlNo = new ArrayList<String>();
						if(null != idxs && idxs.size() > 0){
							for(RptIdxInfo idx : idxs){
								oneLvlCtlNo.add(idx.getIndexCatalogNo());
								if(!"01".equals(validType) && !"02".equals(validType) && !"05".equals(idx.getIndexType())){
									saveNodes.add(validType + "@" + idx.getId().getIndexNo());
								}
							}
							List<RptIdxCatalog> catalogs = new ArrayList<RptIdxCatalog>();
							this.getAllUpCtlNos(oneLvlCtlNo, catalogs);
							if(null != catalogs && catalogs.size() > 0){
								for(RptIdxCatalog ctl : catalogs){//目录节点
									checkNodeIds.add(validType + "@" + ctl.getIndexCatalogNo());
								}
							}
						}
					}
				}
			}
		}
		
		rstMap.put("checkNodeIds", checkNodeIds);
		rstMap.put("saveNodes", saveNodes);
		return rstMap;
	}
	
	public void getAllUpCtlNos(List<String> ctlNos, List<RptIdxCatalog> allCtlObj) {
		String jql = "select ctl from RptIdxCatalog ctl where ctl.indexCatalogNo in (?0)";
		List<RptIdxCatalog> ctls = this.baseDAO.findWithIndexParam(jql, ctlNos);
		ctlNos.clear();
		if(null != ctls && ctls.size() > 0){
			allCtlObj.addAll(ctls);
			for(RptIdxCatalog ctl : ctls){
				ctlNos.add(ctl.getUpNo());
			}
			getAllUpCtlNos(ctlNos, allCtlObj);
		}
	}
	
	public List<RptIdxInfo> getValidIdxByNos(Set<String> ids) {
		List<RptIdxInfo> idxs = new ArrayList<RptIdxInfo>();
		if(null != ids && ids.size() > 0){
			String jql = "select idx from RptIdxInfo idx where idx.id.indexNo in (?0) and idx.endDate = ?1";
			idxs = this.baseDAO.findWithIndexParam(jql, ids, "29991231");
		}
		return idxs;
	}
	
	public List<CommonTreeNode> getAsyncTree(String contextPath,
			String searchNm, String isShowIdx, String isShowMeasureInfo, String isShowDim, String indexNo,String indexNm,
			String exSumAccoutIndex, String isAuth, String nodeType, String upId, String indexVerId, String isPublish,
			String showEmptyFolder, List<String> indexNos, String validType) {
		if (StringUtils.isEmpty(upId)) {
			upId = "0";
		}
		if (StringUtils.isEmpty(nodeType)) {
			nodeType = "idxCatalog";
		}
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		Map<String, Object> condition = new HashMap<String, Object>();
		if (nodeType.equals("idxCatalog")) {
			String jql = "select catalog from RptIdxCatalog catalog where upNo = :upNo ";
			condition.put("upNo", upId);
			jql += " order by catalog.catalogOrder ";
			List<RptIdxInfo> idxList = getIdxByConfig(searchNm, isShowMeasureInfo, indexNo, indexNm , isPublish, exSumAccoutIndex, isAuth, upId,indexNos);
			Set<String> allCatalogNos = new HashSet<String>();
			if(!"1".equals(showEmptyFolder)){
				Set<String> catalogNos =  this.getCatalogByConfig(searchNm, isShowMeasureInfo, indexNo, indexNm , isPublish, exSumAccoutIndex, isAuth);
				if(idxList!= null && idxList.size() > 0 ){
					for(RptIdxInfo info : idxList){
						catalogNos.add(info.getIndexCatalogNo());
					}
				}
				this.getAllCatalogNo(catalogNos, allCatalogNos);
			}
			List<RptIdxCatalog> catalogList = this.baseDAO.findWithNameParm(jql, condition);
			for (RptIdxCatalog tmp : catalogList) {
				if(allCatalogNos.contains(tmp.getIndexCatalogNo()) || "1".equals(showEmptyFolder)){
					CommonTreeNode node = generateCatalogNode(contextPath, tmp, validType);
					node.setIsParent(true);
					node.setTitle(tmp.getIndexCatalogNm());//鼠标经过显示
					resultList.add(node);
				}
			}
			if(idxList != null && idxList.size() > 0){
				List<RptIdxDimRel> dimRelList = new ArrayList<RptIdxDimRel>();
				if (!StringUtils.isEmpty(isShowDim) && isShowDim.equals("1")) {
					if(idxList != null && idxList.size() > 0){
						List<RptIdxInfo> relidx = new ArrayList<RptIdxInfo>();
						for(RptIdxInfo idx : idxList){
							if(idx.getIndexType().equals(GlobalConstants4plugin.ROOT_INDEX)
									|| idx.getIndexType().equals(GlobalConstants4plugin.COMPOSITE_INDEX)
									||idx.getIndexType().equals(GlobalConstants4plugin.DERIVE_INDEX))
							{
								relidx.add(idx);
							}
						}
						if(relidx.size()>0){
							condition.put("idxList", relidx);
							dimRelList = this.getDimNosOfIndex(condition);
						}
					}
				}
				Map<String,String> dimRelMap = new HashMap<String, String>(); 
				if (dimRelList != null && dimRelList.size()>0){
					for(RptIdxDimRel rel : dimRelList){
						dimRelMap.put(rel.getId().getIndexNo()+"-"+rel.getId().getIndexVerId(), rel.getId().getDimNo());
					}
				}
				for (RptIdxInfo tmp : idxList) {
					CommonTreeNode idxNode = generateIndexNode(contextPath, tmp, isShowDim, isShowMeasureInfo, dimRelMap, null, validType);
					resultList.add(idxNode);
				}
			}
		}
		else if (nodeType.equals("idxInfo")) {
			resultList.addAll(generateMeasureNode(contextPath, upId, indexVerId, validType));
		}
		return resultList;
	}
	
	@SuppressWarnings("unchecked")
	private List<RptIdxInfo> getIdxByConfig(String searchNm, String isShowMeasureInfo, String indexNo,String indexNm,
			String isPublish, String exSumAccoutIndex, String isAuth, String upId,List<String> indexNos){
		List<RptIdxInfo> idxs = new ArrayList<RptIdxInfo>();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("isRptIndex", "N");
		params.put("endDate", "29991231");
		String jql = "select idx from RptIdxInfo idx ";
		jql += " where idx.isRptIndex = :isRptIndex and idx.endDate = :endDate";
		List<String> indexType = new ArrayList<String>();
		params.put("indexType", indexType);
		if("1".equals(isPublish)){
			params.put("indexSts", "Y");
			jql += " and idx.indexSts = :indexSts";
		}
		if(!"1".equals(exSumAccoutIndex)){
		}else{
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			jql += " and idx.indexType not in :indexType ";
		}
		
		if("1".equals(isAuth)){
			if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				if(indexNos != null){
					indexNos = ListUtils.intersection(indexNos, BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX"));
				}
				else{
					indexNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
				}
				if(indexNos.size()<=0){
					return idxs;
				}	
			}
		}
		
		if(StringUtils.isNotBlank(upId)){
			params.put("upId", upId);
			jql += " and idx.indexCatalogNo = :upId";
		}
		if(StringUtils.isNotBlank(searchNm)){
			params.put("searchNm", "%"+searchNm+"%");
			jql += " and (idx.indexNm like :searchNm or idx.id.indexNo like :searchNm)";
			jql += " and idx.indexCatalogNo not like 'IB-%'";
		}
		if(StringUtils.isNotBlank(indexNo)){
			params.put("indexNo", indexNo);
			jql += " and idx.id.indexNo = :indexNo";
		}
		if(StringUtils.isNotBlank(indexNm)){
			params.put("indexNm", "%"+indexNm+"%");
			jql += " and idx.indexNm like :indexNm";
		}
		jql += " order by idx.id.indexNo";
		idxs =this.baseDAO.findWithNameParm(jql, params);
		return idxs;
	}
	
	private Set<String> getCatalogByConfig(String searchNm, String isShowMeasureInfo, String indexNo,String indexNm,
			String isPublish, String exSumAccoutIndex, String isAuth){
		Set<String> catalogNos = new HashSet<String>();
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("isRptIndex", "N");
		params.put("endDate", "29991231");
		String jql = "select distinct idx.indexCatalogNo from RptIdxInfo idx where idx.isRptIndex = :isRptIndex and idx.endDate = :endDate";
		List<String> indexType = new ArrayList<String>();
		List<String> indexNos = new ArrayList<String>();
		if("1".equals(isPublish)){
			params.put("indexSts", "Y");
			jql += " and idx.indexSts = :indexSts";
		}
		if(!"1".equals(exSumAccoutIndex)){
		}else{
			indexType.add(com.yusys.bione.plugin.base.common.GlobalConstants4plugin.SUM_ACCOUNT_INDEX);
			jql += " and idx.indexType not in :indexType ";
		}
		
		if("1".equals(isAuth)){
			if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
				indexNos = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
				if(indexNos.size()<=0){
					return catalogNos;
				}	
			}
		}
		if(StringUtils.isNotBlank(searchNm)){
			params.put("searchNm", "%"+searchNm+"%");
			jql += " and (idx.indexNm like :searchNm or idx.id.indexNo like :searchNm)";
		}
		if(StringUtils.isNotBlank(indexNo)){
			params.put("indexNo", indexNo);
			jql += " and idx.id.indexNo = :indexNo";
		}
		if(StringUtils.isNotBlank(indexNm)){
			params.put("indexNm", "%"+indexNm+"%");
			jql += " and idx.id.indexNm like :indexNm";
		}
		List<List<?>> indexNoLists = ReBuildParam.splitLists(indexNos);
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && "1".equals(isAuth)){
			jql += " and ( ";
			int i = 0;
			for(List<?> indexNoList : indexNoLists){
				jql += " idx.id.indexNo in (:indexNoList"+i+") ";
				params.put("indexNoList"+i, indexNoList);
				if(i < indexNoLists.size() - 1){
					jql += " or ";
				}
				else{
					jql += " ) ";
				}
				i++;
			}
		}
		List<String> catalogs =this.baseDAO.findWithNameParm(jql, params);
		if(catalogs != null && catalogs.size() > 0){
			for(String catalogNo: catalogs){
				catalogNos.add(catalogNo);
			}
		}
		return catalogNos;
	}
	
	private void getAllCatalogNo(Set<String> catalogNo, Set<String> allCatalogNo){
		if(catalogNo != null && catalogNo.size() > 0){
			String jql = "select catalog from RptIdxCatalog catalog where 1=1 and catalog.indexCatalogNo in :catalogNo";
			Map<String,Object> params = new HashMap<String, Object>();
			params.put("catalogNo", catalogNo);
			List<RptIdxCatalog> catalogs = this.baseDAO.findWithNameParm(jql, params);
			catalogNo.clear();
			for(RptIdxCatalog catalog : catalogs){
				allCatalogNo.add(catalog.getIndexCatalogNo());
				if(!catalog.getUpNo().equals("0")){
					catalogNo.add(catalog.getUpNo());
				}
			}
			if(catalogNo != null && catalogNo.size() > 0){
				getAllCatalogNo(catalogNo, allCatalogNo);
			}
		}  
	}
	
	private CommonTreeNode generateCatalogNode(String basePath,RptIdxCatalog catalog, String validType){
		CommonTreeNode node = new CommonTreeNode();
		node.setId(catalog.getIndexCatalogNo());
		node.setText(catalog.getIndexCatalogNm());
		node.setTitle(catalog.getIndexCatalogNm());
		node.setData(catalog);
		node.setUpId(catalog.getUpNo());
		node.setOpen(true);
		node.setIcon(basePath + GlobalConstants4frame.LOGIC_MODULE_ICON);
		node.getParams().put("type", "idxCatalog");
		node.getParams().put("nodeType", "idxCatalog");
		node.getParams().put("validType", validType);
		return node;
	}
	
	public List<RptIdxDimRel> getDimNosOfIndex(Map<String, Object> map) {
		List<RptIdxDimRel> list = this.relDao.getAllDimRel(map);
		List<RptIdxDimRel> resultList = new ArrayList<RptIdxDimRel>();
		for (RptIdxDimRel x : list) {
			boolean flag = false;
			for (RptIdxDimRel y : resultList) {
				if (x.getId().getIndexNo().equals(y.getId().getIndexNo())
						&& x.getId().getIndexVerId() == y.getId().getIndexVerId()) {
					//edit by fangjuan 20150721 对维度进行去重
					if(!y.getId().getDimNo().contains(x.getId().getDimNo())){
						String newDimNos = y.getId().getDimNo() + "," + x.getId().getDimNo();
						y.getId().setDimNo(newDimNos);
					}
					flag = true;// 存在
				}
			}
			if (!flag) {
				resultList.add(x);
			}
		}
		return resultList;
	}
	
	private CommonTreeNode generateIndexNode(String basePath,RptIdxInfo idx, 
			String isShowDim, String isShowMeasureInfo,Map<String, String> dimRelMap,
			Map<String,List<RptIdxMeasureInfoVO>> measureMap, String validType) {
		CommonTreeNode tmp = new CommonTreeNode();
		tmp.setId(idx.getId().getIndexNo());
		if(!idx.getIndexType().equals(GlobalConstants4plugin.SUM_ACCOUNT_INDEX)){
			tmp.setText("["+idx.getId().getIndexNo()+"]" + idx.getIndexNm());
		}else{
			tmp.setText(idx.getIndexNm());
		}
		tmp.setData(idx);
		tmp.setUpId(idx.getIndexCatalogNo());
		tmp.setTitle(StringUtils.isNotBlank(idx.getRemark())? idx.getRemark():idx.getIndexNm());
		tmp.setIcon(basePath + "/images/classics/menuicons/grid.png");
		tmp.getParams().put("id",idx.getId().getIndexNo());
		tmp.getParams().put("type", "idxInfo");
		tmp.getParams().put("nodeType", "idxInfo");
		tmp.getParams().put("validType", validType);
		tmp.getParams().put("idxType", idx.getIndexType());
		tmp.getParams().put("indexNo",idx.getId().getIndexNo());
		tmp.getParams().put("indexNm",idx.getIndexNm());
		tmp.getParams().put("indexVerId",String.valueOf((idx.getId().getIndexVerId())));
		// 给总账指标增加度量信息作为子节点
		if (!StringUtils.isEmpty(isShowMeasureInfo)
				&& isShowMeasureInfo.equals("1") && idx.getIndexType() != null
				&& idx.getIndexType().equals(GlobalConstants4plugin.SUM_ACCOUNT_INDEX)) {
			tmp.getParams().put("haveMeasure", "true");
			tmp.setIsParent(true);
			if(measureMap != null){
				List<CommonTreeNode> measureNodes = new ArrayList<CommonTreeNode>();
				List<RptIdxMeasureInfoVO> measureList = measureMap.get(idx.getId().getIndexNo()+"-"+idx.getId().getIndexVerId());
				if(measureList != null && measureList.size()>0){
					for (RptIdxMeasureInfoVO measure : measureList) {
						CommonTreeNode node = new CommonTreeNode();
						node.setUpId(idx.getId().getIndexNo());
						node.setId(idx.getId().getIndexNo()+"."+measure.getMeasureNo());
						node.setText(measure.getMeasureNm());
						node.setData(idx);
						node.setIcon(basePath + "/images/classics/menuicons/computer.png");
						node.getParams().put("nodeType", "measureInfo");
						node.getParams().put("validType", validType);
						node.getParams().put("indexNo",idx.getId().getIndexNo());
						node.getParams().put("indexVerId",String.valueOf(idx.getId().getIndexVerId()));
						measureNodes.add(node);
					}
					tmp.setChildren(measureNodes);
				}
			}
		}
		tmp.setOpen(false);
		return tmp;
	}
	
	private List<CommonTreeNode> generateMeasureNode(String basePath,String upId,String indexVerId, String validType){
		List<CommonTreeNode> resultList = new ArrayList<CommonTreeNode>();
		Map<String,RptIdxMeasureInfo> measureInfoMap = new HashMap<String, RptIdxMeasureInfo>();
		List<RptIdxMeasureInfo> infos = this.getAllEntityList(RptIdxMeasureInfo.class, "measureNo", false);
		for(RptIdxMeasureInfo info : infos){
			measureInfoMap.put(info.getMeasureNo(), info);
		}
		Map<String,Object> condition = new HashMap<String, Object>();
		RptIdxMeasureRelPK pk = new RptIdxMeasureRelPK();
		pk.setIndexNo(upId);
		pk.setIndexVerId(new Long(indexVerId));
		condition.put("id", pk);
		List<RptIdxMeasureInfoVO> measureList = measureDao.getMeasure(condition);
		String jql = "select idx from RptIdxInfo idx where idx.id.indexNo = ?0 and idx.isRptIndex = ?1 and idx.id.indexVerId = ?2";
		RptIdxInfo idx = this.baseDAO.findUniqueWithIndexParam(jql, upId,"N",new Long(indexVerId));
		for (RptIdxMeasureInfoVO measure : measureList) {
			RptIdxMeasureInfo info = measureInfoMap.get(measure.getMeasureNo());
			if(info!=null){
				CommonTreeNode node = new CommonTreeNode();
				node.setUpId(upId);
				node.setData(idx);
				node.setId(idx.getId().getIndexNo()+"."+measure.getMeasureNo());
				node.setText(info.getMeasureNm());
				node.setIcon(basePath + "/images/classics/menuicons/computer.png");
				node.getParams().put("nodeType", "measureInfo");
				node.getParams().put("validType", validType);
				node.getParams().put("indexNo", upId);
				node.getParams().put("indexVerId", indexVerId);
				node.setTitle(info.getMeasureNm());
				resultList.add(node);
			}
		}
		return resultList;
	}
	
	public String checking(String groupId, String[] validTypes, String validDate, String[] validOrgs) {
		String jql = "select distinct rel.id.validType, rel.id.validGid from RptValidGroupInfo grp, RptValidDataRel rel where grp.groupId = ?0 and rel.id.validType in (?1) and grp.validGid = rel.id.validGid order by rel.id.validType";
		List<Object[]> rels = this.baseDAO.findWithIndexParam(jql, groupId, Arrays.asList(validTypes));
		if(null != rels && rels.size() > 0){
			for(Object[] rel : rels){
				Map<String, Object> map_ = Maps.newHashMap();
				if("01".equals(rel[0].toString())){
					map_.put("LogicCheckRptTmpId", rel[1].toString());
				}else if("02".equals(rel[0].toString())){
					map_.put("WarnCheckRptTmpId", rel[1].toString());
				}else if("03".equals(rel[0].toString())){
					map_.put("ZeroCheckRptTmpId", rel[1].toString());
				}else if("04".equals(rel[0].toString())){
					map_.put("SumCheckRptTmpId", rel[1].toString());
				}else if("05".equals(rel[0].toString())){
					map_.put("PlanCheckValidGid", rel[1].toString());
				}
				map_.put("DataDate", validDate);
				map_.put("OrgNo", validOrgs);
				String json = JSON.toJSONString(map_);
				try {
					CommandRemote.sendAync(json, CommandRemoteType.INDEX );
				}catch (Throwable e) {
					logger.debug("校验任务失败");
					return "校验任务失败";
				}
			}
		}
		return "";
	}
	
	public List<CommonTreeNode> loadOrgTree(String basePath,String searchNm) {
		List<CommonTreeNode> rstNodes = new ArrayList<CommonTreeNode>();
		List<RptOrgInfo> orgs = new ArrayList<RptOrgInfo>();
		if(StringUtils.isNotBlank(searchNm)) {
			String jql = "select org.id.orgNo from RptOrgInfo org where org.id.orgNo like ?0 or org.orgNm like ?0";
			List<String> oneLvlOrgs = this.baseDAO.findWithIndexParam(jql, "%"+ searchNm + "%");
			if(null != oneLvlOrgs && oneLvlOrgs.size() > 0) {
				List<String> allOrgs = new ArrayList<String>();
				this.recUpOrgInfo(oneLvlOrgs, allOrgs);
				if(null != allOrgs && allOrgs.size() > 0) {
					jql = "select org from RptOrgInfo org where org.id.orgNo in (?0)";
					orgs = this.baseDAO.findWithIndexParam(jql, allOrgs);
				}
			}
		}else {
			orgs = this.getEntityList(RptOrgInfo.class);
		}
		if(null != orgs && orgs.size() > 0){
			for(RptOrgInfo org : orgs) {
				CommonTreeNode node = new CommonTreeNode();
				node.setId(org.getId().getOrgNo());
				node.setUpId(org.getUpOrgNo());
				node.setText(org.getOrgNm());
				node.setTitle(org.getOrgNm());
				node.setIcon(basePath + GlobalConstants4frame.LOGIC_ORG_ICON);
				rstNodes.add(node);
			}
		}
		return rstNodes;
	}
	
	private void recUpOrgInfo(List<String> oneLvlOrgs, List<String> allOrgs) {
		allOrgs.addAll(oneLvlOrgs);
		String jql = "select org.upOrgNo from RptOrgInfo org where org.id.orgNo in (?0)";
		List<String> upOrgNos = this.baseDAO.findWithIndexParam(jql, oneLvlOrgs);
		if(null != upOrgNos && upOrgNos.size() > 0) {
			oneLvlOrgs.clear();
			oneLvlOrgs.addAll(upOrgNos);
			recUpOrgInfo(oneLvlOrgs, allOrgs);
		}
	}
}
