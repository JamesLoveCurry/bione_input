<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var formName = window.parent.formName;
	var formType = window.parent.formType;
	var formLib = window.parent.formLib;
	var formNullable = window.parent.formNullable;
	var formDataType = window.parent.formDataType;
	var formDetail = window.parent.formDetail;
	var formWritable = window.parent.formWritable;
	var defaultValue = window.parent.defaultValue;
	var rowId = "";
	var chackLine;
	var info;
	var libMap = parent.libMa2;
	var canEdit = window.parent.canSave;
	// 创建表单结构
	var mainform;
	var manager = parent.$("#maingrid").ligerGetGridManager();
	$(function() {
		if ("${rowindex}") {
			rowId = "${rowindex}";
		}
		var data = [];
		for ( var i = 0; i < formName.length; i++) {
			if (formLib[i] == null||formLib[i]=="") {
				var dataType = (formDataType[i] == "DATE"||formDataType[i]=="TIMESTAMP") ? "date" : "text";
				var displayText = formName[i] + ((formNullable[i] == "1") ? "<font color='red'>*</font>" : "");
				//if(defaultValue[i]&&defaultValue[i]!=null&&defaultValue[i]!="")
				//	displayText=displayText+"(默认值:"+defaultValue[i]+")";
				var dataInfo = {
					display : displayText,
					name : formType[i],
					labelAlign : 'right',
					type : dataType,
					newline : i % 2 == 0 ? true : false,
					options : {
						readonly : (formWritable[i] == "1") ? false : true
					}
				};
				if(formDataType[i] == "DATE")
				{
					dataInfo.options.format = "yyyyMMdd";
					dataInfo.options.showTime = true;
				}else if(formDataType[i] == "TIMESTAMP"){
					dataInfo.options.format = "yyyy-MM-dd hh:mm:ss";
					dataInfo.options.showTime = true;
				}
				data.push(dataInfo);
			} else {
				data.push({
					display : formName[i] + ((formNullable[i] == "1") ? "<font color='red'>*</font>" : ""),
					name : formType[i],
					newline : i % 2 == 0 ? true : false,
					type : 'select',
					labelAlign : 'right',
					comboboxName : formType[i],
					options : {
						openwin : (libMap[formLib[i]] && libMap[formLib[i]][0] && libMap[formLib[i]][0]['upid'] == null) ? false : true,
						valueFieldID : formType[i],
						readonly : (!formWritable[i]||formWritable[i]==""||formWritable[i] == "1") ? false : true
					}
				});
			}
		}
		mainform = $("#mainform");
		mainform.ligerForm({
			inputWidth : 160,
			labelWidth : 180,
			fields : data
		});
		for ( var i = 0; i < formType.length; i++) {
			if(defaultValue[i]&&defaultValue[i]!=null&&defaultValue[i]!=""&&defaultValue[i]!="undefined")
				$("#mainform input[name='" + formType[i] + "']").val(defaultValue[i]);
			if(!canEdit)
				$("#mainform input[name='" + formType[i] + "']").attr("readonly", "true");
		}
		for ( var i = 0; i < formType.length; i++) {
			if (formLib[i]) {
				if (libMap[formLib[i]] && libMap[formLib[i]][0] && libMap[formLib[i]][0]['upid'] == null) {
					$("#" + formType[i]).ligerGetComboBoxManager().setData(libMap[formLib[i]]);
				} else {
					if ("${orgColumn}" == formType[i] && "${orgOwn}") {
						var libText = getLibText(libMap[formLib[i]], "${orgOwn}");
						$("#mainform input[name='" + formType[i] + "']").val(libText);
						$("#mainform input[name='" + formName[i] + "']").val("${orgOwn}");
						$("#" + formType[i]).ligerComboBox({
							onBeforeOpen : function(newvalue) {
								return false;
							}
						});
					} else {
						$("#" + formType[i]).ligerComboBox({
							onBeforeOpen : set(i)
						});
					}
				}
			}
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		var butText = "";
		buttons.push({
			text : '关闭', onclick : cancleCallBack
		});
		if ("${paramStr}" != "") {
			setParamInfo("${paramStr}", rowId);
			if(canEdit){
				buttons.push({
					// 修改而不关闭
					text : "应用", onclick : apply
				});
				buttons.push({
					// 修改并关闭
					text : "确定", onclick : submit
				});
			}
		} else {
			buttons.push({
				// 添加不关闭
				text : "添加", onclick : add
			});
		}
		BIONE.addFormButtons(buttons);
		$("#mainform [name='templeId']").val("${id}");
		for ( var i = 0; i < formType.length; i++) {
			$("#" + formType[i]).attr('title', formDetail[i]);
		}
	});
	function setParamInfo(paramStr, rowindex) {
		if (rowindex) {
			rowId = rowindex;
		}
		var param = paramStr.split(",");
		for ( var i = 0; i < param.length; i++) {
			if (formLib[i]) {
				var libText = getLibText(libMap[formLib[i]], param[i]);
				$('#' + formType[i]).ligerGetComboBoxManager()._changeValue(param[i], libText);
			} else {
				$('#' + formType[i]).val(param[i]);
			}
		}
	}
	
	function set(i) {
		return function() {
			openSelectNewDilog(formLib[i], formType[i], formName[i]);
			return false;
		}
	}
	
	function openSelectNewDilog(lib, formType, formName) {
		dialog = BIONE.commonOpenDialog(formName, 'libraryAddWin', 500, 400, "${ctx}/rpt/input/library/getFormTypeName?orgId=" + "${orgOwn}" + "&libId=" 
				+ lib + "&templeId=" + "${templeId}" + "&caseId=" + "${caseId}" + "&formType=" + encodeURIComponent(encodeURIComponent(formType))
				+ "&formName=" + encodeURIComponent(encodeURIComponent(formName)));
	}
	function cancleCallBack() {
		BIONE.closeDialog("dataRules");
	}
	
	function apply() {// 应用
		var obj = {};
		for ( var i = 0; i < formType.length; i++) {
			if (formLib[i]) {
				obj[formType[i]] = $('#' + formType[i]).ligerGetComboBoxManager().getValue();
				if (obj[formType[i]] == "" && formNullable[i] == "1") {
					BIONE.showError(formName[i] + "不能为空。");
					return;
				}
			} else {
				obj[formType[i]] = document.getElementById(formType[i]).value;
				if (obj[formType[i]] == "" && formNullable[i] == "1") {
					BIONE.showError(formName[i] + "不能为空。");
					return;
				}
			}
		}
		if (parent.updateRow["data_type"] != '4')// 只有不是添加的情况,才将状态设置为修改
			obj["data_type"] = '3';
		/********添加修改日志相关   cl*********************/
		var fieldList = [];
		var modifyInfo = {
			fieldId : parent.updateRow.DATAINPUT_ID,
			fieldInfo : []
		};
		for(var x = 0 ;x <formType.length;x++){
			modifyInfo.fieldInfo.push({
				fieldName: formType[x],
				oldValue : parent.updateRow[formType[x]],
				value : obj[formType[x]]
			});
		}
		fieldList.push(modifyInfo);
		parent.fieldModifyLog(fieldList);
		/********添加修改日志相关***********************/
		manager.updateRow(parent.updateRow, obj);
	}
	
	function add() {// 添加
		var obj = {};
		for ( var i = 0; i < formType.length; i++) {
			if (formLib[i]) {
				obj[formType[i]] = $('#' + formType[i]).ligerGetComboBoxManager().getValue();
				if (obj[formType[i]] == "" && formNullable[i] == "1") {
					BIONE.showError(formName[i] + "不能为空。");
					return;
				}
			} else {
				obj[formType[i]] = document.getElementById(formType[i]).value;
				if (obj[formType[i]] == "" && formNullable[i] == "1") {
					BIONE.showError(formName[i] + "不能为空。");
					return;
				}
			}
		}
		obj["data_type"] = '4';
		/********添加新增日志相关   cl*********************/
		var fieldList = [];
		var addInfo = {
			fieldInfo : []
		};
		for(var x = 0 ;x <formType.length;x++){
			addInfo.fieldInfo.push({
				fieldName: formType[x],
				value : obj[formType[x]]
			});
		}
		fieldList.push(addInfo);
		parent.fileAddLog(fieldList);
		/********添加新增日志相关***********************/
		manager.addRow(obj);
	}
	
	function submit() {// 确定
		apply();
		BIONE.closeDialog("dataRules");
	}
	function setDataZidina(name, id, formName, formType) {
		// $("#mainform input[name='" + formType + "']").val(name);
		// $("#mainform [name='" + formName + "']").val(id);
		$('#' + formType).ligerGetComboBoxManager()._changeValue(id, name);
	}
	function getLibText(libObj, libId) {
		for ( var i = 0; i < libObj.length; i++) {
			if (libObj[i]['id'] == libId) {
				return libObj[i]['text'];
			}
		}
		return "";
	};
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform"></form>
	</div>
</body>
</html>