/*******************************************************/
/****             报表查询入口JS文件               ****/
/******************************************************/

var View = {};

require.config({
	paths:{
		"celldetail" : "cfg/views/celldetail",
		"SelectionModule" : "cfg/modules/SelectionModule",
		"utils" : "cfg/utils/designutil"
	}
});

View.spread = {};

View.spreadDom = {};

View.Utils = {};

View._Json;

View._timeoutHandler;

View._defaults = {
	searchJson : "",
	searchArgs : null , // 查询条件 , 格式:{dim1:[val1,val2],dim2:[val1],...}(同查询引擎)
	targetHeight : null , // target总高度
	ctx : "" ,  // 所处jsp的全局contextpath 
	readOnly : true , // 是否只读模式(若是只读模式，单元格不可编辑 )
	cellDetail : true	 ,  // 是否有单元格明细
	canUserEditFormula : true , // 是否可以编辑公式
	tabStripVisible : false , // 是否显示底部sheets' tab信息
	autoAdj: false,
	// ajax获取数据
	visibleCellNos : null , // 可见的单元格编号集合
	cellInfo : [],
	inValidMap : null , // 校验未通过的信息集合，格式: [{cellNo:单元格编号,remindColor:提醒颜色(6位)} , {...}]
	initFromAjax : true , // 自动ajax后台获取数据
	url : "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),
	methodType : "POST",
	ajaxData : {} , // ajax参数，目前支持rptId:报表id；dataDate:数据日期 ;busiLineId 业务条线Id,fileName 文件
	templateType : "",
	// events 
	onEnterCell : null , // 选中单元格事件
	onLeaveCell : null , // 移出单元格事件
	onCellDoubleClick : null ,  // 双击单元格事件
	onEditEnded : null , // 编辑完成事件
	useHTMLSheet : false,  // 是否使用HTML格式的表格 -- MAXL: show spread on IE 6-8
	forColorCells : null //报表修数修改过数的单元格
};

View._settings = {};

View._cellInfos = {};

// 需要被记录进页面内存中的单元格类型
// 目前暂只需要记录【指标单元格】、【数据集单元格】和【表间计算单元格】
View._validCellType = ["02" , "03" , "04" , "05","07","08"];

View._SelectionModule = {};

/**
 * 初始化方法
 */
View.init = function(targetSelector , settings){
	var target = null;
	if(targetSelector == null 
			|| (typeof targetSelector != "object"
				&& typeof targetSelector != "string")){
		return {};
	}
	if(typeof targetSelector == "string"){
		target = $(targetSelector);
	} else {
		target = targetSelector;
	}
	//参数处理
	View._settings = View._initSettings(settings);
	// MAXL: show spread on IE 6-8
	if (! View._settings.useHTMLSheet && $.browser.msie && parseInt($.browser.version, 10) >= 9) {
		initSlcanvas(View._settings.ctx);
	}
	// initSlcanvas(View._settings.ctx);
	//keydown事件
	View.doKeyDown();
	return View._settingHandler(target, View._settings);	
};

View._initSettings = function(settings){
	// 初始化参数
	var properties = View._defaults;
	if(settings
			&& typeof settings == "object"){
		for(var p in settings){
			if(p in properties){
				properties[p] = settings[p];
			}
		}
	}
	return properties;
};

