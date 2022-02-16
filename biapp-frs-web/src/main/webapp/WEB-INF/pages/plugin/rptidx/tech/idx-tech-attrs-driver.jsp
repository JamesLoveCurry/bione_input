<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style>
.noSelectText {
	-moz-user-select: none;
}

#left {
	border-width: 1px;
	border-style: solid;
	border-color: #D6D6D6;
}

#right {
	border-width: 0px;
	border-style: solid;
	border-color: #D6D6D6;
	background-color: #FCFCFC;
}

.panel-default {
	border: none;
}
</style>
<script type="text/javascript"
	src="${ctx}/plugin/js/idx/cursorPosition.js"></script>
<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<script type="text/javascript">
	var isDict = parent.isDict;
	var defSrc = parent.defSrc;
	var orgDimTypeNo = '${orgDimTypeNo}';
	var dateDimTypeNo = '${dateDimTypeNo}';
	var currencyDimTypeNo = '${currencyDimTypeNo}';
	var indexDimTypeNo = '${indexDimTypeNo}';
	var indexNo = '${indexNo}';
	var indexVerId = '${indexVerId}';
	var saveType = '${saveType}';
	var initType = '${initType}';
	var INDEX_DEF_SRC_LIB = '${INDEX_DEF_SRC_LIB}';
	var INDEX_DEF_SRC_ORG = '${INDEX_DEF_SRC_ORG}';
	var INDEX_DEF_SRC_USER = '${INDEX_DEF_SRC_USER}';
	var searchObj = {
		exeFunction : "initIdxTree",
		searchType : "idx"
	};//默认执行initTree方法
	var flag = true;
	var select;
	var idxTreeObj;
	var funcTreeObj;
	var symbolTreeObj;
	var dimNos = [];
	var idxMap = [];//已选指标数组，其中
	var customDimObj;//显示选择的指标下的维度
	var userInfoStr = "${userInfo}";
	var userInfo = (userInfoStr == null || userInfoStr == "") ? null : JSON2
			.parse(userInfoStr);
	var busiType = '${busiType}' == '' ? "00" : '${busiType}'

	function initTreeTab() {
		var htmlArr = [];
		htmlArr
				.push('<div title="全行指标库"  class="l-scroll"><ul id="idxTree" style="font-size: 12;width: 92%"  class="ztree"></ul></div>');
		if (userInfo != null) {
			htmlArr
					.push('<div title="机构自定义"  class="l-scroll"><ul id="tree2" style="font-size: 12;width: 92%"  class="ztree"></ul></div>');
			if (defSrc == INDEX_DEF_SRC_USER) {
				htmlArr
						.push('<div title="用户自定义"  class="l-scroll"><ul id="tree3" style="font-size: 12;width: 92%"  class="ztree"></ul></div>');
			}
		}
		$("#bottom_left").html(htmlArr.join(''));
		var height_ = $("#bottom_left").height();
		//面板
		$("#bottom_left").ligerAccordion({
			height : height_,
			speed : null
		});
		$(".l-accordion-header-inner").css("color", "#183152").css("font-size",
				"11");
	}

	function initAccording() {
		$("#accordion").ligerAccordion({
			height : $("#center").height() - 2
		});
	}

	function initLayout() {
		$("#left").height($("#center").height() - 10);
		$("#right").height($("#center").height() - 10);
		$("#right").width($("#center").width() - 320);
		$("#treeContainer").height($("#left").height() - 93);
		$("#lexpression").height($("#top_middle_middle").height());
		$("#rexpression").height($("#top_middle_middle").height());
	}

	function initPanel() {
		panel = $('#panel').exlabel({
			type : 'btn',
			text : 'text',
			value : 'id',
			isEdit : false,
			isInput : false,
			callback : {
				onClick : function(data, flag) {
					if (data.id == "DATE" || data.id == "ORG") {
						return false;
					}
				}
			}
		});
		$("#panel").height($("#top").height() - 34);
	}

	function initRefresh() {
		$("#title3")
				.append(
						"<div id='upLoadPic'  class='icon-resize-01 search-size' style='width:24px;float:left;z-index:999;position:relative;left:0px;top:5px;cursor:pointer;height:16px;'/>");
		$("#upLoadPic").live("click", function() {
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/idx/replaceNameAndNoByIdx",
				dataType : 'json',
				data : {
					expressionDesc : $("#expression").val()
				},
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					window.top.BIONE.loading = false;
					window.top.BIONE.hideLoading();
				},
				success : function(result) {
					if (result.message) {
						BIONE.tip(result.message);
						return;
					} else {
						var indexNoArray = ""
						if (result.indexNos) {
							indexNoArray = result.indexNos.split(",");
						}
						idxMap = [];

						for (var i = 0; i < indexNoArray.length; i++) {
							idxMap[indexNoArray[i]] = {
								indexNo : indexNoArray[i]
							};
						}
						initData();
						initCustomDim();
					}
				}
			});

		});
	}

	$(function() {
		initLayout();
		if (isDict) {
			$("#left").hide();
			$("#top").hide();
			$("#right").width("100%");
			$("#expression").attr("readonly", "readonly");
			$("#middle").height($("#right").height());
			$("#expression").height($("#right").height() * 0.90);
		}
		if (defSrc) {
			initTreeTab();
		}
		indxInfoObj = parent.indxInfoObj;
		initAccording();
		initIdxTree();
		initTreeSearch();//普通搜索
		initFuncTree();
		initSymbolTree();
		initPanel();
		initBtn();
		initRefresh();
		initForm();
		initBusiType();
		$("#highSearchIcon").click(
				function() {
					BIONE.commonOpenDialog("高级搜索", "highSearch", "600", "250",
							"${ctx}/report/frame/idx/highSearch?searchObj="
									+ JSON2.stringify(searchObj));
				});
	});

	function initForm() {
		if (indxInfoObj && indxInfoObj.indexNo && indxInfoObj.indexVerId
				&& $("#expression").val() == "") {
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/idx/getExtendIdxInfo?indexNo="
						+ indxInfoObj.indexNo + "&indexVerId="
						+ indxInfoObj.indexVerId,
				dataType : 'json',
				type : "GET",
				beforeSend : function() {
					window.top.BIONE.loading = true;
					window.top.BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					window.top.BIONE.loading = false;
					window.top.BIONE.hideLoading();
				},
				success : function(result) {
					//busiType = result.busiType;
					$("#expression").val(result.formulaDesc);
					var indexNoArray = result.srcIndexNo.split(",");
					for (var i = 0; i < indexNoArray.length; i++) {
						idxMap[indexNoArray[i]] = {
							indexNo : indexNoArray[i]
						};
					}
					if (result.dimNos && result.dimNos != "") {
						window.dimNos = result.dimNos.split(",");
					}
					initCustomDim();
					if (result.message != null && result.message != ""
							&& result.message.length > 0) {
						if (!isDict) {
							BIONE.tip(result.message);
						}
					}
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});

		}
	}
	function initMouseOver() {
		$("#idxTree li").bind("mouseover", function(event) {

			event.stopPropagation();
			var node = idxTreeObj.getNodeByTId(this.id);
			if (node.params.type == "idxInfo") {
				$("#tip").html("指标[" + node.text + "]");
				addTipPic();
			}
			if (node.params.type == "measureInfo") {
				$("#tip").html("度量[" + node.text + "]");
				addTipPic();
			}

		});

		$("#funcTree li").live("mouseover", function(event) {

			event.stopPropagation();
			var node = funcTreeObj.getNodeByTId(this.id);
			if (node.children == null) {
				$("#tip").html(node.params.display);
				addTipPic();
			}

		});
		$("#symbolTree li").live("mouseover", function(event) {

			event.stopPropagation();
			var node = symbolTreeObj.getNodeByTId(this.id);
			if (node.children == null) {
				$("#tip").html(node.params.display);
				addTipPic();
			}
		});
	}
	function initBtn() {
		var btns = [ {
			text : "取消",
			onclick : function() {
				window.parent.closeRptIdxInfoBox();
			}
		} ];
		if (!isDict) {
			btns.push({
				text : "保存",
				onclick : function() {
					f_save();
				}
			});
			if (indxInfoObj.indexVerId != "" && indxInfoObj.indexVerId != null) {
				btns.push({
					text : "发布为新版本",
					onclick : function() {
						f_save('newVer');
					}
				});
			}
		}
		// 		BIONE.addFormButtons(btns);
	}
	function initData() {
		var data = panel.getData();
		window.dimNos = [];
		for (var i = 0; i < data.length; i++) {
			if (data[i].flag) {
				window.dimNos.push(data[i].id);
			}
		}
	}
	function initCustomDim() {

		var indexNos = "";
		var indexVerIds = "";
		for ( var tmp in idxMap) {
			indexNos += idxMap[tmp].indexNo + ",";
			indexVerIds += (idxMap[tmp].indexVerId ? idxMap[tmp].indexVerId
					: "")
					+ ",";
		}

		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/idx/getDimByIdx",
			dataType : 'json',
			data : {
				"indexNos" : indexNos,
				"indexVerIds" : indexVerIds
			},
			type : "GET",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var data = result.data;
				panel.removeAll();
				panel.add({
					id : "DATE",
					text : "日期"
				});
				panel.add({
					id : "ORG",
					text : "机构"
				});
				for (var i = 0; i < data.length; i++) {
					if (data[i].id != "ORG" && data[i].id != "DATE"
							&& data[i].id != "INDEXNO") {
						panel.add({
							id : data[i].id,
							text : data[i].text
						});
					}
				}
				var dimNos = window.dimNos;
				panel.selectNodes("DATE,ORG", true, false);
				for (var i = 0; i < dimNos.length; i++) {
					if (dimNos[i] != "ORG" && dimNos[i] != "DATE"
							&& dimNos[i] != "INDEXNO") {
						panel.selectNodes(dimNos[i], true, false);
					}
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});

	}
	function initIdxTree(searchNm, searchObj) {
		if ((searchObj == undefined) || !searchNm) {
			var url_ = "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?busiType="
					+ busiType
					+ "&isShowIdx=1&isShowMeasure=1&showEmptyFolder=1&isAuth=1&t="
					+ new Date().getTime();
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
						name : "text",
						title : "title"
					}
				},
				view : {
					selectedMulti : false
				},
				callback : {
					onClick : idxTreeOnClick
				}
			};

			leftTreeObj = $.fn.zTree.init($("#idxTree"), setting, []);
		} else {
			var _url = "${ctx}/report/frame/idx/getSyncTree";
			var data = {
				'searchNm' : searchNm,
				'isShowIdx' : '1',
				'isShowDim' : '0',
				'isShowMeasure' : '1',
				'isPublish' : '1',
				"isAuth" : "0",
				"showEmptyFolder" : "",
				"busiType" : busiType
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
					"showEmptyFolder" : "",
					"busiType" : busiType
				};
			}
			setting = {
				data : {
					keep : {
						parent : true
					},
					key : {
						name : "text",
						title : "title"
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
					onClick : idxTreeOnClick
				}
			};
			leftTreeObj = $.fn.zTree.init($("#idxTree"), setting, []);
			BIONE
					.loadTree(
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
	}

	function initTreeSearch() {
		$("#treeSearchIcon").live('click', function() {// 树搜索
			initIdxTree($('#treeSearchInput').val(), "");
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				initIdxTree($('#treeSearchInput').val(), "");
			}
		});
	}

	function initFuncTree() {
		var setting = {
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
			callback : {
				onClick : zTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.params.display) {
						$("#" + treeNode.tId + "_a").attr("title",
								treeNode.params.display);
					}
				}
			}
		};
		funcTreeObj = $.fn.zTree.init($("#funcTree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/valid/logic/getFuncTree.json",
				funcTreeObj);
	}
	function zTreeOnClick(event, treeId, treeNode, clickFlag) {
		if (treeNode.children == null) {
			var position = cursorPosition.get(document
					.getElementById("expression"));
			cursorPosition.add(document.getElementById("expression"), position,
					treeNode.text);
			//var p = (treeNode.text).match("\\)");
			position.start = position.start + treeNode.text.length;
			position.end = position.end + treeNode.text.length;
			cursorPosition.set(document.getElementById("expression"), position);

		}
	}
	function initSymbolTree() {
		var setting = {
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
			callback : {
				onClick : symbolTreeOnClick,
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.params.display) {
						$("#" + treeNode.tId + "_a").attr("title",
								treeNode.params.display);
					}
				}
			}
		};
		symbolTreeObj = $.fn.zTree.init($("#symbolTree"), setting);
		//加载数据
		loadTree("${ctx}/report/frame/valid/logic/getSymbolTree.json",
				symbolTreeObj);
	}

	function symbolTreeOnClick(event, treeId, treeNode, clickFlag) {
		if (treeNode.children == null) {
			var position = cursorPosition.get(document
					.getElementById("expression"));
			cursorPosition.add(document.getElementById("expression"), position,
					treeNode.text);
		}
	}
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
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
				var nodes = component.getNodes();
				var num = nodes.length;
				for (var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					component.addNodes(null, result, false);
					component.expandAll(true);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	function idxTreeOnClick(event, treeId, treeNode, clickFlag) {
		if (treeNode.params.nodeType == "idxInfo") {
			if (treeNode.params.haveMeasure
					&& treeNode.params.haveMeasure == "true")
				return;

			idxMap[treeNode.params.indexNo] = {
				indexNo : treeNode.params.indexNo,
				indexVerId : treeNode.params.indexVerId
			};//已选指标
			var position = cursorPosition.get(document
					.getElementById("expression"));
			cursorPosition.add(document.getElementById("expression"), position,
					"I('" + treeNode.data.indexNm + "')");
			initData();
			initCustomDim();
		} else if (treeNode.params.nodeType == "measureInfo") {
			var position = cursorPosition.get(document
					.getElementById("expression"));
			var parentNode = treeNode.getParentNode();
			cursorPosition.add(document.getElementById("expression"), position,
					"I('" + parentNode.text + "." + treeNode.text + "')");
			idxMap[parentNode.params.indexNo] = {
				indexNo : parentNode.params.indexNo,
				indexVerId : parentNode.params.indexVerId
			};//已选指标

			var indexNos = "";
			var indexVerIds = "";
			for ( var tmp in idxMap) {
				indexNos += idxMap[tmp].indexNo + ",";
				indexVerIds += idxMap[tmp].indexVerId + ",";
			}
			initData();
			initCustomDim();
		}
	}
	function addTipPic() {
		var tipIcon = "${ctx}/images/classics/icons/lightbulb.png";
		$("#tip").prepend(
				"<div style='width:24px;float:left;height:16px;background:url("
						+ tipIcon + ") no-repeat' />");
	}

	function f_save(newVer) {
		if (newVer) {
			if (indxInfoObj.startDate <= indxInfoObj.oldStartDate) {
				BIONE.tip("新版启用日期必须大于旧版启用日期");
				return;
			}
		}
		if ($("#expression").val() == null || $("#expression").val() == "") {
			BIONE.tip("请输入指标公式！");
			return;
		}

		$
				.ajax({
					cache : false,
					async : true,
					url : "${ctx}/report/frame/idx/replaceNameAndNoByIdx",
					dataType : 'json',
					data : {
						expressionDesc : $("#expression").val()
					},
					type : "post",
					beforeSend : function() {
						window.top.BIONE.loading = true;
						window.top.BIONE.showLoading("正在校验公式中...");
					},
					complete : function() {
					},
					success : function(result) {
						if (result.message) {
							BIONE.tip(result.message);
							return;
						} else {
							var formula_content = result.expression;
							var formula_desc = result.expressionDesc;

							var indexNoArray = result.indexNos.split(",");

							if (result.indexNos == null
									|| result.indexNos == ""
									|| indexNoArray.length == 0) {
								BIONE.tip("至少选择一个指标！");
								return;
							}
							var dim = window.dimNos;
							var dimArr = dim.split(",");
							var busiDimCount = 0;
							for (var i = 0; i < dimArr.length; i++) {
								var dimObj = dimArr[i];
								if (dimObj != orgDimTypeNo
										&& dimObj != dateDimTypeNo
										&& dimObj != indexDimTypeNo) {
									busiDimCount++;
								}
							}
							if (busiDimCount > 10) {
								BIONE.tip("勾选的业务维度数量不能超过10个！");
								return false;
							}
							for (var i = 0; i < indexNoArray.length; i++) {
								if (idxMap[indexNoArray[i]] == null) {
									for ( var tmp in indexNoArray) {
										idxMap[indexNoArray[tmp]] = {
											indexNo : indexNoArray[tmp]
										};
									}
									initCustomDim();
									BIONE.tip("记录的指标和由公式过滤出的指标不一致，请重新选择维度！");
									return;
								}
							}
							var postData = {
								indexNm : indxInfoObj.indexNm,
								indexUsualNmTemp1 : indxInfoObj.indexUsualNmTemp1,
								indexUsualNmTemp2 : indxInfoObj.indexUsualNmTemp2,
								indexType : indxInfoObj.indexType,
								indexSts : indxInfoObj.indexSts,
								dataType : indxInfoObj.dataType,
								busiDef : indxInfoObj.busiDef,
								dataLen : indxInfoObj.dataLen,
								dataPrecision : indxInfoObj.dataPrecision,
								dataUnit : indxInfoObj.dataUnit,
								valRange : indxInfoObj.valRange,
								calcCycle : indxInfoObj.calcCycle,
								startDate : indxInfoObj.startDate,
								defDept : indxInfoObj.defDept,
								useDept : indxInfoObj.useDept,
								isSum : indxInfoObj.isSum,
								infoRights : indxInfoObj.infoRights,
								busiModel : indxInfoObj.busiModel,
								isPublish : indxInfoObj.isPublish,
								busiRule : indxInfoObj.busiRule,
								remark : indxInfoObj.remark,
								indexNo : indxInfoObj.indexNo,
								indexCatalogNo : indxInfoObj.indexCatalogNo,
								indexVerId : newVer == 'newVer' ? (parseInt(indxInfoObj.indexVerId) + 1)
										: indxInfoObj.indexVerId,
								formulaDesc : formula_desc,
								formulaContent : formula_content,
								srcIndexNo : result.indexNos,
								srcIndexMeasure : result.measureNos,
								dimTypes : dim,
								saveType : saveType,
								isNewVer : newVer == 'newVer' ? 'Y' : 'N'
							};
							if (defSrc) {
								postData.defSrc = defSrc;
							}
							$
									.ajax({
										type : "POST",
										url : "${ctx}/report/frame/idx/createRptIdxInfo",
										dataType : 'json',
										async : true,
										data : postData,
										complete : function() {
											window.top.BIONE.loading = false;
											window.top.BIONE.hideLoading();
										},
										success : function() {
											window.parent.parent.BIONE
													.tip("保存成功！");
											//刷新grid
											window.parent.parent.frames["rptIdxCenterTabFrame"].grid
													.loadData();
											var treeObj = window.parent.parent.currentTree;
											if (!treeObj) {
												treeObj = window.parent.parent.leftTreeObj;
											}
											var ifChangeDirectionaryCatalogNode = treeObj
													.getNodeByParam(
															"id",
															indxInfoObj.indexCatalogNo,
															null);
											if (ifChangeDirectionaryCatalogNode)
												treeObj
														.reAsyncChildNodes(
																ifChangeDirectionaryCatalogNode,
																"refresh");
											treeObj
													.reAsyncChildNodes(
															window.parent.parent.currentNode,
															"refresh");
											window.parent.closeRptIdxInfoBox();
										},
										error : function(XMLHttpRequest,
												textStatus, errorThrown) {
											BIONE
													.tip('保存失败,错误信息:'
															+ textStatus);
										}
									});
						}
					},
					error : function(result, b) {
						window.top.BIONE.loading = false;
						window.top.BIONE.hideLoading();
						BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
					}
				});

	}
	/* function getIndexInfo(treeNodes) {
		for ( var i = 0; i < treeNodes.length; i++) {
			if (treeNodes[i].params.type == "idxInfo") {
				idxInfoMap[treeNodes[i].text] = treeNodes[i].id;
				idxNoAndNameMap[treeNodes[i].id] = treeNodes[i].text;
			}else if (treeNodes[i].params.type == "measureInfo") {
				var parentNode = idxTreeObj.getNodeByParam("id", treeNodes[i].upId);
				idxInfoMap[parentNode.text + "." + treeNodes[i].text ] = "" + parentNode.id + "." +treeNodes[i].id + "";
				idxNoAndNameMap[parentNode.id + "." + treeNodes[i].id] = parentNode.text + "." +treeNodes[i].text;
			}
			if (treeNodes[i].children != null
					&& treeNodes[i].children.length > 0) {
				getIndexInfo(treeNodes[i].children);
			}
		}
	} */
</script>
<!--  -->
<script type="text/javascript">
	function getData() {
		initData();
		initCustomDim();
		if ($("#expression").val() == null || $("#expression").val() == "") {
			throw new Error('请输入指标公式！');
		}
		var result = null;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/idx/replaceNameAndNoByIdx",
			dataType : 'json',
			data : {
				expressionDesc : $("#expression").val()
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
			success : function(data) {
				result = data;
			},
			error : function() {
				throw new Error('加载数据失败！');
			}
		});
		if (JSON2.stringify(result) != "{}") {
			if (result.message) {
				throw new Error(result.message);
			}
			var formula_content = result.expression;
			var formula_desc = result.expressionDesc;
			var indexNoArray = [];
			if (result.indexNos) {
				indexNoArray = result.indexNos.split(",");
			}
			if (result.indexNos == null || result.indexNos == ""
					|| indexNoArray.length == 0) {
				throw new Error('至少选择一个指标！');
			}
			var dimArr = window.dimNos;
			dimArr.push("INDEXNO");
			var busiDimCount = 0;
			for (var i = 0; i < dimArr.length; i++) {
				var dimObj = dimArr[i];
				if (dimObj != orgDimTypeNo && dimObj != dateDimTypeNo
						&& dimObj != currencyDimTypeNo
						&& dimObj != indexDimTypeNo) {
					busiDimCount++;
				}
			}
			if (busiDimCount > 10) {
				throw new Error('勾选的业务维度数量不能超过10个！');
			}
			for (var i = 0; i < indexNoArray.length; i++) {
				if (idxMap[indexNoArray[i]] == null) {
					for ( var tmp in indexNoArray) {
						idxMap[indexNoArray[tmp]] = {
							indexNo : indexNoArray[tmp]
						};
					}
					throw new Error('记录的指标和由公式过滤出的指标不一致，请重新选择维度！');
				}
			}

		} else {
			throw new Error('数据无效！');
		}
		return {
			formulaDesc : formula_desc,
			formulaContent : formula_content,
			srcIndexNo : result.indexNos,
			srcIndexMeasure : result.measureNos,
			dimTypes : dimArr.join(";"),
			saveType : '03',
			isNewVer : 'N',
			busiType : busiType
		};
	}

	function initBusiType() {
		var readonly = false;
		/* if (indxInfoObj && indxInfoObj.indexNo && indxInfoObj.indexVerId
				&& $("#expression").val() == "") {
			readonly = true;
		} */
		$("#busiType").ligerComboBox({
			url : "${ctx}/rpt/frame/dataset/busiTypeList.json",
			onBeforeSelect : function(id, text) {
				if (id && id != busiType) {//下拉框值变化
					if($("#expression").val() != ""){//公式是否有值
						$.ligerDialog.confirm('切换业务类型后，已有的指标公式将会清空，您确认修改吗？',
					      function (yes) {
					         if (yes) {
					            $("#expression").val("");//将表达式的值设为空
					            busiType = id;
								$('#treeSearchInput').val("");
								searchObj = {
									exeFunction : "initIdxTree",
									searchType : "idx"
								};//默认执行initTree方法
								initIdxTree();
					         }else{
					        	 $.ligerui.get("busiType").selectValue(busiType);
					         }
						});
					}else{//没有值  直接加载指标树
						busiType = id;
						$('#treeSearchInput').val("");
						searchObj = {
							exeFunction : "initIdxTree",
							searchType : "idx"
						};//默认执行initTree方法
						initIdxTree();
					}
				}
			},
			readonly : readonly
		});
		$("#busiType").parent().width("99%");
		$.ligerui.get("busiType").selectValue(busiType);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="left" style="width: 300px; float: left;">
			<div id="accordion">
				<div title="指标">
					<div class="l-form" style="margin: 2px;">
						<ul>
							<li style="width: 40%;">业务类型选择：</li>
							<li style="width: 59%;"><input id="busiType" /></li>
						</ul>
					</div>
					<div id="treeSearchbar"
						style="width: 99%; margin-top: 2px; padding-left: 2px;">
						<ul>
							<li style="width: 98%; text-align: left;">
								<div class="l-text-wrapper" style="width: 100%;">
									<div class="l-text l-text-date" style="width: 100%;">
										<input id="treeSearchInput" type="text" class="l-text-field"
											style="width: 75%;" />
										<div class="l-trigger" style="right: 26px;">
											<a id="treeSearchIcon" class="icon-search search-size"></a>
										</div>
										<i class="l-trigger"
											style="right: 20px; width: 1px; height: 12px; border-left: 1px dotted gray; margin-top: 2px;"></i>
										<div title="高级筛选" class="l-trigger" style="right: 0px;">
											<a id="highSearchIcon" class="icon-light_off font-size"></a>
										</div>
									</div>
								</div>
							</li>
						</ul>
					</div>
					<div id="treeContainer"
						style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="idxTree" style="font-size: 12; width: 92%" class="ztree"></ul>
					</div>
				</div>
				<div title="运算符">
					<div id="treeContainer"
						style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="symbolTree" style="font-size: 12; width: 92%"
							class="ztree"></ul>

					</div>
				</div>
				<div title="计算公式">
					<div id="treeContainer"
						style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="funcTree" style="font-size: 12; width: 92%" class="ztree"></ul>
					</div>
				</div>
			</div>
		</div>
		<div id="right" style="float: left;">
			<div id="top"
				style="width: 90%; height: 45%; margin-left: auto; margin-right: auto; margin-top: 0%; overflow: auto">
				<div class="l-form-tabs">
					<ul original-title=""
						class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
						<li
							class="ui-state-default ui-corner-top ui-tabs-selected ui-state-hover"
							data-index="0"><a href="javascript:void(0)"> <span
								id='title3' style="color: #4bbdfb;">维度选择<span></a></li>
					</ul>
				</div>
				<div style="border: 1px solid #999999">
					<div id="panel" style="width: 99%; margin: 2px;"></div>
				</div>

			</div>
			<div id="middle"
				style="width: 90%; height: 45%; margin-left: auto; margin-right: auto; margin-top: 0%;">
				<div style="width: 10%; float: left;">
					<span>指标公式：</span>
				</div>
				<textarea id="expression"
					style="width: 100%; height: 98%; text-align: center; font-weght: bold; resize: none"></textarea>
				<input id="realExpression" type="hidden" />
			</div>

		</div>
	</div>
</body>
</html>