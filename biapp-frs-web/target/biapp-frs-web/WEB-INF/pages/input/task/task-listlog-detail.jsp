<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style>
.indexStsA,.indexNmA{
     width:55%;
     cursor:pointer;
}
.stop{
    color:red;
}
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">


	var logId = '${logId}';
	var operGrid;
	var height = 160;
	
	$(function() {	
		initGrid();
	});
	function initGrid() {
			var columnArr = [
					{
						display : '操作类型',
						name : 'operType',
						width : "15%",
						align : 'center',
						render : function(row,rowNum,rowValue){
							if(rowValue=="add")
								return "添加一行";
							else if(rowValue=="modify")
								return "修改一行";
							else if(rowValue == "delete")
								return "删除一行";
							
						}
					}, {
						display : '操作时间',
						name : 'showDate',
						width : "20%",
						align : 'center'
					}, {
						display : '操作内容',
						name : 'operContent',
						width : "60%",
						align : 'center'
					}];

			grid = $("#maingrid").ligerGrid({
				toolbar : {},
				checkbox : false,
				columns : columnArr,
				title : "任务处理日志",
				dataAction : 'server', //从后台获取数据
				usePager : false, //服务器分页
				alternatingRow : true, //附加奇偶行效果行
				colDraggable : true,
				url : "${ctx}/rpt/input/taskoper/getListLogDetail.json?logId="+logId,
				sortName : 'operDate',//第一次默认排序的字段
				sortOrder : 'desc', //排序的方式
	            delayLoad:true,
				pageParmName : 'page',
				pagesizeParmName : 'pagesize',
				rownumbers : true,
				width : '100%'
			});
		   grid .loadData();	
	}
	function calTime(time){
		time=parseInt(time);
		time = parseInt(time/1000);
		var second=time%60;
		time=parseInt(time/60);
		var minute=time%60;
		time=parseInt(time/60);
		var hour=time%24;
		var day=parseInt(time/24);
		return day+"天"+hour+"时"+minute+"分"+second+"秒";
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>
</body>
</html>