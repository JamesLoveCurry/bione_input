<%@ page language="java" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<%@ page
	import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<html>
<meta name="decorator" content="/template/template1_7.jsp">
<head>
<script type="text/javascript" src="${ctx}/js/datainput/remark/global.js"></script>
<script type="text/javascript" src="${ctx}/js/datainput/UDIP.js"></script>
<script type="text/javascript" src="${ctx}/js/datainput/remark/lib.js"></script>
<script type="text/javascript">
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	var manager, commitflag, colNameFlag, colTextFlag,rowobjUdp;
	var libData = {
		Rows : [ ]
	};
	$(function() {
		searchForm();
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '添加',
			icon : 'add',
			width : '50px',
			click : function() {
				commitflag = 'add';
				addNewRow();
			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '修改',
			icon : 'modify',
			width : '50px',
			click : function() {
				commitflag = 'update';
				addNewRow();
			}
		});
		/*
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '粘贴',
			icon : 'paste',
			width : '50px',
			click : function() {
				form_copy1();
			}
		});
		*/
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '删除',
			icon : 'delete',
			width : '50px',
			click : function() {
				deleteRow();
			}
		});
		BIONE.createButton({
			appendTo : "#searchbtn",
			text : '保存',
			icon : 'save',
			width : '50px',
			click : function() {
				saveRule3();
			}
		});
		initGrid();
		check();
		initData();
		var managers = $("#maingrid").ligerGetGridManager();
	});
	//搜索表单应用ligerui样式
	function searchForm() {
		$("#search").ligerForm({
			fields : [ {
				display : "名称",
				name : "columnName",
				newline : true,
				groupicon : groupicon,
				type : "text",
				cssClass : "field"
			}, {
				display : "代码",
				name : "columnText",
				newline : false,
				type : "text",
				cssClass : "field"
			} ]
		})

	}
	function addNewRow() {
		var manager = $("#maingrid").ligerGetGridManager();
		var columnName = $.trim(document.getElementById('columnName').value);
		var columnText = $.trim(document.getElementById('columnText').value);
		if (columnName == "" || columnName == null) {
			BIONE.tip('请输入名称');
			return null;
		}
		if (columnName.length > 4000) {
			BIONE.tip('名称不能超过4000个字符。');
			return null;
		}
		if (columnText == "" || columnText == null) {
			BIONE.tip('请输入代码');
			return null;
		}
		if (columnText.length > 4000) {
			BIONE.tip('代码不能超过4000个字符。');
			return null;
		}
		var data = manager.getData();
		if (commitflag == 'add') {
			for (i = 0; i < data.length; i++) {
				if (columnName.toUpperCase() == data[i].columnName.toUpperCase()
						|| columnText.toUpperCase() == data[i].columnText.toUpperCase()) {
					BIONE.tip('不能添加相同的名称和代码。');
					return;
				}
			}
		} else {
			if(colNameFlag==null || colNameFlag==''){
				BIONE.tip('需修改字典内容，请先双击要修改内容的行。');
				return;
			}
			for (i = 0; i < data.length; i++) {
				if ((columnName.toUpperCase() == data[i].columnName
						.toUpperCase() && colNameFlag.toUpperCase() != columnName.toUpperCase())
						|| (columnText.toUpperCase() == data[i].columnText
								.toUpperCase() && colTextFlag.toUpperCase() != columnText
								.toUpperCase())) {
					BIONE.tip('不能添加相同的名称和代码。');
					return;
				}

			}
		}

		if (commitflag == 'add') {
			manager.addRow({
				columnName : columnName,
				columnText : columnText
			});
		} else {
			if(rowobjUdp=='' || rowobjUdp==null){
				BIONE.tip('需修改字典内容，请先双击要修改内容的行。');
				return;
			}
			colNameFlag = columnName;
			colTextFlag = columnText;
			manager.updateCell("columnName", columnName, rowobjUdp);
			manager.updateCell("columnText", columnText, rowobjUdp);
		}
	}

	function initData() {
		if (parent.contents) {
			var content1 = parent.contents;
			var contentss;
			var contents = content1.split(";;");
			for (i = 0; i < contents.length; i++) {
				contentss = contents[i].split(":");
				manager.addRow({
					columnName : contentss[0],
					columnText : contentss[1]
				});
			}
		}
	}

	function initGrid() {
		manager = $("#maingrid").ligerGrid(
				{
					columns : [
							{
								display : '名称',
								width : "45%",
								name : 'columnName',
								editor : {
									type : 'text'
								}
							},
							{
								display : '代码',
								width : "45%",
								name : 'columnText',
								editor : {
									type : 'text'
								}
							}],
					rownumbers : true,
					rownumbersColWidth : 34,
		            frozenRownumbers:false,
					checkbox : true,
					data : libData,
					usePager : false,
// 					parms: [{
// 						name : 'searchTerms',
// 						value : parent.contents
// 					}],
// 					url:"${ctx}/udip/library/getDataLibContent",
					width : '100%',
					height : '99%',
					method : 'post',
					onDblClickRow : function(data, rowindex, rowobj) {
						colNameFlag = data['columnName'];
						colTextFlag = data['columnText'];
						$("#search [name='columnName']")
								.val(data['columnName']);
						$("#search [name='columnText']")
								.val(data['columnText']);
						rowobjUdp = rowobj;
					}
				});

	}
	function deleteRow(rowid) {
		var rows = manager.getSelectedRows();
		manager.deleteRange(rows);
	}
	function saveRule3() {
		var manager = $("#maingrid").ligerGetGridManager();
		var data = manager.getData();
		var paramStr = "";
		for (i = 0; i < data.length; i++) {
			if (i == data.length - 1) {
				paramStr = paramStr + data[i].columnName + ":"
						+ data[i].columnText;
			} else {
				paramStr = paramStr + data[i].columnName + ":"
						+ data[i].columnText + ";;";
			}

		}
		parent.getContentList(paramStr);
		BIONE.closeDialog("libContent");
	}
	function form_copy1(){
		var manager = $("#maingrid").ligerGetGridManager(); 
		// 从剪贴板上取得复制的内容
		var clipBoardContent = getClipboard()||'';
		var rows = [];
		var firstRowContent = "";
		// 通过换行来切割，每一行是一条数据
		rows = clipBoardContent.split("\r\n");
		if(rows.length > 0){
			// 将分隔符\t和空格替换成","
			firstRowContent = $.trim(formatString(rows[0]));
		}
		var rowCount = firstRowContent.split(",");
 		if(rowCount.length!=2){
 			BIONE.tip("复制的列数不对应，请检查！");
 			return;
 		}
 		if(rows.length>90){
 			$.ligerDialog.confirm('请不要粘贴超过90行的数据，这样运行速度会缓慢，确定粘贴？', function(yes) {
 				if(yes){
 					addRows(rows);
 				}
 			});
 		}else{
 			addRows(rows);
 		}
	}
	// 剪贴板中的内容，需要是固定的格式
	function formatString(str){
		var strFormat = str;
			if(str.indexOf("\t") != -1){
				strFormat = str.replace("\t",",");
			}
			if(str.indexOf(" ") != -1){
				strFormat = str.replace(" ",",");
			}
			return strFormat;
	}
	// 向grid中插入数据
	function addRows(hang){
		var data = manager.getData();
		var rows1=[],rows2=[];
		for (k = 0; k < data.length; k++) {
			rows1.push(data[k].columnName);
			rows2.push(data[k].columnText);
		}
		for ( var i = 0; i < hang.length; i++) {
	　    		var row = formatString(hang[i]).split(",");
	　    		if (row[0] == null || row[0] == "") {
					BIONE.tip("第"+(i+1)+"行的名称值存在为空。");
					return ;
			}
			if (row[1] == null || row[1] == "") {
				BIONE.tip("第"+(i+1)+"行的代码值存在为空。");
				return ;
			}
			if($.inArray(row[0],rows1)!=-1 || $.inArray(row[1],rows2)!=-1){
				BIONE.tip("第"+(i+1)+"行不能粘贴相同的名称和代码。");
				return;
			}else{
				rows1.push(row[0]);
				rows2.push(row[1]);
			}
	　    		manager.addRow({
	　   			columnName : $.trim(row[0]),
	    		columnText : $.trim(row[1])
	    	});
	　    	}
	}
	
	//获取剪切板信息
	function getClipboard() {
		if (window.clipboardData) {
			return (window.clipboardData.getData('Text'));            
			}            
		else if (window.netscape) {
			netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
			var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
			if (!clip) 
				return;                
			var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);                
			if (!trans) 
				return;                
			trans.addDataFlavor('text/unicode');
			clip.getData(trans, clip.kGlobalClipboard);                
			var str = new Object();                
			var len = new Object();                
			try {
				trans.getTransferData('text/unicode', str, len);
				}                
			catch (error) {                    
				return null;               
			}                
			if (str) {                    
				if (Components.interfaces.nsISupportsWString) 
					str = str.value.QueryInterface(Components.interfaces.nsISupportsWString);                    
				else if (Components.interfaces.nsISupportsString) 
					str = str.value.QueryInterface(Components.interfaces.nsISupportsString);
				else str = null;                
				}                
			if (str) {
				return (str.data.substring(0, len.value / 2));
				}            
			}            
		return null;        
	}
	
	function check() {
		$("#columnName").focus(
				function() {
					checkLabelShow(LibRemark.global.columnName);
					$("#checkLabelContainer").html(
							GlobalRemark.title
									+ LibRemark.global.columnName);
				});
		$("#columnText").focus(
				function() {
					checkLabelShow(LibRemark.global.columnText);
					$("#checkLabelContainer").html(
							GlobalRemark.title
									+ LibRemark.global.columnText);
				});
	}
</script>
</head>
<body>
</body>
</html>