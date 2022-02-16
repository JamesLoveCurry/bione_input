<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">

	var dialog;
	var grid;
	
	$(function() {
		$("#search").ligerForm({
			width : "97%",
			fields : [ {
				display : "服务器名称", name : "serverName", newline : true, type : "text", cssClass : "field",
				attr : {
					op : "like",
					field : "server.serverNm"
				}
			}]
	    });
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	/* 初始化服务器 GRID */
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : "99%",
			height : "99%",
			columns : [ {
				display : '服务器名称', name : 'serverNm', align : 'center', minWidth : '', width : '',isSort: true
			}, {
				display : '适配器类型', name : 'adapterNm', align : 'center', minWidth : '', width : '',sortname:'adapterId',isSort: true
			}, {
				display : '服务器描述', name : 'serverDesc', align : 'center', minWidth : '', width : '',isSort: true
			} ],
			checkbox : false,
			usePager : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/rpt/rpt/rptmgr/server/list.json",
			onDblClickRow : dbclick,
			toolbar:{}
	    });
		var toolBars = [ {
			text : '选择',
			click : on_Select,
			icon : 'view'
		}];
		BIONE.loadToolbar(grid, toolBars, function() {});
	}
	
	function dbclick(data, rowindex, rowobj){
		window.parent.serverId=data.serverId;
		window.parent.tabChangeFlag=true;
		window.parent.tabObj.selectTabItem("dataset");
	}
	function on_Select(){
		var row=grid.getSelectedRow();
		if(row!=null){
			window.parent.serverId=row.serverId;
			window.parent.tabChangeFlag=true;
			window.parent.tabObj.selectTabItem("dataset");
		}
		return "";
	}
	
</script>
</head>
<body>
</body>
</html>