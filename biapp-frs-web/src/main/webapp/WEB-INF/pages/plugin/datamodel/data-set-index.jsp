<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template7_BS.jsp">
<head>
<script type="text/javascript">
	var leftTreeObj;
	var dialog;
	var currentNode = null;//选中树节点
	var datasetGrid = null;
	var catalogBox = null;;

	$(function() {
		// var $centerDom = $(document);
        var $centerDom = $(window.document);
		gridCenter = $centerDom.height() - 26;
		initTree();
		initTab();
		initExport();
		//渲染树
		function initTree() {
			//树
			var setting = {
				async : {
					enable : true,
					url : "${ctx}/rpt/frame/dataset/getTree.json?t="
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
							leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
						}
					}
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			$("#template.left.center").hide();

			//TreeToolBar
			$("#treeToolbar").ligerTreeToolBar({
				items:[{
					icon:'menu',
					text:'目录',
					operNo : 'date-set-menu',
					color :'#fcca54',
					menu:{
						width:90,
							items : [ {
								icon : 'add',
								text : '添加',
								click : addCatalog
							}, {
								line : true
							}, {
								icon : 'chartForm',
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
				}
				,{
					icon:'fontSize',
					text:'数据',
					operNo : 'date-set-impexp',
					menu:{
						width:90,
						items:[{
							icon:'database',
							text:'模板导出',
							click : exportSet
						},{
							icon:'screening',
							text:'模板导入',
							click : improtSet
						}]
					} 
				}],
				treemenu : false
			});
		}
	});
	
	var expModel = function(){
		BIONE.commonOpenSmallDialog("导出数据","exportReports","${ctx}/rpt/frame/dataset/expModel");
	};
	
	var exportTmp=function(fileName){
		var src = '';
		src = "${ctx}/rpt/frame/dataset/exportTmpInfo?filepath="+fileName+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	
	var impModel = function (){
		BIONE.commonOpenDialog("导入数据","importReports",600,380,"${ctx}/rpt/frame/dataset/impModel",null,
			function() {
				if(window.pathname!=null&&window.pathname!=""){
					$.ajax({
						cache : false,
						async : false,
						url : "${ctx}/report/frame/design/cfg/deleteFile",
						dataType : 'json',
						data : {
							pathname: window.pathname
						},
						type : "POST"
					});
				}
			});
	};
	
	var exportExcel=function(fileName,type){
		var src='';
		src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+fileName;
		downdload.attr('src', src);
	};
	var initExport=function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	//渲染选项卡
	function initTab() {
		window['maintab'] = $("#tab").ligerTab({
			contextmenu : true
		});
		maintab.addTabItem({
			tabid : "topTab",
			text : "数据集",
			showClose : false,
			content : "<div id='gridFrame' style='height:" + gridCenter
					+ "px;width:100%;'></div>"
		});
		content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/rpt/frame/dataset/grid?catalogId="
			+ "&catalogName="+encodeURI(encodeURI("全部"))
			+ "'></iframe>";
		$("#gridFrame").html(content);
		
	}

	//树的点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			if (treeNode.id == "ROOT") {
				currentNode = null;
			} else {
				currentNode = treeNode;
			}
			if (!datasetGrid) {
				content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/rpt/frame/dataset/grid?catalogId="
						+ treeNode.realId
						+ "&catalogName="
						+ encodeURI(encodeURI(treeNode.text))
						+ "'></iframe>";
				$("#gridFrame").html(content);
			} else {
				if (treeNode.id == "ROOT") {
					treeNode.realId = "";
				}
				datasetGrid.set('parms', {
					catalogId : treeNode.realId,
					catalogName : treeNode.text,
					d : new Date().getTime()
				});
				datasetGrid.loadData();
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
		var url = "${ctx}/rpt/frame/dataset/catalog?upId=";
		if (currentNode != null && currentNode.realId != "ROOT")
			url += currentNode.realId;
		else
			url += "0";
		BIONE.commonOpenDialog("新增数据集目录", "editCatalogBox", 420, 270, url);
	}

	//修改分类
	function editCatalog() {
		if (!currentNode) {
			BIONE.tip("未选择任何目录。");
			return;
		}
		var  updateUrl = "${ctx}/rpt/frame/dataset/catalog?catalogId="
			+ currentNode.realId+"&upId="+currentNode.upId;  
		BIONE.commonOpenDialog("修改数据集目录", "editCatalogBox", 420, 270,
				updateUrl);
		
	}

	//删除目录
	function deleteCatalog() {
		if (!currentNode || currentNode.id == 'ROOT') {
			BIONE.tip("未选择任何目录。");
			return;
		}
		$.ligerDialog.confirm("确定删除数据集目录 [ " + currentNode.text + " ] ？",
				function(flag) {
					if (flag) {
						$.ajax({
							type : "POST",
							url : "${ctx}/rpt/frame/dataset/deleteCatalog",
							data : {
								catalogId : currentNode.realId
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
									BIONE.tip("该目录下存在数据集，无法删除。");
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
	
	//模板导出
	function exportSet(){
		BIONE.commonOpenDialog("数据模型模板导出", "exportWin", 600, 480,
		"${ctx}/report/frame/wizard/exportWin?type=Model");
	}
	
	//模板导入
	function improtSet(){
		BIONE.commonOpenDialog("数据模型模板导入", "importWin", 600, 480,
		"${ctx}/report/frame/wizard?type=Model");
	}
	
	function   reload(){
		leftTreeObj.reAsyncChildNodes(null, "refresh");
	}
	
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">数据集目录</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
</body>
</html>