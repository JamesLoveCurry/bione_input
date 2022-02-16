<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>

<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>AdminLTE 2 | Dashboard</title>
	<!-- Tell the browser to be responsive to screen width -->
	<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
	<!-- Bootstrap 3.3.7 -->
	<link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/bootstrap/css/bootstrap.min.css">
	<!-- Font Awesome -->
	<link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/font-awesome.min.css">
	<!-- Ionicons -->
	<link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/ionicons.min.css">
	<!-- Theme style -->
	<link rel="stylesheet" href="${ctx}/static/css/bower_components/dist/css/AdminLTE.min.css">
	<link rel="stylesheet" href="${ctx}/static/css/bower_components/dist/css/skins/_all-skins.min.css">
</head>
<body class="hold-transition skin-blue sidebar-mini">
<!-- Content Header (Page header) -->
	<div class="col-md-12">
		<div class="box">
			<div class="box-header with-border">
				<h3 class="box-title" id="box-title"></h3>
			</div>
			<!-- /.box-header -->
			<!-- /.box-header -->
			<div class="box-body">
				<div class="row">
					<div class="chart" id="mainchart"></div>
					<!-- /.chart-responsive -->
					<div id="maingrid" class="maingrid"></div>
				</div>
			</div>
		</div>
		<!-- /.box -->
	</div>
	<!-- /.col -->

<!-- jQuery 2.2.3 -->
<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/jQuery/jquery-2.2.3.min.js"></script>
<!-- Bootstrap 3.3.6 -->
<script src="${ctx}/static/AdminLTE-With-Iframe/bootstrap/js/bootstrap.min.js"></script>
<!-- FastClick -->
<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/fastclick/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="${ctx}/static/AdminLTE-With-Iframe/dist/js/app.js"></script>
<!--tabs-->
<script src="${ctx}/static/AdminLTE-With-Iframe/dist/js/app_iframe.js"></script>
<!--echarts-->
<script src="${ctx}/js/echarts4/js/echarts.js"></script>
<script src="${ctx}/js/echarts4/js/shine.js"></script>

<%@ include file="/common/ligerUI_load_BS.jsp"%>

<script type="text/javascript">

	var moduleType = "${moduleType}";
	//图标类型
	var chartType = "${chartType}";
	var seriesType;
	if(chartType == "01"){
		seriesType = "line";
	}
	else if(chartType == "02"){
		seriesType = "bar";
	}
	else{
		seriesType = "pie";
	}
	//图标类型end
	var moduleDefine = ${moduleDefine};
	var title = moduleDefine.title;
	$('#box-title').html(title); //给title赋值
	var seriesData = [];
	var yAxis = moduleDefine.yAxis;
	var legendArr = [];
	//显示poc图表方法
	if(moduleType == "01"){
		if(chartType == "01" || chartType == '02'){
			var xAxisData = moduleDefine.xAxis;
			for(var obj in yAxis){
				var series = new Object();
				series.name = yAxis[obj];
				series.type = seriesType;
				series.areaStyle = {};
				series.data = getRandomArrData(xAxisData.length, 11);//给y轴数据随机设置
				seriesData.push(series);
			}
		}
		else{
			for(var obj in yAxis){
				var series = new Object();
				series.name = yAxis[obj];
				series.value = getRandomData(11);
				seriesData.push(series);
			}
		}
		legendArr = yAxis;
	}
	else if(moduleType == "02"){
		if(chartType == "01" || chartType == '02'){
			var xAxisData = moduleDefine.xAxis;
			for(var obj in yAxis){
				legendArr.push(yAxis[obj].name);
				var series = new Object();
				series.name = yAxis[obj].name;
				series.type = seriesType;
				series.areaStyle = {};
				series.data = yAxis[obj].data;//给y轴数据随机设置
				seriesData.push(series);
			}
		}
		else{
			for(var obj in yAxis){
				legendArr.push(yAxis[obj].name);
				var series = new Object();
				series.name = yAxis[obj].name;
				series.value = yAxis[obj].data;
				seriesData.push(series);
			}
		}
	}
	else if(moduleType == "11"){
		
	}
	
	if(moduleType == "01" || moduleType == "02"){
		//基于准备好的dom，初始化echarts实例
		$("#mainchart").css("height", "300px");
		var myChart = echarts.init(document.getElementById('mainchart'));
		// 指定图表的配置项和数据
		// 折线、柱状图
		option1 = {
			tooltip : {
				trigger : 'axis',
				axisPointer : {
					type : 'cross',
					label : {
						backgroundColor : '#6a7985'
					}
				}
			},
			legend : {
				x: 'left',
				data : legendArr
			},
			toolbox : {
	
			},
			grid : {
				left : '3%',
				right : '4%',
				bottom : '3%',
				containLabel : true
			},
			xAxis : [ {
				type : 'category',
				boundaryGap : true,
				data : xAxisData
			} ],
			yAxis : [ {
				type : 'value'
			} ],
			series : seriesData
		};
		// 折线、柱状图
		option2 = {
		    tooltip: {
		        trigger: 'item',
		        formatter: "{a} <br/>{b}: {c} ({d}%)"
		    },
		    legend: {
		        orient: 'vertical',
		        x: 'left',
		        data : legendArr
		    },
		    series: [
		        {
		            name:'',
		            type:'pie',
		            radius: ['50%', '70%'],
		            avoidLabelOverlap: false,
		            label: {
		                normal: {
		                    show: false,
		                    position: 'center'
		                },
		                emphasis: {
		                    show: true,
		                    textStyle: {
		                        fontSize: '16',
		                        fontWeight: 'bold'
		                    }
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: false
		                }
		            },
		            data: seriesData
		        }
		    ]
		};
		// 使用刚指定的配置项和数据显示图表。
		if(chartType =='01' || chartType == '02'){
			myChart.setOption(option1);
		}
		else{
			myChart.setOption(option2);
		}
		//自适应窗口调整
		window.onresize = myChart.resize;
		
	}
	else if(moduleType == "11"){
		var xAxis = moduleDefine.xAxis;
		var yAxis = moduleDefine.yAxis;
		var columns = moduleDefine.columns;
		
		var columnsTmp = [];
		for(var i=0;i<xAxis.length;i++){
			var obj = new Object();
			obj.display = xAxis[i];
			obj.name = columns[i];
			obj.width = "16%";
			obj.align = 'left';
			columnsTmp.push(obj);
		}
		var dataRows = {};
		dataRows.Rows = yAxis;
		//初始化表格
		grid = $("#maingrid").ligerGrid({
			toolbar : {},
			checkbox : true,
			columns : columnsTmp,
			dataAction : 'local',//从本地获取数据
			alternatingRow : true, //附加奇偶行效果行
			colDraggable : true,
			sortOrder : 'asc', //排序的方式
			rownumbers : true,
			width:'97%',
			data: dataRows
		});
		BIONE.addSearchButtons(grid);
	}
	//总体结束
	
	//随机生成数据
	function getRandomArrData(length,max){
		var dataArr = [];
		for(var i=0;i<length;i++){
			dataArr.push(Math.floor(Math.random()*max));
		}
		return dataArr;
	}
	
	//随机生成数据
	function getRandomData(max){
		return Math.floor(Math.random()*max); 
	}
	
</script>

</body>
</html>