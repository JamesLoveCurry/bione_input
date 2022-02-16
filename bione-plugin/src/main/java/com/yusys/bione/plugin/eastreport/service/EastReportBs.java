package com.yusys.bione.plugin.eastreport.service;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.eastreport.entity.EastReport;
import com.yusys.bione.plugin.eastreport.repository.EastReportDao;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCatalog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @项目名称：金融基础权限业务类
 * @类名称： eastReportBs
 * @类描述:
 * @功能描述:
 * @创建人: miaokx@yusys.com.cn
 * @创建时间: 2021年03月24日 13:55
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
@Service
@Transactional(readOnly = true)
public class EastReportBs extends BaseBS<Object> {
    @Autowired
    private EastReportDao eastReportDao;
    public static final String RES_OBJ_DEF_NO = GlobalConstants4plugin.EAST_RES_NO;
    private String icon_org = "images/classics/icons/organ.gif";
    private String icon_dept = "images/classics/icons/cur_activity_none.gif";
    /**
     * @方法描述: 获取树结构
     * @创建人: miaokx@yusys.com.cn
     * @创建时间: 2021/3/24 14:05
     * @return: java.util.List<com.yusys.bione.comp.common.CommonTreeNode>
     */
    public List<CommonTreeNode> getAuthTree() {
        List<CommonTreeNode> nodes = new ArrayList<CommonTreeNode>();
        // 构造根节点
        CommonTreeNode baseNode = new CommonTreeNode();
        baseNode.setId(GlobalConstants4frame.TREE_ROOT_NO);
        baseNode.setText("全部报表");
        baseNode.setUpId(GlobalConstants4frame.TREE_ROOT_NO);
        baseNode.getParams().put("type", "root");
        baseNode.setIcon(GlobalConstants4plugin.DATA_TREE_NODE_ICON_ROOT);
        RptIdxCatalog catalogBase = new RptIdxCatalog();
        baseNode.setData(catalogBase);
        catalogBase.setIndexCatalogNo(GlobalConstants4frame.TREE_ROOT_NO);
        nodes.add(baseNode);
        List<String> authRptIds = BioneSecurityUtils.getResIdListOfUser(RES_OBJ_DEF_NO);
        //权限控制，管理者只能看到授权给自己的报表，普通用户无权限
        Map<String, Object> param = new HashMap<String, Object>();
        if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
                && "Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
            if (authRptIds.size()>0) {
                param.put("authRptIds", authRptIds);
            } else {
                return nodes;
            }
        } else if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()
                && !"Y".equals(BioneSecurityUtils.getCurrentUserInfo().getIsManager())){
            return nodes;
        }
        // 查询所有的表
        List<EastReport> eastReportList = eastReportDao.getAllEastReport(param);
        // 获取所有的分类
        List<String> tabTypes = new ArrayList<>();
        for (EastReport eastReport : eastReportList) {
            if (StringUtils.isNotEmpty(eastReport.getTabType()) && !tabTypes.contains(eastReport.getTabType())){
                tabTypes.add(eastReport.getTabType());
            }
        }
        // 对分类处理组装外层树
        for (String tabType : tabTypes) {
            Map<String, String> paramMap = new HashMap<String, String>();
            CommonTreeNode node = new CommonTreeNode();
            node.setId(tabType);
            node.setText(tabType);
            node.setUpId(CommonTreeNode.ROOT_ID);
            node.setData(tabType);
            paramMap.put("id", tabType);
            paramMap.put("realId", tabType);
            // 有这个属性的节点不允许点击
            paramMap.put("cantClick", "1");
            node.setParams(paramMap);
            node.setIcon(icon_org);
            nodes.add(node);
        }

        for (EastReport eastReport : eastReportList) {
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("resDefNo", RES_OBJ_DEF_NO);
            CommonTreeNode node = new CommonTreeNode();
            node.setId(eastReport.getTabNameEn());
            node.setText(eastReport.getTabName());
            node.setUpId(eastReport.getTabType());
            node.setData(eastReport);
            paramMap.put("id", eastReport.getTabNameEn());
            paramMap.put("indexNo", eastReport.getTabNo());
            node.setParams(paramMap);
            node.setIcon(icon_dept);

            nodes.add(node);
        }
        return nodes;
    }
}
