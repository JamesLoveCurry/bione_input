/**
 * 
 */
package com.yusys.biapp.input.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <pre>
 * Title:程序的中文名称
 * Description: 程序功能的描述
 * </pre>
 * 
 * @author caiqy caiqy@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Component(value = "FileUpAndDownUtils")
public class FileUpAndDownUtils {

	@Autowired
	private ExcelConstants excelConstants;

	private final Log logger = LogFactory.getLog(FileUpAndDownUtils.class);

	/**
	 * 规范路径，最后不要/
	 */
	private String standardPath(String path) {
		if (path == null)
			return null;
		if (path.endsWith("/"))
			return path.substring(0, path.length());
		return path;
	}

	/**
	 * 上传文件
	 * 
	 * @param templeID
	 * @param fileName
	 * @param remoteUrl
	 * @param listColumns
	 * @param listDetail
	 * @param colorList
	 * @return
	 */
	public String fileNetUpload(String templeID, String fileName,
			String remoteUrl, List<String> listColumns,
			List<String> listDetail, List<String> colorList,String tmpId) {

		/** EXCEL 工作区对象 */
		HSSFWorkbook workBook = excelConstants.getWorkBook(listColumns,
				listDetail, colorList, tmpId);

		BufferedOutputStream out = null;
		StringBuilder path = new StringBuilder(standardPath(remoteUrl));
		try {
			File file = null;
			file = new File(path.toString());
			if (!file.exists()) {
				file.mkdir();
			}
			path.append(File.separator + templeID);

			file = new File(path.toString()); // 根据FILEpath建立新的文件夹
			if (!file.exists()) {
				file.mkdirs();
			}
			path.append(File.separator + fileName);
			String newpath = path.toString();
			FileOutputStream fileOut = null;
			try {
				fileOut = new FileOutputStream(newpath);
				workBook.write(fileOut);// 把信息写入fileOutputStream中
			} catch (FileNotFoundException e) {
				logger.error("FileNotFoundException", e);
			} catch (IOException e) {
				logger.error("IOException", e);
			}

		} catch (Exception e) {
			logger.error(e);
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return path.toString();
	}
}
