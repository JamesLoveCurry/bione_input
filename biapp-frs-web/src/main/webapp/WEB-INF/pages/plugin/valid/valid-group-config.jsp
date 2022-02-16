<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var navtab,saveNodes,checkNodeIds;
	var allTreeNodes = {};
	var onCheckNodes = [];
	var onCheckSyncNodes = [];
	var followCheckNodes = [];
	var isSave = "1";
	var groupId = "${groupId}";
	var grpType = "${grpType}";
	var tree_icon = "${ctx}/images/classics/icons/house.png";
	
	$(function(){
		navtab = window['tab'] = $("#tab").ligerTab();
		//loadAllTreeNodeObj()
		loadAsyncTreeSelectNodeObj();
		initValidTypeTab();
		initBtn();
	});
	
	function initBtn(){
		//添加按钮
		var btns = [{
			text : "取消",
			onclick : f_cancel
		},{
			text : "保存",
			onclick : f_tosave
		}];
		BIONE.addFormButtons(btns);
	}
	
	function loadAllTreeNodeObj(validType, beanName){
	    $.ajax({
		    async : true,
		    cache : false,
		    url : "${ctx}/report/frame/validgroup/findAllTreeNodeObj?groupId="+groupId+"&grpType="+grpType+"&beanName="+beanName+"&validType="+validType,
		    dataType : 'json',
		    type : "get",
		    beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
		    complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
		    },
		    success : function(result) {
		    	if(result && result["allTreeNodes"]){
		    		//allTreeNodes = result["allTreeNodes"];
		    		$.extend(true, allTreeNodes, result["allTreeNodes"]);
		    	}
		    },
		    error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
		    }
		});
	}
	
	function loadAsyncTreeSelectNodeObj(){
	    $.ajax({
		    async : false,
		    cache : false,
		    url : "${ctx}/report/frame/validgroup/findSelectNodeObj?groupId="+groupId+"&grpType="+grpType,
		    dataType : 'json',
		    type : "get",
		    success : function(result) {
		    	if(result){
		    		checkNodeIds = result["checkNodeIds"];
		    		saveNodes = result["saveNodes"];
		    	}
		    },
		    error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
		    }
		});
	}
	
	function f_tosave(){
		if(isSave == "0"){
			BIONE.tip("节点计算中...请稍等...");
			return;
		}
		//保存
	    $.ajax({
		    async : false,
		    cache : false,
		    url : "${ctx}/report/frame/validgroup/saveValidRel?groupId="+groupId+"&grpType="+grpType,
		    dataType : 'json',
		    type : "post",
		    data : {
		    	saveData : saveNodes.join(",")
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
		    	BIONE.closeDialog("configDialog","保存成功！");
		    },
		    error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
		    }
		});
	}
	
	function f_cancel(){
		BIONE.closeDialog("configDialog");
	}
	
	function initValidTypeTab(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/validgroup/getValidTypeTabs.json?d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			success : function(result) {
				var data = null;
				if (result) {
					data = result.data;
				}
				if (data && data.length > 0) {
					for ( var i = 0; i < data.length; i++) {
						var subTreeId = "#vt-" + data[i].objDefNo;
						var appendHtml = "<div style='overflow: auto;'><div id='"+ data[i].objDefNo
							+ "_Container' style='width: 100%; overflow: hidden; clear: both; background-color: #FFFFFF;'><div id='vt-"+ data[i].objDefNo
							+ "' class='ztree' style='font-size: 12; background-color: #FFFFFF; width: 95%''></div></div></div>";
						navtab.addTabItem({
							tabid : data[i].objDefNo,
							content : appendHtml,
							text : data[i].objDefName,
							showClose : false
						});
						$('li[tabid="'+data[i].objDefNo+'"]').data("clicked",false);//设置未点击标识
						$('li[tabid="'+data[i].objDefNo+'"]').data("beanName", data[i].beanName);
						$('li[tabid="'+data[i].objDefNo+'"]').click(function(){
							var index = $(this).attr("tabid");
							var beanName = $(this).data("beanName");
							if(!$(this).data("clicked")){
								loadTree(index, "#vt-" + index, beanName, true);
								$(this).data("clicked", true);
							}
						});
						//加搜索栏
						var searchInput = "searchInput" + data[i].objDefNo;
						var searchIcon = "searchIcon" + data[i].objDefNo;
						$(subTreeId).parent().prepend('<div style="position: relative;"><input id="'+searchInput+'" type="text" placeholder="请输入指标编号或名称" style="width: 100%;" /><img id="'+searchIcon+'" style="position: absolute;right: 0;top: 0;cursor: pointer;" src="${ctx}/images/classics/icons/find.png"></div>');
						$("#" + searchIcon).data("validType",data[i].objDefNo);
						$("#" + searchIcon).data("beanName",data[i].beanName);
						$("#" + searchIcon).data("subTreeId",subTreeId);
						$("#" + searchIcon).click(function() {
							var searchNm = $("#searchInput"+$(this).data("validType")).val();
							if(searchNm){
								loadSyncTree(groupId, grpType, $(this).data("validType"), $(this).data("beanName"), $(this).data("subTreeId"), searchNm);
							}else{
								loadTree($(this).data("validType"), "#vt-" + $(this).data("validType"), $(this).data("beanName"));
							}
						});
					}
					//设置默认选中tab为第一个tab
					navtab.selectTabItem(data[0].objDefNo);
					loadTree(data[0].objDefNo, "#vt-" + data[0].objDefNo, data[0].beanName, true);
					$('li[tabid="'+data[0].objDefNo+'"]').data("clicked",true);
				}
			},
			error : function(result, b) {
				////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function loadTree(index, subTreeId, beanName, loadAllNode){
		if ($(subTreeId)) {//构造资源树
			if(loadAllNode){
				loadAllTreeNodeObj(index, beanName);
			}
			var setting = {
				async:{
					enable : true,
					url : "${ctx}/report/frame/validgroup/getIdxAsyncTree.json",
					autoParam:["id", "indexVerId","params"],
					otherParam:{"beanName" : beanName, groupId : groupId, grpType : grpType, validType : index},
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								var forCheckStr = childNodes[i].params.validType + '@' + childNodes[i].id;
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								if(($.inArray(forCheckStr, checkNodeIds) != -1 || $.inArray(forCheckStr, followCheckNodes) != -1) &&
										(parentNode.checked || parentNode.id == "0")){
									childNodes[i].checked = true;
								}
							}
						}
						return childNodes;
					}
				},
				check : {
					enable : true,
					chkStyle : "checkbox",
					chkboxType : {
						"Y" : "s",
						"N" : "s"
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
					onCheck:zTreeOnCheck,
					beforeClick : function(treeId, treeNode, clickFlag){
						return true;
					}
				}
			};
			window[index + "_tree"] = $.fn.zTree.init($(subTreeId), setting,[{
				id : "0",
				text : "授权校验树",
				title : "授权校验树",
				icon : tree_icon,
				nocheck : true,
				isParent: true
		      } 
		    ]);
			window[index + "_tree"].expandNode(window[index + "_tree"].getNodeByParam("id","0"));
		}
	}
	
	function loadSyncTree(groupId, grpType, validType, beanName, subTreeId, searchNm) {
		var _url = "${ctx}/report/frame/validgroup/loadSyncTree.json";
		var data = {groupId : groupId, grpType : grpType, validType : validType, beanName : beanName, searchNm : searchNm};
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
				onCheck:zTreeOnCheckSync,
				onNodeCreated : function(node){
					if($(subTreeId + "_1_check").length > 0) {
						$(subTreeId + "_1_check").remove();
					}
				}
			}
		};
		window[validType + "_tree"] = $.fn.zTree.init($(subTreeId),setting,[]);
		BIONE.loadTree(_url, window[validType + "_tree"], data, function(childNodes){
			if(childNodes){
				for(var i = 0;i<childNodes.length;i++){
					childNodes[i].open = true;
					if($.inArray(childNodes[i].id, checkNodeIds) != -1) {
						childNodes[i].checked = true;
					}
				}
			}
			return childNodes;
		},false);
	}
	
	function zTreeOnCheckSync(event, treeId, node, clickFlag){
		if(node.checked){//勾选
			onCheckSyncNodes = [];
			findSyncTreeNodeByCheck([node]);
			$.merge(saveNodes, onCheckSyncNodes);
		}else{//去勾选
			onCheckSyncNodes = [];
			findSyncTreeNodeByCheck([node]);
			for(var i = 0;i < saveNodes.length;i++){
				var obj = saveNodes[i];
				if($.inArray(obj,onCheckSyncNodes) != -1){
					saveNodes.splice(i,1);
					i--;
				}
			}
		}
	}
	
	function findSyncTreeNodeByCheck(arrNodes){
 		var midNodes = [];
 		if(arrNodes.length > 0){
 			$.each(arrNodes,function(index,node){
				onCheckSyncNodes.push(node.id);
				if(node.children != null){
					$.merge(midNodes, node.children);
				}
 			});
 		}
		if(midNodes.length > 0){
			findSyncTreeNodeByCheck(midNodes);
		}
	}
	
	function zTreeOnCheck(event, treeId, node, clickFlag){
		isSave = "0";
	 	var obj = node.params.validType + "@" + node.id;
		if(node.checked){//勾选
			onCheckNodes = [];
			onCheckNodes.push(obj);
			if(!node.isParent){
				$.merge(saveNodes, onCheckNodes);
			}else{
				findNodeObjOnAndOffCheck([obj]);
				$.merge(saveNodes, onCheckNodes);
				$.merge(followCheckNodes, onCheckNodes);
			}
			isSave = "1";
		}else{//去勾选
			onCheckNodes = [];
			onCheckNodes.push(obj);
			findNodeObjOnAndOffCheck([obj]);
			for(var i = 0;i < saveNodes.length;i++){
				var obj = saveNodes[i];
				if($.inArray(obj,onCheckNodes) != -1){
					saveNodes.splice(i,1);
					i--;
				}
			}
			
			//删除后续打开节点
			for(var j = 0;j<followCheckNodes.length;j++){
				var obj = followCheckNodes[i];
				if($.inArray(obj,onCheckNodes) != -1){
					followCheckNodes.splice(i,1);
					i--;
				}
			}
			isSave = "1";
		}
	}
	
	function findNodeObjOnAndOffCheck(arrNodes){
 		var midNodes = [];
 		if(arrNodes.length > 0){
 			$.each(allTreeNodes,function(nodeId,upNodeId){
 				if($.inArray(upNodeId,arrNodes) != -1){
 					onCheckNodes.push(nodeId);
 					midNodes.push(nodeId);
 				}
 			});
 		}
		if(midNodes.length > 0){
			findNodeObjOnAndOffCheck(midNodes);
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="tab" style="width: 100%; overflow: hidden;">
		</div>
	</div>
</body>
</html>