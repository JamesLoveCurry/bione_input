<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var indexCatalogNo='${indexCatalogNo}';
	var upIndexCatalogNm='${upIndexCatalogNm}';
	var defSrc='${defSrc}';
	var catalogOrder='${catalogOrder}';
    var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
			mainform = $("#mainform").ligerForm({
			align : "center",
			fields : [ {
		    	name:'upNo',
		    	type:'hidden'
		    }, {
		    	name:'defSrc',
		    	type:'hidden'
		    }, {
		    	group : "目录信息",
				groupicon : groupicon,
				display : "上级目录 ",
				name : "upIndexCatalogNm",
				newline : false,
				type : "text"
			},{
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
							"upNo" :indexCatalogNo
						}
	        		},
					messages : {
						required : 'This field is required',
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
			if(defSrc){
     			$("#mainform [name='defSrc']").val(defSrc);
			}
			$("#mainform [name='upNo']").val(indexCatalogNo);
			$("#mainform [name='upIndexCatalogNm']").val(upIndexCatalogNm);
			$("#mainform [name='upIndexCatalogNm']").attr("disabled", "disabled");
			$("#mainform [name='catalogOrder']").val(catalogOrder);
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
		var treeObj = window.parent.currentTree;
		if(!treeObj){
			treeObj = window.parent.leftTreeObj;
		}
		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功");
			treeObj.reAsyncChildNodes(window.parent.currentNode, "refresh");				
			BIONE.closeDialog("rptIdxCatalogAddWin");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	function f_close() {
		BIONE.closeDialog("rptIdxCatalogAddWin");
	}
</script>

<title>指标目录管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/report/frame/idx">
	</form>
</div>
</body>
</html>