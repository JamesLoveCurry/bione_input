package com.yusys.bione.plugin.valid.web;

import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaFunc;
import com.yusys.bione.plugin.rptidx.entity.RptIdxFormulaSymbol;
import com.yusys.bione.plugin.rptmgr.service.RptMgrBS;
import com.yusys.bione.plugin.valid.entitiy.RptValidCfgextLogic;
import com.yusys.bione.plugin.valid.service.ValidLogicBS;
import com.yusys.bione.plugin.valid.web.vo.CfgextLogicVO;
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
import java.util.*;
import java.util.Map.Entry;

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
@RequestMapping("/report/frame/valid/logic")
public class ValidLogicController extends BaseController {

	@Autowired
	private ValidLogicBS logicBs;

	@Autowired
	public RptMgrBS rptMgrBS;

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/plugin/valid/valid-cfg-index";
	}

	/**
	 * 跳转到某个报表下的逻辑校验grid
	 * 
	 * @param rptId
	 * @return
	 */
	@RequestMapping(value = "/tab", method = RequestMethod.GET)
	public ModelAndView logicTab(String templateId) {
		templateId = StringUtils2.javaScriptEncode(templateId);
		return new ModelAndView("/plugin/valid/valid-logic-index", "templateId", templateId);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public ModelAndView newEdit(String templateId, String checkId, String rptId, String isQuery, String busiType) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));
		map.put("rptId", StringUtils2.javaScriptEncode(rptId));
		map.put("isQuery", StringUtils2.javaScriptEncode(isQuery));
		map.put("busiType", StringUtils2.javaScriptEncode(busiType));
		return new ModelAndView("/plugin/valid/valid-cfgext-edit", map);
	}

	@RequestMapping(value = "/newLogic", method = RequestMethod.GET)
	public ModelAndView newLogic(String templateId, String checkId, String rptId,String orgType) {
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> dataMap = this.logicBs.getRptAndLine(templateId, rptId);
		for (Iterator<Entry<String, String>> it = dataMap.entrySet().iterator(); it.hasNext(); ) {
			Entry<String, String> entry = it.next();
			map.put(entry.getKey(), StringUtils2.javaScriptEncode(entry.getValue()));
		}
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));
		map.put("rptId", StringUtils2.javaScriptEncode(rptId));
		map.put("orgType", StringUtils2.javaScriptEncode(orgType));
		return new ModelAndView("/plugin/valid/valid-cfgext-logic-edit",
				map);
	}

	@RequestMapping(value = "/basicInfo", method = RequestMethod.GET)
	public ModelAndView basicInfo(String templateId, String checkId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));
		return new ModelAndView("/plugin/valid/valid-cfgext-basic-info",
				map);
	}

	@RequestMapping(value = "/newWarn", method = RequestMethod.GET)
	public ModelAndView newWarn(String templateId, String checkId) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("templateId", StringUtils2.javaScriptEncode(templateId));
		map.put("checkId", StringUtils2.javaScriptEncode(checkId));
		return new ModelAndView("/plugin/valid/valid-cfgext-warn-edit",
				map);
	}

	@RequestMapping(value = "/list.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> list(Pager pager, String templateId, String srcIdxNo, String dataDate, String busiType) {
		return this.logicBs.list(pager, templateId, srcIdxNo, dataDate, busiType);
	}

	@RequestMapping(value = "/{id}/{orgType}", method = RequestMethod.GET)
	@ResponseBody
	public CfgextLogicVO getInfo(@PathVariable("id") String checkId,@PathVariable("orgType") String orgType) {
		return this.logicBs.getInfo(checkId,orgType);
	}

	@RequestMapping(value = "/{ids}", method = RequestMethod.POST)
	public void delete(@PathVariable("ids") String checkIds, String templateId) {
		this.logicBs.delete(checkIds, templateId);
	}

	@RequestMapping(value = "/getFuncAll.*", method = RequestMethod.POST)
	@ResponseBody
	public List<RptIdxFormulaFunc> getFuncAll() {
		return this.logicBs.getFuncAll();
	}

	@RequestMapping(value = "/getFuncTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getFuncTree() {
		return this.logicBs.getFuncTree();
	}

	@RequestMapping(value = "/getSymbolAll.*", method = RequestMethod.POST)
	@ResponseBody
	public List<RptIdxFormulaSymbol> getSymbolAll() {
		return this.logicBs.getSymbolAll();
	}

	@RequestMapping(value = "/getSymbolTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getSymbolTree() {
		return this.logicBs.getSymbolTree();
	}

	@RequestMapping(method = RequestMethod.POST)
	public void save(RptValidCfgextLogic logic, String templateId, String rightFormulaDs,
			String leftFormulaIndex, String rightFormulaIndex, String leftFormulaDs, Boolean isInnerCheck,
			String busiType, String orgNo) {
		if(StringUtils.isNotEmpty(logic.getExpressionDesc()))
			logic.setExpressionDesc( logic.getExpressionDesc().replace('"', '\''));
		if(StringUtils.isNotEmpty(logic.getLeftExpression()))
			logic.setLeftExpression( logic.getLeftExpression().replace('"', '\''));
		if(StringUtils.isNotEmpty(logic.getRightExpression()))
			logic.setRightExpression( logic.getRightExpression().replace('"', '\''));
		leftFormulaIndex = leftFormulaIndex.replace('"', '\'');
		rightFormulaIndex = rightFormulaIndex.replace('"', '\'');
		if(isInnerCheck) {
			logic.setCheckType("01");//表内校验
		}else {
			logic.setCheckType("02");//表间校验
		}
		this.logicBs.saveLogic(logic, templateId, leftFormulaIndex,
				rightFormulaIndex, leftFormulaDs, rightFormulaDs, busiType, orgNo);
	}

	@RequestMapping(value = "/getData", method = RequestMethod.GET)
	public ModelAndView getData() {
		return new ModelAndView("/plugin/valid/design-logic-edit", "formulaDims", "org,date,");
	}
	
	/**
	 * 跳转批量出选择页面
	 * @return
	 */
	@RequestMapping(value = "/exportChoice", method = RequestMethod.GET)
	public ModelAndView exportChoice(String isCellNo) {
		ModelAndView modelAndView = new ModelAndView("/plugin/valid/valid-logic-export");
		modelAndView.addObject("isCellNo",isCellNo);
		return modelAndView;
	}

	@RequestMapping(value = "/getRptTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getRptTree(String searchNm) {
		// 展示行内报表树，展示invalid报表，展示树根节点
		List<String> templateTypeList = new ArrayList<String>();
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_CELL);
		templateTypeList.add(GlobalConstants4plugin.RPT_TMP_TYPE_COM);

		return this.rptMgrBS.getRptTreeSync(GlobalConstants4plugin.RPT_EXT_TYPE_FRS,
				GlobalConstants4plugin.RPT_TYPE_DESIGN, searchNm, true, true,
				templateTypeList, null, true, null,null);
	}

	@RequestMapping(value = "/getRptAndTemplateTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getRptAndTemplateTree(String searchNm) {
		// 展示行内报表树，展示invalid报表，展示树根节点
		List<String> template = new ArrayList<String>();
		template.add(GlobalConstants4plugin.RPT_TMP_TYPE_CELL);
		template.add(GlobalConstants4plugin.RPT_TMP_TYPE_COM);
		//202002 lcy 明细类报表逻辑校验配置
		template.add(GlobalConstants4plugin.RPT_TMP_TYPE_DETAIL);
		return this.rptMgrBS.getRptAndTemplateTree(
				GlobalConstants4plugin.RPT_EXT_TYPE_FRS,
				GlobalConstants4plugin.RPT_TYPE_DESIGN, true, template, searchNm);
	}

	@RequestMapping(value = "/replaceExpression", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> replaceExpression(boolean isInnerCheck,String leftExpression,
			String rightExpression, String expression) {
		// 展示行内报表树，展示invalid报表，展示树根节点
		Map<String, Object> map = this.logicBs.replaceExpression(isInnerCheck, leftExpression, rightExpression, expression);
		//这个地方检测来源指标是否是自己没有意义，先注掉
/*		if (map.get("message") == null && map.get("formulaIndex") != null
				&& !StringUtils.isEmpty(map.get("formulaIndex").toString())) {
			Map<String, Object> result = this.logicBs.checkCycleRef(map.get("formulaIndex").toString());
			if (result == null) {
				return map;
			}
			return result;
		}*/
		return map;
	}

	@RequestMapping(value = "/validExpression", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> validExpression(String expression,
			String expressionDesc) {
		return this.logicBs.validExpression(expression, expressionDesc);
	}

	@RequestMapping(value = "/syncExpressionDesc")
	public Map<String, Object> syncExpressionDesc(String rptId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		if (StringUtils.isNotBlank(rptId)) {
			returnMap = this.logicBs.syncExpressionDesc(rptId);
		}
		return returnMap;
	}
	
	@RequestMapping(value = "/chooseModel")
	public ModelAndView chooseModel(){
		return new ModelAndView("/plugin/valid/choose-data-model");
	}
	
	@RequestMapping(value = "/getModelInfo")
	@ResponseBody
	public Map<String, Object> getModelInfo(String setId){
		List<RptSysModuleCol> list =  this.logicBs.getModelInfo(setId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("Rows", list);
		map.put("Total", list.size());
		return map;
	}
	
	@RequestMapping(value = "/dimSelect")
	public ModelAndView dimSelect(String dimTypeNo){
		dimTypeNo = StringUtils2.javaScriptEncode(dimTypeNo);
		return new ModelAndView("/plugin/valid/choose-dim-item", "dimTypeNo", dimTypeNo);
	}
	
	@RequestMapping(value = "/dimItems")
	@ResponseBody
	public List<CommonTreeNode> getDimItems(String dimTypeNo){
		return this.logicBs.getDimItems(dimTypeNo);
	}
	
	@PostMapping("/validLogicRel/exp")
	@ResponseBody
	public Map<String,Object> getValidRel(String ids, String templateId, String endDate, 
			String realpath,HttpServletResponse response, String busiType, String isCellNo) throws UnsupportedEncodingException {
		Map<String,Object> resMap = new HashMap<String, Object>();
		
		File filepath = logicBs.expValidLogicRel(ids, templateId, null ,this.getRealPath(), endDate, busiType, isCellNo);
		if(filepath != null){
			resMap.put("filepath", URLEncoder.encode(filepath.getPath(),"UTF-8"));
		}
		return resMap;
	}
	
	/**
	 * 批量导出校验公式
	 * @param rptIds
	 * @param response
	 * @return
	 */
	@PostMapping("/validLogicRel/expAll")
	@ResponseBody
	public Map<String,Object> expAll(String rptIds, HttpServletResponse response, String busiTypes, String endDate, String isCellNo) throws UnsupportedEncodingException {
		Map<String,Object> resMap = new HashMap<String, Object>();
		List<String> fileNames = new ArrayList<String>();
		String rptArrs[] = StringUtils.split(rptIds, ",");
		if(rptArrs.length == 1){
			String rptInfos[] = StringUtils.split(rptArrs[0], ";");
			if(rptInfos.length == 2) {
				File filepath = logicBs.expValidLogicRel("all", rptInfos[0], rptInfos[1] + "(逻辑校验公式)", this.getRealPath(), endDate, busiTypes, isCellNo);
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
					File filepath = logicBs.expValidLogicRel("all", rptInfos[0], rptInfos[1] + "(逻辑校验公式)", this.getRealPath(), endDate, busiTypes, isCellNo);
					if (filepath != null) {
						fileNames.add(filepath.getPath());
					}
				}
			}
			String zipfileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator + "逻辑校验公式包.zip";//去工程路径下取模板的文件名]
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
	 * 下载文件
	 */
	@SuppressWarnings("deprecation")
	@RequestMapping("/validLogicRel/download")
	public void export(String fileName, String rptNm, HttpServletResponse response) {
		try {
			if(FilepathValidateUtils.validateFilepath(fileName)) {
				File file=new File(fileName);
				rptNm = URLDecoder.decode(rptNm);
				DownloadUtils.download(response, file, rptNm + "(逻辑校验公式).xls");
			
				file.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 导入校验tab窗口
	 * 
	 * @return
	 */
	@RequestMapping("/validLogicRel/impTabWin")
	public String importvalidLogicTab() {
		return "/plugin/valid/valid-logic-upload-tab";
	}
	/**
	 * 导入校验关系窗口
	 * 
	 * @return
	 */
	@RequestMapping("/validLogicRel/impWin")
	public String importvalidLogic() {
		return "/plugin/valid/valid-logic-fileup";
	}
	
	/**
	 * 导入校验关系上传窗口
	 * 
	 * @return
	 */
	@RequestMapping("/validLogicRel/uploadWin")
	public String uploadValidLogic() {
		return "/plugin/valid/valid-logic-upload";
	}
	
	/**
	 * 导入校验关系上传提示窗口
	 * 
	 * @return
	 */
	@RequestMapping("/validLogicRel/tipWin")
	public String importvalidLogicTip() {
		return "/plugin/valid/valid-logic-validate";
	}
	
	/**
	 * 进行上传时的文件接收与保存
	 * @param uploader
	 * @param dsId
	 * @param isNmImp 是否按照单元格名称进行导入（导入数据分俩种：按照报表指标编号导入、按照报表名称.单元格名称进行导入）
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/validLogicRel/imp")
	@ResponseBody
	public Map<String,Object> startUpload(Uploader uploader, String dsId, String isNmImp, HttpServletResponse response) throws Exception {
		File file = null;
		try {
			file = this.uploadFile(uploader, GlobalConstants4frame.LOGIC_SYS_IMPORT_PATH, false);
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
					UploadResult result = logicBs.impValidLogicRel(fileObj, ehcacheId);
					if(null != result.getInfo()) {
						resultInfo.addAll(result.getInfo());
					}
					fileObj.delete();
				}
				error.addAll(logicBs.easyImpValidLogic(dsId, ehcacheId, isNmImp));
				uploadResMap.put("ehcacheId", ehcacheId);
				uploadResMap.put("info", resultInfo);
				uploadResMap.put("fileName", file.getName());
				resMap.put("result", uploadResMap);
			}else {
				UploadResult result = logicBs.impValidLogicRel(file, ehcacheId);
				error = logicBs.easyImpValidLogic(dsId, result.getEhcacheId(), isNmImp);
				resMap.put("result", result);
			}
			resMap.put("error", error);
			file.delete();
			return resMap;
		}
		return resMap;
	}
	
	@RequestMapping("/validLogicRel/impSave")
	@ResponseBody
	public Map<String, Object> saveValidLogicRel(String ehcacheId,String dsId) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			logicBs.saveValidLogicRel(ehcacheId, dsId);
			map.put("isSuccess", true);
		} catch (Exception e) {
			map.put("isSuccess", false);
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * 根据机构类型获取机构层级
	 * @param orgType
	 * @return
	 */
	@RequestMapping(value = "/initOrgLvl")
	@ResponseBody
	public Map<String, Object> initOrgLvl(String orgType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.putAll(this.logicBs.initOrgLvl(orgType));
		return resultMap;
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
	@RequestMapping("/validLogicRel/downloadAll")
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
	 * 一键导出校验公式-条件过滤页面
	 * @return
	 */
	@RequestMapping(value = "/exportLogicValid")
	public ModelAndView exportFilterByParam() {
		return new ModelAndView("/plugin/valid/valid-logic-export-filter");
	}
}