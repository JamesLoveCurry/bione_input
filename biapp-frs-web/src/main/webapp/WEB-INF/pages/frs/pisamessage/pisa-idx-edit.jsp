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
	    		display : '指标代码',
	    		name : 'idxCode',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
	    		display : '指标名称',
	    		name : 'idxNm',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
	    		display : '维度代码',
	    		name : 'dimCode',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
	    		display : '维度名称',
	    		name : 'dimNm',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 200
	    		}
    	    },{
    	    	//数据类型,是否按机构报送,地区类型,报送频度 "pisaIdxDataType,pisaCom,areaType,pisaSubmitFreq"
	    		display : '数据类型',
	    		name : 'dataType',
	    		newline : true,
	    		comboboxName: "dataTypeBox",
	    		type : "select",
				cssClass : "field",
				options:{
					url: "${ctx}/frs/pisamessage/getComboInfo.json?paramTypeNo=pisaIdxDataType&d=" + new Date()
				},
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    } ,{
    	    	display:"按机构报送",
    			name : "isOrgSubmit",
    			newline : false,
    			comboboxName:"isOrgSubmitBox",
	    		type : "select",
				cssClass : "field",
				options:{
					url: "${ctx}/frs/pisamessage/getComboInfo.json?paramTypeNo=pisaCom&d=" + new Date()
				},
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
	    		display : '地区类型',
	    		name : 'belongAddr',
	    		newline : true,
	    		comboboxName:"belongAddrBox",
	    		type : "select",
				cssClass : "field",
				options:{
					url: "${ctx}/frs/pisamessage/getComboInfo.json?paramTypeNo=areaType&d=" + new Date()
				},
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    } ,{
	    		display : '报送频度',
	    		name : 'submitFreq',
	    		newline : false,
	    		comboboxName:"submitFreqBox",
	    		type : "select",
				cssClass : "field",
				options:{
					url: "${ctx}/frs/pisamessage/getComboInfo.json?paramTypeNo=pisaSubmitFreq&d=" + new Date()
				},
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
    	    	group : "配置信息",
	    		groupicon : groupicon,
	    		display : '报表编号',
	    		name : 'rptNum',
	    		newline : true,
	    		type : 'select',
	    		comboboxName : "rptNum_sel",
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		},options : {
					onBeforeOpen : selectRptDialog,
					selectBoxHeight : '150'
				}
    	    } ,{
	    		display : '值1单元格',
	    		name : 'val1CellNum',
	    		newline : true,
	    		type : 'text',
	    		validate : {
	    		    required : true,
	    		    maxlength : 32
	    		}
    	    },{
	    		display : '值2单元格',
	    		name : 'val2CellNum',
	    		newline : false,
	    		type : 'text',
	    		validate : {
	    		    maxlength : 32
	    		}
    	    },{
	    		name : 'pisaIdxId',
	    		type : 'hidden'
    	    } ]
    	});

    	jQuery.metadata.setType("attr", "validate");
    	BIONE.validate($("#mainform"));

    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("pisaIdxEidtWin", null);
    	    }
    	});
    	buttons.push({
    	    text : '保存',
    	    onclick : f_save
    	});

    	BIONE.addFormButtons(buttons);
    }
    
    function initFormData(){
    	//增加默认数据
    	 $("#val2CellNum").val('*');
    	if(id){
    		BIONE.loadForm(mainform, {
    		    url : "${ctx}/frs/pisamessage/getPisaIdxById?id=${id}"
    		});
    	}
    }
    
  	//保存
    function f_save() {
    	var val1CellNum = $("#val1CellNum").val();
    	var val2CellNum = $("#val2CellNum").val();
    	var rptNum = $("#rptNum").val();
    	$.ajax({
			async : false,
			type : "post",
			url : '${ctx}/frs/pisamessage/validateCellNum',
			data : {
				rptNum : rptNum,
				val1CellNum : val1CellNum,
				val2CellNum : val2CellNum
			},
			success : function(res){
				if(res == false){
					$.ligerDialog.warn('[通用报表定制]中值1单元格或值2单元格未配置指标，无法保存！');
				}else{
					//校验通过 保存
					BIONE.submitForm($("#mainform"), function() {
					    BIONE.closeDialogAndReloadParent("pisaIdxEidtWin", "maingrid", "添加成功");
					}, function() {
					    BIONE.closeDialog("pisaIdxEidtWin", "添加失败");
					});
				}
			},
			error : function(e){
			}
		});
    }
  	
  //弹出窗口
    function selectRptDialog(options) {
    	var height = $(window).height() - 50;
		var width = $(window).width() - 80;
		$.ligerDialog.open({
			name:'addRptWin',
			title : '报表选择',
			width : width,
			height : height,
			url : '${ctx}/frs/pisamessage/selectRptDialog',
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
			$("#mainform input[name='rptNum_sel']").val(data.rptNum);
			$("#mainform input[name='rptNum']").val(data.rptNum);
		}
		dialog.close();
	}
	//取消按钮调用方法
	function f_selectCancel(a,dialog){
		dialog.close();
	}
  	
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action="${ctx}/frs/pisamessage/savePisaIdx"></form>
	</div>
</body>
</html>