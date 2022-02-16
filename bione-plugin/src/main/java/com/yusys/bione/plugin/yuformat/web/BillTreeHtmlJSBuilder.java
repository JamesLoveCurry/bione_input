package com.yusys.bione.plugin.yuformat.web;

import com.yusys.bione.plugin.yuformat.utils.HashVO;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil;
import com.yusys.bione.plugin.yuformat.utils.YuFormatUtil2;
import org.apache.commons.lang3.StringUtils;

/**
 * 树型控件,支持好几种树的构造方式,比如标准的两个字段关联,一张表的某一个或几个字段的分组.
 * 两张表的关联，其中一张表本身是树型目录结构，另一张是表型结构.
 * @author xch
 *
 */
public class BillTreeHtmlJSBuilder {

	private YuFormatUtil bsUtil = new YuFormatUtil(); //
	private YuFormatUtil2 bsUtil2 = new YuFormatUtil2(); //

	//BillCard生成
	public HtmlJavaScriptVO getBillTreeHtmlAndJS(String _webcontext, String _divid, String _templetCode, String[] _buttons, HashVO _hvo_config) throws Exception {

		//★★★从定义的xml模板VO中计算出模板VO！
		Object[] templetVO = bsUtil2.getTempletVO(_templetCode); //
		HashVO parentVO = (HashVO) templetVO[0]; // 第一个主表VO
		HashVO[] hvs_b = (HashVO[]) templetVO[1]; // 第二个是子表VOs

		//把前端传入的参数替换模板中的参数,以前不是替换思路，而是在使用时才实际判断
		//后来感觉还是替换更好,因为前端有可能会用到这个，假如界面实际运行是不分页,但模板VO中取出来的是分行，就不一致了!

		replaceTempletValue(parentVO, _hvo_config, "autocondition"); //自动查询条件
		replaceTempletValue(parentVO, _hvo_config, "querycontion"); //查询条件
		replaceTempletValue(parentVO, _hvo_config, "autoquery"); //是否立即自动查询数据
		replaceTempletValue(parentVO, _hvo_config, "tree_isExpandAll"); //是否只展开到根结点(其实是收缩),默认是
		replaceTempletValue(parentVO, _hvo_config, "isCheckbox"); //是否是勾选框
		replaceTempletValue(parentVO, _hvo_config, "tree_isCheckboxCascade"); //是否勾选联动
		replaceTempletValue(parentVO, _hvo_config, "refwheresql");
		//先计算出Html
		String str_html = getHtml(_webcontext, _divid, parentVO, hvs_b, _buttons);

		//再计算出JavaScript
		String str_jstext = getJavaScript(_webcontext, _divid, parentVO, hvs_b);

		//拼装Html与JS
		HtmlJavaScriptVO htmlJsVO = new HtmlJavaScriptVO(); //
		htmlJsVO.setHtml(str_html); //
		htmlJsVO.setJavaScript(str_jstext); //
		return htmlJsVO;
	}

	//把前端传入的参数替换模板中的参数,以前不是替换思路，而是在使用时才实际判断
	//后来感觉还是替换更好,因为前端有可能会用到这个，假如界面实际运行是不分页,但模板VO中取出来的是分行，就不一致了,也乱了!
	private void replaceTempletValue(HashVO parentVO, HashVO _listConfigVO, String _itemkey) {
		if (_listConfigVO.containsKey(_itemkey)) {
			parentVO.setAttributeValue(_itemkey, _listConfigVO.getStringValue(_itemkey)); //
		}
	}

	//创建表单的Html
	private String getHtml(String str_webcontext, String str_divid, HashVO parentVO, HashVO[] hvs_b, String[] _buttons) {
		StringBuilder sb_html = new StringBuilder();
		if (_buttons != null) {
			sb_html.append("<div class=\"easyui-layout\" style=\"width:100%;height:100%;\">\r\n");
			sb_html.append("<div data-options=\"region:'south',border:false\" style=\"height:45px;text-align:center;padding-top:5px;overflow:hidden;\">\r\n");

			//输出各个按钮
			for (int i = 0; i < _buttons.length; i++) {
				String[] str_btnDefine = bsUtil.split(_buttons[i], "/");
				String str_btnText = str_btnDefine[0];
				String str_btnAction = str_btnDefine[1];
				String str_icon = null;
				if (str_btnDefine.length >= 3) {
					str_icon = str_btnDefine[2];
				}

				sb_html.append("<a id=\"" + str_divid + "_BillTreeBtn" + (i + 1) + "\"   href=\"JavaScript:" + str_btnAction + "();\" class=\"easyui-linkbutton\" ");
				if (str_icon != null) {
					sb_html.append("data-options=\"iconCls:'" + str_icon + "'\" "); //
				}
				sb_html.append("style=\"width:80px\">" + str_btnText + "</a>\r\n");
			}

			sb_html.append("</div>\r\n"); //end region:'south'

			//中间的内容
			sb_html.append("<div data-options=\"region:'center',border:false\">\r\n");

			//实际的树..
			sb_html.append("  <div id=\"" + str_divid + "_BillTree\" style=\"width:100%;height:100%;overflow:auto\">\r\n");
			sb_html.append("  <ul id=\"" + str_divid + "_tree\">\r\n"); //这才是树真正的地方!EasyUI是基于UL来构建树的
			sb_html.append("  </ul>\r\n");
			sb_html.append("  </div>\r\n");

			sb_html.append("</div>\r\n"); //end region:'center'
			sb_html.append("</div>\r\n"); //end easyui-layout
		} else {
			sb_html.append("<div id=\"" + str_divid + "_BillTree\" style=\"width:100%;height:100%;overflow:auto\">\r\n");
			sb_html.append("<ul id=\"" + str_divid + "_tree\">\r\n"); //这才是树真正的地方!EasyUI是基于UL来构建树的
			sb_html.append("</ul>\r\n");
			sb_html.append("</div>\r\n");
		}
		return sb_html.toString(); //
	}

