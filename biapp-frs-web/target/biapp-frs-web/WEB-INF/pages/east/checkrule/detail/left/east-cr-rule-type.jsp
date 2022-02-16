<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template8.jsp">
<script type="text/javascript">
	var grid;

    $(function() {
        initGrid();
    });
    
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '规则类型',
				name : 'typeName',
				width : '80%',
				align : 'center'
			}],
			checkbox : false,
			isScroll : false,
			rownumbers : true,
			usePager : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/east/rules/checkRule/type/queryList",
			width : '100%',
			height : '100%',
			sortName : 'typeId',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			toolbar : {}, 
			onSelectRow : onSelectRow,
			onUnSelectRow : onUnSelectRow
		})
	};
	
	function onSelectRow(rowdata, rowid, rowobj) {
		window.parent.grid.set('parms', {
			typeCd : rowdata.typeName
		});
		window.parent.grid.loadData(); 
	}
	
	function onUnSelectRow(rowdata, rowid, rowobj) {
		window.parent.grid.set('parms', {
		});
		window.parent.grid.loadData(); 
	}
    
</script>
</head>
<body>
</body>
</html>