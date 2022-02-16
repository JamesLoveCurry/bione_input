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
	var downdload;
	var templateId = '${templateId}' != "" ? '${templateId}' : window.parent.currentNode?(window.parent.currentNode.params.templateId?window.parent.currentNode.params.templateId:""):"";
	var dataDate = '${dataDate}';
	$(function() {
		initGrid();
		if(dataDate == ""){
			//初始化
			searchForm();
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
			fields : [{
				display : "制度版本",
				name : "endDate",
				newline : true,
				type : "select",
				options : {
					data : null
				},
				attr : {
					field : "endDate",
					op : ">="
				}
			}, {
				display : '表达式描述',
				name : "expressionDesc",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'expressionDesc',
					op : "like"
				}
			}, {
				display : '校验公式ID',
				name : "checkId",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'checkId',
					op : "="
				}
			}]
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
					BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : "100%",
			height : "99%",
			columns : [ {
				display : '表达式',
				name : 'expressionDesc',
				width : '40%',
				align : 'left'
			},{
				display : '校验类型',
				name : 'expType',
				width : '8%',
				align : 'center',
				render : function(data, row, context, it) {
					if ("01" == context) {
						return "逻辑校验";
					} else if ("02" == context) {
						return "正则表达式校验";
					} else if("03" == context){
						return "字段比较校验";
					}else if("04" == context){
						return "系统重要性银行所有交易对手合计校验";
					}else if("05" == context){
						return "系统重要性银行各交易对手合计校验";
					}else if("06" == context){
						return "系统重要性银行字段比较校验";
					}
				}
			},{
				display : '公式类型',
				name : 'checkType',
				width : '8%',
				align : 'center',
				render : function(data, row, context, it) {
					if ("01" == context) {
						return "表内";
					} else if ("02" == context) {
						return "表间";
					} else {
						return "其他";
					}
				}
			},{
				display : '公式来源',
				name : 'checkSrc',
				width : '8%',
				align : 'center',
				render : function(data, row, context, it) {
					if ("01" == context) {
						return "监管制度";
					} else if ("02" == context) {
						return "自定义";
					} else {
						return "其他";
					}
				}
			}, {
				display : '容差值',
				name : 'floatVal',
				width : '6%',
				align : 'center'
			}, {
				display : '开始时间',
				name : 'startDate',
				width : '8%',
				align : 'center'
			}, {
				display : '结束时间',
				name : 'endDate',
				width : '8%',
				align : 'center'
			}, {
				display : '业务说明',
				name : 'busiExplain',
				width : '13%',
				align : 'center'
			}],
			usePager : true,
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			alternatingRow : true, //附加奇偶行效果行
			url : "${ctx}/rpt/frs/rptDetailValid/detailList.json?templateId=" + templateId + "&dataDate=" + dataDate,
			method : 'post', // get
			sortName : 'expressionDesc', //第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			enabledSort: false,
			toolbar : {}
		});
	}
	function reloadGrid() {
		$("#endDate").val("");
		initSystemVer();
		initToolbar();
		grid.set('parms', {
			condition : ''
		});
		if(window.parent.currentNode){
			grid.set('url', "${ctx}/rpt/frs/rptDetailValid/detailList.json?templateId="
					+ window.parent.currentNode.params.templateId);
		}
	}

	function initToolbar() {
		var items = [{
			text : '查看校验',
			click : function() {
				var selected = grid.getSelectedRows();
				if (!selected) {
					BIONE.tip("请先选择需要修改的逻辑校验");
					return;
				}
				if (selected.length != 1) {
					BIONE.tip("选择一个校验公式");
					return;
				}
				window.parent.parent.BIONE.commonOpenDialog(
								'逻辑校验查看',
								'logicAdd',
								$(window.parent.parent.document.body).width(),
								$(window.parent.parent.document.body).height(),
								"${ctx}//rpt/frs/rptDetailValid/new?templateId="
										+ window.parent.currentNode.params.templateId
										+ "&checkId="
										+ selected[0].checkId
										+ "&rptId="
										+ window.parent.currentNode.params.rptId
										+ "&isQuery=" + true);
			},
			icon : 'fa-pencil-square-o'
		},{
			text : '校验公式下载',
			icon : 'fa-download',
			menu:{
					items:[{
							icon:'export',
							text:'导出所选项',
							click : f_save('sel')
						},{
							line:true
						},{
							icon:'export',
							text:'导出当前报表',
							click : f_save('all')
					}]
				}
		}];
		var endDate = $.ligerui.get("endDate").getValue();
		if(("29991231" == endDate) || ("" == endDate)){
			items = [
				{
					text : '增加逻辑校验',
					click : f_add,
					icon : 'fa-plus'
				},{
					text : '修改',
					click : f_edit,
					icon : 'fa-pencil-square-o'
				},{
					text : '删除',
					click : f_delete,
					icon : 'fa-trash-o'
				},/**{
					text : '数据校验',
					click : detail_Valid,
					icon : 'fa-cogs'
				},**/{
					text : '校验公式下载',
					icon : 'fa-download',
					menu:{
	 					items:[{
	 							icon:'export',
	 							text:'导出所选项',
	 							click : f_save('sel')
	 						},{
	 							line:true
	 						},{
	 							icon:'export',
	 							text:'导出当前报表',
	 							click : f_save('all')
	 						},{
	 							line:true
	 						},{
	 							icon:'export',
	 							text:'批量导出',
	 							click : export_Choice
	 						}]
	 				}
				},{
					text : '校验公式上传',
					click :f_import,
					icon : 'fa-upload'
				}];
		}
		BIONE.loadToolbar(grid, items, function() {
		});
	}
	
	//新增按钮
	function f_add(){
		if (window.parent.currentNode) {
			window.parent.BIONE.commonOpenDialog(
				'新增逻辑校验','logicAdd', 800, 500,"${ctx}/rpt/frs/rptDetailValid/detailNew?templateId="
					+ window.parent.currentNode.params.templateId
					+ "&rptId=" + window.parent.currentNode.params.rptId);
		} else {
			BIONE.tip("请先选择报表");
		}
	}
	
	//修改按钮
	function f_edit(){
		var selected = grid.getSelectedRows();
		if (!selected) {
			BIONE.tip("请先选择需要修改的逻辑校验");
			return;
		}
		if (selected.length != 1) {
			BIONE.tip("选择一个校验公式");
			return;
		}
		window.parent.BIONE.commonOpenDialog(
			'修改校验','logicAdd',800, 500,
			"${ctx}//rpt/frs/rptDetailValid/detailNew?templateId="+ window.parent.currentNode.params.templateId
				+ "&checkId=" + selected[0].checkId
				+ "&rptId=" + window.parent.currentNode.params.rptId
				+ "&busiType="+ window.parent.currentNode.params.busiType);
	}
	
	//删除按钮
	function f_delete() {
		var selected = grid.getSelectedRows();
		if (!selected || selected.length == 0) {
			BIONE.tip("请先选择需要删除的逻辑校验");
			return;
		}
		$.ligerDialog.confirm("确定删除这些逻辑校验吗？", function(flag) {
			if (flag) {
				var ids = "";
				for ( var i = 0; i < selected.length; i++) {
					ids += selected[i].checkId + ",";
				}
				$.ajax({
					type : "POST",
					dataType : "json",
					url : "${ctx}/rpt/frs/rptDetailValid/deleteDetail/" + ids
							+ "?templateId=" + window.parent.currentNode.params.templateId,
					success : function() {
						BIONE.tip("删除成功！");
						reloadGrid();
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						BIONE.tip('删除失败,错误信息:' + textStatus);
					}
				});
			}
		});
	}
	
	function f_import(){
		var $content = $(window);
		var height = $content.height();
		BIONE.commonOpenDialog("校验模板导入", "upload", 600, height-50,
		"${ctx}/rpt/frs/rptDetailValid/validLogicRel/impTabWin");
		
	}
	
	function export_Choice(){
		var $content = $(window);
		var height = $content.height();
		BIONE.commonOpenDialog("明细类-逻辑校验导出", "export", 600, height-50,
		"${ctx}/rpt/frs/rptDetailValid/exportChoice");
	}
	
	function f_save(expType) {
		var endDate = $("#endDate").val();
		return function(){
			var ids = "all";
			if(expType == 'sel'){
				ids = "";
				var selected = grid.getSelectedRows();
				if (!selected || selected.length == 0) {
					BIONE.tip("请先选择需要下载的逻辑校验");
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
				url : "${ctx}/rpt/frs/rptDetailValid/validLogicRel/exp",
				data : {
					"ids" : ids,
					"templateId"  : window.parent.currentNode.params.templateId,
					"rptNm"  : window.parent.currentNode.text,
					"endDate" : endDate
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
					downdload.attr('src', "${ctx}/rpt/frs/rptDetailValid//validLogicRel/download?fileName=" + data.filepath + "&rptNm=" + window.parent.currentNode.text);
				},
				error : function(result) {
					BIONE.hideLoading();
				}
			});
		}
	}

	function initTool(){
		$("#searchbtn").find("button").click(function(){
			initToolbar();
		});
	}
	
	//数据校验入口
	function detail_Valid(){
		var validLogicWinUrl = "${ctx}//rpt/frs/rptDetailValid/detailValid?templateId="+ window.parent.currentNode.params.templateId + "&rptId=" + window.parent.currentNode.params.rptId
		+ "&busiType="+ window.parent.currentNode.params.busiType;
		window.parent.BIONE.commonOpenDialog('明细数据校验', 'validLogicWin', '350', '350', validLogicWinUrl);
	}
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>