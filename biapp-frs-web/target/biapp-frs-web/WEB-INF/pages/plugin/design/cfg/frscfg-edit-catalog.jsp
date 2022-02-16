<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var catalogId = window.parent.catalogId;
	var changeType = "${changeType}"; // 切换类型 1-首页入口的切换 else-功能入口的切换
	var rptId = window.parent.rptId;
	var folderTreeNodePre = "f";
	var currentNode;//当前点击节点
	$(function(){
		if(catalogId == null
				|| catalogId == ""){
			catalogId = window.parent.selectedTab.$("#catalogId").val();
		}
		initTree();
		initBtn();
	});
	
	//初始化数
	function initTree() {
		
	var setting = {
			data : {
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			view : {
				selectedMulti : false
			},
			dataFilter : function(treeId, parentNode, childNodes) {
				if (childNodes) {
					if(childNodes[i].params.isParent == "false" ){
						childNodes=null;
					}
					else{
						if(childNodes[i].params.nodeType == "02" ){
							childNodes.isParent="false";
						}
					}
				}
				return childNodes;
			},
			callback : {
				onClick : zTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.id == "ROOT") {
						//若是根节点，展开下一级节点
						leftTreeOb.expand(treeNode, true, true, true);
					}
				}
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		loadTree("${ctx}/report/frame/design/cfg/getRptCatalog", leftTreeObj);
	}
	//加载树中数据
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
			type : "POST",
			beforeSend : function() {
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = component.getNodes();
				var num = nodes.length;
				for ( var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					component.addNodes(null, result, false);
					component.expandAll(true);
					if(catalogId != null
							&& catalogId != ""
							&& typeof catalogId != "undefined"){
						var selNode = component.getNodeByParam("id", folderTreeNodePre+catalogId, null);
						if(selNode){
							component.selectNode(selNode , false);
						}
					}
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	//树点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
		}
	}

	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("catalogTreeWin");
			}
		}, {
			text : "选择",
			onclick : function() {
				if (typeof currentNode == 'undefined') {
					BIONE.tip("请选择目录节点");
					return;
				}
				if (currentNode.id!="0") {
					if(changeType
							&& changeType == "1"){
						beforeCloseFunc();
					}else{	
						var main = window.parent.selectedTab;
						main.$("#catalogId").val(currentNode.params.realId);
						main.$("#catalogNmBox").val(currentNode.text);
						main.catalogId = currentNode.params.realId;
						BIONE.closeDialog("catalogTreeWin");
					}
				} else {
					BIONE.tip("请选择一个目录节点");
				}
			}
		});
		BIONE.addFormButtons(btns);
	}
	
	function beforeCloseFunc() {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/changeCatalogHandler",
			data : {
				targetCatalogId : currentNode.params.realId,
				currentCatalogId : catalogId,
				rptId : rptId ? rptId : ""
			},
			dataType : 'json',
			type : "post",
			success : function(result){
				// 刷新树
				var topWindow = window.parent;
				topWindow.searchHandler();	
				// 刷新列表
				if(topWindow.document.getElementById("gridFrame").contentWindow.grid){			
					topWindow.document.getElementById("gridFrame").contentWindow.grid.reload();
				}
				BIONE.closeDialog("catalogTreeWin");
			},
			error : function(){
				BIONE.tip("维护失败，请联系管理员");
			}
		})
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; height: 98%">
			<div id="treeContainer"
				style="width: 100%; height: 100%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>