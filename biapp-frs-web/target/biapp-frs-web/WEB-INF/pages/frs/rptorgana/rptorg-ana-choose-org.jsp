<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template9.jsp">
<script type="text/javascript">
var idxBelongType = window.parent.busiType;
$(function() {
	initTool();
	initTree();
});

function initTool() {
	//添加树div
	$('#tab').after(
			'<div id="treeContainer" style="width: 100%; height: 100% ; clear: both; background-color: #FFFFFF;overflow:auto;">'
			            + '<ul id="tree" style="font-size: 12; background-color: #FFFFFF; width: 97%; height: 96%;" class="ztree"></ul>'
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
	loadTree("${ctx}/idx/analysis/show/initOrgTreeData", TreeObj);
}

//树点击事件
function treeonClick(event, treeId, treeNode) {
	var currentNode = treeNode;
	var c = parent.$.ligerui.get("queryOrg");
	c.setValue(treeNode.id, treeNode.text);
	parent.initOrgCols();
	BIONE.closeDialog("chooseOrg");
}

//加载树中数据
function loadTree(url, component) {
	$.ajax({
		cache : false,
		async : true,
		url : url,
		dataType : 'json',
		type : "post",
		data : {
			idxBelongType : idxBelongType
		},
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
				component.expandNode(component.getNodeByParam("tId", "tree_1", null),true,false,false);
			}
		},
		error : function(result, b) {
			BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
		}
	});
}
</script>
<title>机构树</title>
</head>
<body>
</body>
</html>