<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<script type="text/javascript">
	var grid, datas = [];
	$(function() {
		initSearchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '表是否存在',
			icon : 'order',
			//width : '100px',
			click : function() {
				checkTableNameExist();
			}
		});
		$("#tableName").focusout(function() {
			var tableName = document.getElementById("tableName").value;
			$.ligerui.get("tableName").setValue(tableName.toUpperCase());
		});
	});
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : '数据源',
				name : "dsName",
				comboboxName : 'dsNameBox',
				newline : true,
				type : "select",
				options : {
					valueFieldID : "dsId"
				},
				attr : {
					field : 'dsId',
					op : "="
				}
			}, {
				display : '表名',
				newline : false,
				name : 'tableEnName',
				type : 'text',
				attr : {
					field : 'tableEnName',
					op : "like"
				}
			}, {
				display : '表中文名',
				newline : false,
				name : 'tableCnName',
				type : 'text',
				attr : {
					field : 'tableCnName',
					op : "like"
				}
			} ]
		});
		
		$.ajax({
			async : true,
			url : "${ctx}/rpt/input/table/getDatSourceList.json",
			dataType : 'json',
			type : "get",
			success : function(data) {
				$.ligerui.get('dsNameBox').setData (data);
				datas = data;
			}
		});
	};

	function initGrid() {
		grid = $("#maingrid").ligerGrid(
				{
					columns : [ {
						hide : 1,
						name : 'tableId',
						width : '10%',
					}, {
						isSort : true,
						display : '表名',
						name : 'tableEnName',
						width : "25%",
						align : 'left',
						render:function(row){
							return  "<a href='javascript:void(0)'   onclick='tableLinkUpdate(\""+row.tableId+"\")'>"+row.tableEnName+"</a>";
						}	
					}, {
						isSort : true,
						display : '表中文名',
						name : 'tableCnName',
						width : "25%",
						align : 'left'
					}, {
						isSort : true,
						display : '数据源',
						name : 'dsId',
						width : "12%",
						align : 'center',
						render : function(rowdata, index, value) {
							if (value) {
								for (var i = 0, len = datas.length; i < len; i++) {
									if (value == datas[i].id) 
										return datas[i].text;
								}
							}
						}
					}, {
						isSort : true,
						display : '创建日期',
						name : 'createDate',
						width : "20%",
						align : 'center'
					} ],
					url : "${ctx}/rpt/input/table/list.json?d="
							+ new Date().getTime(),
					width : "100%",
					height : "99%",
					rownumbers : true,
					checkbox : true,
					dataAction : 'server', //从后台获取数据
					usePager : true, //服务器分页
					alternatingRow : true, //附加奇偶行效果行
					colDraggable : true,
					pageParmName : 'page',
					pagesizeParmName : 'pagesize',
					method : 'post',
					sortName : 'createDate',//第一次默认排序的字段
					sortOrder : 'desc', //排序的方式
					/* onDblClickRow : function (data, rowindex, rowobj){
						openWin(data.tableId);
						grid.reRender();
					}, */
					toolbar : {}
				});
	}

	function initButtons() {
		items = [ 
			{
				text : '增加',
				click : table_add,
				icon : 'fa-plus'
			}, {
				text : '修改',
				click : table_update,
				icon : 'icon-modify'
			}, {
				text : '删除',
				click : table_delete,
				icon : 'icon-delete'
			}, {
				text : '关联补录模板',
				click : table_search,
				icon : 'icon-refresh'
			}  
			/* 需求没有预加载功能20190613 */
			, {
				text : '数据预加载',
				click : dataload,
				icon : 'icon-screen'
			} 
		];
		BIONE.loadToolbar(grid, items, function() {
		});
	}

	function dataload(){
		achieveIds();
		if (ids.length == 1) {
			BIONE.commonOpenDialog("预增数据管理", "dataload", $("#center").width()-50, $("#center").height()-30,
					"${ctx}/rpt/input/table/dataload?id="+ids[0]);
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行查看');
		} else {
			BIONE.tip('请选择一行进行查看');
		}
		
	}
	
	//查询指定补录表引用的补录模板
	function table_search() {
		achieveIds();
		if (ids.length == 1) {
			BIONE.commonOpenDialog("查看关联的补录模板", "queryTempInfo", $("#center").width()-50, $("#center").height()-30,
					"${ctx}/rpt/input/table/" + ids[0]
							+ "/queryTemp");
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行查看');
		} else {
			BIONE.tip('请选择一行进行查看');
		}

	}

	function table_add() {
		var width = $("#center").width()-50;
		var height = $("#center").height()-30;
		BIONE.commonOpenDialog("数据库表添加", "tableAddWin", width, height,
				"${ctx}/rpt/input/table/newTable");
	}

	function tableLinkUpdate(tableId){
		openWin(tableId);
	}
	
	function table_update() {
		achieveIds();
		if (ids.length == 1) {
			openWin(ids[0]);
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行修改');
		} else {
			BIONE.tip('请选择一行进行修改');
		}
	}
	function openWin(tableId) {
		var width = $("#center").width()-50;
		var height = $("#center").height()-30;
		BIONE.commonOpenDialog("数据库表修改", "tableAddWin", width, height,
				"${ctx}/rpt/input/table/updateTable?tableId="
						+ tableId + "&d=" + new Date().getTime());
	}

	function table_delete() {
		achieveIds();
		if (ids.length > 0) {
			var tableId = '';
			for ( var i = 0; i < ids.length; i++) {
				if (i == ids.length - 1) {
					tableId = tableId + ids[i];
				} else {
					tableId = tableId + ids[i] + ",";
				}
			}
			$.ligerDialog
					.confirm(
							'是否同时删除该条记录中的物理表?',
							function(yess) {
								if (true === yess) {
									$.ajax({
										cache : false,
										async : true,
										url : "${ctx}/rpt/input/table/checkTempListByTableName.json",
										dataType : 'json',
										type : "get",
										data : {
											"tableId" : tableId
										},
										success : function(result) {
											if (result != null && result.length > 0) {
												var tempNames = '';
												for ( var i = 0; i < result.length; i++) {
													if (i == result.length - 1) {
														tempNames = tempNames
																+ result[i];
													} else {
														tempNames = tempNames
																+ result[i]
																+ ',';
													}
												}
												BIONE.tip('该补录表已经关联补录模板【'+ tempNames+ '】,故不能删除');
											} 
											else {
												$.ajax({
													cache : false,
													async : true,
													url : "${ctx}/rpt/input/table/delete?d="
															+ new Date().getTime(),
													dataType : 'text',
													type : "get",
													data : {
														"tableId" : tableId,
														"delete" : yess
													},
													success : function(result) {
														f_reload();
														BIONE.tip('删除成功');
																
													}
												});
											}
											
										}
									});
								}
							});
		} else {
			BIONE.tip('请选择一行或者多行进行删除');
		}
	}

	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].tableId);
		}
	}

	function f_reload() {
		var manager = $("#maingrid").ligerGetGridManager();
		manager.loadData();
	}

	function checkTableNameExist() {
// 		var dsId = $("#search input[name=dsId]").val();
		var dsId = liger.get('dsNameBox').getValue();
		var tableName = $("#search input[name=tableEnName]").val();
		if (dsId) {
			if (tableName) {
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/input/table/checkTableName",
					dataType : 'json',
					type : "get",
					data : {
						"dsId" : dsId,
						"tableEnName" : tableName,
						"tableId" : ''
					},
					success : function(result1) {
						if (result1 == false) {
							BIONE.tip('此表名在数据源中已存在！');
						} else {
							BIONE.tip('此表名在数据源中不存在！');
						}
					}
				});
			} else {
				BIONE.tip('请输入要检测的表名！');
			}
		} else {
			BIONE.tip('请先选择数据源！');
		}
	}
</script>
</head>
<body>
</body>
</html>