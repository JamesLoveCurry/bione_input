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
	var remark='${remark}';
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
	        			url : "${ctx}/rpt/input/idxdatainput/testSameInputCatalogNm",
						type : "POST",
						data : {
							"upNo" :upNo,
							"catalogId":catalogId
						}
	        		},
					messages : {
						remote:"相同路径下指标目录已存在"
					}
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
			$("#mainform [name='upNo']").val(upNo);
			$("#mainform [name='upName']").val(upName);
			$("#mainform [name='catalogName']").val(catalogName);
			$("#mainform [name='catalogId']").val(catalogId);
			$("#mainform [name='remark']").val(remark);
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
		var treeObj = window.parent.leftTreeObj;
		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功");
			if(catalogId!=null&& catalogId!="")
				treeObj.reAsyncChildNodes(window.parent.currentNode.getParentNode(), "refresh");
			else
				treeObj.reAsyncChildNodes(window.parent.currentNode, "refresh");				
			BIONE.closeDialog("rptDataInputEditCatalog");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	function f_close() {
		BIONE.closeDialog("rptDataInputEditCatalog");
	}
</script>

<title>指标目录管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/input/idxdatainput/editIdxInputCatalog.json">
	</form>
</div>
</body>
</html>