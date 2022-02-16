<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<style scoped>

	.status-point {
		margin: 8px 0;
		display: inline-block;
		width: 14px;
		height: 14px;
		border-radius: 50%;
	}
</style>
<script type="text/javascript">
	var grid;
	var busiTypeMap = new Map();
	var rptType = '${rptType}';
	var orgNo = '${orgNo}'
	var orgName = '${orgName}'
	var dataDate = '${dataDate}'

	$(function() {
		initGrid();
		initSearch();
		initButtons();
		initDefData();
	});
	
	//监管制度列表
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [
			{
				display : '报表名称',
				name : 'rptNm',
				width : '20%',
				align : 'left'
			},{
				display : '指标单元格',
				name : 'indexNm',
				width : "15%",
				align : 'left'
			},
			{
				display : '是否通过',
				name : 'isPass',
				width : "6%",
				align : 'center',
				render : function(data) {
					if (data.isPass == "1"){
						return "<div class='status-point' style='background:radial-gradient(circle at 6px 6px,#f6adad, #ff0000)' />";
					}else if (data.isPass == "0"){
						return "<div class='status-point' style='background:radial-gradient(circle at 6px 6px,#73f6ba, #00a65a)' />";
					} else {
						return "<div class='status-point' style='background: radial-gradient(circle at 6px 6px,#c3bcbc, #666)' />";
					}
				}
			},
			{
				display : '本期数值',
				name : 'currentVal',
				width : "10%",
				align : 'center'
			},
			{
				display : '比较数值',
				name : 'comparisonValue',
				width : "10%",
				align : 'center'
			},
			{
				display : '相差数值',
				name : 'minusValue',
				width : "10%",
				align : 'center'
			},
			{
				display : '阈值范围',
				name : 'alertValue',
				width : "10%",
				align : 'center'
			},
			{
				display : '相差比率',
				name : 'differenceVal',
				width : "10%",
				align : 'center'
			},
			{
				display : '预警类型',
				name : 'compareType',
				width : "6%",
				align : 'center',
				render : function(data) {
					if (data.compareType == "01"){
						return "环比";
					}else if (data.compareType == "02"){
						return "同比";
					}else if (data.compareType == "03"){
						return "较上日";
					}else if (data.compareType == "04"){
						return "较月初";
					}else if (data.compareType == "05"){
						return "较上月末";
					}else if (data.compareType == "06"){
						return "较季初";
					}else if (data.compareType == "07"){
						return "较上季末";
					}else if (data.compareType == "08"){
						return "较年初";
					}else if (data.compareType == "09"){
						return "较上年末";
					}
				}
			},
			{
				display : '机构名称',
				name : 'orgName',
				width : '10%',
				align : 'left'
			}, {
				display : '数据日期',
				name : 'dataDate',
				width : '6%',
				align : 'center'
			},
			{
				display : '单位',
				name : 'unit',
				width : "5%",
				align : 'center'
			}, {
				display : '报送模块',
				name : 'busiType',
				width : '6%',
				align : 'center',
				render : function(rowdata, index, value) {
					return busiTypeMap.get(value);
				}
			}],
			checkbox : false,
			rownumbers : true,
			isScroll : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : "${ctx}/frs/verificationWarning/getWarnValidList?rptType="+rptType+"&dataDate="+dataDate+"&orgNo="+orgNo,
			enabledSort: false,
			sortName : 'rptNm',//第一次默认排序的字段
			sortOrder : 'asc', //排序的方式
			width : '100%',
			height : '99%',
			toolbar : {}
		});
	}

	//搜索框初始化
	function initSearch() {
		$("#search").ligerForm({
			labelWidth : 100,
			fields : [
			{
				display : "数据日期",
				name : "dataDate",
				newline : false,
				type : "date",
				width : '140',
				cssClass : "field",
				attr : {
					op : "=",
					field : "dataDate"
				},
				options : {
					format : "yyyyMMdd"
				},
				validate : {
					required : true
				}
			},
			{
				display : "机构名称",
				name : "orgNo",
				newline : false,
				type : "select",
				cssClass : "field",
				comboboxName : "orgNm_sel",
				attr : {
					op : "in",
					field : "orgNo"
				},
				validate : {
					required : true
				},
				options : {
					onBeforeOpen : function() {
						if (rptType) {
							var height = $(window).height() - 120;
							var width = $(window).width() - 480;
							window.BIONE.commonOpenDialog(
									"机构树",
									"taskOrgWin",
									width,
									height,
									"${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=" + rptType,
									null);
							return false;
						} else {
							BIONE.tip("请选择报送模块！");
						}
					},
					cancelable : true
				}
			},
			{
				display: '报表名称',
				name :'rptIdx',
				comboboxName:'rptIdxBox',
				type: 'select',
				newline: false,
				attr : {
					op : "in",
					field : "rptId"
				},
				options : {
					onBeforeOpen : function() {
						if(rptType){
							BIONE.commonOpenDialog(
									"报表选择",
									"rptIdxTreeWin",
									480,
									380,
									'${ctx}/report/frame/design/cfg/rptIdxTreeWin?busiType='+rptType,
									null);
						}else{
							BIONE.tip("请先选择业务类型");
						}
						return false;
					}
				}
			},
			{
				display : "校验状态",
				name : "isPass",
				comboboxName : 'isPassBox',
				newline : false,
				type : "select",
				width : "140",
				options : {
					data : [  {
						text : "请选择",
						id : ""
					},{
						text : '通过',
						id : "0"
					},{
						text : '未通过',
						id : '1'
					},{
						text : '未校验',
						id : '2'
					}]
				},
				attr : {
					op : "=",
					field : "isPass"
				}
			}
			]
		});
	}

	function initDefData(){
		// var date = getLastMonthAndDay();
		$.ligerui.get('dataDate').setValue(dataDate);
		$.ligerui.get('orgNm_sel').setValue(orgNo);
		$.ligerui.get('orgNm_sel').setText(orgName);
	}

	/**
	 * 获取上个月月底日期
	 */
	function getLastMonthAndDay(){
		var nowDate = new Date();
		var year = nowDate.getFullYear();
		var month = nowDate.getMonth();
		if(month == 0){
			month = 12;
			year = year - 1;
		}
		var lastDay = new Date(year,month,0);
		var yyyyMMdd = year+"-"+month+"-"+lastDay.getDate();
		return yyyyMMdd;
	}

	//初始化按钮
	function initButtons() {
		var btns = [ {
			text : "近一年校验结果统计",
			icon : "fa-book",
			operNo : "sumByOrg",
			click : sumByOrg
		}, {
			text : "近一年指标值变化趋势",
			icon : "fa-book",
			operNo : "rpt_version",
			click : analysisByYear
		}];

		BIONE.loadToolbar(grid, btns, function() {
		});

		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}

	function sumByDate() {

	}

	function sumByOrg() {
		var orgno = $.ligerui.get('orgNm_sel').getValue();
		var orgnm = $.ligerui.get('orgNm_sel').getText();
		if(orgno == null || orgno == '' || orgno == undefined){
			BIONE.tip('请选择一个机构');
			return;
		}
		var orgnoArr = orgno.split(",");
		if(orgnoArr.length != 1){
			BIONE.tip('请选择一个机构');
			return;
		} else {
			BIONE.commonOpenDialog("近一年校验结果统计", "imageWin", 900, 460,
					"${ctx}/frs/verificationWarning/warnAnalysisByOrgIndex?orgNo="+orgno+"&orgNm="+orgnm);
		}
	}

	//数据分析 近一年统计
	function analysisByYear() {
		var rows = grid.getSelectedRows();
		if (rows.length == 1) {
			if(rows[0].isPass){
				var orgno = $.ligerui.get('orgNm_sel').getValue();
				var indexNo = rows[0].indexNo;
				var indexNm = rows[0].indexNm;
				var rptNm = rows[0].rptNm;
				BIONE.commonOpenDialog("近一年指标值变化趋势", "imageWin", 900, 460,
						"${ctx}/frs/verificationWarning/analysisByYearIndex?indexNo="+indexNo+"&orgNo="+orgno + "&unit=" + rows[0].unit
						+ "&rptNm=" + rows[0].rptNm + "&indexNm=" + rows[0].indexNm);
			} else {
				BIONE.tip('请选择已校验的数据');
			}

		} else {
			BIONE.tip('请选择一条记录');
		}
	}

	// 创建表单搜索按钮：搜索、高级搜索
	BIONE.addSearchButtons = function(form, grid, btnContainer) {
		if (!form)
			return;
		form = $(form);
		if (btnContainer) {
			BIONE.createButton({
				appendTo : btnContainer,
				text : '查询',
				icon : 'fa-search',
				click : function() {
					BIONE.validate($("#formsearch"));
					if($("#formsearch").valid()){//edit by fangjuan 20150707
						var rule = BIONE.bulidFilterGroup(form);
						if (rule.rules.length) {
							grid.setParm("condition",JSON2.stringify(rule));
							grid.setParm("newPage",1);
							grid.options.newPage=1
						} else {
							grid.setParm("condition","");
							grid.setParm('newPage', 1);
							grid.options.newPage=1
						}
						grid.loadData();
					}
				}
			});
			BIONE.createButton({
						appendTo : btnContainer,
						text : '重置',
						icon : 'fa-repeat',
						click : function() {
							$(":input", form).not(":submit, :reset,:hidden,:image,:button, [disabled]").each(function() {
										$(this).val("");
							});
							initDefData();
						}
					});
		}
	};
</script>
</head>
<body>
</body>
</html>