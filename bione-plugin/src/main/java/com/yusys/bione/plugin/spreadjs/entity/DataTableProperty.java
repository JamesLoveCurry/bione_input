
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.alibaba.fastjson.annotation.JSONType;
import com.yusys.bione.plugin.spreadjs.entity.serialize.DataTablePropertySerialize;

/**
 * 数据表单行
 * 
 */
@JSONType(serializer=DataTablePropertySerialize.class, deserializer=DataTablePropertySerialize.class)
public class DataTableProperty {

	private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
}
