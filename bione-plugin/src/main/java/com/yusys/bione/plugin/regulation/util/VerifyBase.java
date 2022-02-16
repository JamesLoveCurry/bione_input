package com.yusys.bione.plugin.regulation.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.frame.util.excel.ExcelAnalyseUtils;
import com.yusys.bione.frame.util.excel.ExcelEventListener;
import com.yusys.bione.frame.util.excel.ExcelEventReader;
import com.yusys.bione.plugin.regulation.enums.BusiType;
import com.yusys.bione.plugin.regulation.enums.DataUnit;
import com.yusys.bione.plugin.regulation.enums.DisplayFormat;
import com.yusys.bione.plugin.regulation.enums.ElementType;
import com.yusys.bione.plugin.regulation.enums.ExtendDirection;
import com.yusys.bione.plugin.regulation.enums.ExtendMode;
import com.yusys.bione.plugin.regulation.enums.ExtendType;
import com.yusys.bione.plugin.regulation.enums.FilterMode;
import com.yusys.bione.plugin.regulation.enums.RptCycle;
import com.yusys.bione.plugin.regulation.enums.SortMode;
import com.yusys.bione.plugin.regulation.enums.SumMode;
import com.yusys.bione.plugin.regulation.enums.TemplateType;
import com.yusys.bione.plugin.regulation.enums.YesOrNo;
import com.yusys.bione.plugin.regulation.vo.Data;
import com.yusys.bione.plugin.regulation.vo.Template;

public abstract class VerifyBase implements ExcelEventListener {

	protected LoadData loadData;

	protected Data data;
	
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	private Template template;

	private String columnType;

	private Map<String, String> tempDataMap;

	public VerifyBase(LoadData loadData) {
		this.loadData = loadData;
		this.data = loadData.getData();
	}

	/**
	 * 检查报表信息页
	 */
	private void checkReportInfo(Map<String, String> map) {
		// 版本号的格式检查在LoadData类里做
		if (TemplateType.get(map.get("模板类型")) == TemplateType.ERROR) {
			loadData.reportError("报表信息页，模板类型设置错误", template, map, "模板类型");
		}
		DataUnit dataUnit = DataUnit.get(map.get("报表单位"));
		if (dataUnit == DataUnit.EMPTY || dataUnit == DataUnit.NULL || dataUnit == DataUnit.ERROR) {
			loadData.reportError("报表信息页，报表单位设置错误", template, map, "报表单位");
		}
		if (RptCycle.get(map.get("报表周期")) == RptCycle.ERROR) {
			loadData.reportError("报表信息页，报表周期设置错误", template, map, "报表周期");
		}
		YesOrNo option = YesOrNo.get(map.get("报表状态"));
		if (option == YesOrNo.EMPTY || option == YesOrNo.ERROR) {
			loadData.reportError("报表信息页，报表状态设置错误", template, map, "报表状态");
		}
		if (BusiType.get(map.get("业务类型")) == BusiType.ERROR) {
			loadData.reportError("报表信息页，业务类型配置错误", template, map, "业务类型");
		}
		if (YesOrNo.get(map.get("是否下发提交")) == YesOrNo.ERROR) {
			loadData.reportError("报表信息页，是否下发提交配置错误", template, map, "是否下发提交");
		}
		if (YesOrNo.get(map.get("是否数据修改")) == YesOrNo.ERROR) {
			loadData.reportError("报表信息页，是否数据修改配置错误", template, map, "是否数据修改");
		}
		if (! data.getBusiLineMap().containsKey(map.get("业务条线"))) {
			loadData.reportError("报表信息页，业务条线设置错误", template, map, "业务条线");
		}
	}

