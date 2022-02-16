/*********************************************************/
/****            报表设计器 - 牛鼻工具栏 = 。 =     ****/
/** **************************************************** */
define(function() {
	
	var Toolbar = {};
	
	Toolbar._spread = {};
	
	Toolbar._Constants = {
			bold : "bold",  // 加粗
			italic : "italic", // 斜体
			font : "font", // 字体
			fontsize : "fontsize", // 斜体
			underline : "underline", // 下划线
			subscript : "subscript", // 减小字体
			superscript : "superscript", // 增大字体
			mergecell : "mergecell", // 合并单元格
			unmergecell : "unmergecell", // 取消合并
			save : "save", // 保存
			importcss : "importcss", // 导入样式
			borders : "borders", // 边框
			borderall : "borderall", // 所有框线
			borderleft : "borderleft", // 左框线
			borderright : "borderright", // 右框线
			bordertop : "bordertop", // 上框线
			borderbottom : "borderbottom", // 下框线
			borderout : "borderout", // 外框线
			bordernone : "bordernone", // 无框线
			alignsh : "alignsh", // 水平对齐
			alignleft : "alignleft", // 左对齐
			aligncenter : "aligncenter", // 居中
			alignsv : "alignsv", // 垂直对齐
			alignright : "alignright", // 右对齐
			aligntop : "aligntop", // 顶端对齐
			alignmiddle : "alignmiddle", // 垂直对齐
			alignbottom : "alignbottom", // 底端对齐
			changeShowType : "changeShowType", // 翻转展示方式
			fontcolor : "fontcolor" , // 字体颜色
			backgroundcolor : "backgroundcolor", // 背景颜色
			backgroundcolornone : "backgroundcolornone", // 无背景颜色
			remind : "remind" // 使用提醒
	};
	
	Toolbar._default = {
			normalKeyword : "normal",
			borderStyle : new GC.Spread.Sheets.LineBorder("Black",  GC.Spread.Sheets.LineStyle.thin)
	}
	
	Toolbar._bordSettings = [];
	Toolbar._bordSettings[Toolbar._Constants.borderall] = {
			all:true
	};
	Toolbar._bordSettings[Toolbar._Constants.borderleft] = {
			left: true
	};
	Toolbar._bordSettings[Toolbar._Constants.borderright] = {
	        right: true
	};
	Toolbar._bordSettings[Toolbar._Constants.bordertop] = {
	        top: true
	};
	Toolbar._bordSettings[Toolbar._Constants.borderbottom] = {
	        bottom: true
	};
	Toolbar._bordSettings[Toolbar._Constants.borderout] = {
			outline:true
	};
	Toolbar._bordSettings[Toolbar._Constants.bordernone] = {
			
	};
	
	/**
	 * 初始化
	 */
	Toolbar.initToolbar = function(toolbarDom, spreadDom , ctx , isBusiLine) {
		Toolbar._ctx = ctx;
		if(toolbarDom != null
				&& typeof toolbarDom != "undefined"){
			var html = [
			    '<div class="toolbar-awesome" style="width:100%;margin-bottom:2px;margin-left:10px;">',
					'<div class="btn-group">',
						'<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.importcss+'" data-toggle="tooltip" data-placement="bottom" title="导入样式(Ctrl+E)">',
							'<i class="iconfont iconfont-excel iconfont-lgm"></i>',
						'</button>',
					'</div>',
					'<div class="btn-group">',
						// 边框
						'  <button type="button" class="btn btn-default dropdown-toggle hoverdrop usetooltip"', 
						'    data-toggle="dropdown" data-hover="dropdown" data-placement="left" title="边框">',
							'<i class="iconfont iconfont-borderall iconfont-lg"></i>',
						'    <span class="caret"></span>',
						'  </button>',
						'  <ul class="dropdown-menu text-left" role="menu" style="min-width:100px;">',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.borderall+'"><a><i class="iconfont iconfont-borderall iconfont-lg"></i>&nbsp;全边框</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.bordertop+'"><a><i class="iconfont iconfont-bordertop iconfont-lg"></i>&nbsp;上边框</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.borderleft+'"><a><i class="iconfont iconfont-borderleft iconfont-lg"></i>&nbsp;左边框</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.borderbottom+'"><a><i class="iconfont iconfont-borderbottom iconfont-lg"></i>&nbsp;下边框</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.borderright+'"><a><i class="iconfont iconfont-borderright iconfont-lg"></i>&nbsp;右边框</a></li>',
		//				    '  <li><a><i class="iconfont iconfont-borderouter iconfont-lg"></i>&nbsp;外边框</a></li>',
		//				    '  <li><a><i class="iconfont iconfont-borderinner iconfont-lg"></i>&nbsp;内边框</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.bordernone+'"><a><i class="iconfont iconfont-borderclear iconfont-lg"></i>&nbsp;无边框</a></li>',
						'  </ul>',
					'</div>',
					'<div class="btn-group">',
						'  <button type="button" class="btn btn-default dropdown-toggle hoverdrop usetooltip"', 
						'    data-toggle="dropdown" data-hover="dropdown" data-placement="left" title="字体颜色">',
							'<i class="iconfont iconfont-formatcolortext iconfont-lg"></i>',
						'    <span class="caret"></span>',
						'  </button>',
						'  <ul class="dropdown-menu drop-color" style="width:170px;">',
						    '  <li><div class="cp" toolbaritem="'+Toolbar._Constants.fontcolor+'"></div></li>',
						'  </ul>',
					'</div>',
					'<div class="btn-group">',
						'  <button type="button" class="btn btn-default dropdown-toggle hoverdrop usetooltip"', 
						'    data-toggle="dropdown" data-hover="dropdown" data-placement="left" title="背景颜色">',
							'<i class="iconfont iconfont-bgcolor iconfont-lg"></i>',
						'    <span class="caret"></span>',
						'  </button>',
						'  <ul class="dropdown-menu text-center drop-color" style="width:170px;">',
						    '  <li><div class="cp" toolbaritem="'+Toolbar._Constants.backgroundcolor+'"></div></li>',
						    '  <li class="divider" style="margin:3px 1px;"></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.backgroundcolornone+'" style="cursor:pointer;">',
						    '     <i class="iconfont iconfont-iconempty"></i>&nbsp无填充颜色'+
						    '  </li>'+
						'  </ul>',
					'</div>',
					'<div class="btn-group">',
						'  <button type="button" class="btn btn-default dropdown-toggle hoverdrop usetooltip"', 
						'    data-toggle="dropdown" data-hover="dropdown" data-placement="left" title="字体">',
							'<i class="iconfont iconfont-22 iconfont-lg"></i>',
						'    <span class="caret"></span>',
						'	 <span id="fontfamily" data-bind="text:font"></span>',
						'  </button>',
						'  <ul class="dropdown-menu text-left" role="menu" style="min-width:160px; height: 160px; overflow: auto">',
							'  <li class="clickable" toolbaritem="'+Toolbar._Constants.font+'" fontsize="Times New Roman" ><a><i class="iconfont iconfont-lg"></i>Times New Roman</a></li>',
							'  <li class="clickable" toolbaritem="'+Toolbar._Constants.font+'" fontsize="Arial" ><a><i class="iconfont iconfont-lg"></i>Arial</a></li>',
							'  <li class="clickable" toolbaritem="'+Toolbar._Constants.font+'" fontsize="宋体" ><a><i class="iconfont iconfont-lg"></i>宋体</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.font+'" fontsize="微软雅黑" ><a><i class="iconfont iconfont-lg"></i>微软雅黑</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.font+'" fontsize="楷体" ><a><i class="iconfont iconfont-lg"></i>楷体</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.font+'" fontsize="隶书" ><a><i class="iconfont iconfont-lg"></i>隶书</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.font+'" fontsize="黑体" ><a><i class="iconfont iconfont-lg"></i>黑体</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.font+'" fontsize="仿宋" ><a><i class="iconfont iconfont-lg"></i>仿宋</a></li>',
						'  </ul>',
					'</div>',
					'<div class="btn-group">',
						'  <button type="button" class="btn btn-default dropdown-toggle hoverdrop usetooltip"', 
						'    data-toggle="dropdown" data-hover="dropdown" data-placement="left" title="字号">',
							'<i class="iconfont iconfont-mianxingtubiaozitizihao iconfont-lg"></i>',
						'    <span class="caret"></span>',
						'	 <span id="fontsize" data-bind="text:fontSize"></span>',
						'  </button>',
						'  <ul class="dropdown-menu text-left" role="menu" style="min-width:100px; height: 200px; overflow: auto">',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="9" ><a><i class="iconfont iconfont-lg"></i>&nbsp;9</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="10" ><a><i class="iconfont iconfont-lg"></i>&nbsp;10</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="11" ><a><i class="iconfont iconfont-lg"></i>&nbsp;11</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="12" ><a><i class="iconfont iconfont-lg"></i>&nbsp;12</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="14" ><a><i class="iconfont iconfont-lg"></i>&nbsp;14</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="16" ><a><i class="iconfont iconfont-lg"></i>&nbsp;16</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="18" ><a><i class="iconfont iconfont-lg"></i>&nbsp;18</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="20" ><a><i class="iconfont iconfont-lg"></i>&nbsp;20</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="22" ><a><i class="iconfont iconfont-lg"></i>&nbsp;22</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="24" ><a><i class="iconfont iconfont-lg"></i>&nbsp;24</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="26" ><a><i class="iconfont iconfont-lg"></i>&nbsp;26</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="28" ><a><i class="iconfont iconfont-lg"></i>&nbsp;28</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="36" ><a><i class="iconfont iconfont-lg"></i>&nbsp;36</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="48" ><a><i class="iconfont iconfont-lg"></i>&nbsp;48</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.fontsize+'" fontsize="72" ><a><i class="iconfont iconfont-lg"></i>&nbsp;72</a></li>',
						'  </ul>',
					'</div>',
					
					'<div class="btn-group">',
						'<button type="button" data-bind="attr :{class: (bold()==\'\' || bold()==\'normal\')?\'btn btn-default usetooltip clickable\':\'btn btn-default usetooltip clickable active\'}" toolbaritem="'+Toolbar._Constants.bold+'" data-toggle="tooltip" data-placement="bottom" title="加粗">',
							'<i class="iconfont iconfont-bold iconfont-lgm"></i>',
						'</button>',
						'<button type="button" data-bind="attr :{class: (italic()==\'\' || italic()==\'normal\')?\'btn btn-default usetooltip clickable\':\'btn btn-default usetooltip clickable active\'}" toolbaritem="'+Toolbar._Constants.italic+'" data-toggle="tooltip" data-placement="bottom" title="斜体">',
							'<i class="iconfont iconfont-italic iconfont-lgm"></i>',
						'</button>',
						'<button type="button" data-bind="attr :{class: (textDecoration()==\'\' || textDecoration()==\'normal\')?\'btn btn-default usetooltip clickable\':\'btn btn-default usetooltip clickable active\'}" toolbaritem="'+Toolbar._Constants.underline+'" data-toggle="tooltip" data-placement="bottom" title="下划线">',
							'<i class="iconfont iconfont-underline iconfont-lgm"></i>',
						'</button>',
						'<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.mergecell+'" data-toggle="tooltip" data-placement="bottom" title="合并单元格">',
							'<i class="iconfont iconfont-cellmerge iconfont-lgm"></i>',
						'</button>',
					'</div>',
					
					// 水平对齐
					'<div class="btn-group">',
						'  <button type="button" class="btn btn-default dropdown-toggle hoverdrop usetooltip"', 
						'    data-toggle="dropdown" data-hover="dropdown" data-placement="left" title="水平对齐">',
							'<i class="iconfont iconfont-alignjustify iconfont-lg"></i>',
						'    <span class="caret"></span>',
						'  </button>',
						'  <ul class="dropdown-menu text-left" role="menu" style="min-width:100px;">',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.alignleft+'"><a><i class="iconfont iconfont-leftalign iconfont-lg"></i>&nbsp;左对齐</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.aligncenter+'"><a><i class="iconfont iconfont-aligncenter iconfont-lg"></i>&nbsp;居中</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.alignright+'"><a><i class="iconfont iconfont-rightalign iconfont-lg"></i>&nbsp;右对齐</a></li>',
						'  </ul>',
					'</div>',
					// 垂直对齐
					'<div class="btn-group">',
						'  <button type="button" class="btn btn-default dropdown-toggle hoverdrop usetooltip"', 
						'    data-toggle="dropdown" data-hover="dropdown" data-placement="left" title="垂直对齐">',
							'<i class="iconfont iconfont-alignjustify1 iconfont-lg"></i>',
						'    <span class="caret"></span>',
						'  </button>',
						'  <ul class="dropdown-menu text-left" role="menu" style="min-width:110px;">',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.aligntop+'"><a><i class="iconfont iconfont-aligntop iconfont-lg"></i>&nbsp;顶端对齐</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.alignmiddle+'"><a><i class="iconfont iconfont-alignverticalcenters iconfont-lg"></i>&nbsp;垂直居中</a></li>',
						    '  <li class="clickable" toolbaritem="'+Toolbar._Constants.alignbottom+'"><a><i class="iconfont iconfont-alignbottom iconfont-lg"></i>&nbsp;底端对齐</a></li>',
						'  </ul>',
					'</div>',
					
					//配置说明，可以把一些配置过程中需要注意的事情进行编写，提示用户
					'<div class="btn-group">',
						'<button type="button" class="btn btn-default usetooltip clickable" toolbaritem="'+Toolbar._Constants.remind+'" data-toggle="tooltip" data-placement="bottom" title="使用提醒" style="color: red;">',
							'<i class="iconfont iconfont-alignverticalcenters iconfont-lgm"></i>',
						'</button>',
					'</div>',
					'</div>',
				'</div>'
			];    
			Toolbar._spread = Design.spread;
			toolbarDom.html(html.join(""));
			// 初始化提示
			$(".usetooltip").tooltip();
			// 初始化hover下拉菜单
			$('.hoverdrop').dropdownHover().dropdown();
			// 初始化颜色选择组件
			$(".cp").colorPalette()
				.on("selectColor" , function(e){
					Toolbar._initClickEvents($(this).attr("toolbaritem") , e);
				});
			// 初始化点击
			toolbarDom.delegate(".clickable" , "click" , function(){
				Toolbar._initClickEvents($(this).attr("toolbaritem"),$(this).attr("fontsize"));
			});
			var heightTmp = spreadDom.height();
			spreadDom.height(heightTmp - $("#_spreadToolbar").height() - 4);
			$(".drop-color").css("min-width","160px");
		}
	}
	
	Toolbar._initClickEvents = function(cmd , e){
		var sheet = Toolbar._spread.getActiveSheet();
	    sheet.isPaintSuspended(true);
	    var styleName = "";
	    switch (cmd) {
	    	case Toolbar._Constants.save : 
	    		// 保存报表
	    		if(typeof rpt_save == "function"){	    			
	    			rpt_save();
	    		}
                break;
	    	case Toolbar._Constants.fontsize :
	    		//字号大小
	    		var sels = sheet.getSelections();
	    		selectionToolBar.fontSize(e);
	            for (var n = 0; n < sels.length; n++) {
	            	var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	            	for(var i = 0; i < sel.rowCount;i++){
	            		for(var j = 0; j < sel.colCount ; j++){
	            			var cell = sheet.getCell(sel.row + i,sel.col + j);
	            			var font = cell.font();
	            			var newFont =Utils.getFont(font);
	    		    		newFont.fontsize=e+"pt";
	    		    		var fontinfo = newFont._toString();
	    		    		cell.font(fontinfo);
	            		}
	            	}
	            }
	    		break;
	    	case Toolbar._Constants.font :
	    		//字体
	    		var sels = sheet.getSelections();
	    		selectionToolBar.font(e);
	            for (var n = 0; n < sels.length; n++) {
	            	var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	            	for(var i = 0; i < sel.rowCount;i++){
	            		for(var j = 0; j < sel.colCount ; j++){
	            			var cell = sheet.getCell(sel.row + i,sel.col + j);
	            			var font = cell.font();
	            			var newFont =Utils.getFont(font);
	    		    		newFont.font=e;
	    		    		var fontinfo = newFont._toString();
	    		    		cell.font(fontinfo);
	            		}
	            	}
	            }
	    		break;
	    	case Toolbar._Constants.fontcolor : 
	    		// 字体颜色
	    	case Toolbar._Constants.backgroundcolornone : 
	    		// 无背景颜色
	    	case Toolbar._Constants.backgroundcolor :
	    		// 背景颜色
	    		var sels = sheet.getSelections();
	    		for (var n = 0; n < sels.length; n++) {
	    			var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	    			var cellTmp = sheet.getRange(sel.row, sel.col, sel.rowCount, sel.colCount, GC.Spread.Sheets.SheetArea.viewport);
	    			if(cmd == Toolbar._Constants.fontcolor){
	    				if(e
	    						&& typeof e.color != "undefined"){
	    					cellTmp.foreColor(e.color);
	    				}
	    			} else if(cmd == Toolbar._Constants.backgroundcolor){
	    				if(e
	    						&& typeof e.color != "undefined"){
	    					cellTmp.backColor(e.color);
	    				}
	    			} else if(cmd == Toolbar._Constants.backgroundcolornone){
	    				cellTmp.backColor("#FFF");
	    				cellTmp.clearStyleProperty("backColor");
	    			}
	    		}
	    		break;
	    	case Toolbar._Constants.bold : 
	    		// 加粗 , 处理逻辑和斜体相似，所以此处没有break，直接使用斜体中的逻辑
	    	case Toolbar._Constants.italic : 
	    		// 斜体
	    		if(cmd == Toolbar._Constants.bold){
	    			styleName = "bold";
	    		}else{    			
	    			styleName = "italic";
	    		}
	    		var font = sheet.getCell(sheet.getActiveRowIndex(), 
	    				sheet.getActiveColumnIndex(), GC.Spread.Sheets.SheetArea.viewport).font();
	    		var baseFont =Utils.getFont(font);
	    		var style="";
	    		if(baseFont[styleName]!="" && baseFont[styleName]!="normal"){
	    			style="";
	    		}
	    		else{
	    			style=styleName;
	    		}
	    		selectionToolBar[styleName](style);
	    		var sels = sheet.getSelections();
	            for (var n = 0; n < sels.length; n++) {
	            	var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	            	for(var i = 0; i < sel.rowCount;i++){
	            		for(var j = 0; j < sel.colCount ; j++){
	            			var cell = sheet.getCell(sel.row + i,sel.col + j);
	            			var font = cell.font();
	            			var newFont =Utils.getFont(font);
	    		    		newFont[styleName]=style;
	    		    		cell.font(newFont._toString());
	    		    		var seq=cell.tag();
	            		}
	            	}
	            }
	    		break;
	    	case Toolbar._Constants.underline :
	    		// 下划线
	    		var sels = sheet.getSelections(),
	            underline = GC.Spread.Sheets.TextDecorationType.underline;
	    		for (var n = 0; n < sels.length; n++) {
	    			var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount()),
	                	textDecoration = sheet.getCell(sel.row, sel.col, GC.Spread.Sheets.SheetArea.viewport).textDecoration();
	    			if ((textDecoration & underline) === underline) {
	    				textDecoration = textDecoration - underline;
	    			} else {
	    				textDecoration = textDecoration | underline;
	    			}
	    			var cellRange = sheet.getRange(sel.row, sel.col, sel.rowCount, sel.colCount, GC.Spread.Sheets.SheetArea.viewport);
                    cellRange.textDecoration(textDecoration);
	    		}
	    		break;
	    	case Toolbar._Constants.subscript : 
	    		// 和superscript逻辑一致
	    	case Toolbar._Constants.superscript : 
	    		var font = sheet.getCell(sheet.getActiveRowIndex(), 
	    				sheet.getActiveColumnIndex(), GC.Spread.Sheets.SheetArea.viewport).font();
	    		var newFont =Utils.getFont(font);
	    		var suffix=newFont.fontsize.substring(newFont.fontsize.length - 2,newFont.fontsize.length);
	    		if(cmd == Toolbar._Constants.subscript){
					// 字体大小减1
	    			newFont.fontsize = (Number(newFont.fontsize.substring(0,newFont.fontsize.length - 2)) - 1) + suffix;
				}else{
					// 字体大小加1
					newFont.fontsize = (Number(newFont.fontsize.substring(0,newFont.fontsize.length - 2)) + 1) + suffix;
				}
	    		
	    		var sels = sheet.getSelections();
	            for (var n = 0; n < sels.length; n++) {
	                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	                sheet.getCells(sel.row, sel.col, sel.row + sel.rowCount - 1, 
	                		sel.col + sel.colCount - 1, GC.Spread.Sheets.SheetArea.viewport).font(newFont._toString());
	            }
	    		break;
	    	case Toolbar._Constants.mergecell : 
	    		// 合并单元格
	    		var sels = sheet.getSelections();
	            var hasSpan = false;
	            for (var n = 0; n < sels.length; n++) {
	                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	                if (sheet.getSpans(sel, GC.Spread.Sheets.SheetArea.viewport).length > 0) {
	                    for (var i = 0; i < sel.rowCount; i++) {
	                        for (var j = 0; j < sel.colCount; j++) {
	                            sheet.removeSpan(i + sel.row, j + sel.col);
	                        }
	                    }
	                    hasSpan = true;
	                }
	            }
	            if (!hasSpan) {
	            	var cellValue = "";
	            	var cellTag = "";
	                for (var n = 0; n < sels.length; n++) {
	                    var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
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
	                    sheet.addSpan(sel.row, sel.col, sel.rowCount, sel.colCount);
	                    var align = GC.Spread.Sheets.VerticalAlign.center;
                        var cellRange = sheet.getRange(sel.row, sel.col, sel.rowCount, sel.colCount, GC.Spread.Sheets.SheetArea.viewport);
                        cellRange.vAlign(align);
	                }
	            }
	    		break;
	    	case Toolbar._Constants.borderall :
	    	case Toolbar._Constants.borderleft :
	    	case Toolbar._Constants.borderright :
	    	case Toolbar._Constants.bordertop :
	    	case Toolbar._Constants.borderbottom :
	    	case Toolbar._Constants.borderout :
	    	case Toolbar._Constants.bordernone :
	    		var sels = sheet.getSelections();
	    		for (var n = 0; n < sels.length; n++) {
	                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	                var settings = Toolbar._bordSettings[cmd] ;
	                // 先清除旧边框样式，再添加新样式
	                var range = sheet.getRange(sel.row, sel.col, sel.rowCount, sel.colCount);
	                range.setBorder(null, {all: true});
	                range.setBorder(Toolbar._default.borderStyle, settings);
	            }
	    		break;
	    	case Toolbar._Constants.aligntop :
	    	case Toolbar._Constants.alignmiddle :
	    	case Toolbar._Constants.alignbottom :
	    		var align = GC.Spread.Sheets.VerticalAlign.top;
	            if (cmd == Toolbar._Constants.alignmiddle){
	            	align = GC.Spread.Sheets.VerticalAlign.center;
	            }
	            if (cmd == Toolbar._Constants.alignbottom){
	            	align = GC.Spread.Sheets.VerticalAlign.bottom;
	            }
	            var sels = sheet.getSelections();
	            for (var n = 0; n < sels.length; n++) {
	                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	                var cellRange = sheet.getRange(sel.row, sel.col, sel.rowCount, sel.colCount, GC.Spread.Sheets.SheetArea.viewport)
                    cellRange.vAlign(align);
	            }
	    		break;
	    	case Toolbar._Constants.alignleft :
	    	case Toolbar._Constants.aligncenter :
	    	case Toolbar._Constants.alignright :
	    		var align = GC.Spread.Sheets.HorizontalAlign.left;
	            if (cmd == Toolbar._Constants.aligncenter){
	            	align = GC.Spread.Sheets.HorizontalAlign.center;
	            }
	            if (cmd == Toolbar._Constants.alignright){
	            	align = GC.Spread.Sheets.HorizontalAlign.right;
	            }
	            var sels = sheet.getSelections();
	            for (var n = 0; n < sels.length; n++) {
	                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
                    var cellRange = sheet.getRange(sel.row, sel.col, sel.rowCount, sel.colCount, GC.Spread.Sheets.SheetArea.viewport);
                    cellRange.hAlign(align);
	            }
	    		break;
	    	case Toolbar._Constants.importcss : 
	    		// 导入样式
	    		BIONE.commonOpenDialog('样式上传','requireuploadWin',600,330,Toolbar._ctx+'/report/frame/design/cfg/cssupload?d='+new Date().getTime());
	    		break;
	    	case Toolbar._Constants.changeShowType :
	    		// 翻转展现方式
	    		if(window.Design){
	    			Design.changeShowType();
	    		}
	    		break;
	    	case Toolbar._Constants.remind : 
	    		// 使用提醒
	    		BIONE.commonOpenDialog('使用提醒', 'remind', 1200, 800, Toolbar._ctx+'/report/frame/design/cfg/remind');
	    		break;
	    	default : 
	    		break;
	    }
	    sheet.isPaintSuspended(false);
	}

	Toolbar._getActualCellRange = function(cellRange, rowCount, columnCount) {
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

	return Toolbar;
	
})