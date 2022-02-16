<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template14.jsp">
<head>
<script type="text/javascript">

    var mainform=null,
    	   treeObj = null,
    	   checkedVal = "",
    	   filtCfgId = "",
    	   dimTypeStruct = "${dimTypeStruct}";
    
    $(function(){
    	var height = $(window).height();
		$("#content").height(height - 58);
		$("#treeContainer").height(height - 40 - $("#mainform").height() - 8);
		if(dimTypeStruct == ""
				|| dimTypeStruct == null
				|| typeof dimTypeStruct == "undefined"){
			if(window.parent.treeObj){
				var selNodes = window.parent.treeObj.getSelectedNodes();
				if(selNodes != null
						&& selNodes.length > 0
						&& selNodes[0].params.type === "dimTypeInfo"
						&& selNodes[0].data){
					dimTypeStruct = selNodes[0].data.dimTypeStruct;
				}
			}
		}
    	initForm();
    	initTree();
    });
    
    function initForm() {
		mainform = $("#mainform").ligerForm({
			fields : [ {
				display : "过滤类型",
				name : "filterMode",
				type : "select",
				width: '100',
				height:'10',
				comboboxName: "filterModeBox",
				options : {
					data : [ {
						id : '01',
						text : '包含'
					}/* , {
						id : '02',
						text : '不包含'
					} */]
				}
			} ]
		});
		if(window.parent.checkedTypeVals
				&& window.parent.checkedTypeVals["${dimTypeNo}"]
				&& window.parent.checkedTypeVals["${dimTypeNo}"].filterModelVal){
			$.ligerui.get('filterModeBox').selectValue(window.parent.checkedTypeVals["${dimTypeNo}"].filterModelVal);
		}else{			
			$.ligerui.get('filterModeBox').selectValue("01");
		}
		$("#filterModeBox").parent().parent().parent().after("<li><div id='info' style='display:none;width:320px;margin-left:20px'></div></li>");
	}
    
    function initTree() {
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
		treeObj = $.fn.zTree.init($("#tree"), setting);
		loadTree(
				"${ctx}/report/frame/idx/getDimInfoTree?dimTypeNo=${dimTypeNo}&srcIndexNo=${idxNos}",
				treeObj);
		
	}
    
    function zTreeOnCheck(event , treeId , treeNode){
    	if(treeNode.id=="#ytec_root#"){
			treeObj.checkAllNodes(treeNode.checked);
		}
		var checkInfo=getCheckInfo();
		manageCheckData(checkInfo);
    }
    
    function manageCheckData(checkInfo){
    	if(!checkInfo){
    		return ;
    	}
    	filtCfgId = checkInfo.cfgId;
    	var filterVals = checkInfo.checkedVal;
		if(filterVals){			
			if(filterVals instanceof Array
					&& typeof filterVals.length != "undefined"){
				checkedVal = filterVals.join(",");
			}else if(typeof filterVals == "string"){
				checkedVal = filterVals;
			}
		}
		var filters = checkInfo.filterText;
		if(filters){
			if(filters instanceof Array
					&& typeof filters.length != "undefined"){
				var info=filters.join(",");
				if(info.length>100){	
					info=info.substring(0,100)+"...";
				}
				if(info.length>0)
					$("#info").html("("+info+")");
				else
					$("#info").html("");
			}else if(typeof filters == "string"){
				$("#info").html(filters);
			}
		}
    }
    
    function validateNode(node){
		node=node.getParentNode();
		if(node.id!="${treeRoot}"){
			if(node.getCheckStatus().checked === true){
				return false;
			}
			else{
				if(validateNode(node) === false){
					return false;
				}
			}
		}
		return true;
	}
    
    function getCheckInfo(){
		var filterModeVal = $.ligerui.get('filterModeBox').getValue();
		var nodes = treeObj.getCheckedNodes(true);
		var checkid=[];
		var checktext=[];
		var viewformula="";
		var filterformula="";
		for(var i in nodes){
			if(nodes[i].id!="#ytec_root#"){
				if(dimTypeStruct=="02"){
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
		}
		return {
			checkedVal: checkid,
			filterText: checktext
		};
	}
    
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
				component.removeChildNodes(component.getNodeByParam("id",
						0, null));
				component.removeNode(component.getNodeByParam("id",
						0, null), false);
				if (result.length > 0) {
					/* for(var i in result){
						if(result[i].id != "#ytec_root#") 
							result[i].text = result[i].id+ "(" + result[i].text + ")";
					} */
					component.addNodes(null, result, false);
					component.expandNode(component.getNodeByParam("level",0));
				}
				if(window.parent.checkedTypeVals
						&& window.parent.checkedTypeVals["${dimTypeNo}"]){
					var filterTmp = window.parent.checkedTypeVals["${dimTypeNo}"];
					if(filterTmp.checkedVal
							&& typeof filterTmp.checkedVal == "string"){
						var itemNos = filterTmp.checkedVal.split(",");
						for(var i = 0 , l = itemNos.length ; i < l ; i++){
							component.checkNode(component.getNodeByParam("id",
									itemNos[i], null) , true , false);
						}
						manageCheckData(filterTmp);
					}
				}
			},
			error : function(result, b) {
				//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
    
    function generateFilterInfo(){
    	if(checkedVal == ""
    			|| checkedVal == null){
    		return null;
    	}
    	var dimTypeNo = "${dimTypeNo}";
    	var dimTypeNm = window.parent.$("li[tabid=" + dimTypeNo + "] a").text();
    	var filterModelVal = $.ligerui.get('filterModeBox').getValue();
    	var filterText = $("#info").html();
    	return {
    		dimTypeNo : dimTypeNo,
    		dimTypeNm : dimTypeNm,
    		checkedVal : checkedVal,
    		filterModelVal : filterModelVal,
    		filterText : filterText,
    		cfgId : filtCfgId ? filtCfgId : ""
    	};
    }
   
</script>
</head>
<body>
<div id="template.center">
		<form id="mainform" style="height:30px;"></form>
		<div id="left" style="background-color: #FFFFFF">
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree"
					style="font-size: 12; background-color: #FFFFFF; width: 92%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>