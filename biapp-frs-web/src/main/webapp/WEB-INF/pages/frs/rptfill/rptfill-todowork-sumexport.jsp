<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<%@ include file="/common/spreadjs_load.jsp"%>
<style>
	.input {
		width: auto !importent;
	}
	.form-bar-inner {
		overflow: hidden;
    	padding-right: 50px;
	}
</style>
<script type="text/javascript" src="${ctx}/js/frs/rptquery/dateCal.js" ></script>
<script type="text/javascript" src="${ctx}/js/format/date-format.js" ></script>
<script type="text/javascript" >

var View, Spread,layout,rightTreeObj,checkNodes=[];
var cascade = 2;
var treeInfo=null;
$(function() {
	initDate();
	initTreeInfo();
	initTree();
	initExport();
	initBtn();
});

function initDate(){
	var date = new Date();
	date.setDate(date.getDate() - 1);
	$("#date").ligerDateEditor({
		format: 'yyyyMMdd',
		initValue: date.toFormattedString('yyyyMMdd')
	});
}
function initExport()  {
	download = $('<iframe id="download"  style="display: none;"/>');
	$('body').append(download);
};

function initTreeInfo(){
	$.ajax({
		cache : false,
		async : true,
		url :  "${ctx}/frs/rpttsk/publish/getAllParentOrgTree?orgType=${rptType}&d="+ new Date().getTime(),
		dataType : "json",
		type : "POST",
		data:{
            con : JSON2.stringify(con),
            fileType : fileType
        },
		success : function(result) {
			treeInfo=result.treeInfo;
		},
		error : function(result, b) {
		}
	});
	if(parent.taskId){
		var exeobjs = parent.taskInfo.exeobjs;
		if(exeobjs!=null){
			for ( var l in exeobjs) {
				var node={};
				node.id = exeobjs[l].id.exeObjId;
				node.params={};
				node.params.realId = exeobjs[l].id.exeObjId;
				node.params.type="org";
				if(getIndex(node,checkNodes)==-1)
					checkNodes.push(node);
			}
		}
	}
	
}

function initLayout(){
	var centerWidth = $("#center").width();
	layout = $("#layout").ligerLayout({
		height : $("#center").height()-10,
		centerWidth : centerWidth - 270,
		rightWidth : 270,
		allowRightResize : false ,
		onEndResize : function(){
			if(View){
				View.resize(View.spread);
			}
		}
	});
	var rightToggleDoom = $(".l-layout-right").children(".l-layout-header").children(".l-layout-header-toggle");
	rightToggleDoom.unbind("click");
	rightToggleDoom.bind("click" , function(){
		layout.setRightCollapse(true);
		if(View){
			View.resize(View.spread);
		}
	});
	layout.rightCollapse.toggle.unbind("click");
	layout.rightCollapse.toggle.bind("click" , function(){
		layout.setRightCollapse(false);
		if(View){
			View.resize(View.spread);
		}
	});
}

