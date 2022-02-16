package com.yusys.bione.plugin.eastreport.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @项目名称：金融基础报表实体类
 * @类名称： EASTReport
 * @类描述:
 * @功能描述:
 * @创建人: miaokx@yusys.com.cn
 * @创建时间: 2021年03月24日 15:02
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
@Entity
@Table(name = "EAST_CR_TAB")
public class EastReport implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name="RID", unique=true, nullable=false, length=32)
    private String rid;
    @Column(name="TAB_NAME", length=200)
    private String tabName;
    @Column(name="TAB_NAME_EN", length=200)
    private String tabNameEn;
    /**
     * 报表分类
     */
    @Column(name="TAB_TYPE", length=20)
    private String tabType;
    @Column(name="TAB_NO", length=5)
    private String tabNo;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }

    public String getTabNameEn() {
        return tabNameEn;
    }

    public void setTabNameEn(String tabNameEn) {
        this.tabNameEn = tabNameEn;
    }

    public String getTabType() {
        return tabType;
    }

    public void setTabType(String tabType) {
        this.tabType = tabType;
    }

    public String getTabNo() {
        return tabNo;
    }

    public void setTabNo(String tabNo) {
        this.tabNo = tabNo;
    }
}
