<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">
	//全局变量
	var grid;
	var dialog;

	$(function() {

		//初始化
		ligerSearchForm();//初始化查询表单
		ligerGrid();//初始化GRID
		BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮

		//渲染查询表单
		function ligerSearchForm() {
			$("#search").ligerForm({
				fields : [ {
					display : "数据集名称",
					name : "paramTypeNo_name",
					newline : true,
					type : "text",
					cssClass : "field",
					attr : {
						field : "datasetName",
						op : "like"
					}
				} ]
			});
		}

		//渲染GRID
		function ligerGrid() {
			grid = $("#maingrid").ligerGrid({
				InWindow : false,
				width : "100%",
				height : "98%",
				InWindow : true,
				columns : [ {
					display : "数据集名称",
					name : "datasetName",
					width : "25%"
				}, {
					display : "备注",
					name : "remark",
					width : "62%"
				} ],
				checkbox : false,
				userPager : true,
				rownumbers : true,
				alternatingRow : true,//附加奇偶行效果行
				colDraggable : true,
				dataAction : 'server',//从后台获取数据
				method : 'post',
				url : "${ctx}/report/frame/param/templates/datasets.json?d=" + new Date().getTime(),
				onDblClickRow : function(rowdata, rowindex, rowDomElement) {//双击选择
					window.parent.hiddenIdBox.val(rowdata.datasetId);
					window.parent.treeSourceBox.val(rowdata.datasetName);
					window.BIONE.closeDialog("selectBox");
				}
			});
		}
	});
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>