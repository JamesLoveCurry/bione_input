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
import com.yusys.bione.plugin.rptidx.entity.RptIdxSimilarGrp;
import com.yusys.bione.plugin.rptidx.entity.RptIdxSimilarGrpPK;
import com.yusys.bione.plugin.rptidx.service.RptIdxSimiGrpBS;
import com.yusys.bione.plugin.wizard.web.vo.SimilarImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
/**
 * <pre>
 * Title:指标同类组的导入导出
 * Description: 提供指标同类组上传/指标同类组信息的导出/指标同类组保存/上传指标同类组信息的检验等功能
 * </pre>
 * 
 * @author hubing hubing1@yusys.com.cn
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
public class SimilarWizardRequire extends BaseController implements
		IWizardRequire {
	private RptIdxSimiGrpBS rptIdxSimiGrpBS = SpringContextHolder.getBean("rptIdxSimiGrpBS");
	@Autowired
	private ExcelBS excelBS;
	
	/**
	 * 指标同类组上传
	 * @param file 文件对象
	 * @return 上传文件结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(SimilarImportVO.class, file);
		UploadResult result = new UploadResult();
		try {
			List<SimilarImportVO> vos = (List<SimilarImportVO>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					vos);
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos != null && vos.size() > 0) {
				Map<String,String> idxNmMap = new HashMap<String, String>();//初始化一个用于转换指标名称的map变量
				List<Object[]> idxs = rptIdxSimiGrpBS.getIndexNoList();//查询所有指标，用于指标编号转指标名称，返回到页面
				if(null != idxs && idxs.size() > 0){//把指标方法放入到map里，便于判断
					for(Object[] idx : idxs){
						idxNmMap.put(idx[0].toString(), idx[1].toString());
					}
				}
				for (SimilarImportVO vo : vos) {
					//循环excel的单行的每一列的指标
					String idxNms = "";
					Field[] fe = vo.getClass().getDeclaredFields();//获取vo实体的私有方法
					for(int i =0 ;i < fe.length ; i++){//遍历获取的私有方法，逐个执行
						if(!fe[i].getName().equals("excelRowNo")&&!fe[i].getName().equals("sheetName")){//排除fe里多余的方法
							String indexNo = (String)ReflectionUtils.invokeGetter(vo, fe[i].getName());//获取每个单元格的值
							Object indexNm = idxNmMap.get(indexNo);
							if(StringUtils.isNotBlank(indexNo)){
								if(indexNm != null){
									idxNms =idxNms + "," + indexNm;
								}else{
									idxNms =idxNms + ",未知指标" ;
								}
							}
						}
					}
					if(StringUtils.isNotBlank(idxNms)){
						idxNms = StringUtils.substring(idxNms, 1);
						result.setInfo(idxNms, idxNms); 
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
	 * 同类组信息的导出
	 * @param ids 选中的行编号
	 * @return 文件名称
	 */
	@Override
	public String export(String ids) {
		String fileName = "";
		//查询要导出的用户信息
		List<RptIdxSimilarGrp> grps = this.rptIdxSimiGrpBS.getAllSimiGrpList();
		List<SimilarImportVO> vos = new ArrayList<SimilarImportVO>();//初始化一个list，用于存放导出工作簿中第一个sheet页的内容
		Map<String,List<String>> gripIdMap = new HashMap<String, List<String>>();
		if(grps != null && grps.size() > 0){
			for(RptIdxSimilarGrp grp : grps){
				if(gripIdMap.get(grp.getId().getSimigrpId()) == null){
					List<String> indexNos = new ArrayList<String>();
					indexNos.add(grp.getId().getIndexNo());
					gripIdMap.put(grp.getId().getSimigrpId(), indexNos);
				}
				else{
					gripIdMap.get(grp.getId().getSimigrpId()).add(grp.getId().getIndexNo());
				}
			}
		}
		if(gripIdMap != null && gripIdMap.size() > 0){
			for(String key : gripIdMap.keySet()){
				List<String> indexNos = gripIdMap.get(key);
				SimilarImportVO vo = new SimilarImportVO();
				Field[] fe = vo.getClass().getDeclaredFields();//获取vo实体的私有方法
				if(indexNos.size() <= fe.length){//一行指标个数不能大于导出模板的一行单元格数
					for(int i =0 ;i < indexNos.size() ; i++){//遍历获取的私有方法，逐个执行
						if(!fe[i].getName().equals("excelRowNo")&&!fe[i].getName().equals("sheetName")){
							ReflectionUtils.invokeSetter(vo, fe[i].getName(),indexNos.get(i));
						}
					}
				}
				vos.add(vo);
			}
		}
		
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(vos);//放入第一个sheet页
		XlsExcelTemplateExporter fe = null;//初始化一个导出模板数据的对象
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";//去工程路径下取模板的文件名
			//定义一个导出模板数据的对象，传入文件名，模板路径，要导出的实体对象list
			fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
					+ GlobalConstants4plugin.EXPORT_SIMILAR_TEMPLATE_PATH, list);
			fe.run();//执行导出
		} catch (Exception e) {
			fileName = "";
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
		return fileName;
	}
	/**
	 * 同类组信息保存
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);//获取检验是放入缓存的同类组数据
		List<RptIdxSimilarGrp> grps = (List<RptIdxSimilarGrp>) lists.get(0);
		rptIdxSimiGrpBS.delAllGrps();//保存前先整表删除数据
		this.excelBS.saveEntityJdbcBatch(grps);//保存导入数据
	}
	/**
	 * 上传同类组信息的检验
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 * @return 检验信息对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		// TODO Auto-generated method stub
		Map<String,String> checkIdxsExsi = new HashMap<String, String>();//初始化一个用于指标存在性检验的Map
		Map<String,String> checkIdxsDupl = new HashMap<String, String>();//初始化一个用于指标重复性检验的Map
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();//初始化一个检验信息对象
		List<RptIdxSimilarGrp> grps = new ArrayList<RptIdxSimilarGrp>();//初始化一个同类组对象
		//通过缓存ID获取导入excel的数据对象
		List<SimilarImportVO> vos = (List<SimilarImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		//先去查询数据库里已有的指标编号//用于存在性检验
		List<Object[]> idxs = rptIdxSimiGrpBS.getIndexNoList();
		
		if(null != idxs && idxs.size() > 0){//将查询到的指标编号放入到map变量中
			for(Object[] idx : idxs){
				checkIdxsExsi.put(idx[0].toString(), idx[0].toString());
			}
		}
		//检验excel导入数据的正确性，同类组数据，每个同类组成员指标不可出现在其他同类组中
		if (vos != null && vos.size() > 0) {
			for (SimilarImportVO vo : vos) {
				try {
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				//初始化一个grpId 同类组编号,每行一个同类中编号
				String newGrpId = RandomUtils.uuid2();
				//循环excel的单行的每一列的指标
				Field[] fe = vo.getClass().getDeclaredFields();//获取vo实体的私有方法
				for(int i =0 ;i < fe.length ; i++){//遍历获取的私有方法，逐个执行
					if(!fe[i].getName().equals("excelRowNo")&&!fe[i].getName().equals("sheetName")){
						ExcelColumn ec = fe[i].getAnnotation(ExcelColumn.class);//获取方法注释
						int index=ExcelAnnotationUtil.getExcelCol(ec.index());//获取每个单元格的列号并转换成数字
						String indexNo = (String) ReflectionUtils.invokeGetter(vo, fe[i].getName());//获取每个单元格的值
						if(StringUtils.isNotBlank(indexNo)){
							//循环进行指标存在性检验，并记录检验日志
							if(null != indexNo && null == checkIdxsExsi.get(indexNo)){
								ValidErrorInfoObj obj=new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(index+1);
								obj.setValidTypeNm("存在性校验");
								obj.setErrorMsg("指标编号不存在！");
								errors.add(obj);
							}
							//循环进行指标重复性检验，并记录检验日志
							if(null != indexNo && null != checkIdxsDupl.get(indexNo)){
								ValidErrorInfoObj obj=new ValidErrorInfoObj();
								obj.setSheetName(vo.getSheetName());
								obj.setExcelRowNo(vo.getExcelRowNo());
								obj.setExcelColNo(index+1);
								obj.setValidTypeNm("重复性校验");
								obj.setErrorMsg("指标重复，一个指标只能存在于一个同类组！");
								errors.add(obj);
							}else{
								checkIdxsDupl.put(indexNo, indexNo);
							}
							//初始化一个同类组对象，解析excel的值到grp表里，与表字段对应，每个单元格就是一个grp对象
							RptIdxSimilarGrp grp = new RptIdxSimilarGrp();
							RptIdxSimilarGrpPK pk = new RptIdxSimilarGrpPK();
							pk.setSimigrpId(newGrpId);
							pk.setIndexNo(indexNo);
							grp.setId(pk);
							grps.add(grp);//放入到同类组list中
						}
					}
					
				}
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(grps);
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,lists);//把解析好的excel值放入到缓存
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
