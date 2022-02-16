<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="decorator" content="/template/template14.jsp" />
<%@ include file="/common/spreadjs_load.jsp"%>
<%@ include file="../edit/template-content.jsp"%>
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
	
	var flag = true;
	
	var autoColumnWidth =true;
	
	var rptNum =  window.parent.rptNum;
	
	var frameDocument = window.parent.parent;
	
	var templateId = frameDocument.templateId;
	
	// 设计器相关对象 - begin
	var Design,
		   spread,
		   RptIdxInfo,
		   Utils,
		   Uuid,jsonTmp;
	// 设计器相关对象 - end
	var templateType = frameDocument.templateType; // 01 - 明细类；02 - 单元格类
	var busiType = frameDocument.document.getElementById("tabBase").contentWindow.$.ligerui.get("serviceTypeCombo").getValue();//业务类型，根据类型不同，选择不同的指标
	var isRptIdxCfg = frameDocument.isRptIdxCfg;//是否报表指标配置
	var titles = [];
	var moduleTreeObj;
	var indexTreeObj;
	var treeUrls = [];
	treeUrls["01"] = "${ctx}/report/frame/design/cfg/getModuleTree";
	treeUrls["02"] = "${ctx}/report/frame/idx/getAsyncTreeIdxShow.json?isShowIdx=1&isShowMeasure=1&showEmptyFolder=0&busiType="+busiType;
	titles["01"] = "数据模型";
	titles["02"] = "指标";
	var layout;
	
	var catalogType = "01";
	var moduleType = "02";
	var colCatalogType = "03";
	var colType = "04";
	
	var cellNmMap = {};//单元格名称和编号map
	
	var cellNoMap = {};//单元格编号和名称map
	
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
	
	// 已选中的报表指标单元格(工具栏用)
	var selectionToolBar = {
		font : ko.observable(''),
		fontSize : ko.observable(10),
		bold : ko.observable(''),
		italic : ko.observable(''),
		textDecoration : ko.observable('')
	};
	
	//初始化报表指标对象
	var initRptIdxs = [];
	
	//报表指标口径变化集合
	var rptIdxchange = {};
	
	$(function() {
		// 布局
		initLayout();
		// 左侧的树
		initTree();
		//搜索树
		initTreeSearch()
		// 设计器
		initDesign();
		//使用提醒
		initRemind();
	});
	
	// 初始化布局
	function initLayout(){
		// 初始化title
		var leftTitle =         
			'<div width="8%"                                                                                                             '+
			'	style="float: left; position: relative; height: 20p; margin-top: 7px">    '+
			'		<img src="${ctx }/images/classics/icons/application_side_tree.png" />                       '+
			'</div>                                                                                                                             '+
			'<div width="90%">                                                                                                         '+
			'	<span                                                                                                                             '+
			'		style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px"> '+
			'		<span style="font-size: 12">工具栏</span>                                     '+
			'	</span>                                                                                                                         '+
			'</div>                                                                                                                             ';
		$("#leftDiv").attr("title",leftTitle);
		// 初始化ligerlayout
		var centerWidth = $("#center").width();
		layout = $("#designLayout").ligerLayout({ 
			height : $("#center").height(),
			leftWidth : (centerWidth - 220) * 0.21,
			centerWidth : (centerWidth - 220) * 0.79,
			rightWidth : 220,
			allowLeftResize : true ,
			allowRightResize : true ,
			isLeftCollapse: true,
			onEndResize : function(){
				if(Design){
					Design.resize(Design.spread);
				}
			}
		});
		//初始化accordion
		var treeContainerHeight = $("#centerDiv").height() - 25 - 25 - 2;
		if(templateType == "03"){
			// 若是综合类报表
			// 多减去一个accordion item高度
			treeContainerHeight -= 27;
			// 即有数据模型，也有指标
			$("#modelTree").show();
			$($(".treeContainer")[1]).attr("title" , titles["01"]);
			$($(".treeContainer")[0]).attr("title" , titles["02"]);
		}else{
			$(".treeContainer").attr("title" , titles[templateType]);
		}
		$("#treeAccordion").ligerAccordion({
			height:$("#centerDiv").height() - 25 - 2
		});
		$("#remarkDiv").height(100);
		$("#remarkDiv").children(".l-textarea").height(70);
		$(".treeContainer").height(treeContainerHeight);
		
		// 初始化报表备注相关样式
		initRemarkCss();
		
		// 对resize的相关处理
		resizeHandler();
		
		$(".l-layout-right").css("overflow", "auto");
	}
	
	function initTreeSearch(){
		$("#treeSearchIcon").live('click',function(){// 树搜索
			flag = false;
			initTree($('#treeSearchInput').val());
		});
		$('#treeSearchInput').bind('keydown', function(event) {
			if (event.keyCode == 13) {
				flag = false;
				initTree($('#treeSearchInput').val());
			}
		});
	}
	// 初始化备注区域样式
	function initRemarkCss(){
		// 设置缺省备注提示
		var nullText = "描述一下这张报表吧 ...";
		var remarkDivDoom = $("#remarkDiv").children(".l-textarea");
		remarkDivDoom.addClass("l-text-field-null").val(nullText).attr("isNull","true");
		remarkDivDoom.bind("focusin" , function(){
			if(remarkDivDoom.attr("isNull") == "true"){
				remarkDivDoom.removeClass("l-text-field-null").val("");
			}
		});
		remarkDivDoom.bind("focusout" , function(){
			if(remarkDivDoom.val() != null
					&& remarkDivDoom.val() != ""){
				remarkDivDoom.attr("isNull","false");
			}else{
				remarkDivDoom.addClass("l-text-field-null").val(nullText).attr("isNull","true");
			}
		});
		// 报表备注 隐藏/展现按钮事件/样式
		$("#remarkToggle").css("background","url('${ctx}/css/classics/ligerUI/Gray/images/layout/togglebar.gif')");
		$("#remarkToggle").css("background-position","0px -40px");
		$("#remarkToggle").bind("mouseenter" , function(){
			// 鼠标移入
			var type = $("#remarkToggle").attr("currType");
			if(type == "show"){
				// 展现时
				$("#remarkToggle").css("background-position","0px -60px");
			} else {
				// 隐藏时
				$("#remarkToggle").css("background-position","0px -20px");
			}
		})
		$("#remarkToggle").bind("mouseleave" , function(){
			// 鼠标移出
			var type = $("#remarkToggle").attr("currType");
			if(type == "show"){
				// 展现时
				$("#remarkToggle").css("background-position","0px -40px");
			} else {
				// 隐藏时
				$("#remarkToggle").css("background-position","0px 0px");
			}
		})
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
	function initTree(searchNm){
		if(templateType == "01"){
			$("#idxTree").hide();
			$("#modelTree").show();
			moduleTreeHandler();
			// 初始化节点
			initNodes();
			$($(".l-accordion-header")[0]).hide();
		}else if(templateType == "02"){
			indexTreeHandler(null,searchNm);
			$($(".l-accordion-header")[1]).hide();
		}else if(templateType == "03"){
			if(flag){
			$("#modelTree").show();
			// 初始化数据模型树
			moduleTreeHandler($($(".treeContainer")[1]).children("ul"));
			}
			// 初始化节点
			initNodes("01");
			// 初始化指标树
			indexTreeHandler($($(".treeContainer")[0]).children("ul"),searchNm);
		}
	}
	
	function moduleTreeHandler(target){
		target = target ? target : $($(".treeContainer")[1]).children("ul");
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
	
	function indexTreeHandler(target,searchNm){
		target = target ? target : $($(".treeContainer")[0]).children("ul");
		//edit by fangjuan 2014-09-04
		var async ={
				async:{
					enable:true,
					url:treeUrls["02"],
					autoParam:["nodeType", "id", "indexVerId"],   //将该三个参数提交,post方式
					otherParam:{"searchNm":searchNm},
					dataType:"json",
					dataFilter:function(treeId,parentNode,childNodes){
						if(childNodes){
							for(var i = 0;i<childNodes.length;i++){
								childNodes[i].nodeType = childNodes[i].params.nodeType;
								childNodes[i].indexVerId = childNodes[i].params.indexVerId;
								childNodes[i].defSrc = childNodes[i].params.defSrc ? childNodes[i].params.defSrc : "";
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
				BIONE.showError("树加载失败，请联系系统管理员");
			}
		});
	}
	
	// 初始化设计器
	function initDesign(){
		var isShowToolbar = true;
		var isRptIdxCfg_sign = false;
		if("Y" == isRptIdxCfg){
			isShowToolbar = false;
			isRptIdxCfg_sign = true;
		}
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
			var initFromAjax = true;
			var ajaxData = {
					templateId : templateId,
					verId : frameDocument.verId,
					getChildTmps : false
			};
			// 修改时
			if((jsonStr == null
					|| jsonStr == ""
					|| typeof jsonStr == "undefined")
					&& lineId != ""){
				jsonStr = initFromJson();
			}
			if(jsonStr){
				initFromAjax = false;
			}
			// 初始化设计器
			var settings = {
					targetHeight : ($("#centerDiv").height() - $("#remarkDiv").height() - 2 - 2) ,
					templateType : templateType,
					ctx : "${ctx}" ,
					readOnly : false ,
					showBusiType : true , // 可以显示业务标识（人行标识）
					initFromAjax : initFromAjax,//是否获取报表配置数据
					ajaxData : ajaxData,
					onCssImport : css_import ,
					onLeaveCell : spreadLeaveCell,
					onCellDoubleClick : spreadDbclkCell,
					onSelectionChanged : spreadSelectionChanged,
					onClipboardChanging : clipboardChanging,
					onClipboardPasted : clipboardPasted,
					cellDetail : true,
					toolbar : isShowToolbar,//是否显示工具栏
					isRptIdxCfg : isRptIdxCfg_sign,//是否报表指标配置功能
					isBusiLine : lineId == "" ? false : true , 
					// 报表指标数据初始化
					moduleCells : objTmp==null ? null : objTmp.moduleCells,
					formulaCells : objTmp==null ? null : objTmp.formulaCells,
					idxCells : objTmp==null ? null : objTmp.idxCells,
					staticCells : objTmp==null ? null : objTmp.staticCells,
					idxCalcCells : objTmp==null ? null : objTmp.idxCalcCells,
					colIdxCells : objTmp == null ? null : objTmp.colIdxCells,
					colDimCells : objTmp == null ? null : objTmp.colDimCells,
					rowCols : objTmp==null? null : objTmp.batchCfgs,
					comCells : objTmp == null ? null : objTmp.comCells,
					initJson : jsonStr
			};
			Design = ds;
			spread = ds.init($("#spread") , settings);
			Uuid = ds.Uuid;
			RptIdxInfo = ds.RptIdxInfo;
			if(objTmp != null){
				initUptDatas(objTmp.tmpInfo.remark);
			}
			// 单元格信息
			initDetailForm();
			if(true === isShowToolbar){
				initToolBar();
			}
			initRptIdxs = strRptIdxs(Design.rptIdxs);
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
					var canvasOffset = $(spread.getHost()).offset();
					var absolutePosX = e.clientX - canvasOffset.left;
					var absolutePosY = e.clientY - canvasOffset.top;
					var currRow = null;
					var currCol = null;
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
							if(draggingTreeId == "tree2"){
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
			if(!colFolderNode){
				return ;
			}
			var currSelDsId ,currSelDsNm;
			//随机找这一行任意一个单元格的模型信息
			for(var seq in Design.rptIdxs){
				if((Design.rptIdxs[seq].cellType == "02") && (Design.rptIdxs[seq].rowId == row)){
					currSelDsId = Design.rptIdxs[seq].dsId;
					currSelDsNm = Design.rptIdxs[seq].dsName;
				}
			}
			if(currSelDsId != null && typeof currSelDsId != "undefined"){
				//这块限制同一行只能拖拽同一模型下的字段
 				if(currSelDsId != colFolderNode.params.id){
					// 目前数据集明细报表只支持单数据集
					BIONE.showError("同一行只能拖拽同一模型下的字段，只能选择数据模型[<font color='red'>"+currSelDsNm+"</font>]字段");
					return ;
				}
			}
			if(RptIdxInfo
					&& typeof RptIdxInfo == "object"){
				var rptIdx = RptIdxInfo.newInstance("02"); // 数据模型报表指标
				if(rptIdx != null){				
					rptIdx.dsId = treeNode.params.setId;
					rptIdx.dsName = colFolderNode.text;
					rptIdx.columnId = treeNode.params.realId;
					rptIdx.columnName = treeNode.text;
					rptIdx.isPk = treeNode.data.isPk;
				}
				// 先判断当前target是否已经是报表指标，判断逻辑是，Design.rptIdxs[cell.style.seq]是否有记录
				// 若已是报表指标，删除旧指标，维护新指标信息 ； else  直接维护报表指标信息
				// [warning]: 报表指标被记录在Design.rptIdxs数组中，key是客户端内存中的一个随机生成唯一标识；
				//    cell与报表指标关系，全部通过唯一序列值(seq)被定义在cell的styleproperties中的自定义seq属性上。
				//    这么一来，可以最大限度的把报表指标和行、列松耦合，页面操作会简单很多。
				
				var oldCellNo = $("#rightCellForm [name='cellNo']").val();
				var oldCellNm = $("#rightCellForm [name='cellNm']").val();
				var oldTmpCellType = $("#rightCellForm [name='cellTypeNm']").val();
				var oldCellType = cellTypeNum(oldTmpCellType);
				
				var currCell = sheet.getCell(row , col);
				var seq = spread.getActiveSheet().getTag(row, col, GC.Spread.Sheets.SheetArea.viewport);
				if(seq != null
						&& Design.rptIdxs[seq] != null
						&& typeof Design.rptIdxs[seq] != "undefined"){
					delete Design.rptIdxs[seq];
				}
				seq = Uuid.v1();
				Design.rptIdxs[seq] = rptIdx;
				currCell.formula("");
				currCell.value(rptIdx.cellLabel(Design.getShowType()));
				sheet.setTag(row, col, seq);
				Design.autoSetColumnWidth(sheet,col);
				rptIdx.rowId = row;
				rptIdx.colId = col;
				rptIdx.cellNo = Utils.initAreaPosiLabel(row , col);
 				if(rptIdx.cellNo == oldCellNo && rptIdx.cellType == oldCellType){
 					rptIdx.cellNm = oldCellNm;  //相同单元格, 指标改变, 保证单元格名称不变
 				}else if(rptIdx.cellNm == ""){
 					rptIdx.cellNm = rptIdx.cellNo;//去除单元格名称修改问题
 				}
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
					rptIdx.isFillSum = dsNode.params.isSum;
					rptIdx.statType = dsNode.params.statType;
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
				var realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
				
				var oldCellNo = $("#rightCellForm [name='cellNo']").val();
				var oldCellNm = $("#rightCellForm [name='cellNm']").val();
				var oldTmpCellType = $("#rightCellForm [name='cellTypeNm']").val();
				var oldCellType = cellTypeNum(oldTmpCellType);

				var seq = spread.getActiveSheet().getTag(row, col, GC.Spread.Sheets.SheetArea.viewport);
				var oldRptIdx = null;
				if(seq != null
						&& Design.rptIdxs[seq] != null
						&& typeof Design.rptIdxs[seq] != "undefined"){
					var oldIndexNo = Design.rptIdxs[seq].realIndexNo;
					oldRptIdx = Design.rptIdxs[seq];
					oldCellNo = oldRptIdx.cellNo;
					oldCellNm = oldRptIdx.cellNm;
					oldCellType = oldRptIdx.cellType;
					if(oldIndexNo != "" && typeof oldIndexNo != "undefined"){
						realIndexNo = oldIndexNo;
					}
					delete Design.rptIdxs[seq];  //删除当前单元格先前的指标
				}
				seq = Uuid.v1();
				Design.rptIdxs[seq] = rptIdx;
				currCell.formula("");
				currCell.value(rptIdx.cellLabel(Design.getShowType()));
				sheet.setTag(row, col, seq);
				rptIdx.cellNo = Utils.initAreaPosiLabel(row , col);  //暂时的单元格名称,保存的时候还会更新
				//指标配置表存在，则使用配置表中的“报表指标编号”
				if(Design.rptIdxCfg[rptIdx.cellNo] && Design.rptIdxCfg[rptIdx.cellNo] != "" && Design.rptIdxCfg[rptIdx.cellNo] != null){
					realIndexNo = Design.rptIdxCfg[rptIdx.cellNo];
				}
				rptIdx.realIndexNo = realIndexNo;
 				if(rptIdx.cellNo == oldCellNo){//相同单元格,指标改变,保证一些属性不会改变
 					if(oldRptIdx){
 						for (var key in oldRptIdx) {  
 							//可以延用的属性，跨单元格类型
 							if(RptIdxInfo.copyAttrs[key]){
 								if(oldRptIdx[key]){
 									rptIdx[key] = oldRptIdx[key];
 								}
 							}
 				        }
 					}
 				}else if(!rptIdx.cellNm){
 					rptIdx.cellNm = rptIdx.cellNo;//去除单元格名称修改问题
 				}
			}
		}
	}
	
	//单元格名称转化
	function cellTypeNum(cellTypeText){
		var cellType = "";
		if(cellTypeText == "一般单元格"){
			cellType = "01";
		}else if(cellTypeText == "数据模型单元格"){
			cellType = "02";
		}else if(cellTypeText == "指标单元格"){
			cellType = "03";
		}else if(cellTypeText == "excel公式"){
			cellType = "04";
		}else if(cellTypeText == "表间计算"){
			cellType = "05";
		}else if(cellTypeText == "表达式"){
			cellType = "06";
		}
		return cellType;
	}
	function initDetailForm(){
		// 初始化单元格明细模板内容
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/report/frame/design/cfg/getFormDetailDatas",
			dataType : 'json',
			type : "post",
			success : function(result){
				if(result != null
						&& typeof result != "undefined"){
					var objTmp = {template : result};
					cellBaseInfo = objTmp;
					$("#rightCellForm").html($("#template-content").tpl(objTmp));
					// 初始化属性部分的数据绑定
					initCellForm();
				}
			}
		});
	}
	
	// 初始化右侧单元格信息form
	function initCellForm(){
		// 初始化ko数据绑定
		if(ko  != null
				&& typeof ko == "object"
				&& RptIdxInfo != null
				&& typeof RptIdxInfo != "undefined"){
			var tmpCellNm = "";
			var tmpCellNo = "";
			var tmpL = rptNum.length + 1;
			var sheet = spread.getActiveSheet();
			var seq = sheet.getTag(sheet.getActiveRowIndex(), sheet.getActiveColumnIndex(), GC.Spread.Sheets.SheetArea.viewport);
			var rptIdxTmp = Design.rptIdxs[seq];
			selectionIdx=RptIdxInfo.newInstanceKO();
			if(rptIdxTmp){
				$("#rightCellForm [name='cellNo']").val(Utils.initAreaPosiLabel(sheet.getActiveRowIndex() , sheet.getActiveColumnIndex()));
				$("#rightCellForm [name='cellNm']").val(rptIdxTmp.cellNm);  //某一单元格打开后, 更新右侧表单单元格名称
				RptIdxInfo.initIdxKO(selectionIdx,rptIdxTmp);
			}
			ko.applyBindings(selectionIdx , $("#rightCellForm")[0]);
			
			//过滤指标名称
			var idxTmpAll = Design.rptIdxs;
			for(var i in idxTmpAll){
				tmpCellNm = idxTmpAll[i].cellNm;
				tmpCellNo = idxTmpAll[i].cellNo;
				if(tmpCellNm == "" || tmpCellNm == null){
					idxTmpAll[i].cellNm = idxTmpAll[i].cellNo;
				}
			}
		}
	}
	
	function initToolBar(){
		// 初始化ko数据绑定
		if(ko  != null
				&& typeof ko == "object"){
			ko.applyBindings(selectionToolBar , $(".toolbar-awesome")[0]);
		}
	}
	
	//spread移入单元格事件扩展
	function spreadEnterCell(row , col){
		var tmpCellNm = "";
		var tmpCellNo = "";
		var cellNo="";
		var cellTmp = spread.getActiveSheet().getCell(row , col);
		var isFormal = (cellTmp.formula() != null && cellTmp.formula() != "") ? true : false;
		var font=Utils.getFont(cellTmp.font());
		selectionToolBar.font(font.font);
		selectionToolBar.fontSize(font.fontsize.substring(0,font.fontsize.length-2));
		selectionToolBar.bold(font.bold);
		selectionToolBar.italic(font.italic);
		selectionToolBar.textDecoration(cellTmp.textDecoration()?cellTmp.textDecoration():"");
		if(RptIdxInfo.initIdxKO
				&& typeof RptIdxInfo.initIdxKO == "function"){
			var seq = spread.getActiveSheet().getTag(row, col, GC.Spread.Sheets.SheetArea.viewport);
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
				
				//指标单元格过多时，采用单元格内容异步加载模式
				var cellLabel = spread.getActiveSheet().getValue(row, col);
				if(!cellLabel){
					cellLabel = rptIdxTmp.cellLabel("normal");
					spread.getActiveSheet().setValue(row, col, cellLabel);
				}
				
				if(Utils){
					tmpCellNm = rptIdxTmp.cellNm;
					tmpCellNo = rptIdxTmp.cellNo;
					cellNo=Utils.initAreaPosiLabel(row , col);  // 维护单元格编号
					if(tmpCellNm == "" || tmpCellNm == tmpCellNo || tmpCellNo ==""){  //同步单元格名称
						selectionIdx.cellNm(cellNo);
						selectionIdx.cellNo(cellNo);
					}
				}
			}else if(isFormal === true){
				// 将该单元格定义成为公式报表指标
				var uuid = Design.Uuid.v1();
				currSheet.setTag(row, col, uuid);
				var rptIdxInfoTmp = Design.RptIdxInfo.newInstance("04");
				rptIdxInfoTmp.excelFormula = cellTmp.formula();
				var realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
				Design.rptIdxs[uuid] = rptIdxInfoTmp;
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxInfoTmp);
				selectionIdx.seq(seq);
				selectionIdx.excelFormula(cellTmp.formula());
				// 维护单元格编号
				if(Utils){					
					cellNo=Utils.initAreaPosiLabel(row , col);
					//指标配置表存在，则使用配置表中的“报表指标编号”
					if(Design.rptIdxCfg[cellNo] && Design.rptIdxCfg[cellNo] != "" && Design.rptIdxCfg[cellNo] != null){
						realIndexNo = Design.rptIdxCfg[cellNo];
					}
					selectionIdx.realIndexNo = realIndexNo;
					tmpCellNm = rptIdxTmp.cellNm;
					tmpCellNo = rptIdxTmp.cellNo;
					if(tmpCellNm == "" || tmpCellNm == tmpCellNo || tmpCellNo ==""){
						selectionIdx.cellNm(cellNo);
						selectionIdx.cellNo(cellNo);
					}
				}
			}else if(seq != null
					&& Design.commonCells[seq] != null
					&& typeof Design.commonCells[seq] != "undefined"){
				// 若是报表指标
				var commonCellTmp = Design.commonCells[seq];
				RptIdxInfo.initIdxKO(selectionIdx , commonCellTmp);
				selectionIdx.seq(seq);
			}else{
				var uuid = Design.Uuid.v1();
				spread.getActiveSheet().setTag(row, col, uuid);
				var commonCellTmp = Design.RptIdxInfo.newInstance("01");
				commonCellTmp.content = cellTmp.value();
				Design.commonCells[uuid] = commonCellTmp;
				RptIdxInfo.initIdxKO(selectionIdx , commonCellTmp);
				selectionIdx.seq(uuid);
			}
		}
		$("#rightCellForm [name='cellNo']").val(cellNo);
		if(Design.rptIdxCfg[cellNo] && Design.rptIdxCfg[cellNo] != "" && Design.rptIdxCfg[cellNo] != null){
			//$("#realIndexNo").attr("readonly", true);
		}else{
			$("#realIndexNo").attr("readonly", false);
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
		var font=Utils.getFont( currSheet.getCell(currSheet.getActiveRowIndex(), 
				currSheet.getActiveColumnIndex()).font());
		var fontsize=font.fontsize;
		for(var c in selections){
			var currSelection = selections[c];
			for(var i=0;i<currSelection.rowCount;i++){
				for(var j=0;j<currSelection.colCount;j++){
					if(fontsize!=Utils.getFont(currSheet.getCell(currSelection.row+i,currSelection.col+j).font()).fontsize){
						fontsize="  pt";
						break;
					}
				}
			}
		}
		selectionToolBar.font(font.font);
		selectionToolBar.fontSize(fontsize.substring(0,fontsize.length-2));
		selectionToolBar.bold(font.bold);
		selectionToolBar.italic(font.italic);
		selectionToolBar.textDecoration( currSheet.getCell(currSheet.getActiveRowIndex(), 
				currSheet.getActiveColumnIndex()).textDecoration()?currSheet.getCell(currSheet.getActiveRowIndex(), 
						currSheet.getActiveColumnIndex()).textDecoration():"");
		var batchObj = Design.generateBatchSelObj();
		if(batchObj != null){
			for(var i in selectionIdx){
				var reg = /^_.*Batch$/;
				if(reg.test(i)){
					selectionIdx[i](false);
				}
			}
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
			if(args.sheetArea
					&& args.sheetArea != 3){
				// 只有选中单元格区域才进行后续动作
				return ;
			}
			var currSheet = spread.getActiveSheet();
			var selRow = currSheet.getActiveRowIndex();
			var selCol = currSheet.getActiveColumnIndex();
			var currCell = spread.getActiveSheet().getCell(selRow , selCol);
			var seq = currSheet.getTag(selRow, selCol, GC.Spread.Sheets.SheetArea.viewport);
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
				return ;
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
				}
				selectionIdx.rowId(selRow);
				selectionIdx.colId(selCol);
				var width = selectionIdx.dialogWidth();
				var height = selectionIdx.dialogHeight();
				BIONE.commonOpenDialog(selectionIdx.cellTypeNm()+':['+labelTmp+"]",
						'moduleClkDialog',
						width ? Utils.transWidthOrHeight(width , $(window.parent).width()) : 700,
						height ? Utils.transWidthOrHeight(height , $(window.parent).height()) : 400,
						Design._settings.ctx+selectionIdx.dbClkUrl()+'?d='+new Date().getTime());
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
		// selectionIdx在初始化右侧单元格信息form的方法initCellForm()中初始化, 代表选择的指标单元格
		if(selectionIdx == null
				|| typeof selectionIdx == "undefined"
				|| typeof selectionIdx.seq == "undefined"){
			return ;
		}
		var cellType = selectionIdx.cellType();
		var seq = selectionIdx.seq();
		if(seq && cellType){
			if(cellType == "-1"){
				var batchObjTmp = {};
				attrLoop : for(var attr in selectionIdx){
					var reg = /^_.*Batch$/;
					if(!reg.test(attr)
							|| selectionIdx[attr]() === false){
						continue attrLoop;
					}
					var _attr = attr.substring(1 , attr.length - 5);
					if(typeof selectionIdx[_attr] == "function"){
						// 若是批量设置特殊属性，不处理
						if(jQuery.inArray(_attr , Design.RptIdxInfo._batchSpeVals) != -1){
							continue attrLoop;
						}
						// 若批量设置中是default值，不予处理
						// *default值判断：1.==RptIdxInfo._defaultVals中的值；2.else，为""，空字符串
						if(typeof Design.RptIdxInfo._defaultVals[_attr] != 'undefined'){
							// 1
							if(selectionIdx[_attr]() === Design.RptIdxInfo._defaultVals[_attr]){								
								continue attrLoop;
							}
						} else if(selectionIdx[_attr]() == ""){
							// 2
							continue attrLoop;
						}
						batchObjTmp[_attr] = selectionIdx[_attr]();
					}
				}
				var seqs = seq.split(",");
				for(var i = 0 , l = seqs.length ; i < l ; i++){
					var rptIdxTmp = Design.rptIdxs[seqs[i]];
					if(rptIdxTmp){
						jQuery.extend(rptIdxTmp , batchObjTmp);
					}
				}
			}else if(cellType == "01"){  // 处理一般单元格信息
				var commonCell = Design.commonCells[seq];
				if(commonCell){
					for(var i in commonCell){
						if(typeof commonCell[i] == "function"){
							continue;
						}
						if(i in selectionIdx){
							commonCell[i] = selectionIdx[i]();
						}
					}
				}
			}else{
				//针对报表指标格，进行数据修改
				var rptIdx = Design.rptIdxs[seq];
				if(rptIdx){
					for(var i in rptIdx){  //此处遍历的是rptIdx的属性
						if(typeof rptIdx[i] == "function"){
							continue;
						}
						if(i in selectionIdx){
							rptIdx[i] = selectionIdx[i]();
						}
					}
					if(rptIdx.cellNm == ""){
						rptIdx.cellNm = rptIdx.cellNo;   //默认单元格名称为指标编号
					}
				}
			}
		}
	}
	
	// 从父模板初始化表样
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
						var seqTmp = currSheet.getTag(r, c, GC.Spread.Sheets.SheetArea.viewport);
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
			}
		}
		return "";
	}
	
	// 上传表样回调函数
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
						Design.spread.getActiveSheet().setTag(rowTmp, colTmp, seqTmp);
						var rptIdxTmp = RptIdxInfo.newInstance("04");
						rptIdxTmp.seq = seqTmp;
						var realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
						//指标配置表存在，则使用配置表中的“报表指标编号”
						if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
							realIndexNo = Design.rptIdxCfg[currLabel];
						}
						rptIdxTmp.realIndexNo = realIndexNo;
						rptIdxTmp.excelFormula = formulaTmp;
						Design.rptIdxs[seqTmp] = rptIdxTmp; 
					}
				}
			}
		}
	}
	
	// 当前选中单元格设置回调函数
	function selCellSettingHandler(val , type){
		var currSheet = Design.spread.getActiveSheet();
		var selCell = currSheet.getCell(currSheet.getActiveRowIndex() , currSheet.getActiveColumnIndex());
		var currLabel = Utils.initAreaPosiLabel(selCell.row, selCell.col);
		var seqTmp = currSheet.getTag(selCell.row , selCell.col, GC.Spread.Sheets.SheetArea.viewport);
		var oldCellNm = $("#rightCellForm [name='cellNm']").val();
		var oldTmpCellType = $("#rightCellForm [name='cellTypeNm']").val();
		var oldCellType = cellTypeNum(oldTmpCellType);
		switch (type){
		case "02" : 
			syncSelIdxs();
			break;
		case "03" :
			syncSelIdxs();
			break;
		case "05" ://表间取数
			if(!val
					|| typeof val.formulaContent == "undefined"
					|| typeof val.formulaDesc == "undefined"){
				return ;
			}
			var rptIdxTmp = {};
			var realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
			if(seqTmp != null
					&& seqTmp != ""
					&& typeof seqTmp != "undefined"
					&& Design.rptIdxs[seqTmp] != null
					&& typeof Design.rptIdxs[seqTmp] == "object"){
				var oldRptIdx = Design.rptIdxs[seqTmp];
				var oldIndexNo = oldRptIdx.realIndexNo;
				if(oldIndexNo){
					realIndexNo = oldIndexNo;
				}
				// 原先是其他类型的报表指标
				rptIdxTmp = RptIdxInfo.newInstance(type);
				jQuery.extend(rptIdxTmp , val);
				rptIdxTmp.seq = seqTmp;
				if(rptIdxTmp.realIndexNo == null
						|| rptIdxTmp.realIndexNo == ""){
					//指标配置表存在，则使用配置表中的“报表指标编号”
					if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
						realIndexNo = Design.rptIdxCfg[currLabel];
					}
					rptIdxTmp.realIndexNo = realIndexNo;
				}
				//相同单元格,指标改变,保证一些属性不会改变
				for (var key in oldRptIdx) {  
					//可以延用的属性，跨单元格类型
					if(RptIdxInfo.copyAttrs[key]){
						if(oldRptIdx[key]){
							rptIdxTmp[key] = oldRptIdx[key];
						}
					}
		        }
				Design.rptIdxs[seqTmp] = rptIdxTmp; 
			}else{
				seqTmp = Uuid.v1();
				rptIdxTmp = RptIdxInfo.newInstance(type);
				jQuery.extend(rptIdxTmp , val);
				rptIdxTmp.seq = seqTmp;
				if(rptIdxTmp.realIndexNo == null
						|| rptIdxTmp.realIndexNo == ""){
					realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
					//指标配置表存在，则使用配置表中的“报表指标编号”
					if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
						realIndexNo = Design.rptIdxCfg[currLabel];
					}
					rptIdxTmp.realIndexNo = realIndexNo;
				}
				Design.rptIdxs[seqTmp] = rptIdxTmp;
				currSheet.setTag(selCell.row, selCell.col, seqTmp);
			}
			selCell.formula("");
			selCell.value(rptIdxTmp.cellLabel(Design.getShowType()));
			// 若当前选中单元格是selCell，同步两者数据
			if($("#rightCellForm [name='cellNo']").val() == currLabel){
				$("#rightCellForm [name='cellNo']").val(currLabel);
				rptIdxTmp.cellNo = currLabel;
				//修改为类型不同，单元格名称也不变。
 				rptIdxTmp.cellNm = oldCellNm;
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
				syncSelIdxs();
			}
			break;
		case "06":
			var rptIdxTmp = {};
			var currLabel = Utils.initAreaPosiLabel(selCell.row , selCell.col);
			if(seqTmp != null
					&& seqTmp != ""
					&& typeof seqTmp != "undefined"
					&& Design.rptIdxs[seqTmp] != null
					&& typeof Design.rptIdxs[seqTmp] == "object"){
				if(Design.rptIdxs[seqTmp].cellType == "06"){
					// 若该单元格是表达式单元格
					Design.rptIdxs[seqTmp].expression = val;
					jQuery.extend(rptIdxTmp , Design.rptIdxs[seqTmp]);
				} else {
					// 原先是其他类型的报表指标
					rptIdxTmp = RptIdxInfo.newInstance(type);
					rptIdxTmp.seq = seqTmp;
					rptIdxTmp.expression = val;
					Design.rptIdxs[seqTmp] = rptIdxTmp; 
				}
			}else{
				seqTmp = Uuid.v1();
				rptIdxTmp = RptIdxInfo.newInstance(type);
				rptIdxTmp.seq = seqTmp;
				rptIdxTmp.expression = val;
				Design.rptIdxs[seqTmp] = rptIdxTmp;
				currSheet.setTag(selCell.row, selCell.col, seqTmp);
			}
			selCell.formula("");
			selCell.value(rptIdxTmp.cellLabel(Design.getShowType()));
			// 若当前选中单元格是selCell，同步两者数据
			if($("#rightCellForm [name='cellNo']").val() == currLabel){	
				$("#rightCellForm [name='cellNo']").val(currLabel);
				rptIdxTmp.cellNo = currLabel;
				if(rptIdxTmp.cellType == oldCellType){   //单元格类型相同,名称则不变
					rptIdxTmp.cellNm = oldCellNm;
				}else{
					rptIdxTmp.cellNm = rptIdxTmp.cellNo;
 				}
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxTmp);
			}
			break;
		}
	}
	
	//构造报表单元格对象数组
	function strRptCells(rptIdxs){
		var rptCells = [];
		if(rptIdxs){
			for (var key in rptIdxs) {
				var rptidx = rptIdxs[key];
				if(rptidx && ("03" == rptidx.cellType)){//判断是不是指标单元格
					rptCells[rptidx.cellNo] = rptidx;
				}
			}
		}
		return rptCells;
	}
	
	//构造报表指标对象数组
	function strRptIdxs(rptIdxs){
		var rptCells = [];
		if(rptIdxs){
			for (var key in rptIdxs) {
				var rptidx = rptIdxs[key];
				if(rptidx && ("03" == rptidx.cellType)){//判断是不是指标单元格
					rptCells[key] = rptidx;
				}
			}
		}
		return rptCells;
	}
	
	//判断当前单元格是不是修改口径
	function contrastRptIdx(seqTmp, rptidx, rptCells){
		if(seqTmp){
			if(rptidx && ("03" == rptidx.cellType)){
				var oldRptIdx = initRptIdxs[seqTmp];
				if(oldRptIdx){
					initRptIdxs[seqTmp] = null;
					if(rptidx.indexNo != oldRptIdx.indexNo){//指标修改
						if(!rptidx.indexNm){
							rptidx.indexNm = "空指标";
						}
						if(!oldRptIdx.indexNm){
							oldRptIdx.indexNm = "空指标";
						}
						rptIdxchange[rptidx.cellNo] = rptidx.cellNo + "单元格，指标由<" + oldRptIdx.indexNm + ">变成<" + rptidx.indexNm + ">";
					}
				}else{
					oldRptIdx = rptCells[rptidx.cellNo]
					if(oldRptIdx){
						initRptIdxs[oldRptIdx.seq] = null;
						if(rptidx.indexNo != oldRptIdx.indexNo){//指标修改
							if(!rptidx.indexNm){
								rptidx.indexNm = "空指标";
							}
							if(!oldRptIdx.indexNm){
								oldRptIdx.indexNm = "空指标";
							}
							rptIdxchange[rptidx.cellNo] = rptidx.cellNo + "单元格，指标由<" + oldRptIdx.indexNm + ">变成<" + rptidx.indexNm + ">";
						}
					}else{
						//指标新增
						if(!rptidx.indexNm){
							rptidx.indexNm = "空指标";
						}
						rptIdxchange[rptidx.cellNo] = rptidx.cellNo + "单元格，新增指标<" + rptidx.indexNm + ">";
					}
				}
			}
		}
	}
	
	//汇总那些指标已经被删除
	function strReduceRptIdx(rptIdxs){
		if(rptIdxs){
			for (var key in rptIdxs) {
				var rptidx = rptIdxs[key];
				if(rptidx && ("03" == rptidx.cellType)){//判断是不是已经删除
					if(!rptidx.indexNm){
						rptidx.indexNm = "空指标";
					}
					if(!rptIdxchange[rptidx.cellNo]){
						rptIdxchange[rptidx.cellNo] = rptidx.cellNo + "单元格，删除指标<" + rptidx.indexNm + ">";
					}
				}
			}
		}
	}
	
	// 准备待保存数据
	function prepareDatas4Save(){
		//初始化报表单元格对象
		var initRptCells = [];
		initRptCells = strRptCells(initRptIdxs);
		
 		var tmpCellNm = "";
 		var tmpCellNo = "";
		
		var saveObj = null;
		if(Design && spread){
			var tabId = lineId;
			var remarkDivDoom = $("#remarkDiv").children(".l-textarea");
			if(window.parent.lineTmpIds){
				if(tabId == null
						|| tabId == ""){
					tabId = "_mainTab";
				}
				var tmpIdTmp = window.parent.lineTmpIds[tabId];
				if(tmpIdTmp != null
						&& tmpIdTmp != ""
						&& typeof tmpIdTmp != "undefined"){
					templateId = tmpIdTmp;
				}
			}
			if(tabId == null
					|| tabId == ""){
				tabId = "_mainTab";
			}
			var jsonStr = "";
			saveObj = {
					lineId : lineId,
					templateId : templateId,
					jsonStr : "" , 
					remark : remarkDivDoom.attr("isNull") == "true" ? "" : remarkDivDoom.val() ,
					rptIdxs : [],
					rowCols : [],
					cellsArray : [],
					changeArray : {}
			};
			// 分析已定义报表指标
			// 1.执行一次同步方法
			// 2.若Design.rptIdxs为空，表示没定义报表指标
			// 3.若Design.rptIdxs不为空，判断这些seq是否对应有具体的cell
			// 		若有，维护相应rptIdx中的行，列
			//		若无，不算合法的rptIdx，不解析

			syncSelIdxs();  //保存前最后编辑的一个单元格信息的更新
			var currSheet = spread.getActiveSheet();
			var firstCell = currSheet.getCell(0,0);
			if((firstCell.value() == null
					|| firstCell.value() == "")
					&& firstCell.formula() == null){
				// 此处其实是一个为了防止报表无任何数据导致自动生成的json缺失dataTable信息的一个解决方法。
				// 默认把第一个无数据无公式的表置入一个空值，这样就算表单没数，也会生成dataTable属性。此处应有掌声 = 。=
				firstCell.value("");	
			}
			if(Design.rptIdxsSize() > 0
					|| Design.rowColsSize() > 0 || Design.commonCells.length > 0){
				// 获取全部cell，挨个分析是否有seq属性
				//（这部分待优化，可以放入java端处理，但目前json接口不支持扩展属性，所以先在前端处理）
				var rowCount = currSheet.getRowCount();
				var colCount = currSheet.getColumnCount();
				var cellOldVals = [];
				for(var r = 0 ; r < rowCount ; r++){
					for(var c = 0 ; c < colCount ; c++){
						if(r == 0 && c != 0){
							// 列对象
							var colSeqTmp = currSheet.getTag((r-1), c, GC.Spread.Sheets.SheetArea.viewport);
							var objTmp = Design.rowCols[colSeqTmp];
							if(colSeqTmp != null
									&& colSeqTmp != ""
									&& typeof colSeqTmp != "undefined"
									&&  objTmp){
								objTmp.rowId = r;
								objTmp.colId = c;
								saveObj.rowCols.push(objTmp);
							}
						}else if(r != 0 && c == 0){
							// 行对象
							var rowSeqTmp = currSheet.getTag(r, (c-1), GC.Spread.Sheets.SheetArea.viewport);
							var objTmp = Design.rowCols[rowSeqTmp];
							if(rowSeqTmp != null
									&& rowSeqTmp != ""
									&& typeof rowSeqTmp != "undefined"
									&&  objTmp){
								objTmp.rowId = r;
								objTmp.colId = c;
								saveObj.rowCols.push(objTmp);
							}
						}
						// 一般单元格对象
                		var cellTmp = currSheet.getCell(r, c);
						var seqTmp = currSheet.getTag(r, c, GC.Spread.Sheets.SheetArea.viewport);
						if(seqTmp != null
								&& seqTmp != ""
								&& typeof seqTmp != "undefined"
								&& Design.rptIdxs[seqTmp] != null
								&& typeof Design.rptIdxs[seqTmp] == "object"){
							Design.rptIdxs[seqTmp].rowId = r;
							Design.rptIdxs[seqTmp].colId = c;
							tmpCellNo = Design.rptIdxs[seqTmp].cellNo;  //edit by xy 先前的cellNo
							tmpCellNm = Design.rptIdxs[seqTmp].cellNm;
							Design.rptIdxs[seqTmp].cellNo = Utils.initAreaPosiLabel(r , c);

							if(tmpCellNm == "" || tmpCellNm == tmpCellNo || tmpCellNo == ""){  //同步单元格名称
								Design.rptIdxs[seqTmp].cellNm = Design.rptIdxs[seqTmp].cellNo;
			 				}
							
							if(typeof Design.rptIdxs[seqTmp].realIndexNo != "undefined"
									&& (Design.rptIdxs[seqTmp].realIndexNo == null
											|| Design.rptIdxs[seqTmp].realIndexNo == "")){
								var realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
								//指标配置表存在，则使用配置表中的“报表指标编号”
								if(Design.rptIdxCfg[tmpCellNo] && Design.rptIdxCfg[tmpCellNo] != "" && Design.rptIdxCfg[tmpCellNo] != null){
									realIndexNo = Design.rptIdxCfg[tmpCellNo];
								}
								Design.rptIdxs[seqTmp].realIndexNo = realIndexNo;
							}
							if(Design.rptIdxs[seqTmp].cellType == "04"){
								Design.rptIdxs[seqTmp].excelFormula = cellTmp.formula();
							}else if(tabId == "_mainTab"){
								var keyTmp = cellTmp.row + "," + cellTmp.col;
								cellOldVals.push(keyTmp);
							}
							if("03" == Design.rptIdxs[seqTmp].displayFormat || "04" == Design.rptIdxs[seqTmp].displayFormat){//如果显示格式是文本或者数值
								Design.rptIdxs[seqTmp].dataPrecision = "";
							}
							saveObj.rptIdxs.push(Design.rptIdxs[seqTmp]);							
							contrastRptIdx(seqTmp, Design.rptIdxs[seqTmp], initRptCells);
						}else if(seqTmp != null
								&& seqTmp != ""
									&& typeof seqTmp != "undefined"
									&& Design.commonCells[seqTmp] != null
									&& typeof Design.commonCells[seqTmp] == "object"){
								if(Design.commonCells[seqTmp].cellType == Design.Constants.CELL_TYPE_COMMON){
									if(Design.commonCells[seqTmp].typeId != "00"){
										// 一般单元格则保存
										Design.commonCells[seqTmp].rowId = r;
										Design.commonCells[seqTmp].colId = c;
										Design.commonCells[seqTmp].cellNo = Utils.initAreaPosiLabel(r , c);
										saveObj.cellsArray.push(Design.commonCells[seqTmp]);		
									}
									else{
										currSheet.setTag(r, c, null)
									}
								}
								else{
									currSheet.setTag(r, c, null)
								}
								
						}else if (cellTmp.formula() != null
								&& cellTmp.formula() != ""){
							// 若是excel公式，但由于某些原因保存时并没有被解析成报表指标，自动生成报表指标
							var rptIdxTmp = RptIdxInfo.newInstance("04");
							rptIdxTmp.rowId = r;
							rptIdxTmp.colId = c;
							tmpCellNo = rptIdxTmp.cellNo;
							rptIdxTmp.cellNo = Utils.initAreaPosiLabel(r , c);
							currSheet.setTag(r, c, seqTmp);
							//edit by xy
							tmpCellNm = rptIdxTmp.cellNm;
							if(tmpCellNm == "" || tmpCellNm == tmpCellNo || tmpCellNo == ""){
								rptIdxTmp.cellNm = rptIdxTmp.cellNo;
			 				}
							
							var realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
							//指标配置表存在，则使用配置表中的“报表指标编号”
							if(Design.rptIdxCfg[tmpCellNo] && Design.rptIdxCfg[tmpCellNo] != "" && Design.rptIdxCfg[tmpCellNo] != null){
								realIndexNo = Design.rptIdxCfg[tmpCellNo];
							}
							rptIdxTmp.realIndexNo = realIndexNo;
							rptIdxTmp.excelFormula = cellTmp.formula();
							rptIdxTmp.indexNo = indexNo;
							saveObj.rptIdxs.push(rptIdxTmp);
						}
					}
				}
				if(tabId == "_mainTab"){
					//特殊处理excel公式
					for (var i = 0; i < saveObj.rptIdxs.length; i++) {
						var rptIdxTmp = saveObj.rptIdxs[i];
						if(rptIdxTmp.cellType == Design.Constants.CELL_TYPE_FORMULA){
							var indexNos = getExcelRefCell(currSheet,rptIdxTmp.excelFormula, rptIdxTmp.rowId, rptIdxTmp.colId);
							rptIdxTmp.indexNo = indexNos.join(',');
						}
					}

					var jsonTmp = spread.toJSON();
					var jsonMin;
					if(Utils
							&& typeof Utils.jsonMin == "function"){
						jsonMin = Utils.jsonMin(jsonTmp,cellOldVals);
					}else{
						jsonMin = jsonTmp;
					}
					jsonStr = JSON2.stringify(jsonMin);
					saveObj.jsonStr = jsonStr;
				}
			}
		}
		strReduceRptIdx(initRptIdxs);
		saveObj.changeArray = rptIdxchange;
		return saveObj;
	}

	// 查询excel公式引用的单元格指标编号
	function getExcelRefCell(currSheet, formulaContent, rowId, colId){
		var indexNoList = [];
		var cellRanges = GC.Spread.Sheets.CalcEngine.formulaToRanges(currSheet, formulaContent, rowId, colId);
		if(cellRanges.length > 0){
			for (var i = 0; i < cellRanges[0].ranges.length; i++) {
				var cellInfo = cellRanges[0].ranges[i];
				for(var r = 0; r < cellInfo.rowCount; r++){
					for(var c = 0; c < cellInfo.colCount; c++){
						var seqTmp = currSheet.getTag(cellInfo.row + r, cellInfo.col + c, GC.Spread.Sheets.SheetArea.viewport);
						if(seqTmp != null && seqTmp != "" && typeof seqTmp != "undefined" ){
							var indexNo = Design.rptIdxs[seqTmp].realIndexNo;
							if(indexNoList.indexOf(indexNo) == -1){
								indexNoList.push(indexNo);
							}
						}
					}
				}
			}
		}
		return indexNoList;
	}
	
	// 修改时，初始化相关数据
	function initUptDatas(rptRemark){
		// 目前只维护了报表描述信息
		if(rptRemark
				&& $.trim(rptRemark) != ""){
			var remarkDivDoom = $("#remarkDiv").children(".l-textarea");
			remarkDivDoom.removeClass("l-text-field-null").removeAttr("isNull").val(rptRemark);
		}
	}

	// 设置空指标
	function initEmptyIdxCell(sheet , cellInfo){
		if(!sheet || !cellInfo){
			return ;
		}
		var seq = Design.spread.getActiveSheet().getTag(cellInfo.row, cellInfo.col, GC.Spread.Sheets.SheetArea.viewport);
		var rptInfoTmp = Design.rptIdxs[seq];
		var realIndexNo = Uuid.v1().replace(/-/g, '');//去掉报表编号_单元格编号，统一改为：uuid的写法
		if(seq != null
				&& Design.rptIdxs[seq] != null
				&& typeof Design.rptIdxs[seq] != "undefined"){
			var oldIndexNo = Design.rptIdxs[seq].realIndexNo;
			if(oldIndexNo != "" && typeof oldIndexNo != "undefined"){
				realIndexNo = oldIndexNo;
			}
			delete Design.rptIdxs[seq];
		}
		seq = Design.Uuid.v1();
		var emptyInfo = Design.RptIdxInfo.newInstance("03");
		emptyInfo.indexNo = "";
		emptyInfo.indexNm = "";
		emptyInfo.dbClkUrl = ""; // 空指标，不允许和普通指标一样进行双击设置
		var currLabel = Utils.initAreaPosiLabel(cellInfo.row , cellInfo.col);

		var oldCellNo = rptInfoTmp ? rptInfoTmp.cellNo : $("#rightCellForm [name='cellNo']").val();
		var oldCellNm = rptInfoTmp ? rptInfoTmp.cellNm : $("#rightCellForm [name='cellNm']").val();
		var oldTmpCellType = rptInfoTmp ? rptInfoTmp.cellTypeNm : $("#rightCellForm [name='cellTypeNm']").val();
		var oldCellType = cellTypeNum(oldTmpCellType);	
		
		//edit by xy
		emptyInfo.cellNo = currLabel;
		//指标配置表存在，则使用配置表中的“报表指标编号”
		if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
			realIndexNo = Design.rptIdxCfg[currLabel];
		}
		emptyInfo.realIndexNo = realIndexNo;
		
		if(emptyInfo.cellNo == oldCellNo){//相同单元格,指标改变,保证一些属性不会改变
			if(rptInfoTmp){
				for (var key in rptInfoTmp) {  
					//可以延用的属性，跨单元格类型
					if(RptIdxInfo.copyAttrs[key]){
						if(rptInfoTmp[key]){
							emptyInfo[key] = rptInfoTmp[key];
						}
					}
		        }
			}else{
				emptyInfo.cellNm = emptyInfo.cellNo;
			}
		}
		

		Design.spread.getActiveSheet().setTag(cellInfo.row , cellInfo.col, seq)
		Design.rptIdxs[seq] = emptyInfo;
		Design._setCellFormula(cellInfo.row , cellInfo.col , "" , sheet);
		Design._setCellValue(cellInfo.row , cellInfo.col , emptyInfo.cellLabel(Design.getShowType()) , sheet);
		if(Utils&& currLabel == $("#rightCellForm [name='cellNo']").val()){					
			RptIdxInfo.initIdxKO(selectionIdx , emptyInfo);
			$("#rightCellForm [name='cellNo']").val(currLabel);
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
	
	// 单元格名称变化事件处理
	function cellNmChangeHandler(){
		var newVal = $("input[name=cellNm]").val();
		var currCellNo = $("input[name=cellNo]").val();
		if(newVal){
			if(!(/^[0-9a-zA-Z\u2160-\u216B\u4e00-\u9fa5\uFF08\uFF09\uFF1A_-]*$/.test(newVal))){//数字、字母、罗马数字、中文、中文括号、下划线、横线、中文冒号
				BIONE.showError("单元格名称只能输入[数字、字母、罗马数字、中文、中文括号、下划线、横线、中文冒号]");
				$("input[name=cellNm]").val(currCellNo);
				return;
			}
			if(Design.Constants.SHOW_TYPE_CELLNM == Design.getShowType()){
				var rowCol = Design.Utils.posiToRowCol(currCellNo);
				var row = rowCol.row;
				var col = rowCol.col;
				var currSheet = Design.spread.getActiveSheet();
				currSheet.getCell(row , col).value("{"+newVal+"}");
			}
		}else{
			BIONE.showError("单元格名称不能为空!");
			$("input[name=cellNm]").val(currCellNo);
		}
	}
	
	// 业务编号变化事件处理
	function busiNoChangeHandler(){
		if(Design.Constants.SHOW_TYPE_BUSINO == Design.getShowType()){
			// 若是业务编号名称展示，需要同步label
			var newVal = $("input[name=busiNo]").val();
			var currCellNo = $("input[name=cellNo]").val();
			var rowCol = Design.Utils.posiToRowCol(currCellNo);
			var row = rowCol.row;
			var col = rowCol.col;
			var currSheet = Design.spread.getActiveSheet();
			currSheet.getCell(row , col).value("{"+newVal+"}");
		}
	}
	
	// 导入样式
	function css_import(){
		BIONE.commonOpenDialog('样式上传','requireuploadWin',600,330,'${ctx}/report/frame/design/cfg/cssupload?d='+new Date().getTime());
	}
	
	//弹出页面使用提醒，防止出现一些配置问题
	function initRemind(){
		BIONE.commonOpenDialog('使用提醒', 'remind', 1200, 800, '${ctx}/report/frame/design/cfg/remind');
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="designLayout" style="width : 99%;">
            <div id="leftDiv" position="left" >
            	<div id="treeAccordion">
					<div id="idxTree" class="treeContainer" 
						style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<div class="l-text l-text-date" style="width: 100%;">
							<input id="treeSearchInput" type="text" class="l-text-field"
								style="width: 95%;" />
							<div class="l-trigger">
								<i id="treeSearchIcon" class="icon-search search-size"></i>
							</div>
						</div>
						<ul id="tree1" style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree">
						</ul>
					</div>
					<div id="modelTree" class="treeContainer" 
						style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
						<ul id="tree2" style="font-size: 12; background-color: #FFFFFF; width: 92%" class="ztree">
						</ul>
					</div>
				</div>
            </div>
            <div id="centerDiv" position="center" >
            	<div id="spread" style="width:99.5%;border-bottom:1px solid #D0D0D0;"></div>
            	<div id="remarkDiv" style="padding-top:2px;">
            		<div class="l-layout-header">
 						<div id="remarkToggle"  currType="show" 
 							style="position: absolute;top: 3px;right: 3px;height: 20px;width: 20px;overflow: hidden;cursor: pointer;">
 						</div>
 						 <div class="l-layout-header-inner">
	  						  <span>报表描述：</span>           
  						 </div>
  					</div>
            		<textarea id="rptdesc" class="l-textarea" style="width:99%;margin:2px;resize: none;"></textarea>
            	</div>
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
            		<tr>
                		<td align="right" class="l-table-edit-td">单元格名称:</td>
                			<td align="left" class="l-table-edit-td"><input onchange="cellNmChangeHandler()" class="own-formitem-style" name="cellNm" data-bind="value:cellNm,valueUpdate:'afterkeydown'" type="text" id="cellNm" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='01' ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">业务编号:</td>
                			<td align="left" class="l-table-edit-td"><input onchange="busiNoChangeHandler()" class="own-formitem-style" name="busiNo" data-bind="value:busiNo,valueUpdate:'afterkeydown'" type="text" id="busiNo" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
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
            		<tr data-bind="style:{ display : cellType()=='03'  && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">来源指标:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="indexNm"  data-bind="value:indexNm,valueUpdate:'afterkeydown'"  type="text" id="indexNm" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<!-- <tr data-bind="style:{ display : cellType()=='03' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">是否减维:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="isSubDim"  data-bind="value:allDims() == factDims()? '否' : '是'"  type="text" id="isSubDim" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr> -->
            		<tr data-bind="style:{ display : cellType()=='03'  && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">是否过滤:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="isFiltDim"  data-bind="value:filtInfos() == null  || (typeof filtInfos() == 'string' && filtInfos().length <= 2) || (typeof filtInfos() == 'object' && filtInfos().length <= 0)?  '否' : '是'"  type="text" id="isFiltDim" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='04' ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">公式内容:</td>
                		<td align="left" class="l-table-edit-td"> 
                			<textarea  data-bind="value:excelFormula,valueUpdate:'afterkeydown'"class="l-textarea own-formitem-style" style="height:60px;resize:none;" readonly=true></textarea>
                		</td>
                		<td align="left"></td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='04'  && templateType != '01')? '' : 'none'}">
                		<td align="right" class="l-table-edit-td" valign="top">是否报表指标:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="isRptIndex" id="isRptIndex" ltype="select" data-bind="value:isRptIndex" >
								<option value="Y">是</option>
								<option value="N">否</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : cellType()=='04' ? '' : 'none'}">
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
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' ) ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">显示格式:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="displayFormat" id="displayFormat" ltype="select" data-bind="value:displayFormat" >
								<option value="01">原格式</option>
								<option value="02">百分比</option>
								<option value="" style="display:none;"></option>
							</select>
                		</td>
            		</tr>
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()=='02') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td" valign="top">数据单位:</td>
                		<td align="left" class="l-table-edit-td">
                			<select class="own-formitem-style" name="dataUnit" id="dataUnit" ltype="select" data-bind="value:dataUnit" >
                				<option value="">使用模板单位</option>
								<option value="01">元</option>
								<option value="02">百</option>
								<option value="03">千</option>
								<option value="04">万</option>
								<option value="05">亿</option>
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
            		 -->
            		<tr data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()=='02') ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">显示精度:</td>
                			<td align="left" class="l-table-edit-td"><input class="own-formitem-style" name="dataPrecision"  data-bind="value:dataPrecision,valueUpdate:'afterkeydown'"  type="text" id="dataPrecision" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr>
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
            		<tr data-bind="style:{ display : cellType()=='01' || cellType()=='-1'? 'none' : ''}">
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