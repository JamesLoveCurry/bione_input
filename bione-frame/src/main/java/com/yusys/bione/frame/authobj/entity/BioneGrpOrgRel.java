package com.yusys.bione.frame.authobj.entity;

import java.io.Serializable;

/**
 * @项目名称： 统一监管报送
 * @类名称： BioneGrpOrgRel
 * @类描述:
 * @功能描述:
 * @创建人: huzq1
 * @创建时间: 2022/01/06 15:54
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
public class BioneGrpOrgRel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orgNo;

    private String grpId;

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getGrpId() {
        return grpId;
    }

    public void setGrpId(String grpId) {
        this.grpId = grpId;
    }
}
