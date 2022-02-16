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
				url:"${ctx}/rpt/frame/rptorg/getTree.json?orgType=${orgType}&t="+ new Date().getTime(),
				autoParam:["${ctx}/rpt/frame/rptorg","upOrgNo","orgNo","orgNm","mgrOrgNo"],
				dataType:"json",
				dataFilter:function(treeId,parentNode,childNodes){
					if(childNodes){
						for(var i = 0;i<childNodes.length;i++){
							childNodes[i].orgType = childNodes[i].params.orgType;
							childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
							childNodes[i].orgNm = childNodes[i].params.orgNm;
							childNodes[i].orgNo = childNodes[i].params.orgNo;
							childNodes[i].mgrOrgNo = childNodes[i].params.mgrOrgNo;
							//	childNodes[i].searchNm=$("#treeSearchInput").val();
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
				onClick:zTreeOnClick,
				beforeClick : function(treeId, treeNode, clickFlag){
					return true;
				},
				onNodeCreated:function(event, treeId, treeNode){
					if(treeNode.id == "ROOT"){
						leftTreeObj.reAsyncChildNodes(treeNode,"refresh");
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
				if(currentNode != null){
					main = window.parent;
					var c= main.selectTab?main.selectTab.$.ligerui.get("upOrgNoCom"):main.$.ligerui.get("orgNm");
					if(currentNode.id == "0")
						currentNode.text = "无";
					c._changeValue(currentNode.id, currentNode.text);
					BIONE.closeDialog("chooseOrg");
				}else{
					BIONE.tip("请选择上级机构");
				}
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