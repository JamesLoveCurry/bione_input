<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<script type="text/javascript">
var tabObj;
var tabChangeFlag=true;//tab是否可切换标志
var flag=true;//数据缓存标志

var jsondata;//grid加载的json数据
var type="${type}";
$(function(){
	init();
	addTab();
});
//渲染tab
function init(){
	tabObj=$("#tab").ligerTab({
		contextmenu : false,
		dblClickToClose : false,
		onBeforeSelectTabItem : function(tabID){
			return tabChangeFlag;
		} ,
		onAfterSelectTabItem : function(tabID){
			if("${type}"=="Report"){
				if(flag&&tabID=='dataset'&&$("#dataset").attr('src')==''){
					$("#dataset").attr({src:"${ctx}/rpt/frame/rptmgr/info/dataSet"});
					tabChangeFlag=false;
				}
			}
			if(flag&&tabID=='impupload'&&$("#impupload").attr('src')==''){
				$("#impupload").attr({src:"${ctx}/rpt/frame/rptmgr/info/impUpload"});
				tabChangeFlag=false;
			}
			if(flag&&tabID=='impgrid'&&$("#impgrid").attr('src')==''){
				$("#impgrid").attr({src:"${ctx}/rpt/frame/rptmgr/info/impGrid"});
				tabChangeFlag=false;
			}
		}

	});
}

function addTab(){
	window.flag=false;
	var height=$(document).height()-33;
	if("${type}"=="Report"){
		tabObj.addTabItem({
			tabid : 'server',
			text : '服务器选择',
			showClose : false,
			content : "<iframe id='server' name= 'server' src='' style='height:"+height+"px;'  frameborder='0'></iframe>"
		});
	}
	tabObj.addTabItem({
		tabid : 'dataset',
		text : '数据集选择',
		showClose : false,
		content : "<iframe id='dataset' name= 'dataset' src='' style='height:"+height+"px;'  frameborder='0'></iframe>"
	});
	tabObj.addTabItem({
		tabid : 'impupload',
		text : '上传文件',
		showClose : false,
		content : "<iframe id='impupload' name= 'impupload' src='' style='height:"+height+"px;'  frameborder='0'></iframe>"
	});
	tabObj.addTabItem({
		tabid : 'impgrid',
		text : '结果确认',
		showClose : false,
		content : "<iframe id='impgrid' name= 'impgrid' src='' style='height:"+height+"px;'  frameborder='0'></iframe>"
	});
	if("${type}"=="Report"){
		$("#server").attr({src:"${ctx}/rpt/frame/rptmgr/info/server"});
		tabObj.selectTabItem("server");
	}
	if("${type}"=="DataRel"){
		$("#dataset").attr({src:"${ctx}/rpt/frame/rptmgr/info/dataSet"});
		tabObj.selectTabItem("dataset");
	}
	tabChangeFlag=false;
	window.flag=true;
}
</script>
</head>
<body>
</body>
</html>