<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">
	//全局变量
	var grid,searchForm;
	var rptId = "${rptId}";
	var queryCond = "${queryCond}";
	var curCellNo = "${curCellNo}";
	$(function() {
		//渲染grid
		ligerGrid();
		//加载搜索域
		initSearchForm();
	});
	
	function initSearchForm() {
		//搜索表单应用ligerui样式
		searchForm = $("#search").ligerForm({
			fields :[ {
				name : "cellNo",
				display : "单元格编号",
				type : "text",
				attr : {
					field : 'his.cellNo',
					op : "="
				}
			}]
		});
		searchForm.setData({cellNo : curCellNo});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	};
	
	function ligerGrid() {
        grid = $("#maingrid").ligerGrid({
 			checkbox : false,
 			columns : [{
 				display : '报表名称',
 				name : 'taskInstanceId',
 				width : "20%",
 				align : 'center'
 			},{
 				display : '对象类型',
 				name : 'objType',
 				width : "11%",
 				align : 'center',
 				render : function(a,b,c){
 					if(c == "01"){
 						return "明细类报表";
 					}else if(c == "02"){
 						return "单元格类报表";
 					}else if(c == "03"){
 						return c;
 					}else if(c == "04"){
 						return "指标列表（纵）";
 					}else if(c == "05"){
 						return "指标列表（横）";
 					}else{
 						return c;
 					}
 				}
 			},  {
 				display : '单元格',
 				name : 'cellNo',
 				width : "5%",
 				align : 'center'
 			}, {
 				display : '修改前值',
 				name : 'oldObjVal',
 				width : "16%",
 				align : 'center'
 			}, {
 				display : '修改后值',
 				name : 'newObjVal',
 				width : "16%",
 				align : 'center'
 			}, {
 				display : '更新人',
 				name : 'uptUser',
 				width : "8%",
 				align : 'center'
 			}, {
 				display : '更新时间',
 				name : 'uptTime',
 				width : "21%",
 				align : 'center',
 				type : 'date',
 				format : "yyyy-MM-dd hh:mm:ss"
 			}],
 			dataAction : 'server', //从后台获取数据
 			usePager : true, //服务器分页
 			alternatingRow : true, //附加奇偶行效果行
 			colDraggable : true,
 			url : "${ctx}/rpt/frame/rptsubmit/getLogList.json?rptId="+rptId+"&queryCond="+queryCond+"&curCellNo="+curCellNo,
 			rownumbers : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			height : '99%'
		});
	}
</script>
<title></title>
</head>
<body>
	<div id="template.center">
		<div id="maingrid"></div>
	</div>
</body>
</html>