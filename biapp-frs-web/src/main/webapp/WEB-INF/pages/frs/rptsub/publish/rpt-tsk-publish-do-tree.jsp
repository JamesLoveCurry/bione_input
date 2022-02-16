<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
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
var upNos = parent.taskInfo.upNos;
var canEdit = '${canEdit}';
var orgTreeInfo = null;
var rptTreeInfo = null;
var checkOrgNodes = [];
var checkRptNodes = [];
var tmpOrgNodes = [];
var tmpRptNodes = [];
var settingAsync = null;
var settingSync = null;
var taskId = window.parent.taskId;
var moduleType = window.parent.moduleType;
var rptTreeNodeIcon = "${ctx}/images/classics/icons/layout_sidebar.png";

$(function(){
	var height = $(document).height();
	$("#center").height(height-2);
	$("#content").height(height-2);
	$("#treeContainer").height(height-49);
	$("#tree").height(height-60);

	if(window.parent.frames.leftframe.initTree != null 
			&& window.parent.frames.leftframe.initTree != "undefined"
			&& window.parent.frames.rightframe.initTree != null
			&& window.parent.frames.rightframe.initTree != "undefined"){
		parent.initData();   //数据初始化中调用了页面方法, 必须在页面初始化完成后才进行数据初始化
	}
	
	initTreeInfo("org", moduleType);
	initTreeInfo("rpt", moduleType);
});

function initTreeInfo(treeType, typ){
	var url = "";
	if(treeType == "org"){
		url = "${ctx}/frs/rpttsk/publish/getAllOrgTree?orgType=" + typ;
	}else if(treeType == "rpt"){
		url = "${ctx}/frs/rpttsk/publish/getAllRptTree?rptType=" + typ + "&taskId=" + taskId;
	}
	
	$.ajax({
		cache : false,
		async : true,
		url : url,
		dataType : "json",
		type : "GET",
		success : function(result) {
			if(treeType == "org"){
				orgTreeInfo=result.orgTreeInfo;
			}else if(treeType == "rpt"){
				rptTreeInfo=result.rptTreeInfo;
			}	
		},
		error : function(result, b) {
		}
	});

	if(taskId){
		var exeobjs = parent.taskInfo.taskInfoVO.exeobjs;
		if(exeobjs != null){
			for ( var n in exeobjs) {
				var node={};
				node.id = exeobjs[n].id.exeObjId;
				node.upId = exeobjs[n].id.exeUpObjId;
				node.params={};
				node.params.realId = exeobjs[n].id.exeObjId;
				node.params.type="org";
				if(getIndex(node,checkOrgNodes) == -1)
					checkOrgNodes.push(node);
			}
		}
		
		var tskobjs = parent.taskInfo.taskInfoVO.tskobjs;
		if(tskobjs != null){
			for ( var n = 0; n < tskobjs.length; n++) {
				var node={};
				node.id = tskobjs[n].id.taskObjId;
				node.params={};
				node.params.realId = tskobjs[n].id.taskObjId;
				node.params.type="rpt";
				if(getIndex(node, checkRptNodes) == -1)   //判断是否包含
					checkRptNodes.push(node);
			}
		}
	}	
	
}

