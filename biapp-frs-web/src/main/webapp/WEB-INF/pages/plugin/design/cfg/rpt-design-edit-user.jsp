<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style type="text/css">
	#mainform{
		height : 90%;
	}
</style>
<script type="text/javascript">

	$(function() {
		initForm();
	});
	
	//创建表单结构 
	function initForm() {
		var field = [ {
			display : "业务类型",
			name : "busiType",
			comboboxName:'busiTypeBox',
			newline : true,
			type : "select",
			options : {
				onBeforeOpen : function() {
					$.ajax({
						cache : false,
                        async : false,
						url : "${ctx}/frs/validatereport/getIdxTaskType", 
						type : "get", 
						dataType : "json",
						success : function(result) {
							if(result.taskTypeList){
                                $.ligerui.get('busiTypeBox').setData (result.taskTypeList);
                            }
						}
					});
				}
			},
			validate : {
				required : true
			}
		}, {
			display: '报表',
			name :'rptIdx',
			comboboxName:'rptIdxBox',
			type: 'select',
			newline: false,
			options : {
				onBeforeOpen : function() {
					var busiType = $("#busiType").val();
					if(busiType){
						BIONE.commonOpenDialog("报表选择","rptIdxTreeWin", $(document).width()-100, $(document).height()-50,
								'${ctx}/report/frame/design/cfg/rptIdxTreeWin?busiType='+ busiType,null);
					}else{
						BIONE.tip("请选择业务类型！");
					}
					return false;
				}
			},
			validate:{
				required: true
			}
		},{
			display: '机构',
			name :'orgNm',
			comboboxName:'orgNm_sel',
			type: 'select',
			newline: true,
			options : {
				onBeforeOpen : function() {
					var busiType = $("#busiType").val();
					if(busiType){
						window.BIONE.commonOpenDialog("机构树", "taskOrgWin", $(document).width()-100, $(document).height()-50, 
								"${ctx}/frs/integratedquery/rptquery/searchOrgSetInfo?orgType=" + busiType, null);
					}else{
						BIONE.tip("请选择业务类型！");
					}
					return false;
				}
			},
			validate:{
				required: true
			}
		}, {
			display : "填报人",
			name : 'fillEr',
			type : "text",
			newline : false,
			validate : {
				required : true
			}
		}, {
			display : "负责人",
			name : 'charger',
			type : "text",
			newline : true,
			validate : {
				required : true
			}
		}, {
			display : "复核人",
			name : 'auditor',
			type : "text",
			newline : false,
			validate : {
				required : true
			}
		},{
			display : "电话",
			name : 'phoneNumber',
			type : "text",
			newline : true
		}, {
			display : "备注",
			name : 'remark',
			type : "textarea",
			newline : true,
			width : 493,
			attr : {
				style : "resize: none;"
			},
			validate : {
				maxlength : 100
			}
		}];
		
		var mainform = $("#mainform").ligerForm({
			inputWidth : 170,
			labelWidth : 100,
			labelAlign : 'right',
			space : 40,
			fields : field
		});
		
		var buttons = [{
			text:'取消',
			onclick:function(){
				BIONE.closeDialog("baseEdit");
			}
		}, {
			text : '保存',
			onclick : saveUser
		}];
		BIONE.addFormButtons(buttons);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	}
	
	function saveUser() {
		BIONE.submitForm($("#mainform"), function(result) {
			if(result){
				window.parent.BIONE.tip("保存成功");
				BIONE.closeDialog("baseEdit");
			}
		}, function() {
			BIONE.tip("保存失败");
		});
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/report/frame/design/cfg/saveUser"></form>
		<div id="bottom">
			<div class="form-bar">
				<div class="form-bar-inner" style="padding-right:20px"></div>
			</div>
		</div>
	</div>
</body>
</html>