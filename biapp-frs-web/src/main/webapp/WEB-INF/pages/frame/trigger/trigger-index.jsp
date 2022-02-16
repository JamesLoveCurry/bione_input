<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
<script type="text/javascript">
	var grid, items, ids = [];
	$(function() {
		searchForm();
		initGrid();
		initButton();
	});

	function searchForm() {
		//搜索表单应用ligerui样式
		$("#search").ligerForm({
			fields : [ {
				display : '触发器名称',
				name : "triggerName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : 'triggerName',
					op : "like"
				}
			} ]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			width : '100%',
			height : '99%',
			columns : [ {
				display : '触发器名称',
				name : 'triggerName',
				width : "30%",
				align : 'left'
			}, {
				display : '描述',
				name : 'remark',
				width : '64%',
				align : 'left'
			} ],
			checkbox : true,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server', //从后台获取数据
			method : 'post',
			url : "${ctx}/bione/schedule/trigger/list.json",
			sortName : 'triggerId',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			toolbar : {}
		});
	}

	function initButton() {
		items = [ {
			text : '增加',
			click : trigger_add,
			icon : 'fa-plus'
		}, {
			text : '修改',
			click : trigger_update,
			icon : 'fa-pencil-square-o'
		}, {
			text : '删除',
			click : trigger_delete,
			icon : 'fa-trash-o'
		} ];
		BIONE.loadToolbar(grid, items, function() {});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	//角色添加函数
	function trigger_add() {
		BIONE.commonOpenLargeDialog("触发器添加", "triggerAddWin",
				"${ctx}/bione/schedule/trigger/new");
	}

	// 修改
	function trigger_update() {
		achieveIds();
		if (ids.length == 1) {
			BIONE.commonOpenLargeDialog("触发器修改", "triggerModifyWin",
					"${ctx}/bione/schedule/trigger/" + ids[0] + "/edit");
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一行进行修改');
		} else {
			BIONE.tip('请选择一行进行修改');
		}
	}

	// 删除
	function trigger_delete() {
		achieveIds();
		if (ids.length > 0) {
			var idsTmp = ids.join(",");
			$
					.ajax({
						cache : false,
						async : true,
						url : "${ctx}/bione/schedule/trigger/checkHasJobOrNot",
						dataType : 'json',
						type : "post",
						data : {
							"ids" : idsTmp
						},
						success : function(result) {
							var confirmMessage = "";
							if (result == true) {
								//若触发器没被引用
								confirmMessage = "确定删除该触发器吗";
							} else {
								BIONE.tip("触发器正在被作业引用，不允许删除");
								return;
							}
							$.ligerDialog
									.confirm(
											confirmMessage,
											function(yes) {
												if (yes) {
													$
															.ajax({
																cache : false,
																async : true,
																url : "${ctx}/bione/schedule/trigger/destroyOwn",
																type : "post",
																dataType : "json",
																data : {
																	"idStr" : idsTmp
																},
																success : function() {
																	BIONE
																			.tip('删除成功');
																	initGrid();
																}
															});
												}
											});
						}
					});
		} else {
			BIONE.tip('请选择数据');
		}
	}

	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].triggerId);
		}
	}
</script>
</head>
<body>
</body>
</html>