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
					display : "类型标识",
					name : "paramTypeNo_name",
					newline : true,
					type : "text",
					width : 120,
					cssClass : "field",
					attr : {
						field : "paramTypeNo",
						op : "like"
					}
				}, {
					display : "类型名称",
					name : "paramTypeName_name",
					newline : false,
					type : "text",
					width : 120,
					cssClass : "field",
					attr : {
						field : "paramTypeName",
						op : "like"
					}
				} ]
			});
		}

		//渲染GRID
		function ligerGrid() {
			grid = $("#maingrid").ligerGrid(
					{
						InWindow : false,
						width : "100%",
						height : "98%",
						InWindow : true,
						columns : [ {
							display : "类型标识",
							name : "paramTypeNo",
							width : "15%"
						}, {
							display : "类型名称",
							name : "paramTypeName",
							width : "20%"
						}, {
							display : "备注",
							name : "remark",
							width : "50%"
						} ],
						checkbox : false,
						userPager : true,
						rownumbers : true,
						alternatingRow : true,//附加奇偶行效果行
						colDraggable : true,
						dataAction : 'server',//从后台获取数据
						method : 'post',
						url : "${ctx}/report/frame/param/templates/sysParams.json?d="
								+ new Date(),
						onDblClickRow : function(rowdata, rowindex,
								rowDomElement) {//双击选择
							window.parent.hiddenIdBox.val(rowdata.paramTypeId);
							window.parent.combSourceBox
									.val(rowdata.paramTypeName);
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