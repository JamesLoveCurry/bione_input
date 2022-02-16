<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript">
	var dataDate = '${dataDate}';
	var orgNo = '${orgNo}';
	var rptIndexNo = '${rptIndexNo}';
	var dataUnit = '${dataUnit}';
	var dataPrecision = '${dataPrecision}';
	var grid;
</script>
<script type="text/javascript">
	$(function() {
		initLastDataGrid();
	});

	//初始化grid
	function initLastDataGrid() {
		grid = $("#griddiv").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [{
				display : '本期',
				name : 'nowDate',
				width : "15%",
				align : 'center'
			}, {
				display : '上期',
				name : 'lastDate',
				width : "15%",
				align : 'center'
			},{
				display : '上上期',
				name : 'lastLastDate',
				width : "15%",
				align : 'center'
			},{
				display : '年初',
				name : 'thisYearFirst',
				width : "15%",
				align : 'center'
			},{
				display : '去年同期',
				name : 'lastYear',
				width : "15%",
				align : 'center'
			},],
			dataAction : 'server',
			url : "${ctx}/rpt/frs/rptfill/getIdxLastData2",
			parms : {
				dataDate : dataDate,
				orgNo : orgNo,
				rptIndexNo : rptIndexNo,
				dataUnit : dataUnit,
				dataPrecision : dataPrecision
			},
			type : "post",
			checkbox : false,
			rownumbers : true,
			usePager : false,
			isScroll : false,
			alternatingRow : true
		});
		grid.setHeight($("#center").height());
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="griddiv"></div>
	</div>
</body>
</html>