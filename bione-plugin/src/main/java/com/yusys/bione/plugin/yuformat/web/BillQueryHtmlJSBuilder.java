package com.yusys.bione.plugin.yuformat.web;

import java.util.ArrayList;

import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

//查询框,即可以单独存在,又同时在BillList中使用!
public class BillQueryHtmlJSBuilder {

	private YuFormatUtil bsUtil = new YuFormatUtil(); //
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

	private BillCardHtmlJSBuilder billCardBuilder = new BillCardHtmlJSBuilder();

	//BillQuery
	public HtmlJavaScriptVO getBillQueryHtmlAndJS(String _webcontext, String _divid, String _templetCode) throws Exception {
		//★★★从定义的xml模板VO中计算出模板VO！
		Object[] templetVO = bsUtil2.getTempletVO(_templetCode); //
		HashVO parentVO = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		//先计算出Html
		String str_html = getHtml(_webcontext, _divid, parentVO, hvs_b);

		//再计算出JavaScript
		String str_jstext = getJavaScript(_webcontext, _divid, parentVO, hvs_b, true);

		//拼装Html与JS
		HtmlJavaScriptVO htmlJsVO = new HtmlJavaScriptVO(); //
		htmlJsVO.setHtml(str_html); //
		htmlJsVO.setJavaScript(str_jstext); //

		return htmlJsVO;
	}

	//切换第二种查询条件时的html
	public String[] getBillQuerySwitch2Html(String _webcontext, String _divid, String _templetCode, int _buildType) throws Exception {
		//★★★从定义的xml模板VO中计算出模板VO！
		Object[] templetVO = bsUtil2.getTempletVO(_templetCode); //
		HashVO parentVO = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		StringBuilder sb_html = new StringBuilder(); //billList_
		sb_html.append("<div style=\"width:100%;\">\r\n"); //高度是自动撑高,宽度是计算出来

		String[] str_html2 = getQueryFormHtml(_webcontext, _divid, parentVO, hvs_b, _buildType, true); //
		//System.err.println(str_html2[0]); //

		sb_html.append(str_html2[0]); //
		sb_html.append("</div>\r\n"); //

		return new String[] { sb_html.toString(), str_html2[1] }; //
	}

	//自由组合的查询
	public String getBillQueryFreeBuildHtml(String _webcontext, String _divid, HashVO parentVO, HashVO[] hvs_b, String[] _checkitems) throws Exception {
		StringBuilder sb_html = new StringBuilder(); //billList_
		sb_html.append("<div style=\"width:100%;\">\r\n"); //高度是自动撑高,宽度是计算出来

		//先把Form搞在最外面,因为里面是一行一个div,遇到查询是否换行就处理
		sb_html.append("<form id=\"" + _divid + "_QueryForm\" name=\"" + _divid + "_QueryForm\" method=\"post\">\r\n");

		//循环每个控件,每个控件一行!
		for (int i = 0; i < _checkitems.length; i++) {
			HashVO hvo_item = findTempletItemVO(hvs_b, _checkitems[i]);
			int li_width = billCardBuilder.getItemWidth(hvo_item, true);

			//输出一行的Div,一定要带宽度,只有这样才能实现不自动换行!
			sb_html.append("<div style=\"width:" + (li_width + 20) + "px;padding:5px;\">\r\n");

			sb_html.append(getQueryItemRealHtml(hvo_item, _divid)); //直接的各种控件

			sb_html.append("</div>\r\n"); //
		}

		//结束From表单输出
		sb_html.append("</form>\r\n");
		sb_html.append("</div>\r\n"); //
		return sb_html.toString();
	}

	//找到模板子表VO
	private HashVO findTempletItemVO(HashVO[] hvs_b, String _itemkey) {
		for (int i = 0; i < hvs_b.length; i++) {
			if (hvs_b[i].getStringValue("itemkey").equals(_itemkey)) {
				return hvs_b[i];
			}
		}
		return null;
	}

	//单独的查询框
	private String getHtml(String _webcontext, String _divid, HashVO parentVO, HashVO[] hvs_b) {
		StringBuilder sb_html = new StringBuilder(); //billList_
		sb_html.append("<div style=\"width:100%;\">\r\n"); //高度是自动撑高,宽度是计算出来

		String[] str_html = getQueryFormHtml(_webcontext, _divid, parentVO, hvs_b, 1, false);
		sb_html.append(str_html[0]); //
		sb_html.append("</div>\r\n"); //
		return sb_html.toString(); //
	}

