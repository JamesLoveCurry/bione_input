<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">
    var leftTreeObj=null;
    var   rowId  =  '${rowId}';
    var   dimTypeNo = '${dimTypeNo}';
    var isSum = '${isSum}';
    var dimTypeStruct = '${dimTypeStruct}';
    var srcIndexNo = '${srcIndexNo}';
    var mainform = null;
    var flag = true;
    
    // 行列维度过滤信息
    var rowColDims = [];
    
    function initForm() {
		mainform = $("#mainform").ligerForm({
			labelWidth : 120,
			inputWidth  : 150,
			fields : [ {
				display : "维度过滤类型",
				name : "filterMode",
				type : "select",
				width: '70',
				comboboxName: "filterModeBox",
				options : {
					data : [ {
						id : '01',
						text : '包含'
					}],
					onSelected: function(value){
						
					}
				}
			}]
		});
// 		 $("#radio").ligerRadioList(
// 				 {
// 					 data: [
// 					        {"id":"0","text":"否"},
// 					        {"id":"1", "text":"是"}
// 					        ],
// 					 textField: 'text' });
		
		$("#filterModeBox").css("color","#333");
		$("#filterModeBox").parent().parent().parent().after("<li><div id='info' style='width:320px;margin-left:20px'></div></li>");
		
// 		liger.get("radio").setValue("0");
		
// 		$("input[name='radio']").live("click", function(){
// 			if($(this).val() == "1"){
// 				initTree(true);
// 			}
// 			if($(this).val() == "0"){
// 				initTree(false);
// 			}
// 		});
		var filterMode = window.parent.getItemFilterMode(dimTypeNo);
    	if(filterMode != null && !filterMode == ""){
    		liger.get("filterModeBox").selectValue(filterMode);
    	}else{
    		liger.get("filterModeBox").selectValue("01");
    	}
	}
    function initContext(){
    	var  children   =  leftTreeObj.getNodes();
    	var rowColObj = window.parent.getRowColFiltInfos(dimTypeNo);
    	if(rowColObj
    			&& rowColObj.rowColArr
    			&& rowColObj.rowColArr.length){
    		rowColDims = rowColObj.rowColArr;
    	}
    	if(rowColDims.length > 0){
	    	for(var i=0;i<rowColDims.length;i++){
	    		// 渲染 行列过滤 勾选
	    		leftTreeObj.checkNode(leftTreeObj.getNodeByParam("id", rowColDims[i], null), true, false);
	    	}
	    	// 行列过滤生效时，不能更改维度过滤值和类型
	    	$.fn.zTree.getZTreeObj("tree").setting.callback.beforeCheck = function(){
	    		return false;
	    	}
	    	$.ligerui.get("filterModeBox").setDisabled();
	    	$.ligerui.get("filterModeBox").setValue(rowColObj.filterModel);
			$("#filterModeBox").css("color", "#333").attr("readOnly", "true")
    	}else{    		
	    	var itemArr  =  window.parent.getItemArr(dimTypeNo);
	    	for(var i=0;i<itemArr.length;i++){
	    		// 渲染 具体的维度类型过滤 勾选
	    		leftTreeObj.checkNode(leftTreeObj.getNodeByParam("id", itemArr[i], null), true, false);
	    	}
	   	}
    	if("${isDict}"!=""){
    		$.fn.zTree.getZTreeObj("tree").setting.callback.beforeCheck = function(){
	    		return false;
	    	}
    	}
	}
    
	function selectButton() {
		var buttons = [];
		buttons.push({
			text : '确定',
			onclick : selected
		});
		if("${isDict}"=="")
			BIONE.addFormButtons(buttons);
	}
	function selected(){
		if(!rowColDims
				|| !rowColDims.length 
				|| rowColDims.length <= 0){
			var   selectedIds =[];
	        var   checkInfoObj = getCheckInfo();
	        selectedIds  =  checkInfoObj.filterVal;
	        /* if(selectedIds>2){
	        	andSoOn="等";
	        } */
	        //returnStr = checkInfoObj.filterText.slice(0,2).join(";");
	        //finalStr  =   headStr+andSoOn+checkInfoObj.filterVal.length+tailStr;
	        var filterMode = liger.get("filterModeBox").selectedValue;
				window.parent.updateItemArr({
					"dimNo":dimTypeNo,
					"filterVal":selectedIds.join(","),
					"filterMode" : filterMode
				});
			/* window.parent.updateFilterformulaArr({
					"dimTypeNo":dimTypeNo,
					"filterformula":checkInfoObj.filterformula
			}); */
			window.parent.updateDimItemValArr(rowId);
		}
		setTimeout('BIONE.closeDialog("rptCmpoIdxSeltTree");',0);
	}
	
	$(function() {
		initForm();
		initTree(true);
		selectButton();
		$("#treeContainer").height(
				$("#center").height() - $("#mainform").height() - 50 - $("#radio").height());
		$(".form-bar-inner").css("padding-top","22px"); 
		$("div.form-bar").height(40);
		
	})
	function initTree(isCon) {
		//树
		
		var setting = {
			check : {
				enable : true,
				chkStyle : "checkbox",
				chkboxType: { "Y": "", "N": "" }
			},
			data : {
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId"
				}
			},
			view : {
				selectedMulti : false
			},
			callback : {
				onCheck: zTreeOnCheck
			}
		};
		if(isCon){
			setting.check.chkboxType = { "Y": "s", "N": "s" };
		}else{
			setting.check.chkboxType = { "Y": "", "N": "" };
		}
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		/*添加报表业务类型，方便进行机构维度数据查询*/
		var busiType = window.parent.parent.busiType;
		if(busiType==undefined) busiType = window.parent.parent.parent.busiType;//业务类型查询异常处理
		loadTree("${ctx}/report/frame/idx/getDimInfoTree?dimTypeNo=${dimTypeNo}" + "&srcIndexNo=" + srcIndexNo + "&busiType=" + busiType, leftTreeObj);

	}
	function validateNode(node){
		node=node.getParentNode();
		if(node.id!="${treeRoot}"){
			if(node.getCheckStatus().checked==true){
				return false;
			}
			else{
				if(validateNode(node)==false){
					return false;
				}
			}
		}
		return true;
	}
	function getCheckInfo(){
		var nodes = leftTreeObj.getCheckedNodes(true);
		var checkid=[];
		var checktext=[];
		var viewformula="";
		var filterformula="";
		for(var i=0;i<nodes.length;i++){
			if(nodes[i].id!="${treeRoot}"){
				if(isSum=="Y"&&dimTypeStruct=="02"){
					if(validateNode(nodes[i])){
						checkid.push(nodes[i].id);
						checktext.push(nodes[i].text);
					}
				}
				else{
					checkid.push(nodes[i].id);
					checktext.push(nodes[i].text);
				}
				
			}
		}
		if(checkid.length>0){
			var dimNo="${dimTypeNo}";
			filterformula+="( ";
			for(var i in checkid){
				if(isSum=="Y"&&dimTypeStruct=="02"){
					if(leftTreeObj.getNodesByParam("id",checkid[i]).isParent!=false){
						filterformula+=dimNo+" like "+"'"+checkid[i]+"%'" ;
						if(i<checkid.length-1){
							filterformula+=" || ";
						}
					}
					else{
						filterformula+=dimNo+" == "+"'"+checkid[i]+"'" ;
						if(i<checkid.length-1){
							filterformula+=" || ";
						}
					}
				}
				else{
					filterformula+=dimNo+" == "+"'"+checkid[i]+"'" ;
					if(i<checkid.length-1){
						filterformula+=" || ";
					}
				}
			}
			filterformula+=" )";
		}
		return {
			filterVal: checkid,
			filterText: checktext,
			filterformula: filterformula
		};
	}
	
	function zTreeOnCheck(event, treeId, treeNode){
		if(treeNode.id == "${treeRoot}"){
			leftTreeObj.checkAllNodes(flag);
			flag = !flag;
		}
	}
	
	//加载树中数据
	function loadTree(url, component, data) {
		$.ajax({
			cache : false,
			async : true,
			url : url,
			dataType : 'json',
			data : data,
			type : "POST",
			beforeSend : function() {
				BIONE.showLoading();
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				var nodes = component.getNodes();
				var num = nodes.length;
				for ( var i = 0; i < num; i++) {
					component.removeNode(nodes[0], false);
				}
				if (result.length > 0) {
					/* for(var i in result){
						if(result[i].id != "#ytec_root#") 
							result[i].text = result[i].id+ "(" + result[i].text + ")";
					} */
					component.addNodes(null, result, false);
					component.expandNode(component.getNodeByParam("level","0"));
				}
				initContext();
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<form id="mainform"></form>
<!-- 		<div > -->
<!-- 			<div style="float: left;width: 15%;margin-left: 7px;">是否级联：</div> -->
<!-- 			<div id="radio"></div> -->
<!-- 		</div> -->
		<div id="left" style="background-color: #FFFFFF">
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="bottom">
			<div class="form-bar">
				<div id="bottom" style="padding-top: 5px"></div>
			</div>
		</div>
	</div>
	
</body>
</html>