package com.yusys.bione.plugin.valid.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.FilesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextOrgmerge;
import com.yusys.bione.plugin.valid.service.ValidOrgMergeBS;
import com.yusys.bione.plugin.valid.web.vo.CfgextOrgMergeVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import com.yusys.bione.plugin.wizard.web.vo.UploadResultInfo;

/**
 * <pre>
 * Title: 1104总分校验
 * Description:
 * </pre>
 * @author  maojin2
 * @version 1.00.00
   @date 2021年8月17日
 */
@Controller
@RequestMapping("/report/frame/valid/orgmerge")
public class ValidOrgMergeController extends BaseController {

	@Autowired
	private ValidOrgMergeBS validOrgMergeBS;
	
	
	/**
	 * 首页
	 * @param templateId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String templateId) {
		templateId = StringUtils2.javaScriptEncode(templateId);
		return new ModelAndView("/plugin/valid/orgmerge/valid-orgmerge-index", "templateId", templateId);
	}
	/**
	 * 查询列表
	 * @param pager
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Pager pager, String templateId){
		return this.validOrgMergeBS.list(pager, templateId);
	}
	
	/**
	 * 新增编辑页面
	 * @param templateId
	 * @param checkId
	 * @param isQuery
	 * @return
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newEdit(String templateId, String checkId, String isQuery, String startDate) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));
		map.put("isQuery", StringUtils2.javaScriptEncode(isQuery));
		map.put("startDate", StringUtils2.javaScriptEncode(startDate));
		return new ModelAndView("/plugin/valid/orgmerge/valid-orgmerge-edit", map);
	}
	
	/**
	 * 修改时获取反显数据
	 * @param checkId
	 * @return
	 */
	@RequestMapping(value = "/getFormData", method = RequestMethod.GET)
	@ResponseBody
	public CfgextOrgMergeVO getFormData(String checkId){
		return this.validOrgMergeBS.getFormData(checkId);
	}
	
	/**
	 * 删除
	 * @param ids
	 * @param templateId
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	@ResponseBody
	public void delete(String ids){
		this.validOrgMergeBS.delete(ids);
	}

	/**
	 * 保存
	 * @param rptValidCfgextOrgmerge
	 */
	@RequestMapping(value = "/saveData",method = RequestMethod.POST)
	public void saveData(RptValidCfgextOrgmerge rptValidCfgextOrgmerge){
		this.validOrgMergeBS.saveData(rptValidCfgextOrgmerge);
	}
	
	/**
	 * 选择分行报表
	 * @return
	 */
	@RequestMapping("/chooseRpt")
	public ModelAndView chooseRpt(String busiType) {
		Map<String, String> params = Maps.newHashMap();
		params.put("busiType", busiType);
		return new ModelAndView("/plugin/valid/orgmerge/valid-orgmerge-chooserpt", params);
	}
	
