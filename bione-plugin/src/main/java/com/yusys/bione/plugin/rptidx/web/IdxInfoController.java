package com.yusys.bione.plugin.rptidx.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.common.CommonTreeNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.entity.upload.Uploader;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.*;
import com.yusys.bione.frame.base.common.GlobalConstants4frame;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.label.service.LabelBS;
import com.yusys.bione.frame.message.service.MsgNoticeLogBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import com.yusys.bione.frame.security.BioneUser;
import com.yusys.bione.frame.user.service.UserBS;
import com.yusys.bione.plugin.base.utils.ReBuildParam;
import com.yusys.bione.plugin.businessline.service.RptBusinessLineBS;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleCol;
import com.yusys.bione.plugin.datamodel.entity.RptSysModuleInfo;
import com.yusys.bione.plugin.datamodel.service.RptDatasetBS;
import com.yusys.bione.plugin.datashow.service.IdxShowBS;
import com.yusys.bione.plugin.design.service.RptTmpBS;
import com.yusys.bione.plugin.design.util.DataDealUtils;
import com.yusys.bione.plugin.idxana.util.DateUtils;
import com.yusys.bione.plugin.rptdim.entity.RptDimItemInfo;
import com.yusys.bione.plugin.rptdim.entity.RptDimTypeInfo;
import com.yusys.bione.plugin.rptdim.service.DimItemBS;
import com.yusys.bione.plugin.rptidx.entity.*;
import com.yusys.bione.plugin.rptidx.service.IdxInfoBS;
import com.yusys.bione.plugin.rptidx.web.vo.IdxEditorInfoVO;
import com.yusys.bione.plugin.rptidx.web.vo.IdxFormulaAndSrcIdxVO;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxInfoVO;
import com.yusys.bione.plugin.rptidx.web.vo.RptIdxMeasureRelInfoVO;
import com.yusys.bione.plugin.rptmgr.entity.RptMgrReportInfo;
import com.yusys.bione.plugin.rptorg.entity.RptOrgInfo;
import com.yusys.bione.plugin.rptorg.service.RptOrgInfoBS;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yusys.bione.plugin.base.common.GlobalConstants4plugin.*;

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
@RequestMapping("/report/frame/idx")
public class IdxInfoController extends BaseController {
	@Autowired
	private IdxInfoBS idxInfoBs;
	
	@Autowired
	private DimItemBS dimItemBS;
	@Autowired
	private RptBusinessLineBS lineBS;
	@Autowired
	private RptDatasetBS rptDatasetBS;
	@Autowired
	private UserBS userBS;
	@Autowired
	private LabelBS labelBS;
	@Autowired
	private IdxShowBS idxShowBS;
	@Autowired
	private RptOrgInfoBS orgBS;
	@Autowired
	private RptTmpBS rptTmpBS;
	@Autowired
	private MsgNoticeLogBS msgNoticeLogBS;
	
	private static String UPLOAD_ATTACH_DIR = GlobalConstants4frame.IMPORT_PATH
			+ "/rpt/require";

	@RequestMapping(value = "preview" ,method = RequestMethod.GET)
	public ModelAndView preview(String indexNm) {
		ModelMap mm = new ModelMap();
		mm.put("preview", "preview");
		mm.put("isPublish", "1");
		mm.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
      	mm.put("treeRootNo", GlobalConstants4frame.TREE_ROOT_NO);
    	mm.put("rootIcon", GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
	     return new ModelAndView("/plugin/rptidx/content/idx-info-index",mm);
	}
	
	/**
	 * 指标管理入口页面
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		ModelMap mm = new ModelMap();
		mm.put("treeRootNo", GlobalConstants4frame.TREE_ROOT_NO);
		mm.put("rootIcon",com.yusys.bione.frame.base.common.GlobalConstants4frame.DATA_TREE_NODE_ICON_ROOT);
		if(!BioneSecurityUtils.getCurrentUserInfo().isSuperUser()){
			List<String> list = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
			if (list != null && list.size() > 0){
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(),
						"resIndexNo", list);
			}else{
				EhcacheUtils.put(BioneSecurityUtils.getCurrentUserId(),"resIndexNo", new HashMap<String, String>());
			}
		}
		return new ModelAndView("/plugin/rptidx/content/idx-info-index", mm);
	}

	/**
	 * 指标树上目录的移动（包括上移和下移）
	 * 
	 * @return
	 */	
	@RequestMapping(value = "/idxMove")
	@ResponseBody
	public Map<String, String> idxMove(String moveFlag,String selIds,String rankOrders) {
		return this.idxInfoBs.idxMove(moveFlag,selIds,rankOrders);
	}
	
	/**
	 * 指标列表信息页面
	 * @param indexCatalogNo
	 * @param preview
	 * @param indexNm
	 * @param defSrc
	 * @return
	 */
	@RequestMapping(value = "/idxInfos", method = RequestMethod.GET)
	public ModelAndView dimType(String indexCatalogNo, String preview,
			String indexNm, String defSrc) {
		ModelMap mm = new ModelMap();
		try {
			indexCatalogNo = URLDecoder.decode(indexCatalogNo, "UTF-8");
		} catch (UnsupportedEncodingException e2) {
			e2.printStackTrace();
		}
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.put("INDEX_STS_START", INDEX_STS_START);
		mm.put("INDEX_STS_STOP", INDEX_STS_STOP);
		if (StringUtils.isNotEmpty(defSrc)) {
			mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		}
		if (preview != null && !preview.equals("")) {
			mm.put("preview", StringUtils2.javaScriptEncode(preview));
			try {
				indexNm = URLDecoder.decode(indexNm, "UTF-8");
			} catch (UnsupportedEncodingException e2) {
				e2.printStackTrace();
			}
			mm.put("indexNm", StringUtils2.javaScriptEncode(indexNm));
		}
		return new ModelAndView("/plugin/rptidx/content/idx-info-list", mm);
	}
	
	/**
	 * 新增指标目录
	 * 
	 * @param indexCatalogNo
	 * @return
	 */
	@RequestMapping(value = "/newCatalog", method = RequestMethod.GET)
	public ModelAndView newCatalog(String indexCatalogNo, String defSrc) {
		RptIdxCatalog catalog = this.idxInfoBs.getRptIdxCatalogById(
				indexCatalogNo, defSrc);
		ModelMap mm = new ModelMap();
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.put("upIndexCatalogNm",
				catalog == null ? "无" : StringUtils2.javaScriptEncode(catalog.getIndexCatalogNm()));
		if (StringUtils.isNotEmpty(defSrc)) {
			mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		} else {
			mm.put("defSrc", "01");
		}
		return new ModelAndView(
				"/plugin/rptidx/catalog/idx-info-catalog-editNew", mm);
	}

	/**
	 * 获取待修改的维度目录信息
	 * 
	 * @param indexCatalogNo
	 * @return
	 */
	@RequestMapping(value = "/initUpdateCatalog", method = RequestMethod.GET)
	public ModelAndView initUpdateCatalog(String indexCatalogNo, String defSrc) {
		RptIdxCatalog catalog = this.idxInfoBs.getRptIdxCatalogById(
				indexCatalogNo, defSrc);
		if (catalog == null) {
			return null;
		}
		ModelMap mm = new ModelMap();
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.put("indexCatalogNm", StringUtils2.javaScriptEncode(catalog.getIndexCatalogNm()));
		mm.put("remark", StringUtils2.javaScriptEncode(catalog.getRemark()));
		mm.put("catalogOrder", catalog.getCatalogOrder());
		mm.put("upIndexCatalogNm", catalog == null ? "无" : StringUtils2.javaScriptEncode(catalog.getIndexCatalogNm()));
		mm.put("upNo", StringUtils2.javaScriptEncode(catalog.getUpNo()));
		return new ModelAndView("/plugin/rptidx/catalog/idx-info-catalog-update",
				mm);
	}
	
	@RequestMapping("/edit")
	public ModelAndView editNew(String indexCatalogNo, String defSrc) {
		ModelMap mm = new ModelMap();
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		return new ModelAndView("/plugin/rptidx/edit/idx-info-edit", mm);
	}
	
	@RequestMapping("{id}/edit")
	public ModelAndView edit(@PathVariable("id") String id,
			String indexCatalogNo, String defSrc, String indexVerId) {
		ModelMap mm = new ModelMap();
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		mm.put("id", StringUtils2.javaScriptEncode(id));
		mm.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		return new ModelAndView("/plugin/rptidx/edit/idx-info-edit", mm);
	}
	
	@RequestMapping("{id}/show")
	public ModelAndView show(@PathVariable("id") String id,
			String indexCatalogNo, String defSrc, String indexVerId) {
		ModelMap mm = new ModelMap();
		mm.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		if(StringUtils.isEmpty(indexVerId)){
			mm.put("indexVerId", StringUtils2.javaScriptEncode(this.idxInfoBs.getIndexVerId(id)));
		}
		mm.put("id", StringUtils2.javaScriptEncode(id));
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		mm.put("isDict", true);
		mm.put("isRpt", false);
		return new ModelAndView("/plugin/rptidx/edit/idx-info-edit", mm);
	}
	
	@RequestMapping("{id}/preview")
	public ModelAndView preview(@PathVariable("id") String id,
			String indexCatalogNo, String defSrc, String indexVerId) {
		ModelMap mm = new ModelMap();
		mm.put("isDict", true);
		mm.put("isRpt", true);
		mm.put("id", StringUtils2.javaScriptEncode(id));
		mm.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		return new ModelAndView("/plugin/rptidx/edit/idx-info-edit", mm);
	}
	
	@RequestMapping("/busi")
	public ModelAndView busi(String cata) {
		ModelMap mm = new ModelMap();
		RptIdxCatalog catalog = this.idxInfoBs.getEntityById(RptIdxCatalog.class, cata);
		mm.put("catalog", catalog);
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(cata));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//新建指标默认启用日期取今年年初日期
		Date date = new Date();
		date = DateUtils.getFristDateOfCurrYear(date);
		mm.put("today", sdf.format(date));
		return new ModelAndView("/plugin/rptidx/edit/idx-info-busi", mm);
	}

	@RequestMapping("/{id}/busi")
	public ModelAndView busi(@PathVariable("id") String id, String cata,
			String indexVerId, String readOnly) {
		ModelMap mm = new ModelMap();
		RptIdxCatalog catalog = this.idxInfoBs.getEntityById(RptIdxCatalog.class, cata);
		mm.put("catalog", catalog);
		mm.put("indexCatalogNo", StringUtils2.javaScriptEncode(cata));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//新建指标默认启用日期取今年年初日期
		Date date = new Date();
		date = DateUtils.getFristDateOfCurrYear(date);
		mm.put("today", sdf.format(date));
		mm.put("id", StringUtils2.javaScriptEncode(id));
		mm.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		mm.put("readOnly", StringUtils2.javaScriptEncode(readOnly));
		return new ModelAndView("/plugin/rptidx/edit/idx-info-busi", mm);
	}
	
	@RequestMapping("/tech")
	public ModelAndView tech(String type, String indexVerId, String defSrc) {
		ModelMap mm = new ModelMap();
		mm.put("indexType", type);
		ModelAndView mav = new ModelAndView();
		if (ROOT_INDEX.equals(type)) {// 根指标
			mav.setViewName("/plugin/rptidx/tech/idx-tech-attrs-meta-list");
		} else if (COMPOSITE_INDEX.equals(type)) {// 组合指标
			mm.put("INDEX_DEF_SRC_LIB", INDEX_DEF_SRC_LIB);
			mm.put("INDEX_DEF_SRC_ORG", INDEX_DEF_SRC_ORG);
			mm.put("INDEX_DEF_SRC_USER", INDEX_DEF_SRC_USER);
			BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
			List<String> defSrcList = idxShowBS.getUserIdxDefSrc(userInfo);
			if (defSrcList != null && defSrcList.size() > 0
					&& defSrcList.get(0) == null) {
				defSrcList.set(0, "01");
			}
			mm.put("defSrc", defSrcList);
			userInfo.setUserNo(userBS.getUserNo(BioneSecurityUtils
					.getCurrentUserId()));
			mm.put("userInfo", JSON.toJSON(userInfo));
			// mav.setViewName("/plugin/rptidx/idx-tech-attrs-composite-custom");
			mav.setViewName("/plugin/rptidx/tech/idx-tech-composite");
		} else if (ADD_RECORD_INDEX.equals(type)) {// 补录指标
			mm.put("saveType", ADD_RECORD_INDEX);
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
			String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
			String currencyDimTypeNo = propertiesUtils
					.getProperty("currencyDimTypeNo");
			mm.put("dateDimTypeNo", dateDimTypeNo);
			mm.put("orgDimTypeNo", orgDimTypeNo);
			mm.put("currencyDimTypeNo", currencyDimTypeNo);
			mav.setViewName("/plugin/rptidx/tech/idx-tech-attrs-addrecord");
		} else if (GENERIC_INDEX.equals(type)) {// 泛化指标
			mm.put("saveType", GENERIC_INDEX);
			mm.put("setType", SET_TYPE_GENERIC);
			mav.setViewName("/plugin/rptidx/tech/idx-tech-attrs-generic");
		} else if (SUM_ACCOUNT_INDEX.equals(type)) { // 总账指标
			mm.put("saveType", SUM_ACCOUNT_INDEX);
			mm.put("setType", SET_TYPE_SUM);
			mav.setViewName("/plugin/rptidx/tech/idx-tech-attrs-sum");
		} else if (DERIVE_INDEX.equals(type)) { // 派生指标
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
			String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
			String currencyDimTypeNo = propertiesUtils
					.getProperty("currencyDimTypeNo");
			String indexDimTypeNo = propertiesUtils
					.getProperty("indexDimTypeNo");
			mm.put("dateDimTypeNo", dateDimTypeNo);
			mm.put("orgDimTypeNo", orgDimTypeNo);
			mm.put("currencyDimTypeNo", currencyDimTypeNo);
			mm.put("indexDimTypeNo", indexDimTypeNo);
			if (StringUtils.isNotEmpty(defSrc)) {
				mm.put("INDEX_DEF_SRC_LIB", INDEX_DEF_SRC_LIB);
				mm.put("INDEX_DEF_SRC_ORG", INDEX_DEF_SRC_ORG);
				mm.put("INDEX_DEF_SRC_USER", INDEX_DEF_SRC_USER);
				BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
				userInfo.setUserNo(userBS.getUserNo(BioneSecurityUtils
						.getCurrentUserId()));
				mm.put("userInfo", JSON.toJSON(userInfo));
			}
			mav.setViewName("/plugin/rptidx/tech/idx-tech-attrs-driver");
		}
		mav.addAllObjects(mm);
		return mav;
	}
 
