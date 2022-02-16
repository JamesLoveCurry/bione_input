package com.yusys.bione.plugin.spreadjs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.comp.utils.DownloadUtils;

public class CsvExportUtil {
	
	 /** CSV文件列分隔符 */  
    private String CSV_COLUMN_SEPARATOR = "|";  
  
    /** CSV文件列分隔符 */  
    private static final String CSV_RN = "\r\n";  
    
	private OutputStream output;
	private List<Map<String,Object>> infoList=null;
	private LinkedHashMap<String,String> columnInfo=null;
	private String path=null;
	private HttpServletResponse response;
	private List<String> matchColNamesMapArr = new ArrayList<String>();
	private File file = null;
	
	public CsvExportUtil(HttpServletResponse response,List<Map<String, Object>> infoList,LinkedHashMap<String, String> columnInfo,String path){
		this.response=response;
		this.infoList= infoList ;
		this.columnInfo=columnInfo;
		this.path=path;
	}
	
	public CsvExportUtil(HttpServletResponse response,List<Map<String, Object>> infoList,LinkedHashMap<String, String> columnInfo,String path,String tag){
		this.response=response;
		this.infoList= infoList ;
		this.columnInfo=columnInfo;
		this.path=path;
		if(StringUtils.isNotBlank(tag)){
			this.CSV_COLUMN_SEPARATOR = tag;
		}
	}
	
	 /** 
     *  
     * 将检索数据输出的对应的csv列中 
     * */  
    private  String formatCsvData(List<Map<String, Object>> data,  
    		LinkedHashMap<String, String> columnInfo) {  
  
        StringBuilder buf = new StringBuilder();  
  
        List<String> displayColNamesArr = new ArrayList<String>();  
  
       for(String key : columnInfo.keySet()){
    	   displayColNamesArr.add(columnInfo.get(key));
    	   this.matchColNamesMapArr.add(key);
       }
  
        // 输出列头  
        for (int i = 0; i < displayColNamesArr.size(); i++) {  
            buf.append(displayColNamesArr.get(i)).append(CSV_COLUMN_SEPARATOR);  
        }  
        buf.append(CSV_RN);  
  
        if (null != data) {  
            // 输出数据  
            for (int i = 0; i < data.size(); i++) {  
                for (int j = 0; j < matchColNamesMapArr.size(); j++) {  
                	Object value = data.get(i).get(matchColNamesMapArr.get(j));
                	if(value!=null)
	                    buf.append(value).append(  
	                            CSV_COLUMN_SEPARATOR);  
                	else
                		 buf.append("").append(  
 	                            CSV_COLUMN_SEPARATOR);  
                }  
                buf.append(CSV_RN);  
            }  
        }  
        return buf.toString();  
    }  
    
    public void addRow(Map<String, Object> data){
    	StringBuilder buf = new StringBuilder();  
    	for (int j = 0; j < matchColNamesMapArr.size(); j++) {  
        	Object value = data.get(matchColNamesMapArr.get(j));
        	if(value!=null)
                buf.append(value).append(  
                        CSV_COLUMN_SEPARATOR);  
        	else
        		 buf.append("").append(  
                         CSV_COLUMN_SEPARATOR);  
        }  
        buf.append(CSV_RN);  
        try {
			output.write(buf.toString().getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public  void exportFile(String sheetName){
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
		String content = formatCsvData(infoList, columnInfo);
		try {
			output.write(content.getBytes("GBK"));
			if(output!=null){
				output.close();
			}
			DownloadUtils.download(response, file);
			file.delete();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public String createFile(String sheetName){
		File file=new File(path);
		if (! file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return "";
			}
        }
		String content = formatCsvData(infoList, columnInfo);
		try {
			
			output=new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "";
		}
		try {
			output.write(content.getBytes("GBK"));
			if(output!=null){
				output.close();
			}
			return file.getPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return "";
	}
	
	public void createCsv(String sheetName){
		this.file=new File(path);
		if (! file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		String content = formatCsvData(infoList, columnInfo);
		try {
			output=new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			output.write(content.getBytes("GBK"));
			if(output!=null){
				output.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public String finish(){
		try {
			output.close();
			return file.getPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
}
