/*******************************************************/
/****             报表设计器 - 单元格内容明细                    ****/
/****            （仅不支持html5时使用）                          ****/
/*******************************************************/
define(function(){
	
	var Detail = {};
	
	Detail.initCellDetail = function(detailDom , spreadDom , spread){
        var formulaBoxHtml = '     <div id="_contextbox" contenteditable="true" spellcheck="false" style="border: 1px solid gray;height: 16px; line-height: 16px;width: 98%; overflow: hidden; font-size: 13px;font-family: arial,sans-serif;padding: 3px;"></div>';
        var html = 
			'<table id="_cellDetailBar" style="width: 100%; border: 1px solid gray">                                                                                                  '+
			'    <tr>                                                                                                                                                                                               '+
			'    <td >                                                                                                                                                              '+
			'        <input type="text" id="_positionbox" data-bind="value: positionLabel	" disabled="disabled" style="width:80px;text-align: center; height: 18px;margin: 0;"></input>                 '+
			'    </td>                                                                                                                                                                                             '+
			'    <td>                                                                                                                                                                                               '+
	        formulaBoxHtml +
			'    </div> '+
			'    </td>                                                                                                                                                                                             '+
			'    </tr>                                                                                                                                                                                              '+
			'</table>'
		detailDom.append(html);
		var heightTmp = spreadDom.height();
		spreadDom.height(heightTmp - $("#_cellDetailBar").height());
		$("#_contextbox").width($("#spread").width() - $("#_positionbox").width() - 10);
        var fbx = new GC.Spread.Sheets.FormulaTextBox.FormulaTextBox(document.getElementById('_contextbox'));
        fbx.workbook(spread);
		return $("#_contextbox");
	}

	return Detail;
	
})