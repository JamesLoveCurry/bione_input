<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template1_3.jsp">
<head>
<style type="text/css">
	#caozuo {
		color: #000;
	}
</style>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/rule.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid, btns, url, ids = [], dialog, buttons = [],grid2;
	var udpgrid="0",chackName = "0";
	var comboxManager ;
	var selectedInput = null;
	var selection = [];
	var newSelection = [ {
		id : "",
		text : ""
	} ];
	var selectionFunc = [ {
		id : "",
		text : "请选择"
	} ];
	var listDsMap = {};
	var manager;
	$(function() {

		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/data/getColumnList/' + "${id}",
			success : function(data1) {
				selection = data1;
			}
		});
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/rule/dataRulesFuncCombox2',
			success : function(data) {
				selectionFunc = selectionFunc.concat(data); 
			}
		});
		$.ajax({
			async : false,
			url : '${ctx}/bione/mtool/datasource/listDsInfo',
			success : function(data1) {
				listDsMap = data1;
			}
		});
		var oSelect = document.getElementById('logicCode');
		
		var oOption2 = document.createElement("OPTION");
		oOption2.text = "大于等于";
		oOption2.value = ">=";
		oSelect.add(oOption2);
		var oOption3 = document.createElement("OPTION");
		oOption3.text = "等于";
		oOption3.value = "==";
		oSelect.add(oOption3);
		var oOption7 = document.createElement("OPTION");
		oOption7.text = "不等于";
		oOption7.value = "!=";
		oSelect.add(oOption7);
		var oOption4 = document.createElement("OPTION");
		oOption4.text = "小于";
		oOption4.value = "<";
		oSelect.add(oOption4);
		var oOption5 = document.createElement("OPTION");
		oOption5.text = "小于等于";
		oOption5.value = "<=";
		oSelect.add(oOption5);
		var oOption6 = document.createElement("OPTION");
		oOption6.text = "in";
		oOption6.value = "in";
		oSelect.add(oOption6);
		searchForm();
		if ("${id}") {
			$.ajax({
				url : "${ctx}/rpt/input/temple/findTempleInfo?templeId=${id}",
				success : function(data) {
					$("#search [name='dsId2']").val(data.dsId);
					$("#search [name='tableName2']").val(data.tableEnName);
				}
			});
		}
		initGrid();
		initGrid2();
		manager.deleteRow(0);
		manager2.deleteRow(0);
		if ("${ruleId}") {
			$.ajax({
				async : true,
				url : "${ctx}/rpt/input/rule/findRuleInfo?ruleId=" + "${ruleId}",
				success : function(data) {
					$("#logicCode").val(data.logicCode);
					$("#search [name='ruleNm']").val(data.ruleNm);
					$("#search [name='ruleId']").val(data.ruleId);
					$("#search [name='errorTip']").val(data.errorTip);
				}
			});
		}
		if(!"${lookType}"){
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '添加',
				icon : 'add',
				width : '50px',
				click : function() {
					addNewRow();
				}
			});
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '更新',
				icon : 'modify',
				width : '50px',
				click : function() {
					updateGrid();
				}
			});
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '保存',
				icon : 'save',
				width : '50px',
				click : function() {
					saveRule5();
				}
			});
		}
		
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '关闭',
			icon : 'delete',
			width : '50px',
			click : function() {
				closeWindow();
			}
		});
		setCss();
