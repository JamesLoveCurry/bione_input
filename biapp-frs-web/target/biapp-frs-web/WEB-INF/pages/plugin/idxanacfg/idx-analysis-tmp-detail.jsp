<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
    var templateId = '${templateId}';
    var templateNm = '${templateNm}';
    var	templateFreq = '${templateFreq}';
    var dataFormat = '${dataFormat}';
    var dataPrecision = '${dataPrecision}';
    var dataUnit = '${dataUnit}';
    var tmpFormula = '${tmpFormula}';//已勾选公式
    var formulaNum = JSON2.parse('${formulaNum}');//公式顺序
    var formulaList = ["pointFa","cumulativeFa","averageFa","groupFa"];
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var field = [{
		name : 'templateId',
		type : 'hidden'
	}, {
		display : "模板名称",
		name : "templateNm",
		newline : true,
		type : "text",
		width : 170,
		validate : {
			required : true,
			maxlength : 100
		},
		options: {
			readonly: true
		},
		group : "模板配置信息",
		groupicon : groupicon
	}, {
    	display : '模板频度',
		name : 'templateFreq',
		newline : false,
		type : "text",
		width : 170,
		validate : {
			required : true,
			maxlength : 100
		},
		options: {
			readonly: true
		}
    }, {
		display : '数据格式',
		name : 'dataFormat',
		newline : true,
		type : "select",
		width : 170,
		options :{
			data :[{text :'原格式' ,id : '00'},
			       {text :'金额' ,id : '01'},
			       {text :'百分比' ,id : '02'},
			       {text :'个数' ,id : '03'}],
	        onSelected: function (id,value){
	        	if(id)
	        		initdataun(id);
	        }
		}
	}, {
		display : '数据单位',
		name : 'dataUnit',
		newline : false,
		type : "select",
		width : 170,
		options :{
			initValue : dataUnit ? dataUnit : "01",
			data : null
		}
	}, {
    	display : '数据精度',
		name : 'dataPrecision',
		newline : false,
		type : 'spinner',
		width : 170,
		disabled : true,
		validate : {
			required : true, digits : true 
		},
		options : {
			type : 'int', valueFieldID : 'typelengthId',isNegative:false 
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
		group : "勾选规则",
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
	}];
	
	$(function() {
		initForm();
		initFormula();
		initData();
		initCheckNum();
	});
	
	//初始化勾选公式顺序函数
	function initCheckNum(){
		$("input[type=checkbox]").click(function(e){
			if($(this)[0].checked){
				$(this).next('label').after("<input id='"+$(this).attr("value")+"_txt' class='l-text txtNum' style='margin:0px 2px;width:15px;height:15px;' type='digits'></input>");
				$("#"+$(this).attr("value")+"_txt").ligerSpinner({ height: 22, type: 'int',isNegative:false });
				$("#"+$(this).attr("value")+"_txt").css("height","20");
				$("#"+$(this).attr("value")+"_txt").parent().css("height","20").css("width","33").css("float","right").css("margin","0px 2px");
			}
			else{
				$("#"+$(this).attr("value")+"_txt").parent().remove();
			}
		});
		$("input[type=checkbox]").each(function(i,n){
			if(this.checked){
				$(this).next('label').after("<input id='"+$(this).attr("value")+"_txt' class='l-text txtNum' style='margin:0px 2px;width:15px;height:15px;' type='digits'></input>");
				$("#"+$(this).attr("value")+"_txt").ligerSpinner({ height: 20, type: 'int',isNegative:false });
				$("#"+$(this).attr("value")+"_txt").css("height","20");
				$("#"+$(this).attr("value")+"_txt").parent().css("height","20").css("width","33").css("float","right");
				$("#"+$(this).attr("value")+"_txt").val(formulaNum[$(this).attr("value")]);
			}
		});
	}
	
	//创建表单结构 
	function initForm() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 950,
			labelWidth : 120,
			space : 40,
			fields : field
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}
	
	/*
	 * 初始化公式
	 */	
	function initFormula(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/cabin/analysis/config/getInitFormula",
			data :{
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
	
	//初始化数据
	function initData(){
		var tmpFreqText = "";
		if(templateFreq == "01"){
			tmpFreqText = "日";
		}else if(templateFreq == "02"){
			tmpFreqText = "月";
		}else if(templateFreq == "03"){
			tmpFreqText = "季";
		}else if(templateFreq == "04"){
			tmpFreqText = "年";
		}
		$("#mainform [name='templateId']").val(templateId);
		$("#mainform [name='templateNm']").val(templateNm);
		$("#mainform [name='templateFreq']").val(tmpFreqText);
		$("#mainform [name='dataPrecision']").val(dataPrecision ? dataPrecision : "0");
		$("#mainform [name='templateNm']").attr("disabled", "disabled");
		$("#mainform [name='templateFreq']").attr("disabled", "disabled");
		$.ligerui.get("pointFa").setValue(tmpFormula);
		$.ligerui.get("cumulativeFa").setValue(tmpFormula);
		$.ligerui.get("averageFa").setValue(tmpFormula);
		$.ligerui.get("groupFa").setValue(tmpFormula);
		$.ligerui.get("dataFormat").setValue(dataFormat ?dataFormat :"00");
		$("#dataUnit").parent().parent().parent().hide();
		if(dataFormat){
			initdataun(dataFormat);
		}
	}
	
	//保存函数
	function f_save() {
		if(!formulaCheck()){
			return false;
		}
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
	
	//获取数据单位
	function initdataun(id){
		if(id == "00"){
			$("#dataUnit").parent().parent().parent().hide();
			$("#dataPrecision").parent().parent().parent().hide();
		}else if(id == "01"){
			$("#dataUnit").parent().parent().parent().show();
			$("#dataPrecision").parent().parent().parent().show();
			$.ligerui.get("dataUnit").setData([{
				id : "01",
				text : "元"
			},{
				id : "02",
				text : "百元"
			},{
				id : "03",
				text : "千元"
			},{
				id : "04",
				text : "万元"
			},{
				id : "05",
				text : "亿元"
			}]);
		}else if(id == "02"){
			$("#dataUnit").parent().parent().parent().hide();
			$("#dataPrecision").parent().parent().parent().show();
			$.ligerui.get("dataUnit").setValue("00");
		}else if(id == "03"){
			$("#dataUnit").parent().parent().parent().show();
			$("#dataPrecision").parent().parent().parent().hide();
			$.ligerui.get("dataUnit").setData([{
				id : "01",
				text : "个"
			},{
				id : "02",
				text : "百"
			},{
				id : "03",
				text : "千"
			},{
				id : "04",
				text : "万"
			},{
				id : "05",
				text : "亿"
			}]);
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/cabin/analysis/config/saveTmpDetail"></form>
	</div>
</body>
</html>