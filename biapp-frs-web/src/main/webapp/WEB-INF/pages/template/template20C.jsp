<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template20B.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/ligerui-custom.css" />
<sitemesh:write property='head' />
<style type="text/css">
</style>
<script type="text/javascript">
var onToggle = null;
function initFrame() {
	var $main = $('#main');
	onToggle = $.Callbacks();
	$main.css('top', 22);
	$('#togglebtn').unbind('click');
	$('#togglebtn').bind('click', {
		flag: true
	}, function(e) {
		if (e.data.flag == true) {
			e.data.flag = false;
			$(this).removeClass("icon-up");
			$(this).addClass("icon-down");
			$main.animate({
				top: '22px'
			}, 'fast', function() {
				onToggle.fire();
			});
		} else {
			e.data.flag = true;
			$(this).removeClass("icon-down");
			$(this).addClass("icon-up");
			$main.animate({
				top:  $('#searchbox').height() + 24 + 2
			}, 'fast', function() {
				onToggle.fire();
			});
		}
	});
}
$(function() {
	initFrame();
});
</script>
</head>
<body>
  <div id="center">
    <div id="top" class="top">
      <div>
      	<div class="searchtitle" style="height: 24px;position: relative;">
			<i class = "icon-search search-size" ></i>
			<sitemesh:div property='template.toggle' />
			<sitemesh:div property='center.top' />
			<i id="togglebtn" class="togglebtn icon-up"></i>
		</div>
		<div id="searchbox" class="searchbox" style="width: auto; margin: 0; position: relative;">
			<sitemesh:div property='template.form' />
			<div id="searchbtn" class="searchbtn" style="height: 30px;"></div>
		</div>
      </div>
    </div>
    <div id="main" class="main">
    	<div id='frameBorder' class="border">
    		<sitemesh:div property='template.frame' />
    	</div>
    </div>
  </div>
</body>
</html>