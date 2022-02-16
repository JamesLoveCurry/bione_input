<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template22_BS.jsp">
<script type="text/javascript">

	var treeObj , mainTab , rptGrid , gridCenter , catalogEdit , clickCatalogId , clickCatalogType , clickTreeId , clickNodeType;
	var selCatalogId ; 
	var selCatalogNm ;
	
	// 树节点类型
	var rootNodeType = "01";
	var folderNodeType = "02";
	var rptNodeType = "03";
	var busiNodeType = "busiType";
	var systemNodeType = "system";
	
	$(function() {
		// var $centerDom = $(document);
        var $centerDom = $(window.document);
		gridCenter = $centerDom.height() - 26;
		// 初始化页面布局
		initLayout();
		// 构建报表树
		initTree();
		initNodes();
		// 初始化右侧内容
		initTab();
		//初始化导出
		initExport();
	});
	
	var initExport=function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	
	var exportTmp=function(fileName){
		var src = '';
		src = "${ctx}/report/frame/design/cfg/exportTmpInfo?filepath="+fileName+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	// 搜索动作
	var searchHandler = function(){
		var searchName = $("#treeSearchInput").val();
		initNodes(searchName);
	}
	
	// 初始化页面布局
	var initLayout = function(){
		//初始化树查询按钮事件
		$("#treeSearchIcon").bind("click",function(e){
			initTree();
			searchHandler();
		});
		$("#treeSearchInput").bind("keydown",function(e){
			// 按下回车键进行查询
			var code = e.keyCode;
			if(code == null
					|| typeof code == "undefined"
					|| code != "13"){
				return ;
			}
			initTree();
			searchHandler();
		});
		
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
			},{
				icon:'export',
				text:'数据处理',
				menu:{
					width:90,
					items:[{
							icon:'export',
							text:'导出数据',
							click : exportReport
						},{
							line:true
						},{
							icon:'import',
							text:'导入数据',
							click : importReport
					}]
				}
			}]
		});
		if ($.browser.msie && parseInt($.browser.version, 10) == 7) {
			$(".l-treemenubar-item").css("z-index" , '999');
		}
	}
	
	// 初始化tab
	var initTab = function(){
		mainTab = $("#tab").ligerTab({
			contextmenu : false
		});
		mainTab.addTabItem({
			tabid : "topTab",
			text : "报表管理",
			showClose : false,
			content : "<div id='gridDiv' style='height:" + gridCenter
					+ "px;width:100%;'></div>"
		});
	}
	
	// 初始化报表树
	var initTree = function(){
		var async = {
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
			view : {
				selectedMulti : false,
				showLine : false
			},
			callback:{
				onClick : treeOnClick
			}
		};
		treeObj = $.fn.zTree.init($("#tree"),async,[]);
	};
	
	var exportReport = function(){
		BIONE.commonOpenDialog("导出数据","exportFilter",680,400,"${ctx}/report/frame/design/cfg/exportFilter");
		$("#exportReports").css("top","5px");
	};
	
	function commonOpenSmallDialog(title, name,
			url, beforeClose) {
		var width = 450;
		var height = 600;
		var _dialog = $.ligerui.get(name);
		if (_dialog) {
			$.ligerui.remove(name);
		}
		_dialog = $.ligerDialog.open({
			height : height,
			width : width,
			url : url,
			name : name,
			id : name,
			title : title,
			isResize : false,
			isDrag : false,
			isHidden : false
		});
		if (beforeClose != null && typeof beforeClose == "function") {
			_dialog.beforeClose = beforeClose;
		}
		return _dialog;
	};
	
	var importReport = function (){
		BIONE.commonOpenDialog("导入数据","importReports",600,380,"${ctx}/report/frame/design/cfg/impReports",null,
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
		$("#importReports").css("top","5px");
	};
	
	var treeOnClick = function(event, treeId, treeNode){
		if(treeId == null
				|| typeof treeId == "undefined"
				|| treeNode == null
				|| typeof treeNode == "undefined"){
			return ;
		}
		setDefaultTab();
		clickNodeType = treeNode.params.nodeType;
		clickTreeId = treeNode.id;
		if(rootNodeType == clickNodeType){
			clickCatalogId = "";
			clickCatalogType = "";
		}else{
			clickCatalogId = treeNode.params.realId;
			clickCatalogType = treeNode.params.nodeType;
		}
		var headerContent = "报表管理";
		if(folderNodeType == clickNodeType){
			selCatalogId = treeNode.params.realId;
			selCatalogNm = treeNode.text;
			headerContent = "[目录]"+treeNode.text;
		}else if(rptNodeType == clickNodeType){
			selCatalogId = treeNode.params.realId;
			selCatalogNm = treeNode.text;
			headerContent = "[报表]"+treeNode.text;
		}else if(systemNodeType == clickNodeType){
			headerContent = "[制度]"+treeNode.text;
			clickCatalogId = treeNode.params.verId;//当点击制度的时候，要将版本id传入，过滤出这个版本下的报表
		}
		mainTab.setHeader("topTab",headerContent);
		if (!rptGrid) {
			content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src='${ctx}/report/frame/design/cfg/frsindex/content'></iframe>";
			$("#gridDiv").html(content);
		} else {
			rptGrid.set('parms', {
				nodeId:	clickCatalogId,
				nodeType : clickCatalogType
			});
			rptGrid.options.newPage = 1
			rptGrid.loadData();
			document.getElementById("gridFrame").contentWindow.initButtons();
		}
	}
	
	var setDefaultTab = function(){		
		content = "<iframe frameborder='0' id='gridFrame' style='height:100%;width:100%;' src=''></iframe>";
		$("#gridDiv").html(content);
		mainTab.setHeader("topTab","报表管理");
		// 释放frame中的对象
		rptGrid = null;
	}
	
	// 目录管理功能
	var addCatalog = function(){
		catalogEdit = BIONE.commonOpenSmallDialog("目录维护","catalogEdit","${ctx}/report/frame/design/cfg/frsindex/catalogedit");
	}
	
	var editCatalog = function(){
		if(treeObj == null
				|| typeof treeObj == "undefined"){
			return ;
		}
		var selNodes = treeObj.getSelectedNodes();
		if(selNodes.length 
				&& selNodes.length > 0
				&& folderNodeType == selNodes[0].params.nodeType){			
			catalogEdit = BIONE.commonOpenSmallDialog("目录维护","catalogEdit","${ctx}/report/frame/design/cfg/frsindex/catalogedit?catalogId="+selNodes[0].params.realId);
		} else {
			BIONE.tip("请选择要维护的目录节点");
		}
	}
	
	var deleteCatalog = function(){
		if(treeObj == null
				|| typeof treeObj == "undefined"){
			return ;
		}
		$.ligerDialog.confirm('您确定删除这条记录么？', function(yes) {
			if (yes) {
				var selNodes = treeObj.getSelectedNodes();
				if(selNodes.length 
						&& selNodes.length > 0
						&& folderNodeType == selNodes[0].params.nodeType){			
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/report/frame/design/cfg/delCatalog",
						dataType : 'json',
						data : {
							catalogId : selNodes[0].params.realId
						},
						type : "post",
						success : function(result){
							if(result != null
									&& typeof result != "undefined"){
								var flag = result.success;
								if(flag === true){
									BIONE.tip("删除成功");
									// 刷新树
									initTree();
									searchHandler();
									// 刷新tab
									setDefaultTab();
								}else{
									if(result.msg
											&& typeof result.msg != "undefined"){
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
	
	// 加载树节点
	var initNodes = function(searchNm){
		if(treeObj == null 
				|| typeof treeObj == "undefined"){
			return ;
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/getRptTree",
			dataType : 'json',
			type : "post",
			data : {
				searchNm:searchNm
			},
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					// 移除旧节点
					treeObj.removeChildNodes(treeObj.getNodeByParam("id", '0', null));
					treeObj.removeNode(treeObj.getNodeByParam("id", '0', null),false);
					// 渲染新节点
					treeObj.addNodes(treeObj.getNodeByParam("id", '0', null) , result , true);
					//modify by weijx 报表只展开一层
					//treeObj.expandAll(true);
					treeObj.expandNode(treeObj.getNodeByParam("id", "0", null),true,false,false);
					//modify end
					if(clickTreeId != null
							&& clickTreeId != ""
							&& typeof clickTreeId != "undefined"){
						treeObj.selectNode(treeObj.getNodeByParam("id" , clickTreeId , null) , false);
					}
				}
			},
			error:function(){
				BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}
		
	// 初始化列表查询项
	var initSearch = function(){
		$("#search").ligerForm({
			fields : [ {
				display : "报表名称",
				name : "rptNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "rptNm",
					op : "like"
				}
			}, {
				display : "模板类型",
				name : "templateType",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					field : "templateType",
					op : "="
				}
			} ]
		});
	}
		
	
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">Excel报表</span>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>