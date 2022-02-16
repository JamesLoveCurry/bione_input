<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/common/meta.jsp"%>
<%@ include file="/common/taglibs.jsp"%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<%@ include file="/common/jquery_load.jsp"%>
<%@ include file="/common/ligerUI_load.jsp"%>
<style>
.tipptitle{
  margin-left:24px;
  text-align:left
}
.tipp{
  margin-left:40px;
  text-align:left
}
</style>
<script type="text/javascript">
</script>
	<title></title>
	</head>
	<body>
		<div id="center" style="width: 100%;">
			<div id="tipAreaDiv" style="height: 100%; padding-top: 20px; padding-bottom: 20px; padding-right: 20px; background-color: #FFFEE6; border: solid 1px #D0D0D0;">
				<div style="padding-left: 50px;">
					<div style="width: 24px; height: 16px; float: left; background-image: url('${ctx}/images/classics/icons/lightbulb.png'); background-attachment: scroll; background-repeat: no-repeat; background-position-x: 0%; background-position-y: 0%; background-size: auto; background-origin: padding-box; background-clip: border-box; background-color: transparent;"></div>
					<p class="tipptitle" style="font-size: 20px; line-height: 36px;">tips  报表填报使用注意说明：</p>
					<p class="tipp" style="font-size: 20px; line-height: 36px;">(1)   蓝色单元格属于excel公式单元格单元格会根据子项单元格改变进行自动改变。</p>
					<p class="tipp" style="font-size: 20px; line-height: 36px;">(2)   灰色单元格属于不可编辑单元格（表间取数），无法通过线上线下方式直接进行修改，需要点击表间取数按钮改变单元格内容。</p>
					<p class="tipp" style="font-size: 20px; line-height: 36px;">(3)   红色单元格属于校验未通过单元格，点击单元格可以查看未通过校验公式。</p>
					<p class="tipp" style="font-size: 20px; line-height: 36px;">(4)   监管要求的校验公式属于强校验公式，必须通过才能进行任务提交，自定义公式添加说明后可以提交。</p>
					<p class="tipp" style="font-size: 20px; line-height: 36px;">(5)   恢复初始值按钮谨慎使用，会导致报表填报数据恢复初始值。</p>
				</div>
			</div>
		</div>
	</body>
	</html>