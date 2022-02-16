
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 迷你图片集合管理器，比如EXCEL中的折线，柱状，盈亏图
 * 
 */
public class SparklineGroupManager {

    /**
     * groups
     * 
     */
    @Valid
    private List<Object> groups = new ArrayList<Object>();

    /**
     * groups
     * 
     */
    public List<Object> getGroups() {
        return groups;
    }

    /**
     * groups
     * 
     */
    public void setGroups(List<Object> groups) {
        this.groups = groups;
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
