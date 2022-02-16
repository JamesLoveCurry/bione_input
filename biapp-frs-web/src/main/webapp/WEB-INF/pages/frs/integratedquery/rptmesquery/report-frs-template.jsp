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
	var busiLinePageUrl = "${ctx}/report/frame/design/cfg/busiline";

	var designUrl = "${ctx}/frs/integratedquery/rptmesquery/info/design";
	var frameHeight;
	
	
	var uptObj;
	var uptObjArr = null;
	var lines = [];
	var initFlag = false;
	
	var cssJson = "";
	
	var lineTmpIds;  // 条线对应的模板ID（保存但不退出时用）
	
	var clipboardLineId;
	
	$(function(){
		uptObj = window.parent.tmpInfo;
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
		$("#_mainTab").height(frameHeight);
		rptTab = $("#rptTab").ligerTab({
			onBeforeSelectTabItem : function(tabId){
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
		</div>
	</div>
</body>
</html>