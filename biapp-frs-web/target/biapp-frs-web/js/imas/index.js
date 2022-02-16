window.onload=function(){

    // 初始化多选下拉框
    $('.js-example-basic-multiple').select2();

    // 初始化日期选择框为当天
	initSearchDate();
	// 初始化获取报送机构
    initReportOrg();
    // 加载数据
    showInfoByData();
    // 报表总数，月底37张，其他时间36张
	var rptAllCount;
    // 进度条页数标识
    var creStepFlag;
    var orgType;
    var setObj = null;
    //setCharts1();
    initMsgType();
    var notice = getNotice();
    if(notice != null){
        noticeDetail(notice);//显示公告明细
    }

    //var result = latelyChackResult();//最新1期校验结果
    //if(result != null){
    //    setCharts(result);
    //}
    //if(dateTime != null) {
      //  orderByCheckResult("data_dt='"+dateTime+"'",true);//检核结果排名
   // } else {
      //  orderByCheckResultNull();
    //}

};
// 选择日期进行查看报表下发状态
function showInfoByData () {
    creStepFlag = 1;
    var dtDate = $("#dtDate").val();
    // 获取最迟上报时间与剩余时间
    getReportTime(dtDate);
    if(dtDate != "") {
        if (isLastDay(dtDate)) {
            rptAllCount = "37";
        } else {
            rptAllCount = "36";
        }
        var rptOrg = $("#reportOrg").val();
        if (rptOrg != null) {
            if(rptOrg.length == 1) {
                if (rptOrg[0] == "all") {
                    // 如果为all，则说明为汇总机构，需要查询全部
                    orgType = "all";
                    setCharts3("");
                } else {
                    orgType = "";
                    setCharts2(rptOrg[0]);
                }
            } else {
                orgType = "";
                var rptOrgStr = '';
                for (var l = 0;l< rptOrg.length;l++) {
                    rptOrgStr += rptOrg[l] + ";"
                }
                setCharts3(rptOrgStr);
            }
        }
    }
}
var setStep1 = function() {
	$("#setDiv").empty().html("<div class=\"stepCont stepCont3 report-step\">	<div class='ystep-container ystep-lg ystep-blue'></div></div>");
	setObj = new SetStep({
        content:'.stepCont3',
        showBtn:false,
        steps:['采集','校验','下发','补录','审核','上报'],
        stepCounts:6,//总共的步骤数
        curStep:creStepFlag,//当前显示第几页
        clickAble:false
    })
}

var setStep2 = function() {
    $("#setDiv").empty().html("<div class=\"stepCont stepCont3 report-step\">	<div class='ystep-container ystep-lg ystep-blue'></div></div>");
    setObj = new SetStep({
        content:'.stepCont3',
        showBtn:false,
        steps:['采集','校验','补录','上报'],
        stepCounts:4,//总共的步骤数
        curStep:creStepFlag,//当前显示第几页
        clickAble:false
    })
}

//加载报送进度图表
var setCharts2 = function(rptOrg) {
    changeChartDiv("chart1")

    initCollectionCharts(rptOrg);
	initCheckCharts(rptOrg);
	initIssueCharts(rptOrg);
	initInputCharts(rptOrg);
	initExamineCharts(rptOrg);
	initSubmittedCharts(rptOrg);
    setStep1();
}

var setCharts3 = function(rptOrg) {
    changeChartDiv("chart2");

    initCollectionCharts2(rptOrg);
    initCheckCharts2(rptOrg);
    initInputCharts2(rptOrg);
    initSubmittedCharts2(rptOrg);
    setStep2();
}

// 切换图表类型
function changeChartDiv(id) {
    var msg ="";
    if (id == "chart1"){
        $("#chart2").empty();
        msg += "<div id=\"bar-box1\" class=\"report-box1\"></div>";
        msg += "<div id=\"bar-box2\" class=\"report-box2\"></div>";
        msg += "<div id=\"bar-box3\" class=\"report-box3\"></div>";
        msg += "<div id=\"bar-box4\" class=\"report-box4\"></div>";
        msg += "<div id=\"bar-box5\" class=\"report-box5\"></div>";
        msg += "<div id=\"bar-box6\" class=\"report-box6\"></div>";
        $("#chart1").empty().html(msg);
    } else {
        $("#chart1").empty();
        msg += "<div id=\"bar-box11\" class=\"report-box11\"></div>";
        msg += "<div id=\"bar-box12\" class=\"report-box12\"></div>";
        msg += "<div id=\"bar-box13\" class=\"report-box13\"></div>";
        msg += "<div id=\"bar-box14\" class=\"report-box14\"></div>";
        $("#chart2").empty().html(msg);
    }
}

//初始化采集图表 bar-box1
var initCollectionCharts = function(rptOrg){
	// 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getCollectionResult";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
        	dtDate: dtDate,
            rptOrg: rptOrg
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
	var value1 = parseInt(chartJson.etlCount);
	var value2 = parseInt(chartJson.failCount);
	var value3 = value1 + value2;
	if(value1 == 0) {
		// 如果已检验数据不为0，则设置节点为校验节点
		creStepFlag = 1;
	}
    var option = {
    	    title: {
    	        text: '采集数据：'+ value3 +'条\n\n数据表'+chartJson.rptCount+'/'+rptAllCount,
    	        left: 'center'
    	    },
            tooltip: {
                trigger: 'item',
                position: ['30%', '55%'],
                formatter: "{a} <br/>{b} ({d}%)"
            },
            series: [{
                name: '数据采集统计',
                type: 'pie',
                radius: ['70%', '50%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [{
	                    value: value1,
	                    itemStyle: {
	                        normal: {
	                            color: '#5bc0dd'
	                        }
	                    },
	                    label: {
	                    	show: true,
	                        fontSize: '20',
	                        fontWeight: 'bold'
	                    },
	                    name:"自动采集数\n"+value1
                	},
                    {
                        value: value2,
                        itemStyle: {
                            normal: {
                                color: '#E9596F'
                            }
                        },
                        selected:true,
                        name:"手动补录数\n"+value2
                    }
                ]
            }]
        };
    var myCharts = echarts.init(document.getElementById('bar-box1'));
    myCharts.setOption(option);
}

