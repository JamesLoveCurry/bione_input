<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
    <link rel="stylesheet" href="${ctx}/js/echarts/css/beyond.min.css">
    <link rel="stylesheet" href="${ctx}/js/echarts/css/bootstrap.min.css">
    <style>
    	.labels-container {
    		float: right;
    		width: 100px;
    	}
    	
    	.labels-container .label {
    		margin: 0 0 5px 0;
    		display: block;
    	}
    	.page-body .databox{
    		margin: 2px;
    		padding: 0;
    	}
    </style>
</head>
<body>
<div class="page-body" style="padding-top:0;">
	<div class="row">
		<div class="col-lg-12 col-sm-12 col-xs-12">
			<!-- <h6 class="row-title before-blue">关键指标展示</h6>
				<div class="labels-container" style="float: right;">
					<span class="label label-yellow"> 低于同期值 </span> <span
						class="label label-sky"> 高于同期值 </span>
				</div> -->
				<div class="row">
			   <div class="col-sm-4 col-xs-12">
			       <div class="databox databox-xxlg databox-inverted databox-vertical databox-shadowed databox-graded radius-bordered" style="height: 250px;">
			           <div class="databox-top bg-white" style="height: 200px;">
			               <div class="databox-row row-10">
			                   <div id="chart1" class="chart" style="height: 190px; padding: 0; position: relative;"></div>
			               </div>
			           </div>
			           <div class="databox-bottom no-padding bg-white bordered bordered-platinum">
			               <div class="databox-row row-12 no-padding">
			                   <div class="databox-cell cell-6 no-padding text-align-center bg-sky">
			                       <span class="databox-number no-margin">-0.34%</span>
			                       <span class="databox-text no-margin">比年初</span>
			                   </div>
			                   <div class="databox-cell cell-6 no-padding text-align-center bg-orange">
			                       <span class="databox-number no-margin">-0.87%</span>
			                       <span class="databox-text no-margin">比同期</span>
			                   </div>
			               </div>
			           </div>
			       </div>
			   </div>
			   <div class="col-sm-4 col-xs-12">
			       <div class="databox databox-xxlg databox-inverted databox-vertical databox-shadowed databox-graded radius-bordered" style="height: 250px;">
			           <div class="databox-top bg-white" style="height: 200px;">
			               <div class="databox-row row-10">
			                   <div id="chart2" class="chart" style="height: 190px; padding: 0; position: relative;"></div>
			               </div>
			           </div>
			           <div class="databox-bottom no-padding bg-white bordered bordered-platinum">
			               <div class="databox-row row-12 no-padding">
			                   <div class="databox-cell cell-6 no-padding text-align-center bg-sky">
			                       <span class="databox-number no-margin">-0.34%</span>
			                       <span class="databox-text no-margin">比年初</span>
			                   </div>
			                   <div class="databox-cell cell-6 no-padding text-align-center bg-orange">
			                       <span class="databox-number no-margin">-0.87%</span>
			                       <span class="databox-text no-margin">比同期</span>
			                   </div>
			               </div>
			           </div>
			       </div>
			   </div>
			   <div class="col-sm-4 col-xs-12">
			       <div class="databox databox-xxlg databox-inverted databox-vertical databox-shadowed databox-graded radius-bordered" style="height: 250px;">
			           <div class="databox-top bg-white" style="height: 200px;">
			               <div class="databox-row row-10">
			                   <div id="chart3" class="chart" style="height: 190px; padding: 0; position: relative;"></div>
			                </div>
			            </div>
			            <div class="databox-bottom no-padding bg-white bordered bordered-platinum">
			                <div class="databox-row row-12 no-padding">
			                    <div class="databox-cell cell-6 no-padding text-align-center bg-sky">
			                        <span class="databox-number no-margin">-0.34%</span>
			                        <span class="databox-text no-margin">比年初</span>
			                    </div>
			                    <div class="databox-cell cell-6 no-padding text-align-center bg-orange">
			                        <span class="databox-number no-margin">-0.87%</span>
			                        <span class="databox-text no-margin">比同期</span>
			                    </div>
			                </div>
			            </div>
			        </div>
			    </div>
			</div>
			<div class="row">
			   <div class="col-sm-4 col-xs-12">
			       <div class="databox databox-xxlg databox-inverted databox-vertical databox-shadowed databox-graded radius-bordered" style="height: 250px;">
			           <div class="databox-top bg-white" style="height: 200px;">
			               <div class="databox-row row-10">
			                   <div id="chart4" class="chart" style="height: 190px; padding: 0; position: relative;"></div>
			               </div>
			           </div>
			           <div class="databox-bottom no-padding bg-white bordered bordered-platinum">
			               <div class="databox-row row-12 no-padding">
			                   <div class="databox-cell cell-6 no-padding text-align-center bg-sky">
			                       <span class="databox-number no-margin">-0.34%</span>
			                       <span class="databox-text no-margin">比年初</span>
			                   </div>
			                   <div class="databox-cell cell-6 no-padding text-align-center bg-orange">
			                       <span class="databox-number no-margin">-0.87%</span>
			                       <span class="databox-text no-margin">比同期</span>
			                   </div>
			               </div>
			           </div>
			       </div>
			   </div>
			   <div class="col-sm-4 col-xs-12">
			       <div class="databox databox-xxlg databox-inverted databox-vertical databox-shadowed databox-graded radius-bordered" style="height: 250px;">
			           <div class="databox-top bg-white" style="height: 200px;">
			               <div class="databox-row row-10">
			                   <div id="chart5" class="chart" style="height: 190px; padding: 0; position: relative;"></div>
			               </div>
			           </div>
			           <div class="databox-bottom no-padding bg-white bordered bordered-platinum">
			               <div class="databox-row row-12 no-padding">
			                   <div class="databox-cell cell-6 no-padding text-align-center bg-sky">
			                       <span class="databox-number no-margin">-0.34%</span>
			                       <span class="databox-text no-margin">比年初</span>
			                   </div>
			                   <div class="databox-cell cell-6 no-padding text-align-center bg-orange">
			                       <span class="databox-number no-margin">-0.87%</span>
			                       <span class="databox-text no-margin">比同期</span>
			                   </div>
			               </div>
			           </div>
			       </div>
			   </div>
			   <div class="col-sm-4 col-xs-12">
			       <div class="databox databox-xxlg databox-inverted databox-vertical databox-shadowed databox-graded radius-bordered" style="height: 250px;">
			           <div class="databox-top bg-white" style="height: 200px;">
			               <div class="databox-row row-10">
			                   <div id="chart6" class="chart" style="height: 190px; padding: 0; position: relative;"></div>
			                </div>
			            </div>
			            <div class="databox-bottom no-padding bg-white bordered bordered-platinum">
			                <div class="databox-row row-12 no-padding">
			                    <div class="databox-cell cell-6 no-padding text-align-center bg-sky">
			                        <span class="databox-number no-margin">-0.34%</span>
			                        <span class="databox-text no-margin">比年初</span>
			                    </div>
			                    <div class="databox-cell cell-6 no-padding text-align-center bg-orange">
			                        <span class="databox-number no-margin">-0.87%</span>
			                        <span class="databox-text no-margin">比同期</span>
			                    </div>
			                </div>
			            </div>
			        </div>
			    </div>
			</div>
		</div>
	</div>
