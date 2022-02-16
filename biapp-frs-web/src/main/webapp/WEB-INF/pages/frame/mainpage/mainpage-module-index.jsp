
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
			labelWidth: 100,
			fields : [ {
				display : "模块名称",
				name : "moduleName",
				newline : true,
				type : "text",
				attr : {
					field : "moduleName",
					op : "like"
				}
			}]
		});

		grid = $("#maingrid").ligerGrid({
			height : '99%',
			width : '100%',
			align : 'center',
			columns : [ {
				display : '模块编号',
				name : 'moduleId',
				width : '30%',
				minWidth : 100
			}, {
				display : '模块名称',
				name : 'moduleName',
				width : '60%',
				minWidth : 100
			} ],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			url : "${ctx}/bione/mainpage/module/list.json",
			usePager : true, //服务器分页
			sortName : 'moduleId',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			toolbar : {}
		});

		var btns = [ {
			text : '增加',
			click : addModule,
			icon : 'fa-plus'
		}, {
			text : '修改',
			click : editModule,
			icon : 'fa-pencil-square-o'
		}, {
			text : '删除',
			click : deleteModule,
			icon : 'fa-trash-o'
		} ];

		BIONE.loadToolbar(grid, btns, function() {
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	//新建
	function addModule() {
		dialog = BIONE.commonOpenSmallDialog('添加首页模块', 'moduleWin',
				'${ctx}/bione/mainpage/module/edit');
	}

	//修改
	function editModule() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var id = rows[0].moduleId;
			dialog = BIONE.commonOpenSmallDialog("修改首页模块", "moduleWin",
					"${ctx}/bione/mainpage/module/edit?moduleId="+id);
		} else {
			BIONE.tip("请选择一条需要修改的记录");
		}
	}

	//删除基础角色
	function deleteModule() {
		var rows = grid.getSelectedRows();
		if (rows.length > 0) {
			var ids=""
			for(var i in rows){
				ids+=rows[i].moduleId+",";
			}
			ids = ids.substring(0,ids.length-1);
			$.ligerDialog.confirm(
					'是否确认删除这些首页功能模块？',
					function(yes) {
						if (yes) {
							$.ajax({
								cache : false,
								async : true,
								url : '${ctx}/bione/mainpage/module/deModule?moduelIds=' + ids,
								type : "POST",
								beforeSend : function() {
									BIONE.loading = true;
									BIONE.showLoading("正在加载数据中...");
								},
								complete : function() {
									BIONE.loading = false;
									BIONE.hideLoading();
								},
								success : function(result) {
									if(result.moduleNms == null || result.moduleNms.length<=0){
										BIONE.tip('删除成功');
									}
									else{
										var tip = "首页模块（"
										for(var i in result.moduleNms){
											tip+= result.moduleNms[i]+",";
										}
										tip = tip.substring(0,tip.length-1);
										tip +="）正在配置首页无法删除！";
										BIONE.tip(tip);
									}
									grid.loadData();
								},
								error : function(result, b) {
									BIONE
											.tip('发现系统错误 <BR>错误码：'
													+ result.status);
								}
							});
						}
					});
		} else {
			BIONE.tip("请选择一条需要删除的记录");
		}
	}
</script>
</head>
<body>
</body>
</html>