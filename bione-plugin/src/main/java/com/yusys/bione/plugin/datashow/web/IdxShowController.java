package com.yusys.bione.plugin.datashow.web;

import static com.yusys.bione.frame.base.common.GlobalConstants4frame.APP_CONTEXT_PATH;
import static com.yusys.bione.frame.base.common.GlobalConstants4frame.ICON_URL;
import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.RPT_IDX_FILTER_MODE_IN;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.utils.DownloadUtils;
import com.yusys.bione.comp.utils.EhcacheUtils;
import com.yusys.bione.comp.utils.EncodeUtils;
import com.yusys.bione.comp.utils.FilepathValidateUtils;
import com.yusys.bione.comp.utils.PropertiesUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.plugin.base.common.GlobalConstants4plugin;
import com.yusys.bione.plugin.base.utils.CommandRemote;
import com.yusys.bione.plugin.base.utils.CommandRemote.CommandRemoteType;
import com.yusys.bione.plugin.datamodel.service.RptDatasetBS;
import com.yusys.bione.plugin.base.utils.WebUtils;
import com.yusys.bione.plugin.datashow.service.IdxShowBS;
import com.yusys.bione.plugin.datashow.web.vo.IdxBaseInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.IdxDimInfoVO;
import com.yusys.bione.plugin.datashow.web.vo.IdxStoreInfoVO;
import com.yusys.bione.plugin.design.util.IdxFormulaUtils;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolderInsRel;
import com.yusys.bione.plugin.rptfav.entity.RptFavFolderInsRelPK;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDetail;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDim;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimFilter;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimFilterPK;
import com.yusys.bione.plugin.rptfav.entity.RptFavIdxDimPK;
import com.yusys.bione.plugin.rptfav.entity.RptFavQueryins;
import com.yusys.bione.plugin.rptfav.service.RptMyQueryBS;
import com.yusys.bione.plugin.rptidx.entity.RptIdxCalcRule;
import com.yusys.bione.plugin.rptidx.entity.RptIdxInfo;
import com.yusys.bione.plugin.rptidx.entity.RptIdxTimeMeasure;
import com.yusys.bione.plugin.rptidx.entity.RptIdxValType;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
import com.yusys.bione.plugin.rptidx.service.RptIdxDsRelBS;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import com.yusys.bione.plugin.spreadjs.utils.ExcelExportUtil;

@Controller
@RequestMapping("/report/frame/datashow/idx")
public class IdxShowController extends BaseController {
	@Autowired
	private IdxShowBS idxBS;

	@Autowired
	private RptIdxDsRelBS idxDsRelBs;

	@Autowired
	private RptOrgInfoBS rptOrgBs;

	@Autowired
	private RptMyQueryBS queryBS;
	@Autowired
	private IdxInfoBS idxInfoBs;

	@Autowired
	private UserBS userBS;

	@Autowired
	private RptDatasetBS rptDatasetBS;
	
