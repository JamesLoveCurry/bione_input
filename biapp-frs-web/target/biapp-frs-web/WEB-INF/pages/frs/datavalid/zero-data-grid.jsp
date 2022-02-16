<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">


<script>
	var dataDate = "${dataDate}";
	var orgNo = "${orgNo}";
	var orgType = "${orgType}";
	var indexNo = "${indexNo}";
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
		
		if(window.parent.tmp.warnRs!=null){
			$("#validType").val(window.parent.tmp.zeroRs);
			/* if(window.parent.tmp.zeroRs!="未通过")
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
					url:"${ctx}/frs/verificationZero/zeroInfo.json?dataDate="+dataDate+"&orgNo="+orgNo+"&indexNo="+indexNo+"&orgType="+orgType+"&cellNo="+cellNo+"&rptTemplateId="+rptTemplateId,
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
<title>零值校验</title>
</head>
<body>
	<div id="template.center">
	<form id="mainform"></form>
		<div id="griddiv"></div>
	</div>
</body>
</html>