<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    
    var id = "${id}"
    //判断小数点后两位
   	jQuery.validator.addMethod("minNumber",function(value,element){
       	var returnVal=true; 
       	 var arrMen=value.split(".");
       	 if(arrMen.length==2){
       		 if(arrMen[1].length>2){
       			 returnVal=false;
       			 return false;
       		 }
       	 }
       	 return returnVal; 
       },"小数点最多为两位")
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
      },"只能是大写字母")    
        
        
        
        
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
                
			},{
				group : "业务信息",
                groupicon : groupicon,
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
                validate : { required : true, maxlength : 10 }
			},  {
				display : '系统用户账号',
				name : 'reportingId',
				newline : false,
				type : 'text', 
                validate : { required : true, maxlength : 14 }
			},  {
				display : '账户是否已注销',
				name : 'closedAccount',
				newline : false,
				type : 'select', 
				options : {data : [  { 'id' : 'true', 'text' : '账户已注销' }, 
									 { 'id' : 'false', 'text' : '正常户'}]},
                validate : { required : true, maxlength : 10 }
			},  {
				display : '是否取得账户持有人的自证声明',
				name : 'selfCertification',
				newline : false,
				type : 'select', 
				options : {data : [  { 'id' : 'true', 'text' : '取得自证声明' }, 
									 { 'id' : 'false', 'text' : '未取得自证声明'}]},
                validate : { required : true, maxlength : 10 }
			},  {
				display : '货币代码（余额）',
				name : 'currCode',
				newline : false,
				type : 'text', 
                validate : { required : true, maxlength : 30 }
			},  {
				display : '账户余额', 
				name : 'accountBalance',
				 
				/* 账户余额，小数点后两位数字。
				注销账户余额报：0.00
				如果为负值，则按0.00报送。 */
				newline : false,    
				type : 'text', 
                validate : { 
                	required : true,
                	min:0.00,number:true, 
                	minNumber: 'accountBalance',
                	maxlength : 100,messages : {
					remote : "请输入数字，小数点后两位"
				} }
			},  {
				display : '账户持有人类别',
				name : 'accountHolderType',
				newline : false,
				type : 'select',  
				options : {data : [  { 'id' : 'CRS100', 'text' : '非居民个人' }, 
				                     { 'id' : 'CRS101', 'text' : '有非居民控制人的消极非金融机构' }, 
				                     { 'id' : 'CRS102', 'text' : '非居民机构，不包括消极非金融机构' }, 
									 { 'id' : 'CRS103', 'text' : '非居民消极非金融机构，但没有非居民控制人'}]},
                validate : { required : true, maxlength : 20 }
			},  {
				display : '开户金融机构名称',
				name : 'openingFIName',
				newline : false,
				type : 'text', 
                validate : { required : true, maxlength : 14 }
			},  {
				display : '金融机构注册码',
				group : "配置信息",
				groupicon : groupicon,
				name : 'fiId',
				newline : true,
				type : 'text', 
                validate : { required : true, maxlength : 14 }
			},  {
				display : '收入类型',
				name : 'paymentType',
				newline : false,
				/* CRS501 – 股息；
				CRS502 – 利息；
				CRS503 – 销售或者赎回总收入；
				CRS504 – 其他。 */
				type : 'select', 
				options : {data : [  { 'id' : 'CRS501', 'text' : '股息' }, 
				                     { 'id' : 'CRS502', 'text' : '利息' }, 
				                     { 'id' : 'CRS503', 'text' : '销售或者赎回总收入' }, 
									 { 'id' : 'CRS504', 'text' : '其他'}]},
                validate : { required : true, maxlength : 6 }
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
                	         min:0.00,number:true, 
                	         minNumber: 'accountBalance', 
                	         maxlength : 32 }
			},  {
				display : '报告类别',
				name : 'reportingType',
				newline : false,
				type : 'text', 
				options : {data : [   { 'id' : 'CRS', 'text' : 'CRS'}]},
                validate : { required : true, maxlength : 5 }
			},  {
				display : '报告唯一编码',
				name : 'messageRefId',
				newline : false,
				type : 'select', 
				options : {data : [  { 'id' : 'CRS', 'text' : 'CRS'}]},
                validate : { required : true, maxlength : 32 }
			},  {
				display : '报告年度',
				name : 'reportingPeriod',
				width : '10%',
				type : 'date',
				format:'yyyy-MM-dd',
                validate : { required : true, maxlength : 15 }
			}, 
			{
				display : '报告类型',
				name : 'messageTypeIndic',
				newline : false,
				type : 'select', 
				/* 允许取值范围为：
				CRS701：新数据
				CRS702：修改和删除数据
				CRS703：无数据申报
				当该元素值为CRS703时，报文中不能含有ReportingGroup元素，否则报文中必须包括ReportingGroup元素。 */
				options : {data : [  { 'id' : 'CRS701', 'text' : '新数据' }, 
				                     { 'id' : 'CRS702', 'text' : '修改和删除数据' }, 
				                     { 'id' : 'CRS703', 'text' : '无数据申报' }  
									 ]},
                validate : { required : true, maxlength : 55 }
			},  {
				display : '报告生成时间戳', //YYYY-MM-DDTHH:mm:ss
				name : 'tmstp',
				newline : false,
				type : 'date' ,
			    format:'YYYY-MM-DDTHH:mm:ss',
			    validate : { required : true, maxlength : 32 }
                 
			},  {
				display : '账户记录编号',
				name : 'docRefId',
				/* 账户记录编号，不得重复。
				格式：CN+4位年度信息+14位银行法人金融机构编码+9位数字序列号。
				例如：
				CN2017XXXXXXXXXXXXXX000000001
				其中，年度信息应与ReportingPeriod一致，如果ReportingPeriod为2017-12-31，则应为2017。
				当数据报送方式为代报时，应填写被代报银行法人金融机构编码。例如某村镇银行由其发起行代报数据，则此处应填写村镇银行法人金融机构编码。 */
				newline : false,
				type : 'text', 
                validate : { required : true, maxlength : 29 }
			},  {
				display : '被修改或被删除的账户记录编号',
				name : 'corrDocRefId',
/* 				被修改或被删除的账户记录编号。 新增文件不得包括此字段
 */
				newline : false,
				type : 'text', 
                validate : { required : true, maxlength : 29 }
			},  {
				display : '账户报告的类型',
				name : 'docTypeIndic',
				newline : false,
				type : 'select', 
				/* 账户报告的类型，每个报告可以同时包含修改账户记录和删除账户记录，但不能同时包含新账户记录和修改/删除账户记录，二者不得混合报送。。
				取值范围：
				R1: 新账户记录
				R2：修改账户记录
				R3：删除账户记录
				当MessageTypeIndic为CRS701时，则DocTypeIndic为R1；
				当MessageTypeIndic为CRS702时，则DocTypeIndic为R2或R3 */
				options : {data : [  { 'id' : 'R1', 'text' : '新账户记录' }, 
				                     { 'id' : 'R2', 'text' : '修改账户记录' },  
									 { 'id' : 'R3', 'text' : '删除账户记录'}]},
                validate : { required : true, maxlength : 50 }
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
			}  ]
    	});

    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));
        
    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("baseAcctEidtWin", null);
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
    		    url : "${ctx}/frs/nrtmessage/getBaseAcctById?id=${id}"
    		});
    	}
    }
    
  	//保存
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("baseAcctEidtWin", "maingrid", "添加成功");
		}, function() {
		    BIONE.closeDialog("baseAcctEidtWin", "添加失败");
		});
    }
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/nrtmessage/saveBaseAcct"></form>
	</div>
</body>
</html>