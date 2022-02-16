<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var mainform;
	var groupId = '${groupId}';
	var grpType = '${grpType}';
	$(function(){
		initForm();
		var btns = [{
			text : "取消",
			onclick : cancel
		},{
			text : "保存",
			onclick : save
		}];
		BIONE.addFormButtons(btns);
		//修改回显
		if(groupId){
			$.ajax({
				async:true,
				type:"GET",
				dataType:"json",
				url:"${ctx}/report/frame/validgroup/getValidGroupById?groupId="+groupId,
				success:function(res){
					if(res.grp){
						$.ligerui.get("groupType")._changeValue(res.grp.groupType,getGroupByType(res.grp.groupType).grpType);
						$("#mainform input[name=groupId]").val(groupId);
						$("#mainform input[name=groupNo]").val(res.grp.groupNo);
						$("#mainform input[name=groupNm]").val(res.grp.groupNm);
						$("#mainform textarea[name=remark]").val(res.grp.remark);
					}
				},
				error : function(res) {
					//BIONE.hideLoading();
				}
			});
		}
	})
	
	function initForm() {
		var vGid = {
			display : '',
			name : "groupNo",
			newline : false,
			labelWidth : "80",
			type : "text",
			validate : {
				required : true,
				messages : {
					required : "组编号不能为空"
				}
			}
		}
		var vGnm = {
			display : '',
			name : "groupNm",
			labelWidth : "80",
			newline : false,
			type : "text"
		}
		
		vGid.display = getGroupByType(grpType).displayId;
		vGnm.display = getGroupByType(grpType).displayNm;
		
		mainform = $("#mainform").ligerForm({
			fields : [{
				display : 'groupId',
				name : 'groupId',
				type : "hidden"
			},{
				display : '校验组类型',
				name : 'groupType',
				labelWidth : "80",
				newline : false,
				type : "select",
				options :{
					initValue : grpType,
					data :[{
						id : "02",
						text : "指标组"
					},{
						id : "05",
						text : "报表组"
					}]
				},
				validate : {
					required : true,
					messages : {
						required : "校验组类型不能为空"
					}
				}
			}, vGid,vGnm,{
				display : '备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
				name : "remark",
				labelWidth : "80",
				newline : false,
				type : "textarea",
				width : 410
			}]
		});
		
		$.ligerui.get("groupType").setDisabled();
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate("#mainform");
	}
	
	function save(){
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("validDialog", "maingrid","保存成功");
		}, function() {
		    BIONE.closeDialog("validDialog", "保存成功");
		});
	}
	
	function cancel(){
		BIONE.closeDialog("validDialog");
	}
	
	function getGroupByType(type){
		var grp = {};
		switch(type){
			case "02":
				grp.grpType = "指标组";
				grp.displayId = "指标组编号"
				grp.displayNm = "指标组名称";
				break;
			case "05":
				grp.grpType = "报表组";
				grp.displayId = "报表组编号"
				grp.displayNm = "报表组名称";
				break;
			default :
				grp.grpType = type;
				grp.displayId = type;
				grp.displayNm = type;
		}
		return grp;
	}
</script>
</head>
<body>
<div id="template.center">
	<form id="mainform" action="${ctx}/report/frame/validgroup" method="post"></form>
</div>
</body>
</html>