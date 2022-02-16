<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template7.jsp">
<head>
<script type="text/javascript">
	var leftTreeObj;
	var dialog;
	var currentNode = null;//选中树节点
	var tempGrid = null;
	var catalogBox = null;

	$(function() {

		var $centerDom = $(document);
		gridCenter = $centerDom.height() - 26;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/param/tempfix/getTree.json",
			dataType : 'json',
			type : "post",
			success : function(result) {
				initTree(result);
			}
		});
		
		initTab();
	});
		//渲染树
		function initTree(zNode) {
			//树
			
			var setting = {
					data : {
						key : {
							id: "id",
							name : "text",
							realId: "realId"
						}
					},
					callback : {
						onClick : zTreeOnClick
					}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting,zNode);
			$("#template.left.center").hide();
			//menu
			var menuCatalog = {
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
				};
				
				var menuTemp = {
					width :90,
					items : [ {
						icon : 'add',
						text : '添加',
						click : addTemp
					}, {
						line : true
					}, {
						icon : 'modify',
						text : '修改',
						click : editTemp
					}, {
						line : true
					}, {
						icon : 'delete',
						text : '删除',
						click : deleteTemp
					}]
				};
			//TreeToolBar
			 $("#treeToolbar").ligerTreeToolBar({
				items : [ {
					icon : 'config',
					text : '目录',
					menu : menuCatalog
				}, {
					line : true
				}, {
					icon : 'bookpen',
					text : '模板',
					menu : menuTemp
				} ],
				treemenu : false
			}); 
		}

	//渲染选项卡
	function initTab() {
		window['maintab'] = $("#tab").ligerTab({
			contextmenu : true
		});
		maintab.addTabItem({
			tabid : "topTab",
			text : "固定参数模板",
			showClose : false,
			content : "<div id='gridFrame' style='height:" + gridCenter
					+ "px;width:100%;'></div>"
		});
	}

	//树的点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
			
			if (!tempGrid&&!currentNode.isParent) {
				content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/report/frame/param/tempfix/grid?paramTempId="
						+ treeNode.id
						+ "&paramTempName="
						+ treeNode.text
						+ "'></iframe>";
						
				$("#gridFrame").html(content);
			}
			if(currentNode.id=="0"){
				currentNode = null;
			}
		}
	}

	//刷新树
	function refreshTree() {
		if (leftTreeObj) {
			var selectedNodes = leftTreeObj.getSelectedNodes();
			if (selectedNodes != null && selectedNodes.length > 0) {
				//若树有选中节点,刷新指定节点
				leftTreeObj.reAsyncChildNodes(selectedNodes[0], "refresh");
			} else {
				//其他情况，刷新全部
				leftTreeObj.reAsyncChildNodes(null, "refresh");
			}
		}
	}

	//新增分类
	function addCatalog() {
		var url = "${ctx/report/frame/param/templates/catalog?upId=";
		if (currentNode != null && currentNode.realId != "ROOT")
			url += currentNode.id;
		else
			url += "0";
		BIONE.commonOpenDialog("新增参数模板目录", "editCatalogBox", 420, 270, url);
	}

	//修改分类
	function editCatalog() {
		if (!currentNode) {
			BIONE.tip("未选择任何目录。");
			return;
		}
		if(!currentNode.isParent)
		{
			BIONE.tip("请选择参数目录。");
			return;
		}
		BIONE.commonOpenDialog("修改参数模板目录", "editCatalogBox", 420, 270,
				"${ctx}/report/frame/param/templates/catalog?catalogId="
						+ currentNode.id);
	}

	//删除目录
	function deleteCatalog() {
		if (!currentNode || currentNode.id == 'ROOT') {
			BIONE.tip("未选择任何目录。");
			return;
		}
		if(!currentNode.isParent)
			{
			BIONE.tip("请选择参数目录。");
			return;
			}
		$.ligerDialog.confirm("确定删除参数模板目录 [ " + currentNode.text + " ] ？",
				function(flag) {
					if (flag) {
						$.ajax({
							type : "POST",
							url : "${ctx}/report/frame/param/templates/deleteCatalog",
							data : {
								catalogId : currentNode.id
							},
							success : function(res) {
								if (res) {
									BIONE.tip("删除成功！");
									//局部刷新树
									leftTreeObj.reAsyncChildNodes(currentNode
											.getParentNode(), "refresh");
									initTab();
									currentNode = null;
								} else {
									BIONE.tip("该目录下存在参数模板，无法删除。");
								}
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								BIONE.tip('删除失败,错误信息:' + textStatus);
							}
						});
					}
				});
	}
	//新增分类
	function addTemp() {
		var url = "${ctx}/report/frame/param/tempfix/temp?CatalogId=";
		if (currentNode != null && currentNode.id != "ROOT"&&currentNode.isParent)
			{
				url += currentNode.id;
				url += "&&CatalogName="+currentNode.text;
				
				BIONE.commonOpenDialog("新增固定参数模板信息", "editTempFix", 620, 400, url);
			}
		else
			{
				BIONE.tip("请选择参数目录。");
			}
	}
	
	//修改分类
	function editTemp() {
		if (!currentNode||currentNode.isParent) {
			BIONE.tip("未选择固定参数模板。");
			return;
		}
		BIONE.commonOpenDialog("修改固定参数模板信息", "editTempFix", 620, 400,
				"${ctx}/report/frame/param/tempfix/temp?ParamtmpId="
						+ currentNode.id);
	}

	//删除目录
	function deleteTemp() {
		if(currentNode.isParent)
			{
			BIONE.tip("请选择固定参数模板。");
			return;
			}
		$.ligerDialog.confirm("确定删除固定参数模板 [ " + currentNode.text + " ] ？",
				function(flag) {
					if (flag) {
						$.ajax({
							type : "POST",
							url : "${ctx}/report/frame/param/tempfix/deleteTemp",
							data : {
								paramtmpId : currentNode.id
							},
							success : function() {	
								BIONE.tip("删除成功！");
								//局部刷新树
								leftTreeObj.reAsyncChildNodes(currentNode
										.getParentNode(), "refresh");
								initTab();
								currentNode = null;
							
							},
							error : function(XMLHttpRequest, textStatus,
									errorThrown) {
								BIONE.tip('删除失败,错误信息:' + textStatus);
							}
						});
					}
				});
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">固定参数模板目录</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
</body>
</html>