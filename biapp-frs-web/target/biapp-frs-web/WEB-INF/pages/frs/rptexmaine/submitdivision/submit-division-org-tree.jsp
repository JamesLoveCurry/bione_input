<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style type="text/css">

.label-content {
	margin-left: 6px;
	display: block;
	float: left;
}
.label-item {
	height: 20px;
	float: left;
	line-height: 20px;
	border: 1px solid #D4D4D4;
	color: #4C4C4C;
	margin: 0 10px 5px 0;
	padding: 0 2px;
	background-color: #F0F0F0;
	border-radius: 2px;
	cursor: pointer;
}
.label-item:hover {
	border: 1px solid #C0C0C0;
	box-shadow: 0 1px 1px rgba(0, 0, 0, 0.15);
}
.label-item .text {
	float: left;
	height: 20px;
}
.label-item .icon {
	float: left;
	width: 10px;
	height: 10px;
	margin: 5px 0 5px 2px;
	cursor: pointer;
}
.label-item.select {
	background-color: #FDBB69;
	border-color: #FDBB69;
}
</style>
<script type="text/javascript">

    var orgType = '${orgType}'
	var orgTree;
	$(function(){
		var height = $(document).height();
		$("#center").height(height-42);
		$("#content").height(height-42);
		$("#treeContainer").height(height-95);
		$("#tree").height(height-105);

		initTree();
		
		var btns =[];
		btns.push(
				{ text : "取消", onclick : function(){BIONE.closeDialog("orgTreeWin");}},	
				{ text : "选择", onclick : function() {
						var nodes = orgTree.getSelectedNodes();
						if("ROOT" == nodes[0].id){
							BIONE.tip("根节点不可以选择！");
						}else{
							var c = window.parent.jQuery.ligerui.get("orgNmBox");
							c._changeValue(nodes[0].id, nodes[0].text);
							BIONE.closeDialog("orgTreeWin");
						}
					}
			 }
		);
		BIONE.addFormButtons(btns);
	});
	
	function initTree(){
		settingAsync = {  //异步
			async : {
				enable : true,
				url : "${ctx}/frs/rpttsk/publish/getOrgTree?orgType=" + orgType,
				autoParam : ["id"],
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							childNodes[i].id = childNodes[i].id;
							childNodes[i].upId = childNodes[i].upId;
							childNodes[i].nodeType = childNodes[i].params.type;
							// childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true: false;
							childNodes[i].isParent = "true";
						}
						return childNodes;
					}
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
				onNodeCreated : function(event, treeId, treeNode) {
					//若是根节点，展开下一级节点
					if(treeNode.tId == "tree_1"){
						orgTree.reAsyncChildNodes(treeNode, "refresh");
					}
				}
			}
		};
		
		settingSync = {  //同步
			data : {
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
			view : {
				selectedMulti : false
			}
		};
		
		$("#treeSearchIcon").bind("click", function(){
			setTree($("#treeSearchInput").val() != "")
		});
		$("#treeSearchInput").bind("keydown", function(e){
			if(e.keyCode == 13){
				setTree($("#treeSearchInput").val() != "")
			}
		});
		
		setTree(false);  //初始为false
	}
	
	function setTree(searchFlag){
		if(searchFlag){
			orgTree = $.fn.zTree.init($("#tree"), settingSync);
			loadTree("${ctx}/frs/rpttsk/publish/searchOrgTree?orgType=" + orgType+"&d=" + new Date().getTime(), 
					orgTree, {searchName : $("#treeSearchInput").val()});
		}else{
			orgTree = $.fn.zTree.init($("#tree"), settingAsync);
		}
	}
	
	//加载树
	function loadTree(url, treeObj, data){
		 $.ajax({
			cache : false,
			async : true,
			url : url,
			type : "POST",
			dataType : "json",
			data : data,
			beforeSend : function(){
			},
			complete : function(){
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result){
				var nodes = treeObj.getNodes();
				for(var i=0; i<nodes.length; i++){
					treeObj.removeNode(nodes[i], false);   //移除先前节点
				}
				if(result.length > 0){
					treeObj.addNodes(null, result, false);
					treeObj.expandAll(true);
				}
			},
			error : function(result, b){
			}
		 });
	}

</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6;">
			<div id="treeSearchbar" style="width:99%; height:20px; margin-top:2px; padding-left:2px;">
				<ul>
					<li style="width:100%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field" style="width: 100%;padding-top: 0px;padding-left: 0px;bottom: 0px;" />    
								<div class="l-trigger">                                                                      
									<i id="treeSearchIcon" class="icon-search search-size"></i>                                                 
								</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div>
			<div id="treeContainer"
				style="width: 100%; padding-top: 5px; overflow: auto; clear: both; background-color: #FFF">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="label" class="label-content">
			
		</div>
	</div>
</body>
</html>