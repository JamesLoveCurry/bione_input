package com.yusys.bione.plugin.paramtmp;

import java.util.HashMap;
import java.util.Map;

import com.yusys.bione.frame.security.BioneSecurityUtils;


public enum ParamModule {	
	USER_ID("用户ID") {
		public String apply() {
			return BioneSecurityUtils.getCurrentUserInfo().getUserId();
		}
	},
	USER_NAME("用户名称") {
		public String apply() {
			return BioneSecurityUtils.getCurrentUserInfo().getUserName();
		}
	},
	LOGINNMAE("登录用户") {
		public String apply() {
			return BioneSecurityUtils.getCurrentUserInfo().getLoginName();
		}
	},
	ORG_NO("机构编号") {
		public String apply() {
			return BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		}
	},
	ORG_NAME("用户所属机构名称") {
		public String apply() {
			return BioneSecurityUtils.getCurrentUserInfo().getOrgName();
		}
	},
	DEPT_NO("部门编号") {
		public String apply() {
			return BioneSecurityUtils.getCurrentUserInfo().getDeptNo();
		}
	},
	DEPT_NAME("用户所属部门名称") {
		public String apply() {
			return BioneSecurityUtils.getCurrentUserInfo().getDeptName();
		}
	},
	CURRENTLOGICSYSNO("当前登录的逻辑系统标识") {
		public String apply() {
			return BioneSecurityUtils.getCurrentUserInfo()
					.getCurrentLogicSysNo();
		}
	};
	
	private final String name;
	
	private static final Map<String, ParamModule> stringToEnum = new HashMap<String, ParamModule>();
	
	/**
	 * 加载所有的枚举对象
	 */
	static {
		for(ParamModule module : values()){
			stringToEnum.put(module.toString(), module);
		}
	}

	ParamModule(String name){
		this.name = name;
	}
	
	/**
	 * 根据ParamModule的字符串表示返回对应的枚举对象，找不到则返回null
	 * @param symbol	字符串参数
	 * @return			SearchModule的实例	
	 */
	public static ParamModule formString(String symbol){
		return stringToEnum.get(symbol);
	}
	

	public String toChinese() {
		return this.name;
	}
	
	public abstract String apply();

}