	/**
	 * 选择指标
	 * @param templateId
	 * @return
	 */
	@RequestMapping(value = "/chooseIndex", method = RequestMethod.GET)
	public ModelAndView chooseIndex(String templateId, String type) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("type", StringUtils2.javaScriptEncode(type));
		return new ModelAndView("/plugin/valid/orgmerge/valid-orgmerge-chooseIndex", map);
	}
	
	/**
	 * 导入校验公式tab窗口
	 * @return
	 */
	@RequestMapping("/impTabWin")
	public String impTabWin() {
		return "/plugin/valid/orgmerge/valid-orgmerge-upload-tab";
	}
	
	/**
	 * 文件选择页面
	 *
	 * @return
	 */
	@RequestMapping("/impWin")
	public String impWin() {
		return "/plugin/valid/orgmerge/valid-orgmerge-fileup";
	}
	
	/**
	 * 导入报表展示
	 *
	 * @return
	 */
	@RequestMapping("/uploadWin")
	public String uploadWin() {
		return "/plugin/valid/orgmerge/valid-orgmerge-upload";
	}
	
	/**
	 * 校验结果展示
	 * @return
	 */
	@RequestMapping("/tipWin")
	public String tipWin() {
		return "/plugin/valid/orgmerge/valid-orgmerge-validate";
	}
	
	/**
	 * 解析文件
	 * @param uploader
	 * @param dsId
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/imp")
	public Map<String,Object> startUpload(Uploader uploader, HttpServletResponse response) throws Exception {
		File file = null;
		try {
			file = this.uploadFile(uploader, GlobalConstants4frame.ORGMERGE_SYS_IMPORT_PATH, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		response.setContentType("text/plain; charset=UTF-8");
		Map<String,Object> resMap = new HashMap<String, Object>();
		if (file != null) {
			List<ValidErrorInfoObj> error = new ArrayList<ValidErrorInfoObj>();
			String ehcacheId = RandomUtils.uuid2();
			if (file.getName().endsWith(".zip")) {
				File targetDir = new File(file.getParentFile(), RandomUtils.uuid2());
				targetDir.mkdir();
				Map<String,Object> uploadResMap = new HashMap<String, Object>();
				List<File> fileList = FilesUtils.unzip(file, targetDir.getAbsolutePath());
				List<UploadResultInfo> resultInfo = new ArrayList<UploadResultInfo>();
				for(File fileObj : fileList) {
					UploadResult result = validOrgMergeBS.impValidOrgMerge(fileObj, ehcacheId);
					if(null != result.getInfo()) {
						resultInfo.addAll(result.getInfo());
					}
					fileObj.delete();
				}
				error.addAll(validOrgMergeBS.easyImpValidOrgMerge(ehcacheId));
				uploadResMap.put("ehcacheId", ehcacheId);
				uploadResMap.put("info", resultInfo);
				uploadResMap.put("fileName", file.getName());
				resMap.put("result", uploadResMap);
			}else {
				UploadResult result = validOrgMergeBS.impValidOrgMerge(file, ehcacheId);
				error = validOrgMergeBS.easyImpValidOrgMerge(result.getEhcacheId());
				resMap.put("result", result);
			}
			resMap.put("error", error);
			file.delete();
			return resMap;
		}
		return resMap;
	}
	
	/**
	 * 保存导入的数据
	 * @param ehcacheId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping("/impSave")
	public Map<String, Object> impSave(String ehcacheId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
			List<String> checkIdList = (List<String>) lists.get(0);//校验公式编号list
			List<CfgextOrgMergeVO> cfgextOrgMergeVOs = (List<CfgextOrgMergeVO>) lists.get(1);//校验公式导入VO list
			//删除旧校验公式
			this.validOrgMergeBS.delete(StringUtils.join(checkIdList,","));

			this.validOrgMergeBS.saveValidOrgMerge(cfgextOrgMergeVOs);
			map.put("isSuccess", true);
		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}
		return map;
	}
	
	/**
	 * 导出当前报表数据
	 * @param ids
	 * @param templateId
	 * @param endDate
	 * @param sumRptNm
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/exp")
	public Map<String, Object> exp(String ids, String templateId, String endDate, String sumRptNm){
		Map<String,Object> resMap = new HashMap<String, Object>();
		try {
			File filepath = validOrgMergeBS.expValidOrgMerge(ids, templateId, this.getRealPath(), endDate, sumRptNm);
			if(filepath != null){
				resMap.put("filepath", URLEncoder.encode(filepath.getPath(),"UTF-8"));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return resMap;
	}
	
	/**
	 * 导出下载
	 * @param fileName
	 * @param rptNm
	 * @param response
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping("/download")
	public void download(String fileName, String sumRptNm, HttpServletResponse response){
		try {
			if(FilepathValidateUtils.validateFilepath(fileName)) {
				File file=new File(fileName);
				sumRptNm = URLDecoder.decode(sumRptNm);
				DownloadUtils.download(response, file, sumRptNm + "(总分校验公式).xls");
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 跳转批量出选择页面
	 * @return
	 */
	@RequestMapping(value = "/exportBatch", method = RequestMethod.GET)
	public ModelAndView exportBatch() {
		return new ModelAndView("/plugin/valid/orgmerge/valid-orgmerge-export");
	}

	/**
	 * 批量导出校验公式
	 * @param rptIds
	 * @param response
	 * @return
	 */
	@PostMapping("/expAll")
	@ResponseBody
	public Map<String,Object> expAll(String rptIds, HttpServletResponse response, String endDate) throws UnsupportedEncodingException {
		Map<String,Object> resMap = new HashMap<String, Object>();
		List<String> fileNames = new ArrayList<String>();
		String rptArrs[] = StringUtils.split(rptIds, ",");
		if(rptArrs.length == 1){
			String rptInfos[] = StringUtils.split(rptArrs[0], ";");
			if(rptInfos.length == 2) {
				File filepath = validOrgMergeBS.expValidOrgMerge("all", rptInfos[0], this.getRealPath(), endDate, rptInfos[1] + "(总分校验公式)");
				if (filepath != null) {
					resMap.put("filePath", URLEncoder.encode(filepath.getPath(),"UTF-8"));
				}
				return resMap;
			}
		} else {
			for(int i=0 ; i < rptArrs.length; i ++){
				String rptInfos[] = StringUtils.split(rptArrs[i], ";");
				if(rptInfos.length == 2) {
					File filepath = validOrgMergeBS.expValidOrgMerge("all", rptInfos[0],  this.getRealPath(), endDate, rptInfos[1] + "(总分校验公式)");
					if (filepath != null) {
						fileNames.add(filepath.getPath());
					}
				}
			}
			if(fileNames.size() > 0){
				String zipfileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator + "总分校验公式包.zip";//去工程路径下取模板的文件名]
				try{
					createZipFile(zipfileName, fileNames);//创建压缩包并删除源文件
				} catch (IOException e) {
					zipfileName = "";
					e.printStackTrace();
				}
				resMap.put("filePath", URLEncoder.encode(zipfileName,"UTF-8"));
				return resMap;
			}else{
				resMap.put("filePath", "");
				return resMap;
			}
		}
		return resMap;
	}

	/**
	 * 压缩文件
	 *
	 * @param zipFilePath
	 *            压缩文件路径
	 * @param filePathList
	 *            被压缩文件路径LIST
	 * @return
	 * @throws IOException
	 */
	private File createZipFile(String zipFilePath, List<String> filePathList)
			throws IOException {
		if(FilepathValidateUtils.validateFilepath(zipFilePath)) {
			File zipFile = new File(zipFilePath);
			ZipOutputStream zout = null;
			try {
				zout = new ZipOutputStream(new FileOutputStream(zipFilePath));
				for (String filePath : filePathList) {
					if(FilepathValidateUtils.validateFilepath(filePath)) {
						File inputFile = new File(filePath);
						FilesUtils.zip(zout, inputFile, inputFile.getName(), zipFile);
						inputFile.delete();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(zout);
			}
			return zipFile;
		}
		return null;
	}

	/**
	 * 下载批量文件
	 */
	@RequestMapping("/downloadAll")
	public void downloadAll(String filePath, HttpServletResponse response) {
		if(FilepathValidateUtils.validateFilepath(filePath)) {
			File file = new File(filePath);
			try {
				DownloadUtils.download(response, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			file.delete();
		}
	}
}