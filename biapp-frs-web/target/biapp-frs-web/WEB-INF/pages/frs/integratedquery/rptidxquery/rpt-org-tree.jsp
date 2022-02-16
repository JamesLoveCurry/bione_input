<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style type="text/css">

.label-content {
	margin-left: 6px;
	display: block;
	float: left;
}
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
	cursor: pointer;
}
.label-item:hover {
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
.label-item.select {
	background-color: #FDBB69;
	border-color: #FDBB69;
}
</style>
<script type="text/javascript"> var app = { ctx : '${ctx}' }; </script>
<script type="text/javascript" src="${ctx}/frs/js/rptquery/rptquery.js"></script>
<script type="text/javascript" src="${ctx}/frs/js/rptfill/TaskFill.js"></script>
<script type="text/javascript">

    var orgType = parent.liger.get('orgTypeBox').getValue();
	var orgNo = parent.liger.get('orgNm_sel').getValue();
	var taskOrgTree = null;
	var orgNoList = [];
	var orgNoMap = new Map();
	
	$(function(){
		var height = $(document).height();
		$("#center").height(height-42);
		$("#content").height(height-42);
		$("#treeContainer").height(height-95);
		$("#tree").height(height-105);
		if(orgNo){
			orgNoList = orgNo.split(",");
			for(var z = 0; z < orgNoList.length; z++){
				orgNoMap.set(orgNoList[z], orgNoList[z]);
			}
		}
		initTree();
		var zTreeObj = app.zTreeObj = taskOrgTree;
		
		var btns = [
			{ text : "取消", onclick : function() { BIONE.closeDialog("taskOrgWin"); } },	
			{ text : "选择", onclick : function() {
					var nodes = app.zTreeObj.getCheckedNodes(), orgNos = [], orgNms = [];
					if (nodes.length > 0) {
						if( $('input:radio:checked').val() == 'level1'){//如果选择级联获取机构，则再次获取已选机构的下级
							var otyp, oNs = [];
							$(nodes).each(function(i, n) {
								oNs.push(n.id);
								otyp=n.data.id.orgType;
							});
							$.ajax({
								async : false,
								type : "POST",
								url : '${ctx}/frs/integratedquery/rptquery/orgChilds',
								data : {'oNs':JSON.stringify(oNs),'orgType':otyp},
								success : function(result){
									var resorgs = result.orgNos;
									var resmrgs = result.orgNms;
									for(var i=0;i<resorgs.length;i++){
										orgNos.push(resorgs[i]);
										orgNms.push(resmrgs[i]);
									}
									
								},
								error : function(){
									BIONE.tip('下级机构获取失败');
								}
							}); 
							
						}else{
							$(nodes).each(function(i, n) {
								orgNos.push(n.id);
								orgNms.push(n.text);
							});
						}
					}
					var c = window.parent.jQuery.ligerui.get("orgNm_sel");
					c._changeValue(orgNos.join(), orgNms.join());
					BIONE.closeDialog("taskOrgWin");
				}
			}
		];
		BIONE.addFormButtons(btns);
		$('#center').css('overflow', 'hidden');
		
		// 如果是 ‘报表指标查询’的话
		if ($(window).width() > 600) {
			$('.content').css({'margin-left': '4px', 'display': 'block'});
			$('.label-content').width($('.center').width() - $('.content').width() - 10);
			// 获取机构集
			BIONE.ajax({
				data : { orgType : parent.liger.get('orgTypeBox').getValue() },
				url : app.ctx + '/frs/integratedquery/rptidxquery/info/searchOrgSetInfo.json'
			}, function(result) {
				for (var n in result) {
					// label: n, data: result[n]
					var orgNo = $(result[n]).map(function(i, org) {
						return org.orgNo;
					}).toArray().sort();
					$('#label').append($('<div class="label-item"/>').append($('<div class="text">').text(n)).data('orginfo', result[n]).data('orgNo', orgNo.join(',')));
				}
			});
			$('.label-item').live('click.label', function(event) {
				app.zTreeObj.setting.async.otherParam.orgNo = $(this).data('orgNo');
				app.zTreeObj.reAsyncChildNodes(null, 'refresh');
				$('.label-item.select').removeClass('select');
				$(this).addClass('select');
				$('#level2').click();
			});
		}
		
	});
	
	//是否级联选择
	function check() {
		//cascade = $("#level1")[0].checked;
		if ($("#level1")[0].checked == true)
			taskOrgTree.setting.check.chkboxType = {
				"Y" : "s",
				"N" : "s"
			};
		else
			taskOrgTree.setting.check.chkboxType = {
				"Y" : "",
				"N" : ""
			};
	}
	
	function initTree(){
		settingAsync = {  //异步
			async : {
				enable : true,
				url : "${ctx}/frs/integratedquery/rptquery/orgTreeData.json",
				autoParam : ["id"],
				otherParam : { 'orgType': orgType, 'isSum': '1', 'orgNo': orgNo },
				dataType : "json",
				type : "post",
				dataFilter: function (treeId, parentNode, responseData) {
					if (responseData && responseData.length > 0) {
						// 将获取的后端数据封装成 ztree节点对象
						if (responseData[0].params) {
							$(responseData).each(function(i, n) {
								n.id = n.params.realId;
								n.upId = n.upId;
								n.isParent = true;
								if(orgNoMap.get(n.id)){
									n.checked = true;
								}
							});
						}
						// 父节点被勾选, 异步加载的子节点视情况是否级联勾选
						if (parentNode && parentNode.checked && $('input:radio:checked').val() == 'level1') {
							$(responseData).each(function(i, n) {
								n.checked = true;
							});
						}
					}
					return responseData;
				}
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
			view : {
				selectedMulti : false
			},
			check:{ 
				enable : true, 
				chkboxType :{"Y":"","N":""}, 
				chkStyle :"checkbox"
			},
			callback : {
				onNodeCreated : function(event, treeId, treeNode) {
					//若是根节点，展开下一级节点
					if(treeNode.tId == "tree_1"){
						taskOrgTree.reAsyncChildNodes(treeNode, "refresh");
					}
				},
				onAsyncSuccess : function(event, treeId, treeNode, msg) {
					var otherParam = taskOrgTree.setting.async.otherParam;
					delete otherParam.orgNo;   //删除该参数条件, 避免再次进入搜索
				},
			}
		};
		
		settingSync = {  //同步
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
			view : {
				selectedMulti : false
			},
			check:{ 
				enable : true, 
				chkboxType :{"Y":"","N":""}, 
				chkStyle :"checkbox"
			}
		};
		
		$("#treeSearchIcon").bind("click", function(){
			setTree($("#treeSearchInput").val() != "")
		});
		$("#treeSearchInput").bind("keydown", function(e){
			if(e.keyCode == 13){
				setTree($("#treeSearchInput").val() != "")
			}
		});
		
		setTree(false);  //初始为false
	}
	
	function setTree(searchFlag){
		if(searchFlag){
			taskOrgTree = $.fn.zTree.init($("#tree"), settingSync);
			check();
			loadTree("${ctx}/frs/rpttsk/publish/searchOrgTree?orgType="+orgType+"&d=" + new Date().getTime(), 
					taskOrgTree, {searchName : $("#treeSearchInput").val()});
		}else{
			taskOrgTree = $.fn.zTree.init($("#tree"), settingAsync);
			check();
		}
	}
	
	//加载树
	function loadTree(url, treeObj, data){
		 $.ajax({
			cache : false,
			async : true,
			url : url,
			type : "POST",
			dataType : "json",
			data : data,
			beforeSend : function(){
			},
			complete : function(){
				BIONE.loading = false;
				BIONE.hideLoading();
			},
			success : function(result){
				var nodes = treeObj.getNodes();
				for(var i=0; i<nodes.length; i++){
					treeObj.removeNode(nodes[i], false);   //移除先前节点
				}
				if(result.length > 0){
					$(result).each(function(i, n) {
						if(orgNoMap.get(n.id)){
							n.checked = true;
						}
					});
					treeObj.addNodes(null, result, false);
					treeObj.expandAll(true);
				}
			},
			error : function(result, b){
			}
		 });
	}

</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6;">
			<div id="treeToolbar" style="border-bottom-width: 1px; border-bottom-style: solid; border-bottom-color: #D6D6D6; height: 24px; background: #F1F1F1">
				<div id="level" style="line-height: 24px; height: 24px; margin-left: 5px; float: left;">
					<span style="display: block; float: left;">是否级联： 是</span>
					<input type="radio" id="level1" name="level" value="level1" style="display: block; float: left; margin: 5px;" onclick=check() />
					<span style="display: block; float: left;">否 	</span>
					<input type="radio" id="level2" name="level" value="level2" style="display: block; float: left; margin: 5px;" onclick=check() checked="true" />
				</div>
			</div>
			<div id="treeSearchbar" style="width:99%; height:20px; margin-top:2px; padding-left:2px;">
				<ul>
					<li style="width:100%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field" style="width: 100%;padding-top: 0px;padding-left: 0px;bottom: 0px;" />    
								<div class="l-trigger">                                                                      
									<i id="treeSearchIcon" class="icon-search search-size"></i>                                                 
								</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div>
			<div id="treeContainer"
				style="width: 100%; padding-top: 5px; overflow: auto; clear: both; background-color: #FFF">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
		<div id="label" class="label-content">
			
		</div>
	</div>
</body>
</html>