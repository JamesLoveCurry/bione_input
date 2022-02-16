<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css"
	href="../../../../css/classics/ligerui-custom.css" />
<link rel="stylesheet" type="text/css"
	href="../../../../css/classics/temp/template.css" />
<script data-main="../../../../plugin/js/template/validatorMain" src="../../../../js/require/require.js"></script>
<style type="text/css">
	.l-table-edit-td{ padding:5px 10px;}
/* .l-table-edit {
	table-layout: fixed;
	overflow:hidden;
}

.l-table-edit-td {
	padding: 5px 10px;
}

.l-button-submit,.l-button-cancel {
	background: url("../../../../images/classics/ligerui/btn.png") repeat-x scroll 0 #D9D9D9;
	width: 80px;
	margin-left: 10px;
	padding-bottom: 2px;
	float: none;
}
.l-form .l-fieldcontainer {
	    	margin-left: 0px;
	    	padding: 0;
	    	padding-left : 0;
				} */
</style>
<title>验证配置</title>
</head>
<body>

	<div id="validatorDesigner"></div>

	<!-- Templates -->
	<script type="text/template" id="validatorForm-template">
        <table cellpadding="0" cellspacing="0" class="l-table-edit" width="100%">
			<tr>
				<td align="left" class="l-table-edit-td" colspan="2" style="width:100%">
					<form id="validatorForm"></form>
				</td>
			</tr>
			<tr>
				<td align="right" class="l-table-edit-td" style="width:50%">
					<input type="submit" value="保存" id="saveBtn" class="l-button l-button-submit" />
				</td>
				<td align="left" class="l-table-edit-td" style="width:50%">
					<input type="button" value="取消" id="cancelBtn" class="l-button l-button-cancel" />
				</td>
			</tr>
		</table>
    </script>
</body>
</html>