package com.yusys.biapp.input.template.service;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.catalog.service.DataInputCatalogBS;
import com.yusys.biapp.input.common.InputConstants;
import com.yusys.biapp.input.data.entity.MtoolDbTableProBO;
import com.yusys.biapp.input.data.service.RdbConnectionManagerBS;
import com.yusys.biapp.input.dict.entity.RptInputListDataDictInfo;
import com.yusys.biapp.input.dict.service.InputDataDictBS;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableConstraint;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableFieldInf;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableInfo;
import com.yusys.biapp.input.inputTable.service.InputTableBS;
import com.yusys.biapp.input.inputTable.service.TableConstrainBS;
import com.yusys.biapp.input.inputTable.service.TableFieldBS;
import com.yusys.biapp.input.rule.entity.RptInputListDataRuleInfo;
import com.yusys.biapp.input.rule.entity.RptInputListRuleItemInfo;
import com.yusys.biapp.input.rule.service.DataRuleBS;
import com.yusys.biapp.input.rule.service.RuleItemBS;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleFile;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.entity.RptInputLstTempleRule;
import com.yusys.biapp.input.template.utils.object.BOList;
import com.yusys.biapp.input.template.vo.RptInputLstTempleInfoVO;
import com.yusys.biapp.input.utils.FileUpAndDownUtils;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.DateUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.authobj.service.OrgBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.mtool.repository.mybatis.DataSourceDao;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.entity.BioneUserInfo;
import com.yusys.bione.frame.user.service.UserBS;

/**
 * @author dada
 */
@Service
@Transactional(readOnly = false)
public class TempleBS extends BaseBS<RptInputLstTempleInfo> {
	private final Log log = LogFactory.getLog(TempleBS.class);

	@Autowired
	private FileUpAndDownUtils smbFileUpAndDown;
	@Autowired
	private DataInputCatalogBS dirBS;
	// @Autowired
	// private DirBS dirBS;
	@Autowired
	private DataSourceBS dataSourceBS;
	@Autowired
	private DataRuleBS dataRulesBS;
	@Autowired
	private RuleItemBS ruleItemBS;
	@Autowired
	private TempleRuleInfoBS templeRulesBS;
	@Autowired
	private TempleFileBS templeFileBS;
	@Autowired
	private TempleFieldBS templeColumnsBS;
	@Autowired
	private TempleConstraintBS templePrimaryBS;

