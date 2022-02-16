<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<meta name="description" content="morris charts">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">

<style type="text/css">
.label-item {
	height: 20px;
	float: left;
	line-height: 20px;
	border: 1px solid #D4D4D4;
	color: #4C4C4C;
	margin: 0 10px 5px 0;
	padding: 0 2px;
	background-color: #F0F0F0;
	border-radius: 2px;
	cursor: default;
	max-width: 130px;
	overflow: hidden;
	text-overflow: ellipsis;
}

.label-item:HOVER {
	border: 1px solid #C0C0C0;
	box-shadow: 0 1px 1px rgba(0, 0, 0, 0.15);
}

.label-item .text {
	float: left;
	height: 20px;
}

.label-item .icon {
	float: left;
	width: 10px;
	height: 10px;
	margin: 5px 0 5px 2px;
	cursor: pointer;
}
#labelPad {
	overflow: hidden;
    clear: both;
    width: 145px;
}
</style>
<script type="text/javascript" src="${ctx}/js/color/jscolor.js"></script>
<script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
<script ext/javascript" src="${ctx}/js/datashow/jQuery.exLabel.js"></script>
<script src="${ctx}/js/echarts/theme/macarons.js"></script>
<script src="${ctx}/js/echarts/theme/vintage.js"></script>
<script src="${ctx}/js/echarts/theme/shine.js"></script>
<script type="text/javascript"
	src="${ctx}/js/backbone/jquery.tmpl.min.js"></script>
<!-- 拖拽代理html -->
<script id="drag-proxy-content" type="text/x-jquery-tmpl" charset="utf-8">
	 <div style="width：150;position: absolute;padding-left: 10px;color: #183152;font-weight: bold;height: 20px;line-height: 19px;overflow: hidden;background: #E5EFFE ;border: 1px solid #DDDDDD;border-top:none; z-index:9999;">
		<span class="dragimage_span" style="position:absolute;width:16px;height:16px;left:5px;top:2px;background:url(@{proxyLabel})" ></span>
		<span style="padding-left: 14px;">@{targetTitle}</span>
	</div>
