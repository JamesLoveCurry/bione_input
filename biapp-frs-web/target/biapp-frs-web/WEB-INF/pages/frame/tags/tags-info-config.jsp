<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="decorator" content="/template/template5.jsp">
<!-- Bootstrap -->
<link rel="stylesheet" type="text/css" href="${ctx}/js/bootstrap3/css/bootstrap.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/js/tagsinput/bootstrap-tagsinput.css" />
<link rel="stylesheet" type="text/css" href="${ctx}/js/datashow/bootstrap.exlabel.css" />
<script src="${ctx}/js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="${ctx}/js/datashow/jquery.bootstrap.exlabel.js"></script>
<script type="text/javascript" src="${ctx}/js/tagsinput/bootstrap-tagsinput.js"></script>
<script type="text/javascript">
	$(function() {
		initData();
		initPanel();
		initBtn();
	});
	
	function initData(){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/tags/getTagsInfo',
			dataType : 'json',
			type : "post",
			data : {
				id : "${id}",
				tagObjId : "${tagObjId}"
			},
			success : function(result){
				$('#tag').tagsinput({
					tagClass : function(item) {
						return 'label ' + (item.style || 'label-info');
					},
					itemValue : 'id',
					itemText : 'text',
					typeahead: {
						source: result.allTags
					}
				});
				for(var i in result.objTags){
					$('#tag').tagsinput('add', result.objTags[i]);
				}
			}
		});	
	}
	
	function initPanel(){
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/tags/getMLTagInfo',
			dataType : 'json',
			type : "post",
			data : {
				tagObjId : "${tagObjId}"
			},
			success : function(result){
				var pad1 = $('#panel1').exLabel({
					onClick : function(label) {
						$('#tag').tagsinput('add', label);
					}
				});
				var pad2 = $('#panel2').exLabel({
					onClick : function(label) {
						$('#tag').tagsinput('add', label);
					}
				});
				for(var i in result.latest){
					pad1.addLabel(result.latest[i]);
				}
				for(var i in result.most){
					pad2.addLabel(result.most[i]);
				}
			}
		});	
	}
	
	function initBtn(){
		//初始化按钮
		var btns = [{
			text : '取消',
			onclick : function() {
				BIONE.closeDialog("tagEdit");
			}
		},{
			text : '保存',
			onclick : saveRels
		}];
		BIONE.addFormButtons(btns);
	}
	
	function saveRels(){
		var items = $('#tag').tagsinput("items");
		var tagNms = [];
		for(var i in items){
			tagNms.push(items[i].text);
		}
		$.ajax({
			cache : false,
			async : true,
			url : '${ctx}/bione/tags/saveTagRel',
			dataType : 'json',
			type : "post",
			data : {
				id : "${id}",
				tagObjId : "${tagObjId}",
				tagNms : tagNms.join(",")
			},
			success : function(result){
				BIONE.closeDialog("tagEdit","配置成功");
			}
		});	
	}
</script>
<style>
.bootstrap-tagsinput {
	width: 100%;
	height: 100px;
	overflow: auto;
}
</style>
</head>
<body>
	<div id="template.center">
		<div class="container">
			<div class="row">
				<div style="margin: 20px 0;">
					<input id="tag" type="text">
				</div>
			</div>
			<div class="row">
				<div class="panel panel-default">
					<div id="panel1" class="panel-body labelpad-recent"></div>
				</div>
			</div>
			<div class="row">
				<div class="panel panel-default">
					<div id="panel2" class="panel-body labelpad-offen"></div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>