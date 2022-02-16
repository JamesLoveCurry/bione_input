<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head>
<style>
.checklabel {
	color: blue;
}
</style>
<script type="text/javascript"
	src="${ctx}/plugin/js/dim/dimFormula.js"></script>
<script type="text/javascript">
	var templateId = '${templateId}';
	var uniqueKey = '${uniqueKey}';
	var rm = '${content}';
	var taskIndexType = '${taskIndexType}';
	
	while(rm.indexOf('@n') != -1){
		rm = rm.replace("@n","\n");
	}
	var rms = rm.split('@@@@@');
	var content = rms[0];
	var nodeRm = rms[1];
	var view = '${view}';
	
	$(function() {
		initContent();
		initBtn();
		if(checkStrIsNull(rm)){
			$('#beforeNode').hide();
		}
		if(view && view=='true') {
			$("#currentNode").hide();
		}
	});
	
	function initContent (){
		$("#upperNode").val(content);
		$("#upperNode").attr('readOnly','readOnly');
		$("#content").val(nodeRm);
		//$("#content");
	}
	
	function initBtn() {
		if(view && view=='true') {
			return;
		}
		var btns = [  {
			text : "取消",
			onclick : function() {
				BIONE.closeDialog("commentBox");
			}
			},{
			text : "保存",
			onclick : function() {
				var ctx = $("#content").val();

				while(ctx.indexOf('\n') != -1){
					ctx = ctx.replace("\n","@n");
				}
				//BIONE.closeDialog("commentBox" , null , true , ctx?ctx:"");

				if(checkStrIsNull(ctx)) {
					BIONE.tip('意见不可以为空！');
				}

				ctx = ctx?ctx:"";

				ctx = getRm(ctx, '${content}');
				$.ajax({
					type : "post",
					dataType : 'json',
					data : {
						"templateId" : templateId,
						"comment" : encodeURI(encodeURI(ctx)),
						"uniqueKey" : encodeURI(encodeURI(uniqueKey)),
					},
					url : "${ctx}/rpt/input/taskcase/saveComment.json",
					success : function(data) {
						BIONE.closeDialog("commentBox");
					},
					error : function(result, b) {
						if(result.status == 200) {
							BIONE.closeDialog("commentBox" , null , true , ctx?ctx:"");
						}else {
							BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
						}
					}
				});
			}
		}];
		BIONE.addFormButtons(btns);
	}


	function getRm(content,oldComment){
		var linkStr = "";

		if(content)
			content = content.trim();
		if(!content||content=="undefined"){
			content = " ";
		}

		linkStr += '${userName}'+ " "+ new Date().toLocaleString()+ "：";

		return  linkStr + content + '@n' + oldComment;
	}

	/**
	 * @description: 判断是否为空
	 * @author 黄正鑫
	 * @date 2021/7/16 10:17
	 */
	function checkStrIsNull(str) {
		if(!str || str == '') {
			return true;
		}
		var resultStr=str.replace(/[ ]/g,"");    //去掉空格
		resultStr=resultStr.replace(/[\r\n]/g,"");
		return resultStr == '';
	}
</script>
</head>
<body>
	<div id="template.center">
		<div id="contentDiv" style="background-color: #FFFFFF">
			<div id = "beforeNode" >
				前节点意见：
				<textarea id="upperNode" class="l-textarea" style="width:97%;height:115px;margin:2px;resize: none;"></textarea>
			</div>
			<br/>
			<div id = "currentNode" >
				当前节点意见：
				<textarea id="content" class="l-textarea" style="width:97%;height:115px;margin:2px;resize: none;"></textarea>
			</div>
		</div>
	</div>
</body>
</html>