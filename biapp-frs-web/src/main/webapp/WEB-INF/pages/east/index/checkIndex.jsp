<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8" />
		<title></title>
		<link rel="stylesheet" type="text/css" href="${ctx}/css/east/index.css" />
		<script type="text/javascript" src="${ctx}/js/echarts/js/echarts.min.js"></script>
		<script type="text/javascript" src="${ctx}/js/east/index.js"></script>
		<!-- jQuery 2.2.3 -->
		<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/jQuery/jquery-2.2.3.min.js"></script>
		<!-- Bootstrap 3.3.6 -->
		<script src="${ctx}/static/AdminLTE-With-Iframe/bootstrap/js/bootstrap.min.js"></script>
		<!-- FastClick -->
		<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/fastclick/fastclick.js"></script>
		<!-- AdminLTE App -->
		<script src="${ctx}/static/AdminLTE-With-Iframe/dist/js/app.js"></script>
		<!--tabs-->
		
	</head>

	<body>
		<div class="content_box">
			<div class="el-row">
				<div class="el-col el-col-4">
					<div><a href="javascript:cRCButClick();"><img src="${ctx}/images/east/img/icon3.png" /></a><i>检核结果核对</i></div>
				</div>
				<div class="el-col el-col-4">
					<div><a href="javascript:cRLButClick();"><img src="${ctx}/images/east/img/icon1.png" /></a><i>检核结果查询</i></div>
				</div>
				<div class="el-col el-col-4">
					<div><a href="javascript:checkRuleLButClick();"><img src="${ctx}/images/east/img/icon2.png" /></a><i>检核规则查询</i></div>
				</div>
			</div>
			<div class="el-row">
				<div class="el-col el-col-6">
					<div class="color2">
						<div class="col-box">
							<div class="box-tit">通知公告 <i><a href="javascript:openMoreNotice()">更多</a></i></div>
							<div class="box-content">
								<ul id="noticeUL"></ul>
							</div>
						</div>
					</div>
				</div>
				<div class="el-col el-col-6">
					<div>
						<div class="col-box" style="height: 260px;">
							<div id="noticeTitle" class="box-tit" style="text-align:center;"></div>
							<div id="noticeDetail">

							</div>
						</div>

					</div>
				</div>
			</div>
			<div class="el-row">
				<div class="el-col el-col-6">
					<div class="show-box-tit" style="background: #eff5f9;box-shadow: 0px 0px 0px 0px #eff5f9;">
						<img src="${ctx}/images/east/img/icontitle1.png"/>最新1期校验结果
					</div>
					<div>
						<div class="col-box" style="height: 260px;">
							<div class="box-tit">校验不通过记录数/校验总记录数</div>
							<div class="box-content" id="latelyChackResult"></div>
						</div>
					</div>
				</div>
				<div class="el-col el-col-6">
					<div class="show-box-tit" style="background: #eff5f9;box-shadow: 0px 0px 0px 0px #eff5f9;">
						<img src="${ctx}/images/east/img/icontitle.png"/>最近6期校验结果
					</div>
					<div class="bar-pic" style="height: 260px;">
						<div id="bar-box" style="height: 210px;"></div>
						<div class="bar-detail">
							<span class="bar-item1">校验不通过记录数</span>
							<span class="bar-item2">校验总记录数</span>
						</div>
					</div>
				</div>

			</div>
			<div class="el-row">
				<div class="el-col el-col-6">
					<!--表名排名-->
					<div>
						<div class="col-box">
							<div class="box-tit">数据表排名：校验不通过记录TOP5 <i><a href="javascript:openMoreDataInter()">更多</a></i></div>
							<div class="box-content ranking" id="dataInter"></div>
						</div>
					</div>
				</div>
				<div class="el-col el-col-6">
					<!--机构排名-->
					<div>
						<div class="col-box">
							<div class="box-tit">机构排名：校验不通过记录数TOP5 <i><a href="javascript:openMoreOrg()">更多</a></i></div>
							<div class="box-content ranking" id="org"></div>
						</div>

					</div>
				</div>
			</div>
		</div>
	</body>


</html>