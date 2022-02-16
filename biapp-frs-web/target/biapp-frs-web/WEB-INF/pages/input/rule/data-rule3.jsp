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
	var grid, grid2, btns, url, ids = [], dialog, buttons = [];
	var udpgrid="0",chackName = "0";
	var selectedInput = null;
	var selection = [ {
		id : "",
		text : "请选择"
	} ];
	var selectionFunc = [ {
		id : "",
		text : "请选择"
	} ];
	var manager;
	$(function() {
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/data/getColumnList/' + "${id}",
			success : function(data1) {
				selection = selection.concat(data1);
			}
		});

		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/rule/dataRulesFuncCombox',
			success : function(data) {
				selectionFunc= selectionFunc.concat(data);
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

		searchForm();
		if ("${id}") {
			$.ajax({
				url : "${ctx}/rpt/input/temple/findTempleInfo?templeId=${id}",
				success : function(data) {
					$("#search [name='dsId']").val(data.dsId);
					$("#search [name='tableName']").val(data.tableName);
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
					$("#logicCode").val(data.logic);
					$("#search [name='ruleNm']").val(data.ruleNm);
					$("#search [name='ruleId']").val(data.ruleId);
					$("#search [name='errorTip']").val(data.errorTip);
					$("#search [name='queryCondition']").val(data.filterCondition);
				}
			});
		}
		if(!"${lookType}"){
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '左边',
			icon : 'l',
			width : '50px',
			click : function() {
				addLeftNewRow();
			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '右边',
			icon : 'r',
			width : '50px',
			click : function() {
				addRightNewRow();
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
				saveRule3();
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
		$("#tabletype").on("change", function() {
			setCss();
		});
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
						if (!data) {
							chackName = "1";
							BIONE.showError("规则名已存在，请重新填写。");
						} else {
							chackName = "0";
						}
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				}); 
			}
		});
		
		getRuleItemsList();
		
// 		$("#columnNameValue").ligerComboBox({
// 			onSelected : function() {
// 				var columnName = document.getElementById('columnName').value;
// 				insertText(columnName);
// 			}
// 		});
// 		$("#functionNameValue").ligerComboBox(
// 				{
// 					onSelected : function() {
// 						debugger;
// 						var functionName = document.getElementById('functionName1').value;
// 						var col = document.getElementById('functionName').value;
// 						if(!functionName ==""){
// 							insertText(functionName+"()");
// 						}
// 					}
// 				});
// 		$("#functionNameValue").ligerGetComboBoxManager().selectValue("");
		liger.get('functionNameValue').selectValue("");
// 		$("#columnNameValue").ligerGetComboBoxManager().selectValue("");
		liger.get('columnNameValue').selectValue("");
		check();