	//计算出查询控件的各行的控件的模板VO
	public ArrayList<ArrayList<HashVO>> computeQueryRowItems(HashVO parentVO, HashVO[] hvs_b, String _isShowItemkey, String _isWrapItemkey) {
		ArrayList<ArrayList<HashVO>> rowDivList = new ArrayList<ArrayList<HashVO>>(); //
		for (int i = 0; i < hvs_b.length; i++) {
			boolean isQueryShow = hvs_b[i].getBooleanValue(_isShowItemkey, false); //默认是查询不显示的!
			boolean isWrap = hvs_b[i].getBooleanValue(_isWrapItemkey, false); //默认是查询框显换行的
			if (isQueryShow) {
				if (rowDivList.size() == 0) { //如果是第一个,则直接创建
					ArrayList<HashVO> rowList = new ArrayList<HashVO>();
					rowList.add(hvs_b[i]); //
					rowDivList.add(rowList); //
				} else {
					if (isWrap) { //如果卡片换行
						ArrayList<HashVO> rowList = new ArrayList<HashVO>();
						rowList.add(hvs_b[i]); //
						rowDivList.add(rowList); //
					} else { //如果卡片不换行
						ArrayList rowList = rowDivList.get(rowDivList.size() - 1); //取得最后一行!
						rowList.add(hvs_b[i]); //加入
					}
				}
			}
		}
		return rowDivList; //
	}

	private boolean isHaveQueryLavel(HashVO[] hvs_itemVOs, String _queryLevel) {
		for (int i = 0; i < hvs_itemVOs.length; i++) {
			boolean isQueryShow = hvs_itemVOs[i].getBooleanValue(_queryLevel, false);
			if (isQueryShow) {
				return true; //只要有一个显示,则就表示有!
			}
		}

		return false; //
	}

