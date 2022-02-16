<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/taglibs.jsp"%>
<link rel="stylesheet" href="${ctx}/js/dupontChart/dupontChart.css">
<link rel="stylesheet" href="${ctx}/css/classics/font-awesome/css/font-awesome.css">
<script type="text/javascript" src="${ctx}/js/dupontChart/dupontChart.all.js"></script>
<script type="text/javascript" src="${ctx}/js/echarts4/js/echarts.js"></script>
<script type="text/javascript">
	var dataDate = window.parent.dataDate;
	var orgNo = window.parent.orgNo;
	var indexNo = window.parent.indexNo;
	var dataUnit = window.parent.dataUnit;
	var dataPrecision = window.parent.dataPrecision;
	var orgType = window.parent.orgType;
	var orgNm = window.parent.orgNm;
	var dataList = [];
	var pieChart = null;
	var barChart = null;
	var indexNm = window.parent.rptIndexNm;
</script>
<script type="text/javascript">
	var dupontTree = null;
	var dupontData = "";
	
	$(function() {
		idxOrgDrill();
	});
	
	function idxOrgDrill(parentOrgNm){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/getOrgDrillData",
			dataType : 'json',
			data : {
				indexNo : indexNo,
				orgType : orgType,
				dataDate : dataDate,
				orgNo : orgNo,
				parentOrgNm : parentOrgNm,
				dataUnit : dataUnit,
				dataPrecision : dataPrecision
			},
			type : "POST",
			beforeSend : function() {
				BIONE.showLoading("分析中，请稍等");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if(result && result.pieData){
					dataList.push(result);
					if(parentOrgNm){
						orgNm = parentOrgNm;
					}
					result.orgNm = orgNm;
					initPieEcharts(result.xAxisData, result.pieData, orgNm);
					initBarEcharts(result.xAxisData, result.seriesData);
					if(result.error){
						BIONE.tip("当前机构已经是叶子机构，无法继续下钻");
					}
				}
			}
		});
	}
	
	function initPieEcharts(xAxisData, pieData, subtext){
		var selected = {};
	    for (var i = 0; i < xAxisData.length; i++) {
	        selected[xAxisData[i]] = i < 10;//默认选择10个机构，防止数据堆叠问题
	    }
		var option = {
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    title : {
			        text: subtext + '--' + '机构数据下钻',
			        subtext: indexNm,
			        x:'center'
			    },
			    legend: {
			        orient: 'vertical',
			        x: 'left',
			        type: 'scroll',
			        top: 20,
			        bottom: 20,
			        data: xAxisData,
			        selected: selected
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            dataView : {show: true, readOnly: false},
			            saveAsImage : {show: true},
			            myTool: {
			                show: true,
			                title: '上一层',
			                icon: 'image://${ctx}/js/echarts4/before.png',
			                onclick: function (){
			                    if(dataList.length > 1){
			                    	var lastData = dataList[dataList.length-2];
			                    	if(lastData && lastData.pieData){
			                         	dataList.splice(dataList.length-1,1);
				            			initPieEcharts(lastData.xAxisData, lastData.pieData, lastData.orgNm);
				    					initBarEcharts(lastData.xAxisData, lastData.seriesData);
			                    	}
			                    }else{
			                    	BIONE.tip("已经是最顶层子机构，无法继续向上");
			                    }
			                }
			            }
			        }
			    },
			    series : [
			        {
			            name: '机构数据下钻',
			            type: 'pie',
			            radius : '55%',
			            center: ['50%', '60%'],
			            data: pieData,
			            itemStyle: {
			                emphasis: {
			                    shadowBlur: 10,
			                    shadowOffsetX: 0,
			                    shadowColor: 'rgba(0, 0, 0, 0.5)'
			                }
			            }
			        }
			    ]
			};
		if(pieChart){
			echarts.dispose(pieChart);
		}
		pieChart = echarts.init(document.getElementById('pieChartDiv'));
		pieChart.on('click', function (params) {
			var drillOrgNm = params.name;
			idxOrgDrill(drillOrgNm);
		});
		pieChart.setOption(option);
	}
	
	function initBarEcharts(xAxisData, seriesData){
		var option = {
				color: ['#3398DB'],
			    tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			        data:['指标数据']
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            dataView : {show: true, readOnly: false},
			            magicType : {show: true, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true},
			            myTool: {
			                show: true,
			                title: '上一层',
			                icon: 'image://${ctx}/js/echarts4/before.png',
			                onclick: function (){
			                    if(dataList.length > 1){
			                    	var lastData = dataList[dataList.length-2];
			                    	if(lastData && lastData.pieData){
			                         	dataList.splice(dataList.length-1,1);
				            			initPieEcharts(lastData.xAxisData, lastData.pieData, lastData.orgNm);
				    					initBarEcharts(lastData.xAxisData, lastData.seriesData);
			                    	}
			                    }else{
			                    	BIONE.tip("已经是最顶层子机构，无法继续向上");
			                    }
			                }
			            }
			        }
			    },
			    xAxis : [
			        {
			            type : 'category',
			            data : xAxisData
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series: [
			        {            
			        	name:'指标数据',
			            type:'bar',
			            barWidth: '40',
			            data:seriesData,
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
		if(barChart){
			echarts.dispose(barChart);
		}
		barChart = echarts.init(document.getElementById('barChartDiv'));
		barChart.on('click', function (params) {
			var drillOrgNm = params.name;
			idxOrgDrill(drillOrgNm);
		});
		barChart.setOption(option);
	}
</script>
</head>
<body>
	<div id="template.center" style="width: 100%;height: 100%;">
		<div id="pieChartDiv" style="width: 50%;height: 100%;float:left"></div>
		<div id="barChartDiv" style="width: 50%;height: 100%;float:left"></div>
	</div>
</body>
</html>