
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 未测
 * 
 */
public class Condition {

    /**
     * 未测
     * 
     */
    private Integer conType;
    
    private Integer customValueType;
    /**
     * 未测
     * 
     */
    private Boolean ignoreBlank;
    /**
     * 以逗号分隔的候选字符串，eg: 若是填入Y,N的下拉框，则该值为'Y,N'
     * 
     */
    private String expected;
    /**
     * 未测
     * 
     */
    private Object formula;

    /**
     * 未测
     * 
     */
    public Integer getConType() {
        return conType;
    }

    /**
     * 未测
     * 
     */
    public void setConType(Integer conType) {
        this.conType = conType;
    }

    /**
     * 未测
     * 
     */
    public Boolean getIgnoreBlank() {
        return ignoreBlank;
    }

    /**
     * 未测
     * 
     */
    public void setIgnoreBlank(Boolean ignoreBlank) {
        this.ignoreBlank = ignoreBlank;
    }

    /**
     * 以逗号分隔的候选字符串，eg: 若是填入Y,N的下拉框，则该值为'Y,N'
     * 
     */
    public String getExpected() {
        return expected;
    }

    /**
     * 以逗号分隔的候选字符串，eg: 若是填入Y,N的下拉框，则该值为'Y,N'
     * 
     */
    public void setExpected(String expected) {
        this.expected = expected;
    }

    /**
     * 未测
     * 
     */
    public Object getFormula() {
        return formula;
    }

    /**
     * 未测
     * 
     */
    public void setFormula(Object formula) {
        this.formula = formula;
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

	public Integer getCustomValueType() {
		return customValueType;
	}

	public void setCustomValueType(Integer customValueType) {
		this.customValueType = customValueType;
	}

}
