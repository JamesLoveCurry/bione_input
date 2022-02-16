/*******************************************************/
/****             报表查询入口JS文件               ****/
/******************************************************/

var View = {};

require.config({
	paths:{
		"celldetail" : "cfg/views/celldetail",
		"SelectionModule" : "cfg/modules/SelectionModule",
		"utils" : "cfg/utils/designutil",
		"toolbar" : "cfg/views/toolbar-rptview"
	}
});

View.spread = {};

View.spreadDom = {};

View.Utils = {};

View._Json;

View._timeoutHandler;

View._defaults = {
	searchArgs : null , // 查询条件 , 格式:{dim1:[val1,val2],dim2:[val1],...}(同查询引擎)
	targetHeight : null , // target总高度
	ctx : "" ,  // 所处jsp的全局contextpath 
	readOnly : true , // 是否只读模式(若是只读模式，单元格不可编辑 )
	contextMenu : false ,	//是否有右键菜单
	cellDetail : false	 ,  // 是否有单元格明细
	canUserEditFormula : true , // 是否可以编辑公式
	tabStripVisible : false , // 是否显示底部sheets' tab信息
	isloadSave : false,
	// ajax获取数据
	visibleCellNos : null , // 可见的单元格编号集合
	
	inValidMap : null , // 校验未通过的信息集合，格式: [{cellNo:单元格编号,remindColor:提醒颜色(6位)} , {...}]
	initFromAjax : true , // 自动ajax后台获取数据
	url : "/report/frame/tmp/view/getViewInfo?d="+new Date().getTime(),
	methodType : "POST",
	ajaxData : {} , // ajax参数，目前支持rptId:报表id；dataDate:数据日期 ;busiLineId 业务条线Id,fileName 文件
	// events 
	onEnterCell : null , // 选中单元格事件
	onLeaveCell : null , // 移出单元格事件
	onCellDoubleClick : null ,  // 双击单元格事件
	onEditEnded : null ,// 编辑完成事件
	loadCompleted : null//加载完成回调函数
};

View._settings = {};

View._cellInfos = {};

View.cellInfos = {};

// 需要被记录进页面内存中的单元格类型
// 目前暂只需要记录【指标单元格】、【数据集单元格】和【表间计算单元格】
View._validCellType = ["02" , "03" , "04" , "05"];

View._SelectionModule = {};

View.cellSpanList = null;

View.textCells = null;

