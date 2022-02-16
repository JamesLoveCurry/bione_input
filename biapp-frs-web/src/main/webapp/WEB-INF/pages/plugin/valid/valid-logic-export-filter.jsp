<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				display : "报送模块",
				name : "busiType",
				newline : true,
				labelwidth : 120,
				type : "select",
				options : {
					url : "${ctx}/report/frame/datashow/idx/busiTypeList.json",
					onBeforeSelect : function(selectBusiType) {
						getSystemVer(selectBusiType);
					},
					initValue : '02'
				}
			}, {
				display : "制度版本",
				name : "endDate",
				newline : true,
				type : "select",
				labelwidth : 120,
				options : {
					data : null
				}
			} ]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '确定',
			onclick : exportRpt
		});
		BIONE.addFormButtons(buttons);
		getSystemVer("02");
		initExport();
	});
	
	var initExport = function() {
		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(downdload);
	};
	
	function getSystemVer(busiType) {
		if(busiType){
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/frs/system/cfg/getSystemEndDate",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result){
					if(result){
						$.ligerui.get("endDate").setData(result);
					}
				},
				error:function(){
					BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	function exportRpt() {
		var busiType = $("#busiType").val();
		var endDate = $("#endDate").val();
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/design/cfg/getRptIdsByParam2?busiType=" + busiType + "&endDate=" + endDate,
			type : "get",
			dataType : 'json',
			success : function(result) {
				if(result.rptList != null && result.rptList.length > 0){
					parent.exportAll(result.rptList, busiType, endDate)
					BIONE.closeDialog("exportFilter");
				} else {
					BIONE.tip("没有可以导出的逻辑校验公式！");
				}
			},
			error : function() {
				BIONE.closeDialog("exportFilter");
				parent.BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	}
	 function cancle(){                     
		BIONE.closeDialog("exportFilter");
	} 
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="" method="post"></form>
		<div id="tipAreaDiv" style="width: 99.5%;float: left;padding-top: 10px;padding-bottom: 20px;background-color: #FFFEE6;border: solid 1px #D0D0D0;">
				<div style="padding-left: 2px;">
					<div style="width: 24px; height: 16px; float: left; background-image: url('${ctx}/images/classics/icons/lightbulb.png'); background-attachment: scroll; background-repeat: no-repeat; background-position-x: 0%; background-position-y: 0%; background-size: auto; background-origin: padding-box; background-clip: border-box; background-color: transparent;"></div>
					<p>使用说明</p>
					<p class="tipptitle">1.不选择“制度版本”，可以一键导出各报送模块的最新逻辑校验公式。</p>
					<p class="tipptitle">2.选择“制度版本”，可以一键导出该版本的所有逻辑校验公式。</p>
					<p class="tipptitle">3.该功能导出的逻辑校验公式已过滤掉“升级概况”为“停用”的制度。</p>
					<p class="tipp"></p>
				</div>
			</div>
	</div>
</body>
</html>