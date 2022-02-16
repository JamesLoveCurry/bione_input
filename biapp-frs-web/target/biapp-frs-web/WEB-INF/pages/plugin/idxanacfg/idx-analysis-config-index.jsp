<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template3.jsp">
<head>
<style>
.addTab {
	float: right;
	margin-left: 90px;
	margin-right: 4px;
	position: relative;
	top: -21px;
}

.editTab {
	float: right;
	margin-left: 70px;
	margin-right: 15px;
	position: relative;
	top: -21px;
}
</style>
<script type="text/javascript">
	var url = {};
	var currentNode;
	var tabObj = null;
	var height = "";
	var TreeObj = "";
	$(function() {
		initTool();
		initTree();
		initbutten();
		initTab();
	});

	function initTool() {
		$('#right').append('<div id="cover" class="l-tab-loading" style="display:block; background:url(${ctx}/images/classics/index/bg_center.jpg) no-repeat center center #ffffff;"></div>');
		$("#cover").show();
	}

	function initbutten() {
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'config',
				text : '模板维护',
				menu : {
					width : 90,
					items : [ {
						icon : 'add',
						text : '新建',
						click : newTmp
					}, {
						line : true
					}, {
						icon : 'true',
						text : '复制',
						click : copyTmp
					}, {
						line : true
					}, {
						icon : 'delete',
						text : '删除',
						click : deleteTemp
					} ]
				}
			}, /* {
				icon : 'process',
				text : '预览',
				click : process
			},  */{
				icon : 'settings',
				text : '默认',
				click : defaultTemp
			} ]
		});
	}

	//空白页面
	function initTab() {
		$("#mainformdiv").ligerTab({
			contextmenu : false,
			onAfterSelectTabItem : function(tabId) {
				if ($("#" + tabId).attr('src') == "") {
					$("#" + tabId).attr('src', url[tabId]);
				}
			},
			onClose : function(tabId) {
				tabonClose(tabId);
				return false;
			},
			onAfterAddTabItem : function(tabObj) {
				if (tabObj.tabid != "resTab" && tabObj.tabid != "configTab") {
					initEditTabBtn(tabObj.tabid);
				}
			}
		});
		tabObj = $("#mainformdiv").ligerGetTabManager();
		height = $("#mainformdiv").height() - 31;
		tabObj.addTabItem({
			tabid : "resTab",
			text : "模板基本信息",
			showClose : false,
			content : '<iframe frameborder="0" id="resTab" name="resTab" style="height:' + height + 'px;" src="" ></iframe>'
		});
		tabObj.addTabItem({
			tabid : "configTab",
			text : "模板配置信息",
			showClose : false,
			content : '<iframe frameborder="0" id="configTab" name="configTab" style="height:' + height + 'px;" src="" ></iframe>'
		});
		tabObj.selectTabItem("resTab");
		initAddTabBtn();
		initFormBtn();
	}

	//tab页新建按钮
	function initAddTabBtn() {
		$("[tabId=resTab]").find("a").after("<div id = 'addTab' class='l-icon l-icon-add addTab'></div>");
		$("#addTab").click(function() {
			if (!currentNode) {
				BIONE.tip("请选择一个模版！");
			} else {
				var templateId = currentNode.id;
				var url = "${ctx}/cabin/analysis/config/chartCfg?templateId="+ templateId;
				BIONE.commonOpenSmallDialog("添加图表信息","chartsConfig", url);
			}
		});
	}

	//tab页修改
	function initEditTabBtn(chartId) {
		$("[tabId= " + chartId + "]").find("a").after("<div id = 'editTab" + chartId + "' class='l-icon l-icon-edit editTab'></div>");
		$("#editTab" + chartId).click(function() {
			if (chartId == "") {
				BIONE.tip("请选择一个图表！");
			} else {
				var url = "${ctx}/cabin/analysis/config/chartCfg?chartId="+ chartId;
				BIONE.commonOpenSmallDialog("修改图表信息","chartsConfig", url);
			}
		});
	}

	//新建模版页
	function newTmp() {
		tabObj.removeAll();
		$("#resTab").attr("src", "");
		$("#configTab").attr("src", "");
		url["resTab"] = "${ctx}/cabin/analysis/config/tmpBasic";
		url["configTab"] = "";
		tabObj.selectTabItem("resTab");
		tabObj.removeTabItem("configTab");
		$("#cover").hide();
	}

	//复制模版
	function copyTmp() {
		if (currentNode) {
			var templateId = currentNode.id;
			if (templateId) {
				$.ajax({
					cache : false,
					async : false,
					url : "${ctx}/cabin/analysis/config/copyTemp",
					dataType : 'json',
					data : {
						templateId : templateId
					},
					type : "post",
					beforeSend : function() {
						BIONE.loading = true;
						BIONE.showLoading("正在复制模版中...");
					},
					complete : function() {
						BIONE.loading = false;
						BIONE.hideLoading();
					},
					success : function(result) {
						initTree();
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		} else {
			BIONE.tip("请选择一个模版！");
		}
	}

	//修改模板页面
	function detailsTmp(templateId) {
		tabObj.removeAll();
		if(!tabObj.isTabItemExist("configTab")){
			tabObj.addTabItem({
				tabid : "configTab",
				text : "模板配置信息",
				showClose : false,
				content : '<iframe frameborder="0" id="configTab" name="configTab" style="height:' + height + 'px;" src="" ></iframe>'
			});
		}
		$("#resTab").attr("src", "");
		$("#configTab").attr("src", "");
		url["resTab"] = "${ctx}/cabin/analysis/config/tmpBasic?templateId=" + templateId;
		url["configTab"] = "${ctx}/cabin/analysis/config/tmpDetail?templateId="+ templateId;
		$("#cover").hide();
	}

	//初始化保存按钮
	function initFormBtn() {
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				$("#cover").show();
			}
		});
		buttons.push({
			text : '保存',
			onclick : saveForm
		});
		BIONE.addFormButtons(buttons);
	}

	//初始化树
	function initTree() {
		var setting = {
			view : {
				fontCss : changeColor,
				nameIsHTML : true,
				showTitle : false
			},
			data : {
				key : {
					name : 'text'
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			edit : {
				drag : {
					isCopy : false,
					isMove : false
				},
				enable : true,
				showRemoveBtn : false,
				showRenameBtn : false
			},
			callback : {
				onClick : treeonClick
			}
		}

		$("#cover").show();
		//右树对象
		TreeObj = $.fn.zTree.init($("#tree"), setting);
		//加载数据
		loadTree("${ctx}/cabin/analysis/config/getTmpTree", TreeObj);
	}

	//改变树节点颜色
	function changeColor(treeId, treeNode) {
		if (treeNode.data) {
			return treeNode.data.isDefault == "Y" ? {
				color : "red"
			} : {};
		}
	}

	//删除树节点
	function deleteTemp() {
		if (!currentNode || currentNode.id == "0") {
			BIONE.tip("请选择模板节点");
			return;
		}
		$.ligerDialog.confirm('确认删除', '是否确认删除', function(yes) {
			if (yes) {
				var templateId = currentNode.id;
				var data = {
					templateId : templateId
				};
				var url = "${ctx}/cabin/analysis/config/deleteTmp";
				if (templateId != null) {
					$.ajax({
						cache : false,
						async : true,
						url : url,
						data : data,
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
						success : function() {
							BIONE.tip("已删除模板：" +　currentNode.text);
							initTree();
							tabObj.removeAll();
							$("#cover").show();
						},
						error : function(result, b) {
							BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					});
				}
			}
		});
	}

	//加载树中数据
	function loadTree(url, component) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
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
				if (result.nodes.length > 0) {
					var resultNodes = result.nodes;
					component.addNodes(null, resultNodes, true);//添加节点
					component.expandNode(component.getNodeByParam("id", "0",null), true, false, false);//展开第一层节点
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	//树点击事件
	function treeonClick(event, treeId, treeNode) {
		currentNode = treeNode;
		if (currentNode.id == "0") {
			return;
		}
		var templateId = currentNode.id;
		url = {};
		detailsTmp(templateId);
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/cabin/analysis/config/queryCharts",
			dataType : 'json',
			data : {
				templateId : templateId
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
				if (result.chartsList && result.chartsList.length > 0) {
					addDetailsTabs(result.chartsList);
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
		tabObj.selectTabItem("resTab");
	}

	//点击树节点增加图表tab页
	function addDetailsTabs(anaChartsList){
		if(anaChartsList){
			for (var i = 0; i < anaChartsList.length; i++) {
				var chartUrl = "";
				tabObj.addTabItem({
					tabid : anaChartsList[i].chartId,
					text : anaChartsList[i].chartNm,
					showClose : true,
					content : '<iframe frameborder="0" id="'+ anaChartsList[i].chartId +'" name="'+ anaChartsList[i].chartId +'" style="height:' + height + 'px;" src=""></iframe>'
				});
				if (anaChartsList[i].chartType == "01") {
					chartUrl = "outlineCht";
				} else if (anaChartsList[i].chartType == "02") {
					chartUrl = "orgCfg";
				} else if (anaChartsList[i].chartType == "03") {
					chartUrl = "trend";
				} else if (anaChartsList[i].chartType == "04") {
					chartUrl = "structure";
				} else if (anaChartsList[i].chartType == "05") {
					chartUrl = "relationship";
				}
				url[anaChartsList[i].chartId] = "${ctx}/cabin/analysis/config/"+ chartUrl +"?chartId="+ anaChartsList[i].chartId+ "&templateId="+ anaChartsList[i].templateId;
			}
		}
	}
	
	//新增保存方法
	function saveForm() {
		document.getElementById(tabObj.getSelectedTabItemID()).contentWindow.f_save();
	}

	//删除图表
	function tabonClose(tabId) {
		$.ligerDialog.confirm('是否确认删除图表？', function(yes) {
			if (yes) {
				$.ajax({
					cache : false,
					async : false,
					url : "${ctx}/cabin/analysis/config/deleteCha",
					dataType : 'json',
					data : {
						chartId : tabId
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
						BIONE.tip(result.tipText);
						tabObj.removeTabItem(tabId);
						tabObj.selectTabItem("resTab");
					},
					error : function(result, b) {
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});
			}
		});
	}

	//预览模版
	function process() {
		if (!currentNode || currentNode.id == "0") {
			BIONE.tip("请选择模板节点");
			return;
		}
		BIONE.commonOpenDialog("预览", "", "700", "400","${ctx}/cabin/analysis/config/preview?tmpId=" + currentNode.id);
	}

	//设置默认模版
	function defaultTemp() {
		var templateId = currentNode.id;
		if (templateId == "0") {
			BIONE.tip('请选择具体模版');
		} else {
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/cabin/analysis/config/defaultTemp",
				dataType : 'json',
				data : {
					templateId : templateId
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
					initTree();
					BIONE.tip(currentNode.text + '已设置为默认模版');
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">模版信息</span>
	</div>
</body>
</html>