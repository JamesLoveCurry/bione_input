<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="../../../../css/classics/ligerui-custom.css" />
    <link rel="stylesheet" type="text/css" href="../../../../js/codemirror/codemirror.css" />
    <link rel="stylesheet" type="text/css" href="../../../../js/codemirror/eclipse.css" />
    <link rel="stylesheet" type="text/css" href="../../../../css/classics/temp/template.css" />
    <script data-main="../../../../plugin/js/template/treeOptionsMain" src="../../../../js/require/require.js"></script>
    <style type="text/css">
    .l-table-edit-td{ padding:5px 10px;}
        /* .l-table-edit {table-layout:fixed;overflow:hidden;}
        .l-table-edit-td{ padding:5px 10px;}
        .l-button-submit,.l-button-reset{width:80px; margin-left:10px; padding-bottom:2px;}
        .l-toolbar
			{    
    			background:#CEDFEF  url('../../../../css/classics/ligerUI/Aqua/images/panel/panel-toolbar.gif') repeat-x; height:28px;/*edit by fangjuan height有23px改为28px*/ 
    			border:1px solid #9CBAE7;  border-top:1px solid #EFF7F7;
			}
		.l-form {
		    margin: 0;
    		overflow: hidden;
			} */
    </style>
    <title>数据源配置</title>
</head>
<body>

    <div id="treeOptionsDesigner"></div>

    <!-- Templates -->
    <script type="text/template" id="treeOptionsForm-template">
        <form id="treeOptionsForm">
            <table cellpadding="0" cellspacing="0" class="l-table-edit" width="100%">
                <thead>
                    <tr>
                        <td align="left" class="l-table-edit-td" style="width:100px" >数据源类型:</td>
                        <td align="left" class="l-table-edit-td" style="width:190px">
                            <input id="options_type" name="options_type" ltype="select">
                            </input>
                        </td>
						<td align="left" class="l-table-edit-td" style="width:110px" >上级控件:</td>
                        <td align="left" class="l-table-edit-td" style="width:190px">
                            <input id="correlated" name="correlated" ltype="select"> 
                            </input>
                        </td>
                    </tr>
                </thead>
                <tbody>
                </tbody>
            </table>
        </form>
    </script>
    <script type="text/template" id="remoteOptions-template">
        <tr>
            <td colspan="4">
                <div id="gridToolbar"></div>
            </td>
        </tr>
        <tr>
            <td align="left" class="l-table-edit-td">请求类型:</td>
            <td align="left" class="l-table-edit-td" colspan="3">
                <input id="ajaxType" name="ajaxType" ltype="select">
                </input>
            </td>
        </tr>
        <tr>
            <td align="left" class="l-table-edit-td">
				<label>请求地址：</label>
			</td>
            <td align="left" class="l-table-edit-td" colspan="3">
                <textarea id="url" name="url" ltype="textarea" style="width:542px;height:222px;"></textarea>
            </td>
        </tr>
    </script>
    <script type="text/template" id="databaseOptions-template">
        <tr>
            <td colspan="4">
                <div id="gridToolbar"></div>
            </td>
        </tr>
        <tr>
            <td align="left" class="l-table-edit-td">数据库名称:</td>
            <td align="left" class="l-table-edit-td" colspan="2">
                <input id="db" name="db" ltype="select">
                </input>
            </td>
			<td align="left" class="l-table-edit-td" rowspan="2">
				<div id="systemVariables"></div>
			</td>
        </tr>
        <tr>
            <td align="left" class="l-table-edit-td">
				<label>SQL语句:<label>
				<img class="template-help" src="../../../../images/classics/template/help.png" title="SQL语句查询结果列最少需要两列，请通过AS语法另起别名，别名为id的为树节点的实际值字段，别名为text的为树节点的显示值字段。双击右侧列表可以向SQL中添加相应系统参数宏。"/>
			</td>
            <td align="left" class="l-table-edit-td" colspan="2">
                <textarea id="sql" name="sql" ltype="textarea"></textarea>
            </td>
        </tr>
    </script>
</body>
</html>