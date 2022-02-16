/**
 * 
 * <pre>
 * Title: 检核结果数据表排名
 * Description: 【历史】按钮操作
 * </pre>
 * @author wangxy31 
 * @version 1.00.00
   @date 2020年8月12日
 */

var tabName = null; 
var dataDt = null;
var orgNo = null;
var orgType = null;
var orgClass = null;

function AfterInit() {
	JSPFree.createSplit("d1", "上下", 60);  
	JSPFree.createBillQuery("d1_A", "/biapp-bfd/freexml/resultsummary/bfd_result_summary_q.xml");
	document.getElementById("d1_B").innerHTML = "<div style=\"width:100%;text-align:center;\">请输入条件查询数据!</div>";
	
	tabName = jso_OpenPars.tab_name; 
	dataDt = jso_OpenPars.data_dt;
	orgNo = jso_OpenPars.org_no;
	orgType = jso_OpenPars.org_type;
	orgClass = jso_OpenPars.org_class;

	JSPFree.bindQueryEvent(d1_A_BillQuery,function(_par) {
		if(_par==null){
			return;
		}
		queryData(jso_OpenPars, _par);
	});
	
	queryData(jso_OpenPars, null);
}

/**
 * 查询方法
 * @param jso_OpenPars
 * @param _par
 * @returns
 */
function queryData(jso_OpenPars, _par) {
	var jso_par = null;

	var busiType = "";
	if (orgType == "Y") {
		if (orgClass == BfdFreeUtil.getBfdOrgClass().zh) {
			busiType = "1_" + orgNo;
		} else if (orgClass == BfdFreeUtil.getBfdOrgClass().fh) {
			busiType = "2_" + orgNo;
		}
	} else {
		busiType = "1_";
	}
	
	// 报送行机构号，转换成当前用户所属机构号
	var loginUserOrgNo = "";
	var jso_rt = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS", "getUserOrgNoCondition", {"orgNo" : orgNo});
	if (jso_rt.msg == "ok") {
		loginUserOrgNo = jso_rt.data;
	}
	
	var condition = "";
	var jso_con = JSPFree.doClassMethodCall("com.yusys.bfd.common.service.BfdValidateQueryConditionBS", "getQueryCondition", {"_loginUserOrgNo" : loginUserOrgNo});
	if (jso_con.msg == "ok") {
		condition = jso_con.condition;
	}
	
	if (_par != null && _par != "") {
		jso_par = {"SQLWhere": "tab_name='" + tabName + "' and busi_type like '" + busiType + "%' and " + _par + " and " + condition};
	} else {
		var date1 = dataDt;
	    var date2 = getPre6Month(date1);
	   	var _par = "(data_dt >='" + date2 + "' and data_dt<='" + date1 + "') and " + condition;
		jso_par = {"SQLWhere": "tab_name='" + tabName + "' and busi_type like '" + busiType + "%' and " + _par};
	}
	
	var jso_data = JSPFree.doClassMethodCall("com.yusys.bfd.result.service.BfdResultSummaryBS", "getResultTabHistoryChart", jso_par); 
	var data = jso_data.tablehtml; // 返回的html
	// 图表
	var sb_html = "<div> <div id='mainchart'></div> </div>";
	
	document.getElementById("d1_B").innerHTML = sb_html;
	
	var charTable = JSON.parse(data);  
	var legendArr = [];
	var seriesData = [];
	var xAxis = charTable.xAxis;
	var yAxis = charTable.yAxis;
	var legend = charTable.legend;
	var seriesData=[];
	
	for (var obj in yAxis) {
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

/**
 * 获取近6个月日期
 * @param date
 * @returns
 */
function getPre6Month(date) {  
	var arr = date.split('-');  
	var year = arr[0]; // 获取当前日期的年份  
	var month = arr[1]; // 获取当前日期的月份  
	var day = arr[2]; // 获取当前日期的日  
	var days = new Date(year, month, 0);  
	days = days.getDate(); // 获取当前日期中月的天数  
	
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