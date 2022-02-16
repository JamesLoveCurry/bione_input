<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="decorator" content="/template/template14.jsp" />
<%@ include file="/common/spreadjs_load.jsp"%>
<style type="text/css">
        .l-table-edit {}
        .l-table-edit-td{ 
        	padding:4px;
        }
        .l-button-submit,.l-button-reset{
        	width:80px; 
        	float:left; 
        	margin-left:10px; 
        	padding-bottom:2px;
        }
        .l-verify-tip{ 
        	left:230px; 
        	top:120px;
        }
        .own-formitem-style{
        	width:108px;
        }
</style>	
<script type="text/javascript">

	var objTmp = null;

	var lineId = "${lineId}";

	var frameDocument = window.parent.parent;
	// 设计器相关对象 - begin
	var Design,
		   spread,
		   RptIdxInfo,
		   Utils,
		   Uuid,jsonTmp;
	// 设计器相关对象 - end
	var templateType = frameDocument.templateType; // 01 - 明细类；02 - 单元格类
	var templateId = "";
	var titles = [];
	var moduleTreeObj;
	var indexTreeObj;
	var treeUrls = [];
	var layout;
	
	var catalogType = "01";
	var moduleType = "02";
	var colCatalogType = "03";
	var colType = "04";
	
	// 默认标签头图标
	var labelIcon = "/images/classics/icons/column.png";
	// 用于找到可拖拽对象的css类名
	var dragClassName = "bioneDrag";
	
	var treeInitFlag = true;
	
	// 正在拖拽的节点对象
	var draggingNode;
	var draggingTreeId;
	
	// 已选中的报表指标单元格
	var selectionIdx = {};
	
	$(function() {
		// 布局
		initLayout();
		// 左侧的树
		initTree();
		// 设计器
		initDesign();
	});
	
	// 初始化布局
	function initLayout(){
		// 初始化title
		var centerWidth = $("#center").width();
		layout = $("#designLayout").ligerLayout({ 
			height : $("#center").height(),
			leftWidth : (centerWidth - 220) * 0.21,
			centerWidth : (centerWidth - 220) * 0.79,
			rightWidth : 220,
			allowLeftResize : true ,
			allowRightResize : true ,
			onEndResize : function(){
				if(Design){
					Design.resize(Design.spread);
				}
			}
		});
		$("input").css("color", "#333").attr("readOnly", "true");
		$("select").css("color", "#333").attr("disabled", "disabled");
		$("textarea").css("color", "#333").attr("readOnly", "true");
		// 对resize的相关处理
		resizeHandler();
	}
	
	
	
	// resize时的相关处理
	function resizeHandler(){
		$("#remarkToggle").bind("click" , function(){
			// 鼠标点击
			var type = $("#remarkToggle").attr("currType");
			if(type == "show"){
				// 显示时
				$("#remarkDiv").children(".l-textarea").hide();
				$("#spread").height($("#spread").height() + 70);
				$("#remarkToggle").css("background-position","0px 0px");
				$("#remarkToggle").attr("currType","hide");
				if(Design){
					var heightTmp = Design.spreadDom.height();
					Design.spreadDom.height(heightTmp + 70);
					Design.resize(spread);	
				};
			} else {
				// 隐藏时
				$("#remarkDiv").children(".l-textarea").show();
				$("#spread").height($("#spread").height() - 70);
				$("#remarkToggle").css("background-position","0px -40px");
				$("#remarkToggle").attr("currType","show");
				if(Design){
					var heightTmp = Design.spreadDom.height();
					Design.spreadDom.height(heightTmp - 70);
					Design.resize(spread);	
				};
			}
		});
		if(layout){
			var leftToggleDoom = $(".l-layout-left").children(".l-layout-header").children(".l-layout-header-toggle");
			leftToggleDoom.unbind("click");
			leftToggleDoom.bind("click" , function(){
				layout.setLeftCollapse(true);
				if(Design){
					Design.resize(spread);	
				};
			});
			layout.leftCollapse.toggle.unbind("click");
			layout.leftCollapse.toggle.bind("click" , function(){
				layout.setLeftCollapse(false);
				if(Design){
					Design.resize(spread);	
				};
			});
			var rightToggleDoom = $(".l-layout-right").children(".l-layout-header").children(".l-layout-header-toggle");
			rightToggleDoom.unbind("click");
			rightToggleDoom.bind("click" , function(){
				layout.setRightCollapse(true);
				if(Design){
					Design.resize(spread);	
				};
			});
			layout.rightCollapse.toggle.unbind("click");
			layout.rightCollapse.toggle.bind("click" , function(){
				layout.setRightCollapse(false);
				if(Design){
					Design.resize(spread);	
				};
			});
		}
	}
	
	// 初始化左侧树
	function initTree(){
		if(templateType == "01"){
			moduleTreeHandler();
			// 初始化节点
			initNodes();
		}else if(templateType == "02"){
			indexTreeHandler();
		}else if(templateType == "03"){
			// 初始化数据模型树
			moduleTreeHandler($($(".treeContainer")[0]).children("ul"));
			// 初始化节点
			initNodes("01");
			// 初始化指标树
			indexTreeHandler($($(".treeContainer")[1]).children("ul"));
		}
	}
	
	function moduleTreeHandler(target){
		target = target ? target : $(".treeContainer").children("ul");
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
				callback:{
				
				}
			};
			moduleTreeObj = $.fn.zTree.init(target,async,[]);
			// 明细报表时，控制只展开到【数据模型】级
			moduleTreeObj.setting.callback.beforeExpand = function(treeId , treeNode){
				if(treeInitFlag === false){
					// 非初始化时，直接展开
					return true;
				}
				if(moduleType == treeNode.params.type){
					return false;
				}
				return true;
			};
			moduleTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){						
				if(colType == treeNode.params.type){
					setDragDrop("#"+treeNode.tId+"_a");
				}
				moduleTreeObj.expandNode(moduleTreeObj.getNodeByParam("id" , treeNode.id , null) ,true , false , false , true);
			}
	}
	
	function indexTreeHandler(target){
		target = target ? target : $(".treeContainer").children("ul");
		//edit by fangjuan 2014-09-04
		var async ={
				async:{
					enable:true,
					url:treeUrls["02"],
					autoParam:["nodeType", "upId", "indexVerId"],
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
							}
						}
						return childNodes;
					}
				},
				data:{
					key:{
						name:"text"
					}
				},
				view:{
					selectedMulti:false
				},
				callback:{
				}
		};
		indexTreeObj = $.fn.zTree.init(target,async);
		indexTreeObj.setting.callback.beforeExpand = function(treeId , treeNode){
			if(treeInitFlag === false){
				// 非初始化时，直接展开
				return true;
			}
			if(moduleType == treeNode.params.type){
				return false;
			}
			return true;
		};
		indexTreeObj.setting.callback.onNodeCreated = function(event , treeId , treeNode){
			if(("idxInfo" == treeNode.params.nodeType && (!treeNode.params.haveMeasure) )|| treeNode.params.nodeType == "measureInfo"){
				setDragDrop("#"+treeNode.tId+"_a");
			}
		}
	}
	
	function initNodes(type){
		type = type ? type : templateType;
		if(moduleTreeObj == null 
				|| typeof moduleTreeObj == "undefined"){
			return ;
		}
		$.ajax({
			cache : false,
			async : true,
			url : treeUrls[type],
			dataType : 'json',
			type : "post",
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					// 移除旧节点
					moduleTreeObj.removeChildNodes(moduleTreeObj.getNodeByParam("id" , '0' , null));
					moduleTreeObj.removeNode(moduleTreeObj.getNodeByParam("id", '0', null) , false);
					// 渲染新节点
					moduleTreeObj.addNodes(moduleTreeObj.getNodeByParam("id", '0', null) , result , true);
				}
				treeInitFlag = false;
			},
			error:function(){
				treeInitFlag = false;
				//BIONE.tip("加载失败，请联系系统管理员");
			}
		});
	}

	// 初始化设计器
	function initDesign(){
		require.config({
			baseUrl : "${ctx}/plugin/js/",
			paths:{
				"design" : "cfg/views/rptdesign",
				"utils" : "cfg/utils/designutil"
			}
		});
		require(["design" , "utils"] , function(ds , utils){
			Utils = utils;
			var jsonStr = "";
			if(window.parent.uptObjArr != null){
				var mainTmpObj = window.parent.uptObjArr["_mainTab"];
				if(lineId == null
						|| lineId == ""){
					// 主模板
					objTmp = mainTmpObj;
					jsonStr = objTmp.tmpInfo.templateContentjson;
				}else{
					objTmp = window.parent.uptObjArr[lineId];	
					jsonStr = mainTmpObj.tmpInfo.templateContentjson;
				}
			}
			if((jsonStr == null
					|| jsonStr == ""
					|| typeof jsonStr == "undefined")
					&& lineId != ""){
				jsonStr = initFromJson();
			}
			var settings = {
					targetHeight : ($("#centerDiv").height() - 2 - 2) ,
					templateType : templateType,
					ctx : "${ctx}" ,
					readOnly : true ,
					onLeaveCell : spreadLeaveCell,
					onCellDoubleClick : spreadDbclkCell,
					onSelectionChanged : spreadSelectionChanged,
					onClipboardChanging : clipboardChanging,
					onClipboardPasted : clipboardPasted,
					cellDetail:true,
					toolbar:false,
					isBusiLine : lineId == "" ? false : true , 
					// 报表指标数据初始化
					moduleCells : objTmp==null ? null : objTmp.moduleCells,
					formulaCells : objTmp==null ? null : objTmp.formulaCells,
					idxCells : objTmp==null ? null : objTmp.idxCells,
					staticCells : objTmp==null ? null : objTmp.staticCells,
					idxCalcCells : objTmp==null ? null : objTmp.idxCalcCells,
					initJson : jsonStr
			};
			Design = ds;
			spread = ds.init($("#spread") , settings);
			Uuid = ds.Uuid;
			RptIdxInfo = ds.RptIdxInfo;
			// 单元格信息
			initCellForm();
		})
	}
	
	//添加拖拽控制
 	function setDragDrop(dom){
 		if(typeof dom == "undefined"
 			|| dom == null){
 			return ;
 		}
 		$(dom).ligerDrag({
 			proxy : function(target , g , e){
 				var defaultName = "字段  ";
 				if(templateType == "02"){
 					defaultName = "指标  ";
 				}
 				var proxyLabel = "${ctx}"+labelIcon;
 				var targetTitle = $(dom).attr("title") == null ? defaultName : $(dom).attr("title");
 				var proxyHtml = 
                    '<div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;">'+
                  	 	'<span style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url('+proxyLabel+')" ></span>'+
 						'<span style="padding-left: 14px;">'+targetTitle+'</span>'+
                    '</div>';
                  var div = $(proxyHtml);
                  div.appendTo('#center');
                  return div;
 			},
 			revert : false , 
 			receive : "#spread" , 
 			onStartDrag : function(current , e){
				// 获取拖拽树节点信息
 				var treeAId = current.target.attr("id");
				if(treeAId){
					var strsTmp = treeAId.split("_");
					var treeId = treeAId;
					if(strsTmp.length > 1){
						var newStrsTmp = [];
						for(var i = 0 ; i < strsTmp.length - 1 ; i++){
							newStrsTmp.push(strsTmp[i]);
							if(i == 0){
								draggingTreeId = strsTmp[i];
							}
						}
						treeId = newStrsTmp.join("_");
					}
					var treeObj = moduleTreeObj;
					if(templateType == "02"){
						treeObj = indexTreeObj;
					} else if (templateType == "03"){
						treeObj = $.fn.zTree.getZTreeObj(draggingTreeId);
					}
					draggingNode = treeObj.getNodeByTId(treeId);
				}
 			},
 			onDrop : function(obj , target , e){
 				if(typeof spread != "undefined"
					&& spread != null
					&& e != null){
					var sheet = spread.getActiveSheet();
					var canvasOffset = sheet._eventHandler._getCanvasOffset();
					var absolutePosX = e.clientX - canvasOffset.left;
					var absolutePosY = e.clientY - canvasOffset.top;
					var currRow = null;
					var currCol = null;
					if($.browser.msie && parseInt($.browser.version, 10) < 9){
						// 由于silverlight未提供相应接口，hitTest是反编译扩展的方法，调用方式很恶心
						var posStr = spread.canvas.firstChild.Content.SpreadsheetObject.hitTest(absolutePosX,absolutePosY);
						if(posStr != null && typeof posStr == "object"){
							currRow = posStr.Row;
							currCol = posStr.Col;
						}
					}else{    						
						var targetCell = sheet.hitTest(absolutePosX,absolutePosY);
						if(targetCell != null){								
							var targetRow = targetCell.row;
							var targetCol = targetCell.col;
							if((typeof targetRow == "undefined"
									|| targetRow == null)
									&& (typeof targetCol == "undefined"
									&& targetCol == null)){
								currRow = sheet.getActiveRowIndex();
								currCol = sheet.getActiveColumnIndex();
							}else{
								currRow = targetRow;
								currCol = targetCol;
							}
						}
					}
					var spans = Design.getSelectionSpans(sheet , currRow , currCol);
					if(spans 
							&& typeof spans.length != "undefined"
							&& spans.length > 0){
						currRow = spans[0].row;
						currCol = spans[0].col;
					}
					switch(templateType){
						case "01":
							moduleNodeDrop(currRow , currCol , draggingNode , sheet);
							break;
						case "02":
							indexNodeDrop(currRow , currCol , draggingNode , sheet);
							break;
						case "03":
							if(draggingTreeId == "tree1"){
								moduleNodeDrop(currRow , currCol , draggingNode , sheet);
							}else{
								indexNodeDrop(currRow , currCol , draggingNode , sheet);
							}
							break;
					}						
 				}
 			}
 		});
	}
	
	// 数据集明细类，拖拽drop处理
	function moduleNodeDrop(row , col , treeNode , sheet){
		if(row != null
				&& col != null
				&& treeNode != null
				&& typeof treeNode != "undefined"){
			var dsNode = null;
			colFolderNode = treeNode.getParentNode();
			if(colFolderNode){	
				dsNode = colFolderNode.getParentNode();
			}
			if(dsNode == null){
				return ;
			}
			var currSelDsId
				  ,currSelDsNm;
			for(var seq in Design.rptIdxs){
				if(Design.rptIdxs[seq].cellType == "02"){
					currSelDsId = Design.rptIdxs[seq].dsId;
					currSelDsNm = Design.rptIdxs[seq].dsName;
				}
			}
			if(currSelDsId != null
					&& typeof currSelDsId != "undefined"){
				if(currSelDsId != dsNode.params.realId){
					// 目前数据集明细报表只支持单数据集
					BIONE.tip("只能选择数据模型[<font color='red'>"+currSelDsNm+"</font>]字段");
					return ;
				}
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("02"); // 数据模型报表指标
				if(rptIdx != null){				
					rptIdx.dsId = dsNode.params.realId;
					rptIdx.dsName = dsNode.text;
					rptIdx.columnId = treeNode.params.realId;
					rptIdx.columnName = treeNode.text;
					rptIdx.cellNm = Utils.initAreaPosiLabel(row , col);
				}
				// 先判断当前target是否已经是报表指标，判断逻辑是，Design.rptIdxs[cell.style.seq]是否有记录
				// 若已是报表指标，删除旧指标，维护新指标信息 ； else  直接维护报表指标信息
				// [warning]: 报表指标被记录在Design.rptIdxs数组中，key是客户端内存中的一个随机生成唯一标识；
				//    cell与报表指标关系，全部通过唯一序列值(seq)被定义在cell的styleproperties中的自定义seq属性上。
				//    这么一来，可以最大限度的把报表指标和行、列松耦合，页面操作会简单很多。
				var currCell = sheet.getCell(row , col);
				if(currCell.tag() != null
						&& Design.rptIdxs[currCell.tag()] != null
						&& typeof Design.rptIdxs[currCell.tag()] != "undefined"){
					delete Design.rptIdxs[currCell.tag()];
				}
				var seq = Uuid.v1();
				Design.rptIdxs[seq] = rptIdx;
				currCell.formula("");
				currCell.value(rptIdx.cellLabel());
				currCell.tag(seq);
			}
		}
	}
	
	// 指标单元格类，拖拽drop处理
	function indexNodeDrop(row , col , treeNode , sheet){
		if(row != null
				&& col != null
				&& treeNode != null
				&& typeof treeNode != "undefined"){
			var dsNode = treeNode;
			var parentNode = null;
			if(dsNode.params.nodeType == "measureInfo"){	
				parentNode = dsNode.getParentNode();
			} 
			if(dsNode == null){
				return ;
			}
			var currSelDsId
				  ,currSelDsNm;
			for(var seq in Design.rptIdxs){
				if(Design.rptIdxs[seq].cellType == "02"){
					currSelDsId = Design.rptIdxs[seq].dsId;
					currSelDsNm = Design.rptIdxs[seq].dsName;
				}
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("03"); // 指标类型报表指标
				if(rptIdx != null){
					rptIdx.indexNo = dsNode.params.indexNo;
					rptIdx.indexVerId = dsNode.params.indexVerId;
					rptIdx.isSum = dsNode.params.isSum;
					if(parentNode){
						rptIdx.indexNm = parentNode.text + "." +dsNode.text;
						rptIdx.measureNo = dsNode.id;
						rptIdx.allDims = parentNode.params.dimNos;
						rptIdx.factDims = parentNode.params.dimNos;
					}else{
						rptIdx.indexNm = dsNode.text;
						rptIdx.allDims = dsNode.params.dimNos;
						rptIdx.factDims = dsNode.params.dimNos;
					}
					
				}
				// 先判断当前target是否已经是报表指标，判断逻辑是，Design.rptIdxs[cell.style.seq]是否有记录
				// 若已是报表指标，删除旧指标，维护新指标信息 ； else  直接维护报表指标信息
				// [warning]: 报表指标被记录在Design.rptIdxs数组中，key是客户端内存中的一个随机生成唯一标识；
				//    cell与报表指标关系，全部通过唯一序列值(seq)被定义在cell的styleproperties中的自定义seq属性上。
				//    这么一来，可以最大限度的把报表指标和行、列松耦合，页面操作会简单很多。
				var currCell = sheet.getCell(row , col);
				var realIndexNo = Uuid.v1().replace(/-/g, '');
				if(currCell.tag() != null
						&& Design.rptIdxs[currCell.tag()] != null
						&& typeof Design.rptIdxs[currCell.tag()] != "undefined"){
					var oldIndexNo = Design.rptIdxs[currCell.tag()].realIndexNo;
					if(oldIndexNo != ""
							&& typeof oldIndexNo != "undefined"){
						realIndexNo = oldIndexNo;
					}
					delete Design.rptIdxs[currCell.tag()];
				}
				rptIdx.realIndexNo = realIndexNo;
				var seq = Uuid.v1();
				Design.rptIdxs[seq] = rptIdx;
				currCell.formula("");
				currCell.value(rptIdx.cellLabel());
				currCell.tag(seq);
			}
		}
	}
	
	// 初始化右侧单元格信息form
	function initCellForm(){
		// 初始化ko数据绑定
		if(ko  != null
				&& typeof ko == "object"
				&& RptIdxInfo != null
				&& typeof RptIdxInfo != "undefined"){			
			selectionIdx = RptIdxInfo.newInstanceKO();
			ko.applyBindings(selectionIdx , $("#rightCellForm")[0]);
		}
	}
	
	//spread移入单元格事件扩展
	function spreadEnterCell(row , col){
		var cellTmp = spread.getActiveSheet().getCell(row , col);
		var isFormal = (cellTmp.formula() != null && cellTmp.formula() != "") ? true : false;
		if(RptIdxInfo.initIdxKO
				&& typeof RptIdxInfo.initIdxKO == "function"){
			var seq = cellTmp.tag();
			if(seq != null
					&& Design.rptIdxs[seq] != null
					&& typeof Design.rptIdxs[seq] != "undefined"){
				// 若是报表指标
				var rptIdxTmp = Design.rptIdxs[seq];
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				selectionIdx.seq(seq);
				if(selectionIdx.cellType() == "04"){
					// excel公式单元格
					selectionIdx.excelFormula(cellTmp.formula());
				}
				// 维护单元格编号
				if(Utils){					
					$("#rightCellForm [name='cellNo']").val(Utils.initAreaPosiLabel(row , col));
					if(selectionIdx.cellNm() == ""){
						selectionIdx.cellNm(Utils.initAreaPosiLabel(row , col));
					}
				}
			}else if(isFormal === true){
				// 将该单元格定义成为公式报表指标
				var uuid = Design.Uuid.v1();
				cellTmp.tag(uuid);
				var rptIdxInfoTmp = Design.RptIdxInfo.newInstance("04");
				rptIdxInfoTmp.excelFormula = cellTmp.formula();
				rptIdxInfoTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
				Design.rptIdxs[uuid] = rptIdxInfoTmp;
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxInfoTmp);
				selectionIdx.seq(seq);
				selectionIdx.excelFormula(cellTmp.formula());
				// 维护单元格编号
				if(Utils){					
					$("#rightCellForm [name='cellNo']").val(Utils.initAreaPosiLabel(row , col));
					if(selectionIdx.cellNm() == ""){
						selectionIdx.cellNm(Utils.initAreaPosiLabel(row , col));
					}
				}
			}else{
				RptIdxInfo.initIdxKO(selectionIdx);
			}
		}
	}
	
	// spread选择区域发生变化事件扩展
	function spreadSelectionChanged(sender , args){
		var currSheet = Design.spread.getActiveSheet();
		var selections = currSheet.getSelections();
		if(selections.length == 1){
			var currSelection = selections[0];
			if(currSelection.colCount == 1
					&& currSelection.rowCount == 1){
				// 只有一个单元格，不处理（一个单元格的处理交由enterCell事件）
				spreadEnterCell(currSelection.row , currSelection.col);
				return ;
			} else {
				var spans = Design.getSelectionSpans(currSheet);
				if(spans
						&& typeof spans.length != "undefined"
						&& spans.length == 1
						&& spans[0].rowCount == currSelection.rowCount
						&& spans[0].colCount == currSelection.colCount){
					// 选中了多行多列，但是一个合并的单元格
					spreadEnterCell(currSelection.row , currSelection.col);
					return ;
				}
			}
		}
		var batchObj = null;
		var seqs = "";
		sloop:for(var i = 0 , j = selections.length ; i < j ; i++){
			var selectTmp = selections[i];
			rloop:for(var r = 0, rl = selectTmp.rowCount ; r < rl ; r++){
				cloop:for(var c = 0 , cl = selectTmp.colCount ; c < cl ; c++){
					var currCell = currSheet.getCell(selectTmp.row + r , selectTmp.col + c);
					var seqTmp = currCell.tag();
					if(seqTmp == null
							|| typeof seqTmp == "undefined"){
						var isFormula = (currCell.formula() != null && currCell.formula() != "") ? true : false;
						if(isFormula === true){							
							var uuid = Design.Uuid.v1();
							currCell.tag(uuid);
							var rptIdxInfoTmp = Design.RptIdxInfo.newInstance("04");
							rptIdxInfoTmp.excelFormula = currCell.formula();
							rptIdxInfoTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
							Design.rptIdxs[uuid] = rptIdxInfoTmp;
							seqTmp = uuid;
						}else{
							// 包含非报表指标 , 不处理
							continue ;
						}
					}
					if(Design
							&& Design.rptIdxs){
						var selRptIdx = Design.rptIdxs[seqTmp];
						if(selRptIdx == null
								|| typeof selRptIdx == "undefined"){
							// 包含非报表指标，不处理
							continue ;
						}
						// 记录seq
						if("" != seqs){
							seqs += ",";
						}
						seqs += seqTmp;
						if(batchObj == null){
							batchObj = {};
							for(var attr in RptIdxInfo._batchAttrs){
								if(attr in selRptIdx){									
									batchObj[attr] = selRptIdx[attr];
								}
							}
						}else{
							for(var attr in RptIdxInfo._batchAttrs){
								if(selRptIdx[attr] != batchObj[attr]){
									// 当选中的值不一致时,使用特定的值显示(基本都是显示空)
									batchObj[attr] = RptIdxInfo._batchAttrs[attr];
								}
							}
						}
					}
				}
			}
		}
		if(batchObj != null){			
			batchObj.cellType = "-1";
			batchObj.cellTypeNm = "批量处理";
			batchObj.seq = seqs;
			RptIdxInfo.initIdxKO(selectionIdx , batchObj);
		}
	}
	
	// spread移出单元格事件扩展
	function spreadLeaveCell(sender , args){
		syncSelIdxs();
	}
	
	// 双击单元格事件
	function spreadDbclkCell(sender , args){
		if(args){
			var dbClkUrl="";
			if(args.sheetArea
					&& args.sheetArea != 3){
				// 只有选中单元格区域才进行后续动作
				return ;
			}
			var currSheet = spread.getActiveSheet();
			var selRow = currSheet.getActiveRowIndex();
			var selCol = currSheet.getActiveColumnIndex();
			var currCell = spread.getActiveSheet().getCell(selRow , selCol);
			var seq = currCell.tag();
			var isFormal = currCell.formula() == null ? false : true;
			if(isFormal === true){
				return ;
			}
			if(selectionIdx.dbClkUrl() == null
					|| selectionIdx.dbClkUrl() == ""){
				return ;
			}
			
			if(selectionIdx.cellType() == "03"
					&& (selectionIdx.indexNo() == null
						|| selectionIdx.indexNo() == "")){
				// 指标单元格,来源指标为空(空指标)
				return;	
			}
			else if(selectionIdx.cellType() == "01"){
				return;
			}
			else if(selectionIdx.cellType() == "02"){
				dbClkUrl="${ctx}/frs/integratedquery/rptmesquery/info/module?d="+new Date().getTime();
			}
			else if(selectionIdx.cellType() == "03"){
				dbClkUrl="${ctx}/report/frame/design/cfg/dbclk/module/idx?indexNo="+selectionIdx.indexNo()+"&isDict=1&d"+new Date().getTime();
			}
			else if(selectionIdx.cellType() == "04"){
				return;
			}
			else if(selectionIdx.cellType() == "05"){
				return
			}
			else if(selectionIdx.cellType() == "06"){
				return
			}
			else{
				return;
			}
			if(seq != null
					&& typeof seq != "undefined"
					&& Design.rptIdxs[seq] != null
					&& typeof Design.rptIdxs[seq] != "undefined"
					&& selectionIdx != null
					&& typeof selectionIdx == "object"){
				var rptIdxTmp = Design.rptIdxs[seq];
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				selectionIdx.seq(seq);
				
				if(selectionIdx.cellType() == "04"){
					// excel公式单元格
					selectionIdx.excelFormula(currCell.formula());
					return ;
				}
				// 处理报表指标的双击事件，若是excel公式，不处理
				var labelTmp = Utils.initAreaPosiLabel(selRow , selCol);
				if(Utils){		
					$("#rightCellForm [name='cellNo']").val(labelTmp);
					if(selectionIdx.cellNm() == ""){
						selectionIdx.cellNm(labelTmp);
					}
				}
				var width = selectionIdx.dialogWidth();
				var height = selectionIdx.dialogHeight();
				window.parent.parent.parent.selectionIdx=selectionIdx;
				window.parent.parent.parent.tab=window;
				window.parent.parent.parent.BIONE.commonOpenDialog(selectionIdx.cellTypeNm()+':['+labelTmp+"]",
						'moduleClkDialog',
						$(parent.parent.parent.document).width(),
						$(parent.parent.parent.document).height(),
								dbClkUrl);
			}
		}		
	}
	
	// 单元格复制事件，支持跨业务条线复制扩展
	function clipboardChanging(){
		window.parent.clipboardLineId = lineId;
	}
	
	// 单元格粘贴事件，支持跨业务条线复制扩展
	function clipboardPasted(){
		if(window.parent.clipboardLineId != null
				&& typeof window.parent.clipboardLineId != "undefined"
				&& window.parent.clipboardLineId != lineId){
			var tabId = window.parent.clipboardLineId;
			if(tabId == ""){
				tabId = "_mainTab";
			}
			var frameTmp = window.parent.document.getElementById(tabId).contentWindow;
			if(frameTmp.Design._clipBoard){
				jQuery.extend(true , Design._clipBoard , frameTmp.Design._clipBoard);
				if(Design._clipBoard.helper){
					// 跨表复制粘贴目前不支持剪切
					Design._clipBoard.helper.isCutting = false;
				}
				Design._clipBoard.rptIdxsBase = frameTmp.Design.rptIdxs;
			}
		}else{
			Design._clipBoard.rptIdxsBase = null;
		}
	}
	
	// 同步选中节点信息到idxs缓存
	function syncSelIdxs(){
		if(selectionIdx == null
				|| typeof selectionIdx == "undefined"
				|| typeof selectionIdx.seq == "undefined"){
			return ;
		}
		var cellType = selectionIdx.cellType();
		var seq = selectionIdx.seq();
		if(seq != null
				&& typeof seq != "undefined"
				&& cellType != null
				&& typeof cellType != "undefined"
				&& cellType != "01"){
			if(cellType == "-1"){
				// 若之前是批量操作
				var batchObjTmp = {};
				for(var attr in RptIdxInfo._batchAttrs){
					batchObjTmp[attr] = selectionIdx[attr]();
				}
				var seqs = seq.split(",");
				for(var attr in RptIdxInfo._batchAttrs){
					if(selectionIdx[attr]() == RptIdxInfo._batchAttrs[attr]){
						continue;
					}
					for(var i = 0 , l = seqs.length ; i < l ; i++){
						var rptIdxTmp = Design.rptIdxs[seqs[i]];
						if(rptIdxTmp && attr in rptIdxTmp){
							rptIdxTmp[attr] = selectionIdx[attr]();
						}
					}
				}
			}else{				
				//针对报表指标格，进行数据修改
				var rptIdx = Design.rptIdxs[seq];
				if(rptIdx){
					for(var i in rptIdx){
						if(typeof rptIdx[i] == "function"){
							continue;
						}
						if(i in selectionIdx){
							rptIdx[i] = selectionIdx[i]();
						}
					}
				}
			}
		}
	}
	
	// 上传表样回调函数
	function initFromJson(){
		var frameWindow = window.parent.document.getElementById("_mainTab").contentWindow;
		if(frameWindow){
			if(frameWindow.Design
					&& frameWindow.Design.rptIdxsSize() > 0){
				// 获取全部cell，挨个分析是否有seq属性
				var currSheet = frameWindow.spread.getActiveSheet();
				var rowCount = currSheet.getRowCount();
				var colCount = currSheet.getColumnCount();
				var cellOldVals = [];
				for(var r = 0 ; r < rowCount ; r++){
					for(var c = 0 ; c < colCount ; c++){
						var cellTmp = currSheet.getCell( r , c);
						var seqTmp = cellTmp.tag();
						if(seqTmp != null
								&& seqTmp != ""
								&& typeof seqTmp != "undefined"
								&& frameWindow.Design.rptIdxs[seqTmp] != null
								&& typeof frameWindow.Design.rptIdxs[seqTmp] == "object"){
							if(frameWindow.Design.rptIdxs[seqTmp].cellType == "04"){
								continue;
							}
							var keyTmp = cellTmp.row + "," + cellTmp.col;
							cellOldVals.push(keyTmp);
						}
					}
				}
				var jsonTmp = frameWindow.spread.toJSON();
				var jsonMin;
				if(Utils
						&& typeof Utils.jsonMin == "function"){
					jsonMin = Utils.jsonMin(jsonTmp,cellOldVals);
				}else{
					jsonMin = jsonTmp;
				}
				var jsonStr = JSON2.stringify(jsonMin);
				return jsonStr;
				//Design.fromJSON(jsonStr);
			}
		}
		//RptIdxInfo.initIdxKO(selectionIdx);
		return "";
	}
	
	function cssLoadHandler(jsonObj){
		if(jsonObj != null
				&& typeof jsonObj != "undefined"){
			var objResult = JSON2.parse(jsonObj);
			RptIdxInfo.initIdxKO(selectionIdx);
			Design.fromJSON(objResult.json);
			window.parent.syncCssJson(objResult.json);		
			if(objResult.formula){
				for(var posi in objResult.formula){
					var formulaTmp = objResult.formula[posi];
					if(formulaTmp == null
							|| formulaTmp == ""){
						continue;
					}
					var posis = posi.split(",");
					if(posis.length == 2){
						var rowTmp = posis[0];
						var colTmp = posis[1];
						if(rowTmp == null
								|| rowTmp == ""
								|| colTmp == null
								|| colTmp == ""){
							continue;
						}
						var cellTmp = Design.spread.getActiveSheet().getCell(rowTmp , colTmp);
						var seqTmp = Uuid.v1();
						var currLabel = Utils.initAreaPosiLabel(rowTmp , colTmp);
						cellTmp.tag(seqTmp);
						var rptIdxTmp = RptIdxInfo.newInstance("04");
						rptIdxTmp.seq = seqTmp;
						rptIdxTmp.cellNm = currLabel;
						rptIdxTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
						rptIdxTmp.excelFormula = formulaTmp;
						Design.rptIdxs[seqTmp] = rptIdxTmp; 
					}
				}
			}
		}
	}
	
	function getSelectionLabel(){
		var selObj = {};
		var currSheet = Design.spread.getActiveSheet();
		selObj.row = currSheet.getActiveRowIndex();
		selObj.col = currSheet.getActiveColumnIndex();
		selObj.label = Utils.initAreaPosiLabel(currSheet.getActiveRowIndex() , currSheet.getActiveColumnIndex());
		return selObj;
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="designLayout" style="width : 99%;">
            <div id="centerDiv" position="center" >
            	<div id="spread" style="width:99.5%;border-bottom:1px solid #D0D0D0;"></div>
            </div>
           <div id="rightDiv" position="right" title="单元格信息">
            	<form id="rightCellForm" action=""  class="l-form">
            	<table cellpadding="0" cellspacing="0" class="l-table-edit" >
            		<tr>
                		<td align="right" class="l-table-edit-td ">单元格类型:</td>
                		<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="cellTypeNm" data-bind="value: cellTypeNm" type="text" id="cellTypeNm" ltype="text"  readonly=true /></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='01'  || cellType() == '-1'? 'none' : ''}">
                		<td align="right" class="l-table-edit-td ">单元格编号:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="cellNo"  type="text" id="cellNo" ltype="text"  readonly=true /></td>
               			 <td align="left"></td>
            		</tr>
            		<!-- <tr data-bind="style:{ display : cellType()=='01' ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">单元格名称:</td>
                			<td align="left" class="l-table-edit-td"><input onchange="cellNmChangeHandler()" class="own-formitem-style" name="cellNm" data-bind="value:cellNm,valueUpdate:'afterkeydown'" type="text" id="cellNm" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr> -->
            		<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">数据模型:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="dsName"  data-bind="value:dsName,valueUpdate:'afterkeydown'"  type="text" id="dsName" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">字段名称:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="columnName"  data-bind="value:columnName,valueUpdate:'afterkeydown'"  type="text" id="columnName" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='03' || cellType() == '07') && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">来源指标:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="indexNm"  data-bind="value:indexNm,valueUpdate:'afterkeydown'"  type="text" id="indexNm" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<!-- <tr data-bind="style:{ display : cellType()=='03' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">是否减维:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="isSubDim"  data-bind="value:allDims() == factDims()? '否' : '是'"  type="text" id="isSubDim" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr> 
            		<tr data-bind="style:{ display : (cellType()=='03'  || cellType() == '07') && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">是否过滤:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="isFiltDim"  data-bind="value:filtInfos() == null  || (typeof filtInfos() == 'string' && filtInfos().length <= 2) || (typeof filtInfos() == 'object' && filtInfos().length <= 0)?  '否' : '是'"  type="text" id="isFiltDim" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr> -->
            		<tr data-bind="style:{ display : cellType()=='04' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">公式内容:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea  data-bind="value:excelFormula,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" readonly=true></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='04'  && templateType != '01' && templateType != '04') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否报表指标:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isRptIndex" id="isRptIndex" ltype="select" data-bind="value:isRptIndex" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='04' && templateType != '04' ) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">分析扩展:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isAnalyseExt" id="isAnalyseExt" ltype="select" data-bind="value:isAnalyseExt" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='05' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">表间计算:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea  data-bind="value:formulaDesc,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" readonly=true></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<!-- <tr data-bind="style:{ display : cellType()=='01' || cellType()=='06' ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">数据类型:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dataType" id="dataType" ltype="select"  data-bind="value:dataType">
								<option value="01">数值</option>
								<option value="02">字符</option>
							</select>
                		</td>
            		</tr> -->
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06'  || cellType()=='08') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">显示格式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="displayFormat" id="displayFormat" ltype="select" data-bind="value:displayFormat" >
								<option value="01" label="金额" />
								<option value="03" label="数值" />
								<option value="02" label="比例" />
								<option value="-1" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()!='01'|| cellType()=='08') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">数据单位:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dataUnit" id="dataUnit" ltype="select" data-bind="value:dataUnit" >
                				<option value="">使用模板单位</option>
								<option value="01"  label="元" />
								<option value="02"  label="百元" />
								<option value="03"  label="千元" />
								<option value="04"  label="万元" />
								<option value="05"  label="亿元" />
								<option value="00">无单位</option>
								<option value="-1" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()!='03'|| cellType()=='08') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">数据单位:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dataUnit" id="dataUnit" ltype="select" data-bind="value:dataUnit" >
                				<option value="">使用模板单位</option>
								<option value="01"  label="个" />
								<option value="02"  label="百" />
								<option value="03"  label="千" />
								<option value="04"  label="万" />
								<option value="05"  label="亿" />
								<option value="00">无单位</option>
								<option value="-1" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<!-- 
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' )? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">数据长度:</td>
                			<td align="left" class="l-table-edit-td"><input class="own-formitem-style" name="dataLen" data-bind="value:dataLen,valueUpdate:'afterkeydown'" type="text" id="dataLen" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()=='02'|| cellType()=='08') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">显示精度:</td>
                			<td align="left" class="l-table-edit-td"><input class="own-formitem-style" name="dataPrecision"  data-bind="value:dataPrecision,valueUpdate:'afterkeydown'"  type="text" id="dataPrecision" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
            		-->
            		<tr data-bind="style:{ display : (cellType()=='08' && templateType == '04'  && dimType() == '01') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">日期格式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dateFormat" id="dateFormat" ltype="select" data-bind="value:dateFormat" >
								<option value="01">年月日</option>
								<option value="02">年月</option>
								<option value="03">月</option>
								<option value="04">日</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='08' && templateType == '04' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否合计:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isTotal" id="isTotal" ltype="select" data-bind="value:isTotal" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='08' && templateType == '04' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">转码显示:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isConver" id="isConver" ltype="select" data-bind="value:isConver" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<!-- 目前报表系统该字段暂注释掉 -->
            		<!-- 
            		<tr data-bind="style:{ display : (cellType()=='02' ) || (cellType() == '03' && templateType != '03') || (cellType()=='05' )? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否可修改:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isUpt" id="isUpt" ltype="select" data-bind="value:isUpt" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='02' ) || (cellType() == '03' && templateType != '03') || (cellType()=='05' )? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否可空:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isNull" id="isNull" ltype="select" data-bind="value:isNull" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		 -->
            		<tr data-bind="style:{ display : (cellType()=='07' || cellType()=='03') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">计算规则:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="ruleId" id="ruleId" ltype="select" data-bind="value:ruleId" >
								<c:forEach items="${calcRules}" var="calcRule">
									<option value="${calcRule['ruleId']}">${calcRule['ruleNm']}</option>
								</c:forEach>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display :(cellType()=='07' || cellType()=='03') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">时间度量:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="timeMeasureId" id="timeMeasureId" ltype="select" data-bind="value:timeMeasureId" >
								<c:forEach items="${timeMeasures}" var="timeMeasure">
									<option value="${timeMeasure['timeMeasureId']}">${timeMeasure['measureNm']}</option>
								</c:forEach>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='07' || cellType()=='03') ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">取值方式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="modeId" id="modeId" ltype="select" data-bind="value:modeId" >
								<c:forEach items="${valTypes}" var="valType">
									<option value="${valType['modeId']}">${valType['modeNm']}</option>
								</c:forEach>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : templateType=='04' && (cellType()=='07' || cellType()=='04')? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">排序方式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="sortMode" id="sortMode" ltype="select" data-bind="value:sortMode" >
								<option value="01">不排序</option>
								<option value="02">正序</option>
								<option value="03">倒序</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='01' || cellType()=='-1' || cellType()=='08' ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">口径说明:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea data-bind="value:caliberExplain,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;"></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            	</table>
            	</form>
            </div>  
        </div>
	</div>
</body>
</html>