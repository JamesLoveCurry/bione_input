package com.yusys.biapp.input.index.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.biapp.input.index.util.vo.TableColumn;
import com.yusys.biapp.input.index.util.vo.TableColumnVO;

public class TableUtils {

	
	public static void generateCommonJosnString(List<TableColumnVO> vos){
		
		
		String rs = generateColumnJosnString(vos);
		// JSONObject groupJson = JSONObject.fromObject(rs);
		System.out.println(rs);
		
	}
	public static List<TableColumnVO> createVOs(){
		List<TableColumnVO> list =  createVOs(2,true);
		return list;
	}
	
	public static List<TableColumnVO> createVOs(int size,boolean appendChild ){
		List<TableColumnVO> vos = Lists.newArrayList();
		for(int i =0 ; i <size;i++){
			TableColumnVO columnVO = new TableColumnVO();
			columnVO.setAlign("left");
			if(appendChild)
			{
				columnVO.setColumns(createVOs(4,false));

			}
			
			columnVO.setDisplay("测试"+i);
			columnVO.setName("name"+i);
			columnVO.setWidth("100%");
			
			
			vos.add(columnVO);
		}
		
		return vos;
	}
	
	public static List<Map<String,String>> createDates(int size){
		List<Map<String,String>> list = Lists.newArrayList();
		
		Map<String,String> map = Maps.newHashMap();
		for(int i = 0 ; i <size ;i ++){
			map.put("name"+i, "value"+i);
		}
		
		list.add(map) ;
		
		return list;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static String generateColumnJosnString(List<TableColumnVO> vos){
		if(vos==null || vos.isEmpty()) return "[]";
		Map<String,String> fieldMap = getFieldMap(TableColumnVO.class);
		int mapSize = fieldMap.size();
		StringBuilder buff = new StringBuilder();
		int cnt = 0 ;
		for(TableColumnVO vo : vos){
			int i = 0 ;
			buff.append("{");
			boolean appendField = false;
			for(String mapKey : fieldMap.keySet()){
				if(i!=mapSize&&i!=0&&appendField)
					buff.append(",");
				try {
					Field f = TableColumnVO.class.getDeclaredField(mapKey);
					String fName = f.getName();
					Method m=TableColumnVO.class.getDeclaredMethod("get"+fName.substring(0, 1).toUpperCase() +fName.substring(1));
					Object obj = m.invoke(vo);
					if(fName.equals("columns"))
					{
						if(obj!=null  &&obj instanceof List)
						{
							buff.append(fieldMap.get(mapKey)).append(":[").append(generateColumnJosnString((List<TableColumnVO>)obj)).append("]");
							appendField = true;
						}else
							appendField = false;
					}else if(fName.equals("data")){
						if(obj!=null  &&obj instanceof List)
						{
							List<Map<String,String>> dataList = (List<Map<String,String>>)obj;
							if(!dataList.isEmpty()){
								buff.append(fieldMap.get(mapKey)).append(":");
								buff.append("{Rows:[{");
								for(Map<String,String>data:dataList){
									//int dataMapSize = data.size();
									int dataCnt = 0;
									for(String dataKey:data.keySet()){
										if(dataCnt!=0)
											buff.append(",");
										buff.append("'").append(dataKey).append("':'").append(data.get(dataKey)).append("'");
										dataCnt++;
									}
								}
								appendField = true;
								buff.append("}],Total:1}");
							}else
							{
								appendField = false;
							}
						}else
						{
							appendField = false;
						}
						
					}else{
						if(obj == null ||obj.equals("")){
							appendField = false;
						}else{
							buff.append(fieldMap.get(mapKey)).append(":").append("'").append((String)obj).append("'");
							appendField = true;
						}
					}
					i++;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			buff.append("}");
			if(cnt!=vos.size()-1)
				buff.append(",");
			cnt++;
		}
		return buff.toString();
	}
	
	public static Map<String,String> getFieldMap(Class<?> cls){
		Map<String,String>fieldMap = Maps.newHashMap();
		Field[] fields = cls.getDeclaredFields();
		for(Field f : fields){
			TableColumn tc = f.getAnnotation(TableColumn.class);
			fieldMap.put( f.getName(),  tc.columnName());
		}
		return fieldMap;
	}
}
