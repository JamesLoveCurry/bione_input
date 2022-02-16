<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	jQuery.validator.addMethod("packageIdReg", function(value, element) {
	    var packageCode = /^[0-9a-zA-z]*$/;
	    return this.optional(element) || (packageCode.test(value));
	}, "请不要输入包含中文及特殊字符");
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
	var upNo='${upNo}';
	var upDimItemNm='${upDimItemNm}';
	var dimTypeNo='${dimTypeNo}';
	//var   usedDeptParamTypeNo = '${usedDeptParamTypeNo}';
    //var   defDeptParamTypeNo = '${defDeptParamTypeNo}';
    var   ITEM_STS_START = '${ITEM_STS_START}';
    //var   startDateInit = '${startDateInit}';
    //var   endDateInit = '${endDateInit}';
    var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
			mainform = $("#mainform").ligerForm({
				inputWidth : 160,
				labelWidth : 80,
				space : 30,
			align : "center",
			fields : [ {
		    	name:'upNo',
		    	type:'hidden'
		    },{
		    	name:'id.dimTypeNo',
		    	type:'hidden'
		    },{
				display : "上级维度项",
				name : "upDimItemNm",
				newline : false,
				width : 215,
				type : "text",
			    group : "维度项信息",
				groupicon : groupicon
			},{
				display : "维度项标识 ",
				name : "id.dimItemNo",
				newline : false,
				width : 215,
				type : "text",
				validate : {
					packageIdReg:true,
				    required : true,
				    maxlength : 32,
				    remote :  {
	        			url : "${ctx}/rpt/frame/dimCatalog/testRptDimItemNo",
						type : "POST",
						data : {
							'id.dimTypeNo':dimTypeNo
						}
	        		},
				    messages : {
						remote : "同维度下维度项标识已存在"
				    }
				}
			}, {
				display : "维度项名称 ",
				name : "dimItemNm",
				newline : true,
				width : 215,
				type : "text",
				validate : {
				    required : true,
				    maxlength : 100,
				    remote :  {
	        			url : "${ctx}/rpt/frame/dimCatalog/testRptDimItemNm",
						type : "POST",
						data : {
							'id.dimTypeNo':dimTypeNo
						}
	        		},
				    messages : {
						remote : "同维度下维度项名称已存在"
				    }
				}
			},{
				display : "维度项状态",
				name : "itemSts",
				newline :false ,
				type : "select",
				comboboxName:"item_sts_box",
				width : 215,
				options : {
					url : "${ctx}/report/frame/idx/indexStsList.json"
				},
				validate : {
					required : true
				}
			},{
				display: '排列顺序',
				name: 'rankOrder',
				type: 'text',
				width : 215,
				newline:true,
				validate:{
					digits: true
				}
			}/*,{
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
			}*/,{
				group : "业务信息",
				groupicon : groupicon,
				/*display : "定义部门",
				name : "defDept",
				newline : true ,
				type : "select",
				width : 215,
				comboboxName:"def_dept_box",
				options : {
				   isMultiSelect:true,
				   cancelable :true,
					url : "${ctx}/report/frame/idx/defDeptList.json?defDeptParamTypeNo="+defDeptParamTypeNo
				}
			},{
				display : "使用部门",
				name : "useDept",
				newline :false,
				type : "select",
				width : 215,
				comboboxName:"use_dept_box",
				options : {
				   isMultiSelect:true,
				   cancelable :true,
					url : "${ctx}/report/frame/idx/useDeptList.json?usedDeptParamTypeNo="+usedDeptParamTypeNo
				}
			},{*/
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
			}, {
				display : "备注",
				name : "remark",
				newline : true,
				type : "textarea",
				width:543,
				validate : {
				    maxlength : 500
				}
		    } ]
		});
			$("#mainform [name='upNo']").val(upNo);
			$("#mainform [name='id.dimTypeNo']").val(dimTypeNo);
			$("#mainform [name='upDimItemNm']").val(upDimItemNm);
			$("#mainform [name='upDimItemNm']").attr("disabled", "disabled");
// 			$("#mainform input[name='startDate']").attr("readonly", "true");
// 			$("#mainform input[name='endDate']").attr("readonly", "true");
			$.ligerui.get('item_sts_box').selectValue(ITEM_STS_START);
			/* var date = new Date();
			var yy = date.getFullYear();
			var Mm = ((date.getMonth() + 1) < 10) ? ('0' + (date.getMonth() + 1))
					: (date.getMonth() + 1);
			var ddStart = (date.getDate() < 10) ? ('0' + date.getDate()) : date
					.getDate();
			var ddEnd = ((date.getDate()+1) < 10) ? ('0' + (date.getDate()+1)) : (date.getDate()+1);
			$("#startDate").val(yy + '-' + Mm + '-' + ddStart);
			$("#endDate").val(yy + '-' + Mm + '-' + ddEnd); */
		//	$("#startDate").val(startDateInit);
		//	$("#endDate").val(endDateInit);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : f_close
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});
		BIONE.addFormButtons(buttons);
	    
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	});

	function f_save() {
		BIONE.submitForm($("#mainform"), function() {
			parent.BIONE.tip("保存成功");
			window.parent.frames["rptDimCatalogTypeInfoTabFrame"].refreshIt();
			BIONE.closeDialog("rptDimItemInfoAddWin");
		}, function() {
			parent.BIONE.tip("保存失败");
		});
	}
	
	function f_close() {
		BIONE.closeDialog("rptDimItemInfoAddWin");
	}
</script>

<title>维度项管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/frame/dimCatalog/dimItem">
	</form>
</div>
</body>
</html>