View._settingHandler = function(target , settings){
	if(View._settings.targetHeight != null
			&& View._settings.targetHeight != ""){
		target.height(View._settings.targetHeight);
	}
	View.spreadDom  = View. _initSpreadDoom(target);
	
	// 初始化spread上下文环境
	View.spreadDom.wijspread();
	View.spread = View.spreadDom.wijspread("spread");
	// MAXL: show spread on IE 6-8
	if (View._settings.useHTMLSheet && $.browser.msie && parseInt($.browser.version, 10) < 9) {
		View.target = target;
		initSpread();
		return View.spread;
	}
	if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
        //run for ie7/8
        View.spread.bind("SpreadsheetObjectLoaded", function () {
             initSpread();
        });
    } else {
        initSpread();
    }
	
	function initSpread(){
		View.spread.setSheetCount(1);
		View.spread.useWijmoTheme = true;
		View.spread.newTabVisible(false);
		View.spread.canUserEditFormula(settings.canUserEditFormula);
		View.spread.highlightInvalidData(true);
		View.spread.getSheet(0).isPaintSuspended(true);
		View.spread.getSheet(0).isPaintSuspended(false);
		View.spread.tabStripVisible(View._settings.tabStripVisible);
		View.spread.scrollbarMaxAlign(true);
		// 初始化事件
		View._initViewportEvents(View.spread);
		// 初始化依赖组件
		require(["SelectionModule" , "celldetail"  , "utils" ] , function(SelectionModule , Detail  ,  Utils){
			if(View._settings.cellDetail === true){			
				View._cellDetailDom = Detail.initCellDetail($("#_spreadCellDetail") , View.spreadDom);
			}
			View._SelectionModule = SelectionModule;
			View.Utils = Utils;
			if(View._settings.cellDetail === true){			
				if(ko != null
						&& typeof ko == "object"){			
					View._SelectionModule = SelectionModule.newInstance();
					ko.applyBindings(View._SelectionModule , $("#_cellDetailBar")[0]);
				}
			}
			View._initViewEvents();
			View.resize(View.spread);
			View.ajaxJson(null,null,true);
			;
		});
	}
	return View.spread;
};

View.ajaxJson=function(inValidMap,fileName,init,start,step){
	// 初始化报表指标数据
			if(View._settings.initFromAjax === true
					&& View._settings.ctx != null
					&& View._settings.ctx != ""
					&& View._settings.url != null
					&& View._settings.url != ""){
				var ajaxData = View._settings.ajaxData;
				
				if(ajaxData == null){
					ajaxData = {};
				}
				ajaxData.isInit=init;
				ajaxData.readOnly = View._settings.readOnly;
				if(View._settings.searchArgs
						&& View._settings.searchArgs != ""){					
					ajaxData.searchArgs = View._settings.searchArgs;
				}
				if(View._settings.visibleCellNos
						&& View._settings.visibleCellNos != ""){
					ajaxData.visibleCellNos = View._settings.visibleCellNos;
				}
				if(inValidMap!=null){
					ajaxData.inValidMap = inValidMap;
					View._settings.inValidMap=inValidMap;
				}
				else if(View._settings.inValidMap
						&& View._settings.inValidMap != ""){
					ajaxData.inValidMap = View._settings.inValidMap;
				}
				if(start!=null){
					ajaxData.start=start;
				}
				if(step!=null){
					ajaxData.step=step;
				}
				$.ajax({
					cache : false,
					async : true,
					url : View._settings.ctx + View._settings.url,
					dataType : 'json',
					data : ajaxData,
					type : View._settings.methodType,
					beforeSend : function() {
						BIONE.showLoading();
					},
					success : function(result){
						if(result){
							var errorMsg = result.error;
							if(errorMsg != null
									&& errorMsg != ""&& errorMsg != "noinit"
									&& typeof errorMsg != "undefined"){
								BIONE.tip(errorMsg);
							}else if(errorMsg == "noinit"){
								var celldsInfo = result.cellDsInfo;
								var formulas = result.formula;
								var cellInfo=null;
								for(var key in celldsInfo){
									cellInfo=celldsInfo[key];
									break;
								}
								size=View._settings.ajaxData.step;
								View.resetContext(cellInfo,formulas,size);
							}
							else{			
								if(init!=null&&init==true){
									View._Json = result.json;
								}
								if(typeof rptType != "undefined"  && rptType=="01"){
									var total = result.total;
									if(init)
										initPagination(total);
								}
								View.searchJson = result.searchjson;
								View.templateType = result.templateType;
								View.cellInfo = result.cellInfo;
								var jsonStr = result.json;
								var cellInfo = result.cellInfo;
								var dsInfo = result.dsInfo;
								View._initCellInfos(cellInfo , dsInfo);
								View.fromJSON(jsonStr);
								if(typeof initTool == "function")
									initTool();
							}
						}
						BIONE.hideLoading();
					},
					error:function(){
						BIONE.hideLoading();
						BIONE.tip("数据加载异常，请联系系统管理员");
					},
					complete: function(){
						BIONE.hideLoading();
					}
				});
			}
}
View.resetContext=function(cellValues,formulas,size){
	var currSheet = View.spread.getActiveSheet();
	var total=0;
	for(var key in cellValues){
		if(key!="Total"&&key!="Key"){
			var rowCol = View.Utils.posiToRowCol(key);
			for(var i=0;i<size;i++){
				total=cellValues[key].length;
				if(cellValues[key].length-1>=i){
					try{
						View._setCellValue(String(parseInt(rowCol.row)+i),rowCol.col,cellValues[key][i],currSheet);
					}
					catch(e){
					}
				}
				else{
					try{
						View._setCellValue(String(parseInt(rowCol.row)+i),rowCol.col,"",currSheet);
					}
					catch(e){
					}
				}
			}
		}
	}
	for(var key in formulas){
		var rowCol = View.Utils.posiToRowCol(key);
		for(var i=0;i<size;i++){
			if(total-1>=i){
				try{
					var cell = currSheet.getCell(parseInt(rowCol.row)+i , parseInt(rowCol.col));
					cell.value("");
					cell.formula(formulas[key][i]);
				}
				catch(e){
				}
			}
			else{
				try{
					View._setCellValue(String(parseInt(rowCol.row)+i),rowCol.col,"",currSheet);
					var cell = currSheet.getCell(parseInt(rowCol.row)+i , parseInt(rowCol.col));
					cell.value("");
					cell.formula("");
				}
				catch(e){
				}
			}
		}
	}
	
	if(View._settings.autoAdj)
		View._initColInfo(View.spread);
	else if(View.templateType == "01")
		View._initRowInfo(View.spread);
	currSheet.invalidate();
}

