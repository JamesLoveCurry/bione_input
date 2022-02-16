package com.yusys.bione.comp.entity.page;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yusys.bione.comp.repository.jpa.SearchFilter;
import com.yusys.bione.comp.repository.jpa.SearchFilter.Operator;
import com.yusys.bione.comp.repository.mybatis.dialect.DBMS;
import com.yusys.bione.comp.repository.mybatis.dialect.Dialect;
import com.yusys.bione.comp.repository.mybatis.dialect.DialectClient;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.SqlValidateUtils;

/**
 * 
 * <pre>
 * Title: Grid查询 分页处理
 * Description: 
 * 			20200904      增加查询条件防止SQL注入 处理逻辑
 * </pre>
 * 
 * @author yusys
 * @version 1.00.00
 * @since	2018-02-27
 *
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
public class Pager implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected int totalCount = 0;// 总记录数 
	protected int page = 1;// 当前页数
	protected int pagesize = 10;// 每页显示行数
	protected String sortname;// 排序的属性名
	protected String sortorder;// 排序的方式 desc or asc
	protected String condition;// 保存前台传递来的过滤参数
	protected String filterSql;// jdbc或Mybatis使用的动态sql
	protected Map<String, SearchFilter> SearchFilters;// SpringDataJpa使用的动态查询条件
	public boolean isPaging = true;// 是否分页,默认是true,如果不想使用,需要手动至为fasle
	protected boolean isFilter = true;// 是否过滤,默认是true,如果不想使用,需要手动至为fasle
	protected boolean isOrderBy = true;// 是否排序,默认是true,如果不想使用,需要手动至为fasle
	protected boolean isReplaceOrderBy = true;// 是否排序,默认是true,如果不想使用,需要手动至为fasle
	protected int dataCount = 0; // 总记录数,默认是0
	protected boolean isSetIndexFild = false; // 是否在 count 的时候把count(*) 替换成 count({index}) {index}为索引字段; 默认false; 当为 true 的时候,需要设置indexField 字段
	protected String indexField;// 当isSetIndexFild 为 true 的时候,需要设置改字段.
	public int getTotalCount() {
		return totalCount;
	}
	public int getDataCount() {
		return dataCount;
	}
	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}
	public boolean isSetIndexFild() {
		return isSetIndexFild;
	}
	public void setSetIndexFild(boolean isSetIndexFild) {
		this.isSetIndexFild = isSetIndexFild;
	}
	public String getIndexField() {
		return indexField;
	}
	public void setIndexField(String indexField) {
		this.indexField = indexField;
	}
	public boolean isReplaceOrderBy() {
		return isReplaceOrderBy;
	}
	public void setReplaceOrderBy(boolean isReplaceOrderBy) {
		this.isReplaceOrderBy = isReplaceOrderBy;
	}
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public String getSortname() {
		return sortname;
	}

	public void setSortname(String sortname) {
		sortname = StringUtils.trimToNull(sortname);
		if (sortname != null) {
			for (int i = 0; i < sortname.length(); i ++) {
				char ch = sortname.charAt(i);
				if (! CharUtils.isAsciiAlphaLower(ch) && ! CharUtils.isAsciiAlphaUpper(ch) &&
					! CharUtils.isAsciiNumeric(ch) && ch != '#' && ch != '$' && ch != '.') {
					sortname = null;
					break;
				}
			}
		}
		this.sortname = sortname;
	}

	public String getSortorder() {
		return sortorder;
	}

	public void setSortorder(String sortorder) {
		sortorder = StringUtils.trimToNull(sortorder);
		if (sortorder != null && ! "asc".equalsIgnoreCase(sortorder) && ! "desc".equalsIgnoreCase(sortorder)) {
			sortorder = null;
		}
		this.sortorder = sortorder;
	}

	public boolean isPaging() {
		return isPaging;
	}

	public void setPaging(boolean isPaging) {
		this.isPaging = isPaging;
	}

	public boolean isFilter() {
		return isFilter;
	}

	public void setFilter(boolean isFilter) {
		this.isFilter = isFilter;
	}

	public boolean isOrderBy() {
		return isOrderBy;
	}

	public void setOrderBy(boolean isOrderBy) {
		this.isOrderBy = isOrderBy;
	}

	/**
	 * 当前页数据的开始索引
	 * 
	 * @return
	 */
	public int getPageFirstIndex() {

		return (page - 1) * pagesize;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	/**
	 * 生成搜索查询语句 生成按名称绑定的语句 JPABASEDAO使用
	 * 
	 * @return MAP get(jql)获得查询语句 get(params)获得参数Map
	 */
	public Map<String, Object> getSearchCondition() {
		Map<String, Object> conditionMap = new HashMap<String, Object>();
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		Collection<String> oplist = ArrayUtils.asCollection("=,>,<,>=,<=,<>,!=,like,in");
		Collection<String> typelist = ArrayUtils.asCollection("string,date,number,checkboxGroup");
		StringBuilder conditionnew = new StringBuilder();
		String op = "";
		String field = "";
		String value = "";
		String type = "";
		int i = 0;
		if (condition != null && !condition.equals("")) {
			JSONObject groupJson = JSON.parseObject(condition);
			JSONArray rulesJson = groupJson.getJSONArray("rules");
			for (Iterator<?> conditioniter = rulesJson.iterator(); conditioniter.hasNext();) {
				JSONObject rule = (JSONObject) conditioniter.next();
				op = (String) rule.get("op");
				field = (String) rule.get("field");
				//处理 SQL注入
				value = SqlValidateUtils.validateValue((String) rule.get("value"));
				type = (String) rule.get("type");
				if (field.equals("null") || field.equals("") || value.equals("null") || value.equals("")) {
					logger.debug("搜索条件生成失败!");
				} else if (!oplist.contains(op)) {
					logger.debug("搜索条件符号配置错误!");
				} else if (!typelist.contains(type)) {
					logger.debug("搜索条件类型配置错误!");
				} else {
					if (type.equals("date")) {
						SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
						try {
							params.put("param" + i, df.parse(value));
							fieldValues.put(field, df.parse(value));
						} catch (ParseException e) {
							logger.debug("搜索条件[" + value.toString() + "]的日期格式错误!");
						}
					} else if (type.equals("number")) {
						if (StringUtils.isNumeric(value)) {
							BigDecimal bd = new BigDecimal(value);
							params.put("param" + i, bd);
							fieldValues.put(field, bd);
						} else {
							logger.debug("搜索条件[" + value.toString() + "]的数字格式错误!");
						}
					} else {
						if (op.equals("like")) {
							params.put("param" + i, "%" + value + "%");
						} else {
							params.put("param" + i, value);
						}
						fieldValues.put(field, value);
					}

					if (params.get("param" + i) != null) {
						if (conditionnew.length() != 0) {
							conditionnew.append(" and ");
						}
						conditionnew.append(field);
						conditionnew.append(" " + op);
						if (op.equals("in")) {
							conditionnew.append(" (:param" + i + ")");
						} else {
							conditionnew.append(":param" + i);
						}
					}
					i++;
				}
			}
		}
		conditionMap.put("jql", conditionnew.toString());
		conditionMap.put("params", params);
		conditionMap.put("fieldValues", fieldValues);
		return conditionMap;
	}

	/**
	 * 生成搜索查询语句 生成按名称绑定的语句 Mybatis使用
	 * 
	 * @return String
	 */
	public String getFilterSql() {
		Collection<String> oplist = ArrayUtils.asCollection("=,>,<,>=,<=,<>,!=,like,in");
		Collection<String> typelist = ArrayUtils.asCollection("string,date,number,checkboxGroup");
		StringBuilder conditionsql = new StringBuilder();
		String op = "";
		String field = "";
		String value = "";
		String type = "";
		if (condition != null && !condition.equals("")) {
			JSONObject groupJson = JSON.parseObject(condition);
			JSONArray rulesJson = groupJson.getJSONArray("rules");
			for (Iterator<?> conditioniter = rulesJson.iterator(); conditioniter.hasNext();) {
				JSONObject rule = (JSONObject) conditioniter.next();
				op = (String) rule.get("op");
				field = (String) rule.get("field");
				//漏洞修复 ：MyBatis文件-代码注入 处理 SQL注入
				value = SqlValidateUtils.validateValue((String) rule.get("value"));
				type = (String) rule.get("type");
				if (field.equals("null") || field.equals("") || value.equals("null") || value.equals("")) {
					logger.debug("搜索条件生成失败!");
				} else if (!oplist.contains(op)) {
					logger.debug("搜索条件符号配置错误!");
				} else if (!typelist.contains(type)) {
					logger.debug("搜索条件类型配置错误!");
				} else {
					if (conditionsql.length() > 0) {
						conditionsql.append(" and ");
					}
					conditionsql.append(getColumnString(field));
					conditionsql.append(" " + op);
					if (op.equals("in")) {
						List<String> valueList = Lists.newArrayList();
						for (String val : StringUtils.split(value, ',')) {
							valueList.add("'" + val + "'");
						}
						conditionsql.append("(" + StringUtils.join(valueList, ',') + ")");
					} else if (op.equals("like")) {
						conditionsql.append(" '%" + value + "%'");
					} else {
						if (type.equals("number") && StringUtils.isNumeric(value)) {
							conditionsql.append(value);
						} else if (type.equals("date")) {
							// 只有DB2是这样写,如需要使用其他数据库，需要进行扩展
							PropertiesUtils pUtils = PropertiesUtils.get("database.properties");
							String dialectType = pUtils.getProperty("database.type");
							if (dialectType == null || dialectType.equals("")) {
								throw new RuntimeException("没有设置数据库方言参数.");
							} else {
								DBMS dbms = DBMS.valueOf(dialectType.toUpperCase());
								Dialect dialect = DialectClient.getDbmsDialect(dbms);
								conditionsql.append(dialect.getDateString(value));
							}
						} else {
							conditionsql.append("'" + value + "'");
						}
					}
				}
			}
		}
		return conditionsql.toString();
	}

	/**
	 * 生成spring data jpa所需的动态查询参数
	 * 
	 * @return MAP
	 */
	public Map<String, SearchFilter> getSearchFilters() {
		// springdata使用的过滤条件
		Map<String, SearchFilter> searchFilters = new HashMap<String, SearchFilter>();
		Collection<String> oplist = ArrayUtils.asCollection("=,>,<,>=,<=,<>,!=,like,in");
		Map<String, Operator> operators = new HashMap<String, Operator>();
		operators.put("=", Operator.EQ);
		operators.put("like", Operator.LIKE);
		operators.put(">", Operator.GT);
		operators.put(">=", Operator.GTE);
		operators.put("<", Operator.LT);
		operators.put("<=", Operator.LTE);
		operators.put("<>", Operator.NE);
		operators.put("!=", Operator.NE);
		Collection<String> typelist = ArrayUtils.asCollection("string,date,number,checkboxGroup");
		String op = "";
		String field = "";
		String value = "";
		String type = "";
		if (condition != null && !condition.equals("")) {
			JSONObject groupJson = JSON.parseObject(condition);
			JSONArray rulesJson = groupJson.getJSONArray("rules");
			for (Iterator<?> conditioniter = rulesJson.iterator(); conditioniter.hasNext();) {
				JSONObject rule = (JSONObject) conditioniter.next();
				op = (String) rule.get("op");
				field = (String) rule.get("field");
				//处理 SQL注入
				value = SqlValidateUtils.validateValue((String) rule.get("value"));
				type = (String) rule.get("type");
				if (field.equals("null") || field.equals("") || value.equals("null") || value.equals("")) {
					logger.debug("搜索条件生成失败!");
				} else if (!oplist.contains(op)) {
					logger.debug("搜索条件符号配置错误!");
				} else if (!typelist.contains(type)) {
					logger.debug("搜索条件类型配置错误!");
				} else {
					searchFilters.put(field, new SearchFilter(field, operators.get(op), value));
				}
			}
		}

		return searchFilters;
	}

	/**
	 * 生成sql使用的orderby字段名称
	 * 
	 * @return String
	 */
	public String getSqlSortName() {
		return getColumnString(getSortname());
	}

	public PageRequest getPageRequest() {
		Sort sort = null;
		if ("desc".equals(this.sortorder)) {
			sort = new Sort(Direction.DESC, this.sortname);
		} else if ("asc".equals(this.sortorder)) {
			sort = new Sort(Direction.ASC, this.sortname);
		}
		return new PageRequest(this.page - 1, this.pagesize, sort);
	}

	private String getColumnString(String field) {
		for (int i = 0; i < field.length(); i++) {
			char c = field.charAt(i);
			if (Character.isUpperCase(c)) {
				field = StringUtils.replaceOnce(field, Character.toString(c), "_" + Character.toString(c).toLowerCase());
			}
		}
		return field;
	}

	public void setFilterSql(String filterSql) {
		this.filterSql = filterSql;
	}

	public void setSearchFilters(Map<String, SearchFilter> searchFilters) {
		SearchFilters = searchFilters;
	}
	
}
