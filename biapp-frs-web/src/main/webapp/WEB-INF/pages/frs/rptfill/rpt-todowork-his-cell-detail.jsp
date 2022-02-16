<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript"
	src="${ctx}/js/bignumber/bignumber.min.js"></script>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var templateId = '${rptTmpId}';
	var dataDate = '${dataDate}';
	var orgNo = '${orgNo}';
	var cellNo = '${cellNo}';
	var taskInstanceId = '${taskInstanceId}';
	var cellMap = {};
	$(function() {
		initGrid();
	});

	function initGrid() {
		grid = $("#griddiv")
				.ligerGrid(
						{
							height : '100%',
							width : '100%',
							columns : [
									{
										display : '单元格编号',
										name : 'cellNo',
										width : "6%",
										align : 'center'
									},
									{
										display : '初始值',
										name : 'initVal',
										width : "10%",
										align : 'center',
										render : function(a, b, c) {
											var tel = /^[\+\-]?[\d]+([\.][\d]*)?([Ee][+-]?[\d]+)$/;
											if (tel.test(c)) {
												var val = c.split("E");
												if (val.length == 1) {
													return c
												} else {
													var number = new BigNumber(
															val[0]);
													;
													var e = parseInt(val[1]);
													if (Math.abs(e) == e) {
														for (var i = 0; i < e; i++) {
															number = number
																	.times(10);
														}
													} else {
														for (var i = 0; i < e; i++) {
															number = number
																	.dividedBy(10);
														}
													}
													return number.toNumber();
												}
											} else {
												return c
											}

										}
									},
									{
										display : '改前值',
										name : 'objVal',
										width : "10%",
										align : 'center',
										render : function(a, b, c) {
											var tel = /^[\+\-]?[\d]+([\.][\d]*)?([Ee][+-]?[\d]+)$/;
											if (tel.test(c)) {
												var val = c.split("E");
												if (val.length == 1) {
													return c
												} else {
													var number = new BigNumber(
															val[0]);
													;
													var e = parseInt(val[1]);
													if (Math.abs(e) == e) {
														for (var i = 0; i < e; i++) {
															number = number
																	.times(10);
														}
													} else {
														for (var i = 0; i < e; i++) {
															number = number
																	.dividedBy(10);
														}
													}
													return number.toNumber();
												}
											} else {
												return c;
											}

										}
									},
									{
										display : '改后值',
										name : 'newObjVal',
										width : "10%",
										align : 'center',
										render : function(a, b, c) {
											var tel = /^[\+\-]?[\d]+([\.][\d]*)?([Ee][+-]?[\d]+)$/;
											if (tel.test(c)) {
												var val = c.split("E");
												if (val.length == 1) {
													return c
												} else {
													var number = new BigNumber(
															val[0]);
													;
													var e = parseInt(val[1]);
													if (Math.abs(e) == e) {
														for (var i = 0; i < e; i++) {
															number = number
																	.times(10);
														}
													} else {
														for (var i = 0; i < e; i++) {
															number = number
																	.dividedBy(10);
														}
													}
													return number.toNumber();
												}
											} else {
												return c;
											}

										}
									}, {
										display : '修改用户',
										name : 'userNm',
										width : "10%",
										align : 'center'
									}, {
										display : '修改时间',
										name : 'uptTime',
										width : "10%",
										align : 'center',
										type : "date",
										format : 'yyyy-MM-dd hh:mm:ss'
									},{
										display : '修改说明',
										name : 'updateDesc',
										width : "20%",
									},{
										display : '操作',
										name : 'oper',
										width : '10%',
										align : 'center',
										render : function(a, b, c){
											if(a.isLock == "Y"){
												if(a.updateDesc != "" && a.updateDesc != null){
													return "<a href='javascript:void(0);' onClick=\"addUpdateDesc('"+a.hisId +"','"+ a.updateDesc+"')\">修改</a>";
												}else{
													return "<a href='javascript:void(0);' onClick=\"addUpdateDesc('"+a.hisId +"','"+ a.updateDesc+"')\">添加说明</a>";
												}
											}else{
												return "";
											}
										}
									} ],
							dataAction : 'server',
							url : "${ctx}/rpt/frs/rptfill/gethisDetail",
							parms : {
								cellNo : cellNo,
								templateId : templateId,
								taskInstanceId : taskInstanceId,
								dataDate : dataDate
							},
							type : "post",
							checkbox : false,
							rownumbers : true,
							usePager : false,
							isScroll : false,
							alternatingRow : true
						});
		grid.setHeight($("#center").height());
	};

	function toNumber2(dataUnit, value) {
		var unit = 1;
		if (dataUnit == "01") {
			unit = 1;
		} else if (dataUnit == "02") {
			unit = 100;
		} else if (dataUnit == "03") {
			unit = 1000;
		} else if (dataUnit == "04") {
			unit = 10000;
		} else if (dataUnit == "05") {
			unit = 100000000;
		}
		value = (parseFloat(value) / unit).toString();
		var obj = value.split(".");
		if (obj.length == 1) {
			return obj[0] + ".00";
		} else {
			if (obj[1].length == 1) {
				return obj[0] + "." + obj[1] + "0";
			} else {
				return value;
			}
		}
	};
	
	//添加修改说明
	function addUpdateDesc(hisId, updateDesc){
		parent.BIONE.commonOpenDialog("单元格数据修改说明", "updateDescWin", "550", "350", "${ctx}/rpt/frs/rptfill/addUpdateDesc?hisId=" + hisId + "&updateDesc=" + updateDesc, null);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="griddiv"></div>
	</div>
</body>
</html>