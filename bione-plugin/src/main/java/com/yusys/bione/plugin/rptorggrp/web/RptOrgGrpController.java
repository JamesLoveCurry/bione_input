package com.yusys.bione.plugin.rptorggrp.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import com.yusys.bione.plugin.rptorggrp.entity.RptGrpAuthRel;
import com.yusys.bione.plugin.rptorggrp.entity.RptGrpAuthRelPK;
import com.yusys.bione.plugin.rptorggrp.entity.RptOrgGrp;
import com.yusys.bione.plugin.rptorggrp.service.RptOrgGrpBS;
import com.yusys.bione.plugin.rptorggrp.web.vo.RptGrpVO;
@Controller
@RequestMapping("/rpt/frame/rptorggrp")
public class RptOrgGrpController extends BaseController {

	@Autowired
	private RptOrgGrpBS rptOrgGrpBS;
	
	@Autowired
	private RptOrgInfoBS rptMgrFrsOrgBS;

	@RequestMapping
	public ModelAndView index(){
		String orgNo = StringUtils2.javaScriptEncode(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		return new ModelAndView("/plugin/rptorggrp/rptorggrp-index", "orgNo", orgNo);
	}
	
	/**
	 * 加载机构集（组）列表数据
	 * 
	 * @return
	 */
	@RequestMapping("/loaddata")
	@ResponseBody
	public Map<String, Object> indexLoadData( Pager pager) {
		PageMyBatis<RptGrpVO> list = rptOrgGrpBS.getColInfo(pager);
		Map<String, Object> params = Maps.newHashMap();
		params.put("Rows", list.getResult());
		params.put("Total", list.getTotalCount());
		return params;
	}
	/**
	 * 加载新增新建机构集的页面
	 * 
	 * @return
	 */
	@RequestMapping("/newset")
	public String newSet() {
		return "/plugin/rptorggrp/rptorggrp-edit";
	}
	/**
	 * 加载修改新建机构集的页面
	 * 
	 * @return
	 */
	@RequestMapping("/editset")
	public ModelAndView loadOrgset(String groupId) {
		groupId = StringUtils2.javaScriptEncode(groupId);
		return new ModelAndView(
				"/plugin/rptorggrp/rptorggrp-edit",
				"colId", groupId);
	}
	/**
	 * 加载修改机构集（组）的具体信息
	 * 
	 * @return
	 */
	@RequestMapping("/editgroup/{id}")
	@ResponseBody
	public RptOrgGrp changeOrg(@PathVariable("id") String groupId) {
		return rptOrgGrpBS.getRptById(groupId);
	}
	
	/**
	 * 保存新建机构集（组）的信息
	 */
	@RequestMapping("/savegroup")
	public void saveNewOrg(RptOrgGrp rptcol) {
		rptcol.setCreateOrg(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		// 1.新增
		if ("".equals(rptcol.getGrpId())
				|| rptcol.getGrpId() == null) {
			rptcol.setGrpId(RandomUtils.uuid2());
			RptGrpAuthRel rel = new RptGrpAuthRel();
			RptGrpAuthRelPK id = new RptGrpAuthRelPK();
			id.setGrpId(rptcol.getGrpId());
			id.setOrgNo(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			rel.setId(id);
			rptOrgGrpBS.saveGroup(rptcol,rel);
		}
		// 2.修改
		else {
			RptOrgGrp rpt = rptOrgGrpBS.getRptById(rptcol.getGrpId());
			if (rpt != null && rpt.getOrgType() != rptcol.getOrgType()) {
				rptOrgGrpBS.delOrgObj(rptcol.getGrpId());
			}
			rptOrgGrpBS.editGroup(rptcol);
		}
	}

	/**
	 * 保存修改机构集（组）的配置机构的信息
	 * 
	 * @param collectionId
	 * @param ids
	 */
	@RequestMapping(value = "/changeGrpSts", method = RequestMethod.POST)
	@ResponseBody
	public void changeGrpSts(String grpId,String sts){
		if(!sts.equals("null")){
			RptGrpAuthRelPK id = new RptGrpAuthRelPK();
			RptGrpAuthRel rel = new RptGrpAuthRel();
			id.setGrpId(grpId);
			id.setOrgNo(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
			rel.setId(id);
			this.rptOrgGrpBS.saveOrUpdateEntity(rel);
		}
		else{
			this.rptOrgGrpBS.delRel(grpId,BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
		}
	}
	/**
	 * 删除机构集（组）
	 */
	@RequestMapping(value = "/delgroup", method = RequestMethod.POST)
	@ResponseBody
	public Boolean delOrg(String groupId) {
		return rptOrgGrpBS.delOrg(groupId);
	}

	/**
	 * 加载修改机构集（组）配置的机构的页面
	 * 
	 * @return
	 */
	@RequestMapping("/config")
	public ModelAndView SaveChangeOrgObj(String grpId, String orgType) {
		ModelMap model = new ModelMap();
		model.put("grpId", StringUtils2.javaScriptEncode(grpId));
		model.put("orgType", StringUtils2.javaScriptEncode(orgType));
		return new ModelAndView(
				"/plugin/rptorggrp/rptorggrp-obj", model);
	}

	/**
	 * 保存修改机构集（组）的配置机构的信息
	 * 
	 * @param collectionId
	 * @param ids
	 */
	@RequestMapping(value = "/saveorgobj", method = RequestMethod.POST)
	@ResponseBody
	public void saveorgobj(String grpId, String ids,String nodeType) {
		rptOrgGrpBS.saveorgobj(grpId, ids,nodeType);
	}

	/**
	 * 加载机构树
	 * @param nodeType
	 *            ,节点类型:null则为根节点,1:机构集(行政区域:人行大集中) 0:机构组(机构信息:1104)
	 * @param objId
	 *            ,节点id，即机构id
	 * @param orgType
	 *            ,机构类型:RPT_FRS_SERVICE_1104 = "02" 银监会1104; public static final
	 *            String RPT_FRS_SERVICE_PBC = "03" 人行大集中
	 * @return
	 * 
	 */
	@RequestMapping(value = "/gettreelist", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTreeList(String nodeType, String orgNo,
			String orgType) {
		List<CommonTreeNode> treeNodeList = Lists.newArrayList();
		// 第一次加载，加载根节点和该机构组（集）已经配置的机构
		if (orgType == null || "".equals(orgType)) {
			CommonTreeNode treeNode = new CommonTreeNode();
			treeNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
			treeNode.setIsParent(true);
			treeNode.setIcon(this.getContextPath() + "/"
					+ GlobalConstants4frame.ICON_URL + "/house.png");
			Map<String, String> map = Maps.newHashMap();
			map.put("orgNo", GlobalConstants4frame.TREE_ROOT_NO);
			if (nodeType.equals("01")) {
				treeNode.setText("行内机构");
			} else if (nodeType.equals("02")) {
				treeNode.setText("银监会1104");
			} else if (nodeType.equals("03")) {
				treeNode.setText("人行大集中");
			} else {
				treeNode.setText("监管机构");
			}
			map.put("orgType", nodeType);
			treeNode.setParams(map);
			treeNodeList.add(treeNode);
		} else {
			treeNodeList = rptMgrFrsOrgBS.getDataTree(orgType, orgNo, orgNo,
					this.getContextPath(), null);
		}
		return treeNodeList;
	}

	/**
	 * 反选机构树
	 * @param collectionId
	 * @return
	 */
	@RequestMapping(value = "/gettreeobj", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, List<String>> getTreeObj(String grpId) {
		Map<String, List<String>> map = Maps.newHashMap();
		List<String> orgNos = rptOrgGrpBS.getOrgList(grpId);
		map.put("orgNos", orgNos);
		return map;
	}

	/**
	 * 新建或修改机构集，验证名字是否重复
	 * @param collectionName
	 * @param collectionId
	 * @return
	 */
	@RequestMapping("/checkName")
	@ResponseBody
	public boolean checkName(String grpNm, String collectionId) {
		return rptOrgGrpBS.checkName(grpNm, collectionId);
	}

	@RequestMapping("/getorgGrpCombo")
	@ResponseBody
	public List<CommonComboBoxNode> getorgGrpCombo(String orgType){
		return rptOrgGrpBS.getorgGrpCombo(orgType);
	}
	
	@RequestMapping("/getGrpOrgNos")
	@ResponseBody
	public List<String> getGrpOrgNos(String grpId){
		return rptOrgGrpBS.getGrpOrgNos(grpId);
	}
	
	
}