//初始化校验图表 bar-box2
var initCheckCharts = function(rptOrg){
	// 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getCheckResult";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
        	dtDate: dtDate,
            rptOrg: rptOrg
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
	var value1 = chartJson.checkSum; // 已校验数据
	var value2 = parseInt(chartJson.totalCount) - parseInt(chartJson.checkSum); // 未校验数据
	var value3 = chartJson.totalCount; // 数据总量
	if(value1 != 0) {
		// 如果已检验数据不为0，则设置节点为校验节点
		creStepFlag = 2;
	}
    var option = {
    	    title: {
    	        text: '检验数据：'+ value1 +'条\n\n数据表'+chartJson.rptCount+'/'+rptAllCount,
    	        left: 'center'
    	    },
            tooltip: {
                trigger: 'item',
                position: ['30%', '55%'],
                formatter: "{a} <br/>{b} ({d}%)"
            },
            series: [{
                name: '校验数据统计',
                type: 'pie',
                radius: ['70%', '50%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [{
	                    value: value1,
	                    itemStyle: {
	                        normal: {
	                            color: '#5bc0dd'
	                        }
	                    },
	                    label: {
	                    	show: true,
	                        fontSize: '20',
	                        fontWeight: 'bold'
	                    },
	                    name:"已校验数\n"+value1
	                },
                    {
                        value: value2,
                        itemStyle: {
                            normal: {
                                color: '#E9596F'
                            }
                        },
                        selected:true,
                        name:"未校验数\n"+value2
                    }
                ]
            }]
        };
    var myCharts = echarts.init(document.getElementById('bar-box2'));
    myCharts.setOption(option);
}

//初始化下发图表 bar-box3
var initIssueCharts = function(rptOrg){
	// 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getIssueResult";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
        	dtDate: dtDate,
            rptOrg: rptOrg
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
	var value1 = chartJson.issueCount;
	var value2 = rptAllCount - parseInt(value1);
	if(value1 != 0) {
		// 如果已下发报表不为0，则设置节点为下发节点
		creStepFlag = 3;
	}
    var option = {
    	    title: {
    	        text: '下发报表\n\n数据表'+value1+'/'+rptAllCount,
    	        left: 'center'
    	    },
            tooltip: {
                trigger: 'item',
                position: ['30%', '55%'],
                formatter: "{a} <br/>{b} ({d}%)"
            },
            series: [{
                name: '下发报表统计',
                type: 'pie',
                radius: ['70%', '50%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [{
	                    value: value1,
	                    itemStyle: {
	                        normal: {
	                            color: '#5bc0dd'
	                        }
	                    },
	                    label: {
	                    	show: true,
	                        fontSize: '20',
	                        fontWeight: 'bold'
	                    },
	                    name:"已下发报表\n"+value1
	                },
                    {
                        value: value2,
                        itemStyle: {
                            normal: {
                                color: '#E9596F'
                            }
                        },
                        selected:true,
                        name:"未下发报表\n"+value2
                    }
                ]
            }]
        };
    var myCharts = echarts.init(document.getElementById('bar-box3'));
    myCharts.setOption(option);
}

// 初始化补录图表 bar-box4
var initInputCharts = function(rptOrg){
	// 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getInputResult";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
        	dtDate: dtDate,
            rptOrg: rptOrg
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
	var value1 = chartJson.inputCount;
	var value2 = rptAllCount - parseInt(value1);
	if(value1 != 0) {
		// 如果补录报表不为0，则设置节点为补录节点
		creStepFlag = 4;
	}
    var option = {
    	    title: {
    	        text: '补录报表\n\n数据表'+value1+'/'+rptAllCount,
    	        left: 'center'
    	    },
            tooltip: {
                trigger: 'item',
                position: ['30%', '55%'],
                formatter: "{a} <br/>{b} ({d}%)"
            },
            series: [{
                name: '补录报表统计',
                type: 'pie',
                radius: ['70%', '50%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [{
	                    value: value1,
	                    itemStyle: {
	                        normal: {
	                            color: '#5bc0dd'
	                        }
	                    },
	                    label: {
	                    	show: true,
	                        fontSize: '20',
	                        fontWeight: 'bold'
	                    },
	                    name:"已补录报表\n"+value1
	                },
                    {
                        value: value2,
                        itemStyle: {
                            normal: {
                                color: '#E9596F'
                            }
                        },
                        selected:true,
                        name:"未补录报表\n"+value2
                    }
                ]
            }]
        };
    var myCharts = echarts.init(document.getElementById('bar-box4'));
    myCharts.setOption(option);
}

//初始化审核图表  bar-box5
var initExamineCharts = function(rptOrg){
	// 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getExamineResult";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
        	dtDate: dtDate,
            rptOrg: rptOrg
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
	var value1 = chartJson.examineCount;
	var value2 = rptAllCount - parseInt(value1);
	if(value1 != 0) {
		// 如果审核报表不为0，则设置节点为审核节点
		creStepFlag = 5;
	}
    var option = {
    	    title: {
    	        text: '审核报表\n\n数据表'+value1+'/'+rptAllCount,
    	        left: 'center'
    	    },
            tooltip: {
                trigger: 'item',
                position: ['30%', '55%'],
                formatter: "{a} <br/>{b} ({d}%)"
            },
            series: [{
                name: '审核数据统计',
                type: 'pie',
                radius: ['70%', '50%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [{
	                    value: value1,
	                    itemStyle: {
	                        normal: {
	                            color: '#5bc0dd'
	                        }
	                    },
	                    label: {
	                    	show: true,
	                        fontSize: '20',
	                        fontWeight: 'bold'
	                    },
	                    name:"已审核报表\n"+value1
	                },
                    {
                        value: value2,
                        itemStyle: {
                            normal: {
                                color: '#E9596F'
                            }
                        },
                        selected:true,
                        name:"未审核报表\n"+value2
                    }
                ]
            }]
        };
    var myCharts = echarts.init(document.getElementById('bar-box5'));
    myCharts.setOption(option);
}

