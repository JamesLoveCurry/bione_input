/*******************************************************/
/****                        报表设计器 - 右键菜单                     ****/
/** *************************************************** */
define(["uuid" , "rptIdx" , "rowCol", "utils" , "constants"] , function(Uuid , RptIdx , RowCol , Utils , Constants) {
	var Context = {};
	
	Context._templateType ;
	
	Context._contextMenu = {};
	
	// 在silverlight中HitTestType枚举类型(和html5版本中的SheetArea码值不太一致，我去~)
	Context._silverlightHitTestType = {
			RowHeader : 3,
			ColumnHeader : 4,
			Viewport : 5
	};
	
	Context._globalConstants = {
			cut : "剪切(Ctrl+X)",
			copy : "复制(Ctrl+C)" ,
			paste : "粘贴(Ctrl+V)" ,
			delCell : "清除(Delete)" ,
			insert : "插入" ,
			del : "删除" ,
			merge : "合并" , 
			unmerge : "取消合并",
			celltype:"设置来源",
			bjqs:"设置表间取数",
			kzb:"设置为空指标",
			bds:"设置表达式",
			rptIdx:"设置报表指标",
			filtSetting:"指标过滤",
			hide:"隐藏",
			unhide:"取消隐藏"
	};
	
	Context._initDoom = function(spreadDom, isRptIdxCfg){
		var html = "";
		if(true === isRptIdxCfg){//当是报表指标功能配置的时候，右键菜单会有所减少
			html = 
				'<ul id="contextMenu">'+
					'    <li class="context-cell context-settings context-bjqs"><a><span class="wijmo-wijmenu-text">'+
					Context._globalConstants.bjqs + '  </span></a></li>'+
					'    <li class="context-cell context-settings context-kzb"><a><span class="wijmo-wijmenu-text">'+
					Context._globalConstants.kzb + '  </span></a></li>'+
				'</ul>';
			
		}else{
			html = 
				  '<ul id="contextMenu">'+
				  '    <li class="context-clipboard"><a><span class="wijmo-wijmenu-text">'+Context._globalConstants.cut+'</span></a></li>    '+
				  '    <li class="context-clipboard"><a><span class="wijmo-wijmenu-text">'+Context._globalConstants.copy+'</span></a></li>  '+
				  '    <li class="context-clipboard"><a><span class="wijmo-wijmenu-text">'+Context._globalConstants.paste+'</span></a></li> '+
				  '    <li class="context-header">-</li>'+
				  '    <li class="context-header context-insert"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.insert +'  </span></a></li>'+
				  '    <li class="context-header context-delete"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.del + '  </span></a></li>'+
				  '    <li class="context-cell merge-line">-</li>'+
				  '    <li class="context-cell context-merge"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.merge + '  </span></a></li>'+
				  '    <li class="context-cell context-unmerge"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.unmerge + '  </span></a></li>'+
				  '    <li class="context-hide">-</li>'+
				  '    <li class="context-cell context-hide"><a><span class="wijmo-wijmenu-text">'+
				 			Context._globalConstants.hide + '  </span></a></li>'+
				  '    <li class="context-cell context-hide"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.unhide + '  </span></a></li>'+
				  '    <li class="context-cell context-settings">-</li>'+
				  '    <li class="context-cell context-settings context-bjqs"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.bjqs + '  </span></a></li>'+
				  '    <li class="context-cell context-settings context-kzb"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.kzb + '  </span></a></li>'+
				  '    <li class="context-cell context-settings context-bds"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.bds + '  </span></a></li>'+
				  '    <li class="context-cell context-settings context-rptIdx"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.rptIdx + '  </span></a></li>'+
				  '    <li class="context-filtSetting">-</li>'+
				  '    <li class="context-filtSetting"><a><span class="wijmo-wijmenu-text">'+
				  			Context._globalConstants.filtSetting + '  </span></a></li>'+
				  '</ul>';
				
		}
		spreadDom.append(html);
		
		return $("#contextMenu")
	}
	
	/**
	 * 初始化右键菜单
	 */
	Context.initContextMenu = function(spreadDom , templateType , ctx , isBusiLine, isRptIdxCfg) {
		Context._templateType = templateType ? templateType : Constants.TEMPLATE_TYPE_MODULE;
		// init doom
		Context._contextMenu = Context._initDoom(spreadDom, isRptIdxCfg);
		var spread = spreadDom.wijspread("spread");
		var sheetArea = $.wijmo.wijspread.SheetArea.viewport, isHideContextMenu = false;
		spreadDom.mouseup(function(e) {
			// hide context menu when the mouse down on SpreadJS
			if (e.button !== 2) {
				Context._contextMenu.wijmenu("hideAllMenus");
			}
		});
		spreadDom.bind(
				'contextmenu',
				function(e) {
					// move the context menu to the position of the mouse
					// point
					var sheet = spread.getActiveSheet();
					var target = getHitTest(e.pageX, e.pageY, sheet);
					var spans = Design.getSelectionSpans(sheet , target.row , target.col);
					if(spans 
							&& typeof spans.length != "undefined"
							&& spans.length > 0){
						target.row = spans[0].row;
						target.col = spans[0].col;
					}
					isHideContextMenu = false;
					sheetArea = target.hitTestType;
					// 默认隐藏批量指标过滤设置
					$(".context-filtSetting").hide();
					// 默认隐藏行列隐藏设置
					$(".context-hide").hide();
					if (target.hitTestType === $.wijmo.wijspread.SheetArea.colHeader) {
						var modelSelect = null;
						modelSelect = modelFind(sheet.getSelections() , target.row , target.col);
						if (modelSelect === null) {
							if($.browser.msie && parseInt($.browser.version, 10) < 9){
								sheet.clearSelection();
							}
							sheet.setSelection(-1, target.col, sheet
									.getRowCount(), 1);
						}
						if (target.row !== undefined
								&& target.col !== undefined) {
							$(".context-header").show();
							$(".context-cell").show();
							$(".context-hide").show();
							if(Context._templateType != Constants.TEMPLATE_TYPE_MODULE
									&& sheet.getSelections()[0].colCount <= 1){
								// 非明细类报表，且只选择了一列数据，开启指标批量维度过滤设置
								$(".context-filtSetting").show();
							}
							showMergeContextMenu(sheet);
							showSettingContextMenu(isBusiLine , false);
						}
					} else if (target.hitTestType === $.wijmo.wijspread.SheetArea.rowHeader) {
						var modelSelect = null;
						modelSelect = modelFind(sheet.getSelections() , target.row , target.col);
						if (modelSelect === null) {
							if($.browser.msie && parseInt($.browser.version, 10) < 9){
								sheet.clearSelection();
							}
							sheet.setSelection(target.row, -1, 1, sheet
									.getColumnCount());
						}
						if (target.row !== undefined
								&& target.col !== undefined) {
							$(".context-header").show();
							$(".context-cell").show();
							$(".context-hide").show();
							if(Context._templateType != Constants.TEMPLATE_TYPE_MODULE
									&& sheet.getSelections()[0].rowCount <= 1){
								// 非明细类报表，且只选择了一列数据，开启指标批量维度过滤设置
								$(".context-filtSetting").show();
							}
							showMergeContextMenu(sheet);
							showSettingContextMenu(isBusiLine , false);
						}
					} else if (target.hitTestType === $.wijmo.wijspread.SheetArea.viewport) {
						var modelSelect = null;
						modelSelect = modelFind(sheet.getSelections() , target.row , target.col);
						if (modelSelect === null) {
							sheet.clearSelection();
							sheet.endEdit();
							sheet.setActiveCell(target.row, target.col);
						}
						if (target.row !== undefined
								&& target.col !== undefined) {
							$(".context-header").hide();
							$(".context-cell").show();
							showMergeContextMenu(sheet);
							showSettingContextMenu(isBusiLine);
						} else {
							isHideContextMenu = true;
						}
						// 若当前选中单元格全是excel公式，显示设置报表指标菜单
//						var isShowRptIdx = true;
//						if(templateType
//								&& templateType == Constants.TEMPLATE_TYPE_MODULE){
//							isShowRptIdx = false;
//						}else{
//							if($.browser.msie && parseInt($.browser.version, 10) < 9){
//								var cellTmp = sheet.getCell(sheet.getActiveRowIndex() , sheet.getActiveColumnIndex());
//								var seqTmp = cellTmp._getStyleProperty("seq");
//								if(seqTmp == null
//										|| typeof seqTmp == "undefined"){
//									isShowRptIdx = false;
//								}
//								if(Design
//										&& Design.rptIdxs){
//									var selRptIdx = Design.rptIdxs[seqTmp];
//									if(selRptIdx == null
//											|| typeof selRptIdx == "undefined"
//												|| selRptIdx.cellType != Constants.CELL_TYPE_FORMULA){
//										isShowRptIdx = false;
//									}
//								}
//							} else {
//								sloop:for(var i = 0 , j = sheet.getSelections().length ; i < j ; i++){
//									var selectTmp = sheet.getSelections()[i];
//									rloop:for(var r = 0, rl = selectTmp.rowCount ; r < rl ; r++){
//										cloop:for(var c = 0 , cl = selectTmp.colCount ; c < cl ; c++){
//											var seqTmp = sheet.getCell(selectTmp.row + r , selectTmp.col + c)._getStyleProperty("seq");
//											if(seqTmp == null
//													|| typeof seqTmp == "undefined"){
//												isShowRptIdx = false;
//												break sloop;
//											}
//											if(Design
//													&& Design.rptIdxs){
//												var selRptIdx = Design.rptIdxs[seqTmp];
//												if(selRptIdx == null
//														|| typeof selRptIdx == "undefined"
//															|| selRptIdx.cellType != Constants.CELL_TYPE_FORMULA){
//													isShowRptIdx = false;
//													break sloop;
//												}
//											}
//										}
//									}
//								}
//							}
//						}
//						if(isShowRptIdx === true){
//							$(".context-rptIdx").show();
//						}else{
//							$(".context-rptIdx").hide();
//						}
						// 暂时不开发设置报表指标
						$(".context-rptIdx").hide();
					} else if (target.hitTestType === $.wijmo.wijspread.SheetArea.corner) {
						sheet.setSelection(-1, -1, sheet.getRowCount(),
								sheet.getColumnCount());
						if (target.row !== undefined
								&& target.col !== undefined) {	
							$(".context-header").hide();
							$(".context-cell").show();
						}
					}
					Context._contextMenu.wijmenu("option", "position", {
						my : "left top",
						of : e
					});
			return false;
		});
		Context._contextMenu.wijmenu({
			trigger : spreadDom.selector,
			triggerEvent : "rtclick",
			orientation : "vertical",
			showAnimation : {
				animated : 'slide',
				duration : 0,
				easing : null
			},
			hideAnimation : {
				animated : 'slide',
				duration : 0,
				easing : null
			},
			showing : function(e, item) {
				if (isHideContextMenu) {
					return false;
				} else {
					return true;
				}
			},
			select : function(e, data) {
				var options = data.item.options;
				var spread = spreadDom.wijspread("spread");
				var sheet = spread.getActiveSheet();
				switch ($.trim(options.text)) {
				case Context._globalConstants.cut:
					// 剪切
					$.wijmo.wijspread.SpreadActions.cut.call(sheet);
					break;
				case Context._globalConstants.copy:
					// 复制
					$.wijmo.wijspread.SpreadActions.copy.call(sheet);
					break;
				case Context._globalConstants.paste:
					// 粘贴
					$.wijmo.wijspread.SpreadActions.paste.call(sheet);
					break;
				case Context._globalConstants.insert:
					// 插入
					if (sheetArea === $.wijmo.wijspread.SheetArea.colHeader) {
						// 列
						sheet.addColumns(sheet.getActiveColumnIndex(), 1);
					} else if (sheetArea === $.wijmo.wijspread.SheetArea.rowHeader) {
						// 行
						sheet.addRows(sheet.getActiveRowIndex(), 1);
					}
					break;
				case Context._globalConstants.delCell:
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
									if(isIdx === true){
										hasIdx = true;
									}
								}
							}
						}
						if(hasIdx === true
								&& Design._settings.onIdxsChanged
								&& typeof Design._settings.onIdxsChanged == "function"){
							Design._settings.onIdxsChanged();
						}
					}
					break;
				case Context._globalConstants.del:
					// 删除
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
						if (sheetArea === $.wijmo.wijspread.SheetArea.colHeader) {
							// 列
							if(flag === true){
								// 判断待删除区域中是否有指标类单元格
								f1 : for(var k = 0 ; k < colCount ; k++){
									f2 : for(var b = 0 , t = sheet.getRowCount() ; b < t ; b++){
										var cellTmp = sheet.getCell(b , beginCol + k);
										var seqTmp = cellTmp.tag();
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
						} else if (sheetArea === $.wijmo.wijspread.SheetArea.rowHeader) {
							// 行
							if(flag === true){
								// 判断待删除区域中是否有指标类单元格
								f1 : for(var k = 0 ; k < rowCount ; k++){
									f2 : for(var b = 0 , t = sheet.getColumnCount() ; b < t ; b++){
										var cellTmp = sheet.getCell(beginRow+k , b);
										var seqTmp = cellTmp.tag();
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
						}
						if(hasIdx === true){
							// 触发指标类报表指标变化事件
							window.Design._settings.onIdxsChanged();
						}
					}
					break;
				case Context._globalConstants.merge:
					// 合并单元格
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
									cellTag = sheet.getCell(sel.row+i , sel.col+j).tag();
									break;
								}
							}
						}
						sheet.getCell(sel.row , sel.col).value(cellValue);
						sheet.getCell(sel.row , sel.col).tag(cellTag);
						for(var i=0;i<sel.rowCount;i++){
							for(var j =0 ; j < sel.colCount ; j++){
								if(i!=0 || j!=0){
									sheet.getCell(sel.row+i , sel.col+j).value(null);
									 sheet.getCell(sel.row+i , sel.col+j).tag(null);
								}
							}
						}
						sheet.addSpan(sel.row, sel.col, sel.rowCount,
								sel.colCount,
								$.wijmo.wijspread.SheetArea.viewport);
						
						
					}
					break;
				case Context._globalConstants.unmerge:
					// 取消合并单元格
					var sels = sheet.getSelections();
					for ( var i = 0; i < sels.length; i++) {
						var sel = getActualCellRange(sels[i], sheet
								.getRowCount(), sheet.getColumnCount());
						for ( var r = 0; r < sel.rowCount; r++) {
							for ( var c = 0; c < sel.colCount; c++) {
								var span = sheet.getSpan(r + sel.row,c + sel.col,
														$.wijmo.wijspread.SheetArea.viewport);
								if (span) {
									sheet.removeSpan(span.row,span.col,
														$.wijmo.wijspread.SheetArea.viewport);
								}
							}
						}
					}
					break;
				case Context._globalConstants.bds:
					// 表达式
					var width = RptIdx.templateSettings[Constants.CELL_TYPE_EXPRESSION].dialogWidth;
					var height = RptIdx.templateSettings[Constants.CELL_TYPE_EXPRESSION].dialogHeight;
					width = Utils.transWidthOrHeight(width , $(window.parent).width());
					height = Utils.transWidthOrHeight(height , $(window.parent).height());
					BIONE.commonOpenDialog('表达式设置','moduleClkDialog'
							,width
							,height
							,ctx+'/report/frame/design/cfg/sysVarSet?d='+new Date().getTime());
					break;
				case Context._globalConstants.bjqs:
					// 表间取数
					var width = RptIdx.templateSettings[Constants.CELL_TYPE_BJJS].dialogWidth;
					var height = RptIdx.templateSettings[Constants.CELL_TYPE_BJJS].dialogHeight;
					width = Utils.transWidthOrHeight(width , $(window.parent).width());
					height = Utils.transWidthOrHeight(height , $(window.parent).height());
					BIONE.commonOpenDialog('表间取数','moduleClkDialog'
							,width
							,height
							,ctx+'/report/frame/valid/logic/getData?d='+new Date().getTime());
					break;
				case Context._globalConstants.kzb:
					// 空指标
					if(window.Design
							&& typeof window.Design.spread != "undefined"){					
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
					break;
				case Context._globalConstants.busiLine:
					// 设置业务条线（该方法已废弃）
					if(window.Design
							&& typeof window.Design.spread != "undefined"){					
						var currSheet = window.Design.spread.getActiveSheet();
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
										var cellTmp = currSheet.getCell(m,k);
										var seqTmp = cellTmp._getStyleProperty("seq");
										if(seqTmp != null
												&& Design.rptIdxs[seqTmp] != null
												&& typeof Design.rptIdxs[seqTmp] != "undefined"){
											// 选中区域中包含有报表指标，可以设置业务条线
											canSet = true;
											break f1;
										}
									}
								}
							}
						}
					}
					if(canSet === true){					
						// 业务条线设置
						BIONE.commonOpenDialog('业务条线设置','busiLineDialog'
								,$(window.parent).width() * 0.7
								,$(window.parent).height() * 0.7
								,ctx+Context._globalConstants.busiLinePageUrl+'?d='+new Date().getTime());
					}else{
						BIONE.tip("一般单元格不能进行条线设置");
					}
					break;
				case Context._globalConstants.rptIdx:
					// 将excel公式设置报表指标
					if(window.Design
							&& typeof window.Design.spread != "undefined"){			
						var currSheet = window.Design.spread.getActiveSheet();
						if($.browser.msie && parseInt($.browser.version, 10) < 9){
							var cellTmp = currSheet.getCell(currSheet.getActiveRowIndex() , currSheet.getActiveColumnIndex());
							var seqTmp = cellTmp.tag();
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
										cellRptIdx.realIndexNo = Uuid.v1().replace(/-/g, '');
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
											var cellTmp = currSheet.getCell(m,k);
											var seqTmp = cellTmp.tag();
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
														cellRptIdx.realIndexNo = Uuid.v1().replace(/-/g, '');
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
					break;
				case Context._globalConstants.filtSetting : 
					// 设置批量指标维度过滤
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
							if($.browser.msie && parseInt($.browser.version, 10) < 9){
								if(rowCount == -1){
									rowCount = currSheet.getRowCount();
								}
								if(colCount == -1){
									colCount = currSheet.getColumnCount();
								}
							}
							var finalIdxNos = [];
							rloop:for(var r = 0, rl = rowCount ; r < rl ; r++){
								cloop:for(var c = 0 , cl = colCount ; c < cl ; c++){
									var seqTmp = sheet.getCell((row==-1?0:row) + r , (col==-1?0:col) + c).tag();
									if(seqTmp == null
											|| typeof seqTmp == "undefined"){
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
										,ctx+'/report/frame/design/cfg/batch/frame?row='+row+'&col='+col+'&idxNos='+finalIdxNos.join(",")+'&d='+new Date().getTime()
										,null , idxFilterHandler);
							}
						}
					}
					break;
				case Context._globalConstants.hide : 
					hideOrShowRowCols(false);
					break;
				case Context._globalConstants.unhide : 
					hideOrShowRowCols(true);
					break;
				default:
					break;
				}
			}
		});
		
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
					var cellTmp = (row == 0 || row == -1) 
								? Design.spread.getActiveSheet().getColumn(col , $.wijmo.wijspread.SheetArea.colHeader)
								: Design.spread.getActiveSheet().getRow(row , $.wijmo.wijspread.SheetArea.rowHeader);
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
		
		//context menu
		function modelFind(selectionModel , row , col){
			var self = selectionModel,
	        count = self.length,
	        selection;
			if (count > 0){
				for (var i = 0; i < count; i++){
					selection = self[i];
					if (selection.contains(row, col)){
						return selection
					}
				}
			}
			return null
		}
	    function getHitTest(pageX, pageY, sheet) {
	        var offset = spreadDom.offset(),
	                x = pageX - offset.left,
	                y = pageY - offset.top;
	        if($.browser.msie && parseInt($.browser.version, 10) < 9){
				// 由于silverlight未提供相应接口，hitTest是反编译扩展的方法，调用方式很恶心
	        	var target = {};
	        	try{		
	        		var posi = spread.canvas.firstChild.Content.SpreadsheetObject.hitTest(x,y);
	        		if(posi != null && typeof posi == "object"){
	        			target.col = posi.Col;
	        			target.row = posi.Row;
	        			var hitTestType = posi.hitTestType;
	        			var val = $.wijmo.wijspread.SheetArea.viewport;
	        			switch (hitTestType) {
	        			case Context._silverlightHitTestType.RowHeader:
	        				val = $.wijmo.wijspread.SheetArea.rowHeader;
	        				break;
	        			case Context._silverlightHitTestType.ColumnHeader:
	        				val = $.wijmo.wijspread.SheetArea.colHeader;
	        				break;
	        			}
	        			target.hitTestType = val;
	        		}
	        	} catch(e) {}
				return target;
			}else{			
				return sheet.hitTest(x, y);
			}
	    }
	    
	    function showSettingContextMenu(isBusiLine , flag){
	    	if( flag != null
	    			&&typeof flag != "undefined"){
	    		if(flag === true){
	    			$(".context-settings").show();
	    		}else{
	    			$(".context-settings").hide();
	    		}
	    	}else{
	    		if(Context._templateType == Constants.TEMPLATE_TYPE_MODULE
	    				|| Context._templateType == Constants.TEMPLATE_TYPE_IDXCOL){
	    			// 明细报表&指标列表，不显示表间计算&空指标
	    			$(".context-bjqs").hide();
	    			$(".context-kzb").hide();
	    		}else{
	    			$(".context-bjqs").show();
	    			$(".context-kzb").show();
	    		}
	    		if(isBusiLine === true){
					// 业务条线，隐藏文本表达式和表间计算
					$(".context-bds").hide();
					//$(".context-bjqs").hide();
				}else{
					$(".context-bds").show();
					//$(".context-bjqs").show();
				}
	    	}
	    }
	    
	    function showMergeContextMenu(sheet) {
	    	if($.browser.msie && parseInt($.browser.version, 10) < 9){
	    		$(".merge-line").hide();
	    		$(".context-merge").hide();
	    		$(".context-unmerge").hide();
	    	}else{    		
	    		var selections = sheet.getSelections();
	    		if (selections && selections.length > 0) {
	    			var spans = sheet.getSpans(selections[selections.length - 1], $.wijmo.wijspread.SheetArea.viewport);
	    			if (spans && spans.length > 0) {
	    				$(".context-merge").hide();
	    				$(".context-unmerge").show();
	    			} else {
	    				$(".context-merge").show();
	    				$(".context-unmerge").hide();
	    			}
	    		}
	    	}
	    }
	    
	    function hideOrShowRowCols(visibleFlag){
	    	var currSheet = window.Design.spread.getActiveSheet();
			var selectionModules = currSheet.getSelections();
			if(selectionModules
					&& selectionModules.length > 0){
				for(var i = 0 , j = selectionModules.length ; i < j ; i++){
					var row = selectionModules[i].row;
					var rowCount = selectionModules[i].rowCount;
					var col = selectionModules[i].col;
					var colCount = selectionModules[i].colCount;
					if(row == -1){
						// 列
						var cols = currSheet.getColumns(col , (col+colCount-1));
						if(cols){
							cols.visible(visibleFlag);
						}
					} else if(col == -1){
						// 行
						var rows = currSheet.getRows(row , (row+rowCount-1));
						if(rows){
							rows.visible(visibleFlag);
						}
					}
				}
			}
	    }
	    
	    function getActualCellRange(cellRange, rowCount, columnCount) {
	        if (cellRange.row == -1 && cellRange.col == -1) {
	            return new $.wijmo.wijspread.Range(0, 0, rowCount, columnCount);
	        }
	        else if (cellRange.row == -1) {
	            return new $.wijmo.wijspread.Range(0, cellRange.col, rowCount, cellRange.colCount);
	        }
	        else if (cellRange.col == -1) {
	            return new $.wijmo.wijspread.Range(cellRange.row, 0, cellRange.rowCount, columnCount);
	        }
	
	        return cellRange;
	    }
	  
	}

	return Context;
	
})