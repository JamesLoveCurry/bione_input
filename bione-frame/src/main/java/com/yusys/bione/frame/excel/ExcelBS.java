package com.yusys.bione.frame.excel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.comp.utils.JpaEntityUtils;
import com.yusys.bione.comp.utils.ReflectionUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;
@Service
@Transactional(readOnly = true)
public class ExcelBS extends BaseBS<Object>{
	
	public Map<Object,Object> getValue(Class<?> cla,String value,String text,String condition) throws SecurityException, NoSuchFieldException{
		StringBuilder selectSql=new StringBuilder("");
		String vColumn=getColumnNm(value,cla);
		String tColumn=getColumnNm(text,cla);
		selectSql.append("select ").append(vColumn).append(",").append(tColumn).append(" from ");
		String tableNm = JpaEntityUtils.getTableName(cla);
		selectSql.append(tableNm);
		selectSql.append(" where 1=1");
		if(!condition.equals(""))
			selectSql.append(" and ").append(condition);
		if(!cla.getName().equals("com.yusys.bione.frame.user.entity.BioneUserInfo")){
			try{
				cla.getDeclaredField("logicSysNo");
				selectSql.append(" and logic_Sys_No = ").append("'"+BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo()+"' ");
			}
			catch(Exception e){
				
			}
		}
		
		Object[] objs = new Object[0];
		List<Map<String,Object>> list=this.jdbcBaseDAO.find(selectSql.toString(), objs);
		Map<Object,Object> maps=new HashMap<Object, Object>();
		if(list!=null&&list.size()>0){
			for(Map<String,Object> map:list){
				maps.put(map.get(tColumn), map.get(vColumn));
			}
		}
		return maps;
	}
	
