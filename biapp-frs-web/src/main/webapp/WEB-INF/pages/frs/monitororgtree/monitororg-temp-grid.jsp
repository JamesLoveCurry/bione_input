<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp"/>
<head>
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
		initOrgShow(orgType)
	}); 
		
	//创建表单结构 
	function ligerFormNow() {
		var mainform = $("#mainform").ligerForm({
			inputWidth : 150,
			labelWidth : 130,
			space : 40,
			fields : [{
				display : "对应行内机构 ",
				name : "mgrOrgNo",
				newline : true,
				type : "select",
				comboboxName : "mgrOrgNm",
				options:{
					onBeforeOpen:function(){
						if(!"${orgNo}"){
							window.parent.selectTab=window;
							parent.BIONE.commonOpenDialog(
									"机构树",
									"chooseOrg",
									400,
									$(window).height()-10,
									"${ctx}/frs/systemmanage/orgmanage/chooseInterOrg?orgType=${orgType}",
									null
								);
						}
							return false;
					}
				},
				validate : {
					required : true,
					messages : {
						required : "该字段不能为空。"
					}
				},
				group : "前置信息",
				groupicon : groupicon
			}, {
				display : "机构名称",
				name : "orgNm",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 100,
					remote : {url:"${ctx}/frs/systemmanage/orgmanage/getOrgNm",
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
				group : "基本信息",
				groupicon : groupicon
			}, {
				display : "机构编号",
				name : "orgNo",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 100,
					remote : {url:"${ctx}/frs/systemmanage/orgmanage/getOrgNo",
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
				}
			},
			{
				display : "机构类型",
				name : "orgType",
				newline : true,
				type : "select",
				comboboxName:"types",
				options:{
					url : "${ctx}/report/frame/datashow/idx/busiTypeList.json?isPublic=Y"
				}
			},
			{
				display : "上级机构",
				name : "upOrgNo",
				newline : false,
				type : "select",
				comboboxName : "upOrgNoCom",	
				options:{
					onBeforeOpen:function(){
						window.parent.selectTab=window;
						parent.BIONE.commonOpenDialog(
								"机构树",
								"chooseOrg",
								400,
								$(window).height()-100,
								"${ctx}/frs/systemmanage/orgmanage/chooseUpOrg?orgType=${orgType}",//把编号转为机构名称显示
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
			}, {
				display : "金融机构编码(银监会)",
				name : "financeOrgNo",
				newline : true,
				type : "text",
				validate : {
					required : false,
					maxlength : 100
				}
			} , {
				display : "是否参与汇总",
				name:"orgSumType",
				newline : false,
				type : "select",
				comboboxName : "sum",
				options:{
					data:[{
						id:"01",
						text:"是"
					},{
						id:"02",
						text:"否"
					}]
			},
				validate:{
					required : true,
					maxlength : 100,
					messages:{
						required : "该字段不能为空。"
					}
				}
			},{
				display : "是否虚拟机构",
				name:"isVirtualOrg",
				comboboxName:"isVirtualOrgBox",
				newline:true,
				type:"select",
				cssClass:"field",
				options :{
					readonly : false,
					initValue : "N",
					data :[{
						text:"是",
						id : "Y"
					},{
						text:"否",
						id :"N"
					}],
					onSelected : function(id, text) {
						if (id && id == "Y") {
							$("#mgrOrgNm").removeAttr("validate");
							$("#mgrOrgNo").parent().parent().parent().hide();
							if("${orgNo}"){
								$("#mainform [name='orgNo']").attr("readonly", "true");
								$.ligerui.get("orgNo").updateStyle();
							}else{
								$("#mainform [name='orgNo']").removeAttr("readonly");
								$.ligerui.get("orgNo").updateStyle();
							}
						}else{
							$("#mgrOrgNo").parent().parent().parent().show();
							$("#mgrOrgNm").attr("validate",'{"required":true,"messages":{"required":"该字段不能为空。"}}');
							$("#mainform [name='orgNo']").attr("readonly", "true");
							$.ligerui.get("orgNo").updateStyle();
						}
					}
				},
					validate: {
					required: true
					}
			},{
				display : "机构定义类型",
				name : "orgClass",
				newline : false,
				type : "select",
				comboboxName : "orgClassBox",
				options:{
					url : "${ctx}/frs/systemmanage/orgmanage/orgClassList.json",
					onSelected : function(id, text) {
						var orgLevel = "";
						if(id == "总行"){
							orgLevel = "1";
						}else if(id == "分行"){
							orgLevel = "2";
						}else if(id == "支行"){
							orgLevel = "3";
						}
						$("#mainform input[name='orgLevel']").val(orgLevel);
					},
					cancelable:true
				}
			},{
				display : "机构层级",
				name : "orgLevel",
				newline : true,
				type : "text",
				options :{
					readonly : true
				}
			},{
				display : "是否是报送机构",
				name : "isOrgReport",
				newline : false,
				type : "select",
				comboboxName : "isOrgReportBox",
				options:{
					initValue : "Y",
					data :[{
						text:"是",
						id : "Y"
					},{
						text:"否",
						id :"N"
					}],
					cancelable: true
				}
			},
				{
					display : "金融机构LEI编码",
					name : "leiNo",
					newline : true,
					type : "text",
					validate : {
						required : false,
						maxlength : 100
					}
				},
				{
					display : "关联归属地",
					name : "addr",
					newline : false,
					type : "select",
					comboboxName:"addrCombo",
					options:{
						url : "${ctx}/report/frame/datashow/idx/addrList.json",
						cancelable: true
					}
				},
				{
				display : "地区名称(人行)",
				name : "rhOrgNm",
				newline : true,
				type : "text"
			},{
				display : "地区编码(人行)",
				name : "rhOrgNo",
				newline : false,
				type : "text"
			},{
				display : "人行机构编码(人行)",
				name : "dtrctNo",
				newline : true,
				type : "text"
			}]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#mainform"));
	}
	
	function loadDate(){
		if("${orgNo}"){
			var flag = "rh03";//人行标识 add by lxp 2017 0614
			BIONE.ajax({
			    url :  "${ctx}/frs/systemmanage/orgmanage/getBlankOrgInfo?orgNo=${orgNo}&orgType="+orgType+"&sourceOrgNo=${orgNo}"+"&flag="+flag
			}, function(data){//修改
				liger.get("types").selectValue(data.id.orgType);
				$("#mainform [name='orgType']").attr("readonly", "true").val(data.id.orgType);
				$.ligerui.get("types").setDisabled();
				liger.get("addrCombo").selectValue(data.addr);
				$("#types").parent().css('background', '#d0d0d0');
				$("#types").css('color','#333');
				liger.get("sum").selectValue(data.orgSumType);
		 		$("#mainform [name='orgNo']").attr("readonly", "true").val(data.id.orgNo);
		 		$.ligerui.get("orgNo").updateStyle();
				$("#mainform input[name='orgNm']").val(data.orgNm);
				$("#mainform input[name='rhOrgNm']").val(data.rhOrgNm);
				$("#mainform input[name='rhOrgNo']").val(data.rhOrgNo);
				$("#mainform input[name='dtrctNo']").val(data.dtrctNo);
				liger.get("mgrOrgNm").selectValue(data.mgrOrgNo);
				liger.get("mgrOrgNm").setText(data.mgrOrgNm);
				$("#mgrOrgNm").parent().css('background', '#d0d0d0');
				$("#mgrOrgNm").css('background', '#d0d0d0');
				$("#mainform input[name='financeOrgNo']").val(data.financeOrgNo);
				$("#mainform input[name='leiNo']").val(data.leiNo);
				liger.get("isVirtualOrgBox").selectValue(data.isVirtualOrg);
				liger.get("upOrgNoCom")._changeValue(data.upOrgNo, data.upOrgNm);
				liger.get("orgClassBox").selectValue(data.orgClass);
				$("#mainform input[name='orgLevel']").val(data.orgLevel);
				liger.get("isOrgReportBox").setValue(data.isOrgReport);
			});
		}
		else{//新增
			liger.get("types").selectValue(orgType);
			$("#mainform [name='id.orgType']").attr("readonly", "true").val(orgType);
			$("#mainform [name='orgNo']").attr("readonly", "true");
			$.ligerui.get("orgNo").updateStyle();
			$.ligerui.get("types").setDisabled();
			$("#types").css('color','#333');
			$("#types").parent().css('background', '#d0d0d0');
	 		$("#mainform input[name='orgNo']").val("${orgNo}");
			liger.get("sum").selectValue("01");
			liger.get("upOrgNoCom")._changeValue("${upOrgNo}", window.parent.currentNode.text);
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
			BIONE.submitForm($("#mainform"),function(){
				window.parent.refreshTree();
				tabObj.removeTabItem("tab");
				$("#mainform").html("");
				$("#bottom").remove();
				$(".l-dialog-btn").remove();
				BIONE.tip("保存成功");
			});
		}
	}
		
	function initOrgShow(id){
		if(id != "03"){
			$("#rhOrgNm").parent().parent().parent().parent().hide();
			$("#rhOrgNo").parent().parent().parent().parent().hide();
			$("#dtrctNo").parent().parent().parent().parent().hide();
		}
	}
</script>

<title>机构管理</title>
</head>
<body>

	<div id="template.center">
		<form name="mainform" method="get" id="mainform" action="${ctx}/frs/systemmanage/orgmanage/saveInfo">
			<input type="hidden" id="sourceOrgNo" name="sourceOrgNo">
			<input type="hidden" id="type" name="type">
			<input type="hidden" id="lastUpOrgNo" name="lastUpOrgNo">
		</form>
	</div>
</body>
</html>