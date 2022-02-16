package com.yusys.bione.plugin.datashow.service;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_CONTEXT_PATH;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.ICON_URL;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.access.repository.RptAccessIdxDao;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datashow.repository.FavIdxDetailDao;
import com.yusys.bione.plugin.datashow.repository.FavIdxDimDao;
import com.yusys.bione.plugin.datashow.repository.FavIdxDimFilterDao;
import com.yusys.bione.plugin.datashow.repository.FavQueryinsDao;
import com.yusys.bione.plugin.datashow.repository.FolderInsRelDao;
import com.yusys.bione.plugin.datashow.repository.IdxShowMybatisDao;
import com.yusys.bione.plugin.datashow.web.vo.CerrWorkDateVO;
import com.yusys.bione.plugin.datashow.web.vo.DimItemTreeNode;
import com.yusys.bione.plugin.datashow.web.vo.FavIdxDetailInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.IdxBaseInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.IdxDimInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.IdxStoreInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.RptOrgInfoVO;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolderInsRel;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDetail;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDim;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimFilter;
import com.yusys.bione.plugin.rptfav.entity.RptFavQueryins;
import com.yusys.bione.plugin.rptfav.repository.FavIdxDetailMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavIdxDimFilterMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavIdxDimMybatisDao;
import com.yusys.bione.plugin.rptfav.repository.FavQueryinsMybatisDao;
import com.yusys.bione.plugin.rptfav.web.vo.FavIdxDetailAndIndexNm;
import com.yusys.bione.plugin.rptfav.web.vo.FavIdxDimFilterAndName;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCalcRule;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxTimeMeasure;
import com.yusys.bione.plugin.rptidx.entity.RptIdxValType;
import com.yusys.bione.plugin.rptidx.entity.RptIdxVisitHis;
import com.yusys.bione.plugin.rptidx.repository.IdxDimRelMybatisDao;
import com.yusys.bione.plugin.rptidx.repository.IdxInfoMybatisDao;

@Service
@Transactional(readOnly = true)
public class IdxShowBS extends BaseBS<Object>{
	@Autowired
	private IdxShowMybatisDao idxDao;
	
	@Autowired
	private FavIdxDimDao dimDao;
	@Autowired
	private FavIdxDimFilterDao dimFilterDao;
	@Autowired
	private FavIdxDetailDao detailDao;
	@Autowired
	private FavQueryinsDao queryDao;
	@Autowired
	private FolderInsRelDao relDao;
	
	@Autowired
	private FavIdxDetailMybatisDao detailMybatisDao;

	@Autowired
	private FavIdxDimFilterMybatisDao filterDao;

	@Autowired
	private FavIdxDimMybatisDao dimMybatisDao;

	@Autowired
	private IdxDimRelMybatisDao idxDimRelDao;

	@Autowired
	private FavQueryinsMybatisDao queryMybatisDao;

	@Autowired
	private RptAccessIdxDao hisDao;

	@Autowired
	private IdxInfoMybatisDao idxInfoDao;

	public IdxBaseInfoVO getIdxBaseInfo(String idxNo, String verId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("idxNo", idxNo);
		params.put("verId", Long.parseLong(verId));
		return idxDao.getIdxBaseInfo(params);
	}

	public List<IdxDimInfoVO> findIdxDim(String idxNo, String verId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("idxNo", idxNo);
		params.put("verId", Long.parseLong(verId));
		params.put(
				"excludeDimType",
				GlobalConstants4plugin.DIM_TYPE_INDEXNO);
		return idxDao.findIdxDim(params);
	}

	public List<Map<String, Object>> findDimInfo(String idxNo, String verId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("idxNo", idxNo);
		params.put("verId", Long.parseLong(verId));
		List<RptDimTypeInfo> list = idxDao.findDimTypeByIdx(params);
		List<Map<String, Object>> result = Lists.newArrayList();
		for (Iterator<RptDimTypeInfo> iter = list.iterator(); iter.hasNext();) {
			RptDimTypeInfo entity = iter.next();
			Map<String, Object> map = Maps.newHashMap();
			map.put("dimType", entity);
			map.put("dimItem",
					idxDao.findDimItemByDimTypeNo(entity.getDimTypeNo()));
			result.add(map);
		}
		return result;
	}


