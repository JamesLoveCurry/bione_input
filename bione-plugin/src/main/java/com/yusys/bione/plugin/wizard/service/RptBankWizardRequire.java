package com.yusys.bione.plugin.wizard.service;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.RandomUtils;
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
import com.yusys.bione.plugin.rptbank.entity.RptIdxBankInfo;
import com.yusys.bione.plugin.rptbank.entity.RptIdxBankInfoPK;
import com.yusys.bione.plugin.rptbank.service.RptIdxBankBS;
import com.yusys.bione.plugin.rptbank.web.vo.RptBankImportVO;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxMeasureInfo;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;

/**
 * <pre>
 * Title:用户信息的导入导出
 * Description: 提供用户信息上传/用户信息及用户角色关系信息的导出/用户信息保存/上传用户信息的检验等功能
 * </pre>
 * 
 * @author donglt
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Service
public class RptBankWizardRequire extends BaseController implements IWizardRequire {
	
	@Autowired
	private ExcelBS excelBS;
	
	@Autowired
	private RptIdxBankBS idxBankBs;
	
	/**
	 * 用户信息上传
	 * @param file 文件对象
	 * @return 上传文件结果
	 */
	@SuppressWarnings("unchecked")
	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(RptBankImportVO.class, file);
		UploadResult result = new UploadResult();
		try {
			List<RptBankImportVO> vos = (List<RptBankImportVO>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					vos);
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (vos != null && vos.size() > 0) {
				for (RptBankImportVO vo : vos) {
					result.setInfo(vo.getIndexNm(), vo.getIndexNm());
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
	 * 主题信息的导出
	 * @param ids 选中的主题编号
	 * @return 文件名称
	 */
	@Override
	public String export(String ids) {
		String fileName = "";
		//查询要导出的主题信息
		List<RptIdxBankInfo> infos = this.idxBankBs.getEntityListByProperty(RptIdxBankInfo.class, "id.themeId", ids, "orderNum", false);
		List<RptBankImportVO> vos = new ArrayList<RptBankImportVO>();//初始化一个list，用于存放导出工作簿中第一个sheet页的内容
		Map<String,String> pecBankMap = Maps.newHashMap();
		if(null != infos && infos.size() > 0){
			for(RptIdxBankInfo info : infos){
				pecBankMap.put(info.getId().getIndexId(), info.getIndexNm());
			}
		}
		
		if(infos != null && infos.size() >0){//循环遍历查询的主题信息对象，放入到excel模板对象PecBankImportVO中
			for(RptIdxBankInfo info :infos){
				RptBankImportVO vo = new RptBankImportVO();
				vo.setIndexNm(info.getIndexNm());
				vo.setMainIndexNo(info.getMainNo());
				vo.setPartIndexNo(info.getPartNo());
				vo.setUpIndexNm(pecBankMap.get(info.getUpNo()));
				vo.setCurrency(info.getCurrency());
				vo.setRemark(info.getRemark());
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
					+ GlobalConstants4plugin.EXPORT_RPT_BANK_PATH, list);
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
	 * 上传用户信息的检验
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 * @return 检验信息对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		List<ValidErrorInfoObj> errors = new ArrayList<ValidErrorInfoObj>();
		Map<String, String> exiPecMap = this.idxBankBs.getExiPecMap(dsId);
		List<RptIdxBankInfo> pecBanks = new ArrayList<RptIdxBankInfo>();
		List<RptIdxInfo> idxInfo = this.idxBankBs.getEntityList(RptIdxInfo.class);
		List<RptDimItemInfo> curInfo = this.idxBankBs.getEntityListByProperty(RptDimItemInfo.class, "id.dimTypeNo", "CURRENCY");
		List<RptIdxMeasureInfo> mesInfo = this.idxBankBs.getEntityList(RptIdxMeasureInfo.class);
		List<RptBankImportVO> vos = (List<RptBankImportVO>) EhcacheUtils.get(BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		Map<String,String> pecBankMap = Maps.newHashMap();
		Map<String,String[]> lvlBankMap = Maps.newHashMap();
		Map<String,String> idxMap = Maps.newHashMap();
		Map<String,String> curMap = Maps.newHashMap();
		Map<String,String> mesMap = Maps.newHashMap();
		if(null != idxInfo && idxInfo.size() > 0){
			for(RptIdxInfo idx : idxInfo){
				idxMap.put(idx.getId().getIndexNo(), idx.getIndexNm());
			}
		}
		if(null != curInfo && curInfo.size() > 0){
			for(RptDimItemInfo cur : curInfo){
				curMap.put(cur.getId().getDimItemNo(), cur.getDimItemNm());
			}
		}
		if(null != mesInfo && mesInfo.size() > 0){
			for(RptIdxMeasureInfo mes : mesInfo){
				mesMap.put(mes.getMeasureNo(), mes.getMeasureNm());
			}
		}
		if (vos != null && vos.size() > 0) {
			for(RptBankImportVO vo : vos){
				String indexId = exiPecMap.get(dsId + vo.getMainIndexNo() + vo.getPartIndexNo() + vo.getCurrency());
				String[] pecArr = {null == indexId ? RandomUtils.uuid2() : indexId, vo.getUpIndexNm() };
				if(vo.getIndexNm().equals(vo.getUpIndexNm())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(4);
					obj.setValidTypeNm("合理性校验");
					obj.setErrorMsg("上级指标不可以是其本身！");
					errors.add(obj);
					break;
				}
				lvlBankMap.put(vo.getIndexNm(), pecArr);
			}
			int i = 0;
			for (RptBankImportVO vo : vos) {
				try {//其余校验
					Validator.validate(vo);
				} catch (ValidateException e) {
					errors.addAll(e.getErrorInfoObjs());
				}
				//excel内部校验
				if("inExcel".equals(pecBankMap.get(vo.getIndexNm()))){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(1);
					obj.setValidTypeNm("唯一性校验");
					obj.setErrorMsg("同一主题下导入指标名称存在重复");
					errors.add(obj);
				}else{//不存在重复就放入到map变量中，确保能全部校验出所有的重复记录
					pecBankMap.put(vo.getIndexNm(), "inExcel");
				}
				
				//主指标校验
				if(null != vo.getMainIndexNo() && !"".equals(vo.getMainIndexNo())){//主指标存在性校验
					String[] idx = StringUtils.split(vo.getMainIndexNo(), ".");
					if(null != idx && idx.length > 1){
						if(null == idxMap.get(idx[0])){//总账指标编号校验
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(2);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("该主指标不存在");
							errors.add(obj);
						}
						if(null == mesMap.get(idx[1])){//总账指标度量校验
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(2);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("该主指标度量不存在");
							errors.add(obj);
						}
					}else{
						if(null == idxMap.get(vo.getMainIndexNo())){//非总账指标校验
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(2);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("该主指标不存在");
							errors.add(obj);
						}
					}
				}
				
				//分指标校验
				if(null != vo.getPartIndexNo() && !"".equals(vo.getPartIndexNo())){//分指标存在性校验
					String[] idx = StringUtils.split(vo.getPartIndexNo(), ".");
					if(null != idx && idx.length > 1){
						if(null == idxMap.get(idx[0])){//总账指标编号校验
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(3);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("该分指标不存在");
							errors.add(obj);
						}
						if(null == mesMap.get(idx[1])){//总账指标度量校验
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(2);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("该分指标度量不存在");
							errors.add(obj);
						}
					}else{
						if(null == idxMap.get(vo.getPartIndexNo())){
							ValidErrorInfoObj obj=new ValidErrorInfoObj();
							obj.setSheetName(vo.getSheetName());
							obj.setExcelRowNo(vo.getExcelRowNo());
							obj.setExcelColNo(3);
							obj.setValidTypeNm("存在性校验");
							obj.setErrorMsg("该分指标不存在");
							errors.add(obj);
						}
					}
				}
				
				//币种存在性校验
				if(null != vo.getCurrency() && !"".equals(vo.getCurrency()) && 
						null == curMap.get(vo.getCurrency())){
					ValidErrorInfoObj obj=new ValidErrorInfoObj();
					obj.setSheetName(vo.getSheetName());
					obj.setExcelRowNo(vo.getExcelRowNo());
					obj.setExcelColNo(5);
					obj.setValidTypeNm("存在性校验");
					obj.setErrorMsg("该币种于系统不存在");
					errors.add(obj);
				}
				
				RptIdxBankInfo pecBank = new RptIdxBankInfo();
				RptIdxBankInfoPK pecBankPk = new RptIdxBankInfoPK();
				pecBankPk.setThemeId(dsId);
				pecBankPk.setIndexId(null != lvlBankMap.get(vo.getIndexNm()) ? 
						lvlBankMap.get(vo.getIndexNm())[0] : RandomUtils.uuid2());
				pecBank.setId(pecBankPk);
				
				if(null == lvlBankMap.get(vo.getUpIndexNm())){
					pecBank.setUpNo("0");
					pecBank.setIndexLevel("0");
				}else{
					int[] lvl = {0};
					this.getIndexLevel(lvlBankMap,vo.getUpIndexNm(),lvl);
					pecBank.setUpNo(lvlBankMap.get(vo.getUpIndexNm())[0]);
					pecBank.setIndexLevel(Integer.toString(lvl[0]));
				}
				
				pecBank.setIndexNm(vo.getIndexNm());
				pecBank.setMainNo(vo.getMainIndexNo());
				pecBank.setPartNo(null == vo.getPartIndexNo() || "".equals(vo.getPartIndexNo()) ? vo.getMainIndexNo() : vo.getPartIndexNo());
				pecBank.setCurrency(vo.getCurrency());
				pecBank.setRemark(vo.getRemark());
				pecBank.setOrderNum(new BigDecimal(++i));
				pecBanks.add(pecBank);
			}
		}
		List<List<?>> lists = new ArrayList<List<?>>();
		lists.add(pecBanks);//用户信息
		EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,lists);
		return errors;
	}
	private void getIndexLevel(Map<String,String[]> lvlMap,String indexNm,int[] lvl) {
		if(null != lvlMap.get(indexNm)){
			lvl[0] += 1;
			getIndexLevel(lvlMap,lvlMap.get(indexNm)[1],lvl);
		}
	}
	/**
	 * 用户信息保存
	 * @param ehcacheId 缓存标志符
	 * @param dsId 数据源ID
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void saveData(String ehcacheId, String dsId) {
		// TODO Auto-generated method stub
		List<List<?>> lists = (List<List<?>>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		List<RptIdxBankInfo> pecBanks = (List<RptIdxBankInfo>) lists.get(0);//主题信息
		List<String> pecFields = new ArrayList<String>();
		pecFields.add("id.themeId");
		this.excelBS.deleteEntityJdbcBatch(pecBanks, pecFields);
		this.excelBS.saveEntityJdbcBatch(pecBanks);
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
