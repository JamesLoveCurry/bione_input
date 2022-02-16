<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var grid;
	var renderData;
	
	$(function(){
		initRanderData();
		searchForm();
		initGrid();
		initButtons();
		initExport();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});

	//初始化数据码值数据
	function initRanderData(){
		//流向类型
		var paramTypeNos = "pisaFlowType"
		$.ajax({
			async : false,
			type : "post",
			url : '${ctx}/frs/pisamessage/randerDataByParamTypeNo.json',
			data: {
				typeNos : paramTypeNos
			},
			success : function(res){
				renderData = res;
			},
			error : function(e){
				BIONE.tip('初始化数据加载失败');
			}
		});
	}
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "文件名",
				name : "fileName",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "fileName"
				}
			},{
				display : '日期',
				name : 'statisticsDt',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "statisticsDt"
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
				display : '日期',
				name : 'statisticsDt',
				width : '15%'
				
			},{
				display : "文件名",
				name : "fileName",
				width : "10%",
				align: "center"
			},{
				display : "模块名称",
				name : "moduleName",
				width : "20%",
				align: "center"
			},{
				display : "批次",
				name : "batch",
				width : "20%",
				align: "center"
			}],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/nrtmessage/hisfileList',
			sortName : 'fileName', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}

	function initButtons() {
		var btns = [ {
			text : '新增',
			click : oper_add,
			icon : 'fa-plus',
			operNo : 'oper_add'
		}, {
			text : '修改',
			click : oper_modify,
			icon : 'fa-pencil-square-o',
			operNo : 'oper_modify'
		}, {
			text : '删除',
			click : oper_delete,
			icon : 'fa-trash-o',
			operNo : 'oper_delete'
		},{
			text : '导出Excel',
			click : exportInfo,
			icon : 'fa-download'
		}, /**,{
			text : '导出Excel',
			click : exportInfo,
			icon : 'fa-download'
		},{
			text : '导入Excel',
			click : importInfo,
			icon : 'fa-upload'
		}**/];
		BIONE.loadToolbar(grid, btns, function() {});
	}
	
	//码值转换
	function flowTypeRender(rowdata) {
		var renderVal = renderData.pisaFlowType[rowdata.flowType];
		return renderVal;
	}
	
	function oper_add() {
		BIONE.commonOpenLargeDialog('新增信息', 'hisFlowEidtWin','${ctx}/frs/nrtmessage/histFlowEidt');
	}
	//修改属性
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		BIONE.commonOpenLargeDialog("修改信息", "hisFlowEidtWin","${ctx}/frs/nrtmessage/histFlowEidt?id="+rows[0].recordId);
	}
	

	
	

	
	
	//批量删除报文集
	function oper_delete() {
		var ids = achieveIds();
		if(ids.length > 0){
			var idsStr = ids.join(',');
			$.ligerDialog.confirm('您确定删除这' + ids.length + "条记录吗？", function(yes) {
				if (yes) {
					$.ajax({
						async : false,
						type : "post",
						url : '${ctx}/frs/nrtmessage/deletePersonFlow?&ids=' + idsStr,
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
			ids.push(rows[i].recordId)
		}
		return ids;
	}
	
	function exportInfo(){
		BIONE.commonOpenDialog("流量流向配置导出", "exportWin", 600, 440,	"${ctx}/frs/pisamessage/exportExcel");
	}
	
	function importInfo(){
		BIONE.commonOpenDialog("流量流向配置导入", "importWin", 600, 440,	"${ctx}/report/frame/wizard?type=PisaIdx");
	}

	function exportData(fileName,taskName,dataDate){
		var src = '';
		src = "${ctx}/frs/pbmessage/manager/exportDataInfo?&filepath="+fileName+"&taskNm="+taskNm+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};

	function exportXls(fileName){
		var src = '';
		src = "${ctx}/frs/pbmessage/exportXls?filepath="+fileName+"&d="+ new Date().getTime();
		downdload.attr('src', src);
	};
	
	function initExport() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
</script>
</head>
<body>
</body>
</html>