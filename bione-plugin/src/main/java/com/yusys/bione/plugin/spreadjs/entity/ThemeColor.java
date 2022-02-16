
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 主题颜色对象
 * 
 */
public class ThemeColor {

    /**
     * 主题名称，eg:'Office'
     * 
     */
    private String name;
    /**
     * 颜色数组，数组的每个对象分别存了一个颜色的A,R,G,B 4个整形值
     * 
     */
    @Valid
    private List<ColorList> colorList = new ArrayList<ColorList>();

    /**
     * 主题名称，eg:'Office'
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * 主题名称，eg:'Office'
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 颜色数组，数组的每个对象分别存了一个颜色的A,R,G,B 4个整形值
     * 
     */
    public List<ColorList> getColorList() {
        return colorList;
    }

    /**
     * 颜色数组，数组的每个对象分别存了一个颜色的A,R,G,B 4个整形值
     * 
     */
    public void setColorList(List<ColorList> colorList) {
        this.colorList = colorList;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object other) {
        return EqualsBuilder.reflectionEquals(this, other);
    }

}
