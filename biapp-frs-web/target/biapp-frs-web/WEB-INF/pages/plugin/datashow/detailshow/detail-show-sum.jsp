<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<style>

#tree {
	background-color: #F1F1F1;
}
</style>
<script type="text/javascript">
	var colInfos = window.parent.colInfos;
	var colSums = window.parent.colSums;
	var grid = null;
	var isSum = false;
	function templateShow(){
		var $content = $(document);
		var height = $content.height() - 50;
	}
	var data = [];
	$(function() {
		templateShow();
		initData();
		initButtons();
	});

	function initData(){
		if(colInfos != null && colInfos != undefined){
			var rows = [];
			for(var i in colInfos){
				var row ={};
				row.id ={
					cfgId : i
				};
				row.displayNm = colInfos[i].displayNm;
				row.colType = colInfos[i].dataType == "02" ? "02" : "01";
				if(row.colType == "02"){
					row.sumMode = "01";
				}
				rows.push(row);
			}
			data = {
				Rows : rows,
				Total : colInfos.length
			}
		}
		if(colSums.length>0){
			$("#grid").show();
			$("#level1")[0].checked = true;
			$("#level2")[0].checked = false;
			for(var i in colSums){
				for(var  j in rows){
					if(rows[j].id.cfgId == i){
						rows[j].colType =  colSums[i].colType;
						rows[j].sumMode =  colSums[i].sumMode;
					}
				}
			}
			data = {
					Rows : rows,
					Total : colInfos.length
				}
			initGrid();
		}
		else{
			$("#grid").hide();
		}
		
	}
	function check() {
		if ($("#level1")[0].checked == true){
			isSum = true;
			$("#grid").show();
			if(grid == null){
				initGrid();
			}
			
		}
		else{
			isSum = false;
			$("#grid").hide();
		}
			
	}

	function initGrid(){
		grid = $('#maingrid').ligerGrid({
			width : '99%',
			columns : [{
				display : "名称",
				name :  "displayNm",
				width: "35%",
				align: "left"
			},{
				display : "字段类型",
				name :  "colType",
				width: "30%",
				align: "left",
				render : function(a,b,c){
					if(c == "01")
						return "汇总字段";
					if(c == "02")
						return "求值字段";
				},
				editor : {
					type : 'select',
					data : [ {
						'id' : '01',
						'text' : '汇总字段'
					}, {
						'id' : '02',
						'text' : '求值字段'
					}]
				},
				ext: function(rowdata){
    				return {
    					cancelable: false
    				}
				}
				
			},{
				display : "汇总方式",
				name :  "sumMode",
				width: "30%",
				align: "center",
				render : function(a,b,c){
					if(c == "01")
						return "求和";
					if(c == "02")
						return "平均";
					if(c == "03")
						return "最大";
					if(c == "04")
						return "最小";
				},
				editor : {
					type : 'select',
					data : [ {
						'id' : '01',
						'text' : '求和'
					}, {
						'id' : '02',
						'text' : '平均'
					}, {
						'id' : '03',
						'text' : '最大'
					}, {
						'id' : '04',
						'text' : '最小'
					}],
					ext: function(rowdata){
	    				return {
	    					cancelable: false
	    				}
					}
				}
			}],
			enabledEdit : true,
			usePager: false,
			checkbox: false,
			dataAction : 'local',
			allowHideColumn: false,
			onBeforeEdit: function(e){
				if(e.column.columnname=="sumMode"){
					if(e.record.colType != "02"){
						return false;
					}
				}
			},
			onBeforeSubmitEdit : function(e) {
				if(e.column.columnname=="colType"){
					if(e.value == "01"){
						e.record.sumMode = "";
					}
					else if(e.value == "02"){
						e.record.sumMode = "01";
					}
					else{
						BIONE.tip("必须选择一个字段类型");
						return false;
					}
				}
				if(e.column.columnname=="sumMode"){
					if(e.value == ""){
						BIONE.tip("必须选择一个汇总方式");
						return false;
					}
				}
				
			},
			sortName : "",
			data : data
		});
		grid.setHeight($("#center").height()-$("#radio").height()-5);
	}
	
	
 	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function(){
				BIONE.closeDialog("sumEdit");
			}
		});
		buttons.push({
			text : '确认',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
 	
 	function f_save(){
 		var sumInfo = [];
 		if ($("#level1")[0].checked == true){
 			sumInfo = grid.getData();
 		}
 		window.parent.colSums = sumInfo;
	 	BIONE.closeDialog("sumEdit");
 		
 	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="radio" style="height:20px;">
			<div style="width: 100%; margin: auto;">
				<div style="text-align: center;">
					是否汇总： 是 <input type="radio" id="level1" name="level" value="level1"
						onclick=check() /> 否 <input type="radio" id="level2" name="level"
						value="level2" onclick=check() checked="true" />
				</div>
			</div>
		</div>
		<div id="grid">
			<div id="maingrid" style=""></div>
		</div>
	</div>
</body>
</html>