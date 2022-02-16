<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<script type="text/javascript">
    
    $(function() {
    	var sheet = parent.Design.spread.getActiveSheet();
		var selections = sheet.getSelections();
		var value = sheet.getText(selections[0].row, selections[0].col);
		var rightTopValue = "";
		var leftBottomValue = "";
		if (value) {
			var idx = value.lastIndexOf('\n');
			if (idx >= 0) {
				rightTopValue = value.substring(0, idx);
				leftBottomValue = value.substring(idx + 1);
			} else {
				rightTopValue = "";
				leftBottomValue = value;
			}
		}
		var mainform = $("#mainform");
		mainform.ligerForm({
		    fields : [ 
		    {
				display : "右上角内容",
				name : "rightTop",
				id : "rightTop",
			}, {
				display : "左下角内容",
				name : "leftBottom",
				id : "leftBottom",
		    } ]
		});
		$("#rightTop").attr("value", rightTopValue);
		$("#leftBottom").attr("value", leftBottomValue);
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : onCancle
		});
		buttons.push({
		    text : '确认',
		    onclick : onOK
		});
		BIONE.addFormButtons(buttons);
	});
    
    function onOK() {
    	var sheet = parent.Design.spread.getActiveSheet();
		var selections = sheet.getSelections();
		var cells = sheet.getRange(selections[0].row, selections[0].col,
				selections[0].rowCount, selections[0].colCount);
		cells.diagonalDown(new GC.Spread.Sheets.LineBorder("black", GC.Spread.Sheets.LineStyle.thin));
		cells.vAlign(GC.Spread.Sheets.VerticalAlign.center);
		cells.text($("#rightTop").val() + "\n" + $("#leftBottom").val());
		BIONE.closeDialog("diagonal");
    }

	function onCancle() {
		BIONE.closeDialog("diagonal");
    }
    
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform"></form>
	</div>
</body>
</html>