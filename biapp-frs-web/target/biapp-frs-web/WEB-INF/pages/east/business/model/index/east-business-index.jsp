<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">
	var dialog;
	var grid;
	var tabs;

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
				display : "表名",
				name : "tabName",
				newline : false,
				labelWidth : 140, 
				width : 150, 
				type : "text",
				attr : {
					field : "tabName",
					op : "like"
				}
			}, {
				display : "表名（英文）",
				name : "tabNameEn",
				newline : false,
				labelWidth : 140, 
				width : 150, 
				type : "text",
				attr : {
					field : "tabNameEn",
					op : "="
				}
			}]
		})
	};

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
		columns : [ {
			display : '表名',
			name : 'tabName',
			width : '30%',
			align : 'center'
		},{
			display : '表名（英文）',
			name : 'tabNameEn',
			width : '20%',
			align : 'center'
		},{
			display : '分类',
			name : 'tabType',
			width : '20%',
			align : 'center'
		},{
			display:'是否公共表',
			name:'isCommon',
			width : '20%',
			align : 'center'
		}],
		checkbox : true,
		rownumbers : true,
		isScroll : true,
		alternatingRow : true, // 附加奇偶行效果行
		colDraggable : false,
		dataAction : 'server', // 从后台获取数据
		method : 'post',
		url : "${ctx}/east/rules/business/tab/queryList",
		sortName : 'tabNo', // 第一次默认排序的字段
		sortOrder : 'asc', // 排序的方式
		width : '100%',
		height : '100%',
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
        }, {
            text : '维护字段',
            click : maintain,
            icon : 'fa-power-off'
        }, {
            text : '校验表结构',
            click : check,
            icon : 'fa-power-off'
        }];

		BIONE.loadToolbar(grid, btns, function() {});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
	};
	
	//新增任务
    function create() {
		BIONE.commonOpenDialog("新增", "addTab", 960, $("#center").height()-30, 
				"${ctx}/east/rules/business/tab/add");
    }

    // 修改
    function edit() {
    	achieveTabs();
		if(tabs.length == 1){
			BIONE.commonOpenLargeDialog("修改", "editTab","${ctx}/east/rules/business/tab/" + tabs[0].tabName + "/edit");
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
            $.ligerDialog.confirm('您确定删除这' + rows.length + "条任务和任务相关信息吗？", function(yes) {
                var tabNos = [];
                for(var i =0;i<rows.length;i++){
                    var rowData = rows[i];
                    tabNos.push(rowData.tabNo);
                }
                if (yes) {
                    $.ajax({
                        async : false,
                        type : "get",
                        dataType : "json",
                        url : '${ctx}/east/rules/business/tab/delete?tabNos=' + tabNos.join(','),
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

    // 字段维护
    function maintain() {
    	achieveTabs();
		if(tabs.length == 1){
			BIONE.commonOpenLargeDialog("维护字段", "maintainTab","${ctx}/east/rules/business/col/" + tabs[0].tabName + "/maintain");
		} else if(tabs.length>1) {
			BIONE.tip("只能选择一行进行维护");
		} else {
			BIONE.tip("请选择需要维护的表信息");
			return;
		}
		
		var row = grid.getSelectedRow();
		if (!row) {
			BIONE.tip('请选择行');
			return;
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