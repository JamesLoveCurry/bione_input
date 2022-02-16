/**
 * 
 */
package com.yusys.bione.frame.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.yusys.bione.frame.util.excel.WorkbookWrapper;

/**
 * @author tanxu
 * 
 */
public class ExcelImporter extends AbstractExcelImport{

	private String filePath;
	
	public ExcelImporter(Class<?> cla, File file){
		filePath = file.getAbsolutePath();
		this.cla = cla;
	}
	//不带密码
	public List<Object> ReadExcel() throws InstantiationException, IllegalAccessException{
		List<Object> list = new ArrayList<Object>();
		if(filePath != null) {
			Workbook workbook = null;
			try {
				workbook = WorkbookWrapper.openExcel(filePath);
				Sheet sheet= this.getSheet(workbook);
				if(sheet!=null){
					for(int i=firstRow;i<=sheet.getLastRowNum();i++){
						Row row=sheet.getRow(i);
						if(row!=null){
							Object obj=this.cla.newInstance();
							try {
								obj=createObj(obj,row,i,sheet.getSheetName());
								if(obj!=null)
									list.add(obj);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								break;
							} 
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				IOUtils.closeQuietly(workbook);
			}
		}
		return list;
	}
	//带密码
	public List<Object> ReadExcel(String password) throws InstantiationException, IllegalAccessException, EncryptedDocumentException, InvalidFormatException{
		List<Object> list = new ArrayList<Object>();
		if(filePath != null) {
			Workbook workbook = null;
			try {
				workbook = WorkbookWrapper.openExcel(filePath, password);
				Sheet sheet= this.getSheet(workbook);
				if(sheet!=null){
					for(int i=firstRow;i<=sheet.getLastRowNum();i++){
						Row row=sheet.getRow(i);
						if(row!=null){
							Object obj=this.cla.newInstance();
							try {
								obj=createObj(obj,row,i,sheet.getSheetName());
								if(obj!=null)
									list.add(obj);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								break;
							} 
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				IOUtils.closeQuietly(workbook);
			}
		}
		return list;
	}
}
