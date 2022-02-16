<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<!-- <meta name="decorator" content="/template/template9.jsp"> -->
<script type="text/javascript" src="${ctx}/js/echarts/js/jquery.min.js"></script>
<script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
</head>
<body>
	<div id="delayEcharts" style="width: 100%; height: 400px;">
	</div>
	<script type="text/javascript">
		var dateData = [];
		var errorCount = [];
		var successCount = [];
		var indexNo = '${indexNo}'
		var orgNo = '${orgNo}'
		var orgNm = '${orgNm}'
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frs/verificationWarning/warnAnalysisByOrg?orgNo=" + orgNo,
			type : "GET",
			dataType : "json",
			success : function(result){
				for(var i=0; i<result.mapList.length; i++){
					dateData.push(result.mapList[i].month);
					errorCount.push(result.mapList[i].errorCount);
					successCount.push(result.mapList[i].successCount);
				}
			}
		 });
		var option = {
			    title: {
			    	text: '近一年校验结果统计',
			        left: 'center',
			        align: 'right',
			        subtext: orgNm
			    },
			    tooltip : {
			    	feature: {
			            magicType: {
			                type: ['stack', 'tiled']
			            },
			            dataView: {}
			        }
			    },
			    legend: {
			        data: ['不通过数','通过数'],
			        left: '10%'
			    },
			    toolbox: {
			        feature: {
			    	saveAsImage: {}
			    	}
			    },
			    grid: {
			        left: '3%',
			        right: '4%',
			        bottom: '3%',
					height: 'auto',
					width: 'auto',
			        containLabel: true
			    },
			    xAxis : [
			        {
			            type : 'category',
			            data : dateData,
						axisTick: {
							alignWithLabel: true
						},
						splitLine : { show: false }
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        {
			            name:'不通过数',
			            type:'bar',
						barWidth : 20,//柱图宽度
						label: {
							normal: {
								show: true,
								position: 'top'
							}
						},
			            itemStyle: {
					    	normal : {
					    		color:'#5299c1', //改变折线点的颜色
					    		lineStyle:{
								color:'#5299c1' //改变折线颜色
								}
					    	}
					    },
					    data: errorCount
			        }/* ,
			        {
			            name:'通过数',
			            type:'bar',
			            stack: '总数',
						barWidth : 20,//柱图宽度
			            data: successCount,
			            color: '#5299c1'
			        } */
			    ]
			};
		 var myChart = echarts.init(document.getElementById('delayEcharts'));
		 // 使用刚指定的配置项和数据显示图表。
	     myChart.setOption(option);
	</script>
</body>
</html>