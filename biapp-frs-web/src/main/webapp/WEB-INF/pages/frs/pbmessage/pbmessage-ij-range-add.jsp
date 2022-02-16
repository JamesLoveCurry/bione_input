<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<%@ include file="/common/zTree_load.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/template/template12.css" />
<style type="text/css">
.searchbox {
	width: inherit;
	text-align: center;
	margin: auto;
	margin-top: 3px;
	margin-bottom: 5px;
	border-style: solid;
	border-top-width: 1px;
	border-bottom-width: 0px;
	border-left-width: 0px;
	border-right-width: 0px;
/* 	border-width: 0px; */
	border-color: #D6D6D6;
}
</style>
<script type="text/javascript">
    var verId = window.parent.verId;
	var treeObj;
	var checkNodes=[];
	var treeInfo=null;
	var cascade = false;
	var mainform;
	$(function() {
		initOrgCodeForm();
		initReportCodeTree();
		addButtons();
		initContentHeight();
		initTreeInfo();
		
	});
	
	function initOrgCodeForm() {
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [{
				name : "verId",
				type : "hidden"
			 },{
				name : "ids",
				type : "hidden"
			 },{
				display : "报送范围编号",
				name : "submitRangeCode",
				newline : true,
				width: '200',
				labelWidth:90,
				type : "text", 
				validate : { 
					required : true,
					maxlength : 32,
					remote : {
						url : "${ctx}/frs/pbmessage/validateRangeCode",
						type : "POST",
						data : {
							"id" : ""
						}
					},
					messages : {
						remote : "此编号已存在"
					}
				}
			},{
				display : "报送范围名称", 
				name : "submitRangeNm", 
				newline : true, 
				width: '200',
				labelWidth:90,
				type : "text", 
				validate : { 
					required : true,
					maxlength : 200,
					remote : {
						url : "${ctx}/frs/pbmessage/validateRangeNm",
						type : "POST",
						data : {
							"id" : ""
						}
					},
					messages : {
						remote : "此名称已存在"
					}
				}
			}
		]
		});
		$("#mainform input[name='verId']").val(verId);
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
	}
	
	function initReportCodeTree() {
		var setting = {
				check:{
					enable : true,
					chkboxType :{"Y":"","N":""},
					chkStyle :"checkbox"
				},
				async:{
					enable:true,
					url:"${ctx}/frs/pbmessage/getAddrTree?t="+ new Date().getTime(),
					autoParam:["id"],
					dataType:"json",
					dataFilter : function(treeId, parentNode, Nodes) {
						var childNodes = Nodes.commonTreeNodeList;
						if (childNodes) {
							for ( var i = 0; i < childNodes.length; i++) {
								if(checkNodes!=null&&checkNodes.length>0){
									for ( var l in checkNodes) {
										var exeobj = checkNodes[l];
										if(childNodes[i].id == exeobj.id){
											childNodes[i].checked=true;
											childNodes[i].ischecked = true;
										}
									}
								}
							}
							return childNodes;
						}
					}
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
					selectedMulti : true
				},
				callback : {
					onCheck:function(event, treeId, treeNode){
							if(cascade){
								if (treeNode.getCheckStatus().checked) {
									var node={};
									node.id=treeNode.id;
									node.text=treeNode.text;
									node.params=treeNode.params;
									if(getIndex(node,checkNodes)==-1)
										checkNodes.push(node);
									checkCascadeCheck(treeNode.id);
								}
								else{
									var node={};
									node.id=treeNode.id;
									node.text=treeNode.text;
									node.params=treeNode.params;
									var index=getIndex(node,checkNodes);
									if(index>=0){
										checkNodes.splice(index,1);
									}
									unCheckCascadeCheck(treeNode.id);
								}
							}
							else{
								if (treeNode.getCheckStatus().checked) {
									var node={};
									node.id=treeNode.id;
									node.text=treeNode.text;
									node.params=treeNode.params;
									if(getIndex(node,checkNodes)==-1)
										checkNodes.push(node);
								}
								else{
									var node={};
									node.id=treeNode.id;
									node.text=treeNode.text;
									node.params=treeNode.params;
									var index=getIndex(node,checkNodes);
									if(index>=0){
										checkNodes.splice(index,1);
									}
								}
							}
					}
				}
		};
		treeObj = $.fn.zTree.init($("#tree"), setting);
	}
	
	function getIndex(info,array){
		for(var i in array){
			if(array[i].id==info.id){
				return i;
			}
		}
		return -1;
	}
	
	function checkCascadeCheck(id){
		var nodes=treeInfo[id];
		if(nodes!=null&&nodes.length>0){
			for(var i in nodes){
				var node={};
				node.id=nodes[i].id;
				node.text=nodes[i].text;
				node.params=nodes[i].params;
				if(getIndex(node,checkNodes)==-1)
					checkNodes.push(node);
				checkCascadeCheck(node.id);
			}
		}
	}

	function unCheckCascadeCheck(id){
		var nodes=treeInfo[id];
		if(nodes!=null&&nodes.length>0){
			for(var i in nodes){
				var node={};
				node.id=nodes[i].id;
				node.text=nodes[i].text;
				node.params=nodes[i].params;
				var index=getIndex(node,checkNodes);
				if(index>=0){
					checkNodes.splice(index,1);
				}
				unCheckCascadeCheck(node.id);
			}
		}
	}
	
	function initTreeInfo(){
		$.ajax({
			cache : false,
			async : true,
			url :  "${ctx}/frs/pbmessage/getAllAddrTree?&d="+ new Date().getTime(),
			dataType : "json",
			type : "POST",
			success : function(result) {
				treeInfo=result.treeInfo;
			},
			error : function(result, b) {
			}
		});
		
	}
	function addButtons() {
		BIONE.addFormButtons([{
			text : '取消',
			width : '80px',
			onclick : cancelButton
		}, {
			text : '保存', 
			width : '80px',
			onclick : saveButton
		} ]);
	}
	
	//保存按钮
	function saveButton(){
		if(0 == checkNodes.length){
			BIONE.tip("请选择行政区域");
		}else{
			var ids=[];
			for(var i =0;i<checkNodes.length;i++){
				var id = checkNodes[i].params.orgNo + "-" + checkNodes[i].params.addrNo + "-" + checkNodes[i].params.financeCode;
				ids.push(id);
			}
			verId = '1';
			$("#ids").val(ids.join(","));
			$("#verId").val(verId);

			BIONE.submitForm($("#mainform"), function() {
				BIONE.closeDialogAndReloadParent("addRange", "maingrid", "保存成功");
			}, function() {
				BIONE.closeDialog("addRange", "保存失败");
			});
		}
	}
	
	function cancelButton(){
		BIONE.closeDialog("addRange");
	}
	
	function initContentHeight() {
		$('#content').height($(window).height() - $('#mainsearch').height() - $('#bottom').height() - 24);
		$('#treeContainer').height($('#content').height() - $('#bottom').height());
	}
	
	//是否级联选择
	function check() {
		cascade = $("#level1")[0].checked;
		if ($("#level1")[0].checked == true)
			treeObj.setting.check.chkboxType = {
				"Y" : "s",
				"N" : "ps"
			};
		else
			treeObj.setting.check.chkboxType = {
				"Y" : "",
				"N" : ""
			};
	}
