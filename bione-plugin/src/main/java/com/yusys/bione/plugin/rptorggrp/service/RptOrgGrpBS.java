package com.yusys.bione.plugin.rptorggrp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageHelper;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import com.yusys.bione.plugin.rptorggrp.entity.RptGrpAuthRel;
import com.yusys.bione.plugin.rptorggrp.entity.RptGrpOrgRel;
import com.yusys.bione.plugin.rptorggrp.entity.RptGrpOrgRelPK;
import com.yusys.bione.plugin.rptorggrp.entity.RptOrgGrp;
import com.yusys.bione.plugin.rptorggrp.repository.RptOrgGrpMybatisDao;
import com.yusys.bione.plugin.rptorggrp.web.vo.RptGrpVO;
import com.yusys.bione.plugin.rptorggrp.web.vo.RptOrgGrpVO;

@Service
@Transactional(readOnly = true)
public class RptOrgGrpBS extends BaseBS<Object>{

	@Autowired
	private RptOrgGrpMybatisDao rptOrgGrpMybatisDao;

	@Autowired
	private RptOrgInfoBS orgInfoBS;
	
	
	public PageMyBatis<RptGrpVO> getColInfo(Pager pager) {
		PageHelper.startPage(pager);
		Map<String, Object> params = Maps.newHashMap();
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			// 增加归属机构过滤
			String orgNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
			List<RptOrgInfo> orgInfos = orgInfoBS.findOrgChild(orgNo, GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC);
			List<String> createOrgs = new ArrayList<String>();
			if(orgInfos != null && orgInfos.size()>0){
				for(RptOrgInfo orgInfo : orgInfos){
					createOrgs.add(orgInfo.getId().getOrgNo());
				}
			}
			if(null != createOrgs && createOrgs.size() > 0){
				params.put("createOrgs", ReBuildParam.splitLists(createOrgs));
			}
			params.put("orgNo", orgNo);
		}
		PageMyBatis<RptGrpVO> list = (PageMyBatis<RptGrpVO>) rptOrgGrpMybatisDao.getCol(params);
		return list;
	}
	
	// 新建机构组(集)
	@Transactional(readOnly = false)
	public void saveGroup(RptOrgGrp rpt,RptGrpAuthRel rel) {
		this.saveOrUpdateEntity(rpt);
		this.saveOrUpdateEntity(rel);
	}
	
	// 修改机构组(集)
	@Transactional(readOnly = false)
	public void editGroup(RptOrgGrp rpt) {
		rptOrgGrpMybatisDao.editGroup(rpt);
	}
	
	// 删除机构组(集)
	@Transactional(readOnly = false)
	public Boolean delOrg(String groupId) {
		boolean flag = true;
		String[] ids = StringUtils.split(groupId, ",");
		List<String> list = Lists.newArrayList();
		for (String id : ids) {
			String jql = "select rel from RptGrpAuthRel rel where rel.id.grpId = ?0 and rel.id.orgNo != ?1";
			List<RptGrpAuthRel> rels = this.baseDAO.findWithIndexParam(jql, id,BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			if(rels.size()<=0){
				list.add(id);
			}
			else{
				flag = false;
			}
			
		}
		HashMap<String, Object> params = Maps.newHashMap();
		params.put("list", list);
		if(list.size()>0){
			rptOrgGrpMybatisDao.delOrg(params);
			rptOrgGrpMybatisDao.delObjs(params);
		}
		return flag;
	}
	
	@Transactional(readOnly = false)
	public void delRel(String grpId,String orgNo){
		String jql = "delete from RptGrpAuthRel rel where rel.id.orgNo = ?0 and rel.id.grpId = ?1";
		this.baseDAO.batchExecuteWithIndexParam(jql, orgNo,grpId);
	}
	

	public RptOrgGrp getRptById(String groupId) {
		return rptOrgGrpMybatisDao.getRptById(groupId);
	}

	public void saveorgobj(String grpId, String ids, String nodeType) {
		rptOrgGrpMybatisDao.delObj(grpId);
		if (!("".equals(ids) || ids == null)) {
			String[] objids = StringUtils.split(ids, ',');
			for (String objid : objids) {
				RptGrpOrgRelPK objpk = new RptGrpOrgRelPK();
				RptGrpOrgRel rptobj = new RptGrpOrgRel();
				objpk.setGrpId(grpId);
				objpk.setOrgNo(objid);
				objpk.setOrgType(nodeType);
				rptobj.setId(objpk);
				rptOrgGrpMybatisDao.saveObj(rptobj);
			}
		}
	}

	public ArrayList<String> getOrgList(String groupId) {
		ArrayList<String> list = rptOrgGrpMybatisDao.searchbyId(groupId);
		return list;
	}

	// 根据机构集（组）id删除机构集（组）的配置信息
	public void delOrgObj(String collectionId) {
		rptOrgGrpMybatisDao.delObj(collectionId);
	}

	// 验证机构集名称是否重复
	public boolean checkName(String collectionName, String collectionId) {
		Map<String, String> params = Maps.newHashMap();
		params.put("collectionName", collectionName);
		params.put("orgno", BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		if (!StringUtils.isEmpty(collectionName)) {
			params.put("id", collectionId);
		}
		ArrayList<RptOrgGrp> list = rptOrgGrpMybatisDao.checkName(params);
		if (list == null || list.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	public LinkedHashMap<String, List<RptOrgGrpVO>> getOrgGrp(String orgType) {
		LinkedHashMap<String, List<RptOrgGrpVO>> map = Maps.newLinkedHashMap();
		if (orgType != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("orgType", orgType);
			params.put("createOrg", BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
			List<RptGrpVO> orgGrpList = rptOrgGrpMybatisDao.getCol(params);
			if (orgGrpList != null && orgGrpList.size() > 0) {
				for (RptOrgGrp rptOrgGrp : orgGrpList) {
					params.put("grpId", rptOrgGrp.getGrpId());
					String orgNo = BioneSecurityUtils.getCurrentUserInfo()
							.getOrgNo();
					Map<String, String> p = Maps.newHashMap();
					p.put("mgrOrgNo", orgNo);
					p.put("orgType", orgType);
					List<String> mgrOrgs = this.rptOrgGrpMybatisDao
							.getMgrOrgNo(p);
					if (mgrOrgs.size() > 0) {
						List<RptOrgGrpVO> totalList = Lists.newArrayList();
						for (String mgrOrg : mgrOrgs) {
							params.put("orgNo", "%/" + mgrOrg + "/%");
							List<RptOrgGrpVO> voList = rptOrgGrpMybatisDao
									.getOrgGrp(params);
							if (voList != null && voList.size() > 0)
								totalList.addAll(voList);
						}
						map.put(rptOrgGrp.getGrpNm(), totalList);
					}
				}
			}
		}
		return map;
	}

	public Map<String, Object> getOrgGrpComboInfo(String orgType) {
		Map<String, Object> info = new HashMap<String, Object>();
		LinkedHashMap<String, List<RptOrgGrpVO>> map = Maps.newLinkedHashMap();
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		if (orgType != null) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("orgType", orgType);
			params.put("createOrg", BioneSecurityUtils.getCurrentUserInfo()
					.getOrgNo());
			List<RptGrpVO> orgGrpList = rptOrgGrpMybatisDao.getCol(params);
			if (orgGrpList != null && orgGrpList.size() > 0) {
				for (RptOrgGrp rptOrgGrp : orgGrpList) {
					CommonComboBoxNode node = new CommonComboBoxNode();
					node.setId(rptOrgGrp.getGrpId());
					node.setText(rptOrgGrp.getGrpNm());
					params.put("grpId", rptOrgGrp.getGrpId());
					String orgNo = BioneSecurityUtils.getCurrentUserInfo()
							.getOrgNo();
					// params.put("orgNo", "%"+orgNo+"%");
					// 先找到登陆人所属机构所管辖的机构组
					Map<String, String> p = Maps.newHashMap();
					p.put("mgrOrgNo", orgNo);
					p.put("orgType", orgType);
					List<String> mgrOrgs = this.rptOrgGrpMybatisDao
							.getMgrOrgNo(p);
					if (mgrOrgs.size() > 0) {
						List<RptOrgGrpVO> totalList = Lists.newArrayList();
						for (String mgrOrg : mgrOrgs) {
							params.put("orgNo", "%/" + mgrOrg + "/%");
							List<RptOrgGrpVO> voList = rptOrgGrpMybatisDao
									.getOrgGrp(params);
							if (voList != null && voList.size() > 0)
								totalList.addAll(voList);
						}
						// if(voList!=null&&voList.size()>0)
						map.put(rptOrgGrp.getGrpId(), totalList);
					}
					nodes.add(node);
				}
			}
		}
		info.put("combo", nodes);
		info.put("info", map);
		return info;
	}
	
	public List<CommonComboBoxNode> getorgGrpCombo(String orgType){
		List<CommonComboBoxNode> nodes = new ArrayList<CommonComboBoxNode>();
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orgNo", BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		String jql ="select grp from RptOrgGrp grp,RptGrpAuthRel rel where 1=1 and rel.id.grpId = grp.grpId and rel.id.orgNo = :orgNo "; 
		if(StringUtils.isNotBlank(orgType)){
			jql += " and grp.orgType = :orgType";
			map.put("orgType", orgType);
		}
		jql += " order by grp.grpNm";
		List<RptOrgGrp> grps = this.baseDAO.findWithNameParm(jql, map);
		if(grps!=null && grps.size()>0){
			for(RptOrgGrp grp :grps){
				CommonComboBoxNode node = new CommonComboBoxNode();
				node.setId(grp.getGrpId());
				node.setText(grp.getGrpNm());
				nodes.add(node);
			}
		}
		return nodes;
	}
	
	public List<String> getGrpOrgNos(String grpId){
		List<String> orgNos = new ArrayList<String>();
		String jql ="select rel from RptGrpOrgRel rel where 1=1 and rel.id.grpId = ?0";
		List<RptGrpOrgRel> grps = this.baseDAO.findWithIndexParam(jql, grpId);
		if(grps!=null && grps.size()>0){
			for(RptGrpOrgRel grp :grps){
				orgNos.add(grp.getId().getOrgNo());
			}
		}
		return orgNos;
	}
}
