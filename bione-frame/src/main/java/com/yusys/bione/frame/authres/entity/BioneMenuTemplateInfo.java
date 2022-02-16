package com.yusys.bione.frame.authres.entity;

/**
 * @项目名称： 统一监管报送
 * @类名称： BioneMenuTemplateInfo
 * @类描述:
 * @功能描述:
 * @创建人: huzq1
 * @创建时间: 2021/07/06 17:30
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
public class BioneMenuTemplateInfo {

    private String funcType;
    private String funcId;
    private String funcName;
    private String upId;
    private String orderNo;
    private String navIcon;
    private String navPath;
    private String funcSts;
    private String remark;

    public String getFuncType() {
        return funcType;
    }

    public void setFuncType(String funcType) {
        this.funcType = funcType;
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
        this.funcId = funcId;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getUpId() {
        return upId;
    }

    public void setUpId(String upId) {
        this.upId = upId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getNavIcon() {
        return navIcon;
    }

    public void setNavIcon(String navIcon) {
        this.navIcon = navIcon;
    }

    public String getNavPath() {
        return navPath;
    }

    public void setNavPath(String navPath) {
        this.navPath = navPath;
    }

    public String getFuncSts() {
        return funcSts;
    }

    public void setFuncSts(String funcSts) {
        this.funcSts = funcSts;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
