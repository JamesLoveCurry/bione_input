<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template54.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var tableInfo, createTabSql, colUpdateSql, tableNames = '', tableTmepNames = '', buttons = [];
	var parentTableId = parent.getTableId();
	$(function() {
		BIONE.showLoading("页面正在加载，请稍候...");
		$("#mainsearch").hide();
		tableInfo = parent.getTableInfo();
		initbutton();
		$.ajax({
			url : "${ctx}/rpt/input/table/getTableInfoSql",
			dataType : 'json',
			type : "post",
			data : {
				"dsId" : tableInfo.dsId,
				"tableId" : tableInfo.tableId,
				"tableEnName" : tableInfo.tableEnName,
				"tableCnName" : tableInfo.tableCnName,
				"tableType" : tableInfo.tableType,
				"tableColInfo" : tableInfo.tableColInfo,
				"tableIndexInfo" : tableInfo.tableIndexInfo,
				"defaultValue" : tableInfo.defaultValue
			},
			success : function(result) {
				createTabSql = result.createTabSql;
				colUpdateSql = result.colUpdateSql;
				$("#createTabSql").val(createTabSql);
				$("#colUpdateSql").val(colUpdateSql);
			}
		});
		BIONE.hideLoading();
	});

	function initbutton() {
		buttons.push({
			text : '取消',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '上一步',
			onclick : upset
		});
		buttons.push({
			text : '执行建表sql',
			onclick : createTable
		});
		buttons.push({
			text : '执行字段变更sql',
			width : '100px',
			onclick : colUpdateSql
		});
		/*
		buttons.push({
			text : '创建接口表',
			
			onclick : createTempTable
		});*/
		BIONE.addFormButtons(buttons,true);
	}

	function cancleCallBack() {
		parent.closeDsetBox();
	}

	function upset() {
		if (parentTableId != '' && tableNames != '') {
			BIONE.tip("重建补录表后，需重新打开修改表页面！");
		} else {
			parent.setIsFreshTableCol(false);
			parent.next('3');
		}
	}

	function createTempTable() {
		if (tableInfo.tableEnName.length > 26) {
			BIONE.tip("创建接口表时，表名和表类型前缀的总长度不能超过26,因为接口表需加'MID_'(4位长度)的前缀，请修改表名长度！");
			return;
		}
		if (parentTableId != '') {
			$.ligerDialog.confirm('重建接口表将会删除表中的所有数据，是否需要创建!', function(yes) {
				if (yes == true) {
					if (tableTmepNames == '') {
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/rpt/input/table/createTempTableInfo",
							dataType : 'json',
							type : "post",
							data : {
								"dsId" : tableInfo.dsId,
								"tableEnName" : tableInfo.tableEnName,
								"tableCnName" : tableInfo.tableCnName,
								"tableColInfo" : tableInfo.tableColInfo,
								"tableIndexInfo" : tableInfo.tableIndexInfo,
								"tableId" : parentTableId
							},
							success : function(resultMap) {
								if (resultMap.result == '') {
									tableTmepNames = tableInfo.tableEnName;
									BIONE.tip("建接口表成功！");
								} else {
									BIONE.commonOpenDialog('保存失败',"tableUpdateError","570","440", '${ctx}/rpt/input/table/tableUpdateError?result='
														+ encodeURIComponent(encodeURIComponent(resultMap.result)));
								}
							},
							error : function(result, b) {
								BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
							}
						});
					} else {
						BIONE.tip("该接口表已经创建成功！");
					}
				}
			});
		} else {
			if (tableNames != '') {
				if (tableTmepNames == '') {
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/rpt/input/table/createTempTableInfo",
						dataType : 'json',
						type : "post",
						data : {
							"dsId" : tableInfo.dsId,
							"tableEnName" : tableInfo.tableEnName,
							"tableCnName" : tableInfo.tableCnName,
							"tableColInfo" : tableInfo.tableColInfo,
							"tableIndexInfo" : tableInfo.tableIndexInfo,
							"tableId" : parentTableId
						},
						success : function(resultMap) {
							if (resultMap.result == '') {
								tableTmepNames = tableInfo.tableEnName;
								BIONE.tip("建接口表成功！");
							} else {
								BIONE.commonOpenDialog('保存失败',"tableUpdateError","570","440", '${ctx}/rpt/input/table/tableUpdateError?result='
										+ encodeURIComponent(encodeURIComponent(resultMap.result)));
							}
						},
						error : function(result, b) {
							BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					});
				} else {
					BIONE.tip("该接口表已经创建成功！");
				}
			} else {
				BIONE.tip("请先创建补录表！");
			}

		}
	}

	function createTable() {
		if (parentTableId != '') {
			$.ligerDialog.confirm('执行建表SQL将会删除表中的所有数据重新建表，是否需要执行!', function(yes) {
				if (yes == true) {
					if (tableNames == '') {
						$.ajax({
							async : false,
							url : "${ctx}/rpt/input/table/createTableInfo",
							dataType : 'json',
							data : {
								"dsId" : tableInfo.dsId, "createTableSql" : createTabSql, "tableId" : parentTableId, "tableName" : tableInfo.tableEnName
							},
							type : "post",
							success : function(resultMap) {
								if (resultMap.result == '') {
									tableNames = tableInfo.tableEnName;
									parent.saveTableInfo("update");//保存表信息
								} else {
									BIONE.commonOpenDialog('保存失败',"tableUpdateError","570","440", '${ctx}/rpt/input/table/tableUpdateError?result='
											+ encodeURIComponent(encodeURIComponent(resultMap.result)));
								}
							},
							error : function(result, b) {
								BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
							}
						});
					} else {
						BIONE.tip("补录表创建成功！");
					}
				}
			});
		} else {
			if (tableNames == '') {
				$.ajax({
					async : false,
					url : "${ctx}/rpt/input/table/createTableInfo",
					dataType : 'json',
					data : {
						"dsId" : tableInfo.dsId, "createTableSql" : createTabSql, "tableId" : parentTableId, "tableName" : tableInfo.tableEnName
					},
					type : "post",
					success : function(resultMap) {
						if (resultMap.result == '') {
							parent.saveTableInfo("create");//保存表信息
						} else {
							BIONE.commonOpenDialog('保存失败',"tableUpdateError","570","440", '${ctx}/rpt/input/table/tableUpdateError?result='
									+ encodeURIComponent(encodeURIComponent(resultMap.result)));
						}
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			} else {
				BIONE.tip("补录表创建成功！");
			}
		}
	}
	
	function colUpdateSql() {
		if (colUpdateSql != '') {
			$.ligerDialog.confirm('执行字段变更SQL将修改表字段，是否需要执行!', function(yes) {
				if (yes == true) {
					if (tableNames == '') {
						$.ajax({
							async : false,
							url : "${ctx}/rpt/input/table/colUpdateSql",
							dataType : 'json',
							data : {
								"dsId" : tableInfo.dsId, "colUpdateSql" : colUpdateSql, "tableId" : parentTableId, "tableName" : tableInfo.tableEnName
							},
							type : "post",
							success : function(resultMap) {
								if (resultMap.result == '') {
									tableNames = tableInfo.tableEnName;
									parent.saveTableInfo("update");//保存表信息
								} else {
									BIONE.commonOpenDialog('保存失败',"tableUpdateError","570","440", '${ctx}/rpt/input/table/tableUpdateError?result='
											+ encodeURIComponent(encodeURIComponent(resultMap.result)));
								}
							},
							error : function(result, b) {
								BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
							}
						});
					} else {
						BIONE.tip("表字段变更成功！");
					}
				}
			});
		}else{
			BIONE.tip("字段变更SQL为空，无需执行！");
		}
	}
</script>
</head>
<body>
</body>
</html>