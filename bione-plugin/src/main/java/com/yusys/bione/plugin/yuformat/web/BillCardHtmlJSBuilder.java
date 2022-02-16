package com.yusys.bione.plugin.yuformat.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

//卡片
public class BillCardHtmlJSBuilder {

	private YuFormatUtil bsUtil = new YuFormatUtil(); //
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

	//BillCard生成
	public HtmlJavaScriptVO getBilCardHtmlAndJS(String _webcontext, String _divid, String _templetCode, String[] _buttons, HashVO _cardConfigVO) throws Exception {

		//★★★从定义的xml模板VO中计算出模板VO！
		Object[] templetVO = bsUtil2.getTempletVO(_templetCode); //
		HashVO parentVO = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		//把前端传入的参数替换模板中的参数,以前不是替换思路，而是在使用时才实际判断
		//后来感觉还是替换更好,因为前端有可能会用到这个，假如界面实际运行是不分页,但模板VO中取出来的是分行，就不一致了!
		replaceTempletValue(parentVO, _cardConfigVO, "ds"); //数据源也可以替换
		replaceTempletValue(parentVO, _cardConfigVO, "fromtable"); //查询表名也可以从前端替换
		replaceTempletValue(parentVO, _cardConfigVO, "savetable"); //保存表名
		replaceTempletValue(parentVO, _cardConfigVO, "afterloadclass"); //加载数据类
		replaceTempletValue(parentVO, _cardConfigVO, "isafterloadsetcolor"); //是否由后续类处理颜色

		String str_jsCode = null;
		if (templetVO.length > 2) {
			str_jsCode = (String) templetVO[2]; //JS代码
		}

		//有的时候需要共享一个模板,从前端指定只要模板中的几个字段,则进行过滤
		String str_onlyItems = _cardConfigVO.getStringValue("onlyItems");
		if (str_onlyItems != null) {
			String[] str_items = bsUtil.split(str_onlyItems, ";"); //
			ArrayList<HashVO> onlyItemList = new ArrayList<HashVO>(); //
			for (int i = 0; i < str_items.length; i++) {
				for (int j = 0; j < hvs_b.length; j++) {
					if (hvs_b[j].getStringValue("itemkey").equalsIgnoreCase(str_items[i])) {
						hvs_b[j].setAttributeValue("card_isshow", "Y"); //强制显示
						hvs_b[j].setAttributeValue("card_iswrap", "Y"); //强制不换行
						hvs_b[j].setAttributeValue("card_iseditable", "Y"); //强制可编辑
						onlyItemList.add(hvs_b[j]);
						break; //
					}
				}
			}
			hvs_b = onlyItemList.toArray(new HashVO[0]); //模板子表VO
		}

		//先计算出Html
		String str_html = getHtml(_webcontext, _divid, parentVO, hvs_b, _buttons);

		//再计算出JavaScript
		String str_jstext = getJavaScript(_webcontext, _divid, parentVO, hvs_b, str_jsCode);

		//拼装Html与JS
		HtmlJavaScriptVO htmlJsVO = new HtmlJavaScriptVO(); //
		htmlJsVO.setHtml(str_html); //
		htmlJsVO.setJavaScript(str_jstext); //
		return htmlJsVO;
	}

	//创建表单的Html
	private String getHtml(String str_webcontext, String str_divid, HashVO parentVO, HashVO[] hvs_b, String[] _buttons) {
		StringBuilder sb_html = new StringBuilder();
		//如果有按钮,则使用EasyUI的BorderLayout来布局,下面是固定高度,有【保存】【取消】两个按钮
		if (_buttons != null && _buttons.length > 0) {
			sb_html.append("<div class=\"easyui-layout\" style=\"width:100%;height:100%;\">\r\n");
			sb_html.append("<div data-options=\"region:'south',border:false\" style=\"height:45px;text-align:center;padding-top:5px;overflow:hidden;\">\r\n");

			//输出各个按钮
			for (int i = 0; i < _buttons.length; i++) {
				String[] str_btnDefine = bsUtil.split(_buttons[i], "/");
				String str_btnText = str_btnDefine[0];
				String str_btnAction = str_btnDefine[1];
				String str_icon = null;
				if (str_btnDefine.length >= 3) {
					str_icon = str_btnDefine[2]; //
				}

				sb_html.append("<a id=\"" + str_divid + "_BillCardBtn" + (i + 1) + "\"   href=\"JavaScript:" + str_btnAction + "();\" class=\"easyui-linkbutton\" ");
				if (str_icon != null) {
					sb_html.append("data-options=\"iconCls:'" + str_icon + "'\" "); //
				}
				int li_btnWidth = 80 + ((str_btnText.length() - 2) * 12);
				sb_html.append("style=\"width:" + li_btnWidth + "px\">" + str_btnText + "</a>\r\n");
			}

			sb_html.append("</div>\r\n");
			sb_html.append("<div id=\"" + str_divid + "_BillCardDiv\" data-options=\"region:'center',border:false\">\r\n");

			sb_html.append(getCardFormHtml(str_webcontext, str_divid, parentVO, hvs_b));

			sb_html.append("</div>\r\n"); //end Center
			sb_html.append("</div>\r\n");

		} else { //如果不需要按钮,则直接就一个div,里面就是每行一个div，很简洁!
			sb_html.append("<div id=\"" + str_divid + "_BillCardDiv\" style=\"width:100%;height:100%;overflow:auto\">\r\n");
			sb_html.append(getCardFormHtml(str_webcontext, str_divid, parentVO, hvs_b));
			sb_html.append("</div>\r\n"); //
		}
		return sb_html.toString(); //
	}

