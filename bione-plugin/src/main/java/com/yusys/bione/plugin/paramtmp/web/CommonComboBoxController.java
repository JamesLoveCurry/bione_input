package com.yusys.bione.plugin.paramtmp.web;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.SqlValidateUtils;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.paramtmp.ParamModule;
import com.yusys.bione.plugin.paramtmp.service.CommonComboBoxBS;

/**
 * 
 * <pre>
 * Title:下拉框和树的处理控制
 * Description: 提供对动态参数模板自定义配置中树和下拉框一些相关值的查询
 * </pre>
 * 
 * @author fangjuan fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */

@Controller
@RequestMapping("/report/frame/param/commonComboBox")
public class CommonComboBoxController extends BaseController {

	@Autowired
	private CommonComboBoxBS boxBS;

	/**
	 * 得到下拉框的JSON格式的数据源 fangjuan
	 * 
	 * @return
	 */
	@RequestMapping(value = "/systemVariables.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getSystemVariables() {
		List<CommonComboBoxNode> list = Lists.newArrayList();
		for (ParamModule p : ParamModule.values()) {
			CommonComboBoxNode node = new CommonComboBoxNode();
			node.setId(p.name());
			node.setText(p.toChinese());
			list.add(node);
		}
		return list;
	}

	/**
	 * 得到JSON格式的数据源
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dataSources.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getDataSources() {
		return boxBS.getDataSource();
	}

	/**
	 * 下拉框预览时，执行sql语句
	 * 
	 * */
	@RequestMapping(value = "/executeSelectSQL.*/{id}", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> executeDbSQL(
			@PathVariable("id") String paramId, String value, String text) {
		return this.boxBS.executeSelectSqlView(paramId, value, text);

	}
	/**
	 * 修改时执行SQL语句
	 * @param db 数据库ID
	 * @param sql 要执行的SQL语句
	 * @param value 相关联的下拉框选中的值
	 * @param text 相关联的下拉框选中的显示值
	 * @return
	 */
	@RequestMapping(value = "/executeSelectSQL.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> executeSQL(String db, String sql,
			String value, String text) {
		if (StringUtils.isNotEmpty(value)) {
			sql = StringUtils.replace(sql, "#{selectedValue}", value);
		}
		if (StringUtils.isNotEmpty(text)) {
			sql = StringUtils.replace(sql, "#{selectedText}", text);
		}
		String s = boxBS.replace(sql);
		return boxBS.executeSelectSqlEdit(db, s);

	}
	
	/**
	 * 预览时执行树的SQL语句
	 * @param paramId 参数ID
	 */
	@RequestMapping(value = "/executeTreeSQL.*/{id}", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> executeTreeSQL(
			@PathVariable("id") String paramId, String value, String text) {
		return this.boxBS.executeTreeSqlView(paramId, value, text);

	}
	
	/**
	 * 弹出树修改时执行SQL语句 
	 * @param db 数据库的ID
	 * @param sql 要执行的SQL
	 * @return
	 */
	@RequestMapping(value = "/executeTreeSQL.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> treeExecuteWithSQL(String db, String sql, String value, String text) {
		if(StringUtils.isNotEmpty(text)){
			if (StringUtils.isNotEmpty(value)) {
				sql = StringUtils.replace(sql, "#{selectedValue}", value);
			}
				sql = StringUtils.replace(sql, "#{selectedText}", text);
		}else{
			if (StringUtils.isNotEmpty(value)) {
				sql = StringUtils.replace(sql, "#{parentValue}", value);
			}
		}
		String s = boxBS.replace(sql);
		return boxBS.executeTreeSqlEdit(db, s);

	}
	
	/**
	 * 预览时执行弹出树的SQL语句
	 * @param paramId 参数ID
	 */
	@RequestMapping(value = "/executePopupSQL.*/{id}", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> executePopupSQL(
			@PathVariable("id") String paramId, String value, String text,String searchName) {
		return this.boxBS.executePopupSqlView(paramId, value, text,searchName);

	}
	
	/**
	 * 树修改时执行SQL语句 
	 * @param db 数据库的ID
	 * @param sql 要执行的SQL
	 * @return
	 */
	@RequestMapping(value = "/executePopupSQL.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> popupExecuteWithSQL(String db, String sql, String value, String text,String searchName) {
		if(StringUtils.isNotEmpty(text)){
			if (StringUtils.isNotEmpty(value)) {
				sql = StringUtils.replace(sql, "#{selectedValue}", value);
			}
				sql = StringUtils.replace(sql, "#{selectedText}", text);
		}else{
			if (StringUtils.isNotEmpty(value)) {
				sql = StringUtils.replace(sql, "#{parentValue}", value);
			}
		}
		String s = boxBS.replace(sql);
		//2020 lcy 【后台管理】sql注入 代码优化
		if(SqlValidateUtils.validateStr(searchName)) {
			searchName = SqlValidateUtils.replaceValue(searchName);
		}
		return boxBS.executePopupSqlEdit(db, s,searchName);

	}
	
}