	@RequestMapping(method = RequestMethod.GET)
	@SuppressWarnings("unchecked")
	public ModelAndView index(HttpServletRequest request) {
		String rootIconPath = APP_CONTEXT_PATH + "/" + ICON_URL
				+ "/application_side_tree.png";
		ModelMap mm = new ModelMap();
		mm.addAttribute("rootIconPath", rootIconPath);
		Map<String, String[]> map = request.getParameterMap();
		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
			String key = iter.next();
			String[] val = map.get(key);
			if (val != null && val.length > 0) {
				mm.put(key, StringUtils2.javaScriptEncode(EncodeUtils.urlDecode(val[0] != null ? val[0] : "")));
			}
		}
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		List<String> defSrcList = idxBS.getUserIdxDefSrc(user);
		if (defSrcList != null && defSrcList.size() > 0
				&& defSrcList.get(0) == null) {
			defSrcList.set(0, "01");
		}
		mm.put("defSrc", defSrcList);
		BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
		userInfo.setUserNo(userBS.getUserNo(BioneSecurityUtils
				.getCurrentUserId()));
		mm.put("userInfo", JSON.toJSONString(userInfo));
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> list = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
			if (list != null && list.size() > 0){
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(),
						"resIndexNo", list);
			}else{
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(),"resIndexNo", new HashMap<String, String>());
			}
		}

		String moneyUnit = GlobalConstants4plugin.INDEX_QUERY_SPAN;
		List<CommonComboBoxNode> indexQuerySpan = this.idxBS
				.getParamMoneyListByParams(moneyUnit);
		if (indexQuerySpan != null && indexQuerySpan.size() > 0) {
			mm.put("indexQuerySpan", StringUtils2.javaScriptEncode(indexQuerySpan.get(0).getId()));
		}
		String orgNo = BioneSecurityUtils.getCurrentUserInfo().getOrgNo();
		List<RptOrgInfo> orgs = new ArrayList<RptOrgInfo>();
		if (StringUtils.isNotBlank(orgNo)) {
			//20170913 maojin 加注释 数据库数据有问题
			orgs = this.rptOrgBs.getOrgByNos(orgNo);
			/*RptOrgInfo org = this.idxBS.getEntityByProperty(
					RptOrgInfo.class, "id.orgNo", orgNo);
			if (org != null)
				orgs.add(org);*/
		}
		mm.put("orgs", StringUtils2.javaScriptEncode(JSON.toJSONString(orgs)));
		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-index", mm);
	}

	@RequestMapping("/search")
	public ModelAndView search() {
		return new ModelAndView("/plugin/datashow/idxshow/idx-show-search");
	}

	@RequestMapping("/detail")
	public ModelAndView detail(String idxNo, String verId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("idxNo", StringUtils2.javaScriptEncode(idxNo));
		mm.addAttribute("verId", StringUtils2.javaScriptEncode(verId));
		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-detail", mm);
	}

	@RequestMapping(value = "/newConf")
	public ModelAndView newConf(String indexNo, String type) {
		ModelMap mm = new ModelMap();
		mm.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.put("type", StringUtils2.javaScriptEncode(type));
		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-filter", mm);
	}

	@RequestMapping(value = "/show")
	public ModelAndView show(String instanceId) {
		instanceId = StringUtils2.javaScriptEncode(instanceId);
		return new ModelAndView("/plugin/datashow/idxshow/idx-show-show",
				"instanceId", instanceId);
	}

	@RequestMapping(value = "/dimInfo", method = RequestMethod.GET)
	public ModelAndView dimInfo(String dimTypeNo, String dimTypeStruct) {
		ModelMap map = new ModelMap();
		map.put("dimTypeNo", StringUtils2.javaScriptEncode(dimTypeNo));
		map.put("dimTypeStruct", StringUtils2.javaScriptEncode(dimTypeStruct));
		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-dim-tree", map);
	}

	@RequestMapping(value = "/detail/info.*", method = RequestMethod.POST)
	@ResponseBody
	public IdxBaseInfoVO detailInfo(String idxNo, String verId) {
		return idxBS.getIdxBaseInfo(idxNo, verId);
	}

	@RequestMapping("/getDrawDate")
	@ResponseBody
	public Map<String, Object> getDrawDate(String indexNo) {
		Map<String,Object> res = new HashMap<String, Object>();
		List<RptIdxInfo> idx = this.idxBS.getEntityListByProperty(RptIdxInfo.class, "id.indexNo", indexNo, "id.indexVerId", true);
		String cycle = "01";
		if(idx.size()>0){
			cycle = idx.get(0).getCalcCycle();
		}
		res.put("drawDate", this.idxBS.getIdxDrawDate(indexNo,cycle));
		return res;
	}

	@RequestMapping("/grid")
	@ResponseBody
	public Map<String, Object> idxGrid(String idxNo, String verId) {
		Map<String, Object> map = Maps.newHashMap();
		List<IdxDimInfoVO> dimList = idxBS.findIdxDim(idxNo, verId);

		List<IdxDimInfoVO> newDimList = new ArrayList<IdxDimInfoVO>();
		Map<String, IdxDimInfoVO> dimMap = new HashMap<String, IdxDimInfoVO>();

		map.put("idx", idxBS.getIdxBaseInfo(idxNo, verId));
		if (dimList != null && dimList.size() > 0) {
			for (IdxDimInfoVO vo : dimList) {
				if (dimMap.get(vo.getDimTypeNo()) == null) {
					String dsIds = "";
					for (IdxDimInfoVO y : dimList) {
						if (vo.getDimTypeNo().equals(y.getDimTypeNo())) {
							dsIds += y.getDsId() + ",";
						}
					}
					if (dsIds.endsWith(",")) {
						dsIds = dsIds.substring(0, dsIds.length() - 1);
					}
					vo.setDsId(dsIds);
					dimMap.put(vo.getDimTypeNo(), vo);

					if (vo.getDimType().equals(
							GlobalConstants4plugin.DIM_TYPE_CURRENCY)) {
						map.put("hasCurrency", true);
					}
				}
			}
		}
		if (dimMap.keySet() != null) {
			for (String tmp : dimMap.keySet()) {
				newDimList.add(dimMap.get(tmp));
			}
		}
		map.put("dim", newDimList);

		if (map.get("hasCurrency") == null) {
			map.put("hasCurrency", false);
		}

		return map;
	}

	@RequestMapping("/conf")
	public ModelAndView conf(String idxNo, String verId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("idxNo", StringUtils2.javaScriptEncode(idxNo));
		mm.addAttribute("verId", StringUtils2.javaScriptEncode(verId));
		return new ModelAndView("/plugin/datashow/idxshow/idx-show-conf",
				mm);
	}

	@RequestMapping("/config")
	public ModelAndView config(String id, String indexNo, String indexVerId) {
		ModelMap mm = new ModelMap();
		mm.addAttribute("id", StringUtils2.javaScriptEncode(id));
		mm.addAttribute("indexNo", StringUtils2.javaScriptEncode(indexNo));
		mm.addAttribute("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-config", mm);
	}

	@RequestMapping("/conf/dim.*")
	@ResponseBody
	public List<Map<String, Object>> confDim(String idxNo, String verId) {
		return idxBS.findDimInfo(idxNo, verId);
	}

	@RequestMapping("/store")
	public ModelAndView store() {
		return new ModelAndView("/plugin/datashow/idxshow/idx-show-store");
	}

	/**
	 * 收藏树
	 * 
	 * @param param
	 * @return
	 */
	@RequestMapping("/store/node.*")
	@ResponseBody
	public List<CommonTreeNode> treeStoreNode(String param) {
		return queryBS.getStoreTree(param,GlobalConstants4plugin.INST_TYPE_IDX);
	}

	@RequestMapping(value = "/store/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> createQueryIns(String query) {
		Map<String, Object> result = new HashMap<String, Object>();
		JSONObject jo = JSON.parseObject(query);
		String iId = RandomUtils.uuid2();
		String uId = BioneSecurityUtils.getCurrentUserId();
		Timestamp createTime = new Timestamp(System.currentTimeMillis());
		RptFavQueryins ins = jo.getJSONObject("ins").toJavaObject(RptFavQueryins.class);
		ins.setCreateTime(createTime);
		ins.setCreateUser(uId);
		ins.setInstanceId(iId);
		RptFavFolderInsRel rel = new RptFavFolderInsRel();
		rel.setInstanceType(GlobalConstants4plugin.INST_TYPE_IDX);
		RptFavFolderInsRelPK relPk = jo.getJSONObject("folder").toJavaObject(RptFavFolderInsRelPK.class);
		relPk.setInstanceId(iId);
		relPk.setUserId(uId);
		rel.setId(relPk);

		JSONArray dimNos = jo.getJSONArray("searchArg");
		List<RptFavIdxDim> dimList = new ArrayList<RptFavIdxDim>();
		if (dimNos != null) {
			int i = 0;
			for (int k = 0; k < dimNos.size(); k ++) {
				JSONObject ob = dimNos.getJSONObject(k);
				RptFavIdxDim dim = new RptFavIdxDim();
				RptFavIdxDimPK dimPk = new RptFavIdxDimPK();
				dimPk.setDimNo(ob.getString("dimNo"));
				dimPk.setInstanceId(iId);
				dimPk.setOrderNum(new Long(++i));
				dim.setId(dimPk);
				dimList.add(dim);
			}
		}

		JSONArray colums = jo.getJSONArray("colums");
		List<RptFavIdxDetail> detailList = new ArrayList<RptFavIdxDetail>();
		List<RptFavIdxDimFilter> filterList = new ArrayList<RptFavIdxDimFilter>();

		if (colums != null) {
			int i = 0;
			for (int k = 0; k < colums.size(); k ++) {
				JSONObject object = colums.getJSONObject(k);
				RptFavIdxDetail detail = new RptFavIdxDetail();
				String detailId = RandomUtils.uuid2();
				detail.setDetailId(detailId);
				if (object.get("ruleId") != null) {
					detail.setRuleId(object.get("ruleId").toString());
				}
				if (object.get("timeMeasureId") != null) {
					detail.setTimeMeasureId(object.get("timeMeasureId")
							.toString());
				}
				if (object.get("modeId") != null) {
					detail.setModeId(object.get("modeId").toString());
				}
				if (object.get("dataType") != null) {
					detail.setDataType(object.get("dataType").toString());
				}
				if (object.get("dataUnit") != null) {
					detail.setDataUnit(object.get("dataUnit").toString());
				}
				if (object.get("dataPrecision") != null) {
					detail.setDataPrecision(new BigDecimal(object.get(
							"dataPrecision").toString()));
				}
				if (object.get("indexAlias") != null) {
					detail.setIndexAlias(object.get("indexAlias").toString());
				}
				if (object.get("isPassyear") != null) {
					detail.setIsPassyear(object.get("isPassyear").toString());
				}
				detail.setIndexNo(object.getString("indexNo"));
				if (object.get("measureNo") != null
						&& !StringUtils.isEmpty(object.get("measureNo")
								.toString())
						&& !object.get("measureNo").toString().equals("null")) {
					detail.setMeasureNo(object.get("measureNo").toString());
				}
				detail.setInstanceId(iId);
				detail.setOrderNum(new BigDecimal(++i));
				detailList.add(detail);

				JSONArray dimNosInner = jo.getJSONArray("searchArg");
				if (dimNos != null) {
					for (int m = 0; m < dimNosInner.size(); m ++) {
						JSONObject ob = dimNosInner.getJSONObject(m);
						RptFavIdxDimFilter filter = new RptFavIdxDimFilter();
						RptFavIdxDimFilterPK filterPk = new RptFavIdxDimFilterPK();
						filterPk.setDetailId(detailId);
						filterPk.setDimNo(ob.getString("dimNo"));
						filter.setFilterMode(ob.getString("op"));
						String filterVal = "";
						if (ob.getString("op").equals(RPT_IDX_FILTER_MODE_IN)) {
							JSONArray ja = ob.getJSONArray("value");
							if (ja.size() == 0) {
								continue;
							}
							for (int n = 0; n < ja.size(); n ++) {
								if (StringUtils.isNotEmpty(filterVal)) {
									filterVal += ",";
								}
								filterVal += ja.getString(n);
							}
						} else {
							filterVal = ob.getString("value");
						}
						filter.setFilterVal(filterVal);
						filter.setId(filterPk);
						filterList.add(filter);
					}
				}
			}
		}
		this.idxBS.saveQuery(rel, ins, detailList, dimList, filterList);
		result.put("msg", "ok");
		return result;
	}

	/**
	 * 跳转到选择机构页面 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/chooseOrg", method = RequestMethod.GET)
	public ModelAndView chooseOrg(String checkbox) {
		checkbox = StringUtils2.javaScriptEncode(checkbox);
		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-choose-org", "checkbox", checkbox);
	}

	/**
	 * 跳转到选择币种页面 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/chooseCurrency", method = RequestMethod.GET)
	public ModelAndView chooseCurrency() {
		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-choose-currency");
	}

	/**
	 * 维度机构树 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getOrgTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getOrgTree(String id, String searchName,
			String srcCode,String orgType) {
		return this.rptOrgBs.getAuthOrgTree(id, searchName,orgType);

	}

	@RequestMapping(value = "/orgTree")
	@ResponseBody
	public List<CommonTreeNode> orgTree(String id, String searchName,
			String srcCode,String orgType) {
		return this.rptOrgBs.getAuthOrgTree(id, searchName,orgType);

	}

	/**
	 * 维度机构树 方娟
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getCurrencyTree", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getCurrencyTree() {
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		return this.idxDsRelBs.getDimInfoTree(
				propertiesUtils.getProperty("currencyDimTypeNo"),
				this.getContextPath());

	}

	@SuppressWarnings("unchecked")
	public void setOrgValidate( Map<String, Object> map) {
		List<String> dimNos = (List<String>) map.get("DimNo");
		List<String> validateOrgs = new ArrayList<String>();
		if (BioneSecurityUtils.getCurrentUserInfo() != null
				&& !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
			Map<String, List<String>> validateMap = this.rptOrgBs.getValidateOrgInfo();
			validateOrgs = validateMap.get("validate");
		}
		if (!dimNos.contains("ORG")) {
			List<Map<String, Object>> searchs = new ArrayList<Map<String, Object>>();
			Map<String, Object> search = new HashMap<String, Object>();
			search.put("DimNo", "ORG");
			search.put("Op", "IN");
			search.put("Value", validateOrgs);
			searchs.add(search);
			map.put("SearchArg", searchs);
		} else {
			List<Map<String, Object>> cols = (List<Map<String, Object>>) map.get("Colums");
			if (cols != null && cols.size() > 0) {
				for (Map<String, Object> col : cols) {
					List<Map<String, Object>> searchArgs = (List<Map<String, Object>>) col.get("SearchArg");
					boolean orgFlag = false;
					if (searchArgs != null && searchArgs.size() > 0) {
						for (Map<String, Object> searchArg : searchArgs) {
							if ("ORG".equals(searchArg.get("DimNo"))) {
								orgFlag = true;
								List<String> values = (List<String>) searchArg.get("Value");
								//目前前台已经做了机构权限的过滤，这个地方不需要再进行获取授权机构
/*								if (BioneSecurityUtils.getCurrentUserInfo() != null
										&& !BioneSecurityUtils.getCurrentUserInfo().isSuperUser()) {
									values = ListUtils.intersection(values, validateOrgs);
								}*/
								searchArg.put("Value", values);
							}
						}
					}
					if (!orgFlag) {
						Map<String, Object> search = new HashMap<String, Object>();
						search.put("DimNo", "ORG");
						search.put("Op", "IN");
						search.put("Value", validateOrgs);
						if (searchArgs == null) {
							searchArgs = new ArrayList<Map<String, Object>>();
						}
						searchArgs.add(search);
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void setFormula(Map<String, Object> map,
			Map<String, RptFavIdxDetail> detailMap,
			Map<String, RptIdxCalcRule> ruleMap,
			Map<String, RptIdxTimeMeasure> measureMap,
			Map<String, RptIdxValType> valMap) {
		List<Map<String, Object>> cols = (List<Map<String, Object>>) map
				.get("Colums");
		if (cols != null && cols.size() > 0) {
			for (Map<String, Object> col : cols) {
				String columnNo = col.get("ColumNo").toString();
				RptFavIdxDetail detail = detailMap.get(columnNo);
				if (detail != null) {
					String indexNo = detail.getIndexNo();
					if (StringUtils.isNotBlank(detail.getMeasureNo())) {
						indexNo += "." + detail.getMeasureNo();
					}
					String formula = IdxFormulaUtils
							.generateColCalcFormula(indexNo, ruleMap
									.get(detail.getRuleId()), measureMap
									.get(detail.getTimeMeasureId()), valMap
									.get(detail.getModeId()),"");
					if (StringUtils.isNotBlank(formula)) {
						Map<String, Object> Calculation = new HashMap<String, Object>();
						Calculation.put("Formula", formula);
						col.put("Calculation", Calculation);
					}

				}
			}
		}
	}

	@RequestMapping("/search/result")
	@ResponseBody
	public String searchResult(HttpServletRequest request, String parameter,
			String columns, String indexNos, String idxCols) {
		List<RptIdxCalcRule> rules = this.idxBS.getAllEntityList(
				RptIdxCalcRule.class, "ruleId", false);
		List<RptIdxTimeMeasure> measures = this.idxBS.getAllEntityList(
				RptIdxTimeMeasure.class, "timeMeasureId", false);
		List<RptIdxValType> vals = this.idxBS.getAllEntityList(
				RptIdxValType.class, "modeId", false);
		Map<String, RptIdxCalcRule> ruleMap = new HashMap<String, RptIdxCalcRule>();
		Map<String, RptIdxTimeMeasure> measureMap = new HashMap<String, RptIdxTimeMeasure>();
		Map<String, RptIdxValType> valMap = new HashMap<String, RptIdxValType>();
		if (rules != null && rules.size() > 0) {
			for (RptIdxCalcRule rule : rules) {
				ruleMap.put(rule.getRuleId(), rule);
			}
		}
		if (measures != null && measures.size() > 0) {
			for (RptIdxTimeMeasure measure : measures) {
				measureMap.put(measure.getTimeMeasureId(), measure);
			}
		}
		if (vals != null && vals.size() > 0) {
			for (RptIdxValType val : vals) {
				valMap.put(val.getModeId(), val);
			}
		}
		List<RptFavIdxDetail> details = new ArrayList<RptFavIdxDetail>();
		if (StringUtils.isNotBlank(idxCols)) {
			details = JSON.parseArray(idxCols, RptFavIdxDetail.class);
		}
		Map<String, RptFavIdxDetail> detailMap = new HashMap<String, RptFavIdxDetail>();
		if (details != null && details.size() > 0) {
			for (RptFavIdxDetail detail : details) {
				detailMap.put(detail.getDetailId(), detail);
			}
		}
		if (StringUtils.isNotBlank(idxCols)) {
			details = JSON.parseArray(idxCols, RptFavIdxDetail.class);
		}
		this.idxBS.saveIdxHis(indexNos, WebUtils.getRemortIP(request));
		Object obj;
		String returnJson = "";
		try {
			parameter = parameter.replace('@', '<');
			JSONObject map = JSON.parseObject(parameter);
			setOrgValidate(map);
			setFormula(map, detailMap, ruleMap, measureMap, valMap);
			parameter = JSON.toJSONString(map);
			obj = CommandRemote.sendSync(parameter, CommandRemoteType.QUERY);
			if (obj instanceof String)
				returnJson = obj.toString();
			else
				return "查询数据超时异常";
		} catch (Throwable e) {
			e.printStackTrace();
			return "查询数据超时异常";
		}
		return returnJson;
	}

	@RequestMapping("/search/frsResult")
	@ResponseBody
	public JSONObject searchFrsResult(HttpServletRequest request, String parameter,
			String columns, String indexNos, String idxCols) {
		List<RptIdxCalcRule> rules = this.idxBS.getAllEntityList(
				RptIdxCalcRule.class, "ruleId", false);
		List<RptIdxTimeMeasure> measures = this.idxBS.getAllEntityList(
				RptIdxTimeMeasure.class, "timeMeasureId", false);
		List<RptIdxValType> vals = this.idxBS.getAllEntityList(
				RptIdxValType.class, "modeId", false);
		Map<String, RptIdxCalcRule> ruleMap = new HashMap<String, RptIdxCalcRule>();
		Map<String, RptIdxTimeMeasure> measureMap = new HashMap<String, RptIdxTimeMeasure>();
		Map<String, RptIdxValType> valMap = new HashMap<String, RptIdxValType>();
		if (rules != null && rules.size() > 0) {
			for (RptIdxCalcRule rule : rules) {
				ruleMap.put(rule.getRuleId(), rule);
			}
		}
		if (measures != null && measures.size() > 0) {
			for (RptIdxTimeMeasure measure : measures) {
				measureMap.put(measure.getTimeMeasureId(), measure);
			}
		}
		if (vals != null && vals.size() > 0) {
			for (RptIdxValType val : vals) {
				valMap.put(val.getModeId(), val);
			}
		}
		List<RptFavIdxDetail> details = new ArrayList<RptFavIdxDetail>();
		if (StringUtils.isNotBlank(idxCols)) {
			details = JSON.parseArray(idxCols, RptFavIdxDetail.class);
		}
		Map<String, RptFavIdxDetail> detailMap = new HashMap<String, RptFavIdxDetail>();
		if (details != null && details.size() > 0) {
			for (RptFavIdxDetail detail : details) {
				detailMap.put(detail.getDetailId(), detail);
			}
		}
		if (StringUtils.isNotBlank(idxCols)) {
			details = JSON.parseArray(idxCols, RptFavIdxDetail.class);
		}
		this.idxBS.saveIdxHis(indexNos, WebUtils.getRemortIP(request));
		Object obj;
		String returnJson = "";
		try {
			parameter = parameter.replace('@', '<');
			JSONObject map = JSON.parseObject(parameter);
			setOrgValidate(map);
			setFormula(map, detailMap, ruleMap, measureMap, valMap);
			// 实现分成数据查询
			String archiveType = map.getString("ArchiveType");
			boolean QueryInit = false;
			if(GlobalConstants4plugin.ARCHIVE_TYPE_INIT_DATA.equals(archiveType)){ // 初始数据
				QueryInit = true;
				map.put("QueryInit", QueryInit);
				map.remove("ArchiveType");
			}else if(GlobalConstants4plugin.ARCHIVE_TYPE_FILL_DATA.equals(archiveType)){ // 填报数据
				map.put("QueryInit", QueryInit);
				map.remove("ArchiveType");
			}else if(GlobalConstants4plugin.ARCHIVE_TYPE_SUBMIT_DATA.equals(archiveType) 
					|| GlobalConstants4plugin.ARCHIVE_TYPE_IMPORT_DATA.equals(archiveType)){ // 归档数据或回灌数据
				map.put("QueryInit", QueryInit);
			}else if(GlobalConstants4plugin.ARCHIVE_TYPE_INIT_DATA_YUAN.equals(archiveType)){ // 初始数据(元)
				QueryInit = true;
				map.put("QueryInit", QueryInit);
				map.put("ValColumn", "RAW_INDEX_VAL");
				map.remove("ArchiveType");
			}else{
				map.put("QueryInit", QueryInit);
				map.remove("ArchiveType");
			}
			parameter = JSON.toJSONString(map);
			obj = CommandRemote.sendSync(parameter, CommandRemoteType.QUERY);
			if (obj instanceof String)
				returnJson = obj.toString();
			else
				return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
		return JSON.parseObject(returnJson);
	}
	
	@RequestMapping("/store/tree")
	@ResponseBody
	public List<CommonTreeNode> storeTree(String param) {
		return this.queryBS
				.getStoreTree(param, GlobalConstants4plugin.INST_TYPE_IDX);
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/resultExport")
	@ResponseBody
	public Map<String, Object> resultExport(HttpServletResponse response,
			String colums, String result, String formate, String unit) {
		Map<String, Object> res = new HashMap<String, Object>();
		File file = new File(this.getRealPath() + "/export/frame/idx/");
		if (!file.exists()) {
			file.mkdirs();
		}
		List<Map<String, Object>> results = new ArrayList<Map<String, Object>>();
		if (StringUtils.isNotBlank(result)) {
			results = (List<Map<String, Object>>)(List<?>)JSON.parseArray(result, Map.class);
		}
		ExcelExportUtil util = new ExcelExportUtil(response, results,
				colums, formate, unit, this.getRealPath()
						+ "/export/frame/idx/" + RandomUtils.uuid2() + ".xls");
		String fileName = util.createFile("指标查询");
		res.put("fileName", fileName);
		return res;
	}

	@RequestMapping(value = "download")
	public void download(String fileName, HttpServletResponse response) {
		if (FilepathValidateUtils.validateFilepath(fileName)) {
			File file = new File(fileName);
			try {
				DownloadUtils.download(response, file, "指标查询.xls");
			} catch (Exception e) {
				e.printStackTrace();
			}
			file.delete();
		}
	}

	@RequestMapping("/getStoreInfo")
	@ResponseBody
	public IdxStoreInfoVO getStoreInfo(String instanceId) {
		return this.idxBS.getStoreInfo(instanceId);
	}

	@RequestMapping("/getDim")
	@ResponseBody
	public List<RptDimTypeInfo> getDim(String indexNos, String indexVerIds) {
		return this.idxBS.getDim(indexNos, indexVerIds);
	}

	@RequestMapping(value = "/checkQueryNm")
	@ResponseBody
	public boolean checkQueryNm(String folderId, String queryNm) {
		return this.idxBS.checkQueryNm(folderId, queryNm);
	}

	@RequestMapping(value = "/showFav", method = RequestMethod.GET)
	public ModelAndView showFav(String instanceId) {
		instanceId = StringUtils2.javaScriptEncode(instanceId);
		return new ModelAndView("/plugin/datashow/idxshow/idx-fav-index",
				"instanceId", instanceId);
	}

	@RequestMapping("/store/favIdxInfo")
	@ResponseBody
	public Map<String, Object> storeFavIdxInfo(String instanceId) {
		return idxBS.getFavIdxInfo(instanceId);
	}

	@RequestMapping("/initInfo")
	@ResponseBody
	public Map<String, Object> initInfo() {
		return idxBS.getInitInfo();
	}

	// 选择金额单位
	@RequestMapping("/chooseMoney")
	@ResponseBody
	public List<CommonComboBoxNode> chooseMoney() {
		String moneyUnit = GlobalConstants4plugin.MONEY_UNIT_PARAMS;
		return this.idxBS.getParamMoneyListByParams(moneyUnit);
	}

	// 选择个数单位
	@RequestMapping("/chooseNumbers")
	@ResponseBody
	public List<CommonComboBoxNode> chooseNumbers() {
		String numberUnit = GlobalConstants4plugin.NUMBER_UNIT_PARAMS;
		return this.idxBS.getParamMoneyListByParams(numberUnit);
	}

	@RequestMapping("/tree/def/user")
	@ResponseBody
	public List<CommonTreeNode> userDefIdxTree(String id, String searchNm) {
		return idxBS.getIdxTreeByDefUser(
				BioneSecurityUtils.getCurrentUserInfo(), id, searchNm);
	}

	@RequestMapping("/tree/def/org")
	@ResponseBody
	public List<CommonTreeNode> orgDefIdxTree(String id, String searchNm) {
		return idxBS.getIdxTreeByDefOrg(
				BioneSecurityUtils.getCurrentUserInfo(), id, searchNm);
	}

	@RequestMapping("/dimFilter")
	public ModelAndView dimFilter(String dimNo) {
		RptDimTypeInfo info = this.idxBS.getDimTypeInfo(dimNo);
		Map<String, String> map = new HashMap<String, String>();
		map.put("dimTypeNo", StringUtils2.javaScriptEncode(dimNo));
		map.put("dimTypeStruct", StringUtils2.javaScriptEncode(info.getDimTypeStruct()));

		if (dimNo.equals(GlobalConstants4plugin.DIM_TYPE_DATE_NAME)) {
			return new ModelAndView(
					"/plugin/datashow/idxshow/idx-show-choose-date", map);
		} else if (dimNo.equals(GlobalConstants4plugin.DIM_TYPE_ORG_NAME)) {
			return new ModelAndView(
					"/plugin/datashow/idxshow/idx-show-choose-org", map);
		} else {
			return new ModelAndView(
					"/plugin/datashow/idxshow/idx-show-dim-filter", map);
		}
	}

	@RequestMapping("/measureFilter")
	public ModelAndView measureFilter(String indexNo, String indexVerId) {
		Map<String, String> map = new HashMap<String, String>();
		if (indexNo.indexOf("-") > 0) {

			map.put("measureNo",
					StringUtils2.javaScriptEncode(indexNo.substring(indexNo.indexOf("-") + 1,
							indexNo.length())));
			indexNo = indexNo.substring(0, indexNo.indexOf("-"));
		}

		RptIdxInfo info = this.idxBS.getIdxInfo(indexNo, indexVerId);

		map.put("dataType", StringUtils2.javaScriptEncode(info.getDataType()));
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));

		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-measure-filter", map);
	}

	@RequestMapping("/getDimNm")
	@ResponseBody
	public Map<String, String> getDimNm(String dimNos) {
		return this.idxBS.getDimNm(dimNos);
	}

	@RequestMapping("/chart")
	public ModelAndView chart(String chartType) {
		chartType = StringUtils2.javaScriptEncode(chartType);
		return new ModelAndView(
				"/plugin/datashow/idxshow/idx-show-chart", "chartType", chartType);
	}

	@RequestMapping("/getIdxRule")
	@ResponseBody
	public List<CommonComboBoxNode> getIdxRule(String ruleType) {
		return this.idxBS.getIdxRule(ruleType);
	}

	@RequestMapping("/getIdxTimeMeasure")
	@ResponseBody
	public List<CommonComboBoxNode> getIdxTimeMeasure() {
		return this.idxBS.getIdxTimeMeasure();
	}

	@RequestMapping("/getIdxMode")
	@ResponseBody
	public List<CommonComboBoxNode> getIdxMode() {
		return this.idxBS.getIdxMode();
	}
	
	/**
	 * 获取除公共以外的业务类型列表
	 * @return
	 */
	@RequestMapping(value = "/busiTypeList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> busiTypeList(String isPublic) {
		List<String> busiTypes = new ArrayList<String>();
		if(!GlobalConstants4plugin.COMMON_BOOLEAN_YES.equals(isPublic)) {
			busiTypes.add(GlobalConstants4plugin.RPT_FRS_BUSI_PUBLIC);
		}
		return this.rptDatasetBS.getBusiTypeList(false, busiTypes);
	}

	/**
	 * 获取归属地列表
	 * @return
	 */
	@RequestMapping(value = "/addrList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> addrList() {
		return this.rptDatasetBS.getAddrList();
	}
	
	/**
	 * 获取指标类报送任务类型
	 * @return
	 */
	@RequestMapping(value = "/getIdxTaskType.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getIdxTaskType(){
		return this.rptDatasetBS.getIdxTaskType();
	}
	
	/**
	 * 根据指标编号和版本查询相关信息
	 * @param indexNo
	 * @param indexVerId
	 * @return
	 */
	@RequestMapping("/changeIdxVer")
	@ResponseBody
	public Map<String, Object> changeIdxVer(String indexNo, String indexVerId) {
		return idxInfoBs.changeIdxVer(indexNo, indexVerId);
	}
}
