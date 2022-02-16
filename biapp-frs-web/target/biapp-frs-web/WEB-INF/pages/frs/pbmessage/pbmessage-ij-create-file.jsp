<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style>
.singlebox,#allSelect{
  margin-top:5px;
}
.tipptitle{
  margin-left:24px;
  text-align:left
}

.tipp{
  margin-left:40px;
  text-align:left
}

#fdiv{
 margin-top:5% 
}
</style>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var msgsetNo = '${msgsetNo}';
	//创建表单结构 
	var mainform;
	$(function() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
			{
				display : "数据日期",
				name : "dataDate",
				newline : true,
				group : "报文生成信息",
				groupicon : groupicon,
				type : "date",
				cssClass : "field",
				options : {
					format : 'yyyyMMdd',
					onChangeDate: function(value){
						$.ajax({
							cache : false,
							async : false,
							url : "${ctx}/frs/pbmessage/getSortNum",
							dataType : 'json',
							type : "post",
							beforeSend: function(){
								BIONE.showLoading('加载数据中...');
							},
							data : {
								dataDate: value,
								msgsetNo: msgsetNo
							},
							complete: function(){
								BIONE.hideLoading();
							},
							success : function(result) {
								$("#sortNum").val(result);
							},
							error : function(result, b) {
								//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
							}
						});
						
					}
				},
				validate : {
					required : true
				}
			},{
				display : "报送顺序号",
				name : "sortNum",
				newline : true,
				type : "text",
				validate : {
					required : true,
					maxlength : 10,
					digits : true
				}
			} ]
		});
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : cancle
		});
		buttons.push({
			text : '生成',
			onclick : save
		});
		BIONE.addFormButtons(buttons);
	});
	function save() {
		if ($('#mainform').valid()) {
			var dataDate = $("#dataDate").val();
			$.ajax({
				cache : false,
				async : true,
				url : "${ctx}/frs/pbmessage/validateReportTsk",
				dataType : 'json',
				type : "post",
				beforeSend: function(){
					BIONE.showLoading('正在校验报送数据...');
				},
				data : {
					dataDate: dataDate,
					msgsetNo: '${msgsetNo}',
					type: '${type}'
				},
				complete: function(){
					BIONE.hideLoading();
				},
				success : function(result) {
					var isPass=result.isPass;
					if (isPass == true) {
						createIJFile();
					} else {
						$.ligerDialog.confirm('生成数据中存在' + result.notPassSize + "条未归档记录，是否继续生成报文？", function(yes) {
							if (yes) {
								createIJFile();
							}
						});	
					}
				},
				error : function(result, b) {
					//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	} 
	function cancle(){                     
		BIONE.closeDialog("dataDate");
	}
	
	function createIJFile(){
		var dataDate = $("#dataDate").val();
		var sortNum = $("#sortNum").val();
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/frs/pbmessage/createMsg",
			dataType : 'json',
			type : "post",
			beforeSend: function(){
				BIONE.showLoading('正在生成报文数据中...');
			},
			data : {
				dataDate: dataDate,
				sortNum: sortNum,
				msgsetNo: '${msgsetNo}',
				type: '${type}'
			},
			complete: function(){
				BIONE.hideLoading();
			},
			success : function(result) {
				var path=result.path;
				if (parent.$("msg_down").length < 1) {
					parent.$('body').append($('<iframe id="msg_down" />'));
				}
				var downLoadUrl = '${ctx}/frs/pbmessage/downLoad?_type=data_event&_field=downLoad&_event=POST&_comp=main&Request-from=dhtmlx&path=' + encodeURI(path) + "&taskNm=${taskNm}&dataDate="+dataDate;
				parent.$("#msg_down").attr('src',downLoadUrl);
				BIONE.closeDialog("dataDate");
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="center" style="width: 100%;">
			<div id="cdiv" style="width: 99.8%;float: left;padding-top: 5px;">
				<form id="mainform" action="${ctx}/report/frs/pbmessage/pbcMsgsetController.mo?_type=data_event&_field=createCode&_event=POST&_comp=main&Request-from=dhtmlx" method="post"></form>
			</div>
			<div id="tipAreaDiv" style="width: 99.5%;float: left;padding-top: 10px;padding-bottom: 20px;background-color: #FFFEE6;border: solid 1px #D0D0D0;">
				<div style="padding-left: 2px;">
					<div style="width: 24px; height: 16px; float: left; background-image: url('${ctx}/images/classics/icons/lightbulb.png'); background-attachment: scroll; background-repeat: no-repeat; background-position-x: 0%; background-position-y: 0%; background-size: auto; background-origin: padding-box; background-clip: border-box; background-color: transparent;"></div>
					<p>注意事项</p>
					<p class="tipptitle">报送顺序是任务在相同数据日期向人行报送的次数顺序。</p>
					<p class="tipptitle">系统默认生成报送文件后，即完成向人行报送操作。</p>
					<p class="tipptitle">再次生成该任务下数据日期的报文时，顺序号会自动递增！</p>
					<p class="tipp"></p>
				</div>
			</div>
		</div>
	</div>
</body>
</html>