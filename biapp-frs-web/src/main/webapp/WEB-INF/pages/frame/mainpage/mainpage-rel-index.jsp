<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template4C.jsp">
<head>
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
	background: url(${ctx}/images/classics/icons/icons_label.png) no-repeat -288px -30px;
	
	position: absolute;
	top: 2px;
	right:5px;
	
	cursor: pointer;
}
</style>
<script type="text/javascript">
	var ROOT_NO = '0';
    var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png", groupicon = "${ctx}/images/classics/icons/find.png";
    var currentTabId="";
    var OBJ_DEF_USER = "AUTH_OBJ_USER";
    var checkUserIds = [];
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
		    url : "${ctx}/bione/admin/authObjUsrRel/getAuthObjTree.json",
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
			if (!result)   return;
			var curTree = eval(treeId);
			if (curTree) {
			    curTree.addNodes(curTree.getNodeByParam("id", "0", null),result, false);
			    //curTree.expandAll(true);
			    var rootNodeTmp = curTree.getNodeByParam("id", ROOT_NO , null);
			    curTree.expandNode(rootNodeTmp , true , false);
			}
		    },
		    error : function(result, b) {
				////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
		    }
		});
    }

    $(function() {
		BIONE.createButton({
		    text : '保 存',
		    width : '80px',
		    appendTo : '#bottom',
		    operNo : 'saveButton',
		    icon : 'save',
		    click : function() {
		    	if (leftTree && rightTree) {
		    		var checkedNodes = leftTree.getCheckedNodes(true);
					var checkedNodesArray = leftTree.transformToArray(checkedNodes);
					var objIds = "";
					for ( var j = 0; j < checkedNodesArray.length; j++) {
						    if (checkedNodesArray[j].id == "0"|| checkedNodesArray[j].params.cantClick || !checkedNodesArray[j].checked) {
								continue;//若是树根节点，或者节点设置了不能点击，或者是未勾选节点
						    }
						    if (objIds != "") {
								objIds += ",";
						    }
						    objIds += checkedNodesArray[j].params.id;
					}
					var checkedNodes = rightTree.getCheckedNodes(true);
					var designId = checkedNodes[0].id
			    	var objRels={
			    		designId : designId,
						userIds  :objIds
				    };
					$.ajax({
					    async : true,
					    cache : false,
					    url : "${ctx}/bione/mainpage/rel",
					    dataType : 'json',
					    type : "post",
					    data : objRels,
					    beforeSend : function() {
							BIONE.loading = true;
							BIONE.showLoading("正在加载数据中...");
					    },
					    complete : function() {
							BIONE.loading = false;
							BIONE.hideLoading();
					    },
					    success : function(result) {
							BIONE.tip('保存成功!');
					    },
					    error : function(result, b) {
							BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
					    }
					});
		    	}	
		    }
		});
	
		
		function initMpAsyc(){
			window["rightTree"]  = $.fn.zTree.init(
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
		 				    chkStyle : 'radio',
		 				    enable : true
		 				},
						view : {
							selectedMulti : false,
							showLine : false
						}
					}, [ {
						id : "0",
						text : "公共首页树",
						icon : auth_obj_root_icon,
						nocheck : true
					} ]);
			 $.ajax({
					cache : false,
					async : true,
					url : "${ctx}/bione/mainpage/rel/getMainPageTree.json",
					dataType : 'json',
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
					    if (rightTree) {
							//先移除所有旧节点
							var oldTreeNodes = rightTree.getNodes();
							var oldTreeNodesArray = rightTree
								.transformToArray(oldTreeNodes);
							for ( var j = 0; j < oldTreeNodesArray.length; j++) {
							    if (oldTreeNodesArray[j].id == '0') {
								continue;
							    }
							    rightTree.removeNode(oldTreeNodesArray[j]);
							}
							
							
							rightTree.addNodes(rightTree.getNodeByParam("id", "0",
								null), result, false);
							rightTree.expandAll(true);
							var nodesArray = rightTree.transformToArray(rightTree
								.getNodes());
							for ( var i = 0; i < nodesArray.length; i++) {
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
		function initUserAsyc(){
			window["leftTree"]  = $.fn.zTree.init(
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
						view : {
							selectedMulti : false,
							showLine : false
						},
						check : {
		 				    chkStyle : 'checkbox',
		 				    enable : true,
		 				    chkboxType : {
		 					"Y" : "s",
		 					"N" : "s"
		 				    }
		 				},
						async: {
							enable: true,
							url: "${ctx}/bione/admin/auth/getAuthObjDefTree.json",
							autoParam: ['id', 'objDefNo'],
							dataFilter: function() {
								var cNodes = arguments[2];
								$.each(cNodes, function(i, n) {
									if (n.params.cantClick && n.params.cantClick == '1') {
										n.nocheck = true;
									}
									for(var i = 0 , l = checkUserIds.length ; i < l ; i++){
										if(n.params.id == checkUserIds[i]){
											n.checked = true;
											break;
										}
									}
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
						icon : auth_obj_root_icon
					} ]);
			
		}
		initUserAsyc();
		initMpAsyc();
		refreshObjDefTree();
		
		function refreshObjDefTree(searchText) {
			var objDefNo = OBJ_DEF_USER;
			if (leftTree && leftTree.setting) {
				//先移除所有授权对象节点
				leftTree.removeChildNodes(leftTree.getNodeByParam("id", '0', null));
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
						"searchText": searchText
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
							if (n.params.cantClick && n.params.cantClick == '1') {
								n.nocheck = true;
							}
							n.realId = n.params.id;
							n.objDefNo = objDefNo;
						});
						leftTree.addNodes(leftTree.getNodeByParam("id", '0', null),
								result, true);
						//展开树
						//leftTree.expandAll(true);
						var rootNodeTmp = leftTree.getNodeByParam("id", ROOT_NO , null);
						leftTree.expandNode(rootNodeTmp , true , false);
					},
					error : function(result, b) {
						////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		}
		
		// 2015 DO
		var $left = $('#left');
		$left.css('position', 'relative');
		$left.append('<div id="lsearch" class="lsearch" style="display: none;"><div id="l-text-wrap" class="l-input-wrap"><input id="l-text-field" class="l-text-field" /></div><div id="l-search-btn" class="l-search-btn">搜索</div><div class="close" style=""></div></div>');
		$('.close').click(function() {
			$('#lsearch').hide();
		});
		
		$('#l-search-btn').click(function(){
			var text = $('#l-text-field').val();
			var objdefno = $('#template_left_combobox_val').val();
			refreshObjDefTree(text);
		});
		
		//初始化左侧菜单
		$("#treeToolbar").ligerTreeToolBar({
		    items : [ 
		    {
			line : true
		    }, {
			icon : 'orderlist',
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
		    }
		    , {
				icon : 'search',
				text : '搜索',
				click : function() {
					$('#lsearch').show();
					$('#l-text-wrap').width($('#lsearch').width() - 80);
				}
			}  
		    ],
		    treemenu : false
		});
		
		// 动态生成tab
		var objs = $.parseJSON('${authObjDefs}'), content = new Array();
		for (var i = 0, l = objs.length; i < l; i++) {
			content.push('<div tabid="' + objs[i].objDefNo + '" title="' + objs[i].objDefName + '" lselected="true" style="overflow: auto;">');
			content.push('	<div id="container1" style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">');
			content.push('		<div id="' + objs[i].objDefNo + '_tree" class="ztree" style="font-size: 12; background-color: #FFFFFF; width: 95%"></div>');
			content.push('	</div>');
			content.push('</div>');
		}
		$("#navtab").append(content.join(''));
		
		// 构建授权对象tab及树
		window['navtab'] = $("#navtab").ligerTab();
		// 循环tab构建相应树
		var tabIds = navtab.getTabidList();
		if (tabIds && tabIds.length > 0) {
		    for ( var i = 0; i < tabIds.length; i++) {
			var objDefNo = tabIds[i];
			var treeId = tabIds[i] + "_tree";
			if ($("#" + treeId)) {
			    window[treeId] = $.fn.zTree.init($("#" + treeId), {
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
						async: {
							enable: true,
							url: "${ctx}/bione/admin/authObjUsrRel/getAuthObjTree.json",
							autoParam: ['id'],
							otherParam: {
								objDefNo: objDefNo
							}
						},
						callback : {
							beforeClick : function(treeId, treeNode,clickFlag) {
							    if (treeNode.id == "0") {
									//若是根节点
									return false;
							    }
							},
							onClick : function(event, treeId, treeNode,clickFlag) {
							    //获取指定对象关系勾选
							    var objId = treeNode.params.id;
							     $.ajax({
										cache : false,
										async : true,
										url : "${ctx}/bione/admin/authObjUsrRel/getObjUserRel.json",
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
											if(leftTree){
												leftTree.checkAllNodes(false);
											}
										    // 清空缓存
											checkUserIds = [];
											if (!result){  
												return;
											}
											if(leftTree){
											    for ( var i = 0; i < result.length; i++) {
													    var objDefNo = result[i].id.objDefNo;
													    //勾选相应节点
													    var node = leftTree.getNodeByParam("realId",result[i].id.userId,null);
													    if (!node|| typeof node == 'undefined') {
															   checkUserIds.push(result[i].id.userId);
														       continue;
													    }
													    leftTree.checkNode(node,true,true,false);
											    }
											}
										},
										error : function(result, b) {
										    BIONE.tip('发现系统错误 <BR>错误码：'+ result.status);
										}
								    });  
							} 
			           } 
				    },
				    [ {
						id : "0",
						text : "授权对象树",
						icon : auth_obj_root_icon,
						nocheck : true,
						isParent: true
				      } 
				    ]
			    );
			    //初始化树
			    //initAuthObjTree(objDefNo, treeId);
				
			    var rightHeight = $("#right").height();
				var $rightTreeContainer = $("#" + treeId);
				$rightTreeContainer.height(rightHeight - 41);
			
			
			}
		    }
		}
    })
</script>
</head>
<body>
	<div id="template.left.up.icon"> 
		<i class = "icon-circle-my search-size"></i>
	</div>
	<div id="template.left.up">授权用户</div>
	<div id="template.center">
		<div id="navtab" style="overflow: hidden;"></div>
	</div>
	<div id="template.right.up.icon">
		<i class = "icon-guide_2 search-size"></i>
	</div>
	<div id="template.right.up">公共首页</div>
</body>
</html>