<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">
<script type="text/javascript">
	var grid=null;
	var columns= [ {
		display : '账户',
		name : 'name',
		width : "70%"
	},{
		display : '值',
		name : 'value',
		width : "20%"
	}];
	
	$(function() {
		initGrid();
	});
	
	function initGrid(){
		var url="${ctx}/report/frame/idx/idxanalysis/idxDetailList.json?id=${id}";
		
		grid = $("#maingrid").ligerGrid({
				columns :columns,
				checkbox: false,
				dataAction : 'server', //从后台获取数据
				usePager : false, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				url : url,
				rownumbers : true,
				width : '100%',
				parms :{
					searchArgs : JSON2.stringify(window.parent.cursearchArgs)
				}
		});
	}
	
	
</script>
</head>
<body>

</body>
</html>