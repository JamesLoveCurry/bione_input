<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var msgsetNo = "${msgsetNo}"
	//创建表单结构 
	var mainform;
	$(function(){
		initForm();
		initBtn();
		if(msgsetNo) {
			myLoadForm($("#mainform"), {url : "${ctx}/frs/pbmessage/getTaskInfo?&msgsetNo=${msgsetNo}"});
			$("#operType").val("modify");
		}else{
			$("#operType").val("add");
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform); 
	});
	function initBtn(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '保存',
			onclick : save
		});
		BIONE.addFormButtons(buttons);
	}
	function initForm(){
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			{
				display : "任务名称",
				name : "taskNm",
				newline : true,
				type : "text",
				group : "人行报文集信息",
				groupicon : groupicon,
				validate : {
					required : true,
					maxlength : 100
				}
			}, {
				display : "频度",
				name : "freqType",
				newline : false,
				type : "select",
				validate : {
					required : true
				},
				comboboxName : "freqType_sel",
				options : {
					initValue:'0',
					data : [{
						text : '年',
						id : "0"
					},{
						text : '季',
						id : "3"	
					},{
						text : '月',
						id : "4"
					},{
						text : '旬',
						id : "5"
					},{
						text : '周',
						id : "6"
					},{
						text : '日',
						id : "7"
					}]
				}
			},
			{
				display : "报表编码",
				name : "rptCode",
				newline : true,
				type : "select",
				comboboxName : "rptCode_sel",
				validate : {
					required : true
				},
				options : {
					onBeforeOpen : function(){
						var rptCodes = $("#rptCode").val();
						window.BIONE.commonOpenDialog("报表编码树", "rptCodeWin", "450", "400", '${ctx}/frs/pbmessage/rptCodeWin?&rptCodes=' + rptCodes, null);
						return false;
					}
				}
			},{
				display : "批次号",
				name : "batchId",
				newline : false,
				type : "text",
				validate : {
					required : true,
					maxlength : 10,
					digits : true
				}
			},{
				name : "sortNum",
				type : "hidden"
			},{
				name : "operType",
				type : "hidden"
			},{
				name : "msgsetNo",
				type : "hidden"
			}]
		});
	}
	
	function save() {
		BIONE.submitForm($("#mainform"), function() {
			BIONE.closeDialogAndReloadParent("addMsgset", "maingrid", "保存成功");
		}, function() {
			BIONE.closeDialog("addMsgset", "保存失败");
		});
	}
	function cancle() {
		BIONE.closeDialog("addMsgset");
	}
	// 填充表单数据
	function myLoadForm(mainform, options) {
		options = options || {};
		// 获取form数据之后的success函数
		function f_loadform(data) {
			// 根据返回的属性名，找到相应ID的表单元素，并赋值
			for ( var p in data) {
				var ele = $("[name=" + p + "]");
				// 针对复选框和单选框 处理
				if (ele.is(":checkbox,:radio")) {
					ele[0].checked = data[p] ? true : false;
				} else if (ele.is(":text") && ele.attr("ltype") == "date") {
					if (data[p]) {
						var date = null;
						if (data[p].time) {
							date = new Date(data[p].time);
						} else {
							// edit by caiqy
							var tdate;
							if (typeof data[p] == 'string' && data[p].indexOf('-') != -1 && data[p].length >= 10) {
								tdate = new Date(new Number(data[p].substr(0, 4)), new Number(data[p].substr(5, 2)) - 1, new Number(data[p].substr(8, 2)));
							} else {
								tdate = new Date(data[p]);
							}
							date = new Date(tdate);
						}
						var yy = date.getFullYear();
						var Mm = ((date.getMonth() + 1) < 10) ? ('0' + (date.getMonth() + 1)) : (date.getMonth() + 1);
						var dd = (date.getDate() < 10) ? ('0' + date.getDate())	: date.getDate();
						ele.val(yy + '-' + Mm + '-' + dd);
					}
				} else if (ele.attr("ltype") == "radiolist" || ele.attr("ltype") == "checkboxlist") {
					$.ligerui.get(ele.attr("data-ligerid")).setValue(data[p]);
				} else {
					ele.val(data[p]);
				}
			}
			// 下面是更新表单的样式
			var managers = $.ligerui.find($.ligerui.controls.Input);
			for ( var i = 0, l = managers.length; i < l; i++) {
				// 改变了表单的值，需要调用这个方法来更新ligerui样式
				var o = managers[i];
				o.updateStyle();
				o.inputText.blur();
				if (managers[i] instanceof $.ligerui.controls.TextBox)
					o.checkValue();
			}
			BIONE.hideLoading();
		}
		function f_fillComBoBox(data){
			f_loadform(data);
			var c = jQuery.ligerui.get("rptCode_sel");
 			c._changeValue($("#rptCode").val(), $("#rptCode").val());
		}
		BIONE.ajax(options, f_fillComBoBox);
	};
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform" action="${ctx}/frs/pbmessage/saveTaskEdit" method="post"></form>
	</div>
</body>
</html>