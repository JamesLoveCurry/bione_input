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
          href="${ctx}/css/classics/template/template2.css" />
    <script type="text/javascript">
        var grid = null;
        $(function() {
            templateshow();
            templateinit();
        });

        function templateshow() {
            var $height = $(window.parent.document).height();
            $("#right").height($height - 60);
            $("#left").height($height - 60);
            $("#treeContainer").height($("#left").height()-58);
            if (grid) {
                var rightHeight = $("#right").height();
                grid.setHeight(rightHeight - $("#mainsearch").height() - 8);
            }
        }
        function templateinit() {
            $(".l-dialog-btn").live('mouseover', function() {
                $(this).addClass("l-dialog-btn-over");
            }).live('mouseout', function() {
                $(this).removeClass("l-dialog-btn-over");
            });
            $(".l-dialog-tc .l-dialog-close").live('mouseover', function() {
                $(this).addClass("l-dialog-close-over");
            }).live('mouseout', function() {
                $(this).removeClass("l-dialog-close-over");
            });
            $(".searchtitle .togglebtn").live(
                'click',
                function() {
                    var searchbox = $(this).parent().nextAll(
                        "div.searchbox:first");
                    var centerHeight = $("#center").height();
                    if ($(this).hasClass("icon-down")) {
                        $(this).removeClass("icon-down");
                        $(this).addClass("icon-up");
                        searchbox.slideToggle('fast', function() {

                            if (grid) {
                                grid.setHeight(centerHeight
                                    - $("#mainsearch").height() - 8);
                            }
                        });
                    } else {
                        $(this).removeClass("icon-up");
                        $(this).addClass("icon-down");
                        searchbox.slideToggle('fast', function() {

                            if (grid) {
                                grid.setHeight(centerHeight
                                    - $("#mainsearch").height() - 3);
                            }
                        });
                    }
                });
        }
    </script>
    <sitemesh:write property='head' />
    <script type="text/javascript">
        $(function() {
            templateshow();
        });
    </script>
</head>
<body>
<div id="left" style="background-color: #FFFFFF">
    <div id="lefttable" width="100%" border="0">
        <div width="100%"
             style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
            <div width="8%"
                 style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
                <sitemesh:div property='template.left.up.icon' />
            </div>
            <div width="90%">
					<span
                            style="font-size: 12px; float: left; position: relative; line-height: 30px; padding-left: 2px">
						<sitemesh:div property='template.left.up' />
					</span>
            </div>
        </div>
    </div>
    <div id="treeToolbar"
         style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
    <div id="treeContainer"
         style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
        <ul id="tree"
            style="font-size: 12; background-color: #FFFFFF; width: 92%"
            class="ztree"></ul>
    </div>
</div>
<div id="right">
    <div id="mainsearch">
        <div class="searchtitle">
            <i class = "icon-search search-size" ></i><span>??????</span>
            <i class="togglebtn icon-up"></i>
        </div>
        <!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
        <div id="searchbox" class="searchbox">
            <form id="formsearch">
                <div id="search"></div>
                <div class="l-clear"></div>
            </form>
            <div id="searchbtn" class="searchbtn"></div>
        </div>
    </div>
    <div class="content">
        <div id="maingrid" class="maingrid"></div>
    </div>
</div>
</body>
</html>
