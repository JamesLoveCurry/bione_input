<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template23A.jsp">
<head>
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/contextMenu/jquery.contextMenu.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/daterangepicker/daterangepicker.css" />
<%@ include file="/common/codemirror_load.jsp"%>
<script type="text/javascript" src="${ctx}/js/contextMenu/jquery.contextMenu.min.js"></script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript" src="${ctx}/js/datashow/ParamComp.js"></script>
<script type="text/javascript" src="${ctx}/js/require/require.js"></script>
<script type="text/javascript" src="${ctx}/js/ligerUI/ligerui.all.js"></script>

<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<style>
.toggleBtn {
	padding-right: 10px;
	cursor: pointer;
	float: right;
	position: relative;
	margin-top: 8px;
	width: 9px;
	height: 10px;
}
#temps-loading {
	background-image:
		url("${ctx}/css/classics/ligerUI/Gray/images/ui/loading2.gif") #fcfcfc;
}

</style>
<script type="text/javascript">
// 	var paramId = "";
// 	var sqlModel = "sql";
// 	var cursql = "";
// 	var curdsId = "";
// 	var grid = null;
// 	var sqlgrid = null;
// 	var modelFlag = false; //灵活查询TAB加载标识
// 	var storeFlag = false;
// 	var sqlFlag = false; //配置页面TAB加载标识
// 	var configFlag = false; //SQL页面TAB加载标识
// 	var setId = "";
// 	var srcCode = "";
// 	var sqlEdit = null;
    
// 	var detailTreeObj = null;
// 	var storeTreeObj = null;
// 	var leftTab = null;
// 	var modelTab = null;
// 	var tmpTab = null;

    
// 	var colInfos = {};

// 	var sqlcolumns = [];
// 	var filterInfo = {};
	
// 	var leftTable = null;

