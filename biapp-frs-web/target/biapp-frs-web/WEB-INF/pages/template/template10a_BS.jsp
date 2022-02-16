<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/common/jquery_load_BS.jsp"%>
    <%@ include file="/common/ligerUI_load_BS.jsp"%>
    <%@ include file="/common/Bootstrap_load.jsp"%>
    <%@ include file="/common/zTree_load.jsp"%>
    <link rel="stylesheet" type="text/css"
          href="${ctx}/css/classics/template/template10.css" />
    <sitemesh:write property='head' />
    <!-- 工作区填充满样式，勿动 -->
    <style>
        html,body{ height: 98%!important;}
    </style>
</head>
<body>
<div id='all' style="height:100%">
    <div id="left" style="height:100%; background-color: #FFFFFF">
        <div id="lefttable" width="100%" border="0">
            <div width="100%"
                 style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
                <div width="8%"
                     style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
                    <sitemesh:div property='template.left.up.icon' />
                </div>
                <div width="20%">
					<span style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
						<sitemesh:div property='template.left.up' />
					</span>
                </div>
                <div width="70%" align="right" >
                    <sitemesh:div property='template.right.oper' />
                </div>
            </div>
        </div>
        <div id="treeToolbar"
             style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
        <div id="leftTreeContainer"
             style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
            <ul id="leftTree"
                style="font-size: 12; background-color: #FFFFFF; width: 92%"
                class="ztree"></ul>
        </div>
    </div>
    <div id="right" style="height:100%; background-color: #FFFFFF">
        <table id="righttable" width="100%" border="0" cellspacing="0"
               cellpadding="0">
            <tr>
                <table width="100%" height=30
                       style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6; background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg)">
                    <tr>
                        <%--<td width="8%" style="padding-left: 10px">--%>
                            <%--<sitemesh:div property='template.right.up.icon' /></td>--%>
                        <%--<td width="90%"><span style="font-size: 12">--%>
						    <%--<sitemesh:div property='template.right.up' />--%>
						<%--</span></td>--%>
                        <td width="100%" style="padding-left: 10px;font-size: 12">
                            <sitemesh:div property='template.right.up.icon' />
                            <sitemesh:div property='template.right.up' />
                        </td>
                    </tr>
                </table>
            </tr>
            <tr>
                <td><div id="rightTreeToolbar"
                         style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div></td>
            </tr>
            <div id="rightTreeContainer"
                 style="width: 98%; overflow: auto; clear: both; background-color: #FFFFFF">
                <ul id="rightTree"
                    style="font-size: 12; background-color: #FFFFFF; width: 92%"
                    class="ztree"></ul>
            </div>
            </td>
            </tr>
        </table>
    </div>
</div>
<div id="bottom">
    <sitemesh:div property='template.bottom' />
</div>
</body>
</html>