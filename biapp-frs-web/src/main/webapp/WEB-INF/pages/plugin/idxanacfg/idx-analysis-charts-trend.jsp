<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var chartId = '${chartId}';//图表ID
	var templateFreq = '${templateFreq}';//模板频度
	var showType = '${showType}';//显示类型
	var chartType = '${chartType}';//图表类型
	var dateFreq = '${dateFreq}';//日期频度
	var dataType = '${dataType}';//显示日期
	var startDate = '${startDate}';//开始时间
	var endDate = '${endDate}';//结束时间
	var chartCfg = JSON2.parse('${chartCfg}');//图表配置
	var chaFormula = '${chaFormula}';//图表公式
	var textCfg = '${textCfg}';//图表文本
    var formulaList = ["pointFa","cumulativeFa","averageFa","groupFa"];//公式类型
    var idxNmData = {configName : "指标名称" , configValue : "#idxNm#"};
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var formulaMap = {};//公式map
	var gridData = [];//grid数据
	jQuery.validator.addMethod("greaterThan", function(value, element, params) {
		if (value == '' || value == null) {
			// edit by caiqy,have no endtime , return
			return true;
		}
		var ele = $("[name=" + params + "]");
		return value > ele.val() ? true : false;
	}, "结束日期应当大于开始日期");
	
	var field = [{
		name : 'chartId',
		type : 'hidden'
	},{
    	display : '图表类型',
		name : 'chartType',
		type : "text",
		width : 170,
		options: {
			readonly: true
		},
		group : "趋势分析配置",
		groupicon : groupicon
    }, {
    	display : '展示类型',
		name : 'showType',
		type : "select",
		width : 170,
		newline : false,
		options :{
			initValue : showType,	
			data :[{text :'双轴柱线图' ,id : '01'}],
	        onSelected: function (id,value){
	        	initChartCfg(id);
	        }
		}
    }, {
		display : '左侧Y轴',
		name : 'leftY',
		type : "select",
		newline : false,
		width : 170,
		options : {
			data : [ {
				text : '柱图',
				id : '01'
			}, {
				text : '折线图',
				id : '02'
			} ]
		}
	}, {
		display : '右侧Y轴',
		name : 'rightY',
		type : "select",
		newline : true,
		width : 170,
		options : {
			data : [ {
				text : '无',
				id : '00'
			}, {
				text : '柱图',
				id : '01'
			}, {
				text : '折线图',
				id : '02'
			} ]
		}
	}, {
		display : '是否平滑处理',
		name : 'smooth',
		type : "select",
		newline : false,
		width : 170,
		options : {
			data : [ {
				text : '是',
				id : '01'
			}, {
				text : '否',
				id : '00'
			}]
		}
	}, {
		display : '是否显示极值',
		name : 'markPoint',
		type : "select",
		newline : false,
		width : 170,
		options : {
			data : [ {
				text : '都不显示',
				id : '00'
			}, {
				text : '都显示',
				id : '01'
			}, {
				text : '折线显示',
				id : '02'
			}, {
				text : '柱图显示',
				id : '03'
			} ]
		}
	}, {
    	display : '显示频度',
		name : 'dateFreq',
		newline : true,
		type : "select",
		width : 170,
		options :{
			data : null,
	        onSelected: function (id,value){
	        	if(id)
	        		initdisplayRange(id);
	        }
		}
    }, {
		display : "显示范围",
		name : "dataType",
		newline : false,
		width : 170,
		type : "select",
		options :{
			initValue : dataType ? dataType : "04",
			data : null,
	        onSelected: function (id,value){
	        	initdate(id);
	        }
		}
	}, {
		display : '开始时间',
		name : "startDate",
		newline : true,
		width : 170,
		type : "date",
		options : {
			format : "yyyyMMdd"
		},
		cssClass : "field",
		attr : {
			op : ">=",
			field : 'login_date'
		},
		validate : {
			required : true,
			messages : {
				required : "开始日期不能为空。"
			}
		}
		
	}, {
		display : '结束时间',
		name : "endDate",
		newline : false,
		width : 170,
		type : "date",
		options : {
			format : "yyyyMMdd"
		},
		cssClass : "field",
		attr : {
			op : "<=",
			field : 'login_date'
		},
		validate : {
			greaterThan : "startDate",
			required : true,
			messages : {
				required : "结束日期不能为空。"
			}
		}
	}, {
		display : "时点公式",
		name : "pointFa",
		newline : true,
		type : "checkboxlist",
		options : {
			rowSize :10 ,
			data : null
		},
		group : "勾选公式",
		groupicon : groupicon
	}, {
		display : "累积公式",
		name : "cumulativeFa",
		newline : true,
		type : "checkboxlist",
		options : {
			rowSize :10 ,
			data : null
		}
	}, {
		display : "日均公式",
		name : "averageFa",
		newline : true,
		type : "checkboxlist",
		options : {
			rowSize :10 ,
			data : null
		}
	}, {
		display : "组合公式",
		name : "groupFa",
		newline : true,
		type : "checkboxlist",
		options : {
			rowSize :0 ,
			data : null
		}
	},{
		display : "配置项",
		name : "textConfig",
		newline : true,
		type : "text",
		width : 170,
		group : "文字模板",
		groupicon : groupicon
	}, {
		display : "文本内容",
		name : "textCfg",
		newline : false,
		type : "textarea",
		width : 470
	}, {
		name : 'selectFa',
		type : 'hidden'
	}, {
		name : 'chartCfg',
		type : 'hidden'
	}]

	$(function() {
		$("#center").css("overflow-y","auto");
		initFrom();
		initFormula();
		newdate(startDate,endDate);
		initData();
		initCheckNum();
		initGrid();
	});
	
	//创建表单结构 
	function initFrom() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 750,
			labelWidth : 90,
			space : 40,
			fields : field
		});
	}
	
	/*
	 * 初始化公式
	 */
	function initFormula() {
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/cabin/analysis/config/getInitFormula",
			data : {
				templateFreq : templateFreq
			},
			dataType : 'json',
			type : "post",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(resultMap) {
				if(resultMap){
					initFaData(resultMap);
					formulaMap = resultMap.formulaMap;
				}
			},
			error : function(resultMap, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + resultMap.status);
			}
		});
	}

	//初始化公式数据
	function initFaData(formulaMap){
		if (formulaMap.pointFa && formulaMap.pointFa.length > 0) {
			$.ligerui.get("pointFa").setData(formulaMap.pointFa);
		}
		if (formulaMap.cumulativeFa && formulaMap.cumulativeFa.length > 0) {
			$.ligerui.get("cumulativeFa").setData(formulaMap.cumulativeFa);
		}
		if (formulaMap.averageFa && formulaMap.averageFa.length > 0) {
			$.ligerui.get("averageFa").setData(formulaMap.averageFa);				
		}
		if (formulaMap.groupFa && formulaMap.groupFa.length > 0) {
			$.ligerui.get("groupFa").setData(formulaMap.groupFa);						
		}
	}
	
	//初始化from数据
	function initData(){
		$("#startDate").parent().parent().parent().parent().hide();
		$("#endDate").parent().parent().parent().parent().hide();
		$("#mainform [name='chartId']").val(chartId);
		$("#mainform [name='chartType']").val(chartType);
		$("#mainform [name='startDate']").val(startDate);
		$("#mainform [name='endDate']").val(endDate);
		$("#mainform [name='textCfg']").val(textCfg);
		$("#mainform [name='chartCfg']").val('${chartCfg}');
		$.ligerui.get("pointFa").setValue(chaFormula);
		$.ligerui.get("cumulativeFa").setValue(chaFormula);
		$.ligerui.get("groupFa").setValue(chaFormula);
		$.ligerui.get("averageFa").setValue(chaFormula);
		$.ligerui.get("dateFreq").setValue(dateFreq);
		$.ligerui.get("leftY").setValue(chartCfg.leftY ? chartCfg.leftY : "01");
		$.ligerui.get("rightY").setValue(chartCfg.rightY ? chartCfg.rightY  : "00");
		$.ligerui.get("smooth").setValue(chartCfg.smooth ? chartCfg.smooth : "01");
		$.ligerui.get("markPoint").setValue(chartCfg.markPoint ? chartCfg.markPoint : "00");
		$.ligerui.get("dateFreq").setValue(dateFreq ? dateFreq : "04");
		$("#textCfg").height(200).css("resize","none");
		$($(".l-group-hasicon")[1]).find("span").width(300);
		initChartCfg(showType);
		initdisplayFreq();
	};
	
	//初始化日期频度
	function initdisplayFreq(){
		if(templateFreq == "01"){
			$.ligerui.get("dateFreq").setData([{text :'日' ,id : '01'},{text :'月' ,id : '02'},{text :'季' ,id : '03'},{text :'年' ,id : '04'}]);
		}else if (templateFreq == "02"){
			$.ligerui.get("dateFreq").setData([{text :'月' ,id : '02'},{text :'季' ,id : '03'},{text :'年' ,id : '04'}]);
		}else if (templateFreq == "03"){
			$.ligerui.get("dateFreq").setData([{text :'季' ,id : '03'},{text :'年' ,id : '04'}]);
		}else if (templateFreq == "04"){
			$.ligerui.get("dateFreq").setData([{text :'年' ,id : '04'}]);
		}
	}
	
	//初始化日期范围
	function initdisplayRange(id){
		if(id == "01"){
			$.ligerui.get("dataType").setData([{text :'本月' ,id : '01'},{text :'本季' ,id : '02'},{text :'本年' ,id : '03'},{text :'自定义' ,id : '04'},{text :'最近7天' ,id : '06'},{text :'最近30天' ,id : '07'}]);
		}else if( id == "02"){
			$.ligerui.get("dataType").setData([{text :'本季' ,id : '02'},{text :'本年' ,id : '03'},{text :'自定义' ,id : '04'},{text :'最近12月' ,id : '05'}]);
		}else if( id == "03"){
			$.ligerui.get("dataType").setData([{text :'本年' ,id : '03'},{text :'自定义' ,id : '04'}]);
		}else if( id == "04"){
			$.ligerui.get("dataType").setData([{text :'自定义' ,id : '04'}]);
		}
	}
	
	//日期类型改变事件
	function initdate(id){
		if(id == "04"){
			$("#startDate").parent().parent().parent().parent().show();
			$("#endDate").parent().parent().parent().parent().show();
		}else {
			$("#startDate").parent().parent().parent().parent().hide();
			$("#endDate").parent().parent().parent().parent().hide();
		}
	}
	
	//图表配置函数
	function initChartCfg(chartShowType){
		if(chartShowType == "01"){
			$("#leftY").parent().parent().parent().show();
			$("#rightY").parent().parent().parent().show();
		}else{
			$("#leftY").parent().parent().parent().show();
			$("#rightY").parent().parent().parent().hide();
		}
	}
	
	//获取当前日期
	function newdate(start,end){
		var dd = new Date();
		var y = dd.getFullYear();
		var m = dd.getMonth() + 1; //获取当前月份的日期
		if (m < 10) {
			m = "0" + m;
		}
		var d1 = dd.getDate() - 1;
		var d2 = dd.getDate();
		if (d1 < 10) {
			d1 = "0" + d1;
		}
		dd =  y  + m  + d1;
		if(!start){
			startDate = dd;
		}
		if (d2 < 10) {
			d2 = "0" + d2;
		}
		dd =  y  + m  + d2;
		if(!end){
			endDate = dd;
		}
	}
	
	//复选框勾选事件
	function initCheckNum(){
		$("input[type=checkbox]").click(function(e){
			var newData = {};
			var sign = "delete";
			var formulaKey = $(this).next('label').html();
			var formulaId = this.value;
			if(this.checked){
				sign = "add";
			}
			newData = strGridData(formulaKey,formulaId);
			reRenderGrid(sign,newData);
		});
		$.each($("input[type=checkbox]"),function(i,n){
			if(this.checked){
				var formulaKey = $(this).next('label').html();
				var formulaId = this.value;
				gridData.push(strGridData(formulaKey,formulaId));
			}
		});
	}
	
	//初始化gride
	function initGrid() {
		$("#textConfig").parent().parent().html("<div id = 'textGrid'></div>").css("margin-right","40px").next("li").hide();
		grid = $("#textGrid").ligerGrid({
			width : "99%",
			columns : [ {
				display : "配置项名称",
				name : "configName",
				width : "100%"
			}],
			checkbox : false,//复选框
			enabledEdit : false,//是否允许编辑
			usePager : false,//是否分页
			rownumbers : false,//是否显示行序号
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,//是否允许行拖拽
			allowHideColumn : false,//是否显示'切换列层'按钮
			onSelectRow : function(rowdata, rowid, rowobj){
				var textBefore = $("#textCfg").val();
				textBefore += rowdata.configValue;
				$("#textCfg").val(textBefore);
			}
		});
		grid.addRow(idxNmData);
		grid.addRows(gridData);
		grid.setHeight(200);
	}
	
	//构造grid数据
	function strGridData(fromulaKey,formulaId){
		var data = {
				configName	: fromulaKey,
				configValue  : formulaMap[formulaId]
		}
		return data;
	}
	
	//重新渲染grid
	function reRenderGrid(sign,newData){
		if(sign == "add"){
			grid.addRow(newData);
		}else{
			var gridRow = grid.rows;
			for(var i = 0 ; i < gridRow.length ; i++){
				if(gridRow[i].configName == newData.configName){
					grid.deleteRow(gridRow[i]);
					break;
				}
			}
		}
	}
	
	//保存from表单
	function f_save() {
		if(!formulaCheck()){
			return false;
		}
		save_selectFa();
		save_chartCfg();
		BIONE.submitForm($("#mainform"), function() {
			BIONE.tip("保存成功");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	//将已勾选的公式保存到表单中
	function save_selectFa(){
		var selectFa = "";
		$("input[type=checkbox]").each(function(){
			if(this.checked){
				var faId = this.value;
				if(faId){
					selectFa += faId + ";";
				}
			}
		});
		$("#mainform [name='selectFa']").val(selectFa);
	}
	
	//将图表配置保存到表单中
	function save_chartCfg(){
		chartCfg.leftY = $.ligerui.get("leftY").getValue();
		chartCfg.rightY = $.ligerui.get("rightY").getValue();
		chartCfg.smooth = $.ligerui.get("smooth").getValue();
		chartCfg.markPoint = $.ligerui.get("markPoint").getValue();
		$("#mainform [name='chartCfg']").val(JSON2.stringify(chartCfg));
	}
	
	//校验勾选公式
	function formulaCheck(){
		var selectFa = "";
		var falength = 0;
		var chartShowType = $.ligerui.get("showType").getValue();
		for(var i = 0; i < formulaList.length ; i++){
			selectFa = $.ligerui.get(formulaList[i]).getValue();
			if(selectFa){
				falength += selectFa.split(";").length;
			}
		}
		if(falength > 1 && chartShowType == "01"){
			BIONE.tip("双轴柱线图，最多勾选1个！");
			return false;
		}else if(falength > 5){
			BIONE.tip("单轴柱线图，最多勾选5个！");
			return false;
		}else{
			return true;
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/cabin/analysis/config/saveTrend"></form>
	</div>
</body>
</html>