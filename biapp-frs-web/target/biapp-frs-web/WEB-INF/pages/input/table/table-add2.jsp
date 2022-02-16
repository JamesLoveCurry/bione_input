<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template53.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/udip/remark/table.js"></script>
<script type="text/javascript">
	var typeFlag="";
	var nullable_zh, numberType = [], intType = [], intTypeflag, buttons = [];
	var manager, grid, parentTableId = parent.getTableId(), commitflag, colNameFlag, nullableFlag;
	var tableData = {
		Rows : []
	};
	var initExport = function() {
    		window["downdload"] = $('<iframe id="download"  style="display: none;"/>');
    		$('body').append(downdload);
    };
	var tableInfo = parent.getTableInfo();
	var iii = 1, rowobjUdp;
	$(function() {
		initGrid();
		searchForm();
		initExport();
		manager = $("#maingrid").ligerGetGridManager();
		BIONE.createButton({
			appendTo : "#searchbtn", text : '添加', icon : 'add', width : '50px',
			click : function() {
				commitflag = 'add';
				addNewRow();
			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn", text : '修改', icon : 'modify', width : '50px',
			click : function() {
				commitflag = 'update';
				addNewRow();
			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn", text : '从表中导入', icon : 'import', width : '100px',
			click : function() {
				selectSchemaTableDialog();
			}
		});

		/*BIONE.createButton({
            appendTo : "#searchbtn", text : '模板导入', icon : 'fa-upload', width : '100px',
            click : function() {
                selectSchemaExcelDialog();
            }
        });
        BIONE.createButton({
            appendTo : "#searchbtn", text : '模板导出', icon : 'fa-download', width : '100px',
            click : function() {
                exportSchemaExcelDialog();
            }
        });*/
		$("#search input[name=colType]").change(function() {
			var colTypes = $.ligerui.get("colType").getValue();
		});
		$.ligerui.get("typelength").setValue('');
		$.ligerui.get("xiaoshu").setValue('');
		$("search [name='allowNull']").text(true);
		initbutton();
		$.ajax({ //获取精确数值型
			dataType : 'json',
			url : "${ctx}/rpt/input/table/getColumnNumberType.json&d="+new Date().getTime(),
			success : function(data) {
				numberType = data;
			}
		});
		$.ajax({ //获取常量数值型
			dataType : 'json',
			url : "${ctx}/rpt/input/table/getColumnIntType.json&d="+new Date().getTime(),
			success : function(data) {
				intType = data;
			}
		});
		$.ligerui.get("typelength").setValue('');
		$.ligerui.get("xiaoshu").setValue('');
		$.ajax({ //初始化字段类型
			dataType : 'json',
			url : "${ctx}/rpt/input/table/getColumnType.json?dsId="+tableInfo.dsId+"&d="+new Date().getTime(),
			success : function(data) {
				$.ligerui.get("fieldType").setData(data);
			}
		});
		$("search [name='allowNull']").text(true);
		
		if (parentTableId != '') {
			BIONE.ajax({
				type : "post",
				dataType : 'json',
				url : "${ctx}/rpt/input/table/getTableColInfoById",
				data : {
					"tableId" : parentTableId
				}
			}, function(data) {
				if (templateshow) {
					templateshow();
				}
				for ( var i = 0; i < data.length; i++) {
					if (data[i].allowNull == 'true') {
						nullable_zh = '是';
					} else {
						nullable_zh = '否';
					}
					var seqNos;
					if (data[i].orderNo == null || data[i].orderNo == '') {
						seqNos = 1;
					} else {
						seqNos = data[i].orderNo;
					}
					if (data[i].fieldCnName == 'null' || data[i].fieldCnName == null) {
						data[i].fieldCnName = '';
					}
					manager.addRow({
						id : data[i].id,
						orderNo : seqNos,
						tableId : data[i].tableId,
						fieldEnName : data[i].fieldEnName,
						fieldType : data[i].fieldType,
						fieldLength:  data[i].fieldLength,
						decimalLength:  data[i].decimalLength,
						allowNull : nullable_zh,
						fieldCnName : data[i].fieldCnName,
						defaultValue : data[i].defaultValue
					});
				}
				iii = data.length + 1;
			});
			// 加载主键索引信息, 不然在本 tab 页执行保存时可能会丢失 主键索引信息
			BIONE.ajax({
				url : "${ctx}/rpt/input/table/getPriIndexInfoById?t=" + new Date().getTime(),
				data : { "tableId" : parentTableId }
			}, function(data) {
				if (data && data.length > 0) {
					var paramStrs = "";
					for (var k = 0, l = data.length; k < l; k++) {
						paramStrs = paramStrs + data[k].keyType + ',,' + data[k].keyColumn + ';;'
					}
					parent.setTableInfo_tab3(paramStrs.substring(0, paramStrs.length - 2));
				}
			});
		}
		//UDIP.heart("${ctx}/udip/heart/on");//心跳包
	});

	//弹出表选择触发器
	function selectSchemaTableDialog(options) {
		var options = {
			url : "${ctx}/rpt/input/table/selectSchemaTable?dsId="+tableInfo.dsId+"&operType=table",
			dialogname : 'selectSchemaTableBox',
			title : '选择表字段'
		};
		var width = $(document).width();
		var height = $(document).height();
		window.BIONE.commonOpenDialog(options.title, options.dialogname, width, height, options.url, null, function(data){
			if(!data)
				return ;
			parent.clearTableId();
			addSchemaRow(data);
		});
	}

    //弹出表选择触发器
    function selectSchemaExcelDialog(options) {
        window.BIONE.commonOpenDialog('数据导入', 'inputSchemaInfo', 562, 334,"${ctx}/rpt/input/table/uploadSchemaData"
                        ,null , function(data){
                    parent.clearTableId();
                    if(data){
                        addExcelSchemaRow(data);
                    }
        });
    }

    //导出表选择触发器
    function exportSchemaExcelDialog(options) {
        //新增逻辑判断：使用新制度包导出或旧制度包导出，默认使用旧制度包导出
        var url = "${ctx}/rpt/input/table/export";
        $.ajax({
            cache : false,
            async : true,
            url : url,
            dataType : 'json',
            type : "post",
            beforeSend : function() {
                window.parent.BIONE.loading = true;
                window.parent.BIONE.showLoading("正在生成导出数据，请稍等...");
            },
            complete : function() {
                window.parent.BIONE.loading = false;
                window.parent.BIONE.hideLoading();
            },
            success : function(result) {
                if (!result.fileName || result.fileName == "error") {
                    parent.BIONE.tip("无可导出数据");
                    return;
                }
                var src = '';
                src = "${ctx}/rpt/input/table/download?filepath="
                        + result.fileName;//导出成功后的excell文件地址
                downdload.attr('src', src);//给下载文件显示框加上文件地址链接
            },
            error : function() {
                parent.BIONE.tip("数据加载异常，请联系系统管理员");
            }
        });
    }
	
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "字段名称", name : "fieldEnName", newline : true, width : "40%", type : "text"
			}, {
				display : "中文名", name : "fieldCnName", newline : false, width : "40%", type : "text",
				validate : {
					required : true
				}
			}, {
				display : "字段类型", name : "fieldType", newline : false, width : "40%", type : "select",
				validate : {
					required : true
				},
				options :{
					onSelected: function(id,text){
						if(id!=null&&id!=""){
							var colTypes = id;
							var j = 0;
							intTypeflag = false;//判断是否为常量数值型,false为常量数值型
							for ( var k = 0; k < intType.length; k++) {
								if (intType[k].toUpperCase()== colTypes.toUpperCase()) {
									$.ligerui.get("typelength").setDisabled();
									$('#typelength').attr('disabled', 'disabled');
									$.ligerui.get("typelength").setValue('');
									$.ligerui.get("xiaoshu").setDisabled();
									$('#xiaoshu').attr('disabled', 'disabled');
									$.ligerui.get("xiaoshu").setValue('');
									break;
								}
								j++;
							}
							if (j == intType.length) {
								intTypeflag = true;
								$.ligerui.get("typelength").setEnabled();
								$('#typelength').removeAttr('disabled');
								$.ligerui.get("xiaoshu").setDisabled();
								$('#xiaoshu').attr('disabled', 'disabled');
								$.ligerui.get("typelength").setValue('');
								$.ligerui.get("xiaoshu").setValue('');
								if (numberType != null) {
									for ( var i = 0; i < numberType.length; i++) {
										if (numberType[i].toUpperCase() == colTypes.toUpperCase()) {
											$.ligerui.get("xiaoshu").setEnabled();
											$('#xiaoshu').removeAttr('disabled');
											break;
										}
									}
								}
							}
						}
						
					}
				}
			}, {
				display : "默认值", name : "defaultValue", newline : false, width : "20%", type : "text"
			}, {
				display : "字段长度", name : "typelength", newline : false, width : "5%", type : "spinner",
				validate : {
					required : true, digits : true
				},
				options : {
					type : 'int', valueFieldID : 'typelengthId'
				}
			}, {
				display : "小数位", name : "xiaoshu", newline : false, width : "10%", type : "spinner", 
				validate : {
					required : true, digits : true
				},
				options : {
					type : 'int', valueFieldID : 'xiaoshuId', minValue : 0
				}
			}, {
				display : "可否为空", name : "allowNull", newline : false, width : "28%", type : "hidden"
			} ]
		})

	}

	function initbutton() {
		buttons.push({
			text : '取消', onclick : cancleCallBack
		});
		buttons.push({
			text : '下一步', onclick : next
		});
		buttons.push({
			text : '上一步', onclick : upset
		});
		/*if (parentTableId == '') {
			buttons.push({
				
				text : '保存', onclick : save_objDef
			});
		}  else {
			buttons.push({
				
				text : '保存', onclick : update_objDef
			});
		} */
		BIONE.addFormButtons(buttons,true);
	}
	
	function addNewRow() {
		var fieldEnName = $('#fieldEnName').val();
		var fieldType = $("#fieldType").val();
		var fieldLength =$('#typelength').val();
		var allowNull = true;
		var decimalLength = $('#xiaoshu').val();
		var defaultValue = $('#defaultValue').val();
		if(!checkDefaultValue(fieldType.toLowerCase(),defaultValue,fieldLength,decimalLength))
			return ;
		addRow(fieldEnName,fieldType,fieldLength,allowNull,decimalLength,defaultValue);
		
	}
	
	function addSchemaRow(fields){
		commitflag="add";
		var rows = grid.rows;
		$.each(rows, function(i, n) {
			grid.deleteRow(n);
		});
		for(var i = 0 ;i < fields.length;i++){
			var field = fields[i];
			field.allowNull = true;
			//addRow(fieldEnName,fieldType,fieldLength,allowNull,decimalLength)
			addRow(field.fieldEnName,field.fieldType,field.fieldLength,field.allowNull,field.decimalLength,field.defaultValue,field.comments,field.dataPercision,field.dataScan,field.isprimary,true);
		}
	}

	function addExcelSchemaRow(fields){
    		commitflag="add";
    		var rows = grid.rows;
    		$.each(rows, function(i, n) {
    			grid.deleteRow(n);
    		});
    		if(fields){
                for(var i = 0 ;i < fields.length;i++){
                                var field = fields[i];
                                addRow(field.fieldEnName,field.fieldType,field.fieldLength,field.allowNull,field.decimalLength,field.defaultValue,field.fieldCnName,field.dataPercision,field.dataScan,0,true);
                            }
    		}

    	}
	
	function checkDefaultValue(fieldType,defaultValue,fieldLength,decimalLength){
		if(defaultValue==null||defaultValue=="")
			return true;
		var pattern = "";
		var tipMsg = "";
		var  isSucc = true;
		if(fieldType=="varchar"||fieldType=="varchar2"){
			if(defaultValue.length>fieldLength)
			{
				tipMsg = "长度大于默认长度,请重新填写";
				isSucc = false;
			}
		}else if(fieldType=="decimal"){
			var value1 = (defaultValue.split("."))[0];
			var value2 = (defaultValue.split("."))[1];
			if(value1.length>32){
				tipMsg = "字段长度不能大于或等于32,请重新填";
				isSucc = false;
			}
			if(value2&&value2.length&&value2.length>decimalLength ){
				tipMsg = "字段默认值的小数位设置不正确,请重新填";
				isSucc = false;
			}
		}
		if(isSucc){
			if(fieldType=="decimal")
			{
				pattern="\\d+(.\\d+)?";
				tipMsg = "decimal类型数据格式如123.432或者32";
			}
			else if(fieldType=="integer")
			{
				pattern="\\d+";
				tipMsg = "integer类型数据格式如123";
			}
			else if(fieldType=="date")
			{
				pattern = "\\d{4}-[0,1]{1}[0-9]-\\d{2}";
				tipMsg = "date类型数据格式如2010-02-09";
			}
			else if(fieldType=="timestamp")
			{
				pattern = "\\d{4}-[0,1]{1}[0-9]-\\d{2} ([0,1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])";
				tipMsg = "timestamp类型数据格式如2010-02-09 01:04:11";
			}
			var exp = new RegExp(pattern);
			isSucc =  exp.test(defaultValue);
		}
		
		if(!isSucc)
			BIONE.tip(tipMsg);
		return isSucc;
	}
	
	function addRow(fieldEnName,fieldType,fieldLength,allowNull,decimalLength,defaultValue,fieldCnNm,dataPercision,dataScan,isprimary,isImport){
		var flage;
		if (fieldEnName == "" || fieldEnName == null) {
			BIONE.tip('请输入字段名称。');
			return;
		}
		if(/^\s+$/gi.test(document.getElementById('fieldEnName').value)){
			BIONE.tip('字段名称不能为空格！');
			return;
		}
        var fieldCnName = "";
		if(isImport){
		    if(fieldCnNm == null || fieldCnNm == ""){
                fieldCnName = fieldEnName
            } else {
                fieldCnName = fieldCnNm;
            }
        } else {
            fieldCnName = fieldCnNm?fieldCnNm:$('#fieldCnName').val();
        }
		if (fieldCnName == "" || fieldCnName == null) {
			BIONE.tip('请输入中文名。');
			return;
		}
		if(/^\s+$/gi.test(document.getElementById('fieldCnName').value)){
			BIONE.tip('中文名不能为空格！');
			return;
		}
		var reg = /^\d+$/;
		if (flage == 'false') {
			BIONE.tip('字段名称已存在，请重新输入字段名称！');
			return;
		}
		var data = manager.getData();
		if (commitflag == 'add') {
			for (i = 0; i < data.length; i++) {
				if (fieldEnName.toUpperCase() == data[i].fieldEnName.toUpperCase()) {
					BIONE.tip('不能添加相同的字段名称。');
					return;
				}
			}
		} else {
			if (colNameFlag == null || colNameFlag == '') {
				BIONE.tip('需修改表字段信息，请先双击要修改的表字段行。');
				return;
			} else {
				for (i = 0; i < data.length; i++) {
					if (fieldEnName.toUpperCase() == data[i].fieldEnName.toUpperCase()
							&& colNameFlag.toUpperCase() != fieldEnName.toUpperCase()) {
						BIONE.tip('该字段名称已经存在于其他字段中。');
						return;
					}
				}
			}
		}
		if (fieldType == "" || fieldType == null) {
			BIONE.tip('请输入字段类型。');
			return;
		}
		if (intTypeflag == true) {
			if ((fieldLength == "" || fieldLength == null)
					&& fieldType.toLowerCase() != 'number') {
				BIONE.tip('请输入字段类型的长度。');
				return;
			}
			if (fieldLength != "" && fieldLength != null) {
				if (fieldLength <= 0) {
					BIONE.tip('字段长度不能小于等于0。');
					return;
				}
				if (!reg.test(fieldLength)) {
					BIONE.tip('字段长度请输入整数类型。');
					return;
				}
				if (fieldType.toLowerCase() == 'char' && fieldLength > 2000) {
					BIONE.tip('字段类型为char的字段长度不能超过2000。');
					return;
				}
				if (fieldType.toLowerCase() == 'varchar2' && fieldLength > 4000) {
					BIONE.tip('字段类型为varchar2的字段长度不能超过4000。');
					return;
				}
				if (fieldType.toLowerCase() == 'varchar' && fieldLength > 4000) {
					BIONE.tip('字段类型为varchar的字段长度不能超过4000。');
					return;
				}
				if (fieldType.toLowerCase() == 'nvarchar2' && fieldLength > 4000) {
					BIONE.tip('字段类型为nvarchar2的字段长度不能超过4000。');
					return;
				}
				if (fieldType.toLowerCase() == 'number' && fieldLength > 38) {
					BIONE.tip('字段类型为number的字段长度不能超过38。');
					return;
				}
				if (fieldType.toLowerCase() == 'decimal' && fieldLength > 38) {
					BIONE.tip('字段类型为decimal的字段长度不能超过38。');
					return;
				}
				if (numberType != null) {
					for ( var i = 0; i < numberType.length; i++) {
						if (numberType[i] == fieldType) {
							if (decimalLength == '') {
								colTypes = fieldType + "(" + fieldlength + ")";
							} else {
								if (xiaoshu * 1 <= 0) {
									BIONE.tip('小数位长度不能小于等于0。');
									return;
								} else if (!reg.test(decimalLength)) {
									BIONE.tip('小数位请输入整数类型。');
									return;
								} else if (reg.test(decimalLength)
										&& xiaoshu * 1 >= fieldlength * 1) {
									BIONE.tip('小数位长度不能大于等于字段长度。');
									return;
								}
							}
						}
					}
				}
			}
		} else {
			if (fieldType == 'timestamp') {
				fieldLength=6
			}
		}
		//add by cl 如果是导入的数据,变更数据长度
		if(dataPercision&&fieldType.toLowerCase() == 'number'){
			fieldLength=dataPercision;
			decimalLength = dataScan?(dataScan+""):"0";
		}
		if(!checkDefaultValue(fieldType.toLowerCase(),defaultValue,fieldLength,decimalLength))
			return ;
		if (allowNull == true) {
			nullable_zh = '是';
		} else {
			nullable_zh = '否';
		}
		fieldEnName = $.trim(fieldEnName).toUpperCase();
		if (fieldEnName.length > 30) {
			BIONE.tip('表字段名的长度不能超过30！');
			return;
		}
		if (fieldCnName.length > 120) {
			BIONE.tip('表中文名的长度不能超过120！');
			return;
		}
		if (commitflag == 'add') {
			if (data != null && data.length > 0) {
				manager.select(manager.records['r1001']);
			}
			iii = data.length + 1;
			var row = manager.getSelectedRow();
			manager.addRow({
				id : '',
				orderNo : iii,
				fieldEnName : fieldEnName,
				fieldType : fieldType,
				fieldLength: fieldLength,
				decimalLength: decimalLength,
				allowNull : nullable_zh,
				fieldCnName : fieldCnName,
				isprimary : isprimary,
				defaultValue : defaultValue
			});
			iii++;
			rowobjUdp = '';
		} else {
			if (nullableFlag != allowNull) { 
				var tableIndexInfo = parent.getTableInfo().tableIndexInfo;
				if (tableIndexInfo != null && tableIndexInfo != '') {
					var indexinfo = tableIndexInfo.split(';;');
					for ( var i = 0; i < indexinfo.length; i++) {
						var indexinfo2 = indexinfo[i].split(',,');
						if (indexinfo2[0].toLowerCase() == 'primary') {
							if (indexinfo2[1].indexOf(colNameFlag.toUpperCase()) >= 0) {
								BIONE.tip('该表字段属于主键字段，不允许为空。');
								return;
							}
						} else {
							continue;
						}
					}
				}
			}
			if (rowobjUdp == '' || rowobjUdp == null) {
				BIONE.tip('需修改表字段信息，请先重新双击要修改的表字段行。');
			} else {
				manager.updateCell("fieldEnName", fieldEnName, rowobjUdp);
				manager.updateCell("fieldType", fieldType, rowobjUdp);
				manager.updateCell("allowNull", nullable_zh, rowobjUdp);
				manager.updateCell("fieldCnName", fieldCnName, rowobjUdp);
				manager.updateCell("fieldLength", fieldLength, rowobjUdp);
				manager.updateCell("decimalLength", decimalLength, rowobjUdp);
				manager.updateCell("defaultValue", defaultValue, rowobjUdp);
				colNameFlag = fieldEnName;
			}
		}
	}

	function deleteRow(rowid) {
		var data = manager.getData();
		var fieldEnName = data[rowid].fieldEnName;
		var tableIndexInfo = parent.getTableInfo().tableIndexInfo;
		if (tableIndexInfo != null && tableIndexInfo != '') {
			var indexinfo = tableIndexInfo.split(';;');
			for ( var i = 0; i < indexinfo.length; i++) {
				var indexinfo2 = indexinfo[i].split(',,');
				if (indexinfo2[0].toLowerCase() == 'primary') {
					var cols = indexinfo2[1].split(';');
					for ( var k = 0; k < cols.length; k++) {
						if (cols[k].toUpperCase() == fieldEnName.toUpperCase()) {
							BIONE.tip('该表字段属于主键字段不能删除，需去掉该字段为主键保存后才行。');
							return;
						}
					}
				} else {
					var cols = indexinfo2[1].split(';');
					for ( var k = 0; k < cols.length; k++) {
						if (cols[k].toUpperCase() == fieldEnName.toUpperCase()) {
							$.ligerDialog.confirm('该表字段属于唯一性或索引约束字段，删除后约束将被删除，是否需要删除该字段!', function(yes) {
								if (yes) {
									manager.deleteRow(rowid);
								}
							});
							return;
						}
					}
					continue;
				}
			}
		}
		manager.deleteRow(rowid);
	}

	function cancleCallBack() {
		parent.closeDsetBox();
	}

	function save_objDef() {
		var data = manager.getData();
		if (data == null || data.length == 0) {
			BIONE.tip('请至少添加一条字段信息。');
			return;
		}
		setTableInfo();
		parent.saveTableInfo();
	}

	function update_objDef() {
		var data = manager.getData();
		var isUpdateDsId = parent.getIsUpdateDsId();
		if (data == null || data.length == 0) {
			BIONE.tip('请至少添加一条字段信息。');
			return;
		}
		setTableInfo();
		if (isUpdateDsId != '' && isUpdateDsId == true) {
			BIONE.tip('该表已经修改数据源，请先重建补录表和接口表!');
			return;
		}
		$.ajax({
			dataType : 'json',
			url : "${ctx}/rpt/input/table/checkTempleTableName",
			data : {
				"dsId" : tableInfo.dsId,
				"tableEnName" : tableInfo.tableEnName,
				"tableId" : parentTableId
			},
			success : function(result1) {
				if (result1 == false) {
					$.ligerDialog.confirm('现在是修改补录表信息，是否需要同时修改接口表信息(该接口表已经存在数据源中)!', function(yess) {
						parent.setUpdate(yess);
						parent.updateTableInfo();
						parent.next('2');
					});
				} else {
					parent.setUpdate(false);
					parent.updateTableInfo();
					parent.next('2');
				}
			}
		});
	}

	function upset() {
		setTableInfo();
		parent.next('1');
	}

	function next() {
		var data = manager.getData();
		if (data == null || data.length == 0) {
			BIONE.tip('请至少添加一条字段信息。');
			return;
		}
		setTableInfo();
		parent.setIsFreshTableCol(false);
		parent.next('3');
	}

	function setTableInfo() {
		var paramStr = "";
		var allowNull;
		var data = manager.getData();
		//对调整的字段按照序号排序
		function reverseSort(a, b) {
			if (a.orderNo < b.orderNo)
				return -1
			if (a.orderNo > b.orderNo)
				return 1
			return 0
		}
		data.sort(reverseSort);
		for (i = 0; i < data.length; i++) {
			if (data[i].allowNull == '是') {
				allowNull = 'true';
			} else {
				allowNull = 'false';
			}
			if (data[i].fieldCnName == null || data[i].fieldCnName == '') {
				fieldCnName = '';
			} else {
				fieldCnName = data[i].fieldCnName;
			}
			if (i == data.length - 1) {
				paramStr = paramStr + data[i].fieldEnName + ",,"
						+ data[i].fieldType + ",," + allowNull + ",,"
						+ fieldCnName + ",," + data[i].orderNo + ",,"
						+ data[i].id +",," + data[i].fieldLength+",,"
						+ data[i].decimalLength+",,"
						+ data[i].isprimary+",,"
						+ data[i].defaultValue;
			} else {
				paramStr = paramStr + data[i].fieldEnName + ",,"
						+ data[i].fieldType + ",," + allowNull + ",,"
						+ fieldCnName + ",," + data[i].orderNo + ",,"
						+ data[i].id +",," + data[i].fieldLength+",,"
						+ data[i].decimalLength + ",,"
						+ data[i].isprimary+",,"
						+ data[i].defaultValue
						+ ";;";
			}
		}
		parent.setTableInfo_tab2(paramStr);
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [
			{
				display : '排序', name : 'orderNo', align : 'center', width : 50, type : 'int',
				editor : {
					type : 'int'
				}
			}, {
				hide : 1, width : "1%", name : 'tableId'
			}, {
				display : '字段名称', width : "20%", name : 'fieldEnName', type : 'text'
			}, {
				display : '中文名', width : "20%", name : 'fieldCnName', type : 'text'
			}, {
				display : '字段类型', width : "10%", name : 'fieldType', type : 'text',
				render : function(rowData) {
					if (rowData.fieldType == "1")
						return "字符串";
					if (rowData.fieldType == "2")
						return "整形";
					if (rowData.fieldType == "3")
						return "浮点型";
					return rowData.fieldType;
				}
			}, {
				display : '字段长度', width : "10%", name : 'fieldLength', type : 'text',
				render : function(rowData,rowindex,value) {
					if(value!=""&&value!=null&&value!='undefined'){
						return value;
					}else{
						return '0';
					}
				}
			}, {
				display : '字段精度', width : "10%", name : 'decimalLength', type : 'text',
				render : function(rowData,rowindex,value) {
					if(value!=""&&value!=null&&value!='undefined'){
						return value;
					}else{
						return '0';
					}
				}
			}, {
				display : '可否为空', width : "7%", name : 'allowNull', type : 'text'
			}, {
				display : '默认值', width : "10%", name : 'defaultValue', type : 'text',
				render : function(rowData,rowindex,value){
					if(value!=""&&value!=null&&value!='undefined'){
						return value;
					}else{
						return '';
					}
				}
			}, {
				display : '操作', width : '7.5%',
				render : function(rowdata, rowindex, value) {
					var h = "";
					if (!rowdata._editing) {
						h += "<a href='javascript:deleteRow(" + rowindex + ")'>删除</a> ";
					}
					return h;
				}
			} ],
			isScroll : true,
			rownumbers : false,
			checkbox : false,
			enabledEdit : true,
			clickToEdit : true,
			data : tableData,
			usePager : false,
			onDblClickRow : function(data, rowindex, rowobj) {
				$("#search [name='fieldEnName']").val(data['fieldEnName']);
				$("#search [name='fieldCnName']").val(data['fieldCnName']);
				colNameFlag = data['fieldEnName'];
				if (data['allowNull'] == '是') {
					nullableFlag = true;
					$("search [name='allowNull']").text(true);
				} else {
					nullableFlag = false;
					$("search [name='allowNull']").text(false);
				}
				$.ligerui.get("fieldType").selectValue(data.fieldType);
				if(data.fieldLength!=null)
					$.ligerui.get("typelength").setValue(data.fieldLength);
				if(data.decimalLength!=null)
					$.ligerui.get("xiaoshu").setValue(data.decimalLength);
				$("#search [name='defaultValue']").val(data['defaultValue']);
				rowobjUdp = rowobj;
			},
			width : '100%',
			sortName : 'orderNo', //第一次默认排序的字段
			sortOrder : 'asc'
		});
	}
</script>
</head>
<body>
</body>
</html>