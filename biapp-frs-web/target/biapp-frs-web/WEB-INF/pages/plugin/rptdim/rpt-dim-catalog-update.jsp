<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var upNo='${upCatalogId}';
	var catalogNm='${catalogNm}';
	var remark='${remark}';
	var upName='${upName}';
	var catalogId='${catalogId}';
    var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
			mainform = $("#mainform").ligerForm({
			align : "center",
			fields : [ {
				name : "catalogId",
				type : "hidden"
			}, {
				group : "目录信息",
				groupicon : groupicon,
				display : "上级目录 ",
				name : "upCatalogName",
				newline : false,
				type : "text"
			}, {
				display : "目录名称",
				name : "catalogNm",
				newline : false,
				type : "text",
				validate : {
					 required : true,
					    maxlength : 100,
		        		remote :  {
		        			url : "${ctx}/rpt/frame/dimCatalog/testSameDimCatalogNm",
							type : "POST",
							data : {
								"upCatalogId" :upNo,
								"catalogId":catalogId
							}
		        		},
						messages : {
							remote:"相同路径下维度目录已存在"
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
			$("#mainform [name='catalogId']").val(catalogId);
			$("#mainform [name='catalogNm']").val(catalogNm);
			$("#mainform [name='upCatalogName']").val(upName);
			$("#mainform [name='remark']").val(remark);
			$("#mainform [name='upCatalogName']").attr("disabled", "disabled");
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
		var treeObj = window.parent.leftTreeObj;
		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功");
		    window.parent.searchHandler();		
			BIONE.closeDialog("rptDimCatalogUpdateWin");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	function f_close() {
		BIONE.closeDialog("rptDimCatalogUpdateWin");
	}
</script>

<title>维度目录管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/frame/dimCatalog/updateCatalog">
	</form>
</div>
</body>
</html>