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
	var url="";
	var downdload=null;
	var checkMap = new Map();//已勾选的节点集合
	
	$(function(){
		$("#treeContainer").height($("#center").height()-30);
		initData("${type}");
		initTree("${type}");
		//按钮
		initBtn("${type}");
	});
	
	//其他树的搜索框事件绑定
	function initTool(){
		var data = {};
		$("#treeSearchIcon").bind("click",function(e){
			var searchText = $("#treeSearchInput").val();
			if(searchText!= ""){
				data.searchText = searchText;
				data.searchNm = searchText;
			}else{
				data = {};
			} 
			loadTree(url, leftTreeObj, data);
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if(event.keyCode==13){
				var searchText = $("#treeSearchInput").val();
				if(searchText!= ""){
					data.searchText = searchText;
					data.searchNm = searchText;
				}else{
					data = {};
				} 
				loadTree(url, leftTreeObj, data);
			}
		});
	}
	
	//指标数据的搜索框事件绑定
	function initIdxTool(){
		$("#treeSearchIcon").bind("click",function(e){
			var searchText = $("#treeSearchInput").val();
			loadIdxTree(searchText);
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if(event.keyCode==13){
				var searchText = $("#treeSearchInput").val();
				loadIdxTree(searchText);
			}
		});
	}
	
	function initBtn(type){
		var btn = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("exportWin");
			}
		},{
			text:'导出',
			onclick:f_save
		}];
		//基础指标不设置版本概念
