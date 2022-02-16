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
		var url = "${ctx}/rpt/frame/businesslib/tables.json?dsId=${dsId}";//URL
		ligerSearchForm();//初始化查询表单
		ligerGrid();//初始化GRID
		BIONE.addSearchButtons("#search", grid, "#searchbtn");//生成搜索按钮

		//渲染查询表单
		function ligerSearchForm() {
			$("#search").ligerForm({
				fields : [ {
					display : "数据集ID",
					name : "tableName_box",
					newline : true,
					type : "text",
					labelWidth : 65,
					cssClass : "field",
					attr : {
						field : "setId",
						op : "like"
					}
				}, {
					display : "数据集名",
					name : "tableComment_box",
					newline : false,
					type : "text",
					labelWidth : 65,
					cssClass : "field",
					attr : {
						field : "setNm",
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
							display : "数据集ID",
							name : "setId",
							width : "45%",
							isSort : false
						}, {
							display : "数据集名称",
							name : "setNm",
							width : "45%",
							isSort : false
						} ],
						checkbox : false,
						userPager : true,
						rownumbers : true,
						alternatingRow : true,//附加奇偶行效果行
						colDraggable : true,
						dataAction : 'server',//从后台获取数据
						method : 'post',
						url : url
					});
		}
	});
	
	function validate(tableName){
		var res = false;
		$.ajax({
			async : false,
			type : "POST",
			dataType :"text",
			url : "${ctx}/rpt/frame/businessline/checkTableValid.json?",
			data : {
				dsId : "${dsId}",
				tableName : tableName,
				type : "rpt"
			},success : function(result) {
				res = result;
			}
		});
		
		if(res != ""){
			BIONE.tip("当前选择表缺少"+res+"字段");
			return false;
		}else{
			return true;
		}
	}
	
	function addToParent() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var tn = rows[0].tableEnNm
			if(validate(tn)){
				return rows[0];
			}
		} else {
			BIONE.tip('请选择一条记录');
		}
		return "";
	}
	
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>