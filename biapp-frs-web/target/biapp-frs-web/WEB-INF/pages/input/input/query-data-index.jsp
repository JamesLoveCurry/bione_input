<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template3_1.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/udip/index.css" />
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid, btns, url, ids = [], dialog, buttons = [];
	var selection = [],columnList = [];
	var colType = "",colName = "",colLib = "",colNameCN = "",templeId = "";
	var connLower = "",orgColumn = "",orgId = "";
	var manager;
	var selectionNull = [];
	var libMap = {};;
	$(function() {
		initTree();
		searchForm();
		initGrid();
		var managers = $("#maingrid").ligerGetGridManager();
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '添加条件',
			icon : 'add',
			width : '74px',
			click : function() {
				addNewRow();
			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '按条件查询',
			icon : 'view',
			width : '85px',
			click : function() {
				submit2();
			}
		});
		dispCSS();
	});
	function setColumnList(){
		$("#columnName").ligerComboBox({
			onSelected : function() {
				var columnName = document.getElementById('columnsName').value;
				if(columnList[columnName]){
					colType = columnList[columnName]["columnType"];
					colName = columnName;
					colLib = columnList[columnName]["dataZidian"];
					colNameCN = columnList[columnName]["columnComment"];
					setCSS();
				}
			}
		});		
	}
	function dispCSS(){
		$("#search input[name=columnValue1]").parent().parent().parent("ul")
			.hide().find("input").attr("disabled", "disabled");
		$("#search input[name=value1_1]").parent().parent().parent()
			.parent("ul").hide().find("input").attr("disabled",
				"disabled").css("color", "black");
		$("#search input[name=columnValue3]").parent().parent().parent()
			.parent("ul").hide().find("input").attr("disabled",
				"disabled").css("color", "black");
		$("#search input[name=columnValue2]").parent().parent().parent()
			.parent("ul").hide().find("input").attr("disabled",
				"disabled").css("color", "black");
		$("#search input[name=columnValue4]").parent().parent().parent()
			.parent("ul").hide().find("input").attr("disabled",
				"disabled").css("color", "black");
		$("#search input[name=value1]").parent().parent().parent()
			.parent("ul").hide().find("input").attr("disabled",
				"disabled").css("color", "black");
		$("#search [name='columnValue1']").val("");
		$("#search [name='value1_1']").val("");
		$("#search [name='columnValue3']").val("");
		$("#search [name='columnValue2']").val("");
		$("#search [name='columnValue4']").val("");
		$("#search [name='value1']").val("");
		$("#search [name='minValue_1']").val("");
		$("#search [name='minValue']").val("");
		$("#search [name='maxValue_1']").val("");
		$("#search [name='maxValue']").val("");
		$("#search [name='value3_1']").val("");
		$("#search [name='value3']").val("");
		
	}
	function setCSS(){
	if(colLib != null){
		setForm(colType);
		if(libMap[colLib]&&libMap[colLib][0]&&libMap[colLib][0]['upid'] == null){
			$("#columnValue4").ligerGetComboBoxManager().setData(libMap[colLib]);
		}else{
			//$("#columnValue2").ligerGetComboBoxManager().setData(libMap[colLib]);
			//if(orgColumn==colName&& connLower=="no"){
			//	$("#columnValue2").ligerGetComboBoxManager().selectValue(orgId);
			//	$("#columnValue2").ligerComboBox({
			//		onBeforeOpen : function (){
	        //            return false;
	        //        }
			//	});
			//}else{
				$("#columnValue2").ligerComboBox({
					onBeforeOpen : function (){
		    			openSelectNewDilog();
						return false;
					}
				});
			//}
		}
	}else{
		setForm(colType);
	}
	}
	function setDataZidina(name,id,formName,formType){
		$("#search input[name=columnValue2]").val(name);
		$("#search [name=columnsValue2]").val(id);
	}
	function setForm(colType){
	if(colLib != null){
			if(libMap[colLib]&&libMap[colLib][0]&&libMap[colLib][0]['upid'] == null){
				//下拉框显示
			$("#search input[name=columnValue4]").parent().parent().parent()
				.parent("ul").show().find("input").removeAttr("disabled")
				.css("color", "black");	
			$("#search input[name=columnValue2]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			}else{
			//下拉框显示
			$("#search input[name=columnValue2]").parent().parent().parent()
				.parent("ul").show().find("input").removeAttr("disabled")
				.css("color", "black");	
			$("#search input[name=columnValue4]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			}
			//下拉框隐藏
			$("#search input[name=value1_1]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			$("#search input[name=columnValue3]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			$("#search input[name=value1]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			//文本框隐藏
			$("#search input[name=columnValue1]").parent().parent().parent("ul")
				.hide().find("input").attr("disabled", "disabled");
		}else if(colType == "DATE"){
			//下拉框显示
			$("#search input[name=columnValue3]").parent().parent().parent()
				.parent("ul").show().find("input").removeAttr("disabled")
				.css("color", "black");	
			$("#search input[name=value1_1]").parent().parent().parent()
				.parent("ul").show().find("input").removeAttr("disabled")
				.css("color", "black");	
			//文本框隐藏
			$("#search input[name=columnValue1]").parent().parent().parent("ul")
				.hide().find("input").attr("disabled", "disabled");
			//下拉框隐藏
			$("#search input[name=columnValue2]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			$("#search input[name=columnValue4]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");	
			$("#search input[name=value1]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
		}else if(colType == "INTEGER" || colType == "NUMERIC" || colType == "NUMBER" || colType == "DECIMAL"){
			//文本框显示
			$("#search input[name=columnValue1]").parent().parent().parent("ul")
				.show().find("input").removeAttr("disabled");
			//下拉框显示
			$("#search input[name=value1]").parent().parent().parent()
				.parent("ul").show().find("input").removeAttr("disabled")
				.css("color", "black");	
			//下拉框隐藏
			$("#search input[name=value1_1]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			$("#search input[name=columnValue3]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");	
			$("#search input[name=columnValue2]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");		
			$("#search input[name=columnValue4]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");		
		}else {
			//文本框显示
			$("#search input[name=columnValue1]").parent().parent().parent("ul")
				.show().find("input").removeAttr("disabled");
			//下拉框隐藏
			$("#search input[name=value1_1]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			$("#search input[name=columnValue3]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			$("#search input[name=columnValue2]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			$("#search input[name=columnValue4]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
			$("#search input[name=value1]").parent().parent().parent()
				.parent("ul").hide().find("input").attr("disabled",
					"disabled").css("color", "black");
		}
	}
	function openSelectNewDilog(){
		formType = document.getElementById('columnValue2').value;
		formName = 'columnsValue2';
		if(formType == null || formType == ""){
			formType = " ";
		}
		dialog = UDIP.commonOpenDialog({
			title : "选择字典",
			name : 'libraryAddWin',
			height : 400,
			width : 500,
			url :"${ctx}/udip/library/getFormTypeName?orgId="+orgId+"&libId=" + colLib + "&templeId=" +templeId +"&caseId=" + "${caseId}"+ "&formType=" + encodeURIComponent(encodeURIComponent(formType))+ "&formName=" + encodeURIComponent(encodeURIComponent(formName))
		});
	}
	function initTree() {
		var dirType = 2;
		window['orgTreeInfo'] = $.fn.zTree.init($("#tree"), {
			async : {
				enable : true,
				contentType : "application/json",
				url : "${ctx}/udip/temple/templeTree.json?d=" + new Date(),
				dataType : "json",
				type : "get"
			},
			data : {
				keep : {
					parent : true
				},
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			view : {
				showLine : false
			},
			callback : {
				onClick : function(event, treeId, treeNode) {
					if(treeNode.params.type=='temple'){
						templeId = treeNode.id;
						$.ajax({
							async : false,
							url : '${ctx}/udip/data/getColumnList/' + templeId,
							success : function(data1) {
								selectionNull = [ {
									id : "",
									text : "请选择"
								} ];
								selectionNull = selectionNull.concat(data1);
								$("#columnName").ligerGetComboBoxManager().setData(selectionNull);
								$("#columnName").ligerGetComboBoxManager().selectValue("");
								setColumnList();
							}
						});
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/udip/data/getColumnInfo.json",
							dataType : 'json',
							type : "get",
							data : {
								"id" : templeId
							},
							success : function(data) {
								columnList = data;
							}
						});		
						$.ajax({
							async : false,
							url : '${ctx}/udip/library/libMapbyTempleId2.json',
							dataType : 'json',
							type : "get",
							data : {
								"templeId" : templeId
							},
							success : function(data2) {
								libMap = data2;
							}
						});
						$.ajax({
							url : "${ctx}/udip/temple/findTempleInfo?templeId="+templeId,
							success : function(data) {
								connLower = data.connLower;
								orgColumn = data.orgColumn;
								$.ajax({
									cache : false,
									async : false,
									url : "${ctx}/udip/queryData/getOrgId",
									dataType : 'text',
									type : "post",
									success : function(data) {
										orgId = data;
									}
								});	
							}
						});
						dispCSS();
						colNameCN = "";
						for(j=0;j<manager.getData().length+10;j++){
							manager.deleteRow(0); 
						}
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
				display : "过滤字段",
				name : 'columnName',
				newline : true,
				type : 'select',
				options : {
					valueFieldID : 'columnsName'
				}
			},{
				display : "字段值",
				name : 'columnValue1',
				newline : true,
				type : 'text'
			},{
				display : "字段值",
				name : 'columnValue2',
				newline : true,
				type : 'select',
				options : {
					openwin : true,
					valueFieldID : "columnsValue2"
				}
			},{
				display : "字段值",
				name : 'columnValue4',
				newline : true,
				type : 'select',
				options : {
					openwin : false,
					valueFieldID : "columnsValue4"
				}
			}, { display : "字段值", 
				name : 'columnValue3', 
				newline : true,
				type : 'date'
			},{
				display : "值范围",
				name : 'value1',
				newline : true,
				width : 90,
				space : 1,
				type : 'select',
				options : {
					valueFieldID : 'value2',
					data : [ {
						text : '大于',
						id : '{'
					}, {
						text : '大于等于',
						id : '['
					} ]
				}
			},{
				display : "",
				space : 1,
				width : 90,
				labelWidth : 5,
				name : "minValue",
				newline : false,
				type : "text"
			}, {
				display : "",
				space : 1,
				width : 90,
				labelWidth : 5,
				name : 'value3',
				newline : false,
				type : 'select',
				options : {
					valueFieldID : 'value4',
					data : [ {
						text : '小于',
						id : '{'
					}, {
						text : '小于等于',
						id : '['
					} ]
				}
			}, {
				display : "",
				space : 1,
				width : 90,
				labelWidth : 5,
				name : "maxValue",
				newline : false,
				type : "text"
			},{
				display : "值范围",
				name : 'value1_1',
				newline : true,
				width : 90,
				space : 1,
				type : 'select',
				options : {
					valueFieldID : 'value2_1',
					data : [ {
						text : '大于',
						id : '{'
					}, {
						text : '大于等于',
						id : '['
					} ]
				}
			},{
				display : "",
				space : 1,
				width : 90,
				labelWidth : 5,
				name : "minValue_1",
				newline : false,
				type : "date"
			}, {
				display : "",
				space : 1,
				width : 90,
				labelWidth : 5,
				name : 'value3_1',
				newline : false,
				type : 'select',
				options : {
					valueFieldID : 'value4_1',
					data : [ {
						text : '小于',
						id : '{'
					}, {
						text : '小于等于',
						id : '['
					} ]
				}
			}, {
				display : "",
				space : 1,
				width : 90,
				labelWidth : 5,
				name : "maxValue_1",
				newline : false,
				type : "date"
			} ]
		})
	}

	function addNewRow() {
		var manager = $("#maingrid").ligerGetGridManager();
		var functionName = "", columnName = colNameCN, columnValue = "", minValue = "", maxValue = "";
		var chackValue1 = "",chackValue2 = "",fuhao1 = "",fuhao2 = "";
		if(colLib != null){
				if(libMap[colLib]&&libMap[colLib][0]&&libMap[colLib][0]['upid'] == null){
				columnValue = document.getElementById('columnsValue4').value;
				functionName =" and "+colName+ "='"+columnValue+"' "
				
				}else{
				columnValue = document.getElementById('columnsValue2').value;
				functionName = " and "+colName+ "='"+columnValue+"' "
				}
				
			}else if(colType == "DATE"){
				columnValue = document.getElementById('columnValue3').value;
				chackValue1 = document.getElementById('minValue_1').value;
				chackValue2 = document.getElementById('maxValue_1').value;
				if(columnValue != null && columnValue != "" ){
					if(!(chackValue1 == null || chackValue1 == "" ) || !(chackValue2 == null || chackValue2 == "" ) ){
						BIONE.tip('字段值与值范围只可存在一种。');
						return;
					}else{
						functionName = " and "+colName+ "=to_date('" + columnValue + "','yyyy-mm-dd') "
					}
				}else{
					columnValue = "";
					fuhao1 = document.getElementById('value2_1').value;
					fuhao2 = document.getElementById('value4_1').value;
					if (fuhao1 == '{') {
						minValue = document.getElementById('minValue_1').value;
						functionName =functionName+ " and "+colName+ " >to_date('" + minValue + "','yyyy-mm-dd') "
					} else if (fuhao1 == '['){
						minValue = document.getElementById('minValue_1').value;
						functionName = functionName+" and "+colName+ " >=to_date('" + minValue + "','yyyy-mm-dd') "
					}
					if (fuhao2 == '{') {
						maxValue = document.getElementById('maxValue_1').value;
						functionName = functionName+" and "+colName+ " <to_date('" + maxValue + "','yyyy-mm-dd') "
					} else if (fuhao2 == '['){
						maxValue = document.getElementById('maxValue_1').value;
						functionName = functionName+" and "+colName+ " <=to_date('" + maxValue + "','yyyy-mm-dd') "
					}
				}
				
				
			}else if(colType == "INTEGER" || colType == "NUMERIC" || colType == "NUMBER" || colType == "DECIMAL"){
				columnValue = document.getElementById('columnValue1').value;
				chackValue1 = document.getElementById('minValue').value;
				chackValue2 = document.getElementById('maxValue').value;
				if(columnValue != null && columnValue != "" ){
					if(!(chackValue1 == null || chackValue1 == "" ) || !(chackValue2 == null || chackValue2 == "" ) ){
						BIONE.tip('字段值与值范围只可存在一种。');
						return;
					}else{
						functionName = " and "+colName+ "=" + columnValue + " "
					}
				}else{
					columnValue = "";
					fuhao1 = document.getElementById('value2').value;
					fuhao2 = document.getElementById('value4').value;
					if (fuhao1 == '{') {
						minValue = document.getElementById('minValue').value;
						functionName =functionName+ " and "+colName+ " >" + minValue + " "
					} else if (fuhao1 == '['){
						minValue = document.getElementById('minValue').value;
						functionName = functionName+" and "+colName+ " >=" + minValue + " "
					}
					if (fuhao2 == '{') {
						maxValue = document.getElementById('maxValue').value;
						functionName = functionName+" and "+colName+ " <" + maxValue + " "
					} else if (fuhao2 == '['){
						maxValue = document.getElementById('maxValue').value;
						functionName = functionName+" and "+colName+ " <=" + maxValue + " "
					}
				}
				
			}else if(colType == "TIMESTAMP" ){
				columnValue = document.getElementById('columnValue1').value;
				functionName = " and "+colName+ "=to_timestamp('" + columnValue + "','yyyy-mm-dd hh:mi:ss') "
			}else {
				columnValue = document.getElementById('columnValue1').value;
				functionName = " and "+colName+ " like '%"+columnValue+"%' "
			}
			if(columnName == ""){
				BIONE.tip('请选择字段并输入值再添加。');
				return;
			}
		manager.addRow({
			templeId : templeId,
			functionName : functionName,
			columnName :columnName,
			columnValue : columnValue,
			minValue : minValue,
			maxValue : maxValue
		});
	}
	
	function initGrid() {

		grid = manager = $("#maingrid").ligerGrid(
				{
					columns : [
							{
								name : 'templeId',
								hide : 1,
								width : '1%'
							},{
								name : 'functionName',
								hide : 1,
								width : '1%'
							},
							{
								display : '字段名',
								width : "15%",
								name : 'columnName'
							},
							{
								display : '数据值',
								name : 'columnValue',
								width : '35%'
							},
							{
								display : '最小值',
								width : "15%",
								name : 'minValue'
							},
							{
								display : '最大值',
								width : "15%",
								name : 'maxValue'
							},
							{
								display : '操作',
								isSort : false,
								width : '10%',
								render : function(rowdata, rowindex, value) {
									var h = "";
									if (!rowdata._editing && !"${lookType}") {
										h += "<a href='javascript:deleteRow("
												+ rowindex + ")'>删除</a> ";
									}

									return h;
								}
							} ],
					checkbox: false,
					rownumbers : true,
					usePager : false,
					enabledEdit : true,
					clickToEdit : true,
					isScroll : false,
					width : '100%',
					data : {
						Rows : [  ]
					}
					//height : '99%'
				});

	}
	function deleteRow(rowid) {
		manager.deleteRow(rowid);
	}
	function submit2(){//确定
		var data = manager.getData();
		var sqlStr = "";
		for(i = 0;i<data.length;i++){
			sqlStr = sqlStr + data[i].functionName;
		}
		sqlStr = sqlStr + " and SYS_DATA_STATE = 'sucess' ";
		UDIP.commonOpenFullDialog("数据预览", "inputTaskInfo",
				"${ctx}/udip/queryData/inputTaskInfoQuery?templeId=" + templeId
						+ "&sqlStr="
						+ encodeURIComponent(encodeURIComponent(sqlStr)));
	}
</script>
</head>
<body>
<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
<div id="template.left.up">
		<span style="font-size: 12">任务树</span>
	</div>
</body>
</html>