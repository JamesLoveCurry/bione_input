
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * spread
 * <p>
 * 层次结构
 * 
 */
public class SpreadSchema {

    /**
     * spreadJS版本号
     * 
     */
    private String version = "1.0";
    /**
     * 开始标签页索引
     * 
     */
    private Integer startSheetIndex;
    /**
     * 标签页总数
     * 
     */
    private Integer sheetCount;
    /**
     * 是否重新计算,该值默认为false,如果勾选页面的avoid recalculation after load 以后,则为true
     * 
     */
    private Boolean noRecalc = false;
    /**
     * 选中的标签页索引
     * 
     */
    private Integer activeSheetIndex;
    /**
     * 标签页显示比例-未测
     * 
     */
    private Double tabStripRatio;
    /**
     * 标签页是否可见
     * 
     */
    private Boolean tabStripVisible;
    /**
     * 标签页是否可编辑
     * 
     */
    private Boolean tabEditable;
    /**
     * 新标签页是否可见
     * 
     */
    private Boolean newTabVisible;
    /**
     * 引用样式-未测
     * 
     */
    private Integer referenceStyle;
    /**
     * 是否使用wijmo主题
     * 
     */
    private Boolean useWijmoTheme;
    /**
     * 用户是否能编辑公式
     * 
     */
    private Boolean canUserEditFormula;
    /**
     * 是否允许撤销
     * 
     */
    private Boolean allowUndo;
    /**
     * 是否允许用户放大表视图
     * 
     */
    private Boolean allowUserZoom;
    /**
     * 是否允许用户自定义大小
     * 
     */
    private Boolean allowUserResize;
    /**
     * 是否允许拖拽
     * 
     */
    private Boolean allowDragDrop;
    /**
     * 是否允许填充
     * 
     */
    private Boolean allowDragFill;
    /**
     * 是否高亮不合法数据
     * 
     */
    private Boolean highlightInvalidData;
    /**
     * 自适应类型-未测
     * 
     */
    private Integer autoFitType;
    /**
     * 是否显示水平滚动条
     * 
     */
    private Boolean showHorizontalScrollbar;
    /**
     * 是否显示垂直滚动条
     * 
     */
    private Boolean showVerticalScrollbar;
    /**
     * 背景颜色.eg:'#000000'
     * 
     */
    private String backColor;
    /**
     * 背景图片
     * 
     */
    private Object backgroundImage;
    /**
     * 背景图片显示方式-未测
     * 
     */
    private Integer backgroundImageLayout;
    /**
     * 未测
     * 
     */
    private String grayAreaBackColor;
    /**
     * 名称管理器，可以指定某一区域，让公式只作用于此区域 eg:'工资表，引用位置为：$B$20:$E$22'
     * 
     */
    @Valid
    private List<NamesProperty> names;
    /**
     * 未测
     * 
     */
    private Integer showResizeTip;
    /**
     * sheets
     * <p>
     * 
     * 
     */
    @Valid
    @JSONField(name="sheets")
    private Map<String, SheetsProperty> sheetsProperties = new HashMap<String, SheetsProperty>();
    /**
     * 样式数组
     * 
     */
    @Valid
    private Collection<Style> namedStyles;

    /**
     * spreadJS版本号
     * 
     */
    public String getVersion() {
        return version;
    }

    /**
     * spreadJS版本号
     * 
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 开始标签页索引
     * 
     */
    public Integer getStartSheetIndex() {
        return startSheetIndex;
    }

    /**
     * 开始标签页索引
     * 
     */
    public void setStartSheetIndex(Integer startSheetIndex) {
        this.startSheetIndex = startSheetIndex;
    }

    /**
     * 标签页总数
     * 
     */
    public Integer getSheetCount() {
        return sheetCount;
    }

    /**
     * 标签页总数
     * 
     */
    public void setSheetCount(Integer sheetCount) {
        this.sheetCount = sheetCount;
    }

    /**
     * 是否重新计算,该值默认为false,如果勾选页面的avoid recalculation after load 以后,则为true
     * 
     */
    public Boolean getNoRecalc() {
        return noRecalc;
    }

