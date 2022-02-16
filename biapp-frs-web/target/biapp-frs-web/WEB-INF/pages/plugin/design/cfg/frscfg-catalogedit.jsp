<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var defSrc = "${defSrc}";
	var viewFlag = "${viewFlag}";
	var moduleType = "${moduleType}";
	var tree_root_id = "0"
	//创建表单结构 
	var mainform;
	
	jQuery.validator.addMethod("catalogNm", function(value, element) {
	    var numReg = /^[0-9a-zA-Z\u2160-\u216B\u4e00-\u9fa5\uFF08\uFF09\u002E\u0028\u0029\uFF1A_-]*$/;//数字、字母、罗马数字、中文、中文括号、下划线、横线、冒号
	    return this.optional(element) || (numReg.test(value));
	}, " 目录命名不合法");
	
	$(function() {
		$("#viewFlag").attr("value", viewFlag);
		$("#moduleType").attr("value", moduleType);
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ {
				name : "defSrc",
				type : "hidden"
			},{
				name : "extType",
				type : "hidden"
			}, {
				display : "目录名称",
				name : "catalogNm",
				newline : true,
				type : "text",
				group : "目录信息",
				groupicon : groupicon,
				validate : {
					required : true,
					catalogNm : true,
					maxlength : 100
				}
			}, {
				display : "上级目录",
				name : "upCatalogNm",
				newline : false,
				type : "text"
			}, {
				display : "排列顺序",
				name : "catalogOrder",
				newline : true,
			    type : 'number',
			    newline: true,
			    validate : {
			    	required : true,
			    	digits : false,
			    	maxlength : 5
			    }
			}, {
				name : "catalogId",
				type : "hidden"
			},{
				name:"upCatalogId",
				type : "hidden"
			},{
				display : "描述",
				width : 350,
				name : "remark",
				newline : true,
				type : "textarea",
				attr : {
					style : "resize: none;"
				},
				validate : {
					maxlength : 500
				}
			} ]
		});
		var selNodes = window.parent.treeObj.getSelectedNodes();
		$("#defSrc").val(defSrc ? defSrc : "");
		if("${id}") {
			BIONE.loadForm($("#mainform"), {
				url : "${ctx}/report/frame/design/cfg/getCatalogInfoById",
				type:"post",
				data:{
					catalogId : "${id}"
				}
			});
			// 修改时，获取上级目录信息
			if(selNodes != null 
					&& typeof selNodes != "undefined"
					&& typeof selNodes.length != "undefined" 
					&& selNodes.length > 0
					&& window.parent.folderNodeType == selNodes[0].params.nodeType
					&& selNodes[0].getParentNode() != null
					&& window.parent.folderNodeType == selNodes[0].getParentNode().params.nodeType ){
				$("#mainform input[name=upCatalogNm]").val(selNodes[0].getParentNode().text);
				$("#mainform input[name=upCatalogId]").val(selNodes[0].getParentNode().params.realId);
			}else{
				// 新增根节点目录
				$("#mainform input[name=upCatalogNm]").val("无");
				$("#mainform input[name=upCatalogId]").val(tree_root_id);
			}
		} else {
			// 新增时 获取上级目录相应信息
			if(selNodes != null 
					&& typeof selNodes != "undefined"
					&& typeof selNodes.length != "undefined" 
					&& selNodes.length > 0
					&& window.parent.folderNodeType == selNodes[0].params.nodeType){
				$("#mainform input[name=upCatalogNm]").val(selNodes[0].text);
				$("#mainform input[name=upCatalogId]").val(selNodes[0].params.realId);
			}else{
				// 新增根节点目录
				$("#mainform input[name=upCatalogNm]").val("无");
				$("#mainform input[name=upCatalogId]").val(tree_root_id);
			}			
		}
		$("#mainform input[name=upCatalogNm]").attr("readonly", "true").css("color", "black").removeAttr("validate");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		if("${id}") {
			$("#catalogNm").rules("add",{
				remote:{
					url:"${ctx}/report/frame/design/cfg/checkCatalogNm",
					type : "post",
					data : {
						catalogId : "${id}",
						upCatalogId: $("#upCatalogId").val(),
						extType : '${moduleType}',
						defSrc : defSrc
					}
				},
				messages:{
					remote:"该报表目录已存在"
				}
			});
		} else {
			$("#catalogNm").rules("add",{
				remote:{
					url:"${ctx}/report/frame/design/cfg/checkCatalogNm",
					type : "post",
					data : {
						upCatalogId: $("#upCatalogId").val(),
						extType : '${moduleType}',
						defSrc : defSrc
					}
				},
				messages:{
					remote:"该报表目录已存在"
				}
			});
		}
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleHandler
		});
		buttons.push({
			text : '保存',
			onclick : saveHandler
		});
		BIONE.addFormButtons(buttons);

	});
	function saveHandler() {
		BIONE.submitForm($("#mainform"), function() {
			window.parent.searchHandler();
			BIONE.closeDialog("catalogEdit", "保存成功");
		}, function() {
			BIONE.closeDialog("catalogEdit", "保存失败");
		});
	}
	function cancleHandler() {
		BIONE.closeDialog("catalogEdit");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/design/cfg/saveCatalog"  method="post">
		 	<input type="hidden" id="viewFlag" name="viewFlag">
		 	<input type="hidden" id="moduleType" name="moduleType">
		</form>
	</div>
</body>
</html>