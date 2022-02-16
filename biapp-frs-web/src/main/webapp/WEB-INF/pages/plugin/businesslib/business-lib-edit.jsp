<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    
    var id = "${id}"
    
    $(function(){
    	initForm();
    	initFormData();
    });
    
    function initForm() {
    	$("#mainform").ligerForm({
    	    fields : [ {
	    		group : "业务信息",
	    		groupicon : groupicon,
	    		display : '业务库编号',
	    		name : 'busiLibNo',
	    		newline : true,
	    		type : 'text',
				validate : {
					required : true,
					maxlength : 32,
					remote : {
						url : "${ctx}/rpt/frame/businesslib/validateValue",
						type : "POST",
						data : {
							field : 'busiLibNo',
							"id" : id
						}
					},
					messages : {
						remote : "此编号已存在"
					}
				}
    	    },{
	    		display : '业务库名称',
	    		name : 'busiLibNm',
	    		newline : false,
	    		type : 'text',
	    		validate : {
					required : true,
					maxlength : 32,
					remote : {
						url : "${ctx}/rpt/frame/businesslib/validateValue",
						type : "POST",
						data : {
							field : 'busiLibNm',
							"id" : id
						}
					},
					messages : {
						remote : "此名称已存在"
					}
				}
    	    },{
    	    	group : "配置信息",
	    		groupicon : groupicon,
	    		display : '数据源名称',
	    		name : 'dsId',
	    		newline : true,
	    		comboboxName: "dsIdBox",
	    		type : "select",
				cssClass : "field",
				options:{
					url: "${ctx}/rpt/frame/businesslib/getComboInfo.json?typeNo=dsIdBox&d=" + new Date().getTime()
				},
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    }/* ,{
				display : '事实表配置',
				name : "execType",
				newline : false,
				type : "select",
				validate : {
					required : true
				},
				options : {
					data : [ {
						text : '手动',
						id : '0'
					}, {
						text : '自动',
						id : '1'
					} ],
					onSelected : function(val) {
						setCss();
						setRemarkValue();
					}
				}

		
    	    }*/ ,{
    	    	display:"数据集名称",
    			name : "setId",
    			newline : true,
    			comboboxName:"setIdBox",
	    		type : "select",
				cssClass : "field",
				width : 500,
				validate : {
					required : true 
				},
				options : {
					onBeforeOpen : selectDialog,
					selectBoxHeight : '150'
				}
    	    },{
				display:"说明",
				name : "remark",
				newline : true,
				type : "textarea",
				width : 500,
				cssClass : "field"
			},{
	    		name : 'busiLibId',
	    		type : 'hidden'
    	    } ]
    	});

    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));

    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("editDialogWin", null);
    	    }
    	});
    	buttons.push({
    	    text : '保存',
    	    onclick : f_save
    	});

    	BIONE.addFormButtons(buttons);
    	
    	$("#mainform input[name=setId]").parent("div").attr("title","事实表隐藏");
    }
    
    function initFormData(){
    	if(id){
    		var option = {
    				url : "${ctx}/rpt/frame/businesslib/getBusiLibById?id=${id}"
    		}
    		BIONE.ajax(option, function(result){
    			$("#busiLibNo").val(result.busiLibNo);
    			$("#busiLibNm").val(result.busiLibNm);
    			$.ligerui.get("dsIdBox").setValue(result.dsId);
    			$.ligerui.get("setIdBox").setValue(result.setId);
    			$.ligerui.get("setIdBox").setText(result.setId);
    			$("#remark").val(result.remark);
    		});
    	}
    }
    
    function setCss(){
    	var val = $("#mainform input[name=execType]").val();
		if (val == "0") {
			$("#mainform input[name=setId]").parent("div").removeClass("l-text-disabled").parent().parent("ul").show().find("input").removeAttr("disabled").css("color", "black");//显示执行方式 
		}else{
			$("#mainform input[name=setId]").parent().parent().parent("ul").hide().find("input").attr("disabled", "disabled");
		}
    }
    
    function selectDialog () {
		var dsId = $("#mainform input[name='dsId']").val();
		var url = "${ctx}/rpt/frame/businesslib/selectTable?dsId="+dsId;
		
    	$.ligerDialog.open({
			name:'selectGridWin',
			title : '请选择物理表',
			width : 650,
			height : 400,
			url : url,
			buttons : [ {
				text : '确定',
				onclick : f_selectOK
			}, {
				text : '取消',
				onclick : f_selectCancel
			} ]
		});
		return false;
    }
    
  	//保存按钮调用方法
	function f_selectOK(a,dialog){
		var data = dialog.frame.addToParent();
		if(data!=""){
			$("#mainform input[name='setIdBox']").val(data.setNm);
			$("#mainform input[name='setId']").val(data.setId);
			dialog.close();
		}
	}
	//取消按钮调用方法
	function f_selectCancel(a,dialog){
		dialog.close();
	}
  	//保存
    function f_save() {
		BIONE.submitForm($("#mainform"), function() {
		    BIONE.closeDialogAndReloadParent("editDialogWin", "maingrid", "新增成功");
		}, function() {
		    BIONE.closeDialog("editDialogWin", "新增失败");
		});
    }
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/rpt/frame/businesslib/saveBusiLib"></form>
	</div>
</body>
</html>