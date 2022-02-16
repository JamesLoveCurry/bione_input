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
		//数据类型,是否按机构报送,地区类型,报送频度
		var paramTypeNos = "pisaIdxDataType,pisaCom,areaType,pisaSubmitFreq"
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
				display : "报表编号",
				name : "rptNum",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "rptNum"
				}
			},{
				display : "指标代码",
				name : "idxCode",
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "idxCode"
				}
			},{
				display : '维度代码',
				name : 'dimCode',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "dimCode"
				}
			},{
				display : '指标名称',
				name : 'idxNm',
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "idxNm"
				}
			},{
				display : '维度名称',
				name : 'dimNm',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "dimNm"
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
				display : '指标代码',
				name : 'idxCode',
				width : '10%',
				align: 'left'
			},  {
				display : '维度代码',
				name : 'dimCode',
				width : '8%',
				align: 'left'
			}, {
				display : '维度名称',
				name : 'dimNm',
				width : '10%',
				align: 'left'
			}, {
				display : '指标名称',
				name : 'idxNm',
				width : '25%',
				align: 'left'
			}, {
				display : '数据类型',
				name : 'dataType',
				width : '10%',
				render: dataTypeRender
			}, {
				display : '是否按机构报送',
				name : 'isOrgSubmit',
				width : '10%',
				render: isOrgSubmitRender
			}, {
				display : '地区类型',
				name : 'belongAddr',
				width : '10%',
				render: belongAddrRender
			}, {
				display : '报送频度',
				name : 'submitFreq',
				width : '10%',
				render: submitFreqRender
			}, {
				display : '报表编号',
				name : 'rptNum',
				width : '10%'
			}, {
				display : '值1单元格',
				name : 'val1CellNum',
				width : '10%'
			}, {
				display : '值2单元格',
				name : 'val2CellNum',
				width : '10%'
			}],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/pisamessage/idxConfigList',
			sortName : 'idxCode', //第一次默认排序的字段
			sortOrder : 'asc',
			toolbar : {}
		});
	}
	//码值转换
	function dataTypeRender(rowdata) {
		var renderVal = renderData.pisaIdxDataType[rowdata.dataType];
		return renderVal;
	}
	//码值转换
	function isOrgSubmitRender(rowdata) {
		var renderVal = renderData.pisaCom[rowdata.isOrgSubmit];
		return renderVal;
	}
	//码值转换
	function belongAddrRender(rowdata) {
		var renderVal = renderData.areaType[rowdata.belongAddr];
		return renderVal;
	}
	//码值转换
	function submitFreqRender(rowdata) {
		var renderVal = renderData.pisaSubmitFreq[rowdata.submitFreq];
		return renderVal;
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
		} ,{
			text : '导出Excel',
			click : exportInfo,
			icon : 'fa-download'
		},{
			text : '导入Excel',
			click : importInfo,
			icon : 'fa-upload'
		}];
		BIONE.loadToolbar(grid, btns, function() {});
	}
	
	function oper_add() {
		BIONE.commonOpenLargeDialog('新增报送指标信息', 'pisaIdxEidtWin','${ctx}/frs/pisamessage/pisaIdxEidt');
	}
	//修改属性
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		BIONE.commonOpenLargeDialog("修改报送指标信息", "pisaIdxEidtWin","${ctx}/frs/pisamessage/pisaIdxEidt?id="+rows[0].pisaIdxId);
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
						url : '${ctx}/frs/pisamessage/deletePisaIdx?&ids=' + idsStr,
						success : function(res){
							grid.loadData();
							BIONE.tip('删除成功');
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
			ids.push(rows[i].pisaIdxId)
		}
		return ids;
	}
	
	var width = $(window).width() - 60;
	var height = $(window).height() - 30;

	function exportInfo(){
		BIONE.commonOpenDialog("报送指标配置导出", "exportWin", 600, 440,"${ctx}/frs/pisamessage/exportExcel");
	}
	
	function importInfo(){
		BIONE.commonOpenDialog("报送指标配置导入", "importWin", 600, 440,	"${ctx}/report/frame/wizard?type=PisaIdx");
	}
	
	function exportXls(fileName, rptNums){
		var src = '';
		src = "${ctx}/frs/pisamessage/exportXls?filepath="+fileName+"&rptNums="+rptNums+"&d="+ new Date();
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