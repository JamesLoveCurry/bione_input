<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template4A.jsp">
<script type="text/javascript">
	var ROOT_NO = '0';
	//授权资源根节点图标
	var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";
	//记录当前点击的授权对象id
	var selectedObjId = "";
	//初始化选中的对象
	var selectedObjs = '${selectedObjs}';
	var parentRowIndex = '${parentRowIndex}';

	$(function() {
		initTreeToolBar();
		initChooseTree();
		initChooseList();
		initBtn();
	});
	
	function initBtn(){
		//初始化按钮
		var btns = [{
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("onchoseExeObjs");
			}
		},{
			text : '保存',
			onclick : function(){
				var taskObjIds=[],taskObjNms=[],taskObjTypes=[];
				var rows = grid.rows;
				if(rows==null ||rows.length<=0)return ;
				
				for(var i=0;i<rows.length;i++){
					taskObjIds[i]=rows[i].taskObjId;
					taskObjNms[i]=rows[i].taskObjNm;
					taskObjTypes[i]=rows[i].taskObjType;
					
				}
				//index,ids,nms,types
				parent.refreshRowContent(parentRowIndex,taskObjIds,taskObjNms,taskObjTypes);
				

				BIONE.closeDialog("onchoseExeObjs");
			}
		}];
		BIONE.addFormButtons(btns);
	}

	function initChooseList() {
		grid = $("#chooseGrid")
				.ligerGrid(
						{
							display : '已选择的对象',
							columns : [
									{
										display : '任务对象名称',
										name : 'taskObjNm',
										align : 'left',
										width : '30%'
									},
									{
										display : '任务对象类型',
										name : 'taskObjType',
										align : 'left',
										width : '30%',
										render : function(row) {
											if(row.taskObjType=="01")//AUTH_OBJ_USER
												return "用户";
											if(row.taskObjType=="02")//AUTH_OBJ_ROLE
												return "角色";
											if(row.taskObjType=="03")//AUTH_OBJ_POSI
												return "岗位";
											if(row.taskObjType=="04")//AUTH_OBJ_DEPT
												return "部门";
										}
									},
									{
										display : '操作',
										name : 'taskObjId',
										width : '20%',
										render : function(row) {
											return "<a href='javascript:void(0)' class='link' onclick='deleteChoose(\""
													+ arguments[1]
													+ "\")'>"
													+ '删除'
													+ "</a>";
										}
									} ],
							checkbox : false,
							usePager : false,
							toolbar : {},
							colDraggable : true
						});
	}

	function deleteChoose(i) {
		var row = grid.getRow(i);
		grid.deleteRow(row);
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
									onSelectTree(treeNode);
								},
							}
						}, [ {
							id : "0",
							text : "执行对象树",
							icon : auth_obj_root_icon
						} ]);

		$
				.ajax({
					cache : false,
					async : true,
					url : "${ctx}/bione/admin/auth/getAuthResDefTabs.json?d="
							+ new Date().getTime(),
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
						var data = null;
						if (result) {
							data = result.Data;
						}
						if (data && data.length > 0) {
							for ( var i = 0; i < data.length; i++) {
								var appendHtml = "<div style='overflow: auto;'><div id='"
										+ data[i].resDefNo
										+ "_Container' style='width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;'><div id='authRes_"
										+ data[i].resDefNo
										+ "' class='ztree' style='font-size: 12; background-color: #FFFFFF; width: 95%''></div></div></div>";
								var subTreeId = "#authRes_" + data[i].resDefNo;

								if (eval($(subTreeId))) {
									//构造资源树	
									var resDefNoTmp = data[i].resDefNo;
									window['resTree_' + data[i].resDefNo] = $.fn.zTree
											.init(eval($(subTreeId)), {
												check : {
													chkStyle : 'checkbox',
													enable : true,
													chkboxType : {
														"Y" : "ps",
														"N" : "s"
													}
												},
												data : {
													key : {
														name : 'text'
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
												callback : {
													onCheck : function(event,
															treeId, treeNode) {
													}
												}
											});

									//构造高度
									var treeContainer = data[i].resDefNo
											+ "_Container";
									if ($("#" + treeContainer) && $("#center")) {
										$("#" + treeContainer).height(
												$("#center").height() - 28);
									}

									window['resTreeInit_' + data[i].resDefNo] = function(
											resObjNo) {
										var data = {
											"resDefNo" : resObjNo
										};
										//var rootTmp = eval('resTree_'+ resObjNo).getNodesByParam("id", '0', null);
										//eval('resTree_'+ resObjNo).removeChildNodes(rootTmp);
										//eval('resTree_'+ resObjNo).removeNode(rootTmp[0]);
										
									};
									//追加节点
									if (eval('resTreeInit_' + data[i].resDefNo)) {
										eval('resTreeInit_' + data[i].resDefNo
												+ "('" + data[i].resDefNo
												+ "');");
									}

								}
							}
						}
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
		//临时树，备份缓存使用
		window['treeTmp'] = $.fn.zTree.init($("#treeTmp"), {
			check : {
				chkStyle : 'checkbox',
				enable : true,
				chkboxType : {
					"Y" : "ps",
					"N" : "ps"
				}
			},
			data : {
				key : {
					name : 'text'
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
			}
		}, [ {
			id : "0",
			text : "临时树"
		} ]);

		window['authLeftCombobox'] = $("#template_left_combobox")
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
									authLeftCombobox.selectValue(data[0].id);
									//加载相应授权对象树
									if (leftTree && leftTree.setting) {
										refreshObjDefTree(data[0].id)
									}
								}
							}
						});
		//选择授权对象事件
		authLeftCombobox.bind('beforeSelect', function(value, text) {
			//刷新授权对象树
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
	// 加工图标
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
					if (leftTree && authLeftCombobox) {
						var objDefNo = authLeftCombobox.getValue();
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
	//刷新左侧授权对象树
	function refreshObjDefTree(objDefNo) {
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
					"objDefNo" : objDefNo
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
					leftTree.addNodes(leftTree.getNodeByParam("id", '0', null),
							result, true);
					//展开树
					//leftTree.expandAll(true);
					var rootNodeTmp = leftTree.getNodeByParam("id", ROOT_NO,
							null);
					leftTree.expandNode(rootNodeTmp, true, false);
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
	function leftComboChange(value, text) {
		//刷新左侧树
		refreshObjDefTree(value);
	}
	
	function onSelectTree(treeData){
		var authBoxValue = authLeftCombobox.getValue();
		var chooseType = "";
		if(authBoxValue=="AUTH_OBJ_USER")
			chooseType="01";
		if(authBoxValue=="AUTH_OBJ_ROLE")
			chooseType="02";
		if(authBoxValue=="AUTH_OBJ_POSI")
			chooseType="03";
		if(authBoxValue=="AUTH_OBJ_DEPT")
			chooseType="04";
		grid.addRow(
				{
					taskObjNm:treeData.text,
					taskObjType:chooseType,
					taskObjId:treeData.id
				}
		);
	}
	
</script>

<title>指标信息</title>
</head>
<body>
	<div id="template.left.up">授权对象</div>
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