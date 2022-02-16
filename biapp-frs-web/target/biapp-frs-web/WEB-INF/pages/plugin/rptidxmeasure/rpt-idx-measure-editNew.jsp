<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
    var groupicon = "${ctx}/images/classics/icons/communication.gif";
    $(function (){
        $("#mainform").ligerForm({
            fields:[{
                display: '度量标识',
                name: "measureNo",
                type:'text',
                group:"度量信息",
                validate:{
                    required:true,
                    remote:"${ctx}/rpt/frame/idxmeasure/testMeasureNo",
                    messages:{
                        remote: "度量标识已存在"
                    }
                },
                groupicon:groupicon
            },{
                name:"measureType",//度量类型，默认添加为03
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
            } ]
        });
        $("#mainform input[name=measureType]").val('03');//添加时让度量类型默认为03

        jQuery.metadata.setType("attr","validate");
        BIONE.validate($("#mainform"));
        var buttons=[];
        buttons.push({
            text:'取消',
            onclick:function (){
                BIONE.closeDialog("measureAddWin");
            }
        });
        buttons.push({
            text:'保存',
            onclick:f_save
        });
        BIONE.addFormButtons(buttons);
    });

    function f_save(){
        BIONE.submitForm($("#mainform"),function (){
            BIONE.closeDialogAndReloadParent("measureAddWin","maingrid","添加成功");
        },function (){
            BIONE.closeDialog("measureAddWin","添加失败");
        });
    };

</script>
</head>
<body>
<div id="template.center">
    <form name="mainform" method="post" id="mainform"
          action="${ctx}/rpt/frame/idxmeasure"></form>
</div>
</body>
</html>
