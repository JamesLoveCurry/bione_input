<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var dialogWidth = 1000;
	var dialogHeight = 500;
	var rptId = '${rptId}';
	var versionId = '${versionId}';
	$(function() {
		var parent = window.parent;
		// 初始化dialog高、宽
		dialogWidth = $(parent.window).width();
		dialogHeight = $(parent.window).height();
		//初始化
		ligerSearchForm();
		
		ligerGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		initButtons();
		
	});
	//渲染查询表单
	function ligerSearchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "表样路径",
				name : "templatepath",
				newline : true,
				type : "text",
				attr : {
					field : "templatepath",
					op : "like"
				}
			}]
		});
	};
	//渲染GRID
	function ligerGrid() {
		grid = $("#maingrid").ligerGrid({
			width : "100%",
			height : "84%",
			columns : [
				{
					display : "表样路径",
					name : "templatepath",
					width : "60%",
					align : "left"
				}, {
					display : '最后上传时间',
					name : 'modifytime',
					width : '30%',
					align : 'center',
				}, {
					display : 'id',
					name : 'id',
					width : '1%',
					align : 'center',
					hide:true
				} 
			],
			width : "100%",
			height : "99%",
			rownumbers : true,
			checkbox : true,
			dataAction : 'server', //从后台获取数据
			usePager : true, //服务器分页
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			method : 'post',
			url : "${ctx}/report/frame/design/cfg/getRptTemplateList.json?rptId=" + rptId +"&versionId=" + versionId,
			toolbar : {}
		});
	}
	//初始化按钮
	function initButtons() {
		var btns = [
			{
				text : "删除",
				icon : "fa-trash-o",
				click : rpt_delete,
				operNo : "rpt_delete"
			}
		];
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	//删除
	function rpt_delete() {
		if (!grid) {
			return;
		}
		var selRows = grid.getSelectedRows();
		if (selRows != null && selRows.length && selRows.length > 0) {
			var ids = "";
			var paths = "";
			for (var i = 0, l = selRows.length; i < l; i++) {
				var id = selRows[i].id;
				var path = selRows[i].templatepath;
				if (id) {
					ids = ids + id + ",";
					paths = paths + path + ",";  
				}
			}
			ids = ids.substring(0,ids.length-1);
			paths = paths.substring(0,paths.length-1);
			$.ajax({
				async : false,
				url : "${ctx}/report/frame/design/cfg/delRptTemplateById",
				data : {
					ids:ids,
					paths:paths,
				},
				type : "post",
				beforeSend : function() {
					window.parent.parent.BIONE.loading = true;
					window.parent.parent.BIONE.showLoading("正在删除，请稍后..");
				},
				success : function(result) {
					if (typeof window.parent.parent.BIONE != 'undefined') {
						window.parent.parent.BIONE.loading = false;
						window.parent.parent.BIONE.hideLoading();
					}
					window.parent.parent.BIONE.tip("删除成功");
					grid.reload();
				},
				error : function(e) {
					if (typeof window.parent.parent.BIONE != 'undefined') {
						window.parent.parent.BIONE.loading = false;
						window.parent.parent.BIONE.hideLoading();
					}
					window.parent.parent.BIONE.tip("删除失败，请联系系统管理员");
				}
			});
		} else {
			window.parent.parent.BIONE.tip("请选择要删除表样");
		}
	}
</script>
</head>
<body>
</body>
</html>