// 		$("#caozuo").css({border:"0px solid #F1F1F1",background: "#F1F1F1"});
	});
	
	function getRuleItemsList() {
		$.ajax({
			async : false,
			url : "${ctx}/rpt/input/rule/listRuleItem/${ruleId}.json?d="+new Date().getTime(),
			data : {
				ruleId : "${ruleId}"
			},
			success : function(ajaxData) {
				var functionName;
				var queryCondition;
				var groupBy;
				var dsId;
				var tableName;
				var manager = $("#maingrid").ligerGetGridManager();
				var manager2 = $("#maingrid2").ligerGetGridManager();
				for ( var i = 0; i < ajaxData.length; i++) {
					if (ajaxData[i].aggregateFunc == null || ajaxData[i].aggregateFunc == '') {
						functionName = '';
					} else {
						functionName = ajaxData[i].aggregateFunc;
					}
					if (ajaxData[i].filterCondition == null || ajaxData[i].filterCondition == '') {
						queryCondition = ''
					} else {
						queryCondition = ajaxData[i].filterCondition;
					}
					if (ajaxData[i].groupField == null || ajaxData[i].groupField == '') {
						groupBy = ''
					} else {
						groupBy = ajaxData[i].groupField;
					}
					if (ajaxData[i].dsId == null || ajaxData[i].dsId == '') {
						dsId = ''
					} else {
						dsId = ajaxData[i].dsId;
					}
					if (ajaxData[i].tableEnName == null || ajaxData[i].tableEnName == '') {
						tableName = ''
					} else {
						tableName = ajaxData[i].tableEnName;
					}
					if (ajaxData[i].fieldName == null || ajaxData[i].fieldName == '') {
						columnName = ''
					} else {
						columnName = ajaxData[i].fieldName;
					}
					if (ajaxData[i].leftOrRight == "righ") {
						manager2.addRow({
							leftAndRight : "右边",
							leftOrRigh : ajaxData[i].leftOrRight,
							calCode : ajaxData[i].calCode,
							columnName : columnName,
							functionName : functionName,
							queryCondition : queryCondition,
							groupBy : groupBy,
							dsId : dsId,
							tableName : tableName
						});
					} else {
						manager.addRow({
							leftAndRight : "左边",
							leftOrRigh : ajaxData[i].leftOrRight,
							calCode : ajaxData[i].calCode,
							columnName : columnName,
							functionName : functionName,
							queryCondition : queryCondition,
							groupBy : groupBy,
							dsId : dsId,
							tableName : tableName
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
				name : "dsId",
				type : "hidden"
			}, {
				name : "tableName",
				type : "hidden"
			}, {
				display : "规则名称<font color='red'>*</font>",
				name : "ruleNm",
				newline : true,
				width : 190,
				space : 10,
				labelWidth : 70,
				type : "text"
			}, {
				display : "提示信息<font color='red'>*</font>",
				name : "errorTip",
				newline : false,
				width : 350,
				space : 1,
				labelWidth : 70,
				type : "text"
			}, {
				display : "过滤条件",
				name : "queryCondition",
				newline : true,
				width : 620	,
				labelWidth : 70,
				type : "textarea"
			}, {
				group : "表达式项",
				groupicon : groupicon,
				display : "是否常量",
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
				display : "常量",
				name : "changliang",
				newline : true,
				type : "text",
				options :{
					number : true
				}
			},{
				display : "约束字段",
				name : 'columnNameValue',
				newline : true,
				type : 'select',
				options : {
					valueFieldID : 'columnName',
					data : selection,
					onSelected : function() {
						var columnName = document.getElementById('columnName').value;
						insertText(columnName);
					}
				}
			}, {
				display : "计算函数",
				name : "functionNameValue",
				newline : false,
				type : 'select',
				options : {
					valueFieldID : 'functionName1',
					data : selectionFunc,
					onSelected : function() {
						var functionName = document.getElementById('functionName1').value;
						var col = document.getElementById('functionName').value;
						if(!functionName ==""){
							insertText(functionName+"()");
						}
					}
				}
			}, {
				display : "表达式",
				name : "functionName",
				newline : true,
				width : 595,
				type : 'text'
			}]
		}

		)
		$("#queryCondition").css({ height:'38px'});
		$("#queryCondition").attr('title',GlobalRemark.sysParams);
		$("#functionName").attr('title',RuleRemark.global.express);
		document.getElementById('functionName').onclick = function(){
			selectedInput = this;
			selectedInput.posion = getCursorPosition(this);
		}
		document.getElementById('queryCondition').onclick = function(){
			selectedInput = this;
			selectedInput.posion = getCursorPosition(this);
		}
	}
	function addLeftNewRow() {
		var manager = $("#maingrid").ligerGetGridManager();
		if ($("#tabletype").attr('checked') == "checked") {
			var changliang = document.getElementById('changliang').value;
			if (changliang == "" || changliang == null) {
				BIONE.tip('请输入常量。');
				return;
			}
			manager.addRow({
				dsId : "",
				tableName : "",
				leftAndRight : "左边",
				leftOrRigh : "left",
				calCode : "+",
				columnName : "",
				functionName : changliang.replace(/(\s*)/g, ""),
				queryCondition : ""
			});
		} else {
			var dsId = document.getElementById('dsId').value;
			var tableName = document.getElementById('tableName').value;

			var columnName = document.getElementById('columnName').value;
			
			var functionName = document.getElementById('functionName').value;

			if (functionName == "" || functionName == null) {
				BIONE.tip('请输入表达式。');
				return;
			}
			
			manager.addRow({
				dsId : dsId,
				tableName : tableName,
				leftAndRight : "左边",
				leftOrRigh : "left",
				calCode : "+",
				columnName : "",
				functionName : functionName.replace(/(\s*)/g, ""),
				queryCondition : ""
			});
		}
	}
	function addRightNewRow() {
		var manager = $("#maingrid2").ligerGetGridManager();
		if ($("#tabletype").attr('checked') == "checked") {
			var changliang = document.getElementById('changliang').value;
			if (changliang == "" || changliang == null) {
				BIONE.tip('请输入常量。');
				return;
			}
			manager.addRow({
				dsId : "",
				tableName : "",
				leftAndRight : "右边",
				leftOrRigh : "righ",
				calCode : "+",
				columnName : "",
				functionName : changliang.replace(/(\s*)/g, ""),
				queryCondition : ""
			});
		} else {
			var dsId = document.getElementById('dsId').value;
			var tableName = document.getElementById('tableName').value;

			var columnName = document.getElementById('columnName').value;
			var functionName = document.getElementById('functionName').value;
			var queryCondition = document.getElementById('queryCondition').value;

			if (functionName == "" || functionName == null) {
				BIONE.tip('请输入表达式。');
				return;
			}
			manager.addRow({
				dsId : dsId,
				tableName : tableName,
				leftAndRight : "右边",
				leftOrRigh : "righ",
				calCode : "+",
				columnName : "",
				functionName : functionName.replace(/(\s*)/g, ""),
				queryCondition : ""
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
								width : '10%',
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
								name : 'columnName',
								hide : 1,
								width : '0'
							},
							{
								display : '表达式',
								name : 'functionName',
								width : 150
							},
							{
								name : 'queryCondition',
								hide : 1,
								width : '0'
							},
							{
								display : '操作',
								isSort : false,
								width : '12%',
								render : function(rowdata, rowindex, value) {
									var h = "";
									if (!rowdata._editing) {
										h += "<a href='javascript:deleteRow("
												+ rowindex + ")'>删除</a> ";
									}

									return h;
								}
							} ],

					rownumbers : false,
					checkbox: false,
					usePager : false,
					onDblClickRow : function (data, rowindex, rowobj){
						$("#search [name='functionName']").val(data['functionName']);
						$("#search [name='caozuo']").val("修改完表达式后请点击更新按钮");
						rowobjUdp = rowobj;
						udpgrid = "1";
	                },
					enabledEdit : true,
					clickToEdit : true,
					isScroll : true,
					height : 100,
					data : {
						Rows : [ {} ]
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
								width : '10%',
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
								name : 'columnName',
								hide : 1,
								width : '0'
							},
							{
								display : '表达式',
								name : 'functionName',
								width : 150
							},
							{
								name : 'queryCondition',
								hide : 1,
								width : '0'
							},

							{
								display : '操作',
								isSort : false,
								width : '12%',
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
					onDblClickRow : function (data, rowindex, rowobj){
						$("#search [name='functionName']").val(data['functionName']);
						$("#search [name='caozuo']").val("修改完表达式后请点击更新按钮");
						rowobjUdp = rowobj;
						udpgrid = "2";
	                },
					enabledEdit : true,
					clickToEdit : true,
					isScroll : true,
					height : 100,
					data : {
						Rows : [ {} ]
					}
				});

	}
	function updateGrid(){
		if(udpgrid != "0"){
			if(udpgrid == "1"){
				manager.updateCell("functionName", document.getElementById('functionName').value, rowobjUdp ); 
				manager.updateCell("queryCondition", document.getElementById('queryCondition').value, rowobjUdp ); 
			}else if(udpgrid == "2"){
				manager2.updateCell("functionName", document.getElementById('functionName').value, rowobjUdp ); 
				manager2.updateCell("queryCondition", document.getElementById('queryCondition').value, rowobjUdp ); 
			}
			udpgrid = "0";
			$("#search [name='functionName']").val("");
			$("#search [name='caozuo']").val("请输入表达式，可通过约束字段与计算函数进行组装");
			BIONE.tip("更新成功！");
		}else{
			BIONE.tip("请先选择要修改的数据，双击修改后再更新！");
			return;
		}
		
		
	}
	function setCss() {
		var val = $("#search input[name=tabletype]").val();
		if ($("#tabletype").attr('checked') == "checked") {
// 			$("#search input[name=changliang]").parent().parent().parent(
// 				"ul").show().find("input").removeAttr("disabled");
			
// 			$("#changliang").ligerGetTextBoxManager().setEnabled();
// 			$("#search input[name=functionNameValue]").parent().parent()
// 					.parent().parent("ul").hide().find("input").attr(
// 							"disabled", "disabled").css("color", "black");
// 			$("#search input[name=columnNameValue]").parent().parent().parent()
// 					.parent("ul").hide().find("input").attr("disabled",
// 							"disabled").css("color", "black");
// 			$("#search input[name=functionName]").parent().parent().parent(
// 				"ul").hide().find("input").attr("disabled", "disabled");
			$("#search [name='caozuo']").val("请在常量中输入整数");
			$('#columnName, #functionName').parents('li.l-fieldcontainer').parent('ul').hide();
			$('#changliang').parents('li.l-fieldcontainer').parent('ul').show();
			liger.get('columnNameValue').setDisabled();
			liger.get('functionNameValue').setDisabled();
			liger.get('functionName').setDisabled();
		} else {
// 			$("#search input[name=functionName]").parent().parent().parent(
// 				"ul").show().find("input").removeAttr("disabled");
// 			$("#search input[name=changliang]").parent().parent().parent(
// 				"ul").hide().find("input").attr("disabled", "disabled");
// 			$("#changliang").ligerGetTextBoxManager().setValue('');
// 			$("#changliang").ligerGetTextBoxManager().setDisabled();
// 			$("#search input[name=functionNameValue]").parent().parent()
// 					.parent().parent("ul").show().find("input").removeAttr(
// 							"disabled").css("color", "black");
// 			$("#search input[name=columnNameValue]").parent().parent().parent()
// 					.parent("ul").show().find("input").removeAttr("disabled")
// 					.css("color", "black");
// 			$("#search [name='columnNameValue']").val("");
// 			$("#search [name='columnName']").val("");
// 			$("#search [name='functionNameValue']").val("");
// 			$("#search [name='functionName']").val("");
			//$("#functionName").ligerTip({ content: "请输入表达式，可通过约束字段与计算函数进行组装" });
			$("#search [name='caozuo']").val("请输入表达式，可通过约束字段与计算函数进行组装");
			$('#changliang').parents('li.l-fieldcontainer').parent('ul').hide();
			$('#columnName, #functionName').parents('li.l-fieldcontainer').parent('ul').show();
			liger.get('columnNameValue').setEnabled();
			liger.get('functionNameValue').setEnabled();
			liger.get('functionName').setEnabled();
			liger.get('columnNameValue').setValue('');
			liger.get('functionNameValue').setValue('');
			liger.get('functionName').setValue('');
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
						grid2.loadData();
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
	function saveRule3() {
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
		if(ruleNm == "" || ruleNm == null){
			BIONE.tip("请输入规则名称！");
			return;
		}
		if(/^\s+$/gi.test(ruleNm)){
			BIONE.tip('规则名称不能为空格！');
			return;
		}

		var errorTip = document.getElementById('errorTip').value;
		if(errorTip == "" || errorTip == null){
			BIONE.tip("请输入提示信息！");
			return;
		}
		if(/^\s+$/gi.test(errorTip)){
			BIONE.tip('提示信息不能为空格！');
			return;
		}
		if(data.length>0 && (data[0].calCode=="*" || data[0].calCode=="/" )){
			BIONE.tip("左边第一条数据的运算符不可为乘或除！");
			return;
		}
		if(data2.length>0 && (data2[0].calCode=="*" || data2[0].calCode=="/" )){
			BIONE.tip("右边第一条数据的运算符不可为乘或除！");
			return;
		}
		if(data2.length==0 || data.length==0){
			BIONE.tip("右边和右边必须都存在值！");
			return;
		}
		for (i = 0; i < data.length; i++) {
			paramStr = paramStr + data[i].calCode + "@#@" + data[i].columnName
					+ "@#@" + data[i].functionName + "@#@" + data[i].queryCondition
					+ "@#@" + data[i].leftOrRigh + "@#@" + data[i].dsId + "@#@"
					+ data[i].tableName + ";;"
		}

		for (i = 0; i < data2.length; i++) {
			paramStr = paramStr + data2[i].calCode + "@#@" + data2[i].columnName
					+ "@#@" + data2[i].functionName + "@#@"
					+ data2[i].queryCondition + "@#@" + data2[i].leftOrRigh + "@#@"
					+ data2[i].dsId + "@#@" + data2[i].tableName + ";;"
		}

		var info = {
			"templeId" : "${id}",
			"ruleId" : "${ruleId}",
			"logic" : logicCode,
			"filterCondition" : document.getElementById('queryCondition').value,
			"ruleNm" : document.getElementById('ruleNm').value,
			"errorTip" : document.getElementById('errorTip').value,
			"paramStr" : paramStr

		};
		BIONE.ajax({
			type : "post",
			dataType : 'json',
			url : "${ctx}/rpt/input/rule/rule3-save.json",
			data : info
		}, function() {
			BIONE.tip("保存成功！");
			parent.$.ligerui.get("maingrid").set('url', parent.getTempleUrl()+ new Date().getTime());
			parent.$.ligerui.get("maingrid").setOptions({ parms: []  }); 
			parent.$.ligerui.get("maingrid").loadData();
			if (!"${ruleId}") {
				BIONE.closeDialog("dataRules");
			}
		});
	}
	
	/**
	 * 获取光标的位置
	 * @param 控件对象
	 */
	function getCursorPosition(elm){
	    if(elm.createTextRange) { // IE
	        var range = document.selection.createRange();
	        range.setEndPoint('StartToStart', elm.createTextRange());
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
		if(text=="if()"){
			$("#search [name='caozuo']").val("if计算函数例子：if(1==1,1,2)，结果为1");
		}
		if(text=="substr()"){
			$("#search [name='caozuo']").val("substr计算函数例子：substr('abc',1,2)，结果为b");
		}
		if(text=="len()"){
			$("#search [name='caozuo']").val("len计算函数例子：len('abc')，结果为3");
		}
		if(text=="trim()"){
			$("#search [name='caozuo']").val("trim计算函数例子：trim('  a b c  ')，结果为a b c");
		}
		
		UDIP.selectText(selectedInput,posion,posion+text.length);
	}
	function closeWindow(){
		BIONE.closeDialog("dataRules");
	}
	//对输入信息的提示
	function check() {
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
	}
</script>
</head>
<body>
</body>
</html>