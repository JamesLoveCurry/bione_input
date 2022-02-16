<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style type="text/css">
	.l-group-hasicon span {
	    display: block;
	    width: 300px;
	}
</style>
<script type="text/javascript">
	jQuery.validator.addMethod("rowNumValidate", function(value, element) {
	    var numReg = /^[1-9]\d*$/;
	    return this.optional(element) || (numReg.test(value));
	}, "必须输入数字！");
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var tmpId = "${tmpId}";
	var dataDate = "${dataDate}";
	var orgNo = "${orgNo}";
	var moduleInfos = [];
	$(function() {
		//加载报表配置的模型信息
		initRptSourceDs();
	});
	
	function initRptSourceDs(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frs/rptfill/initRptSourceDs",
			dataType : 'json',
			data : {
				tmpId : tmpId,
				dataDate : dataDate
			},
			type : "post",
			success : function(result){
				if(result != null){
					moduleInfos = result;
					initForm();
				}else{
					BIONE.tip("当前报表没有配置模型字段，请修改报表配置！")
				}
				
			},
			error:function(){
				BIONE.tip("数据加载异常，请联系系统管理员");
			}
		});
	}
	
	//创建表单结构 
	function initForm() {
		var fields = [];
		for(var i=0; i<moduleInfos.length; i++){
			var field = {
				display : "数据截止行号",
				name : "rowNum"+i,
				newline : true,
				type : "text",
				group : moduleInfos[i].setNm,
				groupicon : groupicon,
				validate : {
					required : true,
					rowNumValidate : true
				}
			};
			fields.push(field);
		}
		var mainform = $("#mainform").ligerForm({
			inputWidth : 170,
			labelWidth : 170,
			labelAlign : 'right',
			space : 40,
			fields : fields
		});
		
		var buttons = [];
		buttons.push({
			text : '确定',
			onclick : onConfirm
		},{
			text : '取消',
			onclick : function(){
				BIONE.closeDialog("detailCfgWin");
			}
		});
		BIONE.addFormButtons(buttons);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	}
	
	function onConfirm() {
		var moduleParam = [];
		for(var i=0; i<moduleInfos.length; i++){
			if($("#rowNum"+i).val() != null && $("#rowNum"+i).val() != ""){
				moduleInfos[i].rowNum = $("#rowNum"+i).val();
				moduleParam.push(moduleInfos[i]);
			}
		}
		window.parent.BIONE.commonOpenDialog('导入数据文件', 'uploadWin', 600, 330,
				'${ctx}/rpt/frs/rptfill/detailUpload?tmpId='+tmpId+'&dataDate='+dataDate+'&orgNo='+orgNo+'&moduleParam='+JSON.stringify(moduleParam));
		BIONE.closeDialog("detailCfgWin");
	}
</script>
</head>
<body>
	<div id="template.center">
		<div style="height: 340px;">
			<form id="mainform" method="post" id="mainform" action="">
			</form>
		</div>
		<div style="height: 60px;padding: 0 20px;color: red;">
			注：覆盖导入后，将会删除当前日期、机构下的所有数据，请谨慎操作！
			 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 输入的截止行号为excel中数据的最后一行，请输入正确的截止行号！
		</div>
		<div id="bottom">
			<div class="form-bar">
				<div class="form-bar-inner" style="padding-bottom:20px;padding-right:20px"></div>
			</div>
		</div>
	</div>
</body>
</html>