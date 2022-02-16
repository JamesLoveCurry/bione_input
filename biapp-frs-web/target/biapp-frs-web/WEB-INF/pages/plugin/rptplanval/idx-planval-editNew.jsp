<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
 	//var parentCata = window.parent;
	/* var indexNo = parentCata.indexNo;
	var indexVerId = parentCata.indexVerId;
	var indexNm = parentCata.indexNm; */
	var indexCatalogNo = '${indexCatalogNo}';
	var indexNo = '${indexNo}';
	var indexNm = '${indexNm}';
	var editFlag = '${editFlag}';
	var orgNo = "";
	var orgNm = "";

	jQuery.validator.addMethod("numberReg", function(value, element) {
	    var packageCode = /(^[0]$)|(^[1-9][0-9]*$)/;
	    return this.optional(element) || (packageCode.test(value));
	}, "输入正确格式的正整数");
	
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    $(function() {
		$("#mainform").ligerForm({
		    fields : [ {
				name : "indexNo",
				type : "hidden"
			},{
				group : "计划值校验属性",
				groupicon : groupicon,
				display : "对象名称",
				name : 'indexNm',
				width: 244,
				newline : true,
				type : 'text',
				validate : {
			   		required : true
				}
		    },{
				display : '机构名称',
				name : 'orgNm',
				newline : false,
				type : 'popup',
				width: 244,
				options: {
					cancelable: false,  //是否显示弹框的关闭小×
					valueFieldID: 'orgNo',
					onButtonClick: function() {
						var orgNo = parent.orgNo;
						BIONE.commonOpenDialog('机构目录', "orgNoSelect", 400, 300, 
							"${ctx}/rpt/frame/idx/planval/orgTree?editFlag=0&indexNo="+indexNo);
					}
				},
				validate : {
			   		required : true 
				}
		    },{
				display : '计划年份',
				name : 'dataDate',
				newline : true,
				type : "date",
				width: 244,
				options:{
					cancelable: false,
					showType:"year",
					format: "yyyy"
				},
				validate : {
			   		required : true
				}
		    }, {
		    	display : '币种',
				name : 'currency',
				newline : false,
				type : 'select',
				width: 244,
				options: {
					cancelable: false,
					url : '${ctx}/report/frame/idx/getDimInfoTree?dimTypeNo=CURRENCY',
					initValue : '-',
					onSelected: function(value, text){
						if(text && text != ""){
							initData();
						}
					}
				},
				validate : {
			   		required : true
				}
			},{
				display : '计划值',
				name : 'indexVal',
				newline : false,
				type : 'text',
				width: 244,
				validate : {
			   		required : false,
			   		numberReg : true
				}
		    } ]
		});
		
		$("#mainform input[name=indexNo]").val(indexNo);
		$("#mainform input[name=indexNm]").val(indexNm);
		$("#mainform input[name=indexNm]").attr("readonly", "true");
		$("#mainform input[name=dataDate]").attr("readonly", "true");
		$.ajax({
		   cache : false,
		   async : true,
		   url: "${ctx}/rpt/frame/idx/planval/testDimData?indexNo="+ indexNo +"&d="+new Date().getTime(),
		   dataType : 'json',
		   type: "GET",  
		   success: function(returnData){
			  if(returnData== true){
				  $("#currency").parent().parent().parent().parent().remove();
				/*   $("#currency").parent().parent().parent().parent().hide();
				     $("#currency").parent().parent().parent().parent().show();
				  */
				  }
			  }
		});
		jQuery.metadata.setType("attr", "validate");  //表示从表单项的validate属性取得验证规则
		BIONE.validate($("#mainform"));
		
		var buttons = [];
		buttons.push({
		    text : '取消',
		    onclick : function() {
		    	window.parent.closeDsetBox(editFlag);
		    }
		});
		buttons.push({
		    text : '下一步',
		    onclick : f_cache
		}); 
		
		/* buttons.push({
		    text : '保存',
		    onclick : f_save
		}); */

		BIONE.addFormButtons(buttons);
    });
    
    function f_cache() {
		parent.canSelect=true;
		parent.datasetObj.indexNo = $("#mainform input[name=indexNo]").val();
		parent.datasetObj.dataDate = $("#mainform input[name=dataDate]").val();
		parent.datasetObj.orgNo = $("#mainform input[name=orgNo]").val();
		parent.datasetObj.currency = $("#mainform input[name=currency]").val();
	    parent.datasetObj.currencyId = $("#mainform input[name=currencyId]").val();
	    parent.datasetObj.indexVal = $("#mainform input[name=indexVal]").val();
	    parent.datasetObj.saveData = f_save;
		
		parent.next(editFlag);
	}
    
    function f_save() {
    	BIONE.submitForm($("#mainform"), function() {
			window.parent.closeDsetBox(editFlag);
			BIONE.tip("添加成功");
			}, function() { 
				window.parent.closeDsetBox(editFlag);
				BIONE.tip("添加失败");
		}); 
     } 

</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform"
			action="${ctx}/rpt/frame/idx/planval/add"></form>
	</div>
</body>
</html>