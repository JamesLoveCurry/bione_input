package com.yusys.bione.frame.base.web;

import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * 
 * <pre>
 * Title:基础Controller类型
 * Description: 实现部分公用方法，如文件上传、下拉图标选择等
 * </pre>
 * 
 * @author songxf
 * @version 1.00.00
 * 
 *          <pre>
 */
public abstract class BaseController {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private List<String> iconList = Arrays.asList("icon-manage", "icon-excel", "icon-text", "icon-version", "icon-save", "icon-save-01", "icon-bgColor", "icon-bgColor", "icon-menu", "icon-menu-01","icon-operation", "icon-operation", "icon-operation-mark", "icon-test", "icon-search", "icon-search-01", "icon-search-02", "icon-search-03", "icon-search-04", "icon-aligned", "icon-analyze", "icon-analyze-01","icon-analyze-02", "icon-setting", "icon-mergeCell", "icon-bold", "icon-arrow-down", "icon-arrow-down-01", "icon-content", "icon-detail", "icon-detail-01", "icon-italic", "icon-cancel", "icon-all", "icon-user", "icon-screening", "icon-delete", "icon-delete-01", "icon-setting-01", "icon-database", "icon-add", "icon-underline", "icon-chartForm", "icon-folder", "icon-message", "icon-add-01", "icon-xg-01", "icon-xg-02", "icon-xg-03", "icon-password", "icon-algin-right", "icon-zhibiao", "icon-resize", "icon-resize-01", "icon-out", "icon-assets", "icon-assets-01", "icon-fontSize", "icon-fontColor");
	
	/**
	 * 获得当前工程的url
	 */
	public String getProjectUrl() {
		String contextPath = GlobalConstants4frame.APP_CONTEXT_PATH;
		String imgUrl = contextPath + "/";
		return imgUrl;
	}

	/**
	 * 获取Spring管理的Bean
	 * 
	 * @param beanName
	 * @return
	 */
	public Object getSpringBean(String beanName) {
		return SpringContextHolder.getBean(beanName);
	}

	protected File saveUploadFile(File uploadFile, File targetFile) throws InterruptedException {
		if (! FilesUtils.isRealTypeFile(uploadFile)) {
			uploadFile.delete();
			throw new IllegalArgumentException("文件内容不符合文件扩展名所标志的文件类型");
		}
		int count = 0;
		while (targetFile.exists()) {
			if (targetFile.delete()) {
				break;
			}
			if (count >= 10) {
				throw new IllegalStateException("文件\"" + targetFile.getAbsolutePath() + "\"删除失败");
			}
			count ++;
			Thread.sleep(500);
		}
		count = 0;
		while (! uploadFile.renameTo(targetFile)) {
			if (count >= 10) {
				throw new IllegalStateException("文件\"" + uploadFile.getAbsolutePath() + "\"重命名到\"" + targetFile.getAbsolutePath() + "\"失败");
			}
			count ++;
			Thread.sleep(500);
		}
		return targetFile;
	}

