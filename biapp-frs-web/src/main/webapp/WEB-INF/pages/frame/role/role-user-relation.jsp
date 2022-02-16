<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid,id,url;
	function initSearchForm() {

	};

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '用户名称',
				name : 'userName',
				width : "30%",
				align : 'center'
			},{
                display : '所属机构',
                name : 'orgNo',
                width : "30%",
                align : 'center'
            },{
                display : '所属部门',
                name : 'deptNo',
                width : "35%",
                align : 'center'
            }],
			width : '100%',
			height : '99%',
			isScroll : true,
			checkbox: false,
			dataAction : 'server',
			usePager : false,
			alternatingRow : true,
			colDraggable : true,
			delayLoad : true,
			url : "${ctx}/bione/admin/authUsrRel/roleUserRelation.json?id=${id}",
			checkbox : false,
			rownumbers : true
		});
		loadGrid();
	};
	$(function() {
		initSearchForm();
		initGrid();
        BIONE.validate("#formsearch");
	});

	function loadGrid(){
		grid.loadData();
	}

</script>
</head>
<body>
</body>
</html>