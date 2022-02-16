package com.yusys.bione.plugin.wizard.service;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.ReflectionUtils;
import com.yusys.bione.comp.utils.SpringContextHolder;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.AbstractExcelImport;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.excel.ExcelImporter;
import com.yusys.bione.frame.excel.XlsExcelTemplateExporter;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResult;
import com.yusys.bione.plugin.idxplan.entity.RptIdxPlanvalResultPK;
import com.yusys.bione.plugin.idxplan.service.IdxPlanvalInfoBS;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
import com.yusys.bione.plugin.rptvalid.service.RptValidLogicBS;
import com.yusys.bione.plugin.rptvalid.service.RptValidWarnBS;
import com.yusys.bione.plugin.rptvalid.web.vo.IdxValidLogicVO;
import com.yusys.bione.plugin.rptvalid.web.vo.IdxValidPlanvalVO;
import com.yusys.bione.plugin.rptvalid.web.vo.IdxValidWarnVO;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextWarn;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicIdxRel;
import com.yusys.bione.plugin.valid.entitiy.RptValidLogicIdxRelPK;
import com.yusys.bione.plugin.valid.entitiy.RptValidWarnLevel;
import com.yusys.bione.plugin.valid.entitiy.RptValidWarnLevelPK;
import com.yusys.bione.plugin.wizard.web.vo.IdxLogicImportVO;
import com.yusys.bione.plugin.wizard.web.vo.IdxPlanvalImportVO;
import com.yusys.bione.plugin.wizard.web.vo.IdxWarnImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;

