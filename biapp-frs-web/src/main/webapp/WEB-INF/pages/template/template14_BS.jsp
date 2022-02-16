<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
	<!-- 工作区填充满样式，勿动 -->
    <style>
        html,body{ height: 100%!important;}
    </style>
    <%@ include file="/common/jquery_load_BS.jsp"%>
    <%@ include file="/common/ligerUI_load_BS.jsp"%>
    <%@ include file="/common/Bootstrap_load.jsp"%>
    <%@ include file="/common/zTree_load.jsp"%>
    <style>
        * {
            -webkit-box-sizing: content-box;
            -moz-box-sizing: content-box;
            box-sizing: content-box;
        }
        #center{
            background-color : #FCFCFC;
        }
    </style>
    <sitemesh:write property='head' />
</head>
<body>
<div id="center" style="height: 100%">
    <sitemesh:div property='template.center' />
</div>
</body>
</html>
