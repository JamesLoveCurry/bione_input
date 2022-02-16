<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var templateFreq = '${templateFreq}';//模板频度
	var chartId = '${chartId}';
	var showType = '${showType}';
	var chartType = '${chartType}';
	var chaFormula = '${chaFormula}';//图表公式
	var formulaList = ["pointFa","cumulativeFa","averageFa","groupFa"];//公式类型
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var field = [{
		name : 'chartId',
		type : 'hidden'
	}, {
    	display : '图表类型',
		name : 'chartType',
		type : "text",
		width : 170,
		validate : {
			required : true,
			maxlength : 100
		},
		options: {
			readonly: true
		},
		group : "结构解析配置",
		groupicon : groupicon
    }, {
    	display : '展示类型',
		name : 'showType',
		type : "select",
		width : 170,
		newline : false,
		validate : {
			required : true,
			maxlength : 100
		},
		options: {
			readonly: true,
			data :[{text :'杜邦图' ,id : '03'}]
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
		name : 'selectFa',
		type : 'hidden'
	}]

	$(function() {
		initFrom();
		initFormula();
		initData();
	});
	
	//创建表单结构 
	function initFrom() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 950,
			labelWidth : 120,
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
		$.ligerui.get("showType").setValue(showType);
		$.ligerui.get("pointFa").setValue(chaFormula);
		$.ligerui.get("cumulativeFa").setValue(chaFormula);
		$.ligerui.get("averageFa").setValue(chaFormula);
		$.ligerui.get("groupFa").setValue(chaFormula);
		$($(".l-group-hasicon")[1]).find("span").width(200);
	};
	
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
	
	function f_save() {
		if(!formulaCheck()){
			return false;
		}
		save_selectFa();
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
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/cabin/analysis/config/saveStructure"></form>
	</div>
</body>
</html>