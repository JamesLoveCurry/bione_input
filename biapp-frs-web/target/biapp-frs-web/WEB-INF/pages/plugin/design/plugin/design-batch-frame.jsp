<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="decorator" content="/template/template5.jsp" />
<script type="text/javascript">

	var row = "${row}";
	var col = "${col}";
	var idxNos = "${idxNos}";
	var filterInfos = [];
	var tabObj;
	var frameHeight;
	// tab的src对应关系
	var frameSrcs = [];
	frameSrcs["filter"] = "${ctx}/report/frame/design/cfg/batch/filter";
	frameSrcs["confirm"] = "${ctx}/report/frame/design/cfg/batch/confirm";
	var buttons = [];
	buttons.push({
		text : '取消',
		onclick : closeHandler
	});
	buttons.push({
		text : '保存',
		onclick : saveHandler
	});
	
	$(function() {
		var filtersTmp = getRowColInfo();
		if(filtersTmp
				&& filtersTmp.filtInfos){
			filterInfos = JSON2.parse(filtersTmp.filtInfos);
		}
		frameHeight = $(window).height() - 75;
		initButtons(buttons);
		initTab();
	});
	
	function getRowColInfo(){
		var filterInfo = null;
		var design = window.parent.Design;
		if(row != null
				&& typeof row != "undefined"
				&& col != null
				&& typeof col != "undefined"
				&& design){
			var currSheet = design.spread.getActiveSheet();
            var seqTmp = (row == 0 || row == -1) ? currSheet.getTag(-1, parseInt(col), window.parent.GC.Spread.Sheets.SheetArea.viewport) :
            	currSheet.getTag(parseInt(row), -1, window.parent.GC.Spread.Sheets.SheetArea.viewport);
			if(seqTmp != null
					&& typeof seqTmp != "undefined"
						&& typeof design.rowCols[seqTmp] != "undefined"){
				filterInfo = design.rowCols[seqTmp];
			}
		}
		return filterInfo;
	}
	
	function initTab(){
		$("#filter").height(frameHeight);
		$("#confirm").height(frameHeight);
		tabObj = $("#mainTabId").ligerTab({
			onBeforeSelectTabItem : function(tabId){
				var frameSrc = $("#"+tabId).attr("src");
				if( frameSrc == ""
						|| frameSrc == null
						|| typeof frameSrc == "undefined"){
					$("#"+tabId).attr("src" , frameSrcs[tabId]);
				}else if(tabId == "confirm"){
					var confirmContent = document.getElementById("confirm").contentWindow;
					if(confirmContent 
							&& typeof confirmContent.refreshData == "function"){
						confirmContent.refreshData();
					}
				}
			}
		});
		tabObj.selectTabItem("filter");
	}
	
	function initButtons(buttons){
		$(".form-bar-inner").empty();
		BIONE.addFormButtons(buttons);
	}
	
	function saveHandler(){
		if(row == null
				|| row == ""
				|| col == null
				|| col == ""
				|| !window.parent.Design){
			BIONE.closeDialog("idxFiltDialog");
			return;
		}
		var currSheet = window.parent.Design.spread.getActiveSheet();
		// row , col , filterInfos
		var filterResults = {
				row : row,
				col : col,
				filterInfos : generateFilterInfos()
		};
		BIONE.closeDialog("idxFiltDialog" , null , true , filterResults);
	}
	
	// 获取过滤信息
	function generateFilterInfos(){
		filterInfos = [];
		var filterWindow = document.getElementById("filter").contentWindow;
		if(filterWindow
				&& filterWindow.maintab
				&& filterWindow.maintab.getTabItemCount() > 0){
			var tabIdList = filterWindow.maintab.getTabidList();
			for(var i = 0 , l = tabIdList.length ; i < l ; i++){
				var tabIdTmp = tabIdList[i];
				var tabContent = filterWindow.document.getElementById(tabIdTmp+"frame").contentWindow;
				if(tabContent
						&& typeof tabContent.generateFilterInfo == "function"){
					var infosTmp = tabContent.generateFilterInfo();
					if(infosTmp){
						filterInfos.push(infosTmp);
					}
				}else
				{
					var base = eval("filterWindow.checkedTypeVals."+tabIdTmp);	
					if(base)
						filterInfos.push(base);
				}
			}
		}
		return filterInfos;
	}
	
	function closeHandler(){
		BIONE.closeDialog("idxFiltDialog");
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div id="mainTabId" style="width:100%;overflow: hidden;">
			<div tabid="filter" title="过滤配置"  >
				<iframe frameborder="0" id="filter" src=""></iframe> 
			</div>
			<div tabid="confirm" title="信息确认"  >
				<iframe frameborder="0" id="confirm" src=""></iframe> 
			</div>
		</div>
	</div>
</body>
</html>