	@RequestMapping("/{id}/tech")
	public ModelAndView tech(@PathVariable("id") String indexNo, String type,
			String indexVerId, String defSrc, String isDict) {
		ModelAndView mav = tech(type, indexVerId, defSrc);
		if (mav != null) {
			ModelMap mm = mav.getModelMap();
			mm.put("id", indexNo);
			mm.put("indexNo", indexNo);
			mm.put("indexVerId", indexVerId);
			mm.put("isDict", isDict);
			if (ADD_RECORD_INDEX.equals(type)) {
				if (indexVerId != null && !indexVerId.equals("")) {// 补录指标修改
					List<RptIdxInfo> idxs = this.idxInfoBs.listIdxInfo(mm);
					if (idxs.size() > 0) {
						RptIdxInfo temp = idxs.get(0);
						String indexT = temp.getIndexType();
						if (indexT.equals(ADD_RECORD_INDEX)) {
							List<RptIdxDimRel> infos = this.idxInfoBs
									.getIdxDimRelByParams(mm);
							List<String> dims = Lists.newArrayList();
							if (infos.size() > 0) {
								for (int i = 0; i < infos.size(); i++) {
									RptIdxDimRel temp_ = infos.get(i);
									dims.add(temp_.getId().getDimNo());
								}
							}
							mm.put("dims", JSON.toJSONString(dims));
						}
					}
				}
			} else if (GENERIC_INDEX.equals(type)) {// 泛化指标
				mm.put("saveType", GENERIC_INDEX);
				mm.put("setType", SET_TYPE_GENERIC);
				List<RptIdxInfo> infos = this.idxInfoBs.listIdxInfo(mm);
				if (infos.size() > 0) {
					RptIdxInfo temp = infos.get(0);
					String indexT = temp.getIndexType();
					if (indexT != null && indexT.equals(GENERIC_INDEX)) {
						List<RptIdxMeasureRel> rels = this.idxInfoBs
								.getMeasureRelByParams(mm);
						if (rels.size() > 0) {
							mm.put("currVal", rels.get(0).getId().getDsId());
						}
					}
				}
			} else if (SUM_ACCOUNT_INDEX.equals(type)) { // 总账指标
				mm.put("saveType", SUM_ACCOUNT_INDEX);
				mm.put("setType", SET_TYPE_SUM);
				if ((indexNo != null && !indexNo.equals(""))
						&& (indexVerId != null && !indexVerId.equals(""))) {
					List<RptIdxInfo> infos = this.idxInfoBs.listIdxInfo(mm);
					if (infos.size() > 0) {
						RptIdxInfo temp = infos.get(0);
						String indexT = temp.getIndexType();
						mm.put("indexVerId", temp.getId().getIndexVerId());
						if (indexT != null && indexT.equals(SUM_ACCOUNT_INDEX)) {
							List<RptIdxMeasureRel> rels = this.idxInfoBs
									.getMeasureRelByParams(mm);
							if (rels.size() > 0) {
								mm.put("currVal", rels.get(0).getId().getDsId());
							}
						}
					}
				}
			}else if(DERIVE_INDEX.equals(type)){//派生指标
				List<RptIdxInfo> infos = this.idxInfoBs.listIdxInfo(mm);
				if (infos.size() > 0) {
					String busiType = infos.get(0).getBusiType();
					mm.put("busiType", busiType);
				}
			}
		}
		return mav;
	}
	
	@RequestMapping("/{id}/editorInfo")
	@ResponseBody
	public IdxEditorInfoVO editorInfo(String verId,
			@PathVariable("id") String indexNo) {
		return this.idxInfoBs.getIdxEditorInfo(indexNo, verId);
	}
	
	/**
	 * 根指标来源页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/gotoTechAttrMeta")
	public ModelAndView gotoTechAttrMeta(String dsId, String storeCol) {
		ModelMap map = new ModelMap();
		map.put("setType", SET_TYPE_MUTI_DIM);
		if (dsId != null && !dsId.equals("")) {
			RptSysModuleInfo dataset = this.rptDatasetBS
					.getModuleInfoById(dsId);
			map.put("dsId", StringUtils2.javaScriptEncode(dsId));
			map.put("storeCol", StringUtils2.javaScriptEncode(storeCol));
			map.put("setNm", StringUtils2.javaScriptEncode(dataset.getSetNm()));
		}
		return new ModelAndView("/plugin/rptidx/tech/idx-tech-attrs-meta", map);
	}

	/**
	 * 技术属性页面--根指标--来源选择
	 * 
	 * @return
	 */
	@RequestMapping(value = "/gotoMeasureList")
	public ModelAndView gotoMeasureList(String setId, String storeCol,
			String oldSetId) {
		ModelMap map = new ModelMap();
		if (setId != null && !setId.equals("")) {
			RptSysModuleInfo dataset = this.rptDatasetBS
					.getModuleInfoById(setId);
			map.put("setId", StringUtils2.javaScriptEncode(setId));
			map.put("storeCol", StringUtils2.javaScriptEncode(storeCol));
			map.put("oldSetId", StringUtils2.javaScriptEncode(oldSetId));
			map.put("setNm", StringUtils2.javaScriptEncode(dataset.getSetNm()));
			map.put("tableEnNm", StringUtils2.javaScriptEncode(dataset.getTableEnNm()));
		}
		return new ModelAndView(
				"/plugin/rptidx/tech/idx-tech-attrs-meta-measures", map);
	}
	
	@RequestMapping("/idxchoose")
	public ModelAndView idxchoose() {
		return new ModelAndView("/plugin/rptidx/edit/idx-choose-idx");
	}
	
	@RequestMapping("/userEdit")
	public ModelAndView linkUser() {
		return new ModelAndView("/plugin/rptidx/edit/idx-choose-user");
	}
	
	@RequestMapping("/deptEdit")
	public ModelAndView belongDept() {
		return new ModelAndView("/plugin/rptidx/edit/idx-choose-dept");
	}
	
	/**
	 * 指标编号前缀
	 * 
	 * @return
	 */	
	@RequestMapping(value = "/indexNoPrefix")
	@ResponseBody
	public List<CommonComboBoxNode> getIndexNoPrefixTree(String indexType) {
		return this.idxInfoBs.getIndexNoPrefixTree(this.getContextPath(),indexType);
	}
	
	/**
	 * 技术属性页面--获取度量字段列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getMeasureColList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getMeasureColList(String setId) {
		Map<String, Object> colsMap = Maps.newHashMap();
		Map<String, Object> params = Maps.newHashMap();
		params.put("setId", setId);
		params.put("isUse", COMMON_BOOLEAN_YES);
		params.put("colType",  DATASET_COL_TYPE_MEASURE);
		List<RptSysModuleCol> cols = this.idxInfoBs
				.getRptSysColByParams(params);
		colsMap.put("Rows", cols);
		return colsMap;
	}
	
	/**
	 *新增指标时，登录人信息自动反显 
	 */
	@RequestMapping("/inverse")
	public Map<String,Object> inverse(){
		return this.idxInfoBs.inverse();
	}
	
	
	/**
	 * 
	 * @param searchNm
	 * @param isShowIdx
	 * @param isShowMeasure
	 * @param idxNos
	 *            排除指标编号，以，分割
	 * @param isShowDim
	 * @param indexNo
	 * @param exSumAccoutIndex
	 *            是否排除总账指标
	 * @param isAuth
	 *            是否带有权限
	 * @return
	 */
	@RequestMapping(value = "/getTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTree(String searchNm, String isShowIdx,
			String isShowMeasure, String idxNos, String isShowDim,
			String indexNo, String exSumAccoutIndex, String isEngine,
			String isAuth, String isPublish, String defSrc) {
		return this.idxInfoBs.getTree(this.getContextPath(), searchNm,
				isShowIdx, isShowMeasure, idxNos, isShowDim, indexNo,
				exSumAccoutIndex, isEngine, isAuth, isPublish, defSrc);
	}
	@RequestMapping(value = "/getAsyncTree.*")
	@ResponseBody
	public List<CommonTreeNode> getAsyncTree(String searchNm, String isShowIdx,
			String isShowMeasure, String idxNos, String isShowDim,
			String indexNo, String indexNm, String exSumAccoutIndex, String isEngine,
			String isAuth, String nodeType, String id, String indexVerId,
			String isPublish, String showEmptyFolder, String isPreview,
			String defSrc, String isCabin, String busiType) {
		return this.idxInfoBs.getAsyncTree(this.getContextPath(), searchNm,
				isShowIdx, isShowMeasure, idxNos, isShowDim, indexNo,indexNm,
				exSumAccoutIndex, isEngine, isAuth, nodeType, id, indexVerId,
				isPublish, showEmptyFolder, isPreview, defSrc, null, isCabin, busiType);
	}

	@RequestMapping(value = "/getTreeByRptDesign.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTreeByRptDesign(String searchNm,
			String isShowIdx, String isShowMeasure, String idxNos,
			String isShowDim, String indexNo, String exSumAccoutIndex,
			String isEngine, String isAuth, String nodeType, String upId,
			String indexVerId, String isPublish, String showEmptyFolder,
			String isPreview, String defSrc) {
		return this.idxInfoBs.getTreeByRptDesign(this.getContextPath(), upId,
				indexVerId, nodeType, defSrc);
	}

	@RequestMapping(value = "/getAsyncTreeIdxShow.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getAsyncTreeIdxShow(String searchNm,
			String isShowIdx, String isShowMeasure, String idxNos,
			String isShowDim, String indexNo,String indexNm, String exSumAccoutIndex,
			String isEngine, String isAuth, String nodeType, String id,
			String indexVerId, String isPublish, String showEmptyFolder,
			String isPreview, String defSrc, String isCabin, String busiType) {
		return this.idxInfoBs.getAsyncTree(this.getContextPath(), searchNm,
				isShowIdx, isShowMeasure, idxNos, isShowDim, indexNo,indexNm,
				exSumAccoutIndex, isEngine, isAuth, nodeType, id, indexVerId,
				isPublish, showEmptyFolder, isPreview, defSrc, null, isCabin, busiType);
	}

	@RequestMapping(value = "/getAsyncLabelTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getAsyncLabelTree(String searchNm, String isShowIdx,
			String isShowMeasure, String idxNos, String isShowDim,
			String indexNo, String exSumAccoutIndex, String isEngine,
			String isAuth, String nodeType, String id, String indexVerId,
			String isPublish, String showEmptyFolder, String isPreview,
			String defSrc, String isCabin, String busiType) {
		return this.idxInfoBs.getAsyncLabelTree(this.getContextPath(), searchNm, id,
				indexVerId, isShowDim, isShowMeasure, nodeType, isCabin, busiType);
	}
	
	@RequestMapping(value = "/getSyncLabelFilter.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getSyncLabelFilter(String searchNm, String isShowIdx,
			String isShowMeasure, String idxNos, String isShowDim,
			String indexNo, String exSumAccoutIndex, String isEngine,
			String isAuth, String nodeType, String id, String indexVerId,
			String isPublish, String showEmptyFolder, String isPreview,
			String defSrc, String ids, String type, String isCabin, String busiType) {
		return this.idxInfoBs.getSyncLabelFilter(this.getContextPath(), searchNm,
				indexVerId, isShowDim, isShowMeasure, isAuth, ids, type, isCabin, busiType);
	}
	
	@RequestMapping(value = "/getSyncTreePro")
	@ResponseBody
	public List<CommonTreeNode> getSyncTreePro(String searchObj, String isShowIdx,
			String isShowMeasure, String isShowDim, String indexNo,String indexNm,
			String isEngine, String isPublish, String exSumAccoutIndex,
			String isAuth, String showEmptyFolder, String defSrc, String isCabin, String busiType) {
		return this.idxInfoBs.getSyncTreePro(this.getContextPath(), searchObj,
				isShowIdx, isShowMeasure, isShowDim, indexNo, indexNm,isEngine,
				isPublish, exSumAccoutIndex, isAuth, showEmptyFolder, defSrc, null, isCabin, busiType);
	}
	
	@RequestMapping(value = "/getSyncLabelTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getSyncLabelTree(String searchNm, String isShowIdx,
			String isShowMeasure, String idxNos, String isShowDim,
			String indexNo, String exSumAccoutIndex, String isEngine,
			String isAuth, String nodeType, String id, String indexVerId,
			String isPublish, String showEmptyFolder, String isPreview,
			String defSrc, String isCabin, String busiType) {
		return this.idxInfoBs.getSyncLabelTree(this.getContextPath(), searchNm, 
				indexVerId, isShowDim, isShowMeasure, isCabin, busiType);
	}
	
	@RequestMapping(value = "/getSyncTree")
	@ResponseBody
	public List<CommonTreeNode> getSyncTree(String searchNm, String isShowIdx,
			String isShowMeasure, String isShowDim, String indexNo,String indexNm,
			String isEngine, String isPublish, String exSumAccoutIndex,
			String isAuth, String showEmptyFolder, String defSrc, String isCabin, String busiType) {
		return this.idxInfoBs.getSyncTree(this.getContextPath(), searchNm,
				isShowIdx, isShowMeasure, isShowDim, indexNo, indexNm,isEngine,
				isPublish, exSumAccoutIndex, isAuth, showEmptyFolder, defSrc, null, isCabin, null, busiType);
	}