View._setCellValue = function(row , col , val , sheet){
	if(!sheet){
		sheet = Design.spread.getActiveSheet();
	}
	if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
		sheet.setValue(row , col , val , $.wijmo.wijspread.SheetArea.viewport , true);
	}else{
		var model = sheet._getModel($.wijmo.wijspread.SheetArea.viewport);
		model.setValue(row , col , val);
	}
}

View.reset=function(){
	View.fromJSON(View._Json);
}
View._initCellInfos = function(cellInfo , dsInfo){
	var tmp = {};
	if(cellInfo
			&& typeof cellInfo == "object"
			&& dsInfo 
			&& typeof dsInfo == "object"){		
		for(var posi in cellInfo){
			var cellTmp = cellInfo[posi];
			if(dsInfo[cellTmp.cellNo] != null
					&& typeof dsInfo[cellTmp.cellNo] != "undefined"
					&& jQuery.inArray(dsInfo[cellTmp.cellNo].type , View._validCellType) != -1){
				var value=cellTmp.value;
				var cellNo=cellTmp.cellNo;
				jQuery.extend(cellTmp , dsInfo[cellTmp.cellNo]);
				cellTmp.value=value;
				cellTmp.cellNo=cellNo;
				tmp[posi] = cellTmp;
			}
		}
	}
	View._cellInfos = tmp;
	var currSheet = View.spread.getActiveSheet();
	currSheet.getColumns(0, currSheet.getColumnCount($.wijmo.wijspread.SheetArea.rowHeader) - 1, $.wijmo.wijspread.SheetArea.rowHeader).visible(false);
	currSheet.getRows(0, currSheet.getRowCount($.wijmo.wijspread.SheetArea.colHeader) - 1, $.wijmo.wijspread.SheetArea.colHeader).visible(false);
	for(var posi in View._cellInfos){
		var cellTmp = View._cellInfos[posi];
		var rowCol = View.Utils.posiToRowCol(posi);
		if(cellTmp.unit!=null&&cellTmp.unit!=""&&cellTmp.unit!="00"){
				//currSheet.setValue(rowCol.row , rowCol.col,1);
			
		}
			
	}
}

View._initViewEvents = function(){
	if(View._settings.cellDetail === true
			&& View._cellDetailDom){
		View._cellDetailDom.bind("focusout" , function(){
			if(View.spread){				
				var valNew = View._cellDetailDom.html();
				var currSheet = View.spread.getActiveSheet();
				currSheet.setValue(View._SelectionModule.get("positionX") , View._SelectionModule.get("positionY") , valNew);
			}
		});
	}
}

View. _initSpreadDoom = function(target){
	//初始化设计器doom相关
	var spreadDom = target;
	var heightTmp = spreadDom.height();
	var cellDetailDom ;
	if(View._settings.cellDetail === true){
		cellDetailDom = "<div id='_spreadCellDetail' style='width=100%;'></div>";
	}
	if(cellDetailDom != null){
		var domInner = "";
		domInner += cellDetailDom ? cellDetailDom : "";
		domInner += "<div id='_spread' style='100%'></div>";
		target.append(domInner);
		spreadDom = $("#_spread");
		spreadDom.height(heightTmp);
	}
	return spreadDom;
}

