package com.yusys.bione.frame.authobj.web;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.mybatis.PageMyBatis;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.authobj.entity.BioneOrgGrp;
import com.yusys.bione.frame.authobj.service.OrgGrpBS;
import com.yusys.bione.frame.authobj.web.vo.BioneOrgGrpVO;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @项目名称： 统一监管报送
 * @类名称： OrgGrpController
 * @类描述:
 * @功能描述:
 * @创建人: huzq1
 * @创建时间: 2022/01/06 10:00
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
@Controller
@RequestMapping("/bione/admin/orggrp")
public class OrgGrpController {

    @Autowired
    private OrgGrpBS orgGrpBS;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView("/frame/orggrp/orggrp-index");
        String orgNo = StringUtils2.javaScriptEncode(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
        modelAndView.addObject("orgNo", orgNo);
        modelAndView.addObject("isSuperUser", BioneSecurityUtils.getCurrentUserInfo().isSuperUser());
        return modelAndView;
    }

    @ResponseBody
    @RequestMapping("/list")
    public Map<String, Object> list(Pager pager){
        PageMyBatis<BioneOrgGrpVO> list = orgGrpBS.list(pager);
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
        return "/frame/orggrp/orggrp-edit";
    }

    /**
     * 新建或修改机构集，验证名字是否重复
     * @param grpNm
     * @param collectionId
     * @return
     */
    @RequestMapping("/checkName")
    @ResponseBody
    public Map<String, Object> checkName(String grpNm, String collectionId) {
        boolean boo = orgGrpBS.checkName(grpNm, collectionId);
        Map<String, Object> result = new HashMap<>();
        if(boo){
            result.put("status", "success");
        } else {
            result.put("status", "fail");
        }
        return result;
    }

    /**
     * 加载修改机构集（组）的具体信息
     *
     * @return
     */
    @RequestMapping("/editgroup/{id}")
    @ResponseBody
    public BioneOrgGrp changeOrg(@PathVariable("id") String groupId) {
        return orgGrpBS.getRptById(groupId);
    }

    /**
     * 保存新建机构集（组）的信息
     */
    @RequestMapping("/savegroup")
    public void saveNewOrg(BioneOrgGrp orgGrp) {
        // 1.新增
        if (StringUtils.isBlank(orgGrp.getGrpId())) {
            orgGrp.setGrpId(RandomUtils.uuid2());
            orgGrp.setCreateOrg(BioneSecurityUtils.getCurrentUserInfo().getOrgNo());
            orgGrp.setCreateTime(new Timestamp(System.currentTimeMillis()));
            orgGrpBS.saveGroup(orgGrp);
        } else { // 2.修改
            orgGrpBS.editGroup(orgGrp);
        }
    }

    /**
     * 加载修改新建机构集的页面
     *
     * @return
     */
    @RequestMapping("/editset")
    public ModelAndView loadOrgset(String groupId) {
        groupId = StringUtils2.javaScriptEncode(groupId);
        return new ModelAndView("/frame/orggrp/orggrp-edit", "colId", groupId);
    }

    /**
     * 删除机构集（组）
     */
    @RequestMapping(value = "/delgroup", method = RequestMethod.POST)
    @ResponseBody
    public Boolean delOrg(String groupId) {
        return orgGrpBS.delOrg(groupId);
    }

    /**
     * 加载修改机构集（组）配置的机构的页面
     *
     * @return
     */
    @RequestMapping("/config")
    public ModelAndView SaveChangeOrgObj(String grpId) {
        ModelMap model = new ModelMap();
        model.put("grpId", StringUtils2.javaScriptEncode(grpId));
        return new ModelAndView("/frame/orggrp/orggrp-obj", model);
    }

    /**
     * 保存修改机构集（组）的配置机构的信息
     *
     * @param ids
     */
    @RequestMapping(value = "/saveorgobj", method = RequestMethod.POST)
    @ResponseBody
    public void saveorgobj(String grpId, String ids) {
        orgGrpBS.saveorgobj(grpId, ids);
    }

    /**
     * 反选机构树
     * @param grpId
     * @return
     */
    @RequestMapping(value = "/gettreeobj", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, List<String>> getTreeObj(String grpId) {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> orgNos = orgGrpBS.getOrgList(grpId);
        map.put("orgNos", orgNos);
        return map;
    }
}
