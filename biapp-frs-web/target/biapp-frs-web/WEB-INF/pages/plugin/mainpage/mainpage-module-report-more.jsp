<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<script type="text/javascript">
var tab1flag = false;
var tabObj;
//初始化
$(function() {
	initTab();
});
function initTab() {
	var height = $(document).height() - 33;
	$("#tab").append('<div tabid="tab1" title="最近访问报表" />');
	tabObj = $("#tab").ligerTab({
		contextmenu : false,
		onBeforeSelectTabItem : function() {
			return true;
		},
		onAfterSelectTabItem :function(tabId){
			if(tabId=="tab1"&&!tab1flag){
				loadFrame("tab1", "${ctx}/rpt/frame/mainpage/showMoreHistoryRpt", "tab1frame");
				tab1flag=true;
			}
		}
	});
	tabObj.selectTabItem("tab1") ;
}

function loadFrame(tabId, src, id) {
	var height = $(document).height() - 33;
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
</script>
</head>
<body>
</body>
</html>