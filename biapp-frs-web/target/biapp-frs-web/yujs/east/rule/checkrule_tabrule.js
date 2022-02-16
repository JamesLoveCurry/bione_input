function AfterInit(){
	//第1个页签
	JSPFree.createSplit("d1","上下",60);  
	JSPFree.createBillQuery("d1_A","/biapp-east/freexml/east/report/east_cr_rule_ref.xml");
	document.getElementById("d1_B").innerHTML="<div style=\"width:100%;text-align:center;\">请输入条件查询数据!</div>";
	
	JSPFree.bindQueryEvent(d1_A_BillQuery,function(_par){
		//console.log("查询框传入的查询条件[" + _par + "]");
		var jso_par = {SQLWhere:_par, isChart:true};
		var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.RuleReportBS","getRuleTableReport",jso_par); 
		var str_html = jso_data.tablehtml;  //返回的html	
		//图表
		  var sb_html = "<div>" +
		  					"<div id='mainchart1'></div>" +
		  				"</div>" +
		  				"<div>" +
		  					"<div id='mainchart2'></div>" +
		  				"</div> " +
		  			    "<div>" +
		  			    	"<div id='mainchart3' style='float:left;width : 50%;'></div>" + 
		  			    	"<div id='mainchart4' style='float:left;width : 50%;'></div>" +
		  			    "</div>";
		var html = sb_html+str_html.substring(0,str_html.indexOf("{")-1);
		document.getElementById("d1_B").innerHTML=html;
		
		var charTable = JSON.parse(str_html.substring(str_html.indexOf("{")));  
		var legendArr = [];
		var seriesData = [];
		var xAxis1 = charTable.xAxis1;
		var yAxis1 = charTable.yAxis1;
		var xAxis2 = charTable.xAxis2;
		var yAxis2 = charTable.yAxis2;
		var seriesData1=[];
		var seriesData2=[];
		for(var obj in yAxis1){
			var series = new Object();
			series.name = xAxis1[obj];
			series.value = yAxis1[obj];
			seriesData1.push(series);
		}
		for(var obj in yAxis2){
			var series = new Object();
			series.name = xAxis2[obj];
			series.value = yAxis2[obj];
			seriesData2.push(series);
		}
		
		$("#mainchart1").css("height", "300px");
		var myChart1 = echarts.init(document.getElementById('mainchart1'));
		// 折线、柱状图
		option1 = {
				title: {
			        text: '规则'
			    },
			    color: ['#3398DB'],
			    tooltip : {
			        trigger: 'axis',
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
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
			            data : xAxis1,
			            axisTick: {
			                alignWithLabel: true
			            }
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        {
			            name: '',
			            type: 'bar',
			            barWidth: '60%',
			            data: yAxis1
			        }
			    ]
			};

		myChart1.setOption(option1);
		
		$("#mainchart2").css("height", "300px");
		var myChart2 = echarts.init(document.getElementById('mainchart2'));
		option2 = {
				title: {
			        text: '表名'
			    },
			    color: ['#3398DB'],
			    tooltip : {
			        trigger: 'axis',
			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
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
			            data : xAxis2,
			            axisTick: {
			                alignWithLabel: true
			            }
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series : [
			        {
			            name: '',
			            type: 'bar',
			            barWidth: '60%',
			            data: yAxis2
			        }
			    ]
			};
		myChart2.setOption(option2);
		
		$("#mainchart3").css("height", "300px");
		var myChart3 = echarts.init(document.getElementById('mainchart3'));
		option3 = {
				title: {
			        text: '规则'
			    },
			    tooltip: {
			        trigger: 'item',
			        formatter: "{a} <br/>{b}: {c} ({d}%)"
			    },
//			    legend: {
//			        orient: 'vertical',
//			        x: 'left',
//			        data : xAxis1
//			    },
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
			            data: seriesData1
			        }
			    ]
			};
		myChart3.setOption(option3);
		
		$("#mainchart4").css("height", "300px");
		var myChart4 = echarts.init(document.getElementById('mainchart4'));
		option4 = {
				title: {
			        text: '表名'
			    },
			    tooltip: {
			        trigger: 'item',
			        formatter: "{a} <br/>{b}: {c} ({d}%)"
			    },
//			    legend: {
//			        orient: 'vertical',
//			        x: 'left',
//			        data : xAxis2
//			    },
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
			            data: seriesData2
			        }
			    ]
			};
		myChart4.setOption(option4);
	});	
}
