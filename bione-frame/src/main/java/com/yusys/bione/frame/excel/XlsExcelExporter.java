package com.yusys.bione.frame.excel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.util.excel.WorkbookWrapper;

public class XlsExcelExporter extends AbstractExcelExporter {

	public  static final int SHEET_SIZE=65000; //单个sheet数据最大条目数
	
	private HttpServletResponse response;

	private String filePath;
	
	private List<List<?>> allEntitis;

	public XlsExcelExporter(String filePath, List<List<?>> allEntitis) {
		this.filePath = filePath;
		this.allEntitis = allEntitis;
	}

	public XlsExcelExporter(HttpServletResponse response, String fileName, List<List<?>> allEntitis) {
		this.response = response;
		this.filePath = fileName;
		this.allEntitis = allEntitis;
	}
	
	@Override
	public void run() throws BioneExporterException {
		Workbook workbook = null;
		try {
			if (response == null) {
				workbook = WorkbookWrapper.create(filePath, false);
			} else {
				workbook = WorkbookWrapper.create(filePath.substring(filePath.lastIndexOf('.')), false);
			}
			ExcelSheet excelSheet = null;
			for (List<?> entitis :  allEntitis) {
				Class<?> clazz;
				List<ColumnBean> columnBeans = new ArrayList<ColumnBean>();
				if (entitis.size() > 0 && (clazz = getEntitisClass(entitis)) != null && clazz.isAnnotationPresent(ExcelSheet.class)) {
					excelSheet = clazz.getAnnotation(ExcelSheet.class);
					columnBeans.add(ClassMapping.getInstance().getMappingColumn(clazz));
					while(!clazz.getSuperclass().equals(Object.class)){
						clazz = clazz.getSuperclass();
						columnBeans.add(ClassMapping.getInstance().getMappingColumn(clazz));
					}
					int m=entitis.size()/SHEET_SIZE+1;
					for(int i=0;i<m;i++){
						Sheet sheet =m>1? workbook.createSheet(ExcelAnnotationUtil.getExcelSheetName(excelSheet,i)):workbook.createSheet(ExcelAnnotationUtil.getExcelSheetName(excelSheet));						
						List<?> entity=entitis.subList(i*SHEET_SIZE, ((i+1)*SHEET_SIZE>entitis.size())?entitis.size():(i+1)*SHEET_SIZE);
						createHead(sheet, columnBeans);
						createBody(sheet, columnBeans,entity , excelSheet.firstRow());	
					}
					entitis.clear();
				}
			}
			if (response != null) {
				DownloadUtils.printHeader(response, filePath, "application/-excel;charset=UTF-8");
				workbook.write(response.getOutputStream());
			}
		} catch (IOException e) {
			throw new BioneExporterException(e);
		} finally {
			IOUtils.closeQuietly(workbook);
		}
	}

	@Override
	public void destory() throws BioneExporterException{
	}
}
