package com.yusys.bione.plugin.yuformat.web;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

/**
 * 列表生成Html与JS的构造器
 * @author xch
 *
 */
public class BillListHtmlJSBuilder {

	private YuFormatUtil bsUtil = new YuFormatUtil(); //
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

	private BillQueryHtmlJSBuilder billQueryBuilder = new BillQueryHtmlJSBuilder();

	//BillList构造
	public HtmlJavaScriptVO getBillListHtmlAndJS(String _webcontext, String _divid, String _templetCode, String[] _buttons, HashVO _listConfigVO, boolean _isHaveSwitchQuery) throws Exception {
		HashVO parentVO = null; //模板主表
		HashVO[] hvs_b = null; //模板子表

		//可以直接由反射类创建模板  JSPFree.createBillList("d1","Class:cn.com.MyBuilder.build('001','UserCode')");
		if (_templetCode.startsWith("Class:")) { //类名!!
			Object[] templetVO = bsUtil2.getTempletVO(_templetCode); //
			parentVO = (HashVO) templetVO[0]; // 第一个主表VO
			hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs
		} else { //★★★从定义的xml模板VO中计算出模板VO！
			Object[] templetVO = null; //
			try {
				templetVO = bsUtil2.getTempletVO(_templetCode); //
			} catch (Exception e) {
				e.printStackTrace();
				HtmlJavaScriptVO htmljsVO = new HtmlJavaScriptVO();
				htmljsVO.setHtml(e.getMessage());

				return htmljsVO;
			}

			parentVO = (HashVO) templetVO[0]; // 第一个主表VO
			hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs
		}

		//把前端传入的参数替换模板中的参数,以前不是替换思路，而是在使用时才实际判断
		//后来感觉还是替换更好,因为前端有可能会用到这个，假如界面实际运行是不分页,但模板VO中取出来的是分行，就不一致了!
		replaceTempletValue(parentVO, _listConfigVO, "ds"); //数据源也可以替换
		replaceTempletValue(parentVO, _listConfigVO, "fromtable"); //查询表名也可以从前端替换
		replaceTempletValue(parentVO, _listConfigVO, "savetable"); //保存表名
		replaceTempletValue(parentVO, _listConfigVO, "afterloadclass"); //后续处理类!!即有一个类可以后续处理颜色
		replaceTempletValue(parentVO, _listConfigVO, "isafterloadsetcolor"); //是否由后续类处理颜色

		replaceTempletValue(parentVO, _listConfigVO, "ishavebillquery"); //是否有查询框
		replaceTempletValue(parentVO, _listConfigVO, "list_ispagebar"); //是否分页
		replaceTempletValue(parentVO, _listConfigVO, "autoquery"); //是否自动查询
		replaceTempletValue(parentVO, _listConfigVO, "autocondition"); //自动查询条件
		replaceTempletValue(parentVO, _listConfigVO, "querycontion"); //查询条件
		replaceTempletValue(parentVO, _listConfigVO, "list_btns"); //列表按钮
		replaceTempletValue(parentVO, _listConfigVO, "list_ismultisel"); //是否多选
		replaceTempletValue(parentVO, _listConfigVO, "list_ischeckstyle"); //是否勾选
		replaceTempletValue(parentVO, _listConfigVO, "orderbys"); //排序
		replaceTempletValue(parentVO, _listConfigVO, "rownumbers"); //是否显示行号

		//如果有前端参照的SQL,则与模板中配置的条件进行合并...
		String str_querycontion = parentVO.getStringValue("querycontion"); //计算出实际查询条件
		String str_refWhereSQL = _listConfigVO.getStringValue("refWhereSQL", ""); //有可能从参照定义中传入新的SQL，即要与XML模板中定义的原始SQL进行合并!
		if (!str_refWhereSQL.equals("")) { //如果从前端指定了参照过滤条件..
			if (str_querycontion == null || str_querycontion.equals("")) {
				parentVO.setAttributeValue("querycontion", str_refWhereSQL);
			} else {
				String str_span_condition = str_querycontion + " and " + str_refWhereSQL; //
				parentVO.setAttributeValue("querycontion", str_span_condition);
			}
		} else { //
			if (str_querycontion != null) { //有可能是前端传入的查询条件!
				parentVO.setAttributeValue("querycontion", str_querycontion); //
			}
		}

		//如果指定了只要模板中的哪几列,则进行过滤
		if (_listConfigVO.containsKey("onlyItems")) {
			String str_onlyItems = _listConfigVO.getStringValue("onlyItems"); //
			String[] str_items = bsUtil.split(str_onlyItems, ";");

			ArrayList<HashVO> newItemVOList = new ArrayList<HashVO>();
			for (int i = 0; i < str_items.length; i++) {
				HashVO hvo = findItemVOByItemKey(hvs_b, str_items[i]); //
				if (hvo != null) {
					newItemVOList.add(hvo); //
				}
			}

			hvs_b = newItemVOList.toArray(new HashVO[0]); //
		}

		//先计算出Html
		String str_html = getHtml(_webcontext, _divid, parentVO, hvs_b, _buttons, _listConfigVO, _isHaveSwitchQuery);

		//再计算出JavaScript
		String str_jstext = getJavaScript(_webcontext, _divid, parentVO, hvs_b, _buttons, _listConfigVO);

		//拼装Html与JS
		HtmlJavaScriptVO htmlJsVO = new HtmlJavaScriptVO(); //
		htmlJsVO.setHtml(str_html); //
		htmlJsVO.setJavaScript(str_jstext); //

		return htmlJsVO;
	}

	//BillList构造
	public HtmlJavaScriptVO getBillListHtmlAndJsByEasyUIByItems(String _webcontext, String _divid, String _templetCode, String[] _itemKeys, String[] _itemNames, String[] _itemWidths, HashVO _listConfigVO) throws Exception {
		//★★★从定义的xml模板VO中计算出模板VO！
		HashVO parentVO = new HashVO(); //
		parentVO.setAttributeValue("templetcode", _templetCode);
		parentVO.setAttributeValue("templetname", _templetCode);
		parentVO.setAttributeValue("fromtable", "");
		parentVO.setAttributeValue("autoquery", "N"); //
		parentVO.setAttributeValue("ishavebillquery", "N"); //
		parentVO.setAttributeValue("list_ispagebar", "N"); //
		parentVO.setAttributeValue("list_ischeckstyle", "N"); //

		HashVO[] hvs_b = new HashVO[_itemKeys.length]; // 第二个是子表VOs
		for (int i = 0; i < hvs_b.length; i++) {
			hvs_b[i] = new HashVO(); //
			hvs_b[i].setAttributeValue("itemkey", _itemKeys[i]); //
			hvs_b[i].setAttributeValue("itemname", _itemNames[i]); //
			hvs_b[i].setAttributeValue("itemtype", "文本框"); //
			hvs_b[i].setAttributeValue("list_width", _itemWidths[i]); //
		}

		//先计算出Html
		String str_html = getHtml(_webcontext, _divid, parentVO, hvs_b, null, _listConfigVO, false);

		//再计算出JavaScript
		String str_jstext = getJavaScript(_webcontext, _divid, parentVO, hvs_b, null, _listConfigVO);

		//拼装Html与JS
		HtmlJavaScriptVO htmlJsVO = new HtmlJavaScriptVO(); //
		htmlJsVO.setHtml(str_html); //
		htmlJsVO.setJavaScript(str_jstext); //

		return htmlJsVO;
	}

