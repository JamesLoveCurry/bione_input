<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript"
	src="${ctx}/js/bignumber/bignumber.min.js"></script>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var operType = "${operType}";
	var delayFlag = false;
	var cellMap = {};
	var dataUnit = "${dataUnit}";
	var templateId = '${templateId}';
	var orgNo = '${orgNo}';
	var dataDate = '${dataDate}';
	$(function() {
		if ("${cellNo}") {
			delayFlag = true;
		} 
		initData();
		initSearchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		if ("${cellNo}") {
			$("#cellNo").val("${cellNo}");
			$('.fa-search').parent().click();
		}
	});

	function initButtons() {
		var btns = [ {
			text : '数据导出',
			click : oper_fdown,
			icon : 'fa-download',
			operNo : 'oper_fdown'
		} ]
		BIONE.loadToolbar(grid, btns, function() {
		});
	};

	function oper_fdown() {
		var download;
		var flag = false;

		BIONE.tip("正在导出数据中...");
		var src = '';
		src = "${ctx}/rpt/frs/rptfill/download?taskInstanceId=${taskInstanceId}&templateId="
				+ templateId + "&taskFillOperType=27";
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
		downdload.attr('src', src);
	}

	function initData() {
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/getCellMap?taskInstanceId=${taskInstanceId}",
			dataType : 'json',
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				window.cellMap = result;
				if (templateId == "") {
					templateId = window.cellMap.templateId;
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : '单元格编号',
				name : "cellNo",
				newline : false,
				type : "text",
				cssClass : "field",
				labelWidth : "100",
				attr : {
					op : "like",
					field : "his.cellNo"
				}
			}, {
				display : '修改机构',
				name : "orgNm",
				comboboxName : "orgNm_sel",
				newline : false,
				type : "select",
				cssClass : "field",
				labelWidth : "100",
				attr : {
					op : "in",
					field : "org.orgNo"
				},
				options : {
					onBeforeOpen : function() {
						var height = $(window).height() - 120;
						var width = $(window).width() - 480;
						window.BIONE .commonOpenDialog(
										"机构树",
										"taskOrgWin",
										width,
										height,
										"${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=${orgType}",
										null);
						return false;
					},
					cancelable : true
				}
			} ]
		});

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
	function initGrid() {
		grid = $("#maingrid")
				.ligerGrid(
						{
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
										width : "8%",
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
										width : "15%",
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
										width : "15%",
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
										display : '修改用户',
										name : 'userNm',
										width : "8%",
										align : 'center'
									}, {
										display : '修改机构',
										name : 'orgNm',
										width : "15%",
										align : 'center'
									}, {
										display : '修改时间',
										name : 'uptTime',
										width : "10%",
										align : 'center',
										type : "date",
										format : 'yyyy-MM-dd hh:mm:ss'
									}, {
										display : '修改说明',
										name : 'updateDesc',
										width : "20%",
									} ],
							width : "100%",
							height : "99%",
							checkbox : false,
							dataAction : 'server',
							usePager : true,
							alternatingRow : true,
							toolbar : {},
							colDraggable : true,
							delayLoad : delayFlag,
							url : "${ctx}/rpt/frs/rptfill/gethisDetail?taskInstanceId=${taskInstanceId}&taskId=${taskId}&templateId="
									+ templateId + "&taskFillOperType=27&operType=" + operType +'&orgNo='+orgNo+"&orgType=${orgType}"+"&dataDate="+dataDate,
							sortName : 'his.uptTime',//第一次默认排序的字段
							sortOrder : 'desc', //排序的方式
							usePager: true,
							pageParmName : 'page',
							pagesizeParmName : 'pagesize',
							rownumbers : true,
							enabledSort : false
						});

	};
</script>
</head>
<body>
</body>
</html>