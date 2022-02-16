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
	var leftTree;
	var dataDate;
	var openedOrgs ;
	var orgs =  JSON2.parse('${orgs}');
	var init = true;
	$(function() {
		initDate();
	});
	
	function initDate(){
		if(!orgs||orgs=="")
			orgs = window.parent.deployTask.orgs;

			if(orgs &&orgs !=""){
				var orgStr = "";
				for(var i = 0 ;i <orgs.length;i++)
				{
					if(i!=0)
						orgStr =orgStr+",";
					if(orgs[i]!="")
						orgStr =orgStr+orgs[i];
				}
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/input/task/getParentOrgIds.json?d="+new Date(),
					dataType : 'json',
					type : "post",
					data : {
						orgNo :  orgStr
					},
					success : function(result) {
						openedOrgs = result;
						initOtherInfo();
						
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}else
				initOtherInfo();
	}
	
	function initOtherInfo(){
		initTreeToolBar();
		initChooseTree();
		initChooseDate();
		initChooseList();
		initBtn();
		init = false;
		//refreshObjDefTree();
	}
	
	function initBtn(){
		//初始化按钮
		var btns = [{
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("selectDeployBox");
			}
		},{
			text : '下发',
			onclick : function(){
				var rows = grid.rows;
				if(rows==null || rows.length==0)
				{
					BIONE.tip("请选择要下发的部门");
					return;
				}else if(!dataDate||dataDate == null || dataDate==""){
					BIONE.tip('请选择数据日期');
					return ;
				}
				

				var selectedOrgs = [];
				for(var i = 0 ; i<rows.length;i++){
					selectedOrgs[i] = rows[i].orgId;
				}
				var taskId =  window.parent.deployTask.taskId;
				$.ajax({
					cache : false,
					async : true,
					url :"${ctx}/rpt/input/task/testDulpDeployTask.json?d="
							+ new Date().getTime(),
					dataType : 'json',
					type : "post",
					data : {
						"selectedOrgs" : JSON2.stringify(selectedOrgs),
						"dataDate" : dataDate,
						"taskId" : taskId
					},
					success : function(result) {
						if(result&&result !=null){
							var msg = "机构:[";
							for(var i = 0 ;i < result.length;i++){
								if(i!=0)
									msg = msg + ",";
								msg = msg +result[i] ;
							}
							msg = msg + "]在当前数据日期已经下发，请修改";

							BIONE.tip(msg);
							return ;
						}

						$.ligerDialog.confirm('确实要下发吗?', function(yes) {
							if(yes) {
								window.parent.f_deploy(selectedOrgs,dataDate);
							}
						});
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码:' + result.status);
					}
				});
			}
		}];
		BIONE.addFormButtons(btns);
	}

	var chooseDateForm ;
	var exeObjType;
	var exeObjId;
	function initChooseDate(){
		if(window.parent.deployTask.tskExeobjRelVO)
		{
			exeObjType = window.parent.deployTask.tskExeobjRelVO.exeObjType
			exeObjId = window.parent.deployTask.tskExeobjRelVO.exeObjId;
		}
		else
		{
			exeObjType = window.parent.deployTask.exeObjType;
			exeObjId = window.parent.deployTask.exeObjId;
		}
						//如果是指标补录,需要填写数据日期
						chooseDateForm = $("#chooseDate").ligerForm({
							inputWidth : 120,
							labelWidth : 80,
							space : 50,
							align : "left",
							fields : [ {
								display : "数据日期",
								name : "dataDate",
								newline : false,
								type : 'date',
								format : 'yyyy-MM-dd',
								width : 215,
								options:{
									onChangeDate: function(value){
										$("#dataDate").val(value);
										dataDate = value.replace("-","").replace("-","");

										
									}
								}
							}]
						});
		
	}
	function getQ(month){
		var m = parseInt(month)
		var qStr = "";
		if((10<month||10==month)&&(month<12||month==12))
			qStr = "4";
		else if((7<month||7==month)&&(month<9||month==9))
			qStr = "3";
		else if((4<month||4==month)&&(month<6||month==6))
			qStr = "2";
		else if((1<month||1==month)&&(month<3||month==3))
			qStr = "1";
		return qStr;
	}

	function initChooseList() {
		grid = $("#chooseGrid")
				.ligerGrid(
						{
							columns : [{
							           display : '已选择的部门',
							           	columns:[{
											display : '部门名称',
											name : 'orgName',
											align : 'left',
											width : '60%'
										} ,{
											display : '操作',
											name : 'orgName',
											align : 'left',
											width : '30%',
											render : function(row) {
												return "<a href='javascript:void(0)' class='link' onclick='deleteGridData(\""
														+ arguments[1]
														+ "\")'>"
														+ '删除'
														+ "</a>";
											}
										}]
							}],
							checkbox : false,
							usePager : false,
							toolbar : {},
							height:320,
							colDraggable : true
						});
	}

	function deleteChoose(treeNode) {
		if(!treeNode.params) return ;
		var row = tmpSelectDatas[treeNode.params.realId];
		grid.deleteRow(row);
		delete tmpSelectDatas[treeNode.params.realId];
	}
	
	function deleteGridData(i){
		var row = grid.getRow(i);
		var orgId = row.orgId;
		var treeNodeObj = tempCheckedData[orgId];
		grid.deleteRow(row);
		leftTree.checkNode(treeNodeObj, false, false);
		
		delete tmpSelectDatas[orgId];
		delete tempCheckedData[orgId];
		
	}

	function initChooseTree() {		
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
					selectedMulti : true
				},
				check : {
					chkboxType : {"Y":"", "N":""},
					chkStyle : 'checkbox',
					enable : true
				},

				callback : {
					/*
					onClick : function(event, treeId, treeNode){
						onSelectTree(treeNode);
					},
					*/
					onCheck:function(event, treeId, treeNode){
						var isCheck = $("#checkChild").attr("checked")&&$("#checkChild").attr("checked")=="checked"?true:false;
						if(isCheck){
							leftTree.reAsyncChildNodes(treeNode, "refresh");
						}
						if(treeNode.getCheckStatus().checked){
							onSelectTree(treeNode);
						}else{
							deleteChoose(treeNode);
						}
						
					},
					onNodeCreated : function(event, treeId, treeNode) {
						if(treeNode){
							if(isCheckNode(treeNode.id)){
								leftTree.checkNode(treeNode, true, false);
								onSelectTree(treeNode);
							}
						}
						if (treeNode.upId == null) {
							//若是根节点，展开下一级节点
							leftTree.expandNode(treeNode , true , false);
							//leftTreeObj.expand(treeNode, true, true, true);
						}else if(isOpenNode(treeNode.id)){
							if(leftTree){
								leftTree.expandNode(treeNode , true , false);
								//leftTree.checkNode(treeNode, true, false);
								//onSelectTree(treeNode);
							}
						}
					},
					onAsyncSuccess : function(event, treeId, treeNode, msg){
						var isCheck = $("#checkChild").attr("checked")&&$("#checkChild").attr("checked")=="checked"?true:false;
						if(treeNode!=null){
							if(isCheck){
								if (treeNode.getCheckStatus().checked) {
									onSelectTree(treeNode);
									for ( var i in treeNode.children) {
										leftTree.checkNode(treeNode.children[i], true, false);
										onSelectTree(treeNode.children[i]);
									}
								}else{
									deleteChoose(treeNode);
									for ( var i in treeNode.children) {
										leftTree.checkNode(treeNode.children[i], false, false);
										deleteChoose(treeNode.children[i]);
									}
								}
						}
					}
					}
				}
			};
		leftTree = $.fn.zTree.init($("#leftTree"), setting);
		$("#template.left.center").hide();
	}
	
	function isCheckNode(org){
		if(orgs){
			for(var i = 0 ;i <orgs.length;i++){
				if(org == orgs[i])
					return true;
			}
		}
		return false;
	}

	function isOpenNode (org){
		if(openedOrgs){
			for(var i = 0 ;i <openedOrgs.length;i++){
				if(org == openedOrgs[i])
					return true;
			}
		}
		return false;
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
				url :"${ctx}/rpt/input/task/getDeployDeptTreeNode.json?d="
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
	var tmpSelectDatas = [];
	var tempCheckedData = [];
	function onSelectTree(treeData){
		if(!treeData.params) return ;
		if(tmpSelectDatas[treeData.params.realId]){
			return;
		}
		var orgId = treeData.params.realId;
		var targetRow = grid.addRow(
				{
					orgId:orgId,
					orgName:treeData.text
				}
		);
		tmpSelectDatas[orgId] = targetRow;
		tempCheckedData[orgId] = treeData;
	}
	
</script>

<title>指标信息</title>
</head>
<body>
	<div id="template.left.up">选择下发机构</div>
	<div id="template.left.up.right">
		<input id="checkChild" type="checkbox"></input>是否级联下级
	</div>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.center">
		<div id="chooseDate"></div>
		<div id="chooseGrid" style="overflow: auto;"></div>
	</div>
	<div id="treeTmp" style="overflow: hidden;"></div>
</body>
</html>