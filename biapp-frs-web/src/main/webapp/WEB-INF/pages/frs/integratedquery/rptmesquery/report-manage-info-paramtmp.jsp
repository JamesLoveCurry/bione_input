<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var currentNode;//当前点击节点
	var leftTreeObj;
	$(function(){
		initTree();
		initBtn();
	});
	function initTree(){
		var setting = {
				data:{
					key:{
						name:'text'
					},
					simpleData:{
						enable:true,
						idKey: "id",
						pIdKey: "upId"
					}
				},
				callback: {
					onClick: zTreeOnClick
				}
			};
		leftTreeObj =$.fn.zTree.init($("#tree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/param/templates/getTreeForReport.json",leftTreeObj);
	}
		function loadTree(url,component,data){
			$.ajax({
				cache : false,
				async : true,
				url : url,
				dataType : 'json',
				data:data,
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					
					
					var nodes = component.getNodes();
					var num = nodes.length;
					for(var i=0;i<num;i++){
						component.removeNode(nodes[0],false);
					}
					
					if(result.length>0){
						component.addNodes(null,result,false);
						component.expandAll(true);	
					}
				},
				error : function(result, b) {
					//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	//初始化数
	/* function initTree() {
		window['paramtmpTree'] = $.fn.zTree.init($("#tree"),{
			async : {
				enable : true,
				url : "${ctx}/rpt/frame/rptmgr/info/paramtmpTreeList.json?t="
						+ new Date().getTime(),
				autoParam : [ "realId" ],
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							childNodes[i].realId = childNodes[i].params.realId;
							childNodes[i].nodeType = childNodes[i].params.nodeType;
							childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true
									: false;
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
						paramtmpTree.reAsyncChildNodes(treeNode, "refresh");
					}
				}
			}
		});
	}
	 */
	//树点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
		}
	}
	
	//初始化按钮
	function initBtn(){
		btns.push(
			{
				text : "取消",
				onclick : function(){
					BIONE.closeDialog("paramtmpTreeWin");
				}
			},{
				text : "选择",
				onclick : function() {
					if(typeof currentNode == 'undefined'){
						BIONE.tip("请选择模版节点。");
						return;
					}
					if(currentNode.params.type != "paramtmp"){
						BIONE.tip("请选择模版节点。");
						return;
					}
					if(currentNode) {
		 				main = window.parent.selectedTab;
		 				var c = main.$.ligerui.get("paramtmpIdBox");
		 				if(!c){
		 					main.$("#paramtmpIdBox_val").val(currentNode.id);
		 					main.$("#paramtmpIdBox").val(currentNode.text);
		 				}else{
		 					c._changeValue(currentNode.id, currentNode.text);
		 				}
						BIONE.closeDialog("paramtmpTreeWin");
					}else{
						BIONE.tip("请选择一个节点");
					}
				}
			 },{
				text : "预览",
				onclick : function() {
					if (currentNode) {
						if(currentNode.params.type != "paramtmp"){
							BIONE.tip("请选择模版节点。");
								return;
						}
						if(currentNode.id) {
							var frameName = '${frameName}';
							var main;
							if("" == frameName || null == frameName){
			 					main = window.parent.parent;//批量导入统一配置
			 				}else{
			 					main = window.parent;
			 				}
							$.ajax({
								cache : false,
								async : true,
								url : "${ctx}/report/frame/param/templates/getEnity/" + currentNode.id,
								dataType : 'json',
								success : function(data){
									if(data.templateType=="custom")									
										dialog = main.BIONE.commonOpenLargeDialog('预览参数模版', 'preview',
											'${ctx}/report/frame/param/templates/view/'
													+ currentNode.id);
									else
										dialog = main.BIONE.commonOpenLargeDialog('预览参数模版', 'preview',
												data.templateUrl);
								}
							});
						}else{
							BIONE.tip("请选择一个节点");
						}
					}
				 }
			 }
		);
		BIONE.addFormButtons(btns);
	}
</script>
</head>
<body>
	<div id="template.center">
			<div class="content" style="border: 1px solid #C6C6C6; height: 98%">
				<div id="treeContainer"
					style="width: 100%; height: 100%; overflow: auto; clear: both;">
					<ul id="tree" class="ztree"></ul>
				</div>
			</div>
		</div>
</body>
</html>