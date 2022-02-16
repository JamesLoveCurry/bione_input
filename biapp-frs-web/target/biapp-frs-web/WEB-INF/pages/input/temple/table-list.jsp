<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript" src="${ctx}/js/datainput/UDIP.js"></script>
<script type="text/javascript">
	var grid;
	var manager;
	var tableList;
	var dialog;
	$(init);

	function init() {
		if ("${id}") {
			$.ajax({
				async : false,
				url : '${ctx}/rpt/input/table/dataTableList.json?id=' + "${id}"+"&d="+new Date().getTime(),
				success : function(data) {
					tableList = {
						Rows: []
					};
					$.each(data.Rows, function(i, n) {
						tableList.Rows.push({
							tableEnName: n.tableEnName,
							tableCnName: n.tableCnName
						});
					});
				}
			});
		}
		searchForm();
		initGrid();
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '查找',
			icon : 'save',
			click : function() {
				f_search();
			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '选择',
			icon : 'true',
			click : function() {
				var selectedRow = grid.getSelecteds();
				if (selectedRow.length == 0) {
					BIONE.tip('请选择行');
					return;
				}
				var tableName = [], tableNameId = []
				for ( var k = 0; k < selectedRow.length; k++) {
					tableName.push(selectedRow[k].tableEnName);
					tableNameId.push(selectedRow[k].tableEnName);
				}
				//var tableName2 = tableName;
				//var tableNameId2 = tableNameId;

				//window.parent.tableName.val(tableName2);
				//window.parent.tableNameId.val(tableNameId2);
				try {
					window.parent.tableEnNameCombox._changeValue(tableNameId,tableName);
				} catch(e) {
				
				}
				try {
					parent.selectPopupEdit("tableName", tableName, tableNameId);
				} catch(e) {
				
				}
				window.parent.tableName.focusout();
				if (window.parent.reflashOrNot) {
					$.ligerDialog.confirm('是否刷新补录表字段？', function(yes) {
						if (yes) {
							var num = window.parent.getColumnList();
							BIONE.closeDialog("tableList");
						} else {
							BIONE.closeDialog("tableList");
						}
					});
				} else {
					var num = window.parent.getColumnList();
					BIONE.closeDialog("tableList");
				}
			}
		});
	}

	//搜索表单
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "表名",
				name : "tableName",
				newline : false,
				type : "text"
			},{
				display : "表中文名",
				name : "tableNameCN",
				newline : false,
				type : "text"
			} ]
		}

		)
	}

	//渲染表单
	function initGrid() {
		grid = manager = $("#maingrid").ligerGrid({
			height : '90%',
			width : '100%',
			columns : [ {
				isSort:true,
				display : "数据表英文名称",
				name : "tableEnName",
				align : 'left',
				minWidth : 100,
				width : "50%"
			},{
				display : "数据表中文名称",
				name : "tableCnName",
				align : 'center',
				minWidth : 100,
				width : "42%"
			} ],
			checkbox : true,
			usePager : false,
			frozen : false,
			isScroll : true,
			alternatingRow : true,
			colDraggable : true,
			data : tableList,
			isChecked : isChecked,
			onCheckRow : onCheckRow,
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
		var tableName = window.parent.liger.get('tableName').getValue();
		var roles = tableName.split(',');
		return $.inArray(rowdata.tableEnName, roles) != -1;
	}

	function f_search() {
		var manager = $("#maingrid").ligerGetGridManager();
		var data2 = tableList;
		grid.set('data', $.extend(true, {}, tableList));
		var obj = $("#tableName").val();
		var obj2 = $("#tableNameCN").val();
		var fnbody = ' return  ' + filterTranslator.translateGroup(data2, obj,obj2);
		grid.loadData(new Function("o", fnbody));

	}
	var filterTranslator = {
		translateGroup : function(group, obj,obj2) {
			var out = [];
			if (group == null) {
				return " 1==1 ";
			}
			out.push('(');
			if(obj != null){
				out.push(this.translateTable(obj, "tableEnName"));
			}
			if(obj != null && obj2!= null){
				out.push('&&');
			}
			if(obj2!= null){
				out.push(this.translateTable(obj2, "tableCnName"));
			}
			out.push(')');
			return out.join('');
		},

		translateTable : function(value, type) {
			var out = [];
			if (value == null || value == "") {
				out.push(' 1==1 ');
				return out.join('');
			}
			out.push('/');
			out.push($.trim(value));
			out.push('/i.test(');
			out.push('o["');
			out.push(type);
			out.push('"]');
			out.push(')');
			return out.join('');
		}
	};
</script>
</head>
<body>
</body>
</html>