<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    $(function() {
		var mainform = $("#form1").ligerForm({
		    fields : [ {
			    display : '模块编号',
				name : 'moduleId',
				type : 'text',
				group : "首页模块信息",
				groupicon : groupicon,
				validate : {
				    required : true,
				    maxlength : 32,
				    remote : "${ctx}/bione/mainpage/module/validate",
				    messages : {
						remote : "模块编号已存在"
				    }
				}
		    },{
				display : '模块名称',
				name : 'moduleName',
				newline : false,
				type : 'text',
				validate : {
				    required : true,
				    maxlength : 32
				}
		    },{
				display : "模块图标",
				name : "picPath",
				newline : true,
				type : "select",
				options : {
					onBeforeOpen : showIconDialog,
					url : "${ctx}/bione/admin/func/buildIconCombox.json",
					ajaxType : "get"
				},
				validate : {
					required : false
				}
			},{
				display : '模块类型',
				name : 'moduleType',
				newline : true,
				type : 'select',
				options : {data : [ { 'id' : '01', 'text' : 'poc演示图表' },
									 { 'id' : '02', 'text' : 'SQL图表' },
									 { 'id' : '03', 'text' : '引擎报文'},
									 { 'id' : '11', 'text' : 'SQL表格'},
									 { 'id' : '12', 'text' : '报表'},
									 { 'id' : '99', 'text' : '自定义'}],
							onSelected : function(val) {
								setAssemblyShow();
						}},
				validate : {
				    required : true
				},
		    },{
				display : '图表类型',
				name : 'chartType',
				newline : false,
				type : 'select',
				options : {data : [ { 'id' : '01', 'text' : '折线图' },
									 { 'id' : '02', 'text' : '柱状图' },
									 { 'id' : '03', 'text' : '饼图'}]},
				validate : {
				    required : false
				},
		    },{
				display : '模型定义',
				name : 'moduleDefine',
				newline : true,
				width : '443',
				validate : {
				    maxlength : 500
				},
				type : 'textarea',
				validate : {
				    required : false
				},
		    },{
				display : '模块路径',
				name : 'modulePath',
				newline : true,
				type : 'text',
				width : '443',
				validate : {
				    required : false
				}
		    },{
				display : '备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
				name : 'remark',
				newline : true,
				width : '443',
				validate : {
				    maxlength : 500
				},
				type : 'textarea'
		    } ]
		});
		$("#remark").attr("resize","none");
		if("${moduleId}"!=""){
			$("#form1 input[name=moduleId]").attr("readonly", "true").removeAttr("validate");
			$.ligerui.get("moduleId").updateStyle();
			BIONE.loadForm(mainform, {
			    url : "${ctx}/bione/mainpage/module/getModule?moduleId=${moduleId}",
			});
			/* $.ajax({
				cache : false,
				async : true,
				url : "${ctx}/bione/mainpage/module/getModule?moduleId=${moduleId}",
				dataType : 'json',
				type : "post",
				beforeSend : function() {
					BIONE.loading = true;
					BIONE.showLoading("正在加载数据中...");
				},
				complete : function() {
					BIONE.loading = false;
					BIONE.hideLoading();
				},
				success : function(result) {
					$.ligerui.get("picPath")._changeValue(result.picPath,result.picPath);
					$("#moduleId").val(result.moduleId);
					$("#moduleName").val(result.moduleName);
					$("#modulePath").val(result.modulePath);
					$("#moduleType").val(result.moduleType);
					$("#chartType").val(result.chartType);
					$("#moduleDefine").val(result.moduleDefine);
					$("#remark").val(result.remark);
				}
			}); */
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate($("#form1"));
	
		var buttons = [];
	
		buttons.push({
		    text : '取消',
		    onclick : function() {
			BIONE.closeDialog("moduleWin");
		    }
		});
		buttons.push({
		    text : '保存',
		    onclick : f_save
		});
		BIONE.addFormButtons(buttons);
		
		//鼠标移入模块定义弹出提示
	    /* $("#moduleDefine").hover(
        	function (){
        		$(this).ligerTip({content:"显示内容"});
        	},
            function (){
            	$(this).ligerHideTip();
            }
        ); */ //透过jquery的hover来赋值一个鼠标移入移出事件
        
    });
    
    function f_save() {
    	
    	
		BIONE.submitForm($("#form1"), function() {
		    BIONE.closeDialogAndReloadParent("moduleWin", "maingrid",
			    "保存成功");
		}, function() {
		    BIONE.closeDialog("moduleWin", "修改失败");
		});
    }
    
    function showIconDialog(options) {
		var options = {
			url : '${ctx}/bione/admin/func/selectIcon.html',
			dialogname : 'iconselector',
			title : '选择图标',
			comboxName : 'picPath'
		};
		BIONE.commonOpenIconDialog(options.title, options.dialogname,
				options.url, options.comboxName);
		return false;
	}
    
	//根据模块类型，是否显示报表类型，模块路径
    function setAssemblyShow(){
    	var val = $("#form1 input[name=moduleType]").val();
		if (val != "01" && val != "02") {
			$("#form1 input[name='chartType']").parent().parent().parent().hide();
		} 
		else {
			$("#form1 input[name='chartType']").parent().parent().parent().show();
		}
		if (val != "99"){
			$("#form1 input[name='modulePath']").parent().parent().parent().hide();
			$("#moduleDefine").parent().parent().show();
		}
		else{
			$("#form1 input[name='modulePath']").parent().parent().parent().show();
			$("#moduleDefine").parent().parent().hide();
		}
    }

</script>
</head>
<body>
	<div id="template.center">
		<form name="form1" method="post" id="form1"
			action="${ctx}/bione/mainpage/module"></form>
	</div>
</body>
</html>