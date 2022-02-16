<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var tmpId = "${tmpId}";
	var dataDate = "${dataDate}";
	var cellNo = "${cellNo}";
	var remark = window.parent.base_Remark;
	var mainform = null;
	$(function() {
		initForm();
		initData();
	});
	
	function initForm() {
		$("#form").height($("#center").height() - 75);
		mainform = $("#basicform").ligerForm({
			fields : [{
					display : '单元格编号',
					name : "cellNo",
					newline : false,
					type : "text"
				}, {
					display : '备注',
					name : 'remark',
					type : 'textarea',
					newline : true,
					width : 320
			} ]
		});
		$("#remark").css("height", 145);
		$("#cellNo").val(cellNo);
		$("#remark").val(remark);
		initBtn();
	}
	
	function initBtn() {
		var btns = [ {
			text : '重置',
			onclick : function() {
				$("#remark").val(window.remark);
			}
		}, {
			text : '保存',
			onclick : f_save
		} ];
		BIONE.addFormButtons(btns);
	}
	
	function f_save() {
		$.ajax({
			cache : false,
			async : true,
			type : "post",
			url : "${ctx}/rpt/frs/rptfill/saveCellRemark",
			dataType : "json",
			data : {
				tmpId : tmpId,
				dataDate : dataDate,
				cellNo : cellNo,
				remark : $("#remark").val()
			},
			success : function(result) {
				if(result && result.isSuccess){
					parent.BIONE.tip("填写完毕");
					window.parent.View.ajaxJson();
					BIONE.closeDialog("explain");
				}else{
					BIONE.tip("填写失败");
				}
			},
			error : function() {
				BIONE.tip("填写失败");
			}
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="form">
			<form id="basicform" action="" method="post"></form>
		</div>
		<div id="bottom">
			<div class="form-bar">
				<div class="form-bar-inner" style="padding-top: 10px; padding-right: 20px"></div>
			</div>
		</div>
	</div>
</body>
</html>