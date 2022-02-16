<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style type="text/css">
#center {
	overflow: auto;
}
</style>
<script type="text/javascript">
var isDict = '${readOnly}' == 'true';
var isEdit = '${id}'.length > 0;
var indxInfoObj = parent.indxInfoObj;
var defSrc = indxInfoObj.defSrc;
var indexNo = indxInfoObj.indexNo;
var indexVerId = indxInfoObj.indexVerId;
var isUpdate  = '0';
var defaultNo;
var changeId;

jQuery.validator.addMethod("indexNoReg", function(value, element) {
    var packageCode = /^[0-9a-zA-z]*$/;
    return this.optional(element) || (packageCode.test(value));
}, "请不要输入包含中文及特殊字符");


jQuery.validator.addMethod("specialSymbeol", function(value, element) {
    var packageCode = /^[\u4e00-\u9fa5\da-zA-Z－_、（）\[\]]+$/;
    return this.optional(element) || (packageCode.test(value));
}, "该字段不能输入特殊符号");

$(function() {	
	
	var fields = [{
		display : "指标目录",
		name : "indexCatalogNm",
		newline : false,
		type: 'popup',
		options: {
			cancelable: false,
			readonly: isDict,
			valueFieldID: 'indexCatalogNo',
			onButtonClick: function() {
				var cataNo = parent.indxInfoObj.indexCatalogNo;
				BIONE.commonOpenDialog('指标目录', "idxCatalogSlct", 400, 300, 
					"${ctx}/report/frame/idx/catalogTree?catalogId="
							+cataNo+"&defSrc=" + defSrc);
			}
		}
	},{
		display : "指标类型",
		name : "indexType",
		newline : false,
		type : "select",
		validate :isDict==false? {
			required : false,
			messages : {
				required : "请选择一个指标类型。"
			}
		}:{},
		options : {
			readonly: isDict || isEdit,
			initValue: '01',
			url : "${ctx}/report/frame/idx/indexTypeList.json?situation=field&defSrc="+defSrc,
			onSelected : function(value) {
				if (!'${id}') {
					getAutoIdxNoPrefix(value);
				}
				if(value == '01'||value == '04'||value == '05'){ //'01'-根指标,'04'-泛化指标, '05'-总账指标
					if($.ligerui.get("isSave")){
						//$.ligerui.get("isSave").setDisabled("true");
						$.ligerui.get("isSave").set('disabled', true);
					}
				}else{//派生指标、组合指标
					if($.ligerui.get("isSave")){
						//$.ligerui.get("isSave")._setDisabled();
					    $.ligerui.get("isSave").set('disabled', false);
					}
				}
			}
		}
	},{
		display : "指标编号",
		name : "indexNo",
		newline : true,
		type : "text",
		width : '${id}'?215:120,
		options: {
			readonly: isDict || isEdit
		},
		validate : !isEdit?{
			indexNoReg :true,
		    required : true,
		    maxlength : 32,
    		remote :  {
    			url : "${ctx}/report/frame/idx/testSameIndexNo",
				type : "POST",
				data : {
					"indexCatalogNo" :indxInfoObj.indexCatalogNo,
					"isUpdate":isUpdate,
					"oldIndexNo":indexNo
				}
    		},
			messages : {
				remote:"指标编号已存在"
			}
		}:{}
	}, {
		display : "指标名称",
		name : "indexNm",
		newline : false,
		type : "text",
		validate :isDict==false?{
		    required : true,
		    specialSymbeol : true,
		    maxlength : 100,
    		remote :  {
    			url : "${ctx}/report/frame/idx/testSameIndexNm",
				type : "POST",
				data : {
					indexNo: "${id}"
				}
    		},
			messages : {
				remote:"指标名称已存在"
			}
		}:{},
		options: {
			readonly: isDict
		}
	},{
		display : '启用时间',
		name : 'startDate',
		id : 'startDate',
		newline : true,
		width : 215,
		type : 'date',
		validate :isDict==false? {
			required : true
		}:{},
		options : {
			readonly: isDict,
			initValue: '${today}',
			showType: "year",
			format : "yyyyMMdd",
			cancelable:!isDict
		}
	},{
		display : "生成周期",
		name : "calcCycle",
		newline : false,
		type : "select",
		width : 215,
		validate : isDict==false?{
			messages : {
				required : "请选择一个生成周期。"
			}
		}:{},
		options : {
			readonly: isDict,
			initValue: '01',
			url : "${ctx}/report/frame/idx/calcCycleList.json"
		}
	},{
		display : "统计类型",
		name : "statType",
		newline :true ,
		type : "select",
		width : 215,
		options : {
			readonly: isDict,
			initValue: '02',
			url : "${ctx}/report/frame/idx/statTypeList.json"
		}
	},{
		display : "是否可汇总",
		name : "isSum",
		newline : false,
		type : "select",
		width : 215,
		options : {
			readonly: isDict,
			initValue: 'Y',
			url : "${ctx}/report/frame/idx/isSumList.json"
		}
	},{
		display : "数据格式",
		name : "dataType",
		newline : true,
		type : "select",
		validate :isDict==false? {
			required : false,
			messages : {
				required : "请选择一个数据类型。"
			}
		}:{},
		options : {
			readonly: isDict,
			initValue: '01',
			url : "${ctx}/report/frame/idx/dataTypeList.json"
		}
	},{
		display : "数据单位",
		name : "dataUnit",
		newline : false,
		type : "select" ,
		width : 215,
		validate : {
			messages : {
				required : "请选择一个数据单位。"
			}
		},
		options : {
			readonly: isDict,
			initValue: '01',
			url : "${ctx}/report/frame/idx/dataUnitList.json"
		}
	},{
		display : "是否落地",
		name : "isSave",
		newline : true,
		type : "hidden",
		width : 215,
		options : {
			readonly: isDict,
			initValue: 'Y',
			url : "${ctx}/report/frame/idx/isPublishList.json",
			cancelable: false
		}
	},{
		display : "是否发布",
		name : "indexSts",
		newline : true,
		type : "select",
		options : {
			readonly: isDict,
			initValue: 'Y',
			url : "${ctx}/report/frame/idx/isPublishList.json"
		}
	},{
		display : "业务条线",
		name : "lineId",
	    type : 'select',
	    newline: false,
	    options : {
	    	readonly: isDict,
	    	url : "${ctx}/report/frame/idx/getLineInfo"
	    }
	},{
		/*lcy 20190819 业务分库 增加代码*/
		display : "业务分库",
		name : "busiLibId",
	    type : 'select',
	    newline:true,
		options : {
	    	readonly: isDict,
			url : "${ctx}/rpt/frame/businesslib/getComboInfo.json?typeNo=busiLib",
			cancelable:!isDict
	    }
	},{
		display : "联系人",
		name : "userId",
		newline : true,
		type : "select",
		options : {
			readonly: isDict,
			onBeforeOpen : function() {
				var url = "${ctx}/report/frame/idx/userEdit";
				window.parent.$window = window;
				dialog = window.parent.BIONE.commonOpenLargeDialog('请选择联系人',
								"selectWin",  url);
				return false;
			}
		}
	},{
		display : "联系人电话",
		name : "tel",
		newline : false,
		attr : {
			readOnly :true
		}
	},{
		display : "联系人邮箱",
		name : "mail",
		newline : true,
		attr : {
			readOnly :true
		}
	},{
		display : "主管部门",
		name : "deptId",
		newline : false,
		type : "select",
		options : {
			readonly: isDict,
			onBeforeOpen : function() {
				var url = "${ctx}/report/frame/idx/deptEdit";
				window.parent.$window = window;
				dialog = window.parent.BIONE.commonOpenDialog('请选择主管部门',
								"selectWin", 400,500, url);
				return false;
			}
		},
		validate : {
			messages : {
				required : "请选择一个主管部门。"
			}
		}
	}, {
		group : "其它",
		groupicon : '${ctx}/images/classics/icons/communication.gif',
		display : "业务定义",
		name : "busiDef",
		newline : true,
		type : "textarea",
		width : 543,
		validate : {
			maxlength : 500
		},
		attrs: {
			readonly: 'readonly'
		}
	},{
		display : "业务口径",
		name : "busiRule",
		newline : false,
		type : "textarea",
		width : 543,
		validate : {
			maxlength : 500
		},
		attrs: {
			readonly: 'readonly'
		}
	}];
	if ('${id}') {
		if (fields[2].validate.remote) {
			delete fields[2].validate.remote;
		}
	}
	else{
		$("input[ligeruiid=idxBelongType]").parent().parent().parent().hide();
	}
	$('#mainform').ligerForm({
		inputWidth: 215,
		fields: fields
	});
	if (!'${id}') {
		$("input[ligeruiid=idxBelongType]").parent().parent().parent().parent().hide();
	}
	jQuery.metadata.setType("attr", "validate");
	BIONE.validate("#mainform");
	
	//指标编号相应样式
	$("#indexNo").bind("focusin" , function(){
		if($("#indexNo").attr("isNull") == "true"){
			$("#indexNo").removeClass("l-text-field-null").val("");
		}
	});
	$("#indexNo").bind("focusout" , function(){
		if($("#indexNo").val() != null
				&& $("#indexNo").val() != ""){
			$("#indexNo").attr("isNull","false");
		}else{
			$("#indexNo").addClass("l-text-field-null").val(defaultNo).attr("isNull","true");
		}
	});
	if(!'${id}'){
		$("#indexNo").parent().before("<input id='prefix' type='text' />").css("float","right");
		$("#indexNo").parent().parent().css("width","215px");
		
		$("#prefix").ligerComboBox({
			width : "80",
			onSelected :function(id,text){
				if(id != "" && changeId !=id){
					getAutoIndexNo(id);
					changeId = id;
				}
			} 
		});
		$("#prefix").parent().parent().css("float","left");
	}
	
if ('${id}') {
		$.ajax({
			url: '${ctx}/report/frame/idx/${id}/editorInfo',
			type: 'post',
			data: {
				verId: indexVerId
			},
			dataType: 'json',
			beforeSend: function() {
				if ($('.l-window-mask:visible').length == 0) {
					BIONE.showLoading();
				}
			},
			complete: function() {
				BIONE.hideLoading();
			},
			success: function(data) {
				if (data && data.indexNo) {
					data.oldStartDate = data.startDate;
					var obj = parent.indxInfoObj = new parent.IdxInfo(data);
					// 初始化ComboBox
					$.each(liger.find('ComboBox'), function(i, n) {
						if (data[n.id]) {
							n.setValue(data[n.id]);
						}
					});
					// 初始化text和textarea
					$('#mainform').find('input[ltype="text"], input[ltype="date"], textarea').each(function(i, n) {
						if (data[n.id]) {
							n.value = data[n.id];
						}
					});
					//初始化目录
					liger.get('indexCatalogNm').setText(data.indexCatalogNm);
					liger.get('indexCatalogNm').setValue(data.indexCatalogNo);
					liger.get('userId')._changeValue(data.userId,data.userNm);
					liger.get('deptId')._changeValue(data.deptId,data.deptNm);
				}
			}
		});
	} else {
		liger.get('indexCatalogNm').setText('${catalog.indexCatalogNm}');
		liger.get('indexCatalogNm').setValue('${catalog.indexCatalogNo}');
		$.ajax({
			   cache : false,
			   async : true,
			   url: "${ctx}/report/frame/idx/inverse?d="+new Date(), 
			   dataType : 'json',
			   type: "GET",  
			   beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
			   success: function(data){
				   liger.get('userId')._changeValue(data.userId,data.userName);
				   liger.get('deptId')._changeValue(data.deptId,data.deptName);
				   liger.get('tel').setValue(data.tel);
				   liger.get('mail').setValue(data.email);
			   }
			}); 
	}
	$("#isSave").val("Y");//所有衍生指标都要落地
});
function getData() {
	var data = {};
	$("#mainform").find('input[ltype], input:not([ltype]):hidden, textarea')
		.each(function(i, item) {
			data[item.name] = item.value;
		});
	return data;
}
function getAutoIndexNo(value){
	$.ajax({
		url: '${ctx}/report/frame/idx/getAutoIndexNo',
		type: 'get',
		data: {
			prefix : value
		},
		dataType: 'json',
		success: function(result) {
			defaultNo = result.idxNo;
			$("#indexNo").val(result.idxNo);
			$("#indexNo").addClass("l-text-field-null").val(result.idxNo).attr("isNull","true");
		},
		error : function(result, b) {
			BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
		}
	});
}
function getAutoIdxNoPrefix(value){
	$.ajax({
		url: '${ctx}/report/frame/idx/indexNoPrefix',
		type: 'get',
		data: {
			indexType : value
		},
		dataType: 'json',
		success: function(result) {
			if(result.length == '0'){
				$("#prefix").parent().parent().hide();
				$("#indexNo").parent().css("width","213px");
				$("#indexNo").css("width","209px");
				$("#indexNo").removeClass("l-text-field-null").val("");
			}else{
				$("#prefix").parent().parent().show();
				$("#indexNo").parent().css("width","118px");
				$("#indexNo").css("width","114px"); 
				$.ligerui.get("prefix").setData(result);
				$.ligerui.get("prefix").setValue(result[0].id);	
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
		<form id="mainform"
			action="${ctx}/dataquality/rulemanage/meta-rule-grp/saveRuleGrpType"
			method="POST"></form>
	</div>
</body>
</html>