<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<html>
<head>
<%@ include file="/common/taglibs.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript" src="${ctx}/frs/js/rptfill/TaskFill.js"></script>
<script type="text/javascript">

	$(function(){
		var height = $(document).height();
		$("#center").height(height-45);
		$("#content").height(height-45);
		$("#treeContainer").height(height-74);   
		$("#tree").height(height-85);
		initTree();
		initBtn();
	});
	
	//初始化树  getOrgTree
	function initTree() {
		CommonAndSearchOrgTree("${ctx}/frs/rpttsk/publish/getOrgTree?orgType=${orgType}",
							   "${ctx}/frs/rpttsk/publish/searchOrgTree?orgType=${orgType}");
	}

	//初始化按钮
	function initBtn(){
		var btns =[];
		btns.push(
				{ text : "取消", onclick : function(){BIONE.closeDialog("taskOrgWin");}},	
				{ text : "选择", onclick : function() {
						var nodes = taskOrgTree.getSelectedNodes();
						if("ROOT" == nodes[0].id){
							BIONE.tip("根节点不可以选择！");
						}else{
							if("${rptFillFlag}" && "${rptFillFlag}" != null && "${rptFillFlag}" == "eastRptFill"){
								window.parent.chooseOrgTreeVal(nodes[0].id, nodes[0].text);
								BIONE.closeDialog("taskOrgWin");
							}else if("${rptFillFlag}" && "${rptFillFlag}" != null && "${rptFillFlag}" == "detail"){
								var p = parent || window;
								if (p.test) {
									p.test(nodes[0].id, nodes[0].text,"${Dname}");
								}
								BIONE.closeDialog("taskOrgWin");
							}else{
								var c = window.parent.jQuery.ligerui.get("orgNm_sel");
								if(!c){
									c = window.parent.jQuery.ligerui.get("org_tree");
								}
								if(c){
									c._changeValue(nodes[0].id, nodes[0].text);
								}
								BIONE.closeDialog("taskOrgWin");
							}
						}
					}
			 }
		);
		BIONE.addFormButtons(btns);
	}
	
</script>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6;">
			<div id="treeSearchbar" style="width:99%; height:25px; margin-top:2px; padding-left:2px;">
				<ul>
					<li style="width:100%;text-align:left;">                      
						<div class="l-text-wrapper" style="width: 100%;">                         
							<div class="l-text l-text-date" style="width: 100%;">                    
								<input id="treeSearchInput" type="text" class="l-text-field"  style="width: 100%;right: 0px;padding-left: 0px;" />    
								<div class="l-trigger">                                                                      
									<i id="treeSearchIcon" class="icon-search search-size"></i>                                                 
								</div>
							</div>                                                                                                   
						</div>
					</li>
				</ul>                                                                                                         
			</div>   
			
			<div id="treeContainer"
				style="width: 100%; overflow: auto; clear: both;">
				<ul id="tree"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree">
				</ul>
			</div>
		</div>
	</div>
</body>
</html>