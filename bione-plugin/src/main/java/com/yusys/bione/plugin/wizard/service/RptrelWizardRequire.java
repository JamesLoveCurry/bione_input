package com.yusys.bione.plugin.wizard.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yusys.bione.comp.utils.ArrayUtils;
import com.yusys.bione.comp.utils.BeanUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.excel.AbstractExcelImport;
import com.yusys.bione.frame.excel.BioneExporterException;
import com.yusys.bione.frame.excel.ExcelBS;
import com.yusys.bione.frame.excel.ExcelImporter;
import com.yusys.bione.frame.excel.XlsExcelTemplateExporter;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.validator.relatedobj.ValidErrorInfoObj;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCatalog;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.service.RptDatasetBS;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrIdxFilter;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrIdxFilterPK;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleIdxRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleIdxRelPK;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleRel;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrModuleRelPK;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.wizard.repository.RptrelDao;
import com.yusys.bione.plugin.wizard.web.vo.RptrelImportVO;
import com.yusys.bione.plugin.wizard.web.vo.UploadResult;

@Service
@Transactional(readOnly = true)
public class RptrelWizardRequire extends BaseController implements
		IWizardRequire {
	@Autowired
	private ExcelBS excelBS;
	@Autowired
	private RptrelDao relDao;
	@Autowired
	private RptDatasetBS setBs;

	private Map<String, Object> getRequireInfo(Collection<String> rptNumes,
			Collection<String> datasetEnNames,
			Collection<String> dataItemEnNames,
			Collection<String> dimTypeNames, Collection<String> dimItemNames,
			Map<String, Set<String>> fDim) {
		Map<String, Object> map = Maps.newHashMap();
		map.put("rpt", Maps.newHashMap());
		map.put("dataset", Maps.newHashMap());
		map.put("dataitem", Maps.newHashMap());
		map.put("dim", Maps.newHashMap());
		Map<String, Object> mt = null;
		if (rptNumes != null && rptNumes.size() > 0) {
			mt = Maps.newHashMap();
			List<RptMgrReportInfo> list = new ArrayList<RptMgrReportInfo>();
			int index = rptNumes.size() / 1000;
			List<String> rptNums = new ArrayList<String>(rptNumes);
			for (int i = 0; i <= index; i++) {
				mt.clear();
				mt.put("rptIds", rptNums.subList(1000 * i,
						(1000 * i + 1000) >= rptNums.size() ? rptNums.size()
								: (1000 * i + 1000)));
				list.addAll(relDao.findReportInfo(mt));
			}
			mt.clear();
			for (Iterator<RptMgrReportInfo> iter = list.iterator(); iter
					.hasNext();) {
				RptMgrReportInfo info = iter.next();
				mt.put(info.getRptNum(), info);
			}
			map.put("rpt", mt);
		}
		// 通过数据集物理名称查询出数据集信息
		if (datasetEnNames != null && datasetEnNames.size() > 0) {
			List<String> datasetEnNamelists = new ArrayList<String>(
					datasetEnNames);
			int index = datasetEnNames.size() / 1000;
			mt = Maps.newHashMap();
			List<RptSysModuleInfo> list = new ArrayList<RptSysModuleInfo>();
			for (int i = 0; i <= index; i++) {
				mt.clear();
				mt.put("datasetEnNames",
						datasetEnNamelists
								.subList(
										1000 * i,
										(1000 * i + 1000) >= datasetEnNamelists
												.size() ? datasetEnNamelists
												.size() : (1000 * i + 1000)));
				list.addAll(relDao.findDatasetInfo(mt));
			}
			mt.clear();
			for (Iterator<RptSysModuleInfo> iter = list.iterator(); iter
					.hasNext();) {
				RptSysModuleInfo info = iter.next();
				mt.put(info.getTableEnNm(), info);
			}
			map.put("dataset", mt);
		}
		// 通过数据项物理名称查询出模型列信息
		if (dataItemEnNames != null && dataItemEnNames.size() > 0) {
			List<String> dataItemEnNameslists = new ArrayList<String>(
					dataItemEnNames);
			List<RptSysModuleCol> list = new ArrayList<RptSysModuleCol>();
			int index = dataItemEnNames.size() / 1000;
			mt = Maps.newHashMap();
			for (int i = 0; i <= index; i++) {
				mt.clear();
				mt.put("dataItemEnNames", dataItemEnNameslists.subList(
						1000 * i, (1000 * i + 1000) >= dataItemEnNameslists
								.size() ? dataItemEnNameslists.size()
								: (1000 * i + 1000)));
				list.addAll(relDao.findDatasetItemInfo(mt));
			}
			mt.clear();
			for (Iterator<RptSysModuleCol> iter = list.iterator(); iter
					.hasNext();) {
				RptSysModuleCol info = iter.next();
				mt.put(decodeKey(info.getSetId(), info.getEnNm()), info);
			}
			map.put("dataitem", mt);
		}
		// 通过判断数据项类型若为维度通过维度名称查出维度信息
		if (dimTypeNames != null && dimTypeNames.size() > 0) {
			List<String> dimTypeNameslists = new ArrayList<String>(dimTypeNames);
			List<RptDimTypeInfo> list = new ArrayList<RptDimTypeInfo>();
			int index = dimTypeNames.size() / 1000;
			mt = Maps.newHashMap();
			for (int i = 0; i <= index; i++) {
				mt.clear();
				mt.put("dimNames",
						dimTypeNameslists
								.subList(
										1000 * i,
										(1000 * i + 1000) >= dimTypeNameslists
												.size() ? dimTypeNameslists
												.size() : (1000 * i + 1000)));
				list.addAll(relDao.findDimInfo(mt));
			}
			mt.clear();
			for (Iterator<RptDimTypeInfo> iter = list.iterator(); iter
					.hasNext();) {
				RptDimTypeInfo info = iter.next();
				mt.put(info.getDimTypeNm(), info);
			}
			map.put("dim", mt);
		}

		if (dimItemNames != null && dimItemNames.size() > 0) {
			List<String> dimItemNameslists = new ArrayList<String>(dimItemNames);
			List<RptDimItemInfo> list = new ArrayList<RptDimItemInfo>();
			int index = dimItemNames.size() / 1000;
			mt = Maps.newHashMap();
			for (int i = 0; i <= index; i++) {
				mt.clear();
				mt.put("dimItemNames",
						dimItemNameslists
								.subList(
										1000 * i,
										(1000 * i + 1000) >= dimItemNameslists
												.size() ? dimItemNameslists
												.size() : (1000 * i + 1000)));
				list.addAll(relDao.findDimItemInfo(mt));
			}
			mt.clear();
			for (Iterator<RptDimItemInfo> iter = list.iterator(); iter
					.hasNext();) {
				RptDimItemInfo info = iter.next();
				mt.put(info.getDimItemNm(), info);
			}
			map.put("dimItem", mt);
		}

		if (fDim != null && fDim.size() > 0) {
			mt = Maps.newHashMap();
			Map<String, Object> sDim = Maps.newHashMap();
			mt.put("dimTypeNms",
					ReBuildParam.toDbList(new ArrayList<String>(fDim.keySet())));
			List<RptDimItemInfo> dimItems = relDao.findDimItem(mt);
			Iterator<RptDimItemInfo> iIter = dimItems.iterator();
			while (iIter.hasNext()) {
				RptDimItemInfo info = iIter.next();
				sDim.put(decodeKey(info.getRemark(), info.getDimItemNm()), info);
			}
			// Iterator<String> iter = fDim.keySet().iterator();
			// while (iter.hasNext()) {
			// String key = iter.next();
			// Set<String> value = fDim.get(key);
			// if (StringUtils.isNotEmpty(key) && value != null
			// && value.size() > 0) {
			// mt.put("dimTypeNm", key);
			// mt.put("dimItemNames", value);
			// List<RptDimItemInfo> dimItems = relDao.findDimItem(mt);
			// Iterator<RptDimItemInfo> iIter = dimItems.iterator();
			// while (iIter.hasNext()) {
			// RptDimItemInfo info = iIter.next();
			// sDim.put(decodeKey(key, info.getDimItemNm()), info);
			// }
			// }
			// }
			map.put("sDim", sDim);
		}
		return map;
	}

	private String decodeKey(String... ids) {
		JSONObject jo = new JSONObject();
		for (int i = 0; i < ids.length; i++) {
			jo.put("" + i, ids[i]);
		}
		return jo.toJSONString();
	}

	@Override
	public UploadResult upload(File file) {
		AbstractExcelImport xlsImport = new ExcelImporter(RptrelImportVO.class, file);
		UploadResult result = new UploadResult();
		try {
			@SuppressWarnings("unchecked")
			List<RptrelImportVO> list = (List<RptrelImportVO>) xlsImport
					.ReadExcel();
			String ehcacheId = RandomUtils.uuid2();
			EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(), ehcacheId,
					list);
			result.setEhcacheId(ehcacheId);
			result.setFileName(file.getName());
			if (list != null) {
				for (Iterator<RptrelImportVO> iterator = list.iterator(); iterator
						.hasNext();) {
					RptrelImportVO vo = iterator.next();
					result.setInfo(vo.getRptNum(), vo.getRptNm());
				}
			}

		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ValidErrorInfoObj> validateInfo(String dsId, String ehcacheId) {
		// 错误列表
		List<ValidErrorInfoObj> list = Lists.newArrayList();
		List<RptrelImportVO> voLs = ((List<RptrelImportVO>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId));
		Iterator<RptrelImportVO> voIter = voLs.iterator();
		Set<String> tableEnNms = Sets.newHashSet(), enNms = Sets.newHashSet(), dimTypeNms = Sets
				.newHashSet(), rptNums = Sets.newHashSet(), indexNos = Sets
				.newHashSet();
		Map<String, Set<String>> filterMap = Maps.newHashMap();
		// 遍历开始
		while (voIter.hasNext()) {
			RptrelImportVO vo = voIter.next();
			// 数据准备
			tableEnNms.add(vo.getTableEnNm());
			enNms.add(vo.getSetNm());
			dimTypeNms.add(vo.getDimTypeNm());
			rptNums.add(vo.getRptNum());
			indexNos.add(vo.getIndexNo());
			Map<String, Object> detail = vo.getIdxFilterDetail();
			if (detail != null) {
				for (Iterator<String> iterator = detail.keySet().iterator(); iterator
						.hasNext();) {
					String key = iterator.next();
					Set<String> dimItems = Sets.newHashSet();
					dimItems.addAll(Arrays.asList((String[]) detail.get(key)));
					filterMap.put(key, dimItems);
				}
			}
		}
		Map<String, Object> require = getRequireInfo(rptNums, null, null,
				dimTypeNms, null, filterMap);
		Map<String, Object> rptMap = (Map<String, Object>) require.get("rpt");
		Map<String, Object> dimMap = (Map<String, Object>) require.get("dim");
		Map<String, Object> filtMap = (Map<String, Object>) require.get("sDim");
		voIter = voLs.iterator();
		Map<String, List<RptSysModuleCol>> dsmp = setBs.getFieldsOfTableList(
				dsId, new ArrayList<String>(tableEnNms));
		Map<String, RptrelImportVO> validDim = Maps.newHashMap();
		for (int i = 0; i < voLs.size(); i++) {
			RptrelImportVO vo = voLs.get(i);
			// 数据校验
			String tableEnNm = vo.getTableEnNm();
			// 数据集英文名称校验
			if (!dsmp.containsKey(tableEnNm)) {
				list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
						.getExcelRowNo(), 4, "tableEnNm", "数据集物理名称",
						"数据集物理名称错误", "不存在该数据集", null, ""));
			} else {
				List<RptSysModuleCol> cols = dsmp.get(tableEnNm);
				boolean flag = false;
				for (Iterator<RptSysModuleCol> colIter = cols.iterator(); colIter.hasNext();) {
					RptSysModuleCol rptSysModuleCol = colIter.next();
					String enNm = rptSysModuleCol.getEnNm();
					if (vo.getEnNm().equals(enNm)) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
							.getExcelRowNo(), 6, "enNm", "数据项物理名称", "数据项校验",
							"数据项不存在", null, ""));
				}
			}
			// 报表名称校验
			String rptNum = vo.getRptNum();
			if (!rptMap.containsKey(rptNum)) {
				list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
						.getExcelRowNo(), 1, "rptNm", "报表编号", "报表校验", "此报表不存在",
						null, ""));
			}

			// 维度名称校验
			String dimTypeNm = vo.getDimTypeNm();
			if ("02".equals(vo.getDataItemType())) {
				if (!dimMap.containsKey(dimTypeNm)) {
					list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
							.getExcelRowNo(), 8, "dimTypeNm", "维度名称", "维度校验",
							"没有该名称的维度", null, ""));
				}
				String validKey = decodeKey(vo.getRptNm(), vo.getTableEnNm(),
						vo.getDimTypeNm());
				if (validDim.containsKey(validKey)) {
					RptrelImportVO relVO = validDim.get(validKey);
					list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
							.getExcelRowNo(), 8, "dimTypeNm", "维度名称", "维度校验",
							"维度名称[" + vo.getDimTypeNm() + "]和数据项["
									+ relVO.getCnNm() + "]的维度名称冲突", null, ""));
				} else {
					validDim.put(validKey, vo);
				}
			}
			// if ("02".equals(vo.getDataItemType()) &&
			// !dimMap.containsKey(dimTypeNm)) {
			// list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
			// .getExcelRowNo(), 8, "dimTypeNm", "维度名称", "维度校验",
			// "没有该名称的维度", null, ""));
			// String validKey = decodeKey(vo.getRptNm(), vo.getTableEnNm(),
			// vo.getEnNm(), vo.getDimTypeNm());
			// if (validDim.containsKey(validKey)) {
			// RptrelImportVO relVO = validDim.get(validKey);
			// list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
			// .getExcelRowNo(), 8, "dimTypeNm", "维度名称", "维度校验",
			// "维度名称和数据项[" + relVO.getSetNm() + "]的维度名称冲突", null, ""));
			// } else {
			// validDim.put("validKey", vo);
			// }
			// }

			if ("01".equals(vo.getDataItemType())) {
				// 指标校验
				if (StringUtils.isEmpty(vo.getIndexNo())) {
					list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
							.getExcelRowNo(), 9, "index", "指标", "指标校验", "指标为空",
							null, ""));
				} else {
					Map<String, Object> params = Maps.newHashMap();
					List<String> indexList = new ArrayList<String>(indexNos);
					int index = indexList.size() / 1000;
					List<RptIdxInfo> idxLs = new ArrayList<RptIdxInfo>();
					for (int l = 0; l <= index; l++) {
						params.clear();
						params.put("indexNos",
								indexList
										.subList(
												1000 * l,
												(1000 * l + 1000) >= indexList
														.size() ? indexList
														.size()
														: (1000 * l + 1000)));
						idxLs.addAll(relDao.findIdx(params));
					}
					boolean flag = false;
					for (Iterator<RptIdxInfo> iter = idxLs.iterator(); iter.hasNext();) {
						RptIdxInfo rptIdxInfo = iter.next();
						if (vo.getIndexNo().equals(
								rptIdxInfo.getId().getIndexNo())) {
							flag = true;
							break;
						}
					}
					if (!flag) {
						list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
								.getExcelRowNo(), 9, "index", "指标", "指标校验",
								"指标不存在", null, ""));
					}
				}

				// 过滤校验
				if (StringUtils.isEmpty(vo.getIdxFilter())) {
					// list.add(new ValidErrorInfoObj(vo.getSheetName(), vo
					// .getExcelRowNo(), 10, "filter", "过滤条件", "指标过滤条件校验",
					// "过滤条件不能为空", null, null));
				} else {
					Map<String, Object> ifd = vo.getIdxFilterDetail();
					for (Iterator<String> iter = ifd.keySet().iterator(); iter
							.hasNext();) {
						String key = iter.next();
						String[] val = (String[]) ifd.get(key);
						for (int j = 0; j < val.length; j++) {
							String value = val[j];
							Object info = filtMap.get(decodeKey(key, value));
							if (info == null) {
								list.add(new ValidErrorInfoObj(vo
										.getSheetName(), vo.getExcelRowNo(),
										10, "filter", "过滤条件", "指标过滤条件校验",
										"过滤条件[" + key + ":" + value + "]不存在",
										null, null));
							}
						}
					}
				}
			}
		}
		return list;
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false)
	public void saveData(String ehcacheId, String dsId) {
		List<RptrelImportVO> list = (List<RptrelImportVO>) EhcacheUtils.get(
				BioneSecurityUtils.getCurrentUserId(), ehcacheId);
		Set<String> rptIds = Sets.newLinkedHashSet(), datasetEnNames = Sets
				.newLinkedHashSet(), dataItemEnNames = Sets.newLinkedHashSet(), dimNames = Sets
				.newLinkedHashSet(), dimTypeNames = Sets.newLinkedHashSet();
		Map<String, Set<String>> sDim = Maps.newHashMap();
		Iterator<RptrelImportVO> iter = list.iterator();
		RptSysModuleCatalog cata = new RptSysModuleCatalog();
		String catalogId = RandomUtils.uuid2();
		cata.setCatalogId(catalogId);
		cata.setUpId("0");
		StringBuilder stb = new StringBuilder(500);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		stb.append("报表模型 ").append(sdf.format(new Date()));
		cata.setCatalogNm(stb.toString());
		stb.setLength(0);
		cata.setCatalogDesc("Excel导入默认目录");
		Set<String> sets = Sets.newHashSet();
		while (iter.hasNext()) {
			RptrelImportVO vo = iter.next();
			sets.add(vo.getRptNm());
			String rptNum = vo.getRptNum();
			if (StringUtils.isNotEmpty(rptNum)) {
				rptIds.add(rptNum);
			}
			String datasetEnName = vo.getTableEnNm();
			if (StringUtils.isNotEmpty(datasetEnName)) {
				datasetEnNames.add(datasetEnName);
			}
			String dataItemEnName = vo.getEnNm();
			if (StringUtils.isNotEmpty(dataItemEnName)) {
				dataItemEnNames.add(dataItemEnName);
			}
			String type = vo.getDataItemType();
			String dimName = vo.getDimTypeNm();
			if ("02".equals(type) && StringUtils.isNotEmpty(dimName)) {
				dimNames.add(dimName);
				dimTypeNames.add(dimName);
			}
			if (vo.getIdxFilterDetail() != null) {
				for (Iterator<String> fiter = vo.getIdxFilterDetail().keySet()
						.iterator(); fiter.hasNext();) {
					String key = fiter.next();
					String[] value = (String[]) vo.getIdxFilterDetail()
							.get(key);
					Set<String> sDimItems = sDim.get(key);
					if (sDim.containsKey(key) && sDimItems != null) {
						sDimItems.addAll(Arrays.asList(value));
					} else {
						Set<String> set = Sets.newHashSet();
						set.addAll(Arrays.asList(value));
						sDim.put(key, set);
					}
					dimTypeNames.add(key);
					dimNames.addAll(Arrays.asList(value));
				}
			}
		}
		Map<String, List<RptSysModuleCol>> dsmp = setBs.getFieldsOfTableList(
				dsId, new ArrayList<String>(datasetEnNames));
		Map<String, Object> reqMap = getRequireInfo(rptIds, datasetEnNames,
				dataItemEnNames, dimTypeNames, dimNames, sDim);
		Map<String, Object> rpt = (Map<String, Object>) reqMap.get("rpt");
		Map<String, Object> dataset = (Map<String, Object>) reqMap
				.get("dataset");
		Map<String, Object> dataitem = (Map<String, Object>) reqMap
				.get("dataitem");
		Map<String, Object> dim = (Map<String, Object>) reqMap.get("dim");
		Map<String, Object> dimItems = (Map<String, Object>) reqMap.get("sDim");
		iter = list.iterator();
		Set<RptMgrModuleRel> moduleRels = Sets.newHashSet();
		Set<RptMgrModuleIdxRel> moduleIdxRels = Sets.newHashSet();
		Set<RptMgrIdxFilter> filters = Sets.newHashSet();
		Set<RptSysModuleInfo> sModuleInfo = Sets.newHashSet();
		Set<RptSysModuleCol> sModuleCol = Sets.newHashSet();
		Set<RptSysModuleCatalog> sCatalog = Sets.newHashSet();
		while (iter.hasNext()) {
			RptrelImportVO vo = iter.next();
			RptMgrReportInfo rptInfo = (RptMgrReportInfo) rpt.get(vo
					.getRptNum());
			RptSysModuleInfo moudle = (RptSysModuleInfo) dataset.get(vo
					.getTableEnNm());
			RptSysModuleCol col = null;
			List<RptSysModuleCol> dscol = dsmp.get(vo.getTableEnNm());
			if (dscol != null) {
				if (moudle == null) {
					moudle = new RptSysModuleInfo();
					moudle.setCatalogId(catalogId);
					moudle.setSetId(RandomUtils.uuid2());
					moudle.setSetType("04");
					moudle.setSourceId(dsId);
					moudle.setSetNm(vo.getSetNm());
					moudle.setTableEnNm(vo.getTableEnNm());
					moudle.setRemark("Excel报表导入");
					dataset.put(moudle.getTableEnNm(), moudle);
					sModuleInfo.add(moudle);
					sCatalog.add(cata);
				} else if (!StringUtils.equals(vo.getSetNm(), moudle.getSetNm())) {
					moudle.setSetNm(vo.getSetNm());
					sModuleInfo.add(moudle);
				}
				col = (RptSysModuleCol) dataitem.get(decodeKey(moudle.getSetId(), vo.getEnNm()));
				if (col != null) {
					for (int i = 0; i < dscol.size(); i++) {
						RptSysModuleCol mc = dscol.get(i);
						if (mc != null && vo.getEnNm() != null
								&& vo.getEnNm().equals(mc.getEnNm())) {
							RptSysModuleCol nCol = new RptSysModuleCol();
							BeanUtils.copy(mc, nCol);
							nCol.setColId(col.getColId());
							nCol.setSetId(col.getSetId());
							RptDimTypeInfo typeInfo = (RptDimTypeInfo) dim.get(vo.getDimTypeNm());
							if (typeInfo != null) {
								nCol.setDimTypeNo(typeInfo.getDimTypeNo());
							}
							nCol.setColType(vo.getDataItemType());
							col = nCol;
							sModuleCol.add(col);
							break;
						}
					}
				}
				if (col == null) {
					for (int i = 0; i < dscol.size(); i++) {
						RptSysModuleCol mc = dscol.get(i);
						if (mc != null && vo.getEnNm() != null
								&& vo.getEnNm().equals(mc.getEnNm())) {
							RptSysModuleCol nCol = new RptSysModuleCol();
							BeanUtils.copy(mc, nCol);
							nCol.setColId(RandomUtils.uuid2());
							nCol.setSetId(moudle.getSetId());
							RptDimTypeInfo typeInfo = (RptDimTypeInfo) dim.get(vo.getDimTypeNm());
							if (typeInfo != null) {
								nCol.setDimTypeNo(typeInfo.getDimTypeNo());
							}
							nCol.setColType(vo.getDataItemType());
							col = nCol;
							sModuleCol.add(nCol);
							break;
						}
					}
				} else if (!StringUtils.equals(vo.getCnNm(), col.getCnNm())) {
					col.setCnNm(vo.getCnNm());
					sModuleCol.add(col);
				}
//				if ("02".equals(vo.getDataItemType()) && !"02".equals(col.getColType())) {
//					col.setColType("02");
//					sModuleCol.add(col);
//				}
//				if (col.getDimTypeNo() == null && vo.getDimTypeNm() != null) {
//					RptDimTypeInfo typeInfo = (RptDimTypeInfo) dim.get(vo.getDimTypeNm());
//					if (typeInfo != null) {
//						col.setDimTypeNo(typeInfo.getDimTypeNo());
//						sModuleCol.add(col);
//					}
//				}
			} else {
				break;
			}
			Map<String, Object> filterDetails = vo.getIdxFilterDetail();
			
			if ("01".equals(vo.getDataItemType())) {
				RptMgrModuleRel moduleRel = new RptMgrModuleRel();
				RptMgrModuleRelPK relPk = new RptMgrModuleRelPK();
				relPk.setRptId(rptInfo.getRptId());
				relPk.setSetId(moudle.getSetId());
				moduleRel.setId(relPk);
				moduleRels.add(moduleRel);

				RptMgrModuleIdxRel moduleIdxRel = new RptMgrModuleIdxRel();
				RptMgrModuleIdxRelPK idxPk = new RptMgrModuleIdxRelPK();
				idxPk.setRptId(rptInfo.getRptId());
				idxPk.setSetId(moudle.getSetId());
				idxPk.setIndexNo(vo.getIndexNo());
				idxPk.setColId(col.getColId());
				moduleIdxRel.setId(idxPk);
				if (filterDetails != null) {
					StringBuilder filterFormula = new StringBuilder(1000);
					for (Iterator<String> mIter = filterDetails.keySet().iterator(); mIter
							.hasNext();) {
						String key = mIter.next();
						String[] vals = (String[]) filterDetails.get(key);
						if (filterFormula.length() > 0) {
							filterFormula.append(" && ");
						}
						filterFormula.append("(");
						if (vals.length > 0) {
							for (int i = 0; i < vals.length; i++) {
								if (i > 0) {
									filterFormula.append("||");
								}
								String val = vals[i];
								RptDimItemInfo dimItemInfo = (RptDimItemInfo) dimItems.get(decodeKey(key, val));
								if (dimItemInfo != null) {
									filterFormula.append("$").append(dimItemInfo.getId().getDimTypeNo())
									.append("==").append("'").append(dimItemInfo.getId().getDimItemNo())
									.append("'");
								}
							}
						}
						filterFormula.append(")");
					}
					moduleIdxRel.setFilterFormula(filterFormula.toString());
				}
				if (moduleIdxRels.contains(moduleIdxRel) && StringUtils.isNotEmpty(moduleIdxRel.getFilterFormula())) {
					for (Iterator<RptMgrModuleIdxRel> iterator = moduleIdxRels.iterator(); iterator
							.hasNext();) {
						RptMgrModuleIdxRel c = iterator.next();
						if (c.equals(moduleIdxRel)) {
							StringBuilder st = new StringBuilder(1000);
							st.append(c.getFilterFormula()).append(" && ").append(moduleIdxRel.getFilterFormula());
							c.setFilterFormula(st.toString());
						}
					}
				}
				moduleIdxRels.add(moduleIdxRel);
			}

			if (filterDetails != null) {
				for (Iterator<String> mIter = filterDetails.keySet().iterator(); mIter
						.hasNext();) {
					String key = mIter.next();
					String[] value = (String[]) filterDetails.get(key);
					RptMgrIdxFilter filter = new RptMgrIdxFilter();
					RptMgrIdxFilterPK filterPk = new RptMgrIdxFilterPK();
					filterPk.setRptId(rptInfo.getRptId());
					filterPk.setSetId(moudle.getSetId());
					filterPk.setIndexNo(vo.getIndexNo());
					filterPk.setColId(col.getColId());
					RptDimTypeInfo dimTypeE = (RptDimTypeInfo) dim.get(key);
					filterPk.setDimNo(dimTypeE != null ? dimTypeE
							.getDimTypeNo() : null);
					if (value != null && value.length > 0) {
						for (int i = 0; i < value.length; i++) {
							String dimItemNm = value[i];
							RptDimItemInfo dimItemInfo = (RptDimItemInfo) dimItems
									.get(decodeKey(key, dimItemNm));
							if (dimItemInfo != null) {
								if (stb.length() > 0) {
									stb.append(',');
								}
								stb.append(dimItemInfo.getId().getDimItemNo());
							}
						}
					}
					filter.setFilterMode("in");
					filter.setFilterVal(stb.toString());
					stb.setLength(0);
					filter.setId(filterPk);
					filters.add(filter);
				}
			}
		}
		List<String> fields=new ArrayList<String>();
		fields.add("id.rptId");
		this.excelBS.deleteEntityJdbcBatch(sModuleInfo, null);
		this.excelBS.deleteEntityJdbcBatch(sModuleCol, null);
		this.excelBS.deleteEntityJdbcBatch(moduleRels, fields);
		this.excelBS.deleteEntityJdbcBatch(filters, fields);
		this.excelBS.deleteEntityJdbcBatch(moduleIdxRels, fields);
		this.excelBS.saveEntityJdbcBatch(sCatalog);
		this.excelBS.saveEntityJdbcBatch(sModuleInfo);
		this.excelBS.saveEntityJdbcBatch(sModuleCol);
		this.excelBS.saveEntityJdbcBatch(moduleRels);
		this.excelBS.saveEntityJdbcBatch(filters);
		this.excelBS.saveEntityJdbcBatch(moduleIdxRels);
	}

	@Override
	public String export(String ids) {
		String fileName = "";
		Map<String, Object> params = Maps.newHashMap();
		List<RptrelImportVO> list = new ArrayList<RptrelImportVO>();
		List<String> rptIdLists = ArrayUtils.asList(ids, ",");
		int index = rptIdLists.size() / 1000;
		for (int i = 0; i <= index; i++) {
			params.clear();
			params.put("rptIds", rptIdLists.subList(1000 * i,
					(1000 * i + 1000) >= rptIdLists.size() ? rptIdLists.size()
							: (1000 * i + 1000)));
			list.addAll(relDao.getExcelVO(params));
		}
		Iterator<RptrelImportVO> iter = list.iterator();
		Map<String, Object> mDimItem = Maps.newHashMap();
		Set<String> dimTypeNos = Sets.newHashSet();
		while (iter.hasNext()) {
			RptrelImportVO vo = iter.next();
			if (StringUtils.isNotEmpty(vo.getFilterVal())) {
				dimTypeNos.add(vo.getDimNo());
			}
		}
		params.clear();
		params.put("dimTypeNos",
				ReBuildParam.toDbList(new ArrayList<String>(dimTypeNos)));
		List<RptDimItemInfo> items = relDao.findDimItem(params);
		for (Iterator<RptDimItemInfo> it = items.iterator(); it.hasNext();) {
			RptDimItemInfo info = it.next();
			mDimItem.put(
					decodeKey(info.getId().getDimTypeNo(), info.getId()
							.getDimItemNo()), info);
		}
		iter = list.iterator();
		StringBuilder stb = new StringBuilder();
		while (iter.hasNext()) {
			stb.setLength(0);
			RptrelImportVO vo = iter.next();
			if (StringUtils.isEmpty(vo.getFilterVal())) {
				continue;
			}
			stb.append(vo.getDimNm()).append(':');
			String[] vals = StringUtils.split(vo.getFilterVal(), ',');
			for (int i = 0; i < vals.length; i++) {
				RptDimItemInfo info = (RptDimItemInfo) mDimItem.get(decodeKey(
						vo.getDimNo(), vals[i]));
				if (info != null) {
					if (i > 0) {
						stb.append(",");
					}
					stb.append(info.getDimItemNm());
				}
			}
			vo.setIdxFilter(stb.toString());
		}
		List<List<?>> ls = Lists.newArrayList();
		ls.add(list);
		XlsExcelTemplateExporter fe = null;
		try {
			fileName = this.getRealPath() + GlobalConstants4plugin.DESIGN_EXPORT_PATH
					+ File.separator + RandomUtils.uuid2() + ".xls";
			fe = new XlsExcelTemplateExporter(fileName, this.getRealPath()
					+ GlobalConstants4plugin.DESIGN_EXPORT_PATH + File.separator
					+ GlobalConstants4plugin.EXPORT_RPTREL_TEMPLATE_PATH, ls);
			fe.run();
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
