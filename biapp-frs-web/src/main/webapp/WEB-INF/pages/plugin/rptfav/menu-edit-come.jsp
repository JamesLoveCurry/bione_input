<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	var tempGrid = null;
	var folderId = "${folderId}";
	
	$(function() {

		//$("#treeContainer").height($("#center").height() - 2);
		$("#tree").height() -20;
		
		initTree();
		refreshTree();
		
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("chooseMenu");
			}
		}, {
			text : "保存",
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
		jQuery.metadata.setType("attr","validate");
		BIONE.validate($("#mainform"));
		//保存机构树
	});
	
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
		
	//树的点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		currentNode = treeNode;
	}
}

	
	function refreshTree() {  //刷新树
		if (leftTreeObj) {
			loadTree("${ctx}/rpt/frame/index/update/getInfo?folderId="+folderId, leftTreeObj);
		}
	}
		
		function f_save(){
			if(currentNode){
				if(currentNode.id == "0"){
					BIONE.tip("不能修改到根目录");
					return;
				}else{
				window.parent.liger.get("choose").selectValue(currentNode.id);
				window.parent.liger.get("choose").setText(currentNode.text);
				BIONE.closeDialog("chooseMenu");
				}
			}
			else{
				BIONE.tip("请选择机构！");
			}
		}
		
		
	</script>
</head>
	<body>
	<div id="template.center">
			<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>