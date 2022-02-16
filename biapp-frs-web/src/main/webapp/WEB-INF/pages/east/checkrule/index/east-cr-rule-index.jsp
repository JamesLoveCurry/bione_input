<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template11_BS.jsp">
<script type="text/javascript">
	var tabObj;
	var dialog;
	var grid;
	var typeCdVal;
	var tabNameVal;

    $(function() {
    	height = $(document).height() - 30; //显示右侧tab下方的页面序号
    	initTab();
        initForm();
        initGrid();
        initButtons();
    });
    
    function initTab() {
    	var tabChangeFlag = false; // tab 是否可切换的标识
		tabObj = $("#tab")
			.ligerTab(
				{
				changeHeightOnResize : true,
				contextmenu : false,
				onBeforeSelectTabItem : function(tabId) {
					if (!tabChangeFlag) {
						return false;
					}
				},
				onAfterSelectTabItem : function(tabId) {
					if (tabId == 'ruleType') {
						tabChange = "ruleType";
						if ($("#ruleType").attr('src') != "") {
						} else {
							$("#ruleType").attr({src : "${ctx}/east/rules/checkRule/ruleType"});
						}
					}
					if (tabId == 'tabType') {
						tabChange = "tabType";
						if ($("#tabType").attr('src') != "") {
						} else {
							$("#tabType").attr({src : "${ctx}/east/rules/checkRule/tabType"});
						}
					}
				}
			});
	
			tabObj
				.addTabItem({
					tabid : 'ruleType',
					text : '按规则类型分类',
					showClose : false,
					content : "<iframe src='' id='ruleType' name='ruleType' style='height: " + height + "px;' frameborder='0'></iframe>"
				});
			tabObj
				.addTabItem({
					tabid : 'tabType',
					text : '按58张表分类',
					showClose : false,
					content : "<iframe src='' id='tabType' name='tabType' style='height: " + height + "px;' frameborder='0'></iframe>"
				});

			$("#ruleType").attr({
				src : "${ctx}/east/rules/checkRule/ruleType"
			});
			
		tabChangeFlag = true;
		tabObj.selectTabItem('ruleType');
		tabChange = "ruleType";
	};

	//初始化form
    function initForm() {
		//搜索框初始化
		$("#search").ligerForm({
			fields : [{
				display : "所属数据类型",
				name : "tabName",
				newline : false,
				labelWidth : 140, 
				width : 150, 
				type : "select",
				cssClass : "field",
				comboboxName : "ruleTypeBox",
				options : {
					url : "${ctx}/east/rules/business/tab/tabList",
				},
				attr : {
					field : "tabName",
					op : "like"
				}
			}, {
			display : "校验类型",
			name : "typeCd",
			newline : false,
			labelWidth : 140, 
			width : 150, 
			type : "select",
			cssClass : "field",
			options : {
			    data : [{
					text : '空值校验',
					id : "空值"
			    },{
					text : '取值范围校验',
					id : '取值范围'
			    }, {
					text : '格式校验',
					id : '格式'
				}, {
					text : '表内逻辑校验',
					id : '表内逻辑'
				}, {
					text : '表间逻辑校验',
					id : '表间逻辑'
				}, {
					text : '总分核对校验',
					id : '总分核对'
				}, {
					text : '记录数校验',
					id : '记录数'
				}, {
					text : '脱敏校验',
					id : '脱敏'
				}, {
					text : '跨监管一致性校验',
					id : '跨监管一致性'
				}]
			},
			attr : {
				op : "=",
				field : "typeCd"
			}
		}]
		})
	};

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
		columns : [ {
			display : '规则标识',
			name : 'ruleId',
			width : '10%',
			align : 'center'
		},{
			display : '规则名称',
			name : 'ruleName',
			width : '20%',
			align : 'center'
		},{
			display:'校验类型',
			name:'typeCd',
			width:'10%',
			align:'left',
			render : function(row){
				return row.typeCd + '校验';
			}
		}, {
			display : '所属数据类型',
			name : 'tabName',
			width : '10%',
			align : 'left'
		}, {
			display : '创建时间',
			name : 'createTm',
			width : '10%',
			align : 'center',
			type:"date",
			format:"yyyy-MM-dd hh:mm:ss"
		}, {
			display : '最后修改时间',
			name : 'updateTm',
			width : '10%',
			align : 'center',
			type:"date",
			format:"yyyy-MM-dd hh:mm:ss"
		}, {
			display : '最后修改人',
			name : 'updateUser',
			width : '10%',
			align : 'center'
		}, {
			display : '状态',
			name : 'ruleSts',
			align : 'center',
			width : '10%',
			sortname : "ruleSts",
			render : function(row){
				return renderHandler(row);
			}
		}],
		checkbox : true,
		rownumbers : true,
		isScroll : false,
		alternatingRow : true,//附加奇偶行效果行
		colDraggable : false,
		dataAction : 'server',//从后台获取数据
		method : 'post',
		url : "${ctx}/east/rules/checkRule/queryList",
		parms : {
			typeCd : typeCdVal,
			tabName : tabNameVal
		},
		sortName : 'typeCd',//第一次默认排序的字段
		sortOrder : 'desc', //排序的方式
		width : '100%',
		height : '100%',
		toolbar : {},
		onAfterShowData : function(){
			if (!$.browser.msie || parseInt($.browser.version, 10) >= 9) {
				if($(".rcswitcher").length > 0){
					$(".rcswitcher").rcSwitcher({
						onText: '启用',
						offText: '停用',
						height:16.5,
						autoFontSize : true
					}).on({
						'turnon.rcSwitcher': function( e, dataObj ){
							var id = dataObj.$input.attr("id");
							var rowId = dataObj.$input.attr("rowid");
							if(id && rowId){
								changeSts(id , 'Y' , rowId);
							}
					    },
					    'turnoff.rcSwitcher': function( e, dataObj ){
							var id = dataObj.$input.attr("id");
					    	var rowId = dataObj.$input.attr("rowid");
							if(id && rowId){
								changeSts(id , 'N' , rowId);
							}
					    }			
					});
				}
			}
		} 
	})
	};
	
	function changeSts(id , sts , rowId){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/east/rules/checkRule/changeRuleSts',
			dataType : 'json',
			type : "post",
			data : {
				ruleId : id,
				sts : sts
			},
			success : function(result){
				if(rowId && grid){
					var row = grid.getRow(rowId);
					if(row){
						row.ruleSts = sts;
					}
				}
			}
		});	
	}
	
	//初始化按钮
    function initButtons() {
		var btns = [{
           /*  text : '新增',
            click : create,
            icon : 'fa-plus'
        }, { */
            text : '修改',
            click : edit,
            icon : 'fa-pencil-square-o'
        }, {
            text : '删除',
            click : deleteBatch,
            icon : 'fa-trash-o'
        }, {
            text : '启用',
            click : isStart,
            icon : 'fa-power-off'
        }, {
            text : '停用',
            click : isStop,
            icon : 'fa-power-off'
        }, {
            text : '导入',
            click : importExcel,
            icon : 'fa-code-fork'
        }, {
            text : '导出',
            click : exportExcel,
            icon : 'fa-code-fork'
        }, {
            text : '模板下载',
            click : modelDown,
            icon : 'fa-book'
        }, {
            text : '报警级别',
            click : warnLevel,
            icon : 'fa-book'
        }];

		BIONE.loadToolbar(grid, btns, function() {});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate('#formsearch');
	};
	
	//新增任务
    function create() {
		BIONE.commonOpenDialog("规则定制", "addRule", 960, $("#tab").height()-30, 
				"${ctx}/east/rules/checkRule/add");
    }

    // 修改
    function edit() {
        var rows = grid.getSelectedRows();
        if (rows.length < 1) {
            BIONE.tip('请选择记录');
        } else if (rows.length > 1) {
            BIONE.tip('只能选择一条记录');
        } else {
            var ruleId = rows[0].ruleId;
            var typeCd = rows[0].typeCd;
            BIONE.commonOpenDialog("规则定义", "editRule", 960, $("#tab").height()-30, 
            		'${ctx}/east/rules/checkRule/edit?ruleId='+ruleId+'&typeCd='+typeCd);
        }
    }

    function modelDown() {
    	var download=null;
		download = $('<iframe id="download1"  style="display: none;"/>');
		$('body').append(download);
		var src = "${ctx}/east/rules/checkRule/download/model";
		download.attr('src', src);
    }

	// 导入
	function importExcel(){
		BIONE.commonOpenDialog("校验规则导入", "importWin", 600, 480,
				"${ctx}/east/rules/checkRule/import?type=Rule");
	}

	// 导出Execl
	function exportExcel() {
		var download=null;
		download = $('<iframe id="download"  style="display: none;"/>');
		$('body').append(download);
		
		var tabName = $("#search input[name='tabName']").val();
		var typeCd= $("#search input[name='typeCd']").val();
		var src = "${ctx}/east/rules/checkRule/download.mo?tabName="
				+ tabName
				+ "&typeCd="
				+ typeCd;
		download.attr('src', src);
	}

    // 批量删除任务
    function deleteBatch() {
        var rows = grid.getSelectedRows();
        if(rows.length>0){
            // 物理表删除
            $.ligerDialog.confirm('您确定删除这' + rows.length + "条任务和任务相关信息吗？", function(yes) {
                var ruleIds = [];
                for(var i =0;i<rows.length;i++){
                    var rowData = rows[i];
                    ruleIds.push(rowData.ruleId);
                }
                if (yes) {
                    $.ajax({
                        async : false,
                        type : "get",
                        dataType : "json",
                        url : '${ctx}/east/rules/checkRule/deleteRule?ruleIds=' + ruleIds.join(','),
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

    // 启用
    function isStart() {
        var rows = grid.getSelectedRows();
        if(rows.length>0){
            var ruleIds = [];
            for(var i =0;i<rows.length;i++){
                var rowData = rows[i];
                ruleIds.push(rowData.ruleId);
            }
            $.ajax({
                async : false,
                type : "get",
                dataType : "json",
                url : '${ctx}/east/rules/checkRule/isStartOrStop?type=start&ruleIds=' + ruleIds.join(','),
                success : function(result) {
                    if(result && result.updateNo == "ok"){
                        grid.loadData();
                    }
                },error : function(result, b) {
                    BIONE.tip('错误 <BR>错误码：' + result.status);
                }
            });
        } else {
            BIONE.tip('请至少选择一条记录');
        }
    }

    // 停用
    function isStop() {
        var rows = grid.getSelectedRows();
        if(rows.length>0){
            var ruleIds = [];
            for(var i =0;i<rows.length;i++){
                var rowData = rows[i];
                ruleIds.push(rowData.ruleId);
            }
            $.ajax({
                async : false,
                type : "get",
                dataType : "json",
                url : '${ctx}/east/rules/checkRule/isStartOrStop?type=stop&ruleIds=' + ruleIds.join(','),
                success : function(result) {
                    if(result && result.updateNo == "ok"){
                        grid.loadData();
                    }
                },error : function(result, b) {
                    BIONE.tip('错误 <BR>错误码：' + result.status);
                }
            });
        } else {
            BIONE.tip('请至少选择一条记录');
        }
    }
    
    // 报警级别
    function warnLevel() {
    	var rows = grid.getSelectedRows();
        if (rows.length < 1) {
            BIONE.tip('请选择记录');
        } else if (rows.length > 1) {
            BIONE.tip('只能选择一条记录');
        } else {
            var ruleId = rows[0].ruleId;
            var typeCd = rows[0].typeCd;
            BIONE.commonOpenDialog("报警级别", "addWarn", 960, $("#tab").height()-30, 
                	'${ctx}/east/rules/warn?ruleId='+ruleId);
        }
    }
    // 状态显示,停/启用等
    function renderHandler(row) {
        if ($.browser.msie && parseInt($.browser.version, 10) < 9) {
			if ("Y"== row.ruleSts){
				return "启用"
			} else {
				return "停用";
			}
		} else {
			var html = "<input class='rcswitcher' type='checkbox' name='check"+row.ruleId
					+"' id='"+row.ruleId+"' rowId='"+row.__id+"' ";
			if(row.ruleSts == "Y"){
				html += "checked";
			}
			html += " />";
			return html;
		}
    }
    
</script>
</head>
<body>
<div id="template.left">
    <div class="content" >
        <!-- <div id="maingrid1" class="maingrid1"></div> -->
        <div id="tab" style="width: 100%; height: 100%; overflow: hidden;">
        </div>
    </div>
	</div>
</body>
</html>