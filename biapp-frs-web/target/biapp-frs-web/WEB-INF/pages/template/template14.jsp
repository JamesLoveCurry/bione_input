<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap3/css/bootstrap.min.css" />
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template14.css" />
<style>
* {
  -webkit-box-sizing: content-box;
  -moz-box-sizing: content-box;
  box-sizing: content-box;
}
#center{
	background-color : #FCFCFC;
}
.l-group span {
    float: left;
    width: 200px;
}
.wrapper {
    background: #FFFEE6;
    color: #333;
    font-size: 12px;
    margin: -30px 100px 10px 120px;
    padding: 5px 10px;
    position: fixed;
    text-align: left;
    z-index: 1000;
    border-radius: 6px;
}
img{
	padding: 0 8px;
}
.buttentitle{
	font-family: '微软雅黑',Arial;
	color: #23c6c8;
	font-size: 16px;
}
.oper-div{
	float: left;
	border-right: 2px solid #c5c5c5;
	height: 80px;
	padding: 10px;
	margin: 5px 0px;
}
.oper-font{
	padding: 5px 0px;
	text-align: center;
	color: #8a8a8a;
}
.div-desc{
	clear: both; text-align: center; color: #8a8a8a; padding-top: 10px;
}
.searchtitle {
	margin: auto;
	display: inline-block;
	*display: inline;
	zoom: 1;
	width: 100%;
}

.searchtitle .togglebtn {
	position: absolute;
	background: url("../../../images/classics/ligerui/toggle.gif") no-repeat 0px 0px;
	top: 5px;
	width: 9px;
	height: 10px;
	right: 50px;
	cursor: pointer;
	font-size: 0;
	margin: 0;
	padding: 0;
	border: 0;
}

.searchtitle .togglebtn-down {
	background-position: 0px -10px;
}

.searchbox {
	width: 100%;
	text-align: center;
	margin: auto;
	border-style: solid;
	border-width: 1px;
	border-color: #D6D6D6;
}

.searchbutten{
	border: 1px solid transparent;
	border-radius: 10px;
	border-color: #ddd;
	float: left;
	position: relative;
	padding-top: 1px;
	padding-bottom: 1px;
	padding-right: 10px;
	padding-left: 10px;
	margin-right: 7px;
}

.buttentitle{
	font-family: '微软雅黑',Arial;
	color: #23c6c8;
	font-size: 16px;
}

.search-all, .search-all > input[type="text"]:focus{
	height: 24px;
	border: 2px solid #cdcdcd;
	border-radius: 10px;
	outline: 0;
	max-width: 200px;
}

.rpt-config{
	float: left;
	margin: 10px 5px 0 5px;
	color: #3c8dbc;
	cursor: pointer;
}
.rpt-config2{
	float: left;
	margin: 10px 5px 0 5px;
	color: #8a8a8a;
	cursor: pointer;
}

</style>
<script type="text/javascript">
	$(function() {
		templateshow();
	});
	function templateshow(){
		var $content = $(window);
		var height = $content.height();
		$("#center").height(height - 10);
	}
</script>
<sitemesh:write property='head' />
</head>
<body style="padding-right: 0px;
	padding-left: 0px;
	padding-top: 0px;">
	<div id="center">
		<sitemesh:div property='template.center' />
	</div>
</body>
</html>
