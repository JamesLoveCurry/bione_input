<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
	var treeObj;
	var currentNode;
	var nodes;
	var treeUrl = "${ctx}/frs/xmlMessage/getXmlTree";
	
	$(function(){
		//初始化
		initTree();
		
		//按钮
		var btn = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("showXmlWin");
			}
		}];
		
		BIONE.addFormButtons(btn);
		jQuery.metadata.setType("attr","validate");
		BIONE.validate($("#mainform"));
	});
	
	function initTree(){
		$("#treeContainer").height($("#center").height()-2);
		var setting ={
			async:{
				enable:true,
				url: treeUrl,
				autoParam:["id"],
				dataType:"json",
				dataFilter:function(treeId,parentNode,childNodes){
					return childNodes;
				}
			},
			data:{
				key:{
					name:"text"
				}
			},
			check : {
				autoCheckTrigger: true,
					enable : true,
				chkStyle : "checkbox",
				chkboxType : {
					"Y" : "s",
					"N" : "s"
				}
			},
			view:{
				selectedMulti:false
			},
			callback:{
				onClick : zTreeOnClick,
				onCheck : zTreeOnCheck,
				onAsyncSuccess : getChildrenNode					
			}
		};
		treeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	function zTreeOnClick(event, treeId, treeNode) {
		currentNode = treeNode;
	}
	
	//点击复选框，弹出children的树结构
	function zTreeOnCheck(event, treeId, treeNode){
		if(treeNode.getCheckStatus().checked == true){
			treeObj.reAsyncChildNodes(treeNode, "refresh");
		}
	}
	
	function getChildrenNode(event, treeId, treeNode, msg){
		if(treeNode!=null && treeNode.getCheckStatus().checked){
			for ( var i in treeNode.children) {
				treeObj.checkNode(treeNode.children[i], true, false);
				treeObj.reAsyncChildNodes(treeNode.children[i],"refresh");
			}
		}
	}	
	
</script>
</head>
<body>
<div id="template.center">
			<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree" style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>