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
        .l-treemenubar-item{
        	width: 50%;
        }
    </style>
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
	    });
	
	    function templateshow() {
			$("#right").height($(document).height() - 6);
			$("#left").height($(document).height() - 10);
			$("#treeContainer").height($("#left").height()-58);
			if (grid) {
			    var rightHeight = $("#right").height();
			    grid.setHeight(rightHeight - $("#mainsearch").height() - 8);
			}
	    }
	</script>
	<sitemesh:write property='head' />
</head>
<body>
	<div id="left" style="background-color: #FFFFFF">
		<div id="lefttable" width="100%" border="0">
	        <div id="leftTitle" width="100%"
	             style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
	            <div  width="8%"
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
         style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6;"></div>
		<div id="treeContainer"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
			<ul id="tree"
				style="font-size: 12; background-color: #FFFFFF; width: 92%"
				class="ztree"></ul>
		</div>
	</div>
	<div id="right">
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
		<div class="container-fluid">
	         <div class="row">
	             <div id="maingrid" class="maingrid"></div>
	         </div>
	    </div>
	</div>
</body>
</html>
