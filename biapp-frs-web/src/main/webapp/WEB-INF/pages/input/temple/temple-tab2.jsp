<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template1_6.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/temple.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid, btns, url, ids = [], dialog, buttons = [];
	var rowSize = 0, savaType = window.parent.savaType;
	var allowInputLowermanager;
	var allowInputLowerState = "0", setValue = "0", columnSpit = "#@#",reflashOrNot="yes" ;
	var dataZidian2;
	var libMap = {};
	$(function() {
		url = "${ctx}/rpt/input/data/getTableMoreList?templeId=${id}";
		initGrid();
		searchForm();
		allowInputLowermanager = $("#allowInputLower").ligerCheckBox({
			disabled : false
		});
		setCss();
		initbutton();

		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '编辑主键',
			icon : 'save',
			click : function() {
				var dsId = document.getElementById('dsId').value;
				if (dsId == null || dsId == "") {
					BIONE.tip('请先选择数据源。');
					return;
				}
				var tableId = document.getElementById('tableNameId').value;
				if (tableId == null || tableId == "") {
					BIONE.tip('请先选择数据表。');
					return;
				}
				$.ajax({
					async : false,
					url : '${ctx}/rpt/input/data/getColumnList/' + "${id}?d=" + new Date().getTime(),
					success : function(data1) {
						var manager = $("#maingrid").ligerGetGridManager();
						var data = manager.getData();
						if (data1.length == 0) {
							BIONE.tip('此操作需要先保存模板信息。');
							return;
						} else if (data1.length != data.length) {
							BIONE.tip('此操作需要先保存模板信息。');
							return;
						} else {
							if (!window.parent.lookType) {
								BIONE.commonOpenDialog("编辑主键", "templeKey", $(document).width(), $(document).height(),
										"${ctx}/rpt/input/temple/templeKey?id=" + "${id}&d=" + new Date().getTime());
							} else {
								BIONE.commonOpenDialog("编辑主键", "templeKey", $(document).width(), $(document).height(),
										"${ctx}/rpt/input/temple/templeKey?id="
												+ "${id}"
												+ "&lookType=lookType&d="
												+ new Date().getTime());
							}
						}
					}
				});
			}
		});
		/*
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '数据预加载设置',
			icon : 'save',
			width : '110px',
			click : function() {
				var dsId = document.getElementById('dsId').value;
				if (dsId == null || dsId == "") {
					BIONE.tip('请先选择数据源。');
					return;
				}
				var tableId = document.getElementById('tableNameId').value;
				if (tableId == null || tableId == "") {
					BIONE.tip('请先选择数据表。');
					return;
				}
				$.ajax({
					async : false,
					url : '${ctx}/rpt/input/data/getColumnList/' + "${id}",
					success : function(data1) {
						var manager = $("#maingrid").ligerGetGridManager();
						var data = manager.getData();
						if (data1.length == 0) {
							BIONE.tip('此操作需要先保存模板信息。');
							return;
						} else if (data1.length != data.length) {
							BIONE.tip('此操作需要先保存模板信息。');
							return;
						} else {
							if (!window.parent.lookType) {
								BIONE.commonOpenDialog("编辑预加载信息", "templeKey",
										"750", "380",
										"${ctx}/rpt/input/temple/templeDataLoad?id="
												+ "${id}&d=" + new Date());
							} else {
								BIONE.commonOpenDialog("编辑预加载信息", "templeKey",
										"750", "380",
										"${ctx}/rpt/input/temple/templeDataLoad?id="
												+ "${id}"
												+ "&lookType=lookType&d=" + new Date());
							}
						}
					}
				});
			}
		});
		*/
		if (window.parent.lookType) {
			savaType = 1;
		}

		if ("${id}") {
			$.ajax({
				url : "${ctx}/rpt/input/temple/findTempleInfo?templeId=" + "${id}" + "&d=" + new Date().getTime(),
				data : {
					id : "${id}"
				},
				success : function(data) {
					if (data.allowInputLower == "yes") {
						allowInputLowermanager.setValue(true);
					}
					$("#search [name='dsId']").val(data.dsId);
					$("#search [name='dsName']").val(data.dsName);
					selectPopupEdit('dsName', data.dsName, data.dsId);
					$("#search [name='tableName']").val(data.tableEnName);
					$("#search [name='tableNameId']").val(data.tableEnName);
					selectPopupEdit('tableName', data.tableEnName, data.tableEnName);
				}
			});
		}
		check();
		window.UDIP.heart("${ctx}/datainput/heart/on");//心跳包
	});
	//根据ID查找该补录模板的表字段
	function changerList() {
		var manager = $("#maingrid").ligerGetGridManager();
		var id = "${id}";
		manager.setOptions({
			parms : [ {
				name : 'id',
				value : id
			} ]
		});
		manager.loadData();
	}
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "补录数据源", name : 'dsName', newline : true, type : 'popup', width : 140, space : 10, labelWidth : 100,
				options : {
					openwin : true, valueFieldID : "dsId", onButtonClick: openDsNewDilog
				},
				validate : {
					required : true, maxlength : 256
				}
			}, {
				display : "补录表", name : 'tableName', newline : false, type : 'popup', width : 140, space : 10, labelWidth : 100, textFieldID: 'tableName',
				options : {
					openwin : true, valueFieldID : "tableNameId", onButtonClick: openTableNewDilog
				},
				validate : {
					required : true, maxlength : 256
				}
			}/*, {
				display : "可否补录下级", name : 'allowInputLower', width : 100, space : 10, labelWidth : 100, newline : false, type : 'checkbox'
			}*/ ]
		});
		$("#dsName").ligerComboBox({
			onBeforeOpen : function() {
				if (!window.parent.lookType) {
					openDsNewDilog();
				}
				return false;
			}
		});
		$("#tableName").ligerComboBox({
			onBeforeOpen : function() {
				if (!window.parent.lookType) {
					openTableNewDilog();
				}
				return false;
			}
		});
	}
	function getColumnList() {
		var rdbId = document.getElementById('dsId').value;
		var tableName = liger.get('tableName').getText();
		var manager = $("#maingrid").ligerGetGridManager();
		var id = "${id}";
		manager.setOptions({
			parms : [ {
				name : 'dsId', value : rdbId
			}, {
				name : 'tableName', value : tableName
			} ]
		});
		manager.loadData();
		//return manager.getData().length;
	}
	function openDsNewDilog() {
		dsName = $("#dsName");
		dsId = $("#dsId");
		dialog = BIONE.commonOpenDialog('选择数据源', "dsList", "850", "370", "${ctx}/bione/mtool/datasource/chackDS/1");
	}
	function openTableNewDilog() {
		var id = document.getElementById('dsId').value;
		if (id == null || id == "") {
			BIONE.tip('请先选择数据源。');
			return;
		}
		tableName = $('#' + liger.get('tableName').textFieldID);
		tableNameId = $("#tableNameId");
		dialog = BIONE.commonOpenDialog('选择数据表', "tableList", "700", "350",
				"${ctx}/rpt/input/temple/tableListForTemple/" + id);
	}
	function initbutton() {
		buttons.push({
			text : '关闭', onclick : cancleCallBack
		});
		buttons.push({
			text : '下一步', onclick : next
		});
		buttons.push({
			text : '上一步', onclick : upset
		});
		if (!window.parent.lookType) {
			buttons.push({
				text : '保存', onclick : save_obj
			});
		}
		BIONE.addFormButtons(buttons,true);
	}
	function initGrid() {
		grid = manager = $("#maingrid").ligerGrid({
			width : '100%',
			columns : [
					{
						name : 'dictId',
						//hide : 1,
						width : '0%',
						frozen : true
					},{
						name : 'dictName',
						//hide : 1,
						width : '0%',
						frozen : true
					},
					{
						name : 'fieldDetail',
						//hide : 1,
						width : '0%',
						frozen : true
					},
					{
						//hide : 1,
						name : 'allowEdit',
						width : '0%',
						frozen : true
					},
					{
						display : '序号',
						name : 'orderNo',
						align : 'center',
						width : '4%',
						editor : {
							type : 'int',
							minValue : 1
						}
					},
					{
						display : '字段名称',
						name : 'fieldEnName',
						align : 'center',
						width : '13%'
					},
					{
						display : '中文名',
						name : 'fieldCnName',
						align : 'center',
						width : '13%'
					},
					{
						display : '类型',
						name : 'fieldType',
						align : 'center',
						width : '10%'
					},
					{
						display : '长度',
						name : 'fieldLength',
						align : 'center',
						width : '4%'
					},
					{
						display : '小数位',
						name : 'decimalLength',
						align : 'center',
						width : '5%'
					},
					{
						align : 'center',
						display : '说明',
						name : "setshuoming",
						width : '7%',
						render : function(rowdata, rowindex,
								value) {
							var h = "<a id='detail_"+rowindex+"' href='javascript:setDetail("+ rowindex+ ")'>"+((rowdata.fieldDetail == null)?'输入':'修改')+"</a> ";
							return h;
						}
	
					},
					{
						display : '不可为空',
						name : 'allowNull',
						isSort : true,
	
						width : '7%',
						render : function(rowdata, rowindex,
								value, column) {
							var h = '<input onclick="nullCheck()"  name="checkbox" type="checkbox"  checked="checked" id = "nullable_' + rowdata['fieldEnName'] + '" columnname = "' + column.name + '"/>';
							return h;
						}
					},
					{
						display : '可筛选',
						name : 'allowSift',
						isSort : true,
						hide : true,
						width : '7%',
						render : function(rowdata, rowindex,
								value, column) {
							var h = '<input onclick="shooseCheck()"  name="checkbox" type="checkbox"  checked="checked" id = "selectable_' + rowdata['fieldEnName'] + '" columnname = "' + column.name + '"/>';
							return h;
						}
					},
					{
						display : '可补录',
						name : 'allowInput',
						isSort : true,
						width : '7%',
						render : function(rowdata, rowindex,
								value, column) {
							var h = '<input onclick="inputCheck()"  name="checkbox" type="checkbox"  checked="checked" id = "writable_' + rowdata['fieldEnName'] + '" columnname = "' + column.name + '"/>';
							return h;
						}
					},
					{
						display : '是否脱敏',
						name : 'isDesen',
						isSort : true,
						width : '7%',
						render : function(rowdata, rowindex,
								value, column) {
							var h = '<input onclick="desenCheck()"  name="checkbox" type="checkbox"  id = "desen_' + rowdata['fieldEnName'] + '" columnname = "' + column.name + '"/>';
							return h;
						}
					},

					{
						align : 'center',
						display : '数据字典',
						width : '10%',
						render : function(rowdata, rowindex,
								value) {
							var txt = rowdata.dictName;
							if(!rowdata.dictId || rowdata.dictId == "undefined" || !txt || txt=='undefined'){
								txt = '未设置';
							}
							var h = "<a id='lib_"+rowindex+"' href='javascript:getDataZidian("+ rowindex+ ")'>"+txt+"</a> ";
							return h;
						}
					},
					{
						align: 'center',
						name : 'searchType',
						display: '查询类型',
						width: '7%',
						editor: {
							type: 'select',
							data: [{
								id: "hidden",
								text: "隐藏"
							}, {
								id: "text",
								text: "文本"
							}, {
								id: "date",
								text: "日期"
							}/*, {
								id: "select",
								text: "下拉框"
							}, {
								id: "number",
								text: "数值"
							}*/],
							valueField : "id",
							textField : "text"
						},
						render: function (rowdata, rowindex, value) {
							switch (value) {
								case "text" :
									return "文本";
								case "hidden" :
									return "隐藏";
								case "date" :
									return "日期";
								case "number" :
									return "数值";
								case "select" :
									return "下拉框";
								default :
									return "文本";
							}
						}
					},/*
					{
						display : '机构字段',
						name : 'orgColumn',
						width : '10%',
						render : function(rowdata, rowindex,
								value, column) {
							var h = '<input onclick="CookieGroup('+rowindex+')" name="checkbox" type="checkbox"  id = "orgColumn_' + rowdata['fieldEnName'] + '" columnname = "' + column.name + '"/>';
							return h;
						}
					},*/
					{
						display : '操作',
						isSort : true,
	
						width : '5%',
						render : function(rowdata, rowindex,
								value) {
							var h = "<a href='javascript:deleteRow("
									+ rowindex + ",\"" + rowdata.fieldEnName + "\")'>删除</a> ";
							return h;
						},
						onCheckRow : function(checked, rowdata,
								rowindex, rowDomElement) {
							checked == ture;
						}
					} ],
			checkbox : false,
			usePager : false,
			enabledEdit : true,
			clickToEdit : true,
			resizable : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : url,
			frozen: true, 
			onAfterShowData : function(){
				var data = manager.getData();
				for (i = 0; i < data.length; i++) {
					if (data[i].allowNull == "0" && setValue == "0") {
						$('#nullable_' + data[i].fieldEnName).removeAttr("checked");
					}
					if (data[i].allowSift == "0"&& setValue == "0") {
						$('#selectable_'+ data[i].fieldEnName).removeAttr("checked");
					}
					if(data[i].allowInput == "0" && setValue == "0"){
						$('#writable_'+ data[i].fieldEnName).removeAttr("checked");
					}

					if (data[i].isDesen == "0"&& setValue == "0") {
						$('#desen'+ data[i].fieldEnName).removeAttr("checked");
					}

					if (data[i].logicSysNo == data[i].columnName && setValue == "0") {
						$("#orgColumn_"+data[i].fieldEnName).attr("checked", true);
						allowInputLowerState = "1";
						setCss();
					}
					if (data[i].editable == '1') {
						$('#nullable_'+ data[i].fieldEnName).removeAttr("checked");
						$('#nullable_'+ data[i].fieldEnName).on("click", function(){
							return false;
						});
					}
					
				}
				setValue = "1";
			},
			sortName : 'seqNo', //第一次默认排序的字段
			sortOrder : 'asc'
		});
	}
	function CookieGroup(num){
		var data = manager.getData();
		chackOrg(num);
		setCss();
		orgCheck();
	}
	
	function chackOrg(num) {
		var data = manager.getData();
		var chackboxmanager;
		for (i = 0; i < data.length; i++) {
			if (i == num) {
				if (!$("#orgColumn_"+data[i].columnName).attr("checked")) {
					$("#orgColumn_"+data[i].columnName).removeAttr("checked");
					allowInputLowerState = "0";
				} else {
					$("#orgColumn_"+data[i].columnName).attr("checked", true)
					allowInputLowerState = "1";
				}
			} else {
				$("#orgColumn_"+data[i].columnName).removeAttr("checked");
			}
		}

	}
	function setCss() {
		if (allowInputLowerState == "1") {
			$('#connLower').removeAttr('disabled');
		} else {
// 			allowInputLowermanager.setValue(false);
			$('#connLower').attr('disabled', 'disabled');
		}
	}
	function editTree() {
		BIONE.commonOpenLargeDialog('新建数据源', 'objDefManage', '${ctx}/bione/mtool/datasource/new');
	}
	function next() {
		if("${id}") {
			$.ajax({
				async : false,
				cache : false,
				url : "${ctx}/rpt/input/temple/findTempleKey/${id}.json?d="+new Date().getTime(),
				success : function(ajaxData) {
					var flag= false;
					for(var i=0;i<ajaxData.length;i++){
						if(ajaxData[i].keyType == "primary"){
							flag = true;
							break;
						}
					}
					if (!flag) {
						BIONE.tip('主键为空，请选择主键。');
						return;
					}else{
						if (savaType > 0) {
							$.ajax({
								async : false,
								url : '${ctx}/rpt/input/data/getColumnList/' + "${id}?d=" + new Date().getTime(),
								success : function(data1) {
									var manager = $("#maingrid").ligerGetGridManager();
									var data = manager.getData();
									if (data1.length == 0) {
										BIONE.tip('此操作需要先保存模板信息。');
										return;
									} else if (data1.length != data.length) {
										BIONE.tip('此操作需要先保存模板信息。');
										return;
									} else {
										setParentTab2();
										parent.next('3', '${id}');
										if (window.parent.lookType) {
											savaType = 1;
										} else {
											savaType = 0;
										}
									}
								}
							});
						} else {
							var dsId = $("#dsId").val();
							var tableNameId = $("#tableNameId").val();
							var rows = grid.getData();
							
							if (dsId == "" || dsId == null) {
								BIONE.tip('请选择补录数据源。');
								return;
							}
							
							if (tableNameId == "" || tableNameId == null) {
								BIONE.tip('请选择补录表。');
								return;
							}
							
							if (rows.length <= 0) {
								BIONE.tip('请配置字段信息。');
								return;
							}
							
							$.ligerDialog.confirm('新增或修改后未保存的数据也许会丢失，是否需要保存?', function(yes) {
								if (yes) {
									save_obj();
									parent.next('3', '${id}');
									
								} else {
									setParentTab2();
									parent.next('3', '${id}');
								}
							});
						}
					}
					
				}
			});	
		}
		

	}
	function upset() {
		savaType = 1;
		setParentTab2();
		parent.next('1', '${id}');
	}

	function getchecked(id){
		if(document.getElementById(id)){
			return document.getElementById(id).checked;
		}else{
			return false;
		}
	}
	
	function setParentTab2() {
		var manager = $("#maingrid").ligerGetGridManager();
		var data = manager.getData();
		var rdbId = document.getElementById('dsId').value;
// 		var tableName = document.getElementById('tableName').value;
		var tableName = liger.get('tableName').getText();
		var keyColumns;
		var dataZidian, columnDetail;
		var countOrg = 0;
		var paramStr = "";
		var orgColumnName;
		for (i = 0; i < data.length; i++) {
			if (getchecked('selectable_' + data[i].fieldEnName)) {
				allowSift = "1";
			} else {
				allowSift = "0";
			}
			if (getchecked('writable_' + data[i].fieldEnName)) {
				allowEdit = "1";
			} else {
				allowEdit = "0";
			}
			if (getchecked('nullable_' + data[i].fieldEnName)) {
				allowNull = "1";
			} else {
				allowNull = "0";
			}
			if (getchecked('desen_' + data[i].fieldEnName)) {
				isDesen = "1";
			} else {
				isDesen = "0";
			}

			if (getchecked('orgColumn_' + data[i].fieldEnName)) {
				countOrg = 1;
				orgColumnName = data[i].fieldEnName;
			}
			if (data[i].dictId == null) {
				dictId = "";
			} else {
				dictId = data[i].dictId;
			}
			if (data[i].fieldDetail == null) {
				fieldDetail = "";
			} else {
				fieldDetail = data[i].fieldDetail;
			}
			if (data[i].defaultValue == null) {
				defaultValue = "";
			} else {
				defaultValue = data[i].defaultValue;
			}
			paramStr = paramStr + data[i].fieldEnName + columnSpit
					+ data[i].fieldType + columnSpit + data[i].fieldLength
					+ columnSpit + data[i].decimalLength + columnSpit
					+ data[i].fieldCnName + columnSpit + allowNull
					+ columnSpit + isDesen + columnSpit + allowSift + columnSpit + dictId
					+ columnSpit + fieldDetail + columnSpit + data[i].orderNo+ columnSpit + allowEdit
					//添加默认值 cl
					+ columnSpit + defaultValue + columnSpit + data[i].searchType
					+ ";;"
		}
		if (countOrg == 0) {
			orgColumnName = "";
		}

		var allowInputLower = "";
		if ($("#allowInputLower").attr('checked') == "checked") {
			connLower = "yes";
		} else if (orgColumnName == "") {
			connLower = "";
		} else {
			connLower = "no";
		}

		parent.setTempleInfo_tab2(rdbId, tableName, orgColumnName, allowInputLower, paramStr);
	}

	function cancleCallBack() {
		parent.closeDsetBox();
	}
	function deleteRow(rowid, fieldEnName) {
		$.ajax({
			async : false,
			cache : false,
			url : "${ctx}/rpt/input/temple/findTempleKey/${id}.json?d="+new Date().getTime(),
			success : function(result) {
				if(result && result != null){
					for(var i=0; i<result.length; i++){
						if(result[i].keyColumn.indexOf(fieldEnName) != -1){
							BIONE.showError("该字段已存在于模板的主键信息中，不能删除！");
							return;
						}
					}
				}
				$.ligerDialog.confirm('确实要删除这条记录吗?', function(yes) {
					if (yes) {
						manager.deleteRow(rowid);
					}
				});
			}
		});	
	}
	function getDataZidian(rowid) {
		if (!window.parent.lookType) {
			var data = manager.getData();
			dataZidian2 = data[rowid].dictId;
			if (dataZidian2 == '') {
				dataZidian2 = null;
			}
			dialog = BIONE.commonOpenDialog('选择数据字典', "chackedZidian", $(document).width(), $(document).height() , "${ctx}/rpt/input/library/getDataZidian?id=" + dataZidian2
							+ "&rowid=" + rowid+ "&d=" + new Date().getTime());
		}
	}
	function setDataZidina(libId2, rowid,dictName) {
		if (libId2 == null || libId2 == "") {
			manager.updateCell("dictId", "", manager.getRowObj(rowid))
			document.getElementById('lib_' + rowid).innerHTML = "设置";
		} else {
			manager.updateCell("dictId", libId2, manager.getRowObj(rowid))
			document.getElementById('lib_' + rowid).innerHTML = dictName;
		}
	}

	function setDetail(rowid) {
		if (!window.parent.lookType) {
			var data = manager.getData();
			fieldDetail = data[rowid].fieldDetail;
			$.ligerDialog.prompt = detailController.detailSet('设置描述', fieldDetail, true, function(yes, value) {
				if (yes) {
					manager.updateCell("fieldDetail", value, manager.getRowObj(rowid));
					document.getElementById('detail_'+rowid).innerHTML = value == '' ? "输入" : "修改";
				}
			});
		}
	}
	var detailController = {
		detailSet : function(title, value, multi, callback) {
			var target = $('<input type="text" class="l-dialog-inputtext"/>');
			if (typeof (multi) == "function") {
				callback = multi;
			}
			if (typeof (value) == "function") {
				callback = value;
			} else if (typeof (value) == "boolean") {
				multi = value;
			}
			if (typeof (multi) == "boolean" && multi) {
				target = $('<textarea cols="45" rows="5" class="l-dialog-textarea"></textarea>');
			}
			if (typeof (value) == "string" || typeof (value) == "int") {
				target.val(value);
			}
			var btnclick = function(item, Dialog, index) {
				Dialog.close();
				if (callback) {
					callback(item.type == 'yes', target.val());
				}
			}
			p = {
				title : title,
				target : target,
				width : 320,
				buttons : [ {
					text : $.ligerDefaults.DialogString.ok,
					onclick : btnclick,
					type : 'yes'
				}, {
					text : $.ligerDefaults.DialogString.cancel,
					onclick : btnclick,
					type : 'cancel'
				} ]
			};
			return $.ligerDialog(p);
		}
	}
	function save_obj() {
		// 查询模板是否已包含约束信息
		$.ajax({
			url : "${ctx}/rpt/input/temple/chackPrimaryKey?d=" + new Date().getTime(),
			type : 'get',
			async : true,
			data: {
				templeId: "${id}"
			},
			success : function(data) {
				if (data == "") {	// 即未包含约束信息
					var dsId = $('#dsId').val();
					if (dsId == null || dsId == "") {
						BIONE.tip('请先选择数据源。');
						return false;
					}
					var tableId = liger.get('tableName').getValue();
					var tableName = liger.get('tableName').getText();
					if (tableId == null || tableId == "") {
						BIONE.tip('请先对应数据表。');
						return false;
					}
					// 若模板未包含约束信息, 则将表级主键添加为默认主键约束
					// 若不指定约束, 则模板就不包含约束, 因此去掉以下操作 by xugy at 2015-06-11 16:39:00
					/*$.ajax({
						async : false,
						url : "${ctx}/rpt/input/temple/savaTempleKeyIndex.json?d="+new Date(),
						dataType : 'json',
						data : {
							dsId : dsId,
							tableName : tableId,
							templeId : "${id}"
						},
						type : "post",
						success : function() {
							savaType = 1;
							setParentTab2();
							parent.saveTempleInfo();
							return true;
						},
						error : function(result, b) {
							BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					});*/
					savaType = 1;
					setParentTab2();
					parent.saveTempleInfo();
					return true;
				} else {
					savaType = 1;
					setParentTab2();
					parent.saveTempleInfo();
					return true;
				}
			}
		});
	}
	//对输入信息的提示
	function check() {
		$("#connLower").change(function() {
			checkLabelShow(TempleRemark.global.connLower);
			$("#checkLabelContainer").html(GlobalRemark.title + TempleRemark.global.connLower);
		});
	}

	function orgCheck() {
		checkLabelShow(TempleRemark.global.orgColumn);
		$("#checkLabelContainer").html(GlobalRemark.title + TempleRemark.global.orgColumn);
	}
	
	function nullCheck() {
		checkLabelShow(TempleRemark.global.nullable);
		$("#checkLabelContainer").html(GlobalRemark.title + TempleRemark.global.nullable);
	}
	
	function shooseCheck() {
		checkLabelShow(TempleRemark.global.selectable);
		$("#checkLabelContainer").html(GlobalRemark.title + TempleRemark.global.selectable);
	}
	
	function inputCheck() {
		checkLabelShow(TempleRemark.global.inputColumn);
		$("#checkLabelContainer").html(GlobalRemark.title + TempleRemark.global.inputColumn);
	}

	function desenCheck() {
		checkLabelShow(TempleRemark.global.desen);
		$("#checkLabelContainer").html(GlobalRemark.title + TempleRemark.global.desen);
	}

	function selectPopupEdit(name, text, val) {
		var pop = liger.get(name);
		pop.setText(text);
		pop.setValue(val);
	}
</script>
</head>
<body>
	<div id="content" style="width: 100%; overflow: auto; clear: both;">
		<div id="template.center">
			<form id="mainsearch" action="" method="post"></form>
		</div>
	</div>
</body>
</html>