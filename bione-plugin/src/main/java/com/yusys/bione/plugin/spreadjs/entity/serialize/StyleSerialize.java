package com.yusys.bione.plugin.spreadjs.entity.serialize;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.yusys.bione.plugin.spreadjs.entity.Style;

public class StyleSerialize implements ObjectDeserializer {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
		try {
			return (T)parser.parseObject(Style.class);
		} catch (JSONException e) {
			return (T)parser.parseObject(String.class);
		}
	}

	@Override
	public int getFastMatchToken() {
		return 0;
	}
}