	/**
	 * 进入指标目录选择树页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/catalogTree")
	public ModelAndView catalogTreeSelect(String catalogId, String defSrc) {
		ModelMap mm = new ModelMap();
		mm.put("catalogId", StringUtils2.javaScriptEncode(catalogId));
		if (StringUtils.isNotEmpty(defSrc)) {
			mm.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		}
		return new ModelAndView("/plugin/rptidx/idx-catalog-selecttree",
				mm);
	}

	@RequestMapping("/listCatalogTree.*")
	@ResponseBody
	public List<CommonTreeNode> listCatalogTree(String defSrc) {
		List<CommonTreeNode> nodes = this.idxInfoBs.getCatalogTree(defSrc);
		return nodes;
	}

	/**
	 * 保存指标目录
	 * 
	 * @param model
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void createRptIdxCatalog(RptIdxCatalog model) {
		model.setIndexCatalogNo(RandomUtils.uuid2());
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();
		model.setDefUser(user.getUserId());
		if (INDEX_DEF_SRC_ORG.equals(model.getDefSrc())) {
			model.setDefOrg(user.getOrgNo());
		}
		this.idxInfoBs.createRptIdxCatalog(model);
	}

	/**
	 * 新增或者修改指标
	 * 
	 * @param indexCatalogNo
	 * @return 
	 * @return
	 */
	@RequestMapping(value = "/createRptIdxInfo", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> createRptIdxInfo(RptIdxInfoVO model, String busiType) {
		String saveType = model.getSaveType();
		String sourceId = this.idxInfoBs.getSourceIdBySetId(model.getSetId());
		String indexUsualNmTemp1 = model.getIndexUsualNmTemp1();
		String indexUsualNmTemp2 = model.getIndexUsualNmTemp2();
		String dimnos = model.getDimnos();
		String indexUsualNm = "";
		String[] indexUsualNmArr = new String[] { indexUsualNmTemp1,
				indexUsualNmTemp2 };
		for (int i = 0; i < indexUsualNmArr.length; i++) {
			if (StringUtils.isNotEmpty(indexUsualNmArr[i])) {
				indexUsualNm += indexUsualNmArr[i] + ",";
			}
		}
		if (indexUsualNm.length() > 0) {
			indexUsualNm = indexUsualNm.substring(0, indexUsualNm.length() - 1);
		}
		Map<String, Object> bs_params = Maps.newHashMap();
		RptIdxFormulaInfo idxFormulaInfo = null;
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
		String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
		String currencyDimTypeNo = propertiesUtils
				.getProperty("currencyDimTypeNo");
		String indexDimTypeNo = propertiesUtils.getProperty("indexDimTypeNo");
		String steadyMeasureNo = propertiesUtils.getProperty("steadyMeasureNo");
		// String storeModel = propertiesUtils.getProperty("storeModel");
		
		//--lcy 业务分库 
		String dsId = lineBS.getSetIdByBusiLibId(model.getBusiLibId());
		if(StringUtils.isBlank(dsId)) {
			dsId = propertiesUtils.getProperty("dsId");
		}
		String defaultMeasure = propertiesUtils.getProperty("defaultMeasure");
		String defaultDim = propertiesUtils.getProperty("defaultDim");
		String dataDate = propertiesUtils.getProperty("dataDate");
		String orgNo = propertiesUtils.getProperty("orgNo");
		String currType = propertiesUtils.getProperty("currType");
		String indexNo = propertiesUtils.getProperty("indexNo");
		BioneUser user = BioneSecurityUtils.getCurrentUserInfo();

		RptIdxInfoPK infoPk = new RptIdxInfoPK();
		infoPk.setIndexNo(model.getIndexNo());
		infoPk.setIndexVerId(1);

		RptIdxInfo info = new RptIdxInfo();
		if (StringUtils.isNotEmpty(model.getDefSrc())) {
			info.setDefSrc(model.getDefSrc());
			if (INDEX_DEF_SRC_ORG.equals(model.getDefSrc())) {
				info.setDefOrg(user.getOrgNo());
			}
			info.setDefUser(user.getUserId());
		} else {
			info.setDefSrc("01");
		}
		
		info.setId(infoPk);
		if (model.getIsRptIndex() != null
				&& model.getIsRptIndex().equals(COMMON_BOOLEAN_YES)) {
			info.setIsRptIndex(COMMON_BOOLEAN_YES);
			model.setSaveType(REPORT_INDEX);
		} else {
			info.setIsRptIndex(COMMON_BOOLEAN_NO);
		}
		info.setCalcCycle(model.getCalcCycle());
		info.setDataType(model.getDataType());
		info.setDataUnit(model.getDataUnit());
		if (model.getIndexCatalogNo() != null
				&& !model.getIndexCatalogNo().equals(""))
			info.setIndexCatalogNo(model.getIndexCatalogNo());
		info.setIndexNm(model.getIndexNm());
		info.setIndexSts(model.getIndexSts());
		info.setIndexType(model.getIndexType());
		info.setIsSum(model.getIsSum());
		info.setLastUptTime(new Timestamp(new Date().getTime()));
		info.setLastUptUser(user.getUserId());
		info.setRemark(model.getRemark());
		info.setSrcIndexNo("");
		info.setSrcSourceId(sourceId);
		info.setStartDate(model.getStartDate());
		info.setStatType(model.getStatType());
		info.setIsSave(model.getIsSave());
		info.setIsCabin("0");//是否管架默认0
		info.setEndDate("29991231");
		info.setLineId(model.getLineId());
		info.setBusiLibId(model.getBusiLibId());
		//新增字段
		info.setUserId(model.getUserId());//联系人
		info.setDeptId(model.getDeptId());//主管部门
		if(saveType.equals("01")){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
			String dateStr = sdf.format(new Date());
			info.setCreateDate(dateStr);
		}
		RptIdxBusiExtPK busiExtPK = new RptIdxBusiExtPK();
		busiExtPK.setIndexNo(model.getIndexNo());
		busiExtPK.setIndexVerId(1);

		RptIdxBusiExt busiExt = new RptIdxBusiExt();
		busiExt.setId(busiExtPK);

		busiExt.setDefDept(model.getDefDept());
		busiExt.setIndexUsualNm(indexUsualNm);
		busiExt.setBusiDef(model.getBusiDef());
		busiExt.setBusiRule(model.getBusiRule());
		// busiExt.setBusiModel(model.getBusiModel()==null?"":model.getBusiModel());
		busiExt.setUseDept(model.getUseDept());
		RptIdxMeasureRelPK idxMeasureRelPK = new RptIdxMeasureRelPK();
		idxMeasureRelPK.setDsId(model.getSetId());
		idxMeasureRelPK.setIndexNo(model.getIndexNo());
		idxMeasureRelPK.setIndexVerId(1);
		idxMeasureRelPK.setMeasureNo(steadyMeasureNo);

		RptIdxMeasureRel idxMeasureRel = new RptIdxMeasureRel();
		idxMeasureRel.setId(idxMeasureRelPK);
		idxMeasureRel.setStoreCol(model.getStoreCol());
		List<RptIdxDimRel> idxDimRelList = Lists.newArrayList();
		List<RptIdxMeasureRel> idxMeasureRelList = Lists.newArrayList();
		List<RptIdxFilterInfo> idxFilterList = Lists.newArrayList();
		int orderVal = 1;
		int complexIndexOrderVal = 1;// 复合指标（派生、组合,报表）
		// 组合指标
		if (saveType != null && saveType.equals(COMPOSITE_INDEX)) {
			info.setSrcIndexNo(model.getSrcIndexNo());
			info.setBusiType(this.idxInfoBs.getBusiTypeByIndexNo(model.getSrcIndexNo()));
			info.setSetId(dsId);
			if (model.getSumAccMeaNo() != null
					&& !model.getSumAccMeaNo().equals("")) {
				info.setSrcIndexMeasure(model.getSumAccMeaNo());
			} else {
				info.setSrcIndexMeasure(steadyMeasureNo);
			}
			RptIdxFormulaInfoPK formulaInfoPK = new RptIdxFormulaInfoPK();
			formulaInfoPK.setIndexNo(model.getIndexNo());
			if (model.getIndexVerId() != null
					&& !model.getIndexVerId().equals("")) {
				formulaInfoPK.setIndexVerId(Long.parseLong(model
						.getIndexVerId()));
			} else {
				formulaInfoPK.setIndexVerId(1);
			}
			idxFormulaInfo = new RptIdxFormulaInfo();
			idxFormulaInfo.setId(formulaInfoPK);
			idxFormulaInfo.setFormulaContent(model.getFormulaContent());
			idxFormulaInfo.setFormulaDesc("");
			idxFormulaInfo.setFormulaType(FORMULA_TYPE_FILTER);
			idxMeasureRelPK.setDsId(dsId);
			idxMeasureRel.setOrderNum(new BigDecimal(1));
			idxMeasureRel.setStoreCol(defaultMeasure);

			String dataJsonStr = model.getDataJsonStr();
			String dataJsonFilterModeStr = model.getDataJsonFilterModeStr();
			String dataJsonDimStr = model.getDataJsonDimStr();
			JSONArray ja = JSON.parseArray(dataJsonDimStr);
			JSONObject jsonObject = JSON.parseObject(dataJsonStr);
			JSONObject filterModeJsonObject = JSON.parseObject(dataJsonFilterModeStr);
			JSONArray jsonArray = jsonObject.getJSONArray("fields");
			JSONArray filterModeJsonArray = filterModeJsonObject
					.getJSONArray("fields");
			// 减维
			for (int i = 0; i < ja.size(); i ++) {
				JSONObject jo = ja.getJSONObject(i);
				String dimTypeNo0_ = jo.getString("dimTypeNo");
				RptIdxDimRelPK idxDimRelPK = new RptIdxDimRelPK();
				idxDimRelPK.setDimNo(dimTypeNo0_);
				idxDimRelPK.setDsId(dsId);
				idxDimRelPK.setIndexNo(model.getIndexNo());
				if (model.getIndexVerId() != null
						&& !model.getIndexVerId().equals("")) {
					idxDimRelPK.setIndexVerId(Long.parseLong(model
							.getIndexVerId()));
				} else {
					idxDimRelPK.setIndexVerId(1);
				}
				RptIdxDimRel idxDimRel = new RptIdxDimRel();
				idxDimRel.setId(idxDimRelPK);
				if (dimTypeNo0_.equals(orgDimTypeNo)) {
					idxDimRel.setDimType(DIM_TYPE_ORG);
					idxDimRel.setStoreCol(orgNo);
				} else if (dimTypeNo0_.equals(dateDimTypeNo)) {
					idxDimRel.setDimType(DIM_TYPE_DATE);
					idxDimRel.setStoreCol(dataDate);
				} else if (dimTypeNo0_.equals(currencyDimTypeNo)) {
					idxDimRel.setDimType(DIM_TYPE_CURRENCY);
					idxDimRel.setStoreCol(currType);
				} else if (dimTypeNo0_.equals(indexDimTypeNo)) {
					idxDimRel.setDimType(DIM_TYPE_INDEXNO);
					idxDimRel.setStoreCol(indexNo);
				} else {
					idxDimRel
							.setStoreCol(defaultDim + (complexIndexOrderVal++));
					idxDimRel.setDimType(DIM_TYPE_BUSI);
				}
				idxDimRel.setOrderNum(new BigDecimal(orderVal++));
				idxDimRelList.add(idxDimRel);
			}
			for (int j = 0; j < jsonArray.size(); j++) {
				JSONObject object = (JSONObject) jsonArray.get(j);
				JSONObject filterModeObject = (JSONObject) filterModeJsonArray
						.get(j);
				String dimTypeNo0_ = object.getString("dimTypeNo");
				JSONArray dimItemJsonArray_ = object.getJSONArray("itemArr");
				RptIdxFilterInfoPK filterInfoPK = new RptIdxFilterInfoPK();
				filterInfoPK.setDimNo(dimTypeNo0_);
				filterInfoPK.setIndexNo(model.getIndexNo());
				if (model.getIndexVerId() != null
						&& !model.getIndexVerId().equals("")) {
					filterInfoPK.setIndexVerId(Long.parseLong(model
							.getIndexVerId()));
				} else {
					filterInfoPK.setIndexVerId(1);
				}
				RptIdxFilterInfo idxFilterInfo = new RptIdxFilterInfo();
				idxFilterInfo.setId(filterInfoPK);
				idxFilterInfo
						.setFilterMode(filterModeObject.getString("filterMode") == null ? ""
								: filterModeObject.getString("filterMode")
										.equals("[]") ? "" : filterModeObject
										.getString("filterMode"));
				String items = "";
				for (int k = 0; k < dimItemJsonArray_.size(); k++) {
					items += dimItemJsonArray_.getString(k) + ",";
				}
				if (items.length() > 0) {
					items = items.substring(0, items.length() - 1);
				}
				idxFilterInfo.setFilterVal(items);
				idxFilterList.add(idxFilterInfo);
			}
		} else if (saveType != null
				&& (saveType.equals(SUM_ACCOUNT_INDEX) || saveType
						.equals(GENERIC_INDEX))) {// 总账指标
			info.setSetId(model.getSetId());
			info.setBusiType(this.idxInfoBs.getBusiTypeBySetId(model.getSetId()));
			int orderVal2_ = 1;
			int orderVal3_ = 1;
			boolean isGenericIndex = false;
			if (saveType.equals(GENERIC_INDEX)) {
				isGenericIndex = true;
			}
			Map<String, Object> params2_ = Maps.newHashMap();
			params2_.put("setId", model.getSetId());
			params2_.put("isUse", COMMON_BOOLEAN_YES);
			List<RptSysModuleCol> cols = this.idxInfoBs
					.getRptSysColByParams(params2_);
			if (cols.size() > 0) {
				for (int i = 0; i < cols.size(); i++) {
					RptSysModuleCol col = cols.get(i);
					if (col.getDimTypeNo() != null
							&& !col.getDimTypeNo().equals("")) {
						RptIdxDimRelPK idxDimRelPK = new RptIdxDimRelPK();
						idxDimRelPK.setDimNo(col.getDimTypeNo());
						idxDimRelPK.setDsId(model.getSetId());
						idxDimRelPK.setIndexNo(model.getIndexNo());
						if (model.getIndexVerId() != null
								&& !model.getIndexVerId().equals("")) {
							idxDimRelPK.setIndexVerId(Long.parseLong(model
									.getIndexVerId()));
						} else {
							idxDimRelPK.setIndexVerId(1);
						}
						RptIdxDimRel idxDimRel = new RptIdxDimRel();
						idxDimRel.setId(idxDimRelPK);
						idxDimRel.setStoreCol(cols.get(i).getEnNm());
						if (col.getDimTypeNo().equals(orgDimTypeNo)) {
							idxDimRel.setDimType(DIM_TYPE_ORG);
						} else if (col.getDimTypeNo().equals(dateDimTypeNo)) {
							idxDimRel.setDimType(DIM_TYPE_DATE);
						} else if (col.getDimTypeNo().equals(currencyDimTypeNo)) {
							idxDimRel.setDimType(DIM_TYPE_CURRENCY);
						} else if (col.getDimTypeNo().equals(indexDimTypeNo)) {
							idxDimRel.setDimType(DIM_TYPE_INDEXNO);
						} else {
							idxDimRel.setDimType(DIM_TYPE_BUSI);
						}
						idxDimRel.setOrderNum(new BigDecimal(orderVal2_++));
						idxDimRelList.add(idxDimRel);
					} else if ( col.getColType().equals(COL_TYPE_MEASURE)) {
						if (orderVal3_ == 2 && isGenericIndex) {
							continue;
						}
						RptIdxMeasureRelPK idxMeasureRelPK2_ = new RptIdxMeasureRelPK();
						idxMeasureRelPK2_.setDsId(model.getSetId());
						idxMeasureRelPK2_.setIndexNo(model.getIndexNo());
						if (model.getIndexVerId() != null
								&& !model.getIndexVerId().equals("")) {
							idxMeasureRelPK2_.setIndexVerId(Long
									.parseLong(model.getIndexVerId()));
						} else {
							idxMeasureRelPK2_.setIndexVerId(1);
						}
						if (isGenericIndex) {
							idxMeasureRelPK2_.setMeasureNo(steadyMeasureNo);
						} else {
							idxMeasureRelPK2_.setMeasureNo(col.getMeasureNo());
						}
						RptIdxMeasureRel idxMeasureRel2_ = new RptIdxMeasureRel();
						idxMeasureRel2_.setId(idxMeasureRelPK2_);
						idxMeasureRel2_.setStoreCol(col.getEnNm());
						idxMeasureRel2_
								.setOrderNum(new BigDecimal(orderVal3_++));
						idxMeasureRelList.add(idxMeasureRel2_);
					}
				}
			}

		} else if (saveType != null && saveType.equals(DERIVE_INDEX)) {// 派生指标
			info.setSetId(dsId);
			info.setSrcIndexNo(model.getSrcIndexNo());
			info.setBusiType(busiType);
			info.setSrcIndexMeasure(model.getSrcIndexMeasure());
			RptIdxFormulaInfoPK formulaInfoPK = new RptIdxFormulaInfoPK();
			formulaInfoPK.setIndexNo(model.getIndexNo());
			if (model.getIndexVerId() != null
					&& !model.getIndexVerId().equals("")) {
				formulaInfoPK.setIndexVerId(Long.parseLong(model
						.getIndexVerId()));
			} else {
				formulaInfoPK.setIndexVerId(1);
			}
			idxFormulaInfo = new RptIdxFormulaInfo();
			idxFormulaInfo.setId(formulaInfoPK);
			idxFormulaInfo.setFormulaContent(model.getFormulaContent());
			idxFormulaInfo.setFormulaDesc(model.getFormulaDesc());
			idxFormulaInfo.setFormulaType(FORMULA_TYPE_CALC);
			idxMeasureRelPK.setDsId(dsId);
			idxMeasureRel.setOrderNum(new BigDecimal(1));
			idxMeasureRel.setStoreCol(defaultMeasure);
			String dimTypes = model.getDimTypes();
			String[] dimTypeArr = StringUtils.split(dimTypes, ';');
			for (String dimType : dimTypeArr) {
				RptIdxDimRelPK idxDimRelPK = new RptIdxDimRelPK();
				idxDimRelPK.setDimNo(dimType);
				idxDimRelPK.setDsId(dsId);
				idxDimRelPK.setIndexNo(model.getIndexNo());
				if (model.getIndexVerId() != null
						&& !model.getIndexVerId().equals("")) {
					idxDimRelPK.setIndexVerId(Long.parseLong(model
							.getIndexVerId()));
				} else {
					idxDimRelPK.setIndexVerId(1);
				}
				RptIdxDimRel idxDimRel = new RptIdxDimRel();
				idxDimRel.setId(idxDimRelPK);
				if (dimType.equals(orgDimTypeNo)) {
					idxDimRel.setDimType(DIM_TYPE_ORG);
					idxDimRel.setStoreCol(orgNo);
				} else if (dimType.equals(dateDimTypeNo)) {
					idxDimRel.setDimType(DIM_TYPE_DATE);
					idxDimRel.setStoreCol(dataDate);
				} else if (dimType.equals(currencyDimTypeNo)) {
					idxDimRel.setDimType(DIM_TYPE_CURRENCY);
					idxDimRel.setStoreCol(currType);
				} else if (dimType.equals(indexDimTypeNo)) {
					idxDimRel.setDimType(DIM_TYPE_INDEXNO);
					idxDimRel.setStoreCol(indexNo);
				} else {
					idxDimRel
							.setStoreCol(defaultDim + (complexIndexOrderVal++));
					idxDimRel.setDimType(DIM_TYPE_BUSI);
				}
				idxDimRel.setOrderNum(new BigDecimal(orderVal++));
				idxDimRelList.add(idxDimRel);
			}
		} else if (saveType != null && saveType.equals(ROOT_INDEX)) {// 根指标的度量、维度关系信息维护
			info.setSetId(dsId);
			Map<String, Integer> orderNoMap = Maps.newHashMap();
			String dataJsonStr = model.getDataJsonStr();
			JSONObject jsonObject = JSON.parseObject(dataJsonStr);
			JSONArray jsonArray = jsonObject.getJSONArray("fields");
			for (int j = 0; j < jsonArray.size(); j++) {
				JSONObject object = (JSONObject) jsonArray.get(j);
				String dsId0_ = object.getString("dsId");
				String storeCol0_ = object.getString("storeCol");
				if (dsId0_ != null && !dsId0_.equals("")) {
					if (orderNoMap.get(dsId0_) == null) {
						orderNoMap.put(dsId0_, 1);
					} else {
						orderNoMap.put(dsId0_, orderNoMap.get(dsId0_) + 1);
					}
					info.setSetId(dsId0_);
					info.setBusiType(this.idxInfoBs.getBusiTypeBySetId(dsId0_));
					RptIdxMeasureRelPK idxMeasureRelPK0_ = new RptIdxMeasureRelPK();
					idxMeasureRelPK0_.setDsId(dsId0_);
					idxMeasureRelPK0_.setIndexNo(model.getIndexNo());
					if (model.getIndexVerId() != null
							&& !model.getIndexVerId().equals("")) {
						idxMeasureRelPK0_.setIndexVerId(Long.parseLong(model
								.getIndexVerId()));
					} else {
						idxMeasureRelPK0_.setIndexVerId(1);
					}
					idxMeasureRelPK0_.setMeasureNo(steadyMeasureNo);
					RptIdxMeasureRel idxMeasureRel0_ = new RptIdxMeasureRel();
					idxMeasureRel0_.setId(idxMeasureRelPK0_);
					idxMeasureRel0_.setStoreCol(storeCol0_);
					idxMeasureRel0_.setOrderNum(new BigDecimal(orderNoMap
							.get(dsId0_)));
					idxMeasureRelList.add(idxMeasureRel0_);
					int orderVal0_ = 1;
					Map<String, Object> params0_ = Maps.newHashMap();
					params0_.put("setId", dsId0_);
					params0_.put("isUse", COMMON_BOOLEAN_YES);
					List<RptSysModuleCol> cols = this.idxInfoBs
							.getRptSysColByParams(params0_);
					if (cols.size() > 0) {
						for (int i = 0; i < cols.size(); i++) {
							RptSysModuleCol col = cols.get(i);
							if (col.getDimTypeNo() != null
									&& !col.getDimTypeNo().equals("")) {
								RptIdxDimRelPK idxDimRelPK = new RptIdxDimRelPK();
								idxDimRelPK.setDimNo(col.getDimTypeNo());
								idxDimRelPK.setDsId(dsId0_);
								idxDimRelPK.setIndexNo(model.getIndexNo());
								if (model.getIndexVerId() != null
										&& !model.getIndexVerId().equals("")) {
									idxDimRelPK.setIndexVerId(Long
											.parseLong(model.getIndexVerId()));
								} else {
									idxDimRelPK.setIndexVerId(1);
								}
								RptIdxDimRel idxDimRel = new RptIdxDimRel();
								idxDimRel.setId(idxDimRelPK);
								idxDimRel.setStoreCol(cols.get(i).getEnNm());
								if (col.getDimTypeNo().equals(orgDimTypeNo)) {
									idxDimRel.setDimType(DIM_TYPE_ORG);
								} else if (col.getDimTypeNo().equals(
										dateDimTypeNo)) {
									idxDimRel.setDimType(DIM_TYPE_DATE);
								} else if (col.getDimTypeNo().equals(
										currencyDimTypeNo)) {
									idxDimRel.setDimType(DIM_TYPE_CURRENCY);
								} else if (col.getDimTypeNo().equals(
										indexDimTypeNo)) {
									idxDimRel.setDimType(DIM_TYPE_INDEXNO);
								} else {
									idxDimRel.setDimType(DIM_TYPE_BUSI);
								}
								idxDimRel.setOrderNum(new BigDecimal(
										orderVal0_++));
								idxDimRelList.add(idxDimRel);
							}
						}
					}
				}
			}
		} else if (saveType != null && saveType.equals(ADD_RECORD_INDEX)) {// 补录指标的维度关系
			info.setSetId(dsId);
			info.setSrcIndexNo("");
			info.setSrcIndexMeasure(steadyMeasureNo);
			idxMeasureRelPK.setDsId(dsId);
			idxMeasureRel.setOrderNum(new BigDecimal(1));
			idxMeasureRel.setStoreCol(defaultMeasure);
			String[] dimnoArr = null;
			if (StringUtils.isNotEmpty(dimnos)) {
				dimnos = dimnos.substring(0, dimnos.length() - 1);
				dimnoArr = StringUtils.split(dimnos, ',');
				for (String dimno : dimnoArr) {
					String dimTypeNo0_ = dimno;
					RptIdxDimRelPK idxDimRelPK = new RptIdxDimRelPK();
					idxDimRelPK.setDimNo(dimTypeNo0_);
					idxDimRelPK.setDsId(dsId);
					idxDimRelPK.setIndexNo(model.getIndexNo());
					if (model.getIndexVerId() != null
							&& !model.getIndexVerId().equals("")) {
						idxDimRelPK.setIndexVerId(Long.parseLong(model
								.getIndexVerId()));
					} else {
						idxDimRelPK.setIndexVerId(1);
					}
					RptIdxDimRel idxDimRel = new RptIdxDimRel();
					idxDimRel.setId(idxDimRelPK);
					if (dimTypeNo0_.equals(orgDimTypeNo)) {
						idxDimRel.setDimType(DIM_TYPE_ORG);
						idxDimRel.setStoreCol(orgNo);
					} else if (dimTypeNo0_.equals(dateDimTypeNo)) {
						idxDimRel.setDimType(DIM_TYPE_DATE);
						idxDimRel.setStoreCol(dataDate);
					} else if (dimTypeNo0_.equals(currencyDimTypeNo)) {
						idxDimRel.setDimType(DIM_TYPE_CURRENCY);
						idxDimRel.setStoreCol(currType);
					} else if (dimTypeNo0_.equals(indexDimTypeNo)) {
						idxDimRel.setDimType(DIM_TYPE_INDEXNO);
						idxDimRel.setStoreCol(indexNo);
					} else {
						idxDimRel.setStoreCol(defaultDim
								+ (complexIndexOrderVal++));
						idxDimRel.setDimType(DIM_TYPE_BUSI);
					}
					idxDimRel.setOrderNum(new BigDecimal(orderVal++));
					idxDimRelList.add(idxDimRel);
				}
			}
		}
		if (model.getIndexVerId() != null && !model.getIndexVerId().equals("")
				&& model.getIsNewVer().equals("N")) {
			// modify
			infoPk.setIndexVerId(Long.parseLong(model.getIndexVerId()));
			busiExtPK.setIndexVerId(Long.parseLong(model.getIndexVerId()));
			idxMeasureRelPK
					.setIndexVerId(Long.parseLong(model.getIndexVerId()));
			bs_params.put("info", info);
			bs_params.put("busiExt", busiExt);
			bs_params.put("idxMeasureRel", idxMeasureRel);
			bs_params.put("idxDimRelList", idxDimRelList);
			bs_params.put("idxMeasureRelList", idxMeasureRelList);
			bs_params.put("idxFormulaInfo", idxFormulaInfo);
			bs_params.put("idxFilterList", idxFilterList);
			bs_params.put("saveType", saveType);
			//保存前判断口径变化并发送站内信20190530
			this.ifSendMsg(saveType,model,idxFilterList,idxFormulaInfo);
			this.idxInfoBs.updateIdxInfoAndRelInfo(bs_params);
		} else {
			// new or newFullVersion
			if (model.getIsNewVer().equals("Y")) {
				infoPk.setIndexVerId(Long.parseLong(model.getIndexVerId()));
				busiExtPK.setIndexVerId(Long.parseLong(model.getIndexVerId()));
				idxMeasureRelPK.setIndexVerId(Long.parseLong(model
						.getIndexVerId()));
			}
			info.setEndDate("29991231");
			bs_params.put("info", info);
			bs_params.put("isNewVer", model.getIsNewVer());
			bs_params.put("busiExt", busiExt);
			bs_params.put("idxMeasureRel", idxMeasureRel);
			bs_params.put("idxDimRelList", idxDimRelList);
			bs_params.put("idxMeasureRelList", idxMeasureRelList);
			bs_params.put("idxFormulaInfo", idxFormulaInfo);
			bs_params.put("idxFilterList", idxFilterList);
			bs_params.put("saveType", saveType);
			this.idxInfoBs.saveIdxInfoAndRelInfo(bs_params);
		}
		return new HashMap<String,Object>();
	}
	
	/**
	 * 自动生成指标编号
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getAutoIndexNo", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> getAutoIndexNo(String prefix) {
		Map<String, String> param= new HashMap<String, String>();
		if(prefix != null && !"".equals(prefix)){
			String idxNo = this.idxInfoBs.getAutoIndexNo(prefix);
			param.put("idxNo",idxNo);
		}else{
			param.put("idxNo", "");
		}
		return param;
	}

	

	@SuppressWarnings("unused")
	private String getFormatDate(String date) {
		if (date == null || date.equals("")) {
			return "";
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (date.length() == 8) {
			format = new SimpleDateFormat("yyyyMMdd");
		}
		Date newDate = null;
		try {
			newDate = format.parse(date);
			format = new SimpleDateFormat("yyyyMMdd");
			if (date.length() == 8) {
				format = new SimpleDateFormat("yyyy-MM-dd");
			}
			return format.format(newDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 修改指标目录
	 * 
	 * @param model
	 */
	@RequestMapping(value = "/updateCatalog", method = RequestMethod.POST)
	public void updateCatalog(RptIdxCatalog model) {
		this.idxInfoBs.updateCatalog(model);
	}

	/**
	 * 删除指标目录
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/idxCatalog/{id}", method = RequestMethod.POST)
	public Map<String, String> destroy(@PathVariable("id") String id,
			String defSrc) {
		Map<String, String> resultMap = Maps.newHashMap();
		this.idxInfoBs.cascadeDel(id, defSrc);
		resultMap.put("msg", "0");
		return resultMap;
	}

	/**
	 * 修改指标状态
	 * 
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/updateIndexSts", method = RequestMethod.GET)
	public Map<String, String> updateIndexSts(String id, String vid,
			String indexSts) {
		Map<String, String> resultMap = Maps.newHashMap();
		RptIdxInfoPK pk = new RptIdxInfoPK();
		pk.setIndexNo(id);
		pk.setIndexVerId(Long.parseLong(vid));
		RptIdxInfo info = new RptIdxInfo();
		info.setId(pk);
		if (indexSts != null && !indexSts.equals("")) {
			if (indexSts.equals(INDEX_STS_START))
				info.setIndexSts(INDEX_STS_STOP);
			else if (indexSts.equals(INDEX_STS_STOP))
				info.setIndexSts(INDEX_STS_START);
			try {
				this.idxInfoBs.updateIndex(info);
				resultMap.put("msg", "0");
			} catch (Exception e) {
				resultMap.put("msg", "-1");
				e.printStackTrace();
			}
		} else {
			resultMap.put("msg", "-1");
		}
		return resultMap;
	}

	

	/**
	 * 构造指标展示列表
	 */
	@RequestMapping(value = "/getIdxInfoList.*")
	@ResponseBody
	public Map<String, Object> getIdxInfoList(Pager pager,
			String indexCatalogNo, String indexNm, String preview, String auth,
			String defSrc) {
		Map<String, Object> param = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(defSrc)) {
			param.put("defSrc", defSrc);
		}
		if (StringUtils.isNotEmpty(indexNm) && pager.getCondition() == null) {
			param.put("indexNm", indexNm);
		}
		if (StringUtils.isNotEmpty(preview)) {
			param.put("indexSts", INDEX_STS_START);
		}
		if (StringUtils.isNotBlank(indexCatalogNo)) {
			List<String> upNos = new ArrayList<String>();
			List<String> indexCatalogNos = new ArrayList<String>();
			upNos.add(indexCatalogNo);
			indexCatalogNos.add(indexCatalogNo);
			this.idxInfoBs.getAllIndexCatalogNos(upNos, indexCatalogNos);
			param.put("indexCatalogNos", indexCatalogNos);
		}
		SearchResult<RptIdxInfoVO> searchResult = this.idxInfoBs
				.getIdxInfoListShowByParams(pager, param);
		Map<String, Object> rowData = Maps.newHashMap();
		List<RptIdxInfoVO> list = searchResult.getResult();
		Map<String, Object> indexTypeMap = Maps.newHashMap();
		for (int i = 0; i < list.size(); i++) {
			RptIdxInfoVO info = list.get(i);
			if (indexTypeMap.containsKey(info.getIndexType())) {
				info.setIndexType((String) indexTypeMap.get(info.getIndexType()));
			}
		}		
		rowData.put("Rows", list);
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}
	
