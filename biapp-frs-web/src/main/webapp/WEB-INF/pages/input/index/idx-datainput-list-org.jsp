<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%@ include file="/common/meta.jsp"%>
<meta name="decorator" content="/template/template5.jsp">
<script type="text/javascript">
	var leftTreeObj = null;

	var orgFilter = '${orgFilter}';

	$(function() {
		initTree();
		initBtn();
	});
	function initTree() {
		var ids = [];
		var filters = orgFilter.split(",");
		for ( var i = 0; i < filters.length; i++) {
			if (filters[i] && filters[i] != "") {
				var filterValue = filters[i].split("_")
				ids = ids + filterValue[0] + ",";
			}
		}
		var url_ = "${ctx}/rpt/input/task/getOrgTree.json" ;
		var setting = {
			async : {
				enable : true,
				url : url_,
				dataType : "json",
				otherParam: {
					orgs: ids
				}
			},
			data : {
				key : {
					name : "text"
				}
			},
			check : {
				enable : true,
				chkStyle : "checkbox",
				chkboxType : {
					"Y" : "s",
					"N" : "s"
				}
			},
			view : {
				selectedMulti : false,
				showLine : false
			},
			callback : {
				onClick : function(event, treeId, treeData, treeNum) {
					BIONE.closeDialog("selectOrgBox", null, true, {
						orgId : treeData.id,
						orgNm : treeData.text,
						orgIds : null,
						orgNms : null
					});
				}
			}

		};
		leftTreeObj = $.fn.zTree.init($("#leftTreeObj"), setting);
	}
	var btns = [];
	//初始化按钮
	function initBtn() {
		btns.push({
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("selectOrgBox", null, true);
			}
		}, {
			text : "选择",
			onclick : function() {
					var checkNodes = leftTreeObj.getCheckedNodes();
					if(!checkNodes||checkNodes.length==0){
						BIONE.tip("请选择组织");
						return ;
					}
					var texts = [];
					var ids = [];
					for ( var tmp in checkNodes) {
						texts.push(checkNodes[tmp].text);
						ids.push(checkNodes[tmp].id);
					}
					BIONE.closeDialog("selectOrgBox", null, true, {
						orgId : null,
						orgNm : null,
						orgIds : ids,
						orgNms : texts
					});
			}
		});
		BIONE.addFormButtons(btns);
	}
</script>
<title>指标信息</title>
</head>
<body>
	<div id="template.center">
		<div class="content" style="border: 1px solid #C6C6C6; height: 98%">
			<div id="treeContainer"
				style="width: 100%; height: 100%; overflow: auto; clear: both;">
				<ul id="leftTreeObj"
					style="font-size: 12; background-color: F1F1F1; width: 90%"
					class="ztree"></ul>
			</div>
		</div>
	</div>
</body>
</html>