	//卡片中的核心代码,那整个From表单
	private String getCardFormHtml(String str_webcontext, String str_divid, HashVO parentVO, HashVO[] hvs_b) {
		StringBuilder sb_html = new StringBuilder();
		sb_html.append("<form id=\"" + str_divid + "_form\" name=\"" + str_divid + "_form\">\r\n");
		//创建各个表单项

		ArrayList<HashVO> hiddenItemList = new ArrayList<HashVO>(); //
		ArrayList<ArrayList<HashVO>> rowDivList = new ArrayList<ArrayList<HashVO>>(); //
		for (int i = 0; i < hvs_b.length; i++) {
			boolean isCardShow = hvs_b[i].getBooleanValue("card_isshow", true); //卡片是否显示
			boolean iswrap = hvs_b[i].getBooleanValue("card_iswrap", false); //卡片是否换行
			if (isCardShow) {
				if (rowDivList.size() == 0) { //如果是第一个,则直接创建
					ArrayList<HashVO> rowList = new ArrayList<HashVO>();
					rowList.add(hvs_b[i]); //
					rowDivList.add(rowList); //
				} else {
					if (iswrap) { //如果卡片换行
						ArrayList<HashVO> rowList = new ArrayList<HashVO>();
						rowList.add(hvs_b[i]); //
						rowDivList.add(rowList); //
					} else { //如果卡片不换行
						ArrayList rowList = rowDivList.get(rowDivList.size() - 1); //取得最后一行!
						rowList.add(hvs_b[i]); //加入
					}
				}
			} else {
				hiddenItemList.add(hvs_b[i]); //
			}
		}

		String str_border = ""; //
		boolean isDebugBorder = parentVO.getBooleanValue("card_isdebugborder", false);
		if (isDebugBorder) {
			str_border = "border:1px solid red;";
		}
		//先输出所有隐藏域
		for (int i = 0; i < hiddenItemList.size(); i++) {
			HashVO itemVO = hiddenItemList.get(i); //
			String str_itemkey = itemVO.getStringValue("itemkey"); //
			sb_html.append("<input name=\"" + str_itemkey + "\" type=\"hidden\">\r\n");
		}

		//输入各行
		for (int i = 0; i < rowDivList.size(); i++) {
			ArrayList<HashVO> rowList = rowDivList.get(i); //一行的数据
			int li_thisRowWidth = getThisRowWidth(rowList, false); //计算出这一行的宽度
			li_thisRowWidth = li_thisRowWidth + 20;
			sb_html.append("<div style=\"width:" + li_thisRowWidth + "px;padding-left:5px;padding-right:5px;" + str_border + "\">\r\n");

			//输出一行中的各个列,即各个表单项!!
			for (int j = 0; j < rowList.size(); j++) { //遍历这一行的所有列
				HashVO itemVO = rowList.get(j); //
				String str_itemkey = itemVO.getStringValue("itemkey");
				String str_itemtype = itemVO.getStringValue("itemtype");

				String str_rightClick = "FreeUtil.onRightClickBillCard(" + str_divid + "_BillCard,'" + str_divid + "','" + str_itemkey + "',event,this);";
				sb_html.append("<span id=\"" + str_divid + "_CardItem_" + str_itemkey + "\" style=\"display:inline-block;vertical-align:top;margin-top:5px;margin-bottom:5px;\"  oncontextmenu=\"" + str_rightClick + "\">");

				//不同的控件类型不一样!
				if (str_itemtype.equals("Label")) {
					sb_html.append(getLabelHtml(str_divid, itemVO, false));
				} else if (str_itemtype.equals("文本框")) {
					sb_html.append(getTextBoxHtml(itemVO, false));
				} else if (str_itemtype.equals("数字框")) {
					sb_html.append(getNumberBoxHtml(itemVO, false));
				} else if (str_itemtype.equals("下拉框")) {
					sb_html.append(getComboBoxHtml(str_divid, itemVO, false)); //比较复杂
				} else if (str_itemtype.equals("多行文本框")) {
					sb_html.append(getTextAreaHtml(itemVO, false));
				} else if (str_itemtype.equals("日历")) {
					sb_html.append(getDateHtml(itemVO, false));
				} else if (str_itemtype.equals("时间")) {
					sb_html.append(getDateTimeHtml(itemVO, false));
				} else if (str_itemtype.equals("勾选框")) {
					sb_html.append(getCheckBoxHtml(itemVO, false));
				} else if (str_itemtype.equals("列表参照")) {
					sb_html.append(getBillListRefHtml(itemVO, false, str_divid));
				} else if (str_itemtype.equals("列表购物车参照")) {
					sb_html.append(getBillListShopCartRefHtml(itemVO, false, str_divid));
				} else if (str_itemtype.equals("树型参照")) {
					sb_html.append(getBillTreeRefHtml(itemVO, false, str_divid));
				} else if (str_itemtype.equals("公式参照")) { //就是弹出一个窗口，有一堆按钮与列表可以快速生成公式语法返回
					sb_html.append(getFormulaRefHtml(itemVO, false, str_divid));
				} else if (str_itemtype.equals("自定义参照")) {
					sb_html.append(getCustRefHtml(itemVO, false, str_divid));
				} else {
					sb_html.append(getTextBoxHtml(itemVO, false));
				}

				//后来在每个控制下面都默认设置一个错误提示框,然后UI端提供函数可以设置错误提示,display:none
				int li_card_width = getItemWidth(itemVO, false); //
				sb_html.append("\r\n<span id=\"" + str_divid + "_CardItem_" + str_itemkey + "▲error\" style=\"display:none;width:" + li_card_width + "px;\"></span>\r\n"); //

				//该控件结束!
				sb_html.append("</span>");
			}
			sb_html.append("</div>\r\n"); //一行一个div
		}

		//最后加一片空白，保证万一最后一个是下拉框时能显示
		sb_html.append("<div style=\"width:100px;height:200px;padding-left:5px;padding-right:5px;" + str_border + "\"></div>\r\n");

		sb_html.append("</form>\r\n");

		//表头弹出菜单
		sb_html.append("<div id=\"" + str_divid + "_BillCardRightMenu\" class=\"easyui-menu\" style=\"width:120px;\">\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-cut'\" onclick=\"FreeUtil.onRightClickBillCard_0(" + str_divid + "_BillCard,this);\">清空字段数据</div>\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p17'\" onclick=\"FreeUtil.onRightClickBillCard_1(" + str_divid + "_BillCard,this);\">查看当前SQL</div>\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p34'\" onclick=\"FreeUtil.onRightClickBillCard_2(" + str_divid + "_BillCard,this);\">查看UI数据</div>\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p30'\" onclick=\"FreeUtil.onRightClickBillCard_3(" + str_divid + "_BillCard,this);\">查看DB数据</div>\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p28'\" onclick=\"FreeUtil.onRightClickBillCard_4(" + str_divid + "_BillCard,this);\">查看模板配置</div>\r\n");
		sb_html.append("  <div data-options=\"iconCls:'icon-p26'\" onclick=\"FreeUtil.onRightClickBillCard_5(" + str_divid + "_BillCard,this);\">查看本列模板</div>\r\n");
		sb_html.append("</div>\r\n");

		return sb_html.toString();
	}

