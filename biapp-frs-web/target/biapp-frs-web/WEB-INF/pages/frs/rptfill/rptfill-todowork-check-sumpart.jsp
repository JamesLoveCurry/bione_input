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
	var rptTmpId = '${rptTmpId}';
</script>
<script type="text/javascript">
	$(function() {
		initSumpartGrid();
	});
	
	//初始化总分校验grid
	function initSumpartGrid(){
		var grid = $("#griddiv").ligerGrid(
				{
					height : '100%',
					width : '100%',
					columns : [ 
					{
						display : '指标名称',
						name : 'indexNm',
						width : "15%"
					}, {
						display : '指标值',
						name : 'indexValStr',
						width : "15%"
					}, {
						display : '下级合计',
						name : 'lowerlevelTotalStr',
						width : "15%"
					}, {
						display : '差值',
						name : 'different',
						width : "15%"
					}, {
						display : '单位',
						name : 'unit',
						width : "15%"
					}, {
						display : '校验时间',
						name : 'checkTime',
						width : "20%",
						render:function(a,b,checkTime){
							if(checkTime == null){
								return "";
							}else{
								return BIONE.getFormatDate(checkTime,'yyyy-MM-dd hh:mm:ss');
							}
						}
					} ],
					dataAction : 'server',
					url:"${ctx}/frs/verificationTotal/totalInfo.json?",
					parms : {
						dataDate : dataDate,
						orgNo : orgNo,
						indexNo : rptIndexNo,
						rptTemplateId : rptTmpId
					},
					type : "post",
					checkbox:false,
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