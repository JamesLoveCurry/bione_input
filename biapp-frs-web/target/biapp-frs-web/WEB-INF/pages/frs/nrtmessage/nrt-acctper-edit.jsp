<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    
    var id = "${id}";
	jQuery.validator.addMethod("isAllCaps",function(value,element){
      	var returnVal=true; 
      	for(var i=0;i<value.length;i++){
      		var c=value.charAt(i);
      		if(c<'A'||c>'Z'){
      			 returnVal=false;
       			 return false;
      		} 
      	} 
      	return returnVal;
      },"只能是大写字母")   ;
    $(function(){
    	initForm();
    	initFormData();
    });
    
    function initForm() {
    	$("#mainform").ligerForm({
    	    fields : [{
				 display : '序号',
				 name : 'recordId', 
				 type : 'hidden'  
			},  {
				display : '数据日期', 
				hideSpace: true,name : 'statisticsDt',
				newline : false,
				type : 'date',
				format:'yyyy-MM-dd',
               validate : { required : true, maxlength : 10 }
			},{
				display : '账号',
				group : "PART1",
               groupicon : groupicon, 
				hideSpace: true,name : 'accountNumber', 
				newline : true, 
				type : 'text',
				validate : { required : true, maxlength : 32 }
			},  {
				display : '姓名类型',
				hideSpace: true,name : 'nameType', 
				/* 取值范围包括：
				OECD202：个人姓名；
				OECD203：别名；
				OECD204：昵称；
				OECD205：亦称作（aka）；
				OECD208：出生姓名 */
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
				hideSpace: true,name : 'middleName',
				newline : false,
				type : 'text',
               validate : { required : true, maxlength : 100 }
			},  {
				display : '法定英文（拼音）姓',
				hideSpace: true,name : 'lastName',
				newline : false,
				type : 'text',
               validate : { required : true, maxlength : 100 }
			} ,  {
				display : '法定英文（拼音）名',
				/* 法定英文（拼音）名。身份证件只有中文的，填写拼音。
				如果账户持有人的法定姓名是一个单名，不区分姓和名，则在FirstName中填写“NFN”，在LastName中填写该单名。 */
				hideSpace: true,name : 'firstName',
				newline : false,
				type : 'text',
               validate : { required : true, maxlength : 200 }
			} ,  {
				display : '中文姓名', 
				hideSpace: true,name : 'nameCn',
/* 				中文姓名。开户证件有中文姓名的必须填报。
*/				newline : false,
				type : 'text',
               validate : { required : true, maxlength : 100 }
			} ,  {
				display : '曾用名',
				hideSpace: true,name : 'preceDingTitle',
				newline : false,
				type : 'text',
               validate : { required : false, maxlength : 200 }
			} ,  {
				display : '别名',
				hideSpace: true,name : 'titles',
				newline : false,
				type : 'text',
               validate : { required : false, maxlength : 200 }
			} ,  {
				display : '别名1',
				hideSpace: true,name : 'namePreFix',
				newline : false,
				type : 'text',
               validate : { required : false, maxlength : 200 }
			},  {
				display : '标签名',
				hideSpace: true,name : 'generationIdentifier',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 200 }
			} ,  {
				display : '后缀',
				hideSpace: true,name : 'suffix',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 200 }
			} ,  {
				display : '后缀1',
				hideSpace: true,name : 'generalSuffix',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 200 }
			} ,  {
				display : '性别',
				hideSpace: true,name : 'gender',
				newline : false,
				type : 'select',
				/* M-男；
				F-女；
				N-未说明性别。 */
				options : {data : [  { 'id' : 'M', 'text' : '男' },
				                     { 'id' : 'F', 'text' : '女' },
									 { 'id' : 'P', 'text' : '未说明性别'}]},
                validate : { required : true, maxlength : 30 }
			} ,  {
				display : '国籍',
				hideSpace: true,name : 'nationAlity',
				newline : false,
				type : 'text',
                validate : { required : true,  maxlength : 3 }
			},  {
				display : '出生日期',
				hideSpace: true,name : 'birthDate',
				newline : false,
				type : 'date',
				format:'yyyy-MM-dd',
                validate : { required : true, maxlength : 10 }
			} ,  {
				display : '出生城市',
				hideSpace: true,name : 'city',
				newline : false,
				type : 'text',
                validate : { required : true, maxlength : 200 }
			} ,  {
				display : '出生国代码',
				hideSpace: true,name : 'countryCode1',
				newline : false,
				type : 'text',
                validate : { required : true, maxlength : 3 }
			} ,  {
				display : '其他出生国代码',
				hideSpace: true,name : 'formerCountryName',
				newline : false,
				/* 出生国不在标准定义范围内，无法填写代码的，在本字段中填写出生国英文名称。
 */
				type : 'text',
                validate : { required : true, maxlength : 200 }
			},  {
				display : '联系电话',
				hideSpace: true,name : 'phoneNo',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 20 }
			},  {
				display : 'Address的属性',
				hideSpace: true,name : 'legalAddressType',
				newline : false,
				/* "OECD301：居住地址或办公地址；
				OECD302：居住地址；
				OECD303：办公地址；
				OECD304：注册地址；
				OECD305：其他。"
 */
				type : 'select',
				options : {data : [  { 'id' : 'OECD301', 'text' : '居住地址或办公地址' }, 
				                     { 'id' : 'OECD302', 'text' : '居住地址' }, 
				                     { 'id' : 'OECD303', 'text' : '办公地址' }, 
									 { 'id' : 'OECD304', 'text' : '注册地址'},
									 { 'id' : 'OECD305', 'text' : '其他'}]},
                validate : { required : false, maxlength : 10 }
			} ,  {
				display : '国家代码',
				hideSpace: true,name : 'countryCode',
				newline : false,
				type : 'text',
                validate : { required : true,  maxlength : 3 }
			} ,  {
				display : '英文详细地址',
				hideSpace: true,name : 'addressFreeEn',
				newline : false,
				type : 'text',
                validate : { required : true, maxlength : 500 }
			} ,  {
				display : '所在城市',
				hideSpace: true,name : 'cityEn',
				newline : false, 
				/* 所在城市。中国境内地址填写直辖市、地级市或县级市拼音，境外地址填写英文。
				如AddressFixEN为强制、则CityEN为强制；
				如AddressFixEN为可选、则CityEN为可选。 */
				type : 'text',
                validate : { required : true, maxlength : 200 }
			},  {
				display : '楼号',
				hideSpace: true,name : 'buildingIdentifier',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 100 }
			} ,  {
				display : '房门号',
				hideSpace: true,name : 'suiteIdentifier',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 100 }
			}  ,  {
				display : '街道',
				hideSpace: true,name : 'street',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 200 }
			} ,  {
				display : '楼层',
				hideSpace: true,name : 'floorIdentifier',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 100 }
			} ,  {
				display : '县级行政区划代码',
				hideSpace: true,name : 'districtName',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 200 }
			} ,  {
				display : '邮箱',
				hideSpace: true,name : 'pOB',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 50 }
			} ,  {
				display : '邮编',
				hideSpace: true,name : 'postCode',
				newline : false,
				type : 'text',
                validate : { required : true, maxlength : 50 }
			} ,  {
				group : "PART2",                         //--------------分割线---------------------------
				groupicon : groupicon,
				display : '中文详细地址',
				hideSpace: true,name : 'addressFreeCn',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 500 }
			} ,  {
				display : '省级行政区划代码',
				hideSpace: true,name : 'province',
				newline : false,
				type : 'text',
/* 				6位阿拉伯数字 如果存在元素AddressFixCN，则Province为强制
 */
                validate : { required : true, maxlength : 6 }
			} ,  {
				display : '地市级行政区划代码',
				hideSpace: true,name : 'cityCn',
				newline : false,
			/* 	"地市级行政区划代码。
				如果存在元素AddressFixCN，则CityCN为强制。" */

				type : 'text',
                validate : { required : true, maxlength : 6 }
			} ,  {
				display : '县级行政区划代码1',
				hideSpace: true,name : 'districtName1',
				newline : false,
				type : 'text',
                validate : { required : true, maxlength : 200 }
			} ,  {
				display : '邮编1',
				hideSpace: true,name : 'postCode1',
				newline : false,
				type : 'text',
                validate : { required : true, maxlength : 50 }
			}  ,  {
				display : '身份证件类型',
				hideSpace: true,name : 'idType',
				newline : false,
				type : 'select',
				/* 格式：3位英文字母+ 2位阿拉伯数字。
				取值范围：
				ACC01:第一代居民身份证；
				ACC02:第二代居民身份证；
				ACC03:临时身份证；
				ACC04:中国护照；
				ACC05:户口簿；
				ACC06:村民委员会证明；
				ACC07:学生证；
				ACC15:港澳居民来往内地通行证；
				ACC16:台湾居民来往大陆通行证；
				ACC17:外国人永久居留证；
				ACC18:边民出入境通行证；
				ACC19:外国护照；
				ACC20:其他。 */
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
				hideSpace: true,name : 'idNumber',
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 30 }
			} ,  {
				display : '识别号',
				hideSpace: true,name : 'tin',
				newline : false,
				type : 'text',
				/* 该元素表示纳税人识别号。 */
                validate : { required : false, maxlength : 200 }
			},  {
				display : '识别号类型',
				hideSpace: true,name : 'inType',
				/* 属性inType固定填写“TIN”。 */
				options : {data : [  { 'id' : 'TIN', 'text' : 'TIN' }  
				                     ]},
				newline : false,
				type : 'text',
                validate : { required : false, maxlength : 3 }
			},  {
				display : '识别号1',
				hideSpace: true,name : 'tin1',
				newline : false,
				/* 账户持有人在其税收居民国（地区）的纳税人识别号。账户持有人无税收居民国（地区）纳税人识别号的，银行无需收集并报送。
				新开账户：账户持有人有税收居民国（地区）纳税人识别号的，必须收集报送。
				存量账户：银行现有客户资料中没有居民国（地区）纳税人识别号的，无需报送。但是，金融机构应当在账户被认定为非居民账户的次年12月31日前，积极采取措施，获取该信息。
				该元素为可重复元素，重复次数为0至无穷。 */
				type : 'text',
                validate : { required : false, maxlength : 200 }
			}
			,  {
				display : '税收居民国（地区）代码',
				hideSpace: true,name : 'rescountryCode',
				newline : false,
				type : 'text',
				/* 格式：3位大写英文字母。 */
                validate : { 
                	required : true,  
                	isAllCaps:'rescountryCode', 
                	 
				    maxlength : 3 
				 }
			  } , {
					display : '发放识别号的国家（地区）代码',
					hideSpace: true,name : 'issuedBy',
					/* 3位大写英文字母。 可选*/ 
					newline : false,
					type : 'text',
	                validate : { required : true, maxlength : 3 }
				} ,  {
					display : '识别号类型1',
					hideSpace: true,name : 'inType1',
					 /* 属性inType固定填写“TIN” */
					newline : false,
					type : 'text',
	                validate : { required : true, maxlength : 200 }
				} ,  {
					display : '发放识别号的国家（地区）代码1',
					hideSpace: true,name : 'issuedBy1',
					/* 3位大写英文字母。 可选
					 */
					width : "28%",
					newline : false,
					type : 'text',
	                validate : { required : true, maxlength : 200 }
				}  ,  {
					display : '税收居民国（地区）代码1',
					hideSpace: true,name : 'rescountryCode1',
					newline : false,
					type : 'text',
					/* 格式：3位大写英文字母。 */
					width : "28%",
	                validate : { required : true, maxlength : 3 }
				}  ,  {
					display : '不能提供居民国/地区纳税人识别号的理由',
					hideSpace: true,name : 'explanation',
					/* 如果客户表示未能获取纳税人识别号，填报客户说明的具体原因，中英文均可。
					如新增数据中没有填写TIN，则该项目为强制。 */
					newline : false,
					type : 'text',
	                validate : { required : true, maxlength : 200 }
				} ,  {
					
					display : '中国境内地址填写省/自治区/直辖市的拼音',
					hideSpace: true,name : 'countrySubentity', 
					newline : true,
					type : 'text',
	                validate : { required : false, maxlength : 200 }
				} ,  {
					display : '错误标识',
					hideSpace: true,name : 'errorFlag',     
					newline : false,
					type : 'hidden'      
	                
				} ,  {
					display : '错误代码',     
					hideSpace: true,name : 'errorCode',
					newline : false,
					type : 'hidden' 
	                 
				}
			]
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
    		    url : "${ctx}/frs/nrtmessage/getindivById?id=${id}"
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
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/nrtmessage/saveindivAcct"></form>
	</div>
</body>
</html>