	//查询框直接的Html,因为单独的查询框与列表上面的查询都会用到这个,所权要单独拧出来搞一个方法
	public String[] getQueryFormHtml(String _webcontext, String _divid, HashVO parentVO, HashVO[] hvs_b, int _buildType, boolean _isCanSwitch) {

		//以后循环输出一行一行的，即只要是查询是否换行,则就另起一行,每一行的宽度是绝对值,是根据控件宽度累加!!!
		//与卡片中表单生成原理一样,但【查询】与【重置】这两个按钮是放在第一行，只种界面风格特别完美，即查询按钮永远紧靠着查询条件!
		String str_isShowItemkey = "query_isshow"; //(_isSwitch2 ? "query2_isshow" : "query_isshow");
		String str_isWrapItemkey = "query_iswrap"; //
		if (_buildType == 2) {
			str_isShowItemkey = "query2_isshow"; //判断是否显示的字段
			str_isWrapItemkey = "query2_iswrap"; //判断是否换行的字段
		} else if (_buildType == 3) {
			str_isShowItemkey = "query3_isshow"; //判断是否显示的字段
			str_isWrapItemkey = "query3_iswrap"; //判断是否换行的字段
		}

		boolean isHaveQuery2 = isHaveQueryLavel(hvs_b, "query2_isshow");
		boolean isHaveQuery3 = isHaveQueryLavel(hvs_b, "query3_isshow");

		//计算出查询框各行的
		ArrayList<ArrayList<HashVO>> allRowList = computeQueryRowItems(parentVO, hvs_b, str_isShowItemkey, str_isWrapItemkey);
		int li_domHeight = (allRowList.size() * 40) + 5; //根据查询条件是否换行,计算出总共有几行,然后每行高30像素,从而计算出总高
		if (allRowList.size() == 0) {
			li_domHeight = 50; //如果只有一行
		}

		StringBuilder sb_html = new StringBuilder();
		//先把Form搞在最外面,因为里面是一行一个div,遇到查询是否换行就处理
		sb_html.append("<form id=\"" + _divid + "_QueryForm\" name=\"" + _divid + "_QueryForm\" method=\"post\">\r\n");

		//边框
		String str_border = ""; //
		boolean isDebugBorder = parentVO.getBooleanValue("card_isdebugborder", false);
		if (isDebugBorder) {
			str_border = "border:1px solid red;";
		}

		//输出隐藏域,即表单中定义的强制条件..

		if (allRowList.size() == 0) {
			//输出一行的Div,一定要带宽度,只有这样才能实现不自动换行!
			sb_html.append("<div style=\"width:250px;padding:5px;" + str_border + "\">\r\n");
			sb_html.append(getThreeQueryBtns(_divid, _isCanSwitch)); //三个按钮
			sb_html.append("</div>\r\n"); //一行一个div
		} else {
			for (int i = 0; i < allRowList.size(); i++) {
				ArrayList<HashVO> oneRowList = allRowList.get(i); //一行的数据
				int li_thisRowWidth = billCardBuilder.getThisRowWidth(oneRowList, true); //计算出这一行的宽度
				li_thisRowWidth = li_thisRowWidth + 20;

				//如果是第一行,因为后面有【查询】【重置】【下拉】三个按钮,所以还要再加上他们的宽度!
				if (i == 0) {
					li_thisRowWidth = li_thisRowWidth + 245; //(85+85+30+多余)
				}

				//输出一行的Div,一定要带宽度,只有这样才能实现不自动换行!
				sb_html.append("<div style=\"width:" + li_thisRowWidth + "px;padding:5px;" + str_border + "\">\r\n");

				//输出这一行中的各列!
				for (int j = 0; j < oneRowList.size(); j++) {
					HashVO itemVO = oneRowList.get(j); //得到这一项的模板配置
					sb_html.append(getQueryItemRealHtml(itemVO, _divid)); //直接的各种控件
				}

				//如果是第一行,则在最后加上【查询】【重置】【下拉选择】三个按钮
				if (i == 0) {
					sb_html.append(getThreeQueryBtns(_divid, _isCanSwitch)); //
				}

				sb_html.append("</div>\r\n"); //一行一个div
			}
		}

		//最后输出弹出菜单的三个
		if (_isCanSwitch) {
			sb_html.append("<div id=\"" + _divid + "_PopFreeQueryMenu\" style=\"width:100px;\">\r\n");

			//只有存在中级或高级时，才同时出现常用，否则就没切换的意义了
			if (isHaveQuery2 || isHaveQuery3) {
				sb_html.append("  <div data-options=\"iconCls:'icon-p06'\" onclick=\"onSwitchQuery2_" + _divid + "(1);\">常用查询</div>\r\n");
			}

			if (isHaveQuery2) {
				sb_html.append("  <div data-options=\"iconCls:'icon-p35'\" onclick=\"onSwitchQuery2_" + _divid + "(2);\">中级查询</div>\r\n");
			}

			if (isHaveQuery3) {
				sb_html.append("  <div data-options=\"iconCls:'icon-p32'\" onclick=\"onSwitchQuery2_" + _divid + "(3);\">高级查询</div>\r\n");
			}

			sb_html.append("  <div data-options=\"iconCls:'icon-p51'\" onclick=\"onOpenFreeQuery_" + _divid + "();\">灵活查询</div>\r\n");
			sb_html.append("</div>\r\n");
		}

		//结束From表单输出
		sb_html.append("</form>\r\n");
		return new String[] { sb_html.toString(), "" + li_domHeight }; //
	}

	//真正计算每个控件的实际html,因为有两个地方要用,所以独立成一个方法!
	private String getQueryItemRealHtml(HashVO itemVO, String _divid) {
		String str_itemkey = itemVO.getStringValue("itemkey");
		String str_itemname = itemVO.getStringValue("itemname");
		String str_itemtype = itemVO.getStringValue("itemtype");
		String str_query_itemtype = itemVO.getStringValue("query_itemtype");
		if (str_query_itemtype != null && !str_query_itemtype.equals("")) {
			str_itemtype = str_query_itemtype;
		}

		StringBuilder sb_html = new StringBuilder();
		if (str_itemtype.equals("文本框")) {
			sb_html.append(billCardBuilder.getTextBoxHtml(itemVO, true));
		} else if (str_itemtype.equals("下拉框")) {
			sb_html.append(billCardBuilder.getComboBoxHtml(_divid, itemVO, true)); //因为查询框中的下拉框生成与表单中其实一样,所以转调卡片中的
		} else if (str_itemtype.equals("勾选框")) {
			sb_html.append(billCardBuilder.getCheckBoxHtml(itemVO, true));
		} else if (str_itemtype.equals("日历")) {
			sb_html.append(billCardBuilder.getDateHtml(itemVO, true));
		} else if (str_itemtype.equals("日历区间")) {
			sb_html.append(billCardBuilder.getRefHtml(itemVO, true, _divid, "FreeUtil.openTwoDateRefDialog", "icon-date")); //两个日历的区别参照
		} else if (str_itemtype.equals("列表参照")) {
			sb_html.append(billCardBuilder.getBillListRefHtml(itemVO, true, _divid));
		} else if (str_itemtype.equals("列表购物车参照")) {
			sb_html.append(billCardBuilder.getBillListShopCartRefHtml(itemVO, true, _divid));
		} else if (str_itemtype.equals("树型参照")) {
			sb_html.append(billCardBuilder.getBillTreeRefHtml(itemVO, true, _divid));
		} else if (str_itemtype.equals("自定义参照")) {
			sb_html.append(billCardBuilder.getCustRefHtml(itemVO, true, _divid));
		} else {
			sb_html.append(billCardBuilder.getTextBoxHtml(itemVO, true));
		}

		return sb_html.toString();
	}

