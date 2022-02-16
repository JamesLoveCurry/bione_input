
package com.yusys.bione.plugin.spreadjs.entity;

import javax.validation.Valid;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 单元格带下拉框等样式时，该属性值有效
 * 
 */
public class Validator {

    /**
     * 未测
     * 
     */
    private Integer comparisonOperator;
    
    private Boolean ignoreBlank;
    
    private String inputTitle;
    
    private String inputMessage;
    /**
     * 未测
     * 
     */
    private Integer type;
    /**
     * 为数字0表示无下划线；为数字1表示单下划线；为数字2表示删除线；为数字3表示下划线和删除线
     * 
     */
    private Integer textDecoration;
    /**
     * 未测
     * 
     */
    @Valid
    private Condition condition;

    /**
     * 未测
     * 
     */
    public Integer getComparisonOperator() {
        return comparisonOperator;
    }

    /**
     * 未测
     * 
     */
    public void setComparisonOperator(Integer comparisonOperator) {
        this.comparisonOperator = comparisonOperator;
    }

    /**
     * 未测
     * 
     */
    public Integer getType() {
        return type;
    }

    /**
     * 未测
     * 
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 为数字0表示无下划线；为数字1表示单下划线；为数字2表示删除线；为数字3表示下划线和删除线
     * 
     */
    public Integer getTextDecoration() {
        return textDecoration;
    }

    /**
     * 为数字0表示无下划线；为数字1表示单下划线；为数字2表示删除线；为数字3表示下划线和删除线
     * 
     */
    public void setTextDecoration(Integer textDecoration) {
        this.textDecoration = textDecoration;
    }

    /**
     * 未测
     * 
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * 未测
     * 
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((comparisonOperator == null) ? 0 : comparisonOperator
						.hashCode());
		result = prime * result
				+ ((condition == null) ? 0 : condition.hashCode());
		result = prime * result
				+ ((ignoreBlank == null) ? 0 : ignoreBlank.hashCode());
		result = prime * result
				+ ((inputMessage == null) ? 0 : inputMessage.hashCode());
		result = prime * result
				+ ((inputTitle == null) ? 0 : inputTitle.hashCode());
		result = prime * result
				+ ((textDecoration == null) ? 0 : textDecoration.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Validator other = (Validator) obj;
		if (comparisonOperator == null) {
			if (other.comparisonOperator != null)
				return false;
		} else if (!comparisonOperator.equals(other.comparisonOperator))
			return false;
		if (condition == null) {
			if (other.condition != null)
				return false;
		} else if (!condition.equals(other.condition))
			return false;
		if (ignoreBlank == null) {
			if (other.ignoreBlank != null)
				return false;
		} else if (!ignoreBlank.equals(other.ignoreBlank))
			return false;
		if (inputMessage == null) {
			if (other.inputMessage != null)
				return false;
		} else if (!inputMessage.equals(other.inputMessage))
			return false;
		if (inputTitle == null) {
			if (other.inputTitle != null)
				return false;
		} else if (!inputTitle.equals(other.inputTitle))
			return false;
		if (textDecoration == null) {
			if (other.textDecoration != null)
				return false;
		} else if (!textDecoration.equals(other.textDecoration))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public Boolean getIgnoreBlank() {
		return ignoreBlank;
	}

	public void setIgnoreBlank(Boolean ignoreBlank) {
		this.ignoreBlank = ignoreBlank;
	}

	public String getInputTitle() {
		return inputTitle;
	}

	public void setInputTitle(String inputTitle) {
		this.inputTitle = inputTitle;
	}

	public String getInputMessage() {
		return inputMessage;
	}

	public void setInputMessage(String inputMessage) {
		this.inputMessage = inputMessage;
	}

}
