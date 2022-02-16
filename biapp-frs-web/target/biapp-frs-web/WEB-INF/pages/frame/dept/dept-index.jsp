<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head> 
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template2_BS.jsp">
<script type="text/javascript">
	var items, currentNode,grid;
	var treeObj = null;
	$(function(){
		initTree();
		searchForm();
		initGrid();
		initToolbar();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
		$("#search input[name=orgName]").attr("readonly", "true").removeAttr("validate");
		$("#search input[name=orgNo]").attr("readonly", "true").removeAttr("validate");
	});
	
	function initTree() {
		window['orgTreeInfo'] = $.fn.zTree.init($("#tree"),{
			async:{
				enable : true,
				autoParam : [ "id" ],
				otherParam: {
					objDefNo: "AUTH_OBJ_ORG"
				},
				url : "${ctx}/bione/admin/authUsrRel/getAuthObjTree.json?d="+new Date().getTime(),
				dataType : "json",
				type : "post"
			},
			data:{
				key:{
					name:"text"
				}
			},
			view:{
				selectedMulti: false,
				showLine: false
			},
			callback:{
				onClick : function(event,treeId,treeNode){
					currentNode = treeNode;
					if(currentNode.id=="0"){
						return;
					}
					grid.set('parms', {
						orgId : currentNode.data.orgId,
						orgNo : currentNode.data.orgNo
					});
					$("#search input[name=deptNo]").val("");
					$("#search input[name=deptName]").val("");
					$("#search input[name=orgId]").val(currentNode.data.orgId);
					$("#search input[name=orgNo]").val(currentNode.data.orgNo);
					grid.loadData();
				}
			}
		},[ {
			id : "0",
			text : "机构树",
			icon : "${ctx}/images/classics/icons/house.png",
			nocheck : true,
			isParent: true
		    }]);
		var r = orgTreeInfo.getNodesByParam('id', '0');
    	r = r.length > 0 ? r[0] : null;
    	if (r) {
    		orgTreeInfo.reAsyncChildNodes(r, 'refresh', false);
    	}
		treeObj = window['orgTreeInfo'];
	}
	
	function searchForm() {
		$("#search").ligerForm({
			fields:[{
				display:'条线标识',
				name : "deptNo",
				newline : true,
				type : "text",
				cssClass : "field",
				attr : {
					field:'deptNo',
					op : "="
				}
			},{
				display:'条线名称',
				name:'deptName',
				newline:false,
				type : "text",
				cssClass : "field",
				attr : {
					field:'deptName',
					op : "like"
				}
			},{
				name:'orgNo',
				type:'hidden',
				attr:{
					field:'orgNo',
					op : "="
				}
			}]
		});
	}

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			//height:"100%",
			width:"100%",
			columns : [{
				display : '条线标识',
				name : 'deptNo',
				id:'deptNo',
				width : '46%',
				align : 'left'
			}, {
				display : '条线名称',
				name : 'deptName',
				width : '50%',
				align : 'left'
			} ],
			usePager : true,
			checkbox : false,
			tree: { columnId: 'deptNo' },
			dataAction : 'server', 	//从后台获取数据
			usePager : false, 		//服务器分页
			alternatingRow : true, 	//附加奇偶行效果行
			colDraggable : true,
			url : "${ctx}/bione/admin/dept/findDeptInfoByOrg.json?d=" + new Date().getTime(),
			method : 'post', // get
			delayLoad : true,
			sortName : 'deptNo',	//第一次默认排序的字段
			sortOrder : 'asc', 		//排序的方式
			rownumbers : true,
			toolbar : {}
		});
		
	}

	
	
	function initToolbar() {
		items = [ {
			text : '增加',
			click : function(){
				if(!currentNode){
					BIONE.tip("请先选择机构");
					return;
				}
				if(currentNode.id=="0"){
					BIONE.tip("请先选择机构");
					return;
				}
				var selectedDept=grid.getSelectedRow ();
				var deptId;
				if(!selectedDept){
					deptId='';
					BIONE.commonOpenLargeDialog("条线添加", "deptAddWin", "${ctx}/bione/admin/dept/new?orgId="+currentNode.data.orgId+"&deptId="+deptId);
				}else{
					deptId=selectedDept.deptId;
					BIONE.commonOpenLargeDialog("条线修改", "deptAddWin", "${ctx}/bione/admin/dept/new?orgId="+currentNode.data.orgId+"&deptId="+deptId);
				}
				
			},
			icon : 'add'
		}, {
			text : '修改',
			click : function(){
				var selectedDept=grid.getSelectedRow ();
				if(!selectedDept){
					BIONE.tip("请先选择需要修改的条线");
					return;
				}
				BIONE.commonOpenLargeDialog("条线修改", "deptModifyWin", "${ctx}/bione/admin/dept/"+selectedDept.deptId+"/edit");
			},
			icon : 'modify'
		}, {
			text : '删除',
			click : f_delete,
			icon : 'delete'
		} ];
		BIONE.loadToolbar(grid, items, function() { });
	}
	
	var str="";
	function getDeptIdAndChildIdByInfo(rowInfo){
		 if(rowInfo==1){
			 str="";
		 }
		var children=grid.getChildren(rowInfo);
		str+=rowInfo.deptId+",";
		if(children){
			for(var i=0;i<children.length;i++){
				getDeptIdAndChildIdByInfo(children[i]);
			}
		}
	}
	function f_delete() {
		var selectedRow = grid.getSelecteds();
		if(selectedRow.length == 0){
			BIONE.tip('请选择行');
			return;
		}
		for(var k=0;k<selectedRow.length;k++){
			getDeptIdAndChildIdByInfo(selectedRow[k]);	
		}
		
		$.ligerDialog.confirm('确实要删除这些记录及其子记录吗!', function(yes) {
			if (yes) {
				$.ajax({
					async : false,
					type : "POST",
					url : '${ctx}/bione/admin/dept/' + str,
					success : function() {
						BIONE.tip('删除成功');
					},
					error : function() {
						BIONE.tip('删除失败');
					}
				});
				//刷新表格和树
				grid.loadData();
				BIONE.refreshAsyncTreeNodes(treeObj, "id",
						currentNode.upId, "refresh");
				str = "";
			}
		});
	}
	
</script>

<title>条线管理</title>
</head>
<body>
	<div id="template.left.up.icon">
		<img src="${ctx }/images/classics/icons/application_side_tree.png" />
	</div>
	<div id="template.left.up">
		<span style="font-size: 12">机构信息</span>
	</div>
</body>
</html>