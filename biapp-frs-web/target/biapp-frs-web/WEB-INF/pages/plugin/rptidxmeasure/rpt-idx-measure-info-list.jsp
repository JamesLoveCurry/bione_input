<%--
  Created by .
  Date: 2021/09/29
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8"%>
<%@include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
    <!-- 基础的JS和CSS文件-->
    <script type="text/javascript">
        var grid,nos;
        $(function (){
            initSearchForm();
            initGrid();
            initToolBar();
            BIONE.addSearchButtons("#search", grid, "#searchbtn");
        });

        function initSearchForm(){
            $("#search").ligerForm({
                fields:[{
                    display: '度量标识',
                    name: "measureNo",
                    type: "text",
                    cssClass: "field",
                    attr:{
                        field:'measureNo',
                        op:"like"
                    }
                },{
                    display: '度量名称',
                    name: "measureNm",
                    newline:false,//默认是true，重启一列
                    type:"text",
                    cssClass: "field",
                    attr: {
                        field: 'measureNm',
                        op:"like"
                    }
                }
                ]
            });
        };
        function initGrid(){
            grid=$("#maingrid").ligerGrid({
                toolbar:{},
                checkbox:true,
                columns:[{
                    display: '度量标识',
                    name: "measureNo",
                    width:"20%",
                    align:'left'
                },{
                    display: '度量名称',
                    name: "measureNm",
                    width:"20%",
                    align:'left'
                },{
                    display: '备注',
                    name: "remark",
                    width:"60%",
                    align:'left'
                }],
                dataAction : 'server', //从后台获取数据
                usePager : true, //服务器分页
                alternatingRow : true, //附加奇偶行效果行
                colDraggable : true,
                url: "${ctx}/rpt/frame/idxmeasure/list.json",
                sorName:'measureNo',//第一次默认排序字段
                sortOrder:'desc',//默认排序方式
                rownumbers:true,
                width:'100%',
                height:'99%',
                isScroll:true
            });
            grid.setHeight($("#center").height() - 115);
        };

        function initToolBar(){
            var btns=[];
            btns=[{
                text:'增加',
                click:f_open_add,
                icon:'fa-plus'
            },{
                text:'修改',
                click: f_open_update,
                icon:'fa-pencil-square-o'
            },{
                text:'删除',
                click: f_deletem,
                icon:'fa-trash-o'
            }];
            BIONE.loadToolbar(grid,btns,function (){ });
        }

        //添加函数
       function f_open_add(){
            BIONE.commonOpenLargeDialog("度量添加","measureAddWin","${ctx}/rpt/frame/idxmeasure/new");
        }

        //修改函数
        function f_open_update(){
            achieveIds();
            if(ids.length==1){
                BIONE.commonOpenLargeDialog("度量修改","measureModWin","${ctx}/rpt/frame/idxmeasure/"+ids[0].measureNo+"/edit");
            }else if(ids.length>1){
                BIONE.tip("请选择一行进行修改");
                return;
            }
            var row=grid.getSelectedRow();
            if(!row){
                BIONE.tip("请选择行");
                return;
            }
        }

        //删除函数
        function f_deletem(){
            var selectedRow=grid.getSelecteds();
            var selectedLen=selectedRow.length;
            if(selectedLen==0){
                BIONE.tip('请选择要删除的行');
                return;
            }
            $.ligerDialog.confirm('确定要删除这'+selectedLen+'条记录吗？',
            function (yes){
                if(yes) {
                    deleteFun(selectedLen, selectedRow);
                }
            });
        }

        function deleteFun(length,selectedRow){
            var ids="";
            for (var i = 0; i < length; i++) {
                if(ids!=""){
                    ids+=",";
                }
                ids+=selectedRow[i].measureNo;
            }
            $.ajax({
                type:"POST",
                url:'${ctx}/rpt/frame/idxmeasure/destroyOwn.json',
                dataType:"json",
                data:{
                    "ids":ids
                },
                success:function (result){
                    if(result.status=="success"){
                        BIONE.tip("删除成功");
                        grid.loadData();
                    }else{
                        BIONE.tip("删除失败，原因是："+result.msg);
                    }
                }
            });
        }

        //获取当前已勾选的用户记录
        function achieveIds(){
            ids=[];
            var rows=grid.getSelectedRows();
            for(var i in rows){
                ids.push(rows[i]);
            }
        }


    </script>
</head>
<body>
</body>
</html>