View.notUptCells = [];
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
		//如果是明细类报表，缩减高度，预留出分页工具栏的位置 20200211
		if(View._settings.ajaxData.templateType && (View._settings.ajaxData.templateType == "01" || View._settings.ajaxData.templateType == "03")){
			if(View._settings.ajaxData.isPaging == "Y"){
				target.height(View._settings.targetHeight-30);
			}else{
				target.height(View._settings.targetHeight);
			}
		}else{
			target.height(View._settings.targetHeight);
		}
	}
	View.spreadDom = View._initSpreadDoom(target);
	
	// 初始化spread上下文环境
	View.spread = new GC.Spread.Sheets.Workbook(View.spreadDom[0], {
        sheetCount: 1,
        newTabVisible: false,
        tabStripVisible: View._settings.tabStripVisible,
        highlightInvalidData: true,
        allowCopyPasteExcelStyle: true,
        allowContextMenu: false,
        enableFormulaTextbox: true
    });
    initSpread();
	
	function initSpread(){
        // 允许单元格文字溢出
		View.spread.getActiveSheet().options.allowCellOverflow = true;
		// 初始化事件
		View._initViewportEvents(View.spread);
		// 初始化依赖组件
		require(["SelectionModule" , "celldetail"  , "utils" ] , function(SelectionModule, Detail, Utils){
			if(View._settings.cellDetail === true){			
				View._cellDetailDom = Detail.initCellDetail($("#_spreadCellDetail") ,View.spreadDom, View.spread);
			}
			View._SelectionModule = SelectionModule;
			View.Utils = Utils;
			if(View._settings.cellDetail === true){			
				if(ko != null
						&& typeof ko == "object"){			
					View._SelectionModule = SelectionModule.newInstance();
					ko.cleanNode($("#_cellDetailBar")[0]);
					ko.applyBindings(View._SelectionModule , $("#_cellDetailBar")[0]);
				}
			}
			View._initViewEvents();
			View.resize(View.spread);
			View.ajaxJson(null,null,true);
		});
		if(View._defaults.contextMenu === false){//清空系统自带的右键菜单
			View.spread.contextMenu.menuData = [];
		}
		initContextMenu();//添加右键菜单功能
	}
	
	function initContextMenu(){
		var contextMenu  = [];
		
		/*
		*  自定义指标数据下钻函数
		* */
		View.spread.commandManager().register("dataDrill",
	         {
	             canUndo: true,
	             execute: function (context, options, isUndo) {
	         		if(window.View && typeof window.View.spread != "undefined"){					
	         			var currSheet = window.View.spread.getActiveSheet();
	    				var selections = currSheet.getSelections();
	    				var currSelection = selections[0];
	    				if(currSelection && base_selectIdxNo){
	    					var $content = $(window);
	    					var height = $content.height();
	    					var width = $content.width();
	    					BIONE.commonOpenDialog("数据下钻", "rptIdxDataDrill", width*0.99, height*0.99, View._settings.ctx + "/rpt/frs/rptfill/rptIdxDataDrill");
	    				}else{
	    					BIONE.tip("该单元格不是指标单元格,无法下钻");
	    				}
	         		}
	             }
	         });
		
		/*
		*  自定义指标数据跨期分析函数
		* */
		View.spread.commandManager().register("dataAna",
	         {
	             canUndo: true,
	             execute: function (context, options, isUndo) {
	         		if(window.View && typeof window.View.spread != "undefined"){					
	         			var currSheet = window.View.spread.getActiveSheet();
	    				var selections = currSheet.getSelections();
	    				var currSelection = selections[0];
	    				if(currSelection){
	    					var $content = $(window);
	    					var height = $content.height();
	    					var width = $content.width();
	    					BIONE.commonOpenDialog("数据跨期分析", "rptIdxDataAna", width*0.75, height*0.75, View._settings.ctx + "/rpt/frs/rptfill/chartAna");
	    				}
	         		}
	             }
	         });
		View.spread.contextMenu.menuData = contextMenu;
	}
	return View.spread;
};
View.ajaxJson=function(inValidMap,fileName,init,url,start,step,pageFlag, async){
	// 初始化报表指标数据
			if(View._settings.initFromAjax === true
					&& View._settings.ctx != null
					&& View._settings.ctx != ""
					&& View._settings.url != null
					&& View._settings.url != ""){
				var ajaxData = View._settings.ajaxData;
				if(ajaxData == null){
					ajaxData = {};
				}else{
					//明细类报表查询增加分页 20200211
					if(ajaxData.templateType && (ajaxData.templateType == "01" || ajaxData.templateType == "03")){
						if(ajaxData.isPaging == "Y"){
							if(step == "全部"){
								start = null;
								step= null;
								ajaxData.start = null;
								ajaxData.step= null;
							}else{
								ajaxData.start = 1;
								ajaxData.step= pageSize;
							}
						}
					}
				}
				ajaxData.readOnly = View._settings.readOnly;
				ajaxData.fileName = fileName;
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
				if(View._settings.inValidMap
						&& View._settings.inValidMap != ""){
					ajaxData.inValidMap = View._settings.inValidMap;
				}
				if(url == null){
					url = View._settings.url;
				}
				if(start!=null){
					ajaxData.start=start;
				}
				if(step!=null){
					ajaxData.step=step;
				}
				$.ajax({
					cache : false,
					async : async != "undefined" ? async : true, //默认为异步
					url : View._settings.ctx +url ,
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
									&& errorMsg != ""
									&& typeof errorMsg != "undefined"){
								var config = {};
								if(errorMsg.length > 80){
									config = {
										width: $(window).width() * 0.8
									};
								}
								BIONE.showError(errorMsg, null ,config);
							}else{
								if(init!=null&&init==true){
									View._Json = result.json;
								}
								if(ajaxData.templateType && (ajaxData.templateType == "01" || ajaxData.templateType == "03")){
									if(ajaxData.isPaging == "Y"){
										var total = result.total;
										if(init && pageFlag != false)
											initPagination(total);
									}
								}
								View.spread.options.tabEditable = false;
								View.spread.options.newTabVisible = false;
								var jsonStr = result.json;
								var cellInfo = result.cellInfo;
								var dsInfo = result.dsInfo;
								View.templateType = result.templateType;
								View.spread.suspendPaint();
								View.spread.suspendCalcService();
								View.cellRemarks = result.cellRemarks;
								View._initCellInfos(cellInfo , dsInfo);
								View.cellInfos = cellInfo;
								//增加公式的单元格信息
								View.formulaCell = result.formulaCell;
								View.fromJSON(jsonStr);
								View.initEditCell();
								base_verId = result.verId;
								View.textCells = result.textCells;
								View.notUptCells = result.notUptCells;
								//部分excel公式对于单元格内容是String类型的时候，不会自动计算，所以前台处理一下
								for (var key in cellInfo){
									var cellType = cellInfo[key].type;
									var displayFormat = cellInfo[key].displayFormat;
									if(("03" == cellType || "02" == cellType) && ("04" != displayFormat)){//指标单元格或明细单元格,且显示类型不是文本类型
										var rowCol = View.Utils.posiToRowCol(key);
									    var cellVal = View.spread.getActiveSheet().getValue(rowCol.row, rowCol.col);
									    if(cellVal){
									        if(isNaN(cellVal) == false){//判断是否是数字
										    	var floatVal = parseFloat(cellVal);
												View.spread.getActiveSheet().setValue(rowCol.row, rowCol.col, floatVal);
										    }
									    }
									}
								}
								if(null != result.cellSpanList){
									View.cellSpanList = result.cellSpanList;
									var sheet = View.spread.getActiveSheet();
									var cellSpanList = result.cellSpanList;
									for(var i=0; i<cellSpanList.length; i++){
										var beginRow = cellSpanList[i].beginRow;
										var beginCol = cellSpanList[i].beginCol;
										var rowSpan = cellSpanList[i].rowSpan;
										var colSpan = cellSpanList[i].colSpan;
										var value = cellSpanList[i].value;
										sheet.addSpan(beginRow, beginCol, rowSpan, colSpan);
									    sheet.setValue(beginRow, beginCol, value);
									    if("04" != cellSpanList[i].displayFormat){//合并后。除文本以外的单元格居右
									    	sheet.getCell(beginRow,beginCol).hAlign(GC.Spread.Sheets.HorizontalAlign.right);
									    }
									    var endRow = beginRow + rowSpan - 1;
									    for(var j=beginRow; j < endRow; j++){
									    	sheet.setValue(j+1, beginCol, null);
									    }
									}
								}
								View.spread.resumeCalcService();
								View.spread.resumePaint();
								if(fileName != null && fileName != "" && typeof fileName != "undefined"){
									BIONE.showSuccess("上传成功");
								}
							}
						}
						BIONE.hideLoading();
					},
					error:function(){
						BIONE.hideLoading();
					},
					complete: function(){
						//打开后直接保存，用于导入功能
						if(typeof(isOpenSave)!="undefined"){
							tmp.save(true);
						}
						//加载完成后，是否保存，用于报表计算
						if(View._settings.isloadSave){
							View._saveData(ajaxData);
						}
						//报表加载完成执行回调函数
						if(View._settings.loadCompleted != null
								&& typeof View._settings.loadCompleted == "function"){
							View._settings.loadCompleted();
						}
						BIONE.hideLoading();
					}
				});
			}
}
View.initEditCell=function(){

	var currSheet = View.spread.getActiveSheet();
	for(var posi in View._cellInfos){
		var cellTmp = View._cellInfos[posi];
		var rowCol = View.Utils.posiToRowCol(posi);
		if (cellTmp.type&&cellTmp.type!=null&cellTmp.type=="09"){
			currSheet.setValue(rowCol.row , rowCol.col,cellTmp.value);
			currSheet.getCell(rowCol.row , rowCol.col).value=cellTmp.value;
		}else if (cellTmp.type&&cellTmp.type!=null&cellTmp.type=="02"){
			currSheet.setValue(rowCol.row , rowCol.col,cellTmp.value);
			currSheet.getCell(rowCol.row , rowCol.col).value = cellTmp.value;
		}
			
	}
}
View.reset=function(){
	View.fromJSON(View._Json);
}

