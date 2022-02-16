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
		//templateshow();
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
					if ($(this).hasClass("togglebtn-down")) {
						$(this).removeClass("togglebtn-down");
						searchbox.slideToggle('fast', function() {

							/* if (grid) {
								grid.setHeight(centerHeight
										- $("#mainsearch").height() - 8);
							} */
						});
					} else {
						$(this).addClass("togglebtn-down");
						searchbox.slideToggle('fast', function() {

							/* if (grid) {
								grid.setHeight(centerHeight
										- $("#mainsearch").height() - 3);
							} */
						});
					}
				});
	}
	function templateshow() {
		$("#center").height($(document).height());
		/* if (grid) {
			var centerHeight = $("#center").height();
			grid.setHeight(centerHeight - $("#mainsearch").height() - 8);
		} */
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
	<div id="center" style="height:100%;overflow: visible; clear: both;">
		<div id="mainsearch" >
			<!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
			<div id="searchbox" style="padding:3px">
				<form id="formsearch">
					<div id="search"></div>
					<div class="l-clear"></div>
				</form>
				<div id="searchbtn" class="searchbox" style="text-align: center;border-width: 0px;"></div>
			</div>
		</div>
		<div class="content" style="padding:3px">
			<table width="100%" border="0" align="center">
				<tr>
					<td width="45%" height="100%" align="left" valign="top"><div style="margin:3px;height:200"
							id="maingrid" >
							</div></td>
					<td width="8%" align="left" valign="middle"><form>
							<select size="1" id="logicCode" name="logicCode">
								<option value=">" selected="selected">大于</option>
							</select>
						</form></td>
					<td width="45%" height="100%" align="right" valign="top"><div style="margin:3px;height:200"
							id="maingrid2" ></div></td>
				</tr>
			</table>
		</div>
	</div>

</body>
</html>