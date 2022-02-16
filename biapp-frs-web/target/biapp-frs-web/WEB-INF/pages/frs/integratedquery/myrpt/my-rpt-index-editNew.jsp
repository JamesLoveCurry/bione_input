<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22.jsp">
<head>
<style type="text/css">
.searchbox {
	margin-top: 5px;
}
.searchtitle img {
	display: block;
	float: left;
}
.searchtitle span {
	display: block;
	float: left;
	margin-top: 1px;
	margin-left: 2px;
	line-height: 19px;
}
.query {
/*  	color: #018DEE; */
 	color: #0071D7;
}
</style>
<script type="text/javascript">
	var searchNm = "";
	var treeObj ;
	var gridCenter;
	$(function(){
		initButton();
		initTree(searchNm);
		gridCenter = $(document).height() - 26;
		//initTab();
		
		$("#treeSearchIcon").live('click', function() {
			var searchNm = $("#treeSearchInput").val();
			initTree(searchNm);
		});
		$("#treeSearchInput").bind('keydown', function(event) {
			if (event.keyCode == 13) {
				var searchNm = $("#treeSearchInput").val();
				initTree(searchNm);
			}
		});
	});
	function searchHandler(){
		var searchName = $("#treeSearchInput").val();
		initTree(searchNm);
	}
	function initButton(){
			 $("#treeToolbar").ligerTreeToolBar({
					items : [{
						icon:'config',
						text:'目录维护',
						menu: {
							width : 90,
							items : [ {
								icon : 'add',
								text : '添加',
								click : addCatalog
							}, {
								line : true
							}, {
								icon : 'modify',
								text : '修改',
								click : editCatalog
							}, {
								line : true
							}, {
								icon : 'delete',
								text : '删除',
								click : deleteCatalog
							}]
						}
					}]
				}); 
		}
	function initTree(searchNm){
		var setting = {
				data : {
					keep : {
						parent : true//该节点的子节点都移走的话，父节点依旧存在
					},
					key : {
						name : "text"
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId",
						rootPId : 0
					}
				},
				view : {
					selectedMulti : false
				},
				edit : {
					enable : true,
					showRemoveBtn : false,
					showRenameBtn : false,
					drag : {
						isCopy : true,
						isMove : false,
						prev : false,
						next : false,
						inner : false
					}
				} ,
				 callback : {
					  onClick : treeOnClick
				} 
			};
			var data = {
					searchNm : searchNm	
			}
			
			treeObj = $.fn.zTree.init($("#tree"),setting);
			if(searchNm){
				BIONE.loadTree("${ctx}/frs/integratedquery/myrpt/collect/initTree",treeObj,data);
			}else{
				BIONE.loadTree("${ctx}/frs/integratedquery/myrpt/collect/initTree",treeObj,data,null,false);
			}
			
	}

	function addCatalog(){
		   //做判断，是否有选中左侧树的节点
		   catalogEdit = BIONE.commonOpenSmallDialog("目录新增","favEdit","${ctx}/frs/integratedquery/myrpt/collect/catalogedit");
	}
	
	function editCatalog(){
		   var selNodes = treeObj.getSelectedNodes();
		   var upid = selNodes[0].data.upFolderId;
		   var userid = selNodes[0].data.userId;
		   if(selNodes.length && selNodes.length > 0){
			   catalogEdit = BIONE.commonOpenSmallDialog("目录修改","favEdit","${ctx}/frs/integratedquery/myrpt/collect/catalogedit?&tid="+selNodes[0].id+"&upid="+upid+"&userId="+userid);
		   }else{
			   BIONE.tip("请选择要修改的目录节点");
		   }
	}

	 function deleteCatalog(){
			if(treeObj == null || typeof treeObj == "undefined"){
				return ;
			}
			$.ligerDialog.confirm('您确定删除这条记录么？', function(yes) {
				if (yes) {
					var selNodes = treeObj.getSelectedNodes();
					if(selNodes.length 
							&& selNodes.length > 0){			
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/frs/integratedquery/myrpt/collect/cabinindex/delCatalog",
							dataType : 'json',
							data : {
								catalogId : selNodes[0].id
							},
							type : "post",
							success : function(result){
								if(result != null && typeof result != "undefined"){
									var flag = result.success;
									if(flag === true){
										BIONE.tip("删除成功");
										clickTreeId = null;
										// 刷新树
										searchHandler();
										// 刷新tab
										//setDefaultTab();
									}else{
										if(result.msg && typeof result.msg != "undefined"){
											BIONE.tip(result.msg);
										}else{								
											BIONE.tip("删除失败，请联系管理员");
										}
									}
								}
							},
							error:function(){
								BIONE.tip("删除失败，请联系系统管理员");
							}
						});
					} else {
						BIONE.tip("请选择要删除的目录节点");
					}
				}
			});
	}
	//右侧的展示页面
	function initTab(){
		 mainTab = $("#tab").ligerTab({
			contextmenu : false
		}); 
		mainTab.addTabItem({
			tabid : "topTab",
			//text : "要素目录管理",
			showClose : false,
			content : "<div id='gridDiv' style='height:" + gridCenter+ "px;width:100%;'></div>"
		}); 
	}
	//点击左侧目录树，显示右侧的面板
	function treeOnClick(event, treeId, treeNode){
		var nodeId = treeNode.id;
		var nodeNm = treeNode.text;
		var nodeType = treeNode.params.nodeType;
		var eleType = treeNode.params.type;
	    var eleurl = "${ctx}/frs/integratedquery/myrpt/collect/content?&nodeId=" + nodeId  +"&nodeNm=" + nodeNm +"&nodeType="+nodeType +"&eleType="+eleType;
		if(treeId == null || typeof treeId == "undefined" || treeNode == null || typeof treeNode == "undefined"){
			return ;
		}
		clickNodeType = treeNode.params.nodeType;
		clickTreeId = treeNode.id;
		content = '<iframe frameborder="0" id="gridFrame" style="height:100%;width:100%;" src=' + eleurl +'></iframe>';
		$("#rightDiv").html(content);
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<a class = "icon-guide "></a>
	</div>
	<div id="template.left.up">
		<span id="treeTitle" style="font-size: 12">目录</span>
	</div>
	<div id="template.left.up.right">
		<div width="130" class="l-text" style="display: block; float: left; height: 17px; margin-top: 2px; width: 127px;">
			<input id="treesearchtext" name="treesearch" type="text" class="l-text l-text-field" style="float: left; height: 16px; width: 127px;" />
		</div>
		<div width="30%" style="display: block; float: left; margin-left: 8px; margin-top: 3px; margin-right: 3px;">
			<a id="treesearchbtn"><img src="${ctx}/images/classics/icons/find.png" /></a>
		</div>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>