</div>
</body>
</html>

<script>
    var myChart1 = echarts.init(document.getElementById('chart1'));
    var myChart2 = echarts.init(document.getElementById('chart2'));
    var myChart3 = echarts.init(document.getElementById('chart3'));
    var myChart4 = echarts.init(document.getElementById('chart4'));
    var myChart5 = echarts.init(document.getElementById('chart5'));
    var myChart6 = echarts.init(document.getElementById('chart6'));
    function getOption(data) {
    	return {
            title: {
                show: true,
                text: data.title,
                subtext: data.subTitle,
                x: 'center'
            },
            tooltip : {
                formatter: "{a} <br/>{b} : {c}%"
            },
            series : [
                {
                    name:data.title,
                    type:'gauge',
                    center: ['50%', '60%'],
                    // 最小值
                    min: data.min,
                    // 最大值
                    max: data.max,
                    axisLine: {
                        // 默认显示，属性show控制显示与否
                        show: true,
                        lineStyle: {       // 属性lineStyle控制线条样式
                            color: [[data.synVal/data.max, '#ffce55'], [1, '#11a9cc']],
                            width: 15
                        }
                    },
                    // 分隔线
                    splitLine: {
                        // 默认显示，属性show控制显示与否
                        show: true,
                        // 属性length控制线长
                        length: 15,
                        // 属性lineStyle（详见lineStyle）控制线条样式
                        lineStyle: {
                            color: '#eee',
                            width: 2,
                            type: 'solid'
                        }
                    },
                    detail : {
                        formatter:'{value}%',
                        offsetCenter: [0, '70%'],
                        textStyle: {
                            color: 'auto',
                            fontSize: 24
                        }
                    },
                    data:[{
                        value: data.val
                    }]
                }
            ]
        }
    }

    // 使用刚指定的配置项和数据显示图表。
    myChart1.setOption(getOption({
    	title: '平均股东权益回报率',
    	subTitle: '数据时间：2015年3月31日',
    	min: 0,
    	max: 40,
    	synVal: 24,
    	val: 23.28,
    	initialVal: 25
    }));
    myChart2.setOption(getOption({
    	title: '平均资产回报率', //标题
    	subTitle: '数据时间：2015年3月31日', // 副标题
    	min: 0, // 最大值
    	max: 2, // 最小值
    	synVal: 1.5, // 同期值
    	val: 1.44,  // 值
    	initialVal: 1.4  // 年初值
    }));
    myChart3.setOption(getOption({
    	title: '净利息收益率',
    	subTitle: '数据时间：2015年3月31日',
    	min: 0,
    	max: 4,
    	synVal: 3,
    	val: 2.66,
    	initialVal: 2
    }));
    myChart4.setOption(getOption({
    	title: '成本收入比',
    	subTitle: '数据时间：2015年3月31日',
    	min: 0,
    	max: 40,
    	synVal: 30,
    	val: 28.12,
    	initialVal: 25
    }));
    myChart5.setOption(getOption({
    	title: '风险调整的收益率RAROC',
    	subTitle: '数据时间：2015年3月31日',
    	min: 0,
    	max: 4,
    	synVal: 2.5,
    	val: 2.55,
    	initialVal: 2
    }));
    myChart6.setOption(getOption({
    	title: '净利差',
    	subTitle: '数据时间：2015年3月31日',
    	min: 0,
    	max: 4,
    	synVal: 2.5,
    	val: 2.58,
    	initialVal: 2.4
    }));
</script>