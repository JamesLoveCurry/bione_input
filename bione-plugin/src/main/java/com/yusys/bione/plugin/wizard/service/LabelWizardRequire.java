package com.yusys.bione.plugin.wizard.service;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
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
import com.yusys.bione.frame.label.entity.BioneLabelInfo;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.frame.validator.common.Validator;
import com.yusys.bione.frame.validator.exception.ValidateException;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;

/**
 * <pre>
 * Title:用户信息的导入导出
 * Description: 提供用户信息上传/用户信息及用户角色关系信息的导出/用户信息保存/上传用户信息的检验等功能
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
public class LabelWizardRequire extends BaseController implements
		IWizardRequire {
	private UserBS userBS = SpringContextHolder.getBean("userBS");
	@Autowired
	private ExcelBS excelBS;
	
	/**
	 * 标签信息上传
	 * @param file 文件对象
	 * @return 上传文件结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(BioneLabelInfo.class, file);
		UploadResult result = new UploadResult();
		try {
			List<BioneLabelInfo> vos = (List<BioneLabelInfo>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					vos);
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos != null && vos.size() > 0) {
				for (BioneLabelInfo vo : vos) {
					result.setInfo(vo.getLabelName(), vo.getLabelName());
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
	 * 
	 */
	@Override
	public String export(String ids) {
		String fileName = "";
		Map<String, String> labMap = Maps.newHashMap();
		List<BioneLabelInfo> labs = this.userBS.getEntityList(BioneLabelInfo.class);
		if(null != labs && labs.size() > 0){
			for(BioneLabelInfo lab : labs){
				labMap.put(lab.getLabelId(), lab.getLabelName());
			}
			for(BioneLabelInfo lab : labs){
				if("0".equals(lab.getUpId())){
					lab.setUpId("");
				}else{
					lab.setUpId(labMap.get(lab.getUpId()));
				}
			}
		}
		
		List<List<?>> list = new ArrayList<List<?>>();
		list.add(labs);//放入第一个sheet页
		XlsExcelTemplateExporter fe = null;//初始化一个导出模板数据的对象
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";//去工程路径下取模板的文件名
			//定义一个导出模板数据的对象，传入文件名，模板路径，要导出的实体对象list
			fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
					+ GlobalConstants4plugin.EXPORT_LABEL_TEMPLATE_PATH, list);
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
	 * 标签信息保存
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<BioneLabelInfo> labels = (List<BioneLabelInfo>) lists.get(0);//用户信息
		List<String> labFields = new ArrayList<String>();
		labFields.add("labelId");
		this.excelBS.deleteEntityJdbcBatch(labels, labFields);
		this.excelBS.saveEntityJdbcBatch(labels);
	}
	/**
	 * 上传标签信息的检验
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 * @return 检验信息对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		List<BioneLabelInfo> labels = new ArrayList<BioneLabelInfo>();
		List<BioneLabelInfo> vos = (List<BioneLabelInfo>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		Map<String,String> inDataMap = getCheckObjMap(BioneLabelInfo.class,"label","labelId","labelName","labelObjId");
		Map<String,String> inExclMap = Maps.newHashMap();
		
		if (vos != null && vos.size() > 0) {
			for (BioneLabelInfo vo : vos) {
				try {//其余校验
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				
				String uuidLabelId = "";
				//校验excel内部标签是否重复
				if(null != inExclMap.get(vo.getLabelName() + vo.getLabelObjId())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("唯一性校验");
					obj.setErrorMsg("导入模板中标签存在重复");
					errors.add(obj);
				}else{//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					if(null != inDataMap.get(vo.getLabelName() + vo.getLabelObjId())){
						uuidLabelId = inDataMap.get(vo.getLabelName() + vo.getLabelObjId());
					}else{
						uuidLabelId = RandomUtils.uuid2();//如果数据库没有就自生成id
					}
					inExclMap.put(vo.getLabelName() + vo.getLabelObjId(), uuidLabelId);
				}
			}
			//再循环一次,校验上级标签是否存在于数据库或excel中
			inDataMap.putAll(inExclMap);//把excel里的和数据里的标签合并
			for (BioneLabelInfo vo : vos) {
				if(StringUtils.isNotBlank(vo.getUpId()) && null == inDataMap.get(vo.getUpId() + vo.getLabelObjId())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(2);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("上级标签不存在或标签类型不符合");
					errors.add(obj);
				}
				BioneLabelInfo lab = new BioneLabelInfo();
				lab.setLabelId(inDataMap.get(vo.getLabelName() + vo.getLabelObjId()));
				lab.setLabelName(vo.getLabelName());
				lab.setUpId(StringUtils.isNotBlank(vo.getUpId()) ? inDataMap.get(vo.getUpId() + vo.getLabelObjId()) : "0");//vo.getUpId()传入名称，不做转换
				lab.setLabelObjId(vo.getLabelObjId());
				lab.setRemark(vo.getRemark());
				labels.add(lab);
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(labels);//用户信息
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,lists);
		return errors;
	}
	
	private Map<String,String> getCheckObjMap(Class<?> cla,String type,String method1,String method2,String method3){
		Map<String,String> param = new HashMap<String, String>();
		List<?> objs = userBS.getEntityList(cla);
		if(null != objs && objs.size() > 0){
			for(Object obj : objs){
				String lbId = (String) ReflectionUtils.invokeGetter(obj, method1);//标签ID
				String lbNm = (String) ReflectionUtils.invokeGetter(obj, method2);//标签名称
				String lbty = (String) ReflectionUtils.invokeGetter(obj, method3);//标签类型
				param.put(lbNm+lbty, lbId);
			}
		}
		return param;
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