View._initValidates = function(spread){
	// 选择单元格处理
	if(!spread){
		return ;
	}
	var currSheet = spread.getActiveSheet();
	var tag="";
	if($.browser.msie && parseInt($.browser.version, 10) < 9){
		tag="\n";
	}
	else{
		tag="<br>";
	}
	if(View._settings.readOnly != true){
		for(var i in View._cellInfos){
			if(View._cellInfos[i].isPri=="Y"
					&& "04" != View._cellInfos[i].type){
				var formula=[];
				var inputMessage=[];
				if(View._cellInfos[i].unit!=null&&View._cellInfos[i].unit!=""&&View._cellInfos[i].unit!="00"){
					inputMessage.push("该单元格必须填写数字");
					formula.push("IF(IF(ISBLANK("+i+"),0,VALUE("+i+"))=0,TRUE,VALUE("+i+"))");
				}
				if(View._cellInfos[i].isNull=="N"){
					formula.push("IF(ISBLANK("+i+"),FALSE,TRUE)");
					inputMessage.push("该单元格不能为空");
				}
				if(formula.length>0){
					var dv= new $.wijmo.wijspread.DefaultDataValidator.createFormulaValidator("=AND("+formula.join(",")+")");
					dv.inputMessage = inputMessage.join(tag);
					dv.inputTitle="提示：";
					var rowCol = View.Utils.posiToRowCol(i);
					//currSheet.setDataValidator(rowCol.row, rowCol.col, dv);
					View._setDataValidator(rowCol.row , rowCol.col , dv);
				}
				
			}
		}
		View.spread.getActiveSheet().invalidate();
	}
}

View._initViewportEvents = function(spread){
	// 选择单元格处理
	if(!spread){
		return ;
	}
	var currSheet = spread.getActiveSheet();
	
	currSheet.bind($.wijmo.wijspread.Events.EditEnded , function(sender , args){
		if(args){
			if(View._settings.onEditEnded != null
					&& typeof View._settings.onEditEnded != "undefined"){
				View._settings.onEditEnded(sender , args);
			}
		}
	});
	
	currSheet.bind($.wijmo.wijspread.Events.SelectionChanged , function(sender , args){
		if(View._SelectionModule.set
				&& typeof View._SelectionModule.set == "function"){
			var posiNew = View.Utils.initAreaPosiLabel(View._SelectionModule.get("positionX") , View._SelectionModule.get("positionY"));
			View._SelectionModule.set("positionLabel" , posiNew);
		}
	});
	
	currSheet.bind($.wijmo.wijspread.Events.SelectionChanging , function(sender , args){
		if(args != null
				&& args.newSelections
				&& args.newSelections.length
				&& args.newSelections.length > 0){
			// 取最后一块选择区域展示
			var selection = args.newSelections[args.newSelections.length -1];
			View._SelectionModule.set("positionX" , selection.row);
			View._SelectionModule.set("positionY" , selection.col);
			var position = View.Utils.initAreaPosiLabel(selection.row , selection.col , selection.rowCount , selection.colCount);
			View._SelectionModule.set("positionLabel" , position);
			View._SelectionModule.set("val" , currSheet.getValue(selection.row , selection.col));
			View._SelectionModule.set("displayVal" , currSheet.getText(selection.row , selection.col));
		}
	});
	
	currSheet.bind($.wijmo.wijspread.Events.EditStarting , function(sender , args){
		// 报表指标单元格禁止编辑
		if(args != null){
			args.cancel = View._initReadOnly(args.row , args.col);
		}
	});
	
	currSheet.bind($.wijmo.wijspread.Events.EnterCell , function(sender , args){
		if(args == null
				|| typeof args == "undefined"){
			return ;
		}
		if(View._settings.cellDetail === true){				
			$("#_contextbox").attr("contenteditable" , !View._initReadOnly(args.row , args.col));
		}
		if(View._settings.onEnterCell != null
				&& typeof View._settings.onEnterCell == "function"){
			var cellInfo = {};
			var cellLabel = View.Utils.initAreaPosiLabel(args.row , args.col);
			jQuery.extend(cellInfo , View._cellInfos[cellLabel]);
			View._settings.onEnterCell(sender , args , cellInfo);
		}
	});
	
	currSheet.bind($.wijmo.wijspread.Events.LeaveCell , function(sender , args){
		if(args == null
				|| typeof args == "undefined"){
			return ;
		}
		if(View._settings.onLeaveCell != null
				&& typeof View._settings.onLeaveCell == "function"){
			var cellInfo = {};
			var cellLabel = View.Utils.initAreaPosiLabel(args.row , args.col);
			jQuery.extend(cellInfo , View._cellInfos[cellLabel]);
			View._settings.onLeaveCell(sender , args , cellInfo);
		}
	});
	
	currSheet.bind($.wijmo.wijspread.Events.CellDoubleClick , function(sender , args){
		if(args == null
				|| typeof args == "undefined"){
			return ;
		}
		if(View._settings.onCellDoubleClick != null
				&& typeof View._settings.onCellDoubleClick == "function"){
			var cellInfo = {};
			var cellLabel = View.Utils.initAreaPosiLabel(args.row , args.col);
			jQuery.extend(cellInfo , View._cellInfos[cellLabel]);
			View._settings.onCellDoubleClick(sender , args , cellInfo);
		}
	});
	
	currSheet.bind($.wijmo.wijspread.Events.ValueChanged , function(sender , args){
		if(args == null
				|| typeof args == "undefined"){
			return ;
		}
	});
	
	currSheet.bind($.wijmo.wijspread.Events.CellClick , function(sender , args){
		if(args == null
				|| typeof args == "undefined"){
			return ;
		}
		View.curCellNo = View.Utils.initAreaPosiLabel(args.row , args.col);
	});
};

