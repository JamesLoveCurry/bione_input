<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript" src="${ctx}/js/echarts4/js/echarts.js"></script>
<script type="text/javascript">
	var dataDate = window.parent.base_DataDate;
	var orgNo = window.parent.base_OrgNo;
	var rptIndexNo = window.parent.base_selectIdxNo;
	var dataUnit = window.parent.base_selectUnit;
	var dataPrecision = window.parent.base_selectDataPrecision;
</script>
<script type="text/javascript">
	$(function() {
		initIdxData();
		//$('#backgroundDiv').append('<div class="l-tab-loading" style="display:block; background:url(${ctx}/images/classics/index/bg_center.jpg) no-repeat center center #ffffff;"></div>');
	});
	
	//获取指标上期数据
	function initIdxData(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/getIdxLastData",
			data : {
				dataDate : dataDate,
				orgNo : orgNo,
				rptIndexNo : rptIndexNo,
				dataUnit : dataUnit,
				dataPrecision : dataPrecision
			},
			dataType : 'json',
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(resultMap) {
				var lastYear = "0";//去年同期
				var thisYearFirst = "0";//年初
				var lastLastDate = "0";//上上期
				var lastDate = "0";//上期
				var nowDate = "0";//本期
				if(resultMap && resultMap.lastYear){
					lastYear = resultMap.lastYear;
				}
				if(resultMap && resultMap.thisYearFirst){
					thisYearFirst = resultMap.thisYearFirst;
				}
				if(resultMap && resultMap.lastLastDate){
					lastLastDate = resultMap.lastLastDate;
				}
				if(resultMap && resultMap.lastDate){
					lastDate = resultMap.lastDate;
				}
				if(resultMap && resultMap.nowDate){
					nowDate = resultMap.nowDate;
				}
				initEcharts(lastYear, thisYearFirst, lastLastDate, lastDate, nowDate);
			},
			error : function(resultMap, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + resultMap.status);
			}
		});
	}
	
	function initEcharts(lastYear, thisYearFirst, lastLastDate, lastDate, nowDate){
		var option = {
				color: ['#3398DB'],
			    title : {
			        text: '指标数据跨期分析',
			    },
			    tooltip : {
			        trigger: 'axis'
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            dataView : {show: true, readOnly: false},
			            magicType : {show: true, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    xAxis : [
			        {
			            type : 'category',
			            data : ['去年同期', '年初', '上上期', '上期', '本期']
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    legend: {
			        data:['指标数据']
			    },
			    series: [
			        {            
			        	name:'指标数据',
			            type:'bar',
			            barWidth: '40',
			            data:[lastYear, thisYearFirst, lastLastDate, lastDate, nowDate],
			            markPoint : {
			                data : [
			                    {type : 'max', name: '最大值'},
			                    {type : 'min', name: '最小值'}
			                ]
			            },
			            markLine : {
			                data : [
			                    {type : 'average', name: '平均值'}
			                ]
			            }
			        }
			    ]
			};
		var myChart = echarts.init(document.getElementById('chartDiv'));
		myChart.setOption(option);
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="chartDiv" style="width: 100%;height: 100%;"></div>
		<div id="backgroundDiv"></div>
	</div>
</body>
</html>