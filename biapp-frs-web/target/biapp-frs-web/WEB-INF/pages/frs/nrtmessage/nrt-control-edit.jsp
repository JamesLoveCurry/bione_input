<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    
    var id = "${id}"
    
    	 // 校验是否大写字母
        jQuery.validator.addMethod("checkUppercase", function(value, element) {
        	var uppercaseReg = /^[A-Z]+$/;
        	return this.optional(element) || (uppercaseReg.test(value));
        }, "必须大写字母.");
    
    // 校验
    jQuery.validator.addMethod("checkBirthdate", function(value, element) {
    	var birthdateReg = /^(\d{4})-(0\d{1}|1[0-2])-(0\d{1}|[12]\d{1}|3[01])$/;
    	return this.optional(element) || (birthdateReg.test(value));
    }, "YYYY-MM-DD");
    $(function(){
    	initForm();
    	initFormData();
    });
    
    function initForm() {
    	$("#mainform").ligerForm({
    	    fields : [{
	    		group : "业务信息",
	    		groupicon : groupicon	
    	    },{
    	    	display : "账号",
				name : "accountNumber",
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 22
	    		}
    	    },{
    	    	display : "姓名类别",
				name : "nameType",
				newline : false,
				type : "select", 
				options : { data : [ { text : '亦称作', id : "OECD205"}, { text : '虚拟名称', id : "OECD206"}, { text : '法定名称', id : "OECD207"} ], cancelable  : true},
	    		validate : {
	    		    required : true,
	    		    maxlength : 12
	    		}
    	    },{
    	    	display : '法定英文（拼音）姓',
				name : 'lastName',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '英文中间名',
				name : 'middleName',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },
    	    {
    	    	display : '法定英文（拼音）名',
				name : 'firstName',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '中文姓名',
				name : 'nameCn',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '曾用名',
				name : 'precedingTitle',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '称谓',
				name : 'titles',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '别名',
				name : 'namePrefix',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '标签名',
				name : 'generationIdentifier',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '后缀',
				name : 'suffix',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '常规后缀',
				name : 'generalSuffix',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '控制人类型',
				name : 'ctrlgPersonType',
				newline : false,
				type : "select", 
				options : { data : [ { text : '法人控制人-所有权', id : "CRS801"}, { text : '法人控制人-其他', id : "CRS802"}, 
				                     { text : '法人控制人-高管人员', id : "CRS803"} ,{ text : '信托-委托人', id : "CRS804"},
				                     { text : '信托-受托人', id : "CRS805"},{ text : '信托-监察人', id : "CRS806"},
				                     { text : '信托-受益人', id : "CRS807"},{ text : '信托-其他控制人', id : "CRS808"},
				                     { text : '其他-等同于委托人', id : "CRS809"},{ text : '其他-等同于受托人', id : "CRS810"},
				                     { text : '其他-等同于监察人', id : "CRS811"},{ text : '其他-等同于受益人', id : "CRS812"},{ text : '其他-等同于其他控制人', id : "CRS813"}], cancelable  : true},
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '国籍',
				name : 'nationAlity',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '地址类型',
				name : 'legalAddressType',
				newline : false,
				type : "select", 
				options : { data : [ { text : '居住地址或办公地址', id : "OECD301"}, 
				                     { text : '居住地址', id : "OECD302"}, 
				                     { text : '办公地址', id : "OECD303"},
				                     { text : '注册地址', id : "OECD304"},
				                     { text : '其他', id : "OECD305"} ], cancelable  : true},
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '国家代码',
				name : 'countryCode',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 3,
	    		    checkUppercase:true
	    		}
    	    },{
    	    	display : '英文详细地址',
				name : 'addressFreeEn',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 500
	    		}
    	    },{
    	    	display : '所在城市',
				name : 'cityEn',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '街道',
				name : 'street',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 100
	    		}
    	    },{
    	    	display : '楼号',
				name : 'buildingIdentifier',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '房门号',
				name : 'suiteIdentifier',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '楼层',
				name : 'floorIdentifier',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '区',
				name : 'districtName',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '邮箱',
				name : 'pOB',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '邮编',
				name : 'postCode',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '省/自治区/直辖市',
				name : 'countrySubentity',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '中文详细地址',
				name : 'addressFreeCn',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '省级行政区划代码',
				name : 'province',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 6,
	    		    digits:true
	    		}
    	    },{
    	    	display : '地市级行政区划代码',
				name : 'cityCn',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '县级行政区划代码',
				name : 'districtName1',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '邮编CN',
				name : 'postCode1',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : false,
	    		    maxlength : 32
	    		}
    	    },{
    	    	display : '税收居民国（地区）代码',
				name : 'rescountryCode',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 3,
	    		    checkUppercase:true
	    		}
    	    }
    	    ,{
    	    	display : '纳税人识别号',
				name : 'tin',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
				display : '发放识别号的国家（地区）代码',
				name : 'issuedBy',
				newline : false,
				type : 'text',
				validate : {
	    		    required : false,
	    		    maxlength : 3,
	    		    checkUppercase:true
	    		}
			},{
    	    	display : '未能获取纳税人识别号的原因',
				name : 'explanation',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
    	    },{
				display : '出生日期',
				name : 'birthDate',
				newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : false,
	    		    maxlength : 10,
	    		    checkBirthdate:true
	    		}
			},{
				display : '出生城市',
				name : 'city',
				newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		    
	    		}
			},{
				display : '出生国代码',
				name : 'countryCode1',
				newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : false,
	    		    maxlength : 3,
	    		    checkUppercase:true
	    		}
			},{
				display : '其他出生国英文',
				name : 'formerCountryName',
				newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
	    		}
			},{
	    		name : 'recordId',
	    		type : 'hidden'
    	    } ]
    	});

    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));

    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("contrFlowEidtWin", null);
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
    		    url : "${ctx}/frs/nrtmessage/getContrFlowById?id=${id}"
    		});
    	}
    }
    
  	//保存
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("contrFlowEidtWin", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("contrFlowEidtWin", "添加失败");
		});
    }
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/nrtmessage/saveContrFlow"></form>
	</div>
</body>
</html>