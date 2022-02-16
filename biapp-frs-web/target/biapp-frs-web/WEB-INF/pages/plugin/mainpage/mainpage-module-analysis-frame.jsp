<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.yusys.bione.frame.security.BioneSecurityUtils" %>
<%@ page import="com.yusys.bione.comp.utils.PropertiesUtils" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="${ctx}/js/jquery/jquery-1.7.1.min.js"></script>
<script type="text/javascript" src="/biapp-frs-web/js/echarts/js/echarts.min.js"></script>
<script type="text/javascript" src="${ctx}/plugin/js/idxresultanly/date.js"></script>
<script type="text/javascript" type="text/javascript">

	var paintData = new Array();
	var readyCounts = new Array();
	var inDebug = false;	// 是否用于调试，生产环境里，请设置成false

	var orgNo;
	if (inDebug) {
		orgNo = '701100';
	} else {
		orgNo = '<%=BioneSecurityUtils.getCurrentUserInfo().getOrgNo()%>';			// 机构号
	}

	<%
	
	String tabNo = null;
	if( request.getAttribute("tabNo") != null){
		tabNo = (String)request.getAttribute("tabNo");
	}
	PropertiesUtils pro = new PropertiesUtils("bione-plugin/extension/mainpage-index-analysis.properties");
	%>
	var indexNames = new Array();
	var indexNos = new Array();
	var tabPeriods = new Array();
	var tabTypes = new Array();
	var tabYAxisNames = new Array();
	var tabIndexOnYAxises = new Array();
	var tabYAxisUnits = new Array();
	var tabColors = new Array();
	var tabXAxisDatas = new Array();
	var tabLineWidths = new Array();
	var tabBarWidths = new Array();
	<%
	int tabChartCount = pro.getInteger("tabChartCount" + tabNo, 1);
	if (tabChartCount == 1) {
	%>
	indexNames.push(<%=pro.getProperty("tabIndexName" + tabNo).trim()%>);
	indexNos.push(<%=pro.getProperty("tabIndexNo" + tabNo).trim()%>);
	tabPeriods.push('<%=pro.getProperty("tabPeriod" + tabNo, "1d").trim()%>');
	tabTypes.push('<%=pro.getProperty("tabType" + tabNo).trim()%>');
	tabYAxisNames.push(<%=pro.getProperty("tabYAxisName" + tabNo, "[]").trim()%>);
	tabIndexOnYAxises.push(<%=pro.getProperty("tabIndexOnYAxis" + tabNo, "[]").trim()%>);
	tabYAxisUnits.push(<%=pro.getProperty("tabYAxisUnit" + tabNo, "[]").trim()%>);
	tabColors.push(<%=pro.getProperty("tabColor" + tabNo, "[]").trim()%>);
	tabXAxisDatas.push(<%=pro.getProperty("tabXAxisData" + tabNo, "[]").trim()%>);
	tabLineWidths.push(<%=pro.getInteger("tabLineWidth" + tabNo, 0)%>);
	tabBarWidths.push('<%=pro.getProperty("tabBarWidth" + tabNo, "").trim()%>');
	<%
	} else {
		for (int chartNo = 1; chartNo <= tabChartCount; chartNo ++) {
	%>
	indexNames.push(<%=pro.getProperty("tabIndexName" + tabNo + "_" + chartNo).trim()%>);
	indexNos.push(<%=pro.getProperty("tabIndexNo" + tabNo + "_" + chartNo).trim()%>);
	tabPeriods.push('<%=pro.getProperty("tabPeriod" + tabNo + "_" + chartNo, "1d").trim()%>');
	tabTypes.push('<%=pro.getProperty("tabType" + tabNo + "_" + chartNo).trim()%>');
	tabYAxisNames.push(<%=pro.getProperty("tabYAxisName" + tabNo + "_" + chartNo, "[]").trim()%>);
	tabIndexOnYAxises.push(<%=pro.getProperty("tabIndexOnYAxis" + tabNo + "_" + chartNo, "[]").trim()%>);
	tabYAxisUnits.push(<%=pro.getProperty("tabYAxisUnit" + tabNo + "_" + chartNo, "[]").trim()%>);
	tabColors.push(<%=pro.getProperty("tabColor" + tabNo + "_" + chartNo, "[]").trim()%>);
	tabXAxisDatas.push(<%=pro.getProperty("tabXAxisData" + tabNo + "_" + chartNo, "[]").trim()%>);
	tabLineWidths.push(<%=pro.getInteger("tabLineWidth" + tabNo + "_" + chartNo, 0)%>);
	tabBarWidths.push('<%=pro.getProperty("tabBarWidth" + tabNo + "_" + chartNo, "").trim()%>');
	<%
		}
	}
	%>
	var currency = '<%=pro.getProperty("currency").trim()%>';

	function subDate(srcDate, amount) {
		var count = parseInt(amount.substring(0, amount.length - 1));
		if (amount.charAt(amount.length - 1) == 'd') {
			return new Date(srcDate.getTime() - count * 24 * 60 * 60 * 1000);
		}
		if (amount.charAt(amount.length - 1) == 'm') {
			var year = srcDate.getFullYear();
			var month = srcDate.getMonth() - count;
			var date = srcDate.getDate();
			while (month < 0) {
				year = year - 1;
				month = month + 12;
			}
			while (month >= 12) {
				year = year + 1;
				month = month - 12;
			}
			return new Date(year, month, date);
		}
		if (amount.charAt(amount.length - 1) == 'y') {
			var year = srcDate.getFullYear() - count;
			var month = srcDate.getMonth();
			var date = srcDate.getDate();
			return new Date(year, month, date);
		}
		return null;
	}

	function searchIdx(indexNo, dtStart, dtEnd, functions, successFunc, dimArray){
		var condition = {};
		condition.QueryType = "index";
		condition.DimNo = ['DATE', 'ORG', 'CURRENCY'];
		condition.StartDate = dtStart.Format('yyyyMMdd');
		condition.EndDate = dtEnd.Format('yyyyMMdd');
		var columns = [];
		var searchArg = [];
		searchArg.push({
			DimNo: 'ORG',
			Op: '=',
			Value: [orgNo]
		});
		searchArg.push({
			DimNo: 'CURRENCY',
			Op: '=',
			Value: [currency]
		});
		
		if(dimArray && dimArray.length > 0) {
			for (var i = 0; i < dimArray.length ; i ++) {
				condition.DimNo.push(dimArray[i].dimNo);
				searchArg.push({
					DimNo: dimArray[i].dimNo,
					Op: '=',
					Value: dimArray[i].filter
				})
			}
		}

		columns.push({
			 ColumNo: "column0",
	         IndexNo: indexNo,
	         SortType: '03',
	         SearchArg: searchArg
		});
		
		for (var i = 0; i < functions.length; i ++) {
			if (functions[i] == "A") {
				columns.push({
       				 ColumNo: "column" + (i + 1),
       		         IndexNo: indexNo,
       		      	 SearchArg: searchArg,
       		      	 Calculation: {
       		      		 Formula: functions[i] + "('"+ indexNo +"', 'Y', 'Y')"
       		      	 }
  				});
			} else {
				columns.push({
       				 ColumNo: "column" + (i + 1),
       		         IndexNo: indexNo,
       		      	 SearchArg: searchArg,
       		      	 Calculation: {
       		      		 Formula: functions[i] + "('"+ indexNo +"')"
       		      	 }
  				});
			}
		}
		condition.Colums = columns;
		$.ajax({
			url: '/rpt-web/report/frame/datashow/idx/search/result',
			type: 'post',
			data: {
				p: JSON2.stringify(condition)
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: successFunc,
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	function searchOK(paintData, dtStart, dtEnd, data) {
		if (data && data.Code && data.Code == "0000") {
			// 将结果按日期排序
			data.Msg.sort(function(obj1, obj2) {
				return parseInt(obj1.$DATE) - parseInt(obj2.$DATE);
	    	});
			// 以指定的开始日期开始, 没有数据的日期添‘零’处理
			for (var i = 0, dt = dtStart; dt <= dtEnd; i++, dt = new Date(dt.getTime() + 24 * 60 * 60 * 1000)) {
				var _dt = dt.Format('yyyyMMdd');
				if (! data.Msg[i]) {
					data.Msg.splice(i, 0, {
						rtype: '00',
						$DATE: _dt,
						DATE: _dt.substr(0, 4) + '年' + _dt.substr(4, 2) + '月' + _dt.substr(6, 2) + '日',
						column0: '0'
					});
				} else if (parseInt(_dt) < parseInt(data.Msg[i].$DATE)) {
					data.Msg.splice(i, 0, {
						rtype: '00',
						$DATE: _dt,
						DATE: _dt.substr(0, 4) + '年' + _dt.substr(4, 2) + '月' + _dt.substr(6, 2) + '日',
						$ORG: data.Msg[i].$ORG,
						ORG: data.Msg[i].ORG,
						$CURRENCY: data.Msg[i].$CURRENCY,
						CURRENCY: data.Msg[i].CURRENCY,
						column0: '0'
					});
				}
			}
			for (var i = 0; i < data.Msg.length; i ++) {
				paintData.push(data.Msg[i].column0);
			}
		} else {
			BIONE.tip(data.Msg);
		}
	}

	function getChartOption(chartIdx, dtStart, dtEnd, paintData) {
		// 构造 X轴
		var xAxis = tabXAxisDatas[chartIdx];
		if (xAxis.length == 0) {
			xAxis = new Array();
			for (var dt = dtStart; dt <= dtEnd; dt = new Date(dt.getTime() + 24 * 60 * 60 * 1000)) {
				xAxis.push(dt.Format('M-d'));
			}
		}

		var indexName = indexNames[chartIdx];
		var tabColor = tabColors[chartIdx];
		var tabLineWidth = tabLineWidths[chartIdx];
		var tabBarWidth = tabBarWidths[chartIdx];
		var tabYAxisName = tabYAxisNames[chartIdx];
		var tabIndexOnYAxis = tabIndexOnYAxises[chartIdx];

		var option = {
			grid: {
				left: '3%',
				right: '3%',
				bottom: '3%',
				containLabel: true
			},
			series: new Array()
		};
		if (tabTypes[chartIdx] == 'line' || tabTypes[chartIdx] == 'bar') {
			// 图例
			option.legend = {
				data: indexName
			};
			// X轴
			option.xAxis = {
				type: 'category',
				data: xAxis,
				splitLine: {
					show: false
				}
			};
			// Y轴
			var yAxisCount = 1;
			for (var i = 0; i < tabIndexOnYAxis.length; i ++) {
				if (yAxisCount < tabIndexOnYAxis[i]) {
					yAxisCount = tabIndexOnYAxis[i];
				}
			}
			if (yAxisCount == 1) {
				option.yAxis = {
					type: 'value',
					scale: true
				};
				var yAxisUnit = '';
				if (tabYAxisUnits[chartIdx] && tabYAxisUnits[chartIdx][0]) {
					yAxisUnit = tabYAxisUnits[chartIdx][0];
				}
				if (tabYAxisName[0] && tabYAxisName[0].length > 0) {
					if (yAxisUnit.length == 0) {
						option.yAxis.name = tabYAxisName[0];
					} else {
						option.yAxis.name = tabYAxisName[0] + '(' + yAxisUnit + ')';
					}
				} else if (yAxisUnit.length > 0) {
					option.yAxis.name = '(' + yAxisUnit + ')';
				}
			} else {
				option.yAxis = new Array();
				for (var i = 0; i < yAxisCount; i ++) {
					option.yAxis.push({
						type: 'value',
						scale: true
					});
					var yAxisUnit = '';
					if (tabYAxisUnits[chartIdx] && tabYAxisUnits[chartIdx][i]) {
						yAxisUnit = tabYAxisUnits[chartIdx][i];
					}
					if (tabYAxisName[i] && tabYAxisName[i].length > 0) {
						if (yAxisUnit.length == 0) {
							option.yAxis[i].name = tabYAxisName[i];
						} else {
							option.yAxis[i].name = tabYAxisName[i] + '(' + yAxisUnit + ')';
						}
					} else if (yAxisUnit.length > 0) {
						option.yAxis[i].name = '(' + yAxisUnit + ')';
					}
				}
			}
			// 数据
			for (var i = 0; i < indexName.length; i ++) {
				var seriesItem = {
					type: tabTypes[chartIdx],
					name: indexName[i],
					data: paintData[i]
				};
				if (tabIndexOnYAxis[i]) {
					seriesItem.yAxisIndex = tabIndexOnYAxis[i] - 1;
				}
				if (tabColor.length > 0) {
					seriesItem.itemStyle = {
						normal: {
							color: tabColor[i]
						}
					};
				}
				if (tabLineWidth > 0) {
					seriesItem.lineStyle = {
						normal: {
							width: tabLineWidth
						}
					};
				}
				if (tabBarWidth.length > 0) {
					seriesItem.barWidth = tabBarWidth;
				}
				option.series.push(seriesItem);
			}
		} else if (tabTypes[chartIdx] == 'pie') {
			option.legend = {
				show: false
			};
			option.xAxis = {
				show: false
			};
			option.yAxis = {
				show: false
			};
			var seriesItem = {
				type: tabTypes[chartIdx],
				data: new Array(),
				itemStyle: {
					normal: {
						label: {
							show: true,
							formatter: '{b}({d}%)'
						}
					}
				},
				hoverAnimation: false
			};
			for (var i = 0; i < indexName.length; i ++) {
				seriesItem.data.push({
					name: indexName[i],
					value: paintData[i][0],
				});
				if (tabColor.length > 0) {
					seriesItem.data[i].itemStyle = {
						normal: {
							color: tabColor[i]
						}
					};
				}
			}
			option.series.push(seriesItem);
		}
		return option;
	}

	function startQuery(chartIdx, indexNo, idx, dtStart, dtEnd) {
		searchIdx(indexNo[idx], dtStart, dtEnd, [], function(data) {
			searchOK(paintData[chartIdx][idx], dtStart, dtEnd, data);
			var yAxisNo = 1;
			if (tabIndexOnYAxises[chartIdx] && tabIndexOnYAxises[chartIdx][idx]) {
				yAxisNo = tabIndexOnYAxises[chartIdx][idx];
			}
			if (tabYAxisUnits[chartIdx] && tabYAxisUnits[chartIdx][yAxisNo - 1]) {
				if (tabYAxisUnits[chartIdx][yAxisNo - 1] == '万元') {
					for (var i = 0; i < paintData[chartIdx][idx].length; i ++) {
						paintData[chartIdx][idx][i] = paintData[chartIdx][idx][i] / 10000;
					}
				} else if (tabYAxisUnits[chartIdx][yAxisNo - 1] == '亿元') {
					for (var i = 0; i < paintData[chartIdx][idx].length; i ++) {
						paintData[chartIdx][idx][i] = paintData[chartIdx][idx][i] / 100000000;
					}
				}
			}
			if (inDebug && idx > 0) {
				for (var i = 0; i < paintData[chartIdx][idx].length; i ++) {
					paintData[chartIdx][idx][i] = paintData[chartIdx][idx][i] / (idx + 1);
				}
			}
			readyCounts[chartIdx] ++;
			if (readyCounts[chartIdx] == indexNo.length) {
				var option = getChartOption(chartIdx, dtStart, dtEnd, paintData[chartIdx]);
				var chart = echarts.init(document.getElementById('container_' + chartIdx));
				chart.setOption(option);
			}
		});
	}

	$(function() {
		var tabChartCount = <%=tabChartCount%>;
		var widthPercent = 100 / tabChartCount;
		for (var chartIdx = 0; chartIdx < tabChartCount; chartIdx ++) {
			$("#container").append('<td width="' + widthPercent + '%"><div id="container_' + chartIdx + '"></div></td>');
			$("#container_" + chartIdx).height($(document).height() - 37);
			readyCounts.push(0);
			var indexNo = indexNos[chartIdx];
			var tabPeriod = tabPeriods[chartIdx];
			var dtEnd = new Date();
			var dtStart = subDate(dtEnd, tabPeriod);
			var dtEnd = subDate(dtEnd, '1d');
			if (inDebug) {
				var tabNo = <%=tabNo%>;
				dtStart = new Date(2014, 7, 1);
				if (tabNo == 1 && chartIdx == 0) {
					dtEnd = new Date(2014, 7, 7);
				} else {
					dtEnd = new Date(2014, 7, 7);
				}
			}
			paintData.push(new Array());
			for (var i = 0; i < indexNo.length; i ++) {
				paintData[chartIdx].push(new Array());
				startQuery(chartIdx, indexNo, i, dtStart, dtEnd);
			}
		}
	});
</script>
</head>
<body>
	<table border='0' style="width:99%; margin: 5 5"><tr id="container"></tr></table>
</body>
</html>