<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22.jsp">
<head>
<script type="text/javascript">
	var leftTreeObj;
	var dialog;
	var currentNode = null;//选中树节点
	var catalogBox = null;;
	var selectedTab=null;
	var download=null;
	$(function() {
		var $centerDom = $(document);
		framCenter = $centerDom.height() - 38;
		initTree();
		$(".l-treemenubar-item").css("z-index",999);
		$("#mainframe").attr('src','${ctx}/rpt/frame/rptmgr/info/welcome');
		initExport();
	});
	function initTree() {
		//树
		var setting = {
			data : {
				key : {
					name : "text"
				},
				simpleData:{
					enable:true,
					idKey: "id",
					pIdKey: "upId"
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
						leftTreeOb.expand(treeNode,true,true,true);
					}
				}
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		$("#template.left.center").hide();
		loadTree("${ctx}/rpt/frame/rptmgr/info/getRptTree",leftTreeObj);
		
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
		
		var menuReport = {
			width :150,
			items : [{
				icon : 'add',
				text : '新增报表',
				click : reportAdd
			}, {
				icon : 'delete',
				text : '删除报表',
				click : reportDelete
			}/*,{
				line : true
			} ,	{
				icon : 'import',
				text : '数据导入',
				click : dataImport
			},{
				icon : 'export',
				text : '数据导出',
				click : dataExport
			} */,{
				line : true
			},	{
				icon : 'import',
				text : '模板导入',
				click : reportImport
			},{
				icon : 'export',
				text : '模板导出',
				click : reportExport
			}/* ,{
				line : true
			},{
				icon : 'import',
				text : '数据集数据导入',
				click : function() {
					BIONE.commonOpenDialog("数据集数据导入", "importWin", 600,380,
					"${ctx}/rpt/frame/rptmgr/info/impReports?type=DataRel");
				}
			},{
				icon : 'export',
				text : '数据集数据导出',
				click : function() {
					BIONE.commonOpenDialog("数据集数据导出", "exportWin", 600, 480,
					"${ctx}/rpt/frame/rptmgr/info/reportExport?type=DataRel");
				}
			}*/, {
				line : true
			},{
				icon : 'import',
				text : '数据集模板导入',
				click : function() {
					BIONE.commonOpenDialog("数据集模板导入", "importWin", 600, 480,
					"${ctx}/report/frame/wizard?type=Rptrel");
				}
			},{
				icon : 'export',
				text : '数据集模板导出',
				click : function() {
					BIONE.commonOpenDialog("数据集模板导出", "exportWin", 600, 480,
					"${ctx}/report/frame/wizard/exportWin?type=Rptrel");
				}
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
				text : '报表',
				menu : menuReport
			} ],
			treemenu : false
		});
		$("#treeSearchIcon").live('click',function(){
			var searchNm=$("#treeSearchInput").val();
			loadTree("${ctx}/rpt/frame/rptmgr/info/getInnerRptTree" ,leftTreeObj,{searchNm:searchNm});
		});
	}
	
	var exportData=function(fileName,type){
		var src = '';
		src = "${ctx}/rpt/frame/rptmgr/info/exportDataInfo?type="+type+"&filepath="+fileName+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	
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
					component.addNodes(null, result, false);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	//树的点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
			if(currentNode.params.nodeType == "03"&&currentNode.params.rptType=="01"){//判断	是否是报表节点
				$("#mainframe").attr('src','${ctx}/rpt/frame/rptmgr/info/reportInfo?rptId='+currentNode.params.realId);
			}
			else if(currentNode.params.nodeType == "03"&&currentNode.params.rptType=="02"){
				$("#mainframe").attr('src','${ctx}/rpt/frame/rptmgr/info/reportFrs?rptId='+currentNode.params.realId+"&show=manage");
			}
		}
	}

	//新增报表发布
	function reportAdd() {
		$("#mainframe").attr('src','${ctx}/rpt/frame/rptmgr/info/reportInfo');
	}
	
	function removeInfo(){
		$("#mainframe").attr('src','');
	}
	//报表删除
	function reportDelete() {
		if (!currentNode || currentNode.id == 'ROOT') {
			BIONE.tip("请选择报表节点。");
			return;
		}
		if(currentNode.params.nodeType != "03"){
			BIONE.tip("请选择报表节点。");
			return;
		}
		$.ligerDialog.confirm("确定删除报表 [ " + currentNode.text + " ] ？",
				function(flag) {
					if (flag) {
						$.ajax({
							type : "POST",
							url : "${ctx}/rpt/frame/rptmgr/info/deleteReport",
							data : {
								rptId : currentNode.params.realId
							},
							success : function(res) {
									
									BIONE.tip("删除成功！");
									//局部刷新树
									window.refreshAllTree();
									$("#mainframe").attr('src','');
									
									
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

	//刷新树 选中节点的父节点下内容
	function refreshTree() {

	}
	
	function dataImport(){
		BIONE.commonOpenDialog("报表数据导入", "importWin", 600,380,
				"${ctx}/rpt/frame/rptmgr/info/impReports?type=Report");
		
	}
	
	function dataExport(){
		BIONE.commonOpenDialog("报表数据导出", "exportWin", 600, 480,
		"${ctx}/rpt/frame/rptmgr/info/reportExport?type=Report");
		
	}

	
	function reportImport(){
		BIONE.commonOpenDialog("报表模板导入", "importWin", 600, 480,
				"${ctx}/report/frame/wizard?type=Report");
		
	}
	
	function reportExport(){
		BIONE.commonOpenDialog("报表模板导出", "exportWin", 600, 480,
				"${ctx}/report/frame/wizard/exportWin?type=Report");
		 
	}


	//刷新整个树
	function refreshAllTree() {
		loadTree("${ctx}/rpt/frame/rptmgr/info/getRptTree",leftTreeObj,{searchNm:$("#treeSearchInput").val()});
	}
	
	//新增目录
	function addCatalog() {
		var url = "${ctx}/rpt/frame/rptmgr/info/catalog?upId=";
		if (!currentNode) {
			url += "0";
		}
		else{
			if(currentNode.params.nodeType == "03"){
				BIONE.tip("请选择目录节点。");
				return;
			}
			if (currentNode.params.nodeType != "01")
				url += currentNode.params.realId;
			else
				url += "0";
		}
		BIONE.commonOpenDialog("新增报表目录", "editCatalogBox", 500, 320, url);
	}

	//修改目录
	function editCatalog() {
		if (!currentNode) {
			BIONE.tip("未选择任何目录。");
			return;
		}
		if(currentNode.params.nodeType != "02"){
			BIONE.tip("请选择目录节点。");
			return;
		}
		BIONE.commonOpenDialog("修改报表目录", "editCatalogBox", 420, 270,
				"${ctx}/rpt/frame/rptmgr/info/catalog?catalogId=" + currentNode.params.realId);
	}

	//删除目录
	function deleteCatalog() {
		if (!currentNode || currentNode.id == 'ROOT') {
			BIONE.tip("请选择目录节点。");
			return;
		}
		if(currentNode.params.nodeType != "02"){
			BIONE.tip("请选择目录节点。");
			return;
		}
		$.ligerDialog.confirm("确定删除报表目录 [ " + currentNode.text + " ] ？",
				function(flag) {
					if (flag) {
						$.ajax({
							type : "POST",
							url : "${ctx}/rpt/frame/rptmgr/info/deleteCatalog",
							data : {
								catalogId : currentNode.params.realId
							},
							success : function(res) {
								if (res) {
									BIONE.tip("删除成功！");
									//局部刷新树
									refreshAllTree();
									currentNode = null;
								} else {
									BIONE.tip("该目录下存在报表，无法删除！");
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
	
	var exportExcel=function(fileName,type){
		var src='';
		src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+fileName;
		downdload.attr('src', src);
	};
	
	var initExport=function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	
	function   reload(){
		var searchNm=$("#treeSearchInput").val();
		loadTree("${ctx}/rpt/frame/rptmgr/info/getRptTree" ,leftTreeObj,{searchNm:searchNm});
	}
	
	
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">报表管理</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
	<div id="template.right">
		<iframe frameborder='0' id='mainframe' name='o' style='height:100%;width:100%;' src=''></iframe>
	</div>
</body>
</html>