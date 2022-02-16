<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var dialog;

	$(function() {
		$("#search").ligerForm({
			fields : [ {
				display : "认证类型名称",
				name : "authTypeName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field : "authTypeName",
					op : "like"
				}
			} ]
		});

		grid = $("#maingrid").ligerGrid({
			height : '100%',
			width : '100%',
			columns : [ {
				display : '认证类型名称',
				name : 'authTypeName',
				width : "40%"
			}, {
				display : '实现类',
				name : 'beanName',
				width : "55%"
			}],
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			dataAction : 'server',//从后台获取数据
			url : "${ctx}/bione/admin/authConfig/list.json",
			usePager : true, //服务器分页
			sortName : 'authTypeNo',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			toolbar : {}
		});

		var btns = [ {
			text : '增加',
			click : create,
			icon : 'add',
			operNo : 'user_add'
		}, {
			text : '修改',
			click : edit,
			icon : 'modify',
			operNo : 'user_modify'
		}, {
			text : '删除',
			click : deleteEntity,
			icon : 'delete',
			operNo : 'user_delete'
		}, {
			text : '配置授权资源实现',
			click : addAuthRes,
			icon : 'config',
			operNo : 'user_addAuthRes'
		},{
			text : '配置授权对象实现',
			click : addAuthObj,
			icon : 'config',
			operNo : 'user_addAuthObj'
		} ];

		BIONE.loadToolbar(grid, btns, function() {
		});

		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	//新建
	function create() {
		dialog = BIONE.commonOpenLargeDialog('添加认证类别', 'authTypeWin',
				'${ctx}/bione/admin/authConfig/new');
	}

	//修改
	function edit() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var id = rows[0].authTypeNo;
			dialog = BIONE.commonOpenLargeDialog("修改逻辑系统", "authTypeWin",
					"${ctx}/bione/admin/authConfig/" + id + "/edit");
		} else {
			BIONE.tip("请选择需要修改的记录");
		}
	}

	//批量删除
	function deleteEntity() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var id = rows[0].authTypeNo;
			
			if(id =="01"||id =="02"||id =="03"||id =="04"){
				BIONE.tip("内置类型不可删除！");
				return false;
			}
			
			
			$.ligerDialog.confirm('您确定删除这条记录么？', function(yes) {
				if (yes) {
					$.ajax({
						cache : false,
						async : true,
						url : '${ctx}/bione/admin/authConfig/' + id,
						type : "POST",
						beforeSend : function() {
							BIONE.loading = true;
							BIONE.showLoading("正在加载数据中...");
						},
						complete : function() {
							BIONE.loading = false;
							BIONE.hideLoading();
						},
						success : function(data) {
							if(data=="true"){
								BIONE.tip('删除成功');
								grid.loadData();	
							}else{
								BIONE.tip("内置类型不可删除！");
							}
							
						},
						error : function(result, b) {
							////BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					});
				}
			});
		} else {
			BIONE.tip("请选择需要删除的记录");
		}
	}
	
	//添加授权资源实现
	function addAuthRes(){
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var id = rows[0].authTypeNo;
			dialog = BIONE.commonOpenLargeDialog("添加授权资源实现", "authTypeWin",
					"${ctx}/bione/admin/authConfig/getResInfo/" + id);
		} else {
			BIONE.tip("请选择需要修改的记录");
		}
	}

	//添加授权对象实现
	function addAuthObj(){
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var id = rows[0].authTypeNo;
			dialog = BIONE.commonOpenLargeDialog("添加授权对象实现", "authTypeWin",
					"${ctx}/bione/admin/authConfig/getObjInfo/" + id);
		} else {
			BIONE.tip("请选择需要修改的记录");
		}
	}
</script>
</head>
<body>
</body>
</html>