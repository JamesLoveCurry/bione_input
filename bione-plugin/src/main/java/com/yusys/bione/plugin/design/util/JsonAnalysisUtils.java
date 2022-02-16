package com.yusys.bione.plugin.design.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.plugin.spreadjs.entity.Data;
import com.yusys.bione.plugin.spreadjs.entity.DataTable;
import com.yusys.bione.plugin.spreadjs.entity.DataTablePropertyProperty;
import com.yusys.bione.plugin.spreadjs.entity.SheetsProperty;
import com.yusys.bione.plugin.spreadjs.entity.SpreadSchema;

public class JsonAnalysisUtils {

	private DataTable dataTable=null;
	/**
	 * 
	 * @param json  模板json参数
	 */
	public JsonAnalysisUtils(String json){
		SpreadSchema schema = JSON.parseObject(json, SpreadSchema.class);
		if (schema != null) {
			Map<String, SheetsProperty> sheetsTable = schema.getSheetsProperties();
			Object[] key = sheetsTable.keySet().toArray();
			Data data = sheetsTable.get(key[0]).getData();
			if(data != null){				
				dataTable = data.getDataTable();
			}
		}
	}
	
	/**
	 * 
	 * @param row 行号（从0开始）
	 * @param col 列号（从0开始）
	 * @return
	 */
	public Object getData(int row,int col){
		try{
			DataTablePropertyProperty value= (DataTablePropertyProperty)dataTable.getAdditionalProperties().get(String.valueOf(row)).getAdditionalProperties().get(String.valueOf(col));
			
			return value.getValue();
		}
		catch(Exception e){
			return null;
		}
		
	}
	
	
}
