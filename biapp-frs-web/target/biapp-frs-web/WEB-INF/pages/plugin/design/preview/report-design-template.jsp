<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="decorator" content="/template/template14.jsp" />
<script type="text/javascript">
	
	var canCloseFlag = false;

	var designUrl = "${ctx}/report/frame/tmp/view/design";
	var frameHeight;
	
	
	var uptObj;
	var uptObjArr = null;
	var lines = [];
	var initFlag = false;
	
	var cssJson = "";
	
	var lineTmpIds;  // 条线对应的模板ID（保存但不退出时用）
	
	var clipboardLineId;
	
	$(function(){
		frameHeight = $("#center").height() - 10;
		// 初始化tab
		initRptTab();
	})
	// 初始化报表类型tab
	function initRptTab(){
		$("#_mainTab").height(frameHeight);
		var urlTmp = designUrl;
		$("#_mainTab").attr("src" , urlTmp);
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
		<iframe frameborder="0" id="_mainTab" style="width:100%;"></iframe> 
	</div>
</body>
</html>