	@Autowired
	private InputTableBS tableBS;
	@Autowired
	private TableConstrainBS priIndexBS;
	@Autowired
	private TableFieldBS tableFieldBS;
	@Autowired
	private RdbConnectionManagerBS rdbConnectionManagerBS;
	@Autowired
	private InputDataDictBS dictBS;
	@Autowired
	private DataSourceDao dataSourceDao;
	@Autowired
	private OrgBS orgBS;
	@Autowired
	private UserBS userBS;
	/**
	 * 获取模版列表
	 * 
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @param realId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<RptInputLstTempleInfo> getTempleList(int firstResult,
			int pageSize, String orderBy, String orderType, Map<String, Object> conditionMap, String realId, boolean toTask) {
		StringBuilder jql = new StringBuilder("");
		Map<String, Object> values = (Map<String, Object>) conditionMap.get("params");
		if (realId == null || "0".equals(realId)) {
			jql.append("select temple from RptInputLstTempleInfo temple where temple.logicSysNo=:logicSysNo");
			values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		} else {
			jql.append("select temple from RptInputLstTempleInfo temple where temple.catalogId=:dirId and temple.logicSysNo=:logicSysNo");
			values.put("dirId", realId);
			values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		}
		if (toTask)
			jql.append(" and temple.templeSts=1 and temple.templeId not in (select distinct rel.id.exeObjId from RptTskExeobjRel rel)");
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}

		/** 有权限看的 TODO*/
//		TempResImpl tempResImpl = SpringContextHolder.getBean(TempResImpl.name);
//		List<String> idList = tempResImpl.getTempId();
//
//		if (idList == null || idList.isEmpty())
//			return new SearchResult<RptInputLstTempleInfo>();
//		jql.append(" and temple.templeId in :templeId");
//		values.put("templeId", idList);
		/*
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		if (!user.isSuperUser()) {
			//jql.append(" and ( temple.defSrc='01' or (temple.defSrc='02' and temple.defOrg like :defOrg) or  temple.defUser=:defUser )");
			jql.append(" and ( temple.defSrc='01' or (temple.defSrc='02' and temple.defOrg like :defOrg) or (temple.defSrc='03' and temple.defUser like :defUser) )");
			values.put("defUser", "%,"+user.getUserId()+",%");
			values.put("defOrg", "%,"+user.getOrgNo()+",%");
		}
		*/
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by Temple." + orderBy + " " + orderType);
		}
		SearchResult<RptInputLstTempleInfo> authObjDefList = this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(), values);
		return authObjDefList;
	}

	//
	// /**
	// * 获取模版字段列表
	// * @param firstResult
	// * @param pageSize
	// * @param orderBy
	// * @param orderType
	// * @param conditionMap
	// * @return
	// */
	// public SearchResult<UdipTempleColumns> getTempleColumnsList(int
	// firstResult, int pageSize, String orderBy,
	// String orderType, Map<String, Object> conditionMap) {
	// StringBuilder jql = new StringBuilder("");
	// jql.append("select temple from UdipTempleColumns temple where 1=1");
	// if (!conditionMap.get("jql").equals("")) {
	// jql.append(" and " + conditionMap.get("jql"));
	// }
	// if (!StringUtils.isEmpty(orderBy)) {
	// jql.append(" order by Temple." + orderBy + " " + orderType);
	// }
	// @SuppressWarnings("unchecked")
	// Map<String, ?> values = (Map<String, ?>) conditionMap.get("params");
	// SearchResult<UdipTempleColumns> authObjDefList =
	// this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(),
	// values);
	// return authObjDefList;
	// }
	//
	// /**
	// * 获取模版excel文件列表
	// * @param firstResult
	// * @param pageSize
	// * @param orderBy
	// * @param orderType
	// * @param conditionMap
	// * @return
	// */
	// public SearchResult<UdipTempleFile> getTempleFileList(int firstResult,
	// int pageSize, String orderBy,
	// String orderType, Map<String, Object> conditionMap) {
	// StringBuilder jql = new StringBuilder("");
	// jql.append("select temple from UdipTempleFile temple where 1=1");
	// if (!conditionMap.get("jql").equals("")) {
	// jql.append(" and " + conditionMap.get("jql"));
	// }
	// if (!StringUtils.isEmpty(orderBy)) {
	// jql.append(" order by Temple." + orderBy + " " + orderType);
	// }
	// @SuppressWarnings("unchecked")
	// Map<String, ?> values = (Map<String, ?>) conditionMap.get("params");
	// SearchResult<UdipTempleFile> authObjDefList =
	// this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(),
	// values);
	// return authObjDefList;
	// }
	//
	// /**
	// * 获取所有文件列表
	// * @param firstResult
	// * @param pageSize
	// * @param orderBy
	// * @param orderType
	// * @param conditionMap
	// * @return
	// */
	// @SuppressWarnings("unchecked")
	// public SearchResult<UdipTemple> getAllTempleList(int firstResult, int
	// pageSize, String orderBy, String orderType,
	// Map<String, Object> conditionMap) {
	//
	// StringBuilder jql = new StringBuilder("");
	// jql.append("select temple from UdipTemple temple where temple.logicSysNo=:logicSysNo");
	//
	// if (!conditionMap.get("jql").equals("")) {
	// jql.append(" and " + conditionMap.get("jql"));
	// }
	// if (!StringUtils.isEmpty(orderBy)) {
	// jql.append(" order by Temple." + orderBy + " " + orderType);
	// }
	// Map<String, Object> values = (Map<String, Object>)
	// conditionMap.get("params");
	// values.put("logicSysNo",
	// BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
	// SearchResult<UdipTemple> authObjDefList =
	// this.baseDAO.findPageWithNameParam(firstResult, pageSize, jql.toString(),
	// values);
	// return authObjDefList;
	// }
	//
	/**
	 * 根据角色获取补录任务和补录任务实例
	 * 
	 * @param logicSysNo
	 *            逻辑系统标识
	 *            BiOneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo()
	 * @param roles
	 *            用户角色 BiOneSecurityUtils.getCurrentUserInfo().getRoleList()
	 * @return 由目录，任务，实例构成的树
	 */
	public List<CommonTreeNode> buildTempleTree(String logicSysNo,
			List<String> roles) {

		List<CommonTreeNode> nodeList = Lists.newArrayList();
		/** 目录 **/
		List<CommonTreeNode> dirList = dirBS.getByType(
				UdipConstants.DIR_TYPE_TEMP, logicSysNo);
		nodeList.addAll(dirList);

		List<RptInputLstTempleInfo> tempList = this
				.findEntityListByPropertyAndOrder("logicSysNo", logicSysNo,
						"templeName", false);

		for (RptInputLstTempleInfo info : tempList) {
			Map<String, String> map = Maps.newHashMap();
			map.put("type", UdipConstants.CASE_TEMPLE);
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(info.getTempleId());
			treeNode.setUpId(info.getCatalogId());
			treeNode.setText(info.getTempleName());
			treeNode.setIcon(UdipConstants.ICON_TEMPLE);
			treeNode.setParams(map);
			nodeList.add(treeNode);
		}
		return nodeList;
	}

	/**
	 * 获取任务对应的temple
	 * 
	 * @param firstResult
	 * @param pageSize
	 * @param orderBy
	 * @param orderType
	 * @param conditionMap
	 * @param templeIdSet
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public SearchResult<RptInputLstTempleInfo> getTaskCaseTempleList(
			int firstResult, int pageSize, String orderBy, String orderType,
			Map<String, Object> conditionMap, List<String> templeIdSet) {
		StringBuilder jql = new StringBuilder("");
		Map<String, Object> values = (Map<String, Object>) conditionMap
				.get("params");
		if (templeIdSet.size() > 0) {
			values.put("templeIdSet", templeIdSet);
			values.put("logicSysNo", BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo());
			jql.append("select temple from RptInputLstTempleInfo temple where temple.templeId in :templeIdSet and temple.logicSysNo=:logicSysNo");
		} else {
			jql.append("select temple from RptInputLstTempleInfo temple where temple.logicSysNo='0'");
		}
		if (!conditionMap.get("jql").equals("")) {
			jql.append(" and " + conditionMap.get("jql"));
		}
		if (!StringUtils.isEmpty(orderBy)) {
			jql.append(" order by " + orderBy + " " + orderType);
		}

		SearchResult<RptInputLstTempleInfo> authObjDefList = this.baseDAO
				.findPageWithNameParam(firstResult, pageSize, jql.toString(),
						values);
		return authObjDefList;
	}

	/**
	 * 根据上传的补录模版文件保存模版信息
	 * 
	 * @param tempList
	 */
	public void fileImpTemp(List<BOList> tempList, String dirId) {
		if (tempList != null && tempList.size() > 0) {
			String tableName = "";
			String dsId = "";
			String tableId = "";
			for (BOList boList : tempList) {
				String classNames = boList.getClassName();
				classNames = classNames.substring(classNames.lastIndexOf(".") + 1);
				try {
					if ("RptInputLstTempleInfo".equals(classNames)) {// 保存补录模版信息
						RptInputLstTempleInfo udipTemple = (RptInputLstTempleInfo) boList.get(0);
						tableName = udipTemple.getTableEnName();
						udipTemple.setCatalogId(dirId);
						this.saveOrUpdateEntity(udipTemple);
						continue;
					} else if ("RptInputLstTempleField".equals(classNames)) {// 保存补录模版字段信息
						if (boList != null && boList.size() > 0) {
							for (int i = 0; i < boList.size(); i++) {
								RptInputLstTempleField tempCol = (RptInputLstTempleField) boList.get(i);
								this.templeColumnsBS.saveOrUpdateEntity(tempCol);
							}
						}
						continue;
					} else if ("RptInputListDataDictInfo".equals(classNames)) {// 保存补录模版字段对应的数据字典信息
						if (boList != null && boList.size() > 0) {
							for (int i = 0; i < boList.size(); i++) {
								RptInputListDataDictInfo dataLib = (RptInputListDataDictInfo) boList.get(i);
								if (dataLib != null) {
									String libId = "";
									List<RptInputLstTempleField> templeColumnsList = this.templeColumnsBS.getEntityListByProperty(RptInputLstTempleField.class, "dictId", dataLib.getDictId());
									// 通过字典名称检查数据字典是否存在
									RptInputListDataDictInfo oldDataLib = this.dictBS.getEntityByProperty(RptInputListDataDictInfo.class, "dictName", dataLib.getDictName());
									if (oldDataLib != null && StringUtils.isNotBlank(oldDataLib.getDictId())) {// 如果存在
										oldDataLib.setCatalogId(dirId);
										libId = oldDataLib.getDictId();
										this.dictBS.saveOrUpdateEntity(oldDataLib);
									} else {
										dataLib.setCatalogId(dirId);
										libId = RandomUtils.uuid2();
										dataLib.setDictId(libId);
										this.dictBS.saveOrUpdateEntity(dataLib);
									}
									for (RptInputLstTempleField udipTempleColumns : templeColumnsList) {// 更新补录模版字段对应的数据字典ID
										udipTempleColumns.setDictId(libId);
										this.templeColumnsBS.saveOrUpdateEntity(udipTempleColumns);
									}
								}
							}
						}
						continue;
					} else if ("RptInputListTableInfo".equals(classNames)) {// 保存补录模版对应补录表信息
						RptInputListTableInfo udipTable = (RptInputListTableInfo) boList.get(0);
						if (udipTable != null) {
							RptInputListTableInfo udipTableInfo = this.tableBS.getEntityByProperty(RptInputListTableInfo.class, "tableEnName", udipTable.getTableEnName());
							if (udipTableInfo == null || StringUtils.isBlank(udipTableInfo.getTableId())) {
								tableId = RandomUtils.uuid2();
								udipTable.setTableId(tableId);
								dsId = udipTable.getDsId();
								udipTable.setCreateDate(DateUtils.getYYYY_MM_DD_HH_mm_ss());
								this.tableBS.saveOrUpdateEntity(udipTable);
							} else {
								tableId = "";
								dsId = udipTableInfo.getDsId();
							}
						}
						continue;
					} else if ("RptInputListTableFieldInf".equals(classNames)) {// 保存补录表字段信息
						if (boList != null && boList.size() > 0 && StringUtils.isNotBlank(tableId)) {
							for (int i = 0; i < boList.size(); i++) {
								RptInputListTableFieldInf tableCol = (RptInputListTableFieldInf) boList.get(i);
								tableCol.setTableId(tableId);
								this.tableFieldBS.saveOrUpdateEntity(tableCol);
							}
						}
						continue;
					} else if ("RptInputListTableConstraint".equals(classNames)) {// 保存补录表主键索引信息
						if (boList != null && boList.size() > 0 && StringUtils.isNotBlank(tableId)) {
							for (int i = 0; i < boList.size(); i++) {
								RptInputListTableConstraint priIndex = (RptInputListTableConstraint) boList.get(i);
								priIndex.setTableId(tableId);
								this.priIndexBS.saveOrUpdateEntity(priIndex);
							}
						}
						continue;
					} else if ("BioneDsInfo".equals(classNames)) {// 保存补录模版所包含的数据源信息
						if (boList != null && boList.size() > 0) {
							RptInputListTableInfo udipTableInfo = this.tableBS.getEntityByProperty(RptInputListTableInfo.class, "tableId", tableId);
							for (int i = 0; i < boList.size(); i++) {
								BioneDsInfo bioneDsInfo = (BioneDsInfo) boList.get(i);
								String newDsId = "";
								BioneDsInfo bioneDsInfos = dataSourceBS.getEntityByProperty(BioneDsInfo.class, "dsName", bioneDsInfo.getDsName().trim());
								if (bioneDsInfos == null || "".equals(bioneDsInfos.getDsId())) {
									String dsIds = RandomUtils.uuid2();
									bioneDsInfo.setDsId(dsIds);
									this.dataSourceBS.saveOrUpdateEntity(bioneDsInfo);
									newDsId = dsIds;
								} else {
									newDsId = bioneDsInfos.getDsId();
								}
								List<RptInputLstTempleInfo> udipTempleList1 = this.getEntityListByProperty(RptInputLstTempleInfo.class, "dsId", bioneDsInfo.getDsId());
								// 保存补录模版对应的数据源
								if (udipTempleList1 != null && udipTempleList1.size() > 0) {
									RptInputLstTempleInfo udipTemple1 = udipTempleList1.get(0);
									udipTemple1.setDsId(newDsId);
									this.saveOrUpdateEntity(udipTemple1);
								}
								// 保存数据字典对应的数据源
								List<RptInputListDataDictInfo> udipDataLibInfoList = this.getEntityListByProperty(RptInputListDataDictInfo.class, "dsId", bioneDsInfo.getDsId());
								if (udipDataLibInfoList != null && udipDataLibInfoList.size() > 0) {
									for (RptInputListDataDictInfo udipDataLibInfo1 : udipDataLibInfoList) {
										udipDataLibInfo1.setDsId(newDsId);
										this.dictBS.saveOrUpdateEntity(udipDataLibInfo1);
									}
								}
								// 保存补录表对应的数据源
								if (udipTableInfo != null && !"".equals(udipTableInfo.getTableId()) && bioneDsInfo.getDsId().equals(udipTableInfo.getDsId())) {
									udipTableInfo.setDsId(newDsId);
									dsId = newDsId;
									this.tableBS.saveOrUpdateEntity(udipTableInfo);
								}
							}
						}
						continue;
					} else if ("RptInputLstTempleConst".equals(classNames)) {// 保存补录模版字段对应的主键信息
						if (boList != null && boList.size() > 0) {
							for (int i = 0; i < boList.size(); i++) {
								RptInputLstTempleConst udipTemplePrimary = (RptInputLstTempleConst) boList.get(i);
								this.templePrimaryBS.saveOrUpdateEntity(udipTemplePrimary);
							}
						}
						continue;
					} else if ("RptInputLstTempleFile".equals(classNames)) {// 保存补录模版文件信息
						if (boList != null && boList.size() > 0) {
							RptInputLstTempleFile udipTempleFile = (RptInputLstTempleFile) boList.get(0);
							this.excelUps(udipTempleFile.getTempleId());
						}
						continue;
					} else if ("RptInputLstTempleRule".equals(classNames)) {// 保存补录模版规则信息
						if (boList != null && boList.size() > 0) {
							for (int i = 0; i < boList.size(); i++) {
								RptInputLstTempleRule udipTempleRules = (RptInputLstTempleRule) boList.get(i);
								this.templeRulesBS.saveOrUpdateEntity(udipTempleRules);
							}
						}
						continue;
					} else if ("RptInputListDataRuleInfo".equals(classNames)) {// 保存补录规则定义信息
						if (boList != null && boList.size() > 0) {
							for (int i = 0; i < boList.size(); i++) {
								RptInputListDataRuleInfo udipDataRulesInfo = (RptInputListDataRuleInfo) boList.get(i);
								this.dataRulesBS.saveOrUpdateEntity(udipDataRulesInfo);
							}
						}
						continue;
					} else if ("RptInputListRuleItemInfo".equals(classNames)) {// 保存补录规则子项信息
						if (boList != null && boList.size() > 0) {
							for (int i = 0; i < boList.size(); i++) {
								RptInputListRuleItemInfo udipRuleItemsInfo = (RptInputListRuleItemInfo) boList.get(i);
								this.ruleItemBS.saveOrUpdateEntity(udipRuleItemsInfo);
							}
						}
						continue;
					} else if ("UdipTableSql".equals(classNames)) {// 如果表不存在，执行建表sql
						if (boList != null && boList.size() > 0) {
							List<MtoolDbTableProBO> list = rdbConnectionManagerBS.getTableMoreList(dsId, tableName);
							if (list == null || list.size() <= 0) {
								String sql = (String) boList.get(0);
								if (StringUtils.isNotBlank(sql)) {
									this.tableBS.createTableBySql(sql, dsId);
								}
							}
						}
						continue;
					}
				} catch (Exception e) {
					log.error(e);
				}
			}
		}
	}

	/**
	 * 根据数据源和表名获取相应的表信息，生成建表sql
	 * 
	 * @param dsId
	 * @param tableName
	 */
	public String getTableSqlByTableName(String dsId, String tableName) {
		String sql = "";
		try {
			List<RptInputListTableInfo> tableInfoList = tableBS
					.getTableInfoByDsIdAndTableName(dsId, tableName);
			if (tableInfoList != null && tableInfoList.size() > 0) {
				RptInputListTableInfo tableInfo = tableInfoList.get(0);
				Map<String, RptInputListTableFieldInf> tableColMap = Maps.newHashMap();
				List<RptInputListTableConstraint> priIndexList = Lists.newArrayList();
				if (tableInfo != null && !"".equals(tableInfo.getTableId())) {
					List<RptInputListTableFieldInf> tableColList = tableBS
							.getEntityListByProperty(RptInputListTableFieldInf.class,
									"tableId", tableInfo.getTableId());
					if (tableColList != null && tableColList.size() > 0) {
						for (RptInputListTableFieldInf udipTableColInfo : tableColList) {
							tableColMap.put(udipTableColInfo.getFieldEnName(),
									udipTableColInfo);
						}
					}
					List<RptInputListTableConstraint> udipPriList = tableBS
							.getEntityListByProperty(RptInputListTableConstraint.class,
									"tableId", tableInfo.getTableId());
					if (udipPriList != null && udipPriList.size() > 0) {
						for (int i = 0; i < udipPriList.size(); i++) {
							RptInputListTableConstraint udipPriIndexInfo = udipPriList
									.get(i);
							if (UdipConstants.TAB_PRIMARY
									.equals(udipPriIndexInfo.getKeyType())) {
								udipPriIndexInfo.setKeyName("PK_"
										+ tableInfo.getTableEnName()
												.toUpperCase());
								udipPriIndexInfo
										.setKeyType(UdipConstants.TAB_PRIMARY);
							} else if (UdipConstants.TAB_UNIQUE
									.equals(udipPriIndexInfo.getKeyType())) {
								udipPriIndexInfo.setKeyName("UQ_"
										+ tableInfo.getTableEnName()
												.toUpperCase().trim() + i);
								udipPriIndexInfo
										.setKeyType(UdipConstants.TAB_UNIQUE);
							} else {
								udipPriIndexInfo.setKeyName("INDEX_"
										+ tableInfo.getTableEnName()
												.toUpperCase().trim() + i);
								udipPriIndexInfo
										.setKeyType(UdipConstants.TAB_INDEX);
							}
							priIndexList.add(udipPriIndexInfo);
						}
					}
					sql = tableBS.getTableInfoSql(tableInfo, tableColMap,
							priIndexList);
				}
			}

		} catch (Exception e) {
			log.error(e);
		}
		return sql;
	}

	/**
	 * 通过数据源和表名获取补录模版信息
	 * 
	 * @param dsId
	 * @param tableName
	 * @return
	 */
	public List<RptInputLstTempleInfo> getUdipTempleByDsIdAndTableName(
			String dsId, String tableName) {
		StringBuilder jql = new StringBuilder(1000);
		jql.append("select s from RptInputLstTempleInfo s where dsId =:dsId ");
		jql.append("and tableEnName =:tableEnName ");
		Map<String, String> values = Maps.newHashMap();
		values.put("dsId", dsId);
		values.put("tableEnName", tableName);
		return this.baseDAO.findWithNameParm(jql.toString(), values);
	}

	public RptInputLstTempleInfoVO getTempInfoById(String templeId) {
		RptInputLstTempleInfoVO vo = new RptInputLstTempleInfoVO();
		StringBuilder sql = new StringBuilder(1000);
		Map<String, Object> values = new HashMap<String, Object>();
		sql = sql
				.append(" SELECT  temples.temple_Id,temples.temple_Sts,temples.allow_Add,temples.allow_Update,temples.allow_Delete,")
				.append(" temples.duty_User,temples.is_check,temples.etl_Table,temples.allow_input_lower,temples.ds_id,temples.catalog_id,")
				.append(" temples.remark,temples.temple_name,temples.table_en_name,temples.org_column,temples.allow_input_hist,temples.logic_sys_no,")
				.append(" temples.create_user,temples.create_date,temples.duty_user_dept,")
				.append(" temples.is_start, ")//添加回写标识 cl
				.append(" cata.catalog_Name,ds.ds_Name,temples.unit,temples.def_org,temples.def_user,temples.def_src FROM BIONE_ORG_INFO org, RPT_INPUT_LIST_CATALOG_INFO cata,  Rpt_Input_Lst_Temple_Info temples LEFT JOIN BIONE_DS_INFO ds ")
				.append(" ON temples.ds_Id=ds.ds_id WHERE cata.catalog_id=temples.catalog_id and cata.catalog_Type =:catalogType ")
				.append(" and temples.temple_Id =:templeId");
		values.put("templeId", templeId);
		values.put("catalogType", "1");
		List<Object[]> infos = this.baseDAO.findByNativeSQLWithNameParam(
				sql.toString(), values);
		if (infos != null && infos.size() > 0) {
			Object[] obj = infos.get(0);
			vo.setTempleId(obj[0] != null ? obj[0].toString() : null);
			vo.setTempleSts(obj[1] != null ? obj[1].toString() : null);
			vo.setAllowAdd(obj[2] != null ? obj[2].toString() : null);
			vo.setAllowUpdate(obj[3] != null ? obj[3].toString() : null);
			vo.setAllowDelete(obj[4] != null ? obj[4].toString() : null);
			vo.setDutyUser(obj[5] != null ? obj[5].toString() : null);
			vo.setIsCheck(obj[6] != null ? obj[6].toString() : null);
			vo.setEtlTable(obj[7] != null ? obj[7].toString() : null);
			vo.setAllowInputLower(obj[8] != null ? obj[8].toString() : null);
			vo.setDsId(obj[9] != null ? obj[9].toString() : null);
			vo.setCatalogId(obj[10] != null ? obj[10].toString() : null);
			vo.setRemark(obj[11] != null ? obj[11].toString() : null);
			vo.setTempleName(obj[12] != null ? obj[12].toString() : null);
			vo.setTableEnName(obj[13] != null ? obj[13].toString() : null);
			vo.setOrgColumn(obj[14] != null ? obj[14].toString() : null);
			vo.setAllowInputHist(obj[15] != null ? obj[15].toString() : null);
			vo.setLogicSysNo(obj[16] != null ? obj[16].toString() : null);
			vo.setCreateUser(obj[17] != null ? obj[17].toString() : null);
			vo.setCreateDate(obj[18] != null ? obj[18].toString() : null);
			vo.setDutyUserDept(obj[19] != null ? obj[19].toString() : null);
			vo.setIsStart(obj[20] != null ? obj[20].toString() : null);
			vo.setCatalogName(obj[21] != null ? obj[21].toString() : null);
			vo.setDsName(obj[22] != null ? obj[22].toString() : null);
			vo.setUnit(obj[23] != null ? obj[23].toString() : null);
			vo.setDefOrg(obj[24] != null ? obj[24].toString() : null);
			vo.setDefUser(obj[25] != null ? obj[25].toString() : null);
			vo.setDefSrc(obj[26] != null ? obj[26].toString() : null);
		}
		String defOrg = vo.getDefOrg();
		if(StringUtils.isNotEmpty(defOrg)){
			StringBuilder buff = new StringBuilder();
			String[] orgArrays = defOrg.split(",");
			for(String orgNo:orgArrays){
				if(StringUtils.isNotEmpty(orgNo))
				{
					buff.append(orgBS.findOrgInfoByOrgNo(orgNo).getOrgName()).append(",");
				}
			}
			vo.setDefOrgNm(buff.toString());
		}
		String defUser = vo.getDefUser();
		if(StringUtils.isNotEmpty(defUser)){
			String tmpUser = defUser.replaceAll(",", "");
			if(StringUtils.isNotEmpty(defUser)&&StringUtils.isNotEmpty(tmpUser)){
				StringBuilder buff = new StringBuilder();
				String[] userArray = defUser.split(",");
				List<String>userIds=Lists.newArrayList();
				for(String userId:userArray){
					if(StringUtils.isNotEmpty(userId))
					{
						userIds.add(userId);
					}
				}
				List<BioneUserInfo>  userInfos = userBS.getUserInofByIds(userIds);
				boolean isFirst = true;
				for(BioneUserInfo bioneUserInfo:userInfos){
					if(!isFirst)
						isFirst = false;
					else 
						buff.append(",");
					buff.append(bioneUserInfo.getUserName());
				}
				vo.setDefUserNm(buff.toString());
			}
		}
		return vo;
	}

	// @Transactional(readOnly = false)
	// public void setTaskTempLock(String caseId, String templeId) {
	// List<UdipTaskTempLock> orgList =
	// this.getEntityListByProperty(UdipTaskTempLock.class, "caseId", caseId);
	// for (UdipTaskTempLock role : orgList) {
	// this.baseDAO.getEntityManager().remove(role);
	// }
	// if(StringUtils.isNotBlank(templeId)){
	// String[] ids = templeId.split(",");
	// for (int i = 0; i < ids.length; i++) {
	// UdipTaskTempLock info = new UdipTaskTempLock();
	// info.setCaseId(caseId);
	// info.setTempId(ids[i]);
	// info.setId(RandomUtils.uuid2());
	// info.setLockDate(DateUtils.getDateFormatTime(new Date(),"yyyy-MM-dd"));
	// this.baseDAO.getEntityManager().persist(info);
	// }
	// }
	// }
	//
	// public List<String> getTaskTempLock(String caseId) {
	// List<UdipTaskTempLock> orgList =
	// this.getEntityListByProperty(UdipTaskTempLock.class, "caseId", caseId);
	// List<String> list = Lists.newArrayList();
	// for (UdipTaskTempLock role : orgList) {
	// list.add(role.getTempId());
	// }
	// return list;
	// }
	//
	@Transactional(readOnly = false)
	public void excelUps(String id) {
		List<RptInputLstTempleField> listTempleColumns = this.templeColumnsBS.getTempleColumns(id);
		List<String> listColumns = Lists.newArrayList();
		List<String> listDetail = Lists.newArrayList();
		List<String> colorList = Lists.newArrayList();
		List<String> keylist = Lists.newArrayList();
		String[] propVal1 = { id, "primary" };
		String[] propName1 = { "templeId", "keyType" };
		List<RptInputLstTempleConst> udipPriIndexInfo = templePrimaryBS.findByPropertys(RptInputLstTempleConst.class, propName1, propVal1);
		if (!udipPriIndexInfo.isEmpty()) {
			String[] keyStr = udipPriIndexInfo.get(0).getKeyColumn().split(";");
			for (String key : keyStr) {
				keylist.add(key);
			}
		}
		Collections.sort(listTempleColumns, new Comparator<RptInputLstTempleField>() {
			@Override
			public int compare(RptInputLstTempleField o1, RptInputLstTempleField o2) {
                return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		for (int i = 0; i < listTempleColumns.size(); i++) {
			RptInputLstTempleField list = listTempleColumns.get(i);
			if (StringUtils.isNotBlank(list.getFieldCnName())) {
				listColumns.add(list.getFieldEnName() + "#" + list.getFieldCnName());
			} else {
				listColumns.add(list.getFieldCnName() ==null ? "null" : list.getFieldCnName());
			}
			if (keylist.contains(list.getFieldEnName())) {
				colorList.add(UdipConstants.HSSF_COLOR_RED);
			} else {
				colorList.add(UdipConstants.HSSF_COLOR_BLACK);
			}
			listDetail.add(list.getFieldDetail());
		}
		try {
			String fileName = this.getEntityById(id).getTempleName();
			String getTime = DateUtils.getFormatTime(new Long(new Date().getTime()), "yyyy-MM-dd_HH.mm.ss");
			String name = fileName + getTime + ".xls";
			String templeUrl = getRemoteUrl() + File.separator;
			String path = smbFileUpAndDown.fileNetUpload(fileName, name, templeUrl, listColumns, listDetail, colorList,id);

			// 保存文件信息
			RptInputLstTempleFile templeFile = new RptInputLstTempleFile();
			templeFile.setTempleId(id);
			templeFile.setOperUser(BioneSecurityUtils.getCurrentUserInfo().getLoginName());
			List<RptInputLstTempleFile> tempfile = templeFileBS.findEntityListByProperty("templeId", id);
			if (tempfile.isEmpty()) {
				templeFile.setSts("1");
			} else {
				int i = 0;
				for (RptInputLstTempleFile tempf : tempfile) {
					if (tempf.getSts().equals("1")) {
						i++;
					}
					if (i > 0) {
						templeFile.setSts("0");
					} else {
						templeFile.setSts("1");
					}
				}
			}
			templeFile.setCreateDate(DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
			templeFile.setFileName(name);
			templeFile.setFilePath(path);
			templeFile.setFileId(RandomUtils.uuid2());
			this.templeFileBS.saveOrUpdateEntity(templeFile);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
	}
	
	/**
	 * 保存用户上传模板
	 * @param templeFile
	 */
	public void setTempleFile(RptInputLstTempleFile templeFile){
        templeFile.setOperUser(BioneSecurityUtils.getCurrentUserInfo().getLoginName());
        List<RptInputLstTempleFile> tempfile = templeFileBS.findEntityListByProperty("templeId", templeFile.getTempleId());
        if (tempfile.isEmpty()) {
            templeFile.setSts("1");
        } else {
            int i = 0;
            for (RptInputLstTempleFile tempf : tempfile) {
                if (tempf.getSts().equals("1")) {
                    i++;
                }
                if (i > 0) {
                    templeFile.setSts("0");
                } else {
                    templeFile.setSts("1");
                }
            }
        }
        this.templeFileBS.saveOrUpdateEntity(templeFile);
	}
	

	/**
	 * 获取文件上传路径
	 * 
	 * @param smbType
	 * @return
	 * @throws Exception
	 */
	public String getRemoteUrl() throws Exception {
		PropertiesUtils pUtils = PropertiesUtils.get("input.properties");
		if(null == pUtils){
			return GlobalConstants4frame.APP_REAL_PATH + File.separatorChar +InputConstants.FILE_IMP_TEMP_PATH;
		}else{
			return pUtils.getProperty("UPLOAD_FILE_PATH");
		}
		
	}
	
	public List<String> getTempleIdByTaskId(String taskId) {
		String jql = "select t.id.exeObjId from RptTskExeobjRel t where t.id.taskId=?0";
		return this.baseDAO.findWithIndexParam(jql, taskId);
	}
	
	public Object getTemplePathByTaskId(String taskId) {
	    
	    StringBuilder sb = new StringBuilder();
	    sb.append("SELECT tmp.temple_id,tmp.file_path,tsk.task_nm FROM rpt_input_lst_temple_file tmp ");
        sb.append(" LEFT JOIN rpt_tsk_info tsk ");
        sb.append(" ON tmp.temple_id = tsk.exe_obj_id ");
        sb.append(" WHERE tsk.task_id = ?0");
        sb.append(" and tmp.sts = '1'");
	    return this.baseDAO.createNativeQueryWithIndexParam(sb.toString(), taskId).getSingleResult();
	}
	
	public List<CommonComboBoxNode>getDsList(){
		
		List<BioneDsInfo> dsList = dataSourceDao.search();
		if(dsList==null|| dsList.isEmpty())
			return null;
		List<CommonComboBoxNode> nodeList = Lists.newArrayList();
		for(BioneDsInfo ds : dsList){
			CommonComboBoxNode node = new CommonComboBoxNode();
			node.setId(ds.getDsId());
			node.setText(ds.getDsName()+"("+ds.getConnUrl()+"@"+ds.getConnUser()+")");
			nodeList.add(node);
		}
		return nodeList;
	}
	
	public void updateIsStart(String templeId,String isStart){
		this.jdbcBaseDAO.update("UPDATE RPT_INPUT_LST_TEMPLE_INFO t SET     t.IS_START=? WHERE t.TEMPLE_ID=?   ",new String[]{isStart,templeId});
	}
	
	public void updateIsSendNotify(String templeId,String isSendNotify){
		this.jdbcBaseDAO.update("UPDATE RPT_INPUT_LST_TEMPLE_INFO t SET     t.IS_SEND_NOTIFY=? WHERE t.TEMPLE_ID=?   ",new String[]{isSendNotify,templeId});
	}
	
	public List<String>getTempleOrgs(String templeId){

		List<String>orgList = null;
		StringBuilder sql = new StringBuilder(200);
		Map<String,String>values=Maps.newHashMap();
		//查询机构范围 cl
		sql.append("select temples.def_org from Rpt_Input_Lst_Temple_Info where  temples.temple_Id =:templeId and temples.def_src='2'");
		values.put("templeId", templeId);
		String queryOrg = this.baseDAO.findUniqueWithNameParam(sql.toString(), values);
		if(StringUtils.isNotEmpty(queryOrg)){
			orgList = Lists.newArrayList();
			String[] orgs = queryOrg.split(",");
			for(String org:orgs){
				if(StringUtils.isNotEmpty(org))
					orgList.add(org);
			}
		}
		return orgList;
	}
	
	/**
	 * 根据模板id查询关联的任务
	 */
	public boolean checkTaskByTemplateId(String[] ids) {
		StringBuilder sql = new StringBuilder(200);
		sql.append("select t.task_id from RPT_TSK_EXEOBJ_REL t where t.exe_obj_id in (");
		for (int i = 0; i < ids.length; i++) {
			if(i == 0) {
				sql.append("'").append(ids[i]).append("'");
			}else {
				sql.append(",").append("'").append(ids[i]).append("'");
			}
		}
		sql.append(")");
		List<Object[]> resule = this.baseDAO.findByNativeSQLWithNameParam(sql.toString(),null);
		return resule.size()==0;
	}
}
