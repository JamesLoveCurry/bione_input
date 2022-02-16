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
	var templateId = '${templateId}' != "" ? '${templateId}' : window.parent.currentNode?(window.parent.currentNode.params.templateId?window.parent.currentNode.params.templateId:""):"";;
	var dataDate = '${dataDate}';
	var indexNo = '${indexNo}';
	var url = "${ctx}/report/frame/valid/logic/list.json";
	$(function() {
		//初始化
		if(dataDate == ""){
			searchForm();
			initGrid();
			initToolbar();
			BIONE.addSearchButtons("#search", grid, "#searchbtn");
			downdload = $('<iframe id="download"  style="display: none;"/>');
			$('body').append(downdload);
			initTool();
		}else{
			if(window.parent.currentNode){
				url = "${ctx}/report/frame/valid/logic/list.json?templateId="+templateId+"&dataDate="+dataDate+"&srcIdxNo="+indexNo+"&busiType=" +  window.parent.currentNode.params.busiType;
			}else{
				url = "${ctx}/report/frame/valid/logic/list.json?templateId="+templateId+"&dataDate="+dataDate+"&srcIdxNo="+indexNo+"&busiType=" +  window.parent.base_OrgType;
			}
			initGrid();
			$("#mainsearch").hide();
			$(".l-panel-topbar").hide();
			$(".l-panel-topbar").empty();
		}
		
	});

	function searchForm() {
		$("#search").ligerForm({
			labelWidth: 100,
			fields : [{
				display : "制度版本",
				name : "endDate",
				newline : true,
				type : "select",
				options : {
					data : null
				},
				attr : {
					field : "logic.endDate",
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
					field : 'logic.checkId',
					op : "="
				}
			}, {
				display : '是否机构过滤',
				name : "isOrgFilter",
				newline : false,
				type : "select",
				cssClass : "field",
				options : {
					data : [{
						"id" : "N",
						"text" : "否"
					}, {
						"id" : "Y",
						"text" : "是"
					}]
				},
				attr : {
					field : 'logic.isOrgFilter',
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
				name : 'expressionShortDesc',
				width : '40%',
				align : 'left',
				render :  function(data, row, context, it) {
					//小于号影响公式显示不完整，替换所有小于号
					return context.replace(new RegExp("<","g"),"&lt;");
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
				width : '4%',
				align : 'center'
			},{
				display : '差值单位',
				name : 'dataUnit',
				width : '6%',
				align : 'center',
				render : function(data, row, context, it) {
					if ("01" == context) {
						return "元/个";
					} else if ("02" == context) {
						return "百";
					} else if ("03" == context) {
						return "千";
					} else if ("04" == context) {
						return "万";
					} else if ("05" == context) {
						return "亿";
					} else if ("06" == context) {
						return "百分比";
					} else {
						return "无";
					}
				}
			}, {
				display : '精度',
				name : 'dataPrecision',
				width : '5%',
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
				display : '是否机构过滤',
				name : 'isOrgFilter',
				width : '8%',
				align : 'center',
				render : function(data, row, context, it) {
					if ("Y" == context) {
						return "是";
					} else if ("N" == context) {
						return "否";
					}
				}
			}, {
				display : '过滤机构',
				name : 'checkOrg',
				width : '14%',
				align : 'center',
				render : function(val,row, context){
					if(context == '()' || context == null){
						return '<div class="checkOrg1" value='+row+'></div>';
					}else if(context.length > 14){
						return '<div class="checkOrg1" value='+row+' title="' + context + '">' + context.substr(0,14) + '...</div>';
					} else {
						return '<div class="checkOrg1" value='+row+' title="' + context + '">' + context + '</div>';
					}
				}
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
			url : url,
			method : 'post', // get
			sortName : 'startDate', //第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			enabledSort: false,
			toolbar : {}
		});
		grid.setHeight($("#center").height() - 85);
	}
	function reloadGrid() {
		$("#endDate").val("");
		initSystemVer();
		initToolbar();
		grid.set('parms', {
			condition : ''
		});
		if(window.parent.currentNode){
			grid.set('url', "${ctx}/report/frame/valid/logic/list.json?templateId="
					+ window.parent.currentNode.params.templateId+"&busiType="+window.parent.currentNode.params.busiType);
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
								"${ctx}//report/frame/valid/logic/new?templateId="
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
		},{
			text : '一键导出',
			click :exportValid,
			icon : 'fa-download'
		}];
		var endDate = $.ligerui.get("endDate").getValue();
		if(("29991231" == endDate) || ("" == endDate)){
			items = [
				{
					text : '增加逻辑校验',
					click : function() {
						if (window.parent.currentNode) {
							window.parent.parent.BIONE
									.commonOpenDialog(
											'逻辑校验新增',
											'logicAdd',
											$(window.parent.parent.document.body).width(),
											$(window.parent.parent.document.body).height(),
											"${ctx}/report/frame/valid/logic/new?templateId="
													+ window.parent.currentNode.params.templateId
													+ "&rptId="
													+ window.parent.currentNode.params.rptId
													// + "&isQuery"
													// +
													+ "&busiType="
									                + window.parent.currentNode.params.busiType);
						} else {
							BIONE.tip("请先选择报表");
						}

					},
					icon : 'fa-plus'
				},{
					text : '修改',
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
										'逻辑校验修改',
										'logicAdd',
										$(window.parent.parent.document.body).width(),
										$(window.parent.parent.document.body).height(),
										"${ctx}/report/frame/valid/logic/new?templateId="
												+ window.parent.currentNode.params.templateId
												+ "&checkId="
												+ selected[0].checkId
												+ "&rptId="
												+ window.parent.currentNode.params.rptId
												+ "&busiType="
												+ window.parent.currentNode.params.busiType);
					},
					icon : 'fa-pencil-square-o'
				},
				{
					text : '删除',
					click : f_delete,
					icon : 'fa-trash-o'
				},
				{
					text : '校验公式调整',
					click : function() {
						if (window.parent.currentNode) {
							var rptId = window.parent.currentNode.params.rptId;
							$.ajax({
										cache : false,
										async : true,
										url : "${ctx}/report/frame/valid/logic/syncExpressionDesc?rptId="
												+ window.parent.currentNode.params.templateId
												+ "&d="
												+ new Date().getTime(),
										dataType : 'json',
										type : "post",
										data : {},
										beforeSend : function() {
											BIONE.loading = true;
											BIONE.showLoading("正在调整数据中...");
										},
										complete : function() {
											BIONE.loading = false;
											BIONE.hideLoading();
										},
										success : function(data) {
											reloadGrid();
											if(data && (data.isSuccess == "Yes")){
												top.BIONE.tip('校验公式调整成功！');
											}else{
												top.BIONE.tip(data.tip);
											}
										},
										error : function(result, b) {
											BIONE.tip('发现系统错误 <BR>错误码：'
													+ result.status);
										}
									});
						} else {
							BIONE.tip("请先选择报表");
						}

					},
					icon : 'fa-cogs'
				},{
					text : '校验公式下载',
					icon : 'fa-download',
					menu:{
	 					items:[{
	 							icon:'export',
	 							text:'导出所选项',
	 							click : f_save('sel','N')
	 						},{
	 							line:true
	 						},{
	 							icon:'export',
	 							text:'导出当前报表',
	 							click : f_save('all','N')
	 						},{
	 							line:true
	 						},{
	 							icon:'export',
	 							text:'批量导出',
	 							click : export_Choice('N')
	 						}]
	 				}
				},{
					text : '校验公式下载(单元格编号)',
					icon : 'fa-download',
					menu:{
	 					items:[{
	 							icon:'export',
	 							text:'导出所选项',
	 							click : f_save('sel','Y')
	 						},{
	 							line:true
	 						},{
	 							icon:'export',
	 							text:'导出当前报表',
	 							click : f_save('all','Y')
	 						},{
	 							line:true
	 						},{
	 							icon:'export',
	 							text:'批量导出',
	 							click : export_Choice('Y')
	 						}]
	 				}
				},{
					text : '校验公式上传',
					click :f_import,
					icon : 'fa-upload'
				},{
					text : '一键导出',
					click :exportValid,
					icon : 'fa-download'
				} ];
		}
		BIONE.loadToolbar(grid, items, function() {
		});
	}
	
	//一键导出
	function exportValid(){
		var $content = $(window);
		var height = $content.height();
		BIONE.commonOpenDialog("一键导出校验公式", "exportFilter", 600, 340,
		"${ctx}/report/frame/valid/logic/exportLogicValid");
	}
	
	function f_import(){
		var $content = $(window);
		var height = $content.height();
		BIONE.commonOpenDialog("校验模板导入", "upload", 600, height-50,
		"${ctx}/report/frame/valid/logic/validLogicRel/impTabWin");
		
	}
	
	function export_Choice(isCellNo){
		var $content = $(window);
		var height = $content.height();
		return function(){
			BIONE.commonOpenDialog("逻辑校验导出", "export", 600, height-50,
			"${ctx}/report/frame/valid/logic/exportChoice?isCellNo="+isCellNo);
		}
	}
	
	function f_save(expType,isCellNo) {
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
				url : "${ctx}/report/frame/valid/logic/validLogicRel/exp",
				data : {
					"ids" : ids,
					"templateId"  : window.parent.currentNode.params.templateId,
					"endDate" : endDate,
					"busiType" : window.parent.currentNode.params.busiType,
					"isCellNo" : isCellNo
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
					
					downdload.attr('src', "${ctx}/report/frame/valid/logic/validLogicRel/download?fileName=" + data.filepath + "&rptNm=" + encodeURI(encodeURI(window.parent.currentNode.text)));
				},
				error : function(result) {
					BIONE.hideLoading();
				}
			});
		}
	}

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
					url : "${ctx}/report/frame/valid/logic/" + ids
							+ "?templateId="
							+ window.parent.currentNode.params.templateId,
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
	
	function initTool(){
		$("#searchbtn").find("button").click(function(){
			initToolbar();
		});
	}
	
	function exportAll(rptIds, busiType, endDate){
		$.ajax({
			cache : false,
			async : true,
			type : "POST",
			dataType : "json",
			url : '${ctx}/report/frame/valid/logic/validLogicRel/expAll',
			data : {
				"rptIds" : rptIds.join(","),
				"busiTypes" : busiType,
				"endDate" : endDate
			},
			beforeSend : function(a, b, c) {
				BIONE.loading = true;
				BIONE.showLoading('正在导出数据中...');
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if (data.filePath == "") {
					BIONE.tip('导出异常');
					return;
				}
				downdload.attr('src', "${ctx}/report/frame/valid/logic/validLogicRel/downloadAll?filePath=" + data.filePath);
			},
			error : function(result) {
				BIONE.hideLoading();
			}
		});
	}
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>