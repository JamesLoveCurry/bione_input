package com.yusys.bione.plugin.idxana.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
/**
 * 管驾取数工具函数
 * 样例：
 *   指标概要：
 *     请求报文：
 *     {"QueryType":"index","DimNo":["DATE","ORG","CURRENCY"],"Colums":[{"ColumNo":"column0","IndexNo":"ps001","SortType":"03","SearchArg":[{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]},{"DimNo":"CURRENCY","Op":"=","Value":["156"]}]},{"ColumNo":"column1","IndexNo":"ps001","SearchArg":[{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]},{"DimNo":"CURRENCY","Op":"=","Value":["156"]}],"Calculation":{"Formula":"Yesterday('ps001')"}},{"ColumNo":"column2","IndexNo":"ps001","SearchArg":[{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]},{"DimNo":"CURRENCY","Op":"=","Value":["156"]}],"Calculation":{"Formula":"ThisMonthFirst('ps001')"}},{"ColumNo":"column3","IndexNo":"ps001","SearchArg":[{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]},{"DimNo":"CURRENCY","Op":"=","Value":["156"]}],"Calculation":{"Formula":"ThisYearFirst('ps001')"}},{"ColumNo":"column4","IndexNo":"ps001","SearchArg":[{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]},{"DimNo":"CURRENCY","Op":"=","Value":["156"]}],"Calculation":{"Formula":"LastYear('ps001')"}},{"ColumNo":"column5","IndexNo":"ps001","SearchArg":[{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]},{"DimNo":"CURRENCY","Op":"=","Value":["156"]}],"Calculation":{"Formula":"A('ps001', 'Y', 'Y')"}}]}
 *	      返回报文：
 *	   {"Code":"0000","Msg":[{"rtype":"01","$DATE":"20140831","DATE":"2014年08月31日","$ORG":"701100","ORG":"全行","$CURRENCY":"156","CURRENCY":"人民币","column5":"1315481.55646","column2":"5086990.39","column1":"5159015.61","column4":"4138040.27","column0":"5228120.87","column3":"4515764.06"}]}
 * 
 * @author YYF
 *
 */
public class FetchDataUtils {
	/**
	 * 返回结果报文的拼接取数报文函数
	 * @param FetchType 取数类型（01 指标、02 报表）
	 * @param QueryNo 编号（指标id、报表id）
	 * @param DimMap 维度集合（Map<维度类型,维度值>）
	 * @param FormulaMap 计算公式（Map<公式编号,公式>）
	 * @return String 返回结果报文
	 */
	public static String FetchData(String FetchType,String QueryNo,String StartDate,String EndDate,Map<String,List<String>> DimMap,Map<String,String> FormulaMap){
		List<String> Dimlist = new ArrayList<String>();
		List<String> Formulakeylist = new ArrayList<String>();
		List<String> Formulalist = new ArrayList<String>();
		Map<String,String> FormulakeyMap = new HashMap<String, String>();
		Object obj;
		String FetchNo = "";
		String returnJson = "";
		String Message = "{";
		if("01".equals(FetchType)){
			FetchType = "index";
			FetchNo = "IndexNo";
		}else if("02".equals(FetchType)){
			
		}
		Message = Message + '"' + "QueryType" + '"' + ":" +'"' + FetchType + '"' + ",";
		String DimMessage = "";
		String DimString = "[";
		Set<Map.Entry<String, List<String>>> Dimset = DimMap.entrySet();         
		Iterator<Map.Entry<String, List<String>>> DimIterator = Dimset.iterator();         
		while(DimIterator.hasNext()){

		     Dimlist.add(DimIterator.next().getKey());
		}
		if(FormulaMap != null && !FormulaMap.isEmpty()){
			Set<Map.Entry<String, String>> Formulaset = FormulaMap.entrySet();         
			Iterator<Map.Entry<String, String>> FormulaIterator = Formulaset.iterator();         
			while(FormulaIterator.hasNext()){     

			     Formulakeylist.add(FormulaIterator.next().getKey());
			}
			for(int a = 0;a<Formulakeylist.size();a++){
				Formulalist.add(FormulaMap.get(Formulakeylist.get(a)));
			}
		}
		
		if(!(("".equals(StartDate) || StartDate == null) || ("".equals(EndDate) || EndDate == null))){
			 for(int z = 0;z<Dimlist.size();z++){
				 if(z==0){
					 DimString = DimString + '"'+ Dimlist.get(z) +'"';
					 if(Dimlist.get(z).equals("DATE")){
						 continue;
					 }
					 DimMessage =DimMessage + "{" +'"'+ "DimNo" +'"'+":"+'"'+ Dimlist.get(z) +'"'+","+'"'+ "Op" +'"'+":"+'"'+ "=" +'"'+","+'"'+ "Value" +'"'+":"+ DimMap.get(Dimlist.get(z)) +"}";
				 }else{
					 DimString = DimString + "," + '"'+ Dimlist.get(z) +'"';
					 if(Dimlist.get(z).equals("DATE")){
						 continue;
					 }
					 DimMessage =DimMessage + "," + "{" +'"'+ "DimNo" +'"'+":"+'"'+ Dimlist.get(z) +'"'+","+'"'+ "Op" +'"'+":"+'"'+ "=" +'"'+","+'"'+ "Value" +'"'+":"+ DimMap.get(Dimlist.get(z)) +"}";
				 }
			 }
			DimString = DimString + "]";
			Message =  Message + '"' + "DimNo" + '"' + ":" + DimString + "," + '"' + "StartDate" + '"' + ":" + '"' + StartDate + '"' + "," + '"' + "EndDate" + '"' + ":" + '"' + EndDate + '"' + "," + '"' + "Colums" + '"' + ":[";
		}else{
			 for(int z = 0;z<Dimlist.size();z++){
				 if(z==0){
					 DimString = DimString + '"'+ Dimlist.get(z) +'"';
					 DimMessage =DimMessage + "{" +'"'+ "DimNo" +'"'+":"+'"'+ Dimlist.get(z) +'"'+","+'"'+ "Op" +'"'+":"+'"'+ "=" +'"'+","+'"'+ "Value" +'"'+":"+ DimMap.get(Dimlist.get(z)) +"}";
				 }else{
					 DimString = DimString + "," + '"'+ Dimlist.get(z) +'"';
					 DimMessage =DimMessage + "," + "{" +'"'+ "DimNo" +'"'+":"+'"'+ Dimlist.get(z) +'"'+","+'"'+ "Op" +'"'+":"+'"'+ "=" +'"'+","+'"'+ "Value" +'"'+":"+ DimMap.get(Dimlist.get(z)) +"}";
				 }
			 }
			DimString = DimString + "]";
			Message = Message + '"' + "DimNo" + '"' + ":" + DimString + "," + '"' + "Colums" + '"' + ":[";
		}
		for(int j = 0;j<=Formulalist.size();j++){
			 if(j==0){
		    	 Message = Message + "{" + '"' +"ColumNo" + '"' + ":" + '"' +"column" + j + '"' + "," + '"' + FetchNo + '"' + ":" +'"'+ QueryNo +'"'+ ","+'"'+ "SortType" +'"' + ":" +'"'+ "03" +'"'+","+'"'+ "SearchArg" +'"' +":["+DimMessage + "]}";   
		}else{
			FormulakeyMap.put("column"+j,Formulakeylist.get(j-1));
			 Message = Message + ",{" + '"' +"ColumNo" + '"' + ":" + '"' +"column" + j + '"' + "," + '"' + FetchNo + '"' + ":" +'"'+ QueryNo +'"'+ ","+'"'+ "SearchArg" +'"' +":["+DimMessage +"]," +'"'+ "Calculation" +'"'+":{"+'"'+ "Formula" +'"'+":"+'"'+ FormulaMessage(Formulalist.get(j-1),QueryNo) +'"'+"}}";   
			 }	    	 
		}
		Message = Message + "]}";
		try {
			System.out.println(Message);
			obj = CommandRemote.sendSync(Message,
					CommandRemoteType.QUERY);
			if(obj instanceof String){
				returnJson = obj.toString();
			}else{
				return "查询数据超时异常";
			}
			Map<String, Object> jsonMap = JSON.parseObject(returnJson);
			String code = jsonMap.get("Code").toString();
			if(code.equals("0000")){

			}			
		} catch (Throwable e) {
			e.printStackTrace();
			return "查询数据超时异常";
		}
		return returnJson;
	}
	
