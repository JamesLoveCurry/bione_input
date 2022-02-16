package com.yusys.bione.frame.authconfig.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.yusys.bione.frame.authconfig.entity.BioneAuthInfoLdap;
import com.yusys.bione.frame.authconfig.service.AuthConfigBS;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;

@Controller
@RequestMapping("/bione/admin/userauth/authLDAP")
public class UserAuthLdapController {

	@Autowired
	private AuthConfigBS authConfigBS;
	
	/**
	 * 跳转到LDAP配置界面
	 * @return
	 */
	@RequestMapping
	public ModelAndView index(){
		return new ModelAndView("/frame/authConfig/authConfig-ldap");
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
	 * 获取LDAP认证的基本信息
	 * @return
	 */
	@RequestMapping(value = "/getLdapInfo")
	@ResponseBody
	public BioneAuthInfoLdap getLdapInfo(){
		BioneAuthInfoLdap model = null; 
		List<BioneAuthInfoLdap> ldapList = this.authConfigBS.getEntityList(BioneAuthInfoLdap.class);
		if(ldapList.size()>0){
			model = ldapList.get(0);
		}else{
			model = new BioneAuthInfoLdap();
			model.setAuthTypeNo(GlobalConstants4frame.AUTH_TYPE_LDAP);
		}
		return model;
	}
	/**
	 * 保存ldap基本信息
	 * @param model
	 */
	@RequestMapping(value = "/saveLdap", method = RequestMethod.POST)
	public void saveLdap(BioneAuthInfoLdap model){
		authConfigBS.updateEntity(model);
	}
	
}

