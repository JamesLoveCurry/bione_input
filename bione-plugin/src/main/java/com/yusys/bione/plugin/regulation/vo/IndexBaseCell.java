package com.yusys.bione.plugin.regulation.vo;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.plugin.regulation.enums.CellStatus;

/**
 * 指标单元格、指标列表单元格、报表指标类公式单元格基类，支持以templateId、srcIndexNoes等排序；
 * 如果srcIndexNoes都为空，那么以cellNo代替srcIndexNoes的排序位置
 */
public abstract class IndexBaseCell extends BaseCell {

	protected String cellNm;
	
	protected Index index;

	private CellStatus status = CellStatus.UNKNOWN;

	@Override
	public int compareTo(BaseCell o) {
		int comp = template.compareTo(o.getTemplate());
		if (comp != 0) {
			return comp;
		}
		IndexBaseCell iCell = (IndexBaseCell)o;
		String[] srcIndexNoes1 = index.getSrcIndexNoes();
		String[] srcIndexNoes2 = iCell.getIndex().getSrcIndexNoes();
		if(StringUtils.isNotBlank(o.getCellNo())) {
			comp = cellNo.compareTo(o.getCellNo());
			if (comp != 0) {
				return comp;
			}
		}
		if (ArrayUtils.isNotEmpty(srcIndexNoes1) && ArrayUtils.isNotEmpty(srcIndexNoes2)) {
			int count = Math.min(srcIndexNoes1.length, srcIndexNoes2.length);
			for (int i = 0; i < count; i ++) {
				comp = srcIndexNoes1[i].compareTo(srcIndexNoes2[i]);
				if (comp != 0) {
					return comp;
				}
			}
			comp = srcIndexNoes1.length - srcIndexNoes2.length;
		} 
		if (comp != 0) {
			return comp;
		}
		if ((this instanceof IndexCell) && (o instanceof FormulaCell)) {
			return -1;
		}
		if ((this instanceof FormulaCell) && (o instanceof IndexCell)) {
			return 1;
		}
		if ((this instanceof FormulaCell) && (o instanceof FormulaCell)) {
			return 0;
		}
		IndexCell c1 = (IndexCell)this;
		IndexCell c2 = (IndexCell)o;
		comp = c1.getTimeMeasureId() - c2.getTimeMeasureId();
		if (comp != 0) {
			return comp;
		}
		comp = c1.getRuleId() - c2.getRuleId();
		return comp == 0 ? c1.getModeId() - c2.getModeId() : comp;
	}

	public String getCellNm() {
		return cellNm;
	}

	public void setCellNm(String cellNm) {
		this.cellNm = cellNm;
	}

	public Index getIndex() {
		return index;
	}

	public void setIndex(Index index) {
		this.index = index;
	}

	public CellStatus getStatus() {
		return status;
	}

	public void setStatus(CellStatus status) {
		this.status = status;
	}
}