/* 		if(type == "Index"){
			btn.push({
				text:'导出全版本',
				onclick:f_save_all
			});
		} */
		BIONE.addFormButtons(btn);
	}
	function initData(type){
		switch(type){
		case 'Report':
		case 'Rptrel':
			url="${ctx}/rpt/rpt/rptmgr/info/getInnerRptTree";
			break;
		case 'Dim':
			url= "${ctx}/rpt/frame/dimCatalog/listcatalogs.json?preview=${preview}&d=" + new Date().getTime();
			break;
		case 'Model':
			url="${ctx}/rpt/frame/dataset/getDataModuleTree.json"
			break;
		case 'User':
			url="${ctx}/bione/admin/user/getInnerUserTree"
			break;
		}
	}
	
	function loadTree(url,component, data){
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			type : "post",
			data : data,
			success : function(result){
				var nodes = component.getNodes();
				var num = nodes.length;
				for ( var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					component.addNodes(null, result, false);
					component.expandAll(true);
				}
				if(checkMap && checkMap.size > 0){
					// for (var [key, value] of checkMap){
					for (var key in checkMap){
						var node = leftTreeObj.getNodeByParam("id", key, null);
						if(node){
							leftTreeObj.checkNode(node,true,false,false);
						}
					}
				}
			},
			error:function(){
				//BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}
	
	//树
	function initTree(type){
		var setting={};
		switch(type){
		case 'Report':
		case 'Rptrel':
		case 'Dim':
		case 'Model':
		case 'User':
			setting = {
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
				callback: {
					onCheck: treeOnCheck
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
			loadTree(url,leftTreeObj);
			initTool();
			break;
		case 'Idxplanval':
		case 'Idxcheck':
			loadIdxTree();
			initIdxTool();
			break;
		case 'Index':
			loadIdxTree();
			initIdxTool();
			break;
		}
	}
	
	//指标校验原来的指标树，是异步的，替换成和指标管理导出处一样的指标同步树，目前没有发现问题
	function loadIdxCheTree(searchNm){
		setting ={
				async:{
					enable:true,
					url: "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=0&isAuth=1&t="
						+ new Date().getTime(),
					autoParam:["nodeType", "id", "indexVerId"],
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
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
					onCheck:function(event, treeId, treeNode){
							if(treeNode.getCheckStatus().checked==true&&treeNode.children==null)
								leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
					},
					onAsyncSuccess:function(event, treeId, treeNode, msg){
						if(treeNode!=null){
							if (treeNode.getCheckStatus().checked) {
								for ( var i in treeNode.children) {
									leftTreeObj.checkNode(treeNode.children[i], true, false);
									leftTreeObj.reAsyncChildNodes(treeNode.children[i],
												"refresh");
								}
							}
						}
					}
				}
				
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	//指标管理和指标校验现在都用着一颗树
	function loadIdxTree(searchNm){
		var _url = "${ctx}/report/frame/idx/getSyncTree";
		var data = {'searchNm':searchNm ,'isShowIdx':'1','isShowDim':'0', 'isShowMeasure':'0','isPublish':'1', "isAuth" : "0", "showEmptyFolder":""};
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
				onCheck:function(event, treeId, treeNode){
					recheckMap(treeNode);
					if(treeNode.getCheckStatus().checked==true&&treeNode.children==null)
						leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
				},
				onAsyncSuccess:function(event, treeId, treeNode, msg){
					if(treeNode!=null){
						if (treeNode.getCheckStatus().checked) {
							for ( var i in treeNode.children) {
								leftTreeObj.checkNode(treeNode.children[i], true, false);
								leftTreeObj.reAsyncChildNodes(treeNode.children[i],"refresh");
							}
						}
					}
				}
			}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"),setting,[]);
		BIONE.loadTree(_url,leftTreeObj,data,function(childNodes){
			if(childNodes){
				for(var i = 0;i<childNodes.length;i++){
					childNodes[i].nodeType = childNodes[i].params.nodeType;
					childNodes[i].indexVerId = childNodes[i].params.indexVerId;
					childNodes[i].open = false;
					if(checkMap.get(childNodes[i].id)){
						childNodes[i].checked = true;
					}
				}
			}
			return childNodes;
		},false);
	}
	
	function treeOnCheck(event, treeId, treeNode){
		recheckMap(treeNode);
	}
	
	function recheckMap(treeNode){
		if(treeNode){
			if(checkMap.get(treeNode.id) && !treeNode.checked){
				checkMap.delete(treeNode.id);
			}else if(treeNode.checked){
				checkMap.set(treeNode.id, treeNode);
			}
			if(treeNode.children && treeNode.children.length > 0){
				for(var i in treeNode.children){
					recheckMap(treeNode.children[i]);
				}
			}
		}
	}
	
	
	function refreshTree() {  //刷新树
		if (leftTreeObj) {
			loadTree();
		}
	}
	
	function getIds(type){
		var ids="";
		switch(type){
		case 'Report':
		case 'Rptrel':
			nodes = leftTreeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.nodeType == "03"){
					ids += nodes[i].params.realId + ",";
				}
			}
			break;
		case 'Dim':
			nodes = leftTreeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.type == "dimTypeInfo"){
					ids += nodes[i].data.dimTypeNo + ",";
				}
			}
			break;
		case 'Model':
			nodes = leftTreeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.type == "setInfo"){
					ids += nodes[i].id + ",";
				}
			}
			break;
		case 'Index':
			nodes = leftTreeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.nodeType == "idxInfo"){
					ids += nodes[i].id + ",";
				}
			}
			break;
		case 'User':
			nodes = leftTreeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.nodetype == "user"){
					ids += nodes[i].id + ",";
				}
			}
			break;
		case 'Idxplanval':
			nodes = leftTreeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.nodeType == "idxInfo"){
					ids += nodes[i].id + ",";
				}
			}
			break;
		case 'Idxcheck':
			nodes = leftTreeObj.getCheckedNodes(true);
			for(var i in nodes){
				if(nodes[i].params.nodeType == "idxInfo"){
					ids += nodes[i].id + ",";
				}
			}
			break;
		}
		ids = ids.substring(0,ids.length-1);
		return ids;
	}
	
	function getInfo(type){
		switch(type){
		case 'Report':
		case 'Rptrel':
			return "报表";
			break;
		case 'Dim':
			return "维度";
			break;
		case 'Model':
			return "数据模型";
			break;
		case 'Index':
			return "指标";
			break;
		case 'User':
			return "用户";
			break;
		case 'Idxplanval':
			return "指标计划值";
			break;
		case 'Idxcheck':
			return "指标校验数据";
			break;
		}
		return "";
	}
	
	function f_save(){
		ids = getIds("${type}");
		url="${ctx}/report/frame/wizard/download?type=${type}";
		if(ids.length>0){
			$.ajax({
				async:true,
				type:"POST",
				dataType:"json",
				url:url,
				data:{"ids":ids},
				beforeSend : function(a, b, c) {
					BIONE.showLoading('正在导出数据中...');
				},
				success:function(data){
					BIONE.hideLoading();
					if(data.fileName==""){
						BIONE.tip('导出异常');
						return;
					}
					window.parent.exportExcel(data.fileName,"${type}");
					BIONE.closeDialog("exportWin");
				},
				error : function(result) {
					BIONE.hideLoading();
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
		else{
			BIONE.tip("请选择"+getInfo("${type}")+"节点");
		}
	}
	
	function f_save_all(){
		ids = getIds("${type}");
		url="${ctx}/report/frame/wizard/download?type=IndexAll";
		if(ids.length>0){
			$.ajax({
				async:true,
				type:"POST",
				dataType:"json",
				url:url,
				data:{"ids":ids},
				beforeSend : function(a, b, c) {
					BIONE.showLoading('正在导出数据中...');
				},
				success:function(data){
					BIONE.hideLoading();
					if(data.fileName==""){
						BIONE.tip('导出异常');
						return;
					}
					window.parent.exportExcel(data.fileName,"IndexAll");
					BIONE.closeDialog("exportWin");
				},
				error : function(result) {
					BIONE.hideLoading();
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
		else{
			BIONE.tip("请选择"+getInfo("${type}")+"节点");
		}
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">
			<ul>
				<li style="width:98%;text-align:left;">                      
					<div class="l-text-wrapper" style="width: 100%;">                         
						<div class="l-text l-text-date" style="width: 100%;">                    
							<input id="treeSearchInput" type="text" class="l-text-field" style="width: 95%;">    
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
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
</body>
</html>