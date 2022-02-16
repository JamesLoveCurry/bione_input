package com.yusys.bione.plugin.yuformat.service;

import java.util.ArrayList;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

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
public class ExportBillListDataAsExcelDMO extends AbstractDownloadFile {

	private YuFormatUtil bsUtil = new YuFormatUtil();
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2();

	private ExcelUtil excelUtil = new ExcelUtil(); //

	//设置下载类型
	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.WriteServletOutputStream;
	}

	@Override
	public void downLoaByWriteServletOut(JSONObject _par, ServletOutputStream _servertOut) throws Exception {
		String str_sql = _par.getString("SQL"); //
		String str_sheetName = _par.getString("sheetName"); //
		String str_templetCode = _par.getString("TempletCode"); //模板编码

		Object[] templetVO = bsUtil2.getTempletVO(str_templetCode); //
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		HashVO[] hvs_data = bsUtil.getHashVOs(str_sql); //查询数据!!
		new CommDMO().dealAfterLoad(hvs_b, hvs_data); //加载公式处理

		ArrayList<String> showItemKeys = new ArrayList<String>();
		ArrayList<String> showItemNames = new ArrayList<String>();
		ArrayList<Integer> showItemWidths = new ArrayList<Integer>();

		for (int i = 0; i < hvs_b.length; i++) {
			boolean isListShow = hvs_b[i].getBooleanValue("list_isshow", false);
			if (isListShow && !hvs_b[i].getStringValue("itemname").equals("操作")) {
				showItemKeys.add(hvs_b[i].getStringValue("itemkey")); //
				showItemNames.add(hvs_b[i].getStringValue("itemname")); //
				showItemWidths.add(hvs_b[i].getIntegerValue("list_width", 120)); //
			}
		}
		String[] str_keys = showItemKeys.toArray(new String[0]);
		String[] str_names = showItemNames.toArray(new String[0]);
		Integer[] li_widths = showItemWidths.toArray(new Integer[0]); //

		String[][] str_data = new String[hvs_data.length + 1][str_keys.length]; //

		//设置表头
		for (int i = 0; i < str_names.length; i++) {
			str_data[0][i] = str_names[i]; //表头
		}

		//设置内容
		for (int i = 0; i < hvs_data.length; i++) {
			for (int j = 0; j < str_keys.length; j++) {
				str_data[i + 1][j] = hvs_data[i].getStringValue(str_keys[j], ""); //
			}
		}

		int[] li_widths2 = new int[li_widths.length];
		for (int i = 0; i < li_widths2.length; i++) {
			li_widths2[i] = li_widths[i].intValue(); //
		}

		HSSFWorkbook book = excelUtil.exportExcelByStrArrayAsBook(str_data, li_widths2, str_sheetName, null); //
		book.write(_servertOut); //写流!
	}
}
