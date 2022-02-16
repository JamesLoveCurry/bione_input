<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<style type="text/css">
.link {
	color: #065FB9;
	text-decoration: underline;
}
</style>
<script type="text/javascript">
	var grid;
	var url;
	var userName;
	var queryNm;
	var remark;
	var usePager = true;
	
	$(function() {
		usePager = false;
		initGridfrsit();
	});
	function alertRptIndex(instanceId) {
		var modelDialogUri = "${ctx}/report/frame/datashow/idx?storeId="+instanceId+"&mode="+'1';
		window.parent.parent.parent.parent.BIONE.commonOpenDialog("指标查询", "alertRptIndex",window.parent.parent.parent.$("body").width()*0.96 , window.parent.parent.parent.$("body").height()*0.96,
				modelDialogUri);
	}
	
	function initGridfrsit() {
		grid = $("#maingrid")
				.ligerGrid(
						{
							width : "99.8%",
							columns : [
									{
										display : '指标查询名称',
										name : 'queryNm',
										align : 'center',
										width : '50%%',
										render : function(row) {
											return "<a href='javascript:void(0)' class='link' onclick='alertRptIndex(\""
													+ row.instanceId
													+ "\")'>"
													+ row.queryNm + "</a>";
										}
									}, {
										display : '收藏时间',
										name : 'createTime',
										align : 'center',
										width : '45%',
										type : 'date',
										format : 'yyyy-MM-dd hh:mm:ss'
									}],
							usePager : false,
							checkbox : false,
							pageSize : 10,
							dataAction : 'server', //从后台获取数据
							alternatingRow : true, //附加奇偶行效果行
							url : "${ctx}/rpt/frame/rptfav/index/getInfo?instanceType="
								+ '03'+"&folderId="+"${folderId}"+"&type="+"${type}",
							method : 'post', // get
							sortName : 'createTime desc,queryNm', //第一次默认排序的字段
							sortOrder : 'asc', //排序的方式
							rownumbers : false
						});
		grid.setHeight($("#center").height() *0.95);
	}

</script>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>
</body>
</html>