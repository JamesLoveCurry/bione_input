package com.yusys.bione.plugin.frsorg.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.auth.entity.BioneAuthObjUserRel;
import com.yusys.bione.frame.auth.service.AuthUsrRelBS;
import com.yusys.bione.frame.authobj.entity.BioneOrgInfo;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.variable.entity.BioneParamInfo;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.frsorg.entity.RptFimAddrInfo;
import com.yusys.bione.plugin.frsorg.repository.RptMgrFrsOrgMybatisDao;
import com.yusys.bione.plugin.frsorg.web.vo.RptMgrFrsOrgVo;
import com.yusys.bione.plugin.rptmgr.util.SplitStringBy1000;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfoPK;
import com.yusys.bione.plugin.rptorg.web.vo.RptMgrFrsOrgSource;
import com.yusys.bione.plugin.rptsys.repository.RptParamMybatisDao;
import com.yusys.bione.plugin.wizard.web.vo.OrgImportVO;

@Service
@Transactional(readOnly = true)
public class RptMgrFrsOrgBS extends BaseBS<Object> {

	@Autowired
	private RptMgrFrsOrgMybatisDao rptMgrFrsOrgMybatisDao;

	@Autowired
	private RptParamMybatisDao rptParamMybatisDao;

	@Autowired
	private AuthUsrRelBS relBS;


