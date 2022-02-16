<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid,url,id;
	function initSearchForm() {
            $("#search").ligerForm({
                fields : [{
                    display : '对象类型',
                    name : "type",
                    comboboxName : 'resDefNoBox',
                    newline : true,
                    type : "select",
                    cssClass : "field",
                    options : {
                          initValue : 'AUTH_RES_MENU',
                          url : "${ctx}/bione/admin/auth/getBioneAuthResDefInfo.json"
                    },
                          attr : {
                             field : "resDefNo",
                             op : "="
                     },
                    validate : {
                    	required : true
                    }
                },{
                      display : '授权对象',
                      name : "resName",
                      newline : false,
                      comboboxName : 'resNameBox',
                      type : "select",
                      cssClass : "field",
                      options : {
                          url : "${ctx}/bione/admin/auth/getAuthObjCombo.json"
                      },
                      attr : {
                          field : "resName",
                          op : "="
                      }
                },{
                     display : '资源名称',
                     name : 'resourceName',
                     newline : false,
                     cssClass : "field",
                     type : 'text',
                     attr : {
                            field : "resourceName",
                            op : "="
                     }
              }]
            });
    	};

	function initGrid() {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
				display : '资源标识',
				name : 'resDefNo',
				width : "30%",
				align : 'center'
                },{
                     display : '授权对象',
                     name : 'objDefNo',
                     width : "32%",
                     align : 'center'
                 },{
                    display : '资源名称',
                    name : 'resId',
                    width : "32%",
                    align : 'center'
                }],
                width : '100%',
                height : '99%',
                isScroll : true,
                checkbox: false,
                dataAction : 'server',
                usePager : true,
                alternatingRow : true,
                colDraggable : true,
                delayLoad : true,
                url : url,
                pageParmName : 'page',
                pagesizeParmName : 'pagesize',
                checkbox : false,
                rownumbers : true
		});
		loadGrid();
	};
	$(function() {
	    url = "${ctx}/bione/admin/auth/getUserObjResRel.json";
		initSearchForm();
		initGrid();
		BIONE.addSearchButtons("#search", grid, "#searchbtn");
        jQuery.metadata.setType("attr", "validate");
        BIONE.validate("#formsearch");
	});

	function loadGrid(){
		var rule = BIONE.bulidFilterGroup("#search");
		id = "${id}";
		grid.setParm("id",id);
		if (rule.rules.length) {
			grid.setParm("condition",JSON2.stringify(rule));
			grid.setParm("newPage",1);
			grid.options.newPage=1
		} else {
			grid.setParm("condition","");
			grid.setParm('newPage', 1);
			grid.options.newPage=1
		}
		grid.loadData();
	}

</script>
</head>
<body>
</body>
</html>