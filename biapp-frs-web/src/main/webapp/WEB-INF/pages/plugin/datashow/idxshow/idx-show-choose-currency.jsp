<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var flag = true;
	var currentNode;//当前点击节点
	$(function(){
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
				selectedMulti : true
			},
			check : {
				chkboxType : {"Y":"", "N":""},
				chkStyle : 'checkbox',
				enable : true
			},
			dataFilter : function(treeId, parentNode, childNodes) {
				return childNodes;
			},
			callback : {
				//onClick : zTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.id == "ROOT") {
						//若是根节点，展开下一级节点
						leftTreeObj.expandNode(treeNode, true);
					}
				},
				onCheck: function(event, treeId, treeNode){
					if(treeNode.id == "0"){
						leftTreeObj.checkAllNodes(flag);
						flag = !flag;
					}
				}
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		loadTree("${ctx}/report/frame/datashow/idx/getCurrencyTree", leftTreeObj);
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
				BIONE.closeDialog("chooseCurrency");
			}
		}, {
			text : "选择",
			onclick : function() {
					main = window.parent;
					var c = main.$.ligerui.get("currency");
					var checkNodes = leftTreeObj.getCheckedNodes();
					var texts = [];
					var ids = [];
					for(var tmp in checkNodes){
						texts.push(checkNodes[tmp].text);
						ids.push(checkNodes[tmp].id);
					}
					c._changeValue(ids.join(","), texts.join(","));
					BIONE.closeDialog("chooseCurrency");
			}
		});
		BIONE.addFormButtons(btns);
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