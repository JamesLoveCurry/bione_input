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
	var rightWidth=0;
    $(function() {
    	
		templateshow();
    });
    function templateshow() {
		var $content = $(window);
		$("#right").height($content.height() - 5);
		$("#rightDiv").height($("#right").height());
		$("#left").height($content.height() - 35);
		$("#tab").height($content.height() - 5);
		var rightHeight = $("#right").height();
		var leftHeight = $("#left").height();
		var $treeContainer = $("#treeContainer");
		$treeContainer.height(leftHeight - 56 - $("#treeSearchbar").height() );
		 $(".l-layout-header").hide();
		 rightWidth=$content.width()-$("#left").width()-15;
		$("#rightDiv").width(rightWidth);
		$(window).resize(function() {
			 if($("#right").width()>rightWidth)
				 $("#rightDiv").width($("#right").width());
			 else{
				 $("#rightDiv").width(rightWidth);
			 }
		});

    }
</script>
<sitemesh:write property='head' />
</head>
<body>
	<div id="left" position="left" style="background-color: #FFFFFF">
		<div id="lefttable" width="100%" border="0">
			<div width="100%"
				style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
				<div width="8%"
					style="padding-left: 10px; float: left; position: relative; height: 20p; margin-top: 8px">
					<sitemesh:div property='template.left.up.icon' />
				</div>
				<div width="90%">
					<span
						style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
						<sitemesh:div property='template.left.up' />
					</span>
				</div>
			</div>
		</div>                                                                                               
		<div id="taskTypeList"
			style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
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
