<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">

	//报表模板Id
	var base_TmpId = window.parent.base_TmpId;
	//报表模板版本号
	var base_verId = window.parent.base_verId;
	//数据日期
	var base_DataDate = window.parent.base_DataDate;
	//机构编号
	var base_OrgNo = window.parent.base_OrgNo;
	var mainform = null;
	
	//保存数据模型ID
	var setId = "";
	
	$(function() {
		getModelCol();
		initBtn();
	});
	
	//找到当前报表配置的明细模型
	function getModelCol(){
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frs/rptfill/getModelCoByTmp",
			dataType : 'json',
			data : {
				templateId : base_TmpId,
				verId : base_verId
			},
			type : "post",
			beforeSend : function() {
				BIONE.showLoading("加载中...");
			},
			success : function(result){
				if(result && result.moduleInfos){
					var moduleInfos = result.moduleInfos;
					strForm(moduleInfos);
				}
				BIONE.hideLoading();
			}
		});
	}
	
	//构造新增数据表单
	function strForm(moduleInfos){
		var field = [];
		var orgNoCol = "";
		var dataDateCol = "";
		for(var i = 0 , j = moduleInfos.length; i < j; i++){
			setId = moduleInfos[i].setId;
			var readonly = false;
			if(moduleInfos[i].dimTypeNo == "ORG"){
				orgNoCol = moduleInfos[i].enNm;
				readonly = true;
			} else if(moduleInfos[i].dimTypeNo == "DATE"){
				dataDateCol = moduleInfos[i].enNm;
				readonly = true;
			}
			var moduleCol = {
					display : moduleInfos[i].cnNm ? moduleInfos[i].cnNm : moduleInfos[i].enNm,
					name : moduleInfos[i].enNm,
					newline : (i%2 == 0)?true : false,
					type : "text",
					attr:{ 
						readonly : readonly
					},
					validate : {
						required : (moduleInfos[i].isPk == "Y" )? true : false
					}
			}
			field.push(moduleCol);
		}
		mainform = $("#mainform").ligerForm({
			inputWidth : 130,
			space : 60,
			fields : field
		});
		$.ligerui.get(orgNoCol).setValue(base_OrgNo);
		$.ligerui.get(dataDateCol).setValue(base_DataDate);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}
	
	function initBtn() {
		var btns = [  {
			text : '保存',
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
	}
	
	function f_save(){
		//获取要保存的数据
		var saveData = mainform.getData();
		$.ajax({
		   cache : false,
		   async : true,
		   url: "${ctx}/rpt/frs/rptfill/saveModuleData", 
		   dataType : 'json',
		   type: "post",  
		   data:{
			   saveData : JSON2.stringify(saveData),
			   setId : setId,
			   templateId : base_TmpId,
			   dataDate : base_DataDate,
			   orgNo : base_OrgNo
		   },
		   beforeSend : function() {
				BIONE.showLoading("正在保存数据中...");
			},
			complete : function() {
				BIONE.hideLoading();
			},
		   success: function(data){
			   if(data.msg){
				   BIONE.showError(data.msg);
			   }else{
				   window.parent.BIONE.tip("新增数据成功");
				   window.parent.tmp.viewInit(false);
				   BIONE.closeDialog("addRows");  
			   }
		   }
		}); 
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="form">
			<form id="mainform" action="" method="post"></form>
		</div>
	</div>
</body>
</html>