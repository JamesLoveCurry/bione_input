<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="decorator" content="/template/template14.jsp" />
<script type="text/javascript">
	
	var rptTab;
	var canCloseFlag = false;
	var busiLinePageUrl = "/report/frame/design/cfg/busiline";

	var rptNum =  window.parent.rptNum;
	
	var designUrl = "${ctx}/report/frame/design/cfg/frsindex/edit/design";
	var frameHeight;
	
	var templateType = window.parent.templateType;
	
	var uptObj;
	var uptObjArr = null;
	var lines = [];
	var initFlag = false;
	
	var cssJson = "";
	
	var lineTmpIds;  // 条线对应的模板ID（保存但不退出时用）
	
	var clipboardLineId;
	
	$(function(){
		uptObj = window.parent.designInfo4Upt;
		if(uptObj != null){
			uptObjArr = [];
			var mainTmp = uptObj.mainTmp;
			var tmpArr = uptObj.tmpArr;
			if(mainTmp){
				uptObjArr["_mainTab"] = mainTmp;
			}
			if(tmpArr
					&& tmpArr.length){
				for(var i = 0 , j = tmpArr.length ; i < j ; i++){
					if(tmpArr[i].tmpInfo){						
						var lineId = tmpArr[i].tmpInfo.lineId;
						if(lineId == null
								|| lineId == ""
								|| typeof lineId == "undefined"){
							continue;
						}
						uptObjArr[lineId] = tmpArr[i];
					}
				}
			}
			if(uptObj.lines
					&& uptObj.lines.length){
				for(var m = 0 , n = uptObj.lines.length ; m < n ; m++){
					var lineTmp = uptObj.lines[m];
					lines[lineTmp.lineId] = lineTmp.lineNm;
				}
			}
			window.parent.designInfo4Upt = null;
		}
		frameHeight = $("#center").height() - 30;
		// 初始化tab
		initRptTab();
	})
	
	// 初始化报表类型tab
	function initRptTab(){
		if(templateType != "02"){
			// 不是单元格类报表，不允许添加业务条线
			$("div[tabid='_addTab']").remove();
		}
		$("#_mainTab").height(frameHeight);
		rptTab = $("#rptTab").ligerTab({
			onBeforeSelectTabItem : function(tabId){
				if(tabId == "_addTab"){
					// 新增业务类型子模板
					BIONE.commonOpenDialog('添加业务条线','busiLineDialog'
							,$(window.parent).width() * 0.7
							,$(window.parent).height() * 0.7
							,"${ctx}"+busiLinePageUrl+'?d='+new Date().getTime());
					return false;
				}
				if(initFlag === false){
					return false;
				}
				var urlTmp = designUrl;
				if(tabId != "_mainTab"){
					urlTmp += ("?lineId="+tabId);
				}
				var frameSrc = $("#"+tabId).attr("src");
				if( frameSrc == ""
						|| frameSrc == null
						|| typeof frameSrc == "undefined"){
					$("#"+tabId).attr("src" , urlTmp);
				}
			},
			onAfterSelectTabItem : function(tabId){
				
			},
			onBeforeRemoveTabItem : function(tabId){
				if(canCloseFlag === false){					
					var textTmp = $("li[tabId='"+tabId+"']").children("a").html();
					$.ligerDialog.confirm("确实要删除业务条线：【"+textTmp+"】吗?", function(yes) {
						if(yes === true){
							canCloseFlag = true;						
							rptTab.removeTabItem(tabId);
						}
					});
					return false;
				}
				canCloseFlag = false;
				return true;
			},
			onAfterRemoveTabItem : function(tabId){
				if(uptObjArr != null
						&& tabId in uptObjArr){
					delete uptObjArr[tabId];
				}
				rptTab.selectTabItem("_mainTab");
			}
		});
		//2014-09-30修改
		//$("li[tabid='_addTab']").width("22");
		initFlag = false;
		if(uptObjArr != null){
			var tabCount = 0;
			for(var i in uptObjArr){
				if( i == "_mainTab"){
					tabCount++;
					continue;
				}
				if(!(i in lines)){
					// 为主模板或业务条线未定义
					continue;
				}
				rptTab.addTabItem({
					tabid : i,
					text : lines[i],
					showClose : true,
					content : '<iframe frameborder="0" id="'+i+'" style="height:'+frameHeight+'"></iframe>',
					height : frameHeight
				});
				rptTab.moveTabItem(i , "_addTab");
				tabCount++;
			}
		}		
		initFlag = true;
		rptTab.selectTabItem("_mainTab");
	}
	
	function syncCssJson(json){
		if(json == null
				|| json == ""
				|| typeof json == "undefined"){
			return ;
		}
		cssJson = json;
	}
	
	// 准备待保存数据
	function prepareDatas4Save(){
		var saveObj = {};
		var saveObjArr = [];
		var tabIds = rptTab.getTabidList();
		for(var i = 0 , j = tabIds.length ; i < j ; i++){
			var tabId = tabIds[i];
			if(tabId == "_addTab"){
				continue;
			}
			var frameTmp = document.getElementById(tabId).contentWindow;
			if(frameTmp
					&& typeof frameTmp.prepareDatas4Save == "function"){
				var saveObjTmp = frameTmp.prepareDatas4Save();
				if(saveObjTmp){
					var dataTmp = {};
					dataTmp.lineId = (saveObjTmp.lineId == null || typeof saveObjTmp.lineId == "undefined") ? "" : saveObjTmp.lineId;
					dataTmp.templateId = (saveObjTmp.templateId == null || typeof saveObjTmp.templateId == "undefined") ? "" : saveObjTmp.templateId;
					dataTmp.tmpJson = (saveObjTmp.jsonStr == null || typeof saveObjTmp.jsonStr == "undefined") ? "" : saveObjTmp.jsonStr;
					dataTmp.tmpRemark = (saveObjTmp.remark == null || typeof saveObjTmp.remark == "undefined") ? "" : saveObjTmp.remark;
					dataTmp.idxsArray = saveObjTmp.rptIdxs;
					dataTmp.rowCols = saveObjTmp.rowCols;
					dataTmp.cellsArray = saveObjTmp.cellsArray;
					dataTmp.changeArray = saveObjTmp.changeArray;
					if(tabId == "_mainTab"){
						saveObj.mainTmp = dataTmp;
					}else{						
						saveObjArr.push(dataTmp);
					}
				}
			}else{
				if(uptObjArr != null
						&& tabId in uptObjArr){
					var objTmp = uptObjArr[tabId];
					// 修改时，待保存tab，未点击处理 
					if(objTmp && objTmp.tmpInfo){
						var dataTmp = {};
						dataTmp.lineId = (objTmp.tmpInfo.lineId == null || typeof objTmp.tmpInfo.lineId == "undefined") ? "" : objTmp.tmpInfo.lineId;
						dataTmp.templateId = (objTmp.tmpInfo.id.templateId == null || typeof objTmp.tmpInfo.id.templateId == "undefined") ? "" : objTmp.tmpInfo.id.templateId;
						dataTmp.tmpJson = (objTmp.tmpInfo.templateContentjson == null || typeof objTmp.tmpInfo.templateContentjson == "undefined") ? "" : objTmp.tmpInfo.templateContentjson;
						dataTmp.tmpRemark = (objTmp.tmpInfo.remark == null || typeof objTmp.tmpInfo.remark == "undefined") ? "" : objTmp.tmpInfo.remark;
						var idxArray = [];
						generateAllIdxs(idxArray , objTmp.formulaCells , "04");
						generateAllIdxs(idxArray , objTmp.idxCalcCells , "05");
						generateAllIdxs(idxArray , objTmp.idxCells , "03");
						generateAllIdxs(idxArray , objTmp.moduleCells , "02");	
						generateAllIdxs(idxArray , objTmp.staticCells , "06");
						dataTmp.idxsArray = idxArray;
						dataTmp.rowCols = typeof objTmp.rowCols == "undefined" ? [] : objTmp.rowCols;
						if(tabId == "_mainTab"){
							saveObj.mainTmp = dataTmp;
						}else{						
							saveObjArr.push(dataTmp);
						}
					}					
				}
			}
		}
		saveObj.saveObjArr = saveObjArr;
		return saveObj;
	}
	
	function generateAllIdxs(idxArray , baseArray , cellType){
		if(idxArray
				&& baseArray
				&& baseArray.length > 0){
			for(var i = 0 , j = baseArray.length ; i < j ; i++){
				baseArray[i].cellType = cellType;
				idxArray.push(baseArray[i]);
			}
		}
	}

</script>
</head>
<body>
	<div id="template.center">
		<div id="rptTab" style="width:100%;overflow: hidden;">
			<div tabid="_mainTab" title="主模板"  >				
				<iframe frameborder="0" id="_mainTab" ></iframe> 
			</div>
			<!-- <div tabid="_addTab"   title="+"  ></div> -->
		</div>
	</div>
</body>
</html>