	//【查询】【重置】【更多】三个按钮,因为有多个地方用到,所以搞成一个方法!
	private String getThreeQueryBtns(String _divid, boolean _isCanSwitch) {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<a href='#' id = \"" + _divid + "_search_button_id\"  onclick=\"onQuery_" + _divid + "();\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-search1'\" style=\"margin-left:20px;width:85px\">查询</a>");
		sb_html.append("<a href='#' onclick=\"onReset_" + _divid + "();\" class=\"easyui-linkbutton\" data-options=\"iconCls:'icon-reset1'\" style=\"margin-left:10px;width:85px\">重置</a>");
		if (_isCanSwitch) {
			sb_html.append("<a href='#' class=\"easyui-menubutton\" data-options=\"iconCls:'icon-p03',menu:'#" + _divid + "_PopFreeQueryMenu'\"></a>"); //下拉
		}
		sb_html.append("\r\n");
		return sb_html.toString(); //
	}

	//拼出JavaScript代码,然后也返回客户端,直接输出在<body>的最后,并且与LigerForm及LigerGrid匹配上.
	//这段JS源代码要与上面对应!
	public String getJavaScript(String _webcontext, String _divid, HashVO parentVO, HashVO[] hvs_b, boolean _isHavaQueryFunc) throws Exception {
		//先把我们的模板配置输出,因为前端也需要
		String str_templetOption = bsUtil.getTempletOptionSelf(parentVO, hvs_b); //

		//输出表格的ligerUI的参数
		//输出表格的ligerUI的参数
		//GetGridOption gridOptionBuilder = new GetGridOption();
		//String str_gridOption = gridOptionBuilder.getGridOptionBycodeByEasyUI(_webcontext, _divid, parentVO, hvs_b); //表格属性

		//最终输出js代码..
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("try{\r\n");

		//输出模板原始配置对象
		sb_html.append("var jso_templet_" + _divid + " = " + str_templetOption + ";\r\n");
		sb_html.append("var " + _divid + "_BillQuery = {};\r\n"); //创建查询对象!_d1_1_A
		sb_html.append(_divid + "_BillQuery.divid= \"" + _divid + "\";\r\n");
		sb_html.append(_divid + "_BillQuery.billtype= \"BillQuery\";\r\n");
		sb_html.append(_divid + "_BillQuery.queryLevel=1;\r\n"); //默认查询级别是1,即常用查询
		sb_html.append(_divid + "_BillQuery.templetVO = jso_templet_" + _divid + ";\r\n"); //绑定对象!

		//约定的查询方法
		if (_isHavaQueryFunc) {
			sb_html.append("\r\n");
			sb_html.append("//输出各个约定函数\r\n");

			sb_html.append("var onQuery_" + _divid + " = function(){\r\n"); //
			sb_html.append(" var fn_query = " + _divid + "_BillQuery.onQuery;\r\n"); //onQuery是由开发人员自己绑定的查询对象!
			sb_html.append(" if((typeof fn_query) == \"function\"){\r\n");
			sb_html.append("   var str_sqlCons = JSPFree.getQueryFormSQLCons(" + _divid + "_BillQuery);\r\n"); //templetVO
			sb_html.append("   fn_query(str_sqlCons); \r\n");
			sb_html.append(" }\r\n");
			sb_html.append("};\r\n");

			sb_html.append("var onReset_" + _divid + " = function(){\r\n"); //
			sb_html.append(" $('#" + _divid + "_QueryForm').form('clear');\r\n");
			sb_html.append("};\r\n");
		}
		sb_html.append("}catch(_ex){\r\n");
		sb_html.append("console.log(_ex);\r\n");
		sb_html.append("}\r\n");
		return sb_html.toString(); //
	}

}
