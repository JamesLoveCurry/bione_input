package com.yusys.biapp.input.data.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.biapp.input.dict.utils.UdipConstants;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableConstraint;
import com.yusys.biapp.input.inputTable.entity.RptInputListTableFieldInf;
import com.yusys.biapp.input.inputTable.service.TableFieldBS;
import com.yusys.biapp.input.template.entity.RptInputLstTempleConst;
import com.yusys.biapp.input.template.entity.RptInputLstTempleField;
import com.yusys.biapp.input.template.entity.RptInputLstTempleInfo;
import com.yusys.biapp.input.template.service.TempleBS;
import com.yusys.biapp.input.template.service.TempleConstraintBS;
import com.yusys.biapp.input.template.service.TempleFieldBS;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jdbc.entity.BioneTableMetaData;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/rpt/input/data")
public class DataController extends BaseController {
	
	@Autowired
	private DataSourceBS dsBS;
	@Autowired
	private TempleBS templeBS;
	@Autowired
	private TableFieldBS tableFieldBS;
	@Autowired
	private TempleFieldBS templeFieldBS;
	@Autowired
	private TempleConstraintBS templeConstraintBS;

	// 根据数据源Id获取该数据源下的所有表名称和表中文名称
	@RequestMapping(value = "/dataTableList")
	@ResponseBody
	/**
	 * 
	 * @param id 数据源ID
	 * @return
	 */
	public Map<String, Object> dataTableList(String id) {
		List<BioneTableMetaData> tableList = Lists.newArrayList();
		try {
			tableList = dsBS.getTableMetaDataWithColumns(id, UdipConstants.TAB_OPER_DATE,
					UdipConstants.TAB_DATA_STATE, UdipConstants.TAB_OPERATOR, UdipConstants.TAB_DATA_DATE, UdipConstants.TAB_DATA_CASE);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		Map<String, Object> objDefMap = Maps.newHashMap();
		objDefMap.put("Rows", tableList);
		return objDefMap;
	}

	@RequestMapping(value = "/getTableMoreList")
	@ResponseBody
	public Map<String, Object> getTableMoreList(Pager pager, String templeId, String dsId, String tableName) {
		List<RptInputLstTempleField> result = Lists.newArrayList();
		if (StringUtils.isNotBlank(dsId)) {
			Set<String> fieldSet = Sets.newHashSet();
			for (Map.Entry<String, RptInputListTableFieldInf> entry : UdipConstants.colMaps.get(this.dsBS.getDataSourceType(dsId).toUpperCase()).entrySet()) {
				fieldSet.add(entry.getValue().getFieldEnName());
			}
//			List<BioneTableMetaData> tableList = this.dsBS.getTableMetaDataWithColumns(dsId, fieldSet.toArray(new String[0]));
//			for (BioneTableMetaData table : tableList) {
//				if (tableName.equals(table.getTableName())) {
					List<RptInputListTableFieldInf> fieldList = this.tableFieldBS.getTableFieldByDsIdAndTableEnName(dsId, tableName);
					List<RptInputListTableConstraint> constList = this.tableFieldBS.getTableConstraint(dsId, tableName);
					Set<String> constSet = Sets.newHashSet();
					for (RptInputListTableConstraint cons : constList) {
						constSet.add(cons.getKeyColumn());
					}
					for (RptInputListTableFieldInf field : fieldList) {
						if (!fieldSet.contains(field.getFieldEnName())) {
							RptInputLstTempleField templeField = new RptInputLstTempleField();
							templeField.setTempleFieldId(RandomUtils.uuid2());
							templeField.setFieldEnName(field.getFieldEnName());
							templeField.setFieldType(field.getFieldType());
							templeField.setFieldLength(field.getFieldLength());
							templeField.setDecimalLength(field.getDecimalLength());
							templeField.setFieldCnName(field.getFieldCnName());
							templeField.setOrderNo(field.getOrderNo());
							templeField.setAllowEdit(constSet.contains(field.getFieldEnName()) ? "1" : "0");
							templeField.setDefaultValue(field.getDefaultValue());
							result.add(templeField);
						}
					}
//				}
//			}
		} else if (StringUtils.isNotBlank(templeId)) {
			RptInputLstTempleInfo tempInfo = this.templeBS.getEntityById(templeId);
			String orgColumn = "";
			if (tempInfo != null) {
				orgColumn = tempInfo.getOrgColumn();
			} else {
				orgColumn = "";
			}
			List<RptInputLstTempleConst> keyList = this.templeConstraintBS.findEntityListByProperty("templeId", templeId);
			result = this.templeFieldBS.findEntityListByProperty("templeId", templeId);
			List<String> keyCols = Lists.newArrayList();
			
			
			
			for (RptInputLstTempleConst cons : keyList) {
				if (UdipConstants.TAB_PRIMARY.equals(cons.getKeyType())) {
					// 把字段放入keyColes集合中
					String[] keysplit = cons.getKeyColumn().split(";");
					for (String key : keysplit) {
						if (!key.equals(UdipConstants.TAB_DATA_CASE)) {
							keyCols.add(key);
						}
					}
				}
			}
			if (StringUtils.isNotBlank(orgColumn)) {
				for (int i = 0; i < result.size(); i++) {
					if (orgColumn.equals(result.get(i).getFieldEnName())) {
						// 暂时用来回显机构字段,无意义
						result.get(i).setLogicSysNo(orgColumn);
					}
				}
			}
			if (!keyList.isEmpty()) {
				for (int k = 0; k < result.size(); k++) {
					for (int j = 0; j < keyList.size(); j++) {
						if (keyCols.contains((result.get(k).getFieldEnName()))) {
							// 暂时用来回显主键,无意义
							result.get(k).setAllowEdit("1");
						}
					}
				}
			}
		}
		Map<String, Object> rows = Maps.newHashMap();
		Collections.sort(result, new Comparator<RptInputLstTempleField>() {
			@Override
			public int compare(RptInputLstTempleField o1, RptInputLstTempleField o2) {
                return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		
		
		
		List<JSONObject> resList = Lists.newArrayList();
		
		Map<String,String> dict = templeFieldBS.getDictMap();
		
		for(RptInputLstTempleField t : result){
			JSONObject jo = (JSONObject)JSON.toJSON(t);
			if(!StringUtils.isEmpty(t.getDictId())){
				jo.put("dictName", dict.get(t.getDictId()));

			}
			resList.add(jo);
		}
		
		if(resList.size() > 0){
			rows.put("Rows", resList);
			rows.put("Total", resList.size());
		}else{
			rows.put("Rows", result);
			rows.put("Total", result.size());
		}
		return rows;
	}

	// 根据模板ID查找数据表中的字段下拉框
	@RequestMapping(value = "/getColumnList/{id}")
	@ResponseBody
	public List<Map<String, String>> getColumnList(Pager pager,
			@PathVariable("id") String id) {
		List<Map<String, String>> columnList = Lists.newArrayList();
		try {
			List<RptInputLstTempleField> list = getColumnGridList(id);

			for (int i = 0; i < list.size(); i++) {
				Map<String, String> m = Maps.newHashMap();
				if (StringUtils.isNotBlank(list.get(i).getFieldDetail())) {
					m.put("text", list.get(i).getFieldDetail() + "("
							+ list.get(i).getFieldEnName() + ")");
				} else {
					m.put("text", list.get(i).getFieldEnName());
				}

				m.put("id", list.get(i).getFieldEnName());
				columnList.add(i, m);
			}

		} catch (Exception e) {
			logger.error(e.toString());
		}
		return columnList;
	}

	// 根据模板ID查找数据表中的字段表格以及为后续动态表单做准备
	@RequestMapping(value = "/getColumnGridList/{id}")
	@ResponseBody
	public List<RptInputLstTempleField> getColumnGridList(
			@PathVariable("id") String id) {
		List<RptInputLstTempleField> templeColumns = this.templeFieldBS
				.getTempleColumns(id);
		// 排序
		Collections.sort(templeColumns, new Comparator<RptInputLstTempleField>() {
			@Override
			public int compare(RptInputLstTempleField o1,
					RptInputLstTempleField o2) {
                return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		return templeColumns;
	}

	// 根据模板ID查找数据表中的字段表格以及为后续动态表单做准备
	@RequestMapping(value = "/getColumnGridListByQuery/{id}")
	@ResponseBody
	public List<RptInputLstTempleField> getColumnGridListByQuery(
			@PathVariable("id") String id) {
		List<RptInputLstTempleField> templeColumns = this.templeFieldBS
				.getTempleColumns(id);
		List<RptInputLstTempleField> listTempleColumns = this.templeFieldBS
				.getAllTempleColumns(templeColumns);
		// 排序
		Collections.sort(listTempleColumns, new Comparator<RptInputLstTempleField>() {
			@Override
			public int compare(RptInputLstTempleField o1,
							   RptInputLstTempleField o2) {
				return o1.getOrderNo() - o2.getOrderNo();
			}
		});
		return listTempleColumns;
	}

}
