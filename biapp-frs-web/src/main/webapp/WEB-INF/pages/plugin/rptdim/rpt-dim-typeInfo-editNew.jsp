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
	jQuery.validator.addMethod("dimTypeNoReg", function(value, element) {
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
	var catalogId='${catalogId}';
	var catalogName='${catalogName}';
	var isCustomed='${isCustomed}';
	var newMaxDimNo='${newMaxDimNo}';
	//var   usedDeptParamTypeNo = '${usedDeptParamTypeNo}';
    //var   defDeptParamTypeNo = '${defDeptParamTypeNo}';
    var   DIM_STS_START = '${DIM_STS_START}';
    //var   startDateInit = '${startDateInit}';
   // var   endDateInit = '${endDateInit}';
    var mainform;
	$(function() {
		var groupicon = "${ctx}/images/classics/icons/communication.gif";
			mainform = $("#mainform").ligerForm({
				inputWidth : 160,
				labelWidth : 80,
				space : 30,
			align : "center",
			fields : [{
		    	name:'catalogId',
		    	type:'hidden'
		    },{
		    	group : "基础信息",
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
				name : "dimTypeNo",
				newline : false,
				width : 215,
				type : "text",
				validate : {
					required:true,
					dimTypeNoReg:true,
	        		maxlength:32,
	        		remote :  {
	        			url : "${ctx}/rpt/frame/dimCatalog/testSameDimTypeNo",
						type : "POST",
						data : {
						}
	        		},
					messages : {
						remote:"维度标识已存在"
					}
				}
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
						}
	        		},
					messages : {
						remote:"维度名称已存在"
					}
				}
			},{
				display : "英文名称 ",
				name : "dimTypeEnNm",
				newline : false,
				width : 215,
				type : "text",
				validate : {
					packageIdReg:true,
				    maxlength : 100
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
			}*//*, {
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
				newline : true,
				type : "select",
				width : 215,
				comboboxName:"def_dept_box",
				options : {
				   isMultiSelect:true,
				   cancelable :true,
					url : "${ctx}/report/frame/idx/defDeptList.json?defDeptParamTypeNo="+defDeptParamTypeNo
				}
			},{	*/
				/*display : "使用部门",
				name : "useDept",
				newline : false,
				type : "select",
				width : 215,
				comboboxName:"use_dept_box",
				options : {
				   isMultiSelect:true,
				   cancelable :true,
					url : "${ctx}/report/frame/idx/useDeptList.json?usedDeptParamTypeNo="+usedDeptParamTypeNo
				}
			}, {*/
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
		    } ]
		});
// 			$("#mainform input[name='startDate']").attr("readonly", "true");
// 			$("#mainform input[name='endDate']").attr("readonly", "true");
			$("#mainform [name='catalogId']").val(catalogId);
			$.ligerui.get('catalog_box').setText(catalogName);
			$.ligerui.get('dim_sts_box').selectValue(DIM_STS_START);
			/* var date = new Date();
			var yy = date.getFullYear();
			var Mm = ((date.getMonth() + 1) < 10) ? ('0' + (date.getMonth() + 1))
					: (date.getMonth() + 1);
			var ddStart = (date.getDate() < 10) ? ('0' + date.getDate()) : date
					.getDate();
			var ddEnd = ((date.getDate()+1) < 10) ? ('0' + (date.getDate()+1)) : (date.getDate()+1);
			$("#startDate").val(yy + '-' + Mm + '-' + ddStart);
			$("#endDate").val(yy + '-' + Mm + '-' + ddEnd); */
			/*$("#startDate").val(startDateInit);
			$("#endDate").val(endDateInit);*/
			if(isCustomed){
				$("#mainform [name='dimTypeNo']").attr("readonly", "true").removeAttr("validate");
				$("#mainform [name='dimTypeNo']").parent().parent().next().find("span.l-star").html('');
			    $("#mainform [name='dimTypeNo']").val(newMaxDimNo);
			}
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
			top.BIONE.tip("保存成功");
			var f = window.parent.document.getElementById("rptDimCatalogTypeInfoTabFrame");
			f.contentWindow.refreshIt();
			window.parent.searchHandler();//更新左侧目录树
			BIONE.closeDialog("rptDimTypeInfoAddWin");
		}, function() {
			top.BIONE.tip("保存失败");
		});
	}
	
	function f_close() {
		BIONE.closeDialog("rptDimTypeInfoAddWin");
	}
</script>

<title>维度管理</title>
</head>
<body>
<div id="template.center">
	<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/frame/dimCatalog/dimType">
	</form>
</div>
</body>
</html>