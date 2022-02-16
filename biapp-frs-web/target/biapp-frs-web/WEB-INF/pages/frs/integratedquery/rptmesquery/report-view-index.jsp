<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22_BS.jsp">
<head>
<script type="text/javascript">
	var leftTreeObj;
	var dialog;
	var currentNode = null;//选中树节点
	var catalogBox = null;
	var rptNm="${rptNm}";
	var selectedTab = null;
	$(function() {
		// var $centerDom = $(document);
        var $centerDom = $(window.document);
		framCenter = $centerDom.height() - 26;
		initTree();
		initTab();
		var leftHeight = $("#left").height();
		$("#treeContainer").height(leftHeight - 28 - $("#treeSearchbar").height() - 2);
		
	});
	function initTree() {
		//树
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
				onClick : zTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.id == "ROOT") {
						//若是根节点，展开下一级节点
						leftTreeOb.expand(treeNode, true, true, true);
					}
				}
			}
		};
		var t = window.location.href;
		var flag = t.substr(t.length-2,2)
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		$("#template.left.center").hide();
		//${ctx}/rpt/frame/rptmgr/info/getRptTree
		loadTree("${ctx}/frs/integratedquery/rptmesquery/info/getRptTree",leftTreeObj);
		$("#treeSearchIcon").live(
				'click',
				function() {
					var searchNm = $("#treeSearchInput").val();
					loadTree("${ctx}/frs/integratedquery/rptmesquery/info/getRptTree" ,leftTreeObj,{searchNm:searchNm});
				});
	}
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
			if (currentNode.params.nodeType == "03") {//判断	是否是报表节点
				if(currentNode.params.isPri=="Y")
					addTabInfo(currentNode.params.realId,currentNode.text,currentNode.params.rptType);
				else
					BIONE.tip("该用户无权限查看此报表");
			
			}
			if (currentNode.params.nodeType == "02") {//判断	是否是报表节点
					baseInfo.grid.set("url","${ctx}/frs/integratedquery/rptmesquery/info/rptList?state=catalog&catalogId="+currentNode.params.realId);
					maintab.selectTabItem("base");
			}
			if (currentNode.params.nodeType == "01") {//判断	是否是报表节点
					baseInfo.grid.set("url","${ctx}/frs/integratedquery/rptmesquery/info/rptList?state=catalog");
					maintab.selectTabItem("base");
			}
			
		}
	}
	function initTab(){
		window['maintab'] = $("#tab").ligerTab({
			contextmenu : false
		});
		maintab.addTabItem({
			tabid : "base",
			text : "报表列表",
			showClose : false,
			content : "<div id='base' style='height:" + framCenter
					+ "px;width:100%;'></div>"
		});
		content = "<iframe frameborder='0' id='baseframe' name='baseframe' style='height:100%;width:100%;' src='${ctx}/frs/integratedquery/rptmesquery/info/baseInfo'></iframe>";
		$("#base").html(content);
	}
	function addTabInfo(rptId,rptNm,rptType){
		
		if (maintab.isTabItemExist(rptId)) {
			maintab.selectTabItem(rptId);
		}
		else{
			maintab.addTabItem({
				tabid : rptId,
				text : rptNm,
				showClose : true,
				content : "<div id='" + rptId
						+ "' style='height:" + framCenter
						+ "px;width:100%;'></div>"
			});
			var url="";
			if(rptType=="01")
				url="${ctx}/frs/integratedquery/rptmesquery/info/reportInfo";
			else
				url="${ctx}/frs/integratedquery/rptmesquery/info/reportFrsView";
			content = "<iframe frameborder='0' id='"
					+ rptId
					+ "' name='"
					+rptId
					+ "' style='height:100%;width:100%;' src='"+url+"?rptId="
					+ rptId + "&show=1'></iframe>";
			$("#" + rptId).html(content);
		}
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
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>