<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template5.jsp">
<link rel="stylesheet" href="${ctx}/js/bootstrap3/css/bootstrap.css" />
<%-- <link rel="stylesheet" href="${ctx}/js/tagsinput/bootstrap-tagsinput.css" /> --%>
<%-- <link rel="stylesheet" href="${ctx}/js/datashow/exLabel.css"> --%>
<link rel="stylesheet" href="${ctx}/js/exlabel/exlabel.css">
<script type="text/javascript" src="${ctx}/js/exlabel/exlabel.js"></script>
<%-- <script src="${ctx}/js/tagsinput/bootstrap-tagsinput.js"></script>	 --%>
<%-- <script src="${ctx}/js/bootstrap/js/bootstrap.min.js"></script> --%>
<script type="text/javascript">
	var panel = null;
	var target = null;
	var userInfo = [];
	var i = 1;
	$(function(){
		initData();
		initPanel();
		initUser();
		initBtn();
	});
	
	function initPanel(){
		panel = $('#panel').exlabel({
			source: userInfo,
			text: 'userName',
			value: 'userId',
			menuHeight : "150px",
			callback: {
		        beforeShowMenu: function (a,b) {
		        },
		        onClick: function (data) {
		          $('#target').exlabel('add', data);
		        },
		        beforeAdd: function (label) {
		          
		        }
		      }
		});
		$('#panel').exlabel('onClick', function(data) {
			$('#target').exlabel('add', data);
		});
	}
	
	function initData(){
		$.ajax({
			cache : false,
			async : false,
			url : "${ctx}/rpt/frame/rptmgr/share/getUserInfo",
			dataType : 'json',
			type : "post",
			success : function(result) {
				userInfo = result;
			}
		});
	}
	function initUser(){
		$("#userBtn").css("left","0px").css("top","2px");
		$("#userBtn").click(function(){
			window.parent.panel = panel;
			window.parent.BIONE.commonOpenDialog("用户选择","userinfoEdit",600,400,"${ctx}/rpt/frame/rptmgr/share/user");
		});
	}
	function initBtn(){
    	var buttons = [];
    	buttons.push({
    	    text : '取消',
    	    onclick : function() {
    		BIONE.closeDialog("shareEdit");
    	    }
    	});
    	buttons.push({
    	    text : '保存',
    	    onclick : f_save
    	});

    	BIONE.addFormButtons(buttons);
    }
	
	function f_save(){
		var ids = panel.val().join(",");
		$.ajax({
			cache : false,
			async : true,
			url : "${ctx}/rpt/frame/rptmgr/share/saveshare",
			dataType : 'json',
			data : {
				ids : ids,
				objType : "${objType}",
				id : "${id}",
				name : window.parent.name,
				remark : $("#remark").val()
			},
			type : "post",
			success : function(result) {
				BIONE.closeDialog("shareEdit","分享成功");
			}
		});
	}
	
	</script>
</head>
<body>
	<div id="template.center">
		<div class="container" style="padding-top: 10px;padding-right:30px">
			<div class="row">
				<div>
					<div style="width:10%; float:left;padding-left:10px;font-size: 12px;font-weight: 700;color: #959595;text-transform: uppercase;letter-spacing: 1px;">
					用户：
					</div>
					<div id="panel" style="width:85%; float:left">
					</div>
					<img id= "userBtn" src='${ctx}/images/classics/icons/user_add.png' style='position:relative;width:16px;height:16px;cursor:pointer;' title="添加用户" />
				</div>
				<div>
					<div style="width:10%; float:left;padding-left:10px;font-size: 12px;font-weight: 700;color: #959595;text-transform: uppercase;letter-spacing: 1px;">
							内容：
					</div>
					<div style="width:85%; float:left">
						<textarea id="remark" style="width:100%;height:120px; background-color: #fff;border: 1px solid transparent;border-radius: 4px;border-color:#ddd;resize:none"></textarea>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>