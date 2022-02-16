<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/datainput/UDIP.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var libData = {
		Rows : [  ]
	};
	$(function() {
		initGrid();
		var managers = $("#maingrid").ligerGetGridManager();
	});
	function initGrid() {
		grid = $("#maingrid")
				.ligerGrid(
						{
						height : '99%',
						width : '100%',
						columns : [ {
							display : "模板名称",
							name : "templeName",
							align : 'center',
							width : "40%"
						}, {
							display : "表名称",
							name : "tableEnName",
							align : 'center',
							width : "30%"
						}, {
							display : "字段名称",
							name : "orgColumn",
							align : 'center',
							width : "26%"
						} ],
						checkbox : false,
						usePager : false,
						frozen : false,
						isScroll : true,
						data : libData,
						colDraggable : true,
						dataAction : 'server',
						method : 'post',
						rownumbers : true,
						resizable : true,
						url : '${ctx}/rpt/input/library/getTempListById.json?dictId='
								+ "${id}",
						alternatingRow : true
					//附加奇偶行效果行
					});

	}
</script>
</head>
<body>
	<div id="content" style="width: 100%; overflow: auto; clear: both;">
		<div id="trigger.center"></div>
	</div>
</body>
</html>