<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template24S.jsp">
<head>
<script type="text/javascript">
	var leftTreeObj;
	var dialog;
	var currentNode = null;//选中树节点
	var datasetGrid = null;
	var catalogBox = null;
	var tabObj;
	
	var canEdit = window.parent.taskManage.templateManage.canEdit;

	var idxInfos =[];
	var currentNodeId = null;
	
	var dimFilterManager;
	var dimFilterInfo;
	
	var treeTIdIndex =0;

	$(function() {
		initTab();
		initTreeToolbar();
		initTree();
		initBtn();
		initBaseInfo();
	});
	
	function initBtn(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		});
		if(canEdit=="true"){
			buttons.push({
				text : '确定',
				onclick : f_ok
			});
		}
		BIONE.addFormButtons(buttons);
	}
	
	function f_close(){

		BIONE.closeDialog("rptUpdateCfgBox");
		
		
	}
	function f_ok(){
		var treeNodes = leftTreeObj.getNodes();
		if(treeNodes == null || treeNodes.length == 0){
			BIONE.tip("请选择指标");
		}
		var selectedNodeId = leftTreeObj.getSelectedNodes()[0].tId;
		updateDimFilterInfo(selectedNodeId);
		var idxInfos=[];
		for(var i = 0 ; i<treeNodes.length;i++){
			var treeNode = treeNodes[i];
			var tlCfgNm = treeNode.cfgNm||treeNode.text;
			idxInfos.push({idxId:treeNode.id,
				idxNm:treeNode.text,
				dimFilterInfo:treeNode.filterinfos,
				filterFormula:treeNode.formula,
				orgInfos:treeNode.orgInfos,
				orgMode:treeNode.orgMode,
				cfgNm:tlCfgNm
				});
		}
		window.parent.taskManage.templateManage.initIdxTbl(idxInfos);
		BIONE.closeDialog("rptUpdateCfgBox");
	}
	
	function initBaseInfo(){
		var tmpFilterInfos = window.parent.taskManage.templateManage.getDimFilterInfo();
		if(tmpFilterInfos!=null&& typeof tmpFilterInfos!="undefined"){
			for(var i = 0 ; i <tmpFilterInfos.length ;i ++){
				appendOneIdx(tmpFilterInfos[i],tmpFilterInfos[i].filterinfos,tmpFilterInfos[i].formula);
			}
		}
		/*
		dimFilterInfo
		window.parent.taskManage.templateManage.initIdxTbl(idxInfos);
		var selectedNode = selectedNodes[0];
		dimFilterInfo = treeNode.filterinfos;
		*/
	}

	//渲染树
	function initTree() {
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
				callback : {
					onClick : onClickTree
				},
				edit:{
					enable: true,
					showRemoveBtn: false,
					showRenameBtn: false
				}
			};
		leftTreeObj =$.fn.zTree.init($("#leftTree"), setting);
		$("#template.left.center").hide();
	}
	
	function onClickTree(event, treeId, treeNode){
		if(currentNodeId!=null){
			updateDimFilterInfo(currentNodeId);
		}
		currentNodeId = treeNode.tId;
		initCurrentTab(treeNode);
	}
	
	function updateDimFilterInfo(nodeId){
		if(dimFilterManager==null) return ;
		var tmpNode = leftTreeObj.getNodeByTId(nodeId); 
		if(tmpNode==null||typeof tmpNode=="undefined") return ;
		var tmpFilterInfo = dimFilterManager.getDimFilterInfo();
		tmpNode.filterinfos = tmpFilterInfo.dimFilterInfo;
		tmpNode.formula = tmpFilterInfo.filterFormula;
		leftTreeObj.updateNode(tmpNode);
		
	}
	
	function initTreeToolbar(){
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'add',
				text : '添加指标',
				click : addIdx
			}, {
				line : true
			}, {
				icon : 'delete',
				text : '删除指标',
				click : deleteIdx
			} ],
			treemenu : false
		});
	}
	
	function addIdx(){

			var options = {
				url : "${ctx}/rpt/input/idxdatainput/selectIdx",
				dialogname : 'selectIdx',
				title : '选择指标',
				comboxName : 'selectIdxBox'
			};
			BIONE.commonOpenIconDialog(options.title, options.dialogname,
					options.url, options.comboxName);
			return false;
	}
	
	function deleteIdx(){
		var selectedNodes = leftTreeObj.getSelectedNodes();
		if(selectedNodes==null ||typeof selectedNodes=="undefined"||selectedNodes.length!=1)
		{
			BIONE.tip("请选择指标进行删除");
			return ;
		}

		var selectedNode = selectedNodes[0];
		var preNode = selectedNode.getPreNode();
		if(preNode!=null&&typeof preNode!="undefined" ){

			initCurrentTab(preNode);
			leftTreeObj.selectNode(preNode);
		}else{
			var nextNode = selectedNode.getNextNode();
			if(nextNode!=null&&typeof nextNode!="undefined" ){
				initCurrentTab(nextNode);
				leftTreeObj.selectNode(nextNode);
			}else
				initCurrentTab(null);
		}
		leftTreeObj.removeNode(selectedNode);
	}
	
	//渲染选项卡
	function initTab() {
		var tabHeight = $("#tab").height()+90;
		tabObj = $("#tab").ligerTab({
			contextmenu : true,
			height:tabHeight
		});
	}

	function  initCurrentTab(treeNode){
		var curTabUri,tabTitleName;
		if(treeNode!=null && typeof treeNode !="undefined")
		{
			dimFilterInfo = treeNode.filterinfos;
			curTabUri = "${ctx}/rpt/input/idxdatainput/idxdimfliter?indexNo="
				+ treeNode.id+"&type=2";
			tabTitleName = "指标["+treeNode.text+"]"; 
			

		}else{
			curTabUri = "";
			tabTitleName = ""; 
		}
		var height = $("#tab").height() - 160;
		tabObj.removeTabItem("templateConfigTab");
		tabObj
			.addTabItem({
				tabid : "templateConfigTab",
				text:tabTitleName,
				showClose : false,
				content : '<iframe frameborder="0" id="idxInputTabFrame" name="idxInputTabFrame" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
			});
			tabObj.selectTabItem("templateConfigTab");
	}	
	function appendOneIdx(idx,filterinfos,formula){
		treeTIdIndex = treeTIdIndex +1;
		var newTid = "tree_"+treeTIdIndex;
		
		idx.upId=null;
		idx.filterinfos=filterinfos||[];
		idx.formula = formula||{};
		idx.tId = "tree_"+newTid;
		leftTreeObj.addNodes(null,idx,false);
		var newNode = leftTreeObj.getNodeByTId(newTid);
		leftTreeObj.selectNode(newNode);
		
		if(currentNodeId!=null){
			updateDimFilterInfo(currentNodeId);
		}
		currentNodeId = newTid;
		initCurrentTab(newNode);
	}

</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">指标选择</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
</body>
</html>