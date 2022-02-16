<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	//全局变量
	var grid;
	var dialog;
	var catalogId = "";
	var catalogName = "";
	var rptCycle = window.parent.currentNode?(window.parent.currentNode.params.rptCycle?window.parent.currentNode.params.rptCycle:""):"";
	var templateId = '${templateId}' != "" ? '${templateId}' : window.parent.currentNode?(window.parent.currentNode.params.templateId?window.parent.currentNode.params.templateId:""):"";
	var dataDate = '${dataDate}';
	var indexNo = '${indexNo}';
	var download;
	$(function() {	
		//初始化
		initGrid();
		if(dataDate == ""){
			searchForm();
			initSystemVer();
			initToolbar();
			BIONE.addSearchButtons("#search", grid, "#searchbtn");
			downdload = $('<iframe id="download"  style="display: none;"/>');
			$('body').append(downdload);
			initTool();
		}else{
			$("#mainsearch").hide();
			$(".l-panel-topbar").hide();
			$(".l-panel-topbar").empty();
		}
	});
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "制度版本",
				name : "endDate",
				newline : true,
				type : "select",
				options : {
					data : null
				},
				attr : {
					field : "warn.endDate",
					op : ">="
				}
			}, {
				display : '指标单元格',
				name : "indexNm",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'idx.indexNm',
					op : "like"
				}
			}, {
				display : '校验公式ID',
				name : "checkId",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'warn.checkId',
					op : "="
				}
			}, {
				display : '预警类型',
				name : "compareType",
				newline : true,
				type : "select",
				cssClass : "field",
				options : {
					data : [{
						"id" : "01",
						"text" : "环比"
					}, {
						"id" : "02",
						"text" : "同比"
					}, {
						"id" : "03",
						"text" : "较上日"
					}, {
						"id" : "04",
						"text" : "较月初"
					}, {
						"id" : "05",
						"text" : "较上月末"
					}, {
						"id" : "06",
						"text" : "较季初"
					}, {
						"id" : "07",
						"text" : "较上季末"
					}, {
						"id" : "08",
						"text" : "较年初"
					}, {
						"id" : "09",
						"text" : "较上年末"
					}]
				},
				attr : {
					field : 'warn.compareType',
					op : "="
				}
			}, {
				display : '监管要求',
				name : "isFrs",
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
					data : [{"id":"01","text":"是"},{"id":"02","text":"否"}]
				},
				attr : {
					field : 'warn.isFrs',
					op : "="
				}
			}]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid(
				{
					height:"99%",
					width : "100%",
					columns : [{
						display : '指标单元格',
						name : 'indexNm',
						width : '15%',
						align : 'center'
					}, {
						display : '预警类型',
						name : 'compareType',
						width : '10%',
						align : 'center',
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
						display : "幅动类型",
						name : 'rangeType',
						width : '10%',
						align : 'center',
						render : function(data) {
							if (data.rangeType == "01"){
								return "数字";
							}else if (data.rangeType == "02"){
								return "百分比";
							}
						}
					}, {
						display : "单位",
						name : 'unit',
						width : '10%',
						align : 'center',
						render : function(data) {
							if (data.unit == "01"){
								return "元";
							} else if (data.unit == "02"){
								return "百";
							} else if (data.unit == "03"){
								return "千";
							} else if (data.unit == "04"){
								return "万";
							} else if (data.unit == "05"){
								return "亿";
							}
						}
					}, {
						display : "预警波动下限",
						name : 'minusRangeVal',
						width : '10%',
						align : 'center'
					}, {
						display : "预警波动上限",
						name : 'postiveRangeVal',
						width : '10%',
						align : 'center'
					}, {
						display : "监管要求",
						name : 'isFrs',
						width : '8%',
						align : 'center',
						render : function(data) {
							if (data.isFrs == "01"){
								return "是";
							}else if (data.isFrs == "02"){
								return "否";
							}	
						}
					}, {
						display : '开始时间',
						name : 'startDate',
						width : '12%',
						align : 'center'
					}, {
						display : '结束时间',
						name : 'endDate',
						width : '12%',
						align : 'center'
					}, {
						display : '备注',
						name : 'remark',
						width : '15%',
						align : 'left'
					}],
					usePager : true,
					checkbox : true,
					dataAction : 'server', //从后台获取数据
					alternatingRow : true, //附加奇偶行效果行
					url : "${ctx}/report/frame/valid/warn/list.json?templateId=" + templateId + "&dataDate=" + dataDate +"&srcIdxNo=" + indexNo,
					method : 'post', // get
					sortName : 'indexNm', //第一次默认排序的字段
					sortOrder : 'asc', //排序的方式
					rownumbers : true,
					toolbar : {}
				});
		grid.setHeight($("#center").height() - 85);
	}
	
	function reloadGrid(){
		initSystemVer();
		initToolbar();
		grid.set('parms', {
			condition : ''
		});
		if(window.parent.currentNode){
			grid.set('url', "${ctx}/report/frame/valid/warn/list.json?templateId=" + window.parent.currentNode.params.templateId);
		}
	}

	function initToolbar() {
		var items = [
			{
				text : '查看校验',
				click : function() {
					var selected = grid.getSelectedRows();
					if (!selected) {
						BIONE.tip("请先选择需要修改的校验！");
						return;
					}
					if(selected.length != 1){
						BIONE.tip("选择一个校验公式！");
						return ;
					}
					window.parent.BIONE.commonOpenDialog("预警校验修改", "warnAdd",
							800, 500,
							"${ctx}/report/frame/valid/warn/new?templateId="
							+ window.parent.currentNode.params.templateId  + "&cfgId=" + window.parent.currentNode.params.templateId + "&checkId=" + selected[0].checkId + "&isQuery=" + true);
				},
				icon : 'fa-pencil-square-o'
			}
		];
		var endDate = $.ligerui.get("endDate").getValue();
		if(("29991231" == endDate) || ("" == endDate)){
			items = [{
				text : '增加预警校验',
				click : function() {
					if(window.parent.currentNode){
						window.parent.BIONE.commonOpenDialog('预警校验新增',
								'warnAdd', 800, 500,
								"${ctx}/report/frame/valid/warn/new?templateId="
										+ window.parent.currentNode.params.templateId + "&cfgId=" + window.parent.currentNode.params.templateId + "&rptCycle=" + window.parent.currentNode.params.rptCycle);
						}else{
							BIONE.tip("请先选择报表");
						}
	
				},
				icon : 'fa-plus'
			}, {
				text : '修改预警校验',
				click : function() {
					var selected = grid.getSelectedRows();
					if (!selected) {
						BIONE.tip("请先选择需要修改的校验！");
						return;
					}
					if(selected.length != 1){
						BIONE.tip("至多选择一个校验公式!");
						return ;
					}
					window.parent.BIONE.commonOpenDialog("预警校验修改", "warnAdd",
							800, 500,
							"${ctx}/report/frame/valid/warn/new?templateId="
							+ window.parent.currentNode.params.templateId  + "&cfgId=" + window.parent.currentNode.params.templateId + "&checkId=" + selected[0].checkId+ "&rptCycle=" + window.parent.currentNode.params.rptCycle);
				},
				icon : 'fa-pencil-square-o'
			}, {
				text : '删除',
				click : f_delete,
				icon : 'fa-trash-o'
			}, {
				text : '查看校验',
				click : function() {
					var selected = grid.getSelectedRows();
					if (!selected) {
						BIONE.tip("请先选择需要修改的校验！");
						return;
					}
					if(selected.length != 1){
						BIONE.tip("选择一个校验公式！");
						return ;
					}
					window.parent.BIONE.commonOpenDialog("预警校验修改", "warnAdd",
							800, 500,
							"${ctx}/report/frame/valid/warn/new?templateId="
							+ window.parent.currentNode.params.templateId  + "&cfgId=" + window.parent.currentNode.params.templateId + "&checkId=" + selected[0].checkId + "&isQuery=" + true);
				},
				icon : 'fa-pencil-square-o'
			},{
				text : '校验公式下载',
				icon : 'fa-download',
				menu:{
					items:[{
						icon:'export',
						text:'导出所选项',
						click : exp_warn('sel')
					},{
						icon:'export',
						text:'导出当前报表',
						click : exp_warn('all')
					},{
						icon:'export',
						text:'批量导出',
						click : export_Choice
					}]
				}
			},{
				text : '校验公式上传',
				click : f_import,
				icon : 'fa-upload'
			}];
		}
		BIONE.loadToolbar(grid, items, function() {});
	}
	
	function f_delete(){
		var selected = grid.getSelectedRows();
		if (!selected || selected.length == 0) {
			BIONE.tip("请先选择需要删除的校验");
			return;
		}
		$.ligerDialog
		.confirm(
				"确定删除这些校验吗？",
				function(flag) {
					if (flag) {
						var ids = "";
						for(var i=0;i<selected.length;i++){
							ids += selected[i].checkId + ",";
						}
						
						$.ajax({
									type : "POST",
									url : "${ctx}/report/frame/valid/warn/" + ids + "?templateId=" + window.parent.currentNode.params.templateId,
									success : function() {
										BIONE.tip("删除成功！");
										reloadGrid();
									},
									error : function(XMLHttpRequest,
											textStatus, errorThrown) {
										BIONE.tip('删除失败,错误信息:'
												+ textStatus);
									}
								});
					}
				});
	}
	
 	function initSystemVer(){
		if(window.parent.currentNode){
			var busiType = window.parent.currentNode.params.busiType;
		}
		if(busiType){
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/frs/system/cfg/getSystemEndDate",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result){
					if(result){
						$.ligerui.get("endDate").setData(result);
					}
				},
				error:function(){
					BIONE.tip("数据加载异常，请联系系统管理员！");
				}
			});
		}
	}

	function exp_warn(expType) {
		var endDate = $("#endDate").val();
		return function () {
			var ids = "all";
			if(expType == 'sel'){
				ids = "";
				var selected = grid.getSelectedRows();
				if (!selected || selected.length == 0) {
					BIONE.tip("请先选择需要下载的预警校验");
					return;
				}
				for ( var i = 0; i < selected.length; i++) {
					ids += selected[i].checkId + ",";
				}
			}
			if(expType == 'all' && !window.parent.currentNode){
				BIONE.tip("请先选择需要下载的报表");
				return;
			}

			if (ids.length == 0 && expType == 'sel') {
				return;
			}

			$.ajax({
				type : "POST",
				dataType : "json",
				url : "${ctx}/report/frame/valid/warn/validWarnRel/exp",
				data : {
					"ids" : ids,
					"templateId"  : window.parent.currentNode.params.templateId,
					"endDate" : endDate,
					"busiType" : window.parent.currentNode.params.busiType
				},
				beforeSend : function(a, b, c) {
					BIONE.showLoading('正在导出数据中...');
				},
				success : function(data) {
					BIONE.hideLoading();
					if (data.filepath == "") {
						BIONE.tip('导出异常');
						return;
					}

					downdload.attr('src', "${ctx}/report/frame/valid/warn/validWarnRel/download?fileName=" + data.filepath + "&rptNm=" + encodeURI(encodeURI(window.parent.currentNode.text)));
				},
				error : function(result) {
					BIONE.hideLoading();
				}
			});
		}
	}

	function export_Choice(){
		var $content = $(window);
		var height = $content.height();
		BIONE.commonOpenDialog("预警校验导出", "upload", 600, height-50,
				"${ctx}/report/frame/valid/warn/exportChoice");
	}

	function f_import(){
		var $content = $(window);
		var height = $content.height();
		BIONE.commonOpenDialog("校验模板导入", "upload", 600, height-50,
				"${ctx}/report/frame/valid/warn/validWarnRel/impTabWin");
	}

	function initTool(){
		$("#searchbtn").find("button").click(function(){
			initToolbar();
		});
	}
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>