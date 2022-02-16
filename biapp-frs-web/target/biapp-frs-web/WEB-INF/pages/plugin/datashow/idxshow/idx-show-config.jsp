<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>

<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var id = "${id}";
	var indexNo = "${indexNo}";
	var indexVerId = "${indexVerId}";
	//创建表单结构 
	var mainform = null;
	var colInfo = window.parent.getIdx(id) || {};
	
	$(function() {
		initForm();
		initButtons();
		initData();
	});
	
	function initForm(){
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ {
				name : "detailId",
				type : "hidden"
			},{
				display : "显示名称",
				name : "indexAlias",
				newline : true,
				type : "text",
				group : "列属性",
				groupicon : groupicon
			}, {
				display : "显示格式",
				name : "dataType",
				newline : false,
				type : "select",
				options : {
					data : [{
						id: "01",
						text : "金额"
					},{
						id: "02",
						text : "比例"
					},{
						id: "03",
						text : "数值"
					}],
					onSelected: function(id,value){
						setDataInfo(id);
					}
				}
			}, {
				display : "数据单位",
				name : "dataUnit",
				newline : true,
				type : "select",
				options : {
					data : [{
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
					}]
				}
			},{
				display : "数据精度",
				name : "dataPrecision",
				newline : false,
				type : "digits"
			},{
				display : "指标版本",
				name : "indexVerId",
				newline : false,
				type : "select",
				options :{
					initValue:indexVerId,
					url : "${ctx}/report/frame/idx/getAllVer?indexNo=" + indexNo
				}
			},{
				display : "计算规则",
				name : "ruleId",
				newline : true,
				type : "select",
				group : "计算方式",
				groupicon : groupicon,
				options : {
					url : "${ctx}/report/frame/datashow/idx/getIdxRule?ruleType="+colInfo.data.statType
				}
			},{
				display : "时间度量",
				name : "timeMeasureId",
				newline : false,
				type : "select",
				options : {
					url : "${ctx}/report/frame/datashow/idx/getIdxTimeMeasure",
					onSelected : function(id,text){
						if(id != "1"&& id != "5"&& id != "6"&& id != "7"){
							$("#isPassyear").parent().parent().parent().show();
						}
						else{
							$("#isPassyear").parent().parent().parent().hide();
						}
					}
				}
			}, {
				display : "取值方式",
				name : "modeId",
				newline : true,
				type : "select",
				options : {
					url : "${ctx}/report/frame/datashow/idx/getIdxMode"
				}
			}, {
				display : "是否跨年",
				name : "isPassyear",
				newline : false,
				type : "select",
				options : {
					data :[{
						id : "",
						text : "默认"
					},{
						id : "Y",
						text : "是"
					},{
						id : "N",
						text : "否"
					}]
				}
			}]
		});
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	}
	
	function initData(){
		$("#detailId").val(colInfo.detailId);
		$("#indexAlias").val(colInfo.indexAlias);
		$.ligerui.get("dataType").selectValue(colInfo.dataType);
		setDataInfo(colInfo.dataType);
		$.ligerui.get("dataUnit").selectValue(colInfo.dataUnit);
		$("#dataPrecision").val(colInfo.dataPrecision);
		$.ligerui.get("ruleId").selectValue(colInfo.ruleId);
		$.ligerui.get("timeMeasureId").selectValue(colInfo.timeMeasureId);
		$.ligerui.get("modeId").selectValue(colInfo.modeId);
		$.ligerui.get("isPassyear").selectValue(colInfo.isPassyear);
	}
	
	function setDataInfo(id){
		if(id == "01"){
			$("#dataUnit").parent().parent().parent().parent().parent().show();
			$("#dataUnit").parent().parent().parent().parent().show();
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
		}
		else if(id == "02"){
			$("#dataUnit").parent().parent().parent().parent().parent().show();
			$("#dataUnit").parent().parent().parent().parent().hide();
			$("#dataPrecision").parent().parent().parent().show();
		}
		else if(id == "03"){
			$("#dataUnit").parent().parent().parent().parent().parent().show();
			$("#dataUnit").parent().parent().parent().parent().show();
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
	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancleHandler
		});
		buttons.push({
			text : '确认',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
	
	function f_save() {
		colInfo.indexAlias = $("#indexAlias").val();
		colInfo.dataType = $("#dataType").val();
		colInfo.dataUnit = $("#dataUnit").val();
		if(colInfo.dataType == "03")
			colInfo.dataPrecision ="0";
		else
			colInfo.dataPrecision = $("#dataPrecision").val();
		colInfo.ruleId = $("#ruleId").val();
		colInfo.timeMeasureId = $("#timeMeasureId").val();
		colInfo.modeId = $("#modeId").val();
		colInfo.isPassyear  = $("#isPassyear").val();
		for(var i in window.parent.columns){
			if(window.parent.columns[i].name == colInfo.detailId){
				window.parent.columns[i].display = colInfo.indexAlias;
				break;
			}
		}
		window.parent.grid.changeHeaderText(colInfo.detailId, colInfo.indexAlias);
		window.parent.setIdx(colInfo.detailId,colInfo);
		window.parent.addGridData(window.parent.queryResult);
		if(indexVerId != $("#indexVerId").val()){
			indexVerId = $("#indexVerId").val();
			changeIdxVer();//如果选择历史版本重置一些数据
		}
		BIONE.closeDialog("attrEdit");
	}
	
	function cancleHandler() {
		BIONE.closeDialog("attrEdit");
	}
	
	function changeIdxVer() {
		$.ajax({
			cache: false,
			async: false,
			url: '${ctx}/report/frame/datashow/idx/changeIdxVer',
			type: 'post',
			data: {
				indexNo: indexNo,
				indexVerId: indexVerId 
			},
			dataType: 'json',
			success: function(result) {
				if(result.idxInfo){
					window.parent.deleteColByCid(id);
					window.parent.addIdx(result.idxInfo, true);
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/report/frame/design/cfg/custom/uptRpt"  method="post"></form>
	</div>
</body>
</html>