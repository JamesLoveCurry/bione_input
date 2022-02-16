<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/classics/template/template19.css" />
<script type="text/javascript">
	$(function() {
		templateshow();
	});
</script>
<script type="text/javascript">
	var selectedIds = [];
	function getSelectedIds() {
		return $.unique(selectedIds);
	}
	function clearSelectedIds() {
		selectedIds = [];
	}
	function templateshow() {
    	$("#bottom").height(40); 
    	$("#baseinfo_div").height(43);
    	$("#attach_div").height(147);
    	$("#center").height($(document).height() - $("#bottom").height() - 8);
		$("#editor_div").height($("#center").height() - $("#baseinfo_div").height() - $("#attach_div").height() - 8);
		/* if (CKEDITOR && CKEDITOR.config) {
			CKEDITOR.config.height = $("#editor_div").height() - 4*36;
		} */
    }
    function loadAttachToolbar(btns) {
    	/* text : '删除',
		click : attach_delete,
		icon : 'delete',
		operNo : 'attach_delete' */
    	var btnBuffer = [];
    	btnBuffer.push('<div class="l-panel-topbar l-toolbar" >');
    	if (btns && btns.length > 0) {
    		for (var _idx in btns) {
    			var btn = btns[_idx];
    			var toolbarid = "server_" + btn.icon;
    			btnBuffer.push('<div class="l-toolbar-item l-panel-btn l-toolbar-item-hasicon" toolbarid="' + toolbarid + '">');
        		btnBuffer.push('	<span>' + btn.text + '</span>');
        		btnBuffer.push('	<div class="l-panel-btn-l"></div>');
        		btnBuffer.push('	<div class="l-panel-btn-r"></div>');
        		btnBuffer.push('	<div class="l-icon l-icon-' + btn.icon + '"></div>');
    			btnBuffer.push('</div>');
    			btnBuffer.push('<div class="l-toolbar-separator"></div>');
    		}
    	} else {
    		btnBuffer.push('<div class="l-toolbar-separator"></div>');
    	}
    	btnBuffer.push('</div>');
    	/* render */ 
    	$("#attach_box").html(btnBuffer.join(''));
    	/* bind events */
    	for (var _idx in btns) {
    		var btn = btns[_idx];
    		var toolbarid = "server_" + btn.icon;
    		$("[toolbarid='"+toolbarid+"']").click(btn.click);
    	}
    }
    function loadAttachGrid(items, cols) {
    	clearSelectedIds();
    	// 已存在，则清除；
    	if ($("#attach_files") && $("#attach_files").html()) {
    		$("#attach_files").remove();
    	}
    	if (!items || items.length<=0) {
    		return ;
    	}
    	//=====================================
    	// has elements
    	//=====================================
    	var athGrid = [];
    	var cellSize = 200;
    	if (!cols || isNaN(cols) || cols<=0) {
    		cols = 3;
    	}
    	cellSize = ($("#attach_box").width())/cols - 29;
    	// 
    	athGrid.push('<div id="attach_files" class="l-grid-body l-grid-body2 l-scroll" >'); //style="height: 50px; "
    	athGrid.push('  <div class="l-grid-body-inner" style="width: 100%;">');
    	athGrid.push('    <table class="l-grid-body-table" cellpadding="0" cellspacing="0">');
    	athGrid.push('      <tbody>');
    	for (var _idx in items) {
    		var attach = items[_idx];
    		if (_idx==0 || _idx%cols==0) {
	    		if (_idx!=0) {
	    			athGrid.push('        </tr>');
	    		}
	    		athGrid.push('        <tr class="l-grid-row">');
	    	}
			athGrid.push('          <td athid="'+ attach.attachId +'" class="l-grid-row-cell l-grid-row-cell-checkbox" style="width:29px; text-align:center; ">');
			athGrid.push('            <div class="l-grid-row-cell-inner">');
			athGrid.push('              <span class="l-grid-row-cell-btn-checkbox"></span>');
			athGrid.push('            </div>');
			athGrid.push('          </td>');
			athGrid.push('          <td athid="'+ attach.attachId +'" class="l-grid-row-cell " style="width:'+ cellSize +'px;  ">');
			athGrid.push('            <div class="l-grid-row-cell-inner" style="width:'+cellSize+'px; text-align:left; ">');
			athGrid.push('              <img src="'+ getIcon(attach.attachType) +'" style="vertical-align:middle;" alt="'+ attach.attachName +'" />&nbsp;'+ attach.attachName +'</div>');
			athGrid.push('          </td>');
    	}
    	if (items.length % cols != 0) {
			for (var i=0; i<(cols-(items.length % cols)); i++) {
				athGrid.push('          <td class="l-grid-row-cell " style="width:29px">');
				athGrid.push('            <div class="l-grid-row-cell-inner">');
				athGrid.push('              <span>&nbsp;</span>');
				athGrid.push('            </div>');
				athGrid.push('          </td>');
				athGrid.push('          <td class="l-grid-row-cell " style="width:'+ cellSize +'px;  ">');
				athGrid.push('            <div class="l-grid-row-cell-inner" style="width:'+ cellSize +'px; text-align:left; ">&nbsp;</div>');
				athGrid.push('          </td>');
			}
		}
    	athGrid.push('        </tr>');
		// 
		athGrid.push('      </tbody>');
		athGrid.push('    </table>');
		athGrid.push('  </div>');
		athGrid.push('</div>');
    	//
    	$("#attach_box").append(athGrid.join(''));
    	//=============================================
    	// bind event to checkbox
    	//=============================================
    	$("td[athid]").click(function() {
        	var athid = $(this).attr("athid");
        	$("td[athid='"+athid+"']").each(function() {
        		if ($(this).hasClass("ll-selected")) {
        			$(this).removeClass("ll-selected");
        			$(".l-grid-row-cell-btn-checkbox", this).removeClass("l-selected");
    				$(".l-grid-row-cell-inner", this).removeClass("l-selected");
    				//
    				var _exist = $.inArray(athid, selectedIds);
    				if (_exist >= 0) {
    					selectedIds.splice(_exist, 1);	
    				}
        		} else {
        			$(this).addClass("ll-selected");
        			$(".l-grid-row-cell-btn-checkbox", this).addClass("l-selected");
    				$(".l-grid-row-cell-inner", this).addClass("l-selected");
    				//
    				var _exist = $.inArray(athid, selectedIds);
    				if (_exist < 0) {
    					selectedIds.push(athid);	
    				}
        		}
        	});
        });
    }
    var deftIcon = "${ctx}/images/classics/fileicons/default.gif";
    function getIcon(filetype) {
    	var typeArray = getTypeIcons();
    	for (var _idx in typeArray) {
    		if (typeArray[_idx].type == filetype.toLowerCase()) {
    			return typeArray[_idx].icon;
    		}
    	}
    	return deftIcon;
    }
   	function getTypeIcons() {
   		return [
     	    { type : "zip",   icon : "${ctx}/images/classics/fileicons/zip.gif" },
 	    	{ type : "rar",   icon : "${ctx}/images/classics/fileicons/zip.gif" },
 	    	{ type : "txt",   icon : "${ctx}/images/classics/fileicons/txt.gif" },
 	    	{ type : "pdf",   icon : "${ctx}/images/classics/fileicons/pdf.gif" },
 	    	{ type : "doc",   icon : "${ctx}/images/classics/fileicons/doc.gif" },
 	    	{ type : "docx",  icon : "${ctx}/images/classics/fileicons/doc.gif" },
 	    	{ type : "xls",   icon : "${ctx}/images/classics/fileicons/xls.gif" },
 	    	{ type : "xlsx",  icon : "${ctx}/images/classics/fileicons/xls.gif" },
 	    	{ type : "ppt",   icon : "${ctx}/images/classics/fileicons/ppt.gif" },
 	    	{ type : "pptx",  icon : "${ctx}/images/classics/fileicons/ppt.gif" },
 	    	{ type : "jpg",   icon : "${ctx}/images/classics/fileicons/jpg.gif" },
 	    	{ type : "jpeg",  icon : "${ctx}/images/classics/fileicons/jpg.gif" },
 	    	{ type : "gif",   icon : "${ctx}/images/classics/fileicons/jpg.gif" },
 	    	{ type : "png",   icon : "${ctx}/images/classics/fileicons/jpg.gif" },
 	    	{ type : "tif",   icon : "${ctx}/images/classics/fileicons/jpg.gif" }
     	];
   	}
</script>
<sitemesh:write property='head' />

</head>
<body>
	<div id="center">
		<!-- baseinfo begin -->
		<div id="baseinfo_div" class="info_content">
			<sitemesh:div property='template.baseinfo_div' />
		</div>
		<!-- baseinfo end -->
		<!-- "editor_div" begin -->
		<div id="editor_div" class="info_content" style="width: 100%; align: center; ">
		</div>
		<!-- "editor_div" end -->
		<!-- attach_div begin -->
		<div id="attach_div" class="attach_div" style="width: 98%; align: left; ">
			<div style="height:20px; line-height:20px; text-align:center; border-bottom:#ccc 1px solid; color:#999; ">&nbsp;</div>
			<div class="attach_icon">
				<a class = "icon-search" ></a><span>附件</span>
			</div>
			<div id="attach_box" class=attach_box>
			</div>
		</div>
		<!-- attach_div end -->
	</div>
	<!-- bottom-button begin -->
	<div id="bottom">
		<div class="form-bar">
			<div class="form-bar-inner" style="padding-top: 5px"></div>
		</div>
	</div>
	<!-- bottom-button end -->
</body>
</html>