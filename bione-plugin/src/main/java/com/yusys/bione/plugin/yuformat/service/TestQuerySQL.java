package com.yusys.bione.plugin.yuformat.service;

import java.util.HashMap;

public class TestQuerySQL extends AbstractQuerySQL {

	@Override
	public String getSQLCondition(String _loginUserCode, String _loginUserOrgNo, HashMap<String, Object> _otherConfigMap) throws Exception {

		return "org_type='04'";
	}

}
