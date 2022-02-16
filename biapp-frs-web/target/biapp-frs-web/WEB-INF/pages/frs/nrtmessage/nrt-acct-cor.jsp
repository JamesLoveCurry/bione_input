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
				display : "账号",
				name : "accountNumber",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "accountNumber"
				}
			},{
				display : '数据日期',
				name : 'statisticsDt',
				newline : false,
				type : "text",
				cssClass : "field",
				attr : {
					op : "like",
					field : "statisticsDt"
				}
			}  ]
		});

	}

	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [{
				display : '数据日期',
				name : 'statisticsDt',
				width : '15%'
				
			},{
				display : '账号',
				name : 'accountNumber',
				width : '15%'
			},{
				display : '姓名类别',
				name : 'nameType',
				width : '20%',
				align: 'left'
			} ,{
				display : '机构英文名',
				name : 'organisationNameEn',
				width : '20%',
				align: 'left'
			} ,{
				display : '机构中文名',
				name : 'organisationNameCn',
				width : '20%',
				align: 'left'
			} ,{
				display : '地址类型',
				name : 'legalAddressType',
				width : '20%',
				align: 'left'
			} ,{
				display : '国家代码',
				name : 'countryCode',
				width : '10%',
				align: 'left'
			} ,{
				display : '英文详细地址',
				name : 'addressFreeEn',
				width : '20%',
				align: 'left'
			} ,{
				display : '所在城市',
				name : 'cityEn',
				width : '20%',
				align: 'left'
			} ,{
				display : '街道',
				name : 'street',
				width : '20%',
				align: 'left'
			} ,{
				display : '楼号',
				name : 'buildingIdentifier',
				width : '10%',
				align: 'left'
			} ,{
				display : '房门号',
				name : 'suiteIdentifier',
				width : '10%',
				align: 'left'
			} ,{
				display : '楼层',
				name : 'floorIdentifier',
				width : '10%',
				align: 'left'
			} ,{
				display : '区',
				name : 'districtName',
				width : '10%',
				align: 'left'
			} ,{
				display : '邮箱',
				name : 'pOB',
				width : '10%',
				align: 'left'
			} ,{
				display : '邮编EN',
				name : 'postCode',
				width : '10%',
				align: 'left'
			} ,{
				display : '省/自治区/直辖市',
				name : 'countrySubentity',
				width : '10%',
				align: 'left'
			} ,{
				display : '中文详细地址',
				name : 'addressFreeCn',
				width : '30%',
				align: 'left'
			} ,{
				display : '省级行政区划代码',
				name : 'province',
				width : '10%',
				align: 'left'
			} ,{
				display : '地市级行政区划代码',
				name : 'cityCn',
				width : '10%',
				align: 'left'
			} ,{
				display : '县级行政区划代码',
				name : 'districtName1',
				width : '10%',
				align: 'left'
			} ,{
				display : '邮编CN',
				name : 'postCode1',
				width : '10%',
				align: 'left'
			} ,{
				display : '税收居民国（地区）代码',
				name : 'rescountryCode',
				width : '10%',
				align: 'left'
			} ,{
				display : '纳税人识别号',
				name : 'tin',
				width : '10%',
				align: 'left'
			} ,{
				display : '发放识别号的国家（地区）代码',
				name : 'issuedBy',
				width : '10%',
				align: 'left'
			} ,{
				display : '未能获取纳税人识别号的原因',
				name : 'explanation',
				width : '20%',
				align: 'left'
			} ],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/nrtmessage/acctCorList',
			sortName : 'accountNumber', //第一次默认排序的字段
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
		} /**,{
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
		BIONE.commonOpenLargeDialog('新增报送信息', 'organFlowEidtWin','${ctx}/frs/nrtmessage/organFlowEidt');
	}
	//修改属性
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		BIONE.commonOpenLargeDialog("修改信息", 'organFlowEidtWin','${ctx}/frs/nrtmessage/organFlowEidt?id='+rows[0].recordId);
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
						url : '${ctx}/frs/nrtmessage/deleteOrganFlow?&ids=' + idsStr,
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