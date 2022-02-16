<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var chartId = '${chartId}';//图表ID
	var templateFreq = '${templateFreq}';//模板ID
	var showType = '${showType}';//图表显示类型
	var chartType = '${chartType}';//图表类型
	var orgNm = '${orgNm}';//显示机构名称
	var orgNo = '${orgNo}';//显示机构编号
	var chartCfg = JSON2.parse('${chartCfg}');//图表配置
	var chaFormula = '${chaFormula}';//图表公式
	var textCfg = '${textCfg}';//图表文本
	var teFormulaList = JSON2.parse('${formula}');//文本配置公式
	var formulaList = ["pointFa","cumulativeFa","averageFa","groupFa"];//公式类型
	var idxNmData = {configName : "指标名称" , configValue : "#idxNm#"};
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var field = [{
		name : 'chartId',
		type : 'hidden'
	}, {
    	display : '图表类型',
		name : 'chartType',
		type : "text",
		width : 170,
		options: {
			readonly: true
		},
		group : "机构信息配置",
		groupicon : groupicon
    }, {
    	display : '展示类型',
		name : 'showType',
		type : "select",
		newline : false,
		width : 170,
		options :{
			initValue : showType,
	        onSelected: function (id,value){
	        	initFaShow(id);
	        },
			data :[{text :'双轴柱线图' ,id : '01'},
			       {text :'饼图' ,id : '02'},
			       {text :'矩形树图' ,id : '05'}]
		}
    },{
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
	}, {
		display : "配置项",
		name : "textConfig",
		newline : true,
		type : "text",
		width : 300,
		group : "文字模板",
		groupicon : groupicon
	}, {
		display : "文本内容",
		name : "textCfg",
		newline : false,
		type : "textarea",
		width : 330
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
		initData();
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
	
	//改变选择的机构
	function changeOrgNm(){
		$("#mainform [name='orgNo']").val(orgNo);
		$.ligerui.get("orgNo").setText(orgNm);
	}
	
	//控制是否显示公式
	function initFaShow(id){
		if(id == "01"){
			$("#pointFa").parent().parent().parent().parent().show();
			$("#cumulativeFa").parent().parent().parent().parent().show();
			$("#averageFa").parent().parent().parent().parent().show();
			$("#groupFa").parent().parent().parent().parent().show();
			$("#leftY").parent().parent().parent().show();
			$("#rightY").parent().parent().parent().show();
			$("#smooth").parent().parent().parent().show();
			$("#markPoint").parent().parent().parent().show();
		}else{
			$("#pointFa").parent().parent().parent().parent().hide();
			$("#cumulativeFa").parent().parent().parent().parent().hide();
			$("#averageFa").parent().parent().parent().parent().hide();
			$("#groupFa").parent().parent().parent().parent().hide();
			$("#leftY").parent().parent().parent().hide();
			$("#rightY").parent().parent().parent().hide();
			$("#smooth").parent().parent().parent().hide();
			$("#markPoint").parent().parent().parent().hide();
		}
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
		$("#mainform [name='chartId']").val(chartId);
		$("#mainform [name='chartType']").val(chartType);	
		$("#mainform [name='textCfg']").val(textCfg);
		$("#mainform [name='chartCfg']").val('${chartCfg}');
		$.ligerui.get("pointFa").setValue(chaFormula);
		$.ligerui.get("cumulativeFa").setValue(chaFormula);
		$.ligerui.get("averageFa").setValue(chaFormula);
		$.ligerui.get("groupFa").setValue(chaFormula);
		$.ligerui.get("leftY").setValue(chartCfg.leftY ? chartCfg.leftY : "01");
		$.ligerui.get("rightY").setValue(chartCfg.rightY ? chartCfg.rightY  : "00");
		$.ligerui.get("smooth").setValue(chartCfg.smooth ? chartCfg.smooth : "01");
		$.ligerui.get("markPoint").setValue(chartCfg.markPoint ? chartCfg.markPoint : "00");
		$("#pointFa").parent().parent().parent().parent().hide();
		$("#cumulativeFa").parent().parent().parent().parent().hide();
		$("#averageFa").parent().parent().parent().parent().hide();
		$("#groupFa").parent().parent().parent().parent().hide();
		$("#textCfg").height(200).css("resize","none");
		$($(".l-group-hasicon")[1]).find("span").width(200);
		initFaShow(showType);
	}
	
	//校验勾选公式
	function formulaCheck(){
		var selectFa = "";
		var falength = 0;
		for(var i = 0; i < formulaList.length ; i++){
			selectFa = $.ligerui.get(formulaList[i]).getValue();
			if(selectFa){
				falength += selectFa.split(";").length;
			}
		}
		if(falength > 1){
			BIONE.tip("勾选公式过多，最多勾选1个！");
			return false;
		}else{
			return true;
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
	
	//构造grid数据
	function strGridData(teFormulaList){
		var gridData = [];//grid数据
		for(var i = 0; i< teFormulaList.length; i++){
			var data = {
					configName	: teFormulaList[i].formulaNm,
					configValue : "#" + teFormulaList[i].displayContent + "#",
					configNum  : "1",
					configType : "03"
			}
			gridData.push(data);
		}
		return gridData;
	}
	
	//初始化gride
	function initGrid() {
		var gridData = strGridData(teFormulaList);
		$("#textConfig").parent().parent().html("<div id = 'textGrid'></div>").css("margin-right","40px").next("li").hide();
		grid = $("#textGrid").ligerGrid({
			width : "99%",
			columns : [ {
				display : "配置项名称",
				name : "configName",
				width : "50%"
			},{
				display : "数量",
				name : "configNum",
				width : "20%",
				editor:{
					type :"digits"
				}
			},{
				display : "类型",
				name : "configType",
				width : "25%",
				editor : {
					type : 'select',
					data : [ {
						'id' : '01',
						'text' : '全显示'
					}, {
						'id' : '02',
						'text' : '显示占比'
					},{
						'id' : '03',
						'text' : '显示名称'
					}]
				},
				render : function(data, row, context, it) {
					if ("01" == context){
						return "全显示";
					} else if ("02" == context){
						return "显示占比";
					} else if ("03" == context){
						return "显示名称";
					} 
				}
			}],
			checkbox : false,//复选框
			usePager : false,//是否分页
			rownumbers : false,//是否显示行序号
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,//是否允许行拖拽
			allowHideColumn : false,//是否显示'切换列层'按钮
			enabledEdit : true,//是否允许编辑
			clickToEdit : true,//单元格编辑状态
			onDblClickRow : function(rowdata, rowid, rowobj){
				var textBefore = $("#textCfg").val();
				var val =rowdata.configValue;
				if(rowdata.configNum && rowdata.configType)
					val = val.replace("()","("+rowdata.configNum+","+rowdata.configType+")")
				textBefore += val;
				$("#textCfg").val(textBefore);
			},
			onBeforeEdit : function(rowdata){
				var rowNewdata = rowdata.record;
				if(rowNewdata.configValue== "#idxNm#" ){
					return false;
				}
			}
		});
		grid.addRow(idxNmData);
		grid.addRows(gridData);
		grid.setHeight(200);
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/cabin/analysis/config/saveOrgCfg"></form>
	</div>
</body>
</html>