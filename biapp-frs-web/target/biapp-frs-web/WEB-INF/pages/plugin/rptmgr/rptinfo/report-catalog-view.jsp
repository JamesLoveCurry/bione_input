<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template7.jsp">
<script type="text/javascript">
	var items, currentNode,grid;
	var baseInfo;
	$(function(){
		var $centerDom = $(document);
		framCenter = $centerDom.height();
		initTree();
		initTab();
	});
	
	function initTree() {
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
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			$("#template.left.center").hide();
			loadTree("${ctx}/rpt/frame/rptmgr/info/getRptCatalog" ,leftTreeObj);
	}
	//树的点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
			if (currentNode.params.nodeType == "02") {//判断	是否是报表节点
				baseInfo.grid.set("url","${ctx}/rpt/frame/rptmgr/info/rptList.json?state=catalog&catalogId="+currentNode.params.realId);
			}
			if (currentNode.params.nodeType == "01") {//判断	是否是报表节点
				baseInfo.grid.set("url","${ctx}/rpt/frame/rptmgr/info/rptList.json?state=catalog");
			}
		}
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
					component.expandAll(true);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
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
		content = "<iframe frameborder='0' id='baseframe' name='baseframe' style='height:100%;width:100%;' src='${ctx}/rpt/frame/rptmgr/info/baseInfo?state=catalog&rptNm=${rptNm}'></iframe>";
		$("#base").html(content);
	}
	function addTabInfo(rptId,rptNm){
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
			content = "<iframe frameborder='0' id='"
					+ rptId
					+ "' name='"
					+rptId
					+ "' style='height:100%;width:100%;' src='${ctx}/rpt/frame/rptmgr/info/reportInfo?rptId="
					+ rptId + "&show=1'></iframe>";
			$("#" + rptId).html(content);
		}
	}

</script>

<title></title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">报表目录信息</span>
	</div>
</body>
</html>