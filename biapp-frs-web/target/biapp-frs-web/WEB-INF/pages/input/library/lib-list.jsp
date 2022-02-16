<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template8.jsp">
<%--<script type="text/javascript" src="${ctx}/js/udip/EmployeeData.js"></script>--%>
<script type="text/javascript">
	var grid,btns;
	var libId_date = "${libId}".split(';;');
	$(function() {
		var managers = $("#maingrid").ligerGetGridManager();
		initGrid();
		if (libId_date[1] != 'query' && !"${formType}") {
			btns = [ {
				text : '导出',
				click : excel_export,
				icon : 'export',
				operNo : 'excel_export'
			}];
		}
		BIONE.loadToolbar(grid, btns, function() {
		});
		if (libId_date[1] == 'query') {
			libId_date[1] = '';
		}
	});

	function excel_export(){
		if (libId_date[1] != '') {
			if (!document.getElementById('download')) {
				$('body').append($('<iframe id="download" style="display:none"/>'));
			}
			$("#download").attr('src', '${ctx}/rpt/input/library/excel_down?id=' + libId_date[0] + '&dataDate=' + libId_date[1]);
		} else {
			if (!document.getElementById('download')) {
				$('body').append($('<iframe id="download" style="display:none"/>'));
			}
			$("#download").attr('src', '${ctx}/udip/library/excel_down?id=' + libId_date[0]);
		}
	}
	
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			rownumbersColWidth : 40,
			width : '100%',
			columns : [ {
				display : libId_date[2],
				name : 'text',
				width : "48%",
				align : 'left'
			}, {
				display : libId_date[3],
				name : 'id',
				width : "43%",
				align : 'center'
			} ],
			rownumbers : true,
			url : "${ctx}/rpt/input/library/libListByLibId.json?libId=" + libId_date[0] + "&dataDate=" + libId_date[1] + "&t=" + new Date(),
			dataAction : 'server', //从后台获取数据
			usePager : true,
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			method : 'get',
			sortName : 'id',//第一次默认排序的字段
			checkbox : false,
			toolbar : (libId_date[1] != 'query' || "${formType}")?{}:null  
		});
	}
	
</script>
<title>数据字典</title>
</head>
<body>
</body>
</html>