View._checkCellValid = function(row , col){
	var valid = true;
	var currCell = View.spread.getActiveSheet().getCell(args.row , args.col);
	var cellLabel = View.Utils.initAreaPosiLabel(args.row , args.col);
	var cellInfo = View._cellInfos[cellLabel]; 
	if(cellInfo != null){
		if(cellInfo.isNull === false
				&& (currCell.value() == null
				|| $.trim(currCell.value()) == "")){
			// 不可为空
			
		}
	}
	return valid;
};

View._initReadOnly = function(row , col){
	var flag = true;
	// 只读模式，全部禁止编辑
	if(View._settings.readOnly === true){
		if(View._settings.cellDetail === true){
			$("#_contextbox").attr("contenteditable" , false);
		}
		return flag;
	}else{
		// 非只读模式
		// 只有【普通指标单元格】；【数据集单元格】；【表间计算单元格】，
		// 在isUpt为Y时可以进行编辑，其他所有单元格均不可编辑
		var cellPosi = View.Utils.initAreaPosiLabel(row , col);
		if(cellPosi in View._cellInfos){
			var cellInfo = View._cellInfos[cellPosi];
			if("Y" === cellInfo.isUpt
					&& "04" != cellInfo.type){
				if(View._settings.cellDetail === true){
					$("#_contextbox").attr("contenteditable" , true);
				}
				flag = false;
			}
		}
		if(View._settings.cellDetail === true){
			$("#_contextbox").attr("contenteditable" , false);
		}
		return flag;
	}
};

View.resize = function(spread){
	if(spread 
			&& typeof spread == "object"){
		if(View._settings.cellDetail === true){
			$("#_contextbox").width(View.spreadDom.width() - $("#_positionbox").width() - 10);
		}
		spread._doResize();
	}
};

View.getJSON = function(){
	return View._Json;
}

