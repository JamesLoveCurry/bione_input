<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load_BS.jsp"%>
<%@ include file="/common/ligerUI_load_BS.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<style type="text/css">
#center {
	position: relative;
	top: 0;
	width: 100%;
	z-index: 0;
	overflow: hidden;
	margin-left: auto;
	margin-right: auto;
}
</style>
<script type="text/javascript">

var rightTreeObj=null;
var cascade = false;
var searchFlag = false;
var treeInfo=null;
var checkOrgNodes=[];
var tmpNodes=[];
var moduleType = "${moduleType}";
var settingAsync = null;
var settingSync = null;

$(function(){
	var height = $(document).height();
	$("#center").height(height-42);
	$("#content").height(height-42);
	$("#treeContainer").height(height-75-2);
	initTreeInfo();
	initBaseInfo('${moduleType}')
	initBtn();
	initTree();
	setTree(false);
});

function initBtn() {
	var btns = [{
		text : "取消",
		onclick : function() {
			BIONE.closeDialog("setWatchWin", null, false);
		}
	},{
        text : "确定",
        onclick : function() {
            BIONE.closeDialog("setWatchWin", null, true, getCheckedOrgNodes());
        }
    }];
	BIONE.addFormButtons(btns);
}

//搜索级联时添加节点
function addSyncCascadeNodes(){
	var nodes = rightTreeObj.getCheckedNodes();
	if( nodes.length>0)
	{
		for(var i in nodes){
			addCurrentNode(nodes[i], checkOrgNodes);
		}
	}
}

function initBaseInfo(typ){
	$.ajax({
		cache : false,
		async : true,
		url : "${ctx}/frs/rpttsk/publish/getAllOrgTree",
		dataType : "json",
		data : {
		    orgType : typ
		 },
		type : "GET",
		success : function(result) {
			treeInfo = result.orgTreeInfo;
		},
		error : function(result, b) {
		}
	});	
}

