<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    
    var id = "${id}"
    
      // 校验是否大写字母
     jQuery.validator.addMethod("checkUppercase1", function(value, element) {
        	var uppercaseReg = /^[A-Z]+$/;
        	return this.optional(element) || (uppercaseReg.test(value));
        }, "必须大写字母.");
    
    
    // 校验姓名（中文、<大写英文、空格>）
    jQuery.validator.addMethod("checkUppercase", function(value, element) {
    	var uppercaseReg = /^[A-Z\u4e00-\u9fa5\ ]{1,60}$/;
    	return this.optional(element) || (uppercaseReg.test(value));
    }, "英文名称全部使用半角大写字母");   
    
    
    // 校验日期
    jQuery.validator.addMethod("checkBirthdate", function(value, element) {
    	var birthdateReg = /^(\d{4})-(0\d{1}|1[0-2])-(0\d{1}|[12]\d{1}|3[01])$/;
    	return this.optional(element) || (birthdateReg.test(value));
    }, "YYYY-MM-DD");
    
    // 校验身份证号
    jQuery.validator.addMethod("ckrlx", function(value, element) {
    	   
    	   var len = value.length;   	   
    	  var name= $("#mainform [name='depositName']").val();
    	return this.optional(element) || ((len==18||len==15)&&/^\d+$/.test(value));
    }, "一代身份证:15位或18位；二代身份证:18位");
    
     
    //发证机关所在地的地区代码
    jQuery.validator.addMethod("fzqdm", function(value, element) {
    	   
    	   var len = value.length;   	   
    	return this.optional(element) || ((len==4)&&/^\d+$/.test(value));
    }, "地的地区代码长度:4位");
    
    
    //金融机构代码(字母和数字)
    jQuery.validator.addMethod("jrjgdm", function(value, element) {
    	   
    	   var len = value.length;   	   
    	return this.optional(element) || ((len==14)&&/^[A-Za-z]\d+$/.test(value));
    }, "只能是字母和数字；编码长度:14位");
    
    
  //数字
    jQuery.validator.addMethod("shnum", function(value, element) {
    	   
    	   var len = value.length;   	   
    	return this.optional(element) || /^\d+$/.test(value);
    }, "只能是数字");
    
    //身份证类型相关
    jQuery.validator.addMethod("sfzlx", function(value, element) {
    	   
    	var dtyp= $("#mainform [name='depositIdentityTypeCd']").val();  
    	var dcla=  $("#mainform [name='depositClassCd']").val();
    	if((dcla==01)){
    		
    		alert(dcla);
    		
    	}else{
    		return false;
    	}
    	    
    	   
    	return this.optional(element)||dcla.test(value);
    }, "");   
  
  
  
  
    $(function(){
    	initForm();
    	initFormData();
    	checkUnion();
    });
    
    function initForm() {
    	$("#mainform").ligerForm({
    	    fields : [{
	    		group : "信息",
	    		groupicon : groupicon	
    	    },{
				display : '数据日期',
				name : 'statisticsDt',
				newline : false,
				type : 'date',
				cssClass:"field",
				options:{
					format:'yyyyMMdd'
				},
				validate : {
	    		    required : false,
	    		    maxlength : 10
	    		}
			},{
				display : "文件名",
				name : "fileName",
				newline : false,
				type : "text",
				validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
			},{
				display : "模块名称",
				name : "moduleName",
				newline : false,
				type : "select",
				options : { data : [ { text : '-请选择-', id : ""}, { text : '1104', id : "01"}, { text : '大集中', id : "02"},
				                     { text : '利率报备', id : "03"}, { text : '支付报送', id : "04"}, { text : 'EAST', id : "05"},
				                     { text : '金标', id : "06"}, { text : '个人账户', id : "07"}, { text : '客户风险', id : "08"}, 
				                     { text : '本地特色', id : "09"}],cancelable  : true},
				validate : {
	    		    required : false,
	    		    maxlength : 20
	    		    
	    		}
			},{
				display : "批次",
				name : "batch",
				newline : false,
				type : "text",
				validate : {
	    		    required : false,
	    		    maxlength : 30
	    		}
			},{
	    		name : 'recordId',
	    		type : 'hidden'
    	    }]
	});


    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));

    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("hisFlowEidtWin", null);
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
    		    url : "${ctx}/frs/nrtmessage/getHistFlowById?id=${id}"
    		});
    	}
    }
    
  	//保存
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("hisFlowEidtWin", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("perFlowEidtWin", "添加失败");
		});
    }
  	
  

    
  	
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/nrtmessage/saveHistFlow"></form>
	</div>
</body>
</html>