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
			fields : [ {
				display : "数据源名称 ",
				name : "dsName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "dsName",
					op : "like"
				}
			}]
		});
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		
		
	});
	
	//初始化表格 
	function initGrid() {
		//alert("进入initDrid");
		grid = $("#maingrid").ligerGrid({
			//InWindow : false,
			width : '100%',
			columns : [ {
				display : '数据源名称',
				name : 'dsName',
				align : 'left',
				width : '10%'
			}, {
				display : '逻辑系统标识 ',
				name : 'logicSysNo',
				align : 'left',
				width : '10%'
			}, {
				display : '驱动类型',
				name : 'driverId',
				align : 'left',
				width : '10%'
			}, {
				display : '连接URL',
				name : 'connUrl',
				align : 'left',
				width : '25%'
			}, {
				display : '连接用户',
				name : 'connUser',
				align : 'left',
				width : '10%'
			}, {
				display : '描述',
				name : 'remark',
				align : 'left',
				width : '27%'
			} ],
			checkbox : false,
			isScroll : true,
			rownumbers : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/bione/mtool/datasource/list.json",
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			sortName : 'dsId',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
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
		window.parent.dsId=data.dsId;
		window.parent.tabChangeFlag=true;
		window.parent.tabObj.selectTabItem("impupload");
	}
	function on_Select(){
		var row=grid.getSelectedRow();
		if(row!=null){
			window.parent.dsId=row.dsId;
			window.parent.tabChangeFlag=true;
			window.parent.tabObj.selectTabItem("impupload");
		}
		return "";
	}
	
</script>
</head>
<body>
</body>
</html>