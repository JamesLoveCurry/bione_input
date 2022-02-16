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
	var type = "${type}";
	var rptId = "${rptId}";
	var cellNo = "${cellNo}";
</script>
<script type="text/javascript">
	$(function() {
		initGrid();
	});

	//初始化逻辑校验grid
	function initGrid() {
		var grid = $("#griddiv").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [ {
				display : '单元格编号',
				name : 'cellNo',
				width : "10%",
				align : 'center'
			}, {
				display : '机构名称',
				name : 'orgNm',
				width : "15%",
				align : 'center'
			}, {
				display : '批注内容',
				name : 'operInfo',
				width : "45%",
				align : 'center',
				render : function(row, e, val) {
					return val;
				}
			}, {
				display : '填写时间',
				name : 'operTime',
				width : "15%",
				align : 'center',
				type : "date",
				format : 'yyyyMMdd'
			}, {
				display : '操作人',
				name : 'userNm',
				width : "10%",
				align : 'center'
			} ],
			dataAction : 'server',
			url : "${ctx}/rpt/frs/rptfill/iptRemarkList.json",
			parms : {
				dataDate : dataDate,
				orgNo : orgNo,
				indexNo : rptIndexNo,
				type : type,
				rptId : rptId,
				cellNo : cellNo
			},
			type : "post",
			checkbox : false,
			rownumbers : true,
			usePager : true,
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