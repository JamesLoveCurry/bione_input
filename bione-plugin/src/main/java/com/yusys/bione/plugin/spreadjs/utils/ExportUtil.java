package com.yusys.bione.plugin.spreadjs.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ExportUtil {

	public static String getExcelName(String exportPath, String name) {
		String realPath = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest().getSession()
				.getServletContext().getRealPath("/");
		File file = new File(realPath + exportPath);
		if (!file.isDirectory()) {
			file.mkdirs();
		}
		return realPath + exportPath + File.separator + name + "_"
				+ System.currentTimeMillis() + ".xlsx";
	}
	
	/**
	 * 导出文件
	 * 
	 * @param response
	 * @param file
	 *            导出文件
	 * @param contentType
	 * @throws IOException
	 */
	public static void download(HttpServletResponse response, File file,
			String contentType) throws IOException {

		FileInputStream inputStream = new FileInputStream(file);

		String fileName = new String(file.getName().getBytes("GBK"),
				"ISO8859_1");

		response.setContentType(StringUtils.isEmpty(contentType) ? "application/octet-stream"
				: contentType);
		response.setHeader("Content-Disposition", "attachment;filename="
				+ fileName);
		response.setStatus(HttpServletResponse.SC_OK); // 避免超时
		BufferedInputStream reader = null;
		try {
			OutputStream output = response.getOutputStream();
			reader = new BufferedInputStream(inputStream);
			int b = -1;
			int buff = 0;
			while ((b = reader.read()) != -1) {
				output.write(b);
				buff++;
				if (buff > 10240) {
					buff = 0;
					output.flush();
				}
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

}
