package com.yusys.bione.plugin.yuformat.service;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.utils.ExcelUtil;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;

/**
 * 导出Excel
 * @author xch
 *
 */
public class ExportExcelBySQLDMO extends AbstractDownloadFile {

	private YuFormatUtil bsUtil = new YuFormatUtil();
	private ExcelUtil excelUtil = new ExcelUtil();

	//设置下载类型..
	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.WriteServletOutputStream;
	}

	@Override
	public void downLoaByWriteServletOut(JSONObject _par, ServletOutputStream _servertOut) throws Exception {
		String str_sql = _par.getString("SQL");
		String str_sheetName = _par.getString("sheetName"); // sheet页名称
		String str_header = _par.getString("header"); // 表头
		String [] strArray =  str_sql.split(";");
		if (strArray.length > 1) {
			List<HashVO[]> hashVos = new ArrayList<HashVO[]>();
			
			for (String s: strArray) {
				HashVO[] hvs = bsUtil.getHashVOs(s);
				hashVos.add(hvs);
			}
			
			HSSFWorkbook book = excelUtil.exportExcelByHashVOsAsBook1(hashVos, str_sheetName, str_header);
			book.write(_servertOut);
		}  else {
			HashVO[] hvs = bsUtil.getHashVOs(str_sql);
			HSSFWorkbook book = excelUtil.exportExcelByHashVOsAsBook(hvs, str_sheetName, str_header);
			book.write(_servertOut);
		}
	}

}
