package com.yusys.bione.plugin.regulation.vo;

/**
 * 指标单元格，支持以templateId、srcIndexNoes等排序；
 * 如果srcIndexNoes都为空，那么以cellNo代替srcIndexNoes的排序位置
 */
public class IndexCell extends IndexBaseCell {
	
	private byte timeMeasureId;
	
	private byte ruleId;
	
	private byte modeId;

	public String[] getSrcIndexNoes() {
		return index.getSrcIndexNoes();
	}

	public byte getTimeMeasureId() {
		return timeMeasureId;
	}

	public void setTimeMeasureId(byte timeMeasureId) {
		this.timeMeasureId = timeMeasureId;
	}

	public byte getRuleId() {
		return ruleId;
	}

	public void setRuleId(byte ruleId) {
		this.ruleId = ruleId;
	}

	public byte getModeId() {
		return modeId;
	}

	public void setModeId(byte modeId) {
		this.modeId = modeId;
	}
}