package com.yusys.bione.frame.message.web;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.yusys.bione.frame.base.web.BaseController;


/**
 * 公告-富文本编辑框
 * @author maojin1
 *
 */
@Controller
@RequestMapping("/bione/message/ckmedia")
public class CKeditorUploadController extends BaseController {
	/*
	 * 图片命名格式
	 */
	private static final String DEFAULT_SUB_FOLDER_FORMAT_AUTO = "yyyyMMddHHmmss";
	/*
	 * 上传图片文件夹
	 */
	private static final String UPLOAD_PATH="/import/bione/ckmedia/images/";

	/**
	 * 图片上传
	 * @param file
	 * @param request
	 * @param response
	 * @param CKEditorFuncNum
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@RequestMapping("imgUpload")  
	public void uplodaImg(@RequestParam("upload")MultipartFile file,
	        HttpServletRequest request, HttpServletResponse response, 
	        @RequestParam("CKEditorFuncNum")String CKEditorFuncNum) throws IllegalStateException, IOException{  
		//项目路径
		String dstAllPath = this.getRealPath();
	    PrintWriter out =response.getWriter();
	    //文件名
	    String fileName=file.getOriginalFilename();  
	    //图片类型
	    String uploadContentType =file.getContentType();  
	    //图片扩展名
	    String expandedName ="";  
	    if (uploadContentType.equals("image/pjpeg")    
	               || uploadContentType.equals("image/jpeg")) {    
	           // IE6上传jpg图片的headimageContentType是image/pjpeg，而IE9以及火狐上传的jpg图片是image/jpeg    
	           expandedName = ".jpg";    
	       } else if (uploadContentType.equals("image/png")    
	               || uploadContentType.equals("image/x-png")) {    
	           // IE6上传的png图片的headimageContentType是"image/x-png"    
	           expandedName = ".png";    
	       } else if (uploadContentType.equals("image/gif")) {    
	           expandedName = ".gif";    
	       } else if (uploadContentType.equals("image/bmp")) {    
	           expandedName = ".bmp";    
	       } else {    
	           out.println("<script type=\"text/javascript\">");    
	           out.println("window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum    
	                   + ",''," + "'文件格式不正确（必须为.jpg/.gif/.bmp/.png文件）');");    
	           out.println("</script>");    
	           return ;    
	       }    
	    if (file.getSize()> 600 * 1024) {    
	           out.println("<script type=\"text/javascript\">");    
	           out.println("window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum    
	                   + ",''," + "'文件大小不得大于600k');");    
	           out.println("</script>");    
	           return ;    
	    }  
	    DateFormat df = new SimpleDateFormat(DEFAULT_SUB_FOLDER_FORMAT_AUTO);  
	    fileName = df.format(new Date())+expandedName;  
	    file.transferTo(new File(dstAllPath+UPLOAD_PATH +fileName));  
	       // 返回"图像"选项卡并显示图片  request.getContextPath()为web项目名     
	       out.println("<script type=\"text/javascript\">");    
	       out.println("window.parent.CKEDITOR.tools.callFunction(" + CKEditorFuncNum    
	               + ",'" +request.getContextPath() +"/import/bione/ckmedia/images/" + fileName + "','')");    
	       out.println("</script>");    
	       return ;    
	}  
}
