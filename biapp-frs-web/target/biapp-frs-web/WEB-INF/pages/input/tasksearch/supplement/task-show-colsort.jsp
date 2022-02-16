<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>

#left {
	float: left;
	width: 30%;
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
}
#right {

	width: 69%;
	float: right;

}
#tree {
	background-color: #F1F1F1;
}
</style>
<script type="text/javascript">
	var colInfos = window.parent.colInfos;
	var leftTreeObj = null;
	var grid = null;
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	
	function templateShow(){
		var $content = $(document);
		var height = $content.height() - 50;
		var centerHeight = $("#center").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(centerHeight - 38);
	}
	$(function() {
		templateShow();
		initTree();
		initButtons();
	});

	function initTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			edit:{
				enable : true,
				showRemoveBtn : false,
				showRenameBtn : false,
				drag : {
					isMove : true,
					inner : false
				}
			},
			callback : {
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		var nodes = [];
		for ( var i in colInfos) {
			var node = {
				id : i,
				text : colInfos[i].displayNm,
				icon : "${ctx}/images/classics/icons/column.png",
				data : colInfos[i],
				orderno : colInfos[i].orderno
			}
			nodes.push(node);
		}
		nodes = nodes.sort(  
                function(a, b)  
                {  
                    if(a.orderno < b.orderno) return -1;  
                    if(a.orderno > b.orderno) return 1;  
                    return 0;  
                }  
   		);  
		setTreeNode(nodes, leftTreeObj);
	}
	
	function setTreeNode(result, component) {
		var nodes = component.getNodes();
		var num = nodes.length;
		for (var i = 0; i < num; i++) {
			component.removeNode(nodes[0], false);
		}
		component.addNodes(null, result, false);
		component.expandAll(true);
	}
	
	
 	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function(){
				BIONE.closeDialog("colsortEdit");
			}
		});
		buttons.push({
			text : '确认',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
 	
 	function f_save(){
 		var ncolInfos = {};
 		var nodes = leftTreeObj.getNodes();
 		for(var i in nodes){
 			colInfos[nodes[i].id].orderno = i;
 			ncolInfos[nodes[i].id] = colInfos[nodes[i].id];
 		}
 		window.parent.colInfos = ncolInfos;
 		window.parent.setColumn();
 		BIONE.closeDialog("colsortEdit");
 	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
						<img src="${ctx}/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="90%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							数据列
						</span>
					</div>
				</div>
			</div>
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