// 初始化报送图表   bar-box6
var initSubmittedCharts = function(rptOrg){
	// 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getSubmittedResult";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
        	dtDate: dtDate,
            rptOrg: rptOrg
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
	var value1 = chartJson.reportSum;
	var value2 = parseInt(chartJson.totalCount) - parseInt(chartJson.reportSum);
	var value3 = chartJson.totalCount;
	if(value1 != 0) {
		// 如果已上报条数不为0，则设置节点为上报节点
		creStepFlag = 6;
	}
    var option = {
    	    title: {
    	        text: '上报数据：'+ value1 +'条\n\n数据表'+chartJson.rptCount+'/'+rptAllCount,
    	        left: 'center'
    	    },
            tooltip: {
                trigger: 'item',
                position: ['30%', '55%'],
                formatter: "{a} <br/>{b} ({d}%)"
            },
            series: [{
                name: '上报数据统计',
                type: 'pie',
                radius: ['70%', '50%'],
                avoidLabelOverlap: false,
                label: {
                    normal: {
                        show: false,
                        position: 'center'
                    }
                },
                labelLine: {
                    show: false
                },
                data: [{
	                    value: value1,
	                    itemStyle: {
	                        normal: {
	                            color: '#5bc0dd'
	                        }
	                    },
	                    label: {
	                    	show: true,
	                        fontSize: '20',
	                        fontWeight: 'bold'
	                    },
	                    name:"已上报数据\n"+value1
	                },
                    {
                        value: value2,
                        itemStyle: {
                            normal: {
                                color: '#E9596F'
                            }
                        },
                        selected:true,
                        name:"未上报数据\n"+value2
                    }
                ]
            }]
        };
    var myCharts = echarts.init(document.getElementById('bar-box6'));
    myCharts.setOption(option);
}

// 数据采集 法人视角 -bar-box11
var initCollectionCharts2 = function(rptOrg){
    // 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getCollectionResult2";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
            dtDate: dtDate,
            rptOrg: rptOrg,
            orgType: orgType
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
    var value1 = parseInt(chartJson.etlCount);
    var value2 = parseInt(chartJson.failCount);
    var value3 = value1 + value2;
    if(value1 == 0) {
        // 如果已检验数据不为0，则设置节点为校验节点
        creStepFlag = 1;
    }
    var option = {
        title: {
            text: '采集数据：'+ value3 +'条\n\n数据表'+chartJson.rptCount+'/'+rptAllCount,
            left: 'center'
        },
        tooltip: {
            trigger: 'item',
            position: ['30%', '55%'],
            formatter: "{a} <br/>{b} ({d}%)"
        },
        series: [{
            name: '数据采集统计',
            type: 'pie',
            radius: ['70%', '50%'],
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: false,
                    position: 'center'
                }
            },
            labelLine: {
                show: false
            },
            data: [{
                value: value1,
                itemStyle: {
                    normal: {
                        color: '#5bc0dd'
                    }
                },
                label: {
                    show: true,
                    fontSize: '20',
                    fontWeight: 'bold'
                },
                name:"自动采集数\n"+value1
            },
                {
                    value: value2,
                    itemStyle: {
                        normal: {
                            color: '#E9596F'
                        }
                    },
                    selected:true,
                    name:"手动补录数\n"+value2
                }
            ]
        }]
    };
    var myCharts = echarts.init(document.getElementById('bar-box11'));
    myCharts.setOption(option);
}


// 初始化校验图表 法人视角 bar-box12
var initCheckCharts2 = function(rptOrg){
    // 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getCheckResult2";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
            dtDate: dtDate,
            rptOrg: rptOrg,
            orgType: orgType
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
    var value1 = chartJson.checkSum; // 已校验数据
    var value2 = parseInt(chartJson.totalCount) - parseInt(chartJson.checkSum); // 未校验数据
    var value3 = chartJson.totalCount; // 数据总量
    if(value1 != 0) {
        // 如果已检验数据不为0，则设置节点为校验节点
        creStepFlag = 2;
    }
    var option = {
        title: {
            text: '检验数据：'+ value1 +'条\n\n数据表'+chartJson.rptCount+'/'+rptAllCount,
            left: 'center'
        },
        tooltip: {
            trigger: 'item',
            position: ['30%', '55%'],
            formatter: "{a} <br/>{b} ({d}%)"
        },
        series: [{
            name: '校验数据统计',
            type: 'pie',
            radius: ['70%', '50%'],
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: false,
                    position: 'center'
                }
            },
            labelLine: {
                show: false
            },
            data: [{
                value: value1,
                itemStyle: {
                    normal: {
                        color: '#5bc0dd'
                    }
                },
                label: {
                    show: true,
                    fontSize: '20',
                    fontWeight: 'bold'
                },
                name:"已校验数\n"+value1
            },
                {
                    value: value2,
                    itemStyle: {
                        normal: {
                            color: '#E9596F'
                        }
                    },
                    selected:true,
                    name:"未校验数\n"+value2
                }
            ]
        }]
    };
    var myCharts = echarts.init(document.getElementById('bar-box12'));
    myCharts.setOption(option);
}

// 初始化补录图表 bar-box13
var initInputCharts2 = function(rptOrg){
    // 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getInputResult2";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
            dtDate: dtDate,
            rptOrg: rptOrg,
            orgType: orgType
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
    var value1 = chartJson.inputCount;
    var value2 = rptAllCount - parseInt(value1);
    if(value1 != 0) {
        // 如果补录报表不为0，则设置节点为补录节点
        creStepFlag = 3;
    }
    var option = {
        title: {
            text: '补录报表\n\n数据表'+value1+'/'+rptAllCount,
            left: 'center'
        },
        tooltip: {
            trigger: 'item',
            position: ['30%', '55%'],
            formatter: "{a} <br/>{b} ({d}%)"
        },
        series: [{
            name: '补录报表统计',
            type: 'pie',
            radius: ['70%', '50%'],
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: false,
                    position: 'center'
                }
            },
            labelLine: {
                show: false
            },
            data: [{
                value: value1,
                itemStyle: {
                    normal: {
                        color: '#5bc0dd'
                    }
                },
                label: {
                    show: true,
                    fontSize: '20',
                    fontWeight: 'bold'
                },
                name:"已补录报表\n"+value1
            },
                {
                    value: value2,
                    itemStyle: {
                        normal: {
                            color: '#E9596F'
                        }
                    },
                    selected:true,
                    name:"未补录报表\n"+value2
                }
            ]
        }]
    };
    var myCharts = echarts.init(document.getElementById('bar-box13'));
    myCharts.setOption(option);
}

