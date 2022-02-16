/**
 *
 * <pre>
 * Title:【配置管理】【监管码值】
 * Description:监管码值主页面
 * </pre>
 * @author miaokx
 * @version 1.00.00
 * @date 2021/4/16 14:49
 */
function AfterInit() {
    document.write('<script src="../../js/bfd/datagrid_detailview.js"><\/script>');
    document.write("<link rel=\"stylesheet\" type=\"text/css\" href=\"../../css/bfd/datagrid.css\"/>");
    document.getElementById("d1").style.marginLeft = '-15px';
    document.getElementById("d1").style.marginTop = '-15px';
    document.getElementById("d1").innerHTML='<label class="textbox-label textbox-label-before" for="_easyui_textbox_input1" style="text-align: right; width: 85px; height: 30px; line-height: 30px;">代码类型名称</label>' +
        '<span class="textbox" style="width: 95px;"><input id="codeTypeName" type="text" name="codeTypeName" class="textbox-text validatebox-text textbox-prompt" autocomplete="off" tabindex="" placeholder="" style="margin: 0px; padding-top: 0px; padding-bottom: 0px; height: 28px; line-height: 28px; width: 93px; color: black;"></span>'+
        "<a id=\"standardQueryBtn\" href=\"javascript:void(0)\" class=\"easyui-linkbutton l-btn l-btn-small\" style=\"margin-left:10px\">" +
        "<span class=\"l-btn-left l-btn-icon-left\" style=\"margin-top: 0px;\"><span class=\"l-btn-text\" style=\"margin-left :3px\">查询</span><span class=\"l-btn-icon icon-search1\">&nbsp;</span></span></a>" +
        "</div>" +
        "<div id=\"dg\" style=\"width:100%;height:96%;margin-left: -15px; margin-top: -15px\"></div>";
}

$(function(){
    $('#dg').datagrid({
        view: detailview,//detailview必须写
        url:v_context + "/imas/code/getAllPraCode",//向后台请求数据
        //data: datagird_data,
        singleSelect:"true" ,
        fitColumns:"true",
        striped:true,
        checkOnSelect:'true',
        columns:[[
            {field:'codeType',title:'代码类型',width:100},
            {field:'codeTypeName',title:'代码类型名称',width:100,align:'left'}
        ]],
        toolbar: [{
                text: '导入',
                iconCls: 'icon-p68',
                handler: function () {
                    importRule();
                }
            },
            {
                text: '导出',
                iconCls: 'icon-p69',
                handler: function () {
                    exportRule();
                }
            }

        ],
        detailFormatter:function(index,row){
            return '<div style="padding:2px"><table id="ddv-' + index + '"></table></div>';
        },
        pagination: true,
        pageSize: 20,
        pageList: [10, 20, 50, 100],
        onExpandRow: function(index,row){
            $('#ddv-'+index).datagrid({
                //view: detailview,//detailview必须写
                url:v_context +'/imas/code/getByCodeType?codeType='+row.codeType,//点击后再去后台请求数据
                //data:datagrid_detaildata,
                fitColumns:true,
                singleSelect:true,
                rownumbers:true,
                checkOnSelect:'true',
                loadMsg:'',
                height:'auto',
                columns:[[
                    {field:'item',title:'代码项',width:100,align:'left'},
                    {field:'itemName',title:'代码名称',width:100,align:'left'},
                    {field:'itemLvl',title:'代码级别',width:100,align:'left'},
                    {field:'modifier',title:'创建人',width:100,align:'left'},
                    {field:'modifyTm',title:'创建时间',width:100,align:'left'}
                ]],
                onResize:function(){
                    $('#dg').datagrid('fixDetailRowHeight',index);
                },
                onLoadSuccess:function(){
                    $('#dg').datagrid("selectRow", index)
                    setTimeout(function(){
                        $('#dg').datagrid('fixDetailRowHeight',index);
                    },0);
                },
            });
            $('#dg').datagrid('fixDetailRowHeight',index);
        }
    });
    $("#standardQueryBtn").click(function(){
        $("#dg").datagrid("load",{
            "codeTypeName": $("#codeTypeName").val()
        });

    });
});
/**
 * 【导出】
 * 点击按钮，系统下载excel文件，对校验规则进行导出
 *
 * @returns
 */
function exportRule() {
    var download = $('<iframe id="download" style="display: none;"/>');
    $('body').append(download);
    var codeTypeName = $("#codeTypeName").val();
    var src = v_context + "/imas/code/export?codeTypeName=" + codeTypeName;
    download.attr('src', src);
}

/**
 * 【导入】
 * 点击按钮，上传excel模板文件，对数据字典进行导入
 *
 * @returns
 */
function importRule() {
    JSPFree.confirm("提示","码值导入会全量更新系统中的码值字典，请谨慎操作，请确认是否执行导入操作！",function(_isOK){
        if(_isOK){JSPFree.openDialog("文件上传", "/yujs/imas/codeConfig/imas_input_code.js", 500, 240, null,function(_rtdata) {
            if (_rtdata != null && _rtdata != undefined && _rtdata.type != "dirclose") { //不是直接关闭窗口
                JSPFree.alert(_rtdata.msg);
                // 导入之后立即查询导入之前业务类型的数据
                window.location.reload(); // 立即查询刷新数据
            }
        });
        }
    });
}