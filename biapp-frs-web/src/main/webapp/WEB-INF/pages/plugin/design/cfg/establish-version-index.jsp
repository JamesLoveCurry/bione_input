<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<script type="text/javascript">
	/* jQuery.validator.addMethod("srcNewVer", function(value, element) {
		var srcNewVer = $("#srcNewVer").val();
		var newVer = $("#newVer").val();
		if (srcNewVer != "" && newVer != ""
				&& srcNewVer >= newVer) {
			return false;
		} else {
			return true;
		}
	}, "来源版本应小于当前版本");
	jQuery.validator.addMethod("newVer", function(value, element) {
		var srcNewVer = $("#srcNewVer").val();
		var newVer = $("#newVer").val();
		if (srcNewVer != "" && newVer != ""
				&& srcNewVer >= newVer) {
			return false;
		} else {
			return true;
		}
	}, "当前版本应大于来源版本"); */

	var p_busiType = "";
	
	$(function() {
		initForm();
	});
	
	//创建表单结构 
	function initForm() {
		var field = [ {
			display : "业务类型",
			name : "busiType",
			newline : true,
			type : "select",
			options : {
				url : "${ctx}/report/frame/datashow/idx/getIdxTaskType.json",
				onBeforeSelect : function(selectBusiType) {
					p_busiType = selectBusiType;
					$("#rptIdx").val("");
					$("#rptIdxBox").val("");
					getSystemVer(selectBusiType);
				}
			},
			validate : {
				required : true
			}
		},{
			display: '创建报表',
			name :'rptIdx',
			comboboxName:'rptIdxBox',
			type: 'select',
			newline: true,
			options : {
				onBeforeOpen : function() {
					if(p_busiType){
						BIONE.commonOpenDialog(
								"报表选择",
								"rptIdxTreeWin",
								480,
								380,
								'${ctx}/report/frame/design/cfg/rptIdxTreeWin?busiType='+p_busiType,
								null);
					}else{
						BIONE.tip("请先选择业务类型");
					}
					return false;
				}
			},
			validate:{
				required: true
			}
		},{
			display : "来源版本",
			name : 'srcNewVer',
			type : "select",
			newline : true,
			options : {
				data : null,
			},
			validate : {
				required : true/* ,
				srcNewVer : true */
			}
		},{
			display : "目标版本",
			name : 'newVer',
			type : "select",
			newline : true,
			options : {
				data : null,
			},
			validate : {
				required : true/* ,
				newVer : true */
			}
		}];
		
		var mainform = $("#mainform").ligerForm({
			inputWidth : 170,
			labelWidth : 70,
			space : 40,
			fields : field
		});
		
		var buttons = [];
		buttons.push({
			text : '创建',
			onclick : onConfirm
		});
		BIONE.addFormButtons(buttons);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
		
		$("#busiType").val(p_busiType);
	}
	
	function onConfirm() {
		BIONE.submitForm($("#mainform"), function(result) {
			if(result && result.msg){
				BIONE.tip(result.msg);
				return;
			}else{
				BIONE.tip("创建成功");
				window.parent.grid.reload();
				BIONE.closeDialog("establishVer");
			}
		}, function() {
			BIONE.tip("创建失败");
		});
	}
	
	function getSystemVer(busiType){
		if(busiType){
			$.ajax({
				cache : false,
				async : false,
				url : "${ctx}/report/frame/design/cfg/getSystemList",
				dataType : 'json',
				data : {
					busiType : busiType
				},
				type : "post",
				success : function(result){
					if(result){
						$.ligerui.get("srcNewVer").setData(result);
						$.ligerui.get("newVer").setData(result);
					}
				},
				error:function(){
					BIONE.tip("数据加载异常，请联系系统管理员");
				}
			});
		}
	}
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/report/frame/design/cfg/saveEstablishVer"></form>
		<div id="bottom">
			<div class="form-bar">
				<div class="form-bar-inner" style="padding-top:160px;padding-right:20px"></div>
			</div>
		</div>
	</div>
</body>
</html>