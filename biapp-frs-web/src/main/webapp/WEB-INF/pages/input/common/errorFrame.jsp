<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">

$("#maingrid").width($("#center").width() * 0.99);
$("#maingrid").height($("#center").height() * 0.99);
var data = {
		"Rows" : parent.Rows
	};
$(function(){
	var grid = $("#maingrid").ligerGrid({
		//InWindow : false,
		height : $("#center").height() * 0.99,
// 		width : $("#center").width() * 0.99,
		columns : [ {
			display : '信息标题',
			name : 'errorPosi',
			align : 'left',
			width : '30%'
		}, {
			display : '信息内容',
			name : 'errorMsg',
			align : 'left',
			width : '60%'
		}],
		isScroll : true,
		rownumbers : true,
		dataAction : 'local',//从后台获取数据
		usePager : true, //服务器分页
		alternatingRow : true //附加奇偶行效果行
	});
	grid.loadData(data);
	BIONE.addFormButtons({
		text : '关闭',
		onclick : function(){
			BIONE.closeDialog("errorFrame");
		}
	});
});



</script>

<title>错误信息</title>
</head>
<body>
<div id="template.center">
	<div id="maingrid"></div>
</div>s
</body>
</html>