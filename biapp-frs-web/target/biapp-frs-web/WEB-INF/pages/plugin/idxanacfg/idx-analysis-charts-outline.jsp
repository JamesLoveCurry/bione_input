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
	var chartCfg = JSON2.parse('${chartCfg}');//图表配置
	var chaFormula = '${chaFormula}';//图表公式
	var textCfg = "${textCfg}";//图表文本
    var formulaNum = JSON2.parse('${formulaNum}');//公式顺序
    var formulaList = ["pointFa","cumulativeFa","averageFa","groupFa"];//公式类型
    var idxNmData = {configName : "指标名称" , configValue : "#idxNm#"};
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var formulaMap = {};//公式map
	var gridData = [];//grid数据
	var field = [ {
		name : 'chartId',
		type : 'hidden'
	}, {
		display : '图表类型',
		name : 'chartType',
		type : "text",
		width : 170,
		options : {
			readonly : true
		},
		group : "指标概要配置",
		groupicon : groupicon
	}, {
		display : '展示类型',
		name : 'showType',
		type : "select",
		newline : false,
		width : 170,
		options : {
			initValue : showType,
			data : [ {
				text : '单轴柱线图',
				id : '04'
			}, {
				text : '饼图',
				id : '02'
			} ],
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
		display : '是否平滑处理',
		name : 'smooth',
		type : "select",
		newline : true,
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
		display : "文本配置项",
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
		initFrom()
		initFormula();
		initData();
		initCheckNum();
		initGrid();
	});

	//复选框勾选事件
	function initCheckNum(){
		$("input[type=checkbox]").click(function(e){
			var newData = {};
			var sign = "";
			var formulaKey = $(this).next('label').html();
			var formulaId = this.value;
			if(this.checked){
				sign = "add";
				$(this).next('label').after("<input id='"+$(this).attr("value")+"_txt' class='l-text txtNum' style='margin:0px 2px;width:15px;height:15px;' type='digits'></input>");
				$("#"+$(this).attr("value")+"_txt").ligerSpinner({ height: 22, type: 'int',isNegative:false });
				$("#"+$(this).attr("value")+"_txt").css("height","20");
				$("#"+$(this).attr("value")+"_txt").parent().css("height","20").css("width","33").css("float","right").css("margin","0px 2px");
			}
			else{
				sign = "delete";
				$("#"+$(this).attr("value")+"_txt").parent().remove();
			}
			newData = strGridData(formulaKey,formulaId);
			reRenderGrid(sign,newData);
		});
		$.each($("input[type=checkbox]"),function(i,n){
			if(this.checked){
				var formulaKey = $(this).next('label').html();
				var formulaId = this.value;
				gridData.push(strGridData(formulaKey,formulaId));
				$(this).next('label').after("<input id='"+$(this).attr("value")+"_txt' class='l-text txtNum' style='margin:0px 2px;width:15px;height:15px;' type='digits'></input>");
				$("#"+$(this).attr("value")+"_txt").ligerSpinner({ height: 20, type: 'int',isNegative:false });
				$("#"+$(this).attr("value")+"_txt").css("height","20");
				$("#"+$(this).attr("value")+"_txt").parent().css("height","20").css("width","33").css("float","right");
				$("#"+$(this).attr("value")+"_txt").val(formulaNum[$(this).attr("value")]);
			}
		});
	}
	
	//创建表单结构 
	function initFrom() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 950,
			labelWidth : 90,
			space : 40,
			fields : field
		});
	}

	//图表配置函数
	function initChartCfg(chartShowType){
		if(chartShowType == "04"){
			$("#leftY").parent().parent().parent().show();
			$("#smooth").parent().parent().parent().show();
			$("#markPoint").parent().parent().parent().show();
		}else{
			$("#leftY").parent().parent().parent().hide();
			$("#smooth").parent().parent().parent().hide();
			$("#markPoint").parent().parent().parent().hide();
		}
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
		$("#mainform [name='chartId']").val(chartId);
		$("#mainform [name='chartType']").val(chartType);
		$("#mainform [name='textCfg']").val(textCfg);
		$("#mainform [name='chartCfg']").val('${chartCfg}');
		$.ligerui.get("pointFa").setValue(chaFormula);
		$.ligerui.get("cumulativeFa").setValue(chaFormula);
		$.ligerui.get("averageFa").setValue(chaFormula);
		$.ligerui.get("groupFa").setValue(chaFormula);
		$.ligerui.get("leftY").setValue(chartCfg.leftY ? chartCfg.leftY : "01");
		$.ligerui.get("smooth").setValue(chartCfg.smooth ? chartCfg.smooth : "01");
		$.ligerui.get("markPoint").setValue(chartCfg.markPoint ? chartCfg.markPoint : "00");
		$("#textCfg").height(200).css("resize","none");
		$("#leftY").parent().parent().parent().hide();
		$($(".l-group-hasicon")[1]).find("span").width(200);
		initChartCfg(showType);
	};
	
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
		if(falength > 5){
			BIONE.tip("勾选公式过多，最多勾选5个！");
			return false;
		}else{
			return true;
		}
	}
	
	//将已勾选的公式保存到表单中
	function save_selectFa(){
		var selectFa = "";
		$("input[type=checkbox]").each(function(){
			if(this.checked){
				var faId = this.value;
				var faNum = $("#"+this.value+"_txt").val();
				if(faId){
					if(faNum){
						selectFa += faId + ":" + faNum + ";";
					}
				}
			}
		});
		$("#mainform [name='selectFa']").val(selectFa);
	}
	
	//将图表配置保存到表单中
	function save_chartCfg(){
		chartCfg.leftY = $.ligerui.get("leftY").getValue();
		chartCfg.smooth = $.ligerui.get("smooth").getValue();
		chartCfg.markPoint = $.ligerui.get("markPoint").getValue();
		$("#mainform [name='chartCfg']").val(JSON2.stringify(chartCfg));
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
</script>
</head>
<body>
	<div id="template.center" >
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/cabin/analysis/config/saveOutline"></form>
	</div>
</body>
</html>