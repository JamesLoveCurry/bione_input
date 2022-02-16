<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
<meta name="decorator" content="/template/template1_BS.jsp">
<script type="text/javascript">var ctx="${ctx}"</script>
<!-- 基础的JS和CSS文件 -->
<script type="text/javascript">
	var grid,id;
	var checkId ='${checkId}';
	var dataDate ='${dataDate}';
	var orgNo ='${orgNo}';
	var validDesc = '${validDesc}';
	var indexNo = '${indexNo}';
	function initSearchForm() {
        $("#search").ligerForm({
            fields : []
        });
	};

	function initGrid(url) {
		grid = $("#maingrid").ligerGrid({
			columns : [ {
                    display : '机构',
                    name : 'orgNm',
                    width : "20%",
                    align : 'center',
                    render : taskObjNmRender
                },{
                   display : '变动金额',
                   name : 'compareVal',
                   width : "20%",
                   align : 'center'
               },{display : '异动说明', name : 'validDesc', width : "35%"
               },{display : '操作',
                  name : 'serialNumber',
                 width : "15%",
                 align: 'center',
                render : opertorDesc}
               ],
                width : '100%',
                height : '100%',
                isScroll : true,
                checkbox: false,
                dataAction : 'server',
                usePager : false,
                alternatingRow : true,
                colDraggable : true,
                delayLoad : true,
                url : url,
                sortName : 'compareVal',
                pageParmName : 'page',
                pagesizeParmName : 'pagesize',
                checkbox : false,
                rownumbers : true
		});
		loadGrid();
	};
	$(function() {
		initSearchForm();
		initGrid('${ctx}/rpt/frs/rptfill/changeAnalysisChildOrg?orgNo=' + orgNo + "&dataDate=" + dataDate + "&checkId=" + checkId+ "&indexNo=" + indexNo);
        jQuery.metadata.setType("attr", "validate");
        BIONE.validate("#formsearch");
	});

	function detail(validDescs) {
	    parent.BIONE.commonOpenDialog('异动说明详情', 'validDescWin',"550", "300",'${ctx}/rpt/frs/rptfill/getChildOrgWarnValidDescDetail?validDesc='+validDescs);
    }

    taskObjNmRender = function(rowdata) {
    debugger;
        var aa = rowdata.orgNm;
        return "<a href='javascript:void(0)' class='link' id='"+ rowdata.serialNumber +"' onclick='onShowRpt(\""+ rowdata.orgNo+"\")'>"+ aa + "</a>";
    };

    opertorDesc = function(rowdata) {
            var aa = "查看";
            return "<a href='javascript:void(0)' class='link' id='11111' onclick='detail(\""+rowdata.validDesc+"\")'>查看</a>";
    };

    function onShowRpt(orgNos) {
        initGrid('${ctx}/rpt/frs/rptfill/changeAnalysisChildOrg?orgNo=' + orgNos + "&dataDate=" + dataDate + "&checkId=" + checkId+ "&indexNo=" + indexNo);
    }

	function loadGrid(){
		var rule = BIONE.bulidFilterGroup("#search");
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