package com.yusys.bione.frame.logicsys.util;

import java.math.BigDecimal;
import java.util.Comparator;

import com.yusys.bione.frame.authres.entity.BioneFuncInfo;

/**
 * 
 * <pre>
 * Title: 功能节点排序工具类
 * Description: 
 * </pre>
 * @author yunlei yunlei@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class ComparatorFunc implements Comparator<BioneFuncInfo>{

	public int compare(BioneFuncInfo func1, BioneFuncInfo func2) {
		
		if(func1.getOrderNo()==null){
			func1.setOrderNo(new BigDecimal(0));
		}
		if(func2.getOrderNo()==null){
			func2.setOrderNo(new BigDecimal(0));
		}
		return  func1.getOrderNo().intValue()-func2.getOrderNo().intValue();
	}

}
