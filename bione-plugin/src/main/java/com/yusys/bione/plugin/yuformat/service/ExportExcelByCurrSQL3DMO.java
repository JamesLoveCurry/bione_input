package com.yusys.bione.plugin.yuformat.service;

import java.util.ArrayList;

import javax.servlet.ServletOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.utils.ExcelUtil;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

/**
 * 导出Excel
 * @author xch
 *
 */
public class ExportExcelByCurrSQL3DMO extends AbstractDownloadFile {

	private YuFormatUtil bsUtil = new YuFormatUtil();
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2();
	private ExcelUtil excelUtil = new ExcelUtil();

	//设置下载类型..
	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.WriteServletOutputStream;
	}

	@Override
	public void downLoaByWriteServletOut(JSONObject _par, ServletOutputStream _servertOut) throws Exception {
		String str_templetCode = _par.getString("TempletCode"); //模板编码
		String str_templetName = _par.getString("TempletName"); //模板名称
		String str_ds = _par.getString("Ds"); //来源数据库
		
		Object[] templeVO = bsUtil2.getTempletVO(str_templetCode); //
		HashVO[] hvs_b = (HashVO[]) templeVO[1]; // 第二个是模板子表VOs

		ArrayList<String> comBoxItems = new ArrayList<String>(); //
		for (int i = 0; i < hvs_b.length; i++) {
			if (hvs_b[i].getStringValue("itemtype").equals("下拉框")) {
				comBoxItems.add(hvs_b[i].getStringValue("itemkey")); //
			}
		}

		String str_sql = _par.getString("SQL3"); //当前CurrSQL3
		JSONArray cols = _par.getJSONArray("Cols"); //所有列!
		JSONArray aligns = _par.getJSONArray("Aligns"); //所有列布局!

		JSONArray jsy_cols = new JSONArray();
		JSONArray jsy_aligns = new JSONArray();
		
		for (int i = 0; i < cols.size(); i++) {
			if (cols.get(i).toString().indexOf("操作") == -1) {
				jsy_cols.add(cols.get(i));
			}
		}

		for (int i = 0; i < aligns.size(); i++) {
			if (aligns.get(i).toString().indexOf("操作") == -1) {
				jsy_aligns.add(aligns.get(i));
			}
		}
				
		String[] str_cols = new String[jsy_cols.size()]; //字段名
		String[] str_colNames = new String[jsy_cols.size()]; //字段显示名
		String[] str_col_aligns = new String[jsy_aligns.size()]; //列布局

		for (int i = 0; i < jsy_cols.size(); i++) {
			String str_item = jsy_cols.getString(i); //
			str_cols[i] = str_item.substring(0, str_item.indexOf("/"));
			str_colNames[i] = str_item.substring(str_item.indexOf("/") + 1, str_item.length());
		}
		for (int i = 0; i < jsy_aligns.size(); i++) {
			String str_item = jsy_aligns.getString(i); //
			str_col_aligns[i] = str_item.substring(str_item.indexOf("/") + 1, str_item.length());
		}

		//查询数据
		HashVO[] hvs_data = null;
		if (StringUtils.isEmpty(str_ds)) {
			hvs_data = bsUtil.getHashVOs(str_sql);
		} else {
			hvs_data = bsUtil.getHashVOsByDS(str_ds, str_sql); // 从哪个表取数!
		}

		//执行加载公式
		new CommDMO().dealAfterLoad(hvs_b, hvs_data); //

		//把下拉框的显示列再更新到实际列上，这样才会导出中文!
		if (comBoxItems.size() > 0) {
			for (int i = 0; i < hvs_data.length; i++) {
				for (int j = 0; j < comBoxItems.size(); j++) {
					String str_itemkey = comBoxItems.get(j); //
					hvs_data[i].setAttributeValue(str_itemkey, hvs_data[i].getStringValue(str_itemkey + "_◆view◆"));
				}
			}
		}

		//根据指定的列,计算出数据!
		HashVO[] hvs_data2 = new HashVO[hvs_data.length]; //
		for (int i = 0; i < hvs_data.length; i++) {
			hvs_data2[i] = new HashVO(); //
			for (int j = 0; j < str_cols.length; j++) {
				hvs_data2[i].setAttributeValue(str_cols[j], hvs_data[i].getStringValue(str_cols[j])); //
			}
		}

		HSSFWorkbook book = excelUtil.exportExcelByHashVOsAsBook(hvs_data2, str_templetName, str_colNames, str_col_aligns); //
		book.write(_servertOut);
	}
}