function initTree(){
	url = "${ctx}/frs/rpttsk/publish/getParentOrgTree?orgType=${rptType}&d="+ new Date().getTime();
	var setting = {
			async : {
				enable : true,
				url :url,
				autoParam : [ "id" ],
				dataType : "json",
				type : "post",
				dataFilter : function(treeId, parentNode, childNodes) {
					if (childNodes) {
						for ( var i = 0; i < childNodes.length; i++) {
							childNodes[i].id = childNodes[i].params.realId;
							childNodes[i].upId = childNodes[i].upId;
							childNodes[i].nodeType = childNodes[i].params.type;
							childNodes[i].isParent = childNodes[i].params.isParent == "true" ? true: false;
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
			check:{
				enable : true,
				chkboxType :{"Y":"","N":""},
				chkStyle :"checkbox"
			},
			data : {
				key : {
					name : "text"
				}
			},
			view : {
				selectedMulti : false
			},
			callback : {
				onNodeCreated : function(event, treeId, treeNode) {
					if (treeNode.id == "ROOT") {
						//若是根节点，展开下一级节点
						rightTreeObj.reAsyncChildNodes(treeNode, "refresh");
					}
					if(treeNode.upId == "0"){
						//若是根节点，展开下一级节点
						rightTreeObj.reAsyncChildNodes(treeNode, "refresh");
					}
				},
				beforeCheck:function (treeId, treeNode){
					return true;
				},
				onCheck:function(event, treeId, treeNode){
					if(cascade==1){
						if (treeNode.getCheckStatus().checked) {
							var node={};
							node.id=treeNode.id;
							node.text=treeNode.text;
							node.params=treeNode.params;
							if(getIndex(node,checkNodes)==-1)
								checkNodes.push(node);
							checkCascadeCheck(treeNode.id,true);
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
							unCheckCascadeCheck(treeNode.id,true);
						}
					}
					else if(cascade==2){
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
					else if(cascade==3){
						if (treeNode.getCheckStatus().checked) {
							var node={};
							node.id=treeNode.id;
							node.text=treeNode.text;
							node.params=treeNode.params;
							if(getIndex(node,checkNodes)==-1)
								checkNodes.push(node);
							checkCascadeCheck(treeNode.id,false);
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
							unCheckCascadeCheck(treeNode.id,false);
						}
					}
				}
			}
		};
		rightTreeObj = $.fn.zTree.init($("#tree"), setting);
		$("#treeContainer").height($("#center").height()-100);
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

function checkCascadeCheck(id,flag){
	var nodes=treeInfo[id];
	if(nodes!=null&&nodes.length>0){
		for(var i in nodes){
			var node={};
			node.id=nodes[i].id;
			node.text=nodes[i].text;
			node.params=nodes[i].params;
			if(getIndex(node,checkNodes)==-1){
				var treenode = rightTreeObj.getNodeByParam("id", node.id, null);
				if(treenode!=null)
					rightTreeObj.checkNode(treenode, true, false,false);
				checkNodes.push(node);
				if(flag)
					checkCascadeCheck(node.id,true);
			}
				
		}
	}
}

function unCheckCascadeCheck(id,flag){
	var nodes=treeInfo[id];
	if(nodes!=null&&nodes.length>0){
		for(var i in nodes){
			var node={};
			node.id=nodes[i].id;
			node.text=nodes[i].text;
			node.params=nodes[i].params;
			var index=getIndex(node,checkNodes);
			if(index>=0){
				var treenode = rightTreeObj.getNodeByParam("id", node.id, null);
				if(treenode!=null)
					rightTreeObj.checkNode(treenode, false, false,false);
				checkNodes.splice(index,1);
				if(flag)
					unCheckCascadeCheck(node.id,true);
			}
			
		}
	}
}

function getIndex(info,array){
	for(var i in array){
		if(array[i].id==info.id){
			return i;
		}
	}
	return -1;
}

function initBtn(){
	//初始化按钮
	var btns = [{
		text : '下载',
		onclick : function() {
			var date =[$("#date").val()];
			var orgName = '';
			if("${orgNm}" != null){
				orgName = "${orgNm}";
			}
			var rptId = "${rptId}";
			var dataDate = $("#date").val();
			var lineId = "${lineId}";
			if ('checked' == $('#lastData').attr('checked')) {
				date.push(dateCal._getYesterday(date[0]));
			}
			
			var orgNodes = [];
			$.each(checkNodes, function(i, n) {
				orgNodes.push(n.id);
			});
			
			var data = {
				org: orgNodes,
				dataDate: date.join(',')
			}
			argsArr = [];
			var args1 = {'DimNo':'ORG','Op':'=','Value':data.org};
			argsArr.push(args1);
			BIONE.ajax({
				type : 'post',
				dataType : 'text',
				url :'${ctx}/report/frame/tmp/view/getSumViewExport',
				data : { 
					rptId : rptId,
					dataDate : dataDate,
					busiLineId : lineId,
					orgNm : orgName,
					searchArgs: JSON2.stringify(argsArr),
					orgs : data.org.join(","),
					dates : data.dataDate
				}
			}, function(filePath) {
				if (filePath == null) {
					BIONE.tip('无数据可供下载');
				} else {
					$('#download').attr('src', '${ctx}/frs/integratedquery/rptquery/downloadFile?filePath=' + encodeURI(encodeURI(filePath)));
				}
			});
		}
	}];
	BIONE.addFormButtons(btns);
}

</script>
</head>
<body>
	<div id="template.center">
		<div style="margin: 5px; padding-left: 5px;">
			<input id="date"></input>
		</div>
		<div style="width:98%;height: 20px;background-color: #FFFFFF;border-bottom: solid 1px #D0D0D0;"><div style="text-align: left;width:100%; padding-left: 10px;padding-top: 2px;">级联方式：全部级联 <input type="radio" id="level1" style="width:auto" name="level" value="level1" onclick=check() />级联下级<input type="radio" id="level3" name="level" value="level3" onclick=check()  style="width:auto"/>不级联<input type="radio" id="level2" name="level" value="level2" onclick=check() checked="true" style="width:auto"/></div></div>
		<div id="treeContainer"
					style="width: 100%; overflow: auto; clear: both; background-color: #FFFFFF;">
					<ul id="tree"
						style="font-size: 12; background-color: #FFFFFF; width: 200px;"
						class="ztree"></ul>
				</div>
				<div style="padding: 5px 0; height: 20px; line-height: 20px; background-color: #EEE; overflow: hidden; clear: both;">
					<div style="float: left; padding: 0 5px;">
						<input id="lastData" type="checkbox" style="width: auto;" /><label for="lastData" style="margin-left: 5px;">上期值</label>
					</div>
				</div>		
	</div>
</body>

</html>