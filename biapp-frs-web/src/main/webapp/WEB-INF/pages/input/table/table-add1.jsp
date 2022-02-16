<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform, parentTableId = parent.getTableId(), tableNameFlag = parent.getTableName();
	var tableInfo = parent.getTableInfo(), globalDsId, oldTableEnName;
	var tabinput = 'INPUT_', tablib = 'LIB_';
	var dsName, dsId;

	/**
	 * @description: 补录表名校验
	 */
	jQuery.validator.addMethod("tableNameValidate", function(value, element) {
		var numReg = /^[a-zA-Z][_a-zA-Z0-9]*$/;
		return this.optional(element) || (numReg.test(value));
	}, "请输入正确的表名,必须以字母开始,只能包括字母数组下划线.");
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			labelWidth : "120",
			inputWidth : "250",
			fields : [ {
				name : "dsId", type : "hidden"
			}, {
				name : "tableTypeId", type : "hidden" 
			},{
				display : "数据来源", name : 'dsName', newline : true, type : 'text',
				validate : {
					required : true, maxlength : 32
				}
			}/*, {
				display : "表类型", name : 'tableTypeId', newline : true, type : 'select', comboboxName: 'tableType',
				options : {
					width : 250,
					data : [ {
						id : '1', text : '补录表'
					}, {
						id : '2', text : '数据字典'
					}, {
						id : '3', text : '其他表'
					} ],
					onSelected : function(val) {
						var tableName = $("#tableEnName").val();
						tableName = $.trim(tableName);
						if (val == '1') {
							$("#tableEnName").val("");
							$("#tableEnName").val(tabinput);
						} else if (val == '2') {
							$("#tableEnName").val("");
							$("#tableEnName").val(tablib);
						} else {
							$("#tableEnName").val("");
						}
					}
				},
				validate : {
					required : true, maxlength : 32
				}
			}*/, {
				display : "表名称", name : 'tableEnName', newline : true, type : 'text',
				validate : {
					// required : true,
					// tableNameValidate: true,
					// maxlength : 30
				}
			}, {
				display : "表中文名称", name : 'tableCnName', newline : true, type : 'text',
				validate : {
					maxlength : 80
				}
			}/* , {
				display : "表空间",
				name : "tableSpace",
				newline : true,
				type : "select",
				comboboxName : "tableSpaceSelect",
				options : {
					onBeforeOpen : showTlsDialog//0809gaofeng修改,
				},
				width : 215
			} */ ]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform, {
			rules: {
				tableEnName: {
					required : true, tableNameValidate: true, maxlength : 30,
					remote: {
						url : "${ctx}/rpt/input/table/tableNameValid",
						type : 'post',
						data : {
							dsId : function() {
								return $('#dsId').val();
							},
							oldTableEnName : function() {
								return oldTableEnName;
							}
						}
					}
				}
			},
			messages: {
				tableEnName: {
					remote: '指定数据源下已存在相同表名'
				}
			}
		});
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				parent.closeDsetBox();
			}
		});
		buttons.push({
			text : '下一步',
			onclick : setTableInfo2
		});
		BIONE.addFormButtons(buttons);

		//新增补录表时给数据源文本框添加点击事件
		if (parent.getTableId() == '') {
			$("#dsName").live("click", function() {
				openNewDilog();
			});
		}

		if (parent.getTableId() != '') {
			$.ajax({
				type : "get",
				dataType : 'json',
				url : "${ctx}/rpt/input/table/getTableInfoById",
				data : {
					"tableId" : parent.getTableId()
				},
				success : function(result) {
					globalDsId = result.dsId;
					$("#mainform input[name='dsId']").val(result.dsId);
					$("#mainform input[name='dsName']").val(result.dataSourceName);
					$.ligerui.get("dsName").setDisabled();
					$("#tableCnName").val(result.tableCnName);
					//$.ligerui.get("tableType").setValue(result.tableType);
					$("#tableEnName").val(result.tableEnName);
					//$.ligerui.get("tableType").setDisabled();
					//gobalTableType = result.tableType;
					$("#dsName").css("color","#333");
					//$("#tableType").css("color","#333");
					oldTableEnName = result.tableEnName;
					$("#mainform input[name='tableSpace']").val(result.tableSpace);
					if($.ligerui.get("tableSpaceSelect")){
						$.ligerui.get("tableSpaceSelect").setText(result.tableSpace);
					}
					$("#tableEnName").attr("readOnly","readOnly");
					// 保存旧的数据源和表名
					var tableInfo = {
						dsId : result.dsId,
						tableName : result.tableEnName
					}
					parent.setOldTableInfo(tableInfo);
				}
			});
			
		}
		if (tableInfo && tableInfo.dsId && tableInfo.tableEnName) {
			$("#mainform input[name='dsId']").val(tableInfo.dsId);
			$("#mainform input[name='dsId']").val(tableInfo.dsName);
			//setDsName(tableInfo.dsId);
			$("#tableEnName").val(tableInfo.tableEnName);
			$("#tableCnName").val(tableInfo.tableCnName);
			//$.ligerui.get("tableTypeId").setValue(tableInfo.tableType);
			//gobalTableType = tableInfo.tableType;
			if (tableNameFlag == tableInfo.tableEnName) {//如果已经建表成功，则设置数据源和表名不可改
				$.ligerui.get("dsName").setDisabled();
				//$.ligerui.get("tableTypeId").setDisabled();
				$.ligerui.get("tableEnName").setDisabled();
			}
			if(tableInfo.tableSpace){
				$("#mainform input[name='tableSpace']").val(tableInfo.tableSpace);
				$.ligerui.get("tableSpaceSelect").setText(tableInfo.tableSpace);
			}
		}
	});

	function setDsName(dsId) {
		$.ajax({
			type : "get",
			dataType : 'json',
			url : "${ctx}/rpt/input/table/getDatSourceList.json",
			data : {
				"dsId" : dsId
			},
			success : function(result1) {
				if (result1 != null && result1.length > 0) {
					$("#mainform input[name='dsName']").val(result1[0].text);
				}
			}
		});
	}

	function setDsInfo() {
		var dsId = document.getElementById("dsId").value;
		var tableName = document.getElementById('tableEnName').value;
		tableName = $.trim(tableName).toUpperCase();
		if (globalDsId != '' && globalDsId != dsId) {
			$.ligerDialog.confirm('修改数据源后，将重建补录表，是否删除之前数据源中的补录表和接口表?', function(yes) {
				$.ajax({
					type : "get",
					dataType : 'json',
					url : "${ctx}/rpt/input/table/updateDsId.json",
					data : {
						"dsId" : dsId,
						"tableId" : parent.getTableId(),
						"delete" : yes
					},
					success : function(result) {
						//代表该表已经修改数据源，故不能进行修改字段，需重新生成补录表
						parent.setIsUpdateDsId(true);
						globalDsId = dsId;
						setTableInfo();
					},
					error : function(result, b) {
						parent.setIsUpdateDsId(true);
						globalDsId = dsId;
						setTableInfo();
					}
				});
			});
		}
	}

	function setTableInfo2() {
		var dsId = $("#dsId").val();
		if (parent.getTableId() != '' && globalDsId != '' && globalDsId!=dsId) {
			setDsInfo();
		} else {
			parent.setIsUpdateDsId(false);
			setTableInfo();
		}
	}

	function setTableInfo() {
		BIONE.submitForm($("#mainform"), function(text) {
			var tableName = $("#tableEnName").val();
			if (tableName == null) {
				tableName = '';
			}
			tableName = $.trim(tableName).toUpperCase();
			var re = /[^u4e00-u9fa5]/;
			if (re.test(tableName)) {
				BIONE.tip("表名不能含有中文字符或者'-'字符！");
				return;
			}
			var dsId = $('#dsId').val();
			var tableType ="1";/*$('#tableTypeId').val();
			
			if (tableType == '1' && tableName.indexOf(tabinput) != 0) {
				BIONE.tip('补录表名没有以' + tabinput + '为前缀开头，请修改表名');
				return;
			}
			if (tableType == '2' && tableName.indexOf(tablib) != 0) {
				BIONE.tip('数据字典表名没有以' + tablib + '为前缀开头，请修改表名');
				return;
			}
			if (tableType == '3' && (tableName.indexOf(tabinput) == 0 || tableName.indexOf(tablib) == 0)) {
				BIONE.tip('其它表名不需要以' + tabinput + '或者' + tablib + '为前缀开头，请修改表名');
				return;
			}
			*/
			if (tableName.length > 21) {
				BIONE.tip('表名和表类型前缀的总长度不能超过30,请修改表名长度！');
				return;
			}
			if (tableNameFlag == '' || tableNameFlag != tableName) {
				$.ajax({
					type : "get",
					dataType : 'json',
					url : "${ctx}/rpt/input/table/checkTableName?d="+new Date().getTime(),
					beforeSend: function(){
						BIONE.showLoading("正在校验数据，请稍等。。。")
					},
					data : {
						"dsId" : dsId,
						"tableEnName" : tableName,
						"tableId" : parent.getTableId()
					},
					success : function(result1) {
						if (result1 == false) {
							BIONE.tip('表名在数据库中已存在，请重新输入！');
							return;
						} else {
							var info = {
								"dsId" : $('#dsId').val(),
								"tableType" : tableType,
								"tableEnName" : tableName,
								"tableCnName" : $('#tableCnName').val(),
								"tableId" : parentTableId,
								"tableColInfo" : "",
								"tableIndexInfo" : "",
								"update" : "",
								tableSpace:$('#tableSpaceSelect').val()
							};
							parent.setTableInfo_tab1(info);
							parent.next('2');
						}
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					},
					complete: function(){
						BIONE.hideLoading();
					}
				});
			} else {
				var info = {
					"dsId" : $('#dsId').val(),
					"dsName" : $('#dsName').val(),
					"tableType" : tableType,
					"tableEnName" : tableName,
					"tableCnName" : $('#tableCnName').val(),
					"tableId" : parentTableId,
					"tableColInfo" : "",
					"tableIndexInfo" : "",
					"update" : "",
					tableSpace:$('#tableSpaceSelect').val()
				};
				parent.setTableInfo_tab1(info);
				parent.next('2');
			}
		});
	}

	function openNewDilog() {
		dsName = $("#dsName");
		dsId = $("#dsId");
		dialog = BIONE.commonOpenDialog('选择数据源', "dsList", "850", "370",
				"${ctx}/bione/mtool/datasource/chackDS/2");
	}
	//对输入信息的提示
	function check() {
		/*
		$("#tableTypeId").change(function() {
			checkLabelShow(TableRemark.global.tableType);
			$("#checkLabelContainer").html(GlobalRemark.title + TableRemark.global.tableType);
		});
		*/
		$("#tableName").focus(function() {
			checkLabelShow(TableRemark.global.tableName);
			$("#checkLabelContainer").html(GlobalRemark.title + TableRemark.global.tableName);
		});
		$("#tableComment").focus(function() {
			checkLabelShow(TableRemark.global.tableComment);
			$("#checkLabelContainer").html(GlobalRemark.title + TableRemark.global.tableComment);
		});
	}
	
	function showTlsDialog(){

		var options = {
			url : "${ctx}/rpt/input/table/showTableSpace?dsId="+$('#dsId').val()+"&d="+new Date().getTime(),
			dialogname : 'tableSpaceBox',
			title : '选择表空间',
			comboxName : 'tableSpaceBoxbBox'
		};
		BIONE.commonOpenIconDialog(options.title, options.dialogname,
				options.url, options.comboxName);
		return false;
	}
</script>
</head>
<body>
	<div id="template.center">
		<form method="post" id="mainform" action=""></form>
	</div>
</body>
</html>