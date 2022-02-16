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
			fields : [ {
				display : "报表指标导出方式",
				name : 'exportIdxType',
				type : "select",
				newline : false,
				labelwidth : 120,
				options : {
					initValue : "N",
					data : [ {
						text : '全部导为空指标',
						id : 'Y'
					}, {
						text : '导出实际配置信息',
						id : 'N'
					} ]
				}
			}, {
				display : "报送模块",
				name : "busiType",
				newline : true,
				labelwidth : 120,
				type : "select",
				options : {
					url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
					onBeforeSelect : function(selectBusiType) {
						getSystemVer(selectBusiType);
					},
					initValue : '02'
				}
			}, {
				display : "制度版本",
				name : "verId",
				newline : true,
				type : "select",
				labelwidth : 120,
				options : {
					data : null
				}
			},{
				display : "报送对象",
				name : "reportObj",
				newline : true,
				labelwidth : 120,
				type : "select",
				options : {
					data : [{
						id : '01',
						text : '银行'
					}, {
						id : '02',
						text : '农信'
					}, {
						id : '03',
						text : '农商'
					}, {
						id : '04',
						text : '消金'
					}, {
						id : '05',
						text : '财务公司'
					}, {
						id : '06',
						text : '信托'
					}]
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
		if (busiType) {
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getSystemList",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result) {
					if (result) {
						$.ligerui.get("verId").setData(result);
					}
				},
				error : function() {
					parent.BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
	function exportRpt() {
		var busiType = $("#busiType").val();
		var verId = $("#verId").val();
		var verNm = $.ligerui.get("verId").getText();
		var reportObj = $("#reportObj").val();
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/report/frame/design/cfg/getRptIdsByParam?busiType=" + busiType + "&verId=" + verId + "&reportObj=" + reportObj,
			type : "get",
			dataType : 'json',
			success : function(result) {
				if(result.rptList != null && result.rptList.length > 0){
					parent.exportDesignInfos($("#exportIdxType").val(), result.rptList, verId, verNm);
					BIONE.closeDialog("exportFilter");
				} else {
					BIONE.tip("没有可以导出的报表！");
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
					<p class="tipptitle">1.不选择“制度版本”，可以一键导出各报送模块的最新制度。</p>
					<p class="tipptitle">2.选择“制度版本”，可以一键导出该版本的所有制度。</p>
					<p class="tipptitle">3.不选择“报送对象”，可以一键导出所有报送对象的制度</p>
					<p class="tipptitle">4.选择“报送对象”，可以一键导出该报送对象的制度</p>
					<p class="tipptitle">5.该功能导出的制度已过滤掉“升级概况”为“停用”的制度。</p>
					<p class="tipp"></p>
				</div>
			</div>
	</div>
</body>
</html>