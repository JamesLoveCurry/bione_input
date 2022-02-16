<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
	var leftTreeObj;
	var currentNode;
	var nodes;
	var idxIds;
	
	$(function(){
		$("#treeContainer").height($("#center").height()-2);
		initTree();
		refreshTree();
		
		//按钮
		var btn = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("exportIdxs");
			}
		},{
			text:'导出',
			onclick:f_save
			}
		];
		
		BIONE.addFormButtons(btn);
		jQuery.metadata.setType("attr","validate");
		BIONE.validate($("#mainform"));
		
		//树
		function initTree(){
			var setting ={
					async:{
						enable:true,
						url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&isAuth=1",
						autoParam:["nodeType", "id", "indexVerId"],
						dataType:"json",
						dataFilter:function(treeId,parentNode,childNodes){
							if(childNodes){
								for(var i = 0;i<childNodes.length;i++){
									childNodes[i].nodeType = childNodes[i].params.nodeType;
									childNodes[i].indexVerId = childNodes[i].params.indexVerId;
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
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
						
		}
		
		function zTreeOnClick(event, treeId, treeNode) {
			currentNode = treeNode;
		}
		
		//点击复选框，弹出children的树结构
		function zTreeOnCheck(event, treeId, treeNode){
			if(treeNode.getCheckStatus().checked == true){
				leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
			}
		}
		
		function getUpOrgNo(id) {
			for ( var i in upNos) {
				if (id == upNos[i]) {
					return true;
				}
			}
			return false;
		}
		
		function getChildrenNode(event, treeId, treeNode, msg){
			if(treeNode!=null){
					if (treeNode.getCheckStatus().checked) {
						for ( var i in treeNode.children) {
							leftTreeObj.checkNode(treeNode.children[i], true, false);
							leftTreeObj.reAsyncChildNodes(treeNode.children[i],
										"refresh");
					}
				}
			}
		}
		
		//迭代树
		function zTreeOnAsyncSuccess(event,treeId,treeNode){
			if(treeNode){
				if(treeNode.getCheckStatus().checked == true){
					var nodes = treeNode.children;
				}
			}
		}
		
		function refreshTree() {  //刷新树
			if (leftTreeObj) {
				initTree();
			}
		}
		
		function f_save(){
			nodes = leftTreeObj.getCheckedNodes(true);
			if(nodes == null || nodes == ""){
				BIONE.tip("请选择指标！");
			}else{
				idxIds="";
				for(var i in nodes){
					if(nodes[i].params.nodeType == "idxInfo"){
						idxIds += nodes[i].params.indexNo + ",";
					}
				}
				idxIds = idxIds.substring(0,idxIds.length-1);
				$.ajax({
					async:true,
					type:"POST",
					dataType:"json",
					url:"${ctx}/report/frame/idx/exportTmp",
					data:{"idxIds":idxIds},
					beforeSend : function(a, b, c) {
						BIONE.showLoading('正在导出数据中...');
					},
					success:function(data){
						BIONE.hideLoading();
						window.parent.exportTmp(data.filepath);
						BIONE.closeDialog("exportIdxs");
						
					},
					error : function(result) {
						BIONE.hideLoading();
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
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