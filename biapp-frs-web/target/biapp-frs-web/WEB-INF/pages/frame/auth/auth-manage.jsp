<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template4_BS.jsp">
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

		var isSuperUser = '${isSuperUser}';
		var isManager = '${isManager}';
		//当前授权对象，全局变量
		var curObjDefNo = "";

		//许可树，数据备份(切换资源tab页时的缓存)
		//[{tabId,[{permissionTreeId,permissionTreeData}]}]
		// var permissionTreeDatas = new Array();

		//授权资源根节点图标
		var auth_obj_root_icon = "${ctx}/images/classics/icons/house.png";
		//资源操作根节点图标
		var res_oper_root_icon = "${ctx}/images/classics/icons/house.png";

		//资源操作当前选中tabid
		var selectedResTab = "";

		//记录当前点击的授权对象id
		var selectedObjId = "";

		var ROOT_NO = '0';
		//许可类型
		var RES_PERMISSION_TYPE_OPER = '1';//操作
		var RES_PERMISSION_TYPE_NAME = '2';//数据规则

		var PERMISSION_ALL = "*";//全部许可
		var PERMISSION_NONE = "-";//没有任何许可

		var authObjMapping = new Array();

		var rightTreeCheckDataTemp = new Map(); //缓存右侧资源树授权信息

		var groupicon = "${ctx}/images/classics/icons/find.png";

		var menuResNo = "AUTH_RES_MENU";

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

		// 加工图标
		function initIcon(nodes){
			if(nodes
					&& nodes instanceof Array === true){
				for ( var i = 0; i < nodes.length; i++) {
					var r = nodes[i];
					r.icon = "${ctx}"
							+ (r.icon.indexOf("/") != 0 ? "/" : "")
							+ r.icon;
					if(r.children
							&& r.children != null){
						r.children = initIcon(r.children);
					}
				}
			}
			return nodes;
		}

		//刷新左侧授权对象树
		function refreshObjDefTree(objDefNo, searchText) {
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
							n.objDefNo = objDefNo;
						});
						leftTree.addNodes(leftTree.getNodeByParam("id", '0', null),
								result, true);
						//展开树
						//leftTree.expandAll(true);
						var rootNodeTmp = leftTree.getNodeByParam("id", ROOT_NO , null);
						leftTree.expandNode(rootNodeTmp , true , false);
						//leftTree.setting.check.enable = true;
						//leftTree.setting.check.chkStyle = "checkbox";
						//leftTree.setting.check.chkboxType = {"Y": "p", "N": "s"};
						if(leftTree.setting.async.otherParam){
							leftTree.setting.async.otherParam.searchText = searchText;
						}
					},
					error : function(result, b) {
						////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		}

		//左侧combobox值改变
		function leftComboChange(value, text) {
			//刷新左侧树
			refreshObjDefTree(value);
		}

		//设置选择的节点
		function authObjNodeClickNew(){
			if(selectedObjId == null || selectedObjId == "" || selectedResTab == null || selectedResTab == ""){
				return;
			}
			if (authLeftCombobox && authLeftCombobox.getValue()
					&& authLeftCombobox.getValue() != "") {
				//授权对象编号
				var objDefNo = authLeftCombobox.getValue();
				//授权具体对象id
				var objDefId = selectedObjId;
				var resTreeObj = eval("resTree_" + selectedResTab);
				if(rightTreeCheckDataTemp.get(selectedResTab)){
					if(selectedResTab == "AUTH_RES_IDX"){
						resTreeObj.expandAll(false);
					} else {
						return;
					}
				} else {
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/bione/admin/auth/getAuthObjResRel.json",
						dataType : 'json',
						type : "get",
						data : {
							"objDefNo" : objDefNo,
							"objDefId" : objDefId,
							"resDefNo" : selectedResTab
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
							if (selectedResTab) {
								if (!result) {
									return;
								}
								var data = result.Data;
								if (!data || data.length <= 0) {
									rightTreeCheckDataTemp.set(selectedResTab, new Array());
									return;
								}
								rightTreeCheckDataTemp.set(selectedResTab, data);
								if(selectedResTab == "AUTH_RES_IDX"){
									resTreeObj.expandAll(false);
								} else {
									setCheckNode(rightTreeCheckDataTemp.get(selectedResTab));
								}
							}
						}
					});
				}
			}
		}

		//设置选中的节点
		function setCheckNode(data){
			if(data && data.length > 0){
				var tabId = selectedResTab;
				var resTreeObj = eval("resTree_" + tabId);
				for ( var j = 0; j < data.length; j++) {
					var relObj = data[j];
					if (relObj.id.resDefNo == tabId) {
						if (resTreeObj) {
							var resTreeNode = resTreeObj.getNodesByFilter(
									function(node){
										return (node.params.id == relObj.id.resId && node.params.resDefNo == relObj.id.resDefNo);
									} , true);
							if (resTreeNode == null) {
								continue;
							}
							//只勾选存在关联关系的资源节点
							if((tabId != "AUTH_RES_RPTORG") && (menuResNo != tabId)){
								resTreeObj.checkNode(resTreeNode, true, true, false);
							}else{
								resTreeObj.checkNode(resTreeNode, true, false, false);
							}
						}
					}
				}
			}
		}

		//保存按钮 触发
		function saveButton(){
			if (!leftTree) {
				return;
			}
			var leftSelNodes = leftTree.getSelectedNodes();
			if (!leftSelNodes.length || leftSelNodes.length <= 0) {
				//若没有选择授权对象
				BIONE.tip('请选择有效授权对象');
				return;
			}
			var leftSelNode = leftSelNodes[0];
			if (leftSelNode.id == '0') {
				//若选择的是根节点
				BIONE.tip('请选择有效授权对象');
				return;
			}
			var resIds = new Array();
			//获取资源
			if (!navtab1) {
				return;
			}
			if (selectedResTab == null || selectedResTab == "") {
				return;
			}
			var tabTree = eval("resTree_" + selectedResTab);
			if (tabTree) {
				var tabTreeSelNodes = tabTree.getCheckedNodes(true);
				for ( var j = 0; j < tabTreeSelNodes.length; j++) {
					var operNoTmp = tabTreeSelNodes[j].params.resDefNo;
					if(selectedResTab == "AUTH_RES_IDX"){
						if(tabTreeSelNodes[j].id == '0'){
							continue;
						}
						resIds.push(tabTreeSelNodes[j].id);
					} else {
						if (tabTreeSelNodes[j].id == '0' || !operNoTmp || operNoTmp == null) {
							//不包含根节点 , 不包含非资源节点
							continue;
						}
						//获取具体某个菜单操作许可
						resIds.push(tabTreeSelNodes[j].params.id);
					}
				}
			}
			var nodes = {
				objDefNo : authLeftCombobox.getValue(),
				objId : leftSelNode.params.id || leftSelNode.id,
				resDefNo : selectedResTab,
				resIds : resIds
			};
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/bione/admin/auth/saveAuth",
				dataType : 'json',
				type : "post",
				data : {
					nodes : JSON2.stringify(nodes)
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
					BIONE.tip('保存成功!');
					//清空授权信息缓存
					rightTreeCheckDataTemp.delete(selectedResTab);
					//刷新指标授权信息
					if(selectedResTab == 'AUTH_RES_IDX'){
						$.ajax({
							cache : false,
							async : true,
							url : "${ctx}/bione/admin/auth/getAuthObjResRel.json",
							dataType : 'json',
							type : "get",
							data : {
								"objDefNo" : authLeftCombobox.getValue(),
								"objDefId" : selectedObjId,
								"resDefNo" : selectedResTab
							},
							success : function(result) {
								if (selectedResTab) {
									if (!result) {
										return;
									}
									var data = result.Data;
									if (!data || data.length <= 0) {
										return;
									}
									rightTreeCheckDataTemp.set(selectedResTab, data);
								}
							}
						});
					}
				},
				error : function(result, b) {
					BIONE.tip('保存失败,发现系统错误 <BR>错误码：'+ result.status);
				}
			});
		}

		//页面初始化
		$(function() {
			//隐藏操作栏，目前不支持进行操作权限分配
			$('#right').hide();
			$('#center').width('64%');

			// 2015 DO
			var $left = $('#left');
			$left.css('position', 'relative');
			$left.append('<div id="lsearch" class="lsearch" style="display: none;"><div id="l-text-wrap" class="l-input-wrap"><input id="l-text-field" class="l-text-field"/></div><div id="l-search-btn" class="l-search-btn">搜索</div><div class="close" style=""></div></div>');
			$('.close').click(function() {
				$('#lsearch').hide();
			});
			$('#l-search-btn').click(function(){
				var text = $('#l-text-field').val();
				var objdefno = $('#template_left_combobox_val').val();
				if (text) {
					refreshObjDefTree(objdefno, text);
				} else {
					refreshObjDefTree(objdefno)
				}
			});
			$('#l-text-field').keydown(function(e){
				if(e.keyCode==13){
					var text = $('#l-text-field').val();
					var objdefno = $('#template_left_combobox_val').val();
					if (text) {
						refreshObjDefTree(objdefno, text);
					} else {
						refreshObjDefTree(objdefno)
					}
				}
				return e;
			});
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

			BIONE.createButton({
				text : '保 存',
				width : '80px',
				appendTo : '#bottom',
				operNo : 'saveButton',
				icon : 'save',
				click : saveButton
			});

			window['leftTree'] = $.fn.zTree.init($("#leftTree"),{
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
					url: "${ctx}/bione/admin/auth/getAuthObjDefTree.json",
					autoParam: ['id', 'objDefNo' , 'searchText'],
					otherParam : {
						searchText : ''
					},
					dataFilter: function() {
						var cNodes = arguments[2];
						$.each(cNodes, function(i, n) {
							n.objDefNo = $('input[name=template_left_combobox_val]').val();
							n.searchText = $('#l-text-field').val();
						});
						return cNodes;
					}
				},
				callback : {
					beforeClick : function(treeId, treeNode, clickFlag) {
						if (navtab1) {
							//刷新资源树选中
							var tabList = navtab1.getTabidList();
							if (!tabList) {
								return;
							}
							//刷新资源树之前取消之前对象资源授权的勾选
							for ( var m = 0; m < tabList.length; m++) {
								var tabTree = eval("resTree_"
										+ tabList[m]);
								if (tabTree) {
									tabTree.checkAllNodes(false);
								}
							}
						}
					},
					onClick : function(event, treeId, treeNode) {
						if (treeNode.id == "0") {
							return;
						}
						selectedObjId = treeNode.id;
						//清空右侧树缓存的授权信息
						rightTreeCheckDataTemp = new Map();
						authObjNodeClickNew();
					}
				}
			}, [ {
				id : "0",
				text : "授权对象树",
				icon : auth_obj_root_icon
			} ]);
			//初始化授权资源tab
			window['navtab1'] = $("#navtab1").ligerTab({
				onAfterSelectTabItem : function (tabId) {
					selectedResTab = tabId;
					authObjNodeClickNew();
				}
			});

			//初始化tab页
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/bione/admin/auth/getAuthResDefTabs.json",
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
							navtab1.addTabItem({
								tabid : data[i].resDefNo,
								content : appendHtml,
								text : data[i].resName,
								showClose : false
							});
							initRightTree(data[i].resDefNo);
						}
						//设置默认选中tab为第一个tab
						navtab1.selectTabItem(data[0].resDefNo);
					}
				},
				error : function(result, b) {
					////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});

			//初始化左侧下拉框
			window['authLeftCombobox'] = $("#template_left_combobox").ligerComboBox(
					{
						url : "${ctx}/bione/admin/auth/getAuthObjCombo.json?d="
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
				$.ligerDialog.confirm("切换授权对象将丢失当前对象已修改\n资源、操作等信息，是否继续？", function(yes) {
					if (yes) {
						authLeftCombobox.selectValue(value);
						leftComboChange(value, text);
						leftTree.cancelSelectedNode(leftTree.getNodeByParam("id",
								'0', null));
						if (navtab1) {
							//刷新资源树选中
							var tabList = navtab1.getTabidList();
							if (!tabList) {
								return;
							}
							//刷新资源树之前取消之前对象资源授权的勾选
							for ( var m = 0; m < tabList.length; m++) {
								var tabTree = eval("resTree_"
										+ tabList[m]);
								if (tabTree) {
									tabTree.checkAllNodes(false);
								}
							}
						}
						//清空缓存
						rightTreeCheckDataTemp = new Map();
					}
				});
				return false;
			}, this);
		})

		function isExistIdx(node, data){
			if(data && data.length > 0){
				for (let i = 0; i < data.length; i++) {
					if(node.id == data[i].id.resId){
						return true;
					}
				}
			}
			return false;
		}

		//初始化右侧树
		function initRightTree(resDefNo){
			//构造资源树
			var chkboxType = {
				"Y" : "ps",
				"N" : "s"
			};
			if(resDefNo == "AUTH_RES_RPTORG"){
				chkboxType = {
					"Y" : "s",
					"N" : "s"
				};
			}
			var subTreeId = "#authRes_" + resDefNo;
			if (eval($(subTreeId))) {
				//初始化右侧树
				window['resTree_' + resDefNo] = $.fn.zTree.init(eval($(subTreeId)),
						{
							check : {
								chkStyle : 'checkbox',
								enable : true,
								chkboxType : chkboxType
							},
							data : {
								keep : {
									parent : true
								},
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
							async : {
								enable : true,
								url : "${ctx}/bione/admin/auth/getAuthResDefTree.json",
								autoParam : ['id'],
								otherParam : {
									resDefNo : resDefNo
								}
							},
							callback : {
								onExpand : function (event, treeId, treeNode){
									if(selectedResTab == "AUTH_RES_IDX"){
										var resTreeObj = eval("resTree_" + selectedResTab);
										var childrenNodes = treeNode.children;
										for (let i = 0; i < childrenNodes.length; i++) {
											if(isExistIdx(childrenNodes[i], rightTreeCheckDataTemp.get(selectedResTab))){
												resTreeObj.checkNode(childrenNodes[i], true, false, false);
											}
										}
									}
								}

							}
						});
				//构造高度
				var treeContainer = resDefNo + "_Container";
				if ($("#" + treeContainer) && $("#center")) {
					$("#" + treeContainer).height($("#center").height() - 33);
				}
			}
		}

		var i = 0;
		function getCurrObjDefNo() {
			return i++;
		}
	</script>
	<script type="text/javascript">
		$(function() {
			var rightHeight = $("#right").height();
			//treetoolbar高度 为 26
			var $home1Container = $("#home1Container");
			$home1Container.height(rightHeight - 33);
			//var $home2Container = $("#home2Container");
			//$home2Container.height(rightHeight - 28);
		});
	</script>
</head>
<body>
<div id="template.left.up">授权对象</div>
<div id="template.left.up.right">
	<input type="text" id="template_left_combobox"></input>
</div>
<div id="template.left.up.icon">
	<i class = "icon-circle-my search-size"></i>
</div>
<div id="template.center">
	<div id="navtab1" style="overflow: hidden;"></div>
</div>
</body>
</html>