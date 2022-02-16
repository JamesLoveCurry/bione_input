package com.yusys.bione.frame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class SplitStringBy1000 {

	/**
	 * @Title: change
	 * @Description: 分割list，以1000为单位，防止数据操作数据量太大超过界限
	 * @param rptList
	 * @return List<List<String>>  
	 * @throws
	 */
	public static List<List<String>> change(List<String> rptList){
		List<List<String>> rptIdsParam = new ArrayList<List<String>>();
		int count = rptList.size()/1000 + (rptList.size() % 1000 == 0 ? 0 : 1);//rptList以1000为单位分割
		for(int i=0;i<count;i++){
			rptIdsParam.add(rptList.subList(i * 1000, ((i+1) * 1000 > rptList.size() ? rptList.size() : (i + 1) * 1000)));
		}
		return rptIdsParam;
	}
	
	/**
	 * @Title: change
	 * @Description: 分割list，以Num为单位，防止数据操作数据量太大超过界限
	 * @param rptList
	 * @param changeNum
	 * @return List<List<String>>  
	 * @throws
	 */
	public static List<List<String>> changeByNum(List<String> rptList, int changeNum){
		List<List<String>> rptIdsParam = new ArrayList<List<String>>();
		int count = rptList.size()/changeNum + (rptList.size() % changeNum == 0 ? 0 : 1);//rptList以Num为单位分割
		for(int i=0;i<count;i++){
			rptIdsParam.add(rptList.subList(i * changeNum, ((i+1) * changeNum > rptList.size() ? rptList.size() : (i + 1) * changeNum)));
		}
		return rptIdsParam;
	}
	
	public static List<List<Map<String, Object>>> changeMapList(List<Map<String, Object>> list){
		List<List<Map<String, Object>>> list500 = new ArrayList<List<Map<String, Object>>>();
		int count = list.size()/500 + (list.size() % 500 == 0 ? 0 : 1);//rptList以1000为单位分割
		for(int i=0;i<count;i++){
			list500.add(list.subList(i * 500, ((i+1) * 500 > list.size() ? list.size() : (i + 1) * 500)));
		}
		return list500;
	}
}
