
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 公共的行宽，列高对象
 * 
 */
public class SheetSize {

    /**
     * 单位为像素
     * 
     */
    private Double size;
    /**
     * 未测
     * 
     */
    private Boolean dirty;
    
    /**
     * 单位为像素
     * 
     */
    private Boolean resizable;
    
    /**
     * 单位为像素
     * 
     */
    private Boolean visible;

    /**
     * 单位为像素
     * 
     */
    public Double getSize() {
        return size;
    }

    /**
     * 单位为像素
     * 
     */
    public void setSize(Double size) {
        this.size = size;
    }

    /**
     * 未测
     * 
     */
    public Boolean getDirty() {
        return dirty;
    }

    /**
     * 未测
     * 
     */
    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
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

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}
    
   	public Boolean getResizable() {
   		return resizable;
   	}

   	public void setResizable(Boolean resizable) {
   		this.resizable = resizable;
   	}
}
