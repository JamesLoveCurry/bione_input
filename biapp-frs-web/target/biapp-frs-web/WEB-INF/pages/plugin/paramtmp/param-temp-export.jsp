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
	var dimTypeNos;
	var setting;
	
	$(function(){
		initTree();
		refreshTree();
		initNodes();
		//按钮
		var btn = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("exportDim");
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
			setting ={
					data : {
						keep : {
							parent : true
						},
						key : {
							name : "text"
						},
						simpleData : {
							enable : true,
							idKey : "id",
							pIdKey : "upId",
							rootPId : null
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
					view : {
						selectedMulti : false,
						showLine : false
					},
					callback:{
						onClick : zTreeOnClick,
						onCheck : zTreeOnCheck
					}
					
			};
						leftTreeObj = $.fn.zTree.init($("#tree"), setting);
						
		}
		
	 function initNodes(){
			if(leftTreeObj)  leftTreeObj.destroy();
			leftTreeObj = $.fn.zTree.init($("#tree"),setting);
			if(leftTreeObj == null 
					|| typeof leftTreeObj == "undefined"){
				return ;
			}
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/param/templates/getAyncTree.json?d=" + new Date().getTime(),
				dataType : 'json',
				type : "post",
				success : function(result){
					if(result != null
							&& typeof result != "undefined"){
						// 渲染新节点
						leftTreeObj.addNodes(null, result, true);
//	 					leftTreeObj.expandAll(true);
					}
				},
				error:function(){
					BIONE.tip("加载失败，请联系系统管理员");
				}
			});
		}	
		
		function zTreeOnClick(event, treeId, treeNode) {
			currentNode = treeNode;
		}
		
		//点击复选框，弹出children的树结构 -- 同步树
		function zTreeOnCheck(event, treeId, treeNode){
			if(treeNode.checked == true){
				var node = leftTreeObj.getCheckedNodes(true);
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
				BIONE.tip("请选择参数模板！");
			}else{
				var dimTypeNos=[];
				for(var i in nodes){
					if(nodes[i].params.type == "paramtmp"){
						dimTypeNos.push(nodes[i].id);
					}
				}
				var dimTypeNoss = dimTypeNos.join(",");
				$.ajax({
					async:true,
					type:"POST",
					dataType:"json",
					url:"${ctx}/report/frame/param/templates/exportTmp",
					data:{"paramIds":dimTypeNoss},
					beforeSend : function(a, b, c) {
						BIONE.showLoading('正在导出数据中...');
					},
					success:function(data){
						BIONE.hideLoading();
						window.parent.exportTmp(data.filepath);
						BIONE.closeDialog("exportDim");
						
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