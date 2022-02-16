<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template14.jsp">
<style type="text/css" >
#btn{
	float:left;
	overflow:hidden;
	margin:5px 15px 5px 15px;
}
#spread{
	float:left;
	overflow:hidden;
	margin:0px 0px 0px 0px;
}
</style>
<script type="text/javascript">
	var Title;
	// 分组图片
	var groupicon = "${ctx}/images/classics/icons/communication.gif";
	// 报表ID
	var rptId = "${rptId}";	
	// 报表对象
	var tmpRptInfoObj = '${rptInfo}'?'${rptInfo}':null;
	
	var tableNameEn = window.parent.tableNameEn;
	var rptInfoObj = null;
	
	if(typeof window.parent.baseInfo4Upt != "undefined"
			&& window.parent.baseInfo4Upt != null){
		rptInfoObj = window.parent.baseInfo4Upt;
	}
	if(tmpRptInfoObj != null){
		rptInfoObj = JSON2.parse(tmpRptInfoObj.replace(/[\n]/g,""));
	}
	
	// 编辑id
	 if(rptInfoObj != null){
		Title = rptInfoObj.Title;
	}else{
		Title = window.parent.Title;
	} 
	var gridUrl = "${ctx}/frs/submitConfig/detailrpt/getColumnInfoList?rptId="+rptId+"&tableNameEn="+tableNameEn;
	
	var checkTmpNodes = [];	
	
	// 初始化 form
	$(function() {
		//初始化grid
		initGrid();
		// 初始化数据		
		if(rptInfoObj != null){
			initData();	
		}
	});

	/*
	*	grid显示子表信息 
	*	by -- gby   2017.02.09
	*/
	function initGrid(){
		
		var parent = window.parent;
		// 初始化dialog高、宽
		dialogWidth = $(parent.window).width() * 0.90;
		dialogHeight = $(parent.window).height() - 40;	
		parent.rptGrid = grid = $("#maingrid11").ligerGrid({
			width : "100%",
			height : "99%",
			columns : [ {
				display : "可编辑列",
				name : "Title",
				width : "30%",
				align: "center"
			},{
				display : "是否可搜索",
				width : "10%",
				align: "center",
				render : function(rowdata){
 					var isChecked = "";
 					var columnName = $.trim(rowdata.ColumnName);
					for(i=0;i<window.parent.checkedSearch.length;i++){
						if(columnName==window.parent.checkedSearch[i]){
							isChecked = "checked";
						}
					} 
					return "<input id='"+rowdata.ColumnName+"' "+isChecked+" type='checkbox' name='' onclick='changeChecked(\""+$.trim(rowdata.ColumnName)+"\",\""+$.trim(rowdata.Title)+"\")'/>";
				}
			}],
			alternatingRow : true,//附加奇偶行效果行
			colDraggable : false,
			checkbox : false,
			selectRowButtonOnly : true ,
			dataAction : 'server',//从后台获取数据
			method : 'post',			
			url : gridUrl,
		});
		
		
	}
	function  changeChecked(columnName,title){
		var flag = true;
		if(window.parent.checkedSearch.length<1
				&&window.parent.uncheckedSearch.length<1){
			for(i=0;i<grid.data.Rows.length;i++){
				window.parent.uncheckedSearch.push($.trim(grid.data.Rows[i].ColumnName));
				window.parent.uncheckedSearchCN.push($.trim(grid.data.Rows[i].Title));
			}
		}
		if(window.parent.checkedSearch.length>0){
			for(i=0;i<window.parent.checkedSearch.length;i++){
				if(window.parent.checkedSearch[i]==columnName){
					window.parent.checkedSearch.splice(i,1);
					window.parent.checkedSearchCN.splice(i,1);
					window.parent.uncheckedSearch.push(columnName);
					window.parent.uncheckedSearchCN.push(title);
					flag = false;
					return;
				}
			}
			if(flag){
				for(i=0;i<window.parent.uncheckedSearch.length;i++){
					if(window.parent.uncheckedSearch[i]==columnName){
						window.parent.uncheckedSearch.splice(i,1);
						window.parent.uncheckedSearchCN.splice(i,1);
						window.parent.checkedSearch.push(columnName);
						window.parent.checkedSearchCN.push(title);
						return;
					}
				}
			}
		}else{
			for(i=0;i<window.parent.uncheckedSearch.length;i++){
 				if(window.parent.uncheckedSearch[i]==columnName){
					window.parent.uncheckedSearch.splice(i,1);
					window.parent.uncheckedSearchCN.splice(i,1);
					window.parent.checkedSearch.push(columnName);
					window.parent.checkedSearchCN.push(title);
					return;
				}
			}
		}
	}
	
	function prepareDatas4Save(){
		var saveObj = new Array();
		var val = "";
		$("#tb").find('input[type="checkbox"]:checked').each(function (){
			$this = $(this);
			val += $this.val() +";";
		});
		var test ={
				rptNm : window.parent.rptNm,
				rptNameEn : $.trim(tableNameEn),
				editType : "1",
				checkedSearch : window.parent.checkedSearch,
				checkedSearchCN : window.parent.checkedSearchCN,
				uncheckedSearch : window.parent.uncheckedSearch,
				uncheckedSearchCN : window.parent.uncheckedSearchCN

		};
		
		return test;
	}
	
	// 判断元素是否存在
	function getIndex(info, array){
		for(var i in array){
			if(array[i].id == info.id){
				return i;
			}
		}
		return -1;
	}
	
	function checkVal(){
		
		var tree = null;
		var editColCnVal = "";
		$('.ztree').each(function() {
			
			var temp = $(this);
			tree = $.fn.zTree.getZTreeObj(temp.attr('id'));
			var checkNodes = [];
			// 遍历找到选中的
			$.each(tree.getCheckedNodes(), function(i, node) {
				if(node.level!="1"){
					editColCnVal += node.text +";"
				}
			});
		});
		$("#mainform input[name=editColCn]").val(editColCnVal);
	}
	
	// 初始化数据
	function initData(){
		$("#mainform input[name=tableCn]").val(rptInfoObj.tableCn);
		$("#mainform input[name=tableEn]").val(rptInfoObj.tableEn);
		$("#mainform input[name=srcTableCn]").val(rptInfoObj.srcTableCn);
		$("#mainform input[name=srcTableEn]").val(rptInfoObj.srcTableEn);
		$("#mainform input[name=editColEn]").val(rptInfoObj.editColEn);
		$("#mainform input[name=editColCn]").val(rptInfoObj.editColCn);
		
		$.ajax({
			cache : false,
			async : true,			
			type: "post",
			url: "${ctx}/report/east/design/eastTmpCfgController.mo?_type=data_event&_field=getCheckedFileds&_event=POST&_comp=main&Request-from=dhtmlx&rptId="+rptId,			
			success:function(msg){
					if(msg.editColEn != null){
						var editColEns = msg.editColEn.split(";");
						for(var i=0;i<$("input[name =y]").size();i++){
							for(var j=0;j<editColEns.length-1;j++){																
								if($("input[name =y]").get(i).value == editColEns[j]){
									$($("input[name =y]").get(i)).attr("checked",true);
									$($("input[name =y]").get(i)).parent().parent().attr("bgcolor","#E4B7F9")
								}
							}
						}
					}
			}
		});
	}

	
	
	/* var zTreeNodes = [{"name":"报表", open:true, 
	children: [{ "name":"google", "url":"http://g.cn", "target":"_blank"},
		           { "name":"baidu", "url":"http://baidu.com", "target":"_blank"},
		           { "name":"sina", "url":"http://www.sina.com.cn", "target":"_blank"}]}]; */
	
	function doSelectAll(){
		        	   $("input[name=y]").attr("checked",$("#selectAll").is(":checked"));
		        	   if($("#selectAll").is(":checked")){
		        		   $(".tr").attr("bgcolor","#E4B7F9");
		        	   }else{
		        		   $(".tr").attr("bgcolor","#FFFFFF");
		        	   }
		           }
		           
		   
 	function onmouse(nameVal){
 		               var a =  $("#tr[name="+ nameVal +"]").find("input");
 		               var checked = a.attr("checked");
 		               if(checked!="checked"){
 		            	  $("#tr[name="+ nameVal +"]").attr("bgcolor","#F8D77E");
 		               }
		       		  
		       	   }
	function outmouse(nameVal){
		              var a =  $("#tr[name="+ nameVal +"]").find("input");
		               var checked = a.attr("checked");
                       if(checked!="checked"){
                    	   $("#tr[name="+ nameVal +"]").removeAttr("bgcolor");
         			 }
		       		   
		           }
	function change(nameVal){
		           var a =  $("#tr[name="+ nameVal +"]").find("input");
		       	   var checked = a.attr("checked");
		       	       if(checked=="checked"){
		       		        $("#tr[name="+ nameVal +"]").attr("bgcolor","#E4B7F9");
		       	       }
		           }
		       	
		       	
