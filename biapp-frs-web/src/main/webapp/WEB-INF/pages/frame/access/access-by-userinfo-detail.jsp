<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">

	jQuery.validator.addMethod("greaterThan", function(value, element, params) {
	if (value == '' || value == null) {
		return true;
	}
	var ele = $("[name=" + params + "]");
	return value >= ele.val() ? true : false;
	}, "结束时间应当大于开始时间");
	var grid;
	function initSearchForm() {
		$("#search").ligerForm({
			fields : [{
				display : '开始时间',
				name : "startDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				cssClass : "field",
				attr : {
					op : ">=",
					field : 'login_date'
				}
				
			},{
				display : '结束时间',
				name : "endDate",
				newline : false,
				type : "date",
				options : {
					format : "yyyyMMdd"
				},
				cssClass : "field",
				attr : {
					op : "<=",
					field : 'login_date'
				},
				validate : {
					greaterThan : "startDate"
				}
			}]
		});
		$("#startDate").val(window.parent.startDate);
		$("#endDate").val(window.parent.endDate);
	}; 
	
	function initGrid() {	
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '登录时间',
				name : 'loginTime',
				width : "30%",
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss',
				align : 'center'
			},{
				display : '在线时间',
				name : 'onlineTime',
				width : "30%",
				align : 'center',
				render : function(row){
					return calTime(row.onlineTime);
				}
			},{
				display : '访问IP',
				name : 'loginIp',
				width : "30%",
				align : 'center'
			}],
			width : '100%',
			height : '99%',
			isScroll : true,
			checkbox: false,
			dataAction : 'server', 
			usePager : true, 
			alternatingRow : true,
			colDraggable : true,
			delayLoad : true,
			url : "${ctx}/bione/syslog/login/userDetail.json?userId=${userId}",
 			sortName : 'loginTime',//第一次默认排序的字段
 			sortOrder : 'desc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			checkbox : false,
			rownumbers : true
		});
		loadGrid();
	};
	
	function calTime(time){
		time=parseInt(time);
		time = parseInt(time/1000);
		var second=time%60;
		time=parseInt(time/60);
		var minute=time%60;
		time=parseInt(time/60);
		var hour=time%24;
		var day=parseInt(time/24);
		return day+"天"+hour+"时"+minute+"分"+second+"秒";
	}
	
	$(function() {	
		initSearchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#formsearch");
	});
	
	function loadGrid(){
		var rule = BIONE.bulidFilterGroup("#search");
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
	
</script>
</head>
<body>
</body>
</html>