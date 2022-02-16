package com.yusys.biapp.input.browser.web;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yusys.bione.comp.utils.DownloadUtils2;
import com.yusys.bione.frame.base.web.BaseController;

@Controller
@RequestMapping("/rpt/browser/attach")
public class BrowserController extends BaseController {
	
	
	public static final String ATTACH_FILE="import/report/browser/browser8.exe";
	
	/**
	 * 下载附件 
	 * @param response
	 * @param logicSysNo
	 * @throws Exception
	 */
	@RequestMapping("/startDownload")
	public void exportsys(HttpServletResponse response) throws Exception {
		
		String path = this.getRealPath() + "/" + ATTACH_FILE ;
		File src = new File(path);	
		DownloadUtils2.download(response, src);
	}
}
