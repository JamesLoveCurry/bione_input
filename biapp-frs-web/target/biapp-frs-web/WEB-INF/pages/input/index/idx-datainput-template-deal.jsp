<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<script type="text/javascript">
	var templateId = '${templateId}';
	var taskId = '${taskId}';
	var taskInstanceId = '${taskInstanceId}';
	var inputType = '${inputType}';
	var dataDate = '${dataDate}';
	var canEdit = '${canEdit}';
	var canRebut = window.parent.canRebut;

	$(function() {
		window.parent.frameControl = window;
		initPage();
	});
	function initPage() {
		$
				.ajax({
					cache : false,
					async : true,
					url : "${ctx}/rpt/input/idxdatainput/getOperTemplate.json?templateId="
							+ templateId
							+ "&taskInstanceId="
							+ taskInstanceId
							+ "&d=" + new Date().getTime(),
					type : "post",
					success : function(result) {
						if (result) {
							for ( var i = 0; i < result.length; i++)
								appendOneTbl(result[i]);
						}
					},
					error : function(result, b) {
					}
				});
	}
	function appendOneTbl(tbInfo) {
		var gridId = tbInfo.cfgId;
		$("#gridContainer")
				.append(
						'	<div id="'
								+ gridId
								+ '" style="width: 100%; overflow: none; clear: both; background-color: #FFFFFF;">');
		var tabObj = "#" + gridId;
		var oneTab = getGridInfo(tbInfo, gridId);
		var grid = $(tabObj).ligerGrid(oneTab);
		appendGridManager(gridId, grid,tbInfo.cfgId);
	}

	var gridManager = [];
	function appendGridManager(gridId, grid,cfgId) {
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/idxdatainput/getCheckVal.json?cfgId=" + cfgId,
			type : "post",
			success : function(result) {
				var checkValue = null;
				var checkType = null;
				if (result&&result!=null) {
					checkValue = result.value;
					checkType = result.ruleType;
				}
				gridManager.push({
					key : gridId,
					gridObj : grid,
					cfgId : cfgId,
					checkValue : checkValue ,
					checkType : checkType
				});
			},
			error : function(result, b) {
				BIONE.tip('校验失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}

	function getGridInfo(tbInfo, gridId) {
		var realHeight = (tbInfo.data.length-1)*50 +110;
		var tbString = {
			checkbox : canEdit!="true"&&canRebut,
			allowHideColumn : false,
			usePager : false,
			rownumbers : true,
			width : '95%',
			height : realHeight,
			enabledEdit : true,
			clickToEdit : true,
			onCheckRow: zOnCheckRow,
			isChecked: function(rowData){
				if(rowData.dataSts=="01")
					return true;
				return false;
			}
		};
		if (tbInfo.columnVO != null && typeof tbInfo.columnVO != "undefined") {
			tbString.columns = getColumn(tbInfo.columnVO, gridId);
		}
		if (tbInfo.data != null && typeof tbInfo.data != "undefined") {
			tbString.data = getDatas(tbInfo.data, gridId);
		}
		return tbString;
	}
	function zOnCheckRow(isCheck,rowData,rowId,row){
		if(isCheck)
		{
			rowData.dataSts = "01";
		}
		else{
			if(rowData.dataSts)
				delete rowData['dataSts'];
		}
		//this.updateCell(row.rowIndex, null, rowData);		
		this.updateRow(row, rowData);
		//this.updateCell(row.rowIndex, null, rowData);
	}

	function getColumn(column, gridId) {
		var colContent = [];
		var width;
		var colDetail = {
			display : column.display,
			align : column.align,
			columns : []
		};
		if (column.columns) {
			var columns = column.columns;
			for ( var i = 0; i < columns.length; i++) {
				if (columns[i].name == "indexNm") {
					width = columns[i].width;
					colDetail.columns.push({
						name : columns[i].name,
						align : columns[i].align,
						width : columns[i].width,
						display : columns[i].display,
						render : v_render
					});
				} else if (columns[i].name == "org01") {
					//不可以进行选择
					colDetail.columns.push({
						name : "org",
						align : columns[i].align,
						width : columns[i].width,
						display : columns[i].display,
						render : v_render
					});
				}  else if (columns[i].name == "org02") {
					var val={
						name : "org",
						align : columns[i].align,
						width : columns[i].width,
						display : columns[i].display,
						render : v_render
					};
					if(canEdit=="true"){
						val.editor={
								type : "select",
								ext : on_chooseOrg
							};
					}
					//不可以进行选择
					colDetail.columns.push(val);
				} else if (columns[i].name == "currency") {
					var val={
							name : columns[i].name,
							align : columns[i].align,
							width : columns[i].width,
							display : columns[i].display,
							render : v_render
					};
					if(canEdit=="true"){
						val.editor={
								type : "select",
								ext : on_chooseCurrency
							};
					}
					colDetail.columns.push(val);
				} else if (columns[i].name == "value") {
					var colwidth = canEdit!="true"&&canRebut?(columns[i].width.split("%")[0]-4)+"%":(columns[i].width.split("%")[0]-2)+"%";
					var val = {
							name : columns[i].name,
							align : columns[i].align,
							width : colwidth,
							display : columns[i].display
					};
					if(canEdit=="true"){
						val.editor={							
								type : "number"
							};
						/*
						val.validate = {
							required : true,
							maxlength : 100,
							remote : {
								url : "${ctx}/rpt/input/idxdatainput/testTaskVal",
								type : "POST",
								data : {
									"cfgId" : ""
								}
							},
							messages : {
								remote : "相同路径下任务已存在"
							}
						}
						*/
					}
					colDetail.columns.push(val);
				} else {
					var val = {
							name : columns[i].name,
							align : columns[i].align,
							width : columns[i].width,
							display : columns[i].display,
							render : v_render
					};
					if(canEdit=="true"){
						val.editor={						
							type : "select",
							ext : on_chooseDim
						};
					}
					colDetail.columns.push(val);
				}
			}
		}
			colDetail.columns
			.push({
				name : "oper",
				align : "center",
				width : width,
				display : "操作",
				render : function(rowData, rowNum, colData) {
					var showText ="<div style='height:35px;text-align: center; MARGIN-TOP: 10px'>";
					if(canEdit=="true"){
						showText = showText + "<a href='##'   class='oper'  onclick=\"onAddCol('"
						+ gridId
						+ "')\" >增行</a>&nbsp"
						+ "<a href='##'    class='oper'  onclick=\"onDelCol('"
						+ gridId
						+ "','"
						+ rowNum
						+ "') \" >删行</a>&nbsp";
					}
					showText = showText + "<a href='##'   class='oper'  onclick=\"onComment('"+ gridId + "','" + rowNum + "')\" >意见</a></div>";
					return showText;
				}
			});
		colContent.push(colDetail)
		return colContent;
	}

	function onDelCol(gridId, rowNum) {
		var manager = getGridManager(gridId);
		var rowCnt = manager.rows.length;
		if (rowCnt == 1)
			return;
		var row = manager.getRow(rowNum);
		manager.deleteRow(row);
		var tmpHeight = $("#" + gridId).height() - 50;
		//$("#"+gridId).height(tmpHeight);
		$("#" + gridId).height(tmpHeight);
		manager.setHeight(tmpHeight - 1);
	}

	function onComment(gridId, rowNum) {
		var manager = getGridManager(gridId);
		var row = manager.getRow(rowNum);
		var operOpinion = "";
		if(row.operOpinion)
			operOpinion = encodeURI(encodeURI(row.operOpinion));
		BIONE.commonOpenDialog('选择组织', 'commentBox', '400', '150',
				'${ctx}/rpt/input/idxdatainput/comment?content='+ operOpinion, null,
				function(data) {
					if (!data)
						return;
					row.operOpinion = data;
				});
	}

	function onAddCol(gridId) {
		commonAddCol(gridId);
	}
	
	function commonAddCol(gridId,rowInfo){

		var manager = getGridManager(gridId);
		var isAddFirstRow = false;
		if(!rowInfo)
		{
			rowInfo = manager.rows[0];
			isAddFirstRow = true;
		}
		var row = clone(rowInfo);
		var tmpRow = {
			gridId : gridId
		};
		tmpRow['idxInfo'] = row.idxInfo;
		tmpRow['currencyInfo'] = {};
		tmpRow['value'] = "";
		if (row.orgInfo.orgFilter) {
			if(isAddFirstRow){
				tmpRow['orgInfo'] = {
						orgFilter : row.orgInfo.orgFilter,
						orgId : "",
						orgNm : ""
					};
			}else{
				tmpRow['orgInfo'] = {
						orgFilter : row.orgInfo.orgFilter,
						orgId : row.orgInfo.orgId,
						orgNm : row.orgInfo.orgNm
					};
			}
		} else {
			tmpRow['orgInfo'] = {
				orgId : row.orgInfo.orgId,
				orgNm : row.orgInfo.orgNm
			};
		}
		var columns = manager.columns;
		for ( var c = 0; c < columns.length; c++) {
			var column = columns[c];
			if (contains(column.name, "_")) {
				if(isAddFirstRow){
					tmpRow[column.name] = {
							text : "",
							filter : row[column.name].filter
						};
				}else{
					tmpRow[column.name] = {
							text : row[column.name].text,
							dimNo : row[column.name].dimNo,
							filter : row[column.name].filter
						};
				}
			}
		}
		tmpRow.currencyInfo = row.currencyInfo;
		manager.addRow(tmpRow);
		var tmpHeight = $("#" + gridId).height() + 50;
		//$("#"+gridId).height(tmpHeight);
		$("#" + gridId).height(tmpHeight);
		manager.setHeight(tmpHeight - 1);
	}

	function contains(array, string) {
		if (array) {
			for ( var i = 0, l = array.length; i < l; i++) {
				if (array[i] == string)
					return true;
			}
		}
		return false;
	}

	function on_chooseOrg(rowData,rowNum,colData) {
		var colNm = arguments[3].columnname;
		var gridId = rowData.gridId;
		var manager = getGridManager(gridId);
		//rowData.orgInfo;
		BIONE.commonOpenDialog('选择组织', 'selectOrgBox', '300', '480',
				'${ctx}/rpt/input/idxdatainput/listOrg?orgFilter='
						+ encodeURI(encodeURI(rowData.orgInfo.orgFilter)),
				null, function(data) {
					if (!data)
					{
						manager.endEdit();
						return;
					}
					if(data.orgId!=null){
						//var orgInfo={orgId:data.orgId,orgNm:data.orgNm};
						rowData.orgInfo.orgId = data.orgId;
						rowData.orgInfo.orgNm = data.orgNm;
						manager.updateCell(colNm, null, rowData);
						manager.endEdit();
					}else if(data.orgIds!=null){
						var selectedRow = manager.getRow(rowNum);
						var tmpOrgFilter =  selectedRow.orgInfo.orgFilter;
						var cloneRow = clone(selectedRow);
						for(var m =0;m<data.orgIds.length;m++){
							var tmpOrgId = data.orgIds[m];
							var tmpOrgNm = data.orgNms[m];
							cloneRow.orgInfo={
									orgId : tmpOrgId,
									orgNm : tmpOrgNm,
									orgFilter : tmpOrgFilter
								};
							commonAddCol(gridId,cloneRow)
						}

					}
				});
	}

	//初始化
	function clone(obj){
		if(typeof(obj) !='object')return obj;
		 if(obj==null ) return obj;
		 var newobj={};
		 for(var i in obj){
			 newobj[i]=clone(obj[i]);
		 }
		 return newobj;
	}
	function on_chooseCurrency(rowData,rowNum) {
		var gridId = rowData.gridId;
		var colNm = arguments[3].columnname;
		BIONE.commonOpenDialog('选择币种', 'selectCurrencyBox', '300', '480',
				'${ctx}/rpt/input/idxdatainput/selectCurr', null, function(
						data) {
					if (!data)
						return;
					rowData.currencyInfo.currencyId = data.currId;
					rowData.currencyInfo.currencyNm = data.currText;

					var manager = getGridManager(gridId);
					var row = manager.getRow(rowNum);
					manager.updateRow(row, rowData);
					//manager.updateRow(colNm, "", rowData);
					manager.endEdit();
				});
	}

	function on_chooseDim(rowData) {

		var colNm = arguments[3].columnname;
		var gridId = rowData.gridId;
		var colVal = rowData[arguments[3].columnname];

		BIONE.commonOpenDialog('选择维度', 'selectDimBox', '300', '480',
				'${ctx}/rpt/input/idxdatainput/selectDim?dimInfo=' + colNm
						+ '&filterInfo='
						+ rowData[arguments[3].columnname].filter, null,
				function(data) {
					if (!data)
						return;
					var manager = getGridManager(gridId);
					colVal.text = data.dimText;
					colVal.dimNo = data.dimNo;
					manager.updateCell(colNm, colVal, rowData);
					manager.endEdit();
				});
	}

	function getGridManager(gridId) {
		for ( var i = 0; i < gridManager.length; i++) {

			if (gridManager[i].key == gridId) {

				return gridManager[i].gridObj
			}
		}
	}

	function v_render(data) {
		if (arguments[3].columnname == "indexNm")
		{
			if(data.dataSts =="01")
			{
				return  "<font color=\"#ff0000\">"+data.idxInfo.idxNm+"</font>";
			}
			else 
				return "<font scolor=\"#ffffff\">"+data.idxInfo.idxNm+"</font>";
		}
		else if (arguments[3].columnname == "org")
			return data.orgInfo.orgNm;
		else if (arguments[3].columnname == "currency")
			return data.currencyInfo.currencyNm;
		else{
			return data[arguments[3].columnname].text;
		}
	}

	function getDatas(dataList, gridId) {
		var dataSize = dataList.length;
		var rs = {
			Rows : [],
			Total : dataSize
		};
		for(var x = 0 ;x<dataList.length;x++){
			var data = dataList[x];
			var colMap = [];
			for ( var i = 0; i < data.length; i++) {
				if (data[i].type == "commColumn")
					colMap[data[i].key] = {
						text : data[i].text,
						dimNo : data[i].dimNo,
						filter : data[i].filterVal
					};
				else if (data[i].type == "indexColumn")
					colMap['idxInfo'] = {
						idxNm : data[i].idxNm,
						idxId : data[i].idxId
					};
				else if (data[i].type == "orgColumn")
					colMap['orgInfo'] = {
						orgId : data[i].orgId,
						orgNm : data[i].orgNm,
						orgFilter : data[i].filterVal
					};
				else if (data[i].type == "currencyColumn")
					colMap['currencyInfo'] = {
						currencyId : data[i].currencyId,
						currencyNm : data[i].currencyNm
					};
				else if (data[i].type == "dataSts")
					colMap['dataSts'] =  data[i].value;
				else if (data[i].type == "value")
					colMap['value'] = data[i].value;
				else if (data[i].type == "operOpinion")
					colMap['operOpinion'] = data[i].value;
			}
			colMap['gridId'] = gridId;
			rs.Rows.push(colMap);
		}
		return rs;
	}
	//01 保存 02 提交  03 驳回
	function save(operType) {
		var rsInfo = {
			taskId : taskId,
			taskInstanceId : taskInstanceId,
			templateId : templateId,
			dataDate : dataDate,
			dataInputList : []
		};
		for ( var i = 0; i < gridManager.length; i++) {
			var dataInputObj = [];
			var gridObj = gridManager[i].gridObj;
			gridObj.endEdit();
			var columns = gridObj.columns;
			var rows = gridObj.rows;
			var checkType = gridManager[i].checkType;
			var checkValue = gridManager[i].checkValue;
			var tmpCheckArray = [];
			for(var x =0;x <rows.length;x++){
				var colObj = [];
				var row = gridObj.rows[x];
				var tmpKey = "";
				for ( var c = 0; c < columns.length; c++) {
					if(c == 0 )continue;
					var column = columns[c];
					var dimTypeNo;
					var dimItemValue;
					if (column.name == "indexNm") {
						dimItemValue = row.idxInfo.idxId;
						dimTypeNo = "INDEX_NO";
					} else if (column.name == "currency") {
						dimTypeNo = "CURRENCY";
						dimItemValue = row.currencyInfo.currencyId;
					} else if (column.name == "org") {
						dimTypeNo = "org";
					} else if (column.name == "value") {
						dimTypeNo = "INDEX_VAL";
						var tmpValue = row.value;
						//cfgNm,idxNm,rowLine,checkValue,checkRuleType,tmpValue
						if(!checkRowValue(gridObj._columns['c102'].display,row.idxInfo.idxNm,(x+1),checkValue,checkType,tmpValue))
						{
							return ;
						}
						if(!tmpValue||tmpValue==null||tmpValue=="")
							tmpValue = "";
						dimItemValue = tmpValue;
					}  else if(column.name=="oper"){
						dimTypeNo = "oper";
					}else if(column.ischeckbox)
					{
						dimTypeNo = "ischeckbox";
					}	
					else {
						var colName = column.name;
						var cols = colName.split("_");
						dimTypeNo = cols[1];
						dimItemValue = row[colName].dimNo;
					}
					if (dimTypeNo != "org"&&dimTypeNo!="oper"&&dimTypeNo!="ischeckbox"){
						colObj.push({
							dimTypeNo : dimTypeNo,
							dimItemValue : dimItemValue
						});
						if(column.name != "value"){
							tmpKey = tmpKey + dimItemValue+"_";
						}
					}
				}
				if( canEdit!="true"&&canRebut){
						if(row.dataSts){
						colObj.push({
							dimTypeNo : "DATA_STS",
							dimItemValue : row.dataSts
						});
					}
				}else{
					colObj.push({
						dimTypeNo : "DATA_STS",
						dimItemValue : "00"
					});
				}
				colObj.push({
					dimTypeNo : "ORG_NO",
					dimItemValue : row.orgInfo.orgId
				});
				tmpKey = tmpKey +( row.orgInfo.orgId==null?"":row.orgInfo.orgId)+"_";
				colObj.push({
					dimTypeNo : "operOpinion",
					dimItemValue : row.operOpinion
				});
				tmpKey = tmpKey + row.operOpinion+"_";
				colObj.push({
					dimTypeNo : "CFG_ID",
					dimItemValue : gridManager[i].key
				});
				tmpKey = tmpKey +  gridManager[i].key;
				if(tmpCheckArray[tmpKey]!=null){
					BIONE.tip(gridObj._columns['c102'].display+",指标名"+row.idxInfo.idxNm+"第"+ tmpCheckArray[tmpKey] +"行数据和第"+(x+1)+"行数据完全一致,请检查");
					return ;
				}else{
					tmpCheckArray[tmpKey] = x+1;
				}
				dataInputObj.push(colObj);
			}
			rsInfo.dataInputList.push(dataInputObj);
		}
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/input/idxdatainput/saveDataInputData?d="
					+ new Date().getTime(),
			dataType : 'json',
			type : "post",
			data : {
				"data" : JSON2.stringify(rsInfo)
			},
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if (operType == "01")
					window.parent.closeDialog("01");
				else if (operType == "02")
					window.parent.doSubmit();
				else if (operType == "03")
					window.parent.doRebut();
			},
			error : function(result, b) {
				BIONE.tip('保存失败,发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	function getExportContent(){
		var dataInputExportSheetVO = [];
		for ( var i = 0; i < gridManager.length; i++) {
			var gridObj = gridManager[i].gridObj;
			gridObj.endEdit();
			var columns = gridObj.columns;
			var checkRuleType = checkInfo.ruleType;
			var rows = gridObj.rows;
			var columnInfo = [];
			var infoList = [];
			var tmpCheckArray = [];
			for(var x =0;x <rows.length;x++){
				var colObj = [];
				var row = gridObj.rows[x];
				var tmpKey = "";
				var cells = [];
				for ( var c = 0; c < columns.length; c++) {
					if(c == 0 )continue;
					var cellText = null;
					//第一行收取表头信息
					var column = columns[c];
					if(x == 0 &&column.name!="oper")
					{
						var oneCol = [];
						oneCol[c] = column.display;
						columnInfo.push(oneCol[c]);
					}
					var dimTypeNo;
					var dimItemValue;
					if (column.name == "indexNm") {
						dimItemValue = row.idxInfo.idxId;
						dimTypeNo = "INDEX_NO";
						cellText = row.idxInfo.idxNm;
					} else if (column.name == "currency") {
						dimTypeNo = "CURRENCY";
						dimItemValue = row.currencyInfo.currencyId;
						cellText = row.currencyInfo.currencyNm?row.currencyInfo.currencyNm:"";
					} else if (column.name == "org") {
						dimTypeNo = "org";
						cellText = row.orgInfo.orgNm;
						if(!cellText||cellText==null){
							BIONE.tip("请选择配置名["+gridObj._columns['c102'].display+"],指标名["+row.idxInfo.idxNm+"]的机构");
							return ;
						}
					} else if (column.name == "value") {
						dimTypeNo = "INDEX_VAL";
						var tmpValue = row.value;						
						if(!tmpValue||tmpValue==null||tmpValue=="")
							tmpValue = "";
						dimItemValue = tmpValue;
					}  else if(column.name=="oper"){
						dimTypeNo = "oper";
					}else if(column.ischeckbox)
					{
						dimTypeNo = "ischeckbox";
					}else {
						var colName = column.name;
						var cols = colName.split("_");
						dimTypeNo = cols[1];
						dimItemValue = row[colName].dimNo?row[colName].dimNo:"";
						
						cellText = row[colName].text?row[colName].text:"";
					}
					if (dimTypeNo != "org"&&dimTypeNo!="oper"&&dimTypeNo!="ischeckbox"){
						colObj.push({
							dimTypeNo : dimTypeNo,
							dimItemValue : dimItemValue
						});
						if(column.name != "value"){
							tmpKey = tmpKey + dimItemValue+"_";
						}
					}
					//添加导出内容
					if(cellText!=null){
							cells.push(cellText);
					}
				}
				infoList .push(cells);
				colObj.push({
					dimTypeNo : "ORG_NO",
					dimItemValue : row.orgInfo.orgId
				});
				tmpKey = tmpKey +( row.orgInfo.orgId==null?"":row.orgInfo.orgId)+"_";
				colObj.push({
					dimTypeNo : "operOpinion",
					dimItemValue : row.operOpinion
				});
				tmpKey = tmpKey + row.operOpinion+"_";
				colObj.push({
					dimTypeNo : "CFG_ID",
					dimItemValue : gridManager[i].key
				});
				tmpKey = tmpKey +  gridManager[i].key;
				if(tmpCheckArray[tmpKey]!=null){
					BIONE.tip(gridObj._columns['c102'].display+",指标名"+row.idxInfo.idxNm+"第"+ tmpCheckArray[tmpKey] +"行数据和第"+(x+1)+"行数据完全一致,请进行修改后再进行导出");
					return ;
				}else{
					tmpCheckArray[tmpKey] = x+1;
				}
				//dataInputObj.push(colObj);
			}
			//rsInfo.dataInputList.push(dataInputObj);
			dataInputExportSheetVO.push({columnInfo:columnInfo,infoList:infoList});
		}
		return dataInputExportSheetVO;
	}
	
	function checkRowValue(cfgNm,idxNm,rowLine,targetValue,checkType,tmpValue){
		var errorMsg = cfgNm+"</br>指标名:"+idxNm+"</br>第"+rowLine+"行数据值校验失败,数值不能";
		var isOk = false;
		var value = Number(tmpValue);
		if(checkType!=null){
			if(checkType=="1")
			{
				isOk =  value>targetValue;
				errorMsg = errorMsg+"小于或等于"+targetValue;
			}
			else if(checkType=="2")
			{
				isOk = value>=targetValue;
				errorMsg = errorMsg+"小于"+targetValue;
			}
			else if(checkType=="3")
			{
				isOk =  value<targetValue;
				errorMsg = errorMsg+"大于或等于"+targetValue;
			}
			else if(checkType=="4")
			{
				isOk = value<=targetValue;
				errorMsg = errorMsg+"大于"+targetValue;
			}
			else {
				isOk = value==targetValue;
				errorMsg = errorMsg+"等于"+targetValue;
			}
			if(!isOk)
				BIONE.tip(errorMsg);
		}else
			isOk = true;
		
		return isOk;
		
	}
	
	function doImport(value){
		for(var i = 0 ;i <gridManager.length;i++){
			var grid = gridManager[i].gridObj;
			var gridValueMap = getGridValueMap(grid);
			for(var j = 0 ; j<gridValueMap.length;j++){
				var key = gridValueMap[j].key;
				var rowData = gridValueMap[j].rowData;
				var cellValue = getCellValueByKey(key,value);
				rowData.value =cellValue;
				grid.updateCell("value", null, rowData);
			}
		}
	}
	
	function getCellValueByKey(key,v){
		for(var i = 0 ;i <v.length;i++){
			if(v[i].key == key)
				return v[i].value;
		}
	}
	
	function getGridValueMap(gridObj){
		var gridInfo = [];
		var rows = gridObj.rows;
		for(var x =0;x <rows.length;x++){
			
			var key ="" ;
			
			var row = rows[x];
			var columns = gridObj.columns;
			for ( var c = 0; c < columns.length; c++) {
				if(c == 0 )continue;
				var cellText = null;
				//第一行收取表头信息
				var column = columns[c];
				if (column.name == "indexNm") {
					dimItemValue = row.idxInfo.idxId;
					dimTypeNo = "INDEX_NO";
					cellText = row.idxInfo.idxNm;
				} else if (column.name == "currency") {
					dimTypeNo = "CURRENCY";
					dimItemValue = row.currencyInfo.currencyId;
					cellText = row.currencyInfo.currencyNm==null?"":row.currencyInfo.currencyNm;
				} else if (column.name == "org") {
					dimTypeNo = "org";
					cellText = row.orgInfo.orgNm==null?"":row.orgInfo.orgNm;
				} else if (column.name == "value") {
					dimTypeNo = "INDEX_VAL";
					var tmpValue = row.value;
					if(!tmpValue||tmpValue==null||tmpValue=="")
						tmpValue = "";
					dimItemValue = tmpValue;
				}  else if(column.name=="oper"){
					dimTypeNo = "oper";
				}else {
					var colName = column.name;
					var cols = colName.split("_");
					dimTypeNo = cols[1];
					dimItemValue = row[colName].dimNo? row[colName].dimNo:"";
					
					cellText = row[colName].text==null?"": row[colName].text;
				}
				//添加导出内容
				if(cellText!=null){
					if(c!=1)
						key = key +"_";
					key = key +cellText;
				}
			}
			gridInfo.push({key:key,rowData:row});
		}
		return gridInfo;
	}
	
</script>

<title>指标目录管理</title>
</head>
<body style="width: 100%">
	<div id="template.center">
		<div id="gridContainer"></div>
	</div>
</body>
</html>