package com.yusys.bione.plugin.auth.authres;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.frame.auth.entity.BioneAuthObjResRel;
import com.yusys.bione.frame.authres.entity.BioneResOperInfo;
import com.yusys.bione.frame.security.IResObject;
import com.yusys.bione.plugin.BfdReport.service.BfdReportBs;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @项目名称：金融基础报表权限实现类
 * @类名称： BfdReportResImpl
 * @类描述:
 * @功能描述:
 * @创建人: miaokx@yusys.com.cn
 * @创建时间: 2021年03月24日 11:49
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
@Component
public class BfdReportResImpl implements IResObject {
    @Autowired
    private BfdReportBs bfdReportBs;

    public static final String RES_OBJ_DEF_NO = GlobalConstants4plugin.BFD_RES_NO;
    @Override
    public String getResObjDefNo() {
        return null;
    }

    @Override
    public List<CommonTreeNode> doGetResInfo() {
        return this.bfdReportBs.getAuthTree();
    }

    @Override
    public List<String> doGetResPermissions(List<BioneAuthObjResRel> authObjResRelList) {
        return null;
    }

    @Override
    public List<CommonTreeNode> doGetResOperateInfo(Long resId) {
        return null;
    }

    @Override
    public List<CommonTreeNode> doGetResDataRuleInfo(Long resId) {
        return null;
    }

    @Override
    public List<BioneResOperInfo> findResOperList(String resDefNo, List<String> resIdList) {
        return null;
    }
}
