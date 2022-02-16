<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
   
	var leftTreeObj ;
	$(function() {
		initTree();
		
		var btns = [{
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("chooseOrg");
			}
		},{
			text : '确定',
			onclick : f_save
		}];
		BIONE.addFormButtons(btns);
		$("#treeContainer").height($("#center").height() - $("#bottom").height() - 30);
	});
	function f_save(){
		var nodes = leftTreeObj.getCheckedNodes(true);
		if(nodes && nodes.length > 0){
			var checkIds = [];
			var texts = [];
			for(var i=0;i<nodes.length;i++){
				if(nodes[i].id != "${treeRoot}"){
					checkIds.push(nodes[i].id);	
					texts.push(nodes[i].text);
				}
			}
			window.parent.dimFilters["${dimTypeNo}"] = {checkIds : checkIds, selectedNodes : createTreeNode(nodes)};
			if(window.parent.$.ligerui.get('queryCurrency')){
				if(texts){
					window.parent.$.ligerui.get('queryCurrency').setText(texts.join(","));
				}
			}
			BIONE.closeDialog("chooseOrg");
		}else{
			window.parent.dimFilters["${dimTypeNo}"] = null;
			if(window.parent.$.ligerui.get('queryCurrency')){
				window.parent.$.ligerui.get('queryCurrency').setText("");
			}
			BIONE.closeDialog("chooseOrg");
		}
	}
	function createHtml(result){
		var html = "<ul>";
		if(result && result.length){
			for(var i=0;i<result.length;i++){
				html += "<li>"+result[i].text+"</li>";
				if(result[i].children && result[i].children.length > 0){
					html += createHtml(result[i].children);
				}
			}
		}
		html += "</ul>";
		return html;
	}
	function createTreeNode(nodes){
		var result = [];
		var map = [];
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].id != "${treeRoot}"){
				map[nodes[i].id] = nodes[i];
				nodes[i].children = [];
			}
		}
		var parent = null;
		for (var i=0;i<nodes.length;i++) {
			if(nodes[i].id != "${treeRoot}"){
				parent = map[nodes[i].upId];
				if (parent == null) {
					result.push(nodes[i]);
				} else {
					parent.children.push(nodes[i]);
				}
			}
		}
		return result;
	}
	function initTree() {
		//树
		
		var setting = {
			check : {
				enable : true,
				chkStyle : "checkbox",
				chkboxType: { "Y": "", "N": "" }
			},
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
				onCheck: zTreeOnCheck
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		loadTree(
				"${ctx}/report/frame/idx/getDimInfoTree?dimTypeNo=${dimTypeNo}",
				leftTreeObj);

	}
	
	function zTreeOnCheck(event, treeId, treeNode){
		if(treeNode.id == "${treeRoot}"){
			leftTreeObj.checkAllNodes(treeNode.checked);
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
				
				if(window.parent.dimFilters["${dimTypeNo}"]){
					for(var i=0;i<window.parent.dimFilters["${dimTypeNo}"].checkIds.length;i++){
						component.checkNode(component.getNodeByParam("id", window.parent.dimFilters["${dimTypeNo}"].checkIds[i]), true, false, false);
					}
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
</script>
</head>
<body>
	<div id="template.center">
	<div>
		<div style="float: left;width: 15%;margin-left: 7px;"></div></div>
		<div id="left" style="background-color: #FFFFFF">
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="bottom">
		<div class="form-bar">
			<div id="bottom" style="padding-top: 5px"></div>
		</div>
	</div>
	</div>
	
</body>
</html>