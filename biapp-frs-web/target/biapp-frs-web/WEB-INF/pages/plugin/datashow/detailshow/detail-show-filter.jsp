<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script type="text/javascript" src="${ctx}/js/uuid/uuid.js"></script>
<script type="text/javascript">
	var currentNode = null;
	var filter = null;
	var colInfos = window.parent.colInfos;
	var popuptree= {};
	$(function() {
		initFilter();
		initButtons();
	});

	function initFilter(){
		
		var fields = [];
		for(var i in colInfos){
			var colInfo = colInfos[i];
			var field = {
				display : colInfo.displayNm,
				name : colInfo.colId
			};
			if(colInfo.dimTypeNo){
				if(colInfo.dimTypeNo == "DATE"){
					field.editor = {
						type : "date",
						options: { 
							format: 'yyyyMMdd'
						}
					}
				}
				else if(colInfo.dimTypeNo == "ORG"){
					var enNm = colInfo.enNm + "|" + colInfo.colId;
					if( colInfo.dimTypeNo){
						enNm += "|"+colInfo.dimTypeNo;
					}
					field.editor = {
						type : "popup",
						options :{
							onButtonClick : onButtonClick,
							url : "/report/frame/datashow/idx/orgTree",
							ajaxType : "post",
							attr : {
								key : "ORG"
								
							}
						},
						attr :{
							tree :"{'checkbox':false,'parentIDFieldName':'upId','treeLeafOnly':false}",
							//key : uuid.v1(),
							key : enNm,
							options : "{'url':'/report/frame/datashow/idx/orgTree','ajaxType':'post'}"
						}
					}
					
				}
				else{
					field.editor = {
						type : "combobox",
						options :{
							url : "${ctx}/report/frame/design/paramtmp/getTreeDimItems?dimTypeNo="+colInfo.dimTypeNo
						}
					}
				}
			}
			else{
				if(colInfo.dataType == "01"){
					field.editor = {
						type : "string"
					}
				}
				if(colInfo.dataType == "02"){
					field.editor = {
						type : "number",
						options :{
							decimalplace : 2
						}
					}
				}
				if(colInfo.dataType == "03"){
					field.editor = {
						type : "date",
						options: { 
							format: 'yyyyMMdd'
						}
					}
				}
			}
			fields.push(field);
		}
		filter = $("#filter").ligerFilter({
			fields: fields,
			operators :{
				"string" : [{id:"=",text:"相等"},{id:"!=",text:"不相等"},{id:"like",text:"相似"}],
				"date" : [{id:"=",text:"相等"},{id:">=",text:"大于等于"},{id:">",text:"大于"},{id:"<",text:"小于"}, {id:"<=",text:"小于等于"}],
				"number" : [{id:"=",text:"相等"},{id:"!=",text:"不相等"},{id:">=",text:"大于等于"},{id:">",text:"大于"},{id:"<",text:"小于"}, {id:"<=",text:"小于等于"}],
				"combobox" : [{id:"=",text:"相等"},{id:"!=",text:"不相等"}],
				"popup" : [{id:"=",text:"相等"},{id:"!=",text:"不相等"}]
			}
		});
		if(window.parent.filterInfo != null && window.parent.filterInfo.op){
			filter.setData(window.parent.filterInfo);
		}
	}
	
	function onButtonClick(){
		var $this = $(this), id = $this.attr("id"), $ele = $.ligerui.get(id).valueField, puuid = $ele.attr("puuid"), options = $ele.attr("options"), dialog = $ele.attr('dialog'), dw = 220, dh = 300;
		if (window['popuptree'][id]) {
			window['popuptree'][id].destroy();
		}
		window['popuptree'][id] = $.ligerDialog.open({
			id: 'popuptreeDialog',
			title: '请选择',
			url: '${ctx}/report/frame/param/templates/popupTree?id=' + id,
			width: "300",
			height: "400",
			isResize: false,
			allowClose : false,
			onClosed: function () {
                for (var id in window['popuptree']) {
                    if (window['popuptree'].hasOwnProperty(id) && window['popuptree'][id]) {
                    	window['popuptree'][id].destroy();
                        delete window['popuptree'][id];
                    }
                }
            }
		});
	}
	
 	function initButtons(){
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : function(){
				BIONE.closeDialog("filterEdit");
			}
		});
		buttons.push({
			text : '确认',
			onclick : function(){
				f_save();
			}
		});
		BIONE.addFormButtons(buttons);
	}
 	
 	function f_save(){
 		window.parent.filterInfo =filter.getData();
 		BIONE.closeDialog("filterEdit");
 	}
</script>
</head>
<body>
	<div id="template.center">
		<div style="border:1px solid #d3d3d3;background-color:white;height:99%;overflow-y:auto">
			<div id="filter" ></div>
		</div>
		
	</div>
</body>
</html>