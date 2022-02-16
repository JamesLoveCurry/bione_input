<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
	var grid;
	var cfgId = '${cfgId}';
	var tableId = '${tableId}';
	
	$(function() {
		window.parent.dataObj = window;
		initGrid();
	})
	var colInfo;
	function initGrid() {
		$.ajax({
			url : '${ctx}/rpt/input/table/getTableColumnInfo',
			dataType : 'json',
			data : {
				tableId : tableId
			},
			success : function(data) {
				if(data){
					var columns = [];
					colInfo = [];
					$.each(data, function(i, n) {
						var showText = n.fieldCnName&&n.fieldCnName!=""?n.fieldCnName:n.fieldEnName;
						var type = "text";
						var format = "";
						var fieldLength =n.fieldLength;
						if(n.fieldType=="DATE"||n.fieldType=="TIMESTAMP")
						{
							type="date";
							if(n.fieldType=="DATE")
								format= "yyyy-MM-dd";
							else if(n.fieldType="TIMESTAMP")
								format = "yyyy-MM-dd hh:mm:ss"
						}
						else if(n.fieldType=="NUMERIC"||n.fieldType=="DECIMAL")
							type="number";
						var fieldEnName = n.fieldEnName;
						var oneColumn ={
							display : showText,
							name : fieldEnName,
							type : type,
							width : 150,
							align : 'left',
							editor : {
								type : type,
								maxlength : 4
							}
						};
						if( format != "")
						{
							oneColumn.format=format;
							oneColumn.render= function(rowdata,rownum,value){
								if(value&&value!=null)
								{
									var tmpDate = BIONE.getFormatDate(value, format);
									if(tmpDate!=null)
									{
										rowdata[fieldEnName] = BIONE.getFormatDate(value, format);
										return rowdata[fieldEnName];
									}else return rowdata[fieldEnName];
								}
							}
						}
						if(fieldLength&&fieldLength!="0"){
							oneColumn.render= function(data){
								if(data[fieldEnName]){
									var length = data[fieldEnName].length;
									if(length>fieldLength){
										BIONE.tip("字段["+fieldEnName+"]的长度不能超过"+fieldLength);
										return data[fieldEnName].substring(0, fieldLength);
									}
								}
								return data[fieldEnName];
							}
						}
						columns.push(oneColumn);
						colInfo.push(n.fieldEnName);
					});
					if(cfgId&&cfgId!=null&&cfgId!="")
						initData(columns);
					else 
						initEmptyGrid(columns);
					
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function initData(columns){
		$.ajax({
			url : '${ctx}/rpt/input/table/getDataInfo?cfgId='+cfgId,
			dataType : 'json',
			success : function(value) {
				var datas = { Rows: [] };
				datas . Rows = value;
				grid = $("#maingrid").ligerGrid({
					toolbar : {},
					columns : columns,
					data : datas,
					checkbox : true,
					usePager : false, //服务器分页
					alternatingRow : true, //附加奇偶行效果行
					colDraggable : true,
					enabledEdit : true,
					isScroll : true,
					resizable : true,
					height : '99%',
					width : '99%',
					sortName : 'taskNm',//第一次默认排序的字段
					sortOrder : 'asc', //排序的方式
					rownumbers : true
				});

				initToolbar();
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function initEmptyGrid(columns){

		
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			usePager : false, //服务器分页
			columns : columns,
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			enabledEdit : true,
			isScroll : true,
			resizable : true,
			height : '99%',
			width : '99%',
			sortName : 'taskNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			rownumbers : true
		});
		initToolbar();
	}
	
	function initToolbar(){
			var toolBars = [ {
				text : '增行',
				click : f_addRow,
				icon : 'add'
			}, {
				text : '删行',
				click : f_deleteRow,
				icon : 'delete'
			}];
			BIONE.loadToolbar(grid, toolBars, function() {
			});
	}
	
	function f_addRow(){
		grid.addRow();
	}
	
	function f_deleteRow(){
		var rows = grid.getSelectedRows();
		if(rows.length){
			for(var i=0;i<rows.length;i++){				
				grid.deleteRow(rows[i]);	
			}
		}
		grid.endEdit();
	}
	
	function getEmptyColumn(){
		var emptyCol={};
		for(var i = 0 ;i <colInfo.length;i++){
			emptyCol[colInfo[i]]="";
		}
		return emptyCol;
	}
	
	function getData(){
		grid.endEdit();
		return JSON2.stringify(grid.rows);
	}
</script>
</head>
<body>
	<div id='template.center' >
		<div id="maingrid"  class="maingrid"></div>
	</div>
</body>
</html>