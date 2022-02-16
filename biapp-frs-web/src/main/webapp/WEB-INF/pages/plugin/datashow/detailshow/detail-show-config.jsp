<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>

<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var cfgId = "${cfgId}"
	//创建表单结构 
	var mainform = null;
	var colInfo = window.parent.colInfos[cfgId];
	
	$(function() {
		initForm();
		initButtons();
		initData();
	});
	
	function initForm(){
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ {
				name : "cfgId",
				type : "hidden"
			},{
				display : "显示名称",
				name : "displayNm",
				newline : true,
				type : "text",
				group : "列属性",
				groupicon : groupicon
			}, /* {
				display : "序号",
				name : "orderno",
				newline : true,
				type : "digits",
				validate : {
					required : true
				}
			}, */{
				display : "是否转码",
				name : "isConver",
				newline : false,
				type : "select",
				options : {
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N' 
					}]
				}
			},{
				display : "数据类型",
				name : "dataType",
				newline : true,
				type : "select",
				options : {
					data : [ {
						text : '文本',
						id : '01'
					},{
						text : '数字',
						id : '02' 
					},{
						text : '日期',
						id : '03' 
					}],
					readonly: true
				}
			}, {
				display : "显示格式",
				name : "displayFormat",
				newline : false,
				type : "select",
				options : {
					data : [],
					onSelected: function(id,value){
						setDataInfo(id);
					}
				}
			}, {
				display : "数据单位",
				name : "dataUnit",
				newline : true,
				type : "select",
				options : {
					data : [{
						id : "01",
						text : "个"
					},{
						id : "02",
						text : "百"
					},{
						id : "03",
						text : "千"
					},{
						id : "04",
						text : "万"
					},{
						id : "05",
						text : "亿"
					}]
				}
			},{
				display : "数据精度",
				name : "dataPrecision",
				newline : false,
				type : "digits"
			},{
				display : "是否汇总",
				name : "isSum",
				newline : true,
				type : "select",
				options : {
					data : [{
						id : "Y",
						text : "是"
					},{
						id : "N",
						text : "否"
					}]
				}
			}]
		});
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	}
	
	function initData(){
		$("#cfgId").val(colInfo.id.cfgId);
		$("#displayNm").val(colInfo.displayNm);
		$("#orderno").val(colInfo.orderno);
		if(colInfo.dimTypeNo == null || colInfo.dimTypeNo == "" || colInfo.dimTypeNo == "DATE"){
			$("#isConver").parent().parent().parent().hide();
		}
		$.ligerui.get("isConver").selectValue(colInfo.isConver);
		$.ligerui.get("dataType").selectValue(colInfo.dataType);
		if(colInfo.dataType == "01"){
			$("#displayFormat").parent().parent().parent().hide();
		}
		if(colInfo.dataType == "02"){
			$.ligerui.get("displayFormat").setData([{
				id: "00",
				text : "原格式"
			},{
				id: "01",
				text : "金额"
			},{
				id: "02",
				text : "比例"
			},{
				id: "03",
				text : "数值"
			}]);
			$.ligerui.get("displayFormat").selectValue(colInfo.displayFormat);
		}
		if(colInfo.dataType == "03"){
			$.ligerui.get("displayFormat").setData([{
				id: "yyyy年",
				text : "年"
			},{
				id: "yyyy年MM月",
				text : "年月"
			},{
				id: "yyyy年MM月dd日",
				text : "年月日"
			},{
				id: "yyyy年MM月dd日  hh:mm:ss",
				text : "年月日时分秒"
			}]);
			$.ligerui.get("displayFormat").selectValue(colInfo.displayFormat);
		}
		$.ligerui.get("dataUnit").selectValue(colInfo.dataUnit);
		$("#dataPrecision").val(colInfo.dataPrecision);
		$("#dataUnit").parent().parent().parent().parent().parent().hide();
		$.ligerui.get("isSum").selectValue(colInfo.isSum);
		$("#isSum").parent().parent().parent().hide();
		if(colInfo.dataType == "02"){
			setDataInfo(colInfo.displayFormat);
			if(colInfo.displayFormat != "02"){
				$("#isSum").parent().parent().parent().show();
			}
		}
		
		
	}
	
	function setDataInfo(id){
		if(id == "00"){
			$("#isSum").parent().parent().parent().show();
			$("#dataUnit").parent().parent().parent().parent().parent().hide();
		}
		else if(id == "01"){
			$("#dataUnit").parent().parent().parent().parent().parent().show();
			$("#dataUnit").parent().parent().parent().parent().show();
			$("#dataPrecision").parent().parent().parent().show();
			$.ligerui.get("dataUnit").setData([{
				id : "01",
				text : "元"
			},{
				id : "02",
				text : "百元"
			},{
				id : "03",
				text : "千元"
			},{
				id : "04",
				text : "万元"
			},{
				id : "05",
				text : "亿元"
			}]);
			$("#isSum").parent().parent().parent().show();
		}
		else if(id == "02"){
			$("#dataUnit").parent().parent().parent().parent().parent().show();
			$("#dataUnit").parent().parent().parent().parent().hide();
			$("#dataPrecision").parent().parent().parent().show();
			$("#isSum").parent().parent().parent().hide();
		}
		else if(id == "03"){
			$("#dataUnit").parent().parent().parent().parent().parent().show();
			$("#dataUnit").parent().parent().parent().parent().show();
			$("#dataPrecision").parent().parent().parent().hide();
			$.ligerui.get("dataUnit").setData([{
				id : "01",
				text : "个"
			},{
				id : "02",
				text : "百"
			},{
				id : "03",
				text : "千"
			},{
				id : "04",
				text : "万"
			},{
				id : "05",
				text : "亿"
			}]);
			$("#isSum").parent().parent().parent().show();
		}
		else{
			$("#isSum").parent().parent().parent().hide();
			$("#dataUnit").parent().parent().parent().parent().parent().hide();
		}
	}
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleHandler
		});
		buttons.push({
			text : '确认',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
	
	function f_save() {
		colInfo.displayNm = $("#displayNm").val();
		colInfo.orderno = $("#orderno").val();
		colInfo.isConver = $("#isConver").val();
		colInfo.displayFormat = $("#displayFormat").val();
		colInfo.dataUnit = $("#dataUnit").val();
		if(colInfo.displayFormat == "03")
			colInfo.dataPrecision ="0";
		else
			colInfo.dataPrecision = $("#dataPrecision").val();
		colInfo.isSum = $("#isSum").val();
		for(var i in window.parent.columns){
			if(window.parent.columns[i].name == colInfo.id.cfgId){
				window.parent.columns[i].display = colInfo.displayNm;
				break;
			}
		}
		window.parent.grid.changeHeaderText(colInfo.id.cfgId, colInfo.displayNm);
		BIONE.closeDialog("attrEdit");
	}
	
	function cancleHandler() {
		BIONE.closeDialog("attrEdit");
	}
	
	
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/design/cfg/custom/uptRpt"  method="post"></form>
	</div>
</body>
</html>