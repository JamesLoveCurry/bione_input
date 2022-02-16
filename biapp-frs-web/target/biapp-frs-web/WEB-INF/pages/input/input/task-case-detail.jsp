<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid,  url, ids = [], dialog, buttons = [];
	var butLock = "0";
	var rowSize = 0;
	var dataZidian2;
	var orgInfo;
	var delArr = [];
	var paramStrOut = "";
	var _dialog ;
	var selection = [ {
		id : "",
		text : "请选择"
	} ];
	var libMap = {};
	$(function() {
	if("${orgId}"=="parent"){
		orgInfo=window.parent.orgInfo;
	}else{
		orgInfo="${orgId}"
	}
		$.ajax({
			async : false,
			url : '${ctx}/udip/library/libMapbyTempleId.json',
			dataType : 'json',
			type : "get",
			data : {
				"templeId" : "${templeId}",
				"caseId" : "${caseId}"
			},
			success : function(data2) {
				libMap = data2;
			}
		});
		var columns = [];
		if("${templeId}"){
			var templeId = "${templeId}";
			$.ajax({
				async : false,
				url : '${ctx}/udip/data/getColumnGridList/' + templeId,
				success : function(data) {
					var width = 0;
					for(var i =0;i<data.length;i++){
						
						if(data[i].columnComment != null ){
							width = data[i].columnComment.length * 30;
							if(data[i].dataZidian != null ){
							//设置字典的值
								columns.push({ display: data[i].columnComment, name: data[i].columnName, width : width,
									render : set(data[i].columnName,data[i].dataZidian)
								});
								
							}else{
								columns.push({ display: data[i].columnComment, name: data[i].columnName,width : width
									});
							}
							
						}else{
							width = data[i].columnName.length * 30;
							columns.push({ display: data[i].columnName, name: data[i].columnName,width : width
							});
						}
					}
					columns.push({ 
						name:"data_type",hide : "1",width:"1%"
					});
				}
			});
		}
		grid  = $("#maingrid").ligerGrid({
			columns : columns,
			checkbox : true,
			rownumbers : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			method : 'post',
			parms: [{
				name : 'templeId',
				value : '${templeId}'
			}, {
				name : 'caseId',
				value : "${caseId}"
			}, {
				name : 'searchTerms',
				value : "${searchTerms}"
			}, {
				name : 'sqlStr',
				value : "${sqlStr}"
			}, {
				name : 'orgId',
				value : orgInfo
			}],
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			url:"${ctx}/udip/taskcase/getTaskCaseInfoList",
			toolbar : {}
		});
		initButtons();	

	});
	function  set(col,lib){
		return function(row){
			return libMap[lib][row[col]];
		}
	}
	
	function initButtons() {
		
		btns = [{
			id : 'form_save',
			text : '导出',
			click : form_save,
			icon : 'export'
		}];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	
	//导出操作
	function form_save(){
	
		if (!document.getElementById('download')) {
				$('body').append(
						$('<iframe id="download" style="display:none"/>'));
			}
			$("#download").attr('src',
					'${ctx}/udip/taskcase/excelDownLoalTaskcaseInfo?templeId=' + '${templeId}'+'&caseId='+'${caseId}'+'&orgId='+'${orgId}'+'&sqlStr='+encodeURIComponent("${sqlStr}") );
	   
	   
	} 
	
</script>
</head>
<body>
	<div id="content" style="width: 100%; overflow: auto; clear: both;">
		<div id="template.center">
			<form id="mainsearch" action="${ctx}/udip/temple/tab1-save-update"
				method="post"></form>
		</div>
	</div>
</body>
</html>