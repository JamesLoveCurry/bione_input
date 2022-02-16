<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">

<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    var id = "${id}";
    // 1-修改 2-查看 3-复核 4-审核
    var type = "${type}";
    // 1-对私 2-对公
    var accType = "${accType}";
    
    // 判断字符串是否为空
    function isNull(value){
    	if(value == null || value == '' || typeof value == 'undefined'){
    		return true;
    	}
    	return false;
    }
    
		// ===============基本信息校验=====================
		//判断小数点后两位
	   	jQuery.validator.addMethod("minNumber",function(value,element){
	       	var returnVal=true; 
	       	// 判断包含小数点
	       	if(!value.indexOf(".")){
	       		return false;
	       	}
	       	var arrMen=value.split(".");
	       	if(arrMen.length==2){
	       		 if(arrMen[1].length>2){
	       			 returnVal=false;
	       			 return false;
	       		 }
	       	 }
	       	 return returnVal; 
	       },"小数点最多为两位");
	    
	    //判断是否是大写字母
	   	jQuery.validator.addMethod("isAllCaps",function(value,element){
	      	var returnVal=true; 
	      	for(var i=0;i<value.length;i++){
	      		var c=value.charAt(i);
	      		if(c>'A'&c<'Z'){
	      			return true;
	      		}
	      		return false;
	      	} 
	      },"只能是大写字母"); 
	    
	    // 固定值CRS
	   	jQuery.validator.addMethod("equalValueToCRS",function(value,element){
	      	if(value == '' || value == null || typeof value == 'undefined'){
	      		return false;
	      	}
	      	if(value != 'CRS'){
	      		return false
	      	}
	      	return true;
	      },"固定值为CRS");
		
		// ================对私信息校验==============
		
		// =============对公信息校验=====================
		// 校验是否包含大写字母
        jQuery.validator.addMethod("checkUppercase", function(value, element) {
        	var uppercaseReg = /^[A-Z]+$/;
        	return this.optional(element) || (uppercaseReg.test(value));
        }, "必须大写字母.");
		
		// 控制人信息校验
        // 校验生日
        jQuery.validator.addMethod("checkBirthdate", function(value, element) {
        	var birthdateReg = /^(\d{4})-(0\d{1}|1[0-2])-(0\d{1}|[12]\d{1}|3[01])$/;
        	return this.optional(element) || (birthdateReg.test(value));
        }, "YYYY-MM-DD");
	
	// 添加页面按钮
	var buttons = [];
	
	// 1-修改 2-查看 3-复核 4-审核
	if(!isNull(type) && type == '1'){
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    			BIONE.closeDialog("nrtAcctInfoDealWin", null);
    	    }
    	});
    	buttons.push({
    	    text : '保存',
    	    onclick : f_save
    	});
	}
	
	// 查看
	if(!isNull(type) && type == '2'){
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    			BIONE.closeDialog("nrtAcctInfoDealWin", null);
    	    }
    	});
	}
	
	// 复核按钮
	if(!isNull(type) && type == '3'){
    	buttons.push({
	   	    text : '取消',
	   	    onclick : function() {
	   			BIONE.closeDialog("nrtAcctInfoDealWin", null);
	   	    }
	   	});
    	buttons.push({
    	    text : '复核通过',
    	    onclick : function() {
    	    	$.ajax({
					async : false,
					type : "post",
					url : '${ctx}/frs/nrtmessage/reviewNrtAcctInfo?&id=' + id + "&result=1",
					success : function(res){
						BIONE.tip('复核成功');
						BIONE.closeDialogAndReloadParent("nrtAcctInfoDealWin", "maingrid", "复核成功");
					},
					error : function(e){
						BIONE.tip('复核失败');
					}
				});
    	    }
    	});
    	buttons.push({
    	    text : '复核退回',
    	    onclick : function() {
    	    	$.ajax({
					async : false,
					type : "post",
					url : '${ctx}/frs/nrtmessage/reviewNrtAcctInfo?&id=' + id + "&result=0",
					success : function(res){
						BIONE.tip('复核成功');
						BIONE.closeDialogAndReloadParent("nrtAcctInfoDealWin", "maingrid", "复核成功");
					},
					error : function(e){
						BIONE.tip('复核失败');
					}
				});
    	    }
    	});
	}
	
	// 审核按钮
	if(!isNull(type) && type == '4'){
	   	buttons.push({
	   	    text : '取消',
	   	    onclick : function() {
	   			BIONE.closeDialog("nrtAcctInfoDealWin", null);
	   	    }
	   	});
    	buttons.push({
    	    text : '审核通过',
    	    onclick : function() {
    	    	$.ajax({
					async : false,
					type : "post",
					url : '${ctx}/frs/nrtmessage/assessNrtAcctInfo?&id=' + id + "&result=1",
					success : function(res){
						BIONE.tip('审核成功');
						BIONE.closeDialogAndReloadParent("nrtAcctInfoDealWin", "maingrid", "处理成功");
					},
					error : function(e){
						BIONE.tip('审核失败');
					}
				});
    	    }
    	});
    	buttons.push({
    	    text : '审核退回',
    	    onclick : function() {
    	    	$.ajax({
					async : false,
					type : "post",
					url : '${ctx}/frs/nrtmessage/assessNrtAcctInfo?&id=' + id + "&result=0",
					success : function(res){
						BIONE.tip('审核成功');
						BIONE.closeDialogAndReloadParent("nrtAcctInfoDealWin", "maingrid", "处理成功");
					},
					error : function(e){
						BIONE.tip('审核失败');
					}
				});
    	    }
    	});
	}
	
	// 对私
    function initForm1() {
    	// 对私账户信息
       	var fields1 = [{
    		group : "账户基本信息",
    		groupicon : groupicon	
        },{
    		display : '账号',
    		name : 'accountNumber',
    		newline : true,
    		type : 'text', 
            validate : { required : true, maxlength : 32 }
    	},  {
    		display : '数据日期',
    		name : 'statisticsDt',
    		newline : false,
    		type : 'date', 
    		format:'yyyy-MM-dd',
            validate : { required : true, maxlength : 10 }
    	},  {
    		display : '账户类别',
    		name : 'dueDiligenceInd',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'N', 'text' : '新开账户' }, 
    							 { 'id' : 'P', 'text' : '存量账户'}]},
            validate : {required : true}
    	},  {
    		display : '系统用户账号',
    		name : 'reportingId',
    		newline : false,
    		type : 'text', 
            validate : { 
            	required : true,
            	minlength : 14,
            	maxlength : 14 }
    	},  {
    		display : '账户是否已注销',
    		name : 'closedAccount',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'true', 'text' : '账户已注销' }, 
    							 { 'id' : 'false', 'text' : '正常户'}]},
            validate : { required : true}
    	},  {
    		display : '是否取得账户持有人的自证声明',
    		name : 'selfCertification',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'true', 'text' : '取得自证声明' }, 
    							 { 'id' : 'false', 'text' : '未取得自证声明'}]},
            validate : {required : true}
    	},  {
    		display : '货币代码（余额）',
    		name : 'currCode',
    		newline : false,
    		type : 'text', 
            validate : { required : true, maxlength : 30 }
    	},  {
    		display : '账户余额', 
    		name : 'accountBalance',
    		newline : false,    
    		type : 'text', 
            validate : { 
            	required : true,
            	min:0.00,
            	number:true, 
            	minNumber: 'accountBalance',
            	maxlength : 100,
            	messages : {
    				remote : "请输入数字，小数点后两位"
    			} 
    		}
    	},  {
    		display : '账户持有人类别',
    		name : 'accountHolderType',
    		newline : false,
    		type : 'select',  
    		options : {data : [  { 'id' : 'CRS100', 'text' : '非居民个人' }, 
    		                     { 'id' : 'CRS101', 'text' : '有非居民控制人的消极非金融机构' }, 
    		                     { 'id' : 'CRS102', 'text' : '非居民机构，不包括消极非金融机构' }, 
    							 { 'id' : 'CRS103', 'text' : '非居民消极非金融机构，但没有非居民控制人'}]},
            validate : { required : true}
    	},  {
    		display : '开户金融机构',
    		name : 'openingFIName',
    		newline : false,
    		type : 'text', 
            validate : { required : true, minlength:14,maxlength : 14 }
    	},  {
    		display : '金融机构注册码',
    		group : "配置信息",
    		groupicon : groupicon,
    		name : 'fiId',
    		newline : true,
    		type : 'text', 
            validate : { required : true, minlength:14, maxlength : 14 }
    	},  {
    		display : '收入类型',
    		name : 'paymentType',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'CRS501', 'text' : '股息' }, 
    		                     { 'id' : 'CRS502', 'text' : '利息' }, 
    		                     { 'id' : 'CRS503', 'text' : '销售或者赎回总收入' }, 
    							 { 'id' : 'CRS504', 'text' : '其他'}]},
            validate : { required : true}
    	},  {
    		display : '货币代码（收入）',
    		name : 'currCodes',
    		newline : false,
    		type : 'text', 
            validate : { required : true, maxlength : 30 }
    	},  {
    		display : '收入金额',
    		name : 'paymentAmnt',
    		newline : false,
    		type : 'text', 
            validate : { required : true,
            	         min:0.00,
            	         number:true, 
            	         minNumber: 'paymentAmnt', 
            	         maxlength : 32 }
    	},  {
    		display : '报告类别',
    		name : 'reportingType',
    		newline : false,
    		type : 'select', 
    		options : {
    			initValue : "CRS",
    			data : [ 
    			          
    			          { text : 'CRS', id : "CRS"} 
    			           ]}, 
            validate : { 
            	required : true, 
            	//maxlength : 3, 
            	equalValueToCRS : 'reportingType'}
    	},  {
    		display : '报告唯一编码',
    		name : 'messageRefId',
    		newline : false,
    		type : 'text'
    	},  {
    		display : '报告年度',
    		name : 'reportingPeriod',
    		//width : '10%',
    		type : 'date',
    		format:'yyyy-MM-dd',
            validate : { required : true, maxlength : 15 }
    	}, 	{
    		display : '报告类型',
    		name : 'messageTypeIndic',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'CRS701', 'text' : '新数据' }, 
    		                     { 'id' : 'CRS702', 'text' : '修改和删除数据' }, 
    		                     { 'id' : 'CRS703', 'text' : '无数据申报' }  
    							 ]},
            validate : {required : true}
    	},  {
    		display : '报告生成时间戳', //YYYY-MM-DDTHH:mm:ss
    		name : 'tmstp',
    		newline : false,
    		type : 'date' ,
    	    format:'YYYY-MM-DDTHH:mm:ss',
    	    validate : {maxlength : 32 }
             
    	},  {
    		display : '账户记录编号',
    		name : 'docRefId',
    		newline : false,
    		type : 'text', 
            validate : { required : false, maxlength : 29 }
    	},  {
    		display : '被修改或被删除的账户记录编号',
    		name : 'corrDocRefId',
    		newline : false,
    		type : 'text', 
            validate : { required : false, maxlength : 29 }
    	},  {
    		display : '账户报告的类型',
    		name : 'docTypeIndic',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'R1', 'text' : '新账户记录' }, 
    		                     { 'id' : 'R2', 'text' : '修改账户记录' },  
    							 { 'id' : 'R3', 'text' : '删除账户记录'}]},
            validate : { required : true}
    	},  {
    		display : '错误标识',
    		name : 'errorFlag',
    		newline : false,
    		type : 'text', 
            validate : { required : false, maxlength : 32 }
    	},  {
    		display : '错误代码',
    		name : 'errorCode',
    		newline : false,
    		type : 'text', 
            validate : { required : false, maxlength : 32 }
    	},{
    		group : "持有人对私信息",
    		groupicon : groupicon	
        } ,{
    		display : '账号',
    		name : 'iAccountNumber', 
    		newline : true, 
    		type : 'text',
    		validate : { required : true, maxlength : 32 }
    	},  {
    		display : '姓名类型',
    		
    		name : 'iNameType', 
    		newline : false,
    		type : 'select',
    		options : {data : [  { 'id' : 'OECD202', 'text' : '个人姓名' }, 
    		                     { 'id' : 'OECD203', 'text' : '别名' }, 
    		                     { 'id' : 'OECD204', 'text' : '昵称' }, 
    		                     { 'id' : 'OECD205', 'text' : '亦称作（aka）' }, 
    							 { 'id' : 'OECD208', 'text' : '出生姓名'}]},
           validate : { required : false, maxlength : 8 }
    	},  {
    		display : '英文中间名', 
    		name : 'iMiddleName',
    		newline : false,
    		type : 'text',
           validate : { required : true, maxlength : 100 }
    	},  {
    		display : '法定英文（拼音）姓',
    		name : 'iLastName',
    		newline : false,
    		type : 'text',
           validate : { required : true, maxlength : 100 }
    	} ,  {
    		display : '法定英文（拼音）名',
    		/* 法定英文（拼音）名。身份证件只有中文的，填写拼音。
    		如果账户持有人的法定姓名是一个单名，不区分姓和名，则在FirstName中填写“NFN”，在LastName中填写该单名。 */
    		name : 'iFirstName',
    		newline : false,
    		type : 'text',
           validate : { required : true, maxlength : 200 }
    	} ,  {
    		display : '中文姓名', 
    		name : 'iNameCn',
    		newline : false,
    		type : 'text',
           validate : { required : true, maxlength : 100 }
    	} ,  {
    		display : '曾用名',
    		name : 'iPreceDingTitle',
    		newline : false,
    		type : 'text',
           validate : { required : false, maxlength : 200 }
    	} ,  {
    		display : '别名',
    		name : 'iTitles',
    		newline : false,
    		type : 'text',
           validate : { required : false, maxlength : 200 }
    	} ,  {
    		display : '别名1',
    		name : 'iNamePreFix',
    		newline : false,
    		type : 'text',
           validate : { required : false, maxlength : 200 }
    	},  {
    		display : '标签名',
    		name : 'iGenerationIdentifier',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 200 }
    	} ,  {
    		display : '后缀',
    		name : 'iSuffix',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 200 }
    	} ,  {
    		display : '后缀1',
    		name : 'iGeneralSuffix',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 200 }
    	} ,  {
    		display : '性别',
    		name : 'iGender',
    		newline : false,
    		type : 'select',
    		options : {data : [  { 'id' : 'M', 'text' : '男' },
    		                     { 'id' : 'F', 'text' : '女' },
    							 { 'id' : 'P', 'text' : '未说明性别'}]},
            validate : { required : true, maxlength : 30 }
    	} ,  {
    		display : '国籍',
    		name : 'iNationAlity',
    		newline : false,
    		type : 'text',
            validate : { required : true,  maxlength : 3 }
    	},  {
    		display : '出生日期',
    		name : 'iBirthDate',
    		newline : false,
    		type : 'date',
    		format:'yyyy-MM-dd',
            validate : { required : true, maxlength : 10 }
    	} ,  {
    		display : '出生城市',
    		name : 'iCity',
    		newline : false,
    		type : 'text',
            validate : { required : true, maxlength : 200 }
    	} ,  {
    		display : '出生国代码',
    		name : 'iCountryCode1',
    		newline : false,
    		type : 'text',
            validate : { required : true, maxlength : 3 }
    	} ,  {
    		display : '其他出生国代码',
    		name : 'iFormerCountryName',
    		newline : false,
    		/* 出生国不在标准定义范围内，无法填写代码的，在本字段中填写出生国英文名称。
    */
    		type : 'text',
            validate : { required : true, maxlength : 200 }
    	},  {
    		display : '联系电话',
    		name : 'iPhoneNo',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 20 }
    	},  {
    		display : 'Address的属性',
    		name : 'iLegalAddressType',
    		newline : false,
    		type : 'select',
    		options : {data : [  { 'id' : 'OECD301', 'text' : '居住地址或办公地址' }, 
    		                     { 'id' : 'OECD302', 'text' : '居住地址' }, 
    		                     { 'id' : 'OECD303', 'text' : '办公地址' }, 
    							 { 'id' : 'OECD304', 'text' : '注册地址'},
    							 { 'id' : 'OECD305', 'text' : '其他'}]},
            validate : { required : false, maxlength : 10 }
    	} ,  {
    		display : '国家代码',
    		name : 'iCountryCode',
    		newline : false,
    		type : 'text',
            validate : { required : true,  maxlength : 3 }
    	} ,  {
    		display : '英文详细地址',
    		name : 'iAddressFreeEn',
    		newline : false,
    		type : 'text',
            validate : { required : true, maxlength : 500 }
    	} ,  {
    		display : '所在城市',
    		name : 'iCityEn',
    		newline : false, 
    		/* 所在城市。中国境内地址填写直辖市、地级市或县级市拼音，境外地址填写英文。
    		如AddressFixEN为强制、则CityEN为强制；
    		如AddressFixEN为可选、则CityEN为可选。 */
    		type : 'text',
            validate : { required : true, maxlength : 200 }
    	},  {
    		display : '楼号',
    		name : 'iBuildingIdentifier',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 100 }
    	} ,  {
    		display : '房门号',
    		name : 'iSuiteIdentifier',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 100 }
    	}  ,  {
    		display : '街道',
    		name : 'iStreet',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 200 }
    	} ,  {
    		display : '楼层',
    		name : 'iFloorIdentifier',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 100 }
    	} ,  {
    		display : '县级行政区划代码',
    		name : 'iDistrictName',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 200 }
    	} ,  {
    		display : '邮箱',
    		name : 'iPOB',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 50 }
    	} ,  {
    		display : '邮编',
    		name : 'iPostCode',
    		newline : false,
    		type : 'text',
            validate : { required : true, maxlength : 50 }
    	} ,  {
    		/* group : "PART2",                         //--------------分割线---------------------------
    		groupicon : groupicon, */
    		display : '中文详细地址',
    		name : 'iAddressFreeCn',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 500 }
    	} ,  {
    		display : '省级行政区划代码',
    		name : 'iProvince',
    		newline : false,
    		type : 'text',
            validate : { required : true, maxlength : 6, minlength : 6}
    	} ,  {
    		display : '地市级行政区划代码',
    		name : 'iCityCn',
    		newline : false,
    		type : 'text',
            validate : { required : true, maxlength : 6 }
    	} ,  {
    		display : '县级行政区划代码1',
    		name : 'iDistrictName1',
    		newline : false,
    		type : 'text',
            validate : { required : true, maxlength : 200 }
    	} ,  {
    		display : '邮编1',
    		name : 'iPostCode1',
    		newline : false,
    		type : 'text',
            validate : { required : true, maxlength : 50 }
    	}  ,  {
    		display : '身份证件类型',
    		name : 'iIdType',
    		newline : false,
    		type : 'select',
    		options : {data : [  { 'id' : 'ACC01', 'text' : '第一代居民身份证' }, 
    		                     { 'id' : 'ACC02', 'text' : '第二代居民身份证' }, 
    		                     { 'id' : 'ACC03', 'text' : '临时身份证' }, 
    							 { 'id' : 'ACC04', 'text' : '中国护照'},
    							 { 'id' : 'ACC05', 'text' : '户口簿' }, 
    		                     { 'id' : 'ACC06', 'text' : '村民委员会证明' }, 
    		                     { 'id' : 'ACC07', 'text' : '学生证' }, 
    							 { 'id' : 'ACC15', 'text' : '港澳居民来往内地通行证'},
    							 { 'id' : 'ACC16', 'text' : '台湾居民来往大陆通行证' }, 
    		                     { 'id' : 'ACC17', 'text' : '外国人永久居留证' }, 
    		                     { 'id' : 'ACC18', 'text' : '边民出入境通行证' }, 
    							 { 'id' : 'ACC19', 'text' : '外国护照'},
    							 { 'id' : 'ACC20', 'text' : '其他'}]},
            validate : { required : true, maxlength : 50 }
    	} ,  {
    		display : '身份证件号码',
    		name : 'iIdNumber',
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 30 }
    	} ,  {
    		display : '识别号',
    		name : 'iTin',
    		newline : false,
    		type : 'text',
    		/* 该元素表示纳税人识别号。 */
            validate : { required : false, maxlength : 200 }
    	},  {
    		display : '识别号类型',
    		name : 'iInType',
    		/* 属性inType固定填写“TIN”。 */
    		options : {data : [  { 'id' : 'TIN', 'text' : 'TIN' }  
    		                     ]},
    		newline : false,
    		type : 'text',
            validate : { required : false, maxlength : 3 }
    	},  {
    		display : '识别号1',
    		name : 'iTin1',
    		newline : false,
    		/* 账户持有人在其税收居民国（地区）的纳税人识别号。账户持有人无税收居民国（地区）纳税人识别号的，银行无需收集并报送。
    		新开账户：账户持有人有税收居民国（地区）纳税人识别号的，必须收集报送。
    		存量账户：银行现有客户资料中没有居民国（地区）纳税人识别号的，无需报送。但是，金融机构应当在账户被认定为非居民账户的次年12月31日前，积极采取措施，获取该信息。
    		该元素为可重复元素，重复次数为0至无穷。 */
    		type : 'text',
            validate : { required : false, maxlength : 200 }
    	}
    	,  {
    		display : '税收居民国(地区)代码',
    		name : 'iRescountryCode',
    		newline : false,
    		type : 'text',
    		/* 格式：3位大写英文字母。 */
            validate : { 
            	required : true,  
            	isAllCaps:'rescountryCode', 
    		    maxlength : 3 
    		 }
    	  } , {
    			display : '发放识别号的国家(地区)代码',
    			name : 'iIssuedBy',
    			/* 3位大写英文字母。 可选*/ 
    			newline : false,
    			type : 'text',
                validate : { required : true, maxlength : 3 }
    		} ,  {
    			display : '识别号类型1',
    			name : 'iInType1',
    			 /* 属性inType固定填写“TIN” */
    			newline : false,
    			type : 'text',
                validate : { required : true, maxlength : 200 }
    		} ,  {
    			display : '发放识别号的国家(地区)代码1',
    			name : 'iIssuedBy1',
    			/* 3位大写英文字母。 可选*/
    			newline : false,
    			type : 'text',
                validate : { required : true, maxlength : 200 }
    		}  ,  {
    			display : '税收居民国(地区)代码1',
    			name : 'iRescountryCode1',
    			newline : false,
    			type : 'text',
    			/* 格式：3位大写英文字母。 */
                validate : { required : true, maxlength : 3 }
    		}  ,  {
    			display : '不能提供居民国/地区纳税人识别号的理由',
    			name : 'iExplanation',
    			/* 如果客户表示未能获取纳税人识别号，填报客户说明的具体原因，中英文均可。
    			如新增数据中没有填写TIN，则该项目为强制。 */
    			newline : false,
    			type : 'text',
                validate : { required : true, maxlength : 200 }
    		} ,  {
    			
    			display : '中国境内地址填写省/自治区/直辖市的拼音',
    			name : 'iCountrySubentity', 
    			newline : true,
    			type : 'text',
                validate : { required : false, maxlength : 200 }
    		} ,  {
    			display : '错误标识',
    			name : 'iErrorFlag',     
    			newline : false,
    			type : 'hidden'      
                
    		} ,  {
    			display : '错误代码',     
    			name : 'iErrorCode',
    			newline : false,
    			type : 'hidden' 
    		},{
        		name : 'recordId',
        		type : 'hidden'
            },{
        		name : 'iRecordId',
        		type : 'hidden'
            },{
        		//display : '数据日期',
        		name : 'iStatisticsDt',
        		type : 'hidden'
        	}];
    	
       	$("#mainform").ligerForm({
		    inputWidth : 170,
		    labelWidth : 150,
		    space : 40,
    	    fields : fields1
    	 });
    	    
        jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));
        
    	BIONE.addFormButtons(buttons);
    }
    
	// 对公
    function initForm2() {
    	// 对公账户信息
       	var fields2 = [{
    		group : "账户基本信息",
    		groupicon : groupicon	
        },{
    		display : '账号',
    		name : 'accountNumber',
    		newline : true,
    		type : 'text', 
            validate : { required : true, maxlength : 32 }
    	},  {
    		display : '数据日期',
    		name : 'statisticsDt',
    		newline : false,
    		type : 'date', 
    		format:'yyyy-MM-dd',
            validate : { required : true, maxlength : 10 }
    	},  {
    		display : '账户类别',
    		name : 'dueDiligenceInd',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'N', 'text' : '新开账户' }, 
    							 { 'id' : 'P', 'text' : '存量账户'}]},
            validate : {required : true}
    	},  {
    		display : '系统用户账号',
    		name : 'reportingId',
    		newline : false,
    		type : 'text', 
            validate : { 
            	required : true,
            	minlength : 14,
            	maxlength : 14 }
    	},  {
    		display : '账户是否已注销',
    		name : 'closedAccount',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'true', 'text' : '账户已注销' }, 
    							 { 'id' : 'false', 'text' : '正常户'}]},
            validate : { required : true}
    	},  {
    		display : '是否取得账户持有人的自证声明',
    		name : 'selfCertification',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'true', 'text' : '取得自证声明' }, 
    							 { 'id' : 'false', 'text' : '未取得自证声明'}]},
            validate : {required : true}
    	},  {
    		display : '货币代码（余额）',
    		name : 'currCode',
    		newline : false,
    		type : 'text', 
            validate : { required : true, maxlength : 30 }
    	},  {
    		display : '账户余额', 
    		name : 'accountBalance',
    		newline : false,    
    		type : 'text', 
            validate : { 
            	required : true,
            	min:0.00,
            	number:true, 
            	minNumber: 'accountBalance',
            	maxlength : 100,
            	messages : {
    				remote : "请输入数字，小数点后两位"
    			} 
    		}
    	},  {
    		display : '账户持有人类别',
    		name : 'accountHolderType',
    		newline : false,
    		type : 'select',  
    		options : {data : [  { 'id' : 'CRS100', 'text' : '非居民个人' }, 
    		                     { 'id' : 'CRS101', 'text' : '有非居民控制人的消极非金融机构' }, 
    		                     { 'id' : 'CRS102', 'text' : '非居民机构，不包括消极非金融机构' }, 
    							 { 'id' : 'CRS103', 'text' : '非居民消极非金融机构，但没有非居民控制人'}]},
            validate : { required : true}
    	},  {
    		display : '开户金融机构',
    		name : 'openingFIName',
    		newline : false,
    		type : 'text', 
            validate : { required : true, minlength:14,maxlength : 14 }
    	},  {
    		display : '金融机构注册码',
    		name : 'fiId',
    		newline : true,
    		type : 'text', 
            validate : { required : true, minlength:14, maxlength : 14 }
    	},  {
    		display : '收入类型',
    		name : 'paymentType',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'CRS501', 'text' : '股息' }, 
    		                     { 'id' : 'CRS502', 'text' : '利息' }, 
    		                     { 'id' : 'CRS503', 'text' : '销售或者赎回总收入' }, 
    							 { 'id' : 'CRS504', 'text' : '其他'}]},
            validate : { required : true}
    	},  {
    		display : '货币代码（收入）',
    		name : 'currCodes',
    		newline : false,
    		type : 'text', 
            validate : { required : true,/* isAllCaps:'currCodes', */ maxlength : 30 }
    	},  {
    		display : '收入金额',
    		name : 'paymentAmnt',
    		newline : false,
    		type : 'text', 
            validate : { required : true,
            	         min:0.00,
            	         number:true, 
            	         minNumber: 'paymentAmnt', 
            	         maxlength : 32 }
    	},  {
    		display : '报告类别',
    		name : 'reportingType',
    		newline : false,
    		type : 'text', 
            validate : { 
            	required : true, 
            	//maxlength : 3, 
            	equalValueToCRS : 'reportingType'}
    	},  {
    		display : '报告唯一编码',
    		name : 'messageRefId',
    		newline : false,
    		type : 'text', 
    	},  {
    		display : '报告年度',
    		name : 'reportingPeriod',
    		//width : '10%',
    		type : 'date',
    		format:'yyyy-MM-dd',
            validate : { required : true, maxlength : 15 }
    	}, 	{
    		display : '报告类型',
    		name : 'messageTypeIndic',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'CRS701', 'text' : '新数据' }, 
    		                     { 'id' : 'CRS702', 'text' : '修改和删除数据' }, 
    		                     { 'id' : 'CRS703', 'text' : '无数据申报' }  
    							 ]},
            validate : {required : true}
    	},  {
    		display : '报告生成时间戳', //YYYY-MM-DDTHH:mm:ss
    		name : 'tmstp',
    		newline : false,
    		type : 'date' ,
    	    format:'YYYY-MM-DDTHH:mm:ss',
    	    validate : {maxlength : 32 }
             
    	},  {
    		display : '账户记录编号',
    		name : 'docRefId',
    		newline : false,
    		type : 'text', 
            validate : { required : true, maxlength : 29 }
    	},  {
    		display : '被修改或被删除的账户记录编号',
    		name : 'corrDocRefId',
    		newline : false,
    		type : 'text', 
            validate : { required : true, maxlength : 29 }
    	},  {
    		display : '账户报告的类型',
    		name : 'docTypeIndic',
    		newline : false,
    		type : 'select', 
    		options : {data : [  { 'id' : 'R1', 'text' : '新账户记录' }, 
    		                     { 'id' : 'R2', 'text' : '修改账户记录' },  
    							 { 'id' : 'R3', 'text' : '删除账户记录'}]},
            validate : { required : true}
    	},  {
    		display : '错误标识',
    		name : 'errorFlag',
    		newline : false,
    		type : 'text', 
            validate : { required : true, maxlength : 32 }
    	},  {
    		display : '错误代码',
    		name : 'errorCode',
    		newline : false,
    		type : 'text', 
            validate : { required : true, maxlength : 32 }
    	},{
    		group : "持有人对公信息",
    		groupicon : groupicon	
        },{
        	display : "账号",
    		name : "oAccountNumber",
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 22
    		}
        },{
        	display : "姓名类别",
    		name : "oNameType",
    		newline : false,
    		type : "select", 
    		options : { data : [ { text : '亦称作', id : "OECD205"}, { text : '虚拟名称', id : "OECD206"}, { text : '法定名称', id : "OECD207"} ], cancelable  : true},
    		validate : {
    		    required : true,
    		    maxlength : 12
    		}
        },{
        	display : '机构英文名',
    		name : 'oOrganisationNameEn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '机构中文名',
    		name : 'oOrganisationNameCn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '地址类型',
    		name : 'oLegalAddressType',
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
    		name : 'oCountryCode',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 3,
    		    checkUppercase:true
    		}
        },{
        	display : '英文详细地址',
    		name : 'oAddressFreeEn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 500
    		}
        },{
        	display : '所在城市',
    		name : 'oCityEn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '街道',
    		name : 'oStreet',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 100
    		}
        },{
        	display : '楼号',
    		name : 'oBuildingIdentifier',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '房门号',
    		name : 'oSuiteIdentifier',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '楼层',
    		name : 'oFloorIdentifier',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '区',
    		name : 'oDistrictName',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '邮箱',
    		name : 'oPOB',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '邮编',
    		name : 'oPostCode',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '省/自治区/直辖市',
    		name : 'oCountrySubentity',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
        	display : '中文详细地址',
    		name : 'oAddressFreeCn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '省级行政区划代码',
    		name : 'oProvince',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 6,
    		    digits:true
    		}
        },{
        	display : '地市级行政区划代码',
    		name : 'oCityCn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '县级行政区划代码',
    		name : 'oDistrictName1',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '邮编CN',
    		name : 'oPostCode1',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '机构电话',
    		name : 'oPhoneNo',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
        	display : '税收居民国(地区)代码',
    		name : 'oRescountryCode',
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
    		name : 'oTin',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
    		display : '发放识别号的国家(地区)代码',
    		name : 'oIssuedBy',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : false,
    		    maxlength : 3,
    		    checkUppercase:true
    		}
    	},{
        	display : '未能获取纳税人识别号的原因',
    		name : 'oExplanation',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
    		group : "控制人信息",
    		groupicon : groupicon	
        },{
        	display : "账号",
    		name : "tAccountNumber",
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 22
    		}
        },{
        	display : "姓名类别",
    		name : "tNameType",
    		newline : false,
    		type : "select", 
    		options : { data : [ { text : '亦称作', id : "OECD205"}, { text : '虚拟名称', id : "OECD206"}, { text : '法定名称', id : "OECD207"} ], cancelable  : true},
    		validate : {
    		    required : true,
    		    maxlength : 12
    		}
        },{
        	display : '法定英文（拼音）姓',
    		name : 'tLastName',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '英文中间名',
    		name : 'tMiddleName',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },
        {
        	display : '法定英文（拼音）名',
    		name : 'tFirstName',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '中文姓名',
    		name : 'tNameCn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '曾用名',
    		name : 'tPrecedingTitle',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '称谓',
    		name : 'tTitles',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
        	display : '别名',
    		name : 'tNamePrefix',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
        	display : '标签名',
    		name : 'tGenerationIdentifier',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
        	display : '后缀',
    		name : 'tSuffix',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
        	display : '常规后缀',
    		name : 'tGeneralSuffix',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
        	display : '控制人类型',
    		name : 'tCtrlgPersonType',
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
    		name : 'tNationAlity',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '地址类型',
    		name : 'tLegalAddressType',
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
    		name : 'tCountryCode',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 3,
    		    checkUppercase:true
    		}
        },{
        	display : '英文详细地址',
    		name : 'tAddressFreeEn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 500
    		}
        },{
        	display : '所在城市',
    		name : 'tCityEn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
        	display : '街道',
    		name : 'tStreet',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 100
    		}
        },{
        	display : '楼号',
    		name : 'tBuildingIdentifier',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '房门号',
    		name : 'tSuiteIdentifier',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '楼层',
    		name : 'tFloorIdentifier',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '区',
    		name : 'tDistrictName',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '邮箱',
    		name : 'tPOB',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '邮编',
    		name : 'tPostCode',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '省/自治区/直辖市',
    		name : 'tCountrySubentity',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
        	display : '中文详细地址',
    		name : 'tAddressFreeCn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '省级行政区划代码',
    		name : 'tProvince',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 6,
    		    digits:true
    		}
        },{
        	display : '地市级行政区划代码',
    		name : 'tCityCn',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '县级行政区划代码',
    		name : 'tDistrictName1',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '邮编CN',
    		name : 'tPostCode1',
    		newline : false,
    		type : "text", 
    		validate : {
    		    required : false,
    		    maxlength : 32
    		}
        },{
        	display : '税收居民国(地区)代码',
    		name : 'tRescountryCode',
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
    		name : 'tTin',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : true,
    		    maxlength : 200
    		}
        },{
    		display : '发放识别号的国家(地区)代码',
    		name : 'tIssuedBy',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : false,
    		    maxlength : 3,
    		    checkUppercase:true
    		}
    	},{
        	display : '未能获取纳税人识别号的原因',
    		name : 'tExplanation',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
        },{
    		display : '出生日期',
    		name : 'tBirthDate',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : false,
    		    maxlength : 10,
    		    checkBirthdate:true
    		}
    	},{
    		display : '出生城市',
    		name : 'tCity',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : true,
    		    maxlength : 200
    		    
    		}
    	},{
    		display : '出生国代码',
    		name : 'tCountryCode1',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : false,
    		    maxlength : 3,
    		    checkUppercase:true
    		}
    	},{
    		display : '其他出生国英文',
    		name : 'tFormerCountryName',
    		newline : false,
    		type : 'text',
    		validate : {
    		    required : false,
    		    maxlength : 200
    		}
    	},{
    		name : 'recordId',
    		type : 'hidden'
        },{
    		name : 'oRecordId',
    		type : 'hidden'
        },{
    		name : 'tRecordId',
    		type : 'hidden'
        },{
    		//display : '数据日期',
    		name : 'oStatisticsDt',
    		type : 'hidden'
    	},{
    		//display : '数据日期',
    		name : 'tStatisticsDt',
    		type : 'hidden'
    	}];
    	
       	$("#mainform").ligerForm({
		    inputWidth : 170,
		    labelWidth : 150,
		    space : 40,
    	    fields : fields2
    	    });
       
    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));

    	BIONE.addFormButtons(buttons);
    }
    	
    function initFormData(){
    	if(id){
    		BIONE.loadForm(mainform, {
    		    url : "${ctx}/frs/nrtmessage/getNrtAccInfoById?id=${id}"
    		});
    	}
    }
    
  	//保存
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("nrtAcctInfoDealWin", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("nrtAcctInfoDealWin", "添加失败");
		});
    }
  	
    $(function(){
   		if(accType == '1'){
   			initForm1();
   			initFormData();
   		}else{
   			initForm2();
   			initFormData();
   		}
    });
  	
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/nrtmessage/nrtAccInfoSave"></form>
	</div>
</body>
</html>