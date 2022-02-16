<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template4B.jsp">
<head>
<script type="text/javascript">

		var ROOT_NO = '0';
		var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png", groupicon = "${ctx}/images/classics/icons/find.png";
		var selectedIds =[];
		var taskObjType;
		var handleType;
		var isComp = false;
		var seltaskObjs = parent.parent.parent.seltaskObjs;
		var seltaskAuthType = parent.parent.parent.seltaskAuthType;
		var operType = parent.parent.parent.operType;
		var insNodes = parent.parent.parent.insNodes;

		var tableWidth;
		
		$(function() {
			window.parent.frameObj = window;
			initData();
			initTreeToolBar();
			initChooseTree();
			initSearchBar();
			initTask();
			$(".l-treetoolbar-item-hasicon").css("padding-right", "1px").css("padding-left", "5px");

			// $("#treeSearchbar").width($("#lefttable").width() - 330);
			setSearchbarWidth();
		});

		function setSearchbarWidth() {
			tableWidth = $("#lefttable").width();
			if(!tableWidth) {
				setTimeout("setSearchbarWidth()", 100)
			}
			$("#treeSearchbar").width(tableWidth - 330);
		}
		
		function initTask(){
			onCheckOrg();
		}
		function onCheckOrg(orgs,nodeType){
			if (leftTree && authLeftCombobox) {
				var objDefNo = authLeftCombobox.getValue();
				if(objDefNo=="AUTH_OBJ_USER"){
					getTypeTree(objDefNo);
				}else if(objDefNo=="AUTH_OBJ_ROLE"){
					getTypeTree(objDefNo);
				}
			}
		}
		
		function initSearchBar(){
			$("#treeSearchIcon").live(
					'click',
					function() {
						leftTree.removeChildNodes(leftTree.getNodeByParam("id", '0', null));
						var searchNm = $("#treeSearchInput").val();
						if(!taskObjType){
							taskObjType = authLeftCombobox.getValue();
						}
						var type =taskObjType ||  data[0].id;
						getTypeTree(type,searchNm);
					});

			$("#treeSearchInput").bind("keydown",function(e){
				if(e.keyCode == 13){
					leftTree.removeChildNodes(leftTree.getNodeByParam("id", '0', null));
					var searchNm = $("#treeSearchInput").val();
					if(!taskObjType){
						taskObjType = authLeftCombobox.getValue();
					}
					var type =taskObjType ||  data[0].id;
					getTypeTree(type,searchNm);
				}
			});
			
		}
		/*
		function initData(){
			handleType = window.parent.getHandleType();
			if( window.parent.taskUnifyNode){
				taskObjType = window.parent.taskUnifyNode.taskObjType;
				var taskObjIdMap =  window.parent.taskUnifyNode.taskObjIdMap;
				for(var i = 0 ;i <taskObjIdMap.length;i++){
					selectedIds.push(taskObjIdMap[i].taskObjId);
				}
			}
		}
		*/
		function initData(){
			handleType = window.parent.getHandleType();
			var taskNodeDefId =  window.parent.taskNodeDefId;
			if( window.parent.taskUnifyNode){
				taskObjType = window.parent.taskUnifyNode.taskObjType;
				var taskUnifyNodeList = window.parent.parent.taskUnifyNodeList;
				for(var x = 0 ;x < taskUnifyNodeList.length;x++){
					if(taskUnifyNodeList[x].taskNodeDefId == taskNodeDefId ){
						var taskObjIdMap =  taskUnifyNodeList[x].taskObjIdMap;
						for(var i = 0 ;i <taskObjIdMap.length;i++){
							selectedIds.push(taskObjIdMap[i].taskObjId);
						}
					}
				}
			}
		}
		function isCheckNode(id){
			
			for(var i = 0 ;i <selectedIds.length;i++){
				if(selectedIds[i]==id)
					return true;
			}
			return false;
			
			if(selectedIds&&taskObjType){
				var  type = "AUTH_OBJ_ROLE";
				if(window["authLeftCombobox"])
					type =   window["authLeftCombobox"].getValue();
				if(type == taskObjType){
					for(var i = 0 ;i <selectedIds.length;i++){
						if(selectedIds[i]==id)
							return true;
					}
					return false;
				}
			}
			
			return false;
		}
		function initTreeToolBar() {

			$("#treeToolbar").ligerTreeToolBar({
				items : [ {
					icon : 'refresh',
					text : '刷新',
					click : function() {
						if (leftTree && authLeftCombobox) {
							var objDefNo = authLeftCombobox.getValue();
							if (objDefNo && objDefNo != "") {
								getTypeTree(objDefNo);
							}
						}
					}
				}, {
					line : true
				}, {
					icon : 'list4',
					text : '展开',
					click : function() {
						openAllNodes(leftTree);
					}
				}, {
					line : true
				}, {
					icon : 'lock',
					text : '折叠',
					click : function() {
						closeAllNodes(leftTree);
					}
				} ],
				treemenu : false
			});
		}
		
		function initChooseTree() {
			window['leftTree'] = $.fn.zTree
					.init(
							$("#leftTree"),
							{
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
								check : {
									chkStyle : 'checkbox',
									enable : true
								},
								view : {
									selectedMulti : false,
									showLine : false
								},
								async : {
									enable : true,
									url : "${ctx}/bione/admin/auth/getAuthOrgTree.json?d="+new Date(),
									autoParam : [ 'id', 'objDefNo' ],
									dataFilter : function() {
										var cNodes = arguments[2];
										$
												.each(
														cNodes,
														function(i, n) {
															n.objDefNo = $(
																	'input[name=template_left_combobox_val]')
																	.val();
														});
										return cNodes;
									}
								},
								callback : {
									onClick : function(event, treeId, treeNode){
										if(treeNode.data.orgId !=null || typeof treeNode.data.orgId !="undefined") 
											return ;
									},
									onNodeCreated : function(event, treeId, treeNode) {
										if(treeNode.id!=null&&treeNode.id!="" ){
											if(isCheckNode(treeNode.id)){
												window['leftTree'].checkNode(treeNode, true, true);
											}
										}

										if(operType != 'del'){
										var taskNodeDefId =  window.parent.taskNodeDefId
											var selnodes = seltaskObjs[taskNodeDefId];
											if(selnodes){
												 if(selnodes.indexOf(treeNode.id) != -1){
													 window['leftTree'].checkNode(treeNode,true,true);
						                        	 selnodes.splice(selnodes.indexOf(treeNode.id),1)
						        	             }
											}
										}else{
											var uog =  parent.parent.parent.upOrg.concat();
											
											var ntp =  window.parent.nodeType;
											var defiInsNode = insNodes[ntp];
											if(treeNode.upId != null && defiInsNode.indexOf(treeNode.id) == -1
													&& uog.indexOf(treeNode.id) == -1){
												window['leftTree'].removeNode(treeNode);
											}
											
										}
										

										
									},
									onCheck:function(event, treeId, treeNode){
										initTitle();
									}
								}
							}, [ {
								id : "0",
								text : "下发对象"
							} ]);
			
			window['authLeftCombobox'] = $("#template_left_combobox")
				.ligerComboBox(
						{
							url : "${ctx}/rpt/input/task/getAuthObjCombo.json?d="
									+ new Date().getTime(),
							ajaxType : "post",
							labelAligh : "center",
							slide : false,
							width : 60,
							onBeforeOpen : function(){
								if(operType == 'del'){
									return false;
								}
							},
							onSuccess : function(data) {
								if (data && data.length > 0) {
									var taskNodeDefId =  window.parent.taskNodeDefId
									var authType = seltaskAuthType[taskNodeDefId];
									//初始化后设置combobox默认值
									if(!taskObjType){
										taskObjType = authLeftCombobox.getValue();
									}
									var type =authType ||  data[0].id;
									authLeftCombobox.selectValue(type);
									//加载相应授权对象树
									if (leftTree && leftTree.setting) {
										getTypeTree(type);
									}
								}
							}
						});
			//选择授权对象事件
			authLeftCombobox.bind('beforeSelect', function(value, text) {
				if(taskObjType ==value)
					return ;
				leftTree.removeChildNodes(leftTree.getNodeByParam("id", '0', null));
				//刷新授权对象树
				taskObjType = value;
				if (leftTree) {
					if (!leftTree.getSelectedNodes().length
							|| leftTree.getSelectedNodes().length <= 0) {
						leftComboChange(value, text);
						return true;
					}
				}
				authLeftCombobox.selectValue(value);
				leftComboChange(value, text);
				leftTree.cancelSelectedNode(leftTree
						.getNodeByParam("id", '0', null));
				//清空缓存
				permissionTreeDatas = new Array();
				return false;
			}, this);
			
		}
		
		function initTitle(){
			if(window.parent.nodeType=="02")
				return ;
			var checkedNodes = leftTree.getCheckedNodes();
			var allShowNodes=[];
			var showText = "";
			for(var i =0;i<checkedNodes.length;i++){
				if(!checkedNodes[i].isParent&&!checkContains(allShowNodes,checkedNodes[i].upId))
					allShowNodes.push(checkedNodes[i].upId);
			}
			var isFirst = true;
			for(var i = 0 ;i <allShowNodes.length;i++){
				if(isFirst)
					isFirst=false;
				else
					showText +=",";
				showText += allShowNodes[i];
			}
			window.parent.parent.parent.setTaskTitle(showText);
		}
		
		function checkContains(allIds,id){
			for(var i = 0 ;i<allIds.length;i++ )
				if(allIds[i]==id)
					return true;
			return false;
		}
		function initIcon(nodes) {
			if (nodes && nodes instanceof Array === true) {
				for ( var i = 0; i < nodes.length; i++) {
					var r = nodes[i];
					r.icon = "${ctx}" + (r.icon.indexOf("/") != 0 ? "/" : "")
							+ r.icon;
					if (r.children && r.children != null) {
						r.children = initIcon(r.children);
					}
				}
			}
			return nodes;
		}