View.fromJSON = function(json){
	if(json
			&& spread){
		// 导入样式默认认为清除了所有的已配置报表指标
		View._Json = json;
		// MAXL: show spread on IE 6-8
		if (View._settings.useHTMLSheet && $.browser.msie && parseInt($.browser.version, 10) < 9) {
			View._fromJSONHandler();
			return;
		}
		if($.browser.msie && parseInt($.browser.version, 10) < 9){
			if(View.spread.canvas.firstChild.loaded != null
					&& typeof View.spread.canvas.firstChild.loaded != "undefined"
					&& View.spread.canvas.firstChild.loaded === true){
				// sl加载完毕
				if(View._timeoutHandler){
					window.clearTimeout(View._timeoutHandler);
				}
				View.spread._paintSuspended = true;
				View._fromJSONHandler();
				View.spread._paintSuspended = false;
			}else{
				View._timeoutHandler = window.setTimeout('View.fromJSON(View._Json);' , 200);
			}
		}else{			
			View._fromJSONHandler();
		}
	}
	var currSheet = View.spread.getActiveSheet();
	currSheet.getColumns(0, currSheet.getColumnCount($.wijmo.wijspread.SheetArea.rowHeader) - 1, $.wijmo.wijspread.SheetArea.rowHeader).visible(false);
	currSheet.getRows(0, currSheet.getRowCount($.wijmo.wijspread.SheetArea.colHeader) - 1, $.wijmo.wijspread.SheetArea.colHeader).visible(false);
	currSheet.setActiveCell(null);
	View._changeExcelCellColor(currSheet);
};

View._changeExcelCellColor = function(currSheet){
	if(View._settings.forColorCells.colorCells != null){
		var type = View._settings.forColorCells.themeType;
		var cells = View._settings.forColorCells.colorCells.split(",");
		switch(type){
			case "rptSubmit" :
				for(var i in cells){
					var cell = Utils.posiToRowCol(cells[i]);
					var cellObj = currSheet.getCell(cell.row,cell.col);
					cellObj.backColor(View._settings.forColorCells.cellsColor);
				}
				break;
			case "rptQuery" :
				$.each(View.cellInfo,function(key,value){
					if($.inArray(value.cellNo,cells) >= 0){
						var cell = Utils.posiToRowCol(key);
						var cellObj = currSheet.getCell(cell.row,cell.col);
						cellObj.backColor(View._settings.forColorCells.cellsColor);
					}
				})
				break;
		}
	}
}

View._formateData = function(jsonFormat){
	for(var key in jsonFormat.sheets){
		var data=jsonFormat.sheets[key].data.dataTable;
		for(var posi in View._cellInfos){
			var cellTmp = View._cellInfos[posi];
			if(cellTmp.unit!=null&&cellTmp.unit!=""&&cellTmp.unit!="00"){
				var rowCol = View.Utils.posiToRowCol(posi);
				var val= parseFloat(data[rowCol.row][rowCol.col].value);
				if(!isNaN(val))
					data[rowCol.row][rowCol.col].value=val;
			}
		}
	}
}

View._fromJSONHandler = function(){
	if(View._Json){
		var jsonFormat = View._Json;
		if(typeof jsonFormat == "string"){
			jsonFormat = JSON2.parse(jsonFormat);
		}
		// MAXL: show spread on IE 6-8
		if (View._settings.useHTMLSheet && $.browser.msie && parseInt($.browser.version, 10) < 9) {
			View.spread.fromJSON(jsonFormat);
			View.spread.getActiveSheet().generateHTMLTable(View.target);
			return;
		}
		//View._formateData(jsonFormat);
		if($.browser.msie && parseInt($.browser.version, 10) < 9
				&& typeof View.Utils.jsonRestore == "function"){
			jsonFormat = View.Utils.jsonRestore(jsonFormat);
		}
		date = new Date();
		View.spread.fromJSON(jsonFormat);
		date = new Date();
		View._initViewportEvents(View.spread);
		date = new Date();
		View._initValidates(View.spread);
		date = new Date();
		
		if(View._settings.autoAdj)
			View._initColInfo(View.spread);
		else if(View.templateType == "01")
			View._initRowInfo(View.spread);
	}
};

View._getUnit=function(unit){
	switch(unit){
		case '01':{
			return 0;
			break;
		}
		case '02':{
			return 2;
			break;
		}
		case '03':{
			return 3;
			break;
		}
		case '04':{
			return 4;
			break;
		}
		case '05':{
			return 8;
			break;
		}
		default:{
			return 0;
			break;
		}
	}
}


/**
 * 自动调整裂列宽
 * @param spread
 */
View._initColInfo = function(spread){
	
	if(!spread){
		return ;
	}
	var currSheet = spread.getActiveSheet();
	var columnList = [];
	for(var i=0;i<currSheet.getColumnCount();i++){
		currSheet.autoFitColumn(i);
		currSheet.setColumnWidth(i,currSheet.getColumnWidth(i)+5);
	}
	
}

View._initRowInfo = function(spread){
	var currSheet = spread.getActiveSheet();
	for(var i=0;i<currSheet.getRowCount();i++){
		currSheet.autoFitRow(i);
	}
}

