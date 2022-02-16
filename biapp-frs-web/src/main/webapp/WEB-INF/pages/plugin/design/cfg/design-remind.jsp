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
					<p class="tipptitle">tips  报表配置使用注意说明（再次打开请点击红色按钮）：</p>
					<p class="tipp">(1)   不要轻易使用del键，修改单元格取数逻辑可以直接在原单元格上覆盖不要直接删除。</p>
					<p class="tipp">(2)   修改单元格取数逻辑的时候（指标单元格、表间取数单元格、excel单元格互相改变），要注意报表指标编号和人行编码是否有变化。</p>
					<p class="tipp">(3)   修改单元格取数逻辑的时候（指标单元格、表间取数单元格、excel单元格互相改变），以下属性不会改变：单元格名称、人行机构编码、数据精度、数据单位、显示格式、是否可空、备注说明、业务口径、技术口径。</p>
					<p class="tipp">(4)   明细类报表有几个特殊配置属性要注意，在报表信息页签。首先是报表是否定长属性，例如：G13报表都属定长报表，G14-Ⅱ属于不定长报表。</p>
					<p class="tipp">(5)   明细类报表有几个特殊配置属性要注意，在报表信息页签。其次是报表导入配置属性，根据报表是否定长配置不同，不定长报表要根据最后有数据的一行进行倒推，倒推到明细字段那一行，共有几行就填几。配置为数字，例如:2。定长报表配置为：模型ID:长度 例如:MD_G15_1:10 多模型使用英文逗号分隔。</p>
					<p class="tipp">(6)   明细类报表有几个特殊配置属性要注意，在报表信息页签。第三是合计排序SQL属性，有些明细报表需要用合计项做为排序字段，并且合计项在数据库中不存在，就需要配置该字段，配置规则：模型ID:排序字段公式，例如：{"MD_G1402":"(OT_RISK_EXP_AMT+PT_RISK_EXP_AMT+SP_RISK_EXP_AMT+NM_RISK_EXP_AMT+CC_RISK_EXP_AMT+TB_RISK_EXP_AMT)"}。</p>
					<p class="tipp">(7)   明细类报表有几个特殊配置属性要注意，在报表信息页签。最后是明细模型的配置，目前不管是定长报表还是不定长报表，明细模型都只能有且只有一个主键，否则会影响填报数据入库。</p>
				</div>
			</div>
		</div>
	</body>
	</html>