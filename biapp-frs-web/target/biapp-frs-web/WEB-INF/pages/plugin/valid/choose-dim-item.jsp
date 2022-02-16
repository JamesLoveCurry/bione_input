<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template12.jsp">
<script type="text/javascript">
	var treeObj;
	$(function() {
		beforeTree();
		initTree();
		selectButton();
	});
	function templateShow(){
		var height = $(document).height();
		$("#center").height(height);
		$("#content").height(height);
		$("#treeContainer").height(height);
	}
	function beforeTree() {
		var setting = {
				data:{
					key:{
						name:'text'
					},
					simpleData:{
						enable:true,
						idKey: "id",
						pIdKey: "upId"
					}
				},
				callback : {
					beforeClick : beforeClick,
					onClick : onClick
				},	
				view : {
					selectedMulti : false,
					showLine : false,
					expandSpeed :"fast"
				} ,
				check:{
					chkStyle:"checkbox",
					enable:true,
					radioType:"all" 
				}
		};
		treeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	function initTree() {
		$.ajax({
			cache : false,
			async : true,//同步
			url : '${ctx}/report/frame/valid/logic/dimItems?dimTypeNo=${dimTypeNo}',
			type : "get",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				treeObj.addNodes(null, result, true);
				treeObj.expandAll(true);
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function   showBack(){
		if(currVal){
			if(treeObj.getNodeByParam("id", currVal, null)){
				treeObj.checkNode(colTypeSelectTreeObj.getNodeByParam("id", currVal, null), true, true);
			}
		}
	}
	function selectButton() {
		var buttons = [];
		buttons.push({
			text : '选择',
			onclick : selected
		});
		BIONE.addFormButtons(buttons);
	}
	
	function beforeClick(treeId, treeNode, clickFlag) {
			return false;
	}
	
	function onClick(event, treeId, treeNode, clickFlag) {
		treeNode_ = treeNode;
	}
	     
 	function selected() {
 		var currentNode = treeObj.getCheckedNodes(true);
 		if(currentNode.length == 0){
 			BIONE.tip("请选择维度过滤");
 			return;
 		}
 		var itemNames = "";
 		for(var i in currentNode){
 			itemNames += "" + currentNode[i].id + ",";
 		}
 		if(itemNames.endsWith(",")){
 			itemNames = itemNames.substr(0, itemNames.length - 1);
 		}
 		window.parent.rowdata.dimFilter = itemNames;
		//window.parent.grid.endEdit();
		window.parent.grid.endEdit();
		window.parent.grid.updateCell('dimFilter',itemNames,window.parent.rowdata);
		
		BIONE.closeDialog("chooseDimItems");
	}
</script>
</head>
<body>
</body>
</html>