    /**
     * 是否重新计算,该值默认为false,如果勾选页面的avoid recalculation after load 以后,则为true
     * 
     */
    public void setNoRecalc(Boolean noRecalc) {
        this.noRecalc = noRecalc;
    }

    /**
     * 选中的标签页索引
     * 
     */
    public Integer getActiveSheetIndex() {
        return activeSheetIndex;
    }

    /**
     * 选中的标签页索引
     * 
     */
    public void setActiveSheetIndex(Integer activeSheetIndex) {
        this.activeSheetIndex = activeSheetIndex;
    }

    /**
     * 标签页显示比例-未测
     * 
     */
    public Double getTabStripRatio() {
        return tabStripRatio;
    }

    /**
     * 标签页显示比例-未测
     * 
     */
    public void setTabStripRatio(Double tabStripRatio) {
        this.tabStripRatio = tabStripRatio;
    }

    /**
     * 标签页是否可见
     * 
     */
    public Boolean getTabStripVisible() {
        return tabStripVisible;
    }

    /**
     * 标签页是否可见
     * 
     */
    public void setTabStripVisible(Boolean tabStripVisible) {
        this.tabStripVisible = tabStripVisible;
    }

    /**
     * 标签页是否可编辑
     * 
     */
    public Boolean getTabEditable() {
        return tabEditable;
    }

    /**
     * 标签页是否可编辑
     * 
     */
    public void setTabEditable(Boolean tabEditable) {
        this.tabEditable = tabEditable;
    }

    /**
     * 新标签页是否可见
     * 
     */
    public Boolean getNewTabVisible() {
        return newTabVisible;
    }

    /**
     * 新标签页是否可见
     * 
     */
    public void setNewTabVisible(Boolean newTabVisible) {
        this.newTabVisible = newTabVisible;
    }

    /**
     * 引用样式-未测
     * 
     */
    public Integer getReferenceStyle() {
        return referenceStyle;
    }

    /**
     * 引用样式-未测
     * 
     */
    public void setReferenceStyle(Integer referenceStyle) {
        this.referenceStyle = referenceStyle;
    }

    /**
     * 是否使用wijmo主题
     * 
     */
    public Boolean getUseWijmoTheme() {
        return useWijmoTheme;
    }

    /**
     * 是否使用wijmo主题
     * 
     */
    public void setUseWijmoTheme(Boolean useWijmoTheme) {
        this.useWijmoTheme = useWijmoTheme;
    }

    /**
     * 用户是否能编辑公式
     * 
     */
    public Boolean getCanUserEditFormula() {
        return canUserEditFormula;
    }

    /**
     * 用户是否能编辑公式
     * 
     */
    public void setCanUserEditFormula(Boolean canUserEditFormula) {
        this.canUserEditFormula = canUserEditFormula;
    }

    /**
     * 是否允许撤销
     * 
     */
    public Boolean getAllowUndo() {
        return allowUndo;
    }

    /**
     * 是否允许撤销
     * 
     */
    public void setAllowUndo(Boolean allowUndo) {
        this.allowUndo = allowUndo;
    }

    /**
     * 是否允许用户放大表视图
     * 
     */
    public Boolean getAllowUserZoom() {
        return allowUserZoom;
    }

    /**
     * 是否允许用户放大表视图
     * 
     */
    public void setAllowUserZoom(Boolean allowUserZoom) {
        this.allowUserZoom = allowUserZoom;
    }

    /**
     * 是否允许用户自定义大小
     * 
     */
    public Boolean getAllowUserResize() {
        return allowUserResize;
    }

    /**
     * 是否允许用户自定义大小
     * 
     */
    public void setAllowUserResize(Boolean allowUserResize) {
        this.allowUserResize = allowUserResize;
    }

    /**
     * 是否允许拖拽
     * 
     */
    public Boolean getAllowDragDrop() {
        return allowDragDrop;
    }

    /**
     * 是否允许拖拽
     * 
     */
    public void setAllowDragDrop(Boolean allowDragDrop) {
        this.allowDragDrop = allowDragDrop;
    }

