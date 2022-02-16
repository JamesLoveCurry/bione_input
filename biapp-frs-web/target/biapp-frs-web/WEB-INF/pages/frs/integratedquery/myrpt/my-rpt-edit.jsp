<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var tree_root_id = "0"
	var id = '${#id}';
	var userid = "${#userid}";
	var upid = "${#upid}"
	//创建表单结构 
	var mainform;
	$(function(){
		initTab();//新页面的跳转
		initNode();//查询节点中数据
		initButton();//新界面中的按钮
		
		//BIONE.setFormDefaultBtn(cancleHandler,saveHandler);     亲测可用
	});
	function initTab(){
		$("#mainform").ligerForm({
			fields : [{
				name : "folderId",
				type : "hidden"
			},{
				name : "upFolderId",
				type : "hidden"
			},{
				name : "userId",
				type : "hidden"
			},{
				display : "目录名称",
				name : "folderNm",
				newline : true,
				type : "text",
				group : "目录信息",
				validate : {
					required : true,
					maxlength : 100
				}
			}]
		});
	} 
	function initNode(){
		var selNodes = window.parent.treeObj.getSelectedNodes();
		if(selNodes != null && selNodes.length > 0){
			$("#mainform input[name=folderId]").val(selNodes[0].id);
			$("#mainform input[name=folderNm]").val(selNodes[0].text);
		}else{
			// 新增根节点目录
			$("#mainform input[name=folderId]").val("");
			$("#mainform input[name=upFolderId]").val(upid);
			$("#mainform input[name=userId]").val(userid);
		}
	}
	function initButton(){
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
	} 
	function saveHandler() {
		BIONE.submitForm($("#mainform"), function() {
			window.parent.searchHandler();
			BIONE.closeDialog("favEdit", "保存成功");
		}, function() {
			BIONE.closeDialog("favEdit", "保存失败");
		});
	}
	function cancleHandler() {
		BIONE.closeDialog("favEdit");
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frs/integratedquery/myrpt/collect/saveCata"  method="post"></form>
	</div>
</body>
</html>