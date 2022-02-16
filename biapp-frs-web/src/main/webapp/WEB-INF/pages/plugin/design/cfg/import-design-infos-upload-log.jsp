<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="/common/meta.jsp" %>
<%@ include file="/common/taglibs.jsp" %>
<%@ include file="/common/jquery_load.jsp" %>
<html>
<head>
    <title>Title</title>
    <style type="text/css">
        body {
            font-family: "微软雅黑", "宋体", Arial, sans-serif;
            font-size: 12px;
        }
    </style>
</head>
<body>
<div id="logs">

</div>
</body>
<script type="text/javascript">
    //假设每隔5秒发送一次请求
    window.onload = function () {
        getApi();
    };

    function getApi() {
        //设置时间 5-秒  1000-毫秒  这里设置你自己想要的时间
        setTimeout(getApi, parent.queryInterva * 1000);
        if (parent.logFlag) {
            $.ajax({
                url: '${ctx}/report/frame/design/cfg/importDesignInfosUploadLogShow',
                type: 'get',
                dataType: 'json',
                data: {
                    uuid: parent.uuid
                },
                success: function (data) {
                    $('#logs').html("");
                    $('#logs').append(data.out);
                	//查询间隔逐渐拉长，最高10秒一次
                	if(10 > parent.queryInterva){
                		parent.queryInterva++;
                	}
                    if (data.out.indexOf("程序处理异常") != -1){
                        parent.logFlag = false;
                        parent.queryInterva = 1;
                    }
                }
            })
        }
    }
</script>
</html>
