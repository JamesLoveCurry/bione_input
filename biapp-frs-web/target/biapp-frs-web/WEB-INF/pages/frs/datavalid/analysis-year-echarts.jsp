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
		var indexVal = [];
		var indexNo = '${indexNo}';
		var orgNo = '${orgNo}';
		var unit = '${unit}';
		var indexNm = '${indexNm}';
		var rptNm = '${rptNm}';
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frs/verificationWarning/analysisByYear?indexNo=" + indexNo + "&orgNo=" + orgNo,
			type : "GET",
			dataType : "json",
			success : function(result){
				for(var i=0; i<result.mapList.length; i++){
					dateData.push(result.mapList[i].dataDate);
					indexVal.push(result.mapList[i].indexVal);
				}
			}
		 });
		var option = {
			    title: {
			        text: '近一年指标值变化趋势图',
			        left: 'center',
			        align: 'right',
			        subtext: rptNm + "【"+indexNm+"】"
			    },
			    tooltip : {
			        trigger: 'axis',
					axisPointer: { // 坐标轴指示器，坐标轴触发有效
						type: 'line' // 默认为直线，可选为：'line' | 'shadow'
					}
			    },
			    grid: {
			        left: '3%',
			        right: '8%',
			        bottom: '3%',
			        containLabel: true
			    },
			    xAxis : [
			        {
			            type : 'category',
			            boundaryGap : false,
						splitLine : { show: false },
			            data : dateData
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value',
			            name : '指标值('+unit+")"
			        }
			    ],
			    series : [
			        {
			            name:'指标值',
			            type:'line',
			            data: indexVal,
			            smooth: true,
						markLine: {
							data: [{
								name: '平均值',
								type: 'average'
							}]
						}
			        }
			    ]
			};
		 var myChart = echarts.init(document.getElementById('delayEcharts'));
		 // 使用刚指定的配置项和数据显示图表。
	     myChart.setOption(option);
	</script>
</body>
</html>