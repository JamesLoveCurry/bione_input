<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
	var type = "${type}";
	var taskInsId = "${taskInsId}";
	var rptId = "${rptId}";
	var orgNo = "${orgNo}";
	var dataDate = "${dataDate}";
	var cellNo = "${cellNo}";
	var operType = "${operType}";
	var remark = "";
	var mainform = null;
	var readOnly = "${readOnly}";
	$(function() {
		initForm();
		initData();
	});
	function initData() {
		$.ajax({
			cache : false,
			async : true,
			type : "post",
			url : "${ctx}/rpt/frs/rptfill/getIptRemark",
			dataType : "json",
			data : {
				taskInsId : "${taskInsId}",
				cellNo : "${cellNo}"
			},
			success : function(res) {
				if (res) {
					window.remark = res.operInfo;
					$("#remark").val(res.operInfo);
				}
			},
			error : function() {
			}
		});
	}
	function initForm() {
		$("#form").height($("#center").height() - 75);
		mainform = $("#basicform").ligerForm({
			fields : [{
					display : '单元格编号',
					name : "cellNo",
					newline : false,
					type : "text"
				}, {
					display : '批注',
					name : 'remark',
					type : 'textarea',
					newline : true,
					width : 320
			} ]
		});
		$("#remark").css("height", 145);
		$("#cellNo").val(cellNo);
		if(!readOnly||readOnly!="true")   //除了报表填报阶段显示按钮外，其它的均不显示按钮
			initBtn();
	}
	function initBtn() {
		var btns = [ {
			text : '还原',
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
			url : "${ctx}/rpt/frs/rptfill/saveIptRemark",
			dataType : "json",
			data : {
				taskInstanceId : "${taskInsId}",
				rptId : "${rptId}",
				orgNo : "${orgNo}",
				dataDate : "${dataDate}",
				cellNo : "${cellNo}",
				operInfo : $("#remark").val()
			},
			success : function() {
				BIONE.tip("填写完毕");
				var rowCol = window.parent.View.Utils.posiToRowCol("${cellNo}");
				if($("#remark").val()){
					window.parent.View.spread.getActiveSheet().comments.add(rowCol.row, rowCol.col, $("#remark").val());
				}else{
					window.parent.View.spread.getActiveSheet().comments.remove(rowCol.row, rowCol.col);
				}
				BIONE.closeDialog("remarkWin");
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