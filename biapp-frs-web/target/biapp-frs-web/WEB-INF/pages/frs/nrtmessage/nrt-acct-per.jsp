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
				display : '序号',
				name : 'recordId',
				width : '10%',
				hide:true,
				align: 'left'
			},{
				display : '账号',
				name : 'accountNumber',
				width : '5%'
			},  {
				display : '数据日期',
				name : 'statisticsDt',
				width : '8%',
				align: 'left'
			},  {
				display : '姓名类型',
				name : 'nameType',
				width : '8%',
				render : function(rowData) {
					if (rowData.nameType == "OECD202")
						return "个人姓名";
					if (rowData.nameType == "OECD203")
						return "别名";
					if (rowData.nameType == "OECD204")
						return "昵称";
					if (rowData.nameType == "OECD205")
						return "亦称作（aka）";  
					if (rowData.nameType == "OECD208")
						return "出生姓名"; 
					return rowData.nameType;
				}, 
				align: 'left' 
			},  {
				display : '法定英文（拼音）姓',
				name : 'lastName',
				width : '8%',
				align: 'left'
			},  {
				display : '英文中间名',
				name : 'middleName',
				width : '8%',
				align: 'left'
			} ,  {
				display : '法定英文（拼音）名',
				name : 'firstName',
				width : '8%',
				align: 'left'
			} ,  {
				display : '中文姓名',
				name : 'nameCn',
				width : '8%',
				align: 'left'
			} ,  {
				display : '曾用名',
				name : 'preceDingTitle',
				width : '8%',
				align: 'left'
			} ,  {
				display : '别名',
				name : 'titles',
				width : '8%',
				align: 'left'
			} ,  {
				display : '别名1',
				name : 'namePreFix',
				width : '8%',
				align: 'left'
			} ,  {
				display : '标签名',
				name : 'generationIdentifier',
				width : '8%',
				align: 'left'
			} ,  {
				display : '后缀',
				name : 'suffix',
				width : '8%',
				align: 'left'
			} ,  {
				display : '后缀1',
				name : 'generalSuffix',
				width : '8%',
				align: 'left'
			} ,  {
				display : '性别',
				name : 'gender',
				width : '8%',
				render : function(rowData) {
					if (rowData.gender == "M")
						return "男"; 
					if (rowData.gender == "F")
						return "女";  
					if (rowData.gender == "P")
						return "未说明性别"; 
					return rowData.gender;
				}, 
				align: 'left' 
			} ,  {
				display : 'Address的属性',
				name : 'legalAddressType',
				width : '8%',
				render : function(rowData) {
					if (rowData.gender == "OECD301") return "居住地址或办公地址"; 
					if (rowData.gender == "OECD302") return "居住地址";
					if (rowData.gender == "OECD303") return "办公地址";
					if (rowData.gender == "OECD304") return "注册地址";
					if (rowData.gender == "OECD305") return "其他"; 
					return rowData.gender;
				},
				align: 'left' 
			} ,  {
				display : '国家代码',
				name : 'countryCode',
				width : '8%',
				align: 'left'
			} ,  {
				display : '英文详细地址',
				name : 'addressFreeEn',
				width : '8%',
				align: 'left'
			} ,  {
				display : '所在城市',
				name : 'cityEn',
				width : '8%',
				align: 'left'
			} ,  {
				display : '街道',
				name : 'street',
				width : '8%',
				align: 'left'
			} ,  {
				display : '楼号',
				name : 'buildingIdentifier',
				width : '8%',
				align: 'left'
			} ,  {
				display : '房门号',
				name : 'suiteIdentifier',
				width : '8%',
				align: 'left'
			} ,  {
				display : '楼层',
				name : 'floorIdentifier',
				width : '8%',
				align: 'left'
			} ,  {
				display : '县级行政区划代码',
				name : 'districtName',
				width : '8%',
				align: 'left'
			} ,  {
				display : '邮箱',
				name : 'pOB',
				width : '8%',
				align: 'left'
			} ,  {
				display : '邮编',
				name : 'postCode',
				width : '8%',
				align: 'left'
			} ,  {
				display : '中国境内地址填写省/自治区/直辖市的拼音',
				name : 'countrySubentity',
				width : '8%',
				align: 'left'
			} ,  {
				display : '中文详细地址',
				name : 'addressFreeCn',
				width : '8%',
				align: 'left'
			} ,  {
				display : '省级行政区划代码',
				name : 'province',
				width : '8%',
				align: 'left'
			} ,  {
				display : '地市级行政区划代码',
				name : 'cityCn',
				width : '8%',
				align: 'left'
			} ,  {
				display : '县级行政区划代码1',
				name : 'districtName1',
				width : '8%',
				align: 'left'
			} ,  {
				display : '邮编1',
				name : 'postCode1',
				width : '8%',
				align: 'left'
			} ,  {
				display : '联系电话',
				name : 'phoneNo',
				width : '8%',
				align: 'left'
			} ,  {
				display : '身份证件类型',
				name : 'idType',
				width : '8%',
				render : function(rowData) {
					if (rowData.idType == "ACC01") return "第一代居民身份证"; 
					if (rowData.idType == "ACC02") return "第二代居民身份证";
					if (rowData.idType == "ACC03") return "临时身份证";
					if (rowData.idType == "ACC04") return "中国护照";
					if (rowData.idType == "ACC05") return "户口簿";
					if (rowData.idType == "ACC06") return "村民委员会证明";
					if (rowData.idType == "ACC07") return "学生证";
					if (rowData.idType == "ACC15") return "港澳居民来往内地通行证";
					if (rowData.idType == "ACC16") return "台湾居民来往大陆通行证";
					if (rowData.idType == "ACC17") return "外国人永久居留证";
					if (rowData.idType == "ACC18") return "边民出入境通行证";
					if (rowData.idType == "ACC19") return "外国护照";
					if (rowData.idType == "ACC20") return "其他";
					return rowData.idType;
				},
				align: 'left'
			} ,  {
				display : '身份证件号码',
				name : 'idNumber',
				width : '8%',
				align: 'left'
			} ,  {
				display : '税收居民国（地区）代码',
				name : 'rescountryCode',
				width : '8%',
				align: 'left'
			} ,  {
				display : '识别号',
				name : 'tin',
				width : '8%',
				align: 'left'
			} ,  {
				display : '发放识别号的国家（地区）代码',
				name : 'issuedBy',
				width : '8%',
				align: 'left'
			} ,  {
				display : '识别号类型',
				name : 'inType',
				width : '8%',
				align: 'left'
			} ,  {
				display : '税收居民国（地区）代码1',
				name : 'rescountryCode1',
				width : '8%',
				align: 'left'
			} ,  {
				display : '识别号1',
				name : 'tin1',
				width : '8%',
				align: 'left'
			} ,  {
				display : '发放识别号的国家（地区）代码1',
				name : 'issuedBy1',
				width : '8%',
				align: 'left'
			} ,  {
				display : '识别号类型1',
				name : 'inType1',
				width : '8%',
				align: 'left'
			} ,  {
				display : '不能提供居民国/地区纳税人识别号的理由',
				name : 'explanation',
				width : '8%',
				align: 'left'
			} ,  {
				display : '国籍',
				name : 'nationAlity',
				width : '8%',
				align: 'left'
			} ,  {
				display : '出生日期',
				name : 'birthDate',
				width : '8%',
				align: 'left'
			} ,  {
				display : '出生城市',
				name : 'city',
				width : '8%',
				align: 'left'
			} ,  {
				display : '出生国代码',
				name : 'countryCode1',
				width : '8%',
				align: 'left'
			} ,  {
				display : '其他出生国代码',
				name : 'formerCountryName',
				width : '8%',
				align: 'left'
			} ,  {
				display : '错误标识',
				name : 'errorFlag',
				width : '8%',
				align: 'left'
			} ,  {
				display : '错误代码',
				name : 'errorCode',
				width : '8%',
				align: 'left'
			}  ],
			checkbox : true,
			usePager : true,
			isScroll : true,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/nrtmessage/acctPerList',
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
		BIONE.commonOpenLargeDialog('新增流量流向信息', 'pisaFlowEidtWin','${ctx}/frs/nrtmessage/indivAcctEidt');
	}
	//修改属性
	function oper_modify() {
		var rows = grid.getSelectedRows();
		if (rows.length != 1) {
			BIONE.tip('请选择一条记录!');
			return;
		}
		BIONE.commonOpenLargeDialog("修改流量流向信息", "pisaFlowEidtWin","${ctx}/frs/nrtmessage/indivAcctEidt?id="+rows[0].recordId);
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
						url : '${ctx}/frs/nrtmessage/deleteIndivAcct?&ids=' + idsStr,
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