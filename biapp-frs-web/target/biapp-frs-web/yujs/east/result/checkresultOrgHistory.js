//初始化界面,菜单配置url路径是【/frs/yufreejs?js=/yujs/east/warningLevel.js】
var table = "";
function AfterInit(){
	JSPFree.createSplit("d1","上下",60);  
	JSPFree.createBillQuery("d1_A","/biapp-east/freexml/east/result/east_cr_summary_q.xml");
	document.getElementById("d1_B").innerHTML="<div style=\"width:100%;text-align:center;\">请输入条件查询数据!</div>";
	
	JSPFree.bindQueryEvent(d1_A_BillQuery,function(_par){
		if(_par==null){
			return;
		}
		query(jso_OpenPars, _par);
	});

	query(jso_OpenPars, null);
}

function query(jso_OpenPars, _par){
	var orgNm = jso_OpenPars.org_nm; 
	var date1 = jso_OpenPars.data_dt;
	var tabName = jso_OpenPars.tab_name;
	var orgNo = jso_OpenPars.org_no;
	var orgType = jso_OpenPars.org_type;
	var orgClass = jso_OpenPars.org_class;
	
	var jso_par = null;
	var busiType = "";
	if (orgType == "Y") {
		if (orgClass == "总行") {
			busiType = "1_" + orgNo;
		} else if (orgClass == "分行") {
			busiType = "2_" + orgNo;
		}
	} else {
		busiType = "1_";
	}
	
	var condition = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.east.checkrule.rulesummary.service.ValidateQueryCondition","getQueryCondition",{"_loginUserOrgNo" : orgNo});
	if(jso_rt.msg == "ok"){
		condition = jso_rt.condition;
	}

	if(_par != null && _par != ""){
		jso_par = {"SQLWhere": "org_nm='"+orgNm+"' and busi_type like '"+busiType+"%' and tab_name='"+tabName+"' and "+_par+" and "+condition};
	} else{
	    var date2 = getPre6Month(date1);
	   	var _par = "(data_dt >='"+date2+"' and data_dt<='"+date1+"') and "+condition;
		jso_par = {"SQLWhere": "org_nm='"+orgNm+"' and busi_type like '"+busiType+"%' and tab_name='"+tabName+"' and "+_par};
	}
	
	var jso_data = JSPFree.doClassMethodCall("com.yusys.east.checkresult.summary.service.EastCheckResultAllHisBS","getOrgHistoryChart",jso_par); 
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

function getPre6Month(date) {  
	var arr = date.split('-');  
	var year = arr[0]; //获取当前日期的年份  
	var month = arr[1]; //获取当前日期的月份  
	var day = arr[2]; //获取当前日期的日  
	var days = new Date(year, month, 0);  
	days = days.getDate(); //获取当前日期中月的天数  
	var year2 = year;  
	var month2 = parseInt(month) - 6;  
	if (month2 == 0) {  
	    year2 = parseInt(year2) - 1;  
	    month2 = 12;  
	}  
	var day2 = day;  
	var days2 = new Date(year2, month2, 0);  
	days2 = days2.getDate();  
	if (day2 > days2) {  
	    day2 = days2;  
	}  
	if (month2 < 10) {  
	    month2 = '0' + month2;  
	}  
	var t2 = year2 + '-' + month2 + '-' + day2;  
	return t2;  
}