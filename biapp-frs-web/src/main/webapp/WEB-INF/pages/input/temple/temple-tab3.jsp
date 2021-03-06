<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8_2.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<style type="text/css">
#topmenu {
	display: none;
}
</style>
<script type="text/javascript">
var groupicon = "${ctx}/images/classics/icons/communication.gif";
var grid, btns, url, ids = [], dialog,buttons = [];
var checkState = 0;
var manager;
$(function() {
	
	url = "${ctx}/rpt/input/temple/listFile.json?id="+"${id}&d="+new Date().getTime();
	initGrid();
	$("#maingrid").stop();
	initButtons();
	initbutton();
	//changerList();
});

	function initbutton() {

		buttons.push({
			text : '关闭',
			onclick : cancleCallBack
		});
		buttons.push({
			text : '上一步',
			onclick : upset
		});
		if(!window.parent.lookType){
			buttons.push({
				text : '保存',
				onclick : save_obj
			});
		}
		BIONE.addFormButtons(buttons,true);
	}
	function initGrid() {

		grid = manager = $("#maingrid").ligerGrid(
				{
					height : '325',
					width : '100%',
					columns : [
							{
								display : '模板名称',
								name : 'fileName',
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
								name : 'operUser',
								align : 'center',
								width : '20%',
								minWidth : '10%'
							},
							{
								display : "状态",
								name : "sts",
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
									switch (row.sts) {
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
		if(!window.parent.lookType){
			btns = [ {
				text : '生成模板',
				click : excel_add,
				icon : 'add',
				operNo : 'excel_add'
			},{
				text : '下载',
				click : excel_down,
				icon : 'down',
				operNo : 'excel_down'
			},{
				text : '启用/停用',
				click : startOrEnd,
				icon : 'start'
			} ,{
				text : '删除',
				click : delete_templeFile,
				icon : 'delete',
				operNo : 'delete_templeFile'
			} ];
		}else{
			btns = [ {
				text : '下载',
				click : excel_down,
				icon : 'down',
				operNo : 'excel_down'
			}];
		}
		
		BIONE.loadToolbar(grid, btns, function() {
		});
	}
	
	function excel_add() {
		$.ajax({
			type : "post",
			url : '${ctx}/rpt/input/temple/excel_upload/' + "${id}?d="+new Date().getTime(),
			dataType : "text",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在生成文件中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function() {
				BIONE.tip("生成成功");
				grid.loadData();
			}
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
					'${ctx}/rpt/input/temple/excel_down/' + rows[0].fileId+"?d="+new Date().getTime());
		} else if (rows.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}
	function delete_templeFile() {
		var rows = grid.getCheckedRows();
		if (rows.length == 0) {
			BIONE.tip('请选择模板');
			return;
		}
		achieveIds();
		if(checkState>0){
			BIONE.tip('不可删除启用模版');
			checkState = 0;
			return;
		}
		$.ligerDialog.confirm('确实要删除这些条记录吗？', function(yes) {
			if (yes) {
				$.ajax({
					type : "POST",
					url : "${ctx}/rpt/input/temple/templeFile/" + ids+"?d="+new Date().getTime(),
					dataType : "text",
					success : function(text) {
						BIONE.tip(text);
						grid.loadData();
					}
				});
			}
		});
	}

	function next() {
		parent.next('4', '${id}');
	}
	function upset() {
		parent.next('2', '${id}');
	}
	function save_obj() {
		parent.saveTempleInfo();	
	}

	function cancleCallBack() {
		parent.closeDsetBox();
	}

	function startOrEnd(){
		achieveIds();
		checkState = 0;
		var manager = $("#maingrid").ligerGetGridManager(); 
		var data = manager.getData();
		for(var i =0;i<data.length;i++){
			if(data[i].state=='1' && data[i].fileId != ids[0] ){
				BIONE.tip('只能启用一个导入模板。');
				return;
			}
		}
		
		if (ids.length == 1) {
			$.ajax({
				async : false,
				url : '${ctx}/rpt/input/temple/chackFileState/' + ids[0]+"?d="+new Date().getTime(),
				success : function(data) {
					BIONE.tip(data);
					manager.loadData(); 
				}
			});
		} else if (ids.length > 1) {
			BIONE.tip("只能选择一行进行更改");
		} else {
			BIONE.tip("请选择一条数据进行更改");
			return;
		}
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