package com.yusys.bione.plugin.yuformat.service;

import com.yusys.bione.plugin.yuformat.utils.HashVO;

/**
 * 加载公式之Demo
 * @author xch
 *
 */
public class DemoLoadFormula extends AbstractLoadFormula {

	@Override
	public void dealLoadData(HashVO[] _hvs, String _itemkey) throws Exception {
		for (int i = 0; i < _hvs.length; i++) {
			String str_role = _hvs[i].getStringValue("roles"); //
			_hvs[i].setAttributeValue(_itemkey, str_role + "_名称");
		}
	}

}