	public Map<String,Object> nullRow(){
		Map<String,Object> result =new HashMap<String, Object>();
		result.put("Rows", new ArrayList<Object>());
		result.put("Total", 0);
		return result;
		
	}
	/**
	 * 构造指标展示列表
	 */
	@RequestMapping(value = "/getIdxInfoLabelList.*")
	@ResponseBody
	public Map<String, Object> getIdxInfoLabelList(Pager pager,String auth,String labelIds) {
		Map<String, Object> param = new HashMap<String, Object>();
		if (!BioneSecurityUtils.getCurrentUserInfo().isSuperUser() && StringUtils.isNotBlank(auth)){
			List<String> authIdxIds = new ArrayList<String>();
			authIdxIds = BioneSecurityUtils.getResIdListOfUser("AUTH_RES_IDX");
			param.put("list", ReBuildParam.splitLists(authIdxIds));
			if(authIdxIds.size()<=0){
				return nullRow();
			}
		}
		if (StringUtils.isNotBlank(labelIds)){
			String AllArray[] = StringUtils.split(labelIds,";");
			List<String> rptLIds = null;
			for(int i=0;i<AllArray.length;i++){
				List<String> labelIdsList = ArrayUtils.asList(AllArray[i], ",");
				if(rptLIds == null){
					rptLIds = this.labelBS.getObjIdByObjNo("idx", labelIdsList);
				}
				else{
					List<String> colIds =this.labelBS.getObjIdByObjNo("idx", labelIdsList);
					if(colIds.size()<=0){
						return nullRow();
					}
					rptLIds.retainAll(colIds);
				}
			}
			if(rptLIds.size()<=0){
				return nullRow();
			}
			param.put("labelIds", ReBuildParam.splitLists(rptLIds));
		}
		SearchResult<RptIdxInfoVO> searchResult = this.idxInfoBs
				.getIdxInfoListShowByParams(pager, param);
		Map<String, Object> rowData = Maps.newHashMap();
		List<RptIdxInfoVO> list = searchResult.getResult();
		Map<String, Object> indexTypeMap = Maps.newHashMap();
		for (int i = 0; i < list.size(); i++) {
			RptIdxInfoVO info = list.get(i);
			if (indexTypeMap.containsKey(info.getIndexType())) {
				info.setIndexType((String) indexTypeMap.get(info.getIndexType()));
			}
		}
		rowData.put("Rows", list);
		rowData.put("Total", searchResult.getTotalCount());
		return rowData;
	}

