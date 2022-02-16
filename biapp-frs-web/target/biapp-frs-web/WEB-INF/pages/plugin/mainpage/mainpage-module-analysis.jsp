<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.yusys.bione.comp.utils.PropertiesUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/mainpage/example.css" />
<style >
	body,html{
		height:100%;
	}
</style>
<script type="text/javascript">
	var tabFlag = new Array();

	<%
	PropertiesUtils pro = new PropertiesUtils("bione-plugin/extension/mainpage-index-analysis.properties");
	int tabCount = pro.getInteger("tabCount", 0);
	%>
	
	function initTab() {
		<%
		for (int tabNo = 1; tabNo <= tabCount; tabNo ++) {
		%>
		$("#info").append('<div tabid="tab<%=tabNo%>" title="<%=pro.getProperty("tabName" + tabNo, "").trim()%>" />');
		<%
		}
		%>
		var tabObj = $("#info").ligerTab({
			contextmenu : false,
			onBeforeSelectTabItem : function() {
				return true;
			},
			onAfterSelectTabItem :function(tabId) {
				for (var tabNo = 1; tabNo <= <%=tabCount%>; tabNo ++) {
					if(tabId == ("tab" + tabNo) && ! tabFlag[tabNo - 1]) {
						loadFrame(tabId, "${ctx}/rpt/frame/mainpage/analysis/frame?tabNo=" + tabNo, "tabFrame" + tabNo);
						tabFlag[tabNo - 1] = true;
					}
				}
			}
		});
		tabObj.selectTabItem("tab1");
	}

	function loadFrame(tabId, src, id) {
		var height = $("#info").height();
		if ($('#' + id).attr('src')) {
			return;
		}
		var frame = $('<iframe/>');
		frame.attr({
			id : id,
			frameborder : 0,
			src : src
		}).css("height", height);
		$('div[tabId=' + tabId + ']').append(frame);
	}

	$(function() {
		var contentHeight = $(document).height();
		$("#hisrpt_inbox").height(contentHeight - 2);
		$("#info").height(contentHeight - $(".in_box_titbg").height());
		initTab();
	});
</script>
</head>
<body>
	<div id="hisrpt_inbox" class="in_box">
		<div class="in_box_titbg">
			  <div class="in_box_tit"><span class="icon">指标分析</span></div>
		</div>
		<div id="info" class="in_box_con" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>