function initTreeInfo(){
	$.ajax({
		type : "POST",
		url : "${ctx}/rpt/frs/rptfill/getWatchOrg",
		dataType : "json",
		async : false,
        data : {
            moduleType : '${moduleType}'
         },
		success : function(result) {
			if(result!=null){
				for ( var i in result) {
					var node={};
					node.id = result[i].id.orgId;
					node.text = result[i].orgNm;
					if(getIndex(node, checkOrgNodes)==-1)
						checkOrgNodes.push(node);
				}
			}
		},
		error : function(result, b) {
		}
	});
}
function initTree(){
	settingAsync = {
		async : {
			enable : true,
			url :"${ctx}/frs/rpttsk/publish/getOrgTree?orgType=${moduleType}&d="+ new Date().getTime() ,
			autoParam : [ "id" ],
			dataType : "json",
			type : "get",
			dataFilter : function(treeId, parentNode, childNodes) {
				if (childNodes) {
					for ( var i = 0; i < childNodes.length; i++) {
						childNodes[i].id = childNodes[i].params.realId;
						childNodes[i].upId = childNodes[i].upId;
						childNodes[i].nodeType = childNodes[i].params.type;
						childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true: false;
						if(checkOrgNodes!=null && checkOrgNodes.length>0){
							for ( var j in checkOrgNodes) {
								if(childNodes[i].id == checkOrgNodes[j].id){
									childNodes[i].checked=true;
									childNodes[i].ischecked = true;
								}
							}
						}
					}
					return childNodes;
				}
			}
		},
		check:{
			enable : true,
			chkboxType :{"Y":"","N":""},
			chkStyle :"checkbox"
		},
		data : {
			key : {
				name : "text"
			}
		},
		view : {
			selectedMulti : false
		},
		callback : {
			onNodeCreated : function(event, treeId, treeNode) {
				//若是根节点，展开下一级节点
 				if((treeNode.upId == "0")){
					rightTreeObj.reAsyncChildNodes(treeNode, "refresh");
				}
				
			},
			beforeCheck:function (treeId, treeNode){  //是否可选择
				if(treeNode.id=="ROOT" && !cascade){
					return false;
				}
				return true;
			},
			onCheck:function(event, treeId, treeNode){
				if(cascade){
					if (treeNode.getCheckStatus().checked) {
						addCurrentNode(treeNode, checkOrgNodes);   //添加当前节点
						checkCascade(treeNode.id, checkOrgNodes);  //添加下级节点
					}
					else{
						removeCurrentNode(treeNode, checkOrgNodes);   //移除当前节点
						unCheckCascade(treeNode.id, checkOrgNodes);   //移除下级节点
					}
				}
				else{
					if (treeNode.getCheckStatus().checked) {
						addCurrentNode(treeNode, checkOrgNodes);
					}
					else{
						removeCurrentNode(treeNode, checkOrgNodes);
					}
				}
			}
		}
	};
	
	settingSync = {
		check:{
			enable : true,
			chkboxType :{"Y":"","N":""},
			chkStyle :"checkbox"
		},
		data : {
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
		view : {
			selectedMulti : false
		},
		callback : {
			onNodeCreated : function(event, treeId, treeNode) {
				if(checkOrgNodes!=null && checkOrgNodes.length>0){
					for ( var j in checkOrgNodes) {
						if(treeNode.id == checkOrgNodes[j].id){
							treeNode.checked=true;
							treeNode.ischecked = true;
						}
					}
				}
			},
			onCheck:function(event, treeId, treeNode){
				if(cascade){
					if (treeNode.getCheckStatus().checked) {
						//由于根节点与下方节点没有真正的父子关系，因而采用下述方法进行选择
						addSyncCascadeNodes();
					}
					else{
						for(var i in tmpNodes){
							removeCurrentNode(tmpNodes[i], checkOrgNodes);  //移除当前节点
						}
					}
					
				}else{
					if (treeNode.getCheckStatus().checked) {
						addCurrentNode(treeNode, checkOrgNodes);  //添加当前节点
					}
					else{
						removeCurrentNode(treeNode, checkOrgNodes);  //移除当前节点
					}
				}
			}
		}
	};
	//搜索
	$("#treeSearchIcon").bind("click", function(){
		setTree($("#treeSearchInput").val() != "");
	});
	$("#treeSearchInput").bind("keydown", function(e){
		if(e.keyCode == 13){
			setTree($("#treeSearchInput").val() != "");
		}
	});
}

function setTree(searchFlag){
	if(searchFlag){
		rightTreeObj = $.fn.zTree.init($("#tree"), settingSync);
		check();  //用于同步树切换后的级联状态
		loadTree("${ctx}/frs/rpttsk/publish/searchOrgTree?orgType=" + moduleType, rightTreeObj, {searchName : $("#treeSearchInput").val()});
	}else{
		rightTreeObj = $.fn.zTree.init($("#tree"), settingAsync);
		check();
	}
}

//加载树
function loadTree(url, treeObj, data){
	 $.ajax({
		cache : false,
		async : true,
		url : url,
		type : "POST",
		dataType : "json",
		data : data,
		beforeSend : function(){
		},
		complete : function(){
			BIONE.loading = false;
			BIONE.hideLoading();
		},
		success : function(result){
			var nodes = treeObj.getNodes();
			for(var i=0; i<nodes.length; i++){
				treeObj.removeNode(nodes[i], false);   //移除先前节点
			}
			if(result.length > 0){
				tmpNodes = result;   //用于级联取消选择操作
				treeObj.addNodes(null, result, false);
				treeObj.expandAll(true);
				treeObj.refresh();   //更新显示
			}
		},
		error : function(result, b){
		}
	 });
}

//添加当前节点
function addCurrentNode(treeNode, nodes){
	var node={};
	node.id=treeNode.id;
	node.text=treeNode.text;
	node.params=treeNode.params;
	if(getIndex(node, nodes)==-1)
		nodes.push(node);
}

//移除当前节点
function removeCurrentNode(treeNode, nodes){
	var node={};
	node.id=treeNode.id;
	node.text=treeNode.text;
	node.params=treeNode.params;
	var index=getIndex(node, nodes);
	if(index >= 0){
		nodes.splice(index, 1);
	}
}

//添加当前节点下级节点
function checkCascade(id, checkedNodes){
	var nodes = treeInfo[id];
	for(var i in nodes){
		addCurrentNode(nodes[i], checkedNodes)
		checkCascade(nodes[i].id, checkedNodes);
	}
}

//删除当前节点下级节点
function unCheckCascade(id, checkedNodes){
	var nodes = treeInfo[id];
	for(var i in nodes){
		removeCurrentNode(nodes[i], checkedNodes)
		unCheckCascade(nodes[i].id, checkedNodes);
	}
}

function getIndex(info,array){
	for(var i in array){
		if(array[i].id==info.id){
			return i;
		}
	}
	return -1;
}
//是否级联选择
function check() {
	cascade = $("#level1")[0].checked;
	if ($("#level1")[0].checked == true)
		rightTreeObj.setting.check.chkboxType = {
			"Y" : "s",
			"N" : "s"
		};
	else
		rightTreeObj.setting.check.chkboxType = {
			"Y" : "",
			"N" : ""
		};
}

function getUpOrgNo(id) {
	for ( var i in upNos) {
		if (id == upNos[i]) {
			return true;
		}
	}
	return false;
}

function getCheckedOrgNodes(){
	return checkOrgNodes;
}

</script>
<title>Insert title here</title>
</head>
<body style="padding: 0px 0px 0px 0px;">
	<div id="center" style="width:298px; border: solid 1px #D0D0D0;" >
		<div id="content" style="width: 288px;">
			<div style="width:100%; height:25px; background-color:#FFFFFF; border-bottom:solid 1px #D0D0D0;">
				<div style="text-align: left; width:100%; padding-left: 10px; padding-top: 4px;">
					是否级联： <input type="radio" id="level1" name="level" value="level1" onclick=check() />是 &nbsp;&nbsp;
					<input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true" />否 &nbsp;&nbsp;
					<!--  
					<input type="radio" id="level3" name="level" value="level3" onclick=check() />除支行外级联
					-->
				</div>
			</div> 
			<div id="treeSearchbar" style="width:100%; margin-top:2px; padding-left:2px;">
				<ul>
					<li style="width:98%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%; padding-top:5px;" />    
								<div class="l-trigger">                                                                      
									<div id="treeSearchIcon" style="width:100%; height:100%; background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>                                                 
								</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div>   
			<div id="treeContainer"
				style="width: 288px; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 268px;"
					class="ztree"></ul>
			</div>
		</div>
	</div>
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
	</div>
</body>
</html>