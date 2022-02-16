<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var node = window.parent.currentNode;
	var treeObj = window.parent.treeObj;
	//创建表单结构 
	var mainform = null;
	var catalogId = "${catalogId}";
	
	$(function() {
		
		initForm();
		initData();
		initButtons();
	});
	
	function initForm(){
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				name : "catalogId",
				type : "hidden"
			},  {
				name : "upId",
				type : "hidden"
			},  {
				display : "目录名称",
				name : "catalogNm",
				newline : true,
				type : "text",
				group : "模板目录",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 200
				}
			},{
				display : "上级目录",
				name : "upName",
				newline : false,
				type : "text",
				attr:{
					readonly : true
				}
			},{
				display : "备注",
				name : "templateRemark",
				newline : true,
				width : '453',
				type : "textarea"
			}]
		});
		$("#templateRemark").css("resize","none");
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	}
	
	function initData(){
		if(catalogId != ""){
			BIONE.loadForm(mainform,{
				type : "POST",
				url : "${ctx}/report/frame/detailtmp/getCatalogInfo?catalogId="
					+ catalogId+"&d="+new Date().getTime()});
			$("#upName").val(node.getParentNode().text);
		}
		else{
			$("#upId").val(node.id)
			$("#upName").val(node.text);
		}
	}
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleHandler
		});
		buttons.push({
			text : '保存',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
	
	function f_save() {
		BIONE.submitForm($("#mainform"), function(res) {
			if(catalogId == ""){
				var cnode ={
					id : res.catalogId,
					text : $("#catalogNm").val(),
					upId :  $("#upId").val(),
					icon : "${ctx}/images/classics/icons/f1.gif",
					params :{
						nodeType : "02"
					}
				}
				treeObj.addNodes(node,cnode,false);
			}
			else{
				node.text = $("#catalogNm").val();
				treeObj.updateNode(node);
			}
			BIONE.closeDialog("editCatalogWin", "保存成功");
		}, function() {
			BIONE.tip("editCatalogWin", "保存失败");
		});
	}
	
	function cancleHandler() {
		BIONE.closeDialog("editCatalogWin");
	}
	
	
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/detailtmp/saveCatalog"  method="post"></form>
	</div>
</body>
</html>