package com.yusys.bione.plugin.valid.web;

import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.valid.service.ValidWarnBS;
import com.yusys.bione.plugin.valid.web.vo.CfgextWarnVO;
import com.yusys.bione.plugin.valid.web.vo.ValidCfgextWarnVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;
import com.yusys.bione.plugin.wizard.web.vo.UploadResultInfo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
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

/**
 * 
 * <pre>
 * Title:
 * Description: 
 * </pre>
 * 
 * @author fangjuan@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/report/frame/valid/warn")
public class ValidWarnController extends BaseController {

	@Autowired
	private ValidWarnBS warnBs;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(String templateId) {
		templateId = StringUtils2.javaScriptEncode(templateId);
		return new ModelAndView("/plugin/valid/valid-warn-index", "templateId", templateId);
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newEdit(String templateId, String cfgId, String checkId, String isQuery, String rptCycle) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("cfgId", StringUtils2.javaScriptEncode(cfgId));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));
		map.put("isQuery", StringUtils2.javaScriptEncode(isQuery));
		map.put("rptCycle", StringUtils2.javaScriptEncode(rptCycle));
		return new ModelAndView("/plugin/valid/valid-warn-edit", map);
	}
	
	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Pager pager, String templateId, String srcIdxNo, String dataDate){
		return this.warnBs.list(pager, templateId, srcIdxNo, dataDate);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public ValidCfgextWarnVO getInfo(@PathVariable("id") String checkId, String cfgId){
		return this.warnBs.getInfo(checkId, cfgId);
	}
	
	@RequestMapping(value = "/{ids}", method = RequestMethod.POST)
	@ResponseBody
	public void delete(@PathVariable("ids") String checkIds, String templateId){
		this.warnBs.delete(checkIds, templateId);
	}
	
	@RequestMapping(value = "/getLevelInfo.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getLevelInfo(String templateId, String checkId){
		return this.warnBs.getLevelInfo(templateId, checkId);
	}
	//保存
	@RequestMapping(value = "/saveData",method = RequestMethod.POST)
	public void save(ValidCfgextWarnVO warn){
		this.warnBs.saveWarn(warn, null);
	}
	@RequestMapping(value = "/showCellChoose", method = RequestMethod.GET)
	public ModelAndView showCellChoose(String templateId, String cfgId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("cfgId", StringUtils2.javaScriptEncode(cfgId));
		return new ModelAndView("/plugin/valid/valid-warn-cell", map);
	}
	
	@RequestMapping(value = "/validStartDate")
	@ResponseBody
	public String validStartDate(String cfgId){
		return this.warnBs.validStartDate(cfgId);
	}

	@ResponseBody
	@RequestMapping(value = "/validWarnRel/exp")
	public Map<String, Object> exp(String ids, String templateId, String endDate, String busiType){
		Map<String,Object> resMap = new HashMap<String, Object>();
		try {
			File filepath = warnBs.expValidWarnRel(ids, templateId, null ,this.getRealPath(), endDate, busiType);
			if(filepath != null){
				resMap.put("filepath", URLEncoder.encode(filepath.getPath(),"UTF-8"));
			}
		} catch (Exception e){
			e.printStackTrace();
		}
		return resMap;
	}

	@RequestMapping("/validWarnRel/download")
	public void download(String fileName, String rptNm, HttpServletResponse response){
		try {
			if(FilepathValidateUtils.validateFilepath(fileName)) {
				File file=new File(fileName);
				rptNm = URLDecoder.decode(rptNm);
				DownloadUtils.download(response, file, rptNm + "(预警校验公式).xls");

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
	@RequestMapping(value = "/exportChoice", method = RequestMethod.GET)
	public ModelAndView exportChoice() {
		return new ModelAndView("/plugin/valid/valid-warn-export");
	}

	/**
	 * 批量导出校验公式
	 * @param rptIds
	 * @param response
	 * @return
	 */
	@PostMapping("/validWarnRel/expAll")
	@ResponseBody
	public Map<String,Object> expAll(String rptIds, HttpServletResponse response, String busiTypes, String endDate) throws UnsupportedEncodingException {
		Map<String,Object> resMap = new HashMap<String, Object>();
		List<String> fileNames = new ArrayList<String>();
		String rptArrs[] = StringUtils.split(rptIds, ",");
		if(rptArrs.length == 1){
			String rptInfos[] = StringUtils.split(rptArrs[0], ";");
			if(rptInfos.length == 2) {
				File filepath = warnBs.expValidWarnRel("all", rptInfos[0], rptInfos[1] + "(预警校验公式)", this.getRealPath(), endDate, busiTypes);
				if (filepath != null) {
					resMap.put("filePath", URLEncoder.encode(filepath.getPath(),"UTF-8"));
				}
				return resMap;
			}
		}
		else{
			for(int i=0 ; i < rptArrs.length; i ++){
				String rptInfos[] = StringUtils.split(rptArrs[i], ";");
				if(rptInfos.length == 2) {
					File filepath = warnBs.expValidWarnRel("all", rptInfos[0], rptInfos[1] + "(预警校验公式)", this.getRealPath(), endDate, busiTypes);
					if (filepath != null) {
						fileNames.add(filepath.getPath());
					}
				}
			}
			String zipfileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator + "预警校验公式包.zip";//去工程路径下取模板的文件名]
			try{
				createZipFile(zipfileName, fileNames);//创建压缩包并删除源文件
			} catch (IOException e) {
				zipfileName = "";
				e.printStackTrace();
			}
			resMap.put("filePath", URLEncoder.encode(zipfileName,"UTF-8"));
			return resMap;
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
			} catch (IOException e) {
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
	@RequestMapping("/validWarnRel/downloadAll")
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

	/**
	 * 导入校验tab窗口
	 *
	 * @return
	 */
	@RequestMapping("/validWarnRel/impTabWin")
	public String importvalidWarnTab() {
		return "/plugin/valid/valid-warn-upload-tab";
	}

	/**
	 * 导入校验关系窗口
	 *
	 * @return
	 */
	@RequestMapping("/validWarnRel/impWin")
	public String importValidWarn() {
		return "/plugin/valid/valid-warn-fileup";
	}

	/**
	 * 导入校验关系上传窗口
	 *
	 * @return
	 */
	@RequestMapping("/validWarnRel/uploadWin")
	public String uploadValidWarn() {
		return "/plugin/valid/valid-warn-upload";
	}

	/**
	 * 进行上传时的文件接收与保存
	 * @param uploader
	 * @param dsId
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/validWarnRel/imp")
	public Map<String,Object> startUpload(Uploader uploader, String dsId, HttpServletResponse response) throws Exception {
		File file = null;
		try {
			file = this.uploadFile(uploader, GlobalConstants4frame.WARN_SYS_IMPORT_PATH, false);
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
					UploadResult result = warnBs.impValidWarnRel(fileObj, ehcacheId);
					if(null != result.getInfo()) {
						resultInfo.addAll(result.getInfo());
					}
					fileObj.delete();
				}
				error.addAll(warnBs.easyImpValidWarn(dsId, ehcacheId));
				uploadResMap.put("ehcacheId", ehcacheId);
				uploadResMap.put("info", resultInfo);
				uploadResMap.put("fileName", file.getName());
				resMap.put("result", uploadResMap);
			}else {
				UploadResult result = warnBs.impValidWarnRel(file, ehcacheId);
				error = warnBs.easyImpValidWarn(dsId, result.getEhcacheId());
				resMap.put("result", result);
			}
			resMap.put("error", error);
			file.delete();
			return resMap;
		}
		return resMap;
	}

	/**
	 * 导入校验关系上传提示窗口
	 *
	 * @return
	 */
	@RequestMapping("/validWarnRel/tipWin")
	public String importvalidWarnTip() {
		return "/plugin/valid/valid-warn-validate";
	}

	@ResponseBody
	@RequestMapping("/validWarnRel/impSave")
	public Map<String, Object> saveValidWarnRel(String ehcacheId,String dsId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
			List<String> checkIdList = (List<String>) lists.get(0);//校验公式编号list
			List<CfgextWarnVO> cfgextLogic = (List<CfgextWarnVO>) lists.get(1);//校验公式导入VO list
			//删除旧校验公式
			delete(StringUtils.join(checkIdList,","),"");

			warnBs.saveValidWarnRel(cfgextLogic);
			map.put("isSuccess", true);
		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}
		return map;
	}

}