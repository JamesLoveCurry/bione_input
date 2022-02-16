<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var upNo='${catalogId}';
	var upName='${upName}';
    var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
			mainform = $("#mainform").ligerForm({
			align : "center",
			fields : [ {
		    	name:'upCatalogId',
		    	type:'hidden'
		    }, {
		    	group : "目录信息",
				groupicon : groupicon,
				display : "上级目录 ",
				name : "upCatalogName",
				newline : false,
				type : "text"
			},{
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
								"upCatalogId" :upNo
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
			$("#mainform [name='upCatalogId']").val(upNo);
			$("#mainform [name='upCatalogName']").val(upName);
			$("#mainform [name='upCatalogName']").attr("disabled", "disabled");
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
			window.parent.searchHandler();					
			BIONE.closeDialog("rptDimCatalogAddWin");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	function f_close() {
		BIONE.closeDialog("rptDimCatalogAddWin");
	}
</script>

<title>维度目录管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/frame/dimCatalog">
	</form>
</div>
</body>
</html>