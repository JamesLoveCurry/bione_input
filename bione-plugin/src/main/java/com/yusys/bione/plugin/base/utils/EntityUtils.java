/**  
 * @Title: ClassUtils.java  
 * @Package com.yusys.bione.plugin.base.common  
 * @Description: TODO(用一句话描述该文件做什么)  
 * @author guochi  
 * @date 2017年9月20日  
 * @version V1.0  
 */  

package com.yusys.bione.plugin.base.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yusys.bione.comp.repository.jdbc.JDBCBaseDAO;
import com.yusys.bione.comp.repository.jpa.JPABaseDAO;
import com.yusys.bione.comp.utils.JpaEntityUtils;
import com.yusys.bione.comp.utils.RandomUtils;

/**
 * @ClassName: ClassUtils
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author guochi
 * @date 2017年9月20日
 *
 */
@Component
public class EntityUtils  {
	// 直接使用JPAbaseDAO进行数据操作，如果有复杂的数据操作，需要定义一个独立的DAO类
	
	@Autowired
	protected JPABaseDAO<?, ?>  baseDAO;
	@Autowired
	protected JDBCBaseDAO jdbcBaseDAO;
	
	/**
	 * 批量维护实体（拷贝功能用）
	 * 
	 * @param saveObjs
	 * @param params
	 *            key : 属性名 , value : 值
	 * @param replaces
	 *            key : 旧value value : 新value
	 * @param randomFields
	 */
	public  <T>  void copyEntityJdbcBatch(List<T> saveObjs,
			Map<String, Object> params, Map<String, Object> replaces,
			List<String> randomFields) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.get(0).getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder uptSql = new StringBuilder("insert into ").append(
					tableNm).append("(");
			Iterator<String> it = fieldNms.keySet().iterator();
			StringBuilder valuesTmp = new StringBuilder("");
			boolean isFirst = true;
			while (it.hasNext()) {
				String fieldNm = it.next();
				if (!isFirst) {
					uptSql.append(" , ");
					valuesTmp.append(" , ");
				}
				uptSql.append(fieldNms.get(fieldNm));
				valuesTmp.append(" ? ");
				isFirst = false;
			}
			uptSql.append(") values (").append(valuesTmp).append(")");
			List<Object[]> updateParams = new ArrayList<Object[]>();
			for (Object saveObjTmp : saveObjs) {
				int index = 0;
				Object[] objs = new Object[fieldNms.keySet().size()];
				for (String fieldNmTmp : fieldNms.keySet()) {
					if ((null != params) && params.containsKey(fieldNmTmp)) {
						objs[index] = params.get(fieldNmTmp);
					} else if (randomFields != null
							&& randomFields.contains(fieldNmTmp)) {
						objs[index] = RandomUtils.uuid2();
					} else {
						Object valueTmp = getValueByFieldExpr(fieldNmTmp,
								saveObjTmp);
						if (replaces != null && replaces.containsKey(valueTmp)) {
							valueTmp = replaces.get(valueTmp);
						}
						objs[index] = valueTmp;
					}
					index++;
				}
				updateParams.add(objs);
			}
			jdbcBaseDAO.batchUpdate(uptSql.toString(), updateParams, 1000);
		}
	}
	/**
	 * @param <T>
	 * @Title: saveEntityJdbcBatch
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param saveObjs void  
	 * @throws
	 */
	public  <T> void saveEntityJdbcBatch(List<T> saveObjs) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.get(0).getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder uptSql = new StringBuilder("insert into ").append(
					tableNm).append("(");
			Iterator<String> it = fieldNms.keySet().iterator();
			StringBuilder valuesTmp = new StringBuilder("");
			boolean isFirst = true;
			while (it.hasNext()) {
				String fieldNm = it.next();
				if (!isFirst) {
					uptSql.append(" , ");
					valuesTmp.append(" , ");
				}
				uptSql.append(fieldNms.get(fieldNm));
				valuesTmp.append(" ? ");
				isFirst = false;
			}
			uptSql.append(") values (").append(valuesTmp).append(")");
			List<Object[]> params = new ArrayList<Object[]>();
			for (Object saveObjTmp : saveObjs) {
				int index = 0;
				Object[] objs = new Object[fieldNms.keySet().size()];
				for (String fieldNmTmp : fieldNms.keySet()) {
					objs[index] = getValueByFieldExpr(fieldNmTmp,
							saveObjTmp);
					index++;
				}
				params.add(objs);
			}
			jdbcBaseDAO.batchUpdate(uptSql.toString(), params, 1000);
		}
	}
	
	/**
	 * 
	 * @Title: getValueByFieldExpr
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param fieldExpr
	 * @param obj
	 * @return Object  
	 * @throws
	 */
	public  Object getValueByFieldExpr(String fieldExpr, Object obj) {
		Object val = null;
		if (!StringUtils.isEmpty(fieldExpr)) {
			String[] fieldDetails = StringUtils.split(fieldExpr, '.');
			val = obj;
			for (int i = 0; i < fieldDetails.length; i++) {
				if (fieldDetails[i] == null
						|| "".equals(fieldDetails[i].trim())) {
					continue;
				}
				try {
					val = getField(val, fieldDetails[i].trim());
				} catch (Exception e) {
					e.printStackTrace();
					val = null;
				}
			}
		}
		return val;
	}
	/**
	 * @Title: getField
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param obj
	 * @param field
	 * @return
	 * @throws Exception Object  
	 * @throws
	 */
	private   Object getField(Object obj, String field) throws Exception {
		String firstLetter = field.substring(0, 1).toUpperCase();
		String getMethodName = "get" + firstLetter + field.substring(1);
		Method method = obj.getClass().getMethod(getMethodName);
		return method.invoke(obj);
	}
	
	
	@SuppressWarnings("unchecked")
	public  List<Object> getEntitiesJpa(Class<?> targetClass,
			Map<String, Object> params) {

		List<Object> returnList = new ArrayList<Object>();
		if (targetClass != null) {
			StringBuilder sb = new StringBuilder("select obj from ");
			sb.append(targetClass.getSimpleName()).append(" obj where 1 = 1 ");
			Iterator<String> it = params.keySet().iterator();
			Integer count = 1;
			Map<String, Object> queryParams = new HashMap<String, Object>();
			while (it.hasNext()) {
				String key = it.next();
				Object val = params.get(key);
				if (val != null) {
					String oper = " = ";
					if ("ArrayList".equals(val.getClass().getSimpleName())) {
						oper = " in ";
						List<List<?>> lists = ReBuildParam.splitLists((List<Object>)val);
						if(lists != null && lists.size() > 0){
							sb.append(" and (");
							int i = 0;
							for(List<?> list : lists){
								sb.append(" ").append(key).append(oper).append(" (:p").append(count).append("l").append(i).append(")");
								queryParams.put(("p" + count +"l"+i), list);
								if( i == lists.size() - 1){
									sb.append(" ) ");
								}
								else{
									sb.append(" or ");
								}
								i++;
							}
						}
					}
					else{
						sb.append(" and ").append(key).append(oper).append(":p")
						.append(count);
						queryParams.put(("p" + count), val);
					}
					
				
					count++;
				}
			}
			returnList = baseDAO.findWithNameParm(sb.toString(),
					queryParams);
		}
		return returnList;
	}
	
	public  String list2String(List<String> list) {
		StringBuilder returnStr = new StringBuilder();
		if (list != null) {
			for (String strTmp : list) {
				if (StringUtils.isEmpty(strTmp)) {
					continue;
				}
				if (!"".equals(returnStr.toString())) {
					returnStr.append(",");
				}
				returnStr.append(strTmp);
			}
		}
		return returnStr.toString();
	}
}
