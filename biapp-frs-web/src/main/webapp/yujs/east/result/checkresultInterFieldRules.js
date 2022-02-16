//初始化界面,菜单配置url路径是【/frs/yufreejs?js=/yujs/east/warningLevel.js】
var table = "";
function AfterInit(){
	var colName = jso_OpenPars.col_name; 
	var tabNm = jso_OpenPars.tab_name;
	var jso_par = {"SQLWhere": "col_name='"+colName+"' and summary.tab_name='"+tabNm+"'"};
	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.EastCheckResultAllHisBS","getInterFieldRulesChart",jso_par); 
	var data = jso_data.tablehtml;  //返回的html
	//图表
	var sb_html = "<div>" +
					"<div id='mainchart'></div>" +
	  			  "</div>";
	
	document.getElementById("d1").innerHTML=sb_html;
	
	var charTable = JSON.parse(data);  
	var legendArr = [];
	var seriesData = [];
	var xAxis = charTable.xAxis;
	var yAxis = charTable.yAxis;
	var legend = charTable.legend;
	var seriesData=[];
	for(var obj in yAxis){
		var series = new Object();
		series.name = legend[obj];
		series.data = yAxis[obj];
		series.type = "bar";
		seriesData.push(series);
	}
	
	$("#mainchart").css("height", "300px");
	var myChart = echarts.init(document.getElementById('mainchart'));
	// 折线、柱状图
	option = {
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
				data : legend
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
				data : xAxis
			} ],
			yAxis : [ {
				type : 'value'
			} ],
			series : seriesData
		};

	myChart.setOption(option);
}