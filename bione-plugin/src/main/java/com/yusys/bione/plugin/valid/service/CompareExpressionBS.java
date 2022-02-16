package com.yusys.bione.plugin.valid.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yusys.bione.comp.service.BaseBS;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.XlsExcelExporter;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;
import com.yusys.bione.plugin.valid.repository.ValidLogicMybatisDao;
import com.yusys.bione.plugin.valid.web.vo.BankExcelOuterVO;
import com.yusys.bione.plugin.valid.web.vo.ExcelOuterVO;
import com.yusys.bione.plugin.valid.web.vo.LogicAndTemplateVO;
import com.yusys.bione.plugin.valid.web.vo.NameAndIdMap;

@Service
@Transactional(readOnly = true)
public class CompareExpressionBS extends BaseBS<RptValidCfgextLogic>{
	
	@Autowired
	private ValidLogicMybatisDao logicDao;
	
	/**
	 * 校验1104
	 * @param vos
	 */
	public void compare(List<ExcelOuterVO> vos) {
		
//		String test = "[C15]-5<=[G0100_1610_C129]<=[C15]+5";
//		test = test.replaceAll("_\\d{4}_", "_");
		//准备数据 - 校验公式
		String jql = "select logic, rel.id.rptTemplateId,report.rptNum "
				+ "from RptValidCfgextLogic logic, RptValidLogicRptRel rel, RptMgrReportInfo report, RptMgrFrsExt ext  "
				+ "where logic.checkId = rel.id.checkId and report.cfgId = rel.id.rptTemplateId and report.rptId = ext.rptId and ext.busiType = '02' order by rel.id.rptTemplateId";
		List<Object[]> tmpList = this.baseDAO.findWithIndexParam(jql);
		
		List<LogicAndTemplateVO> list = new ArrayList<LogicAndTemplateVO>();

		for(Object[] tmp : tmpList){
			LogicAndTemplateVO vo = new LogicAndTemplateVO((RptValidCfgextLogic)tmp[0], (String)tmp[1], (String)tmp[2]);
			list.add(vo);
		}

		tmpList = null;//释放内存
		
		//准备数据 - 指标
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> templateTypeList = new ArrayList<String>();
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_CELL);
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_COM);
		map.put("templateTypeList", templateTypeList);
		map.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_FRS);
		map.put("rptType", GlobalConstants4plugin.RPT_TYPE_DESIGN);
		map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		
		List<NameAndIdMap> mappingList = this.logicDao.selectCellNoMapping(map);
		
		Map<String, String> replaceMap = new HashMap<String, String>();
		for(NameAndIdMap tmp : mappingList){
			replaceMap.put("I('" + tmp.getIndexNo() + "')", "[" + tmp.getRptNm() + "_" + tmp.getIndexNm() + "]");
		}
		
		mappingList = null;//释放内存
		map = null;
		
		//准备数据 - 指标 end
		
		//解析指标完成后存放的结果，key为templateId，value 为该模板下的所有公式
		Map<String, List<LogicAndTemplateVO>> resultMap = new HashMap<String, List<LogicAndTemplateVO>>();
		
		//解析公式
		Pattern pattern = Pattern.compile("I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[-])+'\\)");
		
		for(LogicAndTemplateVO logic:list){
			
			String expression = logic.getLeftExpression();
			StringBuffer sbL = new StringBuffer();
			
			//转换左表达式
			Matcher matcher = pattern.matcher(expression);
			while (matcher.find()) {
				if (replaceMap.get(matcher.group(0)) != null) {
						matcher.appendReplacement(sbL,
								replaceMap.get(matcher.group(0)));
				}
			}
			matcher.appendTail(sbL);
			String tmp = sbL.toString();
			
			tmp = StringUtils.remove(tmp, logic.getRptNum() + "_");
			tmp = StringUtils.replace(tmp, "==", "=");//将所有的“==”替换为=
			logic.setChangedLE(tmp);
			//转换左表达式 end
			
			
			//转换右表达式
			StringBuffer sbR = new StringBuffer();
			expression = logic.getRightExpression();
			
			matcher = pattern.matcher(expression);
			while (matcher.find()) {
				if (replaceMap.get(matcher.group(0)) != null) {
						matcher.appendReplacement(sbR,
								replaceMap.get(matcher.group(0)));
			}
			
			}
			matcher.appendTail(sbR);
			
			tmp = sbR.toString();
			tmp = StringUtils.remove(tmp, logic.getRptNum() + "_");
			tmp = StringUtils.replace(tmp, "==", "=");//将所有的“==”替换为=
			
			logic.setChangedRE(tmp);
			//转换右表达式 end
			
			java.text.DecimalFormat myformat=new java.text.DecimalFormat("0");
			Double diff = logic.getFloatVal().doubleValue()/10000;
			String str = "";
			if(diff >= 1){
				str = myformat.format(diff);
			}else{
				str = diff.toString();
			}
			if(logic.getFloatVal().doubleValue() != 0){//有容差值
				logic.setChangedExpreesion(logic.getChangedLE() + "+" + str + ">=" +  logic.getChangedRE() + ">=" + logic.getChangedLE() + "-" + str);
				logic.setAnotherFormExpression(logic.getChangedLE() + "-" + str + "<=" + logic.getChangedRE() + "<=" + logic.getChangedLE() + "+" + str );
				
				logic.setExpressonRL(logic.getChangedRE() + "+" + str + ">=" + logic.getChangedLE() + ">=" + logic.getChangedRE() + "-" + str);
				logic.setAnotherExpressionRL(logic.getChangedRE() + "-" + str + "<=" + logic.getChangedLE() + "<=" + logic.getChangedRE() + "+" + str);
			}else{
				logic.setChangedExpreesion(logic.getChangedLE() + logic.getLogicOperType() + logic.getChangedRE());
				
				if(logic.getLogicOperType().equals(">") || logic.getLogicOperType().equals(">=")){
					if(logic.getLogicOperType().equals(">")){
						logic.setExpressonRL(logic.getChangedRE() + "<" + logic.getChangedLE());
					}else{
						logic.setExpressonRL(logic.getChangedRE() + "<=" + logic.getChangedLE());
					}
				}else if(logic.getLogicOperType().equals("<") || logic.getLogicOperType().equals("<=")){
					if(logic.getLogicOperType().equals("<")){
						logic.setExpressonRL(logic.getChangedRE() + ">" + logic.getChangedLE());
					}else{
						logic.setExpressonRL(logic.getChangedRE() + ">=" + logic.getChangedLE());
					}
				}else{
					logic.setExpressonRL(logic.getChangedRE() + logic.getLogicOperType() + logic.getChangedLE());
				}
			}
			
			if(logic.getChangedLE().contains("I('") || logic.getChangedRE().contains("I('")){
				logic.setReason("指标未替换成功");
			}
			
			if(resultMap.get(logic.getRptNum()) == null){
				List<LogicAndTemplateVO> expressionList = new ArrayList<LogicAndTemplateVO>();
				expressionList.add(logic);
				resultMap.put(logic.getRptNum(), expressionList);
			}else{
				List<LogicAndTemplateVO> localList = resultMap.get(logic.getRptNum());
				int i=0;
				for(i=0;i<localList.size();i++){//排序
					LogicAndTemplateVO vo = localList.get(i);
					if(logic.compareTo(vo) < 0){
						break;
					}
				}
				localList.add(i, logic);
			}
//			System.out.println(logic.getChangedExpreesion());
		}
