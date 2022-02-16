<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style>
.checklabel {
	color: blue;
}
</style>
<script type="text/javascript" src="${ctx}/js/report/frame/dim/dimFormula.js"></script>
<script type="text/javascript">
	var leftTreeObj=null;
	var dimTypeStruct="${dimTypeStruct}";
	
	var dimInfo = '${dimInfo}';
	var filterInfo = '${filterInfo}';
	var gridId = '${gridId}';
	
	$(function() {
		initTree();
		$("#treeContainer").height($("#center").height() - 10);
	})

	
	function initTree() {
		//树
		var setting = {
			check : false,
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
				onClick: zTreeOnClick
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		var url = "${ctx}/rpt/input/idxdatainput/getDimInfoTree?dimInfo="+dimInfo+"&filterInfo="+filterInfo;
		loadTree(url,leftTreeObj);

	}
	
	function zTreeOnClick(event,treeId,treeData){
		var selectedDimInfo = {dimNo:treeData.id,dimText:treeData.text};
		BIONE.closeDialog("selectDimBox",null,true,
				selectedDimInfo);
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
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="left" style="background-color: #FFFFFF">
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>