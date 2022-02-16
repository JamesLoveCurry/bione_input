package com.yusys.bione.plugin.regulation.util;

import java.text.ParseException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.plugin.regulation.enums.CompareType;
import com.yusys.bione.plugin.regulation.enums.IsFrs;
import com.yusys.bione.plugin.regulation.enums.YesOrNo;
import com.yusys.bione.plugin.regulation.vo.FormulaCell;
import com.yusys.bione.plugin.regulation.vo.Template;

public class Verify extends VerifyBase {

	public Verify(LoadData loadData) {
		super(loadData);
	}

	@Override
	protected void checkValidCfgextLogic(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String formula = columnDataMap.get("公式");
		if (StringUtils.isEmpty(formula)) {
			return;
		}
		FormulaCell cell = new FormulaCell();
		cell.setTemplate(template);
		cell.setFormula(formula);
		cell.setExcelCellNo(columnDataMap.get("_REGULATION_公式"));
		loadData.parseFormula(template, cell, null, null);
		String str = columnDataMap.get("容差值");
		if (StringUtils.isEmpty(str)) {
			loadData.reportError("校验公式页，容差值不能为空：(" + formula + ")", template, columnDataMap, "容差值");
		} else {
			try {
				Double.parseDouble(str);
			} catch (NumberFormatException e) {
				loadData.reportError("校验公式页，容差值格式错误：(" + str + ")", template, columnDataMap, "容差值");
			}
		}
		str = columnDataMap.get("开始日期");
		if (StringUtils.isEmpty(str)) {
			loadData.reportError("校验公式页，开始日期不能为空：(" + formula + ")", template, columnDataMap, "开始日期");
		} else {
			try {
				sdf.parse(str);
			} catch (ParseException e) {
				loadData.reportError("校验公式页，开始日期格式错误：(" + str + ")", template, columnDataMap, "开始日期");
			}
		}
		str = columnDataMap.get("结束日期");
		if (StringUtils.isEmpty(str)) {
			loadData.reportError("校验公式页，结束日期不能为空：(" + formula + ")", template, columnDataMap, "结束日期");
		} else {
			try {
				sdf.parse(str);
			} catch (ParseException e) {
				loadData.reportError("校验公式页，结束日期格式错误：(" + str + ")", template, columnDataMap, "结束日期");
			}
		}
		str = columnDataMap.get("是否预校验");
		if (YesOrNo.get(str) == YesOrNo.ERROR) {
			loadData.reportError("校验公式页，是否预校验格式错误：(" + str + ")", template, columnDataMap, "是否预校验");
		}
	}

	@Override
	protected void checkValidCfgextWarn(Template template, Map<String, String> columnDataMap) {
		if (columnDataMap == null) {
			return;
		}
		String cellNo = columnDataMap.get("单元格编号");
		if (StringUtils.isEmpty(cellNo)) {
			return;
		}
		if (! loadData.isEmptyCellInVerifyWarn() &&
			! data.getCellNoToCellMap().get(template).containsKey(cellNo)) {
			loadData.reportError("预警校验页，不能对空单元格或一般单元格进行预警校验", template, columnDataMap, "单元格编号", cellNo);
		}
		/*if (CompareValueType.get(columnDataMap.get("比较值类型")) == CompareValueType.ERROR) {
			loadData.reportError("预警校验页，比较值类型格式错误", template, columnDataMap, "比较值类型");
		}*/
		/*if (RangeType.get(columnDataMap.get("幅度类型")) == RangeType.ERROR) {
			loadData.reportError("预警值校验页，幅度类型格式错误", template, columnDataMap, "幅度类型");
		}*/
		if(CompareType.get(columnDataMap.get("预警类型")) == CompareType.ERROR){
			loadData.reportError("预警校验页，预警类型码值错误（环比01，同比02）", template, columnDataMap, "预警类型");
		}
		if(IsFrs.get(columnDataMap.get("监管要求")) == IsFrs.ERROR){
			loadData.reportError("预警校验页，监管要求码值错误（是01，否02）", template, columnDataMap, "监管要求");
		}
		
		String str = columnDataMap.get("最小比率");
		if (! StringUtils.isEmpty(str)) {
			try {
				Double.parseDouble(str);
			} catch (NumberFormatException e) {
				loadData.reportError("预警校验页，最小比率格式错误", template, columnDataMap, "最小比率");
			}
		}
		str = columnDataMap.get("最大比率");
		if (! StringUtils.isEmpty(str)) {
			try {
				Double.parseDouble(str);
			} catch (NumberFormatException e) {
				loadData.reportError("预警校验页，最大比率格式错误", template, columnDataMap, "最大比率");
			}
		}
		
		/*str = columnDataMap.get("开始日期");
		if (StringUtils.isEmpty(str)) {
			loadData.reportError("预警校验页，开始日期不能为空", template, columnDataMap, "开始日期");
		} else {
			try {
				sdf.parse(str);
			} catch (ParseException e) {
				loadData.reportError("预警校验页，开始日期格式错误", template, columnDataMap, "开始日期");
			}
		}
		str = columnDataMap.get("结束日期");
		if (StringUtils.isEmpty(str)) {
			loadData.reportError("预警校验页，结束日期不能为空", template, columnDataMap, "结束日期");
		} else {
			try {
				sdf.parse(str);
			} catch (ParseException e) {
				loadData.reportError("预警校验页，结束日期格式错误", template, columnDataMap, "结束日期");
			}
		}*/
		/*if (LeverType.get(columnDataMap.get("警戒值类型")) == LeverType.ERROR) {
			loadData.reportError("预警值校验页，警戒值类型格式错误", template, columnDataMap, "警戒值类型");
		}
		str = columnDataMap.get("提醒颜色");
		if (StringUtils.isEmpty(str)) {
			loadData.reportError("预警值校验页，提醒颜色不能为空", template, columnDataMap, "提醒颜色");
		} else if (str.length() != 6) {
			loadData.reportError("预警值校验页，提醒颜色格式错误", template, columnDataMap, "提醒颜色");
		} else {
			try {
				Integer.parseInt(str, 16);
			} catch (NumberFormatException e) {
				loadData.reportError("预警值校验页，提醒颜色格式错误", template, columnDataMap, "提醒颜色");
			}
		}
		if (YesOrNo.get(columnDataMap.get("通过条件")) == YesOrNo.ERROR) {
			loadData.reportError("预警值校验页，通过条件格式错误", template, columnDataMap, "通过条件");
		}*/
	}
}
