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
					+ File.separator + "(" + verNm + ")" +"?????????.zip";//???????????????????????????????????????]
			try{
				createZipFile(zipfileName, fileNames);//?????????????????????????????????
			} catch (IOException e) {
				zipfileName = "";
				e.printStackTrace();
			}
			res.put("fileName", URLEncoder.encode(zipfileName,"UTF-8"));
			return res;
		}
	}

	/**
	 * ???????????????(??????)
	 * @param isEmptyIdx ????????????????????????
	 * @param rptId ??????id
	 * @param verId ??????id
	 * @param verNm ????????????
	 * @return ????????????????????????
	 * **/
	protected Map<String, Object> exportDesignInfosNew(String isEmptyIdx, String rptId, String verId, String verNm) throws UnsupportedEncodingException {
		//??????????????????
		Map<String, Object> result = new HashMap<String, Object>();
		List<String> fileNames = new ArrayList<String>();
		//??????????????????
		String[] rptArrs = StringUtils.split(rptId, ",");
		//????????????????????????
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
					+ File.separator + "(" + verNm + ")" +"?????????.zip";//???????????????????????????????????????]
			try{
				createZipFile(zipfileName, fileNames);//?????????????????????????????????
			} catch (IOException e) {
				zipfileName = "";
				e.printStackTrace();
			}
			result.put("fileName", URLEncoder.encode(zipfileName,"UTF-8"));
			return result;
		}
	}
	
	/**
	 * ????????????
	 * 
	 * @param zipFilePath
	 *            ??????????????????
	 * @param filePathList
	 *            ?????????????????????LIST
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
		//????????????
		List<ReportInfoVO> infos = new ArrayList<ReportInfoVO>();
		infos.add(vo);
		list.add(infos);
		//
		//????????????
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
		// ??????????????????cell
		List<RptDesignModuleCellVO> moduleCells = (List<RptDesignModuleCellVO>) tmpInfo.get("moduleCells");
		// excel??????cell
		List<RptDesignFormulaCellVO> formulaCells = (List<RptDesignFormulaCellVO>) tmpInfo.get("formulaCells");
		for (int i = 0; formulaCells != null && i < formulaCells.size(); i ++) {
			RptDesignFormulaCellVO cellVO = formulaCells.get(i);
			if (cellVO.getExcelFormula() != null && cellVO.getExcelFormula().startsWith("=")) {
				cellVO.setExcelFormula(cellVO.getExcelFormula().substring(1));
			}
		}
		// ??????cell
		List<RptDesignIdxCellVO> idxCells = (List<RptDesignIdxCellVO>) tmpInfo.get("idxCells");
		// ???????????????cell
		List<RptDesignStaticCellVO> staticCells = (List<RptDesignStaticCellVO>) tmpInfo.get("staticCells");
		// ????????????cell
		List<RptDesignTabDimVO> colDimCells = (List<RptDesignTabDimVO>) tmpInfo.get("colDimCells");
		// ????????????cell
		List<RptDesignTabIdxVO> colIdxCells = (List<RptDesignTabIdxVO>) tmpInfo.get("colIdxCells");
		// ????????????cell
		List<RptDesignTabIdxVO> idxCalcCells = (List<RptDesignTabIdxVO>) tmpInfo.get("idxCalcCells");
		// ?????????????????????cell
		List<RptDesignComcellInfoVO> comCells = (List<RptDesignComcellInfoVO>) tmpInfo.get("comCells");
		//???????????????
		list.add(comCells);
		list.add(idxCells);
		list.add(moduleCells);
		list.add(colDimCells);
		list.add(colIdxCells);
		list.add(formulaCells);
		list.add(staticCells);
		list.add(idxCalcCells);
		//
		//????????????
		List<RptIdxFilterVO> filters = this.rptTmpBS.getFilterInfo(idxCells, colIdxCells);//????????????????????????
		if(GlobalConstants4plugin.COMMON_BOOLEAN_NO.equals(isEmptyIdx)) {//??????????????????????????????????????????
			list.add(filters);
		}
		
		addRptValidCfgextData(list, vo.getTemplateId(), vo.getVerEndDate(), verId);

		String fileName = "";
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + "("+vo.getRptNm() + ")" + verNm + ".xlsx";//???????????????????????????????????????
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
	 * ???????????????(??????)
	 * @param verNm ????????????
	 * @param verId ????????????
	 * @param rptId ??????id
	 * @param isEmptyIdx ????????????????????????
	 * @param catalogMap ??????????????????
	 * @param catalogUrlMap ????????????
	 * @return ?????????
	 * **/
	@SuppressWarnings("unchecked")
	private String exportInfoNew(String isEmptyIdx, String rptId, String verId, String verNm, Map<String, RptMgrReportCatalog> catalogMap, Map<String, String> catalogUrlMap){
		ReportInfoVO vo = this.rptTmpBS.getRptBaseInfo(rptId, verId, catalogMap, catalogUrlMap);
		ExportReportInfoVO exportReportInfoVO = new ExportReportInfoVO();
		BeanUtils.copy(vo,exportReportInfoVO);
		Map<String, Object> tmpInfo = this.rptTmpBS.getDesignInfo(exportReportInfoVO.getTemplateId(), exportReportInfoVO.getVerId(), null, isEmptyIdx, "export");
		RptDesignTmpInfo tmp = (RptDesignTmpInfo) tmpInfo.get("tmpInfo");
		List<List<?>> list = new ArrayList<List<?>>();
		//????????????
		List<ExportReportInfoVO> infos = new ArrayList<ExportReportInfoVO>();
		infos.add(exportReportInfoVO);
		list.add(infos);
		//
		//????????????
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
		// ??????????????????cell
		List<RptDesignModuleCellVO> moduleCells = (List<RptDesignModuleCellVO>) tmpInfo.get("moduleCells");
		// excel??????cell
		List<RptDesignFormulaCellVO> formulaCells = (List<RptDesignFormulaCellVO>) tmpInfo.get("formulaCells");
		for (int i = 0; formulaCells != null && i < formulaCells.size(); i ++) {
			RptDesignFormulaCellVO cellVO = formulaCells.get(i);
			if (cellVO.getExcelFormula() != null && cellVO.getExcelFormula().startsWith("=")) {
				cellVO.setExcelFormula(cellVO.getExcelFormula().substring(1));
			}
		}
		// ??????cell
		List<RptDesignIdxCellVO> idxCells = (List<RptDesignIdxCellVO>) tmpInfo.get("idxCells");
		// ???????????????cell
		List<RptDesignStaticCellVO> staticCells = (List<RptDesignStaticCellVO>) tmpInfo.get("staticCells");
		// ????????????cell
		List<RptDesignTabDimVO> colDimCells = (List<RptDesignTabDimVO>) tmpInfo.get("colDimCells");
		// ????????????cell
		List<RptDesignTabIdxVO> colIdxCells = (List<RptDesignTabIdxVO>) tmpInfo.get("colIdxCells");
		// ????????????cell
		List<RptDesignIdxCalcCellVO> idxCalcCells = (List<RptDesignIdxCalcCellVO>) tmpInfo.get("idxCalcCells");
		// ?????????????????????cell
		List<RptDesignComcellInfoVO> comCells = (List<RptDesignComcellInfoVO>) tmpInfo.get("comCells");
		//???????????????
		list.add(comCells);
		list.add(idxCells);
		list.add(moduleCells);
		list.add(colDimCells);
		list.add(colIdxCells);
		list.add(formulaCells);
		list.add(staticCells);
		list.add(idxCalcCells);
		//????????????
		List<RptIdxFilterVO> filters = this.rptTmpBS.getFilterInfo(idxCells, colIdxCells);//????????????????????????
		if(GlobalConstants4plugin.COMMON_BOOLEAN_NO.equals(isEmptyIdx)) {//??????????????????????????????????????????
			list.add(filters);
		}

		Map<String, String> titles = new LinkedHashMap<String, String>(){
			private static final long serialVersionUID = 1L;
			{
				put("rptInfo","2-????????????");
				put("rptIdx","3-????????????");
				put("rptBusiNo","4-????????????");
				put("rptCell","5-???????????????");
				put("rptSource","6-????????????");
				put("rptCellNm","7-???????????????");
				put("idxSumUpt","8-???????????????????????????||??????????????????||?????????");
				put("cellDisplayUnit","9-??????????????????||??????||??????");
				put("excelFormula", "10-Excel????????????");
				put("detailed","11-??????????????????");
				put("rptFilter","12-????????????");
				put("cellCaliberExplain","13-????????????");
				put("cellCaliberTechnology","14-????????????");
			}
		};

		String fileName = "";
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + "("+exportReportInfoVO.getRptNm() + ")" + vo.getSystemName() + ".xlsx";//???????????????????????????????????????
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
	 * ????????????(??????)
	 */
	public ModelAndView importDesignInfos(String type) {
		Map<String, String> out = new HashMap<String, String>();
		out.put("type", type);
		return new ModelAndView("/plugin/design/cfg/import-design-infos-index", out);
	}

	/**
	 * ????????????(????????????)
	 */
	protected ModelAndView importDesignInfosCheckOption() {
		return new ModelAndView("/plugin/design/cfg/import-design-infos-check-option");
	}

	/**
	 * ????????????(????????????)
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
	 * ????????????
	 */
	protected Map<String, Object> importDesignInfosImpl(Uploader uploader, Map<String, String> optionMap) throws Exception {
		//?????????????????????
		int totalCount = rptModeImportInfoBS.getTotalImportDesignInfoLogCount();
		if (totalCount > 10000){
			rptModeImportInfoBS.cleanImportDesignInfoLog();
		}
		rptModeImportInfoBS.insertImportDesignInfoLog(0, optionMap.get("uuid"), "????????????????????????????????????????????????...");
		//??????????????????
		String realName = uploader.getUpload().getOriginalFilename();
		if(realName.endsWith(".xls")){
			rptModeImportInfoBS.insertImportDesignInfoLog(1, optionMap.get("uuid"), "??????????????????????????????.xlsx????????????");
			return null;
		}
		File file = null;
		try {
			file = this.uploadFile(uploader, UPLOAD_ATTACH_DIR
					+ File.separatorChar, false);
		} catch (Exception e) {
			logger.info("????????????????????????", e);
		}
		if (file == null) {
			return null;
		}
		logger.info("??????[" + file.getName() + "]????????????");
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
				//????????????????????????
				logger.info("???????????????????????????????????????????????????");
				rptModeImportInfoBS.insertImportDesignInfoLog(1, optionMap.get("uuid"), "???????????????????????????????????????????????????...");
				return rptModelImportBS.importNewVersion(targetDir.getAbsolutePath(), optionMap, file.getName());
			} else {
				return rptModelImportBS.exec(targetDir.getAbsolutePath(), optionMap);
			}
		} finally {
			FilesUtils.deleteFiles(targetDir);
		}
	}

	/**
	 * ??????????????????
	 * @param uuid ??????id
	 * **/
	protected Map<String, String> importDesignInfosUploadLogShow(String uuid){
		return rptModelImportBS.getImportDesignInfosUploadLog(uuid);
	};
}
