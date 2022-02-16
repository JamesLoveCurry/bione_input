<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    
    var id = "${id}"
    
    $(function(){
    	initForm();
    	initFormData();
    });
    
    function initForm() {
    	$("#mainform").ligerForm({
    	    fields : [{
	    		group : "业务信息",
	    		groupicon : groupicon,
	    		display : '流向类型',
	    		name : 'flowType',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
	    		display : '流出代码',
	    		name : 'outCode',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
	    		display : '流出名称',
	    		name : 'outNm',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
	    		display : '流入代码',
	    		name : 'inCode',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
	    		display : '流入名称',
	    		name : 'inNm',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	group : "配置信息",
	    		groupicon : groupicon,
	    		display : '报表编号',
	    		name : 'rptNum',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    } ,{
	    		display : '笔数单元格',
	    		name : 'amountCellNum',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
	    		display : '金额单元格',
	    		name : 'balCellNum',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    maxlength : 32
	    		}
    	    },{
	    		name : 'flowId',
	    		type : 'hidden'
    	    } ]
    	});

    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));

    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("pisaFlowEidtWin", null);
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
    		BIONE.loadForm(mainform, {
    		    url : "${ctx}/frs/pisamessage/getPisaFlowById?id=${id}"
    		});
    	}
    }
    
  	//保存
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("pisaFlowEidtWin", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("pisaFlowEidtWin", "添加失败");
		});
    }
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/pisamessage/savePisaFlow"></form>
	</div>
</body>
</html>