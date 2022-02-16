
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 未知属性
 * 
 */
public class ConditionalFormats {

    /**
     * 未测
     * 
     */
    @Valid
    private List<Object> rules = new ArrayList<Object>();

    /**
     * 未测
     * 
     */
    public List<Object> getRules() {
        return rules;
    }

    /**
     * 未测
     * 
     */
    public void setRules(List<Object> rules) {
        this.rules = rules;
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
