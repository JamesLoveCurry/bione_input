/*******************************************************/
/****             报表设计器入口JS文件               ****/
/******************************************************/
require.config({
	paths:{
		"constants" : "cfg/utils/constants",
		"utils" : "cfg/utils/designutil",
		"celldetail" : "cfg/views/celldetail",
		"toolbar" : "cfg/views/toolbar",
		"toolbarAwesome" : "cfg/views/toolbar-awesome",
		"SelectionModule" : "cfg/modules/SelectionModule",
		"rptIdx" : "cfg/modules/RptIdxInfo",
		"rowCol" : "cfg/modules/RowCol",
		"paramComp" : "cfg/modules/ParamComp",
		"uuid":"../../js/uuid/uuid" , 
		"contextmenu" : "cfg/views/contextmenu",
		"underscore" : "../../js/bione/underscore-min"
	}
});

define([
        "constants" ,
        "utils" , 
        "SelectionModule" , 
        "celldetail" , 
//        "toolbar" ,
        "toolbarAwesome",
        "paramComp",
        "uuid" , 
        "rptIdx" , 
        "rowCol" , 
        "contextmenu",
        "underscore"] , 
    function(
    	 Constants ,
    	 Utils , 
         SelectionModule , 
         Detail , 
         Toolbar , 
         ParamComp ,
         Uuid , 
         RptIdx , 
         rowCol ,
         Context ,
         Underscore){
	var Design = {};
	
	Design.RptIdxInfo = RptIdx;
	Design.RowCol = rowCol;
	Design.ParamComp = ParamComp;
	Design.Uuid = Uuid;
	Design.Utils = Utils;
	Design.Constants = Constants;
	Design.rptIdxCfg = {};//报表指标配置信息{cellNo=indexNo, cellNo=indexNo}
	//已定义报表指标集合 key:seq , value:rptIdx对象(RptIdxInfo.js)
	Design.rptIdxs = [];
	// 已定义的行列对象信息 key:seq , value:rowCol对象(RowCol.js)
	Design.rowCols = [];
	//已定义的一般单元格信息
	Design.commonCells = [];
	Design.rptIdxsSize = function(){
		// 获取报表指标集合长度
		var size = 0;
		for(var i in Design.rptIdxs){
			size++;
		}
		return size;
	}
	
	Design.rowColsSize = function(){
		// 获取行列信息长度
		var size = 0;
		for(var i in Design.rowColsSize){
			size++;
		}
		return size;
	}
	
	Design.spread = {};
	
	Design.spreadDom = {};
	
	Design._cellDetailDom = {};
	
	Design._defaults = {
		targetHeight : null , // target总高度
		templateType : Constants.TEMPLATE_TYPE_IDX , // 模板类型 01-明细类；02-单元格类 ; 03-综合类 ; 04-指标列表
		showType : Constants.SHOW_TYPE_NORMAL , // 展现方式 normal-普通 ； cellnm-单元格名称 ； busino-业务标识
		showBusiType : false , // 展现方式中，是否展现业务类型
		ctx : "" ,  // 所处jsp的全局contextpath 
		readOnly : false , // 是否只读模式(若是只读模式，默认不会有toolbar和contextMenu，单元格不可编辑)
		toolbar : true ,	// 是否有工具栏
		contextMenu : true ,	//是否有右键菜单
		cellDetail : true	 ,  // 是否有单元格明细
		canUserEditFormula : true , // 是否可以编辑公式
		tabStripVisible : false , // 是否显示底部sheets' tab信息
		isBusiLine : false , // 是否业务条线子模板
		isRptIdxCfg : false,//是否是报表指标配置
		// events 
		onEnterCell : null , // 选中单元格事件
		onLeaveCell : null , // 移出单元格事件
		onCellDoubleClick : null , // 双击单元格事件
		onSelectionChanged : null , // 选择区域变化事件
		onClipboardChanging : null , // 利用剪贴板数据粘贴前事件
		onClipboardPasted : null , // 利用剪贴板数据粘贴后事件
		// develop events
		onIdxsChanged : null , // 指标类报表指标发生变化后事件
		onSave : null , // 保存事件
		onCssImport : null , // 样式引入事件
		// rptIdxs init
		initFromAjax : false , // 是否使用ajax异步获取数据
		url : "/report/frame/design/cfg/getDesignInfo" ,  // ajax，一般不需要设置，使用缺省值即可
		methodType : "POST" ,
		ajaxData : {} ,
		// 不想使用ajax全局初始化时，可以使用以下配置做具体单元格初始化
		// 以下配置initFromAjax为true时不生效
		moduleCells : null, // 数据集字段
		formulaCells : null, // 公式
		idxCells : null, // 指标
		idxCalcCells : null, // 表间计算
		staticCells : null, // 文本常量
		colIdxCells : null , // 指标列表 - 指标
		colDimCells : null , // 指标列表 - 维度
		rowCols : null, // 行列过滤初始化
		comCells : null, //一般单元格详情
		initJson : "" // 初始化表样json
	}
	
	Design._settings = {};
	
	Design._SelectionModule = {};
	
	// 剪切板信息(Spread _clipboardHelper)
	Design._clipBoard = {
			active : false ,
			rptIdxs : [],
			helper : {},
			rptIdxsBase : null,
			pastingIdxNos : []  // 待粘贴单元格指标号对应关系 key-"rowNo,colNo" ; value-indexNo
	};
	
	//拉伸扩展基础列信息
	Design._fillCells = {
			fillDirection : "",
			fillType : "",
			upFillType :"",
			fillCellType : "",
			rptIdxs : [],//被扩展单元格的单元格信息
			fillIdxNos : []  // 待扩展单元格指标号对应关系 key-"rowNo,colNo" ; value-indexNo
	};
	Design._jsonTmp;
	
	Design._timeoutHandler;
	
	Design.viewport = GC.Spread.Sheets.SheetArea.viewport;
	
	/**
	 * 初始化方法
	 */
	Design.init = function(targetSelector , settings){
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
		Design._settings = Design._initSettings(settings);
		//keydown事件
		Design.doKeyDown();
		return Design._settingHandler(target, Design._settings);	
	}
	
	Design._initSettings = function(settings){
		// 初始化参数
		var properties = Design._defaults;
		if(settings
				&& typeof settings == "object"){
			for(var p in settings){
				if(p in properties){
					properties[p] = settings[p];
				}
			}
		}
		return properties;
	}
	
	Design._getFont = function(font){
		var newFont={};
		jQuery.extend(newFont , Toolbar._font);
		if(font!=null){
			$("body").append("<div id='font' style='display:none'></div>");
			$("#font").css("font",font);
			var font=$("#font").css("font-family");
			var bold=$("#font").css("font-weight");
			var fontsize=$("#font").css("font-size");
			var italic=$("#font").css("font-style");
			$("#font").remove();
			if(font!=""){
				newFont.font=font;
			}
			if(bold!=""){
				newFont.bold=(bold==400)?"":bold;
			}
			if(fontsize!=""){
				newFont.fontsize=fontsize;
			}
			if(italic!=""){
				newFont.italic=(italic=="normal")?"":italic;
			}
		}
		return newFont;
	};
	
	Design._settingHandler = function(target , settings){
		if(Design._settings.targetHeight != null
				&& Design._settings.targetHeight != ""){
			target.height(Design._settings.targetHeight);
		}
		Design.spreadDom  = Design._initSpreadDom(target);
		
		// 初始化spread上下文环境
        Design.spread = new GC.Spread.Sheets.Workbook(Design.spreadDom[0], {
            sheetCount: 1,
            newTabVisible: false,
            tabStripVisible: Design._settings.tabStripVisible,
            allowCopyPasteExcelStyle: true
        });
		Design.RptIdxInfo.initContext(Design._settings.templateType);
        initSpread();
		initDelAction();
		initPasteAction();
        
		function initSpread(){
            // 允许单元格文字溢出
            Design.spread.getActiveSheet().options.allowCellOverflow = true;
            Design.spread.getActiveSheet().options.clipBoardOptions = GC.Spread.Sheets.ClipboardPasteOptions.all;
            // 初始化事件
			Design._initViewportEvents(Design.spread);
			// 初始化组件
			if(Design._settings.cellDetail === true){			
				Design._cellDetailDom = Detail.initCellDetail($("#_spreadCellDetail") , Design.spreadDom , Design.spread);
			}
			Design._SelectionModule = SelectionModule;
			if(Design._settings.readOnly === false
					&& Design._settings.contextMenu === true){
                Context.initContextMenu(Design.spread , templateType , Design._settings.ctx , Design._settings.isBusiLine);
			}
			if(Design._settings.cellDetail === true){			
				if(ko != null
						&& typeof ko == "object"){			
					Design._SelectionModule = SelectionModule.newInstance();
					ko.cleanNode($("#_cellDetailBar")[0]);
					ko.applyBindings(Design._SelectionModule , $("#_cellDetailBar")[0]);
				}
			}
			// 初始化toolbar
			if(Design._settings.readOnly === false
					&& Design._settings.toolbar === true){
				Toolbar.initToolbar($("#_spreadToolbar") , Design.spreadDom , Design._settings.ctx , Design._settings.isBusiLine);
			}
			Design.resize(Design.spread);
			// 初始化报表指标
			if(Design._settings.initFromAjax === true
					&& Design._settings.ctx != null
					&& Design._settings.ctx != ""
					&& Design._settings.url != null
					&& Design._settings.url != ""){
				$.ajax({
					cache : false,
					async : true,
					url : Design._settings.ctx + Design._settings.url,
					dataType : 'json',
					data : Design._settings.ajaxData,
					type : Design._settings.methodType,
					beforeSend : function() {
						if(typeof BIONE != "undefined"){							
							BIONE.loading = true;
							BIONE.showLoading("数据加载中，请稍候");
						}
					},
					success : function(result){
						if(result != null
								&& typeof result.tmpInfo != "undefined"){
							Design.spread.suspendPaint();
							Design.spread.suspendCalcService();
							Design._settings.moduleCells = result.moduleCells;
							Design._settings.idxCells = result.idxCells;
							Design._settings.formulaCells = result.formulaCells;
							Design._settings.idxCalcCells = result.idxCalcCells;
							Design._settings.staticCells = result.staticCells;
							Design._settings.colIdxCells = result.colIdxCells;
							Design._settings.colDimCells = result.colDimCells;
							Design._settings.comCells = result.comCells;
							Design._settings.rowCols = result.batchCfgs;
							var jsonStr = result.tmpInfo.templateContentjson;
							Design.fromJSON(jsonStr , true);
							Design.spread.resumeCalcService();
							Design.spread.resumePaint();
							Design.spread.options.allowExtendPasteRange = false ;
							Design.spread.options.allowUserDragDrop = false ;
							Design.rptIdxCfg = result.rptIdxCfgMap;
						}
					},
					error:function(){
						BIONE.tip("数据加载异常，请联系系统管理员");
					},
					complete : function() {
						if (typeof BIONE != 'undefined') {
							Design.spread.options.tabEditable = false;
							Design.spread.options.newTabVisible = false;
							BIONE.loading = false;
							BIONE.hideLoading();
						}
					}
				});
			}else if(Design._settings.initJson != null
						&& Design._settings.initJson != ""){
				Design.fromJSON(Design._settings.initJson , true);
			}
		}
		
		//监听删除事件
		function initDelAction(){
/*			var activeSheet = Design.spread.getActiveSheet();
			activeSheet.bind(GC.Spread.Sheets.Events.RangeChanged, function (sender, args) {
                if(args.action == GC.Spread.Sheets.RangeChangedAction.clear){
                	deleteIdxObj();
                }
			});*/
			
			Design.spread.commandManager().register('backspaceAction',
				function backspaceAct() {
					deleteIdxObj();
				}
			);
			//绑定backspace事件
			Design.spread.commandManager().setShortcutKey('backspaceAction', GC.Spread.Commands.Key.backspace, false, false, false, false);
			
			//绑定delete事件
			Design.spread.commandManager().setShortcutKey('backspaceAction', GC.Spread.Commands.Key.del, false, false, false, false);//清空事件
		}
		
		//监听粘贴事件
		function initPasteAction(){
			var oldFun = GC.Spread.Sheets.Commands.clipboardPaste.execute;
			GC.Spread.Sheets.Commands.clipboardPaste.execute = function(context,options,isUndo){
				jQuery.extend(true , Design._clipBoard.helper, options);
				return oldFun.apply(this,arguments);
			}
		}
		
		//删除报表指标事件
		function deleteIdxObj(){
	    	var currSheet = Design.spread.getActiveSheet();
	    	var selectionModules = currSheet.getSelections();
			if(selectionModules.length 
					&& selectionModules.length > 0){
				var hasIdx = false;
				for(var i = 0 , j = selectionModules.length ; i < j ; i++){
					// 选中的多块区域
					var beginRow = selectionModules[i].row < 0 ? 0 : selectionModules[i].row;
					var endRow = beginRow + selectionModules[i].rowCount - 1;
					var beginCol = selectionModules[i].col < 0 ? 0 : selectionModules[i].col;
					var endCol = beginCol + selectionModules[i].colCount - 1;
					for(var m = beginRow , n = endRow ; m <= n ; m++){
						// 行
						for(var k = beginCol , l = endCol ; k <= l ; k++){
							// 列
		              		var currCell = currSheet.getCell(m , k);
		              		var currCellValue = currCell.value();
		                	Design._impactAna(currSheet, m, k, currCellValue);
		                	return false;
		          		  /*var e = window.event;
	                		if(e){
	                			e.stopPropagation();
	                			e.preventDefault();
	                		}*/
						}
					}
				}
			}
		}
		Design.spread.options.tabEditable = false;
		Design.spread.options.newTabVisible = false;
		return Design.spread;
	}
	
	Design. _initSpreadDom = function(target){
		//初始化设计器dom相关
		target.empty();
		var spreadDom = target;
		var heightTmp = spreadDom.height();
		var toolbarDom ;
		var cellDetailDom ;
		if(Design._settings.toolbar === true){
			toolbarDom = "<div id='_spreadToolbar' style='width=100%;'></div>";
		}
		if(Design._settings.cellDetail === true){
			cellDetailDom = "<div id='_spreadCellDetail' style='width=100%;'></div>";
		}
		if(toolbarDom != null
				|| cellDetailDom != null){
			var domInner = "";
			domInner += toolbarDom ? toolbarDom : "";
			domInner += cellDetailDom ? cellDetailDom : "";
			domInner += "<div id='_spread' style='100%'></div>";
			target.append(domInner);
			spreadDom = $("#_spread");
			spreadDom.height(heightTmp);
		}
		return spreadDom;
	}
	
	Design._rollbackFillInfo = function(currSheet){
		for(var i in Design._fillCells.fillIdxNos){
			var info=Design._fillCells.fillIdxNos[i].key.split(",");
			var row=info[0];
			var col=info[1];
			var seq=Design._fillCells.fillIdxNos[i].value;
			currSheet.setTag(row, col, seq);
		}
	}
	
	Design._excuteFillInfo = function(currSheet){
		//扩展开始单元格行号
		var row = Design._fillCells.fillRange.row;
		//扩展开始单元格列号
		var col = Design._fillCells.fillRange.col;
		//扩展行数
		var rowCount = Design._fillCells.fillRange.rowCount;
		//扩展列数
		var colCount = Design._fillCells.fillRange.colCount;
		for(var i = 0;i < rowCount;i++){
			for(var j = 0;j < colCount;j++){
				var cellTmp=null;
				var currow=row+i;
				var curcol=col+j;
				if( Design._fillCells.fillDirection == "0" || Design._fillCells.fillDirection == "1"){
					if(Design._fillCells.fillDirection == "0"){
						curcol=col-j;
					}
					cellTmp=Design._fillCells.rptIdxs[i];
				}
				if( Design._fillCells.fillDirection == "2" || Design._fillCells.fillDirection == "3"){
					if(Design._fillCells.fillDirection == "2"){
						currow=currow-i;
					}
					cellTmp=Design._fillCells.rptIdxs[j];
				}
				if(cellTmp!=null){
					//可编辑单元格
					if("01" == cellTmp.cellType){
						var uuid = Uuid.v1();
						var newCell={};
						jQuery.extend(true , newCell , cellTmp);
						newCell.seq=uuid;
						var cell=currSheet.getCell(currow, curcol);
						//单元格编号
						var currLabel = Utils.initAreaPosiLabel(cell.row, cell.col);
						//新扩展的单元格，单元格编号和名称都默认显示为单元格编号
						newCell.cellNm = currLabel;
						newCell.cellNo = currLabel;
						Design.commonCells[uuid] = newCell;
						currSheet.setTag(currow, curcol, uuid);
					}else{
						var uuid = Uuid.v1();
						var realIndexNo = uuid.replace(/-/g, '');
						var newCell={};
						jQuery.extend(true , newCell , cellTmp);
						newCell.seq=uuid;
						var cell=currSheet.getCell(currow, curcol);
						//单元格编号
						var currLabel = Utils.initAreaPosiLabel(cell.row, cell.col);
						//指标配置表存在，则使用配置表中的“报表指标编号”
						if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
							realIndexNo = Design.rptIdxCfg[currLabel];
						}
						newCell.realIndexNo = realIndexNo;
						if(newCell.cellType==Constants.CELL_TYPE_FORMULA){
							newCell.formula=cell.formula();
						}
						//新扩展的单元格，单元格编号和名称都默认显示为单元格编号
						newCell.cellNm = currLabel;
						newCell.cellNo = currLabel;
						Design.rptIdxs[uuid]=newCell;
						currSheet.setTag(currow, curcol, uuid);
					}
				}
			}
		}
	}
	
	Design._initViewportEvents = function(spread){
		// 选择单元格处理
		if(!spread){
			return ;
		}
		var currSheet = spread.getActiveSheet();
		
		currSheet.bind(GC.Spread.Sheets.Events.EditEnding , function(sender , args){
			if(args){
				var editText = args.editingText;
				if(Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_V
						&& editText
						&& editText.indexOf("=") == "0"){
					// 若是指标列表，且正准备维护一个excel公式，进行数据列判断
					var dataRow = Design.getCurrDataRow();
					if(dataRow != null
							&& dataRow != args.row){
						BIONE.tip("请保持数据在同一行上..");
						args.cancel = true;
					}
				}
				if(Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_H
						&& editText
						&& editText.indexOf("=") == "0"){
					// 若是指标列表，且正准备维护一个excel公式，进行数据列判断
					var dataCol = Design.getCurrDataCol();
					if(dataCol != null
							&& dataCol != args.col){
						BIONE.tip("请保持数据在同一列上..");
						args.cancel = true;
					}
				}
				if(Design._settings.templateType === Constants.TEMPLATE_TYPE_CROSS
						&& editText
						&& editText.indexOf("=") == "0"){
					// 若是指标列表，且正准备维护一个excel公式，进行数据列判断
					var dataCol = Design.getCurrDataCol();
					var dataRow = Design.getCurrDataRow();
					if(dataCol != null
							&& dataCol != args.col && dataRow !=null && dataRow != args.row){
						BIONE.tip("请保持数据与指标在同一行列上..");
						args.cancel = true;
					}
				}
			}
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.EditEnded , function(sender , args){
			if(args){
				var editText = args.editingText == null ? "" : args.editingText;
				var seq = currSheet.getTag(args.row, args.col, GC.Spread.Sheets.SheetArea.viewport);
				var isFormal = editText.indexOf("=") == "0" ? true : false;
				if(seq != null
						&& typeof seq != "undefined"
						&& Design.rptIdxs[seq] != null
						&& typeof Design.rptIdxs[seq] != "undefined"){
					if(Design.rptIdxs[seq].cellType == Constants.CELL_TYPE_FORMULA){	
						if(editText != ""
							&& isFormal === true){
							Design.rptIdxs[seq].excelFormula = editText;
						}else{
							currSheet.setTag(args.row, args.col, null);
							delete Design.rptIdxs[seq];
						}
					}
				}else if(editText != "" && isFormal === true){
					// 将该单元格定义成为公式报表指标
					var uuid = Uuid.v1();
					currSheet.setTag(args.row, args.col, uuid);
					var rptIdxInfoTmp = RptIdx.newInstance(Constants.CELL_TYPE_FORMULA);
					rptIdxInfoTmp.excelFormula = editText;
					var realIndexNo = Uuid.v1().replace(/-/g, '');
					var selCell = currSheet.getCell(currSheet.getActiveRowIndex() , currSheet.getActiveColumnIndex());
					var currLabel = Utils.initAreaPosiLabel(selCell.row, selCell.col);
					//指标配置表存在，则使用配置表中的“报表指标编号”
					if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
						realIndexNo = Design.rptIdxCfg[currLabel];
					}
					rptIdxInfoTmp.realIndexNo = realIndexNo;
					//如果是明细报表或者综合报表，excel公式默认不落成报表指标
					if(Constants.TEMPLATE_TYPE_MODULE == templateType || Constants.TEMPLATE_TYPE_ALL == templateType){
						rptIdxInfoTmp.isRptIndex = "N";
					}
					//默认单元格名称等于单元格编号
					rptIdxInfoTmp.cellNm = currLabel;
					rptIdxInfoTmp.cellNo = currLabel;
					Design.rptIdxs[uuid] = rptIdxInfoTmp;
				}
			}
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.SelectionChanged , function(sender , args){
			var rptIdxTmp = {};
			var currRow = Design._SelectionModule.get("positionX");
			var currCol = Design._SelectionModule.get("positionY");
			var seq = currSheet.getTag(currRow, currCol, GC.Spread.Sheets.SheetArea.viewport);
			if(seq != null
					&& typeof seq != "undefined"
					&& Design.rptIdxs[seq]){
				jQuery.extend(rptIdxTmp , Design.rptIdxs[seq]);
			}
			if(Design._settings.onSelectionChanged != null
					&& typeof Design._settings.onSelectionChanged == "function"){
				Design._settings.onSelectionChanged(sender , args , rptIdxTmp);
			}
			if(Design._SelectionModule.set
					&& typeof Design._SelectionModule.set == "function"){
				var posiNew = Utils.initAreaPosiLabel(currRow , currCol);
				Design._SelectionModule.set("positionLabel" , posiNew);
			}
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.SelectionChanging , function(sender , args){
			if(args != null
					&& args.newSelections
					&& args.newSelections.length
					&& args.newSelections.length > 0){
				// 取最后一块选择区域展示
				var selection = args.newSelections[args.newSelections.length -1];
				Design._SelectionModule.set("positionX" , selection.row);
				Design._SelectionModule.set("positionY" , selection.col);
				var position = Utils.initAreaPosiLabel(selection.row , selection.col , selection.rowCount , selection.colCount);
				Design._SelectionModule.set("positionLabel" , position);
			}
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.EditStarting , function(sender , args){
			if(args != null){
	    		// 报表指标配置下，其他单元格禁止编辑
				if(Design._settings.isRptIdxCfg === true){
					var currRow = args.row;
		    		var currCol = args.col;
		    		var seq = currSheet.getTag(currRow, currCol, GC.Spread.Sheets.SheetArea.viewport);
		    		if(seq != null
		    				&& typeof seq != "undefined"
		    				&& Design.rptIdxs[seq]
		    				&& Design.rptIdxs[seq].cellType != Constants.CELL_TYPE_FORMULA){
		    			args.cancel = false;
		    		}else{
		    			args.cancel = true;
		    		}
				}
				if(Design._settings.readOnly === true){
					// 只读模式，禁止编辑
					args.cancel = true;
					return ;
				}
			}
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.EnterCell , function(sender , args){
			if(args == null
					|| typeof args == "undefined"){
				return ;
			}
			var rptIdxTmp = {};
			var currRow = args.row;
			var currCol = args.col;
			var seq = currSheet.getTag(currRow, currCol, GC.Spread.Sheets.SheetArea.viewport);
			if(seq != null
					&& typeof seq != "undefined"
					&& Design.rptIdxs[seq]){
				jQuery.extend(rptIdxTmp , Design.rptIdxs[seq]);
			}
			if(Design._settings.onEnterCell != null
					&& typeof Design._settings.onEnterCell == "function"){
				Design._settings.onEnterCell(sender , args , rptIdxTmp);
			}
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.LeaveCell , function(sender , args){
            Design._onLeaveCell(sender, args);
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.ClipboardChanging , function(sender , args){
			if(args != null){
				if(Design._settings.onClipboardChanging != null
						&& typeof Design._settings.onClipboardChanging == "function"){
					Design._settings.onClipboardChanging();
				}
				// 取第一段选择区域
				var selections = args.sheet.getSelections()[0];
				var beginRow = selections.row;
				var rowCount = selections.rowCount;
				var beginCol = selections.col;
				var colCount = selections.colCount;
				if(beginRow != null
						&& rowCount != null
						&& beginCol != null
						&& colCount != null){
					//检查单元格种类是否相同
					var hasNormalIdx = false;
					var hasRptIdx = false;
					var rptIdxs = [];
//					var commonCells = [];
					if(beginRow == -1){
						// 选择的列
						beginRow = 0;
						rowCount = currSheet.getRowCount();
					}
					if(beginCol == -1){
						// 选择的行
						beginCol = 0;
						colCount = currSheet.getColumnCount();
					}
					for(var r = 0 ; r < rowCount ; r++){
						var colArrayTmp = [];
//						var cellArrayTmp = [];
						for(var c = 0 ; c < colCount ; c++){
							var seqTmp = currSheet.getTag(beginRow + r, beginCol + c, GC.Spread.Sheets.SheetArea.viewport);
							if(seqTmp != null
									&& Design.rptIdxs[seqTmp] != null
									&& typeof Design.rptIdxs[seqTmp] != "undefined"){
								var objTmp = {};
								jQuery.extend(true , objTmp , Design.rptIdxs[seqTmp]);
								colArrayTmp[c] = objTmp;
								hasRptIdx = true;
							}
							/*if(seqTmp != null
									&& Design.commonCells[seqTmp] != null
									&& typeof Design.commonCells[seqTmp] != "undefined"){
								var objTmp = {};
								jQuery.extend(true , objTmp , Design.commonCells[seqTmp]);
								cellArrayTmp[c] = objTmp;
								hasRptIdx = true;
							}*/
						}
						rptIdxs[r] = colArrayTmp;
//						commonCells[r] = cellArrayTmp;
					}
					if(hasRptIdx){
						// 若是报表指标操作
						Design._clipBoard.active = true;
						Design._clipBoard.rptIdxs = rptIdxs;
						Design._clipBoard.commonCells = commonCells;
						jQuery.extend(true , Design._clipBoard.helper , args.sheet._clipboardHelper);
						//Design._clipBoard.helper = args.sheet._clipboardHelper;
					}else{
						Design._clipBoard.active = false;
					}
				}
			}
		});
		
		
		currSheet.bind(GC.Spread.Sheets.Events.ClipboardPasting , function(sender , args){
			if(Design._settings.onClipboardPasting != null
					&& typeof Design._settings.onClipboardPasting == "function"){
				Design._settings.onClipboardPasting();
			}
			if(Design._settings.templateType === Constants.TEMPLATE_TYPE_MODULE){
				// 若是指标列表，判断待粘贴单元格是否和当前数据行保持一致
				if(Design._clipBoard.active === true
						&& Design._clipBoard.rptIdxs.length > 0){
					// 只处理报表指标的粘贴
					if(args != null){
						var beginRow = args.cellRange.row;
						var rowCount = args.cellRange.rowCount;
						var beginCol = args.cellRange.col;
						var colCount = args.cellRange.colCount;
						if(beginRow != null
								&& rowCount != null
								&& beginCol != null
								&& colCount != null){
							if(beginRow == -1){
								// 选择的列
								beginRow = 0;
								rowCount = currSheet.getRowCount();
							}
							if(beginCol == -1){
								// 选择的行
								beginCol = 0;
								colCount = currSheet.getColumnCount();
							}
							for(var r = 0 ; r < rowCount ; r++){
								var colArrayTmp = Design._clipBoard.rptIdxs[r];
								for(var c = 0 ; c < colCount ; c++){
									var dataRow = Design.getCurrDataRow();
									var dataCol = Design.getCurrDataCol();
									if(dataRow != null
											&& dataRow != (beginRow + r)){
										if ((Design._settings.templateType === Constants.TEMPLATE_TYPE_MODULE
											&& colArrayTmp[c] != null
											&& typeof colArrayTmp[c] != "undefined" && colArrayTmp[c].cellType === Constants.CELL_TYPE_MODULE)
											|| (Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_V 
											&& colArrayTmp[c].cellType != Constants.CELL_TYPE_EXPRESSION)) {
											// 不一致不进行粘贴
											args.cancel = true;
											if (BIONE&& typeof BIONE.tip == "function") {
												BIONE.tip("请保持数据在同一行上..");
											}
											return;
										}
									}
									if(dataCol != null
											&& dataCol != (beginCol + c)){
										if((Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_H 
											&& colArrayTmp[c].cellType != Constants.CELL_TYPE_EXPRESSION)){
											args.cancel = true;
											if (BIONE&& typeof BIONE.tip == "function") {
												BIONE.tip("请保持数据在同一列上..");
											}
											return;
										}
									}
									if(dataCol != null
											&& dataCol != (beginCol + c) && dataRow != null
											&& dataRow != (beginRow + r)){
										if((Design._settings.templateType === Constants.TEMPLATE_TYPE_CROSS
												&& colArrayTmp[c].cellType != Constants.CELL_TYPE_EXPRESSION)){
											if (BIONE&& typeof BIONE.tip == "function") {
												BIONE.tip("请保持数据与指标在同一行列上..");
											}
											return;
										}
									}
									
								}
							}
						}
					}
				}
			}
		});
		
		
		currSheet.bind(GC.Spread.Sheets.Events.DragFillBlock , function(sender , args){
			//垂直向上扩展（2）和向下扩展（3）
			if ((args.fillDirection == "2" || args.fillDirection == "3")
				&& (Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_V || Design._settings.templateType === Constants.TEMPLATE_TYPE_MODULE || Design._settings.templateType === Constants.TEMPLATE_TYPE_CROSS)) {
					args.cancel = true;
					BIONE.tip("请保持数据与指标在同一行上..");
					return;
			}
			//水平向左扩展（0）和水平向右扩展（1）
			if ((args.fillDirection == "0" || args.fillDirection == "1")
					&& (Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_H || Design._settings.templateType === Constants.TEMPLATE_TYPE_CROSS)) {
						args.cancel = true;
						BIONE.tip("请保持数据与指标在同一列上..");
						return;
				}
			
			if(!args.isChangeFill){
				Design._fillCells.fillDirection = args.fillDirection;
				Design._fillCells.fillType = args.autoFillType;
				Design._fillCells.upFileType = "";
				Design._fillCells.fillRange = args.fillRange;
				var rptIdxs = [];
				var fillIdxNos = [];
				//扩展开始单元格行号
				var row = args.fillRange.row;
				//扩展开始单元格列号
				var col = args.fillRange.col;
				//扩展行数
				var rowCount = args.fillRange.rowCount;
				//扩展列数
				var colCount = args.fillRange.colCount;
				//水平向左扩展（0）和水平向右扩展（1）
				if(args.fillDirection == "0"||args.fillDirection == "1"){
					col = col-1;
				}
				//垂直向上扩展（2）和向下扩展（3）
				if(args.fillDirection == "2"||args.fillDirection == "3"){
					row = row-1;
				}
				for(var n = 0;n < rowCount;n++){
					for(var m = 0;m < colCount;m++){
						var i = n;
						var j = m;
						var rptIdx = null;
						if(args.fillDirection == "0"){
							j=-j
						}
						if(args.fillDirection == "2"){
							i=-i;
						}
						var seq = currSheet.getTag(row+i, col+j, GC.Spread.Sheets.SheetArea.viewport);	
						//判断扩展单元格是否是指标单元格
						rptIdx = Design.rptIdxs[seq];
						//判断扩展单元格是否是可编辑单元格
						modifyCell = Design.commonCells[seq];
						if(rptIdx){
							//指标单元格处理
							Design._fillCells.fillCellType = rptIdx.cellType;
							if(args.fillDirection == "2"||args.fillDirection == "3"){
								if(i == 0){
									rptIdxs.push(rptIdx);
								}else{
									seq = currSheet.getTag(row+i,col+j, GC.Spread.Sheets.SheetArea.viewport);
									rptIdx = Design.rptIdxs[seq];
									var idxNo = {};
									idxNo.key = (row+i)+","+(col+j);
									idxNo.value = rptIdx.seq;;
									fillIdxNos.push(idxNo);
								}
							}else if(args.fillDirection == "0"||args.fillDirection == "1"){
								if(j == 0){
									rptIdxs.push(rptIdx);
								}else{
									seq = currSheet.getTag(row+i,col+j, GC.Spread.Sheets.SheetArea.viewport);
									rptIdx = Design.rptIdxs[seq];
									var idxNo = {};
									idxNo.key = (row+i)+","+(col+j);
									idxNo.value = rptIdx.seq;;
									fillIdxNos.push(idxNo);
								}
							}
						}else if(modifyCell){
							//可编辑单元格处理
							Design._fillCells.fillCellType = modifyCell.cellType;
							if(args.fillDirection == "2"||args.fillDirection == "3"){
								if(i == 0){
									rptIdxs.push(modifyCell);
								}
							}else if(args.fillDirection == "0"||args.fillDirection == "1"){
								if(j == 0){
									rptIdxs.push(modifyCell);
								}
							}
						}
					}
				}
				//被扩展单元格信息
				Design._fillCells.rptIdxs = rptIdxs;
				//扩展单元格信息
				Design._fillCells.fillIdxNos = fillIdxNos;
			}else{
				Design._fillCells.upFileType = Design._fillCells.fillType;
				Design._fillCells.fillType = args.autoFillType;
			}
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.DragFillBlockCompleted , function(sender , args){
			if (args.cancel) {
				return;
			}
			if(!args.isChangeFill){
				Design._excuteFillInfo(currSheet);
			}
			else{
				if(Design._fillCells.upFileType!=Design._fillCells.fileType){
					Design._rollbackFillInfo(currSheet);
					if(Design._fillCells.fillType!="2"){
						Design._excuteFillInfo(currSheet);
					}
				}
			}
		});
		currSheet.bind(GC.Spread.Sheets.Events.ClipboardPasted , function(sender , args){
			if(Design._settings.onClipboardPasted != null
					&& typeof Design._settings.onClipboardPasted == "function"){
				Design._settings.onClipboardPasted();
			}
			if(Design._clipBoard.active === true
					&& Design._clipBoard.rptIdxs.length > 0){
				// 只处理报表指标的粘贴
				if(args != null){
					var beginRow = args.cellRange.row;
					var rowCount = args.cellRange.rowCount;
					var beginCol = args.cellRange.col;
					var colCount = args.cellRange.colCount;
					if(beginRow != null
							&& rowCount != null
							&& beginCol != null
							&& colCount != null){
						if(beginRow == -1){
							// 选择的列
							beginRow = 0;
							rowCount = currSheet.getRowCount();
						}
						if(beginCol == -1){
							// 选择的行
							beginCol = 0;
							colCount = currSheet.getColumnCount();
						}
						var indexTmp = 0;
						var isCutting = typeof Design._clipBoard.helper == 'undefined' ? false : Design._clipBoard.helper.isCutting;
						for(var r = 0 ; r < rowCount ; r++){
							var colArrayTmp = Design._clipBoard.rptIdxs[r];
							for(var c = 0 ; c < colCount ; c++){
								if(Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_V){
									// 若是指标列表，判断待粘贴单元格是否和当前数据行保持一致
									var dataRow = Design.getCurrDataRow();
									if(dataRow != null
											&& dataRow != (beginRow + r)){
										// 不一致不进行粘贴
										return ;
									}
								}
								if(Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_H){
									// 若是指标列表，判断待粘贴单元格是否和当前数据行保持一致
									var dataCol = Design.getCurrDataCol();
									if(dataCol != null
											&& dataCol != (beginCol + c)){
										// 不一致不进行粘贴
										return ;
									}
								}
								var cellTmp = currSheet.getCell(beginRow + r , beginCol + c);
								var colTmp = Design._clipBoard.rptIdxs[0][0] ;
								var cellTmpNo = Utils.initAreaPosiLabel(beginRow + r , beginCol + c);
								//	Design._setCellValue(cellTmp.row, cellTmp.col, colTmp.cellLabel(Design.getShowType()), currSheet);
								if(colTmp.cellNo == cellTmpNo){//如果复制的位置和粘贴的位置一样，就不进行复制了
									return;
								}
								if(colArrayTmp&&colArrayTmp.length>c)
									colTmp=colArrayTmp[c];
								else if(colArrayTmp&&colArrayTmp.length<c&&colArrayTmp.length>1){
									continue
								}
								else if(colArrayTmp&&colArrayTmp.length<=1){
									colTmp=colArrayTmp[0];
								}
								else if(!colArrayTmp&&Design._clipBoard.rptIdxs[0].length>c){
									colTmp = Design._clipBoard.rptIdxs[0][c] ;
								}
								else if(!colArrayTmp&&Design._clipBoard.rptIdxs[0].length<c&&Design._clipBoard.rptIdxs[0].length>1){
									continue
								}
								else if(!colArrayTmp&&Design._clipBoard.rptIdxs[0].length>1){
									colTmp = Design._clipBoard.rptIdxs[0][0] ;
								}
								else{
									colTmp = Design._clipBoard.rptIdxs[0][0] ;
								}
								if(colTmp != null
										&& typeof colTmp != "undefined"){								
									var uuid = Uuid.v1();
									var copyModule = colTmp;
									var newModule = {};
									jQuery.extend(true , newModule , copyModule);
									// 复制的是一个带有指标编号的报表指标(IE9下版本中isCutting暂获取不到)
									// 若目标单元格有realIndexNo，使用之，若没有，生成新的indexNo
									var realIndexNo = Uuid.v1().replace(/-/g, '');
									var oldIndexNo = Design._clipBoard.pastingIdxNos[r+","+c];
									if(oldIndexNo != ""
											&& typeof oldIndexNo != "undefined"){
										realIndexNo = oldIndexNo;
									}
									if(isCutting === false){										
										// 清除不允许被copy的属性
										$.each(RptIdxInfo.noCopyAttrs , function(i , attr){
											newModule[attr] = "";
										})
									}
									Design.rptIdxs[uuid] = newModule;
									currSheet.setTag(cellTmp.row, cellTmp.col, uuid);
									var selCell = currSheet.getCell(currSheet.getActiveRowIndex() , currSheet.getActiveColumnIndex());
									var currLabel = Utils.initAreaPosiLabel(selCell.row, selCell.col);
									//指标配置表存在，则使用配置表中的“报表指标编号”
									if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
										realIndexNo = Design.rptIdxCfg[currLabel];
									}
									newModule.realIndexNo = realIndexNo;
									if(newModule.cellType == Constants.CELL_TYPE_FORMULA){
										// 若是公式单元格
										cellTmp.formula(newModule.excelFormula);
									}
								}
							}
						}
					}
					if(isCutting === true){
						// 剪切
						for(var i in Design._clipBoard.rptIdxs){
							if(i.seq){	
								delete Design.rptIdxs[i.seq];
							}
						}
					}
				}
			}
		});
		
		currSheet.bind(GC.Spread.Sheets.Events.CellDoubleClick , function(sender , args){
			if(args == null
					|| typeof args == "undefined"){
				return ;
			}
			var rptIdxTmp = {};
			var currRow = args.row;
			var currCol = args.col;
			var seq = currSheet.getTag(currRow, currCol, GC.Spread.Sheets.SheetArea.viewport);
			if(seq != null
					&& typeof seq != "undefined"
					&& Design.rptIdxs[seq]){
				jQuery.extend(rptIdxTmp , Design.rptIdxs[seq]);
			}
			if(Design._settings.onCellDoubleClick != null
					&& typeof Design._settings.onCellDoubleClick != "undefined"){
				Design._settings.onCellDoubleClick(sender , args , rptIdxTmp);
			}
		});
	}

    Design._onLeaveCell = function(sender , args) {
		if(args == null
				|| typeof args == "undefined"){
			return ;
		}
		var rptIdxTmp = {};
		var currRow = args.row;
		var currCol = args.col;
		var currSheet = Design.spread.getActiveSheet();
		var seq = currSheet.getTag(currRow, currCol, GC.Spread.Sheets.SheetArea.viewport);
		if(seq != null
				&& typeof seq != "undefined"
				&& Design.rptIdxs[seq]){
			jQuery.extend(rptIdxTmp , Design.rptIdxs[seq]);
		}
		if(Design._settings.onLeaveCell != null
				&& typeof Design._settings.onLeaveCell == "function"){
			Design._settings.onLeaveCell(sender , args , rptIdxTmp);
		}
    }

    /** 获取当前选中的区域信息
	 *   （多个单元格合并成一个格算做一个区域） **/
	Design.getSelectionSpans = function(sheet , targetRow , targetCol){
		var spans = null;
		if(sheet 
				&& typeof sheet == "object"
				&& targetRow != 0
				&& targetCol != 0){
			// 目前该方法不支持基于整行整列的获取
			var selTmp = {};
			if(typeof targetRow != "undefined"
					&& typeof targetCol != "undefined"){
				selTmp.row = targetRow;
				selTmp.col = targetCol;
				selTmp.rowCount = 1;
				selTmp.colCount = 1;
			}else{			
				var sels = sheet.getSelections();
				if(sels
						&& typeof sels.length != "undefined"
						&& sels.length > 0){				
					selTmp = sels[0];
				}
			}
			var sel = Design._getActualCellRange(selTmp, sheet.getRowCount(), sheet.getColumnCount());
			spans = sheet.getSpans(sel, GC.Spread.Sheets.SheetArea.viewport);
		}
		return spans;
	}
	
	Design._getActualCellRange = function(cellRange, rowCount, columnCount) {
	    if (cellRange.row == -1 && cellRange.col == -1) {
	        return new GC.Spread.Sheets.Range(0, 0, rowCount, columnCount);
	    }
	    else if (cellRange.row == -1) {
	        return new GC.Spread.Sheets.Range(0, cellRange.col, rowCount, cellRange.colCount);
	    }
	    else if (cellRange.col == -1) {
	        return new GC.Spread.Sheets.Range(cellRange.row, 0, cellRange.rowCount, columnCount);
	    }
	    return cellRange;
	}
	
	Design.resize = function(spread){
		if(spread 
				&& typeof spread == "object"){
			if(Design._settings.cellDetail === true){
				$("#_contextbox").width(Design.spreadDom.width() - $("#_positionbox").width() - 10);
			}
			if(Design._settings.readOnly === false
					&& Design._settings.toolbar === true){			
				$("#_ribbon").width(Design.spreadDom.width());
			}
			spread.refresh();		
		}
	}
	
	Design.fromJSON = function(json , initIdxs){
		if(json){
			// 导入样式默认认为清除了所有的已配置报表指标
			Design.rptIdxs = [];
			Design._jsonTmp = json;
			Design._fromJSONHandler(initIdxs);
		}
	}
	
	Design._fromJSONHandler = function(initIdxs){
		if(Design._jsonTmp){
			var jsonFormat = Design._jsonTmp;
			if(typeof jsonFormat == "string"){
				//JSON2转换容错率较低
				try {
					jsonFormat = JSON2.parse(jsonFormat);
				} catch (e) {
					jsonFormat = eval("(" + jsonFormat + ")");
				}
			}
			Design.spread.fromJSON(jsonFormat);
			Design._initViewportEvents(Design.spread);
			if(initIdxs === true){
				Design.initRowCols();
				Design.initComCells();
				Design.initRptIdxs(Constants.CELL_TYPE_MODULE);
				Design.initRptIdxs(Constants.CELL_TYPE_IDX);
				Design.initRptIdxs(Constants.CELL_TYPE_FORMULA);
				Design.initRptIdxs(Constants.CELL_TYPE_BJJS);
				Design.initRptIdxs(Constants.CELL_TYPE_EXPRESSION);
				Design.initRptIdxs(Constants.CELL_TYPE_IDXCOL);
				Design.initRptIdxs(Constants.CELL_TYPE_DIMCOL , true);
                Design.spread.getActiveSheet().invalidateLayout();
                Design.spread.getActiveSheet().repaint();
			}
            // 检查最后一列是否是空列，如果不是空列，新增一个空列，便于插入操作
            var sheet = Design.spread.getActiveSheet();
            for (var idx = 0; idx < sheet.getRowCount(); idx ++) {
                var cell = sheet.getCell(idx, sheet.getColumnCount() - 1);
                if (cell != null && cell.value() != null && cell.value() != "") {
                    sheet.addColumns(sheet.getColumnCount(), 1);
                    break;
                }
            }
            // 检查最后一行是否是空行，如果不是空行，新增一个空行，便于插入操作
            for (var idx = 0; idx < sheet.getColumnCount(); idx ++) {
                var cell = sheet.getCell(sheet.getRowCount() - 1, idx);
                if (cell != null && cell.value() != null && cell.value() != "") {
                    sheet.addRows(sheet.getRowCount(), 1);
                    break;
                }
            }
		}
	}
	
	Design.clearRptIdxs = function(){
		Design.rptIdxs = [];
	}
	
	Design.initComCells = function(){
		var cellDomains = Design._settings.comCells;
		if(cellDomains){
			for(var i = 0 , l = cellDomains.length ; i < l ; i++){
				var cell = cellDomains[i];
				var rowNo = cell.rowId;
				var colNo = cell.colId;
				if(rowNo == null
						|| typeof rowNo == "undefined"
						|| colNo == null
						|| typeof colNo == null){
					continue;
				}
				var comCellTmp = RptIdx.newInstance("01");
				var seq = Uuid.v1();
				var rptObj = {};
				jQuery.extend(rptObj , comCellTmp);
				jQuery.extend(rptObj , cell);
				rptObj.seq = seq;
				var currSheet = Design.spread.getActiveSheet();
				currSheet.setTag(rowNo, colNo, seq);
				Design.commonCells[seq] = rptObj;
			}
		}
	}
	Design.initRowCols = function(){
		if(Design._settings.rowCols
				&& Design._settings.rowCols.length
				&& Design._settings.rowCols.length > 0){
			var currSheet = Design.spread.getActiveSheet();
			var rowMap = [];
			var colMap = [];
			for(var i = 0 , l = Design._settings.rowCols.length ; i < l ; i++){
				var batchInfo = Design._settings.rowCols[i];
				var filtInfo = {
						dimTypeNo : batchInfo.dimType,
						dimTypeNm : batchInfo.dimTypeNm,
						checkedVal : batchInfo.filterVal,
						filterModelVal : batchInfo.filterMode,
						cfgId : batchInfo.cfgId,
						filterText : ""
				};
				var num = Number(batchInfo.posNum);
				if(Constants.POS_TYPE_ROW == batchInfo.posType){
					// 行
					if(rowMap[num] == null){
						var tmp = [];
						tmp.push(filtInfo);
						rowMap[num] = tmp; 
					}else{
						rowMap[num].push(filtInfo);
					}
				}else if(Constants.POS_TYPE_COL == batchInfo.posType){
					// 列
					if(colMap[num] == null){
						var tmp = [];
						tmp.push(filtInfo);
						colMap[num] = tmp; 
					}else{
						colMap[num].push(filtInfo);
					}
				}
			}
			for(var rowTmp in rowMap){
				if(typeof rowMap[rowTmp] == "function"){
					continue;
				}
				var rowInfo = Design.RowCol.newRow();
				var seq = Design.Uuid.v1();
				rowInfo.objType = Constants.POS_TYPE_ROW;
				rowInfo.rowId = rowTmp;
				rowInfo.filtInfos = JSON2.stringify(rowMap[rowTmp]);
				rowInfo.seq = seq;
				var row = currSheet.getRange(parseInt(rowTmp), -1, GC.Spread.Sheets.SheetArea.viewport);
				if(row){
					row.tag(seq);
				}
				Design.rowCols[seq] = rowInfo;
			}
			for(var colTmp in colMap){
				if(typeof colMap[colTmp] == "function"){
					continue;
				}
				var colInfo = Design.RowCol.newColumn();
				var seq = Design.Uuid.v1();
				colInfo.objType = Constants.POS_TYPE_COL;
				colInfo.colId = colTmp;
				colInfo.filtInfos = JSON2.stringify(colMap[colTmp]);
				colInfo.seq = seq;
				var col = currSheet.getRange(-1, parseInt(colTmp), GC.Spread.Sheets.SheetArea.viewport);
				if(col){
					col.tag(seq);
				}
				Design.rowCols[seq] = colInfo;
			}
		}
	}
	
	Design.initRptIdxs = function(cellType , needRefresh){
		var cellDomains = [];
		switch(cellType){
		case Constants.CELL_TYPE_MODULE:
			cellDomains = Design._settings.moduleCells;
			break;
		case Constants.CELL_TYPE_IDX:
			cellDomains = Design._settings.idxCells;
			break;
		case Constants.CELL_TYPE_FORMULA:
			cellDomains = Design._settings.formulaCells;
			break;
		case Constants.CELL_TYPE_BJJS:
			cellDomains = Design._settings.idxCalcCells;
			break;
		case Constants.CELL_TYPE_EXPRESSION:
			cellDomains = Design._settings.staticCells;
			break;
		case Constants.CELL_TYPE_IDXCOL :
			cellDomains = Design._settings.colIdxCells;
			break;
		case Constants.CELL_TYPE_DIMCOL :
			cellDomains = Design._settings.colDimCells;
			break;
		}
		var refreshFlag = false;
		var sheet = Design.spread.getActiveSheet();
		var isShowIdxNm = true;
		if(cellDomains && (cellDomains.length > 0)){
			//如果单元格类型是指标，且指标单元格数量大于200，就不在报表上显示指标信息，为了展示效率
/*			if((Constants.CELL_TYPE_IDX == cellType) && (cellDomains.length > 200)){
				isShowIdxNm = false;
			}*/
			for(var i = 0 , l = cellDomains.length ; i < l ; i++){
				var cell = cellDomains[i];
				var rowNo = cell.rowId;
				var colNo = cell.colId;
				if(!rowNo || !colNo){
					continue;
				}
				if(needRefresh === true && i == (l - 1)){
					refreshFlag = true;
				}
				var rptTmp = RptIdx.newInstance(cellType);
				var seq = Uuid.v1();
				var rptObj = {};
				jQuery.extend(rptObj , rptTmp);
				jQuery.extend(rptObj , cell);
				rptObj.seq = seq;
				var currSheet = Design.spread.getActiveSheet();
				currSheet.setTag(rowNo, colNo, seq);
				Design.rptIdxs[seq] = rptObj;
				var cellLabelTmp = rptObj.cellLabel(Design._settings.showType);
				Design._initRptIdx(rowNo , colNo , seq , cellType , rptObj.excelFormula , cellLabelTmp , refreshFlag, sheet, isShowIdxNm);
			}
		}
	}
	
	Design._initRptIdx = function(rowNo , colNo , seq , cellType , formula , cellLabel , needRefresh, sheet, isShowIdxNm){
		if((Constants.CELL_TYPE_FORMULA != cellType) && isShowIdxNm){
			//非excel公式单元格，设置label
			sheet.setFormula(rowNo , colNo , "");
			//这个方法巨慢，一秒一次！！！
			sheet.setValue(rowNo , colNo , cellLabel);
		}
		if(needRefresh === true){
			sheet.invalidateLayout();
			sheet.repaint();
		}
	}
	
	Design.removeRptIdxs = function(cellDomains){
		if(cellDomains != null 
				&& typeof cellDomains != "undefined"
				&& cellDomains.length){
			for(var i = 0 , l = cellDomains.length ; i < l ; i++){
				var cell = cellDomains[i];
				var rowNo = cell.rowId;
				var colNo = cell.colId;
				if(rowNo == null
						|| typeof rowNo == "undefined"
						|| colNo == null
						|| typeof colNo == null){
					continue;
				}
				Design.spread.getActiveSheet().getCell(rowNo , colNo).value("");
			}
		}
	}
	
	Design.doKeyDown = function(){
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
				    var readonly = Design._settings.readOnly;
				    var ltype = (e.srcElement || e.target).ltype;
				    if(Design._settings.cellDetail === true){	
				    	// 明细div可以使用backspace
				    	var id = (e.srcElement || e.target).id;
				    	if(id == "_contextbox" && !$("#_contextbox").attr("readonly")){
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
		    if(key == 83 && e.ctrlKey){ // Ctrl + S
		    	// 调用保存方法
		    	if(typeof Design._settings.onSave == "function"){
		    		Design._settings.onSave();
		    	}
		    	return false;
		    }
		    
		    if(key == 69 && e.ctrlKey){ // Ctrl + E
		    	// 调用导入样式方法
		    	if(typeof Design._settings.onCssImport == "function"){
		    		Design._settings.onCssImport();
		    	}
		    	return false;
		    }
		    
		    if ((key == 116) || (e.ctrlKey && key == 82)) { // Ctrl + R || F5
		    	return false;
		    }
	
		    if ((e.ctrlKey) && (key == 78)) { // Ctrl+n
		    	return false;
		    }
	
		    if ((e.shiftKey) && (key == 121)) { // shift+F10
		    	return false;
		    }
		    
		    if((e.ctrlKey) && (key == 88)){ // Ctrl + X
		    	Design._clipBoard.helper.isCutting = true;
		    }
		    
		    if((e.ctrlKey) && (key == 89)){ // Ctrl + Y
		    	return false;
		    }
		    
		    if((e.ctrlKey) && (key == 90)){ // Ctrl + Z
		    	return false;
		    }
		    if(key == 113){//f2按钮，快速进入报表配置
		    	var currSheet = Design.spread.getActiveSheet();
				var selections = currSheet.getSelections();
				var currSelection = selections[0];
				if(currSelection){
					var tagName = (e.srcElement || e.target).tagName;
					var searchIdxNm = Design.spread.getActiveSheet().getCell(currSelection.row , currSelection.col).text();
					if('DIV' == tagName){
						searchIdxNm = (e.srcElement || e.target).textContent;
					}
					BIONE.commonOpenDialog("报表指标配置", "rptIdxCfg", 1200, 550, Context._ctx + "/report/frame/design/cfg/rptIdxCfg?searchIdxNm=" + searchIdxNm);
				}
		    }
		    if(key == 229){//=号按钮，进行excel公式编写
		    	var currSheet = Design.spread.getActiveSheet();
				var selections = currSheet.getSelections();
				var currSelection = selections[0];
				if(currSelection){
					var seqTmp = currSheet.getTag(currSelection.row , currSelection.col, GC.Spread.Sheets.SheetArea.viewport);
					if(seqTmp != null
							&& seqTmp != ""
							&& typeof seqTmp != "undefined"
							&& Design.rptIdxs[seqTmp] != null
							&& typeof Design.rptIdxs[seqTmp] == "object"){
						//之前的单元格属于指标或者表间取数单元格，不能进行修改
						return false;
					}
				}
		    }
		}
	}
	
	// 获取展现方式
	Design.getShowType = function(){
		return Design._settings.showType;
	}
	
	Design._removeRptIdx = function(sheet , row , col){
		var isIdx = false;
		var targetCell = sheet.getCell(row , col);
		var seqTmp = sheet.getTag(row, col, GC.Spread.Sheets.SheetArea.viewport);
		if(seqTmp){
			sheet.setTag(row, col, null);
			if(Design.rptIdxs[seqTmp]
					&& jQuery.inArray(Design.rptIdxs[seqTmp].cellType , Constants.idxCellTypes) != -1){
				//若是指标类报表指标
				isIdx = true;
			}
			delete Design.rptIdxs[seqTmp];
		}
		return isIdx;
	}
	
	// 翻转展现方式
	Design.changeShowType = function(){
		switch (Design._settings.showType) {
		case Constants.SHOW_TYPE_NORMAL :
			Design._settings.showType = Constants.SHOW_TYPE_CELLNM;
			break;
		case Constants.SHOW_TYPE_CELLNM :
			if(Design._settings.showBusiType === true){
				Design._settings.showType = Constants.SHOW_TYPE_BUSINO;
			}else{				
				Design._settings.showType = Constants.SHOW_TYPE_NORMAL;
			}
			break;
		case Constants.SHOW_TYPE_BUSINO :
			Design._settings.showType = Constants.SHOW_TYPE_NORMAL;
			break;
		}
		if(Design.rptIdxsSize() > 0
				|| Design.rowColsSize() > 0){
			var currSheet = Design.spread.getActiveSheet();
			var rowCount = currSheet.getRowCount();
			var colCount = currSheet.getColumnCount();
			currSheet.isPaintSuspended(true);
			f1: for(var r = 0 ; r < rowCount ; r++){
				f2: for(var c = 0 ; c < colCount ; c++){
					var seqTmp = currSheet.getTag(r, c, GC.Spread.Sheets.SheetArea.viewport);
					if(seqTmp != null
							&& seqTmp != ""
							&& typeof seqTmp != "undefined"
							&& Design.rptIdxs[seqTmp] != null
							&& typeof Design.rptIdxs[seqTmp] == "object"){
						var rptIdxTmp = Design.rptIdxs[seqTmp];
						if(Constants.CELL_TYPE_FORMULA == rptIdxTmp.cellType){
							// 不处理excel公式
							continue f2; 
						}
						Design._setCellValue(r , c , rptIdxTmp.cellLabel(Design._settings.showType) , currSheet);
					}
				}
			}
            currSheet.invalidateLayout();
			currSheet.isPaintSuspended(false);
            currSheet.repaint();
		}
	}
	
	// 获取当前报表公共维度
	// @return dimTypes数组
	Design.getCommonDimsByAjax = function(){
		var commonDims = [];
		// 获取所有的来源指标集合
		if(Design.rptIdxsSize() > 0){
			var srcIdxNos = [];
			var currSheet = Design.spread.getActiveSheet();
			// 获取全部cell，挨个分析是否有seq属性
			var rowCount = currSheet.getRowCount();
			var colCount = currSheet.getColumnCount();
			var cellOldVals = [];
			for(var r = 0 ; r < rowCount ; r++){
				for(var c = 0 ; c < colCount ; c++){
					// 一般单元格对象
					var seqTmp = currSheet.getTag(r, c, GC.Spread.Sheets.SheetArea.viewport);
					if(seqTmp != null
							&& seqTmp != ""
							&& typeof seqTmp != "undefined"
							&& Design.rptIdxs[seqTmp] != null
							&& typeof Design.rptIdxs[seqTmp] == "object"){
						if((Design.rptIdxs[seqTmp].cellType == Constants.CELL_TYPE_IDX
								|| Design.rptIdxs[seqTmp].cellType == Constants.CELL_TYPE_IDXCOL)
								&& Design.rptIdxs[seqTmp].indexNo){	
							// 若是指标单元格，且不是空指标
							if(Design.rptIdxs[seqTmp].indexNo){								
								srcIdxNos.push(Design.rptIdxs[seqTmp].indexNo);
							}
						}
					}
				}
			}
			if(srcIdxNos.length > 0){
				// 获取公共维度信息
				$.ajax({
					cache : false,
					async : false,
					url : Design._settings.ctx+"/report/frame/design/cfg/batch/getDimInfos",
					dataType : 'json',
					data : {
						idxNos : srcIdxNos.join(",")
					},
					type : "post",
					success : function(result){
						if(result
								&& typeof result.length != "undefined"){
							for(var i = 0 , l = result.length ; i < l ; i++){
								var dimTmp = result[i];
								if(!dimTmp.dimTypeNo
										|| typeof dimTmp.dimTypeNo == "undefined"){
									continue ;
								}
								commonDims.push(dimTmp);
							}
						}
					}
				});
			}
		}
		return commonDims;
	}
	
	// 清除不在指定维度内的列表维度报表指标（templateType为指标报表时有效）
	// @params allDimArray 
	//                selectIdxKo   选中的单元格knockout对象
	//                currCellNo    选中的单元格cellNo
	Design.clearDimCells = function(allDimArray , selectIdxKo , currCellNo){
		if((Design._settings.templateType != Constants.TEMPLATE_TYPE_IDXCOL_V
				&& Design._settings.templateType != Constants.TEMPLATE_TYPE_IDXCOL_H)
				|| !allDimArray
				|| typeof allDimArray.length == "undefined"){
			return ;
		}
		if(Design.rptIdxsSize() > 0){
			var currSheet = Design.spread.getActiveSheet();
			// 获取全部cell，挨个分析是否有seq属性
			var rowCount = currSheet.getRowCount();
			var colCount = currSheet.getColumnCount();
			var cellOldVals = [];
			for(var r = 0 ; r < rowCount ; r++){
				for(var c = 0 ; c < colCount ; c++){
					// 一般单元格对象
					var cellTmp = currSheet.getCell( r , c);
					var seqTmp = currSheet.getTag(r, c, GC.Spread.Sheets.SheetArea.viewport);
					if(seqTmp != null
							&& seqTmp != ""
							&& typeof seqTmp != "undefined"
							&& Design.rptIdxs[seqTmp] != null
							&& typeof Design.rptIdxs[seqTmp] == "object"){
						var nodeTmp = Design.rptIdxs[seqTmp];
						if(nodeTmp.cellType == Constants.CELL_TYPE_DIMCOL
								&& nodeTmp.dimTypeNo
								&& jQuery.inArray(nodeTmp.dimTypeNo , allDimArray) == -1){
							// 若单元格是列表维度单元格，且该维度已不存在于allDimArray中，移除
							//cellTmp.clearStyleProperty("seq");
							//delete Design.rptIdxs[seqTmp];
							Design.rptIdxs[seqTmp] = RptIdxInfo.newInstance(Design.Constants.CELL_TYPE_COMMON);
							cellTmp.textDecoration(GC.Spread.Sheets.TextDecorationType.LineThrough);
							if(selectIdxKo
									&& typeof selectIdxKo.cellNo == "function"
									&& currCellNo != null
									&& currCellNo != ""){
								var analyseCellNo = Utils.initAreaPosiLabel(r , c);
								if(analyseCellNo === currCellNo){
									// 若移除的列表维度就是当前选中的分析信息对象，重置成一般单元格
									Design.RptIdxInfo.initIdxKO(selectIdxKo);
								}
							}
						}
					}
				}
			}
		}
	}
	
	// 获取当前报表的数据行（指标明细表时有效）
	Design.getCurrDataRow = function(){
		if(Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_V || Design._settings.templateType === Constants.TEMPLATE_TYPE_MODULE || Design._settings.templateType === Constants.TEMPLATE_TYPE_CROSS){
			// 指标明细表/明细报表  进行分析
			var currSheet = Design.spread.getActiveSheet();
			// 挨个分析是否是列表指标或列表维度
			var rowCount = currSheet.getRowCount();
			var colCount = currSheet.getColumnCount();
			f1 : for(var r = 0 ; r < rowCount ; r++){
				f2 : for(var c = 0 ; c < colCount ; c++){
					// 一般单元格对象
					var seqTmp = currSheet.getTag(r, c, GC.Spread.Sheets.SheetArea.viewport);
					if(seqTmp != null
							&& seqTmp != ""
							&& typeof seqTmp != "undefined"
							&& Design.rptIdxs[seqTmp] != null
							&& typeof Design.rptIdxs[seqTmp] == "object"){
						switch (Design._settings.templateType){
							case Constants.TEMPLATE_TYPE_MODULE :
								if(Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_MODULE){
									// 明细单元格
									return r;
								}
								break;
							case	Constants.TEMPLATE_TYPE_IDXCOL_V :
							case	Constants.TEMPLATE_TYPE_IDXCOL_H :
								if(Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_IDXCOL
										|| Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_DIMCOL
										|| Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_FORMULA){
									// 列表指标；列表维度；excel公式
									return r;
								}
								break;
							case    Constants.TEMPLATE_TYPE_CROSS :
								if(Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_IDXCOL
										|| Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_FORMULA){
									// 列表指标；列表维度；excel公式
									return r;
								}
								break;
						}
					}
				}
			}
		}
		return null;
	}
	
	// 获取当前报表的数据列（指标明细表（横）时有效）
	Design.getCurrDataCol = function(){
		if(Design._settings.templateType === Constants.TEMPLATE_TYPE_IDXCOL_H || Design._settings.templateType === Constants.TEMPLATE_TYPE_CROSS){
			// 指标明细表/明细报表  进行分析
			var currSheet = Design.spread.getActiveSheet();
			// 挨个分析是否是列表指标或列表维度
			var rowCount = currSheet.getRowCount();
			var colCount = currSheet.getColumnCount();
			f1 : for(var r = 0 ; r < rowCount ; r++){
				f2 : for(var c = 0 ; c < colCount ; c++){
					// 一般单元格对象
					var seqTmp = currSheet.getTag(r, c, GC.Spread.Sheets.SheetArea.viewport);
					if(seqTmp != null
							&& seqTmp != ""
							&& typeof seqTmp != "undefined"
							&& Design.rptIdxs[seqTmp] != null
							&& typeof Design.rptIdxs[seqTmp] == "object"){
						switch (Design._settings.templateType){
							case Constants.TEMPLATE_TYPE_MODULE :
								if(Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_MODULE){
									// 明细单元格
									return c;
								}
								break;
							case	Constants.TEMPLATE_TYPE_IDXCOL_V :
							case	Constants.TEMPLATE_TYPE_IDXCOL_H :
								if(Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_IDXCOL
										|| Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_DIMCOL
										|| Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_FORMULA){
									// 列表指标；列表维度；excel公式
									return c;
								}
								break;
							case    Constants.TEMPLATE_TYPE_CROSS :
								if(Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_IDXCOL
										|| Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_FORMULA){
									// 列表指标；列表维度；excel公式
									return r;
								}
								break;
						}
					}
				}
			}
		}
		return null;
	}
	
	Design.getCurrIdxCol = function(){
		if(Design._settings.templateType === Constants.TEMPLATE_TYPE_CROSS){
			// 指标明细表/明细报表  进行分析
			var currSheet = Design.spread.getActiveSheet();
			// 挨个分析是否是列表指标或列表维度
			var rowCount = currSheet.getRowCount();
			var colCount = currSheet.getColumnCount();
			f1 : for(var r = 0 ; r < rowCount ; r++){
				f2 : for(var c = 0 ; c < colCount ; c++){
					// 一般单元格对象
					var seqTmp = currSheet.getTag(r, c, GC.Spread.Sheets.SheetArea.viewport);
					if(seqTmp != null
							&& seqTmp != ""
							&& typeof seqTmp != "undefined"
							&& Design.rptIdxs[seqTmp] != null
							&& typeof Design.rptIdxs[seqTmp] == "object"){
						if(Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_IDXCOL
								|| Design.rptIdxs[seqTmp].cellType === Constants.CELL_TYPE_FORMULA){
							// 列表指标；列表维度；excel公式
							return {
								r : r,
								c : c
							};
						}
					}
				}
			}
		}
		return null;
	}
	
	//自动调整列宽
	Design.autoSetColumnWidth = function(currSheet,col){
		if(autoColumnWidth){
			var width=currSheet.getColumnWidth(col);
			currSheet.autoFitColumn(col);
			if(width<=currSheet.getColumnWidth(col)+30)
				currSheet.setColumnWidth(col,currSheet.getColumnWidth(col)+30);//由于自动调整的列宽过于贴近字符长度故添加30px留白
		}
	}
	
	// 触发离开当前单元格事件
	Design.leaveCurrCell = function(){
		var sheet = Design.spread.getActiveSheet();
		sheet.endEdit();
		var args = {
				sheet: sheet, sheetName: sheet._name, row: sheet.getActiveRowIndex(), col: sheet.getActiveColumnIndex(), cancel: false
        };
        Design._onLeaveCell(null, args);
	}
	Design.clearCellTarget = function() {
		//清除当前从选中状态
		var currSheet = Design.spread.getActiveSheet();
		currSheet._clearSelectionImp();
	}
	// 获取批量共有属性对象
	Design.generateBatchSelObj = function(){
		var currSheet = Design.spread.getActiveSheet();
		var selections = currSheet.getSelections();
		var batchObj = null;
		var seqs = "";
		var flag = "01";//批量设置先只对指标单元格生效
		sloop:for(var i = 0 , j = selections.length ; i < j ; i++){
			var selectTmp = selections[i];
			rloop:for(var r = 0, rl = selectTmp.rowCount ; r < rl ; r++){
				cloop:for(var c = 0 , cl = selectTmp.colCount ; c < cl ; c++){
					var currCell = currSheet.getCell(selectTmp.row + r , selectTmp.col + c);
					var seqTmp = currSheet.getTag(selectTmp.row + r, selectTmp.col + c, GC.Spread.Sheets.SheetArea.viewport);
					if(seqTmp == null || seqTmp == ""
							|| typeof seqTmp == "undefined"){
						var isFormula = (currCell.formula() != null && currCell.formula() != "") ? true : false;
						if(isFormula === true){							
							var uuid = Design.Uuid.v1();
							currSheet.setTag(selectTmp.row + r, selectTmp.col + c, uuid);
							var rptIdxInfoTmp = Design.RptIdxInfo.newInstance(Constants.CELL_TYPE_FORMULA);
							rptIdxInfoTmp.excelFormula = currCell.formula();
							var realIndexNo = Design.Uuid.v1().replace(/-/g, '');
							var currLabel = Utils.initAreaPosiLabel(currCell.row, currCell.col);
							//指标配置表存在，则使用配置表中的“报表指标编号”
							if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
								realIndexNo = Design.rptIdxCfg[currLabel];
							}
							rptIdxInfoTmp.realIndexNo = realIndexNo;
							Design.rptIdxs[uuid] = rptIdxInfoTmp;
							seqTmp = uuid;
						}else{
							// 包含非报表指标 , 不处理
							var uuid = Design.Uuid.v1();
							currSheet.setTag(selectTmp.row + r, selectTmp.col + c, uuid);
							var commonCellTmp = Design.RptIdxInfo.newInstance(Constants.CELL_TYPE_COMMON);
							commonCellTmp.content = currCell.value();
							Design.commonCells[uuid] = commonCellTmp;
							seqTmp = uuid;
						}
					}
					if(Design
							&& (Design.rptIdxs || Design.commonCells)){
						var cflag = "";
						var selRptIdx = Design.rptIdxs[seqTmp];
						var commonCell = Design.commonCells[seqTmp];
						if(selRptIdx != null
								&& typeof selRptIdx != "undefined"){
							cflag = "01";
						} 
						if(commonCell != null 
										|| typeof commonCell != "undefined"){
							cflag = "02";
						}
						if(cflag == ""){
							continue cloop;
						}
						//批量设置先只对指标单元格生效
/*						if(flag != "" && flag != cflag){
							return {
								cellType : "-1",
								cellTypeNm : "批量处理"
							}
						}
						else{
							flag = cflag;
						}*/
						if(flag == cflag){
							// 记录seq
							if(seqs){
								seqs += ",";
							}
							seqs += seqTmp;
							if(flag == "01"){
								if(batchObj == null){
									// 分析的第一个单元格
									batchObj = {};
									for(var attr in selRptIdx){
										batchObj[attr] = selRptIdx[attr];
									}
								}else{
									var reg = /^_.*Batch$/;
									battrs : for(var attr in batchObj){
										if(reg.test(attr)){
											continue battrs;
										}
										if(typeof selRptIdx[attr] == "undefined"
												|| selRptIdx[attr] == null){
											batchObj[attr] = null;
											batchObj["_"+attr+"Batch"] = false;
										}
									}
									sattrs : for(var attr in selRptIdx){
										if(typeof batchObj[attr] != "undefined"
												&& batchObj[attr] != null){									
											if(selRptIdx[attr] != batchObj[attr]){
												// 当选中的值不一致时,使用特定的值显示(基本都是显示空)
												var defaultVal = "";
												if(typeof RptIdxInfo._defaultVals[attr] != "undefined"){
													defaultVal = RptIdxInfo._defaultVals[attr];
												}
												batchObj[attr] = defaultVal;
											}
											batchObj["_"+attr+"Batch"] = true;
										} else {
											// 非共性属性，置为undefined，且新增一个对应属性来判断该属性是否是共性属性
											batchObj[attr] = null;
											batchObj["_"+attr+"Batch"] = false;
										}
										if(batchObj["_cellNmBatch"]){//单元格名称不能重复，所以不能统一设置
											batchObj["_cellNmBatch"] = false;
										}
										if(!batchObj["_dataUnitBatch"]){//单元格单位可以统一设置
											batchObj["_dataUnitBatch"] = true;
										}
									}
								}
							}
							if(flag == "02"){
								if(batchObj == null){
									// 分析的第一个单元格
									batchObj = {};
									for(var attr in commonCell){
										batchObj[attr] = commonCell[attr];
									}
									batchObj["_typeIdBatch"] = true;
								}
								else{
									if(commonCell.typeId != batchObj.typeId){
										batchObj.typeId ="";
									}
								}
							}
						}
					}
				}
			}
		}
		if(batchObj != null){	
			batchObj.cellType = "-1";
			batchObj.cellTypeNm = "批量处理";
			batchObj.seq = seqs;
		}
		return batchObj;
	}
	
	/**  以下方法由于只是单纯的赋值，没考虑样式渲染，在批量赋值完成后需要使用一次sheet.invalidate()方法  **/
	
	// 设置单元格样式，
	// 与spread原生方法(Cell._setStyleProperty)的差别是，该方法丢弃了一切影响渲染速度的判断逻辑，只是单纯的附上样式
	Design._setCellStyle = function(row , col , styleNm , styleVal , sheet){
		if(!sheet){
			sheet = Design.spread.getActiveSheet();
		}
		var style = sheet.getStyle(row, col);
	    if (!style)
	    {
	        style = {};
	    }
	    style[styleNm] = styleVal;
	    sheet.setStyle(row , col , style);
	}
	
	// 设置单元格值
	// 与spread原生方法(Cell.value or Sheet.setValue)的差别是，该方法丢弃了一切影响渲染速度的判断逻辑，只是单纯的赋值
	Design._setCellValue = function(row , col , val , sheet){
		if(!sheet){
			sheet = Design.spread.getActiveSheet();
		}
		sheet.setValue(row , col , val);
	}
	
	//设置单元格公式
	//与spread原生方法(Cell.formula)的差别是，该方法丢弃了一切影响渲染速度的判断逻辑，只是单纯的赋公式
	Design._setCellFormula = function(row , col , formula , sheet){
		if(!sheet){
			sheet = Design.spread.getActiveSheet();
		}
		sheet.setFormula(row , col , formula);
	}
	
	//删除指标时，进行影响分析，如果下游有引用那就不能删
	Design._impactAna = function(sheet, row, col, currCellValue){
		var targetCell = sheet.getCell(row , col);
		var seqTmp = sheet.getTag(row, col, GC.Spread.Sheets.SheetArea.viewport);
		if(seqTmp){
			if(Design.rptIdxs[seqTmp] && (jQuery.inArray(Design.rptIdxs[seqTmp].cellType, Constants.idxCellTypes) != -1)){
				//若是指标类报表指标，进行影响查询
				var idx = Design.rptIdxs[seqTmp];
				$.ajax({
					async : true,
					url : Design._settings.ctx + "/report/frame/design/cfg/rptIdxInfluence",
					dataType : 'json',
					data : {
						indexNo : idx.realIndexNo,
						indexVerId : idx.indexVerId
					},
					beforeSend : function() {
			      		BIONE.loading = true;
		          		BIONE.showLoading("下游影响分析中....");
					},
					complete : function() {
						BIONE.loading = false;
						BIONE.hideLoading();
					},
					success : function(result){
						if(result){
							//如果有影响的报表或者有逻辑校验或者警戒值校验
							if('yes' == result.isRpt || 'yes' == result.isLogic || 'yes' == result.isWarn){
								BIONE.commonOpenDialog("影响查看",
										"idxInfluence",900, $(document).height(),
										Design._settings.ctx + "/report/frame/design/cfg/rptIdxpage?indexNo=" + idx.realIndexNo + "&indexVerId=" + idx.indexVerId + "&isRpt=" 
										+ result.isRpt + "&isLogic=" + result.isLogic + "&isWarn=" + result.isWarn + "&currCellValue=" + currCellValue, null, Design._recoveryCellVal(currCellValue));
							}else{
								var currCell = sheet.getCell(row , col);
								currCell.value("");
        						currCell.formula("");
        						currCell.value("");
								Design._removeRptIdx(sheet ,row ,col);
							}
						}
					},
					error:function(){
						BIONE.tip("数据加载异常，请联系系统管理员");
					}
				});
			}else{//如果不是，就直接删除
				var currCell = sheet.getCell(row , col);
				currCell.value("");
				currCell.formula("");
				currCell.value("");
				Design._removeRptIdx(sheet ,row ,col);
			}
		}
	}
	
	//删除指标数据
	Design._deleteIdx = function(){
    	var currSheet = Design.spread.getActiveSheet();
    	var selectionModules = currSheet.getSelections();
		if(selectionModules.length 
				&& selectionModules.length > 0){
			var hasIdx = false;
			for(var i = 0 , j = selectionModules.length ; i < j ; i++){
				// 选中的多块区域
				var beginRow = selectionModules[i].row < 0 ? 0 : selectionModules[i].row;
				var endRow = beginRow + selectionModules[i].rowCount - 1;
				var beginCol = selectionModules[i].col < 0 ? 0 : selectionModules[i].col;
				var endCol = beginCol + selectionModules[i].colCount - 1;
				for(var m = beginRow , n = endRow ; m <= n ; m++){
					// 行
					for(var k = beginCol , l = endCol ; k <= l ; k++){
						// 列
	            		var currCell = currSheet.getCell(m , k);
						currCell.value("");
						var isIdx = Design._removeRptIdx(currSheet , m , k);
					}
				}
			}
		}
	}
	
	//恢复单元格名称
	Design._recoveryCellVal = function(currCellValue){
    	var currSheet = Design.spread.getActiveSheet();
    	var selectionModules = currSheet.getSelections();
		if(selectionModules.length 
				&& selectionModules.length > 0){
			var hasIdx = false;
			for(var i = 0 , j = selectionModules.length ; i < j ; i++){
				// 选中的多块区域
				var beginRow = selectionModules[i].row < 0 ? 0 : selectionModules[i].row;
				var endRow = beginRow + selectionModules[i].rowCount - 1;
				var beginCol = selectionModules[i].col < 0 ? 0 : selectionModules[i].col;
				var endCol = beginCol + selectionModules[i].colCount - 1;
				for(var m = beginRow , n = endRow ; m <= n ; m++){
					// 行
					for(var k = beginCol , l = endCol ; k <= l ; k++){
						// 列
	            		Design._setCellValue(m, k, currCellValue, currSheet);
					}
				}
			}
		}
	}
	
	/**  method end  **/
    	
	return Design;
})