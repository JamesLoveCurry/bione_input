<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var ROOT_NO = '0';
	//授权资源根节点图标
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";
	var leftTreeObj;
	$(function() {
		initTree();
		initBtn();
	});

	//树
	function initTree(){
		var setting ={
				async:{
					enable:true,
					url:"${ctx}/rpt/input/idxdatainput/getAsyncTreeWithIdxType.json?indexType=01",//07
					autoParam:[ "id","childCatalogs"],
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								childNodes[i].childCatalogs = childNodes[i].params.childCatalogs;
							}
						}
						return childNodes;
					}
				},
				data:{
					key:{
						name:"text"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : zTreeOnClick
				}
				
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
					
	}
	function zTreeOnClick(event, treeId, treeNode) {
		currentNode = treeNode;
	}
	
	function initBtn(){
		var buttons = [{
		    text : '取消',
		    onclick : function() {
			BIONE.closeDialog("selectIdx");
		    }
		},{
		    text : '选择',
		    onclick : f_select
		}];
		BIONE.addFormButtons(buttons);
	}
	
	function f_select(){
		var selectedNodes = leftTreeObj.getSelectedNodes();
		if(selectedNodes == null ||typeof selectedNodes=="undefined" || selectedNodes.length==0)
		{
			BIONE.tip("请选择指标");
			return;
		}
		window.parent.appendOneIdx(selectedNodes[0]);
		BIONE.closeDialog("selectIdx");
	}
	
</script>

<title>指标目录管理</title>
</head>
<body style="width: 80%">
	<div id="template.center">
		<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
	</div>
</body>
</html>