</script>
</head>
<body>
	<div id="center" >
		<div id="mainsearch">
			<div id="searchbox" class="searchbox">
				<form id="mainform" action="${ctx}/frs/pbmessage/batchSaveRange" method="post"></form>
			</div>
		</div>
		<div id="content" class="content" style="border: 1px solid #D6D6D6; ">
			<div id="lefttable" width="100%" border="0">
				<div width="100%"
					style="height:30px;background-image: url(${ctx}/css/classics/ligerUI/Gray/images/ui/gridbar.jpg);border-bottom:1px solid #D6D6D6;">
					<div width="8%"
						style="padding-left: 10px; float: left; position: relative; height: 20px; margin-top: 7px">
						<img src="${ctx}/images/classics/icons/application_side_tree.png" />
					</div>
					<div width="30%">
						<span
							style="font-size: 12; float: left; position: relative; line-height: 30px; padding-left: 2px">
							<span id="treeTitle" style="font-size: 12">行政区域</span>
						</span>
					</div>
					<div width="60%"> 
						<div style="float: right; position: relative; padding-right: 3px; padding-top:4px;">
							 <div style="width:100%;height: 20px;"><div style="text-align: left;width:100%; padding-left: 10px;padding-top: 2px;">是否级联： 是 <input type="radio" id="level1"  name="level" value="level1" onclick=check() /> 否 <input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true"/></div></div>
						</div>
					</div> 
				</div>
			</div>
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
				<ul id="tree" style="font-size: 12; background-color: F1F1F1; width: 90%" class="ztree"></ul>
			</div>
		</div>
		
	</div>
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
	</div>
</body>
</html>