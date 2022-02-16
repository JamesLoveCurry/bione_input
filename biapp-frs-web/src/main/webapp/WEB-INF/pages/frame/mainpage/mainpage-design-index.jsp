
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
				display : "首页名称",
				name : "designNm",
				newline : true,
				type : "text",
				attr : {
					field : "designNm",
					op : "like"
				}
			}]
		});

		grid = $("#maingrid").ligerGrid({
			height : '99%',
			width : '100%',
			align : 'center',
			columns : [ {
				display : '首页名称',
				name : 'designNm',
				width : '30%',
				minWidth : 100
			}, {
				display : '备注',
				name : 'remark',
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
			url : "${ctx}/bione/mainpage/design/list.json",
			usePager : true, //服务器分页
			sortName : 'designNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			toolbar : {}
		});

		var btns = [ {
			text : '增加',
			click : addDesign,
			icon : 'add'
		}, {
			text : '修改',
			click : editDesign,
			icon : 'xg-01'
		}, {
			text : '配置',
			click : configDesign,
			icon : 'config'
		}, {
			text : '删除',
			click : deleteDesign,
			icon : 'delete'
		} ];

		BIONE.loadToolbar(grid, btns, function() {
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	//新建
	function addDesign() {
		dialog = BIONE.commonOpenSmallDialog('添加公共首页', 'designWin',
				'${ctx}/bione/mainpage/design/edit');
	}

	//修改
	function editDesign() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var id = rows[0].designId;
			dialog = BIONE.commonOpenSmallDialog("修改公共首页", "designWin",
					"${ctx}/bione/mainpage/design/edit?designId="+id);
		} else {
			BIONE.tip("请选择一条需要修改的记录");
		}
	}
	
	function configDesign(){
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			var id = rows[0].designId;
			dialog = BIONE.commonOpenFullDialog("配置公共首页", "designWin",
					"${ctx}/bione/mainpage/design/config?designId="+id);
		} else {
			BIONE.tip("请选择一条需要配置的首页");
		}
	}
	
	//删除
	function deleteDesign() {
		var rows = grid.getSelectedRows();
		if (rows.length > 0) {
			var ids=""
			for(var i in rows){
				ids+=rows[i].designId+",";
			}
			ids = ids.substring(0,ids.length-1);
			$.ligerDialog.confirm(
					'是否确认删除这些公共首页？',
					function(yes) {
						if (yes) {
							$.ajax({
								cache : false,
								async : true,
								url : '${ctx}/bione/mainpage/design/deDesign?designIds=' + ids,
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
									if(result.designNms == null || result.designNms.length<=0){
										BIONE.tip('删除成功');
									}
									else{
										var tip = "公共首页（"
										for(var i in result.designNms){
											tip+= result.designNms[i]+",";
										}
										tip = tip.substring(0,tip.length-1);
										tip +="）正在配置用户无法删除！";
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