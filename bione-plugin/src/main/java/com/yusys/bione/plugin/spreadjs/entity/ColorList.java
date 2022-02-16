
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ColorList {

    /**
     * a
     * 
     */
    private Integer a;
    /**
     * r
     * 
     */
    private Integer r;
    /**
     * g
     * 
     */
    private Integer g;
    /**
     * b
     * 
     */
    private Integer b;

    /**
     * a
     * 
     */
    public Integer getA() {
        return a;
    }

    /**
     * a
     * 
     */
    public void setA(Integer a) {
        this.a = a;
    }

    /**
     * r
     * 
     */
    public Integer getR() {
        return r;
    }

    /**
     * r
     * 
     */
    public void setR(Integer r) {
        this.r = r;
    }

    /**
     * g
     * 
     */
    public Integer getG() {
        return g;
    }

    /**
     * g
     * 
     */
    public void setG(Integer g) {
        this.g = g;
    }

    /**
     * b
     * 
     */
    public Integer getB() {
        return b;
    }

    /**
     * b
     * 
     */
    public void setB(Integer b) {
        this.b = b;
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