// 初始化报送图表   bar-box14
var initSubmittedCharts2 = function(rptOrg){
    // 日期框
    var dtDate = $("#dtDate").val();
    var chartJson;
    var url = ctx + "/imas/index/getSubmittedResult2";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        data:{
            dtDate: dtDate,
            rptOrg: rptOrg,
            orgType: orgType
        },
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.result);
        }
    });
    var value1 = chartJson.reportSum;
    var value2 = parseInt(chartJson.totalCount) - parseInt(chartJson.reportSum);
    var value3 = chartJson.totalCount;
    if(value1 != 0) {
        // 如果已上报条数不为0，则设置节点为上报节点
        creStepFlag = 4;
    }
    var option = {
        title: {
            text: '上报数据：'+ value1 +'条\n\n数据表'+chartJson.rptCount+'/'+rptAllCount,
            left: 'center'
        },
        tooltip: {
            trigger: 'item',
            position: ['30%', '55%'],
            formatter: "{a} <br/>{b} ({d}%)"
        },
        series: [{
            name: '上报数据统计',
            type: 'pie',
            radius: ['70%', '50%'],
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: false,
                    position: 'center'
                }
            },
            labelLine: {
                show: false
            },
            data: [{
                value: value1,
                itemStyle: {
                    normal: {
                        color: '#5bc0dd'
                    }
                },
                label: {
                    show: true,
                    fontSize: '20',
                    fontWeight: 'bold'
                },
                name:"已上报数据\n"+value1
            },
                {
                    value: value2,
                    itemStyle: {
                        normal: {
                            color: '#E9596F'
                        }
                    },
                    selected:true,
                    name:"未上报数据\n"+value2
                }
            ]
        }]
    };
    var myCharts = echarts.init(document.getElementById('bar-box14'));
    myCharts.setOption(option);
}



var msgTypes = {};
//全局变量
var dateTime;
var tabName;
var setCharts = function(retult) {
    var mycarts = document.getElementById('doughnut-chart');
    var myCharts1 = echarts.init(mycarts);
    var option1 = {
        tooltip: {
            trigger: 'item',
            formatter: "{a} <br/>{b} ({d}%)"
        },
        series: [{
            name: '本期数据校验',
            type: 'pie',
            hoverAnimation: false,
            radius: ['75%', '40%'],
            avoidLabelOverlap: false,
            label: {
                normal: {
                    show: false,
                    position: 'center'
                }
            },
            data: [{
                value: retult.sum_count-retult.fail_count,
                itemStyle: {
                    normal: {
                        color: '#90EE90'
                    }
                },
                name:"通过记录数"
            },
                {
                    value: retult.fail_count,
                    itemStyle: {
                        normal: {
                            color: '#E9596F'
                        }
                    },
                    name:"错误记录数"
                }
            ]
        }]
    };
    myCharts1.setOption(option1);
//myCharts1.on("click", this.dataQuality);
}
var setCharts1 = function() {
    var chartJson;
    var url = ctx + "/east/checkresult/data/getLately6CheckResult";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        dataType : "json",//返回的数据类型
        success: function (data) {
            chartJson = JSON.parse(data.tablehtml);
        },
        error:function (data) {

        }
    });

    var mycarts = document.getElementById('bar-box');
    var myCharts1 = echarts.init(mycarts);
    var option2 = {
        title: {
            show:true,
            text:"单位：笔",
            textStyle:{
                color:"#495A63",
                fontSize:12
            },
            left:14,
            top:10
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'shadow'
            }
        },
        legend: {
            textStyle: {
                color: '#666'
            }
        },
        grid: {
            top: '40',
            left: '20',
            right: '50',
            bottom: '30',
            containLabel: true
        },
        textStyle: {
            color: '#888'
        },
        xAxis: {
            show: true,
            type: 'category',
            data: chartJson.xAxis,
            axisLine: {
                show: false,
                lineStyle: {
                    color: '#DEDEDE',
                    type: 'dashed'
                }
            },
            axisLabel: {
                show: true,
                margin:20,
                textStyle: {
                    color: '#495A63',  //更改坐标轴文字颜色
                    fontSize : 14      //更改坐标轴文字大小
                }
            }
        },
        yAxis: {
            show: true,
            type: 'value',
            axisTick:{
                show:false
            },
            axisLabel: {
                show: true,
                margin:20,
                textStyle: {
                    color: '#495A63',  //更改坐标轴文字颜色
                    fontSize : 14      //更改坐标轴文字大小
                }
            },
            axisLine: {
                show: false,
                lineStyle: {
                    color: '#DEDEDE',
                    type: 'dashed'
                }
            },
            splitLine:{
                show:true,
                lineStyle:{
                    color: '#DEDEDE',
                    type: 'dashed',
                    width: 1
                }
            }
        },
        series: [{
            name: '校验不通过记录数',
            type: 'bar',
            barWidth: '16',
            data: chartJson.yAxis[1],
            itemStyle: {
                normal: {
                    color: '#3F88DE'
                }
            }
        },
            {
                name: '校验总记录数',
                type: 'bar',
                barWidth: '16',
                data: chartJson.yAxis[0],
                itemStyle: {
                    normal: {
                        color: '#75BEDA'
                    }
                }
            }
        ]
    };
    myCharts1.setOption(option2);

    myCharts1.on('click', function (param) {
            dateTime = param.name;
            cRLButClick();
        }
    );
}

/**
 * 检核结果核对
 * checkResultCompareButClick
 * @returns
 */
function checkResultQuery(){
    top.addTabs({
        id: '78cef8a419004bf99de4a6602ae1352c',
        title: '检核结果查询',
        close: true,
        url: '/frs/yufreejs?js=/yujs/imas/resultsummary/imas_check_result_main.js',
        urlType: "relative"
    });
}

/**
 * 检核结果核对
 * checkResultCompareButClick
 * @returns
 */
function rptInput(){
    top.addTabs({
        id: '0830a7e3b4744f3492fdc4be08495604',
        title: '报表填报',
        close: true,
        url: '/frs/yufreejs?js=/yujs/imas/fillingProcess/imas_distribute_fillin_main.js',
        urlType: "relative"
    });
}


/**
 * 检核结果查询
 *
 * @returns
 */
