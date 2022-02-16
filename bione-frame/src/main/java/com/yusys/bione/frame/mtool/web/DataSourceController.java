package com.yusys.bione.frame.mtool.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusys.bione.comp.common.CommonComboBoxNode;
import com.yusys.bione.comp.entity.page.Pager;
import com.yusys.bione.comp.repository.jpa.SearchResult;
import com.yusys.bione.comp.utils.RSAEncryptUtils;
import com.yusys.bione.comp.utils.RandomUtils;
import com.yusys.bione.comp.utils.StringUtils2;
import com.yusys.bione.frame.base.web.BaseController;
import com.yusys.bione.frame.mtool.entity.BioneDriverInfo;
import com.yusys.bione.frame.mtool.entity.BioneDsInfo;
import com.yusys.bione.frame.mtool.service.DataSourceBS;
import com.yusys.bione.frame.mtool.util.MtoolUtils;
import com.yusys.bione.frame.security.BioneSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 
 * <pre>
 * Title:
 * Description:
 * </pre>
 * 
 * @author gaofeng gaofeng5@yuchengtech.com
 * @version 1.00.00
 * 
 *          <pre>
 * 修改记录
 *    修改后版本:     修改人：高峰  修改日期:     修改内容:
 * </pre>
 */
@Controller
@RequestMapping("/bione/mtool/datasource")
public class DataSourceController extends BaseController {
	protected static Logger log = LoggerFactory
			.getLogger(DataSourceController.class);
	@Autowired
	private DataSourceBS dataSourceBS;

	@RequestMapping(method = RequestMethod.GET)
	public String index() {
		return "/frame/mtool/data-source-index";
	}

	// 数据源信息
	/**
	 * 获取用于加载grid的数据
	 */
	@RequestMapping("/list.*")
	@ResponseBody
	public Map<String, Object> list(Pager pager) {
		String sys = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
        String   sysNoConditionStr ="{\"op\":\"and\",\"rules\":[{\"op\":\"=\",\"field\":\"logicSysNo\",\"value\":\""+sys+"\",\"type\":\"string\"}]}";  
        String   sysNoConditionStr_ ="{\"op\":\"=\",\"field\":\"logicSysNo\",\"value\":\""+sys+"\",\"type\":\"string\"}";  
		if(pager.getCondition()==null||pager.getCondition().equals("")){
			pager.setCondition(sysNoConditionStr);
        }else{
        	JSONObject json = JSON.parseObject(pager.getCondition());
        	JSONArray array = json.getJSONArray("rules");
        	JSONObject sysNoObj = JSONObject.parseObject(sysNoConditionStr_);//转为JSONObject,再添加
        	array.add(sysNoObj);
        	json.put("rules", array.toString());
        	pager.setCondition(json.toString());
        }
		SearchResult<BioneDsInfo> bioneDsInfo = dataSourceBS.getList(
				pager);
		if (bioneDsInfo.getResult() != null) {
			for (int j = 0; j < bioneDsInfo.getResult().size(); j++) {
				String drId = bioneDsInfo.getResult().get(j).getDriverId();
				BioneDriverInfo driverInfo = this.dataSourceBS.findDriverInfoById(drId);
				bioneDsInfo.getResult().get(j)
						.setDriverId(driverInfo.getDriverType());
				String passWord = bioneDsInfo.getResult().get(j).getConnPwd();
				String pwd = "";
				if(passWord!=null){
					for (int i = 0; i < passWord.length(); i++) {
						pwd = pwd + "*";
					}
				}
				bioneDsInfo.getResult().get(j).setConnPwd(pwd);
			}
		}
		Map<String, Object> temMap = new HashMap<String, Object>();
		temMap.put("Rows", bioneDsInfo.getResult());
		temMap.put("Total", bioneDsInfo.getTotalCount());
		return temMap;
	}

	// 跳转新建数据源页面
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String editNew() {
		return "/frame/mtool/data-source-edits";
	}

	// 跳转修改数据源页面
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public ModelAndView edit(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/mtool/data-source-edits", "id", id);
	}

	// 根据Id获取数据源信息
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BioneDsInfo show(@PathVariable("id") String id) {
		BioneDsInfo   info  = dataSourceBS.findDataSourceById(id);
		return  info;
	}
	
