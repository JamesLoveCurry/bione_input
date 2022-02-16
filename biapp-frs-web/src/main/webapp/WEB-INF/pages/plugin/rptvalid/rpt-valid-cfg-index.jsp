<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template7_BS.jsp">

<script type="text/javascript">
	var isPublish;
	var curIdxName;
	var leftTreeObj, items, currentNode, grid, tabObj, tabChange;
	var treeRootNo = '${treeRootNo}'; //参数在开始显示主界面时传入
	var rootIcon = '${rootIcon}';
	var searchObj = {
		exeFunction : "initTree",
		searchType : "idx"
	};//默认执行initTree方法
	var refreshObj1, refreshObj2, refreshObj;
	$(function() {
		$("#treeToolbar").ligerTreeToolBar({
			items : [ {
				icon : 'export',
				text : '数据处理',
				menu : {
					width : 90,
					items : [ {
						icon : 'export',
						text : '导出数据',
						click : idxCheckExport
					}, {
						line : true
					}, {
						icon : 'import',
						text : '导入数据',
						click : idxCheckImport
					} ]
				}
			} ]
		});
		initTree(); //显示树
		initExport();
		height = $(document).height() - 25; //显示右侧tab下方的页面序号
		$("#treeSearchbar").hide();
		//增加一个搜索区域
		var searchArea = '<div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">'
				+ '<ul><li style="width:98%;text-align:left;"><div class="l-text-wrapper" style="width: 100%;">'
				+ '<div class="l-text l-text-date" style="width: 100%;"><input id="treeSearchInput" type="text" class="l-text-field"  style="width: 75%;" />'
				+ '<div class="l-trigger"><a id="treeSearchIcon" class = "icon-search font-size"></a>'
				+ '</div></div></div></li></ul></div>'
		$("#treeToolbar").after(searchArea);
		//添加高级搜索按钮
		$(".l-trigger").css("right", "26px");
		var innerHtml = '<i class="l-trigger" style="right:20px;width:1px;height:12px;border-left:1px dotted gray;margin-top:2px;"></i>'
				+ '<div title="高级筛选" class="l-trigger" style="right:0px;"><a id="highSearchIcon" class = "icon-light_off font-size"></a></div>';
		$(".l-trigger").after(innerHtml);
		//添加高级搜索弹框
		$("#highSearchIcon").click(
				function() {
					BIONE.commonOpenDialog("高级搜索", "highSearch", "600", "250",
							"${ctx}/report/frame/idx/highSearch?searchObj="
									+ JSON2.stringify(searchObj));
				});
		initTreeSearch();//普通搜索
	});

	function initTreeSearch() {
		$("#treeSearchIcon").live('click', function() {// 树搜索
			initTree($('#treeSearchInput').val(), "");
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				initTree($('#treeSearchInput').val(), "");
			}
		});
	}

	//初始化tree，只是用于显示树，同右侧的grid没有关系
	function initTree(searchNm, searchObj) {
		if (searchObj == undefined) {
			var url_ = "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=1&showEmptyFolder=1&isAuth=1&t="
					+ new Date().getTime();
			if (isPublish) {
				url_ = "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=1&isPublish=1&isPreview=1&isAuth=1&t="
						+ new Date().getTime();
			}
			var setting = {
				async : {
					enable : true,
					url : url_,
					autoParam : [ "nodeType", "id", "indexVerId" ], //将该三个参数提交,post方式
					dataType : "json",
					dataFilter : function(treeId, parentNode, childNodes) { //对 Ajax返回数据进行预处理
						if (childNodes) {
							for (var i = 0; i < childNodes.length; i++) {
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
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
				view : {
					selectedMulti : false
				},
				callback : {
					onClick : zTreeOnClick
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting, [ {
				id : '0',
				text : '全部',
				params : {
					"nodeType" : 'idxCatalog'
				},
				data : {
					"indexCatalogNo" : '0'
				},
				open : true,
				icon : '${ctx}' + rootIcon,
				isParent : true
			} ]);
		} else {
			var _url = "${ctx}/report/frame/idx/getSyncTree";
			var data = {
				'searchNm' : searchNm,
				'isShowIdx' : '1',
				'isShowDim' : '0',
				'isShowMeasure' : '1',
				'isPublish' : '1',
				"isAuth" : "0",
				"showEmptyFolder" : ""
			};
			if (searchObj != null && searchObj != "") {
				_url = "${ctx}/report/frame/idx/getSyncTreePro";
				data = {
					'searchObj' : JSON2.stringify(searchObj),
					'isShowIdx' : '1',
					'isShowDim' : '0',
					'isShowMeasure' : '1',
					'isPublish' : '1',
					"isAuth" : "0",
					"showEmptyFolder" : ""
				};
			}
			setting = {
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
					selectedMulti : false
				},
				callback : {
					onClick : zTreeOnClick
				}
			};
			leftTreeObj = $.fn.zTree.init($("#tree"), setting, []);
			BIONE.loadTree(
						_url,
						leftTreeObj,
						data,
						function(childNodes) {
							if (childNodes) {
								for (var i = 0; i < childNodes.length; i++) {
									childNodes[i].nodeType = childNodes[i].params.nodeType;
									childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								}
							}
							return childNodes;
						}, false);
		}

		if ($.browser.msie && parseInt($.browser.version, 10) == 7) {
			// ie7 divs position:relative bug
			$(".l-treemenubar-item").css("z-index", '999');
		}
	}

	//模板导入
	function idxCheckImport() {
		BIONE.commonOpenDialog("指标校验数据导入", "importWin", 600, 480,
				"${ctx}/report/frame/wizard?type=Idxcheck");

	}
	//模板导出
	function idxCheckExport() {
		BIONE.commonOpenDialog("指标校验数据导出", "exportWin", 400, 480,
				"${ctx}/report/frame/wizard/exportWin?type=Idxcheck");
	}

	var exportExcel = function(fileName, type) {
		var src = '';
		src = '${ctx}/report/frame/wizard/export?type=' + type + '&fileName='
				+ fileName;
		downdload.attr('src', src);
	};

	var exportTmp = function(fileName) {
		var src = '';
		src = "${ctx}/report/frame/idx/exportTmpInfo?filepath=" + fileName
				+ "&d=" + new Date();
		downdload.attr('src', src);
	};

	var initExport = function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};

	//导入后刷新
	function reload() {
		if (refreshObj1) {
			refreshObj1.reloadGrid();
		}
		if (refreshObj2) {
			refreshObj2.reloadGrid();
		}
		if (refreshObj) {
			refreshObj.reloadGrid();
		}
	}

	//指标节点单击事件
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		currentNode = treeNode;
		var catalogId = currentNode.data.indexCatalogNo;
		var type = currentNode.params.nodeType;
		if (treeNode.params.indexType == "05") {
			BIONE.tip("该指标为总账指标，请选择度量！");
			return;
		}
		if (type == "idxInfo") {
			//节点为指标类型
			if(!tabObj){
				initTab();
			}
			curIdxName = currentNode.data.indexNm;
			initCurrentTab(type, catalogId, currentNode.data.id.indexNo,
					currentNode.data.id.indexVerId, currentNode.data.indexNm,
					currentNode.data.infoRights);

		} else if (type == "measureInfo") {
			if(!tabObj){
				initTab();
			}
			var pNode = currentNode.getParentNode();
			var compNo = pNode.data.id.indexNo + "." + currentNode.id;
			var compNm = pNode.data.indexNm + "." + currentNode.text;
			curIdxName = compNm;
			initCurrentTab(type, catalogId, compNo, pNode.data.id.indexVerId,
					compNm, currentNode.id);
		} else {
			if (clickFlag == "1") {
				BIONE.tip("请选择具体指标！");
			}
		}
	}

	initTab = function() {
		var tabChangeFlag = false; // tab 是否可切换的标识
		tabObj = $("#tab")
				.ligerTab(
						{
							changeHeightOnResize : true,
							contextmenu : false,
							onBeforeSelectTabItem : function(tabId) {
								if (!tabChangeFlag) {
									return false;
								}
							},
							onAfterSelectTabItem : function(tabId) {
								if(!currentNode){
									return false;
								}
								var indexCatalogNo, indexNo, indexVerId, indexName;
								var type = currentNode.params.nodeType;
								indexCatalogNo = currentNode.data.indexCatalogNo;
								if (type == "idxInfo") {
									//节点为指标类型
									indexNo = currentNode.data.id.indexNo;
									indexVerId = currentNode.data.id.indexVerId;
									indexName = currentNode.data.indexNm;

								} else if (type == "measureInfo") {
									var pNode = currentNode.getParentNode();
									indexNo = pNode.data.id.indexNo + "." + currentNode.id;
									indexName = pNode.data.indexNm + "." + currentNode.text;
									indexVerId = pNode.data.id.indexVerId;
								}
								if (tabId == 'logic') {
									tabChange = "logic";
									if ($("#logic").attr('src') != "") {
									} else {
										$("#logic").attr({
											src : "${ctx}/report/frame/rptvalid/logic/tab?indexCatalogNo="
												+ indexCatalogNo + "&indexNo="
												+ indexNo + "&indexVerId=" + indexVerId + "&indexNm="
												+ indexName
										});
									}
								}
								if (tabId == 'warn') {
									tabChange = "warn";
									if ($("#warn").attr('src') != "") {
									} else {
										$("#warn").attr({
											src : "${ctx}/report/frame/rptvalid/warn?indexCatalogNo="
												+ indexCatalogNo + "&indexNo="
												+ indexNo + "&indexVerId=" + indexVerId + "&indexNm="
												+ indexName
										});
									}
								}
								if (tabId == 'planval') {
									tabChange = "planval";
									if ($("#planval").attr('src') != "") {
									} else {
										$("#planval").attr({
											src : "${ctx}/rpt/frame/idx/planval/idxPlanvalInfos?indexCatalogNo="
												+ indexCatalogNo + "&indexNo="
												+ indexNo + "&indexVerId=" + indexVerId + "&indexNm="
												+ indexName
										});
									}
								}
							}
						});
		tabObj.addTabItem({
					tabid : 'logic',
					text : '逻辑校验',
					showClose : false,
					content : "<iframe src='' id='logic' name='logic' style='height: " + height + "px;' frameborder='0'></iframe>"
				});
		tabObj.addTabItem({
					tabid : 'warn',
					text : '警示校验',
					showClose : false,
					content : "<iframe src='' id='warn' name='warn' style='height: " + height + "px;' frameborder='0'></iframe>"
				});
		tabObj.addTabItem({
					tabid : 'planval',
					text : '计划值校验',
					showClose : false,
					content : "<iframe src='' id='planval' name='planval' style='height: " + height + "px;' frameborder='0'></iframe>"
				});
		$("#logic").attr({
			src : "${ctx}/report/frame/rptvalid/logic/tab"
		});
		tabChangeFlag = true;
		tabObj.selectTabItem('logic');
		tabChange = "logic";
	};

	//tab展示，显示右侧的grid内容
	function initCurrentTab(type, indexCatalogNo, indexNo, indexVerId, name,
			infoRights) {
		if (tabChange == "logic") {
			curTabUri = "${ctx}/report/frame/rptvalid/logic/tab?indexCatalogNo="
				+ indexCatalogNo + "&indexNo="
				+ indexNo + "&indexVerId=" + indexVerId + "&indexNm="
				+ name;
			$("#logic").attr({
				src : curTabUri
			});
		}
		if (tabChange == "warn") {
			curTabUri = "${ctx}/report/frame/rptvalid/warn?indexCatalogNo="
				+ indexCatalogNo + "&indexNo="
				+ indexNo + "&indexVerId=" + indexVerId + "&indexNm="
				+ name;
			$("#warn").attr({
				src : curTabUri
			});
		}
		if (tabChange == "planval") {
			curTabUri = "${ctx}/rpt/frame/idx/planval/idxPlanvalInfos?indexCatalogNo="
				+ indexCatalogNo + "&indexNo="
				+ indexNo + "&indexVerId=" + indexVerId + "&indexNm="
				+ name;
			$("#planval").attr({
				src : curTabUri
			});
		}
	}
</script>

<title>指标信息</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx}/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12" id="left_up_span">指标目录</span>
	</div>
	<div id="template.left.down">
		<div id="btn"></div>
	</div>
	<div id="template.right">
		<div id="tab" style="width: 100%; overflow: hidden;"></div>
	</div>
</body>
</html>