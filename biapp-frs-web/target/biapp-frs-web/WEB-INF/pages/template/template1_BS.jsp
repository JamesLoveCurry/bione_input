<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8">
    <!-- 工作区填充满样式，勿动 -->
    <style>
        html,body{ height: 98%!important;}
    </style>

    <%@ include file="/common/jquery_load_BS.jsp"%>
    <%@ include file="/common/ligerUI_load_BS.jsp"%>
    <%@ include file="/common/Bootstrap_load.jsp"%>

    <link rel="stylesheet" type="text/css"
          href="${ctx}/css/classics/template/template1.css" />

    <sitemesh:write property='head' />

</head>
<body>

<div id="center" style="height: 100%">
    <div class="box box-default" id="mainsearch" style="margin: 2px;">
        <!-- /.box-header -->
        <div class="box-body">
            <div class="row">
                <div class="col-md-12" >
                    <div id="searchbox" class="searchbox">
                        <form id="formsearch">
                            <div id="search"></div>
                            <div class="l-clear"></div>
                        </form>
                        <div id="searchbtn" class="btn-group"></div>
                    </div>
                </div>
                <!-- /.col -->
            </div>
            <!-- /.row -->
        </div>
    </div>
    <!-- /.box -->

    <!-- Main Grid -->
    <div class="container-fluid">
         <div class="row">
             <div id="maingrid" class="maingrid"></div>
         </div>
    </div>
</div>

</body>
</html>