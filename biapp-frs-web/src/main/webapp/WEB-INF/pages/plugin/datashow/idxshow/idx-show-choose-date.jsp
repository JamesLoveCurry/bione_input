<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>

<style>
* {
	-webkit-box-sizing: content-box;
	-moz-box-sizing: content-box;
	box-sizing: content-box;
}

#center {
	background-color: #F1F1F1;
}
</style>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript"
	src="${ctx}/js/daterangepicker/moment.min.js"></script>
<script type="text/javascript"
	src="${ctx}/js/daterangepicker/daterangepicker.js"></script>
<link rel="stylesheet" type="text/css"
	href="${ctx}/js/bootstrap3/css/bootstrap.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/daterangepicker/daterangepicker.css" />

<script type="text/javascript">
	Date.prototype.Format = function(fmt) 
	{ //author: meizz 
	  var o = { 
	    "M+" : this.getMonth()+1,                 //月份 
	    "d+" : this.getDate(),                    //日 
	    "h+" : this.getHours(),                   //小时 
	    "m+" : this.getMinutes(),                 //分 
	    "s+" : this.getSeconds(),                 //秒 
	    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
	    "S"  : this.getMilliseconds()             //毫秒 
	  }; 
	  if(/(y+)/.test(fmt)) 
	    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
	  for(var k in o) 
	    if(new RegExp("("+ k +")").test(fmt)) 
	  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
	  return fmt; 
	}
	$(function(){
		initDate();
		initQueryRules();
		initBtn();
	});
	
	//初始化日期控件
	function initDate(){
		if(window.parent.dimFilters["${dimTypeNo}"] != null){
			startDate = window.parent.dimFilters["${dimTypeNo}"].selectedNodes[0].id;
			endDate = window.parent.dimFilters["${dimTypeNo}"].selectedNodes[1].id;
		}else{
			endDate = startDate = new Date().Format("yyyy-MM-dd");
		} 
		$('#daterange').daterangepicker({
			"locale" :{
				format : "YYYY-MM-DD",
				separator: ' 至 ',
				monthNames: ["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],
				daysOfWeek: ["日","一","二","三","四","五","六"]
			},
		    "showDropdowns": true,
		    "autoApply": true,
		    "linkedCalendars":false,
		    "startDate": startDate,
		    "endDate":  endDate
		}, function(start, end, label) {
			var daterangepicker = $("#daterange").data('daterangepicker');
			var date = $("#daterange").val().split("至");
			var selectedNodes = [{id : start.format("YYYY-MM-DD"), text : start.format("YYYY-MM-DD")},
			                     {id :end.format("YYYY-MM-DD"), text : end.format("YYYY-MM-DD")}];
			window.parent.dimFilters["${dimTypeNo}"] = {"selectedNodes" : selectedNodes};
		});
	}
	
	//初始化日期力度
	function initQueryRules(){
		$("#queryRules").ligerComboBox(
				{
					data : [{
						id:'everyDay',
						text:'每日'
					},{
						id:'monthBegin',
						text:'月初'
					},{
						id:'monthEnd',
						text:'月末'
					},{
						id:'quarterBegin',
						text:'季初'
					},{
						id:'quarterEnd',
						text:'季末'
					},{
						id:'yearBegin',
						text:'年初'
					},{
						id:'yearEnd',
						text:'年末'
					}],
					initValue : window.parent.queryRules? window.parent.queryRules : 'everyDay',
					onSelected : function(id, text) {
						window.parent.queryRules = id;
						strQueryDate(id);
					}
				});
	}
	
	//初始化按钮
	function initBtn() {
		var btns =[]; 
		btns.push({
			text : "选择",
			onclick : function() {
				BIONE.closeDialog("chooseOrg");
			}
		});
		BIONE.addFormButtons(btns);
	}
	
	//构造查询日期数组
	function strQueryDate(queryRules){
		var queryDate = [];
		var startDate = window.parent.dimFilters["DATE"].selectedNodes[0].id;
		var endDate = window.parent.dimFilters["DATE"].selectedNodes[1].id;
		if(startDate && endDate){
			switch(queryRules){
			case 'everyDay': 
				break;
			case 'monthBegin': 
				queryDate = strMonthBegin(startDate, endDate);
				break;
			case 'monthEnd':
				queryDate = strMonthEnd(startDate, endDate);
				break;
			case 'quarterBegin': 
				queryDate = strQuarterBegin(startDate, endDate);
				break;
			case 'quarterEnd': 
				queryDate = strQuarterEnd(startDate, endDate);
				break;
			case 'yearBegin':
				queryDate = strYearBegin(startDate, endDate);
				break;
			case 'yearEnd':
				queryDate = strYearEnd(startDate, endDate);
				break;
			}
		}
		window.parent.queryDate = queryDate;
	}
	
	//数组去重
	function newArr(array){ 
	    var arrs = []; 
	    for(var i = 0; i < array.length; i++){ 
	        if (arrs.indexOf(array[i]) == -1){ 
	            arrs.push(array[i])
	        }; 
	    } 
	    return arrs; 
	}
	
	function getDate(datestr){
		  var temp = datestr.split("-");
		  var date = new Date(temp[0],temp[1] - 1,temp[2]);
		  return date;
	}
	
	//构造月初
	function strMonthBegin(startDate, endDate){
		var queryDate = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = startTime.getMonth() + 1;
			if(month < 10)
				month = "0" + month; 
			var day = "01";
			queryDate.push(year + "" + month + "" + day);
			startTime.setMonth(startTime.getMonth()+1);
		}
		return queryDate;
	}
	
	//构造月末
	function strMonthEnd(startDate, endDate){
		var queryDate = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = startTime.getMonth() + 1;
			var day = new Date(year, month, 0).getDate();
			if(month < 10)
				month = "0" + month; 
			queryDate.push(year + "" + month + "" + day);
			startTime.setMonth(startTime.getMonth()+1);
		}
		return queryDate;
	}
	
	//构造季初
	function strQuarterBegin(startDate, endDate){
		var queryDate = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = startTime.getUTCMonth() + 1;
			var day = "01";
			if(month >= 1 && month <= 3){
				month = "01"
			}else if(month >= 4 && month <= 6){
				month = "04"
			}else if(month >= 7 && month <= 9){
				month = "07"
			}else if(month >= 10 && month <= 12){
				month = "10"
			}
			queryDate.push(year + "" + month + "" + day);
			startTime.setMonth(startTime.getMonth()+1);
		}
		queryDate = newArr(queryDate);
		return queryDate;
	}
	
	//构造季末
	function strQuarterEnd(startDate, endDate){
		var queryDate = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = startTime.getUTCMonth() + 1;
			if(month >= 1 && month <= 3){
				month = "03"
			}else if(month >= 4 && month <= 6){
				month = "06"
			}else if(month >= 7 && month <= 9){
				month = "09"
			}else if(month >= 10 && month <= 12){
				month = "12"
			}
			var day = new Date(year, month, 0).getDate();
			queryDate.push(year + "" + month + "" + day);
			startTime.setMonth(startTime.getMonth()+1);
		}
		queryDate = newArr(queryDate);
		return queryDate;
	}
	
	//构造年初
	function strYearBegin(startDate, endDate){
		var queryDate = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = "01";
			var day = "01";
			queryDate.push(year + "" + month + "" + day);
			startTime.setYear(startTime.getFullYear()+1);
		}
		queryDate = newArr(queryDate);
		return queryDate;
	}
	
	//构造年末
	function strYearEnd(startDate, endDate){
		var queryDate = [];
		var startTime = getDate(startDate);
		var endTime = getDate(endDate);
		while((endTime.getTime()-startTime.getTime())>=0){
			var year = startTime.getFullYear();
			var month = "12";
			var day = "31";
			queryDate.push(year + "" + month + "" + day);
			startTime.setYear(startTime.getFullYear()+1);
		}
		queryDate = newArr(queryDate);
		return queryDate;
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" style="margin-left: 20px; margin-top: 10px;">
			<div style="width: 75px; float: left;">
				<span style="font-size: 12">查询日期：</span>
			</div>
			<div class="l-text l-text-date" style="width: 35%; float: left;">
				<input type="text" id="daterange" name="daterange" ltype="date"
					key="DATE" class="l-text-field" style="width: 100%;">
				<div class="l-text-l"></div>
				<div class="l-text-r"></div>
				<div class="l-trigger" style="padding-bottom: 5px;">
					<div class="l-trigger-icon"></div>
				</div>
				<div class="l-trigger l-trigger-cancel" style="display: none;">
					<div class="l-trigger-icon"></div>
				</div>
			</div>
			<div style="width: 75px; float: left; margin-left: 20px;">
				<span style="font-size: 12">查询力度：</span>
			</div>
			<div class="l-form">
				<input id="queryRules" style="width: 100%;" />
			</div>
		</form>
		<div id="bottom" style="margin-top: 280px; margin-right: 30px;">
			<div class="form-bar">
				<div class="form-bar-inner" style="padding-top: 5px"></div>
			</div>
			<sitemesh:div property='template.button' />
		</div>
	</div>
</body>
</html>