/**
 * 刷新单元格value信息
 * @param cellValMap   key - cellNo ; value - 值
 */
View.refreshCellVals = function(cellValMap){
	if(View._settings.readOnly === true){
		// 只读模式，不关心值变化
		return;
	}
	if(View._cellInfos
			&& cellValMap
			&& typeof cellValMap == "object"){
		for(var posi in cellValMap){
			if(typeof View._cellInfos[posi] != "undefined"
				&& typeof View._cellInfos[posi].value != "undefined"){
				View._cellInfos[posi].oldValue = cellValMap[posi];
			}
		}
	}
}
 
/**
 * 构造发生了变化的报表指标信息
 */
View.generateChangeInfo = function(){
	var changeInfo = {};
	var changeCells = [];
	if(View._settings.readOnly === true){
		// 只读模式，不会发生值变化
		return changeInfo;
	}
	if(View._cellInfos){
		var currSheet = View.spread.getActiveSheet();
		for(var posi in View._cellInfos){
			var cellTmp = View._cellInfos[posi];
			if(cellTmp.isUpt === "Y"
					&& cellTmp.isPri === "Y"){
				// 只有权限内可编辑单元格，才进行值变化与否分析
				var rowCol = View.Utils.posiToRowCol(posi);
				var newVal = currSheet.getCell(rowCol.row , rowCol.col).value();
				if(newVal && newVal._error){
					newVal = "0";
				}
				var flag=(View._settings.ajaxData.fileName!=null&&View._settings.ajaxData.fileName!="")?true:false;
				var newValStr = $.trim(newVal)+"";
				cellTmp.newValue =newValStr;
				cellTmp.flag=flag;
				if(cellTmp.newValue != cellTmp.value){
					cellTmp.cellNo = posi;
					changeCells.push(cellTmp);
				}
				
			}
		}
	}
	
	changeInfo = {
			cells : changeCells,
			searchArgs : View._settings.searchArgs,
			dataDate: View._settings.dataDate,
			isValid: true
	};
	for(var i in changeCells){
		var currSheet = View.spread.getActiveSheet();
		var rowCol = View.Utils.posiToRowCol(changeCells[i].cellNo);
		var valid = currSheet.isValid(rowCol.row, rowCol.col, changeCells[i].newvalue);
		if(!valid){
			changeInfo.isValid=false;
			break;
		}
	}
	return changeInfo;
};

/**
 * 获取当前选择的指标信息
 */
View.getSelectionIdxs = function(){
	var selIdxs = [];
	if(View._cellInfos){
		var currSheet = View.spread.getActiveSheet();
		var selectionModules = currSheet.getSelections();
		if(selectionModules.length 
				&& selectionModules.length > 0){
			f1 : for(var i = 0 , j = selectionModules.length ; i < j ; i++){
				// 选中的多块区域
				var beginRow = selectionModules[i].row < 0 ? 0 : selectionModules[i].row;
				var endRow = beginRow + selectionModules[i].rowCount - 1;
				var beginCol = selectionModules[i].col < 0 ? 0 : selectionModules[i].col;
				var endCol = beginCol + selectionModules[i].colCount - 1;
				f2 : for(var m = beginRow , n = endRow ; m <= n ; m++){
					// 行
					f3 : for(var k = beginCol , l = endCol ; k <= l ; k++){
						// 列
						var posiTmp = View.Utils.initAreaPosiLabel(m , k);
						var cellInfoTmp = View._cellInfos[posiTmp];
						if(cellInfoTmp
							&& cellInfoTmp.indexNo
							&& cellInfoTmp.indexNo != ""){
							var cellTmp = currSheet.getCell(m,k);
							cellInfoTmp.newVal = cellTmp.value() + "";
							selIdxs.push(cellInfoTmp);
						}
					}
				}
			}
		}
	}
	return selIdxs;
}

/**
 * 获取全部的指标单元格信息
 */
