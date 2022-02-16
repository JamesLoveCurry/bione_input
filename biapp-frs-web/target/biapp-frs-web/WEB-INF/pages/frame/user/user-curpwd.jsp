<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/js/bione/encode.js"></script>
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    var userId = "${id}";
    var isFirst = "${isFirst}";
    var oldUserName = "${oldUserName}";
    var oldEmail = "${oldEmail}";
    var oldMobile = "${oldMobile}";

    // 通用“是”
    var COMMON_YES_FLAG = "1";
    var COMMON_NO_FLAG = "0";
    
    //创建表单结构 
    var mainform;
    //密码安全策略对象
    var pwdSecurityObj ;
    
    // 校验是否包含大写字母
    jQuery.validator.addMethod("checkUppercase", function(value, element) {
    	var uppercaseReg = /.*[A-Z].*/;
    	return this.optional(element) || (uppercaseReg.test(value));
    }, "必须包含大写字母.");
    
    // 校验是否包含小写字母
    jQuery.validator.addMethod("checkLowercase", function(value, element) {
    	var lowercaseReg = /.*[a-z].*/;
    	return this.optional(element) || (lowercaseReg.test(value));
    }, "必须包含小写字母.");
    
    // 校验是否包含数字
    jQuery.validator.addMethod("checkNum", function(value, element) {
    	var numReg = /.*[0-9].*/;
    	return this.optional(element) || (numReg.test(value));
    }, "必须包含数字.");
    
    // 校验是否包含特殊字符
    jQuery.validator.addMethod("checkSpecial", function(value, element) {
    	var specialReg = /.*\W.*/;
    	return this.optional(element) || (specialReg.test(value));
    }, "必须包含特殊字符.");
    
    //
    $(function() {
		$("#userId").attr("value", userId);
		mainform = $("#mainform");
		mainform.ligerForm({
			labelWidth:'100',
		    fields : [{
				display : "请输入旧密码",
				name : "userPwd_old",
				newline : true,
				type : "password",
				group : "修改用户信息",
				groupicon : groupicon,
				validate : {
				    required : true,
				    maxlength : 100,
				    remote : {
						url : "${ctx}/bione/admin/user/userPwdValid?d=" + new Date().getTime()
				    },
				    messages : {
						remote : "原始密码错误"
				    }
				}
			}, {
				display : "请输入新密码",
				name : "userPwd_1",
				newline : true,
				type : "password",
				options:{
					onChangeValue : function(value){
						// 刷新样式
						refreshByPwd(value);
					}
				},
				validate : {
				    required : true,
				    maxlength : 100,
				    remote : {
						url : "${ctx}/bione/admin/user/userPwdHisValid?d=" + new Date().getTime()
				    },
				    messages : {
						remote : "密码不允许与历史密码重复"
				    }
				}
			}, {
				display : "确认密码",
				name : "userPwd_2",
				newline : true,
				type : "password",
				validate : {
				    required : true,
				    maxlength : 100,
				    equalTo : "#userPwd_1"
				}
		    }, {
                display : "名称",
                name : "userName",
                comboboxName : 'userNameBox',
                newline : true,
                type : "text"
            } , {
                  display : "联系方式1",
                  name : "mobile",
                  newline : true,
                  type : "text"
              }  , {
                  display : "联系方式2",
                  name : "email",
                  newline : true,
                  type : "text"
              } ]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		if(isFirst != '1'){
			buttons.push({
				text : '取消',
				onclick : cancleCallBack
			});
		}
		buttons.push({
		    text : '保存',
		    onclick : save_user
		});
		BIONE.addFormButtons(buttons);
		// 添加自定义密码强度域
		initPwdStrength();
		if(oldUserName) {
        	$("#userName").attr("value",oldUserName);
        }
        if(oldMobile) {
            $("#mobile").attr("value",oldMobile);
        }
        if(oldEmail) {
            $("#email").attr("value",oldEmail);
        }
	});
    
    // 初始化密码强度域
   function initPwdStrength(){
    	// 初始化密码强度显示域
    	var sbuffer = [];
    	sbuffer.push("<ul>");
    	sbuffer.push("	<li style='width:90px;text-align:left;'>&nbsp;</li>");
    	sbuffer.push("	<li style='width:180px;text-align:left;'>");
    	sbuffer.push("		<div style='width: 183px;margin-top: 3px;height: 14px;background-color: #cfc6bd;position: relative;overflow: hidden;'>    ");
    	sbuffer.push("    		<div id='deepPwdArea' style='width:60px;height:14px;overflow:hidden;background-color: #87715a;'></div>                          ");
    	sbuffer.push("    		<div style='position: absolute;left: 0px;top: 0px;''>                             ");
    	sbuffer.push("				<span style='display: inline;float: left;height: 14px;width: 60px;font-size: 12px;text-align: center;line-height: 14px;color: #fff;border-right: 1px solid #FFF;'>弱</span> ");
    	sbuffer.push("				<span style='display: inline;float: left;height: 14px;width: 60px;font-size: 12px;text-align: center;line-height: 14px;color: #fff;border-right: 1px solid #FFF;'>中</span> ");
    	sbuffer.push("				<span style='display: inline;float: left;height: 14px;width: 60px;font-size: 12px;text-align: center;line-height: 14px;color: #fff;border-right: 1px solid #FFF;'>强</span>");
    	sbuffer.push("			</div>                                                ");
    	sbuffer.push("		</div>");
    	sbuffer.push("	</li>");
    	sbuffer.push("</ul>");
    	var uls = $("#mainform").children().children("ul");
    	if(uls.length && uls.length >= 2){
    		$(uls[1]).after(sbuffer.join(""));
    	}
    	// 初始化新密码输入框的keyup事件
    	$("input[name=userPwd_1]").bind("keyup",function(){
    		var pwd = $("input[name=userPwd_1]").val();
    		// 刷新样式
    		refreshByPwd(pwd);
    	});
    }
    
    // 根据密码，更新强度展示样式
    function refreshByPwd(pwd){
    	var newLevel = checkPwdStrength(pwd);
    	// 包含多少个右边框（1px）
    	var borderRightCount = newLevel > 1 ? (newLevel - 1) : 0;
		$("#deepPwdArea").animate({width:60*newLevel + borderRightCount});
    }
    
    // 自定义密码强度校验
    // @return level   
    //					1 -> 弱密码
    //					2 -> 中等密码
    //					3 -> 强密码
    function checkPwdStrength(pwd){
    	var level = 0;
    	if(pwd != null 
    			&& pwd != ""
    			&& typeof pwd != "undefined"){
    		//1、如果密码少于5位，那么就认为这是一个弱密码。
    		if(pwd.length < 5){
    			level = 1;
    		} else {    			
    			//2、如果密码只由数字、小写字母、大写字母或其它特殊符号当中的一种组成，则认为这是一个弱密码。
    			//3、如果密码由数字、小写字母、大写字母或其它特殊符号当中的两种组成，则认为这是一个中度安全的密码。
    			//4、如果密码由数字、小写字母、大写字母或其它特殊符号当中的四种组成，则认为这是一个比较安全的密码。
    			// 包含大写字母
    			if(/.*[A-Z].*/.test(pwd)){
    				level++;
    			}
    			// 包含小写字母
    			if(/.*[a-z].*/.test(pwd)){
    				level++;
    			}
    			// 包含数字
    			if(/.*[0-9].*/.test(pwd)){
    				level++;
    			}
    			// 包含特殊符号
    			if(/.*\W.*/.test(pwd)){
    				level++;
    			}
    		}
    	}
    	if(level <= 1){
    		//<=1 弱密码
    		level = 1;
    	} else if(level == 4){
    		// ==4 强密码
    		level = 3;
    	} else {
    		// >= 2 ； <4 中等密码
    		level = 2;
    	}
    	return level;
    }
    
	// 保存提交方法
    function save_user() {
    	if(!$("#mainform").valid()){
			BIONE.showInvalid(BIONE.validator);
			return ;
		}
		BIONE.submitForm($("#mainform"), function() {
		    var dialog = parent.$.ligerui.get("userManage");
		    parent.BIONE.tip("用户信息修改成功");
		    dialog.close();
		}, function() {
		    parent.BIONE.tip("用户信息修改失败");
		});
    }
    function cancleCallBack() {
        BIONE.closeDialog("userManage");
    }
    /*
     * 显示密码强度的图片
     */
	$(function() {
    	loadPwdCpx(); // load-pwd-cpx
	});
    
    function loadPwdCpx() {
    	$.ajax({
       		cache : false,
       		async : true,
       		url : '${ctx}/bione/admin/pwsec/getPwdComplex.json',
       		dataType : 'json',
       		type : 'get',
       		success : function(result, textStatus, jqXHR) {
       		    if (!result){ 
       		    	return; 
       		    }
       		    if (result.success && result.success=="true") {
       		    	pwdSecurityObj = result.pwdSecurity;
       		    	initSecurityValidation();
       		    } else {
       		    	BIONE.tip(result.msg);
       		    }
       		},
       		error : function(result, textStatus, errorThrown) {
       			BIONE.tip("获取用户密码已使用过的时间发生异常！")
       		}
   		});
    	
    	
    	/**
    	*  初始化密码安全验证
    	*/
    	function initSecurityValidation(){
    		// 前提是本地密码安全策略有配置并且是处于启用状态
    		if(pwdSecurityObj == null
    				|| COMMON_NO_FLAG == pwdSecurityObj.enableSts){
    			return ;
    		}
    		// 添加校验
    		// 1、最小长度
    		if(pwdSecurityObj.minLength != null
    				&& pwdSecurityObj.minLength != "undefined"){    			
    			$("input[name=userPwd_1]").rules("add",{minlength:pwdSecurityObj.minLength});
    		}
    		// 2、最大长度
    		if(pwdSecurityObj.maxLength != null
    				&& pwdSecurityObj.maxLength != "undefined"){    			
    			$("input[name=userPwd_1]").rules("add",{maxlength:pwdSecurityObj.maxLength});
    		}
    		// 3、包含大写字母
    		if(pwdSecurityObj.incUppercase != null
    				&& pwdSecurityObj.incUppercase != "undefined"
    				&& COMMON_YES_FLAG == pwdSecurityObj.incUppercase){    			
    			$("input[name=userPwd_1]").rules("add",{checkUppercase:true});
    		}
    		// 4、包含小写字母
    		if(pwdSecurityObj.incLowercase != null
    				&& pwdSecurityObj.incLowercase != "undefined"
    				&& COMMON_YES_FLAG == pwdSecurityObj.incLowercase){    			
    			$("input[name=userPwd_1]").rules("add",{checkLowercase:true});
    		}
    		// 5、包含数字
    		if(pwdSecurityObj.incNum != null
    				&& pwdSecurityObj.incNum != "undefined"
    				&& COMMON_YES_FLAG == pwdSecurityObj.incNum){    			
    			$("input[name=userPwd_1]").rules("add",{checkNum:true});
    		}
    		// 6、包含特殊符号
    		if(pwdSecurityObj.incSpecial != null
    				&& pwdSecurityObj.incSpecial != "undefined"
    				&& COMMON_YES_FLAG == pwdSecurityObj.incSpecial){    			
    			$("input[name=userPwd_1]").rules("add",{checkSpecial:true});
    		}
    		// 7、与历史密码不能一样
    		if(pwdSecurityObj.diffRecentHis != null
    				&& pwdSecurityObj.diffRecentHis != "undefined"
    				&& pwdSecurityObj.diffRecentHis > 0){
    			$("input[name=userPwd_1]").rules("add",{remote:{
    				url:"${ctx}/bione/admin/user/userPwdHisValid?d="+new Date().getTime() + "&diffRecentHis="+pwdSecurityObj.diffRecentHis,
    				type:"get",
    				async:false
    			}});
    		}
    	}
    }

</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/bione/admin/user/updatePwd" method="post">
			<input type="hidden" id="userId" name="userId">
		</form>
	</div>
</body>
</html>