View.getJSON = function(){
	return View._Json;
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
			}else{
				//可编辑单元格 add by chenl 2016年12月9日
				var value=cellTmp.value;
				var cellNo=cellTmp.cellNo;
				jQuery.extend(cellTmp, dsInfo[cellTmp.cellNo]);
				cellTmp.value=value;
				cellTmp.cellNo=cellNo;
				tmp[posi] = cellTmp;
			}
		}
	}
	View._cellInfos = tmp;
/*	var currSheet = View.spread.getActiveSheet();
	for(var posi in View._cellInfos){
		var cellTmp = View._cellInfos[posi];
		var rowCol = View.Utils.posiToRowCol(posi);
		if(cellTmp.unit!=null&&cellTmp.unit!=""&&cellTmp.unit!="00"){
			currSheet.setValue(rowCol.row , rowCol.col,1);
		}
	}*/
}

View._initViewEvents = function(){
	if(View._settings.cellDetail === true
			&& View._cellDetailDom){
		View._cellDetailDom.bind("focusout" , function(){
			if(View.spread){		
				var valNew = View._cellDetailDom.val();
				var currSheet = View.spread.getActiveSheet();
				currSheet.setValue(View._SelectionModule.get("positionX") , View._SelectionModule.get("positionY") , valNew);
			}
		});
	}
}

