/*******************************************************/
/****                        报表设计器 - 工具栏                         ****/
/** **************************************************** */
define(function() {
	
	var Toolbar = {};
	
	Toolbar._ribbon = {};
	
	Toolbar._spread = {};
	
	Toolbar._ctx = "";
	
	Toolbar._Constants = {
			backgroundColor : "背景颜色",
			font : "字体",
			bold : "加粗",
			italic : "倾斜",
			underline : "下划线",
			subscript : "减小字体",
			superscript : "增大字体",
			mergecell : "合并单元格",
			unmergecell : "取消合并",
			importcss : "导入样式",
			borders : "边框",
			borderall : "所有框线",
			borderleft : "左框线",
			borderright : "右框线",
			bordertop : "上框线",
			borderbottom : "下框线",
			borderout : "外边框",
			bordernone : "无边框",
			alignsh : "水平对齐",
			alignleft : "左对齐",
			aligncenter : "居中",
			alignsv : "垂直对齐",
			alignright : "右对齐",
			aligntop : "顶端对齐",
			alignmiddle : "垂直对齐",
			alignbottom : "底端对齐",
			changeShowType : "翻转展示方式"
	};
	
	Toolbar._default = {
			normalKeyword : "normal",
			font : "Calibri",
			fontsize : "10pt",
			bold : "",
			italic : "",
			borderStyle : new GC.Spread.Sheets.LineBorder("Black",  GC.Spread.Sheets.LineStyle.thin),
			noneBorderStyle : new GC.Spread.Sheets.LineBorder("Black",  GC.Spread.Sheets.LineStyle.none) //wjx修改
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
	Toolbar._font = {
			font : Toolbar._default.font,
			fontsize : Toolbar._default.fontsize,
			bold : Toolbar._default.bold,
			italic : Toolbar._default.italic,
			_toString : function(){
				var fontStr = "";
				fontStr = (this.bold == null? "" : this.bold) + 
					(this.italic == null || this.italic == "" ? "" : " " + this.italic) +
					(this.fontsize == null || this.fontsize == "" ? Toolbar._default.fontsize : " " + this.fontsize) + 
					(this.font == null || this.font == "" ? Toolbar._default.font : " " + this.font);
				return $.trim(fontStr);
			}
	}
	
	/**
	 * 初始化
	 */
	Toolbar.initToolbar = function(toolbarDom, spreadDom , ctx , isBusiLine) {
		Toolbar._ctx = ctx;
		if(toolbarDom != null
				&& typeof toolbarDom != "undefined"){
			var html = 
				"<div id='_ribbon'>                                                                                                             "+
	//			"  <button title='"+Toolbar._Constants.backgroundColor+"' class='wijmo-wijribbon-bgcolor' name='backcolor'>   "+
	//			"  </button>                                                                                                                       "+
	//			"  <div title='"+Toolbar._Constants.font+"' class='wijmo-wijribbon-dropdownbutton'>                                "+
	//			"      <button title='"+Toolbar._Constants.font+"' name='fontname'>                                                              "+
	//			"          "+Toolbar._Constants.font+"</button>                                                                                                         "+
	//			"      <ul>                                                                                                                           "+
	//			"          <li>                                                                                                                         "+
	//			"              <input type='radio' id='C1Editor1_ctl74' name='fontname'></input>                   "+
	//			"              <label for='C1Editor1_ctl74' name='Arial' title='Arial'>                                          "+
	//			"                  Arial</label></li>                                                                                             "+
	//			"          <li>                                                                                                                         "+
	//			"              <input type='radio' id='C1Editor1_ctl76' name='fontname'></input>                   "+
	//			"              <label for='C1Editor1_ctl76' name='Courier New' title='Courier New'>                              "+
	//			"                  Courier New</label></li>                                                                                 "+
	//			"          <li>                                                                                                                         "+
	//			"              <input type='radio' id='C1Editor1_ctl78' name='fontname'></input>                   "+
	//			"              <label for='C1Editor1_ctl78' name='Garamond' title='Garamond'>                                 "+
	//			"                  Garamond</label></li>                                                                                    "+
	//			"          <li>                                                                                                                         "+
	//			"              <input type='radio' id='C1Editor1_ctl80' name='fontname'></input>                   "+
	//			"              <label for='C1Editor1_ctl80' name='Tahoma' title='Tahoma'>                                     "+
	//			"                  Tahoma</label></li>                                                                                        "+
	//			"          <li>                                                                                                                         "+
	//			"              <input type='radio' id='C1Editor1_ctl82' name='fontname'></input>                   "+
	//			"              <label for='C1Editor1_ctl82' name='Times New Roman' title='Times New Roman'>                     "+
	//			"                  Times New Roman</label></li>                                                                       "+
	//			"          <li>                                                                                                                         "+
	//			"              <input type='radio' id='C1Editor1_ctl84' name='fontname'></input>                   "+
	//			"              <label for='C1Editor1_ctl84' name='Verdana' title='Verdana'>                                     "+
	//			"                  Verdana</label></li>                                                                                       "+
	//			"          <li>                                                                                                                         "+
	//			"              <input type='radio' id='C1Editor1_ctl86' name='fontname'></input>                   "+
	//			"              <label for='C1Editor1_ctl86' name='Wingdings' title='Wingdings'>                                 "+
	//			"                  Wingdings</label></li>                                                                                   "+
	//			"      </ul>                                                                                                                          "+
	//			"  </div>                                                                                                                            "+
				"  <div title='"+Toolbar._Constants.borders+"' class='wijmo-wijribbon-dropdownbutton'>                                "+
				"      <button title='"+Toolbar._Constants.borders+"' name='borders'>                                                              "+
				"          "+Toolbar._Constants.borders+"</button>                                                                                                         "+
				"      <ul>                                                                                                                           "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='border_ctl1' name='borders'></input>                   "+
				"              <label for='border_ctl1' name='"+Toolbar._Constants.borderall+"' title='"+Toolbar._Constants.borderall+"' class= 'wijmo-wijribbon-borders'>                                          "+
				"               "+Toolbar._Constants.borderall+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='border_ctl2' name='borders'></input>                   "+
				"              <label for='border_ctl2' name='"+Toolbar._Constants.borderleft+"' title='"+Toolbar._Constants.borderleft+"' class= 'wijmo-wijribbon-borders'>                                          "+
				"               "+Toolbar._Constants.borderleft+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='border_ctl3' name='borders'></input>                   "+
				"              <label for='border_ctl3' name='"+Toolbar._Constants.borderright+"' title='"+Toolbar._Constants.borderright+"' class= 'wijmo-wijribbon-borders'>                                          "+
				"               "+Toolbar._Constants.borderright+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='border_ctl4' name='borders'></input>                   "+
				"              <label for='border_ctl4' name='"+Toolbar._Constants.bordertop+"' title='"+Toolbar._Constants.bordertop+"' class= 'wijmo-wijribbon-borders'>                                          "+
				"               "+Toolbar._Constants.bordertop+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='border_ctl5' name='borders'></input>                   "+
				"              <label for='border_ctl5' name='"+Toolbar._Constants.borderbottom+"' title='"+Toolbar._Constants.borderbottom+"' class= 'wijmo-wijribbon-borders'>                                          "+
				"               "+Toolbar._Constants.borderbottom+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='border_ctl6' name='borders'></input>                   "+
				"              <label for='border_ctl6' name='"+Toolbar._Constants.borderout+"' title='"+Toolbar._Constants.borderout+"' class= 'wijmo-wijribbon-borders'>                                          "+
				"               "+Toolbar._Constants.borderout+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='border_ctl7' name='borders'></input>                   "+
				"              <label for='border_ctl7' name='"+Toolbar._Constants.bordernone+"' title='"+Toolbar._Constants.bordernone+"' class= 'wijmo-wijribbon-borders'>                                          "+
				"               "+Toolbar._Constants.bordernone+"</label></li>                                                                                             "+
				"      </ul>                                                                                                                          "+
				"  </div>                                                                                                                            "+
				"  <input type='checkbox' id='C1Editor1_ctl137'></input>                                                 "+
				"  <label for='C1Editor1_ctl137' name='"+Toolbar._Constants.subscript+"' title='"+Toolbar._Constants.subscript+"' class=                           "+
				"			'wijmo-wijribbon-sub'>"+
				"  </label>                                                                                                                         "+
				"  <input type='checkbox' id='C1Editor1_ctl138'></input>                                                 "+
				"  <label for='C1Editor1_ctl138' name='"+Toolbar._Constants.superscript+"' title='"+Toolbar._Constants.superscript+"' class=                    "+
				"			'wijmo-wijribbon-sup'>"+
				"  </label>                                                                                                                         "+
				"  <input type='checkbox' id='C1Editor1_ctl133'></input>                                                 "+
				"  <label for='C1Editor1_ctl133' name='"+Toolbar._Constants.bold+"' title='"+Toolbar._Constants.bold+"' class='wijmo-wijribbon-bold'>    "+
				"  </label>                                                                                                                         "+
				"  <input type='checkbox' id='C1Editor1_ctl134'></input>                                                 "+
				"  <label for='C1Editor1_ctl134' name='"+Toolbar._Constants.italic+"' title='"+Toolbar._Constants.italic+"' class='wijmo-wijribbon-italic'> "+
				"  </label>                                                                                                                         "+
				"  <input type='checkbox' id='C1Editor1_ctl135'></input>                                                 "+
				"  <label for='C1Editor1_ctl135' name='"+Toolbar._Constants.underline+"' title='"+Toolbar._Constants.underline+"' class=       "+
				"           'wijmo-wijribbon-underline'>"+
				"  </label>                                                                                                                         "+ 
				"  <div title='"+Toolbar._Constants.alignsh+"' class='wijmo-wijribbon-dropdownbutton'>                                "+
				"      <button title='"+Toolbar._Constants.alignsh+"' name='alignsh'>                                                              "+
				"          "+Toolbar._Constants.alignsh+"</button>                                                                                                         "+
				"      <ul>                                                                                                                           "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='align_ctl1' name='alignsh'></input>                   "+
				"              <label for='align_ctl1' name='"+Toolbar._Constants.alignleft+"' title='"+Toolbar._Constants.alignleft+"' class= 'wijmo-wijribbon-justifyleft'>                                          "+
				"               "+Toolbar._Constants.alignleft+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='align_ctl2' name='alignsh'></input>                   "+
				"              <label for='align_ctl2' name='"+Toolbar._Constants.aligncenter+"' title='"+Toolbar._Constants.aligncenter+"' class= 'wijmo-wijribbon-justifycenter'>                                          "+
				"               "+Toolbar._Constants.aligncenter+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='align_ctl3' name='alignsh'></input>                   "+
				"              <label for='align_ctl3' name='"+Toolbar._Constants.alignright+"' title='"+Toolbar._Constants.alignright+"' class= 'wijmo-wijribbon-justifyright'>                                          "+
				"               "+Toolbar._Constants.alignright+"</label></li>                                                                                             "+
				"      </ul>                                                                                                                          "+
				"  </div>                                                                                                                            "+
				"  <div title='"+Toolbar._Constants.alignsv+"' class='wijmo-wijribbon-dropdownbutton'>                                "+
				"      <button title='"+Toolbar._Constants.alignsv+"' name='alignsv'>                                                              "+
				"          "+Toolbar._Constants.alignsv+"</button>                                                                                                         "+
				"      <ul>                                                                                                                           "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='align_ctl4' name='alignsv'></input>                   "+
				"              <label for='align_ctl4' name='"+Toolbar._Constants.aligntop+"' title='"+Toolbar._Constants.aligntop+"' class= 'wijmo-wijribbon-insertcol'>                                          "+
				"               "+Toolbar._Constants.aligntop+"</label></li>                                                                                             "+
				"          <li>                                              	                                                                           "+
				"              <input type='radio' id='align_ctl5' name='alignsv'></input>                   "+
				"              <label for='align_ctl5' name='"+Toolbar._Constants.alignmiddle+"' title='"+Toolbar._Constants.alignmiddle+"' class= 'wijmo-wijribbon-insertrow'>                                          "+
				"               "+Toolbar._Constants.alignmiddle+"</label></li>                                                                                             "+
				"          <li>                                                                                                                         "+
				"              <input type='radio' id='align_ctl6' name='alignsv'></input>                   "+
				"              <label for='align_ctl6' name='"+Toolbar._Constants.alignbottom+"' title='"+Toolbar._Constants.alignbottom+"' class= 'wijmo-wijribbon-deletecol'>                                          "+
				"               "+Toolbar._Constants.alignbottom+"</label></li>                                                                                             "+
				"      </ul>                                                                                                                          "+
				"  </div>                                                                                                                            "+
	//			"  <input type='checkbox' id='C1Editor1_ctl140'></input>                                                 "+
	//			"  <label for='C1Editor1_ctl140' name='splitcell' title='splitcell' class=                    "+
	//			"			'wijmo-wijribbon-splitcell'>"+
	//			"  </label>                                                                                                                         "+
				"  <input type='checkbox' id='C1Editor1_ctl139'></input>                                                 "+
				"  <label for='C1Editor1_ctl139' name='"+Toolbar._Constants.mergecell+"' title='"+Toolbar._Constants.mergecell+"' class=                    "+
				"			'wijmo-wijribbon-mergecell'>"+
				"  </label>                                                                                                                         " +
				"  <input type='checkbox' id='C1Editor1_ctl141'></input>                                                 "+
				"  <label for='C1Editor1_ctl141' name='"+Toolbar._Constants.importcss+"' title='"+Toolbar._Constants.importcss+"' class=                    "+
				"			'wijmo-wijribbon-import'>"+
				"  </label>                                                                                                                         "+
//				"  <input type='checkbox' id='C1Editor1_ctl142'></input>                                                 "+
//				"  <label for='C1Editor1_ctl142' name='"+Toolbar._Constants.changeShowType+"' title='"+Toolbar._Constants.changeShowType+"' class=                    "+
//				"			'wijmo-wijribbon-inspect'>"+
//				"  </label>                                                                                                                         "+
				"</div>                                                                                                                              ";
			toolbarDom.append(html);
			$("#_ribbon").width(toolbarDom.width());
			Toolbar._spread = Design.spreadDom.wijspread("spread");
			if(Toolbar._spread){
				Toolbar._ribbon = $("#_ribbon").wijribbon({
					click : Toolbar._initClickEvents
				});
				var heightTmp = spreadDom.height();
				spreadDom.height(heightTmp - $("#_spreadToolbar").height());
			}
		}
	}
	
	Toolbar._initClickEvents = function(e , cmd){
		var sheet = Toolbar._spread.getActiveSheet();
	    sheet.isPaintSuspended(true);
	    var styleName = "";
	    switch (cmd.commandName) {
	    	case Toolbar._Constants.backgroundColor : 
	    		//颜色选择 : 略复杂，暂没实现
	    		break;
	    	case Toolbar._Constants.bold : 
	    		// 加粗 , 处理逻辑和斜体相似，所以此处没有break，直接使用斜体中的逻辑
	    	case Toolbar._Constants.italic : 
	    		// 斜体
	    		if(cmd.commandName == Toolbar._Constants.bold){
	    			styleName = "bold";
	    		}else{    			
	    			styleName = "italic";
	    		}
	    		var font = sheet.getCell(sheet.getActiveRowIndex(), 
	    				sheet.getActiveColumnIndex(), GC.Spread.Sheets.SheetArea.viewport).font();
	    		var newFont = {};
	    		jQuery.extend(newFont , Toolbar._font);
	    		if(typeof font != "undefined"){
	    			// 已经有样式，分析是否有加粗，若有，取消加粗，若没有，设置字体加粗
	    			var fontArr = font.split(" ");
	    			var fontRealArr = [];
	    			for(var _i = 0 ; _i < fontArr.length ; _i++){
	    				if(Toolbar._default.normalKeyword == fontArr[_i]){
	    					continue;
	    				}
	    				fontRealArr.push(fontArr[_i]);
	    			}
	    			var flag = 0;
					for(var i = 0 ; i < fontRealArr.length ; i++){
						var fontName = fontRealArr[i];
						if("bold" == fontName
								|| "italic" == fontName){
							if(styleName != fontName){    								
								newFont[fontName] = fontName;
							}
						}else{						
							switch (flag) {
							case 0 :
								newFont.fontsize = fontRealArr[i];
								break;
							case 1 : 
								newFont.font = fontRealArr[i];
								break;
							}
							flag++;
						}
					}
	    			if(font.indexOf(styleName) == -1){
	    				// stylename的样式不存在
	    				newFont[styleName] = styleName;
	    			}
	    		}else{
	    			//不存在样式定义
	    			newFont[styleName] = styleName;
	    		}
	    		var sels = sheet.getSelections();
	            for (var n = 0; n < sels.length; n++) {
	                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	                sheet.getCells(sel.row, sel.col, sel.row + sel.rowCount - 1, 
	                		sel.col + sel.colCount - 1, GC.Spread.Sheets.SheetArea.viewport).font(newFont._toString());
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
	    			sheet.getCells(sel.row, sel.col, sel.row + sel.rowCount - 1, sel.col + sel.colCount - 1, GC.Spread.Sheets.SheetArea.viewport).textDecoration(textDecoration);
	    		}
	    		break;
	    	case Toolbar._Constants.subscript : 
	    		// 和superscript逻辑一致
	    	case Toolbar._Constants.superscript : 
	    		var font = sheet.getCell(sheet.getActiveRowIndex(), 
	    				sheet.getActiveColumnIndex(), GC.Spread.Sheets.SheetArea.viewport).font();
	    		var newFont = {};
	    		jQuery.extend(newFont , Toolbar._font);
	    		if(typeof font != "undefined"){
	    			// 已经有样式，将字体大小减小
	    			var fontArr = font.split(" ");
	    			var fontRealArr = [];
	    			for(var _i = 0 ; _i < fontArr.length ; _i++){
	    				if(Toolbar._default.normalKeyword == fontArr[_i]){
	    					continue;
	    				}
	    				fontRealArr.push(fontArr[_i]);
	    			}
	    			var flag = 0;
					for(var i = 0 ; i < fontRealArr.length ; i++){
						var fontName = fontRealArr[i];
						if("bold" != fontName
								&& "italic" != fontName){
							switch (flag) {
							case 0 :
								var oldFontStyle = fontRealArr[i];
								var oldFontSize = "";
								var oldLineHeight = "";
								var newFontSize = oldFontStyle;
								if(oldFontStyle != null
										&& oldFontStyle.length > 2){
									// 判断是否有lineHeight
									if(oldFontStyle.indexOf("/") != -1){
										oldFontSize = oldFontStyle.split("/")[0];
										newFontSize = oldFontSize;
										lineHeight = oldFontStyle.split("/")[1];
										if(lineHeight == Toolbar._default.normalKeyword){
											lineHeight = "";
										}
									}else{
										oldFontSize = oldFontStyle;
									}
									if(oldFontSize.indexOf("px") == (oldFontSize.length - 2)
											|| oldFontSize.indexOf("pt") == (oldFontSize.length - 2)){
										var suffix = "px";
										if(oldFontSize.indexOf("pt") != -1){
											suffix = "pt";
										}
										if(cmd.commandName == Toolbar._Constants.subscript){
											// 字体大小减1
											newFontSize = (Number(oldFontSize.substring(0,oldFontSize.length - 2)) - 1) + suffix;
										}else{
											// 字体大小加1
											newFontSize = (Number(oldFontSize.substring(0,oldFontSize.length - 2)) + 1) + suffix;
										}
									}
								}
								newFont.fontsize = newFontSize;
								break;
							case 1 : 
								newFont.font = fontRealArr[i];
								break;
							}
							flag++;
						}else{
							newFont[fontName] = fontName;
						}
					}
	    		}else{
	    			var newFontsize = newFont.fontsize;
	    			if(cmd.commandName == Toolbar._Constants.subscript){
						// 字体大小减1
						newFontsize = (Number(newFontsize.substring(0,newFontsize.length - 2)) - 1) + "pt";
					}else{
						// 字体大小加1
						newFontsize = (Number(newFontsize.substring(0,newFontsize.length - 2)) + 1) + "pt";
					}
	    			newFont.fontsize = newFontsize;
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
	                for (var n = 0; n < sels.length; n++) {
	                    var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	                    sheet.addSpan(sel.row, sel.col, sel.rowCount, sel.colCount);
	                    var align = GC.Spread.Sheets.VerticalAlign.center;
	                    sheet.getCells(sel.row, sel.col, sel.row + sel.rowCount - 1, sel.col + sel.colCount - 1, GC.Spread.Sheets.SheetArea.viewport).vAlign(align);
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
	                var settings = Toolbar._bordSettings[cmd.commandName] ;
	                // 先清除旧边框样式，再添加新样式
	                sheet.setBorder(sel, Toolbar._default.noneBorderStyle, {all: true});//wjx修改
	                sheet.setBorder(sel, Toolbar._default.borderStyle, settings);
	            }
	    		break;
	    	case Toolbar._Constants.aligntop :
	    	case Toolbar._Constants.alignmiddle :
	    	case Toolbar._Constants.alignbottom :
	    		var align = GC.Spread.Sheets.VerticalAlign.top;
	            if (cmd.commandName == Toolbar._Constants.alignmiddle){
	            	align = GC.Spread.Sheets.VerticalAlign.center;
	            }
	            if (cmd.commandName == Toolbar._Constants.alignbottom){
	            	align = GC.Spread.Sheets.VerticalAlign.bottom;
	            }
	            var sels = sheet.getSelections();
	            for (var n = 0; n < sels.length; n++) {
	                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	                sheet.getCells(sel.row, sel.col, sel.row + sel.rowCount - 1, sel.col + sel.colCount - 1, GC.Spread.Sheets.SheetArea.viewport).vAlign(align);
	            }
	    		break;
	    	case Toolbar._Constants.alignleft :
	    	case Toolbar._Constants.aligncenter :
	    	case Toolbar._Constants.alignright :
	    		var align = GC.Spread.Sheets.HorizontalAlign.left;
	            if (cmd.commandName == Toolbar._Constants.aligncenter){
	            	align = GC.Spread.Sheets.HorizontalAlign.center;
	            }
	            if (cmd.commandName == Toolbar._Constants.alignright){
	            	align = GC.Spread.Sheets.HorizontalAlign.right;
	            }
	            var sels = sheet.getSelections();
	            for (var n = 0; n < sels.length; n++) {
	                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	                sheet.getCells(sel.row, sel.col, sel.row + sel.rowCount - 1, sel.col + sel.colCount - 1, GC.Spread.Sheets.SheetArea.viewport).hAlign(align);
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
	    	default : 
	    		// 字体处理，由于现在版本好像不支持汉字的字体（宋体，微软雅黑等，所以干脆没开放字体修改）
	//    		var font = sheet.getCell(sheet.getActiveRowIndex(), 
	//    				sheet.getActiveColumnIndex(), GC.Spread.Sheets.SheetArea.viewport).font();
	//    		var newFont = {};
	//    		jQuery.extend(newFont , Toolbar._font);
	//    		if(typeof font != "undefined"){
	//    			// 已经有样式，改变字体
	//    			var fontArr = font.split(" ");
	//    			var flag = 0;
	//				for(var i = 0 ; i < fontArr.length ; i++){
	//					if("bold" != fontArr[i]
	//							&& "italic" != fontArr[i]){
	//						switch (flag) {
	//						case 0 :
	//							newFont.fontsize = fontArr[i];
	//							break;
	//						case 1 : 
	//							newFont.font = cmd.commandName;
	//							break;
	//						}
	//						flag++;
	//					}else{
	//						newFont[fontName] = fontName;
	//					}
	//				}
	//    		}else{
	//    			newFont.font = cmd.commandName;
	//    		}
	//    		var sels = sheet.getSelections();
	//            for (var n = 0; n < sels.length; n++) {
	//                var sel = Toolbar._getActualCellRange(sels[n], sheet.getRowCount(), sheet.getColumnCount());
	//                sheet.getCells(sel.row, sel.col, sel.row + sel.rowCount - 1, 
	//                		sel.col + sel.colCount - 1, GC.Spread.Sheets.SheetArea.viewport).font(newFont._toString());
	//            }
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