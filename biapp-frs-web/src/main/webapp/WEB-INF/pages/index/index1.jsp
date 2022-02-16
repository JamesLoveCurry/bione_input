<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript"
	src="${ctx}/js/jquery/jquery.accordion.min.js"></script>
<script type="text/javascript" src="${ctx}/js/mbMenu/mbMenu1.js"></script>
<script type="text/javascript"
	src="${ctx}/js/soundManager/soundmanager2.js"></script>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/ligerui-custom.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/index.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/mbMenu/menu.css" title="styles1"  media="screen">
<title>${logicSysName}</title>

<SCRIPT type="text/javascript">
	//专门存放客户端环境变量,经常遇到在UI端计算要登录人员的名称,角色,是否超级管理员等信息,有的还是在弹出N层的iframe中要取这些值,只有window.top最方便! 与localStorage的区别是关闭窗口就没了,即不需要持久化。
	//有时不同页面之间在UI端交换信息，也可借用这个对象。如果使用window.parent,会遇到一个jsp如果既是主页面，也想当成一个弹出窗口时,会遇到window.parent不一致的情况,而window.top则比较简洁,取完后可以立即删除.
	var clientEnv = {};
	window.clientEnv["userId"]="${userId}";
	window.clientEnv["userName"]="${userName}";

	var searchInfo ="";
	var tabObj = null;
	var sidebarWidth = 200;
	var contentWidthOpen;
	var contentWidthClose;
	//侧边栏是否可见
	var sidebarVisible = false;
	//侧边栏当前状态
	var sidebarState = false;
	var defaultNo = "指标/报表/明细模板/明细模型";
	$(function() {
		$("#logicSysNo").change(function() {
			$.ligerDialog.confirm('将进行逻辑系统切换，是否继续？', function(yes) {
				if (yes) {
					var logicSysNo = $(this).children('option:selected').val();
					$("#changeLogin").submit();
				} else {
					$("#logicSysNo").val("${logicSysNo}");
				}
			});
		});
		window.moveTo(0, 0);
		window.resizeTo(window.screen.width, window.screen.height);
		//初始化头部菜单
		$("#menu").buildMenu({
			template : "menuvVoices.html",
			additionalData : "pippo=1",
			menuWidth : 150,
			openOnRight : false,
			rootMenuSelector: ".rootVoices",
			menuSelector : ".menuContainer",
			containment : "menu",
			iconPath : "${ctx}/",
			iconBlankPath : "${ctx}/images/classics/icons/",
			hasImages : true,
			menuLeft : 0,
			fadeInTime : 100,
			fadeOutTime : 300,
			adjustLeft : 2,
			minZindex : "auto",
			adjustTop : 10,
			opacity : .95,
			shadow : true,
			openOnClick : false,
			closeOnMouseOut : true,
			closeList: false,
			closeAfter : 500,
			hoverIntent : 0
		});
		var $sidebarbtn = $('#open-close-sidebar');
		var delaytime = 500;
		$sidebarbtn.bind("click", function() {

			if (sidebarVisible) {
				hideSideBar(delaytime);
			} else {
				showSideBar(delaytime);
			}

		});

		showWindow();
		showPwdOverdueTip(); // 弹出密码过期的提示窗口
		BIONE.polling(3600000);//轮询时间间隔较小，仅供测试用
		initTab();
		initMsgTip();//初始化是否有新消息提示
		$("#searchElement").val(defaultNo);
		$("#searchElement").addClass("colPosition");
		$("#searchElement").addClass("l-text-field-null").val(defaultNo).attr("isNull","true");
		
		//综合搜索框相应样式
		$("#searchElement").bind("focusin" , function(){
			if($("#searchElement").attr("isNull") == "true"){
				$("#searchElement").removeClass("l-text-field-null").val("");
				$("#searchElement").removeClass("colPosition");
			}
		});
		$("#searchElement").bind("focusout" , function(){
			if($("#searchElement").val() != null
					&& $("#searchElement").val() != ""){
				$("#searchElement").attr("isNull","false");
			}else{
				$("#searchElement").addClass("l-text-field-null").val(defaultNo).attr("isNull","true");
				$("#searchElement").addClass("colPosition");
			}
		});
		
		slideMenu();//滑动菜单函数
	});
	function showWindow() {
		//==================处理页面整体布局==================
		var $mainContent = $("#mainContent");
		var $sidebar = $("#sidebar");
		var $sidebarbtn = $('#open-close-sidebar');
		var $sidebarbutton = $("#sidebarbutton");
		var $content = $("#content");
		var sidebarbuttonWidth = 12;
		var $contentFrame = $("#contentFrame");
		var mainContentHeight = 0;
		var $header = $("#header");
		var $navi = $("#navi");
		//设置mainContent DIV的高度
		mainContentHeight = $(window).height() - $header.height();
				/* - $navi.height(); */
		$mainContent.height(mainContentHeight);
		$mainContent.css('height', mainContentHeight);
		$sidebar.css('height', mainContentHeight - 10);
		$sidebarbutton.css('height', mainContentHeight);
		$("#open-close-sidebar").css('top', mainContentHeight / 2 - 20);
		$content.css("height", mainContentHeight - 8);
		$contentFrame.height(mainContentHeight - 8);
		//设置content DIV的宽度
		contentWidthOpen = $mainContent.width() - sidebarWidth;
		contentWidthClose = $mainContent.width();
		if (sidebarState) {
			$content.width(contentWidthOpen);
		} else {
			$content.width(contentWidthClose);
		}
	}
	window.onresize = function() {
		showWindow();
	};
	
	function initTab(){
		tabObj = $("#tab").ligerTab({
			contextmenu : true,
			onClose : function(tabId){
				if(tabId == "colSearchWin"){
					window.searchInfo = "";
				}
			}
		});
		if("${indexId}"!=""){
			addTabInfo("${indexId}","${indexNm}","${indexUrl}")
		}
		else{
			addTabInfo("1","首页","${indexUrl}")
		}
		$("body").on("click",function(){
			tabObj.tab.menu.hide();
		});
	}
	
	function addTabInfo(menuId,menuNm,url){
		if (tabObj.isTabItemExist(menuId)) {
			tabObj.selectTabItem(menuId);
		}
		else{
			tabObj.addTabItem({
				tabid : menuId,
				text : menuNm,
				showClose : true,
				content : "<div id='" + menuId
						+ "' style='height: "+($("#content").height()-30)+"px;width:100%;'></div>"
			});
			var content = "<iframe frameborder='0' id='"
					+ menuId
					+ "' name='"
					+ menuNm
					+ "' style='height:100%;width:100%;' src='${ctx}"+url+"'></iframe>";
			$("#" + menuId).html(content);
		}
	}

	/*
	 * 响应菜单点击事件
	 * menuId:菜单Id
	 * menuName:菜单Id
	 * url:导航路径
	 * isFirstLevel:是否是第一层菜单
	 */
	function doMenuClick(menuId, menuName, url, isFirstLevel) {
		if(menuId!=null&&menuId!=""){
			$.ajax({
				type : "POST",
				url : "${ctx}/bione/syslog/func/saveLog",
				dataType : 'json',
				data : {'menuId':menuId},
				success : function() {}
			});
		}
		/* BIONE.ajax(({
			url : '${ctx}/bione/admin/log/addLog',
			data : 'logEvent=' + encodeURI('用户点击' + menuName + '菜单')
		}), function() {

		}); */
		$("#navPathName").html(menuName);
		if (url && url != 'null' && url != "") {
			addTabInfo(menuId,menuName,url);
		} 
	}

	var permissionDataLoadError = function() {
		BIONE.tip('加载权限数据 失败,请联系管理员!');
	};

	//-----------获取全局的操作权限信息-----------------
	// 受系统权限控制的操作标识(按钮)
	$.ajax({
		cache : false,
		async : true,
		url : '${ctx}/bione/permission/getProtectedResOperNo.json',
		dataType : 'json',
		type : 'get',
		success : function(result, textStatus, jqXHR) {
			if (!result)
				return;

			if (result.success) {
				window['protectedResOperNo'] = result.data;
			} else {
				permissionDataLoadError();
			}
		},
		error : function(result, textStatus, errorThrown) {
			permissionDataLoadError();
		}
	});
	
	//初始化是否有新消息提示
	function initMsgTip(){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/msgNoticeLog/initMsgTip.json?d='+new Date().getTime(),
			dataType : 'json',
			type : 'get',
			success : function(data) {
				if(data.msgSts == true){
					$("#newMsg").show();
					$("#newMsg").text(data.msgNum);
/* 					window.top.$("#msg span").attr('class', 'new');
					window.top.$("#msg span").html('<B>有新消息</B><a style="color:red">('+data.msgNum+')</a>'); */
				}else{
					$("#newMsg").hide();
/* 					window.top.$("#msg span").attr('class', 'old');
					window.top.$("#msg span").html("消息"); */
				}
			},
			error : function(result, textStatus, errorThrown) {
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
	// 当前用户有权限访问的操作标识(按钮)
	$.ajax({
		cache : false,
		async : true,
		url : '${ctx}/bione/permission/getAuthorizedResOperNo.json',
		dataType : 'json',
		type : 'get',
		success : function(result, textStatus, jqXHR) {
			if (!result)
				return;

			if (result.success) {
				window['authorizedResOperNo'] = result.data;
			} else {
				permissionDataLoadError();
			}
		},
		error : function(result, textStatus, errorThrown) {
			permissionDataLoadError();
		}
	});

	// 修改当前用户头像
	function headIcon() {
		BIONE.commonOpenDialog("头像选择", "headIcon", 500, 350,
				"${ctx}/bione/admin/user/buildHeadIconList");
	}

	// 修改当前用户密码
	function updatePwd() {
		BIONE.commonOpenDialog("修改密码", "userManage", 497, 320,
				"${ctx}/bione/admin/user/updateCurPwd");
	}

	// 消息
	function messageInfo() {
		var d = BIONE.commonOpenDialog("消息查看", "messageInfo();", 800, 530,
				"${ctx}/bione/msg/announcement/showMsg");
		$(".l-dialog-close").bind("click", function() {
			BIONE.refreshMsg(null, false);
			$(".l-dialog-close").unbind("click");
		});
		//d.close();

	}
	// 帮助手册
	function useHelp() {
		//alert("使用帮助");
	}

	// 关于
	function about() {
		BIONE.commonOpenDialog("关于", "About", 400, 250,"${ctx}/bione/admin/logicSys/about");
	}
	
	//退出系统
	function quit() {
		logout();
	}
	 function CloseWebPage() {  
	        if (navigator.userAgent.indexOf("MSIE") > 0) {  
	            if (navigator.userAgent.indexOf("MSIE 6.0") > 0) {  
	                window.opener = null; window.close();  
	            }  
	            else {  
	                window.open('', '_top'); window.top.close();  
	            }  
	        }  
	        else if (navigator.userAgent.indexOf("Firefox") > 0) {  
	            window.location.href = ' ';  
	        }  
	        else {  
	            window.opener = null;   
	            window.open(' ', '_self', '');  
	            window.close();  
	        }  
	    }  
	//注销系统
	function logout() {
		$.ligerDialog.confirm('您确定要注销吗', function(type) {
			if (type) {
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
		});
	}
	/*密码过期，弹出提示框*/
	function showPwdOverdueTip() {
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/admin/pwsec/getOverdue.json',
			dataType : 'json',
			type : 'get',
			success : function(result, textStatus, jqXHR) {
				if (!result) {
					return;
				}
				if (result.success && result.success == "true") {
					var isOverdue = result.data;
					if (isOverdue == "1") {
						$.ligerDialog.confirm('密码使用时间已超出有效期，<br/>请修改密码',
								function(type) {
									if (type) {
										updatePwd();
									}
								});
					}
				} else {
					BIONE.tip(result.msg);
				}
			},
			error : function(result, textStatus, errorThrown) {
				BIONE.tip("获取用户密码已使用过的时间发生异常！")
			}
		});
	}
	
	function oldUdas(){
		window.open("http://10.232.84.18:18123/market");
	}
	
	// 首页-综合搜索-跳转
	function colSearchButClick(){
		var data = encodeURI($("#searchElement").val());
		if(data == encodeURI(defaultNo)){
			data = "";
		}
		if(data != searchInfo){
			searchInfo = data;
			if(searchInfo != "" ){
				tabObj.removeTabItem("colSearchWin");
				//window.searchInfo = data;
				addTabInfo("colSearchWin","综合搜索","/rpt/frame/colsearch?searchNm="+searchInfo);
			}
		}
		//BIONE.commonOpenDialog("综合搜索", "colSearchWin", 1200, 650,"${ctx}/rpt/frame/colsearch?searchNm="+data);
	}
	
	//滑动菜单函数
	function slideMenu(){
		var menuWidth = window.innerWidth -80;
		$("#menu").css("width",menuWidth);
		
		var frameMenuWidth = $("#frameMenu").width();
		var menuWidth = $("#menu").width();
		
		if(frameMenuWidth > menuWidth){//如果菜单有超出
			var menuRightList = [];//右侧菜单项
			var menuLeftList = [];//左侧菜单项
			var table = document.getElementById("frameMenu").getElementsByTagName("tbody")[0].getElementsByTagName("tr")[0];
			var tdNum = table.childElementCount;
			for(var i = tdNum - 1; i > 0; i--){//精简菜单到可以完整显示
				var row = table.getElementsByTagName("td")[i];
				menuRightList.push(row);
				table.removeChild(row);
				frameMenuWidth = $("#frameMenu").width();
				if(frameMenuWidth < menuWidth){//精简完毕
					break;
				}				
			}
			$("#frameMenuLeft").click(function(){//左侧按钮点击事件
				frameMenuWidth = $("#frameMenu").width();
				if((menuRightList.length > 0) || (frameMenuWidth > menuWidth)){
					var table = document.getElementById("frameMenu").getElementsByTagName("tbody")[0].getElementsByTagName("tr")[0];
					var reRow = table.getElementsByTagName("td")[0];
		            table.removeChild(reRow);//删除一个左侧菜单节点
		            menuLeftList.push(reRow);
		            if(menuRightList.length > 0){//多一个判断是为了避免最后一个菜单节点显示不全的问题
						var apRow = menuRightList[menuRightList.length - 1];
						table.appendChild(apRow);//添加一个右侧菜单节点
						menuRightList.remove(menuRightList.length - 1);
		            }
				}
			});
			$("#frameMenuRight").click(function(){//右侧按钮点击事件
				if(menuLeftList.length > 0){
					var table = document.getElementById("frameMenu").getElementsByTagName("tbody")[0].getElementsByTagName("tr")[0];
					var reRow = table.getElementsByTagName("td")[table.childElementCount - 1];
					menuRightList[menuRightList.length] = reRow;
		            table.removeChild(reRow);//删除一个右侧菜单节点
					var apRow = menuLeftList[menuLeftList.length - 1];
					var firstChild = table.firstChild;
					table.insertBefore(apRow,firstChild);//添加一左右侧菜单节点
					menuLeftList.remove(menuLeftList.length - 1);
				}
			});
			$('.frameMenuLeft').width(0);
		}else{
			$("#frameMenuLeft").removeClass('icon-back');
			$("#frameMenuRight").removeClass('icon-enter');
		}
	}
	
	Array.prototype.remove=function(dx){
		if(isNaN(dx)||dx>this.length){
			return false;
		}
		for(var i=0,n=0;i<this.length;i++){
			if(this[i]!=this[dx]){
				this[n++]=this[i]
			}
		}
		this.length-=1
	}
	
</SCRIPT>
<style>
	.menuMain{
		position: relative;
    	left: 0px;
	}
	.colPosition{
	    text-align: center;
	}
	/*新增*/
	.l-tab-links ul{left:5px}
</style>
</head>
<body>
	<div id="header">
		<div class="sh-rpt-headBar">
		<div class="sh-rpt-logo" style="position: absolute;"title="统一指标监管报送平台"><span style="font-size:24px;color:#333;font-weight: normal">统一指标监管报送平台</span></div>
			<div class="sh-rpt-headTools" style="width:610px;">
				<div class="b-search icon-search-01"></div>
			    <input id="searchElement" type="text" class="l-text" style="width:195px;padding-left:35px;padding-right: 5px;border-radius:20px;height:30px;background-color:#cacaca;position:relative"  onfocusout="colSearchButClick();" onkeydown="if(event.keyCode==13){colSearchButClick();return false;}" />
			    <%-- <a id="colSearch" style="padding-left:20px;position:relative;background:url(${ctx}/images/classics/icons/find.png) no-repeat 50% 50% transparent;" onclick="colSearchButClick()"></a> --%>
			  	<div class="userInfoA" style="padding:0 5px"><span class="userInfo icon-user"></span>${userInfo}</div>
			   <%--  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			    ${userInfo} --%>
				<a id="msg" href="javascript: messageInfo();" title="消息" class="icon-message"><i id="newMsg" style = "display: none;"></i></a>
				<a href="javascript: updatePwd();" title="修改密码" class="icon-password"></a>
				<a href="javascript:quit();" title="注销" class="icon-out"></a>
				<%-- <a href="javascript:about();" title="关于"><img src="${ctx}/images/classics/icons/about.gif" />关于</a> --%>							
			</div>
		</div>
		<div id="frameMenuLeft" class="icon-back"></div>
		<div id="menu" >${menuInfoHTML}</div>
		<div id="frameMenuRight" class="icon-enter"></div>
	</div>
	<div id="mainContent">
		<div id="content">
			<div id="tab" style="width: 100%; overflow: hidden;">
			</div>
		</div>
	</div>
</body>
</html>
