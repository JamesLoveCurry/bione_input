<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">
	//全局变量
	var grid;
	var dialog;
	$(function() {
	
		//初始化
		ligerSearchForm();//初始化查询表单
		ligerGrid();//初始化GRID
		initToolbar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮
	
		//调整布局
		$("div .searchtitle").hide();
	
		//渲染查询表单
		function ligerSearchForm() {
		    $("#search").ligerForm({
			fields : [ {
				display : "服务器名称", name : "serverName", newline : true, type : "text", cssClass : "field",
				attr : {
					op : "like",
					field : "serverNm"
				}
			} ]
		    });
		}
	
		//渲染GRID
		function ligerGrid() {
			  grid = $("#maingrid").ligerGrid({
				//InWindow : false,
				width : "100%",
				height : "98%",
				columns : [  {
					display : '服务器名称', name : 'serverNm', align : 'left', minWidth : '', width : '28%'
				}, {
					display : '服务器描述', name : 'serverDesc', align : 'center', minWidth : '', width : '68%'
				}  ],
				checkbox : false,
				userPager : true,
				rownumbers : true,
				alternatingRow : true,//附加奇偶行效果行
				colDraggable : true,
				dataAction : 'server',//从后台获取数据
				method : 'post',
				url : "${ctx}rpt/rpt/rptmgr/server/list.json",
				onDblClickRow : function(rowdata, rowindex, rowDomElement) {//双击选择
				/* 	var main = window.parent.frames['${frameName}'];
					main.$("[name='serverSelName']").val(rowdata.serverName);
					main.$("[name='serverId']").val(rowdata.serverId);
					BIONE.closeDialog("serverBox"); */
				},
				toolbar : {}
		    });
		}
		
		function initToolbar() {
			items = [ {
	 			text : '选择',
	 			click : function(){
	 				var rowdata=grid.getSelectedRow();
	 				if(!rowdata){
	 					BIONE.tip("请选择一条记录");
	 					return;
	 				}
	 				var main = window.parent.frames['${frameName}'];
					main.$("[name='serverSelName']").val(rowdata.serverName);
					main.$("[name='serverId']").val(rowdata.serverId);
					BIONE.closeDialog("serverBox");
	 			},
	 			icon : 'add'
	 		} ];
	 		BIONE.loadToolbar(grid, items, function() { });
		}
	});

</script>
<title>report-adapter-dialog</title>
</head>
<body>
</body>
</html>