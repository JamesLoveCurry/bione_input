
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DataArrayProperty {

    /**
     * 样式名称
     * 
     */
    private String style;

    /**
     * 样式名称
     * 
     */
    public String getStyle() {
        return style;
    }

    /**
     * 样式名称
     * 
     */
    public void setStyle(String style) {
        this.style = style;
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
