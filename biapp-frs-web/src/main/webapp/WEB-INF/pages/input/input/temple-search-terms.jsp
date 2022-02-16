<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template1_8.jsp">
<head>
<script type="text/javascript"
	src="${ctx}/js/udip/datainput/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript"
	src="${ctx}/js/udip/datainput/remark/temple.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid, btns, url, ids = [], dialog, buttons = [];
	var selection = [],columnList = [];
	var colType = "",colName = "",colLib = "",colNameCN = "";
	var manager;
	var libMap = {};
	$(function() {
		$.ajax({
			async : false,
			url : '${ctx}/udip/data/getColumnList/' + "${templeId}",
			success : function(data1) {
				selection = data1;
			}
		});
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/udip/data/getColumnInfo.json",
			dataType : 'json',
			type : "get",
			data : {
				"id" : "${templeId}"
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
				"templeId" : "${templeId}"
			},
			success : function(data2) {
			libMap = data2;
			}
		});		
		searchForm();
		initGrid();
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '添加',
			icon : 'add',
			width : '50px',
			click : function() {
				addNewRow();
			}
		});
		if(!"${look}"){
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '在线补录',
				icon : 'input',
				width : '70px',
				click : function() {
					submit();
				}
			});
		}
			BIONE.createButton({
				appendTo : "#searchbtn",
				text : '数据预览',
				icon : 'view',
				width : '70px',
				click : function() {
					submit2();
				}
			});
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '关闭',
			icon : 'delete',
			width : '50px',
			click : function() {
				closeWindos();
			}
		});
		dispCSS();
		$("#columnName").ligerComboBox({
			onSelected : function() {
				var columnName = document.getElementById('columnsName').value;
				colType = columnList[columnName]["columnType"];
				colName = columnName;
				colLib = columnList[columnName]["dataZidian"];
				colNameCN = columnList[columnName]["columnComment"];
				setCSS();
			}
		});
	});
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
	}
	function setCSS(){
		if(colLib != null){
			setForm(colType);
			if(libMap[colLib]&&libMap[colLib][0]&&libMap[colLib][0]['upid'] == null){
				$("#columnValue4").ligerGetComboBoxManager().setData(libMap[colLib]);
			}else{
				$("#columnValue2").ligerGetComboBoxManager().setData(libMap[colLib]);
					if("${orgColumn}"==colName&& "${orgOwn}"){
						$("#columnValue2").ligerGetComboBoxManager().selectValue("${orgOwn}");
						$("#columnValue2").ligerComboBox({
							onBeforeOpen : function (){
			                    return false;
			                }
						});
					}else{
						$("#columnValue2").ligerComboBox({
							onBeforeOpen : function (){
				    			openSelectNewDilog();
								return false;
							}
						});
					}
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
			url :"${ctx}/udip/library/getFormTypeName?orgId="+"${orgOwn}"+"&libId=" + colLib + "&templeId=" +"${templeId}" +"&caseId=" + "${caseId}"+ "&formType=" + encodeURIComponent(encodeURIComponent(formType))+ "&formName=" + encodeURIComponent(encodeURIComponent(formName))
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
					valueFieldID : 'columnsName',
					data : selection
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
		var templeId = "${templeId}", functionName = "", columnName = colNameCN, columnValue = "", minValue = "", maxValue = "";
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
					rownumbers : true,
					usePager : false,
					enabledEdit : true,
					clickToEdit : true,
					isScroll : false,
					width : '100%',
					data : {
						Rows : [  ]
					},
					height : '99%'
				});

	}
	function deleteRow(rowid) {
		manager.deleteRow(rowid);
	}
	function submit(){//确定
		var data = manager.getData();
		var sqlStr = "";
		for(i = 0;i<data.length;i++){
			sqlStr = sqlStr + data[i].functionName;
		}
		$.ajax({
			url : "${ctx}/udip/taskcase/authRecordDataType?d=" + new Date().getTime(),
			type : 'get',
			async : true,
			data: {
				templeId: "${templeId}",
				caseId : "${caseId}"
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在检查补录表信息...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if(data == "1"){
					BIONE.tip("补录表已不存在或已修改。");
				}else if(data == "0"){
					BIONE.tip("该批次补录已结束。");
				}else if(data=="submit" || data=="sucess" ){
					BIONE.tip('数据已提交。');
				}else{
					parent.searchDataInfo("${templeId}","${caseId}","",sqlStr);
					BIONE.closeDialog("search");
				}
			}
		});
	}
	function submit2(){//确定
		var data = manager.getData();
		var sqlStr = "";
		for(i = 0;i<data.length;i++){
			sqlStr = sqlStr + data[i].functionName;
		}
		parent.look("",sqlStr);
		BIONE.closeDialog("search");
	}
	function closeWindos(){
		BIONE.closeDialog("search");
	}
</script>
</head>
<body>
</body>
</html>