// 		$("#dsName").ligerComboBox({
// 			onBeforeOpen : function() {
// 				openDsNewDilog();
// 				return false;
// 			}
// 		});

		$("#ruleNm").ligerTextBox({
			onChangeValue : function(){
				var info = {
						"ruleId" : "${ruleId}",
						"ruleNm" : document.getElementById('ruleNm').value,
						"templeId" : "${id}"

					};
				$.ajax({
					async : false,
					url : "${ctx}/rpt/input/rule/checkRuleName",
					dataType : 'json',
					data : info,
					type : "post",
					success : function(data) {
						if(!data){
							chackName = "1";
							BIONE.showError("规则名已存在，请重新填写。");
						}else{
							chackName = "0";
						}
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
					
				}); 
			}
		});
		$("#tabletype").on("change", function() {
			setCss();
		});
		getRuleItemsList();
		comboxManager = liger.get('groupByValue');
		liger.get('functionNameValue').selectValue("");
		check();
// 		$("#caozuo").css({border:"0px solid #F1F1F1",background: "#F1F1F1"});
		$("#queryCondition").attr('title',GlobalRemark.sysParams);
		
	});
	function getRuleItemsList() {
		$.ajax({
			async : false,
			url : "${ctx}/rpt/input/rule/listRuleItem/${ruleId}.json",
			data : {
				ruleId : "${ruleId}"
			},
			success : function(ajaxData) {
				var manager = $("#maingrid").ligerGetGridManager();
				var manager2 = $("#maingrid2").ligerGetGridManager();
				var queryCondition;
				var groupBy;
				for ( var i = 0; i < ajaxData.length; i++) {
					if (ajaxData[i].filterCondition == null
							|| ajaxData[i].filterCondition == '') {
						queryCondition = ''
					} else {
						queryCondition = ajaxData[i].filterCondition;
					}
					if (ajaxData[i].groupField == null
							|| ajaxData[i].groupField == '') {
						groupBy = ''
					} else {
						groupBy = ajaxData[i].groupField;
					}
					if (ajaxData[i].fieldName == null
							|| ajaxData[i].fieldName == '') {
						columnName = ''
					} else {
						columnName = ajaxData[i].fieldName;
					}
					if (ajaxData[i].leftOrRight == "righ") {
						manager2.addRow({
							dsId : ajaxData[i].dsId,
							tableName : ajaxData[i].tableEnName,
							leftAndRight : "外表",
							leftOrRigh : ajaxData[i].leftOrRight,
							calCode : ajaxData[i].calCode,
							columnName : columnName,
							functionName : ajaxData[i].aggregateFunc,
							queryCondition : queryCondition,
							groupBy : groupBy
						});
						f_changeHeaderText(ajaxData[i].tableName);
					} else {
						manager.addRow({
							dsId : ajaxData[i].dsId,
							tableName : ajaxData[i].tableEnName,
							leftAndRight : "内表",
							leftOrRigh : ajaxData[i].leftOrRight,
							calCode : ajaxData[i].calCode,
							columnName : columnName,
							functionName : ajaxData[i].aggregateFunc,
							queryCondition : queryCondition,
							groupBy : groupBy
						});
					}

				}
			}

		});
	}
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				name : "templeId",
				type : "hidden"
			}, {
				name : "ruleId",
				type : "hidden"
			}, {
				name : "dsId2",
				type : "hidden"
			}, {
				name : "tableName2",
				type : "hidden"
			}, {
				display : "规则名称<font color='red'>*</font>",
				name : "ruleNm",
				width : 190,
				space : 10,
				labelWidth : 70,
				newline : true,
				type : "text",
				options: {
					onChangeValue : function(){
						var info = {
								"ruleId" : "${ruleId}",
								"ruleNm" : document.getElementById('ruleNm').value,
								"templeId" : "${id}"

							};
						$.ajax({
							async : false,
							url : "${ctx}/rpt/input/rule/checkRuleName",
							dataType : 'json',
							data : info,
							type : "post",
							success : function(data) {
								if(!data){
									chackName = "1";
									BIONE.showError("规则名已存在，请重新填写。");
								}else{
									chackName = "0";
								}
							},
							error : function(result, b) {
								BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
							}
							
						}); 
					}
				}

			}, {
				display : "提示信息<font color='red'>*</font>",
				name : "errorTip",
				newline : false,
				width : 350,
				space : 1,
				labelWidth : 70,
				type : "text"
			}, {
				group : "表达式项",
				groupicon : groupicon,
				display : "是否外表",
				space : 1,
				width : 55,
				name : 'tabletype',
				newline : true,
				type : 'checkbox'
			}, {
				display : GlobalRemark.ruleTitle,
				name : "caozuo",
				newline : false,
				width : 465,
				labelWidth : 70,
				type : "text",
				options :{
					disabled  : true
				}
			} , {
				display : "外表数据源<font color='red'>*</font>",
				name : 'dsName',
				newline : true,
				width:160,
				type : 'popup',
				options : {
					openwin : true,
					valueFieldID : "dsId",
					onButtonClick: openDsNewDilog
				},
				validate : {
					required : true,
					maxlength : 256
				}
			}, {
				display : "外表<font color='red'>*</font>",
				name : 'tableName',
				width:160,
				newline : false,
				type : 'popup',
				options : {
					openwin : true,
					valueFieldID : "tableNameId",
					onButtonClick: openTableNewDilog
				},
				validate : {
					required : true,
					maxlength : 256
				}
			}, {
				display : "约束字段",
				name : 'columnNameValue',
				newline : true,
				type : 'select',
				width:160,
				options : {
					valueFieldID : 'columnName',
					data : selection,
					onSelected : function() {
						var columnName = document.getElementById('columnName').value;
						insertText(columnName);
					}
				}
			}, {
				display : "分类列",
				name : 'groupByValue',
				newline : document.body.scrollWidth<1034,
				width:160,
				newline : false,
				type : 'select',
				options : {
					split: ",",
					isShowCheckBox : true,
					isMultiSelect : true,
					valueFieldID : 'groupBy',
					data : selection,
					onBeforeOpen : function() {
						if(udpgrid == "1" || udpgrid == "0"){
							liger.get('groupByValue').setData(selection);
						}else if(udpgrid == "2"){
							liger.get('groupByValue').setData(newSelection);
						}
					}
				}
			},{
				display : "聚合函数",
				name : "functionNameValue",
				newline : true,
				type : 'select',
				width:162,
// 				width : 120,
				options : {
					onSelected : function() {
						var functionName = document.getElementById('functionName1').value;
						var col = document.getElementById('functionName').value;
						if(!functionName ==""){
							insertText(functionName+"()");
						}
						checkFunc(functionName);
					},
					valueFieldID : 'functionName1',
					data : selectionFunc
				}
			},  {
				display : "表达式",
				name : "functionName",
				newline : false,
				//width : 600,
				width : 330,
				type : 'text'
			}, {
				display : "过滤条件",
				name : "queryCondition",
				newline : true,
				width : 595,
				attr : {
					rows : 2
				},
				
				type : "textarea"
			} ]
		})
		
		$("#queryCondition").css({ height:'38px'});
		document.getElementById('functionName').onclick = function(){
			selectedInput = this;
			selectedInput.posion = getCursorPosition(this);
		}
		document.getElementById('queryCondition').onclick = function(){
			selectedInput = this;
			selectedInput.posion = getCursorPosition(this);
		}
	}

	function setCss() {
		var val = $("#search input[name=tabletype]").val();
		if ($("#tabletype").attr('checked') == "checked") {
// 			$("#search input[name=dsName]").parent().parent().parent().parent(
// 					"ul").show().find("input").removeAttr("disabled").css(
// 					"color", "black");
// 			$("#search input[name=tableName]").parent().parent().parent()
// 					.parent("ul").show().find("input").removeAttr("disabled")
// 					.css("color", "black");
			$('#dsId').parents('li.l-fieldcontainer').parent('ul').show();
			liger.get('columnNameValue').setData(newSelection);
			liger.get('groupByValue').setData(newSelection);
			$("#search [name='columnNameValue']").val("");
			$("#search [name='columnName']").val("");
			$("#search [name='groupByValue']").val("");
			$("#search [name='groupBy']").val("");
			$("#search [name='caozuo']").val("请输入表达式，可通过约束字段与聚合函数进行组装");

		} else {
// 			$("#search input[name=dsName]").parent().parent().parent().parent(
// 					"ul").hide().find("input").attr("disabled", "disabled")
// 					.css("color", "black");
// 			$("#search input[name=tableName]").parent().parent().parent()
// 					.parent("ul").hide().find("input").attr("disabled",
// 							"disabled").css("color", "black");
			$('#dsId').parents('li.l-fieldcontainer').parent('ul').hide();
			liger.get('columnNameValue').setData(selection);
			liger.get('groupByValue').setData(selection);
			$("#search [name='columnNameValue']").val("");
			$("#search [name='columnName']").val("");
			$("#search [name='groupByValue']").val("");
			$("#search [name='groupBy']").val("");
			$("#search [name='caozuo']").val("请输入表达式，可通过约束字段与聚合函数进行组装");
		}
	}
	function openDsNewDilog() {
		dsName = $("#dsName");
		dsId = $("#dsId");
		dialog = BIONE.commonOpenDialog('选择数据源', "dsList", "850", "370",
				"${ctx}/bione/mtool/datasource/chackDS/1");
	}
	function openTableNewDilog() {
		var id = document.getElementById('dsId').value;
		if (id == null || id == "") {
			BIONE.tip('请先选择数据源。');
			return;
		}
		tableName = $("#tableName");
		tableNameId = $("#tableNameId");
		dialog = BIONE.commonOpenDialog('选择数据表', "tableList", "700", "350",
				"${ctx}/rpt/input/temple/tableListForTemple/" + id);
	}
	function getColumnList() {
		var rdbId = document.getElementById('dsId').value;
		var tableName = liger.get('tableName').getText();
		BIONE.ajax({
			type : "post",
			dataType : 'json',
			url : "${ctx}/rpt/input/library/getColumnList.json",
			data : {
				"dsId" : rdbId,
				"tableName" : tableName
			}
		}, function(data1) {
			liger.get('columnNameValue').setData(data1);
			newSelection = data1;
			liger.get('groupByValue').setData(data1);
		});
	}
	function addNewRow() {
		var manager = $("#maingrid").ligerGetGridManager();
		var manager2 = $("#maingrid2").ligerGetGridManager();
		var columnName = document.getElementById('columnName').value;
		var functionName = document.getElementById('functionName').value;
		var queryCondition = document.getElementById('queryCondition').value;
		var groupBy = document.getElementById('groupBy').value;
		var dsId = document.getElementById('dsId').value;
		var tableName = liger.get('tableName').getText();
		var dsId2 = document.getElementById('dsId2').value;
		var tableName2 = document.getElementById('tableName2').value;
		if ($('#tabletype').ligerCheckBox().getValue() == true) {
			if (dsId == "" || dsId == null) {
				BIONE.tip('请选择数据源。');
				return;
			}
			if (tableName == "" || tableName == null) {
				BIONE.tip('请选择数据表。');
				return;
			}
			if (functionName == "" || functionName == null) {
				BIONE.tip('请输入表达式。');
				return;
			}
			manager2.addRow({
				dsId : dsId,
				tableName : tableName,
				leftAndRight : "外表",
				leftOrRigh : "righ",
				calCode : "+",
				columnName : "",
				functionName : functionName.replace(/(\s*)/g, ""),
				queryCondition : queryCondition,
				groupBy : groupBy
			});
			f_changeHeaderText(tableName);
		} else {
			if (functionName == "" || functionName == null) {
				BIONE.tip('请输入表达式。');
				return;
			}
			manager.addRow({
				dsId : dsId2,
				tableName : tableName2,
				leftAndRight : "内表",
				leftOrRigh : "left",
				calCode : "+",
				columnName : "",
				functionName : functionName.replace(/(\s*)/g, ""),
				queryCondition : queryCondition,
				groupBy : groupBy
			});
		}

	}

	function initGrid() {

		grid = manager = $("#maingrid").ligerGrid(
				{
					columns : [
							{
								name : 'dsId',
								hide : 1,
								width : '0'
							},
							{
								name : 'tableName',
								hide : 1,
								width : '0'
							},
							{
								name : 'leftAndRight',
								hide : 1,
								width : '0'
							},
							{
								name : 'leftOrRigh',
								hide : 1,
								width : '0'
							},
							{
								display : '运算',
								name : 'calCode',
								width : '45',
								maxWidth : "45",
								editor : {
									type : 'select',
									data : [ {
										'id' : '+',
										'text' : '加'
									}, {
										'id' : '-',
										'text' : '减'
									}, {
										'id' : '*',
										'text' : '乘'
									}, {
										'id' : '/',
										'text' : '除'
									} ]
								},
								render : function(row) {
									switch (row.calCode) {
									case "+":
										return "加";
									case "-":
										return "减";
									case "*":
										return "乘";
									case "/":
										return "除";
									}
								}
							},
							{
								hide : 1,
								width : '0',
								name : 'columnName'
							},
							{
								name : 'groupBy',
								hide : 1,
								width : '0'
							},

							{
								display : '内表表达式',
								name : 'functionName',
								width : "200",
								maxWidth : "200"
							},
							{
								name : 'queryCondition',
								hide : 1,
								width : '0'

							},
							{
								display : '操作',
								isSort : false,
								width : '45',
								maxWidth : "45",
								render : function(rowdata, rowindex, value) {
									var h = "";
									if (!rowdata._editing) {
										h += "<a href='javascript:deleteRow("
												+ rowindex + ")'>删除</a> ";
									}

									return h;
								}
							} ],
					usePager : false,
					isScroll : true,
					onDblClickRow : function (data, rowindex, rowobj){
						$("#tabletype").ligerCheckBox({ disabled: false }).setValue(false);
						setCss();
						$("#search [name='functionName']").val(data['functionName']);
						$("#search [name='queryCondition']").val(data['queryCondition']);
						liger.get('columnNameValue').setData(selection);
						liger.get('groupByValue').setData(selection);
						$("#search [name='caozuo']").val("修改完表达式后请点击更新按钮");
						comboxManager.selectValue(data['groupBy']);
						rowobjUdp = rowobj;
						udpgrid = "1";
	                },
	                rownumbers : false,
					checkbox: false,
					enabledEdit : true,
					alternatingRow : true,//附加奇偶行效果行
					colDraggable : true,
					height : '90',
					data : {
						Rows : [{}]
					}
				});

	}
	function initGrid2() {

		grid2 = manager2 = $("#maingrid2").ligerGrid(
				{
					columns : [
							{
								name : 'dsId',
								hide : 1,
								width : '0'
							},
							{
								name : 'tableName',
								hide : 1,
								width : '0'
							},
							{
								name : 'leftAndRight',
								hide : 1,
								width : '0'
							},
							{
								name : 'leftOrRigh',
								hide : 1,
								width : '0'
							},
							{
								display : '运算',
								name : 'calCode',
								width : '45',
								maxWidth : "45",
								editor : {
									type : 'select',
									data : [ {
										'id' : '+',
										'text' : '加'
									}, {
										'id' : '-',
										'text' : '减'
									}, {
										'id' : '*',
										'text' : '乘'
									}, {
										'id' : '/',
										'text' : '除'
									} ]
								},
								render : function(row) {
									switch (row.calCode) {
									case "+":
										return "加";
									case "-":
										return "减";
									case "*":
										return "乘";
									case "/":
										return "除";
									}
								}
							},
							{
								hide : 1,
								width : '0',
								name : 'columnName'
							},
							{
								name : 'groupBy',
								hide : 1,
								width : '0'
							},

							{
								display : '外表表达式',
								name : 'functionName',
								width : "200",
								maxWidth : "200"
							},
							{
								name : 'queryCondition',
								hide : 1,
								width : '0'

							},
							{
								display : '操作',
								isSort : false,
								width : '45',
								maxWidth : "45",
								render : function(rowdata, rowindex, value) {
									var h = "";
									if (!rowdata._editing) {
										h += "<a href='javascript:deleteRow2("
												+ rowindex + ")'>删除</a> ";
									}

									return h;
								}
							} ],
					rownumbers : false,
					checkbox: false,
					usePager : false,
					isScroll : true,
					onDblClickRow : function (data, rowindex, rowobj){
						$("#tabletype").ligerCheckBox({ disabled: false }).setValue(true);
						setCss();
						$("#search [name='functionName']").val(data['functionName']);
						$("#search [name='queryCondition']").val(data['queryCondition']);
						$("#search [name='dsId']").val(data['dsId']);
						$("#search [name='dsName']").val(listDsMap[data['dsId']]);
						$("#search [name='tableName']").val(data['tableName']);
						$("#search [name='caozuo']").val("修改完表达式后请点击更新按钮");						
						getColumnList();
						comboxManager.selectValue(data['groupBy']);
						rowobjUdp = rowobj;
						udpgrid = "2";
						
	                },
					enabledEdit : true,
					clickToEdit : true,
					height : '90',
					data : {
						Rows : [{}]
					}
				});
	}
	function updateGrid(){
		if(udpgrid != "0"){
			if(udpgrid == "1"){
				manager.updateCell("functionName", document.getElementById('functionName').value, rowobjUdp ); 
				manager.updateCell("queryCondition", document.getElementById('queryCondition').value, rowobjUdp ); 
				manager.updateCell("groupBy", document.getElementById('groupBy').value, rowobjUdp ); 
				manager.reRender();
			}else if(udpgrid == "2"){
				var dsId = document.getElementById('dsId').value;
				var tableName = liger.get('tableName').getText();
				manager2.updateCell("dsId", dsId, rowobjUdp ); 
				manager2.updateCell("tableName", tableName, rowobjUdp ); 
				
				manager2.updateCell("functionName", document.getElementById('functionName').value, rowobjUdp ); 
				manager2.updateCell("queryCondition", document.getElementById('queryCondition').value, rowobjUdp ); 
				manager2.updateCell("groupBy", document.getElementById('groupBy').value, rowobjUdp ); 
				manager2.reRender();
			}
			udpgrid = "0";
			$("#search [name='functionName']").val("");
			$("#search [name='caozuo']").val("请输入表达式，可通过约束字段与聚合函数进行组装");
			BIONE.tip("更新成功！");
		}else{
			BIONE.tip("请先选择要修改的数据，双击修改后再更新！");
			return;
		}
	}
	function delete_templeFile() {
		achieveIds();
		$.ligerDialog.confirm('确实要删除这些条记录吗？', function(yes) {
			if (yes) {
				$.ajax({
					type : "POST",
					url : "${ctx}/rpt/input/temple/templeFile/" + ids,
					dataType : "text",
					success : function(text) {
						BIONE.tip(text);
						grid.loadData();
					}
				});
			}
		});
	}
	function deleteRow(rowid) {
		var manager = $("#maingrid").ligerGetGridManager();
		manager.deleteRow(rowid);
	}
	function deleteRow2(rowid) {
		var manager = $("#maingrid2").ligerGetGridManager();
		manager.deleteRow(rowid);
	}
	function saveRule5() {
		if(chackName == "1"){
			BIONE.tip("规则名已存在！");
			return;
		}
		var manager = $("#maingrid").ligerGetGridManager();
		var manager2 = $("#maingrid2").ligerGetGridManager();
		var data = manager.getData();
		var data2 = manager2.getData();
		var logicCode = document.getElementById('logicCode').value;
		var paramStr = "";
		var ruleNm = document.getElementById('ruleNm').value;
		if(ruleNm == "" ){
			BIONE.tip("请输入规则名称！");
			return;
		}
		var errorTip = document.getElementById('errorTip').value;
		if(errorTip == "" ){
			BIONE.tip("请输入提示信息！");
			return;
		}
		if(data.length>0 && (data[0].calCode=="*" || data[0].calCode=="/" )){
			BIONE.tip("表内第一条数据的运算符不可为乘或除！");
			return;
		}
		if(data2.length>0 && (data2[0].calCode=="*" || data2[0].calCode=="/" )){
			BIONE.tip("表外第一条数据的运算符不可为乘或除！");
			return;
		}
		if(data2.length==0 || data.length==0){
			BIONE.tip("表内和表外必须都存在值！");
			return;
		}
		for (i = 0; i < data.length; i++) {
			paramStr = paramStr + data[i].calCode + "@#@" + data[i].columnName
					+ "@#@" + data[i].groupBy + "@#@" + data[i].functionName + "@#@"
					+ data[i].queryCondition + "@#@" + data[i].leftOrRigh + "@#@"
					+ data[i].dsId + "@#@" + data[i].tableName + ";;"
		}
		for (i = 0; i < data2.length; i++) {
			paramStr = paramStr + data2[i].calCode + "@#@" + data2[i].columnName
					+ "@#@" + data2[i].groupBy + "@#@" + data2[i].functionName
					+ "@#@" + data2[i].queryCondition + "@#@" + data2[i].leftOrRigh
					+ "@#@" + data2[i].dsId + "@#@" + data2[i].tableName + ";;"
		}
		var info = {
			"templeId" : "${id}",
			"ruleId" : "${ruleId}",
			"logic" : logicCode,
			"ruleNm" : document.getElementById('ruleNm').value,
			"errorTip" : document.getElementById('errorTip').value,
			"paramStr" : paramStr

		};

		$.ajax({
			async : false,
			url : "${ctx}/rpt/input/rule/rule5-save.json",
			dataType : 'json',
			data : info,
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在保存数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function() {
				BIONE.tip("保存成功！");
				parent.$.ligerui.get("maingrid").set('url', parent.getTempleUrl()+new Date());
				parent.$.ligerui.get("maingrid").setOptions({ parms: []  }); 
				parent.$.ligerui.get("maingrid").loadData();
				if (!"${ruleId}") {
					BIONE.closeDialog("dataRules");
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}

		});
	}
	
	/**
	 * 获取光标的位置
	 * @param 控件对象
	 */
// 	function getCursorPosition(txb){
// 	    var slct = document.selection;
// 	    var rng = slct.createRange();
// 	    txb.select();
// 	    rng.setEndPoint("StartToStart", slct.createRange());
// 	    var psn = rng.text.length;
// 	    rng.collapse(false);
// 	    rng.select();
	    
// 	    return psn;
// 	}
	
	function getCursorPosition(elm){
	    if(elm.createTextRange) { // IE
	        var range = document.selection.createRange();
	        range.setEndPoint('StartToStart', elm.createTextRange());
	        range.collapse(false);
	        range.select();
	        return range.text.length;
	    } else if(typeof elm.selectionStart == 'number') { // Firefox Chrome
	        return elm.selectionStart;
	    }
	}
	
	
	
	/**
	 * 插入指标或函数
	 */
	function insertText(text){
		selectedInput = selectedInput||document.getElementById('functionName');
		var value=selectedInput.value;
		var posion = selectedInput.posion||0;
		selectedInput.focus();
		while(value.substr(posion,2).indexOf('\n')!=-1){//换行问题
			posion+=1;
		}
		value=value.substr(0,posion).concat(text,value.substr(posion,value.length));
		selectedInput.value = value;
		
		UDIP.selectText(selectedInput,posion,posion+text.length);
	}
	function closeWindow(){
		BIONE.closeDialog("dataRules");
	}
	//对输入信息的提示
	function check(functionName) {
		$("#ruleNm").focus(
				function() {
					$("#search [name='caozuo']").val(RuleRemark.global.ruleNm);
				});
		$("#errorTip").focus(
				function() {
					$("#search [name='caozuo']").val(RuleRemark.global.errorTip);
				});
		$("#queryCondition").focus(
				function() {
					$("#search [name='caozuo']").val(RuleRemark.global.queryCondition);
				});
		$("#groupByValue").change(
				function() {
					$("#search [name='caozuo']").val(RuleRemark.rule4.groupByValue);
				});
	}
	function checkFunc(functionName) {
		if(functionName.toLowerCase()=="sum"){
			$("#search [name='caozuo']").val(RuleRemark.rule4.sum);
		}
		if(functionName.toLowerCase()=="count"){
			$("#search [name='caozuo']").val(RuleRemark.rule4.count);
		}
		if(functionName.toLowerCase()=="avg"){
			$("#search [name='caozuo']").val(RuleRemark.rule4.avg);
		}
		if(functionName.toLowerCase()=="max"){
			$("#search [name='caozuo']").val(RuleRemark.rule4.max);
		}
		if(functionName.toLowerCase()=="min"){
			$("#search [name='caozuo']").val(RuleRemark.rule4.min);
		}
		if(functionName.toLowerCase()=="abs"){
			$("#search [name='caozuo']").val(RuleRemark.rule4.abs);
		}
	}
	
	function f_changeHeaderText(tableName){
		//var txt = tableName+"表达式"
		//grid2.changeHeaderText('functionName', txt);
	}
	
	function selectPopupEdit(name, text, val) {
		var pop = liger.get(name);
		pop.setText(text);
		pop.setValue(val);
	}
</script>
</head>
<body>

</body>
</html>