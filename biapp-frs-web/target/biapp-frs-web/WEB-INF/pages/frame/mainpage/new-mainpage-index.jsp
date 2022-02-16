<%--
  Created by IntelliJ IDEA.
  User: hzqxy
  Date: 2020/11/3
  Time: 15:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<html>
<head>
    <%@ include file="/common/jquery_load.jsp"%>
    <%@ include file="/common/ligerUI_load.jsp"%>
    <!-- Ionicons -->
    <link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/ionicons.min.css">
    <!-- Bootstrap 3.3.7 -->
    <link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/bootstrap/css/bootstrap.min.css">
<%--    <!-- Font Awesome -->--%>
    <link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/font-awesome.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="${ctx}/static/css/bower_components/dist/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="${ctx}/static/css/bower_components/dist/css/skins/_all-skins.min.css">
    <link rel="stylesheet" type="text/css" href="${ctx}/css/classics/mainpage/newmainpage.css" />

    <script type="text/javascript">

        function toIndex(url,id,text,index){
            /*查看更多跳转页面*/
            window.parent.find_menu(text);
            if(index == '首页'){
                top.addTabs({
                    id: id,
                    title: text + '首页',
                    close: true,
                    url: url,
                    urlType: "relative"
                });
            }
        }

        /*公告查询*/
        function openDetail(id) {
            var hight = window.parent.parent.$("body").height()*0.96
            var width = window.parent.parent.$("body").width()*0.96;
            window.top.BIONE.commonOpenDialog("公告", "noticeWin", width, hight,
                '${ctx}/bione/msg/announcement/'+id+'/view');
        }
    </script>
</head>
<body>
<div class="design-all">
    <%--系统模块--%>
    <div class="design-left">
        <div class="sub-head">
            <span class="span-title">
                <i class="iconfont icon-ai-module"></i>
            </span>
            <h6 class="sub-title">系统模块</h6>
        </div>
        <c:forEach items="${menuAndFuncs}" var="menuInfo" varStatus="menuStatus">
            <div class="sub-content clearfix">
                <div class="sub-content-title">
                    <span class="span-title">
                    <i class="icon ${menuInfo.data.navIcon}"></i>
<%--                    <i class="iconfont ${menuInfo.data.navIcon}"></i>--%>
                    </span>
                    <div class="sub-content-name">${menuInfo.text}</div>
                </div>
                <c:forEach items="${menuInfo.children}" var="funsInfo" varStatus="funsStatus">
                    <div class="box-left">
                        <div>
                            <div class="box-title">${funsInfo.text}</div>
                        </div>
                        <div class="icon-size">
                            <i style="font-size : 40px;" class="icon ${funsInfo.data.navIcon}"></i>
<%--                            <i style="font-size : 40px;" class="iconfont ${funsInfo.data.navIcon}"></i>--%>
                        </div>
                        <a href="javascript:toIndex(
                            <c:forEach items="${funsInfo.children}" var="methodInfo" varStatus="methodStatus">
                                    <c:if test="${methodStatus.first}">
                                       '${methodInfo.data.navPath}', '${methodInfo.id}', '${funsInfo.text}','${methodInfo.text}'
                                    </c:if>
                            </c:forEach>
                        )" class="small-box-left">查看更多 <i class="fa fa-arrow-circle-right"></i></a>
                        <c:if test="${funsInfo.params.backlog > 0}">
                            <div class="sub-badge">
                                ${funsInfo.params.backlog}
                            </div>
                        </c:if>
                    </div>
                </c:forEach>
            </div>
        </c:forEach>
    </div>

    <%--公告栏--%>
    <div class="design-right clearfix">
        <div class="sub-head">
            <span class="span-title">
                <i class="iconfont icon-lujing"></i>
            </span>
            <div class="sub-title">公告栏</div>
        </div>
        <div id="_msg_content" class="sub-content">
            <dl>
                <c:forEach items="${bioneMsgNoticeInfos}" var="item" varStatus="vs">
                    <dt><h5 class="date-box"><fmt:formatDate value="${item.lastUpdateTime}" pattern="yyyyMMdd"/></h5></dt>
                    <dd class="box-right">
                        <div class="small-box-right">
                            <div class="small-box-right-title clearfix">
                                <h5>${item.announcementType}</h5>
                                <div class="time">
                                    <i class="fa icon-time1"></i>
                                    <span>
                                        <fmt:formatDate value="${item.lastUpdateTime}" pattern="yyyy-MM-dd　HH:mm:ss"/>
                                    </span>
                                </div>
                            </div>
                            <div class="small-box-right-content">
                                <div><span>${item.announcementTitle}</span></div>
                                <div><button onclick="openDetail('${item.announcementId}')">了解更多</button></div>
                            </div>
                        </div>
                    </dd>
                </c:forEach>
            </dl>
        </div>
    </div>
</div>

</body>
</html>
