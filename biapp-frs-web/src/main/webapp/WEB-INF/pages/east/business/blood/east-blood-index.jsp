<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var dialog;
	var grid;

    $(function() {
    	initForm();
        initGrid();
        initButtons();
    });
    
  	//初始化form
    function initForm() {
		//搜索框初始化
		$("#search").ligerForm({
			fields : [ {
				display : "业务表",
				name : "tabName",
				newline : false,
				type : "text",
				attr : {
					field : "tabName",
					op : "like"
				}
			}, {
				display : "来源生产系统",
				name : "fromSys",
				newline : false,
				labelWidth : 120,
				type : "select",
				attr : {
					field : "fromSys",
					op : "="
				},
				options:{
					data:[{
						text:"CRM系统",
						id : "CRM系统"
					},{
						text:"HR系统",
						id : "HR系统"
					},{
						text:"信贷系统",
						id : "信贷系统"
					},{
						text:"风险管理系统",
						id : "风险管理系统"
					},{
						text:"合规系统",
						id : "合规系统"
					},{
						text:"审计系统",
						id : "审计系统"
					}]
				}
			}]
		})
	};

	//初始化Grid
	function initGrid() {
		var tabName = "${tabName}";
		grid = $("#maingrid").ligerGrid({
		columns : [ {
			display : '主键',
			name : 'bloodId',
			type : 'hidden',
			frozen: true
		},{
			display : '业务表',
			name : 'tabName',
			width : '14%',
			align : 'center'
		},{
			display : '来源生产系统',
			name : 'fromSys',
			width : '10%',
			align : 'center'
		},{
			display : '生产系统表名',
			name : 'fromTable',
			width : '10%',
			align : 'center'
		},{
			display:'生产系统相关表',
			name:'fromTable2',
			width : '20%',
			align : 'center'
		},{
			display:'ETL名称',
			name:'etlName',
			width : '14%',
			align : 'center'
		},{
			display:'ETL逻辑说明',
			name:'etlDescr',
			width : '20%',
			align : 'center'
		},{
			display:'备注',
			name:'remark',
			width : '9%',
			align : 'center'
		}],
		checkbox : true,
		rownumbers : true,
		isScroll : false,
		alternatingRow : true, // 附加奇偶行效果行
		colDraggable : false,
		dataAction : 'server', // 从后台获取数据
		method : 'post',
		url : "${ctx}/east/business/blood/queryList",
		parms :{
			tabName : tabName
		},
		sortName : 'bloodId', // 第一次默认排序的字段
		sortOrder : 'asc', // 排序的方式
		toolbar : {}
	})
	};

	//初始化按钮
    function initButtons() {
		var btns = [{
           	text : '新增',
            click : create,
            icon : 'fa-plus'
        }, {
            text : '修改',
            click : edit,
            icon : 'fa-pencil-square-o'
        }, {
            text : '删除',
            click : deleteBatch,
            icon : 'fa-trash-o'
        }];

		BIONE.loadToolbar(grid, btns, function() {});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
	};
	
	//新增任务
    function create() {
		BIONE.commonOpenDialog("新增", "editTab", 960, $("#center").height()-30, 
				"${ctx}/east/business/blood/add");
    }

    // 修改
    function edit() {
    	achieveTabs();
		if(tabs.length == 1){
			BIONE.commonOpenDialog("修改", "editTab", 960, $("#center").height()-30, "${ctx}/east/business/blood/" + tabs[0].bloodId + "/edit");
		} else if(tabs.length>1) {
			BIONE.tip("只能选择一行进行修改");
		} else {
			BIONE.tip("请选择需要修改的表信息");
			return;
		}
		
		var row = grid.getSelectedRow();
		if (!row) {
			BIONE.tip('请选择行');
			return;
		}
	}
	
	function achieveTabs() {
		tabs = [];
		var rows = grid.getSelectedRows();
		for(var i in rows) {
			tabs.push(rows[i]);
		}
	}
	
    // 批量删除任务
    function deleteBatch() {
        var rows = grid.getSelectedRows();
        if(rows.length>0){
            // 物理表删除
            $.ligerDialog.confirm('您确定删除这' + rows.length + "条信息吗？", function(yes) {
                var ids = [];
                for(var i =0;i<rows.length;i++){
                    var rowData = rows[i];
                    ids.push(rowData.bloodId);
                }
                if (yes) {
                    $.ajax({
                        async : false,
                        type : "get",
                        dataType : "json",
                        url : '${ctx}/east/business/blood/delete?ids=' + ids.join(','),
                        success : function(result) {
                            if(result && result.deleteNo == "ok"){
                                BIONE.tip('删除成功');
                                grid.loadData();
                            }
                        },
                        error : function(result, b) {
                            BIONE.tip('删除错误 <BR>错误码：' + result.status);
                        }
                    });

                }
            });
        } else {
            BIONE.tip('请至少选择一条记录');
        }
    }
    
    // 校验表结构
    function check() {
    	
    }
    
</script>
</head>
<body>
</body>
</html>