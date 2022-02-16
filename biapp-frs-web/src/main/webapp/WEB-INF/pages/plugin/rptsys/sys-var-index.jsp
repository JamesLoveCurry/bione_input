<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template1.jsp">
<html>
<head>
<script type="text/javascript">
	var grid;
	function reloadGrid() {
		var manager = $("#maingrid").ligerGetGridManager();
		manager.loadData();
	};
	$(init);

	//初始化函数
	function init() {
		searchForm();
		initGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "变量标识",
				name : "varNo",
				newline : true,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "varNo"
				}
			}, {
				display : "变量名称",
				name : "varNm",
				newline : false,
				labelWidth : 100,
				width : 220,
				space : 30,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					vt : "string",
					field : "varNm"
				}
			} ]
		});

	}

	//初始化Grid
	function initGrid() {

		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [ {
				display : '变量标识',
				name : 'varNo',
				align : 'left',
				width : '15%'
			}, {
				display : '变量名称',
				name : 'varNm',
				align : 'left',
				width : '15%'
			}, {
				display : '变量类型',
				name : 'varType',
				align : 'left',
				width : '15%',
				render: function(a,b,c){
					if(c=="01")
						return "常量";
					if(c=="02")
						return "SQL";
				}
			}, {
				display : '变量值',
				name : 'varVal',
				align : 'left',
				width : '15%',
				render:function(a,b,c){
					if(a.varType=="01")
						return c;
					if(a.varType=="02")
						return "SQL语句";
				}
			}, {
				display : '备注',
				name : 'remark',
				align : 'left',
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
			url : '${ctx}/rpt/frame/rptsys/var/list.json',
			sortName : 'varNo', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	//初始化Button
	function initButtons() {
		var btns;
		btns = [ {
			text : '增加',
			click : var_add,
			icon : 'add',
			operNo : 'var_add'
		}, {
			text : '修改',
			click : var_modify,
			icon : 'modify',
			operNo : 'var_modify'
		}, {
			text : '删除',
			click : var_deleteBatch,
			icon : 'delete',
			operNo : 'var_deleteBatch'
		} ];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}

	//增加系统变量
	function var_add(item) {
		BIONE.commonOpenLargeDialog('添加变量', 'VarAdd',
				'${ctx}/rpt/frame/rptsys/var/new');
	}

	//修改系统变量
	function var_modify(item) {
		var rows = grid.getSelectedRows();
		if (rows.length < 1) {
			BIONE.tip('请选择记录');
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			var id = rows[0].varId;
			BIONE.commonOpenLargeDialog("修改变量", "VarAdd",
					"${ctx}/rpt/frame/rptsys/var/" + id + "/edit");
		}
	}

	//批量删除
	function var_deleteBatch(item) {
		var rows = grid.getSelectedRows();
		var ids = '';
		var i = 0;
		if (rows.length > 0) {
			for (; i < rows.length; i++) {
				ids += rows[i].varId + ',';
			}
			$.ligerDialog
					.confirm(
							'您确定删除这' + i + "条记录吗？",
							function(yes) {
								if (yes) {
									BIONE
											.ajax(
													{
														url : '${ctx}/rpt/frame/rptsys/var/deleteBatch?ids='
																+ ids
													}, function() {
														BIONE.tip('删除成功');
														grid.loadData();
													});
								} else {
									BIONE.tip('取消删除');
								}
							});
		} else {
			BIONE.tip('请选择记录');
		}
	}
</script>

</head>
<body>
</body>
</html>