function cRLButClick(){
    top.addTabs({
        id: '707b651df52e42bd8e61d8e168951130',
        title: '检核结果查询',
        close: true,
        url: '/frs/yufreejs?js=/yujs/imas/resultsummary/imas_check_result_main.js',
        urlType: "relative"
    });
}

/**
 * 检核规则查询
 * checkResultCompareButClick
 * @returns
 */
function checkRuleLButClick(){
    top.addTabs({
        id: '9d07275159d04a78a10de0e81634dac7',
        title: '校验配置',
        close: true,
        url: '/frs/yufreejs?js=/yujs/imas/ruleConfig/imas_rule_config_main.js',
        urlType: "relative"
    });
}

/**
 * 报送数据查询
 * @returns
 */
function dataSubmitButClick(){
    top.addTabs({
        id: 'c1ed1996cae74fbe94a8c6ee7e9853da',
        title: '报送数据查询(月报)',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/busiModel/busidataquery_month.js',
        urlType: "relative"
    });
}

/**
 * 全量数据查询
 * @returns
 */
function businessDataQueryButClick(){
    top.addTabs({
        id: 'c4c8b5229ae049fda251004e121300a1',
        title: '报表数据维护',
        close: true,
        url: '/frs/yufreejs?js=/yujs/imas/busidata/imas_busi_data_main.js',
        urlType: "relative"
    });
}

/**
 * 业务模型管理
 * @returns
 */
function businessModelManagerButClick(){
    top.addTabs({
        id: 'f8b714cbe0c34fe889730e804fdaf609',
        title: '业务模型',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/busiModel/58table.js',
        urlType: "relative"
    });
}

/**
 * 检核结果分析
 * @returns
 */
function checkResultAnalysisButClick(){
    top.addTabs({
        id: 'd900a11fbb2344ba8db383b693a7a351',
        title: '检核结果表名排名',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/checkresultDataInter.js',
        urlType: "relative"
    });
}

/**
 * 待办任务更多
 * @returns
 */
function openMoreTask(){

}

/**
 * 通知公告更多
 * @returns
 */
function openMoreNotice(){
    var url = ctx + "/bione/msg/announcement/viewIdx?d="+new Date().getTime();
    window.parent.BIONE.commonOpenLargeDialog("公告查看","noticeWin",url);
}

var getTask = function(){
    var mesuNmstr = ",\u9996\u9875,\u6211\u7684\u6536\u85cf,1104\u76d1\u7ba1,\u4eba\u884c\u5927\u96c6\u4e2d,East\u62a5\u9001,East\u62a5\u9001B,\u5229\u7387\u62a5\u5907,\u652f\u4ed8\u62a5\u9001,\u4e2a\u4eba\u8d26\u6237,\u6307\u6807\u7ba1\u7406,\u62a5\u9001\u914d\u7f6e,\u7efc\u5408\u67e5\u8be2,\u8003\u6838\u7ba1\u7406,\u660e\u7ec6\u8865\u5f55,\u5f15\u64ce\u7ba1\u7406,\u7cfb\u7edf\u7ba1\u7406";
    var url = ctx + "/frs/mainpage/blacklog/initGrid?&mesuNmstr="+mesuNmstr;
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        dataType : "json",//返回的数据类型
        success: function (data) {
            var innerHtml = "";
            for(var i=0;i<data.Rows.length;i++){
                if(i==5)
                    break;
                innerHtml += "<li><span><a href='javascript:void(0)' onclick='openTask(\""+data.Rows[i].taskType+"\",\""+data.Rows[i].sts+"\",\""+data.Rows[i].taskId+"\",\""+data.Rows[i].dataDate+"\",\""+data.Rows[i].rptId+"\")'>"+data.Rows[i].taskNm+"</a></span><b>"+data.Rows[i].dataDate+"</b></li>"
            }
            if(data.Rows.length<5){
                for (var i = 0; i < 5-data.Rows.length; i++) {
                    innerHtml += "<li><span></span><b></b></li>"
                }
            }
            $("#taskUL").append(innerHtml);
        },
        error:function (data) {

        }
    });
}

