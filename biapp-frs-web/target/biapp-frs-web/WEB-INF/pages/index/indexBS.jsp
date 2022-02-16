<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/common/meta.jsp"%>
    <%@ include file="/common/jquery_load.jsp"%>
    <%@ include file="/common/ligerUI_load_BS.jsp"%>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>宇信科技 | 统一监管报送平台</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.6 -->
    <link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/AdminLTE.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/skins/all-skins.min.css">

    <link rel="stylesheet" type="text/css" href="${ctx}/images/classics/icons/icon/style.css" />

    <link rel="stylesheet" type="text/css"  href="${ctx}/static/css/index_user_change.css">
	<link rel="icon" href="data:image/ico;base64,aWNv">
    <style type="text/css">
        html {
            overflow: hidden;
        }
    </style>

    <SCRIPT type="text/javascript">
        var ctx ="${ctx}";
        var isFirst = "${isFirst}";
        var clientEnv = {};
        window.clientEnv["userId"]="${userId}";
        window.clientEnv["userName"]="${userName}";
        // 修改当前用户密码
        function updatePwd() {
            BIONE.commonOpenDialog("修改用户信息", "userManage", 497, 320,
                "${ctx}/bione/admin/user/updateCurPwd");
        }

        //退出系统
        function logout() {
            $.ajax({
                cache : false,
                async : false,
                url : "${ctx}/logout",
                type : 'get',
                success : function(result) {
                    window.location.href = "${ctx}/login";
                }
            });
        }

        // 消息
        function messageInfo() {
          BIONE.commonOpenDialog("消息查看", "messageInfo();", 800, 530,
                "${ctx}/bione/msg/announcement/showMsg");
        }

        /**
		* 响应菜单点击事件
         * menuId:菜单Id
         * menuName:菜单Id
         * url:导航路径
         * isFirstLevel:是否是第一层菜单
         */
        function doMenuClick(menuId, menuName, url, isFirstLevel) {
            url = "${ctx}" + url;
            addTabs({
                id: menuId,
                title: menuName,
                close: true,
                url: url,
                urlType: "absolute"
            });
        }
        
    </SCRIPT>

