<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var upNo='${upNo}';
	var upName='${upName}';
	var catalogId='${catalogId}';
	var catalogName='${catalogName}';
	var orderNo='${orderNo}';
	var catalogType='${catalogType}';
	var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
			mainform = $("#mainform").ligerForm({
				align : "center",
				fields : [ {
			    	name:'upNo',
			    	type:'hidden'
			    }, {
			    	name:'catalogId',
			    	type:'hidden'
			    }, {
			    	name:'catalogType',
			    	type:'hidden'
			    }, {
			    	group : "目录信息",
					groupicon : groupicon,
					display : "上级目录 ",
					name : "upName",
					newline : false,
					type : "text",
					attr : {
						disabled : "disabled;"
					}
				},{
					display : "目录名称",
					name : "catalogName",
					newline : false,
					type : "text",
					validate : {
					    required : true,
					    maxlength : 100,
		        		remote :  {
		        			url : "${ctx}/rpt/input/catalog/testSameInputCatalogNm",
							type : "POST",
							data : {
								"upNo" :upNo,
								"catalogId":catalogId,
								"catalogType":catalogType
							}
		        		},
						messages : {
							remote:"相同目录下已存在同名目录"
						}
					}
				}, {
					display : "顺序编号 ",
					name : "orderNo",
					newline : true,
					type : "text",
					validate : {
						required : true
					}
			    } ]
		});
		
		$("#mainform [name='upNo']").val(upNo);
		$("#mainform [name='upName']").val(upName);
		$("#mainform [name='catalogName']").val(catalogName);
		$("#mainform [name='catalogId']").val(catalogId);
		$("#mainform [name='orderNo']").val(orderNo);
		$("#mainform [name='catalogType']").val(catalogType);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);
	    
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	});

	function f_save() {
		var treeObj = window.parent.taskTree;
		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功");
			if(catalogId!=null&& catalogId!=""){
				treeObj.reAsyncChildNodes(window.parent.currentNode.getParentNode(), "refresh");
				window.parent.currentNode = null;
			}else{
				treeObj.reAsyncChildNodes(null, "refresh");	//刷新整个树
			}
			BIONE.closeDialog("inputEditCatalog");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	function f_close() {
		BIONE.closeDialog("inputEditCatalog");
	}
</script>

<title>目录管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/input/catalog/editTempleCatalog.json">
	</form>
</div>
</body>
</html>