package com.yusys.biapp.input.index.util;

import java.util.Map;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.utils.RandomUtils;

public class DataInputExportUtil {

	private static DataInputExportUtil dataInputExportUtil ;
	
	private static Map<String,String>cacheMap = Maps.newHashMap();
	
	private DataInputExportUtil() {
	}

	public static DataInputExportUtil  getInstance(){
		if(dataInputExportUtil == null)
			dataInputExportUtil = new DataInputExportUtil();
		
		return dataInputExportUtil;
	}
	
	
	public static String saveCacheInfo(String cacheInfo){
		String key = RandomUtils.uuid2();
		cacheMap.put(key, cacheInfo);
		return key;
	}
	
	public static String getAndRemoveCacheInfo(String key){
		String value =  cacheMap.remove(key);
		return value;
	}
	
	
}
