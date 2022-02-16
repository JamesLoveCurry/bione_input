<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	//指标选择页
	var btns =[]; 
	var currentNode;//当前点击节点
	var searchNm;
	var searchTreeObj;
	$(function(){
		initTree();
		initBtn();
		//响应回车事件
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				if($('#treeSearchInput').val()){
					initSearchTree($('#treeSearchInput').val());
				}else{
					initTree();
				}
			}
		});
		//搜索点击事件treeSearchIcon
 		$('#treeSearchIcon').live('click', function() {
 			if($('#treeSearchInput').val()){
 				initSearchTree($('#treeSearchInput').val());
			}else{
				initTree();
			}
		});
	});
	function initSearchTree(searchNm){
		var url = "${ctx}/cabin/rpt/idx/config/getSyncTree";
		var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
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
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : zTreeOnClick
				}
		};
		searchTreeObj = $.fn.zTree.init($("#tree"),setting,[]);
		BIONE.loadTree(url,searchTreeObj,data,function(childNodes){
			if(childNodes){
				for(var i = 0;i<childNodes.length;i++){
					if(i < 1){
						childNodes.splice(i,1)
				    }
				}
			}
			return childNodes;
			
		});
	}
	//初始化树
	function initTree() {
		
	var setting = {
			async:{
				enable : true,
				url : "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=1&isPublish=1&t="
					+ new Date().getTime(),
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
				BIONE.closeDialog("mainIdx");
			}
		}, {
			text : "选择",
			onclick : function() {
				if(currentNode != null){
					main = window.parent.selectTab;
/* 					if(currentNode.params.nodeType == "idxInfo"){
						main.$.ligerui.get(main.comboNm)._changeValue(currentNode.id,currentNode.text);
						BIONE.closeDialog("mainIdx");
					}
					else{
						BIONE.tip("请选择指标");
					} */
					if(currentNode.params.nodeType == "idxCatalog"){
						BIONE.tip("请选择子节点");
					}else if(currentNode.params.nodeType == "idxInfo" && currentNode.params.indexType == "05"){
						BIONE.tip("请选择指标度量");
					}else{
						if(currentNode.params.nodeType == "measureInfo"){
							var pNode = currentNode.getParentNode();
							main.$.ligerui.get(main.comboNm)._changeValue(pNode.id+"."+currentNode.id,pNode.text+"."+currentNode.text);
						}else{
							main.$.ligerui.get(main.comboNm)._changeValue(currentNode.id,currentNode.text);
						}
						BIONE.closeDialog("mainIdx");
					}
				}else{
					BIONE.tip("请选择指标");
				}
			}
		});
		BIONE.addFormButtons(btns);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; height: 99%">
			<div id="treeContainer"	style="width: 100%; height: 100%; overflow: auto; clear: both;">
				<div class="l-text l-text-date" style="width: 99.5%;">                    
					<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;" />    
					<div class="l-trigger">                                                                      
						<div id="treeSearchIcon" style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
					</div>
				</div>  
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>