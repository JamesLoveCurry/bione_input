<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8" />
		<title></title>
		<link rel="stylesheet" type="text/css" href="${ctx}/css/imas/index.css" />
		<script type="text/javascript" src="${ctx}/js/echarts4/js/echarts.js"></script>
		<script type="text/javascript" src="${ctx}/js/imas/index.js"></script>

		<!-- jQuery 2.2.3 -->
		<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/jQuery/jquery-2.2.3.min.js"></script>
		<%--<!-- Bootstrap 3.3.6 -->--%>
		<script src="${ctx}/static/AdminLTE-With-Iframe/bootstrap/js/bootstrap.min.js"></script>
		<!-- FastClick -->
		<script src="${ctx}/static/AdminLTE-With-Iframe/plugins/fastclick/fastclick.js"></script>
		<!-- AdminLTE App -->
		<script src="${ctx}/static/AdminLTE-With-Iframe/dist/js/app.js"></script>
		<%--<!--tabs-->--%>

		<%--&lt;%&ndash;进度条&ndash;%&gt;--%>
		<script type="text/javascript" src="${ctx}/js/setStep/js/setStep.js"></script>
		<link rel="stylesheet" type="text/css" href="${ctx}/js/setStep/css/ystep.css" />

		<%--下拉插架，实现多选--%>
		<script type="text/javascript" src="${ctx}/js/select2/js/select2.min.js"></script>
		<link rel="stylesheet" type="text/css" href="${ctx}/js/select2/css/select2.min.css" />
		<script type="text/javascript">
			var ctx = "${ctx}";
		</script>

	</head>

	<body>
		<div class="content_box">
			<div class="el-row">
				<div class="el-col el-col-3">
					<div><a href="javascript:businessDataQueryButClick();"><img src="${ctx}/images/imas/img/icon4.png" /></a><i>报表数据维护</i></div>
				</div>
				<div class="el-col el-col-3">
					<div><a href="javascript:cRLButClick();"><img src="${ctx}/images/imas/img/icon1.png" /></a><i>检核结果查询</i></div>
				</div>
				<div class="el-col el-col-3">
					<div><a href="javascript:rptInput();"><img src="${ctx}/images/imas/img/icon3.png" /></a><i>报表填报</i></div>
				</div>
				<div class="el-col el-col-3">
					<div><a href="javascript:checkRuleLButClick();"><img src="${ctx}/images/imas/img/icon2.png" /></a><i>检核规则查询</i></div>
				</div>
			</div>

            <div class="el-row">
                <div class="show-box-tit">
                    <img src="${ctx}/images/imas/img/icontitle.png" alt="" />报送进度
                </div>
                <div class="el-col el-col-12">
                    <div class="">
                        <div style="text-align: left">
                            <span>日期选择：<input type="date" id="dtDate" onchange="showInfoByData()"/></span>

							<span>报送机构：
								<select id = "reportOrg" onchange="showInfoByData()" class="js-example-basic-multiple" name="states[]" multiple="multiple"  style = "width:600px;">


								</select>
							</span>
							<br>
							<span>最迟上报时间：<span id="lastReportTime"></span></span>
							<span>剩余上报时间：<span id="remainReportTime"></span></span>
                        </div>
                    </div>
                    <div class="bar-pic">
                        <div id="report-pro">
							<div id = "chart1">

							</div>

							<div id = "chart2">

							</div>

                            <!-- 展示进度条-->
                            <div id="setDiv">
                            
                            </div>
                        </div>
                    </div>
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

			<%--<div class="el-row">
				<div class="show-box-tit">
					<img src="${ctx}/images/imas/img/icontitle1.png" alt="" />最新1期校验结果
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

			<div class="el-row">
				<div class="show-box-tit">
					<img src="${ctx}/images/imas/img/icontitle.png" alt="" />最近6期校验结果
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
			</div>--%>
		</div>
	</body>
</html>