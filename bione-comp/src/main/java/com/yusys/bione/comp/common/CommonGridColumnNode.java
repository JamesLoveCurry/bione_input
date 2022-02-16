package com.yusys.bione.comp.common;

import java.io.Serializable;

/**
 * @项目名称： 统一监管报送
 * @类名称： CommonGridColumnNode
 * @类描述:
 * @功能描述:
 * @创建人: huzq1
 * @创建时间: 2021/09/24 17:34
 * @修改备注:
 * @修改记录: 修改时间  修改人员  修改原因
 * ---------------------------------------------------------
 * @Version 1.0.0
 * @Copyright (c) 宇信科技-版权所有
 */
public class CommonGridColumnNode implements Serializable {

    private String display;
    private String name;
    private String width;

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

}