	/***
	 * 获取root指标对应的多维度来源列表
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @return
	 */
	@RequestMapping(value = "/getMeasureRelInfoList.*")
	@ResponseBody
	public Map<String, Object> getMeasureRelInfoList(String indexNo,
			String indexVerId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("indexNo", indexNo);
		params.put("indexVerId", indexVerId);
		Map<String, Object> rowData = Maps.newHashMap();
		List<RptIdxMeasureRelInfoVO> searchResult = null;
		if (indexVerId == null || indexVerId.equals("")) {// 新增情况，此时新index未存到DB
			searchResult = Lists.newArrayList();
		} else {// 修改，有可能是别的类型指标转过来的
			List<RptIdxInfo> idxList = this.idxInfoBs.listIdxInfo(params);
			if (idxList.size() > 0) {
				String indexType = idxList.get(0).getIndexType();
				if (!indexType.equals(ROOT_INDEX)) {
					searchResult = Lists.newArrayList();
					rowData.put("Rows", searchResult);
					return rowData;
				}
			}
			searchResult = this.idxInfoBs.getMeasureRelInfoList(params);
		}
		rowData.put("Rows", searchResult);
		return rowData;
	}

	/***
	 * 获取对应指标no的所有版本信息
	 * 
	 * @param indexNo
	 * @return
	 */
	@RequestMapping(value = "/getIdxInfoVerList.*")
	@ResponseBody
	public Map<String, Object> getIdxInfoVerList(String indexNo) {
		Map<String, Object> rowData = Maps.newHashMap();
		Map<String, Object> params = Maps.newHashMap();
		params.put("indexNo", indexNo);
		List<RptIdxInfo> list = this.idxInfoBs.getAllIdxVersionByParams(params);
		List<CommonComboBoxNode> boxNodes = this.idxInfoBs.getIndexTypeList(
				null, null);
		Map<String, Object> indexTypeMap = Maps.newHashMap();
		for (int i = 0; i < boxNodes.size(); i++) {
			CommonComboBoxNode boxNode = boxNodes.get(i);
			indexTypeMap.put(boxNode.getId(), boxNode.getText());
		}
		for (int i = 0; i < list.size(); i++) {
			RptIdxInfo info = list.get(i);
			if (indexTypeMap.containsKey(info.getIndexType())) {
				info.setIndexType((String) indexTypeMap.get(info.getIndexType()));
			}
		}
		rowData.put("Rows", list);
		return rowData;
	}