function openTask(taskType,sts,taskId,dataDate,rptId){
    if(taskType=='99999'){
        if(sts=='0'){
            var url=ctx + "/report/frs/rptfill/rptFillTabController.mo?moduleType=01&taskId="+taskId+"&dataDate="+dataDate+"&rptId="+rptId+"&fromMainPage=1&t="+ new Date().getTime();
            window.parent.parent.parent.parent.changePage(url,"利率报备","报表填报>>报表填报");
        }else if(sts=='1'){
            var url=ctx + "/report/frs/rptsubmit/rptSubmitController.mo?orgTypes=02,03,01&moduleType=01&operType=01&t="+ new Date().getTime();
            window.parent.parent.parent.parent.changePage(url,"利率报备","报表复核>>报表复核");
        }else if(sts=='2'){
            var url=ctx + "/report/frs/rptsubmit/rptSubmitController.mo?orgTypes=02,03,01&moduleType=01&operType=02&t="+ new Date().getTime();
            window.parent.parent.parent.parent.changePage(url,"利率报备","报表审核>>报表审核");
        }else if(sts=='6'){
            var url=ctx + "/report/frs/rptfill/rptFillRejectController.mo?doFlag=shenpi&orgTypes=02,03,01&moduleType=01&t="+ new Date().getTime();
            window.parent.parent.parent.parent.changePage(url,"利率报备","审批解锁>>审批解锁");
        }else{
            alert("填报已结束！");
        }
    }else if(taskType=='02'){
        //1104
        if(sts=='0'){
            var url="/rpt/frs/rptfill/fillRpt";
            window.parent.parent.doMenuClick("1b0db592465043659d12bc3647475b86","报表填报",url);
        }else if(sts=='1'){
            var url="frs/rptsubmit/submit/busi1104Index";
            window.parent.parent.doMenuClick("5ce622c111cb47c2b904017b4edbdead","报表复核",url);
        }else if(sts=='2'){
            var url="frs/rptsubmit/submit/busi1104ApprIndex";
            window.parent.parent.doMenuClick("d243e8d226ce4e29a9943536e5d304ef","报表审核",url);
        }else if(sts=='6'){
            var url="frs/rptfill/reject/approve1104";
            window.parent.parent.doMenuClick("0f00fd7ed1834825a8169c9fe4e6aa4d","审批解锁",url);
        }else{
            alert("填报已结束！");
        }
    }else{
        //大集中
        if(sts=='0'){
            var url="/rpt/frs/rptfill/pbcFillRpt";
            window.parent.parent.addTabInfo("0dcc300f950b4884be362d4c6cf07482","报表填报",url);
        }else if(sts=='1'){
            var url="/frs/rptsubmit/submit/busi1104Index";
            window.parent.parent.addTabInfo("98c4f16442244391b471a5dcf17963d5","报表复核",url);
        }else if(sts=='2'){
            var url="/frs/rptsubmit/submit/busiPbcApprIndex";
            window.parent.parent.addTabInfo("b9e433ac96164a06914c6aa7908b432f","报表审核",url);
        }else if(sts=='6'){
            var url="/frs/rptfill/reject/approvePbc";
            window.parent.parent.addTabInfo("953f1f9ada1442ef8d28b6119664e37e","审批解锁",url);
        }else{
            alert("填报已结束！");
        }
    }
}
var noticeData;
var getNotice = function(){
    var url = ctx + "/bione/msg/bulletin/list.json?maxRows="+10+"&d="+new Date().getTime();
    var announcementId1 = null;
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        dataType : "json",//返回的数据类型
        success: function (data) {
            noticeData = data;
            var innerHtml = "";
            for(var i=0;i<data.Rows.length;i++){
                if(i==0){announcementId1 =data.Rows[0].announcementId;}//返回值用于显示明细
                if(i==5)
                    break;
                var read ="";
                var nTime = Date.now() - data.Rows[i].lastUpdateTime;
                var day =Math.floor(nTime/86400000);
                if(day < 30){
                    read = "<i class='no-read'>new</i>";
                }
                else{
                    read = "";//read = "<i class='have-read'></i>";
                }
                msg_type = "["+msgTypes[data.Rows[i].announcementType]+"]:";
                innerHtml += "<li>"+read+"<span style='font-size: 12px;'><a href='javascript:void(0)' onclick=noticeDetail('"+ data.Rows[i].announcementId +"')>"+msg_type+data.Rows[i].announcementTitle+"</a></span><b style='font-size: 12px;'>"+getFormatDate(data.Rows[i].lastUpdateTime, 'yyyyMMdd')+"</b></li>"
            }
            if(data.Rows.length < 5){
                for (var i = 0; i < 5-data.Rows.length; i++) {
                    innerHtml += "<li><span></span><b></b></li>"
                }
            }
            $("#noticeUL").append(innerHtml);
        },
        error:function (data) {

        }
    });
    return announcementId1;
}

//单个明细
function noticeDetail(announcementId){
    $("#noticeTitle").empty();
    $("#noticeDetail").empty();
    if(noticeData != null){
        for(var i=0;i<noticeData.Rows.length;i++){
            if (noticeData.Rows[i].announcementId == announcementId) {
                var msgObj =noticeData.Rows[i];
                var htmls = [];
                htmls.push('<div style="width:94%; text-align:center; margin-left:auto; margin-right:auto; padding:5px;">'); // border:1px solid #ccc;
                //htmls.push('  <div style="width:100%; text-align:center; " ><h4>' + msgObj.announcementTitle + '</h4></div>');
                htmls.push('  <div class="msgbar" style="width:100%; text-align:center;font-size: 12px;">发布时间：' + getFormatDate(msgObj.lastUpdateTime, "yyyy-MM-dd hh:mm:ss") + '&nbsp; 来源：' + typeRender(msgObj.announcementType) + '&nbsp;</div>');
                htmls.push('  <div style="line-height: 40px; border-top: 1px solid #EAEAF0; box-sizing: border-box;"><ul><li></li></ul></div>');
                htmls.push('  <div name="info_content" style="width:99%; height:170px; text-align:left;font-size: 12px;overflow-y:scroll;" >' + msgObj.announcementDetail + '</div>');
                htmls.push('</div>');
                //
                $("#noticeTitle").append(msgObj.announcementTitle);
                $("#noticeDetail").append(htmls.join(''));
                break;
            }
        }
    }
    else{
        for(var i=0;i<5;i++){
            var msgObj ="";
            var htmls = [];
            htmls.push('<div style="width:94%; text-align:center; margin-left:auto; margin-right:auto; padding:5px;">'); // border:1px solid #ccc;
            htmls.push('  <div class="msgbar" style="width:100%; text-align:center;font-size: 12px;"></div>');
            htmls.push('  <div style="line-height: 40px; border-top: 1px solid #EAEAF0; box-sizing: border-box;"><ul><li></li></ul></div>');
            htmls.push('  <div name="info_content" style="width:99%; height:170px; text-align:left;font-size: 12px;overflow-y:scroll;" ></div>');
            htmls.push('</div>');
            //
            $("#noticeTitle").append();
            $("#noticeDetail").append(htmls.join(''));
            break;
        }
    }
}

//公告类型显示转化
function typeRender(data) {
    if (data == '01') {
        return "系统信息";
    }else if(data == '02'){
        return "业务信息";
    }else {
        return data;
    }
}

