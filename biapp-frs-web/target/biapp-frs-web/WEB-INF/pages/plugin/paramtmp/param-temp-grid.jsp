<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
<script type="text/javascript">
	//全局变量
	var grid;
	var dialog;
	var catalogId = "";
	var catalogName = "";
	
	$(function() {	
		//初始化
		ligerSearchForm();
		ligerGrid();
		initButtons();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
		//渲染查询表单
		function ligerSearchForm() {
			$("#search").ligerForm({
				fields : [ {
					display : "模板名称",
					name : "paramtmpName_box",
					newline : false,
					type : "text",
					cssClass : "field",
					attr : {
						field : "paramtmpNm",
						op : "like"
					}
				}, {
					name : "catalogId_box",
					newline : false,
					type : "hidden",
					cssClass : "field",
					attr : {
						field : "catalogId",
						op : "="
					}
				} ]
			});
		}

		//渲染GRID
		function ligerGrid() {
			parent.tempGrid = grid = $("#maingrid").ligerGrid({
				width : "100%",
				height : "99%",
				inWindow : true,
				columns : [ {
					display : "模板名称",
					name : "paramtmpNm",
					width : "42%"
				},{
					display : '模板类型',
					name : "templateType",
					width : "25%",
					render : function(value){
						if(value.templateType=="custom")
							return "自定义模板";
						if(value.templateType=="static")
							return "固定模板";
					}
				}, {
					display : "备注",
					name : "remark",
					width : "25%"
				} ],
				checkbox : true,
				userPager : true,
				rownumbers : true,
				alternatingRow : true,//附加奇偶行效果行
				colDraggable : false,
				dataAction : 'server',//从后台获取数据
				method : 'post',
				url : "${ctx}/report/frame/param/templates/list.json",
				sortName : 'paramtmpNm', //第一次默认排序的字段
				sortOrder : 'asc',
				toolbar : {},
				parms : {
					catalogId : window.parent.currentNode.realId
					
				}
			});
			//设置参数
			 if (window.parent.currentNode) {
				catalogId = window.parent.currentNode.realId;
				catalogName = window.parent.currentNode.text;
			}
			/* grid.set('parms', {
				catalogId : catalogId,
				catalogName : catalogName,
				d : new Date().getTime()
			}); */
			$("input[name='catalogId_box']").val(catalogId);
			/* grid.loadData(); */
			parent.catalogBox = $("input[name='catalogId_box']");
			 
		}

		//初始化按钮
		function initButtons() {
			var btns = [ {
				text : "添加",
				icon : "add",
				click : temp_add,
				operNo : "temp_add"
			}, {
				text : "修改",
				icon : "modify",
				click : temp_modify,
				operNo : "temp_modify"
			}, {
				text : "预览",
				icon : "process",
				click : temp_view,
				operNo : "temp_view"
			}, {
				text : "删除",
				icon : "delete",
				click : temp_delete,
				operNo : "temp_delete"
			} ];

			BIONE.loadToolbar(grid, btns, function() {
			});
		}

		//新增
		function temp_add(type) {
			if (window.parent.currentNode) {
				dialog = window.parent.BIONE.commonOpenDialog("参数模板",
						"paramtmpBox", $(window.parent.window).width()-100, $(window.parent.window).height()-100,
						"${ctx}/report/frame/param/templates/infoPage?type=edit&catalogId="
								+ window.parent.currentNode.realId , null);
			} else {
				BIONE.tip("请选择一个目录。");
			}
		}

		//修改
		function temp_modify() {
			if (window.parent.currentNode) {
				var rows = grid.getSelectedRows();
				if (rows.length == 1) {
					dialog = window.parent.BIONE.commonOpenDialog("参数模板",
							"paramtmpBox", $(window.parent.window).width()-100, $(window.parent.window).height()-100,
							"${ctx}/report/frame/param/templates/infoPage?type=view&paramtmpId="
									+ rows[0].paramtmpId + "&catalogId="
									+ window.parent.currentNode.realId);
				} else {
					BIONE.tip("请选择一条记录");
				}
			} else {
				BIONE.tip("请选择一个目录。");
			}
		}

		//预览
		function temp_view() {
			if (window.parent.currentNode) {
				var rows = grid.getSelectedRows();
				if (rows.length == 1) {
					if(rows[0].templateType=="static"){
						dialog = window.parent.BIONE.commonOpenLargeDialog('',
								'preview',
								'${ctx}'+rows[0].templateUrl
										);
					}else{
						dialog = window.parent.BIONE.commonOpenLargeDialog('',
							'preview',
							'${ctx}/report/frame/param/templates/view/'
									+ rows[0].paramtmpId);
					}
				} else {
					BIONE.tip("请选择一条记录");
				}
			} else {
				BIONE.tip("请选择一个目录。");
			}
		}

		//删除
		function temp_delete() {
			var rows = grid.getSelectedRows();
			if (rows.length > 0) {
				var ids = "";
				for ( var i = 0, l = rows.length; i < l; i++) {
					ids += rows[i].paramtmpId + ",";
				}
				ids = ids.substring(0, ids.length - 1);
				$.ajax({
					type : "get",
					url : "${ctx}/report/frame/param/templates/relRpt?ids=" + ids,//查找选定参数模版是否关联报表
					dataType : "json",
					success : function(result) {
									if (result != "0") {//选定参数模版关联报表
										parent.$.ligerDialog.confirm('共有'+ result + '张报表关联选定的参数模板，<br/>确定重置关联报表的参数模版，<br/>并删除选定参数模版吗？', function(
												flag) {
											if (flag) {
												$.ajax({
													type : "get",
													url : "${ctx}/report/frame/param/templates/updateParamtmpIds?ids=" + ids,//重置关联的报表的参数模版项
													dataType : "json",
													success : function() {
														$.ajax({
															type : "POST",
															url : "${ctx}/report/frame/param/templates/"
																	+ ids,
															dataType : "json",
															success : function(res) {
																BIONE.tip("删除成功！");
																grid.loadData();
															},
															error : function(XMLHttpRequest,
																	textStatus, errorThrown) {
																BIONE.tip('删除失败,错误信息:' + textStatus);
															} 
														});
													},
													error : function(XMLHttpRequest,
															textStatus, errorThrown) {
														BIONE.tip('操作失败,错误信息:' + textStatus);
													} 
												});

											}
										});
									} else {//选定参数模版未关联报表
										parent.$.ligerDialog.confirm('确定要删除选定的参数模板吗', function(
												flag) {
											if (flag) {
 												$.ajax({
													type : "POST",
													url : "${ctx}/report/frame/param/templates/"
															+ ids,
													dataType : "json",
													success : function(res) {
														BIONE.tip("删除成功！");
														grid.loadData();
													},
													error : function(XMLHttpRequest,
															textStatus, errorThrown) {
														BIONE.tip('删除失败,错误信息:' + textStatus);
													}
												}); 
											}
										});
									}
								}
							});
		} else {
			BIONE.tip('请选择要删除的记录。');
		}
	}
</script>
<title>Insert title here</title>
</head>
<body>
</body>
</html>