    /**
     * 是否允许填充
     * 
     */
    public Boolean getAllowDragFill() {
        return allowDragFill;
    }

    /**
     * 是否允许填充
     * 
     */
    public void setAllowDragFill(Boolean allowDragFill) {
        this.allowDragFill = allowDragFill;
    }

    /**
     * 是否高亮不合法数据
     * 
     */
    public Boolean getHighlightInvalidData() {
        return highlightInvalidData;
    }

    /**
     * 是否高亮不合法数据
     * 
     */
    public void setHighlightInvalidData(Boolean highlightInvalidData) {
        this.highlightInvalidData = highlightInvalidData;
    }

    /**
     * 自适应类型-未测
     * 
     */
    public Integer getAutoFitType() {
        return autoFitType;
    }

    /**
     * 自适应类型-未测
     * 
     */
    public void setAutoFitType(Integer autoFitType) {
        this.autoFitType = autoFitType;
    }

    /**
     * 是否显示水平滚动条
     * 
     */
    public Boolean getShowHorizontalScrollbar() {
        return showHorizontalScrollbar;
    }

    /**
     * 是否显示水平滚动条
     * 
     */
    public void setShowHorizontalScrollbar(Boolean showHorizontalScrollbar) {
        this.showHorizontalScrollbar = showHorizontalScrollbar;
    }

    /**
     * 是否显示垂直滚动条
     * 
     */
    public Boolean getShowVerticalScrollbar() {
        return showVerticalScrollbar;
    }

    /**
     * 是否显示垂直滚动条
     * 
     */
    public void setShowVerticalScrollbar(Boolean showVerticalScrollbar) {
        this.showVerticalScrollbar = showVerticalScrollbar;
    }

    /**
     * 背景颜色.eg:'#000000'
     * 
     */
    public String getBackColor() {
        return backColor;
    }

    /**
     * 背景颜色.eg:'#000000'
     * 
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * 背景图片
     * 
     */
    public Object getBackgroundImage() {
        return backgroundImage;
    }

    /**
     * 背景图片
     * 
     */
    public void setBackgroundImage(Object backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    /**
     * 背景图片显示方式-未测
     * 
     */
    public Integer getBackgroundImageLayout() {
        return backgroundImageLayout;
    }

    /**
     * 背景图片显示方式-未测
     * 
     */
    public void setBackgroundImageLayout(Integer backgroundImageLayout) {
        this.backgroundImageLayout = backgroundImageLayout;
    }

    /**
     * 未测
     * 
     */
    public String getGrayAreaBackColor() {
        return grayAreaBackColor;
    }

    /**
     * 未测
     * 
     */
    public void setGrayAreaBackColor(String grayAreaBackColor) {
        this.grayAreaBackColor = grayAreaBackColor;
    }

    /**
     * 名称管理器，可以指定某一区域，让公式只作用于此区域 eg:'工资表，引用位置为：$B$20:$E$22'
     * 
     */
    public List<NamesProperty> getNames() {
        return names;
    }

    /**
     * 名称管理器，可以指定某一区域，让公式只作用于此区域 eg:'工资表，引用位置为：$B$20:$E$22'
     * 
     */
    public void setNames(List<NamesProperty> names) {
        this.names = names;
    }

    /**
     * 未测
     * 
     */
    public Integer getShowResizeTip() {
        return showResizeTip;
    }

    /**
     * 未测
     * 
     */
    public void setShowResizeTip(Integer showResizeTip) {
        this.showResizeTip = showResizeTip;
    }

    /**
     * sheets
     * <p>
     * 
     * 
     */
    public Map<String, SheetsProperty> getSheetsProperties() {
        return sheetsProperties;
    }

    /**
     * sheets
     * <p>
     * 
     * 
     */
    public void setSheetsProperties(Map<String, SheetsProperty> sheetsProperties) {
        this.sheetsProperties = sheetsProperties;
    }

    /**
     * 样式数组
     * 
     */
    public Collection<Style> getNamedStyles() {
        return namedStyles;
    }

    /**
     * 样式数组
     * 
     */
    public void setNamedStyles(Collection<Style> namedStyles) {
        this.namedStyles = namedStyles;
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
