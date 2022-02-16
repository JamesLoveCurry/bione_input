<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var upNo='${upNo}';
	var indexCatalogNm='${indexCatalogNm}';
	var remark='${remark}';
	var upIndexCatalogNm='${upIndexCatalogNm}';
	var indexCatalogNo='${indexCatalogNo}';
	var catalogOrder='${catalogOrder}';
    var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
			mainform = $("#mainform").ligerForm({
			align : "center",
			fields : [ {
				name : "indexCatalogNo",
				type : "hidden"
			}, {
				group : "目录信息",
				groupicon : groupicon,
				display : "上级目录 ",
				name : "upIndexCatalogNm",
				newline : false,
				type : "text"
			}, {
				display : "目录名称",
				name : "indexCatalogNm",	
				newline : false,
				type : "text",
				validate : {
					required : true,
				    maxlength : 100,
	        		remote :  {
	        			url : "${ctx}/report/frame/idx/testSameIndexCatalogNm",
						type : "POST",
						data : {
							"indexCatalogNo" :indexCatalogNo,
							"upNo" :upNo
						}
	        		},
					messages : {
						remote:"相同路径下指标目录已存在"
					}
				}
			}, {
				display : "排列顺序",
				name : "catalogOrder",
			    type : 'number',
			    newline: true,
			    validate : {
			    	//required : true,
			    	digits : true,
			    	maxlength : 5
			    }
			}, {
				display : "备注 ",
				name : "remark",
				newline : true,
				type : "textarea",
				width:490,
				validate : {
				    maxlength : 500
				}
		    } ]
		});
			$("#mainform [name='indexCatalogNo']").val(indexCatalogNo);
			$("#mainform [name='indexCatalogNm']").val(indexCatalogNm);
			$("#mainform [name='upIndexCatalogNm']").val(upIndexCatalogNm);
			$("#mainform [name='remark']").val(remark);
			$("#mainform [name='catalogOrder']").val(catalogOrder);
			$("#mainform [name='upIndexCatalogNm']").attr("disabled", "disabled");
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		}); 
		buttons.push({
			text : '保存',
			onclick : f_update
		});
		BIONE.addFormButtons(buttons);
	    
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	});

	function f_update() {
		var treeObj = window.parent.currentTree;
		if(!treeObj){
			treeObj = window.parent.leftTreeObj;
		}
		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功");
			treeObj.reAsyncChildNodes(window.parent.currentNode.getParentNode(), "refresh");	
			BIONE.closeDialog("rptIdxCatalogUpdateWin");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	function f_close() {
		BIONE.closeDialog("rptIdxCatalogUpdateWin");
	}
</script>

<title>指标目录管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/report/frame/idx/updateCatalog">
	</form>
</div>
</body>
</html>