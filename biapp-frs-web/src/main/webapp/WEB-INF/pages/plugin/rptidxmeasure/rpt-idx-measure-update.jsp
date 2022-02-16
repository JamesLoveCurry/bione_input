<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
var groupicon = "${ctx}/images/classics/icons/communication.gif";
$(function (){
    var mainform=$("#form1").ligerForm({
        fields:[{
            display: '度量标识',
            name: "measureNo",
            type:'text',
            group:"度量信息",
            validate:{
                required:true
            },
            groupicon:groupicon
        },{
            display: '度量类型',
            name:"measureType",
            type:'hidden'
        },{
            display: '度量名称',
            name: "measureNm",
            newline:true,
            type:'text',
            validate: {
                required: true
            }
        },{
            display: '备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注',
            name: "remark",
            newline : true,
            width : '493',
            validate : {
                maxlength : 500
            },
            type : 'textarea'
        }]
    });
    BIONE.loadForm(mainform,{
        url:"${ctx}/rpt/frame/idxmeasure/${id}"
    });
    $("#form1 input[name=measureNo]").attr("readOnly","true").removeAttr("validate");
    jQuery.metadata.setType("attr", "validate");
    BIONE.validate($("#form1"));

    var managers=$.ligerui.find($.ligerui.controls.Input);
    for (var i = 0, a=managers.length; i<a ; i++) {
    //改变了表单的值，需要调用这个方法来更新ligerui样式
        managers[i].updateStyle();
    }

    var buttons=[];
    buttons.push({
        text:'取消',
        onclick:function (){
            BIONE.closeDialog("measureModWin");
        }
    });
    buttons.push({
        text: '修改',
        onclick:f_save
    });
    BIONE.addFormButtons(buttons);
});

function f_save(){
    BIONE.submitForm($("#form1"),function (){
        BIONE.closeDialogAndReloadParent("measureModWin","maingrid",
        "修改成功");
    },function (){
        BIONE.closeDialog("measureModWin","修改失败");
    });
}
</script>
</head>
<body>
    <div id="template.center">
        <form name="form1" method="post" id="form1"
              action="${ctx}/rpt/frame/idxmeasure"></form>
    </div>
</body>
</html>
