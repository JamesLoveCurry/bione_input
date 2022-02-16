<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8_2.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript">
var groupicon = "${ctx}/images/classics/icons/communication.gif";
var grid, btns, url, ids = [], dialog,buttons = [];
var checkState = 0;
var manager;
$(function() {
	
	url = "${ctx}/udip/temple/listFile.json?id="+"${id}&d="+new Date().getTime();
	initGrid();
	$("#maingrid").stop();
	
	initButtons();
	initbutton();
});

	
	function initbutton() {
		buttons.push({
			text : '关闭',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '下一步',
			onclick : next
		});
		buttons.push({
			text : '上一步',
			onclick : upset
		});

		BIONE.addFormButtons(buttons);
	}
	function initGrid() {

		grid = manager = $("#maingrid").ligerGrid(
				{
					height : '325',
					width : '100%',
					columns : [
							{
								display : '模板名称',
								name : 'templeName',
								align : 'center',
								width : '45%',
								minWidth : '10%'
							},
							{
								display : '生成日期',
								name : 'createDate',
								align : 'center',
								width : '21%',
								minWidth : '10%'
							},
							{
								display : '操作人',
								name : 'operator',
								align : 'center',
								width : '20%',
								minWidth : '10%'
							},
							{
								display : "状态",
								name : "state",
								width : '7%',
								minWidth : '10%',
								editor : {
									type : 'select',
									data : [ {
										'id' : '1',
										'text' : '启用'
									}, {
										'id' : '0',
										'text' : '停用'
									} ]
								},
								render : function(row) {
									switch (row.state) {
									case "1":
										return "启用";
									case "0":
										return "停用";
									}
								}
							} ],
					checkbox : true,
					usePager : false,
					isScroll : false,
					rownumbers : true,
					enabledEdit : false,
					clickToEdit : true,
					alternatingRow : true,//附加奇偶行效果行
					colDraggable : true,
					dataAction : 'server',//从后台获取数据
					method : 'post',
					url : url,
					sortName : 'createDate', //第一次默认排序的字段
					sortOrder : 'asc',
					toolbar : {}
				});

	}
	function initButtons() {

		btns = [ {
			text : '下载',
			click : excel_down,
			icon : 'download'
		}];
		
		BIONE.loadToolbar(grid, btns, function() {
		});
	}

	function excel_down() {
		var rows = grid.getCheckedRows();

		if (rows.length == 1) {
			if (!document.getElementById('download')) {
				$('body').append(
						$('<iframe id="download" style="display:none"/>'));
			}
			$("#download").attr('src',
					'${ctx}/udip/temple/excel_down/' + rows[0].fileId+"?d="+new Date().getTime());
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}

	function next() {
		parent.next('4', '${id}');
	}
	function upset() {
		parent.next('2', '${id}');
	}
	function cancleCallBack() {
		parent.closeDsetBox();
	}

	// 获取选中的行
	function achieveIds() {
		ids = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].fileId)
			checkState = checkState+rows[i].state
		}
	}
	function reloadgrid() {
		var manager = $("#maingrid").ligerGetGridManager();
		manager.loadData();
	}
</script>
</head>
<body>

</body>
</html>