	// 补录模板选择数据源时的页面
	@RequestMapping(value = "/chackDS/{id}", method = RequestMethod.GET)
	public ModelAndView chackDS(@PathVariable("id") String id) {
		id = StringUtils2.javaScriptEncode(id);
		return new ModelAndView("/frame/mtool/data-source-index", "id", id);
	}

	// 保存数据源信息
	@RequestMapping(method = RequestMethod.POST)
	public void create(BioneDsInfo model) {
		String sys = BioneSecurityUtils.getCurrentUserInfo().getCurrentLogicSysNo();
		model.setLogicSysNo(sys);
		BioneDriverInfo drInfo = dataSourceBS.getURLData(model.getDriverId());
		if (drInfo != null) {
			String connUrl = drInfo.getConnUrl();
			if (StringUtils.isNotEmpty(connUrl) && StringUtils.contains(connUrl, "$")) {
				connUrl = StringUtils.replace(connUrl, "${host}", model.getHost());
				connUrl = StringUtils.replace(connUrl, "${port}", model.getPort());
				connUrl = StringUtils.replace(connUrl, "${dbname}", model.getDbname());
			}
			model.setConnUrl(connUrl);
		}
		if (model.getDsId() == null || model.getDsId().equals("")) {
			model.setDsId(RandomUtils.uuid2());
			dataSourceBS.saveDS(model);
		}else
		dataSourceBS.updateDS(model);
	}

	// 删除数据源
	@RequestMapping("/removeAll")
	@ResponseBody
	public Map<String,Object> removeAll(String dsId) {
		Map<String,Object>  result =  Maps.newHashMap();
		Map<String,Object>  param =  Maps.newHashMap();
		String[] dsIds = StringUtils.split(dsId, '/');
		List<String>  list  =  Lists.newArrayList();
		if(dsIds!=null){
			for(String ds:dsIds){
				list.add(ds);
			}
		}
		param.put("list", list);
		Integer  dataSetCount = dataSourceBS.getDataSetCountByDsId(param);
		if(dataSetCount>0){
	     	result.put("msg","所选数据源中存在被数据集关联的数据源，不能被删除！");
		}else{
		    dataSourceBS.removeDs(dsId);
		}
		return   result;
		// this.bioneDsBS.removeEntityById(dsId);
	}

	// 重名验证
	@RequestMapping("/dsNameValid")
	@ResponseBody
	public boolean dsNameValid(String dsId, String dsName) {
		List<BioneDsInfo> ds = Lists.newArrayList();
		ds = dataSourceBS.checkedDsName(dsId, dsName);
		if (ds != null && ds.size() > 0)
			return false;
		else
			return true;
	}

	// 测试
	@RequestMapping("/getTest")
	@ResponseBody
	public boolean getTest(String driver, String connUser,
			String connPwd,String driverType, String host, String port, String dbname) {
		boolean flag =false;
		String connUrl = "";
		BioneDriverInfo drInfo = dataSourceBS.getURLData(driverType);
		if (drInfo != null) {
			connUrl = drInfo.getConnUrl();
			driver = drInfo.getDriverName();
		}
		if (StringUtils.isNotEmpty(connUrl) && StringUtils.contains(connUrl, "$")) {
			connUrl = StringUtils.replace(connUrl, "${host}", host);
			connUrl = StringUtils.replace(connUrl, "${port}", port);
			connUrl = StringUtils.replace(connUrl, "${dbname}", dbname);
		}
		try {
			connPwd = URLDecoder.decode(RSAEncryptUtils.decryptSegmentedByPrivateKey(connPwd),"UTF-8");
			flag = MtoolUtils.testConnection(driver, connUrl, connUser, connPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	// 获取驱动信息
	@RequestMapping("/getDriverData")
	@ResponseBody
	public Map<String, Object> getDriverData() {
		Map<String, Object> temMap = new HashMap<String, Object>();
		temMap.put("data", dataSourceBS.getDriverData());
		return temMap;
	}

	// 获取URL
	@RequestMapping("/getUrlData")
	@ResponseBody
	public Map<String, Object> getUrlData(String driverId) {
		Map<String, Object> temMap = new HashMap<String, Object>();
		temMap.put("data", dataSourceBS.getURLData(driverId));
		return temMap;
	}

	/**
	 * 获取数据源列表
	 * @return
	 */
	@RequestMapping(value = "/dsImportList", method = RequestMethod.POST)
	@ResponseBody
	public List<CommonComboBoxNode> dsImportList() {
		return this.dataSourceBS.getDsImportList();
	}

}
