<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid, btns, url;

	//初始化函数
	$(function() {
		url = "${ctx}/bione/admin/user/list.json?isQuery=true";
		initGrid();
		searchForm();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "用户帐号",
				name : "userNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "usr.user_No"
				}
			}, {
				display : "用户名称",
				name : "userName",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "usr.user_Name"
				}
			}, {
				display : "机构名称",
				name : "orgNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "org.org_Name"
				}
			}]
		});
	}

	//初始化grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid(
				{
					width : '100%',
					columns : [
							{
								display : '用户帐号',
								name : 'userNo',
								align : 'left',
								width : '30%',
								sortname : "usr.user_no"
							},
							{
								display : '用户名称',
								name : 'userName',
								align : 'center',
								width : '30%',
								sortname : "usr.user_name"
							},
							{
								display : '机构',
								name : 'orgName',
								align : 'center',
								width : '30%',
								sortname : "org.org_Name"
							}
							/*,
							{
								display : '部门',
								name : 'deptName',
								align : 'center',
								width : '13%',
							},
							{
								display : '用户状态',
								name : 'userSts',
								align : 'center',
								width : '11%',
								sortname : "usr.user_sts",
								render : function(row){
									return renderHandler(row);
								}
							}
							*/
							],
					checkbox : true,
					usePager : true,
					isScroll : false,
					rownumbers : true,
					alternatingRow : true,//附加奇偶行效果行
					colDraggable : true,
					dataAction : 'server',//从后台获取数据
					method : 'post',
					url : url,
					sortName : 'user_No', //第一次默认排序的字段
					sortOrder : 'asc',
					toolbar : {} 
				});
	}

	
	
	//初始化按钮
	function initButtons() {
		btns = [
		/*动态维护功能按钮*/
		{
			text : '选择',
			click : templeSelUser,
			icon : 'fa-plus',
			operNo : 'templeSelUser'
		}];
		BIONE.loadToolbar(grid, btns, function() { });
	}
	
	//选择负责人
	function templeSelUser() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			window.parent.setDutyUser(rows[0].userName,rows[0].orgName);
			BIONE.closeDialog("userView");
		} else if(rows.length > 1){
			BIONE.tip('请选择一条数据');
		}else {
			BIONE.tip('请选择数据');
		}
	}
	
</script>
</head>
<body>
</body>
</html>