package com.yusys.bione.frame.message.web;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.message.entity.BioneMsgAttachInfo;
import com.yusys.bione.frame.message.service.MsgAttachmentBS;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <pre>
 * Title: 消息模块-附件控制器
 * Description: 消息模块-附件控制器
 * </pre>
 * 
 * @author fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/message/attach")
public class MsgAttachController extends BaseController {

	
	@Autowired
	private MsgAttachmentBS msgAttachBS ;
	
	/** 上传路径 */
	private final static String UPLOAD_ATTACH_DIR = GlobalConstants4frame.IMPORT_PATH + "/message/attach";
	

	/**
	 * 获取某一条公告的附件列表
	 * @param msgId 公告消息的ID
	 * @return
	 */
	@RequestMapping(value = "/{id}/listAttach.*")
	@ResponseBody
	public Map<String, Object> listFile(@PathVariable("id") String msgId) {
		List<BioneMsgAttachInfo> result = this.msgAttachBS.getAttachList(msgId);
		Map<String, Object> taskMap = Maps.newHashMap();
		taskMap.put("Rows", result);
		return taskMap;
	}

	/**
	 * 获取某一条消息的附件列表
	 * @param msgId 公告消息的ID
	 * @return
	 */
	@RequestMapping(value = "/{id}/logAttach.*")
	@ResponseBody
	public Map<String, Object> listLogFile(@PathVariable("id") String msgId) {
		List<BioneMsgAttachInfo> result = this.msgAttachBS.getLogAttachList(msgId);
		Map<String, Object> taskMap = Maps.newHashMap();
		taskMap.put("Rows", result);
		return taskMap;
	}
	/**
	 * 上传附件
	 * @param 
	 * @return
	 */
	@RequestMapping("/newAttach")
	public ModelAndView newAttach(String type) {
		ModelMap mm = new ModelMap();
		mm.put("type", type);//利用type区分功能
		return new ModelAndView("/frame/message/msg-announcement-newattach", mm);
	}
	
	

	/**
	 * 附件批量删除
	 * @param id
	 */
	@RequestMapping(value = "/delAttach/{id}", method = RequestMethod.POST)
	@ResponseBody
	public void batchDeleteAttach(@PathVariable("id") String id) {
		String[] ids = StringUtils.split(id, ',');
		this.msgAttachBS.deleteBatch(ids);
	}
	
	
	/**
	 * 上传附件
	 * 
	 * @param uploader
	 * @param msgId  消息ID
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("/startUpload")
	public BioneMsgAttachInfo startUpload(String type, Uploader uploader, HttpServletResponse response) throws Exception {
		File file = null;
		PropertiesUtils path = new PropertiesUtils("fileupload.properties");
		try {
			String filePath = null;
			if(type.equalsIgnoreCase("msg")){//系统公告上传附件
				filePath = path.getProperty("systemMsgPath") + File.separatorChar;
			}else if(type.equalsIgnoreCase("rptdesign")){//通用报表定制上传附件
				filePath = path.getProperty("rptDesignTmpPath") + File.separatorChar;
			}else{//默认路径
				filePath = path.getProperty("defaultPath");
			}
			/*file = this.uploadFile(uploader, UPLOAD_ATTACH_DIR + File.separatorChar
					+ FormatUtils.format(System.currentTimeMillis(), "yyyyMMdd") + File.separatorChar
					+ date + File.separatorChar, date, false);*/
			//上传至服务器上
			file = this.uploadFileWithoutDate(uploader, filePath, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		response.setContentType("application/json; charset=UTF-8");
		if (file !=  null) {
			logger.info("文件[" + file.getName() + "]上传完成");
			
			String fileName = file.getName();
			String filePath = file.getCanonicalPath();
			if (filePath != null && filePath.length() > 0) {
				filePath = filePath.replace('\\', '/');
			}
			long   fileSize = file.length(); // bytes
			String fileExtName = "";//文件后缀名
			
			if (fileName != null && fileName.lastIndexOf(".") > 0) {
				fileExtName = fileName.substring(fileName.lastIndexOf(".") + ".".length());
			}
			
			String relativePath = filePath;
			if (relativePath.indexOf(UPLOAD_ATTACH_DIR) > 0) {
				relativePath = relativePath.substring(relativePath.indexOf(UPLOAD_ATTACH_DIR));
			}
			
			BioneMsgAttachInfo attach = new BioneMsgAttachInfo(); 
			attach.setAttachId(RandomUtils.uuid2());
			attach.setAttachName(fileName);
			attach.setAttachPath(relativePath);
			attach.setAttachSize(new BigDecimal(fileSize));
			attach.setAttachType(fileExtName);
			attach.setAttachSts(GlobalConstants4frame.COMMON_STATUS_VALID);
			attach.setRemark(null);
			//msgAttachBS.saveOrUpdateEntity(attach);
			// do attach-instance
			// return attach;
			String message = JSON.toJSONString(attach);
			response.getOutputStream().write(message.getBytes("UTF-8"));
		}
		return null;
	}
	