	/**
	 * 检查查询条件页
	 */
	private void checkQueryCondition(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		if ("DATE".equals(columnDataMap.get("维度编号"))) {
			try {
				sdf.parse(StringUtils.trimToEmpty(columnDataMap.get("默认值")));
			} catch (ParseException e) {
				loadData.reportError("查询条件页，DATE维度的默认值格式错误", template, columnDataMap, "默认值");
			}
		}
		YesOrNo option = YesOrNo.get(columnDataMap.get("是否必填"));
		if (option == YesOrNo.EMPTY || option == YesOrNo.ERROR) {
			loadData.reportError("查询条件页，是否必填设置错误", template, columnDataMap, "是否必填");
		}
		if (YesOrNo.get(columnDataMap.get("是否复选")) == YesOrNo.ERROR) {
			loadData.reportError("查询条件页，是否复选设置错误", template, columnDataMap, "是否复选");
		}
		if (columnDataMap.containsKey("维度类型") && ! data.getDimTypeMap().containsKey(columnDataMap.get("维度类型"))) {
			loadData.reportError("查询条件页，维度类型设置错误", template, columnDataMap, "维度类型");
		}
		if (columnDataMap.containsKey("控件类型") && ElementType.get(columnDataMap.get("控件类型")) == ElementType.ERROR) {
			loadData.reportError("查询条件页，控件类型设置错误", template, columnDataMap, "控件类型");
		}
	}

