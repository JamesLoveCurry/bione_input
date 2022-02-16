<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template6_BS.jsp">
<head>
<style type="text/css">
</style>
<style type="text/css">
.lsearch {
	height: 26px;
	position: absolute;
	left: 0px;
	top: 31px;
	width: 100%;
	background-color: #f0f0f0;
	z-index: 1000;
}

.l-input-wrap {
	position: relative;
	height: 20px;
	width: 60%;
	margin: 2px 2px 0 2px;
	border: 1px solid #CCC;
	float: left;
}

.l-input-wrap input {
	height: 20px;
	width: 100%;
}

.l-search-btn {
	height: 20px;
	width: 50px;
	border: 1px solid #CCC;
	float: left;
	margin: 2px 5px 0 0;
	line-height: 20px;
	text-align: center;
	cursor: pointer;
	background-color: white;
	border-radius: 2px;
}

.close {
	width: 10px;
	height: 10px;
	background: url(${ctx}/images/classics/icons/icons_label.png) no-repeat
		-288px -30px;
	position: absolute;
	top: 2px;
	right: 5px;
	cursor: pointer;
}
</style>
<script type="text/javascript">
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png", groupicon = "${ctx}/images/classics/icons/find.png";
	var ROOT_NO = '0';
	var OBJ_DEF_USER = "AUTH_OBJ_USER";
	var checkObjs = [];
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

	//初始化授权对象树
	function initAuthObjTree(objDefNo, treeId) {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/bione/admin/authUsrRel/getAuthObjTree.json",
			dataType : 'json',
			data : {
				objDefNo : objDefNo
			},
			type : "post",
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
				var curTree = eval(treeId);
				if (curTree) {
					for (var i = 0; i < result.length; i++) {
						if (result[i].id != "0") {
							result[i].objId = result[i].params.id;
							if (result[i].params.cantClick) {
								result[i].nocheck = true;
							}
						}
					}
					curTree.addNodes(curTree.getNodeByParam("id", "0", null),
							result, false);
					//curTree.expandAll(true);
					var rootNodeTmp = curTree.getNodeByParam("id", ROOT_NO,
							null);
					curTree.expandNode(rootNodeTmp, true, false);
				}
			},
			error : function(result, b) {
				////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	$(function() {
		BIONE
				.createButton({
					text : '保 存',
					width : '80px',
					appendTo : '#bottom',
					operNo : 'saveButton',
					icon : 'save',
					click : function() {
						if (leftTree) {
							var leftSelNodes = leftTree.getSelectedNodes();
							if (!leftSelNodes.length
									|| leftSelNodes.length <= 0) {
								//若没有选择授权对象
								$.ligerDialog.warn('请选择用户');
								return;
							} else {
								var leftSelNode = leftSelNodes[0];
								if (leftSelNode.id == '0') {
									//若选择的是根节点
									$.ligerDialog.warn('请选择用户');
									return;
								} else {
									if (navtab) {
										var objs = new Array();
										var tabIdList = navtab.getTabidList();
										for (var i = 0; i < tabIdList.length; i++) {
											var objDefNo = tabIdList[i];
											var objIds = "";
											var defTree = window[objDefNo
													+ "_tree"];

											if (eval(objDefNo + "_tree")) {
												var checkedNodes = eval(
														objDefNo + "_tree")
														.getCheckedNodes(true);
												var checkedNodesArray = eval(
														objDefNo + "_tree")
														.transformToArray(
																checkedNodes);
												for (var j = 0; j < checkedNodesArray.length; j++) {
													if (checkedNodesArray[j].id == "0"
															|| checkedNodesArray[j].params.cantClick
															|| !checkedNodesArray[j].checked
															|| !checkedNodesArray[j].params.id) {
														//若是树根节点，或者节点设置了不能点击，或者是未勾选节点
														continue;
													}
													if (objIds != "") {
														objIds += ",";
													}
													objIds += checkedNodesArray[j].params.id;
												}
												var node = {};
												$
														.each(
																leftSelNode.result,
																function(k, n) {
																	node = defTree
																			.getNodesByParam(
																					'id',
																					n.id.objId);
																	if (n.id.objDefNo == objDefNo
																			&& !node) {
																		if (objIds != "") {
																			objIds += ",";
																		}
																		objIds += n.id.objId;
																	}
																});
												if (objIds != "") {
													objs.push({
														objDefNo : objDefNo,
														objIds : objIds
													});
												}

											}
										}
										if (checkObjs != null
												&& checkObjs.length > 0) {
											for (var m = 0, n = checkObjs.length; m < n; m++) {
												objs
														.push({
															objDefNo : checkObjs[m].id.objDefNo,
															objIds : checkObjs[m].id.objId
														});
											}
										}
										var relObjs = {
											userId : leftSelNode.params.id,
											objs : objs
										};
										$
												.ajax({
													async : true,
													cache : false,
													url : "${ctx}/bione/admin/authUsrRel/saveObjUserRel",
													dataType : 'json',
													type : "post",
													data : {
														relObjs : JSON2
																.stringify(relObjs),
														userOrgNo : leftSelNode.data.orgNo
													},
													beforeSend : function() {
														BIONE.loading = true;
														BIONE
																.showLoading("正在加载数据中...");
													},
													complete : function() {
														BIONE.loading = false;
														BIONE.hideLoading();
													},
													success : function(result) {
														BIONE.tip('保存成功!');
													},
													error : function(result, b) {
														BIONE
																.tip('保存失败,发现系统错误 <BR>错误码：'
																		+ result.status);
													}
												});
									}
								}
							}
						}
					}
				});

		window["leftTree"] = $.fn.zTree.init($("#leftTree"), {
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
			view : {
				selectedMulti : false,
				showLine : false
			},
			callback : {
				beforeClick : function(treeId, treeNode, clickFlag) {
					if (treeNode.id == "0") {
						//若是根节点
						return false;
					}
				},
				onClick : clickHandler
			},
			async : {
				enable : true,
				url : "${ctx}/bione/admin/auth/getAuthObjDefTree.json",
				autoParam : [ 'id', 'objDefNo' ],
				dataFilter : function() {
					var cNodes = arguments[2];
					$.each(cNodes, function(i, n) {
						n.realId = n.params.id;
						n.userId = n.params.id;
						n.objDefNo = OBJ_DEF_USER;
					});
					return cNodes;
				}
			}
		}, [ {
			id : "0",
			text : "用户树",
			icon : auth_obj_root_icon,
			nocheck : true
		} ]);

		function refreshObjDefTree(searchText) {
			var objDefNo = OBJ_DEF_USER;
			if (leftTree && leftTree.setting) {
				//先移除所有授权对象节点
				leftTree.removeChildNodes(leftTree.getNodeByParam("id", '0',
						null));
				//查询该授权对象并更新树
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/bione/admin/auth/getAuthObjDefTree.json?d="
							+ new Date().getTime(),
					dataType : 'json',
					type : "post",
					data : {
						"objDefNo" : objDefNo,
						"searchText" : searchText
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
						$.each(result,
								function(i, n) {
									if (n.params.cantClick
											&& n.params.cantClick == '1') {
										n.nocheck = true;
									}
									n.realId = n.params.id;
									n.objDefNo = objDefNo;
								});
						leftTree.addNodes(leftTree.getNodeByParam("id", '0',
								null), result, true);
						//展开树
						//leftTree.expandAll(true);
						var rootNodeTmp = leftTree.getNodeByParam("id",
								ROOT_NO, null);
						leftTree.expandNode(rootNodeTmp, true, false);
					},
					error : function(result, b) {
						////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		}

		function clickHandler(event, treeId, treeNode, clickFlag) {
			//获取指定用户关系勾选
			var objId = treeNode.params.id;
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/bione/admin/authUsrRel/getObjUserRel.json",
				dataType : 'json',
				data : {
					objId : objId
				},
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					//清除所有树之前选中
					if (navtab) {
						var tabIdList = navtab.getTabidList();
						for (var i = 0; i < tabIdList.length; i++) {
							var objDefNo = tabIdList[i];
							if (eval(objDefNo + "_tree")) {
								eval(objDefNo + "_tree").checkAllNodes(false);
							}
						}
					}
					if (!result)
						return;
					treeNode.result = result;
					for (var i = 0; i < result.length; i++) {
						var objDefNo = result[i].id.objDefNo;
						if ($("#" + objDefNo + "_tree").length > 0) {
							//勾选相应节点
							var node = eval(objDefNo + "_tree").getNodeByParam(
									"realId", result[i].id.objId, null);
							if (!node || typeof node == 'undefined') {
								checkObjs.push(result[i]);
								continue;
							}

							//这一句是用来将多选框选中的吗。
							eval(objDefNo + "_tree").checkNode(node, true,
									true, false);
						}
					}
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}

		//这个是为了实现级联 点击父节点，子节点自动全部选中，但是我觉得并不好。
		function treenodeClick(event, treeId, treeNode, clickFlag) {
			//此处treeNode 为当前节点
			var str = '';
			var treeName = treeId;
			if(treeNode.checked == true){
				str = getAllChildrenNodes(treeNode,treeId,true);
			}else{
				str = getAllChildrenNodes(treeNode,treeId,false);
			}
			// 加上被选择节点自己
			str = str + ',' + treeNode.id;
			// 去掉最前面的逗号
			var ids = str.substring(1, str.length);
			// 得到所有节点ID 的数组
			var idsArray = ids.split(',');
			// 得到节点总数量
			var length = idsArray.length;

			// 做业务操作
		}

		// 递归，获取所有子节点
		function getAllChildrenNodes(treeNode,treeId,flag) {
			var result = "";
			eval(treeId + "").checkNode(treeNode, flag, true, false);//勾选或取消单个节点
			
			if (treeNode.isParent) {
				var childrenNodes = treeNode.children;
				if (childrenNodes) {
					for (var i = 0; i < childrenNodes.length; i++) {
						
						result += ',' + childrenNodes[i].id;
						result = getAllChildrenNodes(childrenNodes[i], treeId, flag);
					}
				}
			}
			return result;
		}

		//初始化用户树
		function initUserTree(searchText) {
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/bione/admin/authUsrRel/getUserTree.json",
				dataType : 'json',
				type : "post",
				data : {
					'searchText' : searchText
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
					if (leftTree) {
						//先移除所有旧节点
						var oldTreeNodes = leftTree.getNodes();
						var oldTreeNodesArray = leftTree
								.transformToArray(oldTreeNodes);
						for (var j = 0; j < oldTreeNodesArray.length; j++) {
							if (oldTreeNodesArray[j].id == '0') {
								continue;
							}
							leftTree.removeNode(oldTreeNodesArray[j]);
						}
						leftTree.addNodes(leftTree.getNodeByParam("id", "0",
								null), result, false);
						//leftTree.expandAll(true);
						var rootNodeTmp = leftTree.getNodeByParam("id",
								ROOT_NO, null);
						leftTree.expandNode(rootNodeTmp, true, false);
						var nodesArray = leftTree.transformToArray(leftTree
								.getNodes());
						for (var i = 0; i < nodesArray.length; i++) {
							if (nodesArray[i].id != "0") {
								nodesArray[i].objId = nodesArray[i].params.id;
							}
						}
					}
				},
				error : function(result, b) {
					////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
		// 		initUserTree();
		refreshObjDefTree();
		// 2015 DO
		var $left = $('#left');
		$left.css('position', 'relative');
		$left
				.append('<div id="lsearch" class="lsearch" style="display: none;"><div id="l-text-wrap" class="l-input-wrap"><input id="l-text-field" class="l-text-field" /></div><div id="l-search-btn" class="l-search-btn">搜索</div><div class="close" style=""></div></div>');
		$('.close').click(function() {
			$('#lsearch').hide();
		});

		$('#l-search-btn').click(function() {
			var text = $('#l-text-field').val();
			// 			initUserTree(text);
			refreshObjDefTree(text);
		});
		$('#l-text-field').keydown(function(e) {
			if (e.keyCode == 13) {
				var text = $('#l-text-field').val();
				refreshObjDefTree(text);
			}
			return e;
		});
		//初始化左侧菜单
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				line : true
			}, {
				icon : 'add',
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
			}, {
				icon : 'search',
				text : '搜索',
				click : function() {
					$('#lsearch').show();
					$('#l-text-wrap').width($('#lsearch').width() - 80);
				}
			} ],
			treemenu : false
		});

		// 动态生成tab
		var objs = $.parseJSON('${authObjDefs}'), content = new Array();
		for (var i = 0, l = objs.length; i < l; i++) {
			content
					.push('<div tabid="' + objs[i].objDefNo + '" title="' + objs[i].objDefName + '" lselected="true" style="overflow: auto;">');
			content
					.push('	<div id="container1" style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">');
			content
					.push('		<div id="'
							+ objs[i].objDefNo
							+ '_tree" class="ztree" style="font-size: 12; background-color: #FFFFFF; width: 95%"></div>');
			content.push('	</div>');
			content.push('</div>');
		}
		$("#navtab").append(content.join(''));

		// 构建授权对象tab及树
		window['navtab'] = $("#navtab").ligerTab();
		// 循环tab构建相应树
		var tabIds = navtab.getTabidList();
		if (tabIds && tabIds.length > 0) {
			for (var i = 0; i < tabIds.length; i++) {
				var objDefNo = tabIds[i];
				var treeId = tabIds[i] + "_tree";
				if ($("#" + treeId)) {
					window[treeId] = $.fn.zTree
							.init(
									$("#" + treeId),
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
										view : {
											selectedMulti : false,
											showLine : true
										},
										check : {
											chkStyle : 'checkbox',
											enable : true,
											chkboxType : {
												"Y" : "",
												"N" : ""
											}
											//实现级联
											//autoCheckTrigger: true  
										},
										callback : {
											onCheck : treenodeClick
										},
										async : {
											enable : true,
											url : "${ctx}/bione/msg/announcement/getAuthObjTree.json",
											autoParam : [ 'id' ],
											otherParam : {
												objDefNo : objDefNo
											},
											dataFilter : function(id, pNode,
													cNodes) {
												$
														.each(
																cNodes,
																function(i, n) {
																	if (n.params.cantClick
																			&& n.params.cantClick == '1') {
																		n.nocheck = true;
																	}
																	n.realId = n.params.id;
																});
												if (leftTree) {
													var leftSelNodes = leftTree
															.getSelectedNodes();
													var leftSelNode = leftSelNodes
															&& leftSelNodes.length > 0 ? leftSelNodes[0]
															: null;
													if (leftSelNode
															&& leftSelNode.result) {
														var r = {};
														$
																.each(
																		leftSelNode.result,
																		function(
																				i,
																				n) {
																			r[n.id.objId] = n;
																		});
														var newArrayTmp = [];
														$
																.each(
																		cNodes,
																		function(
																				i,
																				n) {
																			if (r[n.realId ? n.realId
																					: n.id]) {
																				n.checked = true;
																			}
																		});
														f1: for (var j = 0, l = checkObjs.length; j < l; j++) {
															f2: for (var j2 = 0, l2 = cNodes.length; j2 < l2; j2++) {
																if (checkObjs[j].id.objId == (cNodes[j2].realId ? cNodes[j2].realId
																		: cNodes[j2].id)) {
																	continue f1;
																}
															}
															newArrayTmp
																	.push(checkObjs[j]);
														}
														checkObjs = newArrayTmp;
													}
												}
												return cNodes;
											}
										}
									}, [ {
										id : "0",
										text : "授权对象树",
										icon : auth_obj_root_icon,
										nocheck : true,
										isParent : true
									} ]);
					//初始化树
					// 			    initAuthObjTree(objDefNo, treeId);
					var t = $.fn.zTree.getZTreeObj(treeId);
					var r = t.getNodesByParam('id', '0');
					r = r.length > 0 ? r[0] : null;
					if (r) {
						t.reAsyncChildNodes(r, 'refresh', false);
					}
					var rightHeight = $("#right").height();
					var $rightTreeContainer = $("#" + treeId);
					$rightTreeContainer.height(rightHeight - 50);
				}
			}
		}
	})
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<i class="icon-circle-my search-size"></i>
	</div>
	<div id="template.left.up">授权用户</div>
	<div id="template.right">
		<div id="navtab" style="overflow: hidden;"></div>
	</div>
</body>
</html>