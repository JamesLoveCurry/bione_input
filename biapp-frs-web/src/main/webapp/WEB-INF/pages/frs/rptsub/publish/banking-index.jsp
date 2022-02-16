<%--
  Created by IntelliJ IDEA.
  User: hzqxy
  Date: 2020/11/4
  Time: 16:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
<head>
    <%@ include file="/common/jquery_load.jsp"%>
    <%@ include file="/common/ligerUI_load.jsp"%>
    <link rel="stylesheet" type="text/css" href="${ctx}/css/classics/mainpage/other-index.css" />
</head>
<body>
    <div class="design_all clearfix">
        <div class="design_head">
            <div class="subdiv_layout_header">
                <span class="subdiv_titleicon">
                    <i class="iconfont icon-ai-module"></i>
                </span>
                <span class="subdiv_titletext">快速导航_控制面板</span>
            </div>
            <div class="subdiv_layout_content">
                <iframe frameborder="0" src="${ctx}/rpt/frame/mainpage/controlPanel" style="width:99.9%;"></iframe>
            </div>
        </div>
        <div class="design_left">
            <div class="subdiv_layout_header">
                <span class="subdiv_titleicon">
                    <i class="iconfont icon-lishuaichaxun"></i>
                </span>
                <span class="subdiv_titletext">待办任务</span>
            </div>
            <div class="subdiv_layout_content">
                <iframe frameborder="0" src="${ctx}/rpt/frame/mainpage/blacklog?taskType=02" style="width:99.9%; height: 330px"></iframe>
            </div>
        </div>
        <div class="design_right">
            <div class="subdiv_layout_header">
                <span class="subdiv_titleicon">
                    <i class="iconfont icon-lujing"></i>
                </span>
                <span class="subdiv_titletext">我的收藏</span>
            </div>
            <div class="subdiv_layout_content">
                <iframe frameborder="0" src="${ctx}/rpt/frame/mainpage/myfav" style="width:99.9%; height: 330px"></iframe>
            </div>
        </div>
    </div>
</body>
</html>
