package com.yusys.biapp.input.task.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class JsonUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object setProperties( Class c,JSONObject jn){
		try{
			Object obj = c.newInstance();
			Field[] fields = c.getDeclaredFields();
			for(Field f :fields){
				String fName =f.getName() ;
				try{
					String value = jn.getString(fName);
					if(fName.endsWith("Date"))
						value = StringUtils.remove(StringUtils.remove(value, '-'), ' ');
					Method m=c.getDeclaredMethod("set"+fName.substring(0, 1).toUpperCase() +fName.substring(1),f.getType());
					if(f.getType().getName() .equals("java.lang.Integer") ){
						m.invoke(obj,new Integer( value));
					}else
						m.invoke(obj,value);
				}catch(Exception ex)
				{
					continue;
				}
				
				
			}
			return obj;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return null;
	}
}
