<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
	var leftTreeObj;
	var currentNode;
	var nodes;
	var rptIds;
	var busiType = '02';
	var searchNm;//搜索报表名称
	var checkNodes=[];//存放选中的报表节点

	var urlPath = '${ctx}/report/frame/valid/orgmerge';

	$(function(){
		$("#treeContainer").height($("#center").height()-2);
		initTree();
		refreshTree();
		initExport();
		
		//按钮
		var btn = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("export");
			}
		},{
			text:'导出',
			onclick:f_save
			}
		];
		
		BIONE.addFormButtons(btn);
		jQuery.metadata.setType("attr","validate");
		BIONE.validate($("#mainform"));
		
		function loadTree(){
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/design/cfg/getRptTree",
				dataType : 'json',
				type : "post",
				data : {
					busiType : busiType,
					searchNm: searchNm
				},
				success : function(result){
					if(result != null
							&& typeof result != "undefined"){
						// 移除旧节点
						leftTreeObj.removeChildNodes(leftTreeObj.getNodeByParam("id", '0', null));
						leftTreeObj.removeNode(leftTreeObj.getNodeByParam("id", '0', null),false);
						// 渲染新节点
						leftTreeObj.addNodes(leftTreeObj.getNodeByParam("id", '0', null) , result , true);
						leftTreeObj.expandAll(true);
					}
				},
				error:function(){
					//BIONE.tip("加载失败，请联系系统管理员");
				}
			});
		}
		
		//树
		function initTree(){
			var setting = {
				data:{
					key:{
						name:"text"
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
				check : {
					enable : true,
					chkStyle : "checkbox",
					chkboxType : {
						"Y" : "s",
						"N" : "s"
					}
				},
				callback : {
					onClick: zTreeOnClick,
					onCheck: function (event, treeId, treeNode) {
						if (!treeNode.ischecked) {
							if (treeNode.params.isLeaf == "true") {
								checkNodes.push(treeNode.params.realId);
							} else {
								var childNodes = treeNode.children;
								for (var node in childNodes) {
									if (childNodes[node].params.isLeaf == "true") {
										checkNodes.push(treeNode.params.realId)
									}
								}
							}
						}else{
							if(treeNode.params.isLeaf=="false"){
								var childNodes=treeNode.children;
								for (var node in childNodes) {
									if(childNodes[node].params.isLeaf=="true"){
										childNodes.push(treeNode.params.realId);
									}
								}
							}else{
								var index=checkNodes.indexOf(treeNode.params.realId)
								if (index != -1) {
									checkNodes.splice(index, 1);
								}
							}
						}
					},
					onNodeCreated:function (event,treeId,treeNode){
						if(checkNodes!=null && checkNodes.length>0){
							for (var i in checkNodes) {
								if(treeNode.params.realId ==checkNodes[i]){
									treeNode.checked=true;
									treeNode.ischecked=true;
									leftTreeObj.checkNode(treeNode, true, true);
								}
							}
						}
					}
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			$("#treeSearchIcon").bind("click",function (){
				searchNm = $("#treeSearchInput").val();
				loadTree();
			});
			$("#treeSearchInput").bind("keydown",function (e){
				if(e.keyCode==13){
					searchNm = $("#treeSearchInput").val();
					loadTree();
				}
			});
		}
		
		function zTreeOnClick(event, treeId, treeNode) {
			currentNode = treeNode;
		}
		
		function refreshTree() {  //刷新树
			if (leftTreeObj) {
				loadTree();
			}
		}
		
		function f_save(){
			nodes = leftTreeObj.getCheckedNodes(true);
			if(nodes == null || nodes == ""){
				BIONE.tip("请选择报表！");
			}else{
				rptIds="";
				var busiTypes = [];
				for(var i in nodes){
					if(nodes[i].params.nodeType == "03"){
						rptIds += nodes[i].params.cfgId + ";" + nodes[i].text + ",";
					}
				}
				rptIds = rptIds.substring(0,rptIds.length-1);
				if(rptIds == ""){
					BIONE.tip("请选择报表！");
					return;
				}
				$.ajax({
					type : "POST",
					dataType : "json",
					url : urlPath+'/expAll',
					data : {
						"rptIds" : rptIds
					},
					beforeSend : function(a, b, c) {
						BIONE.showLoading('正在导出数据中...');
					},
					success : function(data) {
						BIONE.hideLoading();
						if (data.filePath == "") {
							BIONE.tip('导出异常');
							return;
						}
						downdload.attr('src', urlPath+"/downloadAll?filePath=" + data.filePath);
					},
					error : function(result) {
						BIONE.hideLoading();
					}
				});
			}
		}
	});

	var initExport = function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	
</script>
</head>
<body>
<div id="template.center">
	<div id="treeSearchbar" style="width: 98%;height: 20px;margin-top: 2px;padding-left: 2px;">
		<ul>
			<li style="width: 100%;text-align: left;">
				<div class="l-text-wrapper" style="width: 100%">
					<div class="l-text l-text-date" style="width: 100%;">
						<input id="treeSearchInput" type="text" class="l-text-field" style="width: 98%"/>
						<div class="l-trigger">
							<i id="treeSearchIcon" class="icon-search search-size"></i>
						</div>
					</div>
				</div>
			</li>
		</ul>
	</div>
	<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12px; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
	</div>
</div>
</body>
</html>