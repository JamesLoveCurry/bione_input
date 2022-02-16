<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<meta name="decorator" content="/template/template5.jsp">
<head lang="en">
    <meta charset="UTF-8">
    <title></title>
    <style type="text/css">
        dl, dt, dd {
            margin: 0;
            padding: 0;
            list-style: none;
        }
        .l-list dt {
            display: block;
            height: 100px;
            float: left;
            width: 100px;
        }
        .l-list dd {
            display: block;
            float: left;
            width: 200px;
        }
    </style>
    <script type="text/javascript">
    	$(function(){
    		var url = "";
    		if("${dataType}" == "01"){
    			url = "${ctx}/report/frame/datashow/idx/chooseMoney";
    		}else {
    			url = "${ctx}/report/frame/datashow/idx/chooseNumbers";
    		}
    		$.ajax({
    			url: url,
    			type: 'post',
    			dataType: 'json',
    			success: function(n) {
    				var html = "";
    				for(var i=0;i<n.length;i++){
    					if(n[i].id == window.parent.measureFilters["${indexNo}" + ("${measureNo}" == "" ? "" : "-${measureNo}")] ){
    						html += '<dd><input type="radio" name="sel" value="' + n[i].id +'" id="a" text="' + n[i].text + '" checked="checked"><label for="a">'+ n[i].text +'</label></dd>';
    					}else{
    						html += '<dd><input type="radio" name="sel" value="' + n[i].id +'" id="a" text="' + n[i].text + '"><label for="a">'+ n[i].text +'</label></dd>';
    					}
    				}
    				$("dt").after(html);
    				
    			}
    		});
    		var btns = [{
    			text : '取消',
    			onclick : function() {
    				BIONE.closeDialog("chooseOrg");
    			}
    		},{
    			text : '确定',
    			onclick : f_save
    		}];
    		BIONE.addFormButtons(btns);
    		
    		
    		
    	});
    	function f_save(){
    		window.parent.measureFilters["${indexNo}" + ("${measureNo}" == "" ? "" : "-${measureNo}")] = $("input[name=sel]:checked").val();
    		window.parent.measureFilterName["${indexNo}" + ("${measureNo}" == "" ? "" : "-${measureNo}")] = $("input[name=sel]:checked").attr("text");
    		window.parent.addGridData();
    		var column = window.parent.grid.getColumns();
    		$(column).each(function (){
                        if (this.name == ("${indexNo}" + ("${measureNo}" == "" ? "" : "-${measureNo}"))){
                        	window.parent.grid.changeHeaderText(("${indexNo}" + ("${measureNo}" == "" ? "" : "-${measureNo}")), 
                        			this.text + "(" + $("input[name=sel]:checked").attr("text") + ")");
                        }
			});
    		
    		BIONE.closeDialog("chooseOrg");
    	}
    </script>
</head>
<body>
<div id="template.center" >
    <form>
        <dl class="l-list" style="padding-top : 10px;padding-left : 20px">
            <dt>单位选择：</dt>
        </dl>
    </form>
</div>
</body>
</html>