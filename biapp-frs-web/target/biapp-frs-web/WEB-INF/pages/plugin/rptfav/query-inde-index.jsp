<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1.jsp">
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
	var folderId="${folderId}";
	
	$(function() {			
		initGrid();
		$("#search").ligerForm({
			fields : [ {
				display : "查询名称",
				name : "queryNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "queryNm",
					op : "like"
				}
			} ]
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	
	function openIndexQuery(instanceId) {
		var modelDialogUri = "${ctx}/report/frame/datashow/idx/show?instanceId="+instanceId+"&mode="+'1';
		window.parent.parent.parent.parent.BIONE.commonOpenDialog("指标查询", "alertRptIndex",window.parent.parent.parent.$("body").width()*0.96 , window.parent.parent.parent.$("body").height()*0.96,
				modelDialogUri);
	}

	function initGrid() {
		grid = $("#maingrid")
				.ligerGrid(
						{
							width : "99.8%",
							columns : [
									{
										display : '查询名称',
										name : 'queryNm',
										align : 'center',
										width : '35%',
										render : function(row) {
											return "<a href='javascript:void(0)' class='link' onclick='openIndexQuery(\""
													+ row.instanceId
													+ "\")'>"
													+ row.queryNm + "</a>";
										}
									},{
										display : '收藏时间',
										name : 'createTime',
										align : 'center',
										width : '25%',
										type : 'date',
										format : 'yyyy-MM-dd hh:mm:ss'
									},{
										display : '操作',
										name : 'remark',
										align : 'center',
										width : '30%',
										render:function(a){
											return "<a href='javascript:void(0)' class='link' onclick='deletes(\""+ a.instanceId+ "\")'>"+ '删除' + "</a>"+" "
											+ "<a href='javascript:void(0)' class='link' onclick='update(\""+ a.instanceId+"\",\""+a.userName+"\",\""+a.createTime+"\",\""+a.queryNm+"\",\""+a.remark+"\")'>"+ '修改' + "</a>";
										}
									}],
							usePager : usePager,
							checkbox : false,
							dataAction : 'server', //从后台获取数据
							alternatingRow : true, //附加奇偶行效果行
							url : "${ctx}/rpt/frame/rptfav/index/getInfo?instanceType="
								+ '03'+"&folderId="+"${folderId}"+"&type="+"${type}",
							method : 'post', // get
							sortName : 'queryNm', //第一次默认排序的字段
							sortOrder : 'asc', //排序的方式
							rownumbers : true
						});
		grid.setHeight($("#center").height() - 10);
	}
	
	function deletes(a){
		var delurl = "${ctx}/rpt/frame/rptfav/index/del?instanceId="+a;
		$.ajax({
			type : "GET",
			url : delurl,
			success:function(ming){
				if(ming == null || ming.length == 0){
					BIONE.tip("删除成功!");
					initGrid();
					window.parent.refreshTree();
				}else{
					BIONE.tip("删除失败!");
				}
			},
			error : function(XMLHttpRequest,
					textStatus, errorThrown) {
				BIONE.tip('删除失败,错误信息:'
						+ textStatus);
			}
		});
	}
	
	function update(a,b,c,d,e){
		userName = b;
		queryNm = d;
		remark = e;
		var modelDialogUri = "${ctx}/rpt/frame/index/update/updat?instanceId="+a+"&createTime="+c;
		window.BIONE.commonOpenSmallDialog("修改", "addCatalog", modelDialogUri); 
	}

	function reloadGrid() {
		grid.set('url', "${ctx}/rpt/frame/rptfav/index/getInfo?folderId="
				+ window.parent.currentNode.id + "&instanceType=" + "03");
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>
</body>
</html>