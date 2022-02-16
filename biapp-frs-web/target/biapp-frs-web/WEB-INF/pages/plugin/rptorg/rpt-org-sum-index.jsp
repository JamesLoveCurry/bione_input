<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22_BS.jsp">
<head>
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	var tempGrid = null;
	var setting = "";
	var setteng = "";
	var state = "1";
	
	function refreshTree() {
		if (leftTreeObj) {
				leftTreeObj.reAsyncChildNodes(null, "refresh");
				$("#mainformdiv").html("");
		}
	}
	
	//初始化
	$(function(){
		//设置高度
		// var $centerDom = $(document);
        var $centerDom = $(window.parent.document);
		gridCenter = $centerDom.height() - 60;
		initTree();
		setTree(true);
		
		//点击查询事件
		$("#treeSearchIcon").bind("click",function(e){
			setTree($("#treeSearchInput").val() == "");
		});
		 //回车查询事件
		$("#treeSearchInput").bind("keydown",function(e){
			if(e.keyCode == 13){
				setTree($("#treeSearchInput").val() == "");
			}
		}); 
		
		function setTree(falg){
			if(falg){
				leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			}else{
				leftTreeObj = $.fn.zTree.init($("#tree"), setteng);
				loadTree("${ctx}/frs/systemmanage/orgmanage/searchOrgTree",leftTreeObj,{orgNm:$("#treeSearchInput").val()});
			}
		}
		
		//树点击事件
		function zTreeOnClick(event, treeId, treeNode){
			if(!treeNode.params.upOrgNo){
				currentNode = treeNode;
				return;
				$("#mainformdiv").html("");
			}
			if (currentNode != treeNode) {
				currentNode = treeNode;
				if (!tempGrid) {
					content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/rpt/frame/rptorgsum/getNewTree?orgNo="
							+ treeNode.orgNo
							+ "&orgType="
							+ treeNode.orgType
							+ "'></iframe>";
					$("#mainformdiv").html(content);
				} else {
					tempGrid.set('parms', {
						orgNo : treeNode.orgNo,
						orgType : treeNode.orgType,
						d : new Date().getTime()
					});
					if (catalogBox) {
						catalogBox.val(treeNode.orgNo);
					}
					tempGrid.loadData();
				}

			}
		}
		
		//异步树
		function initTree(){
			setting ={
					async:{
						enable:true,
						type:'get',
						url:"${ctx}/frs/systemmanage/orgmanage/getTree?state="+state+"&t="+ new Date().getTime(),
						autoParam:["upOrgNo","orgNo","orgNm","orgType"],
						dataType:"json",
						dataFilter:function(treeId,parentNode,childNodes){
							if(childNodes){
								for(var i = 0;i<childNodes.length;i++){
									childNodes[i].orgType = childNodes[i].params.orgType;
									childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
									childNodes[i].orgNm = childNodes[i].params.orgNm;
									childNodes[i].orgNo = childNodes[i].params.orgNo;
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
						onNodeCreated:function(event, treeId, treeNode){
							if(treeNode.id == "ROOT"){
								leftTreeObj.reAsyncChildNodes(treeNode,"refresh");
							}
						}
					}
			};
			
			setteng={
					data:{
						key:{
							name:"text"
						},
						simpleData:{
							enable:true,
							idKey: "id"
						}
					},
					view:{
						selectedMulti : false
					},callback:{
						onClick:zTreeOnClick,
						onNodeCreated : function(event, treeId, treeNode) {
							if (treeNode.id == "ROOT") {
								//若是根节点，展开下一级节点
								leftTreeOb.expand(treeNode,true,true,true);
							}
						}
					}
			};
			
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			$("#template.left.center").hide();
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
						for(var i = 0;i<result.length;i++){
							result[i].orgType = result[i].params.orgType;
							result[i].upOrgNo = result[i].params.upOrgNo;
							result[i].orgNm = result[i].params.orgNm;
							result[i].orgNo = result[i].params.orgNo;
						//	childNodes[i].searchNm=$("#treeSearchInput").val();
						}
						component.addNodes(null, result, false);
						component.expandAll(true);
					}
				},
				error : function(result, b) {
					//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	});
	
	
	</script>
<title>汇总信息维护</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">机构信息</span>
	</div>
	<div id="template.right">
		<div id="mainformdiv" style="overflow: auto; font-size: 12px;height:100%;">
		</div>
	</div>
</body>
</html>