</head>
<body class="hold-transition skin-blue-light sidebar-mini fixed">
<div class="wrapper">

    <header class="main-header">
        <!-- Logo -->
        <a class="logo">
            <!-- mini logo for sidebar mini 50x50 pixels -->
            <span class="logo-mini"><b>宇信科技</b></span>
            <!-- logo for regular state and mobile devices -->
            <span class="logo-lg"><b>统一监管报送平台</b></span>
        </a>
        <!-- Header Navbar: style can be found in header.less -->
        <nav class="navbar navbar-static-top">
            <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </a>

            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <!-- Messages: style can be found in dropdown.less-->
                    <li class="dropdown messages-menu" onclick="javascript: messageInfo();">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            <i class="fa fa-envelope-o"></i>
                        </a>
                    </li>
                    <li class="dropdown user user-menu">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            <img id="user_image_top" src="${ctx}${userIcon}" class="user-image" alt="User Image">
                            <span class="hidden-xs">${userName}</span>
                        </a>
                        <ul class="dropdown-menu">
                            <!-- User image -->
                            <li class="user-header">
                                <img id="user_image_right" src="${ctx}${userIcon}" class="img-circle" alt="User Image">
                                <p>
                                    ${userName}
                                    <small>所属机构：${userOrgName}<br>最近更新于 ${lastUpdateTime}</small>
                                </p>
                            </li>
                            <!-- Menu Body -->

                            <!-- Menu Footer-->
                            <li class="user-footer">
                                <div class="pull-left">
                                    <a href="javascript: updatePwd();" class="btn btn-default btn-flat">修改用户信息</a>
                                </div>
                                <div class="pull-right">
                                    <a href="javascript:logout();" class="btn btn-default btn-flat">退出系统</a>
                                </div>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>
        </nav>
    </header>
    <!-- Left side column. contains the logo and sidebar -->
    <aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
            <!-- Sidebar user panel -->
            <div class="user-panel">
                <div class="pull-left image">
                    <img id="user_image_left" src="${ctx}${userIcon}" class="img-circle" alt="User Image">
                </div>
                <div class="pull-left info">
                    <p>${userName}</p>
                    <a href="#"><i class="fa fa-circle text-success"></i> 在线</a>
                </div>
            </div>
            <!-- search form -->
            <form action="#" method="get" class="sidebar-form">
                <div class="input-group">
                    <input type="text" name="q" class="form-control" placeholder="">
                    <span class="input-group-btn">
                <button type="button" name="search" id="search-btn" class="btn btn-flat" onclick="search_menu()" ><i
                        class="fa fa-search"></i>
                </button>
              </span>
                </div>
            </form>
            <!-- /.search form -->
            <!-- sidebar menu: : style can be found in sidebar.less -->
            <ul class="sidebar-menu" >
                <%--<li class="header">功能菜单</li>--%>
                <%--${menuInfoHTML}--%>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper" id="content-wrapper" style="min-height: 421px;">
        <!--bootstrap tab风格 多标签页-->
        <div class="content-tabs">
            <button class="roll-nav roll-left tabLeft" onclick="scrollTabLeft();">
                <i class="fa fa-backward"></i>
            </button>
            <nav class="page-tabs menuTabs tab-ui-menu" id="tab-menu">
                <div class="page-tabs-content" style="margin-left: 0px;">
				
                </div>
            </nav>
            <button class="roll-nav roll-right tabRight" onclick="scrollTabRight();">
                <i class="fa fa-forward" style="margin-left: 3px;"></i>
            </button>
            <div class="btn-group roll-nav roll-right">
                <button class="dropdown tabClose" data-toggle="dropdown">
                    页签操作<i class="fa fa-caret-down" style="padding-left: 3px;"></i>
                </button>
                <ul class="dropdown-menu dropdown-menu-right" style="min-width: 128px;">
                    <li><a class="tabReload" href="javascript:refreshTab();">刷新当前</a></li>
                    <li><a class="tabCloseCurrent" href="javascript:closeCurrentTab();">关闭当前</a></li>
                    <li><a class="tabCloseAll" href="javascript:closeOtherTabs(true);">全部关闭</a></li>
                    <li><a class="tabCloseOther" href="javascript:closeOtherTabs();">除此之外全部关闭</a></li>
                </ul>
            </div>
            <button class="roll-nav roll-right fullscreen" onclick="App.handleFullScreen()"><i
                    class="fa fa-arrows-alt"></i></button>
        </div>
        <div class="content-iframe " style="background-color: #ffffff; ">
            <div class="tab-content " id="tab-content">
            
            </div>
        </div>
    </div>
    <!-- /.content-wrapper -->

    <footer class="main-footer" style="padding-top: 5px;padding-bottom: 5px">
        <div class="pull-right hidden-xs">
            <b>Version</b> 3.1.0
        </div>
        <strong>© 2018 Yusys, All Rights Reserved.</strong>
    </footer>

    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Create the tabs -->
        <ul class="nav nav-tabs nav-justified control-sidebar-tabs">
            <li><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-home"></i></a></li>
            <li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-gears"></i></a></li>
        </ul>
        <!-- Tab panes -->
        <div class="tab-content"></div>
    </aside>
    <!-- /.control-sidebar -->
    <!-- Add the sidebar's background. This div must be placed
         immediately after the control sidebar -->
    <div class="control-sidebar-bg"></div>
</div>
<!-- ./wrapper -->

<!-- Bootstrap 3.3.6 -->
<script src="${ctx}/static/AdminLTE-With-Iframe/bootstrap/js/bootstrap.min.js"></script>
<!-- Slimscroll -->
<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/slimScroll/jquery.slimscroll.min.js"></script>
<!-- FastClick -->
<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/fastclick/fastclick.js"></script>
<!-- AdminLTE App -->
<script src="${ctx}/static/AdminLTE-With-Iframe/dist/js/app.js"></script>
<!--tabs-->
<script src="${ctx}/static/AdminLTE-With-Iframe/dist/js/app_iframe.js"></script>
<!--更换头像-->
<script src="${ctx}/static/js/index_user_change.js"></script>

