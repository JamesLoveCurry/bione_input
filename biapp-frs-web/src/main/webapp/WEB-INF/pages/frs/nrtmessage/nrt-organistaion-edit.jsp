<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    
    var id = "${id}"
    
    	 // 校验是否包含大写字母
        jQuery.validator.addMethod("checkUppercase", function(value, element) {
        	var uppercaseReg = /^[A-Z]+$/;
        	return this.optional(element) || (uppercaseReg.test(value));
        }, "必须大写字母.");
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
    	    	display : '机构英文名',
				name : 'organisationNameEn',
				newline : false,
				type : "text", 
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	display : '机构中文名',
				name : 'organisationNameCn',
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
    	    	display : '机构电话',
				name : 'phoneNo',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : false,
	    		    maxlength : 200
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
    		BIONE.closeDialog("organFlowEidtWin", null);
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
    		    url : "${ctx}/frs/nrtmessage/getOrganFlowById?id=${id}"
    		});
    	}
    }
    
  	//保存
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("organFlowEidtWin", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("organFlowEidtWin", "添加失败");
		});
    }
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/nrtmessage/saveOrganFlow"></form>
	</div>
</body>
</html>