View.getAllIdxs = function(){
	var selIdxs = [];
	if(View._cellInfos){
		var currSheet = View.spread.getActiveSheet();
		f1 : for(var m = 0 , n = currSheet.getRowCount() ; m <= n ; m++){
			// 行
			f2 : for(var k = 0 , l = currSheet.getColumnCount() ; k <= l ; k++){
				// 列
				var posiTmp = View.Utils.initAreaPosiLabel(m , k);
				var cellInfoTmp = View._cellInfos[posiTmp];
				if(cellInfoTmp
					&& cellInfoTmp.indexNo
					&& cellInfoTmp.indexNo != ""){
					var cellTmp = currSheet.getCell(m,k);
					cellInfoTmp.newVal = cellTmp.value() + "";
					selIdxs.push(cellInfoTmp);
				}
			}
		}
	}
	return selIdxs;
}

View.doKeyDown = function(){
	var shiftKeyOrCtrlKey = false;
	// 禁止刷新、F11等按键功能
	window.document.onkeydown = function(e) {
	    e = !e ? window.event : e;
	    var key = window.event ? e.keyCode : e.which;

	    shiftKeyOrCtrlKey = false;

	    if (e.shiftKey || e.ctrlKey) {
	    	shiftKeyOrCtrlKey = true;
	    }

	    if ((e.altKey) && (key == 37 || key == 39)) { // Alt+左右方向
	    	return false;
	    }
	    if (key == 8) { // backspace
		try {
		    // 输入框\密码框\文本区域\下拉框\日历\cellContent
		    var type = (e.srcElement || e.target).type;
		    var readonly = (e.srcElement || e.target).readOnly;
		    var ltype = (e.srcElement || e.target).ltype;
		    if(View._settings.cellDetail === true){	
		    	// 明细div可以使用backspace
		    	var id = (e.srcElement || e.target).id;
		    	//明细div必须不为只读时才可以使用backspace
		    	if(id == "_contextbox"&&($("#_contextbox").attr("contenteditable")=="true")){
		    		return true;
		    	}
		    }
		    if (readonly != true && readonly != "readonly") {
			if (type == "password" || type == "textarea") {
			    return true;
			}
			if (type == "text" && ltype && ltype != "date"
				&& ltype != "select") {
			    return true;
			}
			if (type == "text" && !ltype) {
			    return true;
			}
		    }
		} catch (e) {
		}
		key = 0;
		if (window.event) {
		    e.cancelBubble = true;
		} else {
		    e.stopPropagation();
		}
			return false;
	    }
	    if(key==13){
	    	return false;
	    }
	    if ((key == 116) || (e.ctrlKey && key == 82)) { // Ctrl + R
	    	return false;
	    }

	    if ((e.ctrlKey) && (key == 78)) { // Ctrl+n
	    	return false;
	    }

	    if ((e.shiftKey) && (key == 121)) { // shift+F10
	    	return false;
	    }
	    
	    if(key == 46){// delete
	    
	    }
	    
	};
};

View._setDataValidator = function(row, col, value, sheetArea)
{
	var keyword_undefined = undefined,
    	   keyword_null = null;
    if (sheetArea === keyword_undefined || sheetArea === keyword_null)
    {
        sheetArea = 3
    }
    var self = View.spread.getActiveSheet();
    var style = self.getStyle(row, col, sheetArea);
    if (!style)
    {
        style = new View.spread.Style
    } 
    if ($.wijmo.wijspread.features.conditionalFormat && value)
    {
        if ((value.condition instanceof $.wijmo.wijspread.FormulaCondition) && (value.condition._baseRow === keyword_undefined || value.condition._baseRow === keyword_null))
        {
            value.condition._baseRow = row !== -1 ? row : 0
        }
        if ((value.condition instanceof $.wijmo.wijspread.FormulaCondition) && (value.condition._baseCol === keyword_undefined || value.condition._baseCol === keyword_null))
        {
            value.condition._baseCol = col !== -1 ? col : 0
        }
    }
    style.validator = value;
    View._setStyleObject(self , row , col , style);
};

//设置单元格样式，
//与spread原生方法(Sheet.setStyleObject)的差别是，该方法丢弃了一切影响渲染速度的判断逻辑，只是单纯的附上样式
View._setStyleObject = function(sheet , row , col , styleObj){
	if($.browser.msie && parseInt($.browser.version, 10) < 9){
		sheet.setStyle(row , col , styleObj , $.wijmo.wijspread.SheetArea.viewport);
	}else{		
		var m = sheet._getModel($.wijmo.wijspread.SheetArea.viewport);
		if (m){
			m.setStyle(row, col, styleObj);
		}
	}
}

define(function(){
	return View;
})