	//错误提示,仿照这个样子
	private String getErrorMsg(String _itemkey, String _labelWidth, int _witdh) {
		String str_left = _labelWidth + "px"; //
		StringBuilder sb_msg = new StringBuilder(""); //
		if (_itemkey.equals("organization_code")) {
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题1:长度为17位代码</span>");
		} else if (_itemkey.equals("register_code")) {
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题1:大于等于3个字符或2个汉字</span>");
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题2:同一[授信类型]下[工商注册编号]不能重复</span>");
		} else if (_itemkey.equals("tab_name")) { //表名中文
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题1:大于等于3个字符或2个汉字</span>");
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题2:同一[授信类型]下[工商注册编号]不能重复</span>");
		} else if (_itemkey.equals("nationality_name")) {
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题1:对于同一个单一法人客户，同一笔授信下的客户代码均一致</span>");
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题2:同一个机构下只有一个法定代表人的校验</span>");
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题3:表1 PART IV中[发行日期]小于等于[数据日期]</span>");
		} else if (_itemkey.equals("templetcode")) {
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题1:对于同一个单一法人客户，同一笔授信下的客户代码均一致</span>");
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题2:同一个机构下只有一个法定代表人的校验</span>");
			sb_msg.append("<span style=\"display:block;width:" + _witdh + "px;margin-top:5px;margin-bottom:5px;padding-left:" + str_left + ";color:#FF4444\">问题3:表1 PART IV中[发行日期]小于等于[数据日期]</span>");
		}

		String str_msg = sb_msg.toString(); //
		if (str_msg.equals("")) {
			return "";
		} else {
			String str_all = "";
			str_all = str_all + "<span id=\"12345\" style=\"display:block;width:" + _witdh + "px;\">"; //border:1px solid blue
			str_all = str_all + str_msg;
			str_all = str_all + "</span>";
			return str_all;
		}

	}

	//Label控件,就是一个纯Html和帮助说明而已
	public String getLabelHtml(String _divid, HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_itemdefine = _itemVO.getStringValue("itemdefine");
		int li_card_width = getItemWidth(_itemVO, _isQuery);
		int li_card_height = getItemHeight(_itemVO, 28);
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery);

		StringBuilder sb_html = new StringBuilder(); //

		sb_html.append("<div id=\"" + str_itemkey + "\" style=\"display:block;width:" + li_card_width + "px;height:" + li_card_height + "px;padding-left:" + str_labelwidth + "px;\">");
		sb_html.append(str_itemdefine); //
		sb_html.append("</div>");