<script type="text/javascript">
    /**
	* 本地搜索菜单
     */
    function search_menu() {
        //要搜索的值
        var text = $('input[name=q]').val();
        if(text.trim().length==0) return false;
        var $ul = $('.sidebar-menu');
        try{
        	$ul.find("a.nav-link").each(function () {
                var $a = $(this).css("border", "");
                //判断是否含有要搜索的字符串
                if ($a.children("span.menu-text").text().indexOf(text) >= 0) {
                    //点击该菜单
                    $a.click().css("border", "1px solid");
                    $a.parents("ul").css("display", "block");
                    throw new Error("ending");//搜索菜单出现多个时，只显示第一个，抛出异常结束foreach循环
                }
            });
        }catch(e){
        }
    }

    function find_menu(text){
        if(text.trim().length==0) return false;
        var $ul = $('.sidebar-menu');
        $ul.find("a.nav-link").each(function () {
            var $a = $(this).css("border", "");
            $a.parents("li").children("ul").css("display", "none");
        });

        $ul.find("a.nav-link").each(function () {
            var $a = $(this).css("border", "");
            //判断是否含有要搜索的字符串
            if ($a.children("span.menu-text").text().indexOf(text) >= 0) {
                //点击该菜单
                $a.css("border", "1px solid");
                $a.parents("ul").css("display", "block");
                $a.parents("li").children("ul").css("display", "block");
                return false;
            }
        });
    }

    function changePage(url, meunNm, name, funcNm) {
		$(".selected").removeClass("selected");
		$(".selected_next").removeClass("selected_next");
		$(".first_selected").removeClass("first_selected");
		var length = $("[id=rootVoiceContent]").length;
		for ( var i = 0; i < length; i++) {
			var $menu = $(window.parent.parent.$("[id=rootVoiceContent]")[i]);
			if ($menu.html() == meunNm) {
				$menu.addClass("selected");
				$menu.next().addClass("selected_next");
				$menu.trigger("click");
				break;
			}
		}
		$(".single-menu-item-icon").each(function() {
			if ($(this).next("span").html() == "&nbsp;&nbsp;" + funcNm) {
				$(this).parent().trigger("click");
			}
		})
		$("#navPathName").html("您的位置：" + name);
		$("#contentFrame").attr("src", url);

	}
    
    $(function () {
        App.setbasePath("${ctx}");
        App.setGlobalImgPath("/static/img/loading/");
        
        var menus1 = $.parseJSON('${menuInfoHTML}');
     	//查找首页的menuId
        addTabs({
            id: menus1[0].id,
            title: menus1[0].text,
            close: false,
            url: menus1[0].url,
            urlType: "relative"
        });

        App.fixIframeCotent();
        $('.sidebar-menu').sidebarMenu({data: menus1});
        if(isFirst == 'true'){
            commonOpenDialogNoX("修改密码", "userManage", 600, 420,
                "${ctx}/bione/admin/user/updateCurPwd?isFirst=1");
        }
    });

    function commonOpenDialogNoX(title, name, width,
                                 height, url, comboxName, beforeClose){
        var mheight = document.body.scrollHeight - 10;
        var mwidth = document.body.clientWidth+5;
        if(mwidth > 0 && width > mwidth){
            width = mwidth;
        }
        if(mheight>0 && height > mheight){
            height = mheight;
        }
        var _dialog = $.ligerui.get(name);
        if (_dialog) {
            $.ligerui.remove(name);
        }
        _dialog = $.ligerDialog.open({
            height : height,
            width : width,
            url : url,
            name : name,
            id : name,
            title : title,
            comboxName : comboxName,
            isResize : false,
            isDrag : true,
            isHidden : false,
            allowClose : false
        });
        if (beforeClose != null && typeof beforeClose == "function") {
            _dialog.beforeClose = beforeClose;
        }
        return _dialog;
    }

</script>

</body>
</html>