package com.yusys.bione.plugin.yuformat.web;

import java.io.FileOutputStream;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;

/**
 * 直接由JSP创建界面的工具
 * @author xch
 *
 */
public class FreeJSPBuilder {
	private YuFormatUtil bsUtil = new YuFormatUtil(); //
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

	//BillList
	public JSONObject getBillListHtmlAndJsByEasyUI(JSONObject _jso) throws Exception {
		try {
			String str_webcontext = _jso.getString("webcontext"); //
			String str_divid = _jso.getString("divid"); //
			String str_templetCode = _jso.getString("templetCode"); //模板编码
			JSONArray jsy_buttons = _jso.getJSONArray("buttons"); //模板编码

			String[] str_btns = converJsonArrayToStingArray(jsy_buttons); //

			//列表参数
			HashVO hvo_config = convertJSObjectToHashVO(_jso.getJSONObject("listConfig"));
			boolean isSwitchQuery = hvo_config.getBooleanValue("isSwitchQuery", true); //是否可以切换

			HtmlJavaScriptVO htmlJsVO = new BillListHtmlJSBuilder().getBillListHtmlAndJS(str_webcontext, str_divid, str_templetCode, str_btns, hvo_config, isSwitchQuery); //
			writeFile(htmlJsVO, str_divid + "_BillList.html");

			JSONObject rt_jso = new JSONObject(); //
			rt_jso.put("html", htmlJsVO.getHtml()); //
			rt_jso.put("jstext", htmlJsVO.getJavaScript()); //
			return rt_jso; //
		} catch (Exception _ex) {
			_ex.printStackTrace();
			JSONObject rt_jso = new JSONObject(); //
			rt_jso.put("html", bsUtil.getExceptionStringBuffer(_ex)); //
			rt_jso.put("jstext", ""); //
			return rt_jso; //
		}
	}

	//朋前端直接送入参数,生成一个最简单的表格!
	public JSONObject getBillListHtmlAndJsByEasyUIByItems(JSONObject _jso) throws Exception {
		String str_webcontext = _jso.getString("webcontext"); //
		String str_divid = _jso.getString("divid"); //
		String str_templetCode = _jso.getString("templetCode"); //模板编码
		JSONArray jsy_itemKeys = _jso.getJSONArray("itemKeys"); //
		JSONArray jsy_itemNames = _jso.getJSONArray("itemNames"); //
		JSONArray jsy_itemWidths = _jso.getJSONArray("itemWidths"); //

		String[] str_itemKeys = converJsonArrayToStingArray(jsy_itemKeys); //
		String[] str_itemNames = converJsonArrayToStingArray(jsy_itemNames); //
		String[] str_itemWidths = converJsonArrayToStingArray(jsy_itemWidths); //

		//列表参数
		HashVO hvo_config = convertJSObjectToHashVO(_jso.getJSONObject("listConfig"));

		HtmlJavaScriptVO htmlJsVO = new BillListHtmlJSBuilder().getBillListHtmlAndJsByEasyUIByItems(str_webcontext, str_divid, str_templetCode, str_itemKeys, str_itemNames, str_itemWidths, hvo_config); //
		writeFile(htmlJsVO, str_divid + "_BillList.html");

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("html", htmlJsVO.getHtml()); //
		rt_jso.put("jstext", htmlJsVO.getJavaScript()); //
		return rt_jso; //
	}

	//BillCard
	public JSONObject getBilCardHtmlAndJsByEasyUI(JSONObject _jso) throws Exception {
		String str_webcontext = _jso.getString("webcontext"); //
		String str_divid = _jso.getString("divid"); //
		String str_templetCode = _jso.getString("templetCode"); //模板编码
		JSONArray jsa_buttons = _jso.getJSONArray("buttons"); //模板编码
		String[] str_btns = null; //
		if (jsa_buttons != null) {
			str_btns = new String[jsa_buttons.size()];
			for (int i = 0; i < str_btns.length; i++) {
				str_btns[i] = jsa_buttons.getString(i); //
			}
		}

		//把客户端传过来的参数转换成HashVO
		HashVO hvo_config = convertJSObjectToHashVO(_jso.getJSONObject("cardConfig"));

		HtmlJavaScriptVO htmlJsVO = new BillCardHtmlJSBuilder().getBilCardHtmlAndJS(str_webcontext, str_divid, str_templetCode, str_btns, hvo_config);
		writeFile(htmlJsVO, str_divid + "_BillCard.html");

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("html", htmlJsVO.getHtml()); //
		rt_jso.put("jstext", htmlJsVO.getJavaScript()); //
		return rt_jso; //
	}

