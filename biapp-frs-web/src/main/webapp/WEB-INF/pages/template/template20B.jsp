<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap3/css/bootstrap.min.css" />
<style>
* {
  -webkit-box-sizing: content-box;
  -moz-box-sizing: content-box;
  box-sizing: content-box;
}
#center{
	background-color : #F1F1F1;
}
</style>
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
		</div>
		<div id="searchbox" class="searchbox" style="width: auto; margin: 0; position: relative;">
			<sitemesh:div property='template.form' />
			<div id="searchbtn" class="searchbtn" style="height: 30px;"></div>
		</div>
      </div>
    </div>
   <!--  <div id="tool"  style="position: relative;">
    	<div id="searchbox" class="searchbox" style="width: auto; margin: 0; position: relative;">
    	<sitemesh:div property='template.tool' />
    	</div>
    </div> -->
    <div id="main" class="main"> 
    	<div id='frameBorder' class="border" >
    		<sitemesh:div property='template.frame' />
    	</div>
    </div>
  </div>
</body>
</html>