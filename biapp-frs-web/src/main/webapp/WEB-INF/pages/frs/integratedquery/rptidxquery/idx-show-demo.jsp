<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template22.jsp">
<head>
<link rel="stylesheet" type="text/css"
	href="${ctx }/js/datashow/img/iconfont.css" />
	
<%-- <link rel="stylesheet" href="${ctx }/css/classics/gridquote/jquery.gridquote.css">
<script type="text/javascript" src="${ctx }/js/gridquote/jquery.gridquote.min.js"></script> --%>
<style type="text/css">
.label-item {
	height: 20px;
	float: left;
	line-height: 20px;
	border: 1px solid #D4D4D4;
	color: #4C4C4C;
	margin: 0 10px 5px 0;
	padding: 0 2px;
	background-color: #F0F0F0;
	border-radius: 2px;
	cursor: default;
}

.label-item:HOVER {
	border: 1px solid #C0C0C0;
	box-shadow: 0 1px 1px rgba(0, 0, 0, 0.15);
}

.label-item .text {
	float: left;
	height: 20px;
}

.label-item .icon {
	float: left;
	width: 10px;
	height: 10px;
	margin: 5px 0 5px 2px;
	cursor: pointer;
}

#button {
	position: relative;
	margin-left: auto;
	margin-right: auto;
	overflow: hidden;
	border-top: 1px solid #C6C6C6;
}
.l-dialog-btn:first-child{
	margin-top : 120px
}
.l-dialog-btn{
	margin-top : 10px
}
.l-grid-dim-filter{
	overflow : hidden;
	height : 14px;
	top : 4px;
}
.l-grid-dim-filtered{
	overflow : hidden;
	height : 14px;
	top : 4px;
}
</style>
<script type="text/javascript" src="${ctx}/js/datashow/jQuery.exLabel.js"></script>
<script type="text/javascript" src="${ctx}/js/numeral/numeral.js"></script>

