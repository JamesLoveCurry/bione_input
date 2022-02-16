<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template53.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/table.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform, parentTableId = parent.getTableId(), managers, buttons = [];
	var manager, grid, paramStr, tableInfo = parent.getTableInfo();
	var tableData = {
		Rows : []
	}, keyTypeflag, keyColumnsflag;
	var selection = [], commitflag, colPriType = [], rowobjUdp;
	$(function() {
		initGrid();
		searchForm();
		managers = $("#maingrid").ligerGetGridManager();

		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '添加',
			icon : 'add',
			width : '50px',
			click : function() {
				commitflag = 'add';
				addNewRow();
			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '修改',
			icon : 'modify',
			width : '50px',
			click : function() {
				commitflag = 'update';
				addNewRow();
			}
		});
		initbutton();
		
		//////////////////
		$.ajax({ //获取指定的常量字段可以添加主键索引约束
			async : false,
			url : "${ctx}/rpt/input/table/getColumnForPri.json",
			dataType : 'json',
			type : "get",
			success : function(data) {
				colPriType = data;
			}
		});
		
		setColumnValue();
		
		$("#keyColumnsValue").ligerComboBox({
			onBeforeOpen : function() {
				selection = [];
				setColumnValue();
			}
		});
		if (parentTableId != '') {
			if(parent.getIsFreshTableCol()){
				//修改主键索引后，实时更新表字段信息。如添加主键字段后，该字段将变为不可为空。
				var paramStr = "";
				$.ajax({
					async : false,
					url : "${ctx}/rpt/input/table/getTableColInfoById",
					dataType : 'json',
					type : "post",
					data : {
						"tableId" : parentTableId
					},
					success : function(data) {
						for (i = 0; i < data.length; i++) {
							if (i == data.length - 1) {
								paramStr = paramStr + data[i].colName + ",," + data[i].colType
										+ ",," + data[i].nullable + ",," + data[i].comments + ",,"
										+ data[i].seqNo + ",," + data[i].id;
							} else {
								paramStr = paramStr + data[i].colName + ",," + data[i].colType
										+ ",," + data[i].nullable + ",," + data[i].comments + ",,"
										+ data[i].seqNo + ",," + data[i].id + ";;";
							}
						}
						parent.setTableInfo_tab2(paramStr);
					}
				});
			}
		}
		initSchemaPrimaryKey();
	});
	
	function getIsPrimaryKey(strVal){
		var arrays = strVal.split(",,");
		return arrays[arrays.length-1];
	}
	
	function getColNm(strVal){
		var arrays = strVal.split(",,");
		return arrays[0];
	}
	
	function initSchemaPrimaryKey(){
		var tableInfo = parent.getTableInfo();
		if(tableInfo != null && tableInfo.tableIndexInfo != null && tableInfo.tableIndexInfo != ""){
			var indexs = tableInfo.tableIndexInfo.split(";;");
			for(var i=0; i< indexs.length; i++){
				var indexColumns = indexs[i].split(",,");
				manager.addRow({
					keyType : indexColumns[0],
					keyColumns : indexColumns[1]
				});
			}
		}
	}
	function initbutton() {
		buttons.push({
			text : '取消',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '下一步',
			onclick : next
		});
		buttons.push({
			text : '上一步',
			onclick : upset
		});
		/* if (parentTableId == '') {
			buttons.push({
				text : '保存',
				onclick : save_objDef
			});
		} else {
			buttons.push({
				text : '保存',
				onclick : update_objDef
			});
		} */
		BIONE.addFormButtons(buttons,true);
	}

	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "约束类型",
				name : "keyTypeValue",
				newline : false,
				type : 'select',
				options : {
					valueFieldID : 'keyType',
					data : [ /*{
						id : 'primary',
						text : '主键(primary)'
					},*/ {
						id : 'unique',
						text : '唯一性(unique)'
					}, {
						id : 'index',
						text : '索引(index)'
					} ]
				}
			}, {
				display : "约束字段",
				name : 'keyColumnsValue',
				comboboxName : 'keyColumnsValueBox',
				newline : false,
				type : 'select',
				options : {
					isShowCheckBox : true,
					isMultiSelect : true,
					valueFieldID : 'keyColumns'
				}
			} ]
		})
	}
	function addNewRow() {
		var keyType = document.getElementById('keyType').value;
		var keyColumns = document.getElementById('keyColumns').value;
		var data = manager.getData();
		if (keyType == "" || keyType == null) {
			BIONE.tip('请选择主键类型。');
			return;
		}

		if (keyColumns == "" || keyColumns == null) {
			BIONE.tip('请选择主键字段。');
			return;
		}
		for (i = 0; i < data.length; i++) {
			if (commitflag == 'update'
					&& (keyColumnsflag == null || keyColumnsflag == '')) {
				BIONE.tip('需修改主键索引信息，请先重新双击要修改的主键索引行。');
				return;
			}
			if (commitflag == 'update' && data[i].keyColumns == keyColumnsflag
					&& data[i].keyType == keyTypeflag) {
				continue;
			}
			if (data[i].keyColumns.toUpperCase() == keyColumns.toUpperCase()
					&& data[i].keyType.toUpperCase() == keyType.toUpperCase()) {
				BIONE.tip('不能添加重复的约束条件。');
				return;
			}
			if (data[i].keyColumns == keyColumns && keyType == "index") {
				BIONE.tip('主键或者唯一约束已经包含索引，无需重新添加索引。');
				return;
			}
			if (keyType == data[i].keyType && keyType == 'primary') {
				BIONE.tip('只能添加一个主键。');
				return;
			}
			if ("index" == data[i].keyType && keyType == 'primary'
					&& keyColumns == data[i].keyColumns) {
				BIONE.tip('相同字段主键和索引不能重复，要添加主键，请先删除索引。');
				return;
			}
			if ("unique" == data[i].keyType && keyType == 'primary'
					&& keyColumns == data[i].keyColumns) {
				BIONE.tip('相同字段主键和唯一性约束不能重复，要添加主键，请先删除唯一性约束。');
				return;
			}
			if (keyColumns == data[i].keyColumns
					&& data[i].keyType == 'primary' && keyType == 'unique') {
				BIONE.tip('该字段已经是主键，包含唯一约束，无需重新添加。');
				return;
			}
		}

		if (commitflag == 'add') {
			if (keyType == 'primary' && data != null && data.length > 0) {
				manager.select(manager.records['r1001']);
				var row = manager.getSelectedRow();
				manager.addRow({
					keyType : keyType,
					keyColumns : keyColumns
				}, row, true);
			} else {
				manager.addRow({
					keyType : keyType,
					keyColumns : keyColumns
				});
			}
			rowobjUdp = '';
		} else {
			if (rowobjUdp == '' || rowobjUdp == null) {
				BIONE.tip('需修改主键索引信息，请先重新双击要修改的主键索引行。');
			} else {
				manager.updateCell("keyType", keyType, rowobjUdp);
				manager.updateCell("keyColumns", keyColumns, rowobjUdp);
				keyColumnsflag = keyColumns;
				keyTypeflag = keyType;
			}
		}
		var centerHeight = $("#center").height();
		manager.setHeight(centerHeight - $("#mainsearch").height() - 25);
	}
	
	function setColumnValue() {
		var tableColInfos = parent.getTableColInfo();
		var columns = tableColInfos.split(";;");
		for ( var i = 0; i < columns.length; i++) {
			var tableCol = columns[i].split(",,");
			if (tableCol[3] != '' && tableCol[3] != 'null') {
				selection.push({
					id : tableCol[0],
					text : tableCol[3] + "(" + tableCol[0] + ")"
				});
			} else {
				selection.push({
					id : tableCol[0],
					text : tableCol[0]
				});
			}
		}
		/*
		for ( var i = 0; i < colPriType.length; i++) {
			selection.push({
				id : colPriType[i].id,
				text : colPriType[i].text + "(" + colPriType[i].id + ")"
			});
		}*/
		$.ligerui.get('keyColumnsValueBox').setData (selection);
	}
	
	function deleteRow(rowid) {
		manager.deleteRow(rowid);
	}

	function cancleCallBack() {
		parent.closeDsetBox();
	}
	function save_objDef() {
		setTableInfo();
		parent.saveTableInfo();
	}

	function update_objDef() {
		var isUpdateDsId = parent.getIsUpdateDsId();
		setTableInfo();
		if (isUpdateDsId != '' && isUpdateDsId == true) {
			BIONE.tip('该表已经修改数据源，请先重建补录表和接口表!');
			return;
		}
		BIONE.ajax({
			url : "${ctx}/rpt/input/table/checkTempleTableName?t=" + new Date(),
			data : {
				'dsId' : tableInfo.dsId, 'tableEnName' : tableInfo.tableEnName, 'tableId' : parentTableId
			},
			success : function(result) {
				if (result == false) {
					$.ligerDialog.confirm('现在是修改补录表信息，是否需要同时修改接口表信息(该接口表已经存在数据源中)!', function(yess) {
						parent.setUpdate(yess);
						parent.updateTableInfo();
						parent.setIsFreshTableCol(true);
						parent.next('3');
					});
				} else {
					parent.setUpdate(false);
					parent.updateTableInfo();
					parent.setIsFreshTableCol(true);
					parent.next('3');
				}
			}
		});
	}

	function setTableInfo() {
		paramStr = "";
		data = manager.getData();
		if (data == null || data.length == 0) {
			parent.setTableInfo_tab3("");
		} else {
			for (i = 0; i < data.length; i++) {
				if (i == data.length - 1) {
					paramStr = paramStr + data[i].keyType + ",," + data[i].keyColumns;
				} else {
					paramStr = paramStr + data[i].keyType + ",," + data[i].keyColumns + ";;";
				}
			}
			parent.setTableInfo_tab3(paramStr);
		}
	}

	function upset() {
		setTableInfo();
		parent.next('2');
	}

	function next() {
		/*
		var rows = manager.getData();
		for (var i = 0, len = rows.length; i < len; i++) {
			if (rows[i].keyType == 'primary')
				break;
		}
		if (i == len) {
			BIONE.tip('请设置主键');
			return false;
		} else {
			setTableInfo();
			parent.next('4');
		}*/
		//修改为原主键为唯一约束,建表的真正主键为程序添加的ID
		setTableInfo();
		parent.next('4');
		
	}

	function check(){
		$("#keyTypeValue").focus(function() {
			checkLabelShow(TableRemark.global.keyTypeValue);
			$("#checkLabelContainer").html(GlobalRemark.title+ TableRemark.global.keyTypeValue);
		});
	}
	
	function initGrid() {
		manager = grid = $("#maingrid").ligerGrid({
			columns : [{
				hide : 1, name : 'id'
			}, {
				hide : 1, name : 'tableId'
			}, {
				display : '主键类型', name : 'keyType', width : '25%',
				render : function(rowData) {
					if (rowData.keyType == "primary")
						return "主键(primary)";
					if (rowData.keyType == "unique")
						return "唯一性(unique)";
					if (rowData.keyType == "index")
						return "索引(index)";
					return rowData.fieldType;
				},
				editor : {
					type : 'text'
				}
			}, {
				display : "主键字段", name : 'keyColumns', width : '60%',
				editor : {
					type : 'text'
				}
			}, {
				display : '操作', width : '10%',
				render : function(rowdata, rowindex, value) {
					var h = "";
					if (!rowdata._editing) {
						h += "<a href='javascript:deleteRow(" + rowindex + ")'>删除</a> ";
					}
					return h;
				}
			} ],
			rownumbers : true,
			checkbox : false,
			data : tableData,
			usePager : false,
			onDblClickRow : function(data, rowindex, rowobj) {
				$.ligerui.get("keyTypeValue").setValue(data['keyType']);
				$.ligerui.get("keyColumnsValueBox").setValue(data['keyColumns']);
				keyTypeflag = data['keyType'];
				keyColumnsflag = data['keyColumns'];
				rowobjUdp = rowobj;
			},
			height : '70%',
			width : '99%'
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="" method="post"></form>
	</div>
</body>
</html>