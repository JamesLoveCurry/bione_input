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
	var grid;
</script>
<script type="text/javascript">
	$(function() {
		initWarnGrid();
	});

	//初始化警戒值校验grid
	function initWarnGrid() {
		grid = $("#griddiv")
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
							}, {
								display : '操作',
								name : 'oper',
								width : '10%',
								align : 'center',
								render : function(a, b, c){
									if(a.isFrs == "02" && a.isPass == "1"){
										if(a.validDesc != "" && a.validDesc != null){
											return "<a href='javascript:void(0);' onClick=\"addValidDesc('"+a.checkId +"','"+ a.validDesc+"')\">已添加</a>";
										}else{
											return "<a href='javascript:void(0);' onClick=\"addValidDesc('"+a.checkId +"','"+ a.validDesc+"')\">添加说明</a>";
										}
									}else{
										return "";
									}
								}
							},
							{
								display : '说明',
								name : 'validDesc',
								width : '1%',
								hide : true
							}
							,{
								display : '校验id',
								name : 'checkId',
								width : '1%',
								hide : true
							}],
							dataAction : 'server',
							url : "${ctx}/frs/verificationWarning/warningInfo.json?",
							parms : {
								dataDate : dataDate,
								orgNo : orgNo,
								indexNo : rptIndexNo,
								rptTemplateId : rptTmpId,
								isPass : "1"
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

	//添加校验未通过说明
	function addValidDesc(checkId, validDesc){
		parent.BIONE.commonOpenDialog("编辑校验未通过说明", "validDescWin", "550", "350", "${ctx}/frs/verificationWarning/addValidDesc?checkId=" + checkId +"&dataDate=${dataDate}&orgNo=${orgNo}&validDesc=" + validDesc, null);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="griddiv"></div>
	</div>
</body>
</html>