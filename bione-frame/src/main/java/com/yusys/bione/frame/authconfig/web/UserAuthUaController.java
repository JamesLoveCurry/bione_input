package com.yusys.bione.frame.authconfig.web;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.authconfig.entity.BioneAuthInfoUa;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;

@Controller
@RequestMapping("/bione/admin/userauth/authUA")
public class UserAuthUaController {

	@Autowired
	private AuthConfigBS authConfigBS;
	
	/**
	 * 跳转到ua配置界面
	 * @return
	 */
	@RequestMapping()
	public ModelAndView index(){
		return new ModelAndView("/frame/authConfig/authConfig-ua");
	}
	
	/**
	 * 测试连接
	 * @param ipAddress
	 * @param port
	 * @return
	 */
	@RequestMapping(value = "/test",method = RequestMethod.POST)
	@ResponseBody
	public Boolean test(String ipAddress,String port){
		return this.authConfigBS.test(ipAddress,port);
	}
	
	/**
	 * 获取ua认证的基本信息
	 * @return
	 */
	@RequestMapping(value = "/getUaInfo")
	@ResponseBody
	public BioneAuthInfoUa getUaInfo(){
		BioneAuthInfoUa model = null; 
		List<BioneAuthInfoUa> uaList = this.authConfigBS.getEntityList(BioneAuthInfoUa.class);
		if(uaList.size()>0){
			model = uaList.get(0);
		}else{
			model = new BioneAuthInfoUa();
			model.setAuthTypeNo(GlobalConstants4frame.AUTH_TYPE_UA);
		}
		return model;
	}
	
	/**
	 * 保存 ua基本信息
	 * @param model
	 */
	@RequestMapping("/saveUaInfo")
	public void saveUaInfo(BioneAuthInfoUa model) {
		if(StringUtils.isBlank(model.getAuthSrcId())){
			model.setAuthSrcId(RandomUtils.uuid2());
		}
		authConfigBS.updateEntity(model);
	}
}

