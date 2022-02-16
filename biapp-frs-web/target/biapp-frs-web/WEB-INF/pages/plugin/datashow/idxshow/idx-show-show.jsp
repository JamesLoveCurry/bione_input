<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript" 
	src="${ctx}/js/contextMenu/jquery.contextMenu.min.js"></script>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/contextMenu/jquery.contextMenu.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx }/js/datashow/img/iconfont.css" />
<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<script type="text/javascript" src="${ctx}/js/numeral/numeral.js"></script>
<script type="text/javascript" src="${ctx}/js/underscore/underscore-min.js"></script>
<script type="text/javascript"
	src="${ctx}/js/bignumber/bignumber.min.js"></script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<style>
h3 {
    display: block;
    font-size: 1.17em;
    -webkit-margin-before: 1em;
    -webkit-margin-after: 1em;
    -webkit-margin-start: 0px;
    -webkit-margin-end: 0px;
    font-weight: bold;
}
</style>
<script>
	var busiType = "01";//业务类型默认为01
	var instanceId = "${instanceId}";
	var downdload = null;
	var dimNos = [];//多个指标的维度交集
	var columns = [];//表格列
	var measureFilterName = [];//度量单位中文名称
	var type = "";
	var grid = null;
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	var searchObj = {exeFunction : "initTree",searchType : "idx"};//默认执行initTree方法
	var draggingNode = null;
	var chooseIdx = [];
	var dimFilters = [];
	var panel = null;
	var srcCode ='';
	var initFlag = true;
	var queryResult = [];
	var chooseDim = "";
	var unit ={
		"01" : 1,
		"02" : 100,
		"03" : 1000,
		"04" : 10000,
		"05" : 100000000
	};
	$(function(){
		initExport();
		initPanel();
		initGrid();
		loadFav(instanceId);
	});
	function loadFav(instanceId) {
		$.ajax({
			cache : false,
			async : true,
			url: '${ctx}/report/frame/datashow/idx/store/favIdxInfo',
			type: 'post',
			data: {
				'instanceId': instanceId
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function(data) {
				chooseIdx = data.idxs;
				busiType = data.busiType;
				var ids = "DATE";
				$.each(data.favDim, function(i, n) {
					ids += ",";
					ids += n.id.dimNo;
				});
				window.chooseDim = ids;
				initIdx(true);
				$.each(data.filter, function(i, n) {
					if(!dimFilters[n.id.dimNo]){
						dimFilters[n.id.dimNo] = {
							checkIds: n.filterVal.split(","),
							selectedNodes: n.selectNodes
						}
					}
				});
				var date = data.drawDate;
				var selectedNodes =[];
				selectedNodes.push({
					id : date,
					text : date
				});
				selectedNodes.push({
					id : date,
					text : date
				});
				dimFilters["DATE"]={
					selectedNodes : selectedNodes
				};
				
			}
		});
	}
	
	//初始化标签函数
	function initPanel(){
		panel = $('#panel').exlabel({
			type: 'btn',
			text: 'text',
			value: 'id',
			isEdit : false,
			isInput : false,
			callback: {
				onClick : function(data,flag){
					if(data.id == "DATE"){
						return false;
					}
					labelClick(data,flag);
				}
		    }
		});
		panel.add({
			id : "DATE",
			text : "日期"
		});
	}
	
	function labelClick(e,flag){
		if(flag == true){
			var i=0;
			for(i=0;i<columns.length;i++){
				if(columns[i].businessType == "INDEX" || columns[i].businessType == "MEASURE"){
					break;
				}
			}
			columns.splice(i,0,{
				isSort: true,
				display: e.text,
				name: e.id,
				text : e.text,
				businessType : 'DIM',
				sortname : "$"+e.id,
				width: ((e.text.length + 5)* 12 > 120)? (e.text.length + 5) * 12  : 120
			});
		}else{
			var i=0;
			for(i=0;i<columns.length;i++){
				if(columns[i].name == e.id){
					break;
				}
			}
			columns.splice(i,1);
			//删除所选维度过滤
			delete dimFilters[e.id];	
		}
		setGridColumns();
	}
	
	function initGrid() {
		columns = [];
		columns.push({
			isSort: true,
			display: "日期",
			name: 'DATE',
			text : '日期',
			type : 'date',
			sortname: '$DATE',
			businessType : 'DIM',
			width: 120
		});
		grid = $('#maingrid').ligerGrid(
				{
					toolbar : {},
					width : '100%',
					columns : columns,
					usePager : true,
					checkbox : false,
					data :{
						Rows :[]
					},
					dataAction : 'local',
					allowHideColumn : false,
					delayLoad : true,
					onBeforeShowData : function() {
		
					},
					onAfterShowData : function() {
					},
					onAfterChangeColumnWidth : function() {
						$(".delBtn").each(
								function() {
									$(this)
											.css(
													"left",
													$(this).parent().parent()
															.width() - 10);
								})
					},
					mouseoverRowCssClass : null
				});
		var buttons = [{
			text : "查询",
			icon : "search3",
			operNo : "idx-show-search-tab2",
			click : function() {
				indexQuery();
			}
		},{
			text : "导出",
			icon : "export",
			operNo : "idx-show-export-tab2",
			click : function() {
				fexport();
			}
		},{
			text : "折线图",
			icon : "linechart",
			operNo : "idx-show-linechart-tab2",
			click : function() {
				if(result && result.length > 0){
					BIONE.commonOpenDialog("图表", "chartDialog", 900, 500,
							"${ctx}/report/frame/datashow/idx/chart?chartType=01" );
				}
			}
		},{
			text : "柱状图",
			icon : "barchart",
			operNo : "idx-show-barchart-tab2",
			click : function() {
				if(result && result.length > 0){
					BIONE.commonOpenDialog("图表", "chartDialog", 900, 500,
							"${ctx}/report/frame/datashow/idx/chart?chartType=02" );
				}
			}
		}];
		grid.setHeight($(document).height() - 160);
		BIONE.loadToolbar(grid, buttons);
		addDivToColumn();
	}
	
	function fexport(){
		if(window.queryResult.length <= 0){
			BIONE.tip("无可导出数据！");
			return;
		}
		var cols = {};
		var formates = {};
		var unit = {};
		for(var i in  columns){
			cols[columns[i].name] = columns[i].display; 
			if(columns[i].businessType == "DIM"){
				formates[columns[i].name] = "@";
			}
		}
		for(var j in chooseIdx){
			var format = "";
			var val = "";
			if(chooseIdx[j].dataType == "01"){
				format += "#,##";
			}
			if(chooseIdx[j].dataPrecision > 0){
				format += "0.";
				for(var h=0;h<chooseIdx[j].dataPrecision;h++){
					format += "0";
				}
			}
			if(chooseIdx[j].dataType == "02"){
				format += "%";
			}
			if(chooseIdx[j].dataType == "03"){
				format = "0";
			}
			formates[chooseIdx[j].detailId] = format;
			unit[chooseIdx[j].detailId] = chooseIdx[j].dataUnit;
		}
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/resultExport',
			type: 'post',
			data: {
				colums: JSON2.stringify(cols),
				result : JSON2.stringify(window.queryResult),
				formate: JSON2.stringify(formates),
				unit : JSON2.stringify(unit)
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在生成数据文件中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function(json) {
				if(json.fileName){
					var src = '';
					src = "${ctx}/report/frame/datashow/idx/download?fileName="+json.fileName;
					downdload.attr('src', src);
				}
				else{
					BIONE.tip("数据文件生成失败");
				}
			},
			error : function(result, b) {
			}
		});
	}
	function clone(obj){
		var o;
		switch(typeof obj){
		case 'undefined': break;
		case 'string'   : o = obj + '';break;
		case 'number'   : o = obj - 0;break;
		case 'boolean'  : o = obj;break;
		case 'object'   :
			if(obj === null){
				o = null;
			}else{
				if(obj instanceof Array){
					o = [];
					for(var i = 0, len = obj.length; i < len; i++){
						o.push(clone(obj[i]));
					}
				}else{
					o = {};
					for(var k in obj){
						o[k] = clone(obj[k]);
					}
				}
			}
			break;
		default:		
			o = obj;break;
		}
		return o;	
	}
	
	function addGridData(queryResult){ // 将搜索结果转换格式添加到grid中
		window.queryResult = queryResult;
		result = clone(queryResult);
		if(queryResult && queryResult.length > 0){
			for(var i=0;i<queryResult.length;i++){
				for(var j=0;j<chooseIdx.length;j++){
					var format = "";
					var val = "";
					if(chooseIdx[j].dataType == "01"){
						format += "0,";
					}
					if(chooseIdx[j].dataPrecision > 0){
						format += "0.";
						for(var h=0;h<chooseIdx[j].dataPrecision;h++){
							format += "0";
						}
					}
					if(chooseIdx[j].dataType == "02"){
						try{
							val = new BigNumber(queryResult[i][chooseIdx[j].detailId]).times(new BigNumber(100)).toNumber();
						}
						catch(e){
							val = 0;
						}
						
					}
					else{
						try{
							val = new BigNumber(queryResult[i][chooseIdx[j].detailId]).dividedBy(new BigNumber(unit[chooseIdx[j].dataUnit])).toNumber();
						}
						catch(e){
							val = 0; 
						}
					}
					var number = numeral(val);
					val = number.format(format);
					if(chooseIdx[j].dataType == "02"){
						val += "%";
					}
					result[i][chooseIdx[j].detailId] = val;
				}
			}
		}
		grid.sortedData = null;//added by fangjuan 20160601
		grid.set('data',{Rows : result, Total : queryResult.length});	
	}
	
		
	function indexQuery(){// 指标搜索
		var condition = {};
		var cols = {};
		var indexHis = [];
		condition.QueryType = "index";
		if(dimFilters["DATE"] && chooseIdx.length>0 ){
			var searchArg = [];
			var dimNo = [];
			for(var i=0;i<columns.length;i++){
				if(columns[i].businessType == "DIM"){
					var tmp = columns[i].name;
					dimNo.push(tmp);
					if(dimFilters[tmp]){
						if(tmp != "DATE"){
							searchArg.push({DimNo : tmp, Op : '=', Value : dimFilters[tmp].checkIds});
						}else{
							searchArg.push({DimNo : tmp, Op : '>=', Value : [dimFilters["DATE"].selectedNodes[0].id.split("-").join("")]});
							searchArg.push({DimNo : tmp, Op : '<=', Value : [dimFilters["DATE"].selectedNodes[1].id.split("-").join("")]});
						}
					}
				}
			}
			condition.DimNo = dimNo;
			
			if(chooseIdx.length > 0){
				var column = [];
				for(var tmp =0 ;tmp< chooseIdx.length; tmp++){
					var obj = {
							ColumNo : chooseIdx[tmp].detailId, 
							IndexNo : chooseIdx[tmp].indexNo,
							SearchArg : searchArg
					};
					var indexNo = "";
					if(chooseIdx[tmp].measureNo){
						obj.MeasureNo = chooseIdx[tmp].measureNo; 
					}
					column.push(obj);
					indexHis.push({indexNo: chooseIdx[tmp].indexNo, indexVerId: chooseIdx[tmp].indexVerId});
				}
				condition.Colums = column;
			}
			for(var tmp in columns){
				cols[columns[tmp].name] = columns[tmp].text;
			}
			searchJson = JSON2.stringify(condition);
			$.ajax({
				url: '${ctx}/report/frame/datashow/idx/search/result',
				type: 'post',
				data: {
					parameter: JSON2.stringify(condition),
					columns : JSON2.stringify(cols),
					indexNos: JSON2.stringify(indexHis),
					srcSysCode : window.srcCode,
					idxCols : JSON2.stringify(chooseIdx)
				},
				dataType: 'json',
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在查询数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success: function(json) {
					if (json && json.Code && json.Code == "0000") {
						result = json.Msg;
						addGridData(result);
					}else{
						BIONE.tip(json.Msg);
					}
				},
				error : function(result, b) {
				}
			});
		}else{
			BIONE.tip("查询日期未选择或未配置查询指标！");
		}
	}
	function initExport(){
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	}
	
	function renderLabelPad(){//重绘维度标签区
		panel.removeAll();
		panel.add({
			id :　"DATE",
			text : "日期"
		});
		panel.add({
			id :　"ORG",
			text : "机构编号"
		});
		var params = "";
		for(var i=0;i<dimNos.length;i++){
			params += dimNos[i] + ",";
		}
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/getDimNm',
			type: 'post',
			data: {
				dimNos: params
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function(json) {
				for(var i=0;i<dimNos.length;i++){
					if(dimNos[i] != "INDEXNO" && dimNos[i] != "DATE" && dimNos[i] != "ORG"){
						panel.add({
							id :　dimNos[i],
							text : json[dimNos[i]]
						});
					}
				}
				panel.selectNodes(chooseDim,true,true);
				panel.selectNodes(chooseDim,true,false);
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function initIdx(isRenderLabel,ids) {
		for(var i in chooseIdx){
			var idxCol = chooseIdx[i];
			//构造维度
			if(isRenderLabel){
				if(dimNos.length == 0){
					dimNos = idxCol.dims;
				}
				else{
					dimNos = _.intersection(dimNos, idxCol.dimNos);
				}
				
			}
			var businessType = "";
			if(idxCol.measureNo){//有度量
				businessType = "MEASURE";
			}else{
				businessType = "INDEX";
			}
			columns.push({
				isSort: true,
				display: idxCol.indexAlias,
				name: idxCol.detailId,
				indexNo : idxCol.indexNo,
				indexVerId : idxCol.indexVerId,
				businessType : 'MEASURE',
				width: ((idxCol.indexAlias.length * 12 + 80) > 120)? (idxCol.indexAlias.length * 12+80) : 120
			});
		}
		if(isRenderLabel){
			renderLabelPad();
		}
		//移除掉非公共的维度列
		syncDimCol();
		setGridColumns();
	}
	
	function setGridColumns(){
		window.queryResult = [];
		grid.set("data", {Rows:[]});
		grid.set("columns", columns);
		grid.reRender();
		addDivToColumn();
	}
	function addDivToColumn() {
		for (var i = 0; i < columns.length; i++) {
			var columnName = columns[i].name;
			var businessType = columns[i].businessType;
			addColBtn(columnName,businessType);
		}
	}

	function addColBtn(columnName,businessType) {
		var img = $("td[columnname=" + columnName
				+ "] .l-grid-hd-cell-inner > #delBtn");
		if (img.length == 0) {
			var $selectedLi = $("td[columnname=" + columnName
					+ "] .l-grid-hd-cell-inner ");
			var top = 0 - $(".l-panel-topbar").height()
			- $selectedLi.position().top + 13;
			if(businessType == "DIM"){
				var cofImg = $(
						"<img src='${ctx}/images/classics/icons/icons_filter.png' colId = '"+columnName+"' style='width:16px;height:16px' class='l-grid-dim-filter' />")
						.attr('title', '筛选');
				cofImg.click(function() {
					f_open_filter(columnName);
				});
				var cofdiv = $("<div id='cofdiv' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
				cofdiv.css("left", "10px").css(
						"top",
						top).css("width",
						"7px");
				cofdiv.append(cofImg);
				$selectedLi.append(cofdiv);
			}
			else{
				var cofImg = $(
						"<img src='${ctx}/images/classics/icons/cog.png' colId = '"+columnName+"' style='width:16px;height:16px' class='l-grid-dim-filter' />")
						.attr('title', '配置');
				cofImg.click(function() {
					BIONE.commonOpenDialog("指标配置", "attrEdit", 700, 500, 
							"${ctx}/report/frame/datashow/idx/config?id="+columnName);
				});
				var cofdiv = $("<div id='cofdiv' style='display:block;position: relative;cursor: pointer;z-index:5;'/>");
				cofdiv.css("left", "10px").css(
						"top",
						top).css("width",
						"7px");
				cofdiv.append(cofImg);
				$selectedLi.append(cofdiv);
			}
		}
		
		function f_open_filter(dimNo){
			if(dimNo != "DATE"){
				BIONE.commonOpenDialog("维度过滤", "chooseOrg", 400, 500, 
						"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=" + dimNo ,null, function(){
				});
			}else{
				BIONE.commonOpenDialog("维度过滤", "chooseOrg", 600, 420, 
						"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=" + dimNo ,null, function(){
				});
			}
		}
		
	}
	
	function getIdx(id){
		for(var i in chooseIdx){
			if(chooseIdx[i].detailId == id){
				return chooseIdx[i];
			}
		}
	}
	
	function setIdx(id,idxCol){
		for(var i in chooseIdx){
			if(chooseIdx[i].detailId == id){
				chooseIdx[i] = idxCol;
			}
		}
	}
	function syncLabel(){
		dimNos = [];
		for(var i in chooseIdx){
			if(dimNos.length == 0){
				dimNos = chooseIdx[i].dimNos.split(",");
			}else{
				dimNos = _.intersection(dimNos, chooseIdx[i].dimNos.split(","));
			}
		}
		renderLabelPad();
	}
	
	function syncDimCol(){
		//移除掉非公共的维度列
		for(var x =0;x<columns.length;x++){
			if(columns[x].businessType == 'DIM' && columns[x].name != "DATE"){
				var y = 0;
				for(y=0;y<dimNos.length;y++){
					if(columns[x].name == dimNos[y]){
						break;
					}
				}
				if(y >= dimNos.length){
					delete dimFilters[columns[x].name];
					columns.splice(x,1);
					x--;
				}
			}
		}
	}
	function showDimFilter(columnName){
	}
	
	function renderDimFilterPic(dimNo){
	}
</script>
</head>

<body>
		<div id="template.center">
			<div id="showConfig" style="height: 100%;width:100%;">
				<div id="content" style="height: 100%;width:100%;">
					<div class="l-form-tabs">
						<ul original-title="" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
							<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-hover" data-index="0">
								<a href="javascript:void(0)">
									<a d='title3' style = "color: #4bbdfb;padding-left: 20px;padding-right: 0px;">维度选择</a>
								</a>
							</li>
						</ul>
					</div>
					<div id="panel" style="width:99%;height:100px;margin:2px;">
					</div>
					<div id="table" class="frame" style="width: 100%;">
						<div class="win" style="margin-top: 2px;">
							<div id="maingrid" class="maingrid" oncopy="return false" oncut="return false"></div>
						</div>			
					</div>
				</div>
			</div>
		</div>
</body>
</html>