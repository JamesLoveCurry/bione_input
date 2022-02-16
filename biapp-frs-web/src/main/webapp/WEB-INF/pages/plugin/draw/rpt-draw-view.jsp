<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">

	var grid;
	var rptId = "${rptId}";
	$(function() {
		var centerHeight = $("#center").height();
		initGrid();
		if (grid) {
			grid.setHeight(centerHeight - 8);
		}
	});
	//初始化表格 
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : '100%',
			columns : [{
				display : '翻牌日期',
				name : 'drawDate',
				width : '30%'
			}, {
				display : '用户名称 ',
				name : 'userName',
				sortname : 'usr.USER_NAME',
				width : '32%',
				render : function(row,ele,val){
					if(row.userId == null || row.userId == ""){
						return "自动翻牌";
					}
					if(row.userName == null || row.userName == ""){
						return "未知用户";
					}
					return val;
				}
			},{
				display : '翻牌时间',
				name : 'drawTime',
				width : '34%',
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss'
			}],
			checkbox : false,
			isScroll : true,
			rownumbers : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/rpt/rpt/draw/viewlist.json?rptId="+rptId,
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			pageParmName :  'page',
			pagesizeParmName : 'pagesize'
		});
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