</script>
<title>Insert title here</title>
<style type="text/css">
	#divTree{
		height: 300px;
		overflow: auto;
	}
</style>
</head>
<body>
<div id="template.center">
  <div id="maingrid11">
		       			
      </div>
  <div id="editColDiv" > 
<!-- form -->
   <div id="divForm">
		      <form name="mainform" method="post" id="mainform" action=""></form>
		       			</div>
		       			<!-- 树 -->
       <div id="divTree">
		      <table border="1" bordercolor="#DFDFDF">	
		       		<tr height="30px;" align="center"><td colspan="2"><b>${rptInfoVO.rptNm }</b></td></tr>			
		       		<tr>
		       			<td width="110px;" height="25px;">选择要编辑的列：</td>
		       			<td colspan="2" align="center">是否可编辑 &nbsp;<input type="checkbox" id="selectAll" onclick="doSelectAll()">&nbsp;全选</td>
		       		</tr>
		       <tbody id="tb">								
		       	<c:forEach items="${paramInfoList}" var="paramInfo">
		       				<tr id="tr" class="tr" name="${paramInfo.paramDesc}">
		       					<td width="220px;" height="25px;" align="center">${paramInfo.paramDesc}</td>
		       					<td width="220px;" height="25px;" align="center" onmouseover="onmouse('${paramInfo.paramDesc}')" onmouseout="outmouse('${paramInfo.paramDesc}')"><input type="checkbox" name="y" value="${paramInfo.param }" onchange="change('${paramInfo.paramDesc}')"> &nbsp; 是</td>
		       				</tr>							
		       </c:forEach>
		     </tbody>						
		   </table>
		 </div>
       </div>
	</div>
  </body>
</html>