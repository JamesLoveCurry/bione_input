package com.yusys.bione.frame.excel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.yusys.bione.frame.excel.annotations.ExcelSheet;
import com.yusys.bione.frame.util.excel.WorkbookWrapper;

public class XlsExcelTemplateExporter extends AbstractExcelExporter{

	private List<List<?>> allEntitis;

	private OutputStream output;

	private String templatePath;

	private Integer firstRow = null;

	public XlsExcelTemplateExporter(String path, String templatePath,
			List<List<?>> allEntitis) throws FileNotFoundException {
		this(new File(path), templatePath, allEntitis);
	}

	public XlsExcelTemplateExporter(File file, String templatePath,
			List<List<?>> allEntitis) throws FileNotFoundException {
		this(new FileOutputStream(file), templatePath, allEntitis);
	}

	public XlsExcelTemplateExporter(OutputStream output, String templatePath,
			List<List<?>> allEntitis) {
		this.output = output;
		this.templatePath = templatePath;
		this.allEntitis = allEntitis;
	}

	public XlsExcelTemplateExporter(HttpServletResponse response, String fileName,
			String templatePath, List<List<?>> allEntitis)
			throws BioneExporterException {
		try {
			response.setHeader("Content-Disposition", "inline;filename=" + new String(fileName.getBytes("GB18030"), "ISO8859-1"));
			response.setContentType("application/application/-excel;charset=UTF-8");
			this.output = new BufferedOutputStream(response.getOutputStream());
			this.templatePath = templatePath;
			this.allEntitis = allEntitis;
		} catch (UnsupportedEncodingException e) {
			throw new BioneExporterException(e);
		} catch (IOException e) {
			throw new BioneExporterException(e);
		}
	}

	@Override
	public void run() throws BioneExporterException {
		Workbook workbook = null;
		try {
			workbook = WorkbookWrapper.openExcel(templatePath);
			ExcelSheet excelSheet = null;
			
			Sheet sheet;
			for (List<?> entitis : allEntitis) {
				Class<?> clazz;
				if (entitis.size() > 0 && (clazz = getEntitisClass(entitis)) != null
						&& clazz.isAnnotationPresent(ExcelSheet.class)) {
					excelSheet = clazz.getAnnotation(ExcelSheet.class);
					//lcy 改变columnBeans 定义位置 修复多Sheet数据写入异常
					List<ColumnBean> columnBeans = new ArrayList<ColumnBean>();
					columnBeans.add(ClassMapping.getInstance().getMappingColumn(clazz));
					while(!clazz.getSuperclass().equals(Object.class)){
						clazz = clazz.getSuperclass();
						columnBeans.add(ClassMapping.getInstance().getMappingColumn(clazz));
					}
					sheet = findSheet(workbook, excelSheet);
					if (sheet != null) {
						createBody(sheet, columnBeans, entitis, (this.firstRow == null ? excelSheet.firstRow() : firstRow), workbook);
						// 使用模板中的table，并修改table的范围
						//resetTable( findSheet(xwb, excelSheet), entitis.size() + excelSheet.firstRow() + ADDITION_LINE);
					}
				}
			}
			workbook.write(output);
		} catch (FileNotFoundException e) {
			throw new BioneExporterException("导出Excel使用的模板文件" + templatePath + "不存在.", e);
		} catch (IOException e) {
			throw new BioneExporterException(e);
		} finally {
			IOUtils.closeQuietly(workbook);
		}
	}

	private Sheet findSheet(Workbook swb, ExcelSheet excelSheet) {
		for (int i = 0; i < swb.getNumberOfSheets(); i++) {
			if (StringUtils.equals(String.valueOf(excelSheet.index()),
					StringUtils.substringBefore(swb.getSheetAt(i).getSheetName(), ExcelSheet.SP))) {
				return swb.getSheetAt(i);
			}
			else{
				if(StringUtils.equals(excelSheet.name(),swb.getSheetAt(i).getSheetName())){
					return swb.getSheetAt(i);
				}
			}
		}
		return null;
	}

	public void destory() throws BioneExporterException {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				throw new BioneExporterException(e);
			}
		}
	}

	public int getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}
	
	
}
