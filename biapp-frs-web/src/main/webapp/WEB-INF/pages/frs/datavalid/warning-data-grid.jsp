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
	var tmpStr = "";
	
	function initForm(){
		var mainform = $("#mainform").ligerForm({
			fields:[{
				display:'校验状态',
	        	name:'validType',
	        	newline:true,
	        	type:'hidden',
	        	attr:{
	        		readOnly: true
	        	}
			}]
		});

		if(window.parent.tmp && window.parent.tmp.warnRs!=null){
			$("#validType").val(window.parent.tmp.warnRs);
		} else {
			$("#validType").val("未校验");
			// $("#griddiv").hide();
		}
		
	}
	function initGrid(){
		var grid = $("#griddiv")
		.ligerGrid(
				{
					height : '100%',
					width : '100%',
					columns : [ {
						display : '指标单元格',
						name : 'indexNm',
						width : "10%"
					}, {
						display : '预警类型',
						name : 'compareType',
						width : "8%",
						render : function(data) {
							if (data.compareType == "01"){
								return "环比";
							}else if (data.compareType == "02"){
								return "同比";
							}else if (data.compareType == "03"){
								return "较上日";
							}else if (data.compareType == "04"){
								return "较月初";
							}else if (data.compareType == "05"){
								return "较上月末";
							}else if (data.compareType == "06"){
								return "较季初";
							}else if (data.compareType == "07"){
								return "较上季末";
							}else if (data.compareType == "08"){
								return "较年初";
							}else if (data.compareType == "09"){
								return "较上年末";
							}	
						}
					}, {
						display : '监管要求',
						name : 'isFrs',
						width : "8%",
						render : function(data) {
							if (data.isFrs == "01"){
								return "是";
							}else if (data.isFrs == "02"){
								return "否";
							}	
						}
					}, {
						display : '本期数值',
						name : 'currentVal',
						width : "10%"
					}, {
						display : '比较数值',
						name : 'comparisonValue',
						width : "10%"
					}, {
						display : '相差数值',
						name : 'minusValue',
						width : "10%"
					}, {
						display : '相差比率',
						name : 'differenceVal',
						width : "8%"
					}, {
						display : '阈值范围',
						name : 'alertValue',
						width : "20%"
					}, {
						display : '历史最大值',
						name : 'maxValue',
						width : "10%"
					}, {
						display : '历史最小值',
						name : 'minValue',
						width : "10%"
					}, {
						display : '历史平均值',
						name : 'averageValue',
						width : "10%"
					}, {
						display : '单位',
						name : 'units',
						width : "8%"
					},{
						display: '校验未通过说明',
						name: 'validDesc',
						width: "19%",
						align: 'left',
						isSort: true
					}],
					dataAction : 'server',
					url :	"${ctx}/frs/verificationWarning/warningInfo.json?dataDate="+dataDate+"&orgNo="+orgNo+"&indexNo="+indexNo+"&rptTemplateId="+rptTemplateId+"&cellNo="+cellNo+"&isPass="+tmpStr,
					type : "post",
					checkbox:false,
					rownumbers : true,
					usePager : true,
					isScroll : false,
					alternatingRow : true
				});
		grid.setHeight($("#center").height() - $("#mainform").height() - 30);
	}
	$(function() {
		initForm();
		initGrid();
		BIONE.createButton({
			appendTo : "#mainform",
			text : '全部',
			icon : 'home_line',
			width : '55px',
			align : 'right',
			click : function() {
				$(".l-btn").css("background-color", "#3c8dbc");
				$(".icon-home_line").parent().css("background-color", "#44bc3c");
				tmpStr = "";
				initGrid(tmpStr);
			}
		});
		BIONE.createButton({
			appendTo : "#mainform",
			text : '通过',
			icon : 'succed',
			width : '55px',
			align : 'right',
			click : function() {
				$(".l-btn").css("background-color", "#3c8dbc");
				$(".icon-succed").parent().css("background-color", "#44bc3c");
				tmpStr = '0';
				initGrid(tmpStr);
			}
		});
		BIONE.createButton({
			appendTo : "#mainform",
			text : '未通过',
			icon : 'close',
			width : '55px',
			align : 'right',
			click : function() {
				$(".l-btn").css("background-color", "#3c8dbc");
				$(".icon-close").parent().css("background-color", "#44bc3c");
				tmpStr = '1';
				initGrid(tmpStr);
			}
		});

		$(".icon-home_line").parent().css("background-color", "#44bc3c");
	});
</script>
<title>预警校验</title>
</head>
<body>
	<div id="template.center">
	<form id="mainform"></form>
		<div id="griddiv"></div>
	</div>
</body>
</html>