function getFormatDate(dateobj, dateformat)
{
    var date=null;
    if(dateobj.time){
        date=new Date(dateobj.time);
    }else{
        date=new Date(dateobj);
    }
    var g = this, p = this.options;
    if (isNaN(date)) return null;
    var format = dateformat;
    var o = {
        "M+": date.getMonth() + 1,
        "d+": date.getDate(),
        "h+": date.getHours(),
        "m+": date.getMinutes(),
        "s+": date.getSeconds(),
        "q+": Math.floor((date.getMonth() + 3) / 3),
        "S": date.getMilliseconds()
    }
    if (/(y+)/.test(format))
    {
        format = format.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o)
    {
        if (new RegExp("(" + k + ")").test(format))
        {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}

function checkResultDataInterfaceClick(){
    top.addTabs({
        id: 'b6d7fa5397694c3b93b0bfe2a89fb46d',
        title: '检核结果统计(表名)',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/checkresultofinterface.js',
        urlType: "relative"
    });
}

var latelyChackResult = function(){
    var url = ctx + "/east/checkresult/data/getLatelyCheckResult";
    var resultData = null;
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        dataType : "json",//返回的数据类型
        success: function (data) {
            if(data.data != null){
                var vo = data.data.m_hData;
                resultData = vo;
                dateTime = vo.data_dt;
                if(vo.chain_rate == null){
                    vo.chain_rate = "上一期没有失败数";
                }
                if(vo.year_on_rate == null){
                    vo.year_on_rate = "上一期没有失败数";
                }
                var class1;
                var class2;
                if (parseInt(vo.chain_rate) >0) {
                    class1 = "up";
                }
                else{
                    class1 = "down";
                }
                if (parseInt(vo.year_on_rate) >0) {
                    class2 = "up";
                }
                else{
                    class2 = "down";
                }
                var innerHtml = ""+
                    "<span>数据日期：<i>"+vo.data_dt+"</i></span>"+
                    "<div class='show-pic'>"+
                    "<p><span class='num-light'><a href='javascript:cRLButClick();'>"+vo.fail_count +"</a></span>/<span class='num-all'>"+vo.sum_count+"</span> <b>"+Math.round(vo.fail_count/vo.sum_count*10000)/100+"</b></p>"+
                    "<div class='num-line'>"+
                    "<div style='width: "+Math.round(vo.fail_count/vo.sum_count*10000)/100 +"%;'></div>"+
                    "</div>"+
                    "<div class='compare'>"+
                    "<div class='"+class1+"'>"+vo.chain_rate+"<span>环比</span></div>"+
                    "<div class='"+class2+"'>"+vo.year_on_rate+"<span>同比</span></div>"+
                    "</div>"+
                    "</div>";
                var innerFail = ""+
                    "<span>数据日期：<i>"+vo.data_dt+"</i></span>"+
                    "<h3>"+ (parseInt(100)-Math.round(vo.fail_count/vo.sum_count*10000)/100).toFixed(2) +"<i>%</i></h3>"+
                    "<div id='doughnut-chart'></div>";

                $("#latelyChackResult").append(innerHtml);
                $("#failureRate").append(innerFail);
            }
        },
        error:function (data) {

        }
    });
    return resultData;
}

function dataQuality(){
    alert("生成数据质量报告");
}

/**
 * 本期数据质量评分
 * @return {[type]} [description]
 */
var getdataQuality = function(){
    top.addTabs({
        id: 'b6d7fa5397694c3b93b0bfe2a89fb46d',
        title: '检核结果统计(表名)',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/checkresultofinterface.js',
        urlType: "relative"
    });
}

/**
 * 表名更多
 * @return {[type]} [description]
 */
function openMoreDataInter(){
    top.addTabs({
        id: 'd900a11fbb2344ba8db383b693a7a351',
        title: '检核结果数据表排名',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/checkresultDataInter.js&OpenPars={"data_dt":"'+dateTime+'"}',
        urlType: "relative"
    });
}
/**
 * 机构更多
 * @return {[type]} [description]
 */
function openMoreOrg(){
    top.addTabs({
        id: 'e2a85310f09f4b0da42200a94ecb660f',
        title: '检核结果机构排名',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/checkresultOrg.js&OpenPars={"data_dt":"'+dateTime+'","tab_name":"'+encodeURIComponent(tabName)+'"}',
        urlType: "relative"
    });
}
/**
 * 部门更多
 * @return {[type]} [description]
 */
function openMoreDept(){
    top.addTabs({
        id: 'a037e9f3836449eba69936bd730368fe',
        title: '检核结果部门排名',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/checkresultDepart.js',
        urlType: "relative"
    });
}
/**
 * 来源系统更多
 * @return {[type]} [description]
 */
function openMoreSrcSys(){
    top.addTabs({
        id: 'bd80ef0d0634410c9bd0c956d25ff60f',
        title: '检核结果来源系统排名',
        close: true,
        url: '/frs/yufreejs?js=/yujs/east/result/checkresultSrcsys.js',
        urlType: "relative"
    });
}
/**
 * 检核结果排名
 * @return {[type]} [description]
 */
var orderByCheckResult = function(whereSql,flag){
    var url = ctx + "/east/checkresult/data/orderBy?whereSql="+whereSql;
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        dataType : "json",//返回的数据类型
        success: function (data) {
            var list = data.data;
            //表名
            var dataInter = list[0];
            var dIInnerHtml = "<ul>";
            for(var i=0;i<dataInter.length;i++){
                tabName = dataInter[0].m_hData.tab_name;
                var fail_rate = dataInter[i].m_hData.fail_rate==null?"":dataInter[i].m_hData.fail_rate
                dIInnerHtml +="<li>"+
                    "<span>Top"+parseInt(i+1)+"</span>"+
                    "<a href='javascript:DIClick(\""+ dataInter[i].m_hData.tab_name +"\");'>"+
                    "<div class='ranking-detail'><span>"+dataInter[i].m_hData.fail_count+"</span><i>"+dataInter[i].m_hData.tab_name+"</i></div>"+
                    "<div class='ranking-num'>"+
                    "<div class='down'>"+fail_rate+"</div>"+
                    "</div>"+
                    "</a>"+
                    "</li>";
            }
            dIInnerHtml = spellSpace(dataInter,dIInnerHtml);
            dIInnerHtml += "</ul>";
            //机构
            var orgInter = list[1];
            var orgInnerHtml = "<ul>";
            for(var i=0;i<orgInter.length;i++){
                var fail_rate = orgInter[i].m_hData.fail_rate==null?"":orgInter[i].m_hData.fail_rate;
                orgInnerHtml +="<li>"+
                    "<span>Top"+parseInt(i+1)+"</span>"+
                    "<div class='ranking-detail'><span>"+orgInter[i].m_hData.fail_count+"</span><i>"+orgInter[i].m_hData.org_nm+"</i></div>"+
                    "<div class='ranking-num'>"+
                    "<div class='down'>"+fail_rate+"</div>"+
                    "</div>"+
                    "</li>";
            }
            orgInnerHtml = spellSpace(orgInter,orgInnerHtml);
            orgInnerHtml += "</ul>";
            /*//部门
            var deptInter = list[2];
            var deptInnerHtml = "<ul>";
            for(var i=0;i<deptInter.length;i++){
                deptInnerHtml +="<li>"+
                                    "<span>Top"+parseInt(i+1)+"</span>"+
                                    "<div class='ranking-detail'><span>"+deptInter[i].m_hData.fail_count+"</span><i>"+deptInter[i].m_hData.line_nm+"</i></div>"+
                                    "<div class='ranking-num'>"+
                                        "<div class='down'>"+deptInter[i].m_hData.fail_rate+"</div>"+
                                    "</div>"+
                                "</li>";
            }
            deptInnerHtml = spellSpace(deptInter,deptInnerHtml);
            deptInnerHtml += "</ul>";
            //来源系统
            var srcSysInter = list[3];
            var srcSysInnerHtml = "<ul>";
            for(var i=0;i<srcSysInter.length;i++){
                srcSysInnerHtml +="<li>"+
                                    "<span>Top"+parseInt(i+1)+"</span>"+
                                    "<div class='ranking-detail'><span>"+srcSysInter[i].m_hData.fail_count+"</span><i>"+srcSysInter[i].m_hData.src_sys+"</i></div>"+
                                    "<div class='ranking-num'>"+
                                        "<div class='down'>"+srcSysInter[i].m_hData.fail_rate+"</div>"+
                                    "</div>"+
                                "</li>";
            }
            srcSysInnerHtml = spellSpace(srcSysInter,srcSysInnerHtml);
            srcSysInnerHtml += "</ul>";*/
            if(flag)
                $("#dataInter").append(dIInnerHtml);
            $("#org").append(orgInnerHtml);
            /*$("#dept").append(deptInnerHtml);
            $("#srcSys").append(srcSysInnerHtml);*/
        },
        error:function (data) {

        }
    });
}

