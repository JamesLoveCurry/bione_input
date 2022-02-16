package com.yusys.bione.frame.base.web;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yusys.bione.comp.entity.upload.Uploader;

/**
 * <pre>
 * Title:上传文件
 * Description:上传文件
 * </pre>
 * 
 * @author songxf songxf@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/common/upload")
public class UploadController extends BaseController {

//	public DefaultHttpHeaders index() {
//		return new DefaultHttpHeaders("index").disableCaching();
//	}

	@RequestMapping("/startUpload")
	public String startUpload(Uploader uploader, HttpServletResponse response) throws IOException {
		File file = null;
		try {
			file = this.uploadFile(uploader, "dst", false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		if (file != null) {
			logger.info("文件[" + file.getName() + "]上传完成");
			//unzipFile(file);
		}
		response.setContentType("text/plain; charset=UTF-8");
		response.getWriter().print("true");
		return "true";
	}

}
