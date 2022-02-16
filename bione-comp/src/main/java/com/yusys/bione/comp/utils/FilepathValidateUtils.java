package com.yusys.bione.comp.utils;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 
 * <pre>
 * Title:防路径操纵 校验工具
 * Description: 防路径操纵 校验工具
 * </pre>
 * 
 * @author maojin2
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class FilepathValidateUtils {

	private static Logger log = LoggerFactory.getLogger(FilepathValidateUtils.class);

	/**
	 * 白名单路径
	 **/
	private static List<String> list = new ArrayList<>();
	static {
		list.add("/export/design");
		list.add("/export/input");
		list.add("/export/frame");
		list.add("/export/rpt");
		list.add("/export/zip");
		list.add("/import/bione");
		list.add("/import/rpt");

		//金融基础
		PropertiesUtils bfd = PropertiesUtils.get("/biapp-bfd/exception/bfdMessage.properties");
		list.add(bfd.getProperty("data.download.filepath"));
		list.add(bfd.getProperty("data.download.zippath"));

		PropertiesUtils cams = PropertiesUtils.get("/biapp-cams/exception/camsMessage.properties");
		list.add(cams.getProperty("cams.reportfile.path"));
		list.add(cams.getProperty("cams.reportzip.path"));
		list.add(cams.getProperty("cams.receipt.path"));

		PropertiesUtils cr = PropertiesUtils.get("/biapp-cr/exception/crMessage.properties");
		list.add(cr.getProperty("filePath"));
		list.add(cr.getProperty("txtFileWriterPath"));
		list.add(cr.getProperty("txtFileReaderPath"));
		list.add(cr.getProperty("SOURCE_PATH"));
		list.add(cr.getProperty("SAVE_PATH"));
		list.add(cr.getProperty("BATH_PATH"));
		list.add(cr.getProperty("FEEDBACK_PATH"));
		list.add(cr.getProperty("cr.downloadfile.path"));
		list.add(cr.getProperty("cr.downloadzip.path"));

		PropertiesUtils crrs = PropertiesUtils.get("/biapp-crrs/exception/crrsMessage.properties");
		list.add(crrs.getProperty("crrs.reportfile.path"));
		list.add(crrs.getProperty("crrs.reportzip.path"));
		list.add(crrs.getProperty("crrs.validatefile.csv.path"));
		list.add(crrs.getProperty("crrs.validatezip.csv.path"));
		list.add(crrs.getProperty("crrs.validatefile.excel.path"));
		list.add(crrs.getProperty("crrs.validatezip.excel.path"));

		PropertiesUtils east = PropertiesUtils.get("/biapp-east/exception/eastMessage.properties");
		list.add(east.getProperty("east.reportfile.path"));
		list.add(east.getProperty("east.reportzip.path"));
		list.add(east.getProperty("east.downloadfile.path"));
		list.add(east.getProperty("east.downloadzip.path"));
		list.add(east.getProperty("east.nobank.msgupload.path"));
		list.add(east.getProperty("east.nobank.receipt.path"));
		list.add(east.getProperty("east.nobank.msguploadzip.path"));
		list.add(east.getProperty("data.download.filepath"));
		list.add(east.getProperty("data.download.zippath"));

		PropertiesUtils fsrs = PropertiesUtils.get("/biapp-fsrs/exception/fsrsMessage.properties");
		list.add(fsrs.getProperty("fsrs.reportfile.path"));
		list.add(fsrs.getProperty("fsrs.reportzip.path"));
		list.add(fsrs.getProperty("fsrs.downloadfile.path"));
		list.add(fsrs.getProperty("fsrs.downloadzip.path"));

		PropertiesUtils imas = PropertiesUtils.get("/biapp-imas/exception/imasMessage.properties");
		list.add(imas.getProperty("imas.reportfile.path"));
		list.add(imas.getProperty("imas.reportzip.path"));
		list.add(imas.getProperty("imas.reportimas.path"));
		list.add(imas.getProperty("imas.txtimas.path"));
		list.add(imas.getProperty("data.download.filepath"));
		list.add(imas.getProperty("data.download.zippath"));
		list.add(imas.getProperty("imas.direct.file.path"));

		PropertiesUtils pscs = PropertiesUtils.get("/biapp-pscs/exception/pscsMessage.properties");
		list.add(pscs.getProperty("pscs.reportfile.path"));
		list.add(pscs.getProperty("pscs.reportzip.path"));
		list.add(pscs.getProperty("pscs.reportsec.path"));

		PropertiesUtils safe = PropertiesUtils.get("/biapp-safe/exception/safeMessage.properties");
		list.add(safe.getProperty("safe.reportfile.path"));
		list.add(safe.getProperty("safe.reportzip.path"));
		list.add(safe.getProperty("safe.receipt.path"));

		PropertiesUtils wmp = PropertiesUtils.get("/biapp-wmp/config/wmp-config.properties");
		list.add(wmp.getProperty("wmp.reportfile.path"));
		list.add(wmp.getProperty("wmp.reportzip.path"));
		list.add(wmp.getProperty("wmp.downloadfile.path"));
		list.add(wmp.getProperty("wmp.downloadzip.path"));

		PropertiesUtils fileupload = PropertiesUtils.get("fileupload.properties");
		list.add(fileupload.getProperty("cfgtempPath"));
		list.add(fileupload.getProperty("errorReportPath"));
		list.add(fileupload.getProperty("rptDesignTmpPath"));
		list.add(fileupload.getProperty("systemMsgPath"));
		list.add(fileupload.getProperty("defaultPath"));
		list.add(fileupload.getProperty("filePath"));

		PropertiesUtils input = PropertiesUtils.get("input.properties");
		list.add(input.getProperty("UPLOAD_FILE_PATH"));

		list = list.stream().map(str -> FilenameUtils.normalize(str)).collect(Collectors.toList());
	}

	/**
	 * 防路径操纵 校验文件路径是否符合格式
	 * @param filename 路径
	 * @return
	 */
	public static boolean validateFilepath(String filename) {
		PropertiesUtils indexPro = PropertiesUtils.get("/bione-frame/index/index.properties");
		String pathManipulation = indexPro.getProperty("isPathManipulation");
		if(!Boolean.valueOf(pathManipulation)){
			return true;
		}

		filename = FilenameUtils.normalize(filename);
		if(filename.indexOf(File.separatorChar) == -1){
			return true;
		}
		String path = "";
		if(new File(filename).isDirectory()){
			path = filename;
		} else {
			int pos = filename.lastIndexOf(File.separatorChar);
			path = filename.substring(0, pos+1);
		}

		//过滤中文
		path = filterChinese(path);

		if(!isWhitelist(path)){
			log.error("error!!文件路径不在白名单中:" + path);
			return false;
		}

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < path.length(); i++) {
			sb.append(cleanChar(path.charAt(i)));
		}
		if(sb.toString().equals(path)){
			return true;
		} else {
			log.error("validateFilepath============================>>error!!文件路径中携带特殊字符,路径如下:" + path);
			return false;
		}
	}

	private static boolean isWhitelist(String path) {
		for (String str : list) {
			if(path.contains(str)){
				return true;
			}
		}
		return false;
	}

	/**
	 * @方法描述: 文件路径白名单字符
	 * @创建人: huzq1
	 * @创建时间: 2021/10/25 17:06
	  * @param aChar
	 * @return
	 **/
	private static char cleanChar(char aChar) {
		// 0 - 9
		for (int i = 48; i < 58; ++i) {
			if (aChar == i) return (char) i;
		}

		// 'A' - 'Z'
		for (int i = 65; i < 91; ++i) {
			if (aChar == i) return (char) i;
		}

		// 'a' - 'z'
		for (int i = 97; i < 123; ++i) {
			if (aChar == i) return (char) i;
		}
		// other valid characters
		switch (aChar) {
			case '/':
				return '/';
			case '-':
				return '-';
			case '.':
				return '.';
			case '_':
				return '_';
			case ':':
				return ':';
			case '\\':
				return '\\';
		}
		return '%';
	}

	/**
	 * 过滤掉中文
	 * @param str 待过滤中文的字符串
	 * @return 过滤掉中文后字符串
	 */
	public static String filterChinese(String str) {
		// 用于返回结果
		String result = str;
		boolean flag = isContainChinese(str);
		if (flag) {// 包含中文
			// 用于拼接过滤中文后的字符
			StringBuffer sb = new StringBuffer();
			// 用于校验是否为中文
			boolean flag2 = false;
			// 用于临时存储单字符
			char chinese = 0;
			// 5.去除掉文件名中的中文
			// 将字符串转换成char[]
			char[] charArray = str.toCharArray();
			// 过滤到中文及中文字符
			for (int i = 0; i < charArray.length; i++) {
				chinese = charArray[i];
				flag2 = isChinese(chinese);
				if (!flag2) {// 不是中日韩文字及标点符号
					sb.append(chinese);
				}
			}
			result = sb.toString();
		}
		return result;
	}

	/**
	 * 判断字符串中是否包含中文
	 * @param str
	 * 待校验字符串
	 * @return 是否为中文
	 * @warn 不能校验是否为中文标点符号
	 */
	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	/**
	 * 判定输入的是否是汉字
	 *
	 * @param c
	 *  被校验的字符
	 * @return true代表是汉字
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

}
