<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<script type="text/javascript">
$(function() {
	initTool();
	initTree();
});

function initTool() {
	//添加树div
	$('#tab').after(
			'<div id="treeContainer" style="width: 100%; height: 100% ; clear: both; background-color: #FFFFFF;">'
			            + '<ul id="tree" style="font-size: 12; background-color: #FFFFFF; width: 100%; height: 100%;" class="ztree"></ul>'
		                + '</div>');
}

//初始化树
function initTree() {
	var setting = {
		view : {
			nameIsHTML : true,
			showTitle : false
		},
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
		edit : {
			drag : {
				isCopy : false,
				isMove : false
			},
			enable : true,
			showRemoveBtn : false,
			showRenameBtn : false
		},
		callback : {
			onClick : treeonClick
		}
	}

	//右树对象
	TreeObj = $.fn.zTree.init($("#tree"), setting);

	//加载数据
	loadTree("${ctx}/cabin/analysis/theme/getTheTree", TreeObj);
}

//树点击事件
function treeonClick(event, treeId, treeNode) {
	var currentNode = treeNode;
	var main = parent.selectTab;
	var c = main.$.ligerui.get("themeId");
	c._changeValue( treeNode.id,treeNode.text);
	BIONE.closeDialog("themeTree");
}

//加载树中数据
function loadTree(url, component) {
	$.ajax({
		cache : false,
		async : true,
		url : url,
		dataType : 'json',
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
			if (result.length > 0) {
				component.addNodes(null, result, false);
				component.expandAll(false);
			}
		},
		error : function(result, b) {
			BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
		}
	});
}
</script>
<title>面板树</title>
</head>
<body>
</body>
</html>