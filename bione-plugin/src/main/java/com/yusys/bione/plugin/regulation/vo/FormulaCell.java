package com.yusys.bione.plugin.regulation.vo;

import com.yusys.bione.plugin.regulation.enums.YesOrNo;

/**
 * 报表指标类公式单元格，支持以templateId、srcIndexNoes等排序
 */
public class FormulaCell extends IndexBaseCell {

	private String formula;

	private YesOrNo isRptIndex;

	private String excelCellNo;

	private FormulaIndex[] formulaIndexes;

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public YesOrNo getIsRptIndex() {
		return isRptIndex;
	}

	public void setIsRptIndex(YesOrNo isRptIndex) {
		this.isRptIndex = isRptIndex;
	}

	public String getExcelCellNo() {
		return excelCellNo;
	}

	public void setExcelCellNo(String excelCellNo) {
		this.excelCellNo = excelCellNo;
	}

	public FormulaIndex[] getFormulaIndexes() {
		return formulaIndexes;
	}

	public void setFormulaIndexes(FormulaIndex[] formulaIndexes) {
		this.formulaIndexes = formulaIndexes;
	}
}
