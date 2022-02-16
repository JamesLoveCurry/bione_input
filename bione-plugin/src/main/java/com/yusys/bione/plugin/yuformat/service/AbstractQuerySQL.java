package com.yusys.bione.plugin.yuformat.service;

import java.util.HashMap;

/**
 * 查询条件的抽象类
 * @author xch
 *
 */
public abstract class AbstractQuerySQL {

	public abstract String getSQLCondition(String _loginUserCode, String _loginUserOrgNo, HashMap<String, Object> _otherConfigMap) throws Exception; //

}