</script>
<script>
	var queryResult = window.parent.result;
	var columns = window.parent.columns;
	var title = "";
	var labelPad;
	var cdata = {};
	var map = {};
	var indexCol = [];
	var chart = null;
	var xAxis = new Array();
	var ldata = [];
	var series = [];
	var type = "";
	var pid = "";
	var dexinfo ="";
	var boundaryGap = true;
	var indexData = [];
	var color = [];
	var griddata =[];
	// 拖拽标签头图标
	var notAllowedIcon = "/images/classics/icons/cancel.gif";
	var allowedIcon = "/images/classics/icons/accept.png";
	$(function(){
		$.fn.tpl = function(data){
		   $.template('template', $(this).html().replace(/@/g,"$"));
		   return $.tmpl('template', data);
		}
		initLayout();
		initForm();
		initDim();
		initGrid();
		initChartData();
		/* $(".text").each(function(){
			setDragDrop($(this),$("#xinfo"));
		}); */
		
	});
	
	function initDim(){
		labelPad = $('#labelPad').exLabel();
		var colorArray = ['#61a0a8', '#d48265', '#c23531', '#2f4554', '#91c7ae', '#749f83', '#ca8622', '#bda29a', '#6e7074', '#546570'];
		for(var i = 0 ;i < columns.length ; i++){
			if(columns[i].businessType == "DIM")
				labelPad.addLabel({
					flag : true,
					id :　columns[i].name,
					text : columns[i].text,
					css : { 
						"background-color" : "#DFDFDF", 
						"color" :"#333"
					},
					attr: {
						dimNo :columns[i].name
					},
					onClick : labelClick
				});
			if(columns[i].businessType == "INDEX" || columns[i].businessType == "MEASURE"){
				var j = i % 10;
				var chartColor = colorArray[j];
				color.push(chartColor);
				indexData.push({
					name  : columns[i].display,
					color :chartColor
				});
			}
		}
		griddata = {
			Rows : indexData,
			Total : indexData.length
		};
	}
	
	function renderSeries(flag,checkType){
		for(var i in series){
			if(checkType == "minp"){
				if(flag){
					series[i].markPoint.data.push({
						text : "最小值",
						type :"min"
					});
				}
				else{
					for(var j in series[i].markPoint.data){
						if(series[i].markPoint.data[j].type == "min"){
							series[i].markPoint.data.splice(j,1);
							break;
						}
					}
				}
			}
			if(checkType == "maxp"){
				if(flag){
					series[i].markPoint.data.push({
						text : "最大值",
						type :"max"
					});
				}
				else{
					for(var j in series[i].markPoint.data){
						if(series[i].markPoint.data[j].type == "max"){
							series[i].markPoint.data.splice(j,1);
							break;
						}
					}
				}
			}
			if(checkType == "avgline"){
				if(flag){
					series[i].markLine.data.push({
		        		 name: '平均线',
		        	     type: 'average'
		        	 });
				}
				else{
					for(var j in series[i].markLine.data){
						if(series[i].markLine.data[j].type == "average"){
							 series[i].markLine.data.splice(j,1);
							break;
						}
					}
				}
			}
		}
		
	}
	
	
	function renderXinfo(templateStr){
		xAxis = new Array();
		for(var i = 0; i < queryResult.length; i++){
    		var result = queryResult[i];
    		var xInfo = templateStr;
    		for(var j in columns){
    			if(typeof columns[j] != "function"){
    				var colInfo =columns[j];
    				if(colInfo != null){
    					if(colInfo.businessType == "DIM"){
    						xInfo = xInfo.replace(new RegExp("#"+colInfo.name+"#","gm"),result[colInfo.name]);
    					}
    				}
    			}
    		}
    		if(xInfo==""){
    			xInfo= " ";
    		}
    		xAxis.push(xInfo);
    	}
	}
	function initChartData(){
		var height = $("#center").height() -10;
    	$(".chart").height(height);
		for(var i = 0 ;i < columns.length ; i++){
			if(columns[i].businessType == "INDEX" || columns[i].businessType == "MEASURE"){
				indexCol.push(columns[i]);
			}
		}
    	for(var i = 0; i < queryResult.length; i++){
    		var result = queryResult[i];
    		var xInfo = "";
    		for(var j in columns){
    			if(typeof columns[j] != "function"){
    				var colInfo =columns[j];
    				if(colInfo != null){
    					if(colInfo.businessType == "DIM"){
    						xInfo += result[colInfo.name] + " ";	
    					}
    					if(colInfo.businessType == "INDEX" || colInfo.businessType == "MEASURE" ){
    						if(cdata[colInfo.name] == undefined){
    							cdata[colInfo.name] = [];
    							cdata[colInfo.name].push(parseFloat(result[colInfo.name].split(",").join("")));
    						}
    						else{
    							cdata[colInfo.name].push(parseFloat(result[colInfo.name].split(",").join("")));
    						}
    					}
    				}
    			}
    		}
    		xAxis.push(xInfo);
    	}
    	if("${chartType}" == "01"){
    		type = "line";
    		boundaryGap = false;
    	}
    	if("${chartType}" == "02")
    		type = "bar";
    	if("${chartType}" == "03")
    		type = "pie";
    	if("${chartType}" == "04" || "${chartType}" == "05")
    		type = "scatter";
    	if("${chartType}" != "03"){
	    	for( var i = 0; i < indexCol.length ; i++){
	    		
	    		ldata.push(indexCol[i].display);
	    		var maxResult = Math.max.apply(null,cdata[indexCol[i].display]);
	    		var serie = {
			         type: type,
			         showAllSymbol : true,
			         smooth : true,
			         symbolSize: function(val){
			        	 if("${chartType}" != "05")
			        		 return 6;
			        	 else
			        	 	return (Math.round(val / maxResult * 40) <=1 ? 1 : Math.round(val / maxResult * 40));
			         }, 
			         name: indexCol[i].display,
			         data: cdata[indexCol[i].name],
			         markPoint: {
			        	 symbol : "pin",
			        	 label :{
			        		 normal:{
			        			 show :false
			        		 }
			        	 },
			        	 data :[]
			         },
			         markLine: {
			        	 label :{
			        		 normal:{
			        			 show :false
			        		 }
			        	 },
			        	 data :[]
			         }
			    };
			    series.push(serie); 
	    	}
    	}
    	else{
    		for( var i = 0; i < indexCol.length ; i++){
	    		ldata.push(indexCol[i].text);
	    		var data = [];
	    		for(var j =0 ; j < cdata[indexCol[i].name].length ; j++){
	    			data.push({
	    				name : xAxis[j],
	    				value : cdata[indexCol[i].name][j]
	    			});
	    		}
	    		var serie = {
		    		type:'pie',
		            radius : '55%',
		            center: ['50%', '50%'],
		            data: data,
		            label: {
		            	normal :{
		                	show:false
		            	}
		            },
		            labelLine: {
		            	normal :{
		                	show:false
		            	}
		            }
	    		}
			    series.push(serie); 
	    	}
    	}
    	renderChart();
	}
	
	function renderChart(){
		chart = echarts.init(document.getElementById('chart'),'macarons');
		if("${chartType}" != "03"){
    		chart.setOption({
			    title : {
			    	text : $("#title").val(),
			    	x: 'center',
			        y: 0
			    },
			    tooltip : {
			        trigger: 'axis'
			    },
			    legend: {
			        data: ldata,
			        top: "30"
			    },
			    grid: {
			        left: '0',
			        top : '80',
			        right: '8%',
			        bottom: '10%',
			        width: 'auto',
			        containLabel: true
			    },
			    toolbox: {
			        show : true,
			        feature : {
			            mark : {show: true},
			            saveAsImage : {show: true}
			        }
			    },
			   dataZoom: [
	                       {
	                           type: 'slider',
	                           show: true,
	                           startValue:0,
	                           endValue: 4,
	                           showDetail: false,
	                           handleSize: 8
	                       },
	                       {
	                           type: 'inside',
	                           startValue:0,
	                           showDetail: false,
	                           endValue: 4
	                       },
	                       {
	                           type: 'slider',
	                           show: true,
	                           yAxisIndex: 0,
	                           filterMode: 'empty',
	                           width: 12,
	                           height: '70%',
	                           handleSize: 8,
	                           showDataShadow: false,
	                           left: '95%'
	                       }
	            ], 
			    calculable : true,
			    xAxis : [
			        {
			            type : 'category',
			            boundaryGap : boundaryGap,
			            data : xAxis,
			            nameGap: 20
			        }
			    ],
			    yAxis : [
			        {
			            type : 'value'
			        }
			    ],
			    color : color,
			    series : series
			});
    	}
    	else{
    		chart.setOption(option = {tooltip : {
	    	        trigger: 'item',
	    	        formatter: "{b} : {c} ({d}%)"
	    	    },
	    	    series : serie
    		});
    	}
	}
	function initLayout(){
		var centerWidth = $("#center").width();
		layout = $("#layout").ligerLayout({
			height : $("#center").height()-10,
			centerWidth : centerWidth - 270,
			rightWidth : 270,
			allowRightResize : false ,
			onEndResize : function(){
				renderChart();
			}
		});
		var rightToggleDoom = $(".l-layout-right").children(".l-layout-header").children(".l-layout-header-toggle");
		rightToggleDoom.unbind("click");
		rightToggleDoom.bind("click" , function(){
			layout.setRightCollapse(true);
			renderChart();
		});
		layout.rightCollapse.toggle.unbind("click");
		layout.rightCollapse.toggle.bind("click" , function(){
			layout.setRightCollapse(false);
			renderChart();
		});
	}
	function getColumnInfo(id){
		for(var i = 0 ;i < columns.length ; i++){
			if(columns[i].name == id){
				return {
					bussinessType : columns[i].businessType,
					name : columns[i].name
				};
			}
		}
		return null;
	}
	
	function initForm(){
		//渲染表单
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				display : '标题信息',
				name : 'title',
				newline : false,
				width : 170,
				type : 'text',
				options : {
					onChangeValue : function(val){
						renderChart();
					}
				}
			},{
				display : 'X轴信息',
				name : 'xinfo',
				width : 170,
				newline : true,
				type : 'text',
				options : {
					onChangeValue : function(val){
						renderXinfo(val);
						renderChart();
					}
				}
			},{
				display : '标志点',
				name : 'markpoint',
				width : 170,
				newline : true,
				type : "checkboxlist",
				options : {
					data : [{
						id: "maxp", 
						text: '最大值'
					},{
						id: "minp", 
						text: '最小值'
					}]
				}
			},{
				display : '标志线',
				name : 'markline',
				width : 170,
				newline : true,
				type : "checkboxlist",
				options : {
					data : [{
						id: "avgline", 
						text: '平均线'
					}]
				}
			}]
		});
		$("#title").val("图表");
		
		for(var j in columns){
			if(typeof columns[j] != "function"){
				var colInfo =columns[j];
				if(colInfo != null){
					if(colInfo.businessType == "DIM"){
						dexinfo += "#"+colInfo.name+"# ";
					}
				}
			}
		}
		dexinfo = dexinfo.substring(0,dexinfo.length-1);
		$("#xinfo").parent().parent().html('<div class="win" style="overflow: auto; height: 120px; border: 1px solid #D4D4D4"><div id="labelPad"></div></div>');
		$("input[type=checkbox]").live("click", function(){
			var check = $("input[type=checkbox][value="+this.value+"]")[0].checked;
			renderSeries(check,this.value);
			chart.setOption({
				 series : series
			});
		});
		
	
	}
	
	function initGrid() {
		var g = {
			width : "98%",
			height : "180",
			columns : [{
				display : "指标名称",
				name : "name",
				width : "35%",
				isSort : false
			}, {
				display : "显示颜色",
				name : "color",
				width : "55%",
				render : function(row) {
					return renderColor(row.__index, row.color);
				},
				isSort : false
			}],
			enabledEdit : true,
			clickToEdit : true,
			checkbox : false,
			usePager : false,
			isScroll : true,
			rownumbers : false,
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : true,
			dataAction : 'local',//从后台获取数据
			data: griddata,
			method : 'post'
		};
		//渲染GRID
		grid = $("#indexgrid").ligerGrid(g);
		$(".color").live("change", function(){
			color[$(this).attr("id")]= "#"+$(this).val();
			$("#center").focus();
			$("#center").trigger("click");
			renderChart();
		});
		
			
	}
	
	function renderColor(id, color){
		return '<input id="'+id+'" type="text" class="color" style="width:90%;background-color:#'+color+';" readonly="readonly" autocomplete="on" value="'+color+'">';
	}
	
	//添加拖拽控制
 	function setDragDrop(dom , receiveDom){
 		if(typeof dom == "undefined"
 			|| dom == null){
 			return ;
 		}
 		$(dom).ligerDrag({
 			proxy : function(target , g , e){
 				var proxyLabel = "${ctx}"+notAllowedIcon;
 				var targetTitle = $(dom).html();
 				
 				var proxyHtml = $("#drag-proxy-content").tpl({
 					proxyLabel : proxyLabel , 
 					targetTitle : targetTitle
 				});
                var div = $(proxyHtml);
                div.appendTo('#center');
                return div;
 			},
 			revert : false , 
 			receive : receiveDom , 
 			onStartDrag : function(current , e){
 				pid = labelPad.getLabel("text",$(dom).html())[0].id;
 			},
 			onDragEnter : function(receive , source , e){
 				var allowLabel = "${ctx}"+allowedIcon;
 				source.children(".dragimage_span").css("background" , "url('"+allowLabel+"')");
 			},
 			onDragLeave : function(receive , source , e){
 				var notAllowLabel = "${ctx}"+notAllowedIcon;
 				source.children(".dragimage_span").css("background" , "url('"+notAllowLabel+"')");
 			},
 			onDrop : function(obj , target , e){
 				$("#xinfo").val($("#xinfo").val()+"#"+pid+"#");
 				renderXinfo($("#xinfo").val());
				renderChart();
 			}
 		});
	}
	
 	function labelClick(e){
 		e.flag = !e.flag;
 		if(e.flag){
 			e.setCss({ "background-color" : "#DFDFDF", "color" :"#333"});
 		}
 		else{
 			e.setCss({ "border" : "1px dotted #D4D4D4", "color" :"#999"});
 		}
 		var pads = labelPad.getLabel("flag",true);
 		var text="";
 		for(var i in pads){
 			text+="#"+pads[i].id+"#"+" ";
 		}
 		if(text!=""){
 			text=text.substring(0,text.length-1)
 		}
 			
 		renderXinfo(text);
 		renderChart();
 	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="layout" style="width: 99.8%;">
			<div id="centerDiv" position="center">
				<div id="chart" class="chart" style="width: 100%; height: 100%;"></div>
			</div>
			<div id="rightDiv" position="right" title="图表配置信息">
				<form id="mainform"></form>
				<div id="indexgrid" name="levelInfoGrid" style="width: 100%;">
				</div>
			</div>
		</div>
	</div>
</body>