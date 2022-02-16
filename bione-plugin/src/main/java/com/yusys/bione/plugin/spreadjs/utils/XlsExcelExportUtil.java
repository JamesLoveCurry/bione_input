package com.yusys.bione.plugin.spreadjs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class XlsExcelExportUtil {
	
	private OutputStream output;
	private Workbook wb=null;
	private List<Map<String,Object>> infoList=null;
	private LinkedHashMap<String,String> columnInfo=null;
	private String path=null;
	private HttpServletResponse response;
	private String sheetNm = "指标查询";
	private Map<String,Integer> colMap=new HashMap<String, Integer>();
	
//	public XlsExcelExportUtil(HttpServletResponse response,List<Map<String, Object>> infoList,String columnInfo,String path,String sheetNm){
//		this.response=response;
//		this.infoList= infoList ;
//		this.columnInfo=(LinkedHashMap<String, String>) JsonObjTrans.jsonToMap(columnInfo);
//		this.path=path;
//		this.sheetNm = sheetNm;
//	}

//	@SuppressWarnings("unchecked")
//	public XlsExcelExportUtil(HttpServletResponse response,List<Map<String, Object>> infoList,String columnInfo,String path){
//		this.response=response;
//		this.infoList= infoList ;
//		this.columnInfo=(LinkedHashMap<String, String>) JsonObjTrans.jsonToMap(columnInfo);
//		this.path=path;
//	}
//	
//	
	public XlsExcelExportUtil(HttpServletResponse response,List<Map<String, Object>> infoList,LinkedHashMap<String,String>head,String path,String sheetNm){
		this.response=response;
		this.columnInfo= head ;
		this.infoList= infoList ;
		this.path=path;
		this.sheetNm = sheetNm;
	}
//	
//	/**
//	 * 金标全量下载--生成多个Excel表格
//	 * @author zhaoqh
//	 * @Date 20170918
//	 * @param infoList
//	 * @return
//	 * @throws IOException 
//	 */
//	public void generateFilePath(List<Map<String, Object>> infoList,String path,String tableNm,int pageSize) throws IOException{
//		
//		File file = null;
//		Workbook workBook = null;
//		//工作表对象
//		Sheet sheet = null;
//		Row row = null;
//		Cell cell = null;
//		CellStyle cellStyle = null;
//		int rowNo = 0;
//		int pageRowNo = 1;
//		
//		/*//int index = rowNo / 50000;
//		//if (rowNo % 50000 == 0) {
//			String filePath = path + "/export/rpt/idx/" + tableNm + "_" + System.currentTimeMillis()+ ".xlsx";
//			//logger.info("生成第"+(index+1)+"个文件,路径为:"+filePath);
//			file = new File(filePath);
//			if (!file.exists()) {
//				
//				file.createNewFile();
//	        }
//			workBook = new SXSSFWorkbook(100);
//			
//			cellStyle = workBook.createCellStyle();
//			DataFormat format = workBook.createDataFormat();
//			cellStyle.setDataFormat(format.getFormat("@"));
//			sheet = workBook.createSheet(sheetNm);
//			creatHeader(sheet, workBook);
//			pageRowNo = 1;
//		//}
//*/		
//		for(Map<String, Object> info : infoList){
//			//int index = rowNo / 50000;
//			if (rowNo % pageSize == 0) {
//				String newFilePath = path +"/"+ tableNm + "_" + System.currentTimeMillis()+".xlsx";
//				file = new File(newFilePath);
//				if (!file.exists()) {
//					file.createNewFile();
//		        }
//				logger.info("Excel文件已生成,路径为:"+newFilePath);
//				workBook = new SXSSFWorkbook(100);
//				
//				cellStyle = workBook.createCellStyle();
//				DataFormat format = workBook.createDataFormat();
//				cellStyle.setDataFormat(format.getFormat("@"));
//				sheet = workBook.createSheet(sheetNm);
//				creatHeader(sheet, workBook);
//				pageRowNo = 1;
//			}
//			rowNo++;
//			row = sheet.createRow(pageRowNo++);
//			for (String key : info.keySet()) {
//				if (this.colMap.get(key) != null) {
//					int col = this.colMap.get(key);
//					cell = row.createCell(col);
//					cell.setCellStyle(cellStyle);
//					setCellValue(cell, info.get(key));
//				}
//			}
//			/*if(rowNo % 50000 == 0){
//				FileOutputStream fileOutputStream = new FileOutputStream(file);
//				workBook.write(fileOutputStream);
//				fileOutputStream.flush();
//				fileOutputStream.close();
//			}*/
//		}
//		FileOutputStream fileOutputStream = new FileOutputStream(file);
//		workBook.write(fileOutputStream);
//		fileOutputStream.flush();
//		fileOutputStream.close();
//		//return file.getAbsolutePath();
//	}
//	
//	/**
//	 * 多个文件压缩
//	 * @throws IOException 
//	 */
//	
//	@SuppressWarnings({ "deprecation", "unchecked" })
//	public File generateZIPFilePath(List<Map<String, Object>> infoList,String path, String tableNm, long currentTime,File directory) throws IOException{
// 		/**
// 		 * 生成的Excel表格在currentTime文件路径下
// 		 */
//		/*String path = this.getRealPath()+"/export/rpt/idx/"+tableNm+"_"+currentTime;
//				File directory = new File(path);*/
//		
//		
//		//files = file.listFiles();
//		
//		String zipFilePath = path + "/export/rpt/idx/";
//		//File zipFile = new File(filePath);
//		//File [] zipFiles = zipFile.listFiles();
//		//List<File> fileList = Arrays.asList(zipFiles);
//		
//		String zipPath = zipFilePath +tableNm+"_"+ System.currentTimeMillis()+".zip";
//		File fileZip = new File(zipPath);
//		if(!fileZip.exists()){
//			fileZip.createNewFile();
//		}
//		logger.info("ZIP压缩文件已生成,路径为:"+zipPath);
//		FileOutputStream zipOutStream = new FileOutputStream(fileZip);
//		ZipOutputStream zipOut = new ZipOutputStream(zipOutStream);
//		
//		String currentPath = path + "/export/rpt/idx/"+tableNm+"_"+currentTime;
//		File currentFile = new File(currentPath);
//		File [] currentFiles = currentFile.listFiles();
//		List<File> currentFileList = Arrays.asList(currentFiles);
//		
//		zipFile(currentFileList,zipOut);
//		zipOut.close();
//		zipOut.flush();
//		zipOutStream.flush();
//		zipOutStream.close();
//		
//		/**
//		 * 压缩完成后,删除路径idx下生成的currentTime中的Excel文件以及currentTime文件夹
//		 */
//		String filePath = path + "/";
//		File existFile = new File(currentPath);
//		File [] oldFile = existFile.listFiles();
//		if(existFile.isDirectory()){
//			if(oldFile.length > 0){
//				for(int i = 0; i < oldFile.length; i++){
//					File temp = oldFile[i];
//					if(!temp.getName().contains(".zip")){
//						temp.delete();
//					}
//				}
//			} 
//		}
//		directory.delete();
//		
//		return fileZip;
//	}
//	
//	@SuppressWarnings("deprecation")
//	public static void zipFile(List<File> files, ZipOutputStream outputStream) throws IOException{
//		for (File file : files) {
//			if(!file.isDirectory()){
//				FileInputStream fileIn = new FileInputStream(file);
//				
//				BufferedInputStream buffInput = new BufferedInputStream(fileIn, 1024);
//				com.ec.system.utility.zip.ZipEntry ze = new com.ec.system.utility.zip.ZipEntry(file.getName());
//				outputStream.putNextEntry(ze);
//				
//				int number;
//				byte[] buffer = new byte[1024];
//				while((number = buffInput.read(buffer)) != -1){
//					outputStream.write(buffer, 0, number);
//				}
//				buffInput.close();
//				fileIn.close();
//			} else {
//				File[] filess = file.listFiles();
//				for(int i =0; i < filess.length; i++){
//					zipFile(filess[i],outputStream);
//				}
//			}
//		}
//	}
//	
//	@SuppressWarnings("deprecation")
//	public static void zipFile(File file, ZipOutputStream outputStream) throws IOException{
//		if(!file.isDirectory()){
//			FileInputStream fileIn = new FileInputStream(file);
//			
//			BufferedInputStream buffInput = new BufferedInputStream(fileIn, 1024);
//			com.ec.system.utility.zip.ZipEntry ze = new com.ec.system.utility.zip.ZipEntry(file.getName());
//			outputStream.putNextEntry(ze);
//			
//			int number;
//			byte[] buffer = new byte[1024];
//			while((number = buffInput.read(buffer)) != -1){
//				outputStream.write(buffer, 0, number);
//			}
//			buffInput.close();
//			fileIn.close();
//		} else {
//			File[] files = file.listFiles();
//			for(int i =0; i < files.length; i++){
//				zipFile(files[i],outputStream);
//			}
//		}
//	}
//	/**
//	 * 金标全量下载--一个Excel表格生成多个sheet页
//	 * @author zhaoqh
//	 * @Date 20170918
//	 * @param infoList
//	 * @return
//	 * @throws IOException 
//	 */
//	public String generateFilePath(List<Map<String, Object>> infoList) throws IOException{
//		File file = new File(path);
//		//String filePath = this.getRealPath() + "/export/rpt/idx/" + "aaaaas" + "_" + System.currentTimeMillis() + ".xls";
//		Workbook workBook = new SXSSFWorkbook(100);
//		//Workbook workBook = new HSSFWorkbook();
//		//工作表对象
//		Sheet sheet = null;
//		Row row = null;
//		Cell cell = null;
//		int rowNo = 0;
//		int pageRowNo = 1;
//		if (!file.exists()) {
//			file.createNewFile();
//        }
//		CellStyle cellStyle = workBook.createCellStyle();
//		DataFormat format = workBook.createDataFormat();
//		cellStyle.setDataFormat(format.getFormat("@"));
//		int index = rowNo / 100000;
//		for(Map<String, Object> info : infoList){
//			//每个sheet页下载200000行数据,超过200000行存储到下一个工作表
//			if (rowNo % 100000 == 0) {
//				sheet = workBook.createSheet(sheetNm + "_" + index);
//				creatHeader(sheet, workBook);
//				sheet = workBook.getSheetAt(index);
//				pageRowNo = 1;
//			}
//			rowNo++;
//			
//			row = sheet.createRow(pageRowNo++);
//			for (String key : info.keySet()) {
//				if (this.colMap.get(key) != null) {
//					int col = this.colMap.get(key);
//					cell = row.createCell(col);
//					cell.setCellStyle(cellStyle);
//					setCellValue(cell, info.get(key));
//				}
//			}
//		}
//		FileOutputStream fileOutputStream = new FileOutputStream(file);
//		workBook.write(fileOutputStream);
//		fileOutputStream.flush();
//		fileOutputStream.close();
//		return file.getAbsolutePath();
//	}
//	
//	/**
//	 * @author  zhaoqh
//	 * @Date 20170918
//	 * @param sheet
//	 * @param wb
//	 */
//	private  void creatHeader(Sheet sheet,Workbook wb){
//		Row row=sheet.createRow(0);
//		if(columnInfo.size()>0){
//			int col=0;
//			//设置头部字体加粗
//			CellStyle cellStyle = wb.createCellStyle();
//			Font font =wb.createFont();
//			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
//			cellStyle.setFont(font);
//			for(String key:columnInfo.keySet()){
//				Cell cell=row.createCell(col);
//				cell.setCellStyle(cellStyle);
//				cell.setCellValue(columnInfo.get(key));
//				colMap.put(key, col);
//				col++;
//			}
//		}
//	}
//	
//	/**
//	 * 创建下载文件，并返回路径
//	 * @return
//	 */
//	public String generateFile(){
//		File file=new File(path);
//		if (! file.exists()) {
//			try {
//				file.createNewFile();
//				
//			} catch (IOException e) {
//				e.printStackTrace();
//				return null;
//			}
//        }
//		try {
//			output=new FileOutputStream(file);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			return null;
//		}
//		wb=new HSSFWorkbook();
//		
//		Sheet sheet =  wb.createSheet(sheetNm);
//		creatHeader(sheet);
//		creatBody(sheet);
//		try {
//			wb.write(output);
//			if(output!=null){
//				output.close();
//			}
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		return file.getAbsolutePath();
//	}
//	
	public  void exportFile(){
		File file=new File(path);
		if (! file.exists()) {
			try {
				file.createNewFile();
				
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
        }
		try {
			output=new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		wb=new HSSFWorkbook();
		
		Sheet sheet =  wb.createSheet(sheetNm);
		creatHeader(sheet);
		creatBody(sheet);
		try {
			wb.write(output);
			if(output!=null){
				output.close();
			}
			ExportUtil.download(response, file, "application/octet-stream");
			file.delete();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	private  void creatHeader(Sheet sheet){
		Row row=sheet.createRow(0);
		if(columnInfo.size()>0){
			int col=0;
			//设置头部字体加粗
			CellStyle cellStyle = wb.createCellStyle();
			Font font =wb.createFont();
			//font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			cellStyle.setFont(font);
			for(String key:columnInfo.keySet()){
				Cell cell=row.createCell(col);
				cell.setCellStyle(cellStyle);
				cell.setCellValue(columnInfo.get(key));
				colMap.put(key, col);
				col++;
			}
		}
	}
	
	private  void creatBody(Sheet sheet){
		if(infoList.size()>0){
			int rol=1;
			//设置单元格为文本格式
			CellStyle cellStyle = wb.createCellStyle();
			DataFormat format = wb.createDataFormat();
			cellStyle.setDataFormat(format.getFormat("@"));
			for(Map<String,Object> info:infoList){
				Row row=sheet.createRow(rol);
				//creatValue(row, info);
				creatValueAndStyle(row, info,cellStyle);
				rol++;
			}
		}
	}
		
//	private  void creatValue(Row row,Map<String,Object> infos){
//		if(infos.size()>0){
//			//设置单元格为文本格式
//			CellStyle cellStyle = wb.createCellStyle();
//			DataFormat format = wb.createDataFormat();
//			cellStyle.setDataFormat(format.getFormat("@"));
//			for(String key:infos.keySet()){
//				if(this.colMap.get(key) != null){
//					int col=this.colMap.get(key);
//					Cell cell=row.createCell(col);
//					cell.setCellStyle(cellStyle);
//					setCellValue(cell,infos.get(key));
//				}
//			}
//		}
//	}
	private  void creatValueAndStyle(Row row,Map<String,Object> infos,CellStyle cellStyle){
		if(infos.size()>0){
			for(String key:infos.keySet()){
				if(this.colMap.get(key) != null){
					int col=this.colMap.get(key);
					Cell cell=row.createCell(col);
					cell.setCellStyle(cellStyle);
					setCellValue(cell,infos.get(key));
				}
			}
		}
	}
	private void setCellValue(Cell cell, Object value) {
        if (value == null || cell == null) {
            return;
        } else if (value instanceof Number) {
            double doubleValue = ((Number) value).doubleValue();
            cell.setCellValue(doubleValue);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        } else if (isFormulaDefinition(value)) {
            cell.setCellFormula(getFormula(value));
        } else {
            cell.setCellValue(value.toString());
        }
    }
	private boolean isFormulaDefinition(Object obj) {
        if (obj instanceof String) {
            String str = (String) obj;
            if (str.length() < 2) {
                return false;
            } else {
                return ((String) obj).charAt(0) == '=';
            }
        } else {
            return false;
        }
    }
	
	private String getFormula(Object obj) {
        return ((String) obj).substring(1);
    }
	
	
//	public void exportEcxel(){
//		
//	}
//
}
