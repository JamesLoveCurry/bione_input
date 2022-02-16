package com.yusys.bione.frame.authconfig.web;

import java.io.IOException;
import java.net.Socket;
import java.util.List;




import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.frame.authconfig.entity.BioneAuthInfoCAS;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;

@Controller
@RequestMapping("/bione/admin/userauth/authCAS")
public class UserAuthCasController {

	@Autowired
	private AuthConfigBS authconfigbs;

	@RequestMapping
	public ModelAndView index() {
		return new ModelAndView("/frame/authConfig/authConfig-cas");
	}

	@RequestMapping(value = "/getCASInfo")
	@ResponseBody
	public BioneAuthInfoCAS getCASInfo() {
		BioneAuthInfoCAS model = null;
		List<BioneAuthInfoCAS> authInfoCAS = authconfigbs
				.getEntityList(BioneAuthInfoCAS.class);
		if (authInfoCAS.size() > 0) {
			model = authInfoCAS.get(0);
		} else {
			model = new BioneAuthInfoCAS();
			model.setAuthTypeNo(GlobalConstants4frame.AUTH_TYPE_LOCAL_CAS);
		}
		return model;
	}

	@RequestMapping(value = "/saveCASInfo", method = RequestMethod.POST)
	public void saveCASinfo(BioneAuthInfoCAS authInfoCas) {
		this.authconfigbs.updateEntity(authInfoCas);
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	@ResponseBody
	public boolean test(String ipAddress, String serverPort) {
		boolean flag = true;
		if (StringUtils.isEmpty(ipAddress) || StringUtils.isEmpty(serverPort)) {
			flag = false;
		}
		Socket socket = null;
		Integer port = Integer.valueOf(serverPort);
		try {
			socket = new Socket(ipAddress, port);
			// 设置5s的连接超时时间
			socket.setSoTimeout(5000);
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return flag;
	}
}