	//寻找某一个VO..
	private HashVO findItemVOByItemKey(HashVO[] hvs_b, String _itemkey) {
		for (int i = 0; i < hvs_b.length; i++) {
			if (hvs_b[i].getStringValue("itemkey", "").equalsIgnoreCase(_itemkey)) {
				return hvs_b[i]; //
			}
		}
		return null;
	}

	//拼接Html
	private String getHtml(String _webcontext, String _divid, HashVO parentVO, HashVO[] hvs_b, String[] _buttons, HashVO _listConfigVO, boolean _isHaveSwitchQuery) {
		boolean isHaveQuery = parentVO.getBooleanValue("ishavebillquery", true); //计算是否真的需要查询面板
		StringBuilder sb_html = new StringBuilder(); //billList_
		if (_buttons != null && _buttons.length > 0) { //如果有按钮
			if (isHaveQuery) { //如果有查询框
				sb_html.append("<div class=\"easyui-layout\" style=\"width:100%;height:100%;overflow:hidden\">\r\n");
				sb_html.append("<div data-options=\"region:'south',border:false\" style=\"height:45px;text-align:center;padding-top:5px;overflow:hidden;\">\r\n");
				//遍历各个按钮...
				for (int i = 0; i < _buttons.length; i++) {
					String[] str_btnDefine = bsUtil.split(_buttons[i], "/");
					String str_btnText = str_btnDefine[0];
					String str_btnAction = str_btnDefine[1];
					String str_icon = null;
					if (str_btnDefine.length >= 3) {
						str_icon = str_btnDefine[2]; //
					}

					sb_html.append("<a id=\"" + _divid + "_BillListBtn" + (i + 1) + "\"   href=\"JavaScript:" + str_btnAction + "();\" class=\"easyui-linkbutton\" ");
					if (str_icon != null) {
						sb_html.append("data-options=\"iconCls:'" + str_icon + "'\" "); //
					}
					int li_btnWidth = 80 + ((str_btnText.length() - 2) * 12);
					sb_html.append("style=\"width:" + li_btnWidth + "px\">" + str_btnText + "</a>\r\n");
				}
				sb_html.append("</div>\r\n");

				//下面的中间内容!!
				sb_html.append("<div data-options=\"region:'center',border:false\">\r\n");

				//在center中再搞一个BorderLayout,而且是上面是查询框,下面是列表!
				sb_html.append(getQueryFormAndListHtml(_webcontext, _divid, parentVO, hvs_b, _isHaveSwitchQuery)); //★★★★查询框与列表的组合!★★★★

				//BorderLayout的
				sb_html.append("</div>\r\n"); //end Center
				sb_html.append("</div>\r\n");
			} else { //如果没有查询框,但有【确定/取消】按钮
				//创建BorderLayou布局，先加入下面的【确定/取消】按钮
				sb_html.append("<div class=\"easyui-layout\" data-options=\"border:false\" style=\"width:100%;height:100%;overflow:hidden\">\r\n");
				sb_html.append("<div data-options=\"region:'south',border:false\" style=\"height:45px;text-align:center;padding-top:5px;overflow:hidden;\">\r\n");

				for (int i = 0; i < _buttons.length; i++) {
					String[] str_btnDefine = bsUtil.split(_buttons[i], "/");
					String str_btnText = str_btnDefine[0];
					String str_btnAction = str_btnDefine[1];
					String str_icon = null;
					if (str_btnDefine.length >= 3) {
						str_icon = str_btnDefine[2]; //
					}

					sb_html.append("<a id=\"" + _divid + "_BillListBtn" + (i + 1) + "\"   href=\"JavaScript:" + str_btnAction + "();\" class=\"easyui-linkbutton\" ");
					if (str_icon != null) {
						sb_html.append("data-options=\"iconCls:'" + str_icon + "'\" "); //
					}
					int li_btnWidth = 80 + ((str_btnText.length() - 2) * 12);
					sb_html.append("style=\"width:" + li_btnWidth + "px\">" + str_btnText + "</a>\r\n");
				}
				sb_html.append("</div>\r\n");

				//再加上中间的主体内容
				sb_html.append("<div data-options=\"region:'center',border:false\">\r\n");
				sb_html.append("  <div style=\"width:100%;height:100%;overflow:auto\">\r\n"); //
				sb_html.append("    <table id=\"" + _divid + "_BillList\" style=\"width:100%;height:100%\"></table>\r\n"); //
				sb_html.append("  </div>\r\n"); //
				sb_html.append("</div>\r\n"); //end Center

				sb_html.append("</div>\r\n");
			}
		} else { //如果没有【确定/取消】等按钮!
			//如果有查询框,以前是自己搞的一个div,但很难解决拖动窗口变化时的位置与滚动框问题.
			if (isHaveQuery) { //如果有查询框
				sb_html.append(getQueryFormAndListHtml(_webcontext, _divid, parentVO, hvs_b, _isHaveSwitchQuery)); //★★★★查询框!★★★★查询框与表格的组合,使用BorderLayout布局
			} else { //没有查询框，非常简洁
				sb_html.append("<div style=\"width:100%;height:100%;overflow:hidden\">\r\n"); //
				sb_html.append("<table id=\"" + _divid + "_BillList\" style=\"width:100%;height:100%\"></table>\r\n"); //
				sb_html.append("</div>\r\n"); //
			}
		}

		//表头弹出菜单
		sb_html.append("<div id=\"" + _divid + "_BillListHeadRightMenu\" class=\"easyui-menu\" style=\"width:120px;\">\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p17'\" onclick=\"FreeUtil.onRightClickBillListHead_1(" + _divid + "_BillList,this);\">查看当前SQL</div>\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p28'\" onclick=\"FreeUtil.onRightClickBillListHead_2(" + _divid + "_BillList,this);\">查看模板配置</div>\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p26'\" onclick=\"FreeUtil.onRightClickBillListHead_3(" + _divid + "_BillList,this);\">本列模板配置</div>\r\n");
		//系统管理员才能进行穿透查询
		if(BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			sb_html.append("  <div data-options=\"iconCls:'icon-p45'\" onclick=\"FreeUtil.onRightClickBillListHead_4(" + _divid + "_BillList,this);\">穿透查询</div>\r\n");
			sb_html.append("  <div data-options=\"iconCls:'icon-p64'\" onclick=\"FreeUtil.onRightClickBillListHead_5(" + _divid + "_BillList,this);\">设置isPOC</div>\r\n");
		}
		sb_html.append("</div>\r\n");

		//表格内容弹出菜单
		sb_html.append("<div id=\"" + _divid + "_BillListRowRightMenu\" class=\"easyui-menu\" style=\"width:120px;\">\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p34'\" onclick=\"FreeUtil.onRightClickBillListRow_1(" + _divid + "_BillList,this);\">查看UI数据</div>\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p30'\" onclick=\"FreeUtil.onRightClickBillListRow_2(" + _divid + "_BillList,this);\">查看DB数据</div>\r\n");
		//sb_html.append("  <div data-options=\"iconCls:'icon-p35'\" onclick=\"FreeUtil.onRightClickBillListRow_3(" + _divid + "_BillList,this);\">UI与DB比较</div>\r\n");
		sb_html.append("</div>\r\n");

		return sb_html.toString(); //
	}