<script type="text/javascript">
var grid;
var leftTreeObj;
var labelPad;
var chooseIdx = [];//已选指标
var columns = [];//表格列
var dimNos = [];//多个指标的维度交集
var dimFilters = [];//维度过滤Map
var measureFilters = [];//度量单位Map
var measureFilterName = [];//度量单位中文名称
var queryResult = [];//上一次查询结果
var moneyUnit = [{"text":"元", "id":"1"},{"text":"百元", "id":"100"},{"text":"千元", "id":"1000"},{"text":"万元", "id":"10000"},{"text":"亿元", "id":"100000000"}];
var numberUnit = [{"text":"个", "id":"1"},{"text":"百", "id":"100"},{"text":"千", "id":"1000"},{"text":"万", "id":"10000"},{"text":"亿", "id":"100000000"}];
var sl1,sl2;
var dl1,dl2;
var download;
var chooseIndexNo;
$(function(){
	
	$("#right").css("overflow-x","hidden");
	downdload = $('<iframe id="download"  style="display: none;"/>');
	$('body').append(downdload);
	
	//$("#advance").css("display", "block");
	//$("#col1").height(27 + $("#title3").height());
	
	initTree();
	initDims();
	
	//initCSS();
	initGrid([]);
	initTip();
	
	$("#treeSearchIcon").live('click',function(){
		leftTreeObj.reAsyncChildNodes(null, "refresh");
	});
	$('#treeSearchInput').bind('keydown', function(event) {
		if (event.keyCode == 13) {
			leftTreeObj.reAsyncChildNodes(null, "refresh");
 		}
	});
	sl1 = $("#sl1").ligerComboBox({data : moneyUnit, isMultiSelect: false, onSelected: function (value, text)
        {
	/* 	measureFilters["${indexNo}" + ("${measureNo}" == "" ? "" : "-${measureNo}")] = $("input[name=sel]:checked").val();
		measureFilterName["${indexNo}" + ("${measureNo}" == "" ? "" : "-${measureNo}")] = $("input[name=sel]:checked").attr("text"); */
		if(chooseIndexNo && value){
		measureFilters[chooseIndexNo] = value;
		measureFilterName[chooseIndexNo] = text; 
		addGridData();
		var column = grid.getColumns();
		$(column).each(function (){
                    if (this.name == chooseIndexNo){
                    	grid.changeHeaderText(chooseIndexNo, 
                    			this.text + "(" + text + ")");
                    }
		});
		dl1.hidden();
		}
		
		
    }});
	sl2 = $("#sl2").ligerComboBox({data : numberUnit, isMultiSelect: false, onSelected: function (value, text)
        {
		if(chooseIndexNo && value){
		measureFilters[chooseIndexNo] = value;
		measureFilterName[chooseIndexNo] = text; 
		addGridData();
		var column = grid.getColumns();
		$(column).each(function (){
                    if (this.name == chooseIndexNo){
                    	grid.changeHeaderText(chooseIndexNo, 
                    			this.text + "(" + text + ")");
                    }
		});
		dl2.hidden();
		}
		
    }});
	 $("#title3 > span").ligerTip({auto: true,width : 270,content : "这里是所选指标的共有的维度；<p>可以点击下面的标签进行选择是否查询该维度；<p>时间和机构为必选维度；"});
	/* $(".l-grid-dim-filter").mouseover(function(e){
		var tooltip = "<div id='tooltip'>" + "维度过滤" + "</div>";
		$("body").append(tooltip);
		$("#tooltip").css({
			"top" : e.pageX + "px",
			"left" : e.pageY + "px",
		}).show("fast");
	}).mouseout(function(e){
		//$("#tooltip").remove();
	}); */
	grid.setHeight($(document).height()- $("#col1").parent().parent().height() -  15);
	/* $("#extend").toggle(function() {
		$("#extend strong").html("<i class='iconfont icon-xiangshangshouqi'></i>");
		$("#col1").height($("#selected").height()+$("#title3").height());
		grid.setHeight($(document).height()- $("#col1").height() - $("#advance").height() - 15);
	}, function() {
		$("#extend strong").html("<i class='iconfont icon-xiangxiazhankai'></i>");
		$("#col1").height(27+$("#title3").height());
		grid.setHeight($(document).height()- $("#col1").height() - $("#advance").height() - 15);
		
	}); */
	
});
function initDims(){
	labelPad = $('#selected').exLabel();
}
function indexQuery(){
	var condition = {};
	var cols = {};
	condition.queryType = "index";
	if(dimFilters["DATE"] && dimFilters["ORG"] ){
		condition.startDate = dimFilters["DATE"].selectedNodes[0].id.split("-").join("");
		condition.endDate = dimFilters["DATE"].selectedNodes[1].id.split("-").join("");
	
		condition.orgNo = dimFilters["ORG"].checkIds;
		var searchArg = [];
		var dimNo = [];
		for(var tmp in dimFilters){
			dimNo.push(tmp);
			if(tmp != "DATE" && tmp != "ORG"){
				searchArg.push({dimNo : tmp, op : '=', value : dimFilters[tmp].checkIds});
			}
		}
		condition.dimNo = dimNo;
		
		if(chooseIdx.length > 0){
			var column = [];
			for(var tmp in chooseIdx){
				var obj = {columNo : chooseIdx[tmp].params.indexNo, 
						indexNo : chooseIdx[tmp].params.indexNo,
						searchArg : searchArg
						};
				if(chooseIdx[tmp].params.nodeType == "measureInfo"){
					obj.MeasureNo = chooseIdx[tmp].id;
					obj.columNo = chooseIdx[tmp].params.indexNo + "-" + chooseIdx[tmp].id;
				}
				column.push(obj);
			}
			condition.colums = column;
		}
		
		for(var tmp in columns){
			cols[columns[tmp].name] = columns[tmp].text;
		}
		$.ajax({
			url: '${ctx}/report/frame/datashow/idx/search/result',
			type: 'post',
			data: {
				p: JSON2.stringify(condition),
				col : JSON2.stringify(cols)
			},
			dataType: 'json',
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success: function(json) {
				if (json && json.isSuccess && json.isSuccess == true) {
					queryResult = json.result;
					addGridData();
				}else{
					BIONE.tip(json.errorMessage);
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}else{
		BIONE.tip("请对维度进行过滤！");
	}
}
function addGridData(){
	var result = clone(queryResult);
	if(queryResult && queryResult.length > 0){
		for(var i=0;i<queryResult.length;i++){
			for(var j=0;j<chooseIdx.length;j++){
				if(chooseIdx[j].params.nodeType == "idxInfo"){
					if(measureFilters[chooseIdx[j].params.indexNo]){
						if(queryResult[i][chooseIdx[j].params.indexNo]){
							result[i][chooseIdx[j].params.indexNo] = queryResult[i][chooseIdx[j].params.indexNo]/measureFilters[chooseIdx[j].params.indexNo];
						}
					}
				}else{
					if(measureFilters[chooseIdx[j].params.indexNo + "-" + chooseIdx[j].id]){
						if(queryResult[i][chooseIdx[j].params.indexNo + "-" + chooseIdx[j].id]){
							result[i][chooseIdx[j].params.indexNo + "-" + chooseIdx[j].id] = queryResult[i][chooseIdx[j].params.indexNo + "-" + chooseIdx[j].id]/measureFilters[chooseIdx[j].params.indexNo + "-" + chooseIdx[j].id];
						}
					}
				}
				
			}
		}
	}
	grid.set('data',{Rows : result, Total : result.length});	
}

function addDivToColumn(){
	for(var i=0;i<columns.length;i++){
		var y = $("td[columnname="+columns[i].name+"] > .l-grid-hd-cell-inner").height() + 1; 
		var width = $("td[columnname="+columns[i].name+"]").width();
		if($("#Tip" + columns[i].name).length == 0){
			var html = '<div class="l-verify-tip" id="Tip'+columns[i].name+'" style="visibility:hidden;width:'+ width +'px;z-index:1;position:absolute;top: '
			+ y +'px;"><ul id="Tree'+columns[i].name+'"></ul></div>';
			$("td[columnname="+columns[i].name+"]").append(html);
		}
		showDimFilter(columns[i].name);
		
		if(columns[i].businessType == "DIM"){//维度
			if(dimFilters[columns[i].name]){//已过滤
				addFilteredPic(columns[i].name, "dim");
			}else{//未过滤
				addFilteringPic(columns[i].name, "dim");
			}
			if(columns[i].name != "DATE" && columns[i].name != "ORG"){
				addDimDelete(columns[i].name);
			}
			
		}else{//度量
			if(measureFilters[columns[i].name]){//已过滤
				addFilteredPic(columns[i].name, "measure", columns[i].indexVerId);
			}else{//未过滤
				addFilteringPic(columns[i].name, "measure", columns[i].indexVerId);
			}
			addMeasureFilterPic(columns[i].name, columns[i].indexVerId);
		
		}
	}
}
function addDimDelete(columnName){
	/* var img = $("td[columnname="+columnName+"]").find("div:first").find('img[id=' + columnName + 'Dim]');
	if(img.length == 0){
		$("td[columnname="+columnName+"]").find("div:first")
		.prepend("<img id='" + columnName + "Dim' style='' class='l-grid-dim-filter' src='../../../images/classics/template/delete.png' onclick='deleteDim(this,\""+ columnName +"\")'>&nbsp;</img>");	
	} */
	var img = $("td[columnname="+columnName+"] > #delBtn");
	if(img.length == 0){
		var $selectedLi = $("td[columnname="+columnName+"]");
		var newImg = $("<img src='../../../images/classics/icons/icons_label_cross.png' style='width:7px;height:7px' class='l-grid-dim-filter' />").attr('title', '删除');
		newImg.click(function(){
			deleteDim(this, columnName);
		});
	    var div = $("<div id='delBtn' style='display:block;position: relative;clear: both;cursor: pointer;z-index:5;'/>");
	    div.css("left", (($selectedLi.width() + 2 - 9)/2)).css("top", 0 - $(".l-panel-topbar").height() - $selectedLi.position().top + 10);
	    div.append(newImg);
	    $selectedLi.append(div);
	}
}
function addMeasureFilterPic(columnName, indexVerId){
	var img = $("td[columnname="+columnName+"] > #delBtn");
	if(img.length == 0){
		//$("td[columnname="+columnName+"]").find("div:first")
		//.prepend("<img id='" + columnName + "Measure' style='' class='l-grid-dim-filter' src='../../../images/classics/template/delete.png' onclick='deleteIdx(this,\""+ columnName +"\")'>&nbsp;</img>");
		var $selectedLi = $("td[columnname="+columnName+"]");
		var newImg = $("<img src='../../../images/classics/icons/icons_label_cross.png' style='width:7px;height:7px;' class='l-grid-dim-filter' />").attr('title', '删除');
		newImg.click(function(){
			deleteIdx(this, columnName);
		});
	    var div = $("<div id='delBtn' style='display:block;position: relative;clear: both;cursor: pointer;z-index:5;'/>");
	    div.css("left", (($selectedLi.width() + 2 - 9)/2)).css("top", 0 - $(".l-panel-topbar").height() - $selectedLi.position().top + 10);
	    div.append(newImg);
	    $selectedLi.append(div);
	}
}
function deleteDim(g, dimNo){
	var labels = labelPad.getLabel();
	for(var i=0;i<labels.length;i++){
		if(labels[i].id == dimNo){
			labelClick(labels[i]);
		}
	}
	
}

function addFilteringPic(dimNo, type, indexVerId){
	var img = $("td[columnname="+dimNo+"]").find("div:first").find('i[id=' + dimNo + ']');
	if(img.length == 0){
		if(type == "dim"){
			$("td[columnname="+dimNo+"]").find("div:first")
			.prepend("<i id='" + dimNo + "' class='l-grid-dim-filter iconfont icon-guolv101'  title='维度过滤' onclick='openDialog(this,\""+ dimNo +"\", \""+ indexVerId +"\")'>&nbsp;</i>")	
		}else{
			$("td[columnname="+dimNo+"]").find("div:first")
			.prepend("<i id='" + dimNo + "' class='l-grid-dim-filter iconfont icon-huilvhuansuan' title='单位选择' onclick='openDialogMeasure(this,\""+ dimNo +"\", \"" + indexVerId + "\")'>&nbsp;</i>")
		}
		
	}else{
		if(img.hasClass('l-grid-dim-filtered')){
			img.removeClass('l-grid-dim-filtered').addClass('l-grid-dim-filter');
		}
	}
}
function addFilteredPic(dimNo, type, indexVerId){
	var img = $("td[columnname="+dimNo+"]").find("div:first").find('i[id=' + dimNo + ']');
	if(img.length == 0){
		if(type == "dim"){
			$("td[columnname="+dimNo+"]").find("div:first")
			.prepend("<i id='" + dimNo + "' class='l-grid-dim-filter iconfont icon-guolv101'  title='维度过滤' onclick='openDialog(this,\""+ dimNo +"\")'>&nbsp;</i>")
		}else{
			$("td[columnname="+dimNo+"]").find("div:first")
			.prepend("<i id='" + dimNo + "' class='l-grid-dim-filter iconfont icon-huilvhuansuan'  title='单位选择' onclick='openDialogMeasure(this,\""+ dimNo +"\", \""+ indexVerId +"\")'>&nbsp;</i>")
		}
	}else{
		if(img.hasClass('l-grid-dim-filter')){
			img.removeClass('l-grid-dim-filter').addClass('l-grid-dim-filtered');
		}
	}
}
function renderDimFilterPic(dimNo){
	var img = $("td[columnname="+dimNo+"]").find('img[id=' + dimNo + ']');
	if(img.hasClass('l-grid-dim-filter')){
		img.removeClass('l-grid-dim-filter').addClass('l-grid-dim-filtered').attr('src', '../../../images/classics/icons/filter.ico');
	}
}
function showDimFilter(columnName){
	var dims = dimFilters[columnName];
	if(dims){
		var tree = $("#Tree" + columnName).ligerTree({
			data : dims.selectedNodes,
			checkbox : false
		});	
		tree.expandAll();
		$("#Tree" + columnName).width($("#Tip" + columnName).width());
	}
}
function initTip(){
	$(".l-grid-hd-cell-inner").live('mouseover.tip', function ()
    {
		var columnValue = $(this).parent().attr("columnname");
        $("#Tip" + columnValue).css("visibility","visible");
    }).live('mouseout.tip', function ()
    {

    	var columnValue = $(this).parent().attr("columnname");
        $("#Tip" + columnValue).css("visibility","hidden");
    });
}
function initTree() {
	
	var setting ={
			async:{
				enable:true,
				url:"${ctx}/report/frame/idx/getAsyncTreeIdxShow.json",
				autoParam:["nodeType", "id", "indexVerId"],
				otherParam:{'isShowIdx':'1','isShowDim':1, 'isShowMeasure':1,'isPublish':'1', "isAuth" : "1", "showEmptyFolder":1},
				dataType:"json",
				dataFilter:function(treeId,parentNode,childNodes){
					if(childNodes){
						var newChildNodes = [];
						for(var i = 0;i<childNodes.length;i++){
							childNodes[i].nodeType = childNodes[i].params.nodeType;
							childNodes[i].indexVerId = childNodes[i].params.indexVerId;
							if(childNodes[i].nodeType == "idxInfo" && childNodes[i].params.haveMeasure != "true" || childNodes[i].nodeType == "measureInfo"){
							}else{
								childNodes[i].chkDisabled = true;
							}
								
							if($("#treeSearchInput").val() && childNodes[i].nodeType == "idxInfo"){
								
								if(childNodes[i].text.indexOf($("#treeSearchInput").val()) >= 0){
									newChildNodes.push(childNodes[i]);
								}										
							}else{
								newChildNodes.push(childNodes[i]);
							}
						}
					}
					return newChildNodes;
				}
			},
			check : {
				chkStyle : "checkbox",
				chkboxType :　{"Y":"", "N":""},
				enable : true
			},
			data:{
				key:{
					name:"text"
				}
			},
			view:{
				selectedMulti:false
			},
			callback:{
				onCheck : zTreeOnCheck
				//onClick : zTreeOnClick
			}
	};
	leftTreeObj = $.fn.zTree.init($("#tree"), setting,[]);
}

function zTreeOnCheck(event, treeId, treeNode){
	if(treeNode.checked == true){
		zTreeOnClick(event, treeId, treeNode);
	}else{
		if(treeNode.params.nodeType == "idxInfo"){
			deleteIdx(null, treeNode.id);
		}else if(treeNode.params.nodeType == "measureInfo"){
			var parent = treeNode.getParentNode();
			deleteIdx(null , parent.id + "-" + treeNode.id);
		}
		
	}
}
function zTreeOnClick(event, treeId, treeNode, clickFlag){
	if (treeNode.level > 0 && treeNode.params && ("idxInfo" == treeNode.params.nodeType && "05" != treeNode.data.indexType || "measureInfo" == treeNode.params.nodeType))  {
		$("#cover").hide();
		addIdx(treeNode);
	}
}
function deleteIdx(base, indexNo){
	
	var i = 0;
	for(i=0;i<columns.length;i++){
		if(columns[i].name == indexNo){
			break;
		}
	}
	columns.splice(i, 1);
	for(i=0;i<chooseIdx.length;i++){
		if(chooseIdx[i].params.indexNo == indexNo || ((chooseIdx[i].params.indexNo + "-" + chooseIdx[i].id) == indexNo)){
			break;
		}
	}
	leftTreeObj.checkNode(chooseIdx[i], false, false, false);
	chooseIdx.splice(i, 1);
	if(chooseIdx.length == 0){
		columns = [];
		labelPad.removeAllLable();
		grid.set('columns', columns); 
		grid.reRender();
		$("#cover").show();
	}else{
		dimIntersections();//重新计算
		renderLabelPad();
		setGridColumns();
	}
	/* grid.set('columns', columns); 
	grid.reRender(); */
	
	
}
function dimIntersections(){//维度取交集
	if(chooseIdx.length > 0){
		dimNos = chooseIdx[0].params.dimNos.split(",");
		for(var i=1;i<chooseIdx.length;i++){
			var j=0;
			for(j=0;j<dimNos.length;j++){
				if(chooseIdx[i].params.dimNos.indexOf(dimNos[j])<0){
					break;
				}
			}
			dimNos.splice(j, 1);
		}
	}
	
}

function addIdx(treeNode) {
	
	if(treeNode.params.nodeType == "measureInfo"){
		var measureNode = treeNode;
		treeNode = treeNode.getParentNode();
		measureNode.params.dimNos = treeNode.params.dimNos;
		chooseIdx.push(measureNode);
	}else{
		chooseIdx.push(treeNode);
	}
	
	//构造维度
	if(dimNos.length == 0){
		dimNos = treeNode.params.dimNos.split(",");
	}else{
		for(var i=0;i<dimNos.length;i++){
			if(treeNode.params.dimNos.indexOf(dimNos[i]) < 0){
				dimNos.splice(i, 1);
				i--;
			}
		}
	}
	renderLabelPad();
	
	//构造表格列
	if(chooseIdx.length == 1){
		columns.push({
			isSort: true,
			display: "日期",
			name: 'DATE',
			text : '日期',
			type : 'date',
			businessType : 'DIM',
			width: 120
		});
		columns.push({
			isSort: true,
			display: "机构",
			name: 'ORG',
			text : '机构',
			businessType : 'DIM',
			width: 120
		});
		/* columns.push({
			isSort: true,
			display: "币种",
			name: 'CURRENCY',
			text : '币种',
			businessType : 'DIM',
			width: '120px'
		});
		dimCurrencyExist = true; */
	}
	
	/* if(treeNode.params.dimNos.indexOf("CURRENCY") < 0){//不含币种
		columns.splice(2,1);//删除币种列
		dimCurrencyExist = false;
	} */
	//移除掉非公共的维度列
	for(var x =0;x<columns.length;x++){
		if(columns[x].businessType == 'DIM'){
			var y = 0;
			for(y=0;y<dimNos.length;y++){
				if(columns[x].name == dimNos[y]){
					break;
				}
			}
			if(y >= dimNos.length){
				columns.splice(x,1);
				x--;
			}
		}
	}
	
	for(var i=0;i<columns.length;i++){
		if(columns[i].businessType == "INDEX" || columns[i].businessType == "MEASURE"){
			columns[i].display = columns[i].text 
					+ (measureFilterName[columns[i].name] == null ? "" : "(" + measureFilterName[columns[i].name] + ")");
		}
	}
	
	if(measureNode){//有度量
		columns.push({
			isSort: true,
			display: (treeNode.text  + "." + measureNode.text)
					+ (measureFilterName[treeNode.params.indexNo + "." + measureNode.id] == null ? "" : "(" + measureFilterName[treeNode.params.indexNo + "." + measureNode.id] + ")"),
			name: (treeNode.params.indexNo + "-" + measureNode.id),
			indexVerId : treeNode.params.indexVerId,
			businessType : 'MEASURE',
			type : "float",
			text : treeNode.text + "." + measureNode.text,
			width: ((treeNode.text + "." + measureNode.text + 5).length * 12 > 120)? (treeNode.text + "." + measureNode.text + 5).length * 12 : 120
		});
	}else{
		columns.push({
			isSort: true,
			display: treeNode.text
					+ (measureFilterName[treeNode.params.indexNo] == null ? "" : "(" + measureFilterName[treeNode.params.indexNo] + ")"),
			name: treeNode.params.indexNo,
			indexVerId : treeNode.params.indexVerId,
			businessType : 'INDEX',
			type : "float",
			text : treeNode.text,
			width: ((treeNode.text.length + 5)* 12 > 120)? (treeNode.text.length + 5)* 12  : 120
		});
	}
	setGridColumns();
	/* grid.set('columns', columns); 
	grid.reRender(); */
	
}
function setGridColumns(){
	/* var dimColumns = [];
	var measureColumns = [];
	for(var i=0;i<columns.length;i++){
		if(columns[i].businessType == "DIM"){
			dimColumns.push(columns[i]);
		}else{
			measureColumns.push(columns[i]);
		}
	}
	var newColumns = [{display : "维度", columns : dimColumns}, {display : "度量", columns : measureColumns}]; */
	grid.set("columns", columns);
	grid.reRender();
	
}
function renderLabelPad(){//重绘维度标签区
	labelPad.removeAllLable();
	var params = "";
	for(var i=0;i<dimNos.length;i++){
		params += dimNos[i] + ",";
	}
	$.ajax({
		url: '${ctx}/report/frame/datashow/idx/getDimNm',
		type: 'post',
		data: {
			dimNos: params
		},
		dataType: 'json',
		beforeSend : function() {
			BIONE.loading = true;
			BIONE.showLoading("正在加载数据中...");
		},
		complete : function() {
			BIONE.loading = false;
			BIONE.hideLoading();
		},
		success: function(json) {
			//for(var j=0;j<10;j++){
			for(var i=0;i<dimNos.length;i++){
				if(dimNos[i] != "INDEXNO"){
					if(!(dimNos[i] == "DATE" || dimNos[i] == "ORG" )){
						labelPad.addLabel({
							flag : false,
							//css : { "background-color" : "#417EB7", "color" :"#333"},
							id :　dimNos[i],
							text : json[dimNos[i]],
							onClick : labelClick
						});
					}else{
						labelPad.addLabel({
							flag : true,
							//css : { "background-color" : "#DFDFDF", "color" :"#333"},
							id :　dimNos[i],
							text : json[dimNos[i]],
							onClick : labelClick
						});
					}
				}
			}
			//}
			for(var x=0;x<columns.length;x++){
				for(var y=0;y<dimNos.length;y++){
					if(columns[x].name == dimNos[y]){
						if(labelPad.getLabel("id", columns[x].name).length > 0){
							labelPad.getLabel("id", columns[x].name)[0].flag = true;
						}
						break;
					}
				}
			}
			reSortDim();
			
		},
		error : function(result, b) {
			//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
		}
	});
	
}
function labelClick(e){
	if(!(e.id == "DATE" || e.id == "ORG" )){
		e.flag = !e.flag;
		if(e.flag == true){
		//	e.setCss({ "background-color" : "#DFDFDF", "color" :"#333"});
			var i=0;
			for(i=0;i<columns.length;i++){
				if(columns[i].businessType == "INDEX"){
					break;
				}
			}
			columns.splice(i,0,{
				isSort: true,
				display: e.text,
				name: e.id,
				text : e.text,
				businessType : 'DIM',
				width: ((e.text.length + 5)* 12 > 120)? (e.text.length + 5) * 12  : 120
			});
		}else{
		//	e.setCss({ "background-color" : "#417EB7", "color" :"#333"});
			var i=0;
			for(i=0;i<columns.length;i++){
				if(columns[i].name == e.id){
					break;
				}
			}
			columns.splice(i,1);
			//删除所选维度过滤
			delete dimFilters[e.id];	
		}
		
		for(var i=0;i<columns.length;i++){
			if(columns[i].businessType == "INDEX"){
				columns[i].display =  columns[i].text 
						+ (measureFilterName[columns[i].name] == null ? "" : "(" + measureFilterName[columns[i].name] + ")");
			}
		}
		
		setGridColumns();
		/* grid.set('columns', columns); 
		grid.reRender(); */
		reSortDim();
	}
	
}
function reSortDim(){
	//维度区重新排序
	var labelTrue = labelPad.getLabel("flag", true);
	var labelFalse = labelPad.getLabel("flag", false);
	labelPad.removeAllLable();
	for(var i=0;i<labelTrue.length;i++){
		labelTrue[i].setCss({ "background-color" : "#DFDFDF", "color" :"#333"});
		labelPad.addLabel(labelTrue[i]);
		
	}
	for(var i=0;i<labelFalse.length;i++){
		labelFalse[i].setCss({ "border" : "1px dotted #D4D4D4", "color" :"#999"});
		labelPad.addLabel(labelFalse[i]);

	}
}
function openDialogMeasure(g, indexNo, indexVerId){
	chooseIndexNo = indexNo;
	for(var i=0;i<chooseIdx.length;i++){
		if(chooseIdx[i].params.indexNo == indexNo || ((chooseIdx[i].params.indexNo + "-" + chooseIdx[i].id) == indexNo)){
			var parent = chooseIdx[i].getParentNode();
			if(chooseIdx[i].params.dataType == "01" || parent.params.dataType == "01"){
				sl1.selectValue(measureFilters[indexNo]);
				dl1 = $.ligerDialog.open({ isHidden : true, title : "单位选择", target: $("#target1") });
				return;
			}else if(chooseIdx[i].params.dataType == "02" || parent.params.dataType == "02"){
				sl2.selectValue(measureFilters[indexNo]);
				dl2 = $.ligerDialog.open({ isHidden : true, title : "单位选择", target: $("#target2") });
				return;
			}
		}
	}
	
	/* commonOpenDialog("单位选择", "chooseOrg", 400, 250, 
			$(g).parent().parent().offset().left + $(g).parent().parent().width()/2, 
			$(g).parent().parent().offset().top + $(g).parent().parent().height(), 
			"${ctx}/report/frame/datashow/idx/measureFilter?indexNo=" + indexNo + "&indexVerId=" + indexVerId); */
}
function openDialog(g, dimNo){
	if(dimNo != "DATE"){
		commonOpenDialog("维度过滤", "chooseOrg", 400, 300, 
				$(g).parent().parent().offset().left + $(g).parent().parent().width()/2, 
				$(g).parent().parent().offset().top + $(g).parent().parent().height(), 
				"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=" + dimNo );
	}else{
		commonOpenDialog("维度过滤", "chooseOrg", 570, 320, 
				$(g).parent().parent().offset().left + $(g).parent().parent().width()/2, 
				$(g).parent().parent().offset().top + $(g).parent().parent().height(), 
				"${ctx}/report/frame/datashow/idx/dimFilter?dimNo=" + dimNo );
	}
}
function commonOpenDialog(title, name, width,
		height, left, top, url, comboxName, beforeClose){
	BIONE.commonOpenDialog(title, name, width,
			height, url, comboxName, beforeClose);
}
function initGrid(columns){
	grid = $('#maingrid').ligerGrid({
		toolbar : {},
		//columnWidth : '120px',
		width : '100%',
		height : $(document).height()- $("#col1").parent().parent().height() - 15,
		columns : columns,
		usePager: true,
		checkbox: false,
		dataAction : 'local',
		//colDraggable : true,
		allowHideColumn: false,
		onBeforeShowData : function(){
			addDivToColumn();
		},
		onAfterChangeColumnWidth : function(){
			ajustColumnWidth();
		},
		onAfterShowData : function(){
			//addDivToColumn();
		},
		onBeforeChangeColumnWidth : function(){
		},
		mouseoverRowCssClass : null
	});
	var buttons = [];
	
	buttons.push({
		text : '<i class="iconfont icon-circlerun" title="运行" style="color:#666666;"></i>',
		click : indexQuery
	});
	
	buttons.push({
		text : '<i class="iconfont icon-excel" title="导出excel" style="color:#666666;"></i>',
		click : function(){
			if(queryResult && queryResult.length > 0){
				var src = '';

				src = "${ctx}/report/frame/datashow/idx/resultExport";
				downdload.attr('src', src);
				
			}else{
				BIONE.tip("查询出错，不允许保存");
			}
		}
	});
	/* buttons.push({
		text : '<i class="iconfont icon-drivepdf"  title="导出pdf" style="color:#003366;"></i>',
		click : function(){
			
		}
	}); */
	BIONE.loadToolbar(grid, buttons);
}
function ajustColumnWidth(){
	for(var i=0;i<columns.length;i++){
		var div = $("td[columnname="+columns[i].name+"] > #delBtn");
		var $selectedLi = $("td[columnname="+columns[i].name+"]").find("div:first");
		div.css("left", (($selectedLi.width() + 2 - 9)/2)).css("top", 0 - $(".l-panel-topbar").height() - $selectedLi.position().top + 10);	
	}
}
function initCSS(){
	 $("#layout1").ligerLayout({
		 topHeight : 90,
		 onEndResize : function(param, e){
			 $("#col1").height($("#col1").height() + param.diff);
			 grid.setHeight($(document).height()- $("#col1").parent().parent().height() - 15);
			 $("#winparent").height( $("#winparent").height() + param.diff);
		 },
		 onHeightChanged : function(e){
		 }
    });
	
	
}
function clone(obj){
	var o;
	switch(typeof obj){
	case 'undefined': break;
	case 'string'   : o = obj + '';break;
	case 'number'   : o = obj - 0;break;
	case 'boolean'  : o = obj;break;
	case 'object'   :
		if(obj === null){
			o = null;
		}else{
			if(obj instanceof Array){
				o = [];
				for(var i = 0, len = obj.length; i < len; i++){
					o.push(clone(obj[i]));
				}
			}else{
				o = {};
				for(var k in obj){
					o[k] = clone(obj[k]);
				}
			}
		}
		break;
	default:		
		o = obj;break;
	}
	return o;	
}
</script>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">指标树</span>
	</div>
	
	<div id="template.right" >
		<div id='cover' class='l-tab-loading' style='display:block; background:url(${ctx}/images/classics/index/index.jpg) no-repeat center center #ffffff;'></div>
		<!-- <div id="layout" style="height: 100%;"> -->
		<!-- <div position="center" style="height : 100%"> -->
		<div id="container" style="height : 100%">
			<div id="content" style="height : 100%">
			<div id="layout1">
				<div position="top" style="overflow-y: auto;">
				<div id="col1" class="frame" style="height : 100%;overflow-y:hidden;margin : 2px; ">
				<!-- <h3 id='title3'>共有维度   <span ><i style="color:#003366;" class="iconfont icon-wenhao" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</i></span></h3> -->
				<div class="l-form-tabs">
					<ul original-title="" class="ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all">
					<li class="ui-state-default ui-corner-top ui-tabs-selected ui-state-hover" data-index="0">
					<a href="javascript:void(0)"><h3 id='title3'>共有维度   <span ><i style="color:#666666;" class="iconfont icon-wenhao" ></i></span></h3></a>
					</li>
					</ul>
					<div class="ui-tabs-panel ui-widget-content ui-corner-bottom" id="winparent" data-index="0">
					<div class="win" style="">
						<div id="selected" class="content" style="overflow: hidden;"></div>
					</div>
					</div>
					</div>
					
				</div>
				</div>
				<!-- <div id="advance" style="height: 21px; text-align: center; line-height: 20px; display: none;background: #F1F1F1; border-bottom:solid 1px #D6D6D6; ">
  					<div id="extend" style="height: 20px; width:100px; MARGIN-RIGHT: auto; MARGIN-LEFT: auto; background: #FFF;border-left: solid 1px #CCC; border-right: solid 1px #CCC;border-bottom: solid 1px #CCC; cursor: pointer; position: relative; top: -1px;">
  					<strong><i class="iconfont icon-xiangxiazhankai"></i></strong>
  					</div>
				</div> -->
				<!-- 	表格 -->
				<div position="center">
				<div id="table" class="frame" style="width:100%;">
					<div class="win" style=" margin-top: 2px;">
						<div id="maingrid" class="maingrid"></div>
					</div>
				</div>
				</div>
			</div>
			</div>
			</div>
			
		<!-- </div> -->
		<!-- <div id="button" position="right">
			<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
			</div>
		</div> -->
		<!-- </div> -->
		<div id="target1" style="width:200px; margin:3px; display:none;">
    <h3>请选择</h3>
    <div>
        <input type="text" id="sl1">
        </input>
    </div>
 	</div>
 	 <div id="target2" style="width:200px; margin:3px; display:none;">
    <h3>请选择</h3>
    <div>
        <input type="text" id="sl2"></input>
    </div>
 	</div>
	</div>
	
	
	
</body>
</html>