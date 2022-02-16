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
	//初始化选中的对象
	var selectedObjs =JSON2.parse( '${orgInfos}');
	var rownum = '${rownum}';
	var selectedObjsIds= [];

	$(function() {
		initTreeToolBar();
		initChooseTree();
		initChooseList();
		initBtn();
		initChooseData();
		refreshObjDefTree();
	});
	
	function initChooseData(){
		if(selectedObjs&&selectedObjs!=null){
			for(var i = 0 ;i <selectedObjs.length;i++){
				var objInfo = selectedObjs[i];
				appendListRow(objInfo.orgId,objInfo.orgNm);
			}
		}
	}
	
	function initBtn(){
		//初始化按钮
		var btns = [{
			text : '取消',
			onclick : function() {
				window.parent.BIONE.closeDialog("selectOrgBox");
			}
		},{
			text : '选择',
			onclick : function(){
				var rows = grid.rows;
				if(rows==null || rows.length==0)
				{
					BIONE.tip("请选择要下发的机构");
					return ;
				}
				var selectedOrgs = [];
				for(var i = 0 ; i<rows.length;i++){
					selectedOrgs.push( {orgId:rows[i].orgId,orgNm:rows[i].orgName});
				}
				window.parent.taskManage.templateManage.refreshTable(selectedOrgs,rownum);
				BIONE.closeDialog("selectOrgBox");
			}
		}];
		BIONE.addFormButtons(btns);
	}

	function initChooseList() {
		grid = $("#chooseGrid")
				.ligerGrid(
						{
							display : '已选择的机构',
							columns : [{
										display : '机构名称',
										name : 'orgName',
										align : 'left',
										width : '70%'
									},{
										display : '操作',
										name : 'orgId',
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
								url : "${ctx}/report/frame/task/getDeployDeptTreeNode.json",
								autoParam : [ 'id', 'objDefNo' ]
							},
							callback : {
								onClick : function(event, treeId, treeNode){
									onSelectTree(treeNode);
								},
							}
						}, [ {
							id : "0",
							text : "分发的机构",
							icon : auth_obj_root_icon
						} ]);
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
							refreshObjDefTree();
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
				url :"${ctx}/report/frame/task/getDeployDeptTreeNode.json?d="
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
		if(!checkRow(treeData.params.realId))
			return ;
		appendListRow(treeData.params.realId,treeData.text);
	}
	
	function checkRow(orgId){
		var rows = grid.rows;
		for(var i = 0 ;i <rows.length;i++){
			if(orgId == rows[i].orgId) 
				return false;
		}
		return true;
	}
	
	function appendListRow(orgId,orgName){
		grid.addRow(
				{
					orgId:orgId,
					orgName:orgName
				}
		);
		
	}
	
</script>

<title>指标信息</title>
</head>
<body>
	<div id="template.left.up">授权对象</div>
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