		return sb_html.toString();
	}

	//取得文本框的Html
	public String getTextBoxHtml(HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明
		int li_card_width = getItemWidth(_itemVO, _isQuery); //
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery);

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<input class=\"easyui-textbox\" "); //
		sb_html.append("id=\"" + str_itemkey + "\" "); //
		sb_html.append("name=\"" + str_itemkey + "\" "); //
		sb_html.append("label=\"" + str_itemname + "\" ");
		sb_html.append("labelAlign=\"right\" "); //

		if (str_labelwidth != null && !str_labelwidth.equals("")) {
			sb_html.append("labelWidth=" + str_labelwidth + " "); //
		}
		boolean isEditable = _itemVO.getBooleanValue("card_iseditable", true); //卡片是否可编辑
		if (!isEditable && !_isQuery) {
			sb_html.append("disabled=true "); //
		}

		sb_html.append("style=\"width:" + li_card_width + "px;\">\r\n"); //
		return sb_html.toString(); //
	}

	//数字框的Html
	public String getNumberBoxHtml(HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明
		int li_card_width = getItemWidth(_itemVO, _isQuery); //
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery);

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<input class=\"easyui-numberbox\" "); //类型是数字框
		sb_html.append("id=\"" + str_itemkey + "\" "); //
		sb_html.append("name=\"" + str_itemkey + "\" "); //
		sb_html.append("label=\"" + str_itemname + "\" ");
		sb_html.append("labelAlign=\"right\" "); //label的位置

		if (str_labelwidth != null && !str_labelwidth.equals("")) {
			sb_html.append("labelWidth=" + str_labelwidth + " "); //
		}

		//精度,非常关键,精度为2表示可以有小数位2位..
		HashMap<String, String> dfMap = getItemRealDefineMap(_itemVO, _isQuery); //控件定义
		if (dfMap.containsKey("精度")) {
			sb_html.append("precision=\"" + dfMap.get("精度") + "\" "); //
		}

		if ("Y".equalsIgnoreCase(dfMap.get("是否有千分位"))) {
			sb_html.append("groupSeparator=\",\" "); //
		}

		if ("Y".equalsIgnoreCase(dfMap.get("是否百分比"))) {
			sb_html.append("suffix=\"%\" "); //
		}

		boolean isEditable = _itemVO.getBooleanValue("card_iseditable", true); //卡片是否可编辑
		if (!isEditable && !_isQuery) {
			sb_html.append("disabled=true "); //
		}

		sb_html.append("style=\"width:" + li_card_width + "px\">\r\n"); //
		return sb_html.toString(); //
	}

	//多行文本框的Html
	public String getTextAreaHtml(HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明
		int li_card_width = getItemWidth(_itemVO, _isQuery);
		int li_card_height = getItemHeight(_itemVO, 70);
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery);

		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<input class=\"easyui-textbox\" ");
		sb_html.append("id=\"" + str_itemkey + "\" "); //
		sb_html.append("name=\"" + str_itemkey + "\" ");
		sb_html.append("label=\"" + str_itemname + "\" ");
		sb_html.append("labelAlign=\"right\" "); //文字靠右

		if (str_labelwidth != null && !str_labelwidth.equals("")) {
			sb_html.append("labelWidth=" + str_labelwidth + " "); //
		}

		//卡片是否可编辑
		boolean isEditable = _itemVO.getBooleanValue("card_iseditable", true);
		if (!isEditable && !_isQuery) {
			sb_html.append("disabled=true "); //
		}

		sb_html.append("multiline=\"true\" "); //多行

		//sb_html.append("data-options=\"icons:[{iconCls:'icon-search',iconHlign:'top',iconPosition:'top',handler: function(e){ alert('参照弹出窗口!'); }}]\" ");

		sb_html.append("style=\"width:" + li_card_width + "px;height:" + li_card_height + "px\">\r\n");
		return sb_html.toString(); //
	}

	//日历<input class="easyui-datebox" label="Start Date:" labelPosition="top" style="width:100%;">
	public String getDateHtml(HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明
		int li_card_width = getItemWidth(_itemVO, _isQuery);
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery);
		boolean isEditable = _itemVO.getBooleanValue("card_iseditable", true); //是否可编辑

		StringBuilder sb_html = new StringBuilder();
		sb_html.append("<input class=\"easyui-datebox\" "); //
		sb_html.append("id=\"" + str_itemkey + "\" "); //
		sb_html.append("name=\"" + str_itemkey + "\" ");
		sb_html.append("label=\"" + str_itemname + "\" ");
		sb_html.append("labelAlign=\"right\" "); //文字靠右
		sb_html.append("editable=\"false\" "); //文本框不可输入!只能选择

		//sb_html.append("labelPosition=\"right\" ");

		if (str_labelwidth != null && !str_labelwidth.equals("")) {
			sb_html.append("labelWidth=" + str_labelwidth + " "); //
		}

		//把控件定义解析成Map!
		HashMap<String, String> dfMap = getItemRealDefineMap(_itemVO, _isQuery); //控件定义

		//判断是否是8位日期..
		String str_formatFunc = ""; //解析函数!
		if ("Y".equalsIgnoreCase(dfMap.get("is8"))) {
			str_formatFunc = "FreeUtil.formatDate8";
		}else if("Y".equalsIgnoreCase(dfMap.get("is6"))) {
			str_formatFunc = "FreeUtil.formatDate6";
		}else {
			str_formatFunc = "FreeUtil.formatDate";
		}

		sb_html.append("data-options=\"formatter:" + str_formatFunc + ",parser:FreeUtil.parserDate\" ");
		sb_html.append("style=\"width:" + li_card_width + "px\""); //
		
		if (!isEditable && !_isQuery) {
			sb_html.append(" disabled=true "); //
		}

		if (_isQuery) {
			sb_html.append("editable=true "); //竟然说查询框是可编辑
		} else {
			sb_html.append("editable=false ");
		}
		
		sb_html.append("/>\r\n");
		
		return sb_html.toString(); //
	}

	//时间,就是精确到秒
	public String getDateTimeHtml(HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明
		int li_card_width = getItemWidth(_itemVO, _isQuery);
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery);

		StringBuilder sb_html = new StringBuilder();
		sb_html.append("<input class=\"easyui-datetimebox\" "); //时间类型
		sb_html.append("id=\"" + str_itemkey + "\" "); //
		sb_html.append("name=\"" + str_itemkey + "\" ");
		sb_html.append("label=\"" + str_itemname + "\" ");
		sb_html.append("labelAlign=\"right\" "); //文字靠右
		sb_html.append("editable=\"false\" "); //文本框不可输入!只能选择

		//sb_html.append("labelPosition=\"right\" ");

		if (str_labelwidth != null && !str_labelwidth.equals("")) {
			sb_html.append("labelWidth=" + str_labelwidth + " "); //
		}

		sb_html.append("data-options=\"formatter:FreeUtil.formatTime,parser:FreeUtil.parserDate\" ");

		sb_html.append("style=\"width:" + li_card_width + "px\">\r\n"); //

		return sb_html.toString(); //
	}

	//勾选框<input class="easyui-checkbox" name="fruit" value="Apple" label="Apple:">
	//勾选框以前使用的是class="easyui-checkbox",后来发现老是有各种问题,所以又在另一个例子中看见有一个直接使用checkbox搞的
	public String getCheckBoxHtml(HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明
		int li_card_width = getItemWidth(_itemVO, _isQuery);
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery); //
		int li_checkBoxWidth = li_card_width - Integer.parseInt(str_labelwidth); //
		boolean isEditable = _itemVO.getBooleanValue("card_iseditable", true); //是否可编辑

		StringBuilder sb_html = new StringBuilder();
		//		sb_html.append("<input class=\"easyui-checkbox\" "); //
		//		sb_html.append("name=\"" + str_itemkey + "\" ");
		//		sb_html.append("label=\"" + str_itemname + "\" ");
		//		sb_html.append("labelPosition=\"left\" ");
		//		sb_html.append("value=\"Y\"> ");
		//sb_html.append("style=\"width:" + li_card_width + "px\">\r\n"); //

		//sb_html.append("<div style=\"margin-bottom:5px\">");

		//style=\"line-height: 30px;\"
		//label中的for要对应另一个id为其值的文本框,点击这个label时,光标会自动跳转到文本框中(或相当于直接点击那个控件),这是<label>的特性!
		String str_label_style = "text-align:right;width: " + str_labelwidth + "px;height:15px;line-height:15px;"; //
		if (!isEditable && !_isQuery) {
			str_label_style = str_label_style + "opacity:0.6;"; //
		}

		sb_html.append("<label for=\"" + str_itemkey + "\" class=\"textbox-label\" style=\"" + str_label_style + "\">" + str_itemname + "</label>");
		sb_html.append("<input id=\"" + str_itemkey + "\" name=\"" + str_itemkey + "\" type=\"checkbox\"  value=\"true\" style=\"width:" + li_checkBoxWidth + "px;line-height: 15px;\"");
		//sb_html.append("</div>");
		//卡片是否可编辑

		if (!isEditable && !_isQuery) {
			sb_html.append("disabled=true "); //
		}
		sb_html.append("/>");
		return sb_html.toString(); //
	}

	//计算下拉框的Html,easyUI直接使用原生态的,这其实非常好!
	public String getComboBoxHtml(String _divid, HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_query_itemtype = _itemVO.getStringValue("query_itemtype");

		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明
		int li_card_width = getItemWidth(_itemVO, _isQuery); //控件宽度
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery); //label宽度

		//计算的字段名
		String str_field_itemdefine = "itemdefine";
		String str_field_iseditable = "card_iseditable";

		//查询框单独定义
		if (_isQuery && str_query_itemtype != null && !str_query_itemtype.equals("")) {
			str_field_itemdefine = "query_itemdefine";
			str_field_iseditable = "query_iseditable";
		}

		//循环输出各个项
		int li_rows = 1;
		StringBuilder sb_optionHtml = new StringBuilder();

		//根据定义查询
		String str_define = null; //
		str_define = _itemVO.getStringValue(str_field_itemdefine); //
		if (str_define != null && !str_define.trim().equals("")) {
			HashMap<String, String> comboxMap = getComboBoxData(str_define); //
			li_rows = comboxMap.size(); //
			for (Map.Entry<String, String> entry : comboxMap.entrySet()) {
				sb_optionHtml.append("<option value=\"" + entry.getKey() + "\">" + entry.getValue() + "</option>\r\n");
			}
		}

		//根据内容行计算弹出的那个窗口高度
		int li_height = (li_rows + 1) * 33 + 5; //
		if (li_height < 150) {
			li_height = 150;
		}
		if (li_height > 275) {
			li_height = 275;
		}

		//拼接整体
		StringBuilder sb_html = new StringBuilder();
		sb_html.append("<select class=\"easyui-combobox\" "); //
		sb_html.append("id=\"" + str_itemkey + "\" "); //
		sb_html.append("name=\"" + str_itemkey + "\" "); //
		sb_html.append("label=\"" + str_itemname + "\" ");
		sb_html.append("labelAlign=\"right\" ");

		if (str_labelwidth != null && !str_labelwidth.equals("")) {
			sb_html.append("labelWidth=" + str_labelwidth + " "); //
		}

		boolean isEditable = _itemVO.getBooleanValue(str_field_iseditable, true); //卡片是否可编辑
		if (!isEditable && !_isQuery) { //不是查询框，并且不能编辑的时候，整体禁用掉
			sb_html.append("disabled=true "); //
		}

		sb_html.append("panelHeight=\"" + li_height + "px\" "); //★★★下拉环框的高度,这个设成自动也有问题,应该计算

		if (!_isQuery) { // 若下拉框不是查询中内容，整体禁用编辑，只允许下拉
			sb_html.append("editable=false ");
		}

		sb_html.append("style=\"width:" + li_card_width + "px\">\r\n");

		//第一个永远为空!
		sb_html.append("<option value=\"\">&nbsp;</option>\r\n");
		sb_html.append(sb_optionHtml.toString()); //

		sb_html.append("</select>\r\n");

		return sb_html.toString();
	}

	//计算下拉框的Html,easyUI直接使用原生态的,这其实非常好!
	public String getComboBoxHtml_new(String str_divid, HashVO _itemVO, boolean _isQuery) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_query_itemtype = _itemVO.getStringValue("query_itemtype");

		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明
		int li_card_width = getItemWidth(_itemVO, _isQuery); //控件宽度
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery); //label宽度

		//计算的字段名
		String str_field_itemdefine = "itemdefine";
		String str_field_iseditable = "card_iseditable";

		//查询框单独定义
		if (_isQuery && str_query_itemtype != null && !str_query_itemtype.equals("")) {
			str_field_itemdefine = "query_itemdefine";
			str_field_iseditable = "query_iseditable";
		}

		//根据定义查询
		String str_define = _itemVO.getStringValue(str_field_itemdefine); //
		JSONArray jsy_data = getComboBoxDataAsJsonArray(str_define);

		//根据内容行计算弹出的那个窗口高度
		int li_rows = jsy_data.size(); //
		int li_height = (li_rows + 1) * 33 + 5; //
		if (li_height < 150) {
			li_height = 150;
		}
		if (li_height > 275) {
			li_height = 275;
		}

		//计算data数据
		String str_data_test = "[{value:'001',text:'张三'},{value:'002',text:'李四'},{value:'003',text:'王五'}]"; //
		String str_data = jsy_data.toJSONString(); //
		str_data = bsUtil.replaceAll(str_data, "\"", "'"); //
		str_data = bsUtil.replaceAll(str_data, "'value'", "value"); //
		str_data = bsUtil.replaceAll(str_data, "'text'", "text"); //
		System.out.println("数据什么样" + str_data + "end");

		//拼接整体
		StringBuilder sb_html = new StringBuilder();
		sb_html.append("<input id=\"" + str_itemkey + "\" name=\"" + str_itemkey + "\" class=\"easyui-combobox\" ");

		sb_html.append("label=\"" + str_itemname + "\" "); //label名称
		//sb_html.append("labelWidth=" + str_labelwidth + " "); //label宽度
		sb_html.append("labelAlign=\"right\" "); //靠右
		sb_html.append("panelHeight=\"" + li_height + "px\" "); //高度

		//数据格式!!
		sb_html.append("data-options=\"");
		sb_html.append("valueField: 'value',");
		sb_html.append("textField: 'text',");

		sb_html.append("data:" + str_data + ","); //

		//只有在卡片状态下才有事件监听
		if (!_isQuery) { //
			sb_html.append("onSelect : function(_record){\r\n");
			sb_html.append("  FreeUtil.onBillCardItemEditChange('" + str_divid + "','" + str_itemkey + "',_record);\r\n");
			//			sb_html.append(" console.log('pp' + _record); ");
			sb_html.append("},");
		}

		sb_html.append("method:'post'"); //
		sb_html.append("\" "); //

		sb_html.append("style=\"width:" + li_card_width + "px\" ");

		sb_html.append(">\r\n");

		System.out.println("下拉框定义【" + sb_html.toString() + "】");
		return sb_html.toString();
	}

	//列表参照(就是一个表格)
	public String getBillListRefHtml(HashVO _itemVO, boolean _isQuery, String _divid) {
		return getRefHtml(_itemVO, _isQuery, _divid, "FreeUtil.openBillListRefDialog");
	}

	//列表购物车参照,即左右可以加入购物车
	public String getBillListShopCartRefHtml(HashVO _itemVO, boolean _isQuery, String _divid) {
		return getRefHtml(_itemVO, _isQuery, _divid, "FreeUtil.openBillListShopCartDialog");
	}

	//树型参照
	public String getBillTreeRefHtml(HashVO _itemVO, boolean _isQuery, String _divid) {
		return getRefHtml(_itemVO, _isQuery, _divid, "FreeUtil.openBillTreeRefDialog");
	}

	//公式参照
	public String getFormulaRefHtml(HashVO _itemVO, boolean _isQuery, String _divid) {
		return getRefHtml(_itemVO, _isQuery, _divid, "FreeUtil.openFormulaRefDialog", "icon-search", true); //可以编辑
	}

	//自定义参照
	public String getCustRefHtml(HashVO _itemVO, boolean _isQuery, String _divid) {
		return getRefHtml(_itemVO, _isQuery, _divid, "FreeUtil.openCustRefDialog");
	}

	public String getRefHtml(HashVO _itemVO, boolean _isQuery, String _divid, String _functionName) {
		return getRefHtml(_itemVO, _isQuery, _divid, _functionName, "icon-search");
	}

	//默认参照
	public String getRefHtml(HashVO _itemVO, boolean _isQuery, String _divid, String _functionName, String _iconName) {
		return getRefHtml(_itemVO, _isQuery, _divid, _functionName, _iconName, false);
	}

	//所有的参照,其实都是同一个格式,只不过弹出窗口的函数名不一样,所以搞成一个函数!
	public String getRefHtml(HashVO _itemVO, boolean _isQuery, String _divid, String _functionName, String _iconName, boolean _isEditable) {
		String str_itemkey = _itemVO.getStringValue("itemkey");
		String str_itemname = getLabelText(_itemVO, _isQuery); //文本说明

		int li_card_width = getItemWidth(_itemVO, _isQuery);
		String str_labelwidth = getItemLabelWidth(_itemVO, _isQuery);

		int li_card_height = getItemHeight(_itemVO, 0);
		String str_iscomma = _itemVO.getStringValue("iscomma");
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<input class=\"easyui-textbox\" "); //
		sb_html.append("id=\"" + str_itemkey + "\" "); //
		sb_html.append("name=\"" + str_itemkey + "\" "); //
		sb_html.append("label=\"" + str_itemname + "\" ");
		sb_html.append("labelAlign=\"right\" "); //

		if (str_labelwidth != null && !str_labelwidth.equals("")) {
			sb_html.append("labelWidth=" + str_labelwidth + " "); //
		}

		if("FreeUtil.openCustRefDialog".equals(_functionName)) {
			sb_html.append("editable=\"true\" "); //自定义参照中的文本框允许编辑!
		}else {
			sb_html.append("editable=\"" + _isEditable + "\" "); //参照中的文本框不可编辑!
		}

		if (li_card_height > 0 && !_isQuery) {
			sb_html.append("multiline=\"true\" "); //卡片多行时,则是多行文本框
		}

		String str_billPanel = "";
		if (_isQuery) {
			str_billPanel = _divid + "_BillQuery";
		} else {
			str_billPanel = _divid + "_BillCard";
		}
		boolean iscomma = false;
		if ("Y".equals(str_iscomma)) {
			iscomma = true;
		}
		//一定要把参数带过去!!
		sb_html.append("data-options=\"icons:[{iconCls:'" + _iconName + "',handler: function(_event){ " + _functionName + "(" + str_billPanel + ",'" + str_itemkey + "',_event," + _isQuery + "," + iscomma + "); }}]\" ");

		if (li_card_height > 0 && !_isQuery) {
			sb_html.append("style=\"width:" + li_card_width + "px;height:" + li_card_height + "px\">\r\n"); //
		} else {
			sb_html.append("style=\"width:" + li_card_width + "px;\">\r\n"); //
		}
		return sb_html.toString(); //
	}

	//取得控件的实际定义,即如果是查询框,查询有定义则使用查询的,否则使用卡片的!卡片永远使用卡片的!
	//即查询可以覆盖卡片的！
	private HashMap<String, String> getItemRealDefineMap(HashVO _itemVO, boolean _isQuery) {
		String str_define = null; //
		if (_isQuery) {
			String str_define_query = _itemVO.getStringValue("query_itemdefine"); //先看查询是否有定义!
			if (str_define_query != null && !str_define_query.trim().equals("")) {
				str_define = str_define_query; // 
			} else {
				str_define = _itemVO.getStringValue("itemdefine"); //
			}
		} else {
			str_define = _itemVO.getStringValue("itemdefine"); //
		}

		return bsUtil.getDefineMap(str_define); //
	}

	//
	private String getLabelText(HashVO _itemVO, boolean _isQuery) {
		String str_itemname = _itemVO.getStringValue("itemname"); //
		//假如定义本身有<br>(表格列要显示),则在卡片中要去掉,否则也会换行看不见
		str_itemname = bsUtil.replaceAll(str_itemname, "<br>", "");
		if (_isQuery) {
			if (_itemVO.getBooleanValue("query_ismust", false)) { //查询框是否必输
				str_itemname = "<span style='color:red'>*</span>" + str_itemname + "";
			}
		} else {
			if (_itemVO.getBooleanValue("ismust", false)) { //卡片是否必输
				str_itemname = "<span style='color:red'>*</span>" + str_itemname + "";
			}
		}

		return str_itemname;
	}

	//得到某一行的宽度!
	public int getThisRowWidth(ArrayList<HashVO> _rowList, boolean _isQuery) {
		int li_allWidth = 0;
		for (int i = 0; i < _rowList.size(); i++) {
			HashVO hvo = _rowList.get(i); //
			li_allWidth = li_allWidth + getItemWidth(hvo, _isQuery) + 10; //
		}
		return li_allWidth;
	}

	//计算某一项宽度!
	public int getItemWidth(HashVO _hvo, boolean _isQuery) {
		try {
			String str_itemwidth = _hvo.getStringValue("card_width", "250"); //
			String str_querywidth = _hvo.getStringValue("query_width"); //查询框宽度!

			//如果是查询框，并且实际定义了查询宽度,则使用查询框宽度!
			if (_isQuery && str_querywidth != null && !str_querywidth.equals("")) {
				str_itemwidth = str_querywidth; //
			}

			//
			if (str_itemwidth.trim().equals("")) {
				str_itemwidth = "250";
			}

			int li_pos_1 = str_itemwidth.indexOf(","); //
			if (li_pos_1 > 0) {
				str_itemwidth = str_itemwidth.substring(li_pos_1 + 1, str_itemwidth.length()); //
			}

			int li_pos_2 = str_itemwidth.indexOf("*"); //
			if (li_pos_2 > 0) {
				str_itemwidth = str_itemwidth.substring(0, li_pos_2); //
			}
			return Integer.parseInt(str_itemwidth); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			return 250; //
		}
	}

	//
	private String getItemLabelWidth(HashVO _hvo, boolean _isQuery) {
		try {
			String str_itemwidth = _hvo.getStringValue("card_width"); //
			String str_querywidth = _hvo.getStringValue("query_width"); //查询框宽度!

			//如果是查询框，并且实际定义了查询宽度,则使用查询框宽度!
			if (_isQuery && str_querywidth != null && !str_querywidth.equals("")) {
				str_itemwidth = str_querywidth; //
			}

			if (str_itemwidth == null || str_itemwidth.equals("")) {
				return "85";
			}

			int li_pos = str_itemwidth.indexOf(","); //
			if (li_pos < 0) {
				return "85"; //
			}

			return str_itemwidth.substring(0, li_pos); //
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			return "85"; //
		}
	}

	//高度,多行文本框需要
	private int getItemHeight(HashVO _hvo, int _nvl) {
		try {
			String str_itemwidth = _hvo.getStringValue("card_width", ""); //
			if (str_itemwidth.trim().equals("")) {
				return _nvl;
			}

			int li_pos_2 = str_itemwidth.indexOf("*"); //
			if (li_pos_2 > 0) {
				str_itemwidth = str_itemwidth.substring(li_pos_2 + 1, str_itemwidth.length()); //
				return Integer.parseInt(str_itemwidth); //
			} else {
				return _nvl;
			}
		} catch (Exception _ex) {
			_ex.printStackTrace(); //
			return _nvl; //
		}
	}

	//根据下拉框的定义计算出下拉框的值,并生成一个Map，好几个地方都需要这个,在表单构造,查询框下拉框构建,表格查询数据。。
	//所以要单独封装一个public方法
	public HashMap<String, String> getComboBoxData(String str_define) {
		LinkedHashMap<String, String> rtMap = new LinkedHashMap<String, String>();
		if (str_define == null || str_define.trim().equals("")) {
			return rtMap; //
		}
		str_define = bsUtil.replaceAll(str_define, "\r", "");
		str_define = bsUtil.replaceAll(str_define, "\n", "");

		if (str_define.startsWith("【")) {
			str_define = str_define.substring(1, str_define.indexOf("】")); //
		}

		//解析成JSONObject,以后还是改与【key1=value1】【key2=value2】【key3=value3】的格式
		JSONObject json_itemdefine = JSONObject.parseObject(str_define);

		// 下拉选项：分为直接值、SQL等
		String str_optType_sel = (String) json_itemdefine.getString("类型");//
		String str_optVal_sel = (String) json_itemdefine.getString("内容");//
		if (str_optType_sel.equals("直接值")) {
			if (str_optVal_sel != null && !str_optVal_sel.trim().equals("")) { //内容不为空
				String[] allOpts_sel = bsUtil.split(str_optVal_sel, "|");// 分割所有下拉框选项
				for (int i = 0; i < allOpts_sel.length; i++) {
					String str_opt = allOpts_sel[i];// 每个下拉项"001/man"
					String[] perOpt_sel = bsUtil.split(str_opt, "/");// ["001","man"]
					String str_item_value = "";
					String str_item_text = ""; //
					if (perOpt_sel.length == 1) { // 如果没有分割!即value与text一样
						str_item_value = perOpt_sel[0];//
						str_item_text = perOpt_sel[0];//
					} else {
						str_item_value = perOpt_sel[0];//
						str_item_text = perOpt_sel[1];//
					}
					rtMap.put(str_item_value, str_item_text); //
				}
			}
		} else if (str_optType_sel.equals("SQL")) {
			if (str_optVal_sel != null && !str_optVal_sel.trim().equals("")) { //内容不为空
				try {
					// 对sql中关键字处理
					if (str_optVal_sel.indexOf(" name ") > 0) {
						str_optVal_sel = str_optVal_sel.replace(" name ", " \"name\" ");
					}
					HashVO[] hvs = bsUtil.getHashVOs(str_optVal_sel); ////从SQL语句取值
					if (hvs != null) {
						for (int i = 0; i < hvs.length; i++) {
							String str_val = hvs[i].getStringValue(0); //
							String str_text = hvs[i].getStringValue(1); //
							rtMap.put(str_val, str_text); //
						}
					}
				} catch (Exception _ex) { //把异常吃掉,防止因下拉框配置错误造成整个页面出不来!
					_ex.printStackTrace(); //
				}
			}
		}
		return rtMap; //
	}

	//返回JSONArray对象
	public JSONArray getComboBoxDataAsJsonArray(String str_define) {
		JSONArray jsy_data = new JSONArray();
		if (str_define == null || str_define.trim().equals("")) {
			return jsy_data; //
		}

		str_define = bsUtil.replaceAll(str_define, "\r", "");
		str_define = bsUtil.replaceAll(str_define, "\n", "");

		if (str_define.startsWith("【")) {
			str_define = str_define.substring(1, str_define.indexOf("】")); //
		}

		//解析成JSONObject,以后还是改与【key1=value1】【key2=value2】【key3=value3】的格式
		JSONObject json_itemdefine = JSONObject.parseObject(str_define); //

		// 下拉选项：分为直接值、SQL等
		String str_optType_sel = (String) json_itemdefine.getString("类型");//
		String str_optVal_sel = (String) json_itemdefine.getString("内容");//
		if (str_optType_sel.equals("直接值")) {
			if (str_optVal_sel != null && !str_optVal_sel.trim().equals("")) { //内容不为空
				String[] allOpts_sel = bsUtil.split(str_optVal_sel, "|");// 分割所有下拉框选项
				for (int i = 0; i < allOpts_sel.length; i++) {
					String str_opt = allOpts_sel[i];// 每个下拉项"001/man"
					String[] perOpt_sel = bsUtil.split(str_opt, "/");// ["001","man"]
					String str_item_value = "";
					String str_item_text = ""; //
					if (perOpt_sel.length == 1) { // 如果没有分割!即value与text一样
						str_item_value = perOpt_sel[0];//
						str_item_text = perOpt_sel[0];//
					} else {
						str_item_value = perOpt_sel[0];//
						str_item_text = perOpt_sel[1];//
					}

					JSONObject jso_rowData = new JSONObject(); //
					jso_rowData.put("value", str_item_value);
					jso_rowData.put("text", str_item_text);
					jsy_data.add(jso_rowData);
				}
			}
		} else if (str_optType_sel.equals("SQL")) {
			if (str_optVal_sel != null && !str_optVal_sel.trim().equals("")) { //内容不为空
				try {
					HashVO[] hvs = bsUtil.getHashVOs(str_optVal_sel); ////从SQL语句取值
					if (hvs != null) {
						for (int i = 0; i < hvs.length; i++) { //遍历各行数据
							String str_val = hvs[i].getStringValue(0); //
							String str_text = hvs[i].getStringValue(1); //

							JSONObject jso_rowData = new JSONObject(); //
							jso_rowData.put("value", str_val);
							jso_rowData.put("text", str_text);
							jsy_data.add(jso_rowData);
						}
					}
				} catch (Exception _ex) { //把异常吃掉,防止因下拉框配置错误造成整个页面出不来!
					_ex.printStackTrace(); //
				}
			}
		}
		return jsy_data; //
	}

	//把前端传入的参数替换模板中的参数,以前不是替换思路，而是在使用时才实际判断
	//后来感觉还是替换更好,因为前端有可能会用到这个，假如界面实际运行是不分页,但模板VO中取出来的是分行，就不一致了,也乱了!
	private void replaceTempletValue(HashVO parentVO, HashVO _listConfigVO, String _itemkey) {
		if (_listConfigVO.containsKey(_itemkey)) {
			parentVO.setAttributeValue(_itemkey, _listConfigVO.getStringValue(_itemkey)); //
		}
	}

	//创建表单的JS
	private String getJavaScript(String str_webcontext, String str_divid, HashVO parentVO, HashVO[] hvs_b, String _jsCode) {

		//卡片比较特殊的一个地方是,需要在控制下面显示警告提示框,而提示框需要知道该控件的宽度,直接从页面上计算太繁琐了,所以干脆塞在VO中
		for (int i = 0; i < hvs_b.length; i++) {
			int li_width = getItemWidth(hvs_b[i], false); //
			String str_labelWidth = getItemLabelWidth(hvs_b[i], false); //
			hvs_b[i].setAttributeValue("card_width_realallwidth", "" + li_width); //实现的整个宽度
			hvs_b[i].setAttributeValue("card_width_reallabelwidth", str_labelWidth); //实际的label宽度
		}

		StringBuilder sb_html = new StringBuilder();
		sb_html.append("try{\r\n");
		String str_templetOption = bsUtil.getTempletOptionSelf(parentVO, hvs_b); //
		sb_html.append("var jso_templet_" + str_divid + " = " + str_templetOption + ";\r\n"); //先输出整个模板的VO

		sb_html.append("var " + str_divid + "_BillCard = {};\r\n"); //创建查询对象!
		sb_html.append(str_divid + "_BillCard.divid=\"" + str_divid + "\";\r\n"); //
		sb_html.append(str_divid + "_BillCard.billtype= \"BillCard\";\r\n");
		sb_html.append(str_divid + "_BillCard.templetVO=jso_templet_" + str_divid + ";\r\n"); //

		//取得页面中的form表单,也传给
		sb_html.append("var " + str_divid + "_form = document.getElementById('" + str_divid + "_form');\r\n"); //
		sb_html.append(str_divid + "_BillCard.form=" + str_divid + "_form;\r\n"); //

		sb_html.append("}catch(_ex){\r\n");
		sb_html.append("console.log(_ex);\r\n");
		sb_html.append("}\r\n");

		sb_html.append("\r\n");

		//编辑事件相关函数
		if (_jsCode != null && !_jsCode.trim().equals("")) { //如果定义了js函数,才绑定编辑事件
			//先输出几个JS代码,比如保存前自定义校验,卡片编辑
			sb_html.append("\r\n");
			sb_html.append("//xml中定义的JS代码\r\n");
			sb_html.append(_jsCode);
		}

		return sb_html.toString();
	}

}
