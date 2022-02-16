package com.yusys.bione.plugin.yuformat.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class BillCellVO implements Serializable {

	private static final long serialVersionUID = 6202527480266853131L;

	private String id = null; //主键
	private String templetcode = null; //模板编码
	private String templetname = null; //模板名称
	private String billNo = null; //单据编码
	private String descr = null; //备注说明

	private int rowlength; //总共有几行
	private int collength; //总共有几列
	private String seq;

	private BillCellItemVO[][] cellItemVOs = null; //每个格子的实际数据

	private HashMap custMap = null; //可以放一个自定义对象

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getTempletcode() {
		return templetcode;
	}

	public void setTempletcode(String templetcode) {
		this.templetcode = templetcode;
	}

	public String getTempletname() {
		return templetname;
	}

	public void setTempletname(String templetname) {
		this.templetname = templetname;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}

	public int getRowlength() {
		return rowlength;
	}

	public void setRowlength(int rowlength) {
		this.rowlength = rowlength;
	}

	public int getCollength() {
		return collength;
	}

	public void setCollength(int collength) {
		this.collength = collength;
	}

	public BillCellItemVO[][] getCellItemVOs() {
		return cellItemVOs;
	}

	public void setCellItemVOs(BillCellItemVO[][] cellItemVOs) {
		this.cellItemVOs = cellItemVOs;
	}

	//取得其中某个CellKey的值.
	public String getValueAt(String _cellKey) {
		if (cellItemVOs == null) {
			return null;
		}

		for (int i = 0; i < cellItemVOs.length; i++) {
			for (int j = 0; j < cellItemVOs[i].length; j++) { //
				String str_cellKey = cellItemVOs[i][j].getCellkey(); //
				if (isEmpty(str_cellKey) && str_cellKey.equalsIgnoreCase(_cellKey)) { //
					return cellItemVOs[i][j].getCellvalue(); //返回值
				}
			}
		}

		return null;
	}

	private boolean isEmpty(String _value) {
		if (_value == null || _value.trim().equals("")) { // 如果为null或者为空字符串...
			return true;
		} else {
			return false;
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public void setCustProperty(Object _key, Object _value) {
		if (custMap == null) {
			custMap = new HashMap();
		}
		this.custMap.put(_key, _value); //
	}

	public Object getCustProperty(Object _key) {
		if (custMap == null) {
			return null;
		}
		return custMap.get(_key); //
	}

	//深度克隆一个!
	public BillCellVO deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this);
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (BillCellVO) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
