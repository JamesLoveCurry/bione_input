<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var indexNo = '${indexNo}';
	var indexVerId = '${indexVerId}';
	var dataDate = '${dataDate}';
	var orgNo = '${orgNo}';
	var currency = '${currency}';
	var currencyId = '${currencyId}';
	var indexVal = '${indexVal}';
	var editFlag = '${editFlag}';
	
	//数据格式验证
	jQuery.validator.addMethod("numberReg", function(value, element) {
	    var packageCode = /(^[0]$)|(^[1-9][0-9]*$)/;
	    return this.optional(element) || (packageCode.test(value));
	}, "输入正确格式的正整数");

    $(function() {
		var mainform = $("#form1").ligerForm({
			fields : [ {
				name : "indexNo",
				type : "hidden"
			},{
				name : "indexVerId",
				type : "hidden"
			},{
				group : "计划值校验属性",
				groupicon : groupicon,
				display : "对象名称",
				name : 'indexNm',
				newline : true,
				type : 'text',
				width: 244,
				validate : {
			   		required : false
				}
		    },{
		    	display : '机构名称',
				name : 'orgNm',
				newline : false,
				type : 'text',
				width: 244,
				validate : {
			   		required : false
				}
		    },{
				display : '计划年份',
				name : 'dataDate',
				newline : true,
				type : "text",
				width: 244,
				validate : {
			   		required : false
				}
		    },{
				display : '币种',
				name : 'currency',
				newline : false,
				type : 'text',
				width: 244,
				validate : {
			   		required : false
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
		
		$.ajax({
			type : "GET",
			url : "${ctx}/rpt/frame/idx/planval/getPlanvalInfoByParams?indexNo=" + indexNo 
	    		+ "&dataDate=" + dataDate+ "&currency=" + currency + "&orgNo=" + orgNo +"&indexVal=" + indexVal,
	    	dataType : "json" ,
	    	success: function(data){
	    		$("#form1 input[name=indexNo]").val(data.indexNo);
	    		$("#form1 input[name=dataDate]").val(data.dataDate);
	    		$("#form1 input[name=currency]").val(data.currency);
	    		$("#form1 input[name=indexNm]").val(data.indexNm);
	    		$("#form1 input[name=orgNo]").val(data.orgNo);
	    		$("#form1 input[name=orgNm]").val(data.orgNm);
	    		$("#form1 input[name=indexVal]").val(data.indexVal);

	    	}
		});
		
		$("#form1 input[name=indexNm]").attr("readonly", "true").removeAttr("validate");	
		$("#form1 input[name=orgNm]").attr("readonly", "true").removeAttr("validate");	
		$("#form1 input[name=dataDate]").attr("readonly", "true").removeAttr("validate");	
		$("#form1 input[name=currency]").attr("readonly", "true").removeAttr("validate");	
		
		$.ajax({
			   cache : false,
			   async : true,
			   url: "${ctx}/rpt/frame/idx/planval/testDimData?indexNo="+ indexNo +"&d="+new Date().getTime(),
			   dataType : 'json',
			   type: "GET",  
			   success: function(returnData){
				  if(returnData==true){
					  $("#currency").parent().parent().parent().parent().hide();
					  }else if(returnData==false){
					  $("#currency").parent().parent().parent().parent().show();
					  }
				  }
			});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#form1"));   //用于表单数据验证

		var managers = $.ligerui.find($.ligerui.controls.Input);
		for ( var i = 0, l = managers.length; i < l; i++) {
		    //改变了表单的值，需要调用这个方法来更新ligerui样式
		    managers[i].updateStyle();
		}
	
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
		BIONE.addFormButtons(buttons);
		
		setUrl();   //动态指定Form表单的action
    });
    
	function f_cache() {
		parent.canSelect=true;
		
		parent.datasetObj.indexNo = $("#form1 input[name=indexNo]").val();
		parent.datasetObj.dataDate = $("#form1 input[name=dataDate]").val();
		parent.datasetObj.orgNo = orgNo;
		parent.datasetObj.currency = $("#form1 input[name=currency]").val();
	    parent.datasetObj.currencyId = currency;
	    parent.datasetObj.indexVal = $("#form1 input[name=indexVal]").val();
	    parent.datasetObj.saveData = f_save;
		parent.next(editFlag);
	}
    
	function f_save(){
		BIONE.submitForm($("#form1"), function() {
			window.parent.closeDsetBox(editFlag);
			BIONE.tip("修改成功");
			}, function() { 
				window.parent.closeDsetBox(editFlag);
				BIONE.tip("修改失败");
		}); 
	}
	
   //动态设置Form表单的action
    function setUrl(){    
    	$("#form1").attr("action", "${ctx}/rpt/frame/idx/planval/modify?orgNo="+orgNo+"&currencyId="+currency ); 
   }
    
</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" id="form1" method="post" 
			action="tmpAction" ></form>
	</div>
</body>
</html>