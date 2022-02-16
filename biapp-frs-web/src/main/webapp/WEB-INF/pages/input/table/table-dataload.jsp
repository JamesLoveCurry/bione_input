<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1G.jsp">
<script type="text/javascript">
	var mainform,contents;
	var tableId = '${tableId}';
	var currentType ;
	var dataObj;
	var dsName, dsId;
	$(function() {
		initFrame();
		initData();
		initBtn();
	});
	
	function initData(){
		$.ajax({
			url : "${ctx}/rpt/input/table/getDataLoadInfo.json?tableId="+tableId+"&d="+ new Date().getTime(),
// 			dataType : 'json',
			type : "post",
			success : function(result) {
				if(result==null){
					mainform.setData({
						tableId:tableId
					});
					initNodeContent(2,result.cfgId);
// 					$("#nodeContent").html('<iframe frameborder="0" id="dataloadFrame" name="dataloadFrame" style="height:100%;width:100%;" src="${ctx}/rpt/input/table/toEditTypeData?type=2"></iframe>');
				}else{
					mainform.setData({
						tableId : result.tableId,
						typeId :　result.typeId,
						cfgId : result.cfgId,
						type: '2',
						remark : result.remark
					});
					initNodeContent(result.type,result.cfgId,result.tableId);
// 					$("#nodeContent").html('<iframe frameborder="0" id="dataloadFrame" name="dataloadFrame" style="height:95%;width:98%;" src="${ctx}/rpt/input/table/toEditTypeData?type='+result.type+'&tableId='+tableId+'&cfgId='+result.cfgId+'"></iframe>');
				}
			},
			error : function(result, b) {
				if(result==null||result.responseText==''){
					mainform.setData({
						tableId:tableId
					});
					initNodeContent(2,result.cfgId);
// 					$("#nodeContent").html('<iframe frameborder="0" id="dataloadFrame" name="dataloadFrame" style="height:100%;width:100%;" src="${ctx}/rpt/input/table/toEditTypeData?type=2"></iframe>');
				}else{
					mainform.setData({
						tableId : result.tableId,
						typeId :　result.typeId,
						cfgId : result.cfgId,
						type: '2',
						remark : result.remark
					});
					initNodeContent(result.type,result.cfgId,result.tableId);
// 					$("#nodeContent").html('<iframe frameborder="0" id="dataloadFrame" name="dataloadFrame" style="height:95%;width:98%;" src="${ctx}/rpt/input/table/toEditTypeData?type='+result.type+'&tableId='+tableId+'&cfgId='+result.cfgId+'"></iframe>');
				}
			}
		});
	}

	function initNodeContent(type,cfgId,tableId){
	    if(type == 2){
			initSqlForm(cfgId);
	    }else if(type == 1){
       		initDataGrid(cfgId,tableId);
	    }
	}
	
	function initFrame(){
		mainform = $("#mainform").ligerForm({
			fields : [{
					name : "tableId",
					type : "hidden"
				},{
					name : "typeId",
					type : "hidden"
				},{
					name : "cfgId",
					type : "hidden"
				},{
	                name : "dsId",
	                type : "hidden"
	            },{
					display : '任务类型',
					name : 'type',
					newline : false,
					labelWidth : 100,
					height :30,
					width : 220,
					space : 30,
					type : "select",
					comboboxName : "type_box",
					cssClass : "field",
					options : {
					    data : [/* {
								text : "自定义数据",
								id : "1"
						    },  */{
								text : "sql查询",
								id : "2"
						    }
					    ],
					    initValue : 2,
						onSelected : function(key,value){
							if(key&&key!="")
								changeType(key);
						}
				}
			},{
                display : "数据来源", name : 'dsName', newline : false, type : 'text',
                validate : {
                    required : true, maxlength : 32
                }
            },{
				display : "备注",
				name : "remark",
				newline : true,
				type : "textarea",
				height : 80,
				width : 493,
				attr : {
					style : "resize: none;"
				}
			}]
		});
		$("#dsName").live("click", function() {
            openNewDilog();
        });
		$("#dsName").attr("readonly", "readonly");
	}
	function openNewDilog() {
        dsName = $("#dsName");
        dsId = $("#dsId");
        dialog = BIONE.commonOpenDialog('选择数据源', "dsList", "850", "370",
                "${ctx}/bione/mtool/datasource/chackDS/2");
    }
	
	function changeType(type){
		if(currentType==type){
			return ;
		}else{
			currentType = type;
		    if(type == 1){
		        $("#sqlform").empty();
		        $("#sqlTip").attr('style','display:none;');
		        $("#gridDiv").append('<div id="maingrid" class="maingrid"></div>');
		        initDataGrid($("#mainform input[name='cfgId']").val(),tableId);
		    }else{
		        $("#gridDiv").empty();
		        $("#sqlTip").attr('style','display:inline;');
		        initSqlForm($("#mainform input[name='cfgId']").val());
		    }
		}
	    
// 		$("#dataloadFrame").attr("src","${ctx}/rpt/input/table/toEditTypeData?type="+currentType+"&cfgId="+$("#mainform input[name='cfgId']").val()+"&tableId="+tableId+"&d="+new Date());
	}
	
	function initBtn(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("dataload");
			}
		});
		buttons.push({
			text : '保存',
			onclick : f_save
		});

		BIONE.addFormButtons(buttons,true);
	}

	function f_save() {
		BIONE.showLoading("正在保存数据...");
		var dataInfo={
				tableId : tableId,
				typeId : $("#mainform input[name='typeId']").val(),
				cfgId : $("#mainform input[name='cfgId']").val(),
				type : $("#mainform input[name='type']").val(),
				remark : $("#mainform textarea[name='remark']").val(),
				dsId : $("#mainform input[name='dsId']").val()
		};
		var type=$("#mainform input[name='type']").val();
		//1 .自定义数据		2.sql
		var action = "${ctx}/rpt/input/table/saveDataLoadSql";
		if(type == "1"){
			action = "${ctx}/rpt/input/table/saveDataLoadData";
			dataInfo.dataContent = encodeURI(encodeURI(getGridData()));
		}else{
			dataInfo.sqlText = getSqlData();
		}
		if(!(dataInfo.dsId)){
			BIONE.tip("请选择数据来源");
			BIONE.hideLoading();
			return;
		}
		if(!(dataInfo.sqlText)){
			BIONE.tip("请填写SQL语句");
			BIONE.hideLoading();
			return;
		}
		$.ajax({
			url : action+"?d="+ new Date().getTime(),
			contentType : "application/json",
			type : "post",
			data : JSON2.stringify(dataInfo),
			success : function(result) {
				BIONE.hideLoading();
				if(result=="1")
				{
					BIONE.tip("保存成功");
					BIONE.closeDialog("dataload");
				}
				else BIONE.tip("保存失败,错误信息:"+result);
			},
			error : function(result, b) {
				BIONE.hideLoading();
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

    /**
    * 生成grid
    */
	
    var grid;
    function initDataGrid(cfgId,tabId) {
	    var cfgId = cfgId;
	    var tabId = tabId;
        
        initGrid(cfgId,tabId);
    }
    var colInfo;
    function initGrid(cfgId,tabId) {
        $.ajax({
            url : '${ctx}/rpt/input/table/getTableColumnInfo',
//          dataType : 'json',
            type : "get",
            data : {
                tableId : tabId
            },
            success : function(data) {
                if(data){
                    var columns = [];
                    colInfo = [];
                    $.each(data, function(i, n) {
                        var showText = n.fieldCnName&&n.fieldCnName!=""?n.fieldCnName:n.fieldEnName;
                        var type = "text";
                        var format = "";
                        var fieldLength =n.fieldLength;
                        if(n.fieldType=="DATE"||n.fieldType=="TIMESTAMP")
                        {
                            type="date";
                            if(n.fieldType=="DATE")
                                format= "yyyy-MM-dd";
                            else if(n.fieldType="TIMESTAMP")
                                format = "yyyy-MM-dd hh:mm:ss"
                        }
                        else if(n.fieldType=="NUMERIC"||n.fieldType=="DECIMAL")
                            type="number";
                        var fieldEnName = n.fieldEnName;
                        var oneColumn ={
                            display : showText,
                            name : fieldEnName,
                            type : type,
                            width : 150,
                            align : 'left',
                            editor : {
                                type : type,
                                maxlength : 4
                            }
                        };
                        if( format != "")
                        {
                            oneColumn.format=format;
                            oneColumn.render= function(rowdata,rownum,value){
                                if(value&&value!=null)
                                {
                                    var tmpDate = BIONE.getFormatDate(value, format);
                                    if(tmpDate!=null)
                                    {
                                        rowdata[fieldEnName] = BIONE.getFormatDate(value, format);
                                        return rowdata[fieldEnName];
                                    }else return rowdata[fieldEnName];
                                }
                            }
                        }
                        if(fieldLength&&fieldLength!="0"){
                            oneColumn.render= function(data){
                                if(data[fieldEnName]){
                                    var length = data[fieldEnName].length;
                                    if(length>fieldLength){
                                        BIONE.tip("字段["+fieldEnName+"]的长度不能超过"+fieldLength);
                                        return data[fieldEnName].substring(0, fieldLength);
                                    }
                                }
                                return data[fieldEnName];
                            }
                        }
                        columns.push(oneColumn);
                        colInfo.push(n.fieldEnName);
                    });
                    if(cfgId&&cfgId!=null&&cfgId!="")
                        initDataCol(columns);
                    else 
                        initEmptyGrid(columns);
                    
                }
            },
            error : function(result, b) {
                BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
            }
        });
    }
    
    function initDataCol(columns){
        $.ajax({
            url : '${ctx}/rpt/input/table/getDataInfo?cfgId='+cfgId,
//          dataType : 'json',
            success : function(value) {
                var datas = { Rows: [] };
                datas . Rows = value;
                grid = $("#maingrid").ligerGrid({
                    toolbar : {},
                    columns : columns,
                    data : datas,
                    checkbox : true,
                    usePager : false, //服务器分页
                    alternatingRow : true, //附加奇偶行效果行
                    colDraggable : true,
                    enabledEdit : true,
                    isScroll : true,
                    resizable : true,
                    height : '97%',
                    width : '99%',
                    sortName : 'taskNm',//第一次默认排序的字段
                    sortOrder : 'asc', //排序的方式
                    rownumbers : true
                });
                initToolbar();
            },
            error : function(result, b) {
                BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
            }
        });
    }
    
    function initEmptyGrid(columns){

        grid = $("#maingrid").ligerGrid({
            toolbar : {},
            checkbox : true,
            usePager : false, //服务器分页
            columns : columns,
            alternatingRow : true, //附加奇偶行效果行
            colDraggable : true,
            enabledEdit : true,
            isScroll : true,
            resizable : true,
            height : '85%',
            width : '99%',
            sortName : 'taskNm',//第一次默认排序的字段
            sortOrder : 'asc', //排序的方式
            rownumbers : true
        });
        initToolbar();
    }
    
    function initToolbar(){
            var toolBars = [ {
                text : '增行',
                click : f_addRow,
                icon : 'add'
            }, {
                text : '删行',
                click : f_deleteRow,
                icon : 'delete'
            }];
            BIONE.loadToolbar(grid, toolBars, function() {
            });
    }
    
    function f_addRow(){
        grid.addRow();
    }
    
    function f_deleteRow(){
        var rows = grid.getSelectedRows();
        if(rows.length){
            for(var i=0;i<rows.length;i++){             
                grid.deleteRow(rows[i]);    
            }
        }
        grid.endEdit();
    }
    
    function getEmptyColumn(){
        var emptyCol={};
        for(var i = 0 ;i <colInfo.length;i++){
            emptyCol[colInfo[i]]="";
        }
        return emptyCol;
    }
    
    function getGridData(){
        grid.endEdit();
        return JSON2.stringify(grid.rows);
    }


//初始化sql文本域
    var sqlform;
    
    function initSqlForm(cfgId) {
        var cfgId = cfgId;
        initSqlform();
        initSqlData(cfgId);
    };
    
    function initSqlData(cfgId){
        $.ajax({
            url : '${ctx}/rpt/input/table/getSqlInfo?cfgId='+cfgId,
//          dataType : 'json',
            type : "get",
            success : function(data) {
                if (data ){
                    $("#sqlform textarea[name='sqlText']").val(data.SQL);
                    $("#mainform input[name='dsId']").val(data.DS_ID);
                    $("#mainform input[name='dsName']").val(data.DS_NAME);
                }
            },
            error : function(result, b) {
                BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
            }
        });
    }
    
    function initSqlform(){
        sqlform = $("#sqlform");
        sqlform.ligerForm({
            fields : [{
                display : "SQL语句",
                name : 'sqlText',
                newline : true,
                type : 'textarea',
                width : 600,
                validate : {
                    maxlength : 500,
                    required : true
                }
            }]
        });
        $("#sqlText").css({ height:'100px'});
        jQuery.metadata.setType("attr", "validate");
//         BIONE.validate(mainform);
    }
    function getSqlData(){
        var sqlText = $("#sqlText").val();
        return sqlText;
    }
    
	
</script>
</head>
<body>
	<div id="template.center">
		<form name="mainform" method="post" id="mainform" action=""></form>
	</div>
</body>
</html>