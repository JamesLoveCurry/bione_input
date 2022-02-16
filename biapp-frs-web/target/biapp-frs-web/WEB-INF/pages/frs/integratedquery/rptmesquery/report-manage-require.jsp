<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="decorator" content="/template/template14.jsp" />
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
	var expression;
	var constant;
	var reportRequireInfo="";
	var reportManage=window.parent;
	var view;
	var spread;
	$(function() {
		window.parent.reportManage.reportRequire=window;
		initData();
		if(parent.show==""){
			$("#treeToolbar").ligerTreeToolBar({
				items : [ {
					icon : 'import',
					text : '导入',
					click: importRequire
				}],
				treemenu : false
			}); 	
		}
		initView();
	});
	function initData(){
		reportRequireInfo=reportManage.reportInfo.reportRequireInfo;
	}
	
	function refreshView(json){
		reportRequireInfo=json;
		View.fromJSON(json);
	}
	function initView(){
		require.config({
			baseUrl : "${ctx}/plugin/js/",
			paths:{
				"view" : "show/views/rptview"
			}
		});
		
		require(["view"] , function(view){
			var settings = {
					targetHeight : ($("#center").height() - 2) ,
					ctx : "${ctx}" ,
					readOnly : true ,
					cellDetail:true,
					canUserEditFormula : false
			};
			initSlcanvas("${ctx}");
			spread = view.init($("#spread") , settings);
			view.fromJSON(reportRequireInfo);
		})
	}
	function getData(){
		return reportRequireInfo;
	}
	function importRequire(){
		parent.parent.selectedTab=window;
		parent.parent.BIONE.commonOpenDialog('需求模板上传','requireuploadWin',600,330,'${ctx}/frs/integratedquery/rptmesquery/info/requireUpload');
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="top">
			<div id="treeToolbar"
			style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
		</div>
		<div id="spread" style="width:99.5%;border-bottom:1px solid #D0D0D0;"></div>
		</div>
	</div>
</body>
</html>