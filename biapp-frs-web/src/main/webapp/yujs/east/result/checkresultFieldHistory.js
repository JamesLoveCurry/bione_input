//初始化界面,菜单配置url路径是【/frs/yufreejs?js=/yujs/east/warningLevel.js】
var table = "";
function AfterInit(){
	JSPFree.createSplit("d1","上下",60);  
	JSPFree.createBillQuery("d1_A","/biapp-east/freexml/east/result/east_cr_summary_q.xml");
	document.getElementById("d1_B").innerHTML="<div style=\"width:100%;text-align:center;\">请输入条件查询数据!</div>";
	JSPFree.bindQueryEvent(d1_A_BillQuery,function(_par){
		query(jso_OpenPars, _par);
	});
	query(jso_OpenPars, null);
}

function query(jso_OpenPars, _par){
	var tabName = jso_OpenPars.col_name;
	if(_par != null && _par != ""){
		var jso_par = {"SQLWhere": "col_name='"+tabName+"' and "+_par};
	}
	else{
		var date=new Date();
		//年
	    var year=date.getFullYear();
	    //月
	    var month1;
	    var month = date.getMonth()+"";
	    if(month.length == 1){
			month1 = "0"+(date.getMonth()+1);
	    }
	    else{
	    	month1 = date.getMonth()+1;
	    }
	    //日
	    var day=date.getDate();
	    var date1 = year+"-"+month1+"-"+day;
	    var date2 = getDateTime();
	   	var _par = "(data_dt >='"+date2+"' and data_dt<='"+date1+"')";
		var jso_par = {"SQLWhere": "col_name='"+tabName+"' and "+_par};
	}
	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.EastCheckResultAllHisBS","getFieldHistoryChart",jso_par); 
	var data = jso_data.tablehtml;  //返回的html
	//图表
	var sb_html = "<div>" +
					"<div id='mainchart'></div>" +
	  			  "</div>";
	
	document.getElementById("d1_B").innerHTML=sb_html;
	
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
		series.type = "line";
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

function getDateTime(){
    // 创建日期对象，并初始化，完成文本转日期
    var date = new Date();
    var year= date.getFullYear();//年
    var month= date.getMonth() - 5;//  因为js里month从0开始，所以要加1
    if (month <= 0){
        year--;
        month += 12;
    }
    if(month<10){
        month="0"+month;
    }
    var date2=new Date(year,month,0);//新的年月
    var day1=date.getDate();
    var day2=date2.getDate();
    if(day1>day2){  //防止+6月后没有31天
       day1=day2;
    }
    var str = year + '-'
        + month + '-'
        + day1;
    //最后赋值文本框显示
    return str;
}