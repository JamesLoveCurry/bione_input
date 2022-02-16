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
	var delayFlag = false;
	var rptId = '${rptId}';
	var orgNo = '${orgNo}';
	var dataDate = '${dataDate}';
	var orgType = '${orgType}';
	var taskId = '${taskId}';
	$(function() {
		initSearchForm();
		initGrid();
		// initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
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
					op : "=",
					field : "rpt.cellNo"
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
					field : "rpt.orgNo"
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
									},{
										display : '机构',
										name : 'orgNm',
										width : "12%",
										align : 'center'
									}, {
										display : '批注人',
										name : 'userNm',
										width : "10%",
										align : 'center'
									}, {
										display : '批注内容',
										name : 'operInfo',
										width : "40%",
										align : 'center'
									}, {
										display : '修改时间',
										name : 'operTime',
										width : "10%",
										align : 'center',
										type : "date",
										format : 'yyyy-MM-dd hh:mm:ss'
									}],
							width : "100%",
							height : "99%",
							checkbox : false,
							dataAction : 'server',
							usePager : true,
							alternatingRow : true,
							// toolbar : {},
							colDraggable : true,
							delayLoad : delayFlag,
							url : "${ctx}/rpt/frs/rptfill/getRptCellRemark?taskId="+taskId+"&rptId="
									+ rptId + "&orgNo="+orgNo+"&orgType="+orgType+"&dataDate="+dataDate,
							sortName : 'rpt.operTime',//第一次默认排序的字段
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