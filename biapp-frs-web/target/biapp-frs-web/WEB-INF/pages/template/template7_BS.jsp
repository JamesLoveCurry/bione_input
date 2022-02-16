<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/common/jquery_load.jsp"%>
    <%@ include file="/common/ligerUI_load.jsp"%>
    <%@ include file="/common/zTree_load.jsp"%>
    <link rel="stylesheet" type="text/css"
          href="${ctx}/css/classics/template/template22.css" />
    <script type="text/javascript">
        var rightWidth=0;
        $(function() {
            templateshow();
        });
        function templateshow() {
            // var $content = $(document);
            var $content = $(window.parent.document);
            $("#right").height($content.height() - 50);
            $("#left").height($content.height() - 50);
            $("#tab").height($content.height() - 50);
            var rightHeight = $("#right").height();
            var leftHeight = $("#left").height();
            var $treeContainer = $("#treeContainer");
            $("#lefttable").hide();
            var leftTitle =
                '<div width="8%"                                                                                                             '+
                '	style="float: left; position: relative; height: 20p; margin-top: 5px">    '+
                '		<i class = "icon-guide search-size"></i>                       '+
                '</div>                                                                                                                             '+
                '<div width="90%">                                                                                                         '+
                '	<span                                                                                                                             '+
                '		style="font-size: 12; float: left; position: relative; line-height: 25px; padding-left: 2px"> '+
                '		<span style="font-size: 12">'+$("#leftSpan").html()+'</span>                                     '+
                '	</span>                                                                                                                         '+
                '</div>                                                                                                                             ';
            $("#left").attr("title",leftTitle);
            $("body").ligerLayout({
                leftWidth: 240,
                allowLeftResize : true ,
                onEndResize: function(){
                }
            });
            $("#left").attr("title","");
            $treeContainer.height(leftHeight - 155);
        }
        $(window).resize(function() {
            if($("#right").width()>rightWidth)
                $("#rightDiv").width($("#right").width());
            else{
                $("#rightDiv").width(rightWidth);
            }
        });
    </script>
    <sitemesh:write property='head' />
</head>
<body>
<div id="left" position="left" style="background-color: #FFF;">
    <div id="lefttable" width="100%" border="0">
        <div width="100%"
             style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
            <div width="8%"
                 style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
                <sitemesh:div property='template.left.up.icon' />
            </div>
            <div width="90%">
					<span id="leftSpan"
                          style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
						<sitemesh:div property='template.left.up' />
					</span>
            </div>
        </div>
    </div>
    <div id="treeToolbar"
         style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;background-color:#fff;"></div>
    <div id="treeContainer"
         style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
        <ul id="tree"
            style="font-size: 12; background-color: #FFFFFF; width: 92%"
            class="ztree"></ul>
    </div>
</div>
<div id="right" position="center" style="overflow-x: auto;overflow-y: hidden;">
    <div id="rightDiv" style="width: 100%">
        <div id="tab" style="width: 100%; overflow: hidden;"></div>
    </div>
</div>
</body>
</html>
