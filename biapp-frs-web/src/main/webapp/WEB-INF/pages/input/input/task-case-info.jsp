<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template8_2_1.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/udip/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript">

	function formatMoney(number, places, symbol, thousand, decimal) {
	    number = number || 0;
	    places = !isNaN(places = Math.abs(places)) ? places : 2;
	    symbol = symbol !== undefined ? symbol : "";
	    thousand = thousand || ",";
	    decimal = decimal || ".";
	    var negative = number < 0 ? "-" : "",
	        i = parseInt(number = Math.abs(+number || 0).toFixed(places), 10) + "",
	        j = (j = i.length) > 3 ? j % 3 : 0;
	    return symbol + negative + (j ? i.substr(0, j) + thousand : "") + i.substr(j).replace(/(\d{3})(?=\d)/g, "$1" + thousand) + (places ? decimal + Math.abs(number - i).toFixed(places).slice(2) : "");
	}
	var autoLoadRows = [];

	var autoLoad = '${autoLoad}';
	var selectOrg = '${orgNo}';
	var templateId = '${templateId}';
	var taskId = '${taskId}';
	var taskInstanceId = '${taskInstanceId}';
	var taskNodeInstanceId = '${taskNodeInstanceId}';
	var inputType = '${inputType}';
	var dataDate = '${dataDate}';
	var keyColumn = '${keyColumn}';
	var taskIndexType = window.parent.parent.taskIndexType;
	var taskType = parent.taskType;
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var grid, manager, url, ids = [], dialog, buttons = [];
	window.parent.frameControl = window;
	var butCheck = { updatable : '', addable : '', deletable : '', templeName : '', authable : '' };
	var jiaoyan = "0", butLock = "0";
	var rowSize = 0;
	var dataZidian2;
	var paramStrDel = "", paramStrAdd="", paramStrUdp = "";
	var _dialog ;
	var reButNode = '';
	var gridColTranslate = {};
	var Rows = [];
	var uri = '${ctx}/rpt/input/taskcase/getTaskCaseInfoList.json?onlineTask="online"&templeId=' + templateId
			+ '&taskInstanceId=' +???taskInstanceId
			+ '&taskNodeInstanceId=' +taskNodeInstanceId
			+ '&sqlStr=' + encodeURIComponent(encodeURIComponent("${sqlStr}")) + '&caseId=' + taskInstanceId
			+ '&searchTerms=' + encodeURIComponent(encodeURIComponent("${searchTerms}"));
	var selection = [ { id : "", text : "?????????" } ];
	//???????????? ?????? ?????? ??????????????????
	var delArr = [],  addArr = [], udpArr = [];
	var formName = [], formType = [], formLib = [], formSelectable = [], formNullable = [], formDataType = [], formDetail = [], formWritable = [],defaultValue=[];
	var libMap = {};
	var app = {};
	var searchFields = [];//??????????????????
	function getTaskInstanceId(){
		return taskInstanceId;
	}
	function getTaskNodeInstanceId(){
		return taskNodeInstanceId;
	}
	function getTemplateId(){
		return templateId;
	}
	$(function() {
		window.parent.frameControl = window;
		initLibMap();
		initGrid();
		initGridToolbar();
		initSearchForm();
		initSearchData();
		UDIP.heart("${ctx}/datainput/heart/on");//?????????
	});

	var canSave = false; //??????????????????
	var canSubmit = false; //??????????????????
	var canRebut = false; //??????????????????

	function initLibMap(){
		$.ajax({
			async : false,
			url : '${ctx}/rpt/input/library/libMapbyTempleId2.json?d='+new Date().getTime(),
			dataType : 'json',
			type : "get",
			data : {
				"templeId" : templateId,
				"caseId" : taskInstanceId
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

	function initSearchData(){
		for ( var i = 0; i < formType.length; i++) {
			if (formLib[i]) {
				// $("#" + formType[i]).ligerGetComboBoxManager().setData(window.libMa2[formLib[i]]);
				if (window.libMa2[formLib[i]] && window.libMa2[formLib[i]][0] && window.libMa2[formLib[i]][0]['upid'] == null) {
					$("#" + formType[i]).ligerGetComboBoxManager().setData(window.libMa2[formLib[i]]);
				} else {
					if ("${orgColumn}" == formType[i] && selectOrg) {
						var libText = getLibText(window.libMa2[formLib[i]], selectOrg);
						$("#search input[name='" + formType[i] + "']").val(libText);
						$("#search input[name='" + formName[i] + "']").val(selectOrg);
						$("#" + formType[i]).ligerComboBox({
							onBeforeOpen : function(newvalue) {
								return false;
							}
						});
					} else {
						$("#" + formType[i]).ligerComboBox({
							onBeforeOpen : set(i)
						});
					}
				}
			}
		}
	}

	function set(i) {
		return function() {
			openSelectNewDilog(formLib[i], formType[i], formName[i]);
			return false;
		}
	}

	function openSelectNewDilog(lib, formType, formName) {
		BIONE.commonOpenDialog(formName, 'libraryAddWin', 500, 400, "${ctx}/rpt/input/library/getFormTypeName?orgId=&libId="
				+ lib + "&templeId=" + templateId + "&caseId=" + taskInstanceId + "&formType=" + encodeURIComponent(encodeURIComponent(formType))
				+ "&formName=" + encodeURIComponent(encodeURIComponent(formName)));
	}

	function setDataZidina(name, id, formName, formType) {
		// $("#mainform input[name='" + formType + "']").val(name);
		// $("#mainform [name='" + formName + "']").val(id);
		$('#' + formType).ligerGetComboBoxManager()._changeValue(id, name);
	}

	function initGridToolbar() {
		if (taskIndexType) {
			if (taskIndexType == "01") {	//????????????
				if (taskType == 1) {	//?????????
					canSave = true;
					canSubmit = true;
				}
			} else if (taskIndexType != "01") {	//????????????
				if (taskType == 1) {
					canSubmit = true;
					canRebut = true;
				}
			}
		}
		var items = [];
		if (canSave) {
			items.push(
	       		{ text : '??????', click : form_add1, icon : 'add' }, 
	       		{ text : '??????', click : form_updata1, icon : 'modify' }, 
	       		{ text : '??????', click : form_delete1, icon : 'delete' }, 
	       		{ text : '??????', click : form_clearall, icon : 'clear' }, 
	       		{ text : '??????', menu : { width: 120, items: [
           				{ text: '????????????', click: form_auto1, icon : 'switch' },
           	            { text: '????????????', click: form_validate, icon : 'failed' }
           			]
           		}, icon : 'menu' }
           	);
		}
		if (canSave) {
			items.push(
				{ text : '??????', click : form_reflash, icon : 'refresh' }, 
		   		{ text : '??????', click : form_rule, icon : 'setting1' }
			);
		}
		//$("#topmenu").ligerToolBar({ items : items });
		//????????????BIONE?????????
		BIONE.loadToolbar(grid, items, function() { });
	}
	function initGrid() {
		$.extend(app, {
			gridRender : function(col, lib) {
				return function(row) {
					var val = libMap[lib][row[col]] ||???row[col];
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
					url : "${ctx}/rpt/input/temple/findTempleInfo?templeId=" + templateId + "&d=" + new Date().getTime(),
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
		if (templateId) {
			var templeId = templateId;
			$.ajax({
				async : false,
				url : '${ctx}/rpt/input/data/getColumnGridList/' + templateId+"?d="+new Date().getTime(),
				success : function(data) {
					gridColTranslate = {};
					var width = 180; formName = []; formType = []; formLib = []; formSelectable = []; formWritable = [],defaultValue=[];
					for ( var i = 0; i < data.length; i++) {
						//????????????????????????
						//????????????
						var newLine = false;
						/* if (i % 3 == 0) {
							newLine = true;
						} */
						//??????????????????,?????????text
						var type = "text" ;
						var typeArr = ["text","date","hidden","select","number"];//?????????????????????????????????
						if(data[i].searchType != null && typeArr.indexOf(data[i].searchType) > -1){
							type = data[i].searchType;
						}
						if("hidden" == data[i].searchType){
							//??????????????????????????????
						}else if("date" == data[i].searchType){
							//??????????????????????????????
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
						}else if( data[i].dictId!=null){
							searchFields.push({
								display : data[i].fieldCnName,
								name : data[i].fieldEnName,
								newline : newLine,
								labelWidth : 120,
								labelAlign : 'right',
								width:140,
								type : 'select',
								cssClass : "field",
								comboboxName : data[i].fieldEnName,
								options : {
									openwin : (libMa2[data[i].dictId] && libMa2[data[i].dictId][0] && libMa2[data[i].dictId][0]['upid'] == null) ? false : true,
									valueFieldID : data[i].fieldEnName,
								},
								attr : {
									op : "=",
									field : data[i].fieldEnName
								}
							});
						}else{
							//??????????????????????????????,?????????text
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
// 							var displayText = data[i].fieldCnName+"("+data[i].fieldEnName+")";
							var displayText = data[i].fieldCnName;
							gridColTranslate[data[i].fieldEnName] = data[i].fieldCnName;
							//width = data[i].fieldCnName.length * 30;
							if (data[i]&&data[i].dictId != null&&data[i].dictId != "") {
								// ??????????????????
								columns.push({
									//display : data[i].fieldCnName, 
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
// 											return formatMoney(val.toFixed(2));
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
								// ??????????????????
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
						name : "nodeRm", 
						width : 50, 
						isSort : false,
						render:function(rowData, rowNum, colData){
							return "<a href='##'   class='oper'  onclick=\"onComment('"+ rowNum+"','"+taskIndexType+"')\" >??????</a></div>"
						}
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
		}
		grid = manager = $("#maingrid").ligerGrid({
			columns : columns,
			height : '99%',
			checkbox : canSave||canSubmit||canRebut?true:false,
			isScroll : true,
			resizable : true,
			enabledEdit : false,
			clickToEdit : true,
			rownumbers : true,
			dataAction : 'server', // ?????????????????????
			usePager : true, // ???????????????
			alternatingRow : true, // ????????????????????????
			colDraggable : true,
			method : 'post',
			url : uri,
			pageSize: 50, 
			pageParmName : 'page',
			toolbar : {},//?????????
			pageSizeOptions : [10,20,50,100],
			pagesizeParmName : 'pagesize',
			onDblClickRow : function(data, rowindex, rowobj) {
				if (butCheck.updatable == '0') {
					BIONE.tip("????????????????????????!");
					return;
				} else if (butLock == '1') {
					BIONE.tip("??????????????????????????????!");
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
	}
	//???????????????
	function initSearchForm() {
		$("#search").ligerForm({
			fields : searchFields
		});
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
	}
	
	function zOnCheckAllRow(checked,row){
		if(!canSave){
			var rows = grid.rows;
			for(var i = 0 ;i <rows.length;i++){
				if(checked){
					rows[i].ERROR_MARK="01";
				}else{
					rows[i].ERROR_MARK = "00";
				}
				var rowObj = grid.getRowObj(rows[i].__id);
				this.updateRow(rowObj, rows[i]);
			}
		}
	}
	
	function onCheckRow(checked, rowData, rowId, row){
		if(checked){
			rowData.ERROR_MARK = "01";
		}else{
			rowData.ERROR_MARK = "00";
		}
		grid.updateRow(row, rowData);
	}

	function onComment(rowNum,taskIndexType) {
		var row = grid.getRow(rowNum);
		var content = row.COMMENTS;
		var id = row.DATAINPUT_ID;
		if(!content||content=="undefined")
			content = "";
		
		var nodeRm = row.NODERM;
		if(!nodeRm||nodeRm=="undefined")
			nodeRm = "";

		var view = canSave||canSubmit||canRebut?false:true; // ????????????

		var rm = content;
		BIONE.commonOpenDialog('??????', 'commentBox', '600', (view || content == '' )? '230':'365',
				'${ctx}/rpt/input/taskcase/comment?templateId='+templateId+'&uniqueKey='+id+'&taskIndexType='+taskIndexType+'&content='+ encodeURI(encodeURI(rm))+'&view='+view+'&date='+new Date().getTime(), null
						,function (data){
					if(data || data==""){
						row.COMMENTS = data;
						// if(!row.data_type||row.data_type==null)
						// 	row.data_type = "3";
						grid.updateRow(row,row);
						
						onCheckRow(data.trim() != "" || row.COMMENTS,row,null,row);
					}
				});
	}
	//??????????????????
	function form_add1() {
		if (butCheck.allowAdd == '0') {
			BIONE.tip("????????????????????????!");
			return;
		} else if (butLock == '1') {
			BIONE.tip("??????????????????????????????!");
			return;
		} else {
			var paramStr = "";
			openWindow(paramStr, "");
		}
	}
	//??????????????????
	function form_updata1() {
		if (butCheck.allowUpdate == '0') {
			BIONE.tip("????????????????????????!");
			return;
		} else if (butLock == '1') {
			BIONE.tip("??????????????????????????????!");
			return;
		} else {
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
				openWindow(paramStr, ids[0]['__index']);
			} else if (ids.length > 1) {
				BIONE.tip("??????????????????????????????!");
			} else {
				BIONE.tip("?????????????????????????????????!");
				return;
			}
		}
	}
	
	// ??????????????????
	function achieveIds() {
		ids =  grid.getSelectedRows();
	}
	//??????
	function form_clearall(){
		if (butCheck.allowDelete=='0') {
			BIONE.tip("????????????????????????!");
			return;
		} else if (butLock=='1') {
			BIONE.tip("??????????????????????????????!");
			return;
		} else {
			if(grid.rows.length==0)
				return ;
			ids = grid.rows;
			var manager = $("#maingrid").ligerGetGridManager(); 
			var fieldList = [];
			if (ids.length > 0) {
				for (j = 0; j < ids.length; j++){
					if (ids[j].data_type == null) {
					var data1 = [];
					data1.push(ids[j].DATAINPUT_ID);
					delArr.push(data1.join('#@@ '));
					}
					/************??????????????????  cl**************/
					var delInfo = {
						id : ids[j].DATAINPUT_ID,
						fieldInfo : []
					};
					for(var x = 0 ;x <formType.length;x++){
						delInfo.fieldInfo.push({
							fieldName: formType[x],
							value : ids[j][formType[x]]
						});
					}
					fieldList.push(delInfo);
					fieldDeleteLog(fieldList);
				}
				/**********************************/
				manager.deleteRange(ids); 
			} else {
				BIONE.tip("?????????????????????????????????!");
				return;
			}
		}
	}
	
	//????????????
	function form_delete1(){
		if (butCheck.allowDelete=='0') {
			BIONE.tip("????????????????????????!");
			return;
		} else if (butLock=='1') {
			BIONE.tip("??????????????????????????????!");
			return;
		} else {
			achieveIds();
			var manager = $("#maingrid").ligerGetGridManager(); 
			var fieldList = [];
			if (ids.length > 0) {
				for (j = 0; j < ids.length; j++){
					//if (ids[j].data_type == null) {
						var data1 = [];
						data1.push(ids[j].DATAINPUT_ID);
						delArr.push(data1.join('#@@ '));
					//}
					/************??????????????????  cl**************/
					var delInfo = {
						id : ids[j].DATAINPUT_ID,
						fieldInfo : []
					};
					for(var x = 0 ;x <formType.length;x++){
						delInfo.fieldInfo.push({
							fieldName: formType[x],
							value : ids[j][formType[x]]
						});
					}
					fieldList.push(delInfo);
					fieldDeleteLog(fieldList);
				}
				/**********************************/
				manager.deleteRange(ids); 
			} else {
				BIONE.tip("?????????????????????????????????!");
				return;
			}
		}
		
	}
	//????????????
	function openWindow(paramStr, rowindex) {
		achieveIds();
		if (rowindex == "") {
			_dialog = BIONE.commonOpenDialog(butCheck.templeName, "dataRules", "850", "400", "${ctx}/rpt/input/inputtask/taskTempleInput?paramStr="
					+ encodeURIComponent(encodeURIComponent(paramStr)) + "&templeId=" + templateId + "&caseId=" + taskInstanceId+"&d="+new Date().getTime());
		} else {
			_dialog = BIONE.commonOpenDialog(butCheck.templeName, "dataRules", "850", "400", "${ctx}/rpt/input/inputtask/taskTempleInput?paramStr="
					+ encodeURIComponent(encodeURIComponent(paramStr)) + "&templeId=" + templateId + "&caseId=" + taskInstanceId + "&rowindex=" + rowindex+"&d="+new Date().getTime());
		}
	}
	//??????????????? CRUD ?????????
	//udpArr?????????????????? ??? ???????????????????????????????????????data_type=undefined 20191026
	function CRUD_DATA(){
		var manager = $("#maingrid").ligerGetGridManager();
		var data = manager.getData();
		if (data.length == 0) {
			return;
		}
		for (i = 0; i < data.length; i++) {
			if (data[i].data_type == "4") {
				var data1 = [];
				for (j = 0; j < formType.length; j++) {
					data1.push(data[i][formType[j]]);
				}
				data1.push((i+1));
				data1.push(getRm(data[i].COMMENTS,data[i].NODERM));
				data1.push(data[i].DATAINPUT_ID);
				addArr.push(data1.join('#@@ '));
			}else {  //if (data[i].data_type == "3") ????????????data_type == "3" 20190618
				var data1 = [];
				for (j = 0; j < formType.length; j++) {
					data1.push(data[i][formType[j]]);
				}
				data1.push((i+1));
				data1.push(getRm(data[i].COMMENTS,data[i].NODERM));
				data1.push(data[i].DATAINPUT_ID);
				udpArr.push(data1.join('#@@ '));
			}
		}
	}
	/*20191026
		??????????????????????????????????????? ??? ?????????????????????data_type=4??????????????????3???????????????,0??????????????????????????????????????????data_type=undefined???
		???CRUD_DATA()???????????????udpArr?????????????????????????????????????????????
	*/
	function addAndUdp_data(){
		var manager = $("#maingrid").ligerGetGridManager();
		var data = manager.getData();
		if (data.length == 0) {
			return;
		}
		for (i = 0; i < data.length; i++) {
			if (data[i].data_type == "4" || data[i].data_type == "0") {
				var data1 = [];
				for (j = 0; j < formType.length; j++) {
					data1.push(data[i][formType[j]]);
				}
				data1.push((i+1));
				data1.push(getRm(data[i].COMMENTS,data[i].NODERM));
				data1.push(data[i].DATAINPUT_ID);
				addArr.push(data1.join('#@@ '));
			}else if (data[i].data_type == "3") {
				var data1 = [];
				for (j = 0; j < formType.length; j++) {
					data1.push(data[i][formType[j]]);
				}
				data1.push((i+1));
				data1.push(getRm(data[i].COMMENTS,data[i].NODERM));
				data1.push(data[i].DATAINPUT_ID);
				udpArr.push(data1.join('#@@ '));
			}
		}
	}
	
	//????????????
	function form_auto1(submitMark){
		var flag = false;
		var manager = $("#maingrid").ligerGetGridManager();
		var data = manager.getData();
		if (data.length == 0) {
			return;
		}
		if(!validatekeyList()){
			return ;
		}
		var param = "";
		udpArr=[];
		addArr=[];
		if(addArr.length == 0 && udpArr.length==0){
			CRUD_DATA();
		}
		
		for (j = 0; j < formType.length; j++) {
			param = param + formType[j] + ","
		}
		param = param + 'SYS_ORDER_NO' + ',';
		param = param + 'COMMENTS' + ',';
		param = param + 'DATAINPUT_ID' + ',';
		paramStrDel = delArr.join('@;@');
		paramStrAdd = addArr.join('@;@');
		paramStrUdp = udpArr.join('@;@');
		var flag = false;
		var info = {
			"templeId" : templateId, 
			"taskInstanceId" : taskInstanceId,
			"taskId" : taskId,
			"param" : param, 
			"paramStrAdd" : encodeURI(encodeURI(paramStrAdd)), 
			"paramStrDel" : paramStrDel, 
			"paramStrUdp" : encodeURI(encodeURI(paramStrUdp)), 
			"caseId" : taskInstanceId,
			"logList": JSON2.stringify(logList)
		};
		$.ajax({
			data : info,
			type : "post",
			dataType : 'json',
			url : "${ctx}/rpt/input/taskcase/validateWeiHaiTaskcaseTempleInfo.json",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("?????????????????????...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if (data.length == 0) {
					if(submitMark&&submitMark=="submit"){
						//submitNow();
					}else{
						BIONE.showSuccess("??????????????????!");
					}
				} else {
					if(data[0].indexOf('Exception') != -1){
						BIONE.tip("??????????????????!????????????????????????");
						return ;
					}
					//[ORA-00942: ?????????????????????
					if(data[0].indexOf('ORA-') != -1){
						BIONE.tip("?????????????????????!");
						return ;
					}	
					paramStrUdp = ""; paramStrAdd = "";
					BIONE.commonOpenDialog('????????????', "chackDataFile", "630", "440", '${ctx}/rpt/input/taskcase/taskCaseValidateLog?templeId=' + templateId + '&caseId=' + taskInstanceId);
				}
			},
			error : function(result, b) {
				BIONE.tip("??????????????????!????????????????????????");
				return;
			}
		});
	}
	
	//??????????????????????????????????????????????????????????????? 20191026
	function validate_submit(){
		var flag = false;
		var manager = $("#maingrid").ligerGetGridManager();
		var data = manager.getData();
		/* if (data.length == 0) {
			return;
		} */
		if(!validatekeyList()){
			return ;
		}
		var param = "";
		udpArr=[];
		addArr=[];
		if(addArr.length == 0 && udpArr.length==0){
			CRUD_DATA();
		}
		
		for (j = 0; j < formType.length; j++) {
			param = param + formType[j] + ","
		}
		param = param + 'SYS_ORDER_NO' + ',';
		param = param + 'COMMENTS' + ',';
		param = param + 'DATAINPUT_ID' + ',';
		paramStrDel = delArr.join('@;@');
		paramStrAdd = addArr.join('@;@');
		paramStrUdp = udpArr.join('@;@');
		var flag = false;
		var info = {
			"templeId" : templateId, 
			"taskInstanceId" : taskInstanceId,
			"taskId" : taskId,
			"param" : param, 
			"paramStrAdd" : encodeURI(encodeURI(paramStrAdd)), 
			"paramStrDel" : paramStrDel, 
			"paramStrUdp" : encodeURI(encodeURI(paramStrUdp)), 
			"caseId" : taskInstanceId,
			"logList": JSON2.stringify(logList)
		};
		$.ajax({
			data : info,
			type : "post",
			dataType : 'json',
			url : "${ctx}/rpt/input/taskcase/validateWeiHaiTaskcaseTempleInfo.json",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("?????????????????????...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if (data.length == 0) {
					BIONE.showSuccess("??????????????????!");
					//?????????????????????
					form_doSubmit();
				} else {
					if(data[0].indexOf('Exception') != -1){
						BIONE.tip("??????????????????!????????????????????????");
						return ;
					}
					//[ORA-00942: ?????????????????????
					if(data[0].indexOf('ORA-') != -1){
						BIONE.tip("?????????????????????!");
						return ;
					}	
					paramStrUdp = ""; paramStrAdd = "";
					BIONE.commonOpenDialog('????????????', "chackDataFile", "630", "440", '${ctx}/rpt/input/taskcase/taskCaseValidateLog?templeId=' + templateId + '&caseId=' + taskInstanceId);
				}
			},
			error : function(result, b) {
				BIONE.tip("??????????????????!????????????????????????");
				return;
			}
		});
	}
	
	function getKeyData(cellName,rowData){
		return rowData[cellName];
	}
	
	function keyContains(keyArrays,key){
		for(var i = 0 ;i < keyArrays.length;i++)
			if(keyArrays[i]==key){
				return true;
			}
		return false;
	}

	function validatekeyList(){
		var rec = [];
		if(keyColumn&&keyColumn!=null&&keyColumn!=""){
			var keyArrays = [];
			var rows = grid.rows;
			for(var i=0;i<rows.length;i++ ){
				var row = rows[i];
				var keyStr = "";
				var keys = keyColumn.split(";");
				var keyNames = "";
				for(var x = 0 ;x <keys.length;x++){
					keyNames = keyNames + gridColTranslate[keys[x]] + ",";
					keyStr = keyStr + getKeyData(keys[x],row)+"||";
				}
				if(keyContains(keyArrays,keyStr)){
					var record = {
						errorPosi : "???"+(i+1)+"???",
						errorMsg : "???"+keyNames+"????????????????????????"
					};
					rec.push(record);
					continue;
				}
				keyArrays.push(keyStr);
			}
		}
		if(rec.length != 0){
			alertError(rec);
			return false;
		}
		return true;
		
	}
	function alertError(rows){
		window.Rows = rows;
		BIONE.commonOpenDialog("????????????", "errorFrame", "700",
				"500", '${ctx}/rpt/input/errorFrame');
	}
	//??????
	function validateRule(){
		var info = {
				"templeId" : templateId, 
				"taskInstanceId" : taskInstanceId
		};
		$.ajax({
			data : info,
			type : "post",
			dataType : 'json',
			url : "${ctx}/rpt/input/taskcase/getValidateLog",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("???????????????...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if (data.length == 0) {
					
				}
					
			}
		});
	}
	//task-oper-content ????????????????????????  ???????????????????????? ?????? ????????????
	// 01 ?????? 02 ??????  03 ??????
	function save(operType,impFunc) {
		//key????????????
		if(!validatekeyList()){
			return ;
		}
		if (operType == "01") {//??????
			udpArr=[];
			addArr=[];
			if(addArr.length == 0 && udpArr.length==0){
				CRUD_DATA();
			}
			if(typeof impFunc != "function"){
				form_save1(null,impFunc);
			}else if(typeof impFunc == "function"){
				//????????????????????????????????????
				if(addArr.length>0){
					$.ligerDialog.confirm("??????????????????????????????????????????", function(yes) {
						if (yes) {
							//?????? ????????????  ??????????????????????????????????????????
							// ???????????????????????? impFunc??????
							form_save1(null,impFunc);
						}else{
							impFunc();
						}
					});
				}else{
					impFunc();
				}
			}
		} else if (operType == "02") {
			/*???????????? lcy 20191020
			if(addArr.length == 0 && udpArr.length==0){
				CRUD_DATA();
			}
			*/
			
			/*  20191026
				???????????????????????????addArr???udpArr???????????????????????????????????????addArr???udpArr???
				??????addAndUdp_data()?????????????????????????????????????????????????????????
			*/
			addArr = [];
			udpArr = [];
			addAndUdp_data();
			
			/* console.log("addArr="+addArr);
			console.log("udpArr="+udpArr);
			console.log("delArr="+delArr); */
			//???????????????????????????
			if(addArr.length>0 || udpArr.length>0 || delArr.length>0){
				
				//????????????????????????????????????????????????????????????????????????????????????????????? 20191026
				/* $.ligerDialog.confirm("?????????????????????????????????????????????", function(yes) {
					if (yes) {
						//?????? ????????????  ??????????????????????????????????????????
						// ???????????????????????? form_doSubmit????????????
						form_save1('submit');
					}
				}); */
				
				BIONE.tip("????????????????????????????????????????????????");
				return;
			}else{
				/*20191026
					01 ???????????????????????? ???????????? 
					02 ????????????????????????????????????????????????????????????????????????????????????
				*/
				if(taskIndexType == '01'){
					validate_submit();
				}else if(taskIndexType == '02'){
					form_doSubmit();
				}
			}
		} else if (operType == "03") {
			$.ligerDialog.confirm("????????????????????????", function(yes) {
				if (yes) {
					BIONE.commonOpenDialog("??????????????????", "tskNodesSel",350,180,
					"${ctx}/rpt/input/taskoper/taskNodes/" + taskInstanceId);
				}
			});
		}
	}
	
	//????????????
	function form_doSubmit(){
		window.parent.doSubmit(templateId,taskInstanceId,taskNodeInstanceId);
		window.parent.closeDialog("02");
	}
	
	function getRm(content,nodeRm){
		var linkStr = "";

		if(content)
			content = content.trim();
		if(nodeRm)
			nodeRm = nodeRm.trim();
		
		if(!content||content=="undefined"){
			content = " ";
		}else{
			linkStr += "@n" ;
		}
		
		if(!nodeRm||nodeRm=="undefined"){
			nodeRm = "";
			linkStr = "";
		}else{
			if(top.window.clientEnv) {
				linkStr += top.window.clientEnv["userName"]
						+ " "
						+ new Date().toLocaleString()
						+ "???"
			}
		}
		
		return content + linkStr + nodeRm;
	}
	
	function saveErrorMark(){
		var checkedRows = grid.getCheckedRows();
		var errorIds = "";
		var comments = "";
		if(checkedRows.length==0){
// 			return;
// 			window.parent.doRebut(reButNode);
		}else{
			for(var i = 0 ;i < checkedRows.length;i++){
				errorIds = errorIds+checkedRows[i].DATAINPUT_ID+";";
			}
		}
		var rows = grid.rows;
		for(var i = 0 ;i <rows.length;i++){
			comments = comments + rows[i].DATAINPUT_ID+","+getRm(rows[i].COMMENTS,rows[i].NODERM)+";";
		}
		var saveObj = {
			errorIds : errorIds,
			comments : encodeURIComponent(encodeURIComponent(comments))
		};
		$.ajax({
			async : true,
			type : "post",
			data : {
				saveObj : JSON2.stringify(saveObj),
				templateId : templateId					
			},
			url : "${ctx}/rpt/input/taskcase/saveErrorMark.json",
			success : function() {
				window.parent.doRebut(reButNode);
			},
			error : function(result, b) {
				window.parent.doRebut(reButNode);
			}
		});
		
	}

	function initAutoLoadRows(){
	    autoLoadRows = [];
	    if(autoLoad == true){
	        var manager = $("#maingrid").ligerGetGridManager();
	        var data = manager.getData();
	        for (i = 0; i < data.length; i++) {
	            var dataType = grid.getRow(i).data_type;
		        if(dataType != '4'){
		            autoLoadRows.push(grid.getRow(i));
		        }
	        }
	        autoLoad = false;
	    }
	}

	function updateDateType(){
        for (i = 0; i < autoLoadRows.length; i++) {
            var rec = autoLoadRows[i];
            var newObj = JSON.parse(JSON.stringify(rec));
            newObj.data_type = '3';
	        manager.updateRow(rec, newObj);
        }
    }
	
	function form_save1(validataChack,impFunc) {
		var manager = $("#maingrid").ligerGetGridManager();
		var data = manager.getData();
		if(data.length == 0 && delArr.length == 0){
			return;
		}
		var param = "";
		udpArr=[];
		addArr=[];
		if(addArr.length == 0 && udpArr.length==0){
			CRUD_DATA();
		}
		
		for (let j = 0; j < formType.length; j++) {
			param = param + formType[j] + ","
		}
		param = param + 'SYS_ORDER_NO' + ',';
		param = param + 'COMMENTS' + ',';
		param = param + 'DATAINPUT_ID' + ',';
		paramStrDel = delArr.join('@;@');
		paramStrAdd = addArr.join('@;@');
		paramStrUdp = udpArr.join('@;@');
		var flag = false;
		var info = {
			"templeId" : templateId, 
			"taskInstanceId" : taskInstanceId,
			"taskId" : taskId,
			"param" : param, 
			"paramStrAdd" : encodeURIComponent(paramStrAdd), 
			"paramStrDel" : paramStrDel, 
			"paramStrUdp" : encodeURIComponent(paramStrUdp), 
			"caseId" : taskInstanceId,
			"logList": JSON2.stringify(logList)
		};
		$.ajax({
			data : info,
			type : "post",
			dataType : 'json',
			url : "${ctx}/rpt/input/taskcase/saveTaskcaseTempleInfo.json",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("?????????????????????...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if (data.length == 0) {
					paramStrDel = ""; 
					paramStrAdd = ""; 
					paramStrUdp = "";
					addArr = [];
					udpArr = [];
					delArr = [];
					
					if (validataChack == "submit") {
						//?????? ???????????????  ????????????????????????
						form_doSubmit();
					}else if (validataChack == "validata") {
						validata("");
					} else if (validataChack == "save") {
						subimtFun("not");
					} else if (validataChack == "save2") {
						subimtFun("submit");
					} else {
						if(typeof impFunc != "function"){
							BIONE.showSuccess("??????????????????!");
						}else if(typeof impFunc == "function"){
							impFunc();
						}
					}
					logList = [];
					grid.loadData();
				} else if (data == "???????????????????????????") {
					paramStrUdp = ""; paramStrAdd = "";
					BIONE.showError(data);
				} else {
					paramStrUdp = ""; paramStrAdd = "";
					BIONE.commonOpenDialog('????????????', "chackDataFile", "630", "440", '${ctx}/rpt/input/taskcase/taskCaseValidateLog?templeId=' + templateId + '&caseId=' + taskInstanceId);
				}
			},
			error : function(result, b) {
				BIONE.tip("??????????????????!????????????????????????");
			}
		});
	}
	//?????????
	function addRow(info){
		var manager = $("#maingrid").ligerGetGridManager(); 
		manager.addRow(info); 
	}
	
	//????????????
	function validata() {
		BIONE.ajax({
			type : "POST",
			url : "${ctx}/rpt/input/taskcase/validataTaskcaseInfo",
			data : { templeId : templateId, caseId : taskInstanceId, orgList : window.selectOrg }
		}, function(data) {
			if (data.length == 0) {
				jiaoyan = "1";
				BIONE.showSuccess("????????????????????????!");
			} else {
				BIONE.commonOpenDialog('????????????', "chackDataFile", "630", "440",
						'${ctx}/rpt/input/taskcase/taskCaseValidateLog?templeId=' + templateId + '&caseId=' + taskInstanceId);
			}
		});
	}
	//?????????
	function udpdateRow(info, data, rowId) {
		if (rowId != "" && rowId != "1") {
			var manager = $("#maingrid").ligerGetGridManager();
			var data = manager.getData();
			var row = rowId.substring(2, rowId.length);
			var row2 = row - 1;
			if (data[row2].data_type == null || data[row2].data_type == "3") {
				info["data_type"] = "3";
			} else {
				info["data_type"] = "0";
			}
			manager.deleteRow(manager.getRowObj(rowId));
		} else if (rowId == "1") {
			var manager = $("#maingrid").ligerGetGridManager();
			manager.deleteRow(manager.getRowObj(manager.getData().length - 1));
		} else {
			achieveIds();
			var manager = $("#maingrid").ligerGetGridManager();
			if (ids.length == 1) {
				manager.deleteRange(ids);
			}
		}
		manager.addRow(info);
	}
	//????????????
	function form_copy1(){
		if (butCheck.allowAdd == '0') {
			BIONE.tip("????????????????????????!");
			return;
		}
		var ddd = getClipboard()||'';
		var hang = ddd.split("\r\n");
		var row1 = hang[0].split("\t");
		if (row1.length < formName.length || formName.length < row1.length) {
			BIONE.tip("???????????????????????????????????????");
			return;
		}
		for ( var i = 0; i < hang.length-1; i++) {
	???    		var row = hang[i].split("\t");
	???    		/*
			 * if(row.length<formName.length || formName.length<row.length){
			 * BIONE.tip("???????????????????????????????????????"); return; }
			 */
	???    		var obj = {};
			for ( var j = 0; j < row.length; j++) {
				obj[formType[j]] = row[j];
			}
			obj["data_type"] ="0";
			addRow(obj);
	???    	}
	}
	//ctrl+v
	document.onkeypress = function() {
		if (window.event.keyCode == 22) {
			form_copy1();
		}
		return true;
	}

	//?????????????????????
	function getClipboard() {
		if (window.clipboardData) {
			return (window.clipboardData.getData('Text'));
		} else if (window.netscape) {
			netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
			var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
			if (!clip)
				return;
			var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
			if (!trans)
				return;
			trans.addDataFlavor('text/unicode');
			clip.getData(trans, clip.kGlobalClipboard);
			var str = new Object();
			var len = new Object();
			try {
				trans.getTransferData('text/unicode', str, len);
			} catch (error) {
				return null;
			}
			if (str) {
				if (Components.interfaces.nsISupportsWString)
					str = str.value.QueryInterface(Components.interfaces.nsISupportsWString);
				else if (Components.interfaces.nsISupportsString)
					str = str.value.QueryInterface(Components.interfaces.nsISupportsString);
				else
					str = null;
			}
			if (str) {
				return (str.data.substring(0, len.value / 2));
			}
		}
		return null;
	}
	
	function taskSubmitOrg(title) {
		BIONE.commonOpenDialog(title, "taskSubmitOrg", null, null, "${ctx}/rpt/input/taskcase/taskSubmitOrg?caseId=" + taskInstanceId + "&templeId=" + templateId);
	}
	
	function subimtFun(submit) {
		form_auto1('submit');
	}
	//????????????
	function form_submit2() {
		taskSubmitOrg("?????????????????????");
		window.callback = function() {
			var manager = $("#maingrid").ligerGetGridManager();
			var data = manager.getData();
			if (data.length == 0) {
				//BIONE.tip("??????????????????");
				//return;
				subimtFun("submit");
				return;
			}
			for (i = 0; i < data.length; i++) {
				if (data[i].data_type == "0" || data[i].data_type == "3" || delArr != "") {
					$.ligerDialog.confirm('?????????????????????????????????????????????????????????', function(yes) {
						if (yes) {
							form_save1("save2");
						}
					});
					break;
				} else if (i == data.length - 1) {
					if (jiaoyan == '0') {
						$.ligerDialog.confirm('?????????????????????????????????????????????', function(yes) {
							if (yes) {
								subimtFun("submit");
							}
						})
					} else if (jiaoyan == '1') {
						subimtFun("submit");
					}
				}
			}
		}
	}
	//????????????
	function form_return() {
		// ?????????????????????????????????
		form_return.callback && form_return.callback();
		
		/* if (butLock == '1') {
			BIONE.tip("??????????????????????????????!");
			return;
		}
		taskSubmitOrg("???????????????");
		window.callback = function() {
			$.ligerDialog.confirm('????????????????????????????????????????????????????????????????????????????', function(yes) {
				if (yes) {
					var info = {
						"templeId" : templateId, "caseId" : taskInstanceId, "orgCode" : window.selectOrg
					};
					$.ajax({
						data : info,
						type : "post",
						dataType : 'text',
						url : "${ctx}/rpt/input/taskcase/rollbackTaskcaseInfo.json",
						beforeSend : function() {
							BIONE.loading = true;
							BIONE.showLoading("?????????????????????...");
						},
						complete : function() {
							BIONE.loading = false;
							BIONE.hideLoading();
						},
						success : function(data) {
							if (data == 'ok') {
								BIONE.showSuccess("????????????!");
								grid.loadData();
							} else {
								BIONE.showError("????????????!" + data);
							}
						},
						error : function(result, b) {
							BIONE.tip('?????????????????? <BR>????????????' + result.status);
						}
	
					});
				}
			});
		} */
	}
	//????????????
	function form_reflash() {
		paramStrDel = ""; paramStrAdd = ""; paramStrUdp = "";
		delArr = [];
		logList = [] ;
		grid.set('url', uri);
	}
	//??????????????????
	function form_select(){
// 		var manager = $("#maingrid").ligerGetGridManager(); 
// 		var data = manager.getData();
// 	    for(i=0;i<data.length;i++){
// 	    	if(data[i].data_type=="0" || data[i].data_type=="3"){
// 	    		BIONE.tip("???????????????????????????????????????????????????!");
// 	    		return;
// 	    	}
// 	    }
// 		if(butLock=='1'){
// 			BIONE.tip("???????????????????????????!");
// 			return;
// 		}else{
			var paramStr = "";
			_dialog = BIONE.commonOpenDialog(butCheck.templeName + "??????", "taskcaseSearch", "585", "440",
				"${ctx}/udip/task/taskTempleSearch?paramStr="+encodeURIComponent(encodeURIComponent(paramStr))+"&templeId="+templateId + "&caseId="+taskInstanceId);
// 		}
	} 
	//??????????????????
	function form_validate(){
		BIONE.commonOpenDialog('????????????', "chackDataFile", "630", "440", '${ctx}/rpt/input/taskcase/taskCaseValidateLog?templeId=' + templateId+'&caseId=' +taskInstanceId);
	}
	//??????????????????
	function searchRow(strTerms){
		var newUrl = '${ctx}/rpt/input/taskcase/getTaskCaseInfoList.json?onlineTask="online"&taskInstanceId='+taskInstanceId+'&templeId='+templateId+'&sqlStr='+encodeURIComponent(encodeURIComponent("${sqlStr}"+strTerms))+'&caseId='+taskInstanceId+'&searchTerms='+encodeURIComponent(encodeURIComponent("${searchTerms}"))
		grid.set('url', newUrl); 
	}
	// ??????????????????
	function doImport() {
		$.ajax({
			url : "${ctx}/rpt/input/taskcase/authRecordDataType",
			type : 'post',
			async : true,
			data: {
				templeId: templateId,
				caseId : taskInstanceId
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("???????????????????????????...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(data) {
				if (data == "" || data == null) {
					BIONE.tip("????????????????????????????????????");
				} else if(data == "2") {
					BIONE.tip("?????????????????????");
				} else if(data == "1") {
					BIONE.tip("????????????????????????????????????");
				} else if(data == "0") {
					BIONE.tip("???????????????????????????");
				} else if(data=="submit" || data=="sucess" ) {
					BIONE.tip('??????????????????');
				} else {
					Rows = [];
					BIONE.commonOpenDialog('????????????', 'inputTaskInfo', 562, 334, "${ctx}/rpt/input/temple/uploadTempleData?templeId=" + templateId + "&caseId=" + taskInstanceId+"&taskNodeInstanceId="+taskNodeInstanceId
							,null , function(){
						if(Rows.length != 0){
							alertError(Rows);
						}
					});

				}
			}
		});
	}
	//??????grid?????????data?????????????????????????????????URL?????????????????????
	var filterTranslator = {
	    translateGroup : function (group,obj)
	    {
	        var out = [];
	        if (group == null) {
	        	return " 1==1 ";
	        }
	        var appended = false;
	        out.push('(');
	        for ( var i = 0; i < formType.length; i++) {
				if (formSelectable[i] == "1") {
					out.push(this.translateRule(obj[formType[i]],formType[i],appended));
				}
				appended = true;
			}
	        out.push(')');
	        if (appended == false) {
	        	return " 1==1 ";
	        }
	        return out.join('');
	    },
	    translateRule : function(value,type,appended)
	    {
	        var out = [];
	        if (appended){
	        	out.push(this.getOperatorQueryText());
	        }
	        if (value == null || value=="") {
	        	out.push(' 1==1 ');
	        	return out.join('');
	        }
            out.push('/');
            out.push(value);
            out.push('/i.test(');
            out.push('o["');
            out.push(type);
            out.push('"]');
            out.push(')');
            return out.join('');
	    },
	    getOperatorQueryText : function()
	    {
	        return " && ";
	    }
	};
	function form_rule(){
		BIONE.commonOpenDialog('????????????', 'objDefManage', 562, 334,'${ctx}/rpt/input/rule/lookDataRules?lookType=lookType'+'&templeId=' + templateId);
	}
	
	/******??????????????????    ?????? cl**************/
	var logList=[];
	function fileAddLog(fieldList){
		for(var i = 0 ;i <fieldList.length;i++){
			var content = "";
			var fieldInfos = fieldList[i].fieldInfo;
			for(var j = 0 ;j<fieldInfos.length;j++){
				content = content + "?????????:"+fieldInfos[j].fieldName+",???:"+fieldInfos[j].value+";  ";
			}
			var log ={
				type : "add",
				id :  uuid.v1(),
				content : content
			};
			logList.push(log);
		}
	}
	
	function fieldModifyLog(fieldList){
		for(var i = 0 ;i <fieldList.length;i++){
			var content = "";
			var fieldInfos = fieldList[i].fieldInfo;
			var fieldId = fieldList[i].fieldId;
			for(var j = 0 ;j<fieldInfos.length;j++){
				if(fieldInfos[j].oldValue &&fieldInfos[j].oldValue&&fieldInfos[j].oldValue != fieldInfos[j].value )
				content = content +"?????????:"+fieldInfos[j].fieldName+",???:"+fieldInfos[j].oldValue+" ???????????????:"+fieldInfos[j].value+";   ";
			}
			var log ={
				type : "modify",
				id : fieldId,
				content : content
			};
			logList.push(log);
		}
	}
	
	function fieldDeleteLog(fieldList){
		for(var i = 0 ;i <fieldList.length;i++){
			var content = "";
			var fieldId = fieldList[i].id;
			var fieldInfos = fieldList[i].fieldInfo;
			if(isNewField(fieldId))
				continue;
			for(var j = 0 ;j<fieldInfos.length;j++){
				content = content + "?????????:"+fieldInfos[j].fieldName+",???:"+fieldInfos[j].value+";  ";
			}
			content = content + "???????????????";
			var log = {
				type : "delete",
				id : fieldId,
				content : content
			}
			logList.push(log);
		}
	}
	
	function isNewField(fieldId){
		for(var i = 0 ;i <logList.length;i++){
			if(logList[i].type=="add"&&logList[i].id == fieldId)
				return true;
		}
		return false;
	}
	
	/******??????????????????  ??????*****************/
	
	
</script>

</head>
<body style="padding: 0px; overflow: hidden;">
	<div id="content" style="width: 100%; overflow: auto; clear: both;">
		<div id="template.center">
			<form id="mainsearch" action="${ctx}/udip/temple/tab1-save-update"
				method="post"></form>
		</div>
	</div>
</body>
</html>