<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    jQuery.extend(jQuery.validator.messages, {
		greaterThan : "[结束日期]应当大于[开始日期]"
	});
    //报送周期 报表编号 校验
    jQuery.validator.addMethod("requiredCheck", function(value, element) {
  	  	var elementSrcVal = $("#elementSrc").val();
  		var defaultValueVal = $("#defaultValue").val();
  	  	if(elementSrcVal != "" || defaultValueVal != "无" ){
  		  if(value == ""){
  			return false;
  		  }
  	  	}
  	  	return true;
  	 }, "已配置[数据来源]或[常量值]，此项必须配置!");
    //数据来源 或 常量值 校验
    jQuery.validator.addMethod("isGroupCheck", function(value, element) {
    	  var isGroup = $("#isGroup").val();
    	  if(isGroup != "Y"){
    		  if(value.indexOf(";")>0 || value.indexOf("[")>0
    				  || value.indexOf("-")>0|| value.indexOf("]")>0){
    			  return false;
    		  }
    	  }
    	  return true;
    	 }, "[是否分组]配置为是，才可以使用;配置多个值或使用公式!");
    
    
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    var id = "${id}";
    $(function(){
    	initForm();
    	initFormData();
    });
    
    function initForm() {
    	$("#mainform").ligerForm({
    		inputWidth: 280,
    	    fields : [{
	    		group : "配置信息",
	    		groupicon : groupicon,
	    		display : '元素名称',
	    		name : 'elementNm',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
	    		display : '上级元素',
	    		name : 'upElementId',
	    		newline : true,
	    		type : "select",
	    		comboboxName : "upElementCombBox",
	    		options : {
					onBeforeOpen : selectElementDialog,
					selectBoxHeight : '150'
				},
	    		validate : {
	    		    required : true
	    		}
    	    },{
	    		display : '是否分组',
	    		name : 'isGroup',
	    		newline : true,
	    		type : "select",
	    		comboboxName : "isGroupBox",
	    		options:{
	    			initValue:'N',
					data:[{
						text:"是",
						id : "Y"
					},{
						text:"否",
						id : "N"
					}]
				},
				validate : {
					required : true
				}
    	    },{
	    		display : '元素顺序',
	    		name : 'orderId',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    number : "请输入合法的数字"
	    		}
    	    },{
	    		display : '元素属性',
	    		name : 'elementAttr',
	    		newline : true,
	    		type:'text',
	    		validate : {
	    		    maxlength : 500
	    		}
    	    },{
				display : '开始日期',
				name : 'startDate',
				newline : true,
				id : 'startDate',
				type : 'date',
				options : {
					format : "yyyy-MM-dd"
				},
				validate : {
					required : true
				}
			}, {
				display : '结束日期',
				name : 'endDate',
				id : 'endDate',
				newline : true,
				type : 'date',
				options : {
					format : "yyyy-MM-dd"
				},
				validate : {
					required : true,
					greaterThan : "startDate"
				}
			},{
				group : "业务信息",
	    		groupicon : groupicon,
	    		display : '报送周期',
	    		name : 'submitCycle',
	    		newline : true,
	    		type : "select",
	    		comboboxName : "submitCycleBox",
	    		options:{
					data:[{
						text:"月报",
						id : "M"
					},{
						text:"季报",
						id : "Q"
					},{
						text:"半年报",
						id : "H"
					},{
						text:"年报",
						id : "A"
					},{
						text:"无",
						id : ""
					}]
				},
	    		validate : {
	    		    maxlength : 10,
	    		    requiredCheck : true
	    		}
    	    },{
	    		display : '所属报表',
	    		name : 'belongRptNum',
	    		newline : true,
	    		type : 'text',
	    		comboboxName : "belongRptNumBox",
	    		validate : {
	    		    maxlength : 100,
	    		    requiredCheck : true
	    		},options : {
					onBeforeOpen : selectRptDialog
				}
    	    },{
	    		display : '数据来源',
	    		name : 'elementSrc',
	    		newline : true,
	    		type:'textarea',
	    		validate : {
	    		    maxlength : 2000,
	    		    isGroupCheck : true
	    		}
    	    },{
	    		display : '常量值',
	    		name : 'defaultValue',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    maxlength : 200,
	    		    isGroupCheck : true
	    		}
    	    },{
	    		display : '备注',
	    		name : 'remark',
	    		newline : true,
	    		type:'textarea',
	    		validate : {
	    		    maxlength : 500
	    		}
    	    },{
	    		name : 'elementId',
				type : "hidden"
    	    }]
    	});

    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));

    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("xmlMessageEidtWin", null);
    	    }
    	});
    	buttons.push({
    	    text : '保存',
    	    onclick : f_save
    	});
    	BIONE.addFormButtons(buttons);
    }
    
    function initFormData(){
    	if(id){
    		$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/xmlMessage/getXmlElementById",
				dataType : 'json',
				type : "GET",
				beforeSend: function(){
					BIONE.showLoading('加载数据中...');
				},
				data : {
					id: id
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success : function(xmlCfg) {
					$("#elementId").val(xmlCfg.elementId);
	    			$("#elementNm").val(xmlCfg.elementNm);
	    			$("#upElementId").val(xmlCfg.upElementId);
	    			$("#upElementCombBox").val(xmlCfg.upElementNm);
	    			$("#elementAttr").val(xmlCfg.elementAttr);
	    			$("#isGroup").val(xmlCfg.isGroup);
	    			$.ligerui.get("isGroupBox").selectValue(xmlCfg.isGroup);
	    			$("#belongRptNum").val(xmlCfg.belongRptNum);
	    			$("#belongRptNumBox").val(xmlCfg.belongRptNum);
	    			$("#elementSrc").val(xmlCfg.elementSrc);
	    			$("#defaultValue").val(xmlCfg.defaultValue);
	    			$("#submitCycle").val(xmlCfg.submitCycle);
	    			$.ligerui.get("submitCycleBox").selectValue(xmlCfg.submitCycle);
	    			$("#orderId").val(xmlCfg.orderId);
	    			$("#startDate").val(xmlCfg.startDate);
	    			$("#endDate").val(xmlCfg.endDate);
	    			$("#remark").val(xmlCfg.remark);
				},
				error : function(result, b) {
					BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
    		//BIONE.loadForm(mainform, {
    		//    url : "${ctx}/frs/xmlMessage/getXmlElementById?id=${id}"
    		//});
    	}
    }
    
  	//保存
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("xmlMessageEidtWin", "maingrid", "保存成功！");
		}, function() {
		    BIONE.closeDialog("xmlMessageEidtWin", "保存失败！");
		});
    }
  	
  //弹出窗口
    function selectElementDialog(options) {
    	var height = $(window).height() - 50;
		var width = $(window).width() - 80;
		$.ligerDialog.open({
			name:'upElementWin',
			title : '选择上级元素',
			width : width,
			height : height,
			url : '${ctx}/frs/xmlMessage/selectUpElementWin',
			buttons : [ {
				text : '根元素设置',
				onclick : f_selectElementRoot
			},{
				text : '确定',
				onclick : f_selectElementOK
			}, {
				text : '取消',
				onclick : f_selectCancel
			} ]
		});
		return false;
	}

  //保存按钮调用方法
	function f_selectElementRoot(a,dialog){
		$("#mainform input[name='upElementId']").val("0");
		$("#mainform input[name='upElementCombBox']").val("0");
		dialog.close();
  	}
  
	//保存按钮调用方法
	function f_selectElementOK(a,dialog){
		var data = dialog.frame.addToParent();
		if(data!=""){
			$("#mainform input[name='upElementId']").val(data.elementId);
			$("#mainform input[name='upElementCombBox']").val(data.elementNm);
		}
		dialog.close();
	}
	//取消按钮调用方法
	function f_selectCancel(a,dialog){
		dialog.close();
	}
	
	//弹出窗口
    function selectRptDialog(options) {
    	var height = $(window).height() - 50;
		var width = $(window).width() - 80;
		$.ligerDialog.open({
			name:'rptDialogWin',
			title : '报表选择',
			width : width,
			height : height,
			url : '${ctx}/frs/xmlMessage/selectRptDialogWin',
			buttons : [  {
				text : '清空',
				onclick : f_selectEmp
			},{
				text : '确定',
				onclick : f_selectRptOK
			}, {
				text : '取消',
				onclick : f_selectCancel
			} ]
		});
		return false;
	}
  //清空
    function f_selectEmp(a,dialog){
    	$("#belongRptNum").val("");
		$("#belongRptNumBox").val("");
		dialog.close();
    }
  //保存按钮调用方法
	function f_selectRptOK(a,dialog){
		var data = dialog.frame.addToParent();
		if(data!=""){
			$("#belongRptNum").val(data.rptNum);
			$("#belongRptNumBox").val(data.rptNum);
		}
		dialog.close();
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/xmlMessage/saveXmlElement"></form>
	</div>
</body>
</html>