<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>

<meta name="decorator" content="/template/template23.jsp">
<head>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/contextMenu/jquery.contextMenu.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/daterangepicker/daterangepicker.css" />
<%@ include file="/common/codemirror_load.jsp"%>
<script type="text/javascript"
	src="${ctx}/js/contextMenu/jquery.contextMenu.min.js"></script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript" src="${ctx}/js/datashow/ParamComp.js"></script>
<script type="text/javascript" src="${ctx}/js/require/require.js"></script>

<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<style>
.toggleBtn {
	padding-right: 10px;
	cursor: pointer;
	float: right;
	position: relative;
	margin-top: 8px;
	background: url('${ctx}/images/classics/ligerui/toggle.gif') no-repeat
		0px 0px;
	width: 9px;
	height: 10px;
}

.togglebtn-down {
	background-position: 0px -10px;
}
</style>
<script type="text/javascript">
	var paramId = "";
	var sqlModel = "sql";
	var dsCombo = null;
	var cursql = "";
	var curdsId = "";
	var grid = null;
	var sqlgrid = null;
	var modelFlag = false; //灵活查询TAB加载标识
	var detailFlag = false; //模板查询TAB加载标识
	var sqlFlag = false; //配置页面TAB加载标识
	var configFlag = false; //SQL页面TAB加载标识
	var panel = null; // 标签对象	
	var setId = "";
	var srcCode = "";
	var sqlEdit = null;
	var moduleTreeObj = null;
	var detailTreeObj = null;
	var leftTab = null;
	var modelTab = null;
	var tmpTab = null;
	var dsId = "";
	var grid = null;
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	var colInfos = {};
	var columns = [];
	var sqlcolumns = [];
	var params = [];
	var filterInfo = {};
	var colSorts = [];
	var colSums = [];
	var leftTable = null;
	var ztree = $.fn.zTree;
	var downdload = null;
	var catalogId = "${catalogId}";
	var catalogNm = "${catalogNm}";
	var templateId = "${templateId}";
	var dialog = null;
	var init = true;
	var usePager = true;
	var susePager = true;
	var pageSize = 2000;
	var moduleMap = {};
	var colflag = true;
	var paramCombo;
	var curSetIds = [];

	$(function() {
		$("#leftSpan").html("明细查询");
		initModelTab();
		initDsId();
		initModelTree();
		initData();
	});

	function initData() {
		if (templateId) {
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/datashow/detail/getTmpInfo",
				dataType : 'json',
				data : {
					templateId : templateId
				},
				type : "post",
				success : function(result) {
					if ((result.configs)&& (result.configs.length > 0)) {
						setId = result.configs[0].setId;
						if (setId) {
							$.ajax({
								cache : false,
								async : false,
								url : "${ctx}/report/frame/datashow/detail/getSrcCode",
								dataType : 'json',
								data : {
									setId : setId
								},
								type : "get",
								success : function(result) {
									srcCode = result.srcCode;
								},
								error : function(result, b) {
									BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
								}
							});
						}
					}
					templateType = result.tmp.templateType;
					dsCombo.selectValue(result.tmp.dsId);
					dsCombo.setDisabled();
					if (result.tmp.templateType == "01") {
						window.usePager = result.tmp.isPage == "1" ? true : false;
						if (!window.usePager) {
							$("#showGrid").find("#page").removeAttr(
									"checked");
							$("#showGrid").find(".l-panel-bar").hide();
							$("#showGrid").find(".l-grid").height(
									$("#showGrid").find(".l-grid")
											.height() + 30);
							$("#showGrid").find(".l-grid-body").height(
									$("#showGrid").find(".l-grid-body")
											.height() + 30);
							window.pageSize = 2000;
						}
						modelTab.removeTabItem('sqlTab');
						modelTab.selectTabItem('configTab');
						for ( var i in result.configs) {
							if (setId == "") {
								setId = result.configs[i].setId;
							}
							colInfos[result.configs[i].id.cfgId] = result.configs[i];
						}
						params = result.searchs;
						for ( var i in params) {
							params[i].uuid = params[i].id.paramId;
							if (params[i].required == "Y")
								params[i].required = true;
							else
								params[i].required = false;
							if (params[i].checkbox == "Y")
								params[i].checkbox = "true";
							else
								params[i].checkbox = "false";
							params[i].defValue = params[i].defVal;
							panel.add({
								id : params[i].id.paramId,
								text : params[i].cnNm
							});
							params[i].busiType = srcCode;
						}
						colSorts = result.sorts;
						colSums = result.sums;
						filterInfo = JSON2.parse(result.filter.filtInfo);
						createParams("searchForm", params);
						setColumn();
					} else {
						window.susePager = result.tmp.isPage == "1" ? true : false;
						if (!window.susePager) {
							$("#sqlgrid").find("#page").removeAttr(
									"checked");
							$("#sqlgrid").find(".l-panel-bar").hide();
							$("#sqlgrid").find(".l-grid").height(
									$("#sqlgrid").find(".l-grid")
											.height() + 30);
							$("#sqlgrid").find(".l-grid-body").height(
									$("#sqlgrid").find(".l-grid-body")
											.height() + 30);
							window.pageSize = 2000;
						}
						cursql = result.sql.querysql;
						curdsId = result.sql.dsId;
						modelTab.removeTabItem('configTab');
						modelTab.selectTabItem('sqlTab');
						sqlEdit.setValue(result.sql.querysql);
						if (result.sql.paramtmpId) {
							initSqlModel(result.sql.paramtmpId, result.param);
						} 
						init = false;
					}
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		} else {
			modelTab.selectTabItem('configTab');
		}
	}

	function initSqlModel(paramTmpId, param) {
		paramId = paramTmpId;
		$.ligerui.get("paramtmpIdBox")._changeValue(paramTmpId, param ? param.paramtmpNm : "");
		sqlModel = "param";
		createPage("temps", paramId);
	}
	
	function setColumn() {
		for ( var i in colInfos) {
			var column = {
				display : colInfos[i].displayNm,
				name : colInfos[i].id.cfgId,
				width : colInfos[i].width,
				align : "left"
			};
			columns.push(column);
		}
		grid.set("columns", columns);
		try {
			grid.reRender();
			addDivToColumn();
		} catch (e) {
			BIONE.tip('发现系统错误 <BR>：' + e);
		}
	}

	//初始化下载模块
	function initDownLoad() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}

	function initSql() {
		$("#sqlSpan").hide();
		$("#sqlEdit").hide();
		$("#paramSpan").show();
		$("#paramBox").show();
		$("#paramEdit").show();
		$("#paramtmpIdBox").show();
		var mime = "text/x-mysql";
		sqlEdit = CodeMirror.fromTextArea(document.getElementById('sql'), {
			mode : mime,
			indentWithTabs : true,
			smartIndent : true,
			lineNumbers : true,
			matchBrackets : true,
			autofocus : true
		});
		$(".CodeMirror.cm-s-default").css("width", "99%").css("height", "158px").css("padding-bottom", "0px");
		$("#sqlTab").find(".toggleBtn").bind(
				"click",
				function() {
					if ($(this).hasClass("togglebtn-down")) {
						$(this).removeClass("togglebtn-down");
						$("#sqlDiv").slideToggle(
								'fast',
								function() {
									if (grid) {
										sqlgrid.setHeight($(document).height() - $("#sqlDiv").height() - 60);
									}
								});
					} else {
						$(this).addClass("togglebtn-down");
						$("#sqlDiv").slideToggle('fast', function() {
							if (grid) {
								sqlgrid.setHeight($(document).height() - 60);
							}
						});
					}
				});
		$("#sqlQuery").bind(
				"click",
				function() {
					BIONE.commonOpenFullDialog("查询语句", "queryWin",
							"${ctx}/report/frame/datashow/detail/sql");
				});
		if (!paramCombo) {
			paramCombo = $("#paramtmpIdBox").ligerComboBox(
					{
						onBeforeOpen : function() {
							window.parent.selectedTab = window;
							window.parent.BIONE.commonOpenDialog(
									"参数模版目录",
									"paramtmpTreeWin",
									400,
									380,
									'${ctx}/frs/integratedquery/rptmesquery/info/showParamtmpTree',
									null,
									function() {
										paramId = $("#paramtmpIdBox_val").val();
										if ($("#paramtmpIdBox_val").val() == "") {
											$("#temps").html("");
										} else {
											createPage("temps", $("#paramtmpIdBox_val").val());
										}
									});
							return false;
						}
					});
		}
	}

	function initModelTab() {
		$("#model_tab").show();
		$("#tmp").hide();
		if (modelTab == null) {
			modelTab = $("#model_tab").ligerTab({
				contextmenu : false,
				onAfterSelectTabItem : function(tabId) {
					if (tabId == 'configTab') {
						if (!configFlag) {
							initPanel();
							initSearch();
							initModelGrid();
							configFlag = true;
						}
					}
					if (tabId == 'sqlTab') {
						if (!sqlFlag) {
							initSql();
							initSqlGrid();
							sqlFlag = true;
						}
					}
				}
			});
			createParams("searchForm", []);
		}
	}

	function initTmpTab() {
		$("#model_tab").hide();
		$("#tmp").show();
		tmpTab = $('#tmp_tab').ligerTab({
			contextmenu : true,
			height : $('#right').height() - 2,
			onAfterAddTabItem : function() {
				$('#cover').hide();
			},
			onAfterRemoveTabItem : function() {
				if (tmpTab.getTabItemCount() == 0) {
					$('#cover').show();
				}
			}
		});
	}

	function resizeLay() {
		$("#paneldiv").css("width", $("#right").width() - 90);
	}
	
	function initPanel() {
		$("#panel").css("height", "32px");
		$("#paneldiv").css("width", $("#right").width() - 90);
		panel = $('#panel').exlabel(
				{
					text : 'text',
					value : 'id',
					isEdit : false,
					isInput : false,
					callback : {
						remove : function(item) {
							deleteParam(item.id);
						},
						onClick : function(item) {
							BIONE.commonOpenDialog("查询配置", "searchEdit", 600,
									350,
									"${ctx}/report/frame/datashow/detail/paramconfig?uuid="
											+ item.id);
						}
					}
				});

		function deleteParam(id) {
			for (i = 0; i < params.length; i++) {
				if (params[i].uuid == id) {
					break;
				}
			}
			params.splice(i, 1);
			if (columns.length == 0 && params.length == 0) {
				setId = "";
			}
			createParams("searchForm", params);
			resSetId();
		}
	}

	function initDsId() {
		dsCombo = $("#dsId").ligerComboBox(
				{
					url : "${ctx}/rpt/frame/dataset/dsList.json",
					onSelected : function(id, text) {
						if (id != null && id != "") {
							curdsId = id;
							if (moduleTreeObj != null)
								BIONE.loadTree(
										"${ctx}/report/frame/design/cfg/getRowModuleTree?dsId="
												+ curdsId, moduleTreeObj, null,
										null, false);
						}
					}
				});
		$("#dsId").parent().width("99%");
		$.ligerui.get("dsId").selectValue("1");
	}

	function initSearch() {
		$("#searchDiv").show();
		$("#configTab")
				.find(".toggleBtn")
				.bind(
						"click",
						function() {
							if ($(this).hasClass("togglebtn-down")) {
								$(this).removeClass("togglebtn-down");
								$("#searchDiv").slideToggle('fast',function() {
									if (grid) {
										grid.setHeight($(document).height() - $("#searchDiv").height() - 60);
									}

								});
							} else {
								$(this).addClass("togglebtn-down");
								$("#searchDiv").slideToggle('fast',function() {
									if (grid) {
										grid.setHeight($(document).height() - 60);
									}
								});
							}
						});
	}

	function createParams(divName, params) {
		$('#temps-loading').css('display', 'block');
		var pJson = [];
		for ( var i in params) {
			var paramid = uuid.v1()
			paramid = paramid.split("-").join("");
			params[i].paramId = paramid;
			pJson.push(ParamTmp.generateModuleTmp(params[i]));
		}
		require.config({
			baseUrl : '${ctx}/js/',
			paths : {
				jquery : 'jquery/jquery-1.7.1.min',
				JSON2 : 'bione/json2.min'
			},
			shim : {
				JSON2 : {
					exports : 'JSON2'
				}
			}
		});
		require([ 'jquery', 'JSON2', '../plugin/js/template/viewMain' ], function() {
			checkBoxId = [];
			$('#' + divName).templateView({
				data : pJson
			});
			for ( var i in pJson) {
				if (pJson[i].checkbox == "true"
						|| pJson[i].isMultiSelect == "true") {
					checkBoxId.push(pJson[i].name);
				}
			}
			$.metadata.setType("attr", "validate");
			$('#temps-loading').css('display', 'none');
		});
	}

	function initModelTree() {
		var leftHeight = $("#left").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(leftHeight - 36 - $("#treeSearchbar").height());
		var async = {
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
			callback : {
				onNodeCreated : function(event, treeId, treeNode) {
					moduleTreeObj.expandNode(moduleTreeObj.getNodeByParam("id",
							"0", null), true, false, false);
					$("#" + treeNode.tId + "_a").addClass("tree_span");
					if (treeNode.params.type == "02")
						setDragDrop("#" + treeNode.tId + "_span", "#grid");
					if (treeNode.params.type == "04") {
						if (treeNode.params.realId == "@rowNum") {
							setDragDrop("#" + treeNode.tId + "_span",
									"#searchView");
						} else {
							setDragDrop("#" + treeNode.tId + "_span",
									"#grid , #searchView");
						}

					}
				},
				onClick : function(event, treeId, treeNode) {
					if (treeNode.params.type == "02") {
						var click = !$("#" + treeNode.tId + "_a").data("click");
						if (click) {
							$(".tree_span").css("background-color", "#fff");
							$(".tree_span").data("click", false);
							$("#" + treeNode.tId + "_a").css(
									"background-color", "#ddd");
							window.curNode = treeNode;
						} else {
							$("#" + treeNode.tId + "_a").css(
									"background-color", "#fff");
							window.curNode = null;
						}
						$("#" + treeNode.tId + "_a").data("click", click);
					} else {
						$(".tree_span").css("background-color", "#fff");
					}
				}
			}
		};
		moduleTreeObj = ztree.init($("#modeltree"), async, []);
		$("#treeSearchIcon")
				.live(
						'click',
						function() {// 树搜索
							if ($('#treeSearchInput').val() == ""
									&& window.curNode == null) {
								BIONE.loadTree(
										"${ctx}/report/frame/design/cfg/getRowModuleTree?dsId="
												+ dsId, moduleTreeObj, {
											searchNm : $('#treeSearchInput')
													.val()
										}, null, false);
							} else {
								if (window.curNode != null) {
									var searchNm = $('#treeSearchInput').val();
									if (!moduleMap[curNode.id])
										moduleMap[curNode.id] = window.curNode.children;
									//移除旧节点
									moduleTreeObj.removeChildNodes(
											window.curNode, false);
									// 渲染新节点
									var reNodes = $
											.grep(
													moduleMap[curNode.id],
													function(n, i) {
														if (searchNm) {
															return (n.text
																	.indexOf(searchNm) >= 0);
														} else {
															return true;
														}
													});
									moduleTreeObj.addNodes(curNode, reNodes,
											true);
									moduleTreeObj.expandNode(curNode, true);
								} else {
									BIONE
											.loadTree(
													"${ctx}/report/frame/design/cfg/getRowModuleTree?dsId="
															+ dsId,
													moduleTreeObj,
													{
														searchNm : $(
																'#treeSearchInput')
																.val()
													});
								}
							}

						});
		$('#treeSearchInput')
				.bind(
						'keydown',
						function(e) {
							if (e.keyCode == 13) {
								if ($('#treeSearchInput').val() == ""
										&& window.curNode == null) {
									BIONE
											.loadTree(
													"${ctx}/report/frame/design/cfg/getRowModuleTree?dsId="
															+ dsId,
													moduleTreeObj,
													{
														searchNm : $(
																'#treeSearchInput')
																.val()
													}, null, false);
								} else {
									if (window.curNode != null) {
										var searchNm = $('#treeSearchInput')
												.val();
										if (!moduleMap[curNode.id])
											moduleMap[curNode.id] = window.curNode.children;
										//移除旧节点
										moduleTreeObj.removeChildNodes(
												window.curNode, false);
										// 渲染新节点
										var reNodes = $
												.grep(
														moduleMap[curNode.id],
														function(n, i) {
															if (searchNm) {
																return (n.text
																		.indexOf(searchNm) >= 0);
															} else {
																return true;
															}
														});
										moduleTreeObj.addNodes(curNode,
												reNodes, true);
										moduleTreeObj.expandNode(curNode, true);

									} else {
										BIONE.loadTree(
												"${ctx}/report/frame/design/cfg/getRowModuleTree?dsId="
														+ dsId, moduleTreeObj,
												{
													searchNm : $(
															'#treeSearchInput')
															.val()
												});
									}
								}
							}
						});
	}

	function initDetaillTree() {
		var leftHeight = $("#left").height();
		var $treeContainer = $("#detailTab").find("#treeContainer");
		$treeContainer.height(leftHeight - 56 - $("#treeSearchbar").height());
		var async = {
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
			callback : {
				onClick : function(treeId, event, treeNode) {
					if (treeNode.params.nodeType == "03") {
						tmpTab
								.addTabItem({
									tabid : treeNode.id,
									text : treeNode.text,
									url : '${ctx}/report/frame/datashow/detail/showinfo?templateId='
											+ treeNode.id,
									showClose : true
								});
					}
				}
			}
		};
		detailTreeObj = ztree.init($("#detailtree"), async, []);
		BIONE.loadTree("${ctx}/report/frame/datashow/detail/getDetailTree",
				detailTreeObj, null, null, false);
		$("#detailTab")
				.find("#treeSearchIcon")
				.bind(
						"click",
						function() {
							if ($("#detailTab").find("#treeSearchInput").val() == "") {
								BIONE
										.loadTree(
												"${ctx}/report/frame/datashow/detail/getDetailTree",
												detailTreeObj,
												{
													searchNm : $("#detailTab")
															.find(
																	"#treeSearchInput")
															.val()
												}, null, false);
							} else {
								BIONE
										.loadTree(
												"${ctx}/report/frame/datashow/detail/getDetailTree",
												detailTreeObj,
												{
													searchNm : $("#detailTab")
															.find(
																	"#treeSearchInput")
															.val()
												});
							}
						});
		$("#detailTab")
				.find("#treeSearchInput")
				.bind(
						"keydown",
						function(e) {
							if (e.keyCode == 13) {
								if ($("#detailTab").find("#treeSearchInput")
										.val() == "") {
									BIONE
											.loadTree(
													"${ctx}/report/frame/datashow/detail/getDetailTree",
													detailTreeObj,
													{
														searchNm : $(
																"#detailTab")
																.find(
																		"#treeSearchInput")
																.val()
													}, null, false);
								} else {
									BIONE
											.loadTree(
													"${ctx}/report/frame/datashow/detail/getDetailTree",
													detailTreeObj,
													{
														searchNm : $(
																"#detailTab")
																.find(
																		"#treeSearchInput")
																.val()
													});
								}
							}
						});

	}

	function initSqlGrid() {
		var columns = [];
		sqlgrid = $('#sqlgrid').ligerGrid({
			toolbar : {},
			width : '100%',
			columns : columns,
			usePager : window.susePager,
			checkbox : false,
			dataAction : 'server',
			allowHideColumn : false,
			delayLoad : true,
			url : "${ctx}/report/frame/datashow/detail/showSql",
			onSuccess : function(result) {
				if (result.error) {
					BIONE.tip(result.error);
					return;
				}
				if (!colflag) {
					var sqlcolumns = [];
					for ( var i in result.column) {
						var column = {
							display : result.column[i],
							name : "param" + i,
							width : '20%',
							align : "left"
						};
						sqlcolumns.push(column);
						sqlgrid.set("columns", sqlcolumns);
						sqlgrid.reRender();
					}
					colflag = true;
				}
			}
		});
		var buttons = [
				{
					text : "预览",
					icon : "search3",
					click : function() {
						colflag = false;
						var dsId = $("#dsId_val").val();
						var sql = sqlEdit.getValue();
						if (!window.susePager) {
							sqlgrid.setParm("pagesize", window.pageSize);
							sqlgrid.options.pagesize = window.pageSize;
						}
						if (cursql != sql || curdsId != dsId) {
							cursql = sql;
							curdsId = dsId;
							if (window.paramId != "") {
								BIONE.validate($("#temps"));
								if ($("#temps").valid()) {
									var searchArgs = $('#temps').getJson();
									sqlgrid.setParm("searchArgs", JSON2
											.stringify(searchArgs));
								} else {
									return;
								}

							}
							sqlgrid.setParm("sql", sql);
							sqlgrid.setParm("dsId", dsId);
							sqlgrid.setParm("newPage", 1);
							sqlgrid.options.newPage = 1
							sqlgrid.loadData();
						} else {
							if (window.paramId != "") {
								BIONE.validate($("#temps"));
								if ($("#temps").valid()) {
									var searchArgs = $('#temps').getJson();
									sqlgrid.setParm("searchArgs", JSON2
											.stringify(searchArgs));
								} else {
									return;
								}
							}
							var searchArgs = $('#temps').getJson();
							sqlgrid.setParm("searchArgs", JSON2
									.stringify(searchArgs));
							sqlgrid.setParm("sql", sql);
							sqlgrid.setParm("dsId", dsId);
							sqlgrid.setParm("newPage", 1);
							sqlgrid.options.newPage = 1
							sqlgrid.loadData();
						}
					}
				},
				{
					text : "重置SQL",
					icon : "refresh2",
					click : function() {
						//解决重置sql按钮失效
						sqlEdit.setValue("");
					}
				},
				{
					text : "清空",
					icon : "delete",
					click : function() {
						sqlEdit.setValue("");
						window.sqlcolumns = [];
						sqlgrid.set("columns", sqlcolumns);
						sqlgrid.reRender();
					}
				},
				{
					text : '保存',
					click : function() {
						if (templateId == "") {
							var dsId = $("#dsId_val").val();
							var sql = sqlEdit.getValue();
							if (dsId == "") {
								BIONE.tip("未选择数据源！");
								return;
							}
							if (sql == "") {
								BIONE.tip("未填写Sql！");
								return;
							}
							if (sql != cursql) {
								BIONE.tip("检测到sql变化，请先预览数据再保存！");
								return;
							}
							dialog = BIONE
									.commonOpenDialog("模板信息", "infoEdit", 600,
											350,
											"${ctx}/report/frame/datashow/detail/tmpinfo?templateType=02&defSrc=01");
						} else {
							var dsId = $("#dsId_val").val();
							var sql = sqlEdit.getValue();
							if (dsId == "") {
								BIONE.tip("未选择数据源！");
								return;
							}
							if (sql == "") {
								BIONE.tip("未填写Sql！");
								return;
							}
							if (sql != cursql) {
								BIONE.tip("检测到sql变化，请先预览数据再保存！");
								return;
							}
							var data = prepareSqlInfo(templateId);
							data.templateId = templateId;

							$
									.ajax({
										cache : false,
										async : true,
										url : "${ctx}/report/frame/datashow/detail/updateTmp",
										dataType : 'json',
										data : data,
										type : "post",
										beforeSend : function() {
											BIONE.loading = true;
											BIONE.showLoading("正在保存数据中...");
										},
										complete : function() {
											BIONE.loading = false;
											BIONE.hideLoading();
										},
										success : function(result) {
											BIONE.tip("保存成功");
											BIONE.closeDialog("addWin");

										},
										error : function(result, b) {
										}
									});
						}

					},
					icon : "save"
				} ];
		var checked = "checked='true'";
		if (!window.susePager) {
			checked = "";
		}
		$("#sqlgrid")
				.find(".l-panel-topbar")
				.append(
						"<span>分页</span><div style='float:left;margin-top:5px'><input id='page' type='checkbox' "+checked+" ></input></div>");
		$("#sqlgrid").find("#page").bind(
				"click",
				function() {
					if ($(this)[0].checked) {
						window.susePager = true;
						$("#sqlgrid").find(".l-panel-bar").show();
						$("#sqlgrid").find(".l-grid").height(
								$("#sqlgrid").find(".l-grid").height() - 30);
						$("#sqlgrid").find(".l-grid-body")
								.height(
										$("#sqlgrid").find(".l-grid-body")
												.height() - 30);
						for (var i = 0; i < sqlgrid.options.parms.length; i++) {
							if (sqlgrid.options.parms[i].name == "pagesize") {
								sqlgrid.options.parms.splice(i, 1);
								break;
							}

						}
						;
						sqlgrid.options.pagesize = parseInt($("#sqlgrid").find(
								"select[name=rp]").val());
						sqlgrid.set("usePager", true);
					} else {
						sqlgrid.set("usePager", false)
						$("#sqlgrid").find(".l-panel-bar").hide();
						$("#sqlgrid").find(".l-grid").height(
								$("#sqlgrid").find(".l-grid").height() + 30);
						$("#sqlgrid").find(".l-grid-body")
								.height(
										$("#sqlgrid").find(".l-grid-body")
												.height() + 30);
						window.susePager = false;
						window.pageSize = 2000;
					}
				});
		sqlgrid.setHeight($(document).height() - $("#sqlDiv").height() - 60);
		BIONE.loadToolbar(sqlgrid, buttons);
	}

	function getColumnInfo(flag) {
		var dsId = $("#dsId_val").val();
		var sql = sqlEdit.getValue();
		var retflag = false;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/datashow/detail/getColumnInfo",
			dataType : 'json',
			data : {
				dsId : dsId,
				sql : sql
			},
			type : "post",
			beforeSend : function() {
			},
			complete : function() {
			},
			success : function(result) {
				if (result.error != null && result.error != "") {
					BIONE.tip(result.error);
					retflag = false;
				} else {
					if (flag) {
						sqlcolumns = [];
						for ( var i in result.columns) {
							var column = {
								display : result.columns[i],
								name : "param" + i,
								width : '15%',
								align : "left"
							};
							sqlcolumns.push(column);
							sqlgrid.set("columns", sqlcolumns);
							sqlgrid.reRender();

						}
					}
					retflag = true;
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
		return retflag;
	}

	function initModelGrid(columns) {
		var columns = [];
		grid = $('#grid').ligerGrid(
				{
					toolbar : {},
					width : '100%',
					columns : columns,
					usePager : window.usePager,
					checkbox : false,
					dataAction : 'server',
					allowHideColumn : false,
					delayLoad : true,
					url : "${ctx}/report/frame/datashow/detail/showView",
					onBeforeShowData : function() {

					},
					onAfterChangeColumnWidth : function() {
						var columns = grid.getColumns();
						for (var i = 0; i < columns.length; i++) {
							var $selectedLi = $(
									"td[columnname=" + columns[i].name + "]")
									.find("div:first");
							colInfos[columns[i].name].width = $selectedLi
									.width()
						}
						$(".delBtn").each(
								function() {
									$(this)
											.css(
													"left",
													$(this).parent().parent()
															.width() - 10);
								})
					},
					onAfterShowData : function() {
					},
					onBeforeChangeColumnWidth : function() {
					},
					mouseoverRowCssClass : null
				});
		var buttons = [
				{
					text : "",
					icon : "detail",
					click : function() {
						BIONE
								.commonOpenDialog("列排序配置", "colsortEdit", 300,
										350,
										"${ctx}/report/frame/datashow/detail/colsortconfig");
					}

				},
				{
					text : "预览",
					icon : "search3",
					click : function() {
						var configs = [];
						for ( var i in colInfos) {
							configs.push(colInfos[i]);
						}
						try {
							BIONE.validate($("#searchForm"));
							if ($("#searchForm").valid()) {
								grid.setParm("newPageSize", null);
								if (!window.usePager) {
									grid.setParm("pagesize", window.pageSize);
									grid.options.pagesize = window.pageSize;
								}
								var searchArgs = $('#searchForm').getJson();
								var sargs = [];
								for (var i = 0; i < searchArgs.length; i++) {
									if (searchArgs[i].name == "@rowNum") {
										if (window.usePager)
											grid.setParm("newPageSize",
													searchArgs[i].value);
										else {
											grid.setParm("pagesize",
													searchArgs[i].value);
											grid.options.pagesize = searchArgs[i].value;
										}
										continue;
									}
									var info = searchArgs[i].name.split("|");
									if (info.length >= 2) {
										searchArgs[i].name = info[0];
										searchArgs[i].colId = info[1];
									}
									sargs.push(searchArgs[i]);
								}
								searchArgs = sargs;
								grid.setParm("searchInfo", JSON2
										.stringify(searchArgs));
								grid
										.setParm("config", JSON2
												.stringify(configs));
								grid.setParm("filterInfo", JSON2
										.stringify(filterInfo));
								if (colSorts.length > 0)
									grid.setParm("sort", JSON2
											.stringify(colSorts));
								else {
									grid.setParm("sort", "");
								}
								if (colSums.length > 0)
									grid.setParm("sum", JSON2
											.stringify(colSums));
								else {
									grid.setParm("sum", "");
								}
								grid.setParm("newPage", 1);
								grid.options.newPage = 1;
								grid.loadData();
							}
						} catch (e) {
						}

					}
				},
				{
					text : "保存",
					icon : "save",
					click : function() {
						if (templateId == "") {
							if (grid.getColumns().length > 0) {
								dialog = BIONE
										.commonOpenDialog("模板信息", "infoEdit",
												600, 350,
												"${ctx}/report/frame/datashow/detail/tmpinfo?templateType=01&defSrc=01");
							} else {
								BIONE.tip("未配置查询列");
							}
						} else {
							var data = prepareSaveInfo(templateId);
							data.templateId = templateId;
							$
									.ajax({
										cache : false,
										async : true,
										url : "${ctx}/report/frame/datashow/detail/updateTmp",
										dataType : 'json',
										data : data,
										type : "post",
										beforeSend : function() {
											BIONE.loading = true;
											BIONE.showLoading("正在保存数据中...");
										},
										complete : function() {
											BIONE.loading = false;
											BIONE.hideLoading();
										},
										success : function(result) {
											BIONE.tip("保存成功");
											BIONE.closeDialog("addWin");

										},
										error : function(result, b) {
										}
									});
						}
					}
				},
				{
					text : "操作",
					icon : "config",
					menu : {
						items : [ {
							text : "重置查询",
							icon : "refresh2",
							click : function() {
								$("#searchForm").resetForm();
							}
						}, {
							text : "清空",
							icon : "delete",
							click : function() {
								panel.removeAll();
								params = [];
								createParams("searchForm", params);
								window.colInfos = {};
								window.columns = [];
								setId = "";
								grid.set("columns", []);
								grid.reRender();
							}
						} ]
					}
				},
				{
					text : "配置",
					icon : "config",
					menu : {
						items : [
								{
									text : '排序配置',
									click : function() {
										BIONE
												.commonOpenDialog("排序配置",
														"sortEdit", 600, 350,
														"${ctx}/report/frame/datashow/detail/sortconfig");
									},
									icon : "settings"
								},
								{
									text : '高级筛选',
									click : function() {
										BIONE
												.commonOpenDialog("高级筛选",
														"filterEdit", 800, 450,
														"${ctx}/report/frame/datashow/detail/filterconfig");
									},
									icon : "settings"
								},
								{
									text : '汇总配置',
									click : function() {
										BIONE
												.commonOpenDialog("汇总配置",
														"sumEdit", 800, 450,
														"${ctx}/report/frame/datashow/detail/sumconfig");
									},
									icon : "settings"
								} ]
					}
				} ];
		BIONE.loadToolbar(grid, buttons);
		grid.setHeight($(document).height() - $("#searchDiv").height() - 60);
		var checked = "checked='true'";
		if (!window.usePager) {
			checked = "";
		}
		$("#showGrid")
				.find(".l-panel-topbar")
				.append(
						"<span>分页</span><div style='float:left;margin-top:5px'><input id='page' type='checkbox' "+checked+" ></input></div>");
		if (!window.usePager) {
			grid.set("usePager", false)
			$("#showGrid").find(".l-panel-bar").hide();
			$("#showGrid").find(".l-grid").height(
					$("#showGrid").find(".l-grid").height() + 30);
			$("#showGrid").find(".l-grid-body").height(
					$("#showGrid").find(".l-grid-body").height() + 30);
			window.pageSize = 2000;
		}
		$("#page").bind(
				"click",
				function() {
					if ($(this)[0].checked) {
						window.usePager = true;
						$("#showGrid").find(".l-panel-bar").show();
						$("#showGrid").find(".l-grid").height(
								$("#showGrid").find(".l-grid").height() - 30);
						$("#showGrid").find(".l-grid-body")
								.height(
										$("#showGrid").find(".l-grid-body")
												.height() - 30);
						for (var i = 0; i < grid.options.parms.length; i++) {
							if (grid.options.parms[i].name == "pagesize") {
								grid.options.parms.splice(i, 1);
								break;
							}

						}
						;
						delete grid.options.parms["pagesize"];
						grid.options.pagesize = parseInt($("#showGrid").find(
								"select[name=rp]").val());
						grid.set("usePager", true);
					} else {
						grid.set("usePager", false)
						$("#showGrid").find(".l-panel-bar").hide();
						$("#showGrid").find(".l-grid").height(
								$("#showGrid").find(".l-grid").height() + 30);
						$("#showGrid").find(".l-grid-body")
								.height(
										$("#showGrid").find(".l-grid-body")
												.height() + 30);
						window.usePager = false;
						window.pageSize = 2000;
					}
				});

	}
	
	function prepareSqlInfo(tmpId) {
		var sql = {
			templateId : tmpId,
			querysql : cursql,
			paramtmpId : paramId,
			dsId : $("#dsId_val").val()
		};

		var data = {
			sql : JSON2.stringify(sql)
		}
		data.dsId = $("#dsId_val").val();
		data.isPage = window.susePager ? "1" : "0";
		return data;
	}
	
	function prepareSaveInfo(tmpId) {
		for ( var i in params) {
			params[i].orderno = (parseInt(i) + 1);
			params[i].id = {
				templateId : tmpId,
				paramId : params[i].uuid
			};
			params[i].required = (params[i].required == "true" || params[i].required == true) ? "Y"
					: "N";
			params[i].checkbox = (params[i].checkbox == "true" || params[i].checkbox == true) ? "Y"
					: "N";
			params[i].defVal = params[i].defValue;
		}
		var cols = [];
		for ( var i in colInfos) {
			colInfos[i].id.templateId = tmpId;
			cols.push(colInfos[i]);
		}
		for ( var i in colSorts) {
			colSorts[i].id.templateId = tmpId;
		}
		for ( var i in colSums) {
			colSums[i].id.templateId = tmpId;
		}
		var data = {
			params : JSON2.stringify(params),
			cols : JSON2.stringify(cols),
			colSorts : JSON2.stringify(colSorts),
			colSums : JSON2.stringify(colSums),
			filterInfo : JSON2.stringify(filterInfo)
		};
		data.dsId = $("#dsId_val").val();
		data.isPage = window.usePager ? "1" : "0";
		return data;
	}
	
	function addDivToColumn() {
		for (var i = 0; i < columns.length; i++) {
			var columnName = columns[i].name;
			addColBtn(columnName);
			//addColMenu(columnName);
		}
	}

	function addColBtn(columnName) {
		var img = $("td[columnname=" + columnName
				+ "] .l-grid-hd-cell-inner > #delBtn");
		if (img.length == 0) {
			var $selectedLi = $("td[columnname=" + columnName
					+ "] .l-grid-hd-cell-inner ");
			var delImg = $(
					"<img src='${ctx}/images/classics/icons/icons_label_cross.png' colId = '"+columnName+"' style='width:7px;height:7px' class='l-grid-dim-filter' />")
					.attr('title', '删除');
			delImg.click(function() {
				deleteCol(this, $(this).attr("colId"));
			});
			var cofImg = $(
					"<img src='${ctx}/images/classics/icons/cog.png' colId = '"+columnName+"' style='width:16px;height:16px' class='l-grid-dim-filter' />")
					.attr('title', '配置');
			cofImg.click(function() {
				openConfigDialog($(this).attr("colId"));
			});
			var deldiv = $("<div id='delBtn' class='delBtn' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
			deldiv.css("left", ($selectedLi.width() - 10)).css(
					"top",
					0 - $(".l-panel-topbar").height()
							- $selectedLi.position().top + 10).css("width",
					"7px");
			deldiv.append(delImg);
			$selectedLi.append(deldiv);
			var cofdiv = $("<div id='cofdiv' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
			cofdiv.css("left", "10px").css(
					"top",
					0 - $(".l-panel-topbar").height()
							- $selectedLi.position().top + 5).css("width",
					"7px");
			cofdiv.append(cofImg);
			$selectedLi.append(cofdiv);
		}
		
		function deleteCol(g, colId) {
			delete colInfos[colId];
			for (i = 0; i < columns.length; i++) {
				if (columns[i].name == colId) {
					break;
				}
			}
			columns.splice(i, 1);
			if (columns.length == 0 && params.length == 0) {
				setId = "";
			}
			grid.set("columns", columns);
			grid.reRender();
			addDivToColumn();
			resSetId();
		}
	}

	//添加拖拽控制
	function setDragDrop(dom, receiveDom) {
		if (typeof dom == "undefined" || dom == null) {
			return;
		}
		$(dom)
				.ligerDrag(
						{
							proxy : function(target, g, e) {
								var defaultName = "";
								var proxyLabel = "${ctx}" + notAllowedIcon;
								var targetTitle = $(dom).parent().attr("title") == null ? defaultName
										: defaultName + ":"
												+ $(dom).parent().attr("title");
								var proxyHtml = $('<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;"><span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('
										+ proxyLabel
										+ ')" ></span><span style="padding-left: 14px;">'
										+ targetTitle + '</span></div>');
								var div = $(proxyHtml);
								div.appendTo('body');
								return div;
							},
							revert : false,
							receive : receiveDom,
							onStartDrag : function(current, e) {
								// 获取拖拽树节点信息
								var treeAId = current.target.attr("id");
								var treeId = treeAId;
								if (treeAId) {
									var strsTmp = treeAId.split("_");
									var treeId = treeAId;
									if (strsTmp.length > 1) {
										var newStrsTmp = [];
										for (var i = 0; i < strsTmp.length - 1; i++) {
											newStrsTmp.push(strsTmp[i]);
										}
										treeId = newStrsTmp.join("_");
									}
									draggingNode = moduleTreeObj
											.getNodeByTId(treeId);
								}
							},
							onDragEnter : function(receive, source, e) {
								var allowLabel = "${ctx}" + allowedIcon;
								source.children(".dragimage_span").css(
										"background",
										"url('" + allowLabel + "')");
							},
							onDragOver : function(receive, source, e) {
								var allowLabel = "${ctx}" + allowedIcon;
								source.children(".dragimage_span").css(
										"background",
										"url('" + allowLabel + "')");
							},
							onDragLeave : function(receive, source, e) {
								var notAllowLabel = "${ctx}" + notAllowedIcon;
								source.children(".dragimage_span").css(
										"background",
										"url('" + notAllowLabel + "')");
							},
							onDrop : function(obj, target, e) {
								if (draggingNode.params.type == "02") {
									if (setId == "") {
										setId = draggingNode.params.realId;
										srcCode = draggingNode.params.srcCode;
										if (srcCode == "") {
											srcCode == "01";
										}
									} else {
										if(draggingNode.data){
											if (window.curSetIds
													&& $.inArray(
																	draggingNode.data.setId,
																	window.curSetIds) < 0) {
												BIONE.tip("请拖拽同一数据模型的字段", 2000);
												return;
											}
										}else{
											if(!window.curSetIds){
												resSetId(draggingNode.id);
											}
											if (window.curSetIds
													&& $.inArray(draggingNode.id,
																	window.curSetIds) < 0) {
												BIONE.tip("请拖拽同一数据模型的字段", 2000);
												return;
											}
										}
									}
								} else {
									if (setId == "") {
										setId = draggingNode.data.setId;
										srcCode = draggingNode.params.srcCode;
										if (srcCode == "") {
											srcCode == "01";
										}
									} else {
										if(!window.curSetIds){
											resSetId(draggingNode.data.setId);
										}
										if (window.curSetIds
												&& $
														.inArray(
																draggingNode.data.setId,
																window.curSetIds) < 0) {
											BIONE.tip("请拖拽同一数据模型的字段", 2000);
											return;
										}
									}
								}
								if ($(obj).attr("id") == "grid") {
									if (draggingNode.params.type == "02") {
										var nodes = moduleTreeObj
												.getNodesByParam("upId",
														draggingNode.id, null);
										addColumns(nodes);
									} else {
										addColumn(draggingNode);
									}
								} else {
									if ($("#configTab").find(".toggleBtn")
											.hasClass("togglebtn-down")) {
										$("#configTab").find(".toggleBtn")
												.addClass("togglebtn-down");
										$("#searchDiv").slideToggle('fast', function() {
											if (grid) {
												grid.setHeight($(document).height() - $("#searchDiv").height() - 60);
											}
											addParam();
										});
									} else {
										addParam();
									}
								}
							}
						});
	}

	function getParam(uuid) {
		for ( var i in params) {
			if (params[i].uuid == uuid) {
				return params[i];
			}
		}
		return null;
	}

	function equalarray(a1, a2) {
		if (a1.length != a2.length)
			return false;
		for (var i = 0; i < a1.length; i++) {
			var j = $.inArray(a1[i], a2);
			if (j < 0) {
				return false;
			} else {
				a2.splice(j, 1);
			}
		}
		return true;
	}

	function resSetId(param) {
		$("#modelTab").find("#treeSearchInput").val("");
		var setIds = [];
		if(param){
			setIds.push(param);
		}
		for ( var i in colInfos) {
			var setId = colInfos[i].setId;
			if ($.inArray(setId, setIds) < 0) {
				setIds.push(setId);
			}
		}
		for (var j = 0; j < params.length; j++) {
			var setId = params[j].setId;
			if(setId){
				if ($.inArray(setIds, setId) < 0) {
					setIds.push(setId);
				}
			}
		}
		if (setIds.length > 0) {
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/datashow/detail/getSetId",
				dataType : 'json',
				data : {
					setIds : setIds.join(",")
				},
				type : "post",
				success : function(result) {
					if (!equalarray(window.curSetIds, result)) {
						window.curSetIds = result;
						BIONE.loadTree(
								"${ctx}/report/frame/design/cfg/getRowModuleTree?dsId="
										+ dsId, moduleTreeObj, {
									setIds : result.join(",")
								}, null, true);
					}

				}
			});
		} else {
			window.curSetIds = [];
			window.srcCode = "";
			BIONE.loadTree(
					"${ctx}/report/frame/design/cfg/getRowModuleTree?dsId="
							+ dsId, moduleTreeObj, null, null, false);
		}

	}

	function addParam() {
		var type = "09";
		var uid = uuid.v1();
		uid = uid.split("-").join("");
		var enNm = draggingNode.params.realId;
		var data = [ {
			id : "10",
			text : "10"
		}, {
			id : "20",
			text : "20"
		}, {
			id : "30",
			text : "30"
		} ];
		if (enNm != "@rowNum") {
			enNm = draggingNode.data.enNm + "|" + draggingNode.data.colId;
			if (draggingNode.data.dimTypeNo) {
				enNm += "|" + draggingNode.data.dimTypeNo;
			}
			type = getParamsType(draggingNode.data);
			data = [];
		}
		checkbox = "false";
		if (type == "04"
				|| (type == "03" && draggingNode.params.dimTypeStruct == "02")) {
			checkbox = "true";
		}
		var param = {
			setId : draggingNode.data.setId,
			daterange : "",
			required : false,
			paramId : "",
			uuid : uid,
			enNm : enNm,
			cnNm : draggingNode.data.cnNm,
			colId : draggingNode.data.colId,
			dimTypeNo : draggingNode.data.dimTypeNo,
			dimTypeStruct : draggingNode.params.dimTypeStruct,
			elementType : type,
			orderno : params.length,
			required : false,
			checkbox : checkbox,
			defValue : "",
			isConver : "1",
			dataJson : JSON2.stringify(data),
			busiType : draggingNode.params.busiType
		};

		params.push(param);
		panel.add({
			id : uid,
			text : draggingNode.data.cnNm
		});
		createParams("searchForm", params);
		resSetId();
	}

	function getParams(id) {
		var vals = [];
		for (var i = 0; i < params.length; i++) {
			if (id && params[i].uuid == id) {
				var val = {
					type : "-1"
				}
				vals.push(val);
				continue;
			}
			if (params[i].elementType == "01") {
				var val = {
					type : "01",
					op : $.ligerui.get(params[i].paramId).getValue(),
					val : $("#" + params[i].paramId).prev().find("input").val()
				};
				vals.push(val);
			}
			if (params[i].elementType == "02") {
				var val = {
					type : "02",
					op : $.ligerui.get(params[i].paramId).getValue(),
					val : $("#" + params[i].paramId).prev().find("input").val()
				};
				vals.push(val);
			}
			if (params[i].elementType == "03") {
				var val = {
					type : "03",
					val : $.ligerui.get(params[i].paramId).getValue(),
					text : $.ligerui.get(params[i].paramId).getText()
				};
				vals.push(val);
			}
			if (params[i].elementType == "04") {
				var val = {
					type : "04",
					val : $.ligerui.get(params[i].paramId).getValue(),
					text : $.ligerui.get(params[i].paramId).getText()
				};
				vals.push(val);
			}
			if (params[i].elementType == "05") {
				var val = {
					type : "05",
					val : $.ligerui.get(params[i].paramId).getValue()
				};
				vals.push(val);
			}
			if (params[i].elementType == "06") {
				var val = {
					type : "06",
					val : $.ligerui.get(params[i].paramId).getValue()
				};
				vals.push(val);
			}
			if (params[i].elementType == "07") {
				var val = {
					type : "07",
					val : $.ligerui.get(params[i].paramId).getValue(),
					text : $.ligerui.get(params[i].paramId).getText()
				};
				vals.push(val);
			}
			if (params[i].elementType == "08") {
				var val = {
					type : "08",
					start : $(
							$("#" + params[i].paramId).parent().parent().find(
									"input")[0]).val(),
					end : $(
							$("#" + params[i].paramId).parent().parent().find(
									"input")[1]).val()
				};
				vals.push(val);
			}
		}
		return vals;
	}

	function getParamsType(data) {
		if (data.dbType == "" && data.dimTypeNo == "DATE") {
			return "00";
		} else if (data.dbType == "") {
			return "09";
		} else if (data.dbType == "03" || data.dbType == "04"
				|| data.dimTypeNo == "DATE") {
			return "05";
		} else if (data.dimTypeNo == "ORG") {
			return "04";
		} else if (data.dimTypeNo && data.dimTypeNo != "") {
			return "03";
		} else if (data.dbType == "02") {
			return "02";
		} else {
			return "01";
		}
	}
	
	function addColumns(nodes) {
		for (var i = 0; i < nodes.length; i++) {
			if (nodes[i].params.realId == "@rowNum") {
				continue
			}
			var draggingNode = nodes[i];
			var cfgId = uuid.v1()
			cfgId = cfgId.split("-").join("");
			var width = draggingNode.text.length * 12 + 70 > 150 ? draggingNode.text.length * 12 + 70
					: 150;
			var column = {
				display : draggingNode.text,
				name : cfgId,
				width : width,
				align : "left"
			};
			var displayFormat = "00";
			if (draggingNode.data.dbType == "03")
				displayFormat = "yyyy年MM月dd日";
			var colInfo = {
				id : {
					cfgId : cfgId
				},
				enNm : draggingNode.data.enNm,
				displayNm : draggingNode.text,
				colId : draggingNode.data.colId,
				setId : draggingNode.data.setId,
				srcCode : draggingNode.params.srcCode,
				isConver : "Y",
				isSum : "N",
				width : width,
				orderno : columns.length,
				dataType : draggingNode.data.dbType,
				dataUnit : "01",
				displayFormat : displayFormat,
				dimTypeNo : draggingNode.data.dimTypeNo,
				dataPrecision : 2
			};
			colInfos[cfgId] = colInfo;
			columns.push(column);
		}
		grid.set("columns", columns);
		grid.reRender();
		addDivToColumn();
		resSetId();
	}

	function addColumn(draggingNode) {
		var cfgId = uuid.v1()
		cfgId = cfgId.split("-").join("");
		var width = draggingNode.text.length * 12 + 70 > 150 ? draggingNode.text.length * 12 + 70
				: 150;
		var column = {
			display : draggingNode.text,
			name : cfgId,
			width : width,
			align : "left"
		};
		var displayFormat = "00";
		if (draggingNode.data.dbType == "03")
			displayFormat = "yyyy年MM月dd日";
		var colInfo = {
			id : {
				cfgId : cfgId
			},
			displayNm : draggingNode.text,
			colId : draggingNode.data.colId,
			setId : draggingNode.data.setId,
			isConver : "Y",
			isSum : "N",
			width : width,
			orderno : columns.length,
			dataType : draggingNode.data.dbType,
			dataUnit : "01",
			displayFormat : displayFormat,
			dimTypeNo : draggingNode.data.dimTypeNo,
			dataPrecision : 2
		};
		colInfos[cfgId] = colInfo;
		columns.push(column);
		grid.set("columns", columns);
		grid.reRender();
		addDivToColumn();
		resSetId();
	}
	
	function openConfigDialog(cfgId) {
		BIONE.commonOpenDialog("列配置", "attrEdit", 600, 350,
						"${ctx}/report/frame/datashow/detail/attrconfig?cfgId="
								+ cfgId);
	}

	function closeDialog() {
		if (dialog != null) {
			dialog.close();
		}
		BIONE.tip("保存成功");
		if (window.parent.tmpGrid) {
			window.parent.tmpGrid.loadData();
		}
		BIONE.closeDialog("addWin");
	}

	function setColumn() {
		columns = [];
		for ( var i in colInfos) {
			var column = {
				display : colInfos[i].displayNm,
				name : colInfos[i].id.cfgId,
				width : colInfos[i].width,
				align : "left"
			};
			columns.push(column);
		}
		grid.set("columns", columns);
		try {
			grid.reRender();
			addDivToColumn();
		} catch (e) {

		}
	}

	function createPage(divName, paramtmpId) {
		$('#temps-loading').css('display', 'block');
		require.config({
			baseUrl : '${ctx}/js/',
			paths : {
				jquery : 'jquery/jquery-1.7.1.min',
				JSON2 : 'bione/json2.min'
			},
			shim : {
				JSON2 : {
					exports : 'JSON2'
				}
			}
		});
		require(
				[ 'jquery', 'JSON2', '../plugin/js/template/viewMain' ],
				function() {
					$(function() {
						if (paramtmpId != "") {
							var is
							$.ajax({
								cache : false,
								async : true,
								url : "${ctx}/report/frame/param/templates/"
										+ paramtmpId + "?type=view",
								dataType : 'json',
								success : function(data) {
									try {
										$('#' + divName)
												.templateView(
														{
															data : JSON2.parse(data.paramJson)
														});
										var params = JSON2
												.parse(data.paramJson);
										for ( var i in params) {
											if (params[i].checkbox == "true"
													|| params[i].isMultiSelect == "true") {
												checkBoxId.push(params[i].name);
											}
										}

									} catch (e) {
									} finally {
										$('#temps-loading').css(
												'display', 'none');
									}
								}
							});
						}
					});
				});
	}
</script>
</head>
<body>
	<div id="template.left">
		<div id="lefttable" width="100%" border="0">
			<div width="100%"
				style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
				<div width="8%"
					style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
					<img src="${ctx }/images/classics/icons/application_side_tree.png" />
				</div>
				<div width="90%">
					<span
						style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
						数据模型 </span>
				</div>
			</div>
		</div>
		<div class="l-form" style="margin: 2px;">
			<ul>
				<li style="width: 40%;">数据源选择：</li>
				<li style="width: 59%;"><input id="dsId" /></li>
			</ul>
		</div>
		<div id="treeSearchbar"
			style="width: 99%; margin-top: 2px; padding-left: 2px;">
			<ul>
				<li style="width: 98%; text-align: left;">
					<div class="l-text-wrapper" style="width: 100%;">
						<div class="l-text l-text-date" style="width: 100%;">
							<input id="treeSearchInput" type="text" class="l-text-field"
								style="width: 100%;" />
							<div class="l-trigger">
								<div id="treeSearchIcon"
									style="width:100%;height:100%;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;"></div>
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="modeltree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
	<div id="template.right">
		<div id="model_tab" style="width: 100%; overflow: hidden;">
			<div id="configTab" tabid="configTab" title="配置">
				<div id="view">
					<div id="searchView">
						<div id="searchtable" width="100%" border="0">
							<div width="100%"
								style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
								<div
									style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
									<img src="${ctx}/images/classics/icons/chart_pie_edit.png" />
								</div>
								<div>
									<span
										style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
										查询条件 </span>
								</div>
								<div class="toggleBtn"></div>

							</div>
						</div>
						<div id="searchDiv" style="height: 160px;">
							<div style="height: 32px;">
								<span
									style="float: left; width: 60px; line-height: 32px; margin-left: 8px; height: 32px;">筛选条件：</span>
								<div id="paneldiv" style="margin-top: 1px; float: right;">
									<div id="panel" style="overflow-y: auto; width: 100%;"></div>
								</div>
							</div>
							<div
								style="margin-top: 10px; border-top: 1px solid #ddd; height: 128px; overflow-y: auto;">
								<div id='temps-loading' class='l-tab-loading'
									style="display: block; height: 128px; top: 68px;"></div>
								<form id="searchForm" style="width: 99%;"></form>
							</div>

						</div>
					</div>
					<div id="showGrid" oncopy="return false" oncut="return false">
						<div id="grid"></div>
					</div>
				</div>
			</div>
			<div id="sqlTab" tabid="sqlTab" title="高级查询" lselected="true">
				<div id="sqltable" width="100%" border="0">
					<div width="100%"
						style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
						<div
							style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
							<img id="sqlQuery" style="cursor: pointer"
								src="${ctx }/images/classics/icons/chart_pie_edit.png" />
						</div>
						<div id="sqlSpan">
							<span
								style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
								查询语句 </span>
						</div>
						<div id="paramSpan">
							<span
								style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
								参数模板：</span>
						</div>
						<div id="paramBox"
							style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
							<input id="paramtmpIdBox"/>
						</div>
						<div class="toggleBtn"></div>

					</div>
				</div>
				<div id="sqlDiv" style="height: 160px;">
					<div id="sqlEdit"
						style="background: #fff; height: 160px; overflow: hidden;">
						<textarea id="sql" style="width: 99%; height: 99%"></textarea>
					</div>
					<div id="paramEdit"
						style="background: #fff; height: 160px; overflow: hidden; width:99%;">
						<div id='temps-loading' class='l-tab-loading'
							style="display: hidden; height: 160px; top: 68px;"></div>
						<form id="temps"></form>
					</div>
				</div>
				<div id="sqlgrid" oncopy="return false" oncut="return false"></div>
			</div>
		</div>
	</div>
</body>
</html>