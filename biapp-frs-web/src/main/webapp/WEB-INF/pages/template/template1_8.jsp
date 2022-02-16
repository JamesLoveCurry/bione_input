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
	}
	function templateshow() {
		$("#center").height($(document).height() - 6);
		if (grid) {
			var centerHeight = $("#center").height();
			grid.setHeight(centerHeight - $("#mainsearch").height() - 8);
		}
	}
</script>
<sitemesh:write property='head' />
<script type="text/javascript">
	$(function() {
		templateshow();
	});
</script>
<style type="text/css">
#center {
	width: 100%;
	float: center;
}
</style>
</head>
<body>
	<div id="center">
		<div id="mainsearch">
			<!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
			<div id="searchbox" class="searchbox">
				<form id="formsearch" style=" height:100px ">
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