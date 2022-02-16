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
		<script type="text/javascript">
			var ctx = "${ctx}";
		</script>
	</head>

	<body>
		<div class="content_box">
			<div class="el-row">
				<div class="el-col el-col-3">
					<div><a href="javascript:businessDataQueryButClick();"><img src="${ctx}/images/east/img/icon4.png" /></a><i>全量数据查询</i></div>
				</div>
				<div class="el-col el-col-3">
					<div><a href="javascript:cRCButClick();"><img src="${ctx}/images/east/img/icon3.png" /></a><i>检核结果核对</i></div>
				</div>
				<div class="el-col el-col-3">
					<div><a href="javascript:cRLButClick();"><img src="${ctx}/images/east/img/icon1.png" /></a><i>检核结果查询</i></div>
				</div>
				<div class="el-col el-col-3">
					<div><a href="javascript:checkRuleLButClick();"><img src="${ctx}/images/east/img/icon2.png" /></a><i>检核规则查询</i></div>
				</div>
			</div>
			<div class="el-row">
				<div class="el-col el-col-6">
					<div class="color2">
						<div class="col-box">
							<div class="box-tit">通知公告 <i><a href="javascript:openMoreNotice()">更多</a></i></div>
							<div class="box-content">
								<ul id="noticeUL">
								</ul>
							</div>
						</div>
					</div>
				</div>
				<div class="el-col el-col-6">
					<div>
						<div class="col-box" style="height: 260px;">
							<div id="noticeTitle" style="text-align:center;"></div>
							<div id="noticeDetail">

							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="el-row">
				<div class="show-box-tit">
					<img src="${ctx}/images/east/img/icontitle1.png" alt="" />最新1期校验结果
				</div>
				<div class="el-col el-col-6">
					<div>
						<div class="col-box" style="height: 260px;">
							<div class="box-tit">校验不通过记录数/校验总记录数</div>
							<div class="box-content" id="latelyChackResult">
							</div>
						</div>
					</div>
				</div>
				<div class="el-col el-col-6">
					<div>
						<div class="col-box" style="height: 260px;">
							<div class="box-tit">本期校验通过率</div>
							<div class="box-content" id="failureRate">
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="el-row">
				<div class="el-col el-col-6">
					<!--表名排名-->
					<div>
						<div class="col-box">
							<div class="box-tit">数据表排名：校验不通过记录数TOP5 <i><a href="javascript:openMoreDataInter()">更多</a></i></div>
							<div class="box-content ranking" id="dataInter">
							</div>
						</div>
					</div>
				</div>
				<div class="el-col el-col-6">
					<!--机构排名-->
					<div>
						<div class="col-box">
							<div class="box-tit">机构排名：校验不通过记录数TOP5 <i><a href="javascript:openMoreOrg()">更多</a></i></div>
							<div class="box-content ranking" id="org">
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--<div class="el-row">
				<div class="el-col el-col-6">
					部门排名：校验不通记录数TOP5
					<div>
						<div class="col-box">
							<div class="box-tit">部门排名：校验不通记录数TOP5<i><a href="javascript:openMoreDept()">更多</a></i></div>
							<div class="box-content ranking" id="dept">
								 <ul>
									<li>
										<span>Top1</span>
										<div class="ranking-detail"><span>1,225,885</span><i>XX银行运营管理部</i></div>
										<div class="ranking-num">
											<div class="down">1%</div>
										</div>
									</li>
									<li>
										<span>Top2</span>
										<div class="ranking-detail"><span>1,030,286</span><i>XX银行财务会计部</i></div>
										<div class="ranking-num">
											<div class="up">4%</div>
										</div>
									</li>
									<li>
										<span>Top3</span>
										<div class="ranking-detail"><span>725,976</span><i>XX银行零售业务部</i></div>
										<div class="ranking-num">
											<div class="up">3%</div>
										</div>
									</li>
									<li>
										<span>Top4</span>
										<div class="ranking-detail"><span>579,815</span><i>XX银行公司业务部</i></div>
										<div class="ranking-num">
											<div>-</div>
										</div>
									</li>
									<li>
										<span>Top5</span>
										<div class="ranking-detail"><span>418,653</span><i>XX银行电子银行部</i></div>
										<div class="ranking-num">
											<div>-</div>
										</div>
									</li>
								</ul>
							</div>
						</div>
					</div>
				</div>
				<div class="el-col el-col-6">
					来源系统排名：校验不通记录数TOP5
					<div>
						<div class="col-box">
							<div class="box-tit">来源系统排名：校验不通记录数TOP5<i><a href="javascript:openMoreSrcSys()">更多</a></i></div>
							<div class="box-content ranking" id="srcSys">
								 <ul>
									<li>
										<span>Top1</span>
										<div class="ranking-detail"><span>1,225,885</span><i>核心系统</i></div>
										<div class="ranking-num">
											<div class="down">1%</div>
										</div>
									</li>
									<li>
										<span>Top2</span>
										<div class="ranking-detail"><span>1,030,286</span><i>信用卡系统</i></div>
										<div class="ranking-num">
											<div class="up">4%</div>
										</div>
									</li>
									<li>
										<span>Top3</span>
										<div class="ranking-detail"><span>725,976</span><i>二代支付系统</i></div>
										<div class="ranking-num">
											<div class="up">3%</div>
										</div>
									</li>
									<li>
										<span>Top4</span>
										<div class="ranking-detail"><span>579,815</span><i>个人信贷系统</i></div>
										<div class="ranking-num">
											<div>-</div>
										</div>
									</li>
									<li>
										<span>Top5</span>
										<div class="ranking-detail"><span>418,653</span><i>对公信贷系统</i></div>
										<div class="ranking-num">
											<div>-</div>
										</div>
									</li>
								</ul> 
							</div>
						</div>
					</div>
				</div>
			</div> -->
			<div class="el-row">
				<div class="show-box-tit">
					<img src="${ctx}/images/east/img/icontitle.png" alt="" />最近6期校验结果
				</div>
				<div class="el-col el-col-12">
					<div class="bar-pic">
						<div id="bar-box"></div>
						<div class="bar-detail">
							<span class="bar-item1">校验不通过记录数</span>
							<span class="bar-item2">校验总记录数</span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>