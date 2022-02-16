<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script>
	var dataDate = "${dataDate}";
	var orgNo = "${orgNo}";
	var indexNo = "${indexNo}";
	var orgType = "${orgType}";
	var indexNm = "${indexNm}";
	var rptTemplateId = "${rptTemplateId}";
	var cellNo = "${cellNo}";
	
	function initForm(){
		var mainform = $("#mainform").ligerForm({
			fields:[{
				display:'校验状态',
	        	name:'validType',
	        	newline:true, 
	        	type:'text',
	        	attr:{
	        		readOnly: true
	        	}
			}]
		});
		if(window.parent.tmp.sumpartRs!=null){
			$("#validType").val(window.parent.tmp.sumpartRs);
			/* if(window.parent.tmp.sumpartRs!="未通过")
				$("#griddiv").hide(); */
		}
		else{
			$("#validType").val("未校验");
			$("#griddiv").hide();
		}
	}
	
	function initGrid(){
		var grid = $("#griddiv").ligerGrid(
				{
					height : '100%',
					width : '100%',
					columns : [ 
					//            {
					//	display : '指标编码',
					//	name : 'id.indexNo',
					//	width : "13.5%"
					//}, 
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
					url:"${ctx}/frs/verificationTotal/totalInfo.json?dataDate="+dataDate+"&orgNo="+orgNo+"&indexNo="+indexNo+"&orgType="+orgType+"&cellNo="+cellNo+"&rptTemplateId="+rptTemplateId,
					type : "post",
					checkbox:false,
					rownumbers : true,
					usePager : true,
					isScroll : false,
					alternatingRow : true
				});
		grid.setHeight($("#center").height()-20);
	}
	$(function() {
		initForm();
		initGrid();
	});
</script>
<title>总分校验</title>
</head>
<body>
	<div id="template.center">
		<form id="mainform"></form>
		<div id="griddiv"></div>
	</div>
</body>
</html>