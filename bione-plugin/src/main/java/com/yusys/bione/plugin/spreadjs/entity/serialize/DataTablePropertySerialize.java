package com.yusys.bione.plugin.spreadjs.entity.serialize;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.yusys.bione.plugin.spreadjs.entity.DataTableProperty;
import com.yusys.bione.plugin.spreadjs.entity.DataTablePropertyProperty;

public class DataTablePropertySerialize implements ObjectSerializer, ObjectDeserializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
			throws IOException {
		JSON.writeJSONString(serializer.getWriter(), ((DataTableProperty)object).getAdditionalProperties());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		TypeReference<LinkedHashMap<String, Object>> typeReference = new TypeReference<LinkedHashMap<String, Object>>(){};
		Map<String, Object> additionalProperties = (Map<String, Object>)parser.parseObject(typeReference.getType());
		for (Iterator<Entry<String, Object>> it = additionalProperties.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, Object> entry = it.next();
			if (entry.getValue() instanceof JSONObject) {
				entry.setValue(JSON.parseObject(entry.getValue().toString(), DataTablePropertyProperty.class));
			}
		}
		DataTableProperty dataTableProperty = new DataTableProperty();
		dataTableProperty.setAdditionalProperties(additionalProperties);
		return (T)dataTableProperty;
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}
