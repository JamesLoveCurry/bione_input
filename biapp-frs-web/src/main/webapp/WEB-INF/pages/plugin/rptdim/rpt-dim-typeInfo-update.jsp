	<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	jQuery.validator.addMethod("packageIdReg", function(value, element) {
	    var packageCode = /^[a-zA-z]*$/;
	    return this.optional(element) || (packageCode.test(value));
	}, "只能输入英文字符");
	/*jQuery.validator.addMethod("startDateCheck", function(value, element) {
		var  startDateVal =  $("#startDate").val();
		var  endDateVal =  $("#endDate").val();
		if(startDateVal!=""&&endDateVal!=""&&startDateVal>=endDateVal){
      	    return  false;
		}else{
			return  true;
		}
	}, "启用时间应小于结束时间");
	jQuery.validator.addMethod("endDateCheck", function(value, element) {
		var  startDateVal =  $("#startDate").val();
		var  endDateVal =  $("#endDate").val();
		if(startDateVal!=""&&endDateVal!=""&&startDateVal>=endDateVal){
      	    return  false;
		}else{
			return  true;
		}
	}, "结束时间应大于启用时间");*/
	var dimTypeNo='${dimTypeNo}';
	var catalogName='${catalogName}';
	var catalogId='${catalogId}';
	var disabledFlag='${flag}';
	var isPreview  = true;
	if(disabledFlag){
		isPreview  = false;
	}
	//var usedDeptParamTypeNo = '${usedDeptParamTypeNo}';
   // var defDeptParamTypeNo = '${defDeptParamTypeNo}';
    var mainform;
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    var columns =[ {
    	name:'dimTypeNo',
    	type:'hidden'
    },{
    	name:'catalogId',
    	type:'hidden'
    },{
    	group : "维度信息",
		groupicon : groupicon,

		display : "维度目录",
		name : "catalogName",
		newline : false,
		width : 215,
		type : "select",
		comboboxName: "catalog_box",
		options : {
			onBeforeOpen : function() {
				var url = "${ctx}/rpt/frame/dimCatalog/catalogTree?catalogId="
						+ catalogId;
				dialog = BIONE
						.commonOpenDialog('维度目录',
								"dimCatalogSlct", 400,300, url);
				return false;
			}
		}
	},{
		display : "维度标识 ",
		name : "dimTypeNoShow",
		newline : false,
		width : 215,
		type : "text"
	},{
		display : "维度名称 ",
		name : "dimTypeNm",
		newline : true,
		width : 215,
		type : "text",
		validate : {
			required:true,
    		maxlength:100,
    		remote :  {
    			url : "${ctx}/rpt/frame/dimCatalog/testSameDimTypeNm",
				type : "POST",
				data : {
					"dimTypeNo":dimTypeNo
				}
    		},
			messages : {
				remote:"维度名称已存在"
			}
		}
	}, {
		display : "英文名称 ",
		name : "dimTypeEnNm",
		newline : false,
		width : 215,
		type : "text",
		validate : {
			packageIdReg:true,
		    maxlength : 100
		}
	}];
   /* if(disabledFlag){
    	columns.push({
    		display : '启用时间',
    		name : 'startDate',
    		id : 'startDate',
    		newline : true,
    		width : 215,
    		type : 'text'
    	}, {
    		display : '结束时间',
    		name : 'endDate',
    		id : 'endDate',
    		newline : false,
    		width : 215,
    		type : 'text'
    	});
    }else{
    	columns.push({
    		display : '启用时间',
    		name : 'startDate',
    		id : 'startDate',
    		newline : true,
    		width : 215,
    		type : 'date',
    		validate : {
    			startDateCheck:true,
    			required : true
    		},
			options : {
				format : "yyyyMMdd"
			}
    	}, {
    		display : '结束时间',
    		name : 'endDate',
    		id : 'endDate',
    		newline : false,
    		width : 215,
    		type : 'date',
    		validate : {
    			endDateCheck:true,
    			required : true
    		},
			options : {
				format : "yyyyMMdd"
			}
    	});
    }*/
    columns.push({
    	group : "业务信息",
		groupicon : groupicon,
		/*display : "定义部门",
		name : "defDept",
		newline : true,
		width : 215,
		type : "select",
		comboboxName:"def_dept_box",
		width : 215,
		options : {
		   isMultiSelect:true,
		   cancelable :isPreview,
			url : "${ctx}/report/frame/idx/defDeptList.json?defDeptParamTypeNo="+defDeptParamTypeNo
		}
	},{
		
		display : "使用部门",
		name : "useDept",
		newline : false,
		width : 215,
		type : "select",
		comboboxName:"use_dept_box",
		width : 215,
		options : {
		   isMultiSelect:true,
		   cancelable :isPreview,
			url : "${ctx}/report/frame/idx/useDeptList.json?usedDeptParamTypeNo="+usedDeptParamTypeNo
		}
	},{*/
		display : "维度结构",
		name : "dimTypeStruct",
		newline : true,
		width : 215,
		type : "select",
		comboboxName:"struct_box",
		validate : {
			required : true,
			messages : {
				required : "请选择一个维度结构。"
			}
		},
		options : {
			url : "${ctx}/rpt/frame/dimCatalog/strutList.json"
		}
	},{
		display : "维度状态",
		name : "dimSts",
		newline :false ,
		type : "select",
		comboboxName:"dim_sts_box",
		width : 215,
		options : {
			url : "${ctx}/report/frame/idx/indexStsList.json"
		},
		validate : {
			required : true
		}
	},{
		display : "业务定义",
		name : "busiDef",
		newline : true,
		type : "textarea",
		width : 543,
		validate : {
			maxlength : 500
		}
	},{
		display : "业务规则",
		name : "busiRule",
		newline : true,
		type : "textarea",
		width : 543,
		validate : {
			maxlength : 500
		}
	},{
		display : "维度描述 ",
		name : "dimTypeDesc",
		newline : true,
		type : "textarea",
		width:543,
		validate : {
		    maxlength : 500
		}
    });
	$(function() {
		mainform = $("#mainform").ligerForm({
			inputWidth : 160,
			labelWidth : 80,
			space : 30,
			align : "center",
			fields : columns
		});
			var buttons = [];
			$.ajax({
				type : "POST",
				url : "${ctx}/rpt/frame/dimCatalog/getUpdateInfo.json",
				dataType : 'json',
				data : {
					'dimTypeNo':dimTypeNo
				},
				success : function(model) {
					$("#mainform [name='dimTypeNo']").val(dimTypeNo);
					$("#mainform [name='dimTypeNoShow']").val(dimTypeNo);
					$("#mainform [name='catalogId']").val(catalogId);
					$.ligerui.get('catalog_box').setText(catalogName);
					
					$("#mainform [name='dimTypeNm']").val(model.dimTypeNm);
					$("#mainform [name='dimTypeEnNm']").val(model.dimTypeEnNm);
					$("#mainform [name='dimTypeDesc']").val(model.dimTypeDesc);
					//$("#startDate").val(model.startDate);
					//$("#endDate").val(model.endDate);
					$.ligerui.get('struct_box').selectValue(model.dimTypeStruct);
					$("#mainform textarea[name='busiRule']").val(model.busiRule);
					$("#mainform textarea[name='busiDef']").val(model.busiDef);
					//$.ligerui.get('def_dept_box').selectValue(model.defDept);//“定义部门”删掉，不再获取这个值
					//$.ligerui.get('use_dept_box').selectValue(model.useDept);//“使用部门”删掉，不再获取这个值
					$.ligerui.get('dim_sts_box').selectValue(model.dimSts);
				}
			});
			
			if(disabledFlag){
				$("#mainform [name='dimTypeNoShow']").attr("readonly", "readonly");
				$("#mainform [name='dimTypeNm']").attr("readonly", "readonly");
				$("#mainform [name='dimTypeNm']").parent().parent().parent().find(".l-star").html('');
				$("#mainform [name='dimTypeEnNm']").attr("readonly", "readonly");
				$("#mainform [name='dimTypeDesc']").attr("readonly", "readonly");
				$("#mainform [name='busiRule']").attr("readonly", "readonly");
				$("#mainform [name='busiDef']").attr("readonly", "readonly");
				$.ligerui.get('struct_box').setDisabled();
				$('#dimTypeStruct').parent().parent().parent().parent().find(".l-star").html('');
				//$.ligerui.get('def_dept_box').setDisabled();//“定义部门”删掉，不再设置这个combobox
				//$.ligerui.get('use_dept_box').setDisabled();//"使用部门"删掉，不再设置这个combobox
				$.ligerui.get('dim_sts_box').setDisabled();
				$.ligerui.get('catalog_box').setDisabled();
				//$("#mainform input[name='startDate']").attr("readonly", "readonly");
	 			//$("#mainform input[name='endDate']").attr("readonly", "readonly");
				$("#mainform").attr("action","${ctx}/rpt/frame/dimCatalog/updateType?flag="+disabledFlag);
				$("input").css("color","#333");
				buttons.push({
					text : '关闭',
					onclick : f_close
				});
			}else{
				$("#mainform [name='dimTypeNoShow']").attr("disabled", "disabled");
				buttons.push({
					text : '取消',
					onclick : f_close
				});
				buttons.push({
					text : '保存',
					onclick : f_save
				});
				/*buttons.push({
					text : '发布历史版本',
					onclick : f_newVer,
					width:80
				});*/
			}
			BIONE.addFormButtons(buttons);
		    
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate("#mainform");
		});
      /* function  f_newVer(){
        	$.ajax({
				type : "POST",
				url : "${ctx}/rpt/frame/dimCatalog/saveAsHistoryVer.json",
				dataType : 'json',
				data : {
					'dimTypeNo':dimTypeNo
				},
				success : function(result) {
					if(result=="1"){
					  BIONE.tip("历史版本发布成功！");
					  f_close();
					}else{
					  BIONE.tip("历史版本发布失败！");
					}
				},
				beforeSend : function() {
 					BIONE.loading = true;
 					BIONE.showLoading("正在发布中...");
 				},
 				complete : function() {
 					BIONE.loading = false;
 					BIONE.hideLoading();
 				},
 				error : function(XMLHttpRequest, textStatus, errorThrown) {
 					BIONE.tip('操作失败,错误信息:' + textStatus);
 				}
			});
        }*/
		function f_save() {
			/*var  startDateVal =  $("#startDate").val();
			var  endDateVal =  $("#endDate").val();
			if(startDateVal==""||endDateVal==""){
				BIONE.tip("启用时间或结束时间不能为空");
				return  false;
			}
			if(startDateVal>=endDateVal){
				BIONE.tip("启用时间应小于结束时间");
				return  false;
			}*/
			BIONE.submitForm($("#mainform"), function() {
				top.BIONE.tip("保存成功");
				var f = window.parent.document.getElementById("rptDimCatalogTypeInfoTabFrame");
				f.contentWindow.refreshIt();
				window.parent.searchHandler();
				BIONE.closeDialog("rptDimTypeInfoUpdateWin");
			}, function() {
				parent.BIONE.tip("保存失败");
			});
		}
		
		function f_close() {
			BIONE.closeDialog("rptDimTypeInfoUpdateWin");
			parent.jQuery.ligerui.get(dialogName)
		}
</script>

<title>维度管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/frame/dimCatalog/updateType">
	</form>
</div>
</body>
</html>