	//BillQuery
	public JSONObject getBillQueryHtmlAndJsByEasyUI(JSONObject _jso) throws Exception {
		String str_webcontext = _jso.getString("webcontext"); //
		String str_divid = _jso.getString("divid"); //
		String str_templetCode = _jso.getString("templetCode"); //模板编码

		HtmlJavaScriptVO htmlJsVO = new BillQueryHtmlJSBuilder().getBillQueryHtmlAndJS(str_webcontext, str_divid, str_templetCode); //
		writeFile(htmlJsVO, str_divid + "_BillQuery.html"); //

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("html", htmlJsVO.getHtml()); //
		rt_jso.put("jstext", htmlJsVO.getJavaScript()); //

		return rt_jso; //
	}

	//查询面板切换第二种查询条件的方法.
	//查询面板切换第二种比较特殊,只返回Html，而没有JS代码，因为js代码第一次已经生成了,如果再生次则非常乱!
	public JSONObject getBillQuerySwitch2Html(JSONObject _jso) throws Exception {
		String str_webcontext = _jso.getString("webcontext"); //
		String str_divid = _jso.getString("divid"); //
		String str_templetCode = _jso.getString("templetCode"); //模板编码
		int li_buildType = _jso.getIntValue("buildType"); //类型,1-常用查询，2-更多条件

		String[] str_html = new BillQueryHtmlJSBuilder().getBillQuerySwitch2Html(str_webcontext, str_divid, str_templetCode, li_buildType); //
		writeFileHtml(str_html[0], str_divid + "_BillQuery2.html"); //

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("html", str_html[0]); //
		rt_jso.put("domheight", str_html[1]); //

		return rt_jso; //
	}

	//自由灵活查询时
	public JSONObject getBillQueryFreeBuildHtml(JSONObject _jso) throws Exception {
		String str_webcontext = _jso.getString("webcontext"); //
		String str_divid = _jso.getString("divid"); //
		String str_templetCode = _jso.getString("templetCode"); //模板编码
		JSONArray jsy_items = _jso.getJSONArray("checkitems"); //勾选了哪些项
		String[] str_items = jsy_items.toArray(new String[0]); //直接转换成数组
		boolean isFirst = _jso.getBooleanValue("isFirst"); //

		//★★★从定义的xml模板VO中计算出模板VO！
		Object[] templetVO = bsUtil2.getTempletVO(str_templetCode); //
		HashVO parentVO = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		BillQueryHtmlJSBuilder queryBuilder = new BillQueryHtmlJSBuilder();
		String str_html = queryBuilder.getBillQueryFreeBuildHtml(str_webcontext, str_divid, parentVO, hvs_b, str_items); //

		String str_js = ""; //
		//第一次要输出JS的,否则点不开日历选择框!
		if (isFirst) {
			str_js = queryBuilder.getJavaScript(str_webcontext, str_divid, parentVO, hvs_b, false); //
		}

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("html", str_html); //
		rt_jso.put("jstext", str_js); //
		return rt_jso; //
	}

	//BillTree
	public JSONObject getBillTreeHtmlAndJsByEasyUI(JSONObject _jso) throws Exception {
		String str_webcontext = _jso.getString("webcontext"); //
		String str_divid = _jso.getString("divid"); //
		String str_templetCode = _jso.getString("templetCode"); //模板编码
		JSONArray jsa_buttons = _jso.getJSONArray("buttons"); //模板编码

		//所有按钮..
		String[] str_btns = null; //
		if (jsa_buttons != null) {
			str_btns = new String[jsa_buttons.size()];
			for (int i = 0; i < str_btns.length; i++) {
				str_btns[i] = jsa_buttons.getString(i); //
			}
		}

		//把客户端传过来的参数转换成HashVO
		HashVO hvo_config = convertJSObjectToHashVO(_jso.getJSONObject("treeConfig"));

		HtmlJavaScriptVO htmlJsVO = new BillTreeHtmlJSBuilder().getBillTreeHtmlAndJS(str_webcontext, str_divid, str_templetCode, str_btns, hvo_config);
		writeFile(htmlJsVO, str_divid + "_BillTree.html");

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("html", htmlJsVO.getHtml()); //
		rt_jso.put("jstext", htmlJsVO.getJavaScript()); //
		return rt_jso; //
	}

	//取得模板的数据
	public JSONObject getTempletXmlText(JSONObject _jso) throws Exception {
		String str_templetCode = _jso.getString("templetcode"); //模板编码
		String str_text = bsUtil2.getTemplelXmlText(str_templetCode); //

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("text", str_text); //

		return rt_jso; //
	}

	//取得模板的数据
	public JSONObject getTempletItemXmlText(JSONObject _jso) throws Exception {
		String str_templetCode = _jso.getString("templetcode"); //模板编码
		String str_itemkey = _jso.getString("itemkey"); //子项
		String str_text = bsUtil2.getTempletItemXmlText(str_templetCode, str_itemkey); //

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("text", str_text); //

		return rt_jso; //
	}

