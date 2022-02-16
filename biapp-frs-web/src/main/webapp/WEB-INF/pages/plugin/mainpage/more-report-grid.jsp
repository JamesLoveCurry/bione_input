<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var grid;
	
	$(function() {
		grid = $("#grid").ligerGrid({
			width : '99%',
			columns:[{
				display:'报表名称',
				name:'rptNm',
				width:"40%"
			},{
				display:'访问时间',
				name:'accessTime',
				width:"50%",
				type:"date",
				format:'yyyy-MM-dd hh:mm:ss'
			}],
			rownumbers:true,
			checkbox:false,
			usePager:true,
			isScroll : false,
			alternatingRow:true,
			dataAction:'server',
			url:"${ctx}/rpt/frame/mainpage/getReportList?rptId=${rptId}&d="+new Date().getTime(),
			type:"post",
			sortName:'h.accessTime',
			sortOrder:'desc'
		});
		
		grid.setHeight($("#center").height()-5);
		
		var btns = [ {
			text : "关闭",
			onclick : function() {
				BIONE.closeDialog("rptList");
			}
		} ];
		
		BIONE.addFormButtons(btns);
		BIONE.loadToolbar(grid,null,null);
	});
	
	
</script>
<html>
</head>
<body>
	<div id="template.center">
		<div id="grid"></div>
	</div>
</body>
</html>