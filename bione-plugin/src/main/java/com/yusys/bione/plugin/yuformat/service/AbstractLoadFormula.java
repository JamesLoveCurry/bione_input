package com.yusys.bione.plugin.yuformat.service;

import com.yusys.bione.plugin.yuformat.utils.HashVO;

/**
 * 加载公式“自定义类”时处理的抽象类! 只要实现一个抽象方法
 * 
 * @author yangyf
 *
 */
public abstract class AbstractLoadFormula {

	// 自己去处理加载数据!
	public abstract void dealLoadData(HashVO[] _hvs, String _itemkey) throws Exception;

}
