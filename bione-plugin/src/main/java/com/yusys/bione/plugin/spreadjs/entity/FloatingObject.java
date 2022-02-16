
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class FloatingObject {

    /**
     * 悬浮对象路径
     * 
     */
    private String src;
    /**
     * 背景色
     * 
     */
    private String backColor;
    /**
     * 边框半径
     * 
     */
    private Integer borderRadius;
    /**
     * 边框宽度
     * 
     */
    private Integer borderWidth;
    /**
     * 边框样式
     * 
     */
    private String borderStyle;
    /**
     * 边框颜色
     * 
     */
    private String borderColor;
    /**
     * 图片扩展类型
     * 
     */
    private Integer pictureStretch;
    /**
     * 悬浮对象名称
     * 
     */
    private String name;
    /**
     * 对象x坐标
     * 
     */
    private Integer x;
    /**
     * 对象y坐标
     * 
     */
    private Integer y;
    /**
     * 悬浮对象宽度
     * 
     */
    private Integer width;
    /**
     * 悬浮对象高度
     * 
     */
    private Integer height;
    /**
     * 悬浮对象是否可以打印
     * 
     */
    private Boolean canPrint;
    /**
     * 悬浮对象是否被选中
     * 
     */
    private Boolean isSelected;
    /**
     * 悬浮对象是否固定
     * 
     */
    private Boolean isLocked;
    /**
     * 悬浮对象是否可见
     * 
     */
    private Boolean isVisible;
    /**
     * 悬浮对象是否可动
     * 
     */
    private Boolean dynamicMove;
    /**
     * 悬浮对象是否可以编辑大小
     * 
     */
    private Boolean dynamicSize;
    /**
     * 悬浮对象类型
     * 
     */
    private Integer floatingObjectType;


    /**
     * 悬浮对象路径
     * 
     */
    public String getSrc() {
        return src;
    }

    /**
     * 悬浮对象路径
     * 
     */
    public void setSrc(String src) {
        this.src = src;
    }

    /**
     * 背景色
     * 
     */
    public String getBackColor() {
        return backColor;
    }

    /**
     * 背景色
     * 
     */
    public void setBackColor(String backColor) {
        this.backColor = backColor;
    }

    /**
     * 边框半径
     * 
     */
    public Integer getBorderRadius() {
        return borderRadius;
    }

    /**
     * 边框半径
     * 
     */
    public void setBorderRadius(Integer borderRadius) {
        this.borderRadius = borderRadius;
    }

    /**
     * 边框宽度
     * 
     */
    public Integer getBorderWidth() {
        return borderWidth;
    }

    /**
     * 边框宽度
     * 
     */
    public void setBorderWidth(Integer borderWidth) {
        this.borderWidth = borderWidth;
    }

    /**
     * 边框样式
     * 
     */
    public String getBorderStyle() {
        return borderStyle;
    }

    /**
     * 边框样式
     * 
     */
    public void setBorderStyle(String borderStyle) {
        this.borderStyle = borderStyle;
    }

    /**
     * 边框颜色
     * 
     */
    public String getBorderColor() {
        return borderColor;
    }

    /**
     * 边框颜色
     * 
     */
    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * 图片扩展类型
     * 
     */
    public Integer getPictureStretch() {
        return pictureStretch;
    }

    /**
     * 图片扩展类型
     * 
     */
    public void setPictureStretch(Integer pictureStretch) {
        this.pictureStretch = pictureStretch;
    }

    /**
     * 悬浮对象名称
     * 
     */
    public String getName() {
        return name;
    }

    /**
     * 悬浮对象名称
     * 
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 对象x坐标
     * 
     */
    public Integer getX() {
        return x;
    }

    /**
     * 对象x坐标
     * 
     */
    public void setX(Integer x) {
        this.x = x;
    }

    /**
     * 对象y坐标
     * 
     */
    public Integer getY() {
        return y;
    }

    /**
     * 对象y坐标
     * 
     */
    public void setY(Integer y) {
        this.y = y;
    }

    /**
     * 悬浮对象宽度
     * 
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * 悬浮对象宽度
     * 
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * 悬浮对象高度
     * 
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * 悬浮对象高度
     * 
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * 悬浮对象是否可以打印
     * 
     */
    public Boolean getCanPrint() {
        return canPrint;
    }

    /**
     * 悬浮对象是否可以打印
     * 
     */
    public void setCanPrint(Boolean canPrint) {
        this.canPrint = canPrint;
    }

    /**
     * 悬浮对象是否被选中
     * 
     */
    public Boolean getIsSelected() {
        return isSelected;
    }

    /**
     * 悬浮对象是否被选中
     * 
     */
    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }

    /**
     * 悬浮对象是否固定
     * 
     */
    public Boolean getIsLocked() {
        return isLocked;
    }

    /**
     * 悬浮对象是否固定
     * 
     */
    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    /**
     * 悬浮对象是否可见
     * 
     */
    public Boolean getIsVisible() {
        return isVisible;
    }

    /**
     * 悬浮对象是否可见
     * 
     */
    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * 悬浮对象是否可动
     * 
     */
    public Boolean getDynamicMove() {
        return dynamicMove;
    }

    /**
     * 悬浮对象是否可动
     * 
     */
    public void setDynamicMove(Boolean dynamicMove) {
        this.dynamicMove = dynamicMove;
    }

    /**
     * 悬浮对象是否可以编辑大小
     * 
     */
    public Boolean getDynamicSize() {
        return dynamicSize;
    }

    /**
     * 悬浮对象是否可以编辑大小
     * 
     */
    public void setDynamicSize(Boolean dynamicSize) {
        this.dynamicSize = dynamicSize;
    }

    /**
     * 悬浮对象类型
     * 
     */
    public Integer getFloatingObjectType() {
        return floatingObjectType;
    }

    /**
     * 悬浮对象类型
     * 
     */
    public void setFloatingObjectType(Integer floatingObjectType) {
        this.floatingObjectType = floatingObjectType;
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
