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
import com.yusys.bione.frame.excel.ExcelAnnotationUtil;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.excel.ExcelImporter;
import com.yusys.bione.frame.excel.XlsExcelTemplateExporter;
import com.yusys.bione.frame.excel.annotations.ExcelColumn;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCompGrp;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCompGrpPK;
import com.yusys.bione.plugin.rptidx.service.RptIdxCompGrpBS;
import com.yusys.bione.plugin.wizard.web.vo.CompImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
/**
 * <pre>
 * Title:指标对比组关系信息的导入导出
 * Description: 提供指标对比组关系的导入导出及检验等功能
 * </pre>
 * 
 * @author yanqq@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
public class CompgrpWizardRequire extends BaseController implements
		IWizardRequire {
	private RptIdxCompGrpBS rptIdxCompGrpBS = SpringContextHolder.getBean("rptIdxCompGrpBS");
	@Autowired
	private ExcelBS excelBS;
	
	/**
	 * 指标对比组关系信息上传
	 * @param file 文件对象
	 * @return 上传文件结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(CompImportVO.class,
						file);
		UploadResult result = new UploadResult();
		try {
			List<CompImportVO> vos = (List<CompImportVO>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					vos);
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos != null && vos.size() > 0) {
				Map<String,String> idxNmMap = new HashMap<String, String>();//初始化一个用于转换指标名称的map变量
				List<Object[]> idxs = rptIdxCompGrpBS.getIndexNoList();//查询所有指标，用于指标编号转指标名称，返回到页面
				if(null != idxs && idxs.size() > 0){//把指标方法放入到map里，便于判断
					for(Object[] idx : idxs){
						idxNmMap.put(idx[0].toString(), idx[1].toString());
					}
				}
				for (CompImportVO vo : vos) {
					String mainIndexNo=vo.getMainIndexNo();//获取主指标编号
					String mainIndexNm = idxNmMap.get(mainIndexNo);//获取主指标名称
					if(StringUtils.isNotBlank(mainIndexNo)){
						if(mainIndexNm==null){
							mainIndexNm="未知指标-对比组";
						}else{
							mainIndexNm=mainIndexNm+"-对比组";
						}
						result.setInfo(mainIndexNm, mainIndexNm);
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
	 * 指标对比组关系信息的导出
	 * @param idxs 选中的需要导出的主指标编号
	 * @return 文件名称
	 */
	@Override
	public String export(String idxs) {
		String fileName = "";
		List<CompImportVO> vos = new ArrayList<CompImportVO>();//初始化一个list，用于存放导出工作簿中第一个sheet页的内容
		String[] idx = StringUtils.split(idxs.toString(), ',');
		for(int j=0;j<idx.length;j++){
			//查询要导出的指标对比组关联信息
			List<RptIdxCompGrp> grps = this.rptIdxCompGrpBS.getAllCompGrpList(idx[j]);
			CompImportVO vo = new CompImportVO();
			String[] rowIdxs=new String[grps.size()+2];//初始化数组，存放需要导出的行数据
			rowIdxs[0]=idx[j];//数组第一位：存放主指标
			rowIdxs[1]="无对比组";//处理选中主指标没有对比指标的状况
	     	for(int a=0;a<grps.size();a++){
	     		rowIdxs[a+1]=grps.get(a).getId().getIndexNo();//存放查询出的对比指标
	     		}
	     		Field[] fe = vo.getClass().getDeclaredFields();//获取vo实体的私有方法
	     		if(rowIdxs.length <= fe.length){//一行指标个数不能大于导出模板的一行单元格数
	     			for(int i =0 ;i < rowIdxs.length; i++){//遍历获取的私有方法，逐个执行
	     				if(!fe[i].getName().equals("excelRowNo")&&!fe[i].getName().equals("sheetName")){
	     					ReflectionUtils.invokeSetter(vo, fe[i].getName(), rowIdxs[i]);//将rowIdx数组中的行数据存放至vo实体中
	     				}			
	     			}
	     			vos.add(vo);//将所有行数据放入vos中
	     		}
	     		
	     	}
	
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);//放入第一个sheet页
		XlsExcelTemplateExporter fe1 = null;//初始化一个导出模板数据的对象
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";//去工程路径下取模板的文件名
			//定义一个导出模板数据的对象，传入文件名，模板路径，要导出的实体对象list
			fe1 = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
					+ GlobalConstants4plugin.EXPORT_COMP_TEMPLATE_PATH, list);
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
	 * 对比组关系数据保存
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptIdxCompGrp> compGrps = (List<RptIdxCompGrp>) lists.get(0);
		
		if (compGrps != null && compGrps.size() > 0) {
			for(RptIdxCompGrp vo:compGrps){
				rptIdxCompGrpBS.deleteData(vo.getId().getMainIndexNo());//导入数据与库中数据存在重复，执行删除工作
				}
			}
		
		List<String> fields = new ArrayList<String>();
		fields.add("mainIndexNo");
		this.excelBS.deleteEntityJdbcBatch(compGrps, fields);
		this.excelBS.saveEntityJdbcBatch(compGrps);
	}
	/**
	 * 上传指标对比组关系数据的检验
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 * @return 检验信息对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		// TODO Auto-generated method stub
		Map<String,String> checkIdxExists = new HashMap<String, String>();//初始化一个用于指标存在性检验的Map
		Map<String,String> checkIdxMains = new HashMap<String, String>();//初始化一个用于主指标重复性检验的Map
		Map<String,String> checkIdxDifs = new HashMap<String, String>();//初始化一个用于指标重复性检验的Map
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<RptIdxCompGrp> compgrps = new ArrayList<RptIdxCompGrp>();
		List<CompImportVO> vos = (List<CompImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);

		
		//先去查询数据库里存在的指标编号
		List<Object[]> idxs = rptIdxCompGrpBS.getIndexNoList();
		
		if(null != idxs && idxs.size() > 0){//将查询到的主指标编号放入到map变量中
			for(Object[] idx : idxs){
				checkIdxExists.put(idx[0].toString(), idx[0].toString());//put(key,value)
			}
		}

		if (vos != null && vos.size() > 0) {
			for (CompImportVO vo : vos) {
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				//循环进行主指标重复性检验，并记录检验日志
				if(checkIdxMains.get(vo.getMainIndexNo())!=null){//在循环遍历excell记录时，将记录的主指标编号逐条与map变量的key比较，判断此条记录的主指标在excell内部是否存在重复
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
	            	obj.setSheetName(vo.getSheetName());
	            	obj.setExcelRowNo(vo.getExcelRowNo());
	            	obj.setExcelColNo(1);
	            	obj.setValidTypeNm("唯一性校验");
	            	obj.setErrorMsg("主指标编号不能重复");
	            	errors.add(obj);
	            	}
				else{
					//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					checkIdxMains.put(vo.getMainIndexNo(), vo.getMainIndexNo());
					}
				checkIdxDifs.clear();//清除数据
				//循环excel的单行的每一列的指标
				Field[] fe = vo.getClass().getDeclaredFields();//获取vo实体的私有方法
				for(int i =0 ;i < fe.length ; i++){//遍历获取的私有方法，逐个执行
					if(!fe[i].getName().equals("excelRowNo")&&!fe[i].getName().equals("sheetName")){
						ExcelColumn ec = fe[i].getAnnotation(ExcelColumn.class);//获取方法注释
						int index=ExcelAnnotationUtil.getExcelCol(ec.index());//获取每个单元格的列号并转换成数字
						String indexNo = (String) ReflectionUtils.invokeGetter(vo, fe[i].getName());//获取每个单元格的值
						if(StringUtils.isNotBlank(indexNo)){
							//循环进行指标存在性检验，并记录检验日志
							if(null != indexNo && null == checkIdxExists.get(indexNo)){
								ValidErrorInfoObj obj=new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(index+1);
								obj.setValidTypeNm("存在性校验");
								obj.setErrorMsg("指标编号不存在");
								errors.add(obj);
								}
							//循环进行指标重复性检验，并记录检验日志
							if(null != indexNo && null != checkIdxDifs.get(indexNo)){
								ValidErrorInfoObj obj=new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(index+1);
								obj.setValidTypeNm("存在性校验");
								obj.setErrorMsg("同一对比组中指标不可重复");
								errors.add(obj);
								}
							else{
								//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录								
								checkIdxDifs.put(indexNo,indexNo);
								}			
							RptIdxCompGrp compgrp=new RptIdxCompGrp();
							RptIdxCompGrpPK pk = new RptIdxCompGrpPK();
							if(!indexNo.equals(vo.getMainIndexNo())){
								pk.setCompgrpId(RandomUtils.uuid2());//自动生成ID
							    pk.setMainIndexNo(vo.getMainIndexNo());//行数据的主指标
							    pk.setIndexNo(indexNo);//行数据的对比指标
							    compgrp.setId(pk);
							    compgrps.add(compgrp);//放入到对比组list中
							    }
							}
						}
					}
				}
			}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(compgrps);
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
