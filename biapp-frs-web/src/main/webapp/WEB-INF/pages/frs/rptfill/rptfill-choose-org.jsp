<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns = [];
	var currentNode; //当前点击节点
	var leftTreeObj;
	var settingAsync = null;
	var settingSync = null;

	$(function() {
		var height = $(document).height();
		$("#center").height(height - 42);
		$("#content").height(height - 42);
		$("#treeContainer").height(height - 72);
		//initTree();
		initBtn();
		initTreeByTask();
	});

	function initTree() {
		settingAsync = {
			async : {
				type : 'get',
				enable : true,
				url : "${ctx}/frs/systemmanage/orgmanage/getTree?orgType=${orgType}&t="
						+ new Date().getTime(),
				autoParam : [ "upOrgNo", "orgNo", "orgNm" ],
				dataType : "json",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for (var i = 0; i < childNodes.length; i++) {
							childNodes[i].orgType = childNodes[i].params.orgType;
							childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
							childNodes[i].orgNm = childNodes[i].params.orgNm;
							childNodes[i].orgNo = childNodes[i].params.orgNo;
						}
					}
					return childNodes;
				}
			},
			data : {
				key : {
					name : "text"
				}
			},
			callback : {
				onClick : zTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.id == "ROOT" || treeNode.id == "0") {
						leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
					}
				}
			}
		};

		settingSync = {
			data : {
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
			callback : {
				onClick : zTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
				}
			}
		};

		//搜索
		$("#treeSearchIcon").bind("click", function() {
			setTree($("#treeSearchInput").val() != "");
		});
		$("#treeSearchInput").bind("keydown", function(e) {
			if (e.keyCode == 13) {
				setTree($("#treeSearchInput").val() != "");
			}
		});

		setTree(false);
	}

	function setTree(searchFlag) {
		if (searchFlag) {
			leftTreeObj = $.fn.zTree.init($("#tree"), settingSync);
			loadTree("${ctx}/frs/systemmanage/orgmanage/searchOrgTree?orgType=${orgType}&t="
							+ new Date().getTime(), leftTreeObj, {
						orgNm : $("#treeSearchInput").val()
					});
		} else {
			loadTree("${ctx}/rpt/frs/rptfill/getTaskRptExeObjTree?taskid=" + window.parent.base_TaskId + "&taskObjId=" + window.parent.base_InitRptId + "&tskType=${orgType}&dataDate=" + window.parent.base_DataDate + "&onlyWatch=false&moduleType=${orgType}", leftTreeObj);
		}
	}

	//加载树
	function loadTree(url, treeObj, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			type : "post",
			dataType : "json",
			data : data,
			beforeSend : function() {
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = treeObj.getNodes();
				for (var i = 0; i < nodes.length; i++) {
					treeObj.removeNode(nodes[i], false); //移除先前节点
				}
				if (result.length > 0) {
					tmpNodes = result; //用于级联取消选择操作
					treeObj.addNodes(null, result, false);
					treeObj.expandAll(true);
					treeObj.refresh(); //更新显示
				}
			},
			error : function(result, b) {
			}
		});
	}

	//树点击事件
	function zTreeOnClick(event, treeId, treeNode) {
		if (currentNode != treeNode) {
			currentNode = treeNode;
		}
	}

	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("chooseOrg");
			}
		}, {
			text : "选择",
			onclick : function() {
				if (currentNode.id == "0") {
					BIONE.tip("请选择机构节点");
					return;
				}

				if ("${RptFillFlag}" && "${RptFillFlag}" != null
						&& "${RptFillFlag}" == "eastRptFill") {
					window.parent.chooseOrgTreeVal(currentNode.id,
							currentNode.text);
					BIONE.closeDialog("chooseOrg");
				} else {
					window.parent.$.ligerui.get("org")._changeValue(
							currentNode.id, currentNode.text);
					window.parent.orgNo = currentNode.id;
					window.parent.orgNm = currentNode.text;
					BIONE.closeDialog("chooseOrg");
				}
			}
		});
		BIONE.addFormButtons(btns);
	}
	
	//根据任务获取对应的机构号
	function initTreeByTask(){
		settingSync = {
				data : {
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
				callback : {
					onClick : zTreeOnClick,
				}
			};
		
		leftTreeObj = $.fn.zTree.init($("#tree"), settingSync);
		loadTree("${ctx}/rpt/frs/rptfill/getTaskRptExeObjTree?taskid=" + window.parent.base_TaskId + "&taskObjId=" + window.parent.base_InitRptId + "&tskType=${orgType}&dataDate=" + window.parent.base_DataDate + "&onlyWatch=false&moduleType=${orgType}&isOrgTree=Y", leftTreeObj);
		//搜索
		$("#treeSearchIcon").bind("click", function() {
			setTree($("#treeSearchInput").val() != "");
		});
		$("#treeSearchInput").bind("keydown", function(e) {
			if (e.keyCode == 13) {
				setTree($("#treeSearchInput").val() != "");
			}
		});
	
	}
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6">
			<div id="treeSearchbar"
				style="width: 99%; margin-top: 2px; padding-left: 2px;">
				<ul>
					<li style="width: 100%; text-align: left;">
						<div class="l-text-wrapper" style="width: 100%;">
							<div class="l-text l-text-date" style="width: 100%;">
								<input id="treeSearchInput" type="text" class="l-text-field"
									style="width: 100%; padding-top: 0px; padding-left: 0px; bottom: 0px;" />
								<div class="l-trigger">
									<i id="treeSearchIcon" class="icon-search search-size"></i>
								</div>
							</div>
						</div>
					</li>
				</ul>
			</div>
			<div id="treeContainer"
				style="width: 100%; height: 100%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree">
				</ul>
			</div>
		</div>
	</div>
</body>
</html>