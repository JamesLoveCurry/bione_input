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
				display : '表名',
				name : 'tabName',
				width : '80%',
				align : 'center'
			}],
			checkbox : false,
			isScroll : false,
			rownumbers : true,
			usePager : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/east/rules/business/tab/queryList",
			width : '100%',
			height : '100%',
			toolbar : {}, 
			onSelectRow : onSelectRow,
			onUnSelectRow : onUnSelectRow
		})
	};
	
	function onSelectRow(rowdata, rowid, rowobj) {
		window.parent.grid.set('parms', {
			tabName : rowdata.tabName
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