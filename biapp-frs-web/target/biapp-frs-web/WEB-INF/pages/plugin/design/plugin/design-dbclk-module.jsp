<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	$(function() {
		 var groupicon = "${ctx}/images/classics/icons/communication.gif";
		 var rptInfo;
		 if(window.parent.selectionIdx
				 && typeof window.parent.selectionIdx != "undefined"){
			 rptInfo = window.parent.selectionIdx;
		 }else{
			 rptInfo = {};
		 }
		 $("#mainform").ligerForm({
			align : 'center',
			fields : [
// 			          {
// 				display : "单元格编号",
// 				name : "cellNo",
// 				newline : true,
// 				type : "text",
// 				group : "单元格信息",
// 				groupicon : groupicon
// 			}
// 			,{
// 				display : "单元格名称",
// 				name : "cellNm",
// 				newline : false,
// 				type : "text",
// 				validate : {
// 					required : true
// 				}
// 			},
			{
				display : "数据模型",
				name : "dsName",
				newline : true,
				type : "text",
				group : "模型展现",
				groupicon : groupicon,
				initValue : typeof rptInfo.dsName() == "undefined" ? "" : rptInfo.dsName()
			},{
				display : "字段名称",
				name : "columnName",
				newline : false,
				type : "text",
				initValue : typeof rptInfo.columnName() == "undefined" ? "" : rptInfo.columnName()
			},{
				display : "是否扩展",
				name : "isExt",
				newline : true,
				type : "select",
				comboboxName : "isExtCombobox",
				options : {
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N'
					} ],
					onBeforeSelect : hideOrShowFields
				}
			},{
				display : "是否排序",
				name : "isSort",
				newline : false,
				type : "select",
				comboboxName : "isSortCombobox",
				options : {
					data : [ {
						text : '是',
						id : 'Y'
					}, {
						text : '否',
						id : 'N'
					} ],
					onBeforeSelect : hideOrShowOrders
				}
			},{
				display : "扩展方向",
				name : "extDirection",
				newline : true,
				type : "select",
				comboboxName : "extDirectionCombobox",
				options : {
					initValue : typeof rptInfo.extDirection() == "undefined" || rptInfo.extDirection() == "" ? "02" : rptInfo.extDirection(),
					data : [ 
 					{
 						text : '横向',
 						id : '02'
 					},
					{
						text : '纵向',
						id : '01'
					} ]
				}
			},{
				display : "扩展方式",
				name : "extMode",
				newline : false,
				type : "select",
				comboboxName : "extModeCombobox",
				options : {
					initValue : typeof rptInfo.extMode() == "undefined" || rptInfo.extMode() == "" ? "01" : rptInfo.extMode(),
					data : [ {
						text : '插入',
						id : '01'
					}, {
						text : '覆盖',
						id : '02'
					} ]
				}
			},{
				display : "排序顺序",
				name : "sortOrder",
				newline : true,
				type : "number"
			},{
				display : "排序方式",
				name : "sortMode",
				newline : false,
				type : "select",
				comboboxName : "sortModeCombobox",
				options : {
					initValue : typeof rptInfo.sortOrder() == "undefined" || rptInfo.sortMode() == "" ? "01" : rptInfo.sortMode(),
					data : [ {
						text : '顺序(ASC)',
						id : '01'
					}, {
						text : '倒序(DESC)',
						id : '02'
					} ]
				}
			}]
	});

		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		
		$("#mainform input[name=cellNo]").attr("readonly", "true").css("color", "black").removeAttr("validate")
			.val(window.parent.getSelectionLabel().label);
		$("#mainform input[name=dsName]").attr("readonly", "true").css("color", "black").removeAttr("validate")
			.val(typeof rptInfo.dsName() == null ? "" : rptInfo.dsName());
		$("#mainform input[name=columnName]").attr("readonly", "true").css("color", "black").removeAttr("validate")
			.val(typeof rptInfo.columnName() == null ? "" : rptInfo.columnName());
		$("#mainform input[name=cellNm]").val(typeof rptInfo.cellNm() == null ? "" : rptInfo.cellNm());
		$.ligerui.get("isExtCombobox").selectValue(typeof rptInfo.isExt() == "undefined" || rptInfo.extDirection() == "" ? "Y" : rptInfo.isExt());
		hideOrShowFields($.ligerui.get("isExtCombobox").getValue());
		$.ligerui.get("isSortCombobox").selectValue(typeof rptInfo.isSort() == "undefined" || rptInfo.isSort() == "" ? "N" : rptInfo.isSort());
		hideOrShowOrders($.ligerui.get("isSortCombobox").getValue());
		$("#mainform input[name=sortOrder]").val(typeof rptInfo.sortOrder() == null ? "" : rptInfo.sortOrder());
		
		var buttons = [];
		
		buttons.push({
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("moduleClkDialog");
			}
		});
		buttons.push({
			text : '保存',
			onclick : moduleSave
		});
		BIONE.addFormButtons(buttons);
		
		// 根据是否扩展，显示/隐藏其他field
		function hideOrShowFields(isExt){
			if(isExt == "N"){
				$("#extDirectionCombobox").parents("li").hide();
				$("li:contains('扩展方向')").hide();
				$("#extModeCombobox").parents("li").hide();
				$("li:contains('扩展方式')").hide();
			}else{
				$("#extDirectionCombobox").parents("li").show();
				$("li:contains('扩展方向')").show();
				$("#extModeCombobox").parents("li").show();
				$("li:contains('扩展方式')").show();
			}
		}
		
		function hideOrShowOrders(isSort){
			if(isSort == "N"){
				$("#sortModelCombobox").parents("li").hide();
				$("li:contains('排序方式')").hide();
				$("#sortOrder").parents("li").hide();
				$("li:contains('排序顺序')").hide();
				$("#sortOrder").rules("remove");
			}else{
				$("#sortModelCombobox").parents("li").show();
				$("li:contains('排序方式')").show();
				$("#sortOrder").parents("li").show();
				$("li:contains('排序顺序')").show();
				$("#sortOrder").rules("add", {required : true});
			}
		}
		
		// 保存模型字段维护信息
		function moduleSave(){
			if (!$("#mainform").valid()) {
				return;
			}
			rptInfo.isExt($.ligerui.get("isExtCombobox").getValue());
			rptInfo.extDirection($.ligerui.get("extDirectionCombobox").getValue());
			rptInfo.extMode($.ligerui.get("extModeCombobox").getValue());
			rptInfo.isSort($.ligerui.get("isSortCombobox").getValue());
			rptInfo.sortMode($.ligerui.get("sortModeCombobox").getValue());
			rptInfo.sortOrder($("#mainform input[name=sortOrder]").val());
			if(rptInfo.cellLabel != null
					&& typeof rptInfo.cellLabel == "function"
					&& window.parent.spread){
				var currSheet = window.parent.spread.getActiveSheet();
				var selRow = currSheet.getActiveRowIndex();
				var selCol = currSheet.getActiveColumnIndex();
				var spans = window.parent.Design.getSelectionSpans(currSheet);
				if(spans 
						&& typeof spans.length != "undefined"
						&& spans.length > 0){
					selRow = spans[0].row;
					selCol = spans[0].col;
				}
				var currCell = currSheet.getCell(selRow , selCol);
				var posiNew = initLabel(rptInfo);
				window.parent.Design._SelectionModule.set("val" , posiNew);
				currSheet.isPaintSuspended(true);
				currCell.value(posiNew);
				currSheet.isPaintSuspended(false);
			}
			if(typeof window.parent.selCellSettingHandler == "function"){			
				window.parent.selCellSettingHandler("" , "02");
			}
			BIONE.closeDialog("moduleClkDialog");
		}
		
		function initLabel(obj){
			var label = "";
			if(window.parent.Design.getShowType() 
					== window.parent.Design.Constants.SHOW_TYPE_CELLNM){
				var tmp = obj.cellNm() ? obj.cellNm() : obj.cellNo();
				label += ("{" + tmp + "}");
			}else if(window.parent.Design.getShowType() 
					== window.parent.Design.Constants.SHOW_TYPE_BUSINO){
				var tmp = obj.busiNo() ? obj.busiNo() : obj.cellNo();
				label += ("{" + tmp + "}");
			}else{
				label += ("{" + obj.dsName() + "}.{" + obj.columnName() + "}");
				if("Y" == obj.isExt()){
					// 进行扩展
					if("01" == obj.extDirection()){
						// 行扩展
						label += "↓";
					} else {
						label += "→";
					}
				}
			}
			return label;
		}
	});
</script>
</head>
<body>
<div id="template.center">
	<form name="mainform" id="mainform" action="" method="post">
	</form>
</div>
</body>
</html>