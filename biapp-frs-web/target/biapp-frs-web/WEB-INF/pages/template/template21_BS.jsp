<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/common/meta.jsp"%>
    <%@ include file="/common/jquery_load.jsp"%>
    <%@ include file="/common/ligerUI_load.jsp"%>
    <%@ include file="/common/zTree_load.jsp"%>
    <link rel="stylesheet" type="text/css" href="${ctx}/css/classics/template/template21.css" />
    <script type="text/javascript">
        var grid = null;
        $(function() {
            var $height = $(window.parent.document).height();
            $("#left").height($height - 60);
            $("#right").height($height - 60);
            var leftHeight = $("#left").height();
            var rightHeight = $("#right").height();
            var $treeContainer = $("#treeContainer");
            var mainsearchHeight = $('#mainsearch').height();
            var rightWidth = $(window).width() - $("#left").width() - 15;
            $treeContainer.height(leftHeight - $("#treeSearchbar").height() - 35 - 24);
            $('#content').height(rightHeight - mainsearchHeight - 140);
            $('#navtab1').height(rightHeight - mainsearchHeight - 141);
            $("body").ligerLayout({
                leftWidth: 230,
                onEndResize: function() {
                    if (window['View'] && window['spread']) {
                        View.spreadDom.width($('#content').width());
                        View.resize(spread);
                    }
                }
            });
            $(".l-layout-header").hide();
        });
        (function($) {
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
            $(".searchtitle .togglebtn").live('click', function() {
                var searchbox = $(this).parent().nextAll("div.searchbox:first");
                var rightHeight = $("#right").height();
                if ($(this).hasClass("icon-down")) {
                    $(this).removeClass("icon-down");
                    $(this).addClass("icon-up");
                    searchbox.slideToggle('fast', function() {
                        if (grid) {
                            grid.setHeight(rightHeight - $("#mainsearch").height());
                        }
                        var mainsearchHeight = $('#mainsearch').height();
                        $('#content').height(rightHeight - mainsearchHeight - 5);
                        $('#navtab1').height(rightHeight - mainsearchHeight - 5);
                        $('div[tabid^=tab_]').height(rightHeight - mainsearchHeight - 5);
                        if (window['View'] && window['spread']) {
                            View.spreadDom.height($('#content').height() - 30);
                            View.resize(spread);
                        }
                    });
                } else {
                    $(this).removeClass("icon-up");
                    $(this).addClass("icon-down");
                    searchbox.slideToggle('fast', function() {
                        if (grid) {
                            grid.setHeight(rightHeight - $("#mainsearch").height() + 5);
                        }
                        var mainsearchHeight = $('#mainsearch').height();
                        $('#content').height(rightHeight - mainsearchHeight);
                        $('#navtab1').height(rightHeight - mainsearchHeight);
                        $('div[tabid^=tab_]').height(rightHeight - mainsearchHeight - 5);
                        if (window['View'] && window['spread']) {
                            View.spreadDom.height($('#content').height() - 30);
                            View.resize(spread);
                        }
                    });
                }
            });
        })(jQuery);
    </script>
    <sitemesh:write property='head' />
</head>
<body>
<div id="left" position="left" style="background-color: #FFFFFF">
    <div id="lefttable" width="100%" border="0">
        <div width="100%"
             style="height:24px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
            <div width="8%"
                 style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
                <sitemesh:div property='template.left.up.icon' />
            </div>
            <div width="30%">
					<span
                            style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
						<sitemesh:div property='template.left.up' />
					</span>
            </div>
            <div width="60%">
                <div
                        style="float: right; position: relative; padding-right: 3px; padding-top:4px;">
                    <sitemesh:div property='template.left.up.right' />
                </div>
            </div>
        </div>
    </div>
    <div id="treeToolbar"
         style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
    <div id="treeSearchbar" style="width:99%;margin-top:2px;padding-left:2px;">
        <ul style="margin : 0px 0px 10px 0px;">
            <li style="width:98%;text-align:left;">
                <div class="l-text-wrapper" style="width: 100%;">
                    <div class="l-text l-text-date" style="width: 100%;">
                        <input id="treesearchtext" type="text" class="l-text-field"  style="width: 90%;" />
                        <div class="l-trigger">
                            <i id="treeSearchIcon" style="width:100%;height:100%;" class="icon-search search-size"></i>
                        </div>
                    </div>
                </div>
            </li>
        </ul>
    </div>
    <div id="treeContainer"
         style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF; border-bottom: 1px solid #dcdcdc;">
        <ul id="tree"
            style="font-size: 12; background-color: #FFFFFF; width: 92%"
            class="ztree"></ul>
    </div>
    <sitemesh:div property='template.left.down' />
</div>
<div id="right" position="center">
    <div id="mainsearch" class="mainsearch">
        <!-- <div class="searchtitle">
            <i class = "icon-search search-size" ></i><span>报表查询</span>
            <i class="togglebtn icon-up"></i>
        </div> -->
        <!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
        <div id="searchbox" class="searchbox">
            <form id="formsearch">
                <div id="search"></div>
                <div class="l-clear"></div>
            </form>
            <div id="searchbtn" class="searchbtn"></div>
        </div>
    </div>
    <div id="content" class="content">
        <div id="navtab1" style="width: 100%;overflow:hidden; border:1px solid #D6D6D6; ">
        </div>
        <div id="pagination"></div>
    </div>
</div>
</body>
</html>
