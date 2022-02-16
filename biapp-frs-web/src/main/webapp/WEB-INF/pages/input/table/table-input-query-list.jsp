<%@ page language="java" contentType="text/html; charset=UTF-8"
		 pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template1_BS.jsp">
<head>
	<script type="text/javascript">
		var grid, items, ids, searchFields = [];
		var formName = [], formType = [], formLib = [], formSelectable = [], formNullable = [], formDataType = [], formDetail = [], formWritable = [],defaultValue=[];
		var libMap = {};
		var app = {};
		var templateId = '${templateId}';
		var canEdit = true;
		var downdload;

		$(function() {
			searchForm();
			initButton();
			initGrid();
			initlibMap();
			if(templateId != ""){
				BIONE.addSearchButtons("#search", grid, "#searchbtn");
			}
			downdload = $('<iframe id="download"  style="display: none;"/>');
			$('body').append(downdload);
		});

		function initlibMap(){
			if(templateId){
				$.ajax({
					async : false,
					url : '${ctx}/rpt/input/library/libMapbyTempleId2.json?d='+new Date().getTime(),
					dataType : 'json',
					type : "get",
					data : {
						"templeId" : templateId,
						"caseId" : ""
					},
					success : function(data2) {
						window.libMa2 = data2;
						$.each(data2, function(col, list){
							libMap[col] = {};
							$.each(list, function(key, code){
								$.each(code, function(id, text){
									libMap[col][id] = text;
								});
							});
						});
					}
				});
			}
		}

		function searchForm() {
			//搜索表单应用ligerui样式
			$("#search").ligerForm({
				fields : [ {
					display : '数据日期',
					name : 'sysDataDate',
					newline : true,
					type : 'date',
					format : 'yyyy-MM-dd',
					attr : {
						field : 'sysDataDate',
						op : "="
					}
				},
				{
					display : '补录机构',
					name : 'orgNoID',
					newline : false,
					type : 'select',
					attr : {
						field : 'sysOperOrg',
						op : "="
					},
					options : {
						onBeforeOpen: function() {
							BIONE.commonOpenIconDialog('选择机构', 'orgComBoBox',
									'${ctx}/bione/admin/orgtree/asyncOrgTree', 'orgNoID');
						},
						hideOnLoseFocus : true,
						slide : false,
						selectBoxHeight : 1,
						selectBoxWidth : 1,
						resize : false,
						switchPageSizeApplyComboBox : false
					}
				},{
					display : '数据状态',
					name : "sysDataState",
					newline : false,
					type : "select",
					options : {
						valueFieldID : 'state',
						data : [ {
							text : '已保存',
							id : 'save'
						}, {
							text : '已提交',
							id : 'submit'
						}, {
							text : '驳回',
							id : 'reject'
						}, {
							text : '审核通过',
							id : 'success'
						}, {
							text : '已回退',
							id : 'refuse'
						} ]
					},
					cssClass : "field",
					attr : {
						field : 'sysDataState',
						op : "="
					}
				}
				]
			});
		}

		function initGrid() {
			if (templateId == ""){
				return;
			}

			$.extend(app, {
				gridRender : function(col, lib) {
					return function(row) {
						var val = libMap[lib][row[col]] ||　row[col];
						if(row.NODERM || row.COMMENTS)
						{
							return  "<font color=\"#ff0000\">"+val+"</font>";
						}
						else {
							return "<font scolor=\"#ffffff\">"+val+"</font>";
						}
					}
				},
				setButtonLimit : function() {
					$.ajax({
						url : "${ctx}/rpt/input/temple/findTempleInfo?templeId="+templateId,
						success : function(data) {
							butCheck = data;
						}
					});
				},
				colRender : function(row, rowNum, colData){
					if(colData==null) colData = ""
					if(row.NODERM || row.COMMENTS)
					{
						return  "<font color=\"#ff0000\">"+colData+"</font>";
					}
					else {
						return "<font scolor=\"#ffffff\">"+colData+"</font>";
					}
				}
			});
			var columns = [];
			$.ajax({
				async : false,
				url : '${ctx}/rpt/input/data/getColumnGridListByQuery/' + templateId,
				success : function(data) {
					gridColTranslate = {};
					var width = 180; formName = []; formType = []; formLib = []; formSelectable = []; formWritable = [],defaultValue=[];
					for ( var i = 0; i < data.length; i++) {
						//添加查询窗口构造
						//设置换行
						var newLine = false;
						//设置窗口属性,默认为text
						var type = "text" ;
						var typeArr = ["text","date","hidden","select","number"];//设置范围，防止误填信息
						if(data[i].searchType != null && typeArr.indexOf(data[i].searchType) > -1){
							type = data[i].searchType;
						}
						if("hidden" == data[i].searchType){
							//隐藏字段，不画查询框
						}else if("date" == data[i].searchType){
							//日期类型查询条件处理
							searchFields.push({
								display : data[i].fieldCnName,
								name : data[i].fieldEnName,
								newline : newLine,
								labelWidth : 120,
								labelAlign : 'right',
								width:140,
								type : type,
								cssClass : "field",
								options : {
									format : 'yyyyMMdd',
								},
								attr : {
									op : "=",
									field : data[i].fieldEnName
								}
							});
						}else{
							//其他类型查询条件处理,主要为text
							searchFields.push({
								display : data[i].fieldCnName,
								name : data[i].fieldEnName,
								newline : newLine,
								labelWidth : 120,
								labelAlign : 'right',
								width:140,
								type : type,
								cssClass : "field",
								attr : {
									op : "LIKE",
									field : data[i].fieldEnName
								}
							});
						}
						if (data[i].fieldCnName) {
							var displayText = data[i].fieldCnName;
							gridColTranslate[data[i].fieldEnName] = data[i].fieldCnName;
							if (data[i]&&data[i].dictId != null&&data[i].dictId != "") {
								// 设置字典的值
								columns.push({
									display : displayText,
									name : data[i].fieldEnName,
									minWidth : width,
									isSort : true,
									render : app.gridRender(data[i].fieldEnName, data[i].dictId)
								});
							} else {
								if(data[i].decimalLength > 0 && data[i].fieldType == "NUMBER"){
									columns.push({
										display : data[i].fieldCnName, name : data[i].fieldEnName,
										minWidth : width,
										isSort : true,
										render : function(rec,row,val){
											val = parseFloat(val);
											colData = isNaN(val)? "" : val;
											if(colData==null) colData = ""
											if(row.NODERM || row.COMMENTS){
												return  "<font color=\"#ff0000\">"+colData+"</font>";
											}else{
												return "<font scolor=\"#ffffff\">"+colData+"</font>";
											}
										}
									});
								}else{
									columns.push({
										display : data[i].fieldCnName, name : data[i].fieldEnName,
										minWidth : width,
										isSort : true,
										render : app.colRender
									});
								}
							}
							formDetail.push(data[i].fieldDetail);
							formName.push(data[i].fieldCnName);
							formType.push(data[i].fieldEnName);
						} else {
							if (data[i]&&data[i].dictId != null&&data[i].dictId != "") {
								// 设置字典的值
								columns.push({
									display : data[i].fieldEnName, name : data[i].fieldEnName,
									minWidth : width,
									isSort : true,
									render : app.gridRender(data[i].fieldEnName, data[i].dictId)
								});
							} else {
								columns.push({
									display : data[i].fieldEnName, name : data[i].fieldEnName,

									minWidth : width,
									isSort : true,
									render : app.colRender
								});
							}
							formDetail.push(data[i].fieldDetail);
							formName.push(data[i].fieldEnName);
							formType.push(data[i].fieldEnName);
						}
						formLib.push(data[i].dictId);
						formSelectable.push(data[i].allowSift);
						formWritable.push(data[i].allowInput);
						defaultValue.push(data[i].defaultValue);
						formNullable.push(data[i].allowNull);
						formDataType.push(data[i].fieldType);
					}
					columns.push({
						display : "",
						name : "comments",
						hide : true,
						width : 0.1,
					});
					columns.push({
						display : "",
						name : "upperNodeRm",
						hide : true,
						width : 0.1
					});
					columns.push({
						name : "data_type", hide : "1",width : 0.1
					});
					app.setButtonLimit(templateId);
				}
			});
			grid = manager = $("#maingrid").ligerGrid({
				columns : columns,
				height : '99%',
				checkbox : false,
				isScroll : true,
				resizable : true,
				enabledEdit : false,
				clickToEdit : true,
				rownumbers : true,
				dataAction : 'server', // 从后台获取数据
				usePager : true, // 服务器分页
				alternatingRow : true, // 附加奇偶行效果行
				colDraggable : true,
				method : 'post',
				url : "${ctx}/rpt/input/table/queryInputTable?templateId="+templateId,
				pageSize: 50,
				pageParmName : 'page',
				toolbar : {},//工具栏
				pageSizeOptions : [10,20,50,100],
				pagesizeParmName : 'pagesize',
				onDblClickRow : function(data, rowindex, rowobj) {
					if (butCheck.updatable == '0') {
						BIONE.tip("该模板无修改操作!");
						return;
					} else if (butLock == '1') {
						BIONE.tip("数据已提交，无需操作!");
						return;
					} else {
						grid.reRender();
						var paramStr = "";
						for (i = 0; i < formType.length; i++) {
							if (data[formType[i]] == null) {
								paramStr = paramStr + ","
							} else {
								paramStr = paramStr + data[formType[i]] + ","
							}
						}
						window.updateRow = data;
						var win = window.dataRules;
						if (win != null) {
							win.setParamInfo(paramStr, rowindex);
						} else {
							openWindow(paramStr, rowindex);
						}
					}
				}
			});
			BIONE.loadToolbar(grid, items, function() {});
		}

		function initButton() {
			items = [
				{
					text : '修改',
					click : data_update,
					icon : 'fa-pencil-square-o'
				},
				{
					text : '导出',
					click : data_export,
					icon : 'fa-download'
				},
                {
                    text : '导入',
                    click : data_import,
                    icon : 'fa-upload'
                }
			];
		}

		//导出
		function data_export(){
			var rule = BIONE.bulidFilterGroup($("#search"));
			var url = "${ctx}/rpt/input/table/expInputData?templateId="+templateId+"&condition=" + JSON2.stringify(rule);
			downdload.attr('src', url);
		}

		//导入
        function data_import(){
            BIONE.commonOpenDialog('数据导入', 'inputTaskInfo', 562, 334, "${ctx}/rpt/input/table/uploadInputData?templeId=" + templateId
                    ,null , function(){
				grid.reload();
            });
        }

		// 修改
		function data_update() {
			achieveIds();
			var paramStr = "";
			if (ids.length == 1) {
				for (i = 0; i < formType.length; i++) {
					if (ids[0][formType[i]] == null) {
						paramStr = paramStr + ","
					} else {
						paramStr = paramStr + ids[0][formType[i]] + ","
					}
				}
				window.updateRow = ids[0];
				BIONE.commonOpenDialog(butCheck.templeName, "dataRules", "850", "400", "${ctx}/rpt/input/table/inputQueryEditPage?paramStr="
						+ encodeURIComponent(encodeURIComponent(paramStr)) + "&templateId=" + templateId);
			} else if (ids.length > 1) {
				BIONE.tip('只能选择一行进行修改');
			} else {
				BIONE.tip('请选择一行进行修改');
			}
		}

		// 获取选中的行
		function achieveIds() {
			ids =  grid.getSelectedRows();
		}

		/******日志处理相关    开始 cl**************/
		var logList=[];
		function fieldModifyLog(fieldList){
			for(var i = 0 ;i <fieldList.length;i++){
				var content = "";
				var fieldInfos = fieldList[i].fieldInfo;
				var fieldId = fieldList[i].fieldId;
				for(var j = 0 ;j<fieldInfos.length;j++){
					if(fieldInfos[j].oldValue &&fieldInfos[j].oldValue&&fieldInfos[j].oldValue != fieldInfos[j].value )
						content = content +"字段名:"+fieldInfos[j].fieldName+",值:"+fieldInfos[j].oldValue+" 更新后为值:"+fieldInfos[j].value+";   ";
				}
				var log ={
					type : "modify",
					id : fieldId,
					content : content
				};
				logList.push(log);
			}
		}
	</script>
</head>
<body>
	<div id="template.right.down">
		<div id="aaa">
			<div id="maingrid" style="margin-top: 60px;"></div>
		</div>
	</div>
</body>
</html>