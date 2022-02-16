<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<style type="text/css">
	body{
		
	 	font-family: "微软雅黑", "宋体", Arial, sans-serif; 
		font-size: 12px;
		padding-right: 0px; 
		padding-left: 0px;
		padding-top: 0px; 
	}
</style>
<script type="text/javascript">
var tab1flag = false;
var tab2flag = false;
var tabObj;
//初始化
$(function() {
	initTab();
});
function initTab() {
	var height = $(document).height() - 33;
	$("#tab").append('<div tabid="tab1" title="报表收藏" />');
	$("#tab").append('<div tabid="tab2" title="指标收藏" />');
	tabObj = $("#tab").ligerTab({
		contextmenu : false,
		onBeforeSelectTabItem : function() {
			return true;
		},
		onAfterSelectTabItem :function(tabId){
			if(tabId=="tab1"&&!tab1flag){
				loadFrame("tab1", "${ctx}/rpt/frame/mainpage/myfav/rptTab", "tab1frame");
				tab1flag=true;
			}
			if(tabId=="tab2"&&!tab2flag){
				loadFrame("tab2", "${ctx}/rpt/frame/mainpage/myfav/idxTab", "tab2frame");
				tab2flag=true;
			}
		}
	});
	tabObj.selectTabItem("tab1") ;
}

function loadFrame(tabId, src, id) {
	var height = $("#center").height()-27;
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