function onCancelOrg(orgs){
	//leftTree.remove
	if(typeof orgs =="object"){
		for(var i = 0 ;i <orgs.length;i++)
		{
			removeTreeNodeOfOrg(orgs[i]);
		}
	}else if(typeof orgs =="string")
		removeTreeNodeOfOrg(orgs);
	initTitle();
}

function removeTreeNodeOfOrg( org ){
	handleType = window.parent.getHandleType();
	if(handleType =="01"){
		//上级
		var node = window.parent.parent.leftTreeObj.getNodeByParam("id", org, null);
		if(node){
			var parentNode = node.getParentNode();
			if(parentNode){
				for(var i =0;i<parentNode.children.length;i++){
					if(parentNode.children[i].checked)
						return ;
				}
				leftTree.removeNode(leftTree.getNodeByParam("id", parentNode.id, null) );
			}
		}
		//node.getParentNode().getchild
	}
	else{
		leftTree.removeNode(leftTree.getNodeByParam("id", org, null));
	}
}

//刷新左侧授权对象树
function getTypeTree(objDefNo,searchNm,nodeType) {
	var sendOrgs="";
	var checkedNodes =  window.parent.parent.leftTreeObj.getCheckedNodes();
	if(checkedNodes&&checkedNodes.length>0){
		for(var i = 0 ;i <checkedNodes.length;i++)
		{
			//if(!leftTree.getNodeByParam("id", checkedNodes[i].id, null)){
				if(i!=0)
					sendOrgs = sendOrgs +",";
				sendOrgs = sendOrgs+checkedNodes[i].params.realId;
			}
		//}
	}
	handleType = window.parent.getHandleType();
	if (leftTree && leftTree.setting) {
		//先移除所有授权对象节点
		//leftTree.removeChildNodes(leftTree.getNodeByParam("id", '0', null));
		//查询该授权对象并更新树
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/task/getTypeTree.json?d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				"objDefNo" : objDefNo,"handleType":handleType,orgNo: sendOrgs,"searchNm":searchNm,"nodeType":nodeType
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
				if (!result)
					return;
				var addNodes = getAddNodes(result);
				if(!addNodes||addNodes.length ==0)
					return ;
				$.each(result, function(i, n) {
					n.objDefNo = objDefNo;
				});
				leftTree.addNodes(leftTree.getNodeByParam("id", '0', null),
						addNodes, true);
				//展开树
				leftTree.expandAll(true);
				//var rootNodeTmp = leftTree.getNodeByParam("id", ROOT_NO,
				//		null);
				//leftTree.expandNode(rootNodeTmp, true, false);
				
				if(isComp == false){
					isComp = true;
					window.parent.parent.doAfterFrameDone(window.parent.name);
				}
			},
			error : function(result, b) {
				if(result==null||result.responseText==''){
					var addNodes = getAddNodes(result);
					if(!addNodes||addNodes.length ==0)
						return ;
					$.each(result, function(i, n) {
						n.objDefNo = objDefNo;
					});
					leftTree.addNodes(leftTree.getNodeByParam("id", '0', null),
							addNodes, true);
					//展开树
					leftTree.expandAll(true);
					//var rootNodeTmp = leftTree.getNodeByParam("id", ROOT_NO,
					//		null);
					//leftTree.expandNode(rootNodeTmp, true, false);
					
					if(isComp == false){
						isComp = true;
						window.parent.parent.doAfterFrameDone(window.parent.name);
					}
				}else
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

}

function getAddNodes(result){
	var addNodes = getFreshNode(result);
	var resultNodes = [];

	//var objDefNo = authLeftCombobox.getValue();
	for(var i = 0 ;i <result.length;i++){
		//if(objDefNo=="AUTH_OBJ_USER")
		//{
			if(needAdd(addNodes,result[i]))
				resultNodes.push(result[i]);
		//	else continue;
		//}
		//else
			//resultNodes.push(result[i]);
	}
	return resultNodes;
}

function needAdd(addNodes,node){
	for(var i = 0 ;i <addNodes.length;i++){
		if(node.isParent&&node.id == addNodes[i])
			return true;
		if(!node.isParent&&node.upId == addNodes[i])
			return true;
		if(!node.isParent&&node.upId == "0")
			return true;
	}
	return false;
}

function getFreshNode(result){
	var canAddNode = [] ;
	for(var i = 0 ;i <result.length;i++){
		var node = leftTree.getNodeByParam("id", result[i].id, null);
		if(!node)
		{
			canAddNode.push(result[i].id);
		}
	}
	return canAddNode;
}
		//展开全部节点
		function openAllNodes(treeObj) {
			if (treeObj) {
				treeObj.expandAll(true);
			}
		}

		//折叠全部节点
		function closeAllNodes(treeObj) {
			if (treeObj) {
				treeObj.expandAll(false);
			}
		}

		//左侧combobox值改变
		function leftComboChange(value, text) {
			//刷新左侧树
			getTypeTree(value);
		}
		
		function check(){
			var objDefNo = authLeftCombobox.getValue();
			if(objDefNo=="AUTH_OBJ_USER"){
				var orgNodes = leftTree.getNodes()[0].children;
				for(var i = 0 ;i <orgNodes.length;i++){
					var leafs = orgNodes[i].children;
					var isOk = false;
					for(var j = 0 ;j<leafs.length;j++){
						if(leafs[j].checked&&isOk==false)
							isOk = true; 
					}
					if(isOk==false){
						if(operType != 'del'){
							window.parent.parent.BIONE.tip("机构["+orgNodes[i].text+"]节点["+window.parent.taskNodeNm+"]没有选择执行对象,请选择");
							return false;
						}
					}
				}
			}else{
				var orgNodes = leftTree.getNodes()[0].children;
				var isOk = false;
				for(var i = 0 ;i <orgNodes.length;i++){
					if(orgNodes[i].checked&&isOk==false)
						isOk = true; 
				}
				if(isOk==false){
					if(operType != 'del'){
						window.parent.parent.BIONE.tip("节点["+window.parent.taskNodeNm+"]没有选择执行角色,请选择");
						return false;
					}
				}
			}
			return true;
			/*
			var orgNodes = leftTree.getNodes()[0].children;
			var isOk = false;
			for(var i = 0 ;i <orgNodes.length;i++){
				//var leafs = orgNodes[i].children;
				//for(var j = 0 ;j<leafs.length;j++){
					if(orgNodes[i].checked&&isOk==false)
						isOk = true; 
				//}
			}
			if(isOk==false){
				BIONE.tip("节点["+window.parent.taskNodeNm+"]没有选择执行对象，请选择");
				return false;
			}
			return true;
			*/
		}
		
		var taskObjs = [];
		function getTaskObj(){
			taskObjs = [];
			if(check()==false)
				return "-1";
			var  taskObjType      =   window["authLeftCombobox"].getValue();
			//var  taskObjType = "AUTH_OBJ_USER";
			var checkedNodes = leftTree.getCheckedNodes();
			var taskObjIds = [];
			for(var i = 0 ;i <checkedNodes.length;i++ ){
				var nodeInfo = checkedNodes[i];
				if(nodeInfo.id!="0" && !nodeInfo.isParent){
					appendTaskObjs(checkedNodes[i].id,checkedNodes[i].text,taskObjType,nodeInfo.getParentNode().id);
				}
			}
			return {taskObjType:taskObjType,taskObjs:taskObjs};
		}
		function appendTaskObjs(taskObjId,taskObjNm,taskObjType,org){
			var taskObj = getTaskObjByOrg(org);
			if(taskObj == null){
				taskObj = {org:org,taskInfo:[]};
				taskObj.taskInfo.push({taskObjId:taskObjId,taskObjNm:taskObjNm,taskObjType:taskObjType});
				taskObjs.push(taskObj); 
			}else{
				taskObj.taskInfo.push({taskObjId:taskObjId,taskObjNm:taskObjNm,taskObjType:taskObjType});
			}
		}
		function getTaskObjByOrg(org){
			
			for(var i = 0 ;i <taskObjs.length;i++){
				if(taskObjs[i].org == org)
					return taskObjs[i];
			}
			return null;
		}
		function refreshNode(){
			if (leftTree && authLeftCombobox) {
				var objDefNo = authLeftCombobox.getValue();
				if(objDefNo == "AUTH_OBJ_USER")
					if (objDefNo && objDefNo != "") {
						getTypeTree(objDefNo);
					}
			}
		}
</script>

<title>执行对象选择</title>
</head>
<body>
	<div id="template.left.up">下发对象：&nbsp;&nbsp;</div> 
	<div id="template.left.up.right">
		<input type="text" id="template_left_combobox"></input>
	</div>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.center">
		<div id="chooseGrid" style="overflow: hidden;"></div>
	</div>
	<div id="template.center.up">已选择的对象</div>
	<div id="treeTmp"  style="overflow: hidden;"></div>
</body>
</html>