	//取得模板的数据
	public JSONObject getIsPOC(JSONObject _jso) throws Exception {
		String str_isPOC = System.getProperty("isPOC"); //
		if (str_isPOC == null) {
			str_isPOC = "N";
		}

		JSONObject rt_jso = new JSONObject(); //
		rt_jso.put("isPOC", str_isPOC); //

		return rt_jso; //
	}

	//取得模板的数据
	public JSONObject setIsPOC(JSONObject _jso) throws Exception {
		String str_isPOC = _jso.getString("isPOC"); //
		System.setProperty("isPOC", str_isPOC); //
		JSONObject rt_jso = new JSONObject(); //
		return rt_jso; //
	}

	//为了更容易跟踪与调试，直接把生成的html与JS代码写到一个文件中!这样就更透明的理解平台的原理与思想了。。
	private void writeFile(HtmlJavaScriptVO _htmlJSVO, String _filename) {
		try {
			String str_jsfile = System.getProperty("yxtempfile");
			if (str_jsfile != null) {
				YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

				StringBuilder sb_all = new StringBuilder(); //
				sb_all.append(getHtmlHeader());

				sb_all.append(_htmlJSVO.getHtml()); //html

				sb_all.append("\r\n"); //
				sb_all.append("</div>\r\n");

				sb_all.append("<script type=\"text/JavaScript\">\r\n");
				sb_all.append(_htmlJSVO.getJavaScript());
				sb_all.append("</script>\r\n");

				sb_all.append("\r\n"); //
				sb_all.append("</body>\r\n");
				sb_all.append("</html>\r\n");

				if(FilepathValidateUtils.validateFilepath(str_jsfile + "/" + _filename)) {
					bsUtil2.writeStrToOutputStream(new FileOutputStream(str_jsfile + "/" + _filename), sb_all.toString(), "UTF-8"); //
				}
			}
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}

	//只写html
	private void writeFileHtml(String _html, String _filename) {
		try {
			String str_jsfile = System.getProperty("yxtempfile");
			if (str_jsfile != null) {
				YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

				StringBuilder sb_all = new StringBuilder(); //
				sb_all.append(getHtmlHeader());
				sb_all.append(_html); //
				sb_all.append("</div>\r\n");
				sb_all.append("</body>\r\n");
				sb_all.append("</html>\r\n");

				bsUtil2.writeStrToOutputStream(new FileOutputStream(str_jsfile + "/" + _filename), sb_all.toString(), "UTF-8"); //
			}
		} catch (Exception _ex) {
			_ex.printStackTrace();
		}
	}

	//html的头
	private String getHtmlHeader() {
		StringBuilder sb_html = new StringBuilder(); //
		sb_html.append("<!DOCTYPE html>\r\n");
		sb_html.append("<html>\r\n");
		sb_html.append("<head>\r\n");
		sb_html.append("<meta charset=\"UTF-8\">\r\n");
		sb_html.append("<title></title>\r\n");
		sb_html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/biapp-frs-web/js/jqueryEasyUI/themes/default/easyui.css\">\r\n");
		sb_html.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"/biapp-frs-web/js/jqueryEasyUI/themes/icon.css\">\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"/biapp-frs-web/js/jqueryEasyUI/jquery.min.js\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"/biapp-frs-web/js/jqueryEasyUI/jquery.easyui.min.js\"></script>\r\n");

		sb_html.append("<script type=\"text/javascript\" src=\"/biapp-frs-web/frame/js/yuformat/freeutil.js\"></script>\r\n");
		sb_html.append("<script type=\"text/javascript\" src=\"/biapp-frs-web/frame/js/yuformat/jspfree.js\"></script>\r\n");
		sb_html.append("</head>\r\n");

		sb_html.append("<body style=\"overflow:hidden\">\r\n");
		sb_html.append("<div id=\"d1\" style=\"width:100%;height:600px;z-index:1\">\r\n");

		return sb_html.toString(); //

	}

	private String[] converJsonArrayToStingArray(JSONArray _jsy) {
		String[] str_items = new String[] {};
		if (_jsy != null) {
			str_items = new String[_jsy.size()];
			for (int i = 0; i < str_items.length; i++) {
				str_items[i] = _jsy.getString(i); //
			}
		}
		return str_items;
	}

	//把一个JSONObject转换成HashVO，树与表格控件，还可以从前端传入指定参数，覆盖XML配置中的参数
	//因为参数会越来越多,而且有的是boolean,有的是字符串,有的是数字,所以转换成HashVO，取起来方便,比如getBooleanValue()
	private HashVO convertJSObjectToHashVO(JSONObject _jso) {
		HashVO hvo = new HashVO(); //
		if (_jso != null) {
			String[] str_keys = _jso.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				hvo.setAttributeValue(str_keys[i], _jso.getString(str_keys[i])); //
			}
		}
		return hvo; //
	}

}
