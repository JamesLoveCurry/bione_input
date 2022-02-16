<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<meta name="description" content="morris charts">
<script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
<script type="text/javascript">
$(function() {
	initData();
});
function initChart(data) {
	var myChart = echarts.init(document.getElementById('chart'));
	var option = {
		title : {
			text : '报表使用率',
			x : 'center'
		},
		tooltip : {
			trigger : 'item',
			formatter : "{a} <br/>{b} : {c} ({d}%)"
		},
		legend : {
			x : 'left',
			y : 'bottom',
			data : data.leg
		},
		series : [ {
			name : '点击次数',
			type : 'pie',
			radius : [0,'40%'],
			label:{
				normal:{
					position:'inner'
				}
			},
			labelLine:{
				normal:{
					show:false
				}
			},
			center : [ '50%', '60%' ],
			data : data.num
		},{
			name:'访问人数',
			type:'pie',
			radius:['45%','55%'],
			center : [ '50%', '60%' ],
			data: data.pers
			
		} ]
	};
	myChart.setOption(option);
}
function initData() {
		$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/frame/access/info.json",
					dataType : 'json',
					type : "POST",
					success : function(result) {
						var leg = new Array(); 
						var num = new Array(); 
						var pers = new Array();
						result = result.Rows;
						for(i=0;i<result.length;i++){
							leg.push(result[i].rptNm);
							num.push({name:result[i].rptNm,value:result[i].pv})
							pers.push({name:result[i].rptNm,value:result[i].uv})
						}
						var data = {leg:leg,num:num,pers:pers}
						initChart(data);
					}
				});
}
</script>
</head>
<body>
	<div id="template.center">
		<div id="left" style="width: 50%; height: 100%; float: left">
			<div id="chart" class="chart" style="width: 100%; height: 100%;"></div>
		</div>
		<div id="right" style="width: 50%; float: left">
			<form id="mainform" action="${ctx}/report/frame/engine/log/node"
				method="POST"></form>
		</div>

	</div>
</body>
</html>