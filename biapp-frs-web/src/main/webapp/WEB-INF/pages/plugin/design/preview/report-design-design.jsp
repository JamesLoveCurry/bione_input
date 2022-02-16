<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="decorator" content="/template/template5.jsp" />
<%@ include file="/common/spreadjs_load.jsp"%>
<style type="text/css">
.l-table-edit {
	
}

.l-table-edit-td {
	padding: 4px;
}

.l-button-submit,.l-button-reset {
	width: 80px;
	float: left;
	margin-left: 10px;
	padding-bottom: 2px;
}

.l-verify-tip {
	left: 230px;
	top: 120px;
}

.own-formitem-style {
	width: 108px;
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
	var templateType = frameDocument.rptvo.templateType; // 01 - 明细类；02 - 单元格类
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
		// 设计器
		initDesign();
		
		initBtn();
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
	
	function initBtn(){
		//添加按钮
		var btns = [{
			text : "复制",
			onclick : f_copy
		}];
		BIONE.addFormButtons(btns);
	}
	
	function f_copy(){
		var selRow = frameDocument.rptvo;
		var modifyUrl = "${ctx}/report/frame/design/cfg/baseEdit?datetime="+new Date().getTime();
		modifyUrl += ("&defSrc=03&rptId="+selRow.rptId+"&templateId="+selRow.templateId+"&templateType="+selRow.templateType + "&verId="+selRow.verId + "&copyRptId="+selRow.rptId);
		window.BIONE.commonOpenDialog("【复制报表】" , "baseEdit" , 600 , 450 , modifyUrl);
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
			if(window.parent.parent.tmpInfo != null){
				objTmp = parent.parent.tmpInfo;
				if( objTmp.filterContent){
					glofilters = objTmp.filterContent;
				}
				templateId = objTmp.tmpInfo.id.templateId;
				jsonStr = objTmp.tmpInfo.templateContentjson;
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
					onCellDoubleClick : spreadDbclkCell,
					onSelectionChanged : spreadSelectionChanged,
					cellDetail:true,
					toolbar:false,
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
		var cellNo="";
		var cellTmp = spread.getActiveSheet().getCell(row , col);
		var isFormal = (cellTmp.formula() != null && cellTmp.formula() != "") ? true : false;
		var font=Utils.getFont(cellTmp.font());
		if(RptIdxInfo.initIdxKO
				&& typeof RptIdxInfo.initIdxKO == "function"){
			var seq = Design._getStyleProperty(row, col, "seq");
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
					cellNo=Utils.initAreaPosiLabel(row , col);
// 					if(selectionIdx.cellNm() == ""){
// 						selectionIdx.cellNm(Utils.initAreaPosiLabel(row , col));
// 					}
				}
			}else if(isFormal === true){
				// 将该单元格定义成为公式报表指标
				var uuid = Design.Uuid.v1();
				Design._setStyleProperty(row, col, "seq", uuid)
				var rptIdxInfoTmp = Design.RptIdxInfo.newInstance("04");
				rptIdxInfoTmp.excelFormula = cellTmp.formula();
				rptIdxInfoTmp.realIndexNo = Uuid.v1().replace(/-/g, '');
				Design.rptIdxs[uuid] = rptIdxInfoTmp;
				RptIdxInfo.initIdxKO(selectionIdx , rptIdxInfoTmp);
				selectionIdx.seq(seq);
				selectionIdx.excelFormula(cellTmp.formula());
				// 维护单元格编号
				if(Utils){		
					var cellNo=Utils.initAreaPosiLabel(row , col);
// 					if(selectionIdx.cellNm() == ""){
// 						selectionIdx.cellNm(Utils.initAreaPosiLabel(row , col));
// 					}
				}
			}else{
				var rptIdxTmp = {};
				RptIdxInfo.initIdxKO(selectionIdx);
				
			}
		}
		$("#rightCellForm [name='cellNo']").val(cellNo);
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
			var seq = Design._getStyleProperty(selRow, selCol, "seq");
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
				dbClkUrl="${ctx}/report/frame/tmp/view/module?d="+new Date().getTime();
			}
			else if(selectionIdx.cellType() == "03" || selectionIdx.cellType() == "07"){
				dbClkUrl="${ctx}/report/frame/idx/"+selectionIdx.indexNo()+"/preview?d="+new Date().getTime();
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
						cellTmp._setStyleProperty("seq" , seqTmp);
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
		<div id="designLayout" style="width: 99%;">
			<div id="centerDiv" position="center">
				<div id="spread"
					style="width: 99.5%; border-bottom: 1px solid #D0D0D0;"></div>
			</div>
			<div id="rightDiv" position="right" title="单元格信息">
				<form id="rightCellForm" action="" class="l-form">
					<table cellpadding="0" cellspacing="0" class="l-table-edit">
						<tr>
							<td align="right" class="l-table-edit-td ">单元格类型:</td>
							<td align="left" class="l-table-edit-td l-text-readonly"><input
								class="own-formitem-style" name="cellTypeNm"
								data-bind="value: cellTypeNm" type="text" id="cellTypeNm"
								ltype="text" readonly=true /></td>
							<td align="left"></td>
						</tr>
						<tr
							data-bind="style:{ display : cellType()=='01'  || cellType() == '-1'? 'none' : ''}">
							<td align="right" class="l-table-edit-td ">单元格编号:</td>
							<td align="left" class="l-table-edit-td l-text-readonly"><input
								class="own-formitem-style" name="cellNo" type="text" id="cellNo"
								ltype="text" readonly=true /></td>
							<td align="left"></td>
						</tr>
						<!-- <tr data-bind="style:{ display : cellType()=='01' ? 'none' : ''}">
                		<td align="right" class="l-table-edit-td">单元格名称:</td>
                			<td align="left" class="l-table-edit-td"><input onchange="cellNmChangeHandler()" class="own-formitem-style" name="cellNm" data-bind="value:cellNm,valueUpdate:'afterkeydown'" type="text" id="cellNm" ltype="text" /></td>
               			 <td align="left"></td>
            		</tr> -->
						<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
							<td align="right" class="l-table-edit-td">数据模型:</td>
							<td align="left" class="l-table-edit-td l-text-readonly"><input
								class="own-formitem-style" name="dsName"
								data-bind="value:dsName,valueUpdate:'afterkeydown'" type="text"
								id="dsName" ltype="text" readonly=true /></td>
							<td align="left"></td>
						</tr>
						<tr data-bind="style:{ display : cellType()=='02' ? '' : 'none'}">
							<td align="right" class="l-table-edit-td">字段名称:</td>
							<td align="left" class="l-table-edit-td l-text-readonly"><input
								class="own-formitem-style" name="columnName"
								data-bind="value:columnName,valueUpdate:'afterkeydown'"
								type="text" id="columnName" ltype="text" readonly=true /></td>
							<td align="left"></td>
						</tr>
						<tr data-bind="style:{display : (cellType()=='03' || cellType() == '07') && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
                		<td align="right" class="l-table-edit-td">指标编号:</td>
                			<td align="left" class="l-table-edit-td l-text-readonly"><input class="own-formitem-style" name="indexNo"  data-bind="value:indexNo,valueUpdate:'afterkeydown'"  type="text" id="indexNo" ltype="text"  readonly=true/></td>
               			 <td align="left"></td>
            			</tr>
						<tr
							data-bind="style:{ display : (cellType()=='03' || cellType() == '07') && (indexNo() != '' && indexNo() != null) ? '' : 'none'}">
							<td align="right" class="l-table-edit-td">来源指标:</td>
							<td align="left" class="l-table-edit-td l-text-readonly"><input
								class="own-formitem-style" name="indexNm"
								data-bind="value:indexNm,valueUpdate:'afterkeydown'" type="text"
								id="indexNm" ltype="text" readonly=true /></td>
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
							<td align="left" class="l-table-edit-td"><textarea
									data-bind="value:excelFormula,valueUpdate:'afterkeydown'"
									class="l-textarea own-formitem-style"
									style="height: 60px; resize: none;" readonly=true></textarea></td>
							<td align="left"></td>
						</tr>
						<tr
							data-bind="style:{ display : (cellType()=='04'  && templateType != '01' && templateType != '04') ? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">是否报表指标:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="isRptIndex" id="isRptIndex"
								ltype="select" data-bind="value:isRptIndex">
									<option value="Y">是</option>
									<option value="N">否</option>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display : (cellType()=='04' && templateType != '04' ) ? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">分析扩展:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="isAnalyseExt" id="isAnalyseExt"
								ltype="select" data-bind="value:isAnalyseExt">
									<option value="Y">是</option>
									<option value="N">否</option>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						<tr data-bind="style:{ display : cellType()=='05' ? '' : 'none'}">
							<td align="right" class="l-table-edit-td">表间计算:</td>
							<td align="left" class="l-table-edit-td"><textarea
									data-bind="value:formulaDesc,valueUpdate:'afterkeydown'"
									class="l-textarea own-formitem-style"
									style="height: 60px; resize: none;" readonly=true></textarea></td>
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
						<tr
							data-bind="style:{ display : (cellType()=='01' || cellType()=='06'  || cellType()=='08') ? 'none' : ''}">
							<td align="right" class="l-table-edit-td" valign="top">显示格式:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="displayFormat"
								id="displayFormat" ltype="select"
								data-bind="value:displayFormat">
									<option value="01" label="金额" />
									<option value="03" label="数值" />
									<option value="02" label="比例" />
									<option value="-1" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()!='01'|| cellType()=='08') ? 'none' : ''}">
							<td align="right" class="l-table-edit-td" valign="top">数据单位:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="dataUnit" id="dataUnit"
								ltype="select" data-bind="value:dataUnit">
									<option value="">使用模板单位</option>
									<option value="01" label="元" />
									<option value="02" label="百元" />
									<option value="03" label="千元" />
									<option value="04" label="万元" />
									<option value="05" label="亿元" />
									<option value="00">无单位</option>
									<option value="-1" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display : (cellType()=='01' || cellType()=='06' || displayFormat()!='03'|| cellType()=='08') ? 'none' : ''}">
							<td align="right" class="l-table-edit-td" valign="top">数据单位:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="dataUnit" id="dataUnit"
								ltype="select" data-bind="value:dataUnit">
									<option value="">使用模板单位</option>
									<option value="01" label="个" />
									<option value="02" label="百" />
									<option value="03" label="千" />
									<option value="04" label="万" />
									<option value="05" label="亿" />
									<option value="00">无单位</option>
									<option value="-1" style="display: none;"></option>
							</select></td>
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
						<tr
							data-bind="style:{ display : (cellType()=='08' && templateType == '04'  && dimType() == '01') ? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">日期格式:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="dateFormat" id="dateFormat"
								ltype="select" data-bind="value:dateFormat">
									<option value="01">年月日</option>
									<option value="02">年月</option>
									<option value="03">月</option>
									<option value="04">日</option>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display : cellType()=='08' && templateType == '04' ? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">是否合计:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="isTotal" id="isTotal"
								ltype="select" data-bind="value:isTotal">
									<option value="Y">是</option>
									<option value="N">否</option>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display : cellType()=='08' && templateType == '04' ? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">转码显示:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="isConver" id="isConver"
								ltype="select" data-bind="value:isConver">
									<option value="Y">是</option>
									<option value="N">否</option>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						
						<tr data-bind="style:{display : (cellType()=='08' && (templateType == '04' || templateType == '05')) ? '' : 'none'}">
	                		<td align="right" class="l-table-edit-td" valign="top">显示级别:</td>
							<td align="left" class="l-table-edit-td">
	                			<div class="l-text l-text-popup" style="width:106px;border-color:#999999;height:18px;">
									<input class="displayLvlDimNo" type="hidden" name="displayLvlDimNo" data-bind="value:dimTypeNo" />
									<input name="displayLvl" data-bind="value:displayLevel() != '' && displayLevel() != null ? displayLevel() : '无'" type="text" id="popTxt" class="liger-popupedit l-text-field" onclick="displayLvlHandler(this)" readonly="" ligeruiid="popTxt" style="width:102px;padding-left:4px;"/>
								</div>
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
						<tr
							data-bind="style:{ display : (cellType()=='07' || cellType()=='03') ? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">计算规则:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="ruleId" id="ruleId"
								ltype="select" data-bind="value:ruleId">
									<c:forEach items="${calcRules}" var="calcRule">
										<option value="${calcRule['ruleId']}">${calcRule['ruleNm']}</option>
									</c:forEach>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display :(cellType()=='07' || cellType()=='03') ? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">时间度量:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="timeMeasureId"
								id="timeMeasureId" ltype="select"
								data-bind="value:timeMeasureId">
									<c:forEach items="${timeMeasures}" var="timeMeasure">
										<option value="${timeMeasure['timeMeasureId']}">${timeMeasure['measureNm']}</option>
									</c:forEach>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display : (cellType()=='07' || cellType()=='03') ? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">取值方式:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="modeId" id="modeId"
								ltype="select" data-bind="value:modeId">
									<c:forEach items="${valTypes}" var="valType">
										<option value="${valType['modeId']}">${valType['modeNm']}</option>
									</c:forEach>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display : templateType=='04' && (cellType()=='07' || cellType()=='04')? '' : 'none'}">
							<td align="right" class="l-table-edit-td" valign="top">排序方式:</td>
							<td align="left" class="l-table-edit-td"><select
								class="own-formitem-style" name="sortMode" id="sortMode"
								ltype="select" data-bind="value:sortMode">
									<option value="01">不排序</option>
									<option value="02">正序</option>
									<option value="03">倒序</option>
									<option value="" style="display: none;"></option>
							</select></td>
						</tr>
						<tr
							data-bind="style:{ display : cellType()=='01' || cellType()=='-1' || cellType()=='08' ? 'none' : ''}">
							<td align="right" class="l-table-edit-td">口径说明:</td>
							<td align="left" class="l-table-edit-td"><textarea
									data-bind="value:caliberExplain,valueUpdate:'afterkeydown'"
									class="l-textarea own-formitem-style"
									style="height: 60px; resize: none;"></textarea></td>
							<td align="left"></td>
						</tr>
						
						
					</table>
				</form>
			</div>
		</div>
	</div>
</body>
</html>