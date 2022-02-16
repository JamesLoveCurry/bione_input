<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template20.jsp">
<head>
<script type="text/javascript">
	var ctx = "${ctx}", reportId = "${reportId}", paramtmpId = "${paramtmpId}", reportName = "${reportName}", type = "${type}";
</script>
<script type="text/javascript">
	var isHaveTemp = true;
	var height = "100%";
	$(function() {
		$("#title").html(reportName);
		$("#frame").attr({
			frameborder : 0,
			height : "100%",
			width : "100%",
			name : "framename",
			scrolling : "auto"
		});
		var btns = [ {
			appendTo : "#searchbtn",
			text : '运行',
			width : '50px',
			click : function() {
				f_run(true);
			}
		}, {
			appendTo : "#searchbtn",
			text : '窗口打开',
			width : '50px',
			click : function() {
				f_run(false);
			}
		},  {
			appendTo : "#searchbtn",
			text : '清空',
			width : '50px',
			click : f_clear
		}, {
			appendTo : "#searchbtn",
			text : '收藏',
			width : '50px',
			click : f_fav
		},{
			appendTo : "#searchbtn",
			text : '导出excel',
			icon : 'export',
			width : '70px',
			click:function(){
				f_run(false,"exportType=excel");
			}
		},{
			appendTo : "#searchbtn",
			text : '导出pdf',
			icon : 'export',
			width : '70px',
			click:function(){
				f_run(false,"exportType=pdf");
			}
		}];
		var b = [];
		b = [0, 1, 2, 3, 4, 5, 6];
		if (b && b.length > 0) {
			$.each(b, function(i, n) {
				BIONE.createButton(btns[n]);
			});
		}

		if (!paramtmpId) {
			isHaveTemp = false;
			
			f_run(true);
		} else {
			createPage("myPrintArea", '${paramtmpId}');
		}

	});

	// 搜索框收起
	function holdSearchBar() {
		$(".searchtitle .togglebtn").addClass("togglebtn-down");
		$("#searchbox").slideUp(
				'fast',
				function() {
					$("#content").height(
							$("#center").height() - $("#mainsearch").height()
									- 3);
				});
	}

	//运行报表  flag是否在当前也展示报表，extParams扩展参数
	function f_run(flag,extParams) {
		if(typeof(extParams)=="undefined"){
			extParams = "";
		}
		var src = ctx + "/bireport/OpenReport/processRequest?"+extParams+"&reportId=";
		if (!isHaveTemp) {
			if (flag) {
				$("#frame").attr("src",src + reportId, "report_" + reportId);
			} else {
				window.open(src + reportId, "report_" + reportId);
			}
		} else {
			jQuery.metadata.setType("attr", "validate");
			var mainform = $("#myPrintArea");
			if (BIONE.validate(mainform)) {
				if (mainform.valid()) {//对文本框的输入进行验证
					holdSearchBar();
					var paramValue = mainform.serializeArray(), filterValuesArr = filter(paramValue);
					var filterValues = JSON2.stringify(filterValuesArr);
					paramValue = JSON2.stringify(paramValue);
					if (flag) {
						$("#frame")
								.attr("src",src + reportId + "&paramValue="
												+ filterValues,
										"report_" + reportId);
					} else {
						window.open(src + reportId + "&paramValue="
									+ filterValues, "report_"
									+ reportId);
					}
				} else {
					BIONE.tip("有输入不合法的控件，请检查");
				}
			}
		}
	}
	
	
	//收藏
	function f_fav() {
		window.parent.BIONE.commonOpenDialog("收藏目录", "favCataTreeWin", 400,
				380,
				'${ctx}/report/comm/showFavCataTree?frameName=repInfoFrame&rptId='
						+ reportId);
	}
	
	//组织参数
	function filter(paramValue) {
		var result = [];
		for ( var i = 0; i < paramValue.length; i++) {
			if (paramValue[i].name.substring(0, 7) != "ligerui") {
				result.push(paramValue[i]);
			}
		}
		return result;
	}

	//清空
	function f_clear() {

		var form = $(document.getElementById("myPrintArea"));
		if (!form)
			return;
		$(":input", form).not(
				":submit, :reset,:hidden,:image,:button, [disabled]").each(
				function() {
					$(this).val("");
				});
		$(":input[ltype=combobox]", form).each(function() {
			$(this).val("");
		});
		$(":input[ltype=select]", form).each(function() {
			$(this).val("");
		});

	}
</script>
</head>
<body>
	<div id="center.top" style="overflow: left;">
		<span id="title" style="overflow: left;"></span>
	</div>
	<div id="template.form">
		<form id="myPrintArea"></form>
	</div>
	<div id="template.frame">
		<iframe id="frame"></iframe>
	</div>
</body>
</html>