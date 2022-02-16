<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<style>
.addGrid{
	float: left;
    margin-left: 4px;
    margin-top: 4px;
    cursor : pointer;
}
.deleteGrid{
	float: right;
    margin-left: 4px;
    margin-top: 4px;
    cursor : pointer;
}
</style>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/color/jscolor.js"></script>
<script type="text/javascript">
	var templateId = "${templateId}";
	var chartId = "${chartId}";
	var chartNm = "${chartNm}";
	var chartType = "${chartType}";
	var showType = "${showType}";
	var orderNum = "${orderNum}";
	var remark = "${remark}";
	var chartColor = "${chartColor}";
	var grid = null;
	var readonly = false;
	if(chartId){
		readonly = true;
	}
	var field = [{
		name : "templateId",
		type : "hidden"
	}, {
		name : "chartId",
		type : "hidden"
	}, {
		display : "图表名称",
		name : "chartNm",
		newline : true,
		type : "text",
		validate : {
			required : true
		}
	}, {
		display : "顺序",
		name : "orderNum",
		newline : false,
		type : 'spinner',
			validate : {
			required : true,
			digits : true ,
			maxlength : 32
		},
		options : {
			type : 'int', valueFieldID : 'typelengthId',isNegative:false 
		}
	}, {
		display : "图表类型",
		name : "chartType",
		newline : true,
		type : "select",
		options : {
			readonly : readonly,
			data : [ {
				text : '指标概要图',
				id : '01'
			}, {
				text : '机构信息图',
				id : '02'
			}, {
				text : '趋势分析图',
				id : '03'
			},{
				text : '结构解析图',
				id : '04'
			}/* ,{//配合报表平台指标库使用的功能，监管产品不适用
				text : '关系解析图',
				id : '05'
			} */],
	        onSelected: function (id,value){
	        	if(id)
	        		initshowType(id);
	        }
		}
	}, {
		display : "展示类型",
		name : "showType",
		newline : false,
		type : "select",
		options :{
			readonly : readonly,
			data : null
		}
	}, {
		display : "备注",
		name : "remark",
		newline : true,
		type : "textarea",
		attr : {
			style : "resize: none;"
		},
		validate : {
			maxlength : 500
		}
	}, {
		display : "颜色组",
		name : "colors",
		newline : false,
		type : "text",
		attr : {
			style : "resize: none;"
		},
		validate : {
			maxlength : 500
		}
	}, {
		name : 'chartColor',
		type : 'hidden'
	}]
	
	
	$(function() {
		initForm();
		initData();
		initbutten();
		initGrid();
		initEditTabBtn();
	});
	
	function initbutten(){
		//添加按钮
		var btns = [ {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("chartsConfig");
			}
		}, {
			text : "保存",
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
	};
	
	//创建表单结构 
	function initForm() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 160,
			labelWidth : 80,
			space : 30,
			fields : field
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}

	//初始化数据
	function initData() {
		$("#mainform [name='templateId']").val(templateId);
		$("#mainform [name='chartId']").val(chartId);
		$("#mainform [name='chartNm']").val(chartNm);
		$("#mainform [name='orderNum']").val(orderNum ? orderNum : "0");
		$("#mainform [name='remark']").val(remark);
		$("#mainform [name='chartColor']").val(chartColor);
		$("#remark").height(150).css("resize","none");
		$.ligerui.get("chartType").setValue(chartType ?chartType :"01");
		initshowType(chartType);
		$.ligerui.get("showType").setValue(showType ?showType :"01");
	}
	
	//保存
	function f_save() {
		var chartColor = "";
		var colors = grid.getData();
		for(var i in colors){
			chartColor += "#" + colors[i].colorName + ";";
		}
		$("#mainform [name='chartColor']").val(chartColor);
		BIONE.submitForm($("#mainform"), function(result) {
			var selectNode = window.parent.TreeObj.getSelectedNodes();
			if(selectNode.length > 0){
				window.parent.treeonClick(null,null,selectNode[0]);
			}
			window.parent.tabObj.selectTabItem(result.chartsInfo.chartId);
			BIONE.closeDialog("chartsConfig", "保存成功");
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
	//初始化gride
	function initGrid() {
		$("#colors").parent().parent().html("<div id = 'colorGrid'></div>").css("margin-right","40px").next("li").hide();
		grid = $("#colorGrid").ligerGrid({
			width : "99%",
			height: "150",
			columns : [ {
				display : "图表颜色",
				name : "colorName",
				width : "85%",
				render : function(row) {
					return initColor(row.colorName);
				},
				isSort : false
			}],
			checkbox : false,//复选框
			clickToEdit : true,
			isScroll : true,
			enabledEdit : true,//是否允许编辑
			usePager : false,//是否分页
			rownumbers : false,//是否显示行序号
			alternatingRow : false,//附加奇偶行效果行
			colDraggable : false,//是否允许行拖拽
			allowHideColumn : false,//是否显示'切换列层'按钮
		});
		var colorList = chartColor.split(";");
		for(var i in colorList){
			if(colorList[i]){
				grid.addRow({
					colorName : colorList[i].split("#").join("")
				});
			}
		}
	}
	
	//添加增加grid函数
	function initEditTabBtn(){
		$(".l-grid-hd-cell-inner").append("<div id = 'addGrid' class='l-icon l-icon-add addGrid'></div><div id = 'deleteGrid' class='l-icon l-icon-delete deleteGrid'></div>")
		$("#addGrid").click(function(){
			grid.addRow({
				colorName : "B6A2DC"
			});
			jscolor.bind();
			$(".color").live("change", function(){
				var selectedRow = grid.getSelectedRow();
				selectedRow.colorName = $(this).val();
			});
		});
		$("#deleteGrid").click(function(){
			var selectRows = grid.getSelectedRows();
			if(selectRows.length > 0){
				grid.deleteSelectedRow();
			}else{
				BIONE.tip("请先选择一行，再进行删除");
			}
		});
		$(".color").live("change", function(){
			var selectedRow = grid.getSelectedRow();
			selectedRow.colorName = $(this).val();
		});
	}
	
	//初始化颜色
	function initColor(color){
		return '<input type="text" class="color" style="width:90%;background-color:#'+color+';" autocomplete="on" value="'+color+'">';
	}
	
	//根据图表类型改变图形
	function initshowType(chartType){
		if(!chartType){
			chartType = "01";
		}
		if(chartType == "01"){
			$("#showType").parent().parent().parent().show();
			$.ligerui.get("showType").setData([{
				id : "04",
				text : "单轴柱线图"
			},{
				id : "02",
				text : "饼图"
			}]);
			$.ligerui.get("showType").setValue("01");
		}else if(chartType == "02"){
			$("#showType").parent().parent().parent().show();
			$.ligerui.get("showType").setData([{
				id : "01",
				text : "双轴柱线图"
			},{
				id : "02",
				text : "饼图"
			},{
				id : "05",
				text : "矩形树图"
			}]);
			$.ligerui.get("showType").setValue("01");
		}else if(chartType == "03"){
			$("#showType").parent().parent().parent().show();
			$.ligerui.get("showType").setData([{
				id : "01",
				text : "双轴柱线图"
			}]);
			$.ligerui.get("showType").setValue("01");
		}else if(chartType == "04"){
			$("#showType").parent().parent().parent().show();
			$.ligerui.get("showType").setData([{
				id : "03",
				text : "杜邦图"
			}]);
			$.ligerui.get("showType").setValue("03");
		}else if(chartType == "05"){
			$("#showType").parent().parent().parent().show();
			$.ligerui.get("showType").setData([{
				id : "03",
				text : "杜邦图"
			}]);
			$.ligerui.get("showType").setValue("03");
		}
	}
</script>
<title>Insert title here</title>
</head>
<body>
	<div id="template.center">
		<form id="mainform"
			action="${ctx}/cabin/analysis/config/saveChartInfo" method="POST"></form>
	</div>
	</div>
</body>
</html>