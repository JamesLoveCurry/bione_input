<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var orgNo, deptNo;
	//扩展字段map
	var extFields;
	//非扩展字段map
	var unextFields;
	//创建表单结构 
	var mainform;
	var userId = "${id}";

	//密码安全策略对象
	var pwdSecurityObj ;
	// 通用“是”
	var COMMON_YES_FLAG = "1";
	var COMMON_NO_FLAG = "0";

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
	
	jQuery.validator.addMethod("numReg", function(value, element) {
	    var numReg = /^[0-9a-zA-Z]*$/;
	    return this.optional(element) || (numReg.test(value));
	}, "账号格式不合法");
	
	$(function() {
		initFormField();
	});
	
	function initFormField(){
		$.ajax({
			cache : false,
			async : true,
			type : 'post',
			url : "${ctx}/bione/admin/user/generateUserForm.json",
			dataType : 'json',
			data:{
				"userId":userId
			},
			success : function(result) {
				if(result == null || !result.fields
						|| !result.extFields
						|| !result.unextFields){
					BIONE.tip('表单初始化异常');
					return ;
				}
				extFields = result.extFields;
				unextFields = result.unextFields;
				//初始化表单
				mainform = $("#mainform").ligerForm({
					fields:result.fields
				}); 
				//初始化表单中特殊组件动作
				jQuery.metadata.setType("attr", "validate");
				BIONE.validate("#mainform");
				//当是修改操作时，初始化数据
				if(userId){
					setWhenUpdate();
				}else{
					initForm();
				}
				//初始化按钮
				var buttons = [];
				buttons.push({
					text : '取消',
					onclick : cancleCallBack
				});
				buttons.push({
					text : '保存',
					onclick : save_user
				});
				BIONE.addFormButtons(buttons);
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function initForm() {
		function deptSelectOpen() {
			if ($.ligerui.get("orgNoID").getValue() == null 
					|| $.ligerui.get("orgNoID").getValue() == "") {
				BIONE.tip('请先选择机构');
				return ;
			}
			var treeNode_id = $.ligerui.get("orgNoID").getValue();
			var deptOpts = {
				url : "${ctx}/bione/admin/depttree?id=" + treeNode_id,
				dialogname : 'deptComBoBox',
				title : '选择条线',
				comboxName : 'deptNoID',
				height : '410',
				width : '450'
			};
			BIONE.commonOpenIconDialog(deptOpts.title, deptOpts.dialogname,
					deptOpts.url, deptOpts.comboxName);
		};
		//-- 初始化机构条线联动
		if($.ligerui.get("orgNoID") && $.ligerui.get("deptNoID")){
		
			if(userId){
				var orgOptions = {
					url : "${ctx}/bione/admin/orgtree/asyncOrgTree",
					dialogname : 'orgComBoBox',
					title : '选择机构',
					comboxName : 'orgNoID',
					height : '410',
					width : '450'
				};
				$.ligerui.get("orgNoID").bind('beforeOpen', function() {
					BIONE.commonOpenIconDialog(orgOptions.title, orgOptions.dialogname,
							orgOptions.url, orgOptions.comboxName);
					return false;
				});
				var treeNode_id = $.ligerui.get("orgNoID").getValue();
				var deptOpts = {
					url : "${ctx}/bione/admin/depttree?id=" + treeNode_id,
					dialogname : 'deptComBoBox',
					title : '选择条线',
					comboxName : 'deptNoID',
					height : '450',
					width : '450'
				};
				$.ligerui.get("deptNoID").bind('beforeOpen', function() {
					deptSelectOpen();
					return false;
				});
			}
			else {
				var orgOptions = {
					url : "${ctx}/bione/admin/orgtree/asyncOrgTree",
					dialogname : 'orgComBoBox',
					title : '选择机构',
					comboxName : 'orgNoID',
					height : '410',
					width : '450'
				};
				$.ligerui.get("orgNoID").bind('beforeOpen', function() {
					BIONE.commonOpenIconDialog(orgOptions.title, orgOptions.dialogname,
							orgOptions.url, orgOptions.comboxName);
					return false;
				});
				
				$.ligerui.get("deptNoID").bind('beforeOpen', function() {
					deptSelectOpen();
					return false;
				});
			}
		}
		//加载客户经理柜员号选择框
		if($.ligerui.get("userAgnameID")){
			$.ligerui.get("userAgnameID").bind('beforeOpen', function() {
				$.ligerDialog.open({
					name:'userAgnameWin',
					comboboxName: "userAgnameID",
					title : '选择客户经理',
					width :600,
					height : $(window).height()-40-10,
					url:"${ctx}/crrsBsStaff/selectCustomerManager?userAgname="+ $.ligerui.get("userAgnameID").getValue(),
					buttons : [ {
						text : '确定',
						onclick : f_selectOK
					}, {
						text : '取消',
						onclick : f_selectCancel
					} ]
				});
				return false;
			});
		}
		
		//-- 初始化各种校验
		//-- -- 用户帐号重复性验证
		if($("#userNo").length > 0){			
			$("#userNo").rules("add",{remote:{
				url:"${ctx}/bione/admin/user/userNoValid",
				type:"post",
				async:false
			},messages:{remote:"用户标识重复"}});
		}
		if ($("#userName").length > 0) {
			$("#userName").rules("add", {
				remote: {
					url: "${ctx}/bione/admin/user/userNameValid",
					type: "post",
					async: false
				}, messages: {remote: "用户名称不符合规范"}
			});
		}
		$("#userNo").rules("add",{numReg:true});
		
		//-- -- 确认密码一致性验证
		if($("#userPwdTo").length > 0){			
			$("#userPwdTo").rules("add",{equalTo:"#userPwd"});
		}
		//-- -- 日期验证
		if($("#birthday").length > 0){			
			$("#birthday").rules("add",{date:true});
		}
		//-- -- 邮箱验证
		if($("#email").length > 0){
			$("#email").rules("add",{email:true});
		}
		//-- -- 手机号码验证
		if($("#mobile").length > 0){			
			$("#mobile").rules("add",{mobile:true});
		}
		//-- -- 电话号码验证
		if($("#tel").length > 0){			
			$("#tel").rules("add",{phone:true});
		}
		//-- -- 邮编验证
		if($("#postcode").length > 0){			
			$("#postcode").rules("add",{number:true});
		}
		loadPwdCpx();
	}
	
	// 修改动作初始化数据
	function setWhenUpdate(){
		if(userId){
			$.ajax({
				cache : false,
				async : true,
				type : 'post',
				url : "${ctx}/bione/admin/user/setWhenUpdate.json",
				dataType : 'json',
				data:{
					"userId":userId
				},
				success : function(result) {
					if(result){
						var userInfo = result.userInfo;
						var deptName = "";
						var orgName = "";
						if(userInfo){
							//初始化基本信息
							f_loadformOwn(userInfo);
							orgNo = userInfo.orgNo;
							deptNo = userInfo.deptNo;
							if(result.orgName){
								orgName = result.orgName;
							}
							if(result.deptName){
								deptName = result.deptName;
							}
						}
						var attrVals = result.attrVals;
						if(attrVals && attrVals.length){
							var objTmp = {};
							for(var i = 0 ; i < attrVals.length ; i++){
								var valTmp = attrVals[i];
								objTmp[valTmp.fieldName] = valTmp.attrValue;
							}
							//初始化扩展属性
							f_loadformOwn(objTmp);
						}
						$("#userNo").attr("disabled", "disabled");
						$("ul > li input#userPwdTo").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
						$("ul > li input#userPwd").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
						
						if(orgName){
							$("[name='orgNoID']").val(orgName);
						}
						if(deptName){
							$("[name='deptNoID']").val(deptName);
						}
						$("[name='userAgnameID']").val(userInfo.userAgname);
					}
					initForm();
				},
				error : function(result, b) {
					//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
	
	//
	function f_loadformOwn(data) {
		// 根据返回的属性名，找到相应ID的表单元素，并赋值
		for ( var p in data) {
			var ele = $("[name=" + p + "]");
			// 针对复选框和单选框 处理
			if (ele.is(":checkbox,:radio")) {
				ele[0].checked = data[p] ? true : false;
			} else if (ele.is(":text") && ele.attr("ltype") == "date") {
				if (data[p]) {
					var date=null;
					if(data[p].time){
						date = new Date(data[p].time);
					}else{
						//edit by caiqy
						var tdate;
						if(typeof data[p] == "string" 
								&&data[p].indexOf('-') != -1 && data[p].length >= 10){
				    		// if format as 'yyyy-MM-dd' or 'yyyy-mm-dd hh:mm:ss'
				    		tdate = new Date(new Number(data[p].substr(0,4)),new Number(data[p].substr(5,2))-1,new Number(data[p].substr(8,2)));
				    	}else{
			   		 		tdate = new Date(data[p]);
				    	}
						date = new Date(tdate);
					}
					var yy = date.getFullYear();
					var Mm = ((date.getMonth() + 1) < 10) ? ('0' + (date
							.getMonth() + 1)) : (date.getMonth() + 1);
					var dd = (date.getDate() < 10) ? ('0' + date.getDate())
							: date.getDate();
					ele.val(yy + '-' + Mm + '-' + dd);
				}
			} else {
				ele.val(data[p]);
			}
		}
		// 下面是更新表单的样式
		var managers = $.ligerui.find($.ligerui.controls.Input);
		for ( var i = 0, l = managers.length; i < l; i++) {
			// 改变了表单的值，需要调用这个方法来更新ligerui样式
			var o = managers[i];
			o.updateStyle();
			if (managers[i] instanceof $.ligerui.controls.TextBox)
				o.checkValue();
		}
	}
	
	// 保存提交方法
	function save_user() {
		if(!$("#mainform").valid()){
			BIONE.showInvalid(BIONE.validator);
			return ;
		}
		var formArray = $("#mainform").formToArray();
		if(formArray != null && extFields && unextFields){
			var submitObj = {
				extArray:[],
				unextObj:{}
			};
			var extArrayTmp = [];
			var unextObjTmp = {};
			f1:for(var i = 0 ,l = formArray.length ; i < l ; i++){
				var fieldTmp = formArray[i];
				f2:for(var j = 0 , l2 = extFields.length ; j < l2 ; j++){
					if(extFields[j].fieldName == fieldTmp.name){
						//若该属性是扩展属性，进行扩展属性封装
						var extObjTmp = {
							userId:"",
							attrId:extFields[j].attrId,
							attrValue:fieldTmp.value
						};
						extArrayTmp.push(extObjTmp);
						continue f1;
					}
				}
				f3:for(var k = 0 , l3 = unextFields.length ; k < l3 ; k++){
					if(unextFields[k].fieldName == fieldTmp.name){
						//若该属性是非扩展属性，进行非扩展属性封装
						unextObjTmp[fieldTmp.name] = fieldTmp.value;
						continue f1;
					}
				}
			}
			submitObj.extArray = extArrayTmp;
			submitObj.unextObj = unextObjTmp;
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/bione/admin/user",
				dataType : 'json',
				type : "post",
				data : {
					"userId":userId?userId:"",
					"submitObj" : JSON2.stringify(submitObj)
				},
				success : function(){
					BIONE.closeDialogAndReloadParent("userManage", "maingrid", "保存成功");
				},
				error : function(){
					BIONE.closeDialog("userManage", "保存失败");
				}
			});
		}
	}
	function cancleCallBack() {
		BIONE.closeDialog("userManage");
	}
	
	function f_selectOK(a,dialog){
		var data = dialog.frame.addToParent();
		if(data!=""){
			$("#mainform input[name='userAgname']").val(data.gyh);
			$("#mainform input[name='userAgnameID']").val(data.gyh);
		}
		dialog.close();
	}
	
	function f_selectCancel(a,dialog){
		dialog.close();
	}

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
				$("input[name=userPwd]").rules("add",{minlength:pwdSecurityObj.minLength});
			}
			// 2、最大长度
			if(pwdSecurityObj.maxLength != null
					&& pwdSecurityObj.maxLength != "undefined"){
				$("input[name=userPwd]").rules("add",{maxlength:pwdSecurityObj.maxLength});
			}
			// 3、包含大写字母
			if(pwdSecurityObj.incUppercase != null
					&& pwdSecurityObj.incUppercase != "undefined"
					&& COMMON_YES_FLAG == pwdSecurityObj.incUppercase){
				$("input[name=userPwd]").rules("add",{checkUppercase:true});
			}
			// 4、包含小写字母
			if(pwdSecurityObj.incLowercase != null
					&& pwdSecurityObj.incLowercase != "undefined"
					&& COMMON_YES_FLAG == pwdSecurityObj.incLowercase){
				$("input[name=userPwd]").rules("add",{checkLowercase:true});
			}
			// 5、包含数字
			if(pwdSecurityObj.incNum != null
					&& pwdSecurityObj.incNum != "undefined"
					&& COMMON_YES_FLAG == pwdSecurityObj.incNum){
				$("input[name=userPwd]").rules("add",{checkNum:true});
			}
			// 6、包含特殊符号
			if(pwdSecurityObj.incSpecial != null
					&& pwdSecurityObj.incSpecial != "undefined"
					&& COMMON_YES_FLAG == pwdSecurityObj.incSpecial){
				$("input[name=userPwd]").rules("add",{checkSpecial:true});
			}
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/bione/admin/user" method="post"></form>
	</div>
</body>
</html>