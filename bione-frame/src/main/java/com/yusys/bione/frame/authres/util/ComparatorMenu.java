package com.yusys.bione.frame.authres.util;

import java.math.BigDecimal;
import java.util.Comparator;

import com.yusys.bione.frame.authres.entity.BioneMenuInfo;

/**
 * <pre>
 * Title: 菜单排序工具类
 * Description: 
 * </pre>
 * @author yunlei yunlei@yuchengtech.com
 * @version 1.00.00
 * <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class ComparatorMenu implements Comparator<BioneMenuInfo>{

	public int compare(BioneMenuInfo menuInfo1, BioneMenuInfo menuInfo2) {
		if(menuInfo1.getOrderNo()==null){
			menuInfo1.setOrderNo(new BigDecimal(0));
		}
		if(menuInfo2.getOrderNo()==null){
			menuInfo2.setOrderNo(new BigDecimal(0));
		}
		return  menuInfo1.getOrderNo().intValue()-menuInfo2.getOrderNo().intValue();
	}

}
