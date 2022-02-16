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
				display : "账户类别",
				name : "dueDiligenceInd",
				newline : false,
				type : "text",
				options : {data : [  { 'id' : 'N', 'text' : '新开账户' }, 
									 { 'id' : 'P', 'text' : '存量账户'}]},
				cssClass : "field",
				attr : {
					op : "like",
					field : "dueDiligenceInd"
				}
			}   ]
		});

	}

	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '100%',
			columns : [{
				display : '序号',
				name : 'recordId',
				width : '10%',
				hide:true,
				align: 'left'
			},{
				display : '账号',
				name : 'accountNumber',
				width : '10%',
				align: 'left'
			},  {
				display : '数据日期',
				name : 'statisticsDt',
				width : '8%',
				align: 'center'
			},  {
				display : '账户类别',
				name : 'dueDiligenceInd',
				width : '8%',
				render : function(rowData) {
					if (rowData.dueDiligenceInd == "N")
						return "新开账户";
					if (rowData.dueDiligenceInd == "P")
						return "存量账户"; 
					return rowData.dueDiligenceInd;
				}, 
				align: 'left'
			},  {
				display : '系统用户账号',
				name : 'reportingId',
				width : '8%',
				align: 'left'
			},  {
				display : '账户是否已注销',
				name : 'closedAccount',
				width : '8%',
				render : function(rowData) {
					if (rowData.closedAccount == "true")
						return "账户已注销";
					if (rowData.closedAccount == "false")
						return "正常户"; 
					return rowData.closedAccount;
				}, 
				align: 'left' 
			},  {
				display : '是否取得账户持有人的自证声明',
				name : 'selfCertification',
				width : '8%',
				render : function(rowData) {
					if (rowData.selfCertification == "true")
						return "取得自证声明";
					if (rowData.selfCertification == "false")
						return "未取得自证声明"; 
					return rowData.selfCertification;
				}, 
				align: 'left'
			},  {
				display : '货币代码（余额）',
				name : 'currCode',
				width : '8%',
				align: 'left'
			},  {
				display : '账户余额',
				name : 'accountBalance',
				width : '8%',
				align: 'left'
			},  {
				display : '账户持有人类别',
				name : 'accountHolderType',
				width : '8%',
				render : function(rowData) {
					if (rowData.accountHolderType == "CRS100")
						return "非居民个人";
					if (rowData.accountHolderType == "CRS101")
						return "有非居民控制人的消极非金融机构"; 
					if (rowData.accountHolderType == "CRS102")
						return "非居民机构，不包括消极非金融机构"; 
					if (rowData.accountHolderType == "CRS103")
						return "非居民消极非金融机构，但没有非居民控制人"; 
					return rowData.accountHolderType;
				}, 
				align: 'left' 
			},  {
				display : '开户金融机构名称',
				name : 'openingFIName',
				width : '8%',
				align: 'left'
			},  {
				display : '金融机构注册码',
				name : 'fiId',
				width : '8%',
				align: 'left'
			},  {
				display : '收入类型',
				name : 'paymentType',
				width : '8%',
				render : function(rowData) {
					if (rowData.paymentType == "CRS501")
						return "股息";
					if (rowData.paymentType == "CRS502")
						return "利息"; 
					if (rowData.paymentType == "CRS503")
						return "销售或者赎回总收入"; 
					if (rowData.paymentType == "CRS504")
						return "其他"; 
					return rowData.paymentType;
				}, 
				align: 'left' 
			},  {
				display : '货币代码（收入）',
				name : 'currCodes',
				width : '8%',
				align: 'left'
			},  {
				display : '收入金额和货币类型',
				name : 'paymentAmnt',
				width : '8%',
				align: 'left'
			},  {
				display : '报告类别',
				name : 'reportingType',
				width : '8%',
				align: 'left'
			},  {
				display : '报告唯一编码',
				name : 'messageRefId',
				width : '8%',
				align: 'left'
			},  {
				display : '报告年度',
				name : 'reportingPeriod',
				width : '10%',
				align: 'center'
			},  {
				display : '报告类型',
				name : 'messageTypeIndic',
				width : '8%',
				render : function(rowData) {
					if (rowData.messageTypeIndic == "CRS701")
						return "新数据";
					if (rowData.messageTypeIndic == "CRS702")
						return "修改和删除数据";  
					if (rowData.messageTypeIndic == "CRS703")
						return "无数据申报"; 
					return rowData.messageTypeIndic;
				}, 
				align: 'left' 
			},  {
				display : '报告生成时间戳',
				name : 'tmstp',
				width : '8%',
				type : 'date' ,
			    format:'YYYY-MM-DDTHH:mm:ss',
				align: 'center'
			},  {
				display : '账户记录编号',
				name : 'docRefId',
				width : '8%',
				align: 'left'
			},  {
				display : '被修改或被删除的账户记录编号',
				name : 'corrDocRefId',
				width : '8%',
				align: 'left'
			},  {
				display : '账户报告的类型',
				name : 'docTypeIndic',
				width : '8%',
				render : function(rowData) {
					if (rowData.docTypeIndic == "R1")
						return "新账户记录";
					if (rowData.docTypeIndic == "R2")
						return "修改账户记录";  
					if (rowData.docTypeIndic == "R3")
						return "删除账户记录"; 
					return rowData.docTypeIndic;
				},
				align: 'left' 
			},  {
				display : '错误标识',
				name : 'errorFlag',
				width : '8%',
				align: 'left'
			},  {
				display : '错误代码',
				name : 'errorCode',
				width : '8%',
				align: 'left'
			} ],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			overflow:true,
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/nrtmessage/baseAcctList',
			sortName : 'docTypeIndic', //第一次默认排序的字段
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
		} 
	/* 	,{
			text : '导出Excel',
			click : exportInfo,
			icon : 'fa-download'
		},{
			text : '导入Excel',
			click : importInfo,
			icon : 'fa-upload'
		} */
		];
		BIONE.loadToolbar(grid, btns, function() {});
	}
	
	function oper_add() {
		BIONE.commonOpenLargeDialog('新增报送指标信息', 'baseAcctEidtWin','${ctx}/frs/nrtmessage/nrtBaseAcctEidt');
	}
	//修改属性
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		BIONE.commonOpenLargeDialog("修改报送指标信息", "baseAcctEidtWin","${ctx}/frs/nrtmessage/nrtBaseAcctEidt?id="+rows[0].recordId);
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
						url : '${ctx}/frs/nrtmessage/deleteBaseAcct?&ids=' + idsStr,
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
			ids.push(rows[i].recordId)
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
		src = "${ctx}/frs/pisamessage/exportXls?filepath="+fileName+"&rptNums="+rptNums+"&d="+ new Date().getTime();
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