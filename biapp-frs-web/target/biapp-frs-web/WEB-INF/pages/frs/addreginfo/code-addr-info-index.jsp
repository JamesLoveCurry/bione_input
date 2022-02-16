<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template3.jsp">
<head>
<script type="text/javascript">
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var doFlag="";
	var orgInfo={}; //  父级机构信息
	var newFlag =false; //是否新增
	$(function() {
		
		$("#mainformdiv").ligerTab({
			contextmenu : true
		});
		tabObj = $("#mainformdiv").ligerGetTabManager();
		//初始化org异步树
		var setting = {
				async : {
					//zTree的默认参数，且规定以nodeId变量名为变量传递id
					autoParam : [ "id=nodeId" ],
					enable : true,
					url : "${ctx}/bione/frs/addreginfo/getTree.json?d=" + new Date().getTime(),
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
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);

		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'add',
				text : '新建',
				click : addAddrInfo
			}, {
				icon : 'delete',
				text : '删除',
				click : deleteAddrInfo
			} ]
		});
		//delete
		function deleteAddrInfo() {
			if (!currentNode) {
				BIONE.tip("请选择行政区域");
				return;
			}
			if (!currentNode.children || currentNode.children.length == 0) {
				$.ligerDialog.confirm('确认删除', '是否确认删除', function(yes) {
					if (yes) {
						var id = currentNode.id;
						if (id != null) {
							BIONE.ajax({
								type : 'DELETE',
								url : '${ctx}/bione/frs/addreginfo/' + id
							}, function(result) {
								if(result){
									var upId = currentNode.upId;
									if(upId =="0"){
										upId ="0";
									}
									BIONE.refreshAsyncTreeNodes(leftTreeObj, "id", upId,
											"refresh");
									tabObj.removeTabItem("orgTab");
									$(".l-dialog-btn").remove();
									currentNode = null;
									BIONE.tip("删除成功!");
									
									orgInfo.upAddrNo=null;
									orgInfo.addrNo=null;
									orgInfo.addrName=null;
								}else{
									BIONE.tip("该行政区域下面还有行政区域,不能直接删除!");
								}
							});
						}
					}
				});

			} else {
				BIONE.tip("该行政区域下面还有行政区域,不能直接删除!");
				return;
			}

		}
		//新增区域
		function addAddrInfo() {
			doFlag="editNew";
			if (!currentNode) {
				BIONE.tip("请选择行政区域");
				return;
			}
			newFlag = true;//是新增
			var id, addrNm;
			if (!currentNode) {
				id = "0";
				addrNm = "无";
			} else if (currentNode.id == "0") {
				id = "0";
				addrNm = "无";
			} else {
				id = currentNode.data.orgNo;
				addrNm = currentNode.data.orgName;
			}
			var tabUri = "${ctx}/bione/frs/addreginfo/new?orgNo=" + id ;
					orgInfo.upName=addrNm;
			
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
				onclick : saveAddrInfo
			});
			newAddrInfo(tabUri, buttons);
		}
		//新增保存方法
		function saveAddrInfo() {
			document.getElementById("orgTab").contentWindow.f_save();
		}
		//树点击事件
		function f_clickNode(event, treeId, treeNode) {
			doFlag="edit";
 			newFlag =false;
			currentNode = treeNode;
			if (currentNode.id == "0") {
				return;
			}
			var upAddrNo = null;//上级ID
			var parentNode = treeNode.getParentNode();
			var addrNo = null;//当前节点ID
			var addrName = null;//节点名称
			if (parentNode) {
				if(parentNode.id=="0"){
					upAddrNo = "0";
				}else{
					upAddrNo = parentNode.id;
				}				
			} 
		
			addrNo = currentNode.id;//当前节点id
			addrName = currentNode.text;//节点名称
			var tabUri = "${ctx}/bione/frs/addreginfo/edit?upAddrNo=" + upAddrNo;
			
			orgInfo.upAddrNo=upAddrNo;
			orgInfo.addrNo=addrNo;
			orgInfo.addrName=addrName;
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
				onclick : saveAddrInfo
			});
			newAddrInfo(tabUri, buttons); 
		}

		function newAddrInfo(tabUri, buttons) {
			var curTabUri = tabUri;
			height = $("#mainformdiv").height() - 40;
			tabObj.removeTabItem("orgTab");
			tabObj.addTabItem({
						tabid : "orgTab",
						text : "行政区域基本信息",
						showClose : false,
						content : '<iframe frameborder="0" id="orgTab" style="height:'+height+'px;" src='+curTabUri+' ></iframe>'
					});
			tabObj.selectTabItem("orgTab");
			$(".l-dialog-btn").remove();
			BIONE.addFormButtons(buttons);
			
		}
	});

	//异步加载完成回调函数
	function selectTreeNode(){
		if(currentNode !=null){
			if(currentNode.upId=="0"||currentNode.id!="0"){
				var pNode = leftTreeObj.getNodeByParam("id","0");
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
</script>

<title>行政区域管理</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">行政区域管理</span>
	</div>
	<div id="template.right"></div>
</body>
</html>