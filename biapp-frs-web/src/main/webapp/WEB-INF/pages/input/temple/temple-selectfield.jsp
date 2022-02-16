<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var leftTreeObj = null;
	var grid;
	var templateId = '${templateId}';
	var dataType = '${dataType}';

	$(function() {
		initGrid();
	});
	function initGrid() {
		var  columnArr  =[{
 				hide : 1, width : "1%", name : 'tableId'
 			}, {
 				display : '字段名称', width : "15%", name : 'fieldEnName', type : 'text'
 			}, {
 				display : '中文名', width : "20%", name : 'fieldCnName', type : 'text'
 			}, {
 				display : '字段类型', width : "12%", name : 'fieldType', type : 'text',
 				render : function(rowData) {
 					if (rowData.fieldType == "1")
 						return "字符串";
 					if (rowData.fieldType == "2")
 						return "整形";
 					if (rowData.fieldType == "3")
 						return "浮点型";
 					return rowData.fieldType;
 				}
 			}, {
 				display : '字段长度', width : "15%", name : 'fieldLength', type : 'text',
 				render : function(rowData,b,c) {
 					if(c!=""&&c!=null)
 						return c;
 				}
 			}, {
 				display : '字段精度', width : "15%", name : 'decimalLength', type : 'text',
 				render : function(rowData,b,c) {
 					if(c!=""&&c!=null)
 						return c;
 				}
 			}, {
 				display : '可否为空', width : "9%", name : 'allowNull', type : 'text'
 			}];
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : false,
			columns : columnArr,
			dataAction : 'server', //从后台获取数据
			usePager : false, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/rpt/input/temple/getFiledList?templateId="+templateId+"&dataType="+dataType,
			sortName : 'fieldEnName',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
            delayLoad:true,
            onDblClickRow : dbclick,
			width : '100%',
			height :　300
		});
		grid.loadData();
	}
	
	function dbclick(rowData,rowNum,rowValue){
		BIONE.closeDialog("selectFiledBox", null, true, {
			fieldEnName : rowData.fieldEnName,
			fieldType : rowData.fieldType,
			fieldLength : rowData.fieldLength,
			decimalLength : rowData.decimalLength
		});
	}
	
</script>
<title>指标信息</title>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; height: 100%">
			<div id="maingrid" class="maingrid"></div>
		</div>
	</div>
</body>
</html>