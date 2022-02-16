package com.yusys.bione.plugin.yuformat.service;

import java.util.HashMap;

import com.yusys.bione.plugin.yuformat.utils.HashVO;

/**
 * 处理数据,列表或卡片在查询数据后,直接在后台对数据进行再处理.
 * 比如设置颜色等,然后在前端进行显示!
 * @author xch
 *
 */
public abstract class AbstractAfterLoadClass {

	//设置某一条记录的某一个字段的警报颜色
	public void setItemWarnColor(HashVO _hvo, String _item, String _color) {
		_hvo.setAttributeValue(_item + "◆warncolor", _color);
	}

	//设置某一条记录的某个字段的警报消息!
	public void setItemWarnMsg(HashVO _hvo, String _item, String _msg) {
		_hvo.setAttributeValue(_item + "◆warnmsg", _msg);
	}

	//设置某一条记录的某个字段是否置灰!
	public void setItemWarnVisible(HashVO _hvo, String _item, String _visible) {
		_hvo.setAttributeValue(_item + "◆warnvisible", _visible);
	}

	//后续处理,即直接修改HashVO的值
	public abstract void afterLoadDeal(HashVO[] _hvs, HashMap<String, Object> _otherConfigMap) throws Exception;
}
