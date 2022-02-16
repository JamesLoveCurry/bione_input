<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	$(function(){
		searchForm();
		initGrid();
		initButtons();
		initExport();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	function searchForm() {
		$("#search").ligerForm({
			fields : [  {
				display : "任务名称",
				name : "taskNm",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "taskNm"
				}
			} ]
		});

	}

	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [{
				display : '任务名称',
				name : 'taskNm',
				width : '35%',
				align: 'left'
			}, 
		/* 	{
				display : '报送频度',
				name : 'taskFreq',
				width : '15%'
			}, */
			{
				display : '报送日期',
				name : 'generateDate',
				width : '15%'
			}, {
				display : '修改日期',
				name : 'uptDate',
				width : '15%'
			} ],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/nrtmessage/taskConfigList',
			sortName : 'taskNm', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	function initButtons() {
		var btns = [ {
			text : '生成报文',
			click : msg_create,
			icon : 'fa-file-text-o',
			operNo : 'createMsg'
		}, {
			text : '下载',
			click : msg_download,
			icon : 'fa-pencil-square-o',
			operNo : 'oper_modify'
		}, {
			text : '删除',
			click : oper_delete,
			icon : 'fa-trash-o',
			operNo : 'oper_delete'
		}];
		BIONE.loadToolbar(grid, btns, function() {});
	}
	
	//生成报文
	function msg_create() {
		BIONE.commonOpenDialog('生成报文', 'createXmlWin', '350', '350','${ctx}/frs/nrtmessage/createNrtXml');
	}

	//下载报文
	function msg_download() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		
		var src = "${ctx}/frs/nrtmessage/downLoadFile?id="+rows[0].taskNo+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	}
	
	//批量删除
	function oper_delete() {
		var ids = achieveIds();
		if(ids.length > 0){
			var idsStr = ids.join(',');
			$.ligerDialog.confirm('您确定删除这' + ids.length + "条记录吗？", function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/nrtmessage/deleteNrtTask?&ids=' + idsStr,
						success : function(res){
							BIONE.tip('删除成功');
							grid.loadData();
						},
						error : function(e){
							BIONE.tip('删除失败');
						}
					});
				}
			});	
		} else {
			BIONE.tip('请选择记录');
		}
	}
	//获取选中行的主键
	function achieveIds() {
		//过滤版本
		var ids = [];
		var verId = '';
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			ids.push(rows[i].taskNo)
		}
		return ids;
	}

	function initExport() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
</script>
</head>
<body>
</body>
</html>