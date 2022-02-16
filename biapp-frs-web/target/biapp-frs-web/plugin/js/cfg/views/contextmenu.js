/*******************************************************/
/****                        报表设计器 - 右键菜单                     ****/
/** *************************************************** */
define(["uuid" , "rptIdx" , "rowCol", "utils" , "constants"] , function(Uuid , RptIdx , RowCol , Utils , Constants) {
	var Context = {};
	
	Context._templateType ;
	
	/**
	 * 初始化右键菜单
	 */
	Context.initContextMenu = function(spread , templateType , ctx , isBusiLine, isRptIdxCfg) {
		Context._spread = spread;
		Context._templateType = templateType ? templateType : Constants.TEMPLATE_TYPE_MODULE;
		Context._ctx = ctx;
		
		var menuView = Context._spread.contextMenu.menuView;
		while (Context._spread.contextMenu.menuData.length > 0) {
			Context._spread.contextMenu.menuData.pop();
		}
		
		//创建自定义菜单函数
		initCustomMenu();
		
		var contextMenu = [{
			text: '复制(Ctrl+C)',
			name: 'gc.spread.copy',
			command: 'gc.spread.contextMenu.copy',
			iconClass: "gc-spread-copy",
			workArea: 'viewportcolHeaderrowHeaderslicercorner'
		}, {
			text: '剪切(Ctrl+X)',
			name: 'gc.spread.cut',
			command: 'gc.spread.contextMenu.cut',
			iconClass: "gc-spread-cut",
			workArea: 'viewportcolHeaderrowHeaderslicercorner'
		}, {
			text: '粘贴(Ctrl+V)',
			name: 'gc.spread.pasteAll',
			command: 'gc.spread.contextMenu.pasteAll',
			iconClass: "gc-spread-pasteAll",
			workArea: 'viewportcolHeaderrowHeaderslicercorner'
		}, {
			text: '清除(Delete)',
			name: 'clear',
			command: 'clear',
			iconClass: 'gc-spread-deleteComment',
			workArea: 'viewportcolHeaderrowHeadercorner'
		}, {
			type: 'separator'
		}, {
			text: '插入行',
			name: 'gc.spread.insertRows',
			command: 'gc.spread.contextMenu.insertRows',
			iconClass: 'gc-spread-insertComment',
			workArea: 'rowHeader'
		}, {
			text: '插入列',
			name: 'gc.spread.insertColumns',
			command: 'gc.spread.contextMenu.insertColumns',
			iconClass: 'gc-spread-insertComment',
			workArea: 'colHeader'
		}, {
			text: '删除行',
			name: 'gc.spread.deleteRows',
			command: 'deleteRows',
			iconClass: 'gc-spread-deleteComment',
			workArea: 'rowHeader'
		}, {
			text: '删除列',
			name: 'gc.spread.deleteColumns',
			command: 'deleteColumns',
			iconClass: 'gc-spread-deleteComment',
			workArea: 'colHeader'
		}, {
			text: '设置行高',
			name: 'setRowsHeight',
			command: 'setRowsHeight',
			iconClass: 'gc-spread-insertComment',
			workArea: 'rowHeader'
		}, {
			text: '设置列宽',
			name: 'setColumnsWidth',
			command: 'setColumnsWidth',
			iconClass: 'gc-spread-insertComment',
			workArea: 'colHeader'
		}, {
			text: '隐藏行',
			name: 'gc.spread.hideRows',
			command: 'gc.spread.contextMenu.hideRows',
			iconClass: 'gc-spread-deleteComment',
			workArea: 'rowHeader'
		}, {
			text: '取消隐藏行',
			name: 'gc.spread.unhideRows',
			command: 'gc.spread.contextMenu.unhideRows',
			iconClass: 'gc-spread-insertComment',
			workArea: 'rowHeader'
		}, {
			text: '隐藏列',
			name: 'gc.spread.hideColumns',
			command: 'gc.spread.contextMenu.hideColumns',
			iconClass: 'gc-spread-deleteComment',
			workArea: 'colHeader'
		}, {
			text: '取消隐藏列',
			name: 'gc.spread.unhideColumns',
			command: 'gc.spread.contextMenu.unhideColumns',
			iconClass: 'gc-spread-insertComment',
			workArea: 'colHeader'
		}, {
			text: '合并',
			name: 'merge',
			command: 'merge',
			iconClass: 'gc-spread-pasteFormatting',
			workArea: 'viewportcorner'
		}, {
			text: '取消合并',
			name: 'unmerge',
			command: 'unmerge',
			iconClass: 'gc-spread-pasteValues',
			workArea: 'viewportcorner'
		}, {
			type: 'separator',
		}, {
			text: '设置表达式',
			name: 'bds',
			command: 'bds',
			iconClass: 'gc-spread-pasteFormula',
			workArea: 'viewport'
		}, {
			text: '设置空指标',
			name: 'kzb',
			command: 'kzb',
			iconClass: 'gc-spread-pasteValues',
			workArea: 'viewport'
		}, {
			text: '设置指标(F2)',
			name: 'rptIdxCfg',
			command: 'rptIdxCfg',
			iconClass: 'gc-spread-pasteValues',
			workArea: 'viewport'
		}, {
			text: '设置表间取数',
			name: 'bjqs',
			command: 'bjqs',
			iconClass: 'gc-spread-pasteFormula',
			workArea: 'viewport'
		}, {
			text: '批量过滤',
			name: 'filtSetting',
			command: 'filtSetting',
			iconClass: 'gc-spread-pasteFormula',
			workArea: 'colHeader'
		}, {
			type: 'separator',
		}, {
			text: '插入批注',
			name: 'gc.spread.insertComment',
			command: 'gc.spread.contextMenu.insertComment',
			iconClass: 'gc-spread-insertComment', 
			workArea: 'viewportcorner'
		}, {
			text: '编辑批注',
			name: 'gc.spread.editComment',
			command: 'gc.spread.contextMenu.editComment',
			iconClass: 'gc-spread-editComment',
			workArea: 'viewportcorner'
		}, {
			text: '删除批注',
			name: 'gc.spread.deleteComment',
			command: 'gc.spread.contextMenu.deleteComment',
			iconClass: 'gc-spread-deleteComment',
			workArea: 'viewportcorner'
		}];

		for (i in contextMenu) {
			Context._spread.contextMenu.menuData.push(contextMenu[i]);
		}
	}

	function initCustomMenu(){
	/*
	*  自定义设置空指标函数
	* */
	Context._spread.commandManager().register("kzb",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		if(window.Design
         				&& typeof window.Design.spread != "undefined"){			
         			if(Constants.TEMPLATE_TYPE_MODULE == window.templateType){
         				BIONE.tip("明细类型报表不可以设置指标");
         				return;
         			}
         			var currSheet = window.Design.spread.getActiveSheet();
         			var selectionModules = currSheet.getSelections();
         			if(selectionModules.length 
         					&& selectionModules.length > 0){
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
         							window.initEmptyIdxCell(currSheet , currSheet.getCell(m,k));
         						}
         					}
         				}
         			}
         			currSheet.invalidate();
         		}
             }
         });
	
	/*
	*  自定义设置指标函数
	* */
	Context._spread.commandManager().register("rptIdxCfg",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		if(window.Design
         				&& typeof window.Design.spread != "undefined"){
         			if(Constants.TEMPLATE_TYPE_MODULE == window.templateType){
         				BIONE.tip("明细类型报表不可以设置指标");
         				return;
         			}
         			var currSheet = window.Design.spread.getActiveSheet();
    				var selections = currSheet.getSelections();
    				var currSelection = selections[0];
    				if(currSelection){
    					var searchIdxNm = Design.spread.getActiveSheet().getCell(currSelection.row , currSelection.col).text();
    					BIONE.commonOpenDialog("报表指标配置", "rptIdxCfg", 1200, 550, Context._ctx + "/report/frame/design/cfg/rptIdxCfg?searchIdxNm=" + searchIdxNm);
    				}
         		}
             }
         });
	
	/*
	*  自定义清除函数
	* */
	Context._spread.commandManager().register("clear",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
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
        					}
        				}
        			}
        		}
        		return false;
             }
         });
	
	/*
	*  自定义删除行函数
	* */
	Context._spread.commandManager().register("deleteRows",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var sheet = window.Design.spread.getActiveSheet();
        		var selectionModules = sheet.getSelections();
        		if(selectionModules.length 
        				&& selectionModules.length > 0){
        			var flag = false;
        			if((Context._templateType === Constants.TEMPLATE_TYPE_IDXCOL_V || Context._templateType === Constants.TEMPLATE_TYPE_IDXCOL_H)
        					&& window.Design
        					&& window.Design._settings.onIdxsChanged
        					&& typeof window.Design._settings.onIdxsChanged == "function"){
        				flag = true;
        			}
        			var hasIdx = false;
        			var beginRow = selectionModules[0].row < 0 ? 0 : selectionModules[0].row;
        			var rowCount = selectionModules[0].rowCount;
        			var beginCol = selectionModules[0].col < 0 ? 0 : selectionModules[0].col;
        			var colCount = selectionModules[0].colCount;
        			if(flag === true){
        				// 判断待删除区域中是否有指标类单元格
        				f1 : for(var k = 0 ; k < rowCount ; k++){
        					f2 : for(var b = 0 , t = sheet.getColumnCount() ; b < t ; b++){
								var seqTmp = sheet.getTag(beginRow+k, b, GC.Spread.Sheets.SheetArea.viewport);
        						if(seqTmp
        								&& window.Design.rptIdxs[seqTmp]
        								&& jQuery.inArray(window.Design.rptIdxs[seqTmp].cellType , Constants.idxCellTypes) != -1){
        							hasIdx = true;
        							break f1;
        						}
        					}
        				}
        			}
        			sheet.deleteRows(beginRow , rowCount);
        			if(hasIdx === true){
        				// 触发指标类报表指标变化事件
        				window.Design._settings.onIdxsChanged();
        			}
        		}
             }
         });
	
	
	/*
	*  自定义删除列函数
	* */
	Context._spread.commandManager().register("deleteColumns",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var sheet = window.Design.spread.getActiveSheet();
        		var selectionModules = sheet.getSelections();
        		if(selectionModules.length 
        				&& selectionModules.length > 0){
        			var flag = false;
        			if((Context._templateType === Constants.TEMPLATE_TYPE_IDXCOL_V || Context._templateType === Constants.TEMPLATE_TYPE_IDXCOL_H)
        					&& window.Design
        					&& window.Design._settings.onIdxsChanged
        					&& typeof window.Design._settings.onIdxsChanged == "function"){
        				flag = true;
        			}
        			var hasIdx = false;
        			var beginRow = selectionModules[0].row < 0 ? 0 : selectionModules[0].row;
        			var rowCount = selectionModules[0].rowCount;
        			var beginCol = selectionModules[0].col < 0 ? 0 : selectionModules[0].col;
        			var colCount = selectionModules[0].colCount;
        			if(flag === true){
        				// 判断待删除区域中是否有指标类单元格
        				f1 : for(var k = 0 ; k < colCount ; k++){
        					f2 : for(var b = 0 , t = sheet.getRowCount() ; b < t ; b++){
								var seqTmp =  sheet.getTag(b, beginCol + k, GC.Spread.Sheets.SheetArea.viewport);
        						if(seqTmp
        								&& window.Design.rptIdxs[seqTmp]
        								&& jQuery.inArray(window.Design.rptIdxs[seqTmp].cellType , Constants.idxCellTypes) != -1){
        							hasIdx = true;
        							break f1;
        						}
        					}
        				}
        			}
        			sheet.deleteColumns(	beginCol, colCount);
        			if(hasIdx === true){
        				// 触发指标类报表指标变化事件
        				window.Design._settings.onIdxsChanged();
        			}
        		}
             }
         });
	
	
	/*
	*  自定义设置行高函数
	* */
	Context._spread.commandManager().register("setRowsHeight",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var sheet = window.Design.spread.getActiveSheet();
        		var selections = sheet.getSelections();
        		if (selections && selections.length == 1) {
        			var num = "";
        			var row = selections[0].row;
        			var col = selections[0].col;
        			var rowCount = selections[0].rowCount;
        			if (col == -1) {
        				for (var i = 0; i < rowCount; i ++) {
        					if (num == ""){
        						num = sheet.getRowHeight(row + i);
        					} else if (num != sheet.getRowHeight(row + i)) {
        						num = "";
        						break;
        					}
        				}
        			}
        		}
        		BIONE.commonOpenDialog("设置行高", "numWin", 300, 150,
        				Context._ctx + "/report/frame/design/cfg/rowcolInfo?type=02&num=" + num);
             }
         });
	
	
	/*
	*  自定义设置列宽函数
	* */
	Context._spread.commandManager().register("setColumnsWidth",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var sheet = window.Design.spread.getActiveSheet();
        		var selections = sheet.getSelections();
        		if (selections && selections.length == 1) {
        			var num = "";
        			var row = selections[0].row;
        			var col = selections[0].col;
        			var colCount = selections[0].colCount;
        			if (row == -1) {
        				for (var i = 0; i < colCount; i ++) {
        					if (num == ""){
        						num = sheet.getColumnWidth(col + i);
        					} else if (num != sheet.getColumnWidth(col + i)) {
        						num = "";
        						break;
        					}
        				}
        			}
        		}
        		BIONE.commonOpenDialog("设置列宽", "numWin", 300, 150,
        				Context._ctx + "/report/frame/design/cfg/rowcolInfo?type=01&num=" + num);
             }
         });
	
	/*
	*  自定义设置合并函数
	* */
	Context._spread.commandManager().register("merge",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var sheet = window.Design.spread.getActiveSheet();
        		var sel = sheet.getSelections();
        		if (sel.length > 0) {
        			sel = sel[sel.length - 1];
        			var cellValue = "";
        			var cellTag = "";
        			for(var i=0;i<sel.rowCount;i++){
        				for(var j =0 ; j < sel.colCount ; j++){
        					var value = sheet.getCell(sel.row+i , sel.col+j).value();
        					if(value && value != ""){
        						cellValue = value;
        						cellTag = sheet.getTag(sel.row+i, sel.col+j, GC.Spread.Sheets.SheetArea.viewport);
        						break;
        					}
        				}
        			}
        			sheet.getCell(sel.row , sel.col).value(cellValue);
        			sheet.setTag(sel.row, sel.col, cellTag);
        			for(var i=0;i<sel.rowCount;i++){
        				for(var j =0 ; j < sel.colCount ; j++){
        					if(i!=0 || j!=0){
        						sheet.getCell(sel.row+i , sel.col+j).value(null);
        						sheet.setTag(sel.row+i, sel.col+j, null);
        					}
        				}
        			}
        			sheet.addSpan(sel.row, sel.col, sel.rowCount, sel.colCount);
        		}
             }
         });
	
	/*
	*  自定义设置取消合并函数
	* */
	Context._spread.commandManager().register("unmerge",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var sheet = window.Design.spread.getActiveSheet();
        		var selections = sheet.getSelections();
        		if (selections && selections.length == 1) {
        			var range = getActualCellRange(selections[selections.length - 1], sheet.getRowCount(), sheet.getColumnCount());
        			sheet.suspendPaint();
        			for (var i = 0; i < range.rowCount; i ++) {
        				for (var j = 0; j < range.colCount; j ++) {
        					sheet.removeSpan(i + range.row, j + range.col);
        				}
        			}
        			sheet.resumePaint();
        		}
             }
         });
	
	/*
	*  自定义设置表达式函数
	* */
	Context._spread.commandManager().register("bds",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var width = RptIdx.templateSettings[Constants.CELL_TYPE_EXPRESSION].dialogWidth;
        		var height = RptIdx.templateSettings[Constants.CELL_TYPE_EXPRESSION].dialogHeight;
        		width = Utils.transWidthOrHeight(width , $(window.parent).width());
        		height = Utils.transWidthOrHeight(height , $(window.parent).height());
        		BIONE.commonOpenDialog('表达式设置','moduleClkDialog'
        				,width
        				,height
        				,Context._ctx+'/report/frame/design/cfg/sysVarSet?d='+new Date().getTime());
             }
         });
	
	/*
	*  自定义设置表间取数函数
	* */
	Context._spread.commandManager().register("bjqs",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		if(Constants.TEMPLATE_TYPE_MODULE == window.templateType){
     				BIONE.tip("明细类型报表不可以设置表间取数");
     				return;
     			}
				var width = RptIdx.templateSettings[Constants.CELL_TYPE_BJJS].dialogWidth;
				var height = RptIdx.templateSettings[Constants.CELL_TYPE_BJJS].dialogHeight;
				width = Utils.transWidthOrHeight(width , $(window.parent).width());
				height = Utils.transWidthOrHeight(height , $(window.parent).height());
				var currSheet = Design.spread.getActiveSheet();
				var selectionModules = currSheet.getSelections();
				if(selectionModules.length == 1){
					var cellNo = Utils.initAreaPosiLabel(selectionModules[0].row, selectionModules[0].col);
					BIONE.commonOpenDialog('[' + cellNo + ']单元格表间取数','moduleClkDialog'
							,width
							,height
							,Context._ctx+'/report/frame/valid/logic/getData?d='+new Date().getTime());
				}
             }
         });
	
	/*
	*  自定义设置批量过滤函数
	* */
	Context._spread.commandManager().register("filtSetting",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var currSheet = window.Design.spread.getActiveSheet();
        		var selectionModules = currSheet.getSelections();
        		if(selectionModules
        				&& typeof selectionModules.length != "undefined"
        				&& selectionModules.length == 1){
        			var row = selectionModules[0].row;
        			var rowCount = selectionModules[0].rowCount;
        			var col = selectionModules[0].col;
        			var colCount = selectionModules[0].colCount;
        			if((row == -1 || col == -1) 
        					&& (rowCount > 0 || colCount > 0) 
        					&& (Design && Design.rptIdxs)){
        				if(rowCount == -1){
    						rowCount = currSheet.getRowCount();
    					}
    					if(colCount == -1){
    						colCount = currSheet.getColumnCount();
    					}
        				var finalIdxNos = [];
        				rloop:for(var r = 0, rl = rowCount ; r < rl ; r++){
        					cloop:for(var c = 0 , cl = colCount ; c < cl ; c++){
        						var seqTmp = currSheet.getTag((row==-1?0:row) + r, (col==-1?0:col) + c, GC.Spread.Sheets.SheetArea.viewport);
        						if(seqTmp == null || typeof seqTmp == "undefined"){
        							// 不是报表指标
        							continue cloop;
        						}
        						var idxInfoTmp = Design.rptIdxs[seqTmp];
        						if(!idxInfoTmp
        								|| jQuery.inArray(idxInfoTmp.cellType , Constants.idxCellTypes) == -1
        								|| !idxInfoTmp.indexNo){
        							// 不是指标类单元格，或者引用指标为空
        							continue cloop;
        						}
        						if(jQuery.inArray(idxInfoTmp.indexNo , finalIdxNos) == -1){
        							finalIdxNos.push(idxInfoTmp.indexNo);
        						}
        					}
        				}
        				if(finalIdxNos.length <= 0){
        					BIONE.tip('当前所选行/列不包含任何指标');
        				}else{
        					BIONE.commonOpenDialog('设置指标过滤','idxFiltDialog'
        							,$(window.parent).width() * 0.75
        							,$(window.parent).height() * 0.75
        							,Context._ctx+'/report/frame/design/cfg/batch/frame?row='+row+'&col='+col+'&idxNos='+finalIdxNos.join(",")+'&d='+new Date().getTime()
        							,null , idxFilterHandler);
        				}
        			}
        		}
             }
         });
	
	/*
	*  自定义设置对角线函数
	* */
	Context._spread.commandManager().register("diagonal",
         {
             canUndo: true,
             execute: function (context, options, isUndo) {
         		var sheet = window.Design.spread.getActiveSheet();
        		var selections = sheet.getSelections();
        		if (selections && selections.length > 1) {
        			BIONE.tip('只能对一个单元格设置对角线');
        			return;
        		}
        		BIONE.commonOpenDialog('设置对角线两侧内容', 'diagonal', 497, 320,
        				Context._ctx + '/report/frame/design/cfg/diagonalContent');
             }
         });

	}
	
	window.setInfoWidth = function(num){
    	var currSheet = window.Design.spread.getActiveSheet();
		var selectionModules = currSheet.getSelections();
		if(selectionModules
				&& selectionModules.length == 1){
			var row = selectionModules[0].row;
			var col = selectionModules[0].col;
			var colCount = selectionModules[0].colCount;
			if(row == -1){
				for(var i = 0;i< colCount;i++)
					currSheet.setColumnWidth(col+i,num);
			}
		}
    }
	
	window.setInfoHeight = function(num){
    	var currSheet = window.Design.spread.getActiveSheet();
		var selectionModules = currSheet.getSelections();
		if(selectionModules
				&& selectionModules.length == 1){
			var row = selectionModules[0].row;
			var col = selectionModules[0].col;
			var rowCount = selectionModules[0].rowCount;
			if(col == -1){
				for(var i = 0;i< rowCount;i++)
					currSheet.setRowHeight(row+i,num);
			}
		}
    }
	
	function rptIdx() {
		// 将excel公式设置报表指标
		if(window.Design
				&& typeof window.Design.spread != "undefined"){			
			var currSheet = window.Design.spread.getActiveSheet();
			if($.browser.msie && parseInt($.browser.version, 10) < 9){
				var cellTmp = currSheet.getCell(currSheet.getActiveRowIndex() , currSheet.getActiveColumnIndex());
				var seqTmp = currSheet.getTag(cellTmp.row, cellTmp.col, GC.Spread.Sheets.SheetArea.viewport);
				var cellRptIdx = Design.rptIdxs[seqTmp];
				if(seqTmp != null
						&& cellRptIdx != null
						&& typeof cellRptIdx != "undefined"
							&& cellRptIdx.cellType == Constants.CELL_TYPE_FORMULA){
					if(cellRptIdx.isRptIndex === "Y"){
						cellRptIdx.isRptIndex = "N";
					}else{
						cellRptIdx.isRptIndex = "Y";
						if(typeof cellRptIdx.realIndexNo == "undefined"
							|| cellRptIdx.realIndexNo == null
							|| cellRptIdx.realIndexNo == ""){
							var realIndexNo = Uuid.v1().replace(/-/g, '');
							var currLabel = Utils.initAreaPosiLabel(cellTmp.row, cellTmp.col);
							//指标配置表存在，则使用配置表中的“报表指标编号”
							if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
								realIndexNo = Design.rptIdxCfg[currLabel];
							}
							cellRptIdx.realIndexNo = realIndexNo;
						}
					}
					if(selectionIdx
							&& typeof selectionIdx != "undefined"
								&& typeof selectionIdx.isRptIndex == "function"){
						if(selectionIdx.isRptIndex() === "Y"){
							selectionIdx.isRptIndex("N");
						}else{
							selectionIdx.isRptIndex("Y");
						}
					}
				}
			}else{						
				var selectionModules = currSheet.getSelections();
				var canSet = false;
				if(selectionModules.length 
						&& selectionModules.length > 0){
					f1:for(var i = 0 , j = selectionModules.length ; i < j ; i++){
						// 选中的多块区域
						var beginRow = selectionModules[i].row < 0 ? 0 : selectionModules[i].row;
						var endRow = beginRow + selectionModules[i].rowCount - 1;
						var beginCol = selectionModules[i].col < 0 ? 0 : selectionModules[i].col;
						var endCol = beginCol + selectionModules[i].colCount - 1;
						f2:for(var m = beginRow , n = endRow ; m <= n ; m++){
							// 行
							f3:for(var k = beginCol , l = endCol ; k <= l ; k++){
								// 列
								var seqTmp = currSheet.getTag(m, k, GC.Spread.Sheets.SheetArea.viewport);
								var cellRptIdx = Design.rptIdxs[seqTmp];
								if(seqTmp != null
										&& cellRptIdx != null
										&& typeof cellRptIdx != "undefined"
											&& cellRptIdx.cellType == Constants.CELL_TYPE_FORMULA){
									if(cellRptIdx.isRptIndex === "Y"){
										cellRptIdx.isRptIndex = "N";
									}else{
										cellRptIdx.isRptIndex = "Y";
										if(typeof cellRptIdx.realIndexNo == "undefined"
											|| cellRptIdx.realIndexNo == null
											|| cellRptIdx.realIndexNo == ""){
											var realIndexNo = Uuid.v1().replace(/-/g, '');
											var currLabel = Utils.initAreaPosiLabel(cellTmp.row, cellTmp.col);
											//指标配置表存在，则使用配置表中的“报表指标编号”
											if(Design.rptIdxCfg[currLabel] && Design.rptIdxCfg[currLabel] != "" && Design.rptIdxCfg[currLabel] != null){
												realIndexNo = Design.rptIdxCfg[currLabel];
											}
											cellRptIdx.realIndexNo = realIndexNo;
										}
									}
								}
							}
						}
					}
					if(selectionIdx
							&& typeof selectionIdx != "undefined"
								&& typeof selectionIdx.isRptIndex == "function"){
						if(selectionIdx.isRptIndex() === "Y"){
							selectionIdx.isRptIndex("N");
						}else{
							selectionIdx.isRptIndex("Y");
						}
					}
				}
			}
		}
	}
	
	/**
	 * 行列过滤回调函数
	 * @param filterInfos 
	 * 						row:行号; col:列号 ; filterInfos:过滤信息
	 */
	function idxFilterHandler(filterObjs){
		if(filterObjs
				&& typeof filterObjs == "object"
				&& Design){
			var row = filterObjs.row;
			var col = filterObjs.col;
			if(row != null
					&& typeof row != "undefined"
					&& col != null
					&& typeof col != "undefined"){
				var cellTmp = null;
				if(row == 0 || row == -1){
					cellTmp = Design.spread.getActiveSheet().getRange(-1, parseInt(col), GC.Spread.Sheets.SheetArea.viewport);
				}else if (col == 0 || col == -1){
					cellTmp = Design.spread.getActiveSheet().getRange(parseInt(row), -1, GC.Spread.Sheets.SheetArea.viewport);
				}
				var seqTmp = cellTmp.tag();
				var rowColTmp = {};
				if(seqTmp != null
						&& typeof seqTmp != "undefined"
						&& typeof Design.rowCols[seqTmp] != "undefined"){
					rowColTmp = Design.rowCols[seqTmp];
				} else if(row == 0 || row == -1){
					rowColTmp = RowCol.newColumn();
					seqTmp = Uuid.v1();
				} else {
					rowColTmp = RowCol.newRow();
					seqTmp = Uuid.v1();
				}
				rowColTmp.filtInfos = JSON2.stringify(filterObjs.filterInfos);
				rowColTmp.seq = seqTmp;
				cellTmp.tag(seqTmp);
				Design.rowCols[seqTmp] = rowColTmp;
			}
		}
	}
		
    function getActualCellRange(cellRange, rowCount, columnCount) {
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

	function frozenRows() {
		// 冻结(行)
		var sheet = window.Design.spread.getActiveSheet();
		var selections = sheet.getSelections();
		if (selections && selections.length == 1) {
			var col = selections[0].col;
			if (col == -1) {
				var rowCount = selections[0].rowCount;
				sheet.frozenRowCount(rowCount);
			}
		}
	}

	function unfrozenRows() {
		// 取消冻结(行)
		var sheet = window.Design.spread.getActiveSheet();
		var selections = sheet.getSelections();
		if (selections && selections.length == 1) {
			var col = selections[0].col;
			if (col == -1) {
				sheet.frozenRowCount(-1);
			}
		}
	}

	function frozenColumns() {
		// 冻结(列)
		var sheet = window.Design.spread.getActiveSheet();
		var selections = sheet.getSelections();
		if (selections && selections.length == 1) {
			var row = selections[0].row;
			if (row == -1) {
				var columnCount = selections[0].columnCount;
				sheet.frozenColumnCount(columnCount);
			}
		}
	}

	function unfrozenColumns() {
		// 取消冻结(列)
		var sheet = window.Design.spread.getActiveSheet();
		var selections = sheet.getSelections();
		if (selections && selections.length == 1) {
			var row = selections[0].row;
			if (row == -1) {
				sheet.frozenColumnCount(-1);
			}
		}
	}
    
	return Context;
	
})