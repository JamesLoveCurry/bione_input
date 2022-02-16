<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">

<style type="text/css">
	.link{
	    font-size: 16px;
   		font-weight: 600;
	}
	.with-border{
		display: none;
	}
</style>
<script type="text/javascript">
	var grid;
	var rptType = '${rptType}';
	var rptId = '${rptId}';
	var orgNo = '${orgNo}';
	var rptCycle = '${rptCycle}';
	$(function(){
		searchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	});
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [{
				display : "数据日期",
				name : "dataDate",
				newline : true,
				type : "date",
				cssClass : "field",
				labelWidth : '90',
				options : {
					format : 'yyyyMMdd',
				},
				attr : {
					op : "=",
					field : "dataDate"
				}
			}]
		});
	}
	//初始化Grid
	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			InWindow : false,
			height : '99%',
			width : '99%',
			columns : [ {
				display : '报送模块',
				name : 'RPT_TYPE',
				width : '10%',
				align : 'center',
				render : function(rowdata, index, value) {
					if (value == "02")
						return "1104监管";
					if (value == "03")
						return "人行大集中";
					if (value == "05")
						return "利率报备";
					else
						return "未知类型";
				}
			}, {
				display : '报表编号',
				name : "RPT_NUM",
				width : '10%',
				align : 'center'
			}, {
				display : '报表名称',
				name : 'RPT_NM',
				width : '20%',
				align : 'left'
			}, {
				display : '频度',
				name : 'RPT_CYCLE',
				width : '6%',
				align : 'center',
				render : function(rowdata, index, value) {
					if (value == "01")
						return "日";
					if (value == "02")
						return "月";
					if (value == "03")
						return "季";
					if (value == "04")
						return "年";
					if (value == "10")
						return "周";
					if (value == "11")
						return "旬";
					if (value == "12")
						return "半年";
					else
						return "未知类型";
				}
			}, {
				display : '报送机构',
				name : 'RPT_ORG_NM',
				width : '12%',
				align : 'center'
			}, {
				display : '填报人',
				name : 'RPT_USER_NM',
				width : '10%',
				align : 'center'
			}, {
				display : '数据日期',
				name : 'DATA_DATE',
				width : '10%',
				align : 'center'
			}, {
				display : '应报时间',
				name : 'END_TIME',
				width : '10%',
				align : 'center'
			}, {
				display : '上报时间',
				name : 'OPER_DATE',
				width : '10%',
				align : 'center'
			}, {
				display : '报表id',
				name : "RPT_ID",
				hide : "true"
			}, {
				display : '机构编号',
				name : "ORG_NO",
				hide : "true"
			}],
			checkbox : false,
			usePager : true,
			isScroll : false,
			rownumbers : true,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'server',//从后台获取数据
			method : 'post',
			url : '${ctx}/frs/rptexmaine/control/queryDelayDetailList?d='+new Date().getTime()+"&rptType="+ rptType
					+"&rptId="+ rptId +"&orgNo="+ orgNo +"&rptCycle="+ rptCycle,
			sortName : 'dataDate', //第一次默认排序的字段
			sortOrder : 'desc'
		});
		grid.setHeight($("#center").height() - 90);
	}
</script>

</head>
<body>
</body>
</html>