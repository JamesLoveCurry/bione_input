<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template4C.jsp">
<head>
<script type="text/javascript">

		var ROOT_NO = '0';
		var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png", groupicon = "${ctx}/images/classics/icons/find.png";
		var orgNo;
		var taskObjType;
		var selectedTaskObjIds=[];
		var taskObjIdMap;
		var rightTreeIsComp = false;
		$(function() {
			window.parent.frameObj = window;
			initBaseInfo();
			initTreeToolBar();
			initLeftTree();
		});
		
		function initBaseInfo(){
			if(window.parent.taskUnifyNode&&window.parent.taskUnifyNode.taskObjIdMap){
				 taskObjIdMap =  window.parent.taskUnifyNode.taskObjIdMap;
				orgNo =  window.parent.taskUnifyNode.orgNo;
				taskObjType =  window.parent.taskUnifyNode.taskObjType;
				if(taskObjIdMap){
					for(var i = 0 ;i <taskObjIdMap.length;i++){
						selectedTaskObjIds.push(taskObjIdMap[i].id);
					}
				}
			}
		}
		
		function initComponent(){
			window['authrightCombobox'] = $("#template_right_combobox")
			.ligerComboBox(
					{
						url : "${ctx}/rpt/input/task/getAuthObjCombo.json?d="
								+ new Date().getTime(),
						ajaxType : "post",
						labelAligh : "center",
						slide : false,

						onSuccess : function(data) {
							if (data && data.length > 0) {
								//初始化后设置combobox默认值
								authrightCombobox.selectValue(data[0].id);
								//加载相应授权对象树
								if (rightTree && rightTree.setting) {
									if(rightTreeIsComp)
									refreshObjDefTree(data[0].id)
								}
							}
						}
					});
			authrightCombobox.bind('beforeSelect', function(value, text) {
				//刷新授权对象树
				if (rightTree) {
					if (!rightTree.getSelectedNodes().length
							|| rightTree.getSelectedNodes().length <= 0) {
						rightComboChange(value, text);
						return true;
					}
				}
				authRightCombobox.selectValue(value);
				leftComboChange(value, text);
				rightTree.cancelSelectedNode(rightTree
						.getNodeByParam("id", '0', null));
				//清空缓存
				permissionTreeDatas = new Array();
				return false;
			}, this);
			if(orgNo){
				var newNode = leftTreeObj.getNodeByParam("id",orgNo);
				leftTreeObj.selectNode(newNode);
			}
			if(taskObjType){
				authrightCombobox.setValue(taskObjType)
			}
		}
		
		function isCheckNode(id){
			if(taskObjType&&taskObjType == authrightCombobox.getValue()&& taskObjIdMap){
				//taskObjId
				for(var i = 0 ;i <taskObjIdMap.length;i++){
					if(id == taskObjIdMap[i].taskObjId)
						return true;
				}
				return false;
			}
		}
		var openOrgs;
		function initLeftTree(){
			//var height = $("#left").height()-100;
			if(orgNo &&orgNo !=""){

				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/input/task/getParentOrgIds.json?d="+new Date(),
					dataType : 'json',
					type : "post",
					data : {
						orgNo : orgNo
					},
					success : function(result) {
						openOrgs = result;
						initOrgTree();
						
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}else
				initOrgTree();
		}
		
		function initOrgTree(){

			var setting = {
					async : {
						enable : true,
						url : "${ctx}/report/frame/datashow/idx/getOrgTree?t="
								+ new Date().getTime(),
						autoParam : [ "id" ],
						dataType : "json",
						type : "post",
						dataFilter : function(treeId, parentNode, childNodes) {
							if (childNodes) {
								for ( var i = 0; i < childNodes.length; i++) {
									childNodes[i].isParent = true;
								}
							}
							return childNodes;
						}
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
						onClick: function(event, treeId, treeNode){
							var objDefNo = authrightCombobox.getValue();
							if (objDefNo && objDefNo != "") {
								refreshObjDefTree(objDefNo);
							}
						},
						onNodeCreated : function(event, treeId, treeNode) {
							if (treeNode.upId == null) {
								//若是根节点，展开下一级节点
								leftTreeObj.expandNode(treeNode , true , false);
								//leftTreeObj.expand(treeNode, true, true, true);
							}else 	if(isOpenNode(treeNode.id))
								leftTreeObj.expandNode(treeNode , true , false);
							
							if(treeNode.id == orgNo)
							{
								leftTreeObj.selectNode(treeNode);
								var objDefNo = authrightCombobox.getValue();
								if (objDefNo && objDefNo != "") {
									refreshObjDefTree(objDefNo);
								}
							}
						}
					}
				};
				leftTreeObj = $.fn.zTree.init($("#leftTree"), setting);

				initComponent();
				initRightTree();
		}

		function isOpenNode(org){
			if(openOrgs){
				for(var i = 0 ;i <openOrgs.length;i++){
					if(org == openOrgs[i])
						return true;
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
						if (rightTree && authrightCombobox) {
							var objDefNo = authrightCombobox.getValue();
							if (objDefNo && objDefNo != "") {
								refreshObjDefTree(objDefNo);
							}
						}
					}
				}, {
					line : true
				}, {
					icon : 'plus',
					text : '展开',
					click : function() {
						openAllNodes(rightTree);
					}
				}, {
					line : true
				}, {
					icon : 'lock',
					text : '折叠',
					click : function() {
						closeAllNodes(rightTree);
					}
				} ],
				treemenu : false
			});
		}

		function initRightTree() {
			window['rightTree'] = $.fn.zTree
					.init(
							$("#rightTree"),
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
								callback : {
									onNodeCreated : function(event, treeId, treeNode) {
										if(isCheckNode(treeNode.id)){
											rightTree.checkNode(treeNode, true, false);
										}
									}
								},
								view : {
									selectedMulti : false,
									showLine : false
								},
								async : {
									enable : true,
									url : "${ctx}/bione/admin/auth/getAuthObjDefTree.json",
									autoParam : [ 'id', 'objDefNo' ],
									dataFilter : function() {
										var cNodes = arguments[2];
										$
												.each(
														cNodes,
														function(i, n) {
															n.objDefNo = $(
																	'input[name=template_right_combobox_val]')
																	.val();
														});
										return cNodes;
									}
								}
					}, [ {
						id : "0",
						text : "执行对象树",
						icon : auth_obj_root_icon
					}]);

		

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
	function initTreeToolBar() {

		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'refresh',
				text : '刷新',
				click : function() {
					if (rightTree && authrightCombobox) {
						var objDefNo = authrightCombobox.getValue();
						if (objDefNo && objDefNo != "") {
							refreshObjDefTree(objDefNo);
						}
					}
				}
			}, {
				line : true
			}, {
				icon : 'plus',
				text : '展开',
				click : function() {
					openAllNodes(rightTree);
				}
			}, {
				line : true
			}, {
				icon : 'lock',
				text : '折叠',
				click : function() {
					closeAllNodes(rightTree);
				}
			} ],
			treemenu : false
		});
	}
	//刷新左侧授权对象树
	function refreshObjDefTree(objDefNo) {
		var selectedNodes = leftTreeObj.getSelectedNodes();
		if(selectedNodes.length==0){
			return ;
		}
		var orgNo = selectedNodes[0].id;
		if (rightTree && rightTree.setting) {
			//先移除所有授权对象节点
			rightTree.removeChildNodes(rightTree
					.getNodeByParam("id", '0', null));
			//查询该授权对象并更新树
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/rpt/input/task/getTypeTree.json?d="
						+ new Date().getTime(),
				dataType : 'json',
				type : "post",
				data : {
					"objDefNo" : objDefNo,"orgNo":orgNo
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
					$.each(result, function(i, n) {
						n.objDefNo = objDefNo;
					});
					rightTree.addNodes(rightTree
							.getNodeByParam("id", '0', null), result, true);
					//展开树
					//rightTree.expandAll(true);
					rootNodeTmp = rightTree.getNodeByParam("id", ROOT_NO,
							null);
					rightTree.expandNode(rootNodeTmp, true, false);
					
					
					if(!rightTreeIsComp)
					{
						rightTreeIsComp = true;
						window.parent.parent.doAfterFrameDone(window.parent.name);
					}
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
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
	function rightComboChange(value, text) {
		//刷新左侧树
		refreshObjDefTree(value);
	}

	function onSelectTree(treeData) {
		var authBoxValue = authrightCombobox.getValue();
		var chooseType = "";
		if (authBoxValue == "AUTH_OBJ_USER")
			chooseType = "01";
		if (authBoxValue == "AUTH_OBJ_ROLE")
			chooseType = "02";
		if (authBoxValue == "AUTH_OBJ_POSI")
			chooseType = "03";
		if (authBoxValue == "AUTH_OBJ_DEPT")
			chooseType = "04";
		grid.addRow({
			taskObjNm : treeData.text,
			taskObjType : chooseType,
			taskObjId : treeData.id
		});
	}
	

	
	function getTaskObj(){
		var orgNo = leftTreeObj.getSelectedNodes()[0].id;
		var  taskObjType      =   window["authrightCombobox"].getValue();
		var checkedNodes = rightTree.getCheckedNodes();
		var taskObjIds = [];
		for(var i = 0 ;i <checkedNodes.length;i++ ){
			var nodeInfo = checkedNodes[i];
			if(nodeInfo.id!="0"){
				taskObjIds.push({taskObjId:checkedNodes[i].id,taskObjNm:checkedNodes[i].text,taskObjType:checkedNodes[i].objDefNo});
			}
		}
		//var taskObjId = leftTree.getSelectedNodes()[0].id;
		return {taskObjType:taskObjType,taskObjIds:taskObjIds,orgNo:orgNo};
	}
	
</script>

<title>执行对象选择</title>
</head>
<body>
	<div id="template.left.up">选择机构</div>
	<div id="template.right.up.right">
		<input type="text" id="template_right_combobox"></input>
	</div>
	<div id="template.right.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.center">
		<div id="chooseGrid" style="overflow: hidden;"></div>
	</div>
	<div id="template.center.up">已选择的对象</div>
	<div id="treeTmp"  style="overflow: hidden;"></div>
</body>
</html>