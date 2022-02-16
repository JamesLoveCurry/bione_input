<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	var tempGrid = null;
	
	$(function() {

		$("#treeContainer").height($("#center").height()-2);
		
		
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("addTreeNode");
			}
		}, {
			text : "保存",
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
		//保存机构树
		function f_save(){
			if(currentNode){
			window.parent.selectTab.liger.get("mgrOrgNm").selectValue(currentNode.params.orgNo);
			window.parent.selectTab.liger.get("mgrOrgNm").setText(currentNode.text);
			
			BIONE.closeDialog("addTreeNode");
			}else{
				BIONE.tip("请选择机构！");
			}
		}
		
		initTree();
	//	initTab();
		//渲染树
		function initTree() {
			//树
			var setting = {
				async : {
					enable : true,
					url : "${ctx}/rpt/frs/monitororgtree/getTree.json?orgType="+"${orgType}"+"&t="+ new Date().getTime(),
					autoParam : ["${ctx}/rpt/frs/monitororgtree/","upOrgNo","orgNo","orgNm"],
					dataType : "json",
					type : "post",
					dataFilter : function(treeId, parentNode, childNodes) {
						if (childNodes) {
							for ( var i = 0; i < childNodes.length; i++) {
								childNodes[i].orgType = childNodes[i].params.orgType;
								childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
								childNodes[i].orgNm = childNodes[i].params.orgNm;
								childNodes[i].orgNo = childNodes[i].params.orgNo;
							}
						}
						return childNodes;
					}
				},
				data : {
					key : {
						name : "text"
					}
				},
				view : {
					selectedMulti : false
				},
				callback : {
					onClick : zTreeOnClick,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.id == "ROOT") {
							//若是根节点，展开下一级节点
							leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
						}
					}
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			$("#template.left.center").hide();
			
			//树的点击事件
			function zTreeOnClick(event, treeId, treeNode) {
				currentNode = treeNode;
			}
		}
		});
	</script>
</head>
	<body>
	<div id="template.center">
			<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>