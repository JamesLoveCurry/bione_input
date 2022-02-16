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
	<div id="mychart" style="width: 100%;">
	</div>
	<script type="text/javascript">
		var dateArray = [];
		var indefaultArray = [];
		var outdefaultArray = [];
		var warnDefaultArray =[];
		var taskId = '${taskId}';
		var dataDate = '${dataDate}';
		var orgNo = '${orgNo}'
		var rptId = '${rptId}';
		var height = $(window.parent).height()-80;
		$("#mychart").height(height);
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/frs/validatereport/getValidChart",
			type : "POST",
			dataType : "json",
			data : {
				taskId : taskId,
				dataDate : dataDate,
				orgNo : orgNo,
				rptId : rptId
			},
			success : function(result){
				var data = result.validList;
				for(var i=0; i<data.length; i++){
					dateArray.push(data[i].DATADATE);
					indefaultArray.push(data[i].INDEFAULTCOUNT);
					outdefaultArray.push(data[i].OUTDEFAULTCOUNT);
					warnDefaultArray.push(data[i].WARNDEFAULTCOUNT);
				}
			}
		 });
		var option = {
			    tooltip : {
			        trigger: 'axis',
			        axisPointer: {
			            label: {
			                backgroundColor: '#6a7985'
			            }
			        }
			    },
			    legend: {
			        x:'center',	//可设定图例在左、右、居中
			        y:'top',
			        padding:[30,0,0,0],
			        data:['表内校验','表间校验','预警校验']
			    },
			    toolbox: {
			        feature: {
			            saveAsImage: {}
			        }
			    },
			    grid: {
			        left: '3%',
			        right: '10%',
			        bottom: '3%',
			        containLabel: true
			    },
			    xAxis : [
			        {
			            type : 'category',
			            boundaryGap : false,
			            data : dateArray
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        {
			            name:'表内校验',
			            type:'line',
			            stack: '总量',
			            areaStyle: {
			            	normal: {
			            		color: '#E9596F' //改变区域颜色
			            	}
			        	},
			        	itemStyle: {
			        		normal : { 
			        			color:'#E9596F', //改变折线点的颜色
			        			lineStyle:{ 
				        			color:'#E9596F' //改变折线颜色
				        		} 
			        		} 
			        	},
			            data: indefaultArray
			        },
			        {
			            name:'表间校验',
			            type:'line',
			            stack: '总量',
			            label: {
			                normal: {
			                    show: true,
			                    position: 'top',
			                    formatter: function (obj) { 
			        				return indefaultArray[obj.dataIndex] + outdefaultArray[obj.dataIndex];
			                    }
			                }
			            },
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
			            data: outdefaultArray
			        },
			        {
			            name:'预警校验',
			            type:'line',
			            smooth: true,
			            /* stack: '总量',
			            areaStyle: {
			            	normal: {
			            		color: '#e69d87' //改变区域颜色
			            	}
			        	}, */
			        	itemStyle: {
			        		normal : { 
			        			color:'#e69d87', //改变折线点的颜色
			        			lineStyle:{ 
				        			color:'#e69d87' //改变折线颜色
				        		} 
			        		} 
			        	},
			            data: warnDefaultArray
			        }
			    ]
			};
		 var myChart = echarts.init(document.getElementById("mychart"));
		 // 使用刚指定的配置项和数据显示图表。
	     myChart.setOption(option);
	</script>
</body>
</html>