@Service
public class IdxcheckWizardRequire extends BaseController implements
		IWizardRequire {
	
	private RptValidLogicBS logicBs = SpringContextHolder.getBean("rptValidLogicBS");
	private RptValidWarnBS warnBs = SpringContextHolder.getBean("rptValidWarnBS");
	private IdxPlanvalInfoBS idxPlanvalInfoBS = SpringContextHolder.getBean("idxPlanvalInfoBS");
	@Autowired
	private ExcelBS excelBS;
	
	@Autowired
	private IdxInfoBS idxInfoBS;
	
	/**
	 * 指标校验信息上传
	 * @param file 文件对象
	 * @return 上传文件结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport1 = new ExcelImporter(IdxLogicImportVO.class, file);
		AbstractExcelImport xlsImport2 = new ExcelImporter(IdxWarnImportVO.class, file);
		AbstractExcelImport xlsImport3 = new ExcelImporter(IdxPlanvalImportVO.class, file);
		UploadResult result = new UploadResult();
		try {
			List<IdxLogicImportVO> vos1 = (List<IdxLogicImportVO>) xlsImport1.ReadExcel();
			List<IdxWarnImportVO> vos2 = (List<IdxWarnImportVO>) xlsImport2.ReadExcel();
			List<IdxPlanvalImportVO> vos3 = (List<IdxPlanvalImportVO>) xlsImport3.ReadExcel();
			String ehcacheId1 = RandomUtils.uuid2();
			String ehcacheId2 = RandomUtils.uuid2();
			String ehcacheId3 = RandomUtils.uuid2();
			String ehcacheId = ehcacheId1+","+ehcacheId2+","+ehcacheId3;
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId1,
					vos1);
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId2,
					vos2);
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId3,
					vos3);
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos1 != null && vos1.size() > 0) {
				for (IdxLogicImportVO vo : vos1) {
					String checkNm = vo.getCheckNm();//获取导入的校验公式名称
					String linkNo=checkNm;
					String linkNm="指标逻辑校验："+checkNm;
					result.setInfo(linkNo, linkNm);
				}
			}

			if (vos2 != null && vos2.size() > 0) {
				for (IdxWarnImportVO vo : vos2) {
					String checkNm = vo.getCheckNm();//获取导入的校验名称
					String linkNo=checkNm;
					String linkNm="指标警示校验："+checkNm;
					if(vo.getCheckNm()!=null){
						result.setInfo(linkNo, linkNm);
					}
				}
			}
			
			if (vos3 != null && vos3.size() > 0) {
				Map<String,String> orgNmMap = new HashMap<String, String>();//初始化一个用于转换机构名称的map变量
				Map<String,String> dimNmMap = new HashMap<String, String>();//初始化一个用于转换币种名称的map变量
				List<Object[]> orgs = idxPlanvalInfoBS.getOrgNoList();//查询所有机构，用于机构编号转机构名称，返回到页面
				List<Object[]> dims = idxPlanvalInfoBS.getDimItemNoList();//查询所有币种，用于币种编号转币种名称，返回到页面
				
				if(null != orgs && orgs.size() > 0){//把机构放入到map里，便于判断
					for(Object[] org : orgs){
						orgNmMap.put(org[0].toString(), org[1].toString());
					}
				}
				if(null != dims && dims.size() > 0){//把币种放入到map里，便于判断
					for(Object[] dim : dims){
						dimNmMap.put(dim[0].toString(), dim[1].toString());
					}
				}
				for (IdxPlanvalImportVO vo : vos3) {
					String indexNo = vo.getIndexNo();//获取导入的指标编号
					String orgNo = vo.getOrgNo();//获取导入的机构编号
					String orgNm = orgNmMap.get(orgNo);//获取该机构编号对应的机构名称
					String dataDate = vo.getDataDate();
					String currencyId = vo.getCurrency();
					String currency = dimNmMap.get(currencyId);
					//上传文件后，名称处显示处理
					if(StringUtils.isNotBlank(indexNo)){
						
						if(orgNm==null){
							orgNm="未知机构";
						}
						if(currency==null){
							currency="未知币种";
						}
						String linkNo=indexNo+orgNo+dataDate+currencyId;
						String linkNm="指标计划值校验："+indexNo+"-"+orgNm+"-"+dataDate+"-"+currency;
						result.setInfo(linkNo, linkNm);
					}
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 指标校验数据的导出
	 * @param idxs 选中的需要导出的指标编号
	 * @return 文件名称
	 */
	@Override
	public String export(String idxs) {
		String fileName = "";
		List<IdxLogicImportVO> vos1 = new ArrayList<IdxLogicImportVO>();//初始化一个list，用于存放导出工作簿中第一个sheet页的内容
		List<IdxWarnImportVO> vos2 = new ArrayList<IdxWarnImportVO>();//初始化一个list，用于存放导出工作簿中第二个sheet页的内容
		List<IdxPlanvalImportVO> vos3 = new ArrayList<IdxPlanvalImportVO>();//初始化一个list，用于存放导出工作簿中第三个sheet页的内容
		Map<String,String> checkWarnExists = new HashMap<String, String>();
		Map<String,String> checkOrgExists = new HashMap<String, String>();
		Map<String,String> checkDimExists = new HashMap<String, String>();
		//先去查询数据库里存在的机构编号
		List<Object[]> orgs = idxPlanvalInfoBS.getOrgNoList();
        List<Object[]> idxDims = logicBs.getIndexAndMeasureList();
				
		if(null != orgs && orgs.size() > 0){//将查询到的机构编号放入到map变量中
			for(Object[] org : orgs){
				checkOrgExists.put(org[0].toString(), org[1].toString());//put(key,value)
			}
		}
		
		if(null != idxDims && idxDims.size() > 0){//将查询到的主指标编号放入到map变量中
			for(Object[] idx : idxDims){
				checkDimExists.put(idx[0].toString(),idx[1].toString());
			}
		}
		
		String[] idx = StringUtils.split(idxs.toString(), ',');
		for(int j=0;j<idx.length;j++){
			//查询数据库中要导出数据的全部信息
			List<IdxLogicImportVO> idxLogics = this.logicBs.getAllLogicList(idx[j]);
			List<IdxWarnImportVO> idxWarns = this.warnBs.getAllWarnList(idx[j]);
			List<IdxPlanvalImportVO> idxPlanvals = this.idxPlanvalInfoBS.getAllPlanvalList(idx[j]);
			
            Object[] logicIdxs=new Object[8];//初始化数组，存放需要导出的行数据     
            
	     	for(int a=0;a<idxLogics.size();a++){
	     		IdxLogicImportVO vo = new IdxLogicImportVO();
	     		logicIdxs[0]=idxLogics.get(a).getCheckNm();//存放查询出的校验公式名称
	     		logicIdxs[1]=idxLogics.get(a).getLeftExpression();//存放查询出的左表达式
	     		logicIdxs[2]=idxLogics.get(a).getLogicOperType();//存放查询出的逻辑类型
	     		logicIdxs[3]=idxLogics.get(a).getRightExpression();//存放查询出的右表达式
	     		logicIdxs[4]=idxLogics.get(a).getFloatVal();//存放查询出的容差值
	     		logicIdxs[5]=idxLogics.get(a).getStartDate();//存放查询出的开始日期
	     		logicIdxs[6]=idxLogics.get(a).getBusiExplain();//存放查询出的业务说明
	     		logicIdxs[7]=idxLogics.get(a).getRelateDim();//存放查询出的关联维度项
	     		
	     		Field[] fe = vo.getClass().getDeclaredFields();//获取vo实体的私有方法
  		       if(logicIdxs.length <= fe.length){//一行指标个数不能大于导出模板的一行单元格数
  		       	for(int i =0 ;i < logicIdxs.length; i++){//遍历获取的私有方法，逐个执行
  		       		if(!fe[i].getName().equals("excelRowNo")&&!fe[i].getName().equals("sheetName")){
  		       			ReflectionUtils.invokeSetter(vo, fe[i].getName(), logicIdxs[i]);//将logicIdxs数组中的行数据存放至vo实体中
  		       			}
  		       		}
  		       	vos1.add(vo);//将所有行数据放入vos中
  		       	}
	     		}
	     	
            Object[] warnIdxs=new Object[13];//初始化数组，存放需要导出的行数据
			
	     	for(int a=0;a<idxWarns.size();a++){
	     		IdxWarnImportVO vo = new IdxWarnImportVO();
	     		warnIdxs[0]=idxWarns.get(a).getCheckNm();//存放查询出的校验公式名称
	     		if(checkDimExists.get(idxWarns.get(a).getIndexNo())!=null){
		     		warnIdxs[1]=checkDimExists.get(idxWarns.get(a).getIndexNo());//存放查询出的指标名称
	     		}else{
	     			warnIdxs[1]=idxWarns.get(a).getIndexNo();
	     		}
	     		warnIdxs[2]=idxWarns.get(a).getCompareValType();//存放查询出的比较值类型
	     		warnIdxs[3]=idxWarns.get(a).getRangeType();//存放查询出的幅度类型
	     		warnIdxs[4]=idxWarns.get(a).getStartDate();//存放查询出的开始日期
	     		warnIdxs[5]=idxWarns.get(a).getRelateDim();//存放查询出的关联维度项
	     		warnIdxs[6]=idxWarns.get(a).getRemark();//存放查询出的备注
	     		warnIdxs[7]=idxWarns.get(a).getLevelNm();//存放查询出的级别名称
	     		warnIdxs[8]=idxWarns.get(a).getLevelType();//存放查询出的警戒类型
	     		warnIdxs[9]=idxWarns.get(a).getRemindColor();//存放查询出的提醒颜色
	     		warnIdxs[10]=idxWarns.get(a).getPostiveRangeVal();//存放查询出的正向幅度值
	     		warnIdxs[11]=idxWarns.get(a).getMinusRangeVal();//存放查询出的负向幅度值
	     		warnIdxs[12]=idxWarns.get(a).getIsPassCond();//存放查询出的通过条件
				
	     		String warnJudge = warnIdxs[0].toString()+warnIdxs[1].toString()+warnIdxs[2].toString()
	     				+warnIdxs[3].toString()+warnIdxs[4].toString();
	     		if(checkWarnExists.get(warnJudge)==null||checkWarnExists.get(warnJudge).equals("")){
	     			checkWarnExists.put(warnJudge, warnJudge);
	     			}else{
	     				warnIdxs[0]="";//存放查询出的校验名称
	     				warnIdxs[1]="";//存放查询出的指标编号
	    	     		warnIdxs[2]="";//存放查询出的比较值类型
	    	     		warnIdxs[3]="";//存放查询出的幅度类型
	    	     		warnIdxs[4]="";//存放查询出的开始日期
	    	     		warnIdxs[5]="";//存放查询出的关联维度项
	    	     		warnIdxs[6]="";//存放查询出的备注
	     			}
	     		Field[] fe = vo.getClass().getDeclaredFields();//获取vo实体的私有方法
	     		if(warnIdxs.length <= fe.length){//一行指标个数不能大于导出模板的一行单元格数
	     			for(int i =0 ;i < warnIdxs.length; i++){//遍历获取的私有方法，逐个执行
	     				if(!fe[i].getName().equals("excelRowNo")&&!fe[i].getName().equals("sheetName")){
	     					ReflectionUtils.invokeSetter(vo, fe[i].getName(), warnIdxs[i]);//将rowIdx数组中的行数据存放至vo实体中
	     					}
	     				}
	     			vos2.add(vo);//将所有行数据放入vos中
	     			}
	     		}
			
			Object[] planvalIdxs=new Object[7];//初始化数组，存放需要导出的行数据
			
	     	for(int a=0;a<idxPlanvals.size();a++){
	     		IdxPlanvalImportVO vo = new IdxPlanvalImportVO();
	     		if(checkDimExists.get(idxPlanvals.get(a).getIndexNo())!=null){
	     			planvalIdxs[0]=checkDimExists.get(idxPlanvals.get(a).getIndexNo());//存放查询出的指标名称
	     		}else{
	     			planvalIdxs[0]=idxPlanvals.get(a).getIndexNo();//存放查询出的指标编号
	     		}
	     		planvalIdxs[1]=idxPlanvals.get(a).getOrgNo();//存放查询出的机构编号
	     		planvalIdxs[2]=checkOrgExists.get(idxPlanvals.get(a).getOrgNo());//存放查询出的机构名称
	     		planvalIdxs[3]=idxPlanvals.get(a).getDataDate();//存放查询出的计划年份
	     		planvalIdxs[4]=idxPlanvals.get(a).getCurrency();//存放查询出的计划类型
	     		planvalIdxs[5]=idxPlanvals.get(a).getIndexVal();//存放查询出的计划值
	     		planvalIdxs[6]=idxPlanvals.get(a).getRelateDim();//存放查询出的关联维度项
				
	     		Field[] fe = vo.getClass().getDeclaredFields();//获取vo实体的私有方法
	     		if(planvalIdxs.length <= fe.length){//一行指标个数不能大于导出模板的一行单元格数
	     			for(int i =0 ;i < planvalIdxs.length; i++){//遍历获取的私有方法，逐个执行
	     				if(!fe[i].getName().equals("excelRowNo")&&!fe[i].getName().equals("sheetName")){
	     					ReflectionUtils.invokeSetter(vo, fe[i].getName(), planvalIdxs[i]);//将rowIdx数组中的行数据存放至vo实体中
	     					}
	     				}
	     			vos3.add(vo);//将所有行数据放入vos中
	     			}
	     		}
	     	}
	
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos1);//放入第一个sheet页
		list.add(vos2);//放入第二个sheet页
		list.add(vos3);//放入第三个sheet页
		XlsExcelTemplateExporter fe1 = null;//初始化一个导出模板数据的对象
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";//去工程路径下取模板的文件名
			//定义一个导出模板数据的对象，传入文件名，模板路径，要导出的实体对象list
			fe1 = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
					+ GlobalConstants4plugin.EXPORT_IDXCHECK_TEMPLATE_PATH, list);
			fe1.run();//执行导出
		} catch (Exception e) {
			fileName = "";
			e.printStackTrace();
		} finally {
			try {
				if (fe1 != null) {
					fe1.destory();
				}
			} catch (BioneExporterException e) {
				e.printStackTrace();
			}
		}
		return fileName;
	}
	/**
	 * 指标计划值数据保存
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptValidCfgextLogic> idxLogic = (List<RptValidCfgextLogic>) lists.get(0);
		List<RptValidLogicIdxRel> idxLogicRel = (List<RptValidLogicIdxRel>) lists.get(1);
		List<IdxValidLogicVO> idxLogicDim = (List<IdxValidLogicVO>) lists.get(2);
		List<RptValidCfgextWarn> idxWarn = (List<RptValidCfgextWarn>) lists.get(3);
		List<RptValidWarnLevel> idxWarnLevel = (List<RptValidWarnLevel>) lists.get(4);
		List<IdxValidWarnVO> idxWarnDim = (List<IdxValidWarnVO>) lists.get(5);
		List<RptIdxPlanvalResult> idxPlanval = (List<RptIdxPlanvalResult>) lists.get(6);
		List<IdxValidPlanvalVO> idxPlanvalDim = (List<IdxValidPlanvalVO>) lists.get(7);
		
		
		if (idxLogic != null && idxLogic.size() > 0) {
			List<String> fields = new ArrayList<String>();
			fields.add("checkId");
			fields.add("checkNm");
			this.excelBS.deleteEntityJdbcBatch(idxLogic, fields);//根据缓存的数据为标识，执行数据库中的删除工作
			this.excelBS.saveEntityJdbcBatch(idxLogic);//将缓存中的数据全部添加到对应数据表中
			}
		if (idxLogicRel != null && idxLogicRel.size() > 0) {
			List<String> fields = new ArrayList<String>();
			fields.add("id.checkId");
			fields.add("id.indexNo");
			fields.add("id.formulaType");
			this.excelBS.deleteEntityJdbcBatch(idxLogicRel, fields);//根据缓存的数据为标识，执行数据库中的删除工作
			this.excelBS.saveEntityJdbcBatch(idxLogicRel);//将缓存中的数据全部添加到对应数据表中
			}
		if (idxLogicDim != null && idxLogicDim.size() > 0) {
			for(IdxValidLogicVO logic : idxLogicDim){
				logicBs.saveDimInfo(logic.getCheckId(), logic.getRelateDim(), logic.getDimTypes());
			}
		}
		if (idxWarn != null && idxWarn.size() > 0) {
			List<String> fields = new ArrayList<String>();
			fields.add("checkId");
			fields.add("checkNm");
			this.excelBS.deleteEntityJdbcBatch(idxWarn, fields);//根据缓存的数据为标识，执行数据库中的删除工作
			this.excelBS.saveEntityJdbcBatch(idxWarn);//将缓存中的数据全部添加到对应数据表中
			}
		if (idxWarnLevel != null && idxWarnLevel.size() > 0) {
			List<String> fields = new ArrayList<String>();
			fields.add("id.checkId");
			fields.add("indexNo");
			fields.add("levelNm");
			fields.add("levelType");
			this.excelBS.deleteEntityJdbcBatch(idxWarnLevel, fields);//根据缓存的数据为标识，执行数据库中的删除工作
			this.excelBS.saveEntityJdbcBatch(idxWarnLevel);//将缓存中的数据全部添加到对应数据表中
			}
		if (idxWarnDim != null && idxWarnDim.size() > 0) {
			for(IdxValidWarnVO warn : idxWarnDim){
				warnBs.saveDimInfo(warn.getCheckId(), null,warn.getRelateDim(), warn.getDimTypes());
			}
		}
		if (idxPlanval != null && idxPlanval.size() > 0) {
			List<String> fields = new ArrayList<String>();
			fields.add("id.indexNo");
			fields.add("id.orgNo");
			fields.add("id.dataDate");
			fields.add("id.currency");
			this.excelBS.deleteEntityJdbcBatch(idxPlanval, fields);//根据缓存的数据为标识，执行数据库中的删除工作
			this.excelBS.saveEntityJdbcBatch(idxPlanval);//将缓存中的数据全部添加到对应数据表中
			}
		if (idxPlanvalDim != null && idxPlanvalDim.size() > 0) {
			for(IdxValidPlanvalVO planval : idxPlanvalDim){
				idxPlanvalInfoBS.saveDimInfo(planval.getCheckId(),planval.getRelateDim(), planval.getDimTypes());
			}
		}
		}
	/**
	 * 上传指标计划值数据的检验
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 * @return 检验信息对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		// TODO Auto-generated method stub
		Map<String,String> checkIdxExists = new HashMap<String, String>();//初始化一个用于指标存在性检验的Map
		Map<String,String> checkOrgExists = new HashMap<String, String>();//初始化一个用于机构存在性检验的Map
		Map<String,String> checkDimExists = new HashMap<String, String>();//初始化一个用于币种存在性检验的Map
		Map<String,String> checkDimTypeExists = new HashMap<String, String>();//初始化一个用于维度种类存在性检验的Map
		Map<String,String> checkDimTypes = new HashMap<String, String>();//用于存放关联维度的维度类型
		Map<String,String> checkIdxMains = new HashMap<String, String>();//初始化一个用于指标、机构计划年份及币种同时重复的检验Map
		Map<String,String> checkNmWarnMap = new HashMap<String, String>();//初始化一个用于转换警示校验名称的map变量
		Map<String,String> checkNmLogic = new HashMap<String, String>();
		Map<String,String> checkNmWarn = new HashMap<String, String>();
		Map<String,String> checkWarnExist = new HashMap<String, String>();
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<RptValidCfgextLogic> idxLogics = new ArrayList<RptValidCfgextLogic>();
		List<RptValidLogicIdxRel> idxLogicRels = new ArrayList<RptValidLogicIdxRel>();
		List<IdxValidLogicVO> idxLogicDims = new ArrayList<IdxValidLogicVO>();
		List<RptValidCfgextWarn> idxWarns = new ArrayList<RptValidCfgextWarn>();
		List<RptValidWarnLevel> idxWarnLevels = new ArrayList<RptValidWarnLevel>();
		List<IdxValidWarnVO> idxWarnDims = new ArrayList<IdxValidWarnVO>();
		List<RptIdxPlanvalResult> idxPlanvals = new ArrayList<RptIdxPlanvalResult>();
		List<IdxValidPlanvalVO> idxPlanvalDims = new ArrayList<IdxValidPlanvalVO>();
		
		String ehcacheIds[] = StringUtils.split(ehcacheId, ',');
		List<IdxLogicImportVO> vos1 = (List<IdxLogicImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheIds[0]);
		List<IdxWarnImportVO> vos2 = (List<IdxWarnImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheIds[1]);
		List<IdxPlanvalImportVO> vos3 = (List<IdxPlanvalImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheIds[2]);

		//先去查询数据库里存在的指标编号和机构编号及币种编号
		List<Object[]> idxs = logicBs.getIndexAndMeasureList();
		List<Object[]> dimTypes = logicBs.getCheckDimList();
		List<Object[]> orgs = idxPlanvalInfoBS.getOrgNoList();
		List<Object[]> dims = idxPlanvalInfoBS.getDimItemNoList();
		List<Object[]> checkIdWarns = warnBs.getCheckIdList();
		
		if(null != idxs && idxs.size() > 0){//将查询到的指标编号放入到map变量中
			for(Object[] idx : idxs){
				checkIdxExists.put(idx[1].toString(), idx[0].toString());//put(key,value)
			}
		}
		if(null != dimTypes && dimTypes.size() > 0){//将查询到的维度种类信息放入到map变量中
			for(Object[] dimType : dimTypes){
				checkDimTypeExists.put(dimType[1].toString(), dimType[0].toString());//put(key,value)
				checkDimTypes.put(dimType[1].toString(), dimType[2].toString());
			}
		}
		if(null != orgs && orgs.size() > 0){//将查询到的机构编号放入到map变量中
			for(Object[] org : orgs){
				checkOrgExists.put(org[0].toString(), org[1].toString());//put(key,value)
			}
		}
		if(null != dims && dims.size() > 0){//将查询到的币种编号放入到map变量中
			for(Object[] dim : dims){
				checkDimExists.put(dim[0].toString(), dim[0].toString());//put(key,value)
			}
		}
		if(null != checkIdWarns && checkIdWarns.size() > 0){
			for(Object[] checkId : checkIdWarns){
				checkNmWarnMap.put(checkId[1].toString(), checkId[0].toString());
			}
		}
		
		Map<String, String> logicOperTypeMap = new HashMap<String, String>();
		logicOperTypeMap.put("<", "<");
		logicOperTypeMap.put("<=", "<=");
		logicOperTypeMap.put("==", "==");
		logicOperTypeMap.put(">=", ">=");
		logicOperTypeMap.put(">", ">");
		logicOperTypeMap.put("!=", "!=");
		
		Map<String, String> compareValTypeMap = new HashMap<String, String>();
		compareValTypeMap.put("01", "上日");
		compareValTypeMap.put("02", "月初");
		compareValTypeMap.put("03", "上月末");
		compareValTypeMap.put("04", "上月同期");
		compareValTypeMap.put("05", "季初");
		compareValTypeMap.put("06", "上季末");
		compareValTypeMap.put("07", "年初");
		compareValTypeMap.put("08", "上年末");
		compareValTypeMap.put("09", "上年同期");
		
		Map<String, String> rangeTypeMap = new HashMap<String, String>();
		rangeTypeMap.put("01", "数字");
		rangeTypeMap.put("02", "百分比");
		
		if (vos1 != null && vos1.size() > 0) {
			for (IdxLogicImportVO vo : vos1) {
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				Map<String, String> leftExpressionDesc = this.idxInfoBS.replaceNoByIdx(vo.getLeftExpression());
				Map<String, String> rightExpressionDesc = this.idxInfoBS.replaceNoByIdx(vo.getRightExpression());
				Map<String, String> leftIdxNos = this.logicBs.splitNoByIdx(vo.getLeftExpression());
				Map<String, String> rightIdxNos = this.logicBs.splitNoByIdx(vo.getRightExpression());
				
				//保存逻辑校验和指标的关系表
				String leftIdxs[] = StringUtils.split(leftIdxNos.get("indexNos"), ',');
				String rightIdxs[] = StringUtils.split(rightIdxNos.get("indexNos"), ',');
				
				String leftExpression = vo.getLeftExpression();//获取导入的指标编号
				String leftExpressionId = leftExpressionDesc.get("expression");
				String logicOperType = vo.getLogicOperType();
				String rightExpression = vo.getRightExpression();
				String rightExpressionId = rightExpressionDesc.get("expression");
//				String linkNo = leftExpressionId+logicOperType+rightExpressionId;
				String linkNm = "("+leftExpression+")"+logicOperType+"("+rightExpression+")";
				String relateDim = vo.getRelateDim();
				String reDimNos = "";
				String reDimTypes = ""; 
				
				//检验校验公式名称是否同时重复
				if(checkNmLogic.get(vo.getCheckNm())!=null){//在循环遍历excell记录时，将记录的联合编号exists逐条与map变量的key比较，判断此条记录的标识在excell内部是否存在重复
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
	            	obj.setSheetName(vo.getSheetName());
	            	obj.setExcelRowNo(vo.getExcelRowNo());
	            	obj.setExcelColNo(1);
	            	obj.setValidTypeNm("唯一性校验");
	            	obj.setErrorMsg("逻辑校验公式名称导入重复");
	            	errors.add(obj);
	            	}
				else{
					//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					checkNmLogic.put(vo.getCheckNm(), vo.getCheckNm());
					}
				
				//检验左表达式中导入的左表达式中指标是否存在
				if(null != vo.getLeftExpression() && null == leftIdxNos.get("indexNos")){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(2);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("左表达式中指标"+leftIdxNos.get("indexNms")+"不存在");
					errors.add(obj);
					}
				
				//检验导入的逻辑运算类型是否存在
				if(null != vo.getLogicOperType() && null == logicOperTypeMap.get(vo.getLogicOperType())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(3);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该逻辑运算类型不存在");
					errors.add(obj);
					}
				
				//检验右表达式中导入的右表达式中指标是否存在
				if(null != vo.getRightExpression() && null == rightIdxNos.get("indexNos")){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(4);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("右表达式中指标"+rightIdxNos.get("indexNms")+"不存在");
					errors.add(obj);
					}
				
				if(relateDim != null){
					relateDim = relateDim +",";
					String[] reDims = StringUtils.split(relateDim, ',');
					Map<String,String> checkLogicDims = new HashMap<String, String>();//初始化一个逻辑校验中用于校验关联维度项中维度是否重复的map变量
					
					for(String reDim : reDims){
						if(checkDimTypeExists.get(reDim)== null){ 
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(8);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("关联维度项中“"+reDim+"”维度不存在");
							errors.add(obj);
							}else{
								reDimNos += checkDimTypeExists.get(reDim) + ",";
								reDimTypes += checkDimTypes.get(reDim) + ",";
							}
						//检验关联维度项中维度是否重复
						if(checkLogicDims.get(reDim)!=null){
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
			            	obj.setSheetName(vo.getSheetName());
			            	obj.setExcelRowNo(vo.getExcelRowNo());
			            	obj.setExcelColNo(8);
			            	obj.setValidTypeNm("唯一性校验");
			            	obj.setErrorMsg("关联维度项中“"+reDim+"”维度导入重复");
			            	errors.add(obj);
			            	}
						else{
							//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
							checkLogicDims.put(reDim, reDim);
							}
					}
					reDimNos = reDimNos.substring(0,reDimNos.length()-1);
					reDimTypes = reDimTypes.substring(0,reDimTypes.length()-1);
				}
				
				/*//将左表达式、逻辑运算类型及右表达式联合作为唯一标识，用于左表达式、逻辑运算类型及右表达式同时重复的检验
				String exists=linkNo;
				//检验左表达式、逻辑运算类型及右表达式是否同时重复
				if(checkLogicMains.get(exists)!=null){//在循环遍历excell记录时，将记录的联合编号exists逐条与map变量的key比较，判断此条记录的标识在excell内部是否存在重复
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
	            	obj.setSheetName(vo.getSheetName());
	            	obj.setExcelRowNo(vo.getExcelRowNo());
	            	obj.setExcelColNo(4);
	            	obj.setValidTypeNm("唯一性校验");
	            	obj.setErrorMsg("指标逻辑校验表达式导入重复");
	            	errors.add(obj);
	            	}
				else{
					//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					checkLogicMains.put(exists, exists);
					}*/
				
				RptValidCfgextLogic logic = new RptValidCfgextLogic();
				IdxValidLogicVO logicImport = new IdxValidLogicVO(); 
