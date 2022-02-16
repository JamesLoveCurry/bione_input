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
	var indexCatalogNo = '${indexCatalogNo}';
	var indexNo = '${indexNo}';
	var indexVerId = '${indexVerId}';
	var defSrc = '${defSrc}';
    
	$(function() {
		//初始化
		searchForm();
		initGrid();
		initToolbar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		window.parent.refreshObj1 = window;
	});

	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : '表达式描述',
				name : "expressionDesc",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'expressionDesc',
					op : "like"
				}
			} ]
		});
	}

	function initGrid() {
		var  url_ = "${ctx}/report/frame/rptvalid/logic/list.json?indexCatalogNo="
			+ indexCatalogNo;
		if(indexCatalogNo){  //目录不为空，展示具体指标下不同机构的界限
			url_ += "&indexNo=" + indexNo + "&indexVerId=" + indexVerId;
		}
		if(defSrc){
			url_ += "&defSrc="+defSrc;
			}
		grid = $("#maingrid").ligerGrid({
			//height:"100%",
			width : "100%",
			columns : [ {
				display : '校验公式名称',
				name : 'checkNm',
				width : '15%',
				align : 'center'
			},{
				display : '表达式',
				name : 'expressionDesc',
				width : '40%',
				align : 'left'
			},{
				display : '容差值',
				name : 'floatVal',
				width : '10%',
				align : 'center'
			}, {
				display : '开始时间',
				name : 'startDate',
				width : '15%',
				align : 'center'
			}, {
				display : '结束时间',
				name : 'endDate',
				width : '15%',
				align : 'center'
			} ],
			usePager : true,
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			alternatingRow : true, //附加奇偶行效果行
			url : url_,
			method : 'post', // get
			sortName : 'expressionDesc', //第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			toolbar : {}
		});

	}
	function reloadGrid() {
		grid.set('parms', {
			condition : ''
		});
		var  url_ = "${ctx}/report/frame/rptvalid/logic/list.json?indexCatalogNo="
			+ indexCatalogNo;
		if(indexCatalogNo){  //目录不为空，展示具体指标下不同机构的界限
			url_ += "&indexNo=" + indexNo + "&indexVerId=" + indexVerId;
		}
		if(defSrc){
			url_ += "&defSrc="+defSrc;
			}
		grid.set('url', url_);
	}

	function initToolbar() {
		items = [
				{
					text : '增加逻辑校验',
					click : function() {
						if (indexCatalogNo && indexNo) {
							window.parent.parent.logicWindow = window; 
							window.parent.parent.BIONE
									.commonOpenDialog(
											'逻辑校验新增',
											'logicAdd',
											$(document.body).width() + 200,
											$(document.body).height() + 100,
											"${ctx}/report/frame/rptvalid/logic/new?indexCatalogNo="
											+  indexCatalogNo +"&indexNo="+indexNo
											+"&indexNm="+window.parent.curIdxName+"&defSrc="+defSrc+"&d="+ new Date().getTime());
						} else {
							window.parent.parent.logicWindow = window; 
							window.parent.parent.BIONE
									.commonOpenDialog(
											'逻辑校验新增',
											'logicAdd',
											$(document.body).width() + 200,
											$(document.body).height() + 100,
											"${ctx}/report/frame/rptvalid/logic/new?d="
											+ new Date().getTime());
							} 
					},
					icon : 'fa-plus'
				},
				{
					text : '修改逻辑校验',
					click : function() {
						var selected = grid.getSelectedRows();
						if (!selected||selected.length == '0') {
							BIONE.tip("请先选择需要修改的逻辑校验");
							return;
						}
						if (selected.length > 1) {
							BIONE.tip("至多选择一个校验公式");
							return;
						}
						window.parent.parent.logicWindow = window; 
						window.parent.parent.BIONE
								.commonOpenDialog(
										'逻辑校验修改',
										'logicAdd',
										$(document.body).width() + 200,
										$(document.body).height() + 100,
										"${ctx}/report/frame/rptvalid/logic/new?checkId="
												+ selected[0].checkId
												+ "&d="+ new Date().getTime());
					},
					icon : 'fa-pencil-square-o'
				},
				{
					text : '删除逻辑校验',
					click : f_delete,
					icon : 'fa-trash-o'
				}];
		BIONE.loadToolbar(grid, items, function() {
		});
	}

	function f_delete() {
		var selected = grid.getSelectedRows();
		if (!selected||selected.length == '0') {
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
					url : "${ctx}/report/frame/rptvalid/logic/" + ids,
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
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>