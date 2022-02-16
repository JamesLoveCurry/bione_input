<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript">
var tabObj;
var tabChangeFlag = true;	// tab是否可切换标志
var flag = true;			// 数据缓存标志
var logFlag = false;//日志查询标志
var uuid = generateUUID();
var queryInterva = 1;
$(function() {
	init();
	addTab();
});
// 渲染tab
function init() {
	tabObj = $("#tab").ligerTab({
		contextmenu : false,
		dblClickToClose : false,
		onBeforeSelectTabItem : function(tabID) {
			return tabChangeFlag;
		}
	});
}
function addTab() {
	window.flag = false;
	var height = $(document).height() - 33;
	tabObj.addTabItem({
		tabid : 'checkoption',
		text : '检查选项',
		showClose : false,
		content : "<iframe id='checkoption' name='checkoption' src='${ctx}/report/frame/design/cfg/importDesignInfosCheckOption' style='height:"+height+"px;' frameborder='0'></iframe>"
	});
	tabObj.addTabItem({
		tabid : 'impupload',
		text : '上传文件',
		showClose : false,
		content : "<iframe id='impupload' name='impupload' src='' style='height:"+height+"px;' frameborder='0'></iframe>"
	});
	var type = "${type}";
	if (type == "new") {
		tabObj.addTabItem({
			tabid: 'log',
			text: '日志显示',
			showClose: false,
			content: "<iframe id='checkoption' name='checkoption' src='${ctx}/report/frame/design/cfg/importDesignInfosUploadLog' style='height:" + height + "px;' frameborder='0'></iframe>"
		});
	}
	tabObj.addTabItem({
		tabid : 'impgrid',
		text : '结果确认',
		showClose : false,
		content : "<iframe id='impgrid' name='impgrid' src='' style='height:"+height+"px;' frameborder='0'></iframe>"
	});
	tabObj.selectTabItem("checkoption");
	tabChangeFlag = false;
	window.flag = true;
}
function setCheckOption(emptyCellInFormula, emptyIndexInFormula, emptyCellInVerifyWarn, fromSystemRptCfg) {
	var url = "${ctx}/report/frame/design/cfg/importDesignInfosUpload?q=";
	if (emptyCellInFormula) {
		url += "&emptyCellInFormula=1";
	}
	if (emptyIndexInFormula) {
		url += "&emptyIndexInFormula=1";
	}
	if (emptyCellInVerifyWarn) {
		url += "&emptyCellInVerifyWarn=1";
	}
	if (fromSystemRptCfg) {
		url += "&fromSystemRptCfg=1";
	}
	url += "&type=${type}";
	$("#impupload").attr({ src : url });
	tabChangeFlag = true;
	tabObj.selectTabItem("impupload");
	tabChangeFlag = false;
}
function displayResult(result) {
	logFlag = false;
	var html = '<html><head><style type="text/css">';
	html += 'body {font-family: "微软雅黑", "宋体", Arial, sans-serif;font-size: 12px;}';
	html += '</style></head><body>' + result + '</body></html>';
	var doc = document.getElementById("impgrid").contentDocument || document.frames["impgrid"].document;
	doc.body.innerHTML = html;
	tabChangeFlag = true;
	tabObj.selectTabItem("impgrid");
	tabChangeFlag = false;
}
function changeLogTab() {
	tabChangeFlag = true;
	tabObj.selectTabItem("log");
	tabChangeFlag = false;
	logFlag = true
}
function generateUUID() {
	/* var d = new Date().getTime();
	if (window.performance && typeof window.performance.now === "function") {
		d += performance.now(); //use high-precision timer if available
	}
	var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
		var r = (d + Crypto.getRandomValues() * 16) % 16 | 0;
		d = Math.floor(d / 16);
		return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
	}); */
	var uuid = window.uuid.v1();
	return uuid.replace(/-/g,"");
}
</script>
</head>
<body>
</body>
</html>