/**
 * 
 */
package com.yusys.biapp.input.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yusys.bione.frame.base.web.BaseController;

/**
 * @author tobe
 * 会话超时时间为30分钟,每隔25分钟心跳一次.
 * 心跳包接收器
 */
@Controller
@RequestMapping(value="/datainput/heart")
public class HeartController  extends BaseController {
	private final Log log = LogFactory.getLog(HeartController.class);
	
	@RequestMapping("/on")
	@ResponseBody
	public void on() {
		log.info("心跳一次.会话超时时间为30分钟,每隔25分钟心跳一次.");
	}
}
