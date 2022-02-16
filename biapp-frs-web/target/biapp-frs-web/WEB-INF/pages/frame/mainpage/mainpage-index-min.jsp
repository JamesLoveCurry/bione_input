<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>AdminLTE 2 | Dashboard</title>
	<!-- Tell the browser to be responsive to screen width -->
	<meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
	<!-- Bootstrap 3.3.7 -->
	<link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/bootstrap/css/bootstrap.min.css">
	<!-- Theme style -->
	<link rel="stylesheet" href="${ctx}/static/AdminLTE-With-Iframe/dist/css/AdminLTE.min.css">

</head>
<body class="hold-transition skin-blue sidebar-mini">
<section class="content-header">
	<h1>
		快速导航
		<small>控制面板</small>
	</h1>
</section>

<section class="content">
	<!-- Small boxes (Stat box) -->
	<div class="row">
		<div class="col-lg-3 col-xs-3">
			<!-- small box -->
			<div class="small-box bg-aqua">
				<div class="inner">
					<h4>指标追溯分析</h4>
				</div>
				<div class="icon">
					<i class="icon ion-ios-shuffle-strong"></i>
				</div>
				<a href="javascript:indexAnalysisButClick();" class="small-box-footer">点击查看 <i class="fa fa-arrow-circle-right"></i></a>
			</div>
		</div>
		<!-- ./col -->
		<div class="col-lg-3 col-xs-3">
			<!-- small box -->
			<div class="small-box bg-green">
				<div class="inner">
					<h4>报表数据查询</h4>
				</div>
				<div class="icon">
					<i class="ion ion-pie-graph"></i>
				</div>
				<a href="javascript:detailAnalysisButClick()" class="small-box-footer">点击查看 <i class="fa fa-arrow-circle-right"></i></a>
			</div>
		</div>
		<!-- ./col -->
		<div class="col-lg-3 col-xs-3">
			<!-- small box -->
			<div class="small-box bg-yellow">
				<div class="inner">
					<h4>明细灵活查询</h4>
				</div>
				<div class="icon">
					<i class="icon ion-ios-color-wand"></i>
				</div>
				<a href="javascript:searchDetailButClick()" class="small-box-footer">点击查看 <i class="fa fa-arrow-circle-right"></i></a>
			</div>
		</div>
		<!-- ./col -->
		<div class="col-lg-3 col-xs-3">
			<!-- small box -->
			<div class="small-box bg-red">
				<div class="inner">
					<h4>指标信息查询</h4>
				</div>
				<div class="icon">
					<i class="icon ion-search"></i>
				</div>
				<a href="javascript:searchIndexButClick()" class="small-box-footer">点击查看 <i class="fa fa-arrow-circle-right"></i></a>
			</div>
		</div>
		<!-- ./col -->
	</div>
	<!-- /.row -->

	<!-- Main row -->
	<div class="row">
		<div class="col-md-12">
			<div class="box">
				<div class="box-header with-border">
					<h3 class="box-title">每月新增指标统计</h3>

					<div class="box-tools pull-right">
						<button type="button" class="btn btn-box-tool" data-widget="collapse"><i class="fa fa-minus"></i></button>
						<button type="button" class="btn btn-box-tool" data-widget="remove"><i class="fa fa-times"></i></button>
					</div>
				</div>
				<!-- /.box-header -->
				<div class="box-body">
					<div class="row">
						<!-- /.col -->
						<div class="col-md-12 col-xs-12">
							<p class="text-center">
								<strong>本月填报任务完成情况</strong>
							</p>

							<div class="progress-group">
								<span class="progress-text">月报一批</span>
								<span class="progress-number"><b>160</b>/200</span>

								<div class="progress sm">
									<div class="progress-bar progress-bar-aqua" style="width: 80%"></div>
								</div>
							</div>
							<!-- /.progress-group -->
							<div class="progress-group">
								<span class="progress-text">月报二批</span>
								<span class="progress-number"><b>310</b>/400</span>

								<div class="progress sm">
									<div class="progress-bar progress-bar-red" style="width: 80%"></div>
								</div>
							</div>
							<!-- /.progress-group -->
							<div class="progress-group">
								<span class="progress-text">季报一批</span>
								<span class="progress-number"><b>480</b>/800</span>

								<div class="progress sm">
									<div class="progress-bar progress-bar-green" style="width: 60%"></div>
								</div>
							</div>
							<!-- /.progress-group -->
							<div class="progress-group">
								<span class="progress-text">季报二批</span>
								<span class="progress-number"><b>250</b>/500</span>

								<div class="progress sm">
									<div class="progress-bar progress-bar-yellow" style="width: 50%"></div>
								</div>
							</div>
							<!-- /.progress-group -->
						</div>
						<!-- /.col -->
					</div>
					<!-- /.row -->
				</div>
				<!-- ./box-body -->
				<div class="box-footer">
					<div class="row">
						<div class="col-sm-3 col-xs-6">
							<div class="description-block border-right">
								<span class="description-percentage text-blue"><i class="fa fa-caret-left"></i> 0%</span>
								<h5 class="description-header">600</h5>
								<span class="description-text">填报总数</span>
							</div>
							<!-- /.description-block -->
						</div>
						<!-- /.col -->
						<div class="col-sm-3 col-xs-6">
							<div class="description-block border-right">
								<span class="description-percentage text-red"><i class="fa fa-caret-up"></i> 16%</span>
								<h5 class="description-header">298小时</h5>
								<span class="description-text">填报总时长</span>
							</div>
							<!-- /.description-block -->
						</div>
						<!-- /.col -->
						<div class="col-sm-3 col-xs-6">
							<div class="description-block border-right">
								<span class="description-percentage text-red"><i class="fa fa-caret-up"></i> 20%</span>
								<h5 class="description-header">1小时5分25秒</h5>
								<span class="description-text">填报平均时间</span>
							</div>
							<!-- /.description-block -->
						</div>
						<!-- /.col -->
						<div class="col-sm-3 col-xs-6">
							<div class="description-block">
								<span class="description-percentage text-green"><i class="fa fa-caret-down"></i> 18%</span>
								<h5 class="description-header">1200</h5>
								<span class="description-text">填报总人数</span>
							</div>
							<!-- /.description-block -->
						</div>
					</div>
					<!-- /.row -->
				</div>
				<!-- /.box-footer -->
			</div>
			<!-- /.box -->
		</div>
		<!-- /.col -->
	</div>
	<!-- /.row -->
</section>

<script type="text/javascript" src="${ctx}/js/jquery/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
    // 快速图标导航
    var detailAnalysisButClick = function(){
        top.addTabs({
            id: 'detailDataQuery',
            title: '报表数据查询',
            close: true,
            url: '/frs/integratedquery/rptquery',
            urlType: "relative"
        });
    }
    var indexAnalysisButClick = function(){
        top.addTabs({
            id: 'idxAnalysis',
            title: '指标追溯分析',
            close: true,
            url: '/rpt/frame/idx/idxanalysis',
            urlType: "relative"
        });
    }
    var searchDetailButClick = function(){
        top.addTabs({
            id: 'searchDt',
            title: '明细灵活查询',
            close: true,
            url: '/report/frame/datashow/detail',
            urlType: "relative"
        });
    }
    var searchIndexButClick = function(){
        top.addTabs({
            id: 'searchIdxInfo',
            title: '指标信息查询',
            close: true,
            url: '/report/frame/idx/preview',
            urlType: "relative"
        });
    }

</script>

</body>
</html>
