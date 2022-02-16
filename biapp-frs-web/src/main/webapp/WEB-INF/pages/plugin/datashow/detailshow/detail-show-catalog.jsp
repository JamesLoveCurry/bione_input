<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style>
#tree {
	background-color: #F1F1F1;
}
</style>
<script type="text/javascript">
	//创建表单结构 
	var treeObj = null;
	var currentNode = null;
	$(function() {
		initTree();
		initButtons();
	});
	
	
	function initTree(){
		var centerHeight = $("#center").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(centerHeight - 10);
		var async = {
			data : {
				keep : {
					parent : true
				},
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			view : {
				selectedMulti : false,
				showLine : false
			},
			callback : {
				onClick : function(event,target,treeNode){
					currentNode = treeNode;
				}
			}
		};
		treeObj = $.fn.zTree.init($("#tree"), async, []);
		BIONE.loadTree("${ctx}/report/frame/datashow/detail/getCatalogTree",treeObj);
	}
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleHandler
		});
		buttons.push({
			text : '选择',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
	
	function f_save() {
		if(currentNode == null || currentNode.params.nodeType != "02"){
			BIONE.tip("请选择目录");
			return;
		}
		window.parent.selectedTab.$.ligerui.get("catalogId")._changeValue(currentNode.id,currentNode.text);
		BIONE.closeDialog("catalogEdit");
	}
	
	function cancleHandler() {
		BIONE.closeDialog("catalogEdit");
	}
	
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="treeToolbar"
			style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
		<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>