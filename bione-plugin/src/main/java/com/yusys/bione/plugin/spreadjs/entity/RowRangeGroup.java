
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 行范围集合
 * 
 */
public class RowRangeGroup {

    /**
     * 集合个数
     * 
     */
    private Integer itemsCount;
    /**
     * 未测
     * 
     */
    @Valid
    private List<Object> itemsData = new ArrayList<Object>();
    /**
     * 未测
     * 
     */
    private Integer direction;
    /**
     * 未测
     * 
     */
    private Object head;
    /**
     * 未测
     * 
     */
    private Object tail;

    /**
     * 集合个数
     * 
     */
    public Integer getItemsCount() {
        return itemsCount;
    }

    /**
     * 集合个数
     * 
     */
    public void setItemsCount(Integer itemsCount) {
        this.itemsCount = itemsCount;
    }

    /**
     * 未测
     * 
     */
    public List<Object> getItemsData() {
        return itemsData;
    }

    /**
     * 未测
     * 
     */
    public void setItemsData(List<Object> itemsData) {
        this.itemsData = itemsData;
    }

    /**
     * 未测
     * 
     */
    public Integer getDirection() {
        return direction;
    }

    /**
     * 未测
     * 
     */
    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    /**
     * 未测
     * 
     */
    public Object getHead() {
        return head;
    }

    /**
     * 未测
     * 
     */
    public void setHead(Object head) {
        this.head = head;
    }

    /**
     * 未测
     * 
     */
    public Object getTail() {
        return tail;
    }

    /**
     * 未测
     * 
     */
    public void setTail(Object tail) {
        this.tail = tail;
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
