<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var uuid = "${uuid}"
	var datagrid = null;
	//创建表单结构 
	var mainform = null;
	var param = window.parent.getParam(uuid);
	$(function() {
		initForm();
		initButtons();
		initData();
	});
	
	function initForm(){
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ {
				name : "uuid",
				type : "hidden"
			},{
				display : "显示名称",
				name : "cnNm",
				newline : true,
				type : "text",
				group : "查询属性",
				groupicon : groupicon
			},{
				display : "是否必填",
				name : "required",
				newline : true,
				type : "select",
				options : {
					data : [ {
						text : '是',
						id : "1"
					}, {
						text : '否',
						id : "0" 
					}]
				}
			},{
				display : "默认值",
				name : "defValue",
				newline : false,
				type : "text"
			},{
				display : "是否复选",
				name : "checkbox",
				newline : true,
				type : "select",
				options : {
					data : [ {
						text : '是',
						id : "1"
					}, {
						text : '否',
						id : "0"
					}]
				}
			},{
				display : "是否转码",
				name : "isConver",
				newline : false,
				type : "select",
				options : {
					data : [ {
						text : '是',
						id : "1"
					}, {
						text : '否',
						id : "0"
					}]
				}
			},{
				display : "是否区间",
				name : "range",
				newline : true,
				type : "select",
				options : {
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N' 
					}],
					onSelected: function(id,value){
						if(param.elementType == "05" || param.elementType == "06"){
							if(id == "Y"){
								$("#daterange").parent().parent().parent().show();
							}
							else{
								$("#daterange").parent().parent().parent().hide();	
							}
						}
						else{
							$("#daterange").parent().parent().parent().hide();
						}
					}
				}
			},{
				display : "区间范围",
				name : "daterange",
				newline : false,
				text : "digits"
			},{
				display : "数据值",
				name : "dataJson",
				width : '450',
				newline : true,
				text : "text"
			}]
		});
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	}
	
	function initDataGrid(){
		$("#dataJson").parent().html("<div id='datagrid'></div>").css("height","100px");
		datagrid = $("#datagrid").ligerGrid({
			checkbox : false,
			columns : [ {
				display : '数值',
				name : 'id',
				width : "40%",
				align : 'left',
				editor :{
					type : "digits"
				}
			}, {
				display : '文本',
				name : 'text',
				width : "40%",
				align : 'center',
				editor :{
					type : "text"
				}
			}, {
				display : '操作',
				name : 'oper',
				width : "10%",
				align : 'center',
				render : function(a,b,c){
					return "<a onclick = 'f_del(\""+a.__id+"\")'>删除</a>";
				}
			}],
			clickToEdit : true,
			enabledEdit : true,
			dataAction : 'local', //从后台获取数据
			usePager : false, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : false,
			rownumbers : true,
			height:100,
			width:'99%'
		});
		var data = JSON2.parse(param.dataJson);
		datagrid.addRows(data);
		var $btn = $('<div class="l-icon l-icon-add"style="margin:auto;cursor:pointer;margin-top:2px"></div>');
		$btn.bind("click",function(){
			datagrid.addRow({
				id : 10,
				text : 10
			});
		});
		$(".l-grid-hd-cell-rownumbers").find(".l-grid-hd-cell-inner").html($btn);
		
	}
	
	function f_del(id){
		datagrid.remove(datagrid.getRow(id) );
	}
	
	function initData(){
		$("#uuid").val(param.uuid);
		$("#cnNm").val(param.cnNm);
		$("#defValue").val(param.defValue);
		$("#daterange").val(param.daterange);
		$("#dataJson").parent().parent().parent().hide();
		if(param.required)
			$.ligerui.get("required").selectValue("1");
		else
			$.ligerui.get("required").selectValue("0");
		
		if(param.elementType == "03" || param.elementType == "04"){
			if(param.checkbox == "true")
				$.ligerui.get("checkbox").selectValue("1");
			else
				$.ligerui.get("checkbox").selectValue("0");
			$.ligerui.get("isConver").selectValue(param.isConver);
		}
		else{
			$("#checkbox").parent().parent().parent().hide();
			$("#isConver").parent().parent().parent().hide();
		}
		
		$("#daterange").parent().parent().parent().hide();
		if(param.elementType == "02"){
			$.ligerui.get("range").selectValue("N");
		}
		else if(param.elementType == "05"){
			$.ligerui.get("range").selectValue("N");
		}
		else if(param.elementType == "06"){
			$.ligerui.get("range").selectValue("Y");
			$("#daterange").parent().parent().parent().show();
		}
		else if(param.elementType == "08"){
			$.ligerui.get("range").selectValue("Y");
		}
		else if(param.elementType == "09"){
			$("#range").parent().parent().parent().hide();
			$("#dataJson").parent().parent().parent().show();
			initDataGrid();
		}
		else{
			$("#range").parent().parent().parent().hide();
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
		param.cnNm = $("#cnNm").val();
		param.defValue = $("#defValue").val();
		if($("#required").val() == "1"){
			param.required = true;
		}
		else{
			param.required = false;
		}
		if($("#checkbox").val() == "1"){
			param.checkbox = "true";
		}
		else{
			param.checkbox = "false";
		}
		if(param.elementType == "02" || param.elementType == "08"){
			if($("#range").val() == "Y"){
				param.elementType = "08"
			}
			else{
				param.elementType = "02"
			}
		}
		if(param.elementType == "05" || param.elementType == "06"){
			if($("#range").val() == "Y"){
				param.elementType = "06"
			}
			else{
				param.elementType = "05"
			}
		}
		param.daterange = $("#daterange").val();
		
		param.isConver = $("#isConver").val();
		if(datagrid){
			var data = datagrid.getData();
			var js = [];
			for(var i =0;i<data.length;i++){
				if(data[i].id && data[i].text){
					if(parseInt(data[i].id) == data[i].id && parseInt(data[i].id)>0){
						js.push(data[i]);
					}
				}
			}
			param.dataJson = JSON2.stringify(js);
		}
		var vals = window.parent.getParams(param.uuid);
		window.parent.createParams("searchForm",window.parent.params,vals);
		window.parent.panel.rename(uuid,$("#cnNm").val());
		BIONE.closeDialog("searchEdit");
	}
	
	function cancleHandler() {
		BIONE.closeDialog("searchEdit");
	}
	
	
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/design/cfg/custom/uptRpt"  method="post"></form>
	</div>
</body>
</html>