function initTree(treeType, typ){
	var urlAsync = "";
	if(treeType == "org"){
		urlAsync = "${ctx}/frs/rpttsk/publish/getOrgTree?orgType=" + typ;
	}else if(treeType == "rpt"){
		urlAsync = "${ctx}/frs/rpttsk/publish/getRptTree?rptType=" + typ + "&taskId=" + taskId;
	}
	
	settingAsync = {
		async : {
			enable : true,
			url : urlAsync,
			autoParam : ["id",'upId'],
			dataType : "json",
			type : "post",
			dataFilter : function(treeId, parentNode, childNodes) {
				if (childNodes) {
					for ( var i = 0; i < childNodes.length; i++) {
						childNodes[i].upId = childNodes[i].upId;
						childNodes[i].nodeType = childNodes[i].params.type;
						
						if(childNodes[i].params.type == "folder"){
							childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true : false;
						}
						if(childNodes[i].params.type=="rpt"){
							childNodes[i].icon = rptTreeNodeIcon;
							childNodes[i].id = childNodes[i].params.realId;
							childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true: false;
						}
						if(treeType=="org"){
							childNodes[i].id = childNodes[i].params.orgNo;
							if (checkOrgNodes != null && checkOrgNodes.length > 0) {
								for ( var n in checkOrgNodes) {
									var exeobj = checkOrgNodes[n];
									if (childNodes[i].id == exeobj.id && childNodes[i].upId == exeobj.upId) {
										childNodes[i].checked = true;
										childNodes[i].ischecked = true;
									}
								}
							}
						}
						
						if(treeType=="rpt"){
							if(checkRptNodes!=null && checkRptNodes.length>0){
								for (var n=0; n<checkRptNodes.length; n++) {
									var taskobj = checkRptNodes[n];
									if(childNodes[i].id == taskobj.id){
										childNodes[i].checked=true;
										childNodes[i].ischecked = true;
									}
								}
							}
						}
					}
					return childNodes;
				}
			}
		},
		check : {
			enable : true,
			chkboxType : {"Y":"","N":""},
			chkStyle : "checkbox"
		},
		data : {
			key : {
				name : "text"
			},
			simpleData : {
				enable : true,
				idKey : "id"
			}
		},
		view : {
			selectedMulti : false
		},
		callback : {
			onNodeCreated : function(event, treeId, treeNode) {
				if (treeNode.id == "0") {
					//若是根节点，展开下一级节点
					rightTreeObj.reAsyncChildNodes(treeNode, "refresh");  //refresh表示清空后重新加载
				}
				/* if((treeType=="org" && treeNode.upId == "0")){
					//若是根节点，展开下一级节点
					rightTreeObj.reAsyncChildNodes(treeNode, "refresh");
				} */
			},
			beforeCheck : function (treeId, treeNode){
				if(treeType=="rpt" && treeNode.id=="0" && !cascade){
					return false;
				}
				if(canEdit && canEdit=="false")
					return false;   //是否提供选择
				return true;
			},
			onCheck : function(event, treeId, treeNode){
				if(treeType=="org"){
					if(cascade){  //级联
						if (treeNode.getCheckStatus().checked) {
							addCurrentNode(treeNode, checkOrgNodes);  //当前节点
							checkCascade(treeNode.id, checkOrgNodes, treeType); //当前节点所有下级节点            
						}
						else{
							removeCurrentNode(treeNode, checkOrgNodes);
							unCheckCascade(treeNode.id, checkOrgNodes, treeType);
						}
					}else{  //非级联
						if (treeNode.getCheckStatus().checked) {
							addCurrentNode(treeNode, checkOrgNodes);
						}
						else{
							removeCurrentNode(treeNode, checkOrgNodes);
						}
					}
				}else if(treeType=="rpt"){
					if(cascade){  //级联
						if (treeNode.getCheckStatus().checked) {
							addCurrentNode(treeNode, checkRptNodes);  //当前节点
							checkCascade(treeNode.id, checkRptNodes, treeType); //当前节点所有下级节点
						}
						else{
							removeCurrentNode(treeNode, checkRptNodes);
							unCheckCascade(treeNode.id, checkRptNodes, treeType);
						}
					}else{  //非级联
						if (treeNode.getCheckStatus().checked) {
							addCurrentNode(treeNode, checkRptNodes);
						}
						else{
							removeCurrentNode(treeNode, checkRptNodes);
						}
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
				if(treeNode.params.type=="rpt"){
					treeNode.icon = rptTreeNodeIcon;
				}
				
				if(treeType=="org"){
					if(checkOrgNodes!=null && checkOrgNodes.length>0){
						for ( var j in checkOrgNodes) {
							if(treeNode.id == checkOrgNodes[j].id){
								treeNode.checked=true;
								treeNode.ischecked = true;
							}
						}
					}
				}
				
				if(treeType=="rpt"){
					if(checkRptNodes!=null && checkRptNodes.length>0){
						for ( var j in checkRptNodes) {
							if(treeNode.id == checkRptNodes[j].id){
								treeNode.checked=true;
								treeNode.ischecked = true;
							}
						}
					}
				}
			},
			beforeCheck : function (treeId, treeNode){
				if(treeType=="rpt" && treeNode.id=="0" && !cascade){
					return false;
				}
				if(canEdit && canEdit=="false")
					//return false;   //是否提供选择
				return true;
			},
			onCheck : function(event, treeId, treeNode){
				if(treeType=="org"){
					if(cascade){
						if (treeNode.getCheckStatus().checked) {
							//由于根节点与下方节点没有真正的父子关系，因而采用下述方法进行选择
							addSyncCascadeNodes(treeType);
						}
						else{
							for(var i in tmpOrgNodes){
								removeCurrentNode(tmpOrgNodes[i], checkOrgNodes);  //移除当前节点
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
				}else if(treeType=="rpt"){
					if(cascade){
						if (treeNode.getCheckStatus().checked) {
							//由于根节点与下方节点没有真正的父子关系，因而采用下述方法进行选择
							addSyncCascadeNodes(treeType);
						}
						else{
							removeCurrentNode(treeNode, checkRptNodes);          //移除当前节点
							removeSyncCascadeNodes(treeNode.id, checkRptNodes);  //移除级联节点
						}
						
					}else{
						if (treeNode.getCheckStatus().checked) {
							addCurrentNode(treeNode, checkRptNodes);  //添加当前节点
						}
						else{
							removeCurrentNode(treeNode, checkRptNodes);  //移除当前节点
						}
					}
				}
			}
		}
	};
	
	//搜索
	$("#treeSearchIcon").bind("click", function(){
		setTree($("#treeSearchInput").val() != "", treeType, typ);
	});
	$("#treeSearchInput").bind("keydown", function(e){
		if(e.keyCode == 13){
			setTree($("#treeSearchInput").val() != "", treeType, typ);
		}
	});

	setTree(false, treeType, typ);
}

function setTree(searchFlag, treeType, typ){
	if(searchFlag){
		rightTreeObj = $.fn.zTree.init($("#tree"), settingSync);
		check();  //用于同步树切换后的级联状态
		
		if(treeType == "org"){
			loadTree("${ctx}/frs/rpttsk/publish/searchOrgTree?orgType=" + typ, rightTreeObj, treeType);
		}else if(treeType == "rpt"){
			loadTree("${ctx}/frs/rpttsk/publish/searchRptTree?rptType=" + typ + "&taskId=" + taskId, rightTreeObj, treeType);
		}
	}else{
		rightTreeObj = $.fn.zTree.init($("#tree"), settingAsync);
		check();
	}
}

//加载树
function loadTree(urlSync, treeObj, treeType){
	 $.ajax({
		cache : false,
		async : true,
		url : urlSync,
		type : "POST",
		dataType : "json",
		data : {
			searchName : $("#treeSearchInput").val()
		},
		success : function(result){
			var nodes = treeObj.getNodes();
			for(var i=0; i<nodes.length; i++){
				treeObj.removeNode(nodes[i], false);   //移除先前节点
			}
			if(result.length > 0){
				if(treeType == "org"){
					tmpOrgNodes = result;     //用于级联取消选择操作
				}else if(treeType == "rpt"){
					tmpRptNodes = result; 
				}
				treeObj.addNodes(null, result, false);
				treeObj.expandAll(true);
				treeObj.refresh();            //更新显示
			}
		},
		error : function(result, b){
		}
	 });
}

//搜索级联时添加节点
function addSyncCascadeNodes(treeType){
	var nodes = rightTreeObj.getCheckedNodes();
	if( nodes.length>0)
	{
		if(treeType == "org"){
			for(var i in nodes){
				addCurrentNode(nodes[i], checkOrgNodes);
			}
		}else if(treeType == "rpt"){
			for(var i in nodes){
				addCurrentNode(nodes[i], checkRptNodes);
			}
		}
	}
}

//搜索级联时删除节点，for rpt
function removeSyncCascadeNodes(id, checkedNodes){
	var nodes = rptTreeInfo[id];
	if(nodes != null && nodes.length>0){
		for(var i in nodes){
			for(var j in tmpRptNodes){
				if(nodes[i].id == tmpRptNodes[j].id){  //判断是否在查询的目录中
					removeCurrentNode(nodes[i], checkedNodes);
					removeSyncCascadeNodes(nodes[i].id, checkedNodes);   //递归处理
				}
			}
		}
	}
}

//添加当前节点
function addCurrentNode(treeNode, nodes){
	var node={};
	node.id=treeNode.id;
	node.text=treeNode.text;
	node.upId=treeNode.upId;
	node.params=treeNode.params;
	if(getIndex(node, nodes)==-1)
		nodes.push(node);
}

//移除当前节点
function removeCurrentNode(treeNode, nodes){
	var node={};
	node.id=treeNode.id;
	node.text=treeNode.text;
	node.upId=treeNode.upId;
	node.params=treeNode.params;
	var index=getIndex(node, nodes);
	if(index >= 0){
		nodes.splice(index, 1);
	}
}

//添加当前节点下级节点
function checkCascade(id, checkedNodes, treeType){
	var nodes = null;
	if(treeType == "org"){
		nodes = orgTreeInfo[id];
	}else if(treeType == "rpt"){
		nodes = rptTreeInfo[id];
	}
	
	if(nodes!=null && nodes.length>0){
		for(var i in nodes){
			var node={};
			node.id=nodes[i].id;
			node.text=nodes[i].text;
			node.upId=nodes[i].upId;
			node.params=nodes[i].params;
			if(getIndex(node, checkedNodes)==-1)
				checkedNodes.push(node);  //添加节点
			checkCascade(node.id, checkedNodes, treeType);      //递归处理
		}
	}
}

//移除当前节点下级节点
function unCheckCascade(id, checkedNodes, treeType){
	var nodes = null;
	if(treeType == "org"){
		nodes = orgTreeInfo[id];
	}else if(treeType == "rpt"){
		nodes = rptTreeInfo[id];
	}
	
	if(nodes!=null && nodes.length>0){
		for(var i in nodes){
			var node={};
			node.id=nodes[i].id;
			node.text=nodes[i].text;
			node.upId=nodes[i].upId;
			node.params=nodes[i].params;
			var index=getIndex(node, checkedNodes);
			if(index >= 0){
				checkedNodes.splice(index, 1);   //移除节点
			}
			unCheckCascade(node.id, checkedNodes, treeType);  //递归处理
		}
	}
}

function getIndex(info, array){
	for(var i in array){
		if (array[i].id == info.id && (array[i].upId ? (array[i].upId == info.upId) : true)) {
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

function getCheckedOrgNodes(){
	return checkOrgNodes;
}

function getCheckedRptNodes(){
	return checkRptNodes;
}

</script>
<title>Insert title here</title>
</head>
<body style="padding: 0px 0px 0px 0px;">
<div id="center" style="width: 278px;border: solid 1px #D0D0D0;" >
		<div id="content" style="width: 278px;">
			<div style="width:100%;height: 20px;background-color: #FFFFFF;border-bottom: solid 1px #D0D0D0;"><div style="text-align: left;width:100%; padding-left: 10px;padding-top: 2px;">是否级联： 是 <input type="radio" id="level1"  name="level" value="level1" onclick=check() /> 否 <input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true"/></div></div>
			<div id="treeSearchbar" style="width:98%; height:20px; margin-top:2px; padding-left:2px;">
				<ul>
					<li style="width:100%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field" style="width: 100%;padding-top: 0px;padding-left: 0px;bottom: 0px;" />    
									<div class="l-trigger">                                                                      
										<i id="treeSearchIcon" class="icon-search search-size"></i>                                                 
									</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div> 
			<div id="treeContainer"
				style="width: 278px; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 268px;"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>