//		Map<String, Integer> rptNmAndCount = new HashMap<String, Integer>(); 
//		Map<String, Integer> orginMap = new HashMap<String, Integer>();
//		Set<String> rptNmNoFound = new HashSet<String>();
		
		Set<String> kerongRpt = new HashSet<String>();
		int count = 0;
		for(ExcelOuterVO vo: vos){
//			int count = 0;
//			if(orginMap.get(vo.getRptNm()) != null){
//				count = orginMap.get(vo.getRptNm());
//			}
//			count ++;
//			orginMap.put(vo.getRptNm(), count);
			
			kerongRpt.add(vo.getRptNm());
			
			//去掉科荣中的四位整数的版本号
			vo.setExpression(vo.getExpression().replaceAll("_\\d{4}_", "_"));
			
			if(resultMap.get(vo.getRptNm()) != null){
				List<LogicAndTemplateVO> eList = resultMap.get(vo.getRptNm());
				for(LogicAndTemplateVO tmp: eList){
					if(tmp.getChangedExpreesion().trim().toLowerCase().equals(vo.getExpression().trim().toLowerCase())){
						tmp.setIsCheck("Y");
						vo.setIsCheck("Y");
						count++;
//						int count2 = 0;
//						if(rptNmAndCount.get(tmp.getRptNum()) != null){
//							count2 = rptNmAndCount.get(tmp.getRptNum());
//						}
//						count2++;
//						rptNmAndCount.put(tmp.getRptNum(), count2);
						
						break;
					}else if(tmp.getAnotherFormExpression() != null && 
							tmp.getAnotherFormExpression().trim().toLowerCase().equals(vo.getExpression().trim().toLowerCase())){
						tmp.setIsCheck("Y");
						vo.setIsCheck("Y");
						count++;
//						int count2 = 0;
//						if(rptNmAndCount.get(tmp.getRptNum()) != null){
//							count2 = rptNmAndCount.get(tmp.getRptNum());
//						}
//						count2++;
//						rptNmAndCount.put(tmp.getRptNum(), count2);
						
						break;
					}else if(tmp.getExpressonRL() != null && tmp.getExpressonRL().trim().toLowerCase().equals(vo.getExpression().trim().toLowerCase())){
						tmp.setIsCheck("Y");
						vo.setIsCheck("Y");
						count ++;
						break;
					}else if(tmp.getAnotherExpressionRL() != null && tmp.getAnotherExpressionRL().trim().toLowerCase().equals(vo.getExpression().trim().toLowerCase())){
						tmp.setIsCheck("Y");
						vo.setIsCheck("Y");
						count++;
						break;
					}
				}
			}else{
				vo.setReason("该报表在宇信数据没有找到");
			}
		}
		System.out.println(count);
