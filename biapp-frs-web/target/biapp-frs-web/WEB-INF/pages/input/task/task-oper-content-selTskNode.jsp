<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style>
</style>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
var taskInstanceId= '${taskInstanceId}';
var flag = '${flag}';
$(function(){
	$("#sel").ligerForm({
		//控件宽度
        inputWidth: 180,
        //标签宽度
        labelWidth: 90,
        //间隔宽度
        space: 40,
        rightToken: '：',
        //标签对齐方式
        labelAlign: 'left',
        //控件对齐方式
        align: 'left',
        //字段
        fields: [{
			display : "驳回节点",
			name : "tskNodeIns",
			comboboxName : "tskNodeInsId",
			newline : false,
			type : 'select',
			options : {
				url: '${ctx}/rpt/input/taskoper/taskNodes/selectjson.json?tasInsId=' + taskInstanceId,
				valueField: 'taskNodeInstanceId',
				textField: 'taskNodeNm'
	    	},
			validate : {
				required : true
			}
		}]
	});
	BIONE.addFormButtons({
		text : '确定',
		width : '80px',
		icon : 'refresh',
		onclick : function (){
			if(!$.ligerui.get('tskNodeInsId').getValue()){
				BIONE.tip("请选择驳回节点。");
				return;
			}
			if(flag && flag == "rebut"){
				parent.rebutIt($.ligerui.get('tskNodeInsId').getValue());
				
			}else{
				parent.reButNode = $.ligerui.get('tskNodeInsId').getValue();
				parent.form_return.callback = function() {
					parent.saveErrorMark();
				};
				parent.form_return();
			}
			BIONE.closeDialog("tskNodesSel");
		}
	},true);


	
});
	

</script>
</head>
<body>
<div id="template.center">
	<div id='sel'></div>
</div>

</body>
</html>