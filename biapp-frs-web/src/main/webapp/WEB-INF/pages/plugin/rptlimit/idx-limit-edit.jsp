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
	var orgNo = '${orgNo}';

	
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
				name : "orgNo",
				type : "hidden"
			},{
				display : "指标名称",
				name : 'indexNm',
				newline : true,
				type : 'text',
				attr: {
					readonly : true
				}
		    },{
		    	display : '机构名称',
				name : 'orgNm',
				newline : false,
				type : 'text',
				attr: {
					readonly : true
				}
			},{
				display : '最高界限',
				name : 'upperLimit',
				newline : true,
				type : 'text',
				validate : {
			   		required : false,
			   		numberReg : true
				}
			},{
				display : '最低界限',
				name : 'lowerLimit',
				newline : false,
				type : 'text',
				validate : {
			   		required : false,
			   		numberReg : true
				}
			},{
				display : '警告界限',
				name : 'warningLimit',
				newline : true,
				type : 'text',
				validate : {
			   		required : false,
			   		numberReg : true
				}
			},{
				display : '警告模式',
				name : 'warningMode',
				newline : false,
				type : 'select',
				options : {
				    initValue : '0',
				    data : [ {
						text : '大于',
						id : "0"
				    }, {
						text : '小于',
						id : '1'
				    } ]
				}
		    } ]
		});
	
		/* BIONE.loadForm(mainform, {
	 	    url : "${ctx}/rpt/frame/idx/limit/getLimitInfoByParams?indexNo=" + indexNo 
		    		+ "&indexVerId=" + indexVerId + "&orgNo=" + orgNo
		}); */
		
		$.ajax({
			type : "GET",
			url : "${ctx}/rpt/frame/idx/limit/getLimitInfoByParams?indexNo=" + indexNo 
	    		+ "&indexVerId=" + indexVerId + "&orgNo=" + orgNo,
	    	dataType : "json" ,
	    	success: function(data){
	    		$("#form1 input[name=indexNo]").val(data.indexNo);
	    		$("#form1 input[name=indexVerId]").val(data.indexVerId);
	    		$("#form1 input[name=indexNm]").val(data.indexNm);
	    		$("#form1 input[name=upperLimit]").val(data.upperLimit);
	    		$("#form1 input[name=lowerLimit]").val(data.lowerLimit);
	    		$("#form1 input[name=warningLimit]").val(data.warningLimit);
	    		$("#form1 input[name=warningMode]").val(data.warningMode);
	    		$("#form1 input[name=orgNm]").val(data.orgNm);
	    		$("#form1 input[name=orgNo]").val(data.orgNo);
	    	}
		})
		
		$("#form1 input[name=indexNm]").attr("readonly", "true").removeAttr("validate");	
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
			BIONE.closeDialog("rptIdxLimitModifyWin");
		    }
		});
		buttons.push({
		    text : '修改',
		    onclick : f_save
		});
		BIONE.addFormButtons(buttons);
		
    });
    

    
	function f_save() {
		BIONE.submitForm($("#form1"), function() {
		    BIONE.closeDialogAndReloadParent("rptIdxLimitModifyWin", "maingrid",
			    "修改成功");
		}, function() {
		    BIONE.closeDialog("rptIdxLimitModifyWin", "修改失败");
		});
	}
    
   //动态设置Form表单的action
    function setUrl(){    
    	$("#form1").attr("action", "${ctx}/rpt/frame/idx/limit/modify"); 
   }
    
</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" id="form1" method="post" 
			action="${ctx}/rpt/frame/idx/limit/modify" ></form>
	</div>
</body>
</html>