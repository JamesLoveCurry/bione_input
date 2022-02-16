
package com.yusys.bione.plugin.spreadjs.entity;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 文件的主题样式
 * 
 */
public class Theme {

    /**
     * 主题名称，eg:'Office'
     * 
     */
    private String name;
    /**
     * 主题颜色对象
     * 
     */
    @Valid
    private ThemeColor themeColor;
    /**
     * 标题字体名称 eg:'Cambria'
     * 
     */
    private String headingFont;
    /**
     * 正文字体名称 eg:'Calibri'
     * 
     */
    private String bodyFont;

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
     * 主题颜色对象
     * 
     */
    public ThemeColor getThemeColor() {
        return themeColor;
    }

    /**
     * 主题颜色对象
     * 
     */
    public void setThemeColor(ThemeColor themeColor) {
        this.themeColor = themeColor;
    }

    /**
     * 标题字体名称 eg:'Cambria'
     * 
     */
    public String getHeadingFont() {
        return headingFont;
    }

    /**
     * 标题字体名称 eg:'Cambria'
     * 
     */
    public void setHeadingFont(String headingFont) {
        this.headingFont = headingFont;
    }

    /**
     * 正文字体名称 eg:'Calibri'
     * 
     */
    public String getBodyFont() {
        return bodyFont;
    }

    /**
     * 正文字体名称 eg:'Calibri'
     * 
     */
    public void setBodyFont(String bodyFont) {
        this.bodyFont = bodyFont;
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