	@RequestMapping("/checkAttchDownlowd")
	@ResponseBody
	public boolean checkAttchDownlowd(String attachId){
		BioneMsgAttachInfo attach = this.msgAttachBS.getEntityById(attachId);
		if(attach != null && attach.getAttachId() != null){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 下载附件 
	 * @param response
	 * @param logicSysNo
	 * @throws Exception
	 */
	@RequestMapping("/startDownload")
	public void exportsys(HttpServletResponse response, String attachId) throws Exception {
		String attachIds[]=StringUtils.split(attachId, ",");
		List<String> filePath=new ArrayList<String>();
		for(int i=0;i<attachIds.length;i++){
			BioneMsgAttachInfo attach = this.msgAttachBS.getEntityById(attachIds[i]);
			if (attach!=null && attach.getAttachId()!=null && !attach.getAttachName().equals("")) {
				//String path = this.getRealPath() + "/" + attach.getAttachPath() ;
				String path = attach.getAttachPath();
				filePath.add(path);
			}
		}
		if(filePath.size()>1){
			//String folder = com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_REAL_PATH + "/import/frame/message";
			String folder = filePath.get(0).substring(0, filePath.get(0).lastIndexOf("/")+1);
			Date date=new Date();
			SimpleDateFormat ds=new SimpleDateFormat("yyyyMMddhhmmssSSS");
			String info=ds.format(date);
			File zipFile = createZipFile(folder + File.separator + "附件-"+info+".zip", filePath);
			DownloadUtils.download(response, zipFile);
			zipFile.delete();
		}
		else{
			if (FilepathValidateUtils.validateFilepath(filePath.get(0))){
				File src = new File(filePath.get(0));	
				DownloadUtils.download(response, src);
			}
		}
		
	}
	
	/**
	 * 压缩文件
	 * 
	 * @param zipFilePath
	 *            压缩文件路径
	 * @param filePathList
	 *            被压缩文件路径LIST
	 * @return
	 * @throws IOException
	 */
	private File createZipFile(String zipFilePath, List<String> filePathList) throws IOException {
		if (FilepathValidateUtils.validateFilepath(zipFilePath)) {
			File zipFile = new File(zipFilePath);
			ZipOutputStream zout = null;
			try {
				zout = new ZipOutputStream(new FileOutputStream(zipFilePath));
				zout.setEncoding("GBK");
				for (String filePath : filePathList) {
					File inputFile = new File(filePath);
					FilesUtils.zip(zout, inputFile, inputFile.getName(), zipFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(zout);
			}
			return zipFile;
		}
		return null;
	}
	
	/**
	 * 将上传文件的公共方法
	 * 注意，由于显示上传百分比的原因，文件有可能被分为多块上传，如果返回值为null说明文件没有上传完成
	 * @param dstPath   
	 * 				上传的目标路径
	 * @param timeflag   
	 * 				时间戳标识，是否在文件后面追加时间戳
	 * @return File
	 *              file分块全部 上传完成    null分块传输未结束  
	 */
	public File uploadFile(Uploader uploader, String dstPath, String tmpName, boolean timeflag) throws Exception {
		String realName = uploader.getName();
		String dstAllPath = this.getRealPath() + File.separatorChar + dstPath + File.separatorChar;
		if (FilepathValidateUtils.validateFilepath(realName) && FilepathValidateUtils.validateFilepath(dstAllPath)
				&& FilepathValidateUtils.validateFilepath(tmpName)) {
			File dstFilePath = new File(dstAllPath);
			if (dstFilePath.exists() == false) {
				dstFilePath.mkdirs();
			}
			String tmpPath = dstAllPath + tmpName;
			if (FilepathValidateUtils.validateFilepath(tmpPath)) {
				File dstFile = new File(tmpPath);
				
				// 文件已存在（上传了同名的文件）
				if (uploader.getChunk() == 0 && dstFile.exists()) {
					dstFile.delete();
					dstFile = new File(tmpPath);
				}
				copyFile(uploader.getUpload().getInputStream(), dstFile);
				logger.info("上传文件:" + realName + "文件类型:" + uploader.getUpload().getContentType() + " "
						+ (uploader.getChunk() + 1) + " " + uploader.getChunk());
				if (uploader.getChunk() == uploader.getChunks() - 1) {
					File file = null;
					if (timeflag)
						file = new File(dstAllPath + realName + "."
								+ FormatUtils.format(System.currentTimeMillis(), "yyyyMMddHHmmssSSS"));
					else
						file = new File(dstAllPath + realName);
					if (file.exists())
						file.delete();
					dstFile.renameTo(file);
					if (dstFile.exists())
						dstFile.delete();
					return file;
				}
			}
		}
		return null;
	}

	
	/**
	 * 读取文件上传路径配置文件
	 */
	public String findShareCatalogPath(){
		PropertiesUtils path = PropertiesUtils.get("fileupload.properties");
		String dstAllPath = this.getRealPath();
		if(null != path.getProperty("annousPath")){
			dstAllPath = path.getProperty("annousPath");
		}
		return dstAllPath;
	}
	/**
	 * 获取附件全路径
	 */
	@RequestMapping(value = "/getRelPath.*")
	@ResponseBody
	private Map<String,String> getRelPath(String attachId) {
		Map<String,String> rMap = new HashMap<String, String>();
		BioneMsgAttachInfo att = this.msgAttachBS.getEntityById(BioneMsgAttachInfo.class, attachId);
		rMap.put("path", "");
		if(null != att){
			rMap.put("path", this.findShareCatalogPath()+att.getAttachPath());
		}
		return rMap;
	}
	
	/**
	 * 将分块上传的文件整合到一个文件中
	 * 
	 */
	private void copyFile(InputStream src, File dst) {
		int BUFFER_SIZE = 1024;
		InputStream in = null;
		OutputStream out = null;
		try {
			if (dst.exists()) {
				out = new BufferedOutputStream(new FileOutputStream(dst, true), BUFFER_SIZE);
			} else {
				out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE);
			}
			in = new BufferedInputStream(src, BUFFER_SIZE);

			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
