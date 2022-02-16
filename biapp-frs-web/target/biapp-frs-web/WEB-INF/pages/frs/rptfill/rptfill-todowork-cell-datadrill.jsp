<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<%@ include file="/common/taglibs.jsp"%>
<script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
<script type="text/javascript">
	var dataDate = window.parent.base_DataDate;
	var orgNo = window.parent.base_OrgNo;
	var rptIndexNo = window.parent.base_selectIdxNo;
	var dataUnit = window.parent.base_selectUnit;
	var dataPrecision = window.parent.base_selectDataPrecision;
	var indexNo = rptIndexNo;
	var orgType = window.parent.base_OrgType;
	var orgNm = window.parent.base_OrgNm;
	var rptIndexNm = window.parent.base_selectIdxNm;
	var indexVerId = window.parent.base_verId;
	var orgDataList = [];//机构分析图形数据
	var structureDataList = [];//结构分析图形数据
	var orgDataMap = {};//机构分析图形数据集合
	var structureDataMap = {};//结构分析图形数据集合
	var orgChat = null;
	var structureChart = null;
	var trendChart = null;
	var dateMap = {};//日期集合
	var detailGrid = null;
	var columns = [];//表格列
	var isDetail = false;//是否完成明细加载
	var isStructure = false;//是否完成结构加载
	var isTrend = false;//是否完成趋势加载
	var isOrg = false;//是否完成机构加载
	var flag = '${flag}';
	$(function() {
		if(!rptIndexNo){
			window.parent.BIONE.tip("该单元格不是指标单元格");
			BIONE.closeDialog("rptIdxDataDrill");
		}
		if(!dataPrecision){
			dataPrecision = 2;
		}
		var $content = $(window);
		var height = $content.height();
		if(flag == "idxStructure"){
			$("#center").append($('<div id="idxStructure" style="float: left; width: 95%; height: 95%;"></div>'));
			idxStructure();
		}
		if(flag == "idxLast"){
			$("#center").append($('<div id="idxTrend" style="float: left; width: 95%; height: 95%;"></div>'));
			idxTrend();
		}
		if(flag == "orgDrill"){
			$("#center").append($('<div id="idxOrg" style="float: left; width: 95%; height: 95%;"></div>'));
			idxOrg();
		}
		if(flag == "idxDetail"){
			$("#center").append($('<div id="idxDetail" style="float: left; width: 95%; height: 95%;overflow: auto;">' +
					'<div id="idxDetailedGrid" style="margin-right: 10px;margin-bottom: 10px;">' +
					'</div></div>'));
			idxDetailed();
		}
	});
	
	//指标结构分析
	function idxStructure(){
		isStructure = false;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/getIdxStructureData",
			dataType : 'json',
			data : {
				dataDate : dataDate,
				orgNo : orgNo,
				indexNo : indexNo,
				dataUnit : dataUnit,
				dataPrecision : dataPrecision,
				indexVerId : indexVerId,
				indexNm : rptIndexNm
			},
			type : "POST",
			beforeSend : function() {
				BIONE.showLoading("分析中，请稍等");
			},
			success : function(result) {
				if(result && result.pieData){
					var data = structureDataMap[indexNo];
					if(!data){
						structureDataMap[indexNo] = result;
						structureDataList.push(result);
					}
					initStructureEcharts(result.xAxisData, result.pieData, result.indexNm);
				}
				isStructure = true;
				BIONE.loading = false;
				BIONE.hideLoading();
			}
		});
	}
	
	//初始化指标结构分析图
	function initStructureEcharts(xAxisData, pieData, subtext){
		var selected = {};
	    for (var i = 0; i < xAxisData.length; i++) {
	        selected[xAxisData[i]] = i < 10;//默认选择10个机构，防止数据堆叠问题
	    }
		var option = {
			    tooltip : {
			        trigger: 'item',
			        formatter: "{a} <br/>{b} : {c} ({d}%)"
			    },
			    title : {
			        text: '指标结构分析---' + subtext,
			        x:'center'
			    },
			    legend: {
			        orient: 'vertical',
			        x: 'left',
			        type: 'scroll',
			        top: 20,
			        bottom: 20,
			        data: xAxisData,
			        selected: selected
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            dataView : {show: true, readOnly: false},
			            saveAsImage : {show: true},
			            myTool: {
			                show: true,
			                title: '上一层',
			                icon: 'image://${ctx}/js/echarts4/before.png',
			                onclick: function (){
			                    if(structureDataList.length > 1){
			                    	var lastData = structureDataList[structureDataList.length-2];
			                    	if(lastData && lastData.pieData){
			                    		structureDataList.splice(structureDataList.length-1,1);
			                    		delete structureDataMap[indexNo];
			                    		rptIndexNm = lastData.indexNm;
			                    		indexNo = lastData.indexNo;
			                    		indexVerId = lastData.indexVerId;
			                    		initStructureEcharts(lastData.xAxisData, lastData.pieData, lastData.indexNm);
			                    		//idxTrend();
			            				//idxOrg();
			            				//idxDetailed();
			                    	}
			                    }else{
			                    	BIONE.tip("已经是最顶层指标");
			                    }
			                }
			            }
			        }
			    },
			    series : [
			        {
			            name: '指标结构分析',
			            type: 'pie',
			            radius : '55%',
			            center: ['50%', '60%'],
			            data: pieData,
			            itemStyle: {
			                emphasis: {
			                    shadowBlur: 10,
			                    shadowOffsetX: 0,
			                    shadowColor: 'rgba(0, 0, 0, 0.5)'
			                }
			            }
			        }
			    ]
			};
		if(structureChart){
			echarts.dispose(structureChart);
		}
		structureChart = echarts.init(document.getElementById('idxStructure'));
		structureChart.on('click', function (params) {
			if(rptIndexNm == params.name){
				BIONE.tip("已经是最底层指标");
			}else{
				rptIndexNm = params.name;
				indexNo = params.data.idxNo;
				indexVerId = params.data.verId;
				//idxTrend();
				idxStructure();
				//idxOrg();
				//idxDetailed();
			}
		});
		structureChart.setOption(option);
	}
	
	//指标趋势分析
	function idxTrend(){
		var today = dateMap["本期"];
		if(today){
			dataDate = today;
		}
		isTrend = false;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/getIdxLastData",
			data : {
				dataDate : dataDate,
				orgNo : orgNo,
				rptIndexNo : indexNo,
				dataUnit : dataUnit,
				dataPrecision : dataPrecision
			},
			type : "POST",
			beforeSend : function() {
				BIONE.showLoading("分析中，请稍等");
			},
			success : function(resultMap) {
				var lastYear = "0";//去年同期
				var thisYearFirst = "0";//年初
				var lastLastDate = "0";//上上期
				var lastDate = "0";//上期
				var nowDate = "0";//本期
				if(resultMap && resultMap.lastYear){
					lastYear = resultMap.lastYear;
				}
				if(resultMap && resultMap.thisYearFirst){
					thisYearFirst = resultMap.thisYearFirst;
				}
				if(resultMap && resultMap.lastLastDate){
					lastLastDate = resultMap.lastLastDate;
				}
				if(resultMap && resultMap.lastDate){
					lastDate = resultMap.lastDate;
				}
				if(resultMap && resultMap.nowDate){
					nowDate = resultMap.nowDate;
				}
				if(resultMap && resultMap.dateMap){
					dateMap = resultMap.dateMap;
				}
				initTrendEcharts(lastYear, thisYearFirst, lastLastDate, lastDate, nowDate);
				isTrend = true;
				BIONE.loading = false;
				BIONE.hideLoading();
			}
		});
	}
	
	//初始化趋势信息图
	function initTrendEcharts(lastYear, thisYearFirst, lastLastDate, lastDate, nowDate){
		var option = {
				color: ['#3398DB'],
			    tooltip : {
			        trigger: 'axis'
			    },
			    title : {
			        text: '指标趋势分析---' + dataDate,
			        x:'center'
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            dataView : {show: true, readOnly: false},
			            magicType : {show: true, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			    xAxis : [
			        {
			            type : 'category',
			            data : ['去年同期', '年初', '上上期', '上期', '本期']
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series: [
			        {            
			        	name:'指标数据',
			            type:'line',
			            barWidth: '40',
			            data:[lastYear, thisYearFirst, lastLastDate, lastDate, nowDate],
			            markPoint : {
			                data : [
			                    {type : 'max', name: '最大值'},
			                    {type : 'min', name: '最小值'}
			                ]
			            },
			            markLine : {
			                data : [
			                    {type : 'average', name: '平均值'}
			                ]
			            }
			        }
			    ]
			};
		trendChart = echarts.init(document.getElementById('idxTrend'));
		trendChart.on('click', function (params) {
			var clickDate = dateMap[params.name];
			if(clickDate && (clickDate != dataDate)){
				dataDate = clickDate;
				var option = trendChart.getOption();
				option.title[0].text = '指标趋势分析---' + dataDate;
				trendChart.setOption(option);
				//idxStructure();
				//idxOrg();
				//idxDetailed();
			}
		});
		trendChart.setOption(option);
	}
	
	//指标机构分析
	function idxOrg(parentOrgNm){
		isOrg = false;
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/getOrgDrillData",
			dataType : 'json',
			data : {
				indexNo : indexNo,
				orgType : orgType,
				dataDate : dataDate,
				orgNo : orgNo,
				parentOrgNm : parentOrgNm,
				dataUnit : dataUnit,
				dataPrecision : dataPrecision
			},
			type : "POST",
			beforeSend : function() {
				BIONE.showLoading("分析中，请稍等");
			},
			success : function(result) {
				if(result && result.pieData){
					if(parentOrgNm){
						orgNm = parentOrgNm;
						orgNo = result.orgNo;
						//idxTrend();
						//idxStructure();
						//idxDetailed();
					}
					var data = orgDataMap[orgNo];
					if(!data){
						orgDataMap[orgNo] = result;
						orgDataList.push(result);
					}
					result.orgNm = orgNm;
					initOrgEcharts(result.xAxisData, result.seriesData, orgNm);
				}
				isOrg = true;
				BIONE.loading = false;
				BIONE.hideLoading();
			}
		});
	}
	
	//初始化机构信息图
	function initOrgEcharts(xAxisData, seriesData, subtext){
		var option = {
				color: ['#3398DB'],
			    tooltip : {
			        trigger: 'axis'
			    },
			    title : {
			        text: '机构数据下钻---' + subtext,
			        x:'center'
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            dataView : {show: true, readOnly: false},
			            magicType : {show: true, type: ['line', 'bar']},
			            restore : {show: true},
			            saveAsImage : {show: true},
			            myTool: {
			                show: true,
			                title: '上一层',
			                icon: 'image://${ctx}/js/echarts4/before.png',
			                onclick: function (){
			                    if(orgDataList.length > 1){
			                    	var lastData = orgDataList[orgDataList.length-2];
			                    	if(lastData && lastData.pieData){
			                    		delete orgDataMap[orgNo];
			                    	 	orgDataList.splice(orgDataList.length-1,1);
			                    		orgNo = lastData.orgNo;
			                    		orgNm = lastData.orgNm;
			                         	initOrgEcharts(lastData.xAxisData, lastData.seriesData, lastData.orgNm);
			                        	//idxTrend();
			                        	//idxStructure();
			    						//idxDetailed();
			                    	}
			                    }else{
			                    	BIONE.tip("已经是最顶层子机构，无法继续向上");
			                    }
			                }
			            }
			        }
			    },
			    xAxis : [
			        {
			            type : 'category',
			            data : xAxisData
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    series: [
			        {            
			        	name:'指标数据',
			            type:'bar',
			            barWidth: '40',
			            data:seriesData,
			            markPoint : {
			                data : [
			                    {type : 'max', name: '最大值'},
			                    {type : 'min', name: '最小值'}
			                ]
			            },
			            markLine : {
			                data : [
			                    {type : 'average', name: '平均值'}
			                ]
			            }
			        }
			    ]
			};
		if(orgChat){
			echarts.dispose(orgChat);
		}
		orgChat = echarts.init(document.getElementById('idxOrg'));
		orgChat.on('click', function (params) {
			var drillOrgNm = params.name;
			if(drillOrgNm && orgNm != drillOrgNm){
				idxOrg(drillOrgNm);
			}else{
				BIONE.tip("已经是最底层机构，无法继续向下");
			}
		});
		orgChat.setOption(option);
	}
	
	//指标明细分析
	function idxDetailed(){
		isDetail = false;
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/getIdxDetailed",
			dataType : 'json',
			data : {
				indexNo : indexNo,
				dataDate : dataDate,
				orgNo : orgNo,
				dataUnit : dataUnit,
				indexVerId : indexVerId,
				dataPrecision : dataPrecision
			},
			type : "POST",
			beforeSend : function() {
				BIONE.showLoading("分析中，请稍等");
			},
			success : function(result) {
				initDetailGrid();
				columns = [];
				if (result && result.drillData) {
					var drillData = result.drillData;
					var resuleCol = result.columns;
					for(var i = 0; i < resuleCol.length; i++){
						columns.push({
							isSort: true,
							display: resuleCol[i],
							name: resuleCol[i],
							text : resuleCol[i],
							width : 100
						});
					}
					detailGrid.addRows(drillData);
				}
				if(columns.length > 0){
					detailGrid.set("columns", columns);
					detailGrid.reload();
				}else{
					BIONE.tip("缺少配置数据，请先进行配置！")
				}
				isDetail = true;
				BIONE.loading = false;
				BIONE.hideLoading();
			}
		});
	}
	
	//初始化明细Grid
	function initDetailGrid(){
		detailGrid = $('#idxDetailedGrid').ligerGrid(
				{
					columns : columns,
					usePager : false,
					checkbox : false,
					data :{
						Rows :[]
					},
					dataAction : 'local',
					allowHideColumn : false,
					delayLoad : false,
					mouseoverRowCssClass : null
				});
	}
</script>
</head>
<body>
	<div id="template.center" style="width: 100%;height: 100%;">
		<!-- <div id="idxStructure" style="float: left;display:none"></div>
		<div id="idxTrend" style="width: 50%;height: 50%;float: right;"></div>
		<div id="idxOrg" style="width: 50%;height: 50%;float: left"></div>
		<div id="idxDetailed" style="width: 50%;height: 50%;float: right;text-align: center;">
			<b id="idxDetailedTel" style="font-size: 14pt;font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">明细数据分析</b>
			<div id="idxDetailedGrid" style="margin-right: 10px;overflow: auto;margin-bottom: 10px;"></div>
		</div> -->
	</div>
</body>
</html>