var orderByCheckResultNull = function(){
    //表名
    var dIInnerHtml = "<ul>";
    dIInnerHtml = spellSpace(null,"");
    dIInnerHtml += "</ul>";
    //机构
    var orgInnerHtml = "<ul>";

    orgInnerHtml = spellSpace(null,"");
    orgInnerHtml += "</ul>";
    //部门
    var deptInnerHtml = "<ul>";
    deptInnerHtml = spellSpace(null,"");
    deptInnerHtml += "</ul>";
    //来源系统
    var srcSysInnerHtml = "<ul>";
    srcSysInnerHtml = spellSpace(null,"");
    srcSysInnerHtml += "</ul>";

    $("#dataInter").append(dIInnerHtml);
    $("#org").append(orgInnerHtml);
    $("#dept").append(deptInnerHtml);
    $("#srcSys").append(srcSysInnerHtml);
}

function spellSpace(data,InnerHtml){
    if(data == null){
        for (var i = 0; i < 5; i++) {
            InnerHtml += "<li><div class='ranking-detail'><span></div><div class='ranking-num'></div></li>";
        }
    }
    else if(data.length < 5){
        for (var i = 0; i < 5-data.length; i++) {
            InnerHtml += "<li><div class='ranking-detail'><span></div><div class='ranking-num'></div></li>";
        }
    }
    return InnerHtml;
}

function DIClick(tab_name){
    tabName = tab_name;
    $("#org").empty();
    $("#dept").empty();
    $("#srcSys").empty();
    var wherSql = "data_dt='"+dateTbar-cjime+"'" +"and tab_name='"+tab_name+"'";
    orderByCheckResult(wherSql, false);
}

function initMsgType(){
    $.ajax({
        cache : false,
        async : false,
        url : ctx + "/bione/variable/param/find?typeNo=SysBulletin",
        dataType : 'json',
        type : "post",
        success : function(data)
        {
            for(var i in data){
                msgTypes[data[i].paramValue] = data[i].paramName;
            }
        }
    });
}

// 初始化赋值当天日期
function initSearchDate() {
    var nowDate = formatDateToStr(new Date());
    $("#dtDate").val(nowDate);
}

// 初始化获取上报机构
function initReportOrg() {
    var url = ctx + "/imas/index/getReportOrgInfo";
    $.ajax({
        async : false,    //表示请求是否异步处理
        type : "get",    //请求类型
        url : url,//请求的 URL地址
        dataType : "json",//返回的数据类型
        success: function (data) {
            resultJson = JSON.parse(data.result);
            $("#reportOrg").empty().html(resultJson.option);
        }
    });
}


// 判断当前选择日期是否为月底
function isLastDay(inputDate) {
　　var d = new Date(inputDate.replace(/\-/, "/ ")); 
　　var nd = new Date(d.getTime()+24*60*60*1000); //next day 
　　return (d.getMonth()!=nd.getMonth());
} 

// 获取最迟上报时间和剩余时间
function getReportTime(inputDate) {
	// 最迟上报时间为T+3 12:00:00，即当前日期+3天
	var d = new Date(inputDate.replace(/\-/, "/ "));
	var nd = new Date(d.getTime()+24*60*60*1000*3); //三天后
    var lastTime = formatDateToStr(nd) + " 12:00:00";
    $("#lastReportTime").html(lastTime);
    // 获取间隔时间
    var startTime = new Date(lastTime).getTime();
    var endTime = new Date().getTime();
    if (startTime < endTime) {
        $("#remainReportTime").html("0天0时0分");
    } else {
    	var dates = getDuration(startTime - endTime);
        $("#remainReportTime").html(dates);

    }
}

//格式化日期为yyyy-MM-dd
function formatDateToStr(date) {
	var year = date.getFullYear();
    var month = date.getMonth() + 1;
    if(month < 10 ) {
        month = "0" + month;
    }
    var date = date.getDate();
    if (date < 10 ) {
        date = "0" + date;
    }
    return year + "-" + month +"-" +date;
}

/**
 * 根据毫秒转换为时分秒
 * @param my_time
 * @returns
 */
function getDuration(my_time) {  
	var days = my_time / 1000 / 60 / 60 / 24;
    var daysRound = Math.floor(days);
    var hours = my_time / 1000 / 60 / 60 - (24 * daysRound);
    var hoursRound = Math.floor(hours);
    var minutes = my_time / 1000 / 60 - (24 * 60 * daysRound) - (60 * hoursRound);
    var minutesRound = Math.floor(minutes);
    var seconds = my_time / 1000 - (24 * 60 * 60 * daysRound) - (60 * 60 * hoursRound) - (60 * minutesRound);
    var time = daysRound + '天' + hoursRound + '时'+ minutesRound + '分';
    return time;
}