// 	var downdload = null;
// 	var susePager = true;
// 	var pageSize = 2000;
// 	var moduleMap = {}; 
// 	var colflag = true;
// 	var paramCombo;
    //排序字段
    var colSorts = [];
    //合计字段
    var colSums = [];
    //分页大小
	var pageSize = 5000;
    //分页开关
	var usePager = true;
    //数据源ID
    var dsId = "";
    // 标签对象   
    var panel = null; 
    //ztree对象
	var ztree =  $.fn.zTree;
    //左侧树对象
    var moduleTreeObj = null;
    //拖拽树节点
	var draggingNode = null;
	//拖动节点图标
    var notAllowedIcon = "/images/classics/icons/cancel.gif";
    //拖动节点到某个区域时图标
    var allowedIcon = "/images/classics/icons/accept.png";
    //列表对象
	var grid = null;
	//grid列模型数组
    var columns = [];
    //grid列模型对象
    var colInfos = {};
    //拖拽参数
	var params = [];
	//是否增加grid列
    var addColFlag = false;

    var draggingParentNodes = [];

    var treeChilds = [];
    
    function reflash(){
	    panel.removeAll();
	    params=[];
	    createParams("searchForm", params);
	    window.colInfos = {};
	    window.columns = [];
	    setId = "";
	    grid.set("columns", []);
	    grid.reRender();
	    moduleTreeObj.reAsyncChildNodes(moduleTreeObj.getNodeByParam("id", "0", null), "refresh");
	}
	$(function() {
		$("#leftSpan").html("补录任务查询");

		//初始化数据源选择
// 		initDsId();
		//初始化树
		initModelTree();
		//初始右侧
		initModelTab();
		//初始化下载
		initDownLoad();
		
// 		moduleTreeObj.reAsyncChildNodes(moduleTreeObj.getNodeByParam("id", "0", null), "refresh");
		
	});

	//初始化下载模块
	function initDownLoad(){
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}
	
	function initModelTab() {
		initPanel();
		initSearch();
		initModelGrid();
// 		modelTab.selectTabItem('configTab');
		createParams("searchForm", []);
	}

	function resizeLay(){
		$("#paneldiv").css("width", $("#right").width() - 90);
	}
	
	function initPanel() {
		$("#panel").css("height", "32px");
		$("#paneldiv").css("width", $("#right").width() - 90);
		panel = $('#panel').exlabel({
			text : 'text',
			value : 'id',
			isEdit : false,
			isInput : false,
			callback : {
				remove : function(item) {
					deleteParam(item.id);
				},
				onClick : function(item) {
					BIONE.commonOpenDialog("查询配置", "searchEdit", 600, 350, 
							"${ctx}/report/input/tasksearch/supplement/paramconfig?uuid=" + item.id);
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
			var vals = getParams();
			createParams("searchForm", params,vals);
			
		}
	}

	function initDsId() {
		$("#dsId").ligerComboBox({
			url : "${ctx}/report/input/tasksearch/supplement/dsList.json",
			onSelected : function(id, text) {
				if (id != null && id != "") {
					dsId = id;
//                  if (moduleTreeObj != null)
//                  BIONE.loadTree(
//                          "${ctx}/report/input/tasksearch/supplement/getRowModuleTree?dsId="
//                                  + dsId, moduleTreeObj, null,
//                          null,false);
				}
			}
		});
		$("#dsId").parent().width("99%");
		$.ligerui.get("dsId").selectValue("1");
	}

	function initSearch() {
		$("#searchDiv").show();
		$(".toggleBtn").bind("click",function() {
			 if ($(this).hasClass("icon-down")) {
					$(this).removeClass("icon-down");
					$(this).addClass("icon-up");
				$("#searchDiv").slideToggle('fast',function() {
					if (grid) {
						grid.setHeight($(document).height()- $("#searchDiv").height() - 60);
					}
					
				});
			} else {
				$(this).removeClass("icon-up");
				$(this).addClass("icon-down");
				$("#searchDiv").slideToggle('fast',function() {
					if (grid) {
						grid.setHeight($(document).height()- 60);
					}
				});
			}
		});


		$("#treeSearchIcon").live(
				'click',function(){
			reflash();
			searchNode($("#treeSearchInput").val());
		});
		
		$("#treeSearchInput").live('keydown',function(){
			if (event.keyCode == 13) {
				reflash();
				searchNode($("#treeSearchInput").val());
			}
		});
	}

	function searchNode(treeNodeNm){
		moduleTreeObj.reAsyncChildNodes(moduleTreeObj.getNodeByParam("id", "0", null), "refresh");
	}
	

	function createParams(divName, params,vals) {
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
		require([ 'jquery', 'JSON2', 'template/viewMain' ], function() {
			checkBoxId = [];
			$('#' + divName).templateView({
				data : pJson,
				loaded : function(){
					if(vals){
						setView(vals);
					}
				}
			});
			for ( var i in pJson) {
				if (pJson[i].checkbox == "true" || pJson[i].isMultiSelect == "true") {
					checkBoxId.push(pJson[i].name);
				}
			}
			
			$.metadata.setType("attr", "validate");
			$('#temps-loading').css('display', 'none');
		});
	}

	function setView(vals){
		for(var i = 0; i < vals.length; i++){
			if(vals[i].type == "01"){
				$.ligerui.get(params[i].paramId).selectValue(vals[i].op);
				$("#"+params[i].paramId).prev().find("input").val(vals[i].val);
			}
			if(vals[i].type == "02"){
				$.ligerui.get(params[i].paramId).selectValue(vals[i].op);
				$("#"+params[i].paramId).prev().find("input").val(vals[i].val);
			}
			if(vals[i].type == "03"){
				$.ligerui.get(params[i].paramId).setValue(vals[i].val);
				$.ligerui.get(params[i].paramId).setText(vals[i].text);
			}
			if(vals[i].type == "04"){
				$.ligerui.get(params[i].paramId).setValue(vals[i].val);
				$.ligerui.get(params[i].paramId).setText(vals[i].text);
			}
			if(vals[i].type == "05"){
				$.ligerui.get(params[i].paramId).setValue(vals[i].val);
			}
			if(vals[i].type == "06"){
				$("#"+params[i].paramId).val(vals[i].val);
			}
			if(vals[i].type == "07"){
				$.ligerui.get(params[i].paramId).setValue(vals[i].val);
				$.ligerui.get(params[i].paramId).setText(vals[i].text);
			}
			if(vals[i].type == "08"){
				$($("#"+params[i].paramId).parent().parent().find("input")[0]).val(vals[i].start),
				$($("#"+params[i].paramId).parent().parent().find("input")[1]).val(vals[i].end)
			}
			if(vals[i].type == "09"){
				$.ligerui.get(params[i].paramId).selectValue(vals[i].val);
			}
		}
	}
	var treeIdx = true;
	function initModelTree() {
		var leftHeight = $("#left").height();
		var treeContainer = $("#treeContainer");
		treeContainer.height(leftHeight - $("#treeSearchbar").height());
		var setting = {
			async : {
				enable : true,
				url : '${ctx}/report/input/tasksearch/supplement/getRowModuleTree?d=' + new Date().getTime(),
				dataType : "json",
				type : "post",
				autoParam : [ "id" , "data",'params'],
				otherParam : {
					'searchNm':function(){
						//获取搜索框内容
						return $('#treeSearchInput').val();
					}
				}
			},
			data : {
				keep : {
					parent : false
				},
				key : {
					name : "text"
				},
				simpleData : {
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
				    if(treeNode.id == '0'){
					    if(treeIdx){
					    	treeIdx = false;
					    	moduleTreeObj.reAsyncChildNodes(moduleTreeObj.getNodeByParam("id", "0", null), "refresh");
					    }
		        		moduleTreeObj.expandNode(moduleTreeObj.getNodeByParam("id", "0", null),true,false,false);
				    }
// 			        
			        $("#"+treeNode.tId + "_a").addClass("tree_span");
			        var type = treeNode.params.type;
			        if(type == 'table'){
			            setDragDrop("#" + treeNode.tId + "_span", "#grid");
			        }else if(type == 'col'){
			            setDragDrop("#" + treeNode.tId + "_span", "#grid , #searchView");
			        }
			    },
			    onExpand : function(treeId,treeNode,treeNode){
				    if(addColFlag){
				        var addnodes = moduleTreeObj.getNodesByParam("upId",treeNode.id, null);
				        addColumns(addnodes);
				        addColFlag = false;
				    }
		        }

		        
		    }
		};
		moduleTreeObj = ztree.init($("#modeltree"), setting);
	}

	function moduleTreeHiddenFilter(node) {
	    var nodeType = node.params.type;
		if(nodeType == 'col'){
	        return 	false;
		}else if(nodeType != 'root'){
	        return 	draggingParentNodes.indexOf(node.tId) == -1;
		}
// 	    if(nodeType == 'table'){
// 	        return 	node.id != draggingNode.id && node.upId == 0;
// 	    }else if(nodeType == 'col'){
// 	        return 	node.id != draggingNode.upId && node.upId == 0;
// 	    }else if(nodeType == 'catalog'){
// 	        return 	draggingParentNode.indexOf(node.tId) == -1;
// 	    }
	    return false;
	}

	
    function setDragDrop(dom,receiveDom){
        if (typeof dom == "undefined" || dom == null) {
            return;
        }
        if (typeof $(dom).ligerDrag != "function")
            return;

        //给dom绑定拖动事件
        $(dom).ligerDrag({
            //返回动画
            revert: false, 
            //可以拖拽到的dom
            receive: receiveDom,
            //动态更改指针样式
            proxy : function(target, g, e) {
                var defaultName = "";
                var proxyLabel = "${ctx}" + notAllowedIcon;
                var targetTitle = $(dom).parent().attr("title") == null ? defaultName : defaultName + ":" + $(dom).parent().attr("title");
                var proxyHtml = $('<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;"><span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('
                        + proxyLabel
                        + ')" ></span><span style="padding-left: 14px;">'
                        + targetTitle + '</span></div>');
                var div = $(proxyHtml);
                div.appendTo('body');
                //返回更改指针的样式
                return div;
            },
            //开始拖动
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
                    draggingNode = moduleTreeObj.getNodeByTId(treeId);
                }
            },
            //拖动到某dom时
            onDragOver : function(receive, source, e) {
                var allowLabel = "${ctx}" + allowedIcon;
                source.children(".dragimage_span").css("background","url('" + allowLabel + "')");
            },
            //离开dom时
            onDragLeave : function(receive, source, e) {
                var notAllowLabel = "${ctx}" + notAllowedIcon;
                source.children(".dragimage_span").css("background","url('" + notAllowLabel + "')");
            },
            //拖动事件
            onDrop : function(obj, target, e) {
            	draggingParentNodes = [];
            	getDraggingParentNode(draggingNode);
                var nodes = moduleTreeObj.getNodesByFilter(moduleTreeHiddenFilter);
                for (var i=0, l=nodes.length; i < l; i++) {
                    moduleTreeObj.removeNode(nodes[i]);
                }
                
                if ($(obj).attr("id") == "grid") {
                    if (draggingNode.params.type == "table") {
                        if(draggingNode.children && draggingNode.children.length > 0){
                            var addnodes = moduleTreeObj.getNodesByParam("upId",draggingNode.id, null);
                            addColumns(addnodes);
                         }else{
                             addColFlag = true;
                             moduleTreeObj.expandNode(draggingNode, true, false, true,true);
                         }
                    } else {
                        addColumn(draggingNode);
                    }
                } else {
                    if ($("#configTab").find(".toggleBtn").hasClass("togglebtn-down")) {
                        $("#configTab").find(".toggleBtn").addClass("togglebtn-down");
                        $("#searchDiv").slideToggle('fast',function() {
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

    function getDraggingParentNode(node){
    	if(node.upId != '0'){
   			getDraggingParentNode(moduleTreeObj.getNodeByParam("id",node.upId));
        }
    	draggingParentNodes.push(node.tId);
    }


	function  loadStoreTree(){
		BIONE.loadTree("${ctx}/report/input/tasksearch/supplement/getAuthDetailTree?defSrc=03",
				storeTreeObj, null, null, false);
	}

// 	function addHoverDom(treeId, treeNode) {
// 		if (treeNode.id == "0") {
// 			return;
// 		}
// 		var sObj = $("#" + treeNode.tId + "_span");
// 		if ($("#delBtn_"+treeNode.id).length>0) return;
// 		var addStr = "<span class='button remove' id='delBtn_" + treeNode.id
// 			+ "'title='删除' onfocus='this.blur();'></span>";
// 		sObj.after(addStr);
// 		var btn = $("#delBtn_"+treeNode.id);
// 		if (btn) {
// 			btn.bind("click", function(e){
// 				$.ligerDialog.confirm('确实要删除这条记录吗!',
// 						function(yes) {
// 							if (yes) {
// 								$.ajax({
// 									type : "DELETE",
// 									url : '${ctx}/report/input/tasksearch/supplement/deleteTmpInfo',
// 									dataType : "json",
// 									type : "post",
// 									data : {
// 										"rptId" : treeNode.id
// 									},
// 									success : function(result) {
// 										if (result.msg == "ok") {
// 											BIONE.tip("删除成功");
// 											storeTreeObj.removeNode(treeNode);
// 										} else {
// 											BIONE.tip('删除失败');
// 										}
// 									}
// 								});
// 							}
// 				});
// 				e.stopPropagation();
// 			});
// 		}
// 	};
	
// 	function removeHoverDom(treeId, treeNode) {
// 		$("#delBtn_"+treeNode.id).unbind().remove();
// 	}
	
	
	//导出文件
	function downLoadAttach(data,url){
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data: data,
			type : "GET",
			beforeSend : function() {
				BIONE.showLoading("数据文件生成中，请稍等");
			},
			complete : function() {
				BIONE.hideLoading();
			},
			success : function(result) {
				if(!result.path || result.path==""){
					BIONE.tip("数据文件生成失败");
					return;
				}
				else{
					var src = '';
					src = "${ctx}/report/frame/datashow/detail/download?path="+result.path;
					downdload.attr('src', src);
				}
			},
			error : function(result, b) {
				BIONE.tip("数据文件生成失败");
			}
		});


	
// 		if(true){
// 			data.isBigData = "N";
			
// 		}
// 		else{
// 			$.ajax({//先判断数据量在选择下载方式
// 				cache : false,
// 				async : false,
// 				url : "${ctx}/report/frame/datashow/detail/getExpDataCount",
// 				dataType : 'json',
// 				data : data,
// 				type : 'post',
// 				success : function(res){
// 					if(res.dataNum <= 20000){//数据小于2万走常规下载
// 						data.isBigData = "N";
// 						$.ajax({
// 							cache : false,
// 							async : true,
// 							url : url,
// 							dataType : 'json',
// 							data: data,
// 							type : "post",
// 							beforeSend : function() {
// 								BIONE.showLoading("数据文件生成中，请稍等");
// 							},
// 							complete : function() {
// 								BIONE.hideLoading();
// 							},
// 							success : function(result) {
// 								if(!result.path || result.path==""){
// 									BIONE.tip("数据文件生成失败");
// 									return;
// 								}
// 								else{
// 									var src = '';
// 									src = "${ctx}/report/frame/datashow/detail/download?path="+result.path;
// 									downdload.attr('src', src);
// 								}
// 							},
// 							error : function(result, b) {
// 								BIONE.tip("数据文件生成失败");
// 							}
// 						});
// 					}else{
// 						data.isBigData = "Y";
// 				 		$.ajax({
// 							cache : false,
// 							async : true,
// 							url : url,
// 							dataType : 'json',
// 							data: data,
// 							type : "post",
// 							beforeSend : function() {
// 								BIONE.tip("数据文件生成中，请等待消息生成附件！");
// 							},
// 							success : function(result) {
// 								if(result.path && result.path != ""){
// 									window.top.refreshMsg();
// 									BIONE.tip("有新消息！");
// 								}
// 							},
// 							error : function(result, b) {
// 								BIONE.tip("数据文件生成失败");
// 							}
// 						});
// 					}
// 				}
// 			});
// 		}
		
	}
	
	
	
	function initModelGrid(columns) {
		var columns = [];
	    grid = $('#grid').ligerGrid({
			toolbar : {},
			width : '100%',
			columns : columns,
			usePager : true,
			checkbox : false,
			dataAction : 'server',
			allowHideColumn : false,
			enabledSort : false,
			delayLoad : true,
			url : "${ctx}/report/input/tasksearch/supplement/showView",
			onBeforeShowData : function(res) {
				if(res.Total && typeof res.Total == "string") {
// 					BIONE.tip(res.Total);
				}
			},
			onAfterChangeColumnWidth : function() {
				var columns = grid.getColumns();
				for (var i = 0; i < columns.length; i++) {
					var $selectedLi = $("td[columnname=" + columns[i].name + "]").find("div:first");
					colInfos[columns[i].name].width = $selectedLi.width()
				}
				$(".delBtn").each(function() {
						$(this).css("left",$(this).parent().parent().width() - 10);
					});
			},
			onAfterShowData : function() {
			},
			onBeforeChangeColumnWidth : function() {
			},
			mouseoverRowCssClass : null
		});
		var buttons = [{	
			text : "查询",
			icon : "search3",
			click : function() {
				var configs = [];
				for ( var i in colInfos) {
					configs.push(colInfos[i]);
				}
				try {
					grid.setParm("newPageSize",null);
					if(!window.usePager){
						grid.setParm("pagesize",window.pageSize);
						grid.options.pagesize=window.pageSize;
					}
					BIONE.validate($("#searchForm"));
					if ($("#searchForm").valid()) {
						var searchArgs = $('#searchForm').getJson();
						var sargs = [];
						for ( var i = 0;i < searchArgs.length;i++) {
							if(searchArgs[i].name == "@rowNum"){
								if(window.usePager)
									grid.setParm("newPageSize",searchArgs[i].value);
								else{
									grid.setParm("pagesize",searchArgs[i].value);
									grid.options.pagesize=searchArgs[i].value;
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
						grid.setParm("searchInfo", JSON2.stringify(searchArgs));
						grid.setParm("config", JSON2.stringify(configs));
// 						grid.setParm("filterInfo", JSON2.stringify(filterInfo));
						if (colSorts.length > 0){
							grid.setParm("sort", JSON2.stringify(colSorts));
						}else{
							grid.setParm("sort", "");
						}
						if (colSums.length > 0){
							grid.setParm("sum", JSON2
									.stringify(colSums));
						}else{
							grid.setParm("sum", "");
						}

					    var rootChildren = moduleTreeObj.getNodesByFilter(getTableNode);
					    if(rootChildren.length != 1){
						    return;
					    }

					    grid.setParm("tabEnNm",rootChildren[0].params.tabEnNm);
					    grid.setParm("dsId",rootChildren[0].params.dsId);
					    grid.setParm("templeId",rootChildren[0].params.templeId);
						
						grid.setParm("newPage",1);
						grid.options.newPage=1;
						grid.loadData();
				    }
				} catch (e) {

					} 
		    }
		}/* ,{
			text : "操作",
			icon : "config",
			menu : {
				items : [{
					text : "重置查询",
					icon : "refresh2",
					click : function() {
						$("#searchForm").resetForm();
					}
				},{
					text : "清空",
					icon : "delete",
					click : function() {
						panel.removeAll();
						params=[];
						createParams("searchForm", params);
						window.colInfos = {};
						window.columns = [];
						setId = "";
						grid.set("columns", []);
						grid.reRender();
						moduleTreeObj.reAsyncChildNodes(null, "refresh");
					}
				}]
			}
		} */,{
            text : '排序配置',
            click : function() {
                BIONE.commonOpenDialog("排序配置", "sortEdit", 600, 350,
                                "${ctx}/report/frame/datashow/detail/sortconfig");
            },
            icon : "settings"
        }/* ,{
			text : "配置",
			icon : "config",
			menu : {
				items : [{
					text : '排序配置',
					click : function() {
						BIONE.commonOpenDialog("排序配置", "sortEdit", 600, 350,
										"${ctx}/report/frame/datashow/detail/sortconfig");
					},
					icon : "settings"
				},
				{
					text : '高级筛选',
					click : function() {
						BIONE.commonOpenDialog("高级筛选", "filterEdit", 800,450,
										"${ctx}/report/frame/datashow/detail/filterconfig");
					},
					icon : "settings"
				},
				{
					text : '汇总配置',
					click : function() {
						BIONE.commonOpenDialog("汇总配置", "sumEdit", 800,450,
										"${ctx}/report/frame/datashow/detail/sumconfig");
					},
					icon : "settings"
				}]
			}
		} */,{
            text : '导出',
            icon : "export",
            menu :{
				items : [{
					text : '导出当前页',
					icon : "export",
					click : function() {
						for (var i  in colInfos) {
			                exportFile(false);
			                return;
		                }
		                BIONE.tip("请配置查询列");
		                return;
					}
				},{
					text : '导出全部',
					icon : "export",
					click : function() {
						for (var i  in colInfos) {
			                exportFile(true);
			                return;
		                }
		                BIONE.tip("请配置查询列");
		                return;
					}
				}]
            }
        },{
            text : "重置查询",
            icon : "refresh2",
            click : function() {
                $("#searchForm").resetForm();
            }
        },{
            text : "清空",
            icon : "delete",
            click : reflash
        }/* ,{
			text : "导出",
			icon : "export",
			menu : {
				items :[{
					text : '导出EXCEL',
					click : function() {
						if (colInfos.length <= 0) {
							BIONE.tip("请配置查询列");
							return;
						}
						exportFile();
					},
					icon : "export"
				},
				{
					text : '导出CSV',
					click : function() {
						if (colInfos.length <= 0) {
							BIONE.tip("请配置查询列");
							return;
						}
						exportcsv();
					},
					icon : "export"
				}]
			}
		} */];
		BIONE.loadToolbar(grid, buttons);
		grid.setHeight($(document).height() - $("#searchDiv").height() - 60);
// 		$("#showGrid").find(".l-panel-topbar").append("<span style='margin-left:8px;'>分页</span><div style='float:left;margin-top:6px;margin-left:15px;'><input id='page' type='checkbox' checked='true'></input></div>");
		window.usePager = true;
		grid.options.pagesize = parseInt($("#showGrid").find("select[name=rp]").val());
		grid.set("usePager",true);
		
		$("#showGrid").find("#page").bind("click",function(){
			if($(this)[0].checked){
				$("#showGrid").find(".l-panel-bar").show();
				$("#showGrid").find(".l-grid").height($("#showGrid").find(".l-grid").height()-30);
				$("#showGrid").find(".l-grid-body").height($("#showGrid").find(".l-grid-body").height()-30);
				for(var i =0 ; i <grid.options.parms.length; i++ ){
					if(grid.options.parms[i].name == "pagesize"){
						grid.options.parms.splice(i,1);
						break;
					}
					
				};
			}
			else{
				grid.set("usePager",false)
				$("#showGrid").find(".l-panel-bar").hide();
				$("#showGrid").find(".l-grid").height($("#showGrid").find(".l-grid").height()+30);
				$("#showGrid").find(".l-grid-body").height($("#showGrid").find(".l-grid-body").height()+30);
				window.usePager = false;
				window.pageSize = 2000;
			}
		});
		
		function exportFile(isAll){
			BIONE.validate($("#searchForm"));
			var data = {};
			if ($("#searchForm").valid()) {
				var configs = [];
				for ( var i in colInfos) {
					configs.push(colInfos[i]);
				}
				var searchArgs = $('#searchForm').getJson();
				var sargs = [];
				for ( var i = 0; i < searchArgs.length; i++) {
					if(searchArgs[i].name == "@rowNum"){
						if(window.usePager)
							grid.setParm("newPageSize",searchArgs[i].value);
						else{
							grid.setParm("pagesize",searchArgs[i].value);
							grid.options.pagesize=searchArgs[i].value;
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
				data.searchInfo = JSON2.stringify(sargs);
				data.config = JSON2.stringify(configs);
// 				data.filterInfo = JSON2.stringify(filterInfo);
				data.sort = JSON2.stringify(colSorts);
// 				data.sum = JSON2.stringify(colSums);

				var rootChildren = moduleTreeObj.getNodesByFilter(getTableNode);
			    if(rootChildren.length != 1){
				    return
			    }
			    if(!isAll){
				    data.pagesize = grid.options.pagesize;
				    data.page = grid.options.page;
			    }else{
				    data.pagesize = '99999999';
			    }
				data.tabEnNm = rootChildren[0].params.tabEnNm;
				data.dsId = rootChildren[0].params.dsId;
				data.templeId = rootChildren[0].params.templeId;
				data.fileCnNm = rootChildren[0].text;

				var _url = "${ctx}/report/input/tasksearch/supplement/excel";
				downLoadAttach(data,_url);
			}
		}
		
		function exportcsv(){
			BIONE.validate($("#searchForm"));
			var data = {};
			if ($("#searchForm").valid()) {
				var configs = [];
				for ( var i in colInfos) {
					configs.push(colInfos[i]);
				}
				var searchArgs = $('#searchForm').getJson();
				var sargs = [];
				for ( var i = 0; i < searchArgs.length; i++) {
					if(searchArgs[i].name == "@rowNum"){
						data.pagesize = searchArgs[i].value;
						continue;
					}
					var info = searchArgs[i].name.split("|");
					if (info.length >= 2) {
						searchArgs[i].name = info[0];
						searchArgs[i].colId = info[1];
					}
					sargs.push(searchArgs[i]);
				}
				data.searchInfo = JSON2.stringify(sargs);
				data.config = JSON2.stringify(configs);
				data.filterInfo = JSON2.stringify(filterInfo);
				data.sort = JSON2.stringify(colSorts);
				data.sum = JSON2.stringify(colSums);
				var _url = "${ctx}/report/frame/datashow/detail/csvExport";
				downLoadAttach(data,_url);
			}
		}
	}
	function getTableNode(node){
		return node.params.type == 'table';
	}
	function prepareSqlInfo(tmpId) {
		var sql = {
			templateId : tmpId,
			querysql : cursql,
			paramtmpId : paramId
// 			dsId : $("#dsId_val").val()
		};
		var data = {
			sql : JSON2.stringify(sql)
		}
// 		data.dsId = $("#dsId_val").val();
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
			params[i].required = (params[i].required == "true"||params[i].required==true) ? "Y" : "N";
			params[i].checkbox = (params[i].checkbox == "true"||params[i].checkbox==true) ? "Y" : "N";
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
// 		data.dsId = $("#dsId_val").val();
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
			var $selectedLi = $("td[columnname=" + columnName + "] .l-grid-hd-cell-inner ");
			var delImg = $(
					"<img src='${ctx}/images/classics/icons/icons_label_cross.png' colId = '"+columnName+"' style='width:7px;height:7px' class='l-grid-dim-filter' />")
					.attr('title', '删除');
			delImg.click(function() {
				deleteCol(this, $(this).attr("colId"));
			});
// 			var cofImg = $(
// 					"<img src='${ctx}/images/classics/icons/cog.png' colId = '"+columnName+"' style='width:16px;height:16px' class='l-grid-dim-filter' />")
// 					.attr('title', '配置');
// 			cofImg.click(function() {
// 				openConfigDialog($(this).attr("colId"));
// 			});
			var deldiv = $("<div id='delBtn' class='delBtn' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
			deldiv.css("left", ($selectedLi.width() - 10))
			     .css("top",0 - $(".l-panel-topbar").height() - $selectedLi.position().top + 10).css("width","7px");
			deldiv.append(delImg);
			$selectedLi.append(deldiv);
// 			var cofdiv = $("<div id='cofdiv' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
// 			cofdiv.css("left", "10px")
// 			     .css("top", 0 - $(".l-panel-topbar").height()- $selectedLi.position().top + 5).css("width","7px");
// 			cofdiv.append(cofImg);
// 			$selectedLi.append(cofdiv);
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
		}
	}

	function getParam(uuid) {
		for ( var i in params) {
			if (params[i].uuid == uuid) {
				return params[i];
			}
		}
		return null;
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
			} else if (params[i].elementType == "01") {
				var val = {
					type : "01",
					op : $.ligerui.get(params[i].paramId).getValue(),
					val : $("#" + params[i].paramId).prev().find("input").val()
				};
				vals.push(val);
			} else if (params[i].elementType == "02") {
				var val = {
					type : "02",
					op : $.ligerui.get(params[i].paramId).getValue(),
					val : $("#" + params[i].paramId).prev().find("input").val()
				};
				vals.push(val);
			} else if (params[i].elementType == "03") {
				var val = {
					type : "03",
					val : $.ligerui.get(params[i].paramId).getValue(),
					text : $.ligerui.get(params[i].paramId).getText()
				};
				vals.push(val);
			} else if (params[i].elementType == "04") {
				var val = {
					type : "04",
					val : $.ligerui.get(params[i].paramId).getValue(),
					text : $.ligerui.get(params[i].paramId).getText()
				};
				vals.push(val);
			} else if (params[i].elementType == "05") {
				var val = {
					type : "05",
					val : $.ligerui.get(params[i].paramId).getValue()
				};
				vals.push(val);
			} else if (params[i].elementType == "06") {
				var val = {
					type : "06",
					val : $.ligerui.get(params[i].paramId).getValue()
				};
				vals.push(val);
			} else if (params[i].elementType == "07") {
				var val = {
					type : "07",
					val : $.ligerui.get(params[i].paramId).getValue(),
					text : $.ligerui.get(params[i].paramId).getText()
				};
				vals.push(val);
			} else if (params[i].elementType == "08") {
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
			} else if (params[i].elementType == "09") {
				var val = {
					type : "09",
					val : $.ligerui.get(params[i].paramId).getValue(),
					text : $.ligerui.get(params[i].paramId).getText()
				};
				vals.push(val);
			} else {
				var val = {
					type : "-1"
				}
				vals.push(val);
			}
		}
		return vals;
	}
	function addParam() {
		var vals = getParams();
		var searchArgs = $('#searchForm').getJson();
		var type = "09";
		var uid = uuid.v1();
		uid = uid.split("-").join("");
		var enNm = draggingNode.params.fieldEnName;
// 		var data = [ {
// 			id : "10",
// 			text : "10"
// 		}, {
// 			id : "20",
// 			text : "20"
// 		}, {
// 			id : "30",
// 			text : "30"
// 		} ];
// 		if (enNm != "@rowNum") {
// 			enNm = draggingNode.params.fieldEnName + "|" ;
// 			if (true) {
// 				enNm += "|" ;
// 			}
// 			type = getParamsType(draggingNode.params.fieldType);
// 			data = [];
// 		}
// 		checkbox = "false";
// 		if (type == "04"
// 				|| (type == "03" && draggingNode.params.dimTypeStruct == "02")) {
// 			checkbox = "true";
// 		}
		var param = {
			daterange : "",
			paramId : "",
			uuid : uid,
			enNm : enNm,
			cnNm : draggingNode.text,
			elementType : "01",
			orderno : params.length,
			required : false,
			checkbox : true,
			isConver : "1",
			defValue : "",
			dataJson : JSON2.stringify([])
		};
		params.push(param);
		panel.add({
			id : uid,
			text : draggingNode.text
		});
		createParams("searchForm", params, vals);

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

	function addColumn(draggingNode) {
		var cfgId = draggingNode.params.fieldEnName;
		if(colInfos[cfgId]){
            return;
        }
		var width = draggingNode.text.length * 12 + 70 > 150 ? draggingNode.text.length * 12 + 70
				: 150;
		var column = {
			display : draggingNode.text,
			name : draggingNode.params.fieldEnName,
			width : width,
			align : "left"
		};

		if(draggingNode.params.fieldType == 'NUMBER' && draggingNode.params.decimalLength > 0){
			column.render = function(record,rowNm,val){
				val = parseFloat(val);
// 				return formatMoney(val.toFixed(2));
				return isNaN(val)? "" : val.toFixed(2);
			}
		}

		
		var displayFormat = "00";
		if (draggingNode.data.dbType == "03")
			displayFormat = "yyyy年MM月dd日";
		var colInfo = {
			id : {
				cfgId : cfgId
			},
			enNm : draggingNode.params.fieldEnName,
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
			dataPrecision : 2,
			fieldType : draggingNode.params.fieldType
		};
		colInfos[cfgId] = colInfo;
		columns.push(column);
		grid.set("columns", columns);
		grid.reRender();
		addDivToColumn();
	}

	function formatMoney(number, places, symbol, thousand, decimal) {
	    number = number || 0;
	    places = !isNaN(places = Math.abs(places)) ? places : 2;
	    symbol = symbol !== undefined ? symbol : "";
	    thousand = thousand || ",";
	    decimal = decimal || ".";
	    var negative = number < 0 ? "-" : "",
	        i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
	        j = (j = i.length) > 3 ? j % 3 : 0;
	    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
	}

	function addColumns(nodes) {
		for (var i = 0; i < nodes.length; i++) {
			if (nodes[i].params.realId == "@rowNum") {
				continue
			}
			var draggingNode = nodes[i];
			var cfgId = draggingNode.params.fieldEnName;
			if(colInfos[cfgId]){
				continue;
			}
			var width = draggingNode.text.length * 12 + 70 > 150 ? draggingNode.text.length * 12 + 70
					: 150;
			var column = {
				display : draggingNode.text,
				name : cfgId,
				width : width,
				align : "left"
			};
			if(draggingNode.params.fieldType == 'NUMBER' && draggingNode.params.decimalLength > 0){
				column.render = function(record,rowNm,val){
					val = parseFloat(val);
// 					return formatMoney(val.toFixed(2));
					return isNaN(val)? "" : val.toFixed(2);
				}
			}

					
// 			if(draggingNode.params.fieldType == 'NUMBER'){
// 				column.render = function(record,rowNm,val){
// 					debugger;
// 					if(val == ""){
// 						return "";
// 					}
// 					val = val + "";
// 					if(val.indexOf('.') != -1){
// 						var vals = val.split(".");
// 						if(vals[1].length >= 2){
// 							return val;
// 						}else{
// 							return val + addZero(2 - vals[1].length);
// 						}
						

// 					}else{
// 						var index;
// 						try{
// 							index = parseInt(draggingNode.params.decimalLength);
// 							index = isNan(index) ? 0 : index;
// 						}catch(e){
// 							index = 0;
// 						}
// 						index = parseInt(index);
						
// 						if(index > 2){
// 							return val + '.00';
// 						}else if(index == 0){
// 							return val;
// 						}else{
// 							return  val + '.' + addZero(index);
// 						}
// 					}
// 				}
// 			}
			
			var displayFormat = "00";
			if (draggingNode.data.dbType == "03")
				displayFormat = "yyyy年MM月dd日";
			var colInfo = {
				id : {
					cfgId : cfgId
				},
				enNm : draggingNode.params.fieldEnName,
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
				dataPrecision : 2,
				fieldType : draggingNode.params.fieldType
			};
			colInfos[cfgId] = colInfo;
			columns.push(column);
		}
		grid.set("columns", columns);
// 		grid.reRender();
		addDivToColumn();
	}

	function addZero(index){
		index = parseInt(index);
		var zero = '';
		for(var i = 0; i < index; i++){
			zero += '0';
		}
		return zero;
	}

	function openConfigDialog(cfgId) {
		BIONE.commonOpenDialog("列配置", "attrEdit", 600, 350,
						"${ctx}/report/frame/datashow/detail/attrconfig?cfgId="
								+ cfgId);
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
		require([ 'jquery', 'JSON2', 'template/viewMain' ],
				function() {$(function() {
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
										$('#' + divName).templateView({
											data : JSON2.parse(data.paramJson)
										});
										var params = JSON2.parse(data.paramJson);
										for ( var i in params) {
											if (params[i].checkbox == "true" || params[i].isMultiSelect == "true") {
												checkBoxId.push(params[i].name);
											}
										}
									} catch (e) {
										
									} finally {
										$('#temps-loading').css('display', 'none');
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
<!-- 		<div class="l-form" style="margin: 2px;"> -->
<!-- 			<ul> -->
<!-- 				<li style="width: 40%;">数据源选择：</li> -->
<!-- 				<li style="width: 59%;"><input id="dsId" /></li> -->
<!-- 			</ul> -->
<!-- 		</div> -->
		<div id="treeSearchbar" style="width: 99%; margin-top: 2px; padding-left: 2px;">
			<ul>
				<li style="width: 98%; text-align: left;">
					<div class="l-text-wrapper" style="width: 100%;">
						<div class="l-text l-text-date" style="width: 100%;">
							<input id="treeSearchInput" type="text" class="l-text-field" style="width: 100%;" />
							<div class="l-trigger">
								<i id="treeSearchIcon" style="width:100%;height:100%;" class="icon-search search-size"></i>      
							</div>
						</div>
					</div>
				</li>
			</ul>
		</div>
		<div id="treeContainer" style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="modeltree" style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree"></ul>
		</div>
	</div>
	<div id="template.right">
		<div id="view">
			<div id="searchView">
				<div id="searchtable" width="100%" border="0">
					<div  width="100%" style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
						<div style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
							<img src="${ctx }/images/classics/icons/chart_pie_edit.png" />
						</div>
						<div>
							<span style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							       查询条件 
							</span>
						</div>
						<i class="toggleBtn icon-up"></i>
					</div>
				</div>
				<div id="searchDiv" style="height: 160px;" >
					<div style="height: 32px;">
						<span style="float: left; width: 60px; line-height: 32px; margin-left: 8px; height: 32px;">
							         筛选条件：
							</span>
						<div id = "paneldiv" style="margin-top: 1px; float: right;">
							<div id="panel" style="overflow-y: auto;width:100%"></div>
						</div>
					</div>
					<div style="margin-top: 10px; border-top: 1px solid #ddd; height: 128px; overflow-y: auto;">
						<div id='temps-loading' class='l-tab-loading' style="display: block;height: 128px;top:68px;"></div>
						<form id="searchForm" style="width: 99%;"></form>
					</div>
	
				</div>
			</div>
			<div id="showGrid" oncopy="return false" oncut="return false">
				<div id="grid"></div>
			</div>
		</div>
<!-- 		<div id="tmp"> -->
<%-- 			<div id='cover' class='l-tab-loading' style='display:block; background:url(${ctx}/images/classics/index/bg_center.jpg) no-repeat center center #ffffff;'></div> --%>
<!-- 			<div id="tmp_tab" style="width: 100%; overflow: hidden;"></div> -->
<!-- 		</div> -->
	</div>
</body>
</html>