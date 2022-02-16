<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var show="${show}";
	var mainform=null;
	var taskInsId = "${taskInsId}";
	
	function initForm() {
		mainform = $("#basicform").ligerForm({
			fields : [{
				display : "驳回理由",
				name : "rejectReason",
				newline : true,
				type : "textarea",
				width : 400,
				height : 300,
				attr : {
					style : "resize: none;"
				}
			}]
		});

		//$("#basicform textarea[name=rejectReason]").val("");
		$("#basicform textarea[name=rejectReason]").attr("readOnly", "true");
		
		if("${show}"!=""){
			jQuery.metadata.setType("attr", "validate");
			BIONE.validate($("#basicform"));
		}
		
		if (show != "") {
			$("input").css("color", "#333").attr("readOnly", "true")
			.removeAttr("validate");
		}
	};
	
	function initButton(){
		var btns = [ {
			text : "返回", onclick : function() {
				BIONE.closeDialog("showRejectReason");
			}
		}];
		BIONE.addFormButtons(btns);
	};
	
	function initData(){
		if (taskInsId!=""&&taskInsId!=null&&taskInsId!="undefined") {
			$.ajax({
				async : true,
				cache : false,
				type : "POST",
				dataType : "JSON",
				contentType : "application/json",
				url : '${ctx}/rpt/frs/rptfill/getRejectReason?taskInsId=${taskInsId}',
				success : function(data) {
					$("#basicform textarea[name=rejectReason]").val(data.msg);
				},
				error : function(){
					BIONE.hideLoading();
				}
			});
		} 

	};
	
	$(function(){
		initButton();
		initForm();
		initData();
	});
</script>
</head>
<body>
	<div id='template.center'>
		<div id="tab" style="width: 100%; overflow: hidden;">
			<div tabid="basic" title="基本信息" lselected="true">
				<form id="basicform" action="${ctx}/report/mgr/reportInfo/saveReportInfo" method="post"></form>
			</div>
		</div>
	</div>
</body>
</html>