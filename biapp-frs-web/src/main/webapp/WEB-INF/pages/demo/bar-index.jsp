<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<!-- <meta name="decorator" content="/template/template1.jsp"> -->

    <meta charset="utf-8">
    <title>对公存款</title>

    <meta name="description" content="morris charts">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	
	<script type="text/javascript" src="${ctx}/js/echarts/js/jquery.min.js"></script>
    <script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
</head>
<body>
	<div style="width: 100%; height: 150px;">
        <div style="display: block; float: left; width: 30%; height: inherit;">
            <div style="height: 30px; background: #B74442;">
                <div style="margin: auto; float: none; display: table; font-size: 16px; font-family: 微软雅黑; color: white; font-weight: bold; padding-top: 3px;">对公存款</div>
            </div>
            <div style="height: 110px; background: #F9F9F9;">
                <div style="margin: auto; float: none; display: table; height: 15px;"></div>
                <div style="margin: auto; float: none; display: table; font-size: 24px; font-family: 微软雅黑; font-weight: bold; color: #DE3500;">73667.13</div>
                <div style="margin: auto; float: none; display: table; font-size: 10.5px; font-family: 微软雅黑; color: #CE3D3D; padding-top: 5px;">境内口径</div>
                <div style="margin: auto; float: none; display: table; font-size: 9px; font-family: 微软雅黑; padding-top: 5px;">数据日期：2016.01.27</div>
            </div>
        </div>
        <div style="display: block; float: left; width: 70%; height: inherit;">
            <div style="height: 37px;">
                <div style="width: 79%; height: 100%; float: right; margin-left: 3px;">
                    <div style="width: 34%; height: 100%; margin: auto; float: left; display: block;">
                        <div style="margin: auto; float: left; display: block; width: 100%; height: 16px; font-family: 微软雅黑; font-size: 12px;">
                            日期：
                        </div>
                        <div style="margin: auto; float: left; display: block; width: 100%; height: 20px;">
                            <select>
                                <option>2016年</option><option>2015年</option><option>2014年</option>
                            </select>
                            <select>
                                <option>01月</option><option>02月</option><option>03月</option><option>04月</option>
                                <option>05月</option><option>06月</option><option>07月</option><option>08月</option>
                                <option>09月</option><option>10月</option><option>11月</option><option>12月</option>
                            </select>
                            <select>
                                <option>01日</option><option>02日</option><option>03日</option><option>04日</option><option>05日</option>
                                <option>06日</option><option>07日</option><option>08日</option><option>09日</option><option>10日</option>
                                <option>11日</option><option>12日</option><option>13日</option><option>14日</option><option>15日</option>
                                <option>16日</option><option>17日</option><option>18日</option><option>19日</option><option>20日</option>
                                <option>21日</option><option>22日</option><option>23日</option><option>24日</option><option>25日</option>
                                <option>26日</option><option selected>27日</option><option>28日</option><option>29日</option><option>30日</option>
                            </select>
                        </div>
                    </div>
                    <div style="width: 4%; height: 100%; margin: auto; float: left; display: block;"></div>
                    <div style="width: 18%; height: 100%; margin: auto; float: left; display: block;">
                        <div style="margin: auto; float: left; display: block; width: 100%; height: 16px; font-family: 微软雅黑; font-size: 12px;">
                            币种：
                        </div>
                        <div style="margin: auto; float: left; display: block; width: 100%; height: 20px;">
                            <select>
                                <option>人民币</option><option>美元</option><option>欧元</option><option>英磅</option>
                            </select>
                        </div>
                    </div>
                    <div style="width: 18%; height: 100%; margin: auto; float: left; display: block;">
                        <div style="margin: auto; float: left; display: block; width: 100%; height: 16px; font-family: 微软雅黑; font-size: 12px;">
                            口径：
                        </div>
                        <div style="margin: auto; float: left; display: block; width: 100%; height: 20px;">
                            <select>
                                <option>境内口径</option><option>境外口径</option><option>其他口径</option>
                            </select>
                        </div>
                    </div>
                    <div style="width: 18%; height: 100%; margin: auto; float: left; display: block;">
                        <div style="margin: auto; float: left; display: block; width: 100%; height: 16px; font-family: 微软雅黑; font-size: 12px;">
                            机构：
                        </div>
                        <div style="margin: auto; float: left; display: block; width: 100%; height: 20px;">
                            <select>
                                <option>总行</option><option>分行</option><option>支行</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div style="width: 18%; height: 100%; float: right; margin-right: 6px;">
                	<div style="width: 20px; margin-top: 9px; float: left;"><img src="${ctx}/images/classics/icons/pie_chart_48.png" style="display: block; float: none; width: 20px;" /></div>
                    <div style="margin: 0 5px; float: left; display: table; font-family: 微软雅黑; font-size: 14px; color: #E20000; line-height: 37px;">查看同业数据</div>
                </div>
            </div>
            <div style="height: 103px;">
                <div style="width: 19%; height: 100%; float: right; display: block; margin-right: 0px;">
                    <div style="height: 28px; background: #00D05E;">
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 14px; font-weight: bold; color: white; padding-top: 3px;">比去年同期</div>
                    </div>
                    <div style="height: 75px; background: #ECFEE8;">
                        <div style="margin: auto; float: none; display: table; padding-top: 10px; font-family: 微软雅黑; font-size: 12px; font-weight: bold; color: #00B050; line-height: 27.5px;">增量&nbsp;&nbsp;&nbsp;+523.12</div>
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 12px; font-weight: bold; color: #00B050; line-height: 27.5px;">增幅&nbsp;&nbsp;&nbsp;&nbsp;+0.71%</div>
                    </div>
                </div>
                <div style="width: 19%; height: 100%; float: right; display: block; margin-right: 7px;">
                    <div style="height: 28px; background: #00D05E;">
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 14px; font-weight: bold; color: white; padding-top: 3px;">比年初</div>
                    </div>
                    <div style="height: 75px; background: #ECFEE8;">
                        <div style="margin: auto; float: none; display: table; padding-top: 10px; font-family: 微软雅黑; font-size: 12px; font-weight: bold; color: #00B050; line-height: 27.5px;">增量&nbsp;&nbsp;&nbsp;+523.12</div>
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 12px; font-weight: bold; color: #00B050; line-height: 27.5px;">增幅&nbsp;&nbsp;&nbsp;&nbsp;+0.71%</div>
                    </div>
                </div>
                <div style="width: 19%; height: 100%; float: right; display: block; margin-right: 7px;">
                    <div style="height: 28px; background: #00D05E;">
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 14px; font-weight: bold; color: white; padding-top: 3px;">比月初</div>
                    </div>
                    <div style="height: 75px; background: #ECFEE8;">
                        <div style="margin: auto; float: none; display: table; padding-top: 10px; font-family: 微软雅黑; font-size: 12px; font-weight: bold; color: #00B050; line-height: 27.5px;">增量&nbsp;&nbsp;&nbsp;+523.12</div>
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 12px; font-weight: bold; color: #00B050; line-height: 27.5px;">增幅&nbsp;&nbsp;&nbsp;&nbsp;+0.71%</div>
                    </div>
                </div>
                <div style="width: 19%; height: 100%; float: right; display: block; margin-right: 7px;">
                    <div style="height: 28px; background: #FA0000;">
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 14px; font-weight: bold; color: white; padding-top: 3px;">比上日</div>
                    </div>
                    <div style="height: 75px; background: #FFF5F3;">
                        <div style="margin: auto; float: none; display: table; padding-top: 10px; font-family: 微软雅黑; font-size: 12px; font-weight: bold; color: #DE3500; line-height: 27.5px;">71458.35</div>
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 12px; font-weight: bold; color: #DE3500; line-height: 27.5px;">71458.35</div>
                    </div>
                </div>
                <div style="width: 19%; height: 100%; float: right; display: block; margin-right: 7px;">
                    <div style="height: 28px; background: #B84542;">
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 14px; font-weight: bold; color: white; padding-top: 3px;">年日均</div>
                    </div>
                    <div style="height: 75px; background: #FBFBFB;">
                        <div style="margin: auto; float: none; display: table; font-family: 微软雅黑; font-size: 16px; font-weight: bold; color: #C00000; line-height: 75px;">71458.35</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <style type="text/css">
        .l-tab-links{position:relative; height:22px; width:100%; overflow:hidden; border:none;font-family: 微软雅黑;font-size: 13px;}
        .l-tab-links ul{ list-style:none; margin:0; padding:0; width:9999px; height:22px; overflow:hidden; position:absolute; top:0; left:0;}
        .l-tab-links li{ float:left; margin:0; padding:0; height: 22px; line-height:22px; cursor:pointer;background:#E6E6E6; position:relative; overflow:hidden; border:solid 1px grey;  border-top-left-radius: 7px; border-top-right-radius: 7px;}
        .l-tab-links li.l-selected{background:#B84542;}
        .l-tab-links li.l-selected a{ color: white;}
        .l-tab-links li a{ display:block; margin-left: 30px; margin-right: 30px; text-decoration:none; color:#333;}
        .l-tab-links-item-left{ position:absolute;top:0; left:0;width:2px; height:26px; background:url(../images/layout/tabs-item-left-bg.gif)}
        .l-tab-links-item-right{position:absolute;top:0; right:0;width:2px; height:26px;background:url(../images/layout/tabs-item-right-bg.gif)}

    </style>
    <div class="l-tab-links">
        <ul style="left: 0px; ">
            <li class="l-selected" tabid="basic">
                <a>指标概要</a>
                <div class="l-tab-links-item-left"></div>
                <div class="l-tab-links-item-right"></div>
            </li>
            <li class="" tabid="fav">
                <a>机构信息</a>
                <div class="l-tab-links-item-left"></div>
                <div class="l-tab-links-item-right"></div>
            </li>
            <li class="" tabid="fav">
                <a>趋势分析</a>
                <div class="l-tab-links-item-left"></div>
                <div class="l-tab-links-item-right"></div>
            </li>
            <li class="" tabid="fav">
                <a>结构解析</a>
                <div class="l-tab-links-item-left"></div>
                <div class="l-tab-links-item-right"></div>
            </li>
            <li class="" tabid="fav">
                <a>网讯搜索</a>
                <div class="l-tab-links-item-left"></div>
                <div class="l-tab-links-item-right"></div>
            </li>
        </ul>
        <div class="l-tab-switch"></div>
    </div>

    <div style="border: solid 1px gray;">
        <div style="margin-top: 15px; margin-left: 17px; margin-right: 17px; height: 24px; border: none; border-bottom: solid 1px #D99694;">
            <div style="margin: auto; padding: 0 10px; display: block; float: left; font-family: 微软雅黑; font-size: 14px; font-weight: bold;  color: #C81E1D; line-height: 24px; background: #FEF6F0;">去年同期 - 年初值 - 当期余额</div>
            <div style="margin: auto; display: block; float: right;">

            </div>
        </div>
        <div id="main" style="width: 100%; height:315px;"></div>
    </div>
    <script type="text/javascript">
        var chart = echarts.init(document.getElementById('main'));
        chart.setOption({
            /*title : {
                text: '去年同期 - 年初值 - 当期余额'
            },*/
            tooltip : {
                trigger: 'axis'
            },
            legend: {
                data:['同期值','年初值', '当前余额']
            },
            toolbox: {
                show : true,
                feature : {
                    mark : {show: true},
                    dataView : {show: true, readOnly: false},
                    magicType : {show: true, type: ['line', 'bar', 'pie']},
                    restore : {show: true},
                    saveAsImage : {show: true}
                }
            },
            calculable : true,
            xAxis : [
                {
                    type : 'category',
                    data : ['同期值', '年初值', '当前余额']
                }
            ],
            yAxis : [
                {
                    type : 'value'
                }
            ],
            series : [
                {
                    name:'同期值',
                    type:'bar',
                    data:[69459, 72690, 78275]
                },
                {
                    name:'年初值',
                    type:'bar',
                    data:[32980, 40815, 41938]
                },
                {
                    name:'当前余额',
                    type:'bar',
                    data:[27930, 32441, 34214]
                }
            ]
        });
    </script>
</body>
</html>