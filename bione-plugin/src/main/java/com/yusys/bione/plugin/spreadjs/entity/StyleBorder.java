
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 当样式包含边框时，才有此对象
 * 
 */
public class StyleBorder {

    /**
     * 颜色的二进制值 eg:'#000000'
     * 
     */
    private String color;
    /**
     * 
     * 
     */
    private Integer style;

    /**
     * 颜色的二进制值 eg:'#000000'
     * 
     */
    public String getColor() {
        return color;
    }

    /**
     * 颜色的二进制值 eg:'#000000'
     * 
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * 
     * 
     */
    public Integer getStyle() {
        return style;
    }

    /**
     * 
     * 
     */
    public void setStyle(Integer style) {
        this.style = style;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + ((style == null) ? 0 : style.hashCode());
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
		StyleBorder other = (StyleBorder) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (style == null) {
			if (other.style != null)
				return false;
		} else if (!style.equals(other.style))
			return false;
		return true;
	}

}
