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
			fields : [ {
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
				width : '25%',
				align: 'left'
			},  {
				display : '报表编码',
				name : 'rptCode',
				width : '35%'
			}, {
				display : '频度类型',
				name : 'freqType',
				width : '15%',
				render : freqTypeRender
			}, {
				display : '批次号',
				name : 'batchId',
				width : '15%'
			}],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/pbmessage/ijTaskList',
			sortName : 'sortNum', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	function initButtons() {
		var btns = [ {
			text : '生成报文',
			click : createMsgNew,
			icon : 'icon-config',
			operNo : 'createMsg'
		},{
			text : '导入报文',
			click : impMsg,
			icon : 'icon-config',
			operNo : 'impMsg'
		}, {
			text : '新增',
			click : oper_add,
			icon : 'icon-add',
			operNo : 'oper_add'
		}, {
			text : '修改',
			click : oper_modify,
			icon : 'icon-modify',
			operNo : 'oper_modify'
		}, {
			text : '删除',
			click : oper_delete,
			icon : 'icon-delete',
			operNo : 'oper_delete'
		}];
		BIONE.loadToolbar(grid, btns, function() {});
	}
	
	function oper_add() {
		BIONE.commonOpenLargeDialog('新增报文任务', 'addMsgset', '${ctx}/frs/pbmessage/pbcTaskEdit');
	}
	
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选一条记录!');
		}else {
			var msgsetNo = rows[0].msgsetNo;
			BIONE.commonOpenLargeDialog("修改报文任务", "addMsgset", "${ctx}/frs/pbmessage/pbcTaskEdit?pbcTaskNo=" + msgsetNo );
		}
	}
	//批量删除报文集
	function oper_delete() {
		var msgsetNos = achieveIds();
		if(msgsetNos.length > 0){
			$.ligerDialog.confirm('您确定删除这' + msgsetNos.length + "条记录吗？", function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "POST",
						url : '${ctx}/frs/pbmessage/deleteTask?msgsetNos=' + msgsetNos.join(','),
						success : function(){
							BIONE.tip('删除成功');
							grid.loadData();
						},
						error : function(){
							BIONE.tip('删除失败');
						}
					});
				}
			});	
		} else {
			BIONE.tip('请选择记录');
		}
	}
	//生成报文（新）
	function createMsgNew() {
		var msgsetNos = achieveIds();
		if(msgsetNos.length == 1){
			var rows = grid.getSelectedRows();
			var taskNm = rows[0].taskNm;
			BIONE.commonOpenDialog('填写数据日期', 'dataDate', '350', '350', '${ctx}/frs/pbmessage/pbcCreate?type=B&taskNm=' + taskNm + '&msgsetNo=' + msgsetNos[0]);
		}else{
			BIONE.tip("请选择一条记录");
		}
	}
	//获取选中行的主键
	function achieveIds() {
		var msgsetNos = [];
		var rows = grid.getSelectedRows();
		for ( var i in rows) {
			msgsetNos.push(rows[i].msgsetNo)
		}
		return msgsetNos;
	}
	//频度渲染
	function freqTypeRender(rowdata) {
		if (rowdata.freqType == '0') {
			return "年";
		} else if (rowdata.freqType == '3') {
			return "季";
		} else if (rowdata.freqType == '4') {
			return "月";
		} else if (rowdata.freqType == '5') {
			return "旬";
		} else if (rowdata.freqType == '6') {
			return "周";
		} else if (rowdata.freqType == '7') {
			return "日";
		} else {
			return "";
		}
	}

	function exportData(fileName,taskName,dataDate){
		var src = '';
		src = "${ctx}/frs/pbmessage/exportDataInfo?&filepath="+fileName+"&taskNm="+taskNm+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	
	//202005 lcy 导入人行报文数据文件
	function impMsg(){
		BIONE.commonOpenDialog("导入报文-数据文件", "importWin", 600, 480, "${ctx}/report/frame/wizard?type=PbcIJFile");
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