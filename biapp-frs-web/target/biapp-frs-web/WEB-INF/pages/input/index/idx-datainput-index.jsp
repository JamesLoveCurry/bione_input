<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template7.jsp">
<script type="text/javascript">
	var leftTreeObj, items, currentNode,grid,tabObj,height,previewObjId;
	$(function(){
		initToolBar();
		initTree();
		initTab();
		initBaseInfo();
		initCurrentTab("ROOT","catalog","ROOT");
	});
	
	function initBaseInfo(){
        var $centerDom = $(document);
		height = $centerDom.height() - 38;
		$("#treeSearchbar").hide();
	}
	
	function initTab(){
		tabObj = $("#tab").ligerTab({
			contextmenu : true
		});
	}
	
	function initToolBar(){

		$("#treeToolbar").ligerTreeToolBar({
			items:[{
				icon:'config',
				text:'目录维护',
				menu:{
					width : 90,
					items : [ {
						icon : 'add',
						text : '新建',
						click : addDatainputCatalog
					}, {
						line : true
					}, {
						icon : 'modify',
						text : '修改',
						click : updateDatainputCatalog
					},{
						icon : 'delete',
						text : '删除',
						click : deleteDatainputCatalog
					}, {
						line : true
					}]
				}
			}]
			
		}); 
	}
	
	
	function refreshTree(refreshNode){
		if(refreshNode==null ||typeof refreshNode == "undefined")
		{
			var selectedNodes = leftTreeObj.getSelectedNodes();
			if(selectedNodes==null||selectedNodes.length==0)
				leftTreeObj.reAsyncChildNodes(null,"refresh");
			else{
				var node= selectedNodes[0];
				var nodeType = node.params.nodeType;
				if(nodeType=="taskinfo"){
					if(node.getParentNode()== null)
						leftTreeObj.reAsyncChildNodes(null,"refresh");
					else
						leftTreeObj.reAsyncChildNodes(node.getParentNode(), "refresh");
				}
				else if(nodeType=="catalog")
					leftTreeObj.reAsyncChildNodes(node,"refresh");
			}
			refreshNode = node;
		}
		if(refreshNode!=null &&typeof refreshNode != "undefined")
		{
			leftTreeObj.selectNode(refreshNode,false);
			initCurrentTab(refreshNode.id,refreshNode.params.nodeType,refreshNode.text);
		}else
			initCurrentTab("ROOT","catalog","ROOT");
	}
	
	function  addDatainputCatalog(){
		var id,name ;
		if (!currentNode) {
			upNo = "ROOT";
			upName = "根目录";
		} else{
			var nodeType = currentNode.params.nodeType;
			if(nodeType="catalog")
			{
				upNo = currentNode.id;
				upName = currentNode.text;
			}
			else{
					parentNode = currentNode.getParentNode();
					if(parentNode ==null)
					 {
						upNo = "ROOT";
						upName = "根目录";
					}
					else 
					{	
						upNo = parentNode.id;
						upName = parentNode.text;
					}
			}
		}
		var modelDialogUri = "${ctx}/rpt/input/idxdatainput/newCatalog?upNo=" + upNo+"&upName="+encodeURI(encodeURI(upName));
		BIONE.commonOpenDialog("任务目录添加", "rptDataInputEditCatalog","660","320",modelDialogUri);		 
	}
    function  updateDatainputCatalog(){
    	var id,upName;
		if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		}else {
			var nodeType = currentNode.params.nodeType;
			if(nodeType=="catalog")
				{
					id=currentNode.id;
					parentNode = currentNode.getParentNode();
					if(parentNode ==null)
					 {
						upName = "根目录";
					}
					else 
					{	
						upName = parentNode.text;
					}
				}
			else{
				BIONE.tip("请选择目录");
				return;
			}
		}
		var modelDialogUri = "${ctx}/rpt/input/idxdatainput/initUpdateCatalog?catalogId=" + id+"&upName="+ encodeURI(encodeURI(upName));
		BIONE.commonOpenDialog("任务目录修改", "rptDataInputEditCatalog","660","320",modelDialogUri);
    }
    function  deleteDatainputCatalog(){
    	if (!currentNode) {
			BIONE.tip("请选择目录");
			return;
		}
		var  type  =   currentNode.params.nodeType;
		if(type=="taskinfo"){
			return;
		}
		var parentNode= currentNode.getParentNode();
		$.ajax({
			type : "POST",
			url : '${ctx}/rpt/input/idxdatainput/canDeleteCatalog.json?catalogId=' + currentNode.id,
			success : function(result) {
				  if(result=="1"){
					  BIONE.tip("该指标目录下面包含任务，不能进行删除操作！");
					  return;
				  }else if(result=="0"){
					  $.ligerDialog.confirm('将执行级联删除操作，是否继续', '指标目录删除', function(yes) {
							if (yes) {
								if (currentNode.id != null) {
									BIONE.ajax({
										type : 'DELETE',
										url : '${ctx}/rpt/input/idxdatainput/delCatalog/' + currentNode.id
									}, function(result) {
										var returnStr = result.msg;
										if (returnStr == "0") {
											leftTreeObj.reAsyncChildNodes(currentNode
													.getParentNode(), "refresh");
											if(parentNode!=null )
			                                	initCurrentTab(parentNode.id,parentNode.params.nodeType,parentNode.text);
											else
												initCurrentTab("ROOT","catalog","ROOT");
											currentNode = null;
											BIONE.tip("删除成功");
										}else {
											BIONE.tip("删除失败");
										} 
									});
								}
							}
						});
				  }
			}
		});
	}
    
	function searchHandler(){
		//供子页面使用
		leftTreeObj.reAsyncChildNodes(null,"refresh");
	}
	
	function initTree() {
		var  url_ =  "${ctx}/rpt/input/idxdatainput/getTemplateTree.json?";
		var setting ={
				async:{
					enable:true,
					url:url_,
					autoParam:["nodeType", "id=nodeId"],
					dataType:"json"
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
					onClick : zTreeOnClick,
					onNodeCreated : function(event,treeId,treeNode){
						if (treeNode.id == "ROOT") {
							//若是根节点，展开下一级节点
							leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
						}
					}
				}

		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	//树点击事件
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		if(currentNode)
			previewObjId = currentNode.Id;
		currentNode = treeNode;
		var  nodeType = currentNode.params.nodeType;
		var ralateToTask = currentNode.params.ralateToTask;
        initCurrentTab(currentNode.id,nodeType,currentNode.text,ralateToTask);
	} 	
	function  initCurrentTab(nodeId,nodeType,nodeName,ralateToTask){		
		var curTabUri = "${ctx}/rpt/input/idxdatainput/templateInfos?nodeId="+nodeId;
		var tabTitleName = "任务节点"; 
		if(nodeType=="template")
		{
			tabTitleName = "任务"; 
			curTabUri = "${ctx}/rpt/input/idxdatainput/idxInputFrame?templateId="+encodeURI(encodeURI(nodeId))+"&canEdit="+(ralateToTask=="未下发");
		}
		curTabUri = curTabUri+"&nodeType="+nodeType;
		tabTitleName =tabTitleName+"["+nodeName+"]";
		tabObj.removeTabItem("rptDatainputInputTab");
		tabObj
			.addTabItem({
				tabid : "rptDatainputInputTab",
				text:tabTitleName,
				showClose : false,
				content : '<iframe frameborder="0" id="idxInputTabFrame" name="idxInputTabFrame" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
			});
			tabObj.selectTabItem("rptDatainputInputTab");	    
	}
	function   reload(){
		searchHandler();
	}
</script>

<title>任务信息</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">任务目录</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
	<div id="template.right">
	    <div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>