	/**
	 * 获取监管机构类型
	 * @param orgType
	 * @param upOrgNo
	 * @param orgNo
	 * @param path
	 * @param mgrOrgNo
	 * @param state
	 * @param isOrgReport 
	 * @return
	 */
	public List<CommonTreeNode> getDataTrees(String orgType, String upOrgNo,
			String orgNo, String path, String mgrOrgNo, String state, String isOrgReport) {
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("paramTypeNo", GlobalConstants4plugin.TREE_PARAM_TYPE);
		List<BioneParamInfo> lists = this.rptParamMybatisDao.paramInfolist(maps); // 通过参数获取根节点
		List<CommonTreeNode> node = Lists.newArrayList();
		//第一次先加载监管业务类型
		if (orgNo == null && orgType == null) {
			for (int i = 0; i < lists.size(); i++) {
				Map<String, String> oneParams = new HashMap<String, String>();
				CommonTreeNode oneNode = new CommonTreeNode();
				oneNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
				oneNode.setText(lists.get(i).getParamName());
				oneNode.setIcon(path + "/" + GlobalConstants4frame.ICON_URL+ "/house.png");
				oneNode.setIsParent(true);
				oneParams.put("orgType", lists.get(i).getParamValue());
				oneParams.put("orgNo", GlobalConstants4frame.TREE_ROOT_NO);
				oneParams.put("type", "org");
				oneNode.setParams(oneParams);
				node.add(oneNode);
			}
		} else if (orgType != null && orgNo == null) {//加载指定监管业务类型节点
			for (int i = 0; i < lists.size(); i++) {
				if (orgType.equals(lists.get(i).getParamValue())) {
					Map<String, String> pbcParams = new HashMap<String, String>();
					CommonTreeNode pbcNode = new CommonTreeNode();
					pbcNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
					pbcNode.setIsParent(true);
					pbcParams.put("orgType", orgType);
					pbcParams.put("orgNo", GlobalConstants4frame.TREE_ROOT_NO);
					pbcParams.put("type", "org");
					pbcNode.setParams(pbcParams);
					pbcNode.setText(lists.get(i).getParamName());
					pbcNode.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
							+ "/house.png");
					node.add(pbcNode);
				}
			}
		} else {//开始加载机构树节点
			//查询报送机构树类型（是否根据汇总关系构造机构树）
			String jql = "select param.paramValue from BioneParamInfo param where param.paramTypeNo = 'OrgTreeType'";
			List<String> paramValues = this.baseDAO.findWithIndexParam(jql);
			boolean isSuper = BioneSecurityUtils.getCurrentUserInfo()
					.isSuperUser(); // true是管理员;false不是管理员
			if (isSuper == true) {// 是管理员
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("upOrgNo", orgNo);
				map.put("orgType", orgType);
				map.put("isOrgReport", isOrgReport);

				List<RptOrgInfo> list = Lists.newArrayList();
				Map<String, Object> freeMap = new HashMap<String, Object>();
				freeMap.put("orgType", orgType);
				freeMap.put("upOrgNo", GlobalConstants4plugin.TREE_FREE_ROOT_NO);
				freeMap.put("isOrgReport", isOrgReport);
				List<RptOrgInfo> freeList = this.rptMgrFrsOrgMybatisDao
						.findFreeOrg(freeMap);//获取游离机构
				if (freeList != null && freeList.size() != 0) {
					Map<String, String> params = new HashMap<String, String>();
					if (upOrgNo == null || upOrgNo.equals("")) {
						CommonTreeNode freeNode = new CommonTreeNode();
						freeNode.setId(GlobalConstants4plugin.TREE_FREE_ROOT_NO);
						freeNode.setText("游离机构");
						freeNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
						params.put("orgNo",
								GlobalConstants4plugin.TREE_FREE_ROOT_NO);
						params.put("upOrgNo",
								GlobalConstants4frame.TREE_ROOT_NO);
						params.put("orgNm", "游离机构");
						params.put("orgType", orgType);
						params.put("type", "org");
						
						freeNode.setIcon(path + "/"
								+ GlobalConstants4frame.ICON_URL
								+ "/application_view_detail.png");
						freeNode.setParams(params);
						freeNode.setIsParent(true);
						node.add(freeNode);
					}
				}

				if (StringUtils.isNotBlank(state)) {
					if (state.equals("1")) {
						list = this.rptMgrFrsOrgMybatisDao.findOrg(map);
						list = getNotFree(list, state);
					} else {
						if(paramValues != null && paramValues.size() > 0){
							if("Y".equals(paramValues.get(0))){
								list = this.rptMgrFrsOrgMybatisDao.findOrgThenBySumRel(map);
							}else{
								list = this.rptMgrFrsOrgMybatisDao.findOrgThen(map);
							}
						}else {
							list = this.rptMgrFrsOrgMybatisDao.findOrgThen(map);
						}
						list = getNotFree(list, state);
					}
				} else {
					if(paramValues != null && paramValues.size() > 0){
						if("Y".equals(paramValues.get(0))){
							list = this.rptMgrFrsOrgMybatisDao.findOrgThenBySumRel(map);
						}else{
							list = this.rptMgrFrsOrgMybatisDao.findOrgThen(map);
						}
					}else {
						list = this.rptMgrFrsOrgMybatisDao.findOrgThen(map);
					}
					list = getNotFree(list, state);
				}
				for (int i = 0; i < list.size(); i++) {
					CommonTreeNode treeNode = new CommonTreeNode();
					if (list.get(i).getId().getOrgType().equals(orgType)) {
						treeNode.setIcon(path + "/"
								+ GlobalConstants4frame.ICON_URL
								+ "/application_view_detail.png");
						treeNode.setId(list.get(i).getId().getOrgNo());
						treeNode.setText(list.get(i).getOrgNm() + "("
								+ list.get(i).getId().getOrgNo() + ")");// 1.添加机构号
						treeNode.setUpId(list.get(i).getUpOrgNo());
						Map<String, String> params = new HashMap<String, String>();
						params.put("orgNo", list.get(i).getId().getOrgNo());
						params.put("upOrgNo", list.get(i).getUpOrgNo());
						params.put("orgNm", list.get(i).getOrgNm());
						params.put("orgType", orgType);
						params.put("mgrOrgNo", list.get(i).getMgrOrgNo());
						params.put("type", "org");
						params.put("isVirtualOrg", list.get(i)
								.getIsVirtualOrg());
						treeNode.setParams(params);
						treeNode.setData(list.get(i));
						treeNode.setIsParent(true);
						node.add(treeNode);
					}
				}
			} else {// 加权限过滤
				if (orgNo.equals("0")) { // 根节点 相当于总行节点
					List<String> innerOrgNos = BioneSecurityUtils
							.getCurrentUserInfo().getAuthObjMap()
							.get("AUTH_OBJ_ORG");
					Map<String, Object> mapMgr = new HashMap<String, Object>();
					mapMgr.put("innerOrgNos", SplitStringBy1000.change(innerOrgNos));
					mapMgr.put("orgType", orgType);
					List<String> orgNoUser = this.rptMgrFrsOrgMybatisDao
							.getOrgNoUser(mapMgr); // 查询机构orgNo

					// 查询上级节点的函数
					Map<String, Object> mapUpNo = new HashMap<String, Object>();
					mapUpNo.put("orgType", orgType);
					mapUpNo.put("innerOrgNos", SplitStringBy1000.change(innerOrgNos));
					List<RptOrgInfo> userUpNo = this.rptMgrFrsOrgMybatisDao
							.findOrg(mapUpNo); // 查询全部机构
					Map<String, RptOrgInfo> map = new HashMap<String, RptOrgInfo>();
					for (RptOrgInfo org : userUpNo) {
						map.put(org.getId().getOrgNo(), org);
					}
					Map<String, String> MapNo = new HashMap<String, String>(); // 放入路径的Map

					for (int i = 0; i < orgNoUser.size(); i++) {
						String orgNos = ""; // 路径
						RptOrgInfo org = map.get(orgNoUser.get(i));
						orgNos += org.getId().getOrgNo();
						while (map.get(org.getUpOrgNo()) != null) {
							org = map.get(org.getUpOrgNo()); // 获取用户所在机构的上级机构
							orgNos += "," + org.getId().getOrgNo();
						}
						if (!org.getUpOrgNo().equals(" ")) {
							MapNo.put(orgNoUser.get(i), orgNos + ",");
						}
					}
					List<String> listNos = new ArrayList<String>();
					List<String> clistNos = new ArrayList<String>();
					for (String qorgNo : MapNo.keySet()) {
						clistNos.clear();
						if (listNos.size() > 0) {
							boolean flag = true;
							for (String corgNo : listNos) {
								if (MapNo.get(corgNo)
										.indexOf(MapNo.get(qorgNo)) >= 0) {
									clistNos.add(corgNo);
								}
								if (MapNo.get(qorgNo)
										.indexOf(MapNo.get(corgNo)) >= 0) {
									flag = false;
									break;
								}
							}
							if (flag) {
								listNos.add(qorgNo);
							}
							if (clistNos.size() > 0) {
								for (String corg : clistNos) {
									listNos.remove(corg);
								}
							}
						} else {
							listNos.add(qorgNo);
						}
					}
					if (innerOrgNos != null) {
						Map<String, Object> mapNo = new HashMap<String, Object>();
						mapNo.put("orgNo", ReBuildParam.toDbList(listNos));
						mapNo.put("orgType", orgType);
						List<RptOrgInfo> list = Lists.newArrayList();
						// 游离机构
						Map<String, Object> freeMap = new HashMap<String, Object>();
						freeMap.put("orgType", orgType);
						freeMap.put("upOrgNo",
								GlobalConstants4plugin.TREE_FREE_ROOT_NO);
						List<RptOrgInfo> freeList = this.rptMgrFrsOrgMybatisDao
								.findFreeOrg(freeMap);
						if (freeList != null && freeList.size() != 0) {
							if (/*
								 * userMgrNo.equals(GlobalConstants4frame.
								 * TREE_TOTAL_NO) &&
								 */state != null) {
								CommonTreeNode freeNode = new CommonTreeNode();
								freeNode.setId(GlobalConstants4plugin.TREE_FREE_ROOT_NO);
								freeNode.setText("游离机构");
								freeNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
								Map<String, String> params = new HashMap<String, String>();
								params.put(
										"orgNo",
										GlobalConstants4plugin.TREE_FREE_ROOT_NO);
								params.put("upOrgNo",
										GlobalConstants4frame.TREE_ROOT_NO);
								params.put("orgNm", "游离机构");
								params.put("orgType", orgType);
								params.put("type", "org");
								freeNode.setIcon(path + "/"
										+ GlobalConstants4frame.ICON_URL
										+ "/application_view_detail.png");
								freeNode.setParams(params);
								freeNode.setIsParent(true);
								node.add(freeNode);
							}
						}
						if (listNos != null && listNos.size() > 0) {
							if (StringUtils.isNotBlank(state)) {
								if (state.equals("1")) {
									list = this.rptMgrFrsOrgMybatisDao
											.findOrgUser(mapNo);
								} else {
									if(paramValues != null && paramValues.size() > 0){
										if("Y".equals(paramValues.get(0))){
											list = this.rptMgrFrsOrgMybatisDao.findOrgUserThenBySumRel(mapNo);
										}else{
											list = this.rptMgrFrsOrgMybatisDao.findOrgUserThen(mapNo);
										}
									}else {
										list = this.rptMgrFrsOrgMybatisDao.findOrgUserThen(mapNo);
									}
									list = getNotFree(list, state);
								}
							} else {
								if(paramValues != null && paramValues.size() > 0){
									if("Y".equals(paramValues.get(0))){
										list = this.rptMgrFrsOrgMybatisDao.findOrgUserThenBySumRel(mapNo);
									}else{
										list = this.rptMgrFrsOrgMybatisDao.findOrgUserThen(mapNo);
									}
								}else {
									list = this.rptMgrFrsOrgMybatisDao.findOrgUserThen(mapNo);
								}
								list = getNotFree(list, state);
							}
						} else {
							list = Lists.newArrayList();
						}
						for (int i = 0; i < list.size(); i++) {
							if(!listNos.contains(list.get(i).getUpOrgNo())){//去重
								CommonTreeNode treeNode = new CommonTreeNode();
								if (list.get(i).getId().getOrgType()
										.equals(orgType)) {
									treeNode.setIcon(path + "/"
											+ GlobalConstants4frame.ICON_URL
											+ "/application_view_detail.png");
									treeNode.setId(list.get(i).getId().getOrgNo());
									treeNode.setText(list.get(i).getOrgNm() + "("
											+ list.get(i).getId().getOrgNo() + ")");// 2.添加机构号
																					// 孔渊博
																					// 20170306
									treeNode.setUpId(list.get(i).getUpOrgNo());
									Map<String, String> params = new HashMap<String, String>();
									params.put("orgNo", list.get(i).getId()
											.getOrgNo());
									params.put("upOrgNo", list.get(i).getUpOrgNo());
									params.put("orgNm", list.get(i).getOrgNm());
									params.put("orgType", orgType);
									params.put("mgrOrgNo", list.get(i)
											.getMgrOrgNo());
									params.put("isVirtualOrg", list.get(i)
											.getIsVirtualOrg());
									params.put("type", "org");
									treeNode.setData(list.get(i));
									treeNode.setParams(params);
									treeNode.setIsParent(true);
									node.add(treeNode);
								}
							}
						}
					}
				} else {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("upOrgNo", orgNo);
					map.put("orgType", orgType);
					List<RptOrgInfo> list = Lists.newArrayList();
					// if(orgType.equals("03")){
					// map.put("orgSumType", "02");
					// }
					if (StringUtils.isNotBlank(state)) {
						if (state.equals("1")) {
							list = this.rptMgrFrsOrgMybatisDao.findOrg(map);
						} else {
							if(paramValues != null && paramValues.size() > 0){
								if("Y".equals(paramValues.get(0))){
									list = this.rptMgrFrsOrgMybatisDao.findOrgThenBySumRel(map);
								}else{
									list = this.rptMgrFrsOrgMybatisDao.findOrgThen(map);
								}
							}else {
								list = this.rptMgrFrsOrgMybatisDao.findOrgThen(map);
							}
						}
					} else {
						if(paramValues != null && paramValues.size() > 0){
							if("Y".equals(paramValues.get(0))){
								list = this.rptMgrFrsOrgMybatisDao.findOrgThenBySumRel(map);
							}else{
								list = this.rptMgrFrsOrgMybatisDao.findOrgThen(map);
							}
						}else {
							list = this.rptMgrFrsOrgMybatisDao.findOrgThen(map);
						}
					}
					for (int i = 0; i < list.size(); i++) {
						CommonTreeNode treeNode = new CommonTreeNode();
						if (list.get(i).getId().getOrgType().equals(orgType)) {
							treeNode.setIcon(path + "/"
									+ GlobalConstants4frame.ICON_URL
									+ "/application_view_detail.png");
							treeNode.setId(list.get(i).getId().getOrgNo());
							treeNode.setText(list.get(i).getOrgNm() + "("
									+ list.get(i).getId().getOrgNo() + ")");// 3.添加机构编码
																			// 孔渊博
																			// 20170306
							treeNode.setUpId(list.get(i).getUpOrgNo());
							Map<String, String> params = new HashMap<String, String>();
							params.put("orgNo", list.get(i).getId().getOrgNo());
							params.put("upOrgNo", list.get(i).getUpOrgNo());
							params.put("orgNm", list.get(i).getOrgNm());
							params.put("orgType", orgType);
							params.put("mgrOrgNo", list.get(i).getMgrOrgNo());
							params.put("isVirtualOrg", list.get(i)
									.getIsVirtualOrg());
							params.put("type", "org");
							treeNode.setData(list.get(i));
							treeNode.setParams(params);
							treeNode.setIsParent(true);
							node.add(treeNode);
						}
					}
				}
			}
		}
		return node;
	}

	// 去掉游离机构
	public List<RptOrgInfo> getNotFree(List<RptOrgInfo> list, String state) {
		if (state == null) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getUpOrgNo().equals(" ")) {
					list.remove(i);
				}
			}
		}
		return list;
	}

	// 将查询出来的机构放入缓存

	// 点击后显示信息
	public RptMgrFrsOrgVo showAll(String orgNo, String orgType, String upOrgNo,
			String mgrOrgNo) {
		// 查询本级机构的基本信息Map
		Map<String, Object> map = new HashMap<String, Object>();
		RptMgrFrsOrgVo vo = new RptMgrFrsOrgVo();
		map.put("orgNo", orgNo);
		map.put("orgType", orgType);
		map.put("upOrgNo", upOrgNo);
		// 查询管理机构的名称Map
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("orgNo", mgrOrgNo);
		String name = this.rptMgrFrsOrgMybatisDao.findMgrNms(maps);
		RptMgrFrsOrgVo v = this.rptMgrFrsOrgMybatisDao.findOrgNm(map);
		vo.setFinanceOrgNo(v.getFinanceOrgNo());
		vo.setId(v.getId());
		vo.setMgrOrgNm(name);
		vo.setMgrOrgNo(v.getMgrOrgNo());
		vo.setOrgNm(v.getOrgNm());
		vo.setOrgSumType(v.getOrgSumType());
		vo.setUpOrgNo(v.getUpOrgNo());
		return vo;
	}

	// 重复
	public boolean getOrgNm(String orgType, String orgNm, String sourceOrgNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(sourceOrgNo)) {
			map.put("sourceOrgNo", sourceOrgNo);
		}
		map.put("orgType", orgType);
		map.put("orgNm", orgNm);
		RptOrgInfo org = this.rptMgrFrsOrgMybatisDao.getOrg(map);
		if (org != null) {
			return false;
		} else {
			return true;
		}

	}

	// 重复
	public boolean getOrgNo(String orgType, String orgNo, String sourceOrgNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(sourceOrgNo)) {
			map.put("sourceOrgNo", sourceOrgNo);
		}
		map.put("orgType", orgType);
		map.put("orgNo", orgNo);
		RptOrgInfo org = this.rptMgrFrsOrgMybatisDao.getOrg(map);
		if (org != null) {
			return false;
		} else {
			return true;
		}

	}

	// 重复
	public boolean getFinanceOrgNo(String orgType, String financeOrgNo,
			String sourceOrgNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		if (!StringUtils.isEmpty(sourceOrgNo)) {
			map.put("sourceOrgNo", sourceOrgNo);
		}
		map.put("financeOrgNo", financeOrgNo);
		RptOrgInfo org = this.rptMgrFrsOrgMybatisDao.getOrg(map);
		if (org != null) {
			return false;
		} else {
			return true;
		}
	}

	public void addOrg(RptMgrFrsOrgVo vo) {
		String orgNo = vo.getUpOrgNo();
		String orgType = vo.getId().getOrgType();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgNo", orgNo);
		map.put("orgType", orgType);
		RptFimAddrInfo addInfo = new RptFimAddrInfo();
		// add by lxp 2017 0609 新增插入rpt_fim_addr_info
		addInfo.setAddrNo(vo.getRhOrgNo());
		addInfo.setAddrNm(vo.getRhOrgNm());
		addInfo.setUpAddrNo(vo.getUpOrgNo());
		addInfo.setDtrctNo(vo.getDtrctNo());
		addInfo.setOrgNm(vo.getOrgNm());
		addInfo.setOrgNo(vo.getId().getOrgNo());
		if (CommonTreeNode.ROOT_ID.equals(orgNo)) {// 根节点
			vo.setNamespace("/" + vo.getId().getOrgNo() + "/");
			// add by lxp 2017 0609 新增插入rpt_fim_addr_info
			addInfo.setNamespace(vo.getNamespace());
		} else {
			List<RptOrgInfo> list = this.rptMgrFrsOrgMybatisDao.findOrg(map);
			if (list != null && list.size() != 0) {
				String namespace = list.get(0).getNamespace()
						+ vo.getId().getOrgNo() + "/";
				vo.setNamespace(namespace);
				// add by lxp 2017 0609 新增插入rpt_fim_addr_info
				addInfo.setNamespace(vo.getNamespace());
			}
		}
		if((GlobalConstants4plugin.RPT_FRS_BUSI_PBC).equals(vo.getId().getOrgType())) {//大集中才保存
			this.rptMgrFrsOrgMybatisDao.saveAddrOrgInfo(addInfo);
		}
		this.rptMgrFrsOrgMybatisDao.saveOrg(vo);

	}

	public void deleteOrg(String orgType, String orgNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		RptOrgInfoPK pk = new RptOrgInfoPK();
		pk.setOrgNo(orgNo);
		pk.setOrgType(orgType);
		map.put("id", pk);
		this.rptMgrFrsOrgMybatisDao.deleteArrOrg(map);// edit by lxp 0609
		this.rptMgrFrsOrgMybatisDao.deleteOrg(map);
	}

	public List<RptOrgInfo> getListOrg(String orgType, String orgNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgType", orgType);
		map.put("upOrgNo", orgNo);
		List<RptOrgInfo> list = this.rptMgrFrsOrgMybatisDao.findOrg(map);
		return list;
	}

	public String getBandTask(String orgType, String orgNo) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgType", orgType);
		map.put("orgNo", orgNo);
		List<String> list = rptMgrFrsOrgMybatisDao.checkBandTask(map);
		if (list == null || list.isEmpty())
			return null;
		boolean isFirst = true;
		StringBuilder buff = new StringBuilder();
		for (String taskNm : list) {
			if (isFirst)
				isFirst = false;
			else
				buff.append(",");

			buff.append(taskNm);
		}
		return buff.toString();
	}

	public RptMgrFrsOrgVo getOrgInfo(String orgNo, String orgType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgNo", orgNo);
		map.put("orgType", orgType);
		RptMgrFrsOrgVo org = null;
		org = this.rptMgrFrsOrgMybatisDao.getOrg(map);
		RptOrgInfoPK pk = new RptOrgInfoPK();
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("orgType", orgType);
		if (StringUtils.isEmpty(org.getMgrOrgNo())) {
			org.setMgrOrgNo(orgNo);
		}
		maps.put("orgNo", org.getMgrOrgNo());
		String name = this.rptMgrFrsOrgMybatisDao.findMgrNms(maps);
		pk.setOrgNo(orgNo);
		pk.setOrgType(orgType);
		org.setMgrOrgNm(name);
		org.setId(pk);
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("orgNo", org.getUpOrgNo());
		condition.put("orgType", orgType);
		List<RptOrgInfo> list = this.rptMgrFrsOrgMybatisDao.findOrg(condition);
		if (list == null || list.size() == 0) {
			condition.clear();
			condition
					.put("paramTypeNo", GlobalConstants4plugin.TREE_PARAM_TYPE);
			condition.put("paramValue", orgType);
			List<BioneParamInfo> param = this.rptParamMybatisDao
					.paramInfolist(condition);
			if (param != null && param.size() == 1) {
				org.setUpOrgNm(param.get(0).getParamName());
			}
		}
		if (list != null && list.size() == 1) {
			org.setUpOrgNm(list.get(0).getOrgNm());
		}
		return org;
	}

	public RptMgrFrsOrgVo getOrgInfo1(String orgNo, String orgType, String flag) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orgNo", orgNo);
		map.put("orgType", orgType);
		RptMgrFrsOrgVo org = null;
		//先查询出单个机构的机构信息，因为有大集中地区配置所以需要关联一下地区表
		if (orgType.equals("03")) {// 人行大集中调
			org = this.rptMgrFrsOrgMybatisDao.getOrgAndAddr(map);
		} else {
			org = this.rptMgrFrsOrgMybatisDao.getOrg(map);
		}
		//查询不出机构直接返回数据
		if(null == org) {
			return org;
		}
		RptOrgInfoPK pk = new RptOrgInfoPK();
		pk.setOrgNo(orgNo);
		pk.setOrgType(orgType);
		org.setId(pk);
		//再查出对应行内机构机构名称
		Map<String, Object> maps = new HashMap<String, Object>();
		maps.put("orgType", orgType);
		maps.put("orgNo", org.getMgrOrgNo());
		String name = this.rptMgrFrsOrgMybatisDao.findMgrNms(maps);
		org.setMgrOrgNm(name);
		//查出上级机构机构名称
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("orgNo", org.getUpOrgNo());
		condition.put("orgType", orgType);
		List<RptOrgInfo> list = this.rptMgrFrsOrgMybatisDao.findOrg(condition);
		if (list == null || list.size() == 0) {
			//如果上级机构为空，可能是最顶层机构需要查询一下参数信息作为上级节点
			condition.clear();
			condition.put("paramTypeNo", GlobalConstants4plugin.TREE_PARAM_TYPE);
			condition.put("paramValue", orgType);
			List<BioneParamInfo> param = this.rptParamMybatisDao.paramInfolist(condition);
			if (param != null && param.size() == 1) {
				org.setUpOrgNm(param.get(0).getParamName());
			}
		}else {
			org.setUpOrgNm(list.get(0).getOrgNm());
		}
		return org;
	}

	@Transactional(readOnly = false)
	public void updateOrg(RptMgrFrsOrgVo vo, String orgNo, String sourceOrgNo) {
		RptMgrFrsOrgSource source = new RptMgrFrsOrgSource();
		source.setFinanceOrgNo(vo.getFinanceOrgNo());
		String type = vo.getId().getOrgType();
		RptOrgInfoPK pk = new RptOrgInfoPK();
		pk.setOrgNo(orgNo);
		pk.setOrgType(type);
		source.setId(pk);
		source.setMgrOrgNo(vo.getMgrOrgNo());
		source.setOrgNm(vo.getOrgNm());
		source.setOrgSumType(vo.getOrgSumType());
		source.setUpOrgNo(vo.getUpOrgNo());
		source.setSourceOrgNo(sourceOrgNo);
		source.setOrgClass(vo.getOrgClass());
		source.setIsVirtualOrg(vo.getIsVirtualOrg());
		source.setOrgLevel(vo.getOrgLevel());
		source.setIsOrgReport(vo.getIsOrgReport());
		source.setLeiNo(vo.getLeiNo());
		source.setAddr(vo.getAddr());
		this.rptMgrFrsOrgMybatisDao.updateOrg(source);
		// add by lxp 2017 0608 //修改RPT_FIM_ADDR_INFO
		if((GlobalConstants4plugin.RPT_FRS_BUSI_PBC).equals(vo.getId().getOrgType())) {//大集中才保存
			RptFimAddrInfo addInfo = new RptFimAddrInfo();
			addInfo.setAddrNm(vo.getRhOrgNm());
			addInfo.setAddrNo(vo.getRhOrgNo());// 地区编码
			addInfo.setDtrctNo(vo.getDtrctNo());
			addInfo.setUpAddrNo(vo.getUpOrgNo());
			addInfo.setNamespace(vo.getNamespace());
			addInfo.setOrgNm(vo.getOrgNm());
			addInfo.setOrgNo(orgNo);
			this.saveOrUpdateEntity(addInfo);
		}
	}

	/**
	 * 修改机构时，重新修改了该机构的上级机构
	 * 
	 * @param vo
	 * @param orgNo
	 * @param sourceOrgNo
	 * @param laseUpOrgNo
	 */
	@Transactional(readOnly = false)
	public void updateOrgChangeUpNo(RptMgrFrsOrgVo vo, String orgNo,
			String sourceOrgNo, String laseUpOrgNo) {
		RptOrgInfo newOrg = new RptOrgInfo();
		RptOrgInfoPK pk = new RptOrgInfoPK();
		pk.setOrgNo(orgNo);
		pk.setOrgType(vo.getId().getOrgType());
		newOrg.setFinanceOrgNo(vo.getFinanceOrgNo());
		newOrg.setId(pk);
		newOrg.setMgrOrgNo(vo.getMgrOrgNo());
		newOrg.setOrgNm(vo.getOrgNm());
		newOrg.setOrgSumType(vo.getOrgSumType());
		newOrg.setUpOrgNo(vo.getUpOrgNo());
		newOrg.setIsVirtualOrg(vo.getIsVirtualOrg());
		newOrg.setOrgClass(vo.getOrgClass());
		newOrg.setOrgLevel(vo.getOrgLevel());
		newOrg.setIsOrgReport(vo.getIsOrgReport());
		newOrg.setLeiNo(vo.getLeiNo());
		newOrg.setAddr(vo.getAddr());
		Map<String, Object> condition = new HashMap<String, Object>();
		condition.put("orgNo", vo.getUpOrgNo());
		condition.put("orgType", vo.getId().getOrgType());
		RptOrgInfo upOrg = new RptOrgInfo();
		if (!vo.getUpOrgNo().equals(GlobalConstants4plugin.TREE_FREE_ROOT_NO)) {
			upOrg = this.rptMgrFrsOrgMybatisDao.findOrg(condition).get(0);// 新的上级机构
		}
		condition.clear();
		condition.put("orgNo", "%/" + orgNo + "/%");
		condition.put("orgType", vo.getId().getOrgType());
		List<RptOrgInfo> childOrgList = this.rptMgrFrsOrgMybatisDao.findChildOrgs(condition);
		Map<String, RptOrgInfo> map = new HashMap<String, RptOrgInfo>();
		for (RptOrgInfo tmp : childOrgList) {
			map.put(tmp.getId().getOrgNo(), tmp);
		}
		for (int i = 0; i < childOrgList.size(); i++) {
			RptOrgInfo org = childOrgList.get(i);
			List<String> namespaceList = new ArrayList<String>();
			namespaceList.add(org.getId().getOrgNo());
			while (map.get(org.getUpOrgNo()) != null) {
				org = map.get(org.getUpOrgNo());
				namespaceList.add(org.getId().getOrgNo());
			}
			String namespace = "";
			for (int j = namespaceList.size(); j > 0; j--) {
				namespace += namespaceList.get(j - 1) + "/";
			}
			org = childOrgList.get(i);
			org.setNamespace(upOrg.getNamespace() + namespace);
			if (!org.getId().getOrgNo().equals(newOrg.getId().getOrgNo())) {
				this.rptMgrFrsOrgMybatisDao.updateOrg2(org);
			}
		}
		newOrg.setNamespace(upOrg.getNamespace() + newOrg.getId().getOrgNo()
				+ "/");
		this.rptMgrFrsOrgMybatisDao.updateOrg2(newOrg);
		if((GlobalConstants4plugin.RPT_FRS_BUSI_PBC).equals(vo.getId().getOrgType())) {//大集中才保存
			RptFimAddrInfo addInfo = new RptFimAddrInfo();
			addInfo.setAddrNm(vo.getRhOrgNm());
			addInfo.setAddrNo(vo.getRhOrgNo());// 地区编码
			addInfo.setDtrctNo(vo.getDtrctNo());
			addInfo.setUpAddrNo(vo.getUpOrgNo());
			addInfo.setNamespace(vo.getNamespace());
			addInfo.setOrgNm(vo.getOrgNm());
			addInfo.setOrgNo(orgNo);
			this.saveOrUpdateEntity(addInfo);
		}
	}

	// 获取所有机构信息 created by wangyp
	public List<CommonTreeNode> getSetTree(String orgType) { // 1:机构集 (人行),0:机构组
		List<CommonTreeNode> list = Lists.newArrayList();
		CommonTreeNode treeNode = new CommonTreeNode();
		treeNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
		if (orgType.equals(GlobalConstants4plugin.RPT_FRS_BUSI_PBC)) {
			treeNode.setText("行政区域");
		} else {
			treeNode.setText("机构信息");
		}
		list.add(treeNode);
		this.getAllTree(list, orgType, GlobalConstants4frame.TREE_ROOT_NO,
				treeNode);
		return list;
	}

	// also created by wangyp
	public void getAllTree(List<CommonTreeNode> list, String orgType,
			String upOrgNo, CommonTreeNode treeNode) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("upOrgNo", upOrgNo);
		map.put("orgType", orgType);
		List<RptOrgInfo> lists = this.rptMgrFrsOrgMybatisDao.findOrg(map);
		if (lists.size() > 0) {
			treeNode.setIsParent(true);
			for (int i = 0; i < lists.size(); i++) {
				RptOrgInfo rpt = lists.get(i);
				CommonTreeNode ct = new CommonTreeNode();
				ct.setId(rpt.getId().getOrgNo());
				ct.setUpId(upOrgNo);
				ct.setText(rpt.getOrgNm());
				list.add(ct);
				this.getAllTree(list, rpt.getId().getOrgType(), rpt.getId()
						.getOrgNo(), ct);
			}
		} else {
			treeNode.setIsParent(false);
		}
	}

	// 机构树搜索
	public List<CommonTreeNode> searchOrgTree(String orgType, String orgNm,
			String path, String isOrgReport) {
		boolean flagPBC = true;
		boolean flag1104 = true;
		Map<String, Object> map = new HashMap<String, Object>();
		String orgNms = "%" + orgNm + "%";
		map.put("orgNm", orgNms);
		if (!StringUtils.isEmpty(orgType)) {
			map.put("orgType", orgType);
		}
		map.put("isOrgReport", isOrgReport);
		List<CommonTreeNode> nodes = Lists.newArrayList();
		List<RptOrgInfo> list = Lists.newArrayList();
		boolean isSuper = BioneSecurityUtils.getCurrentUserInfo().isSuperUser();
		if (isSuper == true) { // 超级管理员,不过滤权限
			list = this.rptMgrFrsOrgMybatisDao.searchTree(map);
		} else if (isSuper == false && !StringUtils.isEmpty(orgType)) {
			String userId = BioneSecurityUtils.getCurrentUserId();
			String userOrgNo = BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo();
			List<BioneAuthObjUserRel> rels = this.relBS.getObjUserRelByUserId(
					BioneSecurityUtils.getCurrentUserInfo()
							.getCurrentLogicSysNo(), userId);
			List<String> tmpOrgNos = Lists.newArrayList();
			if (rels != null && rels.size() > 0) {
				for (BioneAuthObjUserRel rel : rels) {
					if (rel.getId().getObjDefNo().equals("AUTH_OBJ_ORG")) {
						tmpOrgNos.add(rel.getId().getObjId());
					}
				}
			}
			// 如果用户授权对象关系中不包括用户所属的机构,则加上
			if (!tmpOrgNos.contains(userOrgNo)) {
				tmpOrgNos.add(userOrgNo);
			}
			// 取得当前用户下的机构及子机构
			List<String> orgNos = getAllOrgNo(tmpOrgNos, orgType);
			map.put("orgNos", ReBuildParam.toDbList(orgNos));
			list = this.rptMgrFrsOrgMybatisDao.searchTree(map);
		}

		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				CommonTreeNode treeNode = new CommonTreeNode();
				if (list.get(i).getId().getOrgType()
						.equals(GlobalConstants4plugin.RPT_FRS_BUSI_PBC)) { // 人行大集中
					if (flagPBC) {
						nodes.add(generateRootNode(
								GlobalConstants4plugin.RPT_FRS_BUSI_PBC, path)); // 生成根节点
						flagPBC = false;
					}
					treeNode.setUpId("RH" + GlobalConstants4frame.TREE_ROOT_NO);
				} else if (list.get(i).getId().getOrgType()
						.equals(GlobalConstants4plugin.RPT_FRS_BUSI_1104)) { // 1104
					if (flag1104) {
						nodes.add(generateRootNode(
								GlobalConstants4plugin.RPT_FRS_BUSI_1104, path));
						flag1104 = false;
					}
					treeNode.setUpId("JG" + GlobalConstants4frame.TREE_ROOT_NO);
				}
				treeNode.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
						+ "/application_view_detail.png");
				treeNode.setId(list.get(i).getId().getOrgNo());
				// 查询结果显示名称后方添加机构号
				treeNode.setText(list.get(i).getOrgNm() + "("
						+ list.get(i).getId().getOrgNo() + ")");
				// treeNode.setUpId(list.get(i).getUpOrgNo());
				Map<String, String> params = new HashMap<String, String>();
				params.put("orgNo", list.get(i).getId().getOrgNo());
				params.put("upOrgNo", list.get(i).getUpOrgNo());
				params.put("orgNm", list.get(i).getOrgNm());
				params.put("orgType", list.get(i).getId().getOrgType());
				treeNode.setParams(params);
				treeNode.setData(list.get(i));
				treeNode.setIsParent(true);
				nodes.add(treeNode);
			}
		}
		return nodes;
	}

	// 获取机构及子机构
	private List<String> getAllOrgNo(List<String> tmpOrgNos, String type) {
		List<String> orgNos = Lists.newArrayList();
		List<String> childOrgNos = Lists.newArrayList();
		if (tmpOrgNos != null && tmpOrgNos.size() > 0) {
			orgNos.addAll(tmpOrgNos);
			childOrgNos.addAll(tmpOrgNos);
		}
		getChildOrg(orgNos, childOrgNos, type);
		return orgNos;
	}

	// 递归获取子机构
	private void getChildOrg(List<String> orgNos, List<String> childOrgNos,
			String type) {
		String jql = "select o.id.orgNo from RptOrgInfo o where o.upOrgNo in ?0 and o.id.orgType = ?1";
		List<String> listOrgNos = this.baseDAO.findWithIndexParam(jql,
				childOrgNos, type);
		if (listOrgNos != null && listOrgNos.size() > 0) {
			orgNos.addAll(listOrgNos);
			getChildOrg(orgNos, listOrgNos, type);
		}
	}

	// 生成根节点
	private CommonTreeNode generateRootNode(String orgType, String path) {
		CommonTreeNode treeNode = new CommonTreeNode();
		Map<String, String> params = new HashMap<String, String>();
		if (orgType.equals(GlobalConstants4plugin.RPT_FRS_BUSI_1104)) {
			treeNode.setText("1104");
			treeNode.setId("JG" + GlobalConstants4frame.TREE_ROOT_NO);
		} else if (orgType.equals(GlobalConstants4plugin.RPT_FRS_BUSI_PBC)) {
			treeNode.setText("人行");
			treeNode.setId("RH" + GlobalConstants4frame.TREE_ROOT_NO);
		}
		treeNode.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
				+ "/house.png");
		treeNode.setIsParent(true);
		params.put("orgType", orgType);
		params.put("orgNo", GlobalConstants4frame.TREE_ROOT_NO);
		treeNode.setParams(params);
		return treeNode;
	}

	public List<CommonTreeNode> getOrgTree(String id, String path) {
		Map<String, Object> condition = new HashMap<String, Object>();
		String userMgrNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		Map<String, Object> mapMgr = new HashMap<String, Object>();
		List<String> orgNoUser = Lists.newArrayList();
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			// 权限过滤
			mapMgr.put("mgrOrgNo", userMgrNo);
			orgNoUser = this.rptMgrFrsOrgMybatisDao.getOrgNoUser(mapMgr);
		} else {
			orgNoUser.add(CommonTreeNode.ROOT_ID); // 为了过下面的If语句 纯属无用
			id = (id == null || id.equals("")) ? CommonTreeNode.ROOT_ID : id;
		}
		List<CommonTreeNode> result = new ArrayList<CommonTreeNode>();
		if (orgNoUser != null && orgNoUser.size() > 0) {
			// 生成根节点
			if (id == null || id.equals("")) {
				condition.put("orgNo", ReBuildParam.toDbList(orgNoUser));
				List<BioneOrgInfo> list = this.rptMgrFrsOrgMybatisDao
						.list(condition);
				for (BioneOrgInfo org : list) {
					CommonTreeNode node = new CommonTreeNode();
					node.setId(org.getOrgNo());
					node.setIsParent(true);
					node.setText(org.getOrgName());
					node.setUpId(org.getUpNo());
					node.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
							+ "/application_view_detail.png");
					result.add(node);
				}
			} else {
				List<String> idList = new ArrayList<String>();
				idList.add(id);
				condition.put("upNo", ReBuildParam.toDbList(idList));
				List<BioneOrgInfo> list = this.rptMgrFrsOrgMybatisDao
						.list(condition);
				for (BioneOrgInfo org : list) {
					CommonTreeNode node = new CommonTreeNode();
					node.setId(org.getOrgNo());
					node.setIsParent(true);
					node.setText(org.getOrgName());
					node.setUpId(org.getUpNo());
					node.setIcon(path + "/" + GlobalConstants4frame.ICON_URL
							+ "/application_view_detail.png");
					result.add(node);
				}
			}
		}
		return result;
	}

	/**
	 * 获取所管辖机构的最顶层机构
	 * 
	 * @param mgrOrgNo
	 * @param orgType
	 * @return
	 */
	public List<String> getManageOrg(String mgrOrgNo, String orgType) {
		List<String> orgNos = new ArrayList<String>();
		List<RptOrgInfo> mgrorgNos = new ArrayList<RptOrgInfo>();
		Map<String, Object> mapMgr = new HashMap<String, Object>();
		mapMgr.put("mgrOrgNo", mgrOrgNo);
		mapMgr.put("orgType", orgType);
		// 获取某个机构的所管辖机构
		List<RptOrgInfo> orgs = this.rptMgrFrsOrgMybatisDao
				.getOrgNoInfoUser(mapMgr); // 查询机构orgNo
		if (orgs != null && orgs.size() > 0) {
			if (orgs.size() == 1) {
				orgNos.add(orgs.get(0).getId().getOrgNo());
			} else {
				mgrorgNos.addAll(orgs);
				for (RptOrgInfo org : orgs) {
					List<RptOrgInfo> deletOrg = new ArrayList<RptOrgInfo>();
					for (RptOrgInfo morg : mgrorgNos) {
						// morg存在下级
						if (org.getNamespace().indexOf(morg.getNamespace()) >= 0) {
							break;
						}
						// morg存在上级
						if (morg.getNamespace().indexOf(org.getNamespace()) >= 0) {
							deletOrg.add(morg);
						}
					}
					if (deletOrg.size() > 0) {
						for (RptOrgInfo dorg : deletOrg) {
							mgrorgNos.remove(dorg);
						}
					}
				}
				for (RptOrgInfo morg : mgrorgNos) {
					orgNos.add(morg.getId().getOrgNo());
				}
			}
		}
		return orgNos;
	}

	/**
	 * 获取要导出的机构
	 * 
	 * @return
	 */
	public List<OrgImportVO> getImportOrg(Map<String, Object> map) {
		List<OrgImportVO> vos = Lists.newArrayList();
		List<RptOrgInfo> frsOrgs = this.rptMgrFrsOrgMybatisDao
				.getFrsOrgByNamespace(map);
		if (frsOrgs != null && frsOrgs.size() > 0) {
			for (RptOrgInfo rptMgrFrsOrg : frsOrgs) {
				OrgImportVO vo = new OrgImportVO();
				vo.setOrgNo(rptMgrFrsOrg.getId().getOrgNo());
				vo.setOrgNm(rptMgrFrsOrg.getOrgNm());
				vo.setOrgType(rptMgrFrsOrg.getId().getOrgType());
				vo.setUpOrgNo(rptMgrFrsOrg.getUpOrgNo());
				vo.setMgrOrgNo(rptMgrFrsOrg.getMgrOrgNo());
				vo.setFinanceOrgNo(rptMgrFrsOrg.getFinanceOrgNo());
				vo.setOrgSumType(rptMgrFrsOrg.getOrgSumType());
				vo.setNamespace(rptMgrFrsOrg.getNamespace());
				vo.setIsVirtualOrg(rptMgrFrsOrg.getIsVirtualOrg());
				vos.add(vo);
			}
		}
		return vos;
	}

	public Map<String, String> findOrgInfo(String orgType) {
		Map<String, String> orgMap = new HashMap<String, String>();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("orgType", orgType);
		List<RptOrgInfo> orgs = this.rptMgrFrsOrgMybatisDao.findOrg(params);
		if (orgs != null && orgs.size() > 0) {
			for (RptOrgInfo org : orgs) {
				orgMap.put(org.getId().getOrgNo(), org.getOrgNm());
			}
		}
		return orgMap;
	}

	/**
	 * 根据条件获取级联机构
	 * 
	 * @param map
	 * @return
	 */
	public List<String> getConOrg(Map<String, Object> map) {
		return this.rptMgrFrsOrgMybatisDao.getConOrg(map);
	}

	/**
	 * 判断是否顶层机构
	 * 
	 * @param orgNo
	 * @return
	 */
	public boolean isMaxOrg(String orgNo) {
		List<RptOrgInfo> orgs = this.rptMgrFrsOrgMybatisDao.isMaxOrg(orgNo);
		if (orgs != null && orgs.size() > 0) {
			RptOrgInfo org = orgs.get(0);
			if ("0".equals(org.getUpOrgNo())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取本机机构
	 * 
	 * @param map
	 * @return
	 */
	public List<RptOrgInfo> getOrgNoInfoUser(Map<String, Object> map) {
		return this.rptMgrFrsOrgMybatisDao.getOrgNoInfoUser(map);
	}

	public Map<String, String> getOrgMap(String rptType) {
		Map<String, String> orgMap = new HashMap<String, String>();
		List<RptOrgInfo> orgs = this.getEntityListByProperty(RptOrgInfo.class,
				"id.orgType", rptType);
		if (orgs != null && orgs.size() > 0) {
			for (RptOrgInfo org : orgs) {
				orgMap.put(org.getId().getOrgNo(), org.getOrgNm());
			}
		}
		return orgMap;
	}

	/**
	 * 根据机构类型和机构号获取机构名城
	 * 
	 * @param parme
	 * @return
	 */
	public List<String> getConNmOrg(Map<String, Object> parme) {
		return this.rptMgrFrsOrgMybatisDao.getConNmOrg(parme);
	}

	public List<String> getAllChildOrgNos(String orgNo, String type,
			boolean isOwner) {
		List<String> allOrgNos = new ArrayList<String>();
		List<String> upOrgNos = new ArrayList<String>();
		if (isOwner) {
			allOrgNos.add(orgNo);
			upOrgNos.add(orgNo);
		}
		this.getChildOrgNos(allOrgNos, upOrgNos, type);
		return allOrgNos;
	}

	private void getChildOrgNos(List<String> allOrgNos, List<String> orgNos,
			String type) {
		if (orgNos != null && orgNos.size() > 0) {
			List<List<String>> orgNoss = SplitStringBy1000.change(orgNos);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("orgType", type);
			String jql = "select org.id.orgNo from RptOrgInfo org where org.id.orgType = :orgType";
			jql += " and (";
			for (int i = 0; i < orgNoss.size(); i++) {
				jql += "org.upOrgNo in (:orgNo" + i + ")";
				if (i < orgNoss.size() - 1) {
					jql += " or ";
				} else {
					jql += " ) ";
				}
				params.put("orgNo" + i, orgNoss.get(i));
			}
			List<String> uporgNos = this.baseDAO.findWithNameParm(jql, params);
			if (uporgNos != null && uporgNos.size() > 0) {
				allOrgNos.addAll(uporgNos);
				getChildOrgNos(allOrgNos, uporgNos, type);
			}
		}
	}
	
	/**
	 * 获取全部监管机构类型
	 * @return
	 */
	public List<CommonComboBoxNode> getRptClassList(Boolean isInclude, List<String> rptClasslist) {
		List<CommonComboBoxNode> nodes = Lists.newArrayList();
		String jql = "select param from BioneParamInfo param where param.paramTypeNo = ?0 and param.logicSysNo = ?1";
		List<BioneParamInfo> params = new ArrayList<BioneParamInfo>();
		if(rptClasslist != null && rptClasslist.size() > 0) {
			if(true == isInclude) {
				jql += " and param.paramValue in ?2"; 
			}else{
				jql += " and param.paramValue not in ?2"; 
			}
			params = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.RPT_ORG_CALSS, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo(), rptClasslist);
		}else {
			params = this.baseDAO.findWithIndexParam(jql, GlobalConstants4plugin.RPT_ORG_CALSS, BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
		}
		
		if(params != null && params.size() > 0){
			for(BioneParamInfo param : params){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(param.getParamValue());
				node.setText(param.getParamName());
				nodes.add(node);
			}
		}
		return nodes;
	}

}
