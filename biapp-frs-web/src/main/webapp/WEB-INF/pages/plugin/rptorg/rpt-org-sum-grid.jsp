<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template12_BS.jsp">
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var mainform;
	var orgNo = "${orgNo}";
	var orgType = "${orgType}";
	
	var currentNode;
	var tabObj;
	var leftTreeObj;
	var newFlag = false;
	var tempGrid = null;
	var cascade = false;
	var orgNos = null;
	var upOrgNos = null;
	var ischecked = true;
	var checkedAll = "";

	function refreshTree() {
		if (leftTreeObj) {
			leftTreeObj.reAsyncChildNodes(null, "refresh");
			$("#mainformdiv").html("");
		}
	}

	function init() {}
	
	function getTreeNodeAllId(){
		var checkNos=[];
		checkedAll = leftTreeObj.getCheckedNodes(true);
		var all = [];
		
			for ( var i in checkedAll) {
				if (cascade) {
					if(checkedAll[i].id != "02" && checkedAll[i].id != "03" && checkedAll[i].id != "01"){
						if(checkedAll[i].children){
							all = all.concat(checkedAll[i].children);
						}
						if($.inArray(checkedAll[i],all)<0){
							checkNos.push(checkedAll[i].id);
						}
					}
				
				}
				else {
					checkNos.push(checkedAll[i].id);
				}
		} 
		return checkNos.join(",");
	}

	function check() {
		cascade = $("#level1")[0].checked;
		if ($("#level1")[0].checked == true)
			leftTreeObj.setting.check.chkboxType = {
				"Y" : "s",
				"N" : "s"
			};
		else
			leftTreeObj.setting.check.chkboxType = {
				"Y" : "",
				"N" : ""
			};
	}

	$(function() {
		//设置高度
        var $centerDom = $(window.parent.document);
		gridCenter = $centerDom.height() - 60;

		findCheckData();
		initTree();

		//异步树
		function initTree() {
			var setting = {
				async : {
					enable : true,
					type:'get',
					url : "${ctx}/frs/systemmanage/orgmanage/getTree.json?orgType=${orgType}&state=1&t="
							+ new Date().getTime(),
					autoParam : [ "upOrgNo","orgNo", "orgNm" ],
					dataType : "json",
					dataFilter : function(treeId, parentNode, childNodes) {
						if (childNodes) {
							for ( var i = 0; i < childNodes.length; i++) {
								childNodes[i].orgType = childNodes[i].params.orgType;
								childNodes[i].upOrgNo = childNodes[i].params.upOrgNo;
								childNodes[i].orgNm = childNodes[i].params.orgNm;
								childNodes[i].orgNo = childNodes[i].params.orgNo;
							}
						}
						return childNodes;
					}
				},
				data : {
					key : {
						name : "text"
					}
				},
				view : {
					selectedMulti : false
				},
				check : {
					enable : true,
					chkStyle : "checkbox",
					chkboxType : {
						"Y" : "",
						"N" : ""
					}
				},
				callback : {
					onAsyncSuccess : zTreeOnAsyncSuccess,
					onCheck: zTreeOnCheck,
					onNodeCreated : function(event, treeId, treeNode) {
						if (treeNode.id == "ROOT") {
							leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
						}
					}
				}
			}
			var buttons = [];
			if (savedCallback) {
				buttons.push({
					text : '保存',
					onclick : savedCallback
				});
			}
			BIONE.addFormButtons(buttons);
			
			//保存
			function savedCallback() {
				$.ajax({
					async:false,
					type:"POST",
					dataType:"json",
					url:"${ctx}/rpt/frame/rptorgsum/saveCollect?checkedAll="+getTreeNodeAllId()+"&orgNo="+orgNo+"&orgType="+orgType+"&cascade="+cascade,
					success:function(data){
						if(data){
							parent.BIONE.tip("修改成功");
							if(parent.dialog && parent.dialog != ""){
								BIONE.closeDialog("orgSumRelWin");
							}
						}
					}
				});
			}

			var radio = '<div style="width:100%;margin:auto;"><div style="text-align: center;">是否级联： 是 <input type="radio" id="level1"  name="level" value="level1" onclick=check() /> 否 <input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true"/></div></div>';
			$(".form-bar").prepend(radio);
			$(".form-bar-inner").css("width", "10%").css("margin", "auto");

			leftTreeObj = $.fn.zTree.init($("#tree"), setting);

		}
	});
	function zTreeOnCheck(event, treeId, treeNode) {
		var orgNode=window.parent.leftTreeObj.getNodeByParam("id", orgNo, null);
		if(!validateCheck(orgNode,treeNode.params.orgNo)){
			leftTreeObj.checkNode(treeNode, false, true);
		}
	};
	function validateCheck(treeNode,orgNo){
		if(orgNo==treeNode.params.orgNo){
			return false;	
		}
		else if(treeNode.params.orgNo=="0"){
			return true;
		}
		else{
			return validateCheck(treeNode.getParentNode(),orgNo);
		}
	}
	function zTreeOnAsyncSuccess(event, treeId, treeNode, msg) {
		if (treeNode) {
			for ( var i in treeNode.children) {
				if (treeNode.checked == true && cascade == true) {
					leftTreeObj.checkNode(treeNode.children[i], true, true);
				}
				if (getUpOrgNo(treeNode.children[i].id)) {
					leftTreeObj.reAsyncChildNodes(treeNode.children[i],
							"refresh");
				}
				if (getOrgNo(treeNode.children[i].id)) {
					leftTreeObj.checkNode(treeNode.children[i], true, true);
				}
			}
		} else {
			var nodes = leftTreeObj.getNodes();
			for ( var i in nodes) {
				leftTreeObj.reAsyncChildNodes(nodes[i], "refresh");
			}
		}

	};
	function getUpOrgNo(id) {
		for ( var i in upOrgNos) {
			if (id == upOrgNos[i]) {
				return true;
			}
		}
		return false;
	}
	function getOrgNo(id) {
		for ( var i in orgNos) {
			if (id == orgNos[i]) {
				return true;
			}
		}
		return false;
	}
	function findCheckData() {
		$.ajax({
			async : false,
			type : "POST",
			dataType : "json",
			url : "${ctx}/rpt/frame/rptorgsum/findCheck?orgType="
					+ orgType + "&orgNo="
					+ orgNo,
			success : function(data) {
				upOrgNos = data.upOrgNos;
				orgNos = data.orgNos;
			}
		});
	}
</script>

<title>机构管理</title>
</head>
<body>
	<div id="template.center"></div>
</body>
</html>