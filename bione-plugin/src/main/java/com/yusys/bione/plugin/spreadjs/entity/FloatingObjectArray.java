
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 悬浮对象管理器,是指直接插入的图片，超链接，公式等对象，注意图片是以位图的形式存储的。
 * 
 */
public class FloatingObjectArray {

    /**
     * 悬浮对象数组
     * 
     */
    @Valid
    private List<FloatingObject> floatingObjects = new ArrayList<FloatingObject>();

    /**
     * 悬浮对象数组
     * 
     */
    public List<FloatingObject> getFloatingObjects() {
        return floatingObjects;
    }

    /**
     * 悬浮对象数组
     * 
     */
    public void setFloatingObjects(List<FloatingObject> floatingObjects) {
        this.floatingObjects = floatingObjects;
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
