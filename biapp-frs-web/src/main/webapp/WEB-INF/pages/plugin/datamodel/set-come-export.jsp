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
	var rptIds;
	
	$(function(){
		$("#treeContainer").height($("#center").height()-2);
		initTree();
		refreshTree();
		
		//按钮
		var btn = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("exportReports");
			}
		},{
			text:'导出',
			onclick:f_save
			}
		];
		
		BIONE.addFormButtons(btn);
		jQuery.metadata.setType("attr","validate");
		BIONE.validate($("#mainform"));
		
		function loadTree(url,component, data){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/frame/dataset/getDataModelTree",
				dataType : 'json',
				type : "post",
				data : data,
				success : function(result){
					if(result != null
							&& typeof result != "undefined"){
						// 移除旧节点
						leftTreeObj.removeChildNodes(leftTreeObj.getNodeByParam("id", '0', null));
						leftTreeObj.removeNode(leftTreeObj.getNodeByParam("id", '0', null),false);
						// 渲染新节点
						leftTreeObj.addNodes(leftTreeObj.getNodeByParam("id", '0', null) , result , true);
						leftTreeObj.expandAll(true);
					}
				},
				error:function(){
					//BIONE.tip("加载失败，请联系系统管理员");
				}
			});
		}
		
		//树
		function initTree(){
			var setting = {
				data:{
					key:{
						name:"text"
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
				check : {
					enable : true,
					chkStyle : "checkbox",
					chkboxType : {
						"Y" : "s",
						"N" : "s"
					}
				},
				callback : {
					onClick : zTreeOnClick
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		}
		
		function zTreeOnClick(event, treeId, treeNode) {
			currentNode = treeNode;
		}
		
		function refreshTree() {  //刷新树
			if (leftTreeObj) {
				loadTree();
			}
		}
		
		function f_save(){
			nodes = leftTreeObj.getCheckedNodes(true);
			if(nodes == null || nodes == ""){
				BIONE.tip("请选择报表！");
			}else{
				setIds="";
				for(var i in nodes){
					if(nodes[i].params.type == "setInfo"){
						setIds += nodes[i].id + ",";
					}
				}
				setIds = setIds.substring(0,setIds.length-1);
				$.ajax({
					async:true,
					type:"POST",
					dataType:"json",
					url:"${ctx}/rpt/frame/dataset/exportTmp?setIds="+setIds,
					beforeSend : function(a, b, c) {
						BIONE.showLoading('正在导出数据中...');
					},
					success:function(data){
						BIONE.hideLoading();
						window.parent.exportTmp(data.filepath);
						BIONE.closeDialog("exportReports");
						
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