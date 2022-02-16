<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/template/template20.css" />
<link rel="stylesheet" type="text/css"
	href="${ctx}/css/classics/ligerui-custom.css" />
<script type="text/javascript" src="${ctx}/js/require/require.js"></script>
<script type="text/javascript">
	var Component = liger.core.Component;
	$.fn.extend({
		searchPad: function(opts) {
			return new SearchPad(this, opts);
		}
	});
	function SearchPad(wrap, opts) {
		SearchPad.base.constructor.call(this, opts);
		this.wrap = wrap;
		this.init();
	}
	SearchPad.ligerExtend(Component, {
		init: function() {
			var t = this;
			var w = this.wrap;
			$.each(this.options, function(i, n) {
				t.set(i, n);
			});
			require.config({
				baseUrl : '${ctx}/js/',
				paths : {
					jquery : 'jquery/jquery-1.7.1.min',
					JSON2 : 'bione/json2.min'
				},
				shim : {
					JSON2 : {
						exports : 'JSON2'
					}
				}
			});
			require([ 'jquery', 'JSON2', '../plugin/js/template/viewMain' ], t.load);
			// 按钮相关
			var btns = this.options.buttons || [];
			for(var i = 0; i < btns.length; i++) {
				btns.appendTo = $('#searchbtn');
				BIONE.createButton(btns[i]);
			}
		},
		load: function() {
			var paramtmpId  = this.options.paramtmpId;
			if (paramtmpId) {
				var w = this.wrap,
				t = this;
				$.ajax({
					cache : false,
					async : true,
					url : "${ctx}/report/frame/param/templates/" + paramtmpId
							+ "?type=view",
					dataType : 'json',
					success : function(data) {
						w.templateView({
							data : JSON2.parse(data.paramJson)
						});
						t.trigger('load', [data]);
					}
				});
			}
		}
	});
	
</script>
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
	function templateshow() {
		$("#center").height($(document).height() - 8);
		var centerHeight = $("#center").height();
		$("#content").height(centerHeight - $("#mainsearch").height() - 8);
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
				<i class = "icon-search search-size" ></i>
				<sitemesh:div property='center.top' />
				<i class="togglebtn icon-up"></i>
			</div>
			<!-- <div class="navline" style="margin-bottom: 4px;"></div> -->
			<div id="searchbox" class="searchbox">
				<sitemesh:div property='template.form' />
				<div id="searchbtn" class="searchbtn" style="height: 30px;"></div>
			</div>
		</div>
		<div id="content" class="content">
			<div id="main" class="main"
				style="height: 100%; border: solid 1px #D6D6D6;">
				<sitemesh:div property='template.frame' />
			</div>
		</div>
	</div>
</body>
</html>