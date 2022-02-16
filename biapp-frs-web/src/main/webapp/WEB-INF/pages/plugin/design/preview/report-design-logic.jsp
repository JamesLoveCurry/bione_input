<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<style >
	.noSelectText{
		-moz-user-select:none;
	}
	.haveBorder{
		border: 1px solid #999;
	}
</style>
<script type="text/javascript" src="${ctx}/plugin/js/frame/idx/cursorPosition.js"></script>
<script type="text/javascript">
	//表间计算单元格类型
	var cellType = "05";
	var leftTreeObj;
	var dimMap = [];
	var flag = true;
	var currSheet = window.parent.Design.spread.getActiveSheet();
	var selRow = currSheet.getActiveRowIndex();
	var selCol = currSheet.getActiveColumnIndex();
	var spans = window.parent.Design.getSelectionSpans(currSheet);
	if(spans 
			&& typeof spans.length != "undefined"
			&& spans.length > 0){
		selRow = spans[0].row;
		selCol = spans[0].col;
	}
	var currentNode;
	var designInfo4Upt;
	var position = {start : 0, end : 0};
	$(function(){
		initForm();
	});
	
	function addFormButton(){
		var btns = [ {
			text : "返回",
			onclick : function() {
				BIONE.closeDialog("moduleClkDialog");
			}
		}];
		BIONE.addFormButtons(btns);
	}
	
	function initForm(){
		//修改时初始化表单
		if(currSheet){
			var currVal = "";
            var seq = window.parent.Design._getStyleProperty(selRow, selCol, "seq");			
			if(window.parent.Design.rptIdxs
					&& seq != null
					&& typeof seq != "undefined"){
				var rptIdxTmp = window.parent.Design.rptIdxs[seq];
				if(rptIdxTmp
						&& rptIdxTmp.cellType == "05"){
					currVal = rptIdxTmp.formulaDesc;
				}
			}
			if(currVal != null
					&& currVal != ""){
				$("#expression").val(currVal);
				position.start = currVal.length;
				position.end = currVal.length;
			}
		}
	}
	
	

	
</script>
</head>
<body>
<div id="template.center">
	<div id="top_middle"
		style="width: 90%; height: 98%; margin-left: auto; margin: auto; margin-top: 2%;">
		<div id="top_middle_left">

			<div id="top_middle_left"
				style="width: 100%; height: 100%; ">
				<textarea id="expression"
					style="width: 99%; height: 300px; margin-top: 0%;float:left;resize:none" ></textarea>
			</div>
		</div>
	</div>
</div>
</body>
</html>