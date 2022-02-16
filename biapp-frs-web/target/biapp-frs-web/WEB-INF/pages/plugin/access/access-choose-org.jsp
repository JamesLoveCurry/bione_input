<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var currentNode;//当前点击节点
	
	$(function(){
		initTree();
		initBtn();
	});
	
	//初始化数
	function initTree() {
		
	var setting = {
			async:{
				enable:true,
				url:"${ctx}/rpt/frame/access/org/getOrgTree.json?t="+ new Date().getTime(),
				autoParam:["id"],
				dataType:"json",
				dataFilter:function(treeId,parentNode,childNodes){
					if(childNodes){
					}
					return childNodes;
				}
			},
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
				BIONE.closeDialog("chooseOrg");
			}
		}, {
			text : "选择",
			onclick : function() {
					main = window.parent;
					var c= main.selectTab?main.selectTab.$.ligerui.get("mgrOrgNm"):main.$.ligerui.get("orgNm");
					c._changeValue(currentNode.id, currentNode.text);
					BIONE.closeDialog("chooseOrg");
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