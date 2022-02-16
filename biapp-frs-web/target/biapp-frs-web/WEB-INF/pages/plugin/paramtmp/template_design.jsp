<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html dir="ltr">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" type="text/css" href="${ctx}/css/classics/ligerui-custom.css" />
    <link rel="stylesheet" type="text/css" href="${ctx}/css/classics/jqueryui/smoothness/jquery-ui.min.css" />
    <link rel="stylesheet" type="text/css" href="${ctx}/css/classics/temp/template.css" />
    <link rel="stylesheet" type="text/css" href="${ctx}/images/classics/icons/icon/style.css" />
    <script data-main="${ctx}/plugin/js/template/main" src="${ctx}/js/require/require.js"></script>
    <style>
    	/* .l-form { margin:0px;overflow:hidden;}/*edit by fangjuan margin 由7px改为了0px*/
    	.l-toolbar
			{    
    			background:#CEDFEF  url('../../../../css/classics/ligerUI/Aqua/images/panel/panel-toolbar.gif') repeat-x; height:28px;/*edit by fangjuan height有23px改为28px*/ 
    			border:1px solid #9CBAE7;  border-top:1px solid #EFF7F7;
			}
		.l-form .l-fieldcontainer {
	    	margin-left: 10px;
	    	padding: 0;
				} */
    </style>
    <title>参数模板</title>
</head>
<body>
    <!-- Templates -->
    <script type="text/template" id="designer-template">
        <div id="designer_layout" style="width:99%">
			<div position="top"><div id="topToolbar"></div></div>
            <div position="left" id="left">
                <div id="comp" title="添加组件">
                    <ul id="componentes">
                        <li class="l-toolbar" type="text" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/textfield.png' style="vertical-align:middle;"/><label>文本框</label></li>
                        <li class="l-toolbar" type="hidden" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/box.gif' style="vertical-align:middle;"/><label>隐藏域</label></li>
                        <li class="l-toolbar" type="date" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/calendar.png' style="vertical-align:middle;"/><label>日期框</label></li>
                        <li class="l-toolbar" type="select" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/mnuMenuButton.gif' style="vertical-align:middle;"/><label>下拉框</label></li>
                        <li class="l-toolbar" type="combobox" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/t_parent.gif' style="vertical-align:middle;"/><label>数据树</label></li>
						<li class="l-toolbar" type="popup" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/application_double.png' style="vertical-align:middle;"/><label>弹出树</label></li>
						<li class="l-toolbar" type="daterange" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/application_double.png' style="vertical-align:middle;"/><label>日期区间</label></li>
						<li class="l-toolbar" type="dblin" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/application_double.png' style="vertical-align:middle;"/><label>组合控件</label></li>
						<li class="l-toolbar" type="textrange" style="height:24px;padding-top:5px;"><img src='../../../../images/classics/template/application_double.png' style="vertical-align:middle;"/><label>文本区间</label></li>
                    </ul>
                </div>
                <div id="prop" title="编辑组件">
                </div>
				<div id="temp" title="载入模板">
					<div id="tree">
                	</div>
                </div>
            </div>
            <div position="center" id="center">
                <div id="designerCanvas" style='position: relative;overflow: auto;'> 
                </div>
            </div>
        </div>
    </script>
    <script type="text/template" id="designer-template-empty">
        <ul class="ul-empty"></ul>
    </script>
    <script type="text/template" id="designer-property-table">
        <form id="propForm">
            <table cellpadding="0" cellspacing="0" class="l-table-edit propTable">
                <tr class="l-toolbar">
                    <th class="l-table-edit-td " style="width:38%;"><B>属性名称</B></th>
                    <th class="l-table-edit-td" style="width:61%;"><B>属性值</B></th>
                </tr>
            </table>
        </form>
    </script>
    <script type="text/template" id="designer-property-tr">
        <tr>
            <td align="center" class="l-table-edit-td">${'${'}display}</td>
            <td align="center" class="l-table-edit-td">
            {{if editor == 'text' || editor == 'digits' || editor == 'number'}}
                <input name="${'${'}name}" type="text" id="${'${'}name}" ltype="${'${'}editor}" value="${'${'}value}" nullText="${'${'}nullText}" validate="${'${'}validate}"/> 
            {{else editor == 'select'}}
                <select name="${'${'}name}" id="${'${'}name}" ltype="select"> 
                {{each options}}
                    <option value="${'${'}id}" ${'${'}selected}>${'${'}text}</option>
                {{/each}}
                </select>
            {{else editor == 'popup'}}
                <input id="${'${'}name}" name="${'${'}name}" type="text" ltype="popup" value="${'${'}value}" />
            {{else}}
                undefined
            {{/if}}
            </td>
        </tr>
    </script>
    <script type="text/template" id="designer-property-input">
        <input name="${'${'}name}" type="text" id="${'${'}name}" ltype="${'${'}type}" />        
    </script>
</body>
</html>