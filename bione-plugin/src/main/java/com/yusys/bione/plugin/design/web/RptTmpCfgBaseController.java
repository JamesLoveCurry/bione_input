package com.yusys.bione.plugin.design.web;

import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.design.entity.RptDesignTmpInfo;
import com.yusys.bione.plugin.design.service.RptTmpBS;
import com.yusys.bione.plugin.design.web.vo.*;
import com.yusys.bione.plugin.regulation.service.RptModeImportInfoBS;
import com.yusys.bione.plugin.regulation.service.RptModelImportBS;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportCatalog;
import com.yusys.bione.plugin.spreadjs.service.SpreadWriterFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public abstract class RptTmpCfgBaseController extends BaseController {

	protected static String UPLOAD_ATTACH_DIR = GlobalConstants4frame.IMPORT_PATH
			+ "/rpt/require";

	@Autowired
	protected RptTmpBS rptTmpBS;

	@Autowired
	private RptModelImportBS rptModelImportBS;

	@Autowired
	RptModeImportInfoBS rptModeImportInfoBS;
	
	protected void download(HttpServletResponse response, String filepath)
			throws IOException {
		if(FilepathValidateUtils.validateFilepath(filepath)) {
			File file = new File(filepath);
			DownloadUtils.download(response, file);
			file.delete();
		}
	}

	protected Map<String, Object> exportDesignInfos(String isEmptyIdx, String rptId, String verId, String verNm) throws UnsupportedEncodingException {
		Map<String, Object> res = new HashMap<String, Object>();
		String[] rptArrs = StringUtils.split(rptId, ",");
		List<String> fileNames = new ArrayList<String>();
		List<RptMgrReportCatalog> catalogList = this.rptTmpBS.getCatalogByParam(null, null, null, GlobalConstants4plugin.RPT_TYPE_DESIGN, null);
		Map<String, RptMgrReportCatalog> catalogMap = new HashMap<String, RptMgrReportCatalog>();
		Map<String, String> catalogUrlMap = new HashMap<String, String>();
		if((null != catalogList) && (catalogList.size() > 0)) {
			for(RptMgrReportCatalog catalog : catalogList) {
				catalogMap.put(catalog.getCatalogId(), catalog);
			}
		}
		if(rptArrs.length == 1){
			String fileName = this.exportInfo(isEmptyIdx, rptId, verId, verNm, catalogMap, catalogUrlMap);
			if (StringUtils.isNotEmpty(fileName)) {
				res.put("fileName", URLEncoder.encode(fileName,"UTF-8"));
			}
			return res;
		}
		else{
			for(int i=0 ; i < rptArrs.length; i ++){
				String fileName = this.exportInfo(isEmptyIdx, rptArrs[i], verId, verNm, catalogMap, catalogUrlMap);
				if(StringUtils.isNotEmpty(fileName)){
					fileNames.add(fileName);
				}
			}
			String zipfileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + "(" + verNm + ")" +"制度包.zip";//去工程路径下取模板的文件名]
			try{
				createZipFile(zipfileName, fileNames);//创建压缩包并删除源文件
			} catch (IOException e) {
				zipfileName = "";
				e.printStackTrace();
			}
			res.put("fileName", URLEncoder.encode(zipfileName,"UTF-8"));
			return res;
		}
	}

	/**
	 * 制度包导出(新版)
	 * @param isEmptyIdx 是否填充为空指标
	 * @param rptId 报表id
	 * @param verId 版本id
	 * @param verNm 版本名称
	 * @return 下载的文件名信息
	 * **/
	protected Map<String, Object> exportDesignInfosNew(String isEmptyIdx, String rptId, String verId, String verNm) throws UnsupportedEncodingException {
		//声明返回变量
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> fileNames = new ArrayList<String>();
		//拆分报表编号
		String[] rptArrs = StringUtils.split(rptId, ",");
		//获取报表目录信息
		List<RptMgrReportCatalog> catalogList = this.rptTmpBS.getCatalogByParam(null, null, null, GlobalConstants4plugin.RPT_TYPE_DESIGN, null);
		Map<String, RptMgrReportCatalog> catalogMap = new HashMap<String, RptMgrReportCatalog>();
		Map<String, String> catalogUrlMap = new HashMap<String, String>();
		if((null != catalogList) && (catalogList.size() > 0)) {
			for(RptMgrReportCatalog catalog : catalogList) {
				catalogMap.put(catalog.getCatalogId(), catalog);
			}
		}
		if(rptArrs.length == 1){
			String fileName = this.exportInfoNew(isEmptyIdx, rptId, verId, verNm, catalogMap, catalogUrlMap);
			if (StringUtils.isNotEmpty(fileName)) {
				result.put("fileName", URLEncoder.encode(fileName,"UTF-8"));
			}
			return result;
		}
		else{
			for (String rptArr : rptArrs) {
				String fileName = this.exportInfoNew(isEmptyIdx, rptArr, verId, verNm, catalogMap, catalogUrlMap);
				if (StringUtils.isNotEmpty(fileName)) {
					fileNames.add(fileName);
				}
			}
			String zipfileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + "(" + verNm + ")" +"制度包.zip";//去工程路径下取模板的文件名]
			try{
				createZipFile(zipfileName, fileNames);//创建压缩包并删除源文件
			} catch (IOException e) {
				zipfileName = "";
				e.printStackTrace();
			}
			result.put("fileName", URLEncoder.encode(zipfileName,"UTF-8"));
			return result;
		}
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
		if (FilepathValidateUtils.validateFilepath(zipFilePath)) {
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
	
	protected abstract void addRptValidCfgextData(List<List<?>> list, String templateId, String endDate, String verId);

	@SuppressWarnings("unchecked")
	private String exportInfo(String isEmptyIdx, String rptId, String verId, String verNm, Map<String, RptMgrReportCatalog> catalogMap, Map<String, String> catalogUrlMap){
		ReportInfoVO vo = this.rptTmpBS.getRptBaseInfo(rptId, verId, catalogMap, catalogUrlMap);
		if (vo == null) {
			return null;
		}
		Map<String, Object> tmpInfo = this.rptTmpBS.getDesignInfo(vo.getTemplateId(), vo.getVerId(), null, isEmptyIdx, "export");
		RptDesignTmpInfo tmp = (RptDesignTmpInfo) tmpInfo.get("tmpInfo");
		List<List<?>> list = new ArrayList<List<?>>();
		//报表信息
		List<ReportInfoVO> infos = new ArrayList<ReportInfoVO>();
		infos.add(vo);
		list.add(infos);
		//
		//查询条件
		if(tmpInfo.get("paramJson") != null){
			List<RptDesignDimQueryVO> dimQuery = this.rptTmpBS.getDimQuery(tmpInfo.get("paramJson").toString());
			if(tmpInfo.get("detailObjs") != null){
				List<RptDesignQueryDetailVO> detailObjs = (List<RptDesignQueryDetailVO>) tmpInfo.get("detailObjs");
				if(detailObjs != null && detailObjs.size() > 0){
					int i = 0;
					for(RptDesignQueryDetailVO obj : detailObjs){
						obj.setDisplay(dimQuery.get(i).getDisplay());
						obj.setRequied(dimQuery.get(i).getRequied());
						obj.setValue(dimQuery.get(i).getValue());
					}
					i++;
					list.add(detailObjs);
				}
				else{
					list.add(dimQuery);
				}
			}
			else{
				list.add(dimQuery);
			}
		}
		
		//
		// 数据模型字段cell
		List<RptDesignModuleCellVO> moduleCells = (List<RptDesignModuleCellVO>) tmpInfo.get("moduleCells");
		// excel公式cell
		List<RptDesignFormulaCellVO> formulaCells = (List<RptDesignFormulaCellVO>) tmpInfo.get("formulaCells");
		for (int i = 0; formulaCells != null && i < formulaCells.size(); i ++) {
			RptDesignFormulaCellVO cellVO = formulaCells.get(i);
			if (cellVO.getExcelFormula() != null && cellVO.getExcelFormula().startsWith("=")) {
				cellVO.setExcelFormula(cellVO.getExcelFormula().substring(1));
			}
		}
		// 指标cell
		List<RptDesignIdxCellVO> idxCells = (List<RptDesignIdxCellVO>) tmpInfo.get("idxCells");
		// 静态表达式cell
		List<RptDesignStaticCellVO> staticCells = (List<RptDesignStaticCellVO>) tmpInfo.get("staticCells");
		// 列表维度cell
		List<RptDesignTabDimVO> colDimCells = (List<RptDesignTabDimVO>) tmpInfo.get("colDimCells");
		// 列表指标cell
		List<RptDesignTabIdxVO> colIdxCells = (List<RptDesignTabIdxVO>) tmpInfo.get("colIdxCells");
		// 表间计算cell
		List<RptDesignTabIdxVO> idxCalcCells = (List<RptDesignTabIdxVO>) tmpInfo.get("idxCalcCells");
		// 一般单元格字段cell
		List<RptDesignComcellInfoVO> comCells = (List<RptDesignComcellInfoVO>) tmpInfo.get("comCells");
		//单元格信息
		list.add(comCells);
		list.add(idxCells);
		list.add(moduleCells);
		list.add(colDimCells);
		list.add(colIdxCells);
		list.add(formulaCells);
		list.add(staticCells);
		list.add(idxCalcCells);
		//
		//指标过滤
		List<RptIdxFilterVO> filters = this.rptTmpBS.getFilterInfo(idxCells, colIdxCells);//获取指标过滤信息
		if(GlobalConstants4plugin.COMMON_BOOLEAN_NO.equals(isEmptyIdx)) {//导出空指标不需要指标过滤信息
			list.add(filters);
		}
		
		addRptValidCfgextData(list, vo.getTemplateId(), vo.getVerEndDate(), verId);

		String fileName = "";
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + "("+vo.getRptNm() + ")" + verNm + ".xlsx";//去工程路径下取模板的文件名
			if(FilepathValidateUtils.validateFilepath(fileName)) {
				try {
					SpreadWriterFactory.createWriter("xlsx", new File(fileName)).write(tmp.getTemplateContentjson(),list);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			fileName = "";
			e.printStackTrace();
		} finally {
		}
		return fileName;
	}

	/**
	 * 制度包导出(新版)
	 * @param verNm 版本名称
	 * @param verId 版本编号
	 * @param rptId 报表id
	 * @param isEmptyIdx 是否配置为空指标
	 * @param catalogMap 报表目录集合
	 * @param catalogUrlMap 目录相关
	 * @return 文件名
	 * **/
	@SuppressWarnings("unchecked")
	private String exportInfoNew(String isEmptyIdx, String rptId, String verId, String verNm, Map<String, RptMgrReportCatalog> catalogMap, Map<String, String> catalogUrlMap){
		ReportInfoVO vo = this.rptTmpBS.getRptBaseInfo(rptId, verId, catalogMap, catalogUrlMap);
		ExportReportInfoVO exportReportInfoVO = new ExportReportInfoVO();
		BeanUtils.copy(vo,exportReportInfoVO);
		Map<String, Object> tmpInfo = this.rptTmpBS.getDesignInfo(exportReportInfoVO.getTemplateId(), exportReportInfoVO.getVerId(), null, isEmptyIdx, "export");
		RptDesignTmpInfo tmp = (RptDesignTmpInfo) tmpInfo.get("tmpInfo");
		List<List<?>> list = new ArrayList<List<?>>();
		//报表信息
		List<ExportReportInfoVO> infos = new ArrayList<ExportReportInfoVO>();
		infos.add(exportReportInfoVO);
		list.add(infos);
		//
		//查询条件
		if(tmpInfo.get("paramJson") != null){
			List<RptDesignDimQueryVO> dimQuery = this.rptTmpBS.getDimQuery(tmpInfo.get("paramJson").toString());
			if(tmpInfo.get("detailObjs") != null){
				List<RptDesignQueryDetailVO> detailObjs = (List<RptDesignQueryDetailVO>) tmpInfo.get("detailObjs");
				if(detailObjs != null && detailObjs.size() > 0){
					int i = 0;
					for(RptDesignQueryDetailVO obj : detailObjs){
						obj.setDisplay(dimQuery.get(i).getDisplay());
						obj.setRequied(dimQuery.get(i).getRequied());
						obj.setValue(dimQuery.get(i).getValue());
					}
					i++;
					list.add(detailObjs);
				}
				else{
					list.add(dimQuery);
				}
			}
			else{
				list.add(dimQuery);
			}
		}

		//
		// 数据模型字段cell
		List<RptDesignModuleCellVO> moduleCells = (List<RptDesignModuleCellVO>) tmpInfo.get("moduleCells");
		// excel公式cell
		List<RptDesignFormulaCellVO> formulaCells = (List<RptDesignFormulaCellVO>) tmpInfo.get("formulaCells");
		for (int i = 0; formulaCells != null && i < formulaCells.size(); i ++) {
			RptDesignFormulaCellVO cellVO = formulaCells.get(i);
			if (cellVO.getExcelFormula() != null && cellVO.getExcelFormula().startsWith("=")) {
				cellVO.setExcelFormula(cellVO.getExcelFormula().substring(1));
			}
		}
		// 指标cell
		List<RptDesignIdxCellVO> idxCells = (List<RptDesignIdxCellVO>) tmpInfo.get("idxCells");
		// 静态表达式cell
		List<RptDesignStaticCellVO> staticCells = (List<RptDesignStaticCellVO>) tmpInfo.get("staticCells");
		// 列表维度cell
		List<RptDesignTabDimVO> colDimCells = (List<RptDesignTabDimVO>) tmpInfo.get("colDimCells");
		// 列表指标cell
		List<RptDesignTabIdxVO> colIdxCells = (List<RptDesignTabIdxVO>) tmpInfo.get("colIdxCells");
		// 表间计算cell
		List<RptDesignIdxCalcCellVO> idxCalcCells = (List<RptDesignIdxCalcCellVO>) tmpInfo.get("idxCalcCells");
		// 一般单元格字段cell
		List<RptDesignComcellInfoVO> comCells = (List<RptDesignComcellInfoVO>) tmpInfo.get("comCells");
		//单元格信息
		list.add(comCells);
		list.add(idxCells);
		list.add(moduleCells);
		list.add(colDimCells);
		list.add(colIdxCells);
		list.add(formulaCells);
		list.add(staticCells);
		list.add(idxCalcCells);
		//指标过滤
		List<RptIdxFilterVO> filters = this.rptTmpBS.getFilterInfo(idxCells, colIdxCells);//获取指标过滤信息
		if(GlobalConstants4plugin.COMMON_BOOLEAN_NO.equals(isEmptyIdx)) {//导出空指标不需要指标过滤信息
			list.add(filters);
		}

		Map<String, String> titles = new LinkedHashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put("rptInfo","2-报表表样");
				put("rptIdx","3-报表指标");
				put("rptBusiNo","4-人行编码");
				put("rptCell","5-单元格属性");
				put("rptSource","6-来源数据");
				put("rptCellNm","7-单元格名称");
				put("idxSumUpt","8-单元格是否跑数汇总||是否填报汇总||可修改");
				put("cellDisplayUnit","9-数据显示格式||单位||精度");
				put("excelFormula", "10-Excel公式属性");
				put("detailed","11-明细特有属性");
				put("rptFilter","12-指标过滤");
				put("cellCaliberExplain","13-业务说明");
				put("cellCaliberTechnology","14-技术口径");
			}
		};

		String fileName = "";
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + "("+exportReportInfoVO.getRptNm() + ")" + vo.getSystemName() + ".xlsx";//去工程路径下取模板的文件名
			try {
				if(FilepathValidateUtils.validateFilepath(fileName)) {
					SpreadWriterFactory.createWriter("xlsx", new File(fileName)).write(tmp.getTemplateContentjson(),titles,list);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			fileName = "";
			e.printStackTrace();
		} finally {
		}
		return fileName;
	}
	
	/**
	 * 模板导入(首页)
	 */
	public ModelAndView importDesignInfos(String type) {
		Map<String, String> out = new HashMap<String, String>();
		out.put("type", type);
		return new ModelAndView("/plugin/design/cfg/import-design-infos-index", out);
	}

	/**
	 * 模板导入(检查选项)
	 */
	protected ModelAndView importDesignInfosCheckOption() {
		return new ModelAndView("/plugin/design/cfg/import-design-infos-check-option");
	}

	/**
	 * 模板导入(文件上传)
	 */
	protected ModelAndView importDesignInfosUpload(String emptyCellInFormula,
												   String emptyIndexInFormula, String emptyCellInVerifyWarn, String fromSystemRptCfg, String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("emptyCellInFormula", StringUtils2.javaScriptEncode(emptyCellInFormula));
		map.put("emptyIndexInFormula", StringUtils2.javaScriptEncode(emptyIndexInFormula));
		map.put("emptyCellInVerifyWarn", StringUtils2.javaScriptEncode(emptyCellInVerifyWarn));
		map.put("fromSystemRptCfg", StringUtils2.javaScriptEncode(fromSystemRptCfg));
		map.put("type", type);
		return new ModelAndView("/plugin/design/cfg/import-design-infos-upload", map);
	}

	/**
	 * 模板导入
	 */
	protected Map<String, Object> importDesignInfosImpl(Uploader uploader, Map<String, String> optionMap) throws Exception {
		//记录表清理操作
		int totalCount = rptModeImportInfoBS.getTotalImportDesignInfoLogCount();
		if (totalCount > 10000){
			rptModeImportInfoBS.cleanImportDesignInfoLog();
		}
		rptModeImportInfoBS.insertImportDesignInfoLog(0, optionMap.get("uuid"), "开始进行制度包导入，文件上传开始...");
		//校验文件格式
		String realName = uploader.getUpload().getOriginalFilename();
		if(realName.endsWith(".xls")){
			rptModeImportInfoBS.insertImportDesignInfoLog(1, optionMap.get("uuid"), "文件格式错误，请上传.xlsx格式文件");
			return null;
		}
		File file = null;
		try {
			file = this.uploadFile(uploader, UPLOAD_ATTACH_DIR
					+ File.separatorChar, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		if (file == null) {
			return null;
		}
		logger.info("文件[" + file.getName() + "]上传完成");
		File targetDir = new File(file.getParentFile(), RandomUtils.uuid2());
		targetDir.mkdir();
		try {
			if (file.getName().endsWith(".zip")) {
				FilesUtils.unzip(file, targetDir.getAbsolutePath());
				file.delete();
			} else {
				File newFile = new File(targetDir, file.getName());
				file.renameTo(newFile);
			}
			if ("new".equals(optionMap.get("type"))) {
				//新制度包导入处理
				logger.info("文件上传完成，新制度包准备开始导入");
				rptModeImportInfoBS.insertImportDesignInfoLog(1, optionMap.get("uuid"), "文件上传完成，新制度包准备开始导入...");
				return rptModelImportBS.importNewVersion(targetDir.getAbsolutePath(), optionMap, file.getName());
			} else {
				return rptModelImportBS.exec(targetDir.getAbsolutePath(), optionMap);
			}
		} finally {
			FilesUtils.deleteFiles(targetDir);
		}
	}

	/**
	 * 日志数据查询
	 * @param uuid 查询id
	 * **/
	protected Map<String, String> importDesignInfosUploadLogShow(String uuid){
		return rptModelImportBS.getImportDesignInfosUploadLog(uuid);
	};
}
