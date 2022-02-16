<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	// 全局变量
	var grid;
	var nodeType;
	var realId;
	var dialogWidth = 1000;
	var dialogHeight = 500;
	var selRowNo;
	var selCatalogId = parent.clickCatalogId;
	var selCatalogType = parent.clickCatalogType;
	var selCatalogNm = parent.selCatalogNm;
	var rptUpgradeMap = new Map();
	var busiTypeMap = new Map();
	var systemMap = new Map();
	var rptMgrList = "";
	var versionType = "old";
	
	// 通用“是”，“否”
	var COMMON_VALID = "Y", COMMON_INVALID = "N";

	var initExport = function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};

	//新增
	function rpt_add() {
		var modifyUrl = "${ctx}/report/frame/design/cfg/frsindex/edit/frame?selCatalogId="
				+ selCatalogId + "&selCatalogNm=" + encodeURI(encodeURI(selCatalogNm));
		top.BIONE.commonOpenDialog("报表维护", "rptEdit", $(top).width(), $(top)
				.height(), modifyUrl);
	}

	//修改
	function rpt_modify(type, row) {
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if (selRows.length > 1) {
			parent.BIONE.tip("请选择一张报表");
			return;
		}
		var selRow = grid.getSelectedRow();
		if (selRow != null) {
			selRowNo = selRow.__index;
			var modifyUrl = "${ctx}/report/frame/design/cfg/frsindex/edit/frame?datetime="
					+ new Date().getTime();
			/* if (type && typeof type != "undefined") {
				modifyUrl += ("&type=" + type);
			} */
			modifyUrl += ("&rptId=" + selRow.rptId + "&templateId="
					+ selRow.templateId + "&templateType="
					+ selRow.templateType + "&verId=" + selRow.verId);
			top.BIONE.commonOpenDialog("报表维护", "rptEdit", $(top).width(),
					$(top).height(), modifyUrl);
		} else {
			parent.BIONE.tip("请选择要修改的报表");
		}
	}

	//报表指标配置
	function rptIdx_config(type) {
		var isRptIdxCfg = "Y";
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if (selRows.length > 1) {
			parent.BIONE.tip("请选择一张报表");
			return;
		}
		var selRow = grid.getSelectedRow();
		if (selRow != null) {
			selRowNo = selRow.__index;
			var modifyUrl = "${ctx}/report/frame/design/cfg/frsindex/edit/frame?datetime="
					+ new Date().getTime();
			/* if (type && typeof type != "undefined") {
				modifyUrl += ("&type=" + type);
			} */
			modifyUrl += ("&rptId=" + selRow.rptId + "&templateId="
					+ selRow.templateId + "&templateType="
					+ selRow.templateType + "&verId=" + selRow.verId
					+ "&isRptIdxCfg=" + isRptIdxCfg);
			top.BIONE.commonOpenDialog("报表指标配置", "rptEdit", $(top).width(), $(
					top).height(), modifyUrl);
		} else {
			parent.BIONE.tip("请选择要配置的报表");
		}
	}

	/*将报表表样调整功能入口删掉
	//报表表样调整
	function rptTmp_config(type) {
		var isRptTmpCfg = "Y";
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if (selRows.length > 1) {
			parent.BIONE.tip("请选择一张报表");
			return;
		}
		var selRow = grid.getSelectedRow();
		if (selRow != null) {
			selRowNo = selRow.__index;
			var modifyUrl = "${ctx}/report/frame/design/cfg/frsindex/edit/frame?datetime="
					+ new Date().getTime();
			/!* if (type && typeof type != "undefined") {
				modifyUrl += ("&type=" + type);
			} *!/
			modifyUrl += ("&rptId=" + selRow.rptId + "&templateId="
					+ selRow.templateId + "&templateType="
					+ selRow.templateType + "&verId=" + selRow.verId
					+ "&isRptTmpCfg=" + isRptTmpCfg);
			top.BIONE.commonOpenDialog("报表表样配置", "rptEdit", $(top).width(), $(
					top).height(), modifyUrl);
		} else {
			parent.BIONE.tip("请选择要配置的报表");
		}
	}
	*/
	//删除
	function rpt_delete() {
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if (selRows != null && selRows.length && selRows.length > 0) {
			var rptIdxs = "";
			var verIds = "";
			for (var i = 0, l = selRows.length; i < l; i++) {
				var rowData = selRows[i];
				if (rowData.rptId == null || rowData.rptId == ""
						|| typeof rowData.rptId == "undefined") {
					continue;
				}
				if (rptIdxs != "") {
					rptIdxs += ",";
				}
				if (verIds != "") {
					verIds += ",";
				}
				rptIdxs += rowData.rptId;
				verIds += rowData.verId;
			}
			
			//校验报表是否已存在任务中，如果存在不能删除。 maojin2 20200320
			$.ajax({
				dataType : 'json',
				type : "post",
				url : "${ctx}/report/frame/design/cfg/getRptTaskByRptId",
				data : {
					rptIds : rptIdxs
				},
				beforeSend : function() {
					window.parent.BIONE.loading = true;
				},
				success : function(result) {
					window.parent.BIONE.loading = false;
					var msg = "报表";
					if(result != null && result.length > 0){
						for(var i=0; i<result.length; i++){
							msg = msg + "【" + result[i].RPTM + "】已存在任务【"+ result[i].TASKNM +"】中,";
						}
						msg = msg + "请先删除任务！"
						window.parent.BIONE.showSuccess(msg);
						return;
					}else{
						$.ajax({//判断报表的指标有没有被表间公式引用 maojin2 20200320
							dataType : 'json',
							type : "post",
							url : "${ctx}/report/frame/design/cfg/getSrcIdxByRptId",
							data : {
								rptIds : rptIdxs
							},
							beforeSend : function() {
								window.parent.BIONE.loading = true;
							},
							success : function(result) {
								window.parent.BIONE.loading = false;
								var msg = "报表";
								if(result != null && result.length > 0){
									for(var i=0; i<result.length; i++){
										msg = msg + "【" + result[i].RPTNM + "】的指标被【"+ result[i].RPTNM2 +"】引用,"
									}
									msg = msg + "请检查！"
									window.parent.BIONE.showSuccess(msg);
									return;
								}else{
									window.parent.$.ligerDialog .confirm('此操作会删除当前报表的全部版本，如果想删除某个版本，请使用版本管理功能，是否继续删除？',
										function(yes) {
										if (yes) {
											$ .ajax({
												cache : false,
												async : true,
												url : "${ctx}/report/frame/design/cfg/frsindex/delRpts",
												data : {
													rptIdxs : rptIdxs,
													verIds : verIds
												},
												dataType : 'json',
												type : "post",
												beforeSend : function() {
													window.parent.BIONE.loading = true;
													window.parent.BIONE
															.showLoading("正在删除，请稍后..");
												},
												success : function(result) {
													if (typeof window.parent.BIONE != 'undefined') {
														window.parent.BIONE.loading = false;
														window.parent.BIONE
																.hideLoading();
													}
													if (result && result.msg
															&& result.msg != "") {
														window.parent.BIONE
																.tip(result.msg);
													} else {
														window.parent.BIONE
																.tip("删除成功");
														// 刷新树
														window.parent
																.initTree();
														window.parent
																.searchHandler();
														// 若是目录视图，刷新grid；报表视图，关闭当前tab
														if (window.parent.clickNodeType == window.parent.rptNodeType) {
															window.parent
																	.setDefaultTab();
														} else {
															window.parent.document
																	.getElementById("gridFrame").contentWindow.grid
																	.reload();
														}
													}
												},
												error : function() {
													if (typeof window.parent.BIONE != 'undefined') {
														window.parent.BIONE.loading = false;
														window.parent.BIONE
																.hideLoading();
													}
													window.parent.BIONE
															.tip("删除失败，请联系系统管理员");
												}
											});
										}
									});
								}
							}
						});
					}
				}
			});
		} else {
			parent.BIONE.tip("请选择要删除的报表");
		}
	}

	// 同步
	function rpt_sync() {
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if (selRows != null && selRows.length && selRows.length == 1) {
			var mainTmpId = selRows[0].templateId;
			var verId = selRows[0].verId;
			window.parent.$.ligerDialog.confirm('您确定同步该报表么？', function(yes) {
				if (yes) {
					$.ajax({
						cache : false,
						async : true,
						url : "${ctx}/report/frame/design/cfg/syncMainTmp",
						data : {
							mainTmpId : mainTmpId,
							verId : verId
						},
						dataType : 'json',
						type : "post",
						beforeSend : function() {
							window.parent.BIONE.loading = true;
							window.parent.BIONE.showLoading("正在同步，请稍候..");
						},
						success : function(result) {
							if (typeof window.parent.BIONE != 'undefined') {
								window.parent.BIONE.loading = false;
								window.parent.BIONE.hideLoading();
							}
							if (result && result.msg && result.msg != "") {
								window.parent.BIONE.tip(result.msg);
							} else {
								window.parent.BIONE.tip("同步成功");
							}
						},
						error : function() {
							if (typeof window.parent.BIONE != 'undefined') {
								window.parent.BIONE.loading = false;
								window.parent.BIONE.hideLoading();
							}
							window.parent.BIONE.tip("同步失败，请联系系统管理员");
						}
					});
				}
			});
		} else {
			parent.BIONE.tip("请选择一张要同步的报表");
		}
	}

	// 版本查看
	function rpt_version() {
		if (!grid) {
			return;
		}
		var selRow = null;
		var selRows = grid.getSelectedRows();
		if (selRows != null && selRows.length && selRows.length > 0) {
			selRow = selRows[0];
		}
		if (selRow != null) {
			window.parent.BIONE.commonOpenDialog(
					("报表【" + selRow.rptNm + "】版本查看"), "rptVersion", $(
							window.parent).width() * 0.8, $(window.parent)
							.height() * 0.8,
					"${ctx}/report/frame/design/cfg/versionView?rptId="
							+ selRow.rptId);
		}
	}

	function rpt_export() {
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if (selRows != null && selRows.length >= 1) {
			var ids = [];
			for (var i = 0; i < selRows.length; i++) {
				ids.push(selRows[i].rptId);
			}
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/report/frame/design/cfg/exportDesignInfos",
				dataType : 'json',
				data : {
					rptId : ids.join(",")
				},
				type : "post",
				beforeSend : function() {
					window.parent.BIONE.loading = true;
					window.parent.BIONE.showLoading("正在生成导出数据，请稍等...");
				},
				complete : function() {
					window.parent.BIONE.loading = false;
					window.parent.BIONE.hideLoading();
				},
				success : function(result) {
					if (!result.fileName || result.fileName == "error") {
						parent.BIONE.tip("无可导出数据");
						return;
					}
					var src = '';
					src = "${ctx}/report/frame/design/cfg/download?filepath="
							+ result.fileName;//导出成功后的excell文件地址
					downdload.attr('src', src);//给下载文件显示框加上文件地址链接
				},
				error : function() {
					parent.BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		} else {
			parent.BIONE.tip("请选择一张报表进行修改");
		}
	}

	function rpt_import() {
		BIONE.commonOpenDialog("导入数据", "importDesignInfos", 600, 380,
				"${ctx}/report/frame/design/cfg/importDesignInfos");
	}
	/*//银监报表表样上传
	function import_template(){
		if(!grid){
			return ;
		}
		var selRow  = grid.getSelectedRows();
		if(selRow != null && selRow.length == 1){
			var verId = selRow[0].verId;
			var verNm;
			for (var i in systemMap) {
				if(selRow[0].busiType == i){
					if(systemMap[i]){
						verNm = systemMap[i][verId];
						break;
					}
				}
			}
			var rptId = selRow[0].rptId;
			var busiType = busiTypeMap.get(selRow[0].busiType);
			BIONE.commonOpenDialog('表样上传','importTemplate',600,320,'${ctx}/report/frame/design/cfg/cfgtemplateUpload?rptId='+rptId+"&busiType="+busiType+"&verId="+verId+"&verNm="+encodeURI(encodeURI(verNm)));
		}else if(selRow != null && selRow.length > 1){
			parent.BIONE.tip("请选择一个报表");
		}else{
			parent.BIONE.tip("请先选择报表");
		}
	}
	//银监报表表样 批量上传
	function import_templateBatch(){
		BIONE.commonOpenDialog('批量上传','importTemplate',600,350,'${ctx}/report/frame/design/cfg/cfgtemplateUpload');
	}
*/
	$(function() {
		initRptUpgradeType();
		initBusiType();
		initSystemMap();
		initExport();
		var parent = window.parent;
		// 初始化dialog高、宽
		dialogWidth = $(parent.window).width();
		dialogHeight = $(parent.window).height();
		//初始化
		ligerSearchForm();
		$("#nodeId").val(selCatalogId);
		$("#nodeType").val(selCatalogType);
		ligerGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");

	});

	//渲染查询表单
	function ligerSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "报表名称",
				name : "rptNm",
				newline : true,
				type : "text",
				attr : {
					field : "rptNm",
					op : "like"
				}
			}, {
				display : "业务类型",
				name : "busiType",
				newline : false,
				type : "select",
				options : {
					url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
					onBeforeSelect : function(selectBusiType) {
					 	getSystemVer(selectBusiType);
					}
				},
				attr : {
					field : "busiType",
					op : "="
				}
			}, {
				display : "制度版本",
				name : "verId",
				newline : true,
				type : "select",
				options : {
					data : null
				},
				attr : {
					field : "verId",
					op : "="
				}
			}, {
					display : "版本日期",
					name : "verDate",
					newline : false,
					type : "date",
					options : {
						format : "yyyyMMdd"
					},
					attr : {
						field : "verDate",
						op : "="
					}
				}
			]
		});
	}

	//渲染GRID
	function ligerGrid() {
		parent.rptGrid = grid = $("#maingrid").ligerGrid({
			width : "100%",
			height : "99%",
			columns : [ {
				display : "报表编号",
				name : "rptNum",
				width : "10%",
				align : 'center'
			}, {
				display : "报表名称",
				name : "rptNm",
				width : "25%",
				align : "left"
			}, {
				display : '业务类型',
				name : 'busiType',
				width : '15%',
				align : 'center',
				render : function(rowdata, index, value) {
					return busiTypeMap.get(value);
				}
			}, {
				display : '制度版本',
				name : 'verId',
				width : '10%',
				align : 'center',
				render : function(rowdata, index, value) {
					for (var i in systemMap) {
						if(rowdata.busiType == i){
							if(systemMap[i]){
								return systemMap[i][value];
							}
						}
					}
					return "";
				}
			}, {
				display : "最后修改时间",
				name : "createTime",
				width : "15%",
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss'
			}, {
				display : "升级概况",
				name : "templateNm",
				width : "10%",
				render : function(data, row, context, it) {
					return rptUpgradeMap.get(context);
				}
			}, {
				display : "报表类型",
				name : "templateType",
				width : "10%",
				render : function(data, row, context, it) {
					if ("01" == context) {
						return "明细报表";
					} else if ("02" == context) {
						return "单元格报表";
					} else if ("03" == context) {
						return "综合报表"
					} else {
						return "未知";
					}
				}
			}, {
				display : "开始时间",
				name : "verStartDate",
				width : "10%",
				align : 'center'
			}, {
				display : "结束时间",
				name : "verEndDate",
				width : "10%",
				align : 'center'
			} ],
			rownumbers : true,
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			isScroll : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			method : 'post',
			url : "${ctx}/report/frame/design/cfg/getFrsRptList.json",
			parms : {
				nodeId : selCatalogId,
				nodeType : selCatalogType
			},
			toolbar : {}
		});
	}

	//初始化按钮
	function initButtons() {
		var btns = [{
			text : "报表管理",
			icon : "fa-pencil-square-o",
			operNo : "rpt_modify",
			menu : {
				items : [ {
					text : "添加",
					icon : "add",
					click : rpt_add,
					operNo : "rpt_add"
				}, {
					icon : 'modify',
					text : '修改',
					click : rpt_modify
				}, {
					text : "删除",
					icon : "delete",
					click : rpt_delete,
					operNo : "rpt_delete"
				}, {
					text : "复制",
					icon : "camera",
					click : rpt_copy,
					oper : "rpt_copy"
				}/* , {
					icon : 'details',
					text : '指标配置',
					click : rptIdx_config
				} *//*, {
					icon : 'config',
					text : '表样调整',
					click : rptTmp_config
				}*/, {
					icon : 'config',
					text : '表间取数调整',
					click : formulaDesc_sync
				}]
			}
		}, {
			text : "版本管理",
			icon : "fa-files-o",
			operNo : "rpt_version",
			menu : {
				items : [{
					text : "版本查看",
					icon : "database",
					click : rpt_version,
					operNo : "rpt_version"
				}, {
					text : "创建新版本",
					icon : "add_2",
					click : rpt_version_new,
					operNo : "rpt_version_new"
				}]
			}
		}, {
			text : "制度升级3.2",
			icon : "fa-pencil-square-o",
			operNo : "system_package",
			menu : {
				items : [ {
					icon : 'import',
					text : '一键导入',
					click : version_import
				}, {
					icon : 'export',
					text : '一键导出',
					click : version_export
				} ]
			}
		}, {
			text : "制度升级3.1",
			icon : "fa-pencil-square-o",
			operNo : "excel_modify",
			menu : {
				items : [ {
					icon : 'import',
					text : '报表导入',
					click : excel_import
				}, {
					icon : 'export',
					text : '报表导出',
					click : excel_export
				} ]
			}
		}
		,{
			text : "报送人员配置",
			icon : "fa-pencil-square-o",
			poerNo : "user_config",
			menu : {
				items : [ {
					text : "编辑",
					icon : "modify",
					click : editUser,
					operNo : "edit_user"
				}, {
					text : '查看',
					icon : 'view',
					click : viewUser
				} ]
			}
		},{
			text : "单元格属性调整",
			icon : "fa-pencil-square-o",
			poerNo : "cell_config",
			click : updateCellConfig
		},{
			text : "一键导出",
			icon : "fa-download",
			click : exportRpts
		}];

		var selectedNodes = window.parent.treeObj.getSelectedNodes();
		if (selectedNodes != null
				&& selectedNodes.length > 0
				&& selectedNodes[0].params.nodeType != window.parent.folderNodeType) {
			// 只有选择的是目录节点，才能进行报表新增
			btns[0].menu.items.splice(0,1);
			if (selectedNodes[0].params.nodeType == window.parent.rootNodeType) {
				/* btns.push({
					text : "制度升级",
					icon : "fa-pencil-square-o",
					operNo : "system_package",
					menu : {
						items : [ {
							icon : 'import',
							text : '一键导入',
							click : system_import
						}, {
							icon : 'export',
							text : '一键导出',
							click : system_export
						} ]
					}
				}); */
			}
		}
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	
	// 一键导出制度
	var exportRpts = function(){
		BIONE.commonOpenDialog("导出报表","exportFilter", 500, 380,"${ctx}/report/frame/design/cfg/exportFilterByParam");
	};

	// 复制为新报表
	function rpt_copy() {
		if (!grid) {
			return;
		}
		var selRow = grid.getSelectedRows();
		if (selRow != null && selRow.length == 1) {
			selRow = grid.getSelectedRow();
			var modifyUrl = "${ctx}/report/frame/design/cfg/baseEdit";
			modifyUrl += ("?defSrc=01&catalogId=" + selRow.catalogId
					+ "&rptId=" + selRow.rptId + "&templateId="
					+ selRow.templateId + "&templateType="
					+ selRow.templateType + "&verId=" + selRow.verId
					+ "&copyRptId=" + selRow.rptId);
			window.parent.BIONE.commonOpenDialog("【复制报表】", "baseEdit", 800,
					610, modifyUrl);
		} else {
			parent.BIONE.tip("请选择一条报表记录进行复制！");
		}
	}

	function system_export() {
		versionType = "old";
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if(selRows && (selRows.length > 0)){
			if (selRows.length > 1) {
				var verId = $.ligerui.get("verId").getValue();
				if(!verId){
					parent.BIONE.tip("请先选择业务类型和制度版本");
					return;
				}
			}
			var ids = [];
			for (var i = 0; i < selRows.length; i++) {
				ids.push(selRows[i].rptId);
			}
			rptMgrList = ids.join(",");
			BIONE.commonOpenDialog("导出数据", "exportSystem", 400, 280,
					"${ctx}/report/frame/design/cfg/exportSystem");
		} else {
			parent.BIONE.tip("请至少选择一张报表进行导出");
		}
	}

	//制度包导出功能
	function version_export() {
		versionType = "new";
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if(selRows && (selRows.length > 0)){
			/* if (selRows.length > 1) {
				var verId = $.ligerui.get("verId").getValue();
				if(!verId){
					parent.BIONE.tip("请先选择业务类型和制度版本");
					return;
				}
			} */
			var ids = [];
			for (var i = 0; i < selRows.length; i++) {
				ids.push(selRows[i].rptId);
			}
			rptMgrList = ids.join(",");
			BIONE.commonOpenDialog("导出数据", "exportSystem", 400, 280,"${ctx}/report/frame/design/cfg/exportSystem");
		} else {
			parent.BIONE.tip("请至少选择一张报表进行导出");
		}
	}

	function system_import() {
		BIONE.commonOpenDialog("导入数据", "importDesignInfos", 600, 340,
				"${ctx}/report/frame/design/cfg/importDesignInfos?type=old");
	}

	//制度包导入（新版）
	function version_import() {
		BIONE.commonOpenDialog("导入数据", "importDesignInfos", 600, 340, "${ctx}/report/frame/design/cfg/importDesignInfos?type=new");
	}

	function start_export(isEmptyIdx, rptIds) {
		if(!rptIds){
			rptIds = rptMgrList;
		}
		var selRows = grid.getSelectedRows();
		verId = selRows[0].verId;
		if(selRows){
			for(var i=0; i<selRows.length;i++){
				if(verId != selRows[i].verId){
					verId = "";
				}
			}
			if(verId != ""){
				for (var i in systemMap) {
					if(selRows[0].busiType == i){
						if(systemMap[i]){
							verNm = systemMap[i][verId];
							break;
						}
					}
				}
			}else{
				verNm = "未知制度";
			}
		}
		//新增逻辑判断：使用新制度包导出或旧制度包导出，默认使用旧制度包导出
		var url = "${ctx}/report/frame/design/cfg/exportDesignInfos";
		if(versionType == "new"){
			url = "${ctx}/report/frame/design/cfg/exportDesignInfosNew";
		}
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : {
				isEmptyIdx : isEmptyIdx,
				rptId : rptIds,
				verId : verId,
				verNm : verNm
			},
			type : "post",
			beforeSend : function() {
				window.parent.BIONE.loading = true;
				window.parent.BIONE.showLoading("正在生成导出数据，请稍等...");
			},
			complete : function() {
				window.parent.BIONE.loading = false;
				window.parent.BIONE.hideLoading();
			},
			success : function(result) {
				if (!result.fileName || result.fileName == "error") {
					parent.BIONE.tip("无可导出数据");
					return;
				}
				var src = '';
				src = "${ctx}/report/frame/design/cfg/download?filepath="
						+ result.fileName;//导出成功后的excell文件地址
				downdload.attr('src', src);//给下载文件显示框加上文件地址链接
			},
			error : function() {
				parent.BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	}
	

	function exportDesignInfos(isEmptyIdx, rptIds, verId, verNm){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/exportDesignInfosNew",
			dataType : 'json',
			data : {
				isEmptyIdx : isEmptyIdx,
				rptId : rptIds.join(","),
				verId : verId,
				verNm : verNm == "" ? "最新版本" : verNm//用于压缩包的名称的拼接
			},
			type : "post",
			beforeSend : function() {
				window.parent.BIONE.loading = true;
				window.parent.BIONE.showLoading("正在生成导出数据，请稍等...");
			},
			complete : function() {
				window.parent.BIONE.loading = false;
				window.parent.BIONE.hideLoading();
			},
			success : function(result) {
				if (!result.fileName || result.fileName == "error") {
					parent.BIONE.tip("无可导出数据");
					return;
				}
				var src = '';
				src = "${ctx}/report/frame/design/cfg/download?filepath="
						+ result.fileName;//导出成功后的excell文件地址
				downdload.attr('src', src);//给下载文件显示框加上文件地址链接
			},
			error : function() {
				parent.BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	}

	function rpt_version_new() {
		BIONE.commonOpenDialog("创建新版本", "establishVer", 500, 350,
				"${ctx}/report/frame/design/cfg/establishVer");
	}

	function getSystemVer(busiType) {
		if (busiType) {
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getSystemList",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result) {
					if (result) {
						$.ligerui.get("verId").setData(result);
					}
				},
				error : function() {
					parent.BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}

	//加载升级概况
	function initRptUpgradeType() {
		$.ajax({
			async : false,
			type : "POST",
			url : "${ctx}/report/frame/design/cfg/getRptUpgradeList",
			dataType : 'json',
			success : function(data) {
				if (data) {
					for (var i = 0; i < data.length; i++) {
						rptUpgradeMap.set(data[i].id, data[i].text);
					}
				}
			},
			error : function() {
				top.BIONE.tip('加载失败');
			}
		});
	}
	
	//加载业务类型
	function initBusiType(){
		$.ajax({
			async :false,
			type : "POST",
			url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
			dataType : 'json',
			success: function(data) {
				if(data){
					for(var i = 0; i < data.length ; i++){
						busiTypeMap.set(data[i].id, data[i].text);
					}
				}
			},
			error: function() {
				top.BIONE.tip('加载失败');
			}
		});
	}
	
	//加载业务类型与制度版本类型
	function initSystemMap(){
		$.ajax({
			async :false,
			type : "POST",
			url : "${ctx}/frs/system/cfg/getSystemMap",
			dataType : 'json',
			success: function(data) {
				if(data){
					systemMap = data;
				}
			},
			error: function() {
				top.BIONE.tip('加载失败');
			}
		});
	}
	
	//excel格式的报表导出
	function excel_export() {
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if(selRows && (selRows.length > 0)){
			if (selRows.length > 1) {
				var verId = $.ligerui.get("verId").getValue();
				if(!verId){
					parent.BIONE.tip("请先选择业务类型和制度版本");
					return;
				}
			}
			var ids = [];
			for (var i = 0; i < selRows.length; i++) {
				ids.push(selRows[i].rptId);
			}
			start_export("N", ids.join(","));
		} else {
			parent.BIONE.tip("请至少选择一张报表进行导出");
		}
	}

	//excel格式的报表导入
	function excel_import() {
		BIONE.commonOpenDialog("导入数据", "importDesignInfos", 600, 340,
				"${ctx}/report/frame/design/cfg/importDesignInfos");
	}
	/*//表样管理
	function rpttemplateMgr() {
		var selRows = grid.getSelectedRows();
		if(selRows.length == 1){
			var rptId = selRows[0].rptId;
			var versionId = selRows[0].verId;
			BIONE.commonOpenDialog("表样列表", "rpttemplateMgr", $(document).width()-150, $(document).height()-80,"${ctx}/report/frame/design/cfg/rpttemplateMgr?rptId=" + rptId +"&versionId=" + versionId);
		}else{
			BIONE.commonOpenDialog("表样列表", "rpttemplateMgr", $(document).width()-150, $(document).height()-80,"${ctx}/report/frame/design/cfg/rpttemplateMgr");
		}
	}*/
	
	//表间取数调整
	function formulaDesc_sync(){
		var selRows = grid.getSelectedRows();
		if(selRows && (selRows.length == 1)){
			$.ajax({
				async :false,
				type : "POST",
				url : "${ctx}/report/frame/design/cfg/syncExpressionDesc",
				dataType : 'json',
				data : {
					rptTmpId : selRows[0].templateId,
					verId : selRows[0].verId
				},
				success: function(data) {
					if(data && (data.isSuccess == "Yes")){
						top.BIONE.tip('调整成功');
					}else{
						top.BIONE.tip(data.tip);
					}
				},
				error: function() {
					top.BIONE.tip('调整失败');
				}
			});
		} else {
			top.BIONE.tip("请选择一张报表进行调整");
		}
	}
	
	//报送人员配置
	function editUser() {
		var editUserUrl = "${ctx}/report/frame/design/cfg/editUser";
		BIONE.commonOpenDialog("编辑报送人员", "baseEdit", $("#center").width()-100,
				$("#center").height()-50, editUserUrl);
	}
	
	//单元格属性调整
	function updateCellConfig() {
		var selRows = grid.getSelectedRows();
		if(selRows != null && selRows.length > 0){
			var templateIds = [];
			var verIds = [];
			for(var i=0; i<selRows.length; i++){
				templateIds.push(selRows[i].templateId);
				verIds.push(selRows[i].verId);
			}
			var url = "${ctx}/report/frame/design/cfg/updateCellConfig?templateIds="+templateIds.join(",")+"&verIds="+verIds.join(",");
			BIONE.commonOpenDialog("单元格属性调整", "baseEdit", $("#center").width()-100, $("#center").height()-50, url);
		}else{
			BIONE.tip("请至少选择一张报表！");
		}
	}
	
	//报送人员查看
	function viewUser() {
		var selRows = grid.getSelectedRows();
		if(selRows && (selRows.length == 1)){
			var viewUserUrl = "${ctx}/report/frame/design/cfg/showUser?rptNum="+selRows[0].rptNum;
			BIONE.commonOpenDialog("查看报送人员", "baseView", $("#center").width()-100,
					$("#center").height()-50, viewUserUrl);
		}else{
			BIONE.tip("只能选择一条记录！");
		}
	}
</script>
</head>
<body>
</body>
</html>