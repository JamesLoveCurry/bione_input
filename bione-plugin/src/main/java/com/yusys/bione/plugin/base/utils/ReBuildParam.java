package com.yusys.bione.plugin.base.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class ReBuildParam {
	public static List<List<String>> toDbList(List<String> param) {
		if (param == null) {
			return null;
		}
		List<List<String>> list = Lists.newArrayList();
		if (param.size() > 1000) {
			int index = 0;
			int remain = param.size();
			while (remain > 1000) {
				list.add(param.subList(index, index+1000));
				index += 1000;
				remain -= 1000;
			}
			list.add(param.subList(index, param.size()));
		} else {
			list.add(param);
		}
		return list;
	}
	
	public static List<List<?>> splitLists(List<?> lists){
		List<List<?>> rptIdsParam = new ArrayList<List<?>>();
		int count = lists.size()/1000 + (lists.size() % 1000 == 0 ? 0 : 1);//rptList以1000为单位分割
		for(int i=0;i<count;i++){
			rptIdsParam.add(lists.subList(i * 1000, ((i+1) * 1000 > lists.size() ? lists.size() : (i + 1) * 1000)));
		}
		return rptIdsParam;
	}
}
