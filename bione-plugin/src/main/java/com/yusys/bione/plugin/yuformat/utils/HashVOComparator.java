package com.yusys.bione.plugin.yuformat.utils;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

/**
 * HashVO的比较器,可以运行多个列比较,运行是否倒序,支持是否是数字! new
 * String[][]{{"seq","N","Y"},{"name","N","Y"}}
 * //对HashVO[]中的seq字段升序排序,而且是数字...第二个列表示是否是倒序,第三个列表示是否是数字!!!
 * 
 * @author xch 例如：{ "type", "N", "一类;二类;三类;四类" }, { "score", "Y", "Y" } })
 * @author wot_zhouqinxue 2014-11-4 解决按数组指定顺序排列的问题
 */
public class HashVOComparator implements Comparator<HashVO> {

	private int li_type = 1; //
	private String[][] str_sortColumns = null; //

	private String str_itemkey;
	private String[] str_orders; //

	public HashVOComparator(String[][] _sortColumns) {
		li_type = 1; //
		this.str_sortColumns = _sortColumns;
	}

	//第二种排序方式,即对指定列的值根据特定数组排序!
	public HashVOComparator(String _itemKey, String[] _orders) {
		li_type = 2; //
		str_itemkey = _itemKey; //
		str_orders = _orders; //
	}

	public int compare(HashVO o1, HashVO o2) {
		if (li_type == 1) {
			return compare1(o1, o2); //
		} else if (li_type == 2) {
			return compare2(o1, o2); //
		} else {
			return 0; //
		}
	}

	//第一种比较,即有多个列..
	private int compare1(HashVO o1, HashVO o2) {
		String str_1;
		String str_2;

		HashVO hvo_1 = o1;
		HashVO hvo_2 = o2;
		// new String[][]{{"seq","N","Y"}}
		int[] li_compareResult = new int[str_sortColumns.length]; //
		for (int i = 0; i < str_sortColumns.length; i++) {
			if (str_sortColumns[i][2].equals("Y")) { // 如果是数字
				try {
					str_1 = hvo_1.getStringValue(str_sortColumns[i][0], "-99999").trim(); // 空值当空串处理,即会排在前面
					str_2 = hvo_2.getStringValue(str_sortColumns[i][0], "-99999").trim(); // 空值当空串处理,即会排在前面
					if (str_1.equals("")) {
						str_1 = "-99999"; //
					}
					if (str_2.equals("")) {
						str_2 = "-99999"; //
					}
					if (str_1.endsWith("%")) { // 考虑有百分号的情况，在报表统计中有需要对百分比进行排序【xch/2012-08-27】
						str_1 = str_1.substring(0, str_1.length() - 1); //
					}
					if (str_2.endsWith("%")) {
						str_2 = str_2.substring(0, str_2.length() - 1); //
					}

					Double ld_1 = Double.parseDouble(str_1); // 空值当-99999处理,即会排在前面
					Double ld_2 = Double.parseDouble(str_2); // 空值当-99999处理,即会排在前面
					if (ld_1 == ld_2) {
						li_compareResult[i] = 0;
					} else {
						if (ld_1 > ld_2) {
							li_compareResult[i] = 1;
						} else {
							li_compareResult[i] = -1;
						}
					}
				} catch (Exception ex) {
					System.err.println(ex.getMessage()); //
					li_compareResult[i] = 0; // 
				}
			} else { //如果不是数字...
				// 如果第三列有分号,即认为是个数组,然后就按数组指定的顺序排列,而不是英文字母顺序排列！！解决按数组指定顺序排列的问题
				str_1 = hvo_1.getStringValue(str_sortColumns[i][0], "-99999").trim();
				str_2 = hvo_2.getStringValue(str_sortColumns[i][0], "-99999").trim(); // 空值当空串处理,即会排在前面
				if (str_sortColumns[i][2].indexOf(";") >= 0) {
					String str_sortColumn_array[] = StringUtils.split(str_sortColumns[i][2], ';');
					int li_pos_1 = 0;
					int li_pos_2 = 0;
					boolean flag_1 = false;
					boolean flag_2 = false;
					//  zqx   2014_11_21  这里可以提高下运算性能
					for (int j = 0; j < str_sortColumn_array.length; j++) {
						if (!flag_1) {
							if (str_sortColumn_array[j].equals(str_1)) {
								li_pos_1 = j;
								flag_1 = true;
							}
						}
						if (!flag_2) {
							if (str_sortColumn_array[j].equals(str_2)) {
								li_pos_2 = j;
								flag_2 = true;
							}
						}
						if (flag_1 && flag_2) {//如果多找到了，则立即跳出循环  
							break;
						}
					}
					li_compareResult[i] = li_pos_1 - li_pos_2;

				} else {// // 非数字，也就是字母拼音排序，也就是我们常见的列表双击排序
					try {
						str_1 = new String(str_1.getBytes("GBK"), "ISO-8859-1"); // 转一下
						str_2 = new String(str_2.getBytes("GBK"), "ISO-8859-1"); // 转一下
						li_compareResult[i] = str_1.compareTo(str_2); //
					} catch (Exception ex) {
						System.err.println(ex.getMessage()); //
						li_compareResult[i] = 0; //
					}
				}
			}

			if (str_sortColumns[i][1].equalsIgnoreCase("Y")) { // 是否倒序
				li_compareResult[i] = 0 - li_compareResult[i]; //
			}

			if (li_compareResult[i] != 0) {
				return li_compareResult[i]; // 如果已经比较出来了,则直接返回!!!即没必要继续下一个比较了!!,否则进行下一个的比较!!
			}
		}

		return 0; // 返回0
	}

	//第二种比较..
	private int compare2(HashVO _obj1, HashVO _obj2) {
		String str_1 = _obj1.getStringValue(str_itemkey, ""); //
		String str_2 = _obj2.getStringValue(str_itemkey, ""); //
		int li_pos1 = findPos(str_1); //
		int li_pos2 = findPos(str_2); //
		return (li_pos1 - li_pos2);
	}

	private int findPos(String _value) {
		for (int i = 0; i < str_orders.length; i++) {
			if (str_orders[i].equals(_value)) {
				return i;
			}
		}
		return -1;
	}

}
