<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template22_BS.jsp">
<script type="text/javascript">
	var leftTreeObj, items, currentNode, grid, tabObj;

	$(function() {
		initTree();
		var $centerDom = $(window.parent.document);
		$("#treeSearchIcon").live(
				'click',
				function() {
					var searchNm = $("#treeSearchInput").val();
					loadTree("${ctx}/report/frame/valid/logic/getRptAndTemplateTree?searchNm=" + searchNm, leftTreeObj);
				});
		$('#treeSearchInput').bind(
				'keydown',
				function(event) {
					if (event.keyCode == 13) {
						var searchNm = $("#treeSearchInput").val();
						loadTree("${ctx}/report/frame/valid/logic/getRptAndTemplateTree?searchNm=" + searchNm, leftTreeObj);
					}
				});
		$('#treeContainer')
				.css('position', 'relative')
				.height($centerDom.height() - 175)
				.prepend("<div id='tree_loading' class='l-tab-loading' style='display:none;'></div>");
		initTab();
	});
	initTab = function() {
		tabObj = $("#tab").ligerTab({
			changeHeightOnResize : false,
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				
				if (tabId == 'warn') {
					$("#warn").attr({
						src : "${ctx}/report/frame/valid/warn"
					});
				}
				if (tabId == 'orgMerge') {
					$("#orgMerge").attr({
						src : "${ctx}/report/frame/valid/orgmerge"
					});
				}
				if (tabId == 'detail_logic') {
					$("#detail_logic").attr({
						src : "${ctx}/rpt/frs/rptDetailValid/detailTab"
					});
				}
			}
		});
		var height = $("#tab").height() - 28;
		tabObj.addTabItem({
					tabid : 'logic',
					text : '逻辑校验',
					showClose : false,
					content : "<iframe src='' id='logic' name='logic' style='height: " + height + "px;' frameborder='0'></iframe>"
				});
		tabObj.addTabItem({
					tabid : 'warn',
					text : '预警校验',
					showClose : false,
					content : "<iframe src='' id='warn' name='warn' style='height: " + height + "px;' frameborder='0'></iframe>"
				});
		$("#logic").attr({
			src : "${ctx}/report/frame/valid/logic/tab"
		});
		tabObj.selectTabItem('logic');
	};

	loadTab = function(nodeType, busiType) {
		if ($("#logic").attr('src') != '${ctx}/report/frame/valid/logic/tab') {
			$("#logic").attr({
				src : "${ctx}/report/frame/valid/logic/tab"
			});
		} else {
			frames['logic'].reloadGrid();
		}
		//明细类报表添加明细校验配置tab页
		if(nodeType == 'detail_report'){
			//202002 lcy 增加明细类报表逻辑校验配置
			var detailTabUrl = '${ctx}/rpt/frs/rptDetailValid/detailTab';
			if ($("#detail_logic").attr('src') != detailTabUrl) {
				var height = $("#tab").height() - 28;
				tabObj.addTabItem({
					tabid : 'detail_logic',
					text : '明细逻辑校验',
					showClose : false,
					content : "<iframe src='' id='detail_logic' name='detail_logic' style='height: " + height + "px;' frameborder='0'></iframe>"
				});
			}
		}else{
			tabObj.removeTabItem('detail_logic');
		}
		
		//1104报表添加1104总分配置tab页
		if(busiType == '02'){
			var orgMergeTabUrl = '${ctx}/report/frame/valid/orgmerge';
			if ($("#orgMerge").attr('src') != orgMergeTabUrl) {
				var height = $("#tab").height() - 28;
				tabObj.addTabItem({
					tabid : 'orgMerge',
					text : '总分校验',
					showClose : false,
					content : "<iframe src='' id='orgMerge' name='orgMerge' style='height: " + height + "px;' frameborder='0'></iframe>"
				});
			}
		}else{
			tabObj.removeTabItem('orgMerge');
		}
		tabObj.selectTabItem('logic');
	};

	function initTree() {
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
				onClick : zTreeOnClick
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/valid/logic/getRptAndTemplateTree",
				leftTreeObj);
	}
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = component.getNodes();
				var num = nodes.length;
				for (var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}

				if (result != null && result.length > 0) {
					component.addNodes(null, result, false);
					//component.expandAll(true);

				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		if ((treeNode.params.nodeType == "template" || treeNode.params.nodeType == "report" || treeNode.params.nodeType == "detail_report")
				&& treeNode.params.templateId) {
			currentNode = treeNode;
			loadTab(treeNode.params.nodeType, treeNode.params.busiType);
		} else {
			BIONE.tip("请选择报表模板节点");
			return;
		}

	}
</script>

<title>报表信息</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">校验管理</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>