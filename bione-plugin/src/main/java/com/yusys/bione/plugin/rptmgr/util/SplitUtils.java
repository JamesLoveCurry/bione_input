package com.yusys.bione.plugin.rptmgr.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

public class SplitUtils {
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
	
	public static List<List<Object>> toObjDbList(List<Object> param) {
		if (param == null) {
			return null;
		}
		List<List<Object>> list = Lists.newArrayList();
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
		List<List<?>> newlists=new ArrayList<List<?>>();
		int index=lists.size()/1000;
		for(int i=0;i<=index;i++){
			List<?> list = lists.subList(i*1000, (i*1000+1000)>=lists.size()?lists.size():(i*1000+1000));
			if(list.size()>0)
				newlists.add(list);
		}
		return newlists;
	}
	
	public static String splitListSql(List<?> lists,Map<String,Object> params,String name,String pName){
		String jql = "( ";
		List<List<?>> newlists = SplitUtils.splitLists(lists);
		int i = 0;
		for(List<?> newlist : newlists){
			jql += " "+name+" in (:"+pName+i+")";
			params.put(pName+i, newlist);
			if(i < newlists.size() -1){
				jql += " or ";
			}
			else{
				jql += " ) ";
			}
			i++;
		}
		return jql;
	}
	
	public static final int ORACLE_SPLIT_SIZE = 1000;

	public static <T> List<List<T>> splitList(List<T> list, int eachSize) {
		if (eachSize < 1) {
			throw new IllegalArgumentException("eachSize less than 1");
		}
		int size = list.size();
		if (size > 0) {
			int outSize = size % eachSize == 0 ? size / eachSize : size
					/ eachSize + 1;
			List<List<T>> outList = new ArrayList<List<T>>(outSize);
			for (int start = 0, end;; start = end) {
				end = start + eachSize;
				if (end < size) {
					outList.add(list.subList(start, end));
				} else {
					outList.add(list.subList(start, size));
					return outList;
				}
			}
		}
		return Collections.<List<T>> emptyList();
	}

	public static <T> void processSplitList(String fieldName,
			List<? extends List<T>> splitList, StringBuilder sqlBuilder,
			List<? super T> paramList) {
		int listSize = splitList.size();
		if (listSize < 1) {
			sqlBuilder.append("1!=1");
			return;
		}
		if (listSize == 1) {
			addFieldToSql(fieldName, splitList, 0, sqlBuilder, paramList);
			return;
		}
		sqlBuilder.append("(");
		for (int i = 0;;) {
			addFieldToSql(fieldName, splitList, i, sqlBuilder, paramList);
			if (++i < listSize) {
				sqlBuilder.append(" or ");
			} else {
				sqlBuilder.append(")");
				return;
			}
		}
	}

	private static <T> void addFieldToSql(String fieldName,
			List<? extends List<T>> splitList, int index,
			StringBuilder sqlBuilder, List<? super T> paramList) {
		List<T> innerlist = splitList.get(index);
		sqlBuilder.append(fieldName).append(" in (");
		appendHolder(sqlBuilder, innerlist.size());
		sqlBuilder.append(")");
		paramList.addAll(innerlist);
	}

	private static void appendHolder(StringBuilder sqlBuilder, int innerSize) {
		if (innerSize > 0) {
			for (int i = 0;;) {
				sqlBuilder.append("?");
				if (++i < innerSize) {
					sqlBuilder.append(",");
				} else {
					return;
				}
			}
		}
	}

	public static <T> void processSplitList(String fieldName, String holderPrefix, List<? extends List<T>> splitList,
			StringBuilder sqlBuilder, Map<? super String, ? super List<T>> paramMap) {
		int listSize = splitList.size();
		if(listSize < 1){
			sqlBuilder.append("1!=1");
			return;
		}
		if(listSize == 1){
			sqlBuilder.append(fieldName).append(" in (:").append(holderPrefix).append(")");
			paramMap.put(holderPrefix, splitList.get(0));
			return;
		}
		sqlBuilder.append("(");
		for(int i = 0;;){
			String holderName = holderPrefix + i;
			sqlBuilder.append(fieldName).append(" in (:").append(holderName).append(")");
			paramMap.put(holderName, splitList.get(i));
			if(++i < listSize){
				sqlBuilder.append(" or ");
			}else{
				sqlBuilder.append(")");
				return;
			}
		}
	}
}