	/**
	 * 将上传文件的公共方法 注意，由于显示上传百分比的原因，文件有可能被分为多块上传，如果返回值为null说明文件没有上传完成
	 * 
	 * @param dstPath
	 *            上传的目标路径
	 * @param timeflag
	 *            时间戳标识，是否在文件后面追加时间戳
	 * @return File file分块全部 上传完成 null分块传输未结束
	 */
	public File uploadFile(Uploader uploader, String dstPath, boolean timeflag) throws Exception {
		String realName = uploader.getUpload().getOriginalFilename();
		String dstAllPath = this.getRealPath() + File.separatorChar + dstPath + File.separatorChar
				+ FormatUtils.format(System.currentTimeMillis(), "yyyyMMdd") + File.separatorChar;
		if(FilepathValidateUtils.validateFilepath(dstAllPath)) {
			File dstFilePath = new File(dstAllPath);
			if (dstFilePath.exists() == false) {
				dstFilePath.mkdirs();
			}
			File dstFile = new File(dstAllPath + uploader.getName());
			// 文件已存在（上传了同名的文件）
			if (uploader.getChunk() == 0 && dstFile.exists()) {
				dstFile.delete();
				dstFile = new File(dstAllPath + uploader.getName());
			}
			boolean lastChunks = uploader.getChunk() == uploader.getChunks() - 1;
			copyFile(uploader.getUpload().getInputStream(), dstFile, lastChunks);
			logger.info("上传文件:" + realName + "文件类型:" + uploader.getUpload().getContentType() + " "
					+ (uploader.getChunk() + 1) + " " + uploader.getChunk());
			if (lastChunks) {
				File file = null;
				if (timeflag)
					file = new File(dstAllPath + realName + "."
							+ FormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
				else
					file = new File(dstAllPath + realName);
				return saveUploadFile(dstFile, file);
			}
		}
		return null;
	}

	/**
	 * 将分块上传的文件整合到一个文件中
	 * 
	 */
	protected void copyFile(InputStream src, File dst, boolean sync) {
		FileOutputStream out = null;
		try {
			if (dst.exists()) {
				out = new FileOutputStream(dst, true);
			} else {
				out = new FileOutputStream(dst);
			}
			IOUtils.copy(src, out);
			out.flush();
			if (sync) {
				out.getFD().sync();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 绕过Template,直接输出内容的简便函数.
	 */
	protected String render(String text, String contentType, HttpServletResponse response) {
		try {
			response.setContentType(contentType);
			response.getWriter().write(text);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * 直接输出字符串.
	 */
	protected String renderText(String text, HttpServletResponse response) {
		return render(text, "text/plain;charset=UTF-8", response);
	}

	/**
	 * 直接输出HTML.
	 */
	protected String renderHtml(String html, HttpServletResponse response) {
		return render(html, "text/html;charset=UTF-8", response);
	}

	/**
	 * 直接输出XML.
	 */
	protected String renderXML(String xml, HttpServletResponse response) {
		return render(xml, "text/xml;charset=UTF-8", response);
	}

	/**
	 * 关闭窗口
	 */
	protected void closeWindow(HttpServletResponse response) {
		StringBuilder html = new StringBuilder();
		html.append("<script type=\"text/javascript\">");
		html.append("window.opener=null;");
		html.append("window.open('', '_self', '');");
		html.append("window.close();");
		html.append("</script>");
		this.renderHtml(html.toString(), response);
	}

	/**
	 * 获取HttpServletRequest对象
	 * 
	 * @return
	 */
	protected HttpServletRequest getRequest() {

		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}

	/**
	 * 加载图片选择combox数据,并缓存结果
	 * 
	 * @param directory
	 *            图标存放的目录
	 */
	public List<Map<String, String>> buildIconCombox(String projectPath, String directory) {
		if(FilepathValidateUtils.validateFilepath(projectPath)) {
			String path = "";
			if (projectPath != null && !"".equals(projectPath)) {
				path = "images" + "/" + projectPath + "/" + directory + "/";
			} else {
				path = "images" + "/" + GlobalConstants4frame.THEME + "/" + directory + "/";
			}
			List<Map<String, String>> list = new ArrayList<Map<String, String>>();// 返回普通结果集合
			InputStream inStream = null;
			List<String> styles = null;
			try {
				inStream = new FileInputStream(new File(this.getRealPath() + File.separatorChar +path+"style.css"));
				styles =CssParserUtil.showCssText(inStream);//读取CSS文件获得所有图标样式
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(inStream);
			}
			List<String> icons = new ArrayList<String>();
			if(styles != null && styles.size()>0){
				for(String style : styles){
					style = StringUtils.split(style,":")[0];
					style = StringUtils.substring(style, 2, style.length());
					if(style.indexOf("icon-")==0){//获取所有icon-打头的样式
						icons.add(style);
					}
				}
			}
			else{
				icons = iconList;
			}
			for(String icon : icons){
				Map<String, String> map = new HashMap<String, String>();
				map.put("text", icon);
				map.put("id", icon);
				list.add(map);
			}
			return list;
		}
		return null;
	}

	/**
	 * 加载图片选择combox数据,并缓存结果
	 * 
	 * @param directory
	 *            图标存放的目录
	 */
	public List<Map<String, String>> buildIconCombox(String directory) {

		return buildIconCombox(null, directory);

	}

	/**
	 * 生成图标选择页面,并缓存结果
	 * 
	 * @param directory
	 *            图标存放的目录
	 */
	public String buildIconSelectHTML(String projectPath, String directory) {
		StringBuilder html = new StringBuilder();
		html.append("<div id=\"winicons\">");
		html.append("<ul class=\"iconlist\">");
		if(FilepathValidateUtils.validateFilepath(projectPath)) {
			String path = "";
			if (projectPath != null && !"".equals(projectPath)) {
				path = "images" + "/" + projectPath + "/" + directory + "/";
			} else {
				path = "images" + "/" + GlobalConstants4frame.THEME + "/" + directory + "/";
			}
			InputStream inStream = null;
			List<String> styles = null;
			try {
				inStream = new FileInputStream(new File(this.getRealPath() + File.separatorChar +path+"style.css"));
				styles =CssParserUtil.showCssText(inStream);//读取CSS文件获得所有图标样式
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(inStream);
			}
			List<String> icons = new ArrayList<String>();
			if(styles != null && styles.size()>0){
				for(String style : styles){
					style = StringUtils.split(style,":")[0];
					style = StringUtils.substring(style, 2, style.length());
					if(style.indexOf("icon-")==0){//获取所有icon-打头的样式
						icons.add(style);
					}
				}
			}
			else{
				icons = iconList;
			}
			for(String icon : icons){
				html.append("<li><a class=\""+ icon +"\" style=\"font-size: 18px\"></a></li>");
			}
			html.append("</ul>");
			html.append("</div>");
			return html.toString();
		}
		return null;
	}

	/**
	 * 生成图标选择页面,并缓存结果
	 * 
	 * @param directory
	 *            图标存放的目录
	 */
	public String buildIconSelectHTML(String directory) {
		return buildIconSelectHTML("", directory);
	}

	/**
	 * 工程当前真实的物理地址，weblogic war发布模式时不能获取路径
	 */
	public String getRealPath() {
		String path = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()
				.getSession().getServletContext().getRealPath("/");
		
		// lcy 20190806 weblogic 发布获取路径方法
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-frame/index/index.properties");
		try {
			String isWeblogicPublishWar = propertiesUtils.getProperty("isWeblogicPublishWar");
			if(StringUtils.isNotBlank(isWeblogicPublishWar) && StringUtils.equals("true", isWeblogicPublishWar)) {
				//weblogic 发布获取路径方法
				path = this.getClass().getResource("/").getPath();
			}
		}catch(NoSuchElementException e) {
			logger.error("资源文件bione-frame/index/about.properties中未配置isWeblogicPublishWar属性");
		}
		
		return path;
	}

	/**
	 * 获得ContextPath
	 */
	public String getContextPath() {
		return GlobalConstants4frame.APP_CONTEXT_PATH;
	}
	
	/**
	 * 将上传文件的公共方法 注意，由于显示上传百分比的原因，文件有可能被分为多块上传，如果返回值为null说明文件没有上传完成
	 * 目标路径不再加当前日期
	 * @param dstPath
	 *            上传的目标路径
	 * @param timeflag
	 *            时间戳标识，是否在文件后面追加时间戳
	 * @return File file分块全部 上传完成 null分块传输未结束
	 */
	public File uploadFileWithoutDate(Uploader uploader, String dstPath, boolean timeflag) throws Exception {
		String realName = uploader.getUpload().getOriginalFilename();
		String dstAllPath = dstPath + File.separatorChar;
		if(FilepathValidateUtils.validateFilepath(dstAllPath) && FilepathValidateUtils.validateFilepath(uploader.getName())
				&& FilepathValidateUtils.validateFilepath(realName)) {
			File dstFilePath = new File(dstAllPath);
			if (dstFilePath.exists() == false) {
				dstFilePath.mkdirs();
			}
			if(FilepathValidateUtils.validateFilepath(dstAllPath + uploader.getName())) {
				File dstFile = new File(dstAllPath + uploader.getName());
				// 文件已存在（上传了同名的文件）
				if (uploader.getChunk() == 0 && dstFile.exists()) {
					dstFile.delete();
					dstFile = new File(dstAllPath + uploader.getName());
				}
				boolean lastChunks = uploader.getChunk() == uploader.getChunks() - 1;
				copyFile(uploader.getUpload().getInputStream(), dstFile, lastChunks);
				logger.info("上传文件:" + realName + "文件类型:" + uploader.getUpload().getContentType() + " "
						+ (uploader.getChunk() + 1) + " " + uploader.getChunk());
				if (lastChunks) {
					File file = null;
					if (timeflag)
						file = new File(dstAllPath + realName + "."
								+ FormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
					else
						file = new File(dstAllPath + realName);
					return saveUploadFile(dstFile, file);
				}
			}
		}
		return null;
	}
	
	/**
	 * 将上传文件的公共方法 注意，由于显示上传百分比的原因，文件有可能被分为多块上传，如果返回值为null说明文件没有上传完成
	 * 
	 * @param dstPath
	 *            上传的目标路径
	 * @param timeflag
	 *            时间戳标识，是否在文件后面追加时间戳
	 * @return File file分块全部 上传完成 null分块传输未结束
	 */
	public File uploadFile2(Uploader uploader, String dstPath, boolean timeflag) throws Exception {
		String realName = uploader.getUpload().getOriginalFilename();
		String dstAllPath = this.getRealPath() + File.separatorChar + dstPath + File.separatorChar;
		File dstFile = null;
		if(FilepathValidateUtils.validateFilepath(dstAllPath)) {
			File dstFilePath = new File(dstAllPath);
			if (dstFilePath.exists() == false) {
				dstFilePath.mkdirs();
			}
			if (uploader.getChunks() == 0) {
				// chunks等于0时，不知如何处理旧的同名文件，因此直接把内容写入最终文件 :-(
				if (timeflag) {
					dstFile = new File(dstAllPath + realName + "."
							+ FormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
				} else {
					dstFile = new File(dstAllPath + realName);
				}
			} else {
				if(FilepathValidateUtils.validateFilepath(dstAllPath + uploader.getName())) {
					dstFile = new File(dstAllPath + uploader.getName());
					// 文件已存在（上传了同名的文件），并且chunks大于0
					if (uploader.getChunk() == 0 && dstFile.exists()) {
						dstFile.delete();
						dstFile = new File(dstAllPath + uploader.getName());
					}
				}
			}
			boolean lastChunks = uploader.getChunk() == uploader.getChunks() - 1;
			copyFile(uploader.getUpload().getInputStream(), dstFile, lastChunks);
			logger.info("上传文件:" + realName + "文件类型:" + uploader.getUpload().getContentType() + " "
					+ (uploader.getChunk() + 1) + " " + uploader.getChunk());
			if (lastChunks) {
				// 支持chunks大于0的情况
				File file = null;
				if (timeflag)
					file = new File(dstAllPath + realName + "."
							+ FormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
				else
					file = new File(dstAllPath + realName);
				return saveUploadFile(dstFile, file);
			} else if (uploader.getTotalSize() > 0) {
				return dstFile.length() == uploader.getTotalSize() ? dstFile : null;
			}
			return dstFile;
		}
		return null;
	}
}