View._initSpreadDoom = function(target){
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
	spread.options.highlightInvalidData = true;
	var currSheet = spread.getActiveSheet();
	var tag="\n";
	for(var i in View.cellRemarks){
		//去除单元格填写提示，只显示单元格备注转化成批注
		if(View.cellRemarks[i].operInfo){
			var rowCol = View.Utils.posiToRowCol(View.cellRemarks[i].cellNo);
			View.spread.getActiveSheet().comments.add(rowCol.row , rowCol.col, View.cellRemarks[i].operInfo);
		}
	}
    View.spread.getActiveSheet().invalidateLayout();
    View.spread.getActiveSheet().repaint();
}

View._initViewportEvents = function(spread){
	// 选择单元格处理
	if(!spread){
		return ;
	}
	var currSheet = spread.getActiveSheet();
	
	currSheet.bind(GC.Spread.Sheets.Events.EditEnded , function(sender , args){
		if(args){
			if(View._settings.onEditEnded != null
					&& typeof View._settings.onEditEnded != "undefined"){
				View._settings.onEditEnded(sender , args);
			}
		}
	});
	
	currSheet.bind(GC.Spread.Sheets.Events.SelectionChanged , function(sender , args){
		if(View._SelectionModule.set
				&& typeof View._SelectionModule.set == "function"){
			var posiNew = View.Utils.initAreaPosiLabel(View._SelectionModule.get("positionX") , View._SelectionModule.get("positionY"));
			View._SelectionModule.set("positionLabel" , posiNew);
		}
	});
	
	currSheet.bind(GC.Spread.Sheets.Events.SelectionChanging , function(sender , args){
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
			var currCell = currSheet.getCell(selection.row , selection.col);
			if(currCell.formula()!=null  && currCell.formula()!=""){
				View._SelectionModule.set("val" , currCell.formula());
				View._SelectionModule.set("displayVal" ,  currCell.formula());
			}
			else{
				View._SelectionModule.set("val" , currCell.value());
				View._SelectionModule.set("displayVal" ,  currCell.value());
			}
			
		}
	});
	
	currSheet.bind(GC.Spread.Sheets.Events.EditStarting , function(sender , args){
		// 报表指标单元格禁止编辑
		if(args != null){
			args.cancel = View._initReadOnly(args.row , args.col);
		}
	});
	
	currSheet.bind(GC.Spread.Sheets.Events.EnterCell , function(sender , args){
		if(args == null
				|| typeof args == "undefined"){
			return ;
		}
		if(View._settings.cellDetail === true){		
			if(!View._initReadOnly(args.row , args.col)){
				$("#_contextbox").removeAttr("readonly");
			}
			else{
				$("#_contextbox").attr("readonly",true);
			}
		}
		if(View._settings.onEnterCell != null
				&& typeof View._settings.onEnterCell == "function"){
			var cellInfo = {};
			var cellLabel = View.Utils.initAreaPosiLabel(args.row , args.col);
			jQuery.extend(cellInfo , View._cellInfos[cellLabel]);
			View._settings.onEnterCell(sender , args , cellInfo);
		}
	});
	
	currSheet.bind(GC.Spread.Sheets.Events.LeaveCell , function(sender , args){
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
	
	currSheet.bind(GC.Spread.Sheets.Events.CellDoubleClick , function(sender , args){
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
	
	currSheet.bind(GC.Spread.Sheets.Events.ValueChanged , function(sender , args){
		if(args == null
				|| typeof args == "undefined"){
			return ;
		}
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
		}else if(View._settings.cellDetail === true){
			$("#_contextbox").attr("contenteditable" , false);
		}
		
		if(View.textCells != null){//表达式也可编辑
			for(var i=0; i < View.textCells.length; i++){
				var cellInfo = View.textCells[i];
				if(cellPosi == cellInfo.extInfo.cellNo){
					if("Y" === cellInfo.extInfo.isUpt && (cellInfo.expression.indexOf("fillEr")!= -1 || cellInfo.expression.indexOf("auditor")!= -1 
							|| cellInfo.expression.indexOf("charger")!= -1 || cellInfo.expression.indexOf("phone_number")!= -1)){
						if(View._settings.cellDetail === true){
							$("#_contextbox").attr("contenteditable" , true);
						}
						flag = false;
					}
				}
			}
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
		spread.refresh();
	}
};

View.fromJSON = function(json){
	if(json){
		// 导入样式默认认为清除了所有的已配置报表指标
		View._Json = json;
		View._fromJSONHandler();
	}
};

View._formateData = function(jsonFormat){
	for(var key in jsonFormat.sheets){
		var data=jsonFormat.sheets[key].data.dataTable;
		for(var posi in View._cellInfos){
			var cellTmp = View._cellInfos[posi];
			if((cellTmp.unit!=null&&cellTmp.unit!=""&&cellTmp.unit!="00")||(cellTmp.unit=="00" && View.templateType == "02")){
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

		//View._formateData(jsonFormat);
		View.spread.fromJSON(jsonFormat);
		View._initViewportEvents(View.spread);
		View._initValidates(View.spread);
	}
};

/**
 * 自动调整裂列宽
 * @param spread
 */
View._initRowInfo = function(spread){
	if(!spread){
		return ;
	}
	var currSheet = spread.getActiveSheet();
	for(var i=0;i<currSheet.getColumnCount();i++){
		var width=currSheet.getColumnWidth(i);
		currSheet.autoFitColumn(i);
		if(width<currSheet.getColumnWidth(i)+30)
			currSheet.setColumnWidth(i,currSheet.getColumnWidth(i)+30);
		else{
			currSheet.setColumnWidth(i,width);
		}
	}
	/*for(var i=0;i<currSheet.getRowCount();i++){
		currSheet.autoFitRow(i);
	}*/
}
View._accMul=function(arg1,arg2){
	var m=0;
	s1=arg1.toString(),s2=arg2.toString();
	try{m+=s1.split(".")[1].length}catch(e){}
	try{m+=s2.split(".")[1].length}catch(e){}
	var a1,a2;
	var dot=s1.indexOf(".");
	if(dot>=0){
		a1=Number(s1.substring(0,dot)+s1.substring(dot+1,s1.length));
	}
	else{
		a1=Number(s1);
	}
	dot=s2.indexOf(".");
	if(dot>=0){
		a2=Number(s2.substring(0,dot)+s2.substring(dot+1,s2.length));
	}
	else{
		a2=Number(s2);
	}
	var s=(a1*a2).toString();
	if(0 == m){
		return s;
	}else{
		return s.substring(0,s.length-m)+"."+s.substring(s.length-m,s.length);
	}
};

View._changValue=function(unit,value){
	switch(unit){
		case '01':{
			return value;
			break;
		}
		case '02':{
			return View._accMul(value,100);
			break;
		}
		case '03':{
			return View._accMul(value,1000);
			break;
		}
		case '04':{
			return View._accMul(value,10000);
			break;
		}
		case '05':{
			return View._accMul(value,100000000);
			break;
		}
		default:{
			return value;
			break;
		}
	}
}

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
View.refreshOldCellVals = function(cellValMap){
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
				View._cellInfos[posi].value = cellValMap[posi];
			}
		}
	}
}

View.getFormulaCellInfo=function(){
	var forumulaCellValues=[];
	if(View._settings.readOnly === true){
		// 只读模式，不会发生值变化
		return forumulaCellValues;
	}
	if(View.formulaCell){
		var currSheet = View.spread.getActiveSheet();
		for( var i in View.formulaCell ){
			// 只有权限内可编辑单元格，才进行值变化与否分析
			var rowCol = View.Utils.posiToRowCol(View.formulaCell[i].cellNo);
			var newVal = currSheet.getCell(rowCol.row , rowCol.col).value();
			if(newVal==null||!newVal||newVal._error){
				newVal = "0";
			}
			var newValStr = $.trim(newVal)+"";
			var cell = {cellNo:View.formulaCell[i].cellNo,cellValue:newValStr ,unit:View.formulaCell[i].unit};
			forumulaCellValues.push(cell);
		}
	}
	return forumulaCellValues;
	
}
/**
 * 构造发生了变化的报表指标信息
 */
View.generateChangeInfo = function(isLeafNode, isRecoveryInit){
	var changeInfo = {};
	var changeCells = [];
	var detailCellInfo = {};
	if(View._settings.readOnly === true){
		// 只读模式，不会发生值变化
		return changeInfo;
	}
	var currSheet = View.spread.getActiveSheet();
	//表达式可编辑，判断是否已修改，用于保存
	if(View.textCells != null && View.textCells.length > 0){
		for(var i=0; i<View.textCells.length; i++){
			var cellTmp = View.textCells[i];
			if(cellTmp.extInfo.isUpt === "Y" && (cellTmp.expression.indexOf("$fillEr$")!= -1 || cellTmp.expression.indexOf("$auditor$")!= -1 
					|| cellTmp.expression.indexOf("$charger$")!= -1 || cellTmp.expression.indexOf("$phone_number$")!= -1)){
				var rowCol = View.Utils.posiToRowCol(cellTmp.region);
				var oldValue = cellTmp.extInfo.oldValue;
				var newValue = currSheet.getCell(rowCol.row , rowCol.col).value();
				if(newValue != oldValue){
					cellTmp.oldValue = oldValue;
					cellTmp.newValue = newValue;
					changeCells.push(cellTmp);
				}
			}
		}
	}
	if(View._cellInfos){
		for(var posi in View._cellInfos){
			var cellTmp = View._cellInfos[posi];
			// 只有权限内可编辑单元格，才进行值变化与否分析
			if(cellTmp.isUpt === "Y"){
				var rowCol = View.Utils.posiToRowCol(posi);
				var newVal = null;
				if(null != View.cellSpanList && View.cellSpanList.length > 0){//构造合并单元格信息
					for(var i=0; i<View.cellSpanList.length; i++){
						var beginRow = View.cellSpanList[i].beginRow;
						var beginCol = View.cellSpanList[i].beginCol;
						var rowSpan = View.cellSpanList[i].rowSpan;
						var value = View.cellSpanList[i].value;
						if(rowCol.col == beginCol && rowCol.row > beginRow && rowCol.row <= (beginRow + rowSpan -1)){
							newVal = currSheet.getCell(beginRow , beginCol).value();
							break;
						}else{
							newVal = currSheet.getCell(rowCol.row , rowCol.col).value();
						}
					}
				}else{
					newVal = currSheet.getCell(rowCol.row , rowCol.col).value();
				}
				//先去除空格
				var newValStr = "";
				if(newVal == null){
					newValStr = newVal;
				}else{
					newValStr = $.trim(newVal)+"";
				}
				//如果还为空就赋默认值
				if(!newValStr && newValStr != null){
					if(cellTmp.type=="09" || cellTmp.extMode=="02" || cellTmp.type == "02"){
						newValStr="";
					}else{
						newValStr = "0";
					}
				}
				var flag=(View._settings.ajaxData.fileName!=null&&View._settings.ajaxData.fileName!="")?true:false;
				cellTmp.newValue =newValStr;
				cellTmp.flag=flag;
				cellTmp.posi=posi;
				//如果是恢复初始值并且不是明细单元格，就把原值都致为0，然后进行保存
				if(isRecoveryInit && cellTmp.type != "02"){
					cellTmp.oldValue = "0";
				}
				//excel公式报表指标，如果是汇总的，只保存叶子节点数据
	/*				if(!isLeafNode){//不是叶子节点
					if(cellTmp.type == "04"){//是excel公式
						if(cellTmp.isSum == "Y"){//是汇总的
							continue;
						}
					}
				}*/
				//检测单元格值是否有变化
				var newValue = cellTmp.newValue;
				var oldValue = cellTmp.oldValue;
				//单元格类型为excel公式时，并且值不是数字的，要给将值置为0，应对分母为0的情况
				if(("04" == cellTmp.type) && (isNaN(newValue) == true)){
					newValue = "0";
				}
				//明细类字段不做单位变换
				if("02" != cellTmp.type){
					if(cellTmp.displayFormat == "02"){//百分比
						newValue = Number(newValue).toFixed(Number(cellTmp.dataPrecision) + 2);
					}else if(cellTmp.displayFormat == "01"){
						newValue = Number(newValue).toFixed(Number(cellTmp.dataPrecision));
						newValue = View._changValue(cellTmp.unit, newValue);
					}else{
						newValue = View._changValue(cellTmp.unit, newValue);
					}
				}
				var isUpt = false;
				if(newValue != oldValue){
					cellTmp.newValue = newValue;
					if((!isNaN(newValue)) && (!isNaN(oldValue)) && cellTmp.displayFormat != "04"){//判断是否是数字
						if(parseFloat(newValue) != parseFloat(oldValue)) {
							isUpt = true;
							changeCells.push(cellTmp);
						}
					}else{//如果不是数字
						isUpt = true;
						changeCells.push(cellTmp);
					}
				}
				//如果是明细单元格，需要把这个单元格数据对应的主键数据拿到，插入原模型表会用到
				if(isUpt){
					var detailCellInfos = [];
					detailCellInfo[posi] = cellTmp.key;
				}
			}
		}
	}
	
	changeInfo = {
			cells : changeCells,
			searchArgs : View._settings.searchArgs,
			dataDate: View._settings.dataDate,
			isValid: true,
			detailCells : detailCellInfo
	};
/*	for(var i in changeCells){
		var currSheet = View.spread.getActiveSheet();
		var rowCol = View.Utils.posiToRowCol(changeCells[i].cellNo);
		var valid = currSheet.isValid(rowCol.row, rowCol.col, changeCells[i].newvalue);
		if(!valid){
			changeInfo.isValid=false;
			break;
		}
	}*/
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
			    var readonly = View._settings.readOnly;
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
					return true;
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

View._setDataValidator = function(row, col, value, sheetArea) {
	var rowNo = parseInt(row);
	var colNo = parseInt(col);
	var self = View.spread.getActiveSheet();
	self.setDataValidator(rowNo, colNo, 1, 1, value,
			GC.Spread.Sheets.SheetArea.viewport);

	var keyword_undefined = undefined, keyword_null = null;
	if (sheetArea === keyword_undefined || sheetArea === keyword_null) {
		sheetArea = 3
	}
	var self = View.spread.getActiveSheet();
	var style = self.getStyle(row, col, sheetArea);
	if (!style) {
		if (View.spread.Style)
			style = new View.spread.Style
	}
	if (style)
		View._setStyleObject(self, row, col, style);

};


// 设置单元格样式，
//与spread原生方法(Sheet.setStyleObject)的差别是，该方法丢弃了一切影响渲染速度的判断逻辑，只是单纯的附上样式
View._setStyleObject = function(sheet , row , col , styleObj){
	sheet.setStyle(row, col, styleObj);
}

//报表保存方法
View._saveData = function(ajaxData){
	if(null == ajaxData){
		return;
	}
	var changeInfo = View.generateChangeInfo(true);
	var formulaCellInfo = View.getFormulaCellInfo();
	//进行excel公式初始数据保存
	$.ajax({
		async : true,
		url : View._settings.ctx + "/rpt/frs/rptfill/saveData",
		dataType : 'json',
		type : 'post',
		data : {
			cells : JSON2.stringify(changeInfo.cells),
			rptId : ajaxData.rptId,
			orgNo : ajaxData.orgNo,
			dataDate : ajaxData.dataDate,
			taskInsId : ajaxData.taskInsId,
			formulaCellInfo : JSON2.stringify(formulaCellInfo),
			searchArgs : changeInfo.searchArgs
		}, 
		success: function(result) {
			BIONE.tip("报表计算成功");
			$("#spread"+ ajaxData.rptId + ajaxData.dataDate + ajaxData.orgNo).html("");
		},
		error:function(){
			BIONE.tip("报表计算异常，请查看日志");
		}
	});
}

define(function(){
	return View;
})