	/**
	 * 检查单元格信息的一般单元格
	 */
	private void checkComCell(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			return;
		}
		if (! data.getComCellTypeMap().containsKey(columnDataMap.get("单元格类型"))) {
			loadData.reportError("单元格信息页，一般单元格单元格类型设置错误", template, columnDataMap, "单元格类型");
		}
	}

	/**
	 * 检查单元格信息的指标单元格
	 */
	private void checkIndexCell(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			return;
		}
		// 计算规则、时间度量、取值方式的检查在LoadData类里做
		if (YesOrNo.get(columnDataMap.get("是否汇总")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，指标单元格是否汇总设置错误", template, columnDataMap, "是否汇总");
		}
		if (YesOrNo.get(columnDataMap.get("是否落地")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，指标单元格是否落地设置错误", template, columnDataMap, "是否落地");
		}
		if (DisplayFormat.get(columnDataMap.get("显示格式")) == DisplayFormat.ERROR) {
			loadData.reportError("单元格信息页，指标单元格显示格式设置错误", template, columnDataMap, "显示格式");
		}
		if (DataUnit.get(columnDataMap.get("数据单位")) == DataUnit.ERROR) {
			loadData.reportError("单元格信息页，指标单元格数据单位设置错误", template, columnDataMap, "数据单位");
		}
		if (StringUtils.isNotEmpty(columnDataMap.get("数据精度"))) {
			try {
				Integer.parseInt(columnDataMap.get("数据精度"));
			} catch (NumberFormatException e) {
				loadData.reportError("单元格信息页，指标单元格数据精度设置错误", template, columnDataMap, "数据精度");
			}
		}
		if (YesOrNo.get(columnDataMap.get("是否跨年")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，指标单元格是否跨年设置错误", template, columnDataMap, "是否跨年");
		}
	}

	/**
	 * 检查单元格信息的字段单元格
	 */
	private void checkModuleCell(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			return;
		}
		if (YesOrNo.get(columnDataMap.get("是否扩展")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，字段单元格是否扩展设置错误", template, columnDataMap, "是否扩展");
		}
		if (ExtendDirection.get(columnDataMap.get("扩展方向")) == ExtendDirection.ERROR) {
			loadData.reportError("单元格信息页，字段单元格扩展方向设置错误", template, columnDataMap, "扩展方向");
		}
		if (ExtendMode.get(columnDataMap.get("扩展方式")) == ExtendMode.ERROR) {
			loadData.reportError("单元格信息页，字段单元格扩展方式设置错误", template, columnDataMap, "扩展方式");
		}
		YesOrNo option = YesOrNo.get(columnDataMap.get("是否排序"));
		if (option == YesOrNo.EMPTY || option == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，字段单元格是否排序设置错误", template, columnDataMap, "是否排序");
		}
		if (SortMode.get(columnDataMap.get("排序方式")) == SortMode.ERROR) {
			loadData.reportError("单元格信息页，字段单元格排序方式设置错误", template, columnDataMap, "排序方式");
		}
		if (YesOrNo.get(columnDataMap.get("是否转码")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，字段单元格是否转码设置错误", template, columnDataMap, "是否转码");
		}
		if (SumMode.get(columnDataMap.get("汇总方式")) == SumMode.ERROR) {
			loadData.reportError("单元格信息页，字段单元格汇总方式设置错误", template, columnDataMap, "汇总方式");
		}
		if (DisplayFormat.get(columnDataMap.get("显示格式")) == DisplayFormat.ERROR) {
			loadData.reportError("单元格信息页，字段单元格显示格式设置错误", template, columnDataMap, "显示格式");
		}
		if (DataUnit.get(columnDataMap.get("数据单位")) == DataUnit.ERROR) {
			loadData.reportError("单元格信息页，字段单元格数据单位设置错误", template, columnDataMap, "数据单位");
		}
		if (StringUtils.isNotEmpty(columnDataMap.get("数据精度"))) {
			try {
				Integer.parseInt(columnDataMap.get("数据精度"));
			} catch (NumberFormatException e) {
				loadData.reportError("单元格信息页，字段单元格数据精度设置错误", template, columnDataMap, "数据精度");
			}
		}
	}

	/**
	 * 检查单元格信息的维度列表单元格
	 */
	private void checkTabDimCell(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			return;
		}
		if (YesOrNo.get(columnDataMap.get("是否合计")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，维度列表单元格是否合计设置错误", template, columnDataMap, "是否合计");
		}
		if (YesOrNo.get(columnDataMap.get("是否转码")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，维度列表单元格是否转码设置错误", template, columnDataMap, "是否转码");
		}
	}

	/**
	 * 检查单元格信息的指标列表单元格
	 */
	private void checkListCell(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			return;
		}
		// 计算规则、时间度量、取值方式的检查在LoadData类里做
		if (YesOrNo.get(columnDataMap.get("是否汇总")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，指标列表单元格是否汇总设置错误", template, columnDataMap, "是否汇总");
		}
		if (YesOrNo.get(columnDataMap.get("是否落地")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，指标列表单元格是否落地设置错误", template, columnDataMap, "是否落地");
		}
		if (DisplayFormat.get(columnDataMap.get("显示格式")) == DisplayFormat.ERROR) {
			loadData.reportError("单元格信息页，指标列表单元格显示格式设置错误", template, columnDataMap, "显示格式");
		}
		if (DataUnit.get(columnDataMap.get("数据单位")) == DataUnit.ERROR) {
			loadData.reportError("单元格信息页，指标列表单元格数据单位设置错误", template, columnDataMap, "数据单位");
		}
		if (StringUtils.isNotEmpty(columnDataMap.get("数据精度"))) {
			try {
				Integer.parseInt(columnDataMap.get("数据精度"));
			} catch (NumberFormatException e) {
				loadData.reportError("单元格信息页，指标列表单元格数据精度设置错误", template, columnDataMap, "数据精度");
			}
		}
		if (YesOrNo.get(columnDataMap.get("是否跨年")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，指标列表单元格是否跨年设置错误", template, columnDataMap, "是否跨年");
		}
	}

	/**
	 * 检查单元格信息的公式单元格
	 */
	private void checkFormulaCell(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			return;
		}
		if (YesOrNo.get(columnDataMap.get("是否分析扩展")) == YesOrNo.ERROR) {
			loadData.reportError("单元格信息页，公式单元格是否分析扩展设置错误", template, columnDataMap, "是否分析扩展");
		}
		if (ExtendType.get(columnDataMap.get("扩展类型")) == ExtendType.ERROR) {
			loadData.reportError("单元格信息页，公式单元格扩展类型设置错误", template, columnDataMap, "扩展类型");
		}
		if (DisplayFormat.get(columnDataMap.get("显示格式")) == DisplayFormat.ERROR) {
			loadData.reportError("单元格信息页，公式单元格显示格式设置错误", template, columnDataMap, "显示格式");
		}
		if (DataUnit.get(columnDataMap.get("数据单位")) == DataUnit.ERROR) {
			loadData.reportError("单元格信息页，公式单元格数据单位设置错误", template, columnDataMap, "数据单位");
		}
		if (StringUtils.isNotEmpty(columnDataMap.get("数据精度"))) {
			try {
				Integer.parseInt(columnDataMap.get("数据精度"));
			} catch (NumberFormatException e) {
				loadData.reportError("单元格信息页，公式单元格数据精度设置错误", template, columnDataMap, "数据精度");
			}
		}
	}

	/**
	 * 检查指标过滤信息页
	 */
	private void checkFilter(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			loadData.reportError("指标过滤信息页，单元格编号为空", template, columnDataMap, "单元格编号");
			return;
		}
		String dimTypeNo = data.getDimTypeMap().get(columnDataMap.get("维度类型"));
		if (StringUtils.isEmpty(dimTypeNo)) {
			loadData.reportError("指标过滤信息页，维度类型设置错误", template, columnDataMap, "维度类型");
		} else if (! "DATE".equals(dimTypeNo) && ! "ORG".equals(dimTypeNo) && ! "INDEXNO".equals(dimTypeNo)){
			String dimItemName = columnDataMap.get("过滤信息");
			if (StringUtils.isNotEmpty(dimItemName)) {
				Map<String, String> itemMap = data.getDimItemMap().get(dimTypeNo);
				String[] dimItemNames = StringUtils.split(dimItemName, ',');
				for (int i = 0; i < dimItemNames.length; i ++) {
					String name = StringUtils.trimToEmpty(dimItemNames[i]);
					int pos1 = name.indexOf('(');
					if (pos1 < 0) {
						loadData.reportError("指标过滤信息页，无法找到维度值编号", template, columnDataMap, "过滤信息");
					} else {
						int pos2 = name.indexOf(')', pos1);
						if (pos2 < 0) {
							loadData.reportError("指标过滤信息页，过滤信息值格式错误", template, columnDataMap, "过滤信息");
						} else {
							String no = StringUtils.trimToEmpty(name.substring(pos1 + 1, pos2));
							name = StringUtils.trimToEmpty(name.substring(0, pos1));
							if (StringUtils.isNotEmpty(name) && ! itemMap.containsKey(no)) {
								loadData.reportError("指标过滤信息页，无法找到维度值名称", template, columnDataMap, "过滤信息");
							} else if (! name.equals(itemMap.get(no))) {
								loadData.reportError("指标过滤信息页，维度值名称与编号无法匹配", template, columnDataMap, "过滤信息");
							}
						}
					}
				}
			}
		}
		if (FilterMode.get(columnDataMap.get("过滤类型")) == FilterMode.ERROR) {
			loadData.reportError("指标过滤信息页，过滤类型设置错误", template, columnDataMap, "过滤类型");
		}
	}

	/**
	 * 检查校验公式页
	 */
	protected abstract void checkValidCfgextLogic(Template template, Map<String, String> columnDataMap);

	/**
	 * 检查预警值校验页
	 */
	protected abstract void checkValidCfgextWarn(Template template, Map<String, String> columnDataMap);

	/**
	 * 检查报表模板导入Excel文件中在LoadData类中未检查的字典值、日期值、数字值的合法性，以及校验公式
	 */
	public void verifyData() throws IOException {
		Map<String, ExcelEventReader> excelEventReaderMap = loadData.getExcelEventReaderMap();
		for (Iterator<Entry<String, ExcelEventReader>> it = excelEventReaderMap.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, ExcelEventReader> entry = it.next();
			String filePath = entry.getKey();
			ExcelEventReader excelEventReader = entry.getValue();
			excelEventReader.setExcelEventListener(this);
			excelEventReader.startReadSheet(filePath, "报表信息");
			excelEventReader.startReadSheet(filePath, "查询条件");
			columnType = "一般单元格";
			excelEventReader.startReadSheet(filePath, "单元格信息");
			columnType = "指标单元格";
			excelEventReader.startReadSheet(filePath, "单元格信息");
			columnType = "字段单元格";
			excelEventReader.startReadSheet(filePath, "单元格信息");
			columnType = "维度列表单元格";
			excelEventReader.startReadSheet(filePath, "单元格信息");
			columnType = "指标列表单元格";
			excelEventReader.startReadSheet(filePath, "单元格信息");
			columnType = "公式单元格";
			excelEventReader.startReadSheet(filePath, "单元格信息");
			columnType = null;
			excelEventReader.startReadSheet(filePath, "指标过滤信息");
			excelEventReader.startReadSheet(filePath, "校验公式");
			excelEventReader.startReadSheet(filePath, "预警校验");
		}
	}

	@Override
	public void rowDataReady(ExcelEventReader excelEventReader, int rowIdx, Map<Integer, String> rowDataMap) {
		String sheetName = excelEventReader.getSheetName();
		if ("报表信息".equals(sheetName)) {
			if (rowDataMap == null) {
				loadData.translateDictionary(tempDataMap);
				checkReportInfo(tempDataMap);
				tempDataMap = null;
			} else {
				if (tempDataMap == null) {
					tempDataMap = new HashMap<String, String>();
				}
				String columnName = rowDataMap.get(Integer.valueOf(1));
				tempDataMap.put(columnName, rowDataMap.get(Integer.valueOf(2)));
				tempDataMap.put("_REGULATION_" + columnName, ExcelAnalyseUtils.toABC(rowIdx, 2));
			}
		} else if ("单元格信息".equals(sheetName)) {
			if (rowDataMap == null || rowIdx <= 1) {
				return;
			}
			Map<String, String> columnDataMap = loadData.parseRowDataMap(template, columnType, rowIdx, rowDataMap);
			loadData.translateDictionary(columnDataMap);
			if ("一般单元格".equals(columnType)) {
				checkComCell(template, columnDataMap);
			} else if ("指标单元格".equals(columnType)) {
				checkIndexCell(template, columnDataMap);
			} else if ("字段单元格".equals(columnType)) {
				checkModuleCell(template, columnDataMap);
			} else if ("维度列表单元格".equals(columnType)) {
				checkTabDimCell(template, columnDataMap);
			} else if ("指标列表单元格".equals(columnType)) {
				checkListCell(template, columnDataMap);
			} else if ("公式单元格".equals(columnType)) {
				checkFormulaCell(template, columnDataMap);
			}
		} else if ("指标过滤信息".equals(sheetName)) {
			if (rowIdx >= 1) {
				Template template = loadData.getTemplate(excelEventReader);
				Map<String, String> columnDataMap = loadData.parseRowDataMap(template, sheetName, rowIdx, rowDataMap);
				loadData.translateDictionary(columnDataMap);
				if (columnDataMap != null && StringUtils.isNotEmpty(columnDataMap.get("单元格编号"))) {
					checkFilter(template, columnDataMap);
				}
			}
		} else if ("查询条件".equals(sheetName)) {
			if (rowIdx >= 1) {
				Template template = loadData.getTemplate(excelEventReader);
				Map<String, String> columnDataMap = loadData.parseRowDataMap(template, sheetName, rowIdx, rowDataMap);
				loadData.translateDictionary(columnDataMap);
				if (columnDataMap != null && StringUtils.isNotEmpty(columnDataMap.get("维度编号"))) {
					checkQueryCondition(template, columnDataMap);
				}
			}
		} else if ("校验公式".equals(sheetName)) {
			if (rowIdx >= 1) {
				Template template = loadData.getTemplate(excelEventReader);
				Map<String, String> columnDataMap = loadData.parseRowDataMap(template, sheetName, rowIdx, rowDataMap);
				loadData.translateDictionary(columnDataMap);
				if (columnDataMap != null && StringUtils.isNotEmpty(columnDataMap.get("公式"))) {
					checkValidCfgextLogic(template, columnDataMap);
				}
			}
		} else if ("预警校验".equals(sheetName)) {
			if (rowIdx >= 1) {
				Template template = loadData.getTemplate(excelEventReader);
				Map<String, String> columnDataMap = loadData.parseRowDataMap(template, sheetName, rowIdx, rowDataMap);
				loadData.translateDictionary(columnDataMap);
				if (columnDataMap != null && StringUtils.isNotEmpty(columnDataMap.get("单元格编号"))) {
					checkValidCfgextWarn(template, columnDataMap);
				}
			}
		}
	}
}