	//查询框与表格两者的布局,也是使用BorderLayout
	private String getQueryFormAndListHtml(String _webcontext, String _divid, HashVO parentVO, HashVO[] hvs_b, boolean _isHaveSwitchQuery) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<div id=\"" + _divid + "_BillQueryRoot\" class=\"easyui-layout\" style=\"width:100%;height:100%;overflow:hidden\">\r\n"); //

		String[] str_html = billQueryBuilder.getQueryFormHtml(_webcontext, _divid, parentVO, hvs_b, 1, _isHaveSwitchQuery);
		String li_domHeight = str_html[1]; //查询面板高度,构建整个布局时需要这个

		//borderlayout-NORTH(北方),总高度应该根据表单中的行数自动计算出来!
		sb_html.append("<div id=\"" + _divid + "_BillQueryNorth\" data-options=\"region:'north',border:false\" style=\"width:100%;height:" + li_domHeight + "px;overflow:hidden\">\r\n"); //

		//查询框的div,即一层层的div,像卡片一样的原理,感觉应该是form放在最外面
		sb_html.append(str_html[0]); //转调查询框的生成html

		sb_html.append("</div>\r\n"); //north

		//中间的内容
		sb_html.append("<div data-options=\"region:'center',border:false\">\r\n"); //
		sb_html.append("  <table id=\"" + _divid + "_BillList\" style=\"width:100%;height:100%\"></table>\r\n"); //
		sb_html.append("</div>\r\n"); //

		sb_html.append("</div>\r\n"); // end-easyui-layout
		return sb_html.toString(); //
	}

	//拼出JavaScript代码,然后也返回客户端,直接输出在<body>的最后,并且与LigerForm及LigerGrid匹配上.
	//这段JS源代码要与上面对应!
	private String getJavaScript(String _webcontext, String _divid, HashVO parentVO, HashVO[] hvs_b, String[] _buttons, HashVO _listConfigVO) throws Exception {
		//先把我们的模板配置输出,因为前端也需要
		String str_templetOption = bsUtil.getTempletOptionSelf(parentVO, hvs_b); //

		//输出表格的ligerUI的参数
		//输出表格的ligerUI的参数
		String str_gridOption = getGridOptionBycodeByEasyUI(_webcontext, _divid, parentVO, hvs_b, _listConfigVO); //表格属性

		//最终输出js代码..
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("try{\r\n");

		//一定要先输出函数变量,后执行JS逻辑,否则会运行失败,这一点非常重要!
		//比如设置颜色在option中设置的style属性是一个是个函数,则一定要先输出!

		sb_html.append("//首先输出各个约定函数\r\n");

		//先输出查询框点击查询的【查询】【重置】函数,查询框可以单独使用,但与列表绑定时,查询逻辑在列表中!
		if (parentVO.getBooleanValue("ishavebillquery", true)) { //如果真的有查询面板
			sb_html.append("var onQuery_" + _divid + " = function(){\r\n"); //
			sb_html.append(" JSPFree.doQueryFromBillQuery(" + _divid + "_BillQuery," + _divid + "_BillList);\r\n"); //实际查询数据!!
//			sb_html.append("var maskUtil = FreeUtil.getMaskUtil();\r\n");
//			sb_html.append("maskUtil.mask();\r\n");
			sb_html.append("setTimeout(function () {\r\n");
			sb_html.append("if ("+ _divid + "_BillList.CurrSQL3 != null) {\r\n");
			sb_html.append("var new_sql_f = " + _divid + "_BillList.CurrSQL3;\r\n");
			sb_html.append("var new_sql = 'select count(*) c from (' + new_sql_f + ') t';\r\n");
			sb_html.append("var jso_data = null; \r\n");
//			sb_html.append("if (\"undefined\" == typeof str_ds) { \r\n");
//			sb_html.append(" jso_data = JSPFree.getHashVOs(new_sql); \r\n");
//			sb_html.append("} else { \r\n");
//			sb_html.append(" jso_data = JSPFree.getHashVOsByDS(new_sql, str_ds);\r\n");
//			sb_html.append("} \r\n");
//			sb_html.append("var c = jso_data[0].c;\r\n");
			sb_html.append("if (" + _divid + "_BillList.datagrid('getData').total == 0) {JSPFree.alert('当前条件查询结果，无数据！');}\r\n");
			sb_html.append("};\r\n");
//			sb_html.append("maskUtil.unmask();\r\n");
			sb_html.append("}, 100);\r\n");
			sb_html.append("};\r\n");
			sb_html.append("\r\n");

			sb_html.append("var onReset_" + _divid + " = function(){\r\n"); //
			sb_html.append(" $('#" + _divid + "_QueryForm').form('clear');;\r\n");
			sb_html.append("};\r\n");
			sb_html.append("\r\n");

			//切换第二种查询
			sb_html.append("var onSwitchQuery2_" + _divid + " = function(_type){\r\n"); //
			sb_html.append(" FreeUtil.billQuerySwitch2(" + _divid + "_BillQuery,_type);\r\n"); //重绘第二种查询
			sb_html.append("};\r\n");

			//弹出灵活组装窗口
			sb_html.append("var onOpenFreeQuery_" + _divid + " = function(){\r\n"); //
			sb_html.append(" FreeUtil.billQueryOpenFreeBuild(" + _divid + "_BillQuery);\r\n");
			sb_html.append("};\r\n");
			sb_html.append("\r\n");
		}

		//选择事件
		sb_html.append("var " + _divid + "_BillList_Selected = function(rowIndex, rowData){\r\n"); //
		sb_html.append("var selFunc = " + _divid + "_BillList.rowSelectChanged;\r\n");
		sb_html.append("if(typeof selFunc==\"function\"){\r\n"); //
		sb_html.append("  selFunc(rowIndex,rowData);\r\n");
		sb_html.append("}\r\n");
		sb_html.append("};\r\n");
		sb_html.append("\r\n");

		//双击事件
		sb_html.append("var " + _divid + "_BillList_DblClickRow = function(rowIndex, rowData){\r\n"); //
		sb_html.append("var dbFunc = " + _divid + "_BillList.dblClickRow;\r\n");
		sb_html.append("if(typeof dbFunc==\"function\"){\r\n"); //
		sb_html.append("  dbFunc(rowIndex,rowData);\r\n");
		sb_html.append("}\r\n");
		sb_html.append("};\r\n");
		sb_html.append("\r\n");

		//表头右键
		sb_html.append("var " + _divid + "_BillList_OnRightClickHead = function(_event, _field){\r\n"); //
		sb_html.append("  FreeUtil.onRightClickBillListHead(" + _divid + "_BillList,_event,_field);\r\n"); //
		sb_html.append("};\r\n");
		sb_html.append("\r\n");

		//选中行右键
		sb_html.append("var " + _divid + "_BillList_OnRightClickRow = function(_event, _rowIndex, _rowData){\r\n"); //
		sb_html.append("  FreeUtil.onRightClickBillListRow(" + _divid + "_BillList,_event, _rowIndex, _rowData);\r\n"); //
		sb_html.append("};\r\n");
		sb_html.append("\r\n");

		//点击事件,点击勾选框时，需要这个事件
		sb_html.append("var " + _divid + "_BillList_ClickCell = function(_index, _field, _value){\r\n"); //
		sb_html.append("FreeUtil.onBillListClickCell(" + _divid + "_BillList,_index, _field, _value);\r\n"); //统统转调
		//sb_html.append("d1_BillList.datagrid('selectRow', index).datagrid('beginEdit', index);\r\n"); //行内编辑,以后再弄!
		sb_html.append("};\r\n");
		sb_html.append("\r\n");

		//处理所有下拉框的格式化,即要显示名称..
		for (int i = 0; i < hvs_b.length; i++) {
			String str_itemkey = hvs_b[i].getStringValue("itemkey"); //
			String str_itemtype = hvs_b[i].getStringValue("itemtype"); //
			String str_itemdefine = hvs_b[i].getStringValue("itemdefine"); //
			String str_hrefaction = hvs_b[i].getStringValue("list_hrefaction"); //
			String str_formatvalidate = hvs_b[i].getStringValue("formatvalidate"); // 关于数字文本，需要特殊处理

			// 鼠标移至grid列表，显示内容
			sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n"); //
			sb_html.append("  var str_hreftip = \"\";\r\n");
			sb_html.append("  if(_value!=null){ \r\n");
			sb_html.append("  _value= _value.replace(/'/g,\"\"); \r\n"); // 此处单引号特殊处理，add by wxy 20200415
			sb_html.append("   str_hreftip=_value; \r\n");
			sb_html.append("  } \r\n");
			sb_html.append("  var str_viewtext = _rowData['" + str_itemkey + "'];\r\n"); //下拉框的显示字段!
			sb_html.append("  return \"<span title='\" + str_hreftip + \"' data-itemkey='" + str_itemkey
					+ "' data-itemvalue='\" + _value + \"' data-rowindex='\" + _rowIndex + \"' style='cursor:pointer'>\" + str_viewtext + \"</span>\";\r\n"); //下拉框的显示字段!
			sb_html.append("};\r\n");
			sb_html.append("\r\n");
			
			HashMap<String, String> defineMap = bsUtil.getDefineMap(str_itemdefine); //定义的Map
			if (str_itemtype.equals("文本框")) {
				// 针对数字文本特殊处理
				if (StringUtils.isNotEmpty(str_formatvalidate)) {
					String[] str_items = str_formatvalidate.split("/");
					if ("数字文本".equals(str_items[0])) {
						// 此处，判断精度，进行补位
						sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n");
						// 获取精度
						String p_str = str_items[1].substring(3);
						// 根据精度，进行小数位补位
						sb_html.append("  var str_value = \"\";\r\n");
						sb_html.append("  if (_value != \"\" && typeof _value != \"undefined\") { \r\n");
						sb_html.append("  	if (_value.indexOf('E') >= 0) { \r\n");
						sb_html.append("  		_value = parseFloat(_value).toFixed("+ p_str +"); \r\n");
						sb_html.append("  	} \r\n");
						sb_html.append("  	str_value = FreeUtil.validateStrNum(_value, "+p_str+", \"\");\r\n");
						sb_html.append("  } else { \r\n");
						sb_html.append("  	str_value = _value;\r\n");
						sb_html.append("  }; \r\n");
						
						sb_html.append("  var str_warnmsg = _rowData[\"" + str_itemkey + "◆warnmsg\"];\r\n"); //看有没有警告消息?
						sb_html.append("  return FreeUtil.getWarnMsg(str_value,str_warnmsg);\r\n"); //如果是百分比,则强行在后面加上百
						sb_html.append("};\r\n");
						sb_html.append("\r\n");
					}
				}
				// 文本框添加超链接处理
				if (str_hrefaction != null && !str_hrefaction.equals("")) { //如果有超连接
					//超连接,则有个下划线并可点击!
					sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n"); //
					sb_html.append("  var str_hreftip = \"\";\r\n");
					sb_html.append("  if(_value!=null && _value.length>20){ \r\n");
					sb_html.append("  _value= _value.replace(/'/g,\"\"); \r\n"); // 此处单引号特殊处理，add by wxy 20200415
					sb_html.append("   str_hreftip=_value; \r\n");
					sb_html.append("  } \r\n");
					sb_html.append("  var str_viewtext = _rowData['" + str_itemkey + "'];\r\n"); //下拉框的显示字段!
					sb_html.append("  return \"<span title='\" + str_hreftip + \"' onclick='" + str_hrefaction + "(this);'  data-itemkey='" + str_itemkey
							+ "' data-itemvalue='\" + _value + \"' data-rowindex='\" + _rowIndex + \"' style='text-decoration:underline;color:blue;cursor:pointer'>\" + str_viewtext + \"</span>\";\r\n"); //下拉框的显示字段!
					sb_html.append("};\r\n");
					sb_html.append("\r\n");
				}
				if (parentVO.getBooleanValue("isafterloadsetcolor", false)) { //如果说有彩色定义
					sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n"); //
					sb_html.append("  var str_warnmsg = _rowData[\"" + str_itemkey + "◆warnmsg\"];\r\n"); //看有没有警告消息?
					sb_html.append("  return FreeUtil.getWarnMsg(_value,str_warnmsg);\r\n"); //如果是百分比,则强行在后面加上百分号
					sb_html.append("};\r\n");
					sb_html.append("\r\n");
				}
			} else if (str_itemtype.equals("数字框")) {
				sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n");
				if ("Y".equalsIgnoreCase(defineMap.get("是否百分比"))) {
					if (StringUtils.isNotBlank(defineMap.get("精度"))) {
						sb_html.append("  var str_value = Number(_value).toFixed(" + defineMap.get("精度") + ") + '%';\r\n");
					} else {
						sb_html.append("  var str_value = _value + '%';\r\n");
					}
				} else {
					if (StringUtils.isNotBlank(defineMap.get("精度"))) {
						sb_html.append("  var str_value = Number(_value).toFixed(" + defineMap.get("精度") + ");\r\n");
					} else {
						sb_html.append("  var str_value = _value;\r\n");
					}
				}
				sb_html.append("  var str_warnmsg = _rowData[\"" + str_itemkey + "◆warnmsg\"];\r\n"); //看有没有警告消息?
				sb_html.append("  return FreeUtil.getWarnMsg(str_value,str_warnmsg);\r\n"); //如果是百分比,则强行在后面加上百
				sb_html.append("};\r\n");
				sb_html.append("\r\n");
			} else if (str_itemtype.equals("下拉框")) {
				//三个参数:【_value:本格子的值】【_rowData:这一行的所有值】【_rowIndex:第几行】
				sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n"); //
				if (str_hrefaction != null && !str_hrefaction.equals("")) {
					sb_html.append("var str_viewtext = _rowData['" + str_itemkey + "_◆view◆'];\r\n"); //下拉框的显示字段!
					sb_html.append("return \"<a href='JavaScript:" + str_hrefaction + "'>\" + str_viewtext + \"</a>\";\r\n"); //下拉框的显示字段!
				} else {
					sb_html.append("var str_viewValue = _rowData['" + str_itemkey + "_◆view◆'];\r\n"); //下拉框的显示字段!
					sb_html.append("var str_warnmsg = _rowData[\"" + str_itemkey + "◆warnmsg\"];\r\n"); //看有没有警告消息?
					sb_html.append("return FreeUtil.getWarnMsg(str_viewValue,str_warnmsg);\r\n"); //如果是百分比,则强行在后面加上百
				}
				sb_html.append("};\r\n");
				sb_html.append("\r\n");
			} else if (str_itemtype.equals("勾选框")) {
				long li_now = System.currentTimeMillis(); //
				//三个参数:【_value:本格子的值】【_rowData:这一行的所有值】【_rowIndex:第几行】
				sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n"); //
				//sb_html.append("console.log(_value);  \r\n"); //勾选框在前端处理
				//sb_html.append("console.log(_rowIndex);  \r\n"); //
				sb_html.append("if ('Y'==_value){ \r\n"); //勾选框在前端处理
				sb_html.append("  return \"<input type='checkbox' checked='true' onclick='return false;'>\";\r\n"); //勾选框在前端处理 
				sb_html.append("} else { \r\n");
				sb_html.append("  return \"<input type='checkbox' onclick='return false;'>\";\r\n"); // onclick='return false;'取消选中,不是checked=false,而是不要这个属性!
				sb_html.append("}\r\n");
				sb_html.append("};\r\n");
				sb_html.append("\r\n");
			} else if (str_itemtype.equals("按钮")) { //按钮
				sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n"); //
				String str_btnHtml = "";

				//输出各个按钮!!
				String str_btnDefine = hvs_b[i].getStringValue("itemdefine"); //
				if (str_btnDefine != null && !str_btnDefine.equals("")) {
					//先把所有主键值输出
					String str_pknames = parentVO.getStringValue("pkname"); //
					String[] str_pkitems = bsUtil.split(str_pknames, ","); //
					String str_pkHtml_1 = ""; //
					String str_pkHtml_2 = ""; //
					for (int k = 0; k < str_pkitems.length; k++) { //遍历各个主键
						str_pkHtml_1 = str_pkHtml_1 + "  var str_" + str_pkitems[k] + " = _rowData['" + str_pkitems[k] + "'];\r\n";
						str_pkHtml_2 = str_pkHtml_2 + " data-" + str_pkitems[k] + "='\" + str_" + str_pkitems[k] + " + \"' ";
					}
					sb_html.append(str_pkHtml_1);

					//根据按钮定义
					HashMap<String, String> btnDfMap = bsUtil.getDefineMap(str_btnDefine); //把定义转换成HashMap
					String str_buttons = btnDfMap.get("buttons");
					if (str_buttons != null) {
						String[] str_buttons_item = bsUtil.split(str_buttons, ";");
						String[][] str_btns = new String[str_buttons_item.length][2]; //
						for (int k = 0; k < str_buttons_item.length; k++) {
							int li_pos = str_buttons_item[k].indexOf("/"); //
							str_btns[k][0] = str_buttons_item[k].substring(0, li_pos);
							str_btns[k][1] = str_buttons_item[k].substring(li_pos + 1, str_buttons_item[k].length());
						}

						for (int b = 0; b < str_btns.length; b++) {
							str_btnHtml = str_btnHtml + "<span data-rowindex='\" + _rowIndex + \"' " + str_pkHtml_2 + " style='background:#3C8DBC;color:#FFFFFF;padding:8px;margin-left:10px;' onclick='" + str_btns[b][1] + "(this);'>" + str_btns[b][0] + "</span>";
						}
					} else {
						str_btnHtml = "定义没有buttons";
					}

				} else {
					str_btnHtml = "没有按钮定义";
				}

				sb_html.append("  return \"" + str_btnHtml + "\";\r\n"); //根据按钮的配置,返回多个span,还可以点击
				sb_html.append("};\r\n");
				sb_html.append("\r\n");
			} else if (str_hrefaction != null && !str_hrefaction.equals("")) { //如果有超连接
				//超连接,则有个下划线并可点击!
				sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n"); //
				sb_html.append("  var str_hreftip = \"\";\r\n");
				sb_html.append("  if(_value!=null && _value.length>20){ \r\n");
				sb_html.append("  _value= _value.replace(/'/g,\"\"); \r\n"); // 此处单引号特殊处理，add by wxy 20200415
				sb_html.append("   str_hreftip=_value; \r\n");
				sb_html.append("  } \r\n");
				sb_html.append("  var str_viewtext = _rowData['" + str_itemkey + "'];\r\n"); //下拉框的显示字段!
				sb_html.append("  return \"<span title='\" + str_hreftip + \"' onclick='" + str_hrefaction + "(this);'  data-itemkey='" + str_itemkey
						+ "' data-itemvalue='\" + _value + \"' data-rowindex='\" + _rowIndex + \"' style='text-decoration:underline;color:blue;cursor:pointer'>\" + str_viewtext + \"</span>\";\r\n"); //下拉框的显示字段!
				sb_html.append("};\r\n");
				sb_html.append("\r\n");
			} else { //其他类型
				if (parentVO.getBooleanValue("isafterloadsetcolor", false)) { //如果说有彩色定义
					sb_html.append("var " + _divid + "_" + str_itemkey + "_formatter = function(_value,_rowData,_rowIndex){\r\n"); //
					sb_html.append("  var str_warnmsg = _rowData[\"" + str_itemkey + "◆warnmsg\"];\r\n"); //看有没有警告消息?
					sb_html.append("  return FreeUtil.getWarnMsg(_value,str_warnmsg);\r\n"); //如果是百分比,则强行在后面加上百分号
					sb_html.append("};\r\n");
					sb_html.append("\r\n");
				}
			}
		}

		//如果设置了是由后续加载类处理颜色,则使用第二种颜色函数
		if (parentVO.getBooleanValue("isafterloadsetcolor", false)) {
			//第二种颜色,即根据查询数据的内容显示颜色
			for (int i = 0; i < hvs_b.length; i++) {
				String str_itemkey = hvs_b[i].getStringValue("itemkey"); //
				sb_html.append(getItemColorFunJS2(_divid, str_itemkey));
			}
		} else { //使用模板配置的
			//颜色公式的逻辑,这个函数名要与后面的表格配置一致.
			for (int i = 0; i < hvs_b.length; i++) {
				String str_colorformula = hvs_b[i].getStringValue("list_colorformula"); //
				if (str_colorformula != null && !str_colorformula.equals("")) {
					String str_itemkey = hvs_b[i].getStringValue("itemkey"); //
					sb_html.append(getItemColorFunJS(_divid, str_itemkey, str_colorformula)); //这是一段根据颜色公式解析反向生成的JS代码!
				}
			}
		}

		//真正开始执行JS逻辑代码...
		//输出模板原始配置对象
		sb_html.append("var jso_templet_" + _divid + " = " + str_templetOption + ";\r\n"); //模板VO的Json对象
		//输出列表的对象
		sb_html.append("var jso_list_" + _divid + " = " + str_gridOption + ";\r\n"); //EasyUI的表格的配置对象!
		sb_html.append("console.log(" + str_gridOption + ")"); //EasyUI的表格的配置对象!
		
		sb_html.append("\r\n");

		//创建查询框对象
		sb_html.append("var " + _divid + "_BillQuery = {};\r\n");
		sb_html.append(_divid + "_BillQuery.divid= \"" + _divid + "\";\r\n");
		sb_html.append(_divid + "_BillQuery.billtype= \"BillQuery\";\r\n"); //
		sb_html.append(_divid + "_BillQuery.queryLevel=1;\r\n"); //默认查询级别是1,即常用查询
		sb_html.append(_divid + "_BillQuery.templetVO = jso_templet_" + _divid + ";\r\n"); //查询框的对象也是这个

		//绑定表单!
		sb_html.append("var " + _divid + "_QueryForm = document.getElementById('" + _divid + "_QueryForm');\r\n"); //
		sb_html.append(_divid + "_BillQuery.form=" + _divid + "_QueryForm;\r\n"); //

		sb_html.append("\r\n");

		//创建表格
		sb_html.append("var " + _divid + "_BillList = $(\"#" + _divid + "_BillList\").datagrid(jso_list_" + _divid + ");\r\n"); //
		sb_html.append(_divid + "_BillList.divid= \"" + _divid + "\";\r\n");
		sb_html.append(_divid + "_BillList.billtype= \"BillList\";\r\n");
		sb_html.append(_divid + "_BillList.templetVO = jso_templet_" + _divid + ";\r\n"); //把模板配置的VO绑定到表格对象中!

		//把查询框与列表互相绑定,即可以从查询框对象获取到对应的列表对象,同样从列表也可以得到查询框
		sb_html.append("\r\n");
		sb_html.append("//把列表与查询框互相绑定\r\n");
		sb_html.append(_divid + "_BillQuery.bindBillList= " + _divid + "_BillList;\r\n"); //查询框可以得到列表
		sb_html.append(_divid + "_BillList.bindBillQuery= " + _divid + "_BillQuery;\r\n"); //列表可以得到查询框

		sb_html.append("\r\n");

		//如果有分页,则加上分页的一些属性
		boolean isPager = parentVO.getBooleanValue("list_ispagebar", true); //
		if (isPager) {
			sb_html.append("var " + _divid + "_pager = " + _divid + "_BillList.datagrid('getPager'); \r\n");

			sb_html.append(_divid + "_pager.pagination({\r\n");
			sb_html.append("   showRefresh:false,\r\n"); //显示刷新
			sb_html.append("   onChangePageSize : function(pageSize){\r\n"); //改变每页数量!
			sb_html.append("     FreeUtil.resetOnePageSize(" + _divid + "_BillList,pageSize);\r\n");
			sb_html.append("   }, \r\n");

			sb_html.append("    onSelectPage : function(pageNumber, pageSize){\r\n");
			sb_html.append("     FreeUtil.skipToOnePage(" + _divid + "_BillList,pageNumber);\r\n");
			sb_html.append("   }\r\n");
			sb_html.append("});\r\n");
		}

		//加载数据,如果指定了自动查询,则采用异步加载数据!
		boolean isAutoQuery = parentVO.getBooleanValue("autoquery", false); //是否自动查询!
		//如果需要自动查询..
		if (isAutoQuery) {
			String str_autocondition = parentVO.getStringValue("autocondition", ""); //查询查询条件
			String str_refWhereSQL = _listConfigVO.getStringValue("refWhereSQL", ""); //参照引用
			if (str_autocondition.equals("")) { //如果XML定义的为空,则直接使用参照定义的!
				str_autocondition = str_refWhereSQL; //
			} else { //如果XML中有定义,且有参照传入的!则进行and拼接...
				if (!str_refWhereSQL.equals("")) {
					str_autocondition = str_autocondition + " and " + str_refWhereSQL;
				}
			}

			sb_html.append("FreeUtil.queryDataByConditonReal(" + _divid + "_BillList,\"" + str_autocondition + "\",true,1,null,false);\r\n"); //异步查询数据
		}

		sb_html.append("}catch(_ex){\r\n");
		sb_html.append("console.log(_ex);\r\n");
		sb_html.append("}\r\n");
		return sb_html.toString(); //
	}

	//计算颜色公式函数,这个函数名与option中配置的正好约定了一致...
	//这个返回的css描述最终实际运行时是在在<td>的style中!<td field="from_table2" style="color:red;">
	//所以理论是自己也可以不使用其option的原理，而是自己直接去操作dom修改style的
	private String getItemColorFunJS(String _divid, String _itemkey, String str_colorformula) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("var " + _divid + "_" + _itemkey + "_cellStyler = function(_value,_rowData,_rowIndex){\r\n"); //

		HashMap<String, String> btnDfMap = bsUtil.getDefineMap(str_colorformula); //把定义转换成HashMap
		String str_colordf = btnDfMap.get("colors");
		if (str_colordf == null) {
			sb_html.append(" return \"\";\r\n");
		} else {
			String[] str_color_items = bsUtil.split(str_colordf, ";");
			String[][] str_colors = new String[str_color_items.length][2]; // { { "日报", "#FFB0FF" }, { "月报", "#A6FFA6" } };
			for (int i = 0; i < str_color_items.length; i++) {
				int li_pos = str_color_items[i].indexOf("/"); //
				str_colors[i][0] = str_color_items[i].substring(0, li_pos);
				str_colors[i][1] = str_color_items[i].substring(li_pos + 1, str_color_items[i].length());
			}

			String str_colorType = "background"; //默认是背景
			String str_type = btnDfMap.get("type"); //类型,如果不定义,则默认为是背景色
			if (str_type != null) {
				if (str_type.equals("前景") || str_type.equals("前景色")) {
					str_colorType = "color";
				} else if (str_type.equals("背景") || str_type.equals("背景色")) {
					str_colorType = "background";
				}
			}

			//循环输出JS代码..
			for (int i = 0; i < str_colors.length; i++) {
				if (i == 0) {
					sb_html.append("if");
				} else {
					sb_html.append("else if");
				}
				sb_html.append("('" + str_colors[i][0] + "'==_value || _value.indexOf('" + str_colors[i][0] + "')==0) { \r\n");
				sb_html.append(" return \"" + str_colorType + ":" + str_colors[i][1] + ";\";\r\n"); //background-position-x: 35%;background-repeat:no-repeat;
				sb_html.append("} ");
			}
			sb_html.append("\r\n");
		}

		//以后是一段判断,即根据实际值生成颜色!
		sb_html.append("};\r\n");
		sb_html.append("\r\n");
		return sb_html.toString();
	}

	//根据实际数据决定颜色
	private String getItemColorFunJS2(String _divid, String _itemkey) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("var " + _divid + "_" + _itemkey + "_cellStyler2 = function(_value,_rowData,_rowIndex){\r\n"); //

		//循环输出JS代码..
		sb_html.append("  var str_warncolor = _rowData[\"" + _itemkey + "◆warncolor\"];\r\n"); //看有没有设置颜色?
		sb_html.append("  if(typeof str_warncolor !=\"undefined\" && str_warncolor!=\"\") {\r\n"); //如果定义了警告颜色
		//sb_html.append("    return \"background:#FF6868;\";\r\n");
		sb_html.append("    return \"background:\" + str_warncolor;\r\n"); //设置返回颜色!
		sb_html.append("  } else {\r\n");
		sb_html.append("    return \"\";\r\n");
		sb_html.append("  }\r\n");

		sb_html.append("};\r\n");
		//以后是一段判断,即根据实际值生成颜色!
		sb_html.append("\r\n");
		return sb_html.toString();
	}

	//表格配置属性..
	public String getGridOptionBycodeByEasyUI(String _contextPath, String _divId, HashVO parentVO, HashVO[] hvs_b, HashVO _listConfigVO) throws Exception {
		//创建返回的对象!pagination:true,
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("{\r\n"); //
//		sb_html.append("rownumbers:true,\r\n"); //行号
		sb_html.append("autoRowHeight:false,\r\n"); //是否自动行变高?false时性能会高
		sb_html.append("striped:true,\r\n"); //奇偶行变色
		sb_html.append("idField:\"_rownum\",\r\n"); //设置唯一记录标记字段,极其关键,计算选中行就是根据这个来
		if (parentVO.containsKey("remotesort") && !parentVO.getBooleanValue("remotesort")) {
			sb_html.append("remoteSort:false,\r\n");
		}
		// 是否显示行号(true默认显示行号)
		if (parentVO.getBooleanValue("rownumbers", true)) {
			sb_html.append("rownumbers:true,\r\n"); //行号
		} else {
			sb_html.append("rownumbers:false,\r\n"); //行号
		}
		
		//列表是否多选?
		if (parentVO.getBooleanValue("list_ismultisel", false)) {
			sb_html.append("singleSelect:false,\r\n"); //单选为否,即多选
		} else {
			sb_html.append("singleSelect:true,\r\n"); //单选为true,即单选
		}

		//如果是勾选模式,则默认是相互关联,即勾选即选中行,选中行即勾选!
		if (parentVO.getBooleanValue("list_ischeckstyle", false)) {
			sb_html.append("selectOnCheck:true,\r\n"); //单选
			sb_html.append("checkOnSelect:true,\r\n"); //单选
		}

		//sb_html.append("showFooter:true,\r\n"); //表格底部
		sb_html.append("onClickCell : " + _divId + "_BillList_ClickCell,\r\n"); //点击格子

		sb_html.append("onSelect : " + _divId + "_BillList_Selected,\r\n"); //选择事件
		sb_html.append("onDblClickRow : " + _divId + "_BillList_DblClickRow,\r\n"); //双击事件
		sb_html.append("onHeaderContextMenu : " + _divId + "_BillList_OnRightClickHead,\r\n"); //点击表头右键
		sb_html.append("onRowContextMenu : " + _divId + "_BillList_OnRightClickRow,\r\n"); //选中一条记录右键

		//是否分布
		boolean isPager = parentVO.getBooleanValue("list_ispagebar", true); //
		sb_html.append("pagination:" + isPager + ",\r\n"); //
		if (isPager) { //如果有分页!
			sb_html.append("pageSize:20,\r\n"); //默认每页20个
		}

		//点击事件

		//计算按钮列表配置
		String str_btntext = parentVO.getStringValue("list_btns"); //计算出列表按钮,有可能会前端覆盖!
		if (str_btntext != null && !str_btntext.trim().equals("")) {
			sb_html.append(getListButtonOption(str_btntext, _divId)); //列表按钮配置语法
		}

		//计算各个字段配置
		sb_html.append("columns:[[\r\n");
		sb_html.append(getAllColumnOption(_divId, parentVO, hvs_b, _listConfigVO)); //加上所有的列定义...
		sb_html.append("]]\r\n");
		sb_html.append("}\r\n"); //
		return sb_html.toString(); //
	}

	//计算列表按钮语法
	private StringBuilder getListButtonOption(String str_btntext, String _divId) {
		StringBuilder sb_html = new StringBuilder(); //
		HashMap<String, String> btnTextMap = getBtnTextMap(); //按钮文字
		HashMap<String, String> btnIconMap = getBtnIconMap(); //按钮图标
		HashMap<String, String> btnFuncMap = getBtnFuncMap(_divId); //按钮函数

		//拼接EasyUI语法
		sb_html.append("toolbar:[\r\n");
		String[] str_btns = bsUtil.split(str_btntext, ";"); // 定义的所有按钮.
		for (int i = 0; i < str_btns.length; i++) {
			String str_text = ""; //按钮名称
			String str_icon = ""; //图标名称
			String str_funname = ""; //函数名字

			if (str_btns[i].startsWith("$")) { //$预置按钮,即增加,删除,修改!
				str_text = btnTextMap.get(str_btns[i]); //
				str_icon = btnIconMap.get(str_btns[i]); //
				str_funname = btnFuncMap.get(str_btns[i]); //
			} else {
				if (str_btns[i].startsWith("[")) { //有图标
					str_icon = str_btns[i].substring(1, str_btns[i].indexOf("]")); //图标
					String str_remainText = str_btns[i].substring(str_btns[i].indexOf("]") + 1, str_btns[i].length()); //剩下的文本
					int li_pos = str_remainText.indexOf("/"); //
					if (li_pos > 0) { //如果有方法
						str_text = str_remainText.substring(0, li_pos); //
						str_funname = str_remainText.substring(li_pos + 1, str_remainText.length()); //
					} else {
						str_text = str_remainText; //如果没有方法
					}
				} else {
					int li_pos = str_btns[i].indexOf("/"); //
					if (li_pos > 0) {
						str_text = str_btns[i].substring(0, li_pos); //
						str_funname = str_btns[i].substring(li_pos + 1, str_btns[i].length()); //
					} else {
						str_text = str_btns[i]; //如果没有方法
					}
					str_icon = "icon-ok"; //默认图标
				}
			}

			//是否有工具条
			sb_html.append("{\r\n");
			sb_html.append("text:\"" + str_text + "\",\r\n"); //按钮文字
			//sb_html.append("id:\"" + _divId + "_BillList_Btn" + (i + 1) + "\",\r\n"); //按钮文字
			sb_html.append("id:\"" + _divId + "_BillList_Btn" + str_text + "\",\r\n"); //按钮文字
			sb_html.append("iconCls:\"" + str_icon + "\",\r\n"); //按钮图标

			sb_html.append("handler:function(){\r\n"); //点击函数
			if (str_funname == null || str_funname.equals("")) {
				sb_html.append("alert('该按钮没有定认函数名,请使用/后面加函数名!');\r\n"); //
			} else {
				if (str_funname.indexOf("(") < 0) {
					sb_html.append(str_funname + "(this,event);\r\n"); //
				} else {
					sb_html.append(str_funname + ";\r\n"); //
				}
			}
			sb_html.append("}\r\n");
			sb_html.append("}"); //
			if (i != str_btns.length - 1) {
				sb_html.append(","); //
			}
			sb_html.append("\r\n"); //
		}
		sb_html.append("],\r\n");

		return sb_html;
	}

	//得到所有列的配置,最关键的逻辑!
	private StringBuilder getAllColumnOption(String _divid, HashVO _templetVO, HashVO[] _templetItemVOs, HashVO _listConfigVO) {
		StringBuilder sb_html = new StringBuilder(); //

		//如果是勾选模式,则在第一列加一个勾选框!
		if (_templetVO.getBooleanValue("list_ischeckstyle", false)) {
			sb_html.append("{\r\n"); //
			sb_html.append("field:\"ck\",\r\n"); //
			sb_html.append("title:\"ck\",\r\n"); //
			sb_html.append("checkbox:true\r\n"); //勾选框
			sb_html.append("},\r\n"); //
		}

		//循环输出各个字段列..
		for (int i = 0; i < _templetItemVOs.length; i++) {
			String str_itemkey = _templetItemVOs[i].getStringValue("itemkey"); //
			String str_itemname = _templetItemVOs[i].getStringValue("itemname"); //
			String str_itemtype = _templetItemVOs[i].getStringValue("itemtype"); //
			String str_itemdefine = _templetItemVOs[i].getStringValue("itemdefine"); //

			HashMap<String, String> defineMap = bsUtil.getDefineMap(str_itemdefine); //

			boolean isListShow = _templetItemVOs[i].getBooleanValue("list_isshow", true); //列表是否显示?
			if (!isListShow) { //如果是列表不显示,则是当隐藏域处理
				sb_html.append("{\r\n"); //
				sb_html.append("field:\"" + str_itemkey + "\",\r\n"); //
				sb_html.append("title:\"" + str_itemname + "\",\r\n"); //
				sb_html.append("hidden:true\r\n"); //隐藏
				sb_html.append("},\r\n"); //
			} else { //不是隐藏列
				sb_html.append("{\r\n"); //
				sb_html.append("field:\"" + str_itemkey + "\",\r\n"); //
				sb_html.append("title:\"" + str_itemname + "\",\r\n"); //
				if (_templetItemVOs[i].containsKey("is_sortable") 
						&& _templetItemVOs[i].getBooleanValue("is_sortable", true)) {
					sb_html.append("sortable : true,\r\n"); //是否可以双击表头排序
				}
				//列表宽度!
				String str_listwidth = _templetItemVOs[i].getStringValue("list_width"); // 列表宽度
				if (str_listwidth == null || str_listwidth.equals("")) {
					sb_html.append("width:80,\r\n"); //宽度默认80
				} else {
					sb_html.append("width:" + str_listwidth + ",\r\n");
				}

				//列表居中/居左
				sb_html.append("halign : \"" + "center" + "\",\r\n"); //表头永远强行居中,否则就是表体居左,表头也居左
				String str_list_align = _templetItemVOs[i].getStringValue("list_align"); //
				if (str_list_align == null || str_list_align.equals("")) {
					sb_html.append("align : \"" + "left" + "\",\r\n"); //宽度
				} else if (str_list_align.equals("中")) {
					sb_html.append("align : \"" + "center" + "\",\r\n"); //宽度
				} else if (str_list_align.equals("右")) {
					sb_html.append("align : \"" + "right" + "\",\r\n"); //宽度
				} else {
					sb_html.append("align : \"" + "left" + "\",\r\n"); //宽度
				}

				//处理颜色公式,假如有模板主表参数设置了是由后续处理类设置颜色,则所有字段都绑定颜色回调函数
				if (_templetVO.getBooleanValue("isafterloadsetcolor", false)) {
					sb_html.append("styler : " + _divid + "_" + str_itemkey + "_cellStyler2,\r\n"); //
				} else { //否则只对模板子表中设置了颜色公式的才绑定颜色回调函数
					String str_colorformula = _templetItemVOs[i].getStringValue("list_colorformula"); //
					if (str_colorformula != null && !str_colorformula.equals("")) {
						sb_html.append("styler : " + _divid + "_" + str_itemkey + "_cellStyler,\r\n"); //style是一个函数!
					}
				}

				String str_hrefaction = _templetItemVOs[i].getStringValue("list_hrefaction"); //

				//根据不同的控件类型进行处理
				sb_html.append("formatter : " + _divid + "_" + str_itemkey + "_formatter,\r\n"); //如果是超连接
				
				if (str_itemtype.equals("数字框")) {
					//if ("Y".equalsIgnoreCase(defineMap.get("是否百分比"))) {
					sb_html.append("formatter : " + _divid + "_" + str_itemkey + "_formatter,\r\n"); //数字框有时要显示百分号
					//}
				} else if (str_itemtype.equals("下拉框")) {
					//sb_html.append("editor : {type:'combobox',options:{}},\r\n");
					sb_html.append("formatter : " + _divid + "_" + str_itemkey + "_formatter,\r\n"); //下拉框显示的值一定要是name,所以必须是formatter
				} else if (str_itemtype.equals("勾选框")) {
					//sb_html.append("editor : {type:'checkbox',options:{on:'Y',off:'N'}},\r\n");
					sb_html.append("formatter : " + _divid + "_" + str_itemkey + "_formatter,\r\n"); //勾选框一定要显示成一个checkbox
				} else if (str_itemtype.equals("按钮")) { //如果是按钮
					sb_html.append("formatter : " + _divid + "_" + str_itemkey + "_formatter,\r\n"); //如果是按钮,则是一个独特的显示风格,即有多个按钮,还可以点击!
				} else if (str_hrefaction != null && !str_hrefaction.equals("")) {
					sb_html.append("formatter : " + _divid + "_" + str_itemkey + "_formatter,\r\n"); //如果是超连接
				} else {
					if (_templetVO.getBooleanValue("isafterloadsetcolor", false)) { //如果说有彩色定义
						sb_html.append("formatter : " + _divid + "_" + str_itemkey + "_formatter,\r\n");
					}
				}

				sb_html.append("MyOrder : " + (i + 1) + "\r\n"); //最后加一个,为了逗号方便
				sb_html.append("},\r\n"); //
			}
		}

		//最后一列永远是_rownum,而且是隐藏,因为这是用来唯一标识某一行的!!
		//而且正好不要逗号
		sb_html.append("{\r\n"); //
		sb_html.append("field:\"_rownum\",\r\n"); //
		sb_html.append("title:\"_rownum\",\r\n"); //
		sb_html.append("width:50,\r\n"); //
		sb_html.append("hidden:true\r\n"); //隐藏
		sb_html.append("}\r\n"); //

		//System.out.println(sb_html.toString());
		return sb_html; //
	}

	//把前端传入的参数替换模板中的参数,以前不是替换思路，而是在使用时才实际判断
	//后来感觉还是替换更好,因为前端有可能会用到这个，假如界面实际运行是不分页,但模板VO中取出来的是分行，就不一致了,也乱了!
	private void replaceTempletValue(HashVO parentVO, HashVO _listConfigVO, String _itemkey) {
		if (_listConfigVO.containsKey(_itemkey)) {
			parentVO.setAttributeValue(_itemkey, _listConfigVO.getStringValue(_itemkey)); //
		}
	}

	private HashMap<String, String> getBtnTextMap() {
		HashMap<String, String> map = new HashMap<String, String>(); //
		map.put("$INSERT", "新增"); //
		map.put("$UPDATE", "编辑"); //
		map.put("$DELETE", "删除"); //
		map.put("$VIEW", "查看"); //
		map.put("$VIEW2", "查看"); //
		map.put("$ExportExcel", "导出Excel"); //
		map.put("$VIEWTASK", "查看任务"); //
		return map;
	}

	private HashMap<String, String> getBtnIconMap() {
		HashMap<String, String> map = new HashMap<String, String>(); //
		map.put("$INSERT", "icon-p99"); //icon-add
		map.put("$UPDATE", "icon-p79"); //icon-edit"
		map.put("$DELETE", "icon-remove"); //icon-cut
		map.put("$VIEW", "icon-p81"); //icon-p31
		map.put("$VIEW2", "icon-p81"); //icon-p31
		map.put("$ExportExcel", "icon-p47"); //
		map.put("$VIEWTASK", "icon-p31"); //
		return map;
	}

	//默认按钮逻辑
	private HashMap<String, String> getBtnFuncMap(String _divId) {
		HashMap<String, String> map = new HashMap<String, String>(); //
		map.put("$INSERT", "JSPFree.doBillListInsert(" + _divId + "_BillList)"); //
		map.put("$UPDATE", "JSPFree.doBillListUpdate(" + _divId + "_BillList)"); //
		map.put("$DELETE", "JSPFree.doBillListDelete(" + _divId + "_BillList)"); //
		map.put("$VIEW", "JSPFree.doBillListView(" + _divId + "_BillList)"); //
		map.put("$VIEW2", "JSPFree.doBillListView2(" + _divId + "_BillList)"); //直接查看内存
		map.put("$ExportExcel", "JSPFree.downloadBillListDataAsExcel(" + _divId + "_BillList)"); //导出列表数据为Excel
		map.put("$VIEWTASK", "JSPFree.doBillListView(" + _divId + "_BillList)"); //
		return map;
	}

}
