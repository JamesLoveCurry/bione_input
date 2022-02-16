<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1.jsp">
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
	var indexNo = '${indexNo}';
	var indexNm = '${indexNm}';
	var dataDate = '${dataDate}';
	var orgNo = '${orgNo}';
	var checkType = '${checkType}';
	var tabId = '${tabId}';
	var nodeType = '${nodeType}';
	var checkId = '${checkId}';
	var checkNm = '${checkNm}';
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
					field : 'check_time'
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
					field : 'check_time'
				},
				validate : {
					greaterThan : "startDate"
				}
			}]
		});
		//$("#startDate").val(window.parent.startDate);
		//$("#endDate").val(window.parent.endDate);
	}; 
	
	function initGrid() {	
		var url_ = "";
		if(nodeType == "check"){
			url_ = "${ctx}/report/frame/datashow/valid/checkDetail?tabId="+tabId
			+"&nodeType="+nodeType+"&checkId="+checkId+"&checkNm="+checkNm;
		}else{
			url_ = "${ctx}/report/frame/datashow/valid/checkDetail?indexNo="+indexNo+"&indexNm="+indexNm
			+"&dataDate="+dataDate+"&orgNo="+orgNo+"&checkType="+checkType;
		}
		
		grid = $("#maingrid").ligerGrid({
			columns : [{
				display : '校验名称',
				name : 'checkNm',
				width : "15%",
				align : 'center',
				render : function(data) {
					return '<span title="'+data.expressionDesc+'">'+data.checkNm+'</span>';
				}
			},{
				display : '校验时间',
				name : 'checkTime',
				width : "20%",
				type : 'date',
				format : 'yyyy-MM-dd hh:mm:ss',
				align : 'center'
			},{
				display : '维度校验项',
				name : 'dimCheck',
				width : "35%",
				align : 'center'
			},{
				display : '容差值',
				name : 'dVal',
				width : "15%",
				align : 'center'
			},{
				display : '是否通过',
				name : 'isPass',
				width : "10%",
				align : 'center',
				render : function(data) {
					if (data.isPass == "Y")
						return "通过";
					if (data.isPass == "N")
						return "未通过";
				}
			}],
			checkbox: false,
			dataAction : 'server', 
			usePager : true, 
			alternatingRow : true,
			colDraggable : true,
			delayLoad : true,
			url : url_,
 			sortName : 'checkTime',//第一次默认排序的字段
 			sortOrder : 'desc', //排序的方式
			pageParmName : 'page',
			pagesizeParmName : 'pagesize',
			rownumbers : true
			//toolbar:{}
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
		
		/* var btn = [{
			text:"导出Excel",
			click:exportExcel,
			icon:"export"
		}];
		initExport();
		BIONE.loadToolbar(grid,btn,null); */
		initTip();
	});
	
	function initTip() {
		$("#searchbox").css("position","relative");
		$("#searchbox").append('<div style="position:absolute;bottom:5px;"><img src="${ctx}/images/classics/icons/lightbulb.png"><span style="color:red;font-size:0.5em;"> tip : 鼠标悬停校验名称可查看校验公式 !</span></div>');
	}
	
	function initExport() {
		downdload = $('<iframe id="download"  style="display: none;"/>');
		$('body').append (downdload);
	}
	
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
	
	//导出Execl
	function exportExcel() {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/bione/syslog/login/detail/exportFile?userId=${userId}&startDate="+$("#startDate").val()+"&endDate="+$("#endDate").val()+"&d="+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在生成文件中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var src = '';
				src = "${ctx}/bione/syslog/login/downloadDetail?fileName="+result.fileName;//导出成功后的excell文件地址
				downdload.attr('src', src);//给下载文件显示框加上文件地址链接
			},
			error : function(result) {
			}
		});
	}
</script>
</head>
<body>
</body>
</html>