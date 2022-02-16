package com.yusys.bione.frame.mtool.web;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mtool.entity.BioneDatasetCatalogInfo;
import com.yusys.bione.frame.mtool.entity.BioneDatasetInfo;
import com.yusys.bione.frame.mtool.service.DatasetBS;
import com.yusys.bione.frame.security.BioneSecurityUtils;

/**
 * 
 * <pre>
 * Title:数据集控制器类
 * Description: 提供对数据集及数据项的自定义配置
 * </pre>
 * 
 * @author fanll fanll@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/mtool/dataset")
public class DatasetController extends BaseController {

	@Autowired
	private DatasetBS dsetBS;

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/temp")
	public String temp() {
		return "/frame/mtool/param-temp-info";
	}

	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/mtool/data-set-index";
	}

	/**
	 * 数据集目录信息页
	 */
	@RequestMapping(value = "/catalog")
	public ModelAndView catalog(String catalogId, String upId) {
		ModelMap map = new ModelMap();
		map.addAttribute("catalogId", StringUtils2.javaScriptEncode(catalogId));
		map.addAttribute("upId", StringUtils2.javaScriptEncode(upId));
		return new ModelAndView("/frame/mtool/data-set-catalog", map);
	}

	/**
	 * 数据集列表页
	 * 
	 * @param catalogId
	 *            目录Id
	 * @param catalogName
	 *            目录名称
	 * @return
	 */
	@RequestMapping(value = "/grid")
	public String grid(String catalogId, String catalogName) {
		return "/frame/mtool/data-set-grid";
	}

	/**
	 * 数据集信息框架页
	 * 
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@RequestMapping(value = "/infoFrame")
	public ModelAndView infoFrame(String datasetId, String catalogId) {
		ModelMap map = new ModelMap();
		map.addAttribute("datasetId", StringUtils2.javaScriptEncode(datasetId));
		map.addAttribute("catalogId", StringUtils2.javaScriptEncode(catalogId));
		return new ModelAndView("/frame/mtool/data-set-infoFrame", map);
	}

	/**
	 * 数据集信息页
	 * 
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@RequestMapping(value = "/info")
	public String info() {
		return "/frame/mtool/data-set-info";
	}

	/**
	 * 物理表选择页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/tablePage")
	public ModelAndView tablePage(String dsId) {
		dsId = StringUtils2.javaScriptEncode(dsId);
		return new ModelAndView("/frame/mtool/data-set-table", "dsId", dsId);
	}

	/**
	 * 系统参数页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sysvarPage")
	public String sysvarPage() {
		return "/frame/mtool/data-set-sysvar";
	}

	/**
	 * 数据项列表页
	 */
	@RequestMapping(value = "/cols")
	public String cols() {
		return "/frame/mtool/data-set-cols";
	}

	/**
	 * 数据集预览页
	 * 
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@RequestMapping(value = "/preview")
	public ModelAndView preview(String datasetId) {
		ModelMap map = new ModelMap();
		map.addAttribute("datasetId", StringUtils2.javaScriptEncode(datasetId));
		map.addAttribute("formStructure",
				StringUtils2.javaScriptEncode(this.dsetBS.getSearchStruct(datasetId)));
		map.addAttribute("gridStructure", StringUtils2.javaScriptEncode(this.dsetBS.getGridStruct(datasetId)));
		return new ModelAndView("/frame/mtool/data-set-preview", map);
	}

	/**
	 * 获取数据集目录树
	 */
	@RequestMapping(value = "/getTree.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonTreeNode> getTree(String realId) {
		return this.dsetBS.getDatasetCatalogTree(realId, this.getContextPath());
	}

	/**
	 * 目录名称重复验证
	 */
	@RequestMapping(value = "/catalogNameCanUse*")
	@ResponseBody
	public boolean catalogNameCanUse(String catalogId, String upId,
			String catalogName) {
		return this.dsetBS.catalogNameCanUse(catalogId, upId, catalogName);
	}

	/**
	 * 获取目录信息
	 */
	@RequestMapping(value = "/getCatalogInfo.*")
	@ResponseBody
	public BioneDatasetCatalogInfo getCatalogInfo(String catalogId) {
		return this.dsetBS.getEntityById(BioneDatasetCatalogInfo.class,
				catalogId);
	}

	/**
	 * 保存目录信息
	 */
	@RequestMapping(value = "/saveCatalog")
	public void saveCatalog(BioneDatasetCatalogInfo catalog) {
		catalog.setLogicSysNo(BioneSecurityUtils.getCurrentUserInfo()
				.getCurrentLogicSysNo());
		if (catalog.getCatalogId() == null || "".equals(catalog.getCatalogId())) {
			catalog.setCatalogId(RandomUtils.uuid2());
			this.dsetBS.saveEntity(catalog);
		} else {
			this.dsetBS.updateEntity(catalog);
		}
	}

	/**
	 * 删除目录信息
	 */
	@RequestMapping("/deleteCatalog")
	@ResponseBody
	public boolean deleteCatalog(String catalogId) {
		return this.dsetBS.deleteCatalog(catalogId);
	}

	/**
	 * 获取数据集列表
	 * 
	 * @param pager
	 *            PageBean
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@RequestMapping(value = "/setList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> datasets(Pager pager, String catalogId) {
		return this.dsetBS.getDatasets(pager.getPageFirstIndex(),
				pager.getPagesize(), pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition(), catalogId);
	}
	
	/**
	 * 获取数据集列表所有
	 * 
	 * @param pager
	 *            PageBean
	 * @param catalogId
	 *            目录Id
	 * @return
	 */
	@RequestMapping(value = "/setAll.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> datasets(Pager pager) {
		return this.dsetBS.getDatasets(pager.getPageFirstIndex(),
				pager.getPagesize(), pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition());
	}

	/**
	 * 数据集信息
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @return
	 */
	@RequestMapping(value = "/datasetInfo.*", method = RequestMethod.POST)
	@ResponseBody
	public BioneDatasetInfo datasetInfo(String datasetId) {
		return this.dsetBS.getEntityById(BioneDatasetInfo.class, datasetId);
	}

	/**
	 * 获取数据集的数据项列表
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @return
	 */
	@RequestMapping(value = "/colList.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> datacols(String from, String datasetId,
			String dsId, String table) {
		if (from != null && "dataset".equals(from.toLowerCase())) {
			return this.dsetBS.getDatacolsOfDataset(datasetId);// 已配置的数据项
		} else {
			return this.dsetBS.getFieldsOfTable(dsId, table);// 物理表中的原始字段
		}
	}

	/**
	 * 获取数据源列表
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @return
	 */
	@RequestMapping(value = "/dsList.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> dsList() {
		return this.dsetBS.getDataSources();
	}

	/**
	 * 获取数据源中物理表列表
	 * 
	 * @param dsId
	 *            数据源Id
	 * @return
	 */
	@RequestMapping(value = "/tables.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> tables(Pager pager, String dsId) {
		Map<String, Object> attributes = this.getAttributes(pager
				.getCondition());
		return this.dsetBS.getTablesOfDs(
				dsId,
				attributes.get("tableName") == null ? null : attributes.get(
						"tableName").toString(),
				attributes.get("tableComment") == null ? null : attributes
						.get("tableComment").toString().toString(),
				pager.getPageFirstIndex(), pager.getPagesize());
	}

	/**
	 * 保存
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public void save(BioneDatasetInfo dataset, String datacolsJsonStr) {
		this.dsetBS.save(dataset, datacolsJsonStr);
	}

	/**
	 * 批量删除数据集
	 */
	@RequestMapping(value = "/deleteDataset", method = RequestMethod.POST)
	public void deleteDataset(String ids) {
		List<String> idList = Arrays.asList(StringUtils.split(ids, ','));
		if (idList != null && idList.size() > 0) {
			this.dsetBS.deleteBatch(idList);
		}
	}

	/**
	 * 数据集数据预览
	 */
	@RequestMapping(value = "/previewData.*", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> previewData(Pager pager, String datasetId) {
		return this.dsetBS.datasetPreview(datasetId, 0, 100,
				this.getAttributes(pager.getCondition()), true,"");
	}

	/**
	 * 获取系统变量
	 */
	@RequestMapping("/getSysVar.*")
	@ResponseBody
	public Map<String, Object> getSysVar(Pager pager) {
		return this.dsetBS.getSysVars(pager.getPageFirstIndex(),
				pager.getPagesize(), pager.getSortname(), pager.getSortorder(),
				pager.getSearchCondition());
	}

	// 获取查询条件中的属性
	private Map<String, Object> getAttributes(String condition) {
		Map<String, Object> attributes = Maps.newHashMap();
		if (condition != null && !condition.equals("")) {
			JSONObject groupJson = JSON.parseObject(condition);
			JSONArray rulesJson = groupJson.getJSONArray("rules");
			for (int i = 0; i < rulesJson.size(); i ++) {
				JSONObject rule = rulesJson.getJSONObject(i);
				String field = (String) rule.get("field");
				String value = (String) rule.get("value");
				attributes.put(field, value);
			}
		}
		return attributes;
	}

	/**
	 * 获取数据源列表
	 * 
	 * @param datasetId
	 *            数据集Id
	 * @return
	 */
	@RequestMapping(value = "/dsListNone.*", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> dsListNone() {
		return this.dsetBS.getDataSourcesNone();
	}
}
