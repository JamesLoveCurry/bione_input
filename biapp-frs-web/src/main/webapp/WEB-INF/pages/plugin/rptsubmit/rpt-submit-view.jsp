 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/spreadjs_load.jsp"%>


<script type="text/javascript">
	var obj;
	var btn;
	var posX=0,posY=0;
	var bX=0,bY=0;
	var canDrg=false;
	var searchJson="";
	var cursearchArgs=[];
	var x, y,rw,rh;
	var rptType="${rptType}";
	var total=0;
	var Utils=null;
	var View=null;
	var info=window.parent.info?window.parent.info:window.parent;
	var Pagination=null
	var searchJson = "";
	var oldVal = 1;
	var isAutoAdj = "${isAutoAdj}";
	var dsId = "${dsId}";
	var dimMap = ${dimMap};
	var searchMap = {};
	var templateId = "${templateId}";
	var rptId = "${rptId}";
	$(function() {
		if(window.parent.info==null)
			info.view=window;
		initData();
		initToolbar();
		initSpread();
	});
	
	function initData(){
		var searchArgs = JSON2.parse('${params}');
		for(var i in dimMap){
			if(i != "INDEXNO")
			searchMap[dimMap[i]] = null;
			for(var j in searchArgs){
				if(searchArgs[j].name == i){
					searchMap[dimMap[i]] = searchArgs[j].value;
				}
			}
		}
	}
	
	function initToolbar(){
		$("#toolbar").ligerToolBar({
			items: [{ 
					text: '保存', 
					click: f_save,
					icon:'save'
				},{ 
					text: '还原', 
					click: f_replay,
					icon:'refresh2'
				},{ 
					text: '日志', 
					click: f_showLog,
					icon:'bluebook'
				}]
		});
		$(".l-toolbar").css("background","none").css("height","30px");
	}
	
	function f_save(){
		var cells = View.generateChangeInfo().cells;
		if(cells.length <= 0){
			BIONE.tip("未存在变更数据！");
			return;
		}
		var cols = []
		for(var i in searchMap){
			if(searchMap[i] != null && typeof searchMap[i] != "string" && searchMap[i].length == 1){
				searchMap[i] = searchMap[i][0];
			}
		}
		for(var i in dimMap){
			if(i != "INDEXNO"){
				cols.push(dimMap[i]);
			}
		}
		if(View.templateType == "02"){
			for(var i in searchMap){
				if(searchMap[i] == null || (typeof searchMap[i] != "string" && searchMap[i].length != 1)){
					BIONE.tip("未指定所有查询条件唯一，无法填报");
					return;
				}
			}
			for(var i in cells){
				cells[i]["searchMap"] = searchMap;
			}
		}
		else if(View.templateType == "04" || View.templateType == "05"){
			for(var i in cells){
				var searchJson = getSearchJson(cells[i].cellNo,View.templateType);
				var searchInfo = {};
				for(var j in searchMap){
					if(searchJson[j] != null){
						searchInfo[j] = searchJson[j];
					}
					else{
						searchInfo[j] = searchMap[j];
					}
				}
				if(i==0){
					for(var h in searchInfo){
						if(searchInfo[h] == null || (typeof searchInfo[h] != "string" && searchInfo[h].length != 1)){
							BIONE.tip("未指定所有查询条件唯一，无法填报");
							return;
						}
					}
				}
				cells[i]["searchMap"] = searchInfo;
			}
		}
		else{
			return;
		}
		
		$.each(cells,function(i,cell){//修数保存立马变色，暂且不考虑指标是否落地，不落地不会保存成功
			var currSheet = View.spread.getActiveSheet();
			var currCell = Utils.posiToRowCol(cell.cellNo);
			var cellObj = currSheet.getCell(currCell.row,currCell.col);
			cellObj.backColor("#f5780a");
		});
		
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/rptsubmit/save",
			dataType : 'json',
			type : "post",
			data :{
				cells : JSON2.stringify(cells),
				dsId : dsId,
				cols : JSON2.stringify(cols),
				tmpId : templateId,
				rptId : rptId,
				searchObj : JSON2.stringify(searchMap)
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在保存数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(res) {
				BIONE.tip("保存成功");
			}
		});
	}
	
	function f_replay(){
		View.reset();
	}
	
	function f_showLog(){
		var curCellNo = "";
		if(View.curCellNo){
			curCellNo = View.curCellNo;
		}
		var queryCond = "";
		for(var pt in searchMap){
			if(searchMap[pt] != null){
				if(queryCond != ""){
					queryCond += "-" + searchMap[pt];
				}else{
					queryCond += searchMap[pt];
				}
			}
		}
		window.parent.BIONE.commonOpenDialog("修数日志信息", "showLog", 900,
				400, "${ctx}/rpt/frame/rptsubmit/showLog?rptId="+rptId+"&queryCond="+queryCond+"&curCellNo="+curCellNo )
	}
	
	function initSpread(){
		var searchArgs = JSON2.parse('${params}');
		if(window.parent.info!=null)
			var searchArgs =info.searchArgs;
		require.config({
			baseUrl :'${ctx}' + "/js/",
			paths: {
				"view" : "report/show/views/rptview",
				"utils" : "report/cfg/utils/designutil"
			}
		});
		var height=($(window).height())-30;
		
		var ajaxData={
				rptId : "${rptId}",
				dataDate : "${dataDate}",
				orgNm : "${orgNm}",
				unit : "${unit}"
		}
		if(rptType=="01"){
			
			ajaxData.start=1;
			ajaxData.step=info.pageSize;
		}
		var record = getModCellRecord();
		var forColorCells = {};
		forColorCells.themeType = "rptSubmit";
		forColorCells.colorCells = record.length > 0 ? record.join(",") : null;
		forColorCells.cellsColor = "#f5780a";
		require(["view","utils"] , function(view,utils){
			Utils = utils;
			var autoAdj = false;
			if(isAutoAdj == "Y"){
				autoAdj = true;
			}
			var settings = {
				targetHeight : height,
				ctx : '${ctx}',
				readOnly : false ,
				cellDetail: false,
				url : '/rpt/frame/rptsubmit/getSubmitInfo',
				initFromAjax : true,
				searchArgs : JSON2.stringify(searchArgs),
				ajaxData: ajaxData,
				autoAdj : autoAdj,
				forColorCells : forColorCells
			};
			spread = view.init($("#spread") , settings);
			View=view;
		});
		
		function cellDoubleClick(args){
			if(View.templateType == "01"){
				return;
			}
			if(args){
				cursearchArgs=[];
				for(var i in searchArgs){
					cursearchArgs.push(searchArgs[i]);
				}
				if(View.templateType == "02"){
					searchJson = "";
				}
				else{
					searchJson = View.searchJson;
				}
				var cellInfo = View.cellInfo;
				if(args.sheetArea
						&& args.sheetArea != 3){
					// 只有选中单元格区域才进行后续动作
					return ;
				}
				var currSheet = spread.getActiveSheet();
				var selRow = currSheet.getActiveRowIndex();
				var selCol = currSheet.getActiveColumnIndex();
				var colCount = currSheet.getColumnCount();
				var labelTmp = Utils.initAreaPosiLabel(selRow , selCol);
				if(!cellInfo[labelTmp].indexNo)
					return;
				var indexNo = cellInfo[labelTmp].indexNo;
				for(var i =0 ;i <colCount; i++){
					labelTmp = Utils.initAreaPosiLabel(selRow , i);
					if( cellInfo[labelTmp] &&  cellInfo[labelTmp].dimTypeNo){
						var arg ={
							name : cellInfo[labelTmp].dimTypeNo,
							value : cellInfo[labelTmp].srcValue
						};
						cursearchArgs.push(arg);
					}
				}
				BIONE.commonOpenSmallDialog('指标追溯', 'idxAnalysis', '${ctx}/rpt/frame/idx/idxanalysis/idxView?id='+indexNo);
			}
			
		}
	}	
	
	function getSearchJson(labelTmp,type){
		var searchJson = {}
		var cellInfo = View.cellInfo;
		var currSheet = spread.getActiveSheet();
		var cell = Utils.posiToRowCol(labelTmp);
		var selRow = cell.row;
		var selCol = cell.col;
		var colCount = currSheet.getColumnCount();
		var rowCount = currSheet.getRowCount();
		if(type == "04"){
			for(var i =0 ;i <colCount; i++){
				labelTmp = Utils.initAreaPosiLabel(selRow , i);
				if( cellInfo[labelTmp] &&  cellInfo[labelTmp].dimTypeNo){
					searchJson[dimMap[cellInfo[labelTmp].dimTypeNo]] = cellInfo[labelTmp].srcValue;
				}
			}
		}
		if(type == "05"){
			for(var i =0 ;i <rowCount; i++){
				labelTmp = Utils.initAreaPosiLabel(i , selCol);
				if( cellInfo[labelTmp] &&  cellInfo[labelTmp].dimTypeNo){
					searchJson[dimMap[cellInfo[labelTmp].dimTypeNo]] = cellInfo[labelTmp].srcValue;
				}
			}
		}
		return searchJson;
	}
	function getJson(){
		return View.getJSON();
	}
	
	function initTool(){
		if(window.parent.info==null)
			info.searchFlag=true;
			info.$(".iconfont-disabled").removeClass("iconfont-disabled");
	}
	
	function getModCellRecord(){
		var queryCond = "";
		var hisCells;
		for(var pt in searchMap){
			if(searchMap[pt] != null){
				if(queryCond != ""){
					queryCond += "-" + searchMap[pt];
				}else{
					queryCond += searchMap[pt];
				}
			}
		};
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frame/rptsubmit/getModCells",
			dataType : 'json',
			type : "GET",
			data : {"queryCond":queryCond,"rptId":rptId},
			success : function(data) {
				hisCells = data.cells;
			}
		});
		return hisCells;
	}
	
</script>
</head>
<body>
	<div id="toolbar"></div>
	<div id="spread"></div>
	
</body>
</html>