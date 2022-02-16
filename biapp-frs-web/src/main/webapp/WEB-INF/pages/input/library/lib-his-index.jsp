<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template2.jsp">
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var items, grid, ids = [];
	$(function() {
		initTree();
		searchForm();
		initGrid();
		initButtons();
		if ("${id}") {
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '选择',
				icon : 'true',
				width : '50px',
				click : function() {
					var selectedRow = grid.getSelecteds();
					var libId = [];
					if (selectedRow.length == 1) {
						libId = selectedRow[0].libId;
					} else if (selectedRow.length > 1) {
						BIONE.tip('只能选择一条数据!');
						return;
					}
					var libId2 = libId;
					parent.setDataZidina(libId2, "${rowid}");
					BIONE.closeDialog("chackedZidian");
				}
			});
		}
	});

	function initTree() {
		window['orgTreeInfo'] = $.fn.zTree.init($("#tree"), {
			async : {
				enable : true,
				contentType : "application/json",
				url : "${ctx}/udip/library/libTree.json",
				dataType : "json",
				type : "get"
			},
			data : {
				keep : {
					parent : true
				},
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			view : {
				showLine : false
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					var dirId = treeNode.id;
					grid.set('newPage', 1);
					grid.set('url', '${ctx}/udip/library/all.json?'
							+ (dirId == null ? "" : 'dirId=' + dirId));

				}
			}
		});
	}

	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : '日期字段',
				name : "dataDate",
				newline : false,
				type : "date",
				cssClass : "field",
				attr : {
					op : "=",
					field : "dataDate"
				}
			} ]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : '100%',
			columns : [ {
				hide : 1,
				name : 'libId',
				width : "0.5%"
			}, {
				display : '字典名称',
				name : 'libName',
				width : "25%",
				align : 'left'
			}, {
				display : '字典类型',
				name : 'libType',
				width : '10%',
				align : 'center',
				editor : {
					type : 'select',
					data : [ {
						'id' : '1',
						'text' : '常量'
					}, {
						'id' : '2',
						'text' : '数据库表'
					} ]
				},
				render : function(row) {
					switch (row.libType) {
					case "1":
						return "常量";
					case "2":
						return "数据库表";
					}
				}
			}, {
				display : '数据库表',
				name : 'tableName',
				width : '30%',
				align : 'center'
			}, {
				display : '创建日期',
				name : 'createDate',
				width : '26%',
				align : 'center'
			} ],
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			isScroll : false,
			url : "${ctx}/udip/library/list.json",
			dataType : 'text',
			method : 'get',
			sortName : 'libName',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			toolbar : {},
			isChecked : isChecked,
			onCheckRow : onCheckRow,
			onDblClickRow : function (data, rowindex, rowobj){
				openWin(data.libId);
				grid.reRender();
            },
			onCheckAllRow : onCheckAllRow
		});
	}

	function onCheckRow(checked, rowdata, rowindex, rowDomElement) {
		UDIP.onCheckRow(this, checked, rowindex)
	}
	function onCheckAllRow(checked, grip) {
		UDIP.onCheckAllRow(this);
	}

	function isChecked(rowdata) {
		if ("${id}") {
			var libId = "${id}";
			var roles = libId.split(',');
			return $.inArray(rowdata.libId, roles) != -1;
		}
	}

	function initButtons() {
		items = [ {
			text : '查看',
			click : library_query,
			icon : 'view'
		}, {
			text : '导出',
			click : library_exp,
			icon : 'export'
		} ];
		BIONE.loadToolbar(grid, items, function() {
		});

	}

	// 导出数据字典数据
	function library_exp() {
		achieveIds();
		if (ids.length == 1) {
			var dataDate = document.getElementById('dataDate').value;
			var idsTmp = ids.join(",");
			$
					.ajax({
						cache : false,
						async : true,
						url : "${ctx}/udip/library/findUdipDataById",
						dataType : 'json',
						type : "get",
						data : {
							"libId" : idsTmp
						},
						success : function(data) {
							if (data.dateCol != null) {
								if (dataDate == '') {
									BIONE.tip('请选择日期字段');
								} else {
									$.ajax({
										async : false,
										url : '${ctx}/udip/library/findColumnType',
										data : {
											"dsId" : data.dsId ,
											"tableName" : data.tableName,
											"columnName" : data.dateCol
										},
										success : function(result) {
											if(result!=''){
												BIONE.tip(result);
												return;
											}
											if (!document.getElementById('download')) {
												$('body')
														.append(
																$('<iframe id="download" style="display:none"/>'));
											}
											$("#download").attr(
													'src',
													'${ctx}/udip/library/excel_down?id='
															+ idsTmp + '&dataDate='
															+ dataDate);
										}
									});
								}
							} else {
								if (!document.getElementById('download')) {
									$('body')
											.append(
													$('<iframe id="download" style="display:none"/>'));
								}
								$("#download").attr(
										'src',
										'${ctx}/udip/library/excel_down?id='
												+ idsTmp);
							}

						}
					});

		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行导出');
		} else {
			BIONE.tip('请选择一行进行导出');
		}
	}

	//查看数据字典数据
	function library_query() {
		achieveIds();
		if (ids.length == 1) {
			openWin(ids[0]);
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行预览');
		} else {
			BIONE.tip('请选择一行进行预览');
		}
	}
	function openWin(libId){
		//检查该字典中的数据库是否存在该数据源中
		var queryFlag=true;
		$.ajax({
			async : false,
			url : '${ctx}/udip/library/checkTableExistsById',
			data : {
				"libId" : libId 
			},
			success : function(result) {
				if(result==true){
					BIONE.tip('该字典的数据库表不存在数据源中！');
					queryFlag=false;
				}
			}
		});
		if(queryFlag==true){
			var dataDate = document.getElementById('dataDate').value;
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/udip/library/findUdipDataById",
				dataType : 'json',
				type : "get",
				data : {
					"libId" : libId
				},
				success : function(data) {
					if (data.dateCol != null) {
						if (dataDate == '') {
							BIONE.tip('请选择日期字段');
						} else {
							$.ajax({
								async : false,
								url : '${ctx}/udip/library/findColumnType',
								data : {
									"dsId" : data.dsId ,
									"tableName" : data.tableName,
									"columnName" : data.dateCol
								},
								success : function(result) {
									if(result!=''){
										BIONE.tip(result);
										return;
									}
									BIONE.commonOpenDialog("数据字典内容查看",
											"libraryAddWin", "900", "480",
											"${ctx}/udip/library/queryHis?libId="
													+ libId + "&dataDate="
													+ dataDate);
								}
							});
						}
					} else {
						BIONE.commonOpenDialog("数据字典内容查看",
								"libraryAddWin", "900", "480",
								"${ctx}/udip/library/queryHis?libId="
										+ libId + "&dataDate=");
					}
				}
			});
		}
	}
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].libId);
		}
	}

	function f_reload() {
		var manager = $("#maingrid").ligerGetGridManager();
		manager.loadData();
	}
</script>

<title>数据字典</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">数据字典目录</span>
	</div>
	<div>
		<input type="text" id="txt2" value="" />
	</div>
</body>
</html>