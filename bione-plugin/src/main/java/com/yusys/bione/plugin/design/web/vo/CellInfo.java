package com.yusys.bione.plugin.design.web.vo;

import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.util.ExcelLetterIntTrans;
import com.yusys.bione.plugin.spreadjs.entity.DataTablePropertyProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellInfo {
	private String cellNo;
	protected int rowStart;
	protected int cellStart;
	protected int length = 1;
	private int rowl= 1;
	private int coll= 1;
	protected String initDirection;
	protected int rowEnd;
	protected int cellEnd;
	protected boolean isExt = false;
	private List<DataTablePropertyProperty> pros = new ArrayList<DataTablePropertyProperty>();
	protected String type;
	protected String ONm = "";
	protected Map<String, Object> extInfo = new HashMap<String, Object>();

	public Map<String, Object> getExtInfo() {
		return extInfo;
	}

	public void putExtInfo(String key, Object value) {
		extInfo.put(key, value);
	}

	public boolean getIsExt() {
		return isExt;
	}

	public int getRowStart() {
		return rowStart;
	}

	public int getCellEnd() {
		return cellEnd;
	}

	public int getRowEnd() {
		return rowEnd;
	}

	public int getCellStart() {
		return cellStart;
	}

	public String getType() {
		return type;
	}

	public int getLength() {
		return this.length;
	}

	
	public CellInfo(int rowStart, int cellStart) {
		this.rowStart = rowStart;
		this.cellStart = cellStart;
		this.type = "";
		initEndInfo();
	}

	public CellInfo(String cellNo, int rowStart, int cellStart, int length,
			String type, Map<String, Object> extInfo) {
		this.rowStart = rowStart;
		this.cellStart = cellStart;
		this.length = length;
		this.type = type;
		this.extInfo = extInfo;
		extInfo.put("cellNo", cellNo);
		extInfo.put("type", type);
		initEndInfo();
	}
	
	public CellInfo(String cellNo, int rowStart, int cellStart, int length,int rowl,int coll,
			String type, Map<String, Object> extInfo) {
		this.rowStart = rowStart;
		this.cellStart = cellStart;
		this.length = length;
		this.rowl = rowl;
		this.coll = coll;
		this.type = type;
		this.extInfo = extInfo;
		extInfo.put("cellNo", cellNo);
		extInfo.put("type", type);
		initEndInfo();
	}

	protected void initEndInfo() {
		if (length <= 1
				|| !(this.type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE)
						|| this.type
								.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB) || this.type
							.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB)|| this.type
							.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA))) {
			cellEnd = cellStart;
			rowEnd = rowStart;
			return;
		} else {
			String extDirection = String.valueOf(this.extInfo
					.get("extDirection"));
			if (extDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_H)) {
				cellEnd = cellStart + length - 1;
				rowEnd = rowStart;
				isExt = true;
				return;
			}
			if (extDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_V)) {
				cellEnd = cellStart;
				rowEnd = rowStart + length - 1;
				isExt = true;
				return;
			}
			if (extDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_HV)) {
				cellEnd = cellStart + coll -1;
				rowEnd = rowStart + rowl - 1;
				isExt = true;
				return;
			}
		}

	}

	public void addDataTablePropertyProperty(DataTablePropertyProperty pro) {
		this.pros.add(pro);
	}

	public void addDataTablePropertyProperty(
			List<DataTablePropertyProperty> pros) {
		this.pros.addAll(pros);
	}

	public String checkInfo(int rowNum, int cellNum) {
		String extMode = String.valueOf(this.extInfo.get("extMode"));
		if (cellNum == this.cellStart && rowNum == this.rowStart) {
			if (initDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_H)) {
				if (extMode != null
						&& extMode.equals(GlobalConstants4plugin.EXT_MODE_OVER)) {
					if (cellStart + 1 <= cellEnd)
						return "pass";
				}
				return rowNum + "," + (cellEnd + 1) + "," + initDirection;
			}
			if (initDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_V)) {
				if (extMode != null
						&& extMode.equals(GlobalConstants4plugin.EXT_MODE_OVER)) {
					if (rowStart + 1 <= rowEnd)
						return "pass";
				}
				return (rowEnd + 1) + "," + cellNum + "," + initDirection;
			}
			if (initDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_HV)) {
				int row = rowNum;
				int col = cellNum;
				String extd ="";
				if(cellNum > cellStart){
					if (extMode != null
							&& extMode.equals(GlobalConstants4plugin.EXT_MODE_OVER)) {
						if (cellStart + 1 <= cellEnd)
							return "pass";
					}
					col = cellEnd + 1;
					extd +=GlobalConstants4plugin.EXT_DIRECTION_H;
				}
				if(rowNum > rowStart){
					if (extMode != null
							&& extMode.equals(GlobalConstants4plugin.EXT_MODE_OVER)) {
						if (rowStart + 1 <= rowEnd)
							return "pass";
					}
					row = rowEnd + 1;
					extd +=GlobalConstants4plugin.EXT_DIRECTION_V;
				}
				if(extd.equals(GlobalConstants4plugin.EXT_DIRECTION_H+GlobalConstants4plugin.EXT_DIRECTION_V))
					extd = GlobalConstants4plugin.EXT_DIRECTION_HV;
				return row + "," + col + "," + extd;
			}
		}
		if (isExt && cellNum >= this.cellStart && cellNum <= this.cellEnd
				&& rowNum >= this.rowStart && rowNum <= this.rowEnd) {
			String extDirection = String.valueOf(this.extInfo
					.get("extDirection"));
			if (extMode.equals(GlobalConstants4plugin.EXT_MODE_INSERT)) {
				if (extDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_H)) {
					return rowNum + "," + (cellEnd + 1) + "," + extDirection;
				}
				if (extDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_V)) {
					return (rowEnd + 1) + "," + cellNum + "," + extDirection;
				}
				if (extDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_HV)) {
					int row = rowNum;
					int col = cellNum;
					String extd ="";
					if(cellNum > cellStart){
						col = cellEnd + 1;
						extd +=GlobalConstants4plugin.EXT_DIRECTION_H;
					}
					if(rowNum > rowStart){
						row = rowEnd + 1;
						extd +=GlobalConstants4plugin.EXT_DIRECTION_V;
					}
					if(extd.equals(GlobalConstants4plugin.EXT_DIRECTION_H+GlobalConstants4plugin.EXT_DIRECTION_V))
						extd = GlobalConstants4plugin.EXT_DIRECTION_HV;
					return row + "," + col + "," + extd;
				}
			}
			if (extMode.equals(GlobalConstants4plugin.EXT_MODE_OVER)) {
				return "pass";
			}
		}
		return "";
	}

	public void setInitDirection(String initDirection) {
		this.initDirection = initDirection;
	}

	public String getRegion() {
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cellStart + 1, cellNmBf);
		String FNm = cellNmBf.toString() + (rowStart + 1);
		cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cellEnd + 1, cellNmBf);
		String ENm = cellNmBf.toString() + (rowEnd + 1);
		if (FNm.equals(ENm)) {
			return FNm;
		} else {
			return FNm + ":" + ENm;
		}
	}

	@SuppressWarnings("unchecked")
	public String getRegions(String tag) {
		StringBuilder regions = new StringBuilder("");
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cellStart + 1, cellNmBf);
		String FNm = cellNmBf.toString() + (rowStart + 1);
		regions.append(FNm);
		if (this.getExtDirection() == null) {
			return regions.toString();
		}
		if (this.getExtDirection().equals(GlobalConstants4plugin.EXT_DIRECTION_H)) {
			int j = 0;
			for (int i = cellStart + 1; i <= cellEnd; i++) {
				if (this.getExtInfo().get("totalType") != null) {
					if (!((List<String>) this.getExtInfo().get("totalType"))
							.get(j).equals(GlobalConstants4plugin.IDXTAB_DETAILS)) {
						continue;
					}
				}
				cellNmBf = new StringBuilder("");
				ExcelLetterIntTrans.intToLetter(i + 1, cellNmBf);
				String ENm = cellNmBf.toString() + (rowStart + 1);
				regions.append(tag).append(ENm);
			}
			j++;
		}
		if (this.getExtDirection().equals(GlobalConstants4plugin.EXT_DIRECTION_V)) {
			int j = 0;
			for (int i = rowStart + 1; i <= rowEnd; i++) {
				if (this.getExtInfo().get("totalType") != null) {
					if (!((List<String>) this.getExtInfo().get("totalType"))
							.get(j).equals(GlobalConstants4plugin.IDXTAB_DETAILS)) {
						continue;
					}
				}
				String ENm = cellNmBf.toString() + (i + 1);
				regions.append(tag).append(ENm);
			}
			j++;
		}
		return regions.toString();
	}
	
	@SuppressWarnings("unchecked")
	public String getRegions(String tag,String extDirection) {
		StringBuilder regions = new StringBuilder("");
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cellStart + 1, cellNmBf);
		String FNm = cellNmBf.toString() + (rowStart + 1);
		regions.append(FNm);
		if (this.getExtDirection() == null) {
			return regions.toString();
		}
		if (extDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_H)) {
			int j = 0;
			if(tag.equals(":")){
				cellNmBf = new StringBuilder("");
				ExcelLetterIntTrans.intToLetter(cellEnd+1, cellNmBf);
				String ENm = cellNmBf.toString() + (rowStart + 1);
				regions.append(tag).append(ENm);
				regions = new StringBuilder("SUM("+regions.toString()+")");
			}
			else{
				for (int i = cellStart + 1; i <= cellEnd; i++) {
					if (this.getExtInfo().get("totalType") != null) {
						if (!((List<String>) this.getExtInfo().get("totalType"))
								.get(j).equals(GlobalConstants4plugin.IDXTAB_DETAILS)) {
							continue;
						}
					}
					String ENm = cellNmBf.toString() + (rowEnd + 1);
					regions.append(tag).append(ENm);
				}
			}
			j++;
		}
		if (extDirection.equals(GlobalConstants4plugin.EXT_DIRECTION_V)) {
			int j = 0;
			if(tag.equals(":")){
				String ENm = cellNmBf.toString() + (rowEnd + 1);
				regions.append(tag).append(ENm);
				regions = new StringBuilder("SUM("+regions.toString()+")");
			}
			else{
				for (int i = rowStart + 1; i <= rowEnd; i++) {
					if (this.getExtInfo().get("totalType") != null) {
						if (!((List<String>) this.getExtInfo().get("totalType"))
								.get(j).equals(GlobalConstants4plugin.IDXTAB_DETAILS)) {
							continue;
						}
					}
					String ENm = cellNmBf.toString() + (i + 1);
					regions.append(tag).append(ENm);
				}
			}
			j++;
		}
		return regions.toString();
	}

	public String getONm() {
		return ONm;
	}

	public void setONm(int row, int cell) {
		StringBuilder cellNmBf = new StringBuilder("");
		ExcelLetterIntTrans.intToLetter(cell + 1, cellNmBf);
		ONm = cellNmBf.toString() + (row + 1);
	}

	public DataTablePropertyProperty getDataTablePropertyProperty() {
		DataTablePropertyProperty pro = new DataTablePropertyProperty();
		if (pros != null && pros.size() > 0)
			return pros.get(0);
		return pro;
	}

	public List<DataTablePropertyProperty> getDataTablePropertyPropertys() {
		return pros;
	}

	public String getCellNo() {
		return cellNo;
	}

	public String getFormula() {
		if (type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_FORMULA))
			return String.valueOf(this.extInfo.get("formula"));
		return null;
	}

	public String getExpression() {
		if (type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_STATICTEXT))
			return String.valueOf(this.extInfo.get("expression"));
		return null;
	}

	public String getIndexNo() {
		if (type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_IDX) || type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB))
			return String.valueOf(this.extInfo.get("indexNo"));
		return null;
	}
	
	public String getDimTypeNo() {
		if (type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB))
			return String.valueOf(this.extInfo.get("dimTypeNo"));
		return null;
	}

	public void setPri(boolean flag) {
		if (flag) {
			this.putExtInfo("isPri", "Y");
		} else {
			this.putExtInfo("isPri", "N");
			this.length = 1;
			this.isExt = false;
			this.pros.subList(0, 1);
			this.pros.get(0).setValue("--");
		}
	}

	public String getExtDirection() {
		if (type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE)
				|| type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_IDXTAB)
				|| type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_DIMTAB))
			return String.valueOf(this.extInfo.get("extDirection"));
		return null;
	}

	public String getExtMode() {
		if (type.equals(GlobalConstants4plugin.RPT_CELL_SOURCE_MODULE))
			return String.valueOf(extInfo.get("extMode"));
		return null;
	}

	@SuppressWarnings("unchecked")
	public Object getOldValues(int i) {
		List<Object> oldValues = (List<Object>) this.getExtInfo().get(
				"oldValues");
		if(oldValues == null || oldValues.size() == 0){
			return null;
		} else {
			return oldValues.get(i);
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object getSrcValues(int i) {
		List<Object> srcValues = (List<Object>) this.getExtInfo().get(
				"srcValues");
		return srcValues.get(i);
	}

	public String getDsId() {
		if (extInfo.get("dsId") != null)
			return String.valueOf(extInfo.get("dsId"));
		return null;
	}
}
