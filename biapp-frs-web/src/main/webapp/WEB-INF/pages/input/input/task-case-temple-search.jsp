<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var formName = window.parent.formName;
	var formType = window.parent.formType;
	var formLib = window.parent.formLib;
	var formNullable = window.parent.formNullable;
	var formSelectable = window.parent.formSelectable;
	var formDataType = window.parent.formDataType;
	var formDetail = window.parent.formDetail;
	var rowId = "";
	var chackLine;
	var info;
	var libMap = parent.libMa2;
	//创建表单结构 
	var mainform;
	$(function(){
		var data = [];
		var k = 0;
		for ( var i = 0; i < formName.length; i++) {
			if (formLib[i] == null && formSelectable[i] == "1") {
				data.push({
					display : formName[i],
					name : formType[i],
					type : (formDataType[i]=="DATE")?"date":"text",
					width : "120",
					newline : k % 2 == 0?true:false
				});
				k=k+1;
			} else if(formSelectable[i] == "1"){
				data.push({
					display : formName[i],
					name : formType[i],
					newline : k % 2 == 0?true:false,
					type : 'select',
					width : "120",
					options : {
						openwin : (libMap[formLib[i]]&&libMap[formLib[i]][0]&&libMap[formLib[i]][0]['upid'] == null)?false:true,
						valueFieldID : formName[i]
					},
					validate : {
						required : true
					}
				});
				k=k+1;
			}

		}
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : data
		});
		for ( var i = 0; i < formType.length; i++) {
			
			if (formLib[i] != null && formSelectable[i] == "1") {
				if(libMap[formLib[i]][0]['upid'] == null){
					$("#"+formType[i]).ligerGetComboBoxManager().setData(libMap[formLib[i]]);
				}else{
					if("${orgColumn}"==formType[i]&& "${orgOwn}"){
						var libText = getLibText(libMap[formLib[i]],"${orgOwn}");
						$("#mainform input[name='"+formType[i]+"']").val(libText);
						$("#mainform input[name='"+formName[i]+"']").val("${orgOwn}");
						$("#"+formType[i]).ligerComboBox({
							onBeforeOpen : function (newvalue){
			                    return false;
			                }
						});
					}else{
						$("#"+formType[i]).ligerComboBox({
							onBeforeOpen : set(i)
						});
					}
				}
				
			}
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '关闭',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '查询',
			onclick : save_objDef
		});
		BIONE.addFormButtons(buttons);
		$("#mainform [name='templeId']").val("${id}");
		
		for(var i = 0; i < formType.length; i++){
			if(formSelectable[i] == "1"){
				$("#"+formType[i]).attr('title',formDetail[i]);
			}
		}
	});
	
	function  set(i){
		return function(){
			openSelectNewDilog(formLib[i],formType[i],formName[i]);
			return false;
		}
	}
		
	function openSelectNewDilog(lib,formType,formName){
		dialog = BIONE.commonOpenDialog("数据字典内容预览", "libraryAddWin", "500", "400","${ctx}/udip/library/getFormTypeName?orgId="+"${orgOwn}"+"&libId=" + lib + "&templeId=" +"${templeId}" +"&caseId=" + "${caseId}" +"&formType=" + encodeURIComponent(encodeURIComponent(formType))+ "&formName=" + encodeURIComponent(encodeURIComponent(formName)));
	}
	function cancleCallBack() {
		BIONE.closeDialog("taskcaseSearch");
	}
	function save_objDef() {
		//var obj = {};
		var chackData = "";
		var sqlStr = "";
		
		for ( var i = 0; i < formName.length; i++) {
			if (formLib[i] == null && formSelectable[i] == "1" && formDataType[i] =="DATE") {
				chackData  = document.getElementById(formType[i]).value;
				if(chackData!= ""){
					sqlStr = sqlStr+" and "+formType[i]+ "=to_date('" + chackData + "','yyyy-mm-dd') "
				}
			} else if (formLib[i] == null && formSelectable[i] == "1" && formDataType[i] !="DATE") {
				chackData  = document.getElementById(formType[i]).value;
				if(chackData!= ""){
					sqlStr = sqlStr+" and "+formType[i]+ " like '%"+chackData+"%' "
				}
			} else if(formSelectable[i] == "1"){
				chackData = document.getElementById(formName[i]).value;
				if(chackData!= ""){
					sqlStr = sqlStr+" and "+formType[i]+ "='"+chackData+"' "
				}
			}
		}
		parent.searchRow(sqlStr);
		BIONE.closeDialog("taskcaseSearch");
	}
	function getLibText(libObj,libId){
		for(var i=0;i<libObj.length;i++){
			if(libObj[i]['id']==libId){
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