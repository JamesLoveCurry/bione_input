<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp"/>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var orgNo="${orgNo}";
	var orgType = "${orgType}";
	var upOrgNo = "${upOrgNo}";
	var mgrOrgNo = "${mgrOrgNo}";
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var tabObj = window.parent.tabObj;
	$(function(){
		$("#sourceOrgNo").attr("value", orgNo);
		$("#type").attr("value", orgType);
		$("#lastUpOrgNo").attr("value", upOrgNo);
		ligerFormNow();
		loadDate();
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
		
	}); 
		
		
				//创建表单结构 
				function ligerFormNow() {
					var mainform = $("#mainform").ligerForm({
						inputWidth : 150,
						labelWidth : 90,
						space : 40,
						fields : [{
							name : "id.orgType",
							newline : true,
							type : "hidden"
						},{
							display : "机构编号",
							name : "orgNo",
							newline : true,
							type : "text",
							validate : {
								required : true,
								maxlength : 100,
								remote : {url:"${ctx}/rpt/frame/rptorg/getOrgNo",
									type : "post",
									  data : {
										  orgType:orgType,
										  upOrgNo:upOrgNo,
										  sourceOrgNo:"${orgNo}"
									  }
								},
								messages : {
									remote : "该路径下已存在同名机构编号。"
								}
							},
							group : "基本信息",
							groupicon : groupicon
						},{
							display : "机构名称",
							name : "orgNm",
							newline : false,
							type : "text",
							validate : {
								required : true,
								maxlength : 100,
								remote : {url:"${ctx}/rpt/frame/rptorg/getOrgNm",
									type : "post",
									  data : {
										  orgType : orgType,
										  upOrgNo : upOrgNo,
										  sourceOrgNo : "${orgNo}"
									  }
								},
								messages : {
									remote : "该路径下已存在同名机构名。"
								}
							},
						}, 
						{
							display : "上级机构",
							name : "upOrgNo",
							newline : true,
							type : "select",
							comboboxName : "upOrgNoCom",	
							options:{
								onBeforeOpen:function(){
									window.parent.selectTab=window;
									parent.BIONE.commonOpenDialog(
											"机构树",
											"chooseOrg",
											400,
											380,
											"${ctx}/rpt/frame/rptorg/chooseUpOrgNo?orgType=${orgType}",
											null
										);
										return false;
								}
							},
							validate : {
								required : true,
								maxlength : 100,
								messages : {
									required : "该字段不能为空。"
								}
							}
						},
						{
							display : "机构汇总类型",
							name:"orgSumType",
							newline : false,
							type : "select",
							comboboxName : "sum",
							options:{
								data:[{
									id:"01",
									text:"基础行"
								},{
									id:"02",
									text:"汇总行"
								}]
						},
							validate:{
								required : true,
								maxlength : 100,
								messages:{
									required : "该字段不能为空。"
								}
							}
						}/* ,{
							display : "是否虚拟机构",
							name:"isVirtualOrg",
							comboboxName:"isVirtualOrgBox",
							newline:false,
							type:"select",
							cssClass:"field",
							options :{
								data :[{
									text:"是",
									id : "Y"
								},{
									text:"否",
									id :"N"
								}]
							}
						} *//* ,{
							name:"upOrgNo",
							type:"hidden"
						} */]
					});
					jQuery.metadata.setType("attr", "validate");
					BIONE.validate($("#mainform"));
					
				}
				
				function loadDate(){
					if("${orgNo}" != ""){
						BIONE.ajax({
						    url :  "${ctx}/rpt/frame/rptorg/getOrgInfo?orgNo=${orgNo}&orgType="+orgType+"&sourceOrgNo=${orgNo}"
						}, function(data){//修改
							$("#mainform [name='id.orgType']").val(data.id.orgType);
							$("#types").css('color','#333');
							liger.get("sum").selectValue(data.orgSumType);
					 		$("#mainform [name='orgNo']").attr("readonly", "true").removeAttr("validate").val(data.id.orgNo);
					 		$.ligerui.get("orgNo").updateStyle();
							$("#mainform input[name='orgNm']").val(data.orgNm);
							liger.get("upOrgNoCom")._changeValue(data.upOrgNo, data.upOrgNm);
						});
					}
					else{//新增
						$("#mainform [name='id.orgType']").val(orgType);
				 		$("#mainform input[name='orgNo']").val("${orgNo}");
						liger.get("sum").selectValue("01");
						var upNm = window.parent.currentNode.text;
						if(window.parent.currentNode.id == "0")
							upNm = "无";
						liger.get("upOrgNoCom")._changeValue("${upOrgNo}", upNm);
					}
					 var buttons = [];
						buttons.push({
							text : '取消',
							onclick : function() {
								tabObj.removeTabItem("tab");
								$("#mainform").html("");
								$("#bottom").remove();
								$(".l-dialog-btn").remove();
							}
						});

						buttons.push({
							text : '保存',
							onclick : f_save
						});
						
					BIONE.addFormButtons(buttons);
					
					function f_save(){
						var node = window.parent.currentNode;
						if("${orgNo}" != ""){
							node = window.parent.currentNode.getParentNode();
						}
						BIONE.submitForm($("#mainform"),function(){
							window.parent.refreshTree(node);
							tabObj.removeTabItem("tab");
							$("#mainform").html("");
							$("#bottom").remove();
							$(".l-dialog-btn").remove();
							BIONE.tip("保存成功");
						});
					}
				}
			
</script>

<title>机构管理</title>
</head>
<body>

	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/frame/rptorg/addInfo">
			<input type="hidden" id="sourceOrgNo" name="sourceOrgNo">
			<input type="hidden" id="type" name="type">
			<input type="hidden" id="lastUpOrgNo" name="lastUpOrgNo">
		</form>
	</div>
</body>
</html>