	public <T> void uptEntityJdbcBatch(Collection<T> saveObjs,List<String> fields,List<String> uptfields) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder uptSql = new StringBuilder("update ").append(
					tableNm).append(" set ");
			if(uptfields!=null&&uptfields.size()>0){
				int i = 0;
				for(String uptfield: uptfields){
					uptSql.append(fieldNms.get(uptfield));
					uptSql.append(" = ");
					uptSql.append(" ? ");
					if(i < uptfields.size()-1){
						uptSql.append(" , ");
					}
					i++;
				}
			}
			uptSql.append(" where 1=1 ");
			List<Object[]> params = new ArrayList<Object[]>();
			if(fields==null||fields.size()<=0){
				fieldNms=JpaEntityUtils.getIdColumnsByEntity(saveClass);
				Iterator<String> it = fieldNms.keySet().iterator();
				while (it.hasNext()) {
					String fieldNm = it.next();
					uptSql.append(" and ");
					uptSql.append(fieldNms.get(fieldNm));
					uptSql.append(" = ");
					uptSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[uptfields.size()+fieldNms.keySet().size()];
					for (String uptfield : uptfields) {
						objs[index] = this.getValueByFieldExpr(uptfield,
								saveObjTmp);
						index++;
					}
					for (String fieldNmTmp : fieldNms.keySet()) {
						objs[index] = this.getValueByFieldExpr(fieldNmTmp,
								saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			else{
				for(String field: fields){
					uptSql.append(" and ");
					uptSql.append(fieldNms.get(field));
					uptSql.append(" = ");
					uptSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[uptfields.size()+fields.size()];
					for (String uptfield : uptfields) {
						objs[index] = this.getValueByFieldExpr(uptfield,
								saveObjTmp);
						index++;
					}
					for (String field : fields) {
						objs[index] = this.getValueByFieldExpr(field,saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			this.jdbcBaseDAO.batchUpdate(uptSql.toString(), params, 1000);
		}
	}
	
	public <T> void uptEntityJdbcBatch(Collection<T> saveObjs) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			
			StringBuilder uptSql = new StringBuilder("update ").append(
					tableNm).append(" set ");
			int i = 0;
			for(String field: fieldNms.keySet()){
				uptSql.append(fieldNms.get(field));
				uptSql.append(" = ");
				uptSql.append(" ? ");
				if(i < fieldNms.size()-1){
					uptSql.append(" , ");
				}
				i++;
			}
			uptSql.append(" where 1=1 ");
			
			List<Object[]> params = new ArrayList<Object[]>();
			Map<String, String> fieldIdNms=JpaEntityUtils.getIdColumnsByEntity(saveClass);
			Iterator<String> it = fieldIdNms.keySet().iterator();
			while (it.hasNext()) {
				String fieldIdNm = it.next();
				uptSql.append(" and ");
				uptSql.append(fieldIdNms.get(fieldIdNm));
				uptSql.append(" = ");
				uptSql.append(" ? ");
			}
			for (Object saveObjTmp : saveObjs) {
				Object[] objs = new Object[fieldNms.keySet().size()+fieldIdNms.keySet().size()];
				int index = 0;
				for (String fieldNmTmp : fieldNms.keySet()) {
					objs[index] = this.getValueByFieldExpr(fieldNmTmp,
							saveObjTmp);
					index++;
				}
				for (String fieldNmTmp : fieldIdNms.keySet()) {
					objs[index] = this.getValueByFieldExpr(fieldNmTmp,
							saveObjTmp);
					index++;
				}
				params.add(objs);
			}
			
			this.jdbcBaseDAO.batchUpdate(uptSql.toString(), params, 1000);
		}
	}
	
	public <T> void deleteEntityJdbcBatch(Collection<T> saveObjs) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder deleteSql = new StringBuilder("delete from ").append(
					tableNm);
			this.jdbcBaseDAO.batchUpdate(new String[]{deleteSql.toString()});
		}
	}
	
	public <T> void deleteEntityJdbc(Class<?> saveClass) {
		String tableNm = JpaEntityUtils.getTableName(saveClass);
		Map<String, String> fieldNms = JpaEntityUtils
				.getColumnsByEntity(saveClass);
		if (StringUtils.isEmpty(tableNm) || fieldNms == null
				|| fieldNms.size() <= 0) {
			return;
		}
		StringBuilder deleteSql = new StringBuilder("delete from ").append(
				tableNm);
		this.jdbcBaseDAO.batchUpdate(new String[]{deleteSql.toString()});
	}
	
	public <T> void deleteEntityJdbcBatch(Collection<T> saveObjs,List<String> fields) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(saveClass);
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass);
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder deleteSql = new StringBuilder("delete from ").append(
					tableNm).append(" where 1=1");
			List<Object[]> params = new ArrayList<Object[]>();
			if(fields==null||fields.size()<=0){
				fieldNms=JpaEntityUtils.getIdColumnsByEntity(saveClass);
				Iterator<String> it = fieldNms.keySet().iterator();
				while (it.hasNext()) {
					String fieldNm = it.next();
					deleteSql.append(" and ");
					deleteSql.append(fieldNms.get(fieldNm));
					deleteSql.append(" = ");
					deleteSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[fieldNms.keySet().size()];
					for (String fieldNmTmp : fieldNms.keySet()) {
						objs[index] = this.getValueByFieldExpr(fieldNmTmp,
								saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			else{
				for(String field: fields){
					deleteSql.append(" and ");
					deleteSql.append(fieldNms.get(field));
					deleteSql.append(" = ");
					deleteSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[fields.size()];
					for (String field : fields) {
						objs[index] = this.getValueByFieldExpr(field,saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			this.jdbcBaseDAO.batchUpdate(deleteSql.toString(), params, 1000);
		}
	}
	
	/**
	 * 扩充批量删除的方法，允许传入表名，用于删除子表的情况
	 * 上面那种方法子表的saveObjs是空的，无法做到删除。
	 * @param saveObjs
	 * @param sonClass
	 * @param fields
	 */
	public <T> void deleteEntityJdbcBatch(Collection<T> saveObjs, Class<?> sonClass, List<String> fields) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
			String tableNm = JpaEntityUtils.getTableName(sonClass); //子表的表名
			Map<String, String> fieldNms = JpaEntityUtils
					.getColumnsByEntity(saveClass); //主表的字段，不过也没问题，因为这里主要是获取主键字段和字段值，主表的主键一般也是子表的主键。
			if (StringUtils.isEmpty(tableNm) || fieldNms == null
					|| fieldNms.size() <= 0) {
				return;
			}
			StringBuilder deleteSql = new StringBuilder("delete from ").append(
					tableNm).append(" where 1=1");
			List<Object[]> params = new ArrayList<Object[]>();
			if(fields==null||fields.size()<=0){
				fieldNms=JpaEntityUtils.getIdColumnsByEntity(saveClass);
				Iterator<String> it = fieldNms.keySet().iterator();
				while (it.hasNext()) {
					String fieldNm = it.next();
					deleteSql.append(" and ");
					deleteSql.append(fieldNms.get(fieldNm));
					deleteSql.append(" = ");
					deleteSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[fieldNms.keySet().size()];
					for (String fieldNmTmp : fieldNms.keySet()) {
						objs[index] = this.getValueByFieldExpr(fieldNmTmp,
								saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			else{
				for(String field: fields){
					deleteSql.append(" and ");
					deleteSql.append(fieldNms.get(field));
					deleteSql.append(" = ");
					deleteSql.append(" ? ");
				}
				for (Object saveObjTmp : saveObjs) {
					int index = 0;
					Object[] objs = new Object[fields.size()];
					for (String field : fields) {
						objs[index] = this.getValueByFieldExpr(field,saveObjTmp);
						index++;
					}
					params.add(objs);
				}
			}
			this.jdbcBaseDAO.batchUpdate(deleteSql.toString(), params, 1000);
		}
	}
	
	public <T> void saveEntityJdbcBatch(Collection<T> saveObjs) {
		if (saveObjs != null && saveObjs.size() > 0) {
			Class<?> saveClass = saveObjs.iterator().next().getClass();
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
				/*if(fieldNms.get(fieldNm).equals("PRECISION"))
					uptSql.append("`"+fieldNms.get(fieldNm)+"`");
				else*/
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
					objs[index] = this.getValueByFieldExpr(fieldNmTmp,
							saveObjTmp);
					index++;
				}
				params.add(objs);
			}
			this.jdbcBaseDAO.batchUpdate(uptSql.toString(), params, 1000);
		}
	}
	
	private String getColumnNm(String column, Class<?> cla) throws NoSuchFieldException, SecurityException{
		String[] columns = StringUtils.split(column,".");
		Field field = null;
		for(int i = 0; i < columns.length ; i++){
			field = cla.getDeclaredField(columns[i]);
			cla = field.getType();
		}
		if(field != null){
			return field.getAnnotation(Column.class).name();
		}
		return "";
	}
	
	private Object getValueByFieldExpr(String fieldExpr, Object obj) {
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
	
	private Object getField(Object obj, String field) throws Exception {
		String firstLetter = field.substring(0, 1).toUpperCase();
		String getMethodName = "get" + firstLetter + field.substring(1);
		Method method = obj.getClass().getMethod(getMethodName);
		return method.invoke(obj);
	}
	
	public <X> List<X> validateExist(Object obj,Class<X> cla,Collection<String> fields){
		StringBuilder selectSql=new StringBuilder("");
		selectSql.append("select * from ");
		String tableNm = JpaEntityUtils.getTableName(cla);
		selectSql.append(tableNm);
		selectSql.append(" where 1=1");
		Object[] objs;
		Map<String, String> fieldNms = JpaEntityUtils
				.getColumnsByEntity(cla);
		if(fields==null||fields.size()<=0){
			fieldNms=JpaEntityUtils.getIdColumnsByEntity(cla);
			Iterator<String> it = fieldNms.keySet().iterator();
			while (it.hasNext()) {
				String fieldNm = it.next();
				selectSql.append(" and ");
				selectSql.append(fieldNms.get(fieldNm));
				selectSql.append(" = ");
				selectSql.append(" ? ");
			}
			int index = 0;
			objs = new Object[fieldNms.keySet().size()];
			for (String fieldNmTmp : fieldNms.keySet()) {
				objs[index] = this.getValueByFieldExpr(fieldNmTmp,obj);
				index++;
			}
		}
		else{
			for(String field: fields){
				selectSql.append(" and ");
				selectSql.append(fieldNms.get(field));
				selectSql.append(" = ");
				selectSql.append(" ? ");
			}
			int index = 0;
			objs = new Object[fields.size()];
			for (String field : fields) {
				objs[index] = this.getValueByFieldExpr(field,obj);
				index++;
			}
		}
		List<X> infos = new ArrayList<X>();
		List<Map<String, Object>>lists=jdbcBaseDAO.find(selectSql.toString(), objs);
		if(lists!=null&&lists.size()>0){
			for(Map<String,Object> list:lists){
				try {
					X info=null;
					info = cla.newInstance();
					for(String fieldNm: fieldNms.keySet()){
						if(fieldNm.indexOf(".")>=0){
							Object oInfo=ReflectionUtils.invokeGetter(info, "id");
							if(oInfo==null){
								oInfo=cla.getDeclaredField("id").getType().newInstance();
								ReflectionUtils.invokeSetter(info, "id", oInfo);
							}
						}
						ReflectionUtils._invokeSetterMethod(info, fieldNm, list.get(fieldNms.get(fieldNm)));
					}
					infos.add(info);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			}
		}
		return infos;
	}
}
