package com.yusys.bione.plugin.spreadjs.entity.serialize;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.yusys.bione.plugin.spreadjs.entity.DataTable;
import com.yusys.bione.plugin.spreadjs.entity.DataTableProperty;

public class DataTableSerialize implements ObjectSerializer, ObjectDeserializer {

	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
			throws IOException {
		JSON.writeJSONString(serializer.getWriter(), ((DataTable)object).getAdditionalProperties());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		TypeReference<LinkedHashMap<String, DataTableProperty>> typeReference = new TypeReference<LinkedHashMap<String, DataTableProperty>>(){};
		DataTable dataTable = new DataTable();
		dataTable.setAdditionalProperties((Map<String, DataTableProperty>)parser.parseObject(typeReference.getType()));
		return (T)dataTable;
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}
