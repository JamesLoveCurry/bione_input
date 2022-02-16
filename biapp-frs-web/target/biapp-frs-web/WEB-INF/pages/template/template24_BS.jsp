<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <%@ include file="/common/jquery_load.jsp"%>
    <%@ include file="/common/ligerUI_load.jsp"%>
    <%@ include file="/common/zTree_load.jsp"%>
    <%@ include file="/common/Bootstrap_load.jsp"%>
    <link rel="stylesheet" type="text/css" href="${ctx}/css/classics/template/template2.css" />
    <script type="text/javascript">
        var grid = null;
        function f_reload(id) {
            var manager = $("#" + id).ligerGetGridManager();
            manager.loadData();
        }
        function f_dialogclose() {
            $.ligerDialog.close();
        }
        $(function() {
            var height = $(window.parent.document).height();
            $("#left").height(height - 130);
            $("#right").height(height - 130);
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
                if ($(this).hasClass("togglebtn-down")) {
                    $(this).removeClass("togglebtn-down");
                    searchbox.slideToggle('fast', function() {
                        if (grid) {
                            grid.setHeight(rightHeight - $("#mainsearch").height() - 9);
                        }
                    });
                } else {
                    $(this).addClass("togglebtn-down");
                    searchbox.slideToggle('fast', function() {
                        if (grid) {
                            grid.setHeight(rightHeight - $("#mainsearch").height() - 4);
                        }
                    });
                }
            });
        })(jQuery);
    </script>
    <sitemesh:write property='head' />
    <script type="text/javascript">
        $(function() {
            setTreeContainerHeight();
            setContentHeight();
        });
        function setTreeContainerHeight() {
            var leftHeight = $("#left").height();
            var $treeContainer = $("#treeContainer");
            $treeContainer.height(leftHeight - 70);
        }
        function setContentHeight() {
            var rightHeight = $("#right").height();
            if (grid) {
                grid.setHeight(rightHeight - $("#mainsearch").height() - 9);
            }
        }
    </script>
</head>
<body>
<div id="left" style="background-color: #FFFFFF; margin-top: 5px;">
    <div id="lefttable" width="100%" border="0">
        <div width="100%"
             style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
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
    <div id="treeContainer"
         style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
        <ul id="tree"
            style="font-size: 12; background-color: #FFFFFF; width: 92%"
            class="ztree"></ul>
    </div>
</div>


<%--<div id="right">--%>
    <%--<div id="mainsearch">--%>
        <%--<div class="searchtitle">--%>
            <%--<img src="${ctx}/images/classics/icons/find.png" /> <span>搜索</span>--%>
            <%--<div class="togglebtn">&nbsp;</div>--%>
        <%--</div>--%>
        <%--<!-- <div class="navline" style="margin-bottom: 4px;"></div> -->--%>
        <%--<div id="searchbox" class="searchbox">--%>
            <%--<form id="formsearch">--%>
                <%--<div id="search"></div>--%>
                <%--<div class="l-clear"></div>--%>
            <%--</form>--%>
            <%--<div id="searchbtn" class="searchbtn"></div>--%>
        <%--</div>--%>
    <%--</div>--%>
    <%--<div id="content" class="content">--%>
        <%--<div id="maingrid" class="maingrid"></div>--%>
    <%--</div>--%>
<%--</div>--%>

<div id="right">
    <div id="center" style="height: 100%">
        <div class="box box-default" id="mainsearch" style="margin: 2px;">
            <!-- /.box-header -->
            <div class="box-body">
                <div class="row">
                    <div class="col-md-12" >
                        <div id="searchbox" class="searchbox">
                            <form id="formsearch">
                                <div id="search"></div>
                                <div class="l-clear"></div>
                            </form>
                            <div id="searchbtn" class="btn-group"></div>
                        </div>
                    </div>
                    <!-- /.col -->
                </div>
                <!-- /.row -->
            </div>
        </div>
        <!-- /.box -->

        <!-- Main Grid -->
        <div class="container-fluid">
            <div class="row">
                <div id="maingrid" class="maingrid"></div>
            </div>
        </div>
    </div>
</div>






</body>
</html>