	@Transactional(readOnly = false)
	public void saveQuery(RptFavFolderInsRel rel, RptFavQueryins query,
			Collection<RptFavIdxDetail> details, Collection<RptFavIdxDim> dims,
			Collection<RptFavIdxDimFilter> filters) {

		relDao.save(rel);
		queryDao.save(query);
		detailDao.save(details);
		dimDao.save(dims);
		if (filters != null && filters.size() > 0) {
			dimFilterDao.save(filters);
		}
	}

	public Map<String, Object> getFavIdxInfo(String instanceId) {
		Map<String, Object> params = Maps.newHashMap();
		List<FavIdxDetailInfoVO> list = idxDao.findStoreIdxInfo(instanceId);
		List<String> indexNos = new ArrayList<String>();
		List<String> types = new ArrayList<String>();
		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				FavIdxDetailInfoVO vo = list.get(i);
				if(!StringUtils.isEmpty(vo.getMeasureNo())){
					vo.setIndexNm(vo.getIndexNm() + "." + vo.getMeasureNm());
				}
				params.clear();
				indexNos.add(vo.getIndexNo());
				types.add(vo.getCalcCycle());
				params.put("idxNo", vo.getIndexNo());
				params.put("verId", vo.getIndexVerId());
				params.put("excludeDimType", GlobalConstants4plugin.DIM_TYPE_INDEXNO);
				List<IdxDimInfoVO> dimList = idxDao.findIdxDim(params);
				List<String> dimNos = new ArrayList<String>();
				if(dimList != null && dimList.size() > 0){
					for(IdxDimInfoVO tmp : dimList){
						dimNos.add(tmp.getDimNo());
						if(tmp.getDimType().equals(GlobalConstants4plugin.DIM_TYPE_CURRENCY)){
							vo.setHasCurrency(true);
						}
					}
				}
				vo.setDims(dimNos);
			}
		}
		params.clear();
		params.put("instanceId", instanceId);
		List<FavIdxDimFilterAndName> filterList = this.filterDao.getFiltersByInstanceId(params);
		for (Iterator<FavIdxDimFilterAndName> iter = filterList.iterator(); iter.hasNext();) {
			FavIdxDimFilterAndName favIdxDimFilterAndName = iter.next();
			String filterVal = StringUtils.replace(favIdxDimFilterAndName.getFilterVal(), "\"", "");
			
			List<String> filterValList = Arrays.asList(StringUtils.split(filterVal, ','));
			String jql = "select new com.yusys.bione.plugin.datashow.web.vo.DimItemTreeNode(t) from RptDimItemInfo t where t.id.dimTypeNo=?0 and t.id.dimItemNo in ?1";
			List<DimItemTreeNode> dimItemTreeNodeList = Lists.newArrayList();
			if (filterValList.size() > 0) {
				dimItemTreeNodeList = this.baseDAO.findWithIndexParam(jql, favIdxDimFilterAndName.getId().getDimNo(), filterValList);
			}
			favIdxDimFilterAndName.setSelectNodes(dimItemTreeNodeList);
		}
		RptFavQueryins ins = this.getEntityById(RptFavQueryins.class, instanceId);
		List<RptFavIdxDim> dimList = this.dimMybatisDao.list(params);
		params.clear();
		params.put("idxs", list);
		params.put("filter", filterList);
		params.put("favDim", dimList);
		params.put("drawDate", getIdxsDrawDate(indexNos, types));
		params.put("busiType", GlobalConstants4plugin.RPT_FRS_BUSI_BANK);
		if(ins!=null && StringUtils.isNotBlank(ins.getBusiType())){
			params.put("busiType", ins.getBusiType());
		}
		return params;
	}
	

	public IdxStoreInfoVO getStoreInfo(String instanceId) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("instanceId", instanceId);
		List<FavIdxDetailAndIndexNm> detailList = this.detailMybatisDao
				.list(condition);
		List<RptFavIdxDim> dimList = this.dimMybatisDao.list(condition);
		List<FavIdxDimFilterAndName> filterList = this.filterDao
				.getFiltersByInstanceId(condition);
		condition.clear();
		if (detailList != null && detailList.size() > 0) {
			List<String> indexLstTmp = new ArrayList<String>();
			for (FavIdxDetailAndIndexNm favIdx : detailList) {
				if (!indexLstTmp.contains(favIdx.getIndexNo())) {
					indexLstTmp.add(favIdx.getIndexNo());
				}
			}
			List<List<String>> indexNosParam = new ArrayList<List<String>>();
			if (indexLstTmp.size() > 1000) {
				int index = 0;
				int remain = indexLstTmp.size();
				while (remain > 1000) {
					indexNosParam.add(indexLstTmp.subList(index, index + 1000));
					index += 1000;
					remain -= 1000;
				}
				if (index < indexLstTmp.size()) {
					indexNosParam.add(indexLstTmp.subList(index,
							indexLstTmp.size()));
				}
			} else {
				indexNosParam.add(indexLstTmp);
			}
			condition.put("ids", indexNosParam);
		}
		List<RptDimTypeInfo> dimTypeList = this.idxDimRelDao
				.getDimByIdxInfo(condition);// 去重
		List<RptDimTypeInfo> newDimTypeList = new ArrayList<RptDimTypeInfo>();
		Map<String, RptDimTypeInfo> map = new HashMap<String, RptDimTypeInfo>();
		for (RptDimTypeInfo tmp : dimTypeList) {
			map.put(tmp.getDimTypeNo(), tmp);
		}
		for (String tmp : map.keySet()) {
			newDimTypeList.add(map.get(tmp));
		}

		IdxStoreInfoVO vo = new IdxStoreInfoVO();
		vo.setDetailList(detailList);
		vo.setDimList(dimList);
		vo.setFilterList(filterList);
		vo.setDimTypeList(newDimTypeList);// 全部维度
		return vo;
	}

	public List<RptDimTypeInfo> getDim(String indexNos, String indexVerIds) {
		return null;
	}

	public boolean checkQueryNm(String folerId, String queryNm) {
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("queryNm", queryNm);
		condition.put("folderId", folerId);
		condition.put("createUser", BioneSecurityUtils.getCurrentUserId());
		List<RptFavQueryins> list = this.queryMybatisDao.getAll(condition);
		if (list == null || list.size() == 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public void saveIdxHis(String indexNos, String ip) {
		
		if (!StringUtils.isEmpty(indexNos)) {
			List<Map<String, Object>> list = (List<Map<String, Object>>)(List<?>)JSON.parseArray(indexNos);
			for (Map<String, Object> map : list) {
				RptIdxVisitHis his = new RptIdxVisitHis();
				his.setAccessIp(ip);
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
				his.setAccessDate(df.format(new Date()));
				his.setAccessTime(new Timestamp(System.currentTimeMillis()));
				his.setHisId(RandomUtils.uuid2());
				his.setIndexNo(map.get("indexNo").toString());
				his.setIndexVerId(new BigDecimal(map.get("indexVerId")
						.toString()));
				his.setUserId(BioneSecurityUtils.getCurrentUserId());
				this.hisDao.saveHis(his);
			}
		}
	}

	/**
	 * 默认值信息
	 * 
	 * @return
	 */
	public Map<String, Object> getInitInfo() {
		Map<String, Object> result = Maps.newHashMap();
		Map<String, Object> sqlParam = Maps.newHashMap();
		sqlParam.put("dataItemNum", "FLOW_CERR_ZZ");
		List<CerrWorkDateVO> results = this.idxDao.getCurrentDate(sqlParam);
		result.put("date", results.size() > 0 ? results.get(0).getToday() : "");
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		sqlParam.clear();
		sqlParam.put("orgNo", user.getOrgNo());
		sqlParam.put("logicSysNo", user.getCurrentLogicSysNo());
		List<RptOrgInfoVO> infos = idxDao.getOrgSimpleInfo(sqlParam);
		if (infos.size() > 0) {
			RptOrgInfoVO info = infos.get(0);
			result.put("orgNo", info.getOrgNo());
			result.put("orgName", info.getOrgNm());
		}
		return result;
	}

	public List<CommonComboBoxNode> getParamMoneyListByParams(String type) {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		Map<String, Object> map = Maps.newHashMap();
		map.put("type", type);
		List<BioneParamInfo> paramList = this.idxInfoDao
				.getParamDeptListByParams(map);
		for (BioneParamInfo param : paramList) {
			CommonComboBoxNode node = new CommonComboBoxNode();
			node.setId(param.getParamValue());
			node.setText(param.getParamName());
			nodes.add(node);
		}
		return nodes;
	}

	public List<String> getUserIdxDefSrc(BioneUser user) {
		Map<String, Object> params = Maps.newHashMap();
		if(!user.isSuperUser()){
			params.put("userNo", user.getUserId());
			params.put("orgNo", user.getOrgNo());
		}
		return idxDao.getUserIdxDefSrc(params);
	}

	/**
	 * 指标树-用户定义
	 * 
	 * @param user
	 * @return
	 */
	public List<CommonTreeNode> getIdxTreeByDefUser(BioneUser user, String upNo, String searchNm) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("defSrc", "03");
		params.put("upNo", StringUtils.isEmpty(upNo) ? '0' : upNo);
		params.put("defUser", user.getUserId());
		if (StringUtils.isNotEmpty(searchNm)) {
			params.put("indexNm", "%" + searchNm + "%");
		}
		return getIdxTreeByDef(params);
	}

	/**
	 * 指标树-部门定义
	 * 
	 * @param user
	 * @return
	 */
	public List<CommonTreeNode> getIdxTreeByDefOrg(BioneUser user, String upNo, String searchNm) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("defSrc", "02");
		params.put("upNo", StringUtils.isEmpty(upNo) ? '0' : upNo);
		params.put("defOrg", user.getOrgNo());
		if (StringUtils.isNotEmpty(searchNm)) {
			params.put("indexNm", "%" + searchNm + "%");
		}
		return getIdxTreeByDef(params);
	}
	
	private List<CommonTreeNode> getIdxTreeByDef(Map<String, Object> params) {
		String iconPath = APP_CONTEXT_PATH + "/" + ICON_URL;
		String cataIcon = iconPath + "/folder_magnify.png";
		String idxIcon = iconPath + "/grid.png";
		List<RptIdxInfo> idxLs = idxDao.findIdxInfoBySrc(params);
		List<RptIdxCatalog> cataLs = idxDao.findLimitIdxCataInfoBySrc(params);
		List<CommonTreeNode> nodeLs = Lists.newArrayList();
		CommonTreeNode node = null;
		Set<String> set = Sets.newLinkedHashSet();
		for (Iterator<RptIdxInfo> iter = idxLs.iterator(); iter.hasNext();) {
			RptIdxInfo idx = iter.next();
			set.add(idx.getIndexCatalogNo());
			node = new CommonTreeNode();
			node.setId(RandomUtils.uuid2());
			node.setText(idx.getIndexNm());
			node.setUpId(idx.getIndexCatalogNo());
			node.setData(idx);
			node.setIsParent(false);
			node.setIcon(idxIcon);
			nodeLs.add(node);
		}
		Set<CommonTreeNode> nodeTmp = Sets.newHashSet();
		Map<String, CommonTreeNode> nodeMap = Maps.newHashMap();
		Set<CommonTreeNode> hasIdx = Sets.newHashSet();
		
		Iterator<RptIdxCatalog> iter = cataLs.iterator();
		while (iter.hasNext()) {
			RptIdxCatalog cata = iter.next();
			node = new CommonTreeNode();
			node.setId(cata.getIndexCatalogNo());
			node.setUpId(cata.getUpNo());
			node.setText(cata.getIndexCatalogNm());
			node.setIsParent(true);
			node.setData(cata);
			node.setIcon(cataIcon);
			if (!"0".equals(cata.getRemark())) {
				hasIdx.add(node);
			}
			nodeMap.put(node.getId(), node);
		}
		String upNo = String.valueOf(params.get("upNo"));
		for (Iterator<CommonTreeNode> it = hasIdx.iterator(); it.hasNext();) {
			CommonTreeNode pNode = it.next();
			while(pNode != null) {
				if (StringUtils.isNotEmpty(upNo) && upNo.equals(pNode.getUpId())) {
					nodeTmp.add(pNode);
				}
				pNode = nodeMap.get(pNode.getUpId());
			}
		}
		List<CommonTreeNode> finalNodes = Lists.newArrayList();
		finalNodes.addAll(nodeTmp);
		finalNodes.addAll(nodeLs);
		return finalNodes;
	}

	public RptDimTypeInfo getDimTypeInfo(String dimNo) {
		String jql = "select dim from RptDimTypeInfo dim where dim.dimTypeNo = ?0";
		return this.baseDAO.findUniqueWithIndexParam(jql, dimNo);
	}

	public RptIdxInfo getIdxInfo(String indexNo, String indexVerId) {
		String jql = "select info from RptIdxInfo info where info.id.indexNo = ?0 and info.id.indexVerId = ?1";
		return this.baseDAO.findUniqueWithIndexParam(jql, indexNo, new Long(indexVerId));
	}

	public Map<String, String> getDimNm(String dimNos) {
		Map<String, String> map = new HashMap<String, String>();
		String jql = "select info from RptDimTypeInfo info where info.dimTypeNo in (?0)";
		if(StringUtils.isNotBlank(dimNos)){
			String dim[] = StringUtils.split(dimNos, ',');
			List<String> condition = new ArrayList<String>();
			for(String tmp : dim){
				condition.add(tmp);
			}
			List<RptDimTypeInfo> list = this.baseDAO.findWithIndexParam(jql, condition);
			for(RptDimTypeInfo info : list){
				map.put(info.getDimTypeNo(), info.getDimTypeNm());
			}
		}
		return map;
	}
	
	public List<CommonComboBoxNode> getIdxRule(String ruleType){
		List<String> ruleTypes = new ArrayList<String>();
		
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		Map<String,Object> params = new HashMap<String, Object>();
		String jql = "select rule from RptIdxCalcRule rule where 1=1 ";
		
		if(!ruleType.equals(GlobalConstants4plugin.RULE_TYPE_COMMON)){
			jql += " and rule.ruleType in :ruleType";
			ruleTypes.add(GlobalConstants4plugin.RULE_TYPE_COMMON);
			ruleTypes.add(ruleType);
			params.put("ruleType", ruleTypes);
		}
		jql += " order by rule.sortOrder";
		List<RptIdxCalcRule> rules = this.baseDAO.findWithNameParm(jql, params);
		if(rules != null && rules.size() > 0){
			for(RptIdxCalcRule rule : rules){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(rule.getRuleId());
				node.setText(rule.getRuleNm());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public List<CommonComboBoxNode> getIdxTimeMeasure(){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		List<RptIdxTimeMeasure> measures = this.getAllEntityList(RptIdxTimeMeasure.class, "sortOrder", false);
		if(measures != null && measures.size() > 0){
			for(RptIdxTimeMeasure measure : measures){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(measure.getTimeMeasureId());
				node.setText(measure.getMeasureNm());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public List<CommonComboBoxNode> getIdxMode(){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		List<RptIdxValType> vals = this.getAllEntityList(RptIdxValType.class, "sortOrder", false);
		if(vals != null && vals.size() > 0){
			for(RptIdxValType val : vals){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(val.getModeId());
				node.setText(val.getModeNm());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	/**
	 * 查询翻牌日期，若没有，取前一天
	 * @param indexNo
	 * @return
	 */
	public String getIdxDrawDate(String indexNo,String type) {
		String sql = "select draw.index_no,draw.draw_date from RPT_IDX_DRAW_INFO draw where draw.index_no = ?0 ";
		List<Object[]> lists = this.baseDAO.findByNativeSQLWithIndexParam(sql, indexNo);
		if(lists != null && lists.size() > 0){
			if(lists.get(0)[1]!=null){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				String date = lists.get(0)[1].toString();
				try {
					date = sdf1.format(sdf.parse(date));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return "";
				}
				return date; 
			}
			else{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
				if(type.equals("01"))
					return sdf1.format(DateUtils.getLastDate(sdf.format(new Date())));
				else if(type.equals("02"))
					return sdf1.format(DateUtils.lastDateOfMonth(sdf.format(new Date())));
				else if(type.equals("03"))
					return sdf1.format(DateUtils.lastDateOfSeason(sdf.format(new Date())));
				else if(type.equals("04"))
					return sdf1.format(DateUtils.lastDateOfYear(sdf.format(new Date())));
				else
					return sdf1.format(DateUtils.getLastDate(sdf.format(new Date())));
			}
		}
		else{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			if(type.equals("01"))
				return sdf1.format(DateUtils.getLastDate(sdf.format(new Date())));
			else if(type.equals("02"))
				return sdf1.format(DateUtils.lastDateOfMonth(sdf.format(new Date())));
			else if(type.equals("03"))
				return sdf1.format(DateUtils.lastDateOfSeason(sdf.format(new Date())));
			else if(type.equals("04"))
				return sdf1.format(DateUtils.lastDateOfYear(sdf.format(new Date())));
			else
				return sdf1.format(DateUtils.getLastDate(sdf.format(new Date())));
		}	
	}
	
	/**
	 * 查询翻牌日期，若没有，取前一天
	 * @param indexNo
	 * @return
	 */
	public String getIdxsDrawDate(List<String> indexNos,List<String> types) {
		String date = "";
		if(indexNos != null && indexNos.size() > 0){
			String sql = "select draw.index_no,draw.draw_date from RPT_IDX_DRAW_INFO draw where draw.index_no in ?0 ";
			List<Object[]> lists = this.baseDAO.findByNativeSQLWithIndexParam(sql, indexNos);
			Map<String,String> dateMap = new HashMap<String,String>();
;			if(lists != null && lists.size()>0){
				for(Object[] list : lists){
					dateMap.put(list[0].toString(), list[1].toString());
				}
			}
			int i = 0;
			for(String indexNo : indexNos){
				String curDate = "";
				if(dateMap.get(indexNo)!=null){
					curDate = dateMap.get(indexNo);
				}
				else{
					String type =types.get(i);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					
					if(type.equals("01"))
						curDate = sdf.format(DateUtils.getLastDate(sdf.format(new Date())));
					else if(type.equals("02"))
						curDate = sdf.format(DateUtils.lastDateOfMonth(sdf.format(new Date())));
					else if(type.equals("03"))
						curDate = sdf.format(DateUtils.lastDateOfSeason(sdf.format(new Date())));
					else if(type.equals("04"))
						curDate = sdf.format(DateUtils.lastDateOfYear(sdf.format(new Date())));
					else
						curDate = sdf.format(DateUtils.getLastDate(sdf.format(new Date())));
				}
				if(!StringUtils.isNotBlank(date)){
					date = curDate;
				}
				else if(curDate.compareTo(date)<0){
					date = curDate;
				}
				i++;
			}
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			date = sdf1.format(sdf.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
		}
		return date;
	}
	
}
