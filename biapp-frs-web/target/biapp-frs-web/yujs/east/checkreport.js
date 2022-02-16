function AfterInit() {
	JSPFree.createTabb("d1", [ "帐期规则交叉表", "按帐期统计", "按规则类型统计" ]); //创建多页签
	//第1个页签
	JSPFree.createSplit("d1_1", "上下", 60);
	JSPFree.createBillQuery("d1_1_A", "east_cr_summer_DTRULE");
	document.getElementById("d1_1_B").innerHTML = "<div style=\"width:100%;text-align:center;\">请输入条件查询数据!</div>";

	JSPFree.bindQueryEvent(d1_1_A_BillQuery, function(_par) {
		if(_par != null){
			//console.log("查询框传入的查询条件[" + _par + "]");
			var jso_par = {
				SQLWhere : _par,
				isChart : true
			};
			var jso_data = JSPFree.doClassMethodCall(
					"com.yusys.east.checkresult.summary.service.CheckReportBS",
					"getDataDtRuleReport", jso_par);
			var str_html = jso_data.tablehtml; //返回的html
			//图表
			var sb_html = "<div>" + "<div id='mainchart1'></div>" + "</div>"
					+ "<div>" + "<div id='mainchart2'></div>" + "</div> " + "<div>"
					+ "<div id='mainchart3' style='float:left;width : 50%;'></div>"
					+ "<div id='mainchart4' style='float:left;width : 50%;'></div>"
					+ "</div>";
			var html = sb_html + str_html.substring(0, str_html.indexOf("{") - 1);
			document.getElementById("d1_1_B").innerHTML = html;

			var charTable = JSON.parse(str_html.substring(str_html.indexOf("{")));
			var legendArr = [];
			var seriesData = [];
			var xAxis1 = charTable.xAxis1;
			var yAxis1 = charTable.yAxis1;
			var xAxis2 = charTable.xAxis2;
			var yAxis2 = charTable.yAxis2;
			var seriesData1 = [];
			var seriesData2 = [];
			for ( var obj in yAxis1) {
				var series = new Object();
				series.name = xAxis1[obj];
				series.value = yAxis1[obj];
				seriesData1.push(series);
			}
			for ( var obj in yAxis2) {
				var series = new Object();
				series.name = xAxis2[obj];
				series.value = yAxis2[obj];
				seriesData2.push(series);
			}

			$("#mainchart1").css("height", "300px");
			var myChart1 = echarts.init(document.getElementById('mainchart1'));
			// 折线、柱状图
			option1 = {
				title : {
					text : '帐期'
				},
				color : [ '#3398DB' ],
				tooltip : {
					trigger : 'axis',
					axisPointer : { // 坐标轴指示器，坐标轴触发有效
						type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
					}
				},
				grid : {
					left : '3%',
					right : '4%',
					bottom : '3%',
					containLabel : true
				},
				xAxis : [ {
					type : 'category',
					data : xAxis1,
					axisTick : {
						alignWithLabel : true
					}
				} ],
				yAxis : [ {
					type : 'value'
				} ],
				series : [ {
					name : '',
					type : 'bar',
					barWidth : '60%',
					data : yAxis1
				} ]
			};

			myChart1.setOption(option1);

			$("#mainchart2").css("height", "300px");
			var myChart2 = echarts.init(document.getElementById('mainchart2'));
			option2 = {
				title : {
					text : '规则'
				},
				color : [ '#3398DB' ],
				tooltip : {
					trigger : 'axis',
					axisPointer : { // 坐标轴指示器，坐标轴触发有效
						type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
					}
				},
				grid : {
					left : '3%',
					right : '4%',
					bottom : '3%',
					containLabel : true
				},
				xAxis : [ {
					type : 'category',
					data : xAxis2,
					axisTick : {
						alignWithLabel : true
					}
				} ],
				yAxis : [ {
					type : 'value'
				} ],
				series : [ {
					name : '',
					type : 'bar',
					barWidth : '60%',
					data : yAxis2
				} ]
			};
			myChart2.setOption(option2);

			$("#mainchart3").css("height", "300px");
			var myChart3 = echarts.init(document.getElementById('mainchart3'));
			option3 = {
				title : {
					text : '帐期'
				},
				tooltip : {
					trigger : 'item',
					formatter : "{a} <br/>{b}: {c} ({d}%)"
				},
				//			    legend: {
				//			        orient: 'vertical',
				//			        x: 'left',
				//			        data : xAxis1
				//			    },
				series : [ {
					name : '',
					type : 'pie',
					radius : [ '50%', '70%' ],
					avoidLabelOverlap : false,
					label : {
						normal : {
							show : false,
							position : 'center'
						},
						emphasis : {
							show : true,
							textStyle : {
								fontSize : '16',
								fontWeight : 'bold'
							}
						}
					},
					labelLine : {
						normal : {
							show : false
						}
					},
					data : seriesData1
				} ]
			};
			myChart3.setOption(option3);

			$("#mainchart4").css("height", "300px");
			var myChart4 = echarts.init(document.getElementById('mainchart4'));
			option4 = {
				title : {
					text : '规则'
				},
				tooltip : {
					trigger : 'item',
					formatter : "{a} <br/>{b}: {c} ({d}%)"
				},
				//			    legend: {
				//			        orient: 'vertical',
				//			        x: 'left',
				//			        data : xAxis2
				//			    },
				series : [ {
					name : '',
					type : 'pie',
					radius : [ '50%', '70%' ],
					avoidLabelOverlap : false,
					label : {
						normal : {
							show : false,
							position : 'center'
						},
						emphasis : {
							show : true,
							textStyle : {
								fontSize : '16',
								fontWeight : 'bold'
							}
						}
					},
					labelLine : {
						normal : {
							show : false
						}
					},
					data : seriesData2
				} ]
			};
			myChart4.setOption(option4);
		}
	});

	//第2个页签
	JSPFree.createSplit("d1_2", "上下", 60);
	JSPFree.createBillQuery("d1_2_A", "east_cr_summer_DTTAB");
	document.getElementById("d1_2_B").innerHTML = "<div style=\"width:100%;text-align:center;\">请输入条件查询数据!</div>";

	JSPFree.bindQueryEvent(d1_2_A_BillQuery, function(_par) {
		if(_par != null){
			//console.log("查询框传入的查询条件[" + _par + "]");
			var jso_par = {
				SQLWhere : _par,
				isChart : true
			};
			var jso_data = JSPFree.doClassMethodCall(
					"com.yusys.east.checkresult.summary.service.CheckReportBS",
					"getDataDtTableReport", jso_par);
			var str_html = jso_data.tablehtml; //返回的html
			var sb_html = "<div>" + "<div id='mainchart5'></div>" + "</div>"
					+ "<div>" + "<div id='mainchart6'></div>" + "</div> " + "<div>"
					+ "<div id='mainchart7' style='float:left;width : 50%;'></div>"
					+ "<div id='mainchart8' style='float:left;width : 50%;'></div>"
					+ "</div>";
			var html = sb_html + str_html.substring(0, str_html.indexOf("{") - 1);
			document.getElementById("d1_2_B").innerHTML = html;
	
			var charTable = JSON.parse(str_html.substring(str_html.indexOf("{")));
			var legendArr = [];
			var seriesData = [];
			var xAxis1 = charTable.xAxis1;
			var yAxis1 = charTable.yAxis1;
			var xAxis2 = charTable.xAxis2;
			var yAxis2 = charTable.yAxis2;
			var seriesData1 = [];
			var seriesData2 = [];
			for ( var obj in yAxis1) {
				var series = new Object();
				series.name = xAxis1[obj];
				series.value = yAxis1[obj];
				seriesData1.push(series);
			}
			for ( var obj in yAxis2) {
				var series = new Object();
				series.name = xAxis2[obj];
				series.value = yAxis2[obj];
				seriesData2.push(series);
			}
	
			$("#mainchart5").css("height", "300px");
			var myChart5 = echarts.init(document.getElementById('mainchart5'));
			// 折线、柱状图
			option5 = {
				title : {
					text : '帐期'
				},
				color : [ '#3398DB' ],
				tooltip : {
					trigger : 'axis',
					axisPointer : { // 坐标轴指示器，坐标轴触发有效
						type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
					}
				},
				grid : {
					left : '3%',
					right : '4%',
					bottom : '3%',
					containLabel : true
				},
				xAxis : [ {
					type : 'category',
					data : xAxis1,
					axisTick : {
						alignWithLabel : true
					}
				} ],
				yAxis : [ {
					type : 'value'
				} ],
				series : [ {
					name : '',
					type : 'bar',
					barWidth : '60%',
					data : yAxis1
				} ]
			};
	
			myChart5.setOption(option5);
	
			$("#mainchart6").css("height", "300px");
			var myChart6 = echarts.init(document.getElementById('mainchart6'));
			option6 = {
				title : {
					text : '表名'
				},
				color : [ '#3398DB' ],
				tooltip : {
					trigger : 'axis',
					axisPointer : { // 坐标轴指示器，坐标轴触发有效
						type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
					}
				},
				grid : {
					left : '3%',
					right : '4%',
					bottom : '3%',
					containLabel : true
				},
				xAxis : [ {
					type : 'category',
					data : xAxis2,
					axisTick : {
						alignWithLabel : true
					}
				} ],
				yAxis : [ {
					type : 'value'
				} ],
				series : [ {
					name : '',
					type : 'bar',
					barWidth : '60%',
					data : yAxis2
				} ]
			};
			myChart6.setOption(option6);
	
			$("#mainchart7").css("height", "300px");
			var myChart7 = echarts.init(document.getElementById('mainchart7'));
			option7 = {
				title : {
					text : '帐期'
				},
				tooltip : {
					trigger : 'item',
					formatter : "{a} <br/>{b}: {c} ({d}%)"
				},
				//  legend: {
				//      orient: 'vertical',
				//      x: 'left',
				//      data : xAxis1
				//  },
				series : [ {
					name : '',
					type : 'pie',
					radius : [ '50%', '70%' ],
					avoidLabelOverlap : false,
					label : {
						normal : {
							show : false,
							position : 'center'
						},
						emphasis : {
							show : true,
							textStyle : {
								fontSize : '16',
								fontWeight : 'bold'
							}
						}
					},
					labelLine : {
						normal : {
							show : false
						}
					},
					data : seriesData1
				} ]
			};
			myChart7.setOption(option7);
	
			$("#mainchart8").css("height", "300px");
			var myChart8 = echarts.init(document.getElementById('mainchart8'));
			option8 = {
				title : {
					text : '表名'
				},
				tooltip : {
					trigger : 'item',
					formatter : "{a} <br/>{b}: {c} ({d}%)"
				},
				//  legend: {
				//      orient: 'vertical',
				//      x: 'left',
				//      data : xAxis2
				//  },
				series : [ {
					name : '',
					type : 'pie',
					radius : [ '50%', '70%' ],
					avoidLabelOverlap : false,
					label : {
						normal : {
							show : false,
							position : 'center'
						},
						emphasis : {
							show : true,
							textStyle : {
								fontSize : '16',
								fontWeight : 'bold'
							}
						}
					},
					labelLine : {
						normal : {
							show : false
						}
					},
					data : seriesData2
				} ]
			};
			myChart8.setOption(option8);
		}
	});
	//第3个页签
	JSPFree.createSplit("d1_3", "上下", 60);
	JSPFree.createBillQuery("d1_3_A", "east_cr_summer_RULETAB");
	document.getElementById("d1_3_B").innerHTML = "<div style=\"width:100%;text-align:center;\">请输入条件查询数据!</div>";

	JSPFree
			.bindQueryEvent(
					d1_3_A_BillQuery,
					function(_par) {
						if(_par != null){
							//console.log("查询框传入的查询条件[" + _par + "]");
							var jso_par = {
								SQLWhere : _par,
								isChart : true
							};
							var jso_data = JSPFree
									.doClassMethodCall(
											"com.yusys.east.checkresult.summary.service.CheckReportBS",
											"getRuleTableReport", jso_par);
							var str_html = jso_data.tablehtml; //返回的html
							var sb_html = "<div>"
									+ "<div id='mainchart9'></div>"
									+ "</div>"
									+ "<div>"
									+ "<div id='mainchart10'></div>"
									+ "</div> "
									+ "<div>"
									+ "<div id='mainchart11' style='float:left;width : 50%;'></div>"
									+ "<div id='mainchart12' style='float:left;width : 50%;'></div>"
									+ "</div>";
							var html = sb_html
									+ str_html.substring(0,
											str_html.indexOf("{") - 1);
							document.getElementById("d1_3_B").innerHTML = html;
	
							var charTable = JSON.parse(str_html.substring(str_html
									.indexOf("{")));
							var legendArr = [];
							var seriesData = [];
							var xAxis1 = charTable.xAxis1;
							var yAxis1 = charTable.yAxis1;
							var xAxis2 = charTable.xAxis2;
							var yAxis2 = charTable.yAxis2;
							var seriesData1 = [];
							var seriesData2 = [];
							for ( var obj in yAxis1) {
								var series = new Object();
								series.name = xAxis1[obj];
								series.value = yAxis1[obj];
								seriesData1.push(series);
							}
							for ( var obj in yAxis2) {
								var series = new Object();
								series.name = xAxis2[obj];
								series.value = yAxis2[obj];
								seriesData2.push(series);
							}
	
							$("#mainchart9").css("height", "300px");
							var myChart9 = echarts.init(document
									.getElementById('mainchart9'));
							// 折线、柱状图
							option9 = {
								title : {
									text : '规则'
								},
								color : [ '#3398DB' ],
								tooltip : {
									trigger : 'axis',
									axisPointer : { // 坐标轴指示器，坐标轴触发有效
										type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
									}
								},
								grid : {
									left : '3%',
									right : '4%',
									bottom : '3%',
									containLabel : true
								},
								xAxis : [ {
									type : 'category',
									data : xAxis1,
									axisTick : {
										alignWithLabel : true
									}
								} ],
								yAxis : [ {
									type : 'value'
								} ],
								series : [ {
									name : '',
									type : 'bar',
									barWidth : '60%',
									data : yAxis1
								} ]
							};
	
							myChart9.setOption(option9);
	
							$("#mainchart10").css("height", "300px");
							var myChart10 = echarts.init(document
									.getElementById('mainchart10'));
							option10 = {
								title : {
									text : '表名'
								},
								color : [ '#3398DB' ],
								tooltip : {
									trigger : 'axis',
									axisPointer : { // 坐标轴指示器，坐标轴触发有效
										type : 'shadow' // 默认为直线，可选为：'line' | 'shadow'
									}
								},
								grid : {
									left : '3%',
									right : '4%',
									bottom : '3%',
									containLabel : true
								},
								xAxis : [ {
									type : 'category',
									data : xAxis2,
									axisTick : {
										alignWithLabel : true
									}
								} ],
								yAxis : [ {
									type : 'value'
								} ],
								series : [ {
									name : '',
									type : 'bar',
									barWidth : '60%',
									data : yAxis2
								} ]
							};
							myChart10.setOption(option10);
	
							$("#mainchart11").css("height", "300px");
							var myChart11 = echarts.init(document
									.getElementById('mainchart11'));
							option11 = {
								title : {
									text : '规则'
								},
								tooltip : {
									trigger : 'item',
									formatter : "{a} <br/>{b}: {c} ({d}%)"
								},
								//  legend: {
								//      orient: 'vertical',
								//      x: 'left',
								//      data : xAxis1
								//  },
								series : [ {
									name : '',
									type : 'pie',
									radius : [ '50%', '70%' ],
									avoidLabelOverlap : false,
									label : {
										normal : {
											show : false,
											position : 'center'
										},
										emphasis : {
											show : true,
											textStyle : {
												fontSize : '16',
												fontWeight : 'bold'
											}
										}
									},
									labelLine : {
										normal : {
											show : false
										}
									},
									data : seriesData1
								} ]
							};
							myChart11.setOption(option11);
	
							$("#mainchart12").css("height", "300px");
							var myChart12 = echarts.init(document
									.getElementById('mainchart12'));
							option12 = {
								title : {
									text : '表名'
								},
								tooltip : {
									trigger : 'item',
									formatter : "{a} <br/>{b}: {c} ({d}%)"
								},
								//  legend: {
								//      orient: 'vertical',
								//      x: 'left',
								//      data : xAxis2
								//  },
								series : [ {
									name : '',
									type : 'pie',
									radius : [ '50%', '70%' ],
									avoidLabelOverlap : false,
									label : {
										normal : {
											show : false,
											position : 'center'
										},
										emphasis : {
											show : true,
											textStyle : {
												fontSize : '16',
												fontWeight : 'bold'
											}
										}
									},
									labelLine : {
										normal : {
											show : false
										}
									},
									data : seriesData2
								} ]
							};
							myChart12.setOption(option12);
						}
					});
}
