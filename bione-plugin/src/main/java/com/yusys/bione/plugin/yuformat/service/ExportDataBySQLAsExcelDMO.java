package com.yusys.bione.plugin.yuformat.service;

import java.io.File;

import javax.servlet.ServletOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;

/**
 * 通过sql语句查询数据，使用指定excel模板导出excel文件，提供下载
 * @author liangzy5
 *
 */
public class ExportDataBySQLAsExcelDMO extends AbstractDownloadFile {
	
	private YuFormatUtil bsUtil = new YuFormatUtil();
	
	//设置下载类型
	@Override
	public int getDownloadType() {
		return AbstractDownloadFile.WriteServletOutputStream;
	}

	public void downLoaByWriteServletOut(JSONObject _par, ServletOutputStream _servertOut) throws Exception {
		
		String fileName = _par.getString("FileName");
		String str_ds = _par.getString("Ds"); //来源数据库
		String str_sql = _par.getString("SQL"); //传入SQL
		String headRow = _par.getString("HeadRow"); //表头占的行数，比如表头占了1行就是1，占了两行就是2
		JSONArray jsy_cols = _par.getJSONArray("Cols"); //所有列!

		String[] str_cols = new String[jsy_cols.size()]; //字段名

		for (int i = 0; i < jsy_cols.size(); i++) {
			str_cols[i] = jsy_cols.getString(i); 
		}

		//查询数据
		HashVO[] hvs_data = null;
		if (StringUtils.isEmpty(str_ds)) {
			hvs_data = bsUtil.getHashVOs(str_sql);
		} else {
			hvs_data = bsUtil.getHashVOsByDS(str_ds, str_sql); // 从哪个表取数!
		}
		
		String templateFilePath = this.getRealPath()
				+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
				+ GlobalConstants4plugin.EXPORT_CRRS_TEMPLATE_PATH + fileName; //模板统一放在一个路径下，通过不同文件名称去指定excel模板
		
		//读取指定excel模板,支持xls和xlsx
		Workbook book = WorkbookFactory.create(new File(templateFilePath));
		Sheet sheet= book.getSheetAt(0);
		Row row = null;
		Cell cell = null;
		//根据指定的列,计算出数据!
		int headRowNum = Integer.parseInt(headRow); //表头占的行数
		for (int i = 0; i < hvs_data.length; i++) {
			row = sheet.createRow(i+headRowNum);
			for (int j = 0; j < str_cols.length; j++) {
				cell = row.createCell(j);
				cell.setCellValue(hvs_data[i].getStringValue(str_cols[j]));
			}
		}
		
		book.write(_servertOut); //写流!
		book.close();
	}
	
	private String getRealPath() {
		String path = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
				.getSession().getServletContext().getRealPath("/");
		return path;
	}
}
