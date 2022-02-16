<%@page import="com.itextpdf.text.log.SysoCounter"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html lang="zh">
<head>
	<%@ include file="/common/meta.jsp"%>
	<%@ include file="/common/jquery_load.jsp"%>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<meta name="description" content="">
	<meta name="author" content="">
	<title>宇信科技统一监管报送平台-登录</title>
	<!-- Web Fonts-->
	<link href="${ctx}/static/theme/Boomerang/poppins.css" rel="stylesheet">
	<link href="${ctx}/static/theme/Boomerang/all.css" rel="stylesheet">
	<!-- Plugins-->
	<link href="${ctx}/static/theme/Boomerang/plugins.min.css" rel="stylesheet">
	<!-- Template core CSS-->
	<link href="${ctx}/static/theme/Boomerang/template.css" rel="stylesheet">
	<style id="fit-vids-style">
		.fluid-width-video-wrapper{width:100%;position:relative;padding:0;}.fluid-width-video-wrapper iframe,.fluid-width-video-wrapper object,.fluid-width-video-wrapper embed {position:absolute;top:0;left:0;width:100%;height:100%;}
	</style>
	<style type="text/css" id="jarallax-clip-0">
		#jarallax-container-0 { clip: rect(0 1195.3333740234375px 606px 0); clip: rect(0, 1195.3333740234375px, 606px, 0);}
	</style>
	<script type="text/javascript">
        $(document).ready(function() {
            //判断是否首次进入登录页
            $("#loginForm").validate({
                rules: {
                    username: {
                        required: true,
                        maxlength: 100
                    },
                    password: {
                        required: true,
                        maxlength: 15
                    }
                },
                messages: {
                    username: {
                        required: "用户名不允许为空!",
                        maxlength: jQuery.format("用户名最多允许输入 {0} 字符!")
                    },
                    password: {
                        required: "密码不允许为空!",
                        maxlength: jQuery.format("密码最多允许输入 {0} 字符!")
                    }
                }
            });

            $.ajax({
                cache: false,
                async: true,
                url: "${ctx}/getLogicSysOption",
                dataType: "json",
                success: function(item) {
                    for(var i in item) {
                        var option = "<option value='" + item[i].logicSysNo + "'>" + item[i].logicSysName + "</option>";
                        $("#logicSysNo").append(option);
                    }

                },
                error: function(result, b) {}
            });

            //监控回车事件,提交表单
            document.onkeydown = function(e) {
                var ev = document.all ? window.event : e;
                if(ev.keyCode == 13) {
                    doSubmit();
                }
            };

            //使登录窗口居中(解决不同版本IE浏览器表格高度计算问题)
            //$("#loginFormTd").css("height", $(document).height() - 50);
        });

        //提交单进行登录
        function doSubmit() {
            //验证用户名、密码是否填写完整
            var valid = $("#loginForm").valid();
            if(valid) {
                document.loginForm.submit();
            }

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
		String className = "font_tip_error";
		String tip = "";
		if (error != null) {
			tip = "您尚未登录或者会话超时，请重新登录.";
		} else if (loginFailInfo != null) {
			tip = loginFailInfo;
		} else {
			tip = "请输入用户名和密码";
			className = "font_tip";
		}
		tip = "<span class='" + className + "'>" + tip + "</span>";
	%>
</head>
<body>

<!-- Preloader-->
<div class="page-loader">
	<div class="page-loader-inner">
		<div class="spinner">
			<div class="double-bounce1"></div>
			<div class="double-bounce2"></div>
		</div>
	</div>
</div>
<!-- Preloader end-->

<!-- Header-->
<header class="header header-transparent">
	<div class="container-fluid">
		<!-- Brand-->
		<div class="inner-header"><a class="inner-brand" href="#"><img src="${ctx}/static/img/logo/logo.png" alt=""></a></div>
		<!-- Navigation-->
		<div class="inner-navigation collapse"></div>
		<div class="extra-nav"></div>
	</div>
</header>
<!-- Header end-->

<!-- Wrapper-->
<div class="wrapper">
	<section class="module-cover parallax fullscreen text-center" data-background="${ctx}/static/theme/Boomerang/module-5.jpg" data-overlay="0.65" data-gradient="" style="background-image: none; z-index: 0;" data-jarallax-original-styles="background-image: url(&quot;${ctx}/static/theme/Boomerang/module-5.jpg&quot;);">
		<div class="container">
			<div class="row">
				<div class="col-lg-4 col-md-6 m-auto">
					<div class="m-b-20">
						<h4>统一监管报送平台</h4>
						<h5>超级管理员系统</h5>
						<h6><%=tip%></h6>
					</div>
					<div class="m-b-20">
						<form name="loginForm" id="loginForm" action="${ctx}/logon" method="post">
							<div class="form-group">
								<input class="form-control" name="username" value="bione_super" type="text" placeholder="用户名">
							</div>
							<div class="form-group">
								<input class="form-control" name="password" type="password" autocomplete="off" placeholder="密码" >
							</div>
							<li style="display: none;"><input name="logicSysNo" id="logicSysNo" value="${logicSysNo}" type="hidden"/></select></li>
							<div class="form-group">
								<button class="btn btn-block btn-round btn-brand" type="submit" style="font-size: 18px;" href="javascript:doSubmit();">登录</button>
							</div>
							<div style="display: none;">
								<select name="logicSysNo" id="logicSysNo" style="width: 162px; height: 26px" ltype="select"></select>
							</div>
						</form>
					</div>
					<div class="m-b-20"></div>
				</div>
			</div>
		</div>
	</section>

	<!-- Footer-->
	<footer class="footer">
		<div class="footer-widgets">
			<div class="container">
				<div class="row">
					<div class="col-md-12">
						<!-- Text widget-->
						<aside class="widget widget-text">
							<div class="widget-title">
								<h5>关于我们</h5>
							</div>
							<div class="textwidget">
								<p>北京宇信科技集团股份有限公司是国内规模最大的银行IT解决方案提供商之一，主要从事向以银行为主的金融机构提供包括咨询、软件产品、软件开发和实施、运营维护、系统集成等信息化服务。公司总部位于北京，在全国拥有26家控股子公司和9家参股企业，集团员工总人数达6800余人。<br>据IDC报告显示，自2010年起公司已连续7年在中国银行业IT解决方案市场排名第一，在网络银行、信贷管理、商业智能、风险管理领域的均处于领先地位。同时，公司在客户关系管理、移动金融、呼叫中心、柜台交易以及系统增值服务等领域也拥有业界领先的产品并保持着强劲的增长势头，是中国银行业IT解决方案市场中的领军者，是中国银行IT业产品种类全，专业化程度高，具有品牌影响力的企业之一。</p>
								<p>
									地址: 北京市朝阳区酒仙桥东路9号院电子城研发中心A2楼东5层<br>
									E-mail: support@yusys.com<br>
									Tel: 010-59137700<br>
								</p>
							</div>
						</aside>
					</div>
				</div>
			</div>
		</div>
		<div class="footer-bar">
			<div class="container">
				<div class="row">
					<div class="col-md-12">
						<div class="copyright">
							<p>© 2018 Yusys, All Rights Reserved.</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</footer>
	<!-- Footer end-->
</div>
<!-- Wrapper end-->
<script type="text/javascript">
    if(parent.frames[0]) {
        //如果当前页面是在iframe中，则在父页面中打开
        parent.location = window.location;
    }
</script>

<!-- To top button-->
<a class="scroll-top" href="#top" style="font-size: 11px;">返回</a>

<!-- Scripts-->
<script src="${ctx}/static/theme/Boomerang/popper.min.js"></script>
<script src="${ctx}/static/theme/Boomerang/bootstrap.min.js"></script>
<script src="${ctx}/static/theme/Boomerang/plugins.min.js"></script>
<script src="${ctx}/static/theme/Boomerang/custom.min.js"></script>
</body>
</html>