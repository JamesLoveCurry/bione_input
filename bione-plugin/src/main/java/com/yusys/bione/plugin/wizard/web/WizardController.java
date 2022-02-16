package com.yusys.bione.plugin.wizard.web;

import com.alibaba.fastjson.JSON;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.wizard.service.IWizardRequire;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.*;

@Controller
@RequestMapping("/report/frame/wizard")
public class WizardController extends BaseController {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private static String UPLOAD_ATTACH_DIR = GlobalConstants4frame.IMPORT_PATH
			+ "/model";
	/**
	 * 点击导入，弹出导入弹框
	 * @param type 导入类型
	 * @return mv
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String type) {
		type = StringUtils2.javaScriptEncode(type);
		return new ModelAndView("/plugin/wizard/wizard-index", "type", type);
	}
	
	@RequestMapping(value="/exportWin", method = RequestMethod.GET)
	public ModelAndView exportWin(String type) {
		type = StringUtils2.javaScriptEncode(type);
		return new ModelAndView("/plugin/wizard/wizard-index-export", "type", type);
	}
	
	@RequestMapping(value="/source",method = RequestMethod.GET)
	public String source() {
		return "/plugin/wizard/wizard-index-source";
	}
	/**
	 * 点击上传文件获得的弹框
	 * @param type 导入类型
	 * @return mv
	 */
	@RequestMapping(value="/fileup",method = RequestMethod.GET)
	public ModelAndView fileup(String type) {
		type = StringUtils2.javaScriptEncode(type);
		return new ModelAndView("/plugin/wizard/wizard-index-fileup", "type", type);
	}
	/**
	 * 点击开始上传后进入到检验结果查询iframe弹框
	 * @param type 导入类型
	 * @return mv
	 */
	@RequestMapping(value="/validate",method = RequestMethod.GET)
	public ModelAndView validate(String type) {
		type = StringUtils2.javaScriptEncode(type);
		ModelAndView modelAndView = new ModelAndView("/plugin/wizard/wizard-index-validate", "type", type);
		if(StringUtils.equals(type, "PbcIJFile")) {
			modelAndView = new ModelAndView("/frs/pbmessage/pbmessage-impij-validate", "type", type);
		}
		return modelAndView;
	}
	
	@RequestMapping(value="/uploadFile",method = RequestMethod.GET)
	public ModelAndView uploadFile(String type) {
		type = StringUtils2.javaScriptEncode(type);
		return new ModelAndView("/plugin/wizard/wizard-index-upload", "type", type);
	}
	
