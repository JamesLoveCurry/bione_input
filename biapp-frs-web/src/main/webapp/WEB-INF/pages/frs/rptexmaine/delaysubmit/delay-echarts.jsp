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
		var delayData = [];
		var errorData = [];
		var rebutData = [];
		var rptType = '${rptType}';
		var orgNo = '${orgNo}'
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frs/rptexmaine/control/queryDelayCount?rptType=" + rptType+"&orgNo="+orgNo,
			type : "GET",
			dataType : "json",
			success : function(result){
				for(var i=0; i<result.length; i++){
					dateData.push(result[i].DATA_DATE);
					delayData.push(result[i].DELAY_COUNT);
					errorData.push(result[i].ERROR_COUNT);
					rebutData.push(result[i].REBUT_COUNT);
				}
			}
		 });
		var option = {
			    title: {
			        text: '迟漏报次数变化图'
			    },
			    tooltip : {
			        trigger: 'axis',
			        axisPointer: {
			            label: {
			                backgroundColor: '#6a7985'
			            }
			        }
			    },
			    legend: {
			        data:['迟漏报次数','错报次数','驳回次数']
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
			        containLabel: true
			    },
			    xAxis : [
			        {
			            type : 'category',
			            boundaryGap : false,
			            data : dateData
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        {
			            name:'迟漏报次数',
			            type:'line',
			            stack: '总量',
			            areaStyle: {
			            	normal: {
			            		color: '#e69d87' //改变区域颜色
			            	}
			        	},
			        	itemStyle: {
			        		normal : { 
			        			color:'#e69d87', //改变折线点的颜色
			        			lineStyle:{ 
				        			color:'#e69d87' //改变折线颜色
				        		} 
			        		} 
			        	},
			            data: delayData
			        },
			        {
			            name:'错报次数',
			            type:'line',
			            stack: '总量',
			            // label: {
			            //     normal: {
			            //         show: true,
			            //         position: 'top',
			            //         formatter: function (obj) {
			        	// 			return delayData[obj.dataIndex] + errorData[obj.dataIndex];
			            //         }
			            //     }
			            // },
			            areaStyle: {
			            	normal: {
			            		color: '#5299c1' //改变区域颜色
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
			            data: errorData
			        },
					{
						name:'驳回次数',
						type:'line',
						stack: '总量',
						label: {
							normal: {
								show: true,
								position: 'top',
								formatter: function (obj) {
									return delayData[obj.dataIndex] + errorData[obj.dataIndex] + rebutData[obj.dataIndex];
								}
							}
						},
						areaStyle: {
							normal: {
								color: '#f39c12' //改变区域颜色
							}
						},
						itemStyle: {
							normal : {
								color:'#f39c12', //改变折线点的颜色
								lineStyle:{
									color:'#f39c12' //改变折线颜色
								}
							}
						},
						data: rebutData
					}
			    ]
			};
		 var myChart = echarts.init(document.getElementById('delayEcharts'));
		 // 使用刚指定的配置项和数据显示图表。
	     myChart.setOption(option);
	</script>
</body>
</html>