	/**
	 * 返回指定维度key的取数报文函数
	 * @param idxList 编号（指标id）
	 * @param SortType 排序方式（02升序、03降序）
	 * @param displayNow 是否取指标当前值
	 * @param StartDate 开始日期（不需要时候传null）
	 * @param EndDate 结束日期（不需要时候传null）
	 * @param DimMap 维度集合（Map<维度类型,维度值>）
	 * @param FormulaMap 计算公式（Map<公式编号,公式>）
	 * @param ReturnKey 返回Map的key的属性（返回Map按照什么维度分类）
	 * @param dataUnit 数据单位（01：元，02：百元，03：千元，04：万元，05：亿元，06：个，07：百个，08：千个，09：万个，10：亿个）
	 * @param dataAccuracy 数据精度（0，1，2，3，4）
	 * @param isCache 是否走缓存
	 * @return ReturnMap 返回Map（Map<指定维度<指标值+公式编号,数值>>）
	 * 
	 * 多指标多公式请求报文示例：{"QueryType":"index","DimNo":["CURRENCY","DATE","ORG"],"Colums":[{"ColumNo":"ps001","IndexNo":"ps001","SortType":"03","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}]},{"ColumNo":"ps0011","IndexNo":"ps001","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}],"Calculation":{"Formula":"Yesterday('ps001')"}},{"ColumNo":"PS10001","IndexNo":"PS10001","SortType":"03","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}]},{"ColumNo":"PS100011","IndexNo":"PS10001","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}],"Calculation":{"Formula":"Yesterday('PS10001')"}}]}
     *
     * 多指标多公式返回报文示例：{"Code":"0000","Msg":[{"rtype":"01","$CURRENCY":"156","CURRENCY":"人民币","$DATE":"20140831","DATE":"2014年08月31日","$ORG":"701100","ORG":"全行","ps0011":"3975268.17","PS10001":"76104073730.92","ps001":"8199728.32"}]}
	 * 
	 * 返回map示例：{20140831={PS10001=76104073730.92, PS100011=0, ps0011=3975268.17, ps001=8199728.32}}
	 */
	@SuppressWarnings({ "unchecked"})
	public static Map<String,Map<String,String>> FetchDataMap(List<String> idxList ,String sortType ,Boolean displayNow ,String startDate ,String endDate ,Map<String,List<String>> dimMap ,Map<String,String> formulaMap ,String returnKey ,String dataUnit ,BigDecimal dataAccuracy ,Boolean isCache){
		Map<String,Object> querys = new HashMap<String, Object>();
		querys.put("QueryType", "index");
		querys.put("DimNo", dimMap.keySet());
		if(StringUtils.isNotBlank(startDate)){
			querys.put("StartDate", startDate);
		}
		if(StringUtils.isNotBlank(endDate)){
			querys.put("EndDate", endDate);
		}
		List<Map<String,Object>> columns = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> searchs = new ArrayList<Map<String,Object>>();
		for(String key : dimMap.keySet()){
			Map<String,Object> search = new HashMap<String, Object>();
			search.put("DimNo", key);
			search.put("Op", "=");
			search.put("Value", dimMap.get(key));
			searchs.add(search);
		}
		if(idxList != null && idxList.size() > 0){
			for(String idx : idxList){
				String measureNo = "";
				String indexNo = idx;
				if(idx.indexOf(".")>=0){
					String iArr[] = StringUtils.split(idx,".");
					indexNo = iArr[0];
					measureNo = iArr[1];
				}
				if(displayNow){
					Map<String,Object> column = new HashMap<String, Object>();
					column.put("ColumNo", idx);
					column.put("IndexNo", indexNo);
					if(StringUtils.isNotBlank(sortType))
						column.put("SortType", sortType);
					if(StringUtils.isNotBlank(measureNo))
						column.put("MeasureNo", measureNo);
					column.put("SearchArg", JSON.toJSONString(searchs));
					columns.add(column);
				}
				if(formulaMap != null && formulaMap.size() > 0){
					for(String key :formulaMap.keySet()){
						Map<String,Object> column = new HashMap<String, Object>();
						column.put("ColumNo", idx+key);
						column.put("IndexNo", indexNo);
						if(StringUtils.isNotBlank(sortType))
							column.put("SortType", sortType);
						if(StringUtils.isNotBlank(measureNo))
							column.put("MeasureNo", measureNo);
						column.put("SearchArg", JSON.toJSONString(searchs));
						Map<String,Object> calculation = new HashMap<String, Object>();
						calculation.put("Formula", StringUtils.replace(formulaMap.get(key), "$1", idx));
						column.put("Calculation", calculation);
						columns.add(column);
					}
				}
			}
		}
		querys.put("Colums", columns);
		String message = JSON.toJSONString(querys);
		Map<String,Map<String,String>> returnMap = new LinkedHashMap<String, Map<String,String>>();//返回结果map
		Object obj = null;
		String returnJson = "";
		String dataKey = "";
		int circulation = 0;
		dataKey = (String) EhcacheUtils.get(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE_KEY, message);
		while(dataKey != null && "1".equals(dataKey) && (returnJson == null || "".equals(returnJson)) && (circulation < 300)){
			try {
				Thread.sleep(1000);
				returnJson = (String) EhcacheUtils.get(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE, message);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			circulation++;
		}
		if(StringUtils.isBlank(returnJson) || !isCache){
			try {
				System.out.println("【请求报文明细】： "+message);
				EhcacheUtils.put(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE_KEY,message,"1");
				obj = CommandRemote.sendSync(message,
						CommandRemoteType.QUERY);
				if(obj instanceof String){
					returnJson = obj.toString();
					EhcacheUtils.put(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE,message,returnJson);
					System.out.println("【返回报文明细】："+returnJson);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		try{
			Map<String, Object> jsonMap = JSON.parseObject(returnJson);
			String code = jsonMap.get("Code").toString();
			if(code.equals("0000")){
				List<Map<String, String>> msg =(List<Map<String, String>>) jsonMap.get("Msg");
				for(int b = 0 ;b<msg.size();b++){
					Map<String,String> returnFormulaMap = new LinkedHashMap<String, String>();
					Map<String,String> msgMap = msg.get(b);
					for(int i = 0;i<idxList.size();i++){
						if(displayNow){
							BigDecimal bdpai = new BigDecimal(0);
							if(msgMap.get(idxList.get(i)) != null){
								try{
									bdpai = new BigDecimal(msgMap.get(idxList.get(i))).divide(getdataChange(dataUnit));
								}
								catch(Exception e){
								}
								if(null != dataAccuracy) {
									returnFormulaMap.put(idxList.get(i), bdpai.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP).toString());
								}else {
									returnFormulaMap.put(idxList.get(i), bdpai.toString());
								}
							}else{
								returnFormulaMap.put(idxList.get(i), "0");
							}
						}
							
						if(formulaMap != null && formulaMap.size() > 0){
							for(String key : formulaMap.keySet()){
								BigDecimal bdpai = new BigDecimal(0);
								if(msgMap.get(idxList.get(i) + key) != null){
									try{
										bdpai = new BigDecimal(msgMap.get(idxList.get(i) + key)).divide(getdataChange(dataUnit));
									}
									catch(Exception e){
									}
									if(null != dataAccuracy) {
										returnFormulaMap.put(idxList.get(i) + key, bdpai.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP).toString());
									}else {
										returnFormulaMap.put(idxList.get(i) + key, bdpai.toString());
									}
								}else{
									returnFormulaMap.put(idxList.get(i) + key, "0");
								}
							}
						}
					}
					returnMap.put(msgMap.get("$"+returnKey), returnFormulaMap);
				}
			}		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * 返回指定维度key的取数报文函数(可选择是否使用同一单位)
	 * @param idxList 编号（指标id）
	 * @param SortType 排序方式（02升序、03降序）
	 * @param displayNow 是否取指标当前值
	 * @param StartDate 开始日期（不需要时候传null）
	 * @param EndDate 结束日期（不需要时候传null）
	 * @param DimMap 维度集合（Map<维度类型,维度值>）
	 * @param FormulaMap 计算公式（Map<公式编号,公式>）
	 * @param ReturnKey 返回Map的key的属性（返回Map按照什么维度分类）
	 * @param dataUnit 数据单位（01：元，02：百元，03：千元，04：万元，05：亿元，06：个，07：百个，08：千个，09：万个，10：亿个）
	 * @param dataAccuracy 数据精度（0，1，2，3，4）
	 * @param isFormulaUnit 公式是否使用同一单位
	 * @param formulaUnit 公式使用的单位
	 * @param isCache 是否走缓存
	 * @return ReturnMap 返回Map（Map<指定维度<指标值+公式编号,数值>>）
	 * 
	 * 多指标多公式请求报文示例：{"QueryType":"index","DimNo":["CURRENCY","DATE","ORG"],"Colums":[{"ColumNo":"ps001","IndexNo":"ps001","SortType":"03","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}]},{"ColumNo":"ps0011","IndexNo":"ps001","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}],"Calculation":{"Formula":"Yesterday('ps001')"}},{"ColumNo":"PS10001","IndexNo":"PS10001","SortType":"03","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}]},{"ColumNo":"PS100011","IndexNo":"PS10001","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}],"Calculation":{"Formula":"Yesterday('PS10001')"}}]}
     *
     * 多指标多公式返回报文示例：{"Code":"0000","Msg":[{"rtype":"01","$CURRENCY":"156","CURRENCY":"人民币","$DATE":"20140831","DATE":"2014年08月31日","$ORG":"701100","ORG":"全行","ps0011":"3975268.17","PS10001":"76104073730.92","ps001":"8199728.32"}]}
	 * 
	 * 返回map示例：{20140831={PS10001=76104073730.92, PS100011=0, ps0011=3975268.17, ps001=8199728.32}}
	 */
	@SuppressWarnings({ "unchecked"})
	public static Map<String,Map<String,String>> FetchDataMap(List<String> idxList ,String sortType ,Boolean displayNow ,String startDate ,String endDate ,Map<String,List<String>> dimMap ,Map<String,String> formulaMap ,String returnKey ,String dataUnit ,BigDecimal dataAccuracy ,Boolean isFormulaUnit ,Map<String,String> formulaUnit, Boolean isCache){
		Map<String,Object> querys = new HashMap<String, Object>();
		querys.put("QueryType", "index");
		querys.put("DimNo", dimMap.keySet());
		if(StringUtils.isNotBlank(startDate)){
			querys.put("StartDate", startDate);
		}
		if(StringUtils.isNotBlank(endDate)){
			querys.put("EndDate", endDate);
		}
		List<Map<String,Object>> columns = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> searchs = new ArrayList<Map<String,Object>>();
		for(String key : dimMap.keySet()){
			Map<String,Object> search = new HashMap<String, Object>();
			search.put("DimNo", key);
			search.put("Op", "=");
			search.put("Value", dimMap.get(key));
			searchs.add(search);
		}
		if(idxList != null && idxList.size() > 0){
			for(String idx : idxList){
				String measureNo = "";
				String indexNo = idx;
				if(idx.indexOf(".")>=0){
					String iArr[] = StringUtils.split(idx,".");
					indexNo = iArr[0];
					measureNo = iArr[1];
				}
				if(displayNow){
					Map<String,Object> column = new HashMap<String, Object>();
					column.put("ColumNo", idx);
					column.put("IndexNo", indexNo);
					if(StringUtils.isNotBlank(sortType))
						column.put("SortType", sortType);
					if(StringUtils.isNotBlank(measureNo))
						column.put("MeasureNo", measureNo);
					column.put("SearchArg", JSON.toJSONString(searchs));
					columns.add(column);
				}
				if(formulaMap != null && formulaMap.size() > 0){
					for(String key :formulaMap.keySet()){
						Map<String,Object> column = new HashMap<String, Object>();
						column.put("ColumNo", idx+key);
						column.put("IndexNo", indexNo);
						if(StringUtils.isNotBlank(sortType))
							column.put("SortType", sortType);
						if(StringUtils.isNotBlank(measureNo))
							column.put("MeasureNo", measureNo);
						column.put("SearchArg", JSON.toJSONString(searchs));
						Map<String,Object> calculation = new HashMap<String, Object>();
						calculation.put("Formula", StringUtils.replace(formulaMap.get(key), "$1", idx));
						column.put("Calculation", calculation);
						columns.add(column);
					}
				}
			}
		}
		querys.put("Colums", columns);
		String message = JSON.toJSONString(querys);
		Map<String,Map<String,String>> returnMap = new LinkedHashMap<String, Map<String,String>>();//返回结果map
		Object obj = null;
		String returnJson = "";
		String dataKey = "";
		int circulation = 0;
		dataKey = (String) EhcacheUtils.get(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE_KEY, message);
		while(dataKey != null && "1".equals(dataKey) && (returnJson == null || "".equals(returnJson)) && (circulation < 300)){
			try {
				Thread.sleep(1000);
				returnJson = (String) EhcacheUtils.get(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE, message);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			circulation++;
		}
		if(StringUtils.isBlank(returnJson) || !isCache){
			try {
				System.out.println("【请求报文明细】： "+message);
				EhcacheUtils.put(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE_KEY,message,"1");
				obj = CommandRemote.sendSync(message,
						CommandRemoteType.QUERY);
				if(obj instanceof String){
					returnJson = obj.toString();
					EhcacheUtils.put(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE,message,returnJson);
					System.out.println("【返回报文明细】："+returnJson);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		try{
			Map<String, Object> jsonMap = JSON.parseObject(returnJson);
			String code = jsonMap.get("Code").toString();
			if(code.equals("0000")){
				List<Map<String, String>> msg =(List<Map<String, String>>) jsonMap.get("Msg");
				for(int b = 0 ;b<msg.size();b++){
					Map<String,String> returnFormulaMap = new LinkedHashMap<String, String>();
					Map<String,String> msgMap = msg.get(b);
					for(int i = 0;i<idxList.size();i++){
						if(displayNow){
							BigDecimal bdpai = new BigDecimal(0);
							if(msgMap.get(idxList.get(i)) != null){
								try{
									bdpai = new BigDecimal(msgMap.get(idxList.get(i))).divide(getdataChange(dataUnit));
								}
								catch(Exception e){
								}
								returnFormulaMap.put(idxList.get(i), bdpai.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP).toString());
							}else{
								returnFormulaMap.put(idxList.get(i), "0");
							}
						}
							
						if(formulaMap != null && formulaMap.size() > 0){
							for(String key : formulaMap.keySet()){
								BigDecimal bdpai = new BigDecimal(0);
								if(msgMap.get(idxList.get(i) + key) != null){
									try{
										if(isFormulaUnit){
											bdpai = new BigDecimal(msgMap.get(idxList.get(i) + key)).divide(getdataChange(dataUnit));
										}else{
											bdpai = new BigDecimal(msgMap.get(idxList.get(i) + key)).divide(getdataChange(formulaUnit.get(key)));
										}
									}
									catch(Exception e){
									}
									if(formulaUnit == null || !"00".equals(formulaUnit.get(key))){
										returnFormulaMap.put(idxList.get(i) + key, bdpai.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP).toString());
									}else{
										returnFormulaMap.put(idxList.get(i) + key, bdpai.toString());
									}
								}else{
									returnFormulaMap.put(idxList.get(i) + key, "0");
								}
							}
						}
					}
					returnMap.put(msgMap.get("$"+returnKey), returnFormulaMap);
				}
			}		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return returnMap;
	}
	
	
	/**
	 * 返回指定维度key的取数报文函数(指标可以替换维度,可选择是否使用同一单位)
	 * @param idxList 编号（指标id）
	 * @param SortType 排序方式（02升序、03降序）
	 * @param displayNow 是否取指标当前值
	 * @param StartDate 开始日期（不需要时候传null）
	 * @param EndDate 结束日期（不需要时候传null）
	 * @param DimMap 维度集合（Map<维度类型,维度值>）
	 * @param FormulaMap 计算公式（Map<公式编号,公式>）
	 * @param ReturnKey 返回Map的key的属性（返回Map按照什么维度分类）
	 * @param dataUnit 数据单位（01：元，02：百元，03：千元，04：万元，05：亿元，06：个，07：百个，08：千个，09：万个，10：亿个）
	 * @param dataAccuracy 数据精度（0，1，2，3，4）
	 * @param replaceDim 替换维度 （维度类型）
	 * @param replaceMap 替换维度Map（Map<指标编号,维度值>）
	 * @param isFormulaUnit 公式是否使用同一单位 
	 * @param formulaUnit 公式使用的单位 （Map<公式编号,单位>）
	 * @return ReturnMap 返回Map（Map<指定维度<指标值+公式编号,数值>>）
	 * 
	 * 多指标多公式请求报文示例：{"QueryType":"index","DimNo":["CURRENCY","DATE","ORG"],"Colums":[{"ColumNo":"ps001","IndexNo":"ps001","SortType":"03","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}]},{"ColumNo":"ps0011","IndexNo":"ps001","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}],"Calculation":{"Formula":"Yesterday('ps001')"}},{"ColumNo":"PS10001","IndexNo":"PS10001","SortType":"03","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}]},{"ColumNo":"PS100011","IndexNo":"PS10001","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}],"Calculation":{"Formula":"Yesterday('PS10001')"}}]}
     *
     * 多指标多公式返回报文示例：{"Code":"0000","Msg":[{"rtype":"01","$CURRENCY":"156","CURRENCY":"人民币","$DATE":"20140831","DATE":"2014年08月31日","$ORG":"701100","ORG":"全行","ps0011":"3975268.17","PS10001":"76104073730.92","ps001":"8199728.32"}]}
	 * 
	 * 返回map示例：{20140831={PS10001=76104073730.92, PS100011=0, ps0011=3975268.17, ps001=8199728.32}}
	 */
	@SuppressWarnings({ "unchecked"})
	public static Map<String,Map<String,String>> FetchDataMap(List<String> idxList ,String sortType ,Boolean displayNow ,String startDate ,String endDate ,Map<String,List<String>> dimMap ,Map<String,String> formulaMap ,String returnKey ,String dataUnit ,BigDecimal dataAccuracy ,String replaceDim ,Map<String,List<String>> replaceMap ,Boolean isFormulaUnit ,Map<String,String> formulaUnit){
		Map<String,Object> querys = new HashMap<String, Object>();
		querys.put("QueryType", "index");
		querys.put("DimNo", dimMap.keySet());
		if(StringUtils.isNotBlank(startDate)){
			querys.put("StartDate", startDate);
		}
		if(StringUtils.isNotBlank(endDate)){
			querys.put("EndDate", endDate);
		}
		List<Map<String,Object>> columns = new ArrayList<Map<String,Object>>();
		if(idxList != null && idxList.size() > 0){
			for(String idx : idxList){
				String measureNo = "";
				String indexNo = idx;
				if(idx.indexOf("|")>=0){
					String iArrlist[] = StringUtils.split(idx,"|");
					indexNo = iArrlist[0];
				}
				if(indexNo.indexOf(".")>=0){
					String iArr[] = StringUtils.split(indexNo,".");
					indexNo = iArr[0];
					measureNo = iArr[1];
				}
				if(displayNow){
					Map<String,Object> column = new HashMap<String, Object>();
					column.put("ColumNo", idx);
					column.put("IndexNo", indexNo);
					if(StringUtils.isNotBlank(sortType))
						column.put("SortType", sortType);
					if(StringUtils.isNotBlank(measureNo))
						column.put("MeasureNo", measureNo);
					List<Map<String,Object>> searchs = new ArrayList<Map<String,Object>>();
					for(String key : dimMap.keySet()){
						Map<String,Object> search = new HashMap<String, Object>();
						if(key.equals(replaceDim) && replaceMap.get(idx) != null && replaceMap.get(idx).size()>0){
							search.put("DimNo", replaceDim);
							search.put("Op", "=");
							search.put("Value", replaceMap.get(idx));
							searchs.add(search);
						}else{
							search.put("DimNo", key);
							search.put("Op", "=");
							search.put("Value", dimMap.get(key));
							searchs.add(search);
						}
					}
					if(!dimMap.containsKey(replaceDim)){
						if(replaceMap.get(idx) != null && replaceMap.get(idx).size()>0){
							Map<String,Object> search = new HashMap<String, Object>();
							search.put("DimNo", replaceDim);
							search.put("Op", "=");
							search.put("Value", replaceMap.get(idx));
							searchs.add(search);
						}
					}
					column.put("SearchArg", JSON.toJSONString(searchs));
					columns.add(column);
				}
				if(formulaMap != null && formulaMap.size() > 0){
					for(String key :formulaMap.keySet()){
						Map<String,Object> column = new HashMap<String, Object>();
						column.put("ColumNo", idx +key);
						column.put("IndexNo", indexNo);
						if(StringUtils.isNotBlank(sortType))
							column.put("SortType", sortType);
						if(StringUtils.isNotBlank(measureNo))
							column.put("MeasureNo", measureNo);
						List<Map<String,Object>> searchs = new ArrayList<Map<String,Object>>();
						for(String dimKey : dimMap.keySet()){
							Map<String,Object> search = new HashMap<String, Object>();
							if(dimKey.equals(replaceDim) && replaceMap.get(idx) != null && replaceMap.get(idx).size()>0){
								search.put("DimNo", replaceDim);
								search.put("Op", "=");
								search.put("Value", replaceMap.get(idx));
								searchs.add(search);
							}else{
								search.put("DimNo", dimKey);
								search.put("Op", "=");
								search.put("Value", dimMap.get(dimKey));
								searchs.add(search);
							}
						}
						column.put("SearchArg", JSON.toJSONString(searchs));
						Map<String,Object> calculation = new HashMap<String, Object>();
						calculation.put("Formula", StringUtils.replace(formulaMap.get(key), "$1", indexNo));
						column.put("Calculation", calculation);
						columns.add(column);
					}
				}
			}
		}
		querys.put("Colums", columns);
		String message = JSON.toJSONString(querys);
		Map<String,Map<String,String>> returnMap = new LinkedHashMap<String, Map<String,String>>();//返回结果map
		Object obj = null;
		String returnJson = "";
		returnJson = (String) EhcacheUtils.get(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE, message);
		if(returnJson == null){
			try {
				System.out.println("【请求报文明细】： "+message);
				obj = CommandRemote.sendSync(message,
						CommandRemoteType.QUERY);
				if(obj instanceof String){
					returnJson = obj.toString();
					EhcacheUtils.put(GlobalConstants4plugin.INDEX_ANALYSIS_MESSAGE,message,returnJson);
					System.out.println("【返回报文明细】："+returnJson);
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		try{
			Map<String, Object> jsonMap = JSON.parseObject(returnJson);
			String code = jsonMap.get("Code").toString();
			if(code.equals("0000")){
				List<Map<String, String>> msg =(List<Map<String, String>>) jsonMap.get("Msg");
				Map<String,String> returnFormulaMap = new LinkedHashMap<String, String>();
				for(int b = 0 ;b<msg.size();b++){
					Map<String,String> msgMap = msg.get(b);
					for(int i = 0;i<idxList.size();i++){
						String indexNo = idxList.get(i);
						String rpIndexNo = indexNo;
						if(displayNow){
							BigDecimal bdpai = new BigDecimal(0);
							if(msgMap.get(rpIndexNo) != null){
								try{
									bdpai = new BigDecimal(msgMap.get(rpIndexNo)).divide(getdataChange(dataUnit));
								}
								catch(Exception e){
								}
								returnFormulaMap.put(rpIndexNo, bdpai.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP).toString());
							}else{
								returnFormulaMap.put(rpIndexNo,"0");
							}
						}
						if(formulaMap != null && formulaMap.size() > 0){
							for(String key : formulaMap.keySet()){
								BigDecimal bdpai = new BigDecimal(0);
								if(msgMap.get(rpIndexNo + key) != null){
									try{
										if(isFormulaUnit){
											bdpai = new BigDecimal(msgMap.get(rpIndexNo + key)).divide(getdataChange(dataUnit));
										}else{
											bdpai = new BigDecimal(msgMap.get(rpIndexNo + key)).divide(getdataChange(formulaUnit.get(key)));
										}
									}
									catch(Exception e){
									}
									if(formulaUnit == null || !"00".equals(formulaUnit.get(key))){
										returnFormulaMap.put(rpIndexNo + key, bdpai.setScale(dataAccuracy.intValue(), BigDecimal.ROUND_HALF_UP).toString());
									}else{
										returnFormulaMap.put(rpIndexNo + key, bdpai.toString());
									}
								}else{
									returnFormulaMap.put(rpIndexNo + key,"0");
								}
							}
						}
					}
					returnMap.put(msgMap.get("$"+returnKey), returnFormulaMap);
				}
			}		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return returnMap;
	}
	
	/**
	 * 返回指定维度key的取数报文函数(不进行数据单位和精度转换)
	 * @param idxList 编号（指标id）
	 * @param SortType 排序方式（02升序、03降序）
	 * @param displayNow 是否取指标当前值
	 * @param StartDate 开始日期（不需要时候传null）
	 * @param EndDate 结束日期（不需要时候传null）
	 * @param DimMap 维度集合（Map<维度类型,维度值>）
	 * @param FormulaMap 计算公式（Map<公式编号,公式>）
	 * @param ReturnKey 返回Map的key的属性（返回Map按照什么维度分类）
	 * @return ReturnMap 返回Map（Map<指定维度<指标值+公式编号,数值>>）
	 * 
	 * 多指标多公式请求报文示例：{"QueryType":"index","DimNo":["CURRENCY","DATE","ORG"],"Colums":[{"ColumNo":"ps001","IndexNo":"ps001","SortType":"03","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}]},{"ColumNo":"ps0011","IndexNo":"ps001","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}],"Calculation":{"Formula":"Yesterday('ps001')"}},{"ColumNo":"PS10001","IndexNo":"PS10001","SortType":"03","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}]},{"ColumNo":"PS100011","IndexNo":"PS10001","SearchArg":[{"DimNo":"CURRENCY","Op":"=","Value":["156"]},{"DimNo":"DATE","Op":"=","Value":["20140831"]},{"DimNo":"ORG","Op":"=","Value":["701100"]}],"Calculation":{"Formula":"Yesterday('PS10001')"}}]}
     *
     * 多指标多公式返回报文示例：{"Code":"0000","Msg":[{"rtype":"01","$CURRENCY":"156","CURRENCY":"人民币","$DATE":"20140831","DATE":"2014年08月31日","$ORG":"701100","ORG":"全行","ps0011":"3975268.17","PS10001":"76104073730.92","ps001":"8199728.32"}]}
	 * 
	 * 返回map示例：{20140831={PS10001=76104073730.92, PS100011=0, ps0011=3975268.17, ps001=8199728.32}}
	 */
	@SuppressWarnings({ "unchecked" })
	public static Map<String,Map<String,String>> FetchDataMap(List<String> idxList ,String sortType ,Boolean displayNow ,String startDate ,String endDate ,Map<String,List<String>> dimMap ,Map<String,String> formulaMap ,String returnKey){
		Map<String,Object> querys = new HashMap<String, Object>();
		querys.put("QueryType", "index");
		querys.put("DimNo", dimMap.keySet());
		if(StringUtils.isNotBlank(startDate)){
			querys.put("StartDate", startDate);
		}
		if(StringUtils.isNotBlank(endDate)){
			querys.put("EndDate", endDate);
		}
		List<Map<String,Object>> columns = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> searchs = new ArrayList<Map<String,Object>>();
		for(String key : dimMap.keySet()){
			Map<String,Object> search = new HashMap<String, Object>();
			search.put("DimNo", key);
			search.put("Op", "=");
			search.put("Value", dimMap.get(key));
			searchs.add(search);
		}
		if(idxList != null && idxList.size() > 0){
			for(String idx : idxList){
				String measureNo = "";
				String indexNo = idx;
				if(idx.indexOf(".")>=0){
					String iArr[] = StringUtils.split(idx,".");
					indexNo = iArr[0];
					measureNo = iArr[1];
				}
				if(displayNow){
					Map<String,Object> column = new HashMap<String, Object>();
					column.put("ColumNo", indexNo);
					column.put("IndexNo", indexNo);
					if(StringUtils.isNotBlank(sortType))
						column.put("SortType", sortType);
					if(StringUtils.isNotBlank(measureNo))
						column.put("MeasureNo", measureNo);
					column.put("SearchArg", JSON.toJSONString(searchs));
					columns.add(column);
				}
				if(formulaMap != null && formulaMap.size() > 0){
					for(String key :formulaMap.keySet()){
						Map<String,Object> column = new HashMap<String, Object>();
						column.put("ColumNo", indexNo+key);
						column.put("IndexNo", indexNo);
						if(StringUtils.isNotBlank(sortType))
							column.put("SortType", sortType);
						if(StringUtils.isNotBlank(measureNo))
							column.put("MeasureNo", measureNo);
						column.put("SearchArg", JSON.toJSONString(searchs));
						Map<String,Object> calculation = new HashMap<String, Object>();
						calculation.put("Formula", StringUtils.replace(formulaMap.get(key), "$1", idx));
						column.put("Calculation", calculation);
						columns.add(column);
					}
				}
			}
		}
		querys.put("Colums", columns);
		String message = JSON.toJSONString(querys);
		Map<String,Map<String,String>> returnMap = new LinkedHashMap<String, Map<String,String>>();//返回结果map
		Object obj = null;
		String returnJson = "";
		try {
			System.out.println("【请求报文明细】： "+message);
			obj = CommandRemote.sendSync(message,
					CommandRemoteType.QUERY);
			if(obj instanceof String){
				returnJson = obj.toString();
				System.out.println("【返回报文明细】："+returnJson);
			}
			Map<String, Object> jsonMap = JSON.parseObject(returnJson);
			String code = jsonMap.get("Code").toString();
			if(code.equals("0000")){
				List<Map<String, String>> msg =(List<Map<String, String>>) jsonMap.get("Msg");
				for(int b = 0 ;b<msg.size();b++){
					Map<String,String> returnFormulaMap = new LinkedHashMap<String, String>();
					Map<String,String> msgMap = msg.get(b);
					for(int i = 0;i<idxList.size();i++){
						if(displayNow)
							returnFormulaMap.put(idxList.get(i), msgMap.get(idxList.get(i)));
						if(formulaMap != null && formulaMap.size() > 0){
							for(String key : formulaMap.keySet()){
								returnFormulaMap.put(idxList.get(i) + key, msgMap.get(idxList.get(i) + key));
							}
						}
					}
					returnMap.put(msgMap.get("$"+returnKey), returnFormulaMap);
				}
			}			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return returnMap;
	}
	
	
	/**
	 * 通用引擎取数报文拼接工具
	 * @param idxList 指标编号list
	 * @param sortType 排序方式（02升序、03降序）
	 * @param dimNo 查询维度
	 * @param searchArg 查询条件
	 * @param forMula 查询公式
	 * @return 返回报文
	 */
	public static Map<String,Object> CurrencyFetchData(List<String> idxList ,String sortType ,List<String> dimNo ,List<Map<String,Object>> searchArg ,Map<String,String> forMula ){
		Map<String,Object> returnMap = new LinkedHashMap<String, Object>();
		Map<String,Object> requestMap = new LinkedHashMap<String, Object>();
		Map<String,Object> columsMap = new LinkedHashMap<String, Object>();
		List<Map<String,Object>> columsList = new ArrayList<Map<String,Object>>();
		List<String> forList = new ArrayList<String>(); 
		Object obj;
		String returnJson = "";
		if(idxList != null && idxList.size() > 0){
			if(sortType != null && sortType.length() > 0){
				if(dimNo != null && dimNo.size() > 0){
					if(searchArg != null && searchArg.size() > 0){
						if(forMula != null){
							Set<Map.Entry<String, String>> Dimset = forMula.entrySet();         
							Iterator<Map.Entry<String, String>> DimIterator = Dimset.iterator();         
							while(DimIterator.hasNext()){
							     forList.add(DimIterator.next().getKey());
							}
						}
						requestMap.put("QueryType", "index");
						requestMap.put("DimNo", dimNo);
						if(forList != null && forList.size() > 0){
							for(String idxNo : idxList){
								columsMap = new LinkedHashMap<String, Object>();
								columsMap.put("ColumNo", idxNo);
								columsMap.put("IndexNo", idxNo);
								columsMap.put("SortType", sortType);
								columsMap.put("SearchArg", JSON.toJSONString(searchArg));
								columsList.add(columsMap);
								for(String forNo : forList){
									columsMap = new LinkedHashMap<String, Object>();
									Map<String,String> forMap = new LinkedHashMap<String, String>();
									columsMap.put("ColumNo", idxNo + forNo);
									columsMap.put("IndexNo", idxNo);
									columsMap.put("SortType", sortType);
									columsMap.put("SearchArg", JSON.toJSONString(searchArg));
									forMap.put("Formula", FormulaMessage(forMula.get(forNo),idxNo));
									columsMap.put("Calculation", forMap);
									columsList.add(columsMap);
								}
							}
						}else{
							for(String idxNo : idxList){
								columsMap = new LinkedHashMap<String, Object>();
								columsMap.put("ColumNo", idxNo);
								columsMap.put("IndexNo", idxNo);
								columsMap.put("SortType", sortType);
								columsMap.put("SearchArg", JSON.toJSONString(searchArg));
								columsList.add(columsMap);
							}
						}
						requestMap.put("Colums", columsList);
						String jsonObject = JSON.toJSONString(requestMap);
						System.out.println("【请求报文明细】： "+jsonObject);
						try {
							obj = CommandRemote.sendSync(jsonObject,
									CommandRemoteType.QUERY);
							if(obj instanceof String){
								returnJson = obj.toString();
								System.out.println("【返回报文明细】："+returnJson);
							}
							returnMap = JSON.parseObject(returnJson);
						} catch (Throwable e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return returnMap;
	}



	
	
	/**
	 * 公式编号替换
	 * @param Formula
	 * @param QueryNo
	 * @return
	 */
	
	public static String FormulaMessage (String Formula,String QueryNo){
		Formula = StringUtils.replace(StringUtils.replace(Formula, "$", QueryNo), "1", QueryNo);
		return Formula;
	}
	
	/**
	 * 根据单位进制变换
	 * @param dataUnit
	 * @return
	 */
	public static BigDecimal getdataChange(String dataUnit){
		BigDecimal dataChange = new BigDecimal(1);
		if(StringUtils.isNotBlank(dataUnit)){
			if(dataUnit.equals("00")){//百分比
				dataChange = new BigDecimal("0.01");
			}else if(dataUnit.equals("02") || dataUnit.equals("07")){
				dataChange = new BigDecimal(100);
			}else if(dataUnit.equals("03") || dataUnit.equals("08")){
				dataChange = new BigDecimal(1000);
			}else if(dataUnit.equals("04") || dataUnit.equals("09")){
				dataChange = new BigDecimal(10000);
			}else if(dataUnit.equals("05") || dataUnit.equals("10")){
				dataChange = new BigDecimal(100000000);
			}
		}
		return dataChange;	
	}
	
	/**
	 * 获取数据精度
	 * @param chartId
	 * @return
	 */
	public static BigDecimal getdataAccuracy(String dataAccuracy){
		BigDecimal Accuracy = new BigDecimal(0);
		if(dataAccuracy.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$")){
			Accuracy = new BigDecimal(dataAccuracy);
		}
		return Accuracy;	
	}
}