	/**
	 * 删除指标
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/idxInfoDel/{id}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> idxInfoDestroy(@PathVariable("id") String id, String vid) {
		Map<String, Object> result = Maps.newHashMap();
		String ids = id;
		if (ids != null && ids.length() > 0) {
			ids = ids.substring(0, id.length() - 1);
		}
		List<String> idList = new ArrayList<String>();
		if (id != null && id.length() > 0) {
			String[] idSplit = StringUtils.split(ids, ',');
			for (int i = 0; i < idSplit.length; i++) {
				idList.add(idSplit[i]);
			}
		}
		if (idList != null && idList.size() > 0) {
			if(StringUtils.isNotBlank(vid)){
				boolean flag1 = this.idxInfoBs.idxInfoVerisSrcIdxNo(id, vid);
				if(flag1) {
					result.put("msg", "所选指标当前版本被其他指标关联为来源指标或被报表关联，不能被删除！");
				}else {
					String startDate = this.idxInfoBs.cascadeIdxInfoVerDel(id, vid);
					this.idxInfoBs.cascadeUptVer(id, vid,startDate);
					result.put("msg", "1");
				}
			}
			else{
				boolean flag1 = idxInfoBs.isAsSrcIndexNos(idList);
				Map<String, Object> param = Maps.newHashMap();
				param.put("list", idList);
				boolean flag2 = idxInfoBs.checkHasBeenCascaded(param);
				if (flag1 || flag2) {
					result.put("msg", "所选指标中存在被其他指标关联为来源指标或被报表关联的指标，不能被删除！");
				} else {
					result.put("msg", "1");
					this.idxInfoBs.cascadeIdxInfoDel(id);
				}
			}
		}
		return result;
	}

	/**
	 * 获取指标类型列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/indexTypeList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> indexTypeList(String situation,
			String defSrc) {
		return this.idxInfoBs.getIndexTypeList(situation, defSrc);
	}

	/**
	 * 获取数据类型列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dataTypeList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> dataTypeList() {
		return this.idxInfoBs.dataTypeList();
	}

	/**
	 * 获取使用部门列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/useDeptList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> useDeptList(String usedDeptParamTypeNo) {
		return this.idxInfoBs.getParamDeptListByParams(usedDeptParamTypeNo,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
	}

	/**
	 * 获取定义部门列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/defDeptList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> defDeptList(String defDeptParamTypeNo) {
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		if (defDeptParamTypeNo == null || defDeptParamTypeNo.equals("")) {
			defDeptParamTypeNo = propertiesUtils
					.getProperty("defDeptParamTypeNo");
		}
		return this.idxInfoBs.getParamDeptListByParams(defDeptParamTypeNo,
				BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo());
	}

	/**
	 * 获取生成周期列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/calcCycleList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> calcCycleList() {
		return this.idxInfoBs.calcCycleList();
	}

	/**
	 * 获取数据单位列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/dataUnitList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> dataUnitList(String dataType) {
		return this.idxInfoBs.dataUnitList(dataType);
	}

	/**
	 * 获取指标状态列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/indexStsList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> indexStsList() {
		return this.idxInfoBs.indexStsList();
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/statTypeList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> statTypeList() {
		return this.idxInfoBs.statTypeList();
	}

	/**
	 * 是否发布布尔值列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/isPublishList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> isPublishList() {
		return this.idxInfoBs.isPublishList();
	}

	/**
	 * 获取指标是否汇总标识列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/isSumList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> isSumList() {
		return this.idxInfoBs.isSumList();
	}

	/**
	 * 获取指标是否汇总标识列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hasInforightList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> hasInforightList() {
		return this.idxInfoBs.hasInforightList();
	}

	@RequestMapping(value = "/getLineInfo", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> getLineInfo(){
		return this.lineBS.getLineInfo();
	}
	
	/**
	 * 指标信息框架页
	 * 
	 * @param datasetId
	 * @param catalogId
	 * @return
	 */
	@RequestMapping(value = "/idxInfoFrame")
	public ModelAndView idxInfoFrame(String indexNo, String indexVerId,
			String indexCatalogNo, String isDict, String defSrc) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("indexNo", indexNo);
		if (StringUtils.isNotEmpty(isDict)) {
			List<RptIdxInfo> list = this.idxInfoBs.listIdxInfo(map);
			if (list.size() > 0) {
				RptIdxInfo idx = list.get(0);
				indexVerId = idx.getId().getIndexVerId() + "";
				indexCatalogNo = idx.getIndexCatalogNo();
			}
		} else {
			isDict = "0";
		}
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		map.put("isDict", StringUtils2.javaScriptEncode(isDict));
		try {
			indexCatalogNo = URLDecoder.decode(indexCatalogNo, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		map.put("indexCatalogNo", StringUtils2.javaScriptEncode(indexCatalogNo));
		if (INDEX_DEF_SRC_ORG.equals(defSrc)) {
			map.put("indexCatalogNm", StringUtils2.javaScriptEncode(BioneSecurityUtils
					.getCurrentUserInfo().getOrgName()));
		} else if (INDEX_DEF_SRC_USER.equals(defSrc)) {
			map.put("indexCatalogNm",
					StringUtils2.javaScriptEncode(userBS.getUserNo(BioneSecurityUtils.getCurrentUserId())));
		} else {
			RptIdxCatalog catalog = this.idxInfoBs.getRptIdxCatalogById(
					indexCatalogNo, defSrc);
			map.put("indexCatalogNm", StringUtils2.javaScriptEncode(catalog.getIndexCatalogNm()));
		}
		if (StringUtils.isNotEmpty(defSrc)) {
			map.put("defSrc", StringUtils2.javaScriptEncode(defSrc));
		}
		return new ModelAndView("/plugin/rptidx/idx-infoFrame", map);
	}

	public String getNumStr(String maxIndexNo, String maxCustNm, String type) {

		int unit = Integer.parseInt(maxCustNm);
		if (maxIndexNo == null) {
			String unitStr = unit + "";
			char[] unitCharArr = unitStr.toCharArray();
			unitCharArr[0] = '0';// 至少2位即10
			if (unitCharArr.length > 1) {
				unitCharArr[unitCharArr.length - 1] = '1';
			}
			unitStr = new String(unitCharArr);
			return type + unitStr;
		} else {
			int max = unit * 10 - 1;
			String subMaxIndexNoStr = maxIndexNo.substring(type.length());
			Integer subMaxIndexNoInt = Integer.parseInt(subMaxIndexNoStr);
			if (subMaxIndexNoInt == max) {
				throw new RuntimeException("指标自增序列已达到自增极限[" + type + max
						+ "],请联系管理人员！");
			} else {
				subMaxIndexNoInt += 1;
				String newStr = "";
				while (subMaxIndexNoInt / unit == 0) {
					newStr += "0";
					unit /= 10;
				}
				newStr += subMaxIndexNoInt;
				return type + newStr;
			}
		}
	}

	/**
	 * 指标信息业务属性页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/busiAttrs")
	public ModelAndView busiAttrs() {
		ModelMap map = new ModelMap();
		map.addAttribute("IS_SUM_FALSE", IS_SUM_FALSE);
		map.addAttribute("IS_SUM_TRUE", IS_SUM_TRUE);
		map.addAttribute("IS_PUBLISH_TRUE", IS_PUBLISH_TRUE);
		map.addAttribute("HAS_INFORIGHT_FALSE", HAS_INFORIGHT_FALSE);
		map.addAttribute("INDEX_STS_START", INDEX_STS_START);
		map.addAttribute("ROOT_INDEX", ROOT_INDEX);
		map.addAttribute("DATA_TYPE_MONEY", DATA_TYPE_MONEY);
		map.addAttribute("DATA_TYPE_PERCENT", DATA_TYPE_PERCENT);
		map.addAttribute("DATA_UNIT_YUAN", DATA_UNIT_YUAN);
		map.addAttribute("DATA_UNIT_TEN_THOUSAND", DATA_UNIT_TEN_THOUSAND);
		map.addAttribute("CALC_CYCLE_DAY", CALC_CYCLE_DAY);
		map.addAttribute("STAT_TYPE_DEFAULT", IDX_STAT_TYPE_TIMEPOINT);
		map.addAttribute("COMPOSITE_INDEX", COMPOSITE_INDEX);
		map.addAttribute("DERIVE_INDEX", DERIVE_INDEX);
		map.addAttribute("GENERIC_INDEX", GENERIC_INDEX);
		map.addAttribute("SUM_ACCOUNT_INDEX", SUM_ACCOUNT_INDEX);
		map.addAttribute("ADD_RECORD_INDEX", ADD_RECORD_INDEX);

		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String isCustomed = propertiesUtils.getProperty("isCustomed");
		if (isCustomed.equals("1")) {
			String maxRootIndexNo = this.idxInfoBs
					.getMaxIndexNo(COSTOMED_INDEX_ROOT_PREFIX + "%");
			String maxGenericIndexNo = this.idxInfoBs
					.getMaxIndexNo(COSTOMED_INDEX_GENERIC_PREFIX + "%");
			String maxDeriveIndexNo = this.idxInfoBs
					.getMaxIndexNo(COSTOMED_INDEX_DERIVE_PREFIX + "%");
			String maxCompositeIndexNo = this.idxInfoBs
					.getMaxIndexNo(COSTOMED_INDEX_COMPOSITE_PREFIX + "%");
			String maxCustNm = propertiesUtils.getProperty("maxCustNm");
			String newMaxRootIndexNo = getNumStr(maxRootIndexNo, maxCustNm,
					COSTOMED_INDEX_ROOT_PREFIX);
			String newMaxGenericIndexNo = getNumStr(maxGenericIndexNo,
					maxCustNm, COSTOMED_INDEX_GENERIC_PREFIX);
			String newMaxDeriveIndexNo = getNumStr(maxDeriveIndexNo, maxCustNm,
					COSTOMED_INDEX_DERIVE_PREFIX);
			String newMaxCompositeIndexNo = getNumStr(maxCompositeIndexNo,
					maxCustNm, COSTOMED_INDEX_COMPOSITE_PREFIX);
			map.addAttribute("isCustomed", StringUtils2.javaScriptEncode(isCustomed));
			map.addAttribute("newMaxRootIndexNo", StringUtils2.javaScriptEncode(newMaxRootIndexNo));
			map.addAttribute("newMaxGenericIndexNo", StringUtils2.javaScriptEncode(newMaxGenericIndexNo));
			map.addAttribute("newMaxDeriveIndexNo", StringUtils2.javaScriptEncode(newMaxDeriveIndexNo));
			map.addAttribute("newMaxCompositeIndexNo", StringUtils2.javaScriptEncode(newMaxCompositeIndexNo));
		}
		String usedDeptParamTypeNo = propertiesUtils
				.getProperty("usedDeptParamTypeNo");
		String defDeptParamTypeNo = propertiesUtils
				.getProperty("defDeptParamTypeNo");
		map.addAttribute("usedDeptParamTypeNo", StringUtils2.javaScriptEncode(usedDeptParamTypeNo));
		map.addAttribute("defDeptParamTypeNo", StringUtils2.javaScriptEncode(defDeptParamTypeNo));
		return new ModelAndView("/plugin/rptidx/idx-busi-attrs", map);
	}

	/**
	 * 指标信息业务属性查看页面--最新版本
	 * 
	 * @return
	 */
	@RequestMapping(value = "/idxInfoPreview")
	public ModelAndView idxInfoPreview(String indexNo, String defSrc) {
		ModelMap map = new ModelMap();
		map.addAttribute("indexNo", StringUtils2.javaScriptEncode(indexNo));
		if (StringUtils.isNotEmpty(defSrc)) {
			map.addAttribute("defSrc", StringUtils2.javaScriptEncode(defSrc));
		}
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String usedDeptParamTypeNo = propertiesUtils
				.getProperty("usedDeptParamTypeNo");
		String defDeptParamTypeNo = propertiesUtils
				.getProperty("defDeptParamTypeNo");
		map.addAttribute("usedDeptParamTypeNo", StringUtils2.javaScriptEncode(usedDeptParamTypeNo));
		map.addAttribute("defDeptParamTypeNo", StringUtils2.javaScriptEncode(defDeptParamTypeNo));
		return new ModelAndView("/plugin/rptidx/idx-busi-attrs-preview",
				map);
	}

	/**
	 * 指标信息业务属性查看页面--实时版本
	 * 
	 * @return
	 */
	@RequestMapping(value = "/idxInfoPreRealVer")
	public ModelAndView idxInfoPreRealVer(String indexNo, String indexVerId) {
		ModelMap map = new ModelMap();
		map.addAttribute("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.addAttribute("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		map.addAttribute("isReal", "isReal");
		return new ModelAndView("/plugin/rptidx/idx-busi-attrs-preview",
				map);
	}

	/**
	 * 指标信息版本查看页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/idxInfoVerPre")
	public ModelAndView idxInfoVerPre(String indexNo, String defSrc) {
		ModelMap map = new ModelMap();
		map.addAttribute("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.addAttribute("defSrc", StringUtils2.javaScriptEncode(defSrc));
		return new ModelAndView("/plugin/rptidx/content/idx-info-ver-preview",
				map);
	}

	/**
	 * 技术属性页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/techAttrs")
	public ModelAndView techAttrs(String indexNo, String indexVerId,
			String indexCatalogNo, String indexType, String defSrc) {
		ModelMap map = new ModelMap();
		map.put("indexNo", StringUtils2.javaScriptEncode(indexNo));
		map.put("indexVerId", StringUtils2.javaScriptEncode(indexVerId));
		if (indexType.equals(ROOT_INDEX)) {// 根指标
			map.put("saveType", ROOT_INDEX);
			return new ModelAndView(
					"/plugin/rptidx/tech/idx-tech-attrs-meta-list", map);
		} else if (indexType.equals(COMPOSITE_INDEX)) {// 组合指标
			if (StringUtils.isNotEmpty(defSrc)) {
				map.put("INDEX_DEF_SRC_LIB", INDEX_DEF_SRC_LIB);
				map.put("INDEX_DEF_SRC_ORG", INDEX_DEF_SRC_ORG);
				map.put("INDEX_DEF_SRC_USER", INDEX_DEF_SRC_USER);
				BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
				userInfo.setUserNo(userBS.getUserNo(BioneSecurityUtils
						.getCurrentUserId()));
				map.put("userInfo", JSON.toJSON(userInfo));
				return new ModelAndView(
						"/plugin/rptidx/tech/idx-tech-attrs-composite-custom",
						map);
			}
			return new ModelAndView(
					"/plugin/rptidx/tech/idx-tech-attrs-composite", map);
		} else if (indexType.equals(ADD_RECORD_INDEX)) {// 补录指标
			map.put("saveType", ADD_RECORD_INDEX);
			if (indexVerId != null && !indexVerId.equals("")) {// 补录指标修改
				List<RptIdxInfo> idxs = this.idxInfoBs.listIdxInfo(map);
				if (idxs.size() > 0) {
					RptIdxInfo temp = idxs.get(0);
					String indexT = temp.getIndexType();
					if (indexT.equals(ADD_RECORD_INDEX)) {
						List<RptIdxDimRel> infos = this.idxInfoBs
								.getIdxDimRelByParams(map);
						List<String> dims = Lists.newArrayList();
						if (infos.size() > 0) {
							for (int i = 0; i < infos.size(); i++) {
								RptIdxDimRel temp_ = infos.get(i);
								dims.add(temp_.getId().getDimNo());
							}
						}
						map.put("dims", StringUtils2.javaScriptEncode(JSON.toJSONString(dims)));
					}
				}

			}
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
			String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
			String currencyDimTypeNo = propertiesUtils
					.getProperty("currencyDimTypeNo");
			map.put("dateDimTypeNo", StringUtils2.javaScriptEncode(dateDimTypeNo));
			map.put("orgDimTypeNo", StringUtils2.javaScriptEncode(orgDimTypeNo));
			map.put("currencyDimTypeNo", StringUtils2.javaScriptEncode(currencyDimTypeNo));
			return new ModelAndView(
					"/plugin/rptidx/tech/idx-tech-attrs-addrecord", map);
		} else if (indexType.equals(DERIVE_INDEX)) {// 派生指标
			map.put("initType", "1");
			if (indexVerId != null && !indexVerId.equals("")) {// 派生指标修改
				List<RptIdxInfo> infos = this.idxInfoBs.listIdxInfo(map);
				if (infos.size() > 0) {
					RptIdxInfo temp = infos.get(0);
					String indexT = temp.getIndexType();
					if (indexT == null || !indexT.equals(DERIVE_INDEX)
							|| temp.getSrcIndexNo() == null
							|| temp.getSrcIndexNo().equals("")) {
						map.put("initType", "0");
					}
				}
			}
			map.put("saveType", DERIVE_INDEX);
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String orgDimTypeNo = propertiesUtils.getProperty("orgDimTypeNo");
			String dateDimTypeNo = propertiesUtils.getProperty("dateDimTypeNo");
			String currencyDimTypeNo = propertiesUtils
					.getProperty("currencyDimTypeNo");
			String indexDimTypeNo = propertiesUtils
					.getProperty("indexDimTypeNo");
			map.put("dateDimTypeNo", StringUtils2.javaScriptEncode(dateDimTypeNo));
			map.put("orgDimTypeNo", StringUtils2.javaScriptEncode(orgDimTypeNo));
			map.put("currencyDimTypeNo", StringUtils2.javaScriptEncode(currencyDimTypeNo));
			map.put("indexDimTypeNo", StringUtils2.javaScriptEncode(indexDimTypeNo));
			if (StringUtils.isNotEmpty(defSrc)) {
				map.put("INDEX_DEF_SRC_LIB", INDEX_DEF_SRC_LIB);
				map.put("INDEX_DEF_SRC_ORG", INDEX_DEF_SRC_ORG);
				map.put("INDEX_DEF_SRC_USER", INDEX_DEF_SRC_USER);
				BioneUser userInfo = BioneSecurityUtils.getCurrentUserInfo();
				userInfo.setUserNo(userBS.getUserNo(BioneSecurityUtils
						.getCurrentUserId()));
				map.put("userInfo", JSON.toJSON(userInfo));
			}
			return new ModelAndView("/plugin/rptidx/tech/idx-tech-attrs-driver",
					map);
		} else if (indexType.equals(GENERIC_INDEX)) {// 泛化指标
			map.put("saveType", GENERIC_INDEX);
			map.put("setType", SET_TYPE_GENERIC);
			if ((indexNo != null && !indexNo.equals(""))
					&& (indexVerId != null && !indexVerId.equals(""))) {
				List<RptIdxInfo> infos = this.idxInfoBs.listIdxInfo(map);
				if (infos.size() > 0) {
					RptIdxInfo temp = infos.get(0);
					String indexT = temp.getIndexType();
					if (indexT != null && indexT.equals(GENERIC_INDEX)) {
						List<RptIdxMeasureRel> rels = this.idxInfoBs
								.getMeasureRelByParams(map);
						if (rels.size() > 0) {
							map.put("currVal", StringUtils2.javaScriptEncode(rels.get(0).getId().getDsId()));
						}
					}
				}
			}
			return new ModelAndView(
					"/plugin/rptidx/tech/idx-tech-attrs-generic", map);
		} else if (indexType.equals(SUM_ACCOUNT_INDEX)) {// 总账指标
			map.put("saveType", SUM_ACCOUNT_INDEX);
			map.put("setType", SET_TYPE_SUM);
			if ((indexNo != null && !indexNo.equals(""))
					&& (indexVerId != null && !indexVerId.equals(""))) {
				List<RptIdxInfo> infos = this.idxInfoBs.listIdxInfo(map);
				if (infos.size() > 0) {
					RptIdxInfo temp = infos.get(0);
					String indexT = temp.getIndexType();
					if (indexT != null && indexT.equals(SUM_ACCOUNT_INDEX)) {
						List<RptIdxMeasureRel> rels = this.idxInfoBs
								.getMeasureRelByParams(map);
						if (rels.size() > 0) {
							map.put("currVal", StringUtils2.javaScriptEncode(rels.get(0).getId().getDsId()));
						}
					}
				}
			}
			return new ModelAndView("/plugin/rptidx/tech/idx-tech-attrs-sum",
					map);
		}
		return null;
	}


	@RequestMapping("/dim/{dimNo}/items")
	public ModelAndView dimItems(@PathVariable("dimNo") String dimTypeNo) {
		dimTypeNo = StringUtils2.javaScriptEncode(dimTypeNo);
		return new ModelAndView(
				"/plugin/rptidx/tech/idx-tech-composite-dimfilter", "dimTypeNo", dimTypeNo);
	}

	@RequestMapping("/{idxNo}/dim/{dimNo}/items")
	public ModelAndView dimItems(@PathVariable("dimNo") String dimTypeNo,
			@PathVariable("idxNo") String indexNo, String indexVerId) {
		ModelMap mm = new ModelMap();
		mm.put("dimTypeNo", StringUtils2.javaScriptEncode(dimTypeNo));
		mm.put("filterInfo",
				idxInfoBs.getFilterInfo(indexNo, indexVerId, dimTypeNo));
		return new ModelAndView(
				"/plugin/rptidx/tech/idx-tech-composite-dimfilter", mm);
	}
	

	/**
	 * fangjuan 技术属性页面--获取来源指标信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getDimListBySrcIndex", method = RequestMethod.POST)
	@ResponseBody
	public List<RptDimTypeInfo> getDimListBySrcIndex(String indexNo){
		return this.idxInfoBs.getDimListBySrcIndex(indexNo);
	}
	
	/**
	 * 获取指标对应维度
	 * @Title: getDimListBySrcIndex
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param indexNo
	 * @param srcIndexNo
	 * @param srcIndexVerId
	 * @return Map<String,Object>  
	 * @throws
	 */
	@RequestMapping(value = "/getFrsDimListBySrcIndex.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getDimListBySrcIndex(String indexNo,String srcIndexNo,String srcIndexVerId){
		Map<String, Object> dimMap = Maps.newHashMap();
		Map<String, Object> params = Maps.newHashMap();
		params.put("idxNo", srcIndexNo);
		params.put("dimSts", "Y");
		List<RptDimTypeInfo> infoList = this.idxInfoBs.getDimByIdxInfo(params);

		// infoList 去重复

		Map<String, RptDimTypeInfo> map = new HashMap<String, RptDimTypeInfo>();
		for (RptDimTypeInfo tmp : infoList) {
			map.put(tmp.getDimTypeNo(), tmp);
		}
		infoList.clear();
		for (String tmp : map.keySet()) {
			infoList.add(map.get(tmp));
		}

		// infoList 去重复 end
		// 取到固定指标
		PropertiesUtils propertiesUtils = PropertiesUtils.get("bione-plugin/extension/report-common.properties");
		String necessaryDimTypeKeys = "";
		try {
			necessaryDimTypeKeys = propertiesUtils.getProperty("necessaryDimTypeKeys");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<RptDimTypeInfo> first = new ArrayList<RptDimTypeInfo>();
		List<RptDimTypeInfo> last = new ArrayList<RptDimTypeInfo>();

		if (necessaryDimTypeKeys != null && !necessaryDimTypeKeys.equals("")) {
			String[] arr = StringUtils.split(necessaryDimTypeKeys, ',');
			for (RptDimTypeInfo tmp : infoList) {
				int i = 0;
				for (i = 0; i < arr.length; i++) {
					if (tmp.getDimTypeNo().equals(
							propertiesUtils.getProperty(arr[i]))) {
						first.add(tmp);
						break;
					}
				}
				if (i >= arr.length) {
					last.add(tmp);
				}
			}
		}
		first.addAll(last);
		dimMap.put("Rows", first);
		return dimMap;
		
	}
	
	@RequestMapping(value = "/testSameIndexNo")
	@ResponseBody
	public boolean testSameIndexNo(String indexCatalogNo, String indexNo,
			String isUpdate, String oldIndexNo) {
		if (isUpdate != null && isUpdate.equals("1")
				&& oldIndexNo.equals(indexNo)) {
			return true;
		}
		Map<String, Object> params = Maps.newHashMap();
		params.put("indexCatalogNo", indexCatalogNo);
		params.put("indexNo", indexNo);
		Integer sameEntityCount = idxInfoBs.testSameIndexNo(params);
		if (sameEntityCount > 0) {
			return false;
		}
		return true;
	}

	@RequestMapping(value = "/testSameIndexNm")
	@ResponseBody
	public boolean testSameIndexNm(String indexNo, String indexNm,
			String indexUsualNmTemp1, String indexUsualNmTemp2) {
		Map<String, Object> params = Maps.newHashMap();
		String finalStr = indexNm;
		if (finalStr == null) {
			finalStr = indexUsualNmTemp1;
			if (finalStr == null) {
				finalStr = indexUsualNmTemp2;
			}
			if (finalStr.equals("")) {
				return true;
			}
		}
		params.put("indexNm", finalStr);
		if (StringUtils.isNotEmpty(indexNo)) {
			params.put("indexNo", indexNo);
		}
		Integer sameEntityCount = idxInfoBs.testSameIndexNm(params);
		if (sameEntityCount > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 跳转到派生指标 配置公式页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/extendsIdxEdit", method = RequestMethod.GET)
	public ModelAndView extendsIdxEdit() {
		return new ModelAndView("/plugin/rptidx/idx-extends-edit");
	}

	@RequestMapping(value = "/getDimByIdx", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getDimByIdx(String indexNos, String indexVerIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, CommonComboBoxNode> customMap = this.idxInfoBs
				.getExtendsDimByIdx(indexNos, indexVerIds);
		List<CommonComboBoxNode> staticList = this.idxInfoBs.getSaticDim();

		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String currency = propertiesUtils.getProperty("currencyDimTypeNo");
		if (customMap.get(currency) != null) {
			staticList.add(customMap.get(currency));
			customMap.remove(currency);
		}
		int staticListSize = staticList.size();
		for (Object object : customMap.keySet()) {
			staticList.add((CommonComboBoxNode) customMap.get(object));
		}
		map.put("staticNos", staticListSize);
		map.put("data", staticList);
		return map;
	}

	/**
	 * 指标信息
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @return
	 */
	@RequestMapping(value = "/getIdxInfoByParams.*", method = RequestMethod.POST)
	@ResponseBody
	public RptIdxInfoVO getIdxInfoByParams(String indexNo, String indexVerId,
			String defSrc) {
		Map<String, Object> params = Maps.newHashMap();
		List<RptIdxInfo> infos = null;
		params.put("indexNo", indexNo);
		if (StringUtils.isNotEmpty(defSrc)) {
			params.put("defSrc", defSrc);
		}
		if (indexVerId != null && !indexVerId.equals("")) {
			params.put("indexVerId", indexVerId);
			infos = this.idxInfoBs.getIdxInfoByParams(params);
		} else {
			infos = this.idxInfoBs.listIdxInfo(params);
		}
		RptIdxInfoVO infoVO = new RptIdxInfoVO();
		if (infos.size() > 0) {
			RptIdxInfo info = infos.get(0);
			params.put("indexVerId", info.getId().getIndexVerId());
			RptIdxBusiExt ext = this.idxInfoBs.getIdxBusiExtByParams(params);
			List<RptIdxMeasureRel> meaRelList = this.idxInfoBs
					.getMeasureRelByParams(params);
			PropertiesUtils propertiesUtils = PropertiesUtils.get(
					"bione-plugin/extension/report-common.properties");
			String dsName = "";
			List<String> dsIdList = Lists.newArrayList();
			String dsId = propertiesUtils.getProperty("dsId");
			for (int i = 0; i < meaRelList.size(); i++) {
				RptIdxMeasureRel meaRel = meaRelList.get(i);
				String thisDsId = meaRel.getId().getDsId();
				if (thisDsId.equals(dsId)) {
					String indexType = info.getIndexType();
					if (indexType != null) {
						if (indexType.equals(COMPOSITE_INDEX)) {
							dsName = "组合指标模型";
						} else if (indexType.equals(DERIVE_INDEX)) {
							dsName = "派生指标模型";
						}
					}
					break;
				} else {// 根指标
					dsIdList.add(thisDsId);
				}
			}
			if (dsIdList.size() > 0) {
				Map<String, Object> dsParam = Maps.newHashMap();
				dsParam.put("dsIdList", dsIdList);
				List<String> dsNameList = this.rptDatasetBS
						.getDataSetNamesByParams(dsParam);
				for (int i = 0; i < dsNameList.size(); i++) {
					String tempDsName = dsNameList.get(i);
					if (StringUtils.isNotEmpty(tempDsName)) {
						dsName += tempDsName + ",";
					}
				}
				if (dsName.length() > 0) {
					dsName = dsName.substring(0, dsName.length() - 1);
				}
			}
			infoVO.setSetId(dsName);
			infoVO.setCalcCycle(info.getCalcCycle());
			infoVO.setDataType(info.getDataType());
			infoVO.setDataUnit(info.getDataUnit());
			infoVO.setIndexCatalogNo(info.getIndexCatalogNo());
			infoVO.setIndexNm(info.getIndexNm());
			infoVO.setIndexNo(info.getId().getIndexNo());
			infoVO.setIndexSts(info.getIndexSts());
			infoVO.setIndexType(info.getIndexType());
			infoVO.setStatType(info.getStatType());
			if (ext != null) {
				infoVO.setDefDept(ext.getDefDept());
				infoVO.setBusiDef(ext.getBusiDef());
				infoVO.setBusiRule(ext.getBusiRule());
				String indexUsualNm = ext.getIndexUsualNm();
				if (indexUsualNm != null && !indexUsualNm.equals("")) {
					String[] indexUsualNmArr = StringUtils.split(indexUsualNm, ',');
					for (int i = 0; i < indexUsualNmArr.length; i++) {
						String obj = indexUsualNmArr[i];
						if (i == 0) {
							infoVO.setIndexUsualNmTemp1(obj);
						} else if (i == 1) {
							infoVO.setIndexUsualNmTemp2(obj);
						}
					}
				}
				infoVO.setUseDept(ext.getUseDept());
				infoVO.setBusiModel(ext.getBusiModel());
			}
			infoVO.setIndexVerId(info.getId().getIndexVerId() + "");
			infoVO.setIsSum(info.getIsSum());
			infoVO.setRemark(info.getRemark());
			infoVO.setStartDate(info.getStartDate());
			infoVO.setEndDate(info.getEndDate());
		}
		return infoVO;
	}

	/***
	 * 判断该指标是否具有信息权限
	 * 
	 * @param indexNo
	 * @return result
	 */
	@RequestMapping(value = "/isHasInfoRights.*", method = RequestMethod.POST)
	@ResponseBody
	public String isHasInfoRights(String indexNo) {
		List<String> list = BioneSecurityUtils
				.getResIdListOfUser("AUTH_RES_IDX");
		// List<String>
		// list=this.idxBS.getIdxByUserId(BioneSecurityUtils.getCurrentUserId());
		for (int i = 0; i < list.size(); i++) {
			String liStr = list.get(i);
			if ((!StringUtils.isEmpty(liStr))
					&& (!StringUtils.isEmpty(indexNo)) && liStr.equals(indexNo)) {
				return "1";
			}
		}
		return "0";
	}

	/***
	 * 判断该指标目录下面是否具有指标
	 * 
	 * @param indexCatalogNo
	 * @return result
	 */
	@RequestMapping(value = "/isHasIdx.*", method = RequestMethod.POST)
	@ResponseBody
	public String isHasIdx(String indexCatalogNo) {
		return this.idxInfoBs.isHasIdx(indexCatalogNo);
	}

	/**
	 * 检查新版本是否已经生成
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @return
	 */
	@RequestMapping(value = "/testNewVerExists.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> testNewVerExists(String indexNo,
			String indexVerId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("indexNo", indexNo);
		params.put("indexVerId", Integer.parseInt(indexVerId) + 1);
		List<RptIdxInfo> infos = this.idxInfoBs.getIdxInfoByParams(params);
		if (infos.size() == 0) {
			params.put("msg", "1");
		} else {
			params.put("msg", "0");
		}
		return params;
	}

	/**
	 * 指标与度量关系信息
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @return
	 */
	@RequestMapping(value = "/getMeasureRelByParams.*", method = RequestMethod.POST)
	@ResponseBody
	public RptIdxMeasureRel getMeasureRelByParams(String indexNo,
			String indexVerId) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("indexNo", indexNo);
		params.put("indexVerId", indexVerId);
		List<RptIdxMeasureRel> rels = this.idxInfoBs
				.getMeasureRelByParams(params);
		if (rels.size() > 0) {
			RptIdxMeasureRel idxMeasureRel = rels.get(0);
			RptSysModuleInfo dataset = this.rptDatasetBS
					.getModuleInfoById(idxMeasureRel.getId().getDsId());
			if (dataset == null) {
				return new RptIdxMeasureRel();
			}
			String setNm = dataset.getSetNm();
			String storeCol = idxMeasureRel.getStoreCol();
			if (storeCol != null) {
				storeCol = storeCol + "##" + setNm;
			} else {
				storeCol = "##" + setNm;
			}
			idxMeasureRel.setStoreCol(storeCol);
			return idxMeasureRel;
		}
		return new RptIdxMeasureRel();
	}

	/**
	 * 获取指标信息
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @return
	 */
	@RequestMapping(value = "/getIdxNameByParams.*", method = RequestMethod.POST)
	@ResponseBody
	public RptIdxInfo getIdxNameByParams(String indexNo, String indexVerId,
			String isSrc, String defSrc) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("indexNo", indexNo);
		params.put("indexVerId", indexVerId);
		if (StringUtils.isNotEmpty(defSrc)) {
			params.put("defSrc", defSrc);
		}
		List<RptIdxInfo> idxInfos = this.idxInfoBs.listIdxInfo(params);
		if (idxInfos.size() > 0) {
			RptIdxInfo rptIdxInfo = idxInfos.get(0);
			if (!rptIdxInfo.getIndexType().equals(COMPOSITE_INDEX)) {
				return new RptIdxInfo();
			}
			if (isSrc == null || isSrc.equals("")) {// 别的功能模块有可能用到这个接口时候的选择
				return rptIdxInfo;
			}
			if (rptIdxInfo.getSrcIndexNo() != null) {
				params.put("indexNo", rptIdxInfo.getSrcIndexNo());
				params.remove("indexVerId");
				List<RptIdxInfo> srcIdxInfos = this.idxInfoBs
						.listIdxInfo(params);
				if (srcIdxInfos.size() > 0) {
					RptIdxInfo srcRptIdxInfo = srcIdxInfos.get(0);
					PropertiesUtils propertiesUtils = PropertiesUtils.get(
							"bione-plugin/extension/report-common.properties");
					String steadyMeasureNo = propertiesUtils
							.getProperty("steadyMeasureNo");
					if (!steadyMeasureNo
							.equals(rptIdxInfo.getSrcIndexMeasure())) {
						srcRptIdxInfo.setSrcIndexMeasure(rptIdxInfo
								.getSrcIndexMeasure());
					} else {
						srcRptIdxInfo.setSrcIndexMeasure("");
					}
					return srcRptIdxInfo;
				}
			}
			return new RptIdxInfo();
		}
		return new RptIdxInfo();
	}

	/**
	 * 获取组合指标相关信息
	 * 
	 * @param indexNo
	 * @param indexVerId
	 * @return
	 */
	@RequestMapping(value = "/getCompIdxRelByParams.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getCompIdxRelByParams(String currIndexNo,
			String currIndexVerId) {
		Map<String, Object> params = Maps.newHashMap();
		List<Map<String, Object>> dimItemArrList = Lists.newArrayList();
		List<Map<String, Object>> dimItemValList = Lists.newArrayList();
		List<Map<String, Object>> dimFilterModeList = Lists.newArrayList();
		List<CommonComboBoxNode> idxdimrels = this.idxInfoBs.getDimByIdx(
				currIndexNo, currIndexVerId);
		PropertiesUtils propertiesUtils = PropertiesUtils.get(
				"bione-plugin/extension/report-common.properties");
		String currencyDimTypeNo = propertiesUtils
				.getProperty("currencyDimTypeNo");
		CommonComboBoxNode node = new CommonComboBoxNode();
		node.setId(currencyDimTypeNo);
		idxdimrels.add(node);
		String checkDims = JSON.toJSONString(idxdimrels);
		params.put("checkDims", checkDims);
		List<CommonTreeNode> allItems = null;
		Map<String, Object> allItemMap = null;
		Map<String, Object> params_ = Maps.newHashMap();
		params_.put("indexNo", currIndexNo);
		params_.put("indexVerId", currIndexVerId);
		RptIdxFormulaInfo formulaInfo = this.idxInfoBs
				.getFormulaInfoByParams(params_);
		params.put("formulaInfo", JSON.toJSON(formulaInfo));
		for (int i = 0; i < idxdimrels.size(); i++) {
			CommonComboBoxNode comboBoxNode = idxdimrels.get(i);
			params_.put("dimNo", comboBoxNode.getId());
			RptIdxFilterInfo idxFilterInfo = this.idxInfoBs
					.getIdxFilterInfoByParams(params_);
			if (idxFilterInfo != null) {
				String filterVal = idxFilterInfo.getFilterVal();
				if (filterVal != null && filterVal.length() > 0) {
					String returnStr = "";
					String headStr = "包含(";
					String filterMode = idxFilterInfo.getFilterMode();
					if (filterMode != null
							&& filterMode.equals(RPT_IDX_FILTER_MODE_NOT_IN)) {
						headStr = "不包含(";
					}
					String tailStr = "个过滤值)";
					String andSoOn = "";
					String finalStr = "";
					allItems = this.idxInfoBs.getDimInfoTree(
							comboBoxNode.getId(), this.getContextPath(), null);
					allItemMap = Maps.newHashMap();
					for (int a = 1; a < allItems.size(); a++) {
						CommonTreeNode itemNode = allItems.get(a);
						allItemMap.put(
								comboBoxNode.getId() + "_" + itemNode.getId(),
								itemNode.getText());
					}
					String[] filterValArr = StringUtils.split(filterVal, ',');
					List<String> selectedList = Lists.newArrayList();
					for (int f = 0; f < filterValArr.length; f++) {
						String itemNo = filterValArr[f];
						selectedList.add(itemNo);
						if (f < 2) {
							String itemName = (String) allItemMap
									.get(comboBoxNode.getId() + "_" + itemNo);
							returnStr += itemName + ";";
						} else {
							andSoOn = "等";
						}
					}
					if (returnStr.length() > 0) {
						returnStr = returnStr.substring(0,
								returnStr.length() - 1);
					}
					finalStr = headStr + " " + returnStr + " " + andSoOn
							+ selectedList.size() + tailStr;
					Map<String, Object> dimItemUnit1 = Maps.newHashMap();
					dimItemUnit1.put("dimTypeNo", comboBoxNode.getId());
					dimItemUnit1.put("itemArr", selectedList);
					dimItemArrList.add(dimItemUnit1);
					Map<String, Object> dimItemUnit2 = Maps.newHashMap();
					dimItemUnit2.put("dimTypeNo", comboBoxNode.getId());
					dimItemUnit2.put("value", finalStr);
					dimItemValList.add(dimItemUnit2);
					Map<String, Object> dimItemUnit3 = Maps.newHashMap();
					dimItemUnit3.put("dimTypeNo", comboBoxNode.getId());
					dimItemUnit3.put("filterMode", filterMode);
					dimFilterModeList.add(dimItemUnit3);
				}

			}
		}
		params.put("dimItemArrList", JSON.toJSONString(dimItemArrList));
		params.put("dimItemValList", JSON.toJSONString(dimItemValList));
		params.put("dimFilterModeList", JSON.toJSONString(dimFilterModeList));
		return params;
	}

	@RequestMapping(value = "/getExtendIdxInfo", method = RequestMethod.GET)
	@ResponseBody
	public IdxFormulaAndSrcIdxVO getExtendIdxInfo(String indexNo,
			String indexVerId) {
		return this.idxInfoBs.getExtendIdxInfo(indexNo, indexVerId);
	}

	@RequestMapping(value = "getDimInfoTree")
	@ResponseBody
	public List<CommonTreeNode> getDimInfoTree(String dimTypeNo, String busiType) {
		return this.idxInfoBs.getDimInfoTree(dimTypeNo, this.getContextPath(), busiType);
	}

	/**
	 * 查询所选的数据集下面是否关联度量字段
	 * 
	 * @param searchNm
	 * @return
	 */
	@RequestMapping(value = "/checkHasMeasures.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> checkHasMeasures(String setId, String setType) {
		return this.rptDatasetBS.checkHasMeasures(setId, setType);
	}

	@RequestMapping(value = "/getDimByDimNos", method = RequestMethod.GET)
	@ResponseBody
	public List<CommonComboBoxNode> getDimByDimNos(String dimNos) {
		return this.idxInfoBs.getDimByDimNos(dimNos);

	}

	@RequestMapping(value = "/testSameIndexCatalogNm")
	@ResponseBody
	public boolean testSameIndexCatalogNm(String upNo, String indexCatalogNm,
			String indexCatalogNo) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("upNo", upNo);
		params.put("indexCatalogNm", indexCatalogNm);
		if (indexCatalogNo != null && !indexCatalogNo.equals("")) {
			params.put("indexCatalogNo", indexCatalogNo);
		}

		Integer sameEntityCount = this.idxInfoBs.testSameIndexCatalogNm(params);
		if (sameEntityCount > 0) {
			return false;
		}
		return true;
	}

	/**
	 * 方娟
	 * 
	 * @param expression
	 * @param expressionDesc
	 * @return
	 */
	@RequestMapping(value = "/replaceNameAndNoByIdx")
	@ResponseBody
	public Map<String, String> replaceNameAndNoByIdx(String expression,
			String expressionDesc) {
		return this.idxInfoBs.replaceNoByIdx(expressionDesc);
	}

	@RequestMapping(value = "/exportIdx")
	public ModelAndView exportIdx() {
		return new ModelAndView("/plugin/rptidx/data/idx-come-export");
	}

	/**
	 * 导出导入
	 * 
	 * @author sunym@yuchengtech.com
	 * @return
	 */

	@RequestMapping("/exportTmp")
	public Map<String, Object> exportTmp(String idxIds) throws IOException {
		return DataDealUtils.exportImport(idxIds, this.getRealPath(),
				"exportIdx");
	}

	@RequestMapping("/exportTmpInfo")
	public void exportTmp(HttpServletResponse response, String filepath)
			throws Exception {
		if(FilepathValidateUtils.validateFilepath(filepath)) {
			File file = new File(filepath);
			DownloadUtils.download(response, file, "指标导出数据.txt");
			file.delete();
		}
	}

	@RequestMapping("/deleteFile")
	public void deleteFile(String pathname) {
		if(FilepathValidateUtils.validateFilepath(pathname)) {
			File file = new File(pathname);
			file.delete();
		}
	}

	@RequestMapping("/impIdx")
	public ModelAndView impReports() {
		return new ModelAndView("/plugin/rptidx/idx-imp-index");
	}

	/**
	 * 导入数据(grid展示)
	 */
	@RequestMapping("/impGrid")
	public ModelAndView impGrid() {
		return new ModelAndView("/plugin/rptidx/data/idx-imp-grid");
	}

	@RequestMapping("/impUpload")
	public ModelAndView impUpload() {
		return new ModelAndView("/plugin/rptidx/data/dx-imp-upload");
	}

	@RequestMapping("/dataset")
	public ModelAndView dataset() {
		return new ModelAndView("/plugin/rptidx/data/idx-imp-dataset");
	}

	@RequestMapping("/upload")
	@ResponseBody
	public String upload(Uploader uploader, HttpServletResponse response)
			throws Exception {
		File file = null;
		try {
			file = this.uploadFile(uploader, UPLOAD_ATTACH_DIR
					+ File.separatorChar, false);
		} catch (Exception e) {
			logger.info("文件上传出现异常", e);
		}
		if (file != null) {
			logger.info("文件[" + file.getName() + "]上传完成");
			List<String> fieldNms = new ArrayList<String>();
			fieldNms.add("indexNm");
			return DataDealUtils.getDataInfo(file, RptIdxInfo.class.getName(),
					fieldNms);
		}
		return null;
	}

	@RequestMapping("/saveImport")
	@ResponseBody
	public Map<String, Object> saveImport(String pathname, String dsId)
			throws IOException, ClassNotFoundException {
		return DataDealUtils.saveImport(pathname, dsId);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/{id}/tech/comp/initData", params = "indexVerId")
	@ResponseBody
	public Map<String, Object> initData(@PathVariable("id") String indexNo,
			String indexVerId) {
		Map<String, Object> result = Maps.newHashMap();
		RptIdxInfo idx = this.idxInfoBs.getInfoById(indexNo, indexVerId);
		if (idx != null) {
			result.put("idx", idx);
			RptIdxInfo srcIdx = this.idxInfoBs.getInfoById(idx.getSrcIndexNo(), null);
			Map<String,String> srcIdxMap = new HashMap<String, String>();
			String sidxNo = srcIdx.getId().getIndexNo();
			
			String sidxNm = srcIdx.getIndexNm();
			/*if(!srcIdx.getIndexType().equals(GlobalConstants4frame.SUM_ACCOUNT_INDEX)){
				sidxNm = "["+srcIdx.getId().getIndexNo()+"]"+sidxNm;
			}*/
			String idxNo = srcIdx.getId().getIndexNo();
			String measureNo = idx.getSrcIndexMeasure();
			if(StringUtils.isNotBlank(measureNo)&& !measureNo.equals("INDEX_VAL")){
				RptIdxMeasureInfo info = this.idxInfoBs.getEntityById(RptIdxMeasureInfo.class, measureNo);
				sidxNo += "."+measureNo;
				sidxNm += "."+info.getMeasureNm();
			}
			srcIdxMap.put("sidxNo", sidxNo);
			srcIdxMap.put("sidxNm", sidxNm);
			srcIdxMap.put("idxNo", idxNo);
			srcIdxMap.put("measureNo", measureNo);
			result.put("srcIdx", srcIdxMap);
			// 新的初始化数据
			Map<String, Object> idxRelInfo = this.idxInfoBs.getIdxRelInfo(indexNo,
					indexVerId);
			List<RptIdxFilterInfo> list = (List<RptIdxFilterInfo>) idxRelInfo
					.get("idxFilterInfo");
			StringBuilder stb = new StringBuilder(1000);
			List<JSONObject> textArr = Lists.newArrayList();
			
			JSONObject jo = null;
			for (RptIdxFilterInfo info : list) {
				stb.setLength(0);
				if ("01".equals(info.getFilterMode())) {
					stb.append("包含");
				} else if ("02".equals(info.getFilterMode())) {
					stb.append("不包含");
				}
				if(info.getId().getDimNo().equals("ORG")){
					List<RptOrgInfo> orgs = this.orgBS.getOrgByNos(info.getFilterVal());
					for (int i = 0; i < orgs.size(); i++) {
						RptOrgInfo itemInfo = orgs.get(i);
						if (i == 0) {
							stb.append("(");
						}
						if (i > 0) {
							stb.append(",");
						}
						stb.append(itemInfo.getOrgNm());
						if (i == (orgs.size() - 1)) {
							stb.append(" " + orgs.size() + "个过滤值)");
						}
					}
				}
				else{
					List<RptDimItemInfo> ls = dimItemBS.findById(info.getFilterVal(), info.getId()
							.getDimNo());
					for (int i = 0; i < ls.size(); i++) {
						RptDimItemInfo itemInfo = ls.get(i);
						if (i == 0) {
							stb.append("(");
						}
						if (i > 0) {
							stb.append(",");
						}
						stb.append(itemInfo.getDimItemNm());
						if (i == (ls.size() - 1)) {
							stb.append(" " + ls.size() + "个过滤值)");
						}
					}
				}
				
				jo = new JSONObject();
				jo.put("dimTypeNo", info.getId().getDimNo());
				jo.put("value", stb.toString());
				textArr.add(jo);
			}
			idxRelInfo.put("textArr", textArr);
			result.put("idxRelInfo", idxRelInfo);
		} else {
			return null;
		}
		return result;
	}
	//指标检验迁移begin
	/**
	 * 高级搜索弹框
	 * @param searchObj
	 * @return
	 */
	@RequestMapping("/highSearch")
	public ModelAndView highSearch(String searchObj) {
		searchObj = StringUtils2.javaScriptEncode(searchObj);
		return new ModelAndView("/plugin/search/high-search", "searchObj", searchObj);
	}
	
	/**
	 * 查询一个指标共有多少个版本
	 * @param indexNo
	 * @return
	 */
	@RequestMapping("/getAllVer")
	@ResponseBody
	public List<CommonComboBoxNode> getAllVer(String indexNo) {
		return idxInfoBs.getAllVer(indexNo);
	}
	
	/**
	 * 查询报表指标的来源指标信息
	 * @param queryDate
	 * @param srcIndexNo
	 * @param rptIndexNo
	 * @return
	 */
	@RequestMapping("/querySrcIdx")
	@ResponseBody
	public Map<String, Object> querySrcIdx(String queryDate, String srcIndexNo, String indexVerId, String rptIndexNo){
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap = idxInfoBs.querySrcIdx(queryDate, srcIndexNo, indexVerId, rptIndexNo);
		return returnMap;
	}
	
	/**
	 * 跳转指标影响查看页面
	 * @param indexNo
	 * @param verId
	 * @return
	 */
	@RequestMapping("/idxInfluence")
	public ModelAndView idxInfluence(String indexNo, String indexVerId) {
		Map<String, String> params = Maps.newHashMap();
		indexNo = StringUtils2.javaScriptEncode(indexNo);
		indexVerId = StringUtils2.javaScriptEncode(indexVerId);
		params.put("indexNo", indexNo);
		params.put("indexVerId", indexVerId);
		return new ModelAndView("/plugin/rptidx/edit/idx-info-influence", params);
	}
	
	/**
	 * 跳转指标影响gird页面
	 * @param influenceType
	 * @return
	 */
	@RequestMapping("/influenceGrid")
	public ModelAndView influenceGrid(String influenceType) {
		Map<String, String> params = Maps.newHashMap();
		influenceType = StringUtils2.javaScriptEncode(influenceType);
		params.put("influenceType", influenceType);
		return new ModelAndView("/plugin/rptidx/edit/idx-influence-grid", params);
	}
	
	/**
	 * 加载指标影响grid
	 * @param pager
	 * @param influenceType
	 * @return
	 */
	@RequestMapping(value = "/loadInfluenceGrid")
	@ResponseBody
	public Map<String, Object> loadInfluenceGrid(Pager pager, String influenceType, String srcIdxNo){
		Map<String, Object> rowData = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(influenceType)) {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("srcIdxNo", srcIdxNo);
			param.put("srcIdxNoHead", srcIdxNo + ",%");
			param.put("srcIdxNoTail", "%," + srcIdxNo);
			param.put("srcIdxNoCentre", "%," + srcIdxNo + ",%");
			if("idx".equals(influenceType)) {
				SearchResult<RptIdxInfoVO> searchResult = this.idxInfoBs.getInfluenceIdxListByParams(pager, param);
				rowData.put("Rows", searchResult.getResult());
				rowData.put("Total", searchResult.getTotalCount());
			}else if("rpt".equals(influenceType)) {
				SearchResult<RptMgrReportInfo> searchResult = this.rptTmpBS.getRptsBySrcIdxNos(pager, param);
				rowData.put("Rows", searchResult.getResult());
				rowData.put("Total", searchResult.getTotalCount());
			}
		}
		return rowData;
	}
	
	@SuppressWarnings("unchecked")
	private void ifSendMsg(String saveType,RptIdxInfoVO model,List<RptIdxFilterInfo> idxFilterList,RptIdxFormulaInfo idxFormulaInfo) {
		//组合指标的维度 idxFilterList
		//派生指标的计算公式 idxFormulaInfo.getFormulaContent();
		String formerIndexNo = model.getIndexNo();//指标编号无法修改
		String targetIndeNm = model.getIndexNm();//保存操作 指标版本不变
		//查询原指标信息
		RptIdxInfo idx = this.idxInfoBs.getInfoById(formerIndexNo, model.getIndexVerId());
		String formerIndexNm = idx.getIndexNm();
		
		StringBuilder msg = new StringBuilder();//通知信息
		StringBuilder indexNmBuilder = new StringBuilder(); //指标名称
		StringBuilder dimBuilder = new StringBuilder();		//组合指标的 维度和过滤值
		StringBuilder formulaBuilder = new StringBuilder(); //派生指标的 指标公式
		
		//判断指标名称
		if(!formerIndexNm.equals(targetIndeNm)) {
			indexNmBuilder.append("指标名称 由 ").append(formerIndexNm).append(" 变成  ").append(targetIndeNm).append("<br/>");
		}
		
		// 组合指标
		if(COMPOSITE_INDEX.equals(saveType)) {
			//查询组合指标的 原维度 及 维度的过滤信息
			Map<String, Object> idxRelInfo = this.idxInfoBs.getIdxRelInfo(formerIndexNo,model.getIndexVerId());
			List<RptIdxFilterInfo> formerIdxFilterList = (List<RptIdxFilterInfo>) idxRelInfo.get("idxFilterInfo");
			
			//原维度map<维度no,过滤值no>
			Map<String,String> formerDimMap = new HashMap<String,String>();
			for(RptIdxFilterInfo indexFilterInfo : formerIdxFilterList) {
				String dimNo = indexFilterInfo.getId().getDimNo();//维度
				String filterVal = indexFilterInfo.getFilterVal();//维度过滤
				formerDimMap.put(dimNo, filterVal);
			}
			//要保存的维度集合
			Map<String,String> targetDimMap = new HashMap<String,String>();
			for(RptIdxFilterInfo indexFilterInfo : idxFilterList) {
				String dimNo = indexFilterInfo.getId().getDimNo();//维度
				String filterVal = indexFilterInfo.getFilterVal();//维度过滤
				targetDimMap.put(dimNo, filterVal);
			}
			
			//删除维度信息,新增维度信息,维度过滤值修改信息
			StringBuilder delDimBuilder = new StringBuilder();
			StringBuilder addDimBuilder = new StringBuilder();
			StringBuilder updateDimBuilder = new StringBuilder();
			
			//判断原维度在要保存的维度中是否存在,如果不存在说明是删除了
			for(RptIdxFilterInfo indexFilterInfo : formerIdxFilterList) {
				String dimNo = indexFilterInfo.getId().getDimNo();//维度
				String dimNm = idxInfoBs.getDimTypeInfoById(dimNo).getDimTypeNm();
				if(!targetDimMap.containsKey(dimNo)) {
					//删除掉的维度
					delDimBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;")
								 .append(dimNo).append("&nbsp;").append(dimNm).append("<br/>");
				}
			}
			
			//判断要保存的维度在原维度中是否存在
			for(RptIdxFilterInfo indexFilterInfo : idxFilterList) {
				String dimNo = indexFilterInfo.getId().getDimNo();//维度
				//String filterVal = indexFilterInfo.getFilterVal();//维度过滤
				String dimNm = idxInfoBs.getDimTypeInfoById(dimNo).getDimTypeNm();
				//存在，继续判断维度过滤值。不存在，就是新增维度
				if(formerDimMap.containsKey(dimNo)) {
					String formerItemNos = formerDimMap.get(dimNo);
					if(!formerItemNos.equals(indexFilterInfo.getFilterVal())) {
						//维度过滤有变动
						Map<String,String> itemMap = idxInfoBs.getDimItem(dimNo,formerItemNos,indexFilterInfo.getFilterVal());
						//String getFilterValBy(indexFilterInfo);
						updateDimBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;")
										.append(dimNo).append("&nbsp;").append(dimNm)
										.append("&nbsp;的过滤值由 (").append(itemMap.get("formerItem"))
										.append(") 变成 (").append(itemMap.get("targetItem"))
										.append(")<br/>");
					}
				}else {
					//新增维度
					addDimBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;")
								 .append(dimNo).append("&nbsp;").append(dimNm).append("<br/>");
				}
			}
			
			if(delDimBuilder.length()>0) {
				dimBuilder.append("去除维度:<br/>").append(delDimBuilder);
			}
			if(updateDimBuilder.length()>0) {
				dimBuilder.append("维度过滤值变动:<br/>").append(updateDimBuilder);
			}
			if(addDimBuilder.length()>0) {
				dimBuilder.append("添加维度:<br/>").append(addDimBuilder);
			}
			
		}else if(DERIVE_INDEX.equals(saveType)) {// 派生指标
			//原指标公式   I('A0008')+I('A0012')
			IdxFormulaAndSrcIdxVO idxFormulaAndSrcIdxVO = this.idxInfoBs.getExtendIdxInfo(formerIndexNo, model.getIndexVerId());
			String formerFormulaContent = idxFormulaAndSrcIdxVO.getFormulaContent();
			//当前要保存的指标公式
			String targetFormulaContent = idxFormulaInfo.getFormulaContent();
			if(!targetFormulaContent.equals(formerFormulaContent)) {
				formulaBuilder.append("指标公式变动:<br/>&nbsp;&nbsp;&nbsp;&nbsp;")
							  .append("由&nbsp;").append(formerFormulaContent).append("&nbsp;变成&nbsp;").append(targetFormulaContent)
							  .append("<br/>");
			}
		}
		//
		if(indexNmBuilder.length()>0 || dimBuilder.length()>0 || formulaBuilder.length()>0) {
			msg.append("指标: <span style='color:red'>").append(formerIndexNo).append("  ").append(formerIndexNm).append("</span> 有变动<br/>");
			String currentDate = DateUtils.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
			msg.append("变动日期: ").append(currentDate);
			msg.append("&nbsp;&nbsp;&nbsp;&nbsp;修改人: ").append(userBS.getCurrentUserInfo().get("userName")).append("<br/>");
			msg.append(indexNmBuilder)
			   .append(dimBuilder)
			   .append(formulaBuilder);
		}
		
		//需要发送平台消息
		if(msg.length()>0) {
			//指标联系人
			String idxDefUser = model.getUserId();
			//获取受影响的指标和报表的联系人
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("srcIdxNo", "%"+formerIndexNo+"%");
			Pager pager = new Pager();
			pager.setPaging(false);
			pager.setOrderBy(false);
			List<RptIdxInfoVO> idxList = this.idxInfoBs.getIdxInfoListShowByParams(pager, param).getResult();
			List<RptMgrReportInfo> rptList = this.rptTmpBS.getRptsBySrcIdxNos(pager, param).getResult();
			
			//收信人集合
			Set<String> userIdSet = new HashSet<String>();
			if(StringUtils.isNotBlank(idxDefUser)){
				userIdSet.add(idxDefUser);//指标联系人
			}else{
				userIdSet.add("bc9f3effa7504f389af3e35a629247db");//为空的话联系人为“admin”,暂时逻辑20200611
			}
			
			//受影响的指标
			for(RptIdxInfoVO idxInfo : idxList) {
				userIdSet.add(idxInfo.getUserId());
			}
			//受影响的报表
			Map<String,Object> params = new HashMap<String,Object>();
			for(RptMgrReportInfo rpt : rptList) {
				userIdSet.add(rpt.getDefUser());
				//查询报表填报人员
				params.put("rptId", rpt.getRptId());
				List<Map<String, String>> userList = this.rptTmpBS.getFillUserByRpt(params);
				if(!CollectionUtils.isEmpty(userList)) {
					for(Map<String, String> map : userList) {
						userIdSet.add(map.get("USERID"));
					}
				}
			}
			
			msgNoticeLogBS.savePlatMsgs(userIdSet, "指标口径变动提醒",msg.toString(), "02");
		}
	}
}