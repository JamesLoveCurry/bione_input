package com.yusys.bione.plugin.yuformat.utils;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * 构建Update的SQL的工具,使用该工具生成SQL至少有以下几点好处：
 * 1.会少写一半行数的代码,生成的SQL语句的格式也会标准统一
 * 2.不易发生列与值错位的错误
 * 3.杜绝造成中间少逗号或最后一位少个逗号的错误(这个经常发生)
 * 4.自动处理null的问题,无需再为此费劲处理了,而且支持如果为null的默认值处理,即nvl的概念!!
 * 5.杜绝字符串中有单引号从而发生异常的情况(这个也很烦人),即数据中有单引号会保存不少,需要反义处理一下,即在前面在加一个单引号!!!
 * 6.可扩展性强,以后万一发生跨数据库,日期类型等特殊情况时只需修改这里一处即可!!!
 * 
 * @author kf0612
 */
@SuppressWarnings("serial")
public class UpdateSQLBuilder implements Serializable,SQLBuilder {

	private String tableName = null; // 表名
	private String whereCondition = null; //where条件
	private Map<String, Object> valueMap = new LinkedHashMap<String, Object>(); // 存储所有字段值的集合类

	public UpdateSQLBuilder() {
	}

	public UpdateSQLBuilder(String _tableName) {
		this.tableName = _tableName; //
	}

	public UpdateSQLBuilder(String _tableName, String _whereCondition) {
		this.tableName = _tableName; //
		this.whereCondition = _whereCondition; //
	}

	/**
	 * 在构造方法中直接送入数据，这样就可以通过一行代码生成SQL,比如 new
	 * UpdateSQLBuilder("pub_user","id=100",new
	 * String[][]{{"code","www"},{"name","测试数据"}}).getSQL(); //
	 * 
	 * @param _tableName
	 * @param _whereCondition
	 * @param _data,初始数据,必须是n行,两列的样子!
	 */
	public UpdateSQLBuilder(String _tableName, String _whereCondition, String[][] _data) {
		this.tableName = _tableName; //
		this.whereCondition = _whereCondition; //
		if (_data != null) {
			for (int i = 0; i < _data.length; i++) {
				valueMap.put(_data[i][0], _data[i][1]); //
			}
		}
	}

	public UpdateSQLBuilder(String _tableName, String _whereCondition, HashVO _hvo) {
		this.tableName = _tableName; //
		if (_hvo != null) {
			String[] str_keys = _hvo.getKeys(); //
			for (int i = 0; i < str_keys.length; i++) {
				valueMap.put(str_keys[i], _hvo.getStringValue(str_keys[i])); //
			}
		}
	}

	public void putFieldValue(HashVO _hvo) {
		putFieldValue(_hvo, null); //
	}

	public void putFieldValue(HashVO _hvo, String[] _notKeys) {
		String[] str_keys = _hvo.getKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (_notKeys != null && isExist(str_keys[i], _notKeys)) {
				continue; //
			}
			putFieldValue(str_keys[i], _hvo.getStringValue(str_keys[i])); //
		}
	}

	//是否存在于数据中!
	private boolean isExist(String _key, String[] _array) {
		for (int i = 0; i < _array.length; i++) {
			if (_array[i].equalsIgnoreCase(_key)) {
				return true;
			}
		}
		return false; //
	}

	/**
	 * 塞入值
	 * 
	 * @param _fieldName
	 * @param _value
	 */
	public void putFieldValue(String _fieldName, String _value) {
		valueMap.put(_fieldName, _value); //
	}

	/**
	 * 塞入值,如果值为null,则用nvl代替
	 * 
	 * @param _fieldName
	 * @param _value
	 * @param _nvl
	 *            当_value为null时用该值代替!!!
	 */
	public void putFieldValue(String _fieldName, String _value, String _nvl) {
		if (_value == null && _nvl != null) {
			valueMap.put(_fieldName, _nvl); //
		} else {
			valueMap.put(_fieldName, _value); //
		}
	}

	public void putFieldValue(String _fieldName, int _value) {
		valueMap.put(_fieldName, new Integer(_value)); //
	}

	public void putFieldValue(String _fieldName, Integer _value) {
		valueMap.put(_fieldName, _value); //
	}

	public void putFieldValue(String _fieldName, double _value) {
		valueMap.put(_fieldName, new Double(_value)); //
	}

	public void putFieldValue(String _fieldName, Double _value) {
		valueMap.put(_fieldName, _value); //
	}

	public void putFieldValue(String _fieldName, long _value) {
		valueMap.put(_fieldName, new Long(_value)); //
	}

	public void putFieldValue(String _fieldName, Long _value) {
		valueMap.put(_fieldName, _value); //
	}

	public String getSQL() {
		return getSQL(true); //
	}

	/**
	 * 拼成SQL
	 * 
	 * @return
	 */
	public String getSQL(boolean _isconvert) {
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("update " + getTableName() + " set "); //
		String[] str_keys = valueMap.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_keys.length; i++) {
			sb_sql.append(str_keys[i] + "="); // 先把字段加上.
			Object obj = valueMap.get(str_keys[i]); //
			if (obj == null) {
				sb_sql.append("null"); //
			} else {
				if (obj instanceof String) {
					String str_value = (String) obj; //
					if (str_value.trim().equals("null") || str_value.trim().equals("")) {
						sb_sql.append("null"); //
					} else {
						if (_isconvert) {
							sb_sql.append("'" + convertSQLValue(str_value) + "'"); //
						} else {
							sb_sql.append("'" + str_value + "'"); //
						}
					}
				} else if (obj instanceof Integer) {
					Integer li_value = (Integer) obj; //
					sb_sql.append("'" + li_value + "'"); //
				} else if (obj instanceof Double) {
					Double ld_value = (Double) obj; //
					sb_sql.append("'" + ld_value + "'"); //
				} else if (obj instanceof Long) { // 长整形
					Long ll_value = (Long) obj; //
					sb_sql.append("'" + ll_value + "'"); //
				} else {
					sb_sql.append("'" + obj.toString() + "'"); //
				}
			}

			if (i != str_keys.length - 1) { // 如果不是最后一个..
				sb_sql.append(","); //
			}
		}

		if (getWhereCondition() != null && !"".equals(getWhereCondition())) {
			sb_sql.append(" where " + getWhereCondition()); //	
		}

		return sb_sql.toString(); //
	}

	/**
	 * 替换SQL中的单引号,因为单引号会导致保存失败!!
	 * 
	 * @param _value
	 * @return
	 */
	private String convertSQLValue(String _value) {
		if (_value == null) {
			return "null";
		} else {
			_value = StringUtils.replace(_value, "'", "''"); //单引号要替换!!!
			return _value; //
		}
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getWhereCondition() {
		return whereCondition;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	public int getSize() {
		return valueMap.size(); //
	}

	/**
	 * 重构ToString()方法,返回SQL
	 */
	@Override
	public String toString() {
		return getSQL(); //
	}

}
