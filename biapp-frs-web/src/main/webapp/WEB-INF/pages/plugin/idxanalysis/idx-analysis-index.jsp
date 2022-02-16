<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template22.css" />
<script type="text/javascript">
	var maintab = null;
	var treetab = null;
	var idxtree = null;
	var rpttree = null;
	var dimtree = null;
	var moduletree = null;
	var rel=null;
	var inf=null;
	var framCenter;
	var state="";
	var mainId="";
	var setting={};
	var searchNm="";
	var rightWidth=0;
	var type = "async";
	var searchObj1 = {exeFunction : "initNodes",searchType : "idx",obj:"searchObj1"};//默认执行initNodes方法
	var searchObj2 = {exeFunction : "loadRptTree",searchType : "rpt",obj:"searchObj2"};//默认执行initNodes方法
	var idxlabelFlag = false;
	var rptlabelFlag = false;
	function templateshow() {
		// var $content = $(document);
        var $content = $(window.parent.document);
		$("#right").height($content.height() - 50);
		$("#left").height($content.height() - 50);
		$("#tab1").height($content.height() - 50);
		$("#tab").height($content.height() - 50);
		var leftHeight = $("#left").height();
		var $idxtreeContainer = $("#idxtreeContainer");
		var $rpttreeContainer = $("#rpttreeContainer");
		var $dimtreeContainer = $("#dimtreeContainer");
		var $modeltreeContainer = $("#modeltreeContainer");
		framCenter = $content.height() - 26;
		$idxtreeContainer.height(framCenter - $("#idxtable").height() - $("#idxtreeSearchbar").height() - $("#idxtreeToolbar").height()-25);
		$rpttreeContainer.height(framCenter - $("#rpttable").height() - $("#rpttreeSearchbar").height() - $("#rpttreeToolbar").height()-25);
		$dimtreeContainer.height(framCenter - $("#dimtable").height() - $("#dimtreeSearchbar").height() - $("#dimtreeToolbar").height()-5);
		$modeltreeContainer.height(framCenter - $("#modeltable").height() - $("#modeltreeSearchbar").height()-$("#modeltreeToolbar").height()-5);
		
		 $("#center").ligerLayout({
			 leftWidth: 240,
			 onEndResize: function(){
				 if($("#right").width()>rightWidth)
					 $("#rightDiv").width($("#right").width());
				 else{
					 $("#rightDiv").width(rightWidth);
				 }
			 }
	     });
		 $(".l-layout-header").hide();
		 rightWidth=$content.width()-$("#left").width()-15;
		$("#rightDiv").width(rightWidth);
	}
	$(function() {
		initTab();
		initTree();
		templateshow();
		//添加高级搜索按钮
		//添加高级搜索弹框
		$("#idxhighSearchIcon").click(function(){
			if(idxlabelFlag){
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/report/frame/idx/labelhighSearch?type=idx");
			}
			else{
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/report/frame/idx/highSearch?searchObj="+JSON2.stringify(searchObj1));
			}
		});
		
		$("#rpthighSearchIcon").click(function(){
			if(rptlabelFlag){
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/report/frame/idx/labelhighSearch?type=rpt");
			}
			else{
				BIONE.commonOpenDialog("高级搜索","highSearch","600","250","${ctx}/report/frame/idx/highSearch?searchObj="+JSON2.stringify(searchObj2));
			}
			
		});
	});
	function initTree() {
		initIdxTree();
		initRptTree();
		initDimTree();
		initModelTree();
	}
	function initTab() {
		initTreeTab();
		initMainTab();
	}
	
	function initLabelIdx(ids,stype){
		type = "lsync";
		var _url = "${ctx}/report/frame/idx/getSyncLabelFilter.json";
		var data = {'ids':ids ,'type':stype,'isShowDim':'0', 'isAuth':'1','isShowMeasure':'0'};
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
					onClick : idxzTreeOnClick
				}
		};
		idxtree = $.fn.zTree.init($("#idxtree"),setting,[]);
		BIONE.loadTree(_url,idxtree,data,function(childNodes){
			if(childNodes){
				for(var i = 0;i<childNodes.length;i++){
					childNodes[i].nodeType = childNodes[i].params.nodeType;
					childNodes[i].indexVerId = childNodes[i].params.indexVerId;
				}
			}
			return childNodes;
		},false);
	}
	
	function initIdxLabelTree(){
		setting ={
				async:{
					enable:true,
					type:"post",
					url:"${ctx}/report/frame/idx/getAsyncLabelTree.json",
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam:{'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""},
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
						name:"text",
						title : "title"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : idxzTreeOnClick
				}
		};
	
		idxtree = $.fn.zTree.init($("#idxtree"), setting);
		$("#template.left.center").hide();

		$("#idxtreeSearchIcon").unbind("click");
		$("#idxtreeSearchInput").unbind("keydown");
		
		$("#idxtreeSearchIcon").live(
				'click',function(){
			initLabelNodes($("#idxtreeSearchInput").val());
		});
		
		$("#idxtreeSearchInput").live('keydown',function(){
			if (event.keyCode == 13) {
				initLabelNodes($("#idxtreeSearchInput").val());
			}
		});
	}
	function initIdxTree() {
		//树
		setting ={
				async:{
					enable:true,
					type:"post",
					url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
					autoParam:["nodeType", "id", "indexVerId"],
					otherParam:{'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""},
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
						name:"text",
						title : "title"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
					onClick : idxzTreeOnClick
				}
		};
	
		idxtree = $.fn.zTree.init($("#idxtree"), setting);
		$("#template.left.center").hide();

		$("#idxtreeSearchIcon").unbind("click");
		$("#idxtreeSearchInput").unbind("keydown");
		
		$("#idxtreeSearchIcon").live(
				'click',function(){
			initNodes($("#idxtreeSearchInput").val());
		});
		
		$("#idxtreeSearchInput").live('keydown',function(){
			if (event.keyCode == 13) {
				initNodes($("#idxtreeSearchInput").val());
			}
		});
	}
	// 刷新树节点
	function initNodes(searchNm,searchObj){
		if(searchNm == null || searchNm == ""){
			if(type != "async"){
				type = "async";
				setting ={
						async:{
							enable:true,
							type:"post",
							url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
							autoParam:["nodeType", "id", "indexVerId"],
							otherParam:{'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""},
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
							onClick : idxzTreeOnClick
						}
				};
				idxtree = $.fn.zTree.init($("#idxtree"), setting);
			}
		}
		else{
			var _url = "${ctx}/report/frame/idx/getSyncTree";
			var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""};
			if(searchObj != null && searchObj != ""){
				_url = "${ctx}/report/frame/idx/getSyncTreePro";
				data = {'searchObj':JSON2.stringify(searchObj) ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""};
			}
			if(type !="sync"){
				type = "sync";
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
							onClick : idxzTreeOnClick
						}
				};
				idxtree = $.fn.zTree.init($("#idxtree"), setting);
			}
			BIONE.loadTree(_url,idxtree,data,function(childNodes){
				if(childNodes){
					for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					}
				}
				return childNodes;
			});
		}
	}
	
	function initLabelNodes(searchNm,searchObj){
		if(searchNm == null || searchNm == ""){
			if(type != "lasync"){
				type = "lasync";
				setting ={
						async:{
							enable:true,
							type:"post",
							url:"${ctx}/report/frame/idx/getAsyncLabelTree.json",
							autoParam:["nodeType", "id", "indexVerId"],
							otherParam:{'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""},
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
							onClick : idxzTreeOnClick
						}
				};
				idxtree = $.fn.zTree.init($("#idxtree"), setting);
			}
		}
		else{
			var _url = "${ctx}/report/frame/idx/getSyncLabelTree.json";
			var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "1", "showEmptyFolder":""};
			if(type !="lsync"){
				type = "lsync";
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
							onClick : idxzTreeOnClick
						}
				};
				idxtree = $.fn.zTree.init($("#idxtree"), setting);
			}
			BIONE.loadTree(_url,idxtree,data,function(childNodes){
				if(childNodes){
					for(var i = 0;i<childNodes.length;i++){
						childNodes[i].nodeType = childNodes[i].params.nodeType;
						childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					}
				}
				return childNodes;
			});
		}
	}
	
	function loadRptTree(){
		if(!searchObj2.rptNum){
			loadTree("${ctx}/rpt/frame/rptmgr/info/getRptTree", rpttree);
		}else{
			loadTree("${ctx}/rpt/frame/rptmgr/info/getRptTreeByObj?searchObj="
							+ JSON2.stringify(searchObj2), rpttree);
		}
	}
	// 搜索动作
	function searchHandler(){
		searchNm =  $("#idxtreeSearchInput").val();
		initNodes();
	}
	
	function initRptTree() {
		var setting = {
			data : {
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			view : {
				selectedMulti : false
			},

			callback : {
				onClick : rptzTreeOnClick
			}
		};
		rpttree = $.fn.zTree.init($("#rpttree"), setting);
		loadTree("${ctx}/rpt/frame/rptmgr/info/getRptTree", rpttree);
		$("#rpttreeSearchIcon").live(
				'click',
				function() {
					loadTree(
							"${ctx}/rpt/frame/rptmgr/info/getRptTree?searchNm="
									+ $("#rpttreeSearchInput").val(), rpttree);
				});
		$("#rpttreeSearchInput").live(
				'keydown',
				function() {
					if (event.keyCode == 13) {
						loadTree(
								"${ctx}/rpt/frame/rptmgr/info/getRptTree?searchNm="
										+ $("#rpttreeSearchInput").val(), rpttree);
					}
				});
	}
	
	function initRptLabelTree() {
		var setting = {
			data : {
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			view : {
				selectedMulti : false
			},

			callback : {
				onClick : rptzTreeOnClick
			}
		};
		rpttree = $.fn.zTree.init($("#rpttree"), setting);
		loadTree("${ctx}/rpt/rpt/rptplatshow/getLabeltree", rpttree);
		$("#rpttreeSearchIcon").live(
				'click',
				function() {
					loadTree(
							"${ctx}/rpt/rpt/rptplatshow/getLabeltree?searchNm="
									+ $("#rpttreeSearchInput").val(), rpttree);
				});
		$("#rpttreeSearchInput").live(
				'keydown',
				function() {
					if (event.keyCode == 13) {
						loadTree(
								"${ctx}/rpt/rpt/rptplatshow/getLabeltree?searchNm="
										+ $("#rpttreeSearchInput").val(), rpttree);
					}
				});
	}

	function initLabelRpt(ids,type){
		var url = "${ctx}/rpt/rpt/rptplatshow/getLabelFilter";
 		$.ajax({
			cache : false,
			async : true,
			url :url,
			dataType : 'json',
			type : "post",
			data : {
				ids : ids,
				type : type
			},
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					// 移除旧节点
					var nodes = rpttree.getNodes();
					var num = nodes.length;
					for(var i=0;i<num;i++){
						rpttree.removeNode(nodes[0],false);
					}
					// 渲染新节点
					rpttree.addNodes(rpttree.getNodeByParam("id", '0', null) , result , true);
					//modify by weijx 报表只展开一层
					rpttree.expandNode(rpttree.getNodeByParam("id", "0", null),true,false,false);
				}
			}
		});
	}
	
	function initDimTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				onClick : dimzTreeOnClick
			}
		};
		dimtree = $.fn.zTree.init($("#dimtree"), setting);
		//加载数据
		loadTree("${ctx}/rpt/frame/dimCatalog/listcatalogs.json", dimtree);
		$("#dimtreeSearchIcon").live(
				'click',
				function() {
					loadTree(
							"${ctx}/rpt/frame/dimCatalog/listcatalogs.json?searchText="
									+ $("#dimtreeSearchInput").val(), dimtree);

				});
		$("#dimtreeSearchInput").live(
				'keydown',
				function() {
					if (event.keyCode == 13) {
						loadTree(
								"${ctx}/rpt/frame/dimCatalog/listcatalogs.json?searchText="
										+ $("#dimtreeSearchInput").val(), dimtree);
					}
				});
	}
	
	function initModelTree() {
		var setting = {
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			callback : {
				onClick : modelzTreeOnClick
			}
		};
		modeltree = $.fn.zTree.init($("#modeltree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/design/cfg/getRelModuleTree", modeltree);
		$("#modeltreeSearchIcon").live(
				'click',
				function() {
					loadTree(
							"${ctx}/report/frame/design/cfg/getRelModuleTree?searchNm="
									+ $("#dimtreeSearchInput").val(), modeltree);

				});
		$("#modeltreeSearchInput").live(
				'keydown',
				function() {
					if (event.keyCode == 13) {
						loadTree(
								"${ctx}/report/frame/design/cfg/getRelModuleTree?searchNm="
										+ $("#dimtreeSearchInput").val(), modeltree);
					}
				});
	}
	function idxzTreeOnClick(event, treeId, treeNode) {
		if(treeNode.params.nodeType=="idxInfo"){
			window.top.analysisIndex = window;
			mainId=treeNode.id;
			maintab.addTabItem({
				tabid: mainId,
				text: "指标【"+treeNode.text+"】追溯分析",
				url: "${ctx}/rpt/frame/idx/idxanalysis/tab?id="+mainId+"&type=idx&d="+new Date().getTime(),
				showClose: true
			});
		}
		else{
			BIONE.tip("请选择指标节点");
		}
	};
	function rptzTreeOnClick(event, treeId, treeNode) {
		if(treeNode.params.nodeType=="03"){
			window.top.analysisIndex = window;
			mainId=treeNode.params.realId;
			maintab.addTabItem({
				tabid: mainId,
				text: "报表【"+treeNode.text+"】追溯分析",
				url: "${ctx}/rpt/frame/idx/idxanalysis/tab?id="+mainId+"&type=rpt&d="+new Date().getTime(),
				showClose: true
			});
		}
		else{
			BIONE.tip("请选择报表节点");
		}
	};
	
	function dimzTreeOnClick(event, treeId, treeNode) {
		if(treeNode.params.type=="dimTypeInfo"){
			window.top.analysisIndex = window;
			mainId=treeNode.id;
			maintab.addTabItem({
				tabid: mainId,
				text: "维度【"+treeNode.text+"】追溯分析",
				url: "${ctx}/rpt/frame/idx/idxanalysis/tab?id="+mainId+"&type=dim&d="+new Date().getTime(),
				showClose: true
			});
		}
		else{
			BIONE.tip("请选择维度节点");
		}
	};
	
	function modelzTreeOnClick(event, treeId, treeNode) {
		if(treeNode.params.type=="02"){
			window.top.analysisIndex = window;
			mainId=treeNode.id;
			maintab.addTabItem({
				tabid: mainId,
				text: "模型【"+treeNode.text+"】追溯分析",
				url: "${ctx}/rpt/frame/idx/idxanalysis/tab?id="+mainId+"&type=model&d="+new Date().getTime(),
				showClose: true
			});
		}
		else{
			BIONE.tip("请选择数据模型");
		}
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
					//component.expandAll(true);
				}
			},
			error : function(result, b) {
			}
		});
	}
	
	function addTabInfo(id, text) {
		maintab.addTabItem({
			tabid : id,
			text : text,
			showClose : false,
			content : "<div id='" + id + "' style='height:" + framCenter
					+ "px;width:100%;'></div>"
		});
		content = "<iframe frameborder='0' id='"
				+ id
				+ "frame' name='"
				+ id
				+ "frame' style='height:100%;width:100%;' src=''></iframe>";
		$("#" + id).html(content);
	}
	function initTreeTab() {
		treeTab = $("#tab1").ligerTab({
			contextmenu : false
		});
	}
	function initMainTab() {
		maintab = $("#tab").ligerTab({
			contextmenu : true,
			height: $('#right').height() - 10,
		});
	}
	
	function idxshowtype() {
		$("#idxtreeSearchInput").val("");
		if($("#idxcatalog")[0].checked == true){
			type = "async";
			initIdxTree();
			idxlabelFlag = false;
		}
		else{
			type = "lasync";
			initIdxLabelTree();
			idxlabelFlag = true;
		}
	}
	
	function rptshowtype() {
		$("#rpttreeSearchInput").val("");
		if($("#rptcatalog")[0].checked == true){
			type = "async";
			initRptTree();
			rptlabelFlag = false;
		}
		else{
			type = "lasync";
			initRptLabelTree();
			rptlabelFlag = true;
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="left" position="left" style="background-color: #FFFFFF">
			<div id="tab1" style="width: 100%; overflow: hidden;">
				<div tabid="idx" title="指标" lselected="true">
					<div id="idxtable" width="100%" border="0">
						<div width="100%"
							 style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
							<div width="8%" style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
								<i class = "icon-guide search-size"></i>
							</div>
							<div width="90%">
								<span style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">指标信息</span>
							</div>
						</div>
					</div>
					<div id="idxtreeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
					<div id="idxtreeSearchbar" style="width: 99%; margin-top: 2px; padding-left: 2px;">
						<ul>
							<li style="width: 98%; text-align: left;">
								<div class="l-text-wrapper" style="width: 100%;">
									<div class="l-text l-text-date" style="width: 100%;">
										<input id="idxtreeSearchInput" type="text" class="l-text-field"
											style="width: 100%;bottom: 0px; padding-left: 0px;border-top-width: 1px;"/>
										<div class="l-trigger" style="right:26px;border-top-width: 1px; margin-top: 1px;border-left-width: 1px;">
											<div id="idxtreeSearchIcon" class = "icon-search font-size"></div>
										</div>
										<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:4px;"></i>
										<div title="高级筛选" class="l-trigger" style="right:0px;padding-top: 1px;padding-right: 1px;">
											<div id="idxhighSearchIcon" class = "icon-light_off font-size"></div>
										</div>
									</div>
								</div>
							</li>
						</ul>
					</div>
					<div style="background-image: url(/biapp-frs-web/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);
						 border: 1px solid #D6D6D6; padding-left: 10px; padding-top: 3px; height: 20px;">
						<span>目录</span><input type="radio" id="idxcatalog" name="idxshowtype"
							value="idxcatalog" style="width: 20px;" onclick="idxshowtype()" checked="true" />
						<span>标签</span> <input type="radio" id="idxlabel" name="idxshowtype"
							value="idxlabel" style="width: 20px;" onclick="idxshowtype()" />
					</div>
					<div id="idxtreeContainer" style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="idxtree" style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></ul>
					</div>
				</div>
				
				<div tabid="rpt" title="报表">
					<div id="rpttable" width="100%" border="0">
						<div width="100%"
							style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
							<div width="8%" style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
								<i class = "icon-guide search-size"></i>
							</div>
							<div width="90%">
								<span style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">报表信息</span>
							</div>
						</div>
					</div>
					<div id="rpttreeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
					<div id="rpttreeSearchbar" style="width: 99%; margin-top: 2px; padding-left: 2px;">
						<ul>
							<li style="width: 98%; text-align: left;">
								<div class="l-text-wrapper" style="width: 100%;">
									<div class="l-text l-text-date" style="width: 100%;">
										<input id="rpttreeSearchInput" type="text" class="l-text-field"
											style="width: 100%;bottom: 0px; padding-left: 0px;border-top-width: 1px;"/>
										<div class="l-trigger" style="right:26px;border-top-width: 1px; margin-top: 1px;border-left-width: 1px;">
											<div id="rpttreeSearchIcon" class = "icon-search font-size"></div>
										</div>
										<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:4px;"></i>
										<div title="高级筛选" class="l-trigger" style="right:0px;padding-top: 1px;padding-right: 1px;">
											<div id="rpthighSearchIcon" class = "icon-light_off font-size">
											</div>
										</div>
									</div>
								</div>
							</li>
						</ul>
					</div>
					<div style="background-image: url(/biapp-frs-web/css/classics/ligerUI/Gray/images/ui/gridbar.jpg); border: 1px solid #D6D6D6; padding-left: 10px; padding-top: 3px; height: 20px;">
						<span>目录</span><input type="radio" id="rptcatalog" name="rptshowtype"
							value="rptcatalog" style="width: 20px;" onclick="rptshowtype()" checked="true" />
						<span>标签</span> <input type="radio" id="rptlabel" name="rptshowtype"
							value="rptlabel" style="width: 20px;" onclick="rptshowtype()" />
					</div>
					<div id="rpttreeContainer" style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="rpttree" style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></ul>
					</div>
				</div>
				
				<div tabid="dim" title="维度">
					<div id="dimtable" width="100%" border="0">
						<div width="100%"
							style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
							<div width="8%" style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
								<i class = "icon-guide search-size"></i>
							</div>
							<div width="90%">
								<span style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">维度信息</span>
							</div>
						</div>
					</div>
					<div id="dimtreeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
					<div id="dimtreeSearchbar" style="width: 99%; margin-top: 2px; padding-left: 2px;">
						<ul>
							<li style="width: 98%; text-align: left;">
								<div class="l-text-wrapper" style="width: 100%;">
									<div class="l-text l-text-date" style="width: 100%;">
										<input id="dimtreeSearchInput" type="text"
											class="l-text-field" style="width: 100%;bottom: 0px; padding-left: 0px;border-top-width: 1px;" />
										<div class="l-trigger">
											<div id="dimtreeSearchIcon" class = "icon-search font-size"></div>
										</div>
									</div>
								</div>
							</li>
						</ul>
					</div>
					<div id="dimtreeContainer" style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="dimtree" style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></ul>
					</div>
				</div>
				
				<div tabid="model" title="模型">
					<div id="modeltable" width="100%" border="0">
						<div width="100%"
							style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
							<div width="8%" style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
								<i class = "icon-guide search-size"></i>
							</div>
							<div width="90%">
								<span style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">模型信息</span>
							</div>
						</div>
					</div>
					<div id="modeltreeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
					<div id="modeltreeSearchbar" style="width: 99%; margin-top: 2px; padding-left: 2px;">
						<ul>
							<li style="width: 98%; text-align: left;">
								<div class="l-text-wrapper" style="width: 100%;">
									<div class="l-text l-text-date" style="width: 100%;">
										<input id="modeltreeSearchInput" type="text" class="l-text-field"
										style="width: 100%;bottom: 0px; padding-left: 0px;border-top-width: 1px;" />
										<div class="l-trigger">
											<div id="modeltreeSearchIcon" class = "icon-search font-size"></div>
										</div>
									</div>
								</div>
							</li>
						</ul>
					</div>
					<div id="modeltreeContainer" style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="modeltree" style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></ul>
					</div>
				</div>
			</div>
		</div>
		<div id="right" position="center" style="overflow-x: auto;overflow-y: hidden;">
			<div id="rightDiv" style="">
				<div id="tab" style="width: 100%; overflow: hidden;"></div>
			</div> 
		</div>
	</div>
</body>
</html>