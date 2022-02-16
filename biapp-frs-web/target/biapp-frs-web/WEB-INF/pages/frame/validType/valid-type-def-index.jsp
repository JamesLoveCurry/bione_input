<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, url, ids = [];

	$(function() {
		url = "${ctx}/bione/admin/validTypeDef/list.json";
		searchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "校验标识",
				name : "objDefNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "=",
					field : "def.objDefNo"
				}
			}, {
				display : "校验名称",
				name : "objDefName",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "def.objDefName"
				}
			} ]
		});

	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid(
				{
					height : '100%',
					width : '100%',
					columns : [
							{
								display : '校验标识',
								name : 'objDefNo',
								align : 'center',
								width : '20%'
							},
							{
								display : '校验名称',
								name : 'objDefName',
								align : 'center',
								width : '20%'
							},
							{
								display : '实现类名称',
								name : 'beanName',
								align : 'center',
								width : '20%'
							},
							{
								display : '备注',
								name : 'remark',
								align : 'center',
								width : '33%'
							} ],
					checkbox : true,
					usePager : true,
					isScroll : false,
					rownumbers : true,
					alternatingRow : true,//附加奇偶行效果行
					colDraggable : true,
					dataAction : 'server',//从后台获取数据
					method : 'post',
					url : url,
					sortName : 'objDefOrder', //第一次默认排序的字段
					sortOrder : 'asc',
					toolbar : {}
				});
	}

	function initButtons() {
		btns = [ {
			text : '增加',
			click : def_add,
			icon : 'fa-plus'
		}, {
			text : '修改',
			click : def_modify,
			icon : 'fa-pencil-square-o'
		}, {
			text : '删除',
			click : def_delete,
			icon : 'fa-trash-o'
		} ];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}

	function def_add(item) {
		BIONE.commonOpenDialog('添加校验', 'defManage',590,300,'${ctx}/bione/admin/validTypeDef/new');
	}
	
	function def_modify(item) {
		achieveIds();
		if (ids.length == 1) {
			var buttons = [
					{
						text : '保存',
						onclick : function(item, dialog) {
							BIONE.submitForm($("#mainform",dialog.frame.window.document), function() {
								dialog.close();
								initGrid();
								parent.BIONE.tip('修改成功');
							}, function() {
								BIONE.tip('保存失败');
							});
						}
					}, {
						text : '取消',
						onclick : function(item, dialog) {
							dialog.close();
						}
					} ];
			BIONE.commonOpenDialog('修改校验', 'defManage',590,300,
					'${ctx}/bione/admin/validTypeDef/' + ids[0] + '/edit', buttons);
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}
	
	function def_delete(item) {
		achieveIds();
		if (ids.length > 0) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if(yes) {
					var flag = false;
					$.ajax({
						async : false,
						type : "POST",
						url : "${ctx}/bione/admin/validTypeDef/" + ids.join(','),
						dataType : "script",
						success : function() {
							flag = true;
						}
					});
					if (flag == true) {
						BIONE.tip('删除成功');
						initGrid();
					} else {
						BIONE.tip('删除失败');
					}
				}
			});
		} else {
			BIONE.tip('请选择记录');
		}
	}
	
	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for(var i in rows) {
			ids.push(rows[i].objDefNo);
		}
	}
</script>
</head>
<body>
</body>
</html>