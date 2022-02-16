<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="zh">
<head>
<%@ include file="/common/meta.jsp"%>
<meta charset="UTF-8">
<title>宇信科技统一监管报送平台-登录</title>
<link href="${ctx}/static/theme/Boomerang/template.min.css" rel="stylesheet">
<link rel="icon" href="data:image/ico;base64,aWNv">
<script type="text/javascript" src="${ctx}/js/jquery/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		var option = "<option value='FRS'>统一监管平台</option>";
        $("#logicSysNo").append(option);
		
		//监控回车事件,提交表单
		document.onkeydown = function(e) {
			var ev = document.all ? window.event : e;
			if (ev.keyCode == 13) {
				doSubmit();
			}
		};

		//使登录窗口居中(解决不同版本IE浏览器表格高度计算问题)
		$("#loginMain").css("height", $(document).height() - 2);
	});

	//提交单进行登录
	function doSubmit() {
		document.loginForm.submit();
	}
</script>
<%
	String error = null;
	if(request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME) != null){
		error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);
	}
	String loginFailInfo = null;
	if(request.getAttribute("LOGIN_FAIL_INFO") != null){
		loginFailInfo = (String) request.getAttribute("LOGIN_FAIL_INFO");
	}

	String tip = "";
	String className = "font_tip_error";

	if (error != null) {
		tip = "您尚未登录或者会话超时，请重新登录.";
	} else if (loginFailInfo != null) {
		tip = loginFailInfo;
	} else {
		tip = "请输入用户名和密码";
		className = "font_tip";
	}
	tip = "<span style='font-size: .9375rem; color: #fff;' class='" + className + "'>" + tip + "</span>";
%>
</head>
<body>
	<div>
		<div id="loginMain"
			style="background: url(${ctx}/static/theme/Boomerang/module-5.min.jpg);  background-size: cover !important;display: flex; align-items: center;">
			<div class="container">
				<div class="row">
					<div class="col-lg-4 col-md-6 m-auto">
						<div class="m-b-20">
							<h4 style="font-size: 2.375rem; color: #fff; text-align: center;">统一监管报送平台</h4>
							<h6 style="text-align: center;"><%=tip%></h6>
						</div>
						<div class="m-b-20">
							<form name="loginForm" id="loginForm" action="${ctx}/logon"
								method="post">
								<div class="form-group">
									<input class="form-control" name="username" type="text"
										placeholder="用户名">
								</div>
								<div class="form-group">
									<input class="form-control" name="password" type="password"  autocomplete="off" placeholder="密码" >
								</div>
								<div class="form-group">
									<button class="btn btn-block btn-round btn-brand" type="submit"
										style="font-size: 18px;" href="javascript:doSubmit();">登录</button>
								</div>
								<div style="display: none;">
									<select name="logicSysNo" id="logicSysNo"
										style="width: 162px; height: 26px; font-size: 2.375rem;"
										ltype="select"></select>
								</div>
							</form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>