//				String composite= leftExpressionId+","+logicOperType+","+rightExpressionId;
				String checkIdExist = this.logicBs.getLogicExistList(vo.getCheckNm());
				
				if(vo.getCheckNm()!=null&&!"".equals(vo.getCheckNm())){ 
					String checkId;
					if("".equals(checkIdExist)){
						checkId = RandomUtils.uuid2();
					}else{
						checkId = checkIdExist;
					}
					logic.setCheckId(checkId);
					//logic.setCheckNm(vo.getCheckNm());监管平台校验表无这个字段
					logic.setExpressionDesc(linkNm);
					logic.setLogicOperType(logicOperType);
					logic.setLeftExpression(leftExpressionId);
					logic.setRightExpression(rightExpressionId);
					logic.setFloatVal(vo.getFloatVal());
					logic.setStartDate(vo.getStartDate());
					logic.setEndDate("29991231");
					logic.setBusiExplain(vo.getBusiExplain());
					logic.setIsPre("0");
					logicImport.setCheckId(checkId);
					logicImport.setRelateDim(reDimNos);
					logicImport.setDimTypes(reDimTypes);
					for(String tmp : leftIdxs){
						RptValidLogicIdxRelPK pk = new RptValidLogicIdxRelPK();
						RptValidLogicIdxRel rel = new RptValidLogicIdxRel();
						pk.setCheckId(checkId);
						pk.setIndexNo(tmp);
						pk.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_LEFT);
						rel.setId(pk);
						idxLogicRels.add(rel);
					}
					for(String tmp : rightIdxs){
						RptValidLogicIdxRelPK pk = new RptValidLogicIdxRelPK();
						RptValidLogicIdxRel rel = new RptValidLogicIdxRel();
						pk.setCheckId(checkId);
						pk.setIndexNo(tmp);
						pk.setFormulaType(GlobalConstants4plugin.FORMULA_TYPE_RIGHT);
						rel.setId(pk);
						idxLogicRels.add(rel);
					}
				    idxLogics.add(logic);
				    idxLogicDims.add(logicImport);
				    }
			    }		
			}
		String checkIdWarn = null;
		if (vos2 != null && vos2.size() > 0) {
			for (IdxWarnImportVO vo : vos2) {
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				String reDimNos = "";
				String reDimTypes = ""; 
                Boolean flag = true;
				if(null == vo.getCheckNm() && null == vo.getIndexNo() && null == vo.getCompareValType() && null != vo.getLevelNm() ){
					flag = false;
					vo.setCheckNm(checkWarnExist.get("checkNm"));
					vo.setIndexNo(checkWarnExist.get("indexNo"));
					vo.setCompareValType(checkWarnExist.get("compareValType"));
					vo.setRangeType(checkWarnExist.get("rangeType"));
					vo.setStartDate(checkWarnExist.get("startDate"));
					vo.setRemark(checkWarnExist.get("remark"));
					vo.setRelateDim(checkWarnExist.get("relateDim"));
					
				}
				//检验校验名称是否同时重复
				if(checkNmWarn.get(vo.getCheckNm())!=null && flag == true){//在循环遍历excell记录时，将记录的编号逐条与map变量的key比较，判断此条记录的标识在excell内部是否存在重复
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
	            	obj.setSheetName(vo.getSheetName());
	            	obj.setExcelRowNo(vo.getExcelRowNo());
	            	obj.setExcelColNo(1);
	            	obj.setValidTypeNm("唯一性校验");
	            	obj.setErrorMsg("警示校验名称导入重复");
	            	errors.add(obj);
	            	}
				else{
					//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					checkNmWarn.put(vo.getCheckNm(), vo.getCheckNm());
					checkWarnExist.put("checkNm",vo.getCheckNm());
					checkWarnExist.put("indexNo",vo.getIndexNo());
					checkWarnExist.put("compareValType",vo.getCompareValType());
					checkWarnExist.put("rangeType",vo.getRangeType());
					checkWarnExist.put("startDate",vo.getStartDate());
					checkWarnExist.put("remark",vo.getRemark());
					checkWarnExist.put("relateDim",vo.getRelateDim());
					}
				//检验导入的指标是否存在
				if(null != vo.getIndexNo() && null == checkIdxExists.get(vo.getIndexNo())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(2);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该指标不存在");
					errors.add(obj);
					}
				//检验导入的比较值类型是否存在
				if(null != vo.getCompareValType() && null == compareValTypeMap.get(vo.getCompareValType())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(3);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该比较值类型不存在");
					errors.add(obj);
					}
				
				//检验导入的幅度类型是否存在
				if(null != vo.getRangeType() && null == rangeTypeMap.get(vo.getRangeType())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(4);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该幅度类型不存在");
					errors.add(obj);
					}
				
				String relateDim = vo.getRelateDim();
				if(relateDim != null){
					relateDim = relateDim +",";
					String[] reDims = StringUtils.split(relateDim, ',');
					Map<String,String> checkWarnDims = new HashMap<String, String>();//初始化一个警示校验中用于校验关联维度项中维度是否重复的map变量
					
					for(String reDim : reDims){
						if(checkDimTypeExists.get(reDim)== null){ 
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(6);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("关联维度项中“"+reDim+"”维度不存在");
							errors.add(obj);
							}else{
								reDimNos += checkDimTypeExists.get(reDim) + ",";
								reDimTypes += checkDimTypes.get(reDim) + ",";
							}
						//检验关联维度项中维度是否重复
						if(checkWarnDims.get(reDim)!=null){
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
			            	obj.setSheetName(vo.getSheetName());
			            	obj.setExcelRowNo(vo.getExcelRowNo());
			            	obj.setExcelColNo(6);
			            	obj.setValidTypeNm("唯一性校验");
			            	obj.setErrorMsg("关联维度项中“"+reDim+"”维度导入重复");
			            	errors.add(obj);
			            	}
						else{
							//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
							checkWarnDims.put(reDim, reDim);
							}
					}
					reDimNos = reDimNos.substring(0,reDimNos.length()-1);
					reDimTypes = reDimTypes.substring(0,reDimTypes.length()-1);
				}
				
				/*//将指标编号、比较值类型、幅度类型、级别名称联合作为唯一标识，用于指标、比较值类型、幅度类型、级别名称同时重复的检验
				String exists=vo.getIndexNo()+","+vo.getCompareValType()+","+vo.getRangeType()+","+vo.getLevelNm();
				
				//检验指标编号、比较值类型、幅度类型、级别名称是否同时重复
				if(checkWarnMains.get(exists)!=null){//在循环遍历excell记录时，将记录的联合编号exists逐条与map变量的key比较，判断此条记录的标识在excell内部是否存在重复
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
	            	obj.setSheetName(vo.getSheetName());
	            	obj.setExcelRowNo(vo.getExcelRowNo());
	            	obj.setExcelColNo(7);
	            	obj.setValidTypeNm("唯一性校验");
	            	obj.setErrorMsg("指标警示校验数据导入重复");
	            	errors.add(obj);
	            	}
				else{
					//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					checkWarnMains.put(exists, exists);
					}*/
				
				RptValidCfgextWarn idxWarn = new RptValidCfgextWarn();
				IdxValidWarnVO warnImport = new IdxValidWarnVO(); 
				RptValidWarnLevel warnLevel = new RptValidWarnLevel();
				RptValidWarnLevelPK pk = new RptValidWarnLevelPK();
				
//				String composite= vo.getIndexNo()+","+vo.getCompareValType()+","+vo.getRangeType();
//				String checkIdExist = this.warnBs.getWarnExistList(composite);
				
				if(vo.getCheckNm()!=null){
					String checkId;
					if(checkNmWarnMap.get(vo.getCheckNm())==null && flag == true){
						checkId = RandomUtils.uuid2();
						checkIdWarn = checkId;
					}else if(flag == false){
						checkId = checkIdWarn;
					}else{
						checkId = checkNmWarnMap.get(vo.getCheckNm());
						checkIdWarn = checkId;
					}
					if(flag){
						idxWarn.setCheckId(checkId);
						idxWarn.setCheckNm(vo.getCheckNm());
						idxWarn.setIndexNo(checkIdxExists.get(vo.getIndexNo()));
						idxWarn.setCompareValType(vo.getCompareValType());
						idxWarn.setRangeType(vo.getRangeType());
						idxWarn.setStartDate(vo.getStartDate());
						idxWarn.setEndDate("29991231");
						idxWarn.setRemark(vo.getRemark());
						idxWarns.add(idxWarn);
						warnImport.setCheckId(checkId);
						warnImport.setRelateDim(reDimNos);
						warnImport.setDimTypes(reDimTypes);
						idxWarnDims.add(warnImport);
						}
					pk.setCheckId(checkId);
					pk.setLevelNum(RandomUtils.uuid2());
					warnLevel.setId(pk);
					warnLevel.setIndexNo(checkIdxExists.get(vo.getIndexNo()));
					warnLevel.setLevelNm(vo.getLevelNm());
					warnLevel.setLevelType(vo.getLevelType());
					warnLevel.setPostiveRangeVal(vo.getPostiveRangeVal());
					warnLevel.setMinusRangeVal(vo.getMinusRangeVal());
					warnLevel.setRemindColor(vo.getRemindColor());
					warnLevel.setIsPassCond(vo.getIsPassCond());
					idxWarnLevels.add(warnLevel);
				    }
			    }		
			}
		
		if (vos3 != null && vos3.size() > 0) {
			for (IdxPlanvalImportVO vo : vos3) {
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				String reDimNos = "";
				String reDimTypes = ""; 
				//检验导入的指标是否存在
				if(null != vo.getIndexNo() && null == checkIdxExists.get(vo.getIndexNo())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该指标不存在");
					errors.add(obj);
					}
				//检验导入的机构是否存在
				if(null != vo.getOrgNo() && null == checkOrgExists.get(vo.getOrgNo())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(2);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该机构编号不存在");
					errors.add(obj);
					}
				//检验导入的币种是否存在
				if(null != vo.getCurrency() && null == checkDimExists.get(vo.getCurrency())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该币种不存在");
					errors.add(obj);
					}
				if(null == vo.getCurrency()||vo.getCurrency()==""){
					vo.setCurrency("-");
				}
				//将指标编号、机构编号、计划年份与币种联合作为唯一标识，用于指标、机构、计划年份及币种同时重复的检验
				String exists=vo.getIndexNo()+","+vo.getOrgNo()+","+vo.getDataDate()+","+vo.getCurrency();
				//检验指标、机构、计划年份及币种是否同时重复
				if(checkIdxMains.get(exists)!=null){//在循环遍历excell记录时，将记录的联合编号exists逐条与map变量的key比较，判断此条记录的标识在excell内部是否存在重复
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
	            	obj.setSheetName(vo.getSheetName());
	            	obj.setExcelRowNo(vo.getExcelRowNo());
	            	obj.setExcelColNo(5);
	            	obj.setValidTypeNm("唯一性校验");
	            	obj.setErrorMsg("指标计划值校验数据导入重复");
	            	errors.add(obj);
	            	}
				else{
					//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					checkIdxMains.put(exists, exists);
					}
				
				String relateDim = vo.getRelateDim();
				if(relateDim != null){
					relateDim = relateDim +",";
					String[] reDims = StringUtils.split(relateDim, ',');
					Map<String,String> checkPlanDims = new HashMap<String, String>();//初始化一个计划值校验中用于校验关联维度项中维度是否重复的map变量
					
					for(String reDim : reDims){
						if(checkDimTypeExists.get(reDim)== null){ 
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(7);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("关联维度项中“"+reDim+"”维度不存在");
							errors.add(obj);
							}else{
								reDimNos += checkDimTypeExists.get(reDim) + ",";
								reDimTypes += checkDimTypes.get(reDim) + ",";
							}
						//检验关联维度项中维度是否重复
						if(checkPlanDims.get(reDim)!=null){
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
			            	obj.setSheetName(vo.getSheetName());
			            	obj.setExcelRowNo(vo.getExcelRowNo());
			            	obj.setExcelColNo(7);
			            	obj.setValidTypeNm("唯一性校验");
			            	obj.setErrorMsg("关联维度项中“"+reDim+"”维度导入重复");
			            	errors.add(obj);
			            	}
						else{
							//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
							checkPlanDims.put(reDim, reDim);
							}
					}
					reDimNos = reDimNos.substring(0,reDimNos.length()-1);
					reDimTypes = reDimTypes.substring(0,reDimTypes.length()-1);
				}
				
				RptIdxPlanvalResult idxPlanval =new RptIdxPlanvalResult();
				RptIdxPlanvalResultPK pk = new RptIdxPlanvalResultPK();
				IdxValidPlanvalVO planvalImport = new IdxValidPlanvalVO();
				
				if(checkIdxExists.get(vo.getIndexNo())!=null){ 
					pk.setIndexNo(checkIdxExists.get(vo.getIndexNo()));//行数据的指标编号
					pk.setOrgNo(vo.getOrgNo());//行数据的机构编号
					pk.setDataDate(vo.getDataDate());
					pk.setCurrency(vo.getCurrency());
					pk.setPlanType("01");
					pk.setDim1("-");
					pk.setDim2("-");
					pk.setDim3("-");
					pk.setDim4("-");
					pk.setDim5("-");
					pk.setDim6("-");
					pk.setDim7("-");
					pk.setDim8("-");
					pk.setDim9("-");
					pk.setDim10("-");
//				    pk.setIndexVerId(Integer.parseInt(checkIdxExists.get(vo.getIndexNo())));//行数据的版本号
				    idxPlanval.setId(pk);
				    idxPlanval.setIndexVal(vo.getIndexVal());//行数据的计划值
				    idxPlanvals.add(idxPlanval);
				    planvalImport.setCheckId(checkIdxExists.get(vo.getIndexNo()));
				    planvalImport.setRelateDim(reDimNos);
				    planvalImport.setDimTypes(reDimTypes);
				    idxPlanvalDims.add(planvalImport);
				    }
			    }		
			}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(idxLogics);
		lists.add(idxLogicRels);
		lists.add(idxLogicDims);
		lists.add(idxWarns);
		lists.add(idxWarnLevels);
		lists.add(idxWarnDims);
		lists.add(idxPlanvals);
		lists.add(idxPlanvalDims);
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,lists);//放入缓存
		return errors;
	}
	@Override
	public String exportAll(String ids) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<ValidErrorInfoObj> validateVerInfo(String dsId, String ehcacheId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void saveData(HttpServletRequest request, String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		
	}
}
