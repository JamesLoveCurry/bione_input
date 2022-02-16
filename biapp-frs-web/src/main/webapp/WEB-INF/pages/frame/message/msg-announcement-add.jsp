<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template19.jsp">
<head>

<script type="text/javascript">
	var mainform , ids = [];
	var cacheAttachsADD = [];
	var cacheAttachsDEL = [];
	var isNew = false;
	var OPT_ADD_FLAG = "add";
	var OPT_DEL_FLAG = "delete";
	var msgTypeOpts = [];  //msgTypeOpts = [{"text":"公告", "id":"anno"}];
	var titleWidth = 200;
	$(function() {
		if (!"${id}") {
			isNew = true;
		}
		computeTitleWidth();
		getMsgTypes();
		initBasicForm();
		initGrid();
    });
	function initGrid() {
		$('#grid').ligerGrid({
			title: '附件',
			headerImg: '${ctx}/images/classics/icons/attach.png',
			columns: [{
				display: '名称',
				name: 'attachName',
				align: 'left',
				width: 400
			}, {
				display: '类型',
				name: 'attachType',
				align: 'center',
				width: 100
			}, {
				display: '大小',
				name: 'attachSize',
				align: 'center',
				width: 100
			}],
			width: '100%',
			height: 170,
			usePager: false,
			checkbox: false,
			toolbar: {
				items: [{
					text : '增加',
					click : attach_add,
					icon : 'add',
					operNo : 'attach_add'
				}, {
					text : '删除',
					click : attach_delete,
					icon : 'delete',
					operNo : 'attach_delete'
				}/* , {
					text : '下载',
					click : attach_download,
					icon : 'download',
					operNo : 'attach_download'
				}  */]
			},
			dataAction: 'server',
			url: isNew === true ? '' : '${ctx}/bione/message/attach/${id}/listAttach.json',
			method: 'post'
		});
	}
	function computeTitleWidth() {
		titleWidth = $(document).width() - 150;
	}
	/** 初始化基础信息form */
	function initBasicForm() {
		$('#baseinfo_div').height(55);
		mainform = $("#mainform");
		mainform.ligerForm({
			fields : [ 
				{ name: "announcementId", type: "hidden" },
				{ name: "logicSysNo", type: "hidden" },
//				{ name: "announcementDetail", type: "hidden" },
				{ name: "announcementSts", type: "hidden" },
				{ name: "lastUpdateUser", type: "hidden" },
				{ name: "addAttachs", type: "hidden" },
				{ name: "delAttachs", type: "hidden" },
				{
					display : "公告类别",
					name : "announcementType",
					comboboxName : "msgTypeNoID",
					newline : false,
					type : 'select',
					options : {
// 						data : msgTypeOpts,
				    	valueFieldID : 'announcementType',
				    	initValue: '01',
						url: '${ctx}/bione/variable/param/find?typeNo=SysBulletin',
						valueField: 'paramValue',
						textField: 'paramName'
			    	},
					validate : {
						required : true
					}
				},
				{ 
					name: "announcementDetail", 
					type: "hidden" ,
					validate : {
						required : true
					} 
				}, {
					id: 'authObj',
					display : "授权对象",
					name : "author",
					newline : false,
					type : 'popup',
					validate : {
						required : true
					},
					options : {
						valueFieldID: 'authItem',
						cancelable: false,
						onButtonClick: f_openAuthObjWin
			    	}
				}, {
					display : "标题",
					name : "announcementTitle",
					newline : true,
					type : "text",
					width : titleWidth,
					validate : {
						required : true,
						maxlength : 200
					}
				}]
		});
		if (!isNew) {
			BIONE.loadForm($("#mainform"), {
				url : "${ctx}/bione/msg/announcement/${id}",
				complete : function() {
					if(CKEDITOR){
						setTimeout(function(){
							CKEDITOR.instances.editor1.setData($("#announcementDetail").val());
							$(".cke_editable").html($("#announcementDetail").val());
						})
					}
				}
			});
			$.ajax({
				url: '${ctx}/bione/msg/announcement/auth/${id}',
				type: 'post',
				dataType: 'json',
				success: function(data) {
					var val = [];
					if (data) {
						var authObj = liger.get('authObj');
						$.each(data, function(i, n) {
							val.push({
								objId: n.id.objId,
								objDefNo: n.objDefNo
							});
						});
						window.selected = val;
						authObj.setText('共' + data.length + '个对象');
						authObj.setValue(JSON2.stringify(val));
					}
				}
			});
		}
		else{
		}
		jQuery.metadata.setType("attr", "validate");
		BIONE.validate(mainform);
		var buttons = [];
		buttons.push({
			text : '取消',
			onclick : doMsgCancel
		});
		buttons.push({
			text : '保存',
			onclick : doMsgSave
		});
		BIONE.addFormButtons(buttons);
		
		// 检查标题是否符合规范，防止存储型XSS 共计
		if ($("#announcementTitle").length > 0) {
			$("#announcementTitle").rules("add", {
				remote: {
					url: "${ctx}//bione/msg/announcement/announcementTitleValid",
					type: "post",
					async: false
				}, messages: {remote: "标题不符合规范"}
			});
		}
		
	}
	/** 初始化消息类型 */
	function getMsgTypes() {
		var opts = [{ text: "系统公告", id : "系统公告"}, { text: "公告", id : "公告"}];
		msgTypeOpts = opts;
	}
	
	function refreshGrid(optType,params) {
		var grid = liger.get('grid');
		if(optType == OPT_ADD_FLAG){
			if(params){
				grid.addRows(params);
			}
		}
		if (optType == OPT_DEL_FLAG) {
			var row = getGridRow(params);
			if (row) {
				grid.deleteRow(row);
			}
		}
		
	}
	function getGridRow(attachId) {
		var rows = liger.get('grid').rows;
		var row = null;
		$.each(rows, function(i, n) {
			if (n.attachId == attachId) {
				row = n;
				return false;
			}
		});
		return row;
	}
	/** 初始化附件列表 */
	function initAttachGrid() {
		if (isNew) {
			loadAttachGrid(cacheAttachsADD, 4);
		} 
		else {
			$.ajax({
				cache : false,
				async : true,
				type : "POST",
				url : "${ctx}/bione/message/attach/${id}/listAttach.json",
				dataType : "json",
				success : function(data) {
					var arr = [];
					if (data.Rows && data.Rows.length>0) {
						for (var _idx in data.Rows) {
							arr.push(data.Rows[_idx]);
						}
					}
					if (cacheAttachsDEL && cacheAttachsDEL.length>0) {
						for (var _idx in cacheAttachsDEL) {
							for (var _n in arr) {
								if (cacheAttachsDEL[_idx] == arr[_n].attachId) {
									arr.splice(_n, 1);	
								}
							}
						}
					}
					if (cacheAttachsADD && cacheAttachsADD.length>0) {
						for (var _idx in cacheAttachsADD) {
							arr.push(cacheAttachsADD[_idx]);
						}
					}
					loadAttachGrid(arr, 4);
				},
				error : function(result, b) {
					//BIONE.tip('发现系统错误 <BR>错误码：' + result.status);
				}
			});
		}
	}
	function initAttachButtons() {
		var btns = [
		{
			text : '增加',
			click : attach_add,
			icon : 'add',
			operNo : 'attach_add'
		}, {
			text : '删除',
			click : attach_delete,
			icon : 'delete',
			operNo : 'attach_delete'
		}/* , {
			text : '下载',
			click : attach_download,
			icon : 'download',
			operNo : 'attach_download'
		}  */];
		loadAttachToolbar(btns);
	}
	/** utility */
	function validEditor() {
		$("#announcementDetail").val("");
		if(CKEDITOR &&
				CKEDITOR.instances.editor1 &&
				CKEDITOR.instances.editor1.getData() != null &&
				CKEDITOR.instances.editor1.getData() != "") {
			$("#announcementDetail").val(CKEDITOR.instances.editor1.getData());
			return true;
		}
		return false;
	}
	function doMsgSave() {
		if (!validEditor()) {
			BIONE.showError("请编写公告内容！");
			return ;
		}
		$("#addAttachs").val($.toJSON(cacheAttachsADD));
		$("#delAttachs").val($.toJSON(cacheAttachsDEL));
		
		BIONE.submitForm($("#mainform"), function() {
			cacheAttachsADD = [];
			cacheAttachsDEL = [];
			BIONE.closeDialogAndReloadParent("msgDefManage", "maingrid", "保存成功");
		});
	}
	function doMsgCancel() {
		$.ligerDialog.confirm('确实要取消编辑该信息吗?', function(yes) {
			if (yes) {
				BIONE.closeDialogAndReloadParent("msgDefManage", "maingrid");
			}
		});
	}
	/** attach utility */
	function achieveIds() {
		ids = [];
		ids = getSelectedIds();
	}
	function attach_add() {
		BIONE.commonOpenDialog('上传文件', 'attachUpload', 562, 334, '${ctx}/bione/message/attach/newAttach?type=msg');
	}
	function attach_delete() {
		var row = liger.get('grid').getSelectedRow();
		if (row) {
			$.ligerDialog.confirm('确实要删除这些记录吗?', function(yes) {
				if (yes) {
					afterAttachOperat(OPT_DEL_FLAG, row.attachId);
				}
			});
		}
	}
	function attach_download() {
		achieveIds();
		var row = liger.get('grid').getSelectedRow();
// 		if (ids && ids.length == 1) {
		if (row) {
			var attachId = row.attachId;
			$.ajax({
				type : 'get',
				url : '${ctx}/bione/message/attach/checkAttchDownlowd?attachId='+attachId,
				success : function(data){
					if(data == true){
						$('body').append($('<iframe id="download"/>'));
						$("#download").attr('src', '${ctx}/bione/message/attach/startDownload?attachId=' + attachId);
					}else{
						BIONE.tip("新增加的附件不能下载");
					}
				}
			})
			
		} else if (ids.length > 1) {
			BIONE.tip('只能选择一条记录');
		} else {
			BIONE.tip('请选择记录');
		}
	}
	function afterAttachOperat(optType, attachInst) {
		/* 新增或删除之后都调用此方法 */
		if (optType == OPT_ADD_FLAG) {
			cacheAttachsADD.push(attachInst);
			refreshGrid(optType,attachInst);
		}
		else if (optType == OPT_DEL_FLAG) {
			// 删除存的只有id 
			var flag = false;
			if (cacheAttachsADD && cacheAttachsADD.length>0) {
				for (var _idx in cacheAttachsADD) {
					if (cacheAttachsADD[_idx].attachId == attachInst) {
						cacheAttachsADD.splice(_idx, 1);
						flag = true;
						break;
					}
				}
			}
			if (!flag) {
				cacheAttachsDEL.push(attachInst);
			}
			refreshGrid(optType,attachInst);
		}
		//initAttachGrid();
	}
	
	
	// 打开授权对象窗口
	function f_openAuthObjWin() {
		var auth = window.auth = {};
		BIONE.commonOpenLargeDialog('授权对象', 'author', '${ctx}/bione/msg/announcement/auth');
	}
	
	function f_setAuth(text, data) {
		var authObj = liger.get('authObj');
		authObj.setText(text);
		authObj.setValue(JSON2.stringify(data));
	}
</script>

</head>
<body>
	<div id="template.baseinfo_div">
		<form id="mainform" action="${ctx}/bione/msg/announcement" method="post"></form>
		
	</div>
</body>
</html>