	//创建表单的JS
	private String getJavaScript(String str_webcontext, String str_divid, HashVO parentVO, HashVO[] hvs_b) {
		boolean isAutoQuery = parentVO.getBooleanValue("autoquery", false); //是否立即查询数据
		boolean isExpandAll = parentVO.getBooleanValue("tree_isExpandAll", false); //是否只展开到根结点(其实是收缩),默认是
		boolean isCheckbox = parentVO.getBooleanValue("isCheckbox", false); //是否是勾选框
		boolean isCheckboxCascade = parentVO.getBooleanValue("tree_isCheckboxCascade", false); //是否勾选联动

		StringBuilder sb_html = new StringBuilder();
		sb_html.append("try{\r\n");

		String str_templetcode = parentVO.getStringValue("templetcode"); //模板编码
		String refWhereSql = parentVO.getStringValue("refwheresql");
		String autocondition = parentVO.getStringValue("autocondition");
		String querycontion = parentVO.getStringValue("querycontion");
		String str_templetOption = bsUtil.getTempletOptionSelf(parentVO, hvs_b); //
		sb_html.append("var jso_templet_" + str_divid + " = " + str_templetOption + ";\r\n");

		sb_html.append("var " + str_divid + "_BillTree = {};\r\n"); //创建查询对象!
		sb_html.append(str_divid + "_BillTree.divid=\"" + str_divid + "\";\r\n"); //
		sb_html.append(str_divid + "_BillTree.treeid=\"" + str_divid + "_tree\";\r\n"); //
		sb_html.append(str_divid + "_BillTree.templetVO=jso_templet_" + str_divid + ";\r\n"); //

		//先把树构建好,然后在前端加载异步数据,这样
		sb_html.append("$('#" + str_divid + "_tree').tree({\r\n");
		sb_html.append("  data:[{\r\n");
		sb_html.append("   id : \"-99999ROOT\", \r\n");
		sb_html.append("   text : \"加载数据...\" \r\n");
		sb_html.append("  }],\r\n");

		//如果是勾选框,则加上标记
		if (isCheckbox) {
			sb_html.append("  checkbox : true,\r\n");
			if (isCheckboxCascade) {
				sb_html.append("  cascadeCheck : true,\r\n");
			} else {
				sb_html.append("  cascadeCheck : false,\r\n");
			}
		}

		sb_html.append("  animate : false\r\n"); //展开时有动画效果,不能有,否则会闪两下,因为我们默认先构造了一个树,然后二次刷新数据的
		sb_html.append("});\r\n");

		sb_html.append("\r\n");

		//是否要立即查询数据?		
		if (isAutoQuery) {
			sb_html.append("//因为xml模板中定义了自动加载数据,所以立即异步加载数据\r\n");
			if (StringUtils.isNotEmpty(refWhereSql)) {
				sb_html.append("JSPFree.doClassMethodCall2(\"" + YuFormatUtil.CommDMOClassName + "\",\"getBillTreeDataOption\",{templetcode:\"" + str_templetcode + "\",refwheresql:\"" + refWhereSql + "\",autocondition:\"" + autocondition+ "\",querycontion:\"" + querycontion+ "\"},function(_rtData){\r\n");
			} else {
				sb_html.append("JSPFree.doClassMethodCall2(\"" + YuFormatUtil.CommDMOClassName + "\",\"getBillTreeDataOption\",{templetcode:\"" + str_templetcode + "\",autocondition:\"" + autocondition+ "\",querycontion:\"" + querycontion+ "\"},function(_rtData){\r\n");
			}
			sb_html.append("  $('#" + str_divid + "_tree').tree({ data : _rtData.TreeData });\r\n"); //加载树的数据!★★★关键逻辑★★★
			sb_html.append("  $('#" + str_divid + "_tree').tree({onBeforeCheck : function(node, param){return node.id == '-99999ROOT'?false:true;}});\r\n");
			//EasyUI默认是展开所有结点的,所以有时不想展开则收缩一下
			if (!isExpandAll) {
				sb_html.append("  $('#" + str_divid + "_tree').tree('collapseAll');\r\n");
				sb_html.append("  var rootNode = $('#" + str_divid + "_tree').tree('find', '-99999ROOT');\r\n");
				sb_html.append("  $('#" + str_divid + "_tree').tree('expand',rootNode.target);\r\n"); //展开根结点
			}
			sb_html.append("});\r\n");
		}

		sb_html.append("\r\n");
		sb_html.append("}catch(_ex){\r\n");
		sb_html.append("console.log(_ex);\r\n");
		sb_html.append("}\r\n");

		return sb_html.toString();
	}

}
