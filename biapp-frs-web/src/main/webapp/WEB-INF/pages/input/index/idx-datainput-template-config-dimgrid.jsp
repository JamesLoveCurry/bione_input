<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style>
.checklabel{
	color: blue;
	
	cursor: pointer;
}
</style>
<script type="text/javascript">
	var grid;
	$(function(){
		initGrid();
		//ininBtn();
		grid.setHeight($("#center").height()-10);
		window.parent.gridInfo=window;
	});
	function refreshData(){
		grid.set("data",{
			Rows: parent.dimFilterInfo
		});
	}
	function ininBtn(){
		//初始化按钮
		var btns = [{
			text : '取消',
			onclick : function() {
				parent.BIONE.closeDialog("dimFilterWin");
			}
		},{
			text : '确定',
			onclick : function() {
				window.parent.save();
			}
		}];
		BIONE.addFormButtons(btns);
	}
	
	
	function initGrid(){
		grid = $("#maingrid").ligerGrid({
			width : '99%',
			height: '99%',
			columns : [ {
				display : '维度类型',
				name : 'dimNm',
				width : "20%"
			}, {
				display : '维度过滤信息',
				name : 'dimFilterInfo',
				width : "70%",
				render: function(a,b,c){
					return (a.filterMode=="01"?"包含":"不包含") +" ("+a.filterText+")";
				}
			}],
			checkbox : false,
			data: null,
			usePager : false,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			dataAction : 'loaction',//从后台获取数据
			usePager : false
		});
		var rows=parent.dimFilterInfo;
		grid.set("data",{
			Rows: rows
		});
		$(".l-grid-header").hide();
	}
	
</script>
</head>
<body>
<div id="template.center">
	<div id="maingrid">
	</div>
</div>
</body>
</html>