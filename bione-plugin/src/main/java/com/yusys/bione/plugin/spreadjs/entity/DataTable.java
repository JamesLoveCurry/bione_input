
package com.yusys.bione.plugin.spreadjs.entity;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.alibaba.fastjson.annotation.JSONType;
import com.yusys.bione.plugin.spreadjs.entity.serialize.DataTableSerialize;

/**
 * 数据表单
 * 
 */
@JSONType(serializer=DataTableSerialize.class, deserializer=DataTableSerialize.class)
public class DataTable {

    private Map<String, DataTableProperty> additionalProperties = new LinkedHashMap<String, DataTableProperty>();

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

    public Map<String, DataTableProperty> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, DataTableProperty value) {
        this.additionalProperties.put(name, value);
    }

	public void setAdditionalProperties(Map<String, DataTableProperty> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}
}