//		System.out.println("**********************************");
//		System.out.println("有 " + rptNmNoFound.size() + " 张报表无法在系统中找到对应的报表，报表名称如下：");
//		
//		for(String tmp: rptNmNoFound){
//			System.out.println(tmp);
//		}
//		System.out.println("**********************************");
//		
//		System.out.println("能匹配的公式的报表名称，报表的公式数量，相匹配的公式数量如下");
//		for(String tmp: rptNmAndCount.keySet()){
//			System.out.println(tmp + "\t" + orginMap.get(tmp) + "\t" + rptNmAndCount.get(tmp));
//		}
//		
//		System.out.println("**********************************");
		
		
//		System.gc();
		
		List<LogicAndTemplateVO> newList = new ArrayList<LogicAndTemplateVO>();
		for(String tmp:resultMap.keySet()){
			System.out.println(tmp);
			List<LogicAndTemplateVO> v = resultMap.get(tmp);
			if(!kerongRpt.contains(tmp)){
				for(LogicAndTemplateVO a: v){
					a.setReason("在科荣中未找到该报表");
				}
			}
			newList.addAll(v);
			
		}
		List<List<?>> sysIn = new ArrayList<List<?>>();
		sysIn.add(newList);
		
		resultMap = null;
		
		XlsExcelExporter xfe = null;
		try {
			xfe = new XlsExcelExporter("//Users//fangjuan//Documents//宇信监管平台导出数据-银监会.xls", sysIn);
			xfe.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (xfe != null) {
					xfe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		
		
		List<List<?>> outputList = new ArrayList<List<?>>();
		outputList.add(vos);
		XlsExcelExporter fe = null;
		try {
			fe = new XlsExcelExporter("//Users//fangjuan//Documents//科荣导出数据-银监会.xls", outputList);
			fe.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fe != null) {
					fe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		System.out.println("分析结束");
		outputList = null;
		vos = null;
		System.gc();
		 
	}


	/**
	 * 校验人行
	 * @param vos
	 */
	public void compareBank(List<BankExcelOuterVO> vos) {
		//准备数据 - 校验公式
		String jql = "select logic, rel.id.rptTemplateId,report.rptNum "
				+ "from RptValidCfgextLogic logic, RptValidLogicRptRel rel, RptMgrReportInfo report, RptMgrFrsExt ext "
				+ "where logic.checkId = rel.id.checkId and report.cfgId = rel.id.rptTemplateId and report.rptId = ext.rptId and ext.busiType = '03' order by rel.id.rptTemplateId";
		List<Object[]> tmpList = this.baseDAO.findWithIndexParam(jql);
		
		List<LogicAndTemplateVO> list = new ArrayList<LogicAndTemplateVO>();

		for(Object[] tmp : tmpList){
			LogicAndTemplateVO vo = new LogicAndTemplateVO((RptValidCfgextLogic)tmp[0], (String)tmp[1], (String)tmp[2]);
			list.add(vo);
		}

		tmpList = null;//释放内存
		
		//准备数据 - 指标
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> templateTypeList = new ArrayList<String>();
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_CELL);
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_COM);
		map.put("templateTypeList", templateTypeList);
		map.put("extType", GlobalConstants4plugin.RPT_EXT_TYPE_FRS);
		map.put("rptType", GlobalConstants4plugin.RPT_TYPE_DESIGN);
		map.put("rptSts", GlobalConstants4plugin.COMMON_BOOLEAN_YES);
		
		List<NameAndIdMap> mappingList = this.logicDao.selectBusiNoMapping(map);
		
		Map<String, String> replaceMap = new HashMap<String, String>();
		for(NameAndIdMap tmp : mappingList){
			if(!StringUtils.isEmpty(tmp.getIndexNm())){
				replaceMap.put("I('" + tmp.getIndexNo() + "')", tmp.getRptNm() + ".CNY0001.A" + tmp.getRptNm() + tmp.getIndexNm());
			}
			
		}
		
		mappingList = null;//释放内存
		map = null;
		
		//准备数据 - 指标 end
		
		Map<String,List<LogicAndTemplateVO>> resultMap = new HashMap<String, List<LogicAndTemplateVO>>();
		
		Pattern pattern = Pattern.compile("I\\('([A-Z]|[a-z]|[0-9]|[^\\x00-\\xff]|[_]|[ ]|\\.|[(]|[)]|[-])+'\\)");
		for(LogicAndTemplateVO logic:list){
			String leftExpression = logic.getLeftExpression();
			StringBuffer sbL = new StringBuffer();
			
			//转换左表达式
			Matcher matcher = pattern.matcher(leftExpression);
			while (matcher.find()) {
				if (replaceMap.get(matcher.group(0)) != null) {
						matcher.appendReplacement(sbL,
								replaceMap.get(matcher.group(0)));
				}
			}
			matcher.appendTail(sbL);
			logic.setChangedLE(sbL.toString());
//			System.out.print(sbL + "\t");
			//转换左表达式 end
			
			//转换右表达式
			StringBuffer sbR = new StringBuffer();
			String rightExpression = logic.getRightExpression();
			
			matcher = pattern.matcher(rightExpression);
			while (matcher.find()) {
				if (replaceMap.get(matcher.group(0)) != null) {
						matcher.appendReplacement(sbR,
								replaceMap.get(matcher.group(0)));
			}
			
			}
			matcher.appendTail(sbR);
			logic.setChangedRE(sbR.toString());
			//转换右表达式 end
//			System.out.println(sbR + "\t");
			
			if(resultMap.get(logic.getRptNum()) != null){
				List<LogicAndTemplateVO> tmp = resultMap.get(logic.getRptNum());
				int i=0;
				for(i=0;i<tmp.size();i++){//排序
					LogicAndTemplateVO vo = tmp.get(i);
					if(logic.compareTo(vo) < 0){
						break;
					}
				}
				tmp.add(i, logic);
			}else{
				List<LogicAndTemplateVO> tmp = new ArrayList<LogicAndTemplateVO>();
				tmp.add(logic);
				resultMap.put(logic.getRptNum(), tmp);
			}
		}
//		Map<String, Integer> rptNmAndCount = new HashMap<String, Integer>(); 
//		Map<String, Integer> orginMap = new HashMap<String, Integer>();
		int count = 0;
		Set<String> kerongRptNm = new HashSet<String>();
		for(BankExcelOuterVO vo: vos){
//			int count = 0;
//			if(orginMap.get(vo.getRptNm()) != null){
//				count = orginMap.get(vo.getRptNm());
//			}
//			count ++;
//			orginMap.put(vo.getRptNm(), count);
			
			kerongRptNm.add(vo.getRptNm());
			vo.setLeftExpression(vo.getLeftExpression().replaceAll("\\.\\[([A-Z])+\\]", ""));
			vo.setRightExpression(vo.getRightExpression().replaceAll("\\.\\[([A-Z])+\\]", ""));
			
			//删除掉所有常数
			vo.setLeftExpression(vo.getLeftExpression().replaceAll("\\[[C]\\]", ""));
			vo.setRightExpression(vo.getRightExpression().replaceAll("\\[[C]\\]", ""));
			
			
			if(resultMap.get(vo.getRptNm()) != null){
				
				List<LogicAndTemplateVO> eList = resultMap.get(vo.getRptNm());
				for(LogicAndTemplateVO tmp: eList){
//					if(!StringUtils.isEmpty(tmp.getIsCheck()) && tmp.getIsCheck().equals("Y"))
//						continue;
					
					if(tmp.getChangedLE().contains("I('") || tmp.getChangedRE().contains("I('")){
						tmp.setReason("指标未替换成功");
						continue;
					}
					if(tmp.getChangedLE().trim().equals(vo.getLeftExpression().trim())){
						if(tmp.getChangedRE().trim().equals(vo.getRightExpression().trim())){
							if(tmp.getLogicOperType().trim().equals(tmp.getLogicOperType().trim())){
								tmp.setIsCheck("Y");
								tmp.setReason(null);
								vo.setReason(null);
								vo.setIsCheck("Y");
								count++;
//								break;
							}else{
								if(tmp.getIsCheck() == null || !tmp.getIsCheck().equals("Y")){
									tmp.setReason("左右公式全部匹配，但是操作符不匹配");
								}
								vo.setReason("左右公式全部匹配，但是操作符不匹配");
								
							}
						}else{
							if(tmp.getIsCheck() == null || !tmp.getIsCheck().equals("Y")){
								tmp.setReason("左公式已匹配，但右公式不匹配");
							}
							vo.setReason("左公式已匹配，但右公式不匹配");
							
						}
					}else{
						vo.setReason("宇信左公式没配或者配置错误");
					}
//					if(tmp.getLogicOperType().equals(vo.getOper()) && tmp.getChangedLE().equals(vo.getLeftExpression()) && tmp.getChangedRE().equals(vo.getRightExpression())){
//						tmp.setIsCheck("Y");
//						vo.setIsCheck("Y");
//						
////						int count2 = 0;
////						if(rptNmAndCount.get(tmp.getRptNum()) != null){
////							count2 = rptNmAndCount.get(tmp.getRptNum());
////						}
////						count2++;
////						rptNmAndCount.put(tmp.getRptNum(), count2);
//						
//						break;
//					}
				}
			}else{
//				rptNmNoFound.add(vo.getRptNm());
				vo.setReason("在宇信数据中未找到该报表");
			}
		}
//		System.out.println("**********************************");
//		System.out.println("有 " + rptNmNoFound.size() + " 张报表无法在系统中找到对应的报表，报表名称如下：");
//		
//		for(String tmp: rptNmNoFound){
//			System.out.println(tmp);
//		}
//		System.out.println("**********************************");
//		
//		System.out.println("能匹配的公式的报表名称，报表的公式数量，相匹配的公式数量如下");
//		for(String tmp: rptNmAndCount.keySet()){
//			System.out.println(tmp + "\t" + orginMap.get(tmp) + "\t" + rptNmAndCount.get(tmp));
//		}
//		
//		System.out.println("**********************************");
		
		System.out.println(count);
		//导出execel
		
		List<LogicAndTemplateVO> newList = new ArrayList<LogicAndTemplateVO>();
		for(String tmp:resultMap.keySet()){
			List<LogicAndTemplateVO> v = resultMap.get(tmp);
			if(!kerongRptNm.contains(tmp)){
				for(LogicAndTemplateVO a: v){
					a.setReason("在科荣中未找到该报表");
				}
			}else{
				for(LogicAndTemplateVO a: v){
					if(StringUtils.isEmpty(a.getIsCheck()) && StringUtils.isEmpty(a.getReason())){
						a.setReason("公式在科荣中未找到，或者该公式配置错误");
					}
				}
			}
			newList.addAll(v);
			
		}
		List<List<?>> sysIn = new ArrayList<List<?>>();
		sysIn.add(newList);
		XlsExcelExporter xfe = null;
		try {
			xfe = new XlsExcelExporter("//Users//fangjuan//Documents//宇信监管平台导出数据-人行.xls", sysIn);
			xfe.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (xfe != null) {
					xfe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		//导出execel
		
		
		List<List<?>> outputList = new ArrayList<List<?>>();
		outputList.add(vos);
		XlsExcelExporter fe = null;
		try {
			fe = new XlsExcelExporter("//Users//fangjuan//Documents//科荣导出数据-人行.xls", outputList);
			fe.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fe != null) {
					fe.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		System.out.println("分析结束");
	}
}
