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
		initZeroGrid();
	});

	//初始化零值校验grid
	function initZeroGrid(){
		var grid = $("#griddiv").ligerGrid(
				{
					height : '100%',
					width : '100%',
					columns : [
					{
						display : '指标名称',
						name : 'indexNm',
						width : "25%"
					}, {
						display : '校验信息',
						name : 'resultType',
						width : "25%",
						render : function(a,b,c){
							if(c == "1"){
								return "当期值为空";
							}
							if(c == "-1"){
								return "上期值为空";
							}
							if(c == "0"){
								return "当期上期值均为空";
							}
							if(c == "2"){
								return "本期值为空";
							}
						}
					}, {
						display : '校验时间',
						name : 'checkTime',
						width : "40%",
						render:function(a,b,checkTime){
							if(checkTime == null){
								return "";
							}else{
								return BIONE.getFormatDate(checkTime,'yyyy-MM-dd hh:mm:ss');
							}
						}
					} ],
					dataAction : 'server',
					url:"${ctx}/frs/verificationZero/zeroInfo.json?",
					parms : {
						dataDate : dataDate,
						orgNo : orgNo,
						indexNo : rptIndexNo,
						rptTemplateId : rptTmpId
					},
					type : "post",
					checkbox:false,
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