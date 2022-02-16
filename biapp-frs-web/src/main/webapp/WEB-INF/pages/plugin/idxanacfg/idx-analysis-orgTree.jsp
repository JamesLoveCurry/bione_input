<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var chartId = "${chartId}";
	var leftTreeObj = "";
	var btns =[]; 
	var checkEnable = true;
	var cascade = 2;
	
	$(function(){
		initTree();//初始化机构树
		initCombo();//得到机构组数据
		initTool();
		initBtn();//初始化按钮
	});
	
	function initCombo(){
		 $("#orggrp").ligerComboBox(
				{
					url : "${ctx}/cabin/analysis/orggroup/getorgGrpCombo?d="+ new Date().getTime(),
					ajaxType : "post",
					labelAligh : "center",
					slide : false,
					onSelected : function(val) {
						var data = {
							grpId : val
						};
						if(val != null && val != ""){
							$.ajax({
								cache : false,
								async : true,
								url : "${ctx}/rpt/frame/rptorggrp/getGrpOrgNos",
								type : 'POST',
								data : data,
								success : function (data){
									leftTreeObj.checkAllNodes(false);
									for(var i in data){
										var node = leftTreeObj.getNodeByParam("id", data[i], null);
										if(node){
											leftTreeObj.checkNode(node, true, false);
										}
									}
								},
								error : function (result,b){
								
								}
							});
						}
					}
				});

	}
	
	function initTool(){
		if(checkEnable == true){
			var radio = '<div style="width:100%;margin:auto;"><div style="text-align: center;">级联方式：<input type="radio" id="level1" style="width:auto" name="level" value="level1" onclick=check() />全部级联<input type="radio" id="level3" name="level" value="level3" onclick=check()  style="width:auto"/>级联下级<input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true" style="width:auto"/>不级联</div></div>';
			$(".form-bar").prepend(radio);
			$(".form-bar-inner").css("width", "10%").css("margin", "auto");
			check();
		}
		$("#tree").height($("#center").height()-$("#orgname").height()-30);
	}
	
	//初始化机构树
	function initTree() {
		var setting = {
				data : {
					key : {
						name : 'text'
					},
					simpleData : {
						enable : true,
						idKey : "id",
						pIdKey : "upId"
					}
				},
				view : {
					selectedMulti : false,
					showLine : false,
					expandSpeed : "fast"
				},
				check : {
					chkStyle : "checkbox",
					enable : true,
					chkboxType: { "Y": "", "N": "" }
				},
				callback : {
					onCheck : function(event, treeId, treeNode){
						if(cascade==1){
							if (treeNode.getCheckStatus().checked){
								if(treeNode.isParent){
									checkCascadeCheck(treeNode.children,true);
								}
							}else{
								if(treeNode.isParent){
									unCheckCascadeCheck(treeNode.children,true);
								}
							}
						}else if(cascade==3){
							if (treeNode.getCheckStatus().checked){
								if(treeNode.isParent){
									checkCascadeCheck(treeNode.children,false);
								}
							}else{
								if(treeNode.isParent){
									unCheckCascadeCheck(treeNode.children,false);
								}
							}
						}
					}
				}
		};
		leftTreeObj = $.fn.zTree.init($("#tree"), setting);
		loadTree();
	}
	//加载树中数据
	function loadTree() {
		$.ajax({
			cache : false,
			async : true,//同步
			url : "${ctx}/cabin/analysis/orggroup/getOrgTree",
			type : "POST",
			beforeSend : function() {
				BIONE.loading = true;
				BIONE.showLoading("正在加载数据中...");
			},
			complete : function() {
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result) {
				if (result.length > 0) {
					leftTreeObj.addNodes(null, result, true);
					leftTreeObj.expandAll(false);
					selectOrg();
				}
			},
			error : function(result, b) {
				BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
			}
		});
	}
	
	//机构树反选
	function selectOrg(){
		var slsOrgNo = window.parent.orgNo;
		if(slsOrgNo){
			slsOrgNo = slsOrgNo.split(";");
			for(var i = 0 ; i<slsOrgNo.length;i++){
				var node = leftTreeObj.getNodeByParam("id", slsOrgNo[i], null);
				if(node){
					leftTreeObj.checkNode(node, true, false);
				}
			}
		}
	}
	
 	//是否级联选择
	function check() {
		if($("#level1")[0].checked){
			cascade =1;
		}
		if($("#level2")[0].checked){
			cascade =2;
		}
		if($("#level3")[0].checked){
			cascade =3;
		}
	}

	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("chooseOrg");
			}
		}, {
			text : "选择",
			onclick : function() {
				var TreeNodes=leftTreeObj.getCheckedNodes(true);
				var orgNo = "";
				var orgNm = "";
				for(var i = 0 ;i < TreeNodes.length; i++){
					orgNo +=TreeNodes[i].id + ";";
					orgNm +=TreeNodes[i].text + ";";
				}
				window.parent.orgNo = orgNo;
				window.parent.orgNm = orgNm;
				window.parent.changeOrgNm();
				BIONE.closeDialog("chooseOrg");
			}
		});
		BIONE.addFormButtons(btns);
	}
	
	//勾选树节点
	function checkCascadeCheck(treeNode,flag){
		if(flag){
			for(var i = 0 ;i < treeNode.length; i++){
				leftTreeObj.checkNode(treeNode[i], true, false,false);
				if(treeNode[i].isParent){
					checkCascadeCheck(treeNode[i].children,flag);
				}
			}
		}else{
			for(var i = 0 ;i < treeNode.length; i++){
				leftTreeObj.checkNode(treeNode[i], true, false,false);
			}
		}
	}

	//取消勾选树节点
	function unCheckCascadeCheck(treeNode,flag){
		if(flag){
			for(var i = 0 ;i < treeNode.length; i++){
				leftTreeObj.checkNode(treeNode[i], false, false,false);
				if(treeNode[i].isParent){
					unCheckCascadeCheck(treeNode[i].children,flag);
				}
			}
		}else{
			for(var i = 0 ;i < treeNode.length; i++){
				leftTreeObj.checkNode(treeNode[i], false, false,false);
			}
		}
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div id = "orgname" style="margin: 8px; width:80px;padding-left: 5px;float:left">
			<span>机构组选择：</span>
		</div>
		<div style="margin: 5px; margin-left: 80px;padding-left: 5px;">
		<input id="orggrp"></input>
		</div>
		<div class="content" style="border: 1px solid #C6C6C6;">
			<div id="treeContainer"
				style="width: 100%;  height: 100%;overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90% ;height: 100% ;"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>