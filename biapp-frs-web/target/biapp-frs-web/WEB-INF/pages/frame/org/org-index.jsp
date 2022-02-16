<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template3_BS.jsp">
<head>
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag =false;
	
	function reload() {
		if (leftTreeObj) {
			leftTreeObj.reAsyncChildNodes(leftTreeObj.getNodeByParam("id", 0, null), "refresh");
		}
	}
	
	$(function() {
		$("#mainformdiv").ligerTab({
			contextmenu : true
		});
		tabObj = $("#mainformdiv").ligerGetTabManager();
		height = $("#mainformdiv").height() - 31;
		tabObj
		.addTabItem({
			tabid : "orgTab",
			text : "机构基本信息",
			showClose : false,
			content : '<iframe frameborder="0" id="orgTab" style="height:'+height+'px;" src="" ></iframe>'
		});

			var setting = {
					async : {
						//zTree的默认参数，且规定以nodeId变量名为变量传递id
						autoParam : [ "id=nodeId" ],
						enable : true,
						url : "${ctx}/bione/admin/org/list.json",
						dataType : "json",
						type : "post"
					},
					data : {
						key : {
							name : "text"
						}
					},
					view : {
						selectedMulti : false,
						showLine : true
					},
					callback : {
						onClick : f_clickNode,
						onAsyncSuccess: selectTreeNode//异步加载完成回调函数
					}
				}; 
		leftTreeObj = $.fn.zTree.init($("#tree"), setting, [ {
			id : "0",
			text : "机构树",
			icon : "${ctx}/images/classics/icons/house.png",
			nocheck : true,
			isParent: true
		    }]);
		var r = leftTreeObj.getNodesByParam('id', '0');
    	r = r.length > 0 ? r[0] : null;
    	if (r) {
    		leftTreeObj.reAsyncChildNodes(r, 'refresh', false);
    	}
    	var datadeal = {
				width :150,
				items : [{
					icon : 'import',
					text : '模板导入',
					click : importOrg
				},{
					icon : 'export',
					text : '模板导出',
					click : exportOrg
				}]
			};
    	var catalog = {
    			width :150,
				items : [{
					icon : 'add',
					text : '新建',
					click : addOrg
				},{
					icon : 'delete',
					text : '删除',
					click : deleteOrg
				}]
    	}
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'menu',
				text : '目录',
				menu : catalog
			},{
				icon : 'navigation',
				text : '数据处理',
				menu : datadeal
			} ]
		});
		//delete
		function deleteOrg() {
			if (!currentNode||currentNode.id=="0") {
				BIONE.tip("请选择机构节点");
				return;
			}
			if (!currentNode.children || currentNode.children.length == 0) {
				$.ligerDialog.confirm('确认删除', '是否确认删除', function(yes) {
					if (yes) {
						var id = currentNode.data.orgId;
						if (id != null) {
							BIONE.ajax({
								type : 'post',
								url : '${ctx}/bione/admin/org/' + id
							}, function(result) {
								var returnStr = result.msg;
								if (returnStr == "0") {
									var upId = currentNode.upId;
									if(upId =="0"){
										upId ="0";
									}
									BIONE.refreshAsyncTreeNodes(leftTreeObj, "id", upId,
											"refresh");
									tabObj.removeTabItem("orgTab");
									$(".l-dialog-btn").remove();
									currentNode = null;
									BIONE.tip("机构删除成功!");
								} else if (returnStr == "1") {
									BIONE.tip("该机构被用户引用,不能删除!");
								} else if (returnStr == '2') {
									BIONE.tip("该机构授权了资源信息,不能删除");
								} else if (returnStr == "3") {
									BIONE.tip("该机构与授权对象关联，不能删除!");
								}
							});
						}
					}
				});

			} else {
				BIONE.tip("当前节点含有子节点，请将子节点删除后再进行删除");
				return;
			}

		}
		//新增机构 
		function addOrg() {
			newFlag = true;
			var id, orgName;
			if (!currentNode) {
				id = "0";
				orgName = "无";
			} else if (currentNode.id=="0") {
				id = "0";
				orgName = "无";
			} else {
				id = currentNode.data.orgNo;
				orgName = currentNode.data.orgName;
			}
			var tabUri = "${ctx}/bione/admin/org/new?orgNo=" + id + "&upName="
					+ encodeURI(encodeURI(orgName));
			var buttons = [];

			buttons.push({
				text : '取消',
				onclick : function() {
					tabObj.removeTabItem("orgTab");
					$(".l-dialog-btn").remove();
				}
			});

			buttons.push({
				text : '保存',
				onclick : saveOrg
			});

			newOrgInfo(tabUri, buttons);
		}
		//新增保存方法
		function saveOrg() {
			document.getElementById("orgTab").contentWindow.f_save();
		}
		
		
		
		//树点击事件
		function f_clickNode(event, treeId, treeNode) {
			newFlag =false;
			currentNode = treeNode;
			if (currentNode.id=="0") {
				return;
			}
			var id = treeNode.data.orgId;
			var parentNode = treeNode.getParentNode();
			var orgNo = null;
			var upName = null;
			var orgName = treeNode.data.orgName;
			if (parentNode) {
				if(parentNode.id=="0"){
					orgNo = "0";
					upName = "无";
				}else{
					orgNo = parentNode.id;
					upName = parentNode.text;
				}
				
			}
			var tabUri = "${ctx}/bione/admin/org/" + id + "/edit?orgNo="
					+ orgNo + "&upName=" + encodeURI(encodeURI(upName)) + "&orgName=" + encodeURI(encodeURI(orgName));
			var buttons = [];
			buttons.push({
				text : '取消',
				onclick : function() {
					tabObj.removeTabItem("orgTab");
					$(".l-dialog-btn").remove();
				}
			});

			buttons.push({
				text : '保存',
				onclick : saveOrg
			});
			newOrgInfo(tabUri, buttons);
		}

		function newOrgInfo(tabUri, buttons) {
			var curTabUri = tabUri;
			height = $("#mainformdiv").height() - 31;
			tabObj.removeTabItem("orgTab");
			tabObj
					.addTabItem({
						tabid : "orgTab",
						text : "机构基本信息",
						showClose : false,
						content : '<iframe frameborder="0" id="orgTab" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
					});
			tabObj.selectTabItem("orgTab");

			$(".l-dialog-btn").remove();
			BIONE.addFormButtons(buttons);
			
		}
	});

	function saveSuccess() {
		tabObj.removeTabItem("orgTab");
		$(".l-dialog-btn").remove();
		loadTree("${ctx}/bione/admin/org/list.json?d=" + new Date().getTime(),
				leftTreeObj);
	}
	//异步加载完成回调函数
	function selectTreeNode(){
		if(currentNode !=null){
			if(currentNode.upId=="0"||currentNode.id!="base"){
				var pNode = leftTreeObj.getNodeByParam("id","base");
				leftTreeObj.expandNode(pNode, true);
			}
			var newNode = leftTreeObj.getNodeByParam("id",currentNode.id);
			leftTreeObj.selectNode(newNode);
			if(newFlag){
				if(newNode!=null&&newNode.isParent){
					leftTreeObj.expandNode(newNode, true);
				}
				newFlag =false;
			}
			
		}
		
	}
	
	//机构导入
	function importOrg(){
		BIONE.commonOpenDialog("报表机构导入", "importWin", 600, 480,
		"${ctx}/report/frame/wizard?type=BioneOrg");
	}
	//机构导出
	function exportOrg(){
		var type = 'BioneOrg';
		$.ajax({
			async:true,
			type:"POST",
			dataType:"json",
			url:"${ctx}/rpt/frame/rptorg/download",
			data:{type:type},
			beforeSend : function(a, b, c) {
				BIONE.showLoading('正在导出数据中...');
			},
			success:function(data){
				BIONE.hideLoading();
				if(data.fileName==""){
					BIONE.tip('导出异常');
					return;
				}
				var src='${ctx}/report/frame/wizard/export?type='+type+'&fileName='+data.fileName;
				window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
				$('body').append(downdload);
				downdload.attr('src', src);
			},
			error : function(result) {
				BIONE.hideLoading();
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
</script>

<title>机构管理</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">机构信息</span>
	</div>
	<div id="template.right"></div>
</body>
</html>