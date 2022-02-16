<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var currentNode,currentTree;//当前点击节点
	$(function(){
		initTree();
		initBtn();
		initSearchField();
		initCascadeBtn();
	});
	
	function initSearchField() {
		$("#treeContainer").parent().prepend('<div style="position: relative;"><input id="searchInput" type="text" placeholder="请输入机构编号或名称" style="width: 99%;" /><img id="searchIcon" style="position: absolute;right: 0;top: 0;cursor: pointer;" src="${ctx}/images/classics/icons/find.png"></div>');
		$("#searchIcon").click(function(){
			initTree($("#searchInput").val());
		});
	}
	
	//初始化数
	function initTree(searchNm) {
		setting ={
			check : {
				enable : true,
				chkStyle : "checkbox",
				chkboxType : {
					"Y" : "s",
					"N" : "s"
				}
			},
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
				//onCheck : zTreeOnCheckSync,
				//onClick : zTreeOnClick,
				onNodeCreated : function(node){
					
				}
			}
		};
		currentTree = $.fn.zTree.init($("#tree"),setting,[]);
		BIONE.loadTree("${ctx}/report/frame/validgroup/loadOrgTree.json", currentTree, 
				{searchNm : searchNm}, function(childNodes){
			if(childNodes){
				var checkedOrgs = window.parent.historyOrgs;
				for(var i = 0;i<childNodes.length;i++){
					if(checkedOrgs.indexOf(childNodes[i].id) != -1) {//反显
						childNodes[i].checked = true;
					}
					if(searchNm) {
						childNodes[i].open = true;
					}
				}
			}
			return childNodes;
		},false);
		currentTree.expandNode(currentTree.getNodeByParam("id","0"));
	}
	
	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("selectOrg");
			}
		}, {
			text : "选择",
			onclick : function() {
				var checkedNodes = currentTree.getCheckedNodes(true);
				if(checkedNodes.length > 0) {
					var id = "";
					var text = "";
					for(var i in checkedNodes) {
						id += ";" + checkedNodes[i].id;
						text += ";" + checkedNodes[i].text;
					}
					if(id && text){
						window.parent.selectTab.$.ligerui.get("validOrgs")._changeValue(id.substring(1), text.substring(1));
						window.parent.historyOrgs = id.substring(1);
					}else {
						window.parent.historyOrgs = "";
					}
				}
				BIONE.closeDialog("selectOrg");
			}
		});
		BIONE.addFormButtons(btns);
	}
	
	function initCascadeBtn() {
		$("#bottom").css("position","relative");
		$("#bottom").prepend('<div id="cascade" style="position:absolute;left:10px;top:2px;"><a class="l-checkbox l-checkbox-checked"><a/><span>级联</span></div>');
		$("#cascade").find("a").click(function(){
			var cascade = $("#cascade").find("a");
			var chkboxType = {};
			if(cascade.hasClass('l-checkbox-checked')){
				cascade.removeClass('l-checkbox-checked');
				chkboxType = {
					"Y": "",
					"N": ""
				}
			}else{
				cascade.addClass('l-checkbox-checked');
				chkboxType = {
					"Y": "s",
					"N": "s"
				}
			}
			currentTree.setting.check.chkboxType = chkboxType;
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; height: 98%">
			<div id="treeContainer"
				style="width: 100%; height: 95%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>