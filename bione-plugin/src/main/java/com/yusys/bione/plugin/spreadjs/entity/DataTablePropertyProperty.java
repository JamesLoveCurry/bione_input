
package com.yusys.bione.plugin.spreadjs.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.alibaba.fastjson.annotation.JSONField;
import com.yusys.bione.plugin.spreadjs.entity.serialize.StyleSerialize;

/**
 * 数据表单单元格
 * 
 */
public class DataTablePropertyProperty {

    /**
     * 单元格value值
     * 
     */
    private Object value;
    /**
     * 单元格样式的名称或样式对象
     * 
     */
    @JSONField(deserializeUsing=StyleSerialize.class)
    private Object style;
    /**
     * 单元格公式
     * 
     */
    private String formula;
    
    /**
     * 单元格标签
     * 
     */
    private String tag;

    /**
     * 单元格value值
     * 
     */
    public Object getValue() {
        return value;
    }

    /**
     * 单元格value值
     * 
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 单元格样式的名称或样式对象
     * 
     */
    public Object getStyle() {
        return style;
    }

    /**
     * 单元格样式的名称或样式对象
     * 
     */
    public void setStyle(Object style) {
        this.style = style;
    }

    /**
     * 单元格公式
     * 
     */
    public String getFormula() {
        return formula;
    }

    /**
     * 单元格公式
     * 
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }
    
    /**
    * 注释
    * 
    */
   public String getTag() {
       return tag;
   }

   /**
    * 注释
    * 
    */
   public void setTag(String tag) {
       this.tag = tag;
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
