<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template1.css" />
<script type="text/javascript">
    var grid = null;
    $(function() {
	templateshow();
	templateinit();

    });
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
// 				var ss = $(document).height() - $('#mainsearch').height() - 26;
			    if (grid) {
// 				grid.setHeight(centerHeight
// 					- $("#mainsearch").height() - 18);
// 				grid.setHeight(ss);
			    	grid.setHeight($('#center').height() - $('#mainsearch').height());
			    }
			});
		    } else {
		    	$(this).removeClass("icon-up");
				$(this).addClass("icon-down");
			searchbox.slideToggle('fast', function() {
// 				var ss = $(document).height() - $('#mainsearch').height() - 25;
			    if (grid) {
// 				grid.setHeight(centerHeight
// 					- $("#mainsearch").height() - 10);
// 			    grid.setHeight(ss);
			    	grid.setHeight($('#center').height()  + 6 - $('#mainsearch').height());
			    }
			});
		    }
		});// $(document).height() - $('#mainsearch').height()
    }
    function templateshow() {
//     	var ss = $(document).height() - $('#mainsearch').height() - 26;
	  	$("#center").height($(window).height() - 68);
		if (grid) {
// 		    var centerHeight = $("#center").height();
// 		    grid.setHeight(centerHeight - $("#mainsearch").height() - 18);
// 		    grid.setHeight(ss);
		    grid.setHeight($('#center').height() - $('#mainsearch').height());
		}
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
	<div id="center">
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