	/**
	 * 导入需求Excel
	 * @param uploader 上传对象
	 * @param response 响应对象
	 * @param dsId 数据源ID
	 * @param type 导入类型
	 * @return
	 */
	@RequestMapping("/upload")
	@ResponseBody
	public String upload(Uploader uploader, HttpServletResponse response,String dsId,String type)
			throws Exception {
		File file = null;
		try {
			file = this.uploadFile(
					uploader,
					UPLOAD_ATTACH_DIR
							+ File.separatorChar
							+ FormatUtils.format(System.currentTimeMillis(),
									"yyyyMMdd") + File.separatorChar
							+ File.separatorChar, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		if (file != null) {
			logger.info("文件[" + file.getName() + "]上传完成");
			type=type.substring(0, 1).toLowerCase()+type.substring(1, type.length());
			Map<String,Object> map=new HashMap<String, Object>();
			List<ValidErrorInfoObj> error = new ArrayList<ValidErrorInfoObj>();
			if(type.equals("indexAll")){
				IWizardRequire require= SpringContextHolder.getBean("indexWizardRequire");
				UploadResult result=require.upload(file);
				error=require.validateVerInfo(dsId,result.getEhcacheId());
				
				map.put("result",result);
			}
			else{
				IWizardRequire require= SpringContextHolder.getBean(type+"WizardRequire");
				UploadResult result=require.upload(file);
				error=require.validateInfo(dsId,result.getEhcacheId());
				map.put("result",result);
			}
			
			Collections.sort(error, new Comparator<ValidErrorInfoObj>() {
				@Override
				public int compare(ValidErrorInfoObj o1, ValidErrorInfoObj o2) {
					// TODO Auto-generated method stub
					if(o1.getSheetName().compareTo(o2.getSheetName())>0){
						return 1;
					}
					else if(o1.getSheetName().compareTo(o2.getSheetName())==0){
						if(o1.getExcelRowNo().compareTo(o2.getExcelRowNo())>0){
							return 1;
						}
						else if(o1.getExcelRowNo().compareTo(o2.getExcelRowNo())==0){
							if(o1.getExcelColNo().compareTo(o2.getExcelColNo())>0){
								return 1;
							}
							else if(o1.getExcelColNo().compareTo(o2.getExcelColNo())==0){
								return 0;
							}
							else{
								return -1;
							}
						}
						else{
							return -1;
						}
					}
					else{
						return -1;
					}
				}
			});
			map.put("error",error);
			file.delete();
			return JSON.toJSONString(map);
		}
		return "";
	}
	
	/**
	 * 
	 * @Title: frsUpload
	 * @Description: TODO(监管平台指标导入方法新增)
	 * @param uploader 上传对象
	 * @param response 响应对象
	 * @param dsId 数据源ID
	 * @param type 导入类型
	 * @return
	 * @throws Exception Map<String,Object>  
	 * @throws
	 */
	@RequestMapping("/frsUpload")
	@ResponseBody
	public Map<String,Object> frsUpload(Uploader uploader, HttpServletResponse response,String dsId,String type)
			throws Exception {
		File file = null;
		try {
			file = this.uploadFile(
					uploader,
					UPLOAD_ATTACH_DIR
							+ File.separatorChar
							+ FormatUtils.format(System.currentTimeMillis(),
									"yyyyMMdd") + File.separatorChar
							+ File.separatorChar, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		if (file != null) {
			logger.info("文件[" + file.getName() + "]上传完成");
			type=type.substring(0, 1).toLowerCase()+type.substring(1, type.length());
			Map<String,Object> map=new HashMap<String, Object>();
			List<ValidErrorInfoObj> error = new ArrayList<ValidErrorInfoObj>();
			if(type.equals("indexAll")){
				IWizardRequire require= SpringContextHolder.getBean("indexWizardRequire");
				UploadResult result=require.upload(file);
				error=require.validateVerInfo(dsId,result.getEhcacheId());
				
				map.put("result",result);
			}
			else{
				IWizardRequire require= SpringContextHolder.getBean(type+"WizardRequire");
				UploadResult result=require.upload(file);
				error=require.validateInfo(dsId,result.getEhcacheId());
				map.put("result",result);
			}
			
			Collections.sort(error, new Comparator<ValidErrorInfoObj>() {
				@Override
				public int compare(ValidErrorInfoObj o1, ValidErrorInfoObj o2) {
					// TODO Auto-generated method stub
					if(o1.getSheetName().compareTo(o2.getSheetName())>0){
						return 1;
					}
					else if(o1.getSheetName().compareTo(o2.getSheetName())==0){
						if(o1.getExcelRowNo()!=null && o2.getExcelRowNo()!=null) {//校验明细模型至少设置一个主键时，错误列表是不存行数的，为防止空指针异常，要先判断是否为空值
							if (o1.getExcelRowNo().compareTo(o2.getExcelRowNo()) > 0) {
								return 1;
							} else if (o1.getExcelRowNo().compareTo(o2.getExcelRowNo()) == 0) {
								if (o1.getExcelColNo().compareTo(o2.getExcelColNo()) > 0) {
									return 1;
								} else if (o1.getExcelColNo().compareTo(o2.getExcelColNo()) == 0) {
									return 0;
								} else {
									return -1;
								}
							} else {
								return -1;
							}
						}else{
							if (o1.getExcelColNo().compareTo(o2.getExcelColNo())>0){
								return 1;
							}else if(o1.getExcelColNo().compareTo(o2.getExcelColNo())==0){
								return 0;
							}else {
								return -1;
							}
						}
					}
					else{
						return -1;
					}
				}
			});
			map.put("error",error);
			file.delete();
			return map;
		}
		return null;
	}
	
	/**
	 * 保存导入用户
	 * @param type 导入类型
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 * @return 保存状态
	 */
	@RequestMapping("/save")
	@ResponseBody
	public Map<String,Object> save(String type,String ehcacheId,String dsId){
		Map<String,Object> map=new HashMap<String, Object>();
		IWizardRequire require;
		try {
			type=type.substring(0, 1).toLowerCase()+type.substring(1, type.length());
			require = SpringContextHolder.getBean(type+"WizardRequire");
			require.saveData(ehcacheId,dsId);
			map.put("isSuccess", true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map.put("isSuccess", false);
			e.printStackTrace();
		}
		return map;
	}
	/**
	 * 导出公共方法
	 * @param ids 导出记录Id
	 * @param type 导出类型
	 * @param response 
	 * @return 返回文件名称
	 */
	@RequestMapping("/download")
	@ResponseBody
	public Map<String,Object> download(String ids,String type,HttpServletResponse response){
		Map<String,Object> map=new HashMap<String, Object>();//初始化一个用于接收导出方法返回参数的map变量
		IWizardRequire require;//初始化一个通用接口
		if(type.equals("IndexAll")){
			require = SpringContextHolder.getBean("indexWizardRequire");//通过spring机制获取每种导出实现了通用接口的*+WizardRequire对象
			String fileName=require.exportAll(ids);//调用每种导出的export重写方法去实现每种类型的导出的各自导出逻辑
			map.put("fileName", fileName);//把执行了excel导出后的返回参数放入map并返回页面
		}else{
			type=type.substring(0, 1).toLowerCase()+type.substring(1, type.length());//转化传入参数（导入类型）的首字母为小写
			require = SpringContextHolder.getBean(type+"WizardRequire");//通过spring机制获取每种导出实现了通用接口的*+WizardRequire对象
			String fileName=require.export(ids);//调用每种导出的export重写方法去实现每种类型的导出的各自导出逻辑
			map.put("fileName", fileName);//把执行了excel导出后的返回参数放入map并返回页面
		}
		return map;
	}
	/**
	 * 下载文件
	 * @param ids 导出记录Id
	 * @param type 导出类型
	 * @param response 
	 * @return 返回文件名称
	 */
	@RequestMapping("/export")
	public void export(String fileName,String type,HttpServletResponse response) {
		if (FilepathValidateUtils.validateFilepath(fileName)) {
			try {
				File file=new File(fileName);
				if(type.equals("IndexAll")){
					DownloadUtils.download(response, file,type+"info.zip");
					file.getParentFile().delete();
				}
				else{
					DownloadUtils.download(response, file,type+"info.xls");
				}
			
				file.delete();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
