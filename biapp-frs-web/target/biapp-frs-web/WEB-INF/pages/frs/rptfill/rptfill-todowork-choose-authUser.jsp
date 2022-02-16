<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var btns =[]; 
	var currentNode;  //当前点击节点
	var leftTreeObj;
	var org = "${org}";
	var settingAsync = null;
	var settingSync = null;
	var checkedUser=[];
	
	$(function(){
		var height = $(document).height();
		$("#center").height(height-42);
		$("#content").height(height-42);
		$("#treeContainer").height(height-72);
		$("#radioCon").height($("#content").height()-$("#treeContainer").height());
		initTree();
		initBtn();
	});
	function initTree(flag) {
		settingAsync = {
			async : {
				enable : true,
				autoParam : [ "id" ],
				url : "${ctx}/rpt/frs/rptfill/getAuthTree.json?org=${org}&moduleType=${moduleType}&queryType=${queryType}&t="
						+ new Date().getTime()+"&flag="+flag,
				dataType : "json",
			},
			data : {
				key : {
					name : "text"
				},
				simpleData : {
					enable : true,
					idKey : "id",
					pIdKey : "upId",
					rootPId : null
				}
			},
			check:{ 
				enable : true, 
				chkboxType :{"Y":"","N":""}, 
				chkStyle :"checkbox"
			},
			callback : {
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.id == "ROOT" || treeNode.id == "0") {
						leftTreeObj.reAsyncChildNodes(treeNode, "refresh");
					}
				}
			}
		};

		leftTreeObj = $.fn.zTree.init($("#tree"), settingAsync);
	}

	var checkedUsers ;
	function checkNode(){
		checkedUsers = [];
		var checkedNodes = leftTreeObj.getCheckedNodes();
		var cansave = false;
		for (var i =0 ;i < checkedNodes.length ;i++){
			if(checkedNodes[i].params.type=="user")
				checkedUsers.push(checkedNodes[i].params.realId);
		}
		if(checkedUsers.length==0)
			return false;
		return true;
	}
	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("chooseAuthUser",null,false,null);
			}
		}, {
			text : "选择",
			onclick : function() {
				if(checkNode()){
					BIONE.closeDialog("chooseAuthUser",null,true,checkedUsers);
				}else{
					BIONE.tip("请选择人员节点");
				}
			}
		});
		if(!"${viewFlag}" || "${viewFlag}" == null || "${viewFlag}" == ""){
			btns.push({
				text : "忽略",
				onclick : function() {
					BIONE.closeDialog("chooseAuthUser",null,true,"ignore");
				}
			});
		}
			btns.push({
				text : "本级复核人",
				onclick : function() {
					var flag = "1";//只查询本级机构
					initTree(flag);//只查询本级机构
				}
			});
		BIONE.addFormButtons(btns);
	}
	
	//是否级联选择
	function check() {
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
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6">
			<div id="treeContainer"
				style="width: 100%; height: 100%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree">
				</ul>
			</div>
		</div>
		<div id="radioCon" style="width:100%; height: 10px; background-color: #FFFFFF;margin-top:5px;">
				<div style="text-align: left;width:100%; ">是否级联： 是 <input type="radio" id="level1"  name="level" value="level1" onclick=check() /> 否 <input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true"/></div>
		</div> 
	</div>
</body>
</html>