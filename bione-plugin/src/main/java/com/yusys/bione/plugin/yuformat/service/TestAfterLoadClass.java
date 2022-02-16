package com.yusys.bione.plugin.yuformat.service;

import java.util.HashMap;

import com.yusys.bione.plugin.yuformat.utils.HashVO;

/**
 * 后续处理类
 * @author xch
 *
 */
public class TestAfterLoadClass extends AbstractAfterLoadClass {

	@Override
	public void afterLoadDeal(HashVO[] _hvs, HashMap<String, Object> _otherConfigMap) throws Exception {
		HashVO hvo = (HashVO) _otherConfigMap.get("templetvo"); //crrs_single_corporation
		String str_templetcode = hvo.getStringValue("templetcode"); //
		String str_fromtable = hvo.getStringValue("fromtable"); //
		//System.err.println("*******************模板【" + str_templetcode + "】【" + str_fromtable + "】"); //

		for (int i = 0; i < _hvs.length; i++) {
			this.setItemWarnColor(_hvs[i], "organization_code", "yellow"); //设置背景色!
			this.setItemWarnColor(_hvs[i], "register_code", "yellow"); //设置背景色!

			this.setItemWarnMsg(_hvs[i], "organization_code", "问题1:宽度不能大于8<br>问题2:字符串必须是日期类型"); //设置背景色!
			this.setItemWarnMsg(_hvs[i], "register_code", "问题1:不能为空<br>问题2:宽度必须是20位"); //设置背景色!
		}
	}

}
